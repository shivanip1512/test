

#include "precompiled.h"

#include "RfnBroadcastMessage.h"

#include "msg_pcreturn.h"

#include <cms/StreamMessage.h>

using namespace std;

namespace Cti {
namespace Messaging {
namespace Rfn {

static unsigned int MessageIdCounter = 0;
static volatile LONG GlobalMessageId = std::time(0);

const string RfnBroadcastMessage::RfnMessageClass::DemandResponse= "DR";
const string RfnBroadcastMessage::RfnMessageClass::none = "NONE";

RfnBroadcastMessage::RfnBroadcastMessage() :
_messageId(0),
_messagePriority(0)
{}

void RfnBroadcastMessage::streamInto(cms::StreamMessage &message) const
{
    __int64 milliseconds = (__int64)_expirationDuration * 1000;

    message.writeShort  (_messageId);
    message.writeInt    (_messagePriority);
    message.writeString (_rfnMessageClass);
    message.writeLong   (milliseconds);
    message.writeInt    (_payload.size());
    message.writeBytes  (_payload);
}

RfnBroadcastMessage* RfnBroadcastMessage::createMessage( int messagePriority,
                                                         const string &rfnMessageClass,
                                                         unsigned int expirationDuration,
                                                         const std::vector<unsigned char> &payload )
{
    RfnBroadcastMessage *retVal= new RfnBroadcastMessage();

    // This value is global and shared amongst all RFNBroadcastMessage's. If it needs to be shared with other message types in the future
    // this code will not be sufficient.
    retVal->_messageId = (short) InterlockedIncrement(&GlobalMessageId);

    retVal->_messagePriority = messagePriority;
    retVal->_rfnMessageClass = rfnMessageClass;
    retVal->_expirationDuration = expirationDuration;
    retVal->_payload = payload;

    return retVal;
}

}
}
}
