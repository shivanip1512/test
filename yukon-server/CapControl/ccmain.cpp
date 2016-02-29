#include "precompiled.h"

#include "ccsubstationbusstore.h"
#include "capcontroller.h"
#include "dbaccess.h"
#include "ccexecutor.h"
#include "ccservice.h"
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
ETN_MINIDUMP_EXCEPTION_FILTER;

int main(int argc, char* argv[] )
{
    INT RunningInConsole = false;
    LPTSTR szServiceName = "CapControl";
    LPTSTR szDisplayName = "Yukon Cap Control Service";

    // Catch and clean SEH Exceptions and make sure we get a minidump
    SetUnhandledExceptionFilter(MinidumpExceptionFilter);

    try
    {
        if( ! Cti::createExclusiveEvent("CapControl") )
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
                       NULL,    // Use LocalSystem Account
                       NULL);
        }
        else if( argc > 1 && strcmp(argv[1], "-auto") == 0  )
        {
            cout << CtiTime()  << " - Installing as an autostart service..." << endl;
            CServiceConfig si(szServiceName, szDisplayName);
            si.Install(SERVICE_WIN32_OWN_PROCESS,
                       SERVICE_AUTO_START,
                       NULL,
                       NULL,    // Use LocalSystem Account
                       NULL);
        }
        else if( argc > 1 && strcmp(argv[1], "-remove" ) == 0 )
        {
            cout << CtiTime()  << " - Removing service..." << endl;
            CServiceConfig si(szServiceName, szDisplayName);
            si.Remove();
        }
        else
        {
            CtiCCService service(szServiceName, szDisplayName, SERVICE_WIN32_OWN_PROCESS );

            RunningInConsole = true;
            service.RunInConsole(argc, argv );
        }
    }
    else
    {
        CtiCCService service(szServiceName, szDisplayName, SERVICE_WIN32_OWN_PROCESS );

        //Set up an entry for the one service and go
        BEGIN_SERVICE_MAP
        SERVICE_MAP_ENTRY(CtiCCService, CAPCONTROL)
        END_SERVICE_MAP
    }

    return 0;
}
