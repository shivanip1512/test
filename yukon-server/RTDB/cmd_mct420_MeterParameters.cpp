#include "precompiled.h"

#include "cmd_mct420_MeterParameters.h"

namespace Cti {
namespace Devices {
namespace Commands {

Mct420MeterParametersCommand::Mct420MeterParametersCommand(const unsigned cycleTime, bool disconnectDisplayDisabled, boost::optional<unsigned> transformerRatio, bool readsOnly) :
    _cycleTime(cycleTime),
    _disconnectDisplayDisabled(disconnectDisplayDisabled),
    _transformerRatio(transformerRatio),
    _executionState(readsOnly ? &Mct420MeterParametersCommand::read : &Mct420MeterParametersCommand::write)
{
}

DlcCommand::request_ptr Mct420MeterParametersCommand::execute(const CtiTime now)
{
    // Make sure everything jives.
    validateParameters();

    return doCommand();
}

//  throws CommandException
void Mct420MeterParametersCommand::validateParameters()
{
    if( _executionState == &Mct420MeterParametersCommand::write )
    {
        if( !_cycleTime || _cycleTime > 15 )
        {
            throw CommandException(BADPARAM, "Invalid LCD cycle time (" + CtiNumStr(_cycleTime) + "), must be 1-15");
        }

        if( _transformerRatio && (!*_transformerRatio || *_transformerRatio > 255) )
        {
            throw CommandException(BADPARAM, "Invalid transformer ratio (" + CtiNumStr(*_transformerRatio) + "), must be 1-255");
        }
    }
}

DlcCommand::request_ptr Mct420MeterParametersCommand::decode(const CtiTime now, const unsigned function, const payload_t &payload, std::string &description, std::vector<point_data> &points)
{
    // Nothing to decode for this message.
    return doCommand();
}

//  throws CommandException
DlcCommand::request_ptr Mct420MeterParametersCommand::error(const CtiTime now, const int error_code, std::string &description)
{
    //  This should probably be the default for all commands unless otherwise specified.
    throw CommandException(error_code, GetError(error_code));
}

DlcCommand::request_ptr Mct420MeterParametersCommand::doCommand()
{
    //  call the current state's member function
    return _executionState(this);
}

unsigned char Mct420MeterParametersCommand::getDisplayParametersByte() 
{
    unsigned char displayParameters = _cycleTime;

    if( _disconnectDisplayDisabled )
    {
        // The disconnect display is disabled if this bit is set.
        displayParameters |= 0x80;
    }

    return displayParameters;
}

DlcCommand::request_ptr Mct420MeterParametersCommand::write()
{
    // Byte 0 - SPID
    std::vector<unsigned char> payload(1, gMCT400SeriesSPID);

    // Byte 1 - Display Parameters
    payload.push_back(getDisplayParametersByte());

    if( _transformerRatio )
    {
        payload.push_back(*_transformerRatio);
    }

    _executionState = &Mct420MeterParametersCommand::read;

    return request_ptr(new write_request_t(Write_MeterParameters, payload));
}

DlcCommand::request_ptr Mct420MeterParametersCommand::read()
{
    _executionState = &Mct420MeterParametersCommand::done;

    return request_ptr(new read_request_t(Read_MeterParameters, 2));
}

DlcCommand::request_ptr Mct420MeterParametersCommand::done()
{
    return request_ptr();
}

}
}
}
