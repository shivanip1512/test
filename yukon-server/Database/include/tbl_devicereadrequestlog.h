#pragma once

#include <windows.h>
#include "ctitime.h"

#include "dlldefs.h"
#include "pointdefs.h"
#include "utility.h"
#include "yukon.h"

class IM_EX_CTIYUKONDB CtiTblDeviceReadRequestLog : private boost::noncopyable
{
    long _requestLogId;
    long _requestId;
    std::string _command;
    CtiTime _startTime;
    CtiTime _stopTime;
    long _readJobId;

public:

    CtiTblDeviceReadRequestLog( long requestLogId,
                                long requestId,
                                const std::string &cmd_line,
                                CtiTime& start,
                                CtiTime& end,
                                long jobId);

    virtual ~CtiTblDeviceReadRequestLog();

    virtual bool Insert();
    virtual bool Update();
    virtual std::string getTableName();


    long getRequestLogId() const;
    long getRequestId() const;
    long getReadJobId() const;

    const std::string&   getCommand() const;
    const CtiTime&       getStartTime() const;
    const CtiTime&       getStopTime() const;

    CtiTblDeviceReadRequestLog& setRequestLogId( long requestLogId );
    CtiTblDeviceReadRequestLog& setRequestId( long requestLogId );
    CtiTblDeviceReadRequestLog& setReadJobId( long readJobId );
    CtiTblDeviceReadRequestLog& setStartTime( CtiTime& startTime );
    CtiTblDeviceReadRequestLog& setStopTime( CtiTime& stopTime );
    CtiTblDeviceReadRequestLog& setCommand( std::string& cmd );

};
