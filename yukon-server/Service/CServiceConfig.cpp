#include "yukon.h"
// Implementation class for CServiceConfig

/*-----------------------------------------------------------------------------*
*
* File:   CServiceConfig
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVICE/CServiceConfig.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:24:03 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "precomp.h"
#include "cserviceconfig.h"

#define YUKON_SERVICE_GROUP_NAME    "YUKON GROUP"

CServiceConfig::CServiceConfig(LPTSTR szServiceName, LPTSTR szDisplay, LPTSTR szDescription)
{
   _tcscpy(m_service, szServiceName);
   _tcscpy(m_display, szDisplay);

   if(szDescription != NULL)
   {
      _tcscpy(m_description, szDescription);
   }
   else
   {
      _tcscpy(m_description, "Yukon Server Service");
   }
}

void CServiceConfig::Install(DWORD dwType, DWORD dwStart,
                      LPCTSTR lpDepends, LPCTSTR lpName,
                      LPCTSTR lpPassword)
{
   SC_HANDLE hSCM = NULL;
   SC_HANDLE hService = NULL;

   if(IsInstalled() == TRUE)
      return;

   hSCM = OpenSCManager(NULL, NULL, SC_MANAGER_CREATE_SERVICE);
   if(!hSCM)
   {
      ErrorPrinter(_T("OpenSCManager"));
      goto cleanup;
   }

   TCHAR szFilePath[_MAX_PATH];
    ::GetModuleFileName(NULL, szFilePath, _MAX_PATH);

   hService = CreateService(hSCM,
                             m_service,
                             m_display,
                             SERVICE_ALL_ACCESS,
                             dwType,
                             dwStart,
                             SERVICE_ERROR_NORMAL,
                             szFilePath,
                             YUKON_SERVICE_GROUP_NAME,
                             NULL,
                             lpDepends,
                             lpName,
                             lpPassword);
   if(!hService)
   {
      ErrorPrinter(_T("CreateService"));
      goto cleanup;
   }
   else
   {
      SERVICE_DESCRIPTION desc;
      desc.lpDescription = m_description;
      // ChangeServiceConfig2(hService, SERVICE_CONFIG_DESCRIPTION, &desc);
      _tprintf(_T("%s Created\n"), m_service);
   }

cleanup:
   if(hService) CloseServiceHandle(hService);
   if(hSCM) CloseServiceHandle(hSCM);

   return;
}

void CServiceConfig::Remove(BOOL bForce)
{
   SC_HANDLE hSCM = NULL;
   SC_HANDLE hService = NULL;
   BOOL bSuccess = FALSE;
   hSCM = ::OpenSCManager(NULL, NULL, SC_MANAGER_CONNECT);
   if(!hSCM)
   {
      ErrorPrinter(_T("OpenSCManager"));
      return;
   }

   hService = ::OpenService(hSCM, m_service, DELETE);
   if(!hService)
   {
      ErrorPrinter(_T("OpenService"));
      goto cleanup;
   }

   // Force the service to stop
   if(bForce == TRUE)
   {
      SERVICE_STATUS status;
      ::ControlService(hService, SERVICE_CONTROL_STOP, &status);
      _tprintf(_T("%s stopped\n"), m_service);
   }

   bSuccess = ::DeleteService(hService);
   if (bSuccess)
      _tprintf(_T("%s removed\n"), m_service);
   else
      ErrorPrinter(_T("DeleteService"));

cleanup:
   if(hService) CloseServiceHandle(hService);
   if(hSCM) CloseServiceHandle(hSCM);

   return;
}

BOOL CServiceConfig::IsInstalled()
{
    BOOL bRes = FALSE;

    SC_HANDLE hSCM = ::OpenSCManager(NULL, NULL, SC_MANAGER_CONNECT);
    if(hSCM != NULL)
   {
      SC_HANDLE hService = ::OpenService(hSCM, m_service, SERVICE_QUERY_CONFIG);
        if(hService != NULL)
      {
            bRes = TRUE;
            ::CloseServiceHandle(hService);
        }
        ::CloseServiceHandle(hSCM);
    }
    return bRes;
}

void CServiceConfig::ChangeStartType(BOOL bAutostart)
{
   SC_HANDLE hSCM = 0;
   SC_HANDLE hService = 0;
   SC_LOCK hLock = 0;
   DWORD dwStartType = 0;
   BOOL bRet = FALSE;

   hSCM = OpenSCManager(NULL, NULL, GENERIC_READ | GENERIC_EXECUTE);
   if(!hSCM)
   {
      ErrorPrinter(_T("OpenSCManager"));
      goto cleanup;
   }

   hLock = LockDatabase(hSCM); // Wrapper tells us who locked if locked
   if(!hLock)
   {
      _tprintf(_T("Service Configuration could not be changed\n"));
      goto cleanup;
   }

   hService = OpenService(hSCM, m_service, SERVICE_CHANGE_CONFIG);
   if(!hService)
   {
      ErrorPrinter(_T("OpenService"));
      goto cleanup;
   }

   dwStartType = (bAutostart) ? SERVICE_AUTO_START : SERVICE_DEMAND_START;
   bRet = ChangeServiceConfig(hService,
                               SERVICE_NO_CHANGE,
                               dwStartType,
                               SERVICE_NO_CHANGE,
                               NULL,
                               NULL,
                               NULL,
                               NULL,
                               NULL,
                               NULL,
                               NULL);

   if(bRet)
      _tprintf(_T("Changed Service Configuration Successfully\n"));
   else
      ErrorPrinter(_T("ChangeServiceConfig"));

cleanup:
   if(hService) CloseServiceHandle(hService);
   if(hLock) UnlockServiceDatabase(hLock);
   if(hSCM) CloseServiceHandle(hSCM);
    return;
}

void CServiceConfig::GetConfig()
{
   SC_HANDLE hSCM = 0;
   SC_HANDLE hService = 0;
   BOOL bRet = FALSE;

   LPQUERY_SERVICE_CONFIG pqscBuf = {0};
   DWORD dwBytesNeeded = 0;

   hSCM = OpenSCManager(NULL, NULL, GENERIC_READ);
   if(!hSCM)
   {
      ErrorPrinter(_T("OpenSCManager"));
      goto cleanup;
   }

   hService = OpenService(hSCM, m_service, SERVICE_QUERY_CONFIG);
   if(!hService)
   {
      ErrorPrinter(_T("OpenService"));
      goto cleanup;
   }

   // Obtain the number of bytes needed
   bRet = QueryServiceConfig(hService, 0, 0, &dwBytesNeeded);

   // Allocate the proper size
   pqscBuf = (LPQUERY_SERVICE_CONFIG)LocalAlloc(LPTR, dwBytesNeeded);
   if(!pqscBuf)
   {
      ErrorPrinter(_T("LocalAlloc"), TRUE);
      goto cleanup;
   }

   // Call with the right amount
   bRet = QueryServiceConfig(hService, pqscBuf, dwBytesNeeded, &dwBytesNeeded);
   if(bRet)
   {
      _tcout << _T("Service Configuration for ") << m_service << endl;
      _tcout << _T("Display Name: ") << pqscBuf->lpDisplayName << endl;
      _tcout << _T("Type: 0x") << pqscBuf->dwServiceType << endl;
      _tcout << _T("Start Type: 0x") << pqscBuf->dwStartType << endl;
      _tcout << _T("Error Level: 0x") << pqscBuf->dwErrorControl << endl;
      _tcout << _T("Binary path: ") << pqscBuf->lpBinaryPathName << endl;
      _tcout << _T("Load Order Group: ") << pqscBuf->lpLoadOrderGroup << endl;
      _tcout << _T("Tag ID: ") << pqscBuf->dwTagId << endl;
      _tcout << _T("Dependencies: ") << endl;

      if(pqscBuf->lpDependencies)
      {
         TCHAR* pszDepend = 0;
         int i = 0;
         pszDepend = &pqscBuf->lpDependencies[i];
         while(*pszDepend != 0)
         {
            _tcout << pszDepend << endl;
            i += _tcslen(pszDepend) + 1;
            pszDepend = &pqscBuf->lpDependencies[i];
         }
      }
      _tcout << _T("Login Under: ") << pqscBuf->lpServiceStartName << endl;
   }
   else
      ErrorPrinter(_T("QueryServiceConfig"));

cleanup:
   if(pqscBuf) LocalFree(pqscBuf);
   if(hService) CloseServiceHandle(hService);
   if(hSCM) CloseServiceHandle(hSCM);

   return;
}

void CServiceConfig::Status()
{
   SC_HANDLE hSCM = 0;
   SC_HANDLE hService = 0 ;
   BOOL bRet = FALSE;
   SERVICE_STATUS ss;

   hSCM = OpenSCManager(NULL, NULL, GENERIC_READ);
   if(!hSCM)
   {
      ErrorPrinter(_T("OpenSCManager"));
      goto cleanup;
   }

   hService = OpenService(hSCM, m_service, SERVICE_QUERY_STATUS);
   if(!hService)
   {
      ErrorPrinter(_T("OpenService"));
      goto cleanup;
   }

   bRet = QueryServiceStatus(hService, &ss);
   if(bRet)
   {
      _tcout << _T("Service Status for ") << m_service << endl;
      _tcout << _T("Type: 0x") << ss.dwServiceType << endl;
      _tcout << _T("Current State: 0x") << ss.dwCurrentState << endl;
      _tcout << _T("Controls Accepted: ") << ss.dwControlsAccepted << endl;
      _tcout << _T("Win32 Exit: ") << ss.dwWin32ExitCode << endl;
      _tcout << _T("Service Exit: ") << ss.dwServiceSpecificExitCode << endl;
      _tcout << _T("Checkpoint: 0x") << ss.dwCheckPoint << endl;
      _tcout << _T("WaitHint: 0x") << ss.dwWaitHint << endl;
   }
   else
      ErrorPrinter(_T("QueryServiceStatus"));

cleanup:
   if(hService) CloseServiceHandle(hService);
   if(hSCM) CloseServiceHandle(hSCM);
   return;
}

void CServiceConfig::Dependencies()
{
   SC_HANDLE hSCM = 0;
   SC_HANDLE hService = 0;
   BOOL bRet = 0;

   DWORD dwBytesNeeded = 0;
   DWORD dwServicesReturned = 0;
   LPENUM_SERVICE_STATUS pessBuf = {0};

   hSCM = OpenSCManager(NULL, NULL, GENERIC_READ);
   if(!hSCM)
   {
      ErrorPrinter(_T("OpenSCManager"));
      goto cleanup;
   }

   hService = OpenService(hSCM, m_service, SERVICE_ENUMERATE_DEPENDENTS);
   if(!hService)
   {
      ErrorPrinter(_T("OpenService"));
      goto cleanup;
   }

   // Find out how much to allocate
   bRet = EnumDependentServices(hService,
                                 SERVICE_STATE_ALL,
                                 0,
                                 0,
                                 &dwBytesNeeded,
                                 &dwServicesReturned);

   if(dwBytesNeeded == 0)
   {
      _tprintf(_T("%s has no dependencies\n"), m_service);
      goto cleanup;
   }

   // Allocate the proper size
   pessBuf = (LPENUM_SERVICE_STATUS)LocalAlloc(LPTR, dwBytesNeeded);
   if(!pessBuf)
   {
      ErrorPrinter(_T("LocalAlloc"), TRUE);
      goto cleanup;
   }

   bRet = EnumDependentServices(hService,
                                 SERVICE_STATE_ALL,
                                 pessBuf,
                                 dwBytesNeeded,
                                 &dwBytesNeeded,
                                 &dwServicesReturned);
   if(bRet)
   {
      LPENUM_SERVICE_STATUS pess = 0;
      _tcout << _T("Services Dependent On ") << m_service << endl;
      for(int i = 0 ; i < dwServicesReturned ; i++)
      {
         pess = &pessBuf[i];
         _tcout << pess->lpDisplayName << endl;
         _tcout << _T("  Current State: 0x") << pess->ServiceStatus.dwCurrentState << endl;
      }
   }
   else
      ErrorPrinter(_T("EnumDependentServices"));

cleanup:
   if(pessBuf) LocalFree(pessBuf);
   if(hService) CloseServiceHandle(hService);
   if(hSCM) CloseServiceHandle(hSCM);

   return;
}

void CServiceConfig::Start()
{
   SC_HANDLE hSCM = 0;
   SC_HANDLE hService = 0;
   BOOL bRet = 0;
   DWORD dwOldCheck = 0;
   SERVICE_STATUS ss;

   hSCM = OpenSCManager(NULL, NULL, GENERIC_READ);
   if(!hSCM)
   {
      ErrorPrinter(_T("OpenSCManager"));
      goto cleanup;
   }

   hService = OpenService(hSCM, m_service, SERVICE_START | SERVICE_QUERY_STATUS);
   if(!hService)
   {
      ErrorPrinter(_T("OpenService"));
      goto cleanup;
   }

   if(!StartService(hService, 0, NULL))
   {
      ErrorPrinter(_T("StartService"));
      goto cleanup;
   }

   // Optionally, make sure service is running before continuing...
   bRet = QueryServiceStatus(hService, &ss);
   if(!bRet)
   {
      ErrorPrinter(_T("QueryServiceStatus"), TRUE);
      goto cleanup;
   }

   while(SERVICE_RUNNING != ss.dwCurrentState)
   {
      dwOldCheck = ss.dwCheckPoint;
      Sleep(ss.dwWaitHint);

      bRet = QueryServiceStatus(hService, &ss);
      if(!bRet)
      {
         ErrorPrinter(_T("QueryServiceStatus"), TRUE);
         goto cleanup;
      }

      // Service changed state or did not increment in time, so break
      if(dwOldCheck >= ss.dwCheckPoint)
         break;
   }

   if(SERVICE_RUNNING == ss.dwCurrentState)
      _tcout << m_service << _T(" started successfully.") << endl;
   else
   {
      _tcout << m_service << _T(" start unsuccessful: ") << endl;
      _tcout << _T("Current State: 0x") << ss.dwCurrentState << endl;
      _tcout << _T("Win32 Exit: ") << ss.dwWin32ExitCode << endl;
      _tcout << _T("Service Exit: ") << ss.dwServiceSpecificExitCode << endl;
      _tcout << _T("Checkpoint: 0x") << ss.dwCheckPoint << endl;
      _tcout << _T("WaitHint: 0x") << ss.dwWaitHint << endl;
   }

cleanup:
   if(hService) CloseServiceHandle(hService);
   if(hSCM) CloseServiceHandle(hSCM);
   return;
}

void CServiceConfig::Control(DWORD dwControl)
{
   SC_HANDLE hSCM = 0;
   SC_HANDLE hService = 0;
   BOOL bRet = 0;

   SERVICE_STATUS ss;

   hSCM = OpenSCManager(NULL, NULL, GENERIC_READ);
   if(!hSCM)
   {
      ErrorPrinter(_T("OpenSCManager"));
      goto cleanup;
   }

   hService = OpenService(hSCM, m_service, GENERIC_EXECUTE);
   if(!hService)
   {
      ErrorPrinter(_T("OpenService"));
      goto cleanup;
   }

   bRet = ControlService(hService, dwControl, &ss);
   if(bRet)
   {
      _tcout << _T("Service Status for ") << m_service << endl;
      _tcout << _T("Type: 0x") << ss.dwServiceType << endl;
      _tcout << _T("Current State: 0x") << ss.dwCurrentState << endl;
      _tcout << _T("Controls Accepted: ") << ss.dwControlsAccepted << endl;
      _tcout << _T("Win32 Exit: ") << ss.dwWin32ExitCode << endl;
      _tcout << _T("Service Exit: ") << ss.dwServiceSpecificExitCode << endl;
      _tcout << _T("Checkpoint: 0x") << ss.dwCheckPoint << endl;
      _tcout << _T("WaitHint: 0x") << ss.dwWaitHint << endl;
   }
   else
      ErrorPrinter(_T("ControlService"));

cleanup:
   if (hService) CloseServiceHandle(hService);
   if (hSCM) CloseServiceHandle(hSCM);
   return;
}

// Utility Function locks the SCM database and feeds back status on error
SC_LOCK CServiceConfig::LockDatabase(SC_HANDLE hSCM)
{
   SC_LOCK hLock = 0;
   LPQUERY_SERVICE_LOCK_STATUS pqlsBuf;
   hLock = LockServiceDatabase(hSCM);

   // Lock not available
   if(!hLock)
   {
      DWORD dwBytesNeeded = 0;

      // Allocate buffer for lock information
      // includes enough for service
      // name string--no need to re-call
      pqlsBuf = (LPQUERY_SERVICE_LOCK_STATUS)LocalAlloc(LPTR, sizeof(QUERY_SERVICE_LOCK_STATUS) + MAX_SERVICE_NAME + 1);
      if(!pqlsBuf)
         ErrorPrinter(_T("LocalAlloc"), TRUE);

      BOOL bRet = QueryServiceLockStatus(hSCM, pqlsBuf,
                                           sizeof(QUERY_SERVICE_LOCK_STATUS) + MAX_SERVICE_NAME + 1, &dwBytesNeeded);
      if(!bRet)
      {
         ErrorPrinter(_T("QueryServiceLockStatus"));
         LocalFree(pqlsBuf);
         return NULL;
      }

      // Output who locked it if locked
      if(pqlsBuf->fIsLocked)
         _tprintf(_T("Locked by: %s for %d seconds\n"), pqlsBuf->lpLockOwner, pqlsBuf->dwLockDuration);
      else
         _tprintf(_T("Database Not Locked\n"));

      LocalFree(pqlsBuf);
   }
   return hLock;
}

DWORD CServiceConfig::ErrorPrinter(const TCHAR* psz, DWORD dwErr)
{
   LPVOID lpvMsgBuf;
    if(!FormatMessage(FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_ALLOCATE_BUFFER,
                      0, dwErr, MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), (LPTSTR)&lpvMsgBuf, 0, 0))
   {
      _tprintf(_T("%s failed: Unknown error %x\n"), psz, dwErr);
   }
   else
      _tprintf(_T("%s failed: %s\n"), psz, (LPTSTR)lpvMsgBuf);

   LocalFree(lpvMsgBuf);
   return dwErr;
}

CServiceConfig::~CServiceConfig() {}

LPTSTR CServiceConfig::GetServiceName() {return m_service;}
LPTSTR CServiceConfig::GetDisplayName() {return m_display;}
LPTSTR CServiceConfig::GetDescription() {return m_description;}
