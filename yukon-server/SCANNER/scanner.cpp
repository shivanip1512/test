#include "precompiled.h"

#include "dbaccess.h"
#include "dsm2.h"
#include "error.h"
#include "queues.h"
#include "porter.h"
#include "scanner.h"
#include "master.h"
#include "dllbase.h"
#include "dlldev.h"
// Here are the devices for which scannables have been defined

#include "scanner_syncs.h"

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
#include "logManager.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_dbchg.h"

#include "cparms.h"
#include "connection_client.h"
#include "amq_constants.h"
#include "amq_queues.h"
#include "streamAmqConnection.h"

#include "module_util.h"
#include "dllyukon.h"
#include "thread_monitor.h"
#include "ThreadStatusKeeper.h"
#include "database_connection.h"
#include "database_transaction.h"
#include "millisecond_timer.h"
#include "GlobalSettings.h"
#include "win_helper.h"
#include "ServiceMetricReporter.h"
#include "MessageCounter.h"

#include <boost/ptr_container/ptr_deque.hpp>
#include <boost/assign/list_of.hpp>
#include <boost/interprocess/ipc/message_queue.hpp>
#include <boost/interprocess/exceptions.hpp>
#include <boost/interprocess/creation_tags.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>

#include <process.h>

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

void    NexusThread(Cti::StreamAmqConnection<CtiOutMessage, INMESS>& PorterNexus);
void    ResultThread(Cti::StreamAmqConnection<CtiOutMessage, INMESS>& PorterNexus);
INT     RecordDynamicData();
void    InitScannerGlobals(void);
void    DumpRevision(void);
INT     MakePorterRequests(list< OUTMESS* > &outList, Cti::StreamAmqConnection<CtiOutMessage, INMESS>& PorterNexus);

Cti::ScannableDeviceManager ScannerDeviceManager;
CtiPointManager             ScannerPointManager;

extern BOOL ScannerQuit;

CtiClientConnection VanGoghConnection( Cti::Messaging::ActiveMQ::Queue::dispatch );

ULONG ScannerDebugLevel = 0;

std::array<HANDLE, 2> hLockArray;

void acquireMutex(::Cti::CallSite caller)
{
    const DWORD dwWait = WaitForMultipleObjects(2, hLockArray.data(), FALSE, INFINITE);

    // dwWait returns WAIT_OBJECT_0 if it's the first object signaled, 
    // WAIT_OBJECT_0+1 if it's the second.  Same nomenclature for WAIT_ABANDONED
    switch(dwWait)
    {
    case WAIT_OBJECT_0: // Quit
    case WAIT_ABANDONED_0: // Quit (sent on shutdown)
    case WAIT_ABANDONED_0 + 1: // Quit (sent on shutdown)
        {
            CTILOG_INFO(dout, "Quit Event Posted" << caller);
            ScannerQuit = TRUE;
            break;
        }
    case WAIT_OBJECT_0 + 1:  // Lock.. We expected this though!
        {
            break;
        }
    case WAIT_FAILED:
        {
            const DWORD error = GetLastError();
            CTILOG_ERROR(dout, "Wait failed with error code "<< error <<" /"<< Cti::getSystemErrorMessage(error) << caller);
            break;
        }
    default: // we dont expect WAIT_TIMEOUT
        {
            CTILOG_ERROR(dout, "Unexpected wait result ("<< dwWait <<")"<< caller);
        }
    }

    //FIXME: current logic ignores of the lock is not acquired
    //retry in a loop or throw std::runtime_error() ?
}

void releaseMutex(int line)
{
    if( ! ReleaseMutex( hScannerSyncs[S_LOCK_MUTEX] ))
    {
        const DWORD error = GetLastError();
        CTILOG_ERROR(dout, "ReleaseMutex failed with error code "<< error <<" / "<< Cti::getSystemErrorMessage(error) <<", on "<< line);
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
        CTILOG_WARN(dout, "Non-scannable in scanner, PaoName: "<< Device->getName());
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
    YukonError_t nRet = ClientErrors::None;
    CtiTime TimeNow;
    CtiDeviceBase::OutMessageList &outList = *(static_cast<CtiDeviceBase::OutMessageList *>(d));

    if(ScannerDebugLevel & SCANNER_DEBUG_DEVICEANALYSIS)
    {
        CTILOG_DEBUG(dout, "Looking at "<< pBase->getName());
    }

    auto fillEmptyExpirationTimeWith =
        [](const time_t expirationTime, CtiOutMessage *om)
        {
            if( om && ! om->ExpirationTime )
            {
                om->ExpirationTime = expirationTime;
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
                CTILOG_DEBUG(dout, "Accumulator Scan - "<< DeviceRecord->getID() <<" / "<<  DeviceRecord->getName());
            }

            CtiDeviceBase::OutMessageList scanOMs;
            DeviceRecord->resetScanFlag(CtiDeviceSingle::ScanException);   // Results should be forced though the exception system
            nRet = DeviceRecord->initiateAccumulatorScan(scanOMs);

            const auto defaultAccumExpiration = TimeNow.seconds() + DeviceRecord->getTardyInterval(ScanRateAccum);
            for_each(scanOMs.begin(), scanOMs.end(),
                    [=](CtiOutMessage *om){
                        fillEmptyExpirationTimeWith(defaultAccumExpiration, om);
                    });

            outList.splice(outList.end(), scanOMs);
        }

        /*
         *  INTEGRITY SCANNING
         */

        if(DeviceRecord->getNextScan(ScanRateIntegrity) <= TimeNow)
        {
            if(ScannerDebugLevel & SCANNER_DEBUG_INTEGRITYSCAN)
            {
                CTILOG_DEBUG(dout, "Integrity Scan - "<< DeviceRecord->getID() <<" / "<<  DeviceRecord->getName());
            }

            CtiDeviceBase::OutMessageList scanOMs;
            DeviceRecord->resetScanFlag(CtiDeviceSingle::ScanException);   // Results should be forced though the exception system
            DeviceRecord->initiateIntegrityScan(scanOMs);

            const auto defaultIntegrityExpiration = TimeNow.seconds() + DeviceRecord->getTardyInterval(ScanRateIntegrity);
            for_each(scanOMs.begin(), scanOMs.end(),
                    [=](CtiOutMessage *om){
                        fillEmptyExpirationTimeWith(defaultIntegrityExpiration, om);
                    });

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
                CTILOG_ERROR(dout, "General Scan Failed to Device: "<< DeviceRecord->getName() <<". Error "<< nRet <<": "<< CtiError::GetErrorString(nRet));
            }
            else
            {
                if(ScannerDebugLevel & SCANNER_DEBUG_GENERALSCAN)
                {
                    CTILOG_DEBUG(dout, "General Scan - "<< DeviceRecord->getID() <<" / "<< DeviceRecord->getName());
                }

                DeviceRecord->setScanFlag(CtiDeviceSingle::ScanException);   // Results need NOT be forced though the exception system

                const auto defaultExceptionExpiration = TimeNow.seconds() + DeviceRecord->getTardyInterval(ScanRateGeneral);
                for_each(scanOMs.begin(), scanOMs.end(),
                        [=](CtiOutMessage *om){
                            fillEmptyExpirationTimeWith(defaultExceptionExpiration, om);
                        });

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
    YukonError_t nRet;
    CtiTime TimeNow;
    list< OUTMESS* > &outList =  *((list< OUTMESS* > *)d);

    if(ScannerDebugLevel & SCANNER_DEBUG_DEVICEANALYSIS)
    {
        CTILOG_DEBUG(dout, "Looking at "<< pBase->getName());
    }

    //  only MCTs do DLC load profile scans
    if(isCarrierLPDeviceType(pBase->getType()))
    {
        Cti::Devices::MctDevice *pMCT = (Cti::Devices::MctDevice *)pBase.get();

        if(pMCT->getNextLPScanTime() <= TimeNow)
        {
            if((nRet = pMCT->initiateLoadProfileScan(outList)) > 0)
            {
                CTILOG_ERROR(dout, "Load Profile Scan Failed to Device: "<< pBase->getName() <<". Error "<< nRet <<": "<< CtiError::GetErrorString(nRet));
            }
            else
            {
                if(ScannerDebugLevel & SCANNER_DEBUG_LPSCAN)
                {
                    CTILOG_DEBUG(dout, "Device Id/Name - "<< pBase->getID() <<" / "<< pBase->getName());
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
                CTILOG_DEBUG(dout, "Updating DynamicDeviceScanData for device "<< DeviceRecord->getName());
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
    Cti::identifyExecutable(CompileInfo);

    Cti::DynamicPaoInfoManager::setOwner(Cti::Application_Scanner);

    hLockArray = { hScannerSyncs[S_QUIT_EVENT], hScannerSyncs[S_LOCK_MUTEX] };

    /* check for various flags */
    if(argc > 1)
    {
        for(int i = 1; i < argc; i++)
        {
            if(!(stricmp (argv[i], "/NLP")))
            {
                SuspendLoadProfile = true;
                continue;
            }
        }
    }

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
                CTILOG_ERROR(dout, "Database connection attempt failed.");

                writeLogMessage = false;
            }
            Sleep( 5000 );
        }
        if ( ScannerQuit )
        {
            return -1;
        }
    }

    Cti::StreamAmqConnection<CtiOutMessage, INMESS> PorterNexus(
        Cti::Messaging::ActiveMQ::Queues::OutboundQueue::ScannerOutMessages,
        Cti::Messaging::ActiveMQ::Queues::InboundQueue::ScannerInMessages);

    long pointID = ThreadMonitor.getProcessPointID();
    Cti::ServiceMetrics::MetricReporter metricReporter {
        Cti::ServiceMetrics::CpuPointOffsets::Scanner,
        Cti::ServiceMetrics::MemoryPointOffsets::Scanner,
        "Scanner",
        SCANNER_APPLICATION_NAME };

    CtiTime NextThreadMonitorReportTime;

    CtiThreadMonitor::State previous = CtiThreadMonitor::Normal;

    // Initialize the connection to VanGogh....
    VanGoghConnection.setName("Scanner to Dispatch");
    VanGoghConnection.start();

    VanGoghConnection.WriteConnQue(CTIDBG_new CtiRegistrationMsg(SCANNER_REGISTRATION_NAME, GetCurrentThreadId(), true), CALLSITE);

    CtiTime NextScan[MAX_SCAN_TYPE];

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

                VanGoghConnection.WriteConnQue(CTIDBG_new CtiPointDataMsg(pointID, ThreadMonitor.getState(), NormalQuality, 
                    StatusPointType, ThreadMonitor.getString().c_str()), CALLSITE);
            }
        }

        LoadScannableDevices();

        for(int i = 0; i < MAX_SCAN_TYPE; i++)
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

        // Todo: Looks like NextScan[REMOTE_SCAN] is always set to +60 seconds, so this may be
        // dead code.  
        if(NextScan[REMOTE_SCAN] == MAXTime  &&
           NextScan[DLC_LP_SCAN] == MAXTime  &&
           !ScannerQuit)
        {
            CTILOG_INFO(dout, "Nothing to Scan.. Sleeping 30 seconds");

            for(int i = 0; i < 30 && !ScannerQuit; i++)
            {
                Sleep(1000);
            }

            metricReporter.reportCheck(CompileInfo, VanGoghConnection);
        }
    }
    while(NextScan[REMOTE_SCAN] == MAXTime && NextScan[DLC_LP_SCAN] == MAXTime && !ScannerQuit);

    if(!ScannerQuit)
    {
        std::thread { NexusThread, std::ref( PorterNexus ) }.detach();

        std::thread { ResultThread, std::ref( PorterNexus ) }.detach();

        if(_beginthread (DispatchMsgHandlerThread, 0, NULL) == -1)
        {
            CTILOG_ERROR(dout, "Could not starting Dispatch Thread");
            return -1;
        }

        if(_beginthread (DatabaseHandlerThread, 0, NULL) == -1)
        {
            CTILOG_ERROR(dout, "Could not start Database Thread");
            return -1;
        }

        CTILOG_INFO(dout, "Init Complete. Entering Main Scan Loop");
    }

    Cti::Timing::MillisecondTimer loop_timer;

    HANDLE hScanArray[] = {
        hScannerSyncs[S_QUIT_EVENT],
        hScannerSyncs[S_SCAN_EVENT]
    };

    CtiTime TimeNow;

    list< OUTMESS* > outList;

    acquireMutex(CALLSITE);

    /* Everything is ready so go into the scan loop */
    for(;!ScannerQuit;)
    {
        loop_timer.reset();

        dout->poke();  //  called 2x/second  (see Sleep at bottom of loop)

        if(pointID!=0)
        {
            CtiThreadMonitor::State next;
            if((next = ThreadMonitor.getState()) != previous ||
               CtiTime::now() > NextThreadMonitorReportTime)
            {
                // Any time the state changes or every (StandardMonitorTime / 2) seconds, update the point
                previous = next;
                NextThreadMonitorReportTime = nextScheduledTimeAlignedOnRate( CtiTime::now(), CtiThreadMonitor::StandardMonitorTime / 2 );

                VanGoghConnection.WriteConnQue(CTIDBG_new CtiPointDataMsg(pointID, ThreadMonitor.getState(), NormalQuality, 
                    StatusPointType, ThreadMonitor.getString().c_str()), CALLSITE);
            }
        }

        metricReporter.reportCheck(CompileInfo, VanGoghConnection);

        // release the lock
        releaseMutex(__LINE__);

        /*  get the soonest thing we need to scan
         *  Array position 0 == NEXT_SCAN is the actual NEXT_SCAN
         */
        NextScan[NEXT_SCAN] = MAXTime;
        for(int i = 1; i < MAX_SCAN_TYPE; i++)
        {
            if(NextScan[NEXT_SCAN] > NextScan[i])
            {
                NextScan[NEXT_SCAN] = NextScan[i];
            }
        }

        TimeNow = TimeNow.now();

        if(TimeNow < NextScan[NEXT_SCAN])
        {
            const unsigned long waitSeconds = NextScan[NEXT_SCAN].seconds() - TimeNow.seconds();

            {
                Cti::StreamBuffer logmsg;
                logmsg << "At start of loop - Will wait "<< waitSeconds <<" seconds";

                const LONG omc = OutMessageCount();
                if(omc > 10)
                {
                    logmsg <<". OM Count = "<< omc <<" (size = "<< sizeof(OUTMESS) * omc <<" bytes)";
                }

                CTILOG_INFO(dout, logmsg)
            }

            DWORD dwWait = WaitForMultipleObjects(2, hScanArray, FALSE, waitSeconds * 1000L);

            switch(dwWait)
            {
            case WAIT_OBJECT_0:
                {
                    CTILOG_INFO(dout, "Quit Event Posted");
                    ScannerQuit = TRUE;
                    continue;  // the main for loop I hope.
                }
            case WAIT_OBJECT_0 + 1: // Scan Event
                {
                    CTILOG_INFO(dout, "Scan Event Set. Will examine devices now.");

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
                {
                    break;
                }
            case WAIT_FAILED:
                {
                    const DWORD err = GetLastError();
                    CTILOG_ERROR(dout, "Wait failed with error code "<< err <<" / "<< Cti::getSystemErrorMessage(err));
                    Sleep(1000); // prevent run-away loops
                    break;
                }
            default:
                {
                    CTILOG_ERROR(dout, "unexpected wait result ("<< dwWait <<")");
                    Sleep(1000); // prevent run-away loops
                }
            }
        }
        else
        {
            CTILOG_INFO(dout, "At start of loop - Will scan immediately" );

            ResetEvent(hScannerSyncs[ S_SCAN_EVENT ]);

            NextScan[NEXT_SCAN] = TimeNow + 1L;      // Try to keep this from being too tight.
        }

        acquireMutex(CALLSITE);

        if( ! outList.empty() )
        {
            CTILOG_ERROR(dout, "outList should be empty");
        }

        /* Use the same time base for the full scan check */
        TimeNow = TimeNow.now();   // update the time...

        /* Check if we do remote scanning */
        if(TimeNow >= NextScan[REMOTE_SCAN] || TimeNow >= NextScan[WINDOW_OPENS])
        {
            ScannerDeviceManager.apply(applyGenerateScanRequests, (void*)(&outList) );

            // Send any requests over to porter for processing
            MakePorterRequests(outList, PorterNexus);

            NextScan[REMOTE_SCAN] = TimeOfNextRemoteScan();
            NextScan[WINDOW_OPENS] = TimeOfNextWindow();
        }

        //  check if we need to do any DLC load profile scans
        if(!SuspendLoadProfile && TimeNow >= NextScan[DLC_LP_SCAN])
        {
            //  DLC load profile scans
            ScannerDeviceManager.apply(applyDLCLPScan,(void*)&outList);

            // Send any requests over to porter for processing
            MakePorterRequests(outList, PorterNexus);

            NextScan[DLC_LP_SCAN] = TimeOfNextLPScan();
        }

        TimeNow = TimeNow.now();

        if( LastPorterOutTime.seconds() > (LastPorterInTime.seconds() + (3600 * 6)) )
        {
            CTILOG_WARN(dout, "LastPorterOutTime > LastPorterInTime + (3600 * 6) seconds ("<<  LastPorterOutTime <<" > "<< LastPorterInTime <<" + (3600 * 6) seconds?");

            LastPorterOutTime = PASTDATE;  // Make sure we don't repeat this forever!
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
void ResultThread (Cti::StreamAmqConnection<CtiOutMessage, INMESS>& PorterNexus)
{
    acquireMutex(CALLSITE);

    ThreadStatusKeeper threadStatus("Scanner Result Thread");

    /* perform the wait loop forever */
    for(;!ScannerQuit;)
    {
        threadStatus.monitorCheck(CtiThreadRegData::None);

        // Release the Mutex
        releaseMutex(__LINE__);

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
                        CTILOG_WARN(dout, "Unable to lock SCANNERS's INMESS list. You should not see this much.");
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
                ScannerPointManager.refreshListByPAOIDs(
                    boost::copy_range<std::set<long>>(
                        pendingInQueue | boost::adaptors::transformed(get_inmess_target_device)));
            }

            // Wait on the request thread if necessary
            acquireMutex(CALLSITE);

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

                if( auto pBase = ScannerDeviceManager.getDeviceByID(id); ! pBase )
                {
                    Cti::FormattedList loglist;
                    loglist.add("Device ID") << InMessage.DeviceID;
                    loglist.add("Port listed as") << InMessage.Port;
                    loglist.add("Remote listed as") << InMessage.Remote;
                    loglist.add("Target Remote") << InMessage.TargetID;

                    CTILOG_INFO(dout, "Unknown device scanned." <<
                        loglist);
                }
                else if( ! pBase->isSingle() )
                {
                    CTILOG_INFO(dout, "Device \"" << pBase->getName() << "\" is not \"Single\"");
                }
                else 
                { 
                    if(ScannerDebugLevel & SCANNER_DEBUG_INREPLIES)
                    {
                        CTILOG_DEBUG(dout, "InMessage from "<< pBase->getName() <<" "<< CtiError::GetErrorString(InMessage.ErrorCode));
                    }

                    CtiDeviceSingleSPtr pSingle = boost::static_pointer_cast<CtiDeviceSingle>(pBase);

                    /* get the time for use in the decodes */
                    CtiTime TimeNow;

                    list< OUTMESS* >    outList;
                    list< CtiMessage* > retList;
                    list< CtiMessage* > vgList;

                    // Do some device dependent work on this Inbound message!
                    pSingle->ProcessResult(InMessage, TimeNow, vgList, retList, outList);

                    // Send any new porter requests to porter
                    if((ScannerDebugLevel & SCANNER_DEBUG_OUTLIST) && ! outList.empty())
                    {
                        CTILOG_DEBUG(dout, "outList has OUTMESS");
                    }

                    MakePorterRequests(outList, PorterNexus);

                    // Write any results generated back to VanGogh
                    for( const auto retMsg : retList )
                    {
                        //  add protection here for CtiRequestMsgs going to Dispatch
                        VanGoghConnection.WriteConnQue(retMsg, CALLSITE);   // I no longer manage this, the queue cleans up!
                    }

                    // Write any signals or misc. messages back to VanGogh!
                    for( const auto vgMsg : vgList )
                    {
                        VanGoghConnection.WriteConnQue(vgMsg, CALLSITE);   // I no longer manage this, the queue cleans up!
                    }

                    /* Check if we should kick other thread in the pants */
                    if(pSingle->getScanRate(ScanRateGeneral) == 0)
                    {
                        // FIX FIX FIX This needs a new IPC with PORTER.. No DB connection anymore!
                        pSingle->setNextScan(ScanRateGeneral, TimeNow.now());
                        SetEvent(hScannerSyncs[S_SCAN_EVENT]);
                    }
                }
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }

    } /* End of for */
}

namespace {

void waitForScannerQuitEvent(const unsigned long millis)
{
    const DWORD waitResult = WaitForSingleObject(hScannerSyncs[S_QUIT_EVENT], millis);
    if( waitResult == WAIT_FAILED )
    {
        const int error = GetLastError();
        CTILOG_ERROR(dout, "Wait failed with error code "<< error <<" / "<< Cti::getSystemErrorMessage(error));
        Sleep(1000); // prevent run-away loops
    }
}

} // namespace anonymous

void NexusThread (Cti::StreamAmqConnection<CtiOutMessage, INMESS>& PorterNexus)
{
    /* Misc. definitions */
    ULONG       i = 0;

    /* perform the wait loop forever */
    for(;!ScannerQuit;)
    {
        std::unique_ptr<INMESS> InMessage;

        try
        {
            /* get a result off the port pipe */
            InMessage = PorterNexus.read(Chrono::infinite, &hScannerSyncs[S_QUIT_EVENT]);
        }
        catch( const StreamConnectionException &ex )
        {
            CTILOG_EXCEPTION_ERROR(dout, ex, "NexusThread - Could not read InMessage from porterNexus");
        }

        if( ! InMessage )    // Make sure we have an InMessage!
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

    VanGoghConnection.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15), CALLSITE);
    Sleep(2000);
    VanGoghConnection.close();

    CTILOG_INFO(dout, "Scanner shutdown");
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

                if(ScannerDebugLevel & SCANNER_DEBUG_NEXTSCAN)
                {
                    CTILOG_DEBUG(dout, "Next scan is for "<< Device->getName() <<" at "<< TempTime);
                }
            }
            else if( TempTime < nextRemoteScanTime )
            {
                nextRemoteScanTime = TempTime;

                if(ScannerDebugLevel & SCANNER_DEBUG_NEXTSCAN)
                {
                    CTILOG_DEBUG(dout, "Next scan is for "<< Device->getName() <<" at "<< TempTime);
                }
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

                    if(ScannerDebugLevel & SCANNER_DEBUG_NEXTSCAN)
                    {
                        CTILOG_DEBUG(dout, "Next scan is for "<< Device->getName() <<" at "<< TempTime);
                    }
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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

                if(ScannerDebugLevel & SCANNER_DEBUG_NEXTSCAN)
                {
                    CTILOG_DEBUG(dout, "Next scan is for "<< Device->getName() <<" at "<< TempTime);
                }
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
            CTILOG_DEBUG(dout, "Scanner DEBUGLEVEL "<< hex << ScannerDebugLevel);
        }
    }

    if(gConfigParms.isOpt("SCANNER_OUTPUT_INTERVAL"))
    {
        string Temp = gConfigParms.getValueAsString("SCANNER_OUTPUT_INTERVAL");
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

    CTILOG_INFO(dout, "Starting LoadScannableDevices. "<< (bforce ? "Due to DBChange." : "Not due to DBChange."));

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
                CTILOG_DEBUG(dout, "RefreshList took "<< (stop.seconds() - start.seconds()) <<" seconds");
            }
        }
        catch( ... )
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
        CTILOG_DEBUG(dout, "validateScanTimes took "<< (stop.seconds() - start.seconds()) <<" seconds");
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

    CTILOG_INFO(dout, "Done LoadScannableDevices.");

    return;
}

void DispatchMsgHandlerThread(void *Arg)
{
    CtiTime         TimeNow;
    CtiTime         LastTime;
    Cti::MessageCounter mc("Dispatch->Scanner");

    CtiDBChangeMsg* dbChange;

    ThreadStatusKeeper threadStatus("Scanner DispatchMsgHandlerThread");

    /* perform the wait loop forever */
    for( ; !ScannerQuit ; )
    {
        threadStatus.monitorCheck(CtiThreadRegData::None);

        if( const CtiMessage *MsgPtr = VanGoghConnection.ReadConnQue(5000L) )
        {
            mc.increment();

            switch(MsgPtr->isA())
            {
            case MSG_DBCHANGE:
                {
                    dbChange = (CtiDBChangeMsg*)MsgPtr;
                    // Refresh the scanner in memory database once every 5 minutes.
                    LoadScannableDevices((void*)MsgPtr->replicateMessage());
                    // Post the wakup to ensure that the main loop re-examines the devices.
                    SetEvent(hScannerSyncs[ S_SCAN_EVENT ]);
                    // In the event that a GlobalSetting has been updated, reload GlobalSettings.
                    if (resolveDBCategory(dbChange->getCategory()) == CtiDBChangeCategory::GlobalSetting)
                    {
                        GlobalSettings::reload();

                        doutManager.reloadSettings();
                    }
                    break;
                }
            case MSG_COMMAND:
                {
                    const CtiCommandMsg& Cmd = static_cast<const CtiCommandMsg&>(*MsgPtr);

                    switch(Cmd.getOperation())
                    {
                    case (CtiCommandMsg::Shutdown):
                        {
                            CTILOG_WARN(dout, "Received shutdown request - Shutdown requests by command messages are ignored."<<
                                    Cmd);

                            //SetEvent(hScannerSyncs[S_QUIT_EVENT]);
                            break;
                        }
                    case (CtiCommandMsg::AreYouThere):
                        {
                            VanGoghConnection.WriteConnQue(Cmd.replicateMessage(), CALLSITE); // Copy one back
                            break;
                        }
                    case (CtiCommandMsg::AlternateScanRate):
                        {
                            if(Cmd.getOpArgList().size() >= 4)
                            {
                                LONG token      = Cmd.getOpArgList().at(0);
                                LONG deviceId   = Cmd.getOpArgList().at(1);
                                LONG open       = Cmd.getOpArgList().at(2);
                                LONG duration   = Cmd.getOpArgList().at(3);

                                {
                                    CtiDeviceSPtr device;
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
                                            CTILOG_ERROR(dout, "Attempting to assign alternate rate to a non-scannable device"<<
                                                    Cmd);
                                        }
                                    }
                                }
                            }
                            else
                            {
                                CTILOG_ERROR(dout, "Alternate scan rate switch did not have a valid command vector"<<
                                        Cmd);
                            }
                            break;
                        }
                    default:
                        {
                            CTILOG_WARN(dout, "Unhandled command message sent to Main.."<<
                                    Cmd);
                        }
                    }
                    break;
                }
            default:
                {
                    CTILOG_WARN(dout, "Scanner is absorbing an unhandled message from VanGogh: "<<
                            *MsgPtr)
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
                CTILOG_WARN(dout, "Scanner's connection queue to dispatch is panicked");
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
                            CTILOG_WARN(dout, "receiving paoIds back-to-back for more then 1 second");
                            break;
                        }
                    }

                    Cti::DynamicPaoInfoManager::schedulePaoIdsToReload(paoIds);
                }

                if( ! ScannerQuit && (timeNow = CtiTime::now()) >= databaseRefreshTime )
                {
                    CTILOG_INFO(dout, "Managing the database now");

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
            CTILOG_EXCEPTION_ERROR(dout, ex);
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

INT MakePorterRequests(list< OUTMESS* > &outList, Cti::StreamAmqConnection<CtiOutMessage, INMESS>& PorterNexus)
{
    INT   i, j = 0;
    INT   status = ClientErrors::None;

    OUTMESS *OutMessage = NULL;

    for( i = outList.size() ; status == ClientErrors::None && i > 0; i-- )
    {
        OutMessage = outList.front(); outList.pop_front();

        if(OutMessage->ExpirationTime == 0)
        {
            // Scanner is about to make some big decisions...

            LONG exptime = gConfigParms.getValueAsInt("SCANNER_REQUEST_EXPIRATION_TIME", 3600);
            OutMessage->ExpirationTime = CtiTime().seconds() + exptime;
        }

        /* And send them to porter */
        if(ScannerDebugLevel & SCANNER_DEBUG_OUTREQUESTS)
        {
            if(const CtiDeviceSPtr device = ScannerDeviceManager.getDeviceByID(OutMessage->TargetID))
            {
                CTILOG_DEBUG(dout, "OutMessage to Device: "<< device->getName());
            }
            else
            {
                CTILOG_DEBUG(dout, "OutMessage to TargetID: "<< OutMessage->TargetID);
            }
        }

        bool success = false;
        boost::optional<std::string> errorCause;

        try
        {
            success = PorterNexus.write(OutMessage, Chrono::seconds(30));
        }
        catch( const StreamConnectionException& ex )
        {
            errorCause = ex.what();
        }

        if( ! success )
        {
            CTILOG_ERROR(dout, "Could not write to porter, "
                               "reason: "<< (errorCause ? errorCause->c_str() : "Timeout"));
        }
        else
        {
            LastPorterOutTime = CtiTime();
        }

        // Message is re-built on the other side, so clean it up!
        delete OutMessage;
    }

    if(outList.size()) outList.clear();

    return status;
}


INT RecordDynamicData()
{
    INT status = ClientErrors::None;

    // Make an attempt to keep the ScanData table current
    if(ScannerDeviceManager.entries())
    {
        CtiDeviceSPtr Device;

        vector< CtiTableDeviceScanData > dirtyData;

        Cti::Database::DatabaseConnection   conn;

        if ( ! conn.isValid() )
        {
            CTILOG_ERROR(dout, "Invalid Database Connection");

            return ClientErrors::Abnormal;
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
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Failed to commit transaction on DynamicDeviceScanData");
        }
    }

    return status;
}
