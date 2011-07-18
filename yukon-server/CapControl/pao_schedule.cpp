

/*-----------------------------------------------------------------------------
    Filename:  pao_schedule.cpp

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

using std::endl;

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiPAOSchedule::CtiPAOSchedule() :
_dirty(false),
_scheduleId(0),
_intervalRate(0),
_disabledFlag(false)
{
}

CtiPAOSchedule::CtiPAOSchedule(Cti::RowReader& rdr)
{
    string tempBoolString;

    rdr["scheduleid"] >> _scheduleId;
    rdr["schedulename"] >> _scheduleName;
    rdr["nextruntime"] >> _nextRunTime;
    rdr["lastruntime"] >> _lastRunTime;
    rdr["intervalrate"] >> _intervalRate;
    rdr["disabled"] >> tempBoolString;
    _disabledFlag = (((tempBoolString=="y") || (tempBoolString=="Y"))?TRUE:FALSE);

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
    _disabledFlag = right._disabledFlag;

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


long CtiPAOSchedule::getScheduleId() const
{
    return _scheduleId;
}
const string& CtiPAOSchedule::getScheduleName()  const
{
    return _scheduleName;
}
const CtiTime& CtiPAOSchedule::getNextRunTime()     const
{
    return _nextRunTime;
}
const CtiTime& CtiPAOSchedule::getLastRunTime() const
{
    return _lastRunTime;
}
long CtiPAOSchedule::getIntervalRate()    const
{
    return _intervalRate;
}

bool CtiPAOSchedule::isDisabled()
{
    return _disabledFlag;
}

void CtiPAOSchedule::setScheduleId( long schedId )
{
    if (_scheduleId != schedId)
    {
        _dirty = true;
    }
    _scheduleId = schedId;
    return;
}
void CtiPAOSchedule::setScheduleName(const string& schedName)
{
    if (_scheduleName != schedName)
    {
        _dirty = true;
    }
    _scheduleName = schedName;
    return;
}
void CtiPAOSchedule::setNextRunTime(const CtiTime& nextTime)
{
    if (_nextRunTime != nextTime)
    {
        _dirty = true;
    }
    _nextRunTime = nextTime;
    return;
}
void CtiPAOSchedule::setLastRunTime(const CtiTime& lastTime)
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
void CtiPAOSchedule::setDisabledFlag(bool disabledFlag)
{
    if (_disabledFlag != disabledFlag)
    {
        _dirty = true;
    }
    _disabledFlag = disabledFlag;
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
    dout << CtiTime() << " " <<_scheduleName<<" id: "<<_scheduleId<<" nextRunTime: "<<CtiTime(_nextRunTime.seconds())<<" intvlRate: "<<_intervalRate<<" secs."<< endl;
}
