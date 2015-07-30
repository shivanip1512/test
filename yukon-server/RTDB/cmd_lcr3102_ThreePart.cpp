#include "precompiled.h"

#include "cmd_lcr3102_ThreePart.h"

#include "dsm2err.h"

namespace Cti {
namespace Devices {
namespace Commands {

Lcr3102ThreePartCommand::Lcr3102ThreePartCommand(unsigned length, unsigned retries) :
    _state(State_ExpresscomWrite),
    _length(length),
    _retries(retries)
{
}

DlcCommand::request_ptr Lcr3102ThreePartCommand::executeCommand(const CtiTime now)
{
    return makeRequest(now);
}

DlcCommand::request_ptr Lcr3102ThreePartCommand::makeRequest(const CtiTime now)
{
    if( _state == State_ExpresscomWrite )
    {
        return std::make_unique<read_request_t>(Read_ExpresscomMsgSend, 0);
    }
    else
    {
        return std::make_unique<read_request_t>(Read_ActOnStoredMessage, _length);
    }
}

DlcCommand::request_ptr Lcr3102ThreePartCommand::decodeCommand(CtiTime now, const unsigned function, const boost::optional<Bytes> &payload, std::string &description, std::vector<point_data> &points)
{
    if( _state == State_Reading )
    {
        // This was the decode from the true ActOnStoredMessage call.
        return decodeReading(now, function, *payload, description, points);
    }
    else
    {
        // This was the decode call after the initial expresscom write, we need to call the next execute and change state!
        _state = State_Reading;
        return makeRequest(now);
    }
}

DlcCommand::request_ptr Lcr3102ThreePartCommand::error(const CtiTime now, const YukonError_t error_code, std::string &description)
{
    if( description.empty() )
    {
        description = GetErrorString(error_code);
    }

    description += "\n";

    if( _retries > 0 )
    {
        _retries--;

        description += "Retrying (" + CtiNumStr(_retries) + " remaining)";

        return executeCommand(now);
    }
    else
    {
        throw CommandException(error_code, description + "Retries exhausted");
    }
}

}
}
}
