#include "precompiled.h"

#include "pao_schedule.h"
#include "row_reader.h"
#include "ccutil.h"

using std::string;
using Cti::CapControl::setVariableIfDifferent;
using Cti::CapControl::deserializeFlag;

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
    rdr["scheduleid"] >> _scheduleId;
    rdr["schedulename"] >> _scheduleName;
    rdr["nextruntime"] >> _nextRunTime;
    rdr["lastruntime"] >> _lastRunTime;
    rdr["intervalrate"] >> _intervalRate;

    _disabledFlag = deserializeFlag( rdr[ "disabled" ].as<std::string>() );

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
    _dirty |= setVariableIfDifferent(_scheduleId, schedId);
}
void CtiPAOSchedule::setScheduleName(const string& schedName)
{
    _dirty |= setVariableIfDifferent(_scheduleName, schedName);
}
void CtiPAOSchedule::setNextRunTime(const CtiTime& nextTime)
{
    _dirty |= setVariableIfDifferent(_nextRunTime, nextTime);
}
void CtiPAOSchedule::setLastRunTime(const CtiTime& lastTime)
{
    _dirty |= setVariableIfDifferent(_lastRunTime, lastTime);
}
void CtiPAOSchedule::setIntervalRate(long intervalRate)
{
    _dirty |= setVariableIfDifferent(_intervalRate, intervalRate);
}
void CtiPAOSchedule::setDisabledFlag(bool disabledFlag)
{
    _dirty |= setVariableIfDifferent(_disabledFlag, disabledFlag);
}

bool CtiPAOSchedule::isDirty()
{
    return _dirty;
}
void CtiPAOSchedule::setDirty(bool flag)
{
    _dirty = flag;
}

void CtiPAOSchedule::printSchedule()
{
    CTILOG_INFO(dout, "" <<_scheduleName<<" id: "<<_scheduleId<<" nextRunTime: "<<CtiTime(_nextRunTime.seconds())<<" intvlRate: "<<_intervalRate<<" secs.");
}
