
#pragma warning( disable : 4786 )
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   scanner
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SCANNER/scanner.cpp-arc  $
* REVISION     :  $Revision: 1.19 $
* DATE         :  $Date: 2002/08/16 13:06:34 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#pragma title ( "Scanner Process" )
#pragma subtitle ( "CTI Copyright (c) 1990-1998" )
/*----------------------------------------------------------------------------
    Copyright (c) 1990-2000 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        SCANNER.CPP

    Purpose:
        To scan various devices on the system for value and status

    The following procedures are contained in this module:
        main                    ResultThread
        FirstScan               ReportError
        ScannerCleanUp


    Initial Date:
        Unknown

    Revision History:

   --------------------------------------------------------------------------- */

#include <windows.h>       // These next few are required for Win32
#include <process.h>

#include <rw/toolpro/winsock.h>
#include <rw\cstring.h>

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
// Here are the devices for which scannables have been defined

#include "scanglob.h"
#include "scansup.h"

#include "rtdb.h"
#include "mgr_device.h"
#include "dev_base.h"
#include "dev_single.h"
#include "dev_mct.h"  //  for DLC loadprofile scans

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

#include "dll_msg.h"
#include "utility.h"

// #define DEBUG2 TRUE
#define NEXT_SCAN       0
#define REMOTE_SCAN     1
#define DLC_LP_SCAN     2
#define MAX_SCAN_TYPE   3

static INT     SCANNER_RELOAD_RATE = 900;
static RWTime  LastPorterOutTime;
static RWTime  LastPorterInTime;

const RWTime   MAXTime(YUKONEOT);

void  LoadScannableDevices(void *ptr = NULL);
void  DispatchMsgHandlerThread(VOID *Arg);
void  DatabaseHandlerThread(VOID *Arg);

/* Local Declarations... Utility Functions */

RWTime  TimeOfNextRemoteScan(void);
RWTime  TimeOfNextLPScan(void);

INT RecordDynamicData();
void    InitScannerGlobals(void);
void    DumpRevision(void);
INT     MakePorterRequests(RWTPtrSlist< OUTMESS > &outList);
INT     ReinitializeRemotes(RWTime NextScan[]);

CtiDeviceManager      ScannerDeviceManager;

static RWWinSockInfo  winsock;

extern BOOL ScannerQuit;

CtiConnection     VanGoghConnection;
ULONG             ScannerDebugLevel = 0;
int               CCUNoQueueScans = TRUE;

HANDLE hLockArray[] = {
    hScannerSyncs[S_QUIT_EVENT],
    hScannerSyncs[S_LOCK_MUTEX]
};

void barkAboutCurrentTime(CtiDevice *Device, RWTime &rt, INT line)
{
    if(ScannerDebugLevel & SCANNER_DEBUG_NEXTSCAN)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Next scan is for " << Device->getName() << " at " << rt << " on " << line << endl;
    }
}

void applyUseScanFlags(const CtiHashKey *unusedKey, CtiDevice *&DeviceRecord, void *unusedPtr)
{
    if(DeviceRecord->isSingle())
    {
        ((CtiDeviceSingle*)DeviceRecord)->setUseScanFlags();                      // Mark them as being scanned by this application.
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
    CtiDevice         *Device       = NULL;
    CtiDeviceSingle   *DeviceRecord = NULL;

    RWTime      NextScan[MAX_SCAN_TYPE];
    RWTime      TimeNow;

    /* Define for the porter interface */
    IM_EX_CTIBASE extern USHORT   PrintLogEvent;

    RWTPtrSlist< OUTMESS >         outList;         // Nice little collection of OUTMESS's


    int Op, k;

    HANDLE hScanArray[] = {
        hScannerSyncs[S_QUIT_EVENT],
        hScannerSyncs[S_SCAN_EVENT]
    };

    identifyProject(CompileInfo);

    sprintf(tstr,"Scanner - YUKON (Build %d.%d.%d)", CompileInfo.major, CompileInfo.minor, CompileInfo.build);

    SetConsoleTitle( tstr );

    /* Give us a tiny attitude */
    CTISetPriority(PRTYS_THREAD, PRTYC_TIMECRITICAL, 0, 0);

    /* check for various flags */
    if(argc > 1)
    {
        for(i = 1; i < argc; i++)
        {
            if(!(stricmp (argv[i], "/L")))
            {
                PrintLogEvent = FALSE;
                LogFlag = FALSE;
                continue;
            }

            if(!(stricmp (argv[i], "/NQ")))
            {
                CCUNoQueue = TRUE;
                continue;
            }

            if(!(stricmp (argv[i], "/NLP")))
            {
                SuspendLoadProfile = TRUE;
                continue;
            }
        }
    }

    /* open the port pipe */
    PortPipeInit (NOWAIT);

    do
    {
        LoadScannableDevices();

        for(i = 0; i < MAX_SCAN_TYPE; i++)
        {
            NextScan[i] = MAXTime;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " There are " << ScannerDeviceManager.getMap().entries() << " scannables in my list" << endl;
        }


        {
            RWRecursiveLock<RWMutexLock>::LockGuard guard(ScannerDeviceManager.getMux());

            CtiRTDB<CtiDeviceBase>::CtiRTDBIterator   itr_dev(ScannerDeviceManager.getMap());
            for(; ++itr_dev ;)
            {
                Device = (CtiDevice*)itr_dev.value();

                if( Device->isSingle() )
                {
                    DeviceRecord = (CtiDeviceSingle*)Device;
                    DeviceRecord->doDeviceInit();       // Set the next scan times...
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << "): Non-scannable in scanner" << endl;
                    }
                }
            }
        }

        NextScan[REMOTE_SCAN] = TimeOfNextRemoteScan();

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
                dout << RWTime() << " Nothing to Scan.. Sleeping 30 seconds" << endl;
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
        VanGoghConnection.WriteConnQue(new CtiRegistrationMsg(SCANNER_REGISTRATION_NAME, rwThreadId(), TRUE));

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
            dout << RWTime() << " Init Complete. Entering Main Scan Loop" << endl;
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
            sprintf(tstr, "At Start of Loop -- Will Sleep %d Seconds", NextScan[NEXT_SCAN].seconds() - TimeNow.seconds()  );

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << TimeNow << " " << tstr << endl;
            }

            ObjWait = (NextScan[NEXT_SCAN].seconds() - TimeNow.seconds()) * 1000L;

            dwWait = WaitForMultipleObjects(2, hScanArray, FALSE, ObjWait);

            switch(dwWait)
            {
            case WAIT_OBJECT_0:                                    // This is a quit request!
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Quit Event Posted" << endl << endl;
                    }
                    ScannerQuit = TRUE;
                    continue;            // the main for loop I hope.
                }
            case WAIT_OBJECT_0 + 1:                                // This is a quit request!
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Scan Event Set. Will examine devices now.  " << endl;
                    }
                    ResetEvent(hScannerSyncs[ S_SCAN_EVENT ]);

                    NextScan[REMOTE_SCAN] = TimeOfNextRemoteScan();

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
        }

        /* Wait/Block on the return thread if neccessary */
        dwWait = WaitForMultipleObjects(2, hLockArray, FALSE, INFINITE);

        switch(dwWait - WAIT_OBJECT_0)
        {
        case 0:  // Quit
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Quit Event Posted" << endl << endl;
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

                if(!(PortPipeInit (NOWAIT)))
                {
                    // Make sure we don't hang up on him again.
                    LastPorterOutTime = rwEpoch;
                    LastPorterInTime  = LastPorterInTime.now();

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Reconnected to Port Control" << endl;
                    }

                    /* now walk through the scannable "Remote" devices */
                    RWRecursiveLock<RWMutexLock>::LockGuard  dev_guard(ScannerDeviceManager.getMux());       // Protect our iteration!

                    if(ScannerDeviceManager.getMap().entries())
                    {
                        CtiRTDB<CtiDeviceBase>::CtiRTDBIterator   itr_dev(ScannerDeviceManager.getMap());

                        for(; ++itr_dev ;)
                        {
                            Device = (CtiDevice*) itr_dev.value();

                            if( Device->isSingle() )
                            {
                                DeviceRecord = (CtiDeviceSingle*) Device;

                                if(DeviceRecord->isScanFreezePending())
                                {
                                    DeviceRecord->resetScanFreezePending();;
                                    DeviceRecord->setScanFreezeFailed();
                                }
                                else if(DeviceRecord->isScanPending())
                                {
                                    DeviceRecord->resetScanPending();
                                }
                                else if(DeviceRecord->isScanResetting())
                                {
                                    DeviceRecord->resetScanResetting();
                                    DeviceRecord->setScanResetFailed();
                                }
                            }
                        }
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
                        dout << RWTime() << " Quit Event Posted" << endl << endl;
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

        if(outList.entries() > 0)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** ERROR!!!! Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  There should be only NONE!" << endl;
            }
        }

        /* Use the same time base for the full scan check */
        TimeNow = TimeNow.now();   // update the time...

        /* Check if we do remote scanning */
        if(TimeNow >= NextScan[REMOTE_SCAN])
        {
            {
                RWRecursiveLock<RWMutexLock>::LockGuard guard(ScannerDeviceManager.getMux());
                CtiRTDB<CtiDeviceBase>::CtiRTDBIterator itr(ScannerDeviceManager.getMap());

                for( ; ++itr ; )
                {
                    CtiDeviceBase *pBase = (CtiDeviceBase *)itr.value();

                    if(ScannerDebugLevel & 0x00000020)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Looking at " << pBase->getName() << endl;
                    }

                    if(pBase->isSingle())
                    {
                        DeviceRecord = (CtiDeviceSingle*)pBase;

                        /*
                         *  ACCUMULATOR SCANNING
                         */

                        if(DeviceRecord->getNextScan(ScanRateAccum) <= TimeNow)
                        {
                            if(ScannerDebugLevel & 0x00000002)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Accumulator Scan Checkpoint **** " << DeviceRecord->getID() << " / " <<  DeviceRecord->getName() << endl;

                            }
                            DeviceRecord->resetScanException();   // Results should be forced though the exception system
                            nRet = DeviceRecord->initiateAccumulatorScan(outList);
                        }

                        /*
                         *  INTEGRITY SCANNING
                         */

                        if(DeviceRecord->getNextScan(ScanRateIntegrity) <= TimeNow)
                        {
                            if(ScannerDebugLevel & 0x00000004)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Integrity Scan Checkpoint **** " << DeviceRecord->getID() << " / " <<  DeviceRecord->getName() << endl;

                            }
                            DeviceRecord->resetScanException();   // Results should be forced though the exception system
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
                                dout << RWTime() << " General Scan Fail to Device: " << DeviceRecord->getName() << ". Error " << nRet << ": " << GetError(nRet) << endl;
                            }
                            else
                            {
                                if(ScannerDebugLevel & 0x00000008)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Exception/General Checkpoint ****   " << DeviceRecord->getID() << " / " <<  DeviceRecord->getName() << endl;
                                }
                                DeviceRecord->setScanException();   // Results need NOT be forced though the exception system
                            }
                        }

                        // Make sure times/rates haven't changed on us...
                        DeviceRecord->validateScanTimes();

                        // expire any alternate rates we may have before calculating next scan
                        DeviceRecord->checkSignaledAlternateRateForExpiration();
                    }
                }
            }

            // Send any requests over to porter for processing
            MakePorterRequests(outList);

            NextScan[REMOTE_SCAN] = TimeOfNextRemoteScan();
        }

        //  check if we need to do any DLC load profile scans
        if(!SuspendLoadProfile && TimeNow >= NextScan[DLC_LP_SCAN])
        {
            //  DLC load profile scans
            {
                RWRecursiveLock<RWMutexLock>::LockGuard guard(ScannerDeviceManager.getMux());
                CtiRTDB<CtiDeviceBase>::CtiRTDBIterator itr(ScannerDeviceManager.getMap());

                for( ; ++itr ; )
                {
                    CtiDeviceBase *pBase = (CtiDeviceBase *)itr.value();

                    if(ScannerDebugLevel & 0x00000020)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Looking at " << pBase->getName() << endl;
                    }

                    //  only MCTs do DLC load profile scans
                    if(isCarrierLPDevice(pBase))
                    {
                        CtiDeviceMCT *pMCT = (CtiDeviceMCT *)itr.value();

                        if(pMCT->getNextLPScanTime() <= TimeNow)
                        {
                            if((nRet = pMCT->initiateLoadProfileScan(outList)) > 0)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Load Profile Scan Fail to Device: " << DeviceRecord->getName() << endl;
                                dout << "\tError " << nRet << ": " << GetError(nRet) << endl;
                            }
                            else
                            {
                                if(ScannerDebugLevel & 0x00000008)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Load Profile Checkpoint ****   " << DeviceRecord->getID() << " / " <<  DeviceRecord->getName() << endl;
                                }
                            }
                        }
                    }
                }
            }

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
                dout << LastPorterOutTime << " > " << LastPorterInTime << " + 300 seconds? " << endl;
            }

            LastPorterOutTime = rwEpoch;  // Make sure we don't repeat this forever!

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
    CtiDevice         *pBase;
    CtiDeviceSingle   *DeviceRecord;

    /* Define the various time variable */
    RWTime      TimeNow;

    /* Define the pipe variables */
    ULONG       BytesRead;
    INMESS      InMessage;

    HANDLE      evShutdown;

    RWTPtrSlist< OUTMESS > outList;
    RWTPtrSlist< CtiMessage > retList;
    RWTPtrSlist< CtiMessage > vgList;


    // I want an attitude!
    CTISetPriority (PRTYS_THREAD, PRTYC_TIMECRITICAL, 30, 0);

    int TracePrint (PBYTE, INT);

    InMessage.DeviceID = SCANNER_DEVID;

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
        // CTIReleaseMutexSem (LockSem);
        ReleaseMutex(hScannerSyncs[S_LOCK_MUTEX]);

        /* Wait for the Nexus to come (?back?) up */
        while(PorterNexus.NexusState == CTINEXUS_STATE_NULL && !ScannerQuit)
        {
            if(!(i++ % 30))
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " ResultThread: Waiting for reconnection to Port Control" << endl;
            }
            CTISleep (1000L);
        }

        if(ScannerQuit)
        {
            continue; // get us out of here!
        }

        BytesRead = 0;
        memset(&InMessage, 0, sizeof(InMessage));

        /* get a result off the port pipe */
        if(PorterNexus.CTINexusRead (&InMessage, sizeof(InMessage), &BytesRead, CTINEXUS_INFINITE_TIMEOUT) || BytesRead < sizeof(InMessage))     // Make sure we have an InMessage worth!
        {
            if(PorterNexus.NexusState != CTINEXUS_STATE_NULL)
            {
                PorterNexus.CTINexusClose();
            }
            continue;
        }

        /* Wait on the request thread if neccessary */
        dwWait = WaitForMultipleObjects(2, hLockArray, FALSE, INFINITE);

        switch(dwWait - WAIT_OBJECT_0)
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

            RWRecursiveLock<RWMutexLock>::LockGuard guard(ScannerDeviceManager.getMux());

            // Find the device..

            // DeviceRecord = (CtiDeviceSingle*)ScannerDeviceManager.RemoteGetEqual(InMessage.DeviceID);

            LONG id = InMessage.TargetID;

            if(id == 0)
            {
                id = InMessage.DeviceID;
            }

            pBase = (CtiDevice*)ScannerDeviceManager.RemoteGetEqual(id);

            if(ScannerDebugLevel & SCANNER_DEBUG_INREPLYS)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " InMessage from " << pBase->getName() << " " << FormatError(InMessage.EventCode & 0x3fff) << endl;
            }

            if(pBase != NULL && pBase->isSingle())
            {
                DeviceRecord = (CtiDeviceSingle*)pBase;

                /* get the time for use in the decodes */
                TimeNow = RWTime();

                // Do some device dependent work on this Inbound message!
                DeviceRecord->ProcessResult(&InMessage, TimeNow, vgList, retList, outList);

                // Send any new porter requests to porter
                if((ScannerDebugLevel & 0x00000080) && outList.entries() > 0)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                MakePorterRequests(outList);

                // Write any results generated back to VanGogh
                while(retList.entries())
                {
                    VanGoghConnection.WriteConnQue(retList.get());   // I no longer manage this, the queue cleans up!
                }

                // Write any signals or misc. messages back to VanGogh!
                while(vgList.entries())
                {
                    VanGoghConnection.WriteConnQue((CtiMessage*)vgList.get());   // I no longer manage this, the queue cleans up!
                }

                /* Check if we should kick other thread in the pants */
                if(DeviceRecord->getScanRate(ScanRateGeneral) == 0)
                {
                    // FIX FIX FIX This needs a new IPC with PORTER.. No DB connection anymore!
                    DeviceRecord->setNextScan(ScanRateGeneral, TimeNow.now());
                    SetEvent(hScannerSyncs[S_SCAN_EVENT]);
                }
            }
            else if(pBase != NULL && !pBase->isSingle())
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
                dout << " We just read " << BytesRead << " bytes from the scanner/porter nexus" << endl;
            }
        }
    } /* End of for */
}


VOID ScannerCleanUp ()
{
    ScannerQuit = TRUE;

    ScannerDeviceManager.DeleteList();
    VanGoghConnection.WriteConnQue(new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15));
    VanGoghConnection.ShutdownConnection();

    PortPipeCleanup(0);

    dout << RWTime() << " Scanner terminated!" << endl;

}


RWTime TimeOfNextRemoteScan()
{
    RWTime            nRet(YUKONEOT);
    RWTime            TempTime(YUKONEOT);
    RWTime            TimeNow;
    CtiDeviceSingle   *DeviceRecord;
    CtiDevice         *Device;

    RWRecursiveLock<RWMutexLock>::LockGuard  dm_guard(ScannerDeviceManager.getMux());       // Protect our iteration!
    CtiRTDB<CtiDeviceBase>::CtiRTDBIterator itr(ScannerDeviceManager.getMap());

    for( ; ++itr ; )
    {
        Device = (CtiDeviceSingle*)itr.value();
        RWRecursiveLock<RWMutexLock>::LockGuard  dev_guard(Device->getMux());       // Protect our device!

        if(Device->isSingle())
        {
            DeviceRecord = (CtiDeviceSingle*)Device;

            if(!(DeviceRecord->isInhibited()) && (DeviceRecord->isScanWindowOpen()))
            {
                if( DeviceRecord->getNextScan(ScanRateGeneral)     < TimeNow ||
                    DeviceRecord->getNextScan(ScanRateAccum)       < TimeNow ||
                    DeviceRecord->getNextScan(ScanRateIntegrity)   < TimeNow)
                {
                    if(!(DeviceRecord->isScanPending()) && !(DeviceRecord->isScanFreezePending()) && !(DeviceRecord->isScanResetting()))
                    {
                        nRet = TimeNow;
                        barkAboutCurrentTime( DeviceRecord, TimeNow, __LINE__ );
                        break;
                    }
                }

                TempTime = DeviceRecord->nextRemoteScan();

                if(nRet > TempTime)
                {
                    nRet = TempTime;
                    barkAboutCurrentTime( DeviceRecord, TempTime, __LINE__ );
                }
            }
        }
    }

    /* Do not let this get out of hand, check once a minute if nothing else is looking */
    if(nRet == MAXTime || (nRet.seconds() - TimeNow.seconds()) > 60L)
    {
        TimeNow = TimeNow.now();
        nRet = TimeNow.seconds() - (TimeNow.seconds() % 60) + 60;
    }

    return nRet;
}


RWTime TimeOfNextLPScan( void )
{
    RWTime         nRet(YUKONEOT);
    RWTime         TempTime(YUKONEOT);
    RWTime         TimeNow;
    CtiDeviceMCT  *DeviceRecord;
    CtiDevice     *Device;

    RWRecursiveLock<RWMutexLock>::LockGuard dm_guard(ScannerDeviceManager.getMux());       // Protect our iteration!
    CtiRTDB<CtiDeviceBase>::CtiRTDBIterator itr(ScannerDeviceManager.getMap());

    for( ; ++itr ; )
    {
        Device = (CtiDeviceBase *)itr.value();
        RWRecursiveLock<RWMutexLock>::LockGuard dev_guard(Device->getMux());       // Protect our device!

        if(isCarrierLPDevice(Device))
        {
            DeviceRecord = (CtiDeviceMCT *)Device;

            if(!(DeviceRecord->isInhibited()) && (DeviceRecord->isScanWindowOpen()))
            {
                TempTime = DeviceRecord->calcNextLPScanTime();

                if(nRet > TempTime)
                {
                    nRet = TempTime;
                    barkAboutCurrentTime( DeviceRecord, TempTime, __LINE__ );
                }
            }
        }
    }

    /* Do not let this get out of hand, check once a minute if nothing else is looking */
    if(nRet == MAXTime || (nRet.seconds() - TimeNow.seconds()) > 60L)
    {
        TimeNow = TimeNow.now();
        nRet = TimeNow.seconds() - (TimeNow.seconds() % 60) + 60;
    }

    return nRet;
}


void InitScannerGlobals(void)
{
    if(gConfigParms.isOpt("SCANNER_RELOAD_RATE"))
    {
        RWCString Temp = gConfigParms.getValueAsString("SCANNER_RELOAD_RATE");
        SCANNER_RELOAD_RATE = atoi (Temp.data());
    }

    if(gConfigParms.isOpt("SCANNER_DEBUGLEVEL"))
    {
        RWCString str = gConfigParms.getValueAsString("SCANNER_DEBUGLEVEL");
        char *eptr;
        ScannerDebugLevel = strtoul(str.data(), &eptr, 16);
        if(ScannerDebugLevel & 0x00000001)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Scanner DEBUGLEVEL " << hex << ScannerDebugLevel << dec << endl;
        }
    }

    if(gConfigParms.isOpt("SCANNER_STATUS_PRIORITY"))
    {
        RWCString Temp = gConfigParms.getValueAsString("SCANNER_STATUS_PRIORITY");
        DLCStatusPriority = atoi (Temp.data());

        if(DLCStatusPriority < 1 || DLCStatusPriority > MAXPRIORITY)
        {
            /* invalid alternat priority set back to normal value */
            DLCStatusPriority = PRIORITY_STATUS;
        }
        else
        {
            printf ("Using %d as alternate DLC queue priority for Status points.\n", DLCStatusPriority);
        }
    }

    if(gConfigParms.isOpt("SCANNER_VALUE_PRIORITY"))
    {
        RWCString Temp = gConfigParms.getValueAsString("SCANNER_VALUE_PRIORITY");
        DLCValuePriority = atoi (Temp.data());

        if(DLCValuePriority < 1 || DLCValuePriority > MAXPRIORITY)
        {
            /* invalid alternate priority set back to normal value */
            DLCValuePriority = PRIORITY_VALUE;
        }
        else
        {
            printf ("Using %d as alternate DLC queue priority for Analog/PA points.\n", DLCValuePriority);
        }
    }

    if(gConfigParms.isOpt("SCANNER_QUEUE"))
    {
        RWCString Temp = gConfigParms.getValueAsString("SCANNER_QUEUE");
        Temp.toLower();

        if(Temp == "true" || Temp == "yes")
        {
            CCUNoQueue = FALSE;
        }
        else
        {
            CCUNoQueue = TRUE;
        }
    }

    if(gConfigParms.isOpt("SCANNER_QUEUE_SCANS"))
    {
        RWCString Temp = gConfigParms.getValueAsString("SCANNER_QUEUE_SCANS");
        Temp.toLower();

        if(Temp == "true" || Temp == "yes")
        {
            CCUNoQueueScans = FALSE;
        }
        else
        {
            CCUNoQueueScans = TRUE;
        }
    }

    if(gConfigParms.isOpt("SCANNER_OUTPUT_INTERVAL"))
    {
        RWCString Temp = gConfigParms.getValueAsString("SCANNER_OUTPUT_INTERVAL");
        dout.setWriteInterval(atoi(Temp.data()) * 1000);
    }
}

void LoadScannableDevices(void *ptr)
{
    static bool bLoaded = false;        // Useful for debugging memory leaks.. Set to true inside the if.
    CtiHashKey  *hKey = NULL;
    CtiDevice   *DeviceRecord = NULL;
    CtiDBChangeMsg *pChg = (CtiDBChangeMsg *)ptr;

    bool bforce = ((pChg == NULL) ? false : true);

    RWTime start;
    RWTime stop;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Starting LoadScannableDevices. " << (bforce ? "Due to DBChange." : "Not due to DBChange." ) << endl;
    }

    InitScannerGlobals();      // Go fetch from the environmant

    RWRecursiveLock<RWMutexLock>::LockGuard  dev_guard(ScannerDeviceManager.getMux());

    if(pChg == NULL || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_DEVICE) || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_ROUTE) )
    {
        try
        {
            if(ScannerDebugLevel & SCANNER_DEBUG_DBRELOAD) start = start.now();
            if(pChg && (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_DEVICE))
            {
                DeviceRecord = ScannerDeviceManager.getEqual(pChg->getId());
                if(DeviceRecord)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Reloading " << DeviceRecord->getName() << endl;
                    }
                }
                ScannerDeviceManager.RefreshList(pChg->getId());
            }
            else
            {
                ScannerDeviceManager.RefreshList();
            }

            if(ScannerDebugLevel & SCANNER_DEBUG_DBRELOAD)
            {
                stop = stop.now();
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << RWTime() << " RefreshList took " << stop.seconds() - start.seconds() << endl;
                }
            }

            if(ScannerDebugLevel & SCANNER_DEBUG_DBRELOAD) start = start.now();

            if(pChg && (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_DEVICE))
            {
                ScannerDeviceManager.RefreshScanRates(pChg->getId());
            }
            else
            {
                ScannerDeviceManager.RefreshScanRates();
            }

            if(ScannerDebugLevel & SCANNER_DEBUG_DBRELOAD)
            {
                stop = stop.now();
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << RWTime() << " RefreshScanRates took " << stop.seconds() - start.seconds() << endl;
                }
            }

            if(ScannerDebugLevel & SCANNER_DEBUG_DBRELOAD) start = start.now();
            // Limit this list to just scannable devices!
            // Only do this if we DID NOT have a DBCHANGE message!
            // We will break out of the while at the correct point!
            while(pChg == NULL)
            {
                CtiDeviceManager::val_pair vt = ScannerDeviceManager.getMap().find(isNotScannable, NULL);

                if(vt.first == NULL)
                {
                    break;
                }

                hKey = vt.first;
                DeviceRecord = vt.second;

                try
                {
                    ScannerDeviceManager.getMap().remove( hKey );
                }
                catch( ... )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    break;
                }

                try
                {
                    if(hKey != NULL)
                    {
                        delete hKey;
                        hKey = NULL;
                    }

                    if(DeviceRecord != NULL)
                    {
                        delete DeviceRecord;
                        DeviceRecord = NULL;
                    }
                }
                catch( ... )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    break;
                }
            }
            if(ScannerDebugLevel & SCANNER_DEBUG_DBRELOAD)
            {
                stop = stop.now();
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << RWTime() << " Removing NONSCANABLES took " << stop.seconds() - start.seconds() << endl;
                }
            }

            if(ScannerDebugLevel & SCANNER_DEBUG_DBRELOAD) start = start.now();
            if(pChg && (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_DEVICE))
            {
                ScannerDeviceManager.RefreshRoutes(pChg->getId());  // Get the devices which have routes into memory?
            }
            else
            {
                ScannerDeviceManager.RefreshRoutes();  // Get the devices which have routes into memory?
            }

            if(ScannerDebugLevel & SCANNER_DEBUG_DBRELOAD)
            {
                stop = stop.now();
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << RWTime() << " RefreshRoutes took " << stop.seconds() - start.seconds() << endl;
                }
            }

            if(ScannerDebugLevel & SCANNER_DEBUG_DBRELOAD) start = start.now();
            ScannerDeviceManager.apply( applyUseScanFlags, NULL );
            if(ScannerDebugLevel & SCANNER_DEBUG_DBRELOAD)
            {
                stop = stop.now();
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << RWTime() << " apply( applyUseScanFlags, NULL ) took " << stop.seconds() - start.seconds() << endl;
                }
            }

            // load the scan window list if there is one
            if(ScannerDebugLevel & SCANNER_DEBUG_DBRELOAD) start = start.now();
            if(pChg && (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_DEVICE))
            {
                ScannerDeviceManager.RefreshDeviceWindows(pChg->getId());
            }
            else
            {
                ScannerDeviceManager.RefreshDeviceWindows();
            }
            if(ScannerDebugLevel & SCANNER_DEBUG_DBRELOAD)
            {
               stop = stop.now();
               {
                  CtiLockGuard<CtiLogger> doubt_guard(dout);
                  dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                  dout << RWTime() << " RefreshDeviceWindows took " << stop.seconds() - start.seconds() << endl;
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
    if(ScannerDebugLevel & SCANNER_DEBUG_DBRELOAD) start = start.now();
    if(pChg == NULL || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_DEVICE) )
    {
        if(pChg)
        {
            CtiDeviceBase *pBase = ScannerDeviceManager.getEqual(pChg->getId());

            if(pBase)
            {
                RWRecursiveLock<RWMutexLock>::LockGuard devguard(pBase->getMux());

                if(pBase->isSingle())
                {
                    CtiDeviceSingle* DeviceRecord = (CtiDeviceSingle*)pBase;
                    DeviceRecord->validateScanTimes(bforce);
                }
            }
        }
        else
        {
            CtiRTDB<CtiDeviceBase>::CtiRTDBIterator   itr_dev(ScannerDeviceManager.getMap());
            for(; ++itr_dev ;)
            {
                CtiDeviceBase *pBase = (CtiDeviceBase *)itr_dev.value();
                RWRecursiveLock<RWMutexLock>::LockGuard devguard(pBase->getMux());

                if(pBase->isSingle())
                {
                    CtiDeviceSingle* DeviceRecord = (CtiDeviceSingle*)pBase;
                    DeviceRecord->validateScanTimes(bforce);
                }
            }
        }
    }

    if(ScannerDebugLevel & SCANNER_DEBUG_DBRELOAD)
    {
        stop = stop.now();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << RWTime() << " validateScanTimes took " << stop.seconds() - start.seconds() << endl;
        }
    }

    if(pChg != NULL && pChg->getDatabase() == ChangePointDb)  // On a point specific message only!
    {
        CtiRTDB<CtiDeviceBase>::CtiRTDBIterator itr_dev(ScannerDeviceManager.getMap());
        for(; ++itr_dev ;)
        {
            CtiDeviceBase *pBase = (CtiDeviceBase *)itr_dev.value();
            RWRecursiveLock<RWMutexLock>::LockGuard devguard(pBase->getMux());

            if(pBase->isSingle())
            {
                CtiDeviceSingle *DeviceRecord = (CtiDeviceSingle*)pBase;

                if( (pChg->getTypeOfChange() == ChangeTypeAdd) ||
                    (pChg->getTypeOfChange() == ChangeTypeUpdate && DeviceRecord->getDevicePointEqual(pChg->getId())))        // This device has this ID... Cool, blow it away and break out of the loop.
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " DBChange forced a reload of points for " << pBase->getName() << endl;
                    }
                    pBase->RefreshDevicePoints();
                    break;
                }
            }
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
        dout << RWTime() << " Done LoadScannableDevices." << endl;
    }

    return;
}

void DispatchMsgHandlerThread(VOID *Arg)
{
    BOOL           bServerClosing = FALSE;

    RWTime         TimeNow;

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
                            SetEvent(hScannerSyncs[S_QUIT_EVENT]);
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Dispatch just told scanner to shutdown" << endl;
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
                            if(Cmd->getOpArgList().entries() >= 4)
                            {
                               LONG token       = Cmd->getOpArgList().at(0);
                               LONG deviceId    = Cmd->getOpArgList().at(1);
                               LONG open       = Cmd->getOpArgList().at(2);
                               LONG duration   = Cmd->getOpArgList().at(3);

                               {
                                  RWRecursiveLock<RWMutexLock>::LockGuard guard(ScannerDeviceManager.getMux());
                                  CtiDeviceBase     *device = NULL;
                                  CtiDeviceSingle   *deviceSingle = NULL;

                                  CtiHashKey key(deviceId);
                                  if( ScannerDeviceManager.getMap().entries() > 0 &&
                                      ((device = ScannerDeviceManager.getMap().findValue(&key)) != NULL) )
                                  {
                                      if( device->isSingle() )
                                      {
                                          CtiDeviceSingle *DeviceRecord = (CtiDeviceSingle*)device;
                                          DeviceRecord->applySignaledRateChange(open,duration);
                                          DeviceRecord->validateScanTimes(true);
                                          DeviceRecord->checkSignaledAlternateRateForExpiration();
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
                    dout << RWTime() << " Scanner is absorbing an unhandled message " <<
                    MsgPtr->isA() << " from VanGogh " << endl;
                    break;
                }
            }

            delete MsgPtr;
        }
    } /* End of for */
}

void DatabaseHandlerThread(VOID *Arg)
{
    BOOL           bServerClosing = FALSE;

    RWTime         TimeNow;
    RWTime         RefreshTime = TimeNow - (TimeNow.seconds() % SCANNER_RELOAD_RATE) + SCANNER_RELOAD_RATE;
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

            if(TimeNow >= RefreshTime)
            {
                // Refresh the scanner in memory database once every 5 minutes.
                LoadScannableDevices();
                // Post the wakup to ensure that the main loop re-examines the devices.
                SetEvent(hScannerSyncs[ S_SCAN_EVENT ]);

                RefreshTime = TimeNow - (TimeNow.seconds() % SCANNER_RELOAD_RATE) + SCANNER_RELOAD_RATE;
            }
        }
    } /* End of for */
}

INT MakePorterRequests(RWTPtrSlist< OUTMESS > &outList)
{
    INT   i;
    INT   status = NORMAL;
    ULONG BytesWritten;

    OUTMESS *OutMessage = NULL;

    IM_EX_CTIBASE extern CTINEXUS PorterNexus;

    /* if pipe shut down return the error */
    if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
    {
        status = PIPEWASBROKEN;
    }

    for( i = outList.entries() ; status == NORMAL && i > 0; i-- )
    {
        OutMessage = outList.get();

        /* And send them to porter */
        if(PorterNexus.NexusState != CTINEXUS_STATE_NULL)
        {
            //  if queueing has been turned off
            if( CCUNoQueue ||
                (CCUNoQueueScans && ((OutMessage->Sequence == CtiProtocolEmetcon::Scan_Accum)   ||
                                     (OutMessage->Sequence == CtiProtocolEmetcon::Scan_General) ||
                                     (OutMessage->Sequence == CtiProtocolEmetcon::Scan_Integrity))) )
            {
                RWCString cmdStr(OutMessage->Request.CommandStr);

                if( cmdStr.subString("noqueue",0,RWCString::ignoreCase).isNull() )  // Make sure we don't stuff it on there multiple times!
                {
                    _snprintf(OutMessage->Request.CommandStr, 254, "%s noqueue", cmdStr.data());
                }
            }

            if(ScannerDebugLevel & SCANNER_DEBUG_OUTREQUESTS)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " OutMessage to " << ScannerDeviceManager.RemoteGetEqual(OutMessage->TargetID)->getName() << endl;
            }

            if(PorterNexus.CTINexusWrite (OutMessage, sizeof(OUTMESS), &BytesWritten, 30L) || BytesWritten == 0)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                PorterNexus.CTINexusClose();
                status = PIPEWASBROKEN;
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
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        // Message is re-built on the other side, so clean it up!
        delete OutMessage;
    }

    if(outList.entries()) outList.clearAndDestroy();

    return status;
}

INT ReinitializeRemotes(RWTime NextScan[])
{
    INT status = NORMAL;
    CtiDevice *Device       = NULL;
    CtiDeviceSingle *DeviceRecord = NULL;

    /* now walk through the scannable "Remote" devices */
    RWRecursiveLock<RWMutexLock>::LockGuard  dev_guard(ScannerDeviceManager.getMux());       // Protect our iteration!

    if(ScannerDeviceManager.getMap().entries())
    {
        CtiRTDB<CtiDeviceBase>::CtiRTDBIterator   itr_dev(ScannerDeviceManager.getMap());

        for(; ++itr_dev ;)
        {
            Device = (CtiDevice*) itr_dev.value();

            if( Device->isSingle() )
            {
                DeviceRecord = (CtiDeviceSingle*) Device;

                if(DeviceRecord->isScanFreezePending())
                {
                    DeviceRecord->resetScanFreezePending();;
                    DeviceRecord->setScanFreezeFailed();
                }
                else if(DeviceRecord->isScanPending())
                {
                    DeviceRecord->resetScanPending();
                }
                else if(DeviceRecord->isScanResetting())
                {
                    DeviceRecord->resetScanResetting();
                    DeviceRecord->setScanResetFailed();
                }
            }
        }
    }

    return status;
}

INT RecordDynamicData()
{
    INT status = NORMAL;

    // Make an attempt to keep the ScanData table current
    if(ScannerDeviceManager.getMap().entries())
    {
        CtiDevice *Device;

        RWRecursiveLock<RWMutexLock>::LockGuard guard(ScannerDeviceManager.getMux());
        CtiRTDB<CtiDeviceBase>::CtiRTDBIterator   itr_dev(ScannerDeviceManager.getMap());

        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        if(conn.isValid())
        {
            conn.beginTransaction();

            try
            {
                for(; ++itr_dev ;)
                {
                    Device = (CtiDevice*) itr_dev.value();

                    if( Device->isSingle() )
                    {
                        CtiDeviceSingle *DeviceRecord = (CtiDeviceSingle*)Device;

                        if(DeviceRecord->getScanData().isDirty())
                        {
                            if(ScannerDebugLevel & SCANNER_DEBUG_DYNAMICDATA)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << "   Updating DynamicDeviceScanData for device " << DeviceRecord->getName() << endl;
                            }
                            DeviceRecord->getScanData().Update(conn);
                        }
                    }
                }

                conn.commitTransaction();
            }
            catch(...)
            {
                conn.commitTransaction();
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ") ";
                    dout << " Trying to commit transaction on DynamicDeviceScanData" << endl;

                }
            }
        }
    }

    return status;
}
