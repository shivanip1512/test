/*-----------------------------------------------------------------------------*
*
* File:   tbl_devicereadrequestlog
*
* Date:   3/6/2007
*
* Author : Jess Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_devicereadrequestlog.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2007/03/08 21:57:25 $
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_devicereadrequestlog.h"
#include "logger.h"
#include "dbaccess.h"

#include "rwutil.h"

CtiTblDeviceReadRequestLog::CtiTblDeviceReadRequestLog( long requestLogId, long requestId, string& cmd_line, CtiTime& start, CtiTime& stop, long jobId) :
_requestLogId(requestLogId),
_requestId(requestId),
_command(cmd_line),
_startTime(start),
_stopTime(stop),
_readJobId(jobId)
{}

CtiTblDeviceReadRequestLog::CtiTblDeviceReadRequestLog(const CtiTblDeviceReadRequestLog& aRef)
{
    *this = aRef;
}

CtiTblDeviceReadRequestLog::~CtiTblDeviceReadRequestLog()
{
}

CtiTblDeviceReadRequestLog& CtiTblDeviceReadRequestLog::operator=(const CtiTblDeviceReadRequestLog& aRef)
{
    if(this != &aRef)
    {
        _requestLogId = aRef.getRequestLogId();
        _requestId = aRef.getRequestId();
        _command = aRef.getCommand();
        _startTime = aRef.getStartTime();
        _stopTime = aRef.getStopTime();
        _readJobId = aRef.getReadJobId();
    }
    return *this;
}

long  CtiTblDeviceReadRequestLog::getRequestLogId() const
{
    return _requestLogId;
}

CtiTblDeviceReadRequestLog& CtiTblDeviceReadRequestLog::setRequestLogId( long requestLogId )
{
    _requestLogId = requestLogId;
    return *this;
}

long  CtiTblDeviceReadRequestLog::getRequestId() const
{
    return _requestId;
}

CtiTblDeviceReadRequestLog& CtiTblDeviceReadRequestLog::setRequestId( long requestId )
{
    _requestId = requestId;
    return *this;
}

long  CtiTblDeviceReadRequestLog::getReadJobId() const
{
    return _readJobId;
}

CtiTblDeviceReadRequestLog& CtiTblDeviceReadRequestLog::setReadJobId( long readJobId )
{
    _readJobId = readJobId;
    return *this;
}

const CtiTime&  CtiTblDeviceReadRequestLog::getStartTime() const
{
    return _startTime;
}

CtiTblDeviceReadRequestLog& CtiTblDeviceReadRequestLog::setStartTime( CtiTime& startTime )
{
    _startTime = startTime;
    return *this;
}

const CtiTime&  CtiTblDeviceReadRequestLog::getStopTime() const
{
    return _stopTime;
}

CtiTblDeviceReadRequestLog& CtiTblDeviceReadRequestLog::setStopTime( CtiTime& stopTime )
{
    _stopTime = stopTime;
    return *this;
}

const string& CtiTblDeviceReadRequestLog::getCommand() const
{
    return _command;
}

CtiTblDeviceReadRequestLog& CtiTblDeviceReadRequestLog::setCommand( string& cmd )
{
    _command = cmd;
    return *this;
}

string CtiTblDeviceReadRequestLog::getTableName()
{
    return "DeviceReadRequestLog";
}

RWDBStatus CtiTblDeviceReadRequestLog::Insert()
{
    RWDBStatus retVal;

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getRequestLogId() <<
    getRequestId() <<
    getCommand() <<
    getStartTime() <<
    getStopTime()<<
    getReadJobId();

    return ExecuteInserter(conn,inserter,__FILE__,__LINE__).errorCode();
}

RWDBStatus CtiTblDeviceReadRequestLog::Update()
{
    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBUpdater updater = table.updater();

    updater.where( table["devicereadrequestlogid"] == getRequestLogId() );

    updater <<
    table["requestid"].assign( getRequestId() ) <<
    table["command"].assign( getCommand().c_str() ) <<
    table["starttime"].assign( toRWDBDT(getStartTime()) ) <<
    table["stoptime"].assign( toRWDBDT(getStopTime()) ) <<
    table["devicereadjoblogid"].assign( getReadJobId() );

    return ExecuteUpdater(conn,updater,__FILE__,__LINE__);
}

