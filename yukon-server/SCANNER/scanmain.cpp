/*-----------------------------------------------------------------------------*
*
* File:   scanmain
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.10.14.2 $
* DATE         :  $Date: 2008/11/21 16:14:53 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <iostream>
using namespace std;

#include "ctitime.h"
#include <rw/thr/thrfunc.h>
#include <rw/thr/thrutil.h>

#include "scansvc.h"
#include "CServiceConfig.h"
#include "dllbase.h"
#include "ctibase.h"
#include "logger.h"
#include "thread_monitor.h"

extern INT ScannerMainFunction(INT, CHAR**);

int main(int argc, char* argv[] )
{
   LPTSTR szName = "Scanner";
   LPTSTR szDisplay = "Yukon Real-Time Scan Service";
   LPTSTR szDesc = "Manages the periodic - timed scanning of field devices";

   dout.start();     // fire up the logger thread
   dout.setOwnerInfo(CompileInfo);
   dout.setOutputPath(gLogDirectory);
   dout.setRetentionLength(gLogRetention);
   dout.setOutputFile("scanner");
   dout.setToStdOut(true);
   dout.setWriteInterval(15000);

   ThreadMonitor.start();

   if( SetConsoleTitle("Scanner") )
   {
      //Process command line
      if( argc > 1 && strcmp(argv[1], "-install") == 0  )
      {
         dout << CtiTime()  << " - Installing Yukon Real-Time Scan Service" << endl;
         CServiceConfig si(szName, szDisplay, szDesc);
         si.Install(SERVICE_WIN32_OWN_PROCESS,
                    SERVICE_DEMAND_START,
                    NULL,
                    NULL,   // Use the LocalSystem Account
                    NULL);
      }
      else if( argc > 1 && strcmp(argv[1], "-auto") == 0  )
      {
         dout << CtiTime()  << " - Installing Yukon Real-Time Scan Service" << endl;
         CServiceConfig si(szName, szDisplay, szDesc);
         si.Install(SERVICE_WIN32_OWN_PROCESS,
                    SERVICE_AUTO_START,
                    NULL,
                    NULL,   // Use the LocalSystem Account
                    NULL);
      }
      else if( argc > 1 && strcmp(argv[1], "-remove" ) == 0 )
      {
         dout << CtiTime()  << " - Removing Yukon Real-Time Scanner Service" << endl;
         CServiceConfig si(szName, szDisplay);
         si.Remove();
      }
      else
      {
         dout.setWriteInterval(0);

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

   ThreadMonitor.interrupt(CtiThread::SHUTDOWN);
   ThreadMonitor.join();

   // Make sure all the logs get output and done!
   dout.interrupt(CtiThread::SHUTDOWN);
   dout.join();

   return 0;
}
