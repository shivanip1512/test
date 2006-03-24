/*-----------------------------------------------------------------------------*
*
* File:   ConfigResolvers
*
* Date:   6/15/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/config_resolvers.h-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2006/03/24 15:11:53 $
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
    ConfigTypeMCTSystemOptions,

    //created for the 470
    ConfigTypeMCTPrecannedTable,
    ConfigTypeMCTRelays,
    ConfigTypeMCTLoadProfileChannels,
    

    ConfigTypeMax
};

IM_EX_CONFIG CtiConfig_type resolveConfigType(string rwsTemp);

}//Config
}//Cti

#endif //__CONFIG_RESOLVERS_H__
