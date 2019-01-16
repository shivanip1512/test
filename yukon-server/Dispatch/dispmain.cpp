#include "precompiled.h"

#include <iostream>
using namespace std;

#include "ctitime.h"

#include "dispsvc.h"
#include "CServiceConfig.h"
#include "dllbase.h"
#include "logManager.h"
#include "utility.h"
#include "dbghelp.h"
#include "connection_base.h"
#include "cparms.h"

// Shutdown logging when this object is destroyed
Cti::Logging::AutoShutdownLoggers g_autoShutdownLoggers;

// Close all messaging connections when this object is destroyed
Cti::Messaging::AutoCloseAllConnections g_autoCloseAllConnections;

/* Called when we get an SEH exception.  Generates a minidump. */
ETN_MINIDUMP_EXCEPTION_FILTER;

extern unsigned gDispatchDebugLevel;
extern unsigned gDispatchReloadRate;

void InitDispatchGlobals(void);

int main(int argc, char* argv[])
{
   BOOL bConsole;
   LPTSTR szName    = "Yukon Dispatch Service";
   LPTSTR szDisplay = "Yukon Dispatch Service";

   // Catch and clean SEH Exceptions and make sure we get a minidump
   SetUnhandledExceptionFilter(MinidumpExceptionFilter);


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

   doutManager.setDefaultOptions( CompileInfo, "dispatch" );
   doutManager.start();     // fire up the logger thread

   Cti::identifyExecutable(CompileInfo);

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

void InitDispatchGlobals(void)
{
    string str;
    char var[128];

    strcpy(var, "DISPATCH_RELOAD_RATE");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        gDispatchReloadRate = std::max((unsigned long)gConfigParms.getValueAsULong(var), (unsigned long)86400);
    }

    strcpy(var, "DISPATCH_DEBUGLEVEL");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        char *eptr;
        gDispatchDebugLevel = strtoul(str.c_str(), &eptr, 16);
        CTILOG_DEBUG(dout, "DISPATCH_DEBUGLEVEL: 0x"<< hex << gDispatchDebugLevel);
    }
}
