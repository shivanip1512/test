#pragma once

#include "cservice.h"

namespace Cti {
namespace Simulator {

class FieldSimulatorService : public CService
{
   virtual void OnStop();
   virtual void Init();
   virtual void DeInit();
   virtual void ParseArgs(DWORD argc, LPTSTR* argv);

   bool _quit;

public:

   int   _myargc;
   char  **_myargv;

   FieldSimulatorService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType);

   virtual void Run();

   void RunInConsole(DWORD argc, LPTSTR* argv);

   DECLARE_SERVICE(FieldSimulatorService, FIELDSIMULATOR)

   virtual ~FieldSimulatorService();

};

}
}