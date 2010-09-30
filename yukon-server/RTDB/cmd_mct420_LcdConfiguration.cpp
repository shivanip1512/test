#include "yukon.h"

#include "cmd_mct420_LcdConfiguration.h"

namespace Cti {
namespace Devices {
namespace Commands {

Mct420LcdConfigurationCommand::Mct420LcdConfigurationCommand(const metric_vector_t &display_metrics, bool reads_only) :
    _display_metrics(display_metrics),
    _executionState(reads_only ? &Mct420LcdConfigurationCommand::read1 : &Mct420LcdConfigurationCommand::write1)
{
}


//  throws CommandException
DlcCommand::request_ptr Mct420LcdConfigurationCommand::execute(CtiTime now)
{
    //  now that ExecuteRequest() is wrapped in a try/catch, we could move this check to the constructor
    if( _display_metrics.size() > 26 )
    {
        throw CommandException(BADPARAM, "Invalid number of display metrics (" + CtiNumStr(_display_metrics.size()) + ")");
    }

    return doCommand();
}

DlcCommand::request_ptr Mct420LcdConfigurationCommand::decode(const CtiTime now, const unsigned function, const payload_t &payload, std::string &description, std::vector<point_data> &points)
{
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
    const metric_vector_t::iterator dm_end = _display_metrics.begin() + std::min<unsigned>(_display_metrics.size(), 13);

    metric_vector_t metrics(_display_metrics.begin(), dm_end);

    _display_metrics.erase(_display_metrics.begin(), dm_end);

    if( metrics.size() < 13 )
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

    return request_ptr(new read_request_t(Read_LcdConfiguration1, 13));
}

DlcCommand::request_ptr Mct420LcdConfigurationCommand::read2()
{
    _executionState = &Mct420LcdConfigurationCommand::done;

    return request_ptr(new read_request_t(Read_LcdConfiguration2, 13));
}

DlcCommand::request_ptr Mct420LcdConfigurationCommand::done()
{
    return request_ptr();
}

}
}
}
