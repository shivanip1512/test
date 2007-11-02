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
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2007/11/02 19:03:44 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DLLDEV_H__
#define __DLLDEV_H__
#pragma warning( disable : 4786 )

class CtiDeviceManager;
class CtiPointManager;
class CtiRouteManager;

void IM_EX_DEVDB attachTransmitterDeviceToRoutes(CtiDeviceManager *DM, CtiRouteManager *RteMgr);
void IM_EX_DEVDB attachPointManagerToDevices(CtiDeviceManager *DM, CtiPointManager *PntMgr);
void IM_EX_DEVDB attachRouteManagerToDevices(CtiDeviceManager *DM, CtiRouteManager *RteMgr);


#endif // #ifndef __DLLDEV_H__
