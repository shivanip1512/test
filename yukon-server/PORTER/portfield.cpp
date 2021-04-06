#include "precompiled.h"

#include "c_port_interface.h"
#include "cti_asmc.h"
#include "dsm2.h"
#include "master.h"
#include "portdecl.h"
#include "StatisticsManager.h"
#include "portglob.h"
#include "portverify.h"

#include "mgr_port.h"

#include "mgr_device.h"
#include "dev_wctp.h"
#include "dev_ansi.h"
#include "dev_ccu721.h"
#include "dev_lcu.h"
#include "dev_mark_v.h"
#include "dev_mct.h"  //  for the test addresses
#include "dev_rtc.h"
#include "dev_tap.h"

#include "mgr_route.h"
#include "rte_macro.h"
#include "rte_ccu.h"

#include "prot_711.h"
#include "prot_emetcon.h"

#include "trx_info.h"
#include "trx_711.h"

#include "streamLocalConnection.h"
#include "portfield.h"
#include "dev_dlcbase.h"
#include "connection_client.h"
#include "desolvers.h"

#include <boost/range/algorithm.hpp>

#include <sys\timeb.h>

using namespace std;

using Cti::Porter::PorterStatisticsManager;
using Cti::Devices::DlcBaseDevice;

extern CtiPorterVerification PorterVerificationThread;
extern CtiRouteManager RouteManager;
extern Cti::StreamLocalConnection<INMESS, OUTMESS> PorterToPil;

#define INF_LOOP_COUNT 1000

extern string GetDeviceName( ULONG id );

extern void applyPortQueuePurge(const long unusedid, CtiPortSPtr ptPort, void *unusedPtr);
extern void DisplayTraceList( CtiPortSPtr Port, list< CtiMessage* > &traceList, bool consume);
extern HCTIQUEUE* QueueHandle(LONG pid);
extern void commFail(const CtiDeviceSPtr &Device);
extern bool addCommResult(long deviceID, bool wasFailure, bool retryGtZero);

using Cti::Timing::Chrono;
using namespace Cti;


/* Threads that handle each port for communications */
void PortThread(void *pid)
{
    extern std::atomic_int PortManagerThreadCount;

    PortManagerThreadCount++;

    YukonError_t   status;

    CtiTime        nowTime, nextExpireTime, nextLogPoke;
    YukonError_t   i;
    INMESS         InMessage;
    OUTMESS        *OutMessage = 0;
    ULONG          QueEntries = 0;


    LONG           portid = (LONG)pid;      // NASTY CAST HERE!!!

    bool           profiling = (portid == gConfigParms.getValueAsULong("PORTER_PORT_PROFILING"));
    LONG           expirationRate = gConfigParms.getValueAsULong("QUEUE_EXPIRE_TIMES_PER_DAY", 6);
    DWORD          ticks = 0;

    CtiDeviceSPtr  Device;
    CtiDeviceSPtr  LastExclusionDevice;

    CtiPortSPtr    Port( PortManager.getPortById( portid ) );      // Bump the reference count on the shared object!

    // Let the threads get up and running....
    WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 2500L);

    getNextExpirationTime(expirationRate, nextExpireTime);

    if( !Port )
    {
        CTILOG_ERROR(dout, "Port is Null");
    }
    else
    {
        CTILOG_INFO(dout, "PortThread for port: "<< Port->getPortID() <<" / "<< Port->getName() <<" - Started");

        string thread_name = "Port " + CtiNumStr(Port->getPortID()).zpad(4);

        SetThreadName(-1, thread_name.c_str());

        Port->startLog();

        bool timesyncPreference = false;

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

            if( nowTime > nextExpireTime )
            {
                int entries = purgeExpiredQueueEntries(*Port);
                getNextExpirationTime(expirationRate, nextExpireTime);

                if( entries > 0 )
                {
                    CTILOG_INFO(dout, "Port "<< Port->getName() <<" purged "<< entries <<" expired OM's");
                }
            }
            if( nowTime > nextLogPoke )
            {
                nextLogPoke = nowTime + 2;

                Port->getPortLog()->poke();
            }

            if( Port->isInhibited() )
            {
                Sleep(5000L);
                continue;
            }

            if( !Port->isValid() && !PortManager.getPortById(portid) )
            {
                //  we've been deleted - exit the thread
                break;
            }

            if( ClientErrors::ContinueLoop == (status = ResetChannel(Port, Device)) )
            {
                //  we're busted - don't make anyone else wait on our priorities
                DeviceManager.setDevicePrioritiesForPort(portid, CtiDeviceManager::device_priorities_t());

                Sleep(50);
                status = ClientErrors::None;
                continue;
            }

            Device = DeviceManager.chooseExclusionDevice( Port->getPortID() );

            if(Device)
            {
                Device->getOutMessage(OutMessage);
            }

            if(profiling)
            {
                ticks = GetTickCount();
            }

            if( ! OutMessage && (status = GetWork( Port, OutMessage, QueEntries, timesyncPreference )) )
            {
                if( profiling )
                {
                    ticks = GetTickCount() - ticks;

                    if( ticks > 1000 )
                    {
                        CTILOG_INFO(dout, "Profiling - getWork() took "<< ticks <<" ms");
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
                        CTILOG_INFO(dout, "Profiling - getWork() took "<< ticks <<" ms");
                    }
                }

                if(PorterDebugLevel & PORTER_DEBUG_PORTQUEREAD)
                {
                    CtiDeviceSPtr tempDev = DeviceManager.getDeviceByID(OutMessage->TargetID ? OutMessage->TargetID : OutMessage->DeviceID);

                    if(tempDev)
                    {
                        Cti::StreamBuffer output;
                        output <<"Port "<< Port->getName() <<" an outmessage for "<< tempDev->getName() <<" at priority "<< OutMessage->Priority <<" retries = "<< OutMessage->Retry;

                        if( OutMessage->Request.CommandStr[0] )
                        {
                            output << endl <<"Command : "<< OutMessage->Request.CommandStr;
                        }

                        if( QueEntries > 50 )
                        {
                            output << endl <<"Port has "<< QueEntries <<" pending OUTMESS requests ";
                        }

                        CTILOG_DEBUG(dout, output);
                    }
                }
            }


            /*
             *  Must verify that the outmessage has not expired.  The OM will be consumed and error returned to any
             *   requesting client.
             */
            if( CheckIfOutMessageIsExpired(OutMessage, nowTime) )
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
            if( ClientErrors::ContinueLoop == IdentifyDeviceFromOutMessage(Port, OutMessage, Device) )
            {
                continue;
            }

            if(PorterDebugLevel & PORTER_DEBUG_VERBOSE)
            {
                CTILOG_DEBUG(dout, Port->getName() <<" PortThread read: OutMessage->DeviceID / Remote / Port / Priority = "<< OutMessage->DeviceID <<" / "<< OutMessage->Remote <<" / "<< OutMessage->Port <<" / "<< OutMessage->Priority);
            }

            if( status = CheckInhibitedState(Port, OutMessage, Device) )
            {
                SendError(OutMessage, status);
                continue;
            }

            /* Make sure everything is A-OK with this device */
            if( status = ValidateDevice(Port, Device, OutMessage) )
            {
                RequeueReportError(status, OutMessage);
                continue;
            }

            //  See if there is a reason to proceed...  Note that this is where OMs can be queued onto devices
            if( status = DevicePreprocessing(Port, OutMessage, Device) )   /* do any preprocessing according to type */
            {
                RequeueReportError(status, OutMessage);
                continue;
            }

            timesyncPreference = !timesyncPreference;

            // Copy a good portion of the OutMessage to the to-be-formed InMessage
            OutEchoToIN(*OutMessage, InMessage);

            /* Check if this port is dial up and initiate connection. */
            if( status = EstablishConnection(Port, InMessage, OutMessage, Device) )
            {
                if(status != ClientErrors::RetrySubmitted)
                {
                    Port->reset(TraceFlag);
                }

                RequeueReportError(status, OutMessage);
                continue;
            }

            ticks = GetTickCount();

            Port->setPortCommunicating();
            try
            {
                /* Execute based on wrap protocol.  Sends OutMessage and fills in InMessage */
                i = CommunicateDevice(Port, InMessage, OutMessage, Device);
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "CommunicateDevice() failed for port "<< Port->getName());
            }

            Port->addDeviceQueuedWork( Device->getID(), Device->queuedWorkCount() );

            ticks = GetTickCount() - ticks;
            if( profiling )
            {
                if( ticks > 1000 )
                {
                    CTILOG_INFO(dout, "Profiling - CommunicateDevice() took "<< ticks <<" ms");
                }
            }

            Port->setPortCommunicating(false, ticks);

            //  if the device needs to schedule more work
            if( Device->hasPreloadWork() )
            {
                Port->setDevicePreload(Device->getID());

                processPreloads(Port);

                DeviceManager.addPortExclusion(Device->getID());
            }

            /* Non wrap protcol specific communications stuff */
            if(!i)      // No error yet.
            {
                i = NonWrapDecode(InMessage, Device);
            }

            /*
             * Check if we need to do a retry on this command. Returns RETRY_SUBMITTED if the message has
             * been requeued, or the CommunicateDevice returned otherwise
             */
            if(CheckAndRetryMessage(i, Port, InMessage, OutMessage, Device) == ClientErrors::RetrySubmitted)
            {
                continue;  // It has been re-queued!
            }

            if( YukonError_t error_code = DoProcessInMessage(i, Port, InMessage, OutMessage, Device) )
            {
                RequeueReportError(error_code, OutMessage, &InMessage);
                continue;
            }

            if( YukonError_t error_code = ReturnResultMessage(i, InMessage, OutMessage) )
            {
                RequeueReportError(error_code, OutMessage);
                continue;
            }

            if(OutMessage != NULL)
            {
                delete OutMessage; /* free up the OutMessage, it made a successful run! */
                OutMessage = NULL;
            }
        }  /* and do it all again */
    }

    CTILOG_INFO(dout, "Shutdown PortThread for port "<< Port->getPortID() <<" / "<< Port->getName());

    PortManagerThreadCount--;
}

/* Routine to initialize a remote based on it's type */
bool RemoteReset(CtiDeviceSPtr &Device, const CtiPortSPtr &Port)
{
    bool didareset = false;
    extern INT LoadRemoteRoutes(CtiDeviceSPtr RemoteRecord);

    if(Port->getPortID() == Device->getPortID() && !Device->isInhibited() )
    {
        if( Device->getAddress() >= 0      &&
            Device->getAddress() < MAXIDLC &&
            Device->hasTrxInfo() )
        {
            CtiTransmitterInfo *pInfo = Device->getTrxInfo();

            if(pInfo)
            {
                if( GetPreferredProtocolWrap(Port, Device) == ProtocolWrapIDLC &&
                    Device->getAddress() != RTUGLOBAL &&
                    Device->getAddress() != CCUGLOBAL &&
                    Device->getType()    == TYPE_CCU711 )
                {
                    didareset = true;

                    INT   eRet = 0;

                    if(!Port->isDialup())
                    {
                        ULONG j = 0;
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
                            CTILOG_INFO(dout, "Reset CCU: "<< Device->getName() <<"'s queueing control");

                            IDLCFunction (Device, 0, DEST_QUEUE, CLRDY);

                            /* Check if we need to load the routes */
                            if(LoadRoutes)
                            {
                                LoadRemoteRoutes(Device);
                            }
                        }
                    }
                }
                pInfo->clearStatus(NEEDSRESET);
            }
        }
        else if( Device->getType() == TYPE_CCU721 )
        {
            didareset = true;

            LoadRemoteRoutes(Device);
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

        CtiTime current;

        CtiTime minTimeout(current + stayConnectedMin);
        CtiTime maxTimeout(current + stayConnectedMax);

        // We always look once.
        if((slot = SearchQueue(Port->getPortQueueHandle(), (void*)Port->getConnectedDeviceUID(), searchFuncForOutMessageUniqueID)) != 0 )
        {
            if(PorterDebugLevel & PORTER_DEBUG_VERBOSE && Device)
            {
                CTILOG_DEBUG(dout, "Additional queue entry found for "<< Device->getName());
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
                    /* Check the queue 4 times per second for a new entry for this port ... */
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
                /* Check the queue 4 times per second for a new entry for this port ... */
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

            if(gConfigParms.isTrue("DEBUG_TAP") && Device)
            {
                CTILOG_DEBUG(dout, "Disconnecting Port: "<< Port->getName() <<" Dev: "<< Device->getName());
            }

            Port->disconnect(Device, TraceFlag);
        }
    }

    return slot;
}

struct primeTRXInfo
{
    void operator()(CtiDeviceSPtr &RemoteDevice)
    {
        if(RemoteDevice->hasTrxInfo())
        {
            RemoteDevice->getTrxInfo(); // Prime the TRXInfo Onject
        }
    }
};

/*----------------------------------------------------------------------------*
 * This function prepares or resets the communications port for (re)use.
 * it checks it for proper state and setup condition.
 *
 * One important job of this function is the determination of the port's QueueSlot.  This
 * variable is used by the portqueue to decide which queue entry to pop from its
 * internal queue
 *----------------------------------------------------------------------------*/
YukonError_t ResetCommsChannel(CtiPortSPtr &Port, CtiDeviceSPtr &Device)
{
    YukonError_t status = ClientErrors::None;

    try
    {
        if( !(Port->isInhibited()))
        {
            if(Port->getType() == PortTypeTServerDirect)
            {
                if(isDebugLudicrous())
                {
                   CTILOG_DEBUG(dout, Port->getName() <<" : IP ports open on usage.");
                }
            }
            /*
             *  If the port is inhibited, don't talk to it ok...
             */
            /*
             *  If the port has not been intialized at all, do it NOW!
             */

            pair< bool, YukonError_t > portpair = Port->verifyPortStatus(Device);
            status = portpair.second;

            if( portpair.first == true && portpair.second == ClientErrors::None)    // Indicates that it was (re)opened successfully on this pass.
            {
                vector<CtiDeviceManager::ptr_type> devices;

                DeviceManager.getDevicesByPortID(Port->getPortID(), devices);

                /* Report which devices are available and set queues for those using IDLC*/
                for_each(devices.begin(), devices.end(), primeTRXInfo());
            }

            if(status == ClientErrors::None)
            {
                static const bool release_idle_ports = gConfigParms.isTrue("PORTER_RELEASE_IDLE_PORTS");

                // CGP 062304 // make all ports close
                if(Port->isDialup() || release_idle_ports)
                {
                    Port->setQueueSlot( PostCommQueuePeek(Port, Device) );
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "port "<< Port->getName());
    }

    return status;
}

YukonError_t CheckInhibitedState(CtiPortSPtr Port, OUTMESS *OutMessage, CtiDeviceSPtr &Device)
{
    YukonError_t status = ClientErrors::None;


    if(!Device) /* Non-existant or inhibited device so do as needed with message */
    {
        status = ClientErrors::BadCcu;
    }
    else if(Port->isInhibited())
    {
        status = ClientErrors::PortInhibited;

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
                        p711Info->clearStatus(INLGRPQ);
                    }

                    p711Info->reduceEntsConts(OutMessage->EventCode & RCONT);
                }
            }
        }
    }
    else if(Device->isInhibited())
    {
        status =  ClientErrors::RemoteInhibited;

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
                        p711Info->clearStatus(INLGRPQ);
                    }

                    p711Info->reduceEntsConts(OutMessage->EventCode & RCONT);
                }

                break;
            }
        }
    }

    if(status)
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
YukonError_t EstablishConnection(CtiPortSPtr Port, INMESS &InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device)
{
    YukonError_t status = ClientErrors::None;
    LONG LastConnectedDevice = 0L;

    Port->setShouldDisconnect(FALSE);

    ULONG oldConnUID = Port->getConnectedDeviceUID();

    status = Port->connectToDevice(Device, LastConnectedDevice, TraceFlag);

    // This check tries to correct the status of the "LastConnectedDevice" so that log happens if needed.
    if(oldConnUID > 0 && oldConnUID != Device->getUniqueIdentifier())
    {
        CtiDeviceManager::coll_type::reader_lock_guard_t dev_guard(DeviceManager.getLock());       // Protect our iteration!
        CtiDeviceSPtr pOldConnectedDevice = DeviceManager.getDeviceByID(LastConnectedDevice);
        if(pOldConnectedDevice)
        {
            pOldConnectedDevice->setLogOnNeeded(true);
        }
    }

    if(status || !Port->connectedTo(Device->getUniqueIdentifier()) )
    {
        /* Must call CheckAndRetry to make the re-queue happen if needed */
        status = CheckAndRetryMessage(status, Port, InMessage, OutMessage, Device);  // This call may free OutMessages
    }

    return status;
}

struct TAPNeedsLogon
{
    void operator()(CtiDeviceSPtr &Dev)
    {
        if( Dev->getType() == TYPE_TAPTERM )
        {
            Dev->setLogOnNeeded(true);
        }
    }
};

struct TAPLoggedOn
{
    void operator()(CtiDeviceSPtr &Dev)
    {
        if( Dev->getType() == TYPE_TAPTERM )
        {
            Dev->setLogOnNeeded(false);
        }
    }
};

YukonError_t DevicePreprocessing(CtiPortSPtr Port, OUTMESS *&OutMessage, CtiDeviceSPtr &Device)
{
    YukonError_t status = ClientErrors::None;
    struct timeb   TimeB;
    ULONG          QueueCount;
    CtiOutMessage *om = 0;

    if( Device->getType() == TYPE_TAPTERM )
    {
        // Check if the pacing rate has been exceeded.
        auto &tap = static_cast<Cti::Devices::TapPagingTerminal &>(*Device);
        if( tap.devicePacingExceeded() )
        {
            //  Requeue the OM at the head of its priority so it will be examined next
            if(int result = Port->writeQueueWithPriority(OutMessage, std::min<int>(MAXPRIORITY, OutMessage->Priority + 1), PortThread))
            {
                CTILOG_ERROR(dout, "Could not Replace entry onto Queue ("<< result <<")");

                delete OutMessage;
            }

            OutMessage = 0;
            Sleep(100L);

            vector<CtiDeviceManager::ptr_type> devices;

            DeviceManager.getDevicesByPortID(Port->getPortID(), devices);

            for_each(devices.begin(), devices.end(), TAPNeedsLogon());

            Port->disconnect(Device, FALSE);

            return ClientErrors::RetrySubmitted;
        }
    }
    else if( Device->getType() == TYPE_WCTP )
    {
        CtiDeviceWctpTerminal *pWctp = (CtiDeviceWctpTerminal *)Device.get();
        if(pWctp->devicePacingExceeded())        // Check if the pacing rate has been exceeded.
        {
            //  Requeue the OM at the head of its priority so it will be examined next
            if(int result = Port->writeQueueWithPriority(OutMessage, std::min<int>(MAXPRIORITY, OutMessage->Priority + 1), PortThread))
            {
                CTILOG_ERROR(dout, "Could not Replace entry onto Queue ("<< result <<")");

                delete OutMessage;
            }

            OutMessage = 0;
            Sleep(100);

            return ClientErrors::RetrySubmitted;
        }
    }
    else if( Device->getType() == TYPE_LCU415LG && OutMessage && (OutMessage->EventCode & RIPPLE) ) // A Control message to a LG Ripple group.
    {
        // This is where we pause and reflect upon bit mashing.

        unsigned long sleepSeconds =  gConfigParms.getValueAsULong("MINNKOTA_GROUP_DO_MASH_INITIAL_PAUSE", 10);

        CTILOG_INFO(dout, "Pausing to combine DO bits for Landis and Gyr LCR1000 Groups.  Will sleep "<< sleepSeconds <<" seconds.");
        Sleep(1000 * sleepSeconds);

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

            if(mashcnt && (sleepSeconds = gConfigParms.getValueAsULong("MINNKOTA_GROUP_DO_MASH_REPEAT_PAUSE", 0)))
            {
                CTILOG_INFO(dout, "Pausing again to make certain no more groups are on the way...  Will sleep "<< sleepSeconds <<" seconds.");
                Sleep(1000 * sleepSeconds);
            }

        } while( mashcnt );
    }

    /*
     *  Only certain devices will be able to queue OMs into them.  They will use the OMs to determine the exclusion selection!
     */
    UINT dqcnt = 0;

    //if( (MSGFLG_APPLY_EXCLUSION_LOGIC & OutMessage->MessageFlags) && QUEUED_TO_DEVICE == (status = Device->queueOutMessageToDevice(OutMessage, &dqcnt)) )
    if( ClientErrors::QueuedToDevice == (status = Device->queueOutMessageToDevice(OutMessage, &dqcnt)) )
    {
        Port->setDeviceQueued(Device->getID());

        if( Device->hasPreloadWork() )
        {
            Port->setDevicePreload(Device->getID());

            processPreloads(Port);

            DeviceManager.addPortExclusion(Device->getID());
        }

        if( dqcnt )
        {
            Port->addDeviceQueuedWork( Device->getID(), dqcnt );
        }

        if( gConfigParms.getValueAsULong("YUKON_SIMULATOR_DEBUGLEVEL", 0) & 0x00000001 )
        {
            CTILOG_DEBUG(slog, Device->getName() <<" queuing work.  There are "<< dqcnt <<" entries on the queue.  Last grant at "<< Device->getExclusion().getExecutionGrant());
        }
    }

    if( status == ClientErrors::None )
    {
        status = ProcessExclusionLogic(Port, OutMessage, Device);
    }

    if(status == ClientErrors::None)
    {
        if( Port->isDialup() )
        {
            const CtiDeviceRemote &remote = static_cast<const CtiDeviceRemote&>(*Device);

            if( remote.isDialup() ) // Make sure the dialup pointer is NOT null!
            {
                //  init the port to the device's baud rate
                Port->setBaudRate(remote.getBaudRate());
            }
            else
            {
                CTILOG_WARN(dout, Device->getName() <<" is on a dialup port, but has no devicedialupsettings entry");

                status = ClientErrors::BadParameter;
            }
        }

        switch(Device->getType())
        {
        case TYPE_CCU711:
            {
                auto& trxInfo = static_cast<CtiTransmitter711Info&>(*Device->getTrxInfo());

                /* check if we need to load the time into a time sync */
                if(OutMessage->EventCode & TSYNC)
                {
                    if(OutMessage->EventCode & BWORD)
                    {
                        RefreshMCTTimeSync(OutMessage);
                    }
                    else
                    {
                        LoadXTimeMessage (OutMessage->Buffer.OutMessage);
                    }
                }
                else if(OutMessage->EventCode & LGRPQ_TOKEN)
                {
                    if( auto error = LoadLGrpQMessage(*Device, *OutMessage, CtiTime::now()) )
                    {
                        trxInfo.clearStatus(INLGRPQ);

                        return error;
                    }

                    OutMessage->EventCode &= ~LGRPQ_TOKEN;  //  clear the token in case we have retries, which should resend the message as-is
                }

                /* Broadcasts do not need CCU preprocessing */
                if(Device->getAddress() == CCUGLOBAL)
                    break;

                trxInfo.reduceEntsConts(OutMessage->EventCode & RCONT);

                /* Check if we are in an RCONT condition */
                if(trxInfo.getStatus(INRCONT))
                {
                    if(OutMessage->EventCode & RCONT)
                    {
                        /* Check out what the message is... if it is not RCOLQ we need to set lengths */
                        if(OutMessage->Command != CMND_RCOLQ)
                        {
                            OutMessage->InLength = trxInfo.RContInLength;
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
                        return ClientErrors::Abnormal;
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
                    return ClientErrors::RetrySubmitted;
                }

                break;
            }
        case TYPE_ILEXRTU:
            {
                if(OutMessage->EventCode & TSYNC)
                {
                    LoadILEXTimeMessage (OutMessage->Buffer.OutMessage + PREIDLEN, 0);
                }

                break;
            }
        case TYPE_WELCORTU:
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
                {
                    LoadSES92TimeMessage (OutMessage->Buffer.OutMessage + PREIDLEN, 0);
                }

                break;
            }
        case TYPE_CCU700:
        case TYPE_CCU710:
            {
                /* check if we need to load the time into a time sync */
                if(OutMessage->EventCode & TSYNC)
                {
                    RefreshMCTTimeSync(OutMessage);
                }

                break;
            }
        case TYPE_TCU5000:
            break;

        case TYPE_TCU5500:
            {
                if((OutMessage->EventCode & VERSACOM) && VCUWait)
                {
                    CtiTransmitterInfo *pInfo = Device->getTrxInfo();

                    /* Check see if we need to wait for a message to complete */
                    UCTFTime (&TimeB);
                    if(TimeB.time <= (LONG)pInfo->getNextCommandTime())
                    {
                        /* while queue is empty just sit on it otherwise put it back */
                        do
                        {
                            QueryQueue (Port->getPortQueueHandle(), &QueueCount);
                            if(QueueCount)
                            {
                                /* Write him back out to the queue */
                                if(OutMessage->Priority) OutMessage->Priority--;

                                if(Port->writeQueue(OutMessage, PortThread))
                                {
                                    CTILOG_ERROR(dout, "Could not replace entry onto queue for Port "<< OutMessage->Port <<", DeviceID "<< OutMessage->DeviceID);
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
                        } while(TimeB.time <= (LONG)pInfo->getNextCommandTime());

                        if(QueueCount) status = ClientErrors::Abnormal;
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

struct preload_offset_t
{
    CtiTime time;
    LONG deviceid;

    bool operator<(const preload_offset_t &rhs) const { return time < rhs.time || (time == rhs.time && deviceid < rhs.deviceid); }
};

void processPreloads(CtiPortSPtr Port)
{
    CtiTime now, load_begin(YUKONEOT), load_end(0UL);
    set<LONG> preloads = Port->getPreloads();
    set<LONG>::iterator itr;

    preload_offset_t preload;

    set<preload_offset_t> times;
    set<preload_offset_t>::reverse_iterator r_itr;

    CtiDeviceManager::ptr_type dev;

    for( itr = preloads.begin(); itr != preloads.end(); itr++ )
    {
        dev = DeviceManager.getDeviceByID(*itr);

        //  do not consider the device if it's inhibited
        if( dev && !dev->isInhibited() )
        {
            preload.time     = dev->getPreloadEndTime();
            preload.deviceid = dev->getID();

            times.insert(preload);
        }
    }

    //  a possible optimization here would be to also order the devices by the amount of work they have,
    //    so that a device could not be starved for time

    for( r_itr = times.rbegin(); r_itr != times.rend(); r_itr++ )
    {
        dev = DeviceManager.getDeviceByID(r_itr->deviceid);

        if( dev )
        {
            CtiTablePaoExclusion paox(0, dev->getID(), 0, 0, 0, CtiTablePaoExclusion::ExFunctionCycleTime);

            if( load_end < dev->getPreloadEndTime() )
            {
                load_end = dev->getPreloadEndTime();
            }

            if( load_begin > dev->getPreloadEndTime() )
            {
                load_begin = dev->getPreloadEndTime();
            }


            //  we would like at least this many millis to communicate
            double preload_ideal = (dev->getPreloadBytes() * 8.0) / Port->getBaudRate();

            preload_ideal *= gConfigParms.getValueAsDouble("PRELOAD_MULTIPLIER", 1.2);
            preload_ideal += gConfigParms.getValueAsULong("PRELOAD_PADDING", 5);

            if( isDebugLudicrous() )
            {
                Cti::FormattedList logItems;
                logItems.add("dev->getPreloadEndTime()") << dev->getPreloadEndTime();
                logItems.add("dev->getPreloadBytes()")   << dev->getPreloadBytes();
                logItems.add("Device will fire at")      << load_begin;
                logItems.add("Load will begin at")       << (load_begin - preload_ideal);

                CTILOG_DEBUG(dout, "Device "<< dev->getName() <<" preload info"<<
                        logItems);
            }

            if( dev->getCycleTime() )
            {
                paox.setCycleTime(dev->getCycleTime());
                paox.setCycleOffset((load_begin.seconds() - (long)preload_ideal) % dev->getCycleTime());
                paox.setTransmitTime((long)preload_ideal);
                dev->getExclusion().addExclusion(paox);

                dev->getExclusion().setEvaluateNextAt(load_begin.seconds() - preload_ideal);

                load_begin -= preload_ideal;

                if( load_begin < now )
                {
                    CTILOG_WARN(dout, "preload time window full ("<< load_begin <<") when processing \""<< dev->getName() <<"\" - should only see this once per port per cycle");
                }
            }
            else
            {
                CTILOG_ERROR(dout, "zero-length cycle time for device \""<< dev->getName() <<"\" - unable to insert preloads");
            }
        }
    }
}


YukonError_t CommunicateDevice(const CtiPortSPtr &Port, INMESS &InMessage, OUTMESS *OutMessage, const CtiDeviceSPtr &Device)
{
    YukonError_t   status = ClientErrors::None;
    ULONG          ReadLength;
    struct timeb   TimeB;

    CtiXfer        trx;

    list< CtiMessage* > traceList;

    extern CtiClientConnection VanGoghConnection;

    try
    {
        trx.setTraceMask(TraceFlag, TraceErrorsOnly, TracePort == Port->getPortID(), TraceRemote);

        const int protocolType = GetPreferredProtocolWrap(Port, Device);

        switch( protocolType ) // 031003 CGP // Port->getProtocolWrap())
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
                    case TYPE_CBC7020:
                    case TYPE_CBC8020:
                    case TYPE_CBCDNP:
                    case TYPE_DNPRTU:
                    case TYPE_DARTRTU:
                    case TYPE_SERIESVRTU:
                    case TYPE_SERIESVLMIRTU:
                    case TYPE_RTM:
                    case TYPE_SNPP:
                    case TYPE_RDS:
                    case TYPE_PAGING_RECEIVER:
                    case TYPE_TNPP:
                    case TYPE_MODBUS:
                    case TYPE_CCU721:
                    {
                        CtiDeviceSingleSPtr ds = boost::static_pointer_cast<CtiDeviceSingle>(Device);
                        YukonError_t comm_status = ClientErrors::None;

                        if( !Device->isSingle() )
                        {
                            CTILOG_ERROR(dout, "device \'"<< Device->getName() <<"\' is not a CtiDeviceSingle, aborting communication");
                        }
                        else if( status = ds->recvCommRequest(OutMessage) )
                        {
                            CTILOG_ERROR(dout, "recvCommRequest() failed for \""<< Device->getName() <<"\"");
                        }
                        else
                        {
                            if(Device->getType() == TYPE_SNPP)
                            {
                                Port->close(0);  //  Close the port so it re-opens every time!
                            }

                            while( !ds->isTransactionComplete() )
                            {
                                if( !(status = ds->generate(trx)) )
                                {
                                    comm_status = Port->outInMess(trx, Device, traceList);

                                    status = ds->decode(trx, comm_status);
                                }

                                //  don't record boring outbounds - they don't indicate failure
                                if( status || trx.getInCountExpected() )
                                {
                                    processCommStatus(status,OutMessage->DeviceID,OutMessage->TargetID,OutMessage->Retry > 0, Device);
                                }

                                // Prepare for tracing
                                if(trx.doTrace(comm_status))
                                {
                                    Port->traceXfer(trx, traceList, Device, comm_status);
                                }

                                DisplayTraceList(Port, traceList, true);
                            }

                            if (auto transactionReport = ds->getTransactionReport())
                            {
                                CTILOG_INFO(Port->getPortLog(), "SNPP Transaction Report Start" << endl << 
                                                                *transactionReport <<
                                                                "SNPP Transaction Report End" );
                                ds->clearTransactionReport();
                            }
                            
                            //  send real pointdata messages here
                            ds->sendDispatchResults(VanGoghConnection);

                            //  send text results to Commander here via return string
                            if( YukonError_t commResult_status = ds->sendCommResult(InMessage) )
                            {
                                status = commResult_status;
                            }

                            for( auto result : ds->getQueuedResults() )
                            {
                                OUTMESS *om = result.first;
                                INMESS *im = result.second;

                                if( om && im )
                                {
                                    if( (im->ErrorCode == ClientErrors::EWordReceived) && im->Buffer.RepeaterError.ESt )
                                    {
                                        im->Buffer.RepeaterError.Details =
                                            findRepeaterInRouteByAddress(
                                                om->Request.RouteID,
                                                om->Request.RetryMacroOffset,
                                                im->Buffer.RepeaterError.ESt->echo_address);
                                    }
                                    else if( ! im->ErrorCode )
                                    {
                                        if( const CtiDeviceSPtr temDevice = DeviceManager.getDeviceByID(im ->TargetID) )
                                        {
                                            if( DlcBaseDevice::dlcAddressMismatch(im->Buffer.DSt, *temDevice) )
                                            {
                                                im->ErrorCode = ClientErrors::WrongAddress;
                                            }
                                        }
                                    }

                                    addCommResult(im->TargetID, im->ErrorCode, false);

                                    //  always a completion - om->retry is generally for comms-related retries for transmitter/single devices
                                    PorterStatisticsManager.newCompletion( im->Port, im->DeviceID, im->TargetID, im->ErrorCode, im->MessageFlags );

                                    ReturnResultMessage(im->ErrorCode, *im, om);
                                }

                                // clean up the allocated memory
                                delete om;
                                delete im;
                            }

                            queue< CtiVerificationBase * > verification_queue;
                            ds->getVerificationObjects(verification_queue);

                            PorterVerificationThread.push(verification_queue);
                        }

                        break;
                    }

                    case TYPE_KV2:
                    case TYPE_ALPHA_A3:
                    case TYPE_SENTINEL:
                    case TYPE_IPC_430SL:
                    case TYPE_FOCUS:
                    case TYPE_IPC_410FL:
                    case TYPE_IPC_420FD:
                    {

                       // extern CtiConnection VanGoghConnection;
                        BYTE  inBuffer[512];
                        BYTE  outBuffer[300];
                        ULONG bytesReceived = 0;

                        Cti::Devices::CtiDeviceAnsi *ansiDev = (Cti::Devices::CtiDeviceAnsi*)Device.get();
                        Cti::Protocols::Ansi::CtiProtocolANSI &ansi   = ansiDev->getANSIProtocol();

                        ansi.setAnsiDeviceName(ansiDev->getName());
                        ansi.setAnsiDeviceType();

                        //allocate some space
                        trx.setInBuffer( inBuffer );
                        trx.setOutBuffer( outBuffer );
                        trx.setInCountActual( &bytesReceived );

                        //unwind the message we made in scanner
                        if( ansi.recvOutbound( OutMessage ) != 0 )
                        {
                            CTILOG_TRACE(dout, ansiDev->getName() <<" loop entered");

                            while( !ansi.isTransactionComplete() )
                            {

                                //jump in, check for login, build packets, send messages, etc...
                                ansi.generate( trx );

                                if( status = Port->outInMess(trx, Device, traceList) )
                                {
                                    CTILOG_ERROR(dout, ansiDev->getName() <<" Port->outInMess() failed with error code "<< status);
                                }

                                ansi.decode( trx, status );

                                processCommStatus(status, OutMessage->DeviceID, OutMessage->TargetID, OutMessage->Retry > 0, Device);

                                // Prepare for tracing
                                if( trx.doTrace( status ))
                                {
                                    Port->traceXfer( trx, traceList, Device, status );
                                }
                                DisplayTraceList( Port, traceList, true );
                            }
                            if ( ansi.forceProcessDispatchMsg() ||!ansi.isTransactionFailed())
                            {
                                Sleep(1000);
                                list< CtiReturnMsg* >  retList;
                                delete_container(retList);
                                retList.clear();

                                ansiDev->processDispatchReturnMessage( retList, ansi.getParseFlags() );
                                while( !retList.empty())
                                {
                                    CtiReturnMsg *retMsg = (CtiReturnMsg*)retList.front();retList.pop_front();
                                    VanGoghConnection.WriteConnQue(retMsg, CALLSITE);

                                }
                                delete_container(retList);
                                retList.clear();

                                InMessage.ErrorCode = ClientErrors::None;
                            }
                            else
                            {
                                CTILOG_ERROR(dout, ansiDev->getName() <<"'s ansi TransactionFailed.  ReadFailed");

                                Sleep(1000);

                                InMessage.ErrorCode = ClientErrors::Abnormal;
                            }
                            CTILOG_TRACE(dout, ansiDev->getName() <<" loop exited");
                        }
                        status = ansiDev->sendCommResult( InMessage );

                        ansi.reinitialize();
                        ansiDev = NULL;
                        break;
                    }


                    case TYPE_TDMARKV:
                    {
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

                        /*
                         *  20051014 CGP... What happens if the transdata device never decides it is finished??
                         *  Big bandaid.  Eric will have to clean this up.
                         */

                        CtiTime td_start;

                        while( !isTimedOut(td_start, 900) && !transdata.isTransactionComplete() )
                        {
                            transdata.generate( trx );

                            if( status = Port->outInMess(trx, Device, traceList) )
                            {
                                CTILOG_ERROR(dout, markv->getName() <<" Port->outInMess() failed with error code "<< status);
                            }

                            error = transdata.decode( trx, status );

                            processCommStatus(status, OutMessage->DeviceID, OutMessage->TargetID, OutMessage->Retry > 0, Device);

                            if( trx.doTrace( status ))
                            {
                                Port->traceXfer( trx, traceList, Device, status );
                            }

                            DisplayTraceList( Port, traceList, true );
                        }

                        if(isTimedOut(td_start, 900))
                        {
                            CTILOG_ERROR(dout, markv->getName() <<" has timed out on its operations.");
                        }

                        //debug
                        if( error != 0 )
                        {
                            if( isDebugLudicrous() )
                            {
                                CTILOG_DEBUG(dout, "Comm error");
                            }
                        }

                        CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg();

                        //send dispatch lp data directly
                        markv->processDispatchReturnMessage( retMsg );

                        if( !retMsg->getData().empty() )
                        {
                            VanGoghConnection.WriteConnQue( retMsg, CALLSITE );
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
                        ::memcpy(&InMessage.Buffer.DUPSt.DUPRep.ReqSt, &OutMessage->Buffer.DUPReq, sizeof(DIALUPREQUEST));

                        IED->allocateDataBins(OutMessage);
                        IED->setLogOnNeeded(true);
                        IED->setInitialState(0);

                        if( (status = InitializeHandshake (Port, Device, traceList)) == ClientErrors::None )
                        {
                            status = PerformRequestedCmd (Port, Device, &InMessage, OutMessage, traceList);
                            YukonError_t dcstat = TerminateHandshake (Port, Device, traceList);

                            if(status == ClientErrors::None)
                                status = dcstat;
                        }

                        IED->freeDataBins();

                        // 071901 CGP. For now, prevent the original outmessage from returning to the requestor.

                        // OutMessage->EventCode &= ~RESULT;
                        InMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[0] = CtiDeviceIED::CmdScanData;

                        break;
                    }
                    case TYPE_WCTP:
                    {
                        try
                        {
                            Port->setTAP( TRUE );

                            CtiDeviceIED *IED = (CtiDeviceIED*)Device.get();

                            IED->setLogOnNeeded(false);
                            IED->setInitialState(0);
                            IED->allocateDataBins(OutMessage);

                            status = PerformRequestedCmd(Port, Device, &InMessage, OutMessage, traceList);

                            IED->freeDataBins();

                            Port->close(0);         // 06062002 CGP  Make it reopen when needed.
                            Port->setTAP( FALSE );

                            //  Do verification!
                            queue< CtiVerificationBase * > verification_queue;
                            IED->getVerificationObjects(verification_queue);
                            PorterVerificationThread.push(verification_queue);

                            if(const auto msg = Device->rsvpToDispatch())
                            {
                                VanGoghConnection.WriteConnQue(msg, CALLSITE);
                            }
                        }
                        catch(...)
                        {
                            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "port "<< Port->getName());
                        }

                        break;
                    }
                    case TYPE_TAPTERM:
                    {
                        CtiDeviceIED *IED= (CtiDeviceIED*)Device.get();

                        Port->setTAP( TRUE );

                        IED->allocateDataBins(OutMessage);
                        IED->setInitialState(0);

                        if( (status = InitializeHandshake (Port, Device, traceList)) == ClientErrors::None )
                        {
                            IED->resetHandshakesRemaining();
                            if(!Port->isDialup())
                            {
                                vector<CtiDeviceManager::ptr_type> devices;

                                DeviceManager.getDevicesByPortID(Port->getPortID(), devices);

                                for_each(devices.begin(), devices.end(), TAPLoggedOn());
                            }

                            Port->setConnectedDevice( IED->getID() );
                            Port->setConnectedDeviceUID( IED->getUniqueIdentifier() );

                            status = PerformRequestedCmd (Port, Device, NULL, NULL, traceList);

                            //  Do verification!
                            queue< CtiVerificationBase * > verification_queue;
                            IED->getVerificationObjects(verification_queue);
                            PorterVerificationThread.push(verification_queue);

                            if( status && !(status == ClientErrors::PageRS || status == ClientErrors::PageNAK || status == ClientErrors::PageNoResponse) )
                            {
                                IED->setLogOnNeeded(true);      // We did not come through it cleanly, let's kill this connection.
                                Port->setConnectedDevice(0);
                            }
                            else
                            {
                                auto &tap = static_cast<Cti::Devices::TapPagingTerminal &>(*IED);
                                if( tap.blockedByPageRate() )
                                {
                                    IED->setLogOnNeeded(true);      // We have zero queue entries!
                                }
                                else if( SearchQueue(Port->getPortQueueHandle(), (void*)Port->getConnectedDeviceUID(), searchFuncForOutMessageUniqueID) == 0 )
                                {
                                    IED->setLogOnNeeded(true);      // We have zero queue entries!
                                }
                                else
                                {
                                    // We have queue entries!  This is what gets us through the terminate routine without a hangup!
                                    IED->setLogOnNeeded(false);
                                }
                            }

                            YukonError_t dcstat = TerminateHandshake (Port, Device, traceList);

                            if(status == ClientErrors::None)
                                status = dcstat;
                        }
                        else
                        {
                            if( IED->getHandshakesRemaining() <= 0 )
                            {
                                IED->setLogOnNeeded(true);      // We did not come through it cleanly, let's kill this connection.
                                Port->setShouldDisconnect();
                                IED->resetHandshakesRemaining();
                            }
                            else
                            {
                                IED->decreaseHandshakesRemaining();
                            }
                        }

                        if( Port->isSimulated() )
                        {
                            CTILOG_INFO(slog, IED->getName() <<": "<< OutMessage->Request.CommandStr);
                        }

                        IED->freeDataBins();
                        Port->setTAP( FALSE );

                        if(const auto msg = Device->rsvpToDispatch())
                        {
                            VanGoghConnection.WriteConnQue(msg, CALLSITE);
                        }

                        break;
                    }
                    case TYPE_ALPHA_A1:
                    case TYPE_ALPHA_PPLUS:
                    case TYPE_DR87:
                    case TYPE_LGS4:
                    case TYPE_IPC_430S4E:
                    {
                        try
                        {
                            // Copy the request into the InMessage side....
                            ::memcpy(&InMessage.Buffer.DUPSt.DUPRep.ReqSt, &OutMessage->Buffer.DUPReq, sizeof(DIALUPREQUEST));

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

                            processCommStatus(status,OutMessage->DeviceID,OutMessage->TargetID,OutMessage->Retry > 0, Device);

                            if(!status)
                            {
                                // this will do the initial command requested
                                if(!(status=PerformRequestedCmd (Port, Device, &InMessage, OutMessage, traceList)))
                                {
                                    /*********************************************
                                    * Use the byte 2 of the command message to keep the
                                    * final state of communications with the device
                                    * to send to scanner
                                    **********************************************
                                    */
                                    InMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[1] = IED->getCurrentState();

                                    // check if there is load profile to do
                                    if(IED->getCurrentCommand() == CtiDeviceIED::CmdLoadProfileTransition)
                                    {
                                        // set to load profile request
                                        IED->setCurrentCommand(CtiDeviceIED::CmdLoadProfileData);

                                        // note, current command must be reset to scan data before returning
                                        // or the decode response routine will not work correctly
                                        PerformRequestedCmd ( Port, Device, &InMessage, OutMessage , traceList);

                                        // reset to scan data once completed
                                        IED->setCurrentCommand( CtiDeviceIED::CmdScanData );
                                    }

                                    // only do this if we were doing a scan data
                                    if(IED->getCurrentCommand() == CtiDeviceIED::CmdScanData)
                                    {
                                        // will need to move these back
                                        IED->reformatDataBuffer (InMessage.Buffer.DUPSt.DUPRep.Message,InMessage.InLength);
                                    }
                                }
                                else
                                {
                                    InMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[1] = IED->getCurrentState();
                                }
                            }
                            else
                            {
                                InMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[1] = IED->getCurrentState();
                            }

                            // free the memory we used
                            IED->freeDataBins();
                        }
                        catch(...)
                        {
                            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "port "<< Port->getName());
                        }

                        break;
                    }
                    case TYPE_VECTRON:
                    case TYPE_FULCRUM:
                    case TYPE_QUANTUM:
                    {
                        // Copy the request into the InMessage side....
                        ::memcpy(&InMessage.Buffer.DUPSt.DUPRep.ReqSt, &OutMessage->Buffer.DUPReq, sizeof(DIALUPREQUEST));

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
                            if(!(status=PerformRequestedCmd (Port, Device, &InMessage, OutMessage, traceList)))
                            {
                                /*********************************************
                                * Use the byte 2 of the command message to keep the
                                * final state of communications with the device
                                * to send to scanner
                                **********************************************
                                */
                                InMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[1] = IED->getCurrentState();

                                // check if there is load profile to do
                                if(IED->getCurrentCommand() == CtiDeviceIED::CmdLoadProfileTransition)
                                {
                                    // set to load profile request
                                    IED->setCurrentCommand(CtiDeviceIED::CmdLoadProfileData);

                                    // note, current command must be reset to scan data before returning
                                    // or the decode response routine will not work correctly
                                    PerformRequestedCmd ( Port, Device, &InMessage, OutMessage , traceList);

                                    // reset to scan data once completed
                                    IED->setCurrentCommand( CtiDeviceIED::CmdScanData );
                                }

                                // only do this if we were doing a scan data
                                if(IED->getCurrentCommand() == CtiDeviceIED::CmdScanData)
                                {
                                    // will need to move these back
                                    IED->reformatDataBuffer (InMessage.Buffer.DUPSt.DUPRep.Message,InMessage.InLength);
                                }
                            }
                            else
                            {
                                InMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[1] = IED->getCurrentState();
                            }
                        }
                        else
                        {
                            InMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[1] = IED->getCurrentState();
                        }

                        // free the memory we used
                        IED->freeDataBins();

                        break;
                    }
                    case TYPE_WELCORTU:
                    {
                        CTILOG_ERROR(dout, "Invalid Port Protocol for Welco device");

                        status = ClientErrors::BadPort;
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
                    default:
                    {
                        CTILOG_ERROR(dout, "Device Type "<< desolveDeviceType(Device->getType()) <<" Not specifically accounted for.");

                        status = ClientErrors::BadId;
                    }
                }

                /* get the time into the return message */
                UCTFTime (&TimeB);
                InMessage.Time = TimeB.time;
                InMessage.MilliTime = TimeB.millitm;

                if(TimeB.dstflag) InMessage.MilliTime |= DSTACTIVE;

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
                            trx.setInBuffer(InMessage.Buffer.InMessage);
                            trx.setInCountExpected(OutMessage->InLength);
                            trx.setInCountActual(&InMessage.InLength);
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
                            InMessage.InLength = 0;
                            ReadLength = 0;

                            /* get the first 4 chars of the message */
                            trx.setInBuffer(InMessage.Buffer.InMessage);
                            trx.setInCountExpected( 4 );
                            trx.setInCountActual(&InMessage.InLength);
                            trx.setInTimeout(OutMessage->TimeOut);
                            trx.setMessageStart();                          // This is the first "in" of this message
                            trx.setMessageComplete(0);                      // This is _NOT_ the last "in" of this message

                            status = Port->inMess(trx, Device, traceList);

                            if(!status)
                            {
                                /* check out the message... How much follows */
                                status = PostMaster (InMessage.Buffer.InMessage, Device->getAddress(), &ReadLength);
                            }

                            if(!status)
                            {
                                /* get the rest of the message */
                                trx.setInBuffer(InMessage.Buffer.InMessage + 4);
                                trx.setInCountExpected( ReadLength + 2 );
                                trx.setInCountActual(&ReadLength);
                                trx.setInTimeout(OutMessage->TimeOut);
                                trx.setMessageStart(0);                         // This is _NOT_ the first "in" of this message
                                trx.setMessageComplete();                       // This is the last "in" of this message

                                status = Port->inMess(trx, Device, traceList);
                            }

                            InMessage.InLength += ReadLength;

                            if(!status)
                            {
                                /* Check crc of message */
                                status = MasterReply (InMessage.Buffer.InMessage, (USHORT)InMessage.InLength);
                            }

                            // Prepare for tracing
                            trx.setInBuffer(InMessage.Buffer.InMessage);
                            trx.setInCountActual(&InMessage.InLength);
                            trx.setTraceMask(TraceFlag, TraceErrorsOnly, TracePort == Port->getPortID(), TraceRemote);

                            if(trx.doTrace(status))
                            {
                                Port->traceXfer(trx, traceList, Device, status);
                            }

                            if(!status)
                            {
                                InMessage.InLength = ReadLength - 2;
                            }

                            break;
                        }

                    case TYPE_RTC:
                        {
                            if(OutMessage->InLength)
                            {
                                CTILOG_INFO(dout, Device->getName() <<" results pending.");

                                trx.setInBuffer(InMessage.Buffer.InMessage);
                                trx.setInCountExpected(OutMessage->InLength);
                                trx.setInCountActual(&InMessage.InLength);
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
                    case TYPE_CBC7020:
                    case TYPE_CBC8020:
                    case TYPE_CBCDNP:
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
                    case TYPE_IPC_430S4E:
                    case TYPE_KV2:
                    case TYPE_ALPHA_A3:
                    case TYPE_SENTINEL:
                    case TYPE_IPC_430SL:
                    case TYPE_FOCUS:
                    case TYPE_IPC_410FL:
                    case TYPE_IPC_420FD:
                    case TYPE_TDMARKV:
                    case TYPE_CCU721:
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
                    case TYPE_IPC_430S4E:
                    case TYPE_KV2:
                    case TYPE_ALPHA_A3:
                    case TYPE_SENTINEL:
                    case TYPE_IPC_430SL:
                    case TYPE_FOCUS:
                    case TYPE_IPC_410FL:
                    case TYPE_IPC_420FD:
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
                    case TYPE_RDS:
                    case TYPE_CBC7020:
                    case TYPE_CBC8020:
                    case TYPE_CBCDNP:
                    case TYPE_PAGING_RECEIVER:
                    case TYPE_TNPP:
                    case TYPE_MODBUS:
                    case TYPE_CCU721:
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
                            CTILOG_ERROR(dout, "Device Type "<< desolveDeviceType(Device->getType()) <<" Not specifically accounted for.");
                        }
                    }
                }

                break;  /* No Wrap */
            }
        case ProtocolWrapIDLC:
            {
                /* Check for broadcast type message */
                if(OutMessage->Remote == CCUGLOBAL && Device->getType() == TYPE_CCU711)
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
                    case TYPE_SES92RTU:
                    case TYPE_LCU415:
                    case TYPE_LCU415LG:
                    case TYPE_LCU415ER:
                    case TYPE_LCUT3026:
                    case TYPE_TCU5000:
                    case TYPE_TCU5500:
                        {
                            PreUnSequenced (OutMessage->Buffer.OutMessage, (USHORT)OutMessage->OutLength, Device->getPortID(), Device->getAddress(), Device);
                            break;
                        }
                    case TYPE_CCU700:
                    case TYPE_CCU710:
                    case TYPE_DAVIS:
                    case TYPE_CBC7020:
                    case TYPE_CBC8020:
                    case TYPE_CBCDNP:
                        {
                            CTILOG_WARN(dout, "Function Incomplete");

                            // PreVTU (OutMessage->Buffer.OutMessage, (USHORT)(OutMessage->OutLength - 3), (USHORT)Device->getAddress(), (USHORT)(VTURouteRecord->getBus()), (USHORT)OutMessage->InLength, (USHORT)(OutMessage->TimeOut));
                            break;
                        }
                    case TYPE_CCU711:
                        {
                            if( OutMessage->MessageFlags & MessageFlag_PortSharing &&
                                OutMessage->Buffer.OutMessage[0] == 0x7e &&
                                /*OutMessage->Buffer.OutMessage[2]  & 0x01 &&*/
                                OutMessage->Buffer.OutMessage[2] != HDLC_UD )
                            {
                                //  if it's an IDLC control message from a port share, leave it alone
                            }
                            else
                            {
                                CtiTransmitterInfo *pInfo = Device->getTrxInfo();
                                PreIDLC(OutMessage->Buffer.OutMessage, (USHORT)OutMessage->OutLength, OutMessage->Remote, pInfo->RemoteSequence.Reply, pInfo->RemoteSequence.Request, 1, OutMessage->Source, OutMessage->Destination, OutMessage->Command);
                            }

                            break;
                        }
                    default:
                        {
                            CTILOG_ERROR(dout, "Device Type "<< desolveDeviceType(Device->getType()) <<" Not specifically accounted for.");
                        }
                    }
                }

                /* calculate the crc and output the message */
                switch(Device->getType())
                {
                case TYPE_ILEXRTU:
                case TYPE_WELCORTU:
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
                        if( OutMessage->MessageFlags & MessageFlag_PortSharing &&
                            OutMessage->Buffer.OutMessage[0] == 0x7e &&
                            //OutMessage->Buffer.OutMessage[2]  & 0x01 &&
                            OutMessage->Buffer.OutMessage[2] != HDLC_UD )
                        {
                            //  if it's an IDLC control message from a port share, leave it alone

                            trx.setOutBuffer(OutMessage->Buffer.OutMessage);
                            trx.setOutCount(OutMessage->OutLength);
                        }
                        else
                        {
                            //  tack on the CRC
                            PostIDLC (OutMessage->Buffer.OutMessage, (USHORT)(OutMessage->OutLength + PREIDL - 2));

                            trx.setOutBuffer(OutMessage->Buffer.OutMessage);
                            trx.setOutCount(OutMessage->OutLength + PREIDL);
                        }

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
                        CTILOG_ERROR(dout, "Device Type "<< desolveDeviceType(Device->getType()) <<" Not specifically accounted for.");
                    }
                }

                /* get the time into the return message */
                UCTFTime (&TimeB);
                InMessage.Time = TimeB.time;
                InMessage.MilliTime = TimeB.millitm;
                if(TimeB.dstflag)
                {
                    InMessage.MilliTime |= DSTACTIVE;
                }

                if(!status)
                {
                    if(OutMessage->Remote == CCUGLOBAL)
                    {
                        //For those who follow, this block is used for LCU's too.
                        if(OutMessage->EventCode & DTRAN)
                        {
                            /* Calculate the time the command should take */
                            if(OutMessage->EventCode & BWORD)
                                CTISleep (3000L);
                            else
                                CTISleep(1000L);
                        }
                        status = ClientErrors::None;
                    }
                    else if(OutMessage->Remote == RTUGLOBAL)
                    {
                        status = ClientErrors::None;
                    }
                    else
                    {
                        switch(Device->getType())
                        {
                        case TYPE_ILEXRTU:
                        case TYPE_WELCORTU:
                        case TYPE_SES92RTU:
                        case TYPE_LCU415:
                        case TYPE_LCU415LG:
                        case TYPE_LCU415ER:
                        case TYPE_LCUT3026:
                        case TYPE_TCU5000:
                        case TYPE_TCU5500:
                            {
                                InMessage.InLength = 0;
                                ReadLength = 0;

                                /* get the first 7 chars of the message */

                                trx.setInBuffer( InMessage.IDLCStat + 11 );
                                trx.setInCountExpected( PREIDLEN );
                                trx.setInCountActual( &(InMessage.InLength) );
                                trx.setInTimeout(OutMessage->TimeOut);
                                trx.setMessageStart();                          // This is the first "in" of this message
                                trx.setMessageComplete(0);                      // This is _NOT_ the last "in" of this message

                                status = Port->inMess(trx, Device, traceList);

                                if(!status)
                                {
                                    /* check out the message... How much follows */
                                    status = RTUReplyHeader (Device->getType(), Device->getAddress(), InMessage.IDLCStat + 11, &ReadLength);

                                    if(!status && ReadLength)
                                    {
                                        /* get the rest of the message */
                                        trx.setInBuffer( InMessage.Buffer.InMessage );
                                        trx.setInCountExpected( ReadLength );
                                        trx.setInCountActual( &ReadLength );
                                        trx.setInTimeout(OutMessage->TimeOut);
                                        trx.setMessageStart(0);                         // This is _NOT_ the first "in" of this message
                                        trx.setMessageComplete();                       // This is the last "in" of this message

                                        status = Port->inMess(trx, Device, traceList);
                                    }
                                }

                                InMessage.InLength += ReadLength;

                                if(!status)
                                {
                                    /* Check crc of message */
                                    status = RTUReply (InMessage.IDLCStat + 11, (USHORT)InMessage.InLength);
                                }

                                if(trx.doTrace(status))
                                {
                                    trx.setInBuffer(InMessage.IDLCStat + 11);
                                    trx.setInCountActual(&InMessage.InLength);
                                    Port->traceXfer(trx, traceList, Device, status);
                                }

                                break;
                            }
                        case TYPE_CCU700:
                        case TYPE_CCU710:
                        case TYPE_DAVIS:
                            {
                                InMessage.InLength = 0;
                                ReadLength = 0;

                                trx.setInBuffer( InMessage.IDLCStat + 11 );
                                trx.setInCountExpected( PREIDLEN );
                                trx.setInCountActual( &(InMessage.InLength) );
                                trx.setInTimeout(OutMessage->TimeOut);
                                trx.setMessageStart();                          // This is the first "in" of this message
                                trx.setMessageComplete(0);                      // This is _NOT_ the last "in" of this message

                                status = Port->inMess(trx, Device, traceList);

                                if(!status)
                                {
                                    /* check out the message... How much follows */
                                    status = RTUReplyHeader (Device->getType(), Device->getAddress(), InMessage.IDLCStat + 11, &ReadLength);

                                    if(!status && ReadLength)
                                    {
                                        /* get the rest of the message */
                                        trx.setInBuffer( InMessage.Buffer.InMessage );
                                        trx.setInCountExpected( ReadLength );
                                        trx.setInCountActual( &ReadLength );
                                        trx.setInTimeout(OutMessage->TimeOut);
                                        trx.setMessageStart(0);                          // This is _NOT_ the first "in" of this message
                                        trx.setMessageComplete();                        // This is the last "in" of this message

                                        status = Port->inMess(trx, Device, traceList);
                                    }
                                }
                                InMessage.InLength += ReadLength;

                                if(!status)
                                {
                                    /* Check crc of message */
                                    status = RTUReply (InMessage.IDLCStat + 11, (USHORT)InMessage.InLength);
                                }

                                if(trx.doTrace(status))
                                {
                                    trx.setInBuffer(InMessage.IDLCStat + 11);
                                    trx.setInCountActual(&InMessage.InLength);
                                    Port->traceXfer(trx, traceList, Device, status);
                                }


                                if(!status)
                                {
                                    //  Commented out on 2014-08-29
                                    //  Judging by the FIX FIX FIX comment above,
                                    //    I don't think we support CCU-700/-710 or
                                    //    the Davis weather station with IDLC wrap.
                                    //status = InMessage.Buffer.InMessage[2];
                                    status = ClientErrors::E2eProtocolUnsupported;
                                    InMessage.InLength = InMessage.Buffer.InMessage[1];
                                    if(InMessage.InLength)
                                    {
                                        ::memmove (InMessage.Buffer.InMessage, InMessage.Buffer.InMessage + 3, InMessage.InLength);
                                    }
                                }

                                break;
                            }
                        case TYPE_CCU711:
                            {
                                // CtiTransmitterInfo *pInfo = Device->getTrxInfo();
                                CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)Device->getTrxInfo();

                                /* get the first 5 bytes in the return message */
                                trx.setInBuffer( InMessage.IDLCStat );
                                trx.setInCountExpected( 5 );
                                trx.setInCountActual( &InMessage.InLength );

                                INT lto = OutMessage->TimeOut;
                                if(pInfo->FreeSlots < MAXQUEENTRIES && (OutMessage->EventCode & DTRAN))
                                {
                                    // If the CCU has queued work and we are sendind a DTRAN we need to allow for a QUEUE read to
                                    // complete prior to the DTRAN getting submitted.  Otherwise a timeout may happen.
                                    lto *= 2;   // Let's allow twice as much time in this instance.
                                }

                                trx.setInTimeout(lto);
                                trx.setMessageStart();                           // This is the first "in" of this message
                                trx.setMessageComplete(0);                       // This is NOT the last "in" of this message
                                trx.setCRCFlag(0);

                                status = Port->inMess(trx, Device, traceList);

                                if(!status)
                                {
                                    /* Oh wow, I got me five bytes of datum! */
                                    if( (InMessage.IDLCStat[1] >> 1) != Device->getAddress() )
                                    {
                                        status = ClientErrors::Address;
                                    }
                                    else if(InMessage.IDLCStat[2] & 0x01)    // Supervisory frame. Emetcon S-Spec Section 4.5
                                    {
                                        //  if it's a foreign CCU, we let them deal with the potential error
                                        if( OutMessage->MessageFlags & MessageFlag_PortSharing )
                                        {
                                            InMessage.InLength = 5;
                                        }
                                        else
                                        {
                                            /* Ack patooy What to do here now ya don't ya' know */
                                            switch(InMessage.IDLCStat[2] & 0x0f)
                                            {
                                                case REJ:
                                                {
                                                    if( YukonError_t reject_status = IDLCRej(InMessage.IDLCStat, &pInfo->RemoteSequence.Request) )
                                                    {
                                                        status = reject_status;
                                                    }
                                                    break;
                                                }
                                                default:
                                                {
                                                    CTILOG_INFO(dout, "Supervisory (Inbound) Message 0x"<< hex << (int)InMessage.IDLCStat[2] <<" from CCU: "<< Device->getName());

                                                    if(trx.doTrace(status))
                                                    {
                                                        trx.setInBuffer(InMessage.IDLCStat);
                                                        trx.setInCountActual(&InMessage.InLength);
                                                        Port->traceXfer(trx, traceList, Device, status);
                                                    }
                                                }

                                                /*
                                                 *  4/29/99 CGP There are others like Reset Acknowlege which may need to picked up here
                                                 */
                                            }
                                        }
                                    }
                                    else
                                    {
                                        ReadLength = 0;
                                        /* Go get the rest of the message */
                                        /* Got first byte following length.. Must add two for CRC */
                                        trx.setInBuffer( &InMessage.IDLCStat[5] );
                                        trx.setInCountExpected( InMessage.IDLCStat[3] + 1 );
                                        trx.setInCountActual( &ReadLength );
                                        trx.setInTimeout(OutMessage->TimeOut);
                                        trx.setMessageStart(FALSE);                           // This is the first "in" of this message
                                        trx.setMessageComplete(TRUE);                   // This is NOT the last "in" of this message

                                        status = Port->inMess(trx, Device, traceList);

                                        InMessage.InLength += ReadLength;

                                        if( !status && !(OutMessage->MessageFlags & MessageFlag_PortSharing) )
                                        {
                                            /*
                                             *  This is the guy who does some rudimentary checking on the CCU message
                                             *  He will return REQACK in that case...
                                             */
                                            status = GenReply (InMessage.IDLCStat,
                                                               InMessage.InLength,
                                                               &pInfo->RemoteSequence.Request,
                                                               &pInfo->RemoteSequence.Reply,
                                                               Device->getAddress(),
                                                               OutMessage->Command,
                                                               &pInfo->SequencingBroken);
                                        }
                                    }
                                }

                                if(status)
                                {
                                    if(InMessage.InLength >= 5)
                                    {
                                        if( YukonError_t reject_status = IDLCRej (InMessage.IDLCStat, &pInfo->RemoteSequence.Request) )
                                        {
                                            status = reject_status;
                                        }
                                    }
                                    else if(status == ClientErrors::ReadTimeout)
                                    {
                                        //  We have to assume the CCU got our request, even though we didn't hear the response.
                                        //
                                        //  To prevent any delayed response A from being interpreted as a response to request B,
                                        //    we must throw off the sequence numbers.
                                        //  We need to increment by two or more, because the CCU treats a single increment as an
                                        //    acknowledgement of its previous response - and since the CCU waits for an acknowledgement
                                        //    before clearing queue entries returned by an RCOLQ, that might result in a loss of data.
                                        //
                                        //  Note that this guarantees us a sequence adjust from the slave the next time we successfully
                                        //    communicate, but that is the small price that we pay for this safety.
                                        //
                                        //  The protocol-pure method would be to send a reset request to the CCU to absorb any spurious
                                        //    replies, but if the device is not communicating, any additional port queue entries for this
                                        //    CCU would generate even more timeouts (the "additional time out/extra timeout" delay
                                        //    is the biggest variable contributor here, especially for small messages like the reset).
                                        //  That isn't a problem for a single device on a comm channel, but it is a major problem
                                        //    for a comm channel with multiple devices.
                                        pInfo->adjustSequencingForTimeout();
                                    }
                                }

                                if(trx.doTrace(status))
                                {
                                    trx.setInBuffer(InMessage.IDLCStat);
                                    trx.setInCountActual(&InMessage.InLength);
                                    Port->traceXfer(trx, traceList, Device, status);
                                }

                                if(!status && !(OutMessage->MessageFlags & MessageFlag_PortSharing))
                                {
                                    InMessage.InLength -= 20;
                                }

                                if(PorterDebugLevel & PORTER_DEBUG_CCUMESSAGES)
                                {
                                    CtiProtocol711 ccu711;
                                    ccu711.setSlaveResponse( InMessage.IDLCStat );
                                    ccu711.describeSlaveResponse();
                                }

                                break;
                            }
                        default:
                            {
                                CTILOG_ERROR(dout, "Device Type "<< desolveDeviceType(Device->getType()) <<" Not specifically accounted for.");
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
                if( status == ClientErrors::ReqackFlagSet )
                {
                    OutMessage->Retry = 0;
                }

                break; /* IDLC */
            }
        default:
            {
                CTILOG_ERROR(dout, "Unknown Protocol Type ("<< protocolType <<")");
            }
        }

        DisplayTraceList(Port, traceList, true);
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Comms processing to "<< Device->getName() <<" was TERMINATED");
    }

    return status;
}

YukonError_t NonWrapDecode(const INMESS &InMessage, CtiDeviceSPtr &Device)
{
    YukonError_t status = ClientErrors::None;

    switch(Device->getType())
    {
    case TYPE_DAVIS:
        {
            if(InMessage.Buffer.InMessage[0] != 0x06 && InMessage.Buffer.InMessage[1] != 0x01)
            {
                status = ClientErrors::Framing;
            }
            else
            {
                /* Check the CRC */
                if(MAKEUSHORT (InMessage.Buffer.InMessage[18],
                               InMessage.Buffer.InMessage[17]) != CrcCalc_C ((InMessage.Buffer.InMessage + 2), 15))
                {
                    status = ClientErrors::BadCrc;
                }
            }

            break;
        }
    default:
        break;
    }

    return status;
}


YukonError_t CheckAndRetryMessage(YukonError_t CommResult, CtiPortSPtr Port, INMESS &InMessage, OUTMESS *&OutMessage, CtiDeviceSPtr &Device)
{
    YukonError_t   status = CommResult;
    ULONG          j;
    ULONG          QueueCount;
    INT port = OutMessage->Port;
    INT deviceID = OutMessage->DeviceID;
    INT targetID = OutMessage->TargetID;
    INT msgFlags = OutMessage->MessageFlags;

    if( (CommResult && CommResult != ClientErrors::PortSimulated) ||
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
                if(CommResult == ClientErrors::None)
                {
                    break;
                }
                else if((status = LCUResultDecode(OutMessage, InMessage, Device, CommResult, false)) == ClientErrors::RetrySubmitted)
                {
                    CTILOG_INFO(dout, Device->getName() <<" RETRY SUBMITTED");

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
                    if(PortManager.writeQueue(OutMessage))
                    {
                        CTILOG_ERROR(dout, "Could not write to queue for Port "<< OutMessage->Port);

                        delete OutMessage;
                        OutMessage = 0;

                        status =  ClientErrors::QueueWrite;
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

                        status = ClientErrors::RetrySubmitted;    // This makes us continue the loop!
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

        if((PorterDebugLevel & PORTER_DEBUG_COMMFAIL) && CommResult && CtiError::GetErrorType( CommResult ) == ERRTYPECOMM)
        {
            CTILOG_ERROR(dout, "Port "<< Port->getName() <<" has a COMM category error ("<< CommResult <<")");
        }

        // This tallies success/fail on the port.  The port decides when he has become questionable.
//         bool reportablechange = Port->adjustCommCounts(CommResult);     // returns true if there is a reportable change!
        bool reportablechange = false;
        if(OutMessage)
        {
            reportablechange = processCommStatus(CommResult, OutMessage->DeviceID, OutMessage->TargetID, OutMessage->Retry > 0, Device);
        }
        else
        {
            reportablechange = processCommStatus(CommResult, InMessage.DeviceID, InMessage.TargetID, false, Device);
        }

        if((PorterDebugLevel & PORTER_DEBUG_COMMFAIL) && reportablechange)
        {
            Cti::FormattedList logItems;
            logItems.add(std::string("Port ")   += Port->getName()   += " comm status") << (Port->isQuestionable() ? "QUESTIONABLE" : "GOOD");
            logItems.add(std::string("Device ") += Device->getName() += " comm status") << (Device->isCommFailed() ? "FAILED" : "GOOD");

            CTILOG_DEBUG(dout, "Comm status change (or timed report)"<<
                    logItems);
        }

        if(CommResult && portwasquestionable && status != ClientErrors::RetrySubmitted)
        {
            status = Port->requeueToParent(OutMessage);     // Return all queue entries to the processing parent.
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Exception on port "<< Port->getName());
    }

    if(status == ClientErrors::RetrySubmitted)
    {
        PorterStatisticsManager.newAttempt( port, deviceID, targetID, CommResult, msgFlags );
    }
    else if(CommResult == ClientErrors::PortSimulated)
    {
        // There is no retry submitted on a simulated port??
        if(isDebugLudicrous())
        {
            CTILOG_DEBUG(dout, "CommResult == ClientErrors::PortSimulated")
        }
    }
    else if( CommResult )
    {
        try
        {
            if(OutMessage && OutMessage->MessageFlags & MessageFlag_RequeueCommandOnceOnFail)
            {
                CtiOutMessage *NewOM = CTIDBG_new CtiOutMessage(*OutMessage);

                NewOM->Retry = 2;
                NewOM->MessageFlags &= ~MessageFlag_RequeueCommandOnceOnFail;

                CtiPort *prt = Port.get();
                prt->writeQueue(NewOM, PortThread);
                prt->setShouldDisconnect( TRUE );  //  The REQUEUE_CMD flag means hang up and try again, so hang up
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "port "<< Port->getName());
        }
    }

    return status;
}


YukonError_t DoProcessInMessage(YukonError_t CommResult, CtiPortSPtr Port, INMESS &InMessage, OUTMESS *OutMessage, CtiDeviceSPtr &Device)
{
    extern void blitzNexusFromQueue(HCTIQUEUE q, const StreamConnection *Nexus);
    extern void blitzNexusFromCCUQueue(CtiDeviceSPtr Device, const StreamConnection *Nexus);

    YukonError_t   status = ClientErrors::None;
    ULONG          j;
    struct timeb   TimeB;

    using namespace Cti::Protocols;
    using Cti::Devices::MctDevice;

    if( OutMessage )
    {
        //  CCU-721 DTRAN InMessage.ErrorCode is already set in the DeviceSingle loop in CommunicateDevice, so don't overwrite it here
        //    This could be extended to all devices who process their InMessages similarly in CommunicateDevice
        if( Device->getType() != TYPE_CCU721 )
        {
            InMessage.ErrorCode = CommResult;
        }

        switch(Device->getType())
        {
        case TYPE_CCU721:
            {
                if( (OutMessage->EventCode & BWORD) &&
                    (OutMessage->Buffer.BSt.IO & EmetconProtocol::IO_Read ) )
                {
                    if( ! CommResult && ! InMessage.ErrorCode )
                    {
                        if( const CtiDeviceSPtr temDevice = DeviceManager.getDeviceByID(InMessage.TargetID) )
                        {
                            if( DlcBaseDevice::dlcAddressMismatch(InMessage.Buffer.DSt, *temDevice ))
                            {
                                InMessage.ErrorCode = ClientErrors::WrongAddress;
                            }
                        }
                    }

                    if( InMessage.ErrorCode == ClientErrors::EWordReceived )
                    {
                        InMessage.Buffer.RepeaterError.Details =
                            findRepeaterInRouteByAddress(
                                OutMessage->Request.RouteID,
                                OutMessage->Request.RetryMacroOffset,
                                InMessage.Buffer.RepeaterError.ESt->echo_address);

                    }
                }

                status = InMessage.ErrorCode;

                if( ! CommResult && status )
                {
                    CommResult = status;
                }

                if( InMessage.TargetID != InMessage.DeviceID ) // The CCU itself is account for elsewhere
                {
                    addCommResult(InMessage.TargetID, InMessage.ErrorCode, false);
                }

                break;
            }
        case TYPE_CCU711:
            {
                if( OutMessage->MessageFlags & MessageFlag_PortSharing )
                {
                    break;
                }
                else
                {
                    CtiTransmitter711Info *p711info = (CtiTransmitter711Info *)Device->getTrxInfo();

                    if(OutMessage->Remote == CCUGLOBAL)
                    {
                        break;
                    }

                    InMessage.InLength = OutMessage->InLength;

                    /* Clear the RCOLQ flag if neccessary */
                    if(OutMessage->Command == CMND_RCOLQ)
                    {
                        p711info->clearStatus (INRCOLQ);
                    }

                    /* Clear a LGRPQ flag if neccessary */
                    if(OutMessage->Command == CMND_LGRPQ)
                    {
                        p711info->clearStatus(INLGRPQ);
                    }

                    if(OutMessage->EventCode & RCONT)
                    {
                        status = CCUResponseDecode (InMessage, Device, OutMessage);
                    }
                    else
                    {
                        j = InMessage.InLength;
                        InMessage.InLength = 0;
                        status = CCUResponseDecode (InMessage, Device, OutMessage);
                        InMessage.InLength = j;
                    }

                    if( status == ClientErrors::SocketWrite )
                    {
                        CTILOG_ERROR(dout, "CCUResponseDecode() failed");

                        blitzNexusFromQueue( Port->getPortQueueHandle(), OutMessage->ReturnNexus);
                        blitzNexusFromCCUQueue( Device, OutMessage->ReturnNexus);
                    }

                    //  only break if this is _not_ DTRAN
                    if( !(OutMessage->EventCode & DTRAN) )
                    {
                        break;
                    }
                }
            }
        case TYPE_CCU700:
        case TYPE_CCU710:
            {
                unsigned short nack1 = 0, nack2 = 0;

                if(CommResult)
                {
                    //  NOTE that if we have a comm error, we will break out of the switch statement at this point...
                    InMessage.Buffer.DSt.Time     = InMessage.Time;
                    InMessage.Buffer.DSt.DSTFlag  = InMessage.MilliTime & DSTACTIVE;

                    addCommResult(InMessage.TargetID, CommResult, OutMessage->Retry > 0);
                    break;
                }

                //  if this was targeted at an MCT
                if( OutMessage->TargetID != OutMessage->DeviceID && InMessage.DeviceID != 0 && InMessage.TargetID != 0 )
                {
                    if( !(status = NackTst(InMessage.Buffer.InMessage[0], &nack1, OutMessage->Remote)) &&
                        !(status = NackTst(InMessage.Buffer.InMessage[1], &nack2, OutMessage->Remote)) &&
                        !nack1 &&
                        !nack2 )
                    {
                        InMessage.InLength = OutMessage->InLength;

                        if( (OutMessage->EventCode & BWORD) &&
                            (OutMessage->Buffer.BSt.IO & EmetconProtocol::IO_Read ) )
                        {
                            DSTRUCT DSt;
                            ESTRUCT ESt;
                            BSTRUCT BSt;

                            /* This is I so decode dword(s) for the result */
                            CommResult = InMessage.ErrorCode = status = D_Words (InMessage.Buffer.InMessage + 3, InMessage.InLength - 3,  OutMessage->Remote, &DSt, &ESt, &BSt);

                            if( status == ClientErrors::EWordReceived )
                            {
                                InMessage.Buffer.RepeaterError.ESt = ESt;
                                InMessage.Buffer.RepeaterError.Details =
                                    findRepeaterInRouteByAddress(
                                        OutMessage->Request.RouteID,
                                        OutMessage->Request.RetryMacroOffset,
                                        ESt.echo_address);
                            }
                            else if( status == ClientErrors::BWordReceived )
                            {
                                Cti::FormattedList bWordDetails;

                                bWordDetails.add("Device name") << Device->getName();
                                bWordDetails.add("Device ID")   << Device->getID();
                                bWordDetails.add("Device type") << desolveDeviceType(Device->getType());

                                bWordDetails.add("DLC Address") << BSt.Address;
                                bWordDetails.add("Repeater fixed bits")    << BSt.DlcRoute.RepFixed;
                                bWordDetails.add("Repeater variable bits") << BSt.DlcRoute.RepVar;
                                bWordDetails.add("Function")   << BSt.Function;
                                bWordDetails.add("IO bits")    << BSt.IO;
                                bWordDetails.add("Word count") << BSt.Length;

                                CTILOG_WARN(dout, "B word received, possible interference from another transmitter"
                                                    << bWordDetails);
                            }
                            else
                            {
                                DSt.Time = InMessage.Time;
                                DSt.DSTFlag = InMessage.MilliTime & DSTACTIVE;
                                InMessage.Buffer.DSt = DSt;
                            }
                        }
                    }
                    else if( !status && nack1 )
                    {
                        status = InMessage.ErrorCode = ClientErrors::Abnormal;

                        CTILOG_ERROR(dout, "NACK received in CCU header from device \""<< Device->getName());
                    }
                    else if( !status && nack2 )
                    {
                        if( (OutMessage->EventCode & BWORD) &&
                            (OutMessage->Buffer.BSt.IO & EmetconProtocol::IO_Read) )
                        {
                            status = ClientErrors::Word1NackPadded;
                            InMessage.ErrorCode = ClientErrors::Word1NackPadded;
                        }

                        if( !(OutMessage->MessageFlags & (MessageFlag_AddMctDisconnectSilence |
                                                          MessageFlag_AddCcu711CooldownSilence)) )
                        {
                            CTILOG_WARN(dout, "repeaters expected but not heard");
                        }
                    }

                    if( !CommResult && !status )
                    {
                        if( const CtiDeviceSPtr temDevice = DeviceManager.getDeviceByID(InMessage.TargetID) )
                        {
                            if( DlcBaseDevice::dlcAddressMismatch(InMessage.Buffer.DSt, *temDevice) )
                            {
                                status = CommResult = ClientErrors::WrongAddress;
                                InMessage.ErrorCode = ClientErrors::WrongAddress;
                            }
                        }
                    }
                }

                if( !CommResult && status ) //We need to make sure we log the stats properly here
                {
                    CommResult = status;
                }

                addCommResult(InMessage.TargetID, CommResult, OutMessage->Retry > 0);

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
                        CtiTransmitterInfo *pInfo = Device->getTrxInfo();

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

                        CTILOG_TRACE(dout, "VCUWait");

                        // VCUTime (OutMessage, &pInfo->NextCommandTime);

                        UCTFTime (&TimeB);

                        pInfo->setNextCommandTime(pInfo->getNextCommandTime() + TimeB.time);

                        CTILOG_WARN(dout, "Function Incomplete");
                    }

                    /* Lets check if this command needs to be queued again */
                    if(!CommResult && (InMessage.Buffer.InMessage[4] & VCUOVERQUE) && !gIgnoreTCU5X00QueFull)
                    {
                        /* we need to reque this one  */
                        /* Drop the priority an notch so we don't hog the channel */
                        if(OutMessage->Priority) OutMessage->Priority--;

                        status = ClientErrors::RetrySubmitted;

                        /* Put it on the queue for this port */
                        if(PortManager.writeQueue(OutMessage))
                        {
                            CTILOG_ERROR(dout, "Could not requeue command for Port "<< OutMessage->Port);
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
                    if(!CommResult && (InMessage.Buffer.InMessage[4] & VCUOVERQUE) && !gIgnoreTCU5X00QueFull)
                    {
                        if(PorterDebugLevel & PORTER_DEBUG_VERSACOM)
                        {
                            CTILOG_DEBUG(dout, Device->getName() <<" queue full.  Will resubmit.");
                        }

                        /* we need to reque this one  */
                        status = ClientErrors::RetrySubmitted;

                        /* Put it on the queue for this port */
                        if(PortManager.writeQueue(OutMessage))
                        {
                            CTILOG_ERROR(dout, "Could not requeue command for Port "<< OutMessage->Port);
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
                    CtiTransmitterInfo *pInfo = Device->getTrxInfo();
                    pInfo->RemoteSequence.Reply = !pInfo->RemoteSequence.Reply;
                }

                break;
            }
        case TYPE_SERIESVLMIRTU:
            {
                if( CommResult )
                {
                    status = CommResult;
                }
                break;
            }
        case TYPE_ILEXRTU:
        case TYPE_SES92RTU:
        default:
            {
                break;
            }
        }
    }
    else
    {
        CTILOG_ERROR(dout, "Missing In or Out Message");
    }

    // Statistics processing.
    if( OutMessage )
    {
        if(status == ClientErrors::RetrySubmitted)
        {
            PorterStatisticsManager.newAttempt( OutMessage->Port, OutMessage->DeviceID, OutMessage->TargetID, CommResult, OutMessage->MessageFlags );
        }
        else
        {
            PorterStatisticsManager.newCompletion( OutMessage->Port, OutMessage->DeviceID, OutMessage->TargetID, CommResult, OutMessage->MessageFlags );
        }
    }
    else
    {
        if(status == ClientErrors::RetrySubmitted)
        {
            PorterStatisticsManager.newAttempt( InMessage.Port, InMessage.DeviceID, InMessage.TargetID, CommResult, InMessage.MessageFlags );
        }
        else
        {
            PorterStatisticsManager.newCompletion( InMessage.Port, InMessage.DeviceID, InMessage.TargetID, CommResult, InMessage.MessageFlags );
        }
    }

    return status;
}


Cti::Optional<repeater_info> findRepeaterInRouteByAddress( int routeId, const MacroOffset& retryMacroOffset, const unsigned echo_address )
{
    std::vector<long> repeaterIds;
    std::string routeName;

    {
        readers_writer_lock_t::reader_lock_guard_t lock{ RouteManager.getLock() };

        CtiRouteSPtr route = RouteManager.getRouteById(routeId);

        if (route->getType() == RouteTypeMacro)
        {
            Cti::Routes::MacroRouteSPtr macroRoute = boost::static_pointer_cast<Cti::Routes::MacroRoute>(route);

            const unsigned originalMacroOffset =
                (retryMacroOffset && *retryMacroOffset)
                ? *retryMacroOffset - 1
                : macroRoute->getSubrouteIds().size() - 1; // If retryMacroOffset is 0 or not set, assume it executed on the final macro route.

            if (CtiRouteSPtr subroute = macroRoute->getSubroute(originalMacroOffset))
            {
                route = subroute;

                routeId = route->getRouteID();
            }
        }

        if (route->getType() == RouteTypeCCU)
        {
            CtiRouteCCUSPtr ccuRoute = boost::static_pointer_cast<CtiRouteCCU>(route);

            const CtiRouteCCU::RepeaterSet &repeaters = ccuRoute->getRepeaters();

            boost::transform(repeaters, std::back_inserter(repeaterIds), [](auto &rpt) { return rpt.getDeviceID(); });

            routeName = route->getName();
        }
    }

    int rte_pos = 0;
    for (const auto repeaterId : repeaterIds)
    {
        rte_pos++;
        if (CtiDeviceSPtr repeater = DeviceManager.getDeviceByID(repeaterId))
        {
            if ((repeater->getAddress() & 0x1fff) == echo_address)
            {
                repeater_info details;

                details.route_id = routeId;
                strcpy_s(details.route_name, sizeof(details.route_name), routeName.c_str());
                details.repeater_id = repeater->getID();
                strcpy_s(details.repeater_name, sizeof(details.repeater_name), repeater->getName().c_str());
                details.route_position = rte_pos;
                details.total_stages = repeaterIds.size();

                return Cti::make_optional(details);
            }
        }
    }

    return Cti::Optional<repeater_info>::make_empty();
}


void SnipeDynamicInfo(const INMESS &ResultMessage)
{
    CtiDeviceSPtr dev = DeviceManager.getDeviceByID(ResultMessage.TargetID);

    boost::shared_ptr<Cti::Devices::MctDevice> mct = boost::dynamic_pointer_cast<Cti::Devices::MctDevice>(dev);

    if( mct )
    {
        mct->extractDynamicPaoInfo(ResultMessage);
    }
}


YukonError_t ReturnResultMessage(YukonError_t CommResult, INMESS &InMessage, OUTMESS *&OutMessage)
{
    YukonError_t status = ClientErrors::None;

    if( OutMessage )
    {
        if(OutMessage->EventCode & RESULT)         /* If the OutMessage indicates it this routine responds to the client */
        {
            InMessage.ErrorCode = CommResult;

            if( CommResult )
            {
                status = SendError( OutMessage, CommResult, &InMessage );
            }
            else
            {
                /* send message back to originating process */
                if(OutMessage->ReturnNexus != NULL)
                {
                    //  This won't be decoded in Porter, so grab the dynamic info
                    if(OutMessage->ReturnNexus != &PorterToPil)
                    {
                        SnipeDynamicInfo(InMessage);
                    }

                    boost::optional<std::string> writeError;

                    try
                    {
                        const size_t bytesWritten = OutMessage->ReturnNexus->write(&InMessage, sizeof(INMESS), Chrono::seconds(15));
                        if( bytesWritten != sizeof(INMESS) )
                        {
                            std::string reason = Cti::StreamBuffer() <<"Timeout (Wrote "<< bytesWritten <<"/"<< sizeof(INMESS) <<" bytes)";

                            writeError = "";
                            writeError->swap(reason);
                        }
                    }
                    catch( const StreamConnectionException &ex )
                    {
                        writeError = ex.what();
                    }

                    if( writeError )
                    {
                        CTILOG_ERROR(dout, "Could not write to the Return Nexus"<<
                                endl <<"DeviceID "<< OutMessage->DeviceID <<" TargetID "<< OutMessage->TargetID <<" Command "<< OutMessage->Request.CommandStr <<
                                endl <<"Reason: "<< *writeError);

                        status = ClientErrors::SocketWrite;
                    }
                }
            }
        }
    }
    else
    {
        CTILOG_ERROR(dout, "Missing In or Out Message");
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
void RequeueReportError(YukonError_t status, OUTMESS *OutMessage, INMESS *InMessage)
{
    switch(status)
    {
    case ClientErrors::RetrySubmitted:
        {
            break;  // Don't call this an error.
        }
    case ClientErrors::QueuedToDevice:
        {
            break;
        }
    case ClientErrors::ContinueLoop:
        {
            break;
        }
    default:
        {
            SendError(OutMessage, status, InMessage);   // This call frees the OUTMESS upon completion
            break;
        }
    }
}


YukonError_t ValidateDevice(CtiPortSPtr Port, CtiDeviceSPtr &Device, OUTMESS *&OutMessage)
{
    YukonError_t status = ClientErrors::None;

    if(!isForeignCcuPort(Device->getPortID()) && Device->getAddress() != 0xffff && !Device->isInhibited())
    {
        if( Device->hasTrxInfo() ) // Does this device type support TrxInfo?
        {
            CtiTransmitterInfo *pInfo = Device->getTrxInfo();

            /* Before we do anything else make damn sure we are not a protection violation */
            if(pInfo != NULL && pInfo->getStatus(NEEDSRESET))
            {
                /* Go Ahead an start this one up */
                if( RemoteReset(Device, Port) )
                {
                    //  Requeue the original OM.  Writing at Priority + 1 effectively
                    //    puts it at the head of its priority so it will be examined next
                    if(Port->writeQueueWithPriority(OutMessage, std::min<int>(MAXPRIORITY, OutMessage->Priority + 1), PortThread))
                    {
                        CTILOG_ERROR(dout, "Could not replace entry onto Queue");

                        delete OutMessage;
                    }

                    OutMessage = 0;
                    Sleep(100L);

                    status = ClientErrors::RetrySubmitted;

                }
            }
        }
        else if( Device->getType() == TYPE_CCU721 )
        {
            /* Go Ahead an start this one up */
            if( boost::static_pointer_cast<Cti::Devices::Ccu721Device>(Device)->needsReset()
                && RemoteReset(Device, Port) )
            {
                //  Requeue the original OM.  Writing at Priority + 1 effectively
                //    puts it at the head of its priority so it will be examined next
                if(Port->writeQueueWithPriority(OutMessage, std::min<int>(MAXPRIORITY, OutMessage->Priority + 1), PortThread))
                {
                    CTILOG_ERROR(dout, "Could not replace entry onto Queue");

                    delete OutMessage;
                }

                OutMessage = 0;
                Sleep(100L);

                status = ClientErrors::RetrySubmitted;

            }
        }
    }

    return status;
}

YukonError_t InitializeHandshake (CtiPortSPtr aPortRecord, CtiDeviceSPtr dev, list< CtiMessage* > &traceList)
{
    CtiXfer        transfer;
    BYTE           inBuffer[512];
    BYTE           outBuffer[256];
    YukonError_t   status = ClientErrors::None;
    ULONG          bytesReceived (0);

    CtiDeviceIED *aIEDDevice = (CtiDeviceIED *)dev.get();

    // initialize the transfer structure
    transfer.setInBuffer( inBuffer );
    transfer.setOutBuffer( outBuffer );
    transfer.setInCountActual( &bytesReceived );
    transfer.setTraceMask(TraceFlag, TraceErrorsOnly);

    while( status == ClientErrors::None &&
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
    if(status == ClientErrors::None && aIEDDevice->getCurrentState() == CtiDeviceIED::StateHandshakeAbort)
    {
        status = ClientErrors::Abnormal;
    }

    return status;
}

YukonError_t PerformRequestedCmd ( CtiPortSPtr aPortRecord, CtiDeviceSPtr dev, const INMESS *aInMessage, OUTMESS *aOutMessage, list< CtiMessage* > &traceList)
{
    YukonError_t   status = ClientErrors::None;
    CtiXfer        transfer;
    BYTE           inBuffer[2048];
    BYTE           outBuffer[2048];
    ULONG          bytesReceived=0;

    INT            infLoopPrevention = 0;

    #ifdef _DEBUG
    memset(inBuffer, 0, sizeof(inBuffer));
    #endif

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
                if( aInMessage )
                {
                    status = ReturnLoadProfileData ( aPortRecord, dev, *aInMessage, aOutMessage, traceList);
                }
            }

            if(aOutMessage) processCommStatus(status, aOutMessage->DeviceID, aOutMessage->TargetID, aOutMessage->Retry > 0, dev);

            if( status )
            {
                CTILOG_ERROR(dout, aIED->getName() <<" status = "<< status <<" "<< CtiError::GetErrorString( status ));
            }

            if(!(++infLoopPrevention % INF_LOOP_COUNT))  // If we go INF_LOOP_COUNT loops we're considering this infinite...
            {
                CTILOG_WARN(dout, "Possible infinite loop on device "<< aIED->getName() <<" - breaking loop, forcing abort state.");

                status = ClientErrors::Abnormal;
            }

            DisplayTraceList(aPortRecord, traceList, true);

        } while( status == ClientErrors::None &&
                 !((aIED->getCurrentState() == CtiDeviceIED::StateScanAbort) ||
                   (aIED->getCurrentState() == CtiDeviceIED::StateScanComplete)));

        if( (status == ClientErrors::None && aIED->getCurrentState() == CtiDeviceIED::StateScanAbort) ||
            (PorterDebugLevel & PORTER_DEBUG_VERBOSE && status) )
        {
            CTILOG_WARN(dout, aIED->getName() <<" status was returned as "<< status <<".  This may be a ied state or an actual error status. - State set to abort");

            status = ClientErrors::Abnormal;
        }

        DisplayTraceList(aPortRecord, traceList, true);

    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Port "<< aPortRecord->getName());
    }

    return status;
}



YukonError_t ReturnLoadProfileData ( CtiPortSPtr aPortRecord, CtiDeviceSPtr dev, const INMESS &aInMessage, OUTMESS *aOutMessage,  list< CtiMessage* > &traceList)
{
    YukonError_t status = ClientErrors::None;
    INMESS MyInMessage;

    CtiDeviceIED *aIED = (CtiDeviceIED *)dev.get();

    // need a copy of the read message
    ::memcpy(&MyInMessage, &aInMessage, sizeof(INMESS));
    MyInMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[0] = CtiDeviceIED::CmdLoadProfileData;
    MyInMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[1] = aIED->getCurrentState();

    // move the load profile block into the inmessage copy
    aIED->copyLoadProfileData ((BYTE*)&MyInMessage.Buffer.DUPSt.DUPRep.Message, MyInMessage.InLength);

    /* send message back to originating process */
    if( aOutMessage->ReturnNexus->isValid() )
    {
        boost::optional<std::string> writeError;

        try
        {
            const size_t bytesWritten = aOutMessage->ReturnNexus->write(&MyInMessage, sizeof(INMESS), Chrono::seconds(15));
            if( bytesWritten != sizeof(INMESS) )
            {
                std::string reason = Cti::StreamBuffer() <<"Timeout (Wrote "<< bytesWritten <<"/"<< sizeof(INMESS) <<" bytes)";

                writeError = "";
                writeError->swap(reason);
            }
        }
        catch( const StreamConnectionException &ex )
        {
            writeError = ex.what();
        }

        if( writeError )
        {
            CTILOG_ERROR(dout, "Could not write to the Return Nexus"<<
                    endl <<"Reason: "<< *writeError);

            aIED->setCurrentState(CtiDeviceIED::StateScanAbort);
            status = ClientErrors::SocketWrite;
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


YukonError_t LogonToDevice( CtiPortSPtr aPortRecord, CtiDeviceSPtr dev, INMESS &aInMessage, OUTMESS *aOutMessage, list< CtiMessage* > &traceList)
{
    YukonError_t retCode = ClientErrors::None;
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

        retCode = PerformRequestedCmd (aPortRecord, dev, &aInMessage, aOutMessage, traceList);

        // we're ok so try do the handshake
        if(!retCode)
        {
            // back to the handshake
            aIED->setCurrentState (CtiDeviceIED::StateHandshakeInitialize);
            i = InitializeHandshake (aPortRecord, dev, traceList);

            // finishes the switch command
            if(!i)
                retCode = PerformRequestedCmd (aPortRecord, dev, &aInMessage, aOutMessage, traceList);
        }

        aIED->setCurrentState (CtiDeviceIED::StateHandshakeComplete);
        aIED->setCurrentCommand((CtiDeviceIED::CtiMeterCmdStates_t) previousCmd );

    }
    else
        retCode = InitializeHandshake (aPortRecord, dev, traceList);
    return retCode;
}

YukonError_t TerminateHandshake (CtiPortSPtr aPortRecord, CtiDeviceSPtr dev, list< CtiMessage* > &traceList)
{
    CtiXfer        transfer;
    BYTE           inBuffer[512];
    BYTE           outBuffer[256];
    YukonError_t   status = ClientErrors::None;
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

    } while( status == ClientErrors::None &&
             !((aIEDDevice->getCurrentState() == CtiDeviceIED::StateAbort) ||
               (aIEDDevice->getCurrentState() == CtiDeviceIED::StateComplete) ||
               (aIEDDevice->getCurrentState() == CtiDeviceIED::StateCompleteNoHUP)));

    // check our return
    if(status == ClientErrors::None)
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
        else if( aIEDDevice->getCurrentState() == CtiDeviceIED::StateComplete   // The device did the disconnect.
              || aIEDDevice->getCurrentState() == CtiDeviceIED::StateAbort)
        {
            switch(aIEDDevice->getType())
            {
            case TYPE_TAPTERM:
                {
                    // Since an EOT was done on this port by this device, all devices on this port need to logon the next loop.
                    // Sweep the port and tag them so.
                    vector<CtiDeviceManager::ptr_type> devices;

                    DeviceManager.getDevicesByPortID(aPortRecord->getPortID(), devices);

                    for_each(devices.begin(), devices.end(), TAPNeedsLogon());

                    break;
                }
            }
        }
    }

    return status;
}

bool deviceCanSurviveThisStatus(INT status)
{
    bool survive = false;

    switch(status)
    {
    case ClientErrors::None:
    case ClientErrors::NoDcd:
    case ClientErrors::BadCrc:
    case ClientErrors::ReadTimeout:
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
                ::memmove (OutMessage->Buffer.OutMessage + 7, OutMessage->Buffer.OutMessage + 10, OutMessage->OutLength - 3);
            }
        }
    }
}

INT GetPreferredProtocolWrap( const CtiPortSPtr &Port, const CtiDeviceSPtr &Device )
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
                CtiDeviceManager::coll_type::reader_lock_guard_t find_dev_guard(DeviceManager.getLock());
                qEnt = SearchQueue( Port->getPortQueueHandle(), NULL, findExclusionFreeOutMessage, false );
            }

            if(qEnt > 0)
            {
                BYTE           ReadPriority;

                Port->setQueueSlot(qEnt);
                if(Port->readQueue( &ReadLength, (PPVOID)&pOutMessage, DCWW_NOWAIT, &ReadPriority, &QueueCount))
                {
                    CTILOG_ERROR(dout, "Could not read from port queue");
                }
                else
                {
                    if(PortManager.writeQueueWithPriority(pOutMessage, MAXPRIORITY - 2))
                    {
                        Cti::FormattedList loglist;
                        loglist.add("Port")     << pOutMessage->Port;
                        loglist.add("DeviceID") << pOutMessage->DeviceID;
                        loglist.add("TargetID") << pOutMessage->TargetID;

                        CTILOG_ERROR(dout, "Could not shuffle the queue.  Message lost"<<
                                loglist);

                        delete pOutMessage;
                        pOutMessage = NULL;
                    }

                    /* The OUTMESS that got us here is always examined next again! */
                    if(PortManager.writeQueueWithPriority(OutMessage, MAXPRIORITY - 2))
                    {
                        Cti::FormattedList loglist;
                        loglist.add("Port")     << pOutMessage->Port;
                        loglist.add("DeviceID") << pOutMessage->DeviceID;
                        loglist.add("TargetID") << pOutMessage->TargetID;

                        CTILOG_ERROR(dout, "Could not shuffle the queue.  CONTROL Message lost"<<
                                loglist);

                        delete OutMessage;
                        OutMessage = NULL;
                    }
                    else
                    {
                        CTILOG_INFO(dout, Port->getName() <<" OutMessage Swap!");

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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "port "<< shPort->getName());
    }

    return bSubstitutionMade;
}


BOOL findNonTimesyncOutMessage(void *data, void* d)
{
    OUTMESS  *OutMessage = (OUTMESS *)d;

    return OutMessage && !(OutMessage->EventCode & TSYNC);
}

BOOL findExclusionFreeNonTimesyncOutMessage(void *data, void *d)
{
    return findNonTimesyncOutMessage(data, d) && findExclusionFreeOutMessage(data, d);
}

/*
 *  Used by SearchQueue.  Must be protected appropriately.
 */
BOOL findExclusionFreeOutMessage(void *data, void* d)
{
    BOOL     mayExecute = FALSE;
    OUTMESS  *OutMessage = (OUTMESS *)d;

    CtiDeviceManager::device_priorities_t *excluded_device_priorities = reinterpret_cast<CtiDeviceManager::device_priorities_t *>(data);

    try
    {
        if(OutMessage && gConfigParms.isTrue("PORTER_EXCLUSION_TEST") )     // Indicates an excludable message!
        {
            CtiDeviceSPtr Device = DeviceManager.getDeviceByID( OutMessage->DeviceID );

            if(Device)
            {
                CtiTablePaoExclusion exclusion;

                if( DeviceManager.mayDeviceExecuteExclusionFree(Device, OutMessage->Priority, exclusion) )
                {
                    mayExecute = TRUE;     // This device is locked in as executable!!!
                    Device->setExecuting();
                }
                else if( excluded_device_priorities )  //  are we tracking priorities?
                {
                    (*excluded_device_priorities)[OutMessage->DeviceID] = max((*excluded_device_priorities)[OutMessage->DeviceID], OutMessage->Priority);
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Device is Null");
            }
        }
        else
        {
            mayExecute = TRUE; // We can send anything which says it is non-excludable!
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return mayExecute;
}


YukonError_t ProcessExclusionLogic(CtiPortSPtr Port, OUTMESS *&OutMessage, CtiDeviceSPtr Device)
{
    YukonError_t status = ClientErrors::None;
    static const string PORTER_EXCLUSION_TEST("PORTER_EXCLUSION_TEST");
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
        if(OutMessage->MessageFlags & MessageFlag_ApplyExclusionLogic || gConfigParms.isTrue(PORTER_EXCLUSION_TEST) )
        {
            CtiTablePaoExclusion exclusion;

            if( !DeviceManager.mayDeviceExecuteExclusionFree(Device, OutMessage->Priority, exclusion) )
            {
                // There is an exclusion conflict for this device.  It cannot execute this OM.
                DeviceManager.removeInfiniteExclusion(Device);  // Remove any infinite time exclusions caused by Device from any other device in the list.

                // Decide how to requeue the failed OM.
                status = OutMessageRequeueOnExclusionFail(Port, OutMessage, Device, exclusion);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "port "<< Port->getName());
    }
    // 030503 CGP END Exclusion logic.

    return status;
}


INT ProcessPortPooling(CtiPortSPtr Port)
{
    INT status = ClientErrors::None;

    Port->postEvent();

    if( Port->getParentPort() )
    {
        // Make sure the parent port can assign new work onto this port.
        if(Port->queueCount() == 0 && Port->getPoolAssignedGUID() != 0)
        {
            Cti::StreamBuffer output;
            output <<"Child port "<< Port->getName() <<" relinquishing GUID assignment";

            if(Port->getConnectedDevice())
            {
                output <<". Connected To: "<< GetDeviceName(Port->getConnectedDevice());
            }

            CTILOG_INFO(dout, output);

            Port->setPoolAssignedGUID(0);
        }
    }

    return status;
}


struct commFailDevice
{
    void operator()( CtiDeviceSPtr &device )
    {
        bool commsuccess = false;
        if( device->adjustCommCounts(commsuccess, false) )
        {
            commFail(device);
        }
    }
};

/*
 *  This function sets up and or resets the portthread's comm port or ip port.
 *  It is responsible for opening, or reopening the comm channel or IP channel.
 *  It is responsible for resetting, or verifying any established connection from the last loop.
 */
YukonError_t ResetChannel(CtiPortSPtr &Port, CtiDeviceSPtr &Device)
{
    YukonError_t status = ClientErrors::None;

    if( ClientErrors::None != (status = ResetCommsChannel(Port, Device)) )
    {
        if(!Port->isInhibited())
        {
            if(PorterDebugLevel & PORTER_DEBUG_COMMFAIL)
            {
                CTILOG_DEBUG(dout, "Port: "<< Port->getPortID() <<" / "<< Port->getName() <<" comm failing all attached comm status points");
            }

            vector<CtiDeviceSPtr> devices;

            DeviceManager.getDevicesByPortID(Port->getPortID(), devices);

            for_each(devices.begin(), devices.end(), commFailDevice());
        }

        CTILOG_WARN(dout, "Port "<< Port->getName() <<" will not init. Waiting 15 seconds");

        if( Device && Device->hasExclusions() )
        {
            DeviceManager.removeInfiniteExclusion(Device);
        }

        if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 15000L) )
        {
            PorterQuit = TRUE;
        }

        status = ClientErrors::ContinueLoop;     // make callee continue.
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
    INT status = ClientErrors::None;

    if(OutMessage != 0)
    {
        if(OutMessage->DeviceID == 0 && OutMessage->Remote != 0 && OutMessage->Port != 0)
        {
            if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
            {
                CTILOG_DEBUG(dout, "looking for new deviceID with Port "<< OutMessage->Port <<", Remote "<< OutMessage->Remote);
            }

            Device = DeviceManager.RemoteGetPortRemoteEqual(OutMessage->Port, OutMessage->Remote);

            if( Device )
            {
                OutMessage->DeviceID = Device->getID();

                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {
                    CTILOG_DEBUG(dout, "assigned new deviceID "<< Device->getID() <<" with Port "<< OutMessage->Port <<", Remote "<< OutMessage->Remote);
                }
            }
            else
            {
                if( PorterDebugLevel & PORTER_DEBUG_VERBOSE )
                {
                    CTILOG_DEBUG(dout, "could not assign new deviceID with Port "<< OutMessage->Port <<", Remote "<< OutMessage->Remote);
                }

                SendError(OutMessage, ClientErrors::PortRemoteNotFound);
                status = ClientErrors::ContinueLoop;
            }
        }
        else if( !Device || Device->getID() != OutMessage->DeviceID )
        {
            /* get the device record for this id */
            Device = DeviceManager.getDeviceByID(OutMessage->DeviceID);

            if(!Device)
            {
                CTILOG_WARN(dout, "Port "<< Port->getName() <<" just received a message for device id "<< OutMessage->DeviceID <<
                        endl <<"Porter does not seem to know about him and is throwing away the message!");

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
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "port "<< Port->getName());
                }

                status = ClientErrors::ContinueLoop;
            }
        }
    }
    else
    {
        Device.reset();

        status = ClientErrors::ContinueLoop;
    }

    return status;
}


void analyzePortQueue(CtiPortSPtr Port, bool timesyncPreference, CtiDeviceManager::device_priorities_t maxExcludedDevicePriorities)
{
    //  if the queue slot has already been set, we don't need to do anything
    if( Port->getQueueSlot() != 0 )
    {
        return;
    }

    int slot = 0;

    CtiDeviceManager::coll_type::reader_lock_guard_t find_dev_guard(DeviceManager.getLock());

    void *maxex = reinterpret_cast<void *>(&maxExcludedDevicePriorities);

    //  if we prefer non-timesync messages, try to find one
    if( !timesyncPreference )
    {
        slot = Port->searchQueue(maxex, findExclusionFreeNonTimesyncOutMessage);
    }

    //  if we didn't find a non-exclusion non-timesync, just look for a non-exclusion message
    if( !slot )
    {
        slot = Port->searchQueue(maxex, findExclusionFreeOutMessage);
    }

    //  if we found something, set the queue slot
    if( slot )
    {
        Port->setQueueSlot(slot);
    }
}


YukonError_t GetWork(CtiPortSPtr Port, CtiOutMessage *&OutMessage, ULONG &QueEntries, bool timesyncPreference)
{
    YukonError_t status = ClientErrors::None;
    ULONG ReadLength;
    BYTE ReadPriority;

    OutMessage = 0;         // Don't let us return with a bogus value!

    CtiDeviceManager::device_priorities_t maxExcludedDevicePriorities;

    //  Set the port queue slot to the next eligible work element we want to read
    analyzePortQueue(Port, timesyncPreference, maxExcludedDevicePriorities);

    /*
     *  This is a Read from the CTI queueing structures which will originate from
     *  some other requestor.  This is where this thread blocks and waits if there are
     *  no entries on the queue.  Note that the readQueue call allocs space for the
     *  OutMessage pointer, and fills it from it's queue entries!
     */

    if( const int queueStatus = Port->readQueue(&ReadLength, (PPVOID) &OutMessage, DCWW_NOWAIT, &ReadPriority, &QueEntries) )
    {
        if(queueStatus == ERROR_QUE_EMPTY)
        {
            if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 250L) )
            {
                PorterQuit = TRUE;
            }
        }
        else if( queueStatus != ERROR_QUE_UNABLE_TO_ACCESS)
        {
            CTILOG_ERROR(dout, "Could not read from port queue");
        }

        status = ClientErrors::ContinueLoop;
    }

    //  if the OM we just got was an exclusion OM, we didn't get into the findExclusionFreeOutMessage call,
    //    so we note this one's priority in case it's excluded
    if(OutMessage && OutMessage->MessageFlags & MessageFlag_ApplyExclusionLogic)
    {
        maxExcludedDevicePriorities[OutMessage->DeviceID] = max(OutMessage->Priority, maxExcludedDevicePriorities[OutMessage->DeviceID]);
    }

    //  inform the device manager/exclusion logic about the priorities waiting on this port
    DeviceManager.setDevicePrioritiesForPort(Port->getPortID(), maxExcludedDevicePriorities);

    if(OutMessage)
    {
        Port->incQueueSubmittal();
    }

    return status;
}


YukonError_t OutMessageRequeueOnExclusionFail(CtiPortSPtr &Port, OUTMESS *&OutMessage, CtiDeviceSPtr &Device, CtiTablePaoExclusion &exclusion)
{
    YukonError_t status = ClientErrors::None;

    switch(exclusion.getFunctionRequeue())              // Determine what to do with this OM based upon the exclusion which BLOCKED us?
    {
    case (CtiTablePaoExclusion::RequeueNextExecutableOM):
        {
            if( ShuffleQueue( Port, OutMessage, Device ) )
            {
                // Queue has been shuffled!  OutMessage is no longer ours to touch..
                if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
                {
                    CTILOG_DEBUG(dout, "An excluded OutMessage was bypassed in favor of a non-excluded OutMessage.");
                }
            }
            else
            {
                if(isDebugLudicrous() && (getDebugLevel() & DEBUGLEVEL_EXCLUSIONS))
                {
                    CTILOG_DEBUG(dout, Port->getName() <<" queue unable to be shuffled.  No non-excluded OutMessages exist.");
                }

                if(Port->writeQueue(OutMessage, PortThread))
                {
                    CTILOG_ERROR(dout, "Could not replace entry onto Queue");

                    delete OutMessage;
                }

                OutMessage = 0;
                Sleep(500L);
            }

            status = ClientErrors::RetrySubmitted;
            break;
        }
    case (CtiTablePaoExclusion::RequeueThisCommandNext):
        {
            // Keep this ONE HIGH HIGH PRIORITY.
            OutMessage->Priority = MAXPRIORITY - 1;

            if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
            {
                CTILOG_DEBUG(dout, "Re-queuing original (excluded) message at high priority to examine next");
            }
            // FALL THROUGH!
        }
    case (CtiTablePaoExclusion::RequeueQueuePriority):
    default:
        {
            if(Port->writeQueue(OutMessage, PortThread))
            {
                CTILOG_ERROR(dout, "Could not replace entry onto Queue");

                delete OutMessage;
            }

            OutMessage = 0;
            Sleep(100L);

            status = ClientErrors::RetrySubmitted;
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
        CTILOG_INFO(dout, "Additional RIPPLE'd queue entry found for port "<< Port->getName());

        BYTE ReadPriority;

        Port->setQueueSlot(slot);
        if(Port->readQueue( &ReadLength, (PPVOID)&match, DCWW_NOWAIT, &ReadPriority, &QueueCount))
        {
            CTILOG_ERROR(dout, "Could not read from port queue");

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

bool processCommStatus(INT CommResult, LONG DeviceID, LONG TargetID, bool RetryGTZero, const CtiDeviceSPtr &Device)
{
    bool status = false;

    bool iscommfailed = (CommResult == ClientErrors::None);      // Prime with the communication status

    if(Device->adjustCommCounts( iscommfailed, RetryGTZero ))
    {
        commFail(Device);
        status = true;
    }

    USHORT deviceType = Device->getType();
    if(TargetID != 0 && TargetID != DeviceID &&
       deviceType != TYPE_CCU700 && deviceType != TYPE_CCU710 && deviceType != TYPE_CCU711 && deviceType != TYPE_CCU721 )
    {
        // In this case, we need to account for the fail on the target device too..
        CtiDeviceSPtr pTarget = DeviceManager.getDeviceByID( TargetID );

        if(pTarget)
        {
            iscommfailed = (CommResult == ClientErrors::None);

            if( pTarget->adjustCommCounts( iscommfailed, RetryGTZero ) )
            {
                commFail(pTarget);
                status = true;
            }
        }
    }

    return status;
}

bool isTimedOut( const CtiTime &start_time, const unsigned int &duration_seconds)
{
    CtiTime now;
    return now > start_time + duration_seconds;
}

//Returns the next time expiration should be checked. Expiration always occurs at
//23:04:00.
void getNextExpirationTime(LONG timesPerDay, CtiTime &time)
{
    CtiTime now;
    LONG secondsBetweenExpiration = 24*60*60;//seconds in a day.
    if( timesPerDay > 0 )
    {
        secondsBetweenExpiration = secondsBetweenExpiration/timesPerDay;
        if( secondsBetweenExpiration <= 0 )
        {
            secondsBetweenExpiration = 1;
        }
    }

    time = CtiTime(23, 04, 0); //The default is 11:04 PM

    //We need to move our time back, once we are < now, add one time interval.
    if( time > now )
    {
        while( time > now )
        {
            time.addSeconds(-1*secondsBetweenExpiration);
        }

        time.addSeconds(secondsBetweenExpiration);
    }
    else if( time <= now )
    {
        while( time <= now )
        {
            time.addSeconds(secondsBetweenExpiration);
        }
    }
}

UINT purgeExpiredQueueEntries(CtiPort& port)
{
    extern void cleanupExpiredOutMessages(void *unusedptr, void* d);

    const CtiTime now;

    int entries = CleanQueue(port.getPortQueueHandle(), (void *)&now, findExpiredOutMessage, cleanupExpiredOutMessages);

    for( const auto deviceId : port.getQueuedWorkDevices() )
    {
        if( auto tempDev = DeviceManager.getDeviceByID(deviceId) )
        {
            if( auto queueInterface = tempDev->getDeviceQueueHandler() )
            {
                auto omList = queueInterface->retrieveQueueEntries(findExpiredOutMessage, (void *)&now);
                
                entries += omList.size();
                    
                for( auto outmess : omList ) 
                {
                    cleanupExpiredOutMessages(0, outmess);
                }
            }
        }
    }

    return entries;
}
