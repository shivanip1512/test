#include "precompiled.h"

#include "rf_data_streaming_processor.h"

#include "amq_connection.h"
#include "dev_rfn.h"
#include "rfn_statistics.h"
#include "std_helper.h"
#include "error_helper.h"
#include "MetricIdLookup.h"
#include "DeviceAttributeLookup.h"
#include "mgr_device.h"
#include "mgr_point.h"
#include "pt_numeric.h"
#include "desolvers.h"

#include <boost/range/adaptor/transformed.hpp>
#include <boost/range/algorithm/count_if.hpp>
#include <boost/range/algorithm/transform.hpp>
#include <boost/range/numeric.hpp>

#include <chrono>

using Cti::Messaging::Rfn::E2eMessenger;
using Cti::Logging::Vector::Hex::operator<<;

static const auto RF_DATA_STREAMING_STATS_REPORTING_INTERVAL = 86400;

namespace Cti::Pil {

namespace {

    //  Jan 1 2016 0:00 GMT
    static const auto DataStreamingEpoch = std::chrono::system_clock::from_time_t(1'451'606'400);

    unsigned statsReportFrequency = gConfigParms.getValueAsInt("RF_DATA_STREAMING_STATS_REPORTING_INTERVAL", RF_DATA_STREAMING_STATS_REPORTING_INTERVAL);
    CtiTime nextStatisticsReport = nextScheduledTimeAlignedOnRate(CtiTime::now(), statsReportFrequency);
}

RfDataStreamingProcessor::RfDataStreamingProcessor(CtiDeviceManager& deviceManager, CtiPointManager& pointManager)
    :   _deviceManager{ deviceManager },
        _pointManager { pointManager  }
{}

void RfDataStreamingProcessor::start()
{
    E2eMessenger::registerDataStreamingHandler(
            [&](const E2eMessenger::Indication &msg)
            {
                CTILOG_INFO(dout, "Data streaming packet received for " << msg.rfnIdentifier << "\n" << msg.payload);

                LockGuard guard(_packetMux);

                _packets.push_back(msg);
            });
}


auto RfDataStreamingProcessor::tick() -> ResultVector
{
    PacketQueue newPackets;

    {
        LockGuard g{ _packetMux };

        newPackets.swap(_packets);
    }

    std::vector<DeviceReport> reports;

    boost::range::transform(
        newPackets, 
        std::back_inserter(reports),
        [](const auto& packet) { return processPacket(packet); });

    std::vector<std::unique_ptr<CtiPointDataMsg>> pointData;

    for( auto& report : reports )
    {
        auto reportPointData = processDeviceReport(report);
        std::move(reportPointData.begin(), reportPointData.end(), std::back_inserter(pointData));
    }

    handleStatistics();

    return pointData;
}


uint8_t get_uint8(std::vector<unsigned char>::const_iterator itr)
{
    return *itr;
}

uint16_t get_uint16(std::vector<unsigned char>::const_iterator itr)
{
    return *itr++ << 8 | *itr++;
}

uint32_t get_uint32(std::vector<unsigned char>::const_iterator itr)
{
    return *itr++ << 24 | *itr++ << 16 | *itr++ << 8 | *itr++;
}

int32_t get_int32(std::vector<unsigned char>::const_iterator itr)
{
    return *itr++ << 24 | *itr++ << 16 | *itr++ << 8 | *itr++;
}

double scaling(uint8_t dataScaling)
{
    switch( dataScaling )
    {
        case  3:  return 1e9;  //  Giga
        case  2:  return 1e6;  //  Mega  
        case  1:  return 1e3;  //  Kilo  
        case  4:  return 1e2;  //  Hecto 
        case 10:  return 1e1;  //  Deca  
        case  0:  return 1e0;  //  None  
        case  9:  return 1e-1; //  Deci  
        case  8:  return 1e-2; //  Centi 
        case  7:  return 1e-3; //  Milli 
        case  6:  return 1e-6; //  Micro 
        case  5:  return 1e-9; //  Nano  
        default:
            throw std::invalid_argument{"Invalid scaling value: " + std::to_string(dataScaling)};
    }
}

PointQuality_t convertQuality(uint8_t quality)
{
    switch( quality )
    {
        case 0:  return NormalQuality;    //  OK                 
        case 1:  return AbnormalQuality;  //  MeterAccessError  
        case 2:  return AbnormalQuality;  //  MeterOrNodeBusy   
        case 3:  return AbnormalQuality;  //  MeterReadTimeout  
        case 4:  return AbnormalQuality;  //  MeterProtocolError
        case 5:  return InvalidQuality;   //  ChannelNotSupported
        case 6:  return AbnormalQuality;  //  UnknownError     
        case 7:  return InvalidQuality;   //  ChannelNotEnabled
        default: return UnknownQuality;
    }
}

auto RfDataStreamingProcessor::processPacket(const Packet &p) -> DeviceReport
try
{
    auto& payload = p.payload;

    validate(Condition( ! payload.empty(), ClientErrors::InvalidData) 
        << "Payload was empty");

    const auto formatId = payload[0];

    validate(Condition(formatId == 0x01, ClientErrors::InvalidData) 
        << "Invalid format ID (" << formatId << " != 0x01)");

    const auto HeaderLen = 2;

    validate(Condition( payload.size() >= HeaderLen, ClientErrors::InvalidData) 
        << "Payload too small (" << payload.size() << " < " << HeaderLen << ")");

    const auto dataPointCount = payload[1];

    const auto MetricIdLen  = 2;
    const auto TimestampLen = 4;
    const auto ModifierLen  = 1;
    const auto DataValueLen = 4;
    
    const auto DataPointLen = MetricIdLen + TimestampLen + ModifierLen + DataValueLen;  //  11 bytes per data point value

    const auto expectedLength = HeaderLen + (dataPointCount * DataPointLen);

    validate(Condition(payload.size() >= expectedLength, ClientErrors::InvalidData)
        << "Response size was too small for reported metric count (" << payload.size() << "<" << expectedLength << ")");

    DeviceReport dr;

    dr.rfnId = p.rfnIdentifier;

    for( int i = 0; i < dataPointCount; ++i )
    {
        const auto offset = HeaderLen + i * DataPointLen;
        const auto metricId  = get_uint16(payload.begin() + offset);
        const auto timestamp = get_uint32(payload.begin() + offset + 2);
        const auto modifier  = get_uint8 (payload.begin() + offset + 6);
        const auto dataValue = get_int32 (payload.begin() + offset + 7);

        dr.values.emplace_back(Value{
            metricId,
            DataStreamingEpoch + std::chrono::seconds(timestamp),
            dataValue * scaling(modifier / 16),
            convertQuality(modifier % 16)});
    }

    CTILOG_DEBUG(dout, "rfnId, timestamp, metricId, value, quality");
    for( const Value& value : dr.values )
    {
        CTILOG_DEBUG(dout, dr.rfnId << "," << value.timestamp << "," << value.metricId << "," << value.value << "," << desolvePointQuality(value.quality));
    }

    return dr;
}
catch( const YukonErrorException& ex )
{
    CTILOG_EXCEPTION_ERROR(dout, ex, "Error processing data streaming packet for " << p.rfnIdentifier);

    return { p.rfnIdentifier };
}

StreamBufferSink& operator<<(StreamBufferSink& s, const RfDataStreamingProcessor::Value &v)
{
    FormattedList l;

    l.add("MetricId")  << v.metricId;
    l.add("Timestamp") << v.timestamp;
    l.add("Value")     << v.value;
    l.add("Quality")   << v.quality;

    return s << l;
}

StreamBufferSink& operator<<(StreamBufferSink& s, const RfDataStreamingProcessor::DeviceReport &dr)
{
    FormattedList l;

    l.add("RfnIdentifier") << dr.rfnId;

    l.add("Values") << dr.values.size();

    size_t value = 1;

    for( const auto& v : dr.values )
    {
        l.add("Value " + std::to_string(value++)) << v;
    }

    return s << l;
}

std::map<long, std::map<uint16_t, size_t>> stats;

auto RfDataStreamingProcessor::processDeviceReport(const DeviceReport& deviceReport) -> std::vector<std::unique_ptr<CtiPointDataMsg>>
{
    std::vector<std::unique_ptr<CtiPointDataMsg>> pointdata;

    long deviceId{};

    if( const auto device = _deviceManager.getDeviceByRfnIdentifier(deviceReport.rfnId) )
    {
        deviceId = device->getID();

        for( const auto& drValue : deviceReport.values )
        {
            try
            {
                const auto attributeDescriptor = MetricIdLookup::GetAttributeDescription(drValue.metricId, device->getDeviceType());

                double value = drValue.value;

                if( attributeDescriptor.magnitude )
                {
                    value /= std::pow(10, attributeDescriptor.magnitude);  //  Adjust from Watts to kiloWatts, etc, if necessary
                }

                if( const auto pointOffset = DeviceAttributeLookup::Lookup(device->getDeviceType(), attributeDescriptor.attrib) )
                {
                    if( const auto point = _pointManager.getOffsetTypeEqual(device->getID(), pointOffset->offset, pointOffset->type) )
                    {
                        if( point->isNumeric() )
                        {
                            auto& numeric = static_cast<CtiPointNumeric&>(*point);

                            value = numeric.computeValueForUOM(value);
                        }

                        CtiTime timestamp { drValue.timestamp };

                        auto pdata =                         
                            std::make_unique<CtiPointDataMsg>(
                                point->getID(),
                                value,
                                drValue.quality,
                                point->getType(),
                                device->getName() + " / " + point->getName() + " = " + std::to_string(value) + " @ " + timestamp.asString());

                        pdata->setTime(timestamp);

                        pointdata.emplace_back(std::move(pdata));
                    }
                    else
                    {
                        FormattedList l;

                        l << "Point not found for offset+type";
                        l.add("Device ID")   << device->getID();
                        l.add("Device name") << device->getName();
                        l.add("Device type") << desolveDeviceType(device->getDeviceType());
                        l.add("Offset") << pointOffset->offset;
                        l.add("Type")   << desolvePointType(pointOffset->type);
                        l << drValue;

                        CTILOG_WARN(dout, l);
                    }
                }
                else
                {
                    FormattedList l;

                    l << "Attribute mapping not found";
                    l.add("Device ID")   << device->getID();
                    l.add("Device name") << device->getName();
                    l.add("Device type") << desolveDeviceType(device->getDeviceType());
                    l << drValue;

                    CTILOG_WARN(dout, l);
                }
            }
            catch( AttributeMappingNotFound &ex )
            {
                //  Exclude it from the response, but log loudly.
                CTILOG_EXCEPTION_ERROR(dout, ex);
            }
        }
    }
    else
    {
        CTILOG_WARN(dout, "Device not found" << deviceReport);
    }

    //  Increment data streaming stats for each received attribute
    for( auto &dv : deviceReport.values )
    {
        stats[deviceId][dv.metricId]++;
    }

    return pointdata;
}


void RfDataStreamingProcessor::handleStatistics()
{
    if( CtiTime::now() > nextStatisticsReport )
    {
        nextStatisticsReport = nextScheduledTimeAlignedOnRate(nextStatisticsReport, statsReportFrequency);

        FormattedTable report;

        report.setHorizontalBorders(FormattedTable::Borders_Outside_Bottom, 0);
        report.setVerticalBorders  (FormattedTable::Borders_All);

        std::map<uint16_t, size_t> metricColumns;

        size_t row {}, maxCol {};

        report.setCell(row, maxCol++) << "Device ID";
        report.setCell(row, maxCol++) << "Device name";
        report.setCell(row, maxCol++) << "RFN Identifier";

        ++row;

        for( const auto& dev : stats )
        {
             report.setCell(row, 0) << dev.first;

             if( dev.first )
             {
                 if( auto device = _deviceManager.getDeviceByID(dev.first) )
                 {
                     report.setCell(row, 1) << device->getName();

                     if( auto rfnDevice = dynamic_cast<const Devices::RfnDevice *>(device.get()) )
                     {
                         report.setCell(row, 2) << rfnDevice->getRfnIdentifier();
                     }
                     else
                     {
                         report.setCell(row, 2) << "(not an RfnDevice)";
                     }
                 }
                 else
                 {
                     report.setCell(row, 1) << "(invalid device id)";
                 }
             }
             else
             {
                 report.setCell(row, 1) << "(not found)";
             }

             for( const auto& metricCounts : dev.second )
             {
                 //  Look to see if we've already got a column for this metric ID
                 auto metricColumn = metricColumns.find(metricCounts.first);
                 //  If not, insert it
                 if( metricColumn == metricColumns.end() )
                 {
                     metricColumn = metricColumns.emplace(metricCounts.first, maxCol++).first;
                 }
                 //  Use metricColumn->second to get the column number
                 report.setCell(row, metricColumn->second) << metricCounts.second;
             }
             ++row;
        }

        //  Set the headers
        for( const auto& metricColumn : metricColumns )
        {
            report.setCell(0, metricColumn.second) << "ID " << metricColumn.first;
        }

        stats.clear();

        CTILOG_INFO(dout, "RFN data streaming statistics report:" << report);
    }
}

}