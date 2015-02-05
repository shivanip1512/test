#pragma once

#include "cservice.h"

class CtiDispatchService : public CService
{
   virtual void OnStop();
   virtual void Init();
   virtual void DeInit();
   virtual void ParseArgs(DWORD argc, LPTSTR* argv);

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
