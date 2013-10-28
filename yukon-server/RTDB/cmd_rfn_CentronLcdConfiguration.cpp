#include "precompiled.h"

#include "cmd_rfn_CentronLcdConfiguration.h"

#include "std_helper.h"

#include <boost/assign/list_of.hpp>

#include <sstream>

namespace Cti {
namespace Devices {
namespace Commands {

namespace {

enum
{
    LcdConfiguration_CommandCode_Request  = 0x70,
    LcdConfiguration_CommandCode_Response = 0x71,

    LcdConfiguration_Operation_Write      = 0x00,
    LcdConfiguration_Operation_Read       = 0x01,
};

const std::map<unsigned char, string> StatusItems = boost::assign::map_list_of
        ( 0x0, "Success" )
        ( 0x1, "Failure" );

const std::map<unsigned, std::string> MetricNames = boost::assign::map_list_of
    ( 0x00, "Metric Slot Disabled"                        )
    ( 0x01, "No Segments"                                 )
    ( 0x02, "All Segments"                                )
    ( 0x03, "Tamper"                                      )
    ( 0x04, "Current Local Time"                          )
    ( 0x05, "Current Local Date"                          )
    ( 0x06, "Unidir kWh (Sum of Forward and Reverse)"     )
    ( 0x07, "Net kWh (Difference of Forward and Reverse)" )
    ( 0x08, "Delivered kWh"                               )
    ( 0x09, "Received kWh"                                )
    ( 0x0A, "Last Interval Demand (W)"                    )
    ( 0x0B, "Peak Demand (W)"                             )
    ( 0x0C, "Date of Peak Demand (W)"                     )
    ( 0x0D, "Time of Peak Demand (W)"                     )
    ( 0x0E, "Last Interval Voltage (V)"                   )
    ( 0x0F, "Peak Voltage (V)"                            )
    ( 0x10, "Date of Peak Voltage"                        )
    ( 0x11, "Time of Peak Voltage"                        )
    ( 0x12, "Min Voltage (V)"                             )
    ( 0x13, "Date of Min Voltage"                         )
    ( 0x14, "Time of Min Voltage"                         )
    ( 0x15, "TOU Rate A kWh"                              )
    ( 0x16, "TOU Rate A Peak Demand (W)"                  )
    ( 0x17, "TOU Rate A Date of Peak Demand"              )
    ( 0x18, "TOU Rate A Time of Peak Demand"              )
    ( 0x19, "TOU Rate B kWh"                              )
    ( 0x1A, "TOU Rate B Peak Demand (W)"                  )
    ( 0x1B, "TOU Rate B Date of Peak Demand"              )
    ( 0x1C, "TOU Rate B Time of Peak Demand"              )
    ( 0x1D, "TOU Rate C kWh"                              )
    ( 0x1E, "TOU Rate C Peak Demand (W)"                  )
    ( 0x1F, "TOU Rate C Date of Peak Demand"              )
    ( 0x20, "TOU Rate C Time of Peak Demand"              )
    ( 0x21, "TOU Rate D kWh"                              )
    ( 0x22, "TOU Rate D Peak Demand (W)"                  )
    ( 0x23, "TOU Rate D Date of Peak Demand"              )
    ( 0x24, "TOU Rate D Time of Peak Demand"              );

} // anonymous namespace


//  Class constructor
RfnCentronSetLcdConfigurationCommand::RfnCentronSetLcdConfigurationCommand(const metric_vector_t &display_metrics) :
    display_metrics_to_send(display_metrics)
{
}

RfnCentronGetLcdConfigurationCommand::RfnCentronGetLcdConfigurationCommand()
{
}


void RfnCentronSetLcdConfigurationCommand::invokeResultHandler(RfnCommand::ResultHandler &rh) const
{
    rh.handleCommandResult(*this);
}

void RfnCentronGetLcdConfigurationCommand::invokeResultHandler(RfnCommand::ResultHandler &rh) const
{
    rh.handleCommandResult(*this);
}

void describeMetric(std::ostringstream &metric_description, unsigned metricIndex, unsigned char metric)
{
    metric_description << "\nDisplay metric " << ++metricIndex << ": ";

    boost::optional<std::string> metricName = mapFind(MetricNames, metric);

    if( metricName )
    {
        metric_description << *metricName;
    }
    else
    {
        metric_description << "Unsupported metric [" << (unsigned)metric << "]";
    }
}

std::string describeMetrics(const RfnCentronLcdConfigurationCommand::metric_vector_t &metrics)
{
    if( metrics.empty() )
    {
        return "\nNo display metrics";
    }
    std::ostringstream metric_description;

    unsigned metricIndex = 0;

    for each( unsigned char metric in metrics )
    {
        describeMetric(metric_description, metricIndex++, metric);
    }

    return metric_description.str();
}

std::string describeMetrics(const RfnCentronLcdConfigurationCommand::metric_map_t &metrics)
{
    if( metrics.empty() )
    {
        return "\nNo display metrics";
    }

    std::ostringstream metric_description;

    for each( const RfnCentronLcdConfigurationCommand::metric_map_t::value_type &metric in metrics )
    {
        describeMetric(metric_description, metric.first, metric.second);
    }

    return metric_description.str();
}

//  Decode a Lcd configuration response
RfnCommandResult RfnCentronGetLcdConfigurationCommand::decodeCommand(const CtiTime now, const RfnResponsePayload &response)
{
    RfnCommandResult result;

    if( response.size() < 3 )
    {
        throw CommandException(
            ErrorInvalidData,
            "Response too small - (" + CtiNumStr(response.size()) + ", expecting >=2)");
    }

    if( response[0] != LcdConfiguration_CommandCode_Response )
    {
        throw CommandException(
            ErrorInvalidData,
            "Invalid command code - (" + CtiNumStr(response[0]) + ", expecting LcdConfiguration_CommandCode_Response)");
    }

    const boost::optional<std::string> status = mapFind(StatusItems, response[1]);

    if( ! status )
    {
        throw CommandException(
            ErrorInvalidData,
            "Invalid status code - (" + CtiNumStr(response[1]) + ")");
    }

    const int metrics_nbr = response[2];

    if( (metrics_nbr * 2) != (response.size() - 3) )
    {
        throw CommandException(
            ErrorInvalidData,
            "Invalid display metric length - (" + CtiNumStr(response.size() - 3) + ", expecting " + CtiNumStr(metrics_nbr * 2)  + ")");
    }

    for( unsigned i = 0; i < metrics_nbr; ++i )
    {
        const unsigned slot   = response[3 + i * 2];
        const unsigned metric = response[4 + i * 2];

        switch( slot )
        {
            case Slot_CycleDelay:
                    _cycleDelay = metric;
                    break;

            case Slot_DigitConfiguration:
                    _digitConfiguration = metric;
                    break;

            case Slot_DisconnectDisplay:
                    _disconnectDisplay = metric;
                    break;

            default:
                    _display_metrics_received[slot] = metric;
                    break;
        }
    }

    result.description = "Display metrics:" + describeMetrics(_display_metrics_received);

    if( _cycleDelay )
    {
        result.description += "\nCycle delay        : " + CtiNumStr(*_cycleDelay);
    }
    if( _digitConfiguration )
    {
        result.description += "\nDigit configuration: " + CtiNumStr(*_digitConfiguration);
    }
    if( _disconnectDisplay )
    {
        result.description += "\nDisconnect display : " + CtiNumStr(*_disconnectDisplay);
    }

    return result;
}

//  Decode a Lcd configuration response
RfnCommandResult RfnCentronSetLcdConfigurationCommand::decodeCommand(const CtiTime now, const RfnResponsePayload &response)
{
    RfnCommandResult result;

    if( response.size() < 3 )
    {
        throw CommandException(
            ErrorInvalidData,
            "Response too small - (" + CtiNumStr(response.size()) + ", expecting >=2)");
    }

    if( response[0] != LcdConfiguration_CommandCode_Response )
    {
        throw CommandException(
            ErrorInvalidData,
            "Invalid command code - (" + CtiNumStr(response[0]) + ", expecting LcdConfiguration_CommandCode_Response)");
    }

    const boost::optional<std::string> status = mapFind(StatusItems, response[1]);

    if( ! status )
    {
        throw CommandException(
            ErrorInvalidData,
            "Invalid status code - (" + CtiNumStr(response[1]) + ")");
    }

    const int metrics_nbr = response[2];

    if( metrics_nbr != 0 )
    {
        throw CommandException(
            ErrorInvalidData,
            "Invalid number of display metrics - (" + CtiNumStr(metrics_nbr) + ", expecting 0)");
    }

    result.description = "Display metrics successfully set" + describeMetrics(display_metrics_to_send);

    return result;
}

//  returns the command code
unsigned char RfnCentronLcdConfigurationCommand::getCommandCode() const
{
    return LcdConfiguration_CommandCode_Request;
}

//  returns the operation field of a request
unsigned char RfnCentronSetLcdConfigurationCommand::getOperation() const
{
    return LcdConfiguration_Operation_Write;
}

//  returns the operation field of a request
unsigned char RfnCentronGetLcdConfigurationCommand::getOperation() const
{
    return LcdConfiguration_Operation_Read;
}

//  returns the data portion a request
RfnCommand::Bytes RfnCentronSetLcdConfigurationCommand::getCommandData()
{
    Bytes data;

    data.push_back(display_metrics_to_send.size());

    unsigned metric_number = 0;

    for each( unsigned char display_metric in display_metrics_to_send )
    {
        data.push_back(metric_number++);
        data.push_back(display_metric);
    }

    return data;
}

//  returns the data portion a request
RfnCommand::Bytes RfnCentronGetLcdConfigurationCommand::getCommandData()
{
    return boost::assign::list_of(0);
}


RfnCentronGetLcdConfigurationCommand::metric_map_t RfnCentronGetLcdConfigurationCommand::getReceivedMetrics() const
{
    return _display_metrics_received;
}


}
}
}
