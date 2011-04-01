#include "yukon.h"

#include "ControlHistoryAssociationResponse.h"

#include <cms/StreamMessage.h>

namespace Cti {
namespace Messaging {

ControlHistoryAssociationResponse::ControlHistoryAssociationResponse(int historyRowId, int associationId) :
_historyRowId(historyRowId),
_associationId(associationId)
{
}

void ControlHistoryAssociationResponse::streamInto(cms::StreamMessage &message) const
{
    message.writeInt(_historyRowId);
    message.writeInt(_associationId);
}

}
}

