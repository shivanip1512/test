
/*-----------------------------------------------------------------------------*
*
* File:   tbl_devicereadjoblog
*
* Date:   3/6/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/Database/Include/tbl_devicereadjoblog.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2007/03/08 21:57:26 $
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DEVICEREADJOBLOG_H__
#define __TBL_DEVICEREADJOBLOG_H__

#include <windows.h>
#include "ctitime.h"
#include <rw/db/datetime.h>

#include "dlldefs.h"
#include "pointdefs.h"
#include "utility.h"
#include "yukon.h"

class IM_EX_CTIYUKONDB CtiTblDeviceReadJobLog
{
protected:

    long _jobLogId;
    long _scheduleId;
    CtiTime _startTime;
    CtiTime _stopTime;

private:

public:

    CtiTblDeviceReadJobLog( long jobLogId, long scheduleId = 0, CtiTime& start = CtiTime::now(), CtiTime& stop = CtiTime::now());

    CtiTblDeviceReadJobLog(const CtiTblDeviceReadJobLog& aRef);

    virtual ~CtiTblDeviceReadJobLog();

    CtiTblDeviceReadJobLog& operator=(const CtiTblDeviceReadJobLog& aRef);

    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus UpdateStopTime();
    virtual string getTableName();


    long getScheduleId() const;
    long getJobLogId() const;

    const CtiTime&  getStartTime() const;
    const CtiTime&  getStopTime() const;

    CtiTblDeviceReadJobLog& setJobLogId( long jobLogId );
    CtiTblDeviceReadJobLog& setScheduleId( long scheduleId );
    CtiTblDeviceReadJobLog& setStartTime( CtiTime& startTime );
    CtiTblDeviceReadJobLog& setStopTime( CtiTime& stopTime );

};

#endif
