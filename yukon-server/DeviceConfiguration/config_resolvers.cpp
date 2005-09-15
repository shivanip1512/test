/*-----------------------------------------------------------------------------*
*
* File:   ConfigResolvers
*
* Date:   6/15/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_resolvers.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/09/15 17:57:00 $
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

    if(rwsTemp == "test config type a")
    {
        Ret = TestConfigTypeA;
    }
    else 
    {
        Ret = ConfigTypeInvalid;
    }

    return Ret;
}
