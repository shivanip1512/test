/*-----------------------------------------------------------------------------*
*
* File:   PORTGLOB
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PORTGLOB.cpp-arc  $
* REVISION     :  $Revision: 1.16.2.1 $
* DATE         :  $Date: 2008/11/13 17:23:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <process.h>

#include <stdlib.h>

#include "os2_2w32.h"
#include "cticalls.h"

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "porter.h"
#include "portglob.h"

using std::string;

IM_EX_PORTGLOB HANDLE          hPorterEvents[NUMPORTEREVENTS];

IM_EX_PORTGLOB USHORT          TraceFlag = {FALSE};
IM_EX_PORTGLOB USHORT          TraceErrorsOnly = {FALSE};
IM_EX_PORTGLOB USHORT          ResetAll711s = {FALSE};
IM_EX_PORTGLOB USHORT          LoadRoutes = {FALSE};
IM_EX_PORTGLOB USHORT          AmpFailOver = 0;
IM_EX_PORTGLOB USHORT          VCUWait = {FALSE};
IM_EX_PORTGLOB USHORT          LAndGLCUs = {FALSE};
IM_EX_PORTGLOB USHORT          DLCFreq1 = 94;
IM_EX_PORTGLOB USHORT          DLCFreq2 = 37;
IM_EX_PORTGLOB USHORT          MaxOcts = 241;
IM_EX_PORTGLOB LONG            TracePort   = 0L;
IM_EX_PORTGLOB LONG            TraceRemote = 0L;

/* Porter only globals... */
IM_EX_PORTGLOB CTINEXUS       PorterListenNexus;
IM_EX_PORTGLOB INT            PorterRefreshRate = 86400;

IM_EX_PORTGLOB BOOL           cParmPorterServiceLog = FALSE;

IM_EX_PORTGLOB UINT           PorterDebugLevel = 0x00000000;


IM_EX_PORTGLOB bool           gIgnoreTCU5X00QueFull = false;
IM_EX_PORTGLOB BOOL           PorterQuit = FALSE;

IM_EX_PORTGLOB string      gDelayDatFile("..\\CONFIG\\DELAY.DAT");


CHAR* hPorterEventNames[] = {
   { NULL },
   { NULL },
   { "CTIScannerSem" },
   { NULL },
   { NULL },
};


