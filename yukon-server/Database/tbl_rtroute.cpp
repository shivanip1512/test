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
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dbaccess.h"
#include "logger.h"
#include "resolvers.h"

#include "database_connection.h"
#include "database_reader.h"

using std::string;
using std::endl;

CtiTableRoute::CtiTableRoute() :
RouteID(-1),
Type(RouteTypeInvalid)
{}

CtiTableRoute::CtiTableRoute(LONG &aRoute, string aStr, INT aType) :
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

string& CtiTableRoute::getSQLColumns(string &str)
{
    str += string("ROUTE.ROUTEID,ROUTE.NAME,ROUTE.TYPE");
    return str;
}

string& CtiTableRoute::getSQLTables(string &str)
{
    str += string("ROUTE");
    return str;
}

string& CtiTableRoute::getSQLConditions(string &str)
{
    return str;
}

void CtiTableRoute::getSQL(string &Columns, string &Tables, string &Conditions)
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

string  CtiTableRoute::getName() const                            { return Name;}

CtiTableRoute& CtiTableRoute::setName( const string aName )
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

string CtiTableRoute::getTableName() const
{
    return "Route";
}

void CtiTableRoute::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string rwsTemp;

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
