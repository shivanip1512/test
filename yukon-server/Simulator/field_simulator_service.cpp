#include "precompiled.h"

#include "ctitime.h"

#include "dlldefs.h"
#include "field_simulator_service.h"
#include "dllbase.h"
#include "utility.h"

#include <boost/thread.hpp>

#include <iostream>
using namespace std;

extern bool gQuit;
extern HANDLE gQuitEvent;

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
            SetEvent(gQuitEvent);
            return TRUE;
        }
    case CTRL_LOGOFF_EVENT:
    default:
        {
            return FALSE;
        }
    }
}

namespace Cti {
namespace Simulator {
        
extern int SimulatorMainFunction(int argc, char** argv);

IMPLEMENT_SERVICE(FieldSimulatorService, FIELDSIMULATOR)

FieldSimulatorService::FieldSimulatorService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType ) :
_myargc(0),
_myargv(NULL),
_quit(false),
CService( szName, szDisplay, dwType )
{
   m_pThis = this;        // Allow the base class to know who this is
}

void FieldSimulatorService::RunInConsole(DWORD argc, LPTSTR* argv)
{
   CService::RunInConsole(argc, argv);

   //We need to catch ctrl-c so we can stop
   if(!SetConsoleCtrlHandler((PHANDLER_ROUTINE) CtrlHandler,  TRUE))
      cerr << "Could not install control handler" << endl;

   Init();
   Run();
   OnStop();
}

void FieldSimulatorService::Init()
{
}

void FieldSimulatorService::DeInit()
{
    CService::DeInit();
}

void FieldSimulatorService::OnStop()
{
   SetStatus(SERVICE_STOPPED);
   _quit = true;
}

void FieldSimulatorService::Run()
{
   SetStatus(SERVICE_START_PENDING, 33, 5000 );

   // Start simulator
   boost::thread simulatorThread( Cti::Simulator::SimulatorMainFunction, _myargc, _myargv );

   SetThreadName(-1, "SimulatorSvc ");

   // set service as running Now
   SetStatus(SERVICE_RUNNING, 0, 0, SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN );

   simulatorThread.join();

   SetStatus(SERVICE_STOP_PENDING, 50, 40000 );
}

void FieldSimulatorService::ParseArgs(DWORD argc, LPTSTR* argv)
{
   //Read the config file name if it is available
   _myargc = argc;
   _myargv = argv;
}

FieldSimulatorService::~FieldSimulatorService()
{
    _myargv = NULL;
}

}
}