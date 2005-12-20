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
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2005/12/20 17:16:06 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_loadprofile.h"
#include "logger.h"

#include "rwutil.h"

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
    RWDBTable devTbl = db.table(getTableName().c_str() );

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
    string   rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["lastintervaldemandrate"] >> _lastIntervalDemandRate;
    rdr["loadprofiledemandrate"]  >> _loadProfileDemandRate;
    rdr["voltagedmdinterval"]     >> _voltageDemandInterval;
    rdr["voltagedmdrate"]         >> _voltageProfileRate;

    rdr["loadprofilecollection"] >> rwsTemp;
    std::transform(rwsTemp.begin(), rwsTemp.end(), rwsTemp.begin(), tolower);
    //rwsTemp.toLower();

    if(!rwsTemp.empty())
    {
        for(int i = 0; i < MaxCollectedChannel; i++)
        {
            if( i < ::mblen(rwsTemp.c_str(),::MB_CUR_MAX) && rwsTemp[i] == 'y' )
            {
                if( _loadProfileDemandRate == 0 )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

string CtiTableDeviceLoadProfile::getTableName()
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

    RWDBTable table = getDatabase().table( getTableName().c_str() );
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

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getDeviceID()     <<
    getLastIntervalDemandRate() <<
    getLoadProfileDemandRate();

    if( ExecuteInserter(conn,inserter,__FILE__,__LINE__).errorCode() == RWDBStatus::ok)
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

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater <<
    table["lastintervaldemandrate"].assign(getLastIntervalDemandRate() ) <<
    table["loadprofiledemandrate"].assign(getLoadProfileDemandRate() );

    if( ExecuteUpdater(conn,updater,__FILE__,__LINE__) == RWDBStatus::ok )
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableDeviceLoadProfile::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getDeviceID() );
    deleter.execute( conn );
    return deleter.status();
}

