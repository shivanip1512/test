/*-----------------------------------------------------------------------------*
*
* File:   dllmain
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/dllmain.cpp-arc  $
* REVISION     :  $Revision: 1.9.14.2 $
* DATE         :  $Date: 2008/11/20 16:49:26 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"


#include <stdio.h>
#include <string.h>
#include <process.h>

#include <winbase.h>
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

using std::endl;

CtiSyncDefStruct PorterSyncs[] = {
   { CtiEventType, TRUE , FALSE, "CtiTimeSyncEvent"},
   { CtiEventType, TRUE , FALSE, "CtiQueueEvent"},
   { CtiEventType, TRUE , FALSE, "CtiPorterQuitEvent"},
};

void APIENTRY GlobalsCleanUp ()
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

                std::cout << "Porter is already running!" << endl;

                exit(-1);
            }

            for(int i = 0 ;i < NUMPORTEREVENTS; i++)
            {
                hPorterEvents[ i ] = CreateEvent(NULL, PorterSyncs[i].manualReset, PorterSyncs[i].initState, PorterSyncs[i].syncObjName);

                if(hPorterEvents[ i ] == (HANDLE)NULL)
                {
                    std::cerr << "Couldn't create porter event # " << i << " " << PorterSyncs[i].syncObjName << endl;
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
            GlobalsCleanUp();

            break;
        }
   }

   return TRUE;
}
