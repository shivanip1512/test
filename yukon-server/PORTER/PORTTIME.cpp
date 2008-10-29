/*-----------------------------------------------------------------------------*
*
* File:   PORTTIME
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PORTTIME.cpp-arc  $
* REVISION     :  $Revision: 1.57 $
* DATE         :  $Date: 2008/10/29 19:17:02 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        PORTTIME.C

    Purpose:
        Routines to handle time syncronization in various remotes and devices

    The following procedures are contained in this module:
        TimeSyncThread              SendTime
        LoadXTimeMessage            LoadBTimeMessage
        LoadIlexTimeMessage         WWVClockSync
        WWVReceiversetup            WWVBufferRead
        WWVBufferWrite

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        8-24-93     Updated to use time sync semaphore          WRO
        9-7-93      Converted to 32 bit                         WRO
        12-1-93     Added Support for UCT-10 WWV Clock          TRH
        1-13-94     Changed potential * bug in WWV routines     TRH

   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include <iostream>


#include <rw\thr\mutex.h>

#include "os2_2w32.h"
#include "cticalls.h"


#include <stdlib.h>

#include <stdio.h>
#include <string.h>
#include <time.h>

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "porter.h"
#include "portdecl.h"
#include "elogger.h"

#include "portglob.h"

#include "c_port_interface.h"
#include "port_base.h"
#include "mgr_port.h"
#include "mgr_device.h"
#include "dev_base.h"
#include "dev_ccu.h"
#include "dev_ccu721.h"
#include "dev_ilex.h"
#include "dev_mct4xx.h"
#include "mgr_route.h"

#include "logger.h"
#include "guard.h"
#include "trx_info.h"
#include "trx_711.h"
#include "thread_monitor.h"

#include "prot_welco.h"
#include "prot_lmi.h"
using namespace std;

extern ULONG TimeSyncRate;

extern HCTIQUEUE*       QueueHandle(LONG pid);

extern CtiRouteManager RouteManager;

IM_EX_CTIBASE extern RWMutexLock coutMux;

/* Put WWV Receiver models here */
#define UTSTEN  1
#define N81     0
#define E71     1

USHORT WWVModel = 0;
USHORT WWVComMode = 0;


ULONG getNextTimeSync()
{
    ULONG next = nextScheduledTimeAlignedOnRate(CtiTime::now(), TimeSyncRate).seconds();

    if( TimeSyncRate >= 300 )
    {
        //  for any time sync rates slower than 5 minutes, offset them by 2.5 minutes
        //    e.g. a rate of 1 hour (3600 seconds) will go at 2.5 minutes after the hour
        next += 150;
    }

    return next;
}

static void apply711TimeSync(const long unusedid, CtiDeviceSPtr RemoteRecord, void *lprtid)
{
    LONG PortID = (LONG)lprtid;

    if((RemoteRecord->getPortID() != PortID) || (RemoteRecord->getAddress() == 0xffff) || (RemoteRecord->isInhibited()) )
    {
        return;
    }

    OUTMESS *OutMessage;

    switch(RemoteRecord->getType())
    {
    case TYPE_CCU711:
        /* Allocate some memory */
        if((OutMessage = CTIDBG_new OUTMESS) == NULL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            return;
        }

        /* send a time sync to this guy */
        OutMessage->DeviceID = RemoteRecord->getID();
        OutMessage->Port   = RemoteRecord->getPortID();
        OutMessage->Remote = RemoteRecord->getAddress();
        OutMessage->TimeOut = TIMEOUT;
        OutMessage->Retry = 0;
        OutMessage->OutLength = 10;
        OutMessage->InLength = 0;
        OutMessage->Source = 0;
        OutMessage->Destination = DEST_TSYNC;
        OutMessage->Command = CMND_XTIME;
        OutMessage->Sequence = 0;
        OutMessage->Priority = MAXPRIORITY;
        OutMessage->EventCode = NOWAIT | NORESULT | ENCODED | TSYNC | RCONT;
        OutMessage->ReturnNexus = NULL;
        OutMessage->SaveNexus = NULL;
        OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
        OutMessage->ExpirationTime = getNextTimeSync();

        if(PortManager.writeQueue(OutMessage->Port, OutMessage->Request.GrpMsgID, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
        {
            printf ("Error Writing to Queue for Port %2hd\n", RemoteRecord->getPortID());
            delete (OutMessage);
        }
        else
        {
            CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)RemoteRecord->getTrxInfo();
            /* Increment the number of entries for this guys queue */
            p711Info->PortQueueEnts++;
            p711Info->PortQueueConts++;
        }

        break;

    default:
        break;
    }
}

static void apply710TimeSync(const long unusedid, CtiDeviceSPtr RemoteRecord, void *lprtid)
{
    LONG portid = (LONG)lprtid;

    if((RemoteRecord->getPortID() != portid) || (RemoteRecord->getAddress() == 0xffff) || (RemoteRecord->isInhibited()) )
    {
        return;
    }

    if( RemoteRecord->getType() != TYPE_CCU700 &&
        RemoteRecord->getType() != TYPE_CCU710 )
    {
        return;
    }

    try
    {
        OUTMESS         *OutMessage;
        CtiRouteSPtr    RouteRecord;

        CtiRouteManager::coll_type::reader_lock_guard_t guard(RouteManager.getLock());
        CtiRouteManager::spiterator rte_itr;

        //  Walk down the routes for this ccu and pick out the time sync ("default") routes
        for(rte_itr = RouteManager.begin(); rte_itr != RouteManager.end(); CtiRouteManager::nextPos(rte_itr))
        {
            RouteRecord = rte_itr->second;

            if(RouteRecord->getTrxDeviceID() == RemoteRecord->getID() && RouteRecord->isDefaultRoute())
            {
                BSTRUCT message;
                OUTMESS *OutMessage = CTIDBG_new OUTMESS;

                if( OutMessage )
                {
                    //  load up all of the port/route specific items
                    OutMessage->DeviceID  = RemoteRecord->getID();
                    OutMessage->Port      = portid;
                    OutMessage->Remote    = RemoteRecord->getAddress();
                    OutMessage->TimeOut   = TIMEOUT;
                    OutMessage->Retry     = 0;
                    OutMessage->Sequence  = 0;
                    OutMessage->Priority  = MAXPRIORITY;
                    OutMessage->EventCode = NOWAIT | NORESULT | DTRAN | BWORD | TSYNC;
                    OutMessage->Command   = CMND_DTRAN;
                    OutMessage->InLength  = 0;
                    OutMessage->ReturnNexus = NULL;
                    OutMessage->SaveNexus   = NULL;
                    OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
                    OutMessage->ExpirationTime = getNextTimeSync();

                    OutMessage->Buffer.BSt.Port     = RemoteRecord->getPortID();
                    OutMessage->Buffer.BSt.Remote   = RemoteRecord->getAddress();
                    OutMessage->Buffer.BSt.Address  = CtiDeviceDLCBase::BroadcastAddress;
                    OutMessage->Buffer.BSt.Function = CtiDeviceMCT::Memory_TSyncPos;
                    OutMessage->Buffer.BSt.Length   = CtiDeviceMCT::Memory_TSyncLen;
                    OutMessage->Buffer.BSt.IO       = Cti::Protocol::Emetcon::IO_Write;
                    //  we don't fill in the data because it's filled in by RefreshMCTTimeSync() later on

                    //  this should all be filled in by the route's ExecuteRequest
                    OutMessage->Buffer.BSt.DlcRoute.Amp        = ((CtiDeviceIDLC *)(RemoteRecord.get()))->getIDLC().getAmp();
                    OutMessage->Buffer.BSt.DlcRoute.Bus        = RouteRecord->getBus();
                    OutMessage->Buffer.BSt.DlcRoute.RepVar     = RouteRecord->getCCUVarBits();
                    OutMessage->Buffer.BSt.DlcRoute.RepFixed   = RouteRecord->getCCUFixBits();
                    OutMessage->Buffer.BSt.DlcRoute.Stages     = 0;  //  must set the stages to 0 on a CCU 710

                    //  Ideally, use something like this instead of the above code...
                    //RouteRecord->ExecuteRequest();
                    //  ... but because we're not executing on the route, we have to do this manually
                    Cti::Protocol::Emetcon::buildBWordMessage(OutMessage);

                    if(PortManager.writeQueue(OutMessage->Port, OutMessage->Request.GrpMsgID, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
                    {
                        printf ("Error Writing to Queue for Port %2hd\n", portid);
                        delete (OutMessage);
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - unable to allocate OUTMESS for time sync on portid " << portid << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

static void applyDeviceTimeSync(const long unusedid, CtiDeviceSPtr RemoteRecord, void *lprtid)
{
    LONG PortID = (LONG)lprtid;
    OUTMESS *OutMessage;
    int i;
    ULONG           Index;


    if(!RemoteRecord->isInhibited() && RemoteRecord->getPortID() == PortID)
    {
        if(RemoteRecord->getAddress() == 0xffff)
        {
            /* Generate a time sync for this guy */
            switch(RemoteRecord->getType())
            {
            case TYPE_TDMARKV:
                /* Allocate some memory */
                if((OutMessage = CTIDBG_new OUTMESS) == NULL)
                {
                    return;
                }

                Index = PREIDLEN;

                /* Make sure the meter is ready for the password */
                OutMessage->Buffer.OutMessage[Index++] = '\r';
                OutMessage->Buffer.OutMessage[Index++] = '\0';

                /* Load the password into the buffer */
                cerr << __FILE__ << " (" << __LINE__ << ") This may break.. CGP 072599" << endl;

                for(i = 0; i < STANDNAMLEN; i++)
                {
                    if(RemoteRecord->getPassword()[i] == ' ')
                    {
                        break;
                    }
                    OutMessage->Buffer.OutMessage[Index++] = RemoteRecord->getPassword()[i];
                }

                OutMessage->Buffer.OutMessage[Index++] = '\r';
                OutMessage->Buffer.OutMessage[Index++] = '\0';

                /* Load the ID command */
                if(RemoteRecord->getAddress() != 0)
                {
                    OutMessage->Buffer.OutMessage[Index++] = 'I';
                    OutMessage->Buffer.OutMessage[Index++] = 'D';
                    OutMessage->Buffer.OutMessage[Index++] = '\r';
                    OutMessage->Buffer.OutMessage[Index++] = '\0';
                }

                /* Load the TI command */
                OutMessage->Buffer.OutMessage[Index++] = 'T';
                OutMessage->Buffer.OutMessage[Index++] = 'I';
                OutMessage->Buffer.OutMessage[Index++] = '\r';
                OutMessage->Buffer.OutMessage[Index++] = '\0';

                /* Load the LO command */
                OutMessage->Buffer.OutMessage[Index++] = 'L';
                OutMessage->Buffer.OutMessage[Index++] = 'O';
                OutMessage->Buffer.OutMessage[Index++] = '\r';
                OutMessage->Buffer.OutMessage[Index++] = '\0';

                /* Load all the other stuff that is needed */
                OutMessage->DeviceID = RemoteRecord->getID();
                OutMessage->Port     = RemoteRecord->getPortID();
                OutMessage->Remote   = RemoteRecord->getAddress();
                // memcpy (OutMessage->DeviceName, RemoteRecord->getDeviceName(), STANDNAMLEN);
                OutMessage->TimeOut     = 2;
                OutMessage->OutLength   = Index - PREIDLEN;
                OutMessage->InLength    = -1;
                OutMessage->Sequence    = 0;
                OutMessage->Priority    = MAXPRIORITY - 2;
                OutMessage->EventCode   = NOWAIT | NORESULT | ENCODED | TSYNC;
                OutMessage->ReturnNexus = NULL;
                OutMessage->SaveNexus   = NULL;
                OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
                OutMessage->ExpirationTime = getNextTimeSync();

                if(PortManager.writeQueue(OutMessage->Port, OutMessage->Request.GrpMsgID, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
                {
                    printf ("Error Writing to Queue for Port %2hd\n", RemoteRecord->getPortID());
                    delete (OutMessage);
                }
                break;

            default:
                break;

            }
            return;
        }

        switch(RemoteRecord->getType())
        {
        case TYPE_ILEXRTU:
            /* Allocate some memory */
            if((OutMessage = CTIDBG_new OUTMESS) == NULL)
            {
                return;
            }

            ILEXHeader (OutMessage->Buffer.OutMessage + PREIDLEN, RemoteRecord->getAddress(), ILEXTIMESYNC, TIMESYNC1, TIMESYNC2);

            OutMessage->Buffer.OutMessage[PREIDLEN + 2] = ILEXSETTIME;


            /* send a time sync to this guy */
            OutMessage->DeviceID = RemoteRecord->getID();
            OutMessage->Port     = RemoteRecord->getPortID();
            OutMessage->Remote   = RemoteRecord->getAddress();
            OutMessage->TimeOut     = 2;
            OutMessage->Retry       = 0;
            OutMessage->OutLength   = ILEXTIMELENGTH;
            OutMessage->InLength    = 0;
            OutMessage->Source      = 0;
            OutMessage->Destination = 0;
            OutMessage->Sequence    = 0;
            OutMessage->Priority    = MAXPRIORITY - 2;
            OutMessage->EventCode   = NOWAIT | NORESULT | ENCODED | TSYNC;
            OutMessage->ReturnNexus = NULL;
            OutMessage->SaveNexus   = NULL;
            OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
            OutMessage->ExpirationTime = getNextTimeSync();


#if 0
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Sending ILEX time sync to remote:" << endl;
                dout << "device: " << OutMessage->DeviceID << endl;
                dout << "port:   " << OutMessage->Port << endl;
                dout << "remote: " << OutMessage->Remote << endl;
            }
#endif

            if(PortManager.writeQueue(OutMessage->Port, OutMessage->Request.GrpMsgID, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
            {
                printf ("Error Writing to Queue for Port %2hd\n", RemoteRecord->getPortID());
                delete (OutMessage);
            }

            break;

        case TYPE_WELCORTU:
        case TYPE_VTU:
            {
                if(RemoteRecord->getAddress() != RTUGLOBAL)     // 20050919 CGP: Timesyncs not supported on the RTU (ODEC RTU implementation) to the global address
                {
                    /* Allocate some memory */
                    if((OutMessage = CTIDBG_new OUTMESS) == NULL)
                    {
                        return;
                    }

                    OutMessage->Buffer.OutMessage[5] = IDLC_TIMESYNC | 0x80;
                    OutMessage->Buffer.OutMessage[6] = 7;

                    /* send a time sync to this guy */
                    OutMessage->DeviceID = RemoteRecord->getID();
                    OutMessage->Port     = RemoteRecord->getPortID();
                    OutMessage->Remote   = RemoteRecord->getAddress();
                    OutMessage->TimeOut     = 2;
                    OutMessage->Retry       = 0;
                    OutMessage->OutLength   = 7;
                    OutMessage->InLength    = 0;
                    OutMessage->Source      = 0;
                    OutMessage->Destination = 0;
                    OutMessage->Sequence    = 0;
                    OutMessage->Priority    = MAXPRIORITY - 2;
                    OutMessage->EventCode   = NOWAIT | NORESULT | ENCODED | TSYNC;
                    OutMessage->ReturnNexus = NULL;
                    OutMessage->SaveNexus   = NULL;
                    OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
                    OutMessage->ExpirationTime = getNextTimeSync();

                    if(PortManager.writeQueue(OutMessage->Port, OutMessage->Request.GrpMsgID, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
                    {
                        printf ("Error Writing to Queue for Port %2hd\n", RemoteRecord->getPortID());
                        delete (OutMessage);
                    }
                }

                break;
            }
        case TYPE_SERIESVLMIRTU:
            {
                //  Allocate some memory
                if((OutMessage = CTIDBG_new OUTMESS) == NULL)
                {
                    return;
                }

                //  send a time sync to this guy
                OutMessage->DeviceID    = RemoteRecord->getID();
                OutMessage->Port        = RemoteRecord->getPortID();
                OutMessage->Remote      = RemoteRecord->getAddress();
                OutMessage->TimeOut     = 2;
                OutMessage->Retry       = 0;
                OutMessage->Sequence    = CtiProtocolLMI::Sequence_TimeSync;  //  a relatively unique value, just for safety's sake
                OutMessage->Priority    = MAXPRIORITY - 2;
                OutMessage->EventCode   = NOWAIT | NORESULT | ENCODED | TSYNC;
                OutMessage->ReturnNexus = NULL;
                OutMessage->SaveNexus   = NULL;
                OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
                OutMessage->ExpirationTime = getNextTimeSync();

                if(PortManager.writeQueue(OutMessage->Port, OutMessage->Request.GrpMsgID, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
                {
                    printf ("Error Writing to Queue for Port %2hd\n", RemoteRecord->getPortID());
                    delete (OutMessage);
                }

                break;
            }

        case TYPE_CCU721:
            {
                /* Allocate some memory */
                if((OutMessage = CTIDBG_new OUTMESS) == NULL)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    return;
                }

                using Cti::Device::CCU721;
                boost::shared_ptr<CCU721> ccu = boost::static_pointer_cast<CCU721>(RemoteRecord);

                ccu->buildCommand(OutMessage, CCU721::Command_Timesync);

                OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
                OutMessage->ExpirationTime = getNextTimeSync();

                if(PortManager.writeQueue(OutMessage->Port, OutMessage->Request.GrpMsgID, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
                {
                    printf ("Error Writing to Queue for Port %2hd\n", RemoteRecord->getPortID());
                    delete (OutMessage);
                }

                break;
            }
        default:
            break;
        }
    }
}


//  send out the 400-series time sync on all default routes on this port, since we can't use the normal CCU broadcast format
static void applyMCT400TimeSync(const long key, CtiRouteSPtr pRoute, void* d)
{
    long portid = (long)d;
    CtiDeviceSPtr RemoteRecord;

    try
    {
        RemoteRecord = DeviceManager.getDeviceByID(pRoute->getTrxDeviceID());

        //  make sure the route's transmitting device is on this port...  and make sure this is a default route
        if( RemoteRecord &&
            (RemoteRecord->getType() == TYPE_CCU700 ||
             RemoteRecord->getType() == TYPE_CCU710 ||
             RemoteRecord->getType() == TYPE_CCU711) &&  //  note that we're not including the 721 here - it has timesync broadcast abaility of its own
            (RemoteRecord->getPortID() == portid) && pRoute->isDefaultRoute() )
        {
            BSTRUCT message;
            OUTMESS *OutMessage = CTIDBG_new OUTMESS;

            bool stages_supported = !(RemoteRecord->getType() == TYPE_CCU700 || RemoteRecord->getType() == TYPE_CCU710);

            if( OutMessage )
            {
                //  load up all of the port/route specific items
                OutMessage->DeviceID  = pRoute->getTrxDeviceID();
                OutMessage->Port      = portid;
                OutMessage->Remote    = RemoteRecord->getAddress();
                OutMessage->TimeOut   = TIMEOUT;
                OutMessage->Retry     = 0;
                OutMessage->Sequence  = 0;
                OutMessage->Priority  = MAXPRIORITY;
                OutMessage->EventCode = NOWAIT | NORESULT | DTRAN | BWORD | TSYNC;
                OutMessage->Command   = CMND_DTRAN;
                OutMessage->InLength  = 0;
                OutMessage->ReturnNexus = NULL;
                OutMessage->SaveNexus   = NULL;
                OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
                OutMessage->ExpirationTime = getNextTimeSync();

                OutMessage->Buffer.BSt.Port    = RemoteRecord->getPortID();
                OutMessage->Buffer.BSt.Remote  = RemoteRecord->getAddress();
                //  this is key - this, and the TSYNC flag, are what get the 400-series time loaded into the message
                OutMessage->Buffer.BSt.Address  = CtiDeviceMCT4xx::UniversalAddress;
                OutMessage->Buffer.BSt.Function = CtiDeviceMCT4xx::FuncWrite_TSyncPos;
                OutMessage->Buffer.BSt.Length   = CtiDeviceMCT4xx::FuncWrite_TSyncLen;
                OutMessage->Buffer.BSt.IO       = Cti::Protocol::Emetcon::IO_Function_Write;
                //  we don't fill in the data because it's filled in by RefreshMCTTimeSync() later on

                OutMessage->Buffer.BSt.DlcRoute.Amp        = ((CtiDeviceIDLC *)(RemoteRecord.get()))->getIDLC().getAmp();
                OutMessage->Buffer.BSt.DlcRoute.Bus        = pRoute->getBus();
                OutMessage->Buffer.BSt.DlcRoute.RepVar     = pRoute->getCCUVarBits();
                OutMessage->Buffer.BSt.DlcRoute.RepFixed   = pRoute->getCCUFixBits();
                OutMessage->Buffer.BSt.DlcRoute.Stages     = (stages_supported)?(pRoute->getStages()):(0);  //  must set stages to 0 or the timesync will fail on the 700/710

                //  because we're not executing on the route, we have to do this manually
                Cti::Protocol::Emetcon::buildBWordMessage(OutMessage);

                if(PortManager.writeQueue(OutMessage->Port, OutMessage->Request.GrpMsgID, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
                {
                    printf ("Error Writing to Queue for Port %2hd\n", portid);
                    delete (OutMessage);
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - unable to allocate OUTMESS for time sync on portid " << portid << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION while sending MCT 400 series timesyncs **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

}


/* Routine to generate needed time sync messages */
static void applyPortSendTime(const long unusedid, CtiPortSPtr PortRecord, void *unusedPtr)
{
    ULONG       i, j;
    OUTMESS     *OutMessage;
    CtiDeviceSPtr RemoteRecord;

    {
        /* check for a remote port */
        if(PortRecord->getName()[0] == '@') return;
        if(PortRecord->isInhibited()) return;

        if(gForeignCCUPorts.find(PortRecord->getPortID()) != gForeignCCUPorts.end()) return;

        if(PortRecord->getProtocolWrap() == ProtocolWrapIDLC)
        {
            /* make sure that the broadcast ccu is defined */
            if(RemoteRecord = DeviceManager.RemoteGetPortRemoteEqual (PortRecord->getPortID(), CCUGLOBAL))
            {
                if(RemoteRecord->isInhibited())
                {
                    return;
                }

                /* Allocate some memory */
                if((OutMessage = CTIDBG_new OUTMESS) == NULL)
                {
                    return;
                }

                /* send a time sync to this guy */
                OutMessage->DeviceID = RemoteRecord->getID();
                OutMessage->Port = PortRecord->getPortID();
                OutMessage->Remote = CCUGLOBAL;
                OutMessage->TimeOut = TIMEOUT;
                OutMessage->Retry = 0;
                OutMessage->OutLength = 10;
                OutMessage->InLength = 0;
                OutMessage->Source = 0;
                OutMessage->Destination = DEST_TSYNC;
                OutMessage->Command = CMND_XTIME;
                OutMessage->Sequence = 0;
                OutMessage->Priority = MAXPRIORITY;
                OutMessage->EventCode = NOWAIT | NORESULT | ENCODED | TSYNC;
                OutMessage->ReturnNexus = NULL;
                OutMessage->SaveNexus = NULL;
                OutMessage->MessageFlags = MessageFlag_ApplyExclusionLogic;
                OutMessage->ExpirationTime = getNextTimeSync();

                if(PortRecord->writeQueue(OutMessage->Request.GrpMsgID, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority, PortThread))
                {
                    printf ("Error Writing to Queue for Port %2hd\n", PortRecord->getPortID());
                    delete (OutMessage);
                }
            }
            else
            {
                /* Broadcast ccu does not exist on this port */
                CtiPortManager::coll_type::reader_lock_guard_t guard(PortManager.getLock());  // this applyFunc Writes to the PortManager queues!
                DeviceManager.apply(apply711TimeSync, (void*)PortRecord->getPortID());
            }
        }
        else
        {
            /* we need to walk through and generate to 710's on this port */
            CtiPortManager::coll_type::reader_lock_guard_t guard(PortManager.getLock());  // this applyFunc Writes to the PortManager queues!
            DeviceManager.apply(apply710TimeSync, (void*)PortRecord->getPortID());
        }

        {
            /* Now check for Anything not covered by above */
            CtiPortManager::coll_type::reader_lock_guard_t guard(PortManager.getLock());  // this applyFunc Writes to the PortManager queues!
            DeviceManager.apply(applyDeviceTimeSync, (void*)PortRecord->getPortID());

            CtiDeviceManager::coll_type::reader_lock_guard_t dvguard(DeviceManager.getLock());  // Deadlock avoidance!
            RouteManager.apply(applyMCT400TimeSync, (void*)PortRecord->getPortID());
        }
    }

    return;
}

/* Routine to generate the basic time sync messages... time filled in at port */
VOID TimeSyncThread (PVOID Arg)
{
    HANDLE WWVPortHandle = (HANDLE) NULL;
    struct timeb TimeB;
    ULONG EventWait;
    DWORD dwWait = 0;
    CtiTime nowTime, lastTickleTime, lastReportTime;
    CtiTime nextTime;
    UINT sanity = 0;

    /* See if we should even be running */
    if(TimeSyncRate <= 0)
    {
        /* We are history */
        _endthread();
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " TimeSyncThread started as TID:  " << CurrentTID() << " Sync every " << TimeSyncRate << " seconds" << endl;
    }


    /* Let the port routines get started */
    if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[ P_QUIT_EVENT ], 60000L) )
    {
        return;     // If we get this we never really got to run...
    }

    /* send out time syncs for the first time... */
    PortManager.apply(applyPortSendTime, NULL);

    HANDLE hTimeSyncArray[] = {
        hPorterEvents[P_QUIT_EVENT],
        hPorterEvents[P_TIMESYNC_EVENT]
    };

    /* loop doing time sync at 150 seconds after the hour */
    for(; PorterQuit != TRUE ;)
    {
        if(lastTickleTime.seconds() < (lastTickleTime.now().seconds() - CtiThreadMonitor::StandardTickleTime))
        {
            if(lastReportTime.seconds() < (lastReportTime.now().seconds() - CtiThreadMonitor::StandardMonitorTime))
            {
                lastReportTime = lastReportTime.now();
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Time Sync Thread active. TID:  " << rwThreadId() << endl;
            }

            CtiThreadRegData *data;
            //This is an odd duck, it can sleep for 1 hour at a time (currently set time), we will wait for 1 hour + 5 minutes
            data = CTIDBG_new CtiThreadRegData( GetCurrentThreadId(), "Time Sync Thread", CtiThreadRegData::None, TimeSyncRate + CtiThreadMonitor::StandardMonitorTime );
            ThreadMonitor.tickle( data );
            lastTickleTime = lastTickleTime.now();
        }

        /* Figure out how long to wait */
        nowTime = nowTime.now();

        // CTIResetEventSem (TimeSyncSem, &PostCount);
        ResetEvent(hPorterEvents[P_TIMESYNC_EVENT]);

        EventWait = 1000L * (getNextTimeSync() - nowTime.seconds());

        dwWait = WaitForMultipleObjects(2, hTimeSyncArray, FALSE, EventWait);

        if(dwWait != WAIT_TIMEOUT)
        {
            switch( dwWait - WAIT_OBJECT_0 )
            {
            case WAIT_OBJECT_0: // P_QUIT_EVENT:
                {
                    PorterQuit = TRUE;
                    continue;            // the for loop
                }
            case WAIT_OBJECT_0 + 1:     // P_TIMESYNC_EVENT:
                {
                    break;
                }
            default:
                {
                    Sleep(1000);
                    continue;
                }
            }
        }

        /* Send time Sync messages */
        PortManager.apply(applyPortSendTime, NULL);
    }
}

/* Routine called to load an XTIME message */
LoadXTimeMessage (BYTE *Message)
{
    ULONG Mod8Time;
    struct timeb TimeB;
    struct tm *TimeSt;

    /* get the time from the system */
    UCTFTime (&TimeB);

    TimeSt = UCTLocalTime (TimeB.time, TimeB.dstflag);

    /* Build up the time sync message */
    Message[6] = HIBYTE (TimeSt->tm_year + 1900);
    Message[7] = LOBYTE (TimeSt->tm_year + 1900);

    Message[8] = HIBYTE (TimeSt->tm_yday + 1);
    Message[9] = LOBYTE (TimeSt->tm_yday + 1);

    Message[10] = TimeSt->tm_wday + 1;

    Message[11] = TimeSt->tm_hour / 8;

    Mod8Time = (ULONG)(TimeSt->tm_hour % 8) * 3600L + (ULONG)TimeSt->tm_min * 60L + (ULONG)TimeSt->tm_sec;

    Message[12] = HIBYTE (Mod8Time);
    Message[13] = LOBYTE (Mod8Time);

    return(NORMAL);
}


RefreshMCTTimeSync(OUTMESS *OutMessage)
{
    OUTMESS MyOutMessage;
    struct timeb TimeB;
    struct tm TimeSt;
    USHORT EmetDay;
    USHORT Hour;
    USHORT EmetHTime;
    USHORT EmetFTime;

    USHORT BCH;

    int address   = 0,
        wordcount = 0,
        function  = 0,
        io        = 0;

    int length    = 0;


    //  this is where the processed B word starts...
    //    note that we should NEVER get an unprocessed B word through here;  only DTRAN messages, NEVER queued
    unsigned char *b_word = OutMessage->Buffer.OutMessage + PREIDLEN + PREAMLEN;
    unsigned char timesync_message[13];

    address  |= (b_word[1] & 0x0f) << 18;
    address  |=  b_word[2]         << 10;
    address  |=  b_word[3]         <<  2;
    address  |= (b_word[4] & 0xc0) >>  6;

    wordcount = (b_word[4] & 0x30) >> 4;

    function |= (b_word[4] & 0x0f) << 4;
    function |= (b_word[5] & 0xf0) >> 4;

    io        = (b_word[5] & 0x0c) >> 2;

    if( address == CtiDeviceMCT4xx::UniversalAddress
          || (io == Cti::Protocol::Emetcon::IO_Function_Write
                && function  == CtiDeviceMCT4xx::FuncWrite_TSyncPos
                && wordcount == 2) )  //  the 4xx has 6 bytes to write - two C words
    {
        length = CtiDeviceMCT4xx::loadTimeSync(timesync_message);
    }
    else if( address == CtiDeviceDLCBase::BroadcastAddress
               || (io == Cti::Protocol::Emetcon::IO_Write
                     && function  == CtiDeviceMCT::Memory_TSyncPos
                     && wordcount == 1) )  //  non-400-series MCT timesyncs have 5 bytes to write - one C word
    {
        //  this is the normal MCT timesync
        UCTFTime (&TimeB);

        UCTLocoTime (TimeB.time, TimeB.dstflag, &TimeSt);

        //  figure out how many 15 second intervals left in this 5 minutes
        EmetFTime = 20 - ((TimeSt.tm_min % 5) * 60 + TimeSt.tm_sec) / 15;

        //  figure out how many AM/PMs left in the week
        Hour = TimeSt.tm_hour;
        EmetDay = (7 - TimeSt.tm_wday) * 2;
        if(Hour > 11)
        {
            EmetDay--;
            Hour -= 12;
        }

        //  figure out how many 5 minute periods left in this 12 hours
        EmetHTime = 144 - ((Hour * 12) + (TimeSt.tm_min / 5));

        timesync_message[0] = EmetFTime;
        timesync_message[1] = EmetHTime;
        timesync_message[2] = EmetDay;
        timesync_message[3] = DLCFreq1;  //  these are globals based on the system frequencies
        timesync_message[4] = DLCFreq2;  //    (12.5, 9.6, etc)

        length = 5;

        //OutMessage->Destination = 0;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - unknown timesync in RefreshMCTTimeSync() (address = " << address << ", io = " << io << ", function = " << function << ", wordcount = " << wordcount << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    //  lay it over the original message
    C_Words(OutMessage->Buffer.OutMessage + PREIDLEN + PREAMLEN + BWORDLEN,
            timesync_message,
            length);

    return(NORMAL);
}


/* Routine to stuff the two byte header */
ILEXHeader (PBYTE Header,          /* Pointer to message */
            USHORT Remote,          /* RTU Remote */
            USHORT Function,        /* Function code */
            USHORT SubFunction1,    /* High order sub function code */
            USHORT SubFunction2)    /* Low order sub function code */

{
    Header[0] = (Function & 0x0007);
    Header[0] |= LOBYTE ((Remote << 5) & 0xe0);
    if (SubFunction1)
        Header[0] |= 0x10;
    if (SubFunction2)
        Header[0] |= 0x08;
    Header[1] = LOBYTE (Remote >> 3);
    return (NORMAL);

}

/* Routine to load up time for an ilex rtu */
LoadILEXTimeMessage (BYTE *Message, USHORT MilliSecsSkew)
{
    ULONG MilliTime;
    struct timeb TimeB;
    struct tm TimeSt;

    /* get the time from the system */
    UCTFTime (&TimeB);

    TimeB.millitm += MilliSecsSkew;

    /* Add in the extra seconds */
    TimeB.time += (TimeB.millitm / 1000);

    /* Readjust milliseconds */
    TimeB.millitm %= 1000;

    UCTLocoTime (TimeB.time,
                 TimeB.dstflag,
                 &TimeSt);

    /* Calculate the milliseconds */
    MilliTime = ((TimeSt.tm_hour * 60L + TimeSt.tm_min) * 60L + TimeSt.tm_sec) * 1000L + TimeB.millitm;

    /* Move it into the message */
    Message[2] |= (HIBYTE (HIUSHORT(MilliTime)) << 5) & 0xE0;
    Message[3] = LOBYTE (HIUSHORT (MilliTime));
    Message[4] = HIBYTE (LOUSHORT (MilliTime));
    Message[5] = LOBYTE (LOUSHORT (MilliTime));

    /* Load up the Day of week and day of month */
    Message[6] = LOBYTE ((TimeSt.tm_wday + 1) << 5 | TimeSt.tm_mday);

    /* Load the day of the month */
    Message[7] = LOBYTE (TimeSt.tm_mon + 1);

    /* Load Up the year */
    Message[8] = HIBYTE (TimeSt.tm_year + 1900);
    Message[9] = LOBYTE (TimeSt.tm_year + 1900);

    return(NORMAL);
}


/* Routine to load up time for a Welco rtu */
LoadWelcoTimeMessage (BYTE *Message,
                      USHORT MilliSecsSkew)
{
    struct timeb TimeB;
    struct tm TimeSt;

    /* get the time from the system */
    UCTFTime (&TimeB);

    TimeB.millitm += MilliSecsSkew;

    /* Add in the extra seconds */
    TimeB.time += (TimeB.millitm / 1000);

    /* Readjust milliseconds */
    TimeB.millitm %= 1000;

    UCTLocoTime (TimeB.time,
                 TimeB.dstflag,
                 &TimeSt);

    /* Move it into the message */
    Message[0] = TimeSt.tm_mon + 1;
    Message[1] = TimeSt.tm_mday;
    Message[2] = TimeSt.tm_hour;
    Message[3] = TimeSt.tm_min;
    Message[4] = TimeSt.tm_sec;

    /* Load the milliseconds */
    Message[5] = LOBYTE (TimeB.millitm);
    Message[6] = HIBYTE (TimeB.millitm);

    return(NORMAL);
}


/* Routine to load up time for a Welco rtu */
LoadSES92TimeMessage (BYTE *Message,
                      USHORT MilliSecsSkew)
{
    struct timeb TimeB;
    struct tm TimeSt;

    /* get the time from the system */
    UCTFTime (&TimeB);

    TimeB.millitm += MilliSecsSkew;

    /* Add in the extra seconds */
    TimeB.time += (TimeB.millitm / 1000);

    /* Readjust milliseconds */
    TimeB.millitm %= 1000;

    UCTLocoTime (TimeB.time,
                 TimeB.dstflag,
                 &TimeSt);

    /* Move it into the message */
    Message[2] = HIBYTE (TimeSt.tm_yday + 1);
    Message[3] = LOBYTE (TimeSt.tm_yday + 1);
    Message[4] = TimeSt.tm_hour;
    Message[5] = TimeSt.tm_min;
    Message[6] = TimeSt.tm_sec;

    /* Load the milliseconds */
    Message[7] = HIBYTE (TimeB.millitm);
    Message[8] = LOBYTE (TimeB.millitm);

    return(NORMAL);
}

