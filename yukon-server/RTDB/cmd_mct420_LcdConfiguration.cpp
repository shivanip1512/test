#include "yukon.h"

#include "cmd_mct420_LcdConfiguration.h"

#include <sstream>

namespace Cti {
namespace Devices {
namespace Commands {

char *MetricNames[] =
    {
        "Slot Disabled",
        "No Segments",
        "All Segments",
        "Tamper",
        "Current Local Time",
        "Current Local Date",
        "Total kWh",
        "Net kWh",
        "Delivered kWh",
        "Reverse kWh",
        "Last Interval kW",
        "Peak kW",
        "Peak kW Date",
        "Peak kW Time",
        "Last Interval Voltage",
        "Peak Voltage",
        "Peak Voltage Date",
        "Peak Voltage Time",
        "Minimum Voltage",
        "Minimum Voltage Date",
        "Minimum Voltage Time",
        "TOU Rate A kWh",
        "TOU Rate A Peak kW",
        "TOU Rate A Date of Peak kW",
        "TOU Rate A Time of Peak kW",
        "TOU Rate B kWh",
        "TOU Rate B Peak kW",
        "TOU Rate B Date of Peak kW",
        "TOU Rate B Time of Peak kW",
        "TOU Rate C kWh",
        "TOU Rate C Peak kW",
        "TOU Rate C Date of Peak kW",
        "TOU Rate C Time of Peak kW",
        "TOU Rate D kWh",
        "TOU Rate D Peak kW",
        "TOU Rate D Date of Peak kW",
        "TOU Rate D Time of Peak kW",
        "TOU Rate E kWh",
        "TOU Rate E Peak kW",
        "TOU Rate E Date of Peak kW",
        "TOU Rate E Time of Peak kW"
    };

Mct420LcdConfigurationCommand::Mct420LcdConfigurationCommand(const metric_vector_t &display_metrics, bool reads_only) :
    _display_metrics(display_metrics),
    _executionState(reads_only ? &Mct420LcdConfigurationCommand::read1 : &Mct420LcdConfigurationCommand::write1)
{
}


//  throws CommandException
DlcCommand::request_ptr Mct420LcdConfigurationCommand::execute(CtiTime now)
{
    //  now that ExecuteRequest() is wrapped in a try/catch, we could move this check to the constructor
    if( _display_metrics.size() > TotalDisplayMetricSlots )
    {
        throw CommandException(BADPARAM, "Invalid number of display metrics (" + CtiNumStr(_display_metrics.size()) + ")");
    }

    return doCommand();
}

DlcCommand::request_ptr Mct420LcdConfigurationCommand::decode(const CtiTime now, const unsigned function, const payload_t &payload, std::string &description, std::vector<point_data> &points)
{
    if( ! payload.empty() )
    {
        std::ostringstream metric_description;

        unsigned display_number = 0;

        if(  function == Read_LcdConfiguration2 )
        {
            display_number = DisplayMetricSlotsPerRead;
        }

        unsigned char total_metrics = sizeof(MetricNames) / sizeof(MetricNames[0]);

        for each(unsigned char metric in payload)
        {
            metric_description << "Display metric " << ++display_number << ": ";

            if( metric < total_metrics )
            {
                metric_description << MetricNames[metric] << "\n";
            }
            else
            {
                metric_description << "Unsupported metric [" << (int)metric << "]\n";
            }
        }

        description += metric_description.str();
    }

    return doCommand();
}

//  throws CommandException
DlcCommand::request_ptr Mct420LcdConfigurationCommand::error(const CtiTime now, const int error_code, std::string &description)
{
    //  This should probably be the default for all commands unless otherwise specified.
    throw CommandException(error_code, GetError(error_code));
}

DlcCommand::request_ptr Mct420LcdConfigurationCommand::doCommand()
{
    //  call the current state's member function
    return _executionState(this);
}

DlcCommand::request_ptr Mct420LcdConfigurationCommand::write1()
{
    const metric_vector_t::iterator dm_end = _display_metrics.begin() + std::min<unsigned>(_display_metrics.size(), DisplayMetricSlotsPerWrite);

    metric_vector_t metrics(_display_metrics.begin(), dm_end);

    _display_metrics.erase(_display_metrics.begin(), dm_end);

    if( metrics.size() < DisplayMetricSlotsPerWrite )
    {
        _executionState = &Mct420LcdConfigurationCommand::read1;
    }
    else
    {
        _executionState = &Mct420LcdConfigurationCommand::write2;
    }

    return request_ptr(new write_request_t(Write_LcdConfiguration1, metrics));
}

DlcCommand::request_ptr Mct420LcdConfigurationCommand::write2()
{
    _executionState = &Mct420LcdConfigurationCommand::read1;

    return request_ptr(new write_request_t(Write_LcdConfiguration2, _display_metrics));
}

DlcCommand::request_ptr Mct420LcdConfigurationCommand::read1()
{
    _executionState = &Mct420LcdConfigurationCommand::read2;

    return request_ptr(new read_request_t(Read_LcdConfiguration1, DisplayMetricSlotsPerRead));
}

DlcCommand::request_ptr Mct420LcdConfigurationCommand::read2()
{
    _executionState = &Mct420LcdConfigurationCommand::done;

    return request_ptr(new read_request_t(Read_LcdConfiguration2, DisplayMetricSlotsPerRead));
}

DlcCommand::request_ptr Mct420LcdConfigurationCommand::done()
{
    return request_ptr();
}

}
}
}
