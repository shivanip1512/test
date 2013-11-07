#pragma once

#include "dlldefs.h"

#include <boost/cstdint.hpp>

namespace Cti {
namespace Messaging {
namespace Rfn {

class IM_EX_MSG BroadcastResult
{
    BroadcastResult(std::string desc) : description(desc) {}

public:
    const std::string description;

    static const BroadcastResult Success;
    static const BroadcastResult Failure;
    static const BroadcastResult NetworkTimeout;
    static const BroadcastResult Timeout;
};

struct RfnBroadcastReplyMessage
{
    std::map<boost::int64_t, const BroadcastResult *> gatewayResults;
};

}
}
}
