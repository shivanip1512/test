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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/04/15 18:28:40 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_stats.h"
#include "logger.h"

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

RWTime  CtiTableDeviceStatistics::getStartTime() const
{
    return StartTime;
}

CtiTableDeviceStatistics& CtiTableDeviceStatistics::setStartTime( const RWTime& aStartTime )
{
    StartTime = aStartTime;
    return *this;
}

RWTime  CtiTableDeviceStatistics::getStopTime() const
{
    return StopTime;
}

CtiTableDeviceStatistics& CtiTableDeviceStatistics::setStopTime( const RWTime& aStopTime )
{
    StopTime = aStopTime;
    return *this;
}

RWCString CtiTableDeviceStatistics::getTableName()
{
    return "DeviceStatistics";
}

/* These guys are handled different since they are multi-keyed */
void CtiTableDeviceStatistics::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table("Device");
    RWDBTable devTbl = db.table(getTableName() );

    selector <<
    keyTable["deviceid"] <<
    devTbl["statistictype"] <<
    devTbl["attempts"] <<
    devTbl["comlineerrors"] <<
    devTbl["systemerrors"] <<
    devTbl["dlcerrors"] <<
    devTbl["startdatetime"] <<
    devTbl["stopdatetime"];

    selector.from(keyTable);
    selector.from(devTbl);

    selector.where( selector.where() && keyTable["deviceid"] == devTbl["deviceid"] );
}

void CtiTableDeviceStatistics::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString rwsTemp;

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & DEBUGLEVEL_DATABASE) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

RWDBStatus CtiTableDeviceStatistics::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"] <<
    table["statistictype"] <<
    table["attempts"] <<
    table["comlineerrors"] <<
    table["systemerrors"] <<
    table["dlcerrors"] <<
    table["startdatetime"] <<
    table["stopdatetime"];

    selector.where( table["deviceid"] == getDeviceID() );

    RWDBReader reader = selector.reader( conn );

    if( reader() )
    {
        DecodeDatabaseReader( reader );
        setDirty( false );
    }
    else
    {
        setDirty( true );
    }
    return reader.status();
}

RWDBStatus CtiTableDeviceStatistics::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID() <<
    desolveStatisticsType(getType() ) <<
    getAttempts() <<
    getCommLineErrors() <<
    getSystemErrors() <<
    getDLCErrors() <<
    (RWDate) getStartTime()  <<
    (RWDate) getStopTime();

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableDeviceStatistics::Update()
{
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["statistictype"].assign(desolveStatisticsType(getType() )) <<
    table["attempts"].assign(getAttempts() ) <<
    table["comlineerrors"].assign(getCommLineErrors() ) <<
    table["systemerrors"].assign(getSystemErrors() ) <<
    table["dlcerrors"].assign(getDLCErrors() )<<
    table["startdatetime"].assign(getStartTime() ) <<
    table["stopdatetime"].assign(getStopTime() );

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableDeviceStatistics::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}


