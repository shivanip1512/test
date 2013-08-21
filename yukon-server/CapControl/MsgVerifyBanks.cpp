#include "precompiled.h"

#include "MsgVerifyBanks.h"
#include "ccid.h"

DEFINE_COLLECTABLE( VerifyBanks, CTICCSUBVERIFICATIONMSG_ID )

VerifyBanks::VerifyBanks() : Inherited()
{

}

VerifyBanks::VerifyBanks(int itemId, bool disableOvUv, int commandId) :
    Inherited(commandId,itemId),
    _disableOvUv(disableOvUv)
{

}


VerifyBanks::VerifyBanks(const VerifyBanks& msg)
{
    operator=(msg);
}

VerifyBanks::~VerifyBanks()
{

}

bool VerifyBanks::getDisableOvUv() const
{
    return _disableOvUv;
}

void VerifyBanks::setDisableOvUv(bool disableOvUv)
{
    _disableOvUv = disableOvUv;
}

VerifyBanks& VerifyBanks::operator=(const VerifyBanks& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);
        _disableOvUv = right._disableOvUv;
    }

    return *this;
}

CtiMessage* VerifyBanks::replicateMessage() const
{
    return new VerifyBanks(*this);
}
