#include "dlldefs.h"

class IM_EX_SERVICE CEventLog
{
public:
   CEventLog(LPCTSTR pszSource, LPCTSTR pszServerName = NULL);
   ~CEventLog();

   // Simple Version
   BOOL LogEvent(DWORD    dwEventID,
                 WORD     wEventType  = EVENTLOG_ERROR_TYPE,
                 WORD     wCategory   = 0,
                 PSID     lpUserSid   = NULL);

   // Does anything version
   BOOL LogEvent(DWORD dwEventID,
                 LPCTSTR* lpStrings   = NULL,
                 WORD     wNumStrings = 0,
                 LPVOID   lpRawData   = 0,
                 DWORD    dwDataSize  = 0,
                 WORD     wEventType  = EVENTLOG_ERROR_TYPE,
                 WORD     wCategory   = 0,
                 PSID     lpUserSid   = NULL);

   // Single String Version
   BOOL LogEvent(DWORD    dwEventID,
                 LPCTSTR  lpString,
                 WORD     wEventType  = EVENTLOG_ERROR_TYPE,
                 WORD     wCategory   = 0,
                 PSID     lpUserSid   = NULL);

   // Multiple String Version
   BOOL LogEvent(DWORD    dwEventID,
                 LPCTSTR* lpStrings,
                 WORD     wNumStrings,
                 WORD     wEventType  = EVENTLOG_ERROR_TYPE,
                 WORD     wCategory   = 0,
                 PSID     lpUserSid   = NULL);

   // Send Data Version
   BOOL LogEvent(DWORD    dwEventID,
                 DWORD    dwDataSize,
                 LPVOID   lpRawData,
                 WORD     wEventType  = EVENTLOG_ERROR_TYPE,
                 WORD     wCategory   = 0,
                 PSID     lpUserSid   = NULL);

   // Log a Win32 error
   BOOL LogWin32Error(DWORD   dwEventID,
                      LPCTSTR szString   = NULL,
                      DWORD   dwErrorNum = GetLastError());

   BOOL RegisterLog(LPTSTR szPath);

private:
   HANDLE  m_hes;
   LPCTSTR m_pszSource;
   LPCTSTR m_pszServerName;

   void Register()
   {
      m_hes = RegisterEventSource(m_pszServerName, m_pszSource);
   }

   void Deregister()
   {
      DeregisterEventSource(m_hes);
   }

// Event Types are one of the following
   // EVENTLOG_ERROR_TYPE;
   // EVENTLOG_WARNING_TYPE;
   // EVENTLOG_INFORMATION_TYPE;
};
