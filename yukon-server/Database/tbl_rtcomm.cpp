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
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_rtcomm.h"

#include "database_connection.h"
#include "database_reader.h"

using std::string;
using std::endl;

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

void CtiTableCommRoute::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["routeid"] >> _routeID;
    rdr["deviceid"] >> DeviceID;

    rdr["defaultroute"] >> rwsTemp;
    std::transform(rwsTemp.begin(), rwsTemp.end(), rwsTemp.begin(), tolower);
    DefaultRoute = ((rwsTemp == "y") ? TRUE : FALSE);
}

string CtiTableCommRoute::getTableName()
{
    return "Route";
}
