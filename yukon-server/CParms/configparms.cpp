#include "yukon.h"

#include <iostream>
using namespace std;


#include "configparms.h"

/*
 *  C interface functions for this "MODULE"!
 */

#ifdef __cplusplus
extern "C" {
#endif

   IM_EX_C_CPARM BOOL isConfigOpt(const string& key)
   {
      BOOL b = FALSE;

      b = gConfigParms.isOpt(key);

      return b;
   }

   IM_EX_C_CPARM void DumpConfigParms()
   {
      gConfigParms.Dump();
   }

   IM_EX_C_CPARM BOOL getConfigValueAsString(const string& Key, char *targ, int len)
   {
      if(isConfigOpt(Key))
      {
         string Temp = gConfigParms.getValueAsString(Key);

         if(Temp.length() > len)
         {
            strncpy(targ, Temp.c_str(), len-1);
            targ[len] = '\0';
         }
         else
         {
            strcpy(targ, Temp.c_str());
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


