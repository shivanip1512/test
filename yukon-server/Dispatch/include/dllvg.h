
#pragma warning( disable : 4786)
#ifndef __DLLVG_H__
#define __DLLVG_H__

/*-----------------------------------------------------------------------------*
*
* File:   dllvg
*
* Class:
* Date:   12/6/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/INCLUDE/dllvg.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/07/19 22:48:53 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dlldefs.h"

#define DISPATCH_APPLICATION_NAME               "Dispatch Server"

#define DISPATCH_DEBUG_VERBOSE                  0x00000001
#define DISPATCH_DEBUG_CONNECTIONS              0x00000010
#define DISPATCH_DEBUG_PENDINGOPS               0x00000020
#define DISPATCH_DEBUG_REGISTRATION             0x00000040
#define DISPATCH_DEBUG_CONTROLS                 0x00000080
#define DISPATCH_DEBUG_DELAYED_UPDATE           0x00000100
#define DISPATCH_DEBUG_ALARMACK                 0x01000000
#define DISPATCH_DEBUG_MESSAGES                 0x02000000
#define DISPATCH_DEBUG_MSGSTOCLIENT             0x04000000
#define DISPATCH_DEBUG_MSGSFRMCLIENT            0x08000000
#define DISPATCH_DEBUG_ALARMS                   0x10000000
#define DISPATCH_DEBUG_NOTIFICATION             0x20000000

IM_EX_CTIVANGOGH extern UINT gDispatchDebugLevel;
IM_EX_CTIVANGOGH extern UINT gDispatchReloadRate;
IM_EX_CTIVANGOGH extern INT gCommErrorDays;


IM_EX_CTIVANGOGH void InitDispatchGlobals(void);

#endif // #ifndef __DLLVG_H__
