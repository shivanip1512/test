#include "precompiled.h"

#include <iostream>
using namespace std;

#include "ctitime.h"
#include <rw/thr/thrfunc.h>
#include <rw/thr/thrutil.h>

#include "dispsvc.h"
#include "dllvg.h"
#include "CServiceConfig.h"
#include "dllbase.h"
#include "ctibase.h"
#include "logManager.h"
#include "utility.h"
#include "dbghelp.h"
#include "connection_base.h"

// Shutdown logging when this object is destroyed
Cti::Logging::AutoShutdownLoggers g_autoShutdownLoggers;

// Close all messaging connections when this object is destroyed
Cti::Messaging::AutoCloseAllConnections g_autoCloseAllConnections;

int main(int argc, char* argv[] )
{
   BOOL bConsole;
   LPTSTR szName = "Dispatch";
   LPTSTR szDisplay = "Yukon Dispatch Service";

   try
   {
       if( ! Cti::createExclusiveEvent(szName) )
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

   InitDispatchGlobals();

   doutManager.setOwnerInfo     ( CompileInfo );
   doutManager.setOutputPath    ( gLogDirectory );
   doutManager.setRetentionDays ( gLogRetention );
   doutManager.setOutputFile    ( "dispatch" );
   doutManager.setToStdOut      ( true );
   doutManager.start();     // fire up the logger thread

   Cti::identifyProject(CompileInfo);

   if( Cti::setConsoleTitle(CompileInfo) )
   {
      //Process command line
      if( argc > 1 && strcmp(argv[1], "-install") == 0  )
      {
         cout << CtiTime()  << " - Installing Yukon " << CompileInfo.project << " service" << endl;
         CServiceConfig si(szName, szDisplay);
         si.Install(SERVICE_WIN32_OWN_PROCESS,
                    SERVICE_DEMAND_START,
                    NULL,
                    NULL,   // Use LocalSystem Account
                    NULL );

         return 0;

      }
      else if( argc > 1 && strcmp(argv[1], "-auto") == 0  )
      {
         cout << CtiTime()  << " - Installing Yukon " << CompileInfo.project << " Service" << endl;
         CServiceConfig si(szName, szDisplay);
         si.Install(SERVICE_WIN32_OWN_PROCESS,
                    SERVICE_AUTO_START,
                    NULL,
                    NULL,   // Use LocalSystem Account
                    NULL );

         return 0;

      }
      else if( argc > 1 && strcmp(argv[1], "-remove" ) == 0 )
      {
         cout << CtiTime()  << " - Removing Yukon " << CompileInfo.project << " Service" << endl;
         CServiceConfig si(szName, szDisplay);
         si.Remove();
         return 0;
      }
      else
      {
         CtiDispatchService service(szName, szDisplay, SERVICE_WIN32_OWN_PROCESS );
         service.RunInConsole(argc, argv );
      }
   }
   else
   {
      CtiDispatchService service(szName, szDisplay, SERVICE_WIN32_OWN_PROCESS );

      //Set up an entry for the one service and go
      BEGIN_SERVICE_MAP
      SERVICE_MAP_ENTRY(CtiDispatchService, Dispatch)
      END_SERVICE_MAP
   }

   return 0;
}
