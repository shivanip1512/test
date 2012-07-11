
#pragma once

#include <StreamableMessage.h>

#include "dlldefs.h"
#include "ctitime.h"

namespace Cti {
namespace Messaging {
namespace Rfn {

class IM_EX_MSG RfnBroadcastMessage : public StreamableMessage
{
private:
    short             _messageId;
    int               _messagePriority;
    std::string       _rfnMessageClass;
    unsigned int      _expirationDuration;
    std::vector<unsigned char> _payload;

    RfnBroadcastMessage();

public:

    // Helper method to fill out the standard fields.
    static RfnBroadcastMessage* createMessage(int messagePriority,
                                              const std::string &rfnMessageClass,
                                              unsigned int expirationDuration,
                                              const std::vector<unsigned char> &payload);

    void streamInto(cms::StreamMessage &message) const;

    struct IM_EX_MSG RfnMessageClass
    {
        static const std::string DemandResponse;
        static const std::string none;
    };
    
};


}
}
}
