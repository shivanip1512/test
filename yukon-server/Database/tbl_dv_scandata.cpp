#include "yukon.h"



/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_scandata
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_scandata.cpp-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2005/10/19 19:10:21 $
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
_deviceID(did)
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

RWTime CtiTableDeviceScanData::getLastCommunicationTime(int i) const
{
    return _lastCommunicationTime[i];
}

CtiTableDeviceScanData& CtiTableDeviceScanData::setLastCommunicationTime( int i, const RWTime& tme )
{
    _lastCommunicationTime[i] = tme;
    return *this;
}


