#pragma once

#include "dlldefs.h"
#include "message_serialization_util.h"

namespace Cti {
namespace Messaging {
namespace Rfn {

struct IM_EX_MSG DataStreamingUpdateMessage
{
    long paoId;
    std::string json;
};

struct IM_EX_MSG DataStreamingUpdateReplyMessage
{
    bool success;
};

}
}
}