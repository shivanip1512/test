/*-----------------------------------------------------------------------------*
*
* File:   porter
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/porter.cpp-arc  $
* REVISION     :  $Revision: 1.127 $
* DATE         :  $Date: 2008/09/18 15:30:45 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


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


#include <windows.h>


#include <iostream>
#include <fstream>


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
#include "thread_monitor.h"
#include "CtiLocalConnect.h"
#include "systemmsgthread.h"

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
#include "mgr_config.h"
#include "mgr_point.h"

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

#include "dev_ccu721.h"

#define DO_GATEWAYTHREAD               1
#define DO_PORTERINTERFACETHREAD       1
#define DO_DISPATCHTHREAD              1
#define DO_VCONFIGTHREAD               1
#define DO_PORTERGUICONNECTIONTHREAD   0    // CODE INCOMPLETE AS OF 11/13/02 CGP
#define DO_PORTERCONNECTIONTHREAD      1
#define DO_TIMESYNCTHREAD              1
#define DO_PERFUPDATETHREAD            1
#define DO_FILLERTHREAD                1
#define DO_PORTSHARING                 1
#define DO_VERIFICATIONTHREAD          1

#define DOUT_OUT TRUE

using namespace std;

ULONG TimeSyncRate = 3600L;

static _CRT_ALLOC_HOOK pfnOldCrtAllocHook = NULL;

static int MyAllocHook(int nAllocType, void *pvData,
                       size_t nSize, int nBlockUse, long lRequest,
                       const unsigned char * szFileName, int nLine );



CTI_PORTTHREAD_FUNC_PTR PortThreadFactory(int);
void DisplayTraceList( CtiPortSPtr Port, list< CtiMessage* > &traceList, bool consume);
void LoadPorterGlobals(void);
INT  RefreshPorterRTDB(void *ptr = NULL);
void DebugKeyEvent(KEY_EVENT_RECORD *ke);
string GetDeviceName( ULONG id );
void KickPIL();
bool processInputFunction(CHAR Char);

void reportOnWorkObjects();

extern void QueueThread (void *);
extern void KickerThread (void *);
extern void DispatchMsgHandlerThread(VOID *Arg);
extern HCTIQUEUE* QueueHandle(LONG pid);
void commFail(CtiDeviceSPtr &Device);
bool addCommResult(long deviceID, bool wasFailure, bool retryGtZero);

DLLIMPORT extern BOOL PorterQuit;

extern INT RunningInConsole;              // From portmain.cpp

// Some Global Manager types to allow us some RTDB stuff.
CtiPortManager     PortManager(PortThreadFactory);
CtiDeviceManager   DeviceManager(Application_Porter);
CtiPointManager    PorterPointManager;
CtiConfigManager   ConfigManager;
CtiRouteManager    RouteManager;
map< long, CtiPortShare * > PortShareManager;

//These form the connection between Pil and Porter
extern DLLIMPORT CtiLocalConnect<OUTMESS, INMESS> PilToPorter; //Pil handles this one
CtiLocalConnect<INMESS, OUTMESS> PorterToPil; //Porter handles this one
extern DLLIMPORT CtiFIFOQueue< CtiMessage > PorterSystemMessageQueue;

Cti::Porter::SystemMsgThread _sysMsgThread(&PorterSystemMessageQueue);

RWThreadFunction _connThread;
RWThreadFunction _dispThread;
RWThreadFunction _guiThread;
RWThreadFunction _gwThread;
RWThreadFunction _pilThread;
RWThreadFunction _tsyncThread;
RWThreadFunction _perfuThread;
RWThreadFunction _fillerThread;
RWThreadFunction _vconfThread;

RWThreadFunction _queueCCU711Thread;
RWThreadFunction _kickerCCU711Thread;

CtiPorterVerification PorterVerificationThread;

static void LoadCommFailPoints(LONG ptid = 0);
static LONG GetCommFailPointID(LONG devid);
typedef map< LONG, LONG > CtiCommFailPoints_t;              // pair< LONG deviceid, LONG pointid >
static CtiCommFailPoints_t commFailDeviceIDToPointIDMap;

static RWWinSockInfo  winsock;

ULONG WorkCountPointOffset = 0;

bool findTAPDevice(const long key, const CtiDeviceSPtr &devsptr, void *ptr)
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
        dout << CtiTime() << " WARNING: " << ptPort->getName() << " has a TAP device on it." << endl;
        dout << " This currently sets the port to 7E1 mode for ALL devices on the port" << endl;
    }
    ptPort->verifyPortIsRunnable(hPorterEvents[P_QUIT_EVENT]);
}

static void applyPortShares(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr)
{
    //  this is a one-time load, making this thread-safe (if the map is treated as a read-only collection).
    //  if/when we update this to allow real-time reloads of the port shares,
    //    we need to keep it thread-safe.

    if( (ptPort->getSharedPortType() == "acs") || (ptPort->getSharedPortType() == "ilex" ) )
    {
        CtiPortShare *tmpPortShare = CTIDBG_new CtiPortShareIP(ptPort, PORTSHARENEXUS + PortShareManager.size());
        if( tmpPortShare != NULL )
        {
            ((CtiPortShareIP *)tmpPortShare)->setIPPort(ptPort->getSharedSocketNumber());
            tmpPortShare->start();
            PortShareManager.insert(std::make_pair(ptPort->getPortID(), tmpPortShare));
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Error initializing shared port for " << ptPort->getName() << endl;
        }
    }
}

static void applyQueuedDevicePortMatchup(const long unusedid, CtiDeviceSPtr RemoteDevice, void *portPtr)
{
    if(RemoteDevice->getDeviceQueueHandler() != NULL && portPtr != NULL)
    {
        CtiPortManager *portMgrPtr = (CtiPortManager*)portPtr;
        CtiPortSPtr port = portMgrPtr->PortGetEqual(RemoteDevice->getPortID());
        if(port)
        {
            port->addDeviceQueuedWork(RemoteDevice->getID(), -1);
        }
    }
}

static void applyDeviceInitialization(const long unusedid, CtiDeviceSPtr RemoteDevice, void *unused)
{
    extern CtiPILServer PIL;

    list< CtiRequestMsg * > request_list;

    RemoteDevice->deviceInitialization(request_list);

    while( !request_list.empty() )
    {
        PIL.putQueue(request_list.front());

        request_list.pop_front();
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
    extern bool findAllQueueEntries(void *unused, PQUEUEENT d);
    extern void cleanupOrphanOutMessages(void *unusedptr, void* d);

    LONG PortID = (LONG)lprtid;
    ULONG QueEntCnt = 0L;

    if(PortID == RemoteDevice->getPortID())
    {
        bool commsuccess = false;
        if(RemoteDevice->adjustCommCounts( commsuccess, false ))
        {
            commFail(RemoteDevice);
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
                if(pInfo->getStatus(INLGRPQ))
                {
                    pInfo->clearStatus(INLGRPQ);
                }
            }
        }
    }
}

void applyPortQueuePurge(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr)
{
    extern bool findAllQueueEntries(void *unused, PQUEUEENT d);
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
            dout << CtiTime() << " Port: " << setw(2) << ptPort->getPortID() << " / " << ptPort->getName() << " PURGING " << QueEntCnt << " port queue entries" << endl;
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
            commFail(RemoteDevice);
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
            dout << CtiTime() << " Port: " << setw(2) << ptPort->getPortID() << " / " << ptPort->getName() << " comm failing all attached comm status points" << endl;
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
    ULONG QueWorkCnt = 0L;
    ULONG QueEntCnt = 0L;
    ULONG AQueEntCnt = 0L;

    if(lprtid == NULL || PortID == RemoteDevice->getPortID())
    {
        QueWorkCnt = RemoteDevice->queuedWorkCount();
        CtiTime ent(RemoteDevice->getExclusion().getEvaluateNextAt());

        switch( RemoteDevice->getType() )
        {
            case TYPE_CCU711:
            {
                CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)RemoteDevice->getTrxInfo();

                if(pInfo != NULL)
                {
                    QueryQueue (pInfo->QueueHandle, &QueEntCnt);
                    QueryQueue (pInfo->ActinQueueHandle, &AQueEntCnt);
                    int ccuStatus = (int)pInfo->getStatus();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << " " << RemoteDevice->getName() << ": the time is now " << CtiTime( ) << endl;
                        if(QueWorkCnt) dout << "       " << setw(8) << QueWorkCnt  << " queued work elements. Evaluate next at " << ent << "." << endl;
                        dout << "       Queue Entries:  Queue: " << setw(8) << QueEntCnt << " Actin:  " << setw(8) << AQueEntCnt << " Status Byte: " << hex << setw(4) << ccuStatus << dec << " FreeSlots: " << pInfo->FreeSlots << endl;
                        for(int i = 0; i < 32; i++)
                        {
                            if(pInfo->QueTable[i].InUse)
                            {
                                dout << "       CCU QueTable Slot " << setw(3)  << i << " is " <<
                                    ((pInfo->QueTable[i].InUse & INUSE) ? "    INUSE" : "NOT INUSE" ) << " and " <<
                                    ((pInfo->QueTable[i].InUse & INCCU) ? "    INCCU" : "NOT INCCU" ) << " TimeSent = " << CtiTime( pInfo->QueTable[i].TimeSent ) << " Sequence " << hex << setw(5) << pInfo->QueTable[i].QueueEntrySequence << dec << endl;
                            }
                        }
                    }
                }

                break;
            }

            case TYPE_CCU721:
            {
                using Cti::Device::CCU721;
                Cti::Device::CCU721SPtr ccu = boost::static_pointer_cast<CCU721>(RemoteDevice);

                //  don't lock dout while we do this - the CCU locks internally, and we want to avoid acquiring any muxes out of order
                string queue_report = ccu->queueReport();

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << " " << RemoteDevice->getName() << ": the time is now " << CtiTime( ) << endl;

                    dout << queue_report << endl;
                }

                break;
            }

            default:
            {
                if(QueWorkCnt > 0)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << " " << setw(8) << QueWorkCnt  << " queued commands. Evaluate next at " << ent << ". Transmitter: " << RemoteDevice->getName() << (ent < ent.now() ? ". *** PAST DUE *** " + CtiNumStr(ent.now().seconds() - ent.seconds()) : " seconds.") << endl;
                }
            }
        }
    }
}

void applyDeviceLoadReport(const long unusedid, CtiDeviceSPtr RemoteDevice, void *lprtid)
{
    string printStr;

    bool yep = false;
    int sub, proc, orph;
    LONG PortID = (LONG)lprtid;

    if(lprtid == NULL || PortID == RemoteDevice->getPortID())
    {
        printStr = CtiTime().asString() + string(" Device: ") + CtiNumStr(RemoteDevice->getID()).spad(2) + " / " + RemoteDevice->getName() + "\n";

        for(int i = 0; i < 288; i++)
        {
            RemoteDevice->getQueueMetrics(i, sub, proc, orph);
            if(sub > 0)
            {
                yep = true;
                printStr += CtiNumStr(i).spad(2) + string(", ") + CtiNumStr(sub).spad(5) + ", " + CtiNumStr(proc).spad(5) + ", " + CtiNumStr(orph).spad(5) + "\n";
            }
        }

        if(yep)
        {
            CtiLockGuard<CtiLogger> bguard(blog);
            blog << printStr << endl;
        }
    }
}

void applyPortQueueReport(const long unusedid, CtiPortSPtr ptPort, void *passedPtr)
{
    string printStr;

    /* Report on the state of the queues */

    if(!ptPort->isInhibited())
    {
        ULONG QueEntCnt = ptPort->queueCount();
        /* Print out the port queue information */

        CtiQueueAnalysis_t qa;
        ::memset(&qa, 0, sizeof(qa));

        ptPort->applyPortQueue(&qa, applyPortQueueOutMessageReport);

        #if 0
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);

            dout << CtiTime() << " Port " << ptPort->getName() << endl;
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
            printStr = CtiTime().asString() + " Port: " + CtiNumStr(ptPort->getPortID()).spad(2) + " / " + ptPort->getName() +
                                             " Port Queue Entries:  " + CtiNumStr(QueEntCnt).spad(4);

            if(ptPort->getConnectedDevice())
            {
                printStr += ". Connected To: " + GetDeviceName(ptPort->getConnectedDevice());
            }
        }
    }

    if(!printStr.empty())
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << printStr << endl;
    }
}

void applyPortWorkReport(const long unusedid, CtiPortSPtr ptPort, void *passedPtr)
{
    string printStr;
    ULONG QueEntCnt = 0;

    /* Report on the state of the queues */

    if( passedPtr && !ptPort->isInhibited())
    {
        QueEntCnt = ptPort->getWorkCount();

        *((ULONG *)passedPtr) += QueEntCnt;
    }
    else if( !ptPort->isInhibited() )
    {
        QueEntCnt = ptPort->getWorkCount();
        /* Print out the port queue information */

        printStr = CtiTime().asString() + " Port: " + CtiNumStr(ptPort->getPortID()).spad(2) + " / " + ptPort->getName() +
                                             " Port/Device Work Entries:  " + CtiNumStr(QueEntCnt).spad(4);
    }

    if(!printStr.empty())
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << printStr << endl;
    }
}


void applyPortLoadReport(const long unusedid, CtiPortSPtr ptPort, void *passedPtr)
{
    bool yep = false;
    string printStr;

    /* Report on the state of the queues */

    if(!ptPort->isInhibited())
    {
        int sub, proc, orph;

        printStr = CtiTime().asString() + " Port: " + CtiNumStr(ptPort->getPortID()).spad(2) + " / " + ptPort->getName() + "\n";

        for(int i = 0; i < 288; i++)
        {
            ptPort->getQueueMetrics(i, sub, proc, orph);
            if(sub > 0)
            {
                yep = true;
                printStr += CtiNumStr(i).spad(2) + string(", ") + CtiNumStr(sub).spad(5) + ", " + CtiNumStr(proc).spad(5) + ", " + CtiNumStr(orph).spad(5) + "\n";
            }
        }
    }

    if(yep)
    {
        CtiLockGuard<CtiLogger> bguard(blog);
        blog << printStr << endl;
    }
}

bool findCCU711(const long key, const CtiDeviceSPtr &devsptr, void *ptr)
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
    int WorkReportFrequencyInSeconds;
    extern USHORT PrintLogEvent;
    time_t last_print = 0, last_flush = 0;
    CtiTime lastWorkReportTime;

    BYTE RefKey[8] = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
    BYTE VerKey[8] = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

    /* New stuff for YUKON kbd handling */
    INPUT_RECORD      inRecord;
    HANDLE            hStdIn = GetStdHandle(STD_INPUT_HANDLE);
    DWORD             Count;
    CHAR              Char;

    /* Print out the program name and revison */
    identifyProject(CompileInfo);
    setConsoleTitle(CompileInfo);

    SetThreadName(-1, "PortrMain");

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
            if(!(::strcmp (argv[i], "/?")))
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
    CTISetPriority(PRTYC_TIMECRITICAL, THREAD_PRIORITY_NORMAL);

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
    if(gConfigParms.isOpt("DEBUG_MEMORY") && !stringCompareIgnoreCase(gConfigParms.getValueAsString("DEBUG_MEMORY"),"true") )
        ENABLE_CRT_SHUTDOWN_CHECK;

    //Match up the connections
    PilToPorter.setMatchingConnection(PorterToPil);
    PorterToPil.setMatchingConnection(PilToPorter);

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

    if(DO_GATEWAYTHREAD && !stringCompareIgnoreCase(gConfigParms.getValueAsString("PORTER_GATEWAY_SUPPORT"),"true"))
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

    /* Start the verification thread */
    if(!stringCompareIgnoreCase(gConfigParms.getValueAsString("PORTER_START_VERIFICATIONTHREAD", "TRUE"),"true"))
    {
        PorterVerificationThread.start();
    }

    ThreadMonitor.start();//Start the thread monitor thread!

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
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    /* all of the port threads are started so now start first socket connection handler */
    if(DO_PORTERCONNECTIONTHREAD)
    {
        _connThread = rwMakeThreadFunction( PorterConnectionThread, (void*)NULL );
        _connThread.start();
    }


    /* Check if we need to start the filler thread */
    if(gConfigParms.isOpt("PORTER_START_FILLERTHREAD") && !(stricmp ("TRUE", gConfigParms.getValueAsString("PORTER_START_FILLERTHREAD").c_str())))
    {
        if(DO_FILLERTHREAD)  /* go ahead and start the thread passing the variable */
        {
            _fillerThread = rwMakeThreadFunction( FillerThread, (void*)NULL );
            _fillerThread.start();
        }
    }

    /* Check if we need to start the versacom config thread */
    if( !((gConfigParms.getValueAsString("PORTER_START_VCONFIGTHREAD")).empty()) && !(stricmp ("TRUE", gConfigParms.getValueAsString("PORTER_START_VCONFIGTHREAD").c_str())))
    {
        if(DO_VCONFIGTHREAD) /* go ahead and start the thread passing the variable */
        {
            _vconfThread = rwMakeThreadFunction( VConfigThread, (void*)NULL );
            _vconfThread.start();
        }
    }

    /* Check if we need to start the versacom config thread */
   // if( !((gConfigParms.getValueAsString("PORTER_START_SYSMSGTHREAD")).empty()) && !(stricmp ("TRUE", gConfigParms.getValueAsString("PORTER_START_SYSMSGTHREAD").c_str())))
    {
        _sysMsgThread.setDeviceManager(&DeviceManager);
        _sysMsgThread.setPortManager(&PortManager);
        _sysMsgThread.setPilToPorter(&PilToPorter);
        _sysMsgThread.start();
    }

    if( (WorkReportFrequencyInSeconds = gConfigParms.getValueAsULong("PORTER_WORK_COUNT_TIME", 60)) <= 0 )
    {
        //This is a failure case
        WorkReportFrequencyInSeconds = 60;
    }

    if(gLogPorts)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Trace is now FORCED on by configuration entry YUKON_LOG_PORTS" << endl;
        }
        TraceFlag = TRUE;
    }
    else if(TraceFlag)
    {
        if(TraceErrorsOnly)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Trace is now on for errors only" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Trace is now on for all messages" << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Trace is now off for all messages" << endl;
    }

    CtiDeviceManager::ptr_type system;

    if( system = DeviceManager.getEqual(0) )
    {
        if( system->hasDynamicInfo(CtiTableDynamicPaoInfo::Key_VerificationSequence) )
        {
            VerificationSequenceGen(true, system->getDynamicInfo(CtiTableDynamicPaoInfo::Key_VerificationSequence));
        }
    }

    //  Some devices need to fire off commands on startup - specifically, MCTs may need to resume LLP collection
    DeviceManager.apply(applyDeviceInitialization, NULL);

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
                            Char = ::tolower(inRecord.Event.KeyEvent.uChar.AsciiChar);
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

        /*if( last_print + 3600 <= ::time(0) )
        {
            last_print = ::time(0);
            processInputFunction(0x79);  //  do an alt-y every 60 seconds
        }*/

        if( last_flush + 60 <= ::time(0) )
        {
            last_flush = ::time(0);
            DeviceManager.writeDynamicPaoInfo();
        }

        if( lastWorkReportTime.seconds() < (lastWorkReportTime.now().seconds()) )
        {
            lastWorkReportTime = nextScheduledTimeAlignedOnRate(lastWorkReportTime.now(), WorkReportFrequencyInSeconds);
            reportOnWorkObjects();
        }

        CTISleep(250);
    }

    if( system = DeviceManager.getEqual(0) )
    {
        system->setDynamicInfo(CtiTableDynamicPaoInfo::Key_VerificationSequence, VerificationSequenceGen());
    }

    DeviceManager.writeDynamicPaoInfo();

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

    //  delete/stop the shared ports
    map<long, CtiPortShare *>::iterator itr;
    for( itr = PortShareManager.begin(); itr != PortShareManager.end(); itr++ )
    {
        delete itr->second;
    }
    PortShareManager.erase(PortShareManager.begin(), PortShareManager.end());

    ThreadMonitor.interrupt(CtiThread::SHUTDOWN);

    if(_gwThread.isValid())                 _gwThread.requestCancellation(200);
    if(_pilThread.isValid())                _pilThread.requestCancellation(200);
    if(_dispThread.isValid())               _dispThread.requestCancellation(200);
    if(_connThread.isValid())               _connThread.requestCancellation(200);
    if(_guiThread.isValid())                _guiThread.requestCancellation(200);
    if(_tsyncThread.isValid())              _tsyncThread.requestCancellation(200);
    if(_perfuThread.isValid())              _perfuThread.requestCancellation(200);
    if(_fillerThread.isValid())             _fillerThread.requestCancellation(200);
    if(_vconfThread.isValid())              _vconfThread.requestCancellation(200);
    if(_queueCCU711Thread.isValid())        _queueCCU711Thread.requestCancellation(200);
    if(_kickerCCU711Thread.isValid())       _kickerCCU711Thread.requestCancellation(200);

    if(PorterVerificationThread.isRunning())    PorterVerificationThread.interrupt(CtiPorterVerification::SHUTDOWN);

    ThreadMonitor.join();

    if(_connThread.isValid())
    {
        if(_connThread.join(2000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _connThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _connThread shutdown" << endl;
        }
    }

    if(_guiThread.isValid())
    {
        if(_guiThread.join(2000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _guiThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _guiThread shutdown" << endl;
        }
    }

    if(_perfuThread.isValid())
    {
        if(_perfuThread.join(2000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _perfuThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _perfuThread shutdown" << endl;
        }
    }

    if(_tsyncThread.isValid())
    {
        if(_tsyncThread.join(2000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _tsyncThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _tsyncThread shutdown" << endl;
        }
    }

    if(_fillerThread.isValid())
    {
        if(_fillerThread.join(2000) != RW_THR_COMPLETED)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _fillerThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _fillerThread shutdown" << endl;
        }
    }

    if(_vconfThread.isValid())
    {
        if(_vconfThread.join(2000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _vconfThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _vconfThread shutdown" << endl;
        }
    }


    if(_dispThread.isValid())
    {
        if(_dispThread.join(15000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _dispThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _dispThread shutdown" << endl;
        }
    }

    if(_gwThread.isValid())
    {
        if(_gwThread.join(15000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _gwThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _gwThread shutdown" << endl;
        }
    }

    if(_pilThread.isValid())
    {
        if(_pilThread.join(15000) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _pilThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _pilThread shutdown" << endl;
        }
    }

    if(_queueCCU711Thread.isValid())
    {
        if(_queueCCU711Thread.join(1500) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _queueCCU711Thread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _queueCCU711Thread shutdown" << endl;
        }
    }

    if(_kickerCCU711Thread.isValid())
    {
        if(_kickerCCU711Thread.join(1500) != RW_THR_COMPLETED )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _kickerCCU711Thread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _kickerCCU711Thread shutdown" << endl;
        }
    }

    if(!stringCompareIgnoreCase(gConfigParms.getValueAsString("PORTER_START_VERIFICATIONTHREAD", "TRUE"),"true"))
    {
        PorterVerificationThread.join();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _verificationThread shutdown" << endl;
        }
    }


    PortManager.haltLogs();

    Sleep(3000);

    // Make sure all the logs get output and done!
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " logger shutdown" << endl;
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
            dout << CtiTime() << " " << autoRoleDevice->getName() << " role table being refreshed" << endl;
        }
    }
}

INT RefreshPorterRTDB(void *ptr)
{
    extern CtiPILServer  PIL;
    bool autoRole     = false;              // Set to true if routes might have changed and or we need to download based on time!
    static CtiTime lastAutoRole(PASTDATE);    // This time is used to trigger timed downloads of repeater roles.

    bool autoCCURoute = false;

    INT   i;
    INT   status = NORMAL;
    DWORD dwWait;
    LONG  id = 0;

    CtiDBChangeMsg *pChg = (CtiDBChangeMsg *)ptr;

    // Reload the globals used by the porter app too.
    InitYukonBaseGlobals();
    LoadPorterGlobals();

    if(!PorterQuit && (pChg == NULL || pChg->getDatabase() == ChangePointDb))
    {
        if(pChg == NULL)
        {
            LoadCommFailPoints();
        }
        else
        {
            LoadCommFailPoints(pChg->getId());
        }
    }

    if( !PorterQuit && (pChg == NULL || (pChg->getDatabase() == ChangeStateGroupDb)) )
    {
        ReloadStateNames();
    }

    if(!PorterQuit && (pChg == NULL || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_PORT)) )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Reloading all ports based upon db change" << endl;
        }

        CtiPortManager::coll_type::writer_lock_guard_t guard(PortManager.getLock());
        PortManager.RefreshList();
    }

    if(!PorterQuit && (pChg == NULL || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_DEVICE) ) )
    {
        CtiDeviceManager::coll_type::writer_lock_guard_t guard(DeviceManager.getLock());

        LONG chgid = 0;
        string catstr;
        string devstr;

        if(pChg)
        {
            chgid = pChg->getId();
            catstr = pChg->getCategory();
            devstr = pChg->getObjectType();
        }

        DeviceManager.refresh(DeviceFactory, isNotADevice, NULL, chgid, catstr, devstr);

        if(pChg == NULL)
        {
            attachRouteManagerToDevices(&DeviceManager, &RouteManager);
            attachPointManagerToDevices(&DeviceManager, &PorterPointManager);
            ConfigManager.initialize(DeviceManager);
            DeviceManager.refreshGroupHierarchy();
            DeviceManager.apply(applyQueuedDevicePortMatchup, &PortManager);
        }
        else
        {
            CtiDeviceSPtr pDev = DeviceManager.getEqual( chgid );
            if( pDev )
            {
                pDev->setRouteManager(&RouteManager);
                pDev->setPointManager(&PorterPointManager);

                if( pDev->isGroup() ) //Doing this call here saves us a tiny bit of time! yay!
                {
                    CtiDeviceManager::coll_type::writer_lock_guard_t guard(DeviceManager.getLock());
                    DeviceManager.refreshGroupHierarchy(chgid);
                }

                if(pDev->getDeviceQueueHandler() != NULL)
                {
                    CtiPortSPtr port = PortManager.PortGetEqual(pDev->getPortID());
                    if(port)
                    {
                        port->addDeviceQueuedWork(pDev->getID(), -1);
                    }
                }

                if( pDev->getType() == TYPE_TAPTERM )
                {
                    try
                    {
                        PortManager.apply( applyNewLoad, NULL );
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }
        }
    }

    if(!PorterQuit)
    {
        if(pChg == NULL || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_ROUTE) )
        {
            if(pChg != NULL)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Reloading all routes based upon db change" << endl;
                    dout << CtiTime() << " All repeaters will have their role table refreshed." << endl;
                }
                autoRole = true;
            }

            if(gConfigParms.getValueAsULong("PORTER_AUTOROLE_RATE", 0) > 0 && CtiTime().seconds() > lastAutoRole.seconds() + gConfigParms.getValueAsULong("PORTER_AUTOROLE_RATE") )
            {
                autoRole = true;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " AutoRole Download timer (master.cfg: PORTER_AUTOROLE_RATE) expired." << endl;
                    dout << CtiTime() << " All repeaters will have their role table refreshed." << endl;
                }
            }

            RouteManager.RefreshList();

            /* Make routes associate with devices */
            attachTransmitterDeviceToRoutes(&DeviceManager, &RouteManager);
        }

        if(autoRole)
        {
            try
            {
                lastAutoRole = lastAutoRole.now();      // Update our static variable.

                DeviceManager.apply(applyRepeaterAutoRole,NULL);

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Downloading routes to all CCU-711s on repeater route change" << endl;
                }

                PortManager.apply( applyLoadAllRoutes, NULL );
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }

        if(pChg != NULL && (pChg->getDatabase() == ChangeConfigDb))
        {
            ConfigManager.processDBUpdate(pChg->getId(), pChg->getCategory(), pChg->getObjectType(), pChg->getTypeOfChange());
        }


        if(pChg != NULL && (pChg->getDatabase() == ChangePointDb))
        {
            if(pChg->getTypeOfChange() == ChangeTypeDelete)
            {
                PorterPointManager.orphan(pChg->getId());

                if( getDebugLevel() & DEBUGLEVEL_MGR_POINT )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Deleting pointid (" << pChg->getId() << ")" << endl;
                }
            }
            else
            {
                PorterPointManager.refreshList(isPoint, NULL, pChg->getId(), 0, resolvePointType(pChg->getObjectType()));

                if( getDebugLevel() & DEBUGLEVEL_MGR_POINT )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Refreshing pointid (" << pChg->getId() << ")" << endl;
                }

                // We also need to reload all the point groups to make certain the control strings get updated.
                if(pChg != NULL && pChg->getId() != 0 )
                {
                    if(pChg->getObjectType() == "Status") // only status points can be in point groups.
                    {
                        DeviceManager.refreshPointGroups();
                    }
                }
                else
                {
                    DeviceManager.refreshPointGroups();
                }
            }
        }

        if(pChg == NULL)
        {
            try
            {
                PortManager.apply( applyNewLoad, NULL );
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    else
    {
        return -1;
    }

    /* see if we need to start process's for queuing */
    {
        if(!(_queueCCU711Thread.isValid()) && DeviceManager.contains(findCCU711, NULL))
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
    string Temp;
    /* Definitions to get environment variables */
    CHAR Info[50] = {'\0'};
    PCHAR PInfo;


    // Causes all normal error and dout traffic to be routed to the porter.logxx files
    cParmPorterServiceLog = FALSE;
    if(!(Temp = gConfigParms.getValueAsString("PORTER_SERVICE_LOG")).empty())
    {
        if(!stricmp("TRUE", Temp.c_str()))
        {
            cParmPorterServiceLog = TRUE;
        }
    }

    if(!(Temp = gConfigParms.getValueAsString("PORTER_RELOAD_RATE")).empty())
    {
        PorterRefreshRate = atoi(Temp.c_str());
        if(PorterRefreshRate < 86400)
        {
            PorterRefreshRate = 86400;
        }
    }

    if( !(Temp = gConfigParms.getValueAsString("PIL_QUEUE_SIZE")).empty())
    {
        PILMaxQueueSize = atoi(Temp.c_str());
    }
    else
    {
        PILMaxQueueSize = 1000;
    }

    /* Check the extra delay flag */
    if( !(Temp = gConfigParms.getValueAsString("PORTER_EXTRATIMEOUT")).empty())
    {
        ExtraTimeOut = atoi(Temp.c_str());
    }
    else
    {
        ExtraTimeOut = 0;
    }

    /* Check the number of queue octets allowed */
    if(!(Temp = gConfigParms.getValueAsString("PORTER_MAXOCTS")).empty())
    {
        MaxOcts = atoi(Temp.c_str());
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

    /* Resolve the stuff that we need for DIO24 Output */
    if(!(Temp = gConfigParms.getValueAsString("DIO24")).empty())
    {
        strncpy (Info, Temp.c_str(), 49);
        /* Start the parcing process */
        if(sscanf (strtok (Info, ",;"), "%x", &DIO24Base) != 1)
        {
            DIO24Base = DAS08BASE;
            Info[0] = '\0';
        }
    }
    else if(!(Temp = gConfigParms.getValueAsString("DAS08")).empty())
    {
        strncpy (Info, Temp.c_str(), 49);
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
    if(!(Temp = gConfigParms.getValueAsString("PORTER_LANDGLCUS")).empty() && (!(stricmp ("YES", Temp.c_str()))))
    {
        LAndGLCUs = TRUE;
    }

    /*  set the frequency info -- Defaults to 12.5 */
    if(!(Temp = gConfigParms.getValueAsString("PORTER_FREQ")).empty())
    {
        /* Decode the system frequency */
        if(!(stricmp (Temp.c_str(), "7.3")))
        {
            DLCFreq1 = 125;
            DLCFreq2 = 51;
        }
        else if(!(stricmp (Temp.c_str(), "7.8")))
        {
            DLCFreq1 = 125;
            DLCFreq2 = 48;
        }
        else if(!(stricmp (Temp.c_str(), "8.3")))
        {
            DLCFreq1 = 49;
            DLCFreq2 = 12;
        }
        else if(!(stricmp (Temp.c_str(), "8.8")))
        {
            DLCFreq1 = 124;
            DLCFreq2 = 55;
        }
        else if(!(stricmp (Temp.c_str(), "9.6")))
        {
            DLCFreq1 = 17;
            DLCFreq2 = 7;
        }
        else if(!(stricmp (Temp.c_str(), "10.4")))
        {
            DLCFreq1 = 121;
            DLCFreq2 = 46;
        }
        else if(!(stricmp (Temp.c_str(), "12.5")))
        {
            DLCFreq1 = 94;
            DLCFreq2 = 37;
        }
        else if(!(stricmp (Temp.c_str(), "13.8")))
        {
            DLCFreq1 = 123;
            DLCFreq2 = 35;
        }
    }


    if(!(Temp = gConfigParms.getValueAsString("PORTER_DEBUGLEVEL")).empty())
    {
        char *eptr;
        PorterDebugLevel = strtoul(Temp.c_str(), &eptr, 16);
    }


    if(!(Temp = gConfigParms.getValueAsString("PORTER_TSYNC_RATE")).empty())
    {
        if(!(TimeSyncRate = atol (Temp.c_str())))
        {
            /* Unable to convert so assume "NOT EVER" */
            TimeSyncRate = 0L;
        }
    }


    if(!(Temp = gConfigParms.getValueAsString("PORTER_IGNORE_TCU5000_QUEUEBUSY")).empty())
    {
        std::transform(Temp.begin(), Temp.end(), Temp.begin(), ::tolower);

        if( Temp == "true" || Temp == "yes")
        {
            gIgnoreTCU5X00QueFull = true;
        }
    }

    if(!(Temp = gConfigParms.getValueAsString("PORTER_IGNORE_TCU_QUEUEBUSY")).empty())
    {
        std::transform(Temp.begin(), Temp.end(), Temp.begin(), ::tolower);

        if( Temp == "true" || Temp == "yes")
        {
            gIgnoreTCU5X00QueFull = true;
        }
    }


    if(!(Temp = gConfigParms.getValueAsString("PORTER_IDLC_ECHO_SUPPRESSION")).empty())
    {
        std::transform(Temp.begin(), Temp.end(), Temp.begin(), ::tolower);

        if( Temp == "true" || Temp == "yes")
        {
            gIDLCEchoSuppression = true;
        }
    }

    if(!(Temp = gConfigParms.getValueAsString("PORTER_CCU_DELAY_FILE")).empty())
    {
        gDelayDatFile = Temp;

    }

    if(!(Temp = gConfigParms.getValueAsString("PORTER_PORTINIT_QPURGE_DELAY")).empty())
    {
        if(!(PorterPortInitQueuePurgeDelay = ::abs(atoi(Temp.c_str()))))
        {
            /* Unable to convert so assume default of 5 minutes (20 * 15s) */
            PorterPortInitQueuePurgeDelay = 20;
        }
    }




    if(getDebugLevel() & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Using CCU Delay information from " << gDelayDatFile << endl;
    }


    if(DebugLevel & 0x0001)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << "Loading porter globals: " << endl;
        dout << "PORTER_RELOAD_RATE      " << PorterRefreshRate << endl;
        dout << "PIL_QUEUE_SIZE          " << PILMaxQueueSize << endl;
        dout << "PORTER_EXTRATIMEOUT     " << ExtraTimeOut << endl;
        dout << "PORTER_MAXOCTS          " << MaxOcts << endl;
        dout << "PORTER_MCT400SERIESSPID " << (int)gMCT400SeriesSPID << endl;
    }
}




void DisplayTraceList( CtiPortSPtr Port, list< CtiMessage* > &traceList, bool consume)
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
            std::list< CtiMessage* >::iterator itr = traceList.begin();
            while ( itr != traceList.end() )
            {
                CtiTraceMsg *pTrace = (CtiTraceMsg*)*itr;
                SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), (WORD)pTrace->getAttributes());
                cout << pTrace->getTrace();

                if(pTrace->isEnd())
                {
                    cout << endl;
                }
                ++itr;
            }

            SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_BLUE | FOREGROUND_GREEN | FOREGROUND_RED);
        }

        if(consume)
        {
            delete_container( traceList );
            traceList.clear();
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

string GetDeviceName( ULONG id )
{
    string name;

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
            dout << CtiTime() << " _pilThread did not shutdown" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " _pilThread shutdown" << endl;
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
            dout << "Alt - Z     Reload all CPARMs" << endl;
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
                    dout << CtiTime() << " Trace is Now On for Errors Only" << endl;
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Trace is Now On for All Messages" << endl;
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Trace is Now Off for All Messages" << endl;
                }
            }

            break;
        }
    case 0x7a:              // alt-z
        {
            gConfigParms.RefreshConfigParameters();
            LoadPorterGlobals();

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " CPARMs have been reloaded" << endl;
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
                dout << CtiTime() << " Trace is Now On for Errors Only" << endl;
            }
            else if(TraceFlag)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Trace is Now On for All Messages" << endl;
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Trace is Now Off for All Messages" << endl;
                }
            }

            break;

        }
    case 0x72:              // alt-r
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Downloading Routes to All CCU-711's" << endl;
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
                dout << CtiTime() << " Commands will be sent double" << endl;
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Commands will not be sent double" << endl;
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
                dout << CtiTime() << " Communications events will be logged" << endl;
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Communications events will not be logged" << endl;
            }

            break;

        }
    case 0x63:              // alt-c
        {
            /* Issue a cold start to each CCU */
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Issuing Cold Starts to All CCU-711's" << endl;
            }
            PortManager.apply( applyColdStart, NULL);

            break;
        }
    case 0x6b:              // alt-k
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << endl << CtiTime() << " Kicking PIL." << endl << endl;
            }

            KickPIL();

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << endl << CtiTime() << " PIL has been kicked." << endl << endl;
            }
            break;
        }
    case 0x73:              // alt-s
        {
            /* Force a time sync */
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Forcing Sytem Wide Time Sync" << endl;
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
                dout << endl << CtiTime() << " There are " << OutMessageCount() << " OutMessages held by Port Control." << endl << endl;
            }
            break;
        }
    case 0x77:              // alt-w
        {
            reportOnWorkObjects();
            // PortManager.apply( applyPortWorkReport, NULL );
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

    int alloc_cnt = 0;
    int dealloc_cnt = 0;

    if( (nAllocType == _HOOK_ALLOC) || (nAllocType == _HOOK_REALLOC) )
    {
        if(lRequest > 1000000 )
        {
            if( (nSize == 24) )
            {
                if(lastAlloc == (lRequest - 1))
                {
                    alloc_cnt++;

                    if(prevLastAlloc == (lRequest - 2))
                    {
                        alloc_cnt++;

                        if(pprevLastAlloc == (lRequest - 3))
                        {
                            alloc_cnt++;
                        }
                        pprevLastAlloc = prevLastAlloc;
                    }
                    prevLastAlloc = lastAlloc;
                }

                alloc_cnt++;

                lastAlloc = lRequest;
            }
            if((nSize == 52) )
            {
                alloc_cnt++;
            }
            else if((nSize == 1316) )
            {
                alloc_cnt++;
            }
            else if( (nSize == 68) )
            {
                alloc_cnt++;
            }
            else if( (nSize == 63) )
            {
                alloc_cnt++;
            }
            else if( (nSize == 416) )
            {
                alloc_cnt++;
            }
            else if( (nSize == 40) )
            {
                alloc_cnt++;
            }
            else if( (nSize == 164) )  // RWDBDatabase::RWDBDatabase() constructor.
            {
                alloc_cnt++;
            }
        }
    }
    else if(nAllocType == _HOOK_FREE)
    {
        // Hmm, freeing memory here!
        // pvData is being freed
        if(pvData != 0)
        {
            dealloc_cnt++;
        }
    }

#ifdef IGNORE_CRT_ALLOC
    if(_BLOCK_TYPE(nBlockUse) == _CRT_BLOCK)  // Ignore internal C runtime library allocations
        return TRUE;
#endif
    extern int _crtDbgFlag;

    if( ((_CRTDBG_ALLOC_MEM_DF & _crtDbgFlag) == 0) && ( (nAllocType == _HOOK_ALLOC) || (nAllocType == _HOOK_REALLOC) ) )
    {
        // Someone has pdisabled that the runtime should log this allocation
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

bool addCommResult(long deviceID, bool wasFailure, bool retryGtZero)
{
    bool retVal = false;
    bool isCommFail = !wasFailure; //This is inverted for some reason

    CtiDeviceSPtr device = DeviceManager.getEqual(deviceID);

    if( device )
    {
        if( device->adjustCommCounts(isCommFail, retryGtZero) )
        {
            commFail(device);
        }
        retVal = true;
    }

    return retVal;
}

void commFail(CtiDeviceSPtr &Device)
{
    extern CtiConnection VanGoghConnection;

    CtiPointSPtr  pPoint;
    char temp[80];
    LONG pointid;
    bool state = Device->isCommFailed();        // Ask the device if it has accumulated enough errors to be failed!

    //if( NULL != (pPoint = Device->getDevicePointOffsetTypeEqual(COMM_FAIL_OFFSET, StatusPointType)) )
    if( 0 != (pointid = GetCommFailPointID(Device->getID())) )
    {
        sprintf(temp, "Communication status %s", state ? "FAILED" : "GOOD");
        CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(pointid, (double)(state ? CLOSED : OPENED), NormalQuality, StatusPointType, temp, TAG_POINT_MAY_BE_EXEMPTED);

        if(pData != NULL)
        {
            VanGoghConnection.WriteConnQue(pData);
        }
    }
    else if(PorterDebugLevel & PORTER_DEBUG_VERBOSE && Device && state)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << Device->getName() << " would be COMM FAILED if it had offset " << COMM_FAIL_OFFSET << " defined" << endl;
    }
}

void LoadCommFailPoints(LONG ptid)
{
    commFailDeviceIDToPointIDMap.clear();       // No more map.

    LONG did, pid;
    string sql = string("select paobjectid, pointid from point where pointoffset = ") + CtiNumStr(COMM_FAIL_OFFSET) + string(" and pointtype = 'Status'");
    if(ptid) sql += string(" and pointid = " + CtiNumStr(ptid));

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBReader  rdr = ExecuteQuery( conn, sql.c_str() );

    while(rdr() && rdr.isValid())
    {
        rdr["paobjectid"] >> did;
        rdr["pointid"] >> pid;


        std::pair< CtiCommFailPoints_t::iterator, bool > resultpair = commFailDeviceIDToPointIDMap.insert( std::make_pair(did, pid) );

        if(resultpair.second == false)           // Insert was unsuccessful (should never be this way!).
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** LoadCommFailPoints Error **** " << __FILE__ << " (" << __LINE__ << ") Pao " << did << " has multiple pids at offset 2000? " << endl;
            }
        }
    }

    return;
}

LONG GetCommFailPointID(LONG devid)
{
    LONG pid = 0;

    CtiCommFailPoints_t::iterator itr = commFailDeviceIDToPointIDMap.find( devid );

    if( itr != commFailDeviceIDToPointIDMap.end() )
    {
        pid = (*itr).second;
    }

    return pid;
}

void reportOnWorkObjects()
{
    CtiPointDataMsg *pData = NULL;
    extern CtiConnection VanGoghConnection;
    ULONG count = 0;
    PortManager.apply( applyPortWorkReport, &count );

    if( !WorkCountPointOffset )
    {
        WorkCountPointOffset = GetPIDFromDeviceAndOffset(0, 1500);
    }

    if( WorkCountPointOffset )
    {
        pData = CTIDBG_new CtiPointDataMsg(WorkCountPointOffset, count, NormalQuality, AnalogPointType);
    }

    if(pData != NULL)
    {
        VanGoghConnection.WriteConnQue(pData);
    }

}
