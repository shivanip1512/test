// Class CServiceConfig
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   CServiceConfig
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVICE/INCLUDE/CServiceConfig.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 16:00:51 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "dlldefs.h"

const int MAX_SERVICE_NAME = 256;

class IM_EX_SERVICE CServiceConfig
{
public:
   CServiceConfig(LPTSTR szServiceName, LPTSTR szDisplay, LPTSTR szDescription = NULL);
   ~CServiceConfig();

   LPTSTR GetServiceName();// {return m_service;}
   LPTSTR GetDisplayName();// {return m_display;}
   LPTSTR GetDescription();// {return m_description;}

   void Install(DWORD dwType = SERVICE_WIN32_OWN_PROCESS,
             DWORD dwStart = SERVICE_DEMAND_START,
             LPCTSTR lpDepends = NULL,
             LPCTSTR lpName = NULL,
             LPCTSTR lpPassword = NULL);

   void Remove(BOOL bForce = FALSE);
   BOOL IsInstalled();

   void ChangeStartType(BOOL bAutostart);
   void GetConfig();

   void Status();
   void Dependencies();

   void Start();
   void Control(DWORD dwControl);

   SC_LOCK LockDatabase(SC_HANDLE hSCM);
   DWORD ErrorPrinter(const TCHAR* pszFcn, DWORD dwErr = GetLastError());

private:
   TCHAR m_service[257];
   TCHAR m_display[257];
   TCHAR m_description[257];
};