/*-----------------------------------------------------------------------------*
*
* File:   tbl_ptdispatch
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_ptdispatch.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
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


CtiTablePointDispatch::CtiTablePointDispatch() :
_pointID(0),
_timeStamp( RWDBDateTime( (UINT)1990, (UINT)1, (UINT)1) ),
_quality(UnintializedQuality),
_value(0),
_tags(0),
_staleCount(0),
_lastAlarmLogID(0),
_nextArchiveTime(RWTime(UINT_MAX - 86400))
{
    setTimeStampMillis(0);
}

CtiTablePointDispatch::CtiTablePointDispatch(LONG pointid,
                                             DOUBLE value,
                                             UINT quality,
                                             const RWDBDateTime& timestamp,
                                             UINT millis) :
_pointID(pointid),
_timeStamp(timestamp),
_quality(quality),
_value(value),
_tags(0),
_staleCount(0),
_lastAlarmLogID(0),
_nextArchiveTime(RWTime(UINT_MAX - 86400))
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
        setDirty(TRUE);
        setPointID( right.getPointID() );

        setTimeStamp( right.getTimeStamp() );
        setTimeStampMillis( right.getTimeStampMillis() );
        setQuality( right.getQuality() );
        setValue( right.getValue() );

        resetTags();
        setTags( right.getTags() );
        setNextArchiveTime( right.getNextArchiveTime() );
        setStaleCount( right.getStaleCount() );
        setLastAlarmLogID( right.getLastAlarmLogID() );
    }

    return *this;
}

int CtiTablePointDispatch::operator==(const CtiTablePointDispatch& right) const
{
    return( getPointID() == right.getPointID() );
}

RWCString CtiTablePointDispatch::getTableName()
{
    return "DynamicPointDispatch";
}

void CtiTablePointDispatch::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(CtiTablePointDispatch::getTableName());

    selector <<
    keyTable["pointid"]     <<
    keyTable["timestamp"]   <<
    keyTable["quality"]     <<
    keyTable["value"]       <<
    keyTable["tags"]        <<
    keyTable["nextarchive"] <<
    keyTable["stalecount"]  <<
    keyTable["lastalarmlogid"] <<
    keyTable["millis"];

    selector.from(keyTable);
}

RWDBStatus CtiTablePointDispatch::Restore()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["pointid"] <<
    table["timestamp"] <<
    table["quality"] <<
    table["value"] <<
    table["tags"] <<
    table["nextarchive"] <<
    table["stalecount"]  <<
    table["lastalarmlogid"] <<
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
    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["pointid"] == getPointID() );

    updater <<
    table["timestamp"].assign(getTimeStamp()) <<
    table["quality"].assign(getQuality()) <<
    table["value"].assign(getValue()) <<
    table["tags"].assign(getTags()) <<
    table["nextarchive"].assign(getNextArchiveTime()) <<
    table["stalecount"].assign(getStaleCount())  <<
    table["lastalarmlogid"].assign(getLastAlarmLogID()) <<
    table["millis"].assign(getTimeStampMillis());

    updater.execute( conn );

    if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
    {
        resetDirty(FALSE);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  " << updater.asString() << endl;
        }
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
    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getPointID() <<
    getTimeStamp() <<
    getQuality() <<
    getValue() <<
    getTags() <<
    getNextArchiveTime() <<
    getStaleCount() <<
    getLastAlarmLogID() <<
    getTimeStampMillis();

    inserter.execute( conn );

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

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["pointid"] == getPointID() );

    return deleter.execute( conn ).status();
}

void CtiTablePointDispatch::DecodeDatabaseReader(RWDBReader& rdr )
{
    INT millis;

    rdr["pointid"]        >> _pointID;
    rdr["timestamp"]      >> _timeStamp;
    rdr["quality"]        >> _quality;
    rdr["value"]          >> _value;
    rdr["tags"]           >> _tags;
    rdr["nextarchive"]    >> _nextArchiveTime;
    rdr["stalecount"]     >> _staleCount;
    rdr["lastalarmlogid"] >> _lastAlarmLogID;
    rdr["millis"]         >> millis;

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

const RWDBDateTime& CtiTablePointDispatch::getTimeStamp() const
{
    return _timeStamp;
}

CtiTablePointDispatch& CtiTablePointDispatch::setTimeStamp(const RWDBDateTime& timestamp)
{
    setDirty(TRUE);
    _timeStamp = timestamp;
    return *this;
}

UINT CtiTablePointDispatch::getTimeStampMillis() const
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
            dout << RWTime() << " **** Checkpoint - setTimeStampMillis(), millis = " << millis << " > 999 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        millis %= 1000;
    }
    else if( millis < 0 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - setTimeStampMillis(), millis = " << millis << " < 0 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    setDirty(TRUE);
    _tags |= tags;
    return _tags;
}

UINT CtiTablePointDispatch::resetTags(UINT mask)
{
    setDirty(TRUE);
    _tags &= ~mask;
    return _tags;
}


UINT CtiTablePointDispatch::getStaleCount() const
{
    return _staleCount;
}

CtiTablePointDispatch& CtiTablePointDispatch::setStaleCount(UINT stalecount)
{
    setDirty(TRUE);
    _staleCount = stalecount;
    return *this;
}

const RWDBDateTime& CtiTablePointDispatch::getNextArchiveTime() const
{
    return _nextArchiveTime;
}

CtiTablePointDispatch& CtiTablePointDispatch::setNextArchiveTime(const RWDBDateTime& timestamp)
{
    setDirty(TRUE);
    _nextArchiveTime= timestamp;
    return *this;
}

CtiTablePointDispatch& CtiTablePointDispatch::applyNewReading(const RWDBDateTime& timestamp,
                                                              UINT millis,
                                                              UINT quality,
                                                              DOUBLE value,
                                                              UINT tags,
                                                              const RWDBDateTime& archivetime,
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
        setStaleCount( count );

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
        setStaleCount( count );

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
        dout << " Time Stamp                               : " << _timeStamp.rwtime() << ", " << _timeStampMillis << "ms" << endl;
        dout << " Value                                    : " << _value << endl;
        dout << " Quality                                  : " << _quality << endl;
        dout << " Next Archive Time                        : " << _nextArchiveTime.rwtime() << endl;
        dout << " Tags                                     : 0x" << hex << setw(8) << _tags << dec << endl;
        dout << " Stale Count                              : " << _staleCount << endl;

        dout.fill(oldFill);
    }

}

ULONG CtiTablePointDispatch::getLastAlarmLogID() const
{
    return _lastAlarmLogID;
}

CtiTablePointDispatch& CtiTablePointDispatch::setLastAlarmLogID(ULONG logID)
{
    setDirty(TRUE);
    _lastAlarmLogID = logID;
    return *this;
}

