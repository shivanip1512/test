#include "precompiled.h"

#include "cmd_rfn_CentronLcdConfiguration.h"

#include "cmd_rfn_helper.h"
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

const std::map<unsigned char, std::string> StatusItems = boost::assign::map_list_of
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

const std::map<unsigned, std::string> DisplayDigitNames = boost::assign::map_list_of
    ( 0x04, "4x1" )
    ( 0x05, "5x1" )
    ( 0x06, "6x1" );

const std::map<unsigned, std::string> DisconnectDisplayNames = boost::assign::map_list_of
    ( 0x00, "Disabled" )
    ( 0x01, "Enabled"  );

typedef boost::optional<std::string> OptionalString;

} // anonymous namespace


//  Class constructor
RfnCentronSetLcdConfigurationCommand::RfnCentronSetLcdConfigurationCommand(
        const metric_vector_t &display_metrics_, const DisconnectDisplayState disconnect_display_,
        const DisplayDigits display_digits_, const unsigned char cycle_time_) :
    display_metrics(display_metrics_),
    disconnect_display(disconnect_display_),
    display_digits(display_digits_),
    cycle_time(cycle_time_)
{
}

RfnCentronSetLcdConfigurationCommand::RfnCentronSetLcdConfigurationCommand(
        const metric_vector_t &display_metrics_, const DisplayDigits display_digits_, const unsigned char cycle_time_) :
    display_metrics(display_metrics_),
    display_digits(display_digits_),
    cycle_time(cycle_time_)
{
}

RfnCentronGetLcdConfigurationCommand::RfnCentronGetLcdConfigurationCommand()
{
}


void describeMetric(std::ostringstream &metric_description, unsigned metricIndex, unsigned char metric)
{
    metric_description << "\nDisplay metric " << ++metricIndex << ": ";

    OptionalString metricName = mapFind(MetricNames, metric);

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
    std::ostringstream resultDescription;

    validate(Condition(response.size() >= 3, ClientErrors::InvalidData)
            << "Response too small - (" << response.size() << ", expecting >=3)");

    validate(Condition(response[0] == LcdConfiguration_CommandCode_Response, ClientErrors::InvalidData)
            << "Invalid command code - (" << response[0] << ", expecting LcdConfiguration_CommandCode_Response)");

    const OptionalString status = mapFind(StatusItems, response[1]);

    validate(Condition( !! status, ClientErrors::InvalidData)
            << "Invalid status code - (" << response[1] << ")");

    const int metrics_nbr = response[2];

    validate(Condition((metrics_nbr * 2) == (response.size() - 3), ClientErrors::InvalidData)
            << "Invalid display metric length - (" << (response.size() - 3) << ", expecting " << (metrics_nbr * 2)  + ")");

    for( unsigned i = 0; i < metrics_nbr; ++i )
    {
        const unsigned slot   = response[3 + i * 2];
        const unsigned metric = response[4 + i * 2];

        switch( slot )
        {
            case Slot_CycleDelay:
                    _cycleTime = metric;
                    break;

            case Slot_DigitConfiguration:
                    _digitConfiguration = metric;
                    break;

            case Slot_DisconnectDisplay:
                    _disconnectDisplay = metric;
                    break;

            default:
                    _displayMetrics[slot] = metric;
                    break;
        }
    }

    resultDescription << "Display metrics:" << describeMetrics(_displayMetrics);

    if( _disconnectDisplay )
    {
        resultDescription << "\nDisconnect display: ";

        if( OptionalString disconnectDisplayName = mapFind(DisconnectDisplayNames, *_disconnectDisplay) )
        {
            resultDescription << *disconnectDisplayName;
        }
        else
        {
            resultDescription << "Unsupported disconnect display configuration [" << (unsigned)*_disconnectDisplay << "]";
        }
    }
    if( _cycleTime )
    {
        resultDescription << "\nLCD cycle time: ";

        if( *_cycleTime )
        {
            resultDescription << (unsigned)*_cycleTime << (*_cycleTime == 1 ? " second" : " seconds");
        }
        else
        {
            resultDescription << "(default)";
        }
    }
    if( _digitConfiguration )
    {
        resultDescription << "\nDisplay digits: ";

        if( OptionalString displayDigitName = mapFind(DisplayDigitNames, *_digitConfiguration) )
        {
            resultDescription << *displayDigitName;
        }
        else
        {
            resultDescription << "Unsupported display digit configuration [" << (unsigned)*_digitConfiguration << "]";
        }
    }

    return resultDescription.str();
}

//  Decode a Lcd configuration response
RfnCommandResult RfnCentronSetLcdConfigurationCommand::decodeCommand(const CtiTime now, const RfnResponsePayload &response)
{
    std::ostringstream resultDescription;

    validate(Condition(response.size() >= 3, ClientErrors::InvalidData)
            << "Response too small - (" << response.size() << ", expecting >= 3)");

    validate(Condition(response[0] == LcdConfiguration_CommandCode_Response, ClientErrors::InvalidData)
            << "Invalid command code - (" << response[0] << ", expecting LcdConfiguration_CommandCode_Response)");

    const OptionalString status = mapFind(StatusItems, response[1]);

    validate(Condition( !! status, ClientErrors::InvalidData)
            << "Invalid status code - (" << response[1] << ")");

    const int metrics_nbr = response[2];

    validate(Condition(metrics_nbr == 0, ClientErrors::InvalidData)
            << "Invalid number of display metrics - (" << metrics_nbr << ", expecting 0)");

    resultDescription << "Display metrics successfully set" << describeMetrics(display_metrics);

    if( disconnect_display )
    {
        resultDescription
                << "\nDisconnect display: "
                << (*disconnect_display
                       ? "enabled"
                       : "disabled");
    }

    resultDescription << "\nLCD cycle time: ";
    if( cycle_time )
    {
        resultDescription << (unsigned)cycle_time << (cycle_time == 1 ? " second" : " seconds");
    }
    else
    {
        resultDescription << "(default)";
    }

    resultDescription << "\nDisplay digits: ";
    if( OptionalString displayDigitName = mapFind(DisplayDigitNames, display_digits) )
    {
        resultDescription << *displayDigitName;
    }
    else
    {
        resultDescription << "Unsupported display digit configuration [" << display_digits << "]";
    }

    return resultDescription.str();
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

    data.push_back(display_metrics.size() + 2 + disconnect_display.is_initialized());  //  add 2 for the digit config and cycle delay, and add disconnect display if it's set

    unsigned metric_number = 0;

    for each( unsigned char display_metric in display_metrics )
    {
        data.push_back(metric_number++);
        data.push_back(display_metric);
    }

    data.push_back(Slot_DigitConfiguration);
    data.push_back(display_digits);

    data.push_back(Slot_CycleDelay);
    data.push_back(cycle_time);

    if( disconnect_display )
    {
        data.push_back(Slot_DisconnectDisplay);
        data.push_back(*disconnect_display);
    }

    return data;
}

//  returns the data portion a request
RfnCommand::Bytes RfnCentronGetLcdConfigurationCommand::getCommandData()
{
    return boost::assign::list_of(0);
}


RfnCentronGetLcdConfigurationCommand::metric_map_t RfnCentronGetLcdConfigurationCommand::getDisplayMetrics() const
{
    return _displayMetrics;
}

boost::optional<bool> RfnCentronGetLcdConfigurationCommand::getDisconnectDisplayDisabled() const
{
    if( _disconnectDisplay )
    {
        return *_disconnectDisplay == 0;
    }

    return boost::none;
}

boost::optional<unsigned char>  RfnCentronGetLcdConfigurationCommand::getDigitConfiguration() const
{
    if( _digitConfiguration )
    {
        switch( *_digitConfiguration )
        {
            case DisplayDigits4x1:  return 4;
            case DisplayDigits5x1:  return 5;
            case DisplayDigits6x1:  return 6;
        }
    }

    return boost::none;
}

boost::optional<unsigned char> RfnCentronGetLcdConfigurationCommand::getLcdCycleTime() const
{
    return _cycleTime;
}


}
}
}
