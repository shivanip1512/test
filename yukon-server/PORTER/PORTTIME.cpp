/*-----------------------------------------------------------------------------*
*
* File:   PORTTIME
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PORTTIME.cpp-arc  $
* REVISION     :  $Revision: 1.23 $
* DATE         :  $Date: 2005/04/11 16:16:50 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#pragma title ( "Time Sync Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
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
using namespace std;

#include <rw\thr\mutex.h>

#include "os2_2w32.h"
#include "cticalls.h"


#include <stdlib.h>

// #include "btrieve.h"
#include <stdio.h>
#include <string.h>
#include <time.h>

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "portdecl.h"
#include "ilex.h"
#include "elogger.h"

#include "portglob.h"

#include "c_port_interface.h"
#include "port_base.h"
#include "mgr_port.h"
#include "mgr_device.h"
#include "dev_base.h"
#include "dev_ccu.h"
#include "dev_mct410.h"
#include "mgr_route.h"

#include "logger.h"
#include "guard.h"
#include "trx_info.h"
#include "trx_711.h"

#include "prot_welco.h"

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
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

        if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
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
    LONG PortID = (LONG)lprtid;

    if((RemoteRecord->getPortID() != PortID) || (RemoteRecord->getAddress() == 0xffff) || (RemoteRecord->isInhibited()) )
    {
        return;
    }

    OUTMESS         *OutMessage;
    CtiRouteSPtr    RouteRecord;
    DEVICE          DeviceRecord;
    USHORT          LeadAddress;
    PSZ             LeadTimeSyncs;


    switch(RemoteRecord->getType())
    {
    case TYPE_CCU700:
    case TYPE_CCU710:
        /* Walk down the routes for this ccu and pick out time routes */
        try
        {
            CtiRouteManager::LockGuard guard(RouteManager.getMux());
            CtiRouteManager::spiterator rte_itr;
            for(rte_itr = RouteManager.begin(); rte_itr != RouteManager.end(); CtiRouteManager::nextPos(rte_itr))
            {
                RouteRecord = rte_itr->second;

                if(RouteRecord->getTrxDeviceID() == RemoteRecord->getID() && RouteRecord->isDefaultRoute())
                {
                    /* Check if this is broadcast or lead */
                    if(CTIScanEnv ("DSM2_LEADTSYNCS", &LeadTimeSyncs))
                    {
                        /* Allocate some memory */
                        if((OutMessage = CTIDBG_new OUTMESS) == NULL)
                        {
                            return;
                        }

                        /* send a time sync to this guy */
                        OutMessage->DeviceID = RemoteRecord->getID();
                        OutMessage->Port     = RemoteRecord->getPortID();
                        OutMessage->Remote   = RemoteRecord->getAddress();
                        OutMessage->TimeOut = TIMEOUT;
                        OutMessage->Retry = 0;
                        OutMessage->Sequence = 0;
                        OutMessage->Priority = MAXPRIORITY;
                        OutMessage->EventCode = NOWAIT | NORESULT | DTRAN | BWORD | TSYNC;
                        OutMessage->Command = CMND_DTRAN;
                        OutMessage->InLength = 0;
                        OutMessage->ReturnNexus = NULL;
                        OutMessage->SaveNexus = NULL;

                        /* Load up the BST part of the message */

                        // FIX FIX FIX 090199 CGP
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        //OutMessage->Buffer.BSt.Port      = PortRecord->getPortID();
                        //OutMessage->Buffer.BSt.Remote    = RemoteRecord->getAddress();
                        //OutMessage->Buffer.BSt.Priority  = MAXPRIORITY - 2;
                        //OutMessage->Buffer.BSt.Retry     = 0;
                        //OutMessage->Buffer.BSt.Sequence  = 0;
                        //OutMessage->Buffer.BSt.Amp       = RouteRecord->getAmp();
                        //OutMessage->Buffer.BSt.Feeder    = RouteRecord->getBus();
                        //OutMessage->Buffer.BSt.Stages    = RouteRecord->getStages();
                        //OutMessage->Buffer.BSt.RepFixed  = RouteRecord->getFixBit();
                        //OutMessage->Buffer.BSt.RepVar    = RouteRecord->getVarBit();
                        //OutMessage->Buffer.BSt.Address   = UNIV_ADDRESS;

                        if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
                        {
                            printf ("Error Writing to Queue for Port %2hd\n", RemoteRecord->getPortID());
                            delete (OutMessage);
                        }
                    }
                    else
                    {
                        /* Walk through the leads */
                        while(LeadTimeSyncs != NULL)
                        {
                            if((LeadAddress = atoi (LeadTimeSyncs)) == 0)
                            {
                                break;
                            }

                            /* Move to the next one */
                            LeadTimeSyncs = strchr (LeadTimeSyncs, ',');
                            if(LeadTimeSyncs != NULL)
                                LeadTimeSyncs++;

                            /* Check the range */
                            if(LeadAddress > 4096)
                            {
                                break;
                            }

                            /* This one is ok so send it */
                            if((OutMessage = CTIDBG_new OUTMESS) == NULL)
                            {
                                return;
                            }

                            /* send a time sync to this guy */
                            OutMessage->DeviceID = RemoteRecord->getID();
                            OutMessage->Port = RemoteRecord->getPortID();
                            OutMessage->Remote = RemoteRecord->getAddress();
                            OutMessage->TimeOut = TIMEOUT;
                            OutMessage->Retry = 0;
                            OutMessage->Sequence = 0;
                            OutMessage->Priority = MAXPRIORITY;
                            OutMessage->EventCode = NOWAIT | NORESULT | DTRAN | BWORD | TSYNC;
                            OutMessage->Command = CMND_DTRAN;
                            OutMessage->InLength = 0;
                            OutMessage->ReturnNexus = NULL;
                            OutMessage->SaveNexus = NULL;


                            // FIX FIX FIX 090199 CGP
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

                            /* Load up the BST part of the message */
                            //OutMessage->Buffer.BSt.Port = PortRecord->getPortID();
                            //OutMessage->Buffer.BSt.Remote = RemoteRecord->getAddress();
                            //OutMessage->Buffer.BSt.Priority = MAXPRIORITY - 2;
                            //OutMessage->Buffer.BSt.Retry = 0;
                            //OutMessage->Buffer.BSt.Sequence = 0;
                            //OutMessage->Buffer.BSt.Amp = RouteRecord->getAmp();
                            //OutMessage->Buffer.BSt.Feeder = RouteRecord->getBus();
                            //OutMessage->Buffer.BSt.Stages = RouteRecord->getStages();
                            //OutMessage->Buffer.BSt.RepFixed = RouteRecord->getFixBit();
                            //OutMessage->Buffer.BSt.RepVar = RouteRecord->getVarBit();
                            //OutMessage->Buffer.BSt.Address = LEAD_DATA_BASE + (ULONG) LeadAddress - 1L;

                            if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
                            {
                                printf ("Error Writing to Queue for Port %2hd\n", RemoteRecord->getPortID());
                                delete (OutMessage);
                            }
                            else if(RemoteRecord->getType() == TYPE_CCU711)
                            {
                                CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)RemoteRecord->getTrxInfo();
                                /* Increment the number of entries for this guys queue */
                                p711Info->PortQueueEnts++;
                            }
                        }
                    }
                }
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        break;

    default:
        break;
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
#ifdef OLD_CRAP
                for(i = 0; i < STANDNAMLEN; i++)
                {
                    if(DeviceRecord.RouteName[2][i] == ' ')
                    {
                        break;
                    }
                    OutMessage->Buffer.OutMessage[Index++] = DeviceRecord.RouteName[2][i];
                }
#else

                cerr << __FILE__ << " (" << __LINE__ << ") This may break.. CGP 072599" << endl;

                for(i = 0; i < STANDNAMLEN; i++)
                {
                    if(RemoteRecord->getPassword()(i) == ' ')
                    {
                        break;
                    }
                    OutMessage->Buffer.OutMessage[Index++] = RemoteRecord->getPassword()(i);
                }
#endif

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
                OutMessage->Port = RemoteRecord->getPortID();
                OutMessage->Remote = RemoteRecord->getAddress();
                // memcpy (OutMessage->DeviceName, RemoteRecord->getDeviceName(), STANDNAMLEN);
                OutMessage->TimeOut = 2;
                OutMessage->OutLength = Index - PREIDLEN;
                OutMessage->InLength = -1;
                OutMessage->Sequence = 0;
                OutMessage->Priority = MAXPRIORITY - 2;
                OutMessage->EventCode = NOWAIT | NORESULT | ENCODED | TSYNC;
                OutMessage->ReturnNexus = NULL;
                OutMessage->SaveNexus = NULL;

                if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
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
            OutMessage->Port = RemoteRecord->getPortID();
            OutMessage->Remote = RemoteRecord->getAddress();
            OutMessage->TimeOut = 2;
            OutMessage->Retry = 0;
            OutMessage->OutLength = ILEXTIMELENGTH;
            OutMessage->InLength = 0;
            OutMessage->Source = 0;
            OutMessage->Destination = 0;
            OutMessage->Sequence = 0;
            OutMessage->Priority = MAXPRIORITY - 2;
            OutMessage->EventCode = NOWAIT | NORESULT | ENCODED | TSYNC;
            OutMessage->ReturnNexus = NULL;
            OutMessage->SaveNexus = NULL;


#if 0
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Sending ILEX time sync to remote:" << endl;
                dout << "device: " << OutMessage->DeviceID << endl;
                dout << "port:   " << OutMessage->Port << endl;
                dout << "remote: " << OutMessage->Remote << endl;
            }
#endif

            if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
            {
                printf ("Error Writing to Queue for Port %2hd\n", RemoteRecord->getPortID());
                delete (OutMessage);
            }

            break;

        case TYPE_WELCORTU:
        case TYPE_VTU:
            /* Allocate some memory */
            if((OutMessage = CTIDBG_new OUTMESS) == NULL)
            {
                return;
            }

            OutMessage->Buffer.OutMessage[5] = IDLC_TIMESYNC | 0x80;
            OutMessage->Buffer.OutMessage[6] = 7;

            /* send a time sync to this guy */
            OutMessage->DeviceID = RemoteRecord->getID();
            OutMessage->Port = RemoteRecord->getPortID();
            OutMessage->Remote = RemoteRecord->getAddress();
            OutMessage->TimeOut = 2;
            OutMessage->Retry = 0;
            OutMessage->OutLength = 7;
            OutMessage->InLength = 0;
            OutMessage->Source = 0;
            OutMessage->Destination = 0;
            OutMessage->Sequence = 0;
            OutMessage->Priority = MAXPRIORITY - 2;
            OutMessage->EventCode = NOWAIT | NORESULT | ENCODED | TSYNC;
            OutMessage->ReturnNexus = NULL;
            OutMessage->SaveNexus = NULL;

            if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
            {
                printf ("Error Writing to Queue for Port %2hd\n", RemoteRecord->getPortID());
                delete (OutMessage);
            }

            break;

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
        RemoteRecord = DeviceManager.getEqual(pRoute->getTrxDeviceID());

        //  make sure the route's transmitting device is on this port...  and make sure this is a default route
        if( RemoteRecord && (RemoteRecord->getPortID() == portid) && pRoute->isDefaultRoute() )
        {
            unsigned long time   = RWTime::now().seconds() - rwEpoch;
            bool          is_dst = RWTime::now().isDST();
            int amp      =  0,
                fixed    = 31,
                variable =  7;

            BSTRUCT message;
            OUTMESS *OutMessage = CTIDBG_new OUTMESS;

            if( OutMessage )
            {
                if( RemoteRecord->getType() == TYPE_CCU711 )
                {
                    amp = ((CtiDeviceCCU *)RemoteRecord.get())->getIDLC().getAmp();
                }

                //  load up all of the port/route specific items
                OutMessage->DeviceID  = pRoute->getTrxDeviceID();
                OutMessage->Port      = portid;
                OutMessage->Remote    = RemoteRecord->getAddress();
                OutMessage->TimeOut   = TIMEOUT;
                OutMessage->Retry     = 0;
                OutMessage->Sequence  = 0;
                OutMessage->Priority  = MAXPRIORITY;
                OutMessage->EventCode = NOWAIT | NORESULT | DTRAN | BWORD; // we don't want this to be hijacked | TSYNC;
                OutMessage->Command   = CMND_DTRAN;
                OutMessage->InLength  = 0;
                OutMessage->ReturnNexus = NULL;
                OutMessage->SaveNexus   = NULL;

                message.Port     = portid;
                message.Remote   = RemoteRecord->getAddress();

                message.DlcRoute.Amp      = amp;
                message.DlcRoute.RepFixed = pRoute->getCCUFixBits();
                message.DlcRoute.RepVar   = pRoute->getCCUVarBits();
                message.DlcRoute.Feeder   = pRoute->getBus();
                message.DlcRoute.Stages   = pRoute->getStages();

                //  VERY 400-series specific, watch this space carefully

                message.Address  = CtiDeviceMCT410::UniversalAddress;
                message.Function = CtiDeviceMCT410::FuncWrite_TSyncPos;
                message.Length   = CtiDeviceMCT410::FuncWrite_TSyncLen;
                message.IO       = Cti::Protocol::Emetcon::IO_Function_Write;

                message.Message[0] = 0xff;  //  global SPID
                message.Message[1] = (time >> 24) & 0x000000ff;
                message.Message[2] = (time >> 16) & 0x000000ff;
                message.Message[3] = (time >>  8) & 0x000000ff;
                message.Message[4] =  time        & 0x000000ff;
                message.Message[5] = is_dst;

                INT wordCount;
                //  Now build up the words in the calling structure
                C_Words (OutMessage->Buffer.OutMessage+PREIDLEN+PREAMLEN+BWORDLEN,
                         message.Message,
                         message.Length,
                         &wordCount);

                //  build the b word
                B_Word (OutMessage->Buffer.OutMessage + PREIDLEN + PREAMLEN, message, wordCount);

                //  calculate message lengths
                OutMessage->InLength = 2;
                // FIX FIX FIX 090199 CGP OutMessage->TimeOut = TIMEOUT + MyOutMessage.Buffer.BSt.Stages * (MyOutMessage.Buffer.BSt.NumW + 1);
                OutMessage->OutLength = PREAMLEN + BWORDLEN + wordCount * CWORDLEN + 3;

                BPreamble (OutMessage->Buffer.OutMessage + PREIDLEN, message, wordCount);

                if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
                {
                    printf ("Error Writing to Queue for Port %2hd\n", portid);
                    delete (OutMessage);
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - unable to allocate OUTMESS for time sync on portid " << portid << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION while sending MCT 400 series timesyncs **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        if(PortRecord->getName()(0) == '@') return;
        if(PortRecord->isInhibited()) return;

        if(PortRecord->getProtocolWrap() == ProtocolWrapIDLC)
        {

            /* make sure that the broadcast ccu is defined */
            RemoteRecord = DeviceManager.RemoteGetPortRemoteEqual (PortRecord->getPortID(), CCUGLOBAL);
            if(RemoteRecord) // It existed
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

                if(PortRecord->writeQueue(OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority, PortThread))
                {
                    printf ("Error Writing to Queue for Port %2hd\n", PortRecord->getPortID());
                    delete (OutMessage);
                }
            }
            else
            {
                /* Broadcast ccu does not exist on this port */
                DeviceManager.apply(apply711TimeSync, (void*)PortRecord->getPortID());
            }
        }
        else
        {
            /* we need to walk through and generate to 710's on this port */
            DeviceManager.apply(apply710TimeSync, (void*)PortRecord->getPortID());
        }

        /* Now check for Anything not covered by above */
        DeviceManager.apply(applyDeviceTimeSync, (void*)PortRecord->getPortID());

        RouteManager.apply(applyMCT400TimeSync, (void*)PortRecord->getPortID());
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
    RWTime nowTime;
    RWTime nextTime;

    /* See if we should even be running */
    if(TimeSyncRate <= 0)
    {
        /* We are history */
        _endthread();
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " TimeSyncThread started as TID:  " << CurrentTID() << " Sync every " << TimeSyncRate << " seconds" << endl;
    }


    /* Let the port routines get started */
    if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[ P_QUIT_EVENT ], 60000L) )
    {
        return;     // If we get this we never really got to run...
    }

#if 0
    /*
    ** Check for a WWV Receiver and if one is found then setup port
    ** and induce clock synchronization between Receiver and computer.
    */

    if(!(WWVReceiversetup (&WWVPortHandle)))
    {
        if(!(WWVClockSync (WWVPortHandle)))
        {
            SendTextToLogger ("Inf", "Synchronizing clock  with WWV Receiver.");
        }
    }

#endif

    /* send out time syncs for the first time... */
    PortManager.apply(applyPortSendTime, NULL);

    HANDLE hTimeSyncArray[] = {
        hPorterEvents[P_QUIT_EVENT],
        hPorterEvents[P_TIMESYNC_EVENT]
    };

    /* loop doing time sync at 150 seconds after the hour */
    for(; PorterQuit != TRUE ;)
    {
        /* Figure out how long to wait */
        nowTime = nowTime.now();

        // CTIResetEventSem (TimeSyncSem, &PostCount);
        ResetEvent(hPorterEvents[P_TIMESYNC_EVENT]);

        if(TimeSyncRate >= 300L)
        {
            nextTime = nextScheduledTimeAlignedOnRate( nowTime, TimeSyncRate );
            EventWait = 1000L * (nextTime.seconds() - nowTime.seconds() + 150L);
        }
        else
        {
            nextTime = nextScheduledTimeAlignedOnRate( nowTime, TimeSyncRate );
            EventWait = 1000L * (nextTime.seconds() - nowTime.seconds());
        }

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

#if 0
        /*
        **     If a WWV Receiver exits then induce clock
        **  synchronization between Receiver and computer.
        */

        if(WWVPortHandle != (HANDLE) NULL)
        {
            WWVClockSync (WWVPortHandle);
        }
        else
        {
            if(!(WWVReceiversetup (&WWVPortHandle)))
            {
                if(!(WWVClockSync (WWVPortHandle)))
                {
                    printf ("Synchronizing with WWV Receiver.\n");
                    SendTextToLogger ("Inf", "Synchronizing clock  with WWV Receiver.");
                }
            }
        }
#endif

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


LoadBTimeMessage (OUTMESS *OutMessage)
{
    OUTMESS MyOutMessage;
    struct timeb TimeB;
    struct tm TimeSt;
    USHORT EmetDay;
    USHORT Hour;
    USHORT EmetHTime;
    USHORT EmetFTime;

    /* Save the message information */
    MyOutMessage = *OutMessage;

    /* get the time from the system */
    UCTFTime (&TimeB);

    UCTLocoTime (TimeB.time, TimeB.dstflag, &TimeSt);

    /* Figure out how many 15 second intervals left in this 5 minutes */
    EmetFTime = 20 - ((TimeSt.tm_min % 5) * 60 + TimeSt.tm_sec) / 15;

    /* figure out how many AM/PM's left in the week */
    Hour = TimeSt.tm_hour;
    EmetDay = (7 - TimeSt.tm_wday) * 2;
    if(Hour > 11)
    {
        EmetDay--;
        Hour -= 12;
    }

    /* figure out how many 5 minute periods left in this 12 hours */
    EmetHTime = 144 - ((Hour * 12) + (TimeSt.tm_min / 5));

    /* Load the time sycn parts of the message into the local B word structure */
    MyOutMessage.Buffer.BSt.Message[0] = (UCHAR)EmetFTime;
    MyOutMessage.Buffer.BSt.Message[1] = (UCHAR)EmetHTime;
    MyOutMessage.Buffer.BSt.Message[2] = (UCHAR)EmetDay;
    MyOutMessage.Buffer.BSt.Message[3] = (UCHAR)DLCFreq1;
    MyOutMessage.Buffer.BSt.Message[4] = (UCHAR)DLCFreq2;
    MyOutMessage.Buffer.BSt.Function = 73;
    MyOutMessage.Buffer.BSt.Length = 5;
    MyOutMessage.Buffer.BSt.IO = 0;

    INT wordCount;
    /* Now build up the words in the calling structure */
    C_Words (OutMessage->Buffer.OutMessage+PREIDLEN+PREAMLEN+BWORDLEN,
             MyOutMessage.Buffer.BSt.Message,
             MyOutMessage.Buffer.BSt.Length,
             &wordCount);

    /* build the b word */
    B_Word (OutMessage->Buffer.OutMessage + PREIDLEN + PREAMLEN, MyOutMessage.Buffer.BSt, wordCount);

    /* calculate message lengths */
    OutMessage->InLength = 2;
    // FIX FIX FIX 090199 CGP OutMessage->TimeOut = TIMEOUT + MyOutMessage.Buffer.BSt.Stages * (MyOutMessage.Buffer.BSt.NumW + 1);
    OutMessage->OutLength = PREAMLEN + BWORDLEN + wordCount * CWORDLEN + 3;

    /* build preamble message */
    if(ZeroRemoteAddress)
    {
        // FIX FIX FIX 090199 CGP MyOutMessage.Buffer.BSt.Remote = 0;
    }

    BPreamble (OutMessage->Buffer.OutMessage + PREIDLEN, MyOutMessage.Buffer.BSt, wordCount);

    /* That all folks */
    return(NORMAL);
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


/* Routine to setup a port for a WWV Receivering device */
int WWVReceiversetup (HANDLE *WWVPortHandle)
{
#if 0
    PSZ Environment;
    extern USHORT WWVModel;
    extern USHORT WWVComMode;
    ULONG OpenAction;
    USHORT  Baud;
    CHAR WWVReceiverModel[20], *PortDescription;
    ULONG i;

    /* Make sure port name and receiver model is defined */
    if((i = CTIScanEnv ("WWVRECEIVER",
                        &Environment)) != NORMAL)
    {
        return(i);
    }

    /* get timezone */
    _tzset ();

    PortDescription = (strchr (Environment,',') + 1);
    i = strlen (Environment) - strlen (PortDescription - 1);
    strncpy (WWVReceiverModel, Environment, i);
    WWVReceiverModel[i] = '\0';

    /* get the correct baud rate and model number */
    if(!(stricmp (WWVReceiverModel, "UTS-10")))
    {
        Baud = 9600;
        WWVComMode = N81;
        WWVModel = UTSTEN;
    }
    /* get the correct baud rate and model number */
    else if(!(stricmp (WWVReceiverModel, "PSTI")))
    {
        Baud = 9600;
        WWVComMode = E71;
        WWVModel = UTSTEN;
    }
    else
    {
        printf ("%s...? Unknown WWV Receiver Model\n", WWVReceiverModel);
        SendTextToLogger ("Inf", "Unknown WWV Receiver Model.");
        return(!NORMAL);
    }

    {
        printf ("Opening port %s for \"%s\" WWV Receiver\n", PortDescription, WWVReceiverModel);
    }

    /* Go open the port */
    if((i = CTIOpen (PortDescription,
                     WWVPortHandle,
                     &OpenAction,
                     0L,
                     FILE_NORMAL,
                     FILE_OPEN,
                     OPEN_ACCESS_READWRITE | OPEN_SHARE_DENYREADWRITE
                     | OPEN_FLAGS_WRITE_THROUGH,
                     0L)) != NORMAL)
    {
        printf ("Error Opening WWV Receiver Port\n");
        SendTextToLogger ("Inf", "WWV Receiver Port   Error");
        if(WWVPortHandle != (HANDLE) NULL)
        {
            CTIClose (*WWVPortHandle);
            *WWVPortHandle = (HANDLE) NULL;
        }
        return(i);
    }

    /* set the port baud rate */
    if((i = SetBaudRate (*WWVPortHandle, Baud)) != NORMAL)
    {
        printf ("Error setting WWV Receiver Baud Rate.\n");
        if(WWVPortHandle != (HANDLE) NULL)
        {
            CTIClose (*WWVPortHandle);
            *WWVPortHandle = (HANDLE) NULL;
        }
        return(i);
    }

    if(WWVComMode == N81)
    {
        /* set the port line characteristics */
        if((i = SetLineMode (*WWVPortHandle)) != NORMAL)
        {
            printf ("Error setting WWV Receiver Line Characteristics.\n");
            if(WWVPortHandle != (HANDLE) NULL)
            {
                CTIClose (*WWVPortHandle);
                *WWVPortHandle = (HANDLE) NULL;
            }
            return(i);
        }
    }
    else
    {
        /* set the port line characteristics */
        if((i = SetLineModeE71 (*WWVPortHandle)) != NORMAL)
        {
            printf ("Error setting WWV Receiver Line Characteristics.\n");
            if(WWVPortHandle != (HANDLE) NULL)
            {
                CTIClose (*WWVPortHandle);
                *WWVPortHandle = (HANDLE) NULL;
            }
            return(i);
        }
    }


    /* set the default DCB Info for port */
    if((i = SetDefaultDCB (*WWVPortHandle)) != NORMAL)
    {
        printf ("Error setting WWV Receiver DCB Info.\n");
        if(WWVPortHandle != (HANDLE) NULL)
        {
            CTIClose (*WWVPortHandle);
            *WWVPortHandle = (HANDLE) NULL;
        }
        return(i);
    }

    /* set the read timeout for port */
    if((i = SetReadTimeOut (*WWVPortHandle, TIMEOUT)) != NORMAL)
    {
        printf ("Error setting WWV Receiver read timeout.\n");
        if(WWVPortHandle != (HANDLE) NULL)
        {
            CTIClose (*WWVPortHandle);
            *WWVPortHandle = (HANDLE) NULL;
        }
        return(i);
    }

    /* set the Write timeout for the port */
    if((i = SetWriteTimeOut (*WWVPortHandle, TIMEOUT)) != NORMAL)
    {
        printf ("Error setting WWV Receiver write timeout.\n");
        if(WWVPortHandle != (HANDLE) NULL)
        {
            CTIClose (*WWVPortHandle);
            *WWVPortHandle = (HANDLE) NULL;
        }
        return(i);
    }

#endif
    return(NORMAL);
}

/* Routine to synchronize the computer's clock with a WWV Receiver */
int WWVClockSync (HANDLE WWVPortHandle)
{
#if 0
    struct tm WWVTimeStruct;
    struct timeb  WWVTime;
    time_t WwvUtcTime;
    USHORT WWVReceiverTimeZone;
    CHAR *QueryCommand, WWVMode[23];
    CHAR WWVReceiverTime[23], WWVReceiverDate[23];
    ULONG MinutesElapsed;
    ULONG i;

    /*
    **       check to see if the WWV Receiver has a current updated time,
    **   if it doesn't then send message to logger but still use the time
    **   from the WWV Receiver to update the computer's clock.
    */

    /* Switch to the correct receiver model for retrieving and decoding */
    switch(WWVModel)
    {
    case UTSTEN:
        QueryCommand = "QM";

        /* send mode command */
        if((i = WWVBufferWrite (WWVPortHandle,
                                QueryCommand)) != NORMAL)
        {
            return(i);
        }

        /* retrieve input mode */
        if((i = WWVBufferRead (WWVPortHandle,
                               WWVMode)) != NORMAL)
        {
            return(i);
        }

        WWVReceiverTimeZone = WWVMode[3] - '0';

        /*
        **    See if time zones match and if they don't then print
        **  message and DON'T use WWV time to update computer's clock.
        */

        if(_timezone != (WWVReceiverTimeZone * 3600))
        {
            printf ("Time zones don't match!!!\n");
            SendTextToLogger ("Inf", "Time zones don't    match.");
            return(!NORMAL);
        }

        /*
        **    Check to see if WWV Receiver has locked onto a signal
        **  within the last 6 hours.  If it hasn't then print message
        **  and DO use the receiver to update the computer's clock.
        */

        if(sscanf (WWVMode + 13, "%4ld", &MinutesElapsed) != 1)
        {

            SendTextToLogger ("Inf", "WWV Receiver is not locking on");
            printf ("WWV Receiver is not locking on.\n");
            return(!NORMAL);
        }

        if(MinutesElapsed >= 360)
        {
            SendTextToLogger ("Inf", "WWV Receiver is not locking on");
            printf ("WWV Receiver is not locking on.\n");
        }

        /*
        **      Query the WWV receiver for the date and if we don't get
        **   a useable result then send back sync error
        */

        QueryCommand = "QD";

        /* send date command */
        if((i = WWVBufferWrite (WWVPortHandle,
                                QueryCommand)) != NORMAL)
        {
            return(i);
        }

        /* retrieve input date */
        if((i = WWVBufferRead (WWVPortHandle,
                               WWVReceiverDate)) != NORMAL)
        {
            return(i);
        }

        /*
        **    Check to see if WWV Receiver has ever locked onto a signal,
        **  If it hasn't then print message and DON'T use the receiver
        **  to update the computer's clock.
        */

        if(!(strncmp (WWVReceiverTime, "Not locked on", 13)))
        {
            SendTextToLogger ("Inf", "Not synchronizing   with WWV Receiver.");
            printf ("Not synchronizing with WWV Receiver.\n");
            return(!NORMAL);
        }

        /*
        **       Query the wwv receiver for the time and if we don't get
        **    a useable result then send back sync error
        */

        QueryCommand = "QT";

        /* send time command */
        if((i = WWVBufferWrite (WWVPortHandle,
                                QueryCommand)) != NORMAL)
        {
            return(i);
        }

        /* retrieve input time */
        if((i = WWVBufferRead (WWVPortHandle,
                               WWVReceiverTime)) != NORMAL)
        {
            return(i);
        }

        /*
        **    Check to see if WWV Receiver has ever locked onto a signal,
        **  If it hasn't then print message and DON'T use the receiver
        **  to update the computer's clock.
        */

        if(!(strncmp (WWVReceiverTime, "Not locked on", 13)))
        {
            SendTextToLogger ("Inf", "Not synchronizing   with WWV Receiver.");
            printf ("Not synchronizing with WWV Receiver.\n");
            return(!NORMAL);
        }

        /* check for DST */
        if(strchr (WWVReceiverTime, 'D') == NULL)
        {
            WWVTimeStruct.tm_isdst = 0;
        }
        else
        {
            WWVTimeStruct.tm_isdst = 1;
        }

        /* Decoding the date-time info  and */
        /* Compute the UTC time using the queried date-time */

        WWVTimeStruct.tm_year = atoi (strtok (WWVReceiverDate, "/"));
        WWVTimeStruct.tm_mon  = atoi (strtok (NULL, "/")) - 1;
        WWVTimeStruct.tm_mday = atoi (strtok (NULL, "/"));
        WWVTimeStruct.tm_yday = atoi (strtok (NULL, "\0")) - 1;
        WWVTimeStruct.tm_hour = atoi (strtok (WWVReceiverTime, ":") + 1);
        WWVTimeStruct.tm_min  = atoi (strtok (NULL, ":"));
        WWVTimeStruct.tm_sec  = atoi (strtok (NULL, "."));

        if(strchr (WWVReceiverTime, 'P') != NULL)
        {
            if(WWVTimeStruct.tm_hour != 12)
            {
                WWVTimeStruct.tm_hour += 12;
            }
        }

        if(strchr (WWVReceiverTime, 'A') != NULL)
        {
            if(WWVTimeStruct.tm_hour == 12)
            {
                WWVTimeStruct.tm_hour -= 12;
            }
        }
    }

    /* send UTC time to UTCsetFTime in order to set the computer's clock */
    WwvUtcTime = UCTMakeTime (&WWVTimeStruct);

    WWVTime.time = WwvUtcTime;
    WWVTime.dstflag = WWVTimeStruct.tm_isdst;

    UCTSetFTime (&WWVTime);
#endif
    return(NORMAL);
}

/* This function reads input from the WWV Receiver device */
int WWVBufferRead (HANDLE WWVPortHandle, CHAR *InputBuffer)
{
    ULONG BytesRead;
    USHORT ReadBufferLength;
    CHAR *ReadPointer;
    ULONG i;

    ReadPointer = InputBuffer;
    ReadBufferLength = 23;

    do
    {
        if(((i = CTIRead (WWVPortHandle,
                          ReadPointer++,
                          1,
                          &BytesRead))  != NORMAL) || BytesRead == 0)
        {
            break;
        }

        if(*(ReadPointer - 1) == '\r')
        {
            break;
        }

    } while((ReadPointer - InputBuffer) < ReadBufferLength);

    if(BytesRead == 0 || *(ReadPointer - 1) != '\r')
    {
        printf ("Error Reading WWV Receiver Port.\n");
        SendTextToLogger ("Inf", "Temporarily lost    communication with WWV Receiver.");
        return(!NORMAL);
    }
    else if(i)
    {
        printf ("Error Reading WWV Receiver Port.\n");
        SendTextToLogger ("Inf", "Not synchronizing   with WWV Receiver.");
        if(WWVPortHandle != (HANDLE) NULL)
        {
            CTIClose (WWVPortHandle);
            WWVPortHandle = (HANDLE) NULL;
        }
        return(!NORMAL);
    }

    *(ReadPointer - 1) = '\0';

    return(NORMAL);
}

/* This function writes WWV Receiver commands to the WWV Receiver port */
int WWVBufferWrite (HANDLE WWVPortHandle, CHAR *QueryCommand)
{
    ULONG i, BytesWritten;

#if 0

    /* cleanup the port before we starte talking to it. */
    if((i = PortInputFlush (WWVPortHandle)) != NORMAL)
    {
        printf ("Error Flushing WWV Receiver Port.\n");
        SendTextToLogger ("Inf", "Not synchronizing   with WWV Receiver.");
        if(WWVPortHandle != (HANDLE) NULL)
        {
            CTIClose (WWVPortHandle);
            WWVPortHandle = (HANDLE) NULL;
        }
        return(i);
    }
    if((i = PortOutputFlush (WWVPortHandle)) != NORMAL)
    {
        printf ("Error Flushing WWV Receiver Port.\n");
        SendTextToLogger ("Inf", "Not synchronizing   with WWV Receiver.");
        if(WWVPortHandle != (HANDLE) NULL)
        {
            CTIClose (WWVPortHandle);
            WWVPortHandle = (HANDLE) NULL;
        }
        return(i);
    }

    if(CTIWrite (WWVPortHandle,
                 QueryCommand,
                 sizeof (strlen (QueryCommand)),
                 &BytesWritten) ||
       BytesWritten != sizeof (strlen (QueryCommand)))
    {
        printf ("Error writing to WWV Receiver Port!\n\n");
        SendTextToLogger ("Inf", "Not synchronizing   with WWV Receiver.");
        if(WWVPortHandle != (HANDLE) NULL)
        {
            CTIClose (WWVPortHandle);
            WWVPortHandle = (HANDLE) NULL;
        }

        return(!NORMAL);
    }
#endif
    return(NORMAL);
}
