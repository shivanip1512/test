/*-----------------------------------------------------------------------------*
*
* File:   perform3
*
* Date:   5-93
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/perform3.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:54 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#pragma title ( "Port and Remote Performance Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        Perform2.c

    Purpose:
        Routines to maintain and update statistics on ports and remotes

    The following procedures are contained in this module:
        RemotePerfUpdate                PortPerform
        Port24Perform                   PortFailures
        RemotePerform                   Remote24Perform
        RemoteFailures

    Initial Date:
        5-93

    Revision History:
        Unknown prior to 8-93
        9-6-93  Converted to 32 bit                             WRO
        11-1-93   Modified to keep stats temporarily in memory    TRH

   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

/* Routines used for performance calculations for ports and remotes */

#include <stdlib.h>
// #include "btrieve.h"
#include <stdio.h>
#include <string.h>
#include <math.h>
#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "drp.h"
#include "elogger.h"
#include "alarmlog.h"
#include "porter.h"

#include "portglob.h"
#include "c_port_interface.h"

#ifndef IMPORT
   #define IMPORT
   #include "perform.h"
   IM_EX_CTIBASE extern PERFGLOBS *PerfGlobs;
   #undef IMPORT
#else
   #include "perform.h"
   IM_EX_CTIBASE extern PERFGLOBS *PerfGlobs;
#endif


/* Routine called to update the performance counts for Remotes */
RemotePerfUpdate (REMOTEPERF *RemotePerf, ERRSTRUCT *ErrorRecord)
{
   return (NORMAL);
}

/* Routine to test if port failure has occurred */
PortFailures (STATS *Stats)
{
   return (NORMAL);
}


/* Routine to test if port is operating below percentage */
PortPerform (STATS *Stats, PUSHORT Percent)
{
   return (NORMAL);
}

/* Routine to test if port is operating below percentage for this 24 hour period */
Port24Perform (STATS *Stats, PUSHORT Percent)
{
   return (NORMAL);
}


/* Routine to test if  remote failure has occurred */
RemoteFailures (STATS *Stats)
{
   return (NORMAL);
}


/* Routine to test if Remote is operating below percentage */
RemotePerform (STATS *Stats,
               PUSHORT Percent)

{
   return (NORMAL);
}


/* Routine to test if Remote is operating below percentage for this 24 hour period */
Remote24Perform (STATS *Stats,
                 PUSHORT Percent)
{
   return (NORMAL);
}


