#ifndef __RESOLVERS_H__
#define __RESOLVERS_H__

#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   resolvers
*
* Date:   8/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/common/INCLUDE/resolvers.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:37 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "types.h"
#include "pointtypes.h"
#include "dlldefs.h"
#include "yukon.h"
#include "db_entry_defines.h"

class RWCString;

IM_EX_CTIBASE INT resolvePointType(RWCString rwsTemp);
IM_EX_CTIBASE INT resolvePointArchiveType(RWCString rwsTemp);
IM_EX_CTIBASE INT resolveDeviceType(RWCString rwsTemp);
IM_EX_CTIBASE INT resolveCapControlType(RWCString rwsTemp);
IM_EX_CTIBASE INT resolveLoadManagementType(RWCString rwsTemp);
IM_EX_CTIBASE INT resolvePAOType(RWCString category, RWCString rwsTemp);
IM_EX_CTIBASE INT resolvePAOClass(RWCString rwsTemp);
IM_EX_CTIBASE INT resolvePAOCategory(RWCString rwsTemp);
IM_EX_CTIBASE INT resolveDeviceState(RWCString rwsTemp);
IM_EX_CTIBASE INT resolveScanType(RWCString rwsTemp);
IM_EX_CTIBASE INT resolveStatisticsType(RWCString rwsTemp);
IM_EX_CTIBASE CtiFilter_t resolveFilterType(RWCString rwsTemp);
IM_EX_CTIBASE INT resolveProtocol(RWCString str);
IM_EX_CTIBASE INT resolvePortType(RWCString str);
IM_EX_CTIBASE INT resolvePortState(RWCString str);
IM_EX_CTIBASE INT resolveAmpUseType(RWCString rwsTemp);
IM_EX_CTIBASE INT resolveRouteType(RWCString rwsTemp);
IM_EX_CTIBASE bool resolveIsDeviceTypeSingle(INT Type);
IM_EX_CTIBASE INT resolveRelayUsage(RWCString rwsTemp);
IM_EX_CTIBASE INT resolveAWordTime(INT Seconds);
IM_EX_CTIBASE INT resolveAddressUsage(RWCString str);
IM_EX_CTIBASE RWCString   resolveDBChanged(INT dbnum);
IM_EX_CTIBASE RWCString   resolveDBChangeType(INT type);
IM_EX_CTIBASE INT resolveSlaveAddress(const INT DeviceType, RWCString str);
IM_EX_CTIBASE CtiControlType_t  resolveControlType(RWCString& str);
IM_EX_CTIBASE LONG resolveDeviceWindowType(RWCString rwsTemp);


IM_EX_CTIBASE INT resolveUomToCalcType(RWCString str);




#endif //#ifndef __RESOLVERS_H__

