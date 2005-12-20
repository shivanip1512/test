

/*-----------------------------------------------------------------------------
    Filename:  pao_schedule.h
    
    Programmer:  Julie Richter
        
    Description:    
                        
    Initial Date:  1/27/3005
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2005
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
 
#ifndef CTIPAOSCHEDULE_H
#define CTIPAOSCHEDULE_H

#include <rw/thr/thrfunc.h>
#include <rw/thr/runfunc.h>
#include <rw/thr/srvpool.h>
#include <rw/thr/thrutil.h>
#include <rw/thr/countptr.h> 
#include <rw/collect.h>

#include "dbaccess.h"
#include "connection.h"
#include "ctibase.h"
#include "logger.h"
#include "yukon.h"
#include "pao_event.h"
#include "dbmemobject.h"
                       
class CtiPAOSchedule  : public CtiMemDBObject, public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
public:

    CtiPAOSchedule();
    virtual ~CtiPAOSchedule();
    CtiPAOSchedule(RWDBReader& rdr);

    long getScheduleId() const;
    const string& getScheduleName() const;
    const CtiTime& getNextRunTime() const;
    const CtiTime& getLastRunTime() const;
    long getIntervalRate() const;
    bool isDisabled();

    void setScheduleId(long schedId);
    void setScheduleName(const string& schedName);
    void setNextRunTime(const CtiTime& nextTime);
    void setLastRunTime(const CtiTime& lastTime);
    void setIntervalRate(long intervalRate);
    void setDisabledFlag(bool disabledFlag);

    void printSchedule();

    CtiPAOSchedule& operator=(const CtiPAOSchedule& right);
    int operator==(const CtiPAOSchedule& right) const;
    int operator!=(const CtiPAOSchedule& right) const;

    BOOL isDirty();
    void setDirty(BOOL flag);


private:
    
    BOOL _isValid;
    BOOL _dirty;

    long         _scheduleId;
    string    _scheduleName;
    CtiTime _nextRunTime;
    CtiTime _lastRunTime;
    long _intervalRate; //in seconds!
    bool _disabledFlag;                    

    
};
#endif
