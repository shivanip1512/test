/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_cbc
*
* Date:   8/7/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2005/12/20 17:16:05 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
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


bool CtiTableDeviceCBC::Insert()
{
    static const std::string sql = "insert into " + getTableName() + " values (?, ?, ?)";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter 
        << getDeviceID()
        << getSerial()
        << getRouteID();

    bool success = inserter.execute();

    if ( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableDeviceCBC::Update()
{
    static const std::string sql = "update " + getTableName() +
                                   " set "
                                        "serialnumber = ?, "
                                        "routeid = ?"
                                   " where "
                                        "deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater 
        << getSerial()
        << getRouteID()
        << getDeviceID();

    bool success = updater.execute();

    if ( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableDeviceCBC::Delete()
{
    static const std::string sql = "delete from " + getTableName() + " where deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << getDeviceID();

    return deleter.execute();
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

