#include "precompiled.h"
#include "precomp.h"

/*-----------------------------------------------------------------------------*
*
* File:   cservice
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVICE/cservice.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:24:04 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "eventlog.h"
#include "cservice.h"

CService::CService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType) : m_dwType(dwType), _running_in_console(false)
{
   m_hServiceStatus = NULL;
   m_dwRequestedControl = 0;

   // Control Events
   m_hWatcherThread = NULL;

   m_dwState = 0;
   m_dwControlsAccepted = 0;
   m_dwCheckpoint = 0;
   m_dwWaitHint = 0;

   // Initialize event handles to NULL
   for(int i = 0 ; i < NUMEVENTS ; i++)
      m_hEvents[i] = NULL;

   // Copy string names
   _tcscpy(m_szName, szName);
   _tcscpy(m_szDisplay, szDisplay);

   // Set up class critical section
   InitializeCriticalSection(&m_cs);
}

CService::~CService()
{
   DeleteCriticalSection(&m_cs);
}

void CService::PreInit()
{
   // Initialize Events
   for(int i = 0 ; i < NUMEVENTS ; i++)
   {
      m_hEvents[i] = CreateEvent(NULL, FALSE, FALSE, NULL);
      if(!m_hEvents[i])
         ErrorHandler(_T("CreateEvent"));
   }
}

void CService::DeInit()
{
   // Wait for the watcher thread to terminate
   if(m_hWatcherThread)
   {
      // Wait a reasonable amount of time
      WaitForSingleObject(m_hWatcherThread, 10000);
      CloseHandle(m_hWatcherThread);
   }

   // Uninitialize any resources created in Init()
   for(int i = 0 ; i < NUMEVENTS ; i++)
   {
      if(m_hEvents[i])
         CloseHandle(m_hEvents[i]);
   }
}

void CService::RunInConsole(DWORD argc, LPTSTR* argv)
{
    _running_in_console = true;
    ParseArgs(argc, argv);
}

void CService::ServiceMainMember(DWORD argc, LPTSTR* argv, LPHANDLER_FUNCTION_EX pf, LPTHREAD_START_ROUTINE pfnWTP)
{
   DWORD dwErr = 0;

      PreInit();
      SetupHandlerInside(pf, nullptr);
      ParseArgs(argc, argv);
      LaunchWatcherThread(pfnWTP);
      Init();
      Run();

   DeInit();
}

// Register the control handler for the service
bool CService::SetupHandlerInside(LPHANDLER_FUNCTION_EX lpHandlerProc, LPVOID lpContext)
{
   m_hServiceStatus = RegisterServiceCtrlHandlerEx(m_szName, lpHandlerProc, lpContext);
   if(!m_hServiceStatus)
   {
      ErrorHandler(_T("RegisterServiceCtrlHandler"));
      return false;
   }

   SetStatus(SERVICE_START_PENDING, 1, 5000);
   return true;
}

void CService::HandlerExMember(DWORD dwControl, DWORD dwEventType, LPVOID lpEventData, LPVOID lpContext)
{
   // Keep an additional control request of the same type
   //  from coming in when you're already handling it
   if(m_dwRequestedControl == dwControl)
      return;

   switch(dwControl)
   {
   case SERVICE_CONTROL_STOP:
      m_dwRequestedControl = dwControl;

      // Notify the service to stop...
      SetEvent(m_hEvents[STOP]);
      break;

   case SERVICE_CONTROL_PAUSE:
      m_dwRequestedControl = dwControl;

      // Notify the service to pause...
      SetEvent(m_hEvents[PAUSE]);
      break;

   case SERVICE_CONTROL_CONTINUE:
      if(GetStatus() != SERVICE_RUNNING)
      {
         m_dwRequestedControl = dwControl;

         // Notify the service to continue...
         SetEvent(m_hEvents[CONTINUE]);
      }
      break;

   case SERVICE_CONTROL_SHUTDOWN:
      m_dwRequestedControl = dwControl;

      SetEvent(m_hEvents[SHUTDOWN]);
      break;

   case SERVICE_CONTROL_PRESHUTDOWN:
      m_dwRequestedControl = dwControl;

      SetEvent(m_hEvents[PRESHUTDOWN]);
      break;

   case SERVICE_CONTROL_INTERROGATE:
      // Return current status on interrogation
      SetStatus(GetStatus());
      break;

   default: // User Defined
      m_dwRequestedControl = dwControl;
      HandleUserDefined(dwControl);
   }
}

void CService::LaunchWatcherThread(LPTHREAD_START_ROUTINE pfnWTP)
{
   DWORD tid = 0;

   m_hWatcherThread = (HANDLE)_beginthreadex(0, 0, (unsigned (WINAPI*)(void*))pfnWTP, 0, 0, (unsigned int*)&tid);
   if(!m_hWatcherThread)
      ErrorHandler(_T("_beginthreadex"));
}

DWORD CService::WatcherThreadMemberProc()
{
   DWORD dwWait = 0;
   bool bControlWait = true;

   // Wait for any events to signal
   while(bControlWait)
   {
      dwWait = WaitForMultipleObjects(NUMEVENTS, m_hEvents, FALSE, INFINITE);

      switch(dwWait - WAIT_OBJECT_0)
      {
      case STOP:
         OnStop();
         bControlWait = false;
         break;

      case PAUSE:
         OnPause();
         ResetEvent(m_hEvents[PAUSE]);
         break;

      case CONTINUE:
         OnContinue();
         ResetEvent(m_hEvents[CONTINUE]);
         break;

      case SHUTDOWN:
         OnShutdown();
         bControlWait = false;
         break;

      case PRESHUTDOWN:
         OnPreShutdown();
         bControlWait = false;
         break;
      }
   }
   return 0;
}

void CService::SetStatus(DWORD dwNewState, DWORD dwNewCheckpoint,
                         DWORD dwNewHint,  DWORD dwNewControls,
                         DWORD dwExitCode, DWORD dwSpecificExit)
{
   // The only state that can set Exit Codes is STOPPED
   // Fix if necessary, just in case not set properly.
   if(dwNewState != SERVICE_STOPPED)
   {
      dwExitCode = S_OK;
      dwSpecificExit = 0;
   }

   // Only pending states can set checkpoints or wait hints,
   //  and pending states *must* set wait hints
   if(dwNewState == SERVICE_STOPPED || dwNewState == SERVICE_PAUSED || dwNewState == SERVICE_RUNNING)
   {
      // Requires hint and checkpoint == 0
      // Fix it so that NO_CHANGE from previous state doesn't cause nonzero
      dwNewHint = 0;
      dwNewCheckpoint = 0;
   }
   else
   {
      // Requires hint and checkpoint != 0
      if(dwNewHint <= 0 || dwNewCheckpoint <=0)
      {
         ErrorHandler(_T("CService::SetStatus: Pending statuses require a hint and checkpoint"), true, true, 0);
      }
   }

   // Function can be called by multiple threads - protect member data
   EnterCriticalSection(&m_cs);

   // Alter states if changing
   m_dwState = dwNewState;

   if(dwNewCheckpoint != STATE_NO_CHANGE)
      m_dwCheckpoint = dwNewCheckpoint;

   if(dwNewHint != STATE_NO_CHANGE)
      m_dwWaitHint = dwNewHint;

   if(dwNewControls != STATE_NO_CHANGE)
      m_dwControlsAccepted = dwNewControls;

   SERVICE_STATUS ss = { m_dwType, m_dwState, m_dwControlsAccepted,
                   dwExitCode, dwSpecificExit, m_dwCheckpoint, m_dwWaitHint };

   LeaveCriticalSection(&m_cs);

   if(!_running_in_console && !SetServiceStatus(m_hServiceStatus, &ss)) {
       DWORD LastError = GetLastError();
       ErrorHandler(_T("SetServiceStatus"));
   }
}

//Generic error handler which gathers the last error, looks up the description string, and optionally
//prints the string to the event log and/or raises an exception to stop the service.
DWORD CService::ErrorHandler(const TCHAR* psz, bool bPrintEvent, bool bRaiseException, DWORD dwErr)
{
   if(bPrintEvent)
   {
      CEventLog log(m_szName);
      log.LogWin32Error(MONITOR_WIN32_ERROR, psz, dwErr);
   }

   if(bRaiseException)
      RaiseException(dwErr, EXCEPTION_NONCONTINUABLE, 0, 0);

   return dwErr;
}

void CService::Init()
{}

void CService::OnPause()
{}

void CService::OnContinue()
{}

void CService::OnShutdown()
{
    //  Default shutdown behavior is to stop the service.
    OnStop();
}

void CService::OnPreShutdown()
{
    //  Default preshutdown behavior is the same as shutdown (stop the service).
    OnShutdown();
}

void CService::HandleUserDefined(DWORD dwControl)
{}

void CService::ParseArgs(DWORD argc, LPTSTR* argv)
{}

DWORD   CService::GetStatus()      { return m_dwState; }
DWORD   CService::GetControls()    { return m_dwControlsAccepted; }
LPCTSTR CService::GetName()        { return m_szName; }
LPCTSTR CService::GetDisplayName() { return m_szDisplay; }

