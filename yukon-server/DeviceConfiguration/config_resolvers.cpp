/*-----------------------------------------------------------------------------*
*
* File:   ConfigResolvers
*
* Date:   6/15/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_resolvers.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/12/20 17:16:44 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
    *-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "rwutil.h"

#include "config_resolvers.h"
#include "logger.h"


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
        else
        {
            Ret = ConfigTypeInvalid;
        }

    }

    return Ret;
}

}//Config
}//Cti
