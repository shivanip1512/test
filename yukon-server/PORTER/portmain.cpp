#include "precompiled.h"

#include <iostream>


#include "ctitime.h"

#include "portsvc.h"
#include "CServiceConfig.h"
#include "dllbase.h"
#include "portglob.h"
#include "cparms.h"
#include "logManager.h"
#include "guard.h"
#include "connection_base.h"

// Shutdown logging when this object is destroyed
Cti::Logging::AutoShutdownLoggers g_autoShutdownLoggers;

// Close all messaging connections when this object is destroyed
Cti::Messaging::AutoCloseAllConnections g_autoCloseAllConnections;

using namespace std;

int install(DWORD dwStart = SERVICE_DEMAND_START);
int remove();

// A global variable to indicate the service status..

extern int PorterMainFunction (int argc, char **argv);

INT RunningInConsole = FALSE;
LPTSTR szServiceName = "Yukon Port Control Service";
LPTSTR szDisplayName = "Yukon Port Control Service";
LPTSTR szDesc = "Controls communications to field devices";

/* Called when we get an SEH exception.  Generates a minidump. */
ETN_MINIDUMP_EXCEPTION_FILTER;

int main(int argc, char* argv[])
{
    // Catch and clean SEH Exceptions and make sure we get a minidump
    SetUnhandledExceptionFilter(MinidumpExceptionFilter);

   InitYukonBaseGlobals();

   doutManager.setDefaultOptions( CompileInfo, "porter" );
   doutManager.start(); // fire up the logger thread

   string dbglogdir(gLogDirectory + "\\Debug");
   // Create a subdirectory called Comm beneath Log.
   CreateDirectoryEx( gLogDirectory.c_str(), dbglogdir.c_str(), NULL);

   slogManager.setOwnerInfo     ( CompileInfo );
   slogManager.setOutputPath    ( dbglogdir );
   slogManager.setOutputFile    ( "simulate" );
   slogManager.setToStdOut      ( gConfigParms.getValueAsInt("YUKON_SIMULATE_TOSTDOUT",0) );
   slogManager.reloadSettings();
   slogManager.start(); // fire up the simulator thread

   CTILOG_INFO(slog, "Simulator Started");

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
         CtiPorterService service(szServiceName, szDisplayName, SERVICE_WIN32_OWN_PROCESS );

         RunningInConsole = TRUE;
         service.RunInConsole(argc, argv );

      }
   }
   else
   {
      CtiPorterService service(szServiceName, szDisplayName, SERVICE_WIN32_OWN_PROCESS );

      //Set up an entry for the one service and go
      BEGIN_SERVICE_MAP
      SERVICE_MAP_ENTRY(CtiPorterService, Porter)
      END_SERVICE_MAP

   }

   PorterQuit = TRUE;

   return 0;
}

int install(DWORD dwStart)
{
    CTILOG_INFO(dout, "Installing as a service...");

    CServiceConfig si(szServiceName, szDisplayName, szDesc);

    CTILOG_INFO(dout, "Installing porter Service Using LocalSystem Account.");

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

    CServiceConfig si(szServiceName, szDisplayName);
    si.Remove();

    return 0;
}

