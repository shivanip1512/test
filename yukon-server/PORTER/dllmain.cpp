/*-----------------------------------------------------------------------------*
*
* File:   dllmain
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/dllmain.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/02/10 23:23:54 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <stdio.h>
#include <string.h>
#include <process.h>
#include <windows.h>
#include <winbase.h>
#include <winsock.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include "dsm2.h"
#ifdef IMPORT

   #undef IMPORT
   #include "portglob.h"
   #define IMPORT
#endif

#include "portglob.h"
#include "utility.h"

CtiSyncDefStruct PorterSyncs[] = {
   { CtiEventType, TRUE , FALSE, "CtiTimeSyncEvent"},
   { CtiEventType, TRUE , FALSE, "CtiQueueEvent"},
   { CtiEventType, TRUE , FALSE, "CtiScannerPostEvent"},
   { CtiEventType, TRUE , FALSE, "CtiPerformEvent"},
   { CtiEventType, TRUE , FALSE, "CtiPorterQuitEvent"},
   { CtiEventType, TRUE , FALSE, "CtiRefreshEvent"},
   { CtiEventType, TRUE , FALSE, "CtiGWResultEvent"}
};

VOID APIENTRY GlobalsCleanUp ()
{
   /* Close the semaphore with scanner */
   for(int i = 0 ;i < NUMPORTEREVENTS; i++)
   {
      if(hPorterEvents[ i ] != (HANDLE)NULL)
      {
         CloseHandle(hPorterEvents[ i ]);
      }
   }

}

BOOL APIENTRY DllMain(HANDLE hModule,
                      DWORD  ul_reason_for_call,
                      LPVOID lpReserved)
{
   switch( ul_reason_for_call )
   {
   case DLL_PROCESS_ATTACH:
      {
         identifyProject(CompileInfo);

         PorterListenNexus.NexusState = CTINEXUS_STATE_NULL;

         if((hPorterEvents[ P_QUIT_EVENT ] = OpenEvent(EVENT_ALL_ACCESS, FALSE, PorterSyncs[P_QUIT_EVENT].syncObjName))!= NULL)
         {
            // Oh no, porter is running on this machine already.
            CloseHandle(hPorterEvents[ P_QUIT_EVENT ]);
            cout << "Porter is already running!" << endl;
            exit(-1);
         }

         for(int i = 0 ;i < NUMPORTEREVENTS; i++)
         {
            hPorterEvents[ i ] = CreateEvent(NULL, PorterSyncs[i].manualReset, PorterSyncs[i].initState, PorterSyncs[i].syncObjName);

            if(hPorterEvents[ i ] == (HANDLE)NULL)
            {
               cerr << "Couldn't create porter event # " << i << " " << PorterSyncs[i].syncObjName << endl;
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
         // printf("PortGlob DLL is exiting!\n");
         GlobalsCleanUp();
         break;
      }
   }
   return TRUE;
}
