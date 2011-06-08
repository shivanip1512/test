#include "yukon.h"

#include "tbl_direct.h"
#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"

CtiTableDeviceDirectComm::CtiTableDeviceDirectComm() :
_deviceID(-1),
PortID(-1)
{}

CtiTableDeviceDirectComm::CtiTableDeviceDirectComm(const CtiTableDeviceDirectComm &aRef)
{
    *this = aRef;
}

CtiTableDeviceDirectComm::~CtiTableDeviceDirectComm()
{
}

CtiTableDeviceDirectComm& CtiTableDeviceDirectComm::operator=(const CtiTableDeviceDirectComm &aRef)
{
    RWMutexLock::LockGuard  guard(DirectCommMux);
    if(this != &aRef)
    {
        _deviceID   = aRef.getDeviceID();
        PortID      = aRef.getPortID();
    }

    return *this;
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
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
