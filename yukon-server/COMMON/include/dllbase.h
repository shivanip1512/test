/*-----------------------------------------------------------------------------*
*
* File:   dllbase
*
* Date:   10/13/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/common/INCLUDE/dllbase.h-arc  $
* REVISION     :  $Revision: 1.37.2.1 $
* DATE         :  $Date: 2008/11/13 17:23:50 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DLLBASE_H__
#define __DLLBASE_H__
#pragma warning( disable : 4786)




#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <lmcons.h>
#include <iostream>
#include <set>


#include <rw\thr\mutex.h>

#include "os2_2w32.h"
#include "cticalls.h"
#include "dsm2.h"
#include "dlldefs.h"

using std::string;
using std::set;

#define SCANNERSEM "SCANNER.SEM"

// Used with "DB_DEBUGLEVEL"
#define DEBUGLEVEL_LUDICROUS        0x00000001
#define DEBUGLEVEL_ACTIVITY_INFO    0x00000002
#define DEBUGLEVEL_DATA_INFO        0x00000004
#define DEBUGLEVEL_PORTCOMM         0x00000010
#define DEBUGLEVEL_CONNECTION       0x00000200
#define DEBUGLEVEL_FACTORY          0x00000400
#define DEBUGLEVEL_DATABASE         0x00000800
#define DEBUGLEVEL_MSG_COMMAND      0x00001000
#define DEBUGLEVEL_STATISTICS       0x00002000
#define DEBUGLEVEL_SCANTYPES        0x00004000
#define DEBUGLEVEL_EXCLUSIONS       0x00008000
#define DEBUGLEVEL_MGR_POINT        0x00010000
#define DEBUGLEVEL_MGR_DEVICE       0x00020000
#define DEBUGLEVEL_MGR_ROUTE        0x00040000
#define DEBUGLEVEL_MGR_PORT         0x00080000
#define DEBUGLEVEL_PIL_RESULTTHREAD 0x00100000
#define DEBUGLEVEL_PIL_INTERFACE    0x00200000
#define DEBUGLEVEL_PIL_MAINTHREAD   0x00400000
#define DEBUGLEVEL_RIPPLE           0x01000000
#define DEBUGLEVEL_ILEX_PROTOCOL    0x02000000
#define DEBUGLEVEL_THREAD_SPEW      0x04000000   //yes Corey, I'll rename this when I think of something good
#define DEBUGLEVEL_SA3RDPARTY       0x08000000
#define DEBUGLEVEL_SIXNET_DEVICE    0x10000000
#define DEBUGLEVEL_SIXNET_PROTOCOL  0x20000000
#define DEBUGLEVEL_WELCO_PROTOCOL   0x40000000
#define DEBUGLEVEL_WCTP_PROTOCOL    0x80000000


IM_EX_CTIBASE extern CTINEXUS          PorterNexus;
IM_EX_CTIBASE extern RWMutexLock       coutMux;

IM_EX_CTIBASE extern string      dbDll;
IM_EX_CTIBASE extern string      dbName;
IM_EX_CTIBASE extern string      dbUser;
IM_EX_CTIBASE extern string      dbPassword;
IM_EX_CTIBASE extern string      VanGoghMachine;
IM_EX_CTIBASE extern string      NotificationMachine;
IM_EX_CTIBASE extern int         NotificationPort;
IM_EX_CTIBASE extern string      gSMTPServer;
IM_EX_CTIBASE extern string      gLogDirectory;
IM_EX_CTIBASE extern string      gEmailFrom;

IM_EX_CTIBASE extern bool           gLogPorts;                    // Write port data to portname.dayofmonth
IM_EX_CTIBASE extern bool           gDoPrefix;                    // Attach a prefix to TAP transmissions
IM_EX_CTIBASE extern bool           gCoalesceRippleBits;          // Ripple Groups combine bits to send on routes.
IM_EX_CTIBASE extern int            DebugLevel;
IM_EX_CTIBASE extern int            Double;                       // EMETCON Double send flag...
IM_EX_CTIBASE extern int            useVersacomTypeFourControl;   // Jeesh if you can't figure this out...
IM_EX_CTIBASE extern int            gMaxDBConnectionCount;        // Maximum number of DB connections to allow to remain open.

IM_EX_CTIBASE extern int            ModemConnectionTimeout;      // Modem Connection Timeout in seconds (60 def.)
IM_EX_CTIBASE extern bool           gIDLCEchoSuppression;
IM_EX_CTIBASE extern bool           gDNPVerbose;
IM_EX_CTIBASE extern UINT           gDNPInternalRetries;
IM_EX_CTIBASE extern int            gDefaultCommFailCount;
IM_EX_CTIBASE extern int            gDefaultPortCommFailCount;
IM_EX_CTIBASE extern unsigned char  gMCT400SeriesSPID;
IM_EX_CTIBASE extern short          gSimulatePorts;
IM_EX_CTIBASE extern set<long>      gSimulatedPorts;
IM_EX_CTIBASE extern set<long>      gForeignCCUPorts;
IM_EX_CTIBASE extern set<long>      gScanForceDevices;

IM_EX_CTIBASE void         InitYukonBaseGlobals(void);
IM_EX_CTIBASE INT          getDebugLevel(void);

#endif // #ifndef __DLLBASE_H__
