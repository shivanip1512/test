/*-----------------------------------------------------------------------------*
*
* File:   portentry
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.28 $
* DATE         :  $Date: 2005/02/17 23:25:29 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#pragma title ( "Process Request Server Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1999" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert, Corey G. Plender

    FileName:
        PORTPIPE.C

    Purpose:
        To Accept request from other process's to Remotes and Devices

    The following procedures are contained in this module:
        PorterConnectionThread                  RouterThread

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        8-20-93      Added code to handle ARM's directly          WRO
        8-25-93      Update remote port routine                   WRO
        9-7-93       Converted to 32 bit                          WRO
        11-1-93      Modified to keep stats temporarily in memory TRH

        12-20-99     Rewrite for Yukon                            CGP

   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include <iostream>
#include <list>
using namespace std;
#include "os2_2w32.h"
#include "cticalls.h"

#include <rw\rwtime.h>
#include <rw\thr\mutex.h>

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "cparms.h"
#include "connection.h"
#include "queues.h"
#include "queue.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "portdecl.h"
#include "master.h"
#include "ilex.h"
#include "perform.h"

#include "portglob.h"
#include "color.h"

#include "c_port_interface.h"
#include "mgr_port.h"
#include "mgr_device.h"
#include "dev_base.h"
#include "dev_lcu.h"
#include "dllbase.h"

#include "logger.h"
#include "guard.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "prot_emetcon.h"
#include "trx_711.h"
#include "utility.h"

extern HCTIQUEUE*   QueueHandle(LONG pid);
extern CtiQueue< CtiOutMessage, less<CtiOutMessage> > GatewayOutMessageQueue;

INT PorterEntryPoint(OUTMESS *&OutMessage);
INT RemoteComm(OUTMESS *&OutMessage);
INT RemotePort(OUTMESS *&OutMessage);
INT PorterControlCode(OUTMESS *&OutMessage);
INT ValidateRemote(OUTMESS *&OutMessage);
INT ValidatePort(OUTMESS *&OutMessage);
INT CommandCode(OUTMESS *&OutMessage, CtiDeviceSPtr Dev);
INT ValidateEmetconMessage(OUTMESS *&OutMessage);
INT CCU711Message(OUTMESS *&OutMessage, CtiDeviceSPtr Dev);
INT VersacomMessage(OUTMESS *&OutMessage);
INT ValidateEncodedFlags(OUTMESS *&OutMessage, INT devicetype);
INT BuildMessage( OUTMESS *&OutMessage, OUTMESS *&SendOutMessage );
INT QueueBookkeeping(OUTMESS *&SendOutMessage);
INT ExecuteGoodRemote(OUTMESS *&OutMessage);
INT GenerateCompleteRequest(RWTPtrSlist< OUTMESS > &outList, OUTMESS *&OutTemplate);

INT ValidateOutMessage(OUTMESS *&OutMessage);
VOID ConnectionThread (VOID *Arg);
INT realignNexus(OUTMESS *&OutMessage);

/* Threads to field incoming messages from the pipes */
VOID PorterConnectionThread (VOID *Arg)
{
    INT   iNexus   = 0;
    INT   nRet     = 0;

    CTINEXUS  *NewNexus = NULL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " PorterConnectionThread started as TID:  " << CurrentTID() << endl;
    }

    strcpy(PorterListenNexus.Name, "PorterConnectionThread: Listener");

    /*
     *  4/7/99 This is the server side of a CTIDBG_new Port Control Nexus
     *  This thread rolls off CTIDBG_new instances of this connection on an as needed basis.
     *
     *  1. Create a listener on PORTCONTROLNEXUS for incoming connections
     *  2. Pop off a CTIDBG_new thread to manage the returned connection.
     *       NOTE: This deviates in implementation from DSM/2 which spwned a CTIDBG_new listener thread
     *             sockets don't care for CTIDBG_new listener sockets.
     */

    while(!PorterQuit && PorterListenNexus.CTINexusCreate(PORTCONTROLNEXUS))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** INFO **** PorterConnectionThread unable to create listener. Will attempt again." << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        Sleep(2500);
    }

    for(; !PorterQuit ;)
    {
        NewNexus = (CTINEXUS*) CTIDBG_new CTINEXUS;

        if(NewNexus == NULL)
        {
            fprintf(stderr,"Unable to acquire memory for a CTIDBG_new connection to port control\n");

            Sleep(1000);
            continue;
        }
        sprintf(NewNexus->Name, "PortControl Nexus %d", iNexus++);

        /*
         *  Blocking wait on the listening nexus.
         */

        nRet = PorterListenNexus.CTINexusConnect(NewNexus, &hPorterEvents[P_QUIT_EVENT]);


        if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0L) )
        {
            delete NewNexus;
            NewNexus = 0;

            break;         // FIX FIX FIX...??? Should this stop porter dead?? CGP
        }
        else if(!nRet)
        {
            /* Someone has connected to us.. */
            // fprintf(stderr,"PortControl Connection Server: Nexus Connected\n");
            _beginthread(ConnectionThread, 0, (VOID*)NewNexus);

            NewNexus = 0;
        }
        else
        {
            fprintf(stderr,"Error creating listener nexus\n");
            delete NewNexus;
            NewNexus = 0;

            break;         // FIX FIX FIX...??? Should this stop porter dead?? CGP
        }
    }

    if(NewNexus) delete NewNexus;

    return;
}


/*
 *  This is the guy who deals with incoming data from any one else in the system.
 */
VOID ConnectionThread (VOID *Arg)
{
    INT            i;
    CTINEXUS       *MyNexus = (CTINEXUS*)Arg;     // This is an established connection with a client!
    OUTMESS        *OutMessage = NULL;

    ULONG                   BytesRead;
    RWTPtrSlist< OUTMESS >  outList;

    /* make it clear who is the boss */
    CTISetPriority (PRTYS_THREAD, PRTYC_TIMECRITICAL, 30, 0);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " ConnectionThread started as TID:  " << CurrentTID() << endl;
    }

    /* Now sit and wait for something to come in on this instance */
    for(; !PorterQuit ;)
    {

        /*
         * Allocate memory for block coming in
         * This memory is typically not to be free'd by this code... It is released from our worries
         * by either a SendError call, or a WriteQueue to the internals of the beast.  Both of
         * whom assume memory responsibilities at that point.
         */


        OutMessage = NULL;
        BytesRead = 0;

        if( outList.entries() )
        {
            /*
             *  A previous message has caused a list of OUTMESS objects to require sequential processing!
             */
            OutMessage = outList.get();
            OutMessage->Request.BuildIt = FALSE;         // Make this FALSE, so the OutMessage passes into the PorterEntryPoint function.

            if(PorterDebugLevel & PORTER_DEBUG_NEXUSREAD)
            {
                CtiDeviceSPtr tempDev = DeviceManager.getEqual(OutMessage->TargetID ? OutMessage->TargetID : OutMessage->DeviceID);

                if(tempDev)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Portentry built an outmessage for " << tempDev->getName();
                    dout << " at priority " << OutMessage->Priority << " retries = " << OutMessage->Retry << endl;
                    if(strlen(OutMessage->Request.CommandStr) > 0) dout << "  Command: " << OutMessage->Request.CommandStr << endl;
                }
            }

            if(outList.entries() > 2)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
        else if((OutMessage = CTIDBG_new OUTMESS) == NULL)     // Get a bit of memory for the next else if...
        {
            printf ("Error Allocating Memory for Incoming Block\n");
            CTISleep(5000L);
            continue;
        }
        else if((i = MyNexus->CTINexusRead (OutMessage, sizeof(OUTMESS), &BytesRead, CTINEXUS_INFINITE_TIMEOUT))  || BytesRead != sizeof(OUTMESS))  /* read whatever comes in */
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** WARNING: Error on Nexus Read **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " Failed to receive a full OUTMESS, will try to correct!" << endl;
                dout << " OUTMESS lost, client and porter may be unstable!" << endl;
            }

            // Clean out any residue
            if(BytesRead != sizeof(OUTMESS))
            {
                if( BytesRead > 0 )
                {
                    MyNexus->CTINexusRead (&(((BYTE*)OutMessage)[BytesRead]), sizeof(*OutMessage) - BytesRead, &BytesRead, CTINEXUS_INFINITE_TIMEOUT);
                }
                else if(BytesRead < 0)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                else
                {
                    MyNexus->CTINexusClose();
                    break;   // The for and exit this thread
                }
            }
        }

        if(PorterDebugLevel & PORTER_DEBUG_NEXUSREAD && !(OutMessage->MessageFlags & MSGFLG_ROUTE_TO_PORTER_GATEWAY_THREAD))
        {
            CtiDeviceSPtr tempDev = DeviceManager.getEqual(OutMessage->TargetID ? OutMessage->TargetID : OutMessage->DeviceID);

            if(tempDev)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Portentry connection received an outmessage for " << tempDev->getName();
                dout << " at priority " << OutMessage->Priority << " retries = " << OutMessage->Retry << endl;
                if(strlen(OutMessage->Request.CommandStr) > 0) dout << "  Command: " << OutMessage->Request.CommandStr << endl;
            }
        }

        /*
         * Set the handle for the return message.
         */
        OutMessage->ReturnNexus = MyNexus;             /* This had better copy the entire structure CP */

        /*
         *  A bit of a wiggle here. 061903 CGP.  Capture and re-route this OM type to PorterGWThread.
         */
        if( OutMessage->MessageFlags & MSGFLG_ROUTE_TO_PORTER_GATEWAY_THREAD )
        {
            if(!gConfigParms.getValueAsString("PORTER_GATEWAY_SUPPORT").compareTo("true", RWCString::ignoreCase))
            {
                GatewayOutMessageQueue.putQueue( OutMessage );
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " ***** Gateway message request, but the gateway code was not configured to operate." << endl;
                }

                SendError(OutMessage, BADPARAM);
            }

            continue;
        }

        if(OutMessage->Request.BuildIt == TRUE)
        {
            GenerateCompleteRequest( outList, OutMessage );

            if(OutMessage != NULL)
            {
                delete OutMessage;
                OutMessage = NULL;
            }
        }
        else if( OutMessage->DeviceID == 0 || OutMessage->Port == 0 )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** INVALID OUTMESS **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " OUTMESS destroyed, client may become unstable!" << endl;
            }

            SendError (OutMessage, MISPARAM);      // Message has been consumed!
            continue;            // The for loop
        }
        else
        {
            /*
             *  This function (PorterEntryPoint) was implemented to more easily accomodate an alternate
             *  communication path into porter.  The first alternate will be the PIL
             *  connection channel.  Currently (122899) the only user of this interface is Scanner.
             */

            PorterEntryPoint(OutMessage);
        }
    } /* and do it all again */

    if(MyNexus) delete MyNexus;

    if(OutMessage != NULL)
    {
        delete OutMessage;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " ConnectionThread  TID: " << CurrentTID() << " terminating" << endl;
    }

}

/*-----------------------------------------------------------------------------*
 * After the connection has labeled and marked the OUTMESS to allow it's proper
 * return path, this common function handles the locating of the request into
 * the porter port queues
 *
 *-----------------------------------------------------------------------------*/
INT PorterEntryPoint(OUTMESS *&OutMessage)
{
    INT status = NORMAL;

    if((status = ValidateOutMessage(OutMessage)) != NORMAL)
    {
        return status;
    }

    /* check if this is a global control message */
    if((status = PorterControlCode(OutMessage)) != NORMAL)
    {
        return status;
    }

    if((status = ValidatePort(OutMessage)) != NORMAL)
    {
        return status;
    }

#if 0           // 20020611 CGP.. Thin kagain small man.

    /* Find out if this guy gets routed to another system */
    if(PortFlags[OutMessage->Port] & REMOTEPORT)
    {
        return RemotePort(OutMessage);
    }

#endif

    /*
     * Ok, all checks passed so far, so lets operate on this remote
     */
    return RemoteComm(OutMessage);
}


/* Thread (s) to handle communications with remote porters */
VOID RouterThread (VOID *TPNumber)

{
    INT      nRet;
#if 0

    VOID RouterReturnThread (VOID *Arg);
    ULONG i, ReadLength, Type;
    ULONG  BytesWritten;
    OUTMESS *OutMessage;
    INMESS InMessage;
    BYTE ReadPriority;
    CTINEXUS MyNexus;
    REQUESTDATA ReadResult;
    USHORT ThreadPortNumber;
    CHAR Name[100];

    /* Definitions to access Btrieve files */
    CtiPortSPtr PortRecord;
    CtiDeviceSPtr RemoteRecord;
    REMOTEPERF  RemotePerf;
    ERRSTRUCT   ErrStruct;

    ULONG LastSent = 0L;

    InMessage.DeviceID = PORTPIPE_DEVID;

    MyNexus.NexusState = CTINEXUS_STATE_NULL;

    /* Retrieve the port number for this thread */
    ThreadPortNumber = OFFSETOF (TPNumber);

    printf ("Router Thread for %hd Starting as TID:  %ld\n", ThreadPortNumber, CurrentTID ());

    /* set the type for this port */
    PortFlags[ThreadPortNumber] |= REMOTEPORT;

    /* First get the phone record from the Btrieve file */
    if((PortRecord = PortManager.PortGetEqual (ThreadPortNumber)) != NULL)
    {
        printf ("Error getting Port Record:  %ld\n");
        _endthread ();
    }

    /* setup memory for this port's stats */
    if((PortStats[ThreadPortNumber] = (PORTSTATS*)malloc (sizeof(PORTSTATS))) == NULL)
    {
        printf("1;31m Error Allocating Memory\n0m");
        _endthread ();
    }

    // PortStats[ThreadPortNumber]->Stats = PortRecord.Stats;      // FIX FIX FIX 071999 Add later

    /* create the queue for this port */
    if(CreateQueue (&PortRecord->getPortQueueHandle(), QUE_PRIORITY))
    {
        printf ("Error Creating Queue\n");
        CloseQueue (PortRecord->getPortQueueHandle());
        PortRecord->getPortQueueHandle() = (HCTIQUEUE) NULL;
        _endthread();
    }

    /*
     *  I am a client trying to connect to a remote nexus!
     */

    /* Now try to open up the pipe to the remote system involved */
    strcat (Name, PortRecord->getName() + 1);


    nRet = MyNexus.CTINexusOpen(Name, PORTCONTROLNEXUS, CTINEXUS_FLAG_READEXACTLY);

    if(nRet)
    {
        fprintf(stderr,"Error creating connection %d\n",nRet);
        MyNexus.NexusState = CTINEXUS_STATE_NULL;
        // FIX FIX FIX ???? CGP Should I quit???
    }
    else
    {
        fprintf(stdout,"RouterThread Connected to remote Port Control!\n");
    }


    /* make it clear who is the boss */
    CTISetPriority (PRTYS_THREAD, PRTYC_TIMECRITICAL, 30, 0);

    /* Initialize the local memory for those remotes we know about */
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  dev_guard(DeviceManager.getMux());
        CtiRTDB<CtiDeviceBase>::CtiRTDBIterator   itr_dev(DeviceManager.getMap());

        for(; ++itr_dev ;)
        {
            CtiDeviceSPtr RemoteDevice = itr_dev.value();

            if( PortRecord->getPortID() == RemoteDevice->getID() && RemoteDevice->getType() == TYPE_CCU711)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "This might needs fixin' " << __FILE__ << " " << __LINE__ << endl;
                Type = RemoteDevice->getType();
                RemoteDevice->setType(TYPE_REMOTE);
                // ((CtiTransmitter711Info*)(RemoteDevice->getTrxInfo()))->Type = Type;
                RemoteDevice->setType(Type);
            }
        }
    }

    /* Now sit and wait for something to come in */
    for(;;)
    {
        if(ReadQueue (PortRecord->getPortQueueHandle(), &ReadResult,  &ReadLength, (PVOID FAR *) &OutMessage, 0, DCWW_WAIT, &ReadPriority))
        {
            printf ("Error Reading Routed Port Queue\n");
            continue;
        }

        RemoteRecord = DeviceManager.getEqual(OutMessage->DeviceID);

        if(NULL == RemoteRecord)
        {
            /* Non-existant device so do as needed with message */
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << __FILE__ << " (" << __LINE__ << ")" << endl;
            SendError (OutMessage, BADCCU);
            continue;
        }
        else if(RemoteRecord->isInhibited())
        {
            if((OutMessage->Remote != CCUGLOBAL) && (OutMessage->Remote != RTUGLOBAL))
            {
                /* Update the statistics for this ccu */
                RemotePerf.Port   = RemoteRecord->getPortID();
                RemotePerf.Remote = RemoteRecord->getAddress();
                RemotePerf.Error  = REMOTEINHIBITED;
                RemotePerfUpdate (&RemotePerf, &ErrStruct);
                /* Log the error */
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << __FILE__ << " " << __LINE__ << endl;
                ReportRemoteError (RemoteRecord, &ErrStruct);
            }

            SendError (OutMessage, REMOTEINHIBITED);
            continue;
        }

        CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)RemoteRecord->getTrxInfo();

        if(pInfo == NULL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "This might needs fixin' " << __FILE__ << " " << __LINE__ << endl;
            }

            Type = RemoteRecord->getType();
            RemoteRecord->setType(TYPE_REMOTE);
            RemoteRecord->setType(Type);
        }


        CCUInfo.findValue(&OutMessage->DeviceID)->FiveMinuteCount++;

        /* Good one so take a crack at sending it */
        if(OutMessage->EventCode & RESULT)
        {
            /*  set up info so that we can get it back */
            OutMessage->SaveNexus = OutMessage->ReturnNexus;

            /* Force DTRAN */
            if(!(OutMessage->EventCode & COMMANDCODE))
            {
                /* DO NOT allow queued messages at this time */
                if(OutMessage->EventCode & BWORD)
                    OutMessage->EventCode |= DTRAN;
            }
        }

        /* Now try to write it to the remote porter */
        if(MyNexus.NexusState != CTINEXUS_STATE_NULL)
        {
            if(MyNexus.CTINexusWrite (OutMessage, sizeof (*OutMessage), &BytesWritten, 30L) || BytesWritten != sizeof (*OutMessage))
            {
                if(MyNexus.NexusState != CTINEXUS_STATE_NULL)
                {
                    MyNexus.CTINexusClose();
                    MyNexus.NexusState = CTINEXUS_STATE_NULL;
                }

                LastSent = LongTime ();
            }

            LastSent = 0L;
        }

        if(MyNexus.NexusState == CTINEXUS_STATE_NULL)
        {
            /* attempt to open this guy  (again?) */
            if(LastSent > (LongTime () - 10L))
            {
                SendError (OutMessage, PIPEWRITE);
                continue;
            }
            nRet = MyNexus.CTINexusOpen(Name, PORTCONTROLNEXUS, CTINEXUS_FLAG_READEXACTLY);

            if(nRet)
            {
                fprintf(stderr,"Error creating connection %d\n",nRet);
                MyNexus.NexusState = CTINEXUS_STATE_NULL;

                if((OutMessage->Remote != CCUGLOBAL)
                   && (OutMessage->Remote != RTUGLOBAL))
                {
                    /* Update the statistics for this ccu */
                    RemotePerf.Port   = RemoteRecord->getPortID();
                    RemotePerf.Remote = RemoteRecord->getAddress();
                    RemotePerf.Error  = NOREMOTEPORTER;
                    RemotePerfUpdate (&RemotePerf, &ErrStruct);
                    /* Log the error */
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << __FILE__ << " " << __LINE__ << endl;
                    ReportRemoteError (RemoteRecord, &ErrStruct);
                }

                SendError (OutMessage, NOREMOTEPORTER);

                LastSent = LongTime ();

                continue;
            }

            if(MyNexus.CTINexusWrite (OutMessage, sizeof (*OutMessage), &BytesWritten, 30L) || BytesWritten != sizeof (*OutMessage))
            {
                if(MyNexus.NexusState != CTINEXUS_STATE_NULL)
                {
                    MyNexus.CTINexusClose();
                }

                if((OutMessage->Remote != CCUGLOBAL) && (OutMessage->Remote != RTUGLOBAL))
                {
                    /* Update the statistics for this ccu */
                    RemotePerf.Port   = RemoteRecord->getPortID();
                    RemotePerf.Remote = RemoteRecord->getAddress();
                    RemotePerf.Error = PIPEWRITE;
                    RemotePerfUpdate (&RemotePerf, &ErrStruct);
                    /* Log the error */
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << __FILE__ << " " << __LINE__ << endl;
                    ReportRemoteError (RemoteRecord, &ErrStruct);
                }

                SendError (OutMessage, PIPEWRITE);

                LastSent = LongTime ();

                continue;
            }

            LastSent = 0L;
        }

        /* if this is a two way... wait */
        if(OutMessage->EventCode & RESULT)
        {
            if(MyNexus.CTINexusRead (&InMessage,
                                     sizeof (InMessage),
                                     &ReadLength,
                                     CTINEXUS_INFINITE_TIMEOUT) || ReadLength != sizeof (InMessage))
            {
                if(MyNexus.NexusState != CTINEXUS_STATE_NULL)
                {
                    MyNexus.CTINexusClose ();
                }

                if((OutMessage->Remote != CCUGLOBAL)
                   && (OutMessage->Remote != RTUGLOBAL))
                {
                    /* Update the statistics for this ccu */
                    RemotePerf.Port   = RemoteRecord->getPortID();
                    RemotePerf.Remote = RemoteRecord->getAddress();
                    RemotePerf.Error = PIPEREAD;
                    RemotePerfUpdate (&RemotePerf, &ErrStruct);
                    /* Log the error */
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << __FILE__ << " " << __LINE__ << endl;
                    ReportRemoteError (RemoteRecord, &ErrStruct);
                }

                SendError (OutMessage, PIPEREAD);

                LastSent = LongTime ();

                continue;
            }

            LastSent = 0L;

            if((OutMessage->Remote != CCUGLOBAL)
               && (OutMessage->Remote != RTUGLOBAL))
            {
                /* Update the statistics for this ccu */
                RemotePerf.Port   = RemoteRecord->getPortID();
                RemotePerf.Remote = RemoteRecord->getAddress();
                RemotePerf.Error = InMessage.EventCode & ~ENCODED;
                RemotePerfUpdate (&RemotePerf, &ErrStruct);
                /* Log the error */
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << __FILE__ << " " << __LINE__ << endl;
                ReportRemoteError (RemoteRecord, &ErrStruct);
            }

            delete (OutMessage);

            /* Send this one on to its source */
            if(InMessage.SaveNexus->NexusState != CTINEXUS_STATE_NULL)
            {
                if(InMessage.SaveNexus->CTINexusWrite (&InMessage, sizeof (InMessage), &BytesWritten, 30L) || BytesWritten != sizeof (InMessage))
                {
                    continue;   /* Not really much we can do... calling process disappeared */
                }
            }
        }
        else
        {
            /* This is one way so continue on */
            delete (OutMessage);
        }
    }
#else

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** ACH Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

#endif
}

/*----------------------------------------------------------------------------*
 * This function is responsible for verifying that the message is good and will
 * not cause any immediate problems in the Porter internals.
 *----------------------------------------------------------------------------*/
INT ValidateOutMessage(OUTMESS *&OutMessage)
{
    INT     nRet = NoError;

    if(OutMessage != NULL)
    {
        if(OutMessage->HeadFrame[0] != 0x02 ||
           OutMessage->HeadFrame[1] != 0xe0 ||
           OutMessage->TailFrame[0] != 0xea ||
           OutMessage->TailFrame[1] != 0x03)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " !!!! OutMessage Misalignment !!!! " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " Head bytes " << hex << (int)OutMessage->HeadFrame[0] << " " << hex << (int)OutMessage->HeadFrame[1] << dec << endl;
                dout << " Tail bytes " << hex << (int)OutMessage->TailFrame[0] << " " << hex << (int)OutMessage->TailFrame[1] << dec << endl;
            }

            realignNexus(OutMessage);
        }


        if((OutMessage->DeviceID <= 0 && OutMessage->TargetID <= 0) || OutMessage->Remote > MAXIDLC)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);

            dout << RWTime() << " Bad OUTMESS received TrxID = " << OutMessage->Request.TrxID << endl;
            dout << RWTime() << "   Command " << OutMessage->Request.CommandStr << endl;

            delete(OutMessage);
            OutMessage = NULL;

            nRet = CtiInvalidRequest;
        }
        else
        {
            nRet = NoError;      // This is A-OK.
        }
    }
    else
    {
        nRet = MemoryError;
    }

    return nRet;
}

INT RemotePort(OUTMESS *&OutMessage)
{
    /* No Local processing */
    if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
    {
        printf("Error Writing to Queue for Port %2hd\n", OutMessage->Port);
        SendError (OutMessage, QUEUE_WRITE);
        return QUEUE_WRITE;
    }
    /* Leave the memory intact */
    return NORMAL;
}

INT PorterControlCode(OUTMESS *&OutMessage)
{
    INT i = 0;
    INT status = NORMAL;

    if((OutMessage->EventCode & COMMANDCODE))
    {
        switch(OutMessage->EventCode & COMMANDMASK)
        {
        case STOPALL:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << "Shutdown message received... Porter shutting down" << endl;
                }
                /* put the mother of all queue flushes here */
                CTISleep (2000L);
                CTIExit (EXIT_PROCESS, 0);
            }
        case REMOTECONTROL:
            {
                if((i = RemoteControl (OutMessage)) != NORMAL)
                {
                    SendError (OutMessage, (USHORT)i);
                    return i;
                }

                delete (OutMessage);
                OutMessage = 0;

                // I must cause my caller (PorterEntryPoint) to return in this case,
                // even though there is no real error.
                status = !NORMAL;
            }
        }
    }

    return status;
}

/*----------------------------------------------------------------------------*
 * This function verifies that we know about and have started up the indicated
 * remote.
 *----------------------------------------------------------------------------*/

INT ValidateRemote(OUTMESS *&OutMessage)
{
    INT                  status            = NORMAL;
    CtiDeviceSPtr TransmitterDev   = DeviceManager.getEqual(OutMessage->DeviceID);

    if(!TransmitterDev)
    {
        /* This is a unknown remote so shun it */
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  Device ID " << OutMessage->DeviceID << " not found " << endl;
        }
        SendError (OutMessage, IDNF);
        status = IDNF;
    }

    return status;
}

/*----------------------------------------------------------------------------*
 * This function verifies that we know about and have started up the indicated
 * port.  On the first comm to the port, it also initializes it.
 * If the port is non functional, we will make one stab at getting it going
 * if this fails, we fail!
 *----------------------------------------------------------------------------*/

INT ValidatePort(OUTMESS *&OutMessage)
{
    INT j;
    INT status = NORMAL;
    CtiPortSPtr Port;

    /* Check the memory database to see if a port like this exists */
    if((Port = PortManager.PortGetEqual((LONG)OutMessage->Port)))
    {
        Port->verifyPortIsRunnable( hPorterEvents[P_QUIT_EVENT] );

        if(Port->isInhibited())
        {
            SendError (OutMessage, PORTINHIBITED);
            status = PORTINHIBITED;
        }
        else if(Port->getPortQueueHandle() == NULL)
        {
            SendError (OutMessage, BADPORT);
            status = BADPORT;
        }
    }
    else
    {
        SendError (OutMessage, BADPORT);
        status = BADPORT;
    }

    return status;
}

INT CommandCode(OUTMESS *&OutMessage, CtiDeviceSPtr Dev)
{
    INT status = NORMAL;

    switch(OutMessage->EventCode & COMMANDMASK)
    {
    case LOOPBACKCOMMAND:
        {
            /* Decide what to put in message based on remote type */
            switch(Dev->getType())
            {
            case TYPE_CCU700:
            case TYPE_CCU710:
                /* Build a loopback preamble */
                LPreamble (OutMessage->Buffer.OutMessage + PREIDLEN, OutMessage->Remote);

                OutMessage->OutLength   = 3 + 3;   /* n't Ask */
                OutMessage->InLength    = 4;
                OutMessage->TimeOut     = 2;
                OutMessage->EventCode   = ENCODED | RESULT | NOWAIT;

                break;

            case TYPE_CCU711:
                /* Build an IDLC loopback request */
                OutMessage->EventCode   = RESULT | RCONT | ENCODED | NOWAIT;
                OutMessage->TimeOut     = 2;
                OutMessage->OutLength   = 3;
                OutMessage->InLength    = 0;
                OutMessage->Source      = 0;
                OutMessage->Destination = DEST_BASE;
                OutMessage->Command     = CMND_ACTIN;

                OutMessage->Buffer.OutMessage[PREIDL] = NO_OP;

                break;

            case TYPE_ILEXRTU:
                /* Build an Ilex RTU loopback (Time) request */
                ILEXHeader (OutMessage->Buffer.OutMessage + PREIDLEN,
                            OutMessage->Remote,
                            ILEXTIMESYNC,
                            TIMESYNC1,
                            TIMESYNC2);

                /* set the timesync function */
                OutMessage->Buffer.OutMessage[PREIDLEN + 2] = ILEXGETTIME;

                OutMessage->TimeOut = 2;
                OutMessage->OutLength = ILEXTIMELENGTH;
                OutMessage->InLength = -1;
                OutMessage->EventCode = ENCODED | RESULT | NOWAIT;

                break;

            case TYPE_LCU415:
            case TYPE_LCU415LG:
            case TYPE_LCU415ER:
            case TYPE_TCU5000:
            case TYPE_TCU5500:
                /* Build a mastercom loopback request */
                MasterHeader (OutMessage->Buffer.OutMessage + PREIDLEN,
                              OutMessage->Remote,
                              MASTERLOOPBACK,
                              0);

                OutMessage->TimeOut = 2;
                OutMessage->OutLength = MASTERLENGTH + 1;

                OutMessage->Buffer.OutMessage[PREIDLEN + 4] = 0xfa;

                OutMessage->InLength = -1;
                OutMessage->EventCode = ENCODED | RESULT | NOWAIT;

                break;

            case TYPE_WELCORTU:
            case TYPE_VTU:

                OutMessage->TimeOut     = 2;
                OutMessage->OutLength   = 0;
                OutMessage->InLength    = -1;
                OutMessage->EventCode   = ENCODED | RESULT | NOWAIT;
                OutMessage->Buffer.OutMessage[PREIDL - 1] = 63;
                OutMessage->Buffer.OutMessage[PREIDL] = 0;

                break;

            default:
                SendError (OutMessage, BADTYPE);
                return BADTYPE;
            }

            break;
        }
    case ANALOGLOOP:
        /* generate message if the appropriate port does not exist */
        if(QueueHandle(OutMessage->Port) == NULL)
        {
            SendError (OutMessage, BADTYPE);
            return BADTYPE;
        }

        /* Dispatch this message to the port */
        if(PortManager.writeQueue (OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
        {
            printf("Error Writing to Queue for Port %2hd\n", OutMessage->Port);
            SendError (OutMessage, QUEUE_WRITE);
        }

        return NORMAL;
    }

    return status;
}

/*----------------------------------------------------------------------------*
 * This function verifies that the emetcon message is properly framed to be sent
 * under the current operating conditions.
 *
 * Take care that this function has no ill effects if it is not Emetcon!
 *----------------------------------------------------------------------------*/
INT ValidateEmetconMessage(OUTMESS *&OutMessage)
{
    INT status = NORMAL;

    if(OutMessage->EventCode & BWORD)
    {
        /*  not allow queing on broadcast commands, or if NoQueing is set */
        if(NoQueing || OutMessage->Remote == CCUGLOBAL)
        {
            OutMessage->EventCode |= DTRAN;
            OutMessage->EventCode &= ~QUEUED;
        }

        /* Check if this is a time sync message and force it to go DTRAN */
        /* first determine if this is a time sync */
        if(OutMessage->Buffer.BSt.Length   == 5   &&
           OutMessage->Buffer.BSt.Function == 73  &&
           OutMessage->Buffer.BSt.IO       == 0)
        {
            OutMessage->EventCode |= (TSYNC | DTRAN);
            OutMessage->EventCode &= ~QUEUED;
        }
    }

    return status;
}

INT CCU711Message(OUTMESS *&OutMessage, CtiDeviceSPtr Dev)
{
    INT            i, j;
    INT            status = NORMAL;

    ULONG          BytesRead;

    OUTMESS        *ArmOutMessage;
    OUTMESS        PeekMessage;

    CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)Dev->getTrxInfo();

    /* Check if this is a queued message */
    if(OutMessage->EventCode & BWORD)
    {
        CTINEXUS *Nexus = OutMessage->ReturnNexus;

        /* this is queing so check if this CCU queue is open */
        if(p711Info->QueueHandle == (HCTIQUEUE) NULL)
        {
            /* Check if calling process expects a response */
            SendError (OutMessage, BADCCU);
            return BADCCU;
        }

        /* Check if we need to set one of the arms */
        if(!(OutMessage->Buffer.BSt.IO & 0x01) && !(OutMessage->Buffer.BSt.Length))
        {
            switch(OutMessage->Buffer.BSt.Function)
            {
            case 0x41:
            case 0x42:
                OutMessage->Buffer.BSt.IO |= Q_ARML;
                break;

            case 0x51:
            case 0x52:
                OutMessage->Buffer.BSt.IO |= Q_ARMS;
            }
        }

        if(OutMessage->Sequence == CtiProtocolEmetcon::Scan_LoadProfile)
        {
            if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Cleaning Excess LP Entries for TargetID " << OutMessage->TargetID << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            // Remove any other Load Profile Queue Entries for this Queue.
            CleanQueue( p711Info->QueueHandle, (void*)OutMessage, findLPRequestEntries, cleanupOutMessages );
        }

        /* Go ahead and send block to the appropriate queing queue */
        if(WriteQueue(p711Info->QueueHandle, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
        {
            printf("Error Writing to Queue for Port: %2hd Remote: %3hd\n", OutMessage->Port, OutMessage->Remote);
            SendError (OutMessage, QUEUE_WRITE);
            return QUEUE_WRITE;
        }
        else
        {
            ULONG Bread = 0;

            for(i = 0; i < 5; i++)
            {
                Bread = 0;

                if(Nexus != NULL)
                {
                    Nexus->CTINexusPeek(&PeekMessage, sizeof(OUTMESS), &Bread);

                    if(Bread > 0)
                    {
                        break;
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    break;
                }

                CTISleep(1000);
            }

            SetEvent(hPorterEvents[P_QUEUE_EVENT]);
        }
    }
    else if(OutMessage->EventCode & ACTIN) /* check if this is an ACTIN command */
    {
        /* Check if this CCU ACTIN queue is open */
        if(p711Info->ActinQueueHandle == (HCTIQUEUE) NULL)
        {
            /* Check if calling process expects a response */
            SendError (OutMessage, BADCCU);
            return BADCCU;
        }

        /* Go ahead and send block to the appropriate ACTIN queue */
        if(WriteQueue (p711Info->ActinQueueHandle, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
        {
            printf("Error Writing to Queue for Port: %2hd  Remote: %3hd\n", OutMessage->Port, OutMessage->Remote);
            SendError (OutMessage, QUEUE_WRITE);
            return QUEUE_WRITE;
        }
        else
        {
            SetEvent(hPorterEvents[P_QUEUE_EVENT]);
        }
    }

    return status;
}


INT ValidateEncodedFlags(OUTMESS *&OutMessage, INT devicetype)
{
    INT status = NORMAL;

    /* check to see if this can be used as RCONT */
    switch(devicetype)
    {
    case TYPE_CCU711:
        if(!(OutMessage->EventCode & RESULT))
        {
            if((OutMessage->Command & 0x0007)  < 4 && OutMessage->Remote != CCUGLOBAL)
            {
                OutMessage->EventCode |= RESULT;
                OutMessage->EventCode |= RCONT;
            }
        }
    }

    return status;
}

/*----------------------------------------------------------------------------*
 * This function builds an OutMessage based upon device type and EventCode
 * settings.  I _believe_ this functino will slowly go out of use in the
 * non-BASIC era we have just entered, however, it will remain here as an
 * example of what once was.
 *
 * Note that OutMessage is GONE following this function's successful completion
 *----------------------------------------------------------------------------*/
INT BuildMessage( OUTMESS *&OutMessage, OUTMESS *&SendOutMessage )
{
    INT status = NORMAL;
    OUTMESS    *ArmOutMessage;
    OUTMESS    PeekMessage;

    INT wordCount = 0;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    /* Make sure we decode it on the return */
    SendOutMessage->EventCode |= DTRAN;

    if(OutMessage->EventCode & AWORD)
    {
        /* build preamble message */
        APreamble (SendOutMessage->Buffer.OutMessage + PREIDLEN, OutMessage->Buffer.ASt);

        /* build the A word */
        A_Word (SendOutMessage->Buffer.OutMessage + PREIDLEN + 3, OutMessage->Buffer.ASt);

        /* calculate an approximate timeout */
        SendOutMessage->TimeOut = TIMEOUT + OutMessage->Buffer.ASt.DlcRoute.Stages;

        /* Calculate the message lengths */
        SendOutMessage->OutLength = AWORDLEN + PREAMLEN + 3;
        SendOutMessage->InLength = 2;
    }
    else if(OutMessage->EventCode & BWORD)
    {
        if(OutMessage->Buffer.BSt.IO & 0x01)
        {
            /* calculate number of d words to ask for */
            wordCount = CtiProtocolEmetcon::determineDWordCount(OutMessage->Buffer.BSt.Length);
            /* calculate message lengths */
            SendOutMessage->InLength = 3 + wordCount * (DWORDLEN + 1);
        }
        else
        {
            /* determine if write or function and build appropriate words */
            if(OutMessage->Buffer.BSt.Length == 0)
            {  /* function */

               /* Check if we need to generate an arm command first */
                switch(OutMessage->Buffer.BSt.Function)
                {
                case 0x41:
                case 0x42:
                case 0x51:
                case 0x52:
                    /* We need to generate the appropriate arm command */
                    if((ArmOutMessage = CTIDBG_new OUTMESS) == NULL)
                    {
                        printf ("Error Allocating Memory\n");
                        return(MEMORY);
                    }

                    *ArmOutMessage = *OutMessage;

                    /* Make sure we decode it on the return */
                    ArmOutMessage->EventCode |= DTRAN;

                    PeekMessage = *ArmOutMessage;

                    /* Load up an Arm */
                    PeekMessage.Buffer.BSt.IO = 0;
                    PeekMessage.Buffer.BSt.Length = 0;

                    switch(OutMessage->Buffer.BSt.Function)
                    {
                    case 0x41:
                    case 0x42:
                        PeekMessage.Buffer.BSt.Function = ARML;
                        break;

                    case 0x51:
                    case 0x52:
                        PeekMessage.Buffer.BSt.Function = ARMS;
                        break;
                    }

                    ArmOutMessage->InLength = 2;

                    /* build the b word */
                    B_Word (ArmOutMessage->Buffer.OutMessage + PREIDLEN + PREAMLEN, PeekMessage.Buffer.BSt, wordCount);

                    ArmOutMessage->TimeOut = TIMEOUT + PeekMessage.Buffer.BSt.DlcRoute.Stages;
                    ArmOutMessage->OutLength = PREAMLEN + BWORDLEN + 3;

                    /* build preamble message */
                    BPreamble (ArmOutMessage->Buffer.OutMessage + PREIDLEN, PeekMessage.Buffer.BSt, wordCount);

                    /* copy the return names */

                    /* load the IDLC specific stuff for DTRAN */
                    ArmOutMessage->Source = 0;
                    ArmOutMessage->Destination = DEST_DLC;
                    ArmOutMessage->Command = CMND_DTRAN;
                    ArmOutMessage->Buffer.OutMessage[6] = (UCHAR)ArmOutMessage->InLength;
                    ArmOutMessage->EventCode &= ~RCONT;

                    QueueBookkeeping(ArmOutMessage);

                    /* transfer the message to the appropriate port queue */
                    if(PortManager.writeQueue (ArmOutMessage->Port, ArmOutMessage->EventCode, sizeof (*ArmOutMessage), (char *) ArmOutMessage, ArmOutMessage->Priority))
                    {
                        printf("Error Writing to Queue for Port %2hd\n", ArmOutMessage->Port);
                        SendError (ArmOutMessage, QUEUE_WRITE);
                    }
                }
            }
            else
            {
                /* write */
                C_Words (SendOutMessage->Buffer.OutMessage+PREIDLEN+PREAMLEN+BWORDLEN,
                         OutMessage->Buffer.BSt.Message,
                         OutMessage->Buffer.BSt.Length,
                         &wordCount);
            }

            /* calculate message lengths */
            SendOutMessage->InLength = 2;
        }

        /* build the b word */
        B_Word (SendOutMessage->Buffer.OutMessage + PREIDLEN + PREAMLEN, OutMessage->Buffer.BSt, wordCount);

        SendOutMessage->TimeOut = TIMEOUT + OutMessage->Buffer.BSt.DlcRoute.Stages * (wordCount + 1);

        if(OutMessage->Buffer.BSt.IO & 0x01)
        {
            SendOutMessage->OutLength = PREAMLEN + BWORDLEN + 3;
        }
        else
            SendOutMessage->OutLength = PREAMLEN + BWORDLEN + wordCount * CWORDLEN + 3;

        /* build preamble message */
        BPreamble (SendOutMessage->Buffer.OutMessage + PREIDLEN, OutMessage->Buffer.BSt, wordCount);

        /* copy the return names */
    }

    /* load the IDLC specific stuff for DTRAN */
    SendOutMessage->Source                 = 0;
    SendOutMessage->Destination            = DEST_DLC;
    SendOutMessage->Command                = CMND_DTRAN;
    SendOutMessage->Buffer.OutMessage[6]   = (UCHAR)SendOutMessage->InLength;
    SendOutMessage->EventCode              &= ~RCONT;

    delete(OutMessage);
    OutMessage = NULL;

    return status;
}

INT QueueBookkeeping(OUTMESS *&SendOutMessage)
{
    INT status = NORMAL;

    CtiDeviceSPtr pDev = DeviceManager.getEqual(SendOutMessage->DeviceID);

    /* Update the number of entries on for this ccu on the port queue */
    switch(pDev->getType())
    {
    case TYPE_CCU711:
        {
            CtiTransmitter711Info *pInfo = (CtiTransmitter711Info*)pDev->getTrxInfo();

            pInfo->PortQueueEnts++;

            if(SendOutMessage->EventCode & RCONT)
            {
                pInfo->PortQueueConts++;
            }
        }
    }

    return status;
}


INT ExecuteGoodRemote(OUTMESS *&OutMessage)
{
    INT            status            = NORMAL;
    CtiDeviceSPtr pDev;


    pDev = DeviceManager.getEqual(OutMessage->DeviceID);

    ValidateEmetconMessage(OutMessage);

    /* Check if we should do that weird 711 type stuff */
    if((pDev->getType() == TYPE_CCU711) &&            // This is a CCU711
       !(OutMessage->EventCode & (DTRAN       |      // AND _NONE_ of these flags are set
                                  ENCODED     |
                                  RIPPLE      |
                                  /*VERSACOM    |*/  //  Versacom messages can be queued now
                                  REMS        |
                                  FISHERPIERCE)))
    {
        status = CCU711Message(OutMessage, pDev);
    }
    else
    {
        if(QueueHandle(OutMessage->Port) != NULL) /* check if the port queue for this port is open */
        {
            if(OutMessage->EventCode & ENCODED)
            {
                ValidateEncodedFlags(OutMessage, pDev->getType());
            }

            QueueBookkeeping(OutMessage);

            /* transfer the message to the appropriate port queue */
            if(PortManager.writeQueue (OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *)OutMessage, OutMessage->Priority))
            {
                printf("Error Writing to Queue for Port %2hd\n", OutMessage->Port);
                SendError(OutMessage, QUEUE_WRITE);
            }
        }
        else  // QueueHandle(OutMessage->Port) is NOT available
        {
            /* if the calling process expects a response we must provide one */
            SendError (OutMessage, BADPORT);
            return BADPORT;
        }
    }

    return status;
}

INT RemoteComm(OUTMESS *&OutMessage)
{
    INT status = NORMAL;

    /* Now check if we know about the remote */
    if(OutMessage->Remote != 0xffff)
    {
        if((status = ValidateRemote(OutMessage)) == NORMAL)
        {
            status = ExecuteGoodRemote(OutMessage);   // Does a WriteQueue eventually if all is OK.
        }
    }
    else
    {
        if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
        {
            printf("Error Writing to Queue for Port %2hd\n", OutMessage->Port);
            SendError (OutMessage, QUEUE_WRITE);
            status = QUEUE_WRITE;
        }
    }


    return status;
}

INT GenerateCompleteRequest(RWTPtrSlist< OUTMESS > &outList, OUTMESS *&OutMessage)
{
    extern CtiConnection VanGoghConnection;

    INT status = NORMAL;
    INT i;

    CtiRequestMsg *pReq = CTIDBG_new CtiRequestMsg(OutMessage->DeviceID, OutMessage->Request.CommandStr);
    RWTPtrSlist< CtiMessage >  vgList;
    RWTPtrSlist< CtiMessage >  retList;

    if(OutMessage != NULL)
    {
        CtiCommandParser parse(pReq->CommandString());

        CtiDeviceSPtr Dev = DeviceManager.getEqual(pReq->DeviceId());

        // Re-establish the connection on the beastie..
        pReq->setRouteId( OutMessage->Request.RouteID );

        if(OutMessage->Request.MacroOffset == 0)
        {
            OutMessage->Request.MacroOffset = Dev->selectInitialMacroRouteOffset(OutMessage->Request.RouteID);
        }

        pReq->setMacroOffset( OutMessage->Request.MacroOffset );


        if(Dev)
        {
            CtiReturnMsg   *pcRet = NULL;
            CtiMessage     *pMsg  = NULL;
            CtiMessage     *pVg  = NULL;

            /*
             *  We will execute based upon the data in the request....
             */

            status = Dev->ExecuteRequest(pReq, CtiCommandParser(pReq->CommandString()), vgList, retList, outList, OutMessage);    // Defined ONLY in dev_base.cpp

            if(status != NORMAL)
            {
                {
                    RWTime NowTime;
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << NowTime << " **** Execute Error **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << NowTime << "   Device:  " << Dev->getName() << endl;
                    dout << NowTime << "   Command: " << pReq->CommandString() << endl;
                    dout << NowTime << "   Status = " << status << ": " << FormatError(status) << endl;

                    if(outList.entries() > 0)
                    {
                        dout << NowTime << "   Sending " << outList.entries() << " requests through porter on error condition" << endl;
                    }
                }

                retList.clearAndDestroy();
                vgList.clearAndDestroy();
            }
            else
            {
                while( (i = retList.entries()) > 0 )
                {
                    CtiReturnMsg *pRet = (CtiReturnMsg *)retList.get();
                    CtiConnection *Conn = NULL;

                    if((Conn = ((CtiConnection*)pRet->getConnectionHandle())) != NULL)
                    {
                        pRet->setExpectMore(TRUE);
                        Conn->WriteConnQue(pRet);
                    }
                    else
                    {
                        delete pRet;
                    }
                }

                while( (i = vgList.entries()) > 0 )
                {
                    CtiMessage *pVg = vgList.get();
                    VanGoghConnection.WriteConnQue(pVg);
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Device unknown, unselected, or DB corrupt " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << RWTime() << " Command " << pReq->CommandString() << endl;
                dout << RWTime() << " Device: " << pReq->DeviceId() << endl;
            }

            status = IDNF;
        }
    }
    else
    {
        status = MEMORY;
    }

    if(pReq)
    {
        delete pReq;
    }

    return status;
}


INT realignNexus(OUTMESS *&OutMessage)
{
    INT i;
    INT loops = sizeof(OUTMESS);
    INT status = NORMAL;

    BYTE nextByte;
    ULONG BytesRead = 1;
    CTINEXUS *MyNexus = OutMessage->ReturnNexus;

    // OutMessage->ReturnNexus
    for(loops = sizeof(OUTMESS); loops > 0 && BytesRead > 0; loops--)
    {
        MyNexus->CTINexusRead(&nextByte, 1, &BytesRead, 60);

        if(BytesRead == 1 && nextByte == 0x02)
        {
            MyNexus->CTINexusRead(&nextByte, 1, &BytesRead, 60);

            if(BytesRead == 1 && nextByte == 0xe0)
            {
                MyNexus->CTINexusRead (OutMessage + 2, sizeof(OUTMESS) - 2, &BytesRead, 60);

                if(BytesRead == (sizeof(OUTMESS) - 2) && (OutMessage->TailFrame[0] != 0xea && OutMessage->TailFrame[1] == 0x03))
                {
                    OutMessage->HeadFrame[0] = 0x02;
                    OutMessage->HeadFrame[1] = 0xe0;
                    OutMessage->ReturnNexus = MyNexus;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Inbound Nexus has been successfully realigned " << endl;
                    }

                    break;
                }
            }
        }
    }

    if(loops <= 0)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Unable to realign inbound Nexus " << endl;
        }
    }

    return status;
}
