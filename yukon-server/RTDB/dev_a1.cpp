/*-----------------------------------------------------------------------------*
*
* File:   dev_a1
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_a1.cpp-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2005/02/10 23:23:58 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*    History:
      $Log: dev_a1.cpp,v $
      Revision 1.11  2005/02/10 23:23:58  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.10  2004/07/27 16:53:53  mfisher
      RWTime.seconds workaround for boost ptime::seconds

      Revision 1.9  2003/04/10 21:45:47  dsutton
      Added code to check the CRC on the multiple message classes (ones over 64 bytes)
      and added checks to make sure we had received the entire message


*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include <iostream>
#include <vector>

using namespace std ;

#include "porter.h"
#include "dev_alpha.h"
#include "dev_a1.h"

#include "pt_base.h"
#include "pt_status.h"
#include "pt_analog.h"
#include "pt_accum.h"

#include "msg_pcreturn.h"
#include "msg_cmd.h"
#include "msg_pdata.h"
#include "msg_multi.h"
#include "msg_trace.h"
#include "cmdparse.h"

#include "dupreq.h"
#include "dlldefs.h"

#include "logger.h"
#include "guard.h"

CTI_alpha_func   A1Functions[] = {
    {  0x01, 4     ,  "Password check"},
    {  0x02, 3     ,  "Time set"},
    {  0x06, 1     ,  "Who are you?"},
    {  0x07, 3     ,  "Billing read call en date"},
    {  0x08, 1     ,  "Call back command function"},
    {  0x09, 1     ,  "Packet size"},
    {  0x0B, 2     ,  "Critical peak"},
    {  0x0C, 1     ,  "Time sync"},
    {  0xF2, 1     ,  "Communications time-out value"},
};


CTI_alpha_class   A1Classes[] = {
    {  0, 15    ,  "Primary Metering Constants"},
    {  1, 5     ,  "Password Table"},
    {  2, 52    ,  "Identification & Demand Constants"},
    {  3, 154   ,  "Main display table"},
    {  4, 176   ,  "TOU rate schedule"},
    {  5, 43    ,  "Diplay Table 2"},
    {  6, 272   ,  "Metering Configuration"},
    {  7, 288   ,  "Secondary Metering Constants"},
    {  8, 8     ,  "Firmware Configuration"},
    {  9, 48    ,  "Status Area #1"},
    { 10, 24    ,  "Status Area #2"},
    { 11, 496   ,  "Current billing data"},
    { 12, 496   ,  "Previous period billing data"},
    { 13, 496   ,  "Previous season billing data"},
    { 14, 36    ,  "Load profile configuration"},
    { 15, 56    ,  "Event log configuration"},
    { 16, 999   ,  "Event log data"},
    { 17, 999   ,  "Load profile data"},
    { 18, 999   ,  "Load profile partial data"},
    { 19, 5     ,  "Future password table"},
    { 20, 52    ,  "Future identification and demand table"},
    { 21, 154   ,  "Future display table 1"},
    { 22, 176   ,  "Future rate schedule"},
    { 23, 43    ,  "Future display table 2"},
    { 24, 36    ,  "Future load profile config"},
    { 25, 56    ,  "Future eventlog config"},
    { 30, 43    ,  "Partial interval pulse counts"},
    { 31, 46    ,  "Modem billing call configuration table"},
    { 32, 46    ,  "Modem alarm call configuration information"},
    { 33, 64    ,  "Modem configuration information"},
    { 34, 24    ,  "Modem communication's status"}
};

BOOL findBlockMapping (FLOAT &value, USHORT type,  AlphaA1ScanData_t *data);

INT CtiDeviceAlphaA1::allocateDataBins  (OUTMESS *outMess)
{
    // Load command parameters from the out message
    setCurrentCommand ((CtiDeviceIED::CtiMeterCmdStates_t)outMess->Buffer.DUPReq.Command[0]);
    setReadClass (outMess->Buffer.DUPReq.Command[2]);
    setReadLength (outMess->Buffer.DUPReq.Command[3]);
    setReadFunction (outMess->Buffer.DUPReq.Command[2]);

    // allocate this to as big as we can possible get
    if (_dataBuffer == NULL)
    {
        _dataBuffer = CTIDBG_new BYTE[sizeof (AlphaA1ScanData_t)];

        if (_dataBuffer != NULL)
        {
            ((AlphaA1ScanData_t *)_dataBuffer)->Real.class0.valid = FALSE;
            ((AlphaA1ScanData_t *)_dataBuffer)->Real.class8.valid = FALSE;
            ((AlphaA1ScanData_t *)_dataBuffer)->Real.class14.valid = FALSE;
            ((AlphaA1ScanData_t *)_dataBuffer)->Real.class11.valid = FALSE;
        }
    }

    if (_loadProfileBuffer == NULL)
    {
        _loadProfileBuffer = CTIDBG_new BYTE[sizeof (AlphaA1LoadProfile_t)];

        if (_loadProfileBuffer != NULL)
        {
            ((AlphaA1LoadProfile_t *)_loadProfileBuffer)->porterLPTime = outMess->Buffer.DUPReq.LP_Time;
        }
    }
    // set this to our first class
    if (_singleMsgBuffer == NULL)
    {
        // overkill, each msg is an alpha is supposed to be no more than 71 bytes (64+header(5)+CRC(2)
        _singleMsgBuffer = CTIDBG_new BYTE[100];
    }

    _lpWorkBuffer = NULL;

    // make sure we're starting in the right place
    setClassReadComplete (FALSE);


    setTotalByteCount (0);
    setCurrentCommand ((CtiDeviceIED::CtiMeterCmdStates_t)outMess->Buffer.DUPReq.Command[0]);
    setCurrentState (StateHandshakeInitialize);

    return NORMAL;
}

INT CtiDeviceAlphaA1::freeDataBins  ()
{
    // in case this wasn't destroyed last time, get it now
    _sLPPulseVector.erase(_sLPPulseVector.begin(), _sLPPulseVector.end());

    return Inherited::freeDataBins();
}


INT CtiDeviceAlphaA1::GeneralScan(CtiRequestMsg *pReq,
                                  CtiCommandParser &parse,
                                  OUTMESS *&OutMessage,
                                  RWTPtrSlist< CtiMessage > &vgList,
                                  RWTPtrSlist< CtiMessage > &retList,
                                  RWTPtrSlist< OUTMESS > &outList,
                                  INT ScanPriority)
{
    INT status = NORMAL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " General Scan of device " << getName() << " in progress " << endl;
    }

    if (OutMessage != NULL)
    {
        OutMessage->Buffer.DUPReq.Identity = IDENT_ALPHA_A1;
        status = Inherited::GeneralScan (pReq, parse, OutMessage, vgList, retList, outList, ScanPriority);
        return status;
    }
    else
    {
        return MEMORY;
    }
}

USHORT CtiDeviceAlphaA1::calculateStartingByteCountForCurrentScanState (int aClass)
{
    USHORT byteCount = 0;

    switch (getReadClass())
    {
        case 0:
            byteCount =0;
            break;
        case 8:
            byteCount = sizeof (AlphaA1Class0Raw_t);
            break;
        case 11:
            byteCount = sizeof (AlphaA1Class0Raw_t) + sizeof (AlphaA1Class8Raw_t);
            break;
        case 14:
            byteCount = sizeof (AlphaA1Class0Raw_t) + sizeof (AlphaA1Class8Raw_t)+
                        sizeof (AlphaA1Class11Raw_t);
            break;
    }

    return byteCount;
}


INT CtiDeviceAlphaA1::generateCommandScan( CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList )
{
    AlphaA1LoadProfile_t *localLP      = ((AlphaA1LoadProfile_t*)_loadProfileBuffer);
    AlphaA1ScanData_t    *localData    = ((AlphaA1ScanData_t *)_dataBuffer);
    int               retCode = NORMAL;
    BYTEUSHORT        reqLength;
    BYTEUSHORT        reqOffset;
    BYTE              classToRead;

    // get appropriate data
    switch (getCurrentState())
    {
        case StateHandshakeComplete:
        case StateScanValueSet1FirstScan:
            {
                setAttemptsRemaining(3);
            }

        case StateScanValueSet1:
            {
                // ALPHA_INIT_STATE
                // bytes received set to zero outside of loop
                Transfer.getOutBuffer()[0]      = STX;
                Transfer.getOutBuffer()[1]      = ALPHA_CMD_CLASS_READ;
                Transfer.getOutBuffer()[2]      = PAD;

                reqLength.sh = getReadLength();
                reqOffset.sh = 0;

                classToRead  = (BYTE) getReadClass();

                // fill the transfer structure
                Transfer.getOutBuffer()[3]      = reqLength.ch[1];
                Transfer.getOutBuffer()[4]      = reqLength.ch[0];
                Transfer.getOutBuffer()[5]      = reqOffset.ch[1];
                Transfer.getOutBuffer()[6]      = reqOffset.ch[0];
                Transfer.getOutBuffer()[7]      = classToRead;

                setTotalByteCount (calculateStartingByteCountForCurrentScanState(getReadClass()));
                setSingleMsgByteCount(0);

                // note that we fall through again, no processing needed yet
            }

        case StateScanValueSet2FirstScan:  // done to set attempts only first time thru DLS
        case StateScanValueSet2:
            {
                // ALPHA_SENDCLASS_STATE
                // finish setup in count of zero means send only
                Transfer.setOutCount (8);
                Transfer.setInCountExpected (0);
                Transfer.setInTimeout (1);
                Transfer.setCRCFlag (0);

                // add the CRC and update the outcount
                addCRC (Transfer.getOutBuffer(),Transfer.getOutCount(),true);
                Transfer.setOutCount (Transfer.getOutCount() +2);

                // not sure about this DEBUG DLS
                setPreviousState (StateScanValueSet2);
                setCurrentState (StateScanDecode2);
                break;
            }
        case StateScanValueSet3FirstScan:  // done to set attempts only first time thru DLS
        case StateScanValueSet3:
            {
                // ALPHA_RECKNOWNS_STATE

                // finish building transfer and then receive
                Transfer.setOutCount (0);
                Transfer.setInTimeout (1);
                Transfer.setInCountExpected (getBytesToRetrieve());
                Transfer.setCRCFlag (0);
                setPreviousState (StateScanValueSet3);
                setCurrentState (StateScanDecode3);
                break;
            }
        case StateScanValueSet4FirstScan:  // done to set attempts only first time thru DLS
        case StateScanValueSet4:
            {
                // ALPHA_RECUNKNOWN_STATE
                Transfer.setOutCount (0);
                Transfer.setInTimeout (1);
                Transfer.setInCountExpected (getBytesToRetrieve());

                Transfer.setCRCFlag (0);
                setPreviousState (StateScanValueSet4);
                setCurrentState (StateScanDecode4);
                break;
            }
        case StateScanValueSet5FirstScan:  // done to set attempts only first time thru DLS
        case StateScanValueSet5:
            {
                Transfer.setOutCount (0);
                Transfer.setInTimeout (1);
                Transfer.setInCountExpected (getBytesToRetrieve());
                Transfer.setCRCFlag (0);
                setPreviousState (StateScanValueSet5);
                setCurrentState (StateScanDecode5);
                break;
            }
        case StateScanValueSet6FirstScan:  // done to set attempts only first time thru DLS
        case StateScanValueSet6:
            {
                // ALPHA_CONTINUED_STATE
                Transfer.getOutBuffer()[0] = STX;
                Transfer.getOutBuffer()[1] = ALPHA_CMD_CONTINUE_RD;

                setBytesToRetrieve(6);

                Transfer.setOutCount (2);
                Transfer.setInCountExpected(0);
                Transfer.setInTimeout (1);
                Transfer.setCRCFlag (0);

                // add the CRC and update the outcount
                addCRC (Transfer.getOutBuffer(),Transfer.getOutCount(),true);
                Transfer.setOutCount (Transfer.getOutCount() +2);

                setPreviousState (StateScanValueSet6);
                setCurrentState (StateScanDecode6);

                break;
            }
        case StateScanValueSet7FirstScan:  // done to set attempts only first time thru DLS
        case StateScanValueSet7:
            {
                // this is the ALPHA_INIT_RCLASSD2
                // which class is next
                switch (getReadClass())
                {
                    case 0:
                        {
                            // done with class zero, move to 82
                            if (getTotalByteCount() == sizeof (AlphaA1Class0Raw_t))
                            {
                                localData->Real.class0.valid = TRUE;
                            }
                            else
                            {
                                // if we failed to read this, make sure we are still copying into the correct place
                                // for the next class
                                setTotalByteCount (sizeof (AlphaA1Class0Raw_t));
                            }

                            setReadClass(8);
                            setReadFunction(8);
                            setSingleMsgByteCount(0);
                            setAttemptsRemaining(3);

                            setClassReadComplete (FALSE);
                            setPreviousState (StateScanValueSet7);
                            Transfer.setOutCount (0);
                            Transfer.setInCountExpected(0);
                            Transfer.setInTimeout (1);
                            setCurrentState (StateScanDecode7);
                            break;
                        }

                    case 8:
                        {
                            // done with class zero, move to 82
                            if (getTotalByteCount() == (sizeof (AlphaA1Class0Raw_t) +
                                                        sizeof (AlphaA1Class8Raw_t)))
                            {
                                localData->Real.class8.valid = TRUE;
                            }
                            else
                            {
                                // if we failed to read this, make sure we are still copying into the correct place
                                // for the next class
                                setTotalByteCount (sizeof (AlphaA1Class0Raw_t)+
                                                   sizeof (AlphaA1Class8Raw_t));
                            }

                            setReadClass(11);
                            setReadFunction(11);
                            setAttemptsRemaining(3);
                            setSingleMsgByteCount(0);

                            setClassReadComplete (FALSE);
                            setPreviousState (StateScanValueSet7);
                            Transfer.setOutCount (0);
                            Transfer.setInCountExpected(0);
                            Transfer.setInTimeout (1);
                            setCurrentState (StateScanDecode7);
                            break;
                        }


                    case 11:
                        {
                            if (getTotalByteCount() == (sizeof (AlphaA1Class0Raw_t) +
                                                        sizeof (AlphaA1Class8Raw_t) +
                                                        sizeof (AlphaA1Class11Raw_t) ))
                            {
                                localData->Real.class11.valid = TRUE;
                            }
                            else
                            {
                                // if we failed to read this, make sure we are still copying into the correct place
                                // for the next class
                                setTotalByteCount (sizeof (AlphaA1Class0Raw_t) +
                                                   sizeof (AlphaA1Class8Raw_t) +
                                                   sizeof (AlphaA1Class11Raw_t));
                            }

                            setReadClass(14);
                            setReadFunction(14);
                            setSingleMsgByteCount(0);
                            setAttemptsRemaining(3);

                            setClassReadComplete (FALSE);
                            setPreviousState (StateScanValueSet7);
                            Transfer.setOutCount (0);
                            Transfer.setInCountExpected(0);
                            Transfer.setInTimeout (1);
                            setCurrentState (StateScanDecode7);
                            break;
                        }
                    case 14:

                        {
                            if (getTotalByteCount() == (sizeof (AlphaA1Class0Raw_t) +
                                                        sizeof (AlphaA1Class8Raw_t) +
                                                        sizeof (AlphaA1Class11Raw_t) +
                                                        sizeof (AlphaA1Class14Raw_t)))
                            {
                                localData->Real.class14.valid = TRUE;
                            }

                            /*********************
                            * at minimum, we need the load profile setup to see which blocks
                            * are what data and we need to have read the billing data
                            * correctly or we have no total usage readings to use
                            **********************
                            */
                            // see if we read either of these correctly
                            if (localData->Real.class14.valid && localData->Real.class11.valid)
                            {
                                // set total bytes retrieved into buffer and reset count for load profile retrieval
                                ((AlphaA1ScanData_t *)_dataBuffer)->totalByteCount = getTotalByteCount();
                                setTotalByteCount(0);

                                //  we're now ready to read load profile, reset everything and go
                                setCurrentCommand(CmdLoadProfileTransition);
                                setReadClass(0);
                                setReadFunction(0);
                                setSingleMsgByteCount(0);
                                setAttemptsRemaining(3);

                                if (_lpWorkBuffer != NULL)
                                {
                                    delete []_lpWorkBuffer;
                                    _lpWorkBuffer = NULL;
                                }

                                // allocate this on the fly
                                _lpWorkBuffer = CTIDBG_new BYTE[sizeof (AlphaA1Class0Raw_t)];

                                setClassReadComplete (FALSE);
                                setPreviousState (StateScanValueSet7);
                                Transfer.setOutCount (0);
                                Transfer.setInCountExpected(0);
                                Transfer.setInTimeout (1);
                                setCurrentState (StateScanDecode8);
                            }
                            else
                            {
                                setPreviousState (StateScanAbort);
                                generateCommandTerminate (Transfer, traceList);

                                // if this guy is valid, billing data must have failed
                                if( DebugLevel & 0x0001 )
                                {
                                    if (localData->Real.class14.valid)
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " Class 11 billing data read failed for " << getName() << " aborting scan " << endl;
                                    }
                                    else
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " Class 14 load profile configuration read failed for " << getName() << " aborting scan " << endl;
                                    }
                                }
                            }
                            break;
                        }
                    default:
                        {
                            setPreviousState (StateScanAbort);
                            generateCommandTerminate (Transfer, traceList);
                            break;
                        }
                }
                break;
            }

        case StateScanSendTerminate:
            {
                generateCommandTerminate (Transfer, traceList);
                break;
            }
        default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
            }
            generateCommandTerminate (Transfer, traceList);
            setPreviousState (StateScanAbort);
            retCode = StateScanAbort;
    }
    return retCode;
}

INT CtiDeviceAlphaA1::generateCommandLoadProfile( CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList )
{
    int               retCode = NORMAL;
    BYTEUSHORT        reqLength;
    BYTEUSHORT        reqOffset;
    BYTE              classToRead;
    AlphaA1LoadProfile_t *ptr = (AlphaA1LoadProfile_t *)_loadProfileBuffer;

    // get appropriate data
    switch (getCurrentState())
    {
        case StateHandshakeComplete:
        case StateScanComplete:
        case StateScanValueSet1FirstScan:
            {
                setAttemptsRemaining(3);
            }

        case StateScanValueSet1:
            {
                // ALPHA_INIT_STATE
                // bytes received set to zero outside of loop
                Transfer.getOutBuffer()[0]      = STX;
                Transfer.getOutBuffer()[1]      = ALPHA_CMD_CLASS_READ;
                Transfer.getOutBuffer()[2]      = PAD;

                reqLength.sh = getReadLength();
                reqOffset.sh = 0;

                if (getReadClass() == 18)
                {
//                    reqLength.sh = 100;
                    reqLength.sh = ptr->bytesRequested;
                }

                // fill the transfer structure
                Transfer.getOutBuffer()[3]      = reqLength.ch[1];
                Transfer.getOutBuffer()[4]      = reqLength.ch[0];
                Transfer.getOutBuffer()[5]      = reqOffset.ch[1];
                Transfer.getOutBuffer()[6]      = reqOffset.ch[0];
                Transfer.getOutBuffer()[7]      = (BYTE) getReadClass();
                setTotalByteCount(0);
                setSingleMsgByteCount(0);

                // note that we fall through again, no processing needed yet
            }

        case StateScanValueSet2FirstScan:
        case StateScanValueSet2:
            {
                // ALPHA_SENDCLASS_STATE
                // finish setup in count of zero means send only
                Transfer.setOutCount (8);
                Transfer.setInCountExpected (0);
                Transfer.setInTimeout (1);

                Transfer.setCRCFlag (0);

                // add the CRC and update the outcount
                addCRC (Transfer.getOutBuffer(),Transfer.getOutCount(),true);
                Transfer.setOutCount (Transfer.getOutCount() +2);

                // not sure about this DEBUG DLS
                setPreviousState (StateScanValueSet2);
                setCurrentState (StateScanDecode2);
                break;
            }
        case StateScanValueSet3FirstScan:
        case StateScanValueSet3:
            {
                // ALPHA_RECKNOWNS_STATE

                // finish building transfer and then receive
                Transfer.setOutCount (0);
                Transfer.setInTimeout (1);
                Transfer.setInCountExpected (getBytesToRetrieve());
                Transfer.setCRCFlag (0);
                setPreviousState (StateScanValueSet3);
                setCurrentState (StateScanDecode3);
                break;
            }
        case StateScanValueSet4FirstScan:
        case StateScanValueSet4:
            {
                // ALPHA_RECUNKNOWN_STATE
                Transfer.setOutCount (0);
                Transfer.setInTimeout (1);
                Transfer.setInCountExpected (getBytesToRetrieve());

                Transfer.setCRCFlag (0);
                setPreviousState (StateScanValueSet4);
                setCurrentState (StateScanDecode4);
                break;
            }
        case StateScanValueSet5FirstScan:
        case StateScanValueSet5:
            {
                Transfer.setOutCount (0);
                Transfer.setInTimeout (1);
                Transfer.setInCountExpected (getBytesToRetrieve());
                Transfer.setCRCFlag (0);
                setPreviousState (StateScanValueSet5);
                setCurrentState (StateScanDecode5);
                break;
            }
        case StateScanValueSet6FirstScan:
        case StateScanValueSet6:
            {
                // ALPHA_CONTINUED_STATE
                Transfer.getOutBuffer()[0] = STX;
                Transfer.getOutBuffer()[1] = ALPHA_CMD_CONTINUE_RD;

                setBytesToRetrieve(6);

                Transfer.setOutCount (2);
                Transfer.setInCountExpected(0);
                Transfer.setInTimeout (1);

                Transfer.setCRCFlag (0);

                // add the CRC and update the outcount
                addCRC (Transfer.getOutBuffer(),Transfer.getOutCount(),true);
                Transfer.setOutCount (Transfer.getOutCount() +2);

                setPreviousState (StateScanValueSet6);
                setCurrentState (StateScanDecode6);

                break;
            }
        case StateScanValueSet7FirstScan:
        case StateScanValueSet7:
            {
                // this is the ALPHA_INIT_RCLASSD2
                // which class is next
                switch (getReadClass())
                {
                    case 0:
                        {
                            AlphaA1Class0Raw_t  * wPtr = (AlphaA1Class0Raw_t *)_lpWorkBuffer;
                            // done with class 2, move to 82

                            ptr->class0.wattHoursPerRevolution = (ULONG) BCDtoBase10(wPtr->UKH,     3) / 1000.0;
                            ptr->class0.pulsesPerRevolution = (USHORT) BCDtoBase10(&wPtr->UPR,     1);

                            if( DebugLevel & 0x0001 )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "---------------------- Class 0 -------------------------" << endl;
                                dout << "Kh     : " << ptr->class0.wattHoursPerRevolution << endl;
                                dout << "Mp     : " << ptr->class0.pulsesPerRevolution<< endl;
                            }

                            if (_lpWorkBuffer != NULL)
                            {
                                delete []_lpWorkBuffer;
                                _lpWorkBuffer = NULL;
                            }

                            _lpWorkBuffer = CTIDBG_new BYTE[sizeof (AlphaA1Class8Raw_t)];

                            // moving to load profile next, reset parameters
                            setReadClass(8);
                            setReadFunction(8);
                            setTotalByteCount (0);
                            setSingleMsgByteCount(0);
                            setAttemptsRemaining(3);

                            setClassReadComplete (FALSE);
                            setPreviousState (StateScanValueSet7);
                            Transfer.setOutCount (0);
                            Transfer.setInCountExpected(0);
                            Transfer.setInTimeout (1);
                            setCurrentState (StateScanDecode7);
                            break;
                        }

                    case 8:
                        {
                            AlphaA1Class8Raw_t  * wPtr = (AlphaA1Class8Raw_t *)_lpWorkBuffer;

                            ptr->class8.firmwareSpec = (ULONG)BCDtoBase10 (wPtr->SSPEC, 3);
                            ptr->class8.groupNo      = (USHORT)BCDtoBase10 (&wPtr->SSPEC[3],1);
                            ptr->class8.revisionNo      = (USHORT)BCDtoBase10 (&wPtr->SSPEC[4],1);

                            if ((wPtr->XUOMH & 0x04) || (wPtr->XUOMH & 0x08))
                            {
                                ptr->class8.meterType = ALPHA_A1R;
                            }
                            else if ((wPtr->XUOMH & 0x40) || (wPtr->XUOMH & 0x80))
                            {
                                ptr->class8.meterType = ALPHA_A1K;
                            }
                            else
                            {
                                // default to reactive
                                ptr->class8.meterType = ALPHA_A1R;
                            }

                            if( DebugLevel & 0x0001 )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "---------------------- Class 8 -------------------------" << endl;
                                dout << "Firmware               : " << ptr->class8.firmwareSpec << endl;
                                dout << "Group                  : " << ptr->class8.groupNo << endl;
                                dout << "Revision               : " << ptr->class8.revisionNo << endl;
                                dout << "Type (0=A1R 1=A1K)     : " << ptr->class8.meterType << endl;
                            }

                            if (_lpWorkBuffer != NULL)
                            {
                                delete []_lpWorkBuffer;
                                _lpWorkBuffer = NULL;
                                // because this is calculated, the extra 100 is for final date and time, etc
                            }

                            _lpWorkBuffer = CTIDBG_new BYTE[sizeof (AlphaA1Class14Raw_t)];

                            setReadClass(14);
                            setReadFunction(14);
                            setSingleMsgByteCount(0);
                            setAttemptsRemaining(3);

                            setTotalByteCount (0);
                            setClassReadComplete (FALSE);
                            setPreviousState (StateScanValueSet7);
                            Transfer.setOutCount (0);
                            Transfer.setInCountExpected(0);
                            Transfer.setInTimeout (1);
                            setCurrentState (StateScanDecode7);
                            break;
                        }

                    case 14:
                        {
                            AlphaA1Class14Raw_t  * wPtr = (AlphaA1Class14Raw_t *)_lpWorkBuffer;

                            ptr->class14.touInput[0]        = touBlockMapping (wPtr->TBLKCF1, ptr->class8.meterType);
                            ptr->class14.touInput[1]        = touBlockMapping (wPtr->TBLKCF2, ptr->class8.meterType);
                            ptr->class14.touInput[2]        = touBlockMapping (wPtr->TBLKCF3, ptr->class8.meterType);
                            ptr->class14.touInput[3]        = touBlockMapping (wPtr->TBLKCF4, ptr->class8.meterType);
                            ptr->class14.touInput[4]        = touBlockMapping (wPtr->TBLKCF5, ptr->class8.meterType);
                            ptr->class14.touInput[5]        = touBlockMapping (wPtr->TBLKCF6, ptr->class8.meterType);

                            ptr->class14.channelInput[0]    = (USHORT)wPtr->AIOFLG;
                            ptr->class14.channelInput[1]    = (USHORT)wPtr->BIOFLG;
                            ptr->class14.channelInput[2]    = (USHORT)wPtr->CIOFLG;
                            ptr->class14.channelInput[3]    = (USHORT)wPtr->DIOFLG;
                            ptr->class14.channelInput[4]    = (USHORT)wPtr->EIOFLG;
                            ptr->class14.channelInput[5]    = (USHORT)wPtr->FIOFLG;
                            ptr->class14.channelInput[6]    = (USHORT)wPtr->GIOFLG;
                            ptr->class14.channelInput[7]    = (USHORT)wPtr->HIOFLG;


                            ptr->class14.scalingFactor    = (USHORT) wPtr->LPSCALE;
                            ptr->class14.intervalLength   = (USHORT) wPtr->LPLEN;
                            ptr->class14.lpMemory         = (USHORT) wPtr->LPMEM;

                            /******************
                            * calculate number of channels based on which are configured
                            * (only 4 are supported in this meter)
                            *******************
                            */
                            ptr->class14.numberOfChannels = 0;
                            for (int x=0;x<4;x++)
                            {
                                if (ptr->class14.channelInput[x] != 0)
                                {
                                    ptr->class14.numberOfChannels++;
                                }
                            }

                            // check if we should get our load profile
                            if (shouldRetrieveLoadProfile (ptr->porterLPTime, ptr->class14.intervalLength))
                            {
                                /**********************
                                * this is the number of intervals I am missing
                                * if I am close to the next interval or the meter's clock is differnet
                                * than mine, I should add a couple of intervals here and request more
                                * I need DLS
                                ***********************
                                */
                                int missingIntervals =((RWTime::now().seconds() - ptr->porterLPTime) / (ptr->class14.intervalLength * 60)) + 2;
                                ptr->bytesRequested = missingIntervals * ptr->class14.numberOfChannels * 2;

                                // we get back at most 64 bytes at a time
                                ptr->dayRecordSize = 64;

                                if( DebugLevel & 0x0001 )
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime().asString() << endl << "--------- Alpha Load Profile Inputs ---------------" << endl;
                                    dout << "Number of Channels " << ptr->class14.numberOfChannels  << " Interval " << ptr->class14.intervalLength << endl;
                                    dout << "\tChannel 1: " << (USHORT)ptr->class14.channelInput[0] << endl;
                                    dout << "\tChannel 2: " << (USHORT)ptr->class14.channelInput[1] << endl;
                                    dout << "\tChannel 3: " << (USHORT)ptr->class14.channelInput[2] << endl;
                                    dout << "\tChannel 4: " << (USHORT)ptr->class14.channelInput[3] << endl;

                                    dout << "Scaling factor " << ptr->class14.scalingFactor << endl << endl;
                                }

                                /***********************
                                * the result decode routine copies incoming data into the holding pen
                                * until this flag is set to TRUE signaling this is the last message
                                ************************
                                */
                                ptr->finalDataFlag = FALSE;

                                // we're done with this as is, reallocate
                                if (_lpWorkBuffer != NULL)
                                {
                                    delete []_lpWorkBuffer;
                                    _lpWorkBuffer = NULL;
                                }

                                // because this is calculated, the extra 100 is for final date and time, etc
                                _lpWorkBuffer = CTIDBG_new BYTE[ptr->dayRecordSize + 100];


                                // limit in protocol for the byte request is 64k
                                if (ptr->bytesRequested > 64000)
                                {
                                    setReadClass(17);
                                    setReadFunction(17);
                                }
                                else
                                {
                                    setReadClass(18);
                                    setReadFunction(18);
                                }

                                setTotalByteCount (0);
                                setClassReadComplete (FALSE);
                                setSingleMsgByteCount(0);
                                setAttemptsRemaining(3);

                                setPreviousState (StateScanValueSet7);
                                Transfer.setOutCount (0);
                                Transfer.setInCountExpected(0);
                                Transfer.setInTimeout (1);
                                setCurrentState (StateScanDecode7);

                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " Load profile for " << getName() << " will not be collected this scan" << endl;
                                }
                                setTotalByteCount(0);
                                setPreviousState (StateScanComplete);
                                generateCommandTerminate (Transfer, traceList);
                            }

                            break;
                        }
                    case 17:
                        {
                            setTotalByteCount(0);
                            setPreviousState (StateScanComplete);
                            generateCommandTerminate (Transfer, traceList);
                            break;
                        }

                    case 18:
                        {
                            setTotalByteCount(0);
                            setPreviousState (StateScanComplete);
                            generateCommandTerminate (Transfer, traceList);
                            break;
                        }


                    default:
                        {
                            setPreviousState (StateScanAbort);
                            generateCommandTerminate (Transfer, traceList);
                            break;
                        }
                }
                break;
            }

        case StateScanSendTerminate:
            {
                generateCommandTerminate (Transfer, traceList);
                break;
            }
        default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
            }
            generateCommandTerminate (Transfer, traceList);
            setPreviousState (StateScanAbort);
            retCode = StateScanAbort;
    }
    return retCode;
}


INT CtiDeviceAlphaA1::decodeResponseScan (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{

    INT         iClass;
    // Class Offset is the position of the requested class in the APlusClasses array
    INT         classOffset;
    // ClassLength is the byte length count of the requested class from the APlusClasses array
    INT         classLength;

    int retCode = NORMAL;

    // get appropriate data
    switch (getCurrentState())
    {

        case StateScanDecode1:
            // never decode from state one
            break;
        case StateScanDecode2:
            {
                if (commReturnValue)
                {
                    CTISleep(500);

                    // check are attempts
                    setAttemptsRemaining (getAttemptsRemaining() - 1);
                    if (getAttemptsRemaining() > 0)
                    {
                        setCurrentState (StateScanValueSet1);
                    }
                    else
                    {
                        // we don't abort until we've tried all the classes we want
                        setCurrentState (StateScanValueSet7FirstScan);
                    }
                }
                else
                {
                    /*
                    At this point we decide if the alpha is in a known state or not
                    if we know where we are, got to state3
                    otherwise we skip ahead to state4  DLS
                    */
                    iClass = (INT) getReadClass();
                    classOffset = getA1ClassOffset(KEY_ALPHA_CLASS, (void *)&iClass);
                    classLength = A1Classes[classOffset].Length;

                    if (getReadLength())                                   // Length is "const"
                    {
                        if ( getReadLength() > 64)
                        {
                            setBytesToRetrieve(6);              // GET 6 in case it is a NAK!
                            setCurrentState (StateScanValueSet4FirstScan);
                        }
                        else
                        {                                         // We can getem all
                            setBytesToRetrieve(getReadLength() + 7);  // 7 is header + CRC
                            setCurrentState (StateScanValueSet3FirstScan);
                        }
                    }
                    else
                    {
                        if (classLength > 64)
                        {
                            setBytesToRetrieve(6);              // GET 6 in case it is a NAK!
                            setCurrentState (StateScanValueSet4FirstScan);
                        }
                        else
                        {                                         // We can getem all
                            setBytesToRetrieve(classLength + 7);  // 7 is header + CRC
                            setCurrentState (StateScanValueSet3FirstScan);
                        }
                    }
                }
                break;
            }
        case StateScanDecode3:
            {
                // deciding if we go back to the beginning and try again
                int ret_crc,ret_length;

                CtiTraceMsg trace;
                RWCString msg;
                CHAR traceBuffer[20];

                /********************************************
                * comm error return
                *
                * was the message in the correct format
                *
                * CRC violation
                *
                * check that the length of the message received matches what
                * the message said it would be otherwise get out
                * byte 5 is the length (msb is marker telling us whether this is the
                * last message in a class download) and add 4 for header and 2 for crc
                *********************************************
                */

                if (commReturnValue ||
                    (!isReturnedBufferValid(Transfer)) ||
                    (ret_crc=checkCRC(Transfer.getInBuffer(),Transfer.getInCountActual())) ||
                    (ret_length=(Transfer.getInCountActual() != ((Transfer.getInBuffer()[4] & ~0x80)+7))))
                {
                    // print out CRC error if that was the error
                    if (ret_crc)
                    {
                        trace.setBrightYellow();
                        trace.setTrace( RWTime().asString() );
                        traceList.insert(trace.replicateMessage());
                        trace.setBrightRed();
                        msg = RWCString (" CRC error for ") + getName() + RWCString(" while reading class ") + RWCString(itoa(getReadClass(),traceBuffer,10)) + RWCString("\n");
                        trace.setTrace(msg);
                        traceList.insert (trace.replicateMessage());
                    }
                    else if (ret_length)
                    {
                        trace.setBrightYellow();
                        trace.setTrace( RWTime().asString() );
                        traceList.insert(trace.replicateMessage());
                        trace.setBrightRed();
                        msg = RWCString ("  Byte count mis-match  ") + getName() + RWCString (" while reading class ") + RWCString(itoa(getReadClass(),traceBuffer,10)) + RWCString ("\n");
                        trace.setTrace(msg);
                        traceList.insert (trace.replicateMessage());
                    }

                    // decline attempts remaining first thing
                    setAttemptsRemaining (getAttemptsRemaining() - 1);
                    if (getAttemptsRemaining() > 0)
                    {
                        // try the class again
                        setCurrentState (StateScanValueSet1);
                    }
                    else
                    {
                        // we don't abort until we've tried all the classes we want
                        setCurrentState (StateScanValueSet7FirstScan);
                    }
                    CTISleep(500);
                }
                else
                {
                    try
                    {
                        memcpy(&_dataBuffer[getTotalByteCount()],
                               &Transfer.getInBuffer()[5],
                               (Transfer.getInCountActual()) - 7);

                        setTotalByteCount (getTotalByteCount() + ((Transfer.getInCountActual())) - 7);

                        // need to check if have any more info
                        setCurrentState (StateScanValueSet7FirstScan);
                    }
                    catch (...)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Error retrieving Alpha " << getName() << "'s class " << getReadClass() << endl;
                        // the esc is inside of class 7
                        setCurrentState (StateScanValueSet7FirstScan);
                    }

                }
                break;
            }

        case StateScanDecode4:
            {
                // deciding if we go back to the beginning and try again
                if (commReturnValue || (!isReturnedBufferValid(Transfer)))
                {
                    // decline attempts remaining first thing
                    setAttemptsRemaining (getAttemptsRemaining() - 1);
                    if (getAttemptsRemaining() > 0)
                    {
                        // try the class again
                        setCurrentState (StateScanValueSet1);
                    }
                    else
                    {
                        // we don't abort until we've tried all the classes we want
                        setCurrentState (StateScanValueSet7FirstScan);
                    }
                    CTISleep(500);
                }
                else
                {

                    // Got one last read.. get two crcs at end, minus the data byte we got last read and viola, add only one.
                    setBytesToRetrieve((Transfer.getInBuffer()[4] & ~0x80) + 1);

                    // grab the header information
                    memcpy (&_singleMsgBuffer[getSingleMsgByteCount()],
                            Transfer.getInBuffer(),
                            Transfer.getInCountActual());

                    setSingleMsgByteCount(getSingleMsgByteCount() + Transfer.getInCountActual());

                    //                        DUPRep->Status = InBuffer[3];
                    if (Transfer.getInBuffer()[4] & 0x80)
                    {
                        // This will be the last read the alpha has spoken...
                        setClassReadComplete (TRUE);
                    }
                    else
                    {
                        setClassReadComplete (FALSE);
                    }

                    setPreviousState (StateScanDecode4);
                    setCurrentState (StateScanValueSet5FirstScan);
                }
                break;
            }

        case StateScanDecode5:
            {
                if (commReturnValue || (Transfer.getInCountActual() == 0))
                {
                                       // decline attempts remaining first thing
                    setAttemptsRemaining (getAttemptsRemaining() - 1);
                    if (getAttemptsRemaining() > 0)
                    {
                        // try the class again
                        setCurrentState (StateScanValueSet1);
                    }
                    else
                    {
                        // we don't abort until we've tried all the classes we want
                        setCurrentState (StateScanValueSet7FirstScan);
                    }
                    CTISleep(500);
                }
                else
                {
                    // move the data into the working buffer
                    memcpy (&_singleMsgBuffer[getSingleMsgByteCount()],
                            Transfer.getInBuffer(),
                            Transfer.getInCountActual());

                    setSingleMsgByteCount(getSingleMsgByteCount() + Transfer.getInCountActual());

                    /********************************************
                    * check that the length of the message received matches what
                    * the message said it would be otherwise get out
                    * byte 5 is the length (msb is marker telling us whether this is the
                    * last message in a class download) and add 4 for header and 2 for crc
                    *********************************************
                    */
                    int ret_crc,ret_length;
                    CtiTraceMsg trace;
                    RWCString msg;
                    CHAR traceBuffer[20];

                    if ((ret_crc=checkCRC(_singleMsgBuffer,getSingleMsgByteCount())) ||
                        (ret_length=getSingleMsgByteCount() != ((_singleMsgBuffer[4] & ~0x80)+7)))
                    {
                        if (ret_crc)
                        {
                            trace.setBrightYellow();
                            trace.setTrace( RWTime().asString() );
                            traceList.insert(trace.replicateMessage());
                            trace.setBrightRed();
                            msg = RWCString (" CRC error for ") + getName() + RWCString(" while reading class ") + RWCString(itoa(getReadClass(),traceBuffer,10)) + RWCString("\n");
                            trace.setTrace(msg);
                            traceList.insert (trace.replicateMessage());
                        }
                        else if (ret_length)
                        {
                            trace.setBrightYellow();
                            trace.setTrace( RWTime().asString() );
                            traceList.insert(trace.replicateMessage());
                            trace.setBrightRed();
                            msg = RWCString ("  Byte count mis-match  ") + getName() + RWCString (" while reading class ") + RWCString(itoa(getReadClass(),traceBuffer,10)) + RWCString ("\n");
                            trace.setTrace(msg);
                            traceList.insert (trace.replicateMessage());
                        }

                        // decline attempts remaining first thing
                        setAttemptsRemaining (getAttemptsRemaining() - 1);
                        if (getAttemptsRemaining() > 0)
                        {
                            // try the class again
                            setCurrentState (StateScanValueSet1);
                        }
                        else
                        {
                            // we don't abort until we've tried all the classes we want
                            setCurrentState (StateScanValueSet7FirstScan);
                        }
                    }
                    else
                    {
                        try
                        {
                            memcpy(&_dataBuffer[getTotalByteCount()],
                                   &_singleMsgBuffer[5],
                                   getSingleMsgByteCount() - 7);

                            setTotalByteCount (getTotalByteCount() + (getSingleMsgByteCount() - 7));
                            setSingleMsgByteCount(0);

                            // need to check if have any more info
                            // if we are done
                            if (isClassReadComplete())
                                setCurrentState (StateScanValueSet7FirstScan);
                            else
                                setCurrentState (StateScanValueSet6FirstScan);
                        }
                        catch (...)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Error retrieving " << getName() << "'s class " << getReadClass() << endl;
                            // the esc is inside of class 7
                            setCurrentState (StateScanValueSet7FirstScan);
                        }
                    }
                }
                break;
            }
        case StateScanDecode6:
            {
                if (commReturnValue)
                {
                    CTISleep(500);
                    setCurrentState (StateScanValueSet1);
                }
                else
                {
                    setPreviousState (StateScanDecode6);
                    setCurrentState (StateScanValueSet4);
                }
                break;
            }
        case StateScanDecode7:
            {
                setPreviousState (StateScanDecode7);
                setCurrentState (StateScanValueSet1);
                break;
            }
        case StateScanDecode8:
            {
                setPreviousState (StateScanDecode8);
                setCurrentState (StateScanComplete);
                break;
            }


        case StateScanDecodeTerminate:
            {
                // terminate was sent, reset to status we really wanted
                setCurrentState(getPreviousState());

                if (getCurrentState() == StateScanComplete)
                    retCode =NORMAL;
                else
                    retCode = getCurrentState();
                break;
            }

        default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
            }
            setPreviousState (StateScanAbort);
            setCurrentState (StateScanSendTerminate);
            break;

    }

    return retCode;
}

INT CtiDeviceAlphaA1::decodeResponseLoadProfile (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    INT retCode= NORMAL;
    INT         iClass;
    // Class Offset is the position of the requested class in the APlusClasses array
    INT         classOffset;
    // ClassLength is the byte length count of the requested class from the APlusClasses array
    INT         classLength;
    AlphaA1LoadProfile_t * ptr = (AlphaA1LoadProfile_t *)_loadProfileBuffer;


    // get appropriate data
    switch (getCurrentState())
    {

        case StateScanDecode1:
            // never decode from state one
            break;
        case StateScanDecode2:
            {
                if (commReturnValue)
                {
                    CTISleep(500);

                    // check are attempts
                    setAttemptsRemaining (getAttemptsRemaining() - 1);
                    if (getAttemptsRemaining() > 0)
                    {
                        setCurrentState (StateScanValueSet1);
                    }
                    else
                    {
                        setPreviousState (StateScanAbort);
                        setCurrentState (StateScanSendTerminate);
                    }
                }
                else
                {
                    /*
                    At this point we decide if the alpha is in a known state or not
                    if we know where we are, got to state3
                    otherwise we skip ahead to state4  DLS
                    */
                    iClass = (INT) getReadClass();
                    classOffset = getA1ClassOffset(KEY_ALPHA_CLASS, (void *)&iClass);
                    classLength = A1Classes[classOffset].Length;

                    // I think this is always zero
                    if (getReadLength())                                   // Length is "const"
                    {
                        if ( getReadLength() > 64)
                        {
                            setBytesToRetrieve(6);              // GET 6 in case it is a NAK!
                            setCurrentState (StateScanValueSet4FirstScan);
                        }
                        else
                        {                                         // We can getem all
                            setBytesToRetrieve(getReadLength() + 7);  // 7 is header + CRC
                            setCurrentState (StateScanValueSet3FirstScan);
                        }
                    }
                    else
                    {

                        if (classLength > 64)
                        {
                            setBytesToRetrieve(6);              // GET 6 in case it is a NAK!
                            setCurrentState (StateScanValueSet4FirstScan);
                        }
                        else
                        {                                         // We can getem all
                            setBytesToRetrieve(classLength + 7);  // 7 is header + CRC
                            setCurrentState (StateScanValueSet3FirstScan);
                        }
                    }
                }
                break;
            }
        case StateScanDecode3:
            {
                int ret_crc,ret_length;
                CtiTraceMsg trace;
                RWCString msg;
                CHAR traceBuffer[20];

                /********************************************
                * comm error return
                *
                * was the message in the correct format
                *
                * CRC violation
                *
                * check that the length of the message received matches what
                * the message said it would be otherwise get out
                * byte 5 is the length (msb is marker telling us whether this is the
                * last message in a class download) and add 4 for header and 2 for crc
                *********************************************
                */
                if (commReturnValue ||
                    (!isReturnedBufferValid(Transfer)) ||
                    (ret_crc=checkCRC(Transfer.getInBuffer(),Transfer.getInCountActual())) ||
                    (ret_length=(Transfer.getInCountActual() != ((Transfer.getInBuffer()[4] & ~0x80)+7))))
                {
                    if (ret_crc)
                    {
                        trace.setBrightYellow();
                        trace.setTrace( RWTime().asString() );
                        traceList.insert(trace.replicateMessage());
                        trace.setBrightRed();
                        msg = RWCString (" CRC error for ") + getName() + RWCString(" while reading class ") + RWCString(itoa(getReadClass(),traceBuffer,10)) + RWCString("\n");
                        trace.setTrace(msg);
                        traceList.insert (trace.replicateMessage());
                    }
                    else if (ret_length)
                    {
                        trace.setBrightYellow();
                        trace.setTrace( RWTime().asString() );
                        traceList.insert(trace.replicateMessage());
                        trace.setBrightRed();
                        msg = RWCString ("  Byte count mis-match  ") + getName() + RWCString (" while reading class ") + RWCString(itoa(getReadClass(),traceBuffer,10)) + RWCString ("\n");
                        trace.setTrace(msg);
                        traceList.insert (trace.replicateMessage());
                    }

                    // decline attempts remaining first thing
                    setAttemptsRemaining (getAttemptsRemaining() - 1);
                    if (getAttemptsRemaining() > 0)
                    {
                        // try the class again
                        setCurrentState (StateScanValueSet1);
                    }
                    else
                    {
                        setPreviousState (StateScanAbort);
                        setCurrentState (StateScanSendTerminate);
                    }
                    CTISleep(500);
                }
                else
                {
                    try
                    {
                        memcpy(&_lpWorkBuffer[getTotalByteCount()],
                               &Transfer.getInBuffer()[5],
                               (Transfer.getInCountActual()) - 7);

                        setTotalByteCount (getTotalByteCount() + ((Transfer.getInCountActual())) - 7);

                        // need to check if its time to send back load profile or if we are at the end
                        if ((getTotalByteCount() >= ptr->dayRecordSize) ||
                            ((_lpWorkBuffer[getTotalByteCount()-8] == 0xBF) && (_lpWorkBuffer[getTotalByteCount()-7] == 0xFF)))
                        {
                            // copy into the data buffer for return to scanner
                            memcpy (&ptr->loadProfileData[0], _lpWorkBuffer, getTotalByteCount());
                            ptr->dataBytesActual = getTotalByteCount();

                            // see if we're done
                            if ((_lpWorkBuffer[getTotalByteCount()-8] == 0xBF) && (_lpWorkBuffer[getTotalByteCount()-7] == 0xFF))
                            {
                                ptr->finalDataFlag = TRUE;
                            }

                            // we'll be grabbing a CTIDBG_new pile of info
                            setTotalByteCount (0);

                            setPreviousState (StateScanValueSet7FirstScan);
                            setCurrentState (StateScanReturnLoadProfile);
                        }
                        else
                        {
                            // need to check if have any more info
                            setCurrentState (StateScanValueSet7FirstScan);

                        }
                    }
                    catch (...)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Error retrieving Alpha " << getName() << "'s class " << getReadClass() << endl;
                        }
                        // the esc is inside of class 7
                        setCurrentState (StateScanValueSet7FirstScan);
                    }
                }
                break;
            }

        case StateScanDecode4:
            {
                if (commReturnValue || (!isReturnedBufferValid(Transfer)))
                {
                    // decline attempts remaining first thing
                    setAttemptsRemaining (getAttemptsRemaining() - 1);
                    if (getAttemptsRemaining() > 0)
                    {
                        // try the class again
                        setCurrentState (StateScanValueSet1);
                    }
                    else
                    {
                        setPreviousState (StateScanAbort);
                        setCurrentState (StateScanSendTerminate);
                    }
                    CTISleep(500);

                }
                else
                {
                    // Got one last read.. get two crcs at end, minus the data byte we got last read and viola, add only one.
                    setBytesToRetrieve((Transfer.getInBuffer()[4] & ~0x80) + 1);

                    // grab the header information
                    memcpy (&_singleMsgBuffer[getSingleMsgByteCount()],
                            Transfer.getInBuffer(),
                            Transfer.getInCountActual());

                    setSingleMsgByteCount(getSingleMsgByteCount() + Transfer.getInCountActual());

                    if (Transfer.getInBuffer()[4] & 0x80)
                    {
                        // This will be the last read the alpha has spoken...
                        setClassReadComplete (TRUE);
                    }
                    else
                    {
                        setClassReadComplete (FALSE);
                    }

                    setPreviousState (StateScanDecode4);
                    setCurrentState (StateScanValueSet5FirstScan);
                }
                break;
            }

        case StateScanDecode5:
            {
                if (commReturnValue || (Transfer.getInCountActual() == 0))
                {
                    // decline attempts remaining first thing
                    setAttemptsRemaining (getAttemptsRemaining() - 1);
                    if (getAttemptsRemaining() > 0)
                    {
                        // try the class again
                        setCurrentState (StateScanValueSet1);
                    }
                    else
                    {
                        // we don't abort until we've tried all the classes we want
                        setPreviousState (StateScanAbort);
                        setCurrentState (StateScanSendTerminate);
                    }
                    CTISleep (500);
                }
                else
                {
                    // move the data into the working buffer
                    memcpy (&_singleMsgBuffer[getSingleMsgByteCount()],
                            Transfer.getInBuffer(),
                            Transfer.getInCountActual());

                    setSingleMsgByteCount(getSingleMsgByteCount() + Transfer.getInCountActual());

                    /********************************************
                    * check that the length of the message received matches what
                    * the message said it would be otherwise get out
                    * byte 5 is the length (msb is marker telling us whether this is the
                    * last message in a class download) and add 4 for header and 2 for crc
                    *********************************************
                    */
                    int ret_crc,ret_length;
                    CtiTraceMsg trace;
                    RWCString msg;
                    CHAR traceBuffer[20];

                    if ((ret_crc=checkCRC(_singleMsgBuffer,getSingleMsgByteCount())) ||
                        (ret_length=getSingleMsgByteCount() != ((_singleMsgBuffer[4] & ~0x80)+7)))
                    {
                        if (ret_crc)
                        {
                            trace.setBrightYellow();
                            trace.setTrace( RWTime().asString() );
                            traceList.insert(trace.replicateMessage());
                            trace.setBrightRed();
                            msg = RWCString (" CRC error for ") + getName() + RWCString(" while reading class ") + RWCString(itoa(getReadClass(),traceBuffer,10)) + RWCString("\n");
                            trace.setTrace(msg);
                            traceList.insert (trace.replicateMessage());
                        }
                        else if (ret_length)
                        {
                            trace.setBrightYellow();
                            trace.setTrace( RWTime().asString() );
                            traceList.insert(trace.replicateMessage());
                            trace.setBrightRed();
                            msg = RWCString ("  Byte count mis-match  ") + getName() + RWCString (" while reading class ") + RWCString(itoa(getReadClass(),traceBuffer,10)) + RWCString ("\n");
                            trace.setTrace(msg);
                            traceList.insert (trace.replicateMessage());
                        }

                        // decline attempts remaining first thing
                        setAttemptsRemaining (getAttemptsRemaining() - 1);
                        if (getAttemptsRemaining() > 0)
                        {
                            // try the class again
                            setCurrentState (StateScanValueSet1);
                        }
                        else
                        {
                            // we don't abort until we've tried all the classes we want
                            setCurrentState (StateScanValueSet7FirstScan);
                        }
                    }
                    else
                    {
                        try
                        {
                            memcpy(&_lpWorkBuffer[getTotalByteCount()],
                                   &_singleMsgBuffer[5],
                                   getSingleMsgByteCount() - 7);

                            setTotalByteCount (getTotalByteCount() + (getSingleMsgByteCount() - 7));
                            setSingleMsgByteCount(0);

                            // need to check if have any more info
                            // if we are done
                            if (isClassReadComplete())
                            {
                                // need to check if its time to send back load profile or if we are at the end
                                if ((getTotalByteCount() >= ptr->dayRecordSize) ||
                                    ((_lpWorkBuffer[getTotalByteCount()-8] == 0xBF) && (_lpWorkBuffer[getTotalByteCount()-7] == 0xFF)))
                                {
                                    // copy into the data buffer for return to scanner
                                    memcpy (&ptr->loadProfileData[0], _lpWorkBuffer, getTotalByteCount());
                                    ptr->dataBytesActual = getTotalByteCount();

                                    // see if we're done
                                    if ((_lpWorkBuffer[getTotalByteCount()-8] == 0xBF) && (_lpWorkBuffer[getTotalByteCount()-7] == 0xFF))
                                    {
                                        ptr->finalDataFlag = TRUE;
                                    }
                                    setTotalByteCount (0);

                                    setPreviousState (StateScanValueSet7FirstScan);
                                    setCurrentState (StateScanReturnLoadProfile);
                                }
                                else
                                {
                                    // need to check if have any more info
                                    setCurrentState (StateScanValueSet7FirstScan);
                                }
                            }
                            else
                            {
                                // need to check if its time to send back load profile or if we are at the end
                                if ((getTotalByteCount() >= ptr->dayRecordSize) ||
                                    ((_lpWorkBuffer[getTotalByteCount()-8] == 0xBF) && (_lpWorkBuffer[getTotalByteCount()-7] == 0xFF)))
                                {
                                    // copy into the data buffer for return to scanner
                                    memcpy (&ptr->loadProfileData[0], _lpWorkBuffer, getTotalByteCount());
                                    ptr->dataBytesActual = getTotalByteCount();

                                    // see if we're done
                                    if ((_lpWorkBuffer[getTotalByteCount()-8] == 0xBF) && (_lpWorkBuffer[getTotalByteCount()-7] == 0xFF))
                                    {
                                        ptr->finalDataFlag = TRUE;
                                    }

                                    setTotalByteCount (0);
                                    setPreviousState (StateScanValueSet6FirstScan);
                                    setCurrentState (StateScanReturnLoadProfile);
                                }
                                else
                                {
                                    setCurrentState (StateScanValueSet6FirstScan);
                                }
                            }
                        }
                        catch (...)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Error retrieving Alpha " << getName() << "'s class " << getReadClass() << endl;
                            // the esc is inside of class 7
                            setCurrentState (StateScanValueSet7FirstScan);
                        }
                    }
                }
                break;
            }
        case StateScanDecode6:
            {
                if (commReturnValue)
                {

                    CTISleep(500);
                    setCurrentState (StateScanValueSet1);
                }
                else
                {
                    setPreviousState (StateScanDecode6);
                    setCurrentState (StateScanValueSet4);
                }
                break;
            }
        case StateScanDecode7:
            {
                setPreviousState (StateScanDecode7);
                setCurrentState (StateScanValueSet1);
                break;
            }

        case StateScanDecodeTerminate:
            {
                // terminate was sent, reset to status we really wanted
                setCurrentState(getPreviousState());

                if (getCurrentState() == StateScanComplete)
                    retCode =NORMAL;
                else
                    retCode = getCurrentState();
                break;
            }

        default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
            }
            setPreviousState (StateScanAbort);
            setCurrentState (StateScanSendTerminate);
            break;

    }

    return retCode;
}



INT CtiDeviceAlphaA1::decodeResultScan   (INMESS *InMessage,
                                          RWTime &TimeNow,
                                          RWTPtrSlist< CtiMessage >   &vgList,
                                          RWTPtrSlist< CtiMessage > &retList,
                                          RWTPtrSlist< OUTMESS > &outList)
{
    char tmpCurrentState   = InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1];
//   SYSTEMLOGMESS LogMessage;
    CHAR     temp[100], buffer[100];

    BOOL     bDoStatusCheck = FALSE;
    BOOL     bDoResetStatus = FALSE;
    BOOL     isValidPoint = TRUE;
    /* Misc. definitions */
    ULONG i, j;
    ULONG Pointer;

    /* Variables for decoding LCU Messages */
    SHORT    Value;
    USHORT   UValue;
    FLOAT    PartHour;
    DOUBLE   PValue;

    DIALUPREPLY        *DUPRep       = &InMessage->Buffer.DUPSt.DUPRep;

    CtiPointDataMsg   *pData    = NULL;
    CtiPointNumeric   *pNumericPoint = NULL;

    CtiReturnMsg   *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                            RWCString(InMessage->Return.CommandStr),
                                            RWCString(),
                                            InMessage->EventCode & 0x7fff,
                                            InMessage->Return.RouteID,
                                            InMessage->Return.MacroOffset,
                                            InMessage->Return.Attempt,
                                            InMessage->Return.TrxID,
                                            InMessage->Return.UserID);
    AlphaA1ScanData_t  *ptr = (AlphaA1ScanData_t *)DUPRep->Message;
    RWTime peakTime;

    // here is the class we requested
    setReadClass (InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[2]);

    //check scan pending
    if (isScanPending())
    {

        if ((tmpCurrentState == StateScanAbort)  ||
            (tmpCurrentState == StateHandshakeAbort) ||
            (InMessage->EventCode != 0))
        {
            CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

            if (pMsg != NULL)
            {
                pMsg->insert( -1 );           // This is the dispatch token and is unimplemented at this time
                pMsg->insert(OP_DEVICEID);    // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                pMsg->insert(getID());             // The id (device or point which failed)
                pMsg->insert(ScanRateGeneral);      // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

                if (InMessage->EventCode != 0)
                {
                    pMsg->insert(InMessage->EventCode);
                }
                else
                {
                    pMsg->insert(GeneralScanAborted);
                }
            }

            insertPointIntoReturnMsg (pMsg, pPIL);
        }
        else
        {
            // check status
            bDoStatusCheck = TRUE;

            // loop through my three offsets
            for (i  = 1;
                i <= OFFSET_HIGHEST_CURRENT_OFFSET;
                i ++)
            {
                // grab the point based on device and offset
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(i, AnalogPointType)) != NULL)
                {
                    // only build one of these if the point was found and configured correctly
                    if (getMeterDataFromScanStruct (i, PValue, peakTime, ptr))
                    {
                        PValue = pNumericPoint->computeValueForUOM(PValue);

                        verifyAndAddPointToReturnMsg (pNumericPoint->getPointID(),
                                                      PValue,
                                                      NormalQuality,
                                                      peakTime,
                                                      pPIL);
                    }
                }
            }
        }
    }

    // reset this flag so device makes it on the queue later
    resetScanPending();

    /*
     *  Do some common status point update stuff like power fail etc.
     */

    if (bDoStatusCheck)
    {
        if (DUPRep->Status & ALPHA_POWER_FAIL)
        {
            bDoResetStatus = TRUE;
        }

        // grab the point based on device and offset
        if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(10, StatusPointType)) != NULL)
        {
            // set pvalue
            PValue = (FLOAT) (DUPRep->Status & ALPHA_POWER_FAIL ? CLOSED : OPENED);

            // add CTIDBG_new message with plugged
            pData = CTIDBG_new CtiPointDataMsg(pNumericPoint->getPointID(),
                                        PValue,
                                        NonUpdatedQuality,
                                        AnalogPointType);
            if (pData != NULL)
            {
                if (pPIL != NULL)
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

        if (DUPRep->Status & ALPHA_DEMAND_RESET)
        {
            bDoResetStatus = TRUE;
        }

        if (DUPRep->Status & ~(ALPHA_DEMAND_RESET | ALPHA_POWER_FAIL))
        {
            bDoResetStatus = TRUE;
        }

        if (bDoResetStatus)
        {
            // DeviceRecord->ScanStatus |= SCANSCANPENDING;
            // AlphaResetStatus ( RemoteRecord, DeviceRecord, 0, 0);
        }
    }

    if (pPIL->PointData().entries() > 0)
    {
        retList.insert( pPIL );
    }
    else
    {
        delete pPIL;
    }
    pPIL = NULL;

    if( DebugLevel & 0x0001 )
        ResultDisplay(InMessage);
    return(NORMAL);
}

INT CtiDeviceAlphaA1::decodeResultLoadProfile (INMESS *InMessage,
                                               RWTime &TimeNow,
                                               RWTPtrSlist< CtiMessage >   &vgList,
                                               RWTPtrSlist< CtiMessage > &retList,
                                               RWTPtrSlist< OUTMESS > &outList)
{

    int retCode = NORMAL;
    DIALUPREPLY        *DUPRep       = &InMessage->Buffer.DUPSt.DUPRep;
    AlphaA1LoadProfile_t  *ptr = (AlphaA1LoadProfile_t *)DUPRep->Message;


    BYTEUSHORT        flip;
    CtiPointDataMsg   *pData    = NULL;
    CtiPointNumeric   *pNumericPoint = NULL;
    int               cnt,scratch;
    DOUBLE            intervalData;

    ULONG             currentIntervalTS;
    ULONG             lastLPTime = getLastLPTime().seconds();

    int               dataQuality = NormalQuality;

    CtiReturnMsg   *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                            RWCString(InMessage->Return.CommandStr),
                                            RWCString(),
                                            InMessage->EventCode & 0x7fff,
                                            InMessage->Return.RouteID,
                                            InMessage->Return.MacroOffset,
                                            InMessage->Return.Attempt,
                                            InMessage->Return.TrxID,
                                            InMessage->Return.UserID);

    CtiReturnMsg   *pLastLPIntervals = NULL;

    // alpha only supports 4 channels
    AlphaLPPointInfo_t   validLPPointInfo[4] = { {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1}};
    // if this is the last data, intervals only go to actual - 8
    if (ptr->finalDataFlag)
    {
        ptr->dataBytesActual -= 8;
    }
    /********************
    *  the A1 gives us the date/time of the last interval recorded at the end of the
    *  load profile stream.  Because of this, we are keeping the entire stream in a vector
    *  until we receive this value.  At that point, we will decode backwards to the
    * last load profile time
    *********************
    */
    for (int x=0; x < ptr->dataBytesActual; x+=2)
    {
        flip.ch[0] = ptr->loadProfileData[x+1];
        flip.ch[1] = ptr->loadProfileData[x];
        _sLPPulseVector.push_back (flip.sh);
    }

    // if true
    if (ptr->finalDataFlag)
    {
        /***********************
        *  dataBytesActual is number of data bytes
        *  the stop byte and the date/time info is
        *  in the loadProfile buffer after the data bytes
        ************************
        */
        RWDate lastIntervalDate((USHORT)BCDtoBase10(&ptr->loadProfileData[ptr->dataBytesActual+4],1),
                                (USHORT)BCDtoBase10(&ptr->loadProfileData[ptr->dataBytesActual+3],1),
                                (USHORT)BCDtoBase10(&ptr->loadProfileData[ptr->dataBytesActual+2],1)+2000);

        RWTime lastIntervalTS (lastIntervalDate,
                               (USHORT)BCDtoBase10(&ptr->loadProfileData[ptr->dataBytesActual+5],1),
                               (USHORT)BCDtoBase10(&ptr->loadProfileData[ptr->dataBytesActual+6],1),
                               (USHORT)BCDtoBase10(&ptr->loadProfileData[ptr->dataBytesActual+7],1));

        /*************************
        * Check the validity of the time received
        **************************
        */
        if (lastIntervalTS < (RWTime()-(2*86400)) ||
            (lastIntervalTS > (RWTime()+(2*86400))))
        {
            /***********************
            * if meter time is not within a 2 day window, its
            * invalid
            ************************
            */
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Aborting scan: Invalid last interval timestamp " << getName()  << " " << lastIntervalTS.asString()<< endl;
            }
        }
        else
        {
            // we're walking backwards here
            if (lastIntervalTS.seconds() > lastLPTime)
            {
                // this is our current last lp time
                setLastLPTime (lastIntervalTS.seconds());

                // working timestamp
                currentIntervalTS = lastIntervalTS.seconds();

                int     offset = _sLPPulseVector.size() - 1;


                // now, loop through the channels and get the point offsets and multipliers
                for (int pointCnt=0; pointCnt < ptr->class14.numberOfChannels; pointCnt++)
                {
                    findLPDataPoint (validLPPointInfo[pointCnt], ptr->class14.channelInput[pointCnt], ptr->class14);

                }

                // walk until either we get to the last time or we get to the end of our vector
                while (currentIntervalTS > lastLPTime && offset > -1)
                {
                    // delete the last set and start over
                    if (pLastLPIntervals != NULL)
                    {
                        delete pLastLPIntervals;
                        pLastLPIntervals = NULL;
                    }

                    pLastLPIntervals = CTIDBG_new CtiReturnMsg(getID(),
                                                        RWCString(InMessage->Return.CommandStr),
                                                        RWCString(),
                                                        InMessage->EventCode & 0x7fff,
                                                        InMessage->Return.RouteID,
                                                        InMessage->Return.MacroOffset,
                                                        InMessage->Return.Attempt,
                                                        InMessage->Return.TrxID,
                                                        InMessage->Return.UserID);

                    for (int currentChannel = ptr->class14.numberOfChannels-1; currentChannel > -1 ; currentChannel--)
                    {
                        // perform parity checking on reading
                        cnt = 0;
                        for (scratch = 0x0001; scratch <= 0x8000; scratch <<=1)
                        {
                            if (_sLPPulseVector[offset] & scratch)
                            {
                                cnt++;
                            }
                        }

                        // check for odd parity for each reading
                        if ((cnt % 2) == 1)
                        {
                            if (_sLPPulseVector[offset] & 0x4000)
                            {
                                dataQuality = PartialIntervalQuality;
                            }

                            // data is contained in bits 0 - 13
                            _sLPPulseVector[offset] &= 0x3FFF;

                            intervalData = ((DOUBLE)_sLPPulseVector[offset] * ptr->class14.scalingFactor *
                                            ptr->class0.wattHoursPerRevolution * (1.0/ (DOUBLE)ptr->class0.pulsesPerRevolution) *
                                            validLPPointInfo[currentChannel].multiplier *
                                            (DOUBLE)(60.0 /ptr->class14.intervalLength) / 1000.0);

                            verifyAndAddPointToReturnMsg (validLPPointInfo[currentChannel].pointId,
                                                          intervalData,
                                                          dataQuality,
                                                          currentIntervalTS,
                                                          pPIL,
                                                          TAG_POINT_LOAD_PROFILE_DATA);

                            verifyAndAddPointToReturnMsg (validLPPointInfo[currentChannel].pointId,
                                                          intervalData,
                                                          dataQuality,
                                                          currentIntervalTS,
                                                          pLastLPIntervals);
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Parity calculation failed " << getName() << endl;
                            }
                        }

                        offset--;
                    }

                    // switch the time
                    currentIntervalTS -= ptr->class14.intervalLength * 60;
                }
            }
        }

        // must erase the entire vector
        _sLPPulseVector.erase(_sLPPulseVector.begin(), _sLPPulseVector.end());
    }



    // send the whole mess to dispatch
    if (pPIL->PointData().entries() > 0)
    {
        retList.insert( pPIL );
    }
    else
    {
        delete pPIL;
    }
    pPIL = NULL;

    /***************************
    *  this list of point changes will be sent without the load profile flag set
    *  allowing us to display the last interals in a report
    *  currently dispatch does not route load profile data
    ****************************
    */
    if (pLastLPIntervals != NULL)
    {
        if (pLastLPIntervals->PointData().entries() > 0)
        {
            retList.insert(pLastLPIntervals);
        }
        else
        {
            delete pLastLPIntervals;
        }
        pLastLPIntervals = NULL;
    }

    return retCode;
}


INT CtiDeviceAlphaA1::ResultDisplay(INMESS *InMessage)
{
    DIALUPREPLY        *DUPRep       = &InMessage->Buffer.DUPSt.DUPRep;
    AlphaA1ScanData_t  *ptr = (AlphaA1ScanData_t *)DUPRep->Message;
    CHAR buffer[200];
    /**************************
    * lazy way to do this
    ***************************
    */

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Result display for device " << getName() << " in progress " << endl;


        sprintf(buffer,"--------- Class 0 ---------");
        dout << endl << buffer << endl;
        sprintf(buffer,"DemandDecimals= %f",ptr->Real.class0.demandDecimals);
        dout << buffer << endl;
        sprintf(buffer,"EnergyDecimals= %f",ptr->Real.class0.energyDecimals);
        dout << buffer << endl;


        // class 11
        sprintf(buffer,"--------- Class 11 ---------");
        dout << endl << buffer << endl;

        sprintf(buffer,"                    KWH        KW         Cum            Date/Time");
        dout << buffer << endl;
        sprintf(buffer,"Block 1          %9.3f",ptr->Real.class11.KWH1);
        dout << buffer << endl;
        sprintf(buffer,"Rate A           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.AKWH1,
                ptr->Real.class11.AKW1,
                ptr->Real.class11.AKWCUM1,
                ptr->Real.class11.ATD1.Month,
                ptr->Real.class11.ATD1.Day,
                ptr->Real.class11.ATD1.Year,
                ptr->Real.class11.ATD1.Hour,
                ptr->Real.class11.ATD1.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate B           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.BKWH1,
                ptr->Real.class11.BKW1,
                ptr->Real.class11.BKWCUM1,
                ptr->Real.class11.BTD1.Month,
                ptr->Real.class11.BTD1.Day,
                ptr->Real.class11.BTD1.Year,
                ptr->Real.class11.BTD1.Hour,
                ptr->Real.class11.BTD1.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate C           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.CKWH1,
                ptr->Real.class11.CKW1,
                ptr->Real.class11.CKWCUM1,
                ptr->Real.class11.CTD1.Month,
                ptr->Real.class11.CTD1.Day,
                ptr->Real.class11.CTD1.Year,
                ptr->Real.class11.CTD1.Hour,
                ptr->Real.class11.CTD1.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate D           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d\n",
                ptr->Real.class11.DKWH1,
                ptr->Real.class11.DKW1,
                ptr->Real.class11.DKWCUM1,
                ptr->Real.class11.DTD1.Month,
                ptr->Real.class11.DTD1.Day,
                ptr->Real.class11.DTD1.Year,
                ptr->Real.class11.DTD1.Hour,
                ptr->Real.class11.DTD1.Minute);
        dout << buffer << endl << endl;

        sprintf(buffer,"Block 2          %9.3f\n",ptr->Real.class11.KWH2);
        dout << buffer << endl;
        sprintf(buffer,"Rate A           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.AKWH2,
                ptr->Real.class11.AKW2,
                ptr->Real.class11.AKWCUM2,
                ptr->Real.class11.ATD2.Month,
                ptr->Real.class11.ATD2.Day,
                ptr->Real.class11.ATD2.Year,
                ptr->Real.class11.ATD2.Hour,
                ptr->Real.class11.ATD2.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate B           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.BKWH2,
                ptr->Real.class11.BKW2,
                ptr->Real.class11.BKWCUM2,
                ptr->Real.class11.BTD2.Month,
                ptr->Real.class11.BTD2.Day,
                ptr->Real.class11.BTD2.Year,
                ptr->Real.class11.BTD2.Hour,
                ptr->Real.class11.BTD2.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate C           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.CKWH2,
                ptr->Real.class11.CKW2,
                ptr->Real.class11.CKWCUM2,
                ptr->Real.class11.CTD2.Month,
                ptr->Real.class11.CTD2.Day,
                ptr->Real.class11.CTD2.Year,
                ptr->Real.class11.CTD2.Hour,
                ptr->Real.class11.CTD2.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate D           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d\n",
                ptr->Real.class11.DKWH2,
                ptr->Real.class11.DKW2,
                ptr->Real.class11.DKWCUM2,
                ptr->Real.class11.DTD2.Month,
                ptr->Real.class11.DTD2.Day,
                ptr->Real.class11.DTD2.Year,
                ptr->Real.class11.DTD2.Hour,
                ptr->Real.class11.DTD2.Minute);
        dout << buffer << endl << endl;

        sprintf(buffer,"Block 3          %9.3f\n",ptr->Real.class11.KWH3);
        dout << buffer << endl;
        sprintf(buffer,"Rate A           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.AKWH3,
                ptr->Real.class11.AKW3,
                ptr->Real.class11.AKWCUM3,
                ptr->Real.class11.ATD3.Month,
                ptr->Real.class11.ATD3.Day,
                ptr->Real.class11.ATD3.Year,
                ptr->Real.class11.ATD3.Hour,
                ptr->Real.class11.ATD3.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate B           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.BKWH3,
                ptr->Real.class11.BKW3,
                ptr->Real.class11.BKWCUM3,
                ptr->Real.class11.BTD3.Month,
                ptr->Real.class11.BTD3.Day,
                ptr->Real.class11.BTD3.Year,
                ptr->Real.class11.BTD3.Hour,
                ptr->Real.class11.BTD3.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate C           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.CKWH3,
                ptr->Real.class11.CKW3,
                ptr->Real.class11.CKWCUM3,
                ptr->Real.class11.CTD3.Month,
                ptr->Real.class11.CTD3.Day,
                ptr->Real.class11.CTD3.Year,
                ptr->Real.class11.CTD3.Hour,
                ptr->Real.class11.CTD3.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate D           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.DKWH3,
                ptr->Real.class11.DKW3,
                ptr->Real.class11.DKWCUM3,
                ptr->Real.class11.DTD3.Month,
                ptr->Real.class11.DTD3.Day,
                ptr->Real.class11.DTD3.Year,
                ptr->Real.class11.DTD3.Hour,
                ptr->Real.class11.DTD3.Minute);
        dout << buffer << endl << endl;

        sprintf(buffer,"Block 4          %9.3f\n",ptr->Real.class11.KWH4);
        dout << buffer << endl;
        sprintf(buffer,"Rate A           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.AKWH4,
                ptr->Real.class11.AKW4,
                ptr->Real.class11.AKWCUM4,
                ptr->Real.class11.ATD4.Month,
                ptr->Real.class11.ATD4.Day,
                ptr->Real.class11.ATD4.Year,
                ptr->Real.class11.ATD4.Hour,
                ptr->Real.class11.ATD4.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate B           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.BKWH4,
                ptr->Real.class11.BKW4,
                ptr->Real.class11.BKWCUM4,
                ptr->Real.class11.BTD4.Month,
                ptr->Real.class11.BTD4.Day,
                ptr->Real.class11.BTD4.Year,
                ptr->Real.class11.BTD4.Hour,
                ptr->Real.class11.BTD4.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate C           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.CKWH4,
                ptr->Real.class11.CKW4,
                ptr->Real.class11.CKWCUM4,
                ptr->Real.class11.CTD4.Month,
                ptr->Real.class11.CTD4.Day,
                ptr->Real.class11.CTD4.Year,
                ptr->Real.class11.CTD4.Hour,
                ptr->Real.class11.CTD4.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate D           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.DKWH4,
                ptr->Real.class11.DKW4,
                ptr->Real.class11.DKWCUM4,
                ptr->Real.class11.DTD4.Month,
                ptr->Real.class11.DTD4.Day,
                ptr->Real.class11.DTD4.Year,
                ptr->Real.class11.DTD4.Hour,
                ptr->Real.class11.DTD4.Minute);
        dout << buffer << endl << endl;

        sprintf(buffer,"Block 5          %9.3f",ptr->Real.class11.KWH5);
        dout << buffer << endl;
        sprintf(buffer,"Rate A           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.AKWH5,
                ptr->Real.class11.AKW5,
                ptr->Real.class11.AKWCUM5,
                ptr->Real.class11.ATD5.Month,
                ptr->Real.class11.ATD5.Day,
                ptr->Real.class11.ATD5.Year,
                ptr->Real.class11.ATD5.Hour,
                ptr->Real.class11.ATD5.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate B           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.BKWH5,
                ptr->Real.class11.BKW5,
                ptr->Real.class11.BKWCUM5,
                ptr->Real.class11.BTD5.Month,
                ptr->Real.class11.BTD5.Day,
                ptr->Real.class11.BTD5.Year,
                ptr->Real.class11.BTD5.Hour,
                ptr->Real.class11.BTD5.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate C           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.CKWH5,
                ptr->Real.class11.CKW5,
                ptr->Real.class11.CKWCUM5,
                ptr->Real.class11.CTD5.Month,
                ptr->Real.class11.CTD5.Day,
                ptr->Real.class11.CTD5.Year,
                ptr->Real.class11.CTD5.Hour,
                ptr->Real.class11.CTD5.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate D           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.DKWH5,
                ptr->Real.class11.DKW5,
                ptr->Real.class11.DKWCUM5,
                ptr->Real.class11.DTD5.Month,
                ptr->Real.class11.DTD5.Day,
                ptr->Real.class11.DTD5.Year,
                ptr->Real.class11.DTD5.Hour,
                ptr->Real.class11.DTD5.Minute);
        dout << buffer << endl << endl;

        sprintf(buffer,"Block 6          %9.3f",ptr->Real.class11.KWH6);
        dout << buffer << endl;
        sprintf(buffer,"Rate A           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.AKWH6,
                ptr->Real.class11.AKW6,
                ptr->Real.class11.AKWCUM6,
                ptr->Real.class11.ATD6.Month,
                ptr->Real.class11.ATD6.Day,
                ptr->Real.class11.ATD6.Year,
                ptr->Real.class11.ATD6.Hour,
                ptr->Real.class11.ATD6.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate B           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.BKWH6,
                ptr->Real.class11.BKW6,
                ptr->Real.class11.BKWCUM6,
                ptr->Real.class11.BTD6.Month,
                ptr->Real.class11.BTD6.Day,
                ptr->Real.class11.BTD6.Year,
                ptr->Real.class11.BTD6.Hour,
                ptr->Real.class11.BTD6.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate C           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.CKWH6,
                ptr->Real.class11.CKW6,
                ptr->Real.class11.CKWCUM6,
                ptr->Real.class11.CTD6.Month,
                ptr->Real.class11.CTD6.Day,
                ptr->Real.class11.CTD6.Year,
                ptr->Real.class11.CTD6.Hour,
                ptr->Real.class11.CTD6.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate D           %9.3f %9.3f %9.3f           %d/%d/%d %02d:%02d",
                ptr->Real.class11.DKWH6,
                ptr->Real.class11.DKW6,
                ptr->Real.class11.DKWCUM6,
                ptr->Real.class11.DTD6.Month,
                ptr->Real.class11.DTD6.Day,
                ptr->Real.class11.DTD6.Year,
                ptr->Real.class11.DTD6.Hour,
                ptr->Real.class11.DTD6.Minute);
        dout << buffer << endl << endl;


        sprintf(buffer,"--------- Class 14 ---------");
        dout << endl << buffer << endl;
        sprintf(buffer,"TOU Blk 1 Config:  %d",ptr->Real.class14.touInput[0]);
        dout << buffer << endl;
        sprintf(buffer,"TOU Blk 2 Config:  %d",ptr->Real.class14.touInput[1]);
        dout << buffer << endl;
        sprintf(buffer,"TOU Blk 3 Config:  %d",ptr->Real.class14.touInput[2]);
        dout << buffer << endl;
        sprintf(buffer,"TOU Blk 4 Config:  %d",ptr->Real.class14.touInput[3]);
        dout << buffer << endl;
        sprintf(buffer,"TOU Blk 5 Config:  %d",ptr->Real.class14.touInput[4]);
        dout << buffer << endl;
        sprintf(buffer,"TOU Blk 6 Config:  %d",ptr->Real.class14.touInput[5]);
        dout << buffer << endl;
    }


    return(NORMAL);
}


INT CtiDeviceAlphaA1::reformatDataBuffer(BYTE *aInMessBuffer, ULONG &aTotalBytes)
{
    AlphaA1ScanData_t *ptr = (AlphaA1ScanData_t *)(_dataBuffer);
    int i;
    BYTEUSHORT flip;

    ptr->Real.class0.energyDecimals = 1.0;
    ptr->Real.class0.demandDecimals = 1.0;

    // if we were able to retrieve class 0
    if (ptr->Real.class0.valid)
    {
        // class zero
        FLOAT energyCnt   = (FLOAT) BCDtoBase10(&ptr->Byte.class0.DPLOCE ,     1);
        FLOAT demandCnt   = (FLOAT) BCDtoBase10(&ptr->Byte.class0.DPLOCD ,     1);

        // default and then run the loops

        for (i = 0; i < demandCnt; i++)
            ptr->Real.class0.demandDecimals *= 10.0;

        for (i = 0; i < energyCnt; i++)
            ptr->Real.class0.energyDecimals *= 10.0;

        ptr->Real.class0.wattHoursPerRevolution =  (ULONG)BCDtoBase10(ptr->Byte.class0.UKH,     3) / 1000.0;
        ptr->Real.class0.pulsesPerRevolution = (USHORT) BCDtoBase10(&ptr->Byte.class0.UPR,     1);

    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Unable to read " << getName() << " Class 0 defaulting decimals" << endl;
        }
    }

    // default to KVAR meter
    ptr->Real.class8.meterType = ALPHA_A1R;
    if (ptr->Real.class8.valid)
    {
        ptr->Real.class8.firmwareSpec = (ULONG)BCDtoBase10 (ptr->Byte.class8.SSPEC, 3);
        ptr->Real.class8.groupNo      = (USHORT)BCDtoBase10 (&ptr->Byte.class8.SSPEC[3],1);
        ptr->Real.class8.revisionNo      = (USHORT)BCDtoBase10 (&ptr->Byte.class8.SSPEC[4],1);

        if (ptr->Byte.class8.PCODE[0] & 0x04)
        {
            ptr->Real.class8.meterType = ALPHA_A1K;
        }
        else
        {
            ptr->Real.class8.meterType = ALPHA_A1R;
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Unable to read " << getName() << " Class 8 defaulting type" << endl;
        }
    }

    if (ptr->Real.class11.valid)
    {

        // Block 1
        ptr->Real.class11.KWH1      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.KWH1,    3)) / ptr->Real.class0.energyDecimals;

        ptr->Real.class11.AKWH1     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKWH1,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.AKW1      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKW1,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.AKWCUM1   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKWCUM1,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.ATD1.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD1.Year,   1);
        ptr->Real.class11.ATD1.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD1.Month,  1);
        ptr->Real.class11.ATD1.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD1.Day,    1);
        ptr->Real.class11.ATD1.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD1.Hour,   1);
        ptr->Real.class11.ATD1.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD1.Minute, 1);

        ptr->Real.class11.BKWH1     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKWH1,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.BKW1      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKW1,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.BKWCUM1   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKWCUM1,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.BTD1.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD1.Year,   1);
        ptr->Real.class11.BTD1.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD1.Month,  1);
        ptr->Real.class11.BTD1.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD1.Day,    1);
        ptr->Real.class11.BTD1.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD1.Hour,   1);
        ptr->Real.class11.BTD1.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD1.Minute, 1);

        ptr->Real.class11.CKWH1     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKWH1,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.CKW1      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKW1,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.CKWCUM1   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKWCUM1,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.CTD1.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD1.Year,   1);
        ptr->Real.class11.CTD1.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD1.Month,  1);
        ptr->Real.class11.CTD1.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD1.Day,    1);
        ptr->Real.class11.CTD1.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD1.Hour,   1);
        ptr->Real.class11.CTD1.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD1.Minute, 1);

        ptr->Real.class11.DKWH1     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKWH1,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.DKW1      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKW1,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.DKWCUM1   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKWCUM1,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.DTD1.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD1.Year,   1);
        ptr->Real.class11.DTD1.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD1.Month,  1);
        ptr->Real.class11.DTD1.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD1.Day,    1);
        ptr->Real.class11.DTD1.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD1.Hour,   1);
        ptr->Real.class11.DTD1.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD1.Minute, 1);


        ptr->Real.class11.KWH2      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.KWH2,    3)) / ptr->Real.class0.energyDecimals;

        ptr->Real.class11.AKWH2     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKWH2,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.AKW2      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKW2,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.AKWCUM2   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKWCUM2,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.ATD2.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD2.Year,   1);
        ptr->Real.class11.ATD2.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD2.Month,  1);
        ptr->Real.class11.ATD2.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD2.Day,    1);
        ptr->Real.class11.ATD2.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD2.Hour,   1);
        ptr->Real.class11.ATD2.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD2.Minute, 1);

        ptr->Real.class11.BKWH2     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKWH2,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.BKW2      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKW2,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.BKWCUM2   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKWCUM2,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.BTD2.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD2.Year,   1);
        ptr->Real.class11.BTD2.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD2.Month,  1);
        ptr->Real.class11.BTD2.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD2.Day,    1);
        ptr->Real.class11.BTD2.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD2.Hour,   1);
        ptr->Real.class11.BTD2.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD2.Minute, 1);

        ptr->Real.class11.CKWH2     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKWH2,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.CKW2      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKW2,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.CKWCUM2   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKWCUM2,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.CTD2.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD2.Year,   1);
        ptr->Real.class11.CTD2.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD2.Month,  1);
        ptr->Real.class11.CTD2.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD2.Day,    1);
        ptr->Real.class11.CTD2.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD2.Hour,   1);
        ptr->Real.class11.CTD2.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD2.Minute, 1);

        ptr->Real.class11.DKWH2     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKWH2,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.DKW2      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKW2,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.DKWCUM2   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKWCUM2,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.DTD2.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD2.Year,   1);
        ptr->Real.class11.DTD2.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD2.Month,  1);
        ptr->Real.class11.DTD2.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD2.Day,    1);
        ptr->Real.class11.DTD2.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD2.Hour,   1);
        ptr->Real.class11.DTD2.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD2.Minute, 1);


        ptr->Real.class11.KWH3      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.KWH3,    3)) / ptr->Real.class0.energyDecimals;

        ptr->Real.class11.AKWH3     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKWH3,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.AKW3      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKW3,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.AKWCUM3   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKWCUM3,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.ATD3.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD3.Year,   1);
        ptr->Real.class11.ATD3.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD3.Month,  1);
        ptr->Real.class11.ATD3.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD3.Day,    1);
        ptr->Real.class11.ATD3.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD3.Hour,   1);
        ptr->Real.class11.ATD3.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD3.Minute, 1);

        ptr->Real.class11.BKWH3     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKWH3,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.BKW3      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKW3,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.BKWCUM3   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKWCUM3,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.BTD3.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD3.Year,   1);
        ptr->Real.class11.BTD3.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD3.Month,  1);
        ptr->Real.class11.BTD3.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD3.Day,    1);
        ptr->Real.class11.BTD3.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD3.Hour,   1);
        ptr->Real.class11.BTD3.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD3.Minute, 1);

        ptr->Real.class11.CKWH3     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKWH3,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.CKW3      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKW3,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.CKWCUM3   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKWCUM3,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.CTD3.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD3.Year,   1);
        ptr->Real.class11.CTD3.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD3.Month,  1);
        ptr->Real.class11.CTD3.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD3.Day,    1);
        ptr->Real.class11.CTD3.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD3.Hour,   1);
        ptr->Real.class11.CTD3.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD3.Minute, 1);

        ptr->Real.class11.DKWH3     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKWH3,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.DKW3      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKW3,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.DKWCUM3   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKWCUM3,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.DTD3.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD3.Year,   1);
        ptr->Real.class11.DTD3.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD3.Month,  1);
        ptr->Real.class11.DTD3.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD3.Day,    1);
        ptr->Real.class11.DTD3.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD3.Hour,   1);
        ptr->Real.class11.DTD3.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD3.Minute, 1);

        ptr->Real.class11.KWH4      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.KWH4,    3)) / ptr->Real.class0.energyDecimals;

        ptr->Real.class11.AKWH4     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKWH4,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.AKW4      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKW4,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.AKWCUM4   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKWCUM4,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.ATD4.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD4.Year,   1);
        ptr->Real.class11.ATD4.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD4.Month,  1);
        ptr->Real.class11.ATD4.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD4.Day,    1);
        ptr->Real.class11.ATD4.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD4.Hour,   1);
        ptr->Real.class11.ATD4.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD4.Minute, 1);

        ptr->Real.class11.BKWH4     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKWH4,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.BKW4      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKW4,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.BKWCUM4   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKWCUM4,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.BTD4.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD4.Year,   1);
        ptr->Real.class11.BTD4.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD4.Month,  1);
        ptr->Real.class11.BTD4.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD4.Day,    1);
        ptr->Real.class11.BTD4.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD4.Hour,   1);
        ptr->Real.class11.BTD4.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD4.Minute, 1);

        ptr->Real.class11.CKWH4     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKWH4,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.CKW4      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKW4,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.CKWCUM4   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKWCUM4,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.CTD4.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD4.Year,   1);
        ptr->Real.class11.CTD4.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD4.Month,  1);
        ptr->Real.class11.CTD4.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD4.Day,    1);
        ptr->Real.class11.CTD4.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD4.Hour,   1);
        ptr->Real.class11.CTD4.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD4.Minute, 1);

        ptr->Real.class11.DKWH4     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKWH4,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.DKW4      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKW4,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.DKWCUM4   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKWCUM4,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.DTD4.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD4.Year,   1);
        ptr->Real.class11.DTD4.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD4.Month,  1);
        ptr->Real.class11.DTD4.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD4.Day,    1);
        ptr->Real.class11.DTD4.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD4.Hour,   1);
        ptr->Real.class11.DTD4.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD4.Minute, 1);


        ptr->Real.class11.KWH5      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.KWH5,    3)) / ptr->Real.class0.energyDecimals;

        ptr->Real.class11.AKWH5     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKWH5,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.AKW5      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKW5,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.AKWCUM5   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKWCUM5,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.ATD5.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD5.Year,   1);
        ptr->Real.class11.ATD5.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD5.Month,  1);
        ptr->Real.class11.ATD5.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD5.Day,    1);
        ptr->Real.class11.ATD5.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD5.Hour,   1);
        ptr->Real.class11.ATD5.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD5.Minute, 1);

        ptr->Real.class11.BKWH5     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKWH5,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.BKW5      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKW5,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.BKWCUM5   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKWCUM5,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.BTD5.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD5.Year,   1);
        ptr->Real.class11.BTD5.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD5.Month,  1);
        ptr->Real.class11.BTD5.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD5.Day,    1);
        ptr->Real.class11.BTD5.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD5.Hour,   1);
        ptr->Real.class11.BTD5.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD5.Minute, 1);

        ptr->Real.class11.CKWH5     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKWH5,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.CKW5      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKW5,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.CKWCUM5   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKWCUM5,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.CTD5.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD5.Year,   1);
        ptr->Real.class11.CTD5.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD5.Month,  1);
        ptr->Real.class11.CTD5.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD5.Day,    1);
        ptr->Real.class11.CTD5.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD5.Hour,   1);
        ptr->Real.class11.CTD5.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD5.Minute, 1);

        ptr->Real.class11.DKWH5     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKWH5,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.DKW5      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKW5,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.DKWCUM5   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKWCUM5,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.DTD5.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD5.Year,   1);
        ptr->Real.class11.DTD5.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD5.Month,  1);
        ptr->Real.class11.DTD5.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD5.Day,    1);
        ptr->Real.class11.DTD5.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD5.Hour,   1);
        ptr->Real.class11.DTD5.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD5.Minute, 1);

        ptr->Real.class11.KWH6      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.KWH6,    3)) / ptr->Real.class0.energyDecimals;

        ptr->Real.class11.AKWH6     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKWH6,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.AKW6      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKW6,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.AKWCUM6   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.AKWCUM6,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.ATD6.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD6.Year,   1);
        ptr->Real.class11.ATD6.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD6.Month,  1);
        ptr->Real.class11.ATD6.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD6.Day,    1);
        ptr->Real.class11.ATD6.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD6.Hour,   1);
        ptr->Real.class11.ATD6.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD6.Minute, 1);

        ptr->Real.class11.BKWH6     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKWH6,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.BKW6      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKW6,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.BKWCUM6   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.BKWCUM6,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.BTD6.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD6.Year,   1);
        ptr->Real.class11.BTD6.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD6.Month,  1);
        ptr->Real.class11.BTD6.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD6.Day,    1);
        ptr->Real.class11.BTD6.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD6.Hour,   1);
        ptr->Real.class11.BTD6.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD6.Minute, 1);

        ptr->Real.class11.CKWH6     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKWH6,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.CKW6      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKW6,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.CKWCUM6   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.CKWCUM6,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.CTD6.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD6.Year,   1);
        ptr->Real.class11.CTD6.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD6.Month,  1);
        ptr->Real.class11.CTD6.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD6.Day,    1);
        ptr->Real.class11.CTD6.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD6.Hour,   1);
        ptr->Real.class11.CTD6.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD6.Minute, 1);

        ptr->Real.class11.DKWH6     =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKWH6,    3)) / ptr->Real.class0.energyDecimals;
        ptr->Real.class11.DKW6      =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKW6,    3)) / ptr->Real.class0.demandDecimals;
        ptr->Real.class11.DKWCUM6   =  ((FLOAT)  BCDtoBase10 (ptr->Byte.class11.DKWCUM6,    3)) / ptr->Real.class0.demandDecimals;

        ptr->Real.class11.DTD6.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD6.Year,   1);
        ptr->Real.class11.DTD6.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD6.Month,  1);
        ptr->Real.class11.DTD6.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD6.Day,    1);
        ptr->Real.class11.DTD6.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD6.Hour,   1);
        ptr->Real.class11.DTD6.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD6.Minute, 1);


        /**************************
        *
        *  skipping the coincident block until needed  DLS
        *
        ***************************
        */
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Unable to read " << getName() << " Class 11 billing data not read" << endl;
        }
    }

    // default these to most likely settings
    ptr->Real.class14.channelInput[0] = A1_KW_DELIVERED;
    if (ptr->Real.class8.meterType)
        ptr->Real.class14.channelInput[1] = A1_KVA_DELIVERED;
    else
        ptr->Real.class14.channelInput[1] = A1_REACTIVE_DELIVERED;

    ptr->Real.class14.channelInput[2] = A1_DISABLED;
    ptr->Real.class14.channelInput[3] = A1_DISABLED;
    ptr->Real.class14.channelInput[4] = A1_DISABLED;
    ptr->Real.class14.channelInput[5] = A1_DISABLED;

    if (ptr->Real.class14.valid)
    {
        ptr->Real.class14.touInput[0] = touBlockMapping (ptr->Byte.class14.TBLKCF1, ptr->Real.class8.meterType);
        ptr->Real.class14.touInput[1] = touBlockMapping (ptr->Byte.class14.TBLKCF2, ptr->Real.class8.meterType);
        ptr->Real.class14.touInput[2] = touBlockMapping (ptr->Byte.class14.TBLKCF3, ptr->Real.class8.meterType);
        ptr->Real.class14.touInput[3] = touBlockMapping (ptr->Byte.class14.TBLKCF4, ptr->Real.class8.meterType);
        ptr->Real.class14.touInput[4] = touBlockMapping (ptr->Byte.class14.TBLKCF5, ptr->Real.class8.meterType);
        ptr->Real.class14.touInput[5] = touBlockMapping (ptr->Byte.class14.TBLKCF6, ptr->Real.class8.meterType);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Unable to read " << getName() << " Class 14 TOU inputs defaulted" << endl;
        }
    }

    memcpy (aInMessBuffer, _dataBuffer, sizeof (AlphaA1ScanData_t));
    aTotalBytes = ptr->totalByteCount;
    ptr->bDataIsReal = TRUE;
    return NORMAL;
}

INT CtiDeviceAlphaA1::copyLoadProfileData(BYTE *aInMessBuffer, ULONG &aTotalBytes)
{
    memcpy(aInMessBuffer, _loadProfileBuffer, sizeof (AlphaA1LoadProfile_t));
    aTotalBytes = sizeof (AlphaA1LoadProfile_t);
    return NORMAL;
}


LONG CtiDeviceAlphaA1::findLPDataPoint (AlphaLPPointInfo_t &point, USHORT aMapping, AlphaA1Class14Real_t &class14)
{
    LONG retCode = NORMAL;
    CtiPointNumeric   *pNumericPoint = NULL;

    // always set the metric
    point.mapping = aMapping;

    switch (aMapping)
    {
        case A1_KW_DELIVERED:
            {
                // looking for demand point
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_KW, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.mapping = aMapping;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }
        case A1_REACTIVE_DELIVERED:
            {
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_KVAR, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.mapping = aMapping;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }
        case A1_KVA_DELIVERED:
            {
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_KVA, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.mapping = aMapping;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }
        case A1_REACTIVE_QUADRANT1:
            {
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_QUADRANT1_KVAR, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.mapping = aMapping;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }
        case A1_REACTIVE_QUADRANT2:
            {
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_QUADRANT2_KVAR, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.mapping = aMapping;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }

        case A1_REACTIVE_QUADRANT3:
            {
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_QUADRANT3_KVAR, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.mapping = aMapping;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }
        case A1_REACTIVE_QUADRANT4:
            {
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_QUADRANT4_KVAR, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.mapping = aMapping;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }

        case A1_TOU_BLK1:
            {
                retCode = findLPDataPoint (point, class14.touInput[0] , class14);
                break;
            }
        case A1_TOU_BLK2:
            {
                retCode = findLPDataPoint (point, class14.touInput[1] , class14);
                break;
            }
        case A1_TOU_BLK3:
            {
                retCode = findLPDataPoint (point, class14.touInput[2] , class14);
                break;
            }
        case A1_TOU_BLK4:
            {
                retCode = findLPDataPoint (point, class14.touInput[3] , class14);
                break;
            }
        case A1_TOU_BLK5:
            {
                retCode = findLPDataPoint (point, class14.touInput[4] , class14);
                break;
            }
        case A1_TOU_BLK6:
            {
                retCode = findLPDataPoint (point, class14.touInput[5] , class14);
                break;
            }

        case A1_DISABLED:
        case A1_KW_RECEIVED:
        case A1_KVA_RECEIVED:
        case A1_REACTIVE_RECEIVED:
        default:
            {
                point.pointId = 0;
                point.multiplier = 1.0;
                retCode = !NORMAL;
            }
    }
    return retCode;
}




INT CtiDeviceAlphaA1::getA1ClassOffset(UINT Key, VOID *ptr)
{
    int   i;
    INT   Offset;
    INT   inputClass = *(int *)ptr;
    CHAR  *cptr;

    switch (Key)
    {
        case KEY_ALPHA_CLASS:
            {
                Offset = -1;
                for (i = 0; i < sizeof(A1Classes)/sizeof(CTI_alpha_class) ; i++)
                {
                    if (A1Classes[i].Class == inputClass)
                    {
                        Offset = i;
                        break;
                    }
                }
                break;
            }
        case KEY_ALPHA_DESC:
            {
                Offset = -1;
                for (i = 0; i < sizeof(A1Classes)/sizeof(CTI_alpha_class) ; i++)
                {
                    if (!strcmp((char*)ptr, A1Classes[i].Description))
                    {
                        Offset = i;
                        break;
                    }
                }
                break;
            }
        default:
            Offset = -1;
    }

    return Offset;
}


// type parameter:  1 = A1K meter,  0=A1R
UCHAR CtiDeviceAlphaA1::touBlockMapping (UCHAR config, USHORT type)
{
    UCHAR retCode=A1_DISABLED;

    if (config & 0x80)
    {
        retCode = A1_KW_DELIVERED;
    }
    else if (config & 0x40)
    {
        retCode = A1_KW_RECEIVED;
    }
    else if (config & 0x20)
    {
        if (type)
        {
            retCode = A1_KVA_DELIVERED;
        }
        else
        {
            retCode = A1_REACTIVE_DELIVERED;
        }
    }
    else if (config & 0x10)
    {
        if (type)
        {
            retCode = A1_KVA_RECEIVED;
        }
        else
        {
            retCode = A1_REACTIVE_RECEIVED;
        }
    }
    else if (config == 0x0F)
    {
        retCode = A1_REACTIVE_DELIVERED;
    }
    else if (config == 0x08)
    {
        retCode = A1_REACTIVE_QUADRANT4;
    }
    else if (config == 0x04)
    {
        retCode = A1_REACTIVE_QUADRANT3;
    }
    else if (config == 0x02)
    {
        retCode = A1_REACTIVE_QUADRANT2;
    }
    else if (config == 0x01)
    {
        retCode = A1_REACTIVE_QUADRANT1;
    }
    else
        retCode = A1_DISABLED;

    return retCode;
}


INT CtiDeviceAlphaA1::getA1FuncOffset(UINT Key, VOID *ptr)
{
    int   i;
    INT   Offset;
    INT   inputFunction = *(int *)ptr;
    CHAR  *cptr;

    switch (Key)
    {
        case KEY_ALPHA_FUNC:
            {
                Offset = -1;
                for (i = 0; i < sizeof(A1Functions)/sizeof(CTI_alpha_func) ; i++)
                {
                    if (A1Functions[i].Function == inputFunction)
                    {
                        Offset = i;
                        break;
                    }
                }
                break;
            }
        case KEY_ALPHA_DESC:
            {
                Offset = -1;
                for (i = 0; i < sizeof(A1Functions)/sizeof(CTI_alpha_func) ; i++)
                {
                    if (!strcmp((char*)ptr, A1Functions[i].Description))
                    {
                        Offset = i;
                        break;
                    }
                }
                break;
            }
        default:
            Offset = -1;
    }

    return Offset;
}

BOOL findBlockMapping (FLOAT &value, USHORT type,  AlphaA1ScanData_t *data)
{
    int x;
    BOOL retCode=FALSE;

    // loop through all blocks
    for (x=0; x < 6; x++)
    {
        if (data->Real.class14.touInput[x] == type)
        {
            switch (x)
            {
                case 0:
                    value = data->Real.class11.KWH1;
                    break;
                case 1:
                    value = data->Real.class11.KWH2;
                    break;
                case 2:
                    value = data->Real.class11.KWH3;
                    break;
                case 3:
                    value = data->Real.class11.KWH4;
                    break;
                case 4:
                    value = data->Real.class11.KWH5;
                    break;
                case 5:
                    value = data->Real.class11.KWH6;
                    break;
            }

            retCode = TRUE;
        }
    }
    return retCode;
}

USHORT CtiDeviceAlphaA1::getOffsetMapping (int aOffset)
{
    USHORT type=0;

    switch (aOffset)
    {
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW:
        case OFFSET_PEAK_KW_OR_RATE_A_KW:
        case OFFSET_RATE_B_KW:
        case OFFSET_RATE_C_KW:
        case OFFSET_RATE_D_KW:
        case OFFSET_RATE_E_KW:
        case OFFSET_TOTAL_KWH:
        case OFFSET_RATE_A_KWH:
        case OFFSET_RATE_B_KWH:
        case OFFSET_RATE_C_KWH:
        case OFFSET_RATE_D_KWH:
        case OFFSET_RATE_E_KWH:
            {
                type = A1_KW_DELIVERED;
                break;
            }
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR:
        case OFFSET_PEAK_KVAR_OR_RATE_A_KVAR:
        case OFFSET_RATE_B_KVAR:
        case OFFSET_RATE_C_KVAR:
        case OFFSET_RATE_D_KVAR:
        case OFFSET_RATE_E_KVAR:
        case OFFSET_TOTAL_KVARH:
        case OFFSET_RATE_A_KVARH:
        case OFFSET_RATE_B_KVARH:
        case OFFSET_RATE_C_KVARH:
        case OFFSET_RATE_D_KVARH:
        case OFFSET_RATE_E_KVARH:
            {
                type = A1_REACTIVE_DELIVERED;
                break;
            }
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA:
        case OFFSET_PEAK_KVA_OR_RATE_A_KVA:
        case OFFSET_RATE_B_KVA:
        case OFFSET_RATE_C_KVA:
        case OFFSET_RATE_D_KVA:
        case OFFSET_RATE_E_KVA:
        case OFFSET_TOTAL_KVAH:
        case OFFSET_RATE_A_KVAH:
        case OFFSET_RATE_B_KVAH:
        case OFFSET_RATE_C_KVAH:
        case OFFSET_RATE_D_KVAH:
        case OFFSET_RATE_E_KVAH:
            {
                type = A1_KVA_DELIVERED;
                break;
            }
        default:
            break;
    }
    return type;
}


USHORT CtiDeviceAlphaA1::getRate (int aOffset)
{
    USHORT rate;

    /* Get the value from InMessage */
    switch (aOffset)
    {
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW:
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR:
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA:
        case OFFSET_TOTAL_KWH:
        case OFFSET_TOTAL_KVARH:
        case OFFSET_TOTAL_KVAH:
            {
                rate = ALPHA_RATE_TOTAL;
                break;
            }
        case OFFSET_PEAK_KW_OR_RATE_A_KW:
        case OFFSET_PEAK_KVAR_OR_RATE_A_KVAR:
        case OFFSET_PEAK_KVA_OR_RATE_A_KVA:
        case OFFSET_RATE_A_KWH:
        case OFFSET_RATE_A_KVARH:
        case OFFSET_RATE_A_KVAH:
            {
                rate = ALPHA_RATE_A;
                break;
            }
        case OFFSET_RATE_B_KW:
        case OFFSET_RATE_B_KVAR:
        case OFFSET_RATE_B_KVA:
        case OFFSET_RATE_B_KWH:
        case OFFSET_RATE_B_KVARH:
        case OFFSET_RATE_B_KVAH:
            {
                rate = ALPHA_RATE_B;
                break;
            }
        case OFFSET_RATE_C_KW:
        case OFFSET_RATE_C_KVAR:
        case OFFSET_RATE_C_KVA:
        case OFFSET_RATE_C_KWH:
        case OFFSET_RATE_C_KVARH:
        case OFFSET_RATE_C_KVAH:
            {
                rate = ALPHA_RATE_C;
                break;
            }
        case OFFSET_RATE_D_KW:
        case OFFSET_RATE_D_KVAR:
        case OFFSET_RATE_D_KVA:
        case OFFSET_RATE_D_KWH:
        case OFFSET_RATE_D_KVARH:
        case OFFSET_RATE_D_KVAH:
            {
                rate = ALPHA_RATE_D;
                break;
            }
        case OFFSET_RATE_E_KW:
        case OFFSET_RATE_E_KVAR:
        case OFFSET_RATE_E_KVA:
        case OFFSET_RATE_E_KWH:
        case OFFSET_RATE_E_KVARH:
        case OFFSET_RATE_E_KVAH:
        default:

            {
                rate = ALPHA_UNDEFINED;
                break;
            }
    }
    return rate;
}


BOOL CtiDeviceAlphaA1::getMeterDataFromScanStruct (int aOffset,
                                                   DOUBLE &aValue,
                                                   RWTime &peak,
                                                   AlphaA1ScanData_t *aScanData)
{
    BOOL isValidPoint = FALSE;

    // this is initial value
    peak = rwEpoch;

    /* Get the value from InMessage */
    switch (aOffset)
    {
        case OFFSET_PEAK_KW_OR_RATE_A_KW:
        case OFFSET_RATE_B_KW:
        case OFFSET_RATE_C_KW:
        case OFFSET_RATE_D_KW:
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW:
        case OFFSET_PEAK_KVAR_OR_RATE_A_KVAR:
        case OFFSET_RATE_B_KVAR:
        case OFFSET_RATE_C_KVAR:
        case OFFSET_RATE_D_KVAR:
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR:
        case OFFSET_PEAK_KVA_OR_RATE_A_KVA:
        case OFFSET_RATE_B_KVA:
        case OFFSET_RATE_C_KVA:
        case OFFSET_RATE_D_KVA:
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA:
            {
                // if we find a matching mapping
                if (getRateValueFromBlock(aValue,
                                          ALPHA_DEMAND,
                                          getOffsetMapping (aOffset),
                                          getRate(aOffset),
                                          peak,
                                          aScanData))
                {
                    isValidPoint = TRUE;
                }
                break;
            }

        case OFFSET_TOTAL_KWH:
        case OFFSET_RATE_A_KWH:
        case OFFSET_RATE_B_KWH:
        case OFFSET_RATE_C_KWH:
        case OFFSET_RATE_D_KWH:
        case OFFSET_TOTAL_KVARH:
        case OFFSET_RATE_A_KVARH:
        case OFFSET_RATE_B_KVARH:
        case OFFSET_RATE_C_KVARH:
        case OFFSET_RATE_D_KVARH:
        case OFFSET_TOTAL_KVAH:
        case OFFSET_RATE_A_KVAH:
        case OFFSET_RATE_B_KVAH:
        case OFFSET_RATE_C_KVAH:
        case OFFSET_RATE_D_KVAH:
            {
                // if we find a matching mapping
                if (getRateValueFromBlock(aValue,
                                          ALPHA_ENERGY,
                                          getOffsetMapping (aOffset),
                                          getRate(aOffset),
                                          peak,
                                          aScanData))
                {
                    isValidPoint = TRUE;
                }
                break;
            }

        case OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE:
        case OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE:
        case OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE:
        case OFFSET_INSTANTANEOUS_PHASE_A_CURRENT:
        case OFFSET_INSTANTANEOUS_PHASE_B_CURRENT:
        case OFFSET_INSTANTANEOUS_PHASE_C_CURRENT:
        case OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT:
        case OFFSET_RATE_E_KW:
        case OFFSET_RATE_E_KWH:
        case OFFSET_RATE_E_KVAR:
        case OFFSET_RATE_E_KVARH:
        case OFFSET_RATE_E_KVA:
        case OFFSET_RATE_E_KVAH:

        default:
            isValidPoint = FALSE;
            break;
    }
    return isValidPoint;
}

BOOL CtiDeviceAlphaA1::getRateValueFromBlock (DOUBLE &aValue,
                                              USHORT aValueType,
                                              USHORT aBlockMapping,
                                              USHORT aRate,
                                              RWTime &aPeak,
                                              AlphaA1ScanData_t *data)
{
    int x;
    BOOL retCode=FALSE;

    if (data->Real.class14.touInput[0] == aBlockMapping)
    {
        if (aValueType == ALPHA_DEMAND)
            retCode = getDemandValueFromBlock1 (aValue, aRate, aPeak, data);
        else
            retCode = getEnergyValueFromBlock1 (aValue, aRate, aPeak, data);
    }
    else if (data->Real.class14.touInput[1] == aBlockMapping)
    {
        if (aValueType == ALPHA_DEMAND)
            retCode = getDemandValueFromBlock2 (aValue, aRate, aPeak, data);
        else
            retCode = getEnergyValueFromBlock2 (aValue, aRate, aPeak, data);
    }
    else if (data->Real.class14.touInput[2] == aBlockMapping)
    {
        if (aValueType == ALPHA_DEMAND)
            retCode = getDemandValueFromBlock3 (aValue, aRate, aPeak, data);
        else
            retCode = getEnergyValueFromBlock3 (aValue, aRate, aPeak, data);
    }
    else if (data->Real.class14.touInput[3] == aBlockMapping)
    {
        if (aValueType == ALPHA_DEMAND)
            retCode = getDemandValueFromBlock4 (aValue, aRate, aPeak, data);
        else
            retCode = getEnergyValueFromBlock4 (aValue, aRate, aPeak, data);
    }
    else if (data->Real.class14.touInput[4] == aBlockMapping)
    {
        if (aValueType == ALPHA_DEMAND)
            retCode = getDemandValueFromBlock5 (aValue, aRate, aPeak, data);
        else
            retCode = getEnergyValueFromBlock5 (aValue, aRate, aPeak, data);
    }
    else if (data->Real.class14.touInput[5] == aBlockMapping)
    {
        if (aValueType == ALPHA_DEMAND)
            retCode = getDemandValueFromBlock6 (aValue, aRate, aPeak, data);
        else
            retCode = getEnergyValueFromBlock6 (aValue, aRate, aPeak, data);
    }

    return retCode;
}

BOOL CtiDeviceAlphaA1::getDemandValueFromBlock1 (DOUBLE &aValue,
                                                 USHORT aRate,
                                                 RWTime &aPeak,
                                                 AlphaA1ScanData_t *aScanData)
{
    BOOL retCode = FALSE;
    aValue = 0.0;
    aPeak = rwEpoch;

    switch (aRate)
    {
        case ALPHA_RATE_A:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.ATD1.Month > 0 &&
                    aScanData->Real.class11.ATD1.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.ATD1.Day,
                                            aScanData->Real.class11.ATD1.Month,
                                            aScanData->Real.class11.ATD1.Year+2000),
                                    aScanData->Real.class11.ATD1.Hour,
                                    aScanData->Real.class11.ATD1.Minute,
                                    0);
                    aValue= aScanData->Real.class11.AKW1;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_B:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.BTD1.Month > 0 &&
                    aScanData->Real.class11.BTD1.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.BTD1.Day,
                                            aScanData->Real.class11.BTD1.Month,
                                            aScanData->Real.class11.BTD1.Year+2000),
                                    aScanData->Real.class11.BTD1.Hour,
                                    aScanData->Real.class11.BTD1.Minute,
                                    0);
                    aValue= aScanData->Real.class11.BKW1;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_C:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.CTD1.Month > 0 &&
                    aScanData->Real.class11.CTD1.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.CTD1.Day,
                                            aScanData->Real.class11.CTD1.Month,
                                            aScanData->Real.class11.CTD1.Year+2000),
                                    aScanData->Real.class11.CTD1.Hour,
                                    aScanData->Real.class11.CTD1.Minute,
                                    0);
                    aValue= aScanData->Real.class11.CKW1;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_D:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.DTD1.Month > 0 &&
                    aScanData->Real.class11.DTD1.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.DTD1.Day,
                                            aScanData->Real.class11.DTD1.Month,
                                            aScanData->Real.class11.DTD1.Year+2000),
                                    aScanData->Real.class11.DTD1.Hour,
                                    aScanData->Real.class11.DTD1.Minute,
                                    0);
                    aValue= aScanData->Real.class11.DKW1;
                    retCode = TRUE;
                }
                break;
            }
        default:
            break;
    }

    return retCode;
}

BOOL CtiDeviceAlphaA1::getDemandValueFromBlock2 (DOUBLE &aValue,
                                                 USHORT aRate,
                                                 RWTime &aPeak,
                                                 AlphaA1ScanData_t *aScanData)
{
    BOOL retCode = FALSE;
    aValue = 0.0;
    aPeak = rwEpoch;

    switch (aRate)
    {
        case ALPHA_RATE_A:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.ATD2.Month > 0 &&
                    aScanData->Real.class11.ATD2.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.ATD2.Day,
                                            aScanData->Real.class11.ATD2.Month,
                                            aScanData->Real.class11.ATD2.Year+2000),
                                    aScanData->Real.class11.ATD2.Hour,
                                    aScanData->Real.class11.ATD2.Minute,
                                    0);
                    aValue= aScanData->Real.class11.AKW2;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_B:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.BTD2.Month > 0 &&
                    aScanData->Real.class11.BTD2.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.BTD2.Day,
                                            aScanData->Real.class11.BTD2.Month,
                                            aScanData->Real.class11.BTD2.Year+2000),
                                    aScanData->Real.class11.BTD2.Hour,
                                    aScanData->Real.class11.BTD2.Minute,
                                    0);
                    aValue= aScanData->Real.class11.BKW2;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_C:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.CTD2.Month > 0 &&
                    aScanData->Real.class11.CTD2.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.CTD2.Day,
                                            aScanData->Real.class11.CTD2.Month,
                                            aScanData->Real.class11.CTD2.Year+2000),
                                    aScanData->Real.class11.CTD2.Hour,
                                    aScanData->Real.class11.CTD2.Minute,
                                    0);
                    aValue= aScanData->Real.class11.CKW2;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_D:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.DTD2.Month > 0 &&
                    aScanData->Real.class11.DTD2.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.DTD2.Day,
                                            aScanData->Real.class11.DTD2.Month,
                                            aScanData->Real.class11.DTD2.Year+2000),
                                    aScanData->Real.class11.DTD2.Hour,
                                    aScanData->Real.class11.DTD2.Minute,
                                    0);
                    aValue= aScanData->Real.class11.DKW2;
                    retCode = TRUE;
                }
                break;
            }
        default:
            break;
    }

    return retCode;
}


BOOL CtiDeviceAlphaA1::getDemandValueFromBlock3 (DOUBLE &aValue,
                                                 USHORT aRate,
                                                 RWTime &aPeak,
                                                 AlphaA1ScanData_t *aScanData)
{
    BOOL retCode = FALSE;
    aValue = 0.0;
    aPeak = rwEpoch;

    switch (aRate)
    {
        case ALPHA_RATE_A:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.ATD3.Month > 0 &&
                    aScanData->Real.class11.ATD3.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.ATD3.Day,
                                            aScanData->Real.class11.ATD3.Month,
                                            aScanData->Real.class11.ATD3.Year+2000),
                                    aScanData->Real.class11.ATD3.Hour,
                                    aScanData->Real.class11.ATD3.Minute,
                                    0);
                    aValue= aScanData->Real.class11.AKW3;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_B:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.BTD3.Month > 0 &&
                    aScanData->Real.class11.BTD3.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.BTD3.Day,
                                            aScanData->Real.class11.BTD3.Month,
                                            aScanData->Real.class11.BTD3.Year+2000),
                                    aScanData->Real.class11.BTD3.Hour,
                                    aScanData->Real.class11.BTD3.Minute,
                                    0);
                    aValue= aScanData->Real.class11.BKW3;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_C:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.CTD3.Month > 0 &&
                    aScanData->Real.class11.CTD3.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.CTD3.Day,
                                            aScanData->Real.class11.CTD3.Month,
                                            aScanData->Real.class11.CTD3.Year+2000),
                                    aScanData->Real.class11.CTD3.Hour,
                                    aScanData->Real.class11.CTD3.Minute,
                                    0);
                    aValue= aScanData->Real.class11.CKW3;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_D:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.DTD3.Month > 0 &&
                    aScanData->Real.class11.DTD3.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.DTD3.Day,
                                            aScanData->Real.class11.DTD3.Month,
                                            aScanData->Real.class11.DTD3.Year+2000),
                                    aScanData->Real.class11.DTD3.Hour,
                                    aScanData->Real.class11.DTD3.Minute,
                                    0);
                    aValue= aScanData->Real.class11.DKW3;
                    retCode = TRUE;
                }
                break;
            }
        default:
            break;
    }

    return retCode;
}

BOOL CtiDeviceAlphaA1::getDemandValueFromBlock4 (DOUBLE &aValue,
                                                 USHORT aRate,
                                                 RWTime &aPeak,
                                                 AlphaA1ScanData_t *aScanData)
{
    BOOL retCode = FALSE;
    aValue = 0.0;
    aPeak = rwEpoch;

    switch (aRate)
    {
        case ALPHA_RATE_A:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.ATD4.Month > 0 &&
                    aScanData->Real.class11.ATD4.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.ATD4.Day,
                                            aScanData->Real.class11.ATD4.Month,
                                            aScanData->Real.class11.ATD4.Year+2000),
                                    aScanData->Real.class11.ATD4.Hour,
                                    aScanData->Real.class11.ATD4.Minute,
                                    0);
                    aValue= aScanData->Real.class11.AKW4;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_B:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.BTD4.Month > 0 &&
                    aScanData->Real.class11.BTD4.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.BTD4.Day,
                                            aScanData->Real.class11.BTD4.Month,
                                            aScanData->Real.class11.BTD4.Year+2000),
                                    aScanData->Real.class11.BTD4.Hour,
                                    aScanData->Real.class11.BTD4.Minute,
                                    0);
                    aValue= aScanData->Real.class11.BKW4;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_C:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.CTD4.Month > 0 &&
                    aScanData->Real.class11.CTD4.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.CTD4.Day,
                                            aScanData->Real.class11.CTD4.Month,
                                            aScanData->Real.class11.CTD4.Year+2000),
                                    aScanData->Real.class11.CTD4.Hour,
                                    aScanData->Real.class11.CTD4.Minute,
                                    0);
                    aValue= aScanData->Real.class11.CKW4;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_D:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.DTD4.Month > 0 &&
                    aScanData->Real.class11.DTD4.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.DTD4.Day,
                                            aScanData->Real.class11.DTD4.Month,
                                            aScanData->Real.class11.DTD4.Year+2000),
                                    aScanData->Real.class11.DTD4.Hour,
                                    aScanData->Real.class11.DTD4.Minute,
                                    0);
                    aValue= aScanData->Real.class11.DKW4;
                    retCode = TRUE;
                }
                break;
            }
        default:
            break;
    }

    return retCode;
}

BOOL CtiDeviceAlphaA1::getDemandValueFromBlock5 (DOUBLE &aValue,
                                                 USHORT aRate,
                                                 RWTime &aPeak,
                                                 AlphaA1ScanData_t *aScanData)
{
    BOOL retCode = FALSE;
    aValue = 0.0;
    aPeak = rwEpoch;

    switch (aRate)
    {
        case ALPHA_RATE_A:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.ATD5.Month > 0 &&
                    aScanData->Real.class11.ATD5.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.ATD5.Day,
                                            aScanData->Real.class11.ATD5.Month,
                                            aScanData->Real.class11.ATD5.Year+2000),
                                    aScanData->Real.class11.ATD5.Hour,
                                    aScanData->Real.class11.ATD5.Minute,
                                    0);
                    aValue= aScanData->Real.class11.AKW5;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_B:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.BTD5.Month > 0 &&
                    aScanData->Real.class11.BTD5.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.BTD5.Day,
                                            aScanData->Real.class11.BTD5.Month,
                                            aScanData->Real.class11.BTD5.Year+2000),
                                    aScanData->Real.class11.BTD5.Hour,
                                    aScanData->Real.class11.BTD5.Minute,
                                    0);
                    aValue= aScanData->Real.class11.BKW5;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_C:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.CTD5.Month > 0 &&
                    aScanData->Real.class11.CTD5.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.CTD5.Day,
                                            aScanData->Real.class11.CTD5.Month,
                                            aScanData->Real.class11.CTD5.Year+2000),
                                    aScanData->Real.class11.CTD5.Hour,
                                    aScanData->Real.class11.CTD5.Minute,
                                    0);
                    aValue= aScanData->Real.class11.CKW5;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_D:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.DTD5.Month > 0 &&
                    aScanData->Real.class11.DTD5.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.DTD5.Day,
                                            aScanData->Real.class11.DTD5.Month,
                                            aScanData->Real.class11.DTD5.Year+2000),
                                    aScanData->Real.class11.DTD5.Hour,
                                    aScanData->Real.class11.DTD5.Minute,
                                    0);
                    aValue= aScanData->Real.class11.DKW5;
                    retCode = TRUE;
                }
                break;
            }
        default:
            break;
    }

    return retCode;
}

BOOL CtiDeviceAlphaA1::getDemandValueFromBlock6 (DOUBLE &aValue,
                                                 USHORT aRate,
                                                 RWTime &aPeak,
                                                 AlphaA1ScanData_t *aScanData)
{
    BOOL retCode = FALSE;
    aValue = 0.0;
    aPeak = rwEpoch;

    switch (aRate)
    {
        case ALPHA_RATE_A:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.ATD6.Month > 0 &&
                    aScanData->Real.class11.ATD6.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.ATD6.Day,
                                            aScanData->Real.class11.ATD6.Month,
                                            aScanData->Real.class11.ATD6.Year+2000),
                                    aScanData->Real.class11.ATD6.Hour,
                                    aScanData->Real.class11.ATD6.Minute,
                                    0);
                    aValue= aScanData->Real.class11.AKW6;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_B:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.BTD6.Month > 0 &&
                    aScanData->Real.class11.BTD6.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.BTD6.Day,
                                            aScanData->Real.class11.BTD6.Month,
                                            aScanData->Real.class11.BTD6.Year+2000),
                                    aScanData->Real.class11.BTD6.Hour,
                                    aScanData->Real.class11.BTD6.Minute,
                                    0);
                    aValue= aScanData->Real.class11.BKW6;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_C:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.CTD6.Month > 0 &&
                    aScanData->Real.class11.CTD6.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.CTD6.Day,
                                            aScanData->Real.class11.CTD6.Month,
                                            aScanData->Real.class11.CTD6.Year+2000),
                                    aScanData->Real.class11.CTD6.Hour,
                                    aScanData->Real.class11.CTD6.Minute,
                                    0);
                    aValue= aScanData->Real.class11.CKW6;
                    retCode = TRUE;
                }
                break;
            }
        case ALPHA_RATE_D:
            {
                // make sure we have valid months
                if (aScanData->Real.class11.DTD6.Month > 0 &&
                    aScanData->Real.class11.DTD6.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.class11.DTD6.Day,
                                            aScanData->Real.class11.DTD6.Month,
                                            aScanData->Real.class11.DTD6.Year+2000),
                                    aScanData->Real.class11.DTD6.Hour,
                                    aScanData->Real.class11.DTD6.Minute,
                                    0);
                    aValue= aScanData->Real.class11.DKW6;
                    retCode = TRUE;
                }
                break;
            }
        default:
            break;
    }

    return retCode;
}


BOOL CtiDeviceAlphaA1::getEnergyValueFromBlock1 (DOUBLE &aValue,
                                                 USHORT aRate,
                                                 RWTime &aPeak,
                                                 AlphaA1ScanData_t *aScanData)
{
    BOOL retCode = FALSE;
    aValue = 0.0;
    aPeak = rwEpoch;

    switch (aRate)
    {
        case ALPHA_RATE_A:
            {
                aValue= aScanData->Real.class11.AKWH1;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_B:
            {
                aValue= aScanData->Real.class11.BKWH1;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_C:
            {
                aValue= aScanData->Real.class11.CKWH1;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_D:
            {
                aValue= aScanData->Real.class11.DKWH1;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_TOTAL:
            {
                aValue= aScanData->Real.class11.KWH1;
                retCode = TRUE;
                break;
            }
        default:
            break;
    }

    return retCode;
}

BOOL CtiDeviceAlphaA1::getEnergyValueFromBlock2 (DOUBLE &aValue,
                                                 USHORT aRate,
                                                 RWTime &aPeak,
                                                 AlphaA1ScanData_t *aScanData)
{
    BOOL retCode = FALSE;
    aValue = 0.0;
    aPeak = rwEpoch;

    switch (aRate)
    {
        case ALPHA_RATE_A:
            {
                aValue= aScanData->Real.class11.AKWH2;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_B:
            {
                aValue= aScanData->Real.class11.BKWH2;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_C:
            {
                aValue= aScanData->Real.class11.CKWH2;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_D:
            {
                aValue= aScanData->Real.class11.DKWH2;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_TOTAL:
            {
                aValue= aScanData->Real.class11.KWH2;
                retCode = TRUE;
                break;
            }
        default:
            break;
    }

    return retCode;
}

BOOL CtiDeviceAlphaA1::getEnergyValueFromBlock3 (DOUBLE &aValue,
                                                 USHORT aRate,
                                                 RWTime &aPeak,
                                                 AlphaA1ScanData_t *aScanData)
{
    BOOL retCode = FALSE;
    aValue = 0.0;
    aPeak = rwEpoch;

    switch (aRate)
    {
        case ALPHA_RATE_A:
            {
                aValue= aScanData->Real.class11.AKWH3;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_B:
            {
                aValue= aScanData->Real.class11.BKWH3;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_C:
            {
                aValue= aScanData->Real.class11.CKWH3;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_D:
            {
                aValue= aScanData->Real.class11.DKWH3;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_TOTAL:
            {
                aValue= aScanData->Real.class11.KWH3;
                retCode = TRUE;
                break;
            }
        default:
            break;
    }

    return retCode;
}

BOOL CtiDeviceAlphaA1::getEnergyValueFromBlock4 (DOUBLE &aValue,
                                                 USHORT aRate,
                                                 RWTime &aPeak,
                                                 AlphaA1ScanData_t *aScanData)
{
    BOOL retCode = FALSE;
    aValue = 0.0;
    aPeak = rwEpoch;

    switch (aRate)
    {
        case ALPHA_RATE_A:
            {
                aValue= aScanData->Real.class11.AKWH4;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_B:
            {
                aValue= aScanData->Real.class11.BKWH4;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_C:
            {
                aValue= aScanData->Real.class11.CKWH4;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_D:
            {
                aValue= aScanData->Real.class11.DKWH4;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_TOTAL:
            {
                aValue= aScanData->Real.class11.KWH4;
                retCode = TRUE;
                break;
            }
        default:
            break;
    }

    return retCode;
}

BOOL CtiDeviceAlphaA1::getEnergyValueFromBlock5 (DOUBLE &aValue,
                                                 USHORT aRate,
                                                 RWTime &aPeak,
                                                 AlphaA1ScanData_t *aScanData)
{
    BOOL retCode = FALSE;
    aValue = 0.0;
    aPeak = rwEpoch;

    switch (aRate)
    {
        case ALPHA_RATE_A:
            {
                aValue= aScanData->Real.class11.AKWH5;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_B:
            {
                aValue= aScanData->Real.class11.BKWH5;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_C:
            {
                aValue= aScanData->Real.class11.CKWH5;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_D:
            {
                aValue= aScanData->Real.class11.DKWH5;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_TOTAL:
            {
                aValue= aScanData->Real.class11.KWH5;
                retCode = TRUE;
                break;
            }
        default:
            break;
    }

    return retCode;
}


BOOL CtiDeviceAlphaA1::getEnergyValueFromBlock6 (DOUBLE &aValue,
                                                 USHORT aRate,
                                                 RWTime &aPeak,
                                                 AlphaA1ScanData_t *aScanData)
{
    BOOL retCode = FALSE;
    aValue = 0.0;
    aPeak = rwEpoch;

    switch (aRate)
    {
        case ALPHA_RATE_A:
            {
                aValue= aScanData->Real.class11.AKWH6;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_B:
            {
                aValue= aScanData->Real.class11.BKWH6;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_C:
            {
                aValue= aScanData->Real.class11.CKWH6;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_D:
            {
                aValue= aScanData->Real.class11.DKWH6;
                retCode = TRUE;
                break;
            }
        case ALPHA_RATE_TOTAL:
            {
                aValue= aScanData->Real.class11.KWH6;
                retCode = TRUE;
                break;
            }
        default:
            break;
    }

    return retCode;
}

