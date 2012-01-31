#include "precompiled.h"

// These next few are required for Win32
#include <process.h>
#include <vector>

#include <rw/toolpro/winsock.h>

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <float.h>
#include <time.h>

#include "os2_2w32.h"
#include "cticalls.h"

#include "dbaccess.h"
#include "dsm2.h"
#include "elogger.h"
#include "dsm2err.h"
#include "queues.h"
#include "porter.h"
#include "scanner.h"
#include "master.h"
#include "dllbase.h"
#include "dlldev.h"
// Here are the devices for which scannables have been defined

#include "scanglob.h"

#include "rtdb.h"
#include "mgr_device_scannable.h"
#include "mgr_point.h"
#include "mgr_config.h"
#include "dev_base.h"
#include "dev_single.h"
#include "dev_mct.h"  //  for DLC loadprofile scans
#include "dev_welco.h"

#include "logger.h"
#include "exchange.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_dbchg.h"
#include "msg_signal.h"

#include "c_port_interface.h"
#include "cparms.h"
#include "configparms.h"
#include "connection.h"

#include "utility.h"
#include "dllyukon.h"
#include "thread_monitor.h"
#include "ThreadStatusKeeper.h"
#include "database_connection.h"

#include "millisecond_timer.h"

#define NEXT_SCAN       0
#define REMOTE_SCAN     1
#define DLC_LP_SCAN     2
#define WINDOW_OPENS    3
#define MAX_SCAN_TYPE   4

using namespace std;
using Cti::ThreadStatusKeeper;

static INT      ScannerReloadRate = 86400;
static CtiTime  LastPorterOutTime;
static CtiTime  LastPorterInTime;

static CtiMutex inmessMux;
static list< INMESS* > inmessList;

const CtiTime   MAXTime(YUKONEOT);

void  LoadScannableDevices(void *ptr = NULL);
void  DispatchMsgHandlerThread(void *Arg);
void  DatabaseHandlerThread(void *Arg);

/* Local Declarations... Utility Functions */

CtiTime  TimeOfNextRemoteScan(void);
CtiTime  TimeOfNextLPScan(void);
static CtiTime  TimeOfNextWindow( void );

void    NexusThread(void *Arg);
INT     RecordDynamicData();
void    InitScannerGlobals(void);
void    DumpRevision(void);
INT     MakePorterRequests(list< OUTMESS* > &outList);

Cti::ScannableDeviceManager ScannerDeviceManager(Application_Scanner);
CtiConfigManager            ConfigManager;
CtiPointManager             ScannerPointManager;

static RWWinSockInfo  winsock;

extern BOOL ScannerQuit;

CtiConnection     VanGoghConnection;
ULONG             ScannerDebugLevel = 0;

HANDLE hLockArray[] = {
    hScannerSyncs[S_QUIT_EVENT],
    hScannerSyncs[S_LOCK_MUTEX]
};

void barkAboutCurrentTime(CtiDeviceSPtr Device, CtiTime &rt, INT line)
{
    if(ScannerDebugLevel & SCANNER_DEBUG_NEXTSCAN)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Next scan is for " << Device->getName() << " at " << rt;
        if(ScannerDebugLevel & 0x00000001)
        {
            dout << " on " << line;
        }
        dout << endl;
    }
}

static void applyDeviceInit(const long key, CtiDeviceSPtr Device, void *d)
{

    if( Device->isSingle() )
    {
        CtiDeviceSingle *DeviceRecord = (CtiDeviceSingle*)Device.get();
        DeviceRecord->doDeviceInit();       // Set the next scan times...
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << "): Non-scannable in scanner" << endl;
            dout << " PaoName: " << Device->getName() << endl;
        }
    }
}

static void applyResetScanFlags(const long key, CtiDeviceSPtr Device, void *d)
{
    if( Device->isSingle() )
    {
        CtiDeviceSingle *DeviceRecord = (CtiDeviceSingle*) Device.get();
        DeviceRecord->resetScanFlag();  // Reset the flags?
    }
}

static void applyGenerateScanRequests(const long key, CtiDeviceSPtr pBase, void *d)
{
    INT   nRet = 0;
    CtiTime TimeNow;
    list< OUTMESS* > &outList =  *((list< OUTMESS* > *)d);

    if(ScannerDebugLevel & SCANNER_DEBUG_DEVICEANALYSIS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Looking at " << pBase->getName() << endl;
    }

    if(pBase->isSingle() && !pBase->isInhibited())
    {
        CtiDeviceSingle *DeviceRecord = (CtiDeviceSingle*)pBase.get();

        /*
         *  ACCUMULATOR SCANNING
         */

        if(DeviceRecord->getNextScan(ScanRateAccum) <= TimeNow)
        {
            if(ScannerDebugLevel & SCANNER_DEBUG_ACCUMSCAN)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Accumulator Scan Checkpoint **** " << DeviceRecord->getID() << " / " <<  DeviceRecord->getName() << endl;

            }
            DeviceRecord->resetScanFlag(CtiDeviceSingle::ScanException);   // Results should be forced though the exception system
            nRet = DeviceRecord->initiateAccumulatorScan(outList);
        }

        /*
         *  INTEGRITY SCANNING
         */

        if(DeviceRecord->getNextScan(ScanRateIntegrity) <= TimeNow)
        {
            if(ScannerDebugLevel & SCANNER_DEBUG_INTEGRITYSCAN)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Integrity Scan Checkpoint **** " << DeviceRecord->getID() << " / " <<  DeviceRecord->getName() << endl;

            }
            DeviceRecord->resetScanFlag(CtiDeviceSingle::ScanException);   // Results should be forced though the exception system
            DeviceRecord->initiateIntegrityScan(outList);
        }

        /*
         *  EXCEPTION/GENERAL SCANNING
         */

        if(DeviceRecord->getNextScan(ScanRateGeneral) <= TimeNow)
        {
            if((nRet = DeviceRecord->initiateGeneralScan(outList)) > 0)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " General Scan Fail to Device: " << DeviceRecord->getName() << ". Error " << nRet << ": " << GetError(nRet) << endl;
            }
            else
            {
                if(ScannerDebugLevel & SCANNER_DEBUG_GENERALSCAN)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Exception/General Checkpoint ****   " << DeviceRecord->getID() << " / " <<  DeviceRecord->getName() << endl;
                }
                DeviceRecord->setScanFlag(CtiDeviceSingle::ScanException);   // Results need NOT be forced though the exception system
            }
        }

        // Make sure times/rates haven't changed on us...
        DeviceRecord->validateScanTimes();

        // expire any alternate rates we may have before calculating next scan
        DeviceRecord->checkSignaledAlternateRateForExpiration();
    }
}


static void applyDLCLPScan(const long key, CtiDeviceSPtr pBase, void *d)
{
    INT nRet;
    CtiTime TimeNow;
    list< OUTMESS* > &outList =  *((list< OUTMESS* > *)d);

    if(ScannerDebugLevel & SCANNER_DEBUG_DEVICEANALYSIS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Looking at " << pBase->getName() << endl;
    }

    //  only MCTs do DLC load profile scans
    if(isCarrierLPDevice(pBase))
    {
        Cti::Devices::MctDevice *pMCT = (Cti::Devices::MctDevice *)pBase.get();

        if(pMCT->getNextLPScanTime() <= TimeNow)
        {
            if((nRet = pMCT->initiateLoadProfileScan(outList)) > 0)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Load Profile Scan Fail to Device: " << pBase->getName() << endl;
                dout << "\tError " << nRet << ": " << GetError(nRet) << endl;
            }
            else
            {
                if(ScannerDebugLevel & SCANNER_DEBUG_LPSCAN)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Load Profile Checkpoint ****   " << pBase->getID() << " / " <<  pBase->getName() << endl;
                }
            }
        }
    }
}

static void applyValidateScanTimes(const long key, CtiDeviceSPtr pBase, void *d)
{
    bool bforce = (bool)d;

    if(pBase->isSingle())
    {
        CtiDeviceSingle* DeviceRecord = (CtiDeviceSingle*)pBase.get();
        DeviceRecord->validateScanTimes(bforce);
    }
}

static void applyGenerateScannerDataRows(const long key, CtiDeviceSPtr Device, void *d)
{
    vector< CtiTableDeviceScanData > dirtyData = *((vector< CtiTableDeviceScanData > *)d);

    if( Device->isSingle() )
    {
        CtiDeviceSingle *DeviceRecord = (CtiDeviceSingle*)Device.get();

        if(DeviceRecord->getScanData().isDirty())
        {
            if(ScannerDebugLevel & SCANNER_DEBUG_DYNAMICDATA)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << "   Updating DynamicDeviceScanData for device " << DeviceRecord->getName() << endl;
            }
            // DeviceRecord->getScanData().Update(conn);
            CtiTableDeviceScanData msd = DeviceRecord->getScanData();
            dirtyData.push_back( msd );
            DeviceRecord->getScanData().resetDirty();
        }
    }
}

INT ScannerMainFunction (INT argc, CHAR **argv)
{
    char  tstr[256];

    INT   nRet = 0;
    INT   i, j;
    DWORD dwWait;
    INT   ObjWait;

    /* Misc. definitions */
    UINT invcnt = 0;

    /* Define the various records */
    CtiDeviceSPtr Device;
    CtiDeviceSingle *DeviceRecord = NULL;

    CtiTime      NextScan[MAX_SCAN_TYPE];
    CtiTime      TimeNow;

    /* Define for the porter interface */
    IM_EX_CTIBASE extern USHORT   PrintLogEvent;
    list< OUTMESS* >         outList;         // Nice little collection of OUTMESS's


    int Op, k;

    HANDLE hScanArray[] = {
        hScannerSyncs[S_QUIT_EVENT],
        hScannerSyncs[S_SCAN_EVENT]
    };

    identifyProject(CompileInfo);
    setConsoleTitle(CompileInfo);

    /* Give us a tiny attitude */
    CTISetPriority(PRTYC_TIMECRITICAL, THREAD_PRIORITY_NORMAL);

    /* check for various flags */
    if(argc > 1)
    {
        for(i = 1; i < argc; i++)
        {
            if(!(stricmp (argv[i], "/NLP")))
            {
                SuspendLoadProfile = true;
                continue;
            }
        }
    }

    /* open the port pipe */
    PortPipeInit (NOWAIT);

    SET_CRT_OUTPUT_MODES;
    if(gConfigParms.isOpt("DEBUG_MEMORY") && gConfigParms.isTrue("DEBUG_MEMORY") )
        ENABLE_CRT_SHUTDOWN_CHECK;

    long pointID = ThreadMonitor.getPointIDFromOffset(CtiThreadMonitor::Scanner);
    CtiTime NextThreadMonitorReportTime;
    CtiThreadMonitor::State previous = CtiThreadMonitor::Normal;

    // Initialize the connection to VanGogh....
    VanGoghConnection.doConnect(VANGOGHNEXUS, VanGoghMachine);
    VanGoghConnection.setName("Dispatch");
    VanGoghConnection.WriteConnQue(CTIDBG_new CtiRegistrationMsg(SCANNER_REGISTRATION_NAME, rwThreadId(), TRUE));

    do
    {
        if(pointID!=0)
        {
            CtiThreadMonitor::State next;
            if((next = ThreadMonitor.getState()) != previous ||
               CtiTime::now() > NextThreadMonitorReportTime)
            {
                // Any time the state changes or every (StandardMonitorTime / 2) seconds, update the point
                previous = next;
                NextThreadMonitorReportTime = nextScheduledTimeAlignedOnRate( CtiTime::now(), CtiThreadMonitor::StandardMonitorTime / 2 );

                VanGoghConnection.WriteConnQue(CTIDBG_new CtiPointDataMsg(pointID, ThreadMonitor.getState(), NormalQuality, StatusPointType, ThreadMonitor.getString().c_str()));
            }
        }

        LoadScannableDevices();

        for(i = 0; i < MAX_SCAN_TYPE; i++)
        {
            NextScan[i] = MAXTime;
        }

        ScannerDeviceManager.apply(applyDeviceInit,NULL);

        NextScan[REMOTE_SCAN] = TimeOfNextRemoteScan();
        NextScan[WINDOW_OPENS] = TimeOfNextWindow();

        if( !SuspendLoadProfile )
        {
            NextScan[DLC_LP_SCAN] = TimeOfNextLPScan();
        }

        if(NextScan[REMOTE_SCAN] == MAXTime  &&
           NextScan[DLC_LP_SCAN] == MAXTime  &&
           !ScannerQuit)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Nothing to Scan.. Sleeping 30 seconds" << endl;
            }

            for(int i = 0; i < 30 && !ScannerQuit; i++)
            {
                Sleep(1000);
            }
        }
    }
    while(NextScan[REMOTE_SCAN] == MAXTime && NextScan[DLC_LP_SCAN] == MAXTime && !ScannerQuit);

    if(!ScannerQuit)
    {
        if(_beginthread (NexusThread, RESULT_THREAD_STK_SIZE, (void *)SCANNER_REGISTRATION_NAME) == -1)
        {
            dout << "Error starting Nexus Thread" << endl;
            return -1;
        }

        if(_beginthread (ResultThread, RESULT_THREAD_STK_SIZE, (void *)SCANNER_REGISTRATION_NAME) == -1)
        {
            dout << "Error starting Result Thread" << endl;
            return -1;
        }

        if(_beginthread (DispatchMsgHandlerThread, 0, NULL) == -1)
        {
            dout << "Error starting Dispatch Thread" << endl;
            return -1;
        }

        if(_beginthread (DatabaseHandlerThread, 0, NULL) == -1)
        {
            dout << "Error starting Dispatch Thread" << endl;
            return -1;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Init Complete. Entering Main Scan Loop" << endl;
        }
    }

    Cti::Timing::MillisecondTimer loop_timer;

    /* Everything is ready so go into the scan loop */
    for(;!ScannerQuit;)
    {
        loop_timer.reset();

        if(pointID!=0)
        {
            CtiThreadMonitor::State next;
            if((next = ThreadMonitor.getState()) != previous ||
               CtiTime::now() > NextThreadMonitorReportTime)
            {
                // Any time the state changes or every (StandardMonitorTime / 2) seconds, update the point
                previous = next;
                NextThreadMonitorReportTime = nextScheduledTimeAlignedOnRate( CtiTime::now(), CtiThreadMonitor::StandardMonitorTime / 2 );

                VanGoghConnection.WriteConnQue(CTIDBG_new CtiPointDataMsg(pointID, ThreadMonitor.getState(), NormalQuality, StatusPointType, ThreadMonitor.getString().c_str()));
            }
        }

        /* Release the Lock Semaphore */
        ReleaseMutex( hScannerSyncs[S_LOCK_MUTEX] );

        /*  get the soonest thing we need to scan
         *  Array position 0 == NEXT_SCAN is the actual NEXT_SCAN
         */
        NextScan[NEXT_SCAN] = MAXTime;
        for( i = 1; i < MAX_SCAN_TYPE; i++)
        {
            if(NextScan[NEXT_SCAN] > NextScan[i])
            {
                NextScan[NEXT_SCAN] = NextScan[i];
            }
        }

        TimeNow = TimeNow.now();

        if(TimeNow < NextScan[NEXT_SCAN])
        {
            ::sprintf(tstr, "At Start of Loop -- Will Sleep %d Seconds", NextScan[NEXT_SCAN].seconds() - TimeNow.seconds()  );

            {
                LONG omc = OutMessageCount();

                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << TimeNow << " " << tstr;
                if(omc > 10)
                {
                    dout << ". OM Count = " << omc << " size = " << sizeof(OUTMESS) * omc;
                }
                dout << endl;
            }

            ObjWait = (NextScan[NEXT_SCAN].seconds() - TimeNow.seconds()) * 1000L;

            dwWait = WaitForMultipleObjects(2, hScanArray, FALSE, ObjWait);

            switch(dwWait)
            {
            case WAIT_OBJECT_0:                                    // This is a quit request!
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Quit Event Posted" << endl << endl;
                    }
                    ScannerQuit = TRUE;
                    continue;            // the main for loop I hope.
                }
            case WAIT_OBJECT_0 + 1:                                // This is a quit request!
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Scan Event Set. Will examine devices now.  " << endl;
                    }
                    ResetEvent(hScannerSyncs[ S_SCAN_EVENT ]);

                    NextScan[REMOTE_SCAN] = TimeOfNextRemoteScan();
                    NextScan[WINDOW_OPENS] = TimeOfNextWindow();

                    if( !SuspendLoadProfile )
                    {
                        NextScan[DLC_LP_SCAN] = TimeOfNextLPScan();
                    }

                    break;
                }
            case WAIT_TIMEOUT:
            default:
                {
                    break;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << TimeNow << " At Start of Loop -- Will Scan Immediately" << endl;
            }
            ResetEvent(hScannerSyncs[ S_SCAN_EVENT ]);

            NextScan[NEXT_SCAN] = TimeNow + 1L;      // Try to keep this from being too tight.
        }

        /* Wait/Block on the return thread if neccessary */
        dwWait = WaitForMultipleObjects(2, hLockArray, FALSE, INFINITE);

        switch(dwWait - WAIT_OBJECT_0)
        {
        case 0:  // Quit
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Quit Event Posted" << endl << endl;
                }
                ScannerQuit = TRUE;
                break;            // the main for loop I hope.
            }
        case 1:  // Lock.. We expected this though!
            {
                break;
            }
        }

        /* Check if we need to reopen the port pipe */
        if(PorterNexus.NexusState == CTINEXUS_STATE_NULL && !ScannerQuit)
        {
            TimeNow = TimeNow.now();
            invcnt = TimeNow.second() - TimeNow.second() % 30;

            while(PorterNexus.NexusState == CTINEXUS_STATE_NULL && !ScannerQuit)
            {

                if(!(invcnt++ % 30) )     // Only gripe every 30 seconds.
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << TimeNow.now() << " Port Control connection is not valid " << endl;
                }

                if(!(PortPipeInit(NOWAIT)))
                {
                    // Make sure we don't hang up on him again.
                    LastPorterOutTime = PASTDATE;
                    LastPorterInTime  = LastPorterInTime.now();

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Reconnected to Port Control" << endl;
                    }

                    /* now walk through the scannable "Remote" devices */
                    if(ScannerDeviceManager.entries())
                    {
                        ScannerDeviceManager.apply(applyResetScanFlags, NULL);
                    }
                }

                // Check for the quit event every interation...
                dwWait = WaitForMultipleObjects(2, hScanArray, FALSE, 1000);
                switch(dwWait)
                {
                case WAIT_OBJECT_0:                                    // This is a quit request!
                    {
                        ScannerQuit = TRUE;
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Quit Event Posted" << endl << endl;
                        break;
                    }
                case WAIT_OBJECT_0 + 1:                                // This is a quit request!
                    {
                        ResetEvent(hScannerSyncs[ S_SCAN_EVENT ]);
                        break;
                    }
                case WAIT_TIMEOUT:
                default:
                    {
                        break;
                    }
                }
            }
        }

        if(ScannerQuit)
        {
            continue;      // get us out of here!
        }

        if(outList.size() > 0)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** ERROR!!!! Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  There should be only NONE!" << endl;
            }
        }

        /* Use the same time base for the full scan check */
        TimeNow = TimeNow.now();   // update the time...

        /* Check if we do remote scanning */
        if(TimeNow >= NextScan[REMOTE_SCAN] || TimeNow >= NextScan[WINDOW_OPENS])
        {
            ScannerDeviceManager.apply(applyGenerateScanRequests, (void*)(&outList) );

            // Send any requests over to porter for processing
            MakePorterRequests(outList);

            NextScan[REMOTE_SCAN] = TimeOfNextRemoteScan();
            NextScan[WINDOW_OPENS] = TimeOfNextWindow();
        }

        //  check if we need to do any DLC load profile scans
        if(!SuspendLoadProfile && TimeNow >= NextScan[DLC_LP_SCAN])
        {
            //  DLC load profile scans
            ScannerDeviceManager.apply(applyDLCLPScan,(void*)&outList);

            // Send any requests over to porter for processing
            MakePorterRequests(outList);

            NextScan[DLC_LP_SCAN] = TimeOfNextLPScan();
        }


        TimeNow = TimeNow.now();

        if( LastPorterOutTime.seconds() > (LastPorterInTime.seconds() + (3600 * 6)) )
        {

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << LastPorterOutTime << " > " << LastPorterInTime << " + (3600 * 6) seconds? " << endl;
            }

            LastPorterOutTime = PASTDATE;  // Make sure we don't repeat this forever!

            // I haven't heard from porter in a long long time...  Let's make sure he's init ok.
            PorterNexus.CTINexusClose();
        }

        DWORD loop_elapsed_time = loop_timer.elapsed();

        //  don't loop any faster than twice per second
        if( loop_elapsed_time < 500 )
        {
            Sleep(500 - loop_elapsed_time);
        }
    }  /* and do it all again */

    ScannerCleanUp();

    return 0;
}


/* The following thread handles results coming back from field devices */
void ResultThread (void *Arg)
{
    // I want an attitude!
    CTISetPriority(PRTYC_TIMECRITICAL, THREAD_PRIORITY_HIGHEST);

    /* Wait until main program is in loop */
    DWORD dwWait = WaitForMultipleObjects(2, hLockArray, FALSE, INFINITE);

    switch(dwWait - WAIT_OBJECT_0)
    {
    case 0:  // Quit.. ScannerQuit is set somewhere
        {
            ScannerQuit = TRUE;
        }
    case 1:  // Lock.. We expected this though!
        {
            break;
        }
    }

    ThreadStatusKeeper threadStatus("Scanner Result Thread");

    /* perform the wait loop forever */
    for(;!ScannerQuit;)
    {
        threadStatus.monitorCheck(CtiThreadRegData::None);

        /* Release the Lock Semaphore */
        if(dwWait == 1) ReleaseMutex(hScannerSyncs[S_LOCK_MUTEX]);
        dwWait = 0;

        deque<INMESS *> pendingInQueue;

        const unsigned int inQueueBlockSize =  50;
        const unsigned int inQueueMaxWait   = 500;  //  500 ms

        unsigned long start = GetTickCount();

        try
        {
            while( (GetTickCount() - start) < inQueueMaxWait )
            {
                {
                    CtiLockGuard< CtiMutex > ilguard( inmessMux, 500 );

                    if( ilguard.isAcquired() )
                    {
                        if( inmessList.size() >= inQueueBlockSize )
                        {
                            break;
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Unable to lock SCANNERS's INMESS list. You should not see this much." << endl;
                    }
                }

                CTISleep(50);
            }

            {
                CtiLockGuard< CtiMutex > ilguard( inmessMux, 500 );

                if( ilguard.isAcquired() )
                {
                    while( !inmessList.empty() )
                    {
                        pendingInQueue.push_back(inmessList.front());
                        inmessList.pop_front();
                    }
                }
            }

            if( !pendingInQueue.empty() )
            {
                set<long> paoids;

                for_each(pendingInQueue.begin(),
                         pendingInQueue.end(),
                         collect_inmess_target_device(paoids));

                ScannerPointManager.refreshListByPAOIDs(paoids);
            }

            /* Wait on the request thread if neccessary */
            dwWait = WaitForMultipleObjects(2, hLockArray, FALSE, INFINITE);

            dwWait -= WAIT_OBJECT_0;

            switch(dwWait)
            {
            case 0:  // Quit
                {
                    ScannerQuit = TRUE;
                    break;
                }
            case 1:  // Lock.. We expected this though!
                {
                    break;
                }
            }

            while( !ScannerQuit && !pendingInQueue.empty() )
            {
                INMESS *InMessage = pendingInQueue.front();
                pendingInQueue.pop_front();

                if( !InMessage )  continue;

                LastPorterInTime = LastPorterInTime.now();

                CtiDeviceManager::coll_type::reader_lock_guard_t guard(ScannerDeviceManager.getLock());

                // Find the device..
                LONG id = InMessage->TargetID;

                if(id == 0)
                {
                    id = InMessage->DeviceID;
                }

                CtiDeviceSPtr pBase = (CtiDeviceSPtr )ScannerDeviceManager.getDeviceByID(id);

                if(ScannerDebugLevel & SCANNER_DEBUG_INREPLIES)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " InMessage from " << pBase->getName() << " " << FormatError(InMessage->EventCode & 0x3fff) << endl;
                }

                if(pBase && pBase->isSingle())
                {
                    CtiDeviceSingleSPtr pSingle = boost::static_pointer_cast<CtiDeviceSingle>(pBase);

                    /* get the time for use in the decodes */
                    CtiTime TimeNow;

                    list< OUTMESS* >    outList;
                    list< CtiMessage* > retList;
                    list< CtiMessage* > vgList;

                    // Do some device dependent work on this Inbound message!
                    pSingle->ProcessResult(InMessage, TimeNow, vgList, retList, outList);

                    // Send any new porter requests to porter
                    if((ScannerDebugLevel & SCANNER_DEBUG_OUTLIST) && outList.size() > 0)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }

                    MakePorterRequests(outList);

                    // Write any results generated back to VanGogh
                    while(!retList.empty())
                    {
                        //  add protection here for CtiRequestMsgs going to Dispatch
                        VanGoghConnection.WriteConnQue(retList.front());   // I no longer manage this, the queue cleans up!
                        retList.pop_front();
                    }

                    // Write any signals or misc. messages back to VanGogh!
                    while(!vgList.empty())
                    {
                        VanGoghConnection.WriteConnQue(vgList.front());   // I no longer manage this, the queue cleans up!
                        vgList.pop_front();
                    }

                    /* Check if we should kick other thread in the pants */
                    if(pSingle->getScanRate(ScanRateGeneral) == 0)
                    {
                        // FIX FIX FIX This needs a new IPC with PORTER.. No DB connection anymore!
                        pSingle->setNextScan(ScanRateGeneral, TimeNow.now());
                        SetEvent(hScannerSyncs[S_SCAN_EVENT]);
                    }
                }
                else if(pBase && !pBase->isSingle())
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Device \"" << pBase->getName() <<"\" is not \"Single\" " << endl;
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Unknown device scanned.  Device ID: " << InMessage->DeviceID << endl;
                    dout << " Port listed as                   : " << InMessage->Port     << endl;
                    dout << " Remote listed as                 : " << InMessage->Remote   << endl;
                    dout << " Target Remote                    : " << InMessage->TargetID   << endl;
                }

                delete InMessage;
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

    } /* End of for */
}

void NexusThread (void *Arg)
{
    DWORD       dwWait;
    /* Define the return Pipe handle */
    IM_EX_CTIBASE extern CTINEXUS PorterNexus;

    /* Misc. definitions */
    ULONG       i = 0;
    /* Define the various time variable */
    CtiTime      TimeNow;

    /* Define the pipe variables */
    ULONG       BytesRead;
    INMESS      *InMessage = NULL;

    // I want an attitude!
    CTISetPriority(PRTYC_TIMECRITICAL, THREAD_PRIORITY_HIGHEST);

    /* perform the wait loop forever */
    for(;!ScannerQuit;)
    {
        /* Wait for the Nexus to come (?back?) up */
        while(PorterNexus.NexusState == CTINEXUS_STATE_NULL && !ScannerQuit)
        {
            if(!(i++ % 30))
            {
                PortPipeInit(NOWAIT);
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " NexusThread: Waiting for reconnection to Port Control" << endl;
            }

            CTISleep (1000L);
        }

        if(ScannerQuit)
        {
            continue; // get us out of here!
        }

        BytesRead = 0;
        InMessage = CTIDBG_new INMESS;
        ::memset(InMessage, 0, sizeof(*InMessage));

        /* get a result off the port pipe */
        if(PorterNexus.CTINexusRead(InMessage, sizeof(*InMessage), &BytesRead, CTINEXUS_INFINITE_TIMEOUT) || BytesRead < sizeof(*InMessage))     // Make sure we have an InMessage worth!
        {
            if(PorterNexus.NexusState != CTINEXUS_STATE_NULL)
            {
                PorterNexus.CTINexusClose();
            }

            Sleep(500);

            delete InMessage;
            InMessage = 0;
            continue;
        }

        if(!ScannerQuit)
        {
            LastPorterInTime = LastPorterInTime.now();

            if(InMessage != 0)
            {
                CtiLockGuard< CtiMutex > inguard( inmessMux );
                inmessList.push_back( InMessage );
                InMessage = 0;
            }
        }
    } /* End of for */

    if(InMessage != NULL)
    {
        delete InMessage;
    }
}


void ScannerCleanUp ()
{
    ScannerQuit = TRUE;

    ScannerDeviceManager.deleteList();
    PortPipeCleanup(0);

    VanGoghConnection.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15));
    Sleep(2000);
    VanGoghConnection.ShutdownConnection();


    dout << CtiTime() << " Scanner terminated!" << endl;

}


static void applyAnalyzeNextRemoteScan(const long key, CtiDeviceSPtr Device, void *d)
{
    CtiTime TimeNow;
    CtiTime TempTime(YUKONEOT);
    CtiTime &nextRemoteScanTime = *((CtiTime*)d);

    CtiDeviceSingle *DeviceRecord = (CtiDeviceSingle*)Device.get();

    if(Device->isSingle())
    {
        CtiDeviceSingle *DeviceRecord = (CtiDeviceSingle*)Device.get();

        if(!(DeviceRecord->isInhibited()) && (DeviceRecord->isWindowOpen()))
        {
            TempTime = DeviceRecord->nextRemoteScan();

            if(TempTime < TimeNow)
            {
                nextRemoteScanTime = TempTime + 1;
                barkAboutCurrentTime( Device, TempTime, __LINE__ );
            }
            else if( TempTime < nextRemoteScanTime )
            {
                nextRemoteScanTime = TempTime;
                barkAboutCurrentTime( Device, TempTime, __LINE__ );
            }
        }
    }
}

CtiTime TimeOfNextRemoteScan()
{
    CtiTime            nextRemoteScanTime(YUKONEOT);
    CtiTime            TimeNow;

    ScannerDeviceManager.apply(applyAnalyzeNextRemoteScan, (void*)&nextRemoteScanTime);

    /* Do not let this get out of hand, check once a minute if nothing else is looking */
    if(nextRemoteScanTime == MAXTime || (nextRemoteScanTime.seconds() - TimeNow.seconds()) > 60L)
    {
        TimeNow = TimeNow.now();
        nextRemoteScanTime = TimeNow.seconds() - (TimeNow.seconds() % 60) + 60;
    }

    return nextRemoteScanTime;
}


static void applyAnalyzeNextLPScan(const long key, CtiDeviceSPtr Device, void *d)
{
    try
    {
        CtiTime TimeNow;
        CtiTime TempTime(YUKONEOT);
        CtiTime &nextLPScanTime = *((CtiTime*)d);

        if(isCarrierLPDevice(Device))
        {
            Cti::Devices::MctDevice  *DeviceRecord = (Cti::Devices::MctDevice *)Device.get();

            if(!(DeviceRecord->isInhibited()) && (DeviceRecord->isWindowOpen()))
            {
                TempTime = DeviceRecord->calcNextLPScanTime();

                if(nextLPScanTime > TempTime)
                {
                    nextLPScanTime = TempTime;
                    barkAboutCurrentTime( Device, TempTime, __LINE__ );
                }
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

CtiTime TimeOfNextLPScan( void )
{
    CtiTime         nextLPScanTime(YUKONEOT);

    ScannerDeviceManager.apply(applyAnalyzeNextLPScan, (void*)&nextLPScanTime);

    return nextLPScanTime;
}

static void applyAnalyzeNextWindow(const long key, CtiDeviceSPtr Device, void *d)
{
    CtiTime TimeNow;
    CtiTime TempTime(YUKONEOT);
    CtiTime &nextWindow = *((CtiTime*)d);

    if(Device->isSingle())
    {
        CtiDeviceSingle *DeviceRecord = (CtiDeviceSingle*)Device.get();

        if(!(DeviceRecord->isInhibited()) && (DeviceRecord->isWindowOpen()))
        {
            TempTime = DeviceRecord->getNextWindowOpen();

            if(nextWindow > TempTime)
            {
                nextWindow = TempTime;
                barkAboutCurrentTime( Device, TempTime, __LINE__ );
            }
        }
    }
}

CtiTime TimeOfNextWindow( void )
{
    CtiTime nextWindow(YUKONEOT);

    ScannerDeviceManager.apply(applyAnalyzeNextWindow, (void*)&nextWindow);

    return nextWindow;
}


void InitScannerGlobals(void)
{
    if(gConfigParms.isOpt("SCANNER_RELOAD_RATE"))
    {
        ScannerReloadRate = std::max((unsigned int)gConfigParms.getValueAsULong("SCANNER_RELOAD_RATE"), (unsigned int)86400);
    }

    if(gConfigParms.isOpt("SCANNER_DEBUGLEVEL"))
    {
        string str = gConfigParms.getValueAsString("SCANNER_DEBUGLEVEL");
        char *eptr;
        ScannerDebugLevel = strtoul(str.c_str(), &eptr, 16);
        if(ScannerDebugLevel & 0x00000001)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Scanner DEBUGLEVEL " << hex << ScannerDebugLevel << dec << endl;
        }
    }

    if(gConfigParms.isOpt("SCANNER_OUTPUT_INTERVAL"))
    {
        string Temp = gConfigParms.getValueAsString("SCANNER_OUTPUT_INTERVAL");
        dout.setWriteInterval(atoi(Temp.c_str()) * 1000);
    }
}

void LoadScannableDevices(void *ptr)
{
    static bool bLoaded = false;        // Useful for debugging memory leaks.. Set to true inside the if.
    CtiHashKey  *hKey = NULL;
    CtiDeviceSPtr DeviceRecord;
    CtiDBChangeMsg *pChg = (CtiDBChangeMsg *)ptr;

    bool bforce = ((pChg == NULL) ? false : true);

    CtiTime start;
    CtiTime stop;


    InitScannerGlobals();      // Go fetch from the environmant
    ResetBreakAlloc();         // Make certain the debug library does not break us.

    CtiDeviceManager::coll_type::writer_lock_guard_t guard(ScannerDeviceManager.getLock());

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Starting LoadScannableDevices. " << (bforce ? "Due to DBChange." : "Not due to DBChange." ) << endl;
    }

    if(pChg == NULL || (pChg->getDatabase() == ChangeStateGroupDb) )
    {
        ReloadStateNames();
    }

    if(pChg == NULL || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_DEVICE) || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_ROUTE) )
    {
        try
        {
            start = start.now();

            if(pChg)
            {
                ScannerDeviceManager.refreshDeviceByID(pChg->getId(), pChg->getCategory(), pChg->getObjectType());
            }
            else
            {
                ScannerDeviceManager.refreshAllDevices();
            }

            ConfigManager.initialize(ScannerDeviceManager);

            stop = stop.now();

            if(stop.seconds() - start.seconds() > 5 || ScannerDebugLevel & SCANNER_DEBUG_DBRELOAD)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " RefreshList took " << stop.seconds() - start.seconds() << endl;
                }
            }
        }
        catch( ... )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    // Do this if there is no DBChange, or the change was a DEVICE change!
    start = start.now();
    if(pChg == NULL || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_DEVICE) )
    {
        if(pChg)
        {
            CtiDeviceSPtr pBase = ScannerDeviceManager.getDeviceByID(pChg->getId());

            if(pBase)
            {
                if(pBase->isSingle())
                {
                    boost::static_pointer_cast<CtiDeviceSingle>(pBase)->validateScanTimes(bforce);
                }

                pBase->setPointManager(&ScannerPointManager);
            }
        }
        else
        {
            ScannerDeviceManager.apply(applyValidateScanTimes, (void*)bforce);
            ScannerDeviceManager.apply(attachPointManagerToDevice, &ScannerPointManager);
        }
    }

    stop = stop.now();
    if(stop.seconds() - start.seconds() > 5 || ScannerDebugLevel & SCANNER_DEBUG_DBRELOAD)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " validateScanTimes took " << stop.seconds() - start.seconds() << endl;
        }
    }

    if(pChg != NULL && (pChg->getDatabase() == ChangeConfigDb))
    {
        ConfigManager.processDBUpdate(pChg->getId(), pChg->getCategory(), pChg->getObjectType(), pChg->getTypeOfChange());
    }

    if(pChg != NULL && pChg->getDatabase() == ChangePointDb)  // On a point specific message only!
    {
        if( (pChg->getTypeOfChange() == ChangeTypeAdd) || (pChg->getTypeOfChange() == ChangeTypeUpdate) )
        {
            LONG paoDeviceID = GetPAOIdOfPoint(pChg->getId());

            if( paoDeviceID )
            {
                CtiDeviceSPtr pBase = ScannerDeviceManager.getDeviceByID(paoDeviceID);

                if(pBase && pBase->isSingle())
                {
                    // Do anything required be particular devices on a reload here.
                    if(pBase->getType() == TYPE_WELCORTU)
                    {
                        ((CtiDeviceWelco*)pBase.get())->setDeadbandsSent(false);
                    }
                }
            }
        }
        else if(pChg->getTypeOfChange() == ChangeTypeDelete)
        {
            ScannerPointManager.erase(pChg->getId());
        }
    }

    if(!bLoaded)
    {
        bLoaded = true;
    }

    if(pChg != NULL)
    {
        delete pChg;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Done LoadScannableDevices." << endl;
    }

    return;
}

void DispatchMsgHandlerThread(void *Arg)
{
    BOOL           bServerClosing = FALSE;

    CtiTime         TimeNow;
    CtiTime         LastTime;

    ThreadStatusKeeper threadStatus("Scanner DispatchMsgHandlerThread");

    /* perform the wait loop forever */
    for( ; !ScannerQuit ; )
    {
        threadStatus.monitorCheck(CtiThreadRegData::None);

        CtiMessage *MsgPtr = VanGoghConnection.ReadConnQue(5000L);

        if(MsgPtr != NULL)
        {
            switch(MsgPtr->isA())
            {
            case MSG_DBCHANGE:
                {
                    // Refresh the scanner in memory database once every 5 minutes.
                    LoadScannableDevices((void*)MsgPtr->replicateMessage());
                    // Post the wakup to ensure that the main loop re-examines the devices.
                    SetEvent(hScannerSyncs[ S_SCAN_EVENT ]);

                    break;
                }
            case MSG_COMMAND:
                {
                    CtiCommandMsg* Cmd = (CtiCommandMsg*)MsgPtr;

                    switch(Cmd->getOperation())
                    {
                    case (CtiCommandMsg::Shutdown):
                        {
                            //SetEvent(hScannerSyncs[S_QUIT_EVENT]);
                            {
                                Cmd->dump();
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Shutdown requests by command messages are ignored." << endl;
                            }
                            break;
                        }
                    case (CtiCommandMsg::AreYouThere):
                        {
                            VanGoghConnection.WriteConnQue(Cmd->replicateMessage()); // Copy one back
                            break;
                        }
                    case (CtiCommandMsg::AlternateScanRate):
                        {
                            if(Cmd->getOpArgList().size() >= 4)
                            {
                                LONG token      = Cmd->getOpArgList().at(0);
                                LONG deviceId   = Cmd->getOpArgList().at(1);
                                LONG open       = Cmd->getOpArgList().at(2);
                                LONG duration   = Cmd->getOpArgList().at(3);

                                {
                                    CtiDeviceSPtr device;
                                    CtiDeviceSingle   *deviceSingle = NULL;
                                    //  I don't think we need this here - getEqual() is muxed, and what we really want is a device-level mux
                                    //CtiDeviceManager::LockGuard guard(ScannerDeviceManager.getMux());

                                    if( (device = ScannerDeviceManager.getDeviceByID( deviceId )) )
                                    {
                                        if( device->isSingle() )
                                        {
                                            CtiDeviceSingle *DeviceRecord = (CtiDeviceSingle*)device.get();
                                            DeviceRecord->applySignaledRateChange(open,duration);
                                            DeviceRecord->validateScanTimes(true);
                                            // 052203 CGP // DeviceRecord->checkSignaledAlternateRateForExpiration();
                                            SetEvent(hScannerSyncs[ S_SCAN_EVENT ]);
                                        }
                                        else
                                        {
                                            {
                                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") attempting to assign alternate rate to a non-scannable device" << endl;
                                            }
                                        }
                                    }
                                }
                            }
                            else
                            {
                               CtiLockGuard<CtiLogger> doubt_guard(dout);
                               dout << "**** Error, Alternate scan rate switch did not have a valid command vector **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            break;
                        }
                    default:
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "Unhandled command message " << Cmd->getOperation() << " sent to Main.." << endl;
                        }
                    }
                    break;
                }
            default:
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Scanner is absorbing an unhandled message " <<
                    MsgPtr->isA() << " from VanGogh " << endl;
                    break;
                }
            }

            delete MsgPtr;
        }
        else
        {
            // Should only get in here once every 5 seconds maximum... any faster and we are a CPU pig.
            TimeNow = TimeNow.now();

            if(TimeNow.seconds() - LastTime.seconds() < 4)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  Scanner's connection queue to dispatch is panicked." << endl;
                }

                Sleep(2500);
            }

            LastTime = TimeNow;
        }

    } /* End of for */
}

void DatabaseHandlerThread(void *Arg)
{
    BOOL    bServerClosing = FALSE;

    ThreadStatusKeeper threadStatus("Scanner DatabaseHandlerThread");

    CtiTime TimeNow;
    CtiTime RefreshTime = nextScheduledTimeAlignedOnRate( TimeNow, ScannerReloadRate );

    ULONG   refreshRate = 900L;     // Max 15 minute (900 second) time between database processing.
    ULONG   delta       = std::min( static_cast<ULONG>(RefreshTime.seconds() - TimeNow.seconds()), refreshRate);
    ULONG   counter     = 0;

    /* perform the wait loop forever */
    for( ; !ScannerQuit ; )
    {
        threadStatus.monitorCheck(CtiThreadRegData::None);

        if( WAIT_OBJECT_0 == WaitForSingleObject(hScannerSyncs[S_QUIT_EVENT], 1000) )
        {
            ScannerQuit = TRUE;
        }
        else
        {
            counter++;
        }

        if( !ScannerQuit && counter >= delta )
        {
            TimeNow = TimeNow.now();

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << TimeNow << " DatabaseHandlerThread managing the database now. " << endl;
            }

            RecordDynamicData();
            ScannerDeviceManager.writeDynamicPaoInfo();

            if(TimeNow >= RefreshTime)
            {
                // Refresh the scanner in memory database once every 5 minutes.
                LoadScannableDevices();
                // Post the wakup to ensure that the main loop re-examines the devices.
                SetEvent(hScannerSyncs[ S_SCAN_EVENT ]);

                RefreshTime = nextScheduledTimeAlignedOnRate( TimeNow, ScannerReloadRate );
            }

            counter = 0;
            TimeNow = TimeNow.now();
            delta   = std::min( static_cast<ULONG>(RefreshTime.seconds() - TimeNow.seconds()), refreshRate);
        }
    } /* End of for */

    RecordDynamicData();
    ScannerDeviceManager.writeDynamicPaoInfo();
}

INT MakePorterRequests(list< OUTMESS* > &outList)
{
    INT   i, j = 0;
    INT   status = NORMAL;
    ULONG BytesWritten;

    OUTMESS *OutMessage = NULL;

    IM_EX_CTIBASE extern CTINEXUS PorterNexus;

    /* if pipe shut down return the error */
    if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
    {
        status = PIPEWASBROKEN;
    }

    for( i = outList.size() ; status == NORMAL && i > 0; i-- )
    {
        OutMessage = outList.front(); outList.pop_front();

        if(OutMessage->ExpirationTime == 0)
        {
            // Scanner is about to make some big decisions...

            LONG exptime = gConfigParms.getValueAsInt("SCANNER_REQUEST_EXPIRATION_TIME", 3600);
            OutMessage->ExpirationTime = CtiTime().seconds() + exptime;
        }

        while(PorterNexus.NexusState == CTINEXUS_STATE_NULL && !ScannerQuit)
        {
            if(!(j++ % 30))
            {
                PortPipeInit(NOWAIT);
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " MakePorterRequests: Waiting for reconnection to Port Control" << endl;
            }


            CTISleep (1000L);

            if(j > 900)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                status = PIPEWASBROKEN;
                break;              // Did not connect after 15 minutes!
            }
        }

        /* And send them to porter */
        if(PorterNexus.NexusState != CTINEXUS_STATE_NULL)
        {
            if(ScannerDebugLevel & SCANNER_DEBUG_OUTREQUESTS)
            {
                if(ScannerDeviceManager.getDeviceByID(OutMessage->TargetID))
                {
                    string name = ScannerDeviceManager.getDeviceByID(OutMessage->TargetID)->getName();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " OutMessage to " << name << endl;
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " OutMessage to TargetID: " << OutMessage->TargetID << endl;
                    }
                }
            }

            if(PorterNexus.CTINexusWrite (OutMessage, sizeof(OUTMESS), &BytesWritten, 30L) || BytesWritten == 0)
            {
                PorterNexus.CTINexusClose();
            }
            else
            {
                if(BytesWritten != sizeof(OUTMESS))
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** ERROR ERROR ERROR **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Porter has been shorted by a few bytes!" << endl;
                    dout << " Scannable ID " << OutMessage->DeviceID << " may be out-of-sync \n\tuntil 5 minutes past the next legitimate scan time" << endl;
                }
                LastPorterOutTime = LastPorterOutTime.now();
            }
        }

        // Message is re-built on the other side, so clean it up!
        delete OutMessage;
    }

    if(outList.size()) outList.clear();

    return status;
}


INT RecordDynamicData()
{
    INT status = NORMAL;

    // Make an attempt to keep the ScanData table current
    if(ScannerDeviceManager.entries())
    {
        CtiDeviceSPtr Device;

        vector< CtiTableDeviceScanData > dirtyData;

        Cti::Database::DatabaseConnection   conn;

        if ( ! conn.isValid() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** ERROR **** Invalid Connection to Database.  " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            return NOTNORMAL;
        }

        /*
         *  We will be going with the idea that a minimal duration locking of the Manager is KEY KEY KEY here.
         */
        ScannerDeviceManager.apply(applyGenerateScannerDataRows, (void*)&dirtyData);

        conn.beginTransaction();

        try
        {
            vector< CtiTableDeviceScanData >::iterator dirtyit;

            for(dirtyit = dirtyData.begin(); dirtyit != dirtyData.end(); dirtyit++)
            {
                CtiTableDeviceScanData &dirty = *dirtyit;
                dirty.Update(conn);
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ") ";
                dout << " Trying to commit transaction on DynamicDeviceScanData" << endl;

            }
        }
        conn.commitTransaction();
    }

    return status;
}
