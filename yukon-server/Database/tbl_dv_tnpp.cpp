/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_tnpp
*
* Date:   6/28/2005
*
* Author : Jess Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_tnpp.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/12/20 17:16:06 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_dv_tnpp.h"
#include "logger.h"

#include "database_connection.h"
#include "database_writer.h"

CtiTableDeviceTnpp::CtiTableDeviceTnpp() :
    _inertia(2),
    _destinationAddress(0),
    _deviceID(0),
    _originAddress(0)
{}

CtiTableDeviceTnpp::CtiTableDeviceTnpp(const CtiTableDeviceTnpp& aRef)
{
    *this = aRef;
}

CtiTableDeviceTnpp::~CtiTableDeviceTnpp()
{}

CtiTableDeviceTnpp& CtiTableDeviceTnpp::operator=(const CtiTableDeviceTnpp& aRef)
{


    if(this != &aRef)
    {
        _deviceID =           aRef.getDeviceID();
        _inertia =            aRef.getInertia();
        _destinationAddress = aRef.getDestinationAddress(); //The tnpp devices address
        _originAddress     =  aRef.getOriginAddress();
        /*_identifierFormat =   aRef.getIdentifierFormat();
        _pagerProtocol =      aRef.getPagerProtocol();
        _dataFormat =         aRef.getPagerDataFormat();
        _channel =            aRef.getChannel();
        _zone =               aRef.getZone();
        _functionCode =       aRef.getFunctionCode();
        _pagerID =            aRef.getPagerID();*/ //FIX_ME JESS
    }
    return *this;
}

void CtiTableDeviceTnpp::DecodeDatabaseReader(Cti::RowReader &rdr)
{

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"]           >>   _deviceID;
    rdr["inertia"]            >>   _inertia;
    rdr["destinationaddress"] >>   _destinationAddress;
    rdr["originaddress"]      >>   _originAddress;
    rdr["identifierformat"]   >>   _identifierFormat;
    rdr["protocol"]           >>   _pagerProtocol;
    rdr["dataformat"]         >>   _dataFormat;
    rdr["channel"]            >>   _channel;
    rdr["zone"]               >>   _zone;
    rdr["functioncode"]       >>   _functionCode;
    rdr["pagerid"]            >>   _pagerID;

}

string CtiTableDeviceTnpp::getTableName()
{
    return "DeviceTNPPSettings";
}

LONG CtiTableDeviceTnpp::getDeviceID() const
{

    return _deviceID;
}

bool CtiTableDeviceTnpp::Insert()
{
    static const std::string sql = "insert into " + getTableName() + " (deviceid) values (?)";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter << getDeviceID();

    bool success = inserter.execute();

    if ( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableDeviceTnpp::Update()
{
#if 0
    static const std::string sql = "update " + getTableName() + "  ~set some stuff here~  where deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
//        << getPagerNumber()
        << getDeviceID();

    bool success = updater.execute();
#endif

    bool success = true;

    if ( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableDeviceTnpp::Delete()
{
    static const std::string sql = "delete from " + getTableName() + " where deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << getDeviceID();

    return deleter.execute();
}

unsigned short CtiTableDeviceTnpp::getInertia() const
{
    return _inertia;
}

int CtiTableDeviceTnpp::getDestinationAddress() const
{ //The tnpp devices address
    return _destinationAddress;
}

int CtiTableDeviceTnpp::getOriginAddress() const
{ //The Computer's address
    return _originAddress;
}

string CtiTableDeviceTnpp::getIdentifierFormat()
{
    return _identifierFormat;
}

string CtiTableDeviceTnpp::getPagerProtocol()
{
    return _pagerProtocol;
}

string CtiTableDeviceTnpp::getPagerDataFormat()
{
    return _dataFormat;
}

string CtiTableDeviceTnpp::getChannel()
{
    return _channel;
}

string CtiTableDeviceTnpp::getZone()
{
    return _zone;
}

string CtiTableDeviceTnpp::getFunctionCode()
{
    return _functionCode;
}

string CtiTableDeviceTnpp::getPagerID()
{
    return _pagerID;
}
