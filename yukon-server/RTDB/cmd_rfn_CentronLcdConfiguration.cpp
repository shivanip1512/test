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
    LcdConfiguration_CommandCode_Request  = 0x69,
    LcdConfiguration_CommandCode_Response = 0x71,

    LcdConfiguration_Operation_Write      = 0x00,
    LcdConfiguration_Operation_Read       = 0x01,
};

std::map<unsigned, std::string> MetricNames = boost::assign::map_list_of
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
RfnCentronLcdConfigurationCommand::RfnCentronLcdConfigurationCommand(const metric_vector_t &display_metrics) :
    _display_metrics_to_send(display_metrics)
{
}

RfnCentronLcdConfigurationCommand::RfnCentronLcdConfigurationCommand()
{
}


void RfnCentronLcdConfigurationCommand::invokeResultHandler(RfnCommand::ResultHandler &rh) const
{
    rh.handleCommandResult(*this);
}


//  Decode a Lcd configuration response
RfnCommandResult RfnCentronLcdConfigurationCommand::decodeCommand(const CtiTime now, const RfnResponsePayload &response)
{
    RfnCommandResult result;

    if( response.size() < 2 )
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

    const int metrics_nbr = response[1];

    if( metrics_nbr != response.size() - 2 )
    {
        throw CommandException(
            ErrorInvalidData,
            "Invalid number of display metrics - (" + CtiNumStr(metrics_nbr) + ", expecting " + CtiNumStr(response.size() - 2) + ")");
    }

    if( ! metrics_nbr )
    {
        result.description = "No display metrics";

        return result;
    }

    std::ostringstream metric_description;

    _display_metrics_received.assign(
       response.begin() + 2,
       response.begin() + 2 + metrics_nbr );

    unsigned metricIndex = 0;

    for each( unsigned char metric in _display_metrics_received )
    {
        metric_description << "Display metric " << ++metricIndex << ": ";

        boost::optional<std::string> metricName = mapFind(MetricNames, metric);

        if( metricName )
        {
            metric_description << *metricName << "\n";
        }
        else
        {
            metric_description << "Unsupported metric [" << (unsigned)metric << "]\n";
        }
    }

    result.description += metric_description.str();

    return result;
}

//  throws CommandException
RfnCommandResult RfnCentronLcdConfigurationCommand::error(const CtiTime now, const YukonError_t error_code)
{
    //  This should probably be the default for all commands unless specified otherwise.
    throw CommandException(error_code, GetErrorString(error_code));
}

//  returns the command code
unsigned char RfnCentronLcdConfigurationCommand::getCommandCode() const
{
    return LcdConfiguration_CommandCode_Request;
}

//  returns the operation field of a request
unsigned char RfnCentronLcdConfigurationCommand::getOperation() const
{
    return _display_metrics_to_send ? LcdConfiguration_Operation_Write : LcdConfiguration_Operation_Read;
}

//  returns the data portion a request
RfnCommand::Bytes RfnCentronLcdConfigurationCommand::getCommandData()
{
    Bytes data;

    if( _display_metrics_to_send )
    {
        data.push_back(_display_metrics_to_send->size());
        data.insert(data.end(), _display_metrics_to_send->begin(), _display_metrics_to_send->end());
    }
    else
    {
        data.push_back(0); // zero items
    }

    return data;
}


RfnCentronLcdConfigurationCommand::metric_vector_t RfnCentronLcdConfigurationCommand::getReceivedMetrics() const
{
    return _display_metrics_received;
}


}
}
}
