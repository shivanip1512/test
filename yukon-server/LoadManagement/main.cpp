#include "precompiled.h"

#include "lmcontrolareastore.h"
#include "loadmanager.h"
#include "dbaccess.h"
#include "executor.h"
#include "lmservice.h"
#include "precomp.h"
#include "monitor.h"
#include "CServiceConfig.h"
#include "rtdb.h"
#include "dllbase.h"
#include "module_util.h"
#include "connection_base.h"
#include "logManager.h"

// Shutdown logging when this object is destroyed
Cti::Logging::AutoShutdownLoggers g_autoShutdownLoggers;

// Close all yukon messaging connections when this object is destroyed
Cti::Messaging::AutoCloseAllConnections g_autoCloseAllConnections;

using namespace std;

/* Called when we get an SEH exception.  Generates a minidump. */
static LONG WINAPI MinidumpExceptionFilter(PEXCEPTION_POINTERS pExceptionPtrs)
{
    return CreateMiniDumpExceptionHandler(CompileInfo);
}

int main(int argc, char* argv[])
{
    INT RunningInConsole = FALSE;
    LPTSTR szServiceName = "LoadManagement";
    LPTSTR szDisplayName = "Yukon Load Management Service";

    // Catch and clean SEH Exceptions and make sure we get a minidump
    SetUnhandledExceptionFilter(MinidumpExceptionFilter);

    try
    {
        if( ! Cti::createExclusiveEvent("LoadManagement") )
        {
            cerr << CompileInfo.project <<" is already running on this machine, exiting."<< endl;
            exit(-1);
        }
    }
    catch( const std::exception& e )
    {
        cerr << e.what() << endl;
        exit(-1);
    }

    if( Cti::setConsoleTitle(CompileInfo) ) // We are a console application
    {
        //Process command line
        if( argc > 1 && strcmp(argv[1], "-install") == 0  )
        {
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

    return 0;
}
