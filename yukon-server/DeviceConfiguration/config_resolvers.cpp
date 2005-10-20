/*-----------------------------------------------------------------------------*
*
* File:   ConfigResolvers
*
* Date:   6/15/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_resolvers.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/10/20 18:26:07 $
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

    if(rwsTemp.contains(" mct "))
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
