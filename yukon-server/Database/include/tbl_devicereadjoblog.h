#pragma once

#include "ctitime.h"

#include "dlldefs.h"
#include "pointdefs.h"
#include "utility.h"
#include "yukon.h"

class IM_EX_CTIYUKONDB CtiTblDeviceReadJobLog : private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTblDeviceReadJobLog(const CtiTblDeviceReadJobLog&);
    CtiTblDeviceReadJobLog& operator=(const CtiTblDeviceReadJobLog&);

protected:

    long _jobLogId;
    long _scheduleId;
    CtiTime _startTime;
    CtiTime _stopTime;

public:

    CtiTblDeviceReadJobLog( long jobLogId, long scheduleId = 0, CtiTime& start = CtiTime::now(), CtiTime& stop = CtiTime::now());
    virtual ~CtiTblDeviceReadJobLog();

    virtual bool Insert();
    virtual bool UpdateStopTime();
    virtual std::string getTableName();

    long getScheduleId() const;
    long getJobLogId() const;

    const CtiTime&  getStartTime() const;
    const CtiTime&  getStopTime() const;

    CtiTblDeviceReadJobLog& setJobLogId( long jobLogId );
    CtiTblDeviceReadJobLog& setScheduleId( long scheduleId );
    CtiTblDeviceReadJobLog& setStartTime( CtiTime& startTime );
    CtiTblDeviceReadJobLog& setStopTime( CtiTime& stopTime );

};

