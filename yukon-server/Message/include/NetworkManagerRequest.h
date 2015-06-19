#pragma once

#include <string>

namespace Cti {
namespace Messaging {
namespace Rfn {

struct NetworkManagerBase
{
    virtual ~NetworkManagerBase() = default;
};

struct NetworkManagerCancelRequest : NetworkManagerBase
{
    std::string clientGuid;
    long long sessionId;
    std::set<long long> ids;

    enum class CancelType
    {
        Message,
        Group,
        Invalid
    }
    type;
};

struct NetworkManagerCancelRequestAck : NetworkManagerCancelRequest
{
};

struct NetworkManagerCancelResponse : NetworkManagerBase
{
    enum class MessageStatus
    {
        Success,
        NotFound,
        Invalid
    };

    using MessageStatusPerId = std::map<long long, MessageStatus>;

    std::string clientGuid;
    long long sessionId;
    MessageStatusPerId results;
};

}
}
}
