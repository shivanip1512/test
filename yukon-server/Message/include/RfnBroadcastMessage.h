#pragma once

#include <StreamableMessage.h>

#include "dlldefs.h"
#include "ctitime.h"

namespace Cti {
namespace Messaging {
namespace Rfn {

struct IM_EX_MSG RfnBroadcastMessage : StreamableMessage
{
private:

    RfnBroadcastMessage();

public:

    short             messageId;
    int               messagePriority;
    std::string       rfnMessageClass;
    unsigned int      expirationDuration;
    std::vector<unsigned char> payload;

    // Helper method to fill out the standard fields.
    static std::unique_ptr<const RfnBroadcastMessage>
            createMessage(int messagePriority,
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
