#pragma once

#include "dlldefs.h"
#include "connectionHandle.h"

#include <StreamableMessage.h>

class CtiReturnMsg;

namespace Cti {
namespace Messaging {

class IM_EX_MSG PorterResponseMessage : public StreamableMessage
{
    long _connectionId;
    long _deviceId;
    long _userMessageId;
    int  _status;
    bool _final;

public:

    PorterResponseMessage(const CtiReturnMsg &msg, const ConnectionHandle connectionHandle);

    void streamInto(cms::StreamMessage &message) const;
};


}
}

