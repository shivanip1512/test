

#include "precompiled.h"

#include "MsgVerifySelectedBank.h"
#include "ccid.h"

DEFINE_COLLECTABLE( VerifySelectedBank, VERIFY_SELECTED_BANK_MSG_ID )

VerifySelectedBank::VerifySelectedBank() :
    Inherited()
{

}

VerifySelectedBank::VerifySelectedBank(int itemId, int bankId, bool disableOvUv) :
    Inherited(itemId,disableOvUv,CapControlCommand::VERIFY_SELECTED_BANK),
    _bankId(bankId)
{

}

VerifySelectedBank::VerifySelectedBank(const VerifySelectedBank& msg)
{
    operator=(msg);
}

VerifySelectedBank::~VerifySelectedBank()
{

}

long VerifySelectedBank::getBankId() const
{
    return _bankId;
}

void VerifySelectedBank::setBankId(long bankId)
{
    _bankId = bankId;
}

VerifySelectedBank& VerifySelectedBank::operator=(const VerifySelectedBank& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);
        _bankId = right._bankId;
    }

    return *this;
}

CtiMessage* VerifySelectedBank::replicateMessage() const
{
    return new VerifySelectedBank(*this);
}
