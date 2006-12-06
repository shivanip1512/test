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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2006/12/06 22:12:51 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DLLDEV_H__
#define __DLLDEV_H__
#pragma warning( disable : 4786 )


//#include "mgr_device.h"
//#include "mgr_route.h"

class CtiDeviceManager;
class CtiRouteManager;
struct PointDeviceMapping;

void IM_EX_DEVDB attachTransmitterDeviceToRoutes(CtiDeviceManager *DM, CtiRouteManager *RteMgr);
void IM_EX_DEVDB attachRouteManagerToDevices(CtiDeviceManager *DM, CtiRouteManager *RteMgr);
void IM_EX_DEVDB attachPointIDDeviceMapToDevices(CtiDeviceManager *DM, PointDeviceMapping *PtTrk);

#endif // #ifndef __DLLDEV_H__
