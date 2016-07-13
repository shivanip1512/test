#include "precompiled.h"

#include "rf_data_streaming_processor.h"

#include "amq_connection.h"
#include "dev_rfn.h"
#include "rfn_statistics.h"
#include "std_helper.h"
#include "error_helper.h"
#include "MetricIdLookup.h"

#include <boost/range/adaptor/transformed.hpp>
#include <boost/range/algorithm/count_if.hpp>
#include <boost/range/algorithm/transform.hpp>
#include <boost/range/numeric.hpp>

#include <chrono>

using Cti::Logging::Vector::Hex::operator<<;
using Cti::Messaging::Rfn::E2eMessenger;

static const auto RF_DATA_STREAMING_STATS_REPORTING_INTERVAL = 86400;

namespace Cti {
namespace Pil {

namespace {

    //  Jan 1 2016 0:00 GMT
    static const auto DataStreamingEpoch = std::chrono::system_clock::from_time_t(1'451'606'400);
}

void RfDataStreamingProcessor::start()
{
    E2eMessenger::registerDataStreamingHandler(
            [&](const E2eMessenger::Indication &msg)
            {
                LockGuard guard(_packetMux);

                _packets.push_back(msg);
            });
}


void RfDataStreamingProcessor::tick()
{
    PacketQueue newPackets;

    {
        LockGuard g{ _packetMux };

        newPackets.swap(_packets);
    }

    std::vector<DeviceReport> results;

    boost::range::transform(
        newPackets, 
        std::back_inserter(results),
        [](const auto& packet) { return processPacket(packet); });

    std::vector<std::unique_ptr<CtiPointDataMsg>> pointData;

    boost::range::transform(
        results, 
        std::back_inserter(pointData),
        [this](const auto& deviceReport) { return processDeviceReport(deviceReport); });

    handleStatistics();

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
        case 1:  return AbnormalQuality;  //  MeterReadTimeout  
        case 2:  return InvalidQuality;   //  NoSuchChannel     
        case 3:  return AbnormalQuality;  //  MeterAccessError  
        case 4:  return AbnormalQuality;  //  MeterOrNodeBusy   
        case 5:  return AbnormalQuality;  //  MeterProtocolError
        case 6:  return AbnormalQuality;  //  UnknownError     
        default: return UnknownQuality;
    }
}

auto RfDataStreamingProcessor::processPacket(const Packet &p) -> DeviceReport
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
        const auto dataValue = get_uint32(payload.begin() + offset + 7);

        try
        {
            dr.values.emplace_back(Value{
                MetricIdLookup::getAttribute(metricId),
                DataStreamingEpoch + std::chrono::seconds(timestamp),
                dataValue * scaling(modifier / 16),
                convertQuality(modifier % 16)});
        }
        catch( AttributeMappingNotFound &ex )
        {
            //  Exclude it from the response, but log loudly.
            CTILOG_EXCEPTION_ERROR(dout, ex);
        }
    }
    return dr;
}

auto RfDataStreamingProcessor::processDeviceReport(const DeviceReport& deviceReport) -> std::unique_ptr<CtiPointDataMsg>
{
    //  To be implemented under YUK-15553
    return nullptr;
}

/*
unsigned statsReportFrequency = gConfigParms.getValueAsInt("RF_DATA_STREAMING_STATS_REPORTING_INTERVAL", RF_DATA_STREAMING_STATS_REPORTING_INTERVAL);
CtiTime nextStatisticsReport = nextScheduledTimeAlignedOnRate(CtiTime::now(), statsReportFrequency);
*/

void RfDataStreamingProcessor::handleStatistics()
{/*
    if( CtiTime::now() > nextStatisticsReport )
    {
        nextStatisticsReport = nextScheduledTimeAlignedOnRate(nextStatisticsReport, statsReportFrequency);

        StreamBuffer report;

        report << "RFN data streaming statistics report:" << std::endl;

        CTILOG_INFO(dout, report);
    }*/
}

}
} //namespace Cti::Pil
