#pragma once

#include "dlldefs.h"

namespace cms {
    class StreamMessage;
}

namespace Cti {
namespace Messaging {

struct IM_EX_MSG StreamableMessage
{
    typedef std::unique_ptr<const StreamableMessage> auto_type;

    virtual ~StreamableMessage() { };

    virtual void streamInto(cms::StreamMessage &message) const = 0;
};

}
}

