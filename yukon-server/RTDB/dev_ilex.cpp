#include "precompiled.h"

#include "dsm2.h"
#include "porter.h"

#include "dev_ilex.h"
#include "pt_base.h"
#include "pt_status.h"
#include "connection.h"

#include "pt_status.h"
#include "pt_analog.h"
#include "pt_accum.h"

#include "logger.h"
#include "msg_pcreturn.h"
#include "msg_signal.h"
#include "msg_pdata.h"
#include "msg_cmd.h"
#include "msg_lmcontrolhistory.h"
#include "cmdparse.h"

#include "dlldefs.h"
#include "utility.h"

using namespace std;

CtiDeviceILEX::CtiDeviceILEX() :
    _freezeNumber(0),
    _sequence(0)
{}

INT CtiDeviceILEX::header(PBYTE  Header,          /* Pointer to message */
                          USHORT Function,        /* Function code */
                          USHORT SubFunction1,    /* High order sub function code */
                          USHORT SubFunction2)    /* Low order sub function code */
{
    Header[0] = (Function & 0x0007);
    Header[0] |= LOBYTE ((getAddress() << 5) & 0xe0);
    if(SubFunction1) Header[0] |= 0x10;
    if(SubFunction2) Header[0] |= 0x08;
    Header[1] = LOBYTE (getAddress() >> 3);

    return ClientErrors::None;
}

YukonError_t CtiDeviceILEX::AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;

    if(OutMessage != NULL)
    {
        /* Load the freeze message */
        header(OutMessage->Buffer.OutMessage + PREIDLEN, ILEXFREEZE, 0, 1);

        /* Load all the other stuff that is needed */
        populateRemoteOutMessage(*OutMessage);
        OutMessage->OutLength             = 3;
        OutMessage->InLength              = -1;
        OutMessage->Retry = 3;  //  override

        OutMessage->Buffer.OutMessage[9]  = setFreezeNumber((BYTE)getLastFreezeNumber()).getFreezeNumber();
        OverrideOutMessagePriority( OutMessage, (MAXPRIORITY - 3) );

        setScanFlag(ScanFreezePending);                     // This is an outbound freeze request.  We need a response to knowthey are frozen.
        setScanFlag(ScanRateIntegrity);                         // We are an integrity scan (equiv. anyway).  Data must be propagated.

        outList.push_back(OutMessage);
        OutMessage = NULL;
    }

    return status;
}

YukonError_t CtiDeviceILEX::exceptionScan(OUTMESS *&OutMessage, INT ScanPriority, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    if(OutMessage != NULL)
    {
        /* Load the forced scan message */
        header(OutMessage->Buffer.OutMessage + PREIDLEN, ILEXSCAN, !getIlexSequenceNumber(), EXCEPTION_SCAN);

        /* Load all the other stuff that is needed */
        populateRemoteOutMessage(*OutMessage);
        OutMessage->Priority              = (UCHAR)(ScanPriority);
        OutMessage->OutLength             = 2;
        OutMessage->InLength              = -1;

        outList.push_back(OutMessage);
        OutMessage = NULL;
    }

    return status;
}

YukonError_t CtiDeviceILEX::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    return IntegrityScan(pReq,parse,OutMessage,vgList,retList,outList,ScanPriority);
    // return exceptionScan(OutMessage,ScanPriority,outList);
}

YukonError_t CtiDeviceILEX::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;

    if(OutMessage != NULL)
    {
        /* Load the forced scan message */
        // Always send one so that any subsequent "intermediate" message
        // goes out as zero!  This keeps message repeats down!
        setIlexSequenceNumber(1);
        header(OutMessage->Buffer.OutMessage + PREIDLEN, ILEXSCAN, getIlexSequenceNumber(), FORCED_SCAN);

        /* Load all the other stuff that is needed */
        populateRemoteOutMessage(*OutMessage);
        EstablishOutMessagePriority( OutMessage, ScanPriority );
        OutMessage->OutLength             = 2;
        OutMessage->InLength              = -1;

        outList.push_back(OutMessage);
        OutMessage = NULL;
    }

    return status;
}


YukonError_t CtiDeviceILEX::ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList   &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t    status = ClientErrors::None;
    CtiPointSPtr    PointRecord;
    CtiPointNumericSPtr NumericPoint;
    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointAccumulatorSPtr pAccumPoint;

    CHAR  tStr[128];
    INT AIPointOffset;

    CtiPointDataMsg *pData = NULL;
    CtiConnection   *Conn = ((CtiConnection*)InMessage.Return.Connection);
    OUTMESS         *OutMessage = NULL;


    /* Misc. definitions */
    ULONG i, j;

    /* Variables for decoding ILEX Messages */
    USHORT  Offset;
    USHORT  NumAnalogs = 0;
    USHORT  NumAccum = 0;
    USHORT  NumStatusGroups = 0;
    USHORT  NumSOE = 0;
    SHORT   Value;
    USHORT  UValue;
    USHORT  StartAccum;
    USHORT  EndAccum;
    USHORT  StartStatus;
    FLOAT   PartHour;
    USHORT  State1;
    USHORT  State2;
    USHORT  State3;
    FLOAT   PValue;

    try
    {
        ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
        ReturnMsg->setUserMessageId(InMessage.Return.UserID);

        unsigned char firstByte = InMessage.Buffer.InMessage[0];

        /* decode whatever message this is */
        switch(firstByte & 0x07)
        {
        case ILEXFREEZE:
            {
                if(isScanFlagSet(ScanFreezePending))
                {
                    resetScanFlag(ScanFreezePending);
                    setScanFlag(ScanFrozen);
                    setPrevFreezeTime(getLastFreezeTime());
                    setLastFreezeTime( CtiTime(InMessage.Time) );
                    setPrevFreezeNumber( getLastFreezeNumber() );
                    setLastFreezeNumber(InMessage.Buffer.InMessage[2]);
                    resetScanFlag(ScanFreezeFailed);

                    /* then force a scan */
                    OutMessage = new OUTMESS;

                    InEchoToOut(InMessage, *OutMessage);

                    CtiCommandParser parse(InMessage.Return.CommandStr);

                    if( i = IntegrityScan (NULL, parse, OutMessage, vgList, retList, outList, MAXPRIORITY - 4) )
                    {
                        ReportError ((USHORT)i); /* Send Error to logger */
                    }
                    else
                    {
                        setScanFlag(ScanRateGeneral);
                    }
                }
                else
                {
                    CTILOG_ERROR(dout, "Throwing away unexpected freeze response");

                    setScanFlag(ScanFreezeFailed);   // FIX FIX FIX 090799 CGP ?????
                    /* message for screwed up freeze */
                }
                break;
            }
        case ILEXSCAN:
            {
                if(isScanFlagSet(ScanRateGeneral))
                {
                    resetScanFlag(ScanRateGeneral);

                    /* ILEX doesn't seem to keep the ACK/NACK bit straight, so force it */
                    firstByte |= 0x10;

                }
                else
                {
                    /* Something screwed up message goes here */
                    CTILOG_ERROR(dout, "Unexpected scan flag is not set");

                    break;
                }

                // !!! FALL THROUGH FALL THROUGH FALL THROUGH !!!
            }
        case ILEXSCANPARTIAL:
            {
                if(getDebugLevel() & DEBUGLEVEL_ILEX_PROTOCOL)
                {
                    std::ostringstream logMessage;

                    logMessage << endl <<"First byte:";
                    logMessage << endl << setfill('0') << hex << setw(2) << (int)firstByte;
                    logMessage << endl <<"Ilex Data:";

                    for(i=0; i < 64; i++)
                    {
                        if(i && !(i % 10)) logMessage << endl;
                        logMessage << setw(2) << (int)InMessage.Buffer.InMessage[i] << " ";
                    }

                    logMessage << endl <<"Ilex Data Complete";

                    CTILOG_DEBUG(dout, logMessage);
                }

                if((firstByte & 0x07) == ILEXSCANPARTIAL)
                {
                    /* do an exception scan */
                    OutMessage = new OUTMESS;

                    InEchoToOut(InMessage, *OutMessage);

                    setIlexSequenceNumber( firstByte & 0x10 );

                    if( i = exceptionScan(OutMessage, MAXPRIORITY - 4, outList) )
                    {
                        ReportError ((USHORT)i); /* Send Error to logger */
                    }
                    else
                    {
                        setScanFlag(ScanRateGeneral);
                    }
                }

                if(InMessage.Buffer.InMessage[2])
                {
                    Offset      = 3;
                    NumAnalogs  = InMessage.Buffer.InMessage[2];
                    NumStatusGroups   = 0;
                    NumAccum    = 0;
                    NumSOE      = 0;
                }
                else
                {
                    /* How many of each type is here? */
                    Offset      = 5;
                    NumAnalogs  = InMessage.Buffer.InMessage[3];
                    NumStatusGroups   = InMessage.Buffer.InMessage[4] & 0x3f;

                    if( InMessage.Buffer.InMessage[4] & 0x40 )
                    {
                        Offset  += 1;
                        NumAccum = InMessage.Buffer.InMessage[5];
                        if( InMessage.Buffer.InMessage[4] & 0x80 )
                        {
                            Offset      += 2;
                            NumAnalogs  += InMessage.Buffer.InMessage[6];
                            NumSOE      = InMessage.Buffer.InMessage[7];
                        }
                        else
                            NumSOE      = 0;
                    }
                    else
                    {
                        NumAccum        = 0;
                        if( InMessage.Buffer.InMessage[4] & 0x80 )
                        {
                            Offset      += 2;
                            NumAnalogs  += InMessage.Buffer.InMessage[5];
                            NumSOE      = InMessage.Buffer.InMessage[6];
                        }
                        else
                            NumSOE      = 0;
                    }
                }

                /* now check if we need to plug accums because of missed freeze */
                if(((isScanFlagSet(ScanFrozen)) || (isScanFlagSet(ScanFreezeFailed))) && (NumAccum == 0))
                {
                    /* make sure this guy is marked as a bad freeze */
                    setLastFreezeNumber( 0 );

                    resetScanFlag(ScanFrozen);
                    resetScanFlag(ScanFreezeFailed);
                }

                /* Now process them in order */
                if(NumStatusGroups)
                {
                    for(i = 0; i < NumStatusGroups; i++)
                    {
                        //  offset is nonzero, so we don't need to use firstByte
                        StartStatus = InMessage.Buffer.InMessage[Offset] * 16;

                        if(getDebugLevel() & DEBUGLEVEL_ILEX_PROTOCOL)
                        {
                            std::ostringstream logMessage;

                            logMessage <<"Indication Group Number "<< (int)InMessage.Buffer.InMessage[Offset] <<": Start status offset = "<< StartStatus;
                            logMessage << endl << hex << setfill('0');

                            for(j = 0; j < 7; j++)
                            {
                                logMessage <<"0x"<< setw(2) << (int)InMessage.Buffer.InMessage[Offset + j] <<" ";
                            }

                            CTILOG_DEBUG(dout, logMessage);
                        }

                        for(j = 1; j <= 16; j++)
                        {
                            /* Get the Status Record */
                            if( PointRecord = getDevicePointOffsetTypeEqual( StartStatus + j, StatusPointType ))
                            {
                                /****** NOTE 3-state's are ignored...
                                 * a status is a 2 state at this point, period.  Work needs to be done here if that changes.
                                 */

                                /* Strip out bits for this status */
                                /* Note:    State2 and State3 are not */
                                /* used right now but may be in the */
                                /* future so I computed them (Oh Hell! */
                                /* more usecs down the drain */
                                if(j <= 8)
                                {
                                    State1 = InMessage.Buffer.InMessage[Offset + 1] >> (j - 1) & 0x0001;
                                    State2 = InMessage.Buffer.InMessage[Offset + 3] >> (j - 1) & 0x0001;
                                    State3 = InMessage.Buffer.InMessage[Offset + 5] >> (j - 1) & 0x0001;
                                }
                                else
                                {
                                    State1 = InMessage.Buffer.InMessage[Offset + 2] >> (j - 9) & 0x0001;
                                    State2 = InMessage.Buffer.InMessage[Offset + 4] >> (j - 9) & 0x0001;
                                    State3 = InMessage.Buffer.InMessage[Offset + 6] >> (j - 9) & 0x0001;
                                }

                                /* Update the records */

                                if(State1)
                                    Value = STATE_CLOSED;
                                else
                                    Value = STATE_OPENED;

                                PValue = (FLOAT) Value;

                                _snprintf(tStr, 127, "%s point %s = %s", getName().c_str(), PointRecord->getName().c_str(), ((PValue == STATE_OPENED) ? "OPENED" : "CLOSED") );

                                pData = CTIDBG_new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, StatusPointType, tStr);

                                if(pData != NULL)
                                {
                                    ReturnMsg->PointData().push_back(pData);
                                    pData = NULL;  // We just put it on the list...
                                }
                            }
                        }
                        Offset += 7;
                    }
                }

                if(NumAccum)
                {
                    if((firstByte & 0x07) == ILEXSCAN)
                    {
                        if(isScanFlagSet(ScanFrozen) || isScanFlagSet(ScanFreezeFailed))
                        {
                            if( getScanRate(ScanRateGeneral) < getScanRate(ScanRateAccum) )
                            {
                                /* Force a continuation to clean that mother out */
                                OutMessage = new OUTMESS;

                                InEchoToOut(InMessage, *OutMessage);

                                // CtiCommandParser parse(InMessage.Return.CommandStr);
                                setIlexSequenceNumber( firstByte & 0x10 );

                                if( i = exceptionScan(OutMessage, MAXPRIORITY - 4, outList) )
                                {
                                    ReportError ((USHORT)i); /* Send Error to logger */
                                }
                                else
                                {
                                    setScanFlag(ScanRateGeneral);
                                }
                            }
                        }
                    }

                    /* Check the freeze number */
                    if(
                      ((getLastFreezeNumber() != InMessage.Buffer.InMessage[Offset]) && isScanFlagSet(ScanFrozen)) ||
                      (isScanFlagSet(ScanFreezeFailed) && getPrevFreezeNumber() + 1 != 0 && getPrevFreezeNumber() + 1 != InMessage.Buffer.InMessage[Offset]) ||
                      (isScanFlagSet(ScanFreezeFailed) && getPrevFreezeNumber() + 1 == 0 && getPrevFreezeNumber() + 2 != InMessage.Buffer.InMessage[Offset])
                      )
                    {
                        /* Process wrong freeze number */
                        Offset += (2 + (NumAccum * 2));

                        /* make sure this guy is marked as a bad freeze */
                        setLastFreezeNumber( 0 );

                        CTILOG_ERROR(dout, "A.C.H. Bad freeze number!");
                    }
                    else
                    {
                        /* mark the freeze as valid */
                        if(isScanFlagSet(ScanFreezeFailed))
                            setLastFreezeNumber(InMessage.Buffer.InMessage[Offset]);

                        /* Calculate the part of an hour involved here */
                        PartHour = (FLOAT) (getLastFreezeTime().seconds() - getPrevFreezeTime().seconds());
                        PartHour /= (3600.0);

                        /* Loop through the accumulator records */
                        StartAccum = InMessage.Buffer.InMessage[Offset + 1];
                        EndAccum = StartAccum + NumAccum;
                        Offset += 2;

                        CTILOG_INFO(dout, "Decoding accumulators from "<< (int)(StartAccum + 1) <<" to "<< EndAccum);

                        for(AIPointOffset = StartAccum + 1; AIPointOffset <= EndAccum; AIPointOffset++)
                        {
                            // get the current pulse count
                            ULONG curPulseValue = MAKEUSHORT(InMessage.Buffer.InMessage[Offset], InMessage.Buffer.InMessage[Offset + 1]);

                            if(pAccumPoint = boost::static_pointer_cast<CtiPointAccumulator>(getDevicePointOffsetTypeEqual(AIPointOffset, DemandAccumulatorPointType)))
                            {
                                /* Copy the pulses */
                                pAccumPoint->getPointHistory().setPreviousPulseCount(pAccumPoint->getPointHistory().getPresentPulseCount());
                                pAccumPoint->getPointHistory().setPresentPulseCount(curPulseValue);

                                if(!(getPrevFreezeNumber()) || !(getLastFreezeNumber()))
                                {
                                    CTILOG_WARN(dout, getName() <<" doesn't appear to have had two demand accumulator scans. Waiting for a second scan");
                                }
                                else
                                {
                                    try
                                    {
                                        /* Calculate the number of pulses */
                                        if(pAccumPoint->getPointHistory().getPresentPulseCount() < pAccumPoint->getPointHistory().getPreviousPulseCount())
                                            UValue = 0xffff - pAccumPoint->getPointHistory().getPreviousPulseCount() + pAccumPoint->getPointHistory().getPresentPulseCount();  /* Rollover */
                                        else
                                            UValue = pAccumPoint->getPointHistory().getPresentPulseCount() - pAccumPoint->getPointHistory().getPreviousPulseCount();

                                        /* Calculate in units/hour */
                                        PValue = (FLOAT) UValue * pAccumPoint->getMultiplier();
                                        /* to convert to units */
                                        PValue /= PartHour;

                                        /* Apply offset */
                                        PValue += pAccumPoint->getDataOffset();

                                        if(getDebugLevel() & DEBUGLEVEL_ILEX_PROTOCOL)
                                        {
                                            CTILOG_DEBUG(dout, "Demand Accum offset "<< AIPointOffset <<" = "<< PValue <<
                                                    endl <<"UValue    = "<< UValue <<
                                                    endl <<"PartHour  = "<< PartHour <<
                                                    endl <<"PrevPulse = "<< pAccumPoint->getPointHistory().getPreviousPulseCount() <<
                                                    endl <<"CurrPulse = "<< pAccumPoint->getPointHistory().getPresentPulseCount()
                                                    );
                                        }

                                        _snprintf(tStr, 127, "%s point %s = %f", getName().c_str(), pAccumPoint->getName().c_str(), PValue);

                                        pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, DemandAccumulatorPointType, tStr);
                                        if(pData != NULL)
                                        {
                                            ReturnMsg->PointData().push_back(pData);
                                            pData = NULL;  // We just put it on the list...
                                        }
                                    }
                                    catch(...)
                                    {
                                        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                                    }
                                }
                            }
                            else
                            {
                                if(getDebugLevel() & DEBUGLEVEL_ILEX_PROTOCOL)
                                {
                                    CTILOG_DEBUG(dout, "No point for DACC offset "<< AIPointOffset);
                                }
                            }

                            if(pAccumPoint = boost::static_pointer_cast<CtiPointAccumulator>(getDevicePointOffsetTypeEqual(AIPointOffset, PulseAccumulatorPointType)))
                            {
                                /* Copy the pulses */
                                pAccumPoint->getPointHistory().setPreviousPulseCount(pAccumPoint->getPointHistory().getPresentPulseCount());
                                pAccumPoint->getPointHistory().setPresentPulseCount(curPulseValue);

                                if(pAccumPoint->getPointHistory().getPresentPulseCount() < pAccumPoint->getPointHistory().getPreviousPulseCount())
                                    UValue = 0xffff - pAccumPoint->getPointHistory().getPreviousPulseCount() + pAccumPoint->getPointHistory().getPresentPulseCount();  /* Rollover */
                                else
                                    UValue = pAccumPoint->getPointHistory().getPresentPulseCount() - pAccumPoint->getPointHistory().getPreviousPulseCount();


                                /* Calculate in units/hour */
                                PValue = (FLOAT) UValue * pAccumPoint->getMultiplier();

                                /* Apply offset */
                                PValue += pAccumPoint->getDataOffset();

                                if(getDebugLevel() & DEBUGLEVEL_ILEX_PROTOCOL)
                                {
                                    CTILOG_DEBUG(dout, "Pulse Accum offset "<< AIPointOffset <<" = "<< PValue);
                                }

                                _snprintf(tStr, 127, "%s point %s = %f", getName().c_str(), pAccumPoint->getName().c_str(), PValue);

                                pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, PulseAccumulatorPointType, tStr);

                                if(pData != NULL)
                                {
                                    ReturnMsg->PointData().push_back(pData);
                                    pData = NULL;  // We just put it on the list...
                                }
                            }
                            else
                            {
                                if(getDebugLevel() & DEBUGLEVEL_ILEX_PROTOCOL)
                                {
                                    CTILOG_DEBUG(dout, "No point for PACC offset "<< AIPointOffset);
                                }
                            }

                            Offset += 2;
                        }
                    }
                    resetScanFlag(ScanFrozen);
                    resetScanFlag(ScanFreezeFailed);
                }

                if(NumSOE)
                {
                    /* No use for SOE reports at this time */
                    Offset += (NumSOE * 5);
                }

                if(NumAnalogs)
                {
                    if(getDebugLevel() & DEBUGLEVEL_ILEX_PROTOCOL)
                    {
                        CTILOG_DEBUG(dout, "About to decode "<< NumAnalogs <<" analogs");
                    }
                    for(i = 0; i < NumAnalogs; i++)
                    {
                        if(i & 0x01)
                        {
                            AIPointOffset = InMessage.Buffer.InMessage[Offset + 4] + 1;
                            Value = MAKEUSHORT (InMessage.Buffer.InMessage[Offset + 3], InMessage.Buffer.InMessage[Offset + 2] & 0x0f);
                            Offset += 5;
                        }
                        else
                        {
                            AIPointOffset = InMessage.Buffer.InMessage[Offset] + 1;
                            Value = MAKEUSHORT (InMessage.Buffer.InMessage[Offset + 1], (InMessage.Buffer.InMessage[Offset + 2] & 0xf0) >> 4);
                        }

                        Value = Value << 4;
                        Value = Value / 16;


                        if(getDebugLevel() & DEBUGLEVEL_ILEX_PROTOCOL)
                        {
                            CTILOG_DEBUG(dout, "AI offset " << AIPointOffset << " = " << Value);
                        }

                        if(NumericPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(AIPointOffset, AnalogPointType)))
                        {
                            PValue = NumericPoint->computeValueForUOM( Value );

                            _snprintf(tStr, 127, "%s point %s = %f", getName().c_str(), NumericPoint->getName().c_str(), PValue );

                            pData = CTIDBG_new CtiPointDataMsg(NumericPoint->getPointID(), PValue, NormalQuality, AnalogPointType, tStr);

                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().push_back(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }
                    }       /* end of for */
                }           /* end of analogs */

                break;

            }
        default:
        case ILEXNODATA:
            {
                if(firstByte & 0x08)
                {
                    /* update the accumulator criteria for this Remote */
                    setLastFreezeTime(CtiTime(YUKONEOT));
                    setLastFreezeNumber( 0 );

                    /* now force another scan */
                    OutMessage = new OUTMESS;

                    InEchoToOut(InMessage, *OutMessage);

                    CtiCommandParser parse(InMessage.Return.CommandStr);
                    if( i = IntegrityScan(NULL, parse, OutMessage, vgList, retList, outList, MAXPRIORITY - 3) )
                    {
                        if(getDebugLevel() & DEBUGLEVEL_ILEX_PROTOCOL)
                        {
                            CTILOG_DEBUG(dout, "IntegrityScan return error "<< i);
                        }
                        ReportError ((USHORT)i); /* Send Error to logger */
                    }
                    else
                    {
                        setScanFlag(ScanRateGeneral);
                    }
                }
                else if(isScanFlagSet(ScanRateGeneral))
                {
                    resetScanFlag(ScanRateGeneral);

                    // 04302002 CGP This doesn't seem to be used anywhere around here...
                    // DeviceRecord->LastFullScan = TimeB->time;
                }
                else
                {
                    /* put a something fishy message here */
                    CTILOG_ERROR(dout, "Unexpected firstByte = "<< firstByte);
                }
                break;
            }               /* end of default and reset conditions */
        }                   /* End of switch */


        if(ReturnMsg != NULL)
        {
            if(!(ReturnMsg->ResultString().empty()) || ReturnMsg->getData().size() > 0)
            {
                retList.push_back( ReturnMsg );
            }
            else
            {
                delete ReturnMsg;
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return status;
}

YukonError_t CtiDeviceILEX::ErrorDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList)
{
    YukonError_t status = ClientErrors::None;

    CTILOG_INFO(dout, "ErrorDecode for device "<< getName());

    resetForScan(desolveScanRateType(InMessage.Return.CommandStr));

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

YukonError_t CtiDeviceILEX::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    /*  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*, INT ScanPriority)
     *   (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */


    switch(parse.getCommand())
    {
    case LoopbackRequest:
        {
            status = exceptionScan(OutMessage, 13, outList);
            break;
        }
    case ScanRequest:
        {
            status = executeScan(pReq, parse, OutMessage, vgList, retList, outList);
            break;
        }
    case ControlRequest:
        {
            status = executeControl(pReq, parse, OutMessage, vgList, retList, outList);
            break;
        }
    case GetStatusRequest:
    case GetValueRequest:
    case PutValueRequest:
    case PutStatusRequest:
    case GetConfigRequest:
    case PutConfigRequest:
    default:
        {
            CTILOG_ERROR(dout, "No method found");

            status = ClientErrors::NoMethodForExecuteRequest;
            /* Set the error value in the base class. */
            // FIX FIX FIX 092999
            retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                                    string(OutMessage->Request.CommandStr),
                                                    string("Ilex Devices do not support this command (yet?)"),
                                                    status,
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


    return status;
}

YukonError_t CtiDeviceILEX::executeControl(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    if(!isInhibited())
    {
        CtiPointStatusSPtr ctlPoint;
        INT ctlpt = parse.getiValue("point");
        INT controlState;

        if(ctlpt < 0)
        {
            // Must have provided only a name... Find it the hard way.
            string pname = parse.getsValue("point");
            ctlPoint = boost::static_pointer_cast<CtiPointStatus>(getDevicePointEqualByName(pname));
        }
        else
        {
            CtiPointSPtr point = getDevicePointByID(ctlpt);

            if ( ! point )
            {
                std::string errorMessage = "The specified point is not on device " + getName();
                returnErrorMessage(ClientErrors::PointLookupFailed, OutMessage, retList, errorMessage);

                return ClientErrors::PointLookupFailed;
            }

            ctlPoint = boost::static_pointer_cast<CtiPointStatus>(point);
        }


        if(ctlPoint)
        {
            if( const boost::optional<CtiTablePointStatusControl> controlParameters = ctlPoint->getControlParameters() )
            {
                INT ctloffset = controlParameters->getControlOffset();

                if( ! controlParameters->isControlInhibited() )
                {
                    if(INT_MIN == ctlpt || !(parse.getFlags() & (CMD_FLAG_CTL_CLOSE | CMD_FLAG_CTL_OPEN)))
                    {
                        CTILOG_ERROR(dout, "Poorly formed control message.  Specify select pointid and open or close");
                    }
                    else    // We have all our info available.
                    {
                        OutMessage->Port        = getPortID();
                        OutMessage->Remote      = getAddress();

                        OverrideOutMessagePriority( OutMessage, MAXPRIORITY );

                        OutMessage->TimeOut     = 2;
                        OutMessage->InLength    = -1;
                        OutMessage->EventCode   |= ENCODED | NOWAIT | NORESULT;          // May contain RESULT based upon the incoming OutMessage
                        OutMessage->ReturnNexus = NULL;
                        OutMessage->SaveNexus   = NULL;

                        if(!OutMessage->TargetID) OutMessage->TargetID = getID();
                        if(!OutMessage->DeviceID) OutMessage->DeviceID = getID();

                        OUTMESS *MyOutMessage = CTIDBG_new OUTMESS(*OutMessage);

                        /* This actually takes two messages... a select and an execute */
                        MyOutMessage->OutLength = 4;

                        if( findStringIgnoreCase(parse.getCommandStr(), controlParameters->getStateZeroControl()) )       //  (parse.getFlags() & CMD_FLAG_CTL_OPEN)
                        {
                            controlState = STATEZERO;

                            header(MyOutMessage->Buffer.OutMessage + PREIDLEN, ILEXSBOSELECT, 0, 0);
                            /* set the operation time */
                            MyOutMessage->Buffer.OutMessage[PREIDLEN + ILEXHEADERLEN + 1] = controlParameters->getCloseTime1() / 100;

                        }
                        else if( findStringIgnoreCase(parse.getCommandStr(), controlParameters->getStateOneControl()) )  // (parse.getFlags() & CMD_FLAG_CTL_CLOSE)
                        {
                            controlState = STATEONE;
                            header(MyOutMessage->Buffer.OutMessage + PREIDLEN, ILEXSBOSELECT, 1, 0);
                            /* set the operation time */
                            MyOutMessage->Buffer.OutMessage[PREIDLEN + ILEXHEADERLEN + 1] = controlParameters->getCloseTime2() / 100;
                        }
                        else
                        {
                            delete (MyOutMessage);
                            return ClientErrors::BadState;
                        }

                        /* set the point number */
                        MyOutMessage->Buffer.OutMessage[PREIDLEN + ILEXHEADERLEN] = ctloffset - 1;

                        outList.push_back( MyOutMessage );
                        MyOutMessage = 0;



                        // EXECUTE!!!


                        /* Load all the other stuff that is needed */
                        OutMessage->Sequence = 0;
                        OutMessage->Retry = 0;
                        OutMessage->OutLength = 3;
                        OutMessage->Priority = MAXPRIORITY;

                        /* set up the execute */
                        header (OutMessage->Buffer.OutMessage + PREIDLEN, ILEXSBOEXECUTE, 0, 0);
                        /* set the point number */
                        OutMessage->Buffer.OutMessage[PREIDLEN + ILEXHEADERLEN] = ctloffset - 1;

                        /* Sent the message on to the remote */
                        outList.push_back( OutMessage );
                        OutMessage = 0;

                        CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg ( ctlPoint->getDeviceID(), ctlPoint->getPointID(), controlState, CtiTime(), -1, 100 );

                        hist->setMessagePriority( hist->getMessagePriority() + 1 );
                        vgList.push_back( hist );

                        if(ctlPoint->isPseudoPoint())
                        {
                            // There is no physical point to observe and respect.  We lie to the control point.
                            CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(ctlPoint->getID(), (DOUBLE)controlState, NormalQuality, StatusPointType, string("This point has been controlled"));
                            pData->setUser( pReq->getUser() );
                            vgList.push_back(pData);
                        }

                        retList.push_back( CTIDBG_new CtiReturnMsg(getID(), parse.getCommandStr(), string("Command submitted to port control")) );
                    }
                }
                else
                {
                    CTILOG_WARN(dout, "Control Point "<< ctlPoint->getName() <<" is disabled");
                }
            }
        }
        else
        {
            CTILOG_ERROR(dout, getName() << " Control point " << ctlpt << " does not exist");
        }
    }
    else
    {
        CTILOG_WARN(dout, getName() <<" is disabled");
    }

    return status;
}


BYTE CtiDeviceILEX::getFreezeNumber() const
{
    return _freezeNumber;
}

CtiDeviceILEX& CtiDeviceILEX::setFreezeNumber(BYTE number)
{
    _freezeNumber = number + 1;

    if(!_freezeNumber)
    {
        _freezeNumber = 1;
    }

    return *this;
}

BYTE CtiDeviceILEX::getIlexSequenceNumber() const
{
    return _sequence;
}

CtiDeviceILEX& CtiDeviceILEX::setIlexSequenceNumber(BYTE number)
{
    _sequence = number;
    return *this;
}


