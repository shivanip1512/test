

#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_ilex
*
* Date:   2/15/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_welco.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2002/05/21 15:09:12 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>

#include "dsm2.h"
#include "porter.h"

#include "device.h"
#include "dev_ilex.h"
#include "yukon.h"
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
#include "cmdparse.h"

#include "dlldefs.h"
#include "utility.h"

#define DEBUG_PRINT_DECODE 0

CtiDeviceILEX::CtiDeviceILEX() : _freezeNumber(0)
{}

CtiDeviceILEX::CtiDeviceILEX(const CtiDeviceILEX& aRef)
{
    *this = aRef;
}

CtiDeviceILEX::~CtiDeviceILEX() {}

CtiDeviceILEX& CtiDeviceILEX::operator=(const CtiDeviceILEX& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        _freezeNumber = aRef.getFreezeNumber();
    }
    return *this;
}


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

    return(NORMAL);
}

INT CtiDeviceILEX::AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;

    if(OutMessage != NULL)
    {
        /* Load the freeze message */
        header(OutMessage->Buffer.OutMessage + PREIDLEN, ILEXFREEZE, 0, 1);

        /* Load all the other stuff that is needed */
        OutMessage->DeviceID              = getID();
        OutMessage->Port                  = getPortID();
        OutMessage->Remote                = getAddress();
        OutMessage->TimeOut               = 2;
        OutMessage->OutLength             = 3;
        OutMessage->InLength              = -1;
        OutMessage->EventCode             = RESULT | ENCODED;
        OutMessage->Sequence              = 0;
        OutMessage->Retry                 = 3;

        OutMessage->Buffer.OutMessage[9]  = getFreezeNumber();
        EstablishOutMessagePriority( OutMessage, (MAXPRIORITY - 3) );

        setScanIntegrity(TRUE);                         // We are an integrity scan (equiv. anyway).  Data must be propagated.

        outList.insert(OutMessage);
        OutMessage = NULL;
    }

    return status;
}

INT CtiDeviceILEX::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;

    if(OutMessage != NULL)
    {
        /* Load the forced scan message */
        header(OutMessage->Buffer.OutMessage + PREIDLEN, ILEXSCAN, !getIlexSequenceNumber(), FORCED_SCAN);

        /* Load all the other stuff that is needed */
        OutMessage->DeviceID              = getID();
        OutMessage->Port                  = getPortID();
        OutMessage->Remote                = getAddress();
        OutMessage->Priority              = (UCHAR)(ScanPriority);
        OutMessage->TimeOut               = 2;
        OutMessage->OutLength             = 2;
        OutMessage->InLength              = -1;
        OutMessage->EventCode             = RESULT | ENCODED;
        OutMessage->Sequence              = 0;
        OutMessage->Retry                 = 2;

        outList.insert(OutMessage);
        OutMessage = NULL;
    }

    return status;
}

INT CtiDeviceILEX::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;

    if(OutMessage != NULL)
    {
        /* Load the forced scan message */
        header(OutMessage->Buffer.OutMessage + PREIDLEN, ILEXSCAN, getIlexSequenceNumber(), FORCED_SCAN);

        /* Load all the other stuff that is needed */
        OutMessage->DeviceID              = getID();
        OutMessage->Port                  = getPortID();
        OutMessage->Remote                = getAddress();
        EstablishOutMessagePriority( OutMessage, ScanPriority );
        OutMessage->TimeOut               = 2;
        OutMessage->OutLength             = 2;
        OutMessage->InLength              = -1;
        OutMessage->EventCode             = RESULT | ENCODED;
        OutMessage->Sequence              = 0;
        OutMessage->Retry                 = 2;

        outList.insert(OutMessage);
        OutMessage = NULL;
    }

    return status;
}


INT CtiDeviceILEX::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage >   &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
{
    INT             status = NORMAL;
    CtiPoint        *PointRecord;
    CtiPointNumeric *NumericPoint;
    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointAccumulator *pAccumPoint = NULL;

    CHAR  tStr[128];
    INT AIPointOffset;

    CtiPointDataMsg *pData = NULL;
    CtiConnection   *Conn = ((CtiConnection*)InMessage->Return.Connection);
    OUTMESS         *OutMessage = NULL;


    /* Misc. definitions */
    ULONG i, j;

    /* Variables for decoding ILEX Messages */
    USHORT  Offset;
    USHORT  NumAnalogs;
    USHORT  NumAccum;
    USHORT  NumStatus;
    USHORT  NumSOE;
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

    if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }
    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

    /* decode whatever message this is */
    switch(InMessage->Buffer.InMessage[0] & 0x07)
    {
    case ILEXFREEZE:
        {
            if(isScanFreezePending())
            {
                resetScanFreezePending();
                setScanFrozen();
                setPrevFreezeTime(getLastFreezeTime());
                setLastFreezeTime( RWTime(InMessage->Time) );
                setPrevFreezeNumber( getLastFreezeNumber() );
                setLastFreezeNumber(InMessage->Buffer.InMessage[2]);
                resetScanFreezeFailed();

                /* then force a scan */
                OutMessage = new OUTMESS;

                if(OutMessage != NULL)
                {
                    InEchoToOut(InMessage, OutMessage);

                    CtiCommandParser parse(InMessage->Return.CommandStr);

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
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << RWTime() << " Throwing away unexpected freeze response" << endl;
                }
                setScanFreezeFailed();   // FIX FIX FIX 090799 CGP ?????
                /* message for screwed up freeze */
            }
            break;
        }
    case ILEXSCAN:
        {
            if(isScanPending())
            {
                resetScanPending();

                /* update the scan time */
                // 04302002 CGP These don't seem to be used anywhere around here...
                // DeviceRecord->LastFullScan      = TimeB->time;
                // DeviceRecord->LastExceptionScan = TimeB->time;

                /* The moron's at ILEX can't seem to keep straight the ACK/NACK bit so force it */
                InMessage->Buffer.InMessage[0] |= 0x10;

            }
            else
            {
                /* Something screwed up message goes here */
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }

#if 0
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);

                char oldfill = dout.fill('0');

                dout << RWTime() << " Ilex Data:" << endl;
                for(i=0; i < 64; i++)
                {
                    if(i && !(i % 10)) dout << endl;
                    dout << hex << setw(2) << (int)InMessage->Buffer.InMessage[i] << dec << " ";
                }
                dout << RWTime() << " Ilex Data Complete" << endl;
                dout.fill(oldfill);
            }
#endif



            // !!! FALL THROUGH FALL THROUGH FALL THROUGH !!!

        }
    case ILEXSCANPARTIAL:
        {
            if((InMessage->Buffer.InMessage[0] & 0x07) == ILEXSCANPARTIAL)
            {
                /* do an exception scan */
                OutMessage = new OUTMESS;

                if(OutMessage != NULL)
                {
                    InEchoToOut(InMessage, OutMessage);

                    CtiCommandParser parse(InMessage->Return.CommandStr);

                    setIlexSequenceNumber( InMessage->Buffer.InMessage[0] & 0x10 );
                    // if((i = ILEXExceptionScan (RemoteRecord, DeviceRecord, InMessage->Buffer.InMessage[0] & 0x10, MAXPRIORITY - 4)) != NORMAL)            }
                    if((i = exceptionScan(OutMessage, MAXPRIORITY - 4)) != NORMAL)
                    {
                        ReportError ((USHORT)i); /* Send Error to logger */
                    }
                    else
                    {
                        setScanPending();
                    }
                }
            }

            if(InMessage->Buffer.InMessage[2])
            {
                Offset      = 3;
                NumAnalogs  = InMessage->Buffer.InMessage[2];
                NumStatus   = 0;
                NumAccum    = 0;
                NumSOE      = 0;
            }
            else
            {
                /* How many of each type is here? */
                Offset      = 5;
                NumAnalogs  = InMessage->Buffer.InMessage[3];
                NumStatus   = InMessage->Buffer.InMessage[4] & 0x3f;

                if( InMessage->Buffer.InMessage[4] & 0x40 )
                {
                    Offset  += 1;
                    NumAccum = InMessage->Buffer.InMessage[5];
                    if( InMessage->Buffer.InMessage[4] & 0x80 )
                    {
                        Offset      += 2;
                        NumAnalogs  += InMessage->Buffer.InMessage[6];
                        NumSOE      = InMessage->Buffer.InMessage[7];
                    }
                    else
                        NumSOE      = 0;
                }
                else
                {
                    NumAccum        = 0;
                    if( InMessage->Buffer.InMessage[4] & 0x80 )
                    {
                        Offset      += 2;
                        NumAnalogs  += InMessage->Buffer.InMessage[5];
                        NumSOE      = InMessage->Buffer.InMessage[6];
                    }
                    else
                        NumSOE      = 0;
                }
            }

            /* now check if we need to plug accums because of missed freeze */
            if(((isScanFrozen()) || (isScanFreezeFailed())) && (NumAccum == 0))
            {
                /* make sure this guy is marked as a bad freeze */
                setLastFreezeNumber( 0 );

                if(_pointMgr == NULL)      // Attached via the dev_base object.
                {
                    RefreshDevicePoints();
                }

                if(_pointMgr != NULL)
                {
                    LockGuard guard(monitor());

                    /* Walk the point in memory db to see what the point range is */
                    CtiRTDB<CtiPoint>::CtiRTDBIterator   itr_pt(_pointMgr->getMap());

                    for(; ++itr_pt ;)
                    {
                        PointRecord = itr_pt.value();
                        RWRecursiveLock<RWMutexLock>::LockGuard pGuard( PointRecord->getMux() );

                        switch(PointRecord->getType())
                        {
                        case StatusPointType:
                        case AnalogPointType:
                        case PulseAccumulatorPointType:
                        case DemandAccumulatorPointType:
                            {
                                CtiPointAccumulator *AccumPoint = (CtiPointAccumulator *)PointRecord;
                                break;
                            }
                        }
                    }
                }

#if 0
                /* loop through the accums for this Remote */
                PointRecord.PointType = ACCUMULATORPOINT;
                if(!(PointGetDeviceTypeFirst (&PointRecord)))
                {
                    do
                    {
                        PointLock (&PointRecord);

                        /* BDW - The line below was moved above checkdatastatquality because that function updates the database. */
                        /* Move the pulse count */
                        PointRecord.PreviousPulses = PointRecord.PresentPulses;

                        CheckDataStateQuality (DeviceRecord, &PointRecord, &PValue, NORMAL, TimeB->time, TimeB->dstflag, DATAFREEZEFAIL, LogFlag);


                        /* Send the point to feedback */
                        memcpy (DRPValue.DeviceName, PointRecord.DeviceName, STANDNAMLEN);
                        memcpy (DRPValue.PointName, PointRecord.PointName, STANDNAMLEN);
                        DRPValue.Quality = PointRecord.CurrentQuality;
                        DRPValue.AlarmState = PointRecord.AlarmStatus;
                        DRPValue.Value = PointRecord.CurrentValue;
                        DRPValue.TimeStamp = PointRecord.CurrentTime;
                        DRPValue.Type = DRPTYPEVALUE;

                        SendDRPPoint (&DRPValue);

                    } while(!(PointGetDeviceTypeNext (&PointRecord)));
                }
                /* loop through the demamd accums for this Remote */
                memcpy (PointRecord.DeviceName, DeviceRecord->DeviceName, STANDNAMLEN);
                PointRecord.PointType = DEMANDACCUMPOINT;
                if(!(PointGetDeviceTypeFirst (&PointRecord)))
                {
                    do
                    {
                        PointLock (&PointRecord);

                        /* BDW - The line below was moved above checkdatastatquality
                                because that function updates the database. */
                        /* Move the pulse count */
                        PointRecord.PreviousPulses = PointRecord.PresentPulses;

                        CheckDataStateQuality (DeviceRecord, &PointRecord, &PValue, NORMAL, TimeB->time, TimeB->dstflag, DATAFREEZEFAIL, LogFlag);

                        /* Send the point to feedback */
                        memcpy (DRPValue.DeviceName, PointRecord.DeviceName, STANDNAMLEN);
                        memcpy (DRPValue.PointName, PointRecord.PointName, STANDNAMLEN);
                        DRPValue.Quality = PointRecord.CurrentQuality;
                        DRPValue.AlarmState = PointRecord.AlarmStatus;
                        DRPValue.Value = PointRecord.CurrentValue;
                        DRPValue.TimeStamp = PointRecord.CurrentTime;
                        DRPValue.Type = DRPTYPEVALUE;

                        SendDRPPoint (&DRPValue);
                    } while(!(PointGetDeviceTypeNext (&PointRecord)));
                }
#endif

                resetScanFrozen();
                resetScanFreezeFailed();
            }

            /* Now process them in order */
            if(NumStatus)
            {

                for(i = 0; i < NumStatus; i++)
                {
                    StartStatus = InMessage->Buffer.InMessage[Offset] * 16;
                    dout << RWTime() << " Start status = " << StartStatus << endl;

#if 1
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        char oldfill = dout.fill('0');
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

                        for(j = 0; j < 7; j++)
                        {
                            dout << "0x" << hex << setw(2) << (int)InMessage->Buffer.InMessage[Offset + j] << " " << dec << endl;
                        }
                        dout << endl;

                        dout.fill(oldfill);
                    }
#endif

                    for(j = 1; j <= 16; j++)
                    {
                        /* Get the Status Record */


                        if( (PointRecord = getDevicePointOffsetTypeEqual( i, StatusPointType )) != NULL)
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
                                State1 = InMessage->Buffer.InMessage[Offset + 1] >> (j - 1) & 0x0001;
                                State2 = InMessage->Buffer.InMessage[Offset + 3] >> (j - 1) & 0x0001;
                                State3 = InMessage->Buffer.InMessage[Offset + 5] >> (j - 1) & 0x0001;
                            }
                            else
                            {
                                State1 = InMessage->Buffer.InMessage[Offset + 2] >> (j - 9) & 0x0001;
                                State2 = InMessage->Buffer.InMessage[Offset + 4] >> (j - 9) & 0x0001;
                                State3 = InMessage->Buffer.InMessage[Offset + 6] >> (j - 9) & 0x0001;
                            }

                            /* Update the records */

                            if(State1)
                                Value = CLOSED;
                            else
                                Value = OPENED;

                            PValue = (FLOAT) Value;

                            sprintf(tStr, "%s point %s = %s", getName(), PointRecord->getName(), ((PValue == OPENED) ? "OPENED" : "CLOSED") );

                            pData = new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, StatusPointType, tStr);

                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().insert(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }
                    }
                    Offset += 7;
                }
            }

            if(NumAccum)
            {
                if((InMessage->Buffer.InMessage[0] & 0x07) == ILEXSCAN)
                {
                    if(isScanFrozen() || isScanFreezeFailed())
                    {
                        if( getScanRate(ScanRateGeneral) < getScanRate(ScanRateAccum) )
                        {
                            /* Force a continuation to clean that mother out */
                            OutMessage = new OUTMESS;

                            if(OutMessage != NULL)
                            {
                                InEchoToOut(InMessage, OutMessage);

                                CtiCommandParser parse(InMessage->Return.CommandStr);
                                setIlexSequenceNumber( InMessage->Buffer.InMessage[0] & 0x10 );

                                if((i = exceptionScan(OutMessage, MAXPRIORITY - 4)) != NORMAL)
                                {
                                    ReportError ((USHORT)i); /* Send Error to logger */
                                }
                                else
                                {
                                    setScanPending();
                                }
                            }
                        }
                    }
                }

                /* Check the freeze number */
                if(
                  ((getLastFreezeNumber() != InMessage->Buffer.InMessage[Offset]) && isScanFrozen()) ||
                  (isScanFreezeFailed() && getPrevFreezeNumber() + 1 != 0 && getPrevFreezeNumber() + 1 != InMessage->Buffer.InMessage[Offset]) ||
                  (isScanFreezeFailed() && getPrevFreezeNumber() + 1 == 0 && getPrevFreezeNumber() + 2 != InMessage->Buffer.InMessage[Offset])
                  )
                {
                    /* Process wrong freeze number */
                    Offset += (2 + (NumAccum * 2));

                    /* make sure this guy is marked as a bad freeze */
                    setLastFreezeNumber( 0 );

#if 1

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << " " << getName() << " Bad freeze number!" << endl;
                    }

#else
                    /* loop through the demand accums for this Remote */
                    memcpy (PointRecord.DeviceName, DeviceRecord->DeviceName, STANDNAMLEN);
                    PointRecord.PointType = DEMANDACCUMPOINT;
                    if(!(PointGetDeviceTypeFirst (&PointRecord)))
                    {
                        do
                        {

                            PointLock (&PointRecord);

                            /* BDW - The line below was moved above checkdatastatquality because that function updates the database. */
                            /* Move the pulse count */
                            PointRecord.PreviousPulses = PointRecord.PresentPulses;

                            CheckDataStateQuality (DeviceRecord, &PointRecord, &PValue, NORMAL, TimeB->time, TimeB->dstflag, DATAFREEZEFAIL, LogFlag);

                            /* Send the point to feedback */
                            memcpy (DRPValue.DeviceName, PointRecord.DeviceName, STANDNAMLEN);
                            memcpy (DRPValue.PointName, PointRecord.PointName, STANDNAMLEN);
                            DRPValue.Quality = PointRecord.CurrentQuality;
                            DRPValue.AlarmState = PointRecord.AlarmStatus;
                            DRPValue.Value = PointRecord.CurrentValue;
                            DRPValue.TimeStamp = PointRecord.CurrentTime;
                            DRPValue.Type = DRPTYPEVALUE;

                            SendDRPPoint (&DRPValue);
                        } while(!(PointGetDeviceTypeNext (&PointRecord)));
                    }

                    /* loop through the accums for this Remote */
                    memcpy (PointRecord.DeviceName, DeviceRecord->DeviceName, STANDNAMLEN);
                    PointRecord.PointType = ACCUMULATORPOINT;
                    if(!(PointGetDeviceTypeFirst (&PointRecord)))
                    {
                        do
                        {

                            PointLock (&PointRecord);

                            /* BDW - The line below was moved above checkdatastatquality
                                     because that function updates the database. */
                            /* Move the pulse count */
                            PointRecord.PreviousPulses = PointRecord.PresentPulses;

                            CheckDataStateQuality (DeviceRecord, &PointRecord, &PValue, NORMAL, TimeB->time, TimeB->dstflag, DATAFREEZEFAIL, LogFlag);

                            /* Send the point to feedback */
                            memcpy (DRPValue.DeviceName, PointRecord.DeviceName, STANDNAMLEN);
                            memcpy (DRPValue.PointName, PointRecord.PointName, STANDNAMLEN);
                            DRPValue.Quality = PointRecord.CurrentQuality;
                            DRPValue.AlarmState = PointRecord.AlarmStatus;
                            DRPValue.Value = PointRecord.CurrentValue;
                            DRPValue.TimeStamp = PointRecord.CurrentTime;
                            DRPValue.Type = DRPTYPEVALUE;

                            SendDRPPoint (&DRPValue);
                        } while(!(PointGetDeviceTypeNext (&PointRecord)));
                    }

#endif
                }
                else
                {
                    // get the current pulse count
                    ULONG curPulseValue;

                    /* mark the freeze as valid */
                    if(isScanFreezeFailed())
                        setLastFreezeNumber(InMessage->Buffer.InMessage[Offset]);

                    /* Calculate the part of an hour involved here */
                    PartHour = (FLOAT) (getLastFreezeTime().seconds() - getPrevFreezeTime().seconds());
                    PartHour /= (3600.0);

                    /* Loop through the accumulator records */
                    StartAccum = InMessage->Buffer.InMessage[Offset + 1];
                    EndAccum = StartAccum + NumAccum;
                    Offset += 2;

                    for(AIPointOffset = StartAccum + 1; AIPointOffset <= EndAccum; AIPointOffset++)
                    {
                        if((pAccumPoint = (CtiPointAccumulator *)getDevicePointOffsetTypeEqual(AIPointOffset, DemandAccumulatorPointType)) != NULL)
                        {
                            curPulseValue = MAKEUSHORT(InMessage->Buffer.InMessage[Offset], InMessage->Buffer.InMessage[Offset + 1]);

                            /* Copy the pulses */
                            pAccumPoint->getPointHistory().setPreviousPulseCount(pAccumPoint->getPointHistory().getPresentPulseCount());
                            pAccumPoint->getPointHistory().setPresentPulseCount(curPulseValue);

                            if(!(getPrevFreezeNumber()) || !(getLastFreezeNumber()))
                            {
                                // Inform dispatch that the point pump has just been primed.
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                            }
                            else
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

                                sprintf(tStr, "%s point %s = %f", getName(), pAccumPoint->getName(), PValue);

                                pData = new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, DemandAccumulatorPointType, tStr);
                                if(pData != NULL)
                                {
                                    ReturnMsg->PointData().insert(pData);
                                    pData = NULL;  // We just put it on the list...
                                }
                            }
                        }

                        if((pAccumPoint = (CtiPointAccumulator *)getDevicePointOffsetTypeEqual(AIPointOffset, PulseAccumulatorPointType)) != NULL)
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

                            sprintf(tStr, "%s point %s = %f", getName(), pAccumPoint->getName(), PValue);

                            pData = new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, PulseAccumulatorPointType, tStr);

                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().insert(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }

                        Offset += 2;
                    }

#if 0
                    for(PointRecord.PointOffset = StartAccum + 1; PointRecord.PointOffset <= EndAccum; PointRecord.PointOffset++)
                    {
                        /* Get the demand accumulator record */
                        PointRecord.PointType = DEMANDACCUMPOINT;
                        if(!(PointGetDeviceTypeOffsetEqual (&PointRecord)))
                        {

                            PointLock (&PointRecord);

                            /* Move the pulse counts */
                            PointRecord.PreviousPulses = PointRecord.PresentPulses;

                            /* Calculate the new pulses */
                            PointRecord.PresentPulses = MAKEUSHORT (InMessage->Buffer.InMessage[Offset], InMessage->Buffer.InMessage[Offset + 1]);

                            /* Check if we have two reads */
                            if(!DeviceRecord->PrevFreezeNumber || !DeviceRecord->LastFreezeNumber)
                            {
                                if(PointRecord.CurrentValue < PointRecord.PlugValue)
                                    PointRecord.CurrentValue = PointRecord.PlugValue;

                                CheckDataStateQuality (DeviceRecord, &PointRecord, &PValue, NORMAL, TimeB->time, TimeB->dstflag, DATAFREEZEFAIL, LogFlag);
                            }
                            else
                            {
                                /* Calculate the number of pulses */
                                if(PointRecord.PresentPulses < PointRecord.PreviousPulses)
                                    /* Rollover */
                                    UValue = 0xffff - PointRecord.PreviousPulses + PointRecord.PresentPulses;
                                else
                                    UValue = PointRecord.PresentPulses - PointRecord.PreviousPulses;

                                /* Calculate in units/hour */
                                PValue = (FLOAT) UValue * PointRecord.Multiplier;

                                /*  convert to units */
                                PValue /= PartHour;

                                /* Apply offset */
                                PValue += PointRecord.Offset;

                                CheckDataStateQuality (DeviceRecord, &PointRecord, &PValue, NORMAL, TimeB->time, TimeB->dstflag, NORMAL, LogFlag);
                            }

                            /* Send to DRP */
                            memcpy (DRPValue.DeviceName, PointRecord.DeviceName, STANDNAMLEN);
                            memcpy (DRPValue.PointName, PointRecord.PointName, STANDNAMLEN);
                            DRPValue.Quality = PointRecord.CurrentQuality;
                            DRPValue.AlarmState = PointRecord.AlarmStatus;
                            DRPValue.Value = PointRecord.CurrentValue;
                            DRPValue.TimeStamp = PointRecord.CurrentTime;
                            DRPValue.Type = DRPTYPEVALUE;

                            SendDRPPoint (&DRPValue);
                        }
                        else
                        {
                            PointRecord.PointType = ACCUMULATORPOINT;
                            if(!(PointGetDeviceTypeOffsetEqual (&PointRecord)))
                            {

                                PointLock (&PointRecord);

                                /* Move the pulse counts */
                                PointRecord.PreviousPulses = PointRecord.PresentPulses;

                                /* Calculate the new pulses */
                                PointRecord.PresentPulses = MAKEUSHORT (InMessage->Buffer.InMessage[Offset],
                                                                        InMessage->Buffer.InMessage[Offset + 1]);

                                /* Check if we have two reads */
                                if(!DeviceRecord->PrevFreezeNumber || !DeviceRecord->LastFreezeNumber)
                                {
                                    if(PointRecord.CurrentValue < PointRecord.PlugValue)
                                        PointRecord.CurrentValue = PointRecord.PlugValue;

                                    CheckDataStateQuality (DeviceRecord, &PointRecord, &PValue, NORMAL, TimeB->time, TimeB->dstflag, DATAFREEZEFAIL, LogFlag);
                                }
                                else
                                {
                                    /* Calculate the number of pulses */
                                    if(PointRecord.PresentPulses < PointRecord.PreviousPulses)
                                        /* Rollover */
                                        UValue = 0xffff - PointRecord.PreviousPulses + PointRecord.PresentPulses;
                                    else
                                        UValue = PointRecord.PresentPulses - PointRecord.PreviousPulses;

                                    /* Calculate in units/hour */
                                    PValue = (FLOAT) UValue * PointRecord.Multiplier;

                                    /* Apply offset */
                                    PValue += PointRecord.Offset;

                                    CheckDataStateQuality (DeviceRecord, &PointRecord, &PValue, NORMAL, TimeB->time, TimeB->dstflag, NORMAL, LogFlag);
                                }

                                /* Send to DRP */
                                memcpy (DRPValue.DeviceName, PointRecord.DeviceName, STANDNAMLEN);
                                memcpy (DRPValue.PointName, PointRecord.PointName, STANDNAMLEN);
                                DRPValue.Quality = PointRecord.CurrentQuality;
                                DRPValue.AlarmState = PointRecord.AlarmStatus;
                                DRPValue.Value = PointRecord.CurrentValue;
                                DRPValue.TimeStamp = PointRecord.CurrentTime;
                                DRPValue.Type = DRPTYPEVALUE;

                                SendDRPPoint (&DRPValue);
                            }
                        }
                        Offset += 2;
                    }

#endif
                }
                resetScanFrozen();
                resetScanFreezeFailed();
            }

            if(NumSOE)
            {
                /* No use for SOE reports at this time */
                Offset += (NumSOE * 5);
            }

            if(NumAnalogs)
            {
                for(i = 0; i < NumAnalogs; i++)
                {
                    if(i & 0x01)
                    {
                        AIPointOffset = InMessage->Buffer.InMessage[Offset + 4] + 1;
                        Value = MAKEUSHORT (InMessage->Buffer.InMessage[Offset + 3], InMessage->Buffer.InMessage[Offset + 2] & 0x0f);
                        Offset += 5;
                    }
                    else
                    {
                        AIPointOffset = InMessage->Buffer.InMessage[Offset] + 1;
                        Value = MAKEUSHORT (InMessage->Buffer.InMessage[Offset + 1], (InMessage->Buffer.InMessage[Offset + 2] & 0xf0) >> 4);
                    }

                    Value = Value << 4;

                    Value = Value / 16;

                    if((NumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(AIPointOffset, AnalogPointType)) != NULL)
                    {
                        PValue = NumericPoint->computeValueForUOM( Value );

                        sprintf(tStr, "%s point %s = %f", getName(), NumericPoint->getName(), PValue );

                        pData = new CtiPointDataMsg(PointRecord->getPointID(), PValue, NormalQuality, AnalogPointType, tStr);

                        if(pData != NULL)
                        {
                            ReturnMsg->PointData().insert(pData);
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
            if(InMessage->Buffer.InMessage[0] & 0x08)
            {
                /* update the accumulator criteria for this Remote */
                setLastFreezeTime(RWTime(YUKONEOT));
                setLastFreezeNumber( 0 );

                /* now force another scan */
                OutMessage = new OUTMESS;

                if(OutMessage != NULL)
                {
                    InEchoToOut(InMessage, OutMessage);

                    CtiCommandParser parse(InMessage->Return.CommandStr);

                    setIlexSequenceNumber( 1 );
                    if((i = IntegrityScan(NULL, parse, OutMessage, vgList, retList, outList, MAXPRIORITY - 3)) != NORMAL)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        ReportError ((USHORT)i); /* Send Error to logger */
                    }
                    else
                    {
                        setScanPending();
                    }
                }
            }
            else if(isScanPending())
            {
                resetScanPending();

                // 04302002 CGP This doesn't seem to be used anywhere around here...
                // DeviceRecord->LastFullScan = TimeB->time;
            }
            else
            {
                /* put a something fishy message here */
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
            break;
        }               /* end of default and reset conditions */
    }                   /* End of switch */


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


    return status;
}

INT CtiDeviceILEX::ErrorDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage >   &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
{
    INT status = NoError;

    return status;
}

INT CtiDeviceILEX::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NoError;

    return status;
}

INT CtiDeviceILEX::executeControl(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    return status;
}


BYTE CtiDeviceILEX::getFreezeNumber() const
{
    return _freezeNumber;
}

CtiDeviceILEX& CtiDeviceILEX::setFreezeNumber(BYTE number)
{
    _freezeNumber = number;
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

INT CtiDeviceILEX::exceptionScan(OUTMESS *&OutMessage, INT ScanPriority)
{
    INT status = NORMAL;

    if(OutMessage != NULL)
    {
        /* Load the forced scan message */
        header(OutMessage->Buffer.OutMessage + PREIDLEN, ILEXSCAN, !getIlexSequenceNumber(), EXCEPTION_SCAN);

        /* Load all the other stuff that is needed */
        OutMessage->DeviceID              = getID();
        OutMessage->Port                  = getPortID();
        OutMessage->Remote                = getAddress();
        OutMessage->Priority              = (UCHAR)(ScanPriority);
        OutMessage->TimeOut               = 2;
        OutMessage->OutLength             = 2;
        OutMessage->InLength              = -1;
        OutMessage->EventCode             = RESULT | ENCODED;
        OutMessage->Sequence              = 0;
        OutMessage->Retry                 = 2;
    }

    return status;
}

