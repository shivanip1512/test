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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/04/15 18:28:40 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_rtcarrier.h"
#include "logger.h"

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

void CtiTableCarrierRoute::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable routetbl = db.table(getTableName());

    selector <<
    routetbl["busnumber"] <<
    routetbl["ccufixbits"] <<
    routetbl["ccuvariablebits"] <<
    routetbl["userlocked"] <<
    routetbl["resetrptsettings"];

    selector.from(routetbl);

    selector.where( selector.where() && keyTable["paobjectid"].leftOuterJoin(routetbl["routeid"]));
}

RWCString CtiTableCarrierRoute::getTableName()
{
    return "CarrierRoute";
}

void CtiTableCarrierRoute::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString rwsTemp;

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & DEBUGLEVEL_DATABASE) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["busnumber"]        >> Bus;

    // Bus will be zero based if I have anything to do with it.
    Bus--;

    rdr["routeid"] >> _routeID;
    rdr["ccufixbits"]       >> CCUFixBits;
    rdr["ccuvariablebits"]  >> CCUVarBits;
    rdr["userlocked"]  >> rwsTemp;

    rwsTemp.toLower();
    _userLocked = rwsTemp[(size_t)0] == 'y' ? true : false;


    rdr["resetrptsettings"]  >> rwsTemp;

    rwsTemp.toLower();
    _resetRPTSettings = rwsTemp[(size_t)0] == 'y' ? true : false;


}

RWDBStatus CtiTableCarrierRoute::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["routeid"] <<
    table["busnumber"] <<
    table["ccufixbits"] <<
    table["ccuvariablebits"] <<
    table["userlocked"] <<
    table["resetrptsettings"];

    selector.where( table["routeid"] == getRouteID() );

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

RWDBStatus CtiTableCarrierRoute::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getRouteID() <<
    getBus() <<
    getCCUFixBits() <<
    getCCUVarBits() <<
    getUserLocked() <<
    getResetRPTSettings();

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableCarrierRoute::Update()
{
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["routeid"] == getRouteID() );

    updater <<
    table["busnumber"].assign( getBus() ) <<
    table["ccufixbits"].assign( getCCUFixBits() ) <<
    table["ccuvariablebits"].assign(getCCUVarBits()) <<
    table["userlocked"].assign(getUserLocked()) <<
    table["resetrptsettings"].assign(getResetRPTSettings());

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableCarrierRoute::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["routeid"] == getRouteID() );
    deleter.execute( conn );
    return deleter.status();
}
