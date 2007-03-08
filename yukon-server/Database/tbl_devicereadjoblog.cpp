/*-----------------------------------------------------------------------------*
*
* File:   tbl_devicereadjoblog
*
* Date:   3/6/2007
*
* Author : Jess Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_devicereadjoblog.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2007/03/08 21:57:25 $
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_devicereadjoblog.h"
#include "logger.h"
#include "dbaccess.h"

#include "rwutil.h"

CtiTblDeviceReadJobLog::CtiTblDeviceReadJobLog( long jobLogId, long scheduleId, CtiTime& start, CtiTime& stop) :
_jobLogId(jobLogId),
_scheduleId(scheduleId),
_startTime(start),
_stopTime(stop)
{}

CtiTblDeviceReadJobLog::CtiTblDeviceReadJobLog(const CtiTblDeviceReadJobLog& aRef)
{
    *this = aRef;
}

CtiTblDeviceReadJobLog::~CtiTblDeviceReadJobLog()
{
}

CtiTblDeviceReadJobLog& CtiTblDeviceReadJobLog::operator=(const CtiTblDeviceReadJobLog& aRef)
{
    if(this != &aRef)
    {
        _jobLogId = aRef.getJobLogId();
        _scheduleId = aRef.getScheduleId();
        _startTime = aRef.getStartTime();
        _stopTime = aRef.getStopTime();
    }
    return *this;
}

long  CtiTblDeviceReadJobLog::getJobLogId() const
{
    return _jobLogId;
}

CtiTblDeviceReadJobLog& CtiTblDeviceReadJobLog::setJobLogId( long jobLogId )
{
    _jobLogId = jobLogId;
    return *this;
}

long  CtiTblDeviceReadJobLog::getScheduleId() const
{
    return _scheduleId;
}

CtiTblDeviceReadJobLog& CtiTblDeviceReadJobLog::setScheduleId( long scheduleId )
{
    _scheduleId = scheduleId;
    return *this;
}

const CtiTime&  CtiTblDeviceReadJobLog::getStartTime() const
{
    return _startTime;
}

CtiTblDeviceReadJobLog& CtiTblDeviceReadJobLog::setStartTime( CtiTime& startTime )
{
    _startTime = startTime;
    return *this;
}

const CtiTime&  CtiTblDeviceReadJobLog::getStopTime() const
{
    return _stopTime;
}

CtiTblDeviceReadJobLog& CtiTblDeviceReadJobLog::setStopTime( CtiTime& stopTime )
{
    _stopTime = stopTime;
    return *this;
}

string CtiTblDeviceReadJobLog::getTableName()
{
    return "DeviceReadJobLog";
}

RWDBStatus CtiTblDeviceReadJobLog::Insert()
{
    RWDBStatus retVal;

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getJobLogId() <<
    getScheduleId() <<
    getStartTime() <<
    getStopTime();

    return ExecuteInserter(conn,inserter,__FILE__,__LINE__).errorCode();
}

RWDBStatus CtiTblDeviceReadJobLog::UpdateStopTime()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBUpdater updater = table.updater();

    updater.where( table["devicereadjoblogid"] == getJobLogId() );

    updater << table["stoptime"].assign( toRWDBDT(getStopTime()) );

    return ExecuteUpdater(conn,updater,__FILE__,__LINE__);
}

RWDBStatus CtiTblDeviceReadJobLog::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBUpdater updater = table.updater();

    updater.where( table["devicereadjoblogid"] == getJobLogId() );

    updater <<
    table["scheduleid"].assign( getScheduleId() ) <<
    table["starttime"].assign( toRWDBDT(getStartTime()) ) <<
    table["stoptime"].assign( toRWDBDT(getStopTime()) );

    return ExecuteUpdater(conn,updater,__FILE__,__LINE__);
}

