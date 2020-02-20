#include "precompiled.h"

#include "cmd_rfn_DataStreamingConfiguration.h"
#include "cmd_rfn_helper.h"
#include "MetricIdLookup.h"

namespace Cti      {
namespace Devices  {
namespace Commands {

static const size_t HeaderLength = 2;
static const size_t BytesPerMetric = 5;
static const size_t SequenceLength = 4;
static const size_t ResponseHeaderLength = 1 + HeaderLength;

//  This comes from the DS streaming spec, 10030-06_Hub_Meter_Application_ICD, section 2.2.50.
//  It must match the Java enum in DataStreamingMetricStatus.java
std::array<std::string, 8> statusStrings {
    "OK",
    "METER_ACCESS_ERROR",
    "METER_OR_NODE_BUSY",
    "METER_READ_TIMEOUT",
    "METER_PROTOCOL_ERROR",
    "CHANNEL_NOT_SUPPORTED",
    "UNKNOWN_ERROR",
    "CHANNEL_NOT_ENABLED"
};


RfnDataStreamingConfigurationCommand::RfnDataStreamingConfigurationCommand(const DeviceTypes paoType)
    :   _paoType { paoType }
{}

unsigned char RfnDataStreamingConfigurationCommand::getOperation() const
{ 
    return {};  //  unused
}

DeviceCommand::Bytes RfnDataStreamingConfigurationCommand::getCommandHeader()
{ 
    return { getCommandCode() }; 
}

auto RfnDataStreamingConfigurationCommand::decodeConfigResponse(const RfnResponsePayload & response) const -> ConfigResponse
{
    validate(Condition(response.size() >= ResponseHeaderLength, ClientErrors::InvalidData) 
        << "Response size was less than " << ResponseHeaderLength);

    ConfigResponse cr;

    const auto responseCode = response[0];

    validate(Condition(responseCode == getResponseCode(), ClientErrors::InvalidData) 
        << "Invalid response code (" << responseCode << " != " << getResponseCode() << ")");

    const auto metricCount  = response[1];
    const auto globalEnable = response[2];

    if( globalEnable > 1 )
    {
        CTILOG_WARN(dout, "Streaming enable status is " << static_cast<int>(globalEnable) << ", expecting 0 or 1");
    }

    cr.streamingEnabled = response[2];

    const auto expectedLength = ResponseHeaderLength + (metricCount * BytesPerMetric) + SequenceLength;

    validate(Condition(response.size() >= expectedLength, ClientErrors::InvalidData)
        << "Response size was too small for reported metric count (" << response.size() << "<" << expectedLength << ")");

    for( int i = 0; i < metricCount; ++i )
    {
        const auto offset = ResponseHeaderLength + i * BytesPerMetric;
        const auto metricId = getValueFromBytes_bEndian(response, offset, 2);
        const auto enabled  = response[offset + 2];
        const auto interval = response[offset + 3];
        const auto status   = response[offset + 4];

        if( enabled > 1 )
        {
            CTILOG_WARN(dout, "Channel " << i << " enabled status is " << static_cast<int>(enabled) << ", expecting 0 or 1");
        }

        ConfigResponse::MetricConfiguration mc { static_cast<uint16_t>(metricId), static_cast<bool>(enabled), interval, status };

        cr.metrics.emplace_back(mc);
    }

    const auto offset = ResponseHeaderLength + metricCount * BytesPerMetric;

    cr.sequence = getValueFromBytes_bEndian(response, offset, 4);

    return cr;
}


std::string RfnDataStreamingConfigurationCommand::createJson(const ConfigResponse& response, const DeviceTypes paoType)
{
    StreamBuffer metricDescription;

    //  Generate the response as JSON for the Java client to consume

    metricDescription << "DATA_STREAMING_JSON{";
    metricDescription << "\n\"streamingEnabled\" : " << (response.streamingEnabled ? "true" : "false");

    metricDescription << ",\n\"configuredMetrics\" : [";

    bool seenDisabledChannel    = false;
    bool seenUnsupportedChannel = false;

    unsigned metricsPrinted = 0;

    for( const auto& metric : response.metrics )
    {
        try
        {
            const auto attribute = MetricIdLookup::GetAttribute(metric.metricId, paoType);

            std::string statusString;

            if( metricsPrinted++ )
            {
                metricDescription << ",";
            }

            if( metric.status < statusStrings.size() )
            {
                statusString = statusStrings[metric.status];
            }
            else
            {
                CTILOG_WARN(dout, "Received unknown status (" << metric.status << "), mapping to UNKNOWN_ERROR (6)");
                statusString = "UNKNOWN_ERROR";
            }

            metricDescription << "\n  {"
                "\n    \"attribute\" : \"" << attribute.getName() << "\","
                "\n    \"interval\" : " << metric.interval << ","
                "\n    \"enabled\" : " << (metric.enabled ? "true" : "false") << ","
                "\n    \"status\" : \"" << statusString << "\"\n  }";

            //  We only consider a metric's status when 1) streaming is globally enabled and 2) streaming is enabled for this specific channel
            if( response.streamingEnabled && metric.enabled && metric.status )
            {
                seenUnsupportedChannel |= (metric.status == 5);
                seenDisabledChannel    |= (metric.status == 7);

                CTILOG_WARN(dout, "Received status " << metric.status << " (" << statusString << ") for enabled metric " << metric.metricId);
            }
        }
        catch( AttributeMappingNotFound &ex )
        {
            //  Exclude it from the JSON response, but log quietly.
            CTILOG_EXCEPTION_WARN(dout, ex);
        }
    }

    metricDescription << "],\n\"sequence\" : " << response.sequence << "\n}";

    //  Disabled channel can be fixed by the user (by enabling the channel in rfnChannelConfiguration)
    //    so notify them about this first.
    if( seenDisabledChannel )
    {
        throw YukonErrorException(ClientErrors::ChannelDisabled, metricDescription);
    }
    //  If no disabled channels exist, then warn about Unsupported (not enabled in the meter itself).
    if( seenUnsupportedChannel )
    {
        throw YukonErrorException(ClientErrors::ChannelUnsupported, metricDescription);
    }

    return metricDescription;
}



RfnDataStreamingGetMetricsListCommand::RfnDataStreamingGetMetricsListCommand(const DeviceTypes paoType)
    :   RfnDataStreamingConfigurationCommand(paoType)
{}

unsigned char RfnDataStreamingGetMetricsListCommand::getCommandCode() const 
{ 
    return CommandCode_Request; 
}

DeviceCommand::Bytes RfnDataStreamingGetMetricsListCommand::getCommandData() 
{ 
    return {};  //  no data beyond the command code
}

unsigned char RfnDataStreamingGetMetricsListCommand::getResponseCode() const
{
    return CommandCode_Response;
}

RfnCommandResult RfnDataStreamingGetMetricsListCommand::decodeCommand(const CtiTime now, const RfnResponsePayload & response)
{
    const auto cr = decodeConfigResponse(response);

    return createJson(cr, _paoType);
}

std::string RfnDataStreamingGetMetricsListCommand::getCommandName() const
{
    return "Data Streaming Get Metrics List Request";
}


RfnDataStreamingSetMetricsCommand::RfnDataStreamingSetMetricsCommand(const DeviceTypes paoType, StreamingState enabled)
    :   RfnDataStreamingConfigurationCommand(paoType),
        _enabled(enabled) 
{}

RfnDataStreamingSetMetricsCommand::RfnDataStreamingSetMetricsCommand(const DeviceTypes paoType, MetricList&& states) 
    :   RfnDataStreamingConfigurationCommand(paoType),
        _enabled(StreamingEnabled), 
        _states(std::move(states)) 
{}


unsigned char RfnDataStreamingSetMetricsCommand::getCommandCode() const 
{ 
    return CommandCode_Request; 
}

DeviceCommand::Bytes RfnDataStreamingSetMetricsCommand::getCommandData()
{
    Bytes commandData;

    commandData.reserve(HeaderLength + BytesPerMetric * _states.size());

    commandData.push_back(_states.size());
    commandData.push_back(_enabled);

    for( const auto& s : _states )
    {
        insertValue_bEndian<2>(commandData, s.metricId);
        commandData.push_back(s.interval > 0);
        commandData.push_back(s.interval);  //  Disable sends an interval of 0, which is "don't change" on the node
    }

    return commandData;
}

unsigned char RfnDataStreamingSetMetricsCommand::getResponseCode() const
{
    return CommandCode_Response;
}

RfnCommandResult RfnDataStreamingSetMetricsCommand::decodeCommand(const CtiTime now, const RfnResponsePayload & response)
{
    const auto cr = decodeConfigResponse(response);

    auto json = createJson(cr, _paoType);

    //  If we've gotten this far, all of the enabled channels have an a MetricStatus of OK.
    if( !!_enabled != cr.streamingEnabled )
    {
        throw YukonErrorException(ClientErrors::ConfigNotCurrent, json);
    }

    std::map<unsigned, unsigned> reported;

    //  Build the device states into a map for lookup
    for( const auto& metric : cr.metrics )
    {
        reported.emplace(metric.metricId, metric.enabled ? metric.interval : 0);
    }

    //  We just need to make sure the requested metrics took effect
    for( const auto& state : _states )
    {
        if( const auto reportedInterval = mapFind(reported, state.metricId) )
        {
            if( *reportedInterval != state.interval )
            {
                throw YukonErrorException(ClientErrors::ConfigNotCurrent, json);
            }
        }
        else
        {
            throw YukonErrorException(ClientErrors::ConfigNotCurrent, json);
        }
    }

    return json;
}

std::string RfnDataStreamingSetMetricsCommand::getCommandName() const
{
    return "Data Streaming Set Metrics Request";
}


} // Commands
} // Devices
} // Cti
