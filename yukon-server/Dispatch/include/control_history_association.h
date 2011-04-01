#pragma once
#include "tbl_lm_controlhist.h"

class ControlHistoryTableAssociation : public CtiTableLMControlHistory
{
private:
    int _associationId;
    typedef CtiTableLMControlHistory Inherited;

public:
    ControlHistoryTableAssociation(const CtiTableLMControlHistory& aRef, int associationId);

    virtual int getAssociationId();
};
