/*-----------------------------------------------------------------------------*
*
* File:   porter
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/porter.cpp-arc  $
* REVISION     :  $Revision: 1.65 $
* DATE         :  $Date: 2004/12/14 22:36:13 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#pragma title ( "PORTER -- Port Control Program" )
#pragma subtitle ( "CTI Copyright (c) 1990-1999" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert
        All bugs added later by Corey Plender


    FileName:
        Porter.cpp

    Purpose:
        Process to take requests from pipes, prioritize them on the queues,
        output them to a port, retrieve the result, and if neccessary return
        that result to the calling program via the pipe.  Simple, eh?

    The following procedures are contained in this module:
        main                    SendError
        ReportRemoteError       StartPortThead
        PorterInputThread       PorterCleanUp

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        8-20-93     3.59 (Arms into portpipe)                     WRO
        8-24-93     3.5.10 (L Flag, Route Load Race, Queue Write) WRO
        8-24-93     3.5.10 Traces write direct to screen          WRO
        9-7-93    Converted to 32 bit                             WRO
        11-1-93   Modified to keep stats temporarily in memory    TRH
        7-8-94      4.00a Versacom config timing and queue probs  WRO
        7-8-94      4.00b Changed to use our queue                WRO
        7-23-94     Added support for L and G LCU                 WRO
        8-29-94     Check for Emetcon freq moved to main startup  WRO
        9-1-94      Added Spark support                           WRO
        9-6-94      Fixed CCU-711 response bug                    WRO
        4-21-95     Fixed outmessage timing bug                   WRO
        5-19-95     Fixed Queue Inhibit, TZ and VCOM DTRAN timing WRO
        11-20-95    Added Semaphore protection to status flags    WRO
        12-2-95     Added support for DIO24 Output                WRO
        12-11-95    Added packed actins and flag                  WRO
        2-12-96     Added time to queue flush messages            WRO
        5-8-96      Changed sense of autodst and added -A         WRO
        5-8-96      Fixed (Hopefully) Errant time in REQACL       WRO
        6-5-96      4.13a  Added more file handles and inlgrpq check  WRO
        6-11-96     4.13b  Changed queing priority                WRO
        6-11-96     4.13b  Fixed remote previous day statistics   WRO
        6-18-96     4.13c  Fixed Inhibited remote queue problem   WRO
        8-20-96     4.13d  Fixed Class/Division in VCONFIG        WRO
        11-19-96    5.0    Initial Version                        WRO
        09-19-97    5.0a   Modified TCP/IP startup code           WRO
        11-5-97     5.0b   Allowed controls to VTU's              WRO
        3-98        5.01   RELEASE
        3-6-98      5.01a  ESCA/Welco Serial in support           WRO
        3-9-98      5.01b  Added TAP Protocol Support             WRO
        6-15-98     5.01c  Fixed dual dial up site bug            WRO
        7-24-98     5.01d  Fixed bug in CP&L Vax interface        WRO
        7-24-98     5.02   Release Version                        WRO
        8-21-98     5.02a  Added changes for MPC-XA21             BDW
        10-5-98     5.02a  Added support for HARDLOCK             RWN

        -------------------------------------------------------------

        07-07-99    0.90   Converted to Win32                     CGP


   -------------------------------------------------------------------- */

#pragma warning( disable : 4786 )

#include <windows.h>


#include <iostream>
#include <fstream>
using namespace std;

#include <process.h>

#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <crtdbg.h>

#include <stdio.h>
#include <conio.h>
#include <string>
#include <vector>

#include <rw/thr/thrfunc.h>
#include <rw/toolpro/winsock.h>

#ifdef HARDLOCK

// FIX FIX FIX #include "fastapi.h"
// FIX FIX FIX #include "Hlapi_c.h"
    #define MOD_ADDR    16393
    #include <time.h>  /* Used for the check of Hardlock only */
#endif

#include "color.h"
#include "cparms.h"
#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "portdecl.h"
#include "portverify.h"
#include "master.h"
#include "elogger.h"
#include "alarmlog.h"
#include "drp.h"

#include "perform.h"
#include "das08.h"

#include "portgui.h"
#include "logger.h"
#include "numstr.h"

/* define the global area */
#include "portglob.h"
#include "c_port_interface.h"
#include "mgr_port.h"
#include "mgr_device.h"
#include "mgr_route.h"

#include "port_base.h"
#include "port_shr.h"
#include "port_shr_ip.h"
#include "rtdb.h"
#include "dllbase.h"
#include "dlldev.h"
#include "msg_dbchg.h"
#include "msg_trace.h"

#include "eventlog.h"
#include "cparms.h"
#include "configparms.h"
#include "trx_711.h"
#include "utility.h"
#include "dllyukon.h"

#include "pilserver.h"
#include "msg_pcrequest.h"
#include "numstr.h"

#define DO_GATEWAYTHREAD               1
#define DO_PORTERINTERFACETHREAD       1
#define DO_DISPATCHTHREAD              1
#define DO_VCONFIGTHREAD               1
#define DO_PORTERGUICONNECTIONTHREAD   0    // CODE INCOMPLETE AS OF 11/13/02 CGP
#define DO_PORTERCONNECTIONTHREAD      1
#define DO_TIMESYNCTHREAD              1
#define DO_PERFTHREAD                  0    // DONT RUN THIS AS OF 11/13/02 CGP
#define DO_PERFUPDATETHREAD            1
#define DO_FILLERTHREAD                1
#define DO_PORTSHARING                 1
#define DO_VERIFICATIONTHREAD          1

#define DOUT_OUT TRUE

ULONG TimeSyncRate = 3600L;

static _CRT_ALLOC_HOOK pfnOldCrtAllocHook = NULL;

static int MyAllocHook(int nAllocType, void *pvData,
                       size_t nSize, int nBlockUse, long lRequest,
                       const unsigned char * szFileName, int nLine );



CTI_PORTTHREAD_FUNC_PTR PortThreadFactory(int);
void DisplayTraceList( CtiPortSPtr Port, RWTPtrSlist< CtiMessage > &traceList, bool consume);
void LoadPorterGlobals(void);
INT  RefreshPorterRTDB(void *ptr = NULL);
void DebugKeyEvent(KEY_EVENT_RECORD *ke);
RWCString GetDeviceName( ULONG id );
void KickPIL();
bool processInputFunction(CHAR Char);

extern void QueueThread (void *);
extern void KickerThread (void *);
extern void DispatchMsgHandlerThread(VOID *Arg);
extern HCTIQUEUE* QueueHandle(LONG pid);
extern void commFail(CtiDeviceSPtr &Device, INT state);

DLLIMPORT extern BOOL PorterQuit;

extern INT RunningInConsole;              // From portmain.cpp

// Some Global Manager types to allow us some RTDB stuff.
CtiPortManager     PortManager(PortThreadFactory);
CtiDeviceManager   DeviceManager;
CtiRouteManager    RouteManager;
vector< CtiPortShare * > PortShareManager;

RWThreadFunction _connThread;
RWThreadFunction _dispThread;
RWThreadFunction _guiThread;
RWThreadFunction _gwThread;
RWThreadFunction _pilThread;
RWThreadFunction _tsyncThread;
RWThreadFunction _perfThread;
RWThreadFunction _perfuThread;
RWThreadFunction _fillerThread;
RWThreadFunction _vconfThread;

RWThreadFunction _queueCCU711Thread;
RWThreadFunction _kickerCCU711Thread;

CtiPorterVerification PorterVerificationThread;


static RWWinSockInfo  winsock;

bool findTAPDevice(const long key, CtiDeviceSPtr devsptr, void *ptr)
{
    bool b = false;
    LONG PortNumber = (LONG) ptr;

    if( devsptr->getPortID() == PortNumber && devsptr->getType() == TYPE_TAPTERM )
    {
        b = true;
    }

    return b;
}

bool isTAPTermPort(LONG PortNumber)
{
    INT            i;
    bool           result = false;

    CtiDeviceManager::LockGuard  dev_guard(DeviceManager.getMux());
    result = DeviceManager.find(findTAPDevice, (void*)PortNumber);

    return result;
}

/* Routine to load all routes on a system */
static void applyLoadAllRoutes(const long portid, CtiPortSPtr Port, void *unusedPtr)
{
    extern INT LoadPortRoutes (USHORT Port);
    LoadPortRoutes(Port->getPortID());
    return;
}

static void applyPortVerify(const long unusedid, CtiPortSPtr Port, void *unusedPtr)
{
    Port->verifyPortIsRunnable(hPorterEvents[P_QUIT_EVENT]);
}

static void applyNewLoad(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr)
{
    /* Find out if we have a TAP terminal */
    ptPort->setTAP( isTAPTermPort(ptPort->getPortID()) );

    if((PorterDebugLevel & PORTER_DEBUG_VERBOSE) && ptPort->isTAP())
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " WARNING: " << ptPort->getName() << " has a TAP device on it." << endl;
        dout << " This currently sets the port to 7E1 mode for ALL devices on the port" << endl;
    }
    ptPort->verifyPortIsRunnable(hPorterEvents[P_QUIT_EVENT]);
}

static void applyPortShares(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr)
{
    if( (ptPort->getSharedPortType() == "acs") || (ptPort->getSharedPortType() == "ilex" ) )
    {
        CtiPortShare *tmpPortShare = CTIDBG_new CtiPortShareIP(ptPort, PORTSHARENEXUS + PortShareManager.size());
        if( tmpPortShare != NULL )
        {
            ((CtiPortShareIP *)tmpPortShare)->setIPPort(ptPort->getSharedSocketNumber());
            tmpPortShare->start();
            PortShareManager.push_back(tmpPortShare);
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Error initializing shared port for " << ptPort->getName() << endl;
        }
    }
}

static void applyDeviceColdStart(const long unusedid, CtiDeviceSPtr RemoteDevice, void *prtid)
{
    LONG portid = (LONG)prtid;

    if(portid == RemoteDevice->getPortID() && RemoteDevice->getType() == TYPE_CCU711 && !RemoteDevice->isInhibited())
    {
        if(RemoteDevice->getAddress() != CCUGLOBAL)
        {
            /* Cold Start */
            IDLCFunction (RemoteDevice, 0, DEST_BASE, COLD);
        }
    }
}

static void applyColdStart(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr)
{
    if(!ptPort->isInhibited())
    {
        DeviceManager.apply(applyDeviceColdStart, (void*) ptPort->getPortID());
    }
}

void applyDeviceQueuePurge(const long unusedid, CtiDeviceSPtr RemoteDevice, void *lprtid)
{
    extern bool findAllQueueEntries(void *unused, void* d);
    extern void cleanupOrphanOutMessages(void *unusedptr, void* d);

    LONG PortID = (LONG)lprtid;
    ULONG QueEntCnt = 0L;

    if(PortID == RemoteDevice->getPortID())
    {
        bool commsuccess = false;
        if(RemoteDevice->adjustCommCounts( commsuccess, false ))
        {
            commFail(RemoteDevice, (commsuccess ? CLOSED : OPENED));
        }

        if(RemoteDevice->getType() == TYPE_CCU711)
        {
            CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)RemoteDevice->getTrxInfo();

            if(pInfo != NULL)
            {
                QueryQueue(pInfo->QueueHandle, &QueEntCnt);
                if(QueEntCnt)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "    CCU:  " << RemoteDevice->getName() << "  PURGING " << QueEntCnt << " queue queue entries" << endl;
                    }
                    CleanQueue(pInfo->QueueHandle, NULL, findAllQueueEntries, cleanupOrphanOutMessages);
                    // PurgeQueue(pInfo->QueueHandle);
                }

                QueryQueue(pInfo->ActinQueueHandle, &QueEntCnt);
                if(QueEntCnt)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "    CCU:  " << RemoteDevice->getName() << "  PURGING " << QueEntCnt << " actin queue entries" << endl;
                    }
                    CleanQueue(pInfo->ActinQueueHandle, NULL, findAllQueueEntries, cleanupOrphanOutMessages);
                    //PurgeQueue(pInfo->ActinQueueHandle);
                }

                //  make sure we clear out the pending bits - otherwise the device will refuse any new queued requests
                if(pInfo->GetStatus(INLGRPQ))
                {
                    pInfo->ClearStatus(INLGRPQ);
                }
            }
        }
    }
}

void applyPortQueuePurge(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr)
{
    extern bool findAllQueueEntries(void *unused, void* d);
    extern void cleanupOrphanOutMessages(void *unusedptr, void* d);

    LONG id = (LONG)unusedPtr;

    if( (id != 0 && ptPort->getPortID() != id) )
    {
        // Skip it!
        return;
    }

    if(!ptPort->isInhibited())
    {
        ULONG QueEntCnt = 0L;
        /* Print out the port queue information */
        QueryQueue(*QueueHandle(ptPort->getPortID()), &QueEntCnt);

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port: " << setw(2) << ptPort->getPortID() << " / " << ptPort->getName() << " PURGING " << QueEntCnt << " port queue entries" << endl;
        }

        CleanQueue(*QueueHandle(ptPort->getPortID()), NULL, findAllQueueEntries, cleanupOrphanOutMessages);
        // PurgeQueue(*QueueHandle(ptPort->getPortID()));

        DeviceManager.apply(applyDeviceQueuePurge,(void*)ptPort->getPortID());
    }
}

void applyDeviceInitFail(const long unusedid, CtiDeviceSPtr RemoteDevice, void *lprtid)
{
    LONG PortID = (LONG)lprtid;

    if(PortID == RemoteDevice->getPortID())
    {
        bool commsuccess = false;
        if(RemoteDevice->adjustCommCounts( commsuccess, false ))
        {
            commFail(RemoteDevice, (commsuccess ? CLOSED : OPENED));
        }
    }
}

void applyPortInitFail(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr)
{
    LONG id = (LONG)unusedPtr;

    if( (id != 0 && ptPort->getPortID() != id) )
    {
        // Skip it!
        return;
    }

    if(!ptPort->isInhibited())
    {
        if(PorterDebugLevel & PORTER_DEBUG_COMMFAIL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port: " << setw(2) << ptPort->getPortID() << " / " << ptPort->getName() << " comm failing all attached comm status points" << endl;
        }

        DeviceManager.apply(applyDeviceInitFail, (void*)ptPort->getPortID());
    }
}

static char* metric_names[] = {
    "   WAIT        ",
    "   RESULT      ",
    "   QUEUED      ",
    "   ACTIN       ",
    "   AWORD       ",
    "   BWORD       ",
    "   DTRAN       ",
    "   RCONT       ",
    "   RIPPLE      ",
    "   STAGE       ",
    "   VERSACOM    ",
    "   TSYNC       ",
    "   REMS        ",
    "   FISHERPIERCE",
    "   EN/DECODED  ",
    "   COMMANDCODE "
};

void applyDeviceQueueReport(const long unusedid, CtiDeviceSPtr RemoteDevice, void *lprtid)
{
    LONG PortID = (LONG)lprtid;
    ULONG QueEntCnt = 0L;

    if(lprtid == NULL || PortID == RemoteDevice->getPortID())
    {
        if(RemoteDevice->getType() == TYPE_CCU711)
        {
            CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)RemoteDevice->getTrxInfo();

            if(pInfo != NULL)
            {
                QueryQueue (pInfo->QueueHandle, &QueEntCnt);
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "    CCU:  " << RemoteDevice->getName() << endl;
                    dout << "                   Queue Queue Entries:  " << QueEntCnt << endl;
                }
                QueryQueue (pInfo->ActinQueueHandle, &QueEntCnt);
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    CHAR oldfill = dout.fill('0');
                    dout << "                   Actin Queue Entries:  " << QueEntCnt << endl;
                    dout << "                   Status Byte:          " << hex << setw(4) << (int)pInfo->Status << endl;
                    dout.fill(oldfill);
                }
            }
        }

        QueEntCnt = RemoteDevice->queuedWorkCount();

        if(QueEntCnt > 0)
        {
            RWTime ent(RemoteDevice->getExclusion().getEvaluateNextAt());
            {

                CtiLockGuard<CtiLogger> doubt_guard(dout);
                // dout << "    Transmitter:  " << setw(50) << RemoteDevice->getName() << " queued commands:  " << setw(4) << QueEntCnt << " Evaluate Next at " << ent << (ent < ent.now() ? ". *** PAST DUE ***" : ".") << endl;
                dout << "  " << setw(4) << QueEntCnt  << " queued commands. Evaluate next at " << ent << ". Transmitter: " << RemoteDevice->getName() << (ent < ent.now() ? ". *** PAST DUE ***" + CtiNumStr(ent.now().seconds() - ent.seconds()) : ".") << endl;
            }
        }
    }
}

void applyDeviceLoadReport(const long unusedid, CtiDeviceSPtr RemoteDevice, void *lprtid)
{
    RWCString printStr;

    int sub, proc, orph;
    LONG PortID = (LONG)lprtid;

    if(lprtid == NULL || PortID == RemoteDevice->getPortID())
    {
        printStr = RWTime().asString() + " Device: " + CtiNumStr(RemoteDevice->getID()).spad(2) + " / " + RemoteDevice->getName() + "\n";

        for(int i = 0; i < 288; i++)
        {
            RemoteDevice->getQueueMetrics(i, sub, proc, orph);
            printStr += CtiNumStr(i).spad(2) + ", " + CtiNumStr(sub).spad(5) + ", " + CtiNumStr(proc).spad(5) + ", " + CtiNumStr(orph).spad(5) + "\n";
        }

        {
            CtiLockGuard<CtiLogger> bguard(blog);
            blog << printStr << endl;
        }
    }
}

void applyPortQueueReport(const long unusedid, CtiPortSPtr ptPort, void *passedPtr)
{
    RWCString printStr;

    /* Report on the state of the queues */

    if(!ptPort->isInhibited())
    {
        ULONG QueEntCnt = ptPort->queueCount();
        /* Print out the port queue information */

        CtiQueueAnalysis_t qa;
        memset(&qa, 0, sizeof(qa));

        ptPort->applyPortQueue(&qa, applyPortQueueOutMessageReport);

        #if 0
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);

            dout << RWTime() << " Port " << ptPort->getName() << endl;
            for(int pri = 1; pri < 16; pri++)
            {
                if(qa.priority_count[pri] > 0)
                {
                    dout << "  Priority " << setw(3) << pri << " OM Count " << setw(5) << qa.priority_count[pri] << endl;
                }
            }

            // Now I need to look for some of the interesting metrics in the system!  These will be the first 16.
            // 0  #define WAIT            0x0001
            // 1  #define RESULT          0x0002
            // 2  #define QUEUED          0x0004
            // 3  #define ACTIN           0x0008
            // 4  #define AWORD           0x0010
            // 5  #define BWORD           0x0020
            // 6  #define DTRAN           0x0040
            // 7  #define RCONT           0x0080
            // 8  #define RIPPLE          0x0100
            // 09 #define STAGE           0x0200
            // 10 #define VERSACOM        0x0400
            // 11 #define TSYNC           0x0800
            // 12 #define REMS            0x1000   // This can never be used now.... CGP Corey.
            // 13 #define FISHERPIERCE    0x1000
            // 14 #define ENCODED         0x4000
            // 14 #define DECODED         0x4000
            // 15 #define COMMANDCODE     0x8000

            int pos;
            for(pos = 0; pos < 16; pos++)
            {
                if(qa.metrics[pos])
                    dout << metric_names[pos] << "           OM Count " << setw(5) << qa.metrics[pos] << endl;
            }
        }
        #endif

        {
            printStr = RWTime().asString() + " Port: " + CtiNumStr(ptPort->getPortID()).spad(2) + " / " + ptPort->getName() +
                                             " Port Queue Entries:  " + CtiNumStr(QueEntCnt).spad(4);

            if(ptPort->getConnectedDevice())
            {
                printStr += ". Connected To: " + GetDeviceName(ptPort->getConnectedDevice());
            }
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << printStr << endl;
    }
}

void applyPortLoadReport(const long unusedid, CtiPortSPtr ptPort, void *passedPtr)
{
    RWCString printStr;

    /* Report on the state of the queues */

    if(!ptPort->isInhibited())
    {
        int sub, proc, orph;

        printStr = RWTime().asString() + " Port: " + CtiNumStr(ptPort->getPortID()).spad(2) + " / " + ptPort->getName() + "\n";

        for(int i = 0; i < 288; i++)
        {
            ptPort->getQueueMetrics(i, sub, proc, orph);
            printStr += CtiNumStr(i).spad(2) + ", " + CtiNumStr(sub).spad(5) + ", " + CtiNumStr(proc).spad(5) + ", " + CtiNumStr(orph).spad(5) + "\n";
        }
    }

    {
        CtiLockGuard<CtiLogger> bguard(blog);
        blog << printStr << endl;
    }
}

bool findCCU711(const long key, CtiDeviceSPtr devsptr, void *ptr)
{
    bool bStatus = false;

    if( devsptr->getType() == TYPE_CCU711 )
    {
        bStatus = true;
    }
    return bStatus;
}

INT PorterMainFunction (INT argc, CHAR **argv)
{
    /* Misc Definitions */
    INT    i, j;
    extern USHORT PrintLogEvent;
    time_t last_print = 0;

    BYTE RefKey[8] = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
    BYTE VerKey[8] = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

    /* New stuff for YUKON kbd handling */
    INPUT_RECORD      inRecord;
    HANDLE            hStdIn = GetStdHandle(STD_INPUT_HANDLE);
    DWORD             Count;
    CHAR              Char;

    /* Print out the program name and revison */
    identifyProject(CompileInfo);

    {
        char tstr[256];
        sprintf(tstr,"Port Control - YUKON (Build %d.%d.%d)", CompileInfo.major, CompileInfo.minor, CompileInfo.build);
        SetConsoleTitle( tstr );
    }

    /* check for various flags */
    if(argc > 1)
    {
        for(i = 1; i < argc; i++)
        {
            if(!(stricmp (argv[i], "/D")))
            {
                Double = TRUE;
                continue;
            }
            if(!(stricmp (argv[i], "/T")))
            {
                TraceFlag = TRUE;
                continue;
            }
            if(!(stricmp (argv[i], "/E")))
            {
                TraceErrorsOnly = TRUE;
                continue;
            }
            if(!(stricmp (argv[i], "/C")))
            {
                ResetAll711s = TRUE;
                continue;
            }
            if(!(stricmp (argv[i], "/R")))
            {
                LoadRoutes = TRUE;
                continue;
            }
            if(!(stricmp (argv[i], "/A")))
            {
                AutoDSTChange = TRUE;
                continue;
            }
            if(!(stricmp (argv[i], "-A")))
            {
                AutoDSTChange = FALSE;
                continue;
            }

            if(!(stricmp (argv[i], "/N")))
            {
                NoQueing = TRUE;
                continue;
            }
            if(!(stricmp (argv[i], "/W")))
            {
                VCUWait = TRUE;
                continue;
            }
            if(!(stricmp (argv[i], "/F")))
            {
                AmpFailOver |= AMP_FAILOVER;
                continue;
            }
            if(!(stricmp (argv[i], "/S")))
            {
                AmpFailOver |= AMP_SWAPPING;
                continue;
            }
            if(!(stricmp (argv[i], "/L")))
            {
                PrintLogEvent = FALSE;
                continue;
            }
            if(!(stricmp (argv[i], "/P")))
            {
                PackActins = TRUE;
                continue;
            }
            if(!(strcmp (argv[i], "/?")))
            {
                cout << "/C       Reset (Cold Start) all 711's in system" << endl;
                cout << "/D       Emetcon Double - CCU's Double feild transmissions" << endl;
                cout << "/E       Trace Communication for failed communications (errors) only" << endl;
                cout << "/L       Logging to printer on any event" << endl;
                cout << "/N       No queueing to CCU 711's" << endl;
                cout << "/R       Download emetcon routes" << endl;
                cout << "/T       Trace Communication for all ports / remotes" << endl;
                cout << "/W       VCU Wait - VCU transmissions on same port must wait for completion" << endl;
                cout << endl;
                exit(-1);
            }
        }
    }

    /* Allow a shitload of files to be open */
    CTISetMaxFH (200);

    /* make it clear who is just about the boss */
    CTISetPriority (PRTYS_THREAD, PRTYC_TIMECRITICAL, 0, 0);

    /* LOGON TO HARDLOCK */
#ifdef HARDLOCK
    Result = HL_LOGIN (MOD_ADDR, LOCAL_DEVICE, RefKey, VerKey);
    if(Result != STATUS_OK)
    {
        fprintf(stdout, "Required Hardlock Not Detected.\n");
        CTIExit (EXIT_PROCESS, -1);
    }
#endif

    /* Open the error message file */
    if((i = InitError ()) != NORMAL)
    {
        PrintError ((USHORT)i);
        CTIExit (EXIT_PROCESS, -1);
    }

    if(RefreshPorterRTDB())             // Loads globals and the RTDB
    {
        return -1;
    }

    SET_CRT_OUTPUT_MODES;
    if(gConfigParms.isOpt("DEBUG_MEMORY") && !gConfigParms.getValueAsString("DEBUG_MEMORY").compareTo("true", RWCString::ignoreCase) )
        ENABLE_CRT_SHUTDOWN_CHECK;


    pfnOldCrtAllocHook = _CrtSetAllocHook(MyAllocHook);

    /* A new guy with Yukon,  start a thread to handle GUI requests.  */
    /* This is a future project as of 070799, allowing a GUI to interface with Porter
     * to tweak parameters
     */
    if(DO_PORTERGUICONNECTIONTHREAD)
    {
        _guiThread = rwMakeThreadFunction( PorterGUIConnectionThread, (void*)NULL );
        _guiThread.start();
    }

    if(DO_GATEWAYTHREAD && !gConfigParms.getValueAsString("PORTER_GATEWAY_SUPPORT").compareTo("true", RWCString::ignoreCase))
    {
        _gwThread = rwMakeThreadFunction( PorterGWThread, (void*)NULL );
        _gwThread.start();
    }

    /* Another new Yukon Thread:
     * This thread manages connections to iMacs and other RWCollectable message senders
     * This guy is the PIL
     */
    if(DO_PORTERINTERFACETHREAD)
    {
        _pilThread = rwMakeThreadFunction( PorterInterfaceThread, (void*)NULL );
        _pilThread.start();
    }

    if(DO_DISPATCHTHREAD)
    {
        _dispThread = rwMakeThreadFunction( DispatchMsgHandlerThread, (void*)NULL );
        _dispThread.start();
    }

    /* Start the time sync thread */
    if(DO_TIMESYNCTHREAD)
    {
        _tsyncThread = rwMakeThreadFunction( TimeSyncThread, (void*)NULL );
        _tsyncThread.start();
    }

    /* Start the performance update thread */
    if(DO_PERFUPDATETHREAD)
    {
        _perfuThread = rwMakeThreadFunction( PerfUpdateThread, (void*)NULL );
        _perfuThread.start();
    }

    /* Start the performance thread */
    if(DO_PERFTHREAD)
    {
        _perfThread = rwMakeThreadFunction( PerfThread, (void*)NULL );
        _perfThread.start();
    }

    /* Start the verification thread */
    if(DO_VERIFICATIONTHREAD)
    {
        PorterVerificationThread.start();
    }

    /*
     *  Now start up the ports' thread
     */
    try
    {
        PortManager.apply( applyPortVerify, NULL );
        if(DO_PORTSHARING)
        {
            PortManager.apply( applyPortShares, NULL );
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    /* all of the port threads are started so now start first socket connection handler */
    if(DO_PORTERCONNECTIONTHREAD)
    {
        _connThread = rwMakeThreadFunction( PorterConnectionThread, (void*)NULL );
        _connThread.start();
    }

    /* Check if we need to start the filler thread */
    if(gConfigParms.isOpt("PORTER_START_FILLERTHREAD") && !(stricmp ("TRUE", gConfigParms.getValueAsString("PORTER_START_FILLERTHREAD").data())))
    {
        if(DO_FILLERTHREAD)  /* go ahead and start the thread passing the variable */
        {
            _fillerThread = rwMakeThreadFunction( FillerThread, (void*)NULL );
            _fillerThread.start();
        }
    }

    /* Check if we need to start the versacom config thread */
    if( !((gConfigParms.getValueAsString("PORTER_START_VCONFIGTHREAD")).isNull()) && !(stricmp ("TRUE", gConfigParms.getValueAsString("PORTER_START_VCONFIGTHREAD").data())))
    {
        if(DO_VCONFIGTHREAD) /* go ahead and start the thread passing the variable */
        {
            _vconfThread = rwMakeThreadFunction( VConfigThread, (void*)NULL );
            _vconfThread.start();
        }
    }

    if(gLogPorts)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Trace is now FORCED on by configuration entry YUKON_LOG_PORTS" << endl;
        }
        TraceFlag = TRUE;
    }
    else if(TraceFlag)
    {
        if(TraceErrorsOnly)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Trace is now on for errors only" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Trace is now on for all messages" << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Trace is now off for all messages" << endl;
    }

    /* Startup is done so main process becomes input thread */
    for(;!PorterQuit;)
    {
        if(RunningInConsole == FALSE)
        {
            CTISleep (1000L);
        }
        else
        {
#ifdef HARDLOCK
            time(&timeStart);
#endif

            if(PeekConsoleInput(hStdIn, &inRecord, 1L, &Count) && (Count > 0))     // There is something ther if we succeed.
            {
                if(inRecord.EventType != KEY_EVENT)
                {
#ifdef HARDLOCK
                    /* HARDLOCK check once every 5 minutes */
                    time (&timeStop);
                    if(difftime(timeStop, timeStart) >= 300)
                    {
                        if(HL_AVAIL () != STATUS_OK)
                        {
                            fprintf(stdout, "Required Hardlock Not Detected.\n");
                            Result = HL_LOGOUT ();
                            CTIExit (EXIT_PROCESS, -1);
                        }
                        time (&timeStart);
                    }
#endif

                    ReadConsoleInput(hStdIn, &inRecord, 1L, &Count);     // Read out the offending input.
                }
                else    // These are to only kind we care about!
                {
                    ReadConsoleInput(hStdIn, &inRecord, 1L, &Count);

                    if( inRecord.Event.KeyEvent.bKeyDown != FALSE )
                    {
                        if((inRecord.Event.KeyEvent.dwControlKeyState & (LEFT_ALT_PRESSED | RIGHT_ALT_PRESSED)))
                        {
                            Char = tolower(inRecord.Event.KeyEvent.uChar.AsciiChar);
                        }
                        else
                        {
                            Char = 0;
                        }

                        if( processInputFunction(Char) )
                        {
                            if(Char != 0)
                            {
                                fprintf(stdout, " No ALT 0x%04X, 0x%04X, 0x%02X 0x%08X\n",inRecord.Event.KeyEvent.wVirtualScanCode,inRecord.Event.KeyEvent.wVirtualKeyCode, Char, inRecord.Event.KeyEvent.dwControlKeyState);
                            }
                        }
                    }
                }
            }
        }

        /*
        if( last_print + 60 <= ::time(0) )
        {
            last_print = ::time(0);
            processInputFunction(0x79);  //  do an alt-y every 60 seconds
        }
        */

        CTISleep(250);
    }

    PorterCleanUp(0);

    _CrtSetAllocHook(pfnOldCrtAllocHook);

#ifdef HARDLOCK

    // LOGOUT HARDLOCK
    Result = HL_LOGOUT ();
#endif

    return 0;
}


/*
 *  called in an atexit() routine to clean up the schtick.
 */
VOID APIENTRY PorterCleanUp (ULONG Reason)
{
    PorterQuit = TRUE;
    SetEvent( hPorterEvents[P_QUIT_EVENT] );
    PorterListenNexus.CTINexusClose();


    if(PortShareManager.size() > 0)
    {
        //  delete/stop the shared ports
        while(PortShareManager.size() > 0)
        {
            delete PortShareManager.back();
            PortShareManager.pop_back();
        }
    }

    if(_gwThread.isValid())                 _gwThread.requestCancellation(200);
    if(_pilThread.isValid())                _pilThread.requestCancellation(200);
    if(_dispThread.isValid())               _dispThread.requestCancellation(200);
    if(_connThread.isValid())               _connThread.requestCancellation(200);
    if(_guiThread.isValid())                _guiThread.requestCancellation(200);
    if(_tsyncThread.isValid())              _tsyncThread.requestCancellation(200);
    if(_perfThread.isValid())               _perfThread.requestCancellation(200);
    if(_perfuThread.isValid())              _perfuThread.requestCancellation(200);
    if(_fillerThread.isValid())             _fillerThread.requestCancellation(200);
    if(_vconfThread.isValid())              _vconfThread.requestCancellation(200);
    if(_queueCCU711Thread.isValid())        _queueCCU711Thread.requestCancellation(200);
    if(_kickerCCU711Thread.isValid())       _kickerCCU711Thread.requestCancellation(200);

    if(PorterVerificationThread.isRunning())    PorterVerificationThread.interrupt(CtiPorterVerification::SHUTDOWN);

    if(_connThread.isValid())
    {
        if(_connThread.join(2000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _connThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _connThread shutdown" << endl;
        }
    }

    if(_guiThread.isValid())
    {
        if(_guiThread.join(2000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _guiThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _guiThread shutdown" << endl;
        }
    }

    if(_perfThread.isValid())
    {
        if(_perfThread.join(2000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _perfThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _perfThread shutdown" << endl;
        }
    }

    if(_perfuThread.isValid())
    {
        if(_perfuThread.join(2000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _perfuThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _perfuThread shutdown" << endl;
        }
    }

    if(_tsyncThread.isValid())
    {
        if(_tsyncThread.join(2000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _tsyncThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _tsyncThread shutdown" << endl;
        }
    }

    if(_fillerThread.isValid())
    {
        if(_fillerThread.join(2000) != RW_THR_COMPLETED)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _fillerThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _fillerThread shutdown" << endl;
        }
    }

    if(_vconfThread.isValid())
    {
        if(_vconfThread.join(2000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _vconfThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _vconfThread shutdown" << endl;
        }
    }


    if(_dispThread.isValid())
    {
        if(_dispThread.join(15000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _dispThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _dispThread shutdown" << endl;
        }
    }

    if(_gwThread.isValid())
    {
        if(_gwThread.join(15000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _gwThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _gwThread shutdown" << endl;
        }
    }

    if(_pilThread.isValid())
    {
        if(_pilThread.join(15000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _pilThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _pilThread shutdown" << endl;
        }
    }

    if(_queueCCU711Thread.isValid())
    {
        if(_queueCCU711Thread.join(1500) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _queueCCU711Thread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _queueCCU711Thread shutdown" << endl;
        }
    }

    if(_kickerCCU711Thread.isValid())
    {
        if(_kickerCCU711Thread.join(1500) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _kickerCCU711Thread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _kickerCCU711Thread shutdown" << endl;
        }
    }

    if(DO_VERIFICATIONTHREAD)
    {
        PorterVerificationThread.join();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _verificationThread shutdown" << endl;
        }
    }


    {
        CtiPortManager::LockGuard prt_guard(PortManager.getMux()); // Protect our destruction!
        PortManager.haltLogs();
    }

    Sleep(3000);

    // Make sure all the logs get output and done!
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " logger shutdown" << endl;
    }

    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();

    slog.interrupt(CtiThread::SHUTDOWN);
    slog.join();

    blog.interrupt(CtiThread::SHUTDOWN);
    blog.join();
}



void DebugKeyEvent(KEY_EVENT_RECORD *ke)
{
    if(ke->bKeyDown)  fprintf(stdout, "Key     DOWN\n");
    else              fprintf(stdout, "Key NOT DOWN\n");

    fprintf(stdout, "Repeat Count = %d\n",ke->wRepeatCount);
    fprintf(stdout, "Key  Code %d = 0x%04X\n", ke->wVirtualKeyCode, ke->wVirtualKeyCode);
    fprintf(stdout, "Scan Code %d = 0x%04X\n", ke->wVirtualScanCode, ke->wVirtualScanCode);
    fprintf(stdout, "Char         = %x\n",ke->uChar.AsciiChar);

    fprintf(stdout, "Control State: 0x%08X\n", ke->dwControlKeyState);

    if(ke->dwControlKeyState & CAPSLOCK_ON              )
    {
        fprintf(stdout, "\tCapsLock ON\n");
    }
    if(ke->dwControlKeyState & ENHANCED_KEY             )
    {
        fprintf(stdout, "\tEnhanced Key\n");
    }
    if(ke->dwControlKeyState & LEFT_ALT_PRESSED         )
    {
        fprintf(stdout, "\tL Alt      \n");
    }
    if(ke->dwControlKeyState & LEFT_CTRL_PRESSED        )
    {
        fprintf(stdout, "\tL Ctrl     \n");
    }
    if(ke->dwControlKeyState & NUMLOCK_ON               )
    {
        fprintf(stdout, "\tNumLock ON\n");
    }
    if(ke->dwControlKeyState & RIGHT_ALT_PRESSED        )
    {
        fprintf(stdout, "\tR Alt      \n");
    }
    if(ke->dwControlKeyState & RIGHT_CTRL_PRESSED       )
    {
        fprintf(stdout, "\tR Ctrl     \n");
    }
    if(ke->dwControlKeyState & SCROLLLOCK_ON            )
    {
        fprintf(stdout, "\tScroll Lock\n");
    }
    if(ke->dwControlKeyState & SHIFT_PRESSED            )
    {
        fprintf(stdout, "\tShift      \n");
    }

    fprintf(stdout, "\n");

}

static void applyRepeaterAutoRole(const long unusedid, CtiDeviceSPtr autoRoleDevice, void *prtid)
{
    extern CtiPILServer     PIL;

    if( (autoRoleDevice->getType() == TYPE_REPEATER800 || autoRoleDevice->getType() == TYPE_REPEATER900) &&
        !autoRoleDevice->isInhibited())
    {
        // We should fire off a message to PIL asking for a role configuration!
        PIL.putQueue( new CtiRequestMsg(autoRoleDevice->getID(), "putconfig emetcon install") );

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << autoRoleDevice->getName() << " role table being refreshed" << endl;
        }
    }
}

INT RefreshPorterRTDB(void *ptr)
{
    extern CtiPILServer     PIL;
    bool autoRole = false;                              // Set to true if routes might have changed and or we need to download based on time!
    static RWTime lastAutoRole(rwEpoch);        // This time is used to trigger timed downloads of repeater roles.

    INT   i;
    INT   status = NORMAL;
    DWORD dwWait;
    LONG  id = 0;

    CtiDBChangeMsg *pChg = (CtiDBChangeMsg *)ptr;

    // Reload the globals used by the porter app too.
    InitYukonBaseGlobals();
    LoadPorterGlobals();
    ResetBreakAlloc();          // Make certain the debug library does not break us.

    if( !PorterQuit && (pChg == NULL || (pChg->getDatabase() == ChangeStateGroupDb)) )
    {
        ReloadStateNames();
    }

    if(!PorterQuit && (pChg == NULL || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_PORT)) )
    {
        if(pChg != NULL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Reloading all ports based upon db change" << endl;
            }
        }

        CtiPortManager::LockGuard  guard(PortManager.getMux());
        PortManager.RefreshList();
        // PortManager.DumpList();
    }

    if(!PorterQuit && (pChg == NULL || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_DEVICE) ) )
    {
        CtiDeviceManager::LockGuard  guard(DeviceManager.getMux());

        LONG chgid = 0;
        RWCString catstr;
        RWCString devstr;

        if(pChg)
        {
            chgid = pChg->getId();
            catstr = pChg->getCategory();
            devstr = pChg->getObjectType();
        }

        DeviceManager.refresh(DeviceFactory, isNotADevice, NULL, chgid, catstr, devstr);
    }

    if(!PorterQuit)
    {
        if(pChg == NULL || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_ROUTE) )
        {
            if(pChg != NULL)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Reloading all routes based upon db change" << endl;
                    dout << RWTime() << " All repeaters will have their role table refreshed." << endl;
                }
                autoRole = true;
            }

            if(gConfigParms.getValueAsULong("PORTER_AUTOROLE_RATE", 0) > 0 && RWTime().seconds() > lastAutoRole.seconds() + gConfigParms.getValueAsULong("PORTER_AUTOROLE_RATE") )
            {
                autoRole = true;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " AutoRole Download timer (master.cfg: PORTER_AUTOROLE_RATE) expired." << endl;
                    dout << RWTime() << " All repeaters will have their role table refreshed." << endl;
                }
            }

            RouteManager.RefreshList();
            // RouteManager.DumpList();
        }

        /* Make routes associate with devices */
        attachRouteManagerToDevices(&DeviceManager, &RouteManager);
        attachTransmitterDeviceToRoutes(&DeviceManager, &RouteManager);

        if(autoRole)
        {
            try
            {
                lastAutoRole = lastAutoRole.now();      // Update our static variable.

                DeviceManager.apply(applyRepeaterAutoRole,NULL);
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }

        if(pChg != NULL && (pChg->getDatabase() == ChangePointDb))
        {
            LONG paoid = GetPAOIdOfPoint( pChg->getId() );

            if(paoid != 0)
            {
                CtiDeviceManager::LockGuard  dev_guard(DeviceManager.getMux());       // Protect our iteration!
                CtiDeviceSPtr pDevToReset = DeviceManager.getEqual( paoid );
                if(pDevToReset)
                {
                    if(pChg->getTypeOfChange() == ChangeTypeDelete)
                    {
                        pDevToReset->orphanDevicePoint(pChg->getId());
                    }

                    pDevToReset->ResetDevicePoints();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Reset device " << pDevToReset->getName() << "'s pointmanager due to pointchange on point " << pChg->getId() << endl;
                    }
                }
            }
        }
    }
    else
    {
        return -1;
    }

    /* see if we need to start process's for queuing */
    {
        CtiDeviceManager::LockGuard  guard(DeviceManager.getMux());        // Protect our iteration!

        if(!(_queueCCU711Thread.isValid()) && DeviceManager.find(findCCU711, NULL))
        {
            _queueCCU711Thread = rwMakeThreadFunction( QueueThread, (void*)NULL );
            _queueCCU711Thread.start();

            if( !(_kickerCCU711Thread.isValid()) )
            {
                _kickerCCU711Thread = rwMakeThreadFunction( KickerThread, (void*)NULL );
                _kickerCCU711Thread.start();
            }
        }
    }

    try
    {
        PortManager.apply( applyNewLoad, NULL );
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if(pChg != NULL)
    {
        delete pChg;
    }

    return status;
}


void LoadPorterGlobals(void)
{
    INT  j;
    char temp[80];
    RWCString Temp;
    /* Definitions to get environment variables */
    CHAR Info[50] = {'\0'};
    PCHAR PInfo;


    // Causes all normal error and dout traffic to be routed to the porter.logxx files
    cParmPorterServiceLog = FALSE;
    if(!(Temp = gConfigParms.getValueAsString("PORTER_SERVICE_LOG")).isNull())
    {
        if(!stricmp("TRUE", Temp.data()))
        {
            cParmPorterServiceLog = TRUE;
        }
    }

    if(!(Temp = gConfigParms.getValueAsString("PORTER_RELOAD_RATE")).isNull())
    {
        PorterRefreshRate = atoi(Temp.data());
    }

    if( !(Temp = gConfigParms.getValueAsString("PIL_QUEUE_SIZE")).isNull())
    {
        PILMaxQueueSize = atoi(Temp.data());
    }
    else
    {
        PILMaxQueueSize = 1000;
    }

    /* Check the extra delay flag */
    if( !(Temp = gConfigParms.getValueAsString("PORTER_EXTRATIMEOUT")).isNull())
    {
        ExtraTimeOut = atoi(Temp.data());
    }
    else
    {
        ExtraTimeOut = 0;
    }

    /* Check the number of queue octets allowed */
    if(!(Temp = gConfigParms.getValueAsString("PORTER_MAXOCTS")).isNull())
    {
        MaxOcts = atoi(Temp.data());
        if(MaxOcts < 81)
        {
            MaxOcts = 81;
        }
        else if(MaxOcts > 261)
        {
            MaxOcts = 261;
        }
    }
    else
    {
        MaxOcts = 261;
    }

    /* Check if we need to start the TCP/IP Interface */
    if(!(Temp = gConfigParms.getValueAsString("PORTER_TCPIP")).isNull())
    {
        if(!(stricmp ("YES", Temp.data())) || (!(stricmp ("SES92", Temp.data()))))
        {
            StartTCPIP = TCP_SES92;
        }
        else if(!(stricmp ("CCU710", Temp.data())))
        {
            StartTCPIP = TCP_CCU710;
        }
        else if(!(stricmp ("WELCO", Temp.data())))
        {
            StartTCPIP = TCP_WELCO;
        }
        else
        {
            fprintf(stdout, "Unknown TCP/IP Interface Type\n");
        }
    }

    /* Resolve the stuff that we need for DIO24 Output */
    if(!(Temp = gConfigParms.getValueAsString("DIO24")).isNull())
    {
        strncpy (Info, Temp.data(), 49);
        /* Start the parcing process */
        if(sscanf (strtok (Info, ",;"), "%x", &DIO24Base) != 1)
        {
            DIO24Base = DAS08BASE;
            Info[0] = '\0';
        }
    }
    else if(!(Temp = gConfigParms.getValueAsString("DAS08")).isNull())
    {
        strncpy (Info, Temp.data(), 49);
        /* Start the tokenizing */
        if(sscanf (strtok (Info, ",;"), "%x", &DIO24Base) != 1)
        {
            Info[0] = '\0';
        }
        else
        {
            DIO24Base += DIO24OFFSET;
        }
    }
    else
    {
        Info[0] = '\0';
    }

    /* Unravel the mode flags */
    if(Info[0] == '\0')
    {
        DIO24Mode[0] = 0xffff;
    }
    else
    {
        j = 0;
        while((PInfo = strtok (NULL, ";,")) != NULL && j < 9)
        {
            if(sscanf (PInfo, "%hx", &DIO24Mode[j++]) != 1)
            {
                break;
            }
        }
    }


    /* Check if we are using L and G LCU's */
    if(!(Temp = gConfigParms.getValueAsString("PORTER_LANDGLCUS")).isNull() && (!(stricmp ("YES", Temp.data()))))
    {
        LAndGLCUs = TRUE;
    }

    /*  set the frequency info -- Defaults to 12.5 */
    if(!(Temp = gConfigParms.getValueAsString("PORTER_FREQ")).isNull())
    {
        /* Decode the system frequency */
        if(!(stricmp (Temp.data(), "7.3")))
        {
            DLCFreq1 = 125;
            DLCFreq2 = 51;
        }
        else if(!(stricmp (Temp.data(), "7.8")))
        {
            DLCFreq1 = 125;
            DLCFreq2 = 48;
        }
        else if(!(stricmp (Temp.data(), "8.3")))
        {
            DLCFreq1 = 49;
            DLCFreq2 = 12;
        }
        else if(!(stricmp (Temp.data(), "8.8")))
        {
            DLCFreq1 = 124;
            DLCFreq2 = 55;
        }
        else if(!(stricmp (Temp.data(), "9.6")))
        {
            DLCFreq1 = 17;
            DLCFreq2 = 7;
        }
        else if(!(stricmp (Temp.data(), "10.4")))
        {
            DLCFreq1 = 121;
            DLCFreq2 = 46;
        }
        else if(!(stricmp (Temp.data(), "12.5")))
        {
            DLCFreq1 = 94;
            DLCFreq2 = 37;
        }
        else if(!(stricmp (Temp.data(), "13.8")))
        {
            DLCFreq1 = 123;
            DLCFreq2 = 35;
        }
    }


    if(!(Temp = gConfigParms.getValueAsString("PORTER_DEBUGLEVEL")).isNull())
    {
        char *eptr;
        PorterDebugLevel = strtoul(Temp.data(), &eptr, 16);
    }


    if(!(Temp = gConfigParms.getValueAsString("PORTER_TSYNC_RATE")).isNull())
    {
        if(!(TimeSyncRate = atol (Temp.data())))
        {
            /* Unable to convert so assume "NOT EVER" */
            TimeSyncRate = 0L;
        }
    }


    if(!(Temp = gConfigParms.getValueAsString("PORTER_IGNORE_TCU5000_QUEUEBUSY")).isNull())
    {
        Temp.toLower();

        if( Temp == "true" || Temp == "yes")
        {
            gIgnoreTCU5X00QueFull = true;
        }
    }

    if(!(Temp = gConfigParms.getValueAsString("PORTER_IGNORE_TCU_QUEUEBUSY")).isNull())
    {
        Temp.toLower();

        if( Temp == "true" || Temp == "yes")
        {
            gIgnoreTCU5X00QueFull = true;
        }
    }


    if(!(Temp = gConfigParms.getValueAsString("PORTER_IDLC_ECHO_SUPPRESSION")).isNull())
    {
        Temp.toLower();

        if( Temp == "true" || Temp == "yes")
        {
            gIDLCEchoSuppression = true;
        }
    }

    /* Check if we need to start the TCP/IP Interface */
    if(!(Temp = gConfigParms.getValueAsString("PORTER_CCU_DELAY_FILE")).isNull())
    {
        gDelayDatFile = Temp;

    }

    if(!(Temp = gConfigParms.getValueAsString("PORTER_PORTINIT_QPURGE_DELAY")).isNull())
    {
        if(!(PorterPortInitQueuePurgeDelay = abs(atoi(Temp.data()))))
        {
            /* Unable to convert so assume default of 5 minutes (20 * 15s) */
            PorterPortInitQueuePurgeDelay = 20;
        }
    }




    if(getDebugLevel() & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Using CCU Delay information from " << gDelayDatFile << endl;
    }


    if(DebugLevel & 0x0001)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << "Loading porter globals: " << endl;
        dout << "PORTER_RELOAD_RATE    " << PorterRefreshRate << endl;
        dout << "PIL_QUEUE_SIZE        " << PILMaxQueueSize << endl;
        dout << "PORTER_EXTRATIMEOUT     " << ExtraTimeOut << endl;
        dout << "PORTER_MAXOCTS          " << MaxOcts << endl;
        dout << "PORTER_PORTER_TCPIP     " << StartTCPIP << endl;
    }
}




void DisplayTraceList( CtiPortSPtr Port, RWTPtrSlist< CtiMessage > &traceList, bool consume)
{
    try
    {
        Port->fileTraces(traceList);

        {
            int attempts = 5;
            RWMutexLock::TryLockGuard coutTryGuard(coutMux);

            while(!coutTryGuard.isAcquired() && attempts-- > 0 )
            {
                Sleep(100);
                coutTryGuard.tryAcquire();
            }

            for(size_t i = 0; i < traceList.entries(); i++)
            {
                CtiTraceMsg *&pTrace = ((CtiTraceMsg*&)traceList.at(i));
                SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), (WORD)pTrace->getAttributes());
                cout << pTrace->getTrace();

                if(pTrace->isEnd())
                {
                    cout << endl;
                }
            }

            SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_BLUE | FOREGROUND_GREEN | FOREGROUND_RED);
        }

        if(consume)
        {
            traceList.clearAndDestroy();
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}


CTI_PORTTHREAD_FUNC_PTR PortThreadFactory(int porttype)
{
    extern VOID PortPoolDialoutThread(void *pid);

    CTI_PORTTHREAD_FUNC_PTR fptr = PortThread;

    switch(porttype)
    {
    case PortTypeLocalDialBack:
        {
            fptr = PortDialbackThread;
            break;
        }
    case PortTypePoolDialout:
        {
            fptr = PortPoolDialoutThread;
            break;
        }
    }

    return fptr;
}

RWCString GetDeviceName( ULONG id )
{
    RWCString name;

    CtiDeviceSPtr pDev = DeviceManager.getEqual( id );

    if(pDev)
    {
        name = pDev->getName();
    }

    return name;
}

void KickPIL()
{
    // TEARDOWN

    if(_pilThread.isValid())
        _pilThread.requestCancellation(200);

    if(_pilThread.isValid())
    {
        if(_pilThread.join(15000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _pilThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " _pilThread shutdown" << endl;
        }
    }

    Sleep(2500);

    // AND RESTART

    if(DO_PORTERINTERFACETHREAD)
    {
        _pilThread = rwMakeThreadFunction( PorterInterfaceThread, (void*)NULL );
        _pilThread.start();
    }

    return;
}

bool processInputFunction(CHAR Char)
{
    bool process_fail = false;
    PSZ Environment;

    switch(Char)
    {
    case 0x68:              // alt - h
    case 0x3f:              // alt - ?
        /* Print some instructions */
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);

            dout << endl;
            dout << "Port Control" << endl << endl;
            dout << "Alt - C     Reset (cold start) all CCU 711's in system" << endl;
            dout << "Alt - D     Send Emetcon \"Doubles\" to field devices" << endl;
            dout << "Alt - E     Trace error communications only" << endl;
            dout << "Alt - F     Toggle trace filtering off, or reload from environment." << endl;
            dout << "             PORTER_TRACE_PORT    " << endl;
            dout << "             PORTER_TRACE_REMOTE  " << endl;
            dout << "Alt - H     This help screen" << endl;
            dout << "Alt - L     Toggle printer logging" << endl;
            dout << "Alt - P     Purge all port queues. (Careful)" << endl;
            dout << "Alt - Q     Display port queue counts / stats" << endl;
            dout << "Alt - R     Download all CCU Default Routes" << endl;
            dout << "Alt - S     Issue a system wide timesync" << endl;
            dout << "Alt - T     Trace all communications" << endl;
            dout << endl;

            break;
        }
    case 0x74:              // alt-t
        {
            TraceFlag = !TraceFlag;
            if(TraceFlag)
            {
                if(TraceErrorsOnly)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Trace is Now On for Errors Only" << endl;
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Trace is Now On for All Messages" << endl;
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Trace is Now Off for All Messages" << endl;
                }
            }

            break;
        }
    case 0x65:              // alt-e
        {
            TraceErrorsOnly = !TraceErrorsOnly;
            if(TraceErrorsOnly)
            {
                TraceFlag = TRUE;
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Trace is Now On for Errors Only" << endl;
            }
            else if(TraceFlag)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Trace is Now On for All Messages" << endl;
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Trace is Now Off for All Messages" << endl;
                }
            }

            break;

        }
    case 0x72:              // alt-r
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Downloading Routes to All CCU-711's" << endl;
            }
            PortManager.apply( applyLoadAllRoutes, NULL );
            break;
        }
    case 0x64:              // alt-d
        {
            Double = !Double;
            if(Double)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Commands will be sent double" << endl;
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Commands will not be sent double" << endl;
            }

            break;
        }
    case 0x66:              // alt-f trace filter.
        {
            if(TracePort || TraceRemote)
            {
                fprintf(stdout, "Trace filter is now off\n");
                TracePort = 0;
                TraceRemote = 0;
            }
            else
            {
                if(!(CTIScanEnv ("PORTER_TRACE_PORT", &Environment)))
                {
                    TracePort = atoi (Environment);
                    fprintf(stdout, "Filtering Traces for Port %ld\n", TracePort);
                }
                else if(!(CTIScanEnv ("PORTER_TRACE_REMOTE",  &Environment)))
                {
                    TraceRemote = atoi (Environment);
                    fprintf(stdout, "Filtering Traces for Remote %ld\n", TraceRemote);
                }
                else
                {
                    fprintf(stdout, "Neither PORTER_TRACE_PORT nor PORTER_TRACE_REMOTE defined in the environment\n");
                }
            }
            break;
        }
    case 0x6d:              // alt-m trace filter.
        {
#ifndef DEBUG_MEMORY
            fprintf(stdout, "Module not compiled for Memory Dumps\n");
            _CrtDumpMemoryLeaks();
#else
            fprintf(stderr, "Memory Dump ------- \n");
            fprintf(stderr, "  Start Memory Deltas ------- \n");
            _dump_allocated_delta(10);
            fprintf(stderr, "  Stop  Memory Deltas ------- \n\n");
            fprintf(stderr, "  Start Allocated Memory  ------- \n");
            _dump_allocated(10);
            fprintf(stderr, "  Stop  Allocated Memory  ------- \n\n");
            fprintf(stderr, "  Start Heap Check  ------- \n");
            _heap_check();
            fprintf(stderr, "  Stop  Heap Check  ------- \n");
            fprintf(stderr, "Memory Dump ------- \n");
#endif

            break;
        }

    case 0x6c:              // alt-l
        {
            PrintLogEvent = !PrintLogEvent;
            if(PrintLogEvent)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Communications events will be logged" << endl;
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Communications events will not be logged" << endl;
            }

            break;

        }
    case 0x63:              // alt-c
        {
            /* Issue a cold start to each CCU */
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Issuing Cold Starts to All CCU-711's" << endl;
            }
            PortManager.apply( applyColdStart, NULL);

            break;
        }
    case 0x6b:              // alt-k
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << endl << RWTime() << " Kicking PIL." << endl << endl;
            }

            KickPIL();

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << endl << RWTime() << " PIL has been kicked." << endl << endl;
            }
            break;
        }
    case 0x73:              // alt-s
        {
            /* Force a time sync */
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Forcing Sytem Wide Time Sync" << endl;
            }

            //CTIPostEventSem (TimeSyncSem);
            SetEvent(hPorterEvents[P_TIMESYNC_EVENT]);

            break;
        }
    case 0x70:              // alt-p
        {
            /* DUMP the queues */
            PortManager.apply( applyPortQueuePurge, NULL );
            break;
        }
    case 0x71:              // alt-q
        {
            PortManager.apply( applyPortQueueReport, (void*)1 );
            DeviceManager.apply( applyDeviceQueueReport, NULL );

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << endl << RWTime() << " There are " << OutMessageCount() << " OutMessages held by Port Control." << endl << endl;
            }
            break;
        }
    case 0x79:              // alt-y
        {
            PortManager.apply( applyPortLoadReport, (void*)1 );
            DeviceManager.apply( applyDeviceLoadReport, NULL );

            break;
        }
    default:
        process_fail = true;
        break;
    }

    return process_fail;
}




static int MyAllocHook(int nAllocType, void *pvData,
                       size_t nSize, int nBlockUse, long lRequest,
                       const unsigned char * szFileName, int nLine )
{
    static ULONG lastAlloc = 0;
    static ULONG prevLastAlloc = 0;
    static ULONG pprevLastAlloc = 0;

    int twnetyfourcnt = 0;

    if(lRequest > 1000000 )
    {
        if( (nSize == 24) )
        {
            if(lastAlloc == (lRequest - 1))
            {
                twnetyfourcnt++;

                if(prevLastAlloc == (lRequest - 2))
                {
                    twnetyfourcnt++;

                    if(pprevLastAlloc == (lRequest - 3))
                    {
                        twnetyfourcnt++;
                    }
                    pprevLastAlloc = prevLastAlloc;
                }
                prevLastAlloc = lastAlloc;
            }

            twnetyfourcnt++;

            lastAlloc = lRequest;
        }
        if((nSize == 52) )
        {
            twnetyfourcnt++;
        }
        else if((nSize == 1316) )
        {
            twnetyfourcnt++;
        }
        else if( (nSize == 68) )
        {
            twnetyfourcnt++;
        }
        else if( (nSize == 63) )
        {
            twnetyfourcnt++;
        }
        else if( (nSize == 416) )
        {
            twnetyfourcnt++;
        }
        else if( (nSize == 40) )
        {
            twnetyfourcnt++;
        }
        else if( (nSize == 164) )  // RWDBDatabase::RWDBDatabase() constructor.
        {
            twnetyfourcnt++;
        }
    }

#ifdef IGNORE_CRT_ALLOC
    if(_BLOCK_TYPE(nBlockUse) == _CRT_BLOCK)  // Ignore internal C runtime library allocations
        return TRUE;
#endif
    extern int _crtDbgFlag;

    if( ((_CRTDBG_ALLOC_MEM_DF & _crtDbgFlag) == 0) && ( (nAllocType == _HOOK_ALLOC) || (nAllocType == _HOOK_REALLOC) ) )
    {
        // Someone has disabled that the runtime should log this allocation
        // so we do not log this allocation
        if(pfnOldCrtAllocHook != NULL)
            pfnOldCrtAllocHook(nAllocType, pvData, nSize, nBlockUse, lRequest, szFileName, nLine);
        return TRUE;
    }

    // call the previous alloc hook
    if(pfnOldCrtAllocHook != NULL)
        pfnOldCrtAllocHook(nAllocType, pvData, nSize, nBlockUse, lRequest, szFileName, nLine);
    return TRUE; // allow the memory operation to proceed
}
