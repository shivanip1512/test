/*-----------------------------------------------------------------------------*
*
* File:   tbl_loadprofile
*
* Date:   8/14/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_loadprofile.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/04/15 18:28:40 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_loadprofile.h"
#include "logger.h"

CtiTableDeviceLoadProfile::CtiTableDeviceLoadProfile() :
_deviceID(-1),
_lastIntervalDemandRate(INT_MAX),
_loadProfileDemandRate(INT_MAX)
{
    for(int i = 0; i < MaxCollectedChannel; i++)
    {
        _channelValid[i] = FALSE;
    }
}

CtiTableDeviceLoadProfile::CtiTableDeviceLoadProfile(const CtiTableDeviceLoadProfile& aRef)
{
    *this = aRef;
}

CtiTableDeviceLoadProfile::~CtiTableDeviceLoadProfile() {}

CtiTableDeviceLoadProfile& CtiTableDeviceLoadProfile::operator=(const CtiTableDeviceLoadProfile& aRef)
{
    if(this != &aRef)
    {
        _deviceID               = aRef.getDeviceID();
        _lastIntervalDemandRate = aRef.getLastIntervalDemandRate();
        _loadProfileDemandRate  = aRef.getLoadProfileDemandRate();

        for(int i = 0; i < MaxCollectedChannel; i++)
        {
            _channelValid[i]     = aRef.isChannelValid(i);
        }
    }
    return *this;
}

INT  CtiTableDeviceLoadProfile::getLastIntervalDemandRate() const   {   return _lastIntervalDemandRate; }
INT  CtiTableDeviceLoadProfile::getLoadProfileDemandRate()  const   {   return _loadProfileDemandRate;  }
INT  CtiTableDeviceLoadProfile::getVoltageDemandInterval()  const   {   return _voltageDemandInterval;  }
INT  CtiTableDeviceLoadProfile::getVoltageProfileRate()     const   {   return _voltageProfileRate;    }

/*
CtiTableDeviceLoadProfile& CtiTableDeviceLoadProfile::setLastIntervalDemandRate( const INT aDemandInterval )
{

    _lastIntervalDemandRate = aDemandInterval;
    return *this;
}

CtiTableDeviceLoadProfile& CtiTableDeviceLoadProfile::setLoadProfileDemandRate( const INT aRate )
{

    _loadProfileDemandRate = aRate;
    return *this;
}
*/

BOOL CtiTableDeviceLoadProfile::isChannelValid(const INT ch) const
{

    return _channelValid[ch];
}

CtiTableDeviceLoadProfile& CtiTableDeviceLoadProfile::setChannelValid( const INT ch, const BOOL val )
{

    _channelValid[ch] = val;
    return *this;
}

void CtiTableDeviceLoadProfile::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName() );

    selector <<
    devTbl["lastintervaldemandrate"] <<
    devTbl["loadprofiledemandrate"] <<
    devTbl["loadprofilecollection"] <<
    devTbl["voltagedmdinterval"] <<
    devTbl["voltagedmdrate"];

    selector.from(devTbl);
    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
}

void CtiTableDeviceLoadProfile::DecodeDatabaseReader(RWDBReader &rdr)
{
    INT iTemp;
    RWDBNullIndicator isNull;
    RWCString   rwsTemp;

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & DEBUGLEVEL_DATABASE) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["lastintervaldemandrate"] >> _lastIntervalDemandRate;
    rdr["loadprofiledemandrate"]  >> _loadProfileDemandRate;
    rdr["voltagedmdinterval"]     >> _voltageDemandInterval;
    rdr["voltagedmdrate"]         >> _voltageProfileRate;

    rdr["loadprofilecollection"] >> rwsTemp;
    rwsTemp.toLower();

    if(!rwsTemp.isNull())
    {
        for(int i = 0; i < MaxCollectedChannel; i++)
        {
            if( i < rwsTemp.mbLength() && rwsTemp.data()[i] == 'y' )
            {
                if( _loadProfileDemandRate == 0 )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << " **** Load Profile collection disabled for deviceID = " << _deviceID << " ****" << endl;
                        dout << " **** LOADPROFILE DEMAND RATE == 0 while attempting Load Profile collection ****" << endl;
                        dout << " **** FIX DATABASE ENTRY **** " << endl;
                    }
                    _channelValid[i] = FALSE;
                }
                else
                {
                    _channelValid[i] = TRUE;
                }
            }
            else
            {
                _channelValid[i] = FALSE;
            }
        }
    }
}

RWCString CtiTableDeviceLoadProfile::getTableName()
{
    return "DeviceLoadProfile";
}

LONG CtiTableDeviceLoadProfile::getDeviceID() const
{

    return _deviceID;
}

CtiTableDeviceLoadProfile& CtiTableDeviceLoadProfile::setDeviceID( const LONG deviceID )
{

    _deviceID = deviceID;
    return *this;
}

RWDBStatus CtiTableDeviceLoadProfile::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["deviceid"] <<
    table["lastintervaldemandrate"] <<
    table["loadprofiledemandrate"];

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

RWDBStatus CtiTableDeviceLoadProfile::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID()     <<
    getLastIntervalDemandRate() <<
    getLoadProfileDemandRate();

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableDeviceLoadProfile::Update()
{
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["lastintervaldemandrate"].assign(getLastIntervalDemandRate() ) <<
    table["loadprofiledemandrate"].assign(getLoadProfileDemandRate() );

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableDeviceLoadProfile::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}

