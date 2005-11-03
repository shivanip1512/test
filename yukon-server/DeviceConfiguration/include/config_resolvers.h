/*-----------------------------------------------------------------------------*
*
* File:   ConfigResolvers
*
* Date:   6/15/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/config_resolvers.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/11/03 17:57:37 $
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
    ConfigTypeMCTHoliday,
    ConfigTypeMCTUsage,
    ConfigTypeMCTLongLoadProfile,

    //created for the 470
    ConfigTypeMCTPrecannedTable,
    ConfigTypeMCTRelays,
    ConfigTypeMCTLoadProfileChannels,
    

    ConfigTypeMax
};

IM_EX_CONFIG CtiConfig_type resolveConfigType(RWCString rwsTemp);

}//Config
}//Cti

#endif //__CONFIG_RESOLVERS_H__
