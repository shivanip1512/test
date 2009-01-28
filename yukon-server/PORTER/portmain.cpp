/*-----------------------------------------------------------------------------*
*
* File:   portmain
*
* Date:   2/2/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.14.14.2 $
* DATE         :  $Date: 2008/11/21 16:14:53 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <iostream>


#include "ctitime.h"
#include <rw/thr/thrutil.h>

#include "portsvc.h"
#include "CServiceConfig.h"
#include "ctibase.h"
#include "portglob.h"
#include "cparms.h"
#include "configparms.h"

#include "logger.h"
#include "guard.h"

using namespace std;

int install(DWORD dwStart = SERVICE_DEMAND_START);
int remove();

// A global variable to indicate the service status..

extern int PorterMainFunction (int argc, char **argv);

INT RunningInConsole = FALSE;
LPTSTR szServiceName = "Porter";
LPTSTR szDisplayName = "Yukon Port Control Service";
LPTSTR szDesc = "Controls communications to field devices";

int main(int argc, char* argv[] )
{
   InitYukonBaseGlobals();

   dout.start();     // fire up the logger thread
   dout.setOwnerInfo(CompileInfo);
   dout.setOutputPath(gLogDirectory);
   dout.setOutputFile("porter");
   dout.setToStdOut(true);
   dout.setWriteInterval(15000);

   string dbglogdir(gLogDirectory + "\\Debug");
   // Create a subdirectory called Comm beneath Log.
   CreateDirectoryEx( gLogDirectory.c_str(), dbglogdir.c_str(), NULL);

   slog.start();     // fire up the simulator thread
   slog.setOwnerInfo(CompileInfo);
   slog.setOutputPath(dbglogdir.c_str());
   slog.setOutputFile("simulate");
   slog.setToStdOut( (bool)(gConfigParms.getValueAsInt("YUKON_SIMULATE_TOSTDOUT",0)) );
   slog.setWriteInterval(15000);

   blog.start();
   blog.setOwnerInfo(CompileInfo);
   blog.setOutputPath(dbglogdir.c_str());
   blog.setOutputFile("comstats");
   blog.setToStdOut( false );
   blog.setWriteInterval(15000);

   {
       CtiLockGuard<CtiLogger> doubt_guard(slog);
       slog << endl << CtiTime() << " **** Simulator Started **** " << endl;
   }

   if( SetConsoleTitle("Port Control") ) // We are a console application
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
    char depend[1000];

    memset(depend, 0, 1000 );

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime()  << " - Installing as a service..." << endl;
    }


    // Attempt to determine any services we are going to be dependent on
    HINSTANCE hLib = LoadLibrary("cparms.dll");

   if (hLib)
   {
       CPARM_GETCONFIGSTRING   fpGetAsString = (CPARM_GETCONFIGSTRING)GetProcAddress( hLib, "getConfigValueAsString" );

       if ( (*fpGetAsString)("SERVICE_DEPENDENCIES", depend, 1000) )
       {
           CtiLockGuard<CtiLogger> doubt_guard(dout);
           dout << "Service is dependent on the following services:" << endl
                << depend << endl;
       }
       else
       {
           depend[0] = NULL;
           CtiLockGuard<CtiLogger> doubt_guard(dout);
           dout << "Couldn't locate any services that this service is to be dependent upon" << endl
                << "installing anyways" << endl;
       }

   }


   char* tmp = depend;

   //replace whitespace with '\0'
   while( (tmp = strchr( tmp, ' ')) != NULL )
       *tmp = '\0';

    CServiceConfig si(szServiceName, szDisplayName, szDesc);

    //check whether or not we found dependencies
    if( depend[0] == NULL )
        tmp = NULL;
    else
        tmp = depend;


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Installing porter Service Using LocalSystem Account." << endl;
    }


    // test using the LocalSystem account
    si.Install(SERVICE_WIN32_OWN_PROCESS,
                       dwStart,
                       tmp,
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

