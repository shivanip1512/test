/*-----------------------------------------------------------------------------*
*
* FILE NAME: fdrservice.cpp
*
* DATE: 11/08/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.8.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:47 $
*
* AUTHOR: Ben Wallace
*
* PURPOSE: FDR NT Service Module
*
* DESCRIPTION: Inplements the needed functions for the NT Service Controller.
*
*
*
* Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

/*

From: winsvc.h

//
// Service State -- for CurrentState
//
#define SERVICE_STOPPED                0x00000001
#define SERVICE_START_PENDING          0x00000002
#define SERVICE_STOP_PENDING           0x00000003
#define SERVICE_RUNNING                0x00000004
#define SERVICE_CONTINUE_PENDING       0x00000005
#define SERVICE_PAUSE_PENDING          0x00000006
#define SERVICE_PAUSED                 0x00000007

//
// Controls Accepted  (Bit Mask)
//
#define SERVICE_ACCEPT_STOP            0x00000001
#define SERVICE_ACCEPT_PAUSE_CONTINUE  0x00000002
#define SERVICE_ACCEPT_SHUTDOWN        0x00000004
#define SERVICE_ACCEPT_PARAMCHANGE     0x00000008
#define SERVICE_ACCEPT_NETBINDCHANGE   0x00000010

*/

/** include files **/
#include <crtdbg.h>

#include <iostream>

#include <conio.h>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw/thr/thrfunc.h>
#include <rw/thr/mutex.h>

#include "cparms.h"
#include "logger.h"
#include "guard.h"
#include "fdrservice.h"
#include "thread_monitor.h"
#include "connection.h"
#include "msg_cmd.h"

CtiConnection     FdrVanGoghConnection;


BOOL MyCtrlHandler( DWORD fdwCtrlType )
{
    switch(fdwCtrlType)
    {
        case CTRL_CLOSE_EVENT:
        case CTRL_C_EVENT:
        case CTRL_BREAK_EVENT:
        case CTRL_LOGOFF_EVENT:
        case CTRL_SHUTDOWN_EVENT:
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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " FDR Could not install control handler" << endl;
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
    string   interfaces;

    try
    {
        ThreadMonitor.start();

        if ( !(gConfigParms.isOpt(CPARM_NAME_FDR_INTERFACES)) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "No interfaces specified in config file " << CPARM_NAME_FDR_INTERFACES << endl;
            return;
        }

        interfaces = gConfigParms.getValueAsString(CPARM_NAME_FDR_INTERFACES);
        if(interfaces.length() == 0)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "No interfaces specified in config file (len = 0)" << endl;
            return;
        }

        boost::char_separator<char> sep(",");
        Boost_char_tokenizer next(interfaces, sep);
        Boost_char_tokenizer::iterator tok_iter = next.begin();

        string       myInterfaceName;
        string       tempString;

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
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Unable to locate: \"" << myInterfaceName << "\" error code " << errCode << endl;
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Loaded Interface: " << myInterfaceName << endl;
                }

                //  make sure the DLL has the startup routine
                interfacesList[iInterfaceCount].StartFunction = (int (FAR WINAPI *)())GetProcAddress( hInterfaceLib, "RunInterface" );
                if( !interfacesList[iInterfaceCount].StartFunction )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Unable to find routine RunInterface() in: " << myInterfaceName << endl;
                }
                else
                {
                    // get the function we use to stop the interface
                    interfacesList[iInterfaceCount].StopFunction = (int (FAR WINAPI *)())GetProcAddress( hInterfaceLib, "StopInterface" );
                    if( !interfacesList[iInterfaceCount].StopFunction)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "Unable to find routine StopInterface() in: " << myInterfaceName << endl;
                    }

                    // track the interfaces loaded
                    ++iInterfaceCount;

                }
            }
        } // end for

    }
    catch( RWxmsg &msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception in FDR Service init(): ";
        dout << msg.why() << endl;
    }


}


void CtiFDRService::ParseArgs( DWORD argc, LPTSTR *argv )
{
    //  dunno when this'll ever be needed, as the "console" parameter is parsed by now already
}


void CtiFDRService::OnStop( )
{
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

    // stop dout thread
    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();

    CloseHandle(iShutdown);

    SetStatus( SERVICE_STOPPED );

}


void CtiFDRService::Run( )
{
    long pointID = ThreadMonitor.getPointIDFromOffset(CtiThreadMonitor::FDR);
    CtiTime NextThreadMonitorReportTime;
    CtiThreadMonitor::State previous = CtiThreadMonitor::Normal;

    string FdrVanGoghMachine = gConfigParms.getValueAsString("DISPATCH_MACHINE", "127.0.0.1");

    // for shutting down
    iShutdown = CreateEvent(NULL,TRUE,FALSE,NULL);

    SetStatus(SERVICE_START_PENDING, 33, 5000 );

    try
    {
        //call run method to start interfaces
        startInterfaces();

        // set service as running
        SetStatus(SERVICE_RUNNING, 0, 0,
                  SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN );

        // Initialize the connection to VanGogh....
        FdrVanGoghConnection.doConnect(VANGOGHNEXUS, FdrVanGoghMachine);
        FdrVanGoghConnection.setName("FDR Service to Dispatch");
        FdrVanGoghConnection.WriteConnQue(CTIDBG_new CtiRegistrationMsg("FDR Service", rwThreadId(), TRUE));

        do
        {
            if(pointID!=0)
            {
                CtiThreadMonitor::State next;
                if((next = ThreadMonitor.getState()) != previous ||
                   CtiTime::now() > NextThreadMonitorReportTime)
                {
                    // Any time the state changes or every (StandardMonitorTime / 2) seconds, update the point
                    previous = next;
                    NextThreadMonitorReportTime = nextScheduledTimeAlignedOnRate( CtiTime::now(), CtiThreadMonitor::StandardMonitorTime / 2 );

                    FdrVanGoghConnection.WriteConnQue(CTIDBG_new CtiPointDataMsg(pointID, ThreadMonitor.getState(), NormalQuality, StatusPointType, ThreadMonitor.getString().c_str()));
                }
            }

            while( CtiMessage *msg = FdrVanGoghConnection.ReadConnQue(0) )
            {
                if( msg->isA() == MSG_COMMAND && ((CtiCommandMsg*)msg)->getOperation() == CtiCommandMsg::AreYouThere )
                {
                    FdrVanGoghConnection.WriteConnQue(msg->replicateMessage());

                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " FDR Service Replied to Are You There message." << endl;
                    }
                }

                delete msg;
                msg = NULL;
            }
        }
        while ( WAIT_TIMEOUT == WaitForSingleObject( iShutdown, 10000 ) );   // 10 seconds

        FdrVanGoghConnection.ShutdownConnection();
    }
    catch( RWxmsg &msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception in FDR Run() Service: ";
        dout << msg.why() << endl;
    }

}



void CtiFDRService::startInterfaces( )
{
    // start all interfaces
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Starting All FDR Interfaces" << endl;
    }

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
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Stopping All FDR Interfaces" << endl;
    }

    for (int i=0; i < iInterfaceCount; i++)
    {
        if (interfacesList[i].StopFunction != 0)
        {
            interfacesList[i].StopFunction();
        }
    }

}
