#pragma warning( disable : 4786 )
/*-----------------------------------------------------------------------------*
*
* File:   dlldbmemmgr
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/dlldbmemmgr.cpp-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


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




