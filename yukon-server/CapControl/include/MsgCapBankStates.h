#pragma once

#include "MsgCapControlMessage.h"
#include "ccstate.h"

class CtiCCCapBankStatesMsg : public CapControlMessage
{
    public:
        DECLARE_COLLECTABLE( CtiCCCapBankStatesMsg )

    private:
        typedef CapControlMessage Inherited;

    public:
        CtiCCCapBankStatesMsg(CtiCCState_vec& ccCapBankStates);
        CtiCCCapBankStatesMsg(const CtiCCCapBankStatesMsg& ccCapBankStatesMsg);

        virtual ~CtiCCCapBankStatesMsg();

        CtiCCState_vec* getCCCapBankStates() const     { return _ccCapBankStates; }

        virtual CtiMessage* replicateMessage() const;

        CtiCCCapBankStatesMsg& operator=(const CtiCCCapBankStatesMsg& right);

    private:
        CtiCCCapBankStatesMsg() : Inherited(), _ccCapBankStates(NULL){};

        CtiCCState_vec* _ccCapBankStates;
};

