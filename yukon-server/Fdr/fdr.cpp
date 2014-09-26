#include "precompiled.h"

#include <crtdbg.h>
#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/thr/thrfunc.h>
#include "ctitime.h"

#include "dlldefs.h"
#include "ctibase.h"
#include "logger.h"
#include "guard.h"
#include "cparms.h"
#include "CServiceConfig.h"
#include "fdrservice.h"
#include "id_fdr.h"

#include "connection_base.h"
// Close all yukon messaging connections when this object is destroyed
Cti::Messaging::AutoCloseAllConnections g_autoCloseAllConnections;

int install( DWORD dwStart = SERVICE_DEMAND_START );
int remove( void );

LPTSTR szServiceName = "FDR";
LPTSTR szDisplayName = "Yukon Foreign Data Service";

int main( int argc, char *argv[] )
{
    Cti::createExclusiveEvent(CompileInfo, "FDR_EXCLUSION_EVENT");

//    InitYukonBaseGlobals();
//    identifyProject(CompileInfo);

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
            dout.start();     // fire up the logger thread
            dout.setOwnerInfo(CompileInfo);
            dout.setOutputPath(gLogDirectory);
            dout.setRetentionLength(gLogRetention);
            dout.setOutputFile("fdr");
            dout.setToStdOut(true);
            dout.setWriteInterval(0);

            Cti::identifyProject(CompileInfo);

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime( ) << "Starting " << CompileInfo.project << " in console mode" << endl;
            }


            //cout << CtiTime( ) << " - FDR starting up..." << endl;
            CtiFDRService service(szServiceName, szDisplayName, SERVICE_WIN32_OWN_PROCESS );

            service.RunInConsole( argc, argv );
        }
    }
    else
    {
        dout.start();     // fire up the logger thread
        dout.setOwnerInfo(CompileInfo);
        dout.setOutputPath(gLogDirectory);
        dout.setRetentionLength(gLogRetention);
        dout.setOutputFile("fdr");
        dout.setToStdOut(false);
        dout.setWriteInterval(5000);

        Cti::identifyProject(CompileInfo);

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime( ) << "Starting " << CompileInfo.project << " as service" << endl;
        }


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

