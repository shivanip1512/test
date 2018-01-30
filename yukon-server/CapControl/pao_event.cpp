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


CtiPAOEvent::CtiPAOEvent(long eventId, long schedId, long paoId, const std::string& command, bool disableOvUv)
    :   _eventId( eventId ),
        _scheduleId( schedId ),
        _paoId( paoId ),
        _eventCommand( command ),
        _disableOvUvFlag( disableOvUv )
{
    // empty
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
CtiPAOEvent& CtiPAOEvent::operator=(const CtiPAOEvent& right)
{
    _eventId      = right._eventId;
    _scheduleId   = right._scheduleId;
    _paoId        = right._paoId;
    _eventCommand = right._eventCommand;
    _disableOvUvFlag = right._disableOvUvFlag;

    return *this;
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

