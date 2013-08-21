#include "precompiled.h"

#include "MsgCapBankStates.h"
#include "utility.h"
#include "ccid.h"

DEFINE_COLLECTABLE( CtiCCCapBankStatesMsg, CTICCCAPBANKSTATES_MSG_ID )

/*---------------------------------------------------------------------------
    Constuctors
---------------------------------------------------------------------------*/
CtiCCCapBankStatesMsg::CtiCCCapBankStatesMsg(CtiCCState_vec& ccCapBankStates) : Inherited(), _ccCapBankStates(NULL)
{
    _ccCapBankStates = new CtiCCState_vec;
    int y = ccCapBankStates.size();
    for(int i=0;i<y;i++)
    {
        _ccCapBankStates->push_back(((CtiCCState*)ccCapBankStates.at(i))->replicate());
    }
}

CtiCCCapBankStatesMsg::CtiCCCapBankStatesMsg(const CtiCCCapBankStatesMsg& ccCapBankStatesMsg) : Inherited(), _ccCapBankStates(NULL)
{
    operator=(ccCapBankStatesMsg);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCCapBankStatesMsg::~CtiCCCapBankStatesMsg()
{
    if( _ccCapBankStates != NULL &&
            _ccCapBankStates->size() > 0 )
        {
            delete_container(*_ccCapBankStates);
            _ccCapBankStates->clear();
            delete _ccCapBankStates;
        }

}

CtiMessage* CtiCCCapBankStatesMsg::replicateMessage() const
{
    return new CtiCCCapBankStatesMsg(*this);

}

CtiCCCapBankStatesMsg& CtiCCCapBankStatesMsg::operator=(const CtiCCCapBankStatesMsg& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);

        if( _ccCapBankStates != NULL &&
            _ccCapBankStates->size() > 0 )
        {
            delete_container(*_ccCapBankStates);
            _ccCapBankStates->clear();
            delete _ccCapBankStates;
        }

        int y = (right.getCCCapBankStates())->size();

        if ( _ccCapBankStates == NULL )
            _ccCapBankStates = new CtiCCState_vec;

        for(int i=0;i < y;i++)
        {
            _ccCapBankStates->push_back(((CtiCCState*)(*right.getCCCapBankStates()).at(i))->replicate());
        }
    }

    return *this;
}
