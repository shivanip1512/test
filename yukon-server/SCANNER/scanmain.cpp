#include "precompiled.h"

#include <iostream>
using namespace std;

#include "ctitime.h"

#include "scansvc.h"
#include "scanner_syncs.h"
#include "CServiceConfig.h"
#include "dllbase.h"
#include "dllbase.h"
#include "logManager.h"
#include "thread_monitor.h"
#include "connection_base.h"

// Shutdown logging when this object is destroyed
Cti::Logging::AutoShutdownLoggers g_autoShutdownLoggers;

// Close all messaging connections when this object is destroyed
Cti::Messaging::AutoCloseAllConnections g_autoCloseAllConnections;

extern INT ScannerMainFunction(INT, CHAR**);

/* Called when we get an SEH exception.  Generates a minidump. */
ETN_MINIDUMP_EXCEPTION_FILTER;

int main(int argc, char* argv[])
{
   LPTSTR szName    = "Yukon Real-Time Scan Service";
   LPTSTR szDisplay = "Yukon Real-Time Scan Service";
   LPTSTR szDesc = "Manages the periodic - timed scanning of field devices";

   // Catch and clean SEH Exceptions and make sure we get a minidump
   SetUnhandledExceptionFilter(MinidumpExceptionFilter);

   doutManager.setDefaultOptions(CompileInfo, "scanner");
   doutManager.start(); // fire up the logger thread

   ThreadMonitor.start(CtiThreadMonitor::Scanner);

   Cti::Scanner::CreateSyncEvents();

   if( Cti::setConsoleTitle(CompileInfo) )
   {
      //Process command line
      if( argc > 1 && strcmp(argv[1], "-install") == 0  )
      {
          CTILOG_INFO(dout, "Installing Yukon Real-Time Scan Service");

         CServiceConfig si(szName, szDisplay, szDesc);
         si.Install(SERVICE_WIN32_OWN_PROCESS,
                    SERVICE_DEMAND_START,
                    NULL,
                    NULL,   // Use the LocalSystem Account
                    NULL);
      }
      else if( argc > 1 && strcmp(argv[1], "-auto") == 0  )
      {
          CTILOG_INFO(dout, "Installing Yukon Real-Time Scan Service");

         CServiceConfig si(szName, szDisplay, szDesc);
         si.Install(SERVICE_WIN32_OWN_PROCESS,
                    SERVICE_AUTO_START,
                    NULL,
                    NULL,   // Use the LocalSystem Account
                    NULL);
      }
      else if( argc > 1 && strcmp(argv[1], "-remove" ) == 0 )
      {
          CTILOG_INFO(dout, "Removing Yukon Real-Time Scanner Service");

         CServiceConfig si(szName, szDisplay);
         si.Remove();
      }
      else
      {
         CtiScannerService service(szName, szDisplay, SERVICE_WIN32_OWN_PROCESS );
         service.RunInConsole(argc, argv );
      }
   }
   else
   {
      CtiScannerService service(szName, szDisplay, SERVICE_WIN32_OWN_PROCESS );

      //Set up an entry for the one service and go
      BEGIN_SERVICE_MAP
      SERVICE_MAP_ENTRY(CtiScannerService, Scanner)
      END_SERVICE_MAP
   }

   Cti::Scanner::DestroySyncEvents();

   ThreadMonitor.interrupt(CtiThread::SHUTDOWN);
   ThreadMonitor.join();

   return 0;
}
