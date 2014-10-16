#include "precompiled.h"

#include "tbl_rtcarrier.h"
#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"

using std::string;
using std::endl;

CtiTableCarrierRoute::CtiTableCarrierRoute(INT b, INT f, INT v, INT a) :
_routeID(-1),
Bus(b),
CCUFixBits(f),
CCUVarBits(v),
_userLocked(false),
_resetRPTSettings(false)
{}

CtiTableCarrierRoute::~CtiTableCarrierRoute()
{
}

std::string CtiTableCarrierRoute::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableCarrierRoute";
    itemList.add("Bus Number")           << Bus;
    itemList.add("CCU Fixed Bits")       << CCUFixBits;
    itemList.add("CCU Variable Bits")    << CCUVarBits;
    itemList.add("User Locked ?")        << (bool)_userLocked;
    itemList.add("Reset RPT Settings ?") << (bool)_resetRPTSettings;

    return itemList.toString();
}

INT  CtiTableCarrierRoute::getBus() const
{


    return Bus;
}

CtiTableCarrierRoute& CtiTableCarrierRoute::setBus( const INT aBus )
{


    Bus = aBus;
    return *this;
}

LONG CtiTableCarrierRoute::getRouteID() const
{


    return _routeID;
}

CtiTableCarrierRoute& CtiTableCarrierRoute::setRouteID( const LONG routeID )
{


    _routeID = routeID;
    return *this;
}

BOOL CtiTableCarrierRoute::getUserLocked() const
{


    return _userLocked;
}

CtiTableCarrierRoute& CtiTableCarrierRoute::setUserLocked( const BOOL userLocked )
{


    _userLocked = userLocked;
    return *this;
}

BOOL CtiTableCarrierRoute::getResetRPTSettings() const
{


    return _resetRPTSettings;
}

CtiTableCarrierRoute& CtiTableCarrierRoute::setResetRPTSettings( const BOOL reset )
{


    _resetRPTSettings = reset;
    return *this;
}

INT  CtiTableCarrierRoute::getCCUFixBits() const
{


    return CCUFixBits;
}

CtiTableCarrierRoute& CtiTableCarrierRoute::setCCUFixBits( const INT aCCUFixBit )
{


    CCUFixBits = aCCUFixBit;
    return *this;
}

INT  CtiTableCarrierRoute::getCCUVarBits() const
{


    return CCUVarBits;
}

CtiTableCarrierRoute& CtiTableCarrierRoute::setCCUVarBits( const INT aCCUVarBit )
{


    CCUVarBits = aCCUVarBit;
    return *this;
}

string CtiTableCarrierRoute::getTableName()
{
    return "CarrierRoute";
}

void CtiTableCarrierRoute::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["busnumber"]        >> Bus;

    // Bus will be zero based if I have anything to do with it.
    Bus--;

    rdr["routeid"] >> _routeID;
    rdr["ccufixbits"]       >> CCUFixBits;
    rdr["ccuvariablebits"]  >> CCUVarBits;
    rdr["userlocked"]  >> rwsTemp;

    std::transform(rwsTemp.begin(), rwsTemp.end(), rwsTemp.begin(), tolower);
    _userLocked = rwsTemp[(size_t)0] == 'y' ? true : false;

    CCUFixBits %= 32;

    rdr["resetrptsettings"]  >> rwsTemp;

    std::transform(rwsTemp.begin(), rwsTemp.end(), rwsTemp.begin(), tolower);
    _resetRPTSettings = rwsTemp[(size_t)0] == 'y' ? true : false;


}

