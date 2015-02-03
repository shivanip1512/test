#pragma once

#include "dbmemobject.h"
#include "ctitime.h"

namespace Cti
{
class RowReader;
}


class CtiPAOSchedule  : public CtiMemDBObject
{
public:

    CtiPAOSchedule();
    virtual ~CtiPAOSchedule();
    CtiPAOSchedule(Cti::RowReader& rdr);

    long getScheduleId() const;
    const std::string& getScheduleName() const;
    const CtiTime& getNextRunTime() const;
    const CtiTime& getLastRunTime() const;
    long getIntervalRate() const;
    bool isDisabled();

    void setScheduleId(long schedId);
    void setScheduleName(const std::string& schedName);
    void setNextRunTime(const CtiTime& nextTime);
    void setLastRunTime(const CtiTime& lastTime);
    void setIntervalRate(long intervalRate);
    void setDisabledFlag(bool disabledFlag);

    void printSchedule();

    CtiPAOSchedule& operator=(const CtiPAOSchedule& right);
    int operator==(const CtiPAOSchedule& right) const;
    int operator!=(const CtiPAOSchedule& right) const;

    bool isDirty();
    void setDirty(bool flag);

private:
    
    bool _dirty;

    long         _scheduleId;
    std::string  _scheduleName;
    CtiTime      _nextRunTime;
    CtiTime      _lastRunTime;
    long         _intervalRate; //in seconds!
    bool         _disabledFlag;  
};
