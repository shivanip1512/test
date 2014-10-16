#include "precompiled.h"

#include "tbl_carrier.h"
#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"

using std::string;
using std::endl;

CtiTableDeviceCarrier::CtiTableDeviceCarrier() :
_deviceID(-1),
_address(-1)
{
}

CtiTableDeviceCarrier::~CtiTableDeviceCarrier()
{
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
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
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
