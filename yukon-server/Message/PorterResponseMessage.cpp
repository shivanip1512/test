#include "yukon.h"

#include "PorterResponseMessage.h"

#include "msg_pcreturn.h"

#include <cms/StreamMessage.h>

namespace Cti {
namespace Messaging {

PorterResponseMessage::PorterResponseMessage(const CtiReturnMsg &msg, void *connectionHandle) :
    _connectionId(reinterpret_cast<long>(connectionHandle)),
    _deviceId(msg.DeviceId()),
    _final( ! msg.ExpectMore()),
    _status(msg.Status()),
    _userMessageId(msg.UserMessageId())
{
}

void PorterResponseMessage::streamInto(cms::StreamMessage &message) const
{
    message.writeLong(_connectionId);
    message.writeInt(_deviceId);
    message.writeBoolean(_final);
    message.writeInt(_status);
    message.writeInt(_userMessageId);
}

}
}

