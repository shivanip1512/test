#pragma once

#include "dlldefs.h"


namespace Cti::Messaging
{

namespace Proton
{
    class EncoderProxy;
}

struct IM_EX_MSG StreamableMessage
{
    virtual ~StreamableMessage() { };

    virtual void streamInto(Proton::EncoderProxy &message) const = 0;
};

using StreamableMessagePtr = std::unique_ptr<const StreamableMessage>;

}

