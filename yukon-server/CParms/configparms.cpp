#include "yukon.h"
#include <windows.h>
#include <iostream>
using namespace std;

#include <rw/cstring.h>

#include "configparms.h"

/*
 *  C interface functions for this "MODULE"!
 */

#ifdef __cplusplus
extern "C" {
#endif

   IM_EX_C_CPARM BOOL isConfigOpt(RWCString key)
   {
      BOOL b = FALSE;

      b = gConfigParms.isOpt(key);

      return b;
   }

   IM_EX_C_CPARM int RefreshConfigParameters(RWCString FileName)
   {
      int status = !0;

      status = gConfigParms.RefreshConfigParameters();

      return status;
   }

   IM_EX_C_CPARM void DumpConfigParms()
   {
      gConfigParms.Dump();
   }

   IM_EX_C_CPARM BOOL getConfigValueAsString(RWCString Key, char *targ, int len)
   {
      if(isConfigOpt(Key))
      {
         RWCString Temp = gConfigParms.getValueAsString(Key);

         if(Temp.length() > len)
         {
            strncpy(targ, Temp, len-1);
            targ[len] = '\0';
         }
         else
         {
            strcpy(targ, Temp);
         }

         return TRUE;
      }
      else
      {
         return FALSE;
      }
   }

#ifdef __cplusplus
}
#endif


