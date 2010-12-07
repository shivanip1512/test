#include "yukon.h"

#include "tbl_carrier.h"
#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"

CtiTableDeviceCarrier::CtiTableDeviceCarrier() :
_deviceID(-1),
_address(-1)
{
}

CtiTableDeviceCarrier::CtiTableDeviceCarrier(const CtiTableDeviceCarrier& aRef)
{
    *this = aRef;
}

CtiTableDeviceCarrier::~CtiTableDeviceCarrier()
{
}

CtiTableDeviceCarrier& CtiTableDeviceCarrier::operator=(const CtiTableDeviceCarrier& aRef)
{
    if(this != &aRef)
    {
        _deviceID = aRef.getDeviceID();
        _address = aRef.getAddress();
    }
    return *this;
}

INT CtiTableDeviceCarrier::getAddress() const
{
    return _address;
}

CtiTableDeviceCarrier& CtiTableDeviceCarrier::setAddress( const INT aAddress )
{
    _address = aAddress;
    return *this;
}

string CtiTableDeviceCarrier::getTableName()
{
    return "DeviceCarrierSettings";
}

void CtiTableDeviceCarrier::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["paobjectid"] >> _deviceID;
    rdr["address"] >> _address;
}

LONG CtiTableDeviceCarrier::getDeviceID() const
{

    return _deviceID;
}

CtiTableDeviceCarrier& CtiTableDeviceCarrier::setDeviceID( const LONG deviceID )
{

    _deviceID = deviceID;
    return *this;
}

bool CtiTableDeviceCarrier::isInitialized()
{
    return ( (getAddress() >= 0) && (getDeviceID() >= 0) );
}
