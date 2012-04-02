/*-----------------------------------------------------------------------------
    Filename:  pao_event.cpp

    Programmer:  Julie Richter

    Description:

    Initial Date:  1/27/2005

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2005
-----------------------------------------------------------------------------*/

#include "precompiled.h"
#include "dbaccess.h"
#include "connection.h"
#include "msg_dbchg.h"
#include "capcontroller.h"
#include "pao_schedule.h"

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiPAOEvent::CtiPAOEvent() :
_dirty(false),
_eventId(0),
_scheduleId(0),
_paoId(0),
_disableOvUvFlag(false)
{
}


CtiPAOEvent::CtiPAOEvent(long eventId, long schedId, long paoId, const string& command, bool disableOvUv) :
_dirty(false)
{
    _eventId = eventId;
    _scheduleId = schedId;
    _paoId = paoId;
    _eventCommand = command;
    _disableOvUvFlag = disableOvUv;
}


CtiPAOEvent::CtiPAOEvent(Cti::RowReader& rdr)
{
    string tempBoolString;

    rdr["eventid"] >> _eventId;
    rdr["scheduleid"] >> _scheduleId;
    rdr["paoid"] >> _paoId;
    rdr["command"] >> _eventCommand;
    rdr["disableovuv"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setDisableOvUvFlag(tempBoolString=="y");


    _dirty = false;
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

    _dirty = right._dirty;

    return *this;
}
int CtiPAOEvent::operator==(const CtiPAOEvent& right) const
{
    return _eventId == right._eventId;
}
int CtiPAOEvent::operator!=(const CtiPAOEvent& right) const
{
    return _eventId != right._eventId;
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

const string& CtiPAOEvent::getEventCommand() const
{
    return _eventCommand;
}

bool CtiPAOEvent::getDisableOvUvFlag() const
{
    return _disableOvUvFlag;
}

void CtiPAOEvent::setEventId(long eventId)
{
    if (_eventId != eventId)
    {
        _dirty = true;
    }
    _eventId = eventId;
    return;
}
void CtiPAOEvent::setScheduleId(long schedId)
{
    if (_scheduleId != schedId)
    {
        _dirty = true;
    }
    _scheduleId = schedId;
    return;
}
void CtiPAOEvent::setPAOId(long paoId)
{
    if (_paoId != paoId)
    {
        _dirty = true;
    }
    _paoId = paoId;
    return;
}
void CtiPAOEvent::setEventCommand(const string& eventCommand)
{
    if (_eventCommand != eventCommand)
    {
        _dirty = true;
    }
    _eventCommand = eventCommand;
    return;
}
void CtiPAOEvent::setDisableOvUvFlag(bool flag)
{
    if (_disableOvUvFlag != flag)
    {
        _dirty = true;
    }
    _disableOvUvFlag = flag;
    return;
}

bool CtiPAOEvent::isDirty()
{
    return _dirty;
}
void CtiPAOEvent::setDirty(bool flag)
{
    _dirty = flag;
}
