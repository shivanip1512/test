#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_2way
*
* Date:   8/6/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_2way.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:47 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/db/reader.h>
#include <rw\cstring.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>

#include "tbl_2way.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "logger.h"

CtiTableDevice2Way::CtiTableDevice2Way(LONG did) :
_deviceID(did),
Flag(0),
PerformanceThreshold(-1)
{
}

CtiTableDevice2Way::CtiTableDevice2Way(const CtiTableDevice2Way &aRef)
{
    *this = aRef;
}

CtiTableDevice2Way& CtiTableDevice2Way::operator=(const CtiTableDevice2Way &aRef)
{


    if(this != &aRef)
    {
        PerformanceThreshold  = aRef.getPerformanceThreshold();

        _deviceID             = aRef.getDeviceID();
        MonthlyStats          = aRef.getMonthlyStats();
        DailyStats            = aRef.getDailyStats();
        HourlyStats           = aRef.getHourlyStats();
        FailureAlarm          = aRef.getFailureAlarm();
        PerformAlarm          = aRef.getPerformAlarm();
        Perform24Alarm        = aRef.getPerform24Alarm();
    }

    return *this;
}

LONG CtiTableDevice2Way::getDeviceID() const
{

    return _deviceID;
}

INT  CtiTableDevice2Way::getPerformanceThreshold() const
{

    return PerformanceThreshold;
}

CtiTableDevice2Way& CtiTableDevice2Way::setPerformanceThreshold( const INT aPerformanceThreshold )
{

    PerformanceThreshold = aPerformanceThreshold;
    return *this;
}

INT  CtiTableDevice2Way::getMonthlyStats() const
{

    return MonthlyStats;
}

CtiTableDevice2Way& CtiTableDevice2Way::setMonthlyStats( const INT theMonthlyStats )
{

    MonthlyStats = theMonthlyStats;
    return *this;
}

INT  CtiTableDevice2Way::getDailyStats() const
{

    return DailyStats;
}

CtiTableDevice2Way& CtiTableDevice2Way::setDailyStats( const INT theDailyStats )
{

    DailyStats = theDailyStats;
    return *this;
}

INT  CtiTableDevice2Way::getHourlyStats() const
{

    return HourlyStats;
}

CtiTableDevice2Way& CtiTableDevice2Way::setHourlyStats( const INT theHourlyStats )
{

    HourlyStats = theHourlyStats;
    return *this;
}

INT  CtiTableDevice2Way::getFailureAlarm() const
{

    return FailureAlarm;
}

CtiTableDevice2Way& CtiTableDevice2Way::setFailureAlarm( const INT aFailureAlarm )
{

    FailureAlarm = aFailureAlarm;
    return *this;
}

INT  CtiTableDevice2Way::getPerformAlarm() const
{

    return PerformAlarm;
}

CtiTableDevice2Way& CtiTableDevice2Way::setPerformAlarm( const INT aPerformAlarm )
{

    PerformAlarm = aPerformAlarm;
    return *this;
}

INT  CtiTableDevice2Way::getPerform24Alarm() const
{

    return Perform24Alarm;
}

CtiTableDevice2Way& CtiTableDevice2Way::setPerform24Alarm( const INT aPerform24Alarm )
{

    Perform24Alarm = aPerform24Alarm;
    return *this;
}

RWCString CtiTableDevice2Way::getTableName()
{
    return "Device2WayFlags";
}

void CtiTableDevice2Way::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName() );

    selector <<
    devTbl["deviceid"] <<
    devTbl["monthlystats"] <<
    devTbl["twentyfourhourstats"] <<
    devTbl["hourlystats"] <<
    devTbl["failurealarm"] <<
    devTbl["performancethreshold"] <<
    devTbl["performancealarm"] <<
    devTbl["performancetwentyfouralarm"];// <<

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
}

RWDBStatus CtiTableDevice2Way::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"] <<
    table["monthlystats"] <<
    table["twentyfourhourstats"] <<
    table["hourlystats"] <<
    table["failurealarm"] <<
    table["performancethreshold"] <<
    table["performancealarm"] <<
    table["performancetwentyfouralarm"];// <<

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

CtiTableDevice2Way& CtiTableDevice2Way::setDeviceID(LONG deviceID)
{

    _deviceID = deviceID;
    return *this;
}

void CtiTableDevice2Way::DecodeDatabaseReader(RWDBReader &rdr)
{
    {
        if(getDebugLevel() & 0x0800)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout); dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }


    INT iTemp;
    RWCString   rwsTemp;

    rdr["deviceid"]     >> _deviceID;

    rdr["monthlystats"]     >> rwsTemp;
    rwsTemp.toLower();
    MonthlyStats = ((rwsTemp != 'y') ? FALSE : TRUE );

    rdr["twentyfourhourstats"] >> rwsTemp;
    rwsTemp.toLower();
    DailyStats = ((rwsTemp == 'y') ? TRUE : FALSE);

    rdr["hourlystats"]     >> rwsTemp;
    rwsTemp.toLower();
    HourlyStats = ((rwsTemp == 'y') ? TRUE : FALSE);

    rdr["failurealarm"]     >> rwsTemp;
    rwsTemp.toLower();
    FailureAlarm = ((rwsTemp == 'y') ? TRUE : FALSE);

    rdr["performancealarm"]     >> rwsTemp;
    rwsTemp.toLower();
    PerformAlarm = ((rwsTemp == 'y') ? TRUE : FALSE);

    rdr["performancetwentyfouralarm"]     >> rwsTemp;
    rwsTemp.toLower();
    Perform24Alarm = ((rwsTemp == 'y') ? TRUE : FALSE);

    rdr["performancethreshold"] >> PerformanceThreshold;
}

RWDBStatus CtiTableDevice2Way::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID() <<
    getMonthlyStats() <<
    getDailyStats() <<
    getHourlyStats() <<
    getFailureAlarm() <<
    getPerformAlarm() <<
    getPerform24Alarm() <<
    getPerformanceThreshold();// <<

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableDevice2Way::Update()
{
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["monthlystats"].assign(getMonthlyStats() ) <<
    table["dailystats"].assign(getDailyStats() ) <<
    table["hourlystats"].assign(getHourlyStats() ) <<
    table["failurealarm"].assign(getFailureAlarm() ) <<
    table["performalarm"].assign(getPerformAlarm() ) <<
    table["performtwentyfouralarm"].assign(getPerform24Alarm() ) <<
    table["performancethreshold"].assign(getPerformanceThreshold() );// <<

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableDevice2Way::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}

