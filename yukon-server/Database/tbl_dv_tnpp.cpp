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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/06/29 19:49:49 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_dv_tnpp.h"
#include "logger.h"

CtiTableDeviceTnpp::CtiTableDeviceTnpp() :
_inertia(2),           
_destinationAddress(0)
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
        _identifierFormat =   aRef.getIdentifierFormat();
        _pagerProtocol =      aRef.getPagerProtocol();
        _dataFormat =         aRef.getPagerDataFormat();
        _channel =            aRef.getChannel();
        _zone =               aRef.getZone();
        _functionCode =       aRef.getFunctionCode();
        _pagerID =            aRef.getPagerID();
    }
    return *this;
}


void CtiTableDeviceTnpp::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName() );

    selector << devTbl["inertia"]
             << devTbl["destinationaddress"]
             << devTbl["identifierformat"]
             << devTbl["protocol"]
             << devTbl["dataformat"]
             << devTbl["channel"]
             << devTbl["zone"]
             << devTbl["functioncode"]
             << devTbl["pagerid"];


    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
}

void CtiTableDeviceTnpp::DecodeDatabaseReader(RWDBReader &rdr)
{

    if(getDebugLevel() & DEBUGLEVEL_DATABASE) 
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"]           >>   _deviceID;
    rdr["inertia"]            >>   _inertia;              
    rdr["destinationaddress"] >>   _destinationAddress;  
    rdr["identifierformat"]   >>   _identifierFormat;
    rdr["protocol"]           >>   _pagerProtocol;  
    rdr["dataformat"]         >>   _dataFormat;
    rdr["channel"]            >>   _channel;
    rdr["zone"]               >>   _zone;
    rdr["functioncode"]       >>   _functionCode;
    rdr["pagerid"]            >>   _pagerID;
                              
}

RWCString CtiTableDeviceTnpp::getTableName()
{
    return "DeviceTNPPSettings";
}

LONG CtiTableDeviceTnpp::getDeviceID() const
{

    return _deviceID;
}

RWDBStatus CtiTableDeviceTnpp::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"];

    selector.where( table["deviceid"] == getDeviceID() );

    RWDBReader reader = selector.reader( conn );

    if( reader() )
    {
        DecodeDatabaseReader( reader  );
        setDirty( false );
    }
    else
    {
        setDirty( true );
    }
    return reader.status();
}

RWDBStatus CtiTableDeviceTnpp::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID();

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableDeviceTnpp::Update()
{
    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableDeviceTnpp::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}

unsigned short CtiTableDeviceTnpp::getInertia() const
{
    return _inertia;
}

int CtiTableDeviceTnpp::getDestinationAddress() const
{ //The tnpp devices address
    return _destinationAddress;
}

char CtiTableDeviceTnpp::getIdentifierFormat() const
{
    return _identifierFormat;
}

char CtiTableDeviceTnpp::getPagerProtocol() const
{
    return _pagerProtocol;
}

char CtiTableDeviceTnpp::getPagerDataFormat() const
{
    return _dataFormat;
}

char CtiTableDeviceTnpp::getChannel() const
{
    return _channel;
}

char CtiTableDeviceTnpp::getZone() const
{
    return _zone;
}

char CtiTableDeviceTnpp::getFunctionCode() const
{
    return _functionCode;
}

int CtiTableDeviceTnpp::getPagerID() const
{
    return _pagerID;
}
