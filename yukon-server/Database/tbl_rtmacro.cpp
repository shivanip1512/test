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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_rtmacro.h"
#include "database_connection.h"
#include "database_reader.h"

using std::string;
using std::endl;

CtiTableMacroRoute::CtiTableMacroRoute(LONG routeId, INT routeOrder) :
    RouteID(0),
    _singleRouteID(0),
    RouteOrder(0)
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

string CtiTableMacroRoute::getSQLCoreStatement()
{
    static const string sql =  "SELECT RT.routeid, MR.singlerouteid, MR.routeorder "
                               "FROM Route RT, MacroRoute MR "
                               "WHERE RT.routeid = MR.routeid ORDER BY MR.routeid ASC, MR.routeorder ASC";

    return sql;
}

void CtiTableMacroRoute::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["routeid"]       >> RouteID;
    rdr["singlerouteid"] >> _singleRouteID;
    rdr["routeorder"]    >> RouteOrder;
}

string CtiTableMacroRoute::getTableName()
{
    return "MacroRoute";
}
