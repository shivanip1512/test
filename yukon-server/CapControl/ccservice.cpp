/*-----------------------------------------------------------------------------
    Filename:  ccservice.cpp

    Programmer:  Josh Wolberg

    Description:  Source file for CtiCCService.

    Initial Date:  9/04/2001

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#include "yukon.h"
#include <io.h>

#include "ccservice.h"
#include "eventlog.h"
#include "configparms.h"
#include "rtdb.h"
#include "ccsubstationbus.h"
#include "logger.h"

//Boolean if debug messages are printed
ULONG _CC_DEBUG;
//Boolean if we ignore non normal qualities
BOOL _IGNORE_NOT_NORMAL_FLAG;
ULONG _SEND_TRIES;

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
        cerr << "Could not install console control handler" << endl;

    Init();
    Run();

    OnStop();

    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();
    SetStatus(SERVICE_STOPPED);
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

    _CC_DEBUG = CC_DEBUG_NONE;

    strcpy(var, "CAP_CONTROL_DEBUG");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
        str.toLower();
        _CC_DEBUG = (str=="true"?(CC_DEBUG_STANDARD|CC_DEBUG_POINT_DATA):CC_DEBUG_NONE);

        if( !_CC_DEBUG )
        {
            if( str=="false" )
            {
                _CC_DEBUG = CC_DEBUG_NONE;
            }
            else
            {
                char *eptr;
                _CC_DEBUG = strtoul(str.data(), &eptr, 16);
            }
        }

        if( _CC_DEBUG & CC_DEBUG_STANDARD )
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

    dout.setOutputFile("capcontrol");

    strcpy(var, "CAP_CONTROL_LOG_FILE");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
        dout.setOutputFile(str.data());
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
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

    _IGNORE_NOT_NORMAL_FLAG = FALSE;
    
    strcpy(var, "CAP_CONTROL_IGNORE_NOT_NORMAL");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
        str.toLower();
        _IGNORE_NOT_NORMAL_FLAG = (str=="true"?TRUE:FALSE);
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
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

    _SEND_TRIES = 1;

    strcpy(var, "CAP_CONTROL_SEND_RETRIES");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
        _SEND_TRIES = atoi(str.data())+1;
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
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
    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Cap Control shutdown" << endl;
    }
    CService::DeInit();
}

void CtiCCService::OnStop()
{
    SetStatus(SERVICE_STOP_PENDING, 2, 5000 );

    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Cap Control shutting down...." << endl;
    }

    //Time to quit - send a shutdown message through the system
    CtiCCExecutorFactory f;
    CtiCCExecutor* executor = f.createExecutor(new CtiCCShutdown());
    executor->Execute();

    SetStatus(SERVICE_STOP_PENDING, 50, 5000 );

    delete executor;

    /*if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Cap Control shut down!" << endl;
    }*/

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

            RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());
            if ( !store->isValid() )
            {
                trouble = true;
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Unable to obtain connection to database...will keep trying." << endl;
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
        dout << RWTime() << " - Starting cap controller thread..." << endl;
    }
    CtiCapController* controller = CtiCapController::getInstance();
    controller->start();

    SetStatus(SERVICE_START_PENDING, 66, 5000 );

    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Starting client listener thread..." << endl;
    }
    CtiCCClientListener* clientListener = CtiCCClientListener::getInstance();
    clientListener->start();

    /*{
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Cap Control started." << endl;
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

