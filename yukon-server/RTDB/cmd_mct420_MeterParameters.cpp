#include "precompiled.h"

#include "cmd_mct420_MeterParameters.h"

namespace Cti {
namespace Devices {
namespace Commands {

Mct420MeterParametersCommand::Mct420MeterParametersCommand(const unsigned cycleTime, bool disconnectDisplayDisabled, boost::optional<unsigned> transformerRatio) :
    _cycleTime(cycleTime),
    _disconnectDisplayDisabled(disconnectDisplayDisabled),
    _transformerRatio(transformerRatio),
    _executionState(&Mct420MeterParametersCommand::write)
{
    if( !_cycleTime || _cycleTime > 15 )
    {
        throw CommandException(ClientErrors::BadParameter, "Invalid LCD cycle time (" + CtiNumStr(_cycleTime) + "), must be 1-15");
    }

    if( _transformerRatio && (!*_transformerRatio || *_transformerRatio > 255) )
    {
        throw CommandException(ClientErrors::BadParameter, "Invalid transformer ratio (" + CtiNumStr(*_transformerRatio) + "), must be 1-255");
    }
}

Mct420MeterParametersCommand::Mct420MeterParametersCommand() :
    // Default values here, we're a read anyway, so these values will be ignored.
    _cycleTime(8),
    _disconnectDisplayDisabled(false),
    _transformerRatio(boost::none),
    _executionState(&Mct420MeterParametersCommand::read)
{
}

DlcCommand::request_ptr Mct420MeterParametersCommand::executeCommand(const CtiTime now)
{
    return doCommand();
}

DlcCommand::request_ptr Mct420MeterParametersCommand::decodeCommand(const CtiTime now, const unsigned function, const boost::optional<Bytes> &payload, std::string &description, std::vector<point_data> &points)
{
    // Nothing to decode for this message.
    return doCommand();
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
    std::vector<unsigned char> payload;

    // Byte 0 - SPID
    payload.push_back(gMCT400SeriesSPID);

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
