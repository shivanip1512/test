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
            Cti::identifyProject(CompileInfo);

            hPorterEvents[P_QUIT_EVENT] = Cti::createExclusiveEvent("Porter",
                                                                    PorterSyncs[P_QUIT_EVENT].manualReset,
                                                                    PorterSyncs[P_QUIT_EVENT].initState,
                                                                    PorterSyncs[P_QUIT_EVENT].syncObjName);

            for(int i = 0 ;i < NUMPORTEREVENTS; i++)
            {
                if(i = P_QUIT_EVENT)
                    continue;

                hPorterEvents[i] = CreateEvent(NULL,
                                               PorterSyncs[i].manualReset,
                                               PorterSyncs[i].initState,
                                               PorterSyncs[i].syncObjName);

                if(hPorterEvents[i] == (HANDLE)NULL)
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
