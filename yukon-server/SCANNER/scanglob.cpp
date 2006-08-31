/*-----------------------------------------------------------------------------*
*
* File:   scanglob
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SCANNER/scanglob.cpp-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2006/08/31 23:18:59 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


/* Global definitions for the scanner */

#include <windows.h>       // These next few are required for Win32
#include <iostream>
using namespace std;

#include "os2_2w32.h"
#include "scanner.h"
#include "dsm2.h"
#include "scanglob.h"
#include "dlldefs.h"
#include "utility.h"

IM_EX_SCANSUP bool CCUQueue           = true;
IM_EX_SCANSUP bool SuspendLoadProfile = false;

IM_EX_SCANSUP HANDLE      hScannerSyncs[S_MAX_MUTEX];

CtiSyncDefStruct ScannerSyncs[] = {
   { CtiEventType, TRUE , FALSE, "CtiScannerQuitEvent" },
   { CtiEventType, TRUE , FALSE, "CtiScannerPostEvent" },
   { CtiMutexType, FALSE, FALSE, "CtiScannerLockMutex" }      // Initially UNowned
};

BOOL APIENTRY DllMain(HANDLE hModule, DWORD ul_reason_for_call, LPVOID lpReserved)
{
   switch( ul_reason_for_call )
   {
   case DLL_PROCESS_ATTACH:
      {
         identifyProject(CompileInfo);

         if((hScannerSyncs[ S_QUIT_EVENT ] = OpenEvent(EVENT_ALL_ACCESS,
                                                       FALSE,
                                                       ScannerSyncs[S_QUIT_EVENT].syncObjName))
            != NULL)
         {
            // Oh no, scanner is running on this machine already.
            CloseHandle(hScannerSyncs[ S_QUIT_EVENT ]);
            cout << "Scanner is already running!" << endl;
            exit(-1);
         }

         for(int i = 0 ;i < S_MAX_MUTEX; i++)
         {
            if(i < S_MAX_EVENT)
            {
               hScannerSyncs[ i ] = CreateEvent(NULL,
                                                ScannerSyncs[i].manualReset,
                                                ScannerSyncs[i].initState,
                                                ScannerSyncs[i].syncObjName);

            }
            else
            {
               hScannerSyncs[ i ] = CreateMutex(NULL,
                                                ScannerSyncs[i].initState,
                                                ScannerSyncs[i].syncObjName);

            }

            if(hScannerSyncs[ i ] == (HANDLE)NULL)
            {
               cout << "Couldn't create scanner sync object # " << i << endl;
               exit(-1);
            }
         }
         break;
      }
   case DLL_THREAD_ATTACH:
      {
         break;
      }
   case DLL_THREAD_DETACH:
      {
         break;
      }
   case DLL_PROCESS_DETACH:
      {
         for(int i = 0 ;i < S_MAX_MUTEX; i++)
         {
            if(hScannerSyncs[ i ] != (HANDLE)NULL)
            {
               CloseHandle(hScannerSyncs[ i ]);
            }
         }
         break;
      }
   }
   return TRUE;
}

