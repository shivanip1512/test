#pragma warning( disable : 4786 )
/*-----------------------------------------------------------------------------*
*
* File:   dlldev
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dlldev.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 16:00:11 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <windows.h>
#include <iostream>
using namespace std;

#include <rw/db/db.h>
// #include "rtdb.h"
#include "mgr_device.h"
#include "mgr_route.h"
#include "dlldefs.h"
#include "devicetypes.h"
#include "msg_pcrequest.h"
#include "rte_base.h"
#include "rte_xcu.h"
#include "rte_macro.h"
#include "dev_dlcbase.h"
#include "dev_grp_emetcon.h"
#include "utility.h"


//#include "dllyukon.h"

// Global DLL Object... for C interface...

// DLLEXPORT CtiDeviceManager gPortManager;

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
         // cout << "Yukon DB Miner RTDB DLL is exiting!" << endl;
         break;
      }
   }
   return TRUE;
}


void IM_EX_DEVDB attachRouteManagerToDevices(CtiDeviceManager *DM, CtiRouteManager *RteMgr)
{
   CtiDeviceBase  *pBase;

   CtiRTDB<CtiDevice>::CtiRTDBIterator   itr_dv(DM->getMap());

   RWRecursiveLock<RWMutexLock>::LockGuard guard(DM->getMux());

   for(; ++itr_dv ;)
   {
      pBase = itr_dv.value();
      pBase->setRouteManager(RteMgr);
   }

}

void IM_EX_DEVDB attachTransmitterDeviceToRoutes(CtiDeviceManager *DM, CtiRouteManager *RM)
{
   int            i;
   LONG           dID;
   CtiRoute       *pRte;
   CtiDeviceBase  *pDev;

   CtiRTDB<CtiRoute>::CtiRTDBIterator   itr(RM->getMap());


   for(; ++itr ;)
   {
      pRte = itr.value();

      switch(pRte->getType())
      {
      case CCURouteType:
      case TCURouteType:
      case LCURouteType:
      case RepeaterRouteType:
      case VersacomRouteType:
      case TapRouteType:
         {
            CtiRouteXCU  *pXCU = (CtiRouteXCU*)pRte;

            dID = pXCU->getCommRoute().getTrxDeviceID();

            if( dID > 0 )
            {
               pDev = DM->getEqual(dID);

               if(pDev != NULL)
               {
                  //cout << "Attaching device " << pDev->getDeviceName() << " to route " << pXCU->getName() << endl;
                  pXCU->setDevicePointer(pDev);
               }
               else
               {
                  pXCU->resetDevicePointer();
               }
            }
            break;
         }
      case MacroRouteType:
         {
            CtiRouteMacro *pMac = (CtiRouteMacro*)pRte;
            pMac->getRoutePtrList().clear();

            for (int i = 0; i < pMac->getRouteList().length(); i++)
            {
               pMac->getRoutePtrList().insert(RM->getEqual(pMac->getRouteList()[i].getSingleRouteID()));
            }
         }
      default:
         {
            break;
         }
      }
   }

}



