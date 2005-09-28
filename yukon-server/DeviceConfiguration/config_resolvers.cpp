/*-----------------------------------------------------------------------------*
*
* File:   ConfigResolvers
*
* Date:   6/15/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_resolvers.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/09/28 14:32:09 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
    *-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw\cstring.h>


#include "config_resolvers.h"
#include "logger.h"

INT resolveConfigType(RWCString rwsTemp)
{
    INT Ret = 0;
    rwsTemp.toLower();
    rwsTemp = rwsTemp.strip(RWCString::both);

    if(rwsTemp.contains(" mct "))
    {
        if(rwsTemp == "config type mct tou")
        {
            Ret = ConfigTypeMCTTOU;
        }
        else if(rwsTemp == "config type mct addressing")
        {
            Ret = ConfigTypeMCTAddressing;
        }
        else if(rwsTemp == "config type mct configuration")
        {
            Ret = ConfigTypeMCTConfiguration;
        }
        else if(rwsTemp == "config type mct demand LP")
        {
            Ret = ConfigTypeMCTDemandLP;
        }
        else if(rwsTemp == "config type mct dst")
        {
            Ret = ConfigTypeMCTDST;
        }
        else if(rwsTemp == "config type mct vthreshold")
        {
            Ret = ConfigTypeMCTVThreshold;
        }
        else
        {
            Ret = ConfigTypeInvalid;
        }

    }
    else
    {
            if(rwsTemp == "config type general")
            {
                Ret = ConfigTypeGeneral;
            }
                else 
            {
                Ret = ConfigTypeInvalid;
            }
    }

    return Ret;
}
