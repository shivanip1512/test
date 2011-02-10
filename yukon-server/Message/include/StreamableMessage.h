#pragma once

#include "dlldefs.h"

namespace cms {
    class StreamMessage;
}

namespace Cti {
namespace Messaging {

struct IM_EX_MSG StreamableMessage
{
    virtual ~StreamableMessage() { };

    virtual void streamInto(cms::StreamMessage &message) const = 0;
};

}
}

