#include "yukon.h"

#include <iostream>
using namespace std;

#include <rw/thr/thrfunc.h>
#include "ctitime.h"

#include "dlldefs.h"
#include "ccusimsvc.h"
#include "ctibase.h"
#include "utility.h"

namespace Cti {
namespace Simulator {

extern int SimulatorMainFunction(int argc, char** argv);

}
}

extern bool gQuit;

BOOL WINAPI CtrlHandler(DWORD fdwCtrlType)
{
    switch( fdwCtrlType )
    {
    case CTRL_C_EVENT:
    case CTRL_SHUTDOWN_EVENT:
    case CTRL_CLOSE_EVENT:
    case CTRL_BREAK_EVENT:

        {
            gQuit = true;
            return TRUE;
        }
    case CTRL_LOGOFF_EVENT:
    default:
        {
            return FALSE;
        }
    }
}

IMPLEMENT_SERVICE(CtiSimulatorService, CCUSIMULATOR)

CtiSimulatorService::CtiSimulatorService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType ) :
_myargc(0),
_myargv(NULL),
_quit(false),
CService( szName, szDisplay, dwType )
{
   m_pThis = this;        // Allow the base class to know who this is
}

void CtiSimulatorService::RunInConsole(DWORD argc, LPTSTR* argv)
{
   CService::RunInConsole(argc, argv);

   //We need to catch ctrl-c so we can stop
   if(!SetConsoleCtrlHandler((PHANDLER_ROUTINE) CtrlHandler,  TRUE))
      cerr << "Could not install control handler" << Cti::endl;

   Init();
   Run();
   OnStop();
}

void CtiSimulatorService::Init()
{
}

void CtiSimulatorService::DeInit()
{
    CService::DeInit();
}

void CtiSimulatorService::OnStop()
{
   SetStatus(SERVICE_STOPPED);
   _quit = true;
}

void CtiSimulatorService::Run()
{
   SetStatus(SERVICE_START_PENDING, 33, 5000 );

   // Start simulator
   RWThreadFunction _simulatorThread = rwMakeThreadFunction( Cti::Simulator::SimulatorMainFunction, _myargc, _myargv );
   _simulatorThread.start();

   SetThreadName(-1, "SimulatorSvc ");   

   // set service as running Now
   SetStatus(SERVICE_RUNNING, 0, 0, SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN );

   _simulatorThread.join();

   SetStatus(SERVICE_STOP_PENDING, 50, 40000 );
}

void CtiSimulatorService::ParseArgs(DWORD argc, LPTSTR* argv)
{
   //Read the config file name if it is available
   _myargc = argc;
   _myargv = argv;
}

CtiSimulatorService::~CtiSimulatorService() 
{
    _myargv = NULL;
}
