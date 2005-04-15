/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_emetcon
*
* Date:   8/13/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_emetcon.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/04/15 18:28:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_dv_emetcon.h"
#include "logger.h"

CtiTableEmetconLoadGroup::CtiTableEmetconLoadGroup() :
_deviceID(-1),
_silver(0),
_gold(0),
_addressUsage(SILVERADDRESS),
_relay(Invalid_Relay),
_routeID(-1)
{}

CtiTableEmetconLoadGroup::CtiTableEmetconLoadGroup(const CtiTableEmetconLoadGroup& aRef)
{
    *this = aRef;
}

CtiTableEmetconLoadGroup::~CtiTableEmetconLoadGroup() {}

CtiTableEmetconLoadGroup& CtiTableEmetconLoadGroup::operator=(const CtiTableEmetconLoadGroup& aRef)
{
    if(this != &aRef)
    {
        _deviceID      = aRef.getDeviceID();
        _silver        = aRef.getSilver();
        _gold          = aRef.getGold();
        _addressUsage  = aRef.getAddressUsage();
        _relay         = aRef.getRelay();
        _routeID       = aRef.getRouteID();
    }
    return *this;
}

INT CtiTableEmetconLoadGroup::getEmetconAddress() const
{
    INT address;



    if(_addressUsage == GOLDADDRESS)
    {
        address = _gold;
    }
    else
    {
        address = _silver;
    }

    return address;
}

INT  CtiTableEmetconLoadGroup::getSilver() const
{

    return _silver;
}

CtiTableEmetconLoadGroup& CtiTableEmetconLoadGroup::setSilver( const INT a_silver )
{

    _silver = a_silver;
    return *this;
}

INT  CtiTableEmetconLoadGroup::getGold() const
{

    return _gold;
}

CtiTableEmetconLoadGroup& CtiTableEmetconLoadGroup::setGold( const INT a_gold )
{

    _gold = a_gold;
    return *this;
}

INT  CtiTableEmetconLoadGroup::getAddressUsage() const
{

    return _addressUsage;
}

CtiTableEmetconLoadGroup& CtiTableEmetconLoadGroup::setAddressUsage( const INT a_addressUsage )
{

    _addressUsage = a_addressUsage;
    return *this;
}

INT  CtiTableEmetconLoadGroup::getRelay() const
{

    return _relay;
}

CtiTableEmetconLoadGroup& CtiTableEmetconLoadGroup::setRelay( const INT a_relay )
{
    _relay = a_relay;
    return *this;
}

LONG  CtiTableEmetconLoadGroup::getRouteID() const
{

    return _routeID;
}

CtiTableEmetconLoadGroup& CtiTableEmetconLoadGroup::setRouteID( const LONG a_routeID )
{

    _routeID = a_routeID;
    return *this;
}

LONG  CtiTableEmetconLoadGroup::getDeviceID() const
{

    return _deviceID;
}

CtiTableEmetconLoadGroup& CtiTableEmetconLoadGroup::setDeviceID( const LONG deviceID )
{

    _deviceID = deviceID;
    return *this;
}


RWCString CtiTableEmetconLoadGroup::getTableName()
{
    return "LMGroupEmetcon";
}

void CtiTableEmetconLoadGroup::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName() );

    selector <<
    devTbl["goldaddress"] <<
    devTbl["silveraddress"] <<
    devTbl["addressusage"] <<
    devTbl["relayusage"] <<
    devTbl["routeid"];

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
    // selector.where( selector.where() && keyTable["deviceid"] == devTbl["deviceid"] );
}

void CtiTableEmetconLoadGroup::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString rwsTemp;



    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & DEBUGLEVEL_DATABASE) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["goldaddress"]   >> _gold;
    rdr["silveraddress"] >> _silver;
    rdr["routeid"]       >> _routeID;

    rdr["addressusage"] >> rwsTemp;
    rwsTemp.toLower();
    _addressUsage = ((rwsTemp == 'g') ? GOLDADDRESS : SILVERADDRESS);

    rdr["relayusage"] >> rwsTemp;
    _relay = resolveRelayUsage(rwsTemp);

    // Make these guys right with a binary world;
    _silver -= 1;     // Silver is 0 through 59
    _gold   += 59;    // Gold is 60 - 63
}

RWDBStatus CtiTableEmetconLoadGroup::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"] <<
    table["goldaddress"] <<
    table["silveraddress"] <<
    table["routeid"];

    selector.where( table["deviceid"] == getDeviceID() );

    RWDBReader reader = selector.reader( conn );

    if( reader() )
    {
        DecodeDatabaseReader( reader );
        setDirty( false );
    }
    else
    {
        setDirty( true );
    }
    return reader.status();
}

RWDBStatus CtiTableEmetconLoadGroup::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID() <<
    getGold() <<
    getSilver() <<
    getRouteID();

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableEmetconLoadGroup::Update()
{
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["goldaddress"].assign(getGold() ) <<
    table["silveraddress"].assign(getSilver() ) <<
    table["routeid"].assign(getRouteID() );

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableEmetconLoadGroup::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}

