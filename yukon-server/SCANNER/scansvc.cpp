/*-----------------------------------------------------------------------------*
*
* File:   scansvc
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/02/10 23:24:03 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <iostream>
using namespace std;

#include <rw/thr/thrfunc.h>

#include "scanglob.h"
#include "scansvc.h"


extern INT ScannerMainFunction(INT argc, CHAR** argv);


BOOL ScannerQuit = false;

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
      ScannerQuit = TRUE;
      SetEvent(hScannerSyncs[S_QUIT_EVENT]);
      Sleep(30000);
      return TRUE;
   default:
      return FALSE;
   }
}

IMPLEMENT_SERVICE(CtiScannerService, Scanner)

CtiScannerService::CtiScannerService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType ) :
_myargc(0),
_myargv(NULL),
CService( szName, szDisplay, dwType ), _quit(false)
{
   m_pThis = this;        // Allow the base class to know who this is
}

void CtiScannerService::RunInConsole(DWORD argc, LPTSTR* argv)
{
   CService::RunInConsole(argc, argv);

   //We need to catch ctrl-c so we can stop
   if(!SetConsoleCtrlHandler((PHANDLER_ROUTINE) CtrlHandler,  TRUE))
      cerr << "Could not install control handler" << endl;

   Init();
   Run();
   OnStop();
}

void CtiScannerService::Init()
{
   _quit = false;
}

void CtiScannerService::DeInit()
{
    CService::DeInit();
}

void CtiScannerService::OnStop()
{
   SetStatus( SERVICE_STOP_PENDING, 1, 20000 );
   _quit = true;
   ScannerQuit = true;
}

void CtiScannerService::Run()
{

   SetStatus(SERVICE_START_PENDING, 33, 5000 );

   //Start scanner
   RWThreadFunction _scannerThread = rwMakeThreadFunction( ScannerMainFunction, _myargc, _myargv );

   _scannerThread.start();

   // set service as running
   SetStatus(SERVICE_RUNNING, 0, 0, SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN );

   _scannerThread.join();
}

void CtiScannerService::ParseArgs(DWORD argc, LPTSTR* argv)
{
   //Read the config file name if it is available
   _myargc = argc;
   _myargv = argv;
}

CtiScannerService::~CtiScannerService() {}

