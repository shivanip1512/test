/*-----------------------------------------------------------------------------
    Filename:  ccservice.cpp

    Programmer:  Josh Wolberg

    Description:  Source file for CtiCCService.

    Initial Date:  9/04/2001

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#include <io.h>

#include "ccserver.h"
#include "ccservice.h"
#include "eventlog.h"
#include "configparms.h"
#include "rtdb.h"
#include "ccsubstationbus.h"
#include "logger.h"

//Boolean if debug messages are printed
BOOL _CC_DEBUG;

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

        capcontrol_do_quit = TRUE;
        Sleep(30000);
        return TRUE;

        /* CTRL+CLOSE: confirm that the user wants to exit. */
        /* Pass other signals to the next handler. */

    case CTRL_BREAK_EVENT:

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
        cerr << "Could not install console control handler" << endl;

    Init();
    Run();

    OnStop();
}

void CtiCCService::Init()
{
    RWCString logFile = "capcontrol";
    dout.start();     // fire up the logger thread
    dout.setOutputPath(gLogDirectory.data());
    dout.setToStdOut(true);
    dout.setWriteInterval(1);

    RWCString str;
    char var[128];

    _CC_DEBUG = FALSE;

    strcpy(var, "CAP_CONTROL_DEBUG");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
        str.toLower();
        _CC_DEBUG = (str=="true"?TRUE:FALSE);
        if( _CC_DEBUG )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    strcpy(var, "CAP_CONTROL_LOG_FILE");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
        dout.setOutputFile(str.data());
        if( _CC_DEBUG )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    _quit = false;
}

void CtiCCService::DeInit()
{
    if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Cap Control shutdown" << endl;
    }
}

void CtiCCService::OnStop()
{
    SetStatus(SERVICE_STOP_PENDING, 2, 5000 );

    if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Cap Control shutting down...." << endl;
    }

    //Time to quit - send a shutdown message through the system
    CtiCCExecutorFactory f;
    RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue = new CtiCountedPCPtrQueue<RWCollectable>();
    CtiCCExecutor* executor = f.createExecutor(new CtiCCShutdown());
    executor->Execute(queue);

    SetStatus(SERVICE_STOP_PENDING, 50, 5000 );

    delete executor;

    if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Cap Control shut down!" << endl;
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

        CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
        RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses();

        if ( !store->isValid() )
        {
            trouble = true;
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Unable to obtain connection to database...will keep trying." << endl;
        }
        else
        {
            if( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Figuring initial estimated var values." << endl;
            }

            for(ULONG i=0;i<ccSubstationBuses.entries();i++)
            {
                CtiCCSubstationBus* current = (CtiCCSubstationBus*)ccSubstationBuses[i];
                //if( current->getEstimatedVarLoadPointId() > 0 )
                //{
                current->figureEstimatedVarLoadPointValue();
                //}
            }
            trouble = false;
        }
    }
    while ( trouble );

    SetStatus(SERVICE_START_PENDING, 33, 5000 );

    if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Starting up the controller thread..." << endl;
    }
    CtiCapController* controller = CtiCapController::getInstance();
    controller->start();

    SetStatus(SERVICE_START_PENDING, 66, 5000 );

    if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Starting up the client connection thread..." << endl;
    }
    CtiCCServer* server = CtiCCServer::getInstance();
    server->start();

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Cap Control started and initialized." << endl;
    }

    SetStatus(SERVICE_RUNNING, 0, 0,
              SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN );

    while ( !_quit && !capcontrol_do_quit)
    {
        Sleep(500);
    }


    SetStatus( SERVICE_STOPPED );
}

void CtiCCService::ParseArgs(DWORD argc, LPTSTR* argv)
{
    //Read the config file name if it is available
    if ( argc > 1 )
    {
        _config_file = argv[1];
    }
}

