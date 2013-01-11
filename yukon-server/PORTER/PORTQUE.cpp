#include "precompiled.h"

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "cparms.h"
#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "porter.h"
#include "elogger.h"
#include "thread_monitor.h"
#include "ThreadStatusKeeper.h"

#include "portglob.h"
#include "portdecl.h"
#include "StatisticsManager.h"
#include "c_port_interface.h"
#include "mgr_device.h"
#include "mgr_port.h"
#include "port_shr.h"
#include "ctilocalconnect.h"

#include "logger.h"
#include "guard.h"
#include "trx_711.h"
#include "dev_ccu.h"
#include "dev_ccu721.h"
#include "prot_emetcon.h"

using namespace std;
using Cti::ThreadStatusKeeper;
using Cti::Porter::PorterStatisticsManager;

extern CtiPortManager            PortManager;
extern map<long, CtiPortShare *> PortShareManager;

extern CtiLocalConnect<INMESS, OUTMESS> PorterToPil;

extern HCTIQUEUE *QueueHandle(LONG pid);
extern bool addCommResult(long deviceID, bool wasFailure, bool retryGtZero);
extern void SnipeDynamicInfo(const INMESS &ResultMessage);

static const ULONG MAX_CCU_QUEUE_TIME = 1800;
static const ULONG QUEUED_MSG_REQ_ID_BASE = 0xFFFFFF00;
USHORT QueSequence = 0x8000;

static CHAR tempstr[100];

bool findAllQueueEntries(void *unused, void *d);
bool findReturnNexusMatch(void *nid, void *d);
void cleanupOrphanOutMessages(void *unusedptr, void* d);
void cancelOutMessages(void *doSendError, void* om);
int  ReturnQueuedResult(CtiDeviceSPtr Dev, CtiTransmitter711Info *pInfo, USHORT QueTabEnt);

void blitzNexusFromQueue(HCTIQUEUE q, CtiConnect *&Nexus)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Attempting to remove all queue entries for bad nexus 0x" << Nexus << endl;
    }
    CleanQueue( q, (void*)Nexus, findReturnNexusMatch, cleanupOrphanOutMessages );
}

void blitzNexusFromCCUQueue(CtiDeviceSPtr Device, CtiConnect *&Nexus)
{
    if(Device && !isForeignCcuPort(Device->getPortID()) && Device->getType() == TYPE_CCU711)
    {
        CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)Device->getTrxInfo();

        if(pInfo)
        {
            ULONG QueEntCnt;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Attempting to remove all queue entries for bad nexus 0x" << Nexus << endl;
            }

            QueryQueue(pInfo->QueueHandle, &QueEntCnt);
            if(QueEntCnt)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "    CCU:  " << Device->getName() << "  PURGING " << QueEntCnt << " queue queue entries" << endl;
                }
                CleanQueue( pInfo->QueueHandle, (void*)Nexus, findReturnNexusMatch, cleanupOrphanOutMessages );
            }

            QueryQueue(pInfo->ActinQueueHandle, &QueEntCnt);
            if(QueEntCnt)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "    CCU:  " << Device->getName() << "  PURGING " << QueEntCnt << " actin queue entries" << endl;
                }
                CleanQueue( pInfo->ActinQueueHandle, (void*)Nexus, findReturnNexusMatch, cleanupOrphanOutMessages );
            }

        }
    }
}

struct buildLGRPQ
{
    void operator()(CtiDeviceSPtr &ccu_device)
    {
        if( !ccu_device )
        {
            return;
        }

        if(!isForeignCcuPort(ccu_device->getPortID()) && !ccu_device->isInhibited())
        {
            switch( ccu_device->getType() )
            {
                default:    break;

                case TYPE_CCU721:
                {
                    using Cti::Devices::Ccu721Device;
                    boost::shared_ptr<Ccu721Device> ccu = boost::static_pointer_cast<Ccu721Device>(ccu_device);

                    OUTMESS *OutMessage = CTIDBG_new OUTMESS;

                    if( ccu->hasWaitingWork()
                        && ccu->buildCommand(OutMessage, Ccu721Device::Command_LoadQueue) )
                    {
                        PortManager.writeQueue(OutMessage->Port, OutMessage->Request.GrpMsgID, OutMessage, OutMessage->Priority);
                    }
                    else
                    {
                        delete OutMessage;
                    }

                    break;
                }

                case TYPE_CCU711:
                {
                    CtiTransmitter711Info *pInfo = (CtiTransmitter711Info*)ccu_device->getTrxInfo();
                    if(pInfo != NULL)
                    {
                        if(pInfo->ActinQueueHandle != (HCTIQUEUE) NULL)
                        {
                            BuildActinShed(ccu_device);
                        }

                        /* Now check out the queue queue handle */
                        if(pInfo->QueueHandle != (HCTIQUEUE) NULL)
                        {
                            if( pInfo->getStatus(INLGRPQ) && pInfo->getINLGRPQWarning() < CtiTime::now() )
                            {
                                CtiPortSPtr port = PortManager.getPortById(ccu_device->getPortID());
                                if( port )
                                {
                                    ULONG reqCount = 0;
                                    ULONG requestID = QUEUED_MSG_REQ_ID_BASE + ccu_device->getAddress();
                                    HCTIQUEUE portQueue = port->getPortQueueHandle();

                                    GetRequestCount(portQueue, requestID, reqCount);

                                    if( reqCount > 0 )
                                    {
                                        pInfo->setINLGRPQWarning(CtiTime::now().seconds() + 300 ); //Yuck, but ok?
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " **** CHECKPOINT **** - INLGRPQ warning, entry found, NOT removing entry **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    }
                                    else
                                    {
                                        pInfo->clearStatus(INLGRPQ);
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << " **** CHECKPOINT **** - INLGRPQ warning, no queue entry found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                        }
                                    }

                                }
                                else
                                {
                                    pInfo->clearStatus(INLGRPQ);
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " **** CHECKPOINT **** - INLGRPQ warning, unknown port **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    }
                                }
                            }

                            if( !pInfo->getStatus(INLGRPQ) )
                            {
                                BuildLGrpQ(ccu_device);
                            }
                        }
                    }

                    break;
                }
            }
        }
    }
};

/* Thread to process and dispatch entries from Queue & ACTIN queues */
void QueueThread (void *Arg)
{
    USHORT Port, Remote;
    DWORD  dwWait = 0;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Queue Thread Starting as TID " << CurrentTID() << endl;
    }

    HANDLE hQueueArray[] = {
        hPorterEvents[P_QUIT_EVENT],
        hPorterEvents[P_QUEUE_EVENT]
    };

    /* make it clear who isn't the boss */
    CTISetPriority(PRTYC_REGULAR, THREAD_PRIORITY_BELOW_NORMAL);

    for( ;PorterQuit != TRUE; )
    {
        dwWait = WaitForMultipleObjects(2, hQueueArray, FALSE, 5000L);

        if(dwWait != WAIT_TIMEOUT)
        {
            switch( dwWait - WAIT_OBJECT_0 )
            {
            case WAIT_OBJECT_0: // P_QUIT_EVENT:
                {
                    PorterQuit = TRUE;
                    continue;            // the for loop
                }
            case WAIT_OBJECT_0 + 1: // P_QUEUE_EVENT:
                {
                    ResetEvent(hPorterEvents[ P_QUEUE_EVENT ]);
                    break;
                }
            default:
                {
                    Sleep(1000);
                    continue;
                }
            }
        }

        {
            CtiPortManager::coll_type::reader_lock_guard_t guard(PortManager.getLock());  // this applyFunc Writes to the PortManager queues!

            vector<CtiDeviceSPtr> ccu_devices;

            DeviceManager.getDevicesByType(TYPE_CCU711, ccu_devices);
            DeviceManager.getDevicesByType(TYPE_CCU721, ccu_devices);

            for_each(ccu_devices.begin(), ccu_devices.end(), buildLGRPQ());
        }
    }
}


/* Routine to process results from CCU's */
INT CCUResponseDecode (INMESS *InMessage, CtiDeviceSPtr Dev, OUTMESS *OutMessage)
{
    extern INT LoadRemoteRoutes(CtiDeviceSPtr RemoteRecord);

    CtiDeviceCCU *ccu = (CtiDeviceCCU *)Dev.get();

    INT status = NORMAL;
    INT SocketError = NORMAL;
    ULONG i;
    INMESS ResultMessage;
    USHORT QueTabEnt;
    USHORT setL;
    ULONG  BytesWritten;
    USHORT Offset;
    ULONG ActinQueueCount, QueueCount;
    USHORT AlgStatus[8];
    CHAR Message[50];
    ULONG TimeAdder;

    CtiTransmitter711Info *pInfo = (CtiTransmitter711Info*)Dev->getTrxInfo();

    UINT ErrorReturnCode = (InMessage->EventCode & ~DECODED);

    bool foreignCCU = isForeignCcuPort(InMessage->Port);

    //  if this is a foreign CCU, we're not allowed to touch the queues
    if( !foreignCCU )
    {
        if(ErrorReturnCode                          &&
           InMessage->Sequence & 0x8000 &&
           (ErrorReturnCode != REQACK || OutMessage->Retry == 0))
        {
            return DeQueue(InMessage);       // Removes all queue entries placed upon in the queue via this message.
        }
        else if(InMessage->Sequence & 0x8000)
        {
            bool detected = false;
            /* loop through queue entry slots and mark those that are now in the ccu */
            for(i = 0; i < MAXQUEENTRIES; i++)
            {
                if(pInfo->QueTable[i].InUse)
                {
                    if(pInfo->QueTable[i].QueueEntrySequence == InMessage->Sequence)
                    {
                        pInfo->QueTable[i].InUse |= INCCU;
                        pInfo->QueTable[i].TimeSent = LongTime();
                        detected = true;
                    }
                }
            }

            if(!detected)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Orphaned CCU queue entry response on device \"" << Dev->getName() << "\".  It will be ignored.  InMessage->Sequence 0x" << hex << (int)InMessage->Sequence << dec << endl;
                    dout << "  Not found in any queue slot" << endl;
                }
            }
        }
    }

    if(ErrorReturnCode && ErrorReturnCode != REQACK)     // Handle non-Sequence & 0x8000 (queued entries)
    {
        return(ErrorReturnCode);
    }


    //  if this is a foreign CCU, we're not allowed to touch the queues
    if( !foreignCCU )
    {
        /* check the RCONT flag in the returned message */
        if(InMessage->IDLCStat[2] & 0x10)
        {
            pInfo->clearStatus(INRCONT);
        }
        else
        {
            pInfo->setStatus(INRCONT);
        }
    }

    /* Decode the important contents of message header */

    /* Check if we have a new power fail */
    if(InMessage->IDLCStat[6] & STAT_POWER)
    {
        if(!(pInfo->getStatus(POWERFAILED)))
        {
            pInfo->setStatus (POWERFAILED);

            _snprintf(tempstr, 99,"Power Fail Detected on Port: %2hd Remote: %3hd... Resetting\n", InMessage->Port, InMessage->Remote);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << tempstr << endl;
            }
            IDLCFunction(Dev, 0, DEST_BASE, CLPWR);

            /* Now send a message to logger */
            _snprintf(Message, 50, "%0.20s Power Fail Detected", Dev->getName().c_str());
            SendTextToLogger ("Inf", Message);
        }
    }
    else
    {
        pInfo->clearStatus(POWERFAILED);
    }


    //  if this is a foreign CCU, we're also not allowed to timesync it - even if there's a reset - so we just ignore this status
    if( !foreignCCU )
    {
        /* Check if we need to issue a time sync to this guy */
        if(InMessage->IDLCStat[7] & STAT_BADTIM)
        {
            if(!(pInfo->getStatus(TIMESYNCED)))
            {
                pInfo->setStatus(TIMESYNCED);
                _snprintf(tempstr, 99,"Time Sync Loss Detected on Port: %2hd Remote: %3hd... Issuing Time Sync\n", InMessage->Port, InMessage->Remote);
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << tempstr << endl;
                }
                CtiOutMessage *TimeSyncMessage = CTIDBG_new OUTMESS;
                /* Allocate some memory */
                if(TimeSyncMessage != NULL)
                {
                    /* send a time sync to this guy */
                    TimeSyncMessage->Port = InMessage->Port;
                    TimeSyncMessage->Remote = InMessage->Remote;
                    TimeSyncMessage->TimeOut = TIMEOUT;
                    TimeSyncMessage->Retry = 0;
                    TimeSyncMessage->OutLength = 10;
                    TimeSyncMessage->InLength = 0;
                    TimeSyncMessage->Source = 0;
                    TimeSyncMessage->Destination = DEST_TSYNC;
                    TimeSyncMessage->Command = CMND_XTIME;
                    TimeSyncMessage->Sequence = 0;
                    TimeSyncMessage->Priority = MAXPRIORITY;
                    TimeSyncMessage->EventCode = NOWAIT | NORESULT | ENCODED | TSYNC | RCONT;

                    if(QueueHandle(InMessage->Port) != NULL)
                    {
                        if(PortManager.writeQueue (TimeSyncMessage->Port, TimeSyncMessage->Request.GrpMsgID, TimeSyncMessage, TimeSyncMessage->Priority))
                        {
                            _snprintf(tempstr, 99,"Error Writing to Queue for Port %2hd\n", InMessage->Port);
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " " << tempstr << endl;
                            }
                            delete (TimeSyncMessage);
                        }
                        else
                        {
                            pInfo->PortQueueEnts++;
                            pInfo->PortQueueConts++;
                        }
                    }
                }

                /* Now send a message to logger */
                _snprintf(Message, 50,  "%0.20s Time Sync Loss", Dev->getName().c_str());
                SendTextToLogger ("Inf", Message);
            }
        }
        else
        {
            pInfo->clearStatus (TIMESYNCED);
        }
    }


    /* Check if we need to issue a Bad Battery Message */
    if(InMessage->IDLCStat[7] & STAT_BATTRY)
    {
        if(!(pInfo->getStatus(LOWBATTRY)))
        {
            pInfo->setStatus(LOWBATTRY);
            _snprintf(tempstr, 99,"Low Battery Detected on Port: %2hd Remote: %3hd... Replace Logic Card\n", InMessage->Port, InMessage->Remote);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << tempstr << endl;
            }

            /* Now send a message to logger */
            _snprintf(Message, 50,  "%0.20s Low Battery Detected", Dev->getName().c_str());
            SendTextToLogger ("Inf", Message);
        }
    }
    else
    {
        pInfo->clearStatus(LOWBATTRY);
    }


    /* Check if we have need to re-download this ccu********  Logic needs improvement */
    if(InMessage->IDLCStat[6] & (STAT_FAULTC | STAT_DEADMN | STAT_COLDST))
    {
        if( foreignCCU )
        {
            //  we need to fake a cold start status to the foreign system

            map< long, CtiPortShare * >::iterator ps_itr;

            if( (ps_itr = PortShareManager.find(InMessage->Port)) != PortShareManager.end() )
            {
                //  set cold start for this CCU on this port's port share

                ps_itr->second->setSharedCCUError(Dev->getAddress(), InMessage->IDLCStat[6]);
            }
        }

        if(!(InMessage->IDLCStat[6] & STAT_COLDST) && pInfo->getLastColdStartTime() + gConfigParms.getValueAsInt("COLD_START_FREQUENCY", 300) < CtiTime::now().seconds()
           && !(pInfo->FreeSlots < MAXQUEENTRIES && !pInfo->PortQueueEnts))
        {
            /* Issue a cold start */
            _snprintf(tempstr, 99,"Reset Needed on Port: %2hd Remote: %3hd... Cold Starting\n", InMessage->Port, InMessage->Remote);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << tempstr << endl;
            }

            IDLCFunction(Dev, 0, DEST_BASE, COLD);

            /* Now send a message to logger */
            if( Dev )
            {
                _snprintf(Message, 50,  "%0.20s Cold Start Sent", Dev->getName().c_str());
                SendTextToLogger ("Inf", Message);
            }
        }
        else if(!(pInfo->getStatus(RESETTING)))
        {
            pInfo->setStatus(RESETTING);
            if(InMessage->IDLCStat[6] & STAT_FAULTC)
            {
                printf ("Fault Detected on Port: %2hd Remote: %3hd... Reloading\n", InMessage->Port, InMessage->Remote);

                /* Yup */
                IDLCFunction(Dev, 0, DEST_BASE, CLFLT);

                /* Now send a message to logger */
                if( Dev )
                {
                    _snprintf(Message, 50,  "%0.20s FAULTC Detected", Dev->getName().c_str());
                    SendTextToLogger ("Inf", Message);
                }
            }

            if(InMessage->IDLCStat[6] & STAT_COLDST)
            {
                _snprintf(tempstr, 99,"Cold Start Detected on Port: %2hd Remote: %3hd... Reloading\n", InMessage->Port, InMessage->Remote);
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << tempstr << endl;
                }
                IDLCFunction(Dev, 0, DEST_BASE, CLCLD);

                /* Assume this could be a new chip */
                pInfo->RColQMin = 0;

                /* Best to flush the queues */
                QueueFlush(Dev);

                /* Now send a message to logger */
                if( Dev )
                {
                    _snprintf(Message, 50,  "%0.20s Cold Start Detected", Dev->getName().c_str());
                    SendTextToLogger ("Inf", Message);
                }
            }

            if(InMessage->IDLCStat[6] & STAT_DEADMN)
            {
                _snprintf(tempstr, 99,"Dead Man Detected on Port: %2hd Remote: %3hd... Reloading\n", InMessage->Port, InMessage->Remote);
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << tempstr << endl;
                }

                IDLCFunction(Dev, 0, DEST_BASE, CLDMN);

                /* Best to flush the queues */
                QueueFlush(Dev);

                /* Now send a message to logger */
                if( Dev )
                {
                    _snprintf(Message, 50,  "%0.20s Deadman Detected", Dev->getName().c_str());
                    SendTextToLogger ("Inf", Message);
                }
            }

            /* Load the delay sets if any */
            IDLCSetDelaySets(Dev);  // this could be dodgy in a foreign CCU scenario (port sharing) - we may not have the same delay.dat file

            /* set the Base Status List */
            pInfo->setStatus(SETSLIST);

            IDLCSetBaseSList(Dev);

            //  if this is a foreign CCU, we probably also don't have the same routes - but they'll reload them when we release control and
            //    notify them of the error
            if( !foreignCCU )
            {
                LoadRemoteRoutes(Dev);
            }

            /* set the time sync Algorithm startup time */
            IDLCSetTSStores(Dev, 15, gConfigParms.getValueAsInt("CCU_COMMS_LOST_TIME", 3600), 3600); // Priority, trigger time, period

            /* Enable the time Sync algorithm */
            IDLCFunction(Dev, 0,  DEST_TSYNC, ENPRO);

        }
    }
    else
    {
        pInfo->clearStatus(RESETTING);
    }


    /* Check the various algorithm status's and correct as neccessary */
    if(IDLCAlgStat (&InMessage->IDLCStat[8], AlgStatus))
    {
        if(!(pInfo->getStatus(SETSLIST)))  /* Check if we are in the process of doing this */
        {
            pInfo->setStatus(SETSLIST);
            /* set the Base Status List */
            IDLCSetBaseSList(Dev);
        }
    }
    else
    {
        /* Make sure we can update list if neccessary */
        pInfo->clearStatus (SETSLIST);

        if(AlgStatus[DEST_DLC] != ALGO_RUNNING && ccu->checkAlgorithmReset(DEST_DLC) )  /* Check the DLC algorithm status */
        {
            IDLCFunction (Dev, 0, DEST_DLC, START_ALGORITHM);

            if(Dev)   /* Now send a message to logger */
            {
                _snprintf(Message, 50,  "%0.20s DLC Alg Restarted", Dev->getName().c_str());
                SendTextToLogger ("Inf", Message);
            }
        }

        if( ccu->checkForTimeSyncLoop(AlgStatus[DEST_TSYNC]) )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - time sync loop detected on device \"" << Dev->getName() << "\", sending cold start **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            //  Send a cold start, the time sync algorithm has been set to 0 seconds
            IDLCFunction(Dev, 0, DEST_BASE, COLD);
        }
        else
        {
            /* Check the time sync Algorithm status */
            if(AlgStatus[DEST_TSYNC] != ALGO_ENABLED && ccu->checkAlgorithmReset(DEST_TSYNC))
            {
                if(AlgStatus[DEST_TSYNC] == ALGO_HALTED)
                {
                    IDLCFunction(Dev, 0, DEST_TSYNC, ENPRO);
                    /* Now send a message to logger */
                    if( Dev )
                    {
                        _snprintf(Message, 50,  "%0.20s TS Alg ENABLED", Dev->getName().c_str());
                        SendTextToLogger ("Inf", Message);
                    }
                }
                else if(AlgStatus[DEST_TSYNC] == ALGO_RUNNING || AlgStatus[DEST_TSYNC] == ALGO_SUSPENDED)
                {
                    IDLCFunction(Dev, 0, DEST_TSYNC, HOPRO);
                }
            }
        }

        /* Check the status of the LM algorithm */
        if(AlgStatus[DEST_LM] != ALGO_ENABLED && ccu->checkAlgorithmReset(DEST_LM))
        {
            IDLCFunction(Dev, 0, DEST_LM, ENPRO);

            /* Now send a message to logger */
            if( Dev )
            {
                _snprintf(Message, 50,  "%0.20s LM Alg ENPRO", Dev->getName().c_str());
                SendTextToLogger ("Inf", Message);
            }
        }
    }

    /* there are more STATS but these are a start ..... <-----<<<<<<<------- */

    /* Now decode pertinant STATD information */
    /* Check for persistent DLC fault */
    if(InMessage->IDLCStat[10] & STAT_DLCFLT)
    {
        if(!(pInfo->getStatus(DLCFAULT)))
        {
            pInfo->setStatus(DLCFAULT);
            _snprintf(tempstr, 99,"Persistent DLC Fault Detected on Port: %2hd Remote: %3hd\n", InMessage->Port, InMessage->Remote);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << tempstr << endl;
            }
        }
    }
    else
    {
        pInfo->clearStatus(DLCFAULT);
    }

    //  if it's a foreign CCU, we should ignore any queueing information the CCU is telling us
    if( !foreignCCU )
    {
        /* Load the CCU's interpretation of queue Info available */
        pInfo->ReadyN = InMessage->IDLCStat[12];
        pInfo->NCsets = InMessage->IDLCStat[13];
        pInfo->NCOcts = MAKEUSHORT (InMessage->IDLCStat[15],InMessage->IDLCStat[14]);

        //  this is more data than we can ever receive in a single packet - the CCU is confused
        if( pInfo->NCsets == 1 && pInfo->NCOcts > 241 )
        {
            //  CCU's queues are messed up and need to be reset
            IDLCFunction(Dev, 0, DEST_BASE, COLD);

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - CCU \"" << Dev->getName() << "\"'s internal queue is corrupt, sending cold start ";
                dout << " (NCsets = " << pInfo->NCsets << ", NCOcts = " << pInfo->NCOcts << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }


    /* Need checks on STATP here <<<<<<<<<---------<<<<<<<<<<< */

    if(InMessage->IDLCStat[6] & STAT_REQACK)
    {
        _snprintf(tempstr,99,"REQACK Detected on Port: %2hd Remote: %3hd\n", InMessage->Port, InMessage->Remote);
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << tempstr << endl;
        }

        if( !foreignCCU )
        {
            /* Check to see what we were up to */
            if((InMessage->IDLCStat[5] & 0x7f) == CMND_RCOLQ)
            {
                /* Some moron put a wrong chip in the CCU */
                if(pInfo->RColQMin == 0)
                {
                    if(InMessage->IDLCStat[3] - 14 < 15)
                    {
                        pInfo->RColQMin = 15;
                        /* Now send a message to logger */
                        _snprintf(Message, 50,  "%0.20s Bad Firmware Adjust ", Dev->getName().c_str());
                        SendTextToLogger ("Inf", Message);
                    }
                    else if(InMessage->IDLCStat[3] - 14 < 61)
                    {
                        pInfo->RColQMin = 61;
                        _snprintf(Message, 50,  "%0.20s Bad Firmware Adjust2", Dev->getName().c_str());
                        SendTextToLogger ("Inf", Message);
                    }
                }
                else if(pInfo->RColQMin == 15)
                {
                    if(InMessage->IDLCStat[3] - 14 >= 15 && InMessage->IDLCStat[3] - 14 < 61)
                    {
                        pInfo->RColQMin = 61;
                        _snprintf(Message, 50,  "%0.20s Bad Firmware Adjust2", Dev->getName().c_str());
                        SendTextToLogger ("Inf", Message);
                    }
                }
            }
            else if( !pInfo->getStatus(INRCOLQ) && pInfo->NCOcts)
            {
                // REQACK was possibly due to full CCU Queue.
                // We must send an RCOLQ to empty it out!

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " REQACK with outstanding NCOcts on device \"" << Dev->getName() << "\".  Will RCOLQ." << endl;
                }

                if(pInfo->NCsets || pInfo->ReadyN)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " CCU " << Dev->getName() << endl;
                        dout << CtiTime() << "   CCU Command Sets Complete (NCSets)   " << pInfo->NCsets << endl;
                        dout << CtiTime() << "   CCU Command Slots Available (ReadyN) " << pInfo->ReadyN << endl;
                    }
                }
                // And do the RCOLQ.  CGP -> Should be HIGHEST priority!
                IDLCRColQ(Dev, MAXPRIORITY);
            }
        }

        if( OutMessage->Retry > 0 )
        {
            // Decrement retries...
            OutMessage->Retry--;

            /* Put it back on the queue for this port */
            if(PortManager.writeQueue(OutMessage->Port, OutMessage->Request.GrpMsgID, OutMessage, OutMessage->Priority))
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Error Requeing Command on device \"" << Dev->getName() << "\"" << endl;
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " REQACK'd LGRPQ message has been requeued for retry on " << Dev->getName() << endl;
            }

            return RETRY_SUBMITTED;
        }
        else
        {
            return REQACK;
        }

    }

    //  if this is a foreign CCU, we're not allowed to touch the queues
    if( !foreignCCU )
    {
        /* Check if this was a related reply */
        if(InMessage->InLength && !ErrorReturnCode)
        {
            /* This is a queue entry reply so loop through and get results */

            /* set the initial pointer into the result field */
            Offset = 0;

            while(setL = InMessage->Buffer.InMessage[Offset++])
            {
                /* This many bytes no longer needed in return message */
                if(pInfo->NCOcts >= setL)
                {
                    pInfo->NCOcts -= setL;     // setL is the number of bytes we just pulled from the message!
                }
                else
                {
                    pInfo->NCOcts = 0;         // We must have been messed up before this...
                }

                if(pInfo->NCsets)             // This is a count of queue "sets" on the CCU.. We are now processing one.
                {
                    pInfo->NCsets--;
                }

                /* Find out which queue entry this is */
                // 20020703 CGP. // if((QueTabEnt = MAKEUSHORT (InMessage->Buffer.InMessage[Offset + 1], InMessage->Buffer.InMessage[Offset])) > MAXQUEENTRIES - 1)
                if((QueTabEnt = MAKEUSHORT (InMessage->Buffer.InMessage[Offset], 0)) > MAXQUEENTRIES - 1)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Wrong Offset into CCU Queue Entry Table on device \"" << Dev->getName() << "\"" << endl;
                    }
                    Offset += setL - 1;
                    continue;
                }

                Offset += 1;

                USHORT OriginalOutMessageSequence = MAKEUSHORT (InMessage->Buffer.InMessage[Offset], 0);

                Offset += 1;

                /* Make sure this entry is in use */
                if(!(pInfo->QueTable[QueTabEnt].InUse))                        // Make sure we think we are using this one!
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Entry Received into Unused CCU Queue Entry on \"" << Dev->getName() << "\"" << endl;
                    }
                    Offset += setL - 3;                                         // Hop over this set and process the next one!
                    continue;
                }

                /* then kinda double check this guy */
                if(pInfo->QueTable[QueTabEnt].QueueEntrySequence != MAKEUSHORT (InMessage->Buffer.InMessage[Offset + 1], InMessage->Buffer.InMessage[Offset]) ||
                   LOBYTE(pInfo->QueTable[QueTabEnt].OriginalOutMessageSequence) != OriginalOutMessageSequence)
                {
                    /* Things are screwed up beyond all recognition */
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Unknown Queue Entry Received... Ignoring on \"" << Dev->getName() << "\"" << endl;
                    }
                    Offset += setL - 3;
                    continue;
                }

                Offset += 2;      // Now looking at the queue entries Priority...

                /* see if we have any interest in decoding the results */
                if(pInfo->QueTable[QueTabEnt].EventCode & RESULT)
                {
                    ResultMessage.EventCode       = NORMAL;

                    ResultMessage.DeviceID        = InMessage->DeviceID;
                    ResultMessage.MessageFlags    = InMessage->MessageFlags;

                    /* Load up the info out of the CCUInfo Structure */
                    ResultMessage.TargetID        = pInfo->QueTable[QueTabEnt].TargetID;
                    ResultMessage.ReturnNexus     = pInfo->QueTable[QueTabEnt].ReturnNexus;
                    ResultMessage.Priority        = pInfo->QueTable[QueTabEnt].Priority;
                    ResultMessage.Sequence        = pInfo->QueTable[QueTabEnt].OriginalOutMessageSequence;
                    ResultMessage.MessageFlags    = pInfo->QueTable[QueTabEnt].MessageFlags;

                    ResultMessage.Return          = pInfo->QueTable[QueTabEnt].Request;


                    ResultMessage.Port            = InMessage->Port;
                    ResultMessage.Remote          = InMessage->Remote;


                    if(pInfo->QueTable[QueTabEnt].EventCode & BWORD)
                    {
                        ResultMessage.Buffer.DSt.Address = pInfo->QueTable[QueTabEnt].Address;
                    }

                    /* Check if queue entry completed successfully */
                    if((InMessage->Buffer.InMessage[Offset++] >> 4) != 15)
                    {
                        /* Nope */
                        ResultMessage.EventCode = QUEUEEXEC;
                        Offset = Offset + setL - 5;
                    }
                    else
                    {
                        /* this is a completed message */
                        /* get the time this guy was actually read */
                        TimeAdder = MidNightWas (LongTime (), (USHORT)DSTFlag ()) + (28800 * (InMessage->Buffer.InMessage[Offset++] % 3));
                        ResultMessage.Time = InMessage->Buffer.InMessage[Offset++] << 8;
                        ResultMessage.Time |= InMessage->Buffer.InMessage[Offset++];
                        ResultMessage.Time += TimeAdder;

                        /* Just in case this wasn't today */
                        while(ResultMessage.Time > LongTime ())
                        {
                            ResultMessage.Time -= (3600L * 24L);
                        }

                        ResultMessage.MilliTime = DSTSET (0);

                        /* Ignore the route information */
                        Offset++;

                        /* At this point a maximum of two requests per queue entry
                           are allowed. This code will be subject to change once
                           we allow full implementation of the queue structure */

                        /* decode number of functions that came back */
                        ResultMessage.InLength = 0;
                        switch(InMessage->Buffer.InMessage[Offset++])
                        {
                        case 3:
                            /* Not implemented but we will work on it any way */
                            switch(InMessage->Buffer.InMessage[Offset++] >> 6)
                            {
                            case 0:
                                ResultMessage.EventCode = NOATTEMPT;
                                break;
                            case 1:
                                ResultMessage.EventCode = NORMAL;
                                break;
                            case 2:
                                ResultMessage.EventCode = ROUTEFAILED;
                                break;
                            case 3:
                                ResultMessage.EventCode = TRANSFAILED;
                                break;
                            }
                            /* Skip next byte */
                            Offset++;

                            /* Update the length */
                            ResultMessage.InLength += InMessage->Buffer.InMessage[Offset++];

                        case 2:
                            switch(InMessage->Buffer.InMessage[Offset++] >> 6)
                            {
                            case 0:
                                ResultMessage.EventCode = NOATTEMPT;
                                break;
                            case 1:
                                if(!ResultMessage.EventCode)
                                    ResultMessage.EventCode = NORMAL;
                                break;
                            case 2:
                                ResultMessage.EventCode = ROUTEFAILED;
                                break;
                            case 3:
                                ResultMessage.EventCode = TRANSFAILED;
                                break;
                            }
                            /* Skip next byte */
                            Offset++;

                            /* Update the length */
                            ResultMessage.InLength += InMessage->Buffer.InMessage[Offset++];

                        case 1:
                            switch(InMessage->Buffer.InMessage[Offset++] >> 6)
                            {
                            case 0:
                                ResultMessage.EventCode = NOATTEMPT;
                                break;
                            case 1:
                                if(!ResultMessage.EventCode)
                                    ResultMessage.EventCode = NORMAL;
                                break;
                            case 2:
                                ResultMessage.EventCode = ROUTEFAILED;
                                break;
                            case 3:
                                ResultMessage.EventCode = TRANSFAILED;
                                break;
                            }
                            /* Skip next byte */
                            Offset++;

                            /* Update the length */
                            ResultMessage.InLength += InMessage->Buffer.InMessage[Offset++];
                        }

                        /* if this was a read pick up alarm bits and data */
                        if(ResultMessage.InLength)
                        {
                            /* Check for device alarm */
                            if(InMessage->Buffer.InMessage[Offset] & 0x0001)
                                ResultMessage.Buffer.DSt.Alarm = 1;
                            else
                                ResultMessage.Buffer.DSt.Alarm = 0;

                            /* Check for device power fail */
                            if(InMessage->Buffer.InMessage[Offset] & 0x0002)
                                ResultMessage.Buffer.DSt.Power = 1;
                            else
                                ResultMessage.Buffer.DSt.Power = 0;

                            /* Check for device in Time Sync */
                            if(InMessage->Buffer.InMessage[Offset] & 0x0008)
                                ResultMessage.Buffer.DSt.TSync = 1;
                            else
                                ResultMessage.Buffer.DSt.TSync = 0;

                            if(InMessage->Buffer.InMessage[Offset] & 0x0040)
                            {
                                ResultMessage.EventCode = EWORDRCV;
                                //  The CCU doesn't pass along the E word, so make sure the optional repeater info fields are cleared out
                                ResultMessage.Buffer.RepeaterError.ESt = 0;
                                ResultMessage.Buffer.RepeaterError.Details = 0;
                            }

                            if(InMessage->Buffer.InMessage[Offset++] & 0x0080)
                                ResultMessage.EventCode = DLCTIMEOUT;

                            Offset++;

                            if(!ResultMessage.EventCode)
                            {
                                ResultMessage.Buffer.DSt.Length = (USHORT)ResultMessage.InLength;
                                for(i = 0; i < ResultMessage.InLength; i++)
                                {
                                    ResultMessage.Buffer.DSt.Message[i] = InMessage->Buffer.InMessage[Offset++];
                                }
                            }
                        }
                    }

                    //  This won't be decoded in Porter, so grab the dynamic info
                    if( ResultMessage.ReturnNexus != &PorterToPil )
                    {
                        SnipeDynamicInfo(ResultMessage);
                    }

                    /* this is a completed result so send it to originating process */
                    if( (SocketError = ResultMessage.ReturnNexus->CTINexusWrite(&ResultMessage, sizeof (ResultMessage), &BytesWritten, 60L)) != NORMAL)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Error writing to nexus. CCUResponseDecode() on device \"" << Dev->getName() << "\".  "
                                 << "Wrote " << BytesWritten << "/" << sizeof(ResultMessage) << " bytes" << endl;
                        }

                        if(CTINEXUS::CTINexusIsFatalSocketError(SocketError))
                        {
                            status = SocketError;
                        }
                    }

                    //We had a comm error and need to report it.
                    addCommResult(ResultMessage.TargetID, (ResultMessage.EventCode & 0x3fff) != NORMAL, false);

                    PorterStatisticsManager.newCompletion( ResultMessage.Port, ResultMessage.DeviceID, ResultMessage.TargetID, ResultMessage.EventCode & 0x3fff, ResultMessage.MessageFlags );
                }
                else
                {
                    /* we are not going to return results so jump over this one */
                    /* NOTE... This will need to change for statistics */
                    Offset += setL - 5;
                }

                /* Free up the entry in the QueTable */
                if(pInfo->QueTable[QueTabEnt].InUse) InterlockedIncrement( &(pInfo->FreeSlots) );
                if(pInfo->FreeSlots > MAXQUEENTRIES)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                pInfo->QueTable[QueTabEnt].InUse = 0;
                pInfo->QueTable[QueTabEnt].TimeSent = -1L;
            }
        }

        /* see if anything is in the ccu queues */
#ifdef DEBUG
        if(pInfo->FreeSlots != 32)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Port: " << InMessage->Port <<
                "  Remote: " << InMessage->Remote <<
                "  FreeSlots: " << pInfo->FreeSlots <<
                "  PortQueEnts: " << pInfo->PortQueueEnts <<
                "  PortQueueConts: " << pInfo->PortQueueConts << endl;
            }
        }
#endif

        if((InMessage->IDLCStat[5] & 0x007f) == CMND_RCOLQ)
        {
            pInfo->clearStatus(INRCOLQ);
        }

        /* See if we need to clear an RCONT */
        if(pInfo->getStatus(INRCONT))
        {
            if(!(pInfo->PortQueueConts))
            {
                /* see if any entries on the queue queue for this ccu */
                QueryQueue(pInfo->QueueHandle, &QueueCount);
                /* see if any entries on the actin queue for this ccu */
                QueryQueue(pInfo->ActinQueueHandle, &ActinQueueCount);

                if(!QueueCount && !ActinQueueCount)
                {
                    /* Lets figure out if we should do a RCONT or a RCOLQ ... */

                    /* If nothing is left do something minimal */
                    if(pInfo->FreeSlots == MAXQUEENTRIES)
                    {
                        IDLCRColQ(Dev);
                    }
                    else if(pInfo->NCOcts + 1 > pInfo->RContInLength)
                    {
                        if(pInfo->RContInLength != 241)
                        {
                            IDLCRColQ(Dev);
                        }
                        else
                        {
                            IDLCRCont(Dev);
                        }
                    }
                    else
                    {
                        if(pInfo->NCOcts + 14 <= pInfo->RContInLength)
                        {
                            IDLCRColQ(Dev);
                        }
                        else
                        {
                            IDLCRCont(Dev);
                        }
                    }
                }
            }
        }
        else if(pInfo->FreeSlots < MAXQUEENTRIES && !(pInfo->getStatus(INRCOLQ)) && pInfo->NCOcts)
        {
            IDLCRColQ(Dev);
        }
    }

    return(status);
}


static ULONG sleepTime = 15000L;


struct kick
{
    void operator()(CtiDeviceSPtr &ccu_device)
    {
        if( ccu_device->getType() == TYPE_CCU721 )
        {
            using Cti::Devices::Ccu721Device;
            Cti::Devices::Ccu721SPtr ccu = boost::static_pointer_cast<Ccu721Device>(ccu_device);

            OUTMESS *OutMessage = CTIDBG_new OUTMESS;

            if( ccu->hasRemoteWork()
                && ccu->buildCommand(OutMessage, Ccu721Device::Command_ReadQueue) )
            {
                PortManager.writeQueue(OutMessage->Port, OutMessage->Request.GrpMsgID, OutMessage, OutMessage->Priority);
            }
            else
            {
                delete OutMessage;
            }
        }
        else if( ccu_device->getType() == TYPE_CCU711 )
        {
            CtiLockGuard<CtiMutex> devguard(ccu_device->getMux());                  // Protect our device!
            CtiTransmitter711Info *pInfo = (CtiTransmitter711Info*)ccu_device->getTrxInfo();

            LONG FreeQents = 0;
            LONG OldFreeEnts;
            bool rcol_sent = false;

            ULONG nowtime = LongTime();
            /* Check if we have anything done yet */
            for(int offset = 0; offset < MAXQUEENTRIES; offset++)
            {
                if((pInfo->QueTable[offset].InUse & INUSE) && (pInfo->QueTable[offset].TimeSent <= nowtime - MAX_CCU_QUEUE_TIME))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << ccu_device->getName() << " INUSE queue entry is more than " << MAX_CCU_QUEUE_TIME << " seconds old. " << endl;
                    }

                    /* send a message to the calling process */
                    if(pInfo->QueTable[offset].EventCode & RESULT) ReturnQueuedResult(ccu_device, pInfo, offset);
                    if(pInfo->QueTable[offset].InUse) InterlockedIncrement( &(pInfo->FreeSlots) );
                    if(pInfo->FreeSlots > MAXQUEENTRIES)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                    pInfo->QueTable[offset].InUse = 0;
                    pInfo->QueTable[offset].TimeSent = -1L;
                }
                else if(pInfo->QueTable[offset].InUse & INCCU)
                {
                    if(pInfo->QueTable[offset].TimeSent <= nowtime - 5L)
                    {
                        if(!rcol_sent)
                        {
                            rcol_sent = true;
                            IDLCRColQ(ccu_device);
                        }
                    }
                    else if(pInfo->QueTable[offset].TimeSent == -1L)
                    {
                        // 20051129 CGP - This should never hapen if we are INCCU.  I've become paranoid!
                        pInfo->QueTable[offset].TimeSent = nowtime;
                        sleepTime = 2500L;                             // Wake every 2.5 seconds in this case.
                    }
                    else
                    {
                        sleepTime = 2500L;                             // Wake every 2.5 seconds in this case.
                    }
                }

                if(!pInfo->QueTable[offset].InUse) FreeQents++;          // Counting the number of Open slots.
            }

            if( FreeQents != (OldFreeEnts = InterlockedExchange(&(pInfo->FreeSlots), FreeQents)) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** FREESLOTS CORRECTION!  **** " << __FILE__ << " (" << __LINE__ << ") " << OldFreeEnts << " != " << FreeQents << endl;
            }

            if(!rcol_sent && pInfo->FreeSlots == MAXQUEENTRIES && pInfo->NCOcts)
            {
                /* Opps... something got left behind */
                IDLCRColQ(ccu_device);
            }
        }
    }
};

/* Thread to kick the CCU-711 if no activity and it might have data */
void KickerThread (void *Arg)
{
    USHORT Port, Remote;
    ULONG i;
    UINT sanity = 0;

    ThreadStatusKeeper threadStatus("CCU Kicker Thread");

    /* make it clear who isn't the boss */
    CTISetPriority(PRTYC_REGULAR, THREAD_PRIORITY_LOWEST);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Queue Kicker Thread Starting as TID:  " << CurrentTID() << endl;
    }

    SetThreadName(-1, "KickerThd");

    for(; !PorterQuit ;)
    {
        if(sleepTime > 15000L )
        {
            sleepTime = 15000L;
        }

        if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], sleepTime) )
        {
            PorterQuit = TRUE;
            continue;
        }

        sleepTime = 15000L;

        /* loop through and check if we need to do any RCOLQ's */

        {
            CtiPortManager::coll_type::reader_lock_guard_t guard(PortManager.getLock());  // this applyFunc Writes to the PortManager queues!

            vector<CtiDeviceSPtr> ccu_devices;

            DeviceManager.getDevicesByType(TYPE_CCU711, ccu_devices);
            DeviceManager.getDevicesByType(TYPE_CCU721, ccu_devices);

            for_each(ccu_devices.begin(), ccu_devices.end(), kick());
        }

        threadStatus.monitorCheck(CtiThreadRegData::None);
    }
}


/* Routine to flush the 711 queue's for a given CCU */
/* Dequeues only those entries marked as having been slated for queing (likely on the port queue) and INUSE */
INT QueueFlush (CtiDeviceSPtr Dev)
{
    USHORT QueTabEnt;

    CtiTransmitter711Info *pInfo = (CtiTransmitter711Info*)Dev->getTrxInfo();

    if(pInfo->FreeSlots < MAXQUEENTRIES)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Flushing " << Dev->getName() << "'s INUSE Porter Queue Table" << endl;
        }

        /* walk through the entry table for this ccu */
        for(QueTabEnt = 0; QueTabEnt < MAXQUEENTRIES; QueTabEnt++)
        {
            if(pInfo->QueTable[QueTabEnt].InUse & INUSE)
            {
                /* send a message to the calling process */
                if(pInfo->QueTable[QueTabEnt].EventCode & RESULT)
                {
                    ReturnQueuedResult(Dev, pInfo, QueTabEnt);
                }

                pInfo->QueTable[QueTabEnt].InUse = 0;
                pInfo->QueTable[QueTabEnt].TimeSent = -1L;
            }
        }
        InterlockedExchange(&(pInfo->FreeSlots), MAXQUEENTRIES);
        pInfo->clearStatus(INLGRPQ);            // 20050506 CGP on a wh.
    }
    return(NORMAL);
}


INT BuildLGrpQ (CtiDeviceSPtr Dev)
{
    ULONG Length;
    ULONG Count;
    OUTMESS *MyOutMessage, *OutMessage = 0;
    ULONG i, j;
    USHORT Offset;
    USHORT SETLPos, QueTabEnt;
    BYTE Priority = 7;
    USHORT QueueEntrySequence = 0;

    CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)Dev->getTrxInfo();

    if(QueryQueue (pInfo->QueueHandle, &Count))
    {
        _snprintf(tempstr, 99,"Error Querying Queue Port: %2hd Remote: %3hd\n", Dev->getPortID(), Dev->getAddress());
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << tempstr << endl;
        }
        return(!NORMAL);
    }

    if(Count)
    {
        /* Handle the scenario of a broadcast entry */
        if(Dev->getAddress() == CCUGLOBAL)
        {
            /* This is a no no so throw em away (for now) */
            for(i = 0; i < Count; i++)
            {
                if(ReadFrontElement(pInfo->QueueHandle, &Length, (PVOID *) &MyOutMessage, DCWW_WAIT, &Priority))
                {
                    _snprintf(tempstr, 99,"Error Reading Queue\n");
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << tempstr << endl;
                    }
                    return(QUEUE_READ);
                }
                delete (MyOutMessage);
                _snprintf(tempstr, 99,"Broadcast Entry Received on Queue Queue for Port:  %hd\n", Dev->getPortID());
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << tempstr << endl;
                }
            }
            return(NORMAL);
        }

        /* Check how many entries we can handle on this CCU */
        if(pInfo->FreeSlots <= pInfo->ReadyN)
        {
            if(Count > pInfo->FreeSlots)
                Count = pInfo->FreeSlots;
        }
        else
        {
            if(Count > pInfo->ReadyN)
                Count = pInfo->ReadyN;
        }

        /* Count now indicates the number of queue entries we will submit to the CCU for execution! */

        /*
         *  QueSequence is always >= 0x8000.  This "bit" is used to indicate items AND CtiOutMessages which
         *  are queued on the CCU.  The original Sequence is stored in the CtiTransmitterInfo (pInfo).
         */
        if(Count)
        {
            QueueEntrySequence = QueSequence++;
            if(!(QueSequence & 0x8000)) QueSequence = 0x8000;
        }

        /* Zero out the block counter */
        Offset = PREIDL;                  // First entry starts here.
        for(i = 1; i <= Count && !pInfo->getStatus(INLGRPQ); i++)       /* limit to one Lgrpq per ccu on queue at a time */
        {
            /* get the client submitted entry from the queue */
            if(ReadFrontElement(pInfo->QueueHandle, &Length, (PVOID *) &MyOutMessage, DCWW_WAIT, &Priority))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Error Reading Device Queue of " << Dev->getName() << endl;
                }
                return(QUEUE_READ);
            }

            /* if this is first in the group get memory for it */
            if(Offset == PREIDL)
            {
                if(!OutMessage && (OutMessage = CTIDBG_new OUTMESS(*MyOutMessage)) == NULL)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Error Allocating Memory" << endl;
                    }
                    return(MEMORY);
                }

                OutMessage->Priority       = Priority;
                OutMessage->DeviceID       = Dev->getID();
                OutMessage->TargetID       = Dev->getID();
                OutMessage->Remote         = Dev->getAddress();
                OutMessage->Port           = Dev->getPortID();
                OutMessage->Sequence       = QueueEntrySequence;              // Tells us on return that we are queued ( >= 0x8000 )
                OutMessage->Request.UserID = QUEUED_MSG_REQ_ID_BASE + Dev->getAddress();
                OutMessage->EventCode      = NOWAIT | RESULT | RCONT;
                OutMessage->TimeOut        = TIMEOUT;
                OutMessage->Retry          = 2;
                OutMessage->InLength       = 0;

                OutMessage->Source         = 0;
                OutMessage->Destination    = DEST_QUEUE;
                OutMessage->Command        = CMND_LGRPQ;
                OutMessage->ReturnNexus    = NULL;                    // This message IS NOT reportable to the requesting client!
            }

            /* Find the first open entry in the QueTable */
            for(QueTabEnt = 0; QueTabEnt < MAXQUEENTRIES; QueTabEnt++)
            {
                if(!pInfo->QueTable[QueTabEnt].InUse)
                    break;
            }

            /*
             *  20020703 CGP
             *  In a perfect world, the loop above will never have NOT found an open slot...  What if we didn't though?
             *  It is never nice to stomp memory.  The block below will attempt something new and exciting.
             *  This "could" happen if FreeSlots had gone south.
             */
            if(QueTabEnt == MAXQUEENTRIES)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << Dev->getName() << "'s CCU QueTable is already full.  Cannot do a LGrpQ. Requeuing the command for later execution." << endl;
                }

                // Replace the MyOutMessage at the rear of its priority on the CCU Queue.
                if(WriteQueue(pInfo->QueueHandle, MyOutMessage->Request.GrpMsgID, sizeof (*MyOutMessage), (char *) MyOutMessage, MyOutMessage->Priority))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Unable to requeue OM to the CCU->QueueHandle* " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    delete MyOutMessage;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << Dev->getName() << " re-queued an OM to the CCU->QueueHandle. Count = " << Count << " iteration " << i << endl;
                    }
                }
            }
            else
            {
                /* save where to put the length of this entry */
                SETLPos = Offset++;

                /* tick off free slots available */
                InterlockedDecrement( &(pInfo->FreeSlots) );
                pInfo->QueTable[QueTabEnt].InUse |= INUSE;
                pInfo->QueTable[QueTabEnt].TimeSent = LongTime();          // Erroneous, but not totally evil.  20020703 CGP.  pInfo->QueTable[i].TimeSent = LongTime();

                /* and load the entry */
                pInfo->QueTable[QueTabEnt].TargetID       = MyOutMessage->TargetID;
                pInfo->QueTable[QueTabEnt].ReturnNexus    = MyOutMessage->ReturnNexus;
                pInfo->QueTable[QueTabEnt].EventCode      = MyOutMessage->EventCode;
                pInfo->QueTable[QueTabEnt].Priority       = MyOutMessage->Priority;
                pInfo->QueTable[QueTabEnt].Address        = MyOutMessage->Buffer.BSt.Address;
                pInfo->QueTable[QueTabEnt].Request        = MyOutMessage->Request;
                pInfo->QueTable[QueTabEnt].MessageFlags   = MyOutMessage->MessageFlags;
                pInfo->QueTable[QueTabEnt].OriginalOutMessageSequence  = MyOutMessage->Sequence;            // The orignial requestor's sequence.
                pInfo->QueTable[QueTabEnt].QueueEntrySequence          = QueueEntrySequence;                // The tatoo which makes this entry (QueTabEnt) identifiable with this OUT/INMESS pair.

                OutMessage->Buffer.OutMessage[Offset++] = LOBYTE (QueTabEnt) & 0x007f;
                OutMessage->Buffer.OutMessage[Offset++] = LOBYTE (MyOutMessage->Sequence);

                OutMessage->Buffer.OutMessage[Offset++] = HIBYTE (QueueEntrySequence);
                OutMessage->Buffer.OutMessage[Offset++] = LOBYTE (QueueEntrySequence);

                /* Load the Priority */
                OutMessage->Buffer.OutMessage[Offset++] = Priority;

                /* Make sure the outmessage priority keeps up */
                if(OutMessage->Priority < Priority)
                    OutMessage->Priority = Priority;

                /* Load the Remote */
                OutMessage->Buffer.OutMessage[Offset++] = LOBYTE (HIUSHORT (MyOutMessage->Buffer.BSt.Address));
                OutMessage->Buffer.OutMessage[Offset++] = HIBYTE (LOUSHORT (MyOutMessage->Buffer.BSt.Address));
                OutMessage->Buffer.OutMessage[Offset++] = LOBYTE (LOUSHORT (MyOutMessage->Buffer.BSt.Address));

                /* Load the bus */
                OutMessage->Buffer.OutMessage[Offset++] = (UCHAR)MyOutMessage->Buffer.BSt.DlcRoute.Bus;

                /* Load repeater control */
                OutMessage->Buffer.OutMessage[Offset++] = (MyOutMessage->Buffer.BSt.DlcRoute.RepVar << 5) | (MyOutMessage->Buffer.BSt.DlcRoute.RepFixed & 0x001f);

                /* Load number of repeaters */
                OutMessage->Buffer.OutMessage[Offset++] = (UCHAR)MyOutMessage->Buffer.BSt.DlcRoute.Stages;

                /* Load the number of functions that will be involved */
                if(MyOutMessage->Buffer.BSt.IO & Cti::Protocols::EmetconProtocol::IO_Read)
                {
                    //  we are going to ignore the arm request
                    OutMessage->Buffer.OutMessage[Offset++] = 1;

                    //  Select B word, single transmit, & read
                    if(Double)
                        OutMessage->Buffer.OutMessage[Offset++] = 0xa8;
                    else
                        OutMessage->Buffer.OutMessage[Offset++] = 0x88;

                    //  Select the type of read
                    if(MyOutMessage->Buffer.BSt.IO & 0x02)
                        OutMessage->Buffer.OutMessage[Offset - 1] |= 0x10;

                    OutMessage->Buffer.OutMessage[Offset++] = 0;

                    //  select the Remote
                    OutMessage->Buffer.OutMessage[Offset++] = (UCHAR)MyOutMessage->Buffer.BSt.Function;

                    //  Select the number of bytes to read
                    OutMessage->Buffer.OutMessage[Offset++] = (UCHAR)MyOutMessage->Buffer.BSt.Length;
                    //  Save the number of bytes to be read
                    pInfo->QueTable[QueTabEnt].Length = MyOutMessage->Buffer.BSt.Length;
                }
                else
                {
                    //  NFUNC set to 1...  perhaps get smarter about combining ARM commands into a single write?
                    //  This is how things used to be, but to unify the queuing code with DTRAN, they're now two seperate submissions.
                    //    This isn't ideal, but it works.
                    OutMessage->Buffer.OutMessage[Offset++] = 1;

                    //  Select B word, single transmit & write
                    OutMessage->Buffer.OutMessage[Offset++] = 0x80;

                    if(Double)
                        OutMessage->Buffer.OutMessage[Offset - 1] |= 0x20;

                    //  If it's a function write
                    if(MyOutMessage->Buffer.BSt.IO & 0x02)
                        OutMessage->Buffer.OutMessage[Offset - 1] |= 0x10;

                    OutMessage->Buffer.OutMessage[Offset++] = 0;

                    //  select the address / function
                    OutMessage->Buffer.OutMessage[Offset++] = (UCHAR)MyOutMessage->Buffer.BSt.Function;

                    //  select the length of the write
                    OutMessage->Buffer.OutMessage[Offset++] = (UCHAR)MyOutMessage->Buffer.BSt.Length;

                    //  if this is a write copy message to buffer
                    if(MyOutMessage->Buffer.BSt.Length)
                    {
                        for(j = 0; j < MyOutMessage->Buffer.BSt.Length; j++)
                            OutMessage->Buffer.OutMessage[Offset++] = MyOutMessage->Buffer.BSt.Message[j];
                    }

                    pInfo->QueTable[QueTabEnt].Length = 0;
                }

                //  we are done with the request message
                delete (MyOutMessage);

                //  Now load the setl length for this guy
                OutMessage->Buffer.OutMessage[SETLPos] = Offset - SETLPos;
            }

            /* Check if this is the last one or buffer full */
            if(i == Count || Offset > (MaxOcts - MAXQUEENTLEN - 2))
            {
                OutMessage->Buffer.OutMessage[Offset] = 0;
                OutMessage->OutLength = Offset - PREIDLEN + 2;

                /* Check the priority and do not let it be less than 11 */
                if(OutMessage->Priority < gConfigParms.getValueAsInt("PORTER_MINIMUM_CCUQUEUE_PRIORITY",11))
                    OutMessage->Priority = gConfigParms.getValueAsInt("PORTER_MINIMUM_CCUQUEUE_PRIORITY",11);

                PorterStatisticsManager.newRequest(OutMessage->Port, OutMessage->DeviceID, 0, OutMessage->MessageFlags);
                if(PortManager.writeQueue (OutMessage->Port, OutMessage->Request.GrpMsgID, OutMessage, OutMessage->Priority))
                {
                    _snprintf(tempstr, 99,"Error Writing to Queue for Port %2hd\n", OutMessage->Port);
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << tempstr << endl;
                    }
                    delete OutMessage;                      // Starting over, so we'd better bop the OM.
                    OutMessage = 0;
                    Offset = PREIDL;
                    continue;
                }
                else
                {
                    OutMessage = 0;                         // Passed on the memory now!
                    /* Update the port entries count */
                    pInfo->setStatus(INLGRPQ);              // This should break our for loop too.
                    pInfo->PortQueueEnts++;
                    pInfo->PortQueueConts++;
                }

                Offset = PREIDL;
            }
        }

        if(OutMessage)
        {
            delete OutMessage;
            OutMessage = 0;
        }
    }

    return(NORMAL);
}

INT BuildActinShed (CtiDeviceSPtr Dev)
{
    ULONG Length;
    ULONG Count;
    OUTMESS *MyOutMessage, *OutMessage;
    USHORT Lastset;
    USHORT Offset;
    BYTE Priority;

    // CCUINFO *pInfo = CCUInfo.findValue(&DevID);
    CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)Dev->getTrxInfo();

    if(QueryQueue (pInfo->ActinQueueHandle, &Count))
    {
        _snprintf(tempstr, 99,"Error Querying Actin Queue Port: %2hd Remote: %3hd\n", Dev->getPortID(), Dev->getAddress());
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << tempstr << endl;
        }
        return(QUEUE_READ);
    }

    while(Count)
    {
        /* get the entry from the queue */
        if(ReadFrontElement(pInfo->ActinQueueHandle, &Length, (PVOID *) &MyOutMessage, DCWW_WAIT, &Priority))
        {
            _snprintf(tempstr, 99,"Error Reading Actin Queue\n");
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << tempstr << endl;
            }
            return(QUEUE_READ);
        }

        if((OutMessage = CTIDBG_new OUTMESS(*MyOutMessage)) == NULL)
        {
            _snprintf(tempstr, 99,"Error Allocating Memory\n");
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << tempstr << endl;
            }
            return(MEMORY);
        }

        /* Seed the priority entry */
        if(Dev->getAddress() != CCUGLOBAL)
        {
            OutMessage->Priority = Priority;
        }
        else
        {
            OutMessage->Priority = MAXPRIORITY;
        }

        if(OutMessage->Priority && MyOutMessage->Buffer.ASt.Function == A_RESTORE)
        {
            OutMessage->Priority--;
        }

        OutMessage->DeviceID    = Dev->getID();
        OutMessage->Remote      = Dev->getAddress();
        OutMessage->Port        = Dev->getPortID();
        // OutMessage->Sequence    = 0;
        OutMessage->OutLength   = 5;
        OutMessage->EventCode   = NOWAIT | NORESULT | RCONT;
        OutMessage->TimeOut     = TIMEOUT;
        OutMessage->Retry       = 2;
        OutMessage->InLength    = 0;
        OutMessage->Source      = 0;
        OutMessage->Destination = DEST_LM;
        OutMessage->Command     = CMND_ACTIN;
        OutMessage->ReturnNexus = NULL;
        OutMessage->SaveNexus   = NULL;

        Offset = PREIDL;

        /* Select the set */
        if(MyOutMessage->Buffer.ASt.Group >= 60)
            Lastset = 0;
        else
            Lastset = (MyOutMessage->Buffer.ASt.Group / 4) + 1;
        OutMessage->Buffer.OutMessage[Offset++] = SEL0 + Lastset;

        /* select the time */
        OutMessage->Buffer.OutMessage[Offset++] = USTM0 + MyOutMessage->Buffer.ASt.Time;

        /* now do command out of selected set */
        OutMessage->Buffer.OutMessage[Offset++] = SHD0 + (MyOutMessage->Buffer.ASt.Group % 4) * 8 + MyOutMessage->Buffer.ASt.Function;

        /* we are done with the request message */
        delete (MyOutMessage);

        if(PortManager.writeQueue (Dev->getPortID(), OutMessage->Request.GrpMsgID, OutMessage, OutMessage->Priority))
        {
            _snprintf(tempstr, 99,"Error Writing to Queue for Port %2hd\n", Dev->getPortID());
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << tempstr << endl;
            }

            return(QUEUE_WRITE);
        }
        else
        {
            pInfo->PortQueueEnts++;
            pInfo->PortQueueConts++;
        }

        /* check for entries on the queue again */
        if(QueryQueue (pInfo->ActinQueueHandle, &Count))
        {
            _snprintf(tempstr, 99,"Error Querying Actin Queue Port: %2hd Remote: %3hd\n", Dev->getPortID(), Dev->getAddress());
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << tempstr << endl;
            }
            return(QUEUE_READ);
        }
    }

    return(NORMAL);
}


/* Routine to take messages off because of errant LGRPQ */
/* Dequeues only those entries marked as having QueueEntrySequence equal to the inbound message sequence*/
INT DeQueue (INMESS *InMessage)
{
    INT status = NORMAL;
    INT SocketError = NORMAL;
    struct timeb TimeB;
    ULONG i;
    INMESS ResultMessage;
    ULONG BytesWritten;

    /* set the time */
    UCTFTime (&TimeB);
    InMessage->Time = TimeB.time;
    InMessage->MilliTime = TimeB.millitm;
    if(TimeB.dstflag)
    {
        InMessage->MilliTime |= DSTACTIVE;
    }

    /* Make sure this is one of em */
    if(!(InMessage->Sequence & 0x8000))
    {
        return(!NORMAL);
    }

    CtiDeviceSPtr TransmitterDev = DeviceManager.getDeviceByID(InMessage->DeviceID);

    if(TransmitterDev)
    {
        CtiTransmitter711Info *pInfo = (CtiTransmitter711Info*)TransmitterDev->getTrxInfo();

        /* loop through queue entry slots and send back messages */
        for(i = 0; i < MAXQUEENTRIES; i++)
        {
            if(pInfo->QueTable[i].InUse)
            {
                if(pInfo->QueTable[i].QueueEntrySequence == InMessage->Sequence)
                {
                    if(pInfo->QueTable[i].EventCode & RESULT)
                    {
                        ResultMessage.Port = InMessage->Port;
                        ResultMessage.Remote = InMessage->Remote;
                        ResultMessage.TargetID = pInfo->QueTable[i].TargetID;
                        ResultMessage.Sequence = pInfo->QueTable[i].OriginalOutMessageSequence;
                        ResultMessage.Priority = pInfo->QueTable[i].Priority;
                        ResultMessage.ReturnNexus = pInfo->QueTable[i].ReturnNexus;
                        ResultMessage.Return = pInfo->QueTable[i].Request;
                        ResultMessage.MessageFlags = pInfo->QueTable[i].MessageFlags;
                        ResultMessage.EventCode = InMessage->EventCode;
                        ResultMessage.Time = InMessage->Time;
                        ResultMessage.MilliTime = InMessage->MilliTime;
                        if(pInfo->QueTable[i].EventCode & BWORD)
                        {
                            ResultMessage.Buffer.DSt.Address = pInfo->QueTable[i].Address;
                        }

                        //We had a comm error and need to report it.
                        addCommResult(ResultMessage.TargetID, (ResultMessage.EventCode & 0x3fff) != NORMAL, false);

                        PorterStatisticsManager.newCompletion( ResultMessage.Port, InMessage->DeviceID, ResultMessage.TargetID, ResultMessage.EventCode & 0x3fff, ResultMessage.MessageFlags );
                        /* Now send it back */
                        if((SocketError = ResultMessage.ReturnNexus->CTINexusWrite (&ResultMessage, sizeof (ResultMessage), &BytesWritten, 60L)))
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Error writing to return nexus.  DeQueue().  "
                                << "Wrote " << BytesWritten << "/" << sizeof(ResultMessage) << " bytes.  Error: " << SocketError << endl;
                            }

                            if(CTINEXUS::CTINexusIsFatalSocketError(SocketError))
                            {
                                status = SocketError;
                            }
                        }
                    }
                    /* Now free up the table entry */
                    if(pInfo->QueTable[i].InUse) InterlockedIncrement( &(pInfo->FreeSlots) );
                    if(pInfo->FreeSlots > MAXQUEENTRIES)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                    pInfo->QueTable[i].InUse = 0;
                    pInfo->QueTable[i].TimeSent = -1L;
                }
            }
        }
    }

    return(status);
}

bool findReturnNexusMatch(void *nid, void *d)
{
    CTINEXUS *rnid = (CTINEXUS *)nid;
    OUTMESS *OutMessage = (OUTMESS *)d;

    return(OutMessage->ReturnNexus == rnid);
}

bool findAllQueueEntries(void *unused, void *d)
{
    return true;
}

void cleanupOrphanOutMessages(void *unusedptr, void* d)
{
    OUTMESS *OutMessage = (OUTMESS *)d;

    if(PorterDebugLevel & PORTER_DEBUG_VERBOSE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " OutMessage being cleaned up. " << endl;
    }

    OutMessage->Request.MacroOffset = 0; //Do not resend this on macro route!
    SendError( OutMessage, ErrorQueuePurged );
    // delete OutMessage;

    return;
}

void cancelOutMessages(void *doSendError, void* om)
{
    OUTMESS *OutMessage = (OUTMESS *)om;

    if(PorterDebugLevel & PORTER_DEBUG_VERBOSE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " OutMessage being cleaned up. " << endl;
    }

    OutMessage->Request.MacroOffset = 0; //Do not resend this on macro route!

    if( doSendError != 0 )
    {
        OutMessage->Request.Connection = NULL;
        SendError( OutMessage, ErrorRequestCancelled );
    }
    else
    {
        PorterStatisticsManager.newCompletion(OutMessage->Port, OutMessage->DeviceID, OutMessage->TargetID, ErrorRequestCancelled, OutMessage->MessageFlags);
        delete OutMessage;
        OutMessage = 0;
    }

    return;
}

int ReturnQueuedResult(CtiDeviceSPtr Dev, CtiTransmitter711Info *pInfo, USHORT QueTabEnt)
{
    INMESS InMessage;
    ULONG BytesWritten;

    try
    {
        try
        {
            InMessage.DeviceID      = Dev->getID();
            InMessage.Port          = Dev->getPortID();
            InMessage.Remote        = Dev->getAddress();
            InMessage.TargetID      = pInfo->QueTable[QueTabEnt].TargetID;    // This is the DID of the target DLC device ( MCT_XYZ )
            InMessage.Sequence      = pInfo->QueTable[QueTabEnt].OriginalOutMessageSequence;    // Client identifier?
            InMessage.ReturnNexus   = pInfo->QueTable[QueTabEnt].ReturnNexus; // This is who asked for it.
            InMessage.Priority      = pInfo->QueTable[QueTabEnt].Priority;
            InMessage.Return        = pInfo->QueTable[QueTabEnt].Request;
            InMessage.MessageFlags  = pInfo->QueTable[QueTabEnt].MessageFlags;
            InMessage.Time          = LongTime();
            InMessage.MilliTime     = 0;
            InMessage.EventCode     = QUEUEFLUSHED;                 // Indicates the result of the request.. The CCU queue was blown away!
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        try
        {
            if(pInfo->QueTable[QueTabEnt].EventCode & BWORD)                  // If the outbound request was a BWORD then...
            {
                InMessage.Buffer.DSt.Address = pInfo->QueTable[QueTabEnt].Address;
            }

            /* send message back to originating process */
            if(InMessage.ReturnNexus->CTINexusWrite(&InMessage, sizeof (InMessage), &BytesWritten, 60L) || BytesWritten == 0)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Error Writing to nexus on device \"" << Dev->getName() << "\" " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                InMessage.ReturnNexus->CTINexusClose();
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return NORMAL;
}
