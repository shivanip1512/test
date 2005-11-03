/*-----------------------------------------------------------------------------*
*
* File:   ConfigResolvers
*
* Date:   6/15/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_resolvers.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/11/03 17:50:27 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
    *-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw\cstring.h>


#include "config_resolvers.h"
#include "logger.h"
namespace Cti    {
namespace Config {


CtiConfig_type resolveConfigType(RWCString rwsTemp)
{
    CtiConfig_type Ret = ConfigTypeInvalid;
    rwsTemp.toLower();
    rwsTemp = rwsTemp.strip(RWCString::both);

    if(rwsTemp.contains("mct "))//note, if even a word ends in "...mct " it will go into this path. use caution
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
/*  else
    {
            if(rwsTemp == "config type general")
            {
                Ret = ConfigTypeGeneral;
            }
                else 
            {
                Ret = ConfigTypeInvalid;
            }
    }*/

    return Ret;
}

}//Config
}//Cti
