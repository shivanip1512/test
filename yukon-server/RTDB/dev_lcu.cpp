#include "precompiled.h"

#include <iostream>
#include <iomanip>

#include <assert.h>

#include "cparms.h"
#include "connection.h"
#include "cmdparse.h"
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
#include "pt_status.h"
#include "utility.h"

#include "numstr.h"

#define DUTYCYCLESIZE 15

using namespace std;

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
CtiMutex      CtiDeviceLCU::_staticMux;
CtiMutex      CtiDeviceLCU::_lcuExclusionMux;


CtiDeviceLCU::CtiDeviceLCU(INT type) :
_stageTime( PASTDATE ),
_lcuStaged(FALSE),
_lcuFlags(0),
_numberStarted(0),
_nextCommandTime( PASTDATE ),
_honktime(DUTYCYCLESIZE, make_pair(CtiTime().seconds() - CtiTime().seconds() % DUTYCYCLESIZE, 0.0)),
_lockedOut(false),
_lastControlMessage(0),
_lcuStatus(0)
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

CtiDeviceLCU::~CtiDeviceLCU()
{
    if(_lastControlMessage)
    {
        delete _lastControlMessage;
        _lastControlMessage = 0;
    }
}


YukonError_t CtiDeviceLCU::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;

    if(OutMessage != NULL)
    {
        if(_lcuType == LCU_T3026)
        {
            setScanFlag(ScanRateGeneral);
            EstablishOutMessagePriority( OutMessage, ScanPriority );

            OUTMESS *pNewOutMessage = CTIDBG_new OUTMESS(*OutMessage);

            status = lcuScanInternalStatus(OutMessage);

            // Put the built up OUTMESS into the Slist
            outList.push_back(OutMessage);
            OutMessage = NULL;

            status = lcuScanExternalStatus(pNewOutMessage);
            outList.push_back(pNewOutMessage);
        }
        else
        {
            setScanFlag(ScanRateGeneral);
            EstablishOutMessagePriority( OutMessage, ScanPriority );
            status = lcuScanAll(OutMessage);

            // Put the single built up OUTMESS into the Slist
            outList.push_back(OutMessage);
            OutMessage = NULL;
        }
    }
    else
    {
        status = ClientErrors::MemoryAccess;
    }

    return status;
}

YukonError_t CtiDeviceLCU::AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;

    if(OutMessage != NULL)
    {
        setScanFlag(ScanFreezePending);
        status = lcuFreeze(OutMessage);

        // Put the single built up OUTMESS into the Slist
        outList.push_back(OutMessage);
        OutMessage = NULL;
    }
    else
    {
        status = ClientErrors::MemoryAccess;
    }

    return status;
}

YukonError_t CtiDeviceLCU::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    return( GeneralScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority) );
}

YukonError_t CtiDeviceLCU::ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    return lcuDecode(InMessage, TimeNow, vgList, retList, outList);
}

/* Routine to output freeze to an LCU */
YukonError_t CtiDeviceLCU::lcuFreeze(OUTMESS *&OutMessage)
{
    YukonError_t status = ClientErrors::None;

    /* Load the freeze message */
    if( status = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)getAddress(), MASTERFREEZE, 0) )
        return(status);

    /* Load all the other stuff that is needed */
    populateRemoteOutMessage(*OutMessage);

    OverrideOutMessagePriority(OutMessage, MAXPRIORITY - 1);        // This is required to make certain fast scans do not block our comms.

    OutMessage->OutLength = 4;
    OutMessage->InLength = -1;

    return(status);
}

/* Routine to reset freeze to an LCU */
INT CtiDeviceLCU::lcuReset(OUTMESS *&OutMessage)
{
    INT status = ClientErrors::None;

    setScanFlag(ScanResetting);

    /* Load the reset freeze message */
    if( status = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)getAddress(), MASTERRESET, 0) )
        return(status);

    /* Load all the other stuff that is needed */
    populateRemoteOutMessage(*OutMessage);

    OverrideOutMessagePriority(OutMessage, MAXPRIORITY - 1);        // This is required to make certain fast scans do not block our comms.

    OutMessage->OutLength = 4;
    OutMessage->InLength = -1;

    return(status);
}

/* Routine to scan ALL LCU status */
YukonError_t CtiDeviceLCU::lcuScanAll(OUTMESS *&OutMessage)            /* Priority to place command on queue */
{
    YukonError_t status = ClientErrors::None;

    if(_lcuType == LCU_T3026)
    {
        status = lcuScanInternalStatus(OutMessage);
    }
    else
    {
        /* Load the forced scan message */
        if( status = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)getAddress(), MASTERSCANALL, 0) )
            return(status);

        /* Load all the other stuff that is needed */
        populateRemoteOutMessage(*OutMessage);
        OutMessage->OutLength       = 4;
        OutMessage->InLength        = -1;
    }

    return(status);
}

/* Routine to scan internal LCU status */
YukonError_t CtiDeviceLCU::lcuScanInternalStatus(OUTMESS *&OutMessage)
{
    YukonError_t status = ClientErrors::None;

    /* Load the forced scan message */
    if( status = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)getAddress(), MASTERSCANINT, 0) )
        return(status);

    /* Load all the other stuff that is needed */
    populateRemoteOutMessage(*OutMessage);
    OutMessage->OutLength       = 4;
    OutMessage->InLength        = -1;

    return(status);
}

/* Routine to scan internal LCU status */
YukonError_t CtiDeviceLCU::lcuScanExternalStatus(OUTMESS *&OutMessage)
{
    YukonError_t status = ClientErrors::None;

    /* Load the forced scan message */
    if( status = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)getAddress(), MASTERSCANEXT, 0) )
        return(status);

    /* Load all the other stuff that is needed */
    populateRemoteOutMessage(*OutMessage);
    OutMessage->OutLength       = 4;
    OutMessage->InLength        = -1;

    return(status);
}

YukonError_t CtiDeviceLCU::lcuDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = InMessage.ErrorCode;

    if( status )
    {
        resetForScan(ScanRateGeneral);
    }
    else if( InMessage.Buffer.InMessage[0] == 0x01 )
    {
        /* decode whatever message this is */
        switch(InMessage.Buffer.InMessage[2])
        {
        case MASTERFREEZE:
            {
                if(isScanFlagSet(ScanFreezePending))
                {
                    resetScanFlag(ScanFreezePending);
                    setScanFlag(ScanFrozen);

                    /* update the accumulator criteria for this RTU */
                    // Done in the reset... which zeros it out.  // setPrevFreezeTime(getLastFreezeTime());
                    setLastFreezeTime( CtiTime(InMessage.Time) );
                    resetScanFlag(ScanFreezeFailed);

                    setPrevFreezeNumber(getLastFreezeNumber());
                    setLastFreezeNumber(getLastFreezeNumber() + 1);

                    /* then force a scan */
                    OUTMESS *OutMessage = new OUTMESS;

                    InEchoToOut(InMessage, *OutMessage);
                    CtiCommandParser parse(InMessage.Return.CommandStr);

                    if( status = GeneralScan (NULL, parse, OutMessage, vgList, retList, outList, MAXPRIORITY - 4) )
                    {
                        CTILOG_ERROR(dout, getName() <<" General Scan return error "<< status);

                        delete OutMessage;
                    }
                }
                else
                {
                    CTILOG_ERROR(dout, "Throwing away unexpected freeze response");

                    setScanFlag(ScanFreezeFailed);
                    /* message for screwed up freeze */
                }

                break;
            }
        case MASTERRESET:
            {
                CTILOG_INFO(dout, getName() <<" accumulator reset");

                setPrevFreezeTime(CtiTime(InMessage.Time));
                setLastFreezeTime(CtiTime(InMessage.Time));
                resetScanFlag(ScanResetting);
                /* LCU is unaware of these */
                break;
            }
        case MASTERSCANEXT:
            {
                CtiMessage *Msg = lcuDecodeDigitalInputs(InMessage);

                if(Msg != NULL)
                {
                    retList.push_back( Msg );
                }

                Msg = lcuDecodeAnalogs(InMessage);

                if(Msg != NULL)
                {
                    retList.push_back( Msg );
                }

                resetScanFlag(ScanRateGeneral);

                break;
            }
        case MASTERSCANINT:
            {
                CtiMessage *Msg = lcuDecodeStatus(InMessage);

                if(Msg != NULL)
                {
                    retList.push_back( Msg );
                }

                resetScanFlag(ScanRateGeneral);

                break;
            }
        case MASTERSCANALL:
            {
                if(useScanFlags())
                {
                    CtiMessage *Msg = lcuDecodeStatus(InMessage);
                    if(Msg != NULL) retList.push_back( Msg );

                    Msg = lcuDecodeDigitalInputs(InMessage);
                    if(Msg != NULL) retList.push_back( Msg );

                    Msg = lcuDecodeAnalogs(InMessage);
                    if(Msg != NULL) retList.push_back( Msg );

                    Msg = lcuDecodeAccumulators(InMessage, outList);
                    if(Msg != NULL) retList.push_back( Msg );
                }
                else
                {
                    CtiMessage *Msg = lcuDecodeStatus(InMessage);
                    if(Msg != NULL) retList.push_back( Msg );
                }

                resetScanFlag(ScanRateGeneral);

                break;
            }
        case MASTERLOOPBACK:
            {
                CtiReturnMsg   *pLoop = CTIDBG_new CtiReturnMsg(getID(),
                                                                string(InMessage.Return.CommandStr),
                                                                string(getName() + " / successful ping"),
                                                                InMessage.ErrorCode,
                                                                InMessage.Return.RouteID,
                                                                InMessage.Return.RetryMacroOffset,
                                                                InMessage.Return.Attempt,
                                                                InMessage.Return.GrpMsgID,
                                                                InMessage.Return.UserID);

                if(pLoop != NULL)
                {
                    retList.push_back(pLoop);
                }

                break;
            }
        case MASTERSEND:
            {
                CtiMessage *Msg = lcuDecodeStatus(InMessage);
                if(Msg != NULL) retList.push_back( Msg );
                break;
            }
        case MASTERLOCKOUTSET:
            {
                CTILOG_INFO(dout, getName() <<" LOCK OUT SET");

                _lockedOut = true;
                break;
            }
        case MASTERLOCKOUTRESET:
            {
                CTILOG_INFO(dout, getName() << " LOCK OUT CLEARED");

                _lockedOut = false;
                break;
            }
        default:
            {
                /* This should never happen so reset the scan */
                CTILOG_ERROR(dout, getName() << " Unknown Mastercom response " << hex << setw(2) << (int)(InMessage.Buffer.InMessage[2]));

                resetScanFlag();
                setScanFlag(ScanStarting);

                break;
            }
        }   /* End of switch */
    }
    else
    {
        CTILOG_ERROR(dout, "Message is not a proper MASTERCOM reply");

        status = ClientErrors::Framing;
    }

    return status;
}


YukonError_t CtiDeviceLCU::ErrorDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList)
{
    YukonError_t status = ClientErrors::None;

    resetForScan(desolveScanRateType(string(InMessage.Return.CommandStr)));
    /* see what handshake was */
    if( useScanFlags() )            // Do we care about any of the scannable flags?
    {
        if(isScanFlagSet(ScanFreezePending))
        {
            resetScanFlag(ScanRateAccum);
            resetScanFlag(ScanFreezePending);
            setScanFlag(ScanFreezeFailed);
            setPrevFreezeTime(getLastFreezeTime());
            setPrevFreezeNumber(getLastFreezeNumber());
            setLastFreezeNumber(0);
            setLastFreezeTime(InMessage.Time);
        }
        else if(isScanFlagSet(ScanRateGeneral))
        {
            resetScanFlag(ScanRateGeneral);

            /* Check if we need to plug accumulators */
            if(isScanFlagSet(ScanFreezeFailed) || isScanFlagSet(ScanFrozen))
            {
                resetScanFlag(ScanFreezeFailed);
                resetScanFlag(ScanFrozen);
                setLastFreezeNumber(0);
            }
        }
        else if(isScanFlagSet(ScanResetting))
        {
            resetScanFlag(ScanResetting);
            setScanFlag(ScanResetFailed);
        }
    }

    return status;
}

YukonError_t CtiDeviceLCU::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList,OutMessageList &outList)
{
    YukonError_t nRet = ClientErrors::None;
    OUTMESS *pOM = 0;

    switch(parse.getCommand())
    {
    case ControlRequest:
        {
            if( int ptOffset = parse.getiValue("point", 0) )
            {
                CtiPointSPtr point = getDevicePointByID(ptOffset);

                if ( ! point )
                {
                    std::string errorMessage = "The specified point is not on device " + getName();
                    insertReturnMsg(ClientErrors::PointLookupFailed, OutMessage, retList, errorMessage);

                    return ClientErrors::PointLookupFailed;
                }

                if( point->isStatus() )
                {
                    CtiPointStatusSPtr pStatus = boost::static_pointer_cast<CtiPointStatus>(point);

                    if( const boost::optional<CtiTablePointStatusControl> controlParameters = pStatus->getControlParameters() )
                    {
                        if( controlParameters->getControlOffset() == 9 )
                        {
                            if(!lcuLockout(OutMessage, parse.getFlags() & (CMD_FLAG_CTL_OPEN | CMD_FLAG_CTL_SHED) ? false : true))
                            {
                                outList.push_back( OutMessage );
                                OutMessage = NULL;
                            }
                        }
                    }
                }
            }
            else
            {
                if((pOM = lcuControl(OutMessage)) == 0)
                {
                    vgList.push_back(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT, pReq->getSOE(), getDescription(parse), string("Control Request for LCU failed"), LoadMgmtLogType, SignalEvent, pReq->getUser()));

                    retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                                               string(OutMessage->Request.CommandStr),
                                                               string("Control Request for LCU failed"),
                                                               nRet,
                                                               OutMessage->Request.RouteID,
                                                               OutMessage->Request.RetryMacroOffset,
                                                               OutMessage->Request.Attempt,
                                                               OutMessage->Request.GrpMsgID,
                                                               OutMessage->Request.UserID,
                                                               OutMessage->Request.SOE,
                                                               CtiMultiMsg_vec()) );
                }
                else
                {
                    outList.push_back( pOM );
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
                CTILOG_ERROR(dout, "Could not scan device "<< getName());

                vgList.push_back(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT, pReq->getSOE(), getDescription(parse), string("Scan All Request for LCU failed"), GeneralLogType, SignalEvent, pReq->getUser()));
                retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                                           string(OutMessage->Request.CommandStr),
                                                           string("Scan All Request for LCU failed"),
                                                           nRet,
                                                           OutMessage->Request.RouteID,
                                                           OutMessage->Request.RetryMacroOffset,
                                                           OutMessage->Request.Attempt,
                                                           OutMessage->Request.GrpMsgID,
                                                           OutMessage->Request.UserID,
                                                           OutMessage->Request.SOE,
                                                           CtiMultiMsg_vec()));
            }
            else
            {
                outList.push_back( OutMessage );
                OutMessage = NULL;
            }

            break;
        }
    case LoopbackRequest:
        {
            // Get a scan done maybe?
            if((nRet = lcuLoop(OutMessage)) != 0)
            {
                CTILOG_ERROR(dout, "Could not loop device "<< getName());

                retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                                           string(OutMessage->Request.CommandStr),
                                                           string(getName() + " / unsuccessful ping to LCU"),
                                                           nRet,
                                                           OutMessage->Request.RouteID,
                                                           OutMessage->Request.RetryMacroOffset,
                                                           OutMessage->Request.Attempt,
                                                           OutMessage->Request.GrpMsgID,
                                                           OutMessage->Request.UserID,
                                                           OutMessage->Request.SOE,
                                                           CtiMultiMsg_vec()));
            }
            else
            {
                outList.push_back( OutMessage );
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
            nRet = ClientErrors::NoMethodForExecuteRequest;
            /* Set the error value in the base class. */
            // FIX FIX FIX 092999
            retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                                       string(OutMessage->Request.CommandStr),
                                                       string("LCU Devices do not support this command (yet?)"),
                                                       nRet,
                                                       OutMessage->Request.RouteID,
                                                       OutMessage->Request.RetryMacroOffset,
                                                       OutMessage->Request.Attempt,
                                                       OutMessage->Request.GrpMsgID,
                                                       OutMessage->Request.UserID,
                                                       OutMessage->Request.SOE,
                                                       CtiMultiMsg_vec()));
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


    if(OutMessage->Remote == LCUGLOBAL || OutMessage->Remote == MASTERGLOBAL )
    {
        OutMessage->Retry = 0;
        if(_lcuType == LCU_EASTRIVER)
        {
            OutMessage->Retry = 2;
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
                if( i = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)OutMessage->Remote, MASTERSENDREPLY, (USHORT)(Count + 1)) )
                {
                    delete (OutMessage);
                    OutMessage = NULL;
                }
            }
            else
            {
                if( i = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)OutMessage->Remote, MASTERSEND, (USHORT)(Count + 1)) )
                {
                    delete (OutMessage);
                    OutMessage = NULL;
                }
            }

            if(OutMessage != NULL)
            {
                /* Copy message into buffer */
                ::memcpy (OutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH, OutMessage->Buffer.RSt.Message, Count + 1);
            }
        }
    }

    return OutMessage;
}


/* Routine to execute a loop message */
YukonError_t CtiDeviceLCU::lcuLoop(OUTMESS *&OutMessage)
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
        OutMessage->TargetID                = getID();
        OutMessage->Port                    = getPortID();
        OutMessage->Remote                  = getAddress();
        OutMessage->Retry                   = 2; //VSt->Retry;
        OutMessage->Sequence                = 0;
        OutMessage->ReturnNexus             = NULL;
        OutMessage->SaveNexus               = NULL;
    }

    return ClientErrors::None;
}


CtiReturnMsg* CtiDeviceLCU::lcuDecodeDigitalInputs(const INMESS &InMessage)
{
    ULONG      i;
    string  resultString;

    /* Define the various records */
    CtiPointSPtr  PointRecord;

    /* Variables for decoding LCU Messages */
    FLOAT      PValue;

    CtiPointDataMsg *pData = 0;
    CtiReturnMsg    *pPIL  = 0;

    if(_lcuType == LCU_T3026)
    {
        if(InMessage.Buffer.InMessage[3] >= 4)
        {
            pPIL = new CtiReturnMsg(getID(),
                                    string(InMessage.Return.CommandStr),
                                    string("LCU status request complete"),
                                    InMessage.ErrorCode,
                                    InMessage.Return.RouteID,
                                    InMessage.Return.RetryMacroOffset,
                                    InMessage.Return.Attempt,
                                    InMessage.Return.GrpMsgID,
                                    InMessage.Return.UserID);

            /*
             *  Due to the T3026's unique nature of connector order determines data order, and a lack of any way to tell
             *  what data is what.  We decode bytes based upon what analog offset is defined.
             */

            int offset = 4;

            if((getDevicePointOffsetTypeEqual(1, AnalogPointType)))
            {
                // Analog offset one is defined.. We will assume DI's come from the second pair (6,7) of bytes
                offset = 6;
            }

            USHORT lcuDigital = MAKEUSHORT (InMessage.Buffer.InMessage[offset], InMessage.Buffer.InMessage[offset+1]);

            for(i = 1; i <= 16; i++)
            {
                if(PointRecord = getDevicePointOffsetTypeEqual(i, StatusPointType))
                {
                    PValue = ((lcuDigital >> (i - 1)) & 0x0001) ? STATE_CLOSED : STATE_OPENED;

                    resultString = getName() + " / " + PointRecord->getName() + " is ";
                    if(PValue == STATE_CLOSED)
                        resultString += "ACTIVE";
                    else
                        resultString += "INACTIVE";

                    pData = new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, StatusPointType, resultString);

                    pPIL->PointData().push_back(pData);
                    pData = NULL;  // We just put it on the list...

                }
            }


            if(pPIL->PointData().size() == 0)
            {
                pPIL->setResultString("Communication Successful.  LCU has no DB defined points.");
            }

        }
        else if(InMessage.Buffer.InMessage[3] == 12)
        {
            CTILOG_WARN(dout, "That is likely not a T3026 LCU!");
        }
        else if(InMessage.Buffer.InMessage[3] != 2)
        {
            // We have a very poor assumption here...
            CTILOG_ERROR(dout, "LCU T3026 does not meet External data assumptions!!!");
        }
    }
    else
    {
        pPIL = new CtiReturnMsg(getID(),
                                string(InMessage.Return.CommandStr),
                                string("LCU status request complete"),
                                InMessage.ErrorCode,
                                InMessage.Return.RouteID,
                                InMessage.Return.RetryMacroOffset,
                                InMessage.Return.Attempt,
                                InMessage.Return.GrpMsgID,
                                InMessage.Return.UserID);

        /* Rebuild the status word */
        USHORT lcuDigital = MAKEUSHORT (InMessage.Buffer.InMessage[6], InMessage.Buffer.InMessage[17]);

        /* Now loop through and update remote processes as needed */
        for(i = 1; i <= 16; i++)
        {
            if(PointRecord = getDevicePointOffsetTypeEqual(i, StatusPointType))
            {
                PValue = ((lcuDigital >> (i - 1)) & 0x0001) ? STATE_CLOSED : STATE_OPENED;

                if(isLCU(getType()))
                {
                    resultString = getName() + " / " + PointRecord->getName() + " is ";
                    if(PValue == STATE_CLOSED)
                        resultString += "ACTIVE";
                    else
                        resultString += "INACTIVE";
                }
                else
                {
                    resultString = "Error " + string(__FILE__) + "(" + CtiNumStr(__LINE__) + ")";
                }

                pData = new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, StatusPointType, resultString);

                pPIL->PointData().push_back(pData);
                pData = NULL;  // We just put it on the list...
            }
        }

        if(pPIL->PointData().size() == 0)
        {
            pPIL->setResultString("Communication Successful.  LCU has no DB defined points.");
        }
    }

    return pPIL;
}

CtiReturnMsg* CtiDeviceLCU::lcuDecodeStatus(const INMESS &InMessage)
{
    ULONG      i;
    string  resultString;

    //  Define the various records
    CtiPointSPtr  PointRecord;

    //  Variables for decoding LCU Messages
    FLOAT      PValue;

    CtiPointDataMsg *pData = NULL;

    CtiReturnMsg    *pPIL = new CtiReturnMsg(getID(),
                                             string(InMessage.Return.CommandStr),
                                             string("LCU status request complete"),
                                             InMessage.ErrorCode,
                                             InMessage.Return.RouteID,
                                             InMessage.Return.RetryMacroOffset,
                                             InMessage.Return.Attempt,
                                             InMessage.Return.GrpMsgID,
                                             InMessage.Return.UserID);

    switch(getLCUType())
    {
    case (CtiDeviceLCU::LCU_STANDARD):
    case (CtiDeviceLCU::LCU_LANDG):
    case (CtiDeviceLCU::LCU_EASTRIVER):
    case (CtiDeviceLCU::LCU_T3026):
        {
            //  Rebuild the status word
            _lcuStatus = MAKEUSHORT (InMessage.Buffer.InMessage[4], InMessage.Buffer.InMessage[5]);

            //  Now loop through and update remote processes as needed
            for(i = 1; i <= 16; i++)
            {
                if(PointRecord = getDevicePointOffsetTypeEqual(i + 16, StatusPointType))
                {
                    PValue = ((_lcuStatus >> (i - 1)) & 0x0001) ? STATE_CLOSED : STATE_OPENED;

                    if(isLCU(getType()))
                    {
                        resultString = getName() + " / " + PointRecord->getName() + " is ";
                        if(PValue == STATE_CLOSED)
                            resultString += "ACTIVE";
                        else
                            resultString += "INACTIVE";
                    }
                    else
                    {
                        resultString = "Error " + string(__FILE__) + "(" + CtiNumStr(__LINE__) + ")";
                    }

                    pData = new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, StatusPointType, resultString);

                    pPIL->PointData().push_back(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            break;
        }
    default:
        {
            CTILOG_ERROR(dout, getName() << " unexpected LCU Type ("<< getLCUType() <<")");
        }
    }

    if(pPIL->PointData().size() == 0)
    {
        pPIL->setResultString("Communication Successful.  LCU has no DB defined points.");
    }

    return pPIL;
}

CtiReturnMsg* CtiDeviceLCU::lcuDecodeAccumulators(const INMESS &InMessage, OutMessageList &outList)
{
    ULONG      i;
    string  resultString;

    /* Define the various records */
    CtiPointSPtr  PointRecord;

    /* Variables for decoding LCU Messages */
    DOUBLE     PValue;

    CtiPointDataMsg *pData = NULL;
    CtiReturnMsg    *pPIL = 0;

    CtiPointAccumulatorSPtr pAccumPoint;

    if(isScanFlagSet(ScanFreezePending))
    {
        if(pAccumPoint = boost::static_pointer_cast<CtiPointAccumulator>(getDevicePointOffsetTypeEqual(1, DemandAccumulatorPointType)))
        {
            CTILOG_ERROR(dout, "Freeze Failed to dispatch message for DemandAccumulatorPointType, with offset of 1"); //FIXME: is this the correct message ?
        }

        if(pAccumPoint = boost::static_pointer_cast<CtiPointAccumulator>(getDevicePointOffsetTypeEqual(1, PulseAccumulatorPointType)))
        {
            CTILOG_ERROR(dout, "Freeze Failed to dispatch message for PulseAccumulatorPointType, with offset of 1"); //FIXME: is this the correct message ?
        }

        /* This is catastrophic so zero out previous freeze's */
        setPrevFreezeNumber(0);

        /* if it was just a reset let this one be */
        if(isScanFlagSet(ScanFrozen) && !isScanFlagSet(ScanFreezeFailed))
            setLastFreezeNumber(!0);
        else
            setLastFreezeNumber(0);

        resetScanFlag(ScanFrozen);
        resetScanFlag(ScanFreezeFailed);
        resetScanFlag(ScanFreezePending);
    }
    else if(isScanFlagSet(ScanFrozen))
    {
        pPIL  = CTIDBG_new CtiReturnMsg(getID(),
                                        string(InMessage.Return.CommandStr),
                                        string("LCU accumulator request complete"),
                                        InMessage.ErrorCode,
                                        InMessage.Return.RouteID,
                                        InMessage.Return.RetryMacroOffset,
                                        InMessage.Return.Attempt,
                                        InMessage.Return.GrpMsgID,
                                        InMessage.Return.UserID);

        USHORT Value = MAKEUSHORT(InMessage.Buffer.InMessage[7], InMessage.Buffer.InMessage[8]);

        if(pAccumPoint = boost::static_pointer_cast<CtiPointAccumulator>(getDevicePointOffsetTypeEqual(1, DemandAccumulatorPointType)))
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
#endif

                pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(),
                                                   PValue, NormalQuality,
                                                   DemandAccumulatorPointType,
                                                   string( getName() + " point " + pAccumPoint->getName() + " " + CtiNumStr(PValue) ));

                if(pData != NULL)
                {
                    pPIL->PointData().push_back(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Unexpected prev freeze time == last freeze time ("<< getPrevFreezeTime() <<")");
            }
        }

        /* Check if there is a pulse point */
        if(pAccumPoint = boost::static_pointer_cast<CtiPointAccumulator>(getDevicePointOffsetTypeEqual(1, PulseAccumulatorPointType)))
        {
            /* Calculate in units/hour */
            PValue = (FLOAT) Value * pAccumPoint->getMultiplier();

            /* Apply offset */
            PValue += pAccumPoint->getDataOffset();
            pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, PulseAccumulatorPointType, string( getName() + " point " + pAccumPoint->getName() + " " + CtiNumStr(PValue) ));
            if(pData != NULL)
            {
                pPIL->PointData().push_back(pData);
                pData = NULL;  // We just put it on the list...
            }
        }

        resetScanFlag(ScanFrozen);
        resetScanFlag(ScanFreezeFailed);

        // Need to perform an lcuReset here!
        OUTMESS *OutMessage = new OUTMESS;

        InEchoToOut(InMessage, *OutMessage);
        CtiCommandParser parse(InMessage.Return.CommandStr);
        lcuReset(OutMessage);

        outList.push_back( OutMessage );
    }


    return pPIL;
}

CtiReturnMsg* CtiDeviceLCU::lcuDecodeAnalogs(const INMESS &InMessage)
{
    ULONG      i;
    string  resultString;

    /* Define the various records */
    CtiPointSPtr  PointRecord;

    /* Variables for decoding LCU Messages */
    DOUBLE     PValue;

    CtiPointDataMsg *pData = NULL;
    CtiReturnMsg     *pPIL = new CtiReturnMsg(getID(),
                                              string(InMessage.Return.CommandStr),
                                              string("LCU analog request complete"),
                                              InMessage.ErrorCode,
                                              InMessage.Return.RouteID,
                                              InMessage.Return.RetryMacroOffset,
                                              InMessage.Return.Attempt,
                                              InMessage.Return.GrpMsgID,
                                              InMessage.Return.UserID);

    switch(_lcuType)
    {
    case LCU_T3026:
        {
            int offset = 4;

            for(i = 1; i < 8 && (i * 2 <= InMessage.Buffer.InMessage[3]); i++)
            {
                /*
                 *  Since the T3026 can support multiple expansion modules, we will process any byte pairs as analogs if told to.
                 *  Be careful not to decode stati as analogs por favor.
                 */

                if(PointRecord = getDevicePointOffsetTypeEqual(i, AnalogPointType))
                {
                    CtiPointNumericSPtr pNum = boost::static_pointer_cast<CtiPointNumeric>(PointRecord);

                    // A bit of BCD conversion here!
                    INT itemp = 0;
                    itemp = (InMessage.Buffer.InMessage[offset + 1] >> 4) & 0x0f;
                    itemp *= 10;
                    itemp += (InMessage.Buffer.InMessage[offset + 1]) & 0x0f;
                    itemp *= 10;
                    itemp += (InMessage.Buffer.InMessage[offset] >> 4) & 0x0f;
                    itemp *= 10;
                    itemp += (InMessage.Buffer.InMessage[offset]) & 0x0f;

                    PValue = pNum->computeValueForUOM( (DOUBLE) (itemp - gConfigParms.getValueAsInt("LCUT3026_ANALOG_SKEW", 500)) );

                    resultString = getName() + " / " + PointRecord->getName() + " = " + CtiNumStr(PValue);

                    pData = new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, AnalogPointType, resultString);

                    pPIL->PointData().push_back(pData);
                    pData = NULL;  // We just put it on the list...
                }

                offset += 2;
            }

            if(pPIL->PointData().size() == 0)
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
                if((PointRecord = getDevicePointOffsetTypeEqual(i, AnalogPointType)))
                {
                    CtiPointNumericSPtr pNum = boost::static_pointer_cast<CtiPointNumeric>(PointRecord);


                    Value = MAKEUSHORT(InMessage.Buffer.InMessage[i + 8], 0);
                    PValue = pNum->computeValueForUOM( (DOUBLE) Value );

                    if(isLCU(getType()))
                    {
                        resultString = getName() + " / " + PointRecord->getName() + " = " + CtiNumStr(PValue);
                    }
                    else
                    {
                        resultString = "Error " + string(__FILE__) + "(" + CtiNumStr(__LINE__) + ")";
                    }

                    pData = new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, AnalogPointType, resultString);

                    pPIL->PointData().push_back(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }

            if(pPIL->PointData().size() == 0)
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

    CtiLockGuard<CtiMutex> guard(_staticMux);
    ULONG Seconds = 0;

#ifdef REALTIME

    ULONG Count;
    ULONG SubCount;

    if(OutMessage)
    {
        /* first see how many bytes are involved */
        Count = OutMessage->Buffer.OutMessage[PREIDLEN + 3];

        /* now see how many bits are involved in last byte */
        for(SubCount = 0; SubCount <= 7; SubCount++)
        {
            if(((OutMessage->Buffer.OutMessage[PREIDLEN + MASTERLENGTH + Count - 1] >> SubCount) & 0x0001))
                break;
        }
    }
    else
    {
        Count = 8;
        Subcount = 0;
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
    string str;

    if(!_lcuGlobalsInit)
    {
        CtiLockGuard<CtiMutex> guard(_staticMux);


        if( !(str = gConfigParms.getValueAsString("DSM2_LANDGMINIMUMTIME")).empty() )
        {
            _landgMinimumTime = strtoul(str.c_str(), NULL, 10);
            if(DebugLevel & 0x0001)
            {
                CTILOG_DEBUG(dout, "Configuration Parameter DSM2_LANDGMINIMUMTIME found      : "<< str);
            }
        }

        if( !(str = gConfigParms.getValueAsString("DSM2_LANDGTIMEOUT")).empty() )
        {
            _landgTimeout = strtoul(str.c_str(), NULL, 10);
            if(DebugLevel & 0x0001)
            {
                CTILOG_DEBUG(dout, "Configuration Parameter DSM2_LANDGTIMEOUT found          : " << str);
            }
        }

        if( !(str = gConfigParms.getValueAsString("DSM2_LANDGRETRIES")).empty() )
        {
            _landgRetries = strtoul(str.c_str(), NULL, 10);
            if(DebugLevel & 0x0001)
            {
                CTILOG_DEBUG(dout, "Configuration Parameter DSM2_LANDGRETRIES found          : " << str);
            }
        }

        if( !(str = gConfigParms.getValueAsString("DSM2_EREPCRETRIES")).empty() )
        {
            _erepcRetries = strtoul(str.c_str(), NULL, 10);
            if(DebugLevel & 0x0001)
            {
                CTILOG_DEBUG(dout, "Configuration Parameter DSM2_EREPCRETRIES found          : " << str);
            }
        }

        if( !(str = gConfigParms.getValueAsString("RIPPLE_STARTSTOP_CROSSINGS")).empty() )
        {
            _lcuStartStopCrossings = strtoul(str.c_str(), NULL, 10);
            if(DebugLevel & 0x0001)
            {
                CTILOG_DEBUG(dout, "Configuration Parameter RIPPLE_STARTSTOP_CROSSINGS found : " << str);
            }
        }

        if( !(str = gConfigParms.getValueAsString("RIPPLE_BIT_CROSSINGS")).empty() )
        {
            _lcuStartStopCrossings = strtoul(str.c_str(), NULL, 10);
            if(DebugLevel & 0x0001)
            {
                CTILOG_DEBUG(dout, "Configuration Parameter RIPPLE_BIT_CROSSINGS found       : " << str);
            }
        }

        if( !(str = gConfigParms.getValueAsString("RIPPLE_DUTY_CYCLE_PERCENT")).empty() )
        {
            _lcuDutyCyclePercent = strtoul(str.c_str(), NULL, 10);
            if(DebugLevel & 0x0001)
            {
                CTILOG_DEBUG(dout, "Configuration Parameter RIPPLE_DUTY_CYCLE_PERCENT found  : " << str);
            }
        }

        if( !(str = gConfigParms.getValueAsString("RIPPLE_SLOW_SCAN_DELAY")).empty() )
        {
            _lcuSlowScanDelay = strtoul(str.c_str(), NULL, 10);
            if(DebugLevel & 0x0001)
            {
                CTILOG_DEBUG(dout, "Configuration Parameter RIPPLE_SLOW_SCAN_DELAY found     : " << str);
            }
        }

        if( gConfigParms.getValueAsString("RIPPLE_OBSERVE_BUSYBIT") == string("FALSE") )
        {
            _lcuObserveBusyBit = false;       // Make us go at maximal speed.
        }

        if( gConfigParms.isTrue("RIPPLE_EXCLUDE_ALL_INJECTORS") )
        {
            _excludeAllLCUs = true;
        }


        CtiDeviceLCU::_lcuGlobalsInit = TRUE;
    }
}

// delete and set to new val.
CtiDeviceLCU& CtiDeviceLCU::setLastControlMessage(const OUTMESS *pOutMessage)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    resetFlags(LCUWASGLOBAL); // This is set whenever a global message is added to non-global devices

    if(_lastControlMessage != NULL)
    {
        delete _lastControlMessage;
        _lastControlMessage = 0;
    }

    if(pOutMessage)
    {
        _lastControlMessage = CTIDBG_new OUTMESS( *pOutMessage );
        _lastCommand = string( _lastControlMessage->Request.CommandStr );
    }

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
    CtiLockGuard<CtiMutex> guard(_classMutex);
    OUTMESS* lcm = _lastControlMessage;
    _lastControlMessage = NULL;
    return lcm;
}

// delete and set NULL
void CtiDeviceLCU::deleteLastControlMessage()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    resetFlags(LCUWASGLOBAL); // This is set whenever a global message is added to non-global devices

    if(_lastControlMessage != NULL)
    {
        delete _lastControlMessage;
        _lastControlMessage = NULL;
    }
    return;
}

CtiTime CtiDeviceLCU::getNextCommandTime() const
{
    return _nextCommandTime;
}

CtiDeviceLCU& CtiDeviceLCU::setNextCommandTime(const CtiTime& aTime)
{
    _nextCommandTime = aTime;
    return *this;
}

CtiTime CtiDeviceLCU::getStageTime() const
{
    return _stageTime;
}

CtiDeviceLCU& CtiDeviceLCU::setStageTime(const CtiTime& aTime)
{
    _stageTime = aTime;
    return *this;
}

UINT CtiDeviceLCU::setFlags( const UINT mask )
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    _lcuFlags |= mask;
    return _lcuFlags;
}
UINT CtiDeviceLCU::resetFlags( const UINT mask )
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    _lcuFlags &= ~(mask);
    return _lcuFlags;
}
UINT CtiDeviceLCU::getFlags()
{
    return _lcuFlags;
}

bool CtiDeviceLCU::isFlagSet(UINT flags) const
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    return(_lcuFlags & flags);
}

bool CtiDeviceLCU::isGlobalLCU() const
{
    return (getAddress() == LCUGLOBAL || getAddress() == MASTERGLOBAL) ? true : false;
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


BOOL CtiDeviceLCU::isStagedUp(const CtiTime &tRef)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);

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
        if(CtiTime() > (getStageTime() + STAGETIME - 30L))
        {
            /*
             *  Staged condition may need reinforcement...
             *
             *  LCUs stage up for STAGETIME seconds (30 minutes by default)
             *  The getStageTime() method returns the time that the last successful
             *  stage command was sent.  We want to err on the side of caution and re-stage
             *  the LCU 30 seconds before it de-stages.
             */
            if(getStageTime() !=PASTDATE )  // This check prevents us from repeating the send operation repeatedly..
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
                    if( i = MasterHeader (MyOutMessage->Buffer.OutMessage + PREIDLEN, MyOutMessage->Remote, MASTERSTAGE, 0) )
                    {
                        delete (MyOutMessage);
                        MyOutMessage = NULL;
                    }

                    if(MyOutMessage != NULL)
                    {
                        CTILOG_INFO(dout, getName() << " setting stage time to PASTDATE");

                        setStageTime( PASTDATE );
                    }
                }
            }
        }
    }

    return(MyOutMessage);
}


ULONG CtiDeviceLCU::lcuRetries()
{
    return _landgRetries;
}

CtiDeviceLCU::CtiLCUType_t   CtiDeviceLCU::getLCUType() const
{
    return _lcuType;
}

/*
 *  This method could stand some attention.  The behavior should be what was, but was that ever correct?  Will lockout happen on an inhibit?
 *  Does the database care about lockout?
 */
void CtiDeviceLCU::verifyControlLockoutState(const INMESS &InMessage)
{
    if(_lcuType == LCU_LANDG || _lcuType == LCU_STANDARD)
    {
        if(((InMessage.Buffer.InMessage[5] & LCULOCKEDOUT) && !(getControlInhibit())) ||
           (!(InMessage.Buffer.InMessage[5] & LCULOCKEDOUT) && (getControlInhibit())))
        {
            /* Database disagrees */
            if(InMessage.Buffer.InMessage[5] & LCULOCKEDOUT)
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
        if(InMessage.Buffer.InMessage[4] & MnA_TESTMODE && !(isFlagSet(LCUWASTRANSMITTING)) )
        {
            CTILOG_INFO(dout, getName() <<" LOCAL MODE is set");

            lcuResetFlagsAndTags();
        }
    }
}

bool CtiDeviceLCU::isLCUAlarmed( const INMESS &InMessage )
{
    bool bAlarmed = false;

    if( _lcuType == LCU_STANDARD )
    {
        if(InMessage.Buffer.InMessage[4] & LCUCHECKBITALARM)
        {
            bAlarmed = true;
        }
    }
    else if( _lcuType == LCU_LANDG )
    {
        if(InMessage.Buffer.InMessage[4] & LCUCHECKBITALARM)
        {
            bAlarmed = true;
        }
        else if(InMessage.Buffer.InMessage[5] & LCUMINORALARM)
        {
            bAlarmed = true;
        }
    }
    else if( _lcuType == LCU_EASTRIVER )
    {
        if(InMessage.Buffer.InMessage[4] & MnA_ANYALARM)
        {
            dumpStatus( InMessage.Buffer.InMessage[4], InMessage.Buffer.InMessage[5] );
            bAlarmed = true;
        }
    }

    return bAlarmed;
}

CtiMutex& CtiDeviceLCU::getLCUExclusionMux()
{
    return _lcuExclusionMux;
}

/*
 *   This is a porter side decode used only when the OutMessage sent was RIPPLE'd and a control!
 *   It makes him pound the LCU into submission!
 */
void CtiDeviceLCU::lcuFastScanDecode(OUTMESS *&OutMessage, const INMESS &InMessage, CtiLCUResult_t &resultCode, bool globalControlAvailable, CtiMessageList  &vgList)
{
    CtiTime now;

    // Pretend for the simulated ports!
    if(InMessage.ErrorCode == ClientErrors::PortSimulated)
    {
        if(getNextCommandTime() > now)
        {
            if(!(now.seconds() % 5))
            {
                CTILOG_INFO(dout, "** SIMULATED ** "<< getName() <<" \"BUSY\" scanning for \"NOT-BUSY\"");
            }
            setFlags( LCUWASTRANSMITTING );
            resultCode = eLCUFastScan;
            Sleep(250);
        }
        else
        {
            CTILOG_INFO(dout, "** SIMULATED ** "<< getName() <<" HAS COMPLETED AND GONE IDLE!");

            resetFlags(LCUTRANSMITSENT | LCUWASTRANSMITTING);
            resultCode = eLCUDeviceControlComplete;
        }
    }

    if(InMessage.InLength > 0)
    {
        if( InMessage.Buffer.InMessage[0] == 0x01 )
        {
            switch( InMessage.Buffer.InMessage[2] )
            {
            case MASTERSCANALL:
            case MASTERSCANINT:
                {
                    vgList.push_back( lcuDecodeStatus(InMessage) );
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

                        if(InMessage.Buffer.InMessage[4] & LCUBUSYTRANSMITTING)       // Does the LCU indicate that he is busy
                        {
                            if( !(isInhibited()) )
                            {
                                setFlags( LCUWASTRANSMITTING );

                                CTILOG_INFO(dout, getName() << " \"BUSY\" scanning for \"NOT-BUSY\"");
                            }
                            else
                            {
                                CTILOG_WARN(dout, "Inhibited LCU \" "<< getName() <<" just reported as busy");
                            }
                        }     // Busy Transmitting
                        else
                        {     // NOT Busy.
                            if( isLCULockedOut( InMessage ) )  /* Check if the lockout flag is set */
                            {
                                if(getLastControlMessage() != NULL)             /* Check if this was sent just to us ... */
                                {
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
                                CTILOG_INFO(dout, getName() <<" HAS COMPLETED AND GONE IDLE!");

                                /*
                                 *  This LCUWASTRANSMITTING bit is set only if the result decode was processed
                                 *  (at least) once and the LCU responded with a busy bit status indication!
                                 *
                                 *  If we get to this point we must assume that this LCU completed his control command
                                 */

                                resetFlags(LCUTRANSMITSENT | LCUWASTRANSMITTING);
                                resultCode = eLCUDeviceControlComplete;
                            }
                            else if(getLastControlMessage() != NULL)
                            {
                                if(getLCUType() == LCU_STANDARD || getLCUType() == LCU_T3026)
                                {
                                    if( !isExecutionProhibited() )
                                    {
                                        CTILOG_INFO(dout, getName() <<" HAS CLOCKED OUT ALL BITS!");

                                        // Just let it all end...
                                        resultCode = eLCUDeviceControlComplete;
                                    }
                                }
                                else
                                {
                                    resultCode = eLCUNotBusyNeverTransmitted;

                                    // This message resulted from a global message, we are doing global syncs, and our scan time is not yet up.
                                    if( (getFlags() & LCUWASGLOBAL) && gConfigParms.isTrue("RIPPLE_LCU_GLOBAL_SYNCHRONIZE")
                                        && (getNextCommandTime() > now) )
                                    {
                                        resultCode = eLCUFastScan;
                                    }
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
            CTILOG_ERROR(dout, "Message is not a proper MASTERCOM reply");
        }
    }
}


bool CtiDeviceLCU::isLCULockedOut( ) const
{
    return (_lockedOut || getControlInhibit());
}

bool CtiDeviceLCU::isLCULockedOut( const INMESS &InMessage )
{
    if( _lcuType == LCU_LANDG || _lcuType == LCU_STANDARD )
    {
        if(InMessage.Buffer.InMessage[5] & LCULOCKEDOUT)
        {
            CTILOG_INFO(dout, "Local Mode is set on the LCU! " << getName());

            _lockedOut = true;
        }
        else
        {
            _lockedOut = false;
        }
    }
    else if( _lcuType == LCU_EASTRIVER )
    {
        if(InMessage.Buffer.InMessage[4] & MnA_TESTMODE && !isFlagSet(LCUWASTRANSMITTING))
        {
            CTILOG_INFO(dout, "Local (MnA_TESTMODE) Mode is set on the LCU! "<< getName());

            _lockedOut = true;
        }
        else
        {
            _lockedOut = false;
        }
    }

    return (_lockedOut || getControlInhibit());
}

CtiTime CtiDeviceLCU::selectCompletionTime() const
{
    return Inherited::selectCompletionTime();
}

void CtiDeviceLCU::lcuResetFlagsAndTags()
{
    resetFlags(LCUTRANSMITSENT | LCUWASTRANSMITTING);
    deleteLastControlMessage();
    setNumberStarted( 0 );
    removeInfiniteProhibit(getID());        // Do not block yourself Mr. LCU.
    setExecuting(false);
}


void CtiDeviceLCU::dumpStatus(BYTE Byte4, BYTE Byte5)
{
    switch(getLCUType())
    {
    case (LCU_STANDARD):
        {
            // FIXME the right log?
            CTILOG_ERROR(dout, getName() <<" unexpected lcu type LCU_STANDARD");

            break;
        }
    case (LCU_LANDG):
        {
            // FIXME the right log?
            CTILOG_ERROR(dout, getName() <<" unexpected lcu type LCU_LANDG");

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

            CTILOG_INFO(dout, getName() << " Status bytes = 0x"<< hex << setfill('0') << setw(2) << (int)Byte4 <<" 0x"<< setw(2) << (int)Byte5 <<
                   ((Byte4 & MnA_TESTMODE)    ? "\nMnA_TESTMODE SET"     : "" ) <<
                   ((Byte4 & MnA_ANYALARM)    ? "\nMnA_ANYALARM SET"     : "" ) <<
                   ((Byte4 & MnA_BUSY)        ? "\nMnA_BUSY SET"         : "" ) <<
                   ((Byte4 & MnA_STANDALONE)  ? "\nMnA_STANDALONE SET"   : "" ) <<
                   ((Byte4 & MnA_RESET)       ? "\nMnA_RESET SET"        : "" ) <<
                   ((Byte5 & MnA_BITERROR)    ? "\nMnA_BITERROR SET"     : "" ) <<
                   ((Byte5 & MnA_INJDISABLED) ? "\nMnA_INJDISABLED SET"  : "" ) <<
                   ((Byte5 & MnA_INJSTAGED)   ? "\nMnA_INJSTAGED SET"    : "" ) <<
                   ((Byte5 & MnA_COUPLING_AL) ? "\nMnA_COUPLING_AL SET"  : "" ) <<
                   ((Byte5 & MnA_PRECHARGE_AL)? "\nMnA_PRECHARGE_AL SET" : "" ) <<
                   ((Byte5 & MnA_TRANSMIT_AL) ? "\nMnA_TRANSMIT_AL SET"  : "" ) <<
                   ((Byte5 & MnA_OVERCURR_AL) ? "\nMnA_OVERCURR_AL SET"  : "" ) <<
                   ((Byte5 & MnA_PHASELOSS_AL)? "\nMnA_PHASELOSS_AL SET" : "" )
                   );

            break;
        }
    default:
        {
            CTILOG_ERROR(dout, getName() << " unexpected LCU Type ("<< getLCUType() <<")");

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
    CtiTime now;                             // Current Time.

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
                CTILOG_INFO(dout, "DUTY CYCLE PAUSE:  Message would have contributed "<< honkcntr <<" bits, and "<< txtime <<" seconds. Cannot transmit because "<< currenttally + txtime <<" exceeds the allotted " << dutycycleallotment <<" seconds");
            }
        }
        else
        {
            // We can GO!  add us into the tally!
            _honktime[currentminute].second = _honktime[currentminute].second + txtime;

             CTILOG_INFO(dout, "Message is "<< honkcntr <<" bits and "<< txtime <<" seconds, totalling "<< currenttally + txtime <<" of an allotted "<< dutycycleallotment <<" seconds");
        }
    }

    return bStatus;
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
        if( ciStringEqual(gConfigParms.getValueAsString("LCU_PROTOCOLWRAP"),"idlc") )
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
INT CtiDeviceLCU::lcuLockout(OUTMESS *&OutMessage, bool lockout)
{
    INT status = ClientErrors::None;
    USHORT cmd = (lockout ? MASTERLOCKOUTSET : MASTERLOCKOUTRESET);

    /* Load the forced scan message */
    if( status = MasterHeader(OutMessage->Buffer.OutMessage + PREIDLEN, (USHORT)getAddress(), cmd, 0) )
        return(status);

    /* Load all the other stuff that is needed */
    OverrideOutMessagePriority( OutMessage, MAXPRIORITY - 2 );
    populateRemoteOutMessage(*OutMessage);
    OutMessage->OutLength       = 4;
    OutMessage->InLength        = -1;

    return(status);
}

//make up for the old way of doing mpcpointset and clear
//ecs 12/10/2004
CtiPointDataMsg* CtiDeviceLCU::getPointSet( int status )
{
    CtiPointSPtr     pPoint;
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
    }

    return pData;
}

CtiPointDataMsg* CtiDeviceLCU::getPointClear( int status )
{
    CtiPointSPtr pPoint;
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

void CtiDeviceLCU::resetForScan(const CtiScanRate_t scantype)
{
    if(isScanFlagSet(scantype))
    {
        resetScanFlag(scantype);
    }

    switch(scantype)
    {
    case ScanRateAccum:
        {
            if(isScanFlagSet(ScanFreezePending))
            {
                resetScanFlag(ScanFreezePending);
                setScanFlag(ScanFreezeFailed);
            }

            if(isScanFlagSet(ScanResetting))
            {
                resetScanFlag(ScanResetting);
                setScanFlag(ScanResetFailed);
            }
            break;
        }
    }
}

