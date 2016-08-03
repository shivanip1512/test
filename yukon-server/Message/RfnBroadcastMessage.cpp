#include "precompiled.h"

#include "RfnBroadcastMessage.h"

#include "msg_pcreturn.h"

#include <cms/StreamMessage.h>

#include <ctime>
#include <atomic>

using namespace std;

namespace Cti {
namespace Messaging {
namespace Rfn {

static unsigned int MessageIdCounter = 0;
static std::atomic<long> GlobalMessageId = std::time(0);

const string RfnBroadcastMessage::RfnMessageClass::DemandResponse= "DR";
const string RfnBroadcastMessage::RfnMessageClass::none = "NONE";

RfnBroadcastMessage::RfnBroadcastMessage() :
messageId(0),
messagePriority(0)
{}

void RfnBroadcastMessage::streamInto(cms::StreamMessage &message) const
{
    long long milliseconds = (long long)expirationDuration * 1000;

    message.writeShort  (messageId);
    message.writeInt    (messagePriority);
    message.writeString (rfnMessageClass);
    message.writeLong   (milliseconds);
    message.writeInt    (payload.size());
    message.writeBytes  (payload);
}

std::unique_ptr<const RfnBroadcastMessage> RfnBroadcastMessage::createMessage( int messagePriority,
                                                                               const string &rfnMessageClass,
                                                                               unsigned int expirationDuration,
                                                                               const std::vector<unsigned char> &payload )
{
    std::unique_ptr<RfnBroadcastMessage> retVal { new RfnBroadcastMessage() };

    // This value is global and shared amongst all RFNBroadcastMessage's. If it needs to be shared with other message types in the future
    // this code will not be sufficient. Currently Java uses all negative values of short and C++ uses all positive values of short
    retVal->messageId = ++GlobalMessageId & 0x7FFF;

    retVal->messagePriority = messagePriority;
    retVal->rfnMessageClass = rfnMessageClass;
    retVal->expirationDuration = expirationDuration;
    retVal->payload = payload;

    return std::move(retVal);  //  move construct a unique_ptr holding a _const_ RfnBroadcastMessage
}

}
}
}
