#pragma once

#include "cservice.h"

class CtiScannerService : public CService
{
   virtual void OnStop();
   virtual void Init();
   virtual void DeInit();
   virtual void ParseArgs(DWORD argc, LPTSTR* argv);

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
