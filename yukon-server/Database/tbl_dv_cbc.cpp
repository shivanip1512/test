#include "yukon.h"

#include "tbl_dv_cbc.h"
#include "logger.h"
#include "database_connection.h"
#include "database_writer.h"

CtiTableDeviceCBC::CtiTableDeviceCBC() :
_deviceID(-1),
_serial(0),
_routeID(-1)
{}

CtiTableDeviceCBC::CtiTableDeviceCBC(const CtiTableDeviceCBC& aRef)
{
    *this = aRef;
}

CtiTableDeviceCBC::~CtiTableDeviceCBC()
{}

CtiTableDeviceCBC& CtiTableDeviceCBC::operator=(const CtiTableDeviceCBC& aRef)
{
    if(this != &aRef)
    {
        _deviceID = aRef.getDeviceID();
        _serial = aRef.getSerial();
        _routeID = aRef.getRouteID();
    }
    return *this;
}

INT  CtiTableDeviceCBC::getSerial() const
{

    return _serial;
}

CtiTableDeviceCBC& CtiTableDeviceCBC::setSerial( const INT a_ser )
{

    _serial = a_ser;
    return *this;
}

LONG  CtiTableDeviceCBC::getRouteID() const
{

    return _routeID;
}

CtiTableDeviceCBC& CtiTableDeviceCBC::setRouteID( const LONG a_routeID )
{

    _routeID = a_routeID;
    return *this;
}

void CtiTableDeviceCBC::DecodeDatabaseReader(Cti::RowReader &rdr)
{

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["serialnumber"]     >> _serial;
    rdr["routeid"]          >> _routeID;
}

LONG CtiTableDeviceCBC::getDeviceID() const
{

    return _deviceID;
}

string CtiTableDeviceCBC::getTableName()
{
    return "DeviceCBC";
}

CtiTableDeviceCBC& CtiTableDeviceCBC::setDeviceID( const LONG deviceID)
{

    _deviceID = deviceID;
    return *this;
}

