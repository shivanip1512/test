

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

