/*-----------------------------------------------------------------------------*
*
* File:   RIPPLE
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/RIPPLE.cpp-arc  $
* REVISION     :  $Revision: 1.28 $
* DATE         :  $Date: 2006/03/03 18:35:31 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


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

void ProcessRippleGroupTrxID( LONG LMGIDControl, UINT TrxID);
void Send4PartToDispatch(string Source, string MajorName, string MinorName, string Message1 = string(""), string Message2 = string(""));
INT SendTextToDispatch(PCHAR Source, PCHAR Message = NULL, string majorName = string(""), string minorName = string(""));
INT QueueForScan( CtiDeviceSPtr splcu, bool mayqueuescans );
INT QueueAllForScan( CtiDeviceSPtr splcu, bool mayqueuescans );

void SubmitDataToDispatch( list< CtiMessage* >  &vgList );
INT ReportCompletionStateToLMGroup(CtiDeviceSPtr splcu);     // f.k.a. ReturnTrxID();
INT RequeueLCUCommand( CtiDeviceSPtr splcu );
bool ResetLCUsForControl(CtiDeviceSPtr splcu);
BOOL LCUsAreDoneTransmitting(CtiDeviceSPtr splcu);
INT LCUProcessResultCode(CtiDeviceSPtr splcu, CtiDeviceSPtr GlobalLCUDev, OUTMESS *OutMessage, INT resultCode);
BOOL areAnyLCUControlEntriesOkToSend(void *pId, void* d);
BOOL areAnyLCUScanEntriesOkToSend(void *pId, void* d);
INT ReleaseAnLCU(OUTMESS *&OutMessage, CtiDeviceSPtr splcu);

bool AnyLCUCanExecute( OUTMESS *&OutMessage, CtiDeviceSPtr splcu, CtiTime &Now );
bool LCUCanExecute( OUTMESS *&OutMessage, CtiDeviceSPtr splcu, CtiTime &Now );
bool LCUPortHasAnLCUScan( OUTMESS *&OutMessage, CtiDeviceSPtr splcu, CtiTime &Now );

bool DevPortIDMatch(LONG id1, LONG id2)
{
    return gConfigParms.isTrue("RIPPLE_PORT_EXCLUSIONS_ONLY") ? (id1 == id2) : true;
}

/*
 *  This find funciton is used to determine if there are any LCUs which are transmitting, or need to do so.
 *
 *  It is currently limited to looking at the current port.
 */
bool findAnyLCUsTransmitting(const long key, CtiDeviceSPtr Dev, void* ptr)
{
    bool bStatus = false;
    CtiDeviceLCU *lcu = (CtiDeviceLCU *)ptr;

    if( isLCU(Dev->getType()) && DevPortIDMatch(Dev->getPortID(), lcu->getPortID()) )
    {
        CtiDeviceLCU *pOtherLCU = (CtiDeviceLCU*)Dev.get();
        if(pOtherLCU->isExecuting() || pOtherLCU->getLastControlMessage() != NULL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            bStatus = true;
        }
    }
    return bStatus;
}

/*
 *  This find function is used to determine if a given LCU passed in by ptr is blocked by any other LCU.
 *
 *  20060228 CGP: This function seems woefully inadequate.
 */
bool findExclusionBlockage(const long key, CtiDeviceSPtr Dev, void* ptr)
{
    bool bStatus = false;
    CtiDeviceLCU *lcu = (CtiDeviceLCU *)ptr;

    if( isLCU(Dev->getType()) )
    {
        CtiTime Now;
        CtiDeviceLCU *pOtherLCU = (CtiDeviceLCU*)Dev.get();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** ACH Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if( CtiDeviceLCU::excludeALL() )        // No LCU may execute simultaneous with another
        {
            if( pOtherLCU->getLastControlMessage() != NULL && pOtherLCU->getNextCommandTime() < Now )
            {
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

        if(DevPortIDMatch(Dev->getPortID(), lcu->getPortID()))
        {
            // Do we need to ACH ACH ACH?
        }
    }

    return bStatus;
}

static void applySetActive(const long key, CtiDeviceSPtr Dev, void* vplcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vplcu;

    if( isLCU(Dev->getType()) && DevPortIDMatch(Dev->getPortID(), lcu->getPortID()) )
    {
        MPCPointSet( SEQUENCE_ACTIVE, Dev.get(), true);
    }
}

static void applyClearActive(const long key, CtiDeviceSPtr Dev, void* vplcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vplcu;

    if( isLCU(Dev->getType()) && DevPortIDMatch(Dev->getPortID(), lcu->getPortID()) )
    {
        MPCPointSet( SEQUENCE_ACTIVE, Dev.get(), false);
    }
}

static void applySetMissed(const long key, CtiDeviceSPtr Dev, void* vplcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vplcu;

    if( isLCU(Dev->getType()) && DevPortIDMatch(Dev->getPortID(), lcu->getPortID()) )
    {
        MPCPointSet( MISSED, Dev.get(), true);
    }
}

static void applyClearMissed(const long key, CtiDeviceSPtr Dev, void* vplcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vplcu;

    if( isLCU(Dev->getType()) && DevPortIDMatch(Dev->getPortID(), lcu->getPortID()) )
    {
        MPCPointSet( MISSED, Dev.get(), false);
    }
}

void ApplyPortXResetOnTimeout(const long key, CtiDeviceSPtr Dev, void* vpTXlcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vpTXlcu;     // This is the transmitting lcu...  Might be the broadcast LCU (yeah, LCUGLOBAL.. how lazy is that)
    CtiTime         Now;
    CtiDeviceLCU   *pOtherLCU;

    if( isLCU(Dev->getType()) && DevPortIDMatch(Dev->getPortID(), lcu->getPortID()) )
    {
        CtiDeviceLCU *pOtherLCU = (CtiDeviceLCU*)Dev.get();

        if(pOtherLCU->getNextCommandTime() > rwEpoch && Now > pOtherLCU->getNextCommandTime() )
        {
            if(pOtherLCU->getLastControlMessage() != NULL)
            {
                Send4PartToDispatch ("Rsc", (char*)Dev->getName().data(), "Sequence Complete", "Timeout");

                MPCPointSet( SEQUENCE_ACTIVE, Dev.get(), false );
                MPCPointSet( MISSED, Dev.get(), true );
            }

            pOtherLCU->lcuResetFlagsAndTags();
            pOtherLCU->setExecuting(false);
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
void ApplyPortXLCUSet(const long key, CtiDeviceSPtr Dev, void* vpTXlcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vpTXlcu;     // This is the transmitting lcu...  Might be the broadcast LCU (yeah, LCUGLOBAL.. how lazy is that)

    if( isLCU(Dev->getType()) && !Dev->isInhibited() &&
        DevPortIDMatch(Dev->getPortID(), lcu->getPortID()) ) // && Dev->getAddress() != LCUGLOBAL )            // CGP 12202004 Added on a whim.  Be very alert here...
    {
        CtiDeviceLCU *pOtherLCU = (CtiDeviceLCU*)Dev.get();

        pOtherLCU->setNextCommandTime( lcu->getNextCommandTime() + 1L );  // Mark them all out to this remotes completion time to prevent xtalk

        if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " LCU " << lcu->getName() << " blocking " << pOtherLCU->getName() << " until " << pOtherLCU->getNextCommandTime() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if( lcu->getAddress() == LCUGLOBAL )
        {
            // 012805 CGP.  For Minnkota.  // if( pOtherLCU->isLCULockedOut() || pOtherLCU->getControlInhibit() )
            if( pOtherLCU->getControlInhibit() )
            {
                pOtherLCU->resetFlags( LCUTRANSMITSENT | LCUWASTRANSMITTING );  // This LCU is inhibited, it better not squawk
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << pOtherLCU->getName() << " should transmit on this port due to BROADCAST control" << endl;
                }
                MPCPointSet( SEQUENCE_ACTIVE, pOtherLCU, true );
                pOtherLCU->setFlags( LCUTRANSMITSENT );    // Global address will make this LCU squawk
            }
        }
        else if( lcu->getAddress() != pOtherLCU->getAddress() ) // It isn't me...
        {
            pOtherLCU->lcuResetFlagsAndTags();                  // Make sure nothing is in there!
        }

        if(pOtherLCU->getAddress() != LCUGLOBAL)
        {
            if(pOtherLCU->isFlagSet(LCUTRANSMITSENT))
            {
                lcu->setNumberStarted( lcu->getNumberStarted() + 1 );

                if(gConfigParms.isTrue("RIPPLE_REPORT_LCU_TXSENT"))
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Command on lcu " << lcu->getName() << " has caused " << lcu->getNumberStarted() << " lcus to be targeted for control" << endl;
                }
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
void ApplyPortXLCUReset(const long key, CtiDeviceSPtr Dev, void* vpTXlcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vpTXlcu;     // This is the transmitting lcu...  Might be the broadcast LCU (yeah, LCUGLOBAL.. how lazy is that)

    if( isLCU(Dev->getType()) ) // 200603032006 CGP // && DevPortIDMatch(Dev->getPortID(), lcu->getPortID()) )
    {
        CtiDeviceLCU *pOtherLCU = (CtiDeviceLCU*)Dev.get();
        pOtherLCU->lcuResetFlagsAndTags();
        pOtherLCU->removeInfiniteProhibit(lcu->getID());
    }

    return;
}


/*
 *  This apply is valid only on lcu's port.
 *  It assumes that lcu is the global address and that a command was just sent to all LCUs.
 *  In tat case, they must all be appropriately marked.
 */
void ApplyStageTime(const long key, CtiDeviceSPtr Dev, void* vpTXlcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vpTXlcu;     // This is the transmitting lcu...  Might be the broadcast LCU (yeah, LCUGLOBAL.. how lazy is that)

    if( isLCU(Dev->getType()) && Dev->getPortID() == lcu->getPortID() )
    {
        CtiDeviceLCU *pOtherLCU = (CtiDeviceLCU*)Dev.get();

        pOtherLCU->setStageTime( lcu->getStageTime() );                      // Everyone staged up at this time (ie the command went out)
        pOtherLCU->setNextCommandTime( lcu->getStageTime() + TIMETOSTAGE );  // I can't send until the damn thing is staged up!
        pOtherLCU->setExecutionProhibited(lcu->getID(), lcu->getStageTime() + TIMETOSTAGE);
        pOtherLCU->setExecuting();
    }

    return;
}

/* Routine to check an LCU message and set flags prior to sending */
/* LCUDevice is the LCU which we are about to transmit through    */
LCUPreSend(OUTMESS *&OutMessage, CtiDeviceSPtr Dev)
{
    BOOL           lcuWasSending = FALSE;
    BOOL           commandTimeout = FALSE;
    ULONG          QueueCount;
    CtiDeviceLCU  *lcu = (CtiDeviceLCU*)Dev.get();

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
            if( (status = ReleaseAnLCU( OutMessage, Dev )) == RETRY_SUBMITTED )
            {
                // Queues have been shuffled
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Queue Shuffled **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }


    return status;
}


BOOL Block;
BOOL OverRetry;

/* Routine to decode result of LCU handshake */
LCUResultDecode (OUTMESS *OutMessage, INMESS *InMessage, CtiDeviceSPtr Dev, ULONG Result, bool mayqueuescans)
{
    INT status = Result;

    CtiDeviceLCU  *lcu = (CtiDeviceLCU*)Dev.get();
    CtiDeviceSPtr MyRemoteRecord;
    CtiDeviceSPtr GlobalLCUDev   = DeviceManager.RemoteGetPortRemoteEqual(lcu->getPortID(), LCUGLOBAL);
    BOOL NoneStarted = TRUE;

    if(Result == ErrPortSimulated)
    {
        Sleep(1000);    // Keep it from bing insane!
    }

    if(OutMessage->EventCode & STAGE)
    {
        if(!(Result))
        {
            lcu->setStageTime( CtiTime() );                     // Device was successfully staged at this time!
        }
        else
        {
            lcu->setStageTime( rwEpoch );   // Device failed to stage
        }

        lcu->setNextCommandTime( CtiTime() + TIMETOSTAGE );
        lcu->setExecutionProhibited(lcu->getID(), CtiTime() + TIMETOSTAGE);

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
        MPCPointSet( SEQUENCE_ACTIVE, lcu, true );

        /* Clear the block flag */
        Block = FALSE;
        OverRetry = FALSE;

        /* get how long this command will take */
        lcu->setNextCommandTime( CtiTime() + CtiDeviceLCU::lcuTime(OutMessage, lcu->getLCUType()) );
        lcu->setExecutionProhibited(lcu->getID(), lcu->getNextCommandTime());
        lcu->setFlags( LCUTRANSMITSENT );            // This LCU is TXSENT...
        lcu->setLastControlMessage( OutMessage );    // Creates a copy based upon this guy.
        lcu->setNumberStarted( 0 );                  // Reset the number started count..

        if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** LCUResultDecode: Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << lcu->getName() << endl;
        }

        DeviceManager.apply(ApplyPortXLCUSet, (void*)lcu ); // Mark all other affected LCUs out or as transmitting.

        if( lcu->getNumberStarted() > 0 )
        {
            if(CtiDeviceLCU::excludeALL())
            {
                CTISleep( CtiDeviceLCU::getSlowScanDelay() );
            }

            QueueForScan( Dev, Result ? true : mayqueuescans );     // Make porter do a fast scan!
            status = RETRY_SUBMITTED;                               // Keep the decode from happening on this stupid thing.
        }
        else if( lcu->getNumberStarted() == 0 )
        {
            DeviceManager.apply(ApplyPortXLCUReset, (void*)lcu );   /* Clear out the times and the command save */
            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "ALL LCU Lockout");
            MPCPointSet( SEQUENCE_ACTIVE, Dev.get(), false );
        }

        if(lcu->getAddress() == LCUGLOBAL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " LCUGLOBAL is transmitting on the port! " << endl;
            }
            // 20050307 CGP Removed. // ReportCompletionStateToLMGroup(lcu);
        }
    }

    if( lcu->getAddress() != LCUGLOBAL )  /* Check if this decode is the result of a scan */
    {
        if( Result == NORMAL)            // Successful communication, or simulate
        {
            list< CtiMessage* >       vgList;
            CtiDeviceLCU::CtiLCUResult_t    resultCode = CtiDeviceLCU::eLCUInvalid;

            lcu->lcuFastScanDecode(OutMessage, InMessage, resultCode, (bool)GlobalLCUDev, vgList); // Note: resultCode is modified here!!!
            SubmitDataToDispatch(vgList);
            LCUProcessResultCode(Dev, GlobalLCUDev, OutMessage, resultCode );
        }
        else if(lcu->isFlagSet(LCUTRANSMITSENT))      /* Bad return message & we had a TX indication. */
        {
            if((lcu->getNextCommandTime() > CtiTime()) )
            {
                if(mayqueuescans)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << lcu->getName() << " Queued for fast scan on port " << OutMessage->Port << endl;
                }
                QueueForScan( Dev, mayqueuescans );    // Make porter do a fast scan!
            }
            else
            {
                // We need to requeue this guy if possible.
                if( RequeueLCUCommand(Dev) == RETRY_SUBMITTED )
                {
                    status = RETRY_SUBMITTED;

                    Send4PartToDispatch ("Rmc", (char *)lcu->getName().data(), "Command Resubmited", "Missed Comm");
                    MPCPointSet ( MISSED, lcu, true );
                }
                else
                {
                    // The command is OLD.  Lets do the cleanup on this device!
                    Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Timeout");
                    MPCPointSet( SEQUENCE_ACTIVE, lcu, false );
                    MPCPointSet ( MISSED, lcu, true );
                    lcu->lcuResetFlagsAndTags();
                }

                /* Check if we made it... */
                if( LCUsAreDoneTransmitting( Dev ) )
                {
                    if(GlobalLCUDev && ((CtiDeviceLCU*)(GlobalLCUDev.get()))->getLastControlMessage() != NULL)
                    {
                        MPCPointSet ( MISSED, (CtiDeviceBase*)GlobalLCUDev.get(), true );
                    }

                    Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Missed Comm 1");
                    /* Everybody is done... */
                    MPCPointSet ( MISSED, lcu, true );
                    ResetLCUsForControl(Dev);
                }
            }
        }
        else
        {
            if( lcu->getNextCommandTime() > CtiTime() )
            {
                lcu->lcuResetFlagsAndTags();
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

    /* Do it */
    return(SendTextToDispatch (Source, Message, DeviceName));
}




/* Routine to send double orders to Logger */
SendDOToLogger (const CHAR *DeviceName, const BYTE *Telegraph)
{
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

    case 0320:
        strcat (Message, "2.03    ");
        break;

    case 0602:
        strcat (Message, "2.04    ");
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
    return(SendTextToDispatch (Source, Message, DeviceName));
}


/* Routine to set $_MPC related status points */
//MPCPointSet( int status, CtiDeviceBase *dev )
MPCPointSet( int status, CtiDeviceBase *dev, bool setter )
{
    CtiPointDataMsg *pData = NULL;

    if(isLCU(dev->getType()))
    {
        CtiDeviceLCU *lcu = ( CtiDeviceLCU *)dev;
        list< CtiMessage* >  vgList;

        if( setter )
            pData = lcu->getPointSet( status );
        else
            pData = lcu->getPointClear( status );

        if( pData )
        {
            vgList.push_back( pData );
            SubmitDataToDispatch( vgList );
        }
    }

    return(NORMAL);
}

/*
 *  Returns true if the lcu is on ptrlcu's port and is set as LCUTRANSMITSENT.
 */
static bool findWorkingLCU(const long unusedid, CtiDeviceSPtr Dev, void *ptrlcu)
{
    bool found = false;

    CtiDeviceLCU *lcu = (CtiDeviceLCU *)ptrlcu;

    if(isLCU(Dev->getType())                    &&
       !Dev->isInhibited()                      &&
       DevPortIDMatch(Dev->getPortID(), lcu->getPortID()) &&
       Dev->getID()         != lcu->getID()     &&
       Dev->getAddress()    != LCUGLOBAL           )
    {
        CtiDeviceLCU *pOtherLCU = (CtiDeviceLCU*)Dev.get();

        /* LCUTRANSMITSENT is set... Someone is still at it! */
        if(pOtherLCU->isFlagSet(LCUTRANSMITSENT))                                   // && pOtherLCU->getNextCommandTime() > CtiTime() )
        {
            found = true;
        }
    }

    return found;
}

/*
 *  Returns TRUE if all LCU's on the same port are NOT marked as LCUTRANSMITSENT.  Realy just flips the sense of a findWorkingLCU
 */
BOOL LCUsAreDoneTransmitting(CtiDeviceSPtr splcu)
{
    BOOL LCUsFinished = TRUE;

    if(DeviceManager.find( findWorkingLCU,(void*)splcu.get()) )
    {
        LCUsFinished = FALSE;
    }

    return LCUsFinished;
}

/*
 *  Returns true if any LCU reported BUSY.
 */
static bool findWasTrxLCU(const long unusedid, CtiDeviceSPtr Dev, void *lprtid)
{
    bool WasTrx = false;
    LONG PortID = (LONG)lprtid;

    if(isLCU(Dev->getType()) && DevPortIDMatch(Dev->getPortID(), PortID) )
    {
        CtiDeviceLCU   *lcu = (CtiDeviceLCU*)Dev.get();
        WasTrx = lcu->isFlagSet(LCUWASTRANSMITTING);
    }

    return WasTrx;
}

static void applyResetLCUsOnPort(const long unusedid, CtiDeviceSPtr Dev, void *lprtid)
{
    LONG PortID = (LONG)lprtid;

    if(isLCU(Dev->getType()) && DevPortIDMatch(Dev->getPortID(), PortID) )
    {
        CtiDeviceLCU   *lcu = (CtiDeviceLCU*)Dev.get();
        lcu->lcuResetFlagsAndTags();
        MPCPointSet( SEQUENCE_ACTIVE, lcu, false );
    }
}

/*
 *  Do this to reset all lcus on the port and return a true if there was someone that thought he was
 *  transmitting.
 */
bool ResetLCUsForControl(CtiDeviceSPtr splcu)
{
    bool WasTrx = false;

    CtiDeviceManager::LockGuard  dev_guard(DeviceManager.getMux());       // Protect our iteration!

    /* Scan through all of them looking for one still transmitting */
    WasTrx = DeviceManager.find(findWasTrxLCU, (void*)splcu->getPortID());
    DeviceManager.apply(applyResetLCUsOnPort, (void*)splcu->getPortID());
    DeviceManager.removeInfiniteExclusion(splcu);                          // make certain splcu no longer blocks anyone on ANY port.

    return WasTrx;
}




INT LCUProcessResultCode(CtiDeviceSPtr splcu, CtiDeviceSPtr GlobalLCUDev, OUTMESS *OutMessage, INT resultCode)
{
    INT  status = NORMAL;
    bool wasTransmitting;

    CtiDeviceLCU *lcu = (CtiDeviceLCU *)splcu.get();

    switch(resultCode)
    {
    case CtiDeviceLCU::eLCUFastScan:
        {
            QueueForScan(splcu, true);
            break;
        }
    case CtiDeviceLCU::eLCUSlowScan:
        {
            CTISleep( CtiDeviceLCU::getSlowScanDelay() );
            QueueForScan(splcu, true);
            break;
        }
    case CtiDeviceLCU::eLCULockedOutSpecificControl:
        {
            /* This means we did not complete so log it */
            Send4PartToDispatch ("Rab", (char*)lcu->getName().data(), "Command Aborted", "LCU Lockout");
            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "LCU Lockout");
            ResetLCUsForControl(splcu);
            break;
        }
    case CtiDeviceLCU::eLCULockedOut:
        {
            // I sent as the GlobalLCU, and got this response from an individual LCU.
            if(GlobalLCUDev && ((CtiDeviceLCU*)(GlobalLCUDev.get()))->getLastControlMessage() != NULL)
            {
                if( LCUsAreDoneTransmitting( splcu ) )
                {
                    // Do this iff LCUTRANSMITSENT _NOT_ set for any LCU on this port
                    wasTransmitting = ResetLCUsForControl(splcu);    // OK, is someone taking an awefully long time here!

                    if( !(wasTransmitting) )      // No one was transmitting
                    {
                        /*
                         * iff no-one was transmitting on this port, mark them
                         * all for the ability to send a command.
                         */
                        DeviceManager.apply( ApplyPortXLCUReset, (void*)lcu);
                    }

                    Send4PartToDispatch ("Rsc", (char*)GlobalLCUDev->getName().data(), "Sequence Complete", "LCU Lockout");
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
            MPCPointSet ( MISSED, lcu, true );
            if( RequeueLCUCommand( splcu ) == RETRY_SUBMITTED )
            {
                /* This means we did not complete the message so log it */
                Send4PartToDispatch ("Rre", (char*)lcu->getName().data(), "Command Resubmitted", "Injector Error");
            }
            else
            {
                /* This means that we have exceeded the number of times we can send a message */
                lcu->lcuResetFlagsAndTags();
                Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Injector Error");
                MPCPointSet (SEQUENCE_ACTIVE, lcu, false );
            }
            break;
        }
    case CtiDeviceLCU::eLCURequeueGlobalControl:
        {
            if(GlobalLCUDev && ((CtiDeviceLCU*)(GlobalLCUDev.get()))->getLastControlMessage()->Sequence)
            {
                DeviceManager.apply(applySetMissed, (void*)GlobalLCUDev.get()); // Does MPCPointSet ( MISSED, GlobalLCUDev, true ) on all LCUs

                if( RequeueLCUCommand(GlobalLCUDev) == RETRY_SUBMITTED )
                {
                    Send4PartToDispatch ("Rlc", (char*)GlobalLCUDev->getName().data(), "Command Resubmitted", "Injector Error");
                }
                else
                {
                    /* This means that we have exceeded the number of times we can send a message */
                    ((CtiDeviceLCU*)(GlobalLCUDev.get()))->lcuResetFlagsAndTags();
                    Send4PartToDispatch ("Rsc", (char*)GlobalLCUDev->getName().data(), "Sequence Complete", "Injector Error");
                    DeviceManager.apply(applyClearActive, (void*)GlobalLCUDev.get());  // Does MPCPointSet (SEQUENCE_ACTIVE, GlobalLCUDev, false) on all LCUs;
                }
            }
            else
            {
                /* We have exceeded number of resubmissions */
                OverRetry = TRUE;
                lcu->lcuResetFlagsAndTags();
            }

            /* Check if we made it... */
            if( LCUsAreDoneTransmitting( splcu ) )
            {
                /* Everybody is done... */
                if(GlobalLCUDev && ((CtiDeviceLCU*)(GlobalLCUDev.get()))->getFlags() & LCUTRANSMITSENT)
                {
                    Send4PartToDispatch ("Rsc", (char*)((CtiDeviceLCU*)(GlobalLCUDev.get()))->getName().data(), "Sequence Complete", "Injector Error");
                }
                else
                {
                    Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(),  "Sequence Complete","Injector Error");
                    MPCPointSet ( MISSED, lcu, true );
                }

                ResetLCUsForControl(splcu);
            }
            break;
        }
    case CtiDeviceLCU::eLCUDeviceControlComplete:
        {
            /* Check if we made it... */
            if( LCUsAreDoneTransmitting( splcu ) )
            {
                if(GlobalLCUDev && ((CtiDeviceLCU*)(GlobalLCUDev.get()))->getFlags() & LCUTRANSMITSENT)
                {
                    if(((CtiDeviceLCU*)(GlobalLCUDev.get()))->getLastControlMessage() != NULL)
                    {
                        if(Block)
                        {
                            Send4PartToDispatch ("Rsc", (char*)((CtiDeviceLCU*)(GlobalLCUDev.get()))->getName().data(), "Sequence Complete", "LCU Lockout");
                        }
                        else if(OverRetry)
                        {
                            Send4PartToDispatch ("Rsc", (char*)((CtiDeviceLCU*)(GlobalLCUDev.get()))->getName().data(), "Sequence Complete", "Retry Error");
                            MPCPointSet (SEQUENCE_ACTIVE, lcu, false);
                        }
                        else
                        {
                            ReportCompletionStateToLMGroup(GlobalLCUDev);
                            Send4PartToDispatch ("Rsc", (char*)((CtiDeviceLCU*)(GlobalLCUDev.get()))->getName().data(), "Sequence Complete", "Normal");

                            // Adjust the status points for both the global and the LCU that got us here.
                            MPCPointSet (SEQUENCE_ACTIVE, lcu, false);
                            MPCPointSet( MISSED, lcu, false );

                            MPCPointSet (SEQUENCE_ACTIVE, GlobalLCUDev.get(), false);
                            MPCPointSet( MISSED, GlobalLCUDev.get(), false );
                        }
                    }
                    else
                    {
                        Send4PartToDispatch ("Rrs", (char*)((CtiDeviceLCU*)(GlobalLCUDev.get()))->getName().data(), "Sequence Continues", "Resubmitted");
                        MPCPointSet ( MISSED, lcu, true );
                    }
                }
                else
                {
                    if(lcu->getLastControlMessage() != NULL)
                    {
                        if(Block)
                        {
                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "LCU Lockout");
                        }
                        else if(OverRetry)
                        {
                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Retry Error");
                            MPCPointSet (SEQUENCE_ACTIVE, lcu, false);
                        }
                        else
                        {
                            /*
                             *  This is a correct completion state (hooray)
                             */
                            ReportCompletionStateToLMGroup(splcu);
                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Normal");

                            // CGP
                            MPCPointSet (SEQUENCE_ACTIVE, lcu, false);
                            MPCPointSet( MISSED, lcu, false );
                        }
                    }
                    else
                    {
                        Send4PartToDispatch ("Rrs", (char*)lcu->getName().data(), "Sequence Continues", "Resubmitted");
                        MPCPointSet ( MISSED, lcu, true );
                    }
                }

                ResetLCUsForControl(splcu);
            }
            else
            {
                if(GlobalLCUDev && ((CtiDeviceLCU*)(GlobalLCUDev.get()))->getFlags() & LCUTRANSMITSENT)
                {
                    ReportCompletionStateToLMGroup(GlobalLCUDev);
                }
                else
                {
                    ReportCompletionStateToLMGroup(splcu);
                }

                Send4PartToDispatch ("Inf", (char*)lcu->getName().data(), "Command Complete", "Normal");    // 20041220 CGP Added.
                lcu->lcuResetFlagsAndTags();
                MPCPointSet(MISSED, lcu, false);
            }


            if(PorterDebugLevel & PORTER_DEBUG_RIPPLE)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Pausing " << 2.5 << " seconds. based on PORTER_DEBUGLEVEL & " << PORTER_DEBUG_RIPPLE << endl;
                }

                Sleep(2500);
            }

            break;
        }
    case (CtiDeviceLCU::eLCUDeviceControlCompleteAllowTimeout):
        {
            if(lcu->getNextCommandTime() < CtiTime())
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << lcu->getName() << " getNextCommandTime() < now " << endl;
                }
            }

            break;
        }
    case CtiDeviceLCU::eLCUNotBusyNeverTransmitted:
        {
            if(GlobalLCUDev && ((CtiDeviceLCU*)(GlobalLCUDev.get()))->getLastControlMessage() && ((CtiDeviceLCU*)(GlobalLCUDev.get()))->getLastControlMessage()->Sequence)
            {
                if( (status = RequeueLCUCommand(GlobalLCUDev)) == RETRY_SUBMITTED )
                {
                    Send4PartToDispatch ("Rmc", (char *)((CtiDeviceLCU*)(GlobalLCUDev.get()))->getName().data(), "Command Resubmitted", "Missed Comm 2");
                    MPCPointSet ( MISSED, lcu, true );
                }
            }
            else if(lcu != NULL && lcu->getLastControlMessage() != NULL && lcu->getLastControlMessage()->Sequence)
            {
                if( (status = RequeueLCUCommand(splcu)) == RETRY_SUBMITTED )
                {
                    Send4PartToDispatch ("Rmc", (char *)lcu->getName().data(), "Command Resubmitted", "Missed Comm 3");
                    MPCPointSet ( MISSED, lcu, true );
                }
            }
            else
            {
                OverRetry = TRUE;
            }

            /* Check if we made it... */
            if( LCUsAreDoneTransmitting( splcu ) )
            {
                if(GlobalLCUDev && ((CtiDeviceLCU*)(GlobalLCUDev.get()))->getFlags() & LCUTRANSMITSENT)
                {
                    if(((CtiDeviceLCU*)(GlobalLCUDev.get()))->getLastControlMessage() != NULL)
                    {
                        if(Block)
                        {
                            Send4PartToDispatch ("Rsc", (char*)((CtiDeviceLCU*)(GlobalLCUDev.get()))->getName().data(), "Sequence Complete",  "Missed Comm 4");
                        }
                        else if(OverRetry)
                        {
                            Send4PartToDispatch ("Rsc", (char*)((CtiDeviceLCU*)(GlobalLCUDev.get()))->getName().data(), "Sequence Complete", "Missed Comm 5");
                            MPCPointSet (SEQUENCE_ACTIVE, lcu, false);
                        }
                        else
                        {
                            ReportCompletionStateToLMGroup(GlobalLCUDev);
                            Send4PartToDispatch ("Rsc", (char*)((CtiDeviceLCU*)(GlobalLCUDev.get()))->getName().data(), "Sequence Complete", "Normal");

                            // CGP
                            MPCPointSet (SEQUENCE_ACTIVE, GlobalLCUDev.get(), false);
                            MPCPointSet( MISSED, GlobalLCUDev.get(), false );
                        }
                    }
                    else
                    {
                        Send4PartToDispatch ("Rrs", (char*)((CtiDeviceLCU*)(GlobalLCUDev.get()))->getName().data(), "Sequence Continues", "Resubmitted");
                        MPCPointSet (SEQUENCE_ACTIVE, lcu, false);
                    }
                }
                else if(lcu != NULL && lcu->getFlags() & LCUTRANSMITSENT)
                {
                    if(lcu->getLastControlMessage() != NULL)
                    {
                        if(Block)
                        {
                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete",  "Missed Comm 6");
                        }
                        else if(OverRetry)
                        {
                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Missed Comm 7");
                            MPCPointSet (SEQUENCE_ACTIVE, lcu, false);
                        }
                        else
                        {
                            ReportCompletionStateToLMGroup(splcu);
                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Normal");

                            MPCPointSet (SEQUENCE_ACTIVE, lcu, false);
                            MPCPointSet( MISSED, lcu, false );
                        }

                    }
                    else
                    {
                        Send4PartToDispatch ("Rrs", (char*)lcu->getName().data(), "Sequence Continues", "Resubmitted");
                        MPCPointSet (SEQUENCE_ACTIVE, lcu, false);
                    }
                }
                else
                {
                    if(lcu->getLastControlMessage() != NULL)
                    {
                        MPCPointSet (SEQUENCE_ACTIVE, lcu, false);
                        MPCPointSet (MISSED, lcu, true);

                        if(Block)
                        {
                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(),  "Sequence Complete", "Missed Comm (block)");
                        }
                        else if(OverRetry)
                        {
                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Missed Comm (overretry)");
                        }
                        else
                        {
                            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Missed Comm 8");
                        }
                    }
                }

                /* Everybody is done... */
                ResetLCUsForControl( splcu );
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
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    default:
        {
            if(lcu->getNextCommandTime() > rwEpoch && CtiTime() > lcu->getNextCommandTime() )
            {
                if(lcu->getLastControlMessage() != NULL)
                {
                    Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "Timeout");
                    MPCPointSet( MISSED, lcu, true );
                }

                lcu->lcuResetFlagsAndTags();
            }

            break;
        }
    }


    return status;
}

/* try to requeue the command */
INT RequeueLCUCommand( CtiDeviceSPtr splcu )
{
    int successflag = NORMAL;

    CtiDeviceLCU *lcu = (CtiDeviceLCU*)splcu.get();

    OUTMESS *OutMessage = lcu->releaseLastControlMessage(); // Don't let it get blown away.;

    if(OutMessage != NULL)
    {
        /*
         *  check if Retries for this LCU is still turned on AND we are not tried out on this message.
         *  lcus use the Sequence variable to control the number of retries.  This is a bit odd, and is left over from the original architect.
         */

        if( !(lcu->isFlagSet(LCUNEVERRETRY)) && OutMessage->Sequence > 0)
        {
            // we need to retry this message - put is back on queue
            OutMessage->Sequence--;
            successflag = RETRY_SUBMITTED;

            lcu->setNextCommandTime( rwEpoch );
            lcu->setExecutionProhibited(lcu->getID(), CtiTime(rwEpoch));
            lcu->resetFlags(LCUTRANSMITSENT | LCUWASTRANSMITTING);

            if(PortManager.writeQueue (OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Error Writing Retry into Queue " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                successflag = FALSE;

                // Reconnect the OutMessage, it failed to get queued..
                OutMessage->Sequence++;
                lcu->setLastControlMessage( OutMessage );    // Makes a CTIDBG_new copy, you must delete the detached one.

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
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << lcu->getName() << " has been marked as LCUNEVERRETRY" << endl;
            }

            // 20051011 CGP ACH.  When this occurs, we should set the pseudo status for LCU Transmit to state 1
            // The expected state names are 0/1 = Fired/Failed.

            if(OutMessage != NULL)
            {
                delete OutMessage;
                OutMessage = NULL;
            }
        }
    }

    return(successflag);
}

INT ReportCompletionStateToLMGroup(CtiDeviceSPtr splcu)     // f.k.a. ReturnTrxID();
{
    INT      status = 0;

    LONG     LMGIDControl = 0;  // LM Group controlled.  Could be a macro group!
    LONG     Rte = 0;
    UINT     TrxID = 0;

    CtiDeviceLCU *lcu = (CtiDeviceLCU*)splcu.get();

    // this means we had a good message - retries are OK
    lcu->resetFlags( LCUNEVERRETRY );

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    while( lcu->popControlledGroupInfo(LMGIDControl, TrxID) )
    {
        ProcessRippleGroupTrxID(LMGIDControl, TrxID);
    }

    if(lcu->getLastControlMessage() != NULL)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        LMGIDControl    = lcu->getLastControlMessage()->DeviceIDofLMGroup;
        TrxID           = lcu->getLastControlMessage()->TrxID;

        ProcessRippleGroupTrxID(LMGIDControl, TrxID);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
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
INT ReleaseAnLCU(OUTMESS *&OutMessage, CtiDeviceSPtr splcu)
{
    INT         status = NORMAL;
    CtiTime      Now;                          // Pre-init to current time.
    int         loopCnt = 0;

    do
    {
        Now = Now.now();

        if(LCUCanExecute( OutMessage, splcu, Now ))               // I am the LCU, and my OutMessage can execute.
        {
            if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << splcu->getName() << " CAN EXECUTE!!! **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            status = NORMAL;                                    // I can continue without a REQUEUE
            break;
        }
        else if( AnyLCUCanExecute( OutMessage, splcu, Now ) ||    // Some other non-excluded LCU has a control ready on this port, and has been substituted
                 LCUPortHasAnLCUScan( OutMessage, splcu, Now) )   // Some scan or non-lcu device type has been substituted
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << splcu->getName() << " LCU CANNOT EXECUTE... REQUEUED!!! **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            status = RETRY_SUBMITTED;                           // We need to re-ReadQueue as it has been shuffled.
        }
        else                                                    // We have nothing better to do, we may as well hang out here.
        {
            if( !(loopCnt++ % 30) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << splcu->getName() << " control is blocked, no scan is waiting. " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

    CtiTime   &Now = *((CtiTime*)pRWtime);

    bool     blockedByExclusion = false;

    if(OutMessage->EventCode & RIPPLE)     // Indicates a command message!
    {
        CtiDeviceSPtr  Dev = DeviceManager.getEqual( OutMessage->DeviceID );

        if(Dev)
        {
            if(isLCU(Dev->getType()))
            {
                CtiDeviceLCU *lcu = (CtiDeviceLCU*)Dev.get();      // This IS an LCU

                if( Now > lcu->getNextCommandTime() && !lcu->isExecutionProhibited())
                {
                    CtiLockGuard<CtiMutex> guard( lcu->getLCUExclusionMux() );             // get mux for all LCU's
                    blockedByExclusion = DeviceManager.contains( findExclusionBlockage, (void*)lcu);

                    if( !blockedByExclusion )
                    {
                        bStatus = TRUE;
                        CtiDeviceLCU::assignToken( lcu->getID() );

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << lcu->getName() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
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

    CtiTime   &Now = *((CtiTime*)pRWtime);

    CtiDeviceSPtr  Dev = DeviceManager.getEqual( OutMessage->DeviceID );

    if(Dev)
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


bool LCUCanExecute(OUTMESS *&OutMessage, CtiDeviceSPtr splcu, CtiTime &Now )
{
    bool bStatus = false;
    bool blockedByExclusion = false;
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)splcu.get();

    if( !splcu->isExecutionProhibited() && Now > lcu->getNextCommandTime() )
    {
        {
            CtiLockGuard<CtiMutex> guard( lcu->getLCUExclusionMux() );             // get mux for all LCU's

            try
            {
                CtiTablePaoExclusion exclusion;
                blockedByExclusion = !DeviceManager.mayDeviceExecuteExclusionFree(splcu, exclusion);
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            if(!blockedByExclusion)
            {
                splcu->setExecuting();

                CtiDeviceLCU::assignToken( splcu->getID() );

                if( lcu->getType() == TYPE_LCUT3026 && gConfigParms.getValueAsString("RIPPLE_ENFORCE_LCU_DUTY_CYCLE") == string("TRUE") )
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

bool AnyLCUCanExecute( OUTMESS *&OutMessage, CtiDeviceSPtr splcu, CtiTime &Now )
{
    bool bSubstitutionMade = false;
    ULONG QueueCount, ReadLength;
    OUTMESS *pOutMessage = NULL;

    try
    {
        QueryQueue( *QueueHandle(splcu->getPortID()), &QueueCount );

        if(QueueCount)      // There are queue entries.
        {
            // We cannot be executed, we should look for another CONTROL queue entry.. We are still protected by mux...
            INT qEnt = SearchQueue( *QueueHandle(splcu->getPortID()), (void*)&Now, areAnyLCUControlEntriesOkToSend );

            if(qEnt > 0)
            {
                REQUESTDATA    ReadResult;
                BYTE           ReadPriority;

                if(ReadQueue( *QueueHandle(OutMessage->Port), &ReadResult, &ReadLength, (PPVOID)&pOutMessage, qEnt, DCWW_NOWAIT, &ReadPriority))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Error Reading Port Queue " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                else
                {
                    if(PortManager.writeQueue(pOutMessage->Port, pOutMessage->EventCode, sizeof (*pOutMessage), (char *)pOutMessage, MAXPRIORITY - 2))
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Error Shuffling the Queue.  Message lost " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        delete pOutMessage;
                        pOutMessage = NULL;
                    }

                    /* The OUTMESS that got us here is always examined next again! */
                    if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *)OutMessage, MAXPRIORITY - 2))
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Error Shuffling the Queue.  CONTROL message lost " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        delete OutMessage;
                        OutMessage = NULL;
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Substituting a different (non-blocked) LCU" << endl;
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
        dout << CtiTime() << " **** EX Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return bSubstitutionMade;
}

bool LCUPortHasAnLCUScan( OUTMESS *&OutMessage, CtiDeviceSPtr splcu, CtiTime &Now )
{
    bool bSubstitutionMade = false;
    ULONG QueueCount, ReadLength;
    OUTMESS *pOutMessage = NULL;

    try
    {
        QueryQueue( *QueueHandle(splcu->getPortID()), &QueueCount );

        if(QueueCount)
        {
            // May as well substitute in a scan if it exists...
            INT qEnt = SearchQueue( *QueueHandle(splcu->getPortID()), (void*)&Now, areAnyLCUScanEntriesOkToSend );

            if(qEnt > 0)
            {
                REQUESTDATA    ReadResult;
                BYTE           ReadPriority;

                if(ReadQueue( *QueueHandle(OutMessage->Port), &ReadResult, &ReadLength, (PPVOID)&pOutMessage, qEnt, DCWW_NOWAIT, &ReadPriority))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Error Reading Port Queue " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                else
                {
                    /* Write the scan in there first. */
                    if(PortManager.writeQueue(pOutMessage->Port, pOutMessage->EventCode, sizeof (*pOutMessage), (char *)pOutMessage, MAXPRIORITY - 2))
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Error Shuffling the Queue.  CONTROL message lost " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        delete pOutMessage;
                        pOutMessage = NULL;
                    }

                    /* The OUTMESS that got us here is always examined next again! */
                    if(PortManager.writeQueue(OutMessage->Port, OutMessage->EventCode, sizeof (*OutMessage), (char *)OutMessage, MAXPRIORITY - 2))
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Error Shuffling the Queue.  CONTROL message lost " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        delete OutMessage;
                        OutMessage = NULL;
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Substituting an LCU scan" << endl;
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
        dout << CtiTime() << " **** EX Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return bSubstitutionMade;
}

INT QueueForScan( CtiDeviceSPtr splcu, bool mayqueuescans )
{
    INT status = NORMAL;

    if(splcu->getAddress() != LCUGLOBAL && mayqueuescans)
    {
        CtiDeviceLCU *lcu = (CtiDeviceLCU*)splcu.get();
        OUTMESS *ScanOutMessage = CTIDBG_new OUTMESS;

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
                        dout << CtiTime() << " Unable to queue for fast scan." << endl;
                    }
                    delete ScanOutMessage;
                    status = QUEUE_WRITE;
                }
            }
        }
    }
    else if(splcu->getAddress() == LCUGLOBAL)
    {
        QueueAllForScan(splcu, mayqueuescans);        // A bit recursive, but not really too bad.
    }

    return status;
}


static void applyQueueForScanTrue(const long unusedid, CtiDeviceSPtr Dev, void *plcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU *)plcu;

    if(isLCU(Dev->getType()) &&
       !Dev->isInhibited() &&
       DevPortIDMatch(Dev->getPortID(), lcu->getPortID()) &&
       Dev->getID() != lcu->getID() &&
       Dev->getAddress() != LCUGLOBAL )
    {
        QueueForScan(Dev, true);
    }
}

static void applyQueueForScanFalse(const long unusedid, CtiDeviceSPtr Dev, void *plcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU *)plcu;

    if(isLCU(Dev->getType()) &&
       !Dev->isInhibited() &&
       DevPortIDMatch(Dev->getPortID(), lcu->getPortID()) &&
       Dev->getID() != lcu->getID() &&
       Dev->getAddress() != LCUGLOBAL )
    {
        QueueForScan(Dev, false);
    }
}

INT QueueAllForScan( CtiDeviceSPtr splcu, bool mayqueuescans )
{
    if(mayqueuescans)
    {
        DeviceManager.apply(applyQueueForScanTrue, (void*)splcu.get());
    }
    else
    {
        DeviceManager.apply(applyQueueForScanFalse, (void*)splcu.get());
    }

    return NORMAL;
}

void SubmitDataToDispatch  ( list< CtiMessage* >  &vgList )
{
    extern CtiConnection VanGoghConnection;

    while( vgList.size() > 0 )
    {
        CtiMessage *pVg = vgList.front();vgList.pop_front();
        VanGoghConnection.WriteConnQue(pVg);
    }

    return;
}

void Send4PartToDispatch(string Source, string MajorName, string MinorName, string Message1, string Message2)
{
    string fullString;
    string sourceandname;

    if( !Source.empty() )
    {
        sourceandname = sourceandname + Source;
    }

    if(!MajorName.empty())
    {
        sourceandname = sourceandname + string(" ") + MajorName;
    }

    if(!MinorName.empty())
    {
        sourceandname = sourceandname + string(" ") + MinorName;
    }

    if(!Message1.empty())
    {
        fullString = fullString + Message1;
    }

    if(!Message2.empty())
    {
        fullString = fullString + string(" ") + Message2;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << sourceandname << " " << fullString << endl;
    }

    CtiSignalMsg *pSig = CTIDBG_new CtiSignalMsg(SYS_PID_DISPATCH, 0, sourceandname, fullString );

    list< CtiMessage* >  lst;
    lst.push_back( pSig );

    SubmitDataToDispatch( lst );

    return;
}


INT SendTextToDispatch(PCHAR Source, PCHAR Message, string majorName, string minorName)
{
    string fullString;
    string sourceandname;

    if( Source )
    {
        sourceandname = sourceandname + string(Source);
    }

    if(!majorName.empty())
    {
        sourceandname = sourceandname + string(" ") + majorName;
    }

    if(!minorName.empty())
    {
        sourceandname = sourceandname + string(" ") + minorName;
    }

    if(Message)
    {
        fullString = string(Message);
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << sourceandname << " " << fullString << endl;
    }

    CtiSignalMsg *pSig = CTIDBG_new CtiSignalMsg(SYS_PID_DISPATCH, 0, sourceandname, fullString );

    list< CtiMessage* >  lst;
    lst.push_back( pSig );

    SubmitDataToDispatch( lst );

    return NORMAL;
}

void ProcessRippleGroupTrxID( LONG LMGIDControl, UINT TrxID)
{
    list< CtiMessage* >  vgList;

    // Find the controlling group.
    CtiDeviceSPtr pGroupDev = DeviceManager.getEqual( LMGIDControl );

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if(pGroupDev)
    {
        pGroupDev->processTrxID( TrxID, vgList );
    }

    SubmitDataToDispatch( vgList );
}

