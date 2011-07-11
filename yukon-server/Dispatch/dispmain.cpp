/*-----------------------------------------------------------------------------*
*
* File:   dispmain
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/dispmain.cpp-arc  $
* REVISION     :  $Revision: 1.12.12.3 $
* DATE         :  $Date: 2008/11/20 20:37:41 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

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
#include "logger.h"
#include "utility.h"

#include "dbghelp.h"

extern INT DispatchMainFunction(INT, CHAR**);

int main(int argc, char* argv[] )
{
   BOOL bConsole;
   LPTSTR szName = "Dispatch";
   LPTSTR szDisplay = "Yukon Dispatch Service";

   HANDLE hExclusion = INVALID_HANDLE_VALUE;

   if( (hExclusion = OpenEvent(EVENT_ALL_ACCESS, FALSE, szName)) != NULL )
   {
       // Oh no, dispatch is running on this machine already.
       CloseHandle(hExclusion);
       cout << "Dispatch is already running!!!" << endl;
       Sleep(15000);
       return(-1);
   }

   hExclusion = CreateEvent(NULL, TRUE, FALSE, szName);

   if( hExclusion == (HANDLE)NULL )
   {
       cout << "Couldn't create " << szName << " Event Object" << endl;
       return(-1);
   }

   InitDispatchGlobals();

   dout.start();     // fire up the logger thread
   dout.setOwnerInfo(CompileInfo);
   dout.setOutputPath(gLogDirectory);
   dout.setOutputFile("dispatch");
   dout.setToStdOut(true);
   dout.setWriteInterval(15000);

   identifyProject(CompileInfo);

   if( setConsoleTitle(CompileInfo) )
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
         dout.setWriteInterval(0);

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

   dout.interrupt(CtiThread::SHUTDOWN);
   dout.join();

   if(hExclusion != INVALID_HANDLE_VALUE) CloseHandle(hExclusion);


   return 0;
}
