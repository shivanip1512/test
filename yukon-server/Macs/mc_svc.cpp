#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   mcsvc
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mc_svc.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/05/03 18:20:01 $
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
#include "mc_svc.h"
#include "CParms.h"
#include "mc_script.h"

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
	    dout << RWTime() << " **Checkpoint** " << "MACS received one of these events, Ctrl-C, Shutdown, or Close.  About to signal the server to shut down! " << __FILE__ << "(" << __LINE__ << ")" << endl;
	}
    
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

    SetConsoleTitle(MC_CONSOLE_DISPLAY_NAME);
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
	dout << RWTime() << " **Checkpoint** " << "MACS service received an OnStop event.  About to signal the server to shut down! " << __FILE__ << "(" << __LINE__ << ")" << endl;
    }
	
    SetEvent(hShutdown);
}

void CtiMCService::Run()
{
    SetStatus(SERVICE_START_PENDING, 1, 5000 );

    hShutdown = CreateEvent(NULL,TRUE,FALSE,NULL);

    CtiConfigParameters configParameters;

    if( configParameters.isOpt("CTL_SCRIPTS_DIR") )
    {
        string path = configParameters.getValueAsString("CTL_SCRIPTS_DIR");
        CtiMCScript::setScriptPath( path );
    }
    else
    {
        CtiMCScript::setScriptPath("c:\\yukon\\server\\macsscripts");
    }

    // sets a simple debug level if MACS_DEBUG exists
    bool macs_debug = false;
    if( configParameters.isOpt("MACS_DEBUG") )
    {
        macs_debug = true;
    }

    SetStatus(SERVICE_START_PENDING, 33, 5000 );

    // Initialize the global logger
    dout.setOutputFile("macs");
    dout.setOutputPath(gLogDirectory.data());
    dout.setWriteInterval(0);
    dout.setToStdOut(true);
    dout.start();

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

    dout.interrupt(CtiThread::SHUTDOWN);
    dout.join();

    SetStatus( SERVICE_STOPPED );
}

void CtiMCService::ParseArgs(DWORD argc, LPTSTR* argv)
{
}
