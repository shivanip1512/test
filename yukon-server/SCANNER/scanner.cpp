#include "precompiled.h"

#include "dbaccess.h"
#include "dsm2.h"
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
#include "mgr_dyn_paoinfo.h"
#include "dev_base.h"
#include "dev_single.h"
#include "dev_mct.h"  //  for DLC loadprofile scans
#include "dev_welco.h"

#include "logger.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_dbchg.h"

#include "cparms.h"
#include "connection_client.h"
#include "amq_constants.h"

#include "utility.h"
#include "dllyukon.h"
#include "thread_monitor.h"
#include "ThreadStatusKeeper.h"
#include "database_connection.h"
#include "database_transaction.h"
#include "millisecond_timer.h"
#include "win_helper.h"

#include <boost/ptr_container/ptr_deque.hpp>
#include <boost/assign/list_of.hpp>
#include <boost/interprocess/ipc/message_queue.hpp>
#include <boost/interprocess/exceptions.hpp>
#include <boost/interprocess/creation_tags.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>

#define NEXT_SCAN       0
#define REMOTE_SCAN     1
#define DLC_LP_SCAN     2
#define WINDOW_OPENS    3
#define MAX_SCAN_TYPE   4

using namespace std;
using Cti::ThreadStatusKeeper;
using Cti::Timing::Chrono;
using Cti::StreamConnectionException;
using Cti::StreamSocketConnection;

static INT      ScannerReloadRate = 60 * 60 * 24; // 24 hours
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

Cti::ScannableDeviceManager ScannerDeviceManager;
CtiPointManager             ScannerPointManager;

extern BOOL ScannerQuit;

CtiClientConnection VanGoghConnection( Cti::Messaging::ActiveMQ::Queue::dispatch );

ULONG ScannerDebugLevel = 0;

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
    CtiDeviceBase::OutMessageList &outList = *(static_cast<CtiDeviceBase::OutMessageList *>(d));

    if(ScannerDebugLevel & SCANNER_DEBUG_DEVICEANALYSIS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Looking at " << pBase->getName() << endl;
    }

    //  poor man's lambda, fix me when C++11-able
    struct setOutMessageExpiration
    {
        setOutMessageExpiration(CtiTime expirationTime_) : expirationTime(expirationTime_) {}

        const CtiTime expirationTime;

        void operator()(CtiOutMessage *om)
        {
            if( om && ! om->ExpirationTime )
            {
                om->ExpirationTime = expirationTime.seconds();
            }
        }
    };

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
            CtiDeviceBase::OutMessageList scanOMs;
            DeviceRecord->resetScanFlag(CtiDeviceSingle::ScanException);   // Results should be forced though the exception system
            nRet = DeviceRecord->initiateAccumulatorScan(scanOMs);

            for_each(scanOMs.begin(), scanOMs.end(), setOutMessageExpiration(TimeNow.seconds() + DeviceRecord->getTardyTime(ScanRateAccum)));

            outList.splice(outList.end(), scanOMs);
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
            CtiDeviceBase::OutMessageList scanOMs;
            DeviceRecord->resetScanFlag(CtiDeviceSingle::ScanException);   // Results should be forced though the exception system
            DeviceRecord->initiateIntegrityScan(scanOMs);

            for_each(scanOMs.begin(), scanOMs.end(), setOutMessageExpiration(TimeNow.seconds() + DeviceRecord->getTardyTime(ScanRateIntegrity)));

            outList.splice(outList.end(), scanOMs);
        }

        /*
         *  EXCEPTION/GENERAL SCANNING
         */

        if(DeviceRecord->getNextScan(ScanRateGeneral) <= TimeNow)
        {
            CtiDeviceBase::OutMessageList scanOMs;
            if((nRet = DeviceRecord->initiateGeneralScan(scanOMs)) > 0)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " General Scan Fail to Device: " << DeviceRecord->getName() << ". Error " << nRet << ": " << GetErrorString(nRet) << endl;
            }
            else
            {
                if(ScannerDebugLevel & SCANNER_DEBUG_GENERALSCAN)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Exception/General Checkpoint ****   " << DeviceRecord->getID() << " / " <<  DeviceRecord->getName() << endl;
                }
                DeviceRecord->setScanFlag(CtiDeviceSingle::ScanException);   // Results need NOT be forced though the exception system

                for_each(scanOMs.begin(), scanOMs.end(), setOutMessageExpiration(TimeNow.seconds() + DeviceRecord->getTardyTime(ScanRateGeneral)));

                outList.splice(outList.end(), scanOMs);
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
    if(isCarrierLPDeviceType(pBase->getType()))
    {
        Cti::Devices::MctDevice *pMCT = (Cti::Devices::MctDevice *)pBase.get();

        if(pMCT->getNextLPScanTime() <= TimeNow)
        {
            if((nRet = pMCT->initiateLoadProfileScan(outList)) > 0)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Load Profile Scan Fail to Device: " << pBase->getName() << endl;
                dout << "\tError " << nRet << ": " << GetErrorString(nRet) << endl;
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

    INT   i, j;
    DWORD dwWait;
    INT   ObjWait;

    /* Misc. definitions */
    UINT invcnt = 0;

    CtiTime      NextScan[MAX_SCAN_TYPE];
    CtiTime      TimeNow;

    /* Define for the porter interface */
    list< OUTMESS* >         outList;         // Nice little collection of OUTMESS's

    HANDLE hScanArray[] = {
        hScannerSyncs[S_QUIT_EVENT],
        hScannerSyncs[S_SCAN_EVENT]
    };

    identifyProject(CompileInfo);
    setConsoleTitle(CompileInfo);

    Cti::DynamicPaoInfoManager::setOwner(Cti::Application_Scanner);

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

    // Make sure the database is available before we try to load anything from it.
    {
        bool writeLogMessage = true;

        while ( ! ( ScannerQuit || canConnectToDatabase() ) )
        {
            if ( writeLogMessage )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime( ) << " - Database connection attempt failed." << std::endl;

                writeLogMessage = false;
            }
            Sleep( 5000 );
        }
        if ( ScannerQuit )
        {
            return -1;
        }
    }

    long pointID = ThreadMonitor.getPointIDFromOffset(CtiThreadMonitor::Scanner);
    CtiTime NextThreadMonitorReportTime;
    CtiThreadMonitor::State previous = CtiThreadMonitor::Normal;

    // Initialize the connection to VanGogh....
    VanGoghConnection.setName("Scanner to Dispatch");
    VanGoghConnection.start();
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
            dout << "Error starting Database Thread" << endl;
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
        if( ! PorterNexus.isValid() && ! ScannerQuit )
        {
            TimeNow = TimeNow.now();
            invcnt = TimeNow.second() - TimeNow.second() % 30;

            while( ! PorterNexus.isValid() && ! ScannerQuit )
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
            PorterNexus.close();
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

        boost::ptr_deque<INMESS> pendingInQueue;

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
                        if( INMESS *im = inmessList.front() )
                        {
                            pendingInQueue.push_back(im);
                        }
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
                boost::ptr_deque<INMESS>::auto_type pInMessage = pendingInQueue.pop_front();
                INMESS &InMessage = *pInMessage;

                LastPorterInTime = LastPorterInTime.now();

                CtiDeviceManager::coll_type::reader_lock_guard_t guard(ScannerDeviceManager.getLock());

                // Find the device..
                LONG id = InMessage.TargetID;

                if(id == 0)
                {
                    id = InMessage.DeviceID;
                }

                CtiDeviceSPtr pBase = (CtiDeviceSPtr )ScannerDeviceManager.getDeviceByID(id);

                if(ScannerDebugLevel & SCANNER_DEBUG_INREPLIES)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " InMessage from " << pBase->getName() << " " << GetErrorString(InMessage.ErrorCode) << endl;
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
                    dout << "Unknown device scanned.  Device ID: " << InMessage.DeviceID << endl;
                    dout << " Port listed as                   : " << InMessage.Port     << endl;
                    dout << " Remote listed as                 : " << InMessage.Remote   << endl;
                    dout << " Target Remote                    : " << InMessage.TargetID   << endl;
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

    } /* End of for */
}

namespace {

void waitForScannerQuitEvent(const unsigned long millis)
{
    const DWORD waitResult = WaitForSingleObject(&hScannerSyncs[S_QUIT_EVENT], millis);
    if( waitResult == WAIT_FAILED )
    {
        const int error = GetLastError();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** ERROR **** " << error << " -> " << Cti::getSystemErrorMessage(error) << " "
                 <<  __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        Sleep(millis); // avoid run-away loops
    }
}

} // namespace anonymous

void NexusThread (void *Arg)
{
    /* Define the return Pipe handle */
    IM_EX_CTIBASE extern StreamSocketConnection PorterNexus;

    /* Misc. definitions */
    ULONG       i = 0;

    /* perform the wait loop forever */
    for(;!ScannerQuit;)
    {
        /* Wait for the Nexus to come (?back?) up */
        while( ! PorterNexus.isValid() && ! ScannerQuit )
        {
            if(!(i++ % 30))
            {
                PortPipeInit(NOWAIT);
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " NexusThread: Waiting for reconnection to Port Control" << endl;
            }

            waitForScannerQuitEvent(1000);
        }

        if( ScannerQuit )
        {
            continue; // get us out of here!
        }

        auto_ptr<INMESS> InMessage(new INMESS); // Note: INMESS has a memset zero in its constructor

        int bytesRead = 0;
        try
        {
            /* get a result off the port pipe */
            bytesRead = PorterNexus.read(InMessage.get(), sizeof(INMESS), Chrono::infinite, &hScannerSyncs[S_QUIT_EVENT]);
        }
        catch( const StreamConnectionException &ex )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " NexusThread: Error while reading porterNexus: " << ex.what() << endl;
        }

        if( bytesRead != sizeof(INMESS) )    // Make sure we have an InMessage worth!
        {
            if( ! ScannerQuit )
            {
                waitForScannerQuitEvent(500);
            }

            continue; // skip queuing the message
        }

        if( ! ScannerQuit )
        {
            LastPorterInTime = LastPorterInTime.now();

            {
                CtiLockGuard< CtiMutex > inguard( inmessMux );
                inmessList.push_back(InMessage.release());
            }
        }
    } /* End of for */
}


void ScannerCleanUp ()
{
    ScannerQuit = TRUE;

    ScannerDeviceManager.deleteList();
    PortPipeCleanup(0);

    VanGoghConnection.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15));
    Sleep(2000);
    VanGoghConnection.close();


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

        if(isCarrierLPDeviceType(Device->getType()))
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
    CtiDBChangeMsg *pChg = (CtiDBChangeMsg *)ptr;

    bool bforce = ((pChg == NULL) ? false : true);

    CtiTime start;
    CtiTime stop;


    InitScannerGlobals();      // Go fetch from the environmant

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

            Cti::ConfigManager::initialize();

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
        Cti::ConfigManager::handleDbChange(pChg->getId(), pChg->getCategory(), pChg->getObjectType(), pChg->getTypeOfChange());
    }

    if(pChg != NULL && pChg->getDatabase() == ChangePointDb)  // On a point specific message only!
    {
        if( (pChg->getTypeOfChange() == ChangeTypeAdd) || (pChg->getTypeOfChange() == ChangeTypeUpdate) )
        {
            ScannerPointManager.refreshList(pChg->getId(), 0, resolvePointType(pChg->getObjectType()));

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
    CtiTime         TimeNow;
    CtiTime         LastTime;

    ThreadStatusKeeper threadStatus("Scanner DispatchMsgHandlerThread");

    /* perform the wait loop forever */
    for( ; !ScannerQuit ; )
    {
        threadStatus.monitorCheck(CtiThreadRegData::None);

        if( CtiMessage *MsgPtr = VanGoghConnection.ReadConnQue(5000L) )
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

namespace {

inline boost::posix_time::ptime getAbsTimeFromMillis( unsigned long millis )
{
    return boost::posix_time::microsec_clock::universal_time() + boost::posix_time::milliseconds(millis);
}

} // anonymous

void DatabaseHandlerThread(void *Arg)
{
    ThreadStatusKeeper threadStatus("Scanner DatabaseHandlerThread");

    CtiTime timeNow;
    CtiTime refreshTime = nextScheduledTimeAlignedOnRate(timeNow, ScannerReloadRate);

    const unsigned long databaseRefreshRate = 60 * 15; // Max 15 minute (900 seconds) time between database processing.

    CtiTime databaseRefreshTime = std::min((timeNow + databaseRefreshRate), refreshTime);

    long paoId;
    unsigned int priority;
    boost::interprocess::message_queue::size_type recvd_size;

    while( ! ScannerQuit )
    {
        try
        {
            // Erase the message queue (should not throw)
            boost::interprocess::message_queue::remove("SCANNER_DYNAMICDATA_DBCHANGE");

            // Create a message_queue.
            boost::interprocess::message_queue message_queue(
                   boost::interprocess::create_only,   // create only
                   "SCANNER_DYNAMICDATA_DBCHANGE",     // name
                   1024,                               // max message number
                   sizeof(long)                        // max message size
                   );

            while( ! ScannerQuit )
            {
                threadStatus.monitorCheck(CtiThreadRegData::None);

                if( message_queue.timed_receive(&paoId, sizeof(long), recvd_size, priority, getAbsTimeFromMillis(1000)) &&
                    recvd_size == sizeof(long) )
                {
                    Cti::Database::id_set paoIds = boost::assign::list_of(paoId);

                    Cti::Timing::MillisecondTimer timer_backtoback;

                    // receive back-to-back paoIds from porter, allow 1 ms
                    while( message_queue.timed_receive(&paoId, sizeof(long), recvd_size, priority, getAbsTimeFromMillis(1)) &&
                           recvd_size == sizeof(long) )
                    {
                        paoIds.insert(paoId);

                        if( timer_backtoback.elapsed() > 1000 )
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** WARNING **** receiving paoIds back-to-back for more then 1 second " << __FILE__ << " (" << __LINE__ << ") " << endl;
                            }
                            break;
                        }
                    }

                    Cti::DynamicPaoInfoManager::schedulePaoIdsToReload(paoIds);
                }

                if( ! ScannerQuit && (timeNow = CtiTime::now()) >= databaseRefreshTime )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << timeNow << " DatabaseHandlerThread managing the database now. " << endl;
                    }

                    RecordDynamicData();
                    Cti::DynamicPaoInfoManager::writeInfo();

                    if( timeNow >= refreshTime )
                    {
                        LoadScannableDevices();
                        // Post the wakup to ensure that the main loop re-examines the devices.
                        SetEvent(hScannerSyncs[S_SCAN_EVENT]);

                        refreshTime = nextScheduledTimeAlignedOnRate(timeNow, ScannerReloadRate);
                    }

                    databaseRefreshTime = std::min((timeNow + databaseRefreshRate), refreshTime);
                }
            }
        }
        catch( boost::interprocess::interprocess_exception& ex )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** : " << ex.what() << " " << __FILE__ << " (" << __LINE__ << ") " << endl;
        }

        if( ! ScannerQuit )
        {
            Sleep(1000); // prevent run-away loop
        }
    }

    // Erase the message queue (should not throw)
    boost::interprocess::message_queue::remove("SCANNER_DYNAMICDATA_DBCHANGE");

    RecordDynamicData();
    Cti::DynamicPaoInfoManager::writeInfo();
}

INT MakePorterRequests(list< OUTMESS* > &outList)
{
    INT   i, j = 0;
    INT   status = NoError;

    OUTMESS *OutMessage = NULL;

    IM_EX_CTIBASE extern StreamSocketConnection PorterNexus;

    /* if pipe shut down return the error */
    if( ! PorterNexus.isValid() )
    {
        status = PIPEWASBROKEN;
    }

    for( i = outList.size() ; status == NoError && i > 0; i-- )
    {
        OutMessage = outList.front(); outList.pop_front();

        if(OutMessage->ExpirationTime == 0)
        {
            // Scanner is about to make some big decisions...

            LONG exptime = gConfigParms.getValueAsInt("SCANNER_REQUEST_EXPIRATION_TIME", 3600);
            OutMessage->ExpirationTime = CtiTime().seconds() + exptime;
        }

        while( ! PorterNexus.isValid() && ! ScannerQuit )
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
        if( PorterNexus.isValid() )
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

            int bytesWritten = 0;
            boost::optional<std::string> errorReason;

            try
            {
                bytesWritten = PorterNexus.write(OutMessage, sizeof(OUTMESS), Chrono::seconds(30));
            }
            catch( const StreamConnectionException &ex )
            {
                errorReason = ex.what();
            }

            if( ! bytesWritten )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** ERROR **** while writing to porter, closing connection " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  Reason: " << (errorReason ? errorReason->c_str() : "Timeout") << endl;
                }
                PorterNexus.close();
            }
            if( bytesWritten != sizeof(OUTMESS) )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** ERROR ERROR ERROR **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    INT status = NoError;

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

            return Error_Abnormal;
        }

        /*
         *  We will be going with the idea that a minimal duration locking of the Manager is KEY KEY KEY here.
         */
        ScannerDeviceManager.apply(applyGenerateScannerDataRows, (void*)&dirtyData);

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
    }

    return status;
}
