

#pragma warning (disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   RIPPLE
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/RIPPLE.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2002/10/03 16:16:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma title ( "Ripple Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        ripple.c

    Purpose:
        Routines to build and handle messages to ripple injectors

    The following procedures are contained in this module:
        LCUStage                    LCUShed
        LCUTime

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        9-7-93   Converted to 32 bit                                WRO
        7-24-94  Added support for L&G LCU's                        WRO


   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdio.h>
// #include "btrieve.h"
#include <memory.h>
#include <string.h>
#include <malloc.h>

#include "connection.h"
#include "cparms.h"
#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "portdecl.h"
#include "master.h"
#include "lm_auto.h"
#include "perform.h"
#include "scanner.h"
#include "elogger.h"
#include "mpc.h"
#include "drp.h"

#include "portglob.h"
#include "c_port_interface.h"
#include "dev_base.h"
#include "dev_lcu.h"
#include "mgr_device.h"
#include "mgr_port.h"

#include "logger.h"
#include "guard.h"
#include "utility.h"

extern CtiPortManager   PortManager;
extern HCTIQUEUE*       QueueHandle(LONG pid);

void Send4PartToDispatch(RWCString Source, RWCString MajorName, RWCString MinorName, RWCString Message1 = RWCString(""), RWCString Message2 = RWCString(""));
INT QueueForScan( CtiDeviceLCU *lcu, bool mayqueuescans );
INT QueueAllForScan( CtiDeviceLCU *lcu, bool mayqueuescans );

void SubmitDataToDispatch( RWTPtrSlist< CtiMessage >  &vgList );
INT ReportCompletionStateToLMGroup(CtiDeviceLCU *lcu);     // f.k.a. ReturnTrxID();
INT RequeueLCUCommand( CtiDeviceLCU *lcu );
bool ResetLCUsForControl(LONG PortID);
BOOL LCUsAreDoneTransmitting(CtiDeviceLCU *lcu);
INT LCUProcessResultCode(CtiDeviceLCU *lcu, CtiDeviceLCU *GlobalLCUDev, OUTMESS *OutMessage, INT resultCode);
BOOL areAnyLCUControlEntriesOkToSend(void *pId, void* d);
BOOL areAnyLCUScanEntriesOkToSend(void *pId, void* d);
INT ReleaseAnLCU(OUTMESS *&OutMessage, CtiDeviceLCU *lcu);

bool AnyLCUCanExecute( OUTMESS *&OutMessage, CtiDeviceLCU *lcu, RWTime &Now );
bool LCUCanExecute( OUTMESS *&OutMessage, CtiDeviceLCU *lcu, RWTime &Now );
bool LCUPortHasAnLCUScan( OUTMESS *&OutMessage, CtiDeviceLCU *lcu, RWTime &Now );

bool containsAnyLCUsTransmitting(CtiDeviceManager::val_pair a, void* ptr)
{
    bool bStatus = false;
    CtiDeviceLCU *lcu = (CtiDeviceLCU *)ptr;

    CtiDevice *Dev = a.second;

    if( isLCU(Dev->getType()) && Dev->getPortID() == lcu->getPortID() )
    {
        CtiDeviceLCU *pOtherLCU = (CtiDeviceLCU*)Dev;
        if(pOtherLCU->getLastControlMessage() != NULL)
        {
            bStatus = true;
        }
    }
    return bStatus;
}

bool containsExclusionBlockage(CtiDeviceManager::val_pair a, void* ptr)
{
    bool bStatus = false;
    CtiDeviceLCU *lcu = (CtiDeviceLCU *)ptr;

    CtiDevice *Dev = a.second;

    if( isLCU(Dev->getType()) )
    {
        CtiDeviceLCU *pOtherLCU = (CtiDeviceLCU*)Dev;

        if( CtiDeviceLCU::excludeALL() )        // No LCU may execute simultaneous with another
        {
            RWTime Now;

            if( pOtherLCU->getLastControlMessage() != NULL && pOtherLCU->getNextCommandTime() < Now )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                pOtherLCU->lcuResetFlagsAndTags();
            }


            if(!CtiDeviceLCU::tokenIsAvailable(lcu->getID()))
            {
                bStatus = true;
            }
            else if(pOtherLCU->getNextCommandTime() > Now )
            {
                bStatus = true;
            }
        }

        if(Dev->getPortID() == lcu->getPortID() )
        {
            // Do we need to ACH ACH ACH?
        }
    }

    return bStatus;
}


void ApplyPortXResetOnTimeout(const CtiHashKey *key, CtiDeviceBase *&Dev, void* vpTXlcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vpTXlcu;     // This is the transmitting lcu...  Might be the broadcast LCU (yeah, LCUGLOBAL.. how lazy is that)
    RWTime         Now;
    CtiDeviceLCU   *pOtherLCU;

    if( isLCU(Dev->getType()) && Dev->getPortID() == lcu->getPortID() )
    {
        CtiDeviceLCU *pOtherLCU = (CtiDeviceLCU*)Dev;

        if(pOtherLCU->getNextCommandTime() > rwEpoch && Now > pOtherLCU->getNextCommandTime() )
        {
            if(pOtherLCU->getLastControlMessage() != NULL)
            {
                Send4PartToDispatch ("Rsc", (char*)Dev->getName().data(), "Sequence Complete", "Timeout");
                MPCPointSet ("SEQUENCE COMPLETE");
            }

            pOtherLCU->lcuResetFlagsAndTags();
        }
    }
}

/*
 *  This method identifies all LCU's on the same port as the transmitting port
 *  it marks their next command time out in the future to indicate that they are
 *  prohibited from communicating until that time.
 *
 *  If the transmitting address is the GLOBAL address, and the lcu is not inhibited
 *  it is marked as transmitting via LCUTRANSMITSENT.  Otherwise, the lcu->address
 *  is LCUTRANSMITSENT is lowered and any outbound control message is deleted.
 *
 *  Lastly, the lcu object contains a hacked counter which records the total number of started
 *  lcu's.  This is likely to be only one (itself), but could be as many as the number
 *  of lcu's on the port (minus inhibited ones)
 */
void ApplyPortXLCUSet(const CtiHashKey *key, CtiDeviceBase *&Dev, void* vpTXlcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vpTXlcu;     // This is the transmitting lcu...  Might be the broadcast LCU (yeah, LCUGLOBAL.. how lazy is that)

    if( isLCU(Dev->getType()) && Dev->getPortID() == lcu->getPortID() )
    {
        CtiDeviceLCU *pOtherLCU = (CtiDeviceLCU*)Dev;

        pOtherLCU->setNextCommandTime( lcu->getNextCommandTime() + 1L );  // Mark them all out to this remotes completion time to prevent xtalk

        if( lcu->getAddress() == LCUGLOBAL )
        {
            if( pOtherLCU->getControlInhibit() )
            {
                pOtherLCU->resetFlags( LCUTRANSMITSENT | LCUWASTRANSMITTING );  // This LCU is inhibited, it better not squawk
            }
            else
            {
                pOtherLCU->setFlags( LCUTRANSMITSENT );    // Global address will make this LCU squawk
            }
        }
        else if( lcu->getAddress() != pOtherLCU->getAddress() ) // It isn't me...
        {
            pOtherLCU->lcuResetFlagsAndTags();                            // Make sure nothing is in there!
        }

        if(pOtherLCU->getAddress() != LCUGLOBAL)
        {
            if(pOtherLCU->isFlagSet(LCUTRANSMITSENT))
            {
                lcu->setNumberStarted( lcu->getNumberStarted() + 1 );
            }
        }
    }
    return;
}

/*
 *  This method identifies all LCU's on the same port as the transmitting port
 *  it resets them to their quiescent state and is useful when no-one fired
 *  off based upon this scan request...
 */
void ApplyPortXLCUReset(const CtiHashKey *key, CtiDeviceBase *&Dev, void* vpTXlcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vpTXlcu;     // This is the transmitting lcu...  Might be the broadcast LCU (yeah, LCUGLOBAL.. how lazy is that)

    if( isLCU(Dev->getType()) && Dev->getPortID() == lcu->getPortID() )
    {
        CtiDeviceLCU *pOtherLCU = (CtiDeviceLCU*)Dev;
        pOtherLCU->lcuResetFlagsAndTags();
    }

    return;
}


void ApplyStageTime(const CtiHashKey *key, CtiDeviceBase *&Dev, void* vpTXlcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vpTXlcu;     // This is the transmitting lcu...  Might be the broadcast LCU (yeah, LCUGLOBAL.. how lazy is that)

    if( isLCU(Dev->getType()) && Dev->getPortID() == lcu->getPortID() )
    {
        CtiDeviceLCU *pOtherLCU = (CtiDeviceLCU*)Dev;

        pOtherLCU->setStageTime( lcu->getStageTime() );                      // Everyone staged up at this time (ie the command went out)
        pOtherLCU->setNextCommandTime( lcu->getStageTime() + TIMETOSTAGE );  // I can't send until the damn thing is staged up!
    }

    return;
}

/* Routine to check an LCU message and set flags prior to sending */
/* LCUDevice is the LCU which we are about to transmit through    */
LCUPreSend(OUTMESS *&OutMessage, CtiDeviceBase *Dev)
{
    BOOL           lcuWasSending = FALSE;
    BOOL           commandTimeout = FALSE;
    ULONG          QueueCount;
    CtiDeviceLCU  *lcu = (CtiDeviceLCU*)Dev;

    INT            status = NORMAL;


    if(OutMessage->EventCode & RIPPLE)  /* Check if this is another control message, scans get through */
    {
        OUTMESS *StageOutMessage = lcu->lcuStage(OutMessage); // See if we need any staging... That can go through.

        if(StageOutMessage != NULL)      // Only STANDARD LCU's return non-NULL on the above call.  And only when needed
        {
            if(PortManager.writeQueue (StageOutMessage->Port, StageOutMessage->EventCode, sizeof (*StageOutMessage), (char *) StageOutMessage, StageOutMessage->Priority))
            {
                printf ("\nError putting \"Staging LCU\" entry onto Queue\n");
            }

            /*
             * Write the control OutMessage back to the queue util the staging operation completes.
             * At which time, the getStageTime() will be set to current time, or rwEpoch if the stage command fails...
             */
            if(PortManager.writeQueue (OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
            {
                printf ("\nError Replacing entry onto Queue\n");
            }

            /* Force main routine to continue */
            status = !NORMAL;
        }
        else
        {
            if( (status = ReleaseAnLCU( OutMessage, lcu )) == RETRY_SUBMITTED )
            {
                // Queues have been shuffled
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Queue Shuffled **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }


    return status;
}


BOOL Block;
BOOL OverRetry;

/* Routine to decode result of LCU handshake */
LCUResultDecode (OUTMESS *OutMessage, INMESS *InMessage, CtiDeviceBase *Dev, ULONG Result, bool mayqueuescans)
{
    INT status = Result;

    CtiDeviceLCU  *lcu = (CtiDeviceLCU*)Dev;
    CtiDeviceBase *MyRemoteRecord;
    CtiDeviceLCU  *GlobalLCUDev   = (CtiDeviceLCU*)DeviceManager.RemoteGetPortRemoteEqual(lcu->getPortID(), LCUGLOBAL);
    BOOL NoneStarted = TRUE;

    {
        if(OutMessage->EventCode & STAGE)
        {
            if(!(Result))
            {
                lcu->setStageTime( RWTime() );                     // Device was successfully staged at this time!
            }
            else
            {
                lcu->setStageTime( rwEpoch );   // Device failed to stage
            }

            lcu->setNextCommandTime( RWTime() + TIMETOSTAGE );

            if(lcu->getAddress() == LCUGLOBAL && !Result)
            {
                DeviceManager.apply( ApplyStageTime, (void*)lcu );
            }
        }

        if(OutMessage->EventCode & RIPPLE)        // Indicates a control was sent...
        {
            /* Send the commands to the logger */
            SendBitPatternToLogger (lcu->getName().data(), OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH, OutMessage->OutLength - MASTERLENGTH);
            SendDOToLogger (lcu->getName().data(), OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
            SendTelegraphStatusToMPC (TELEGRAPHSENT, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);

            /* Clear the block flag */
            Block = FALSE;
            OverRetry = FALSE;

            /* get how long this command will take */
            lcu->setNextCommandTime( RWTime() + CtiDeviceLCU::lcuTime(OutMessage, lcu->getLCUType()) );
            lcu->setFlags( LCUTRANSMITSENT );            // This LCU is TXSENT...
            lcu->setLastControlMessage( OutMessage );    // Creates a copy based upon this guy.
            lcu->setNumberStarted( 0 );                  // Reset the number started count..

            DeviceManager.apply(ApplyPortXLCUSet, (void*)lcu );

            if( lcu->getNumberStarted() > 0 )
            {
                if(CtiDeviceLCU::excludeALL())
                {
                    CTISleep( CtiDeviceLCU::getSlowScanDelay() );
                }
                QueueForScan( lcu, mayqueuescans );     // Make porter do a fast scan!
                status = RETRY_SUBMITTED;               // Keep the decode from happening on this stupid thing.
            }
            else if( lcu->getNumberStarted() == 0 )
            {
                /* Clear out the times and the command save */
                DeviceManager.apply(ApplyPortXLCUReset, (void*)lcu );

                Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "ALL LCU Lockout");
                SendTelegraphStatusToMPC (TELEGRAPHEND_NORMAL, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                MPCPointSet ("SEQUENCE COMPLETE");
            }

            if(lcu->getAddress() == LCUGLOBAL)
            {
                ReportCompletionStateToLMGroup(lcu);
            }
        }

        if( lcu->getAddress() != LCUGLOBAL )  /* Check if this decode is the result of a scan */
        {
            if( !(Result) )            // Successful communication
            {
                RWTPtrSlist< CtiMessage >       vgList;
                CtiDeviceLCU::CtiLCUResult_t    resultCode;

                lcu->lcuFastScanDecode(OutMessage, InMessage, resultCode, (GlobalLCUDev != NULL), vgList); // Note: resultCode is modified here!!!
                SubmitDataToDispatch(vgList);
                LCUProcessResultCode(lcu, GlobalLCUDev, OutMessage, resultCode );
            }
            else if(lcu->isFlagSet(LCUTRANSMITSENT))      /* Bad return message & we had a TX indication. */
            {
                if((lcu->getNextCommandTime() > RWTime()) && (lcu->getAddress() != LCUGLOBAL) )
                {
                    if(mayqueuescans)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Queueing " << lcu->getName() << " for fast scan " << endl;
                    }
                    QueueForScan( lcu, mayqueuescans );    // Make porter do a fast scan!
                }
                else
                {
                    // We need to requeue this guy if possible.
                    if( RequeueLCUCommand(lcu) == RETRY_SUBMITTED )
                    {
                        status = RETRY_SUBMITTED;

                        Send4PartToDispatch ("Rmc", (char *)lcu->getName().data(), "Command Resubmited", "Missed Comm");
                        SendTelegraphStatusToMPC (TELEGRAPHEND_ERROR, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                        MPCPointSet ("MISSED COMM");
                    }

                    /* Check if we made it... */
                    if( LCUsAreDoneTransmitting( lcu ))
                    {
                        Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Missed Comm 1");
                        /* Everybody is done... */
                        SendTelegraphStatusToMPC (TELEGRAPHEND_BLOCK, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                        MPCPointSet ("SEQUENCE COMPLETE");
                        ResetLCUsForControl(lcu->getPortID());
                    }
                }
            }
        }
    }

    return(status);
}



/* Routine to send a ripple bit pattern to the logger */
SendBitPatternToLogger (const CHAR *DeviceName, const BYTE *Telegraph, int len)
{
    PCHAR Source = "Rbp";
    CHAR Message[100];
    ULONG i, j;
#if 1

    memset(Message, '\0', sizeof(Message) );

    strcat (Message, "Bit Pattern ");

    /* Print the telegraph... */
    for(j = 0; j < 7; j++)
    {
        for(i = 0; i < 8; i++)
        {
            /* The Specials... */
            if(((j * 8) + i) == 10)
            {
                strcat (Message, " ");
            }
            else if(((j * 8) + i) == 16)
            {
                strcat (Message, " ");
            }
            else if(((j * 8) + i) == 50)
            {
                break;
            }

            /* check the present bit and append appropriatly */
            if(j < len)
            {
                if((Telegraph[j] << i) & 0x80)
                    strcat (Message, "1");
                else
                    strcat (Message, "0");
            }
            else
            {
                strcat (Message, "0");
            }
        }

        if(((j * 8) + i) == 50)
        {
            break;
        }
    }

#endif
    /* Do it */
    return(SendTextToLogger (Source, Message, DeviceName));
}




/* Routine to send double orders to Logger */
SendDOToLogger (const CHAR *DeviceName, const BYTE *Telegraph)
{
#if 1

    return NORMAL;

#else
    PCHAR Source = "Rdo";
    CHAR Message[200];
    ULONG i;
    memset(Message, '\0', sizeof(Message) );

    strcat (Message, "Load Group: ");

    /* Determine the Load Group */
    switch((Telegraph[0] << 2) | ((Telegraph[1] >> 6) & 0x03))
    {
    case 01000:
        strcat (Message, "TEST    ");
        break;

    case 0444:
        strcat (Message, "1.00    ");
        break;

    case 0640:
        strcat (Message, "1.01    ");
        break;

    case 0064:
        strcat (Message, "1.02    ");
        break;

    case 0222:
        strcat (Message, "2.00    ");
        break;

    case 0032:
        strcat (Message, "2.01    ");
        break;

    case 0203:
        strcat (Message, "2.02    ");
        break;

    case 0111:
        strcat (Message, "3.00    ");
        break;

    case 0105:
        strcat (Message, "3.01    ");
        break;

    case 0301:
        strcat (Message, "3.06    ");
        break;

    case 0411:
        strcat (Message, "3.07    ");
        break;

    case 0114:
        strcat (Message, "3.09    ");
        break;

    case 0115:
        strcat (Message, "3.01/9  ");
        break;

    case 0421:
        strcat (Message, "4.00    ");
        break;

    case 0061:
        strcat (Message, "4.01    ");
        break;

    case 0403:
        strcat (Message, "4.02    ");
        break;

    case 0214:
        strcat (Message, "6.00    ");
        break;

    case 0610:
        strcat (Message, "6.01    ");
        break;

    case 0250:
        strcat (Message, "6.06    ");
        break;

    default:
        strcat (Message, "Unknown ");
    }

    /* Determine the area code */
    switch(Telegraph[1] & 0x3f)
    {
    case 000:
        strcat (Message, "Universal         ");
        break;

    case 046:
        strcat (Message, "Minnkota          ");
        break;

    case 045:
        strcat (Message, "Beltrami          ");
        break;

    case 043:
        strcat (Message, "Cass County       ");
        break;

    case 026:
        strcat (Message, "Cavalier Rural    ");
        break;

    case 025:
        strcat (Message, "Clearwater-Polk   ");
        break;

    case 023:
        strcat (Message, "NoDak Rural       ");
        break;

    case 016:
        strcat (Message, "North Star        ");
        break;

    case 015:
        strcat (Message, "PKM Electric      ");
        break;

    case 013:
        strcat (Message, "Red Lake          ");
        break;

    case 064:
        strcat (Message, "Red River Valley  ");
        break;

    case 054:
        strcat (Message, "Roseau Electric   ");
        break;

    case 034:
        strcat (Message, "Sheyenne Valley   ");
        break;

    case 062:
        strcat (Message, "Wild Rice         ");
        break;

    case 052:
        strcat (Message, "NMPA              ");
        break;

    default:
        strcat (Message, "Unknown           ");
        break;

    }


    /* Now Concatenate on the double orders */
    for(i = 17; i < 51; i += 2)
    {
        if(((Telegraph[(i - 1) / 8] << ((i - 1) % 8)) & 0x80) &&
           !((Telegraph[i / 8] << (i % 8)) & 0x80))
        {
            /* This is a valid double order restore */
            sprintf (Message + strlen (Message), "R%02ld", ((i + 1) / 2));
        }
        else if(!((Telegraph[(i - 1) / 8] << ((i - 1) % 8)) & 0x80) &&
                ((Telegraph[i / 8] << (i % 8)) & 0x80))
        {
            /* This is a valid double order shed */
            sprintf (Message + strlen (Message), "S%02ld", ((i + 1) / 2));
        }
        else if(((Telegraph[(i - 1) / 8] << ((i - 1) % 8)) & 0x80) &&
                ((Telegraph[i / 8] << (i % 8)) & 0x80))
        {
            /* This is a valid double order shed */
            sprintf (Message + strlen (Message), "C%02ld", ((i + 1) / 2));
        }
    }


    /* Do it */
    return(SendTextToLogger (Source, Message, DeviceName));
#endif
}


static CTINEXUS TelegraphSentNexus;
static CHAR saveTelegraph[7] = {0,0,0,0,0,0,0};

SendTelegraphStatusToMPC (ULONG Function, const BYTE *MyTelegraph)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    /* That's all the initialization needed */
    return(NORMAL);
}


/* Routine to clear $_MPC related status points */
MPCPointClear (PCHAR Name)
{
#if 0
    DRPVALUE DRPValue;

    /* set the source */
    memcpy (DRPValue.SourceName, "PORTER    ", DESTSIZE);

    /* set the drp message type */
    DRPValue.Type = DRPTYPEVALUE;

    /* Create the Point Name */
    memcpy (DRPValue.DeviceName, "$_MPC_TRANSMISSION  ", STANDNAMLEN);
    strcpy (DRPValue.PointName, Name);
    memset (DRPValue.PointName + strlen (Name), ' ', STANDNAMLEN - strlen (Name));

    /* set the value */
    DRPValue.Value = (FLOAT) OPENED;

    if(DSTFlag ())
    {
        DRPValue.Quality = NORMAL | DSTACTIVE;
    }
    else
    {
        DRPValue.Quality = NORMAL;
    }

    DRPValue.TimeStamp = LongTime ();
    DRPValue.AlarmState = NORMAL;

    /* Ship it to DRP */

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
#endif
    return(NORMAL);
}



/* Routine to set $_MPC related status points */
MPCPointSet (PCHAR Name)
{
#if 0

    DRPVALUE DRPValue;

    /* set the source */
    memcpy (DRPValue.SourceName, "PORTER    ", DESTSIZE);

    /* set the drp message type */
    DRPValue.Type = DRPTYPEVALUE;

    /* Create the Point Name */
    memcpy (DRPValue.DeviceName, "$_MPC_TRANSMISSION  ", STANDNAMLEN);
    strcpy (DRPValue.PointName, Name);
    memset (DRPValue.PointName + strlen (Name), ' ', STANDNAMLEN - strlen (Name));

    /* set the value */
    DRPValue.Value = (FLOAT) CLOSED;

    if(DSTFlag ())
    {
        DRPValue.Quality = NORMAL | DSTACTIVE;
    }
    else
    {
        DRPValue.Quality = NORMAL;
    }

    DRPValue.TimeStamp = LongTime ();
    DRPValue.AlarmState = NORMAL;

    /* Ship it to DRP */

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
#endif
    return(NORMAL);
}


/*
 *  Returns TRUE if all LCU's on the same port are NOT marked as LCUTRANSMITSENT
 */
BOOL LCUsAreDoneTransmitting(CtiDeviceLCU *lcu)
{
    BOOL LCUsFinished = TRUE;
    CtiDeviceLCU *pOtherLCU;

    RWRecursiveLock<RWMutexLock>::LockGuard guard(DeviceManager.getMux()); // Protect our iteration!
    CtiRTDB<CtiDeviceBase>::CtiRTDBIterator itr(DeviceManager.getMap());

    /* Scan through all of them looking for one still transmitting */
    for(; ++itr ;)
    {
        CtiDeviceBase *Dev = itr.value();

        if(isLCU(Dev->getType())                    &&
           Dev->getPortID()     == lcu->getPortID() &&
           Dev->getID()         != lcu->getID()     &&
           Dev->getID()         != LCUGLOBAL           )
        {
            pOtherLCU = (CtiDeviceLCU*)Dev;

            /* LCUTRANSMITSENT is set... Someone is still at it! */
            if(pOtherLCU->isFlagSet(LCUTRANSMITSENT))
            {
                LCUsFinished = FALSE;
                break;
            }
        }
    }

    return LCUsFinished;
}

/*
 *  Do this to reset all lcus on the port and return a true if there was someone that thought he was
 *  transmitting.
 */
bool ResetLCUsForControl(LONG PortID)
{
    bool           WasTrx = false;
    CtiDeviceLCU   *lcu;

    RWRecursiveLock<RWMutexLock>::LockGuard guard(DeviceManager.getMux());        // Protect our iteration!
    CtiRTDB<CtiDeviceBase>::CtiRTDBIterator   itr(DeviceManager.getMap());

    /* Scan through all of them looking for one still transmitting */
    for(; ++itr ;)
    {
        CtiDeviceBase *Dev = itr.value();

        if(isLCU(Dev->getType()) && Dev->getPortID() == PortID )
        {
            lcu = (CtiDeviceLCU*)Dev;

            if((lcu->isFlagSet(LCUWASTRANSMITTING)))
            {
                WasTrx = true;
            }

            lcu->lcuResetFlagsAndTags();
        }
    }

    return WasTrx;
}




INT LCUProcessResultCode(CtiDeviceLCU *lcu, CtiDeviceLCU *GlobalLCUDev, OUTMESS *OutMessage, INT resultCode)
{
    INT  status = NORMAL;
    bool wasTransmitting;

    switch(resultCode)
    {
    case CtiDeviceLCU::eLCUFastScan:
        {
            QueueForScan(lcu, true);
            break;
        }
    case CtiDeviceLCU::eLCUSlowScan:
        {
            CTISleep( CtiDeviceLCU::getSlowScanDelay() );
            QueueForScan(lcu, true);
            break;
        }
    case CtiDeviceLCU::eLCULockedOutSpecificControl:
        {
            /* This means we did not complete so log it */
            Send4PartToDispatch ("Rab", (char*)lcu->getName().data(), "Command Aborted", "LCU Lockout");
            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "LCU Lockout");
            SendTelegraphStatusToMPC (TELEGRAPHEND_BLOCK, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
            ResetLCUsForControl(lcu->getPortID());
            break;
        }
    case CtiDeviceLCU::eLCULockedOut:
        {
            // I sent as the GlobalLCU, and got this response from an individual LCU.
            if(GlobalLCUDev != NULL && GlobalLCUDev->getLastControlMessage() != NULL)
            {
                if( LCUsAreDoneTransmitting( lcu ) )
                {  // Do this iff LCUTRANSMITSENT _NOT_ set for any LCU on this port
                    wasTransmitting = ResetLCUsForControl(lcu->getPortID());    // OK, is someone taking an awefully long time here!

                    if( !(wasTransmitting) )      // No one was transmitting
                    {
                        /*
                         * iff no-one was transmitting on this port, mark them
                         * all for the ability to send a command.
                         */
                        DeviceManager.apply( ApplyPortXLCUReset, (void*)lcu);
                    }

                    Send4PartToDispatch ("Rsc", (char*)GlobalLCUDev->getName().data(), "Sequence Complete", "LCU Lockout");
                    SendTelegraphStatusToMPC (TELEGRAPHEND_BLOCK, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                }
                else  // All LCUs have not finished the transmit.
                {
                    /*
                     * iff the LCU in the args was transmitting, and still is doing so.
                     */
                    if(lcu->getFlags() & LCUWASTRANSMITTING)
                    {
                        Block = TRUE;
                    }

                    lcu->resetFlags(LCUTRANSMITSENT);
                }
            }
            break;
        }
    case CtiDeviceLCU::eLCURequeueDeviceControl:
        {

            if( RequeueLCUCommand( lcu ) == RETRY_SUBMITTED )
            {
                /* This means we did not complete the message so log it */
                Send4PartToDispatch ("Rre", (char*)lcu->getName().data(), "Command Resubmited", "Injector Error");
                SendTelegraphStatusToMPC (TELEGRAPHEND_ERROR, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                MPCPointSet ("MISSED COMM");
            }
            else
            {
                /* This means that we have exceeded the number of times we can send a message */
                lcu->lcuResetFlagsAndTags();
                Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Injector Error");
                SendTelegraphStatusToMPC (TELEGRAPHEND_BLOCK, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                MPCPointSet ("SEQUENCE COMPLETE");
            }

            break;
        }
    case CtiDeviceLCU::eLCURequeueGlobalControl:
        {
            if(GlobalLCUDev!= NULL && GlobalLCUDev->getLastControlMessage()->Sequence)
            {
                if( RequeueLCUCommand(GlobalLCUDev) == RETRY_SUBMITTED )
                {
                    Send4PartToDispatch ("Rlc", (char*)GlobalLCUDev->getName().data(), "Command Resubmited", "Injector Error");
                    MPCPointSet ("MISSED COMM");
                    SendTelegraphStatusToMPC (TELEGRAPHEND_ERROR, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                }
                else
                {
                    /* This means that we have exceeded the number of times we can send a message */
                    GlobalLCUDev->lcuResetFlagsAndTags();
                    Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Injector Error");
                    SendTelegraphStatusToMPC (TELEGRAPHEND_BLOCK, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                    MPCPointSet ("SEQUENCE COMPLETE");
                }
            }
            else
            {
                /* We have exceeded number of resubmissions */
                OverRetry = TRUE;
                lcu->lcuResetFlagsAndTags();
            }

            /* Check if we made it... */
            if( LCUsAreDoneTransmitting( lcu ) )
            {
                /* Everybody is done... */
                if(GlobalLCUDev != NULL && GlobalLCUDev->getFlags() & LCUTRANSMITSENT)
                {
                    Send4PartToDispatch ("Rsc", (char*)GlobalLCUDev->getName().data(), "Sequence Complete", "Injector Error");
                    MPCPointSet ("SEQUENCE COMPLETE");
                }
                else
                {
                    Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(),  "Sequence Complete","Injector Error");
                    MPCPointSet ("MISSED COMM");
                }

                SendTelegraphStatusToMPC (TELEGRAPHEND_BLOCK, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                ResetLCUsForControl(lcu->getPortID());
            }
            break;
        }
    case CtiDeviceLCU::eLCUDeviceControlComplete:
        {
            /* Check if we made it... */
            if( LCUsAreDoneTransmitting( lcu ) )
            {
                if(GlobalLCUDev != NULL && GlobalLCUDev->getFlags() & LCUTRANSMITSENT)
                {
                    if(GlobalLCUDev->getLastControlMessage() != NULL)
                    {
                        if(Block)
                        {
                            Send4PartToDispatch ("Rsc", (char*)GlobalLCUDev->getName().data(), "Sequence Complete", "LCU Lockout");
                            SendTelegraphStatusToMPC (TELEGRAPHEND_BLOCK, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                        }
                        else if(OverRetry)
                        {
                            Send4PartToDispatch ("Rsc", (char*)GlobalLCUDev->getName().data(), "Sequence Complete", "Retry Error");
                            SendTelegraphStatusToMPC (TELEGRAPHEND_BLOCK, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                            MPCPointSet ("SEQUENCE COMPLETE");
                        }
                        else
                        {
                            Send4PartToDispatch ("Rsc", (char*)GlobalLCUDev->getName().data(), "Sequence Complete", "Normal");
                            SendTelegraphStatusToMPC (TELEGRAPHEND_NORMAL, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                        }
                    }
                    else
                    {
                        Send4PartToDispatch ("Rrs", (char*)GlobalLCUDev->getName().data(), "Sequence Continues", "Resubmited");
                        MPCPointSet ("MISSED COMM");
                    }
                }
                else
                {
                    if(lcu->getLastControlMessage() != NULL)
                    {
                        if(Block)
                        {
                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "LCU Lockout");
                            SendTelegraphStatusToMPC (TELEGRAPHEND_BLOCK, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                        }
                        else if(OverRetry)
                        {
                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Retry Error");
                            SendTelegraphStatusToMPC (TELEGRAPHEND_BLOCK, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                            MPCPointSet ("SEQUENCE COMPLETE");
                        }
                        else
                        {
                            /*
                             *  This is a correct completion state (hooray)
                             */
                            ReportCompletionStateToLMGroup(lcu);

                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Normal");
                            SendTelegraphStatusToMPC (TELEGRAPHEND_NORMAL, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                        }
                    }
                    else
                    {
                        Send4PartToDispatch ("Rrs", (char*)lcu->getName().data(), "Sequence Continues", "Resubmited");
                        MPCPointSet ("MISSED COMM");
                    }
                }

                ResetLCUsForControl(OutMessage->Port);
            }
            else
            {
                lcu->lcuResetFlagsAndTags();
            }


            if(PorterDebugLevel & PORTER_DEBUG_RIPPLE)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Pausing " << 2.5 << " seconds. based on PORTER_DEBUGLEVEL & " << PORTER_DEBUG_RIPPLE << endl;
                }

                Sleep(2500);
            }

            break;
        }
    case (CtiDeviceLCU::eLCUDeviceControlCompleteAllowTimeout):
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            if(lcu->getNextCommandTime() < RWTime())
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            break;
        }
    case CtiDeviceLCU::eLCUNotBusyNeverTransmitted:
        {
            if(GlobalLCUDev != NULL && GlobalLCUDev->getLastControlMessage() && GlobalLCUDev->getLastControlMessage()->Sequence)
            {
                if( (status = RequeueLCUCommand(GlobalLCUDev)) == RETRY_SUBMITTED )
                {
                    Send4PartToDispatch ("Rmc", (char *)GlobalLCUDev->getName().data(), "Command Resubmited", "Missed Comm 2");
                    SendTelegraphStatusToMPC (TELEGRAPHEND_ERROR, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                    MPCPointSet ("MISSED COMM");
                }
            }
            else if(lcu != NULL && lcu->getLastControlMessage() != NULL && lcu->getLastControlMessage()->Sequence)
            {
                if( (status = RequeueLCUCommand(lcu)) == RETRY_SUBMITTED )
                {
                    Send4PartToDispatch ("Rmc", (char *)lcu->getName().data(), "Command Resubmited", "Missed Comm 3");
                    SendTelegraphStatusToMPC (TELEGRAPHEND_ERROR, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                    MPCPointSet ("MISSED COMM");
                }
            }
            else
            {
                OverRetry = TRUE;
            }

            /* Check if we made it... */
            if( LCUsAreDoneTransmitting( lcu ) )
            {
                if(GlobalLCUDev != NULL && GlobalLCUDev->getFlags() & LCUTRANSMITSENT)
                {
                    if(GlobalLCUDev->getLastControlMessage() != NULL)
                    {
                        if(Block)
                        {
                            Send4PartToDispatch ("Rsc", (char*)GlobalLCUDev->getName().data(), "Sequence Complete",  "Missed Comm 4");
                            SendTelegraphStatusToMPC (TELEGRAPHEND_BLOCK, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                        }
                        else if(OverRetry)
                        {
                            Send4PartToDispatch ("Rsc", (char*)GlobalLCUDev->getName().data(), "Sequence Complete", "Missed Comm 5");
                            SendTelegraphStatusToMPC (TELEGRAPHEND_BLOCK, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                            MPCPointSet ("SEQUENCE COMPLETE");
                        }
                        else
                        {
                            Send4PartToDispatch ("Rsc", (char*)GlobalLCUDev->getName().data(), "Sequence Complete", "Normal");
                            SendTelegraphStatusToMPC (TELEGRAPHEND_NORMAL, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                        }

                    }
                    else
                    {
                        Send4PartToDispatch ("Rrs", (char*)GlobalLCUDev->getName().data(), "Sequence Continues", "Resubmited");
                        MPCPointSet ("SEQUENCE COMPLETE");
                    }
                }
                else if(lcu != NULL && lcu->getFlags() & LCUTRANSMITSENT)
                {
                    if(lcu->getLastControlMessage() != NULL)
                    {
                        if(Block)
                        {
                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete",  "Missed Comm 6");
                            SendTelegraphStatusToMPC (TELEGRAPHEND_BLOCK, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                        }
                        else if(OverRetry)
                        {
                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Missed Comm 7");
                            SendTelegraphStatusToMPC (TELEGRAPHEND_BLOCK, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                            MPCPointSet ("SEQUENCE COMPLETE");
                        }
                        else
                        {
                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Normal");
                            SendTelegraphStatusToMPC (TELEGRAPHEND_NORMAL, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                        }

                    }
                    else
                    {
                        Send4PartToDispatch ("Rrs", (char*)lcu->getName().data(), "Sequence Continues", "Resubmited");
                        MPCPointSet ("SEQUENCE COMPLETE");
                    }
                }
                else
                {
                    if(lcu->getLastControlMessage() != NULL)
                    {
                        if(Block)
                        {
                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(),  "Sequence Complete", "Missed Comm (block)");
                            SendTelegraphStatusToMPC (TELEGRAPHEND_BLOCK, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                        }
                        else if(OverRetry)
                        {
                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Missed Comm (overretry)");
                            SendTelegraphStatusToMPC (TELEGRAPHEND_BLOCK, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                            MPCPointSet ("SEQUENCE COMPLETE");
                        }
                        else
                        {
                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Missed Comm 8");
                            SendTelegraphStatusToMPC (TELEGRAPHEND_BLOCK, OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
                            MPCPointSet ("SEQUENCE COMPLETE");
                        }
                    }
                }

                /* Everybody is done... */
                ResetLCUsForControl( lcu->getPortID() );
            }
            else
            {
                lcu->lcuResetFlagsAndTags();
            }

            break;
        }
    case CtiDeviceLCU::eLCUAlternateRate:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    }


    return status;
}

/* try to requeue the command */
INT RequeueLCUCommand( CtiDeviceLCU *lcu )
{
    int successflag = NORMAL;

    OUTMESS *OutMessage = lcu->releaseLastControlMessage(); // Don't let it get blown away.;

    if(OutMessage != NULL)
    {
        /*
         *  check if Retries for this LCU is still turned on AND we are not tried out on this message
         */

        if( !(lcu->isFlagSet(LCUNEVERRETRY)) && OutMessage->Sequence > 0)
        {
            // we need to retry this message - put is back on queue
            OutMessage->Sequence--;
            successflag = RETRY_SUBMITTED;

            lcu->setNextCommandTime( rwEpoch );
            lcu->resetFlags(LCUTRANSMITSENT | LCUWASTRANSMITTING);

            if(PortManager.writeQueue (OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Error Writing Retry into Queue " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                successflag = FALSE;

                // Reconnect the OutMessage, it failed to get queued..
                OutMessage->Sequence++;
                lcu->setLastControlMessage( OutMessage );    // Makes a new copy, you must delete the detached one.

                if(OutMessage != NULL)
                {
                    delete OutMessage;
                    OutMessage = NULL;
                }
            }
        }
        else
        {
            lcu->setFlags( LCUNEVERRETRY );     // failed on all retries.. so set flag

            if(OutMessage != NULL)
            {
                delete OutMessage;
                OutMessage = NULL;
            }
        }
    }

    return(successflag);
}

INT ReportCompletionStateToLMGroup(CtiDeviceLCU *lcu)     // f.k.a. ReturnTrxID();
{
    INT      status = 0;

    LONG     LMGIDControl = 0;  // LM Group controlled.  Could be a macro group!
    LONG     Rte = 0;
    UINT     TrxID = 0;

    RWTPtrSlist< CtiMessage >  vgList;

    // this means we had a good message - retries are OK
    lcu->resetFlags( LCUNEVERRETRY );

    if(lcu->getLastControlMessage() != NULL)
    {
        LMGIDControl    = lcu->getLastControlMessage()->DeviceIDofLMGroup;
        TrxID           = lcu->getLastControlMessage()->TrxID;

        // Find the controlling group.
        CtiDeviceBase *pDev = DeviceManager.getEqual( LMGIDControl );

        if(pDev)
        {
            pDev->processTrxID( TrxID, vgList );
        }

        SubmitDataToDispatch( vgList );
    }
    else
    {
        Send4PartToDispatch ("Xxx", (char*)lcu->getName().data(), "Transmission Id", "Was Not found");
        printf("The transmission ID was not where it was expected\n");
    }

    return(status);
}

/*---------------------------------------------------------------------*
 * This function determines whether the OutMessage which got us here is
 * able to be released into the world.  If not, it searches the port's
 * queue and finds one which can be released.  It will then write the
 * non-releasable OUTMESS back to the queue at elevated priority to
 * maintain an appropriate ordering.  The releasable entry will proceed
 * to be executed in the non-releasable entry's place.
 *---------------------------------------------------------------------*/
INT ReleaseAnLCU(OUTMESS *&OutMessage, CtiDeviceLCU *lcu)
{
    INT         status = NORMAL;
    RWTime      Now;                          // Pre-init to current time.
    int         loopCnt = 0;

    do
    {
        Now = Now.now();

        if(LCUCanExecute( OutMessage, lcu, Now ))               // I am the LCU, and my OutMessage can execute.
        {
            status = NORMAL;                                    // I can continue without a REQUEUE
            break;
        }
        else if( AnyLCUCanExecute( OutMessage, lcu, Now ) ||    // Some other non-excluded LCU has a control ready on this port, and has been substituted
                 LCUPortHasAnLCUScan( OutMessage, lcu, Now) )   // Some scan or non-lcu device type has been substituted
        {
            status = RETRY_SUBMITTED;                           // We need to re-ReadQueue as it has been shuffled.
        }
        else                                                    // We have nothing better to do, we may as well hang out here.
        {
            if( !(loopCnt++ % 30) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << lcu->getName() << " control is blocked, no scan is waiting. " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            Sleep(500);
        }

    } while( status == NORMAL );

    return status;
}

/*
 *  Used by SearchQueue.  Must be protected appropriately.
 */
BOOL areAnyLCUControlEntriesOkToSend(void *pRWtime, void* d)
{
    BOOL     bStatus = FALSE;
    OUTMESS  *OutMessage = (OUTMESS *)d;

    RWTime   &Now = *((RWTime*)pRWtime);

    bool     blockedByExclusion = false;

    if(OutMessage->EventCode & RIPPLE)     // Indicates a command message!
    {
        CtiDevice* Dev = DeviceManager.getEqual( OutMessage->DeviceID );

        if(Dev != NULL)
        {
            if(isLCU(Dev->getType()))
            {
                CtiDeviceLCU *lcu = (CtiDeviceLCU*)Dev;      // This IS an LCU

                if( Now > lcu->getNextCommandTime() )
                {
                    RWMutexLock::LockGuard guard( lcu->getExclusionMux() );             // get mux for all LCU's
                    blockedByExclusion = DeviceManager.getMap().contains( containsExclusionBlockage, (void*)lcu);

                    if( !blockedByExclusion )
                    {
                        bStatus = TRUE;
                        CtiDeviceLCU::assignToken( lcu->getID() );
                    }
                }
            }
        }
    }

    return bStatus;
}

/*
 *  Used by SearchQueue.  Must be protected appropriately.
 */
BOOL areAnyLCUScanEntriesOkToSend(void *pRWtime, void* d)
{
    BOOL     bStatus = FALSE;
    OUTMESS  *OutMessage = (OUTMESS *)d;

    RWTime   &Now = *((RWTime*)pRWtime);

    CtiDevice* Dev = DeviceManager.getEqual( OutMessage->DeviceID );

    if(Dev != NULL)
    {
        if(isLCU(Dev->getType()))
        {
            if(!(OutMessage->EventCode & RIPPLE))   // Indicates a command message!
            {
                bStatus = TRUE;
            }
        }
    }

    return bStatus;
}


bool LCUCanExecute(OUTMESS *&OutMessage, CtiDeviceLCU *lcu, RWTime &Now )
{
    bool bStatus = false;
    bool blockedByExclusion = false;

    if( Now > lcu->getNextCommandTime() )
    {
        {
            RWMutexLock::LockGuard guard( lcu->getExclusionMux() );             // get mux for all LCU's

            blockedByExclusion = DeviceManager.getMap().contains( containsExclusionBlockage, (void*)lcu);

            if(!blockedByExclusion)
            {
                CtiDeviceLCU::assignToken( lcu->getID() );

                if( lcu->getType() == TYPE_LCUT3026 && gConfigParms.getValueAsString("RIPPLE_ENFORCE_LCU_DUTY_CYCLE") == RWCString("TRUE") )
                {
                    // Should point to the master header now!
                    blockedByExclusion = lcu->exceedsDutyCycle( OutMessage->Buffer.OutMessage + PREIDLEN );
                }
            }

            if(!blockedByExclusion)     // This lcu is available by time, and is not blocked due to any other criteria.
            {
                bStatus = true;
            }
        }
    }

    return bStatus;
}

bool AnyLCUCanExecute( OUTMESS *&OutMessage, CtiDeviceLCU *lcu, RWTime &Now )
{
    bool bSubstitutionMade = false;
    ULONG QueueCount, ReadLength;
    OUTMESS *pOutMessage = NULL;

    try
    {
        QueryQueue( *QueueHandle(lcu->getPortID()), &QueueCount );

        if(QueueCount)      // There are queue entries.
        {
            // We cannot be executed, we should look for another CONTROL queue entry.. We are still protected by mux...
            INT qEnt = SearchQueue( *QueueHandle(lcu->getPortID()), (void*)&Now, areAnyLCUControlEntriesOkToSend );

            if(qEnt > 0)
            {
                REQUESTDATA    ReadResult;
                BYTE           ReadPriority;

                if(ReadQueue( *QueueHandle(OutMessage->Port), &ReadResult, &ReadLength, (PPVOID)&pOutMessage, qEnt, DCWW_NOWAIT, &ReadPriority))
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
                            dout << RWTime() << " Substituting a different (non-blocked) LCU" << endl;
                        }
                        OutMessage = pOutMessage;
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

bool LCUPortHasAnLCUScan( OUTMESS *&OutMessage, CtiDeviceLCU *lcu, RWTime &Now )
{
    bool bSubstitutionMade = false;
    ULONG QueueCount, ReadLength;
    OUTMESS *pOutMessage = NULL;

    try
    {
        QueryQueue( *QueueHandle(lcu->getPortID()), &QueueCount );

        if(QueueCount)
        {
            // May as well substitute in a scan if it exists...
            INT qEnt = SearchQueue( *QueueHandle(lcu->getPortID()), (void*)&Now, areAnyLCUScanEntriesOkToSend );

            if(qEnt > 0)
            {
                REQUESTDATA    ReadResult;
                BYTE           ReadPriority;

                if(ReadQueue( *QueueHandle(OutMessage->Port), &ReadResult, &ReadLength, (PPVOID)&pOutMessage, qEnt, DCWW_NOWAIT, &ReadPriority))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Error Reading Port Queue " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                else
                {
                    /* Write the scan in there first. */
                    if(PortManager.writeQueue(pOutMessage->Port, pOutMessage->EventCode, sizeof (*pOutMessage), (char *)pOutMessage, MAXPRIORITY - 2))
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Error Shuffling the Queue.  CONTROL message lost " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                            dout << RWTime() << " Substituting an LCU scan" << endl;
                        }
                        OutMessage = pOutMessage;
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

INT QueueForScan( CtiDeviceLCU *lcu, bool mayqueuescans )
{
    INT status = NORMAL;

    if(lcu->getAddress() != LCUGLOBAL && mayqueuescans)
    {
        OUTMESS *ScanOutMessage = new OUTMESS;

        if(ScanOutMessage)
        {
            lcu->lcuScanAll(ScanOutMessage); // See if we need any staging... That can go through.

            ScanOutMessage->Retry = 0;      // Make sure no retrys are done on this one

            if(ScanOutMessage != NULL)
            {
                ScanOutMessage->Priority = MAXPRIORITY - 1;  // This is the only thing that really matters now.

                if(PortManager.writeQueue(ScanOutMessage->Port, ScanOutMessage->EventCode, sizeof (*ScanOutMessage), (char *) ScanOutMessage, MAXPRIORITY - 1))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Unable to queue for fast scan." << endl;
                    }
                    delete ScanOutMessage;
                    status = QUEUE_WRITE;
                }
            }
        }
    }
#if 0
    else
    {
        QueueAllForScan(lcu, mayqueuescans);        // A bit recursive, but not really too bad.
    }
#endif

    return status;
}

INT QueueAllForScan( CtiDeviceLCU *lcu, bool mayqueuescans )
{
    CtiDeviceLCU *pOtherLCU;

    RWRecursiveLock<RWMutexLock>::LockGuard guard(DeviceManager.getMux()); // Protect our iteration!
    CtiRTDB<CtiDeviceBase>::CtiRTDBIterator itr(DeviceManager.getMap());

    /* Scan through all of them looking for one still transmitting */
    for(; ++itr ;)
    {
        CtiDeviceBase *Dev = itr.value();

        if(isLCU(Dev->getType()) && Dev->getPortID() == lcu->getPortID() && Dev->getID() != lcu->getID() && Dev->getID() != LCUGLOBAL )
        {
            pOtherLCU = (CtiDeviceLCU*)Dev;
            QueueForScan( pOtherLCU, mayqueuescans);
        }
    }

    return NORMAL;
}




void SubmitDataToDispatch( RWTPtrSlist< CtiMessage >  &vgList )
{
    extern CtiConnection VanGoghConnection;

    while( vgList.entries() > 0 )
    {
        CtiMessage *pVg = vgList.get();
        VanGoghConnection.WriteConnQue(pVg);
    }

    return;
}

void Send4PartToDispatch(RWCString Source, RWCString MajorName, RWCString MinorName, RWCString Message1, RWCString Message2)
{
    RWCString fullString;
    RWCString sourceandname;

    if( !Source.isNull() )
    {
        sourceandname = sourceandname + Source;
    }

    if(!MajorName.isNull())
    {
        sourceandname = sourceandname + RWCString(" ") + MajorName;
    }

    if(!MinorName.isNull())
    {
        sourceandname = sourceandname + RWCString(" ") + MinorName;
    }

    if(!Message1.isNull())
    {
        fullString = fullString + Message1;
    }

    if(!Message2.isNull())
    {
        fullString = fullString + RWCString(" ") + Message2;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << sourceandname << " " << fullString << endl;
    }

    CtiSignalMsg *pSig = new CtiSignalMsg(SYS_PID_DISPATCH, 0, sourceandname, fullString );

    RWTPtrSlist< CtiMessage >  lst;
    lst.insert( pSig );

    SubmitDataToDispatch( lst );

    return;
}

