#pragma once

#include "cservice.h"

class CtiSimulatorService : public CService
{
   virtual void OnStop();
   virtual void Init();
   virtual void DeInit();
   virtual void ParseArgs(DWORD argc, LPTSTR* argv);

   bool _quit;

public:

   int   _myargc;
   char  **_myargv;

   CtiSimulatorService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType);

   virtual void Run();

   void RunInConsole(DWORD argc, LPTSTR* argv);

   DECLARE_SERVICE(CtiSimulatorService, CCUSIMULATOR)

   virtual ~CtiSimulatorService();

};
