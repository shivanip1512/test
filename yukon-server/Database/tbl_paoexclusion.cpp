

/*-----------------------------------------------------------------------------*
*
* File:   tbl_paoexclusion
*
* Date:   5/14/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/05/14 14:25:42 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include "tbl_paoexclusion.h"

CtiTablePaoExclusion::CtiTablePaoExclusion(long xid,
                                           long paoid,
                                           long excludedpaoid,
                                           long pointid = 0,
                                           double value = 0.0,
                                           long function = 0,
                                           RWCString str = RWCString(),
                                           long funcrequeue = 0)
{

}

CtiTablePaoExclusion::CtiTablePaoExclusion(const CtiTablePaoExclusion& aRef)
{
    *this = aRef;
}

bool CtiTablePaoExclusion::operator<(const CtiTablePaoExclusion &rhs) const
{
    return (_exclusionId < rhs.getExclusionId());
}

long CtiTablePaoExclusion::getExclusionId() const
{
    return _exclusionId;
}
CtiTablePaoExclusion& CtiTablePaoExclusion::setExclusionId(long xid)
{
    _exclusionId = xid;
    return *this;
}

long CtiTablePaoExclusion::getPaoId() const
{
    return _paoId;
}
CtiTablePaoExclusion& CtiTablePaoExclusion::setPaoId(long val)
{
    _paoId = val;
    return *this;
}

long CtiTablePaoExclusion::getExcludedPaoId() const
{
    return _excludedPaoId;
}
CtiTablePaoExclusion& CtiTablePaoExclusion::setExcludedPaoId(long val)
{
    _excludedPaoId = val;
    return *this;
}

long CtiTablePaoExclusion::getPointId() const
{
    return _pointId;
}
CtiTablePaoExclusion& CtiTablePaoExclusion::setPointId(long val)
{
    _pointId = val;
    return *this;
}

double CtiTablePaoExclusion::getValue() const
{
    return _value;
}
CtiTablePaoExclusion& CtiTablePaoExclusion::setValue(double val)
{
    _value = val;
    return *this;
}

long CtiTablePaoExclusion::getFunctionId() const
{
    return _functionId;
}
CtiTablePaoExclusion& CtiTablePaoExclusion::setFunctionId(long val)
{
    _functionId = val;
    return *this;
}

RWCString CtiTablePaoExclusion::getFunctionName() const
{
    return _funcName;
}
CtiTablePaoExclusion& CtiTablePaoExclusion::setFunctionName(RWCString val)
{
    _funcName = val;
    return *this;
}

long CtiTablePaoExclusion::getFunctionRequeue() const
{
    return _funcRequeue;
}
CtiTablePaoExclusion& CtiTablePaoExclusion::setFunctionRequeue(long val)
{
    _funcRequeue = val;
    return *this;
}

RWCString CtiTablePaoExclusion::getTableName()
{
    return "PaoExclusion";
}

void CtiTablePaoExclusion::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(getTableName());

    selector <<
    keyTable["exclusionid"] <<
    keyTable["paoid"] <<
    keyTable["excludedpaoid"] <<
    keyTable["pointid"] <<
    keyTable["value"] <<
    keyTable["functionid"] <<
    keyTable["funcname"] <<
    keyTable["funcrequeue"];

    selector.from(keyTable);
}

void CtiTablePaoExclusion::DecodeDatabaseReader(RWDBReader &rdr)
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & 0x0800) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["exclusionid"]      >> _exclusionId;
    rdr["paoid"]            >> _paoId;
    rdr["excludedpaoid"]    >> _excludedPaoId;
    rdr["pointid"]          >> _pointId;
    rdr["value"]            >> _value;
    rdr["functionid"]       >> _functionId;
    rdr["funcname"]         >> _funcName;
    rdr["funcrequeue"]      >> _funcRequeue;
}

RWDBStatus CtiTablePaoExclusion::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["exclusionid"] <<
    table["paoid"] <<
    table["excludedpaoid"] <<
    table["pointid"] <<
    table["value"] <<
    table["functionid"] <<
    table["funcname"] <<
    table["funcrequeue"];

    selector.where( table["exclusionid"] == getExclusionId() );  //??

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

RWDBStatus CtiTablePaoExclusion::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    return Insert(conn);
}

RWDBStatus CtiTablePaoExclusion::Insert(RWDBConnection &conn)
{
    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return inserter.status();
}


RWDBStatus CtiTablePaoExclusion::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["paobjectid"] == getPAOID() );

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return updater.status();
}

RWDBStatus CtiTablePaoExclusion::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["paobjectid"] == getPAOID() );

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return deleter.status();
}

