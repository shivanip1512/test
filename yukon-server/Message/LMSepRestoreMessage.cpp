
#include "yukon.h"

#include "LMSepRestoreMessage.h"

#include "msg_pcreturn.h"

#include <cms/StreamMessage.h>

namespace Cti {
namespace Messaging {
namespace LoadManagement {

LMSepRestoreMessage::LMSepRestoreMessage(int groupId,
                                         unsigned int restoreTime,
                                         unsigned char eventFlags) :
_groupId(groupId),
_utcRestoreTime(restoreTime),
_eventFlags(eventFlags)
{
}

void LMSepRestoreMessage::streamInto(cms::StreamMessage &message) const
{
    message.writeInt(_groupId);
    message.writeInt(_utcRestoreTime);
    message.writeByte(_eventFlags);
}


}
}
}
