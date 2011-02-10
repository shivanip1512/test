#include "yukon.h"

#include <rw/toolpro/winsock.h>
#include <rw/thr/thrutil.h>

#include "lmcontrolareastore.h"
#include "loadmanager.h"
#include "dbaccess.h"
#include "executor.h"
#include "lmservice.h"
#include "precomp.h"
#include "monitor.h"
#include "CServiceConfig.h"
#include "rtdb.h"
#include "ctibase.h"

using namespace std;

int main(int argc, char* argv[] )
{
    INT RunningInConsole = FALSE;
    LPTSTR szServiceName = "LoadManagement";
    LPTSTR szDisplayName = "Yukon Load Management Service";
    HANDLE hExclusion;

    /*{
        RWMutexLock::LockGuard guard(coutMux);
        cout << CtiTime() << " - Load Management starting up..." << endl;
    }*/

    if( (hExclusion = OpenEvent(EVENT_ALL_ACCESS, FALSE, "LoadManagement")) != NULL )
    {
       // Oh no, porter is running on this machine already.
       CloseHandle(hExclusion);
       cout << "Load Management is already running!!!" << endl;
       exit(-1);
    }

    hExclusion = CreateEvent(NULL, TRUE, FALSE, "LoadManagement");

    if( hExclusion == (HANDLE)NULL )
    {
       cout << "Couldn't create LoadManagement!!!" << endl;
       exit(-1);
    }

    if( setConsoleTitle(CompileInfo) ) // We are a console application
    {
        //Process command line
        if( argc > 1 && strcmp(argv[1], "-install") == 0  )
        {
            RWMutexLock::LockGuard guard(coutMux);
            cout << CtiTime()  << " - Installing as a service..." << endl;
            CServiceConfig si(szServiceName, szDisplayName);
            si.Install(SERVICE_WIN32_OWN_PROCESS,
                       SERVICE_DEMAND_START,
                       NULL,
                       NULL,
                       NULL );
        }
        else if( argc > 1 && strcmp(argv[1], "-auto") == 0  )
        {
            RWMutexLock::LockGuard guard(coutMux);
            cout << CtiTime()  << " - Installing as a service..." << endl;
            CServiceConfig si(szServiceName, szDisplayName);
            si.Install(SERVICE_WIN32_OWN_PROCESS,
                       SERVICE_AUTO_START,
                       NULL,
                       NULL,
                       NULL );
        }
        else if( argc > 1 && strcmp(argv[1], "-remove" ) == 0 )
        {
            RWMutexLock::LockGuard guard(coutMux);
            cout << CtiTime()  << " - Removing service..." << endl;
            CServiceConfig si(szServiceName, szDisplayName);
            si.Remove();
        }
        else
        {
            CtiLMService service(szServiceName, szDisplayName, SERVICE_WIN32_OWN_PROCESS );

            RunningInConsole = TRUE;
            service.RunInConsole(argc, argv );
        }
    }
    else
    {
        CtiLMService service(szServiceName, szDisplayName, SERVICE_WIN32_OWN_PROCESS );

        //Set up an entry for the one service and go
        BEGIN_SERVICE_MAP
        SERVICE_MAP_ENTRY(CtiLMService, LOADMANAGEMENT)
        END_SERVICE_MAP
    }

    CloseHandle(hExclusion);
    return 0;
}
