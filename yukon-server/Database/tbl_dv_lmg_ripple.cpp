/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_lmg_ripple
*
* Date:   8/13/2001
*
* Author : Eric Schmit     **didn't add to project**
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_lmg_ripple.cpp-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2005/12/20 17:16:06 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <assert.h>
#include "logger.h"
#include "tbl_dv_lmg_ripple.h"

#include "rwutil.h"

CtiTableRippleLoadGroup::CtiTableRippleLoadGroup() :
_controlBits((size_t)7, (char)0),      // 7 zeros
_restoreBits((size_t)7, (char)0),      // 7 zeros
_routeID(-1)
{}

CtiTableRippleLoadGroup::CtiTableRippleLoadGroup(const CtiTableRippleLoadGroup& aRef)
{
    *this = aRef;
}

CtiTableRippleLoadGroup::~CtiTableRippleLoadGroup() {}

CtiTableRippleLoadGroup& CtiTableRippleLoadGroup::operator=(const CtiTableRippleLoadGroup& aRef)
{
    if(this != &aRef)
    {
        setDeviceID(aRef.getDeviceID());
        setControlBits(aRef.getControlBits());
        setRestoreBits(aRef.getRestoreBits());
        setRouteID(aRef.getRouteID());
        setShedTime(aRef.getShedTime());
    }
    return *this;
}

LONG  CtiTableRippleLoadGroup::getRouteID() const
{
    return _routeID;
}

CtiTableRippleLoadGroup& CtiTableRippleLoadGroup::setRouteID( const LONG a_routeID )
{
    _routeID = a_routeID;
    return *this;
}

string CtiTableRippleLoadGroup::getControlBits() const
{
    return _controlBits;
}

BYTE  CtiTableRippleLoadGroup::getControlBit(INT i)
{
    assert( i >= 0 && i < 8);
    return _controlBits[(size_t)i];
}

CtiTableRippleLoadGroup& CtiTableRippleLoadGroup::setControlBits( const string str )
{
    _controlBits = str;
    return *this;
}

CtiTableRippleLoadGroup& CtiTableRippleLoadGroup::setControlBit( INT pos, const BYTE ch )
{
    _controlBits[(size_t)pos] = ch;
    return *this;
}

string  CtiTableRippleLoadGroup::getRestoreBits() const
{
    return _restoreBits;
}

BYTE  CtiTableRippleLoadGroup::getRestoreBit(INT i)
{
    assert( i >= 0 && i < 8);
    return _restoreBits[(size_t)i];
}

CtiTableRippleLoadGroup& CtiTableRippleLoadGroup::setRestoreBits( const string str )
{
    _restoreBits = str;
    return *this;
}

CtiTableRippleLoadGroup& CtiTableRippleLoadGroup::setRestoreBit( INT pos, const BYTE ch )
{
    _restoreBits[(size_t)pos] = ch;
    return *this;
}

void CtiTableRippleLoadGroup::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName().c_str() );

    selector <<
    devTbl["shedtime"] <<
    devTbl["controlvalue"] <<
    devTbl["restorevalue"] <<
    devTbl["routeid"];

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
    // selector.where( selector.where() && keyTable["deviceid"] == devTbl["deviceid"] );
}

string CtiTableRippleLoadGroup::getTableName()
{
    return string("LMGroupRipple");
}

LONG CtiTableRippleLoadGroup::getDeviceID() const
{

    return _deviceID;
}


LONG CtiTableRippleLoadGroup::getShedTime() const
{

    return _shedTime > 0 ? _shedTime : (31536000);       // Continuous Latch devices (0 == _shedtime) will control for 1 year.
}

CtiTableRippleLoadGroup& CtiTableRippleLoadGroup::setDeviceID( const LONG deviceID)
{

    _deviceID = deviceID;
    return *this;
}

CtiTableRippleLoadGroup& CtiTableRippleLoadGroup::setShedTime( const LONG shedTime)
{

    _shedTime = shedTime;
    return *this;
}

void CtiTableRippleLoadGroup::DecodeDatabaseReader(RWDBReader &rdr)
{

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["controlvalue"] >> _controlBits;        // Fix Me -ecs
    rdr["restorevalue"] >> _restoreBits;
    rdr["shedtime"] >> _shedTime;
    rdr["routeid"] >> _routeID;
}

RWDBStatus CtiTableRippleLoadGroup::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"] <<
    table["routeid"] <<
    table["shedtime"] <<
    table["controlvalue"] <<
    table["controlvalue"] <<
    table["restorevalue"];

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

RWDBStatus CtiTableRippleLoadGroup::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID() <<
    getRouteID() <<
    getShedTime() <<
    getControlBits() <<
    getRestoreBits();

    if( ExecuteInserter(conn,inserter,__FILE__,__LINE__).errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableRippleLoadGroup::Update()
{
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["controlvalue"].assign(getControlBits().c_str() ) <<
    table["restorevalue"].assign(getRestoreBits().c_str() ) <<
    table["shedtime"].assign(getShedTime() ) <<
    table["routeid"].assign(getRouteID() );

    if( ExecuteUpdater(conn,updater,__FILE__,__LINE__) == RWDBStatus::ok )
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableRippleLoadGroup::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}


bool CtiTableRippleLoadGroup::copyMessage(BYTE *bptr, bool shed) const
{
    try
    {
        int i;
        if(shed == true)
        {
            for(i = 0; i < 50; i++)
            {
                if(_controlBits[(size_t) i ] == '1')
                {
                    int offset = i / 8;
                    int shift = (7 - i % 8);

                    bptr[ offset ] |= (0x01 << shift);
                }
            }
        }
        else
        {
            for(i = 0; i < 50; i++)
            {
                if(_restoreBits[(size_t) i ] == '1')
                {
                    int offset = i / 8;
                    int shift = (7 - i % 8);

                    bptr[ offset ] |= (0x01 << shift);
                }
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return false;
}
