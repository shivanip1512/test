/*-----------------------------------------------------------------------------*
*
* File:   dispmain
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/dispmain.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:22 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <windows.h>
#include <iostream>
using namespace std;

#include <rw\rwtime.h>
#include <rw/thr/thrfunc.h>
#include <rw/thr/thrutil.h>

#include "dispsvc.h"
#include "dllvg.h"
#include "CServiceConfig.h"
#include "dllbase.h"
#include "ctibase.h"
#include "logger.h"
#include "utility.h"

extern INT DispatchMainFunction(INT, CHAR**);

int main(int argc, char* argv[] )
{
   BOOL bConsole;
   LPTSTR szName = "Dispatch";
   LPTSTR szDisplay = "Yukon Dispatch Service";

   InitDispatchGlobals();

   dout.start();     // fire up the logger thread
   dout.setOutputPath(gLogDirectory.data());
   dout.setOutputFile("dispatch");
   dout.setToStdOut(true);
   dout.setWriteInterval(0);

   identifyProject(CompileInfo);

   {
      char tstr[256];
      sprintf(tstr,"Dispatch - YUKON (Build %d.%d.%d)", CompileInfo.major, CompileInfo.minor, CompileInfo.build);
      bConsole = SetConsoleTitle( tstr );
   }

   if( bConsole )
   {
      //Process command line
      if( argc > 1 && strcmp(argv[1], "-install") == 0  )
      {
         cout << RWTime()  << " - Installing Yukon Dispatch Service" << endl;
         CServiceConfig si(szName, szDisplay);
         si.Install(SERVICE_WIN32_OWN_PROCESS,
                    SERVICE_DEMAND_START,
                    NULL,
                    NULL,   // Use LocalSystem Account
                    NULL );

         return 0;

      }
      else if( argc > 1 && strcmp(argv[1], "-remove" ) == 0 )
      {
         cout << RWTime()  << " - Removing CTI Dispatch Service" << endl;
         CServiceConfig si(szName, szDisplay);
         si.Remove();
         return 0;
      }
      else
      {
         CtiDispatchService service(szName, szDisplay, SERVICE_WIN32_OWN_PROCESS );
         service.RunInConsole(argc, argv );

         //while( service.GetStatus() == SERVICE_STOPPED ) rwSleep(1000);
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

   dout.interrupt(CtiThread::SHUTDOWN);
   dout.join();

   return 0;
}
