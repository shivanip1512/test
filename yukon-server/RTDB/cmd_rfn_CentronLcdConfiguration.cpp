#include "precompiled.h"

#include "cmd_rfn_CentronLcdConfiguration.h"

#include <sstream>

namespace Cti {
namespace Devices {
namespace Commands {

namespace {

const char *MetricNames[] =
    {
        "Metric Slot Disabled",                        //  0x00
        "No Segments",                                 //  0x01
        "All Segments",                                //  0x02
        "Tamper",                                      //  0x03
        "Current Local Time",                          //  0x04
        "Current Local Date",                          //  0x05
        "Unidir kWh (Sum of Forward and Reverse)",     //  0x06
        "Net kWh (Difference of Forward and Reverse)", //  0x07
        "Delivered kWh",                               //  0x08
        "Received kWh",                                //  0x09
        "Last Interval Demand (W)",                    //  0x0A
        "Peak Demand (W)",                             //  0x0B
        "Date of Peak Demand (W)",                     //  0x0C
        "Time of Peak Demand (W)",                     //  0x0D
        "Last Interval Voltage (V)",                   //  0x0E
        "Peak Voltage (V)",                            //  0x0F
        "Date of Peak Voltage",                        //  0x10
        "Time of Peak Voltage",                        //  0x11
        "Min Voltage (V)",                             //  0x12
        "Date of Min Voltage",                         //  0x13
        "Time of Min Voltage",                         //  0x14
        "TOU Rate A kWh",                              //  0x15
        "TOU Rate A Peak Demand (W)",                  //  0x16
        "TOU Rate A Date of Peak Demand",              //  0x17
        "TOU Rate A Time of Peak Demand",              //  0x18
        "TOU Rate B kWh",                              //  0x19
        "TOU Rate B Peak Demand (W)",                  //  0x1A
        "TOU Rate B Date of Peak Demand",              //  0x1B
        "TOU Rate B Time of Peak Demand",              //  0x1C
        "TOU Rate C kWh",                              //  0x1D
        "TOU Rate C Peak Demand (W)",                  //  0x1E
        "TOU Rate C Date of Peak Demand",              //  0x1F
        "TOU Rate C Time of Peak Demand",              //  0x20
        "TOU Rate D kWh",                              //  0x21
        "TOU Rate D Peak Demand (W)",                  //  0x22
        "TOU Rate D Date of Peak Demand",              //  0x23
        "TOU Rate D Time of Peak Demand",              //  0x24
    };

const unsigned int total_metrics = sizeof(MetricNames) / sizeof(MetricNames[0]);

} // anonymous namespace


//  Class constructor
RfnCentronLcdConfigurationCommand::RfnCentronLcdConfigurationCommand(const metric_vector_t &display_metrics, bool reads_only) :
    _display_metrics(display_metrics),
    _read_only(reads_only)
{
}

//  Decode a Lcd configuration response
RfnCommand::RfnResult RfnCentronLcdConfigurationCommand::decode(const CtiTime now, const RfnResponse &response)
{
    RfnResult result;

    if( response.size() < 2 )
    {
        // "Invalid response"
        throw CommandException(ErrorInvalidData, GetErrorString(ErrorInvalidData));
    }

    if( response[0] != LcdConfiguration_CommandCode_Response )
    {
        // "Invalid command code"
        throw CommandException(ErrorInvalidData, GetErrorString(ErrorInvalidData));
    }

    const int metrics_nbr = response[1];

    if( metrics_nbr != response.size() - 2 )
    {
        // "Invalid number of display metrics"
        throw CommandException(ErrorInvalidData, GetErrorString(ErrorInvalidData));
    }

    if( !metrics_nbr )
    {
        result.description = "No display metric found";
    }
    else
    {
        std::ostringstream metric_description;

        std::vector<unsigned char> metrics = std::vector<unsigned char>( response.begin()+2, response.begin()+2+metrics_nbr );

        for each(unsigned char metric in metrics)
        {
            metric_description << "Display metric : ";

            if( metric < total_metrics )
            {
                metric_description << MetricNames[metric] << "\n";
            }
            else
            {
                metric_description << "Unsupported metric [" << (int)metric << "]\n";
            }
        }

        result.description += metric_description.str();
    }

    return result;
}

//  throws CommandException
RfnCommand::RfnResult RfnCentronLcdConfigurationCommand::error(const CtiTime now, const YukonError_t error_code)
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
    return (_read_only) ? LcdConfiguration_Operation_Read : LcdConfiguration_Operation_Write;
}

//  returns the data portion a request
RfnCommand::Bytes RfnCentronLcdConfigurationCommand::getData()
{
    Bytes data;

    if( _read_only )
    {
        data.push_back(0); // zero items
    }
    else
    {
        data.push_back(_display_metrics.size());
        data.insert(data.end(), _display_metrics.begin(), _display_metrics.end());
    }

    return data;
}


}
}
}
