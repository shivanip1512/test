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
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2004/05/05 15:31:41 $
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

IM_EX_DEVDB CtiDeviceBase* DeviceFactory(RWDBReader &rdr);
IM_EX_DEVDB CtiRouteBase*  RouteFactory(RWDBReader &rdr);

IM_EX_DEVDB bool isADevice(CtiDeviceSPtr&,void*);
IM_EX_DEVDB bool isNotADevice(CtiDeviceSPtr& pSp, void *arg);
IM_EX_DEVDB bool isAScannableDevice(CtiDeviceSPtr&,void*);
IM_EX_DEVDB bool isNotAScannableDevice(CtiDeviceSPtr& pDevice, void* d);

IM_EX_DEVDB RWBoolean isCarrierLPDevice(CtiDeviceSPtr& pDevice);
IM_EX_DEVDB RWBoolean isNotScannable( CtiDeviceSPtr& Dev, void* d);

#endif // #ifndef __SLCTDEV_H__
