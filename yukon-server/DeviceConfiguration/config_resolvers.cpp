/*-----------------------------------------------------------------------------*
*
* File:   ConfigResolvers
*
* Date:   6/15/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_resolvers.cpp-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2006/10/19 15:56:26 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
    *-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "rwutil.h"

#include "config_resolvers.h"
#include "logger.h"
#include "utility.h"

namespace Cti    {
namespace Config {


CtiConfig_type resolveConfigType(string rwsTemp)
{
    CtiConfig_type Ret = ConfigTypeInvalid;
    CtiToLower(rwsTemp);
    rwsTemp = trim(rwsTemp);

    if(rwsTemp.find("mct ")!= string::npos)//note, if even a word ends in "...mct " it will go into this path. use caution
    {
        if(rwsTemp == "mct tou")
        {
            Ret = ConfigTypeMCTTOU;
        }
        else if(rwsTemp == "mct rate schedule")
        {
            Ret = ConfigTypeMCTTOURateSchedule;
        }
        else if(rwsTemp == "mct addressing")
        {
            Ret = ConfigTypeMCTAddressing;
        }
        else if(rwsTemp == "mct options")
        {
            Ret = ConfigTypeMCTOptions;
        }
        else if(rwsTemp == "mct demand and load profile")
        {
            Ret = ConfigTypeMCTDemandLP;
        }
        else if(rwsTemp == "mct daylight savings time")
        {
            Ret = ConfigTypeMCTDST;
        }
        else if(rwsTemp == "mct voltage threshold")
        {
            Ret = ConfigTypeMCTVThreshold;
        }
        else if(rwsTemp == "mct disconnect")
        {
            Ret = ConfigTypeMCTDisconnect;
        }
        else if(rwsTemp == "mct load profile channels")
        {
            Ret = ConfigTypeMCTLoadProfileChannels;
        }
        else if(rwsTemp == "mct relays")
        {
            Ret = ConfigTypeMCTRelays;
        }
        else if(rwsTemp == "mct holidays")
        {
            Ret = ConfigTypeMCTHoliday;
        }
        else if(rwsTemp == "mct precanned table")
        {
            Ret = ConfigTypeMCTPrecannedTable;
        }
        else if(rwsTemp == "mct long load profile")
        {
            Ret = ConfigTypeMCTLongLoadProfile;
        }
        else if(rwsTemp == "mct system options")
        {
            Ret = ConfigTypeMCTSystemOptions;
        }
        else if(rwsTemp == "mct centron")
        {
            Ret = ConfigTypeMCTCentron;
        }
        else if(rwsTemp == "mct dnp")
        {
            Ret = ConfigTypeMCTDNP;
        }
        else
        {
            Ret = ConfigTypeInvalid;
        }
    }
    else if(rwsTemp.find("cbc ")!= string::npos)//note, if even a word ends in "...cbc " it will go into this path. use caution
    {
        if(rwsTemp == "cbc voltage")
        {
            Ret = ConfigTypeCBCVoltage;
        }
        else if(rwsTemp == "cbc comms lost")
        {
            Ret = ConfigTypeCBCCommsLost;
        }
        else if(rwsTemp == "cbc neutral current")
        {
            Ret = ConfigTypeCBCNeutralCurrent;
        }
        else if(rwsTemp == "cbc fault detection")
        {
            Ret = ConfigTypeCBCFaultDetection;
        }
        else if(rwsTemp == "cbc season 1 time and temp")
        {
            Ret = ConfigTypeCBCSeason1TimeAndTemp;
        }
        else if(rwsTemp == "cbc season 2 time and temp")
        {
            Ret = ConfigTypeCBCSeason2TimeAndTemp;
        }
        else if(rwsTemp == "cbc control times")
        {
            Ret = ConfigTypeCBCControlTimes;
        }
        else if(rwsTemp == "cbc data logging")
        {
            Ret = ConfigTypeCBCDataLogging;
        }
        else if(rwsTemp == "cbc addressing")
        {
            Ret = ConfigTypeCBCAddressing;
        }
        else if(rwsTemp == "cbc dnp")
        {
            Ret = ConfigTypeCBC_DNP;
        }
        else if(rwsTemp == "cbc udp")
        {
            Ret = ConfigTypeCBC_UDP;
        }
        else
        {
            Ret = ConfigTypeInvalid;
        }
    }

    return Ret;
}

}//Config
}//Cti
