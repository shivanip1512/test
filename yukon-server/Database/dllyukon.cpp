#pragma warning( disable : 4786 )

/*-----------------------------------------------------------------------------*
*
* File:   dllyukon
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/dllyukon.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:26 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <windows.h>
#include <iostream>
using namespace std;

#include <rw/db/db.h>


#include "tbl_route.h"
#include "tbl_rtcarrier.h"
#include "tbl_rtcomm.h"
#include "tbl_rtmacro.h"
#include "tbl_rtroute.h"
#include "tbl_rtrepeater.h"
#include "tbl_rtversacom.h"



#include "dlldefs.h"
#include "msg_pcrequest.h"


// Global DLL Object... for C interface...

// DLLEXPORT CtiDeviceManager gPortManager;

BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
   switch( ul_reason_for_call )
   {
   case DLL_PROCESS_ATTACH:
      {
         // cout << "Yukon DB Miner DLL is initializing!" << endl;
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
         // cout << "Yukon DB Miner RTDB DLL is exiting!" << endl;
         break;
      }
   }
   return TRUE;
}



