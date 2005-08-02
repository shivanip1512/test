

/*-----------------------------------------------------------------------------
    Filename:  pao_schedule.cpp

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
CtiPAOSchedule::CtiPAOSchedule()
{
}

CtiPAOSchedule::CtiPAOSchedule(RWDBReader& rdr)
{                                            
    rdr["scheduleid"] >> _scheduleId;
    rdr["schedulename"] >> _scheduleName;
    rdr["nextruntime"] >> _nextRunTime;
    rdr["lastruntime"] >> _lastRunTime;
    rdr["intervalrate"] >> _intervalRate;

    _dirty = false;

}
/*---------------------------------------------------------------------------
    Destructor

---------------------------------------------------------------------------*/
CtiPAOSchedule::~CtiPAOSchedule()
{
   
}

CtiPAOSchedule& CtiPAOSchedule::operator=(const CtiPAOSchedule& right)
{
    _scheduleId   = right._scheduleId;
    _scheduleName = right._scheduleName;
    _nextRunTime  = right._nextRunTime;
    _lastRunTime  = right._lastRunTime;
    _intervalRate = right._intervalRate;

    _dirty = right._dirty;

    return *this;
}
int CtiPAOSchedule::operator==(const CtiPAOSchedule& right) const
{
    return _scheduleId == right._scheduleId;
}
int CtiPAOSchedule::operator!=(const CtiPAOSchedule& right) const
{
    return _scheduleId != right._scheduleId;
}


long CtiPAOSchedule::getScheduleId()
{
    return _scheduleId;
}
RWCString CtiPAOSchedule::getScheduleName()
{
    return _scheduleName;
}
RWDBDateTime CtiPAOSchedule::getNextRunTime()
{
    return _nextRunTime;
}
RWDBDateTime CtiPAOSchedule::getLastRunTime()
{
    return _lastRunTime;
}
long CtiPAOSchedule::getIntervalRate()
{
    return _intervalRate;
}

void CtiPAOSchedule::setScheduleId(long schedId)
{
    if (_scheduleId != schedId)
    {                  
        _dirty = true;
    }
    _scheduleId = schedId;
    return;
}
void CtiPAOSchedule::setScheduleName(RWCString schedName)
{
    if (_scheduleName != schedName)
    {
        _dirty = true;
    }
    _scheduleName = schedName;
    return;
}
void CtiPAOSchedule::setNextRunTime(RWDBDateTime nextTime)
{
    if (_nextRunTime != nextTime)
    {
        _dirty = true;
    }
    _nextRunTime = nextTime;
    return;
}
void CtiPAOSchedule::setLastRunTime(RWDBDateTime lastTime)
{
    if (_lastRunTime != lastTime)
    {
        _dirty = true;
    }
    _lastRunTime = lastTime;
    return;
}
void CtiPAOSchedule::setIntervalRate(long intervalRate)
{
    if (_intervalRate != intervalRate)
    {
        _dirty = true;
    }
    _intervalRate = intervalRate;
    return;
}

BOOL CtiPAOSchedule::isDirty()
{
    return _dirty;
}
void CtiPAOSchedule::setDirty(BOOL flag)
{
    _dirty = flag;
}

void CtiPAOSchedule::printSchedule()
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << RWTime() << " " <<_scheduleName<<" id: "<<_scheduleId<<" nextRunTime: "<<RWTime(_nextRunTime.seconds())<<" intvlRate: "<<_intervalRate<<" secs."<< endl;
}
