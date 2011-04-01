#include "yukon.h"
#include "control_history_association.h"

ControlHistoryTableAssociation::ControlHistoryTableAssociation(const CtiTableLMControlHistory& aRef, int associationId) :
_associationId(associationId), Inherited(aRef)
{
}

int ControlHistoryTableAssociation::getAssociationId()
{
    return _associationId;
}
