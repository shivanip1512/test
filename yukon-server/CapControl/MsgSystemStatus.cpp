#include "precompiled.h"

#include "MsgSystemStatus.h"
#include "ccid.h"

RWDEFINE_COLLECTABLE( SystemStatus, SYSTEM_STATUS_MSG_ID )

SystemStatus::SystemStatus(bool state)
{
    _systemState = state;
}

SystemStatus::~SystemStatus()
{

}

SystemStatus& SystemStatus::operator=(const SystemStatus& right)
{

    if( this != &right )
    {
        Inherited::operator=(right);
    }

    return *this;
}

void SystemStatus::restoreGuts(RWvistream& strm)
{
    Inherited::restoreGuts(strm);

    strm >> _systemState;

    return;
}

void SystemStatus::saveGuts(RWvostream& strm) const
{
    Inherited::saveGuts(strm);

    strm << _systemState;

    return;
}

CtiMessage* SystemStatus::replicateMessage() const
{
    return new SystemStatus(*this);
}
