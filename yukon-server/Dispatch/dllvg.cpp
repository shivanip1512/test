/*-----------------------------------------------------------------------------*
*
* File:   dllvg
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/dllvg.cpp-arc  $
* REVISION     :  $Revision: 1.10.2.2 $
* DATE         :  $Date: 2008/11/20 16:49:26 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <iostream>
using namespace std;

#include "dllbase.h"
#include "dlldefs.h"
#include "dllvg.h"
#include "logger.h"
#include "utility.h"
#include "cparms.h"

IM_EX_CTIVANGOGH UINT gDispatchDebugLevel = 0x00000000;
IM_EX_CTIVANGOGH UINT gDispatchReloadRate = 86400;
IM_EX_CTIVANGOGH INT gCommErrorDays = 7;




BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
    switch( ul_reason_for_call )
    {
        case DLL_PROCESS_ATTACH:
        {
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

   strcpy(var, "DISPATCH_COMMERROR_DAYS");
   if( !(str = gConfigParms.getValueAsString(var)).empty() )
   {
      gCommErrorDays = atoi (str.c_str());
   }

   strcpy(var, "DISPATCH_DEBUGLEVEL");
   if( !(str = gConfigParms.getValueAsString(var)).empty() )
   {
      char *eptr;
      gDispatchDebugLevel = strtoul(str.c_str(), &eptr, 16);
      if(gDispatchDebugLevel & DISPATCH_DEBUG_VERBOSE)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << CtiTime() << " DISPATCH_DEBUGLEVEL: 0x" << hex <<  gDispatchDebugLevel << dec << endl;
      }
   }
}

