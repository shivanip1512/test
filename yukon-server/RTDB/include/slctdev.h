
#ifndef __SLCTDEV_H__
#define __SLCTDEV_H__

#pragma warning (disable : 4786)

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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/09/06 19:03:42 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <rw/db/db.h>

#include "dlldefs.h"
#include "dev_base.h"
#include "rte_base.h"

IM_EX_DEVDB CtiDeviceBase* DeviceFactory(RWDBReader &rdr);
IM_EX_DEVDB CtiRouteBase*  RouteFactory(RWDBReader &rdr);

IM_EX_DEVDB BOOL isADevice(CtiDeviceBase*,void*);
IM_EX_DEVDB BOOL isAScannableDevice(CtiDeviceBase*,void*);

IM_EX_DEVDB RWBoolean isCarrierLPDevice(CtiDeviceBase *pDevice);
IM_EX_DEVDB RWBoolean isNotScannable( CtiRTDB< CtiDeviceBase >::val_pair vp, void* d);

#endif // #ifndef __SLCTDEV_H__
