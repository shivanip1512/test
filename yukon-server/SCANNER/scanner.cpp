/*-----------------------------------------------------------------------------*
*
* File:   scanner
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SCANNER/scanner.cpp-arc  $
* REVISION     :  $Revision: 1.73 $
* DATE         :  $Date: 2008/09/18 15:30:45 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>       // These next few are required for Win32
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
#include "device.h"
#include "drp.h"
#include "elogger.h"
#include "dsm2err.h"
#include "alarmlog.h"
#include "routes.h"
#include "queues.h"
#include "porter.h"
#include "lm_auto.h"
#include "perform.h"
#include "scanner.h"
#include "ilex.h"
#include "master.h"
#include "dllbase.h"
#include "dlldev.h"
// Here are the devices for which scannables have been defined

#include "scanglob.h"
#include "scansup.h"

#include "rtdb.h"
#include "mgr_device.h"
#include "mgr_point.h"
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

#define NEXT_SCAN       0
#define REMOTE_SCAN     1
#define DLC_LP_SCAN     2
#define WINDOW_OPENS    3
#define MAX_SCAN_TYPE   4

using namespace std;

static INT      ScannerReloadRate = 86400;
static CtiTime  LastPorterOutTime;
static CtiTime  LastPorterInTime;

static CtiMutex inmessMux;
static list< INMESS* > inmessList;

const CtiTime   MAXTime(YUKONEOT);

void  LoadScannableDevices(void *ptr = NULL);
void  DispatchMsgHandlerThread(VOID *Arg);
void  DatabaseHandlerThread(VOID *Arg);

/* Local Declarations... Utility Functions */

CtiTime  TimeOfNextRemoteScan(void);
CtiTime  TimeOfNextLPScan(void);
static CtiTime  TimeOfNextWindow( void );

VOID    NexusThread(VOID *Arg);
INT     RecordDynamicData();
void    InitScannerGlobals(void);
void    DumpRevision(void);
INT     MakePorterRequests(list< OUTMESS* > &outList);

CtiDeviceManager  ScannerDeviceManager(Application_Scanner);
CtiPointManager   ScannerPointManager;

static RWWinSockInfo  winsock;

extern BOOL ScannerQuit;

CtiConnection     VanGoghConnection;
ULONG             ScannerDebugLevel = 0;
bool              CCUQueueScans = false;

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
        CtiDeviceMCT *pMCT = (CtiDeviceMCT *)pBase.get();

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
            if(!(stricmp (argv[i], "/NQ")))
            {
                CCUQueue = false;
                continue;
            }

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
    if(gConfigParms.isOpt("DEBUG_MEMORY") && !stringCompareIgnoreCase(gConfigParms.getValueAsString("DEBUG_MEMORY"),"true") )
        ENABLE_CRT_SHUTDOWN_CHECK;



    do
    {
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
        // Initialize the connection to VanGogh....
        VanGoghConnection.doConnect(VANGOGHNEXUS, VanGoghMachine);
        VanGoghConnection.setName("Dispatch");
        VanGoghConnection.WriteConnQue(CTIDBG_new CtiRegistrationMsg(SCANNER_REGISTRATION_NAME, rwThreadId(), TRUE));

        if(_beginthread (NexusThread, RESULT_THREAD_STK_SIZE, (VOID *)SCANNER_REGISTRATION_NAME) == -1)
        {
            dout << "Error starting Nexus Thread" << endl;
            return -1;
        }

        if(_beginthread (ResultThread, RESULT_THREAD_STK_SIZE, (VOID *)SCANNER_REGISTRATION_NAME) == -1)
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

    /* Everything is ready so go into the scan loop */
    for(;!ScannerQuit;)
    {
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
                ULONG omc = OutMessageCount();

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
    }  /* and do it all again */

    ScannerCleanUp();

    return 0;
}


/* The following thread handles results coming back from field devices */
VOID ResultThread (VOID *Arg)
{
    DWORD       dwWait;
    /* Define the return Pipe handle */
    IM_EX_CTIBASE extern CTINEXUS PorterNexus;

    /* Misc. definitions */
    ULONG       i;
    FLOAT       PValue;
    USHORT      DoAccums;

    CtiMessage  *pResponseMsg = NULL;
    /* Define the various records */
    CtiDeviceSPtr pBase;
    CtiDeviceSingle   *DeviceRecord;

    /* Define the various time variable */
    CtiTime      TimeNow;

    /* Define the pipe variables */
    ULONG       BytesRead;
    INMESS      *InMessage = 0;

    HANDLE      evShutdown;

    list< OUTMESS* > outList;
    list< CtiMessage* > retList;
    list< CtiMessage* > vgList;


    // I want an attitude!
    CTISetPriority(PRTYC_TIMECRITICAL, THREAD_PRIORITY_HIGHEST);

    int TracePrint (PBYTE, INT);

    if(NULL == (evShutdown = CreateEvent(NULL, TRUE, FALSE, SCANNER_SHUTDOWN_EVENT)))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "ResultThread is unable to open the shutdown event" << endl;
        }
        Sleep(2500);
    }

    /* Wait until main program is in loop */
    dwWait = WaitForMultipleObjects(2, hLockArray, FALSE, INFINITE);

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

    /* perform the wait loop forever */
    for(;!ScannerQuit;)
    {
        /* Release the Lock Semaphore */
        if(dwWait == 1) ReleaseMutex(hScannerSyncs[S_LOCK_MUTEX]);
        dwWait = 0;

        do
        {
            // Let's go look at the inbound sList, if we can!
            while( inmessList.empty() && !ScannerQuit)
            {
                Sleep( 500 );
            }

            if( !ScannerQuit && !inmessList.empty() )
            {
                CtiLockGuard< CtiMutex > ilguard( inmessMux, 15000 );
                if(ilguard.isAcquired())
                {
                    InMessage = inmessList.front();inmessList.pop_front();
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Unable to lock SCANNERS's INMESS list. You should not see this much." << endl;
                }
            }
        }
        while (InMessage == 0 && !ScannerQuit);

        if(ScannerQuit || InMessage == 0)
        {
            continue;
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

        if(!ScannerQuit)
        {
            LastPorterInTime = LastPorterInTime.now();

            CtiDeviceManager::coll_type::reader_lock_guard_t guard(ScannerDeviceManager.getLock());

            // Find the device..
            LONG id = InMessage->TargetID;

            if(id == 0)
            {
                id = InMessage->DeviceID;
            }

            pBase = (CtiDeviceSPtr )ScannerDeviceManager.getEqual(id);

            if(ScannerDebugLevel & SCANNER_DEBUG_INREPLIES)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " InMessage from " << pBase->getName() << " " << FormatError(InMessage->EventCode & 0x3fff) << endl;
            }

            if(pBase && pBase->isSingle())
            {
                try
                {
                    DeviceRecord = (CtiDeviceSingle*)pBase.get();

                    /* get the time for use in the decodes */
                    TimeNow = CtiTime();

                    // Do some device dependent work on this Inbound message!
                    DeviceRecord->ProcessResult(InMessage, TimeNow, vgList, retList, outList);

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
                    while(retList.size())
                    {
                        //  add protection here for CtiRequestMsgs going to Dispatch
                        VanGoghConnection.WriteConnQue(retList.front());   // I no longer manage this, the queue cleans up!
                        retList.pop_front();
                    }

                    // Write any signals or misc. messages back to VanGogh!
                    while(vgList.size())
                    {
                        VanGoghConnection.WriteConnQue((CtiMessage*)vgList.front());   // I no longer manage this, the queue cleans up!
                        vgList.pop_front();
                    }

                    /* Check if we should kick other thread in the pants */
                    if(DeviceRecord->getScanRate(ScanRateGeneral) == 0)
                    {
                        // FIX FIX FIX This needs a new IPC with PORTER.. No DB connection anymore!
                        DeviceRecord->setNextScan(ScanRateGeneral, TimeNow.now());
                        SetEvent(hScannerSyncs[S_SCAN_EVENT]);
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
        }

        if(InMessage)
        {
            delete InMessage;
            InMessage = 0;
        }

    } /* End of for */
}

VOID NexusThread (VOID *Arg)
{
    DWORD       dwWait;
    /* Define the return Pipe handle */
    IM_EX_CTIBASE extern CTINEXUS PorterNexus;

    /* Misc. definitions */
    ULONG       i;
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


VOID ScannerCleanUp ()
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
                CtiDeviceMCT  *DeviceRecord = (CtiDeviceMCT *)Device.get();

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
        string Temp = gConfigParms.getValueAsString("SCANNER_RELOAD_RATE");
        ScannerReloadRate = atoi (Temp.c_str());
        if(ScannerReloadRate < 86400)
        {
            ScannerReloadRate = 86400;
        }
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

    if(gConfigParms.isOpt("SCANNER_QUEUE"))
    {
        string Temp = gConfigParms.getValueAsString("SCANNER_QUEUE");
        std::transform(Temp.begin(), Temp.end(), Temp.begin(), ::tolower);

        if(Temp == "true" || Temp == "yes")
        {
            CCUQueue = true;
        }
        else
        {
            CCUQueue = false;
        }
    }

    if(gConfigParms.isOpt("SCANNER_QUEUE_SCANS"))
    {
        string Temp = gConfigParms.getValueAsString("SCANNER_QUEUE_SCANS");
        std::transform(Temp.begin(), Temp.end(), Temp.begin(), ::tolower);

        if(Temp == "true" || Temp == "yes")
        {
            CCUQueueScans = true;
        }
        else
        {
            CCUQueueScans = false;
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
    ScannerDeviceManager.setIncludeScanInfo();

    if(pChg == NULL || (pChg->getDatabase() == ChangeStateGroupDb) )
    {
        ReloadStateNames();
    }

    if(pChg == NULL || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_DEVICE) || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_ROUTE) )
    {
        try
        {
            start = start.now();

            LONG chgid = 0;
            string catstr;
            string devstr;

            if(pChg)
            {
                chgid = pChg->getId();
                catstr = pChg->getCategory();
                devstr = pChg->getObjectType();
            }

            ScannerDeviceManager.refresh(DeviceFactory, isNotAScannableDevice, NULL, chgid, catstr, devstr);

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
            CtiDeviceSPtr pBase = ScannerDeviceManager.getEqual(pChg->getId());

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
            attachPointManagerToDevices(&ScannerDeviceManager, &ScannerPointManager);
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

    if(pChg != NULL && pChg->getDatabase() == ChangePointDb)  // On a point specific message only!
    {
        if( (pChg->getTypeOfChange() == ChangeTypeAdd) || (pChg->getTypeOfChange() == ChangeTypeUpdate) )
        {
            ScannerPointManager.refreshList(isPoint, NULL, pChg->getId(), 0);

            LONG paoDeviceID = ScannerPointManager.getPAOIdForPointId(pChg->getId());

            if( paoDeviceID >= 0 )
            {
                CtiDeviceSPtr pBase = ScannerDeviceManager.getEqual(paoDeviceID);

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
            ScannerPointManager.orphan(pChg->getId());
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

void DispatchMsgHandlerThread(VOID *Arg)
{
    BOOL           bServerClosing = FALSE;

    CtiTime         TimeNow;
    CtiTime         LastTime;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << TimeNow << " DispatchMsgHandlerThread started as TID " << rwThreadId() << endl;
    }

    /* perform the wait loop forever */
    for( ; !ScannerQuit ; )
    {
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

                                    if( (device = ScannerDeviceManager.getEqual( deviceId )) )
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

void DatabaseHandlerThread(VOID *Arg)
{
    BOOL           bServerClosing = FALSE;

    CtiTime         TimeNow;
    CtiTime         RefreshTime = nextScheduledTimeAlignedOnRate( TimeNow, ScannerReloadRate );
    ULONG          delta;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << TimeNow << " DatabaseHandlerThread started as TID " << rwThreadId() << endl;
    }

    /* perform the wait loop forever */
    for( ; !ScannerQuit ; )
    {
        TimeNow = TimeNow.now();

        delta = (RefreshTime.seconds() - TimeNow.seconds()) * 1000;

        if(delta > 900000)      // Every 15 minutes, I want to write the DynamicScanData.
        {
            delta = 900000;
        }

        if( WAIT_OBJECT_0 == WaitForSingleObject(hScannerSyncs[S_QUIT_EVENT], delta) )
        {
            ScannerQuit = TRUE;
        }
        else
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
            //  if queueing has been turned off
            if( !CCUQueue ||
                (!CCUQueueScans && ((OutMessage->Sequence == Cti::Protocol::Emetcon::Scan_Accum)   ||
                                    (OutMessage->Sequence == Cti::Protocol::Emetcon::Scan_General) ||
                                    (OutMessage->Sequence == Cti::Protocol::Emetcon::Scan_Integrity))) )
            {
                string cmdStr(OutMessage->Request.CommandStr);

                if( findStringIgnoreCase(cmdStr, "noqueue") )  // Make sure we don't stuff it on there multiple times!
                {
                    _snprintf(OutMessage->Request.CommandStr, 254, "%s noqueue", cmdStr.c_str());
                }
            }

            if(ScannerDebugLevel & SCANNER_DEBUG_OUTREQUESTS)
            {
                if(ScannerDeviceManager.RemoteGetEqual(OutMessage->TargetID))
                {
                    string name = ScannerDeviceManager.RemoteGetEqual(OutMessage->TargetID)->getName();
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

        /*
         *  We will be going with the idea that a minimal duration locking of the Manager is KEY KEY KEY here.
         */
        ScannerDeviceManager.apply(applyGenerateScannerDataRows, (void*)&dirtyData);

        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        if(conn.isValid())
        {
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
    }

    return status;
}
