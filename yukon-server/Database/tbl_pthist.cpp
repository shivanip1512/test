#include "yukon.h"
#include "tbl_pthist.h"

/*-----------------------------------------------------------------------------*
*
* File:   tbl_pthist
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pthist.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dbaccess.h"

CtiTablePointHistory::CtiTablePointHistory()
{
}

CtiTablePointHistory::CtiTablePointHistory(LONG pointid, const RWDBDateTime& timestamp, INT quality, FLOAT value ):
PointID(pointid),
TimeStamp(timestamp),
Quality(quality),
Value(value)
{
}

CtiTablePointHistory::CtiTablePointHistory(const CtiTablePointHistory& ref)
{
    *this = ref;
}

CtiTablePointHistory::~CtiTablePointHistory()
{
}

CtiTablePointHistory::operator=(const CtiTablePointHistory& right)
{
    setPointID( right.getPointID() );
    setTimeStamp( right.getTimeStamp() );
    setQuality( right.getQuality() );
    setValue( right.getValue() );
}

int CtiTablePointHistory::operator==(const CtiTablePointHistory& right) const
{
    return( getPointID() == right.getPointID() &&
            getTimeStamp() == right.getTimeStamp() );
}

RWCString CtiTablePointHistory::getTableName() const
{
    return "PointHistory";
}

void CtiTablePointHistory::Restore()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector << table["pointid"]
    << table["timestamp"]
    << table["quality"]
    << table["value"];

    selector.where( table["pointid"] == getPointID() &&
                    table["timestamp"] == getTimeStamp() );

    RWDBReader reader = selector.reader( conn );

    if( reader() )
    {
        DecodeDatabaseReader( reader );
    }
    else
    {
        setDirty( true );
    }
}

void CtiTablePointHistory::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["pointid"] == getPointID() &&
                   table["timestamp"] == getTimeStamp() );

    updater << table["quality"].assign(getQuality())
    << table["value"].assign(getValue());

    updater.execute( conn );
}

void CtiTablePointHistory::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter << getPointID()
    << getTimeStamp()
    << getQuality()
    << getValue();

    inserter.execute( conn );
}

void CtiTablePointHistory::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["pointid"] == getPointID() &&
                   table["timestamp"] == getTimeStamp() );

    deleter.execute( conn );
}

void CtiTablePointHistory::DecodeDatabaseReader(RWDBReader& rdr )
{
    rdr >> PointID >> TimeStamp >> Quality >> Value;
}

LONG CtiTablePointHistory::getPointID() const
{


    return PointID;
}

CtiTablePointHistory& CtiTablePointHistory::setPointID(LONG pointid)
{


    PointID = pointid;
    return *this;
}

const RWDBDateTime& CtiTablePointHistory::getTimeStamp() const
{


    return TimeStamp;
}

CtiTablePointHistory& CtiTablePointHistory::setTimeStamp(const RWDBDateTime& timestamp)
{


    TimeStamp = timestamp;
    return *this;
}

INT CtiTablePointHistory::getQuality() const
{


    return Quality;
}

CtiTablePointHistory& CtiTablePointHistory::setQuality(INT quality)
{


    Quality = quality;
    return *this;
}

FLOAT CtiTablePointHistory::getValue() const
{


    return Value;
}

CtiTablePointHistory& CtiTablePointHistory::setValue(FLOAT value)
{


    Value = value;
    return *this;
}


