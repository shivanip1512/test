/*-----------------------------------------------------------------------------*
*
* File:   ConfigResolvers
*
* Date:   6/15/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/config_resolvers.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/10/20 18:26:07 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __CONFIG_RESOLVERS_H__
#define __CONFIG_RESOLVERS_H__
#include "yukon.h"

//#define EXPORT

#include "logger.h"
#include "dllbase.h"
namespace Cti    {
namespace Config {


enum CtiConfig_type
{
    ConfigTypeInvalid = 0,
    ConfigTypeMCTTOU,
    ConfigTypeMCTAddressing,
    ConfigTypeMCTOptions,
    ConfigTypeMCTDemandLP,
    ConfigTypeMCTDST,
    ConfigTypeMCTVThreshold,
    ConfigTypeMCTDisconnect,
    ConfigTypeMCTLLP,
    ConfigTypeMCTHoliday,
    ConfigTypeMCTUsage,

    //created for the 470
    ConfigTypeMCTTables,
    ConfigTypeMCTRelays,
    ConfigTypeMCTChannels,
    

    ConfigTypeMax
};

IM_EX_CONFIG CtiConfig_type resolveConfigType(RWCString rwsTemp);

}//Config
}//Cti

#endif //__CONFIG_RESOLVERS_H__
