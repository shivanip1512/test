#pragma once

#include <StreamableMessage.h>

#include "dlldefs.h"

class CtiReturnMsg;

namespace Cti {
namespace Messaging {

class IM_EX_MSG PorterResponseMessage : public StreamableMessage
{
    long _connectionId;
    long _deviceId;
    long _userMessageId;
    long _status;
    bool _final;

public:

    PorterResponseMessage(const CtiReturnMsg &msg, void *connectionHandle);

    void streamInto(cms::StreamMessage &message) const;
};


}
}

