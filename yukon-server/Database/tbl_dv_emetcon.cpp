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
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2005/12/20 17:16:05 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_dv_emetcon.h"
#include "logger.h"

#include "database_connection.h"
#include "database_writer.h"

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


string CtiTableEmetconLoadGroup::getTableName()
{
    return "LMGroupEmetcon";
}

void CtiTableEmetconLoadGroup::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string rwsTemp;


    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["goldaddress"]   >> _gold;
    rdr["silveraddress"] >> _silver;
    rdr["routeid"]       >> _routeID;

    rdr["addressusage"] >> rwsTemp;
    std::transform(rwsTemp.begin(), rwsTemp.end(), rwsTemp.begin(), tolower);
    _addressUsage = ((rwsTemp == "g") ? GOLDADDRESS : SILVERADDRESS);

    rdr["relayusage"] >> rwsTemp;
    _relay = resolveRelayUsage(rwsTemp.c_str());

    // Make these guys right with a binary world;
    _silver -= 1;     // Silver is 0 through 59
    _gold   += 59;    // Gold is 60 - 63
}

bool CtiTableEmetconLoadGroup::Insert()
{
    static const std::string sql = "insert into " + getTableName() +
                                   " (deviceid, goldaddress, silveraddress, routeid)"
                                   " values (?, ?, ?, ?)";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter 
        << getDeviceID()
        << getGold()
        << getSilver()
        << getRouteID();

    bool success = inserter.execute();

    if ( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableEmetconLoadGroup::Update()
{
    static const std::string sql = "update " + getTableName() +
                                   " set "
                                        "goldaddress = ?, "
                                        "silveraddress = ?, "
                                        "routeid = ?"
                                   " where "
                                        "deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater 
        << getGold()
        << getSilver()
        << getRouteID()
        << getDeviceID();

    bool success = updater.execute();

    if ( success )
    {
        setDirty(false);
    }

    return success;
}

bool CtiTableEmetconLoadGroup::Delete()
{
    static const std::string sql = "delete from " + getTableName() + " where deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << getDeviceID();

    return deleter.execute();
}

