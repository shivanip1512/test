
#pragma warning( disable : 4786)
#ifndef __DLLBASE_H__
#define __DLLBASE_H__

/*-----------------------------------------------------------------------------*
*
* File:   dllbase
*
* Date:   10/13/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/common/INCLUDE/dllbase.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:24 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <windows.h>
#include <lmcons.h>
#include <iostream>
using namespace std;

#include <rw\thr\mutex.h>

#include "os2_2w32.h"
#include "cticalls.h"
#include "dsm2.h"
#include "dlldefs.h"

#define SCANNERSEM "SCANNER.SEM"

#define DEBUGLEVEL_LUDICROUS        0x00000001
#define DEBUGLEVEL_PORTCOMM         0x00000010
#define DEBUGLEVEL_FACTORY          0x00000400
#define DEBUGLEVEL_DATABASE         0x00000800
#define DEBUGLEVEL_MSG_COMMAND      0x00001000
#define DEBUGLEVEL_MGR_POINT        0x00010000
#define DEBUGLEVEL_MGR_DEVICE       0x00020000
#define DEBUGLEVEL_MGR_ROUTE        0x00040000
#define DEBUGLEVEL_MGR_PORT         0x00080000
#define DEBUGLEVEL_RIPPLE           0x01000000
#define DEBUGLEVEL_SIXNET_DEVICE    0x10000000
#define DEBUGLEVEL_SIXNET_PROTOCOL  0x20000000

IM_EX_CTIBASE extern CTINEXUS        PorterNexus;
IM_EX_CTIBASE extern RWMutexLock     coutMux;

IM_EX_CTIBASE extern RWCString      dbDll;
IM_EX_CTIBASE extern RWCString      dbName;
IM_EX_CTIBASE extern RWCString      dbUser;
IM_EX_CTIBASE extern RWCString      dbPassword;
IM_EX_CTIBASE extern RWCString      VanGoghMachine;
IM_EX_CTIBASE extern RWCString      gSMTPServer;
IM_EX_CTIBASE extern RWCString      gLogDirectory;
IM_EX_CTIBASE extern RWCString      gEmailFrom;
IM_EX_CTIBASE extern bool           gLogPorts;                    // Write port data to portname.dayofmonth
IM_EX_CTIBASE extern bool           gOptimizeVersacom;            // Allow an assign to section class and division in one page
IM_EX_CTIBASE extern bool           gDoPrefix;                    // Attach a prefix to TAP transmissions
IM_EX_CTIBASE extern bool           gCoalesceRippleBits;          // Ripple Groups combine bits to send on routes.
IM_EX_CTIBASE extern int            DebugLevel;
IM_EX_CTIBASE extern int            Double;                       // EMETCON Double send flag...
IM_EX_CTIBASE extern int            useVersacomTypeFourControl;   // Jeesh if you can't figure this out...

IM_EX_CTIBASE extern int            ModemConnectionTimeout;      // Modem Connection Timeout in seconds (60 def.)


IM_EX_CTIBASE void         InitYukonBaseGlobals(void);
IM_EX_CTIBASE INT          getDebugLevel(void);

IM_EX_CTIBASE bool         OptimizeVersacomConfiguration();


#endif // #ifndef __DLLBASE_H__
