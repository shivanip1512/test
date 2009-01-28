#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_accumhistory
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_accumhistory.cpp-arc  $
* REVISION     :  $Revision: 1.8.24.1 $
* DATE         :  $Date: 2008/11/18 20:11:29 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "tbl_pt_accumhistory.h"
#include "dbaccess.h"

CtiTablePointAccumulatorHistory::CtiTablePointAccumulatorHistory(LONG pid,
                                                                 ULONG prevpulsecount,
                                                                 ULONG pulsecount) :
_pointID(pid),
_previousPulseCount(prevpulsecount),
_presentPulseCount(pulsecount)
{}

CtiTablePointAccumulatorHistory::CtiTablePointAccumulatorHistory(const CtiTablePointAccumulatorHistory& ref)
{
    *this = ref;
}

CtiTablePointAccumulatorHistory::~CtiTablePointAccumulatorHistory()
{
    if(isDirty())
    {
        Update();
    }
}

CtiTablePointAccumulatorHistory& CtiTablePointAccumulatorHistory::operator=(const CtiTablePointAccumulatorHistory& aRef)
{
    if(this != &aRef)
    {
        _pointID    = aRef.getPointID();
        _previousPulseCount = aRef.getPreviousPulseCount();
        _presentPulseCount = aRef.getPresentPulseCount();
    }
    return *this;
}

bool CtiTablePointAccumulatorHistory::operator==(const CtiTablePointAccumulatorHistory& right) const
{
    return( getPointID() == right.getPointID() );
}

string CtiTablePointAccumulatorHistory::getTableName() const
{
    return "DynamicAccumulator";
}

RWDBStatus CtiTablePointAccumulatorHistory::Restore()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["pointid"] <<
    table["previouspulses"] <<
    table["presentpulses"];

    selector.where( table["pointid"] == getPointID() );

    RWDBReader reader = selector.reader( conn );

    if( reader() )
    {
        DecodeDatabaseReader( reader );
        setDirty(FALSE);
    }
    else
    {
        setDirty( TRUE );
    }

    return reader.status();
}

RWDBStatus CtiTablePointAccumulatorHistory::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBUpdater updater = table.updater();

    updater.where( table["pointid"] == getPointID() );

    updater <<
    table["previouspulses"].assign( getPresentPulseCount() ) <<
    table["presentpulses"].assign( getPresentPulseCount() );

    if( ExecuteUpdater(conn,updater,__FILE__,__LINE__) == RWDBStatus::ok  )
    {
        setDirty(FALSE);
    }

    return updater.status();
}

RWDBStatus CtiTablePointAccumulatorHistory::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getPointID() <<
    getPreviousPulseCount() <<
    getPresentPulseCount();

    setDirty(FALSE);

    return ExecuteInserter(conn,inserter,__FILE__,__LINE__);
}

RWDBStatus CtiTablePointAccumulatorHistory::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["pointid"] == getPointID() );


    return deleter.execute( conn ).status();
}

void CtiTablePointAccumulatorHistory::DecodeDatabaseReader(RWDBReader& rdr )
{
    rdr >> _pointID >> _previousPulseCount >> _presentPulseCount;
}

ULONG CtiTablePointAccumulatorHistory::getPreviousPulseCount() const
{
    return _previousPulseCount;
}

ULONG CtiTablePointAccumulatorHistory::getPresentPulseCount() const
{
    return _presentPulseCount;
}

LONG CtiTablePointAccumulatorHistory::getPointID() const
{
    return _pointID;
}
CtiTablePointAccumulatorHistory& CtiTablePointAccumulatorHistory::setPointID(LONG pointID)
{
    _pointID = pointID;
    return *this;
}

CtiTablePointAccumulatorHistory& CtiTablePointAccumulatorHistory::setPreviousPulseCount(ULONG pc)
{
    _previousPulseCount = pc;
    setDirty(TRUE);
    return *this;
}

CtiTablePointAccumulatorHistory& CtiTablePointAccumulatorHistory::setPresentPulseCount(ULONG pc)
{
    _presentPulseCount = pc;
    setDirty(TRUE);
    return *this;
}
