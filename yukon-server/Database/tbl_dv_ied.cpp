#include "yukon.h"

#include "tbl_dv_ied.h"
#include "logger.h"

#include "utility.h"
#include "database_connection.h"
#include "database_writer.h"

CtiTableDeviceIED::CtiTableDeviceIED() :
_deviceID(-1),
_slaveAddress(0)
{
}

CtiTableDeviceIED::CtiTableDeviceIED(const CtiTableDeviceIED& aRef)
{
    *this = aRef;
}

CtiTableDeviceIED::~CtiTableDeviceIED() {}

CtiTableDeviceIED& CtiTableDeviceIED::operator=(const CtiTableDeviceIED& aRef)
{

    if(this != &aRef)
    {
        _deviceID      = aRef.getDeviceID();
        _password      = aRef.getPassword();
        _slaveAddress  = aRef.getSlaveAddress();
    }
    return *this;
}

INT CtiTableDeviceIED::getSlaveAddress() const
{

    return _slaveAddress;
}

INT& CtiTableDeviceIED::getSlaveAddress()
{

    return _slaveAddress;
}

CtiTableDeviceIED CtiTableDeviceIED::setSlaveAddress(INT &aInt)
{

    _slaveAddress = aInt;
    return *this;
}

string CtiTableDeviceIED::getPassword() const
{

    return _password;
}

string& CtiTableDeviceIED::getPassword()
{

    return _password;
}

CtiTableDeviceIED CtiTableDeviceIED::setPassword(string &aStr)
{

    _password = aStr;
    return *this;
}

void CtiTableDeviceIED::DecodeDatabaseReader(const INT DeviceType, Cti::RowReader &rdr)
{
    string temp;


    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["password"]         >> temp;

    if(temp.empty() ||
       !temp.compare("0") ||
       string_equal(temp, "none") ||
       string_equal(temp, "(none)"))
    {
        _password = string();
    }
    else
    {
        _password = temp;
    }
    rdr["slaveaddress"]     >> temp;

    _slaveAddress = resolveSlaveAddress(DeviceType, temp);
}

string CtiTableDeviceIED::getTableName()
{
    return "DeviceIED";
}

LONG CtiTableDeviceIED::getDeviceID() const
{

    return _deviceID;
}

CtiTableDeviceIED& CtiTableDeviceIED::setDeviceID( const LONG deviceID )
{

    _deviceID = deviceID;
    return *this;
}

