#include "precompiled.h"

#include "cparms.h"
#include "logger.h"
#include "guard.h"
#include "fdrservice.h"
#include "thread_monitor.h"
#include "connection_client.h"
#include "msg_cmd.h"
#include "amq_constants.h"
#include "module_util.h"
#include "ServiceMetricReporter.h"

#include <iostream>
#include <crtdbg.h>
#include <conio.h>

CtiClientConnection FdrVanGoghConnection( Cti::Messaging::ActiveMQ::Queue::dispatch );

bool UserQuit = false;


BOOL MyCtrlHandler( DWORD fdwCtrlType )
{
    switch(fdwCtrlType)
    {
        case CTRL_CLOSE_EVENT:
        case CTRL_C_EVENT:
        case CTRL_BREAK_EVENT:
        case CTRL_LOGOFF_EVENT:
        case CTRL_SHUTDOWN_EVENT:
            UserQuit = true;
            // notify of shutdown
            SetEvent(iShutdown);
            Sleep(30000);
            return TRUE;

        default:
            return FALSE;
    }
}


IMPLEMENT_SERVICE(CtiFDRService, FDR)


CtiFDRService::CtiFDRService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType ) :
    CService( szName, szDisplay, dwType ),
    iGoodStatus(TRUE),
    iInterfaceCount(0)
{
    // Special static pointer needed by base class
    m_pThis = this;


    for (int x = 0; x < MAX_FDR_INTERFACES; x++)
    {
        interfacesList[x].StartFunction = 0;
        interfacesList[x].StopFunction = 0;
    }

}


void CtiFDRService::RunInConsole( DWORD argc, LPTSTR *argv )
{
    CService::RunInConsole(argc, argv);

    //We need to catch ctrl-c so we can stop
    if( !SetConsoleCtrlHandler( (PHANDLER_ROUTINE)MyCtrlHandler,  TRUE ) )
    {
        iGoodStatus = FALSE;
        CTILOG_ERROR(dout, "FDR Could not install control handler");
    }


    Init( );
    Run( );

    // run returned so stop
    OnStop( );
}


void CtiFDRService::DeInit( )
{
    // only base class stuff
    CService::DeInit( );
}

/************************************************************************
* Function Name: CtiFDRService::Init()
*
* Description: loads all interface dll's but does not start them
*
*
*************************************************************************
*/
void CtiFDRService::Init( )
{
    // add FDR stuff here
    std::string interfaces;

    SetStatus(SERVICE_START_PENDING, 33, 5000 );

    try
    {
        ThreadMonitor.start(CtiThreadMonitor::FDR);

        if ( !(gConfigParms.isOpt(CPARM_NAME_FDR_INTERFACES)) )
        {
            CTILOG_ERROR(dout, "No interfaces specified in config file " << CPARM_NAME_FDR_INTERFACES);
            return;
        }

        interfaces = gConfigParms.getValueAsString(CPARM_NAME_FDR_INTERFACES);
        if(interfaces.length() == 0)
        {
            CTILOG_ERROR(dout, "No interfaces specified in config file (len = 0)");
            return;
        }

        boost::char_separator<char> sep(",");
        Boost_char_tokenizer next(interfaces, sep);
        Boost_char_tokenizer::iterator tok_iter = next.begin();

        std::string myInterfaceName;

        // parse the interfaces
        //while (!(myInterfaceName=*tok_iter++).empty())
        for( ;tok_iter != next.end(); tok_iter++ )
        {
            HMODULE     ModuleHandle = (HMODULE) NULL;
            HINSTANCE   hInterfaceLib;

            myInterfaceName = *tok_iter;
            myInterfaceName+= ".DLL";

            //  load DLL
            if( !(hInterfaceLib = LoadLibrary( myInterfaceName.c_str() )) )
            {
                DWORD errCode = GetLastError();
                CTILOG_ERROR(dout, "Unable to locate: \"" << myInterfaceName << "\" error code " << errCode);
            }
            else
            {
                CTILOG_INFO(dout, "Loaded Interface: " << myInterfaceName);

                //  make sure the DLL has the startup routine
                interfacesList[iInterfaceCount].StartFunction = (int (FAR WINAPI *)())GetProcAddress( hInterfaceLib, "RunInterface" );
                if( !interfacesList[iInterfaceCount].StartFunction )
                {
                    CTILOG_ERROR(dout, "Unable to find routine RunInterface() in: " << myInterfaceName);
                }
                else
                {
                    // get the function we use to stop the interface
                    interfacesList[iInterfaceCount].StopFunction = (int (FAR WINAPI *)())GetProcAddress( hInterfaceLib, "StopInterface" );
                    if( !interfacesList[iInterfaceCount].StopFunction)
                    {
                        CTILOG_ERROR(dout, "Unable to find routine StopInterface() in: " << myInterfaceName);
                    }

                    // track the interfaces loaded
                    ++iInterfaceCount;

                }
            }
        } // end for

    }
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "FDR Service init() failed");
    }
}


void CtiFDRService::ParseArgs( DWORD argc, LPTSTR *argv )
{
    //  dunno when this'll ever be needed, as the "console" parameter is parsed by now already
}


void CtiFDRService::OnStop( )
{
    Cti::Messaging::BaseConnection::stopReconnects();

    // change state
    SetStatus( SERVICE_STOP_PENDING,
               33,       // check point??
               5000     // hint
             );

    // stop all threads
    stopInterfaces();

    SetStatus( SERVICE_STOP_PENDING,
               66,       // check point??
               5000     // hint
             );

    //complete
    //UserQuit = true;
    SetEvent(iShutdown);

    ThreadMonitor.interrupt(CtiThread::SHUTDOWN);
    ThreadMonitor.join();

    CloseHandle(iShutdown);
}


void CtiFDRService::Run( )
{
    // set service as running
    SetStatus(SERVICE_RUNNING, 0, 0,
              SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN | SERVICE_ACCEPT_PRESHUTDOWN );

    // Make sure the database is available before we try to load anything from it.
    {
        bool writeLogMessage = true;

        while ( ! ( UserQuit || canConnectToDatabase() ) )
        {
            if ( writeLogMessage )
            {
                CTILOG_ERROR(dout, "Database connection attempt failed");
                writeLogMessage = false;
            }
            Sleep( 5000 );
        }
        if ( UserQuit )
        {
            return;
        }
    }

    long pointID = ThreadMonitor.getProcessPointID();
    Cti::ServiceMetrics::MetricReporter metricReporter {
        Cti::ServiceMetrics::CpuPointOffsets::FDR,
        Cti::ServiceMetrics::MemoryPointOffsets::FDR,
        "FDR",
        FDR_APPLICATION_NAME };

    CtiTime NextThreadMonitorReportTime;
    CtiTime nextCPULoadReportTime;

    CtiThreadMonitor::State previous = CtiThreadMonitor::Normal;

    // for shutting down
    iShutdown = CreateEvent(NULL,TRUE,FALSE,NULL);

    try
    {
        //call run method to start interfaces
        startInterfaces();

        // Initialize the connection to VanGogh....
        FdrVanGoghConnection.setName("FDR Service to Dispatch");
        FdrVanGoghConnection.start();
        FdrVanGoghConnection.WriteConnQue(
            CTIDBG_new CtiRegistrationMsg(FDR_APPLICATION_NAME, GetCurrentThreadId(), true), CALLSITE);

        do
        {
            dout->poke();  //  called every 10 seconds (see while() condition)

            if(pointID!=0)
            {
                CtiThreadMonitor::State next;
                if((next = ThreadMonitor.getState()) != previous ||
                   CtiTime::now() > NextThreadMonitorReportTime)
                {
                    // Any time the state changes or every (StandardMonitorTime / 2) seconds, update the point
                    previous = next;
                    NextThreadMonitorReportTime = nextScheduledTimeAlignedOnRate( CtiTime::now(), CtiThreadMonitor::StandardMonitorTime / 2 );

                    FdrVanGoghConnection.WriteConnQue(
                        CTIDBG_new CtiPointDataMsg(pointID, ThreadMonitor.getState(), NormalQuality, 
                        StatusPointType, ThreadMonitor.getString().c_str()), CALLSITE);
                }
            }

            metricReporter.reportCheck(CompileInfo, FdrVanGoghConnection);

            while(CtiMessage *msg = FdrVanGoghConnection.ReadConnQue(0))
            {
                if( msg->isA() == MSG_COMMAND && ((CtiCommandMsg*)msg)->getOperation() == CtiCommandMsg::AreYouThere )
                {
                    FdrVanGoghConnection.WriteConnQue(msg->replicateMessage(), CALLSITE);

                    CTILOG_INFO(dout, "FDR Service Replied to Are You There message.");
                }

                delete msg;
                msg = NULL;
            }
        }
        while ( WAIT_TIMEOUT == WaitForSingleObject( iShutdown, 10000 ) );   // 10 seconds

        FdrVanGoghConnection.close();
    }
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "FDR Run() Service failed")
    }

    SetStatus( SERVICE_STOPPED );

}



void CtiFDRService::startInterfaces( )
{
    // start all interfaces
    CTILOG_INFO(dout, "Starting All FDR Interfaces");

    for (int i=0; i < iInterfaceCount; i++)
    {
        if (interfacesList[i].StartFunction != 0)
        {
            interfacesList[i].StartFunction();
        }
    }

}


void CtiFDRService::stopInterfaces( )
{
    // stop all interfaces
    CTILOG_INFO(dout, "Stopping All FDR Interfaces");

    for (int i=0; i < iInterfaceCount; i++)
    {
        if (interfacesList[i].StopFunction != 0)
        {
            interfacesList[i].StopFunction();
        }
    }

}
