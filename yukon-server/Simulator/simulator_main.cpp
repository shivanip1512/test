#include "precompiled.h"

#include <iostream>

#include "ctitime.h"

#include "field_simulator_service.h"
#include "CServiceConfig.h"
#include "dllbase.h"
#include "logManager.h"
#include "guard.h"
#include "connection_base.h"

// Shutdown logging when this object is destroyed
Cti::Logging::AutoShutdownLoggers g_autoShutdownLoggers;

// Close all messaging connections when this object is destroyed
Cti::Messaging::AutoCloseAllConnections g_autoCloseAllConnections;

using namespace std;
using Cti::Simulator::FieldSimulatorService;

int install(DWORD dwStart = SERVICE_DEMAND_START);
int remove();

int RunningInConsole = FALSE;
LPTSTR szServiceName = "Yukon Field Simulator Service";
LPTSTR szDisplayName = "Yukon Field Simulator Service";
LPTSTR szDesc = "Simulates the actions of field devices";

extern HANDLE gQuitEvent;

int main(int argc, char* argv[] )
{
    doutManager.setOutputPath    (gLogDirectory);
    doutManager.setOutputFile    ("field_simulator");
    doutManager.setToStdOut      (true);

    doutManager.start(); // fire up the logger thread

    gQuitEvent = CreateEvent(NULL, TRUE, FALSE, NULL);
    if( gQuitEvent == (HANDLE)NULL )
    {
       CTILOG_FATAL(dout, "Couldn't create hQuitEvent!!!");
       exit(-1);
    }

    if( Cti::setConsoleTitle(CompileInfo) ) // We are a console application
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
             FieldSimulatorService service(szServiceName, szDisplayName, SERVICE_WIN32_OWN_PROCESS );

             RunningInConsole = TRUE;
             service.RunInConsole(argc, argv );
          }
    }
    else
    {
        FieldSimulatorService service(szServiceName, szDisplayName, SERVICE_WIN32_OWN_PROCESS );

          //Set up an entry for the one service and go
          BEGIN_SERVICE_MAP
          SERVICE_MAP_ENTRY(FieldSimulatorService, FIELDSIMULATOR)
          END_SERVICE_MAP
    }

    return 0;
}

int install(DWORD dwStart)
{
    char depend[1000];

    memset(depend, 0, 1000 );

    CTILOG_INFO(dout, "Installing as a service...");

    CServiceConfig si(szServiceName, szDisplayName, szDesc);

    CTILOG_INFO(dout, "Installing Field Simulator Service Using LocalSystem Account.");

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
    CTILOG_INFO(dout, "Removing service...");

    {
        CServiceConfig si(szServiceName, szDisplayName);
        si.Remove();
    }

    return 0;
}

