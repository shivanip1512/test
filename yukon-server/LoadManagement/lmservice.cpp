#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <io.h>

#include "lmservice.h"
#include "eventlog.h"
#include "configparms.h"
#include "rtdb.h"
#include "logger.h"

//Boolean if debug messages are printed
ULONG _LM_DEBUG;
//Boolean if point enevts messages are created and sent to Dispatch
BOOL _LM_POINT_EVENT_LOGGING;

//Use this to indicate globally when ctrl-c was pressed
//Kinda ugly... The Run() member function watches this
//To know when to bail
bool load_management_do_quit = false;

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

        load_management_do_quit = TRUE;
        Sleep(30000);
        return TRUE;

        /* CTRL+CLOSE: confirm that the user wants to exit. */
        /* Pass other signals to the next handler. */

    case CTRL_LOGOFF_EVENT:

    default:
        return FALSE;

    }

}


IMPLEMENT_SERVICE(CtiLMService, LOADMANAGEMENT)

CtiLMService::CtiLMService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType )
: CService( szName, szDisplay, dwType ), _quit(false)
{
    m_pThis = this;
}


void CtiLMService::RunInConsole(DWORD argc, LPTSTR* argv)
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

    SetStatus( SERVICE_STOPPED );
}

void CtiLMService::Init()
{
    RWCString logFile = "loadmanagement";
    dout.start();     // fire up the logger thread
    dout.setOutputPath(gLogDirectory.data());
    dout.setToStdOut(true);
    dout.setWriteInterval(1);

    RWCString str;
    char var[128];

    _LM_DEBUG = LM_DEBUG_NONE;//default

    strcpy(var, "LOAD_MANAGEMENT_DEBUG");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
        str.toLower();
        _LM_DEBUG = (str=="true"?(LM_DEBUG_STANDARD|LM_DEBUG_POINT_DATA):LM_DEBUG_NONE);

        if( !_LM_DEBUG )
        {
            if( str=="false" )
            {
                _LM_DEBUG = LM_DEBUG_NONE;
            }
            else
            {
                char *eptr;
                _LM_DEBUG = strtoul(str.data(), &eptr, 16);
            }
        }

        if( _LM_DEBUG & LM_DEBUG_STANDARD )
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

    strcpy(var, "LOAD_MANAGEMENT_LOG_FILE");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
        logFile = str;
        dout.setOutputFile(logFile.data());
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - " << var << ":  " << str << endl;
        }
    }
    else
    {
        dout.setOutputFile(logFile.data());
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Unable to obtain '" << var << "' value from cparms." << endl;
    }

    strcpy(var, "LOAD_MANAGEMENT_POINT_EVENT_LOGGING");
    if( !(str = gConfigParms.getValueAsString(var)).isNull() )
    {
        str.toLower();
        _LM_POINT_EVENT_LOGGING = (str=="true"?TRUE:FALSE);
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
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

void CtiLMService::DeInit()
{
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - Load Management shutdown" << endl;
    }
    CService::DeInit();
}

void CtiLMService::OnStop()
{
    SetStatus(SERVICE_STOP_PENDING, 2, 5000 );

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - Load Management shutting down...." << endl;
    }

    //Time to quit - send a shutdown message through the system
    CtiLMExecutorFactory f;

    CtiLMShutdown* msg = new CtiLMShutdown();
    CtiLMExecutor* executor = f.createExecutor(msg);
    executor->Execute();

    SetStatus(SERVICE_STOP_PENDING, 50, 5000 );

    delete executor;

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - Load Management shut down!" << endl;
    }

    SetStatus(SERVICE_STOP_PENDING, 75, 5000 );

    _quit = true;
}

void CtiLMService::Run()
{
    try
    {
        SetStatus(SERVICE_START_PENDING, 1, 5000 );
        //Make sure the database gets hit so we'll know if the database
        //connection is legit now rather than later
        bool trouble = false;

        do
        {
            if ( trouble )
                Sleep(5000);

            CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
        {
            RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());
            RWOrdered* controlAreas = store->getControlAreas(RWDBDateTime().seconds());


            if ( controlAreas == NULL || controlAreas->entries() == 0 )
            {
                trouble = true;
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Unable to obtain a connection to the database or no control areas exist...will keep trying." << endl;
            }
            else
            {
                trouble = false;
            }
        } 
        } while ( trouble );

        SetStatus(SERVICE_START_PENDING, 33, 5000 );

        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime().asString() << " - Starting load manager thread..." << endl;
        }
        CtiLoadManager* manager = CtiLoadManager::getInstance();
        manager->start();

        SetStatus(SERVICE_START_PENDING, 66, 5000 );

        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime().asString() << " - Starting client listener thread..." << endl;
        }
        CtiLMClientListener* clientListener = CtiLMClientListener::getInstance();
        clientListener->start();

        /*{
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime().asString() << " - Load management started." << endl;
        }*/

        SetStatus(SERVICE_RUNNING, 0, 0,
                  SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN );

        while ( !_quit && !load_management_do_quit)
        {
            Sleep(500);
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiLMService::ParseArgs(DWORD argc, LPTSTR* argv)
{
    //Read the config file name if it is available
    if ( argc > 1 )
    {
        _config_file = argv[1];
    }
}
