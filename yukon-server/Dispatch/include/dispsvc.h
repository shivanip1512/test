/*-----------------------------------------------------------------------------*
*
* File:   dispsvc
*
* Date:   12/11/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/INCLUDE/dispsvc.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:52 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __DISPSVC_H__
#define __DISPSVC_H__

#include "cservice.h"

class CtiDispatchService : public CService
{
protected:

   virtual void OnStop();
   virtual void Init();
   virtual void DeInit();
   virtual void ParseArgs(DWORD argc, LPTSTR* argv);

private:

   BOOL _quit;

public:

   int   _myargc;
   char  **_myargv;

   CtiDispatchService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType);

   virtual void Run();

   void RunInConsole(DWORD argc, LPTSTR* argv);

   DECLARE_SERVICE(CtiDispatchService, Dispatch)

   virtual ~CtiDispatchService();

};
#endif // #ifndef __DISPSVC_H__
