#include "yukon.h"

#include <windows.h>
#include <iostream>
using namespace std;

#include <rw/db/db.h>
#include <rw\thr\mutex.h>

#include "dll_prot.h"
#include "utility.h"

BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
   switch( ul_reason_for_call )
   {
   case DLL_PROCESS_ATTACH:
      {
         // cout << "CTI Protocol DLL initializing!" << endl;
         identifyProject(CompileInfo);
         break;
      }
   case DLL_THREAD_ATTACH:
      {
         break;
      }
   case DLL_THREAD_DETACH:
      {
         break;
      }
   case DLL_PROCESS_DETACH:
      {
         // cout << "CTI Protocol DLL is exiting!" << endl;
         break;
      }
   }
   return TRUE;
}

