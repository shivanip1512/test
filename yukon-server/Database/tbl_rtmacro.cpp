#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_rtmacro
*
* Date:   8/22/2001
*
* Author : Eric Schmit        **IS NOT FINISHED - NEED COREY**
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_rtmacro.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "tbl_rtmacro.h"

CtiTableMacroRoute::CtiTableMacroRoute(LONG RouteID, INT RouteOrder)
{
}

CtiTableMacroRoute::CtiTableMacroRoute(const CtiTableMacroRoute& aRef)
{
    *this = aRef;
}

CtiTableMacroRoute::~CtiTableMacroRoute()
{
}

CtiTableMacroRoute& CtiTableMacroRoute::operator=(const CtiTableMacroRoute& aRef)
{
    if(this != &aRef)
    {
        RouteID           = aRef.getRouteID();
        RouteOrder        = aRef.getRouteOrder();
        _singleRouteID    = aRef.getSingleRouteID();
    }
    return *this;
}

void CtiTableMacroRoute::DumpData()
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << " Macro Standard RouteID / Order             " << getSingleRouteID()  << " / " << getRouteOrder() << endl;
}

LONG  CtiTableMacroRoute::getRouteID() const
{


    return RouteID;
}

CtiTableMacroRoute& CtiTableMacroRoute::setRouteID( const LONG aRouteID )
{


    RouteID = aRouteID;
    return *this;
}

INT  CtiTableMacroRoute::getRouteOrder() const
{


    return RouteOrder;
}

CtiTableMacroRoute& CtiTableMacroRoute::setRouteOrder( const INT aRouteOrder )
{


    RouteOrder = aRouteOrder;
    return *this;
}

INT CtiTableMacroRoute::getSingleRouteID() const
{


    return _singleRouteID;
}

CtiTableMacroRoute& CtiTableMacroRoute::setSingleRouteID( const INT singleID )
{


    _singleRouteID = singleID;
    return *this;
}

RWBoolean CtiTableMacroRoute::operator<(const CtiTableMacroRoute& t2)
{
    return(getRouteOrder() < t2.getRouteOrder() );
}

RWBoolean CtiTableMacroRoute::operator==(const CtiTableMacroRoute& t2)
{
    return( getRouteID() == t2.getRouteID() && getRouteOrder() == t2.getRouteOrder() );
}

void CtiTableMacroRoute::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table("Route");
    RWDBTable routetbl = db.table(getTableName() );

    selector <<
    keyTable["routeid"] <<
    routetbl["singlerouteid"] <<
    routetbl["routeorder"];

    selector.from(keyTable);
    selector.from(routetbl);

    selector.where( selector.where() && keyTable["routeid"] == routetbl["routeid"]);

    selector.orderBy(routetbl["routeid"]);
    selector.orderBy(routetbl["routeorder"]);
}

void CtiTableMacroRoute::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString rwsTemp;

    if(getDebugLevel() & 0x0800)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["routeid"]       >> RouteID;
    rdr["singlerouteid"] >> _singleRouteID;
    rdr["routeorder"]    >> RouteOrder;
}

RWCString CtiTableMacroRoute::getTableName()
{
    return "MacroRoute";
}

RWDBStatus CtiTableMacroRoute::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["singlerouteid"] <<
    table["routeorder"];

    selector.where( table["routeid"] == getRouteID() );

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

RWDBStatus CtiTableMacroRoute::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getRouteID() <<
    getSingleRouteID() <<
    getRouteOrder();

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableMacroRoute::Update()
{
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["routeid"] == getRouteID() );

    updater <<
    table["singlerouteid"].assign( getSingleRouteID() ) <<
    table["routeorder"].assign(getRouteOrder());

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableMacroRoute::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["routeid"] == getRouteID() );
    deleter.execute( conn );
    return deleter.status();
}

