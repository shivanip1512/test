#include "yukon.h"
// File EventLog.cpp Implements the CEventLog class

/*-----------------------------------------------------------------------------*
*
* File:   EventLog
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVICE/EventLog.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:24:03 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "precomp.h"
#include "eventlog.h"

// Construction
CEventLog::CEventLog(LPCTSTR pszSource, LPCTSTR pszServerName)
{
   m_hes = NULL;
   m_pszSource = pszSource;
   m_pszServerName = pszServerName;
}

// Simple Version
BOOL CEventLog::LogEvent(DWORD dwEventID,   WORD     wEventType,
                         WORD  wCategory,   PSID     lpUserSid)
{
   Register();
   BOOL b = ReportEvent(m_hes, wEventType, wCategory,
                                      dwEventID, lpUserSid, 0, 0, NULL, NULL);
   Deregister();
   return b;
}

// Does anything version
BOOL CEventLog::LogEvent(DWORD dwEventID,   LPCTSTR* lpStrings,
                         WORD  wNumStrings, LPVOID   lpRawData,
                         DWORD dwDataSize,  WORD     wEventType,
                         WORD  wCategory,   PSID     lpUserSid)
{
   if(wNumStrings == 0 && lpStrings != NULL)
      return FALSE;
   if(lpStrings == NULL && wNumStrings != 0)
      return FALSE;

   Register();
   BOOL b = ReportEvent(m_hes, wEventType, wCategory, dwEventID, lpUserSid,
                               wNumStrings, dwDataSize, lpStrings, lpRawData);
   Deregister();
   return b;
}

// Single String Version
BOOL CEventLog::LogEvent(DWORD dwEventID,   LPCTSTR  lpString,
                         WORD  wEventType,  WORD     wCategory,
                         PSID  lpUserSid)
{
   const TCHAR* rgsz[] = {lpString};

   Register();
   BOOL b = ReportEvent(m_hes, wEventType, wCategory, dwEventID,
                                                 lpUserSid, 1, 0, rgsz, NULL);
   Deregister();
   return b;
}

// Multiple String Version
BOOL CEventLog::LogEvent(DWORD dwEventID,   LPCTSTR* lpStrings,
                         WORD wNumStrings,  WORD     wEventType,
                         WORD wCategory,    PSID     lpUserSid)
{
   Register();
   BOOL b = ReportEvent(m_hes, wEventType, wCategory, dwEventID,
                                  lpUserSid, wNumStrings, 0, lpStrings, NULL);
   Deregister();
   return b;
}

// Send Data Version
BOOL CEventLog::LogEvent(DWORD  dwEventID,  DWORD    dwDataSize,
                         LPVOID lpRawData,  WORD     wEventType,
                         WORD   wCategory,  PSID     lpUserSid)
{
   Register();
   BOOL b = ReportEvent(m_hes, wEventType, wCategory, dwEventID,
                                   lpUserSid, 0, dwDataSize, NULL, lpRawData);
   Deregister();
   return b;
}

// Log a Win32 error
BOOL CEventLog::LogWin32Error(DWORD dwEventID,
                                           LPCTSTR szString, DWORD dwErrorNum)
{
   LPVOID lpvMsgBuf;
   TCHAR szErrorDesc[1024];
   if(!FormatMessage(
                  FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_ALLOCATE_BUFFER,
                  0, dwErrorNum, MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
                  (LPTSTR)&lpvMsgBuf, 0, 0))
   {
      if(szString != NULL)
         wsprintf(szErrorDesc,
                  _T("Function: %s returned Win32 Error: %d, Unknown Error"),
                  szString, dwErrorNum);
      else
         wsprintf(szErrorDesc,
                  _T("Win32 Error: %d, Unknown Error"),
                  dwErrorNum);
   }
   else
   {
      if(szString != NULL)
         wsprintf(szErrorDesc,
                  _T("Function: %s returned Win32 Error: %d Description: %s"),
                  szString, dwErrorNum, lpvMsgBuf);
      else
         wsprintf(szErrorDesc,
                  _T("Win32 Error: %d Description: %s"),
                  dwErrorNum, lpvMsgBuf);
      LocalFree(lpvMsgBuf);
   }

   const TCHAR* rgsz[] = { szErrorDesc };

   Register();
   BOOL b = ReportEvent(m_hes, EVENTLOG_ERROR_TYPE, NULL, dwEventID,
                                                      NULL, 1, 0, rgsz, NULL);
   Deregister();
   return b;
}

BOOL CEventLog::RegisterLog(LPTSTR szPath)
{
   HKEY hk;
   DWORD dwData;
   TCHAR szBuf[256];
   TCHAR szKey[256];
   wsprintf(szKey,
         _T("SYSTEM\\CurrentControlSet\\Services\\EventLog\\Application\\%s"),
         m_pszSource);

   // Does the key already exist?
   if(RegOpenKey(HKEY_LOCAL_MACHINE, szKey, &hk) == ERROR_SUCCESS)
      return TRUE;

   if(RegCreateKey(HKEY_LOCAL_MACHINE, szKey, &hk) != ERROR_SUCCESS)
      return FALSE;

   _tcscpy(szBuf, szPath);

   // EventMessageFile subkey
   if(RegSetValueEx(hk, _T("EventMessageFile"), 0, REG_EXPAND_SZ,
      (LPBYTE)szBuf, ((_tcslen(szBuf) + 1) * sizeof(TCHAR))) != ERROR_SUCCESS)
      return FALSE;

   // TypesSupported
   dwData = EVENTLOG_ERROR_TYPE |
            EVENTLOG_WARNING_TYPE | EVENTLOG_INFORMATION_TYPE;

   if(RegSetValueEx(hk, _T("TypesSupported"), 0, REG_DWORD, (LPBYTE)&dwData,
                                              sizeof(DWORD)) != ERROR_SUCCESS)
      return FALSE;

   RegCloseKey(hk);
   return TRUE;
}

CEventLog::~CEventLog() { }
