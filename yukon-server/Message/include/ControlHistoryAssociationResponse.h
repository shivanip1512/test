
#pragma once

#include <StreamableMessage.h>

#include "dlldefs.h"

namespace Cti {
namespace Messaging {

class IM_EX_MSG ControlHistoryAssociationResponse : public StreamableMessage
{
private:
    int _historyRowId;
    int _associationId;

public:

    ControlHistoryAssociationResponse(int historyRowId,
                                      int associationId);

    void streamInto(cms::StreamMessage &message) const;
};


}
}

