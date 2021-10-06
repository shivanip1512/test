#pragma once

#include "dlldefs.h"

// jmoc
namespace cms
{
class Destination;
}



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


    ///  jmoc  -- stub this junk out until i can figure out where it
    ///  is supposed to go.

 //   void setCMSReplyTo( const cms::Destination * ) {    }



};

using StreamableMessagePtr = std::unique_ptr<const StreamableMessage>;

}

