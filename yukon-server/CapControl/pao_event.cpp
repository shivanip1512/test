/*-----------------------------------------------------------------------------
    Filename:  pao_event.cpp

    Programmer:  Julie Richter

    Description:    

    Initial Date:  1/27/2005

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2005
-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "dbaccess.h"
#include "connection.h"
#include "msg_dbchg.h"
#include "capcontroller.h"
#include "pao_schedule.h"
#include <rw/thr/prodcons.h>

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiPAOEvent::CtiPAOEvent()
{
}


CtiPAOEvent::CtiPAOEvent(long eventId, long schedId, long paoId, RWCString command)
{
    _eventId = eventId;
    _scheduleId = schedId;
    _paoId = paoId;
    _eventCommand = command;
}


CtiPAOEvent::CtiPAOEvent(RWDBReader& rdr)
{
    rdr["eventid"] >> _eventId;
    rdr["scheduleid"] >> _scheduleId;
    rdr["paoid"] >> _paoId;
    rdr["command"] >> _eventCommand;

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

long CtiPAOEvent::getEventId()
{
    return _eventId;
}

long CtiPAOEvent::getScheduleId()
{
    return _scheduleId;
}

long CtiPAOEvent::getPAOId()
{
    return _paoId;
}

RWCString CtiPAOEvent::getEventCommand()
{
    return _eventCommand;
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
void CtiPAOEvent::setEventCommand(RWCString eventCommand)
{
    if (_eventCommand != eventCommand)
    {
        _dirty = true;
    }
    _eventCommand = eventCommand;
    return;
}

BOOL CtiPAOEvent::isDirty()
{
    return _dirty;
}
void CtiPAOEvent::setDirty(BOOL flag)
{
    _dirty = flag;
}
