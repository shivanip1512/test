
/*-----------------------------------------------------------------------------*
*
* File:   portsvc
*
* Date:   12/2/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __PORTSVC_H__
#define __PORTSVC_H__

#include "cservice.h"

class CtiPorterService : public CService
{
protected:

   virtual void OnStop();
   virtual void Init();
   virtual void DeInit();
   virtual void ParseArgs(DWORD argc, LPTSTR* argv);

private:

   bool _quit;

public:

   int   _myargc;
   char  **_myargv;

   CtiPorterService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType);

   virtual void Run();

   void RunInConsole(DWORD argc, LPTSTR* argv);

   DECLARE_SERVICE(CtiPorterService, Porter)

   virtual ~CtiPorterService();

};
#endif // #ifndef __PORTSVC_H__
