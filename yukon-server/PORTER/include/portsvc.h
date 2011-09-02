#pragma once

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
