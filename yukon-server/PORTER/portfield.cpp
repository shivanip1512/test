
/*-----------------------------------------------------------------------------*
*
* File:   portfield
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.41 $
* DATE         :  $Date: 2002/11/15 20:44:32 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#pragma warning(disable : 4786)
#pragma title ( "Higher Level Port Handshake Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1999" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        PORTPORT.C

    Purpose:
        Following Routines form a subsystem that take requests off of the
        by port queue's and process them out to the ports to field devices
        stolen from dsm2's portport.c

    The following procedures are contained in this module:
        PortThread              RemoteInitialize
        RemoteReset

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        8-25-93     Added code to load routes if flag set         WRO
        9-7-93      Converted to 32 bit                           WRO
        11-1-93     Modified to keep stats temporarily in memory  TRH
        11-30-93    Added startup code for TCP/IP interface       WRO
        2-2-94      Added support for SES 92                      WRO
        9-2-94      Changed intialize to note 711's and default   WRO

        12-99       NT and functionalized                         CGP

   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include <iostream>
#include <iomanip>
#include <set>
using namespace std;

#include "os2_2w32.h"
#include "cticalls.h"
#include "cti_asmc.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include <rw\thr\mutex.h>

#include "color.h"
#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "master.h"
#include "portsup.h"
#include "portdecl.h"
#include "tcpsup.h"
#include "perform.h"
#include "tapterm.h"
#include "porttcp.h"

#include "portglob.h"

#include "connection.h"
#include "c_port_interface.h"
#include "mgr_port.h"
#include "mgr_device.h"
#include "dev_base.h"
#include "dev_cbc6510.h"
#include "dev_ied.h"
#include "dev_schlum.h"
#include "dev_remote.h"
#include "dev_kv2.h"
#include "msg_trace.h"
#include "porttypes.h"
#include "xfer.h"
#include "rtdb.h"

#include "port_base.h"
#include "prot_711.h"
#include "statistics.h"
#include "trx_info.h"
#include "trx_711.h"
#include "utility.h"

#define INF_LOOP_COUNT 10000


/*
 *  gQueSlot is used by dialup ports to pluck the next item from within the queue's guts.
 */
static ULONG   gQueSlot = 0;

extern void DisplayTraceList( CtiPortSPtr Port, RWTPtrSlist< CtiMessage > &traceList, bool consume);
extern HCTIQUEUE* QueueHandle(LONG pid);

bool deviceCanSurviveThisStatus(INT status);
void commFail(CtiDeviceBase *Device, INT state);
BOOL areAnyOutMessagesForMyDevID(void *pId, void* d);
BOOL areAnyOutMessagesForMyRteID(void *pId, void* d);
BOOL areAnyOutMessagesForUniqueID(void *pId, void* d);
BOOL isTAPTermPort(LONG PortNumber);
INT RequeueReportError(INT status, OUTMESS *OutMessage);
INT PostCommQueuePeek(CtiPortSPtr Port, CtiDevice *Device, OUTMESS *OutMessage);
INT VerifyPortStatus(CtiPortSPtr Port);
INT ResetPortParameters(CtiPortSPtr Port);
INT ResetCommsChannel(CtiPortSPtr Port, CtiDevice *Device, OUTMESS *OutMessage);
INT InitInMessage(INMESS *InMessage, OUTMESS *OutMessage);
INT CheckInhibitedState(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDevice *Device);
INT ValidateDevice(CtiPortSPtr Port, CtiDevice *Device);
INT VTUPrep(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDevice *Device);
INT EstablishConnection(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDevice *Device);
INT DevicePreprocessing(CtiPortSPtr Port, OUTMESS *&OutMessage, CtiDevice *Device);
INT CommunicateDevice(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDevice *Device);
INT NonWrapDecode(INMESS *InMessage, CtiDevice *Device);
INT CheckAndRetryMessage(INT CommResult, CtiPortSPtr Port, INMESS *InMessage, OUTMESS *&OutMessage, CtiDevice *Device);
INT DoProcessInMessage(INT CommResult, CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDevice *Device);
INT ReturnResultMessage(INT CommResult, INMESS *InMessage, OUTMESS *&OutMessage);
INT UpdatePerformanceData(INT CommResult, CtiPortSPtr Port, CtiDevice *Device);
INT InitializeHandshake (CtiPortSPtr aPortRecord, CtiDeviceIED *aIEDDevice, RWTPtrSlist< CtiMessage > &traceList);
INT TerminateHandshake (CtiPortSPtr aPortRecord, CtiDeviceIED *aIEDDevice, RWTPtrSlist< CtiMessage > &traceList);
INT PerformRequestedCmd ( CtiPortSPtr aPortRecord, CtiDeviceIED *aIED, INMESS *aInMessage, OUTMESS *aOutMessage, RWTPtrSlist< CtiMessage > &traceList);
INT ReturnLoadProfileData ( CtiPortSPtr aPortRecord, CtiDeviceIED *aIED, INMESS *aInMessage, OUTMESS *aOutMessage, RWTPtrSlist< CtiMessage > &traceList);
INT LogonToDevice( CtiPortSPtr aPortRecord, CtiDeviceIED *aIED, INMESS *aInMessage, OUTMESS *aOutMessage, RWTPtrSlist< CtiMessage > &traceList);
INT verifyConnectedDevice(CtiPortSPtr Port, CtiDevice *pDevice, LONG &oldid, LONG &portConnectedUID);

/* Threads that handle each port for communications */
VOID PortThread(void *pid)
{
    INT            status;

    ULONG          i, j, ReadLength;
    ULONG          BytesWritten;
    INMESS         InMessage;
    OUTMESS        *OutMessage;
    REQUESTDATA    ReadResult;
    BYTE           ReadPriority;
    ULONG          MSecs, QueEntries;

    RWTime         lastQueueReportTime;

    LONG           portid = (LONG)pid;      // NASTY CAST HERE!!!

    CtiDeviceBase  *Device = NULL;

    CtiPortSPtr    Port( PortManager.PortGetEqual( portid ) );      // Bump the reference count on the shared object!

    lastQueueReportTime = lastQueueReportTime.seconds() - (lastQueueReportTime.seconds() % 300L);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " PortThread TID: " << CurrentTID () << " for port: " << setw(4) << Port->getPortID() << " / " << Port->getName() << endl;
    }

    /* make it clear who is the boss */
    CTISetPriority (PRTYS_THREAD, PRTYC_TIMECRITICAL, 31, 0);

    // Let the threads get up and running....
    WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 2500L);

    /* and wait for something to come in */
    for(;!PorterQuit;)
    {
        if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0L) )
        {
            PorterQuit = TRUE;
            continue;
        }

        if( NORMAL != (status = ResetCommsChannel(Port, Device, OutMessage)) )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Port " << Port->getName() << " will not init. Waiting 15 seconds " << endl;
            }

            if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 15000L) )
            {
                PorterQuit = TRUE;
            }

            continue; // 012601 CGP...  What is this..  // return;
        }

        try
        {
            rwServiceCancellation( );
        }
        catch(const RWCancellation& cMsg)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            return;  // We've been nixed!
        }

        /*
         *  This is a Read from the CTI queueing structures which will originate from
         *  some other requestor.  This is where this thread blocks and waits if there are
         *  no entries on the queue.  One may think of the "above" call as "cleanup" for the
         *  previous ReadQueue's operation. Note that the ReadQueue call mallocs space for the
         *  OutMessage pointer, and fills it from it's queue entries!
         */
        if((status = ReadQueue (Port->getPortQueueHandle(), &ReadResult, &ReadLength, (PPVOID) &OutMessage, gQueSlot, DCWW_WAIT, &ReadPriority, &QueEntries)) != NORMAL )
        {
            if(status == ERROR_QUE_EMPTY)
            {
                if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 500L) )
                {
                    PorterQuit = TRUE;
                }
            }
            else if( status != ERROR_QUE_UNABLE_TO_ACCESS)
            {
                printf ("Error Reading Port Queue\n");
            }
            continue;
        }
        else if(PorterDebugLevel & PORTER_DEBUG_PORTQUEREAD)
        {
            CtiDeviceBase *tempDev = DeviceManager.getEqual(OutMessage->TargetID);

            if(tempDev)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Port " << Port->getName() << " read an outmessage for " << tempDev->getName();
                dout << " at priority " << OutMessage->Priority << " retries = " << OutMessage->Retry << endl;
                dout << RWTime() << " Port has " << QueEntries << " pending OUTMESS requests " << endl;
            }
        }

        if(QueEntries > 5000 && RWTime() > lastQueueReportTime)  // Ok, we may have an issue here....
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Port " << Port->getName() << " has " << QueEntries << " pending OUTMESS requests " << endl;
            }
            lastQueueReportTime = RWTime() + 300;
        }

        statisticsNewRequest(OutMessage->Port, OutMessage->DeviceID, OutMessage->TargetID);

        if(PorterDebugLevel & PORTER_DEBUG_VERBOSE)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Portfield connection read OutMessage->DeviceID = " << OutMessage->DeviceID << endl;
            dout << RWTime() << "                           OutMessage->Remote   = " << OutMessage->Remote << endl;
            dout << RWTime() << "                           OutMessage->Port     = " << OutMessage->Port << endl;
        }

        if(OutMessage->DeviceID == 0 && OutMessage->Remote != 0 && OutMessage->Port != 0)
        {
            if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " looking for CTIDBG_new deviceID..." << endl;
            }

            CtiDeviceBase *tempDev = DeviceManager.RemoteGetPortRemoteEqual(OutMessage->Port, OutMessage->Remote);

            if( tempDev != NULL )
            {
                OutMessage->DeviceID = tempDev->getID();

                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " assigned CTIDBG_new deviceID = " << tempDev->getID() << endl;
                }
            }
            else
            {
                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " did not assign CTIDBG_new deviceID" << endl;
                }
            }
        }

        gQueSlot = 0;   // Set this back to zero every time regardless of port type.

        // Make sure port hasn't changed any here..
        if( ResetPortParameters(Port) != NORMAL )
        {
            if(OutMessage->DeviceID != OutMessage->TargetID)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << "Port " << Port->getPortNameWas() << " has a re-init problem" << endl;
                dout << RWTime() << "  Messages have been lost." << endl;
            }

            if(OutMessage != NULL)
            {
                delete OutMessage;
                OutMessage = NULL;
            }

            continue;
        }
        // Copy a good portion of the OutMessage to the to-be-formed InMessage
        InitInMessage(&InMessage, OutMessage);

        /* get the device record for this id */
        Device = DeviceManager.RemoteGetEqual(OutMessage->DeviceID);

        if(Device == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            cerr << "Port " << Port->getPortID() << " just received a message for device id " << \
            OutMessage->DeviceID << endl << \
            " Porter does not seem to know about him and is throwing away the message!" << endl;

            SendError(OutMessage, status);
            continue;
        }

        if((status = CheckInhibitedState(Port, &InMessage, OutMessage, Device)) != NORMAL)
        {
            SendError(OutMessage, status);
            continue;
        }

        /* Check if this is an analog loopback */
#if 0
        if((status = VTUPrep(&Port, &InMessage, OutMessage, Device)) != NORMAL)
        {
            SendError (OutMessage, status);
            continue;
        }
#endif

        /* Make sure everything is A-OK with this device */
        if((status = ValidateDevice(Port, Device)) != NORMAL)
        {
            SendError (OutMessage, status);
            continue;
        }

        // See if there is a reason to proceed..
        if((status = DevicePreprocessing(Port, OutMessage, Device)) != NORMAL) /* do any preprocessing according to type */
        {
            RequeueReportError(status, OutMessage);
            continue;            // Connection never happened!
        }

        /* Check if this port is dial up and initiate connection. */
        if((status = EstablishConnection(Port, &InMessage, OutMessage, Device)) != NORMAL)
        {
            if(status != RETRY_SUBMITTED)
            {
                Port->reset(TraceFlag);
            }

            RequeueReportError(status, OutMessage);
            continue;
        }

        /* Execute based on wrap protocol.  Sends OutMessage and fills in InMessage */
        i = CommunicateDevice(Port, &InMessage, OutMessage, Device);

        /* Non wrap protcol specific communications stuff */
        if(!i)      // No error yet.
        {
            i = NonWrapDecode(&InMessage, Device);
        }

        /*
         * Check if we need to do a retry on this command. Returns RETRY_SUBMITTED if the message has
         * been requeued, or the CommunicateDevice returned otherwise
         */
        if(CheckAndRetryMessage(i, Port, &InMessage, OutMessage, Device) == RETRY_SUBMITTED)
        {
            continue;  // It has been re-queued!
        }
        else   /* we are either successful or retried out */
        {
            if((status = DoProcessInMessage(i, Port, &InMessage, OutMessage, Device)) != NORMAL)
            {
                RequeueReportError(status, OutMessage);
                continue;
            }
        }

        if((status = ReturnResultMessage(i, &InMessage, OutMessage)) != NORMAL)
        {
            RequeueReportError(status, OutMessage);
            continue;
        }

        if( Port->getType() == TCPIPSERVER )
        {
            Port->close(TraceFlag);
        }

        if(OutMessage != NULL)
        {
            delete OutMessage; /* free up the OutMessage, it made a successful run! */
            OutMessage = NULL;
        }
    }  /* and do it all again */
}

void RemoteInitialize (CtiDeviceBase *&Device, CtiPortSPtr pPort)
{
    ULONG i;
    CtiTransmitterInfo *pInfo = NULL;

    if(Device->hasTrxInfo() && !Device->isInhibited())
    {
        if(pPort->getPortID() == Device->getPortID())
        {
            if( 0 <= Device->getAddress() && Device->getAddress() < MAXIDLC )
            {
                if( (pInfo = Device->getTrxInfo()) == NULL )
                {
                    pInfo = Device->initTrxInfo();

                    if(Device->getAddress() > 0)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Enabling P: " << Device->getPortID() << " D: " << Device->getID() << " / " << Device->getName() << ". DLC ID: " << Device->getAddress() << endl;
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Enabling P: " << Device->getPortID() << " D: " << Device->getID() << " / " << Device->getName() << endl;
                    }

                    pInfo->SetStatus( NEEDSRESET );
                }
            }
        }
    }
}

/* Routine to initialize a remote based on it's type */
void RemoteReset(CtiDeviceBase *&Device, CtiPortSPtr Port)
{
    extern LoadRemoteRoutes(CtiDeviceBase *RemoteRecord);

    ULONG j;
    INT   eRet = 0;

    if(Port->getPortID() == Device->getPortID() && !Device->isInhibited())
    {
        if(0 <= Device->getAddress() && Device->getAddress() < MAXIDLC)
        {
            CtiTransmitterInfo *pInfo = (CtiTransmitterInfo *)Device->getTrxInfo();

            if(pInfo)
            {
                if(Port->getProtocol() == IDLC)
                {
                    if(Device->getAddress() != RTUGLOBAL && Device->getAddress() != CCUGLOBAL)
                    {
                        switch(Device->getType())
                        {
                        case TYPE_CCU711:
                            {
                                if(!(Port->isDialup()))
                                {
                                    j = 0;
                                    while((eRet = IDLCInit(Port, Device, &pInfo->RemoteSequence)) && j++ < 1);
                                }

                                if(!eRet)
                                {
                                    if(ResetAll711s)
                                    {
                                        /* Reset the whole thing */
                                        IDLCFunction (Device, 0, DEST_BASE, COLD);
                                    }
                                    else
                                    {
                                        /* Download the delay sets */
                                        IDLCSetDelaySets (Device);

                                        /* flush the queue's */
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " Reset CCU: " << Device->getName() << "'s queueing control" << endl;
                                        }
                                        IDLCFunction (Device, 0, DEST_QUEUE, CLRDY);

                                        /* Check if we need to load the routes */
                                        if(LoadRoutes)
                                        {
                                            LoadRemoteRoutes(Device);
                                        }
                                    }
                                }
                            }

                            break;

                        case TYPE_LCU415:
                        case TYPE_LCU415LG:
                        case TYPE_LCU415ER:
                        case TYPE_LCUT3026:
                        case TYPE_TCU5000:
                        case TYPE_TCU5500:
                        default:
                            break;
                        }
                    }
                }
                pInfo->ClearStatus(NEEDSRESET);
            }
        }
    }
    return;
}

/*----------------------------------------------------------------------------*
 * This function esentially is used to hang up the modem if it is offhook.
 * As a throughput enhancement, it was modified to peek at the queues before
 * doing so to verify that no entries exist for this device.  It will peek
 * 4 times per second for the post comm wait number of seconds.
 *----------------------------------------------------------------------------*/
INT PostCommQueuePeek(CtiPortSPtr Port, CtiDevice *Device, OUTMESS *OutMessage)
{
    INT    i = 0;
    INT    status = NORMAL;
    ULONG  QueueCount = 0;

    bool    bDisconnect = false;

    if(Port->connected() && (Port->shouldDisconnect() || Device == NULL))
    {
        Port->disconnect(Device, TraceFlag);
    }

    if(Port->connected() && Device != NULL)
    {
        ULONG stayConnectedMin = Device->getMinConnectTime();
        ULONG stayConnectedMax = Device->getMaxConnectTime();

        if((gQueSlot = SearchQueue(Port->getPortQueueHandle(), (void*)Port->getConnectedDeviceUID(), areAnyOutMessagesForUniqueID)) != 0 )
        {
            if(PorterDebugLevel & PORTER_DEBUG_VERBOSE && Device)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Additional queue entry found for " << Device->getName() << endl;
            }
        }
        else
        {
            if(stayConnectedMin)
            {
                for(i = 0; i < (ULONG)(4 * stayConnectedMin - 1); i++)
                {
                    /* Check the queue 4 times per second for a CTIDBG_new entry for this port ... */
                    if((gQueSlot = SearchQueue(Port->getPortQueueHandle(), (void*)Port->getConnectedDeviceUID(), areAnyOutMessagesForUniqueID)) != 0 )
                    {
                        break;
                    }

                    CTISleep (250L);
                }
            }

            /* do not reinit i since times are non cumulative! */
            for( ; gQueSlot == 0 && i < (ULONG)(4 * stayConnectedMax); i++)
            {
                /* Check the queue 4 times per second for a CTIDBG_new entry for this port ... */
                if( !QueryQueue(Port->getPortQueueHandle(), &QueueCount) && QueueCount > 0)
                {
                    gQueSlot = SearchQueue(Port->getPortQueueHandle(), (void*)Port->getConnectedDeviceUID(), areAnyOutMessagesForUniqueID);

                    if( gQueSlot == 0 ) // No entry, or the entry is not first on the list
                    {
                        bDisconnect = true;
                        break;
                    }
                    else
                    {
                        break;
                    }
                }

                CTISleep (250L);
            }
        }

        if(gQueSlot == 0 || bDisconnect)
        {
            /* Hang Up */
            Port->disconnect(Device, TraceFlag);
        }
    }

    return status;
}


INT ResetPortParameters(CtiPortSPtr Port)
{
    INT status = NORMAL;

#if 0    // 8/9/01 CGP This is not working correctly anyway!

    if( !(Port->isTCPIPPort()) )
    {
        /*
         *  Check if the port has changed name i.e. com1 -> com2 etc... via the editor.
         */
        if( stricmp(Port->getName(), Port->getPortNameWas()) )
        {
            if(Port->init())
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Error initializing Virtual Port " << Port->getPortID() << " on " << Port->getName() << endl;
                }
                status = ErrPortInitFailed;
            }
            else
            {
                // Mark it so we can tell if the "ComX" designation changes.
                Port->setPortNameWas(Port->getName());
            }
        }

        /*
         *  Lastly, check if the port's baud rate has been modified by editor.
         */
        if(!status && Port->getBaudRate() != Port->getLastBaudRate())
        {
            Port->setLastBaudRate( Port->getBaudRate() );
        }
    }

#endif

    return status;
}

/*----------------------------------------------------------------------------*
 * This function prepares or resets the communications port for (re)use.
 * it checks it for proper state and setup condition.
 *----------------------------------------------------------------------------*/
INT ResetCommsChannel(CtiPortSPtr Port, CtiDevice *Device, OUTMESS *OutMessage)
{
    INT status = NORMAL;

    /*
     *  If the port is inhibited, don't talk to it ok...
     */
    if( !(Port->isInhibited()) )
    {
        /*
         *  If the port has not been intialized at all, do it NOW!
         */
        if( (Port->getLastBaudRate() == 0 || Port->needsReinit()) && !Port->isDialup() )
        {
            /* set up the port */
            if( (status = Port->init()) != NORMAL )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Error initializing Virtual Port " << Port->getPortID() <<" on " << Port->getName() << endl;
                }

                return status;
            }
            else
            {
                Port->setPortNameWas( Port->getName() );
                Port->setLastBaudRate( Port->getBaudRate() );
            }

            /* Report which devices are available and set queues for those using IDLC*/
            {
                CtiPortManager::LockGuard  prt_guard(PortManager.getMux());         //
                RWRecursiveLock<RWMutexLock>::LockGuard  dev_guard(DeviceManager.getMux());       //

                //DeviceManager.getMap().apply(RemoteInitialize, (void*)&Port);

                CtiRTDB<CtiDeviceBase>::CtiRTDBIterator itr_dev(DeviceManager.getMap());

                for(; ++itr_dev ;)
                {
                    CtiDeviceBase *RemoteDevice = itr_dev.value();
                    RemoteInitialize(RemoteDevice, Port);
                }
            }

            /* If neccessary start the TCPIP Interface */
            if(
              (StartTCPIP == TCP_SES92)                            ||
              (StartTCPIP == TCP_CCU710 &&  Port->isTCPIPPort())   ||
              (StartTCPIP == TCP_WELCO  &&  Port->isTCPIPPort())
              )
            {

#if 0 // 040300 CGP FIX FIX FIX This was pulled for brevity!
                if(PortTCPIPStart(ThreadPortNumber))
                {
                    printf ("Error Starting TCP/IP Interface\n");
                    // _endthread ();
                }
#else
                printf ("TCP/IP Interface disabled by #ifdef. CGP 040300\n");
#endif
            }
        }

        if(status == NORMAL)
        {
            status = Port->verifyPortStatus();    // Is this port still A-OK?

            if(Port->isDialup())
            {
                PostCommQueuePeek(Port, Device, OutMessage);
            }
        }
    }

    return status;
}

INT InitInMessage(INMESS *InMessage, OUTMESS *OutMessage)
{
    INT status = NORMAL;

    // Clear it...
    memset(InMessage, 0, sizeof(INMESS));

    /* Lotsa stuff requires the InMessage to be loaded so load it */
    memcpy(&(InMessage->Return),&(OutMessage->Request), sizeof(PIL_ECHO));  // 092899 Get this crap back to the requestor.

    InMessage->DeviceID        = OutMessage->DeviceID;
    InMessage->TargetID        = OutMessage->TargetID;
    // 082002 CGP // InMessage->RouteID         = OutMessage->RouteID;

    InMessage->Remote          = OutMessage->Remote;
    InMessage->Port            = OutMessage->Port;
    InMessage->Sequence        = OutMessage->Sequence;
    InMessage->ReturnNexus     = OutMessage->ReturnNexus;
    InMessage->SaveNexus       = OutMessage->SaveNexus;
    InMessage->Priority        = OutMessage->Priority;

    InMessage->DeviceIDofLMGroup  = OutMessage->DeviceIDofLMGroup;
    InMessage->TrxID              = OutMessage->TrxID;

    return status;
}

INT CheckInhibitedState(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDevice *Device)
{
    INT      status = NORMAL;


    if(Device == NULL) /* Non-existant or inhibited device so do as needed with message */
    {
        status = BADCCU;
    }
    else if(Port->isInhibited())
    {
        status = PORTINHIBITED;

        switch(Device->getType())
        {
        case TYPE_CCU711:
            QueueFlush (Device);

            CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)Device->getTrxInfo();

            if(p711Info != NULL)
            {
                if(OutMessage->Command == CMND_LGRPQ)
                {
                    InMessage->EventCode = PORTINHIBITED;
                    p711Info->ClearStatus(INLGRPQ);
                }

                p711Info->reduceEntsConts(OutMessage->EventCode & RCONT);
            }
        }
    }
    else if(Device->isInhibited())
    {
        status =  REMOTEINHIBITED;

        switch(Device->getType())
        {
        case TYPE_CCU711:
            {
                CtiTransmitter711Info *p711Info = (CtiTransmitter711Info *)Device->getTrxInfo();

                if(p711Info != NULL)
                {
                    // QueueFlush (OutMessage->DeviceID, Port->getPortID(), OutMessage->Remote);
                    QueueFlush (Device);

                    if(OutMessage->Command == CMND_LGRPQ)
                    {
                        InMessage->EventCode = REMOTEINHIBITED;
                        p711Info->ClearStatus(INLGRPQ);
                    }

                    p711Info->reduceEntsConts(OutMessage->EventCode & RCONT);
                }

                break;
            }
        }
    }

    if(status != NORMAL)
    {
        /* if this is a dial up port make sure that we hang up if needed */
        Port->disconnect(Device, TraceFlag);
    }

    return status;
}

/*-------------------------------------------------------------------*
 * This function checks if the port is dialup and makes a call to
 * connect if so.  The call to connect checks if we are already
 * connected to the correct phone number/crc and keeps the connection
 * otherwise it makes the correct connection.
 *-------------------------------------------------------------------*/
INT EstablishConnection(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDevice *Device)
{
    INT status = NORMAL;

    status = Port->connectToDevice(Device, TraceFlag);

    if(status != NORMAL)
    {
        /* Must call CheckAndRetry to make the re-queue happen if needed */
        status = CheckAndRetryMessage(status, Port, InMessage, OutMessage, Device);     // This call may free OutMessages
    }
    else if( !Port->connectedTo(Device->getUniqueIdentifier()) ) /* Check if we made the connect OK */
    {
        /* Must call CheckAndRetry to make the re-queue happen if needed */
        // This call may free OutMessages
        status = CheckAndRetryMessage(status, Port, InMessage, OutMessage, Device);
    }

    return status;
}

INT DevicePreprocessing(CtiPortSPtr Port, OUTMESS *&OutMessage, CtiDevice *Device)
{
    INT status = NORMAL;
    struct timeb   TimeB;
    ULONG          QueueCount;

    if( Port->isDialup() )
    {
        if(((CtiDeviceRemote *)Device)->isDialup())     // Make sure the dialup pointer is NOT null!
        {
            //  init the port to the device's baud rate
            Port->setBaudRate(((CtiDeviceRemote *)Device)->getDialup()->getBaudRate());
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** WARNING **** " << Device->getName() << " is on a dialup port, but has no devicedialupsettings entry" << endl;

            status = BADPARAM;
        }
    }

    if(status == NORMAL)
    {
        switch(Device->getType())
        {
        case TYPE_CCU711:
            {
                CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)Device->getTrxInfo();

                /* check if we need to load the time into a time sync */
                if(OutMessage->EventCode & TSYNC)
                {
                    if(OutMessage->EventCode & BWORD)
                        LoadBTimeMessage (OutMessage);
                    else
                        LoadXTimeMessage (OutMessage->Buffer.OutMessage);
                }

                /* Broadcasts do not need CCU preprocessing */
                if(Device->getAddress() == CCUGLOBAL)
                    break;

                pInfo->reduceEntsConts(OutMessage->EventCode & RCONT);

                /* Check if we are in an RCONT condition */
                if(pInfo->GetStatus(INRCONT))
                {
                    if(OutMessage->EventCode & RCONT)
                    {
                        /* Check out what the message is... if it is not RCOLQ we need to set lengths */
                        if(OutMessage->Command != CMND_RCOLQ)
                        {
                            OutMessage->InLength = pInfo->RContInLength;
                        }
                    }
                }
                else
                {
                    /* Check if we ended up with an unneeded message */
                    if(OutMessage->Command == CMND_RCONT)
                    {
                        /* Yup */
                        delete OutMessage;
                        OutMessage = NULL;
                        return NOTNORMAL;
                    }
                }

                break;
            }
        case TYPE_LCU415LG:
        case TYPE_LCU415ER:
        case TYPE_LCU415:
        case TYPE_LCUT3026:
            {
                if(LCUPreSend (OutMessage, Device))  // Requeued if non-zero returned
                {
                    return RETRY_SUBMITTED;
                }

                break;
            }
        case TYPE_ILEXRTU:
            {
                if(OutMessage->EventCode & TSYNC)
                    LoadILEXTimeMessage (OutMessage->Buffer.OutMessage + PREIDLEN, 0);

                break;
            }
        case TYPE_WELCORTU:
        case TYPE_VTU:
            {
                if(OutMessage->EventCode & TSYNC)
                {
                    LoadWelcoTimeMessage (OutMessage->Buffer.OutMessage + PREIDLEN, 0);
                }

                break;
            }
        case TYPE_SES92RTU:
            {
                if(OutMessage->EventCode & TSYNC)
                    LoadSES92TimeMessage (OutMessage->Buffer.OutMessage + PREIDLEN, 0);

                break;
            }
        case TYPE_CCU700:
        case TYPE_CCU710:
            {
                /* check if we need to load the time into a time sync */
                if(OutMessage->EventCode & TSYNC)
                    LoadBTimeMessage (OutMessage);

                break;
            }
        case TYPE_TCU5000:
            break;

        case TYPE_TCU5500:
            {
                if((OutMessage->EventCode & VERSACOM) && VCUWait)
                {
                    CtiTransmitterInfo *pInfo = (CtiTransmitterInfo *)Device->getTrxInfo();

                    /* Check see if we need to wait for a message to complete */
                    UCTFTime (&TimeB);
                    if(TimeB.time <= (LONG)pInfo->NextCommandTime)
                    {
                        /* while queue is empty just sit on it otherwise put it back */
                        do
                        {
                            QueryQueue (Port->getPortQueueHandle(), &QueueCount);
                            if(QueueCount)
                            {
                                /* Write him back out to the queue */
                                if(OutMessage->Priority) OutMessage->Priority--;

                                if(Port->writeQueue(OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority, PortThread))
                                {
                                    printf ("\nError Replacing entry onto Queue\n");
                                }
                                CTISleep (50L);
                                break;
                            }
                            else
                            {
                                CTISleep (100L);
                                UCTFTime (&TimeB);
                            }
                        } while(TimeB.time <= (LONG)pInfo->NextCommandTime);

                        if(QueueCount) status = !NORMAL;
                    }
                }
                break;
            }
        default:
            {
                break;
            }
        }
    }

    return status;
}

INT CommunicateDevice(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDevice *Device)
{
    INT            status = NORMAL;
    ULONG          reject_status, ReadLength;
    struct timeb   TimeB;
    CtiRoute       *VTURouteRecord;

    BYTE           scratchBuffer[300];
    BYTE           *dataBuffer = NULL;
    CtiXfer        trx;
    LONG           oldid = 0L;

    RWTPtrSlist< CtiMessage > traceList;

    /* Load the post remote delay */
    if(Port->isDialup())
    {
        Port->setDelay(POST_REMOTE_DELAY, 0);
    }
    else
    {
        Port->setDelay(POST_REMOTE_DELAY, Device->getPostDelay());
    }

    trx.setTraceMask(TraceFlag, TraceErrorsOnly, TracePort == Port->getPortID(), TraceRemote);

    switch(Port->getProtocol())
    {
    case ProtocolWrapNone:
        {
            /* check if this is a MasterComm or ILEX device */
            switch(Device->getType())
            {
            case TYPE_ILEXRTU:
            case TYPE_SES92RTU:
            case TYPE_DAVIS:
            case TYPE_COOP4C:
            case TYPE_COOPCL4C:
            case TYPE_COOPCL5A:
                {
                    trx.setOutBuffer(OutMessage->Buffer.OutMessage + PREIDLEN);
                    trx.setOutCount(OutMessage->OutLength);
                    status = Port->outMess(trx, Device, traceList);
                    break;
                }

            case TYPE_LCU415:
            case TYPE_LCU415LG:
            case TYPE_LCU415ER:
            case TYPE_LCUT3026:
            case TYPE_TCU5000:
            case TYPE_TCU5500:
                {
                    PreMaster (OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT) OutMessage->OutLength);

                    trx.setOutBuffer(OutMessage->Buffer.OutMessage + PREIDLEN);
                    trx.setOutCount(OutMessage->OutLength + 2);
                    status = Port->outMess(trx, Device, traceList);
                    break;
                }

            case TYPE_ION7700:
            case TYPECBC6510:
            case TYPE_DNPRTU:
                {
                    CtiProtocolBase *protocol;

                    if( (protocol = Device->getProtocol()) != NULL )
                    {
                        protocol->recvCommRequest(OutMessage);

                        while( !protocol->isTransactionComplete() )
                        {
                            protocol->generate(trx);

                            status = Port->outInMess(trx, Device, traceList);

                            protocol->decode(trx, status);

                            // Prepare for tracing
                            if(trx.doTrace(status))
                            {
                                Port->traceXfer(trx, traceList, Device, status);
                            }

                            DisplayTraceList(Port, traceList, true);
                        }

                        protocol->sendCommResult(InMessage);
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << RWTime() << " **** Device \'" << Device->getName() << "\' has no protocol object, aborting communication **** " << endl;
                        }
                    }

                    break;
                }

            case TYPE_KV2:
               {
                  BYTE  inBuffer[512];
                  BYTE  outBuffer[300];
                  ULONG bytesReceived=0;

                  CtiDeviceKV2 *kv2dev    = ( CtiDeviceKV2 *)Device;
                  CtiProtocolANSI &ansi   = kv2dev->getProtocol();

                  //allocate some space
                  trx.setInBuffer( inBuffer );
                  trx.setOutBuffer( outBuffer );
                  trx.setInCountActual( &bytesReceived );

                  //unwind the message we made in scanner
                  if( ansi.recvOutbound( OutMessage ) != 0 )
                  {
                     while( !ansi.isTransactionComplete() )
                     {
                        //jump in, check for login, build packets, send messages, etc...
                        ansi.generate( trx );

                        status = Port->outInMess( trx, Device, traceList );

                        if( status != NORMAL )
                        {
                           CtiLockGuard<CtiLogger> doubt_guard(dout);
                           dout << RWTime() << " KV2 loop is A-B-N-O-R-M-A-L " << endl;
                        }

                        ansi.decode( trx, status );

                        // Prepare for tracing
                        if( trx.doTrace( status ) )
                        {
                           Port->traceXfer( trx, traceList, Device, status );
                        }

                        DisplayTraceList( Port, traceList, true );
                     }

                     {
                         CtiLockGuard<CtiLogger> doubt_guard(dout);
                         dout << RWTime() << " KV2 loop exited ******************************************************************" << endl;
                     }
                  }

                  ansi.sendInbound( InMessage );
                  ansi.setTransactionComplete(false);
                  //ansi.reinitialize();
                  break;
               }

            case TYPE_SIXNET:
                {
                    CtiDeviceIED         *IED= (CtiDeviceIED*)Device;
                    // Copy the request into the InMessage side....
                    memcpy(&InMessage->Buffer.DUPSt.DUPRep.ReqSt, &OutMessage->Buffer.DUPReq, sizeof(DIALUPREQUEST));

                    IED->allocateDataBins(OutMessage);
                    IED->setLogOnNeeded(TRUE);
                    IED->setInitialState(0);

                    if( (status = InitializeHandshake (Port, IED, traceList)) == NORMAL )
                    {
                        int dcstat;

                        status = PerformRequestedCmd (Port, IED, InMessage, OutMessage, traceList);
                        dcstat = TerminateHandshake (Port, IED, traceList);

                        if(status == NORMAL)
                            status = dcstat;
                    }

                    IED->freeDataBins();

                    // 071901 CGP. For now, prevent the original outmessage from returning to the requestor.

                    // OutMessage->EventCode &= ~RESULT;
                    InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[0] = CtiDeviceIED::CmdScanData;

                    break;
                }
            case TYPE_WCTP:
                {
                    try
                    {
                        CtiDeviceIED        *IED = (CtiDeviceIED*)Device;

                        IED->setLogOnNeeded(FALSE);
                        IED->setInitialState(0);
                        IED->allocateDataBins(OutMessage);

                        status = PerformRequestedCmd(Port, IED, InMessage, OutMessage, traceList);

                        IED->freeDataBins();

                        Port->close(0);         // 06062002 CGP  Make it reopen when needed.
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    break;
                }
            case TYPE_TAPTERM:
                {
                    LONG portConnectedUID = -1;
                    CtiDeviceIED *IED= (CtiDeviceIED*)Device;

                    verifyConnectedDevice(Port, Device, oldid, portConnectedUID);

                    IED->allocateDataBins(OutMessage);
                    IED->setInitialState(oldid);

                    if( (status = InitializeHandshake (Port, IED, traceList)) == NORMAL )
                    {
                        Port->setConnectedDevice( IED->getID() );

                        status = PerformRequestedCmd (Port, IED, NULL, NULL, traceList);

                        if( status != NORMAL)
                        {
                            IED->setLogOnNeeded(TRUE);      // We did not come through it cleanly, let's kill this connection.
                        }
                        else
                        {
                            if( SearchQueue(Port->getPortQueueHandle(), (void*)portConnectedUID, areAnyOutMessagesForUniqueID) == 0 )
                            {
                                IED->setLogOnNeeded(TRUE);      // We have zero queue entries!
                            }
                            else
                            {
                                // We have queue entries!  This is what gets us through the terminate routine without a hangup!
                                IED->setLogOnNeeded(FALSE);
                            }
                        }

                        INT dcstat = TerminateHandshake (Port, IED, traceList);

                        if(status == NORMAL)
                            status = dcstat;
                    }

                    IED->freeDataBins();

                    break;
                }
            case TYPE_ALPHA_A1:
            case TYPE_ALPHA_PPLUS:
            case TYPE_DR87:
            case TYPE_LGS4:
                {
                    // Copy the request into the InMessage side....
                    memcpy(&InMessage->Buffer.DUPSt.DUPRep.ReqSt, &OutMessage->Buffer.DUPReq, sizeof(DIALUPREQUEST));

                    // initialize the ied
                    CtiDeviceIED *IED= (CtiDeviceIED*)Device;

                    /***********************
                    *
                    *  allocating memory for the many different data internal structures the IED
                    *  uses to gather data
                    *
                    ************************
                    */

                    IED->allocateDataBins(OutMessage);
                    status = InitializeHandshake (Port,IED, traceList);

                    if(!status)
                    {
                        // this will do the initial command requested
                        if(!(status=PerformRequestedCmd (Port, IED, InMessage, OutMessage, traceList)))
                        {
                            /*********************************************
                            * Use the byte 2 of the command message to keep the
                            * final state of communications with the device
                            * to send to scanner
                            **********************************************
                            */
                            InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1] = IED->getCurrentState();

                            // check if there is load profile to do
                            if(IED->getCurrentCommand() == CtiDeviceIED::CmdLoadProfileTransition)
                            {
                                // set to load profile request
                                IED->setCurrentCommand(CtiDeviceIED::CmdLoadProfileData);

                                // note, current command must be reset to scan data before returning
                                // or the decode response routine will not work correctly
                                PerformRequestedCmd ( Port, IED, InMessage, OutMessage , traceList);

                                // reset to scan data once completed
                                IED->setCurrentCommand( CtiDeviceIED::CmdScanData );
                            }

                            // only do this if we were doing a scan data
                            if(IED->getCurrentCommand() == CtiDeviceIED::CmdScanData)
                            {
                                // will need to move these back
                                IED->reformatDataBuffer (InMessage->Buffer.DUPSt.DUPRep.Message,InMessage->InLength);
                            }
                        }
                        else
                        {
                            InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1] = IED->getCurrentState();
                        }
                    }
                    else
                    {
                        InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1] = IED->getCurrentState();
                    }

                    // free the memory we used
                    IED->freeDataBins();

                    break;
                }
            case TYPE_VECTRON:
            case TYPE_FULCRUM:
            case TYPE_QUANTUM:
                {
                    // Copy the request into the InMessage side....
                    memcpy(&InMessage->Buffer.DUPSt.DUPRep.ReqSt, &OutMessage->Buffer.DUPReq, sizeof(DIALUPREQUEST));

                    // initialize the ied
                    CtiDeviceIED *IED= (CtiDeviceIED*)Device;

                    /***********************
                    *
                    *  allocating memory for the many different data internal structures the IED
                    *  uses to gather data
                    *
                    ************************
                    */

                    IED->allocateDataBins(OutMessage);

                    /************************
                    *
                    *  initializes handshake and decides if there is a master/slave relationship
                    *  to deal with
                    *
                    *************************
                    */

                    status = LogonToDevice (Port,IED,InMessage,OutMessage, traceList);

                    if(!status)
                    {
                        // this will do the initial command requested
                        if(!(status=PerformRequestedCmd (Port, IED, InMessage, OutMessage, traceList)))
                        {
                            /*********************************************
                            * Use the byte 2 of the command message to keep the
                            * final state of communications with the device
                            * to send to scanner
                            **********************************************
                            */
                            InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1] = IED->getCurrentState();

                            // check if there is load profile to do
                            if(IED->getCurrentCommand() == CtiDeviceIED::CmdLoadProfileTransition)
                            {
                                // set to load profile request
                                IED->setCurrentCommand(CtiDeviceIED::CmdLoadProfileData);

                                // note, current command must be reset to scan data before returning
                                // or the decode response routine will not work correctly
                                PerformRequestedCmd ( Port, IED, InMessage, OutMessage , traceList);

                                // reset to scan data once completed
                                IED->setCurrentCommand( CtiDeviceIED::CmdScanData );
                            }

                            // only do this if we were doing a scan data
                            if(IED->getCurrentCommand() == CtiDeviceIED::CmdScanData)
                            {
                                // will need to move these back
                                IED->reformatDataBuffer (InMessage->Buffer.DUPSt.DUPRep.Message,InMessage->InLength);
                            }
                        }
                        else
                        {
                            InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1] = IED->getCurrentState();
                        }
                    }
                    else
                    {
                        InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1] = IED->getCurrentState();
                    }

                    // free the memory we used
                    IED->freeDataBins();

                    break;
                }
            case TYPE_WELCORTU:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Invalid Port Protocol for Welco device **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    status = BADPORT;
                    break;
                }
            default:
                {
                    /* output the message to the remote */
                    trx.setOutBuffer(OutMessage->Buffer.OutMessage + PREIDLEN);
                    trx.setOutCount(OutMessage->OutLength - 3);
                    status = Port->outMess(trx, Device, traceList);
                }
            }

            /* get the time into the return message */
            UCTFTime (&TimeB);
            InMessage->Time = TimeB.time;
            InMessage->MilliTime = TimeB.millitm;

            if(TimeB.dstflag) InMessage->MilliTime |= DSTACTIVE;

            /* !status is a successful return... This is a "post" successful send switch */
            if(!status)
            {
                switch(Device->getType())
                {
                case TYPE_CCU700:
                case TYPE_CCU710:
                case TYPE_DAVIS:
                case TYPE_COOP4C:
                case TYPE_COOPCL4C:
                case TYPE_COOPCL5A:
                    {
                        /* get the returned message from the remote */
                        trx.setInBuffer(InMessage->Buffer.InMessage);
                        trx.setInCountExpected(OutMessage->InLength);
                        trx.setInCountActual(&InMessage->InLength);
                        trx.setInTimeout(OutMessage->TimeOut);
                        trx.setMessageStart();                          // This is the first "in" of this message
                        trx.setMessageComplete();                       // This is the last "in" of this message

                        status = Port->inMess(trx, Device, traceList);

                        // Prepare for tracing
                        if(trx.doTrace(status))
                        {
                            Port->traceXfer(trx, traceList, Device, status);
                        }

                        break;
                    }
                case TYPE_ILEXRTU:

                    /* Add some code */
                    break;

                case TYPE_LCU415:
                case TYPE_LCU415LG:
                case TYPE_LCU415ER:
                case TYPE_LCUT3026:
                case TYPE_TCU5000:
                case TYPE_TCU5500:
                    {
                        InMessage->InLength = 0;
                        ReadLength = 0;


                        /* get the first 4 chars of the message */
                        trx.setInBuffer(InMessage->Buffer.InMessage);
                        trx.setInCountExpected( 4 );
                        trx.setInCountActual(&InMessage->InLength);
                        trx.setInTimeout(OutMessage->TimeOut);
                        trx.setMessageStart();                          // This is the first "in" of this message
                        trx.setMessageComplete(0);                      // This is _NOT_ the last "in" of this message

                        status = Port->inMess(trx, Device, traceList);

                        if(!status)
                        {
                            /* check out the message... How much follows */
                            status = PostMaster (InMessage->Buffer.InMessage, Device->getAddress(), &ReadLength);
                        }

                        if(!status)
                        {
                            /* get the rest of the message */
                            trx.setInBuffer(InMessage->Buffer.InMessage + 4);
                            trx.setInCountExpected( ReadLength + 2 );
                            trx.setInCountActual(&ReadLength);
                            trx.setInTimeout(OutMessage->TimeOut);
                            trx.setMessageStart(0);                         // This is _NOT_ the first "in" of this message
                            trx.setMessageComplete();                       // This is the last "in" of this message

                            status = Port->inMess(trx, Device, traceList);
                        }

                        InMessage->InLength += ReadLength;

                        if(!status)
                        {
                            /* Check crc of message */
                            status = MasterReply (InMessage->Buffer.InMessage, (USHORT)InMessage->InLength);
                        }

                        // Prepare for tracing
                        trx.setInBuffer(InMessage->Buffer.InMessage);
                        trx.setInCountActual(&InMessage->InLength);
                        trx.setTraceMask(TraceFlag, TraceErrorsOnly, TracePort == Port->getPortID(), TraceRemote);

                        if(trx.doTrace(status))
                        {
                            Port->traceXfer(trx, traceList, Device, status);
                        }

                        if(!status)
                        {
                            InMessage->InLength = ReadLength - 2;
                        }

                        break;
                    }

                case TYPECBC6510:
                case TYPE_DNPRTU:
                case TYPE_ION7700:
                case TYPE_SIXNET:
                case TYPE_TAPTERM:
                case TYPE_WCTP:
                case TYPE_ALPHA_PPLUS:
                case TYPE_ALPHA_A1:
                case TYPE_FULCRUM:
                case TYPE_VECTRON:
                case TYPE_QUANTUM:
                case TYPE_DR87:
                case TYPE_LGS4:
                case TYPE_KV2:
                default:
                    {
                        /*  These guys are handled in a special way...  */
                        /*    they do all of their communications in the "outbound" block - typically in a loop, handling out/in pairs until they're complete.  */
                        break;
                    }
                }
            }
            else
            {
                switch(Device->getType())
                {
                // none of these use the transfer struct defined in this function so it blows the thread
                case TYPE_SIXNET:
                case TYPE_TAPTERM:
                case TYPE_WCTP:
                case TYPE_ALPHA_PPLUS:
                case TYPE_ALPHA_A1:
                case TYPE_FULCRUM:
                case TYPE_VECTRON:
                case TYPE_QUANTUM:
                case TYPE_DR87:
                case TYPE_LGS4:
                case TYPE_KV2:
                    break;
                default:
                    // There was an outbound error, the Xfer was not traced...
                    if(trx.doTrace(status))
                    {
                        Port->traceXfer(trx, traceList, Device, status);
                    }
                }
            }

            break;  /* No Wrap */
        }
    case ProtocolWrapIDLC:
        {
            /* Check for broadcast type message */
            if(OutMessage->Remote == CCUGLOBAL)
            {
                /* form the CCU IDLC preamble */
                PreIDLC (OutMessage->Buffer.OutMessage, (USHORT)OutMessage->OutLength, CCUGLOBAL, 0, 1, 1, OutMessage->Source, OutMessage->Destination, OutMessage->Command);
            }
            else if(OutMessage->Remote == RTUGLOBAL)
            {
                /* form the RTU IDLC preamble */
                PreUnSequenced (OutMessage->Buffer.OutMessage, (USHORT)OutMessage->OutLength, OutMessage->Port, RTUGLOBAL, Device);
            }
            else
            {
                switch(Device->getType())
                {
                case TYPE_ILEXRTU:
                case TYPE_WELCORTU:
                case TYPE_VTU:
                case TYPE_SES92RTU:
                case TYPE_LCU415:
                case TYPE_LCU415LG:
                case TYPE_LCU415ER:
                case TYPE_LCUT3026:
                case TYPE_TCU5000:
                case TYPE_TCU5500:
                    {
                        PreUnSequenced (OutMessage->Buffer.OutMessage, (USHORT)OutMessage->OutLength,  Device->getPortID(),Device->getAddress(), Device);
                        break;
                    }
                case TYPE_CCU700:
                case TYPE_CCU710:
                case TYPE_COOP4C:
                case TYPE_COOPCL4C:
                case TYPE_COOPLTC4C:
                case TYPE_COOPCL5A:
                case TYPE_JEM1:
                case TYPE_JEM2:
                case TYPE_JEM3:
                case TYPE_DAVIS:
                case TYPECBC6510:
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << "**** FIX FIX FIX FIX FIX THIS **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        // PreVTU (OutMessage->Buffer.OutMessage, (USHORT)(OutMessage->OutLength - 3), (USHORT)Device->getAddress(), (USHORT)(VTURouteRecord->getBus()), (USHORT)OutMessage->InLength, (USHORT)(OutMessage->TimeOut));
                        break;
                    }
                case TYPE_CCU711:
                default:
                    {
                        CtiTransmitterInfo *pInfo = (CtiTransmitterInfo *)Device->getTrxInfo();
                        PreIDLC (OutMessage->Buffer.OutMessage, (USHORT)OutMessage->OutLength, OutMessage->Remote, pInfo->RemoteSequence.Reply, pInfo->RemoteSequence.Request, 1, OutMessage->Source, OutMessage->Destination, OutMessage->Command);
                        break;
                    }
                }
            }

            /* calculate the crc and output the message */
            switch(Device->getType())
            {
            case TYPE_ILEXRTU:
            case TYPE_WELCORTU:
            case TYPE_VTU:
            case TYPE_SES92RTU:
            case TYPE_LCU415:
            case TYPE_LCU415LG:
            case TYPE_LCU415ER:
            case TYPE_LCUT3026:
            case TYPE_TCU5000:
            case TYPE_TCU5500:
            case TYPE_CCU700:
            case TYPE_CCU710:
            case TYPE_COOP4C:
            case TYPE_COOPCL4C:
            case TYPE_COOPLTC4C:
            case TYPE_COOPCL5A:
            case TYPE_JEM1:
            case TYPE_JEM2:
            case TYPE_JEM3:
            case TYPE_DAVIS:
                {
                    PostIDLC (OutMessage->Buffer.OutMessage, (USHORT)(OutMessage->OutLength + PREIDL + 1));

                    trx.setOutBuffer(OutMessage->Buffer.OutMessage);
                    trx.setOutCount(OutMessage->OutLength + PREIDL + 3);

                    status = Port->outMess(trx, Device, traceList);

                    break;
                }
            default:
                {
                    PostIDLC (OutMessage->Buffer.OutMessage, (USHORT)(OutMessage->OutLength + PREIDL - 2));

                    trx.setOutBuffer(OutMessage->Buffer.OutMessage);
                    trx.setOutCount(OutMessage->OutLength + PREIDL);

                    status = Port->outMess(trx, Device, traceList);

                    if(PorterDebugLevel & PORTER_DEBUG_CCUMESSAGES)
                    {
                        CtiProtocol711 ccu711;
                        ccu711.setMasterRequest( OutMessage->Buffer.OutMessage );
                        ccu711.describeMasterRequest();
                    }

                    break;
                }
            }

            /* get the time into the return message */
            UCTFTime (&TimeB);
            InMessage->Time = TimeB.time;
            InMessage->MilliTime = TimeB.millitm;
            if(TimeB.dstflag)
            {
                InMessage->MilliTime |= DSTACTIVE;
            }

            if(!status)
            {
                if(OutMessage->Remote == CCUGLOBAL)
                {
                    if(OutMessage->EventCode & DTRAN)
                    {
                        /* Calculate the time the command should take */
                        if(OutMessage->EventCode & BWORD)
                            CTISleep (3000L);
                        else
                            CTISleep(1000L);
                    }
                    status = NORMAL;
                }
                else if(OutMessage->Remote == RTUGLOBAL)
                {
                    status = NORMAL;
                }
                else
                {
                    switch(Device->getType())
                    {
                    case TYPE_ILEXRTU:
                    case TYPE_WELCORTU:
                    case TYPE_VTU:
                    case TYPE_SES92RTU:
                    case TYPE_LCU415:
                    case TYPE_LCU415LG:
                    case TYPE_LCU415ER:
                    case TYPE_LCUT3026:
                    case TYPE_TCU5000:
                    case TYPE_TCU5500:
                        {
                            InMessage->InLength = 0;
                            ReadLength = 0;

                            /* get the first 7 chars of the message */

                            trx.setInBuffer( InMessage->IDLCStat + 11 );
                            trx.setInCountExpected( PREIDLEN );
                            trx.setInCountActual( &(InMessage->InLength) );
                            trx.setInTimeout(OutMessage->TimeOut);
                            trx.setMessageStart();                          // This is the first "in" of this message
                            trx.setMessageComplete(0);                      // This is _NOT_ the last "in" of this message

                            status = Port->inMess(trx, Device, traceList);

                            if(!status)
                            {
                                /* check out the message... How much follows */
                                status = RTUReplyHeader (Device->getType(), Device->getAddress(), InMessage->IDLCStat + 11, &ReadLength);

                                if(!status && ReadLength)
                                {
                                    /* get the rest of the message */
                                    trx.setInBuffer( InMessage->Buffer.InMessage );
                                    trx.setInCountExpected( ReadLength );
                                    trx.setInCountActual( &ReadLength );
                                    trx.setInTimeout(OutMessage->TimeOut);
                                    trx.setMessageStart(0);                         // This is _NOT_ the first "in" of this message
                                    trx.setMessageComplete();                       // This is the last "in" of this message

                                    status = Port->inMess(trx, Device, traceList);
                                }
                            }

                            InMessage->InLength += ReadLength;

                            if(!status)
                            {
                                /* Check crc of message */
                                status = RTUReply (InMessage->IDLCStat + 11, (USHORT)InMessage->InLength);
                            }

                            if(trx.doTrace(status))
                            {
                                trx.setInBuffer(InMessage->IDLCStat + 11);
                                trx.setInCountActual(&InMessage->InLength);
                                Port->traceXfer(trx, traceList, Device, status);
                            }

                            break;
                        }
                    case TYPE_CCU700:
                    case TYPE_CCU710:
                    case TYPE_COOP4C:
                    case TYPE_COOPCL4C:
                    case TYPE_COOPLTC4C:
                    case TYPE_COOPCL5A:
                    case TYPE_JEM1:
                    case TYPE_JEM2:
                    case TYPE_JEM3:
                    case TYPE_DAVIS:
                        {
                            InMessage->InLength = 0;
                            ReadLength = 0;

                            trx.setInBuffer( InMessage->IDLCStat + 11 );
                            trx.setInCountExpected( PREIDLEN );
                            trx.setInCountActual( &(InMessage->InLength) );
                            trx.setInTimeout(OutMessage->TimeOut);
                            trx.setMessageStart();                          // This is the first "in" of this message
                            trx.setMessageComplete(0);                      // This is _NOT_ the last "in" of this message

                            status = Port->inMess(trx, Device, traceList);

                            if(!status)
                            {
                                /* check out the message... How much follows */
                                status = RTUReplyHeader (Device->getType(), Device->getAddress(), InMessage->IDLCStat + 11, &ReadLength);

                                if(!status && ReadLength)
                                {
                                    /* get the rest of the message */
                                    trx.setInBuffer( InMessage->Buffer.InMessage );
                                    trx.setInCountExpected( ReadLength );
                                    trx.setInCountActual( &ReadLength );
                                    trx.setInTimeout(OutMessage->TimeOut);
                                    trx.setMessageStart(0);                          // This is _NOT_ the first "in" of this message
                                    trx.setMessageComplete();                        // This is the last "in" of this message

                                    status = Port->inMess(trx, Device, traceList);
                                }
                            }
                            InMessage->InLength += ReadLength;

                            if(!status)
                            {
                                /* Check crc of message */
                                status = RTUReply (InMessage->IDLCStat + 11, (USHORT)InMessage->InLength);
                            }

                            if(trx.doTrace(status))
                            {
                                trx.setInBuffer(InMessage->IDLCStat + 11);
                                trx.setInCountActual(&InMessage->InLength);
                                Port->traceXfer(trx, traceList, Device, status);
                            }


                            if(!status)
                            {
                                status = InMessage->Buffer.InMessage[2];
                                InMessage->InLength = InMessage->Buffer.InMessage[1];
                                if(InMessage->InLength)
                                {
                                    memmove (InMessage->Buffer.InMessage, InMessage->Buffer.InMessage + 3, InMessage->InLength);
                                }
                            }

                            break;
                        }
                    default:
                        {
                            CtiTransmitterInfo *pInfo = (CtiTransmitterInfo *)Device->getTrxInfo();

                            /* get the first 5 bytes in the return message */
                            trx.setInBuffer( InMessage->IDLCStat );
                            trx.setInCountExpected( 5 );
                            trx.setInCountActual( &InMessage->InLength );
                            trx.setInTimeout(OutMessage->TimeOut);
                            trx.setMessageStart();                           // This is the first "in" of this message
                            trx.setMessageComplete(0);                       // This is NOT the last "in" of this message
                            trx.setCRCFlag(0);

                            status = Port->inMess(trx, Device, traceList);

                            if(!status)
                            {
                                /* Oh wow, I got me five bytes of datum! */
                                if(InMessage->IDLCStat[2] & 0x01)    // Supervisory frame. Emetcon S-Spec Section 4.5
                                {
                                    /* Ack patooy What to do here now ya don't ya' know */
                                    switch(InMessage->IDLCStat[2] & 0x0f)
                                    {
                                    case REJ:
                                        {
                                            if((reject_status = IDLCRej(InMessage->IDLCStat, &pInfo->RemoteSequence.Request)) != NORMAL)
                                            {
                                                status = reject_status;
                                            }
                                            break;
                                        }
                                    default:
                                        {
                                            {
                                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                dout << RWTime() << " *** Supervisory (Inbound) Message 0x" << hex << InMessage->IDLCStat[2] << " from CCU: " << Device->getName() << endl;
                                            }

                                            if(trx.doTrace(status))
                                            {
                                                trx.setInBuffer(InMessage->IDLCStat);
                                                trx.setInCountActual(&InMessage->InLength);
                                                Port->traceXfer(trx, traceList, Device, status);
                                            }
                                        }
                                    }

                                    /*
                                     *  4/29/99 CGP There are others like Reset Acknowlege which may need to picked up here
                                     */
                                }
                                else
                                {
                                    ReadLength = 0;
                                    /* Go get the rest of the message */
                                    /* Got first byte following length.. Must add two for CRC */
                                    trx.setInBuffer( &InMessage->IDLCStat[5] );
                                    trx.setInCountExpected( InMessage->IDLCStat[3] + 1 );
                                    trx.setInCountActual( &ReadLength );
                                    trx.setInTimeout(OutMessage->TimeOut);
                                    trx.setMessageStart(FALSE);                           // This is the first "in" of this message
                                    trx.setMessageComplete(TRUE);                   // This is NOT the last "in" of this message

                                    status = Port->inMess(trx, Device, traceList);

                                    InMessage->InLength += ReadLength;

                                    if(!status)
                                    {
                                        /*
                                         *  This is the guy who does some rudimentary checking on the CCU message
                                         *  He will return REQACK in that case...
                                         */
                                        status = GenReply (InMessage->IDLCStat, InMessage->InLength, &pInfo->RemoteSequence.Request, &pInfo->RemoteSequence.Reply, Device->getAddress());
                                    }
                                }
                            }

                            if(status)
                            {
                                if(InMessage->InLength >= 5)
                                {
                                    if((reject_status = IDLCRej (InMessage->IDLCStat, &pInfo->RemoteSequence.Request)) != NORMAL)
                                    {
                                        status = reject_status;
                                    }
                                }
                            }

                            if(trx.doTrace(status))
                            {
                                trx.setInBuffer(InMessage->IDLCStat);
                                trx.setInCountActual(&InMessage->InLength);
                                Port->traceXfer(trx, traceList, Device, status);
                            }

                            if(!status)
                            {
                                InMessage->InLength -= 20;
                            }

                            if(PorterDebugLevel & PORTER_DEBUG_CCUMESSAGES)
                            {
                                CtiProtocol711 ccu711;
                                ccu711.setSlaveResponse( InMessage->IDLCStat );
                                ccu711.describeSlaveResponse();
                            }

                            break;
                        }
                    }
                }
            }
            else
            {
                // There was an outbound error, the Xfer was not traced...
                if(trx.doTrace(status))
                {
                    Port->traceXfer(trx, traceList, Device, status);
                }
            }


            /* Do not do a retry if we REQACK */
            if((status & ~DECODED) == REQACK)
            {
                OutMessage->Retry = 0;
            }

            break; /* IDLC */
        }
    case UCAWRAP:     /* Don't know about this one yet */
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    }

    DisplayTraceList(Port, traceList, true);

    return status;
}

INT NonWrapDecode(INMESS *InMessage, CtiDevice *Device)
{
    INT status = NORMAL;

    switch(Device->getType())
    {
    case TYPE_DAVIS:
        {
            if(InMessage->Buffer.InMessage[0] != 0x06 && InMessage->Buffer.InMessage[1] != 0x01)
            {
                status = FRAMEERR;
            }
            else
            {
                /* Check the CRC */
                if(MAKEUSHORT (InMessage->Buffer.InMessage[18],
                               InMessage->Buffer.InMessage[17]) != CrcCalc_C ((InMessage->Buffer.InMessage + 2), 15))
                {
                    status = BADCRC;
                }
            }

            break;
        }
    default:
        break;
    }

    return status;
}


INT CheckAndRetryMessage(INT CommResult, CtiPortSPtr Port, INMESS *InMessage, OUTMESS *&OutMessage, CtiDevice *Device)
{
    INT            status = CommResult;
    ULONG          j;
    ULONG          QueueCount;
    REMOTEPERF     RemotePerf;
    ERRSTRUCT      ErrStruct;
    bool           iscommfailed = (CommResult == NORMAL);      // Prime with the communication status

    if(Device->adjustCommCounts( iscommfailed, OutMessage->Retry > 0 ))
    {
        commFail(Device, (iscommfailed ? CLOSED : OPENED));
    }

    if(OutMessage->TargetID != OutMessage->DeviceID)
    {
        // In this case, we need to account for the fail on the target device too..
        CtiDevice *pTarget = DeviceManager.getEqual( OutMessage->TargetID );

        if(pTarget)
        {
            iscommfailed = (CommResult == NORMAL);

            if( pTarget->adjustCommCounts( iscommfailed, OutMessage->Retry > 0 ) )
            {
                commFail(pTarget, (iscommfailed ? CLOSED : OPENED));
            }
        }
    }

    if( (CommResult != NORMAL) || (  (Port->getProtocol() == IDLC) && (OutMessage->Remote == CCUGLOBAL || OutMessage->Remote == RTUGLOBAL) ) )
    {
        switch( Device->getType() )
        {
        case TYPE_LCU415:
        case TYPE_LCU415LG:
        case TYPE_LCU415ER:
        case TYPE_LCUT3026:
            {
                if(CommResult == NORMAL)
                {
                    break;
                }
                else if((status = LCUResultDecode(OutMessage, InMessage, Device, CommResult, false)) == RETRY_SUBMITTED)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << Device->getName() << " RETRY SUBMITTED " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    break; // **** DO NOT FALL THROUGH ****
                }
                else
                {
                    status = CommResult;
                }

                if(OutMessage->EventCode & RIPPLE)
                {
                    // Never requeue this stuff.  It is handled internally.

                    break;
                }
            }
        default:
            {
                if(OutMessage->Retry > 0)
                {
                    /* decrement the retry counter */
                    --OutMessage->Retry;

                    /* If this was a VTU message we need to slide things back over */
                    if(Port->getProtocol() == ProtocolWrapIDLC)
                    {
                        switch(Device->getType())
                        {
                        case TYPE_CCU700:
                        case TYPE_CCU710:
                        case TYPE_COOP4C:
                        case TYPE_COOPCL4C:
                        case TYPE_COOPLTC4C:
                        case TYPE_COOPCL5A:
                        case TYPE_JEM1:
                        case TYPE_JEM2:
                        case TYPE_JEM3:
                        case TYPE_DAVIS:
                            {
                                memmove (OutMessage->Buffer.OutMessage + 7, OutMessage->Buffer.OutMessage + 10, OutMessage->OutLength - 3);
                            }
                        }
                    }

                    /* Put it on the queue for this port */
                    if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
                    {
                        // VioWrtTTY("Error Writing Retry into Queue\n\r", 31, 0);
                        printf("Error Writing Retry into Queue\n");

                        delete OutMessage;
                        OutMessage = NULL;

                        status =  QUEUE_WRITE;
                    }
                    else
                    {
                        /* don't free the memory, it is back on the queue!!! */
                        /* Update the CCUInfo if neccessary */
                        if(OutMessage->Remote != 0)
                        {
                            switch(Device->getType())
                            {
                            case TYPE_CCU711:
                                {
                                    CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)Device->getTrxInfo();

                                    pInfo->PortQueueEnts++;

                                    if(OutMessage->EventCode & RCONT)
                                        pInfo->PortQueueConts++;
                                }
                            }
                        }

                        /* If this is an TCP/IP Transaction port, disconnect */
                        if(Port->getType() & TCPIPSERVER)
                        {
                            // TCPClose (NetCXPortRecord[OutMessage->Port]);
                            Port->close(TraceFlag);

                            /* Query how many are on the queue and if it is only one sleep for a bit */
                            for(j = 0; j < 4; j++)
                            {
                                /* Check the queue */
                                QueryQueue (Port->getPortQueueHandle(), &QueueCount);
                                if(QueueCount > 1)
                                {
                                    break;
                                }

                                CTISleep (250L);
                            }
                        }

                        status = RETRY_SUBMITTED;    // This makes us continue the loop!
                    }
                }
                else
                {
                    switch(Device->getType())
                    {
                    case TYPE_CCU711:                // Remove this queue entry.
                        {
                            CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)Device->getTrxInfo();

                            pInfo->reduceEntsConts(OutMessage->EventCode & RCONT);
                        }
                    }
                }

                break;
            }
        }
    }

    if(status == RETRY_SUBMITTED)
    {
        statisticsNewAttempt( OutMessage->Port, OutMessage->DeviceID, OutMessage->TargetID, CommResult );
    }

    return status;
}



INT DoProcessInMessage(INT CommResult, CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDevice *Device)
{
    extern void blitzNexusFromQueue(HCTIQUEUE q, CTINEXUS *&Nexus);
    extern void blitzNexusFromCCUQueue(CtiDevice *Device, CTINEXUS *&Nexus);

    INT            status = NORMAL;
    ULONG          j, QueueCount;
    struct timeb   TimeB;
    REMOTEPERF     RemotePerf;
    ERRSTRUCT      ErrStruct;

    InMessage->EventCode = (USHORT)CommResult;

    switch(Device->getType())
    {
    case TYPE_CCU711:
        {
            CtiTransmitter711Info *p711info = (CtiTransmitter711Info *)Device->getTrxInfo();

            if(OutMessage->Remote == CCUGLOBAL)
            {
                break;
            }

            InMessage->InLength = OutMessage->InLength;

            /* Clear the RCOLQ flag if neccessary */
            if(OutMessage->Command == CMND_RCOLQ)
            {
                p711info->ClearStatus (INRCOLQ);
            }

            /* Clear a LGRPQ flag if neccessary */
            if(OutMessage->Command == CMND_LGRPQ)
            {
                p711info->ClearStatus(INLGRPQ);
                if(!(QueryQueue (p711info->QueueHandle,&QueueCount)))
                {
                    if(QueueCount)
                    {
                        SetEvent(hPorterEvents[P_QUEUE_EVENT]);
                    }
                }
            }

            if(OutMessage->EventCode & RCONT)
            {
                status = CCUResponseDecode (InMessage, Device, OutMessage);
            }
            else
            {
                j = InMessage->InLength;
                InMessage->InLength = 0;
                status = CCUResponseDecode (InMessage, Device, OutMessage);
                InMessage->InLength = j;
            }

            if(status == BADSOCK)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                blitzNexusFromQueue( Port->getPortQueueHandle(), InMessage->ReturnNexus);
                blitzNexusFromCCUQueue( Device, InMessage->ReturnNexus);

            }

            /* Only break if this is not DTRAN */
            if(!(OutMessage->EventCode & DTRAN))
                break;

        }
    case TYPE_CCU700:
    case TYPE_CCU710:
        {
            if(CommResult)
            {
                InMessage->Buffer.DSt.Time     = InMessage->Time;
                InMessage->Buffer.DSt.DSTFlag  = InMessage->MilliTime & DSTACTIVE;
                break;
            }

            InMessage->InLength = OutMessage->InLength;

            /* The data here is a CCU 700 type result */
            if(OutMessage->EventCode & AWORD)
            {
                /* Make sure that we got back Acks */

            }
            else if(OutMessage->EventCode & BWORD)
            {
                /* find out if this is a read or a write */
                if(OutMessage->Buffer.BSt.IO & READ)
                {
                    DSTRUCT        DSt;

                    /* This is I so decode dword(s) for the result */
                    status = D_Words (InMessage->Buffer.InMessage + 3, (USHORT)((InMessage->InLength - 3) / (DWORDLEN + 1)),  OutMessage->Remote, &DSt);
                    DSt.Time = InMessage->Time;
                    DSt.DSTFlag = InMessage->MilliTime & DSTACTIVE;
                    InMessage->Buffer.DSt = DSt;
                }
                else
                {
                    /* This is a write so make sure we got back ACKS */
                }
            }

            break;
        }
    case TYPE_LCU415:
    case TYPE_LCU415LG:
    case TYPE_LCU415ER:
    case TYPE_LCUT3026:
        {
            LCUResultDecode(OutMessage, InMessage, Device, CommResult, true);
            break;
        }
    case TYPE_TCU5500:
        {
            if(OutMessage->EventCode & VERSACOM)
            {
                if(VCUWait)
                {
                    CtiTransmitterInfo *pInfo = (CtiTransmitterInfo *)Device->getTrxInfo();

                    /* The assumption (for now) is overlapping coverage of VCU's on the
                           same comm port (VCU's on another comm port better damn well not
                           overlap this one!!!!!!!) and that we have exclusive use of the VHF
                           channels (No paging or voice channels!!!).  Obviosly this is an
                           extremely simplistic view of the world but will do till we can do
                           otherwise... Queues and time tables etc.... */

                    /* Another gotcha... we assume that the VCU is doing something even
                       if this command fails... Life's a bitch but thats the way
                       its gotta be or things could fubar beyond belief... Then again??? */

                    /* get how long this command will take */
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    // VCUTime (OutMessage, &pInfo->NextCommandTime);

                    UCTFTime (&TimeB);

                    pInfo->NextCommandTime += TimeB.time;

#ifdef OLD_CRAP
                    for(j = 0; j <= MAXIDLC; j++)
                    {
                        if(CCUInfo[ThreadPortNumber][j] != NULL && CCUInfo[ThreadPortNumber][j]->Type == TYPE_TCU5500)
                        {
                            CCUInfo[ThreadPortNumber][j]->NextCommandTime = CCUInfo[ThreadPortNumber][Device->getAddress()]->NextCommandTime;
                        }
                    }
#else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "**** ADD CODE HERE **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
#endif
                }

                /* Lets check if this command needs to be queued again */
                if(!CommResult && (InMessage->Buffer.InMessage[4] & VCUOVERQUE) && !gIgnoreTCU5X00QueFull)
                {
                    /* we need to reque this one  */
                    /* Drop the priority an notch so we don't hog the channel */
                    if(OutMessage->Priority) OutMessage->Priority--;

                    status = RETRY_SUBMITTED;

                    /* Put it on the queue for this port */
                    if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "Error Requeing Command" << endl;
                    }
                }
            }
            break;
        }
    case TYPE_TCU5000:
        {
            if(OutMessage->EventCode & VERSACOM)
            {
                /* Lets check if this command needs to be queued again */
                if(!CommResult && (InMessage->Buffer.InMessage[4] & VCUOVERQUE) && !gIgnoreTCU5X00QueFull)
                {
                    if(PorterDebugLevel & PORTER_DEBUG_VERSACOM)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << Device->getName() << " queue full.  Will resubmit." << endl;
                    }

                    /* we need to reque this one  */
                    status = RETRY_SUBMITTED;

                    /* Put it on the queue for this port */
                    if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "Error Requeing Command" << endl;
                    }
                }
            }
            break;
        }
    case TYPE_WELCORTU:
        {
            /* Handle the Sequencing */
            if(!(CommResult))
            {
                CtiTransmitterInfo *pInfo = (CtiTransmitterInfo *)Device->getTrxInfo();
                pInfo->RemoteSequence.Reply = !pInfo->RemoteSequence.Reply;
            }

            break;
        }
    case TYPE_ILEXRTU:
    case TYPE_VTU:
    case TYPE_SES92RTU:
    default:
        {
            break;
        }
    }

    // Statistics processing.
    if(status == RETRY_SUBMITTED)
    {
        statisticsNewAttempt( InMessage->Port, InMessage->DeviceID, InMessage->TargetID, CommResult );
    }
    else
    {
        statisticsNewCompletion( InMessage->Port, InMessage->DeviceID, InMessage->TargetID, CommResult );
    }

    return status;
}

INT ReturnResultMessage(INT CommResult, INMESS *InMessage, OUTMESS *&OutMessage)
{
    INT         status = NORMAL;
    ULONG       BytesWritten;

    if(OutMessage->EventCode & RESULT)         /* If the OutMessage indicates it this routine responds to the client */
    {
        InMessage->EventCode = (USHORT)CommResult;

        if(CommResult != NORMAL)
        {
            status = SendError( OutMessage, CommResult, InMessage );
        }
        else
        {
            /* send message back to originating process */
            if(OutMessage->ReturnNexus != NULL)
            {
                if(OutMessage->ReturnNexus->CTINexusWrite(InMessage, sizeof (INMESS), &BytesWritten, 15L))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Error Writing to Return Nexus " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  DeviceID " << OutMessage->DeviceID << " TargetID " << OutMessage->TargetID << " " << OutMessage->Request.CommandStr << endl;
                    }
                    status = SOCKWRITE;
                }
            }
        }
    }

    return status;
}

INT UpdatePerformanceData(INT CommResult, CtiPortSPtr Port, CtiDevice *Device)
{
    INT            status = NORMAL;
    REMOTEPERF     RemotePerf;
    ERRSTRUCT      ErrStruct;

    if(Device->getAddress() != 0xffff)
    {
        RemotePerf.Port   = Device->getPortID();
        RemotePerf.Remote = Device->getAddress();
        RemotePerf.Error  = (USHORT)CommResult;
        RemotePerfUpdate (&RemotePerf, &ErrStruct);

        /* Log the error */
        ReportRemoteError (Device,&ErrStruct);
    }

    return status;
}

/*---------------------------------------------------------------------------*
 * This guy allows the same code to be called whether the OutMessage was
 * requeued, or wehter it sould be cleaned up by a SendError call.
 *
 * It should ALWAYS be used in a case where some errors may actually cause
 * a re-queue
 *---------------------------------------------------------------------------*/
INT RequeueReportError(INT status, OUTMESS *OutMessage)
{
    INT result = NORMAL;

    switch(status)
    {
    case RETRY_SUBMITTED:
        {
            break;  // Don't call this an error.
        }
    default:
        {
            SendError(OutMessage, status);   // This call frees the OUTMESS upon completion
            break;
        }
    }

    return result;
}


INT VTUPrep(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDevice *Device)
{
    INT status = NORMAL;

    if(
      Device->getType() != TYPE_TDMARKV &&
      Device->getType() != TYPE_TAPTERM &&
      Device->getType() != TYPE_WCTP &&
      Device->getType() != TYPE_ALPHA_PPLUS &&
      Device->getType() != TYPE_FULCRUM &&
      Device->getType() != TYPE_VECTRON &&
      Device->getType() != TYPE_QUANTUM &&
      Device->getType() != TYPE_LGS4 &&
      Device->getType() != TYPE_DR87 &&
      Device->getType() != TYPE_KV2 &&
      Device->getType() != TYPE_ALPHA_A1

      )
    {
        /* Check if this device is on a VTU and if so get it's record */

#if 0 // FIX FIX FIX change for routesIDs
        if(DeviceRecord.RouteName[2][0] != ' ')
        {
            /* This is a VTU! */
            memcpy (VTURouteRecord.RouteName, DeviceRecord.RouteName[2], STANDNAMLEN);
            if(!(RoutegetEqual (&VTURouteRecord)))
            {
                /* Go ahead and get the VTU Remote record */
                memcpy (Device.RemoteName, VTURouteRecord.RemoteName, STANDNAMLEN);
                if(RemotegetEqual (&Device))
                {
                    SendError (OutMessage, BADCCU);
                    continue;
                }
            }
            else
            {
                SendError (OutMessage, BADROUTE);
                continue;
            }
        }
#endif
    }

    return status;
}


INT ValidateDevice(CtiPortSPtr Port, CtiDevice *Device)
{
    INT status = NORMAL;

    if(Device->getAddress() != 0xffff)
    {
        if( Device->hasTrxInfo() && !Device->isInhibited() ) // Does this device type support TrxInfo?
        {
            CtiTransmitterInfo *pInfo = (CtiTransmitterInfo *)Device->getTrxInfo();

            /* Before we do anything else make damn sure we are not a protection violation */
            if(pInfo == NULL)
            {
                /* Go Ahead an start this one up */
                RemoteInitialize(Device, Port);
                RemoteReset(Device, Port);
            }
            else
            {
                if(pInfo->GetStatus(NEEDSRESET))
                {
                    RemoteReset(Device, Port);
                }
            }
        }
    }

    return status;
}

INT InitializeHandshake (CtiPortSPtr aPortRecord, CtiDeviceIED *aIEDDevice, RWTPtrSlist< CtiMessage > &traceList)
{
    CtiXfer        transfer;
    BYTE           inBuffer[512];
    BYTE           outBuffer[256];
    INT            status = NORMAL;
    ULONG          bytesReceived (0);

    // initialize the transfer structure
    transfer.setInBuffer( inBuffer );
    transfer.setOutBuffer( outBuffer );
    transfer.setInCountActual( &bytesReceived );
    transfer.setTraceMask(TraceFlag, TraceErrorsOnly);

    while( status == NORMAL &&
           !((aIEDDevice->getCurrentState() == CtiDeviceIED::StateHandshakeAbort) ||
             (aIEDDevice->getCurrentState() == CtiDeviceIED::StateHandshakeComplete)))
    {
        status = aIEDDevice->generateCommandHandshake (transfer, traceList);
        status = aPortRecord->outInMess(transfer, aIEDDevice, traceList);

        if( transfer.doTrace(status) )
        {
            aPortRecord->traceXfer(transfer, traceList, aIEDDevice, status);
        }

        if( deviceCanSurviveThisStatus(status) )
        {
            status = aIEDDevice->decodeResponseHandshake (transfer, status, traceList);
        }

        DisplayTraceList(aPortRecord, traceList, true);
    }


    // check our return
    if(status == NORMAL && aIEDDevice->getCurrentState() == CtiDeviceIED::StateHandshakeAbort)
    {
        status = !NORMAL;
    }

    return status;
}

INT PerformRequestedCmd ( CtiPortSPtr aPortRecord, CtiDeviceIED *aIED, INMESS *aInMessage, OUTMESS *aOutMessage, RWTPtrSlist< CtiMessage > &traceList)
{
    INT            status = NORMAL;
    CtiXfer        transfer;
    BYTE           inBuffer[512];
    BYTE           outBuffer[256];
    ULONG          bytesReceived=0;

    INT            infLoopPrevention = 0;

    // initialize the transfer structure
    transfer.setInBuffer( inBuffer );
    transfer.setOutBuffer( outBuffer );
    transfer.setInCountActual( &bytesReceived );
    transfer.setTraceMask(TraceFlag, TraceErrorsOnly);

    do
    {
        status = aIED->generateCommand ( transfer , traceList);
        status = aPortRecord->outInMess( transfer, aIED, traceList );

        if( transfer.doTrace( status ) )
        {
            aPortRecord->traceXfer(transfer, traceList, aIED, status);
        }
        if( deviceCanSurviveThisStatus(status) )
        {
            status = aIED->decodeResponse (transfer, status, traceList);
        }

        // check if we are sending load profile to scanner
        if(!status && aIED->getCurrentState() == CtiDeviceIED::StateScanReturnLoadProfile)
        {
            status = ReturnLoadProfileData ( aPortRecord, aIED, aInMessage, aOutMessage, traceList);
        }

        if(status != NORMAL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  status = " << status << " " << FormatError( status ) << endl;
            }
        }

        if(!(++infLoopPrevention % INF_LOOP_COUNT))  // If we go INF_LOOP_COUNT loops we're considering this infinite...
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  Possible infinite loop on device " << aIED->getName() << endl;
                dout << "  breaking loop, forcing abort state." << endl;
            }

            status = !NORMAL;
        }

        DisplayTraceList(aPortRecord, traceList, true);

    } while( status == NORMAL &&
             !((aIED->getCurrentState() == CtiDeviceIED::StateScanAbort) ||
               (aIED->getCurrentState() == CtiDeviceIED::StateScanComplete)));

    if( status == NORMAL && aIED->getCurrentState() == CtiDeviceIED::StateScanAbort)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  " << aIED->getName() << " State set to abort" << endl;
        }
        status = !NORMAL;
    }

    DisplayTraceList(aPortRecord, traceList, true);
    return status;
}



INT ReturnLoadProfileData ( CtiPortSPtr aPortRecord, CtiDeviceIED *aIED, INMESS *aInMessage, OUTMESS *aOutMessage,  RWTPtrSlist< CtiMessage > &traceList)
{
    INT         status = NORMAL;

    INMESS      MyInMessage;
    ULONG       bytesWritten;

    // need a copy of the read message
    memcpy(&MyInMessage, aInMessage, sizeof(INMESS));
    MyInMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[0] = CtiDeviceIED::CmdLoadProfileData;
    MyInMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[1] = aIED->getCurrentState();

    // move the load profile block into the inmessage copy
    aIED->copyLoadProfileData ((BYTE*)&MyInMessage.Buffer.DUPSt.DUPRep.Message, MyInMessage.InLength);

    /* send message back to originating process */
    if(aOutMessage->ReturnNexus->NexusState != CTINEXUS_STATE_NULL)
    {
        if(aOutMessage->ReturnNexus->CTINexusWrite(&MyInMessage, sizeof (INMESS), &bytesWritten, 15L))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Error Writing to Return Nexus " << endl;
            }
            aIED->setCurrentState(CtiDeviceIED::StateScanAbort);

            status = SOCKWRITE;
        }
        else
        {
            // return to this state to continue receiving data
            aIED->setCurrentState(aIED->getPreviousState());
        }
    }
    else
    {
        aIED->setCurrentState(CtiDeviceIED::StateScanAbort);
    }

    return status;
}


INT LogonToDevice( CtiPortSPtr aPortRecord, CtiDeviceIED *aIED, INMESS *aInMessage, OUTMESS *aOutMessage, RWTPtrSlist< CtiMessage > &traceList)
{
    int retCode (NORMAL);
    int i;
    INT   previousCmd ((INT)aIED->getCurrentCommand());


    /************************
    * a standalone master doesn't need to do the special login procedure
    *************************
    */
    if(!(aIED->isStandaloneMaster()))
    {
        // may need to put handshake here in case we had to re-dial and the master needs to
        // be interrogated first
        i = InitializeHandshake (aPortRecord,aIED, traceList);
        aIED->setCurrentState (CtiDeviceIED::StateHandshakeInitialize);

        // set the command to send the switch statement
        aIED->setCurrentCommand(CtiDeviceIED::CmdSelectMeter);

        retCode = PerformRequestedCmd (aPortRecord, aIED, aInMessage, aOutMessage, traceList);

        // we're ok so try do the handshake
        if(!retCode)
        {
            // back to the handshake
            aIED->setCurrentState (CtiDeviceIED::StateHandshakeInitialize);
            i = InitializeHandshake (aPortRecord,aIED, traceList);

            // finishes the switch command
            if(!i)
                retCode = PerformRequestedCmd (aPortRecord, aIED, aInMessage, aOutMessage, traceList);
        }

        aIED->setCurrentState (CtiDeviceIED::StateHandshakeComplete);
        aIED->setCurrentCommand((CtiDeviceIED::CtiMeterCmdStates_t) previousCmd );

    }
    else
        retCode = InitializeHandshake (aPortRecord,aIED, traceList);
    return retCode;
}

INT TerminateHandshake (CtiPortSPtr aPortRecord, CtiDeviceIED *aIEDDevice, RWTPtrSlist< CtiMessage > &traceList)
{
    CtiXfer        transfer;
    BYTE           inBuffer[512];
    BYTE           outBuffer[256];
    INT            status = NORMAL;
    ULONG          bytesReceived (0);

    // initialize the transfer structure
    transfer.setInBuffer( inBuffer );
    transfer.setOutBuffer( outBuffer );
    transfer.setInCountActual( &bytesReceived );
    transfer.setTraceMask(TraceFlag, TraceErrorsOnly);

    do
    {
        status = aIEDDevice->generateCommandDisconnect(transfer, traceList);
        status = aPortRecord->outInMess(transfer, aIEDDevice, traceList);

        if( transfer.doTrace(status) )
        {
            aPortRecord->traceXfer(transfer, traceList, aIEDDevice, status);
        }

        if( deviceCanSurviveThisStatus(status) )
        {
            status = aIEDDevice->decodeResponseDisconnect (transfer, status, traceList);
        }

    } while( status == NORMAL &&
             !((aIEDDevice->getCurrentState() == CtiDeviceIED::StateAbort) ||
               (aIEDDevice->getCurrentState() == CtiDeviceIED::StateComplete) ||
               (aIEDDevice->getCurrentState() == CtiDeviceIED::StateCompleteNoHUP)));

    // check our return
    if(status == NORMAL)
    {
        if( aPortRecord->isDialup() )
        {
            if(aIEDDevice->getCurrentState() == CtiDeviceIED::StateCompleteNoHUP)
            {
                aPortRecord->setShouldDisconnect( FALSE );
            }
            else
            {
                aPortRecord->setShouldDisconnect( TRUE );
            }
        }
    }

    else if(status == NORMAL && aIEDDevice->getCurrentState() == CtiDeviceIED::StateAbort)
    {
        status = !NORMAL;
    }

    return status;
}

BOOL areAnyOutMessagesForMyDevID(void *pId, void* d)
{
    LONG Id = (LONG)pId;
    OUTMESS *OutMessage = (OUTMESS *)d;

    return(OutMessage->DeviceID == Id);
}

BOOL areAnyOutMessagesForMyRteID(void *pId, void* d)
{
    LONG Id = (LONG)pId;
    OUTMESS *OutMessage = (OUTMESS *)d;

    return(OutMessage->Request.RouteID == Id);
}

BOOL areAnyOutMessagesForUniqueID(void *pId, void* d)
{
    LONG Id = (LONG)pId;
    OUTMESS *OutMessage = (OUTMESS *)d;

    if(PorterDebugLevel & PORTER_DEBUG_VERBOSE && OutMessage->Request.CheckSum == 0)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << " OutMessage detected zero valued checksum " << __FILE__ << " (" << __LINE__ << ") " <<
        OutMessage->Request.CheckSum << " ?=? " << Id << endl;
        dout << " Device ID " << OutMessage->DeviceID << endl;
        dout << "   " << OutMessage->Buffer.OutMessage << endl;
    }

    return(OutMessage->Request.CheckSum == Id);
}

void commFail(CtiDeviceBase *Device, INT state)
{
    extern CtiConnection VanGoghConnection;

    CtiPoint * pPoint = NULL;
    char temp[80];

    if( NULL != (pPoint = Device->getDevicePointOffsetTypeEqual(COMM_FAIL_OFFSET, StatusPointType)) )
    {
        sprintf(temp, "Communication status %s", (state == OPENED) ? "GOOD" : "FAILED");

        CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), (double)state, NormalQuality, StatusPointType, temp, TAG_POINT_MAY_BE_EXEMPTED);

        if(pData != NULL)
        {
            VanGoghConnection.WriteConnQue(pData);
        }
    }
    else if(PorterDebugLevel & PORTER_DEBUG_VERBOSE && state == CLOSED)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << Device->getName() << " would be COMM FAILED if it had offset " << COMM_FAIL_OFFSET << " defined" << endl;
    }
}


bool deviceCanSurviveThisStatus(INT status)
{
    bool survive = false;

    switch(status)
    {
    case NORMAL:
    case NODCD:
    case BADCRC:
    case READTIMEOUT:
        {
            survive = true;
        }
    default:
        {
            break;
        }
    }

    return survive;
}



INT verifyConnectedDevice(CtiPortSPtr Port, CtiDevice *pDevice, LONG &oldid, LONG &portConnectedUID)
{
    INT status = NORMAL;

    portConnectedUID = Port->getConnectedDeviceUID();

    /* 050201 CGP Added this if... May cause issues, so pay attention */
    /*
     *  Is the port connected to a DIFFERENT device with the same UID?
     */
    if( Port->getConnectedDevice() != pDevice->getID() )
    {
        // We need to fix up the old device... because we are stealing some of his thunder here!
        {
            RWRecursiveLock<RWMutexLock>::LockGuard  dev_guard(DeviceManager.getMux());       // Protect our iteration!

            CtiDevice *pOldConnectedDevice = DeviceManager.getEqual(Port->getConnectedDevice());

            oldid = pOldConnectedDevice->getPortID();
            pOldConnectedDevice->setLogOnNeeded(TRUE);

            if( pOldConnectedDevice->getUniqueIdentifier() != pDevice->getUniqueIdentifier() )
            {
                // OK, this means we need to do a logon. (At least to TAP)
                oldid = 0;
                pDevice->setLogOnNeeded(TRUE);
            }
        }

        Port->setConnectedDevice( pDevice->getID() ); // Let's switch allegiance.
    }

    return status;
}
