/*-----------------------------------------------------------------------------*
*
* File:   tbl_rtcomm
*
* Date:   8/17/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_rtcomm.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/10/20 21:41:28 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_rtcomm.h"

CtiTableCommRoute::CtiTableCommRoute(const LONG dID, const bool aDef) :
_routeID(-1),
DeviceID(dID),
DefaultRoute(aDef)
{}

CtiTableCommRoute::CtiTableCommRoute(const CtiTableCommRoute& aRef)
{
    *this = aRef;
}

CtiTableCommRoute::~CtiTableCommRoute()
{}

CtiTableCommRoute& CtiTableCommRoute::operator=(const CtiTableCommRoute& aRef)
{
    if(this != &aRef)
    {
        _routeID       = aRef.getRouteID();
        DeviceID       = aRef.getDeviceID();
        DefaultRoute   = aRef.getDefaultRoute();
    }
    return *this;
}

void CtiTableCommRoute::DumpData()
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << " Route ID                                   " << _routeID     << endl;
    dout << " Device ID                                  " << DeviceID     << endl;
    dout << " Default Route                              " << DefaultRoute << endl;
}

LONG  CtiTableCommRoute::getID() const
{

    return DeviceID;
}

LONG  CtiTableCommRoute::getDeviceID() const
{

    return DeviceID;
}

LONG CtiTableCommRoute::getTrxDeviceID() const
{

    return DeviceID;
}

LONG CtiTableCommRoute::getRouteID() const
{

    return _routeID;
}

CtiTableCommRoute& CtiTableCommRoute::setRouteID( const LONG routeID )
{

    _routeID = routeID;
    return *this;
}

CtiTableCommRoute& CtiTableCommRoute::setDeviceID( const LONG aDeviceID )
{

    DeviceID = aDeviceID;
    return *this;
}

bool CtiTableCommRoute::getDefaultRoute() const
{

    return DefaultRoute;
}

CtiTableCommRoute& CtiTableCommRoute::setDefaultRoute( const bool aDefaultRoute )
{

    DefaultRoute = aDefaultRoute;
    return *this;
}

void CtiTableCommRoute::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable routetbl = db.table(getTableName() );

    selector <<
    routetbl["routeid"] <<
    routetbl["deviceid"] <<
    routetbl["defaultroute"];

    selector.from(routetbl);

    selector.where( selector.where() && keyTable["paobjectid"].leftOuterJoin(routetbl["routeid"]));
}

void CtiTableCommRoute::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["routeid"] >> _routeID;
    rdr["deviceid"] >> DeviceID;

    rdr["defaultroute"] >> rwsTemp;
    rwsTemp.toLower();
    DefaultRoute = ((rwsTemp == 'y') ? TRUE : FALSE);
}

RWCString CtiTableCommRoute::getTableName()
{
    return "Route";
}

RWDBStatus CtiTableCommRoute::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["routeid"] <<
    table["deviceid"] <<
    table["defaultroute"];

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

RWDBStatus CtiTableCommRoute::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getRouteID() <<
    getDeviceID() <<
    getDefaultRoute();

    if( ExecuteInserter(conn,inserter,__FILE__,__LINE__).errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableCommRoute::Update()
{
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["routeid"] == getRouteID() );

    updater <<
    table["routeid"].assign( getRouteID() ) <<
    table["deviceid"].assign( getDeviceID() ) <<
    table["defaultroute"].assign(getDefaultRoute());

    if( ExecuteUpdater(conn,updater,__FILE__,__LINE__).errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableCommRoute::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["routeid"] == getRouteID() );
    deleter.execute( conn );
    return deleter.status();
}
