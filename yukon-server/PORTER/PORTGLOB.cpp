/*-----------------------------------------------------------------------------*
*
* File:   PORTGLOB
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PORTGLOB.cpp-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2005/02/10 23:23:54 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <process.h>

#include <stdlib.h>

#include "os2_2w32.h"
#include "cticalls.h"

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "das08.h"
#include "tcpsup.h"
#include "portglob.h"

//IM_EX_PORTGLOB PCCUINFO        CCUInfo[MAXPORT][MAXIDLC];
IM_EX_PORTGLOB RWTPtrSlistDictionary< LONG, CCUINFO > CCUInfo;
// IM_EX_PORTGLOB RWTPtrSlistIterator< CCUINFO >         CCUInfoItr( RWTPtrSlist< CCUINFO >(CCUInfo) );

/* 20020611 CGP Oh boy...
IM_EX_PORTGLOB PPORTSTATS      PortStats[MAXPORT];
IM_EX_PORTGLOB ULONG           PortFlags[MAXPORT];
IM_EX_PORTGLOB NETCXPORTINFO   *NetCXPortInfo[MAXPORT];
IM_EX_PORTGLOB ULONG           MilliLast[MAXPORT];
IM_EX_PORTGLOB USHORT          LastCCU[MAXPORT];
*/

IM_EX_PORTGLOB HANDLE          hPorterEvents[NUMPORTEREVENTS];

IM_EX_PORTGLOB USHORT          TraceFlag = {FALSE};
IM_EX_PORTGLOB USHORT          TraceErrorsOnly = {FALSE};
IM_EX_PORTGLOB USHORT          ResetAll711s = {FALSE};
IM_EX_PORTGLOB USHORT          LoadRoutes = {FALSE};
IM_EX_PORTGLOB USHORT          AutoDSTChange = {TRUE};
IM_EX_PORTGLOB USHORT          AmpFailOver = 0;
IM_EX_PORTGLOB USHORT          NoQueing = {FALSE};
IM_EX_PORTGLOB USHORT          PackActins = {FALSE};
IM_EX_PORTGLOB USHORT          VCUWait = {FALSE};
IM_EX_PORTGLOB USHORT          ExtraTimeOut = {0};
IM_EX_PORTGLOB USHORT          LAndGLCUs = {FALSE};
IM_EX_PORTGLOB USHORT          StartTCPIP = {FALSE};
IM_EX_PORTGLOB USHORT          PortPerfUpdateCount = 100;
IM_EX_PORTGLOB USHORT          RemotePerfUpdateCount = 10;
IM_EX_PORTGLOB USHORT          DLCFreq1 = 94;
IM_EX_PORTGLOB USHORT          DLCFreq2 = 37;
IM_EX_PORTGLOB USHORT          DIO24Base = DAS08BASE + DIO24OFFSET;
IM_EX_PORTGLOB USHORT          DIO24Mode[9] = {0x0f,0xffff,0xffff,0xffff,0xffff,0xffff,0xffff,0xffff,0xffff};
IM_EX_PORTGLOB USHORT          MaxOcts = 241;
IM_EX_PORTGLOB USHORT          ZeroRemoteAddress = FALSE;
IM_EX_PORTGLOB ULONG           AlphaFailures = 0;
IM_EX_PORTGLOB LONG            TracePort   = 0L;
IM_EX_PORTGLOB LONG            TraceRemote = 0L;
IM_EX_PORTGLOB BOOL            NetCXAltPin = FALSE;
IM_EX_PORTGLOB UINT            ExtraPortTimeOut[MAXPORT];
IM_EX_PORTGLOB ULONG           PILMaxQueueSize = 1000;

/* Porter only globals... */
IM_EX_PORTGLOB CTINEXUS       PorterListenNexus;
IM_EX_PORTGLOB INT            PorterRefreshRate = 3600;

IM_EX_PORTGLOB BOOL           cParmPorterServiceLog = FALSE;

IM_EX_PORTGLOB UINT           PorterDebugLevel = 0x00000000;


IM_EX_PORTGLOB bool           gIgnoreTCU5X00QueFull = false;
IM_EX_PORTGLOB BOOL           PorterQuit = FALSE;

IM_EX_PORTGLOB UINT           PorterPortInitQueuePurgeDelay = 20;

IM_EX_PORTGLOB RWCString      gDelayDatFile("..\\CONFIG\\DELAY.DAT");


CHAR* hPorterEventNames[] = {
   { NULL },
   { NULL },
   { "CTIScannerSem" },
   { NULL },
   { NULL },
};


