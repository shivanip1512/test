/*-----------------------------------------------------------------------------*
*
* File:   portsvc
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/02/10 23:23:55 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <iostream>
using namespace std;

#include <rw/thr/thrfunc.h>
#include <rw/rwtime.h>

#include "dlldefs.h"
#include "portsvc.h"
#include "ctibase.h"
#include "portglob.h"

extern INT PorterMainFunction(INT argc, CHAR** argv);

/* CtrlHandler handles is used to catch ctrl-c when run in a console */
BOOL CtrlHandler(DWORD fdwCtrlType)
{
   switch(fdwCtrlType)
   {
   case CTRL_C_EVENT:
   case CTRL_SHUTDOWN_EVENT:
   case CTRL_CLOSE_EVENT:
   case CTRL_BREAK_EVENT:
   case CTRL_LOGOFF_EVENT:

      SetEvent( hPorterEvents[P_QUIT_EVENT] );
      PorterQuit = TRUE;
      Sleep(30000);
      return TRUE;

   default:
      return FALSE;
   }
}

IMPLEMENT_SERVICE(CtiPorterService, Porter)

CtiPorterService::CtiPorterService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType ) :
_myargc(0),
_myargv(NULL),
_quit(false),
CService( szName, szDisplay, dwType )
{
   m_pThis = this;        // Allow the base class to know who this is
}

void CtiPorterService::RunInConsole(DWORD argc, LPTSTR* argv)
{
   CService::RunInConsole(argc, argv);

   //We need to catch ctrl-c so we can stop
   if(!SetConsoleCtrlHandler((PHANDLER_ROUTINE) CtrlHandler,  TRUE))
      cerr << "Could not install control handler" << endl;

   Init();
   Run();
   OnStop();
}

void CtiPorterService::Init()
{
}

void CtiPorterService::DeInit()
{
    CService::DeInit();
}

void CtiPorterService::OnStop()
{
   SetEvent(hPorterEvents[P_QUIT_EVENT]);

   _quit = true;
   PorterQuit = TRUE;

   SetStatus( SERVICE_STOP_PENDING, 1, 10000 );
}

void CtiPorterService::Run()
{
   SetStatus(SERVICE_START_PENDING, 33, 5000 );

   //Start porter
   RWThreadFunction _porterThread = rwMakeThreadFunction( PorterMainFunction, _myargc, _myargv );
   _porterThread.start();

   // set service as running Now
   SetStatus(SERVICE_RUNNING, 0, 0, SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN );

   WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], INFINITE);

   SetStatus( SERVICE_STOP_PENDING, 50, 40000 );

   _porterThread.join();
}

void CtiPorterService::ParseArgs(DWORD argc, LPTSTR* argv)
{
   //Read the config file name if it is available
   _myargc = argc;
   _myargv = argv;
}

//CtiPorterService::CtiPorterService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType);

CtiPorterService::~CtiPorterService() {}


