#include "precompiled.h"

#include "ctitime.h"

#include "CServiceConfig.h"
#include "id_macs.h"
#include "mc_svc.h"
#include "dllbase.h"
#include "connection_base.h"
#include "logManager.h"

#include <windows.h>
#include <iostream>
#include <iterator>
#include <vector>
#include <algorithm>
#include <crtdbg.h>

// Shutdown logging when this object is destroyed
Cti::Logging::AutoShutdownLoggers g_autoShutdownLoggers;

// Close all messaging connections when this object is destroyed
Cti::Messaging::AutoCloseAllConnections g_autoCloseAllConnections;

using namespace std;

/* Called when we get an SEH exception.  Generates a minidump. */
ETN_MINIDUMP_EXCEPTION_FILTER;

int main(int argc, char* argv[])
{
    LPTSTR szName = MC_SERVICE_NAME;
    LPTSTR szDisplay = MC_SERVICE_DISPLAY_NAME;

    // Catch and clean SEH Exceptions and make sure we get a minidump
    SetUnhandledExceptionFilter(MinidumpExceptionFilter);

    try
    {
        if( ! Cti::createExclusiveEvent("MC_EXCLUSION_EVENT") )
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

    // Initialize the global logger
    doutManager.setDefaultOptions( CompileInfo, "macs" );
    doutManager.start();

    // Hack to detect whether we are running as a service
    // or in a console
    if( Cti::setConsoleTitle(CompileInfo) )
    {
        if ( argc > 1 )
        {
            //Process command line
            if (strcmp(argv[1], "-install") == 0)
            {
                CTILOG_INFO(dout, "Installing as a service...");
                CServiceConfig si(szName, szDisplay);
                si.Install(SERVICE_WIN32_OWN_PROCESS,
                           SERVICE_DEMAND_START,
                           NULL,
                           NULL,
                           NULL );
                return 0;
            }
            else if (strcmp(argv[1], "-auto") == 0)
            {
                CTILOG_INFO(dout, "Installing as a service...");
                CServiceConfig si(szName, szDisplay);
                si.Install(SERVICE_WIN32_OWN_PROCESS,
                           SERVICE_AUTO_START,
                           NULL,
                           NULL,
                           NULL );
                return 0;
            }
            else
            if ( strcmp(argv[1], "-remove" ) == 0 )
            {
                CTILOG_INFO(dout, "Removing service...");
                CServiceConfig si(szName, szDisplay);
                si.Remove();
                return 0;
            }
        }

        CtiMCService service(szName, szDisplay, SERVICE_WIN32_OWN_PROCESS );

        if ( argc > 2 )
            argv[1] = argv[2];

        service.RunInConsole(argc - 1, argv );
    }
    else
    {
        CtiMCService service(szName, szDisplay, SERVICE_WIN32_OWN_PROCESS );

        //Set up an entry for the one service and go
        BEGIN_SERVICE_MAP
        SERVICE_MAP_ENTRY(CtiMCService, MACS)
        END_SERVICE_MAP
    }

    return 0;
}
