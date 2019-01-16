#include "precompiled.h"

#include <io.h>

#include "lmservice.h"
#include "id_loadmanagement.h"
#include "eventlog.h"
#include "rtdb.h"
#include "logManager.h"
#include "ctidate.h"
using std::transform;
using std::string;
using std::endl;
using std::vector;
//Boolean if debug messages are printed
ULONG _LM_DEBUG = 0L;
//Boolean if point enevts messages are created and sent to Dispatch
BOOL _LM_POINT_EVENT_LOGGING = FALSE;

CtiTime gInvalidCtiTime = CtiTime(CtiDate(1,1,1990),0,0,0);
ULONG gInvalidCtiTimeSeconds = gInvalidCtiTime.seconds();

CtiTime gEndOfCtiTime = CtiTime(CtiDate(1,1,2035),0,0,0);
ULONG gEndOfCtiTimeSeconds = gEndOfCtiTime.seconds();

//Use this to indicate globally when ctrl-c was pressed
//Kinda ugly... The Run() member function watches this
//To know when to bail
bool load_management_do_quit = false;

/* CtrlHandler handles is used to catch ctrl-c in the case where macs is being
   run in a console */
bool CtrlHandler(DWORD fdwCtrlType)
{
    switch( fdwCtrlType )
    {

    /* Handle the CTRL+C signal. */

    case CTRL_C_EVENT:
    case CTRL_SHUTDOWN_EVENT:
    case CTRL_CLOSE_EVENT:
    case CTRL_BREAK_EVENT:

        load_management_do_quit = TRUE;
        return TRUE;

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
    if( !SetConsoleCtrlHandler((PHANDLER_ROUTINE) CtrlHandler,  TRUE) )
        std::cerr << "Could not install console control handler" << endl;

    Init();
    Run();

    OnStop();
}

void CtiLMService::Init()
{
    SetStatus(SERVICE_START_PENDING, 1, 5000 );

    doutManager.setDefaultOptions(CompileInfo, gConfigParms.getValueAsString("LOAD_MANAGEMENT_LOG_FILE", "loadmanagement"));
    doutManager.start();     // fire up the logger thread

    string str;
    char var[128];

    _LM_DEBUG = LM_DEBUG_NONE;//default

    strcpy(var, "LOAD_MANAGEMENT_DEBUG");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        transform(str.begin(), str.end(), str.begin(), tolower);
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
                _LM_DEBUG = strtoul(str.c_str(), &eptr, 16);
            }
        }

        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    strcpy(var, "LOAD_MANAGEMENT_POINT_EVENT_LOGGING");
    if( !(str = gConfigParms.getValueAsString(var)).empty() )
    {
        transform(str.begin(), str.end(), str.begin(), tolower);
        _LM_POINT_EVENT_LOGGING = (str=="true"?TRUE:FALSE);
        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, var << ":  " << str);
        }
    }
    else
    {
        CTILOG_INFO(dout, "Unable to obtain '" << var << "' value from cparms.");
    }

    SET_CRT_OUTPUT_MODES;
    if( gConfigParms.isOpt("DEBUG_MEMORY") && gConfigParms.isTrue("DEBUG_MEMORY") )
        ENABLE_CRT_SHUTDOWN_CHECK;

    _quit = false;
}

void CtiLMService::DeInit()
{
    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Load Management shutdown");
    }
    CService::DeInit();
}

void CtiLMService::OnStop()
{
    Cti::Messaging::BaseConnection::stopReconnects();

    SetStatus(SERVICE_STOP_PENDING, 2, 5000 );

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Load Management shutting down....");
    }

    try
    {
        CtiLoadManager::getInstance()->stop();
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    try
    {
        CtiLMClientListener::getInstance().stop();
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    try
    {
        CtiLMControlAreaStore::deleteInstance();
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    SetStatus(SERVICE_STOP_PENDING, 50, 5000 );

    if( _LM_DEBUG & LM_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Load Management shut down!");
    }

    SetStatus(SERVICE_STOP_PENDING, 75, 30000 );

    _quit = true;
}

void CtiLMService::Run()
{
    SetStatus(SERVICE_RUNNING, 0, 0,
              SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN | SERVICE_ACCEPT_PRESHUTDOWN );

    Cti::identifyExecutable(CompileInfo);

    try
    {
        // Make sure the database is available before we try to load anything from it.
        {
            bool writeLogMessage = true;

            while ( ! ( _quit || load_management_do_quit || canConnectToDatabase() ) )
            {
                if ( writeLogMessage )
                {
                    CTILOG_ERROR(dout, "Database connection attempt failed");

                    writeLogMessage = false;
                }
                Sleep( 5000 );
            }
            if ( _quit || load_management_do_quit )
            {
                return;
            }
        }

        //Make sure the database gets hit so we'll know if the database
        //connection is legit now rather than later
        bool trouble = false;

        do
        {
            if( trouble )
                Sleep(5000);

            CtiLMControlAreaStore* store = CtiLMControlAreaStore::getInstance();
            {
                vector<CtiLMControlArea*>* controlAreas = store->getControlAreas(CtiTime());


                if( controlAreas == NULL || controlAreas->empty() )
                {
                    store->setValid(false);
                    trouble = true;
                    CTILOG_WARN(dout, "Unable to obtain a connection to the database or no control areas exist...will keep trying.");
                }
                else
                {
                    trouble = false;
                }
            }
        } while( trouble && !_quit && !load_management_do_quit ); //!quit and !load_management_do_quit added to make this not lock us up.

        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, "Starting client listener thread...");
        }
        CtiLMClientListener::getInstance().start();

        if( _LM_DEBUG & LM_DEBUG_STANDARD )
        {
            CTILOG_DEBUG(dout, "Starting load manager thread...");
        }
        CtiLoadManager* manager = CtiLoadManager::getInstance();
        manager->start();

        /*{
            CTILOG_INFO(dout, "Load management started.");
        }*/

        while( !_quit && !load_management_do_quit )
        {
            Sleep(500);
        }
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    SetStatus(SERVICE_STOPPED);
}

void CtiLMService::ParseArgs(DWORD argc, LPTSTR* argv)
{
    //Read the config file name if it is available
    if( argc > 1 )
    {
        _config_file = argv[1];
    }
}
