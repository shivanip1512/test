#pragma warning (disable : 4786)
/*-----------------------------------------------------------------------------*
*
* File:   portload
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/portload.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:24 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma title ( "CCU-711 DLC Algorithm Load Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
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
// #include "btrieve.h"
#include <stdio.h>
#include <string.h>
#include <malloc.h>

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
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

extern CtiRouteManager    RouteManager;

/* Routine to load all routes on a system */
LoadAllRoutes ()
{
    CtiRTDB<CtiPort>::CtiRTDBIterator   itr(PortManager.getMap());

    for( ; ++itr ; )
    {
        LoadPortRoutes (itr.value()->getPortID());
    }

    return(NORMAL);
}


/* Routine to load routes on all remotes on a port */
LoadPortRoutes (USHORT Port)

{
    CtiDevice *Device;

    CtiRTDB< CtiDevice >::CtiRTDBIterator   itr(DeviceManager.getMap());

    for( ; ++itr ; )
    {
        Device = (CtiDevice*)itr.value();

        if( Port == Device->getPortID() )
        {
            LoadRemoteRoutes(Device);
        }
    }

    return(NORMAL);
}


/* Routine to load routes into specified CCU */
LoadRemoteRoutes(CtiDeviceBase *RemoteRecord)
{
    USHORT            Index;
    USHORT            RouteCount;
    USHORT            AmpMode;
    OUTMESS           *OutMessage;
    // CtiDeviceBase     *RemoteRecord;
    CtiRoute          *RouteRecord;

    /* get this remote from database */
    if( NULL != RemoteRecord )
    {
        /* make sure we do this one */
        if( (RemoteRecord->getType()    == TYPE_CCU711)     &&
            (RemoteRecord->getAddress() != CCUGLOBAL        &&
             RemoteRecord->getAddress() != RTUGLOBAL) )
        {

            CtiTransmitter711Info *pInfo = (CtiTransmitter711Info*)RemoteRecord->getTrxInfo();

            /* set the initial PARID for routes */
            RouteCount = 0;

            /* now check if this dude has any routes */

#ifdef OLD_WAY    //  CGP 071999 Must add back in...
            memcpy (RouteRecord.RemoteName, RemoteRecord.RemoteName, STANDNAMLEN);

            if( !(RoutegetRemoteFirst (&RouteRecord)) )
            {
                /* If set amp based on amp in first route */
                if( AmpFailOver & AMP_SWAPPING )
                {
                    if( AmpFailOver & AMP_FAILOVER )
                    {
                        AmpMode = LOBYTE (-4);
                    }
                    else
                    {
                        AmpMode = LOBYTE (-1);
                    }
                }
                else
                {
                    if( RouteRecord.AmpNum == 2 )
                    {
                        if( AmpFailOver & AMP_FAILOVER )
                        {
                            AmpMode = LOBYTE (-2);
                        }
                        else
                        {
                            AmpMode = LOBYTE (1);
                        }
                    }
                    else if( AmpFailOver & AMP_FAILOVER )
                    {
                        AmpMode = LOBYTE (-3);
                    }
                    else
                    {
                        AmpMode = LOBYTE (0);
                    }
                }
                /* Now do the routes */
                do
                {
                    if( RouteCount > 32 )
                        break;

                    /* Lock the route record */
                    if( !(RouteLock (&RouteRecord)) )
                    {
                        RouteRecord.CCUTableEntry = RouteCount;
                        if( RouteFastUpdate (&RouteRecord) )
                        {
                            RouteUnLock (&RouteRecord);
                        }
                    }
                    else
                    {
                        RouteUnLock (&RouteRecord);
                    }

                    /* Allocate some memory */
                    if( (OutMessage = new OUTMESS) == NULL )
                    {
                        return(MEMORY);
                    }

                    /* Load up the queue structure */
                    OutMessage->Port = RemoteRecord.Port;
                    OutMessage->Remote = RemoteRecord.Remote;
                    OutMessage->TimeOut = TIMEOUT;
                    OutMessage->Retry = 1;
                    OutMessage->InLength = 0;
                    OutMessage->Source = 0;
                    OutMessage->Destination = DEST_DLC;
                    OutMessage->Command = CMND_WMEMS;
                    OutMessage->Sequence = 0;
                    OutMessage->Priority = MAXPRIORITY - 1;
                    OutMessage->EventCode = NOWAIT | NORESULT | ENCODED | RCONT;
                    OutMessage->ReturnNexus.NexusState = CTINEXUS_STATE_NULL;
                    OutMessage->SaveNexus.NexusState = CTINEXUS_STATE_NULL;

                    Index = PREIDL;

                    /* Load route */
                    OutMessage->Buffer.OutMessage[Index++] = 6;

                    OutMessage->Buffer.OutMessage[Index++] = HIBYTE (2000 + RouteCount);
                    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (2000 + RouteCount);

                    OutMessage->Buffer.OutMessage[Index++] = RouteRecord.BusNum - 1;
                    OutMessage->Buffer.OutMessage[Index++] = ((RouteRecord.VarBit & 0x07) << 5) | (RouteRecord.FixBit & 0x1f);

                    OutMessage->Buffer.OutMessage[Index++] =  RouteRecord.getStages() & 0x07;
                    if( RouteRecord.Timed )
                        OutMessage->Buffer.OutMessage[Index - 1] |= 0x80;

                    /* Load the Route set */
                    OutMessage->Buffer.OutMessage[Index++] = 7;

                    OutMessage->Buffer.OutMessage[Index++] = HIBYTE (3300 + RouteCount);
                    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (3300 + RouteCount);

                    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (RouteCount);
                    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (RouteCount);
                    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (-1);
                    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (-1);

                    /* Load the zone */
                    OutMessage->Buffer.OutMessage[Index++] = 4;

                    OutMessage->Buffer.OutMessage[Index++] = HIBYTE (14000 + RouteCount);
                    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (14000 + RouteCount);

                    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (RouteCount);

                    /* Last SETL */
                    OutMessage->Buffer.OutMessage[Index++] = 0;

                    /* Thats it so send the message */
                    OutMessage->OutLength = Index - PREIDL + 2;

                    if( PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority) )
                    {
                        printf ("Error Writing to Queue for Port %2hd\n", RemoteRecord.Port);
                        delete (OutMessage);
                        continue;
                    }

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    CCUInfo.findValue(&OutMessage->DeviceID)->PortQueueEnts++;
                    CCUInfo.findValue(&OutMessage->DeviceID)->PortQueueConts++;

                    RouteCount++;

                } while( !(RoutegetRemoteGT (&RouteRecord)) );
            }
#else
            {  // get me some SCOPE...

                CtiRTDB<CtiRoute>::CtiRTDBIterator   rte_itr(RouteManager.getMap());

                /* Now do the routes */
                for( ; ++rte_itr ; )
                {
                    CtiRouteCCU *CCURouteRecord = (CtiRouteCCU*)rte_itr.value();

                    //  we only care about routes on this device
                    if( CCURouteRecord->getCommRoute().getTrxDeviceID() != RemoteRecord->getID() )
                        continue;

                    if( RouteCount > 32 )
                        break;

                    // RouteRecord.CCUTableEntry = RouteCount; // This is used in exactly NO other place...

                    /* Allocate some memory */
                    if( (OutMessage = new OUTMESS) == NULL )
                    {
                        return(MEMORY);
                    }

                    /* Load up the queue structure */
                    OutMessage->DeviceID = RemoteRecord->getID();
                    OutMessage->TargetID = RemoteRecord->getID();
                    OutMessage->Port     = RemoteRecord->getPortID();
                    OutMessage->Remote   = RemoteRecord->getAddress();
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

                    if( PorterDebugLevel & 0x00000001 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Route " << RouteCount << " **** " << endl;
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

                    if( PorterDebugLevel & 0x00000001 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << endl << "**** RouteCount: " << RouteCount << " ****" << endl;
                        dout << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-6]) << " "
                             << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-5]) << " "
                             << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-4]) << " "
                             << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-3]) << " "
                             << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-2]) << " "
                             << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-1]) << endl;
                    }

                    /* Load the Route set */
                    OutMessage->Buffer.OutMessage[Index++] = 7;

                    OutMessage->Buffer.OutMessage[Index++] = HIBYTE (3300 + RouteCount);
                    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (3300 + RouteCount);

                    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (RouteCount);
                    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (RouteCount);
                    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (-1);
                    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (-1);

                    if( PorterDebugLevel & 0x00000001 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-7]) << " "
                             << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-6]) << " "
                             << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-5]) << " "
                             << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-4]) << " "
                             << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-3]) << " "
                             << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-2]) << " "
                             << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-1]) << endl;
                    }

                    /* Load the zone */
                    OutMessage->Buffer.OutMessage[Index++] = 4;

                    OutMessage->Buffer.OutMessage[Index++] = HIBYTE (14000 + RouteCount);
                    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (14000 + RouteCount);

                    OutMessage->Buffer.OutMessage[Index++] = LOBYTE (RouteCount);

                    if( PorterDebugLevel & 0x00000001 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-4]) << " "
                             << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-3]) << " "
                             << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-2]) << " "
                             << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-1]) << endl;
                    }

                    /* Last SETL */
                    OutMessage->Buffer.OutMessage[Index++] = 0;

                    if( PorterDebugLevel & 0x00000001 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-1]) << endl;
                    }

                    /* Thats it so send the message */
                    OutMessage->OutLength = Index - PREIDL + 2;

                    if( PorterDebugLevel & 0x00000001 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "OutLength " << OutMessage->OutLength << endl;
                    }

                    if( PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority) )
                    {
                        printf ("Error Writing to Queue for Port %2hd\n", RemoteRecord->getPortID());
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
#endif
            /* Allocate some memory for additional functions */
            if( (OutMessage = new OUTMESS) == NULL )
            {
                return(MEMORY);
            }

            /* Load up the queue structure */
            OutMessage->DeviceID = RemoteRecord->getID();
            OutMessage->TargetID = RemoteRecord->getID();
            OutMessage->Port = RemoteRecord->getPortID();
            OutMessage->Remote = RemoteRecord->getAddress();
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

            if( PorterDebugLevel & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << endl << "**** Final Message ****" << endl;
                dout << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-4]) << " "
                     << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-3]) << " "
                     << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-2]) << " "
                     << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-1]) << endl;
            }

            switch( ((CtiDeviceCCU *)RemoteRecord)->getIDLC().getCCUAmpUseType() )  //  CCURouteRecord->getCarrier().getAmpUseType())
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

            if( PorterDebugLevel & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-4]) << " "
                     << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-3]) << " "
                     << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-2]) << " "
                     << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-1]) << endl;
            }

            /* Load the DLC Retries */
            OutMessage->Buffer.OutMessage[Index++] = 4;

            OutMessage->Buffer.OutMessage[Index++] = HIBYTE (1006);
            OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1006);

            OutMessage->Buffer.OutMessage[Index++] = 0;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-4]) << " "
                     << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-3]) << " "
                     << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-2]) << " "
                     << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-1]) << endl;
            }

            /* Load the zone count */
            OutMessage->Buffer.OutMessage[Index++] = 4;

            OutMessage->Buffer.OutMessage[Index++] = HIBYTE (1007);
            OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1007);

            OutMessage->Buffer.OutMessage[Index++] = (UCHAR)RouteCount;

            if( PorterDebugLevel & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-4]) << " "
                     << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-3]) << " "
                     << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-2]) << " "
                     << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-1]) << endl;
            }

            /* Load the route set count */
            OutMessage->Buffer.OutMessage[Index++] = 4;

            OutMessage->Buffer.OutMessage[Index++] = HIBYTE (1008);
            OutMessage->Buffer.OutMessage[Index++] = LOBYTE (1008);

            OutMessage->Buffer.OutMessage[Index++] = (UCHAR)RouteCount;

            if( PorterDebugLevel & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-4]) << " "
                     << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-3]) << " "
                     << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-2]) << " "
                     << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-1]) << endl;
            }

            /* Last SETL */
            OutMessage->Buffer.OutMessage[Index++] = 0;

            if( PorterDebugLevel & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << hex << setw(2) << setfill('0') << (int)(OutMessage->Buffer.OutMessage[Index-1]) << endl;
            }

            /* Thats it so send the message */
            OutMessage->OutLength = Index - PREIDL + 2;

            if( PorterDebugLevel & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "OutLength " << OutMessage->OutLength << endl;
            }

            if( PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority) )
            {
                printf ("Error Writing to Queue for Port %2ld\n", RemoteRecord->getPortID());
                delete (OutMessage);
                return(QUEUE_WRITE);
            }
            else
            {
                pInfo->PortQueueEnts++;
                pInfo->PortQueueConts++;
            }
        }
    }
    return(NORMAL);
}
