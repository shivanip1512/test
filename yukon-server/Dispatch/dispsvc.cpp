#include "yukon.h"



/*-----------------------------------------------------------------------------*
*
* File:   dispsvc
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/dispsvc.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/02/10 23:23:50 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <windows.h>
#include <iostream>
using namespace std;

#include <rw/thr/thrfunc.h>

#include "dispsvc.h"
#include "dlldefs.h"

DLLIMPORT extern BOOL  bGCtrlC;
extern BOOL CtrlHandler(DWORD fdwCtrlType);
extern INT DispatchMainFunction(INT argc, CHAR** argv);

BOOL CtrlHandler(DWORD fdwCtrlType)
{
    switch(fdwCtrlType)
    {

    /* Handle the CTRL+C signal., shutdown,
       and logoff when running in console mode.
    */
    case CTRL_C_EVENT:
    case CTRL_CLOSE_EVENT:
    case CTRL_BREAK_EVENT:
    case CTRL_LOGOFF_EVENT:
    case CTRL_SHUTDOWN_EVENT:
        bGCtrlC = TRUE;
        // I need to assure that the threads go down....
        Sleep(30000);

        return TRUE;
    default:
        return FALSE;

    }

}


CtiDispatchService::~CtiDispatchService() {}

IMPLEMENT_SERVICE(CtiDispatchService, Dispatch)

CtiDispatchService::CtiDispatchService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType ) :
_myargc(0),
_myargv(NULL),
CService( szName, szDisplay, dwType ), _quit(false)
{
    m_pThis = this;        // Allow the base class to know who this is
}

void CtiDispatchService::RunInConsole(DWORD argc, LPTSTR* argv)
{
    CService::RunInConsole(argc, argv);

    //We need to catch ctrl-c, close, logoff- so we can stop
    if(!SetConsoleCtrlHandler((PHANDLER_ROUTINE) CtrlHandler,  TRUE))
        cerr << "Could not install control handler" << endl;

    Init();
    Run();
    OnStop();
}

void CtiDispatchService::Init()
{
    _quit = false;
}

void CtiDispatchService::DeInit()
{
    CService::DeInit();
}

void CtiDispatchService::OnStop()
{
    _quit = true;
    bGCtrlC = TRUE;
    SetStatus( SERVICE_STOP_PENDING, 33, 10000 );
}

void CtiDispatchService::Run()
{

    // SetStatus( SERVICE_RUNNING, 0, 0, SERVICE_ACCEPT_STOP  );
    SetStatus(SERVICE_START_PENDING, 33, 5000 );

    //Start dispatch main thread
    RWThreadFunction _dispatchThread = rwMakeThreadFunction( DispatchMainFunction, _myargc, _myargv );
    _dispatchThread.start();

    // set service as running
    SetStatus(SERVICE_RUNNING, 0, 0,
              SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN );

    _dispatchThread.join();

    SetStatus( SERVICE_STOPPED );
}

void CtiDispatchService::ParseArgs(DWORD argc, LPTSTR* argv)
{
    //Read the config file name if it is available
    _myargc = argc;
    _myargv = argv;
}




