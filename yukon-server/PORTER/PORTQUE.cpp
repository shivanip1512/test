/*-----------------------------------------------------------------------------*
*
* File:   PORTQUE
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PORTQUE.cpp-arc  $
* REVISION     :  $Revision: 1.25 $
* DATE         :  $Date: 2003/12/22 21:45:03 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning (disable : 4786)


#pragma title ( "Queing and Actin Message Builders" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        PORTQUE.C

    Purpose:
        Routines to take Queing and Actin Shed requests and form them into
        messages for the CCU-711, Routines to process queue results, and
        routines to oversee the process.

    The following procedures are contained in this module:
        QueueThread                 CCUResponseDecode
        KickerThread                CCUQueueFlush
        QueueFlush

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        9-7-93   Converted to 32 bit                                WRO
        11-15-93 Removed logging of persistant DLC fault message    WRO
        02-03-94 Fixed various broadcast issues                     WRO

   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
// #include "btrieve.h"
#include <stdio.h>
#include <string.h>

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "elogger.h"

#include "portglob.h"
#include "portdecl.h"
#include "c_port_interface.h"
#include "mgr_device.h"
#include "mgr_port.h"

#include "logger.h"
#include "guard.h"
#include "trx_711.h"
#include "dev_ccu.h"

extern CtiPortManager   PortManager;
extern HCTIQUEUE*       QueueHandle(LONG pid);

USHORT QueSequence = {0x8000};

static CHAR tempstr[100];

bool findAllQueueEntries(void *unused, void* d);
bool findReturnNexusMatch(void *nid, void* d);
void cleanupOrphanOutMessages(void *unusedptr, void* d);


void blitzNexusFromQueue(HCTIQUEUE q, CTINEXUS *&Nexus)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Attempting to remove all queue entries for bad nexus 0x" << Nexus << endl;
    }
    CleanQueue( q, (void*)Nexus, findReturnNexusMatch, cleanupOrphanOutMessages );
}

void blitzNexusFromCCUQueue(CtiDevice *Device, CTINEXUS *&Nexus)
{
    if(Device->getType() == TYPE_CCU711)
    {
        CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)Device->getTrxInfo();

        if(pInfo)
        {
            ULONG QueEntCnt;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Attempting to remove all queue entries for bad nexus 0x" << Nexus << endl;
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

/* Thread to process and dispatch entries from Queue & ACTIN queues */
VOID QueueThread (VOID *Arg)
{
    USHORT Port, Remote;
    DWORD  dwWait = 0;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Queue Thread Starting as TID " << CurrentTID() << endl;
    }

    HANDLE hQueueArray[] = {
        hPorterEvents[P_QUIT_EVENT],
        hPorterEvents[P_QUEUE_EVENT]
    };

    /* make it clear who isn't the boss */
    CTISetPriority(PRTYS_THREAD, PRTYC_REGULAR, -10, 0);

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
            RWRecursiveLock<RWMutexLock>::LockGuard   guard(DeviceManager.getMux());                  // Protect our iteration!
            CtiRTDB<CtiDeviceBase>::CtiRTDBIterator   itr(DeviceManager.getMap());

            for(; ++itr ;)
            {
                CtiDeviceBase *Dev = itr.value();
                if(!Dev->isInhibited() && Dev->getType() == TYPE_CCU711)
                {
                    CtiTransmitter711Info *pInfo = (CtiTransmitter711Info*)Dev->getTrxInfo();
                    if(pInfo != NULL)
                    {
                        if(pInfo->ActinQueueHandle != (HCTIQUEUE) NULL)
                        {
                            BuildActinShed(Dev);
                        }

                        /* Now check out the queue queue handle */
                        if(pInfo->QueueHandle != (HCTIQUEUE) NULL)
                        {
                            /* See if we have one outstanding */
                            if(!(pInfo->GetStatus(INLGRPQ)))
                            {
                                BuildLGrpQ(Dev);
                            }
                        }
                    }
                }
            }
        }
    }
}


/* Routine to process results from CCU's */
CCUResponseDecode (INMESS *InMessage, CtiDevice *Dev, OUTMESS *OutMessage)
{
    extern LoadRemoteRoutes(CtiDeviceBase *RemoteRecord);

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

    if(ErrorReturnCode                       &&
       InMessage->Sequence & 0x8000          &&
       !(ErrorReturnCode == REQACK && OutMessage->Retry > 0))
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
                dout << RWTime() << " Orphaned CCU queue entry response.  It will be ignored.  InMessage->Sequence 0x" << hex << (int)InMessage->Sequence << dec << endl;
                dout << "  Not found in any queue slot" << endl;
            }
        }
    }

    if(ErrorReturnCode && ErrorReturnCode != REQACK)     // Handle non-Sequence & 0x8000 (queued entries)
    {
        return(ErrorReturnCode);
    }


    /* check the RCONT flag in the returned message */
    if(InMessage->IDLCStat[2] & 0x10)
    {
        pInfo->ClearStatus(INRCONT);
    }
    else
    {
        pInfo->SetStatus(INRCONT);
    }


    /* Decode the important contents of message header */

    /* Check if we have a CTIDBG_new power fail */
    if(InMessage->IDLCStat[6] & STAT_POWER)
    {
        if(!(pInfo->GetStatus(POWERFAILED)))
        {
            pInfo->SetStatus (POWERFAILED);

            _snprintf(tempstr, 99,"Power Fail Detected on Port: %2hd Remote: %3hd... Resetting\n", InMessage->Port, InMessage->Remote);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << tempstr << endl;
            }
            IDLCFunction (Dev, 0, DEST_BASE, CLPWR);

            /* Now send a message to logger */
            _snprintf(Message, 50, "%0.20sPower Fail Detected", Dev->getName());
            SendTextToLogger ("Inf", Message);
        }
    }
    else
    {
        pInfo->ClearStatus(POWERFAILED);
    }


    /* Check if we need to issue a time sync to this guy */
    if(InMessage->IDLCStat[7] & STAT_BADTIM)
    {
        if(!(pInfo->GetStatus(TIMESYNCED)))
        {
            pInfo->SetStatus(TIMESYNCED);
            _snprintf(tempstr, 99,"Time Sync Loss Detected on Port: %2hd Remote: %3hd... Issuing Time Sync\n", InMessage->Port, InMessage->Remote);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << tempstr << endl;
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
                    if(PortManager.writeQueue (TimeSyncMessage->Port, TimeSyncMessage->EventCode, sizeof (*TimeSyncMessage), (char *)TimeSyncMessage, TimeSyncMessage->Priority))
                    {
                        _snprintf(tempstr, 99,"Error Writing to Queue for Port %2hd\n", InMessage->Port);
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " " << tempstr << endl;
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
            _snprintf(Message, 50,  "%0.20sTime Sync Loss", Dev->getName());
            SendTextToLogger ("Inf", Message);
        }
    }
    else
    {
        pInfo->ClearStatus (TIMESYNCED);
    }


    /* Check if we need to issue a Bad Battery Message */
    if(InMessage->IDLCStat[7] & STAT_BATTRY)
    {
        if(!(pInfo->GetStatus(LOWBATTRY)))
        {
            pInfo->SetStatus(LOWBATTRY);
            _snprintf(tempstr, 99,"Low Battery Detected on Port: %2hd Remote: %3hd... Replace Logic Card\n", InMessage->Port, InMessage->Remote);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << tempstr << endl;
            }

            /* Now send a message to logger */
            _snprintf(Message, 50,  "%0.20sLow Battery Detected", Dev->getName());
            SendTextToLogger ("Inf", Message);
        }
    }
    else
    {
        pInfo->ClearStatus(LOWBATTRY);
    }


    /* Check if we have need to re-download this ccu********  Logic needs improvement */
    if(InMessage->IDLCStat[6] & (STAT_FAULTC | STAT_DEADMN | STAT_COLDST))
    {
        if(!(InMessage->IDLCStat[6] & STAT_COLDST) && !(pInfo->FreeSlots < MAXQUEENTRIES && !pInfo->PortQueueEnts))
        {
            /* Issue a cold start */
            _snprintf(tempstr, 99,"Reset Needed on Port: %2hd Remote: %3hd... Cold Starting\n", InMessage->Port, InMessage->Remote);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << tempstr << endl;
            }

            IDLCFunction (Dev, 0, DEST_BASE, COLD);

            /* Now send a message to logger */
            if(NULL != Dev)
            {
                _snprintf(Message, 50,  "%0.20sCold Start Sent", Dev->getName());
                SendTextToLogger ("Inf", Message);
            }
        }
        else if(!(pInfo->GetStatus(RESETTING)))
        {
            pInfo->SetStatus(RESETTING);
            if(InMessage->IDLCStat[6] & STAT_FAULTC)
            {
                printf ("Fault Detected on Port: %2hd Remote: %3hd... Reloading\n", InMessage->Port, InMessage->Remote);

                /* Yup */
                IDLCFunction (Dev, 0, DEST_BASE, CLFLT);

                /* Now send a message to logger */
                if(NULL != Dev)
                {
                    _snprintf(Message, 50,  "%0.20sFAULTC Detected", Dev->getName());
                    SendTextToLogger ("Inf", Message);
                }
            }

            if(InMessage->IDLCStat[6] & STAT_COLDST)
            {
                _snprintf(tempstr, 99,"Cold Start Detected on Port: %2hd Remote: %3hd... Reloading\n", InMessage->Port, InMessage->Remote);
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << tempstr << endl;
                }
                IDLCFunction (Dev, 0, DEST_BASE, CLCLD);

                /* Assume this could be a CTIDBG_new chip */
                pInfo->RColQMin = 0;

                /* Best to flush the queues */
                QueueFlush (Dev);

                /* Now send a message to logger */
                if(NULL != Dev)
                {
                    _snprintf(Message, 50,  "%0.20sCold Start Detected", Dev->getName());
                    SendTextToLogger ("Inf", Message);
                }
            }

            if(InMessage->IDLCStat[6] & STAT_DEADMN)
            {
                _snprintf(tempstr, 99,"Dead Man Detected on Port: %2hd Remote: %3hd... Reloading\n", InMessage->Port, InMessage->Remote);
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << tempstr << endl;
                }

                IDLCFunction (Dev, 0, DEST_BASE, CLDMN);

                /* Best to flush the queues */
                QueueFlush(Dev);

                /* Now send a message to logger */
                if(NULL != Dev)
                {
                    _snprintf(Message, 50,  "%0.20s Deadman Detected", Dev->getName());
                    SendTextToLogger ("Inf", Message);
                }
            }

            /* Load the delay sets if any */
            IDLCSetDelaySets(Dev);

            /* set the Base Status List */
            pInfo->SetStatus (SETSLIST);

            IDLCSetBaseSList (Dev);

            LoadRemoteRoutes (Dev);

            /* set the time sync Algorithm startup time */
            IDLCSetTSStores (Dev, 15, 600, 3600); // Priority, trigger time, period

            /* Enable the time Sync algorithm */
            IDLCFunction (Dev, 0,  DEST_TSYNC, ENPRO);

        }
    }
    else
    {
        pInfo->ClearStatus(RESETTING);
    }


    /* Check the various algorithm status's and correct as neccessary */
    if(IDLCAlgStat (&InMessage->IDLCStat[8], AlgStatus))
    {
        if(!(pInfo->GetStatus(SETSLIST)))  /* Check if we are in the process of doing this */
        {
            pInfo->SetStatus(SETSLIST);
            /* set the Base Status List */
            IDLCSetBaseSList (Dev);
        }
    }
    else
    {
        /* Make sure we can update list if neccessary */
        pInfo->ClearStatus (SETSLIST);

        if(AlgStatus[DEST_DLC] != ALGO_RUNNING)  /* Check the DLC algorithm status */
        {
            IDLCFunction (Dev, 0, DEST_DLC, START);

            if(NULL != Dev)   /* Now send a message to logger */
            {
                _snprintf(Message, 50,  "%0.20sDLC Alg Restarted", Dev->getName());
                SendTextToLogger ("Inf", Message);
            }
        }

        if( ((CtiDeviceCCU *)Dev)->checkForTimeSyncLoop(AlgStatus[DEST_TSYNC]) )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - time sync loop detected, sending cold start **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            //  Send a cold start, the time sync algorithm has been set to 0 seconds
            IDLCFunction (Dev, 0, DEST_BASE, COLD);
        }
        else
        {
            /* Check the time sync Algorithm status */
            if(AlgStatus[DEST_TSYNC] != ALGO_ENABLED)
            {
                if(AlgStatus[DEST_TSYNC] == ALGO_HALTED)
                {
                    IDLCFunction (Dev, 0, DEST_TSYNC, ENPRO);
                    /* Now send a message to logger */
                    if(NULL != Dev)
                    {
                        _snprintf(Message, 50,  "%0.20sTS Alg ENABLED", Dev->getName());
                        SendTextToLogger ("Inf", Message);
                    }
                }
                else if(AlgStatus[DEST_TSYNC] == ALGO_RUNNING || AlgStatus[DEST_TSYNC] == ALGO_SUSPENDED)
                {
                    IDLCFunction (Dev, 0, DEST_TSYNC, HOPRO);
                }
            }
        }

        /* Check the status of the LM algorithm */
        if(AlgStatus[DEST_LM] != ALGO_ENABLED)
        {
            IDLCFunction (Dev, 0, DEST_LM, ENPRO);

            /* Now send a message to logger */
            if(NULL != Dev)
            {
                _snprintf(Message, 50,  "%0.20sLM Alg ENPRO", Dev->getName());
                SendTextToLogger ("Inf", Message);
            }
        }
    }

    /* there are more STATS but these are a start ..... <-----<<<<<<<------- */

    /* Now decode pertinant STATD information */
    /* Check for persistant DLC fault */
    if(InMessage->IDLCStat[10] & STAT_DLCFLT)
    {
        if(!(pInfo->GetStatus(DLCFAULT)))
        {
            pInfo->SetStatus(DLCFAULT);
            _snprintf(tempstr, 99,"Persistant DLC Fault Detected on Port: %2hd Remote: %3hd\n", InMessage->Port, InMessage->Remote);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << tempstr << endl;
            }
        }
    }
    else
    {
        pInfo->ClearStatus(DLCFAULT);
    }

    /* Load the CCU's interpretation of queue Info available */
    pInfo->ReadyN = InMessage->IDLCStat[12];
    pInfo->NCsets = InMessage->IDLCStat[13];
    pInfo->NCOcts = MAKEUSHORT (InMessage->IDLCStat[15],InMessage->IDLCStat[14]);


    //  this is more data than we can ever receive in a single packet - the CCU is confused
    if( pInfo->NCsets == 1 && pInfo->NCOcts > 241 )
    {
        //  CCU's queues are messed up and need to be reset
        IDLCFunction (Dev, 0, DEST_BASE, COLD);

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - CCU \"" << Dev->getName() << "\"'s internal queue is corrupt, sending cold start ";
            dout << " (NCsets = " << pInfo->NCsets << ", NCOcts = " << pInfo->NCOcts << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }


    /* Need checks on STATP here <<<<<<<<<---------<<<<<<<<<<< */

    if(InMessage->IDLCStat[6] & STAT_REQACK)
    {
        _snprintf(tempstr,99,"REQACK Detected on Port: %2hd Remote: %3hd\n", InMessage->Port, InMessage->Remote);
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << tempstr << endl;
        }

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
                    _snprintf(Message, 50,  "%0.20sBad Firmware Adjust ", Dev->getName());
                    SendTextToLogger ("Inf", Message);
                }
                else if(InMessage->IDLCStat[3] - 14 < 61)
                {
                    pInfo->RColQMin = 61;
                    _snprintf(Message, 50,  "%0.20sBad Firmware Adjust2", Dev->getName());
                    SendTextToLogger ("Inf", Message);
                }
            }
            else if(pInfo->RColQMin == 15)
            {
                if(InMessage->IDLCStat[3] - 14 >= 15 && InMessage->IDLCStat[3] - 14 < 61)
                {
                    pInfo->RColQMin = 61;
                    _snprintf(Message, 50,  "%0.20sBad Firmware Adjust2", Dev->getName());
                    SendTextToLogger ("Inf", Message);
                }
            }
        }
        else if( !pInfo->GetStatus(INRCOLQ) && pInfo->NCOcts)
        {
            // REQACK was possibly due to full CCU Queue.
            // We must send an RCOLQ to empty it out!

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " REQACK with outstanding NCOcts.  Will RCOLQ." << endl;
            }

            if(pInfo->NCsets || pInfo->ReadyN)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " CCU " << Dev->getName() << endl;
                    dout << RWTime() << "   CCU Command Sets Complete (NCSets)   " << pInfo->NCsets << endl;
                    dout << RWTime() << "   CCU Command Slots Available (ReadyN) " << pInfo->ReadyN << endl;
                }
            }
            // And do the RCOLQ.  CGP -> Should be HIGHEST priority!
            IDLCRColQ(Dev, MAXPRIORITY);
        }

        if( OutMessage->Retry > 0 )
        {
            // Decrement retries...
            OutMessage->Retry--;

            /* Put it back on the queue for this port */
            if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Error Requeing Command" << endl;
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " REQACK'd LGRPQ message has been requeued for retry on " << Dev->getName() << endl;
            }

            return RETRY_SUBMITTED;
        }
        else
        {
            return REQACK;
        }

    }

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
                _snprintf(tempstr, 99,"Wrong Offset into CCU Queue Entry Table\n");
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << tempstr << endl;
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
                    dout << RWTime() << " Entry Received into Unused CCU Queue Entry on \"" << Dev->getName() << "\"" << endl;
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
                    dout << RWTime() << " Unknown Queue Entry Received... Ignoring on \"" << Dev->getName() << "\"" << endl;
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

                /* Load up the info out of the CCUInfo Structure */
                ResultMessage.TargetID        = pInfo->QueTable[QueTabEnt].TargetID;
                ResultMessage.ReturnNexus     = pInfo->QueTable[QueTabEnt].ReturnNexus;
                ResultMessage.SaveNexus       = pInfo->QueTable[QueTabEnt].SaveNexus;
                ResultMessage.Priority        = pInfo->QueTable[QueTabEnt].Priority;
                ResultMessage.Sequence        = pInfo->QueTable[QueTabEnt].OriginalOutMessageSequence;

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
                            ResultMessage.EventCode = EWORDRCV;

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
                /* this is a completed result so send it to originating process */
                ResultMessage.EventCode |= DECODED;
                if( (SocketError = ResultMessage.ReturnNexus->CTINexusWrite(&ResultMessage, sizeof (ResultMessage), &BytesWritten, 60L)) != NORMAL)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Error writing to nexus. CCUResponseDecode().  "
                            << "Wrote " << BytesWritten << "/" << sizeof(ResultMessage) << " bytes" << endl;
                    }

                    if(CTINEXUS::CTINexusIsFatalSocketError(SocketError))
                    {
                        status = SocketError;
                    }
                }

                statisticsNewCompletion( ResultMessage.Port, ResultMessage.DeviceID, ResultMessage.TargetID, ResultMessage.EventCode & 0x3fff );
            }
            else
            {
                /* we are not going to return results so jump over this one */
                /* NOTE... This will need to change for statistics */
                Offset += setL - 5;
            }

            /* Free up the entry in the QueTable */
            pInfo->QueTable[QueTabEnt].InUse = 0;
            pInfo->QueTable[QueTabEnt].TimeSent = -1L;
            pInfo->FreeSlots++;
        }

        if(Offset == 1)
        {
            /* We didn't process anything so see if we have gotten out of step with the ccu */
            if(pInfo->FreeSlots != 32 && pInfo->ReadyN == 32)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << Dev->getName() << "'s bookkeeping has gotten out of sync with the field device"<< endl;
                    dout << RWTime() << "   Attempting to purge queue's and ccu queues."<< endl;
                }
                CCUQueueFlush(Dev);     /* Flush whatever we think is in the ccu */

                if(pInfo->FreeSlots != 32)    /* Now check it again */
                {
                    if(!(pInfo->PortQueueEnts)) /* See if we have any outstanding messages */
                    {
                        QueueFlush(Dev); /* Bummer */
                    }
                }
            }
        }
    }

    /* see if anything is in the ccu queues */
#ifdef DEBUG
    if(pInfo->FreeSlots != 32)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        pInfo->ClearStatus(INRCOLQ);
    }

    /* See if we need to clear an RCONT */
    if(pInfo->GetStatus(INRCONT))
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
    else if(pInfo->FreeSlots < MAXQUEENTRIES && !(pInfo->GetStatus(INRCOLQ)) && pInfo->NCOcts)
    {
        IDLCRColQ(Dev);
    }

    return(status);
}


/* Thread to kick the CCU-711 if no activity and it might have data */
VOID KickerThread (VOID *Arg)
{
    USHORT Port, Remote;
    ULONG i;
    ULONG sleepTime = 15000L;

    /* make it clear who isn't the boss */
    CTISetPriority(PRTYS_THREAD, PRTYC_REGULAR, -15, 0);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Queue Kicker Thread Starting as TID:  " << CurrentTID() << endl;
    }

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
            RWRecursiveLock<RWMutexLock>::LockGuard   guard(DeviceManager.getMux());                  // Protect our iteration!
            CtiRTDB<CtiDeviceBase>::CtiRTDBIterator   itr(DeviceManager.getMap());

            for(; ++itr ;)
            {
                CtiDeviceBase *Dev = itr.value();

                {
                    switch(Dev->getType())
                    {
                    case TYPE_CCU711:
                        {
                            RWRecursiveLock<RWMutexLock>::LockGuard   devguard(Dev->getMux());                  // Protect our device!

                            CtiTransmitter711Info *pInfo = (CtiTransmitter711Info*)Dev->getTrxInfo();

#if 0

                            /* see if anything is in the ccu queues */
                            if(pInfo->FreeSlots != MAXQUEENTRIES || pInfo->PortQueueEnts > 1 || pInfo->NCOcts != 0)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " " << Dev->getName() << " (on-device) Queue Report: " << endl;
                                dout << "Port: " << Dev->getPortID() <<
                                "  Remote: " << Dev->getAddress() <<
                                "  FreeSlots: " << pInfo->FreeSlots <<
                                "  PortQueEnts: " << pInfo->PortQueueEnts <<
                                "  NCOcts: " << pInfo->PortQueueConts << endl;
                            }
#endif
                            if(pInfo->FreeSlots < MAXQUEENTRIES)
                            {
                                /* Check if we have anything done yet */
                                for(i = 0; i < MAXQUEENTRIES; i++)
                                {
                                    if(pInfo->QueTable[i].InUse & INCCU)
                                    {
                                        ULONG nowtime = LongTime();

                                        if(pInfo->QueTable[i].TimeSent <= nowtime - 5L)
                                        {
                                            IDLCRColQ(Dev);
                                            break;
                                        }
                                        else
                                        {
                                            sleepTime = 2500L;                             // Wake every 2.5 seconds in this case.
                                        }
                                    }
                                }
                            }
                            else if(pInfo->NCOcts)
                            {
                                /* Opps... something got left behind */
                                IDLCRColQ(Dev);
                            }

                            break;
                        }
                    }
                }
            }
        }
    }
}


/* Routine to flush outstanding 711 queue's for a given CCU */
/* Dequeues only those entries marked as having been successfully queued and INCCU */
CCUQueueFlush (CtiDeviceBase *Dev)
{
    USHORT QueTabEnt;
    INMESS InMessage;
    ULONG  BytesWritten;

    CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)Dev->getTrxInfo();

    if(pInfo->FreeSlots < MAXQUEENTRIES)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Flushing " << Dev->getName() << "'s INCCU Porter Queue Table" << endl;
        }

        /* walk through the entry table for this ccu */
        for(QueTabEnt = 0; QueTabEnt < MAXQUEENTRIES; QueTabEnt++)
        {
            if(pInfo->QueTable[QueTabEnt].InUse & INCCU)
            {
                /* send a message to the calling process */
                if(pInfo->QueTable[QueTabEnt].EventCode & RESULT)
                {
                    InMessage.Port = Dev->getPortID();
                    InMessage.Remote = Dev->getAddress();
                    InMessage.Sequence = pInfo->QueTable[QueTabEnt].OriginalOutMessageSequence;
                    InMessage.ReturnNexus = pInfo->QueTable[QueTabEnt].ReturnNexus;
                    InMessage.SaveNexus = pInfo->QueTable[QueTabEnt].SaveNexus;
                    InMessage.Priority = pInfo->QueTable[QueTabEnt].Priority;
                    InMessage.Return = pInfo->QueTable[QueTabEnt].Request;
                    InMessage.Time = LongTime();
                    InMessage.MilliTime =  0;
                    InMessage.EventCode = QUEUEFLUSHED | DECODED;

                    if(pInfo->QueTable[QueTabEnt].EventCode & BWORD)
                    {
                        InMessage.Buffer.DSt.Address = pInfo->QueTable[QueTabEnt].Address;
                    }

                    /* send message back to originating process */
                    if(InMessage.ReturnNexus->CTINexusWrite (&InMessage, sizeof (InMessage), &BytesWritten, 60L) || BytesWritten == 0)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Error Writing to nexus " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        InMessage.ReturnNexus->CTINexusClose();
                    }
                }

                pInfo->QueTable[QueTabEnt].InUse = 0;
                pInfo->QueTable[QueTabEnt].TimeSent = -1L;
                pInfo->FreeSlots++;
            }
        }
    }
    return(NORMAL);
}


/* Routine to flush the 711 queue's for a given CCU */
/* Dequeues only those entries marked as having been slated for queing (likely on the port queue) and INUSE */
QueueFlush (CtiDevice *Dev)

{
    USHORT QueTabEnt;
    INMESS InMessage;
    ULONG BytesWritten;

    CtiTransmitter711Info *pInfo = (CtiTransmitter711Info*)Dev->getTrxInfo();

    if(pInfo->FreeSlots < MAXQUEENTRIES)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Flushing " << Dev->getName() << "'s INUSE Porter Queue Table" << endl;
        }

        /* walk through the entry table for this ccu */
        for(QueTabEnt = 0; QueTabEnt < MAXQUEENTRIES; QueTabEnt++)
        {
            if(pInfo->QueTable[QueTabEnt].InUse & INUSE)
            {
                /* send a message to the calling process */
                if(pInfo->QueTable[QueTabEnt].EventCode & RESULT)
                {
                    InMessage.DeviceID      = Dev->getID();
                    InMessage.Port          = Dev->getPortID();
                    InMessage.Remote        = Dev->getAddress();
                    InMessage.TargetID      = pInfo->QueTable[QueTabEnt].TargetID;    // This is the DID of the target DLC device ( MCT_XYZ )
                    InMessage.Sequence      = pInfo->QueTable[QueTabEnt].OriginalOutMessageSequence;    // Client identifier?
                    InMessage.ReturnNexus   = pInfo->QueTable[QueTabEnt].ReturnNexus; // This is who asked for it.
                    InMessage.SaveNexus     = pInfo->QueTable[QueTabEnt].SaveNexus;
                    InMessage.Priority      = pInfo->QueTable[QueTabEnt].Priority;
                    InMessage.Return        = pInfo->QueTable[QueTabEnt].Request;
                    InMessage.Time          = LongTime();
                    InMessage.MilliTime     =  0;
                    InMessage.EventCode     = QUEUEFLUSHED | DECODED;                 // Indicates the result of the request.. The CCU queue was blown away!

                    if(pInfo->QueTable[QueTabEnt].EventCode & BWORD)                  // If the outbound request was a BWORD then...
                    {
                        InMessage.Buffer.DSt.Address = pInfo->QueTable[QueTabEnt].Address;
                    }

                    /* send message back to originating process */
                    if(InMessage.ReturnNexus->CTINexusWrite(&InMessage, sizeof (InMessage), &BytesWritten, 60L) || BytesWritten == 0)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Error Writing to nexus " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        InMessage.ReturnNexus->CTINexusClose();
                    }
                }

                pInfo->QueTable[QueTabEnt].InUse = 0;
                pInfo->QueTable[QueTabEnt].TimeSent = -1L;
            }
        }
        pInfo->FreeSlots = MAXQUEENTRIES;
    }
    return(NORMAL);
}


BuildLGrpQ (CtiDevice *Dev)
{
    ULONG Length;
    ULONG Count;
    OUTMESS *MyOutMessage, *OutMessage;
    ULONG i, j;
    REQUESTDATA QueueResult;
    USHORT Offset;
    USHORT SETLPos, QueTabEnt;
    BYTE Priority = 7;
    USHORT QueueEntrySequence;

    CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)Dev->getTrxInfo();

    if(QueryQueue (pInfo->QueueHandle, &Count))
    {
        _snprintf(tempstr, 99,"Error Querying Queue Port: %2hd Remote: %3hd\n", Dev->getPortID(), Dev->getAddress());
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << tempstr << endl;
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
                if(ReadQueue (pInfo->QueueHandle, &QueueResult, &Length, (PVOID *) &MyOutMessage, 0, DCWW_WAIT, &Priority))
                {
                    _snprintf(tempstr, 99,"Error Reading Queue\n");
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << tempstr << endl;
                    }
                    return(QUEUE_READ);
                }
                delete (MyOutMessage);
                _snprintf(tempstr, 99,"Broadcast Entry Received on Queue Queue for Port:  %hd\n", Dev->getPortID());
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << tempstr << endl;
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
        for(i = 1; i <= Count; i++)
        {
            /* For now limit to one Lgrpq per ccu on queue at a time */
            if(pInfo->GetStatus(INLGRPQ))
            {
                return(NORMAL);
            }

            /* get the client submitted entry from the queue */
            if(ReadQueue (pInfo->QueueHandle, &QueueResult, &Length, (PVOID *) &MyOutMessage, 0, DCWW_WAIT, &Priority))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Error Reading Device Queue of " << Dev->getName() << endl;
                }
                return(QUEUE_READ);
            }

            /* if this is first in the group get memory for it */
            if(Offset == PREIDL)
            {
                if((OutMessage = CTIDBG_new OUTMESS(*MyOutMessage)) == NULL)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Error Allocating Memory" << endl;
                    }
                    return(MEMORY);
                }

                OutMessage->Priority       = Priority;
                OutMessage->DeviceID       = Dev->getID();
                OutMessage->TargetID       = Dev->getID();
                OutMessage->Remote         = Dev->getAddress();
                OutMessage->Port           = Dev->getPortID();
                OutMessage->Sequence       = QueueEntrySequence;              // Tells us on return that we are queued ( >= 0x8000 )
                OutMessage->EventCode      = NOWAIT | RESULT | RCONT;
                OutMessage->TimeOut        = TIMEOUT;
                OutMessage->Retry          = 2;
                OutMessage->InLength       = 0;

                OutMessage->Source         = 0;
                OutMessage->Destination    = DEST_QUEUE;
                OutMessage->Command        = CMND_LGRPQ;
                OutMessage->ReturnNexus    = NULL;                    // This message IS NOT reportable to the requesting client!
            }

            /* save where to put the length of this entry */
            SETLPos = Offset++;

            /* Find the first open entry in the QueTable */
            for(QueTabEnt = 0; QueTabEnt < MAXQUEENTRIES; QueTabEnt++)
            {
                if(!pInfo->QueTable[QueTabEnt].InUse)
                    break;
            }

            /*
             *  20020703 CGP
             *  In a perfect world, the loop above will never have NOT found an open slot...  What if we didn't though?
             *  It is never nice to stomp memory.  The block below will attempt something CTIDBG_new and exciting.
             *  This "could" happen if FreeSlots had gone south.
             */
            if(QueTabEnt == MAXQUEENTRIES)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " We've fallen off the back of " << Dev->getName() << "'s QueTable." << endl;
                    dout << RWTime() << "   This should never have ever happened." << endl;
                }
            }

            /* tick off free slots available */
            pInfo->FreeSlots--;
            pInfo->QueTable[QueTabEnt].InUse |= INUSE;
            pInfo->QueTable[QueTabEnt].TimeSent = LongTime();          // Erroneous, but not totally evil.  20020703 CGP.  pInfo->QueTable[i].TimeSent = LongTime();

            /* and load the entry */
            pInfo->QueTable[QueTabEnt].TargetID       = MyOutMessage->TargetID;
            pInfo->QueTable[QueTabEnt].ReturnNexus    = MyOutMessage->ReturnNexus;
            pInfo->QueTable[QueTabEnt].SaveNexus      = MyOutMessage->SaveNexus;
            pInfo->QueTable[QueTabEnt].EventCode      = MyOutMessage->EventCode;
            pInfo->QueTable[QueTabEnt].Priority       = MyOutMessage->Priority;
            pInfo->QueTable[QueTabEnt].Address        = MyOutMessage->Buffer.BSt.Address;
            pInfo->QueTable[QueTabEnt].Request        = MyOutMessage->Request;
            pInfo->QueTable[QueTabEnt].OriginalOutMessageSequence  = MyOutMessage->Sequence;            // The orignial requestor's sequence.
            pInfo->QueTable[QueTabEnt].QueueEntrySequence          = QueueEntrySequence;                // The tatoo which makes this entry (QueTabEnt) identifiable with this OUT/INMESS pair.

#ifdef FOLLOW_THE_DSM2_PATH
            OutMessage->Buffer.OutMessage[Offset++] = HIBYTE (QueTabEnt) & 0x007f;
            OutMessage->Buffer.OutMessage[Offset++] = LOBYTE (QueTabEnt);
#endif

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
            OutMessage->Buffer.OutMessage[Offset++] = (UCHAR)MyOutMessage->Buffer.BSt.DlcRoute.Feeder;

            /* Load repeater control */
            OutMessage->Buffer.OutMessage[Offset++] = (MyOutMessage->Buffer.BSt.DlcRoute.RepVar << 5) | (MyOutMessage->Buffer.BSt.DlcRoute.RepFixed & 0x001f);

            /* Load number of repeaters */
            OutMessage->Buffer.OutMessage[Offset++] = (UCHAR)MyOutMessage->Buffer.BSt.DlcRoute.Stages;

            /* Load the number of functions that will be involved */
            if(MyOutMessage->Buffer.BSt.IO & READ)
            {
                /* we are going to ignore the arm request */
                OutMessage->Buffer.OutMessage[Offset++] = 1;

                /* Select B word, single transmit, & read */
                if(Double)
                    OutMessage->Buffer.OutMessage[Offset++] = 0xa8;
                else
                    OutMessage->Buffer.OutMessage[Offset++] = 0x88;

                /* Select the type of read */
                if(MyOutMessage->Buffer.BSt.IO & 0x02)
                    OutMessage->Buffer.OutMessage[Offset - 1] |= 0x10;

                OutMessage->Buffer.OutMessage[Offset++] = 0;

                /* select the Remote */
                OutMessage->Buffer.OutMessage[Offset++] = (UCHAR)MyOutMessage->Buffer.BSt.Function;

                /* Select the number of bytes to read */
                OutMessage->Buffer.OutMessage[Offset++] = (UCHAR)MyOutMessage->Buffer.BSt.Length;
                /* Save the number of bytes to be read */
                pInfo->QueTable[QueTabEnt].Length = MyOutMessage->Buffer.BSt.Length;
            }
            else  /* this has to be a write or function so check if arm needed */
            {
                if(MyOutMessage->Buffer.BSt.IO & (Q_ARML | Q_ARMC | Q_ARMS))
                    OutMessage->Buffer.OutMessage[Offset++] = 2;
                else
                    OutMessage->Buffer.OutMessage[Offset++] = 1;

                if(MyOutMessage->Buffer.BSt.IO & (Q_ARML | Q_ARMC | Q_ARMS))
                {
                    /* Select B word, single transmit, & write */
                    if(Double)
                        OutMessage->Buffer.OutMessage[Offset++] = 0xa0;
                    else
                        OutMessage->Buffer.OutMessage[Offset++] = 0x80;
                    OutMessage->Buffer.OutMessage[Offset++] = 0;

                    /* select the function */
                    if(MyOutMessage->Buffer.BSt.IO & Q_ARML)
                        OutMessage->Buffer.OutMessage[Offset++] = ARML;
                    else if(MyOutMessage->Buffer.BSt.IO & Q_ARMS)
                        OutMessage->Buffer.OutMessage[Offset++] = ARMS;
                    else
                        OutMessage->Buffer.OutMessage[Offset++] = ARMC;

                    /* this is a function so zero length */
                    OutMessage->Buffer.OutMessage[Offset++] = 0;
                }

                /* Now load the real reason we came here */
                /* Select B word, single transmit & write */
                if(Double)
                    OutMessage->Buffer.OutMessage[Offset++] = 0xa0;
                else
                    OutMessage->Buffer.OutMessage[Offset++] = 0x80;

                /* Select the type of write */
                if(MyOutMessage->Buffer.BSt.IO & 0x02)
                    OutMessage->Buffer.OutMessage[Offset - 1] |= 0x10;

                OutMessage->Buffer.OutMessage[Offset++] = 0;

                /* select the address / function */
                OutMessage->Buffer.OutMessage[Offset++] = (UCHAR)MyOutMessage->Buffer.BSt.Function;

                /* select the length of the write */
                OutMessage->Buffer.OutMessage[Offset++] = (UCHAR)MyOutMessage->Buffer.BSt.Length;

                /* if this is a write copy message to buffer */
                if(MyOutMessage->Buffer.BSt.Length)
                {
                    for(j = 0; j < MyOutMessage->Buffer.BSt.Length; j++)
                        OutMessage->Buffer.OutMessage[Offset++] = MyOutMessage->Buffer.BSt.Message[j];
                }

                pInfo->QueTable[QueTabEnt].Length = 0;
            }

            /* we are done with the request message */
            delete (MyOutMessage);

            /* Now load the setl length for this guy */
            OutMessage->Buffer.OutMessage[SETLPos] = Offset - SETLPos;

            /* Check if this is the last one or buffer full */
            if(i == Count || Offset > (MaxOcts - MAXQUEENTLEN - 2))
            {
                OutMessage->Buffer.OutMessage[Offset] = 0;
                OutMessage->OutLength = Offset - PREIDLEN + 2;

                /* Check the priority and do not let it be less than 11 */
                if(OutMessage->Priority < 11)
                    OutMessage->Priority = 11;

                if(PortManager.writeQueue (OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (VOID *) OutMessage, OutMessage->Priority))
                {
                    _snprintf(tempstr, 99,"Error Writing to Queue for Port %2hd\n", OutMessage->Port);
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << tempstr << endl;
                    }
                    Offset = 0;

                    continue;
                }
                else
                {
                    /* Update the port entries count */
                    pInfo->SetStatus(INLGRPQ);
                    pInfo->PortQueueEnts++;
                    pInfo->PortQueueConts++;
                }

                Offset = 0;
            }
        }
    }

    return(NORMAL);
}

BuildActinShed (CtiDevice *Dev)
{
    ULONG Length;
    ULONG Count;
    OUTMESS *MyOutMessage, *OutMessage;
    USHORT Lastset;
    REQUESTDATA QueueResult;
    USHORT Offset;
    BYTE Priority;

    // CCUINFO *pInfo = CCUInfo.findValue(&DevID);
    CtiTransmitter711Info *pInfo = (CtiTransmitter711Info *)Dev->getTrxInfo();

    if(QueryQueue (pInfo->ActinQueueHandle, &Count))
    {
        _snprintf(tempstr, 99,"Error Querying Actin Queue Port: %2hd Remote: %3hd\n", Dev->getPortID(), Dev->getAddress());
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << tempstr << endl;
        }
        return(QUEUE_READ);
    }

    while(Count)
    {
        /* get the entry from the queue */
        if(ReadQueue (pInfo->ActinQueueHandle, &QueueResult, &Length, (PVOID *) &MyOutMessage, 0, DCWW_WAIT, &Priority))
        {
            _snprintf(tempstr, 99,"Error Reading Actin Queue\n");
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << tempstr << endl;
            }
            return(QUEUE_READ);
        }

        if((OutMessage = CTIDBG_new OUTMESS(*MyOutMessage)) == NULL)
        {
            _snprintf(tempstr, 99,"Error Allocating Memory\n");
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << tempstr << endl;
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

        if(PortManager.writeQueue (Dev->getPortID(), OutMessage->EventCode, sizeof (*OutMessage), (VOID *) OutMessage, OutMessage->Priority))
        {
            _snprintf(tempstr, 99,"Error Writing to Queue for Port %2hd\n", Dev->getPortID());
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << tempstr << endl;
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
                dout << RWTime() << " " << tempstr << endl;
            }
            return(QUEUE_READ);
        }
    }

    return(NORMAL);
}


/* Routine to take messages off because of errant LGRPQ */
/* Dequeues only those entries marked as having QueueEntrySequence equal to the inbound message sequence*/
DeQueue (INMESS *InMessage)
{
    INT status = NORMAL;
    INT SocketError = NORMAL;
    INMESS ResultMessage;
    struct timeb TimeB;
    ULONG i;
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

    CtiDeviceBase *TransmitterDev = DeviceManager.getEqual(InMessage->DeviceID);

    if(TransmitterDev != NULL)
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
                        ResultMessage.SaveNexus = pInfo->QueTable[i].SaveNexus;
                        ResultMessage.Return = pInfo->QueTable[i].Request;
                        ResultMessage.EventCode = InMessage->EventCode | DECODED;
                        ResultMessage.Time = InMessage->Time;
                        ResultMessage.MilliTime = InMessage->MilliTime;
                        if(pInfo->QueTable[i].EventCode & BWORD)
                        {
                            ResultMessage.Buffer.DSt.Address = pInfo->QueTable[i].Address;
                        }

                        /* Now send it back */
                        if((SocketError = ResultMessage.ReturnNexus->CTINexusWrite (&ResultMessage, sizeof (ResultMessage), &BytesWritten, 60L)))
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Error writing to return nexus.  DeQueue().  "
                                    << "Wrote " << BytesWritten << "/" << sizeof(ResultMessage) << " bytes.  Error: " << SocketError << endl;
                            }

                            if(CTINEXUS::CTINexusIsFatalSocketError(SocketError))
                            {
                                status = SocketError;
                            }
                        }
                    }
                    /* Now free up the table entry */
                    pInfo->QueTable[i].InUse = 0;
                    pInfo->QueTable[i].TimeSent = -1L;
                    pInfo->FreeSlots++;
                }
            }
        }
    }

    return(status);
}


bool findReturnNexusMatch(void *nid, void* d)
{
    CTINEXUS *rnid = (CTINEXUS *)nid;
    OUTMESS *OutMessage = (OUTMESS *)d;

    return(OutMessage->ReturnNexus == rnid);
}

bool findAllQueueEntries(void *unused, void* d)
{
    OUTMESS *OutMessage = (OUTMESS *)d;
    return true;
}

void cleanupOrphanOutMessages(void *unusedptr, void* d)
{
    OUTMESS *OutMessage = (OUTMESS *)d;

    if(PorterDebugLevel & PORTER_DEBUG_VERBOSE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " OutMessage being cleaned up. " << endl;
    }

    SendError( OutMessage, ErrorQueuePurged );
    // delete OutMessage;

    return;
}



