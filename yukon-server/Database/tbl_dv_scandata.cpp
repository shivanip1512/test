#include "yukon.h"



/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_scandata
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_scandata.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dbaccess.h"
#include "logger.h"
#include "tbl_dv_scandata.h"

CtiTableDeviceScanData::CtiTableDeviceScanData(LONG did) :
lastFreezeNumber(0),
prevFreezeNumber(0),
lastLPTime(RWTime( RWTime() - (30 * 24 * 3600) )),       // Thirty days ago.
lastFreezeTime((unsigned)1985, (unsigned)1, (unsigned)1),
prevFreezeTime((unsigned)1985, (unsigned)1, (unsigned)1),
_deviceID(did),
_flags(0)
{
    for(int i=0; i < ScanRateInvalid; i++)
    {
        _nextScan[i] = (RWDBDateTime)RWTime(YUKONEOT);
        _lastCommunicationTime[i] = RWTime(YUKONEOT);
    }

    Restore();
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

RWTime CtiTableDeviceScanData::getNextScan(INT a) const
{
    RWTime tm = _nextScan[a].rwtime();
    return tm;
}

CtiTableDeviceScanData& CtiTableDeviceScanData::setNextScan(INT a, const RWTime &b)
{
    _nextScan[a] = RWDBDateTime(b);
    return *this;
}

RWTime CtiTableDeviceScanData::nextNearestTime(int maxrate) const
{
    RWDBDateTime Win = RWDBDateTime( RWTime(YUKONEOT) );

    for(int i = 0; i < maxrate; i++)
    {
        if(_nextScan[i] < Win) Win = _nextScan[i];
    }

    return Win.rwtime();
}

INT      CtiTableDeviceScanData::clearScanFlags(INT i)
{
    return _flags = i;
}

INT      CtiTableDeviceScanData::resetScanFlags(INT i)
{
    setDirty(true);
    return _flags = i;
}

BOOL CtiTableDeviceScanData::isScanStarting() const
{
    return starting;
}
BOOL CtiTableDeviceScanData::setScanStarting(BOOL b)
{
    setDirty(true);
    return starting = b;
}
BOOL CtiTableDeviceScanData::resetScanStarting(BOOL b)
{
    return setScanStarting(b);
}

BOOL CtiTableDeviceScanData::isScanIntegrity() const
{
    return integrity;
}
BOOL CtiTableDeviceScanData::setScanIntegrity(BOOL b)
{
    setDirty(true);
    return integrity = b;
}
BOOL CtiTableDeviceScanData::resetScanIntegrity(BOOL b)
{
    return setScanIntegrity(b);
}


BOOL CtiTableDeviceScanData::isScanFrozen() const
{
    return frozen;
}
BOOL CtiTableDeviceScanData::setScanFrozen(BOOL b)
{
    setDirty(true);
    return frozen = b;
}
BOOL CtiTableDeviceScanData::resetScanFrozen(BOOL b)
{
    return setScanFrozen(b);
}

BOOL CtiTableDeviceScanData::isScanFreezePending() const
{
    return freezePending;
}
BOOL CtiTableDeviceScanData::setScanFreezePending(BOOL b)
{
    setDirty(true);
    return freezePending = b;
}
BOOL CtiTableDeviceScanData::resetScanFreezePending(BOOL b)
{
    return setScanFreezePending(b);
}

BOOL CtiTableDeviceScanData::isScanPending() const
{
    return pending;
}
BOOL CtiTableDeviceScanData::setScanPending(BOOL b)
{
    setDirty(true);
    return pending = b;
}
BOOL CtiTableDeviceScanData::resetScanPending(BOOL b)
{
    return setScanPending(b);
}

BOOL CtiTableDeviceScanData::isScanFreezeFailed() const
{
    return freezeFailed;
}
BOOL CtiTableDeviceScanData::setScanFreezeFailed(BOOL b)
{
    setDirty(true);
    return freezeFailed = b;
}
BOOL CtiTableDeviceScanData::resetScanFreezeFailed(BOOL b)
{
    return setScanFreezeFailed(b);
}

BOOL CtiTableDeviceScanData::isScanResetting() const
{
    return resetting;
}
BOOL CtiTableDeviceScanData::setScanResetting(BOOL b)
{
    setDirty(true);
    return resetting = b;
}
BOOL CtiTableDeviceScanData::resetScanResetting(BOOL b)
{
    return setScanResetting(b);
}

BOOL CtiTableDeviceScanData::isScanResetFailed() const
{
    return resetFailed;
}
BOOL CtiTableDeviceScanData::setScanResetFailed(BOOL b)
{
    setDirty(true);
    return resetFailed = b;
}
BOOL CtiTableDeviceScanData::resetScanResetFailed(BOOL b)
{
    return setScanResetFailed(b);
}

BOOL CtiTableDeviceScanData::isScanForced() const
{
    return forced;
}
BOOL CtiTableDeviceScanData::setScanForced(BOOL b)
{
    setDirty(true);
    return forced = b;
}
BOOL CtiTableDeviceScanData::resetScanForced(BOOL b)
{
    return setScanForced(b);
}

BOOL CtiTableDeviceScanData::isScanException() const
{
    return exception;
}
BOOL CtiTableDeviceScanData::setScanException(BOOL b)
{
    setDirty(true);
    return exception = b;
}
BOOL CtiTableDeviceScanData::resetScanException(BOOL b)
{
    return setScanException(b);
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

RWTime  CtiTableDeviceScanData::getLastFreezeTime() const
{
    return lastFreezeTime.rwtime();
}
CtiTableDeviceScanData& CtiTableDeviceScanData::setLastFreezeTime( const RWTime& aLastFreezeTime )
{
    setDirty(true);
    lastFreezeTime = aLastFreezeTime;
    return *this;
}

RWTime  CtiTableDeviceScanData::getLastLPTime() const
{
    return lastLPTime.rwtime();
}
CtiTableDeviceScanData& CtiTableDeviceScanData::setLastLPTime( const RWTime& aLastTime )
{
    setDirty(true);
    lastLPTime = aLastTime;
    return *this;
}

RWTime  CtiTableDeviceScanData::getPrevFreezeTime() const
{
    return prevFreezeTime.rwtime();
}
CtiTableDeviceScanData& CtiTableDeviceScanData::setPrevFreezeTime( const RWTime& aPrevFreezeTime )
{
    setDirty(true);
    prevFreezeTime = RWDBDateTime(aPrevFreezeTime);
    return *this;
}



RWCString CtiTableDeviceScanData::getTableName() const
{
    return "DynamicDeviceScanData";
}

RWDBStatus CtiTableDeviceScanData::Restore()
{
    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
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
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["lastfreezetime"].assign(getLastFreezeTime()) <<
    table["prevfreezetime"].assign(getPrevFreezeTime()) <<
    table["lastlptime"].assign(getLastLPTime()) <<
    table["lastfreezenumber"].assign(getLastFreezeNumber()) <<
    table["prevfreezenumber"].assign(getPrevFreezeNumber());

    for(int i = 0; i <= ScanRateIntegrity; i++)
    {
        sprintf(temp, "nextscan%d", i);
        updater.set( table[ temp ].assign( getNextScan(i) ) );
    }

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
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

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID() <<
    (RWDBDateTime)getLastFreezeTime() <<
    (RWDBDateTime)getPrevFreezeTime() <<
    (RWDBDateTime)getLastLPTime() <<
    getLastFreezeNumber() <<
    getPrevFreezeNumber();

    for(int i = 0; i <= ScanRateIntegrity; i++)
    {
        inserter << (RWDBDateTime)getNextScan(i);
    }

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableDeviceScanData::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}

void CtiTableDeviceScanData::DecodeDatabaseReader(RWDBReader& rdr )
{
    char temp[32];
    RWDBDateTime adt;

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
        setNextScan(i, adt.rwtime());
    }
#endif

    _flags = 0;
}

UINT  CtiTableDeviceScanData::getFlags() const
{
    return _flags;
}

BOOL CtiTableDeviceScanData::isMeterRead() const
{
    return meterread;
}
BOOL CtiTableDeviceScanData::setMeterRead(BOOL b)
{
    if(meterread != b) setDirty(true);
    return meterread = b;
}
BOOL CtiTableDeviceScanData::resetMeterRead(BOOL b)
{
    return setMeterRead(b);
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

        _flags = aRef.getFlags();
    }

    return *this;
}

RWTime CtiTableDeviceScanData::getLastCommunicationTime(int i) const
{
    return _lastCommunicationTime[i];
}

CtiTableDeviceScanData& CtiTableDeviceScanData::setLastCommunicationTime( int i, const RWTime& tme )
{
    _lastCommunicationTime[i] = tme;
    return *this;
}


