#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dllvg
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/dllvg.cpp-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <windows.h>
#include <iostream>
using namespace std;

#include "configparms.h"
#include "dllbase.h"
#include "dlldefs.h"
#include "dllvg.h"
#include "logger.h"
#include "utility.h"

IM_EX_CTIVANGOGH UINT gDispatchDebugLevel = 0x00000000;
IM_EX_CTIVANGOGH UINT gDispatchReloadRate = 3600;



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
         //cout << "Van Gogh DLL is exiting!" << endl;
         break;
      }
   }
   return TRUE;
}



IM_EX_CTIVANGOGH void InitDispatchGlobals(void)
{
   RWCString str;
   char var[128];

   strcpy(var, "DISPATCH_RELOAD_RATE");
   if( !(str = gConfigParms.getValueAsString(var)).isNull() )
   {
      gDispatchReloadRate = atoi (str.data());
   }

   strcpy(var, "DISPATCH_DEBUGLEVEL");
   if( !(str = gConfigParms.getValueAsString(var)).isNull() )
   {
      char *eptr;
      gDispatchDebugLevel = strtoul(str.data(), &eptr, 16);
      if(gDispatchDebugLevel & DISPATCH_DEBUG_VERBOSE)
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " DISPATCH_DEBUGLEVEL: 0x" << hex <<  gDispatchDebugLevel << dec << endl;
      }
   }
}

