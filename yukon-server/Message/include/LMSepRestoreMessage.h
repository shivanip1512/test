#pragma once

#include <StreamableMessage.h>

#include "dlldefs.h"

namespace Cti {
namespace Messaging {
namespace LoadManagement {

class IM_EX_MSG LMSepRestoreMessage : public StreamableMessage
{
    int             _groupId;
    unsigned int    _utcRestoreTime;
    unsigned char   _eventFlags;

public:

    LMSepRestoreMessage(int groupId, unsigned int restoreTime, unsigned char eventFlags);

    void streamInto(cms::StreamMessage &message) const;
};


}
}
}
