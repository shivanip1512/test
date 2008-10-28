/*-----------------------------------------------------------------------------*
*
* File:   selectors
*
* Class:
* Date:   8/31/1999
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/slctdev.h-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __SLCTDEV_H__
#define __SLCTDEV_H__
#pragma warning (disable : 4786)

#include <rw/db/db.h>

#include "dlldefs.h"
#include "dev_base.h"
#include "rtdb.h"
#include "rte_base.h"

IM_EX_DEVDB CtiDeviceBase *DeviceFactory(RWDBReader &rdr);
IM_EX_DEVDB CtiDeviceBase *createDeviceType(int type);
IM_EX_DEVDB CtiRouteBase  *RouteFactory(RWDBReader &rdr);

IM_EX_DEVDB bool isAScannableDevice(CtiDeviceSPtr& pDevice, void* d);

IM_EX_DEVDB RWBoolean isCarrierLPDevice(CtiDeviceSPtr& pDevice);

#endif // #ifndef __SLCTDEV_H__
