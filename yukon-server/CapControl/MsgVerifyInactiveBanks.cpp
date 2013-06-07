
#include "precompiled.h"

#include "MsgVerifyInactiveBanks.h"
#include "ccid.h"

DEFINE_COLLECTABLE( VerifyInactiveBanks, VERIFY_INACTIVE_BANKS_MSG_ID )

VerifyInactiveBanks::VerifyInactiveBanks() :
    Inherited()
{

}

VerifyInactiveBanks::VerifyInactiveBanks(int itemId, long bankInactiveTime, bool disableOvUv) :
    Inherited(itemId,disableOvUv,CapControlCommand::VERIFY_INACTIVE_BANKS),
    _bankInactiveTime(bankInactiveTime)
{

}

VerifyInactiveBanks::VerifyInactiveBanks(const VerifyInactiveBanks& msg)
{
    operator=(msg);
}

VerifyInactiveBanks::~VerifyInactiveBanks()
{

}

long VerifyInactiveBanks::getBankInactiveTime() const
{
    return _bankInactiveTime;
}

void VerifyInactiveBanks::setBankInactiveTime(long bankInactiveTime)
{
    _bankInactiveTime = bankInactiveTime;
}

VerifyInactiveBanks& VerifyInactiveBanks::operator=(const VerifyInactiveBanks& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);
        _bankInactiveTime = right._bankInactiveTime;
    }

    return *this;
}

CtiMessage* VerifyInactiveBanks::replicateMessage() const
{
    return new VerifyInactiveBanks(*this);
}
