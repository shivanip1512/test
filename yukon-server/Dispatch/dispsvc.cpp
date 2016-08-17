#include "precompiled.h"

#include "dispsvc.h"
#include "dlldefs.h"

#include <boost/thread.hpp>

#include <iostream>
using namespace std;

extern BOOL  bGCtrlC;
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


CtiDispatchService::~CtiDispatchService()
{
  //intentionally ignore PCLint warning about _myargv cleanup
}

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
    boost::thread _dispatchThread(DispatchMainFunction, _myargc, _myargv);

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




