#include "yukon.h"

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

DlcCommand::request_ptr Lcr3102ThreePartCommand::execute(const CtiTime now)
{
    return makeRequest(now);
}

DlcCommand::request_ptr Lcr3102ThreePartCommand::makeRequest(const CtiTime now)
{
    if( _state == State_ExpresscomWrite )
    {
        return request_ptr(new read_request_t(Read_ExpresscomMsgSend, 0));
    }
    else
    {
        return request_ptr(new read_request_t(Read_ActOnStoredMessage, _length));
    }
}

DlcCommand::request_ptr Lcr3102ThreePartCommand::decode(CtiTime now, const unsigned function, const payload_t &payload, std::string &description, std::vector<point_data> &points)
{
    if( _state == State_Reading )
    {
        // This was the decode from the true ActOnStoredMessage call.
        return decodeReading(now, function, payload, description, points);
    }
    else
    {
        // This was the decode call after the initial expresscom write, we need to call the next execute and change state!
        _state = State_Reading;
        return makeRequest(now);
    }
}

DlcCommand::request_ptr Lcr3102ThreePartCommand::error(const CtiTime now, const int error_code, std::string &description)
{
    if( description.empty() )
    {
        description = GetError(error_code);
    }

    description += "\n";

    if( _retries > 0 )
    {
        _retries--;

        description += "Retrying (" + CtiNumStr(_retries) + " remaining)";

        return execute(now);
    }
    else
    {
        throw CommandException(error_code, description + "Retries exhausted");
    }
}

}
}
}
