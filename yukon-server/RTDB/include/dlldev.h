#pragma warning( disable : 4786 )

#ifndef __DLLDEV_H__
#define __DLLDEV_H__

/*-----------------------------------------------------------------------------*
*
* File:   dllyukon
*
* Date:   10/5/1999
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dlldev.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "mgr_device.h"
#include "mgr_route.h"

void IM_EX_DEVDB attachTransmitterDeviceToRoutes(CtiDeviceManager *DM, CtiRouteManager *RteMgr);
void IM_EX_DEVDB attachRouteManagerToDevices(CtiDeviceManager *DM, CtiRouteManager *RteMgr);


#endif // #ifndef __DLLDEV_H__
