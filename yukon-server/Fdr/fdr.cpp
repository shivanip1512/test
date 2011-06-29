/*-----------------------------------------------------------------------------*
*
* DATE: 8/25/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.10.14.1 $
* DATE         :  $Date: 2008/11/13 17:23:46 $
*
*
* AUTHOR: Matt Fisher
*
* PURPOSE: Main() Module for Foreign Data Router (FDR)
*
* DESCRIPTION: Based on CParms it dynamicly loads interface dlls and
*              starts them by calling their RunInterface() function.
*              It also get a pointer to their StopInterface() which it
*              calls on a shutdown.
*
*
*
* Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <crtdbg.h>
#include <iostream>

#include <conio.h>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/thr/thrfunc.h>
#include <rw/toolpro/winsock.h>
#include "ctitime.h"
//#include <rw/thr/thrfunc.h>
//#include <rw/thr/mutex.h>

//#include "dbaccess.h"
#include "dlldefs.h"
#include "ctibase.h"
#include "logger.h"
#include "guard.h"
#include "cparms.h"
#include "CServiceConfig.h"
#include "fdrservice.h"
#include "id_fdr.h"


int install( DWORD dwStart = SERVICE_DEMAND_START );
int remove( void );

LPTSTR szServiceName = "FDR";
LPTSTR szDisplayName = "Yukon Foreign Data Service";

int main( int argc, char *argv[] )
{
    RWWinSockInfo sock_init;        // global declare for winsock
    HANDLE hExclusion;

    if( (hExclusion = OpenEvent(EVENT_ALL_ACCESS, FALSE, "FDR_EXCLUSION_EVENT")) != NULL )
    {
        // Oops, fdr is running on this machine already.
        CloseHandle(hExclusion);

        cout << "FDR is already running on this machine, exiting." << Cti::endl;

        exit(-1);
    }

    // Set event so to avoid additional copies of FDR on this machine
    hExclusion = CreateEvent(NULL, TRUE, FALSE, "FDR_EXCLUSION_EVENT");

    if( hExclusion == (HANDLE)NULL )
    {
       cout << "Couldn't create fdr" << Cti::endl;
       exit(-1);
    }

//    InitYukonBaseGlobals();
//    identifyProject(CompileInfo);

    if( setConsoleTitle(CompileInfo) ) // We are a console application
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
            cout << " - " << CompileInfo.project << " Version [" << CompileInfo.version << "]" << Cti::endl;
        }
        else
        {
            dout.start();     // fire up the logger thread
            dout.setOwnerInfo(CompileInfo);
            dout.setOutputPath(gLogDirectory);
            dout.setOutputFile("fdr");
            dout.setToStdOut(true);
            dout.setWriteInterval(0);

            identifyProject(CompileInfo);

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
        dout.setOutputFile("fdr");
        dout.setToStdOut(false);
        dout.setWriteInterval(5000);

        identifyProject(CompileInfo);

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

