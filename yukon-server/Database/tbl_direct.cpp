#include "precompiled.h"

#include "tbl_direct.h"
#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"

CtiTableDeviceDirectComm::CtiTableDeviceDirectComm() :
_deviceID(-1),
PortID(-1)
{}

CtiTableDeviceDirectComm::~CtiTableDeviceDirectComm()
{
}

LONG CtiTableDeviceDirectComm::getPortID() const
{

    return PortID;
}

void CtiTableDeviceDirectComm::setPortID(LONG id)
{

    PortID = id;
}

void CtiTableDeviceDirectComm::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["deviceid"] >> _deviceID;
    rdr["portid"] >> PortID;
}

LONG CtiTableDeviceDirectComm::getDeviceID() const
{

    return _deviceID;
}

std::string CtiTableDeviceDirectComm::getTableName()
{
    return "DeviceDirectCommSettings";
}

CtiTableDeviceDirectComm& CtiTableDeviceDirectComm::setDeviceID( const LONG deviceID)
{

    _deviceID = deviceID;
    return *this;
}
