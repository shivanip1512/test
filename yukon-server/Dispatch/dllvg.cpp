#include "precompiled.h"

#include <iostream>
using namespace std;

#include "dllbase.h"
#include "dlldefs.h"
#include "dllvg.h"
#include "logger.h"
#include "module_util.h"
#include "cparms.h"

IM_EX_CTIVANGOGH UINT gDispatchDebugLevel = 0x00000000;
IM_EX_CTIVANGOGH UINT gDispatchReloadRate = 86400;




BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
    switch( ul_reason_for_call )
    {
        case DLL_PROCESS_ATTACH:
        {
            Cti::identifyProject(CompileInfo);

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
            break;
        }
    }

    return TRUE;
}



IM_EX_CTIVANGOGH void InitDispatchGlobals(void)
{
   string str;
   char var[128];

   strcpy(var, "DISPATCH_RELOAD_RATE");
   if( !(str = gConfigParms.getValueAsString(var)).empty() )
   {
       gDispatchReloadRate = std::max((unsigned long)gConfigParms.getValueAsULong(var), (unsigned long)86400);
   }

   strcpy(var, "DISPATCH_DEBUGLEVEL");
   if( !(str = gConfigParms.getValueAsString(var)).empty() )
   {
      char *eptr;
      gDispatchDebugLevel = strtoul(str.c_str(), &eptr, 16);
      if(gDispatchDebugLevel & DISPATCH_DEBUG_VERBOSE)
      {
         CTILOG_DEBUG(dout, "DISPATCH_DEBUGLEVEL: 0x"<< hex << gDispatchDebugLevel);
      }
   }
}

