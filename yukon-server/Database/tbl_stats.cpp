/*-----------------------------------------------------------------------------*
*
* File:   tbl_stats
*
* Date:   8/13/2001
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_stats.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_stats.h"
#include "logger.h"
#include "ctitime.h"
#include "ctidate.h"

using std::string;
using std::endl;

CtiTableDeviceStatistics::CtiTableDeviceStatistics() :
_deviceID(-1),
Type(StatTypeInvalid),
Attempts(0),
CommLineErrors(0),
SystemErrors(0),
DLCErrors(0)
{}

CtiTableDeviceStatistics::CtiTableDeviceStatistics(const CtiTableDeviceStatistics& aRef)
{
    *this = aRef;
}

CtiTableDeviceStatistics::~CtiTableDeviceStatistics() {}

CtiTableDeviceStatistics& CtiTableDeviceStatistics::operator=(const CtiTableDeviceStatistics& aRef)
{
    if(this != &aRef)
    {
        _deviceID      = aRef.getDeviceID();
        Type           = aRef.getType();
        Attempts       = aRef.getAttempts();
        CommLineErrors = aRef.getCommLineErrors();
        SystemErrors   = aRef.getSystemErrors();
        DLCErrors      = aRef.getDLCErrors();
        StartTime      = aRef.getStartTime();
        StopTime       = aRef.getStopTime();
    }
    return *this;
}

INT  CtiTableDeviceStatistics::getType() const
{
    return Type;
}

CtiTableDeviceStatistics& CtiTableDeviceStatistics::setType( const INT aType )
{
    Type = aType;
    return *this;
}

LONG CtiTableDeviceStatistics::getDeviceID() const
{
    return _deviceID;
}

CtiTableDeviceStatistics& CtiTableDeviceStatistics::setDeviceID( const LONG deviceID )
{
    _deviceID = deviceID;
    return *this;
}

INT  CtiTableDeviceStatistics::getAttempts() const
{
    return Attempts;
}

CtiTableDeviceStatistics& CtiTableDeviceStatistics::setAttempts( const INT aAttempts )
{
    Attempts = aAttempts;
    return *this;
}

INT  CtiTableDeviceStatistics::getCommLineErrors() const
{
    return CommLineErrors;
}

CtiTableDeviceStatistics& CtiTableDeviceStatistics::setCommLineErrors( const INT aCommLineErrors )
{
    CommLineErrors = aCommLineErrors;
    return *this;
}

INT  CtiTableDeviceStatistics::getSystemErrors() const
{
    return SystemErrors;
}

CtiTableDeviceStatistics& CtiTableDeviceStatistics::setSystemErrors( const INT aSystemErrors )
{
    SystemErrors = aSystemErrors;
    return *this;
}

INT  CtiTableDeviceStatistics::getDLCErrors() const
{
    return DLCErrors;
}

CtiTableDeviceStatistics& CtiTableDeviceStatistics::setDLCErrors( const INT aDLCErrors )
{
    return *this;
}

CtiTime  CtiTableDeviceStatistics::getStartTime() const
{
    return StartTime;
}

CtiTableDeviceStatistics& CtiTableDeviceStatistics::setStartTime( const CtiTime& aStartTime )
{
    StartTime = aStartTime;
    return *this;
}

CtiTime  CtiTableDeviceStatistics::getStopTime() const
{
    return StopTime;
}

CtiTableDeviceStatistics& CtiTableDeviceStatistics::setStopTime( const CtiTime& aStopTime )
{
    StopTime = aStopTime;
    return *this;
}

string CtiTableDeviceStatistics::getTableName()
{
    return "DeviceStatistics";
}

void CtiTableDeviceStatistics::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;

    rdr["statistictype"] >> rwsTemp;
    Type = resolveStatisticsType(rwsTemp);

    rdr["attempts"] >> Attempts;
    rdr["comlineerrors"] >> CommLineErrors;
    rdr["systemerrors"] >> SystemErrors;
    rdr["dlcerrors"] >> DLCErrors;
    rdr["startdatetime"] >> StartTime;
    rdr["stopdatetime"] >> StopTime;
}

void CtiTableDeviceStatistics::DumpData()
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << " Device ID                                  : " << _deviceID << endl;
    dout << " Statistics Type                            : " << desolveStatisticsType(Type)  << endl;
    dout << " Total Attempts                             : " << Attempts    << endl;
    dout << " Comm Errors                                : " << CommLineErrors  << endl;
    dout << " System Errors                              : " << SystemErrors << endl;
    dout << " DLC Errors                                 : " << DLCErrors << endl;
    dout << " Start Time                                 : " << StartTime << endl;
    dout << " Stop Time                                  : " << StopTime << endl;
}

