#include "yukon.h"
#if !defined (NOMINMAX)
#define NOMINMAX
#endif
#include <process.h>
#include <iostream>
#include <list>
#include "os2_2w32.h"
#include "cticalls.h"

#include "ctitime.h"
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
#include "porter.h"
#include "portdecl.h"
#include "master.h"

#include "portglob.h"

#include "c_port_interface.h"
#include "mgr_port.h"
#include "mgr_device.h"
#include "dev_base.h"
#include "dev_lcu.h"
#include "dllbase.h"
#include "CtiLocalConnect.h"

#include "logger.h"
#include "guard.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "prot_emetcon.h"
#include "trx_711.h"
#include "utility.h"
#include "dev_mct4xx.h"

using namespace std;

extern HCTIQUEUE*   QueueHandle(LONG pid);
extern CtiQueue< CtiOutMessage, std::greater<CtiOutMessage> > GatewayOutMessageQueue;
extern CtiLocalConnect<INMESS, OUTMESS> PorterToPil;

INT PorterEntryPoint(OUTMESS *&OutMessage);
INT RemoteComm(OUTMESS *&OutMessage);
INT RemotePort(OUTMESS *&OutMessage);
INT ValidateRemote(OUTMESS *&OutMessage, CtiDeviceSPtr TransmitterDev);
INT ValidatePort(OUTMESS *&OutMessage);
INT ValidateEmetconMessage(OUTMESS *&OutMessage);
INT CCU711Message(OUTMESS *&OutMessage, CtiDeviceSPtr Dev);
INT VersacomMessage(OUTMESS *&OutMessage);
INT ValidateEncodedFlags(OUTMESS *&OutMessage, INT devicetype);
INT QueueBookkeeping(OUTMESS *&SendOutMessage);
INT ExecuteGoodRemote(OUTMESS *&OutMessage, CtiDeviceSPtr Dev);
INT GenerateCompleteRequest(list< OUTMESS* > &outList, OUTMESS *&OutTemplate);

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
        dout << CtiTime() << " PorterConnectionThread started as TID:  " << CurrentTID() << endl;
    }

    SetThreadName(-1, "PrtrConn ");

    ::strcpy(PorterListenNexus.Name, "PorterConnectionThread: Listener");

    //Initiate a thread for the porter pil connection
    _beginthread(ConnectionThread, 0, (VOID*)&PorterToPil);

    /*
     *  4/7/99 This is the server side of a new Port Control Nexus
     *  This thread rolls off new instances of this connection on an as needed basis.
     *
     *  1. Create a listener on PORTCONTROLNEXUS for incoming connections
     *  2. Pop off a new thread to manage the returned connection.
     *       NOTE: This deviates in implementation from DSM/2 which spwned a new listener thread
     *             sockets don't care for new listener sockets.
     */

    while(!PorterQuit && PorterListenNexus.CTINexusCreate(PORTCONTROLNEXUS))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** INFO **** PorterConnectionThread unable to create listener. Will attempt again." << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        ::sprintf(NewNexus->Name, "PortControl Nexus %d", iNexus++);

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
    CtiConnect        *MyNexus = (CtiConnect*)Arg;     // This is an established connection with a client!
    OUTMESS        *OutMessage = NULL;

    ULONG                   BytesRead;
    list< OUTMESS* >  outList;

    /* make it clear who is the boss */
    CTISetPriority(PRTYC_TIMECRITICAL, THREAD_PRIORITY_HIGHEST);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " ConnectionThread started as TID:  " << CurrentTID() << endl;
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

        if( outList.size() )
        {
            /*
             *  A previous message has caused a list of OUTMESS objects to require sequential processing!
             */
            OutMessage = outList.front();outList.pop_front();
            OutMessage->Request.BuildIt = FALSE;         // Make this FALSE, so the OutMessage passes into the PorterEntryPoint function.

            if(PorterDebugLevel & PORTER_DEBUG_NEXUSREAD)
            {
                CtiDeviceSPtr tempDev = DeviceManager.getDeviceByID(OutMessage->TargetID ? OutMessage->TargetID : OutMessage->DeviceID);

                if(tempDev)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Portentry built an outmessage for " << tempDev->getName();
                    dout << " at priority " << OutMessage->Priority << " retries = " << OutMessage->Retry << endl;
                    if(::strlen(OutMessage->Request.CommandStr) > 0) dout << "  Command: " << OutMessage->Request.CommandStr << endl;
                }
            }

            if(outList.size() > 2)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                else
                {
                    MyNexus->CTINexusClose();
                    break;   // The for and exit this thread
                }
            }
        }

        if(PorterDebugLevel & PORTER_DEBUG_NEXUSREAD && !(OutMessage->MessageFlags & MessageFlag_RouteToPorterGatewayThread))
        {
            CtiDeviceSPtr tempDev = DeviceManager.getDeviceByID(OutMessage->TargetID ? OutMessage->TargetID : OutMessage->DeviceID);

            if(tempDev)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Portentry connection received an outmessage for " << tempDev->getName();
                dout << " at priority " << OutMessage->Priority << " retries = " << OutMessage->Retry << endl;
                if(::strlen(OutMessage->Request.CommandStr) > 0) dout << "  Command: " << OutMessage->Request.CommandStr << endl;
            }
        }

        /*
         * Set the handle for the return message.
         */
        OutMessage->ReturnNexus = MyNexus;             /* This had better copy the entire structure CP */

        /*
         *  A bit of a wiggle here. 061903 CGP.  Capture and re-route this OM type to PorterGWThread.
         */
        if( OutMessage->MessageFlags & MessageFlag_RouteToPorterGatewayThread )
        {
            if(!stringCompareIgnoreCase(gConfigParms.getValueAsString("PORTER_GATEWAY_SUPPORT"),"true"))
            {
                GatewayOutMessageQueue.putQueue( OutMessage );
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " ***** Gateway message request, but the gateway code was not configured to operate." << endl;
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
                dout << CtiTime() << " **** INVALID OUTMESS ****  Neither deviceid nor portid are defined in the OM request. Returning error to the requestor." << endl;
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

    //  memory leak, but the alternative is screaming death
    //if(MyNexus && MyNexus != &PorterToPil) delete MyNexus;

    if(OutMessage != NULL)
    {
        delete OutMessage;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " ConnectionThread  TID: " << CurrentTID() << " terminating" << endl;
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
    //Expiration default is 1 day.
    static UINT defaultExpirationSeconds = gConfigParms.getValueAsULong("DEFAULT_EXPIRATION_SECONDS", 86400);
    INT status = NORMAL;

    if((status = ValidateOutMessage(OutMessage)) != NORMAL)
    {
        return status;
    }

    if((status = ValidatePort(OutMessage)) != NORMAL)
    {
        return status;
    }

    if(OutMessage->ExpirationTime == 0)
    {
        OutMessage->ExpirationTime = CtiTime().seconds() + defaultExpirationSeconds;
    }

     //This could go after more checking, but I like it here. I think the message has been checked enough?
    statisticsNewRequest(OutMessage->Port, OutMessage->DeviceID, OutMessage->TargetID, OutMessage->MessageFlags);

    /*
     * Ok, all checks passed so far, so lets operate on this remote
     */
    return RemoteComm(OutMessage);
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
                dout << CtiTime() << " !!!! OutMessage Misalignment !!!! " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " Head bytes " << hex << (int)OutMessage->HeadFrame[0] << " " << hex << (int)OutMessage->HeadFrame[1] << dec << endl;
                dout << " Tail bytes " << hex << (int)OutMessage->TailFrame[0] << " " << hex << (int)OutMessage->TailFrame[1] << dec << endl;
            }

            realignNexus(OutMessage);
        }

        if((OutMessage->DeviceID <= 0 && OutMessage->TargetID <= 0))
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);

            dout << CtiTime() << " Bad OUTMESS received TrxID = " << OutMessage->Request.GrpMsgID << endl;
            dout << CtiTime() << "   Command " << OutMessage->Request.CommandStr << endl;

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
    if(PortManager.writeQueue(OutMessage->Port, OutMessage->Request.GrpMsgID, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
    {
        printf("Error Writing to Queue for Port %2hd\n", OutMessage->Port);
        SendError (OutMessage, QUEUE_WRITE);
        return QUEUE_WRITE;
    }
    /* Leave the memory intact */
    return NORMAL;
}

/*----------------------------------------------------------------------------*
 * This function verifies that we know about and have started up the indicated
 * remote.
 *----------------------------------------------------------------------------*/

INT ValidateRemote(OUTMESS *&OutMessage, CtiDeviceSPtr TransmitterDev)
{
    if( !TransmitterDev )
    {
        /* This is a unknown remote so shun it */
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  Device ID " << OutMessage->DeviceID << " not found " << endl;
        }

        SendError (OutMessage, IDNF);
        return IDNF;
    }

    return NORMAL;
}

/*----------------------------------------------------------------------------*
 * This function verifies that we know about and have started up the indicated
 * port.  On the first comm to the port, it also initializes it.
 * If the port is non functional, we will make one stab at getting it going
 * if this fails, we fail!
 *----------------------------------------------------------------------------*/

INT ValidatePort(OUTMESS *&OutMessage)
{
    INT status = NORMAL;

    static CtiCriticalSection crit;
    static CtiPortSPtr last_port;
    static long last_port_id;
    static bool first;

    CtiPortSPtr Port;

    //  must be threadsafe - Scanner also comes through here
    {
        CtiLockGuard<CtiCriticalSection> guard(crit);

        if( first || last_port_id != OutMessage->Port )
        {
            first = false;

            Port = PortManager.PortGetEqual(OutMessage->Port);

            last_port_id = OutMessage->Port;
            last_port = Port;
        }
        else
        {
            Port = last_port;
        }
    }

    /* Check the memory database to see if a port like this exists */
    if( Port )
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
        //  do not allow queing if this is a foreign CCU port (shared CCU), if it's a broadcast command, or if it's a timesync
        //    This is only to handle things writing directly to the port that do not go through CtiRouteCCU::ExecuteRequest().
        if( isForeignCcuPort(OutMessage->Port)
            || (OutMessage->Remote == CCUGLOBAL)
            || (OutMessage->EventCode & TSYNC) )
        {
            OutMessage->EventCode |= DTRAN;
            OutMessage->EventCode &= ~QUEUED;
        }
    }

    return status;
}

INT CCU711Message(OUTMESS *&OutMessage, CtiDeviceSPtr Dev)
{
    INT            i;
    INT            status = NORMAL;

    OUTMESS        PeekMessage;

    CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)Dev->getTrxInfo();

    /* Check if this is a queued message */
    if(OutMessage->EventCode & BWORD)
    {
        //  we're saving the nexus for use after we've called WriteQueue, which consumes the OutMessage
        CtiConnect *Nexus = OutMessage->ReturnNexus;

        /* this is queing so check if this CCU queue is open */
        if(p711Info->QueueHandle == (HCTIQUEUE) NULL)
        {
            /* Check if calling process expects a response */
            SendError (OutMessage, BADCCU);
            return BADCCU;
        }

        if(OutMessage->Sequence == Cti::Protocols::EmetconProtocol::Scan_LoadProfile)
        {
            if( isDebugLudicrous() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Cleaning Excess LP Entries for TargetID " << OutMessage->TargetID << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            // Remove any other Load Profile Queue Entries for this Queue.
            CleanQueue( p711Info->QueueHandle, (void*)OutMessage, findLPRequestEntries, cleanupOutMessages );
        }

        /* Go ahead and send block to the appropriate queing queue */
        if(WriteQueue(p711Info->QueueHandle, OutMessage->Request.GrpMsgID, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
        {
            printf("Error Writing to Queue for Port: %2hd Remote: %3hd\n", OutMessage->Port, OutMessage->Remote);
            SendError (OutMessage, QUEUE_WRITE);
            return QUEUE_WRITE;
        }
        else
        {
            ULONG Bread = 0;

            for(i = 0; i < 20; i++)
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
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    break;
                }

                CTISleep(250);
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
        if(WriteQueue (p711Info->ActinQueueHandle, OutMessage->Request.GrpMsgID, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
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


INT QueueBookkeeping(OUTMESS *&SendOutMessage)
{
    INT status = NORMAL;

    CtiDeviceSPtr pDev = DeviceManager.getDeviceByID(SendOutMessage->DeviceID);

    /* Update the number of entries on for this ccu on the port queue */
    if( pDev )
    {
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
    }

    return status;
}


INT ExecuteGoodRemote(OUTMESS *&OutMessage, CtiDeviceSPtr pDev)
{
    INT            status            = NORMAL;

    //  if this is a nonqueued port, we will come out of this marked DTRAN
    ValidateEmetconMessage(OutMessage);

    /* Check if we should do that weird 711 type stuff */
    if(pDev && (pDev->getType() == TYPE_CCU711) &&            // This is a CCU711
       !(OutMessage->EventCode & (DTRAN       |      // AND _NONE_ of these flags are set
                                  ENCODED     |
                                  RIPPLE      |
                                  /*VERSACOM    |*/  //  Versacom messages can be queued now
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
            if(PortManager.writeQueue (OutMessage->Port, OutMessage->Request.GrpMsgID, sizeof (*OutMessage), (char *)OutMessage, OutMessage->Priority))
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
        CtiDeviceSPtr Device = DeviceManager.getDeviceByID(OutMessage->DeviceID);

        if((status = ValidateRemote(OutMessage, Device)) == NORMAL)
        {
            status = ExecuteGoodRemote(OutMessage, Device);   // Does a WriteQueue eventually if all is OK.
        }
    }
    else
    {
        if(PortManager.writeQueue(OutMessage->Port, OutMessage->Request.GrpMsgID, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
        {
            printf("Error Writing to Queue for Port %2hd\n", OutMessage->Port);
            SendError (OutMessage, QUEUE_WRITE);
            status = QUEUE_WRITE;
        }
    }


    return status;
}

INT GenerateCompleteRequest(list< OUTMESS* > &outList, OUTMESS *&OutMessage)
{
    extern CtiConnection VanGoghConnection;

    INT status = NORMAL;
    INT i;

    CtiRequestMsg *pReq = CTIDBG_new CtiRequestMsg(OutMessage->DeviceID, OutMessage->Request.CommandStr);
    list< CtiMessage* >  vgList;
    list< CtiMessage* >  retList;

    if(OutMessage != NULL)
    {
        CtiCommandParser parse(pReq->CommandString());

        CtiDeviceSPtr Dev = DeviceManager.getDeviceByID(pReq->DeviceId());

        if(Dev)
        {
            CtiReturnMsg   *pcRet = NULL;
            CtiMessage     *pMsg  = NULL;
            CtiMessage     *pVg  = NULL;

            // Re-establish the connection on the beastie..
            pReq->setRouteId( OutMessage->Request.RouteID );

            if(OutMessage->Request.MacroOffset == 0)
            {
                OutMessage->Request.MacroOffset = Dev->selectInitialMacroRouteOffset(OutMessage->Request.RouteID);
            }

            pReq->setMacroOffset( OutMessage->Request.MacroOffset );

            pReq->setMessagePriority(OutMessage->Priority);

            /*
             *  We will execute based upon the data in the request....
             */

            status = Dev->beginExecuteRequest(pReq, CtiCommandParser(pReq->CommandString()), vgList, retList, outList, OutMessage);

            if(status != NORMAL)
            {
                {
                    CtiTime NowTime;
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << NowTime << " **** Execute Error **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << NowTime << "   Device:  " << Dev->getName() << endl;
                    dout << NowTime << "   Command: " << pReq->CommandString() << endl;
                    dout << NowTime << "   Status = " << status << ": " << FormatError(status) << endl;

                    if(outList.size() > 0)
                    {
                        dout << NowTime << "   Sending " << outList.size() << " requests through porter on error condition" << endl;
                    }
                }
                delete_container(vgList);
                delete_container(retList);
                retList.clear();
                vgList.clear();
            }
            else
            {
                while( !retList.empty() )
                {
                    CtiReturnMsg *pRet = (CtiReturnMsg *)retList.front();retList.pop_front();
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

                while( !vgList.empty() )
                {
                    CtiMessage *pVg = vgList.front();vgList.pop_front();
                    VanGoghConnection.WriteConnQue(pVg);
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Device unknown, unselected, or DB corrupt " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << CtiTime() << " Command " << pReq->CommandString() << endl;
                dout << CtiTime() << " Device: " << pReq->DeviceId() << endl;
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
    INT loops = sizeof(OUTMESS);
    INT status = NORMAL;

    BYTE nextByte;
    ULONG BytesRead = 1;

    for(loops = sizeof(OUTMESS); loops > 0 && BytesRead > 0; loops--)
    {
        OutMessage->ReturnNexus->CTINexusRead(&nextByte, 1, &BytesRead, 60);

        if(BytesRead == 1 && nextByte == 0x02)
        {
            OutMessage->ReturnNexus->CTINexusRead(&nextByte, 1, &BytesRead, 60);

            if(BytesRead == 1 && nextByte == 0xe0)
            {
                OutMessage->ReturnNexus->CTINexusRead (OutMessage + 2, sizeof(OUTMESS) - 2, &BytesRead, 60);

                if(BytesRead == (sizeof(OUTMESS) - 2) && (OutMessage->TailFrame[0] != 0xea && OutMessage->TailFrame[1] == 0x03))
                {
                    OutMessage->HeadFrame[0] = 0x02;
                    OutMessage->HeadFrame[1] = 0xe0;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Inbound Nexus has been successfully realigned " << endl;
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
            dout << CtiTime() << " Unable to realign inbound Nexus " << endl;
        }
    }

    return status;
}

