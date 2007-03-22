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
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2007/03/22 17:22:39 $
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

    for( int i = 0; i < MaxCollectedChannel; i++ )
    {
        _channelValid[i] = false;
    }
}

CtiTableDeviceLoadProfile::CtiTableDeviceLoadProfile(const CtiTableDeviceLoadProfile& aRef)
{
    *this = aRef;
}

CtiTableDeviceLoadProfile::~CtiTableDeviceLoadProfile() {}

CtiTableDeviceLoadProfile& CtiTableDeviceLoadProfile::operator=(const CtiTableDeviceLoadProfile& aRef)
{
    if( this != &aRef )
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

INT  CtiTableDeviceLoadProfile::getLastIntervalDemandRate()  const  {  return _lastIntervalDemandRate;  }
INT  CtiTableDeviceLoadProfile::getLoadProfileDemandRate()   const  {  return _loadProfileDemandRate;   }
INT  CtiTableDeviceLoadProfile::getVoltageDemandInterval()   const  {  return _voltageDemandInterval;   }
INT  CtiTableDeviceLoadProfile::getVoltageProfileRate()      const  {  return _voltageProfileRate;      }

bool CtiTableDeviceLoadProfile::isChannelValid(int channel)  const  {  return _channelValid[channel];   }

void CtiTableDeviceLoadProfile::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName().c_str() );

    selector << devTbl["lastintervaldemandrate"]
             << devTbl["loadprofiledemandrate"]
             << devTbl["loadprofilecollection"]
             << devTbl["voltagedmdinterval"]
             << devTbl["voltagedmdrate"];

    selector.from(devTbl);
    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
}

void CtiTableDeviceLoadProfile::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWDBNullIndicator isNull;
    string temp_str;

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

    rdr["loadprofilecollection"] >> temp_str;
    std::transform(temp_str.begin(), temp_str.end(), temp_str.begin(), tolower);

    if( !temp_str.empty() )
    {
        for( int i = 0; i < MaxCollectedChannel; i++ )
        {
            if( i < temp_str.length() && temp_str[i] == 'y' )
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

                    _channelValid[i] = false;
                }
                else
                {
                    _channelValid[i] = true;
                }
            }
            else
            {
                _channelValid[i] = false;
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

RWDBStatus CtiTableDeviceLoadProfile::Restore()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBSelector selector = getDatabase().selector();

    selector << table["deviceid"]
             << table["lastintervaldemandrate"]
             << table["loadprofiledemandrate"];

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

    inserter << getDeviceID()
             << getLastIntervalDemandRate()
             << getLoadProfileDemandRate();

    if( ExecuteInserter(conn,inserter,__FILE__,__LINE__).errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableDeviceLoadProfile::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
    RWDBUpdater updater = table.updater();

    updater.where( table["deviceid"] == getDeviceID() );

    updater << table["lastintervaldemandrate"].assign(getLastIntervalDemandRate())
            << table["loadprofiledemandrate" ].assign(getLoadProfileDemandRate());

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

