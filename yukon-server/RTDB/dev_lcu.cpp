/*-----------------------------------------------------------------------------*
*
* File:   dev_lcu
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_lcu.cpp-arc  $
* REVISION     :  $Revision: 1.23 $
* DATE         :  $Date: 2005/02/10 23:24:00 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include <windows.h>
#include <iostream>
#include <iomanip>
using namespace std;

#include <assert.h>

#include "cparms.h"
#include "connection.h"
#include "cmdparse.h"
#include "device.h"
#include "dev_lcu.h"
#include "dsm2.h"
#include "master.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_multi.h"
#include "numstr.h"
#include "porter.h"
#include "pt_base.h"
#include "pt_accum.h"
#include "pt_numeric.h"
#include "utility.h"

#include "elogger.h"       // for Send4PartToLogger()
#include "mpc.h"

#include "numstr.h"

#define DEBUG_PRINT_DECODE 0
#define DUTYCYCLESIZE 15


//extern CtiConnection VanGoghConnection;

static char StatusPointNames50[][40] = {
    {"Local Operation   "},
    {"Alarm State       "},
    {"Busy              "},
    {"Standalone        "},
    {"Reset             "},
    {"NA                "},
    {"Transmitter Alarm "},
    {"Power reset       "},
    {"Reserved Point    "},
    {"Reserved Point    "},
    {"Reserved Point    "},
    {"Reserved Point    "},
    {"Reserved Point    "},
    {"Reserved Point    "},
    {"Reserved Point    "},
    {"Reserved Point    "}
};

BOOL          CtiDeviceLCU::_lcuGlobalsInit         = FALSE;
bool          CtiDeviceLCU::_excludeAllLCUs         = false;
bool          CtiDeviceLCU::_lcuObserveBusyBit      = true;
ULONG         CtiDeviceLCU::_landgMinimumTime       = 50L;
ULONG         CtiDeviceLCU::_landgTimeout           = 100L;
ULONG         CtiDeviceLCU::_landgRetries           = 2L;
ULONG         CtiDeviceLCU::_erepcRetries           = 1L;
ULONG         CtiDeviceLCU::_lcuWithToken           = 0L;
ULONG         CtiDeviceLCU::_lcuStartStopCrossings  = 44L;
ULONG         CtiDeviceLCU::_lcuBitCrossings        = 16L;
ULONG         CtiDeviceLCU::_lcuDutyCyclePercent    = 4L;
ULONG         CtiDeviceLCU::_lcuSlowScanDelay       = 2500L;
RWMutexLock   CtiDeviceLCU::_staticMux;
RWMutexLock   CtiDeviceLCU::_lcuExclusionMux;


CtiDeviceLCU::CtiDeviceLCU(INT type) :
_stageTime( rwEpoch ),
_lcuStaged(FALSE),
_lcuFlags(0),
_numberStarted(0),
_nextCommandTime( rwEpoch ),
_honktime(DUTYCYCLESIZE, make_pair(RWTime().seconds() - RWTime().seconds() % DUTYCYCLESIZE, 0.0)),
_lockedOut(false),
_lastControlMessage(0)
{
    switch(type)
    {
    case TYPE_LCU415LG:
        {
            _lcuType = LCU_LANDG;
            break;
        }
    case TYPE_LCU415ER:
        {
            _lcuType = LCU_EASTRIVER;
            break;
        }
    case TYPE_LCUT3026:
        {
            _lcuType = LCU_T3026;
            break;
        }
    case TYPE_LCU415:
    default:
        {
            _lcuType = LCU_STANDARD;
            break;
        }
    }
}

CtiDeviceLCU::CtiDeviceLCU(const CtiDeviceLCU& aRef) :
_honktime(DUTYCYCLESIZE, make_pair(RWTime().seconds() - RWTime().seconds() % DUTYCYCLESIZE, 0.0))
{
    *this = aRef;
}

CtiDeviceLCU::~CtiDeviceLCU()
{
    if(_lastControlMessage)
    {
        delete _lastControlMessage;
        _lastControlMessage = 0;
    }
}


INT CtiDeviceLCU::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;

    if(OutMessage != NULL)
    {
        if(_lcuType == LCU_T3026)
        {
            setScanPending();
            EstablishOutMessagePriority( OutMessage, ScanPriority );

            OUTMESS *pNewOutMessage = CTIDBG_new OUTMESS(*OutMessage);

            status = lcuScanInternalStatus(OutMessage);

            // Put the built up OUTMESS into the Slist
            outList.insert(OutMessage);
            OutMessage = NULL;

            status = lcuScanExternalStatus(pNewOutMessage);
            outList.insert(pNewOutMessage);
        }
        else
        {
            setScanPending();
            EstablishOutMessagePriority( OutMessage, ScanPriority );
            status = lcuScanAll(OutMessage);

            // Put the single built up OUTMESS into the Slist
            outList.insert(OutMessage);
            OutMessage = NULL;
        }
    }
    else
    {
        status = MEMORY;
    }

    return status;
}

INT CtiDeviceLCU::AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;

    if(OutMessage != NULL)
    {
        setScanFreezePending();
        EstablishOutMessagePriority( OutMessage, ScanPriority );
        status = lcuFreeze(OutMessage);

        // Put the single built up OUTMESS into the Slist
        outList.insert(OutMessage);
        OutMessage = NULL;
    }
    else
    {
        status = MEMORY;
    }

    return status;
}

INT CtiDeviceLCU::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    return( GeneralScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority) );
}

INT CtiDeviceLCU::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    return lcuDecode(InMessage, TimeNow, vgList, retList, outList);
}

/* Routine to output freeze to an LCU */
INT CtiDeviceLCU::lcuFreeze(OUTMESS *&OutMessage)
{
    INT status = NORMAL;

    /* Load the freeze message */
    if((status = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)getAddress(), MASTERFREEZE, 0)) != NORMAL)
        return(status);

    /* Load all the other stuff that is needed */
    OutMessage->DeviceID        = getID();
    OutMessage->Port            = getPortID();
    OutMessage->Remote          = getAddress();

    OutMessage->TimeOut = 2;
    OutMessage->OutLength = 4;
    OutMessage->InLength = -1;
    OutMessage->EventCode = RESULT | ENCODED;
    OutMessage->Sequence = 0;
    OutMessage->Retry = 2;

    return(status);
}

/* Routine to reset freeze to an LCU */
INT CtiDeviceLCU::lcuReset(OUTMESS *&OutMessage)
{
    INT status = NORMAL;

    setScanResetting();

    /* Load the reset freeze message */
    if((status = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)getAddress(), MASTERRESET, 0)) != NORMAL)
        return(status);

    /* Load all the other stuff that is needed */
    OutMessage->DeviceID        = getID();
    OutMessage->Port            = getPortID();
    OutMessage->Remote          = getAddress();

    OutMessage->TimeOut = 2;
    OutMessage->OutLength = 4;
    OutMessage->InLength = -1;
    OutMessage->EventCode = RESULT | ENCODED;
    OutMessage->Sequence = 0;
    OutMessage->Retry = 2;

    return(status);
}

/* Routine to scan ALL LCU status */
INT CtiDeviceLCU::lcuScanAll(OUTMESS *&OutMessage)            /* Priority to place command on queue */
{
    INT status = NORMAL;

    if(_lcuType == LCU_T3026)
    {
        status = lcuScanInternalStatus(OutMessage);
    }
    else
    {
        /* Load the forced scan message */
        if((status = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)getAddress(), MASTERSCANALL, 0)) != NORMAL)
            return(status);

        /* Load all the other stuff that is needed */
        OutMessage->DeviceID        = getID();
        OutMessage->Port            = getPortID();
        OutMessage->Remote          = getAddress();
        OutMessage->TimeOut         = 2;
        OutMessage->OutLength       = 4;
        OutMessage->InLength        = -1;
        OutMessage->EventCode       = RESULT | ENCODED;
        OutMessage->Sequence        = 0;
        OutMessage->Retry           = 2;
    }

    return(status);
}

/* Routine to scan internal LCU status */
INT CtiDeviceLCU::lcuScanInternalStatus(OUTMESS *&OutMessage)
{
    INT status = NORMAL;

    /* Load the forced scan message */
    if((status = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)getAddress(), MASTERSCANINT, 0)) != NORMAL)
        return(status);

    /* Load all the other stuff that is needed */
    OutMessage->DeviceID        = getID();
    OutMessage->Port            = getPortID();
    OutMessage->Remote          = getAddress();
    OutMessage->TimeOut         = 2;
    OutMessage->OutLength       = 4;
    OutMessage->InLength        = -1;
    OutMessage->EventCode       = RESULT | ENCODED;
    OutMessage->Sequence        = 0;
    OutMessage->Retry           = 2;

    return(status);
}

/* Routine to scan internal LCU status */
INT CtiDeviceLCU::lcuScanExternalStatus(OUTMESS *&OutMessage)
{
    INT status = NORMAL;

    /* Load the forced scan message */
    if((status = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)getAddress(), MASTERSCANEXT, 0)) != NORMAL)
        return(status);

    /* Load all the other stuff that is needed */
    OutMessage->DeviceID        = getID();
    OutMessage->Port            = getPortID();
    OutMessage->Remote          = getAddress();
    OutMessage->TimeOut         = 2;
    OutMessage->OutLength       = 4;
    OutMessage->InLength        = -1;
    OutMessage->EventCode       = RESULT | ENCODED;
    OutMessage->Sequence        = 0;
    OutMessage->Retry           = 2;

    return(status);
}

INT CtiDeviceLCU::lcuDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = InMessage->EventCode & 0x7fff;

    if( status )
    {
        resetForScan(ScanRateGeneral);
    }
    else if( InMessage->Buffer.InMessage[0] == 0x01 )
    {
        /* decode whatever message this is */
        switch(InMessage->Buffer.InMessage[2])
        {
        case MASTERFREEZE:
            {
                if(isScanFreezePending())
                {
                    resetScanFreezePending();
                    setScanFrozen();

                    /* update the accumulator criteria for this RTU */
                    // Done in the reset... which zeros it out.  // setPrevFreezeTime(getLastFreezeTime());
                    setLastFreezeTime( RWTime(InMessage->Time) );
                    resetScanFreezeFailed();

                    setPrevFreezeNumber(getLastFreezeNumber());
                    setLastFreezeNumber(getLastFreezeNumber() + 1);

                    /* then force a scan */
                    OUTMESS *OutMessage = CTIDBG_new OUTMESS;

                    if(OutMessage != NULL)
                    {
                        InEchoToOut(InMessage, OutMessage);
                        CtiCommandParser parse(InMessage->Return.CommandStr);

                        if((status = GeneralScan (NULL, parse, OutMessage, vgList, retList, outList, MAXPRIORITY - 4)) != NORMAL)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

                            delete OutMessage;
                        }
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Throwing away unexpected freeze response" << endl;
                    }
                    setScanFreezeFailed();
                    /* message for screwed up freeze */
                }

                break;
            }
        case MASTERRESET:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << getName() << " accumulator reset"  << endl;
                }
                setPrevFreezeTime(RWTime(InMessage->Time));
                setLastFreezeTime(RWTime(InMessage->Time));
                resetScanResetting();
                /* LCU is unaware of these */
                break;
            }
        case MASTERSCANEXT:
            {
                CtiMessage *Msg = lcuDecodeDigitalInputs(InMessage);

                if(Msg != NULL)
                {
                    retList.insert( Msg );
                }

                Msg = lcuDecodeAnalogs(InMessage);

                if(Msg != NULL)
                {
                    retList.insert( Msg );
                }

                resetScanPending();

                break;
            }
        case MASTERSCANINT:
            {
                CtiMessage *Msg = lcuDecodeStatus(InMessage);

                if(Msg != NULL)
                {
                    retList.insert( Msg );
                }

                resetScanPending();

                break;
            }
        case MASTERSCANALL:
            {
                if(useScanFlags())
                {
                    if(isScanPending())
                    {

                        CtiMessage *Msg = lcuDecodeStatus(InMessage);

                        if(Msg != NULL)
                        {
                            retList.insert( Msg );
                        }

                        Msg = lcuDecodeDigitalInputs(InMessage);

                        if(Msg != NULL)
                        {
                            retList.insert( Msg );
                        }

                        Msg = lcuDecodeAnalogs(InMessage);

                        if(Msg != NULL)
                        {
                            retList.insert( Msg );
                        }

                        Msg = lcuDecodeAccumulators(InMessage, outList);

                        if(Msg != NULL)
                        {
                            retList.insert( Msg );
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " LCU response unexpected.. " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                }
                else
                {
                    CtiMessage *Msg = lcuDecodeStatus(InMessage);

                    if(Msg != NULL)
                    {
                        // Msg->dump();
                        retList.insert(Msg);
                    }
                }

                resetScanPending();

                break;
            }
        case MASTERLOOPBACK:
            {
                CtiReturnMsg   *pLoop = CTIDBG_new CtiReturnMsg(getID(), RWCString(InMessage->Return.CommandStr), RWCString(getName() + " / successful ping"), InMessage->EventCode & 0x7fff, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt, InMessage->Return.TrxID, InMessage->Return.UserID);

                if(pLoop != NULL)
                {
                    retList.insert(pLoop);
                }

                break;
            }
        case MASTERSEND:
            {
                CtiMessage *Msg = lcuDecodeStatus(InMessage);

                if(Msg != NULL)
                {
                    // Msg->dump();
                    retList.insert(Msg);
                }
                break;
            }
        case MASTERLOCKOUTSET:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << getName() << " LOCK OUT SET" << endl;
                }
                _lockedOut = true;
                break;
            }
        case MASTERLOCKOUTRESET:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << getName() << " LOCK OUT CLEARED" << endl;
                }
                _lockedOut = false;
                break;
            }
        default:
            {
                /* This should never happen so reset the scan */
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << getName() << " Unknown Mastercom response " << __FILE__ << " (" << __LINE__ << ") " << hex << setw(2) << (int)(InMessage->Buffer.InMessage[2]) << endl;
                }
                resetScanFlags();
                setScanStarting();

                break;
            }
        }   /* End of switch */
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Message is not a proper MASTERCOM reply" << endl;
        }

        status = FRAMEERR;
    }

    return status;
}


INT CtiDeviceLCU::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList,RWTPtrSlist< OUTMESS > &outList)
{
    INT nRet = NORMAL;
    OUTMESS *pOM = 0;

    switch(parse.getCommand())
    {
    case ControlRequest:
        {
            int ptOffset;
            if( 0 != (ptOffset = parse.getiValue("point", 0)) )
            {
                CtiPoint *pPt = getDevicePointEqual(ptOffset);

                if(pPt && pPt->isStatus() && pPt->getControlOffset() == 9)
                {
                    if(!lcuLockout(OutMessage, parse.getFlags() & CMD_FLAG_CTL_OPEN ? false : true))
                    {
                        outList.insert( OutMessage );
                        OutMessage = NULL;
                    }
                }
            }
            else
            {
                if((pOM = lcuControl(OutMessage)) == 0)
                {
                    vgList.insert(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT, pReq->getSOE(), getDescription(parse), RWCString("Control Request for LCU failed"), LoadMgmtLogType, SignalEvent, pReq->getUser()));
                    retList.insert( CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), RWCString("Control Request for LCU failed"), nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE,  RWOrdered()) );
                }
                else
                {
                    outList.insert( pOM );
                    pOM = 0;
                }
            }

            break;
        }
    case GetStatusRequest:
        {
            // Get a scan done maybe?
            if((nRet = lcuScanAll(OutMessage)) != 0)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " error scanning " << getName()<< endl;
                }

                vgList.insert(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT, pReq->getSOE(), getDescription(parse), RWCString("Scan All Request for LCU failed"), GeneralLogType, SignalEvent, pReq->getUser()));
                retList.insert( CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), RWCString("Scan All Request for LCU failed"), nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered()));
            }
            else
            {
                outList.insert( OutMessage );
                OutMessage = NULL;
            }

            break;
        }
    case LoopbackRequest:
        {
            // Get a scan done maybe?
            if((nRet = lcuLoop(OutMessage)) != 0)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " error looping " << getName()<< endl;
                }
                retList.insert( CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr),  RWCString(getName() + " / unsuccessful ping to LCU"), nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered()));
            }
            else
            {
                outList.insert( OutMessage );
                OutMessage = NULL;
            }


            break;
        }
    case GetValueRequest:
    case PutValueRequest:
    case PutStatusRequest:
    case GetConfigRequest:
    case PutConfigRequest:
    default:
        {
            nRet = NoExecuteRequestMethod;
            /* Set the error value in the base class. */
            // FIX FIX FIX 092999
            retList.insert( CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), RWCString("LCU Devices do not support this command (yet?)"), nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID,  OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered()));
            break;
        }
    }

    return nRet;
}

/* Routine to send ripple message to an LCU */
OUTMESS* CtiDeviceLCU::lcuControl(OUTMESS *&OutMessage)
{
    ULONG i;
    USHORT Count;

    /* Load up all the goodies */
    OverrideOutMessagePriority( OutMessage, MAXPRIORITY - 3 );
    OutMessage->TimeOut      = 3;
    OutMessage->InLength     = -1;
    OutMessage->EventCode    |= ENCODED | RESULT | RIPPLE;          // May contain RESULT based upon the incoming OutMessage
    OutMessage->ReturnNexus  = NULL;
    OutMessage->SaveNexus    = NULL;

    if(!OutMessage->TargetID) OutMessage->TargetID = getID();
    if(!OutMessage->DeviceID) OutMessage->DeviceID = getID();


    if(OutMessage->Remote == RTUGLOBAL)
    {
        OutMessage->Retry = 0;

        if(_lcuType == LCU_EASTRIVER)
        {
            /* First do not allow messages to be sent to Broadcast Address - just punt */
            // East River LCU does not support this
            delete (OutMessage);
            OutMessage = NULL;
        }
    }
    else
    {
        OutMessage->Retry = 2;
    }

    if(OutMessage != NULL)
    {
        /* Load up the max times we will resubmit this message */
        if(_lcuType == LCU_LANDG)
        {
            OutMessage->Sequence = CtiDeviceLCU::_landgRetries;
        }
        else if(_lcuType == LCU_EASTRIVER)
        {
            OutMessage->Sequence = CtiDeviceLCU::_erepcRetries;
        }
        else
        {
            OutMessage->Sequence = 2;
        }

        /* see how many bytes we need to send */
        if(_lcuType == LCU_LANDG)
        {
            Count = 6;
        }
        else
        {
            for(Count = 6; Count > 0; Count--)
            {
                if(OutMessage->Buffer.RSt.Message[Count])     // Count backwards from 6 looking for the first byte with bits set.  IE if the 6th byte has bits set, the count will be 6.
                    break;
            }
        }

        /* Make sure we have some data */
        if(!Count && !(OutMessage->Buffer.RSt.Message[0]))
        {
            delete (OutMessage);
            OutMessage = NULL;
        }
        else
        {
            /* Calculate the length */
            OutMessage->OutLength = MASTERLENGTH + Count + 1;

            /* Build MasterComm header */
            if((_lcuType == LCU_LANDG) && OutMessage->Remote != RTUGLOBAL)
            {
                if((i = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)OutMessage->Remote, MASTERSENDREPLY, (USHORT)(Count + 1))) != NORMAL)
                {
                    delete (OutMessage);
                    OutMessage = NULL;
                }
            }
            else
            {
                if((i = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)OutMessage->Remote, MASTERSEND, (USHORT)(Count + 1))) != NORMAL)
                {
                    delete (OutMessage);
                    OutMessage = NULL;
                }
            }

            if(OutMessage != NULL)
            {
                /* Copy message into buffer */
                memcpy (OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH, OutMessage->Buffer.RSt.Message, Count + 1);
            }
        }
    }

    return OutMessage;
}


/* Routine to execute a loop message */
INT CtiDeviceLCU::lcuLoop(OUTMESS *&OutMessage)
{
    if(_lcuType == LCU_T3026)
    {
        lcuScanInternalStatus(OutMessage);
    }
    else
    {
        /* Build a mastercom loopback request */
        MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)getAddress(), MASTERLOOPBACK, 0);

        OutMessage->TimeOut                 = 2;
        OutMessage->OutLength               = MASTERLENGTH;
        OutMessage->InLength                = -1;
        OutMessage->EventCode               = ENCODED | RESULT | NOWAIT;

        /* Load up the pieces of the structure */
        OutMessage->DeviceID                = getID();
        OutMessage->Port                    = getPortID();
        OutMessage->Remote                  = getAddress();
        OutMessage->Retry                   = 2; //VSt->Retry;
        OutMessage->Sequence                = 0;
        OutMessage->ReturnNexus             = NULL;
        OutMessage->SaveNexus               = NULL;
    }

    return(NORMAL);
}


CtiReturnMsg* CtiDeviceLCU::lcuDecodeDigitalInputs(INMESS *InMessage)
{
    ULONG      i;
    RWCString  resultString;

    /* Define the various records */
    CtiPoint  *PointRecord;

    /* Variables for decoding LCU Messages */
    FLOAT      PValue;

    CtiPointDataMsg *pData = 0;
    CtiReturnMsg    *pPIL  = 0;

    if(_lcuType == LCU_T3026)
    {
        if(InMessage->Buffer.InMessage[3] >= 4)
        {
            pPIL = CTIDBG_new CtiReturnMsg(getID(), RWCString(InMessage->Return.CommandStr), RWCString("LCU status request complete"), InMessage->EventCode & 0x7fff, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt, InMessage->Return.TrxID, InMessage->Return.UserID);

            /*
             *  Due to the T3026's unique nature of connector order determines data order, and a lack of any way to tell
             *  what data is what.  We decode bytes based upon what analog offset is defined.
             */

            int offset = 4;

            if((getDevicePointOffsetTypeEqual(1, AnalogPointType)) != NULL)
            {
                // Analog offset one is defined.. We will assume DI's come from the second pair (6,7) of bytes
                offset = 6;
            }

            USHORT lcuDigital = MAKEUSHORT (InMessage->Buffer.InMessage[offset], InMessage->Buffer.InMessage[offset+1]);

            for(i = 1; i <= 16; i++)
            {
                if((PointRecord = getDevicePointOffsetTypeEqual(i, StatusPointType)) != NULL)
                {
                    PValue = ((lcuDigital >> (i - 1)) & 0x0001) ? CLOSED : OPENED;

                    resultString = getName() + " / " + PointRecord->getName() + " is ";
                    if(PValue == CLOSED)
                        resultString += "ACTIVE";
                    else
                        resultString += "INACTIVE";

                    pData = CTIDBG_new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, StatusPointType, resultString);
                    if(pData != NULL)
                    {
                        if(pPIL != NULL)
                        {
                            pPIL->PointData().insert(pData);
                            pData = NULL;  // We just put it on the list...
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "ERROR: " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            delete pData;
                            pData = NULL;
                        }
                    }
                }
            }


            if(pPIL->PointData().entries() == 0)
            {
                pPIL->setResultString("Communication Successful.  LCU has no DB defined points.");
            }

        }
        else if(InMessage->Buffer.InMessage[3] == 12)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  That is likely not a T3026 LCU!" << endl;
            }
        }
        else if(InMessage->Buffer.InMessage[3] != 2)
        {
            // We have a very poor assumption here...
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  LCU T3026 does not meet External data assumptions!!!" << endl;
            }
        }
    }
    else
    {
        pPIL = CTIDBG_new CtiReturnMsg(getID(), RWCString(InMessage->Return.CommandStr), RWCString("LCU status request complete"), InMessage->EventCode & 0x7fff, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt, InMessage->Return.TrxID, InMessage->Return.UserID);

        /* Rebuild the status word */
        USHORT lcuDigital = MAKEUSHORT (InMessage->Buffer.InMessage[6], InMessage->Buffer.InMessage[17]);

        /* Now loop through and update remote processes as needed */
        for(i = 1; i <= 16; i++)
        {
            if((PointRecord = getDevicePointOffsetTypeEqual(i, StatusPointType)) != NULL)
            {
                PValue = ((lcuDigital >> (i - 1)) & 0x0001) ? CLOSED : OPENED;

                if(isLCU(getType()))
                {
                    resultString = getName() + " / " + PointRecord->getName() + " is ";
                    if(PValue == CLOSED)
                        resultString += "ACTIVE";
                    else
                        resultString += "INACTIVE";
                }
                else
                {
                    resultString = "Error " + RWCString(__FILE__) + "(" + CtiNumStr(__LINE__) + ")";
                }

                pData = CTIDBG_new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, StatusPointType, resultString);
                if(pData != NULL)
                {
                    if(pPIL != NULL)
                    {
                        pPIL->PointData().insert(pData);
                        pData = NULL;  // We just put it on the list...
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "ERROR: " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        delete pData;
                        pData = NULL;
                    }
                }
            }
        }

        if(pPIL->PointData().entries() == 0)
        {
            pPIL->setResultString("Communication Successful.  LCU has no DB defined points.");
        }
    }

    return pPIL;
}

CtiReturnMsg* CtiDeviceLCU::lcuDecodeStatus(INMESS *InMessage)
{
    ULONG      i;
    RWCString  resultString;

    //  Define the various records
    CtiPoint  *PointRecord;

    //  Variables for decoding LCU Messages
    FLOAT      PValue;

    CtiPointDataMsg *pData = NULL;

    CtiReturnMsg    *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                             RWCString(InMessage->Return.CommandStr),
                                             RWCString("LCU status request complete"),
                                             InMessage->EventCode & 0x7fff,
                                             InMessage->Return.RouteID,
                                             InMessage->Return.MacroOffset,
                                             InMessage->Return.Attempt,
                                             InMessage->Return.TrxID,
                                             InMessage->Return.UserID);

    switch(getLCUType())
    {
    case (CtiDeviceLCU::LCU_STANDARD):
    case (CtiDeviceLCU::LCU_LANDG):
    case (CtiDeviceLCU::LCU_EASTRIVER):
    case (CtiDeviceLCU::LCU_T3026):
        {
            //  Rebuild the status word
            _lcuStatus = MAKEUSHORT (InMessage->Buffer.InMessage[4], InMessage->Buffer.InMessage[5]);

            //  Now loop through and update remote processes as needed
            for(i = 1; i <= 16; i++)
            {
                if((PointRecord = getDevicePointOffsetTypeEqual(i + 16, StatusPointType)) != NULL)
                {
                    PValue = ((_lcuStatus >> (i - 1)) & 0x0001) ? CLOSED : OPENED;

                    if(isLCU(getType()))
                    {
                        resultString = getName() + " / " + PointRecord->getName() + " is ";
                        if(PValue == CLOSED)
                            resultString += "ACTIVE";
                        else
                            resultString += "INACTIVE";
                    }
                    else
                    {
                        resultString = "Error " + RWCString(__FILE__) + "(" + CtiNumStr(__LINE__) + ")";
                    }

                    pData = CTIDBG_new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, StatusPointType, resultString);
                    if(pData != NULL)
                    {
                        if(pPIL != NULL)
                        {
                            pPIL->PointData().insert(pData);
                            pData = NULL;  // We just put it on the list...
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "ERROR: " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            delete pData;
                            pData = NULL;
                        }
                    }
                }
            }
            break;
        }
    default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    if(pPIL->PointData().entries() == 0)
    {
        pPIL->setResultString("Communication Successful.  LCU has no DB defined points.");
    }

    return pPIL;
}

CtiReturnMsg* CtiDeviceLCU::lcuDecodeAccumulators(INMESS *InMessage, RWTPtrSlist< OUTMESS > &outList)
{
    ULONG      i;
    RWCString  resultString;

    /* Define the various records */
    CtiPoint  *PointRecord;

    /* Variables for decoding LCU Messages */
    DOUBLE     PValue;

    CtiPointDataMsg *pData = NULL;
    CtiReturnMsg    *pPIL = 0;

    CtiPointAccumulator *pAccumPoint;

    if(isScanFreezePending())
    {
        if((pAccumPoint = (CtiPointAccumulator *)getDevicePointOffsetTypeEqual(1, DemandAccumulatorPointType)) != NULL)
        {
            // Freeze Failed to dispatch message
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
        if((pAccumPoint = (CtiPointAccumulator *)getDevicePointOffsetTypeEqual(1, PulseAccumulatorPointType)) != NULL)
        {
            // Freeze Failed to dispatch message
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        /* This is catastrophic so zero out previous freeze's */
        setPrevFreezeNumber(0);

        /* if it was just a reset let this one be */
        if(isScanFrozen() && !isScanFreezeFailed())
            setLastFreezeNumber(!0);
        else
            setLastFreezeNumber(0);

        resetScanFrozen();
        resetScanFreezeFailed();
        resetScanFreezePending();
    }
    else if(isScanFrozen())
    {
        pPIL  = CTIDBG_new CtiReturnMsg(getID(),
                                 RWCString(InMessage->Return.CommandStr),
                                 RWCString("LCU accumulator request complete"),
                                 InMessage->EventCode & 0x7fff,
                                 InMessage->Return.RouteID,
                                 InMessage->Return.MacroOffset,
                                 InMessage->Return.Attempt,
                                 InMessage->Return.TrxID,
                                 InMessage->Return.UserID);

        USHORT Value = MAKEUSHORT(InMessage->Buffer.InMessage[7], InMessage->Buffer.InMessage[8]);

        if((pAccumPoint = (CtiPointAccumulator *)getDevicePointOffsetTypeEqual(1, DemandAccumulatorPointType)) != NULL)
        {
            if( getPrevFreezeTime() != getLastFreezeTime() )
            {
                /* Calculate the part of an hour involved here */
                FLOAT PartHour = (FLOAT)(getLastFreezeTime().seconds() - getPrevFreezeTime().seconds());
                PartHour /= (3600.0);

                #if 0
                /* to convert to units */
                PValue /= PartHour;
                //  apply multiplier and offset
                PValue = pAccumPoint->computeValueForUOM(PValue);
                #else

                PValue = (FLOAT) Value * pAccumPoint->getMultiplier();
                /* to convert to units */
                PValue /= PartHour;
                PValue += pAccumPoint->getDataOffset();

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  Demand accumulators are not computed correctly " << endl;
                }

                #endif

                pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(),
                                            PValue, NormalQuality,
                                            DemandAccumulatorPointType,
                                            RWCString( getName() + " point " + pAccumPoint->getName() + " " + CtiNumStr(PValue) ));

                if(pData != NULL)
                {
                    pPIL->PointData().insert(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        /* Check if there is a pulse point */
        if((pAccumPoint = (CtiPointAccumulator *)getDevicePointOffsetTypeEqual(1, PulseAccumulatorPointType)) != NULL)
        {
            /* Calculate in units/hour */
            PValue = (FLOAT) Value * pAccumPoint->getMultiplier();

            /* Apply offset */
            PValue += pAccumPoint->getDataOffset();
            pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, PulseAccumulatorPointType, RWCString( getName() + " point " + pAccumPoint->getName() + " " + CtiNumStr(PValue) ));
            if(pData != NULL)
            {
                pPIL->PointData().insert(pData);
                pData = NULL;  // We just put it on the list...
            }
        }

        resetScanFrozen();
        resetScanFreezeFailed();

        // Need to perform an lcuReset here!
        OUTMESS *OutMessage = CTIDBG_new OUTMESS;

        if(OutMessage != NULL)
        {
            InEchoToOut(InMessage, OutMessage);
            CtiCommandParser parse(InMessage->Return.CommandStr);
            lcuReset(OutMessage);

            outList.insert( OutMessage );
        }
    }


    return pPIL;
}

CtiReturnMsg* CtiDeviceLCU::lcuDecodeAnalogs(INMESS *InMessage)
{
    ULONG      i;
    RWCString  resultString;

    /* Define the various records */
    CtiPoint  *PointRecord;

    /* Variables for decoding LCU Messages */
    DOUBLE     PValue;

    CtiPointDataMsg *pData = NULL;
    CtiReturnMsg     *pPIL = CTIDBG_new CtiReturnMsg(getID(), RWCString(InMessage->Return.CommandStr), RWCString("LCU analog request complete"), InMessage->EventCode & 0x7fff, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt, InMessage->Return.TrxID, InMessage->Return.UserID);


    switch(_lcuType)
    {
    case LCU_T3026:
        {
            int offset = 4;

            for(i = 1; i < 8 && (i * 2 <= InMessage->Buffer.InMessage[3]); i++)
            {
                /*
                 *  Since the T3026 can support multiple expansion modules, we will process any byte pairs as analogs if told to.
                 *  Be careful not to decode stati as analogs por favor.
                 */

                if((PointRecord = getDevicePointOffsetTypeEqual(i, AnalogPointType)) != NULL)
                {
                    CtiPointNumeric *pNum = (CtiPointNumeric*)PointRecord;

                    // A bit of BCD conversion here!
                    INT itemp = 0;
                    itemp = (InMessage->Buffer.InMessage[offset + 1] >> 4) & 0x0f;
                    itemp *= 10;
                    itemp += (InMessage->Buffer.InMessage[offset + 1]) & 0x0f;
                    itemp *= 10;
                    itemp += (InMessage->Buffer.InMessage[offset] >> 4) & 0x0f;
                    itemp *= 10;
                    itemp += (InMessage->Buffer.InMessage[offset]) & 0x0f;

                    PValue = pNum->computeValueForUOM( (DOUBLE) (itemp - gConfigParms.getValueAsInt("LCUT3026_ANALOG_SKEW", 500)) );

                    resultString = getName() + " / " + PointRecord->getName() + " = " + CtiNumStr(PValue);

                    #if 0
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << resultString << endl;
                    }
                    #endif

                    pData = CTIDBG_new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, AnalogPointType, resultString);
                    if(pData != NULL)
                    {
                        if(pPIL != NULL)
                        {
                            pPIL->PointData().insert(pData);
                            pData = NULL;  // We just put it on the list...
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "ERROR: " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            delete pData;
                            pData = NULL;
                        }
                    }
                }

                offset += 2;
            }

            if(pPIL->PointData().entries() == 0)
            {
                pPIL->setResultString("Communication Successful.  LCU has no DB defined points.");
            }

            break;
        }
    default:
        {
            USHORT Value;

            /* Now loop through and update remote processes as needed */
            for(i = 1; i <= 8; i++)
            {
                if((PointRecord = getDevicePointOffsetTypeEqual(i, AnalogPointType)) != NULL)
                {
                    CtiPointNumeric *pNum = (CtiPointNumeric*)PointRecord;


                    Value = MAKEUSHORT(InMessage->Buffer.InMessage[i + 8], 0);
                    PValue = pNum->computeValueForUOM( (DOUBLE) Value );

                    if(isLCU(getType()))
                    {
                        resultString = getName() + " / " + PointRecord->getName() + " = " + CtiNumStr(PValue);
                    }
                    else
                    {
                        resultString = "Error " + RWCString(__FILE__) + "(" + CtiNumStr(__LINE__) + ")";
                    }

                    pData = CTIDBG_new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, AnalogPointType, resultString);
                    if(pData != NULL)
                    {
                        if(pPIL != NULL)
                        {
                            pPIL->PointData().insert(pData);
                            pData = NULL;  // We just put it on the list...
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "ERROR: " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            delete pData;
                            pData = NULL;
                        }
                    }
                }
            }

            if(pPIL->PointData().entries() == 0)
            {
                pPIL->setResultString("Communication Successful.  LCU has no DB defined points.");
            }

            break;
        }
    }

    return pPIL;
}

/* Routine to calculate time a ripple shed will take */
ULONG CtiDeviceLCU::lcuTime(OUTMESS *&OutMessage, UINT lcuType)
{
    if(_lcuGlobalsInit == FALSE)
    {
        initLCUGlobals();
    }

    RWMutexLock::LockGuard guard(_staticMux);
    ULONG Seconds = 0;

#ifdef REALTIME

    ULONG Count;
    ULONG SubCount;

    /* first see how many bytes are involved */
    Count = OutMessage->Buffer.OutMessage[PREIDLEN + 3];

    /* now see how many bits are involved in last byte */
    for(SubCount = 0; SubCount <= 7; SubCount++)
    {
        if(((OutMessage->Buffer.OutMessage[PREIDLEN + MASTERLENGTH + Count - 1] >> SubCount) & 0x0001))
            break;
    }

    /* Calculate the number of bits involved */
    Count *= 8;
    Count += (8 - SubCount);
    Count += 2;

    /* Now Calculate the time */
    Seconds = 733L + 467L + (Count * 2L * 267L);
    Seconds /= 1000L;
    Seconds++;

#else

    if(lcuType == LCU_LANDG)
    {
        if(OutMessage->Remote != RTUGLOBAL)
        {
            Seconds = _landgMinimumTime;        // Defaults to 50L
        }
        else
        {
            Seconds = _landgTimeout;            // Defaults to 100L
        }
    }
    else if( lcuType == LCU_EASTRIVER )
    {
        if( (OutMessage->OutLength - MASTERLENGTH) < 6 )
        {
            // shorter message
            Seconds = 32L;
        }
        else
        {
            // assume longest message
            Seconds = 42L;
        }
    }
    else
    {
        Seconds = 30L;
    }
#endif

    return(Seconds);
}


void CtiDeviceLCU::initLCUGlobals()
{
    char temp[80];
    RWCString str;

    if(!_lcuGlobalsInit)
    {
        RWMutexLock::LockGuard guard(_staticMux);


        if( !(str = gConfigParms.getValueAsString("DSM2_LANDGMINIMUMTIME")).isNull() )
        {
            char *eptr;
            _landgMinimumTime = strtoul(str.data(), &eptr, 10);
            if(DebugLevel & 0x0001)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Configuration Parameter DSM2_LANDGMINIMUMTIME found : " << temp << endl;
            }
        }

        if( !(str = gConfigParms.getValueAsString("DSM2_LANDGTIMEOUT")).isNull() )
        {
            char *eptr;
            _landgTimeout = strtoul(str.data(), &eptr, 10);
            if(DebugLevel & 0x0001)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Configuration Parameter DSM2_LANDGTIMEOUT     found : " << str << endl;
            }
        }

        if( !(str = gConfigParms.getValueAsString("DSM2_LANDGRETRIES")).isNull() )
        {
            char *eptr;
            _landgRetries = strtoul(str.data(), &eptr, 10);
            if(DebugLevel & 0x0001)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Configuration Parameter DSM2_LANDGRETRIES     found : " << str << endl;
            }
        }

        if( !(str = gConfigParms.getValueAsString("DSM2_EREPCRETRIES")).isNull() )
        {
            char *eptr;
            _erepcRetries = strtoul(str.data(), &eptr, 10);
            if(DebugLevel & 0x0001)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Configuration Parameter DSM2_EREPCRETRIES     found : " << str << endl;
            }
        }

        if( !(str = gConfigParms.getValueAsString("RIPPLE_STARTSTOP_CROSSINGS")).isNull() )
        {
            char *eptr;
            _lcuStartStopCrossings = strtoul(str.data(), &eptr, 10);
            if(DebugLevel & 0x0001)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Configuration Parameter RIPPLE_STARTSTOP_CROSSINGS     found : " << str << endl;
            }
        }

        if( !(str = gConfigParms.getValueAsString("RIPPLE_BIT_CROSSINGS")).isNull() )
        {
            char *eptr;
            _lcuStartStopCrossings = strtoul(str.data(), &eptr, 10);
            if(DebugLevel & 0x0001)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Configuration Parameter RIPPLE_BIT_CROSSINGS     found : " << str << endl;
            }
        }

        if( !(str = gConfigParms.getValueAsString("RIPPLE_DUTY_CYCLE_PERCENT")).isNull() )
        {
            char *eptr;
            _lcuDutyCyclePercent = strtoul(str.data(), &eptr, 10);
            if(DebugLevel & 0x0001)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Configuration Parameter RIPPLE_DUTY_CYCLE_PERCENT     found : " << str << endl;
            }
        }

        if( !(str = gConfigParms.getValueAsString("RIPPLE_SLOW_SCAN_DELAY")).isNull() )
        {
            char *eptr;
            _lcuSlowScanDelay = strtoul(str.data(), &eptr, 10);
            if(DebugLevel & 0x0001)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Configuration Parameter RIPPLE_SLOW_SCAN_DELAY     found : " << str << endl;
            }
        }

        if( gConfigParms.getValueAsString("RIPPLE_OBSERVE_BUSYBIT") == RWCString("FALSE") )
        {
            _lcuObserveBusyBit = false;       // Make us go at maximal speed.
        }

        if( !gConfigParms.getValueAsString("RIPPLE_EXCLUDE_ALL_INJECTORS").compareTo("TRUE", RWCString::ignoreCase) )
        {
            _excludeAllLCUs = true;
        }


        CtiDeviceLCU::_lcuGlobalsInit = TRUE;
    }
}

// delete and set to CTIDBG_new val.
CtiDeviceLCU& CtiDeviceLCU::setLastControlMessage(const OUTMESS *pOutMessage)
{
    LockGuard guard(monitor());

    if(_lastControlMessage != NULL)
    {
        delete _lastControlMessage;
        _lastControlMessage = 0;
    }

    _lastControlMessage = CTIDBG_new OUTMESS( *pOutMessage );

    _lastCommand = RWCString( _lastControlMessage->Request.CommandStr );

    return *this;
}

// return OUTMESS or NULL
OUTMESS* CtiDeviceLCU::getLastControlMessage()
{
    return _lastControlMessage;
}

// set NULL, we've writen it into a queue
OUTMESS*  CtiDeviceLCU::releaseLastControlMessage()
{
    LockGuard guard(monitor());
    OUTMESS* lcm = _lastControlMessage;
    _lastControlMessage = NULL;
    return lcm;
}

// delete and set NULL
void CtiDeviceLCU::deleteLastControlMessage()
{
    LockGuard guard(monitor());

    if(_lastControlMessage != NULL)
    {
        delete _lastControlMessage;
        _lastControlMessage = NULL;
    }
    return;
}

RWTime CtiDeviceLCU::getStageTime() const
{
    return _stageTime;
}

CtiDeviceLCU& CtiDeviceLCU::setStageTime(const RWTime& aTime)
{
    _stageTime = aTime;
    return *this;
}

RWTime CtiDeviceLCU::getNextCommandTime() const
{
    return _nextCommandTime;
}

CtiDeviceLCU& CtiDeviceLCU::setNextCommandTime(const RWTime& aTime)
{
    _nextCommandTime = aTime;
    return *this;
}

UINT CtiDeviceLCU::setFlags( const UINT mask )
{
    LockGuard guard(monitor());
    _lcuFlags |= mask;
    return _lcuFlags;
}
UINT CtiDeviceLCU::resetFlags( const UINT mask )
{
    LockGuard guard(monitor());
    _lcuFlags &= ~(mask);
    return _lcuFlags;
}
UINT CtiDeviceLCU::getFlags()
{
    return _lcuFlags;
}

bool CtiDeviceLCU::isFlagSet(UINT flags) const
{
    LockGuard guard(monitor());
    return(_lcuFlags & flags);
}

UINT CtiDeviceLCU::getNumberStarted() const
{
    return _numberStarted;
}

CtiDeviceLCU& CtiDeviceLCU::setNumberStarted( const UINT ui )
{
    _numberStarted = ui;
    return *this;
}


BOOL CtiDeviceLCU::isStagedUp(const RWTime &tRef)
{
    LockGuard guard(monitor());

    switch( _lcuType )
    {
    case LCU_LANDG:
    case LCU_EASTRIVER:
        {
            _lcuStaged = TRUE;    // Autostaging
            break;
        }
    case LCU_STANDARD:
    default:
        {
            if( ( _stageTime + STAGETIME - 60) > tRef )
            {
                _lcuStaged = TRUE;
            }
            else
            {
                _lcuStaged = FALSE;
            }

            break;
        }
    }

    return _lcuStaged;
}

/* Routine to stage an LCU */
OUTMESS* CtiDeviceLCU::lcuStage(OUTMESS *&OutMessage)
{
    ULONG i;
    OUTMESS *MyOutMessage = NULL;

    if( getLCUType() == CtiDeviceLCU::LCU_STANDARD )
    {
        if(RWTime() > (getStageTime() + STAGETIME - 30L))
        {
            /*
             *  Staged condition may need reinforcement...
             *
             *  LCUs stage up for STAGETIME seconds (30 minutes by default)
             *  The getStageTime() method returns the time that the last successful
             *  stage command was sent.  We want to err on the side of caution and re-stage
             *  the LCU 30 seconds before it de-stages.
             */
            if(getStageTime() != rwEpoch )  // This check prevents us from repeating the send operation repeatedly..
            {
                /* Allocate the memory for the queue entry */
                if((MyOutMessage =  CTIDBG_new OUTMESS(*OutMessage) ) != NULL)
                {
                    /* Load up all the goodies */
                    OverrideOutMessagePriority( OutMessage, MAXPRIORITY - 2 );
                    MyOutMessage->TimeOut = 2;
                    MyOutMessage->OutLength = MASTERLENGTH;
                    MyOutMessage->InLength = -1;
                    MyOutMessage->EventCode = NORESULT | ENCODED | STAGE;
                    MyOutMessage->Sequence = 0;
                    MyOutMessage->Retry = 2;
                    MyOutMessage->ReturnNexus = NULL;
                    MyOutMessage->SaveNexus = NULL;

                    /* Build MasterComm header */
                    if((i = MasterHeader (MyOutMessage->Buffer.OutMessage + PREIDLEN, MyOutMessage->Remote, MASTERSTAGE, 0)) != NORMAL)
                    {
                        delete (MyOutMessage);
                        MyOutMessage = NULL;
                    }

                    if(MyOutMessage != NULL)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        setStageTime( rwEpoch );
                    }
                }
            }
        }
    }

    return(MyOutMessage);
}


BOOL CtiDeviceLCU::isBusyByCommand(const RWTime &aTime) const
{
    BOOL busy = FALSE;

    assert( RWTime() + 1800 > getNextCommandTime() );

    if( getNextCommandTime() > aTime)   // This LCU is busy according to it's next command time...
    {
        busy = TRUE;
    }

    return busy;
}

ULONG CtiDeviceLCU::lcuRetries()
{
    return _landgRetries;
}

CtiDeviceLCU::CtiLCUType_t   CtiDeviceLCU::getLCUType() const
{
    return _lcuType;
}

void CtiDeviceLCU::verifyControlLockoutState(INMESS *InMessage)
{
    if(_lcuType == LCU_LANDG || _lcuType == LCU_STANDARD)
    {
        if(((InMessage->Buffer.InMessage[5] & LCULOCKEDOUT) && !(getControlInhibit())) ||
           (!(InMessage->Buffer.InMessage[5] & LCULOCKEDOUT) && (getControlInhibit())))
        {
            /* Database disagrees */
            if(InMessage->Buffer.InMessage[5] & LCULOCKEDOUT)
            {
                _lockedOut = true;
            }
            else
            {
                _lockedOut = false;
            }
        }
    }
    else if( _lcuType == LCU_EASTRIVER )
    {
        if(InMessage->Buffer.InMessage[4] & MnA_TESTMODE && !(isFlagSet(LCUWASTRANSMITTING)) )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << getName() << " LOCAL MODE is set. " << endl;
            }

            lcuResetFlagsAndTags();
        }
    }
}

bool CtiDeviceLCU::isLCUAlarmed( INMESS *InMessage )
{
    bool bAlarmed = false;

    if( _lcuType == LCU_STANDARD )
    {
        if(InMessage->Buffer.InMessage[4] & LCUCHECKBITALARM)
        {
            bAlarmed = true;
        }
    }
    else if( _lcuType == LCU_LANDG )
    {
        if(InMessage->Buffer.InMessage[4] & LCUCHECKBITALARM)
        {
            bAlarmed = true;
        }
        else if(InMessage->Buffer.InMessage[5] & LCUMINORALARM)
        {
            bAlarmed = true;
        }
    }
    else if( _lcuType == LCU_EASTRIVER )
    {
        if(InMessage->Buffer.InMessage[4] & MnA_ANYALARM)
        {
            dumpStatus( InMessage->Buffer.InMessage[4], InMessage->Buffer.InMessage[5] );
            bAlarmed = true;
        }
    }

    return bAlarmed;
}

RWMutexLock& CtiDeviceLCU::getExclusionMux()
{
    return _lcuExclusionMux;
}

CtiDeviceLCU& CtiDeviceLCU::operator=(const CtiDeviceLCU& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
    }
    return *this;
}

/*
 *   This is a porter side decode used only when the OutMessage sent was RIPPLE'd and a control!
 *   It makes him pound the LCU into submission!
 */
INT CtiDeviceLCU::lcuFastScanDecode(OUTMESS *&OutMessage, INMESS *InMessage, CtiLCUResult_t &resultCode, bool globalControlAvailable, RWTPtrSlist< CtiMessage >  &vgList)
{
    INT status = NORMAL;

    if(InMessage->InLength > 0)
    {
        if( InMessage->Buffer.InMessage[0] == 0x01 )
        {
            switch( InMessage->Buffer.InMessage[2] )
            {
            case MASTERSCANALL:
            case MASTERSCANINT:
                {
                    vgList.insert( lcuDecodeStatus(InMessage) );
                    // FALL THROUGH FALL THROUGH....
                }
            case MASTERSENDREPLY:
            case MASTERLOCKOUTSET:
            case MASTERLOCKOUTRESET:
                {
                    /* First off update the control lockout flag if neccessary */
                    verifyControlLockoutState( InMessage );

                    if( isFlagSet(LCUTRANSMITSENT) )                                  // Decode only if I sent something..
                    {
                        if(getLCUType() == LCU_STANDARD || getLCUType() == LCU_T3026)
                        {
                            resultCode = eLCUSlowScan;
                        }
                        else
                        {
                            resultCode = eLCUFastScan;
                        }

                        if(InMessage->Buffer.InMessage[4] & LCUBUSYTRANSMITTING)       // Does the LCU indicate that he is busy
                        {
                            if( !(isInhibited()) )
                            {
                                setFlags( LCUWASTRANSMITTING );
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " " << getName() << " \"BUSY\" scanning for \"NOT-BUSY\" " << endl;
                                }
                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << "**** WARNING: Inhibited LCU \" " << getName() << " just reported as busy **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                            }
                        }     // Busy Transmitting
                        else
                        {     // NOT Busy.
                            if( isLCULockedOut( InMessage ) )  /* Check if the lockout flag is set */
                            {
                                if(getLastControlMessage() != NULL)             /* Check if this was sent just to us ... */
                                {
                                    #if 0
                                    deleteLastControlMessage();                  // Blast the memory..

                                    if( !(isFlagSet(LCUWASTRANSMITTING)) )
                                    {
                                        setNextCommandTime( rwEpoch );            // Mark him as a non-player.. Not TXing.
                                    }
                                    resetFlags( LCUTRANSMITSENT | LCUWASTRANSMITTING );
                                    #else
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    }
                                    #endif
                                    resultCode = eLCULockedOutSpecificControl;     // Let us inform the peasants
                                }
                                else  // It is quite possible that the LCU decode was a result of a global control...
                                {
                                    resultCode = eLCULockedOut;
                                }
                            }       // LCU Locked Out
                            else if( isLCUAlarmed( InMessage ) )
                            {
                                /* Check if we need to put the message back on the queue */
                                if( getLastControlMessage() != NULL )
                                {
                                    resultCode = eLCURequeueDeviceControl;
                                }
                                else if( globalControlAvailable )
                                {
                                    resultCode = eLCURequeueGlobalControl;
                                }
                                else
                                {
                                    resetFlags(LCUTRANSMITSENT | LCUWASTRANSMITTING);;
                                }
                            }       // Checkbit or Minor
                            else if(watchBusyBit() && isFlagSet(LCUWASTRANSMITTING))
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " " << getName() << " HAS COMPLETED AND GONE IDLE! " << endl;
                                }
                                /*
                                 *  This LCUWASTRANSMITTING bit is set only if the result decode was processed
                                 *  (at least) once and the LCU responded with a busy bit status indication!
                                 *
                                 *  If we get to this point we must assume that this LCU completed his control command
                                 */

                                if(getLastControlMessage() != NULL)
                                {
                                    resetFlags(LCUTRANSMITSENT | LCUWASTRANSMITTING);
                                }

                                resultCode = eLCUDeviceControlComplete;
                            }
                            else if(getLastControlMessage() != NULL)
                            {
                                if(getLCUType() == LCU_STANDARD || getLCUType() == LCU_T3026)
                                {
                                    if(getNextCommandTime() < RWTime())
                                    {
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " " << getName() << " HAS CLOCKED OUT ALL BITS! " << endl;
                                        }
                                        // Just let it all end...
                                        resultCode = eLCUDeviceControlComplete;
                                    }
                                }
                                else
                                {
                                    resultCode = eLCUNotBusyNeverTransmitted;
                                }
                            }
                            else if(globalControlAvailable)
                            {
                                resultCode = eLCUDeviceControlComplete;
                                // resultCode = eLCUNotBusyNeverTransmitted;
                            }
                            else
                            {
                                /* Peculiar */
                                resetFlags(LCUTRANSMITSENT | LCUWASTRANSMITTING);
                                deleteLastControlMessage();
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                            }
                        }
                    }

                    break;
                }
            default:
                break;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  Message is not a proper MASTERCOM reply" << endl;
        }
    }

    return status;
}


bool CtiDeviceLCU::isLCULockedOut( ) const
{
    return _lockedOut;
}

bool CtiDeviceLCU::isLCULockedOut( INMESS *InMessage )
{
    if( _lcuType == LCU_LANDG || _lcuType == LCU_STANDARD )
    {
        if(InMessage->Buffer.InMessage[5] & LCULOCKEDOUT)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Local Mode is set on the LCU! " << getName() << endl;
            }
            _lockedOut = true;
        }
    }
    else if( _lcuType == LCU_EASTRIVER )
    {
        if(InMessage->Buffer.InMessage[4] & MnA_TESTMODE && !isFlagSet(LCUWASTRANSMITTING))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Local Mode is set on the LCU! " << getName() << endl;
            }
            _lockedOut = true;
        }
    }

    return _lockedOut;
}

void CtiDeviceLCU::lcuResetFlagsAndTags()
{
    resetFlags(LCUTRANSMITSENT | LCUWASTRANSMITTING);
    deleteLastControlMessage();
    setNextCommandTime( rwEpoch );
    setNumberStarted( 0 );
    releaseToken();
}


void CtiDeviceLCU::dumpStatus(BYTE Byte4, BYTE Byte5)
{
    switch(getLCUType())
    {
    case (LCU_STANDARD):
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    case (LCU_LANDG):
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    case (LCU_EASTRIVER):
        {
            // Was any alarm bit set????
            // LS Status byte [4]
            //#define MnA_TESTMODE       0x01      // De-coupled ???
            //#define MnA_ANYALARM       0x02
            //#define MnA_BUSY           0x04
            //#define MnA_STANDALONE     0x08
            //#define MnA_RESET          0x10
            //#define MnA_NA             0xe0

            // MS Status byte [5]
            //#define MnA_BITERROR       0x01
            //#define MnA_INJDISABLED    0x02
            //#define MnA_INJSTAGED      0x04
            //#define MnA_COUPLING_AL    0x08
            //#define MnA_PRECHARGE_AL   0x10
            //#define MnA_TRANSMIT_AL    0x20
            //#define MnA_OVERCURR_AL    0x40
            //#define MnA_PHASELOSS_AL   0x80

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);

                char ofill = dout.fill('0');

                dout << RWTime() << " " << getName() << " Status bytes = 0x" <<
                hex << setw(2) << (int)Byte4 << " 0x" <<
                hex << setw(2) << (int)Byte5 << dec << endl <<
                ((Byte4 & MnA_TESTMODE)    ? "   MnA_TESTMODE SET\n" : "" ) <<
                ((Byte4 & MnA_ANYALARM)    ? "   MnA_ANYALARM SET\n" : "" ) <<
                ((Byte4 & MnA_BUSY)        ? "   MnA_BUSY SET\n" : "" ) <<
                ((Byte4 & MnA_STANDALONE)  ? "   MnA_STANDALONE SET\n" : "" ) <<
                ((Byte4 & MnA_RESET)       ? "   MnA_RESET SET\n" : "" ) <<
                ((Byte5 & MnA_BITERROR)    ? "   MnA_BITERROR SET\n" : "" ) <<
                ((Byte5 & MnA_INJDISABLED) ? "   MnA_INJDISABLED SET\n" : "" ) <<
                ((Byte5 & MnA_INJSTAGED)   ? "   MnA_INJSTAGED SET\n" : "" ) <<
                ((Byte5 & MnA_COUPLING_AL) ? "   MnA_COUPLING_AL SET\n" : "" ) <<
                ((Byte5 & MnA_PRECHARGE_AL)? "   MnA_PRECHARGE_AL SET\n" : "" ) <<
                ((Byte5 & MnA_TRANSMIT_AL) ? "   MnA_TRANSMIT_AL SET\n" : "" ) <<
                ((Byte5 & MnA_OVERCURR_AL) ? "   MnA_OVERCURR_AL SET\n" : "" ) <<
                ((Byte5 & MnA_PHASELOSS_AL)? "   MnA_PHASELOSS_AL SET\n" : "" ) << endl;

                dout.fill(ofill);
            }
            break;
        }
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    }
}

/*
 *  This function computes how much time the LCU will be transmitting a one value onto the line
 *  The constants come from the ripple spec.
 */

bool CtiDeviceLCU::exceedsDutyCycle(BYTE *bptr)     // bptr MUST point at a valid MASTERCOM message
{
    bool bStatus = false;
    bool bnewminute = false;
    static int currentminute = 0;                  // What minute was it last?
    int nextinterval = DUTYCYCLESIZE;
    RWTime now;                             // Current Time.

    double currenttally = 0.0;
    double dutycycleallotment = 60.0 * (double)(DUTYCYCLESIZE) * (double)_lcuDutyCyclePercent / 100.0;


    if(bptr[0] == MASTERHEADER && (bptr[2] == MASTERSEND || bptr[2] == MASTERSENDREPLY))
    {
        // OK, we have a mastercom outbound which is a CONTROL command
        int i, j;
        int len = bptr[3];                                              // Number of mastercom data bytes to count across.
        int honkcntr = 0;                                               // Number of one bits in a message
        double txtime = 3.0 * (double)_lcuStartStopCrossings / 60.0;    // Number of seconds of honk contributed by this message. start + 2 * stop bits.

        for(i = 0; i < len; i++)
        {
            for(j = 0; j < 8; j++)
            {
                if( (bptr[4 + i] >> j) & 0x01 )         // If we have a bit here!
                {
                    honkcntr++;
                }
            }
        }

        txtime += (double)honkcntr * (double)_lcuBitCrossings / 60.0;        // How much do these bits contribute.

        if(currentminute != (now.minute() % DUTYCYCLESIZE))
        {
            bnewminute = true;
            currentminute = now.minute() % DUTYCYCLESIZE;
            if(currentminute > 0) nextinterval = currentminute - 1;
            _honktime[currentminute] = make_pair( now.seconds() - now.seconds() % 60, 0.0 );
        }

        for(i=0; i < _honktime.size(); i++)
        {
            if(_honktime[currentminute].first - 60 * DUTYCYCLESIZE <= _honktime[i].first)
            {
                currenttally += _honktime[i].second;
            }
            else
            {
                _honktime[i].second = 0.0;
            }
        }

        if(currenttally + txtime > dutycycleallotment)
        {
            bStatus = true;

            if( bnewminute )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " DUTY CYCLE PAUSE: ";
                dout << " Message would have contributed " << honkcntr << " bits, and " << txtime << " seconds" << endl;
                dout << RWTime() << "   Cannot transmit because " << currenttally + txtime << " exceeds the allotted " << dutycycleallotment << " seconds" << endl;
            }
        }
        else
        {
            // We can GO!  add us into the tally!
            _honktime[currentminute].second = _honktime[currentminute].second + txtime;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Message is " << honkcntr << " bits = " << txtime << " seconds, totalling " << currenttally + txtime << " of an allotted " << dutycycleallotment << " seconds" << endl;
            }
        }
    }

    return bStatus;
}

void CtiDeviceLCU::assignToken(ULONG id)
{
    RWMutexLock::LockGuard guard(_staticMux);

    if( CtiDeviceLCU::excludeALL() )
    {
        _lcuWithToken = id;
    }
}

void CtiDeviceLCU::releaseToken()
{
    RWMutexLock::LockGuard guard(_staticMux);
    _lcuWithToken = 0;
}

ULONG CtiDeviceLCU::whoHasToken()
{
    RWMutexLock::LockGuard guard(_staticMux);
    return _lcuWithToken;
}

bool CtiDeviceLCU::tokenIsAvailable(ULONG id_whosasking)
{
    bool ret = false;

    RWMutexLock::LockGuard guard(_staticMux);

    if( !CtiDeviceLCU::excludeALL() || _lcuWithToken == 0 || _lcuWithToken == id_whosasking)
    {
        ret = true;
    }

    return ret;
}

bool CtiDeviceLCU::excludeALL()
{
    initLCUGlobals();
    return _excludeAllLCUs;
}

ULONG CtiDeviceLCU::getSlowScanDelay()
{
    initLCUGlobals();
    return _lcuSlowScanDelay;
}

bool CtiDeviceLCU::watchBusyBit() const
{
    bool ret = true;

    initLCUGlobals();               // Method uses _lcuObserveBusyBit.

    if((_lcuType == LCU_STANDARD || _lcuType == LCU_T3026) && !_lcuObserveBusyBit)
    {
        ret = false;
    }

    return ret;
}

bool CtiDeviceLCU::isExecutionProhibitedByInternalLogic() const
{
    bool prohibited = false;
    RWTime now;

    if(now < getNextCommandTime())
    {
        prohibited = true;
    }

    return prohibited;
}

INT CtiDeviceLCU::getProtocolWrap() const
{
    INT protocol = ProtocolWrapIDLC;

    switch(_lcuType)
    {
    case LCU_T3026:
        {
            protocol = ProtocolWrapNone;
            break;
        }
    default:
        {
            protocol = ProtocolWrapIDLC;
            break;
        }
    }

    if(gConfigParms.isOpt("LCU_PROTOCOLWRAP"))
    {
        if( !gConfigParms.getValueAsString("LCU_PROTOCOLWRAP").compareTo("idlc", RWCString::ignoreCase) )
        {
            protocol = ProtocolWrapIDLC;
        }
        else
        {
            protocol = ProtocolWrapNone;
        }
    }

    return protocol;
}

/*  */
INT CtiDeviceLCU::lcuLockout(OUTMESS *&OutMessage, bool set)
{
    INT status = NORMAL;
    USHORT cmd = (set ? MASTERLOCKOUTSET : MASTERLOCKOUTRESET);

    /* Load the forced scan message */
    if((status = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)getAddress(), cmd, 0)) != NORMAL)
        return(status);

    /* Load all the other stuff that is needed */
    OverrideOutMessagePriority( OutMessage, MAXPRIORITY - 2 );
    OutMessage->DeviceID        = getID();
    OutMessage->Port            = getPortID();
    OutMessage->Remote          = getAddress();
    OutMessage->TimeOut         = 2;
    OutMessage->OutLength       = 4;
    OutMessage->InLength        = -1;
    OutMessage->EventCode       = RESULT | ENCODED;
    OutMessage->Sequence        = 0;
    OutMessage->Retry           = 2;

    return(status);
}

//make up for the old way of doing mpcpointset and clear
//ecs 12/10/2004
CtiPointDataMsg* CtiDeviceLCU::getPointSet( int status )
{
    CtiPointBase    *pPoint = NULL;
    CtiPointDataMsg *pData = NULL;

    //put some stuff here
    pPoint = getDevicePointOffsetTypeEqual( status, StatusPointType );

    if( pPoint )
    {
        pData = CTIDBG_new CtiPointDataMsg();
        pData->setId( pPoint->getPointID() );
        pData->setType( StatusPointType );
        pData->setQuality( NormalQuality );
        pData->setValue( 1 );

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Sending **** " << pPoint->getPointID() << endl;
        }

    }

    return pData;
}

CtiPointDataMsg* CtiDeviceLCU::getPointClear( int status )
{
    CtiPointBase    *pPoint = NULL;
    CtiPointDataMsg *pData = NULL;

    //put some stuff here
    pPoint = getDevicePointOffsetTypeEqual( status, StatusPointType );

    if( pPoint )
    {
        pData = CTIDBG_new CtiPointDataMsg();
        pData->setId( pPoint->getPointID() );
        pData->setType( StatusPointType );
        pData->setQuality( NormalQuality );
        pData->setValue( 0 );

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Sending **** " << pPoint->getPointID() << endl;
        }

    }

    return pData;
}

void CtiDeviceLCU::pushControlledGroupInfo(LONG LMGIDControl, UINT TrxID)
{
    _controlledGroupVector.push_back( make_pair(LMGIDControl, TrxID) );
    return;
}

bool CtiDeviceLCU::popControlledGroupInfo(LONG &LMGIDControl, UINT &TrxID)
{
    bool got_one = false;

    if(!_controlledGroupVector.empty())
    {
        const pair<LONG, UINT> &mypair = _controlledGroupVector.back();
        LMGIDControl = mypair.first;
        TrxID = mypair.second;

        _controlledGroupVector.pop_back();
        got_one = true;
    }

    return got_one;
}
