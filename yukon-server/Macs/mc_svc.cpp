/*-----------------------------------------------------------------------------*
*
* File:   mcsvc
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mc_svc.cpp-arc  $
* REVISION     :  $Revision: 1.9.12.1 $
* DATE         :  $Date: 2008/11/21 20:56:59 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------
        Filename:  mc_svc.cpp

        Programmer:  Aaron Lauinger

        Description:    Source file for CtiMCSservice


        Initial Date:  4/7/99

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 1999
---------------------------------------------------------------------------*/
#include "precompiled.h"
#include "mc_svc.h"
#include "CParms.h"
#include "mc_script.h"
#include "thread_monitor.h"

using namespace std;

bool UserQuit = false;

/* CtrlHandler handles is used to catch ctrl-c in the case where macs is being
   run in a console */
BOOL CtrlHandler(DWORD fdwCtrlType)
{
    switch (fdwCtrlType)
    {

    /* Handle the CTRL+C signal. */

    case CTRL_C_EVENT:
    case CTRL_SHUTDOWN_EVENT:
    case CTRL_CLOSE_EVENT:

    if( gMacsDebugLevel & MC_DEBUG_SHUTDOWN )
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " **Checkpoint** " << "MACS received one of these events, Ctrl-C, Shutdown, or Close.  About to signal the server to shut down! " << __FILE__ << "(" << __LINE__ << ")" << endl;
    }
        UserQuit = true;
        SetEvent(hShutdown);
        Sleep(30000);
        return TRUE;

    // Don't quit if
    case CTRL_BREAK_EVENT:
    case CTRL_LOGOFF_EVENT:

    default:
        return FALSE;

    }

}


IMPLEMENT_SERVICE(CtiMCService, MACS)

CtiMCService::CtiMCService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType )
: CService( szName, szDisplay, dwType )
{
    m_pThis = this;
}


void CtiMCService::RunInConsole(DWORD argc, LPTSTR* argv)
{
    CService::RunInConsole(argc, argv);

    //We need to catch ctrl-c so we can stop
    if (!SetConsoleCtrlHandler((PHANDLER_ROUTINE) CtrlHandler,  TRUE))
        cerr << "Could not install control handler" << endl;

    Run();
}

void CtiMCService::Init()
{   /* FIX FIX Change the server log directory to be an offset
       of the yukon base directory after directory structure is
       finalized */
}

void CtiMCService::DeInit()
{
    CService::DeInit();
}

void CtiMCService::OnStop()
{
    if( gMacsDebugLevel & MC_DEBUG_SHUTDOWN )
    {
    CtiLockGuard<CtiLogger> dout_guard(dout);
    dout << CtiTime() << " **Checkpoint** " << "MACS service received an OnStop event.  About to signal the server to shut down! " << __FILE__ << "(" << __LINE__ << ")" << endl;
    }

    SetEvent(hShutdown);
}

void CtiMCService::Run()
{
    SetStatus(SERVICE_START_PENDING, 1, 5000 );

    hShutdown = CreateEvent(NULL,TRUE,FALSE,NULL);

    CtiMCScript::setScriptPath(gConfigParms.getValueAsPath("CTL_SCRIPTS_DIR", "server\\macsscripts"));

    // sets a simple debug level if MACS_DEBUG exists
    bool macs_debug = false;
    if( gConfigParms.isOpt("MACS_DEBUG") )
    {
        macs_debug = true;
    }

    SetStatus(SERVICE_START_PENDING, 33, 5000 );

    // Initialize the global logger
    dout.setOwnerInfo(CompileInfo);
    dout.setOutputFile("macs");
    dout.setOutputPath(gLogDirectory);
    dout.setRetentionLength(gLogRetention);
    dout.setWriteInterval(1000);
    dout.setToStdOut(true);
    dout.start();

    // Make sure the database is available before we try to load anything from it.
    {
        bool writeLogMessage = true;

        while ( ! ( UserQuit || canConnectToDatabase() ) )
        {
            if ( writeLogMessage )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime( ) << " - Database connectivity failed." << std::endl;

                writeLogMessage = false;
            }
            Sleep( 5000 );
        }
        if ( UserQuit )
        {
            SetStatus( SERVICE_STOP_PENDING, 50, 2500 );

            dout.interrupt(CtiThread::SHUTDOWN);
            dout.join();

            SetStatus( SERVICE_STOPPED );

            return;
        }
    }

    ThreadMonitor.start();

    SetStatus(SERVICE_START_PENDING, 66, 5000 );

    _mc_server = new CtiMCServer();
    _mc_server->setDebug(macs_debug);
    _mc_server->start();

    SetStatus(SERVICE_RUNNING, 0, 0,
              SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN );

    //Wait for the shutdown event to become signalled
    WaitForSingleObject( hShutdown, INFINITE );
    CloseHandle(hShutdown);

    SetStatus( SERVICE_STOP_PENDING, 2, 5000 );

    _mc_server->interrupt( CtiThread::SHUTDOWN );
    _mc_server->join();

    delete _mc_server;

    SetStatus( SERVICE_STOP_PENDING, 50, 2500 );

    ThreadMonitor.interrupt(CtiThread::SHUTDOWN);
    ThreadMonitor.join();

    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();

    SetStatus( SERVICE_STOPPED );
}

void CtiMCService::ParseArgs(DWORD argc, LPTSTR* argv)
{
}
