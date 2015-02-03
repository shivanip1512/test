#include "precompiled.h"

#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdio.h>
#include <memory.h>
#include <string.h>

#include "connection_client.h"
#include "cparms.h"
#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "porter.h"
#include "portdecl.h"
#include "master.h"
#include "scanner.h"
#include "elogger.h"

#include "portglob.h"
#include "c_port_interface.h"
#include "dev_base.h"
#include "dev_lcu.h"
#include "mgr_device.h"
#include "mgr_port.h"

#include "logger.h"
#include "guard.h"
#include "utility.h"

using std::string;
using std::endl;
using std::list;

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

INT SendBitPatternToLogger (const CHAR *DeviceName, const BYTE *Telegraph, int len);
INT SendDOToLogger (const CHAR *DeviceName, const BYTE *Telegraph);
/*
 *  Assumes lcu is the global LCU.
 */
static void applySetActive(const long key, CtiDeviceSPtr Dev, void* vplcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vplcu;

    if( isLCU(Dev->getType()) && Dev->getPortID() == lcu->getPortID() )
    {
        MPCPointSet( SEQUENCE_ACTIVE, Dev.get(), true);
    }
}

/*
 *  Assumes lcu is the global LCU.
 */
static void applyClearActive(const long key, CtiDeviceSPtr Dev, void* vplcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vplcu;

    if( isLCU(Dev->getType()) && Dev->getPortID() == lcu->getPortID() )
    {
        MPCPointSet( SEQUENCE_ACTIVE, Dev.get(), false);
    }
}

/*
 *  Assumes lcu is the global LCU.
 */
static void applySetMissed(const long key, CtiDeviceSPtr Dev, void* vplcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vplcu;

    if( isLCU(Dev->getType()) && Dev->getPortID() == lcu->getPortID() )
    {
        MPCPointSet( MISSED, Dev.get(), true);
    }
}

/*
 *  Assumes lcu is the global LCU.
 */
static void applyClearMissed(const long key, CtiDeviceSPtr Dev, void* vplcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vplcu;

    if( isLCU(Dev->getType()) && Dev->getPortID() == lcu->getPortID() )
    {
        MPCPointSet( MISSED, Dev.get(), false);
    }
}

/*
 *  This method identifies all LCU's excluded by the transmitting lcu (lcu)
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
void applyLCUSet(const long key, CtiDeviceSPtr Dev, void* vpTXlcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vpTXlcu;     // This is the transmitting lcu...  Might be the broadcast LCU (yeah, LCUGLOBAL.. how lazy is that)

    if( isLCU(Dev->getType()) && !Dev->isInhibited() )
    {
        CtiDeviceLCU *pOtherLCU = (CtiDeviceLCU*)Dev.get();

        if(Dev->isDeviceExcluded(lcu->getID()))
        {
            if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
            {
                if( lcu->getNextCommandTime() > CtiTime::now() )  // In this location, we must use this lcu's nextcommand time.
                {
                    CTILOG_INFO(dout, lcu->getName() <<" blocking "<< pOtherLCU->getName() <<" until "<< lcu->getNextCommandTime() + 1L);
                }
                else
                {
                    CTILOG_INFO(dout, lcu->getName() <<" releasing block on "<< pOtherLCU->getName());
                }
            }

            // Mark them all out to this remotes completion time to prevent xtalk
            pOtherLCU->setExecutionProhibited( lcu->getID(), lcu->getNextCommandTime() + 1L );
            pOtherLCU->removeInfiniteProhibit( lcu->getID() );                   // Remove any infinite set up at any other point.
        }

        if( lcu->isGlobalLCU() && Dev->getPortID() == lcu->getPortID() )
        {
            // Mark them all out to this remotes completion time to prevent xtalk
            pOtherLCU->removeInfiniteProhibit( lcu->getID() );                   // Remove any infinite set up at any other point.

            // Dev is an lcu on the GLOBAL lcu's (transmitting lcu) port
            if( pOtherLCU->getControlInhibit() )
            {
                pOtherLCU->resetFlags( LCUTRANSMITSENT | LCUWASTRANSMITTING );  // This LCU is inhibited, it better not squawk
            }
            else
            {
                CTILOG_INFO(dout, pOtherLCU->getName() <<" should transmit on this port due to BROADCAST control");

                MPCPointSet( SEQUENCE_ACTIVE, pOtherLCU, true );
                pOtherLCU->setFlags( LCUTRANSMITSENT );    // Global address will make this LCU squawk

                // I dont think we should be in here at all if lcu == pOtherLCU, but we were previously
                // and who am I to change that.
                if(gConfigParms.isTrue("RIPPLE_LCU_GLOBAL_SYNCHRONIZE") && lcu != pOtherLCU)
                {
                    pOtherLCU->setLastControlMessage(lcu->getLastControlMessage());
                    pOtherLCU->setFlags(LCUWASGLOBAL); // Tell this device that this message was a global message
                    OUTMESS *lcuOutMess = pOtherLCU->getLastControlMessage();
                    lcuOutMess->Buffer.OutMessage[PREIDLEN+1] = pOtherLCU->getAddress();
                    lcuOutMess->DeviceID = pOtherLCU->getID();
                    lcuOutMess->Remote = pOtherLCU->getAddress();
                    lcuOutMess->TargetID = pOtherLCU->getID();
                    lcuOutMess->Sequence = 2; //I want this to retry twice due to some oddities about how this is working.
                    pOtherLCU->setNextCommandTime(lcu->getNextCommandTime());
                }
            }
        }
        else if( lcu->getAddress() != pOtherLCU->getAddress() && pOtherLCU->isDeviceExcluded(lcu->getID())  )
        {
            if(!(gConfigParms.isTrue("RIPPLE_LCU_GLOBAL_SYNCHRONIZE") && lcu->isGlobalLCU()))
            {
                pOtherLCU->lcuResetFlagsAndTags();                  // Make sure nothing is in there!  Insurance.
            }
        }

        /* This block of code counts the number of LCUs that were triggered to transmit because of the global LCU request */
        if(!pOtherLCU->isGlobalLCU())
        {
            if(pOtherLCU->isFlagSet(LCUTRANSMITSENT))
            {
                lcu->setNumberStarted( lcu->getNumberStarted() + 1 );

                if(gConfigParms.isTrue("RIPPLE_REPORT_LCU_TXSENT"))
                {
                    CTILOG_INFO(dout, "Command on lcu "<< lcu->getName() <<" has caused "<< lcu->getNumberStarted() <<" lcus to be targeted for control");
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
void applyLCUReset(const long key, CtiDeviceSPtr Dev, void* vpTXlcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vpTXlcu;     // This is the transmitting lcu...  Might be the broadcast LCU (yeah, LCUGLOBAL.. how lazy is that)

    if(isLCU(Dev->getType()))
    {
        CtiDeviceLCU *pOtherLCU = (CtiDeviceLCU*)Dev.get();
        pOtherLCU->removeProhibit(lcu->getID());                // lcu has no blockages on pOtherLCU after this.

        if( lcu->isGlobalLCU() && Dev->getPortID() == lcu->getPortID() )  // If we are operating as the global LCU.
        {
            pOtherLCU->lcuResetFlagsAndTags();
            MPCPointSet( SEQUENCE_ACTIVE, pOtherLCU, false );
        }
    }

    return;
}


/*
 *  This apply is valid only on lcu's port.
 *  It assumes that lcu is the global address and that a command was just sent to all LCUs.
 *  In tat case, they must all be appropriately marked.
 */
void applyStageTime(const long key, CtiDeviceSPtr Dev, void* vpTXlcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)vpTXlcu;     // This is the transmitting lcu...  lcu IS the broadcast LCU (yeah, LCUGLOBAL.. how lazy is that)

    if( isLCU(Dev->getType()) && Dev->getPortID() == lcu->getPortID() )
    {
        CtiDeviceLCU *pOtherLCU = (CtiDeviceLCU*)Dev.get();

        pOtherLCU->setStageTime( lcu->getStageTime() );                      // Everyone staged up at this time (ie the command went out)
        pOtherLCU->setNextCommandTime( lcu->getStageTime() + TIMETOSTAGE );  // I can't send until the damn thing is staged up!
        pOtherLCU->setExecutionProhibited(lcu->getID(), lcu->getStageTime() + TIMETOSTAGE);
        pOtherLCU->removeInfiniteProhibit( lcu->getID() );                   // Remove and infinite set up at any other point.
        pOtherLCU->setExecuting();
    }

    return;
}

/* Routine to check an LCU message and set flags prior to sending */
/* LCUDevice is the LCU which we are about to transmit through    */
INT LCUPreSend(OUTMESS *&OutMessage, CtiDeviceSPtr Dev)
{
    BOOL           lcuWasSending = FALSE;
    BOOL           commandTimeout = FALSE;
    ULONG          QueueCount;
    CtiDeviceLCU  *lcu = (CtiDeviceLCU*)Dev.get();

    INT            status = ClientErrors::None;


    if(OutMessage->EventCode & RIPPLE)  /* Check if this is another control message, scans get through */
    {
        OUTMESS *StageOutMessage = lcu->lcuStage(OutMessage); // See if we need any staging... That can go through.

        if(StageOutMessage != NULL)      // Only STANDARD LCU's return non-NULL on the above call.  And only when needed
        {
            if(PortManager.writeQueue(StageOutMessage))
            {
                CTILOG_ERROR(dout, "Could not write \"Staging LCU\" entry onto for DeviceID "<< Dev->getID());
            }

            /*
             * Write the control OutMessage back to the queue util the staging operation completes.
             * At which time, the getStageTime() will be set to current time, or 0 if the stage command fails...
             */
            if(PortManager.writeQueue(OutMessage))
            {
                CTILOG_ERROR(dout, "Could not replace entry onto queue");
            }

            /* Force main routine to continue */
            status = ClientErrors::Abnormal;
        }
    }


    return status;
}


BOOL Block;
BOOL OverRetry;

/* Routine to decode result of LCU handshake */
YukonError_t LCUResultDecode (OUTMESS *OutMessage, const INMESS &InMessage, CtiDeviceSPtr Dev, YukonError_t Result, bool mayqueuescans)
{
    YukonError_t status = Result;

    CtiDeviceLCU  *lcu = (CtiDeviceLCU*)Dev.get();
    CtiDeviceSPtr MyRemoteRecord;
    CtiDeviceSPtr GlobalLCUDev   = DeviceManager.RemoteGetPortRemoteEqual(lcu->getPortID(), LCUGLOBAL);
    BOOL NoneStarted = TRUE;

    if(!GlobalLCUDev && gConfigParms.isTrue("RIPPLE_LCU_GLOBAL_SYNCHRONIZE"))
    {
        GlobalLCUDev = DeviceManager.RemoteGetPortRemoteEqual(lcu->getPortID(), MASTERGLOBAL);
    }

    if(Result == ClientErrors::PortSimulated)
    {
        Sleep(500);    // Keep it from being insane!
    }

    if(OutMessage->EventCode & STAGE)
    {
        if(!(Result))
        {
            lcu->setStageTime( CtiTime() );                     // Device was successfully staged at this time!
        }
        else
        {
            lcu->setStageTime( CtiTime(0UL) );   // Device failed to stage
        }

        lcu->setNextCommandTime( CtiTime() + TIMETOSTAGE );
        lcu->setExecutionProhibited(lcu->getID(), CtiTime() + TIMETOSTAGE);
        lcu->removeInfiniteProhibit( lcu->getID() );                   // Remove any infinite set up at any other point.

        if(lcu->isGlobalLCU() && !Result)
        {
            DeviceManager.apply( applyStageTime, (void*)lcu );
        }
    }

    if(OutMessage->EventCode & RIPPLE)        // Indicates a control was sent...
    {
        /* Send the commands to the logger */
        SendBitPatternToLogger (lcu->getName().data(), OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH, OutMessage->OutLength - MASTERLENGTH);
        if( lcu->getType() == TYPE_LCU415LG ) SendDOToLogger (lcu->getName().data(), OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH);
        MPCPointSet( SEQUENCE_ACTIVE, lcu, true );

        /* Clear the block flag */
        Block = FALSE;
        OverRetry = FALSE;

        /* get how long this command will take */
        lcu->setNextCommandTime( CtiTime() + CtiDeviceLCU::lcuTime(OutMessage, lcu->getLCUType()) );     // This is legit usage of NCT!
        lcu->setExecutionProhibited(lcu->getID(), lcu->getNextCommandTime());                           // This lcu blcoks itself here.
        lcu->setExecuting(true, lcu->getNextCommandTime());                                             // This should prevent infinity.
        lcu->removeInfiniteProhibit( lcu->getID() );                                          // Remove and infinite set up at any other point.
        lcu->setFlags( LCUTRANSMITSENT );            // This LCU is TXSENT...
        lcu->setLastControlMessage( OutMessage );    // Creates a copy based upon this guy.
        lcu->setNumberStarted( 0 );                  // Reset the number started count..

        if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
        {
            CTILOG_DEBUG(dout, lcu->getName() <<" just completed sending a ripple message.");
        }

        if(lcu->isGlobalLCU() && gConfigParms.isTrue("RIPPLE_LCU_GLOBAL_SYNCHRONIZE"))
        {
            lcu->setNextCommandTime(CtiTime() + gConfigParms.getValueAsInt("RIPPLE_GLOBAL_MESSAGE_SECONDS", 120));
        }

        DeviceManager.apply(applyLCUSet, (void*)lcu ); // Mark all other affected LCUs out, or as transmitting.

        if( lcu->getNumberStarted() > 0 )
        {
            if(CtiDeviceLCU::excludeALL()) CTISleep( CtiDeviceLCU::getSlowScanDelay() );
            QueueForScan( Dev, Result ? true : mayqueuescans );     // Make porter do a fast scan!
            status = ClientErrors::RetrySubmitted;                               // Keep the decode from happening on this stupid thing.
        }
        else if( lcu->getNumberStarted() == 0 )
        {
            DeviceManager.apply(applyLCUReset, (void*)lcu );   /* Clear out the times and the command save */
            Send4PartToDispatch ("Rsc", (char*)lcu->getName().data(), "Sequence Complete", "ALL LCU Lockout");
            MPCPointSet( SEQUENCE_ACTIVE, Dev.get(), false );
        }

        if(lcu->isGlobalLCU())
        {
            CTILOG_INFO(dout, "LCUGLOBAL is transmitting on the port!");

            if(gConfigParms.isTrue("RIPPLE_LCU_GLOBAL_SYNCHRONIZE"))
            {
                //The logic here is that later on retries we dont want this hanging around
                //as that code looks to the global to do a retry on the global LCU, which we dont want
                lcu->deleteLastControlMessage();
            }
        }
    }

    if( !lcu->isGlobalLCU() )  /* Check if this decode is the result of a scan */
    {
        if( Result == ClientErrors::None || Result == ClientErrors::PortSimulated)            // Successful communication, or simulate
        {
            list< CtiMessage* >       vgList;
            CtiDeviceLCU::CtiLCUResult_t    resultCode = CtiDeviceLCU::eLCUInvalid;

            lcu->lcuFastScanDecode(OutMessage, InMessage, resultCode, (bool)GlobalLCUDev, vgList); // Note: resultCode is modified here!!!
            SubmitDataToDispatch(vgList);
            LCUProcessResultCode(Dev, GlobalLCUDev, OutMessage, resultCode );
        }
        else if(lcu->isFlagSet(LCUTRANSMITSENT))      /* Bad return message & we had a TX indication. */
        {
            if((lcu->getNextCommandTime() > CtiTime()))      // If this is set, we actually were a transmitting lcu.  Must scan for status.
            {
                if(mayqueuescans)
                {
                    CTILOG_INFO(dout, lcu->getName() <<" Queued for fast scan on port "<< OutMessage->Port);
                }
                Dev->dumpProhibits();
                QueueForScan( Dev, mayqueuescans );    // Make porter do a fast scan!
            }
            else
            {
                // We need to requeue this guy if possible.
                if( RequeueLCUCommand(Dev) == ClientErrors::RetrySubmitted )
                {
                    status = ClientErrors::RetrySubmitted;

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
            if( lcu->isExecutionProhibited() )
            {
                lcu->lcuResetFlagsAndTags();
            }
        }
    }

    return(status);
}



/* Routine to send a ripple bit pattern to the logger */
INT SendBitPatternToLogger (const CHAR *DeviceName, const BYTE *Telegraph, int len)
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
INT SendDOToLogger (const CHAR *DeviceName, const BYTE *Telegraph)
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
INT MPCPointSet( int status, CtiDeviceBase *dev, bool setter )
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

    return ClientErrors::None;
}

/*
 *  Returns true if the lcu is on ptrlcu's port and is set as LCUTRANSMITSENT.
 */
static bool findWorkingLCU(const long unusedid, const CtiDeviceSPtr &Dev, void *ptrlcu)
{
    bool found = false;

    CtiDeviceLCU *lcu = (CtiDeviceLCU *)ptrlcu;

    if(isLCU(Dev->getType())                                &&
       !Dev->isInhibited()                                  &&
       Dev->getPortID() == lcu->getPortID()                 &&
       Dev->getID()         != lcu->getID()                 &&
       !((CtiDeviceLCU*)Dev.get())->isGlobalLCU()        )
    {
        CtiDeviceLCU *pOtherLCU = (CtiDeviceLCU*)Dev.get();

        /* LCUTRANSMITSENT is set... Someone is still at it! */
        if(pOtherLCU->isFlagSet(LCUTRANSMITSENT))
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
 *  Returns true if any LCU reported BUSY.  Assumes lcu is the global LCU.
 */
static bool findWasTrxLCU(const long unusedid, const CtiDeviceSPtr &Dev, void *lprtid)
{
    bool WasTrx = false;
    LONG PortID = (LONG)lprtid;

    if(isLCU(Dev->getType()) && Dev->getPortID() == PortID )
    {
        CtiDeviceLCU   *lcu = (CtiDeviceLCU*)Dev.get();
        WasTrx = lcu->isFlagSet(LCUWASTRANSMITTING);
    }

    return WasTrx;
}

/*
 *  Do this to reset all lcus on the port and return a true if there was someone that thought he was
 *  transmitting.
 */
bool ResetLCUsForControl(CtiDeviceSPtr splcu)
{
    bool WasTrx = false;

    if(getDebugLevel() & DEBUGLEVEL_EXCLUSIONS)
    {
        CTILOG_DEBUG(dout, splcu->getName() <<" Releasing all exclusions");
    }

    CtiDeviceManager::coll_type::reader_lock_guard_t dev_guard(DeviceManager.getLock());       // Protect our iteration!

    /* Scan through all of them looking for one still transmitting */
    WasTrx = !! DeviceManager.find(findWasTrxLCU, (void*)splcu->getPortID());
    DeviceManager.apply(applyLCUReset, (void*)splcu.get());

    return WasTrx;
}

INT LCUProcessResultCode(CtiDeviceSPtr splcu, CtiDeviceSPtr GlobalLCUDev, OUTMESS *OutMessage, INT resultCode)
{
    INT  status = ClientErrors::None;
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
                        DeviceManager.apply( applyLCUReset, (void*)lcu);
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
            if( RequeueLCUCommand( splcu ) == ClientErrors::RetrySubmitted )
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

                if( RequeueLCUCommand(GlobalLCUDev) == ClientErrors::RetrySubmitted )
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
                if(GlobalLCUDev && ((CtiDeviceLCU*)(GlobalLCUDev.get()))->getFlags() & LCUTRANSMITSENT  && !gConfigParms.isTrue("RIPPLE_LCU_GLOBAL_SYNCHRONIZE"))
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
            }
            else
            {
                if(GlobalLCUDev && ((CtiDeviceLCU*)(GlobalLCUDev.get()))->getFlags() & LCUTRANSMITSENT)
                {
                    if(gConfigParms.isTrue("RIPPLE_LCU_GLOBAL_SYNCHRONIZE") && ((CtiDeviceLCU*)(GlobalLCUDev.get()))->getLastControlMessage() == NULL)
                    {
                        ReportCompletionStateToLMGroup(splcu);
                    }
                    else
                    {
                        ReportCompletionStateToLMGroup(GlobalLCUDev);
                    }
                }
                else
                {
                    ReportCompletionStateToLMGroup(splcu);
                }

                Send4PartToDispatch ("Inf", (char*)lcu->getName().data(), "Command Complete", "Normal");    // 20041220 CGP Added.
                MPCPointSet(MISSED, lcu, false);
                lcu->lcuResetFlagsAndTags();
            }

            ResetLCUsForControl(splcu);

            break;
        }
    case (CtiDeviceLCU::eLCUDeviceControlCompleteAllowTimeout):
        {
            if(lcu->getNextCommandTime() < CtiTime())
            {
                CTILOG_INFO(dout, lcu->getName() <<" getNextCommandTime() < now");
            }

            break;
        }
    case CtiDeviceLCU::eLCUNotBusyNeverTransmitted:
        {
            if(GlobalLCUDev && ((CtiDeviceLCU*)(GlobalLCUDev.get()))->getLastControlMessage() && ((CtiDeviceLCU*)(GlobalLCUDev.get()))->getLastControlMessage()->Sequence)
            {
                if( (status = RequeueLCUCommand(GlobalLCUDev)) == ClientErrors::RetrySubmitted )
                {
                    Send4PartToDispatch ("Rmc", (char *)((CtiDeviceLCU*)(GlobalLCUDev.get()))->getName().data(), "Command Resubmitted", "Missed Comm 2");
                    MPCPointSet ( MISSED, lcu, true );
                }
            }
            else if(lcu != NULL && lcu->getLastControlMessage() != NULL && lcu->getLastControlMessage()->Sequence)
            {
                if( (status = RequeueLCUCommand(splcu)) == ClientErrors::RetrySubmitted )
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
            }
            else
            {
                lcu->lcuResetFlagsAndTags();
            }

            /* Everybody is done... */
            ResetLCUsForControl( splcu );

            break;
        }
    case CtiDeviceLCU::eLCUAlternateRate:
        {
            CTILOG_ERROR(dout, "Unexpected result code CtiDeviceLCU::eLCUAlternateRate");
            break;
        }
    default:
        {
            if(lcu->getNextCommandTime() > CtiTime(0UL) && CtiTime() > lcu->getNextCommandTime() )
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
    int successflag = ClientErrors::None;

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
            successflag = ClientErrors::RetrySubmitted;

            lcu->setNextCommandTime( CtiTime(0UL) );
            lcu->setExecutionProhibited(lcu->getID(), CtiTime(0UL));
            lcu->resetFlags(LCUTRANSMITSENT | LCUWASTRANSMITTING);

            if(PortManager.writeQueue(OutMessage))
            {
                CTILOG_ERROR(dout, "Could not write to port queue for DeviceID "<< OutMessage->DeviceID <<" / Port "<< OutMessage->Port);

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
            CTILOG_WARN(dout, lcu->getName() <<" has been marked as LCUNEVERRETRY");

            CtiPointSPtr pnt = lcu->getDevicePointOffsetTypeEqual(CtiDeviceLCU::LCU_POINTOFFSET_NEVERRETRY, StatusPointType);

            if( pnt )
            {
                list< CtiMessage* >  lst;
                CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(pnt->getID(), 1);
                lst.push_back( pData );
                SubmitDataToDispatch( lst );
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
    CtiPointSPtr pnt = lcu->getDevicePointOffsetTypeEqual(CtiDeviceLCU::LCU_POINTOFFSET_NEVERRETRY, StatusPointType);

    // this means we had a good message - retries are OK
    lcu->resetFlags( LCUNEVERRETRY );

    if( pnt )
    {
        list< CtiMessage* >  lst;
        CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(pnt->getID(), 0);
        lst.push_back( pData );
        SubmitDataToDispatch( lst );
    }

    while( lcu->popControlledGroupInfo(LMGIDControl, TrxID) )
    {
        ProcessRippleGroupTrxID(LMGIDControl, TrxID);
    }

    if(lcu->getLastControlMessage() != NULL)
    {
        LMGIDControl    = lcu->getLastControlMessage()->DeviceIDofLMGroup;
        TrxID           = lcu->getLastControlMessage()->TrxID;

        ProcessRippleGroupTrxID(LMGIDControl, TrxID);
    }
    else
    {
        Send4PartToDispatch ("Xxx", (char*)lcu->getName().data(), "Transmission Id", "Was Not found");
        CTILOG_ERROR(dout, lcu->getName() <<" The transmission ID was not where it was expected");
    }

    lcu->deleteLastControlMessage();                // Do not report it again for this lcu.
    return(status);
}

INT QueueForScan( CtiDeviceSPtr splcu, bool mayqueuescans )
{
    INT status = ClientErrors::None;
    CtiDeviceLCU *lcu = (CtiDeviceLCU*)splcu.get();

    if(!lcu->isGlobalLCU() && mayqueuescans)
    {
        OUTMESS *ScanOutMessage = CTIDBG_new OUTMESS;

        if(ScanOutMessage)
        {
            lcu->lcuScanAll(ScanOutMessage); // See if we need any staging... That can go through.

            ScanOutMessage->Retry = 0;      // Make sure no retrys are done on this one

            if(ScanOutMessage != NULL)
            {
                ScanOutMessage->Priority = MAXPRIORITY - 1;  // This is the only thing that really matters now.

                if(PortManager.writeQueueWithPriority(ScanOutMessage, MAXPRIORITY - 1))
                {
                    CTILOG_ERROR(dout, "Could not write to port queue for fast scan, DeviceID "<< ScanOutMessage->DeviceID <<" / Port "<< ScanOutMessage->Port);

                    delete ScanOutMessage;
                    status = ClientErrors::QueueWrite;
                }
            }
        }
    }
    else if(lcu->isGlobalLCU())
    {
        CtiPortManager::coll_type::reader_lock_guard_t portlock(PortManager.getLock());       // this applyFunc Writes to the PortManager queues!
        QueueAllForScan(splcu, mayqueuescans);        // A bit recursive, but not really too bad.
    }

    return status;
}


/*
 * This call is used to queue all lcus on the port to scan after an LCUGLOBAL comm.
 */
static void applyQueueForScanTrue(const long unusedid, CtiDeviceSPtr Dev, void *plcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU *)plcu;

    if(isLCU(Dev->getType()) && !Dev->isInhibited() && Dev->getPortID() == lcu->getPortID() && Dev->getID() != lcu->getID() && !((CtiDeviceLCU*)Dev.get())->isGlobalLCU() )
    {
        QueueForScan(Dev, true);
    }
}

/*
 * This call is used to queue all lcus on the port to scan or stop scanning in this case after an LCUGLOBAL comm.
 */
static void applyQueueForScanFalse(const long unusedid, CtiDeviceSPtr Dev, void *plcu)
{
    CtiDeviceLCU *lcu = (CtiDeviceLCU *)plcu;

    if(isLCU(Dev->getType()) && !Dev->isInhibited() && Dev->getPortID() == lcu->getPortID() && Dev->getID() != lcu->getID() && !((CtiDeviceLCU*)Dev.get())->isGlobalLCU() )
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

    return ClientErrors::None;
}

void SubmitDataToDispatch  ( list< CtiMessage* >  &vgList )
{
    extern CtiClientConnection VanGoghConnection;

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

    CTILOG_INFO(dout, sourceandname <<" "<< fullString);

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

    CTILOG_INFO(dout, sourceandname <<" "<< fullString);

    CtiSignalMsg *pSig = CTIDBG_new CtiSignalMsg(SYS_PID_DISPATCH, 0, sourceandname, fullString );

    list< CtiMessage* >  lst;
    lst.push_back( pSig );

    SubmitDataToDispatch( lst );

    return ClientErrors::None;
}

void ProcessRippleGroupTrxID( LONG LMGIDControl, UINT TrxID)
{
    list< CtiMessage* >  vgList;

    // Find the controlling group.
    CtiDeviceSPtr pGroupDev = DeviceManager.getDeviceByID( LMGIDControl );

    if(pGroupDev)
    {
        pGroupDev->processTrxID( TrxID, vgList );
    }

    SubmitDataToDispatch( vgList );
}

