/*-----------------------------------------------------------------------------*
*
* File:   dev_welco
*
* Date:   2/15/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_welco.cpp-arc  $
* REVISION     :  $Revision: 1.26 $
* DATE         :  $Date: 2005/02/10 23:24:01 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include <windows.h>

#include "cparms.h"
#include "dsm2.h"
#include "dllyukon.h"
#include "porter.h"

#include "device.h"
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

#define DEBUG_PRINT_DECODE 0

// DLLIMPORT extern CtiConnection VanGoghConnection;

CtiDeviceWelco::CtiDeviceWelco() :
_deadbandsSent(false)
{}

CtiDeviceWelco::CtiDeviceWelco(const CtiDeviceWelco& aRef) :
_deadbandsSent(false)
{
    *this = aRef;
}

CtiDeviceWelco::~CtiDeviceWelco() {}

CtiDeviceWelco& CtiDeviceWelco::operator=(const CtiDeviceWelco& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        setDeadbandsSent( aRef.getDeadbandsSent() );
    }
    return *this;
}


INT CtiDeviceWelco::AccumulatorScan(CtiRequestMsg *pReq,
                                    CtiCommandParser &parse,
                                    OUTMESS *&OutMessage,
                                    RWTPtrSlist< CtiMessage > &vgList,
                                    RWTPtrSlist< CtiMessage > &retList,
                                    RWTPtrSlist< OUTMESS > &outList,
                                    INT ScanPriority)
{
    /*
     *  This is the WelCoFreeze code from the bad old daze.
     */

    INT         status      = NORMAL;

    if(OutMessage != NULL)
    {
        {
            /* Load the sectn to scan the demand accumulators */
            OutMessage->Buffer.OutMessage[5] = IDLC_FREEZE | 0x80;
            OutMessage->Buffer.OutMessage[6] = 0;

            /* Load all the other stuff that is needed */
            OutMessage->DeviceID              = getID();
            OutMessage->Buffer.OutMessage[4]  = 0x08;
            OutMessage->Port                  = getPortID();
            OutMessage->Remote                = getAddress();
            EstablishOutMessagePriority( OutMessage, ScanPriority );
            OutMessage->TimeOut               = 2;
            OutMessage->OutLength             = 0;
            OutMessage->InLength              = -1;

            if(OutMessage->Remote == RTUGLOBAL)
            {
                OutMessage->EventCode = NORESULT | ENCODED;
            }
            else
            {
                OutMessage->EventCode = RESULT | ENCODED;
            }

            OutMessage->Sequence              = 0;
            OutMessage->Retry                 = 2;

            setScanIntegrity(TRUE);                         // We are an integrity scan (equiv. anyway).  Data must be propagated.
            outList.insert(OutMessage);

            OutMessage = NULL;
        }
    }


    return status;
}

INT CtiDeviceWelco::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;

    if(getDeadbandsSent() == false)      // We are currently unsure whether a deadband request has ever been sent.
    {
        status = WelCoDeadBands(OutMessage, outList, MAXPRIORITY - 1);
    }

    if(OutMessage != NULL)
    {
        WelCoPoll(OutMessage, ScanPriority);

        /* Message is loaded so send it to porter */
        outList.insert(OutMessage);
        OutMessage = NULL;
    }

    return status;
}


INT CtiDeviceWelco::IntegrityScan(CtiRequestMsg *pReq,
                                  CtiCommandParser &parse,
                                  OUTMESS *&OutMessage,
                                  RWTPtrSlist< CtiMessage > &vgList,
                                  RWTPtrSlist< CtiMessage > &retList,
                                  RWTPtrSlist< OUTMESS > &outList,
                                  INT ScanPriority)
{
    INT      AIOffset = 0;

    USHORT   AnalogFirst = 0xffff;
    USHORT   AnalogLast = 0;

    USHORT   StatusFirst = 0xffff;
    USHORT   StatusLast = 0;

    USHORT   AccumFirst = 0xffff;
    USHORT   AccumLast = 0;

    INT         status      = NORMAL;

    if(OutMessage != NULL)
    {
        if(_pointMgr == NULL)      // Attached via the dev_base object.
        {
            RefreshDevicePoints(  );
        }

        if(_pointMgr != NULL)
        {
            LockGuard guard(monitor());

            /* Walk the point in memory db to see what the point range is */
            CtiRTDB<CtiPoint>::CtiRTDBIterator   itr_pt(_pointMgr->getMap());

            for(; ++itr_pt ;)
            {
                CtiPoint *PointRecord = itr_pt.value();

                switch(PointRecord->getType())
                {
                case StatusPointType:
                    {
                        CtiPointStatus *StatusPoint = (CtiPointStatus *)PointRecord;

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
                        CtiPointAnalog *AnalogPoint = (CtiPointAnalog *)PointRecord;

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
                        CtiPointAccumulator *AccumPoint = (CtiPointAccumulator *)PointRecord;

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

        if(!useScanFlags() || (isScanFrozen() || isScanFreezeFailed()))
        {
            /*
             *  This is our big hint that the message needs accums to be included!
             */
            OutMessage->Buffer.OutMessage[5] = IDLC_ACCUMDUMP;
            OutMessage->Buffer.OutMessage[6] = 2;
            OutMessage->Buffer.OutMessage[7] = LOBYTE (AccumFirst);
            OutMessage->Buffer.OutMessage[8] = LOBYTE (AccumLast);
            AIOffset = 4;

#ifdef DEBUG1
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Accum Scan of device " << getName() << " in progress " << endl;
            }
#endif
        }
#ifdef DEBUG1
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Integrity Scan of device " << getName() << " in progress " << endl;
        }
#endif


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
        OutMessage->DeviceID              = getID();
        OutMessage->Buffer.OutMessage[4]  = 0x08 | IDLC_NUL_HDR; // IDLC_NUL_HDR is used to request 32 bit accumulators

        OutMessage->Port                  = getPortID();
        OutMessage->Remote                = getAddress();
        EstablishOutMessagePriority( OutMessage, ScanPriority );
        OutMessage->TimeOut               = 2;

        if(!useScanFlags() || (isScanFrozen() || isScanFreezeFailed()))
        {
            OutMessage->OutLength = 16;
        }
        else
        {
            OutMessage->OutLength = 12;
        }

        OutMessage->InLength = -1;
        OutMessage->EventCode = RESULT | ENCODED;
        OutMessage->Sequence = 0;
        OutMessage->Retry = 2;

        setScanIntegrity(TRUE);                         // We are an integrity scan.  Data must be propagated.

        /* Message is loaded so send it to porter */
        outList.insert(OutMessage);
        OutMessage = NULL;
    }

    return status;
}

INT CtiDeviceWelco::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage >   &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
{
    LONG  PointOffset;

    CHAR  tStr[128];
    /* Misc. definitions */
    ULONG i;
    ULONG Pointer;
    PBYTE MyInMessage, SaveInMessage;

    /* Define the various records */
    CtiPoint          *PointRecord;
    CtiPointNumeric   *NumericPoint;

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
    resetScanPending();

    if(InMessage->InLength == 0)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << TimeNow << " Message returned for " << getName() << " is zero bytes in length " << endl;
        return READTIMEOUT;
    }

    MyInMessage = InMessage->Buffer.InMessage - 2;

    /* Check to see if this is a null response */
    if((*(InMessage->Buffer.InMessage - 3)) & 0x80)
    {
        // CtiLockGuard<CtiLogger> doubt_guard(dout);
        // dout << TimeNow << " " << getName() << " No exceptions.. " << endl;
        return(NORMAL);
    }

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

    /* Walk through the sectins */
    do
    {
        /* decode whatever message this is */
        switch(MyInMessage[0] & 0x7f)
        {
        case IDLC_CONFIGURATION:
            {
                break;
            }
        case IDLC_DIAGNOSTICS:
            {
                RWCString result("Diagnostic Scan\n");
                RWCString tstr = result;

                if(MyInMessage[2] & (EW_HDW_BIT))
                {
                    result += RWCString("Hardware error is indicated by RTU\n");
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Hardware error is indicated by RTU " << getName() << endl;
                }

                if(MyInMessage[2] & (EW_FMW_BIT))
                {
                    result += RWCString("Firmware error is indicated by RTU\n");
                    OutMessage = CTIDBG_new OUTMESS;

                    if(OutMessage != NULL)
                    {
                        InEchoToOut(InMessage, OutMessage);

                        /* This is the Big E so reset the RTU, download the deadbands and clear the demand accums */
                        if((i = WelCoReset(OutMessage, MAXPRIORITY)) != NORMAL)
                        {
                            /* Send Error to logger */
                            ReportError ((USHORT)i);
                        }
                        else
                        {
                            outList.insert(OutMessage);
                        }
                    }
                }

                if(MyInMessage[2] & (EW_FMW_BIT | EW_PWR_BIT))
                {
                    result += RWCString("Deadbands downloaded due to powerfail bit\n");

                    setDeadbandsSent(false);

                    if((i = WelCoDeadBands(InMessage, outList, MAXPRIORITY - 1)) != NORMAL)
                    {
                        /* Send Error to logger */
                        ReportError (i);
                    }
                    break;
                }

                if(MyInMessage[2] & (EW_FMW_BIT | EW_PWR_BIT | EW_SYN_BIT))
                {
                    result += RWCString("Time synchronization sent to RTU\n");
                    if((i = WelCoTimeSync (InMessage, outList, MAXPRIORITY - 1)) != NORMAL)
                    {
                        /* Send Error to logger */
                        ReportError (i);
                    }
                }

                if(tstr == result)
                {
                    result += RWCString("RTU indicates GOOD status\n");
                }

                ReturnMsg->setResultString(result);

                break;
            }
        case IDLC_FREEZE:
            {
                if(isScanFreezePending() || !useScanFlags())
                {
                    resetScanFreezePending();
                    setScanFrozen();

                    /* update the accumulator criteria for this RTU */
                    if(useScanFlags())
                    {
                        setPrevFreezeTime(getLastFreezeTime());
                        setLastFreezeTime( RWTime(InMessage->Time) );
                        resetScanFreezeFailed();

                        setPrevFreezeNumber(getLastFreezeNumber());
                        setLastFreezeNumber(TRUE);
                    }

                    /* then force a scan */
                    OutMessage = CTIDBG_new OUTMESS;

                    if(OutMessage != NULL)
                    {
                        InEchoToOut(InMessage, OutMessage);

                        CtiCommandParser parse(InMessage->Return.CommandStr);

                        int welcofreezedelay = gConfigParms.getValueAsInt("WELCO_FREEZE_TO_SCAN_MSEC_DELAY", 0);
                        if(welcofreezedelay)
                        {
                            Sleep(welcofreezedelay);
                        }

                        if((i = IntegrityScan (NULL, parse, OutMessage, vgList, retList, outList, MAXPRIORITY - 4)) != NORMAL)
                        {
                            ReportError ((USHORT)i); /* Send Error to logger */
                        }
                        else
                        {
                            setScanPending();
                        }
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Throwing away unexpected freeze response" << endl;
                    }
                    // What is this ??? DeviceRecord->ScanStatus &= SCANFREEZEFAILED;
                    setScanFreezeFailed();   // FIX FIX FIX 090799 CGP ?????
                    /* message for screwed up freeze */
                }

                break;
            }
        case IDLC_ACCUM32DUMP:
            {
                CtiPointAccumulator *pAccumPoint;

                StartPoint = MyInMessage[2] + 1;
                FinishPoint = MyInMessage[2] + ((MyInMessage[1] - 1) / 4);

                // get the current pulse count
                ULONG curPulseValue;

                if(isScanFreezePending())
                {

                    // This is a per device message, not per point....

                    for(PointOffset = (USHORT)StartPoint; PointOffset <= (USHORT)FinishPoint; PointOffset++)
                    {
                        if((PointRecord = getDevicePointOffsetTypeEqual(PointOffset, DemandAccumulatorPointType)) != NULL)
                        {
                            pAccumPoint = (CtiPointAccumulator *)PointRecord;

                            // Freeze Failed to dispatch message
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }


                        if((PointRecord = getDevicePointOffsetTypeEqual(PointOffset, PulseAccumulatorPointType)) != NULL)
                        {
                            pAccumPoint = (CtiPointAccumulator *)PointRecord;

                            // Freeze Failed to dispatch message
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
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
                else if(isScanFrozen() || !useScanFlags())
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

                        if(useScanFlags() && (PointRecord = getDevicePointOffsetTypeEqual(PointOffset, DemandAccumulatorPointType)) != NULL)
                        {
                            pAccumPoint = (CtiPointAccumulator *)PointRecord;

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

                                _snprintf(tStr, 126, "%s point %s = %f", getName(), pAccumPoint->getName(), PValue);

                                pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, DemandAccumulatorPointType, tStr);
                                if(pData != NULL)
                                {
                                    ReturnMsg->PointData().insert(pData);
                                    pData = NULL;  // We just put it on the list...
                                }
                            }
                        }

                        /* Check if there is a pulse point */
                        if((PointRecord = getDevicePointOffsetTypeEqual(PointOffset, PulseAccumulatorPointType)) != NULL)
                        {
                            pAccumPoint = (CtiPointAccumulator *)PointRecord;

                            /* Copy the pulses */
                            pAccumPoint->getPointHistory().setPreviousPulseCount(pAccumPoint->getPointHistory().getPresentPulseCount());
                            pAccumPoint->getPointHistory().setPresentPulseCount(curPulseValue);

                            /* Calculate in units/hour */
                            PValue = (FLOAT) pAccumPoint->getPointHistory().getPresentPulseCount() * pAccumPoint->getMultiplier();

                            /* Apply offset */
                            PValue += pAccumPoint->getDataOffset();

                            _snprintf(tStr, 126, "%s point %s = %f", getName(), pAccumPoint->getName(), PValue);

                            pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, PulseAccumulatorPointType, tStr);

                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().insert(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }
                    }

                    resetScanFrozen();
                    resetScanFreezeFailed();
                }

                break;
            }
        case IDLC_ACCUMDUMP:
            {
                CtiPointAccumulator *pAccumPoint;

                StartPoint = MyInMessage[2] + 1;
                FinishPoint = StartPoint + (MyInMessage[1] - 1 / 2) - 1;

                if(isScanFreezePending())
                {

                    // This is a per device message, not per point....

                    for(PointOffset = (USHORT)StartPoint; PointOffset <= (USHORT)FinishPoint; PointOffset++)
                    {
                        if((PointRecord = getDevicePointOffsetTypeEqual(PointOffset, DemandAccumulatorPointType)) != NULL)
                        {
                            pAccumPoint = (CtiPointAccumulator *)PointRecord;

                            // Freeze Failed to dispatch message
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }


                        if((PointRecord = getDevicePointOffsetTypeEqual(PointOffset, PulseAccumulatorPointType)) != NULL)
                        {
                            pAccumPoint = (CtiPointAccumulator *)PointRecord;

                            // Freeze Failed to dispatch message
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
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
                else if(isScanFrozen() || !useScanFlags())
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

                        if( useScanFlags() && PartHour != 0.0 && (PointRecord = getDevicePointOffsetTypeEqual(PointOffset, DemandAccumulatorPointType)) != NULL)
                        {
                            pAccumPoint = (CtiPointAccumulator *)PointRecord;

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

                                _snprintf(tStr, 126, "%s point %s = %f", getName(), pAccumPoint->getName(), PValue);

                                pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, DemandAccumulatorPointType, tStr);
                                if(pData != NULL)
                                {
                                    ReturnMsg->PointData().insert(pData);
                                    pData = NULL;  // We just put it on the list...
                                }

                                if(getDebugLevel() & DEBUGLEVEL_WELCO_PROTOCOL && PValue > 100000.0)
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                        dout << " " << getName() << " Demand Accum pt. " << pAccumPoint->getName() << " = " << PValue << endl;
                                        dout << " Previous Pulses   " << prevpulses << endl;
                                        dout << " Present Pulses    " << prespulses << endl;
                                        dout << " Point Multiplier  " << multiplier << endl;
                                        dout << " Point Data Offset " << dataoffset << endl;
                                        dout << " Part Hour         " << PartHour << endl;
                                    }
                                }
                            }
                        }

                        /* Check if there is a pulse point */
                        if((PointRecord = getDevicePointOffsetTypeEqual(PointOffset, PulseAccumulatorPointType)) != NULL)
                        {
                            pAccumPoint = (CtiPointAccumulator *)PointRecord;

                            /* Copy the pulses */
                            pAccumPoint->getPointHistory().setPreviousPulseCount(pAccumPoint->getPointHistory().getPresentPulseCount());
                            pAccumPoint->getPointHistory().setPresentPulseCount(MAKEUSHORT (MyInMessage[Pointer], MyInMessage[Pointer + 1]));

                            /* Calculate in units/hour */
                            PValue = (FLOAT) pAccumPoint->getPointHistory().getPresentPulseCount() * pAccumPoint->getMultiplier();

                            /* Apply offset */
                            PValue += pAccumPoint->getDataOffset();

                            _snprintf(tStr, 126, "%s point %s = %f", getName(), pAccumPoint->getName(), PValue);

                            pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, PulseAccumulatorPointType, tStr);
                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().insert(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }
                    }

                    resetScanFrozen();
                    resetScanFreezeFailed();
                }

                break;
            }
        case IDLC_STATUSDUMP:
            {
                StartPoint = MAKEUSHORT (MyInMessage[2], MyInMessage[3]) + 1;
                FinishPoint = StartPoint + ((MyInMessage[1] - 2) / 2) * 8;

                /* Now loop through and update received points as needed */
                for(PointOffset = StartPoint; PointOffset <= FinishPoint; PointOffset++)
                {
                    /* PointOffset is the offset is this RTU... */
                    if((PointRecord = getDevicePointOffsetTypeEqual(PointOffset, StatusPointType)) != NULL)
                    {
                        /* Apply offset */
                        if((MyInMessage[4 + 2 * ((PointOffset - StartPoint) / 8)] >> ((PointOffset - StartPoint) % 8)) & 0x01)
                        {
                            PValue = CLOSED;
                        }
                        else
                        {
                            PValue = OPENED;
                        }

#if (DEBUG_PRINT_DECODE > 0)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " " << PointRecord->getName() << " Status " << PointOffset << " is " << ResolveStateName(PointRecord->getStateGroupID(), (LONG)(PValue)) << endl;
                        }
#endif
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
                            PValue = ( (PValue == CLOSED) ? OPENED : CLOSED );
#if (DEBUG_PRINT_DECODE > 0)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " " << PointRecord->getName() << " Status " << PointOffset << " was " << ResolveStateName(PointRecord->getStateGroupID(), (LONG)(PValue)) << endl;
                            }
#endif
                        }

                        _snprintf(tStr, 126, "%s point %s = %s", getName(), PointRecord->getName(), ResolveStateName(PointRecord->getStateGroupID(), (LONG)(PValue)).data() );

                        pData = CTIDBG_new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, StatusPointType, tStr);
                        if(pData != NULL)
                        {
                            pData->setTime( pData->getTime() - 1 );
                            ReturnMsg->PointData().insert(pData);
                            pData = NULL;  // We just put it on the list...
                        }

                        /* Check if the "Changed Flag is high and toggle it back to the current status" */
                        if((MyInMessage[4 + 2 * ((PointOffset - StartPoint) / 8) + 1] >> ((PointOffset - StartPoint) % 8)) & 0x01)
                        {
                            PValue = ( (PValue == CLOSED) ? OPENED : CLOSED );

                            _snprintf(tStr, 126, "%s point %s = %s", getName(), PointRecord->getName(), ResolveStateName(PointRecord->getStateGroupID(), (LONG)(PValue)).data() );

                            pData = CTIDBG_new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, StatusPointType, tStr);

                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().insert(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }
                    }
                }

                break;
            }
        case IDLC_STATUSEXCEPTION:
            {
                /* Now loop through and update received points as needed */
                /* FinishPoint is used as COUNT of points in exception dump */
                FinishPoint = MyInMessage[1] / 2;
                /* StartPoint is used as the counter, and really holds the position rather than the actual offset point number */
                for(StartPoint = 0; StartPoint < FinishPoint; StartPoint++)
                {
                    /* Which offset is this in the EW Protocol spec... */
                    PointOffset = MAKEUSHORT (MyInMessage[(StartPoint * 2) + 2],
                                              MyInMessage[(StartPoint * 2) + 3] & 0x0f) + 1;

                    if((PointRecord = getDevicePointOffsetTypeEqual(PointOffset, StatusPointType)) != NULL)
                    {
                        /* get the present value */
                        if(MyInMessage[(StartPoint * 2) + 3]  & 0x80)
                        {
                            PValue = CLOSED;
                        }
                        else
                        {
                            PValue = OPENED;
                        }

#if (DEBUG_PRINT_DECODE > 0)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " " << PointRecord->getName() << " Status " << PointOffset << " is " << ResolveStateName(PointRecord->getStateGroupID(), (LONG)(PValue)) << endl;
                        }
#endif

                        /* Check if this is "changed" */
                        if(MyInMessage[(StartPoint * 2) + 3] & 0x40)
                        {
                            PValue = ( (PValue == CLOSED) ? OPENED : CLOSED );
#if (DEBUG_PRINT_DECODE > 0)
                            { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << RWTime() << " " << PointRecord->getName() << " Status " << PointOffset << " was " << ResolveStateName(PointRecord->getStateGroupID(), (LONG)(PValue)) << endl; }
#endif
                        }

                        _snprintf(tStr, 126, "%s point %s = %s", getName(), PointRecord->getName(), ResolveStateName(PointRecord->getStateGroupID(), (LONG)(PValue)).data() );

                        pData = CTIDBG_new CtiPointDataMsg(PointRecord->getPointID(),
                                                    PValue,
                                                    NormalQuality,
                                                    StatusPointType,
                                                    tStr);
                        if(pData != NULL)
                        {
                            ReturnMsg->PointData().insert(pData);
                            pData = NULL;  // We just put it on the list...
                        }

                        /* Check if this is "changed" */
                        if(MyInMessage[(StartPoint * 2) + 3] & 0x40)
                        {
                            PValue = ( (PValue == CLOSED) ? OPENED : CLOSED );
                            _snprintf(tStr, 126, "%s point %s = %s", getName(), PointRecord->getName(), ResolveStateName(PointRecord->getStateGroupID(), (LONG)(PValue)).data() );
                            pData = CTIDBG_new CtiPointDataMsg(PointRecord->getPointID(),
                                                        PValue,
                                                        NormalQuality,
                                                        StatusPointType,
                                                        tStr);
                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().insert(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }
                    }
                }

                break;
            }
        case IDLC_ANALOGDUMP:
            {
                StartPoint = MAKEUSHORT (MyInMessage[2], MyInMessage[3]) + 1;

                if((*(InMessage->Buffer.InMessage - 3)) & 0x08)
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
                    if((NumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(PointOffset, AnalogPointType)) != NULL)
                    {
                        /* update the point data */
                        if((*(InMessage->Buffer.InMessage - 3)) & 0x08)
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

                        _snprintf(tStr, 126, "%s point %s = %f", getName(), NumericPoint->getName(), PValue );

                        pData = CTIDBG_new CtiPointDataMsg(NumericPoint->getPointID(),
                                                    PValue,
                                                    NormalQuality,
                                                    AnalogPointType,
                                                    tStr);
                        if(pData != NULL)
                        {
                            ReturnMsg->PointData().insert(pData);
                            pData = NULL;  // We just put it on the list...
                        }
                    }
                }
                break;

            }
        case IDLC_ANALOGEXCEPTION:
            {

                // memcpy (PointRecord.getName(), DeviceRecord->getName(), STANDNAMLEN);
                // PointRecord.PointType = ANALOGPOINT;

                /* FinishPoint is used as count for analog exceptions */
                if((*(InMessage->Buffer.InMessage - 3)) & 0x08)
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
                    if((*(InMessage->Buffer.InMessage - 3)) & 0x08)
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
                    if((NumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(PointOffset, AnalogPointType)) != NULL)
                    {
                        if((*(InMessage->Buffer.InMessage - 3)) & 0x08)
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

                        _snprintf(tStr, 126, "%s point %s = %f", getName(), NumericPoint->getName(), PValue );

                        pData = CTIDBG_new CtiPointDataMsg(NumericPoint->getPointID(),
                                                    PValue,
                                                    NormalQuality,
                                                    AnalogPointType,
                                                    tStr);
                        if(pData != NULL)
                        {
                            ReturnMsg->PointData().insert(pData);
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
    if(!((*(InMessage->Buffer.InMessage - 3)) & 0x01))
    {
        OutMessage = CTIDBG_new OUTMESS;

        if(OutMessage != NULL)
        {
            InEchoToOut(InMessage, OutMessage);

            if((i = WelCoContinue (OutMessage, MAXPRIORITY - 4)) != NORMAL)
            {
                /* Send Error to logger */
                ReportError (i);
            }
            else
            {
                outList.insert(OutMessage);
                setScanPending();
            }
        }
    }

    resetScanIntegrity();

    if(ReturnMsg != NULL)
    {
        if(!(ReturnMsg->ResultString().isNull()) || ReturnMsg->getData().entries() > 0)
        {
            retList.append( ReturnMsg );
        }
        else
        {
            delete ReturnMsg;
        }
    }

    return(NORMAL);
}




/* Routine to error codes from a WelCo device */
INT CtiDeviceWelco::WelCoGetError(OUTMESS *OutMessage, INT Priority)            /* Priority to place command on queue */
{
    INT   status = NORMAL;

    /* Load the sectn to reset the RTU */
    OutMessage->Buffer.OutMessage[5] = IDLC_DIAGNOSTICS | 0x80;  // This is what it should be. CGP.
    OutMessage->Buffer.OutMessage[6] = 0;

    /* Load all the other stuff that is needed */
    OutMessage->DeviceID     = getID();
    OutMessage->Buffer.OutMessage[4] = 0x08;
    OutMessage->Port         = getPortID();
    OutMessage->Remote       = getAddress();
    EstablishOutMessagePriority( OutMessage, Priority );
    OutMessage->TimeOut      = 2;
    OutMessage->OutLength    = 0;
    OutMessage->InLength     = -1;
    OutMessage->EventCode    = RESULT | ENCODED;

    OutMessage->Sequence     = 0;
    OutMessage->Retry        = 2;

    return status;
}




/* Routine to output continue message to a WelCo device */
INT CtiDeviceWelco::WelCoContinue (OUTMESS *OutMessage, INT Priority)

{
    INT   status = NORMAL;

    if(getDebugLevel() & DEBUGLEVEL_WELCO_PROTOCOL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Issuing a continue message! " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    /* Load the sectn to scan the stati */
    OutMessage->Buffer.OutMessage[5] = IDLC_CONTINUE | 0x80;
    OutMessage->Buffer.OutMessage[6] = 0;

    /* Load all the other stuff that is needed */
    OutMessage->Buffer.OutMessage[4] = 0x08;

    OutMessage->DeviceID  = getID();
    OutMessage->Port      = getPortID();
    OutMessage->Remote    = getAddress();
    EstablishOutMessagePriority( OutMessage, Priority );
    OutMessage->TimeOut   = 2;
    OutMessage->OutLength = 0;
    OutMessage->InLength  = -1;
    OutMessage->EventCode = RESULT | ENCODED;
    OutMessage->Sequence  = 0;
    OutMessage->Retry     = 2;


    return status;
}


/* Routine to output continue message to a WelCo device */
INT CtiDeviceWelco::WelCoPoll (OUTMESS *OutMessage, INT Priority)
{
    INT   status = NORMAL;

    /* Load the sectn to scan the stati */
    OutMessage->Buffer.OutMessage[5] = IDLC_POLL | 0x80;
    OutMessage->Buffer.OutMessage[6] = 0;

    /* Load all the other stuff that is needed */
    OutMessage->Buffer.OutMessage[4] = 0x08 | IDLC_NUL_HDR;
    OutMessage->DeviceID     = getID();
    OutMessage->Port         = getPortID();
    OutMessage->Remote       = getAddress();
    EstablishOutMessagePriority( OutMessage, Priority );
    OutMessage->TimeOut      = 2;
    OutMessage->OutLength    = 0;
    OutMessage->InLength     = -1;
    OutMessage->EventCode    = RESULT | ENCODED;
    OutMessage->Sequence     = 0;
    OutMessage->Retry        = 2;


    return status;

}

INT CtiDeviceWelco::WelCoTimeSync(INMESS *InMessage, RWTPtrSlist< OUTMESS > &outList, INT Priority)
{
    INT   status = NORMAL;

    OUTMESS *OutMessage = CTIDBG_new OUTMESS;

    if(OutMessage != NULL)
    {
        InEchoToOut(InMessage, OutMessage);
        status = WelCoTimeSync(OutMessage, MAXPRIORITY - 1);

        if(status == NORMAL)
        {
            outList.insert(OutMessage);
        }
    }
    else
    {
        status = MEMORY;
    }

    return status;
}

/* Routine to send a time sync to a WelCo device */
INT CtiDeviceWelco::WelCoTimeSync(OUTMESS *OutMessage, INT Priority)
{
    INT   status = NORMAL;

    if(OutMessage != NULL)
    {

        OutMessage->Buffer.OutMessage[5] = IDLC_TIMESYNC | 0x80;
        OutMessage->Buffer.OutMessage[6] = 7;

        /* send a time sync to this guy */
        OutMessage->DeviceID           = getID();
        OutMessage->Port               = getPortID();
        OutMessage->Remote             = getAddress();
        OutMessage->TimeOut            = 2;
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
        status = MEMORY;
    }

    return status;
}


/* Routine to reset a WelCo device */
INT CtiDeviceWelco::WelCoReset(OUTMESS *OutMessage, INT Priority)
{
    INT   status = NORMAL;

    /* Load the sectn to reset the RTU */
    OutMessage->Buffer.OutMessage[5] = IDLC_RESET | 0x80;
    OutMessage->Buffer.OutMessage[6] = 3;
    OutMessage->Buffer.OutMessage[7] = 0;
    OutMessage->Buffer.OutMessage[8] = 0;
    OutMessage->Buffer.OutMessage[9] = 0;

    /* Load all the other stuff that is needed */
    OutMessage->Buffer.OutMessage[4] = 0x08;
    OutMessage->DeviceID     = getID();
    OutMessage->Port         = getPortID();
    OutMessage->Remote       = getAddress();
    EstablishOutMessagePriority( OutMessage, Priority );
    OutMessage->TimeOut      = 2;
    OutMessage->OutLength    = 3;
    OutMessage->InLength     = -1;
    OutMessage->EventCode    = NORESULT | ENCODED;

    OutMessage->Sequence     = 0;
    OutMessage->Retry        = 2;

    return status;
}


INT CtiDeviceWelco::WelCoDeadBands(INMESS *InMessage, RWTPtrSlist< OUTMESS > &outList, INT Priority)
{
    INT status = NORMAL;

    OUTMESS *OutMessage = CTIDBG_new OUTMESS;

    if(OutMessage != NULL)
    {
        InEchoToOut(InMessage, OutMessage);

        status = WelCoDeadBands(OutMessage, outList, MAXPRIORITY - 1);
    }
    else
    {
        status = MEMORY;
    }

    return status;
}

/* Routine to download deadbands for analogs */
INT CtiDeviceWelco::WelCoDeadBands(OUTMESS *OutMessage, RWTPtrSlist< OUTMESS > &outList, INT Priority)
{


    INT      Position;
    ULONG    ByteCount;

    USHORT   AnalogFirst = 0xffff;
    USHORT   AnalogLast = 0;

    CtiPoint *PointRecord;

    INT   status = NORMAL;

    OUTMESS *MyOutMessage = NULL;

    if(OutMessage != NULL)
    {
        if(_pointMgr == NULL)      // Attached via the dev_base object.
        {
            RefreshDevicePoints(  );
        }

        if(_pointMgr != NULL)
        {
            LockGuard guard(monitor());
            /* Walk the point database to see what the analog point range is */
            CtiRTDB<CtiPoint>::CtiRTDBIterator   itr_pt(_pointMgr->getMap());

            for(; ++itr_pt ;)
            {
                PointRecord = itr_pt.value();

                switch(PointRecord->getType())
                {
                case AnalogPointType:
                    {
                        CtiPointAnalog *Point = (CtiPointAnalog *)PointRecord;
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

                for(Position = AnalogFirst + 1; Position <= AnalogLast + 1 && status == NORMAL; Position++)
                {
                    CtiPointAnalog *Point = (CtiPointAnalog*)getDevicePointOffsetTypeEqual(Position, AnalogPointType);

                    /* Check for this one in the database and load the deadband */
                    if(Point != NULL && !Point->isPseudoPoint())
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
                        MyOutMessage->DeviceID                 = getID();
                        MyOutMessage->Port                     = getPortID();
                        MyOutMessage->Remote                   = getAddress();
                        OverrideOutMessagePriority( MyOutMessage, Priority );
                        MyOutMessage->TimeOut                  = 2;
                        MyOutMessage->OutLength                = ByteCount + 2;
                        MyOutMessage->InLength                 = -1;
                        MyOutMessage->EventCode                = RESULT | ENCODED;
                        MyOutMessage->Sequence                 = 0;
                        MyOutMessage->Retry                    = 2;

                        outList.insert(MyOutMessage);
                        MyOutMessage = NULL;                // Out of our hands now...

                        if(Position != (AnalogLast + 1))
                        {
                            MyOutMessage = CTIDBG_new OUTMESS( *OutMessage );

                            if(MyOutMessage == NULL)
                            {
                                status = MEMORY;
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
                status = MEMORY;
            }
        }
    }

    return status;
}



INT CtiDeviceWelco::ErrorDecode(INMESS *InMessage,
                                RWTime &TimeNow,
                                RWTPtrSlist< CtiMessage >   &vgList,
                                RWTPtrSlist< CtiMessage > &retList,
                                RWTPtrSlist<OUTMESS> &outList)
{
    INT nRet = NoError;

#ifdef OLD_WAY
    memcpy (PointRecord.getName(), DeviceRecord.getName(), STANDNAMLEN);
    if(!(PointgetDeviceFirst (&PointRecord)))
    {
        do
        {
            /* Load up the common parts of the DRP message */
            memcpy (DRPValue.getName(), PointRecord.getName(), STANDNAMLEN);
            memcpy (DRPValue.PointName, PointRecord.PointName, STANDNAMLEN);
            DRPValue.TimeStamp = InMessage.Time;

            switch(PointRecord.PointType)
            {
            case ACCUMULATORPOINT:
            case DEMANDACCUMPOINT:
                if(DoAccums)
                {
                    DRPValue.Type = DRPTYPEVALUE;

                    PointLock (&PointRecord);

                    /* Move the pulse count */
                    PointRecord.PreviousPulses = PointRecord.PresentPulses;

                    /* Clear the pulse counts */
                    PointRecord.PresentPulses = 0;

                    CheckDataStateQuality (&DeviceRecord,
                                           &PointRecord,
                                           &PValue,
                                           PLUGGED,
                                           InMessage.Time,
                                           InMessage.MilliTime & DSTACTIVE,
                                           NORMAL,
                                           LogFlag);
                }

                break;

            case ANALOGPOINT:
                DRPValue.Type = DRPTYPEVALUE;
                PointLock (&PointRecord);

                CheckDataStateQuality (&DeviceRecord,
                                       &PointRecord,
                                       &PValue,
                                       PLUGGED,
                                       InMessage.Time,
                                       InMessage.MilliTime & DSTACTIVE,
                                       NORMAL,
                                       LogFlag);

                break;

            case TWOSTATEPOINT:
                DRPValue.Type = DRPTYPEVALUE;
                PointLock (&PointRecord);

                CheckDataStateQuality (&DeviceRecord,
                                       &PointRecord,
                                       &PValue,
                                       PLUGGED,
                                       InMessage.Time,
                                       InMessage.MilliTime & DSTACTIVE,
                                       NORMAL,
                                       LogFlag);


                break;

            case THREESTATEPOINT:
                DRPValue.Type = DRPTYPEVALUE;
                PointLock (&PointRecord);

                CheckDataStateQuality (&DeviceRecord,
                                       &PointRecord,
                                       &PValue,
                                       PLUGGED,
                                       InMessage.Time,
                                       InMessage.MilliTime & DSTACTIVE,
                                       NORMAL,
                                       LogFlag);


                break;

            default:
                continue;
            }

            /* Send the point to drp */
            DRPValue.Value = PointRecord.CurrentValue;
            DRPValue.Quality = PointRecord.CurrentQuality;
            DRPValue.AlarmState = PointRecord.AlarmStatus;

            SendDRPPoint (&DRPValue);
        } while(!(PointgetDeviceNext (&PointRecord)));
    }
#else

    CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

    if(pMsg != NULL)
    {
        pMsg->insert( -1 );                 // This is the dispatch token and is unimplemented at this time
        pMsg->insert(OP_DEVICEID);          // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
        pMsg->insert(getID());        // The id (device or point which failed)
        pMsg->insert(ScanRateGeneral);      // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

        if(InMessage->EventCode != 0)
        {
            pMsg->insert(InMessage->EventCode);
        }
        else
        {
            pMsg->insert(GeneralScanAborted);
        }

        retList.insert( pMsg );
    }

#endif

    return nRet;
}

INT CtiDeviceWelco::ExecuteRequest(CtiRequestMsg                  *pReq,
                                   CtiCommandParser               &parse,
                                   OUTMESS                        *&OutMessage,
                                   RWTPtrSlist< CtiMessage >      &vgList,
                                   RWTPtrSlist< CtiMessage >      &retList,
                                   RWTPtrSlist< OUTMESS >         &outList)
{
    INT nRet = NORMAL;
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
                    outList.insert(OutMTemp);
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
                                         CtiNumStr(TimeSt.tm_mon + 1) + "/" +
                                         CtiNumStr(TimeSt.tm_mday) + " " +
                                         CtiNumStr(TimeSt.tm_hour).zpad(2) + ":" +
                                         CtiNumStr(TimeSt.tm_min).zpad(2) + ":" +
                                         CtiNumStr(TimeSt.tm_sec).zpad(2));
                }

                retList.insert( ret );

                outList.insert(OutMessage);
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
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
            nRet = NoExecuteRequestMethod;
            /* Set the error value in the base class. */
            // FIX FIX FIX 092999
            retList.insert( CTIDBG_new CtiReturnMsg(getID(),
                                             RWCString(OutMessage->Request.CommandStr),
                                             RWCString("Welco Devices do not support this command (yet?)"),
                                             nRet,
                                             OutMessage->Request.RouteID,
                                             OutMessage->Request.MacroOffset,
                                             OutMessage->Request.Attempt,
                                             OutMessage->Request.TrxID,
                                             OutMessage->Request.UserID,
                                             OutMessage->Request.SOE,
                                             RWOrdered()));

            break;
        }
    }

    return nRet;
}

bool CtiDeviceWelco::getDeadbandsSent() const
{
    return _deadbandsSent;
}
CtiDeviceWelco& CtiDeviceWelco::setDeadbandsSent(const bool b)
{
    _deadbandsSent = b;
    return *this;
}


INT CtiDeviceWelco::executeControl(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    if(!isInhibited())
    {
        CtiPointStatus *ctlPoint = 0;
        INT ctlpt = parse.getiValue("point");
        INT controlState;

        if(ctlpt < 0)
        {
            // Must have provided only a name... Find it the hard way.
            RWCString pname = parse.getsValue("point");
            ctlPoint = (CtiPointStatus*)getDevicePointEqualByName(pname);
        }
        else
        {
            ctlPoint = (CtiPointStatus*)getDevicePointEqual(ctlpt);
        }


        if(ctlPoint)
        {
            if(ctlPoint->getPointStatus().getControlType() > NoneControlType &&
               ctlPoint->getPointStatus().getControlType() < InvalidControlType)
            {
                INT ctloffset = ctlPoint->getPointStatus().getControlOffset();

                if( !ctlPoint->getPointStatus().getControlInhibit() )
                {
                    if(INT_MIN == ctlpt || !(parse.getFlags() & (CMD_FLAG_CTL_CLOSE | CMD_FLAG_CTL_OPEN)))
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  Poorly formed control message.  Specify select pointid and open or close" << endl;
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
                        if( parse.getCommandStr().contains(ctlPoint->getPointStatus().getStateZeroControl(), RWCString::ignoreCase) )       //  (parse.getFlags() & CMD_FLAG_CTL_OPEN)
                        {
                            controlState = STATEZERO;
                            MyOutMessage->Buffer.OutMessage[8] = LOBYTE (ctlPoint->getPointStatus().getCloseTime1() / 10);
                            MyOutMessage->Buffer.OutMessage[9] = (HIBYTE (ctlPoint->getPointStatus().getCloseTime1() / 10) & 0x3f) | ((parse.getFlags() & CMD_FLAG_CTL_OPEN) ? EW_TRIP_MASK : EW_CLOSE_MASK);
                        }
                        else if( parse.getCommandStr().contains(ctlPoint->getPointStatus().getStateOneControl(), RWCString::ignoreCase) )  // (parse.getFlags() & CMD_FLAG_CTL_CLOSE)
                        {
                            controlState = STATEONE;
                            MyOutMessage->Buffer.OutMessage[8] = LOBYTE (ctlPoint->getPointStatus().getCloseTime2() / 10);
                            MyOutMessage->Buffer.OutMessage[9] = (HIBYTE (ctlPoint->getPointStatus().getCloseTime2() / 10) & 0x3f) | ((parse.getFlags() & CMD_FLAG_CTL_OPEN) ? EW_TRIP_MASK : EW_CLOSE_MASK);
                        }
                        else
                        {
                            delete (MyOutMessage);
                            return(BADSTATE);
                        }

                        outList.insert( MyOutMessage );

                        /* Load all the other stuff that is needed */
                        OutMessage->Retry = 0;
                        OutMessage->OutLength = 1;
                        OutMessage->Sequence    = 0;

                        /* Set up the SBO Execute */
                        OutMessage->Buffer.OutMessage[5] = IDLC_SBOEXECUTE | 0x80;
                        OutMessage->Buffer.OutMessage[6] = 1;
                        OutMessage->Buffer.OutMessage[7] = LOBYTE (ctloffset - 1);

                        /* Sent the message on to the remote */
                        outList.insert( OutMessage );
                        OutMessage = 0;

                        CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg ( ctlPoint->getDeviceID(), ctlPoint->getPointID(), controlState, RWTime(), -1, 100 );

                        hist->setMessagePriority( hist->getMessagePriority() + 1 );
                        vgList.insert( hist );

                        if(ctlPoint->isPseudoPoint())
                        {
                            // There is no physical point to observe and respect.  We lie to the control point.
                            CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(ctlPoint->getID(),
                                                                         (DOUBLE)controlState,
                                                                         NormalQuality,
                                                                         StatusPointType,
                                                                         RWCString("This point has been controlled"));
                            pData->setUser( pReq->getUser() );
                            vgList.insert(pData);
                        }

                        retList.insert( CTIDBG_new CtiReturnMsg(getID(), parse.getCommandStr(), RWCString("Command submitted to port control")) );
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  Control Point " << ctlPoint->getName() << " is disabled" << endl;
                    }
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << getName() << " Control point " << ctlpt << " does not exist" << endl;
            }
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << getName() << " is disabled" << endl;
        }
    }


    return status;
}

INT CtiDeviceWelco::RefreshDevicePoints()
{
    setDeadbandsSent(false);                    // Make them go again on next general scan!!
    return Inherited::RefreshDevicePoints();
}

