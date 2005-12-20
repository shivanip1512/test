#include "yukon.h"



/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_scandata
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_scandata.cpp-arc  $
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2005/12/20 17:16:06 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dbaccess.h"
#include "logger.h"
#include "tbl_dv_scandata.h"
#include "rwutil.h"

CtiTableDeviceScanData::CtiTableDeviceScanData(LONG did) :
lastFreezeNumber(0),
prevFreezeNumber(0),
lastLPTime(CtiTime( CtiTime() - (30 * 24 * 3600) )),       // Thirty days ago.
lastFreezeTime((unsigned)1985, (unsigned)1, (unsigned)1),
prevFreezeTime((unsigned)1985, (unsigned)1, (unsigned)1),
_deviceID(did)
{
    for(int i=0; i < ScanRateInvalid; i++)
    {
        _nextScan[i] = (CtiTime)CtiTime(YUKONEOT);
        _lastCommunicationTime[i] = CtiTime(YUKONEOT);
    }

}

CtiTableDeviceScanData::~CtiTableDeviceScanData()
{
    if( isDirty() )
    {
        if(Update().errorCode() != RWDBStatus::ok)
        {
            Insert();
        }
    }
}


LONG CtiTableDeviceScanData::getDeviceID() const
{
    return _deviceID;
}
CtiTableDeviceScanData& CtiTableDeviceScanData::setDeviceID(LONG id)
{
    _deviceID = id;
    return *this;
}

CtiTime CtiTableDeviceScanData::getNextScan(INT a) const
{
    CtiTime tm = _nextScan[a];
    return tm;
}

CtiTableDeviceScanData& CtiTableDeviceScanData::setNextScan(INT a, const CtiTime &b)
{
    _nextScan[a] = CtiTime(b);
    return *this;
}

CtiTime CtiTableDeviceScanData::nextNearestTime(int maxrate) const
{
    CtiTime Win = CtiTime( CtiTime(YUKONEOT) );

    for(int i = 0; i < maxrate; i++)
    {
        if(_nextScan[i] < Win) Win = _nextScan[i];
    }

    return Win;
}

LONG  CtiTableDeviceScanData::getLastFreezeNumber() const
{
    return lastFreezeNumber;
}
LONG& CtiTableDeviceScanData::getLastFreezeNumber()
{
    return lastFreezeNumber;
}
CtiTableDeviceScanData& CtiTableDeviceScanData::setLastFreezeNumber( const LONG aLastFreezeNumber )
{
    setDirty(true);
    lastFreezeNumber = aLastFreezeNumber;
    return *this;
}

LONG  CtiTableDeviceScanData::getPrevFreezeNumber() const
{
    return prevFreezeNumber;
}
LONG& CtiTableDeviceScanData::getPrevFreezeNumber()
{
    return prevFreezeNumber;
}
CtiTableDeviceScanData& CtiTableDeviceScanData::setPrevFreezeNumber( const LONG aPrevFreezeNumber )
{
    setDirty(true);
    prevFreezeNumber = aPrevFreezeNumber;
    return *this;
}

CtiTime  CtiTableDeviceScanData::getLastFreezeTime() const
{
    return lastFreezeTime;
}
CtiTableDeviceScanData& CtiTableDeviceScanData::setLastFreezeTime( const CtiTime& aLastFreezeTime )
{
    setDirty(true);
    lastFreezeTime = aLastFreezeTime;
    return *this;
}

CtiTime  CtiTableDeviceScanData::getLastLPTime() const
{
    return lastLPTime;
}
CtiTableDeviceScanData& CtiTableDeviceScanData::setLastLPTime( const CtiTime& aLastTime )
{
    setDirty(true);
    lastLPTime = aLastTime;
    return *this;
}

CtiTime  CtiTableDeviceScanData::getPrevFreezeTime() const
{
    return prevFreezeTime;
}
CtiTableDeviceScanData& CtiTableDeviceScanData::setPrevFreezeTime( const CtiTime& aPrevFreezeTime )
{
    setDirty(true);
    prevFreezeTime = CtiTime(aPrevFreezeTime);
    return *this;
}



string CtiTableDeviceScanData::getTableName() const
{
    return "DynamicDeviceScanData";
}

RWDBStatus CtiTableDeviceScanData::Restore()
{
    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"] <<
    table["lastfreezetime"] <<
    table["prevfreezetime"] <<
    table["lastlptime"] <<
    table["lastfreezenumber"] <<
    table["prevfreezenumber"];

    for(int i = 0; i <= ScanRateIntegrity; i++)
    {
        sprintf(temp, "nextscan%d", i);
        selector.select( table[ temp ] );
    }

    selector.where( table["deviceid"] == getDeviceID() );

    if( selector.execute( conn ).status().errorCode() != RWDBStatus::ok )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << selector.asString() << endl;
        }
    }

    RWDBReader reader = selector.reader();

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

RWDBStatus CtiTableDeviceScanData::Update(RWDBConnection &conn)
{
    char temp[32];

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["lastfreezetime"].assign(toRWDBDT(getLastFreezeTime())) <<
    table["prevfreezetime"].assign(toRWDBDT(getPrevFreezeTime())) <<
    table["lastlptime"].assign(toRWDBDT(getLastLPTime())) <<
    table["lastfreezenumber"].assign(getLastFreezeNumber()) <<
    table["prevfreezenumber"].assign(getPrevFreezeNumber());

    for(int i = 0; i <= ScanRateIntegrity; i++)
    {
        sprintf(temp, "nextscan%d", i);
        updater.set( table[ temp ].assign( toRWDBDT(getNextScan(i)) ) );
    }

    if( ExecuteUpdater(conn,updater,__FILE__,__LINE__) == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableDeviceScanData::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    return Update(conn);
}

RWDBStatus CtiTableDeviceScanData::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID() <<
    (CtiTime)getLastFreezeTime() <<
    (CtiTime)getPrevFreezeTime() <<
    (CtiTime)getLastLPTime() <<
    getLastFreezeNumber() <<
    getPrevFreezeNumber();

    for(int i = 0; i <= ScanRateIntegrity; i++)
    {
        inserter << (CtiTime)getNextScan(i);
    }

    if( ExecuteInserter(conn,inserter,__FILE__,__LINE__).errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableDeviceScanData::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}

void CtiTableDeviceScanData::DecodeDatabaseReader(RWDBReader& rdr )
{
    char temp[32];
    CtiTime adt;

    rdr["deviceid"] >> _deviceID;
    rdr["lastfreezetime"] >> lastFreezeTime;
    rdr["prevfreezetime"] >> prevFreezeTime;
    rdr["lastlptime"] >> lastLPTime;
    rdr["lastfreezenumber"] >> lastFreezeNumber;
    rdr["prevfreezenumber"] >> prevFreezeNumber;

#if 0   // 11/15/01 CGP Breakage??
    for(int i = 0; i <= ScanRateIntegrity; i++)
    {
        sprintf(temp, "nextscan%d", i);
        rdr[ temp ] >> adt;
        setNextScan(i, adt);
    }
#endif

}

CtiTableDeviceScanData::CtiTableDeviceScanData(const CtiTableDeviceScanData& aRef)
{
    *this = aRef;
}

CtiTableDeviceScanData::~CtiTableDeviceScanData();


CtiTableDeviceScanData& CtiTableDeviceScanData::operator=(const CtiTableDeviceScanData& aRef)
{
    if(this != &aRef)
    {
        setDeviceID( aRef.getDeviceID() );

        setLastFreezeTime( aRef.getLastFreezeTime() );
        setPrevFreezeTime( aRef.getPrevFreezeTime() );
        setLastLPTime( aRef.getLastLPTime() );
        setLastFreezeNumber( aRef.getLastFreezeNumber() );

        for(int i=0; i < ScanRateInvalid; i++)
        {
            _nextScan[i] = aRef.getNextScan(i);
            _lastCommunicationTime[i] = aRef.getLastCommunicationTime(i);
        }
    }

    return *this;
}

CtiTime CtiTableDeviceScanData::getLastCommunicationTime(int i) const
{
    return _lastCommunicationTime[i];
}

CtiTableDeviceScanData& CtiTableDeviceScanData::setLastCommunicationTime( int i, const CtiTime& tme )
{
    _lastCommunicationTime[i] = tme;
    return *this;
}


