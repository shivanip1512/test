#include "yukon.h"

#include "cmd_lcr3102_lastMessageReceived.h"

#include "dsm2err.h"

namespace Cti {
namespace Devices {
namespace Commands {

DlcCommand::request_ptr Lcr3102LastMessageReceivedCommand::execute(const CtiTime now)
{
    return request_ptr(new read_request_t(Read_LastMessageReceived, ReadLength_LastMessageReceived));
}

DlcCommand::request_ptr Lcr3102LastMessageReceivedCommand::decode(const CtiTime now, const unsigned function, const payload_t &payload, std::string &description, std::vector<point_data> &points)
{
    const int messageLength = getValueFromBits(payload, 0, 8);

    std::vector<unsigned> lastMessage = getValueVectorFromBits(payload, 8, 8, messageLength);

    if(lastMessage.empty())
    {
        description = "Invalid payload received (" + CtiNumStr(messageLength).xhex(2) + ")";
        throw CommandException(NOTNORMAL, description);
    }

    description = "Last message received: 0x";

    for each( const unsigned &val in lastMessage )
    {
        description += CtiNumStr(val).hex(2);
    }

    description += "\n";

    return request_ptr();
}

}
}
}

