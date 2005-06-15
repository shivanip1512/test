#include "yukon.h"
#include "tbl_rtroute.h"

/*-----------------------------------------------------------------------------*
*
* File:   tbl_rtroute
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_rtroute.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/06/15 23:56:34 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dbaccess.h"
#include "logger.h"

CtiTableRoute::CtiTableRoute() :
RouteID(-1),
Type(RouteTypeInvalid)
{}

CtiTableRoute::CtiTableRoute(LONG &aRoute, RWCString aStr, INT aType) :
RouteID(aRoute),
Name(aStr),
Type(aType)
{}

CtiTableRoute::CtiTableRoute(const CtiTableRoute& aRef)
{
    *this = aRef;
}

CtiTableRoute::~CtiTableRoute() {}

CtiTableRoute& CtiTableRoute::operator=(const CtiTableRoute& aRef)
{
    if(this != &aRef)
    {
        RouteID  = aRef.getRouteID();
        Type     = aRef.getType();
        Name     = aRef.getName();
    }
    return *this;
}

void CtiTableRoute::DumpData()
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << "Route ID                                    " << RouteID << endl;
    dout << " Route Name                                 " << Name << endl;
    dout << " Route Type                                 " << Type << endl;
}

RWCString& CtiTableRoute::getSQLColumns(RWCString &str)
{
    str += RWCString("ROUTE.ROUTEID,ROUTE.NAME,ROUTE.TYPE");
    return str;
}

RWCString& CtiTableRoute::getSQLTables(RWCString &str)
{
    str += RWCString("ROUTE");
    return str;
}

RWCString& CtiTableRoute::getSQLConditions(RWCString &str)
{
    return str;
}

void CtiTableRoute::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table("Route");

    selector <<
    keyTable["routeid"] <<
    keyTable["name"] <<
    keyTable["type"];

    selector.from(keyTable);

    // No where clause...

}

void CtiTableRoute::getSQL(RWCString &Columns, RWCString &Tables, RWCString &Conditions)
{
    getSQLColumns(Columns);
    getSQLTables(Tables);
    getSQLConditions(Conditions);
}

INT  CtiTableRoute::getType() const                                  { return Type;}

CtiTableRoute& CtiTableRoute::setType( const INT aType )
{
    Type = aType;
    return *this;
}

RWCString  CtiTableRoute::getName() const                            { return Name;}

CtiTableRoute& CtiTableRoute::setName( const RWCString aName )
{
    Name = aName;
    return *this;
}

LONG  CtiTableRoute::getID() const                                   { return RouteID;}
LONG  CtiTableRoute::getRouteID() const                              { return RouteID;}
CtiTableRoute& CtiTableRoute::setRouteID( const LONG aRouteID )
{
    RouteID = aRouteID;
    return *this;
}

void CtiTableRoute::Insert()
{
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBTable table = getDatabase().table( getTableName() );
        RWDBInserter inserter = table.inserter();

        inserter << getRouteID() << getName() << getType();
        inserter.execute( conn );
    }
}

void CtiTableRoute::Update()
{
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBTable table = getDatabase().table( getTableName() );
        RWDBUpdater updater = table.updater();

        updater.where( table["routeid"] == RouteID );

        updater << table["name"].assign(getName()) << table["type"].assign(getType());

        updater.execute( conn );
    }
}

void CtiTableRoute::Delete()
{
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBTable table = getDatabase().table( getTableName() );
        RWDBDeleter deleter = table.deleter();

        deleter.where( table["routeid"] == RouteID );

        deleter.execute( conn );
    }
}

void CtiTableRoute::Restore()
{
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBTable table = getDatabase().table( getTableName() );
        RWDBSelector selector = getDatabase().selector();

        selector << table["routeid"] << table["name"] << table["type"];
        selector.where( table["routeid"] == RouteID );

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
}

RWCString CtiTableRoute::getTableName() const
{
    return "Route";
}

void CtiTableRoute::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE) 
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    //rdr >> RouteID >> Name >> rwsTemp;

    rdr["routeid"] >> RouteID;
    rdr["name"]    >> Name;
    rdr["type"]    >> rwsTemp;

    Type = resolveRouteType(rwsTemp);
}
