/*-----------------------------------------------------------------------------*
*
* File:   tbl_rtcarrier
*
* Date:   8/16/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_rtcarrier.cpp-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2007/06/27 17:31:53 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
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

CtiTableCarrierRoute::CtiTableCarrierRoute(const CtiTableCarrierRoute& aRef)
{
    *this = aRef;
}

CtiTableCarrierRoute::~CtiTableCarrierRoute()
{
}

CtiTableCarrierRoute& CtiTableCarrierRoute::operator=(const CtiTableCarrierRoute& aRef)
{
    if(this != &aRef)
    {
        _routeID          = aRef.getRouteID();
        Bus               = aRef.getBus();
        CCUFixBits        = aRef.getCCUFixBits();
        CCUVarBits        = aRef.getCCUVarBits();
        _userLocked       = aRef.getUserLocked();
        _resetRPTSettings = aRef.getResetRPTSettings();
    }
    return *this;
}

void CtiTableCarrierRoute::DumpData()
{
    CtiLockGuard<CtiLogger> logger_guard(dout);
    dout << " Bus Number                                 " << Bus        << endl;
    dout << " CCU Fixed Bits                             " << CCUFixBits << endl;
    dout << " CCU Variable Bits                          " << CCUVarBits << endl;
    dout << " User Locked ?                              " << _userLocked << endl;
    dout << " Reset RPT Settings ?                       " << _resetRPTSettings << endl;
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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

