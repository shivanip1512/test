
#include "precompiled.h"

#include "MsgVerifyInactiveBanks.h"
#include "ccid.h"

RWDEFINE_COLLECTABLE( VerifyInactiveBanks, VERIFY_INACTIVE_BANKS_MSG_ID )

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

long VerifyInactiveBanks::getBankInactiveTime()
{
    return _bankInactiveTime;
}

void VerifyInactiveBanks::setBankInactiveTime(long bankInactiveTime)
{
    _bankInactiveTime = bankInactiveTime;
}

void VerifyInactiveBanks::restoreGuts(RWvistream& iStream)
{
    Inherited::restoreGuts(iStream);

    iStream >> _bankInactiveTime;

    return;
}

void VerifyInactiveBanks::saveGuts(RWvostream& oStream) const
{
    Inherited::saveGuts(oStream);

    oStream << _bankInactiveTime;

    return;
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
