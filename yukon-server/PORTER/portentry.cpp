#include "precompiled.h"

#include "connection_client.h"
#include "queues.h"
#include "queue.h"
#include "dsm2.h"
#include "portdecl.h"
#include "StatisticsManager.h"

#include "portglob.h"

#include "c_port_interface.h"
#include "mgr_port.h"
#include "mgr_device.h"
#include "streamLocalConnection.h"

#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "prot_emetcon.h"
#include "trx_711.h"

using namespace std;
using Cti::Porter::PorterStatisticsManager;
using Cti::Timing::Chrono;
using Cti::StreamConnection;
using Cti::StreamLocalConnection;
using Cti::StreamSocketConnection;
using Cti::StreamConnectionException;

extern HCTIQUEUE*   QueueHandle(LONG pid);

INT PorterEntryPoint(OUTMESS *&OutMessage);
INT RemoteComm(OUTMESS *&OutMessage);
INT ValidateRemote(OUTMESS *&OutMessage, CtiDeviceSPtr TransmitterDev);
INT ValidatePort(OUTMESS *&OutMessage);
INT ValidateEmetconMessage(OUTMESS *&OutMessage);
INT CCU711Message(OUTMESS *&OutMessage, CtiDeviceSPtr Dev);
INT VersacomMessage(OUTMESS *&OutMessage);
INT ValidateEncodedFlags(OUTMESS *&OutMessage, INT devicetype);
INT QueueBookkeeping(OUTMESS *&SendOutMessage);
INT ExecuteGoodRemote(OUTMESS *&OutMessage, CtiDeviceSPtr Dev);
INT GenerateCompleteRequest(list< OUTMESS* > &outList, OUTMESS &OutTemplate);

INT ValidateOutMessage(OUTMESS *&OutMessage);
void ConnectionThread(StreamConnection *Nexus);
bool realignNexus(OUTMESS *&OutMessage);

/*
 *  This is the guy who deals with incoming data from any one else in the system.
 */
void ConnectionThread(StreamConnection *MyNexus)
{
    OUTMESS *OutMessage = NULL;
    list< OUTMESS* > outList;

    CTILOG_INFO(dout, "ConnectionThread started");

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
                    Cti::StreamBuffer output;
                    output <<"Portentry built an outmessage for "<< tempDev->getName() <<" at priority "<< OutMessage->Priority <<" retries = "<< OutMessage->Retry;

                    if( OutMessage->Request.CommandStr[0] )
                    {
                        output << endl <<"Command : "<< OutMessage->Request.CommandStr;
                    }

                    CTILOG_DEBUG(dout, output);
                }
            }

            if(outList.size() > 2)
            {
                CTILOG_WARN(dout, "outList.size() > 2");
            }
        }
        else /* read whatever comes in */
        {
            OutMessage = new OUTMESS;

            int bytesRead = 0;

            try
            {
                // MyNexus is expected to be in ReadExactly mode
                // read() can only end if either:
                // - an abort event is signaled, in which case bytesReads will be zero
                // - a full message is received
                // - and exception is thrown
                bytesRead = MyNexus->read(OutMessage, sizeof(OUTMESS), Chrono::infinite, &hPorterEvents[P_QUIT_EVENT]);
            }
            catch( const StreamConnectionException &ex )
            {
                CTILOG_EXCEPTION_WARN(dout, ex, "Nexus read failed");
            }

            if( bytesRead != sizeof(OUTMESS) )
            {
                break; // The for and exit this thread
            }

        }

        if(PorterDebugLevel & PORTER_DEBUG_NEXUSREAD)
        {
            CtiDeviceSPtr tempDev = DeviceManager.getDeviceByID(OutMessage->TargetID ? OutMessage->TargetID : OutMessage->DeviceID);

            if(tempDev)
            {
                Cti::StreamBuffer output;
                output <<"Portentry connection received an outmessage for "<< tempDev->getName() <<" at priority "<< OutMessage->Priority <<" retries = "<< OutMessage->Retry;

                if( OutMessage->Request.CommandStr[0] )
                {
                    output << endl <<"Command : "<< OutMessage->Request.CommandStr;
                }

                CTILOG_DEBUG(dout, output);
            }
        }

        /*
         * Set the handle for the return message.
         */
        OutMessage->ReturnNexus = MyNexus;             /* This had better copy the entire structure CP */

        if(OutMessage->Request.BuildIt == TRUE)
        {
            GenerateCompleteRequest( outList, *OutMessage );

            delete OutMessage;
            OutMessage = NULL;
        }
        else if( OutMessage->DeviceID == 0 || OutMessage->Port == 0 )
        {
            CTILOG_ERROR(dout, "Invalid OUTMESS - Neither deviceid nor portid are defined in the OM request. Returning error to the requestor.");

            SendError (OutMessage, ClientErrors::MissingParameter);      // Message has been consumed!
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

    CTILOG_INFO(dout, "ConnectionThread terminating");

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
    INT status = ClientErrors::None;

    if( status = ValidateOutMessage(OutMessage) )
    {
        return status;
    }

    if( status = ValidatePort(OutMessage) )
    {
        return status;
    }

    if(OutMessage->ExpirationTime == 0)
    {
        OutMessage->ExpirationTime = CtiTime().seconds() + defaultExpirationSeconds;
    }

     //This could go after more checking, but I like it here. I think the message has been checked enough?
    PorterStatisticsManager.newRequest(OutMessage->Port, OutMessage->DeviceID, OutMessage->TargetID, OutMessage->MessageFlags);

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
    if(OutMessage != NULL)
    {

        if(OutMessage->HeadFrame[0] != 0x02 ||
           OutMessage->HeadFrame[1] != 0xe0 ||
           OutMessage->TailFrame[0] != 0xea ||
           OutMessage->TailFrame[1] != 0x03)
        {
            CTILOG_WARN(dout, "OutMessage Misalignment"<< hex << setfill('0') <<
                    endl <<"Head bytes "<< setw(2) << (int)OutMessage->HeadFrame[0] <<" "<< setw(2) << (int)OutMessage->HeadFrame[1] <<
                    endl <<"Tail bytes "<< setw(2) << (int)OutMessage->TailFrame[0] <<" "<< setw(2) << (int)OutMessage->TailFrame[1]);

            if( ! realignNexus(OutMessage) )
            {
                CTILOG_ERROR(dout, "Unable to realign OutMessage");

                delete(OutMessage);
                OutMessage = NULL;

                return ClientErrors::InvalidRequest;
            }
        }

        if( OutMessage->DeviceID <= 0 && OutMessage->TargetID <= 0 )
        {
            CTILOG_ERROR(dout, "Bad OUTMESS received TrxID = "<< OutMessage->Request.GrpMsgID <<
                    endl <<"Command: "<< OutMessage->Request.CommandStr);

            delete(OutMessage);
            OutMessage = NULL;

            return ClientErrors::InvalidRequest;
        }
    }
    else
    {
        return ClientErrors::Memory;
    }

    return ClientErrors::None; // This is A-OK.
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
        CTILOG_ERROR(dout, "Device ID "<< OutMessage->DeviceID <<" not found");

        SendError (OutMessage, ClientErrors::IdNotFound);
        return ClientErrors::IdNotFound;
    }

    return ClientErrors::None;
}

/*----------------------------------------------------------------------------*
 * This function verifies that we know about and have started up the indicated
 * port.  On the first comm to the port, it also initializes it.
 * If the port is non functional, we will make one stab at getting it going
 * if this fails, we fail!
 *----------------------------------------------------------------------------*/

INT ValidatePort(OUTMESS *&OutMessage)
{
    INT status = ClientErrors::None;

    static CtiCriticalSection crit;
    static CtiPortSPtr last_port;
    static long last_port_id;
    static bool first = true;

    CtiPortSPtr Port;

    //  must be threadsafe - Scanner also comes through here
    {
        CtiLockGuard<CtiCriticalSection> guard(crit);

        if( first || last_port_id != OutMessage->Port )
        {
            first = false;

            Port = PortManager.getPortById(OutMessage->Port);

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
            SendError (OutMessage, ClientErrors::PortInhibited);
            status = ClientErrors::PortInhibited;
        }
        else if(Port->getPortQueueHandle() == NULL)
        {
            SendError (OutMessage, ClientErrors::BadPort);
            status = ClientErrors::BadPort;
        }
        else if( ! Port->isViable())
        {
            if( CtiConnection *conn = static_cast<CtiConnection *>(OutMessage->Request.Connection) )
            {
                //  Provide an interim error so they know the comms channel is stalled.
                const YukonError_t error = ClientErrors::PortNotInitialized;

                string error_string = "Error " + CtiNumStr(error) + ": " + GetErrorString(error);

                const long error_id = OutMessage->TargetID ? OutMessage->TargetID : OutMessage->DeviceID;

                CtiReturnMsg *info = new CtiReturnMsg(error_id, OutMessage->Request, error_string, error);

                //  This isn't the last you'll hear from this request.
                info->setExpectMore(true);

                conn->WriteConnQue(info, CALLSITE);
            }
        }
    }
    else
    {
        SendError (OutMessage, ClientErrors::BadPort);
        status = ClientErrors::BadPort;
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
    INT status = ClientErrors::None;

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
    INT status = ClientErrors::None;

    CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)Dev->getTrxInfo();

    /* Check if this is a queued message */
    if(OutMessage->EventCode & BWORD)
    {
        //  we're saving the nexus for use after we've called WriteQueue, which consumes the OutMessage
        StreamConnection *Nexus = OutMessage->ReturnNexus;

        /* this is queing so check if this CCU queue is open */
        if(p711Info->QueueHandle == (HCTIQUEUE) NULL)
        {
            /* Check if calling process expects a response */
            SendError (OutMessage, ClientErrors::BadCcu);
            return ClientErrors::BadCcu;
        }

        if(OutMessage->Sequence == Cti::Protocols::EmetconProtocol::Scan_LoadProfile)
        {
            if( isDebugLudicrous() )
            {
                CTILOG_DEBUG(dout, "Cleaning Excess LP Entries for TargetID "<< OutMessage->TargetID);
            }

            // Remove any other Load Profile Queue Entries for this Queue.
            CleanQueue( p711Info->QueueHandle, (void*)OutMessage, findLPRequestEntries, cleanupOutMessages );
        }

        /* Go ahead and send block to the appropriate queing queue */
        if(WriteQueue(p711Info->QueueHandle, OutMessage->Request.GrpMsgID, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
        {
            CTILOG_ERROR(dout, "Error Writing to Queue for Port: "<< OutMessage->Port <<" Remote: "<< OutMessage->Remote);

            SendError (OutMessage, ClientErrors::QueueWrite);
            return ClientErrors::QueueWrite;
        }
        else
        {
            if( ! Nexus )
            {
                CTILOG_ERROR(dout, "Nexus is Null");
            }
            else
            {
                try
                {
                    OUTMESS PeekMessage;

                    for(int i = 0; i < 20; i++)
                    {
                        if( Nexus->peek(&PeekMessage, sizeof(OUTMESS)) )
                        {
                            break;
                        }

                        CTISleep(250);
                    }
                }
                catch( const StreamConnectionException &ex )
                {
                    CTILOG_EXCEPTION_ERROR(dout, ex);
                }
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
            SendError (OutMessage, ClientErrors::BadCcu);
            return ClientErrors::BadCcu;
        }

        /* Go ahead and send block to the appropriate ACTIN queue */
        if(WriteQueue (p711Info->ActinQueueHandle, OutMessage->Request.GrpMsgID, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
        {
            CTILOG_ERROR(dout, "Could not write to queue for Port "<< OutMessage->Port <<", Remote "<< OutMessage->Remote);
            SendError (OutMessage, ClientErrors::QueueWrite);
            return ClientErrors::QueueWrite;
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
    INT status = ClientErrors::None;

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
    INT status = ClientErrors::None;

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
    INT            status            = ClientErrors::None;

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
            if(PortManager.writeQueue (OutMessage))
            {
                CTILOG_ERROR(dout, "Could not write to queue for Port "<< OutMessage->Port);

                SendError(OutMessage, ClientErrors::QueueWrite);
            }
        }
        else  // QueueHandle(OutMessage->Port) is NOT available
        {
            /* if the calling process expects a response we must provide one */
            SendError (OutMessage, ClientErrors::BadPort);
            return ClientErrors::BadPort;
        }
    }

    return status;
}

INT RemoteComm(OUTMESS *&OutMessage)
{
    INT status = ClientErrors::None;

    /* Now check if we know about the remote */
    if(OutMessage->Remote != 0xffff)
    {
        CtiDeviceSPtr Device = DeviceManager.getDeviceByID(OutMessage->DeviceID);

        if((status = ValidateRemote(OutMessage, Device)) == ClientErrors::None)
        {
            status = ExecuteGoodRemote(OutMessage, Device);   // Does a WriteQueue eventually if all is OK.
        }
    }
    else if(PortManager.writeQueue(OutMessage))
    {
        CTILOG_ERROR(dout, "Could not write to queue for Port "<< OutMessage->Port);

        SendError (OutMessage, ClientErrors::QueueWrite);
        status = ClientErrors::QueueWrite;
    }

    return status;
}

INT GenerateCompleteRequest(list< OUTMESS* > &outList, OUTMESS &OutMessage)
{
    extern CtiClientConnection VanGoghConnection;

    YukonError_t status = ClientErrors::None;

    CtiRequestMsg pReq(OutMessage.DeviceID, OutMessage.Request.CommandStr);
    list< CtiMessage* >  vgList;
    list< CtiMessage* >  retList;

    CtiCommandParser parse(pReq.CommandString());

    CtiDeviceSPtr Dev = DeviceManager.getDeviceByID(pReq.DeviceId());

    if(Dev)
    {
        // Re-establish the connection on the beastie..
        pReq.setRouteId( OutMessage.Request.RouteID );

        if( ! OutMessage.Request.RetryMacroOffset )
        {
            OutMessage.Request.RetryMacroOffset = Dev->selectInitialMacroRouteOffset(OutMessage.Request.RouteID);
        }

        pReq.setMacroOffset( OutMessage.Request.RetryMacroOffset );

        pReq.setMessagePriority(OutMessage.Priority);

        try
        {
            /*
             *  We will execute based upon the data in the request....
             */

            status = Dev->beginExecuteRequestFromTemplate(&pReq, CtiCommandParser(pReq.CommandString()), vgList, retList, outList, &OutMessage);
        }
        catch(...)
        {
            status = ClientErrors::InvalidRequest;

            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "ExecuteRequest FAILED for \""<< Dev->getName() <<"\" "
                    "Command: "<< pReq.CommandString());
        }

        if( status )
        {
            Cti::FormattedList logItems;
            logItems.add("Device")  << Dev->getName();
            logItems.add("Command") << pReq.CommandString();
            logItems.add("Status")  << status <<": "<< GetErrorString(status);

            CTILOG_ERROR(dout, "Execute failed "<<
                    logItems);

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
                    pRet->setExpectMore(true);
                    Conn->WriteConnQue(pRet, CALLSITE);
                }
                else
                {
                    delete pRet;
                }
            }

            if( ! vgList.empty() )
            {
                CTILOG_INFO(dout, "sending "<< vgList.size() <<" "<< (vgList.size() == 1 ? "message":"messages") <<" to dispatch");

                while( ! vgList.empty() )
                {
                    CtiMessage *pVg = vgList.front();vgList.pop_front();
                    VanGoghConnection.WriteConnQue(pVg, CALLSITE);
                }
            }

        }
    }
    else
    {
        Cti::FormattedList logItems;
        logItems.add("Command") << pReq.CommandString();
        logItems.add("Device")  << pReq.DeviceId();

        CTILOG_ERROR(dout, "Device unknown, unselected, or DB corrupt"<<
                logItems);

        status = ClientErrors::IdNotFound;
    }

    return status;
}


bool realignNexus(OUTMESS *&OutMessage)
{
    BYTE nextByte;
    int bytesRead = 1;

    try
    {
        for( int loops = sizeof(OUTMESS); loops > 0 && bytesRead > 0; loops-- )
        {
            bytesRead = OutMessage->ReturnNexus->read(&nextByte, 1, Chrono::seconds(60), &hPorterEvents[P_QUIT_EVENT]);

            if( bytesRead == 1 && nextByte == 0x02 )
            {
                bytesRead = OutMessage->ReturnNexus->read(&nextByte, 1, Chrono::seconds(60), &hPorterEvents[P_QUIT_EVENT]);

                if( bytesRead == 1 && nextByte == 0xe0 )
                {
                    bytesRead = OutMessage->ReturnNexus->read(OutMessage + 2, sizeof(OUTMESS) - 2, Chrono::seconds(60), &hPorterEvents[P_QUIT_EVENT]);

                    if( bytesRead == (sizeof(OUTMESS) - 2) && OutMessage->TailFrame[0] != 0xea && OutMessage->TailFrame[1] == 0x03 )
                    {
                        OutMessage->HeadFrame[0] = 0x02;
                        OutMessage->HeadFrame[1] = 0xe0;

                        CTILOG_INFO(dout, "Inbound Nexus has been successfully realigned");

                        return true;
                    }
                }
            }
        }
    }
    catch( const StreamConnectionException &ex )
    {
        CTILOG_EXCEPTION_ERROR(dout, ex, "Could not read from inbound nexus");
    }

    CTILOG_ERROR(dout, "Unable to realign inbound Nexus");

    return false;
}

