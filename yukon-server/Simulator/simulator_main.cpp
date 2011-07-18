#include "precompiled.h"

#include <iostream>

#include "ctitime.h"
#include <rw/thr/thrutil.h>

#include "ccusimsvc.h"
#include "CServiceConfig.h"
#include "ctibase.h"

#include "logger.h"
#include "guard.h"

using namespace std;

int install(DWORD dwStart = SERVICE_DEMAND_START);
int remove();

int RunningInConsole = FALSE;
LPTSTR szServiceName = "CCUSIMULATOR";
LPTSTR szDisplayName = "Yukon CCU Simulator Service";
LPTSTR szDesc = "Simulates the actions of CCU devices";

int main(int argc, char* argv[] )
{
    if( SetConsoleTitle("CCU Simulator") ) // We are a console application
    {
          //Process command line
          if( argc > 1 && strcmp(argv[1], "-install") == 0  )
          {
              return install();
          }
          else if( argc > 1 && strcmp(argv[1], "-auto") == 0  )
          {
              return install(SERVICE_AUTO_START);
          }
          else if( argc > 1 && strcmp(argv[1], "-remove" ) == 0 )
          {
              return remove();
          }
          else
          {
             dout.setWriteInterval(0);
             CtiSimulatorService service(szServiceName, szDisplayName, SERVICE_WIN32_OWN_PROCESS );

             RunningInConsole = TRUE;
             service.RunInConsole(argc, argv );
          }
    }
    else
    {
          CtiSimulatorService service(szServiceName, szDisplayName, SERVICE_WIN32_OWN_PROCESS );

          //Set up an entry for the one service and go
          BEGIN_SERVICE_MAP
          SERVICE_MAP_ENTRY(CtiSimulatorService, CCUSIMULATOR)
          END_SERVICE_MAP
    }

    return 0;
}

int install(DWORD dwStart)
{
    char depend[1000];

    memset(depend, 0, 1000 );

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime()  << " - Installing as a service..." << endl;
    }

    CServiceConfig si(szServiceName, szDisplayName, szDesc);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Installing CCU Simulator Service Using LocalSystem Account." << endl;
    }

    // test using the LocalSystem account
    si.Install(SERVICE_WIN32_OWN_PROCESS,
                       dwStart,
                       NULL,
                       NULL,
                       NULL);

    return 0;
}

int remove()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime()  << " - Removing service..." << endl;
    }

    CServiceConfig si(szServiceName, szDisplayName);
    si.Remove();

    return 0;
}

