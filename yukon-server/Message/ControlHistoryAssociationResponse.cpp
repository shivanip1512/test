#include "precompiled.h"

#include "ControlHistoryAssociationResponse.h"

#include "proton_encoder_proxy.h"

namespace Cti {
namespace Messaging {

ControlHistoryAssociationResponse::ControlHistoryAssociationResponse(int historyRowId, int associationId) :
_historyRowId(historyRowId),
_associationId(associationId)
{
}

void ControlHistoryAssociationResponse::streamInto(Proton::EncoderProxy &message) const
{
    message.writeInt(_historyRowId);
    message.writeInt(_associationId);
}

}
}

