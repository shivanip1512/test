/*-----------------------------------------------------------------------------*
*
* File:   dlldev
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dlldev.cpp-arc  $
* REVISION     :  $Revision: 1.18 $
* DATE         :  $Date: 2007/07/10 21:01:34 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <iostream>

#include <rw/db/db.h>

#include "mgr_device.h"
#include "mgr_route.h"
#include "mgr_point.h"
#include "dlldefs.h"
#include "devicetypes.h"
#include "msg_pcrequest.h"
#include "rte_base.h"
#include "rte_xcu.h"
#include "rte_macro.h"
#include "dev_dlcbase.h"
#include "dev_grp_emetcon.h"
#include "utility.h"


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


void IM_EX_DEVDB attachRouteManagerToDevices(CtiDeviceManager *DM, CtiRouteManager *RteMgr)
{
    CtiDeviceManager::ptr_type pBase;

    CtiDeviceManager::spiterator itr;

    for(itr = DM->begin(); itr != DM->end(); itr++)
    {
        pBase = itr->second;
        pBase->setRouteManager(RteMgr);
    }
}

void IM_EX_DEVDB attachPointIDDeviceMapToDevices(CtiDeviceManager *DM, PointDeviceMapping *PtTrk)
{
    CtiDeviceManager::ptr_type pBase;

    CtiDeviceManager::spiterator itr;

    for(itr = DM->begin(); itr != DM->end(); itr++)
    {
        pBase = itr->second;
        pBase->setPointDeviceMap(PtTrk);
    }
}

void IM_EX_DEVDB attachTransmitterDeviceToRoutes(CtiDeviceManager *DM, CtiRouteManager *RM)
{
    int            i;
    LONG           dID;
    CtiRouteSPtr   pRte;
    CtiDeviceManager::ptr_type pDev;

    CtiRouteManager::spiterator itr;


    try
    {
        for(itr = RM->begin(); itr != RM->end() ; RM->nextPos(itr))
        {
            pRte = itr->second;

            switch(pRte->getType())
            {
                case RouteTypeCCU:
                case RouteTypeTCU:
                case RouteTypeLCU:
                case RouteTypeRepeater:
                case RouteTypeVersacom:
                case RouteTypeExpresscom:
                case RouteTypeTap:
                case RouteTypeWCTP:
                case RouteTypeSNPP:
                case RouteTypeRTC:
                case RouteTypeSeriesVLMI:
                case RouteTypeForeignPorter:
                {
                    CtiRouteXCU  *pXCU = (CtiRouteXCU*)itr->second.get();         // Wild man, wild!  I guess that holding pRte lets this be ok...

                    dID = pXCU->getCommRoute().getTrxDeviceID();

                    if( dID > 0 )
                    {
                        pDev = DM->getEqual(dID);

                        if(pDev)
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
                case RouteTypeMacro:
                {

                    CtiRouteMacro *pMac = (CtiRouteMacro*)itr->second.get();         // Wild man, wild!  I guess that holding pRte lets this be ok...

                    try
                    {
                        CtiLockGuard< CtiMutex > listguard(pMac->getRouteListMux());    // Lock it so that it cannot conflict with an ExecuteRequest() on the route!!

                        pMac->getRoutePtrList().clear();

                        for(int i = 0; i < pMac->getRouteList().length(); i++)
                        {
                            CtiRouteSPtr pSingleRoute = RM->getEqual(pMac->getRouteList()[i].getSingleRouteID());
                            pMac->getRoutePtrList().insert( pSingleRoute );
                        }
                    }
                    catch(...)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                }
                default:
                {
                    break;
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


