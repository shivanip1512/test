/*-----------------------------------------------------------------------------*
*
* File:   resolvers
*
* Date:   8/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/common/INCLUDE/resolvers.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2007/10/30 17:59:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __RESOLVERS_H__
#define __RESOLVERS_H__
#pragma warning( disable : 4786)


#include "types.h"
#include "pointtypes.h"
#include "dlldefs.h"
#include "yukon.h"
#include "db_entry_defines.h"

using std::string;


typedef enum
{
    invalidAddressUsage = 0,
    versacomAddressUsage,
    expresscomAddressUsage,

    maximumAddressUsage

} CtiAddressUsage_t;


IM_EX_CTIBASE CtiPointType_t resolvePointType(const string& rwsTemp);
IM_EX_CTIBASE INT resolvePointArchiveType(const string& rwsTemp);
IM_EX_CTIBASE INT resolveDeviceType(const string& rwsTemp);
IM_EX_CTIBASE INT resolveCapControlType(const string& rwsTemp);
IM_EX_CTIBASE INT resolveLoadManagementType(const string& rwsTemp);

IM_EX_CTIBASE INT resolvePAOType(const string& category, const string& rwsTemp);

IM_EX_CTIBASE INT resolveDeviceType(const string& rwsTemp);
IM_EX_CTIBASE INT resolvePortType(const string& str);
IM_EX_CTIBASE INT resolveLoadManagementType(const string& rwsTemp);
IM_EX_CTIBASE INT resolveCapControlType(const string& rwsTemp);
IM_EX_CTIBASE INT resolveRouteType(const string& rwsTemp);

IM_EX_CTIBASE INT resolvePAOType(const string& category, const string& rwsTemp);

IM_EX_CTIBASE INT resolvePAOClass(const string& rwsTemp);
IM_EX_CTIBASE INT resolvePAOCategory(const string& rwsTemp);
IM_EX_CTIBASE INT resolveDeviceState(const string& rwsTemp);
IM_EX_CTIBASE INT resolveScanType(const string& rwsTemp);
IM_EX_CTIBASE INT resolveStatisticsType(const string& rwsTemp);
IM_EX_CTIBASE CtiFilter_t resolveFilterType(const string& rwsTemp);
IM_EX_CTIBASE INT resolveProtocol(const string& str);
IM_EX_CTIBASE INT resolvePortType(const string& str);
IM_EX_CTIBASE INT resolvePortState(const string& str);
IM_EX_CTIBASE INT resolveAmpUseType(const string& rwsTemp);
IM_EX_CTIBASE INT resolveRouteType(const string& rwsTemp);
IM_EX_CTIBASE bool resolveIsDeviceTypeSingle(INT Type);
IM_EX_CTIBASE INT resolveRelayUsage(const string& rwsTemp);
IM_EX_CTIBASE INT resolveAWordTime(INT Seconds);
IM_EX_CTIBASE INT resolveAddressUsage(const string& str, int type);
IM_EX_CTIBASE string   resolveDBChanged(INT dbnum);
IM_EX_CTIBASE string   resolveDBChangeType(INT type);
IM_EX_CTIBASE INT resolveSlaveAddress(const INT DeviceType, const string& str);
IM_EX_CTIBASE CtiControlType_t  resolveControlType(const string& str);
IM_EX_CTIBASE LONG resolveDeviceWindowType(const string& rwsTemp);


IM_EX_CTIBASE INT resolveUomToCalcType(const string& str);




#endif //#ifndef __RESOLVERS_H__

