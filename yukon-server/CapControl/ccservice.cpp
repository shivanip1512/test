#include "precompiled.h"

#include "id_capcontrol.h"
#include "ccservice.h"
#include "dllBase.h"
#include "ccsubstationbus.h"
#include "logManager.h"
#include "ctitime.h"
#include "thread_monitor.h"
#include "ExecutorFactory.h"
#include "ccclientlistener.h"
#include "capcontroller.h"
#include "ccsubstationbusstore.h"

using std::endl;

extern void refreshGlobalCParms();

extern unsigned long _CC_DEBUG;

CtiDate gInvalidCtiDate = CtiDate(1,1, 1990);
CtiTime gInvalidCtiTime = CtiTime(gInvalidCtiDate,0,0,0);
unsigned long gInvalidCtiTimeSeconds = gInvalidCtiTime.seconds();


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

        capcontrol_do_quit = true;
        Sleep(30000);
        return true;

        /* CTRL+CLOSE: confirm that the user wants to exit. */
        /* Pass other signals to the next handler. */

    case CTRL_LOGOFF_EVENT:


    default:
        return false;

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
}

void CtiCCService::Init()
{
    SetStatus(SERVICE_START_PENDING, 33, 5000 );

    doutManager.setOwnerInfo    (CompileInfo);
    doutManager.setOutputFile   (gConfigParms.getValueAsString("CAP_CONTROL_LOG_FILE", "capcontrol"));
    doutManager.setOutputPath   (gLogDirectory);
    doutManager.setRetentionDays(gLogRetention);
    doutManager.setToStdOut     (true);
    doutManager.start();     // fire up the logger thread

    ThreadMonitor.start(CtiThreadMonitor::CapControl);

    refreshGlobalCParms();

    _quit = false;
}

void CtiCCService::DeInit()
{
    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Cap Control shutdown");
    }
    CService::DeInit();
}

void CtiCCService::OnStop()
{
    Cti::Messaging::BaseConnection::stopReconnects();

    SetStatus(SERVICE_STOP_PENDING, 2, 5000 );

    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Cap Control shutting down....");
    }

    //Time to quit - send a shutdown message through the system
    CtiCCExecutorFactory::createExecutor(new CtiCCShutdown())->execute();

    SetStatus(SERVICE_STOP_PENDING, 50, 5000 );

    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Cap Control shut down!");
    }

    SetStatus(SERVICE_STOP_PENDING, 75, 5000 );

    _quit = true;
}

void CtiCCService::Run()
{
    SetStatus(SERVICE_RUNNING, 0, 0,
              SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN | SERVICE_ACCEPT_PRESHUTDOWN );

    Cti::identifyExecutable(CompileInfo);

    // Make sure the database is available before we try to load anything from it.
    {
        bool writeLogMessage = true;

        while ( ! ( _quit || capcontrol_do_quit || canConnectToDatabase() ) )
        {
            if ( writeLogMessage )
            {
                CTILOG_ERROR(dout, "Database connection attempt failed.");

                writeLogMessage = false;
            }
            Sleep( 5000 );
        }
        if ( _quit || capcontrol_do_quit )
        {
            return;
        }
    }

    //Make sure the database gets hit so we'll know if the database
    //connection is legit now rather than later
    bool trouble = false;

    // create the BusStore and manually start his threads...
    {
        CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

        store->startThreads();
    }

    do
    {
        if ( trouble )
        {
            Sleep(1000);
        }

        {
            CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
            CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

            if ( ! store->testDatabaseConnectivity() )
            {
                trouble = true;
                CTILOG_ERROR(dout, "Failed database connectivity test, will keep trying.");
            }
            else
            {
                trouble = false;
            }
        }
    }
    while ( trouble );

    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Starting cap controller thread...");
    }
    CtiCapController* controller = CtiCapController::getInstance();
    controller->start();

    if( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_DEBUG(dout, "Starting client listener thread...");
    }
    CtiCCClientListener::getInstance().start();

    /*{
        CTILOG_INFO(dout, "Cap Control started.");
    }*/

    while ( !_quit && !capcontrol_do_quit )
    {
        Sleep(500);
    }

    SetStatus(SERVICE_STOP_PENDING, 50, 5000 );

    SetStatus(SERVICE_STOPPED);
}

void CtiCCService::ParseArgs(DWORD argc, LPTSTR* argv)
{
    //Read the config file name if it is available
    if ( argc > 1 )
    {
        _config_file = argv[1];
    }
}

