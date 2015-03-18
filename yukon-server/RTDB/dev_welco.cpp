#include "precompiled.h"

#include "cparms.h"
#include "dsm2.h"
#include "dllyukon.h"
#include "porter.h"

#include "dev_welco.h"
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
#include "numstr.h"
#include "cmdparse.h"

#include "dlldefs.h"
#include "utility.h"

#include <sys/timeb.h>

using std::string;
using std::endl;
using std::list;

CtiDeviceWelco::CtiDeviceWelco()
    :   _deadbandsSent(0)
{}

YukonError_t CtiDeviceWelco::AccumulatorScan(CtiRequestMsg *pReq,
                                             CtiCommandParser &parse,
                                             OUTMESS *&OutMessage,
                                             CtiMessageList &vgList,
                                             CtiMessageList &retList,
                                             OutMessageList &outList,
                                             INT ScanPriority)
{
    /*
     *  This is the WelCoFreeze code from the bad old daze.
     */

    YukonError_t status = ClientErrors::None;

    if(OutMessage != NULL)
    {
        {
            /* Load the sectn to scan the demand accumulators */
            OutMessage->Buffer.OutMessage[5] = IDLC_FREEZE | 0x80;
            OutMessage->Buffer.OutMessage[6] = 0;

            /* Load all the other stuff that is needed */
            populateRemoteOutMessage(*OutMessage);
            OutMessage->Buffer.OutMessage[4]  = 0x08;
            EstablishOutMessagePriority( OutMessage, ScanPriority );
            OutMessage->OutLength             = 0;
            OutMessage->InLength              = -1;

            if(OutMessage->Remote == RTUGLOBAL)
            {
                //  override EventCode - no result expected for a global freeze
                OutMessage->EventCode = NORESULT | ENCODED;
            }

            setScanFlag(ScanRateIntegrity);                         // We are an integrity scan (equiv. anyway).  Data must be propagated.
            setScanFlag(ScanFreezePending);
            outList.push_back(OutMessage);

            OutMessage = NULL;
        }
    }


    return status;
}

YukonError_t CtiDeviceWelco::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;

    if(getDeadbandsSent() == false)      // We are currently unsure whether a deadband request has ever been sent.
    {
        incDeadbandsSent();
        status = WelCoDeadBands(OutMessage, outList, ScanPriority);
    }

    if(OutMessage != NULL)
    {
        setScanFlag(ScanRateGeneral);
        WelCoPoll(OutMessage, ScanPriority);

        /* Message is loaded so send it to porter */
        outList.push_back(OutMessage);
        OutMessage = NULL;
    }

    return status;
}


YukonError_t CtiDeviceWelco::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    INT      AIOffset = 0;

    USHORT   AnalogFirst = 0xffff;
    USHORT   AnalogLast = 0;

    USHORT   StatusFirst = 0xffff;
    USHORT   StatusLast = 0;

    USHORT   AccumFirst = 0xffff;
    USHORT   AccumLast = 0;

    YukonError_t status = ClientErrors::None;

    if(OutMessage != NULL)
    {
        std::vector<CtiPointSPtr> points;

        getDevicePoints(points);

        std::vector<CtiPointSPtr>::iterator itr;

        for( itr = points.begin(); itr != points.end(); itr++ )
        {
            CtiPointSPtr PointRecord = *itr;

            switch(PointRecord->getType())
            {
                case StatusPointType:
                {
                    CtiPointStatusSPtr StatusPoint = boost::static_pointer_cast<CtiPointStatus>(PointRecord);

                    if(!StatusPoint->isPseudoPoint() && StatusPoint->getPointOffset() < 2000)
                    {
                        if(StatusPoint->getPointOffset() - 1 > StatusLast)
                        {
                            StatusLast = StatusPoint->getPointOffset() - 1;
                        }

                        if(StatusPoint->getPointOffset() - 1 < StatusFirst)
                        {
                            StatusFirst = StatusPoint->getPointOffset() - 1;
                        }
                    }
                    break;
                }
                case AnalogPointType:
                {
                    CtiPointAnalogSPtr AnalogPoint = boost::static_pointer_cast<CtiPointAnalog>(PointRecord);

                    if(!AnalogPoint->isPseudoPoint())
                    {
                        if(AnalogPoint->getPointOffset() - 1 > AnalogLast)
                        {
                            AnalogLast = AnalogPoint->getPointOffset() - 1;
                        }

                        if(AnalogPoint->getPointOffset() - 1 < AnalogFirst)
                        {
                            AnalogFirst = AnalogPoint->getPointOffset() - 1;
                        }
                    }

                    break;
                }
                case PulseAccumulatorPointType:
                case DemandAccumulatorPointType:
                {
                    CtiPointAccumulatorSPtr AccumPoint = boost::static_pointer_cast<CtiPointAccumulator>(PointRecord);

                    if(!AccumPoint->isPseudoPoint())
                    {
                        if(AccumPoint->getPointOffset() - 1 > AccumLast)
                        {
                            AccumLast = AccumPoint->getPointOffset() - 1;
                        }

                        if(AccumPoint->getPointOffset() - 1 < AccumFirst)
                        {
                            AccumFirst = AccumPoint->getPointOffset() - 1;
                        }
                    }

                    break;
                }
            }
        }


        if(AnalogFirst > AnalogLast)
        {
            AnalogFirst = AnalogLast = 0;
        }

        if(AccumFirst > AccumLast)
        {
            AccumFirst = AccumLast = 0;
        }

        if(StatusFirst > StatusLast)
        {
            StatusFirst = StatusLast = 0;
        }

        if(!useScanFlags() || (isScanFlagSet(ScanFrozen) || isScanFlagSet(ScanFreezeFailed)))
        {
            /*
             *  This is our big hint that the message needs accums to be included!
             */
            OutMessage->Buffer.OutMessage[5] = IDLC_ACCUMDUMP;
            OutMessage->Buffer.OutMessage[6] = 2;
            OutMessage->Buffer.OutMessage[7] = LOBYTE (AccumFirst);
            OutMessage->Buffer.OutMessage[8] = LOBYTE (AccumLast);
            AIOffset = 4;
        }

        /* Load the sectn to scan the stati */
        OutMessage->Buffer.OutMessage[5  + AIOffset] = IDLC_STATUSDUMP;
        OutMessage->Buffer.OutMessage[6  + AIOffset] = 4;
        OutMessage->Buffer.OutMessage[7  + AIOffset] = LOBYTE (StatusFirst);
        OutMessage->Buffer.OutMessage[8  + AIOffset] = HIBYTE (StatusFirst);
        OutMessage->Buffer.OutMessage[9  + AIOffset] = LOBYTE (StatusLast);
        OutMessage->Buffer.OutMessage[10 + AIOffset] = HIBYTE (StatusLast);

        /* Load the Sectin to scan the Analogs */
        OutMessage->Buffer.OutMessage[11 + AIOffset] = IDLC_ANALOGDUMP;
        OutMessage->Buffer.OutMessage[12 + AIOffset] = 4;
        OutMessage->Buffer.OutMessage[13 + AIOffset] = LOBYTE (AnalogFirst);
        OutMessage->Buffer.OutMessage[14 + AIOffset] = HIBYTE (AnalogFirst);
        OutMessage->Buffer.OutMessage[15 + AIOffset] = LOBYTE (AnalogLast);
        OutMessage->Buffer.OutMessage[16 + AIOffset] = HIBYTE (AnalogLast);

        /* Load the Sectin to request diagnostics */
        OutMessage->Buffer.OutMessage[17 + AIOffset] = IDLC_DIAGNOSTICS | 0x80;
        OutMessage->Buffer.OutMessage[18 + AIOffset] = 0;


        /* Load all the other stuff that is needed */
        populateRemoteOutMessage(*OutMessage);
        OutMessage->Buffer.OutMessage[4]  = 0x08 | IDLC_NUL_HDR; // IDLC_NUL_HDR is used to request 32 bit accumulators

        EstablishOutMessagePriority( OutMessage, ScanPriority );

        if(!useScanFlags() || (isScanFlagSet(ScanFrozen) || isScanFlagSet(ScanFreezeFailed)))
        {
            OutMessage->OutLength = 16;
        }
        else
        {
            OutMessage->OutLength = 12;
        }

        OutMessage->InLength = -1;

        setScanFlag(ScanRateIntegrity);

        /* Message is loaded so send it to porter */
        outList.push_back(OutMessage);
        OutMessage = NULL;
    }

    return status;
}

YukonError_t CtiDeviceWelco::ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList   &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    bool continue_required = false;             // This is not the last report from this device for the previous request.
    bool accums_spill_frame = false;            // The accumulator block spills across this frame into the next one.
    bool last_sectn;
    LONG  PointOffset;

    string str;
    /* Misc. definitions */
    ULONG i;
    ULONG Pointer;
    const BYTE *MyInMessage, *SaveInMessage;

    /* Define the various records */
    CtiPointSPtr          PointRecord;
    CtiPointNumericSPtr   NumericPoint;

    /* Variables for decoding Messages */
    SHORT Value;
    USHORT UValue;
    ULONG ULValue;
    FLOAT PartHour;
    FLOAT PValue;

    ULONG StartPoint;
    ULONG FinishPoint;

    static LONG  NextTime = 0;

    CtiPointDataMsg      *pData = NULL;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    OUTMESS              *OutMessage = NULL;

/* Clear the Scan Pending flag, if neccesary it will be reset */
    resetScanFlag(ScanRateGeneral);

    if(InMessage.InLength == 0)
    {
        CTILOG_ERROR(dout, "Message returned for "<< getName() <<" is zero bytes in length");

        return ClientErrors::ReadTimeout;
    }

    MyInMessage = InMessage.Buffer.InMessage - 2;

    /* Check if we need to do a continue */
    if(!((*(InMessage.Buffer.InMessage - 3)) & 0x01))      // This must be the IDLC header frame STATUS byte.  LSB is the FIN bit.
    {
        continue_required = true;
    }

    /* Check to see if this is a null response */
    if((*(InMessage.Buffer.InMessage - 3)) & 0x80)
    {
        return ClientErrors::None;
    }

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    /* Walk through the sectins */
    do
    {
        last_sectn = (MyInMessage[0] & 0x80);   // Is this the last sectn of this frame/message?

        /* decode whatever message this is */
        switch(MyInMessage[0] & 0x7f)
        {
        case IDLC_CONFIGURATION:
            {
                break;
            }
        case IDLC_DIAGNOSTICS:
            {
                string result("Diagnostic Scan\n");
                string tstr = result;

                if(MyInMessage[2] & (EW_HDW_BIT))
                {
                    result += string("Hardware error is indicated by RTU\n");

                    CTILOG_ERROR(dout, "Hardware error is indicated by RTU " << getName());
                }

                if(MyInMessage[2] & (EW_FMW_BIT))
                {
                    result += string("Firmware error is indicated by RTU\n");
                    OutMessage = new OUTMESS;

                    InEchoToOut(InMessage, *OutMessage);

                    /* This is the Big E so reset the RTU, download the deadbands and clear the demand accums */
                    if(i = WelCoReset(OutMessage, MAXPRIORITY))
                    {
                        /* Send Error to logger */
                        ReportError ((USHORT)i);
                    }
                    else
                    {
                        outList.push_back(OutMessage);
                    }
                }

                if(MyInMessage[2] & (EW_FMW_BIT | EW_PWR_BIT))
                {
                    result += string("Deadbands downloaded due to powerfail bit\n");

                    setDeadbandsSent(false);

                    if(i = WelCoDeadBands(InMessage, outList, MAXPRIORITY - 1))
                    {
                        /* Send Error to logger */
                        ReportError (i);
                    }
                    break;
                }

                if(MyInMessage[2] & (EW_FMW_BIT | EW_PWR_BIT | EW_SYN_BIT))
                {
                    result += string("Time synchronization sent to RTU\n");
                    if(i = WelCoTimeSync (InMessage, outList, MAXPRIORITY - 1))
                    {
                        /* Send Error to logger */
                        ReportError (i);
                    }
                }

                if(tstr == result)
                {
                    result += string("RTU indicates GOOD status\n");
                }

                ReturnMsg->setResultString(result);

                break;
            }
        case IDLC_FREEZE:
            {
                if(isScanFlagSet(ScanFreezePending) || !useScanFlags())
                {
                    resetScanFlag(ScanFreezePending);
                    setScanFlag(ScanFrozen);

                    /* update the accumulator criteria for this RTU */
                    if(useScanFlags())
                    {
                        setPrevFreezeTime(getLastFreezeTime());
                        setLastFreezeTime( CtiTime(InMessage.Time) );
                        resetScanFlag(ScanFreezeFailed);
                        setPrevFreezeNumber(getLastFreezeNumber());
                        setLastFreezeNumber(TRUE);
                    }

                    /* then force a scan */
                    OutMessage = new OUTMESS;

                    InEchoToOut(InMessage, *OutMessage);

                    CtiCommandParser parse(InMessage.Return.CommandStr);

                    int welcofreezedelay = gConfigParms.getValueAsInt("WELCO_FREEZE_TO_SCAN_MSEC_DELAY", 0);
                    if(welcofreezedelay)
                    {
                        Sleep(welcofreezedelay);
                    }

                    if(i = IntegrityScan (NULL, parse, OutMessage, vgList, retList, outList, MAXPRIORITY - 4))
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

                    // What is this ??? DeviceRecord->ScanStatus &= SCANFREEZEFAILED;
                    setScanFlag(ScanFreezeFailed);   // FIX FIX FIX 090799 CGP ?????
                    /* message for screwed up freeze */
                }

                break;
            }
        case IDLC_ACCUM32DUMP:
            {
                resetScanFlag(ScanRateIntegrity);
                accums_spill_frame = continue_required && last_sectn;

                CtiPointAccumulatorSPtr pAccumPoint;

                StartPoint = MyInMessage[2] + 1;
                FinishPoint = MyInMessage[2] + ((MyInMessage[1] - 1) / 4);

                // get the current pulse count
                ULONG curPulseValue;

                if(isScanFlagSet(ScanFreezePending))
                {
                    // This is a per device message, not per point....
                    CTILOG_ERROR(dout, getName() <<" - scan freeze still pending");

                    if(!accums_spill_frame)
                    {
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
                }
                else if(isScanFlagSet(ScanFrozen) || !useScanFlags())
                {
                    if(useScanFlags())
                    {
                        /* Calculate the part of an hour involved here */
                        PartHour = (FLOAT)(getLastFreezeTime().seconds() - getPrevFreezeTime().seconds());
                        PartHour /= (3600.0);
                    }
                    else
                    {
                        PartHour = 1;
                    }

                    for(PointOffset = (USHORT)StartPoint, Pointer = 3;
                       PointOffset <= FinishPoint;
                       PointOffset++, Pointer += 4)                        // 4 bytes per accumulator.  Move Pointer offset to the next one!
                    {
                        curPulseValue = MAKEULONG( MAKEUSHORT (MyInMessage[Pointer], MyInMessage[Pointer + 1]), MAKEUSHORT (MyInMessage[Pointer + 2], MyInMessage[Pointer + 3]) );

                        if(useScanFlags() && (PointRecord = getDevicePointOffsetTypeEqual(PointOffset, DemandAccumulatorPointType)))
                        {
                            pAccumPoint = boost::static_pointer_cast<CtiPointAccumulator>(PointRecord);

                            /* Copy the pulses */
                            pAccumPoint->getPointHistory().setPreviousPulseCount(pAccumPoint->getPointHistory().getPresentPulseCount());
                            pAccumPoint->getPointHistory().setPresentPulseCount(curPulseValue);

                            if(!(getPrevFreezeNumber()) || !(getLastFreezeNumber()))
                            {
                                // Inform dispatch that the point pump has just been primed.
                            }
                            else
                            {
                                /* Calculate the number of pulses */
                                if(pAccumPoint->getPointHistory().getPresentPulseCount() < pAccumPoint->getPointHistory().getPreviousPulseCount())
                                {
                                    /* Rollover */
                                    ULValue = 0xffffffff - pAccumPoint->getPointHistory().getPreviousPulseCount() + pAccumPoint->getPointHistory().getPresentPulseCount();
                                }
                                else
                                {
                                    ULValue = pAccumPoint->getPointHistory().getPresentPulseCount() - pAccumPoint->getPointHistory().getPreviousPulseCount();
                                }

                                /* Calculate in units/hour */
                                PValue = (FLOAT) ULValue * pAccumPoint->getMultiplier();

                                /* to convert to units */
                                PValue /= PartHour;

                                /* Apply offset */
                                PValue += pAccumPoint->getDataOffset();

                                str = string(getName() + " point " + pAccumPoint->getName() + " = " + CtiNumStr(PValue));
                                pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, DemandAccumulatorPointType, str);
                                if(pData != NULL)
                                {
                                    ReturnMsg->PointData().push_back(pData);
                                    pData = NULL;  // We just put it on the list...
                                }
                            }
                        }

                        /* Check if there is a pulse point */
                        if((PointRecord = getDevicePointOffsetTypeEqual(PointOffset, PulseAccumulatorPointType)))
                        {
                            pAccumPoint = boost::static_pointer_cast<CtiPointAccumulator>(PointRecord);

                            /* Copy the pulses */
                            pAccumPoint->getPointHistory().setPreviousPulseCount(pAccumPoint->getPointHistory().getPresentPulseCount());
                            pAccumPoint->getPointHistory().setPresentPulseCount(curPulseValue);

                            /* Calculate in units/hour */
                            PValue = (FLOAT) pAccumPoint->getPointHistory().getPresentPulseCount() * pAccumPoint->getMultiplier();

                            /* Apply offset */
                            PValue += pAccumPoint->getDataOffset();

                            str = string(getName() + " point " + pAccumPoint->getName() + " = " + CtiNumStr(PValue));
                            pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, PulseAccumulatorPointType, str);

                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().push_back(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }
                    }

                    if(!accums_spill_frame)
                    {
                        resetScanFlag(ScanFrozen);
                        resetScanFlag(ScanFreezeFailed);
                    }
                }

                break;
            }
        case IDLC_ACCUMDUMP:
            {
                resetScanFlag(ScanRateIntegrity);
                accums_spill_frame = continue_required && last_sectn;

                CtiPointAccumulatorSPtr pAccumPoint;

                StartPoint = MyInMessage[2] + 1;
                FinishPoint = StartPoint + (MyInMessage[1] - 1 / 2) - 1;

                if(isScanFlagSet(ScanFreezePending))
                {
                    // This is a per device message, not per point....
                    CTILOG_ERROR(dout, getName() <<" - scan freeze still pending");

                    if(!accums_spill_frame)
                    {
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
                }
                else if(isScanFlagSet(ScanFrozen) || !useScanFlags())
                {
                    Pointer = 1;

                    if(useScanFlags())
                    {
                        /* Calculate the part of an hour involved here */
                        PartHour = (FLOAT)(getLastFreezeTime().seconds() - getPrevFreezeTime().seconds());
                        PartHour /= (3600.0);
                    }
                    else
                    {
                        PartHour = 1;
                    }

                    for(PointOffset = (USHORT)StartPoint; PointOffset <= FinishPoint; PointOffset++)
                    {
                        Pointer += 2;

                        if( useScanFlags() && PartHour != 0.0 && (PointRecord = getDevicePointOffsetTypeEqual(PointOffset, DemandAccumulatorPointType)))
                        {
                            pAccumPoint = boost::static_pointer_cast<CtiPointAccumulator>(PointRecord);

                            ULONG  prevpulses = pAccumPoint->getPointHistory().getPresentPulseCount();
                            ULONG  prespulses = MAKEUSHORT (MyInMessage[Pointer], MyInMessage[Pointer + 1]);
                            DOUBLE multiplier = pAccumPoint->getMultiplier();
                            DOUBLE dataoffset = pAccumPoint->getDataOffset();

                            /* Copy the pulses */
                            pAccumPoint->getPointHistory().setPreviousPulseCount(prevpulses);
                            pAccumPoint->getPointHistory().setPresentPulseCount(prespulses);

                            if(!(getPrevFreezeNumber()) || !(getLastFreezeNumber()))
                            {
                                // Inform dispatch that the point pump has just been primed.
                            }
                            else
                            {
                                /* Calculate the number of pulses */
                                if(prespulses < prevpulses)
                                {
                                    /* Rollover */
                                    UValue = 0xffff - prevpulses + prespulses;
                                }
                                else
                                {
                                    UValue = prespulses - prevpulses;
                                }

                                /* Calculate in units/hour */
                                PValue = (FLOAT) UValue * multiplier;

                                /* to convert to units */
                                PValue /= PartHour;

                                /* Apply offset */
                                PValue += dataoffset;

                                str = string(getName() + " point " + pAccumPoint->getName() + " = " + CtiNumStr(PValue));
                                pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, DemandAccumulatorPointType, str);
                                if(pData != NULL)
                                {
                                    ReturnMsg->PointData().push_back(pData);
                                    pData = NULL;  // We just put it on the list...
                                }

                                if(getDebugLevel() & DEBUGLEVEL_WELCO_PROTOCOL && PValue > 100000.0)
                                {
                                    Cti::FormattedList itemList;

                                    itemList.add("Previous Pulses")   << prevpulses;
                                    itemList.add("Present Pulses")    << prespulses;
                                    itemList.add("Point Multiplier")  << multiplier;
                                    itemList.add("Point Data Offset") << dataoffset;
                                    itemList.add("Part Hour")         << PartHour;

                                    CTILOG_DEBUG(dout, getName() <<" Demand Accum pt. "<< pAccumPoint->getName() <<" = "<< PValue <<
                                            itemList
                                            );
                                }
                            }
                        }

                        /* Check if there is a pulse point */
                        if((PointRecord = getDevicePointOffsetTypeEqual(PointOffset, PulseAccumulatorPointType)))
                        {
                            pAccumPoint = boost::static_pointer_cast<CtiPointAccumulator>(PointRecord);

                            /* Copy the pulses */
                            pAccumPoint->getPointHistory().setPreviousPulseCount(pAccumPoint->getPointHistory().getPresentPulseCount());
                            pAccumPoint->getPointHistory().setPresentPulseCount(MAKEUSHORT (MyInMessage[Pointer], MyInMessage[Pointer + 1]));

                            /* Calculate in units/hour */
                            PValue = (FLOAT) pAccumPoint->getPointHistory().getPresentPulseCount() * pAccumPoint->getMultiplier();

                            /* Apply offset */
                            PValue += pAccumPoint->getDataOffset();

                            str = string(getName() + " point " + pAccumPoint->getName() + " = " + CtiNumStr(PValue));
                            pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, PulseAccumulatorPointType, str);
                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().push_back(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }
                    }

                    if(!accums_spill_frame)
                    {
                        resetScanFlag(ScanFrozen);
                        resetScanFlag(ScanFreezeFailed);
                    }
                }

                break;
            }
        case IDLC_STATUSDUMP:
            {
                resetScanFlag(ScanRateIntegrity);
                StartPoint = MAKEUSHORT (MyInMessage[2], MyInMessage[3]) + 1;
                FinishPoint = StartPoint + ((MyInMessage[1] - 2) / 2) * 8;

                /* Now loop through and update received points as needed */
                for(PointOffset = StartPoint; PointOffset <= FinishPoint; PointOffset++)
                {
                    /* PointOffset is the offset is this RTU... */
                    if((PointRecord = getDevicePointOffsetTypeEqual(PointOffset, StatusPointType)))
                    {
                        /* Apply offset */
                        if((MyInMessage[4 + 2 * ((PointOffset - StartPoint) / 8)] >> ((PointOffset - StartPoint) % 8)) & 0x01)
                        {
                            PValue = STATE_CLOSED;
                        }
                        else
                        {
                            PValue = STATE_OPENED;
                        }

                        /*
                         *  If this next bit is too confusing, you haven't read the protocol document.
                         *
                         *  Basically the RTU tells you the current status (above) and provides a bit which
                         *   is set on the second (and subsequent) state change(s) since the last
                         *   status (or exception) report.  We know only that two or more changes have
                         *   occured iff the Change Flag is set, so we'll just log a change in the
                         *   opposite direction and then the current state....
                         */

                        /* Check if this is "changed" */
                        if((MyInMessage[4 + 2 * ((PointOffset - StartPoint) / 8) + 1] >> ((PointOffset - StartPoint) % 8)) & 0x01)
                        {
                            PValue = ( (PValue == STATE_CLOSED) ? STATE_OPENED : STATE_CLOSED );
                        }

                        str = string(getName() + " point " + PointRecord->getName() + " = " + ResolveStateName(PointRecord->getStateGroupID(), (LONG)(PValue)));
                        pData = CTIDBG_new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, StatusPointType, str);
                        if(pData != NULL)
                        {
                            pData->setTime( pData->getTime() - 1 );
                            ReturnMsg->PointData().push_back(pData);
                            pData = NULL;  // We just put it on the list...
                        }

                        /* Check if the "Changed Flag is high and toggle it back to the current status" */
                        if((MyInMessage[4 + 2 * ((PointOffset - StartPoint) / 8) + 1] >> ((PointOffset - StartPoint) % 8)) & 0x01)
                        {
                            PValue = ( (PValue == STATE_CLOSED) ? STATE_OPENED : STATE_CLOSED );
                            str = string(getName() + " point " + PointRecord->getName() + " = " + ResolveStateName(PointRecord->getStateGroupID(), (LONG)(PValue)));
                            pData = CTIDBG_new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, StatusPointType, str);

                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().push_back(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }
                    }
                }

                break;
            }
        case IDLC_STATUSEXCEPTION:
            {
                resetScanFlag(ScanException);
                /* Now loop through and update received points as needed */
                /* FinishPoint is used as COUNT of points in exception dump */
                FinishPoint = MyInMessage[1] / 2;
                /* StartPoint is used as the counter, and really holds the position rather than the actual offset point number */
                for(StartPoint = 0; StartPoint < FinishPoint; StartPoint++)
                {
                    /* Which offset is this in the EW Protocol spec... */
                    PointOffset = MAKEUSHORT (MyInMessage[(StartPoint * 2) + 2],
                                              MyInMessage[(StartPoint * 2) + 3] & 0x0f) + 1;

                    if((PointRecord = getDevicePointOffsetTypeEqual(PointOffset, StatusPointType)))
                    {
                        /* get the present value */
                        if(MyInMessage[(StartPoint * 2) + 3]  & 0x80)
                        {
                            PValue = STATE_CLOSED;
                        }
                        else
                        {
                            PValue = STATE_OPENED;
                        }

                        /* Check if this is "changed" */
                        if(MyInMessage[(StartPoint * 2) + 3] & 0x40)
                        {
                            PValue = ( (PValue == STATE_CLOSED) ? STATE_OPENED : STATE_CLOSED );
                        }

                        str = string(getName() + " point " + PointRecord->getName() + " = " + ResolveStateName(PointRecord->getStateGroupID(), (LONG)(PValue)));
                        pData = CTIDBG_new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, StatusPointType, str);
                        if(pData != NULL)
                        {
                            ReturnMsg->PointData().push_back(pData);
                            pData = NULL;  // We just put it on the list...
                        }

                        /* Check if this is "changed" */
                        if(MyInMessage[(StartPoint * 2) + 3] & 0x40)
                        {
                            PValue = ( (PValue == STATE_CLOSED) ? STATE_OPENED : STATE_CLOSED );
                            str = string(getName() + " point " + PointRecord->getName() + " = " + ResolveStateName(PointRecord->getStateGroupID(), (LONG)(PValue)));
                            pData = CTIDBG_new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, StatusPointType, str);
                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().push_back(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }
                    }
                }

                break;
            }
        case IDLC_ANALOGDUMP:
            {
                resetScanFlag(ScanRateIntegrity);
                StartPoint = MAKEUSHORT (MyInMessage[2], MyInMessage[3]) + 1;

                if((*(InMessage.Buffer.InMessage - 3)) & 0x08)
                {
                    /* This is a 16 bit analog */
                    FinishPoint = StartPoint  + (MyInMessage[1] - 2) / 2 - 1;
                }
                else
                {
                    /* This is a 12 bit analog */
                    FinishPoint = StartPoint  + ((MyInMessage[1] - 2) / 3) * 2;
                    if(((MyInMessage[1] - 2) / 3) * 3 != (MyInMessage[1] - 2))
                    {
                        FinishPoint++;
                    }
                }

                /* Now loop through and update received points as needed */

                for(PointOffset = (USHORT)StartPoint; PointOffset <= FinishPoint; PointOffset++)
                {
                    if(NumericPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(PointOffset, AnalogPointType)))
                    {
                        /* update the point data */
                        if((*(InMessage.Buffer.InMessage - 3)) & 0x08)
                        {
                            /* This is a 16 bit analog */
                            Value = MAKEUSHORT (MyInMessage[((PointOffset - StartPoint) * 2) + 4],
                                                MyInMessage[((PointOffset - StartPoint) * 2) + 5]);
                        }
                        else
                        {
                            /* This is a 12 bit analog */
                            if((PointOffset - StartPoint) & 0x01)
                            {
                                Value = MAKEUSHORT (((MyInMessage[((PointOffset - StartPoint) / 2) * 3 + 5] >> 4) & 0x0f) |
                                                    ((MyInMessage[((PointOffset - StartPoint) / 2) * 3 + 6] << 4) &0xf0),
                                                    (MyInMessage[((PointOffset - StartPoint) / 2) * 3 + 6] >> 4) & 0x0f);
                            }
                            else
                            {
                                Value = MAKEUSHORT (MyInMessage[((PointOffset - StartPoint) / 2) * 3 + 4],
                                                    MyInMessage[((PointOffset - StartPoint) / 2) * 3 + 5] & 0x0f);
                            }

                            Value = Value << 4;
                            Value = Value / 16;
                        }

                        PValue = Value * NumericPoint->getMultiplier() + NumericPoint->getDataOffset();

                        str = string(getName() + " point " + NumericPoint->getName() + " = " + CtiNumStr(PValue));
                        pData = CTIDBG_new CtiPointDataMsg(NumericPoint->getPointID(),
                                                    PValue,
                                                    NormalQuality,
                                                    AnalogPointType,
                                                    str);
                        if(pData != NULL)
                        {
                            ReturnMsg->PointData().push_back(pData);
                            pData = NULL;  // We just put it on the list...
                        }
                    }
                }
                break;

            }
        case IDLC_ANALOGEXCEPTION:
            {
                resetScanFlag(ScanException);
                // memcpy (PointRecord.getName(), DeviceRecord->getName(), STANDNAMLEN);
                // PointRecord.PointType = ANALOGPOINT;

                /* FinishPoint is used as count for analog exceptions */
                if((*(InMessage.Buffer.InMessage - 3)) & 0x08)
                {
                    /* 16 bit analogs pack 2 to 7 bytes */
                    FinishPoint = (MyInMessage[1] * 2) / 7;
                }
                else
                {
                    /* 12 bit analogs pack 1 to 3 bytes */
                    FinishPoint = MyInMessage[1] / 3;
                }

                /* StartPoint is used as the counter */
                for(StartPoint = 0; StartPoint < FinishPoint; StartPoint++)
                {
                    if((*(InMessage.Buffer.InMessage - 3)) & 0x08)
                    {
                        if(StartPoint & 0x01)
                        {
                            PointOffset = MAKEUSHORT ( ((MyInMessage[(((StartPoint - 1) * 7) / 2) + 7] >> 4) & 0x0f) |
                                                       ((MyInMessage[(((StartPoint - 1) * 7) / 2) + 8] << 4) & 0xf0),
                                                       ((MyInMessage[(((StartPoint - 1) * 7) / 2) + 8] >> 4) & 0x0f)) + 1;
                        }
                        else
                        {
                            PointOffset = MAKEUSHORT (MyInMessage[((StartPoint * 7) / 2) + 2],
                                                      MyInMessage[((StartPoint * 7) / 2) + 3] & 0x0f) + 1;
                        }

                    }
                    else
                    {
                        PointOffset = MAKEUSHORT (MyInMessage[(StartPoint * 3) + 2],
                                                  MyInMessage[(StartPoint * 3) + 3] & 0x0f);
                    }

                    /* Now Update the Record if it exists */
                    if(NumericPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(PointOffset, AnalogPointType)))
                    {
                        if((*(InMessage.Buffer.InMessage - 3)) & 0x08)
                        {
                            if(StartPoint & 0x01)
                            {
                                Value = MAKEUSHORT ((((MyInMessage[(((StartPoint - 1) * 7) / 2) + 5] >> 4) & 0x0f) |
                                                     ((MyInMessage[(((StartPoint - 1) * 7) / 2) + 6] << 4) & 0xf0)),
                                                    (((MyInMessage[(((StartPoint - 1) * 7) / 2) + 6] >> 4) & 0x0f) |
                                                     ((MyInMessage[(((StartPoint - 1) * 7) / 2) + 7] << 4) & 0xf0)));
                            }
                            else
                            {
                                Value = MAKEUSHORT ((((MyInMessage[((StartPoint * 7) / 2) + 3] >> 4) & 0x0f) |
                                                     ((MyInMessage[((StartPoint * 7) / 2) + 4] << 4) & 0xf0)),
                                                    (((MyInMessage[((StartPoint * 7) / 2) + 4] >> 4) & 0x0f) |
                                                     ((MyInMessage[((StartPoint * 7) / 2) + 5] << 4) & 0xf0)));
                            }
                        }
                        else
                        {
                            /* update the point data */
                            Value = MAKEUSHORT (((MyInMessage[(StartPoint * 3) + 3] >> 4) & 0x0f),
                                                MyInMessage[(StartPoint * 3) + 4]);

                            Value = Value << 4;

                            Value = Value / 16;
                        }

                        PValue = Value * NumericPoint->getMultiplier() + NumericPoint->getDataOffset();

                        str = string(getName() + " point " + NumericPoint->getName() + " = " + CtiNumStr(PValue));
                        pData = CTIDBG_new CtiPointDataMsg(NumericPoint->getPointID(), PValue, NormalQuality, AnalogPointType, str);
                        if(pData != NULL)
                        {
                            ReturnMsg->PointData().push_back(pData);
                            pData = NULL;  // We just put it on the list...
                        }
                    }

                }       /* end of for */

                break;
            }
        case IDLC_DEADBANDS:
            {
                if(MyInMessage[4] > 0)     // Response indicates more than zero were sent!
                {
                    setDeadbandsSent(true);
                }

                break;
            }
        default:
            {
                break;
            }
        }   /* End of switch */

        /* Figure out the next sectin */
        SaveInMessage = MyInMessage;
        MyInMessage   = MyInMessage + MyInMessage[1] + 2;

    } while(!(SaveInMessage[0] & 0x80));

    /* Check if we need to do a continue */
    if( continue_required )
    {
        if(ReturnMsg)                                       // Let clients know there is more data coming.
            ReturnMsg->setExpectMore(true);

        OutMessage = new OUTMESS;

        InEchoToOut(InMessage, *OutMessage);

        if(i = WelCoContinue (OutMessage, MAXPRIORITY - 4))
        {
            /* Send Error to logger */
            ReportError (i);
        }
        else
        {
            outList.push_back(OutMessage);
            setScanFlag(ScanRateGeneral);
        }
    }

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

    return ClientErrors::None;
}




/* Routine to error codes from a WelCo device */
void CtiDeviceWelco::WelCoGetError(OUTMESS *OutMessage, INT Priority)            /* Priority to place command on queue */
{
    /* Load the sectn to reset the RTU */
    OutMessage->Buffer.OutMessage[5] = IDLC_DIAGNOSTICS | 0x80;  // This is what it should be. CGP.
    OutMessage->Buffer.OutMessage[6] = 0;

    /* Load all the other stuff that is needed */
    populateRemoteOutMessage(*OutMessage);
    OutMessage->Buffer.OutMessage[4] = 0x08;
    EstablishOutMessagePriority( OutMessage, Priority );
    OutMessage->OutLength    = 0;
    OutMessage->InLength     = -1;
}




/* Routine to output continue message to a WelCo device */
YukonError_t CtiDeviceWelco::WelCoContinue (OUTMESS *OutMessage, INT Priority)
{
    YukonError_t status = ClientErrors::None;

    if(getDebugLevel() & DEBUGLEVEL_WELCO_PROTOCOL)
    {
        CTILOG_DEBUG(dout, "Issuing a continue message!");
    }

    /* Load the sectn to scan the stati */
    OutMessage->Buffer.OutMessage[5] = IDLC_CONTINUE | 0x80;
    OutMessage->Buffer.OutMessage[6] = 0;

    /* Load all the other stuff that is needed */
    OutMessage->Buffer.OutMessage[4] = 0x08;

    populateRemoteOutMessage(*OutMessage);
    EstablishOutMessagePriority( OutMessage, Priority );
    OutMessage->OutLength = 0;
    OutMessage->InLength  = -1;

    return status;
}


/* Routine to output continue message to a WelCo device */
void CtiDeviceWelco::WelCoPoll (OUTMESS *OutMessage, INT Priority)
{
    /* Load the sectn to scan the stati */
    OutMessage->Buffer.OutMessage[5] = IDLC_POLL | 0x80;
    OutMessage->Buffer.OutMessage[6] = 0;

    /* Load all the other stuff that is needed */
    OutMessage->Buffer.OutMessage[4] = 0x08 | IDLC_NUL_HDR;
    populateRemoteOutMessage(*OutMessage);
    EstablishOutMessagePriority( OutMessage, Priority );
    OutMessage->OutLength    = 0;
    OutMessage->InLength     = -1;
}

YukonError_t CtiDeviceWelco::WelCoTimeSync(const INMESS &InMessage, OutMessageList &outList, INT Priority)
{
    YukonError_t status = ClientErrors::None;

    OUTMESS *OutMessage = new OUTMESS;

    InEchoToOut(InMessage, *OutMessage);
    status = WelCoTimeSync(OutMessage, MAXPRIORITY - 1);

    if(status == ClientErrors::None)
    {
        outList.push_back(OutMessage);
    }

    return status;
}

/* Routine to send a time sync to a WelCo device */
YukonError_t CtiDeviceWelco::WelCoTimeSync(OUTMESS *OutMessage, INT Priority)
{
    YukonError_t status = ClientErrors::None;

    if(OutMessage != NULL)
    {

        OutMessage->Buffer.OutMessage[5] = IDLC_TIMESYNC | 0x80;
        OutMessage->Buffer.OutMessage[6] = 7;

        /* send a time sync to this guy */
        populateOutMessage(*OutMessage);  //  not the Remote version, because we override Retry and EventCode anyway
        OutMessage->Retry              = 0;
        OutMessage->OutLength          = 7;
        OutMessage->InLength           = 0;
        OutMessage->Source             = 0;
        OutMessage->Destination        = 0;
        OutMessage->Sequence           = 0;
        OverrideOutMessagePriority( OutMessage, Priority );
        OutMessage->EventCode          = NORESULT | ENCODED | TSYNC;
        OutMessage->ReturnNexus        = NULL;
        OutMessage->SaveNexus          = NULL;
    }
    else
    {
        status = ClientErrors::MemoryAccess;
    }

    return status;
}


/* Routine to reset a WelCo device */
YukonError_t CtiDeviceWelco::WelCoReset(OUTMESS *OutMessage, INT Priority)
{
    YukonError_t   status = ClientErrors::None;

    /* Load the sectn to reset the RTU */
    OutMessage->Buffer.OutMessage[5] = IDLC_RESET | 0x80;
    OutMessage->Buffer.OutMessage[6] = 3;
    OutMessage->Buffer.OutMessage[7] = 0;
    OutMessage->Buffer.OutMessage[8] = 0;
    OutMessage->Buffer.OutMessage[9] = 0;

    /* Load all the other stuff that is needed */
    OutMessage->Buffer.OutMessage[4] = 0x08;
    populateRemoteOutMessage(*OutMessage);
    EstablishOutMessagePriority( OutMessage, Priority );
    OutMessage->OutLength    = 3;
    OutMessage->InLength     = -1;

    return status;
}


YukonError_t CtiDeviceWelco::WelCoDeadBands(const INMESS &InMessage, OutMessageList &outList, INT Priority)
{
    OUTMESS *OutMessage = new OUTMESS;

    InEchoToOut(InMessage, *OutMessage);

    return WelCoDeadBands(OutMessage, outList, MAXPRIORITY - 1);
}

/* Routine to download deadbands for analogs */
YukonError_t CtiDeviceWelco::WelCoDeadBands(OUTMESS *OutMessage, OutMessageList &outList, INT Priority)
{
    INT      Position;
    ULONG    ByteCount;

    USHORT   AnalogFirst = 0xffff;
    USHORT   AnalogLast = 0;

    CtiPointSPtr PointRecord;

    YukonError_t status = ClientErrors::None;

    OUTMESS *MyOutMessage = NULL;

    if(OutMessage != NULL)
    {
        std::vector<CtiPointSPtr> points;

        getDevicePoints(points);

        std::vector<CtiPointSPtr>::iterator itr;

        for( itr = points.begin(); itr != points.end(); itr++ )
        {
            CtiPointSPtr PointRecord = *itr;

            switch(PointRecord->getType())
            {
                case AnalogPointType:
                {
                    CtiPointAnalogSPtr Point = boost::static_pointer_cast<CtiPointAnalog>(PointRecord);

                    if(Point->getPointOffset() - 1 > AnalogLast)
                    {
                        AnalogLast = Point->getPointOffset() - 1;
                    }

                    if(Point->getPointOffset() - 1 < AnalogFirst)
                    {
                        AnalogFirst = Point->getPointOffset() - 1;
                    }

                    break;
                }
            }
        }


        if(AnalogFirst <= AnalogLast)
        {
            MyOutMessage = CTIDBG_new OUTMESS( *OutMessage );      // Use the copy constructor...

            if(MyOutMessage != NULL)
            {
                /* We now have the starting and ending analog points */
                ByteCount = 0;

                MyOutMessage->Buffer.OutMessage[7] = LOBYTE (AnalogFirst);
                MyOutMessage->Buffer.OutMessage[8] = HIBYTE (AnalogFirst);

                Position = AnalogFirst + 1;

                for(Position = AnalogFirst + 1; Position <= AnalogLast + 1 && status == ClientErrors::None; Position++)
                {
                    CtiPointAnalogSPtr Point = boost::static_pointer_cast<CtiPointAnalog>(getDevicePointOffsetTypeEqual(Position, AnalogPointType));

                    /* Check for this one in the database and load the deadband */
                    if(Point && !Point->isPseudoPoint())
                    {
                        if(Point->getDeadband() == -1.0)
                        {
                            MyOutMessage->Buffer.OutMessage[ByteCount + 9] = 255;
                        }
                        else
                        {
                            double absmult = fabs(Point->getMultiplier());

                            if((Point->getDeadband() / absmult) > 255)
                            {
                                MyOutMessage->Buffer.OutMessage[ByteCount + 9] = 255;
                            }
                            else
                            {
                                MyOutMessage->Buffer.OutMessage[ByteCount + 9] = (UCHAR)(Point->getDeadband() / absmult);
                            }
                        }
                    }
                    else
                    {
                        MyOutMessage->Buffer.OutMessage[ByteCount + 9] = 255;
                    }

                    /* Load the group number (default for now) */
                    MyOutMessage->Buffer.OutMessage[ByteCount + 10] = 0;

                    ByteCount += 2;

                    /* Check if we need to ship it */
                    if(ByteCount >= 250 || Position == (AnalogLast + 1))
                    {
                        MyOutMessage->Buffer.OutMessage[4]     = 0x08;     // 16 bit message...
                        MyOutMessage->Buffer.OutMessage[5]     = IDLC_DEADBANDS | 0x80;
                        MyOutMessage->Buffer.OutMessage[6]     = (UCHAR)(ByteCount + 2);

                        /* Load all the other stuff that is needed */
                        populateRemoteOutMessage(*MyOutMessage);
                        MyOutMessage->Retry = 0;  //  override
                        OverrideOutMessagePriority( MyOutMessage, Priority );
                        MyOutMessage->OutLength = ByteCount + 2;
                        MyOutMessage->InLength  = -1;

                        MyOutMessage->ExpirationTime = CtiTime().seconds() + 120;     // These guys do not need to be on-queue for too long.

                        outList.push_back(MyOutMessage);
                        MyOutMessage = NULL;                // Out of our hands now...

                        if(Position != (AnalogLast + 1))
                        {
                            MyOutMessage = CTIDBG_new OUTMESS( *OutMessage );

                            if(MyOutMessage == NULL)
                            {
                                status = ClientErrors::MemoryAccess;
                                break;
                            }

                            MyOutMessage->Buffer.OutMessage[7] = LOBYTE (Position+1);
                            MyOutMessage->Buffer.OutMessage[8] = HIBYTE (Position+1);

                            ByteCount = 0;
                        }
                    }
                }
            }
            else
            {
                status = ClientErrors::MemoryAccess;
            }
        }
    }

    return status;
}



YukonError_t CtiDeviceWelco::ErrorDecode(const INMESS   &InMessage,
                                         const CtiTime   TimeNow,
                                         CtiMessageList &retList)
{
    YukonError_t nRet = ClientErrors::None;

    CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

    if(pMsg != NULL)
    {
        pMsg->insert( -1 );                 // This is the dispatch token and is unimplemented at this time
        pMsg->insert(CtiCommandMsg::OP_DEVICEID);          // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
        pMsg->insert(getID());              // The id (device or point which failed)
        pMsg->insert(ScanRateGeneral);      // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

        pMsg->insert(
                InMessage.ErrorCode
                    ? InMessage.ErrorCode
                    : ClientErrors::GeneralScanAborted);

        retList.push_back( pMsg );
    }

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
            setLastFreezeTime(InMessage.Time );
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

    return nRet;
}

YukonError_t CtiDeviceWelco::ExecuteRequest(CtiRequestMsg     *pReq,
                                            CtiCommandParser  &parse,
                                            OUTMESS          *&OutMessage,
                                            CtiMessageList    &vgList,
                                            CtiMessageList    &retList,
                                            OutMessageList    &outList)
{
    YukonError_t nRet = ClientErrors::None;
    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*, INT ScanPriority)
     *   (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */


    switch(parse.getCommand())
    {
    case LoopbackRequest:
        {
            int cnt = parse.getiValue("count");

            for(int i = 0; i < cnt; i++)
            {
                OUTMESS *OutMTemp = CTIDBG_new OUTMESS(*OutMessage);

                if(OutMTemp != NULL)
                {
                    OutMTemp->Request = OutMessage->Request;
                    WelCoGetError(OutMTemp, 12);
                    outList.push_back(OutMTemp);
                }
            }

            break;
        }
    case ScanRequest:
        {
            nRet = executeScan(pReq, parse, OutMessage, vgList, retList, outList);
            break;
        }
    case ControlRequest:
        {
            nRet = executeControl(pReq, parse, OutMessage, vgList, retList, outList);
            break;
        }
    case PutConfigRequest:
        {
            {
                CtiReturnMsg *ret = CTIDBG_new CtiReturnMsg(getID(), parse.getCommandStr());

                nRet = WelCoTimeSync(OutMessage, MAXPRIORITY - 1);

                OutMessage->EventCode &= ~TSYNC;    // We'll be tricking this one here.
                BYTE *Message = OutMessage->Buffer.OutMessage + PREIDLEN;

                {
                    struct timeb TimeB;
                    struct tm TimeSt;

                    /* get the time from the system */
                    UCTFTime (&TimeB);

                    /* Add in the extra seconds */
                    TimeB.time += (TimeB.millitm / 1000);

                    /* Readjust milliseconds */
                    TimeB.millitm %= 1000;

                    UCTLocoTime (TimeB.time, TimeB.dstflag, &TimeSt);

                    /* Move it into the message */
                    Message[0] = TimeSt.tm_mon + 1;
                    Message[1] = TimeSt.tm_mday;
                    Message[2] = TimeSt.tm_hour;
                    Message[3] = TimeSt.tm_min;
                    Message[4] = TimeSt.tm_sec;

                    /* Load the milliseconds */
                    Message[5] = LOBYTE (TimeB.millitm);
                    Message[6] = HIBYTE (TimeB.millitm);

                    ret->setResultString("Time set to " +
                                         CtiNumStr(TimeSt.tm_mon + 1) + string("/") +
                                         CtiNumStr(TimeSt.tm_mday) + " " +
                                         CtiNumStr(TimeSt.tm_hour).zpad(2) + ":" +
                                         CtiNumStr(TimeSt.tm_min).zpad(2) + ":" +
                                         CtiNumStr(TimeSt.tm_sec).zpad(2));
                }

                retList.push_back( ret );

                outList.push_back(OutMessage);
                OutMessage = NULL;
            }

            break;
        }
    case GetStatusRequest:
    case GetValueRequest:
    case PutValueRequest:
    case PutStatusRequest:
    case GetConfigRequest:
    default:
        {
            CTILOG_ERROR(dout, "Command not supported ("<< parse.getCommand() <<")");

            nRet = ClientErrors::NoMethodForExecuteRequest;
            /* Set the error value in the base class. */
            // FIX FIX FIX 092999
            retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                             string(OutMessage->Request.CommandStr),
                                             string("Welco Devices do not support this command (yet?)"),
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

bool CtiDeviceWelco::getDeadbandsSent() const
{
    return _deadbandsSent >= 10;
}
void CtiDeviceWelco::incDeadbandsSent()
{
    _deadbandsSent++;
    return;
}
CtiDeviceWelco& CtiDeviceWelco::setDeadbandsSent(const bool b)
{
    if(b)
    {
        _deadbandsSent = 10;
    }
    else
    {
        _deadbandsSent = 0;
    }
    return *this;
}


YukonError_t CtiDeviceWelco::executeControl(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
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


        if( ctlPoint )
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

                        OverrideOutMessagePriority( OutMessage, MAXPRIORITY - 1);

                        OutMessage->TimeOut     = 2;
                        OutMessage->InLength    = -1;
                        OutMessage->EventCode   |= ENCODED | NOWAIT | NORESULT;          // May contain RESULT based upon the incoming OutMessage
                        OutMessage->ReturnNexus = NULL;
                        OutMessage->SaveNexus   = NULL;

                        if(!OutMessage->TargetID) OutMessage->TargetID = getID();
                        if(!OutMessage->DeviceID) OutMessage->DeviceID = getID();

                        OUTMESS *MyOutMessage = CTIDBG_new OUTMESS(*OutMessage);

                        MyOutMessage->OutLength = 3;

                        /* Load Up the SBO Sectin */
                        MyOutMessage->Buffer.OutMessage[4] = 0x01;

                        MyOutMessage->Buffer.OutMessage[5] = IDLC_SBOSELECT | 0x80;
                        MyOutMessage->Buffer.OutMessage[6] = 3;
                        MyOutMessage->Buffer.OutMessage[7] = LOBYTE(ctloffset - 1);

                        /* Load the appropriate times into the message */
                        if( findStringIgnoreCase(parse.getCommandStr(), controlParameters->getStateZeroControl()) )       //  (parse.getFlags() & CMD_FLAG_CTL_OPEN)
                        {
                            controlState = STATEZERO;
                            MyOutMessage->Buffer.OutMessage[8] = LOBYTE (controlParameters->getCloseTime1() / 10);
                            MyOutMessage->Buffer.OutMessage[9] = (HIBYTE (controlParameters->getCloseTime1() / 10) & 0x3f) | ((parse.getFlags() & CMD_FLAG_CTL_OPEN) ? EW_TRIP_MASK : EW_CLOSE_MASK);
                        }
                        else if( findStringIgnoreCase(parse.getCommandStr(), controlParameters->getStateOneControl()) )  // (parse.getFlags() & CMD_FLAG_CTL_CLOSE)
                        {
                            controlState = STATEONE;
                            MyOutMessage->Buffer.OutMessage[8] = LOBYTE (controlParameters->getCloseTime2() / 10);
                            MyOutMessage->Buffer.OutMessage[9] = (HIBYTE (controlParameters->getCloseTime2() / 10) & 0x3f) | ((parse.getFlags() & CMD_FLAG_CTL_OPEN) ? EW_TRIP_MASK : EW_CLOSE_MASK);
                        }
                        else
                        {
                            delete (MyOutMessage);
                            return ClientErrors::BadState;
                        }

                        outList.push_back( MyOutMessage );

                        /* Load all the other stuff that is needed */
                        OutMessage->Retry = 0;
                        OutMessage->OutLength = 1;
                        OutMessage->Sequence    = 0;

                        /* Set up the SBO Execute */
                        OutMessage->Buffer.OutMessage[5] = IDLC_SBOEXECUTE | 0x80;
                        OutMessage->Buffer.OutMessage[6] = 1;
                        OutMessage->Buffer.OutMessage[7] = LOBYTE (ctloffset - 1);

                        /* Sent the message on to the remote */
                        outList.push_back( OutMessage );
                        OutMessage = 0;

                        CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg ( ctlPoint->getDeviceID(), ctlPoint->getPointID(), controlState, CtiTime(), -1, 100 );

                        hist->setMessagePriority( hist->getMessagePriority() + 1 );
                        vgList.push_back( hist );

                        if(ctlPoint->isPseudoPoint())
                        {
                            // There is no physical point to observe and respect.  We lie to the control point.
                            CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(ctlPoint->getID(),
                                                                         (DOUBLE)controlState,
                                                                         NormalQuality,
                                                                         StatusPointType,
                                                                         string("This point has been controlled"));
                            pData->setUser( pReq->getUser() );
                            vgList.push_back(pData);
                        }

                        retList.push_back( CTIDBG_new CtiReturnMsg(getID(), parse.getCommandStr(), string("Command submitted to port control")) );
                    }
                }
                else
                {
                    CTILOG_ERROR(dout, "Control Point " << ctlPoint->getName() << " is disabled");
                }
            }
        }
        else
        {
            CTILOG_ERROR(dout, getName() <<" Control point "<< ctlpt <<" does not exist");
        }
    }
    else
    {
        CTILOG_WARN(dout, getName() <<" is disabled"); // FIXME is this a warning or an error?
    }

    return status;
}


bool CtiDeviceWelco::clearedForScan(const CtiScanRate_t scantype)
{
    bool status = false;

    switch(scantype)
    {
    case ScanRateGeneral:
        {
            status = (!isScanFlagSet(ScanRateIntegrity) && !isScanFlagSet(scantype) && !isScanFlagSet(ScanFreezePending) && !isScanFlagSet(ScanResetting)) || ( isScanFlagSet(ScanForced) );
            break;
        }
    case ScanRateIntegrity:
        {
            status = (!isScanFlagSet(ScanRateAccum) && !isScanFlagSet(scantype) && !isScanFlagSet(ScanFreezePending) && !isScanFlagSet(ScanResetting));
            break;
        }
    case ScanRateAccum:
        {
            status = (!isScanFlagSet(scantype) && !isScanFlagSet(ScanFreezePending) && !isScanFlagSet(ScanResetting));
            break;
        }
    }

    return validateClearedForScan(status, scantype);
}

void CtiDeviceWelco::resetForScan(const CtiScanRate_t scantype)
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

