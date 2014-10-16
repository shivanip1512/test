#include "precompiled.h"

#include "tbl_dv_ied.h"
#include "logger.h"

#include "utility.h"
#include "database_connection.h"
#include "database_writer.h"

using std::string;
using std::endl;

CtiTableDeviceIED::CtiTableDeviceIED() :
_deviceID(-1),
_slaveAddress(0)
{
}

CtiTableDeviceIED::~CtiTableDeviceIED() {}

INT CtiTableDeviceIED::getSlaveAddress() const
{

    return _slaveAddress;
}

INT& CtiTableDeviceIED::getSlaveAddress()
{

    return _slaveAddress;
}

void CtiTableDeviceIED::setSlaveAddress(INT &aInt)
{
    _slaveAddress = aInt;
}

string CtiTableDeviceIED::getPassword() const
{

    return _password;
}

string& CtiTableDeviceIED::getPassword()
{

    return _password;
}

void CtiTableDeviceIED::setPassword(string &aStr)
{
    _password = aStr;
}

void CtiTableDeviceIED::DecodeDatabaseReader(const INT DeviceType, Cti::RowReader &rdr)
{
    string temp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["password"]         >> temp;

    if(temp.empty() ||
       !temp.compare("0") ||
       ciStringEqual(temp, "none") ||
       ciStringEqual(temp, "(none)"))
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

