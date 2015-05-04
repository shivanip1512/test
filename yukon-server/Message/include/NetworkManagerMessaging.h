#pragma once

#include <string>

namespace Cti {
namespace Messaging {
namespace Rfn {

struct NetworkManagerRequestHeader
{
    std::string clientGuid;
    long long sessionId;
    long long groupId;
    long long messageId;
    long long expiration;
    char priority : 7;
};

struct NetworkManagerRequestAck
{
    NetworkManagerRequestHeader header;
};

}
}
}
