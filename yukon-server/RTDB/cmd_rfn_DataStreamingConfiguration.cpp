#include "precompiled.h"

#include "cmd_rfn_DataStreamingConfiguration.h"
#include "cmd_rfn_helper.h"

#include <boost/optional.hpp>

namespace Cti      {
namespace Devices  {
namespace Commands {


unsigned char RfnDataStreamingConfigurationCommand::getOperation() const 
{ 
    return {};  //  unused
}


DeviceCommand::Bytes RfnDataStreamingConfigurationCommand::getCommandHeader()
{ 
    return { getCommandCode() }; 
}


unsigned char RfnDataStreamingGetMetricsListCommand::getCommandCode() const 
{ 
    return CommandCode_Request; 
}

DeviceCommand::Bytes RfnDataStreamingGetMetricsListCommand::getCommandData() 
{ 
    return{}; 
}

RfnCommandResult RfnDataStreamingGetMetricsListCommand::decodeCommand(const CtiTime now, const RfnResponsePayload & response)
{
    if( response[2] )
    {
        return
            "Data Streaming metrics:"
            "\n3 metrics configured"
            "\nMetric 1: DEMAND, 5 min"
            "\nMetric 2: POWER_FACTOR, 15 min, disabled individually"
            "\nMetric 3: VOLTAGE, 30 min"
            "\nSequence: 3735928559";
    }
    return
        "Data Streaming metrics:"
        "\nData streaming DISABLED"
        "\n3 metrics configured"
        "\nMetric 1: DEMAND, 5 min, disabled globally"
        "\nMetric 2: POWER_FACTOR, 15 min, disabled individually"
        "\nMetric 3: VOLTAGE, 30 min, disabled globally"
        "\nSequence: 3735928559";
}



unsigned char RfnDataStreamingSetMetricsCommand::getCommandCode() const 
{ 
    return CommandCode_Request; 
}

DeviceCommand::Bytes RfnDataStreamingSetMetricsCommand::getCommandData()
{
    if( ! _states.empty() )
    {
        if( _states.size() > 1 )
        {
            if( _states[1].enabled )
            {
                return{ 0x02, 0x01, 0x00, 0x05, 0x01, 0x05, 0x00, 0x53, 0x01, 0x0f };
            }
            else
            {
                return{ 0x02, 0x01, 0x00, 0x05, 0x01, 0x05, 0x00, 0x53, 0x00, 0x0f };
            }
        }
        if( _states[0].enabled )
        {
            return{ 0x01, 0x01, 0x00, 0x05, 0x01, 0x05 };
        }
        else
        {
            return{ 0x01, 0x01, 0x00, 0x53, 0x00, 0x0f };
        }
    }
    return{ 0x00, _enabled };
}


RfnCommandResult RfnDataStreamingSetMetricsCommand::decodeCommand(const CtiTime now, const RfnResponsePayload & response)
{
    if( ! _states.empty() )
    {
        if( _states.size() > 1 )
        {
            return "";
        }
        if( _states[0].enabled )
        {
            return
                "Data Streaming metrics:"
                "\n1 metric configured"
                "\nMetric 1: DEMAND, 5 min"
                "\nSequence: 3735928559";
        }
        else
        {
            return
                "Data Streaming metrics:"
                "\n1 metric configured"
                "\nMetric 1: DEMAND, 5 min, disabled individually"
                "\nSequence: 3735928559";
        }
    }
    if( _enabled )
    {
        return
            "Data Streaming metrics:"
            "\n2 metrics configured"
            "\nMetric 1: DEMAND, 5 min"
            "\nMetric 2: POWER_FACTOR, 15 min, disabled individually"
            "\nSequence: 3735928559";
    }
    else
    {
        return
            "Data Streaming metrics:"
            "\nData streaming DISABLED"
            "\n2 metrics configured"
            "\nMetric 1: DEMAND, 5 min, disabled globally"
            "\nMetric 2: POWER_FACTOR, 15 min, disabled individually"
            "\nSequence: 3735928559";
    }
}


} // Commands
} // Devices
} // Cti
