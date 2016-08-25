#include "precompiled.h"

#include "dsm2.h"
#include "scanner_syncs.h"
#include "module_util.h"
#include "logger.h"

#include <iostream>
using namespace std;

bool SuspendLoadProfile = false;

HANDLE      hScannerSyncs[S_MAX_MUTEX];

CtiSyncDefStruct ScannerSyncs[] = {
   { CtiEventType, TRUE , FALSE, "CtiScannerQuitEvent" },
   { CtiEventType, TRUE , FALSE, "CtiScannerPostEvent" },
   { CtiMutexType, FALSE, FALSE, "CtiScannerLockMutex" }      // Initially UNowned
};

namespace Cti {
namespace Scanner {

void CreateSyncEvents()
{
    CTILOG_INFO(dout, "Creating sync events");

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
}

void DestroySyncEvents()
{
    CTILOG_INFO(dout, "Destroying sync events");
    
    for(int i = 0 ;i < S_MAX_MUTEX; i++)
    {
        if(hScannerSyncs[ i ] != (HANDLE)NULL)
        {
            CloseHandle(hScannerSyncs[ i ]);
        }
    }
}

}}