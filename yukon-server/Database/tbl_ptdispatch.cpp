

#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_ptdispatch
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_ptdispatch.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2002/09/19 18:04:57 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <iostream>
#include <iomanip>
using namespace std;

#include "dbaccess.h"
#include "logger.h"
#include "tbl_ptdispatch.h"

CtiTablePointDispatch::CtiTablePointDispatch() :
iPointID(0),
iTimeStamp( RWDBDateTime( (UINT)1990, (UINT)1, (UINT)1) ),
iQuality(UnintializedQuality),
iValue(0),
iTags(0),
iStaleCount(0),
iLastAlarmLogID(0),
iNextArchiveTime(RWTime(UINT_MAX - 86400))
{
}

CtiTablePointDispatch::CtiTablePointDispatch(LONG pointid,
                                             DOUBLE value,
                                             UINT quality,
                                             const RWDBDateTime& timestamp) :
iPointID(pointid),
iTimeStamp(timestamp),
iQuality(quality),
iValue(value),
iTags(0),
iStaleCount(0),
iLastAlarmLogID(0),
iNextArchiveTime(RWTime(UINT_MAX - 86400))
{
    setDirty(TRUE);
}

CtiTablePointDispatch::CtiTablePointDispatch(const CtiTablePointDispatch& ref) :
iTags(0)
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
    keyTable["lastalarmlogid"];

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
    table["lastalarmlogid"];

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
    table["lastalarmlogid"].assign(getLastAlarmLogID());

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
    getLastAlarmLogID();

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
    rdr["pointid"] >> iPointID;
    rdr["timestamp"] >> iTimeStamp;
    rdr["quality"] >> iQuality;
    rdr["value"] >> iValue;
    rdr["tags"] >> iTags;
    rdr["nextarchive"] >> iNextArchiveTime;
    rdr["stalecount"] >> iStaleCount;
    rdr["lastalarmlogid"] >> iLastAlarmLogID;

    resetDirty(FALSE);
}

LONG CtiTablePointDispatch::getPointID() const
{


    return iPointID;
}

CtiTablePointDispatch& CtiTablePointDispatch::setPointID(LONG pointid)
{


    setDirty(TRUE);
    iPointID = pointid;
    return *this;
}

const RWDBDateTime& CtiTablePointDispatch::getTimeStamp() const
{


    return iTimeStamp;
}

CtiTablePointDispatch& CtiTablePointDispatch::setTimeStamp(const RWDBDateTime& timestamp)
{


    setDirty(TRUE);
    iTimeStamp = timestamp;
    return *this;
}

UINT CtiTablePointDispatch::getQuality() const
{


    return iQuality;
}

CtiTablePointDispatch& CtiTablePointDispatch::setQuality(UINT quality)
{


    setDirty(TRUE);
    iQuality = quality;
    return *this;
}

DOUBLE CtiTablePointDispatch::getValue() const
{


    return iValue;
}

CtiTablePointDispatch& CtiTablePointDispatch::setValue(DOUBLE value)
{


    setDirty(TRUE);
    iValue = value;
    return *this;
}

UINT CtiTablePointDispatch::getTags() const
{


    return iTags;
}

UINT CtiTablePointDispatch::setTags(UINT tags)
{


    setDirty(TRUE);
    iTags |= tags;
    return iTags;
}

UINT CtiTablePointDispatch::resetTags(UINT mask)
{
    setDirty(TRUE);
    iTags &= ~mask;
    return iTags;
}


UINT CtiTablePointDispatch::getStaleCount() const
{
    return iStaleCount;
}

CtiTablePointDispatch& CtiTablePointDispatch::setStaleCount(UINT stalecount)
{
    setDirty(TRUE);
    iStaleCount = stalecount;
    return *this;
}

const RWDBDateTime& CtiTablePointDispatch::getNextArchiveTime() const
{
    return iNextArchiveTime;
}

CtiTablePointDispatch& CtiTablePointDispatch::setNextArchiveTime(const RWDBDateTime& timestamp)
{
    setDirty(TRUE);
    iNextArchiveTime= timestamp;
    return *this;
}

CtiTablePointDispatch& CtiTablePointDispatch::applyNewReading(const RWDBDateTime& timestamp,
                                                              UINT quality,
                                                              DOUBLE value,
                                                              UINT tags,
                                                              const RWDBDateTime& archivetime,
                                                              UINT count )
{


    if(timestamp < iTimeStamp)    // The setting is backward in time...
    {
        setTimeStamp( timestamp );
        setQuality( quality );
        setValue( value );

        setTags( tags );
        setNextArchiveTime( archivetime );
        setStaleCount( count );

        setDirty(TRUE);
    }
    else if(timestamp >= iTimeStamp)
    {
        setTimeStamp( timestamp );
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

        dout << endl << "PointID                           : " << iPointID << endl;
        dout << " Time Stamp                               : " << iTimeStamp.rwtime() << endl;
        dout << " Value                                    : " << iValue << endl;
        dout << " Quality                                  : " << iQuality << endl;
        dout << " Next Archive Time                        : " << iNextArchiveTime.rwtime() << endl;
        dout << " Tags                                     : 0x" << hex << setw(8) << iTags << dec << endl;
        dout << " Stale Count                              : " << iStaleCount << endl;

        dout.fill(oldFill);
    }

}

CtiTablePointDispatch::CtiTablePointDispatch();

CtiTablePointDispatch::CtiTablePointDispatch(LONG pointid,DOUBLE value,UINT quality,const RWDBDateTime& timestamp);

CtiTablePointDispatch::CtiTablePointDispatch(const CtiTablePointDispatch& aRef);

CtiTablePointDispatch::~CtiTablePointDispatch();



ULONG CtiTablePointDispatch::getLastAlarmLogID() const
{
    return iLastAlarmLogID;
}
CtiTablePointDispatch& CtiTablePointDispatch::setLastAlarmLogID(ULONG logID)
{
    setDirty(TRUE);
    iLastAlarmLogID = logID;
    return *this;
}

