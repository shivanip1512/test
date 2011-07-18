/*---------------------------------------------------------------------------
        Filename:  ccmain.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiCCMain.
                        CtiCCMain is the main program for Cap Control.

        Initial Date:  9/04/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "precompiled.h"

#include <rw/toolpro/winsock.h>
#include <rw/thr/thrutil.h>

#include "ccsubstationbusstore.h"
#include "capcontroller.h"
#include "dbaccess.h"
#include "ccexecutor.h"
#include "ccservice.h"
#include "precomp.h"
#include "monitor.h"
#include "CServiceConfig.h"
#include "rtdb.h"
#include "ctibase.h"

extern compileinfo_t CompileInfo;

using namespace std;

int main(int argc, char* argv[] )
{
    RWWinSockInfo sock_init;

    INT RunningInConsole = FALSE;
    LPTSTR szServiceName = "CapControl";
    LPTSTR szDisplayName = "Yukon Cap Control Service";
    HANDLE hExclusion;

    /*{
        RWMutexLock::LockGuard guard(coutMux);
        cout << CtiTime() << " - Cap Controller starting up..." << endl;
    }*/

    if( (hExclusion = OpenEvent(EVENT_ALL_ACCESS, FALSE, "CapControl")) != NULL )
    {
       // Oh no, porter is running on this machine already.
       CloseHandle(hExclusion);
       cout << "Cap Control is already running!!!" << endl;
       exit(-1);
    }

    hExclusion = CreateEvent(NULL, TRUE, FALSE, "CapControl");

    if( hExclusion == (HANDLE)NULL )
    {
       cout << "Couldn't create CapControl!!!" << endl;
       exit(-1);
    }

    if( setConsoleTitle(CompileInfo) ) // We are a console application
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

            RunningInConsole = TRUE;
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
