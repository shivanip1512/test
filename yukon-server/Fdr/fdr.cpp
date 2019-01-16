#include "precompiled.h"

#include "ctitime.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "logManager.h"
#include "guard.h"
#include "cparms.h"
#include "CServiceConfig.h"
#include "fdrservice.h"
#include "id_fdr.h"
#include "connection_base.h"

#include <crtdbg.h>
#include <iostream>

using namespace std;

// Shutdown logging when this object is destroyed
Cti::Logging::AutoShutdownLoggers g_autoShutdownLoggers;

// Close all yukon messaging connections when this object is destroyed
Cti::Messaging::AutoCloseAllConnections g_autoCloseAllConnections;

int install( DWORD dwStart = SERVICE_DEMAND_START );
int remove( void );

LPTSTR szServiceName = "Yukon Foreign Data Service";
LPTSTR szDisplayName = "Yukon Foreign Data Service";

/* Called when we get an SEH exception.  Generates a minidump. */
ETN_MINIDUMP_EXCEPTION_FILTER;

int main(int argc, char *argv[])
{
    // Catch and clean SEH Exceptions and make sure we get a minidump
    SetUnhandledExceptionFilter(MinidumpExceptionFilter);

    try
    {
        if( ! Cti::createExclusiveEvent("FDR_EXCLUSION_EVENT") )
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

//    InitYukonBaseGlobals();
//    identifyExecutable(CompileInfo);

    if( Cti::setConsoleTitle(CompileInfo) ) // We are a console application
    {
        // Process command line if in console

        //  Process command line
        if( argc > 1 && strcmp(argv[1], "-install") == 0 )
        {
            return install();
        }
        else if( argc > 1 && strcmp(argv[1], "-auto") == 0 )
        {
            return install(SERVICE_AUTO_START);
        }
        else if( argc > 1 && strcmp(argv[1], "-remove" ) == 0 )
        {
            return remove();
        }
        else if( argc > 1 && strcmp(argv[1], "-version" ) == 0 )
        {
            cout << " - " << CompileInfo.project << " Version [" << CompileInfo.version << "]" << endl;
        }
        else
        {
            doutManager.setDefaultOptions(CompileInfo, "fdr");
            doutManager.start();     // fire up the logger thread

            Cti::identifyExecutable(CompileInfo);

            CTILOG_INFO(dout, "Starting "<< CompileInfo.project <<" in console mode");

            //cout << CtiTime( ) << " - FDR starting up..." << endl;
            CtiFDRService service(szServiceName, szDisplayName, SERVICE_WIN32_OWN_PROCESS );

            service.RunInConsole( argc, argv );
        }
    }
    else
    {
        doutManager.setDefaultOptions(CompileInfo, "fdr");
        doutManager.start();     // fire up the logger thread

        Cti::identifyExecutable(CompileInfo);

        CTILOG_INFO(dout, "Starting "<< CompileInfo.project << " as service");

        CtiFDRService service(szServiceName, szDisplayName, SERVICE_WIN32_OWN_PROCESS );

        //Set up an entry for the one service and go
        BEGIN_SERVICE_MAP
        SERVICE_MAP_ENTRY(CtiFDRService, FDR)
        END_SERVICE_MAP
    }

    return 0;
}

int install( DWORD dwStart )
{
    string           depends;

    if ( gConfigParms.isOpt(CPARM_NAME_SRV_DEPENDENCIES) )
    {
        depends = gConfigParms.getValueAsString(CPARM_NAME_FDR_INTERFACES);
        cout << "FDR Services Dependencies specified: " << depends << endl;
    }

    cout << CtiTime( ) << " - Installing FDR as a service..." << endl;

    CServiceConfig si(szServiceName, szDisplayName);

    si.Install( SERVICE_WIN32_OWN_PROCESS,
                dwStart,
                depends.c_str(),
                NULL,   // Use LocalSystem Account
                NULL);

    return 0;
}

int remove( void )
{
    cout << CtiTime( )  << " - Removing FDR service..." << endl;
    CServiceConfig si(szServiceName, szDisplayName);
    si.Remove( );

    return 0;
}

