/*-----------------------------------------------------------------------------*
*
* File:   dlldbmemmgr
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/dlldbmemmgr.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:24:03 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <iostream>
using namespace std;

#include <rw/db/db.h>
// #include "rtdb.h"
#include "mgr_mempoint.h"
#include "dlldefs.h"
#include "utility.h"



BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
   switch( ul_reason_for_call )
   {
   case DLL_PROCESS_ATTACH:
      {
         // cout << "MemPoint RTDB DLL is initializing!" << endl;
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
         // cout << "MemPoint RTDB DLL is exiting!" << endl;
         break;
      }
   }
   return TRUE;
}




