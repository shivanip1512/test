/*-----------------------------------------------------------------------------*
*
* File:   portload
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/portload.cpp-arc  $
* REVISION     :  $Revision: 1.23 $
* DATE         :  $Date: 2008/11/17 17:34:40 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        PORTLOAD.C

    Purpose:
        Download routing and amp mode information to CCU-711's

    The following procedures are contained in this module:
        LoadAllRoutes               LoadPortRoutes
        LoadRemoteRoutes

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        08-24-93    Fixed Failed queue write protection bug      WRO
        9-7-93   Converted to 32 bit                                WRO

   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "porter.h"

#include "portglob.h"
#include "portdecl.h"
#include "c_port_interface.h"
#include "mgr_port.h"
#include "mgr_device.h"
#include "mgr_route.h"
#include "port_base.h"
#include "dev_base.h"
#include "rte_base.h"
#include "rte_ccu.h"
#include "trx_711.h"
#include "dev_ccu.h"
#include "dev_ccu721.h"
#include "std_helper.h"


using namespace std;

using Cti::arrayToRange;
using Cti::Logging::Range::Hex::operator<<;

extern CtiRouteManager    RouteManager;

INT LoadRemoteRoutes(CtiDeviceSPtr Dev);
INT LoadPortRoutes (USHORT Port);

struct loadRemoteRoutes
{
    void operator()(CtiDeviceSPtr &Dev)
    {
        if( Dev && !Dev->isInhibited() )
        {
            LoadRemoteRoutes(Dev);
        }
    }
};

/* Routine to load routes on all remotes on a port */
INT LoadPortRoutes (USHORT Port)
{
    vector<CtiDeviceManager::ptr_type> devices;

    DeviceManager.getDevicesByPortID(Port, devices);

    for_each(devices.begin(), devices.end(), loadRemoteRoutes());

    return ClientErrors::None;
}

/* Routine to load routes into specified CCU */
INT LoadRemoteRoutes(CtiDeviceSPtr Dev)
{
    USHORT            Index;
    USHORT            RouteCount;
    USHORT            AmpMode;
    OUTMESS           *OutMessage;
    CtiRoute          *RouteRecord;

    /* get this remote from database */
    if( Dev && !Dev->isInhibited() )
    {
        /* CCU-721 is handled seperately from the 711 */
        if( Dev->getType() == TYPE_CCU721 )
        {
            using Cti::Devices::Ccu721Device;
            Cti::Devices::Ccu721SPtr ccu = boost::static_pointer_cast<Ccu721Device>(Dev);

            if( (OutMessage = CTIDBG_new OUTMESS) == NULL )
            {
                return ClientErrors::MemoryAccess;
            }

            if( !ccu->buildCommand(OutMessage, Ccu721Device::Command_LoadRoutes) )
            {
                delete OutMessage;
            }
            else
            {
                OutMessage->DeviceID = Dev->getID();
                OutMessage->TargetID = Dev->getID();
                OutMessage->Port     = Dev->getPortID();
                OutMessage->Remote   = Dev->getAddress();
                OutMessage->TimeOut  = TIMEOUT;
                OutMessage->Retry    = 1;
                OutMessage->InLength = 0;
                OutMessage->Priority = MAXPRIORITY - 1;
                OutMessage->EventCode    = NORESULT | ENCODED;  //  used?
                OutMessage->ReturnNexus  = NULL;
                OutMessage->SaveNexus    = NULL;

                if(PortManager.writeQueue(OutMessage))
                {
                    CTILOG_ERROR(dout, "Could not write to port queue for DeviceID "<< OutMessage->DeviceID <<" / Port "<< OutMessage->Port);

                    delete (OutMessage);
                    return ClientErrors::QueueWrite;
                }
            }
        }
        /* Find all non-broadcast 711 transmitters. */
        else if((Dev->getType() == TYPE_CCU711) && (Dev->getAddress() != CCUGLOBAL && Dev->getAddress() != RTUGLOBAL) )
        {
            CtiTransmitter711Info *pInfo = (CtiTransmitter711Info*)Dev->getTrxInfo();

            if(pInfo)
            {
                /* set the initial PARID for routes */
                RouteCount = 0;

                /* now check if this dude has any routes */
                try
                {  // get me some SCOPE...

                    CtiRouteManager::spiterator   rte_itr;

                    /* Now do the routes */
                    for( rte_itr = RouteManager.begin(); rte_itr != RouteManager.end()  ; CtiRouteManager::nextPos(rte_itr))
                    {
                        CtiRouteCCU *CCURouteRecord = (CtiRouteCCU*)rte_itr->second.get();

                        //  we only care about routes on this device
                        if( CCURouteRecord->getCommRoute().getTrxDeviceID() != Dev->getID() )       // if not me.
                            continue;

                        if( RouteCount > 32 )
                            break;

                        /* Allocate some memory */
                        if( (OutMessage = CTIDBG_new OUTMESS) == NULL )
                        {
                            return ClientErrors::MemoryAccess;
                        }

                        /* Load up the queue structure */
                        OutMessage->DeviceID = Dev->getID();
                        OutMessage->TargetID = Dev->getID();
                        OutMessage->Port     = Dev->getPortID();
                        OutMessage->Remote   = Dev->getAddress();
                        OutMessage->TimeOut  = TIMEOUT;
                        OutMessage->Retry    = 1;
                        OutMessage->InLength = 0;
                        OutMessage->Source   = 0;
                        OutMessage->Destination = DEST_DLC;
                        OutMessage->Command  = CMND_WMEMS;
                        OutMessage->Sequence = 0;
                        OutMessage->Priority = MAXPRIORITY - 1;
                        OutMessage->EventCode    = NOWAIT | NORESULT | ENCODED | RCONT;
                        OutMessage->ReturnNexus  = NULL;
                        OutMessage->SaveNexus    = NULL;

                        Index = PREIDL;

                        if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                        {
                            CTILOG_DEBUG(dout, "RouteCount: "<< RouteCount);
                        }

                        /* Load route */
                        OutMessage->Buffer.OutMessage[Index++] = 6;

                        OutMessage->Buffer.OutMessage[Index++] = HIBYTE (2000 + RouteCount);
                        OutMessage->Buffer.OutMessage[Index++] = LOBYTE (2000 + RouteCount);

                        OutMessage->Buffer.OutMessage[Index++] = CCURouteRecord->getBus();
                        OutMessage->Buffer.OutMessage[Index++] = ((CCURouteRecord->getCCUVarBits() & 0x07) << 5) | (CCURouteRecord->getCCUFixBits() & 0x1f);     // FIX FIX FIX ... Verify this with old system

                        OutMessage->Buffer.OutMessage[Index++] =  CCURouteRecord->getStages() & 0x07;
                        if( CCURouteRecord->isDefaultRoute() )
                            OutMessage->Buffer.OutMessage[Index - 1] |= 0x80;

                        if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                        {
                            CTILOG_DEBUG(dout, "Load route - outMessage["<< Index-6 <<":"<< Index-1 <<"]"<<
                                    endl << arrayToRange(&OutMessage->Buffer.OutMessage[Index-6], 6));
                        }

                        /* Load the Route set */
                        OutMessage->Buffer.OutMessage[Index++] = 7;

                        OutMessage->Buffer.OutMessage[Index++] = HIBYTE (3300 + RouteCount);
                        OutMessage->Buffer.OutMessage[Index++] = LOBYTE (3300 + RouteCount);

                        OutMessage->Buffer.OutMessage[Index++] = LOBYTE (RouteCount);
                        OutMessage->Buffer.OutMessage[Index++] = LOBYTE (RouteCount);
                        OutMessage->Buffer.OutMessage[Index++] = LOBYTE (-1);
                        OutMessage->Buffer.OutMessage[Index++] = LOBYTE (-1);

                        if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                        {
                            CTILOG_DEBUG(dout, "Load the Route set - outMessage["<< Index-7 <<":"<< Index-1 <<"]"<<
                                    endl << arrayToRange(&OutMessage->Buffer.OutMessage[Index-7], 7));
                        }

                        /* Load the zone */
                        OutMessage->Buffer.OutMessage[Index++] = 4;

                        OutMessage->Buffer.OutMessage[Index++] = HIBYTE (14000 + RouteCount);
                        OutMessage->Buffer.OutMessage[Index++] = LOBYTE (14000 + RouteCount);

                        OutMessage->Buffer.OutMessage[Index++] = LOBYTE (RouteCount);

                        if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                        {
                            CTILOG_DEBUG(dout, "Load the zone - outMessage["<< Index-4 <<":"<< Index-1 <<"]"<< 
                                    endl << arrayToRange(&OutMessage->Buffer.OutMessage[Index-4], 4));
                        }

                        /* Last SETL */
                        OutMessage->Buffer.OutMessage[Index++] = 0;

                        if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                        {
                            CTILOG_DEBUG(dout, "Last SETL - outMessage["<< Index-1 <<"]"<< 
                                    endl << arrayToRange(&OutMessage->Buffer.OutMessage[Index-1], 1));
                        }

                        /* Thats it so send the message */
                        OutMessage->OutLength = Index - PREIDL + 2;

                        if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                        {
                            CTILOG_DEBUG(dout, "OutLength "<< OutMessage->OutLength);
                        }

                        if(PortManager.writeQueue(OutMessage))
                        {
                            CTILOG_ERROR(dout, "Could not write to port queue for DeviceID "<< OutMessage->DeviceID <<" / Port "<< OutMessage->Port);

                            delete (OutMessage);
                            continue;
                        }
                        else
                        {
                            pInfo->PortQueueEnts++;
                            pInfo->PortQueueConts++;
                        }

                        RouteCount++;
                    }
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }

                /* Allocate some memory for additional functions */
                if( (OutMessage = CTIDBG_new OUTMESS) == NULL )
                {
                    return ClientErrors::MemoryAccess;
                }

                /* Load up the queue structure */
                OutMessage->DeviceID = Dev->getID();
                OutMessage->TargetID = Dev->getID();
                OutMessage->Port = Dev->getPortID();
                OutMessage->Remote = Dev->getAddress();
                OutMessage->TimeOut = TIMEOUT;
                OutMessage->Retry = 1;
                OutMessage->InLength = 0;
                OutMessage->Source = 0;
                OutMessage->Destination = DEST_DLC;
                OutMessage->Command = CMND_WMEMS;
                OutMessage->Sequence = 0;
                OutMessage->Priority = MAXPRIORITY - 1;
                OutMessage->EventCode = NOWAIT | NORESULT | ENCODED | RCONT;
                OutMessage->ReturnNexus = NULL;
                OutMessage->SaveNexus = NULL;

                Index = PREIDL;

                /* Load RTE_CNT */
                OutMessage->Buffer.OutMessage[Index++] = 4;

                OutMessage->Buffer.OutMessage[Index++] = HIBYTE (1001);
                OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1001);

                OutMessage->Buffer.OutMessage[Index++] = (UCHAR)RouteCount;

                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                { 
                    CTILOG_DEBUG(dout, "Final Message - RTE_COUNT - outMessage["<< Index-4 <<":"<< Index-1 <<"]"<<
                            endl << arrayToRange(&OutMessage->Buffer.OutMessage[Index-4], 4));
                }

                switch( ((CtiDeviceCCU *)Dev.get())->getIDLC().getCCUAmpUseType() )  //  CCURouteRecord->getCarrier().getAmpUseType())
                {
                    case RouteAmpAltFail:
                        {
                            AmpMode = LOBYTE (-4);
                            break;
                        }
                    case RouteAmpDefault1Fail2:
                        {
                            AmpMode = LOBYTE (-3);
                            break;
                        }
                    case RouteAmpDefault2Fail1:
                        {
                            AmpMode = LOBYTE (-2);
                            break;
                        }
                    case RouteAmpAlternating:
                        {
                            AmpMode = LOBYTE (-1);
                            break;
                        }
                    case RouteAmp2:
                        {
                            AmpMode = LOBYTE (1);
                            break;
                        }
                    case RouteAmp1:               // Use primary amp exclusively
                    default:
                        {
                            AmpMode = LOBYTE (0);
                            break;
                        }
                }

                /* Load the amp mode */
                OutMessage->Buffer.OutMessage[Index++] = 4;

                OutMessage->Buffer.OutMessage[Index++] = HIBYTE (1003);
                OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1003);

                OutMessage->Buffer.OutMessage[Index++] = LOBYTE (AmpMode);

                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {
                    CTILOG_DEBUG(dout, "Load the amp mode - outMessage["<< Index-4 <<":"<< Index-1 <<"]"<<
                            endl << arrayToRange(&OutMessage->Buffer.OutMessage[Index-4], 4));
                }

                /* Load the DLC Retries */
                OutMessage->Buffer.OutMessage[Index++] = 4;

                OutMessage->Buffer.OutMessage[Index++] = HIBYTE (1006);
                OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1006);

                OutMessage->Buffer.OutMessage[Index++] = 0;

                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {   
                    CTILOG_DEBUG(dout, "Load the DLC Retries - outMessage["<< Index-4 <<":"<< Index-1 <<"]"<<
                            endl << arrayToRange(&OutMessage->Buffer.OutMessage[Index-4], 4));
                }

                /* Load the zone count */
                OutMessage->Buffer.OutMessage[Index++] = 4;

                OutMessage->Buffer.OutMessage[Index++] = HIBYTE (1007);
                OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1007);

                OutMessage->Buffer.OutMessage[Index++] = (UCHAR)RouteCount;

                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {
                    CTILOG_DEBUG(dout, "Load the zone count - outMessage["<< Index-4 <<":"<< Index-1 <<"]"<<
                            endl << arrayToRange(&OutMessage->Buffer.OutMessage[Index-4], 4));
                }

                /* Load the route set count */
                OutMessage->Buffer.OutMessage[Index++] = 4;

                OutMessage->Buffer.OutMessage[Index++] = HIBYTE (1008);
                OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1008);

                OutMessage->Buffer.OutMessage[Index++] = (UCHAR)RouteCount;

                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {
                    CTILOG_DEBUG(dout, "Load the route set count - outMessage["<< Index-4 <<":"<< Index-1 <<"]"<<
                            endl << arrayToRange(&OutMessage->Buffer.OutMessage[Index-4], 4));
                }

                /* Last SETL */
                OutMessage->Buffer.OutMessage[Index++] = 0;

                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {
                    CTILOG_DEBUG(dout, "Last SETL - outMessage["<< Index-1 <<"]"<<
                            endl << arrayToRange(&OutMessage->Buffer.OutMessage[Index-1], 1));
                }

                /* Thats it so send the message */
                OutMessage->OutLength = Index - PREIDL + 2;

                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {
                    CTILOG_DEBUG(dout, "OutLength "<< OutMessage->OutLength);
                }

                if(PortManager.writeQueue(OutMessage))
                {
                    CTILOG_ERROR(dout, "Could not write to port queue for DeviceID "<< OutMessage->DeviceID <<" / Port "<< OutMessage->Port);

                    delete (OutMessage);
                    return ClientErrors::QueueWrite;
                }
                else
                {
                    pInfo->PortQueueEnts++;
                    pInfo->PortQueueConts++;
                }
            }
        }
    }
    return ClientErrors::None;
}
