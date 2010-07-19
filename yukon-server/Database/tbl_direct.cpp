/*-----------------------------------------------------------------------------*
*
* File:   tbl_direct
*
* Date:   8/7/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_direct.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2005/12/20 17:16:05 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
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
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["portid"] >> PortID;
}

bool CtiTableDeviceDirectComm::Insert()
{
    static const std::string sql = "insert into " + getTableName() + " values (?, ?)";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter 
        << getDeviceID()
        << getPortID();

    bool success = inserter.execute();

    if ( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableDeviceDirectComm::Update()
{
    static const std::string sql = "update " + getTableName() +
                                   " set "
                                        "phonenumber = ?"
                                   " where "
                                        "deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater 
        << getPortID()
        << getDeviceID();

    bool success = updater.execute();

    if ( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableDeviceDirectComm::Delete()
{
    static const std::string sql = "delete from " + getTableName() + " where deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << getDeviceID();

    return deleter.execute();
}

LONG CtiTableDeviceDirectComm::getDeviceID() const
{

    return _deviceID;
}

string CtiTableDeviceDirectComm::getTableName()
{
    return "DeviceDirectCommSettings";
}

CtiTableDeviceDirectComm& CtiTableDeviceDirectComm::setDeviceID( const LONG deviceID)
{

    _deviceID = deviceID;
    return *this;
}
