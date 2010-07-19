/*-----------------------------------------------------------------------------*
*
* File:   tbl_metergrp
*
* Date:   8/14/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_metergrp.cpp-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2008/10/14 21:25:45 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "logger.h"
#include "tbl_metergrp.h"

#include "database_connection.h"
#include "database_writer.h"

CtiTableDeviceMeterGroup::CtiTableDeviceMeterGroup():
_deviceID(-1)
{}

CtiTableDeviceMeterGroup::CtiTableDeviceMeterGroup(const CtiTableDeviceMeterGroup& aRef)
{
    *this = aRef;
}

CtiTableDeviceMeterGroup::~CtiTableDeviceMeterGroup() {}

CtiTableDeviceMeterGroup& CtiTableDeviceMeterGroup::operator=(const CtiTableDeviceMeterGroup& aRef)
{
    if(this != &aRef)
    {
        _deviceID            = aRef.getDeviceID();
        _meterNumber         = aRef.getMeterNumber();
    }
    return *this;
}

LONG CtiTableDeviceMeterGroup::getDeviceID() const
{
    return _deviceID;
}

string CtiTableDeviceMeterGroup::getMeterNumber() const
{
    return _meterNumber;
}

CtiTableDeviceMeterGroup& CtiTableDeviceMeterGroup::setMeterNumber( const string &meterNumber )
{
    _meterNumber = meterNumber;

    return *this;
}

void CtiTableDeviceMeterGroup::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["meternumber"] >> _meterNumber;
}

string CtiTableDeviceMeterGroup::getTableName()
{
    return "DeviceMeterGroup";
}


bool CtiTableDeviceMeterGroup::Insert()
{
    static const std::string sql = "insert into " + getTableName() +
                                   " values (?, ?)";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter
        << getDeviceID()
        << getMeterNumber();

    bool success = inserter.execute();

    if( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableDeviceMeterGroup::Update()
{
    static const std::string sql = "update " + getTableName() +
                                   " set "
                                        "meternumber = ?"
                                   " where "
                                        "deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << getMeterNumber()
        << getDeviceID();

    bool success = updater.execute();

    if( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableDeviceMeterGroup::Delete()
{
    static const std::string sql = "delete from " + getTableName() + " where deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << getDeviceID();

    return deleter.execute();
}


