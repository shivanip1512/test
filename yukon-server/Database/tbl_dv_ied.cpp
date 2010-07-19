/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_ied
*
* Date:   8/13/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_ied.cpp-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2006/01/03 20:23:37 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
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
       !stringCompareIgnoreCase(temp, "none") ||
       !stringCompareIgnoreCase(temp, "(none)"))
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


bool CtiTableDeviceIED::Insert()
{
    static const std::string sql = "insert into " + getTableName() + " values (?, ?, ?)";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter 
        << getDeviceID()
        << getPassword()
        << getSlaveAddress();

    bool success = inserter.execute();

    if ( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableDeviceIED::Update()
{
    static const std::string sql = "update " + getTableName() +
                                   " set "
                                        "password = ?, "
                                        "slaveaddress = ? "
                                   " where "
                                        "deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater 
        << getPassword()
        << getSlaveAddress()
        << getDeviceID();

    bool success = updater.execute();

    if ( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableDeviceIED::Delete()
{
    static const std::string sql = "delete from " + getTableName() + " where deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << getDeviceID();

    return deleter.execute();
}

