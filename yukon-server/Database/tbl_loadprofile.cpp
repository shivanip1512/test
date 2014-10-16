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
#include "precompiled.h"

#include "tbl_loadprofile.h"
#include "logger.h"

#include "database_connection.h"
#include "database_reader.h"

using std::string;
using std::endl;

CtiTableDeviceLoadProfile::CtiTableDeviceLoadProfile() :
    _deviceID(-1),
    _lastIntervalDemandRate(INT_MAX),
    _loadProfileDemandRate(INT_MAX),
    _voltageDemandInterval(0),
    _voltageProfileRate(0)
{
}


CtiTableDeviceLoadProfile::~CtiTableDeviceLoadProfile() {}


INT  CtiTableDeviceLoadProfile::getLastIntervalDemandRate()  const  {  return _lastIntervalDemandRate;  }
INT  CtiTableDeviceLoadProfile::getLoadProfileDemandRate()   const  {  return _loadProfileDemandRate;   }
INT  CtiTableDeviceLoadProfile::getVoltageDemandInterval()   const  {  return _voltageDemandInterval;   }
INT  CtiTableDeviceLoadProfile::getVoltageProfileRate()      const  {  return _voltageProfileRate;      }

bool CtiTableDeviceLoadProfile::isChannelValid(int channel)  const  {  return _channelsValid[channel];   }

void CtiTableDeviceLoadProfile::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string temp_str;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["deviceid"] >> _deviceID;
    rdr["lastintervaldemandrate"] >> _lastIntervalDemandRate;
    rdr["loadprofiledemandrate"]  >> _loadProfileDemandRate;
    rdr["voltagedmdinterval"]     >> _voltageDemandInterval;
    rdr["voltagedmdrate"]         >> _voltageProfileRate;

    rdr["loadprofilecollection"] >> temp_str;

    if( !temp_str.empty() )
    {
        for( int i = 0; i < MaxCollectedChannel; i++ )
        {
            if( i < temp_str.length() && tolower(temp_str[i]) == 'y' )
            {
                if( _loadProfileDemandRate == 0 )
                {
                    CTILOG_WARN(dout, "Load Profile collection disabled for deviceID = "<< _deviceID <<
                            endl <<"**** LOADPROFILE DEMAND RATE == 0 while attempting Load Profile collection ****"<<
                            endl <<"**** FIX DATABASE ENTRY ****"
                            );

                    _channelsValid.reset(i);
                }
                else
                {
                    _channelsValid.set(i);
                }
            }
            else
            {
                _channelsValid.reset(i);
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

