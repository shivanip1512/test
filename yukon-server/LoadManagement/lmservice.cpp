#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <io.h>

#include "lmserver.h"
#include "lmservice.h"
#include "eventlog.h"
#include "configparms.h"
#include "rtdb.h"
#include "logger.h"

//Boolean if debug messages are printed
BOOL _LM_DEBUG;

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

        load_management_do_quit = TRUE;
        return TRUE;

        /* CTRL+CLOSE: confirm that the user wants to exit. */
        /* Pass other signals to the next handler. */

    case CTRL_BREAK_EVENT:

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
}

void CtiLMService::Init()
{
    RWCString logFile = "loadmanagement";
    dout.start();     // fire up the logger thread
    dout.setOutputPath(gLogDirectory.data());
    dout.setToStdOut(true);
    dout.setWriteInterval(1);

    char temp[80];

    HINSTANCE hLib = LoadLibrary("cparms.dll");

    if (hLib)
    {
        CPARM_GETCONFIGSTRING   fpGetAsString = (CPARM_GETCONFIGSTRING)GetProcAddress( hLib, "getConfigValueAsString" );

        bool trouble = FALSE;

        if ( (*fpGetAsString)("LOAD_MANAGEMENT_DEBUG", temp, 80) )
        {
            RWCString tempStr(temp);
            tempStr.toLower();

            _LM_DEBUG = (tempStr=="true"?TRUE:FALSE);

            {
              CtiLockGuard<CtiLogger> logger_guard(dout);
              dout << RWTime().asString() << " - LOAD_MANAGEMENT_DEBUG:  " << temp << endl;
            }
        }
        else
            trouble = TRUE;

        if ( (*fpGetAsString)("LOAD_MANAGEMENT_LOG_FILE", temp, 80) )
        {
            logFile = temp;
            dout.setOutputFile(logFile.data());

            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime().asString() << " - "  << " - Load Management using log file: " << temp << endl;
            }
        }
        else
        {
            dout.setOutputFile(logFile.data());
            trouble = TRUE;
        }

        if ( trouble == TRUE )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime().asString() << " - Unable to find a config value in the configuration file! lmservice.cpp::Init()" << endl;
        }

        FreeLibrary(hLib);
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - Unable to load cparms.dll" << endl;
    }

    _quit = false;
}

void CtiLMService::DeInit()
{
    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - Load Management shutdown" << endl;
    }
}

void CtiLMService::OnStop()
{
    SetStatus(SERVICE_STOP_PENDING, 2, 5000 );

    if( _LM_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - Load Management shutting down...." << endl;
    }

    //Time to quit - send a shutdown message through the system
    CtiLMExecutorFactory f;
    RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue = new CtiCountedPCPtrQueue<RWCollectable>();

    CtiLMShutdown* msg = new CtiLMShutdown();
    CtiLMExecutor* executor = f.createExecutor(msg);
    executor->Execute(queue);

    SetStatus(SERVICE_STOP_PENDING, 50, 5000 );

    delete executor;

    if( _LM_DEBUG )
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
                Sleep(1000);

            CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
            RWOrdered* controlAreas = store->getControlAreas();

            if ( !store->isValid() )
            {
                trouble = true;
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime().asString() << " - Unable to obtain connection to database...will keep trying." << endl;
            }
            else
            {
                /*if( _LM_DEBUG )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - Figuring initial actual var values." << endl;
                }
    
                for(ULONG i=0;i<strategies.entries();i++)
                {
                    CtiLMStrategy *current = (CtiCCStrategy*)strategies[i];
                    if( current->ActualVarPointId() > 0 )
                        current->figureActualVarPointValue();
                }*/
                trouble = false;
            }
        }
        while ( trouble );

        SetStatus(SERVICE_START_PENDING, 33, 5000 );

        if( _LM_DEBUG )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime().asString() << " - Starting up the manager thread..." << endl;
        }
        CtiLoadManager* manager = CtiLoadManager::getInstance();
        manager->start();

        SetStatus(SERVICE_START_PENDING, 66, 5000 );

        if( _LM_DEBUG )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime().asString() << " - Starting up the client connection thread..." << endl;
        }
        CtiLMServer* server = CtiLMServer::getInstance();
        server->start();

        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime().asString() << " - Load Management started and initialized." << endl;
        }

        SetStatus(SERVICE_RUNNING, 0, 0,
                  SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN );

        while ( !_quit && !load_management_do_quit)
        {
            Sleep(500);
        }


        SetStatus( SERVICE_STOPPED );
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
