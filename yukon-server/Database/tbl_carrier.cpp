/*-----------------------------------------------------------------------------*
*
* File:   tbl_carrier
*
* Date:   8/7/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_carrier.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2005/12/20 17:16:05 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
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

bool CtiTableDeviceCarrier::Insert()
{
    static const std::string sql = "insert into " + getTableName() + " values (?, ?)";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter 
        << getDeviceID()
        << getAddress();

    bool success = inserter.execute();

    if ( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableDeviceCarrier::Update()
{
    static const std::string sql = "update " + getTableName() + " set address = ? where deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater 
        << getAddress()
        << getDeviceID();

    bool success = updater.execute();

    if ( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableDeviceCarrier::Delete()
{
    static const std::string sql = "delete from " + getTableName() + " where deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << getDeviceID();

    return deleter.execute();
}

