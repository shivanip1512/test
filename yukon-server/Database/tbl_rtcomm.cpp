#include "precompiled.h"

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

CtiTableCommRoute::~CtiTableCommRoute()
{}

std::string CtiTableCommRoute::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableCommRoute";
    itemList.add("Route ID")      << _routeID;
    itemList.add("Device ID")     << DeviceID;
    itemList.add("Default Route") << DefaultRoute;

    return itemList.toString();
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
    string defaultRouteStr;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["routeid"] >> _routeID;
    rdr["deviceid"] >> DeviceID;

    rdr["defaultroute"] >> defaultRouteStr;
    std::transform(defaultRouteStr.begin(), defaultRouteStr.end(), defaultRouteStr.begin(), tolower);
    DefaultRoute = ((defaultRouteStr == "y") ? TRUE : FALSE);
}

string CtiTableCommRoute::getTableName()
{
    return "Route";
}
