/*-----------------------------------------------------------------------------*
*
* File:   scansvc
*
* Date:   12/2/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2003/03/13 19:36:20 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __SCANSVC_H__
#define __SCANSVC_H__
#pragma warning( disable : 4786)

#include "cservice.h"


class CtiScannerService : public CService
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

   CtiScannerService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType);

   virtual void Run();

   void RunInConsole(DWORD argc, LPTSTR* argv);

   DECLARE_SERVICE(CtiScannerService, Scanner)

   virtual ~CtiScannerService();

};
#endif // #ifndef __SCANSVC_H__
