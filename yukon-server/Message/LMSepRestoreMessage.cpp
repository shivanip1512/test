#include "precompiled.h"

#include "LMSepRestoreMessage.h"
#include "msg_pcreturn.h"
#include "proton_encoder_proxy.h"

namespace Cti::Messaging::LoadManagement
{

LMSepRestoreMessage::LMSepRestoreMessage(int groupId,
                                         unsigned int restoreTime,
                                         unsigned char eventFlags) :
_groupId(groupId),
_utcRestoreTime(restoreTime),
_eventFlags(eventFlags)
{
}

void LMSepRestoreMessage::streamInto(Proton::EncoderProxy &message) const
{
    message.writeInt(_groupId);
    message.writeInt(_utcRestoreTime);
    message.writeByte(_eventFlags);
}

}

