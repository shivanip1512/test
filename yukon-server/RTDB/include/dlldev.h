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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2003/03/13 19:36:15 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DLLDEV_H__
#define __DLLDEV_H__
#pragma warning( disable : 4786 )


#include "mgr_device.h"
#include "mgr_route.h"

void IM_EX_DEVDB attachTransmitterDeviceToRoutes(CtiDeviceManager *DM, CtiRouteManager *RteMgr);
void IM_EX_DEVDB attachRouteManagerToDevices(CtiDeviceManager *DM, CtiRouteManager *RteMgr);


#endif // #ifndef __DLLDEV_H__
