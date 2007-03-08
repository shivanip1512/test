
/*-----------------------------------------------------------------------------*
*
* File:   tbl_devicereadrequestlog
*
* Date:   3/6/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/Database/Include/tbl_devicereadrequestlog.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2007/03/08 21:57:26 $
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DEVICEREADREQUESTLOG_H__
#define __TBL_DEVICEREADREQUESTLOG_H__

#include <windows.h>
#include "ctitime.h"
#include <rw/db/datetime.h>

#include "dlldefs.h"
#include "pointdefs.h"
#include "utility.h"
#include "yukon.h"

class IM_EX_CTIYUKONDB CtiTblDeviceReadRequestLog
{
protected:

    long _requestLogId;
    long _requestId;
    string _command;
    CtiTime _startTime;
    CtiTime _stopTime;
    long _readJobId;

private:

public:

    CtiTblDeviceReadRequestLog( long requestLogId,
                                long requestId,
                                string& cmd_line,
                                CtiTime& start,
                                CtiTime& end,
                                long jobId);

    CtiTblDeviceReadRequestLog(const CtiTblDeviceReadRequestLog& aRef);

    virtual ~CtiTblDeviceReadRequestLog();

    CtiTblDeviceReadRequestLog& operator=(const CtiTblDeviceReadRequestLog& aRef);

    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual string getTableName();


    long getRequestLogId() const;
    long getRequestId() const;
    long getReadJobId() const;

    const string&   getCommand() const;
    const CtiTime&  getStartTime() const;
    const CtiTime&  getStopTime() const;

    CtiTblDeviceReadRequestLog& setRequestLogId( long requestLogId );
    CtiTblDeviceReadRequestLog& setRequestId( long requestLogId );
    CtiTblDeviceReadRequestLog& setReadJobId( long readJobId );
    CtiTblDeviceReadRequestLog& setStartTime( CtiTime& startTime );
    CtiTblDeviceReadRequestLog& setStopTime( CtiTime& stopTime );
    CtiTblDeviceReadRequestLog& setCommand( string& cmd );

};

#endif
