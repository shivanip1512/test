#pragma once

#include "MsgCapControlMessage.h"
#include "ccstate.h"

class CtiCCCapBankStatesMsg : public CapControlMessage
{
    private:
        typedef CapControlMessage Inherited;
        RWDECLARE_COLLECTABLE( CtiCCCapBankStatesMsg )

    public:
        CtiCCCapBankStatesMsg(CtiCCState_vec& ccCapBankStates);
        CtiCCCapBankStatesMsg(const CtiCCCapBankStatesMsg& ccCapBankStatesMsg);

        virtual ~CtiCCCapBankStatesMsg();

        CtiCCState_vec* getCCCapBankStates() const     { return _ccCapBankStates; }

        virtual CtiMessage* replicateMessage() const;

        void restoreGuts( RWvistream& iStream);
        void saveGuts( RWvostream& oStream) const;

        CtiCCCapBankStatesMsg& operator=(const CtiCCCapBankStatesMsg& right);

    private:
        CtiCCCapBankStatesMsg() : Inherited(), _ccCapBankStates(NULL){};

        CtiCCState_vec* _ccCapBankStates;
};

