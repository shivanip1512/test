#include "precompiled.h"

#include "MsgSystemStatus.h"
#include "ccid.h"

DEFINE_COLLECTABLE( SystemStatus, SYSTEM_STATUS_MSG_ID )

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

CtiMessage* SystemStatus::replicateMessage() const
{
    return new SystemStatus(*this);
}
