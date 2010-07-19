/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_tappaging
*
* Date:   8/14/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_tappaging.cpp-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2005/12/20 17:16:06 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_dv_tappaging.h"
#include "logger.h"

#include "database_connection.h"
#include "database_writer.h"

CtiTableDeviceTapPaging::CtiTableDeviceTapPaging(string pn) :
_deviceID(-1),
_pagerNumber(pn)
{}

CtiTableDeviceTapPaging::CtiTableDeviceTapPaging(const CtiTableDeviceTapPaging& aRef)
{
    *this = aRef;
}

CtiTableDeviceTapPaging::~CtiTableDeviceTapPaging()
{}

CtiTableDeviceTapPaging& CtiTableDeviceTapPaging::operator=(const CtiTableDeviceTapPaging& aRef)
{


    if(this != &aRef)
    {
        _deviceID    = aRef.getDeviceID();
        _pagerNumber = aRef.getPagerNumber();
        _senderID = aRef.getSenderID();
        _securityCode = aRef.getSecurityCode();
        _postPath = aRef.getPOSTPath();
    }
    return *this;
}

string CtiTableDeviceTapPaging::getPagerNumber() const
{

    return _pagerNumber;
}

string& CtiTableDeviceTapPaging::getPagerNumber()
{

    return _pagerNumber;
}

CtiTableDeviceTapPaging&   CtiTableDeviceTapPaging::setPagerNumber(const string &aStr)
{


    _pagerNumber = aStr;
    return *this;
}

void CtiTableDeviceTapPaging::DecodeDatabaseReader(Cti::RowReader &rdr)
{

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["pagernumber"] >> _pagerNumber;
    rdr["sender"] >> _senderID;
    rdr["securitycode"] >> _securityCode;
    rdr["postpath"] >> _postPath;
    if( _securityCode.find("none")!=string::npos || _securityCode.compare("0")==string::npos )
    {
        _securityCode = string();    // Make it NULL.
    }
}

string CtiTableDeviceTapPaging::getTableName()
{
    return "DeviceTapPagingSettings";
}

LONG CtiTableDeviceTapPaging::getDeviceID() const
{

    return _deviceID;
}

CtiTableDeviceTapPaging& CtiTableDeviceTapPaging::setDeviceID( const LONG deviceID )
{

    _deviceID = deviceID;
    return *this;
}


bool CtiTableDeviceTapPaging::Insert()
{
    static const std::string sql = "insert into " + getTableName() + " (deviceid, pagernumber) values (?, ?)";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter
        << getDeviceID()
        << getPagerNumber();

    bool success = inserter.execute();

    if ( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableDeviceTapPaging::Update()
{
    static const std::string sql = "update " + getTableName() + " set pagernumber = ? where deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << getPagerNumber()
        << getDeviceID();

    bool success = updater.execute();

    if ( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableDeviceTapPaging::Delete()
{
    static const std::string sql = "delete from " + getTableName() + " where deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << getDeviceID();

    return deleter.execute();
}


string CtiTableDeviceTapPaging::getSenderID() const
{
    return _senderID;
}

string CtiTableDeviceTapPaging::getSecurityCode() const
{
    return _securityCode;
}
string CtiTableDeviceTapPaging::getPOSTPath() const
{
    return _postPath;
}


