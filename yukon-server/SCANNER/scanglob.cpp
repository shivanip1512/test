#include "precompiled.h"

#include "dsm2.h"
#include "scanglob.h"
#include "module_util.h"

// These next few are required for Win32
#include <iostream>
using namespace std;

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
            Cti::identifyProject(CompileInfo);

            try
            {
                hScannerSyncs[S_QUIT_EVENT] = Cti::createExclusiveEvent(ScannerSyncs[S_QUIT_EVENT].manualReset,
                                                                        ScannerSyncs[S_QUIT_EVENT].initState,
                                                                        ScannerSyncs[S_QUIT_EVENT].syncObjName);
            }
            catch( const std::exception& e )
            {
                cerr << e.what() << endl;
                exit(-1);
            }

            if( ! hScannerSyncs[S_QUIT_EVENT] )
            {
                cerr <<"Scanner is already running on this machine, exiting."<< endl;
                exit(-1);
            }

            for(int i = 0 ;i < S_MAX_MUTEX; i++)
            {
                if(i == S_QUIT_EVENT)
                    continue;

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

