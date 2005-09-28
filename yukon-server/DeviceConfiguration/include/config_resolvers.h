/*-----------------------------------------------------------------------------*
*
* File:   ConfigResolvers
*
* Date:   6/15/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/config_resolvers.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/09/28 21:13:55 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __CONFIG_RESOLVERS_H__
#define __CONFIG_RESOLVERS_H__
#include "yukon.h"

//#define EXPORT

#include "logger.h"
#include "dllbase.h"

enum CtiConfig_t
{
    ConfigTypeInvalid = 0,
    ConfigTypeGeneral,
    ConfigTypeMCTTOU,
    ConfigTypeMCTAddressing,
    ConfigTypeMCTConfiguration,
    ConfigTypeMCTDemandLP,
    ConfigTypeMCTDST,
    ConfigTypeMCTVThreshold,

    ConfigTypeMax
};

IM_EX_CONFIG INT resolveConfigType(RWCString rwsTemp);

#endif //__CONFIG_RESOLVERS_H__
