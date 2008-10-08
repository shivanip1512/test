/*-----------------------------------------------------------------------------*
*
* File:   tbl_ptdispatch
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_ptdispatch.cpp-arc  $
* REVISION     :  $Revision: 1.18 $
* DATE         :  $Date: 2008/10/08 20:44:58 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <iostream>
#include <iomanip>
using namespace std;

#include "dbaccess.h"
#include "logger.h"
#include "tbl_ptdispatch.h"
#include "rwutil.h"
#include "ctidate.h"
#include "ctitime.h"

CtiTablePointDispatch::CtiTablePointDispatch() :
_pointID(0),
_timeStamp( CtiDate( (UINT)1, (UINT)1, (UINT)1990 )),
_quality(UnintializedQuality),
_value(0),
_tags(0),
//_staleCount(0),
//_lastAlarmLogID(0),
_nextArchiveTime(CtiTime(YUKONEOT+86400))
{
    setTimeStampMillis(0);
}

CtiTablePointDispatch::CtiTablePointDispatch(LONG pointid,
                                             DOUBLE value,
                                             UINT quality,
                                             const CtiTime& timestamp,
                                             UINT millis) :
_pointID(pointid),
_timeStamp(timestamp),
_quality(quality),
_value(value),
_tags(0),
//_staleCount(0),
//_lastAlarmLogID(0),
_nextArchiveTime(CtiTime(YUKONEOT - 86400))
{
    setTimeStampMillis(millis);

    setDirty(TRUE);
}

CtiTablePointDispatch::CtiTablePointDispatch(const CtiTablePointDispatch& ref) :
_tags(0)
{
    *this = ref;
}

CtiTablePointDispatch::~CtiTablePointDispatch()
{
}

CtiTablePointDispatch& CtiTablePointDispatch::operator=(const CtiTablePointDispatch& right)
{
    if(this != &right)
    {
        Inherited::operator=(right);
        setDirty(TRUE);
        setPointID( right.getPointID() );

        setTimeStamp( right.getTimeStamp() );
        setTimeStampMillis( right.getTimeStampMillis() );
        setQuality( right.getQuality() );
        setValue( right.getValue() );

        resetTags();
        setTags( right.getTags() );
        setNextArchiveTime( right.getNextArchiveTime() );
        //setStaleCount( right.getStaleCount() );
        //setLastAlarmLogID( right.getLastAlarmLogID() );
    }

    return *this;
}

int CtiTablePointDispatch::operator==(const CtiTablePointDispatch& right) const
{
    return( getPointID() == right.getPointID() );
}

string CtiTablePointDispatch::getTableName()
{
    return "DynamicPointDispatch";
}

void CtiTablePointDispatch::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(CtiTablePointDispatch::getTableName().c_str());

    selector <<
    keyTable["pointid"]     <<
    keyTable["timestamp"]   <<
    keyTable["quality"]     <<
    keyTable["value"]       <<
    keyTable["tags"]        <<
    keyTable["nextarchive"] <<
    //keyTable["stalecount"]  <<
    //keyTable["lastalarmlogid"] <<
    keyTable["millis"];

    selector.from(keyTable);
}

RWDBStatus CtiTablePointDispatch::Restore()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["pointid"] <<
    table["timestamp"] <<
    table["quality"] <<
    table["value"] <<
    table["tags"] <<
    table["nextarchive"] <<
    //table["stalecount"]  <<
    //table["lastalarmlogid"] <<
    table["millis"];

    selector.where( table["pointid"] == getPointID() );

    RWDBReader reader = selector.reader( conn );

    /*
     *  If we are in the database, we reload and ARE NOT dirty... otherwise, we are sirty and need to be
     *  written into the database
     */
    if( reader() )
    {
        DecodeDatabaseReader( reader );
    }
    else
    {
        setDirty( TRUE );
    }

    return reader.status();
}

RWDBStatus CtiTablePointDispatch::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    return Update(conn);
}

RWDBStatus CtiTablePointDispatch::Update(RWDBConnection &conn)
{
    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBUpdater updater = table.updater();
    try
    {
    
        updater.where( table["pointid"] == getPointID() );
    
        updater <<
        table["timestamp"].assign(toRWDBDT(getTimeStamp())) <<
        table["quality"].assign(getQuality()) <<
        table["value"].assign(getValue()) <<
        table["tags"].assign(getTags()) <<
        table["nextarchive"].assign(toRWDBDT(getNextArchiveTime())) <<
        table["stalecount"].assign(getStaleCount())  <<
        //table["lastalarmlogid"].assign(getLastAlarmLogID()) <<
        table["millis"].assign(getTimeStampMillis());
    
        ExecuteUpdater(conn,updater,__FILE__,__LINE__);
    
        if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
        {
            resetDirty(FALSE);
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  " << updater.asString() << endl;
            }
        }
    }catch(...)
    {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return updater.status();
}

RWDBStatus CtiTablePointDispatch::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    return Insert(conn);
}

RWDBStatus CtiTablePointDispatch::Insert(RWDBConnection &conn)
{
    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getPointID() <<
    toRWDBDT(getTimeStamp()) <<
    getQuality() <<
    getValue() <<
    getTags() <<
    toRWDBDT( getNextArchiveTime() ) <<
    getStaleCount() <<
    getLastAlarmLogID() <<
    getTimeStampMillis();

    ExecuteInserter(conn,inserter,__FILE__,__LINE__);

    if(inserter.status().errorCode() != RWDBStatus::ok)    // No error occured!
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** SQL FAILED Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << inserter.asString() << endl;
        }
    }
    else
    {
        resetDirty(FALSE);
    }

    return inserter.status();
}

RWDBStatus CtiTablePointDispatch::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["pointid"] == getPointID() );

    return deleter.execute( conn ).status();
}

void CtiTablePointDispatch::DecodeDatabaseReader(RWDBReader& rdr )
{
    static const RWCString pointid = "pointid";
    INT millis;

    rdr[pointid] >> _pointID;
    rdr >> _timeStamp;
    rdr >> _quality;
    rdr >> _value;
    rdr >> _tags;
    rdr >> _nextArchiveTime;
    rdr >> millis;

    setTimeStampMillis(millis);

    resetDirty(FALSE);
}

LONG CtiTablePointDispatch::getPointID() const
{
    return _pointID;
}

CtiTablePointDispatch& CtiTablePointDispatch::setPointID(LONG pointid)
{
    setDirty(TRUE);
    _pointID = pointid;
    return *this;
}

const CtiTime& CtiTablePointDispatch::getTimeStamp() const
{
    return _timeStamp;
}

CtiTablePointDispatch& CtiTablePointDispatch::setTimeStamp(const CtiTime& timestamp)
{
    setDirty(TRUE);
    _timeStamp = timestamp;
    return *this;
}

INT CtiTablePointDispatch::getTimeStampMillis() const
{
    return _timeStampMillis;
}

CtiTablePointDispatch& CtiTablePointDispatch::setTimeStampMillis(INT millis)
{
    setDirty(TRUE);

    if( millis > 999 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - setTimeStampMillis(), millis = " << millis << " > 999 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        millis %= 1000;
    }
    else if( millis < 0 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - setTimeStampMillis(), millis = " << millis << " < 0 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        millis = 0;
    }

    _timeStampMillis = millis;

    return *this;
}

UINT CtiTablePointDispatch::getQuality() const
{
    return _quality;
}

CtiTablePointDispatch& CtiTablePointDispatch::setQuality(UINT quality)
{
    setDirty(TRUE);
    _quality = quality;
    return *this;
}

DOUBLE CtiTablePointDispatch::getValue() const
{
    return _value;
}

CtiTablePointDispatch& CtiTablePointDispatch::setValue(DOUBLE value)
{
    setDirty(TRUE);
    _value = value;
    return *this;
}

UINT CtiTablePointDispatch::getTags() const
{
    return _tags;
}

UINT CtiTablePointDispatch::setTags(UINT tags)
{
    UINT oldTags = _tags;
    _tags |= tags;
    if( oldTags != _tags )
    {
        setDirty(TRUE);
    }
    return _tags;
}

UINT CtiTablePointDispatch::resetTags(UINT mask)
{
    UINT oldTags = _tags;
    _tags &= ~mask;
    if( oldTags != _tags )
    {
        setDirty(TRUE);
    }
    return _tags;
}

// getStaleCount always returns 0
UINT CtiTablePointDispatch::getStaleCount() const
{
    return 0; //_stalecount
}

const CtiTime& CtiTablePointDispatch::getNextArchiveTime() const
{
    return _nextArchiveTime;
}

CtiTablePointDispatch& CtiTablePointDispatch::setNextArchiveTime(const CtiTime& timestamp)
{
    setDirty(TRUE);
    _nextArchiveTime= timestamp;
    return *this;
}

CtiTablePointDispatch& CtiTablePointDispatch::applyNewReading(const CtiTime& timestamp,
                                                              UINT millis,
                                                              UINT quality,
                                                              DOUBLE value,
                                                              UINT tags,
                                                              const CtiTime& archivetime,
                                                              UINT count )
{


    if(timestamp < _timeStamp)    // The setting is backward in time...
    {
        setTimeStamp( timestamp );
        setTimeStampMillis(millis);
        setQuality( quality );
        setValue( value );

        setTags( tags );
        setNextArchiveTime( archivetime );

        setDirty(TRUE);
    }
    else if(timestamp >= _timeStamp)
    {
        setTimeStamp( timestamp );
        setTimeStampMillis(millis);
        setQuality( quality );
        setValue( value );

        setTags( tags );
        setNextArchiveTime( archivetime );

        setDirty(TRUE);
    }
    return *this;
}

void CtiTablePointDispatch::dump()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        CHAR  oldFill = dout.fill();
        dout.fill('0');

        dout << endl;
        dout << " PointID                                  : " << _pointID << endl;
        dout << " Time Stamp                               : " << _timeStamp << ", " << _timeStampMillis << "ms" << endl;
        dout << " Value                                    : " << _value << endl;
        dout << " Quality                                  : " << _quality << endl;
        dout << " Next Archive Time                        : " << _nextArchiveTime << endl;
        dout << " Tags                                     : 0x" << hex << setw(8) << _tags << dec << endl;

        dout.fill(oldFill);
    }

}

// getLastAlarmLogID always returns 0
ULONG CtiTablePointDispatch::getLastAlarmLogID() const
{
    return 0;
}
