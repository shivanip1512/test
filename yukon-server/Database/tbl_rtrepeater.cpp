#include "yukon.h"



/*-----------------------------------------------------------------------------*
*
* File:   tbl_rtrepeater
*
* Date:   8/22/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_rtrepeater.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "tbl_rtrepeater.h"

CtiTableRepeaterRoute::CtiTableRepeaterRoute(LONG dID, INT vb, INT ro) :
DeviceID(dID),
VarBit(vb),
RepeaterOrder(ro)
{}

CtiTableRepeaterRoute::CtiTableRepeaterRoute(const CtiTableRepeaterRoute& aRef)
{
    *this = aRef;
}

CtiTableRepeaterRoute::~CtiTableRepeaterRoute()
{}

CtiTableRepeaterRoute& CtiTableRepeaterRoute::operator=(const CtiTableRepeaterRoute& aRef)
{
    if(this != &aRef)
    {
        DeviceID       = aRef.getDeviceID();
        VarBit         = aRef.getVarBit();
        RepeaterOrder  = aRef.getRepeaterOrder();
    }
    return *this;
}

void CtiTableRepeaterRoute::DumpData()
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << " Repeater DeviceID                          " << DeviceID << endl;
    dout << " Repeater Variable Bits                     " << VarBit << endl;
    dout << " Repeater Order                             " << RepeaterOrder << endl;
}

LONG  CtiTableRepeaterRoute::getDeviceID() const
{


    return DeviceID;
}

CtiTableRepeaterRoute& CtiTableRepeaterRoute::setDeviceID( const LONG aDeviceID )
{


    DeviceID = aDeviceID;
    return *this;
}


LONG  CtiTableRepeaterRoute::getRouteID() const
{


    return _routeID;
}

CtiTableRepeaterRoute& CtiTableRepeaterRoute::setRouteID( const LONG routeID )
{


    _routeID = routeID;
    return *this;
}

INT  CtiTableRepeaterRoute::getVarBit() const
{


    return VarBit;
}

CtiTableRepeaterRoute& CtiTableRepeaterRoute::setVarBit( const INT aVarBit )
{


    VarBit = aVarBit;
    return *this;
}

INT  CtiTableRepeaterRoute::getRepeaterOrder() const
{


    return RepeaterOrder;
}

CtiTableRepeaterRoute& CtiTableRepeaterRoute::setRepeaterOrder( const INT aRepeaterOrder )
{


    RepeaterOrder = aRepeaterOrder;
    return *this;
}

void CtiTableRepeaterRoute::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table("Route");
    RWDBTable routetbl = db.table(getTableName() );

    selector <<
    keyTable["routeid"] <<
    routetbl["deviceid"] <<
    routetbl["variablebits"] <<
    routetbl["repeaterorder"];

    selector.from(keyTable);
    selector.from(routetbl);

    selector.where( selector.where() && keyTable["routeid"] == routetbl["routeid"]);

    selector.orderBy(keyTable["routeid"]);
    selector.orderBy(routetbl["repeaterorder"]);
}

void CtiTableRepeaterRoute::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString rwsTemp;

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & 0x0800) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> DeviceID;

    rdr["variablebits"] >> rwsTemp;
    VarBit = atoi(rwsTemp.data());

    rdr["repeaterorder"] >> RepeaterOrder;
}

RWBoolean CtiTableRepeaterRoute::operator<( const CtiTableRepeaterRoute& t2 )
{
    return(RepeaterOrder < t2.getRepeaterOrder() );
}

RWBoolean CtiTableRepeaterRoute::operator==( const CtiTableRepeaterRoute& t2 )
{
    // This better not ever happen!
    return( RepeaterOrder == t2.getRepeaterOrder() );
}

RWCString CtiTableRepeaterRoute::getTableName()
{
    return "RepeaterRoute";
}


RWDBStatus CtiTableRepeaterRoute::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["routeid"] <<
    table["deviceid"] <<
    table["variablebits"] <<
    table["repeaterorder"];

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

RWDBStatus CtiTableRepeaterRoute::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getRouteID() <<
    getDeviceID() <<
    getVarBit() <<
    getRepeaterOrder() ;

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableRepeaterRoute::Update()
{
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["routeid"].assign(getRouteID() ) <<
    table["deviceid"].assign(getDeviceID() ) <<
    table["variablebits"].assign(getVarBit() ) <<
    table["repeaterorder"].assign(getRepeaterOrder() );

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableRepeaterRoute::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}
