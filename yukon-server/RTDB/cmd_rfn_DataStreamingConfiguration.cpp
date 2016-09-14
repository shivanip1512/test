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

//  These need to match the Java enum in DataStreamingMetricStatus.java
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


unsigned char RfnDataStreamingConfigurationCommand::getOperation() const
{ 
    return {};  //  unused
}

DeviceCommand::Bytes RfnDataStreamingConfigurationCommand::getCommandHeader()
{ 
    return { getCommandCode() }; 
}

RfnCommandResult RfnDataStreamingConfigurationCommand::decodeCommand(const CtiTime now, const RfnResponsePayload & response)
{
    validate(Condition(response.size() >= ResponseHeaderLength, ClientErrors::InvalidData) 
        << "Response size was less than " << ResponseHeaderLength);

    const auto responseCode = response[0];

    validate(Condition(responseCode == getResponseCode(), ClientErrors::InvalidData) 
        << "Invalid response code (" << responseCode << " != " << getResponseCode() << ")");

    const auto metricCount = response[1];
    const auto streamingEnabled = response[2];

    const auto expectedLength = ResponseHeaderLength + (metricCount * BytesPerMetric) + SequenceLength;

    validate(Condition(response.size() >= expectedLength, ClientErrors::InvalidData)
        << "Response size was too small for reported metric count (" << response.size() << "<" << expectedLength << ")");

    StreamBuffer metricDescription;

    //  Generate the response as JSON for the Java client to consume
    
    metricDescription << "{";
    metricDescription << "\n\"streamingEnabled\" : " << (streamingEnabled ? "true" : "false");

    metricDescription << ",\n\"configuredMetrics\" : [";

    bool seenDisabledChannel    = false;
    bool seenUnsupportedChannel = false;

    for( int i = 0, metricsPrinted = 0; i < metricCount; ++i )
    {
        const auto offset = ResponseHeaderLength + i * BytesPerMetric;
        const auto metricId = getValueFromBytes_bEndian(response, offset, 2);
        const auto enabled  = response[offset + 2];
        const auto interval = response[offset + 3];
        const auto status   = response[offset + 4];

        try
        {
            const auto attribute = MetricIdLookup::getAttribute(metricId);
            
            if( metricsPrinted++ )
            {
                metricDescription << ",";
            }

            const auto statusString = (status < statusStrings.size() ? statusStrings[status] : "INVALID_STATUS");

            metricDescription << "\n  {"
                "\n    \"attribute\" : \"" << attribute.getName() << "\","
                "\n    \"interval\" : " << interval << ","
                "\n    \"enabled\" : " << (enabled ? "true" : "false") << ","
                "\n    \"status\" : \"" << statusString << "\"\n  }";

            if( enabled && status )
            {
                seenUnsupportedChannel |= (status == 5);
                seenDisabledChannel    |= (status == 7);

                CTILOG_WARN(dout, "Received status " << status << " (" << statusString << ") for enabled metric " << metricId);
            }
        }
        catch( AttributeMappingNotFound &ex )
        {
            //  Exclude it from the JSON response, but log loudly.
            CTILOG_EXCEPTION_ERROR(dout, ex);
        }
    }

    const auto offset = ResponseHeaderLength + metricCount * BytesPerMetric;

    const unsigned sequence = getValueFromBytes_bEndian(response, offset, 4);

    metricDescription << "],\n\"sequence\" : " << sequence << "\n}";

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


RfnDataStreamingSetMetricsCommand::RfnDataStreamingSetMetricsCommand(StreamingState enabled) 
    :   _enabled(enabled) 
{}

RfnDataStreamingSetMetricsCommand::RfnDataStreamingSetMetricsCommand(MetricList&& states) 
    :   _enabled(StreamingEnabled), 
        _states(std::move(states)) {}


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
        commandData.push_back(s.interval ? s.interval : 30);
    }

    return commandData;
}

unsigned char RfnDataStreamingSetMetricsCommand::getResponseCode() const
{
    return CommandCode_Response;
}


} // Commands
} // Devices
} // Cti
