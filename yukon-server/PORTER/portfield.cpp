/*-----------------------------------------------------------------------------*
*
* File:   portfield
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.147 $
* DATE         :  $Date: 2005/07/14 17:19:12 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


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
        PortThread
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

#include <rw/regexp.h>
#include <rw\ctoken.h>
#include <rw\thr\mutex.h>

#include "os2_2w32.h"
#include "cticalls.h"
#include "cti_asmc.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>


#include "cparms.h"
#include "color.h"
#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "dev_lcu.h"
#include "dev_tap.h"
#include "dev_snpp.h"
#include "dev_pagingreceiver.h"
#include "dev_tnpp.h"
#include "dev_rtc.h"
#include "dev_rtm.h"
#include "dev_wctp.h"
#include "routes.h"
#include "porter.h"
#include "master.h"
#include "portdecl.h"
#include "tcpsup.h"
#include "perform.h"
#include "tapterm.h"
#include "porttcp.h"

#include "portglob.h"
#include "prot_sa3rdparty.h"
#include "portverify.h"

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
#include "dev_sentinel.h"
#include "dev_mark_v.h"
#include "msg_trace.h"
#include "msg_cmd.h"
#include "pilserver.h"
#include "xfer.h"
#include "rtdb.h"

#include "port_base.h"
#include "prot_711.h"
#include "statistics.h"
#include "trx_info.h"
#include "trx_711.h"
#include "tbl_paoexclusion.h"
#include "utility.h"


extern CtiPorterVerification PorterVerificationThread;

#define INF_LOOP_COUNT 1000

extern RWCString GetDeviceName( ULONG id );

extern void applyPortInitFail(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr);
extern void applyPortQueuePurge(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr);
extern void DisplayTraceList( CtiPortSPtr Port, RWTPtrSlist< CtiMessage > &traceList, bool consume);
extern HCTIQUEUE* QueueHandle(LONG pid);
extern void commFail(CtiDeviceSPtr &Device, INT state);

bool deviceCanSurviveThisStatus(INT status);
BOOL isTAPTermPort(LONG PortNumber);
INT RequeueReportError(INT status, OUTMESS *OutMessage);
INT PostCommQueuePeek(CtiPortSPtr Port, CtiDeviceSPtr &Device);
INT VerifyPortStatus(CtiPortSPtr Port);
INT ResetCommsChannel(CtiPortSPtr Port, CtiDeviceSPtr &Device);
INT CheckInhibitedState(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device);
INT ValidateDevice(CtiPortSPtr Port, CtiDeviceSPtr &Device, OUTMESS *&OutMessage);
INT VTUPrep(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device);
INT EstablishConnection(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device);
INT DevicePreprocessing(CtiPortSPtr Port, OUTMESS *&OutMessage, CtiDeviceSPtr &Device);
INT CommunicateDevice(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device);
INT NonWrapDecode(INMESS *InMessage, CtiDeviceSPtr &Device);
INT CheckAndRetryMessage(INT CommResult, CtiPortSPtr Port, INMESS *InMessage, OUTMESS *&OutMessage, CtiDeviceSPtr &Device);
INT DoProcessInMessage(INT CommResult, CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device);
INT ReturnResultMessage(INT CommResult, INMESS *InMessage, OUTMESS *&OutMessage);

INT InitializeHandshake (CtiPortSPtr aPortRecord, CtiDeviceSPtr aIEDDevice, RWTPtrSlist< CtiMessage > &traceList);
INT TerminateHandshake (CtiPortSPtr aPortRecord, CtiDeviceSPtr aIEDDevice, RWTPtrSlist< CtiMessage > &traceList);
INT PerformRequestedCmd ( CtiPortSPtr aPortRecord, CtiDeviceSPtr aIED, INMESS *aInMessage, OUTMESS *aOutMessage, RWTPtrSlist< CtiMessage > &traceList);
INT ReturnLoadProfileData ( CtiPortSPtr aPortRecord, CtiDeviceSPtr aIED, INMESS *aInMessage, OUTMESS *aOutMessage, RWTPtrSlist< CtiMessage > &traceList);
INT LogonToDevice( CtiPortSPtr aPortRecord, CtiDeviceSPtr aIED, INMESS *aInMessage, OUTMESS *aOutMessage, RWTPtrSlist< CtiMessage > &traceList);

void ShuffleVTUMessage( CtiPortSPtr &Port, CtiDeviceSPtr &Device, CtiOutMessage *OutMessage );
INT GetPreferredProtocolWrap( CtiPortSPtr Port, CtiDeviceSPtr &Device );
BOOL findExclusionFreeOutMessage(void *data, void* d);
bool ShuffleQueue( CtiPortSPtr shPort, OUTMESS *&OutMessage, CtiDeviceSPtr &device );
static INT CheckIfOutMessageIsExpired(OUTMESS *&OutMessage);
INT ProcessExclusionLogic(CtiPortSPtr Port, OUTMESS *&OutMessage, CtiDeviceSPtr Device);
INT ProcessPortPooling(CtiPortSPtr Port);
INT ResetChannel(CtiPortSPtr Port, CtiDeviceSPtr &Device);
INT IdentifyDeviceFromOutMessage(CtiPortSPtr Port, OUTMESS *&OutMessage, CtiDeviceSPtr &Device);
INT GetWork(CtiPortSPtr Port, CtiOutMessage *&OutMessage, ULONG &QueEntries);

static void ApplyTapNeedsLogon(const long key, CtiDeviceSPtr Dev, void* vpPortId);
static INT OutMessageRequeueOnExclusionFail(CtiPortSPtr &Port, OUTMESS *&OutMessage, CtiDeviceSPtr &Device, CtiTablePaoExclusion &exclusion);

CtiOutMessage *GetLGRippleGroupAreaBitMatch(CtiPortSPtr Port, CtiOutMessage *&OutMessage);
BOOL searchFuncForRippleOutMessage(void *firstOM, void* om);

/* Threads that handle each port for communications */
VOID PortThread(void *pid)
{
    INT            status;

    RWTime         nowTime;
    ULONG          i, j;
    ULONG          BytesWritten;
    INMESS         InMessage;
    OUTMESS        *OutMessage = 0;
    ULONG          MSecs, QueEntries;


    LONG           portid = (LONG)pid;      // NASTY CAST HERE!!!

    bool           profiling = (portid == gConfigParms.getValueAsULong("PORTER_PORT_PROFILING"));
    DWORD          ticks;

    CtiDeviceSPtr  Device;
    CtiDeviceSPtr  LastExclusionDevice;

    CtiPortSPtr    Port( PortManager.PortGetEqual( portid ) );      // Bump the reference count on the shared object!

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
        OutMessage = 0;
        nowTime = nowTime.now();

        if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 0L) )
        {
            PorterQuit = TRUE;
            continue;
        }

        if( CONTINUE_LOOP == (status = ResetChannel(Port, Device)) )
        {
            Sleep(50);
            status = 0;
            continue;
        }

        if( Port->shouldProcessQueuedDevices() )
        {
            if( LastExclusionDevice &&
                LastExclusionDevice->hasQueuedWork() &&                                             // This device has more work
                nowTime >= LastExclusionDevice->getExclusion().getExecutingUntil() &&               // This device thinks he is ready for more work
                nowTime <= LastExclusionDevice->getExclusion().getExecutionGrantExpires() &&        // The grant has not expired.
                nowTime >= LastExclusionDevice->getExclusion().getEvaluateNextAt() )                // The device thinkscan be evaluated.
            {
                Device = LastExclusionDevice;

                {
                    CtiLockGuard<CtiLogger> doubt_guard(slog);
                    slog << nowTime << " " << Device->getName() << " has been reselected by exclusion logic." << endl;
                }
            }
            else
            {
                // The OutMessage popped from here must come from a selection amongst devices that have queues...
                Device = DeviceManager.chooseExclusionDevice( Port->getPortID() );
                LastExclusionDevice = Device;
            }

            if(Device)
            {
                Device->getOutMessage(OutMessage);
                Device->incQueueProcessed(1, RWTime());

                /*  This block is trying to make us go back to normal processing through readQueue. */
                if(!OutMessage)
                {
                    Port->resetDeviceQueued(Device->getID());
                }

                // There may be retries on this device.
                if(Device->hasQueuedWork())
                {
                    Port->setDeviceQueued(Device->getID());
                }
            }
        }


        if( profiling)
        {
            ticks = GetTickCount();
        }

        if( !OutMessage && (status = GetWork( Port, OutMessage, QueEntries )) != NORMAL )
        {
            if( profiling )
            {
                ticks = GetTickCount() - ticks;

                if( ticks > 1000 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Profiling - getWork() took " << ticks << " ms **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            Sleep(250);

            continue;
        }
        else
        {
            if( profiling )
            {
                ticks = GetTickCount() - ticks;

                if( ticks > 1000 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Profiling - getWork() took " << ticks << " ms **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            if(PorterDebugLevel & PORTER_DEBUG_PORTQUEREAD)
            {
                CtiDeviceSPtr tempDev = DeviceManager.getEqual(OutMessage->TargetID ? OutMessage->TargetID : OutMessage->DeviceID);

                if(tempDev)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Port " << Port->getName() << " read an outmessage for " << tempDev->getName();
                    dout << " at priority " << OutMessage->Priority << " retries = " << OutMessage->Retry << endl;
                    if(strlen(OutMessage->Request.CommandStr) > 0) dout << RWTime() << " Command : " << OutMessage->Request.CommandStr << endl;
                    if(QueEntries > 50) dout << RWTime() << " Port has " << QueEntries << " pending OUTMESS requests " << endl;
                }
            }
        }


        /*
         *  Must verify that the outmessage has not expired.  The OM will be consumed and error returned to any
         *   requesting client.
         */
        if( CheckIfOutMessageIsExpired(OutMessage) != NORMAL )
        {
            continue;
        }

        if(Port->getConnectedDevice() != OutMessage->DeviceID)
        {
            if(Device && Device->hasExclusions())
                DeviceManager.removeInfiniteExclusion(Device);
        }

        /*
         *  This is the call which establishes the OutMessage's DeviceID as the Device we are operating upon.
         *  Upon successful return, the Device pointer is set to nonNull.
         */
        if( CONTINUE_LOOP == IdentifyDeviceFromOutMessage(Port, OutMessage, Device) )
        {
            continue;
        }

        statisticsNewRequest(OutMessage->Port, OutMessage->DeviceID, OutMessage->TargetID);

        if(PorterDebugLevel & PORTER_DEBUG_VERBOSE)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << Port->getName() << " PortThread read: OutMessage->DeviceID / Remote / Port / Priority = " << OutMessage->DeviceID << " / " << OutMessage->Remote << " / " << OutMessage->Port << " / " << OutMessage->Priority << endl;
        }

        // Copy a good portion of the OutMessage to the to-be-formed InMessage
        OutEchoToIN(OutMessage, &InMessage);

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
        if((status = ValidateDevice(Port, Device, OutMessage)) != NORMAL)
        {
            RequeueReportError(status, OutMessage);
            continue;
        }

        // See if there is a reason to proceed..
        if((status = DevicePreprocessing(Port, OutMessage, Device)) != NORMAL)   /* do any preprocessing according to type */
        {
            RequeueReportError(status, OutMessage);
            continue;
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

        if( profiling )
        {
            ticks = GetTickCount();
        }

        try
        {
            /* Execute based on wrap protocol.  Sends OutMessage and fills in InMessage */
            i = CommunicateDevice(Port, &InMessage, OutMessage, Device);
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        if( profiling )
        {
            ticks = GetTickCount() - ticks;

            if( ticks > 1000 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Profiling - CommunicateDevice took " << ticks << " ms for \"" << Device->getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

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

        if(OutMessage != NULL)
        {
            delete OutMessage; /* free up the OutMessage, it made a successful run! */
            OutMessage = NULL;
        }
    }  /* and do it all again */

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Shutdown PortThread TID: " << CurrentTID () << " for port: " << setw(4) << Port->getPortID() << " / " << Port->getName() << endl;
    }

}

/* Routine to initialize a remote based on it's type */
bool RemoteReset(CtiDeviceSPtr &Device, CtiPortSPtr Port)
{
    bool didareset = false;
    extern LoadRemoteRoutes(CtiDeviceSPtr RemoteRecord);

    ULONG j;
    INT   eRet = 0;

    if(Port->getPortID() == Device->getPortID() && !Device->isInhibited())
    {
        if(0 <= Device->getAddress() && Device->getAddress() < MAXIDLC)
        {
            CtiTransmitterInfo *pInfo = (CtiTransmitterInfo *)Device->getTrxInfo();

            if(pInfo)
            {
                if( GetPreferredProtocolWrap(Port, Device) == ProtocolWrapIDLC ) // 031003 CGP // Port->getProtocolWrap() == ProtocolWrapIDLC)
                {
                    if(Device->getAddress() != RTUGLOBAL && Device->getAddress() != CCUGLOBAL)
                    {
                        switch(Device->getType())
                        {
                        case TYPE_CCU711:
                            {
                                didareset = true;

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

                        default:
                            break;
                        }
                    }
                }
                pInfo->ClearStatus(NEEDSRESET);
            }
        }
    }

    return didareset;
}

/*----------------------------------------------------------------------------*
 * This function esentially is used to hang up the modem if it is offhook.
 * As a throughput enhancement, it was modified to peek at the queues before
 * doing so to verify that no entries exist for this device.  It will peek
 * 4 times per second for the post comm wait number of seconds.
 *
 * It returns the next queue slot which matches the connected device's UID
 * or zero if no such queue entry exists.
 *----------------------------------------------------------------------------*/
INT PostCommQueuePeek(CtiPortSPtr Port, CtiDeviceSPtr &Device)
{
    INT    i = 0;
    INT    slot = 0;
    ULONG  QueueCount = 0;

    bool    bDisconnect = false;

    if(Port->connected() && (Port->shouldDisconnect() || !Device))
    {
        Port->disconnect(Device, TraceFlag);
    }

    if(Port->connected() && Device)
    {
        ULONG stayConnectedMin = Device->getMinConnectTime();
        ULONG stayConnectedMax = Device->getMaxConnectTime();

        RWTime current;

        RWTime minTimeout(current + stayConnectedMin);
        RWTime maxTimeout(current + stayConnectedMax);

        // We always look once.
        if((slot = SearchQueue(Port->getPortQueueHandle(), (void*)Port->getConnectedDeviceUID(), searchFuncForOutMessageUniqueID)) != 0 )
        {
            if(PorterDebugLevel & PORTER_DEBUG_VERBOSE && Device)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Additional queue entry found for " << Device->getName() << endl;
            }
        }
        else
        {
            Port->setMinMaxIdle(false);

            if(stayConnectedMin)
            {
                current = current.now();
                for(i = 0; i < (ULONG)(4 * stayConnectedMin - 1); i++, current <= minTimeout)
                {
                    /* Check the queue 4 times per second for a CTIDBG_new entry for this port ... */
                    if((slot = SearchQueue(Port->getPortQueueHandle(), (void*)Port->getConnectedDeviceUID(), searchFuncForOutMessageUniqueID)) != 0 )
                    {
                        break;
                    }

                    CTISleep (250L);
                    current = current.now();
                }
            }

            Port->setMinMaxIdle(true);
            Port->postEvent();

            /* do not reinit i since times are non cumulative! */
            current = current.now();
            for( ; slot == 0 && i < (ULONG)(4 * stayConnectedMax); i++, current <= maxTimeout)
            {
                /* Check the queue 4 times per second for a CTIDBG_new entry for this port ... */
                if( !QueryQueue(Port->getPortQueueHandle(), &QueueCount) && QueueCount > 0)
                {
                    slot = SearchQueue(Port->getPortQueueHandle(), (void*)Port->getConnectedDeviceUID(), searchFuncForOutMessageUniqueID);

                    if( slot == 0 ) // No entry, or the entry is not first on the list
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
                current = current.now();
            }

            Port->setMinMaxIdle(false);
        }

        if(slot == 0 || bDisconnect)
        {
            /* Hang Up */
            Port->disconnect(Device, TraceFlag);
        }
    }

    return slot;
}

void applyPrimeTRXInfo(const long unusedid, CtiDeviceSPtr RemoteDevice, void *punused)
{
    if(RemoteDevice->hasTrxInfo())
    {
        RemoteDevice->getTrxInfo(); // Prime the TRXInfo Onject
    }
}

/*----------------------------------------------------------------------------*
 * This function prepares or resets the communications port for (re)use.
 * it checks it for proper state and setup condition.
 *
 * One important job of this function is the determination of the port's QueueSlot.  This
 * variable is used by the portqueue to decide which queue entry to pop from its
 * internal queue
 *----------------------------------------------------------------------------*/
INT ResetCommsChannel(CtiPortSPtr Port, CtiDeviceSPtr &Device)
{
    INT status = NORMAL;

    try
    {
        if( !(Port->isInhibited()))
        {
            if(Port->getType() == PortTypeTServerDirect)
            {
                if(getDebugLevel() & DEBUGLEVEL_LUDICROUS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << Port->getName() << ": IP ports open on usage." << endl;
                }
            }
            /*
             *  If the port is inhibited, don't talk to it ok...
             */
            /*
             *  If the port has not been intialized at all, do it NOW!
             */

            pair< bool, INT > portpair = Port->verifyPortStatus(Device);
            status = portpair.second;

            if( portpair.first == true && portpair.second == NORMAL)    // Indicates that it was (re)opened successfully on this pass.
            {
                /* Report which devices are available and set queues for those using IDLC*/
                DeviceManager.apply(applyPrimeTRXInfo,NULL);

                /* If neccessary start the TCPIP Interface */
                if( (StartTCPIP == TCP_SES92) || (StartTCPIP == TCP_CCU710 &&  Port->isTCPIPPort()) || (StartTCPIP == TCP_WELCO  &&  Port->isTCPIPPort()))
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
                // CGP 062304 // make all ports close
                if(Port->isDialup() || gConfigParms.isOpt("PORTER_RELEASE_IDLE_PORTS", "true"))
                {
                    Port->setQueueSlot( PostCommQueuePeek(Port, Device) );
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}

INT CheckInhibitedState(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device)
{
    INT      status = NORMAL;


    if(!Device) /* Non-existant or inhibited device so do as needed with message */
    {
        status = BADCCU;
    }
    else if(Port->isInhibited())
    {
        status = PORTINHIBITED;

        switch(Device->getType())
        {
        case TYPE_CCU711:
            {
                QueueFlush(Device);

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
                    QueueFlush(Device);

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
INT EstablishConnection(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device)
{
    INT status = NORMAL;
    LONG LastConnectedDevice = 0L;

    Port->setShouldDisconnect(FALSE);

    ULONG oldConnUID = Port->getConnectedDeviceUID();

    status = Port->connectToDevice(Device, LastConnectedDevice, TraceFlag);

    if(oldConnUID > 0 && oldConnUID != Device->getUniqueIdentifier())
    {
        {
            CtiDeviceManager::LockGuard  dev_guard(DeviceManager.getMux());       // Protect our iteration!
            CtiDeviceSPtr pOldConnectedDevice = DeviceManager.getEqual(LastConnectedDevice);

            if(pOldConnectedDevice)
            {
                pOldConnectedDevice->setLogOnNeeded(TRUE);
            }
        }
    }

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

INT DevicePreprocessing(CtiPortSPtr Port, OUTMESS *&OutMessage, CtiDeviceSPtr &Device)
{
    INT status = NORMAL;
    struct timeb   TimeB;
    ULONG          QueueCount;
    CtiOutMessage *om = 0;

    if( Device->getType() == TYPE_TAPTERM )
    {
        CtiDeviceTapPagingTerminal *pTap = (CtiDeviceTapPagingTerminal *)Device.get();
        if(pTap->devicePacingExceeded())        // Check if the pacing rate has been exceeded.
        {
            // Put it back & slow it down!

            if(Port->writeQueue(OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority, PortThread))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") Error Replacing entry onto Queue" << endl;
                }

                delete OutMessage;
            }

            OutMessage = 0;
            Sleep(100L);

            DeviceManager.apply( ApplyTapNeedsLogon, (void*)(Port->getPortID()) );
            Port->disconnect(Device, FALSE);

            return RETRY_SUBMITTED;
        }
    }
    else if( Device->getType() == TYPE_WCTP )
    {
        CtiDeviceWctpTerminal *pWctp = (CtiDeviceWctpTerminal *)Device.get();
        if(pWctp->devicePacingExceeded())        // Check if the pacing rate has been exceeded.
        {
            // Put it back & slow it down!

            if(Port->writeQueue(OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority, PortThread))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") Error Replacing entry onto Queue" << endl;
                }

                delete OutMessage;
            }

            OutMessage = 0;
            Sleep(100);

            return RETRY_SUBMITTED;
        }
    }
    else if( Device->getType() == TYPE_LCU415LG && OutMessage && (OutMessage->EventCode & RIPPLE) ) // A Control message to a LG Ripple group.
    {
        // This is where we pause and reflect upon bit mashing.

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Pausing to combine DO bits for Landis and Gyr LCR1000 Groups.  Will sleep " << gConfigParms.getValueAsULong("MINNKOTA_GROUP_DO_MASH_INITIAL_PAUSE", 10) << " seconds." << endl;
        }

        Sleep(1000 * gConfigParms.getValueAsULong("MINNKOTA_GROUP_DO_MASH_INITIAL_PAUSE", 10));

        int mashcnt;
        do
        {
            mashcnt = 0;
            // Now go find any other RBPs that have matching Group and Area code bit patterns.
            while( NULL != (om = GetLGRippleGroupAreaBitMatch(Port, OutMessage)) )
            {
                mashcnt++;

                BYTE *pGroup = (BYTE*)(OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                BYTE *pMatch = (BYTE*)(om->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);

                for(int byt=2; byt<6; byt++)
                {
                    pGroup[byt] |= pMatch[byt];
                }

                ((CtiDeviceLCU*)Device.get())->pushControlledGroupInfo(om->DeviceIDofLMGroup, om->TrxID); // Record the groups, TrxIDs in qustion here.

                delete om;
                om = 0;
            }

            if(mashcnt && gConfigParms.getValueAsULong("MINNKOTA_GROUP_DO_MASH_REPEAT_PAUSE", 0))
            {

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Pausing again to make certain no more groups are on the way...  Will sleep " << gConfigParms.getValueAsULong("MINNKOTA_GROUP_DO_MASH_REPEAT_PAUSE", 0) << " seconds." << endl;
                }
                Sleep(1000 * gConfigParms.getValueAsULong("MINNKOTA_GROUP_DO_MASH_REPEAT_PAUSE", 0));
            }

        } while( mashcnt );
    }

    /*
     *  Only certain devices will be able to queue OMs into them.  They will use the OMs to determine the exclusion selection!
     */
    UINT dqcnt = 0;

    //if( (MSGFLG_APPLY_EXCLUSION_LOGIC & OutMessage->MessageFlags) && QUEUED_TO_DEVICE == (status = Device->queueOutMessageToDevice(OutMessage, &dqcnt)) )
    if( QUEUED_TO_DEVICE == (status = Device->queueOutMessageToDevice(OutMessage, &dqcnt)) )
    {
        Device->incQueueSubmittal(1, RWTime());
        Port->setDeviceQueued( Device->getID() );

        if(gConfigParms.getValueAsULong("YUKON_SIMULATOR_DEBUGLEVEL", 0) & 0x00000001)
        {
            CtiLockGuard<CtiLogger> doubt_guard(slog);
            slog << RWTime() << " " << Device->getName() << " queuing work.  There are " << dqcnt << " entries on the queue.  Last grant at " << Device->getExclusion().getExecutionGrant() << endl;
        }
    }

    if( status == NORMAL )
        status = ProcessExclusionLogic(Port, OutMessage, Device);

    if(status == NORMAL)
    {
        if( Port->isDialup() )
        {
            if(((CtiDeviceRemote *)Device.get())->isDialup())     // Make sure the dialup pointer is NOT null!
            {
                //  init the port to the device's baud rate
                Port->setBaudRate(((CtiDeviceRemote *)Device.get())->getDialup()->getBaudRate());
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** WARNING **** " << Device->getName() << " is on a dialup port, but has no devicedialupsettings entry" << endl;

                status = BADPARAM;
            }
        }

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
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " Error Replacing entry onto Queue\n" << endl;
                                    }
                                }
                                else
                                {
                                    OutMessage = 0;
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

INT CommunicateDevice(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device)
{
    INT            status = NORMAL;
    ULONG          reject_status, ReadLength;
    struct timeb   TimeB;
    CtiRoute       *VTURouteRecord;

    BYTE           scratchBuffer[300];
    BYTE           *dataBuffer = NULL;
    CtiXfer        trx;

    RWTPtrSlist< CtiMessage > traceList;

    extern CtiConnection VanGoghConnection;

    try
    {
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

        switch(  GetPreferredProtocolWrap(Port, Device) ) // 031003 CGP // Port->getProtocolWrap())
        {
        case ProtocolWrapNone:
            {
                switch(Device->getType())
                {
                case TYPE_ILEXRTU:
                case TYPE_SES92RTU:
                case TYPE_DAVIS:
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

                case TYPE_ION7330:
                case TYPE_ION7700:
                case TYPE_ION8300:
                case TYPECBC6510:
                case TYPECBC7010:
                case TYPECBC7020:
                case TYPE_DNPRTU:
                case TYPE_DARTRTU:
                case TYPE_SERIESVRTU:
                case TYPE_SERIESVLMIRTU:
                case TYPE_RTM:
                case TYPE_SNPP:
                case TYPE_PAGING_RECEIVER:
                case TYPE_TNPP:
                    {
                        CtiDeviceSingle *ds = static_cast<CtiDeviceSingle *>(Device.get());
                        int protocolStatus = NoError;

                        if( Device->isSingle() )
                        {
                            if( !(status = ds->recvCommRequest(OutMessage)) )
                            {
                                if(Device->getType() == TYPE_SNPP)
                                {
                                    Port->close(0);  //  Close the port so it re-opens every time!
                                }

                                while( !ds->isTransactionComplete() )
                                {
                                    if( !(status = ds->generate(trx)) )
                                    {
                                        status = Port->outInMess(trx, Device, traceList);

                                        protocolStatus = ds->decode(trx, status);
                                    }

                                    //  if we had no comm errors, copy over the protocol's status -
                                    //    it may've had non-comm-related errors (NACK, bad inbound address, etc)
                                    if( !status )
                                    {
                                        status = protocolStatus;
                                    }

                                    // Prepare for tracing
                                    if(trx.doTrace(status))
                                    {
                                        Port->traceXfer(trx, traceList, Device, status);
                                    }

                                    DisplayTraceList(Port, traceList, true);
                                }

                                //  send real pointdata messages here
                                ds->sendDispatchResults(VanGoghConnection);

                                //  send text results to Commander here via return string
                                ds->sendCommResult(InMessage);

                                queue< CtiVerificationBase * > verification_queue;

                                ds->getVerificationObjects(verification_queue);

                                PorterVerificationThread.push(verification_queue);
                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint - error \"" << status << "\" in recvCommRequest() for \"" << Device->getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                            }
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - device \'" << Device->getName() << "\' is not a CtiDeviceSingle, aborting communication **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }

                        break;
                    }

                case TYPE_KV2:
                case TYPE_ALPHA_A3:
                    {

                        //extern CtiConnection VanGoghConnection;
                        BYTE  inBuffer[512];
                        BYTE  outBuffer[300];
                        ULONG bytesReceived = 0;

                        CtiDeviceKV2 *kv2dev    = ( CtiDeviceKV2 *)Device.get();
                        CtiProtocolANSI &ansi   = kv2dev->getKV2Protocol();

                        //allocate some space
                        trx.setInBuffer( inBuffer );
                        trx.setOutBuffer( outBuffer );
                        trx.setInCountActual( &bytesReceived );

                        //unwind the message we made in scanner
                        if( ansi.recvOutbound( OutMessage ) != 0 )
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " "<< kv2dev->getName() << " loop entered **********************************************" << endl;
                            }

                            while( !ansi.isTransactionComplete() )
                            {
                                //jump in, check for login, build packets, send messages, etc...
                                ansi.generate( trx );
                                status = Port->outInMess( trx, Device, traceList );
                                if( status != NORMAL )
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " "<< kv2dev->getName() << " loop is A-B-N-O-R-M-A-L " << endl;
                                }
                                ansi.decode( trx, status );

                                // Prepare for tracing
                                if( trx.doTrace( status ))
                                {
                                    Port->traceXfer( trx, traceList, Device, status );
                                }
                                DisplayTraceList( Port, traceList, true );
                            }

                            if (!ansi.isTransactionFailed())
                            {
                                Sleep(1000);
                                CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg();

                                //send dispatch lp data directly
                                kv2dev->processDispatchReturnMessage( retMsg, ansi.getParseFlags() );
                                if( !retMsg->getData().isEmpty() )
                                {
                                    VanGoghConnection.WriteConnQue( retMsg );
                                }
                                else
                                {
                                    delete retMsg;
                                }
                                InMessage->EventCode = NORMAL;
                            }
                            else
                            {
                                Sleep(1000);
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " "<< kv2dev->getName() << "'s ansi TransactionFailed.  ReadFailed. " << endl;
                                }
                                InMessage->EventCode = NOTNORMAL;
                            }
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " "<< kv2dev->getName() << " loop exited  **********************************************" << endl;
                            }
                        }
                        // return value to tell us if we are successful or not
                        //status = ansi.sendCommResult( InMessage );
                        status = kv2dev->sendCommResult( InMessage );

                        ansi.reinitialize();
                        break;
                    }
                case TYPE_SENTINEL:
                    {

                       // extern CtiConnection VanGoghConnection;
                        BYTE  inBuffer[512];
                        BYTE  outBuffer[300];
                        ULONG bytesReceived = 0;

                        CtiDeviceSentinel *sentinelDev    = ( CtiDeviceSentinel *)Device.get();
                        CtiProtocolANSI &ansi   = sentinelDev->getSentinelProtocol();

                        //allocate some space
                        trx.setInBuffer( inBuffer );
                        trx.setOutBuffer( outBuffer );
                        trx.setInCountActual( &bytesReceived );

                        //unwind the message we made in scanner
                        if( ansi.recvOutbound( OutMessage ) != 0 )
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " "<< sentinelDev->getName() <<" loop entered **********************************************" << endl;
                            }

                            while( !ansi.isTransactionComplete() )
                            {
                                //jump in, check for login, build packets, send messages, etc...
                                ansi.generate( trx );
                                status = Port->outInMess( trx, Device, traceList );
                                if( status != NORMAL )
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " "<< sentinelDev->getName()<<" loop is A-B-N-O-R-M-A-L " << endl;
                                }
                                ansi.decode( trx, status );

                                // Prepare for tracing
                                if( trx.doTrace( status ))
                                {
                                    Port->traceXfer( trx, traceList, Device, status );
                                }
                                DisplayTraceList( Port, traceList, true );
                            }
                            if (!ansi.isTransactionFailed())
                            {
                                Sleep(1000);
                                CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg();

                                //send dispatch lp data directly
                                sentinelDev->processDispatchReturnMessage( retMsg, ansi.getParseFlags() );
                                if( !retMsg->getData().isEmpty() )
                                {
                                    VanGoghConnection.WriteConnQue( retMsg );
                                }
                                else
                                {
                                    delete retMsg;
                                }
                                InMessage->EventCode = NORMAL;
                            }
                            else
                            {
                                Sleep(1000);
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " "<< sentinelDev->getName() << "'s ansi TransactionFailed.  ReadFailed. " << endl;
                                }
                                InMessage->EventCode = NOTNORMAL;
                            }
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " "<< sentinelDev->getName() <<" loop exited  **********************************************" << endl;
                            }
                        }
                        status = sentinelDev->sendCommResult( InMessage );

                        ansi.reinitialize();
                        break;
                    }


                case TYPE_TDMARKV:
                    {
                        extern CtiConnection VanGoghConnection;
                        BYTE   inBuffer[5000];
                        BYTE   outBuffer[5000];     //smaller?
                        ULONG  bytesReceived = 0;
                        int    error = 1;

                        CtiDeviceMarkV        *markv = ( CtiDeviceMarkV *)Device.get();
                        CtiProtocolTransdata  &transdata = markv->getTransdataProtocol();

                        transdata.recvOutbound( OutMessage );

                        trx.setInBuffer( inBuffer );
                        trx.setOutBuffer( outBuffer );
                        trx.setInCountActual( &bytesReceived );

                        transdata.reinitalize();

                        while( !transdata.isTransactionComplete() )
                        {
                            transdata.generate( trx );

                            status = Port->outInMess( trx, Device, traceList );

                            error = transdata.decode( trx, status );

                            if( trx.doTrace( status ))
                            {
                                Port->traceXfer( trx, traceList, Device, status );
                            }

                            DisplayTraceList( Port, traceList, true );
                        }

                        //debug
                        if( error != 0 )
                        {
                            if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " ! comm error !" << endl;
                            }
                        }

                        CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg();

                        //send dispatch lp data directly
                        markv->processDispatchReturnMessage( retMsg );

                        if( !retMsg->getData().isEmpty() )
                        {
                            VanGoghConnection.WriteConnQue( retMsg );
                        }
                        else
                        {
                            delete retMsg;
                        }

                        //send the billing data back to scanner
                        markv->sendCommResult( InMessage );

                        transdata.destroy();
                        markv = NULL;
                        break;
                    }

                case TYPE_SIXNET:
                    {
                        CtiDeviceIED         *IED= (CtiDeviceIED*)Device.get();
                        // Copy the request into the InMessage side....
                        memcpy(&InMessage->Buffer.DUPSt.DUPRep.ReqSt, &OutMessage->Buffer.DUPReq, sizeof(DIALUPREQUEST));

                        IED->allocateDataBins(OutMessage);
                        IED->setLogOnNeeded(TRUE);
                        IED->setInitialState(0);

                        if( (status = InitializeHandshake (Port, Device, traceList)) == NORMAL )
                        {
                            int dcstat;

                            status = PerformRequestedCmd (Port, Device, InMessage, OutMessage, traceList);
                            dcstat = TerminateHandshake (Port, Device, traceList);

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
                            Port->setTAP( TRUE );

                            CtiDeviceIED        *IED = (CtiDeviceIED*)Device.get();

                            IED->setLogOnNeeded(FALSE);
                            IED->setInitialState(0);
                            IED->allocateDataBins(OutMessage);

                            status = PerformRequestedCmd(Port, Device, InMessage, OutMessage, traceList);

                            IED->freeDataBins();

                            Port->close(0);         // 06062002 CGP  Make it reopen when needed.
                            Port->setTAP( FALSE );

                            //  Do verification!
                            queue< CtiVerificationBase * > verification_queue;
                            IED->getVerificationObjects(verification_queue);
                            PorterVerificationThread.push(verification_queue);

                            VanGoghConnection.WriteConnQue(Device->rsvpToDispatch());
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        break;
                    }
                case TYPE_TAPTERM:
                    {
                        LONG oldid = 0L;
                        CtiDeviceIED *IED= (CtiDeviceIED*)Device.get();

                        Port->setTAP( TRUE );

                        IED->allocateDataBins(OutMessage);
                        IED->setInitialState(oldid);

                        if( (status = InitializeHandshake (Port, Device, traceList)) == NORMAL )
                        {
                            Port->setConnectedDevice( IED->getID() );

                            status = PerformRequestedCmd (Port, Device, NULL, NULL, traceList);

                            //  Do verification!
                            queue< CtiVerificationBase * > verification_queue;
                            IED->getVerificationObjects(verification_queue);
                            PorterVerificationThread.push(verification_queue);

                            if( status != NORMAL && !(status == ErrorPageRS || status == ErrorPageNAK || status == ErrorPageNoResponse) )
                            {
                                IED->setLogOnNeeded(TRUE);      // We did not come through it cleanly, let's kill this connection.

                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }

                                Port->setConnectedDevice(0);
                            }
                            else
                            {
                                if( ((CtiDeviceTapPagingTerminal*)IED)->blockedByPageRate() )
                                {
                                    IED->setLogOnNeeded(TRUE);      // We have zero queue entries!
                                }
                                else if( SearchQueue(Port->getPortQueueHandle(), (void*)Port->getConnectedDeviceUID(), searchFuncForOutMessageUniqueID) == 0 )
                                {
                                    IED->setLogOnNeeded(TRUE);      // We have zero queue entries!
                                }
                                else
                                {
                                    // We have queue entries!  This is what gets us through the terminate routine without a hangup!
                                    IED->setLogOnNeeded(FALSE);
                                }
                            }

                            INT dcstat = TerminateHandshake (Port, Device, traceList);

                            if(status == NORMAL)
                                status = dcstat;
                        }

                        IED->freeDataBins();
                        Port->setTAP( FALSE );

                        VanGoghConnection.WriteConnQue(Device->rsvpToDispatch());

                        break;
                    }
                case TYPE_ALPHA_A1:
                case TYPE_ALPHA_PPLUS:
                case TYPE_DR87:
                case TYPE_LGS4:
                    {
                        try
                        {
                            // Copy the request into the InMessage side....
                            memcpy(&InMessage->Buffer.DUPSt.DUPRep.ReqSt, &OutMessage->Buffer.DUPReq, sizeof(DIALUPREQUEST));

                            // initialize the ied
                            CtiDeviceIED *IED= (CtiDeviceIED*)Device.get();

                            /***********************
                            *
                            *  allocating memory for the many different data internal structures the IED
                            *  uses to gather data
                            *
                            ************************
                            */

                            IED->allocateDataBins(OutMessage);
                            status = InitializeHandshake (Port,Device, traceList);

                            if(!status)
                            {
                                // this will do the initial command requested
                                if(!(status=PerformRequestedCmd (Port, Device, InMessage, OutMessage, traceList)))
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
                                        PerformRequestedCmd ( Port, Device, InMessage, OutMessage , traceList);

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
                        }
                        catch(...)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }

                        break;
                    }
                case TYPE_VECTRON:
                case TYPE_FULCRUM:
                case TYPE_QUANTUM:
                    {
                        // Copy the request into the InMessage side....
                        memcpy(&InMessage->Buffer.DUPSt.DUPRep.ReqSt, &OutMessage->Buffer.DUPReq, sizeof(DIALUPREQUEST));

                        // initialize the ied
                        CtiDeviceIED *IED= (CtiDeviceIED*)Device.get();

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

                        status = LogonToDevice (Port,Device,InMessage,OutMessage, traceList);

                        if(!status)
                        {
                            // this will do the initial command requested
                            if(!(status=PerformRequestedCmd (Port, Device, InMessage, OutMessage, traceList)))
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
                                    PerformRequestedCmd ( Port, Device, InMessage, OutMessage , traceList);

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
                case TYPE_CCU700:
                case TYPE_CCU710:
                    {
                        /* output the message to the remote */
                        trx.setOutBuffer(OutMessage->Buffer.OutMessage + PREIDLEN);
                        trx.setOutCount(OutMessage->OutLength - 3);
                        status = Port->outMess(trx, Device, traceList);
                        break;
                    }
                case TYPE_RTC:
                    {
                        queue< CtiVerificationBase * > verification_queue;

                        OutMessage->InLength = 0;

                        CtiDeviceRTC *rtc = (CtiDeviceRTC *)Device.get();

                        rtc->prepareOutMessageForComms(OutMessage);

                        rtc->getVerificationObjects(verification_queue);

                        PorterVerificationThread.push(verification_queue);

                        /* output the message to the remote */
                        trx.setOutBuffer(OutMessage->Buffer.OutMessage);
                        trx.setOutCount(OutMessage->OutLength);
                        status = Port->outMess(trx, Device, traceList);
                        break;
                    }
/*                case TYPE_RTM:
                    {
                        OutMessage->InLength = 0;

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        CtiDeviceRTM *rtm = (CtiDeviceRTM *)Device.get();

                        rtm->prepareOutMessageForComms(OutMessage);

                        // output the message to the remote
                        trx.setOutBuffer(OutMessage->Buffer.OutMessage);
                        trx.setOutCount(OutMessage->OutLength);
                        status = Port->outMess(trx, Device, traceList);

                        break;
                    }*/
                default:
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << "  Device Type " << desolveDeviceType( Device->getType() ) << " Not specifically accounted for." << endl;
                        }

                        status = BADID;
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

                    case TYPE_RTC:
                        {
                            if(OutMessage->InLength)
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " " << Device->getName() << " results pending." << endl;
                                }

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
                            }

                            break;
                        }
                    case TYPECBC6510:
                    case TYPECBC7010:
                    case TYPECBC7020:
                    case TYPE_DNPRTU:
                    case TYPE_DARTRTU:
                    case TYPE_SERIESVRTU:
                    case TYPE_SERIESVLMIRTU:
                    case TYPE_ION7330:
                    case TYPE_ION7700:
                    case TYPE_ION8300:
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
                    case TYPE_ALPHA_A3:
                    case TYPE_SENTINEL:
                    case TYPE_TDMARKV:
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
                    case TYPE_ALPHA_A3:
                    case TYPE_SENTINEL:
                    case TYPE_TDMARKV:
                    case TYPE_DNPRTU:
                    case TYPE_DARTRTU:
                    case TYPE_RTM:
                    case TYPE_SERIESVRTU:
                    case TYPE_SERIESVLMIRTU:
                    case TYPE_ION7330:
                    case TYPE_ION7700:
                    case TYPE_ION8300:
                    case TYPE_SNPP:
                    case TYPE_PAGING_RECEIVER:
                    case TYPE_TNPP:
                        break;
                    case TYPE_CCU700:
                    case TYPE_CCU710:
                        {
                            // There was an outbound error, the Xfer was not traced...
                            if(trx.doTrace(status))
                            {
                                Port->traceXfer(trx, traceList, Device, status);
                            }
                            break;
                        }
                    default:
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "  Device Type " << desolveDeviceType( Device->getType() ) << " Not specifically accounted for." << endl;
                            }
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
                    case TYPE_DAVIS:
                    case TYPECBC6510:
                    case TYPECBC7010:
                    case TYPECBC7020:
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
                        {
                            CtiTransmitterInfo *pInfo = (CtiTransmitterInfo *)Device->getTrxInfo();
                            PreIDLC (OutMessage->Buffer.OutMessage, (USHORT)OutMessage->OutLength, OutMessage->Remote, pInfo->RemoteSequence.Reply, pInfo->RemoteSequence.Request, 1, OutMessage->Source, OutMessage->Destination, OutMessage->Command);
                            break;
                        }
                    default:
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "  Device Type " << desolveDeviceType( Device->getType() ) << " Not specifically accounted for." << endl;
                            }
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
                case TYPE_DAVIS:
                    {
                        PostIDLC (OutMessage->Buffer.OutMessage, (USHORT)(OutMessage->OutLength + PREIDL + 1));

                        trx.setOutBuffer(OutMessage->Buffer.OutMessage);
                        trx.setOutCount(OutMessage->OutLength + PREIDL + 3);

                        status = Port->outMess(trx, Device, traceList);

                        break;
                    }
                case TYPE_CCU711:
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
                default:
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << "  Device Type " << desolveDeviceType( Device->getType() ) << " Not specifically accounted for." << endl;
                        }
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
                        case TYPE_CCU711:
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
                                    if( (InMessage->IDLCStat[1] >> 1) != Device->getAddress() )
                                    {
                                        status = ADDRESSERROR;
                                    }
                                    else if(InMessage->IDLCStat[2] & 0x01)    // Supervisory frame. Emetcon S-Spec Section 4.5
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
                        default:
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    dout << "  Device Type " << desolveDeviceType( Device->getType() ) << " Not specifically accounted for." << endl;
                                }
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
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Unknown Protocol Type" << endl;
                }
                break;
            }
        }

        DisplayTraceList(Port, traceList, true);
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION PROCESSING COMMS TO " << Device->getName() << " **** " << __FILE__ << " (" << __LINE__ << ") " << endl;
        }
    }

    return status;
}

INT NonWrapDecode(INMESS *InMessage, CtiDeviceSPtr &Device)
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


INT CheckAndRetryMessage(INT CommResult, CtiPortSPtr Port, INMESS *InMessage, OUTMESS *&OutMessage, CtiDeviceSPtr &Device)
{
    INT            status = CommResult;
    ULONG          j;
    ULONG          QueueCount;
    bool           iscommfailed = (CommResult == NORMAL);      // Prime with the communication status
    INT port = OutMessage->Port;
    INT deviceID = OutMessage->DeviceID;
    INT targetID = OutMessage->TargetID;

    if(Device->adjustCommCounts( iscommfailed, OutMessage->Retry > 0 ))
    {
        commFail(Device, (iscommfailed ? CLOSED : OPENED));
    }

    if(OutMessage->TargetID != 0 && OutMessage->TargetID != OutMessage->DeviceID)
    {
        // In this case, we need to account for the fail on the target device too..
        CtiDeviceSPtr pTarget = DeviceManager.getEqual( OutMessage->TargetID );

        if(pTarget)
        {
            iscommfailed = (CommResult == NORMAL);

            if( pTarget->adjustCommCounts( iscommfailed, OutMessage->Retry > 0 ) )
            {
                commFail(pTarget, (iscommfailed ? CLOSED : OPENED));
            }
        }
    }

    if( (CommResult != NORMAL) ||
        (  (GetPreferredProtocolWrap(Port, Device) == ProtocolWrapIDLC) &&         // 031003 CGP // (  (Port->getProtocolWrap() == ProtocolWrapIDLC) &&
           (OutMessage->Remote == CCUGLOBAL || OutMessage->Remote == RTUGLOBAL) ))
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
                INT omRemote = OutMessage->Remote;
                UINT omEventCode = OutMessage->EventCode;

                if(OutMessage && OutMessage->Retry > 0)
                {
                    /* decrement the retry counter */
                    --OutMessage->Retry;

                    ShuffleVTUMessage( Port, Device, OutMessage );

                    /* Put it on the queue for this port */
                    if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
                    {
                        // VioWrtTTY("Error Writing Retry into Queue\n\r", 31, 0);
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Error Writing Retry into Queue" << endl;
                        }

                        delete OutMessage;
                        OutMessage = 0;

                        status =  QUEUE_WRITE;
                    }
                    else
                    {
                        OutMessage = 0; // Cannot use it anymore.  IT is owned by others!

                        /* don't free the memory, it is back on the queue!!! */
                        /* Update the CCUInfo if neccessary */
                        if(omRemote != 0)
                        {
                            switch(Device->getType())
                            {
                            case TYPE_CCU711:
                                {
                                    CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)Device->getTrxInfo();

                                    pInfo->PortQueueEnts++;

                                    if(omEventCode & RCONT)
                                        pInfo->PortQueueConts++;
                                }
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

                            pInfo->reduceEntsConts(omEventCode & RCONT);
                        }
                    }
                }

                break;
            }
        }
    }

    // 050603 CGP The deal with the port/port pool logic!
    try
    {
        bool portwasquestionable = Port->isQuestionable();      // true if this is not his first time...

        if((PorterDebugLevel & PORTER_DEBUG_COMMFAIL) && CommResult && GetErrorType( CommResult ) == ERRTYPECOMM)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << Port->getName() << " has a COMM category error. " << CommResult << endl;
        }

        // This tallies success/fail on the port.  The port decides when he has become questionable.
        bool reportablechange = Port->adjustCommCounts(CommResult);     // returns true if there is a reportable change!

        if((PorterDebugLevel & PORTER_DEBUG_COMMFAIL) && reportablechange)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Port " << Port->getName() << " has had a comm status change (or timed report).  COMM STATUS: " << (Port->isQuestionable() ? "QUESTIONABLE" : "GOOD") << endl;
            }
        }

        if(CommResult && portwasquestionable && status != RETRY_SUBMITTED)
        {
            status = Port->requeueToParent(OutMessage);     // Return all queue entries to the processing parent.
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " EXCEPTION CAUGHT " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if(status == RETRY_SUBMITTED)
    {
        statisticsNewAttempt( port, deviceID, targetID, CommResult );
    }
    else if(CommResult != NORMAL)
    {
        try
        {
            if(OutMessage && OutMessage->MessageFlags & MSGFLG_REQUEUE_CMD_ONCE_ON_FAIL)
            {
                CtiOutMessage *NewOM = CTIDBG_new CtiOutMessage(*OutMessage);

                NewOM->Retry = 2;
                NewOM->MessageFlags &= ~MSGFLG_REQUEUE_CMD_ONCE_ON_FAIL;

                CtiPort *prt = Port.get();
                prt->writeQueue(NewOM->EventCode, sizeof (*NewOM), (char *) NewOM, NewOM->Priority, PortThread);
                prt->setShouldDisconnect( TRUE );  //  The REQUEUE_CMD flag means hang up and try again, so hang up
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    return status;
}



INT DoProcessInMessage(INT CommResult, CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device)
{
    extern void blitzNexusFromQueue(HCTIQUEUE q, CTINEXUS *&Nexus);
    extern void blitzNexusFromCCUQueue(CtiDeviceSPtr Device, CTINEXUS *&Nexus);

    INT            status = NORMAL;
    ULONG          j, QueueCount;
    struct timeb   TimeB;
    REMOTEPERF     RemotePerf;

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

            if( status && CTINEXUS::CTINexusIsFatalSocketError(status))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                blitzNexusFromQueue( Port->getPortQueueHandle(), OutMessage->ReturnNexus);
                blitzNexusFromCCUQueue( Device, OutMessage->ReturnNexus);
            }

            //  only break if this is _not_ DTRAN
            if( !(OutMessage->EventCode & DTRAN) )
            {
                break;
            }
        }
    case TYPE_CCU700:
    case TYPE_CCU710:
        {
            unsigned short nack1 = 0, nack2 = 0;

            if(CommResult)
            {
                InMessage->Buffer.DSt.Time     = InMessage->Time;
                InMessage->Buffer.DSt.DSTFlag  = InMessage->MilliTime & DSTACTIVE;
                break;
            }

            if( (status = NackTst(InMessage->Buffer.InMessage[0], &nack1, OutMessage->Remote)) ||
                (status = NackTst(InMessage->Buffer.InMessage[1], &nack2, OutMessage->Remote)) )
            {
                nack1 = nack2 = 1;
            }


            if( !nack1 && !nack2 )
            {
                InMessage->InLength = OutMessage->InLength;

                if( (OutMessage->EventCode     & BWORD) &&
                    (OutMessage->Buffer.BSt.IO & READ ) )
                {
                    DSTRUCT        DSt;

                    /* This is I so decode dword(s) for the result */
                    CommResult = InMessage->EventCode = status = D_Words (InMessage->Buffer.InMessage + 3, (USHORT)((InMessage->InLength - 3) / (DWORDLEN + 1)),  OutMessage->Remote, &DSt);
                    DSt.Time = InMessage->Time;
                    DSt.DSTFlag = InMessage->MilliTime & DSTACTIVE;
                    InMessage->Buffer.DSt = DSt;
                }
            }
            else
            {
                status = NACK1;
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
                    else
                    {
                        OutMessage = 0;
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
                    else
                    {
                        OutMessage = 0;
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
    case QUEUED_TO_DEVICE:
        {
            break;
        }
    case CONTINUE_LOOP:
        {
            break;
        }
    default:
        {
            SendError(OutMessage, status);   // This call frees the OUTMESS upon completion
            break;
        }
    }

    return result;
}


INT VTUPrep(CtiPortSPtr Port, INMESS *InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device)
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
      Device->getType() != TYPE_ALPHA_A3 &&
      Device->getType() != TYPE_SENTINEL &&
      Device->getType() != TYPE_ALPHA_A1 &&
      Device->getType() != TYPE_TDMARKV &&
      Device->getType() != TYPE_SNPP
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


INT ValidateDevice(CtiPortSPtr Port, CtiDeviceSPtr &Device, OUTMESS *&OutMessage)
{
    INT status = NORMAL;

    if(Device->getAddress() != 0xffff)
    {
        if( Device->hasTrxInfo() && !Device->isInhibited() ) // Does this device type support TrxInfo?
        {
            CtiTransmitterInfo *pInfo = (CtiTransmitterInfo *)Device->getTrxInfo();

            /* Before we do anything else make damn sure we are not a protection violation */
            if(pInfo != NULL && pInfo->GetStatus(NEEDSRESET))
            {
                /* Go Ahead an start this one up */
                if( RemoteReset(Device, Port) )
                {
                    // This device probably sourced some OMs.  We should requeue the OM which got us here!
                    OutMessage->Priority = MAXPRIORITY - 1;     // Get this message out next (after the reset messages).

                    if(Port->writeQueue(OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority, PortThread))
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") Error Replacing entry onto Queue" << endl;
                        }
                        delete OutMessage;
                    }

                    OutMessage = 0;
                    Sleep(100L);

                    status = RETRY_SUBMITTED;

                }
            }
        }
    }

    return status;
}

INT InitializeHandshake (CtiPortSPtr aPortRecord, CtiDeviceSPtr dev, RWTPtrSlist< CtiMessage > &traceList)
{
    CtiXfer        transfer;
    BYTE           inBuffer[512];
    BYTE           outBuffer[256];
    INT            status = NORMAL;
    ULONG          bytesReceived (0);

    CtiDeviceIED *aIEDDevice = (CtiDeviceIED *)dev.get();

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
        status = aPortRecord->outInMess(transfer, dev, traceList);

        if( transfer.doTrace(status) )
        {
            aPortRecord->traceXfer(transfer, traceList, dev, status);
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

INT PerformRequestedCmd ( CtiPortSPtr aPortRecord, CtiDeviceSPtr dev, INMESS *aInMessage, OUTMESS *aOutMessage, RWTPtrSlist< CtiMessage > &traceList)
{
    INT            status = NORMAL;
    CtiXfer        transfer;
    BYTE           inBuffer[512];
    BYTE           outBuffer[256];
    ULONG          bytesReceived=0;

    INT            infLoopPrevention = 0;

    CtiDeviceIED *aIED = (CtiDeviceIED *)dev.get();

    // initialize the transfer structure
    transfer.setInBuffer( inBuffer );
    transfer.setOutBuffer( outBuffer );
    transfer.setInCountActual( &bytesReceived );
    transfer.setTraceMask(TraceFlag, TraceErrorsOnly);

    try
    {
        do
        {
            status = aIED->generateCommand ( transfer , traceList);
            status = aPortRecord->outInMess( transfer, dev, traceList );
            if( transfer.doTrace( status ) )
            {
                aPortRecord->traceXfer(transfer, traceList, dev, status);
            }

            if( deviceCanSurviveThisStatus(status) )
            {
                status = aIED->decodeResponse (transfer, status, traceList);
            }

            // check if we are sending load profile to scanner
            if(!status && aIED->getCurrentState() == CtiDeviceIED::StateScanReturnLoadProfile)
            {
                status = ReturnLoadProfileData ( aPortRecord, dev, aInMessage, aOutMessage, traceList);
            }

            if(status != NORMAL)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << aIED->getName() << " status = " << status << " " << FormatError( status ) << endl;
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

        if( (status == NORMAL && aIED->getCurrentState() == CtiDeviceIED::StateScanAbort) ||
            (PorterDebugLevel & PORTER_DEBUG_VERBOSE && status != NORMAL) )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  " << aIED->getName() << " State set to abort" << endl;
                dout << "  status was returned as " << status << ".  This may be a ied state or an actual error status." << endl;
            }

            status = !NORMAL;
        }

        DisplayTraceList(aPortRecord, traceList, true);

    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return status;
}



INT ReturnLoadProfileData ( CtiPortSPtr aPortRecord, CtiDeviceSPtr dev, INMESS *aInMessage, OUTMESS *aOutMessage,  RWTPtrSlist< CtiMessage > &traceList)
{
    INT         status = NORMAL;

    INMESS      MyInMessage;
    ULONG       bytesWritten;

    CtiDeviceIED *aIED = (CtiDeviceIED *)dev.get();

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


INT LogonToDevice( CtiPortSPtr aPortRecord, CtiDeviceSPtr dev, INMESS *aInMessage, OUTMESS *aOutMessage, RWTPtrSlist< CtiMessage > &traceList)
{
    int retCode (NORMAL);
    int i;
    CtiDeviceIED *aIED = (CtiDeviceIED *)dev.get();
    INT   previousCmd ((INT)aIED->getCurrentCommand());



    /************************
    * a standalone master doesn't need to do the special login procedure
    *************************
    */
    if(!(aIED->isStandaloneMaster()))
    {
        // may need to put handshake here in case we had to re-dial and the master needs to
        // be interrogated first
        i = InitializeHandshake (aPortRecord, dev, traceList);
        aIED->setCurrentState (CtiDeviceIED::StateHandshakeInitialize);

        // set the command to send the switch statement
        aIED->setCurrentCommand(CtiDeviceIED::CmdSelectMeter);

        retCode = PerformRequestedCmd (aPortRecord, dev, aInMessage, aOutMessage, traceList);

        // we're ok so try do the handshake
        if(!retCode)
        {
            // back to the handshake
            aIED->setCurrentState (CtiDeviceIED::StateHandshakeInitialize);
            i = InitializeHandshake (aPortRecord, dev, traceList);

            // finishes the switch command
            if(!i)
                retCode = PerformRequestedCmd (aPortRecord, dev, aInMessage, aOutMessage, traceList);
        }

        aIED->setCurrentState (CtiDeviceIED::StateHandshakeComplete);
        aIED->setCurrentCommand((CtiDeviceIED::CtiMeterCmdStates_t) previousCmd );

    }
    else
        retCode = InitializeHandshake (aPortRecord, dev, traceList);
    return retCode;
}

INT TerminateHandshake (CtiPortSPtr aPortRecord, CtiDeviceSPtr dev, RWTPtrSlist< CtiMessage > &traceList)
{
    CtiXfer        transfer;
    BYTE           inBuffer[512];
    BYTE           outBuffer[256];
    INT            status = NORMAL;
    ULONG          bytesReceived (0);

    CtiDeviceIED *aIEDDevice = (CtiDeviceIED *)dev.get();

    // initialize the transfer structure
    transfer.setInBuffer( inBuffer );
    transfer.setOutBuffer( outBuffer );
    transfer.setInCountActual( &bytesReceived );
    transfer.setTraceMask(TraceFlag, TraceErrorsOnly);

    do
    {
        status = aIEDDevice->generateCommandDisconnect(transfer, traceList);
        status = aPortRecord->outInMess(transfer, dev, traceList);

        if( transfer.doTrace(status) )
        {
            aPortRecord->traceXfer(transfer, traceList, dev, status);
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
        else if(aIEDDevice->getCurrentState() == CtiDeviceIED::StateComplete)   // The device did the disconnect.
        {
            switch(aIEDDevice->getType())
            {
            case TYPE_TAPTERM:
                {
                    // Since an EOT was done on this port by this device, all devices on this port need to logon the next loop.
                    // Sweep the port and tag them so.
                    DeviceManager.apply( ApplyTapNeedsLogon, (void*)(aPortRecord->getPortID()) );
                    break;
                }
            }
        }
    }

    else if(status == NORMAL && aIEDDevice->getCurrentState() == CtiDeviceIED::StateAbort)
    {
        status = !NORMAL;
    }

    return status;
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



void ShuffleVTUMessage( CtiPortSPtr &Port, CtiDeviceSPtr &Device, CtiOutMessage *OutMessage )
{
    /* If this was a VTU message we need to slide things back over */
    if(GetPreferredProtocolWrap(Port, Device) == ProtocolWrapIDLC)  // 031003 CGP // Port->getProtocolWrap() == ProtocolWrapIDLC)
    {
        switch(Device->getType())
        {
        case TYPE_CCU700:
        case TYPE_CCU710:
        case TYPE_DAVIS:
            {
                memmove (OutMessage->Buffer.OutMessage + 7, OutMessage->Buffer.OutMessage + 10, OutMessage->OutLength - 3);
            }
        }
    }
}

INT GetPreferredProtocolWrap( CtiPortSPtr Port, CtiDeviceSPtr &Device )
{
    INT protocol = Port->getProtocolWrap();

    if(Device)
    {
        protocol = Device->getProtocolWrap();
    }

    return protocol;
}

bool ShuffleQueue( CtiPortSPtr shPort, OUTMESS *&OutMessage, CtiDeviceSPtr &device )
{
    bool bSubstitutionMade = false;
    ULONG QueueCount, ReadLength;
    OUTMESS *pOutMessage = NULL;

    CtiPort *Port = shPort.get();

    try
    {
        QueryQueue( Port->getPortQueueHandle(), &QueueCount );

        if(QueueCount)      // There are queue entries.
        {
            // We cannot be executed, we should look for another CONTROL queue entry.. We are still protected by mux...
            INT qEnt = 0;
            {
                CtiLockGuard< CtiMutex >  find_dev_guard(DeviceManager.getMux());
                qEnt = SearchQueue( Port->getPortQueueHandle(), NULL, findExclusionFreeOutMessage );
            }

            if(qEnt > 0)
            {
                REQUESTDATA    ReadResult;
                BYTE           ReadPriority;

                Port->setQueueSlot(qEnt);
                if(Port->readQueue( &ReadResult, &ReadLength, (PPVOID)&pOutMessage, DCWW_NOWAIT, &ReadPriority, &QueueCount))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Error Reading Port Queue " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                else
                {
                    if(PortManager.writeQueue(pOutMessage->Port, pOutMessage->EventCode, sizeof (*pOutMessage), (char *)pOutMessage, MAXPRIORITY - 2))
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Error Shuffling the Queue.  Message lost " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        delete pOutMessage;
                        pOutMessage = NULL;
                    }

                    /* The OUTMESS that got us here is always examined next again! */
                    if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *)OutMessage, MAXPRIORITY - 2))
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Error Shuffling the Queue.  CONTROL message lost " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        delete OutMessage;
                        OutMessage = NULL;
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " " << Port->getName() << " OutMessage Swap!" << endl;
                        }
                        //OutMessage = pOutMessage;
                        OutMessage = 0;
                        bSubstitutionMade = true;
                    }
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return bSubstitutionMade;
}


/*
 *  Used by SearchQueue.  Must be protected appropriately.
 */
BOOL findExclusionFreeOutMessage(void *data, void* d)
{
    BOOL     bStatus = FALSE;
    OUTMESS  *OutMessage = (OUTMESS *)d;

    bool     blockedByExclusion = false;

    try
    {
        if(OutMessage->MessageFlags & MSGFLG_APPLY_EXCLUSION_LOGIC  ||
           !gConfigParms.getValueAsString("PORTER_EXCLUSION_TEST").compareTo("true", RWCString::ignoreCase) )     // Indicates an excludable message!
        {
            CtiDeviceSPtr Device = DeviceManager.getEqual( OutMessage->DeviceID );

            if(Device)
            {
                CtiTablePaoExclusion exclusion;

                if( DeviceManager.mayDeviceExecuteExclusionFree(Device, exclusion) )
                {
                    if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << Device->getName() << " might be able to execute." << endl;
                    }
                    bStatus = TRUE;     // This device is locked in as executable!!!
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
        else
        {
            if(0 && getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " NON-Excludable OM found for PAOID " << OutMessage->DeviceID << endl;
            }

            bStatus = TRUE; // We can send anything which says it is non-excludable!
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return bStatus;
}


/*----------------------------------------------------------------------------*
 * This function is responsible for verifying that the message is good and will
 * not cause any immediate problems in the Porter internals.
 *----------------------------------------------------------------------------*/
INT CheckIfOutMessageIsExpired(OUTMESS *&OutMessage)
{
    INT     nRet = NoError;

    if(OutMessage != NULL)
    {
        RWTime  now;

        if(OutMessage->ExpirationTime != 0 && OutMessage->ExpirationTime < now.seconds())
        {
            // This OM has expired and should not be acted upon!
            nRet = ErrRequestExpired;
            SendError( OutMessage, nRet, NULL );
        }
    }
    else
    {
        nRet = MemoryError;
    }

    return nRet;
}

INT ProcessExclusionLogic(CtiPortSPtr Port, OUTMESS *&OutMessage, CtiDeviceSPtr Device)
{
    INT status = NORMAL;

    /*
     * Exclusion logic will consist of:
     *  - Is there a time exclusion on either the port or the device in that order?
     *  - Is the device blocked by any other currently executing device.
     *
     *  A paobject will be considered blocked if a paobjectid in the exclusion list of the paobject in question
     *  reports itself as currently executing...
     */

    try
    {
        if(OutMessage->MessageFlags & MSGFLG_APPLY_EXCLUSION_LOGIC ||
           !gConfigParms.getValueAsString("PORTER_EXCLUSION_TEST").compareTo("true", RWCString::ignoreCase) )
        {
            CtiTablePaoExclusion exclusion;

            if( !DeviceManager.mayDeviceExecuteExclusionFree(Device, exclusion) )
            {
                // There is an exclusion conflict for this device.  It cannot execute this OM.
                DeviceManager.removeInfiniteExclusion(Device);  // Remove any infinite time exclusions caused by Device from any other device in the list.

                if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << Device->getName() << " may not execute this request.  Requeue and scan for another." << endl;
                }

                // Decide how to requeue the failed OM.
                status = OutMessageRequeueOnExclusionFail(Port, OutMessage, Device, exclusion);
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    // 030503 CGP END Exclusion logic.

    return status;
}


INT ProcessPortPooling(CtiPortSPtr Port)
{
    INT status = NORMAL;

    Port->postEvent();

    if( Port->getParentPort() )
    {
        // Make sure the parent port can assign new work onto this port.
        if(Port->queueCount() == 0 && Port->getPoolAssignedGUID() != 0)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Child port " << Port->getName() << " relinquishing GUID assignment";

                if(Port->getConnectedDevice())
                {
                    dout << ". Connected To: " << GetDeviceName(Port->getConnectedDevice()) << endl;
                }
                else
                {
                    dout << endl;
                }
            }

            Port->setPoolAssignedGUID(0);
        }
    }

    return status;
}

/*
 *  This function sets up and or resets the portthread's comm port or ip port.
 *  It is responsible for opening, or reopening the comm channel or IP channel.
 *  It is responsible for resetting, or verifying any established connection from the last loop.
 */
INT ResetChannel(CtiPortSPtr Port, CtiDeviceSPtr &Device)
{
    INT status = NORMAL;
    INT initFails = 0;

    if( NORMAL != (status = ResetCommsChannel(Port, Device)) )
    {
        if(initFails++ > PorterPortInitQueuePurgeDelay)  // Every 5 minutes (default, can be CPARM'd), we will purge the queue entries.
        {
            initFails = 0;
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Port " << Port->getName() << " will not init. Queue entries are being purged." << endl;
            }

            PortManager.apply( applyPortQueuePurge, (void*)Port->getPortID() );

            //  !!!  MUST RESET LGRPQ STUFF FOR CCU'S PINFO  !!!
        }
        else
        {
            PortManager.apply( applyPortInitFail, (void*)Port->getPortID() );
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << Port->getName() << " will not init. Waiting 15 seconds " << endl;
        }

        if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 15000L) )
        {
            PorterQuit = TRUE;
        }

        status = CONTINUE_LOOP;     // make callee continue.
    }
    else
    {
        ProcessPortPooling(Port);

        // We need to see if the next Q entry is for this Device... If it is, we should not release our exclusion
        if( Device && Device->hasExclusions() && !Device->hasQueuedWork() )
        {
            DeviceManager.removeInfiniteExclusion(Device);
        }
    }

    return status;
}

/*
 *  This function uses the outmessage and the devicemanager to identify the device on which to operate
 *  Successful return is NORMAL if a device is found. In this case, the Device pointer will be a valid device.
 *  Unsuccessful return is CONTINUE_LOOP.  In this case, the Device pointer is set to 0;
 */
INT IdentifyDeviceFromOutMessage(CtiPortSPtr Port, OUTMESS *&OutMessage, CtiDeviceSPtr &Device)
{
    INT status = NORMAL;

    Device.reset();

    if(OutMessage != 0)
    {
        if(OutMessage->DeviceID == 0 && OutMessage->Remote != 0 && OutMessage->Port != 0)
        {
            if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " looking for new deviceID..." << endl;
            }

            Device = DeviceManager.RemoteGetPortRemoteEqual(OutMessage->Port, OutMessage->Remote);

            if( Device )
            {
                OutMessage->DeviceID = Device->getID();

                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " assigned new deviceID = " << Device->getID() << endl;
                }
            }
            else
            {
                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " did not assign new deviceID" << endl;
                }

                SendError(OutMessage, UnknownError);
                status = CONTINUE_LOOP;
            }
        }
        else
        {
            /* get the device record for this id */
            Device = DeviceManager.RemoteGetEqual(OutMessage->DeviceID);

            if(!Device)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Port " << Port->getName() << " just received a message for device id " << OutMessage->DeviceID << endl << \
                    " Porter does not seem to know about him and is throwing away the message!" << endl;
                }

                try
                {
                    // 060403 CGP.... No No no SendError(OutMessage, status);
                    if(OutMessage)
                    {
                        delete OutMessage;
                        OutMessage = 0;
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                status = CONTINUE_LOOP;
            }
        }
    }
    else
    {
        status = CONTINUE_LOOP;
    }

    return status;
}

INT GetWork(CtiPortSPtr Port, CtiOutMessage *&OutMessage, ULONG &QueEntries)
{
    INT status;
    ULONG ReadLength;
    REQUESTDATA ReadResult;
    BYTE ReadPriority;

    OutMessage = 0;         // Don't let us return with a bogus value!

    /*
     *  Search for the first queue entry which is ok to send.  In the general case, this should be the zeroeth entry and
     *  this call is relatively inexpensive.
     */
    if( Port->getQueueSlot() == 0 )
    {
        CtiLockGuard< CtiMutex >  find_dev_guard(DeviceManager.getMux());
        Port->setQueueSlot( Port->searchQueue( NULL, findExclusionFreeOutMessage ) );
    }

    /*
     *  This is a Read from the CTI queueing structures which will originate from
     *  some other requestor.  This is where this thread blocks and waits if there are
     *  no entries on the queue.  Note that the readQueue call allocs space for the
     *  OutMessage pointer, and fills it from it's queue entries!
     */

    if((status = Port->readQueue( &ReadResult, &ReadLength, (PPVOID) &OutMessage, DCWW_NOWAIT, &ReadPriority, &QueEntries)) != NORMAL )
    {
        if(status == ERROR_QUE_EMPTY)
        {
            if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 250L) )
            {
                PorterQuit = TRUE;
            }
        }
        else if( status != ERROR_QUE_UNABLE_TO_ACCESS)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Error Reading Port Queue " << Port->getName() << endl;
            }
        }

        status = CONTINUE_LOOP;
    }

    if(OutMessage)
    {
        Port->incQueueSubmittal(1, RWTime());
    }

    return status;
}


void ApplyTapNeedsLogon(const long key, CtiDeviceSPtr Dev, void* vpPortId)
{
    LONG pid = (LONG)vpPortId;

    if( Dev->getType() == TYPE_TAPTERM && Dev->getPortID() == pid )
    {
        Dev->setLogOnNeeded( TRUE );
    }

    return;
}

INT OutMessageRequeueOnExclusionFail(CtiPortSPtr &Port, OUTMESS *&OutMessage, CtiDeviceSPtr &Device, CtiTablePaoExclusion &exclusion)
{
    INT status = NORMAL;

    switch(exclusion.getFunctionRequeue())              // Determine what to do with this OM based upon the exclusion which BLOCKED us?
    {
    case (CtiTablePaoExclusion::RequeueNextExecutableOM):
        {
            if( ShuffleQueue( Port, OutMessage, Device ) )
            {
                // Queue has been shuffled!  OutMessage is no longer ours to touch..
                if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " OutMessage shuffled excluded for non-excluded outmessage. " << endl;
                }
            }
            else
            {
                if(getDebugLevel() & DEBUGLEVEL_LUDICROUS && getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << Port->getName() << " queue unable to be shuffled.  No non-excluded outmessages exist. " << endl;
                }

                if(Port->writeQueue(OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority, PortThread))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") Error Replacing entry onto Queue" << endl;
                    }

                    delete OutMessage;
                }

                OutMessage = 0;
                Sleep(100L);
            }

            status = RETRY_SUBMITTED;
            break;
        }
    case (CtiTablePaoExclusion::RequeueThisCommandNext):
        {
            // Keep this ONE HIGH HIGH PRIORITY.
            OutMessage->Priority = MAXPRIORITY - 1;

            if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Re-queuing original (excluded) message at high priority to examine next" << endl;
            }
            // FALL THROUGH!
        }
    case (CtiTablePaoExclusion::RequeueQueuePriority):
    default:
        {
            if(Port->writeQueue(OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority, PortThread))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") Error Replacing entry onto Queue" << endl;
                }

                delete OutMessage;
            }

            OutMessage = 0;
            Sleep(100L);

            status = RETRY_SUBMITTED;
            break;
        }
    }

    return status;
}

CtiOutMessage *GetLGRippleGroupAreaBitMatch(CtiPortSPtr Port, CtiOutMessage *&OutMessage)
{
    ULONG QueueCount, ReadLength;
    CtiOutMessage *match = 0;
    INT slot = 0;

    if((slot = SearchQueue(Port->getPortQueueHandle(), (void*)OutMessage, searchFuncForRippleOutMessage)) != 0 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Additional RIPPLE'd queue entry found for port " << Port->getName() << endl;
        }

        REQUESTDATA    ReadResult;
        BYTE           ReadPriority;

        Port->setQueueSlot(slot);
        if(Port->readQueue( &ReadResult, &ReadLength, (PPVOID)&match, DCWW_NOWAIT, &ReadPriority, &QueueCount))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Error Reading Port Queue " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            match = 0;
        }
    }

    return match;
}

BOOL searchFuncForRippleOutMessage(void *firstOM, void* om)
{
    BOOL match = FALSE;
    OUTMESS *groupOM = (OUTMESS *)firstOM;       // First OM contains the group and area code to match the bits upon.  All Bits must match.
    OUTMESS *matchOM = (OUTMESS *)om;            // This is the om on queue which is being evaluated for match.

    if( matchOM->EventCode & RIPPLE && (matchOM->DeviceID == groupOM->DeviceID))           // This is a control communication to the same LCU (Minnkota will only have LCUGLOBAL).
    {
        BYTE *pGroup = (BYTE*)(groupOM->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
        BYTE *pMatch = (BYTE*)(matchOM->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);

        if(pGroup[0] == pMatch[0] && pGroup[1] == pMatch[1] )
        {
            match = TRUE;   // Don't sweat the petty stuff and don't pet the sweaty stuff.
        }
    }

    return( match );
}



