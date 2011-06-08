/*-----------------------------------------------------------------------------
    Filename:  ccservice.cpp

    Programmer:  Josh Wolberg

    Description:  Source file for CtiCCService.

    Initial Date:  9/04/2001

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#include "yukon.h"
#include <io.h>

#include "id_capcontrol.h"
#include "ccservice.h"
#include "eventlog.h"
#include "configparms.h"
#include "rtdb.h"
#include "ccsubstationbus.h"
#include "logger.h"
#include "ctitime.h"
#include "thread_monitor.h"

using std::endl;

extern void refreshGlobalCParms();

extern ULONG _CC_DEBUG;

CtiDate gInvalidCtiDate = CtiDate(1,1, 1990);
CtiTime gInvalidCtiTime = CtiTime(gInvalidCtiDate,0,0,0);
ULONG gInvalidCtiTimeSeconds = gInvalidCtiTime.seconds();


//Use this to indicate globally when ctrl-c was pressed
//Kinda ugly... The Run() member function watches this
//To know when to bail
bool capcontrol_do_quit = false;

/* CtrlHandler handles is used to catch ctrl-c in the case where macs is being
   run in a console */
bool CtrlHandler(DWORD fdwCtrlType)
{
    switch (fdwCtrlType)
    {

    /* Handle the CTRL+C signal. */

    case CTRL_C_EVENT:
    case CTRL_SHUTDOWN_EVENT:
    case CTRL_CLOSE_EVENT:
    case CTRL_BREAK_EVENT:

        capcontrol_do_quit = TRUE;
        Sleep(30000);
        return TRUE;

        /* CTRL+CLOSE: confirm that the user wants to exit. */
        /* Pass other signals to the next handler. */

    case CTRL_LOGOFF_EVENT:


    default:
        return FALSE;

    }

}


IMPLEMENT_SERVICE(CtiCCService, CAPCONTROL)

CtiCCService::CtiCCService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType )
: CService( szName, szDisplay, dwType ), _quit(false)
{
    m_pThis = this;
}


void CtiCCService::RunInConsole(DWORD argc, LPTSTR* argv)
{
    CService::RunInConsole(argc, argv);

    //We need to catch ctrl-c so we can stop
    if (!SetConsoleCtrlHandler((PHANDLER_ROUTINE) CtrlHandler,  TRUE))
        std::cerr << "Could not install console control handler" << endl;

    Init();
    Run();

    OnStop();

    ThreadMonitor.interrupt(CtiThread::SHUTDOWN);
    ThreadMonitor.join();

    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();
    SetStatus(SERVICE_STOPPED);
}

void CtiCCService::Init()
{
    dout.setOwnerInfo(CompileInfo);
    dout.setOutputFile("capcontrol");
    dout.setOutputPath(gLogDirectory);
    dout.setToStdOut(true);
    dout.setWriteInterval(1);
    dout.start();     // fire up the logger thread

    ThreadMonitor.start();

    refreshGlobalCParms();

    _quit = false;
}

void CtiCCService::DeInit()
{
    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Cap Control shutdown" << endl;
    }
    CService::DeInit();
}

void CtiCCService::OnStop()
{
    SetStatus(SERVICE_STOP_PENDING, 2, 5000 );

    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Cap Control shutting down...." << endl;
    }

    //Time to quit - send a shutdown message through the system
    CtiCCExecutorFactory::createExecutor(new CtiCCShutdown())->execute();

    SetStatus(SERVICE_STOP_PENDING, 50, 5000 );

    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Cap Control shut down!" << endl;
    }

    SetStatus(SERVICE_STOP_PENDING, 75, 5000 );

    _quit = true;
}

void CtiCCService::Run()
{

    SetStatus(SERVICE_START_PENDING, 1, 5000 );

    //Make sure the database gets hit so we'll know if the database
    //connection is legit now rather than later
    bool trouble = false;

    do
    {
        if ( trouble )
            Sleep(1000);

        {
            CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
            RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

            CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds(), true);
            if ( !store->isValid() )
            {
                trouble = true;
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Unable to obtain connection to database...will keep trying." << endl;
            }
            else
            {
                trouble = false;
            }
        }
    }
    while ( trouble );

    SetStatus(SERVICE_START_PENDING, 33, 5000 );

    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Starting cap controller thread..." << endl;
    }
    CtiCapController* controller = CtiCapController::getInstance();
    controller->start();

    SetStatus(SERVICE_START_PENDING, 66, 5000 );

    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Starting client listener thread..." << endl;
    }
    CtiCCClientListener* clientListener = CtiCCClientListener::getInstance();
    clientListener->start();

    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Cap Control started." << endl;
    }*/

    SetStatus(SERVICE_RUNNING, 0, 0,
              SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN );

    while ( !_quit && !capcontrol_do_quit )
    {
        Sleep(500);
    }


    SetStatus(SERVICE_STOP_PENDING, 50, 5000 );
}

void CtiCCService::ParseArgs(DWORD argc, LPTSTR* argv)
{
    //Read the config file name if it is available
    if ( argc > 1 )
    {
        _config_file = argv[1];
    }
}

