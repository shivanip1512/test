/*-----------------------------------------------------------------------------*
*
* File:   PORTGLOB
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/INCLUDE/PORTGLOB.H-arc  $
* REVISION     :  $Revision: 1.17 $
* DATE         :  $Date: 2004/10/08 20:40:42 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include <rw/tpsldict.h>
#include <set>
using namespace std;

#include <rw/tpslist.h>


#include "dlldefs.h"
#include "types.h"
#include "tcpsup.h"
#include "porter.h"
#include "logger.h"
#include "statistics.h"

#define PORTER_DEBUG_VERBOSE           0x00000001
#define PORTER_DEBUG_NEXUSREAD         0x00000004
#define PORTER_DEBUG_PORTQUEREAD       0x00000008
#define PORTER_DEBUG_SENDERROR         0x00000010
#define PORTER_DEBUG_COMMFAIL          0x00000020
#define PORTER_DEBUG_VERSACOM          0x00000100
#define PORTER_DEBUG_RIPPLE            0x00000800
#define PORTER_DEBUG_PRIORITIES        0x00001000
#define PORTER_DEBUG_CCUMESSAGES       0x00010000
#define PORTER_DEBUG_CCUCONFIG         0x00020000

#define PORTER_DEBUG_DIALBACK_PILDIRECT     0x10000000

#define PORTER_DBGBIT_VERBOSEERRORS    0x00000001


#define P_TIMESYNC_EVENT               0
#define P_QUEUE_EVENT                  1
#define P_SCANNER_EVENT                2
#define P_PERFORM_EVENT                3
#define P_QUIT_EVENT                   4
#define P_REFRESH_EVENT                5
#define P_GWRESULT_EVENT               6

#define NUMPORTEREVENTS                7


typedef map< ULONG, CtiStatistics > CtiStatisticsMap_t;
typedef CtiStatisticsMap_t::iterator CtiStatisticsIterator_t;

extern IM_EX_PORTGLOB INT           PorterRefreshRate;

/*  20020611 CGP ... Oh boy.
extern IM_EX_PORTGLOB ULONG         PortFlags[MAXPORT];
extern IM_EX_PORTGLOB ULONG         MilliLast[MAXPORT];
extern IM_EX_PORTGLOB USHORT        LastCCU[MAXPORT];
extern IM_EX_PORTGLOB PPORTSTATS    PortStats[MAXPORT];
extern IM_EX_PORTGLOB NETCXPORTINFO *NetCXPortInfo[MAXPORT];
*/

extern IM_EX_PORTGLOB USHORT        TraceFlag;
extern IM_EX_PORTGLOB USHORT        TraceErrorsOnly;
extern IM_EX_PORTGLOB USHORT        ResetAll711s;
extern IM_EX_PORTGLOB USHORT        LoadRoutes;
extern IM_EX_PORTGLOB USHORT        AutoDSTChange;
extern IM_EX_PORTGLOB USHORT        AmpFailOver;
extern IM_EX_PORTGLOB USHORT        NoQueing;
extern IM_EX_PORTGLOB USHORT        PackActins;
extern IM_EX_PORTGLOB USHORT        VCUWait;
extern IM_EX_PORTGLOB USHORT        ExtraTimeOut;
extern IM_EX_PORTGLOB USHORT        LAndGLCUs;
extern IM_EX_PORTGLOB USHORT        StartTCPIP;
extern IM_EX_PORTGLOB USHORT        PortPerfUpdateCount;
extern IM_EX_PORTGLOB USHORT        RemotePerfUpdateCount;
extern IM_EX_PORTGLOB USHORT        DLCFreq1;
extern IM_EX_PORTGLOB USHORT        DLCFreq2;
extern IM_EX_PORTGLOB USHORT        DIO24Base;
extern IM_EX_PORTGLOB USHORT        DIO24Mode[9];
extern IM_EX_PORTGLOB USHORT        MaxOcts;
extern IM_EX_PORTGLOB USHORT        ZeroRemoteAddress;
extern IM_EX_PORTGLOB ULONG         AlphaFailures;
extern IM_EX_PORTGLOB LONG          TracePort;
extern IM_EX_PORTGLOB LONG          TraceRemote;
extern IM_EX_PORTGLOB BOOL          NetCXAltPin;

extern IM_EX_PORTGLOB ULONG         PILMaxQueueSize;        // Debugging Bits
extern IM_EX_PORTGLOB BOOL          cParmPorterServiceLog;  // CPARM which causes the porter service to write output to porter.logxx
extern IM_EX_PORTGLOB UINT          PorterDebugLevel;


/* Not exported */
extern IM_EX_PORTGLOB BOOL          PorterQuit;
extern IM_EX_PORTGLOB HANDLE        hPorterEvents[];
extern IM_EX_PORTGLOB bool          gIgnoreTCU5X00QueFull;
extern IM_EX_PORTGLOB UINT          PorterPortInitQueuePurgeDelay;



extern CtiSyncDefStruct             PorterSyncs[];
extern IM_EX_PORTGLOB CTINEXUS      PorterListenNexus;

extern IM_EX_PORTGLOB RWCString     gDelayDatFile;



