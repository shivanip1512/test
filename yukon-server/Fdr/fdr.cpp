#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

/*-----------------------------------------------------------------------------*
*
* DATE: 8/25/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:30 $
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
*-----------------------------------------------------------------------------*
*/

//
//$Archive$
//
//$Revision: 1.3 $
//$Date: 2002/04/16 15:58:30 $
//

/** include files **/
#include <crtdbg.h>
#include <windows.h>
#include <iostream>

#include <conio.h>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/thr/thrfunc.h>
#include <rw/toolpro/winsock.h>
#include <rw/rwtime.h>
//#include <rw/thr/thrfunc.h>
//#include <rw/thr/mutex.h>
#include <rw/cstring.h>

//#include "dbaccess.h"
#include "dlldefs.h"
#include "ctibase.h"
#include "logger.h"
#include "guard.h"
#include "cparms.h"
#include "CServiceConfig.h"
#include "fdrservice.h"
#include "id_ctibase.h"


int install( void );
int remove( void );

LPTSTR szServiceName = "YukonFDR";
LPTSTR szDisplayName = "Yukon Foreign Data Service";

int main( int argc, char *argv[] )
{
    RWWinSockInfo sock_init;        // global declare for winsock
    BOOL consoleRet;

//    InitYukonBaseGlobals();
    identifyProject(CompileInfo);

    char tstr[256];
    sprintf(tstr,"Foreign Data Router - YUKON (Build %d.%d.%d)", CompileInfo.major, CompileInfo.minor, CompileInfo.build);
    consoleRet = SetConsoleTitle( tstr );

    if(consoleRet) // We are a console application
    {
        // Process command line if in console

        //  Process command line
        if( argc > 1 && strcmp(argv[1], "-install") == 0 )
        {
            return install();
        }
        else if( argc > 1 && strcmp(argv[1], "-remove" ) == 0 )
        {
            return remove();
        }
        else if( argc > 1 && strcmp(argv[1], "-version" ) == 0 )
        {
            cout << " - " << RWCString (tstr) << endl;
        }
        else
        {
            dout.start();     // fire up the logger thread
            dout.setOutputPath(gLogDirectory.data());
            dout.setOutputFile("fdr");
            dout.setToStdOut(true);
            dout.setWriteInterval(0);

            {
                sprintf(tstr," (Build %d.%d.%d) Foreign Data Router", CompileInfo.major, CompileInfo.minor, CompileInfo.build);
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime( ) << RWCString (tstr) << " starting console mode ..." << endl;
            }


            //cout << RWTime( ) << " - FDR starting up..." << endl;
            CtiFDRService service(szServiceName, szDisplayName, SERVICE_WIN32_OWN_PROCESS );

            service.RunInConsole( argc, argv );
        }
    }
    else
    {
        dout.start();     // fire up the logger thread
        dout.setOutputPath(gLogDirectory.data());
        dout.setOutputFile("fdr");
      dout.setToStdOut(false);
        dout.setWriteInterval(5000);

        {
            sprintf(tstr," (Build %d.%d.%d) Foreign Data Router", CompileInfo.major, CompileInfo.minor, CompileInfo.build);
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime( ) << RWCString (tstr) << " starting up..." << endl;
        }


        CtiFDRService service(szServiceName, szDisplayName, SERVICE_WIN32_OWN_PROCESS );

        //Set up an entry for the one service and go
        BEGIN_SERVICE_MAP
        SERVICE_MAP_ENTRY(CtiFDRService, FDR)
        END_SERVICE_MAP
    }

    return 0;
}

int install( void )
{
    CtiConfigParameters configParameters;
    RWCString           depends;

    if ( configParameters.isOpt(CPARM_NAME_SRV_DEPENDENCIES) )
    {
        depends = configParameters.getValueAsString(CPARM_NAME_FDR_INTERFACES);
        cout << "FDR Services Dependencies specified: " << depends << endl;
    }

    cout << RWTime( ) << " - Installing FDR as a service..." << endl;


    CServiceConfig si(szServiceName, szDisplayName);

    si.Install( SERVICE_WIN32_OWN_PROCESS,
                SERVICE_DEMAND_START,
                depends,
                NULL,   // Use LocalSystem Account
                NULL);

    return 0;
}

int remove( void )
{
    cout << RWTime( )  << " - Removing FDR service..." << endl;
    CServiceConfig si(szServiceName, szDisplayName);
    si.Remove( );

    return 0;
}

