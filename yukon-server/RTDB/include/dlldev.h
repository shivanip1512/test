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
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2008/10/23 20:38:04 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DLLDEV_H__
#define __DLLDEV_H__
#pragma warning( disable : 4786 )

class CtiDeviceManager;
class CtiRouteManager;

void IM_EX_DEVDB attachTransmitterDeviceToRoutes(CtiDeviceManager *DM, CtiRouteManager *RteMgr);


#endif // #ifndef __DLLDEV_H__
