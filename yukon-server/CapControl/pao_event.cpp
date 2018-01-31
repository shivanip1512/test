#include "precompiled.h"

#include "pao_event.h"
#include "row_reader.h"
#include "ccutil.h"

using Cti::CapControl::deserializeFlag;


/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiPAOEvent::CtiPAOEvent() :
_eventId(0),
_scheduleId(0),
_paoId(0),
_disableOvUvFlag(false)
{
}

CtiPAOEvent::CtiPAOEvent(Cti::RowReader& rdr)
{
    rdr["eventid"] >> _eventId;
    rdr["scheduleid"] >> _scheduleId;
    rdr["paoid"] >> _paoId;
    rdr["command"] >> _eventCommand;

    _disableOvUvFlag = deserializeFlag( rdr[ "disableovuv" ].as<std::string>() );
}
/*---------------------------------------------------------------------------
    Destructor

---------------------------------------------------------------------------*/
CtiPAOEvent::~CtiPAOEvent()
{

}

long CtiPAOEvent::getEventId() const
{
    return _eventId;
}

long CtiPAOEvent::getScheduleId()  const
{
    return _scheduleId;
}

long CtiPAOEvent::getPAOId() const
{
    return _paoId;
}

const std::string& CtiPAOEvent::getEventCommand() const
{
    return _eventCommand;
}

bool CtiPAOEvent::getDisableOvUvFlag() const
{
    return _disableOvUvFlag;
}

