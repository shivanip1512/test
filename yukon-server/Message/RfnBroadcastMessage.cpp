#include "precompiled.h"

#include "RfnBroadcastMessage.h"

#include "msg_pcreturn.h"

#include <cms/StreamMessage.h>

#include <ctime>

using namespace std;

namespace Cti {
namespace Messaging {
namespace Rfn {

static unsigned int MessageIdCounter = 0;
static volatile LONG GlobalMessageId = std::time(0);

const string RfnBroadcastMessage::RfnMessageClass::DemandResponse= "DR";
const string RfnBroadcastMessage::RfnMessageClass::none = "NONE";

RfnBroadcastMessage::RfnBroadcastMessage() :
messageId(0),
messagePriority(0)
{}

void RfnBroadcastMessage::streamInto(cms::StreamMessage &message) const
{
    __int64 milliseconds = (__int64)expirationDuration * 1000;

    message.writeShort  (messageId);
    message.writeInt    (messagePriority);
    message.writeString (rfnMessageClass);
    message.writeLong   (milliseconds);
    message.writeInt    (payload.size());
    message.writeBytes  (payload);
}

std::auto_ptr<const RfnBroadcastMessage> RfnBroadcastMessage::createMessage( int messagePriority,
                                                                             const string &rfnMessageClass,
                                                                             unsigned int expirationDuration,
                                                                             const std::vector<unsigned char> &payload )
{
    std::auto_ptr<RfnBroadcastMessage> retVal(new RfnBroadcastMessage());

    // This value is global and shared amongst all RFNBroadcastMessage's. If it needs to be shared with other message types in the future
    // this code will not be sufficient.
    retVal->messageId = (short) InterlockedIncrement(&GlobalMessageId);

    retVal->messagePriority = messagePriority;
    retVal->rfnMessageClass = rfnMessageClass;
    retVal->expirationDuration = expirationDuration;
    retVal->payload = payload;

    return retVal;
}

}
}
}
