#include "yukon.h"

/*-----------------------------------------------------------------------------*
*
* File:   dev_alpha
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_aplus.cpp-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2005/02/10 23:23:59 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*    History:
      $Log: dev_aplus.cpp,v $
      Revision 1.9  2005/02/10 23:23:59  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.8  2004/07/27 16:53:54  mfisher
      RWTime.seconds workaround for boost ptime::seconds

      Revision 1.7  2003/07/14 20:17:11  dsutton
      Added processing for usage hours and last interval demand for
      vars atttributed to the four quadrants.  New point offsets were needed
      and the alpha power plus is the only electronic meter to support these
      offsets as of 7/14

      Revision 1.5.2.1  2003/04/10 21:45:21  dsutton
      Added code to check the CRC on the multiple message classes (ones over 64 bytes)
      and added checks to make sure we had received the entire message


*-----------------------------------------------------------------------------*/

#include "porter.h"
#include "dev_alpha.h"
#include "dev_aplus.h"

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


CTI_alpha_func   APlusFunctions[] = {
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


CTI_alpha_class   APlusClasses[] = {
    {  0, 40    ,  "Primary Metering Constants"},
    {  1, 5     ,  "Password Table"},
    {  2, 64    ,  "Identification & Demand Constants"},
    {  3, 196   ,  "Main display table"},
    {  4, 176   ,  "TOU rate schedule"},
    {  5,   0   ,  "Filler"},
    {  6, 288   ,  "Metering Configuration"},
    {  7, 304   ,  "Secondary Metering Constants"},
    {  8, 64    ,  "Firmware Configuration"},
    {  9, 48    ,  "Status Area #1"},
    { 10, 24    ,  "Status Area #2"},
    { 11, 212   ,  "Current billing data"},
    { 12, 212   ,  "Previous period billing data"},
    { 13, 212   ,  "Previous season billing data"},
    { 14, 42    ,  "Load profile configuration"},
    { 15, 15    ,  "Event log configuration"},
    { 16, 999   ,  "Event log data"},
    { 17, 999   ,  "Load profile data"},
    { 18, 999   ,  "Load profile partial data"},
    { 19, 5     ,  "Future password table"},
    { 20, 64    ,  "Future identification and demand table"},
    { 21, 196   ,  "Future display table 1"},
    { 22, 176   ,  "Future rate schedule"},
    { 30, -1    ,  "Partial interval pulse counts"},
    { 31, 46    ,  "Modem billing call configuration table"},
    { 32, 46    ,  "Modem alarm call configuration information"},
    { 33, 64    ,  "Modem configuration information"},
    { 34, 24    ,  "Modem communication's status"},
    { 72, 999   ,  "Rules Class"},
    { 82, 999   ,  "Real-Time Demand Class"}

};

// DLLIMPORT extern CtiConnection VanGoghConnection;
DOUBLE PPlusBCDtoDouble(UCHAR* buffer, USHORT len);

INT CtiDeviceAlphaPPlus::allocateDataBins  (OUTMESS *outMess)
{
    // Load command parameters from the out message
    setCurrentCommand ((CtiDeviceIED::CtiMeterCmdStates_t)outMess->Buffer.DUPReq.Command[0]);
    setReadClass (outMess->Buffer.DUPReq.Command[2]);
    setReadLength (outMess->Buffer.DUPReq.Command[3]);
    setReadFunction (outMess->Buffer.DUPReq.Command[2]);

    // allocate this to as big as we can possible get
    if (_dataBuffer == NULL)
    {
        _dataBuffer = CTIDBG_new BYTE[sizeof (AlphaPPlusScanData_t)];

        if (_dataBuffer != NULL)
        {
            ((AlphaPPlusScanData_t *)_dataBuffer)->Real.class0.valid = FALSE;
            ((AlphaPPlusScanData_t *)_dataBuffer)->Real.class2.valid = FALSE;
            ((AlphaPPlusScanData_t *)_dataBuffer)->Real.class6.valid = FALSE;
            ((AlphaPPlusScanData_t *)_dataBuffer)->Real.class82.valid = FALSE;
            ((AlphaPPlusScanData_t *)_dataBuffer)->Real.class11.valid = FALSE;
        }
    }

    if (_loadProfileBuffer == NULL)
    {
        _loadProfileBuffer = CTIDBG_new BYTE[sizeof (AlphaPPlusLoadProfile_t)];

        if (_loadProfileBuffer != NULL)
        {
            ((AlphaPPlusLoadProfile_t *)_loadProfileBuffer)->porterLPTime = outMess->Buffer.DUPReq.LP_Time;
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


INT CtiDeviceAlphaPPlus::GeneralScan(CtiRequestMsg *pReq,
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
        OutMessage->Buffer.DUPReq.Identity = IDENT_ALPHA_PPLUS;
        status = Inherited::GeneralScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority);
        return status;
    }
    else
    {
        return MEMORY;
    }
}


USHORT CtiDeviceAlphaPPlus::calculateStartingByteCountForCurrentScanState (int aClass)
{
    USHORT byteCount = 0;

    switch (getReadClass())
    {
        case 0:
            byteCount =0;
            break;
        case 6:
            byteCount = sizeof (AlphaPPlusClass0Raw_t);
            break;
        case 2:
            byteCount = sizeof (AlphaPPlusClass0Raw_t) + sizeof (AlphaPPlusClass6Raw_t);
            break;
        case 82:
            byteCount = sizeof (AlphaPPlusClass0Raw_t) + sizeof (AlphaPPlusClass6Raw_t) +
                        sizeof (AlphaPPlusClass2Raw_t);
            break;
        case 11:
            byteCount = sizeof (AlphaPPlusClass0Raw_t) + sizeof (AlphaPPlusClass6Raw_t) +
                        sizeof (AlphaPPlusClass2Raw_t) + sizeof (AlphaPPlusClass82Raw_t);
            break;
    }

    return byteCount;
}


INT CtiDeviceAlphaPPlus::generateCommandScan( CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList )
{
    AlphaPPlusLoadProfile_t *localLP      = ((AlphaPPlusLoadProfile_t*)_loadProfileBuffer);
    AlphaPPlusScanData_t    *localData    = ((AlphaPPlusScanData_t *)_dataBuffer);
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
                Transfer.setCRCFlag (0);

                // reset total byte count correctly in case we are doubling back here
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
                            if (getTotalByteCount() == sizeof (AlphaPPlusClass0Raw_t))
                            {
                                localData->Real.class0.valid = TRUE;
                                setReadClass(6);
                                setReadFunction(6);
                                setSingleMsgByteCount(0);
                                setAttemptsRemaining(3);

                                setClassReadComplete (FALSE);
                                setPreviousState (StateScanValueSet7);
                                Transfer.setOutCount (0);
                                Transfer.setInCountExpected(0);
                                Transfer.setInTimeout (1);
                                setCurrentState (StateScanDecode7);
                            }
                            else
                            {
                                setPreviousState (StateScanAbort);
                                generateCommandTerminate (Transfer, traceList);
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " Class 0 failed for " << getName() << " aborting scan " << endl;
                                }
                            }
                            break;
                        }

                    case 6:
                        {
                            // done with class 2, move to 82
                            if (getTotalByteCount() == (sizeof (AlphaPPlusClass0Raw_t) +
                                                        sizeof (AlphaPPlusClass6Raw_t)))
                            {
                                localData->Real.class6.valid = TRUE;
                                // moving to load profile next, reset parameters
                                setReadClass(2);
                                setReadFunction(2);
                                setSingleMsgByteCount(0);
                                setAttemptsRemaining(3);

                                setClassReadComplete (FALSE);
                                setPreviousState (StateScanValueSet7);
                                Transfer.setOutCount (0);
                                Transfer.setInCountExpected(0);
                                Transfer.setInTimeout (1);
                                setCurrentState (StateScanDecode7);

                            }
                            else
                            {
                                setPreviousState (StateScanAbort);
                                generateCommandTerminate (Transfer, traceList);
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " Class 6 failed for " << getName() << " aborting scan " << endl;
                                }
                            }

                            break;
                        }

                    case 2:
                        {
                            // done with class 2, move to 82
                            if (getTotalByteCount() == (sizeof (AlphaPPlusClass0Raw_t) +
                                                        sizeof (AlphaPPlusClass6Raw_t) +
                                                        sizeof (AlphaPPlusClass2Raw_t)))
                            {
                                localData->Real.class2.valid = TRUE;
                                setReadClass(82);
                                setReadFunction(82);
                                setSingleMsgByteCount(0);
                                setAttemptsRemaining(3);

                                setClassReadComplete (FALSE);
                                setPreviousState (StateScanValueSet7);
                                Transfer.setOutCount (0);
                                Transfer.setInCountExpected(0);
                                Transfer.setInTimeout (1);
                                setCurrentState (StateScanDecode7);
                            }
                            else
                            {
                                setPreviousState (StateScanAbort);
                                generateCommandTerminate (Transfer, traceList);
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " Class 2 failed for " << getName() << " aborting scan " << endl;
                                }
                            }

                            break;
                        }

                    case 82:
                        {
                            // done with class 82, move to 11
                            if (getTotalByteCount() == (sizeof (AlphaPPlusClass0Raw_t) +
                                                        sizeof (AlphaPPlusClass2Raw_t) +
                                                        sizeof (AlphaPPlusClass6Raw_t) +
                                                        sizeof (AlphaPPlusClass82Raw_t)))
                            {
                                localData->Real.class82.valid = TRUE;
                                setReadClass(11);
                                setReadFunction(11);
                                setSingleMsgByteCount(0);
                                setAttemptsRemaining(3);

                                setClassReadComplete (FALSE);
                                setPreviousState (StateScanValueSet7);
                                Transfer.setOutCount (0);
                                Transfer.setInCountExpected(0);
                                Transfer.setInTimeout (1);
                                setCurrentState (StateScanDecode7);

                            }
                            else
                            {
                                setPreviousState (StateScanAbort);
                                generateCommandTerminate (Transfer, traceList);
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " Class 82 failed for " << getName() << " aborting scan " << endl;
                                }
                            }

                            break;
                        }
                    case 11:
                        {
                            if (getTotalByteCount() == (sizeof (AlphaPPlusClass0Raw_t) +
                                                        sizeof (AlphaPPlusClass2Raw_t) +
                                                        sizeof (AlphaPPlusClass82Raw_t) +
                                                        sizeof (AlphaPPlusClass6Raw_t) +
                                                        sizeof (AlphaPPlusClass11Raw_t)))
                            {
                                localData->Real.class11.valid = TRUE;
                                // set total bytes retrieved into buffer and reset count for load profile retrieval
                                ((AlphaPPlusScanData_t *)_dataBuffer)->totalByteCount = getTotalByteCount();
                                setTotalByteCount(0);

                                setCurrentCommand(CmdLoadProfileTransition);
                                setReadClass(0);
                                setReadFunction(0);
                                setSingleMsgByteCount(0);

                                if (_lpWorkBuffer != NULL)
                                {
                                    delete []_lpWorkBuffer;
                                    _lpWorkBuffer = NULL;
                                }

                                _lpWorkBuffer = CTIDBG_new BYTE[sizeof (AlphaPPlusClass0Raw_t)];

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
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " Class 11 failed for " << getName() << " aborting scan " << endl;
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

INT CtiDeviceAlphaPPlus::generateCommandLoadProfile( CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList )
{
    AlphaPPlusLoadProfile_t * ptr = (AlphaPPlusLoadProfile_t *)_loadProfileBuffer;
    int               retCode = NORMAL;
    BYTEUSHORT        reqLength;
    BYTEUSHORT        reqOffset;
    BYTE              classToRead;

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
                // bytes received set to zero outside of loop
                Transfer.getOutBuffer()[0]      = STX;
                Transfer.getOutBuffer()[1]      = ALPHA_CMD_CLASS_READ;
                Transfer.getOutBuffer()[2]      = PAD;

                reqLength.sh = getReadLength();
                reqOffset.sh = 0;

                if (getReadClass() == 18)
                {
                    reqLength.sh = ptr->daysRequested;
//                    reqLength.sh = 2;
                }

                // fill the transfer structure
                Transfer.getOutBuffer()[3]      = reqLength.ch[1];
                Transfer.getOutBuffer()[4]      = reqLength.ch[0];
                Transfer.getOutBuffer()[5]      = reqOffset.ch[1];
                Transfer.getOutBuffer()[6]      = reqOffset.ch[0];
                Transfer.getOutBuffer()[7]      = (BYTE) getReadClass();

                // we are restarting a scan
                setTotalByteCount (0);
                setSingleMsgByteCount(0);


                // note that we fall through again, no processing needed yet
            }

        case StateScanValueSet2FirstScan:
        case StateScanValueSet2:
            {
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
                Transfer.getOutBuffer(   )[   0]    =    STX;
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
                // which class is next
                switch (getReadClass())
                {
                    case 0:
                        {
                            AlphaPPlusClass0Raw_t  * wPtr = (AlphaPPlusClass0Raw_t *)_lpWorkBuffer;

                            ptr->class0.wattHoursPerRevolution = (ULONG) BCDtoBase10(wPtr->UKH,     3) / 1000.0;
                            ptr->class0.pulsesPerRevolution = (USHORT) BCDtoBase10(&wPtr->UPR,     1);

//                            {
//                                CtiLockGuard<CtiLogger> doubt_guard(dout);
//                                dout << "watt hours   : " <<  ptr->class0.wattHoursPerRevolution << endl;
//                                dout << "pulses per : " << ptr->class0.pulsesPerRevolution << endl;
//                            }

                            if (_lpWorkBuffer != NULL)
                            {
                                delete []_lpWorkBuffer;
                                _lpWorkBuffer = NULL;
                            }

                            _lpWorkBuffer = CTIDBG_new BYTE[sizeof (AlphaPPlusClass6Raw_t)];


                            // moving to load profile next, reset parameters
                            setReadClass(6);
                            setReadFunction(6);
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


                    case 6:
                        {
                            AlphaPPlusClass6Raw_t  * wPtr = (AlphaPPlusClass6Raw_t *)_lpWorkBuffer;
                            // done with class 2, move to 82

                            ptr->class6.primaryFunction = primaryFunction(wPtr->XUOM[0]);
                            ptr->class6.secondaryFunction = secondaryFunction(wPtr->XUOM[0]);

/*
                            {
                                char junk[10];
                                sprintf (junk,"0x%2x",wPtr->XUOM[0]);

                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "Raw data  : " << RWCString(junk) << endl;
                                dout << "Primary   : " << (USHORT)ptr->class6.primaryFunction << endl;
                                dout << "Secondary : " << (USHORT)ptr->class6.secondaryFunction << endl;
                            }
*/
                            if (_lpWorkBuffer != NULL)
                            {
                                delete []_lpWorkBuffer;
                                _lpWorkBuffer = NULL;
                            }

                            _lpWorkBuffer = CTIDBG_new BYTE[sizeof (AlphaPPlusClass2Raw_t)];

                            // moving to load profile next, reset parameters
                            setReadClass(2);
                            setReadFunction(2);
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
                    case 2:
                        {
                            AlphaPPlusClass2Raw_t  * wPtr = (AlphaPPlusClass2Raw_t *)_lpWorkBuffer;
                            // done with class 2, move to 82

                            // try finding a config with primary block first
                            ptr->class2.configTOUBlk1  = touBlockMapping (wPtr->EBLKCF1,ptr->class6.primaryFunction);
                            if (ptr->class2.configTOUBlk1==PPLUS_DISABLED)
                            {
                                ptr->class2.configTOUBlk1  = touBlockMapping (wPtr->EBLKCF1,ptr->class6.secondaryFunction);
                            }

                            // try finding a config with primary block first
                            ptr->class2.configTOUBlk2  = touBlockMapping (wPtr->EBLKCF2,ptr->class6.primaryFunction);
                            if (ptr->class2.configTOUBlk2==PPLUS_DISABLED)
                            {
                                ptr->class2.configTOUBlk2  = touBlockMapping (wPtr->EBLKCF2,ptr->class6.secondaryFunction);
                            }

/*
                            {
                              CtiLockGuard<CtiLogger> doubt_guard(dout);
                              dout << "Blk 1 Mapping= " << (USHORT)ptr->class2.configTOUBlk1 << endl;
                              dout << "Blk 2 Mapping= " << (USHORT)ptr->class2.configTOUBlk2 << endl;
                            }
*/
                            if (_lpWorkBuffer != NULL)
                            {
                                delete []_lpWorkBuffer;
                                _lpWorkBuffer = NULL;
                            }

                            _lpWorkBuffer = CTIDBG_new BYTE[sizeof (AlphaPPlusClass14Raw_t)];

                            // moving to load profile next, reset parameters
                            setReadClass(14);
                            setReadFunction(14);
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


                    case 14:
                        {
                            AlphaPPlusClass14Raw_t  * wPtr = (AlphaPPlusClass14Raw_t *)_lpWorkBuffer;

                            ptr->class14.scalingFactor    = (USHORT) wPtr->scalingFactor;
                            ptr->class14.intervalLength   = (USHORT) wPtr->intervalLength;
                            ptr->class14.numberOfChannels = (USHORT) wPtr->numberOfChannels;
                            ptr->class14.lpMemory         = (USHORT) wPtr->lpMemory;
                            ptr->class14.channelInput[0]  = (USHORT) wPtr->channel1Input;
                            ptr->class14.channelInput[1]  = (USHORT) wPtr->channel2Input;
                            ptr->class14.channelInput[2]  = (USHORT) wPtr->channel3Input;
                            ptr->class14.channelInput[3]  = (USHORT) wPtr->channel4Input;

                            BYTEUSHORT  flip;
                            flip.ch[0] = wPtr->dayRecordSize[1];
                            flip.ch[1] = wPtr->dayRecordSize[0];
                            ptr->class14.dayRecordSize = flip.sh;

                            ptr->daysRequested = ((RWTime::now().seconds() - ptr->porterLPTime) / 86400) + 2;
                            ptr->dayRecordSize = ptr->class14.dayRecordSize;

                            if( DebugLevel & 0x0001 )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << endl << RWTime() << " -------- Alpha Load Profile Inputs ---------------" << endl;
                                dout << "Number of Channels " << ptr->class14.numberOfChannels << " Interval " << ptr->class14.intervalLength << endl;
                                dout << "\tChannel 1: " << ptr->class14.channelInput[0] << endl;
                                dout << "\tChannel 2: " << ptr->class14.channelInput[1] << endl;
                                dout << "\tChannel 3: " << ptr->class14.channelInput[2] << endl;
                                dout << "\tChannel 4: " << ptr->class14.channelInput[3] << endl;

                                dout << endl << "Scaling factor " << ptr->class14.scalingFactor << endl;
                                dout << "Days requested " << ptr->daysRequested << endl;
                            }

                            if (shouldRetrieveLoadProfile (ptr->porterLPTime, ptr->class14.intervalLength))
                            {
                                // we're done with this as is, reallocate
                                if (_lpWorkBuffer != NULL)
                                {
                                    delete []_lpWorkBuffer;
                                    _lpWorkBuffer = NULL;

                                }

                                /**********************
                                *   Workbuffer needs to be the size of day record + 100 bytes.  Since a day
                                *   record could end anywhere inside of the 64 bytes returned by the alpha
                                *   we need to have extra bytes in the workbuffer to hold the beginning
                                *   of the next day record
                                ***********************
                                */
                                _lpWorkBuffer = CTIDBG_new BYTE[ptr->class14.dayRecordSize + 100];

                                setReadClass(18);
                                setReadFunction(18);
                                setTotalByteCount (0);
                                setSingleMsgByteCount(0);
                                setAttemptsRemaining(3);

                                setClassReadComplete (FALSE);
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
                                setPreviousState (StateScanComplete);
                                generateCommandTerminate (Transfer, traceList);
                            }
                            break;
                        }

                    case 18:
                        {
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


INT CtiDeviceAlphaPPlus::decodeResponseScan (CtiXfer  &Transfer,
                                             INT      commReturnValue,
                                             RWTPtrSlist< CtiMessage > &traceList)
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
                    classOffset = getAPlusClassOffset(KEY_ALPHA_CLASS, (void *)&iClass);
                    classLength = APlusClasses[classOffset].Length;

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


INT CtiDeviceAlphaPPlus::decodeResponseLoadProfile (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    INT retCode= NORMAL;
    INT         iClass;
    // Class Offset is the position of the requested class in the APlusClasses array
    INT         classOffset;
    // ClassLength is the byte length count of the requested class from the APlusClasses array
    INT         classLength;
    AlphaPPlusLoadProfile_t * ptr = (AlphaPPlusLoadProfile_t *)_loadProfileBuffer;


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
                    classOffset = getAPlusClassOffset(KEY_ALPHA_CLASS, (void *)&iClass);
                    classLength = APlusClasses[classOffset].Length;

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

                        // need to check if its time to send back load profile
                        if (getTotalByteCount() >= ptr->class14.dayRecordSize)
                        {
                            BYTE buffer[100];
                            ptr->class18.recordDateTime.Year = (UCHAR) BCDtoBase10(&_lpWorkBuffer[0],   1);
                            ptr->class18.recordDateTime.Month = (UCHAR) BCDtoBase10(&_lpWorkBuffer[1],   1);
                            ptr->class18.recordDateTime.Day = (UCHAR) BCDtoBase10(&_lpWorkBuffer[2],   1);
                            memcpy (&ptr->class18.data[0], &_lpWorkBuffer[6], ptr->class14.dayRecordSize-6);

                            // now, readjust my workbuffer
                            memcpy (&buffer[0], &_lpWorkBuffer[ptr->class14.dayRecordSize], getTotalByteCount()-ptr->class14.dayRecordSize);
                            memcpy (&_lpWorkBuffer[0], &buffer[0], getTotalByteCount()-ptr->class14.dayRecordSize);
                            setTotalByteCount(getTotalByteCount()-ptr->class14.dayRecordSize);

                            /*************************
                            * Check the validity of the time received
                            **************************
                            */
                            RWTime dateOfRecord(RWDate(ptr->class18.recordDateTime.Day,
                                                       ptr->class18.recordDateTime.Month,
                                                       ptr->class18.recordDateTime.Year + 2000));

                            if (dateOfRecord < (RWTime()-(2*86400)) ||
                                (dateOfRecord > (RWTime()+(2*86400))))
                            {
                                /***********************
                                * if meter time is not within a 2 day window, its
                                * invalid
                                ************************
                                */
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " Aborting scan: Invalid day record timestamp " << getName()  << " " << dateOfRecord.asString()<< endl;
                                }
                                setPreviousState (StateScanAbort);
                                setCurrentState (StateScanSendTerminate);
                            }
                            else
                            {
                                ptr->lastLPMessage = TRUE;
                                setPreviousState (StateScanValueSet7FirstScan);
                                setCurrentState (StateScanReturnLoadProfile);
                            }
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
//                if (commReturnValue || decodeAckNak(Transfer.getInBuffer()[2]))
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
                                // need to check if its time to send back load profile
                                if (getTotalByteCount() >= ptr->class14.dayRecordSize)
                                {
                                    BYTE buffer[100];

                                    ptr->class18.recordDateTime.Year = (UCHAR) BCDtoBase10(&_lpWorkBuffer[0],   1);
                                    ptr->class18.recordDateTime.Month = (UCHAR) BCDtoBase10(&_lpWorkBuffer[1],   1);
                                    ptr->class18.recordDateTime.Day = (UCHAR) BCDtoBase10(&_lpWorkBuffer[2],   1);
                                    memcpy (&ptr->class18.data[0], &_lpWorkBuffer[6], ptr->class14.dayRecordSize-6);

                                    // now, readjust my workbuffer
                                    memcpy (&buffer[0], &_lpWorkBuffer[ptr->class14.dayRecordSize], getTotalByteCount()-ptr->class14.dayRecordSize);
                                    memcpy (&_lpWorkBuffer[0], &buffer[0], getTotalByteCount()-ptr->class14.dayRecordSize);
                                    setTotalByteCount(getTotalByteCount()-ptr->class14.dayRecordSize);

                                    ptr->lastLPMessage = TRUE;
                                    setPreviousState (StateScanValueSet7FirstScan);
                                    setCurrentState (StateScanReturnLoadProfile);
                                }
                                else
                                {
                                    setCurrentState (StateScanValueSet7FirstScan);
                                }
                            }
                            else
                            {
                                // need to check if its time to send back load profile
                                if (getTotalByteCount() >= ptr->class14.dayRecordSize)
                                {
                                    BYTE buffer[100];

                                    ptr->class18.recordDateTime.Year = (UCHAR) BCDtoBase10(&_lpWorkBuffer[0],   1);
                                    ptr->class18.recordDateTime.Month = (UCHAR) BCDtoBase10(&_lpWorkBuffer[1],   1);
                                    ptr->class18.recordDateTime.Day = (UCHAR) BCDtoBase10(&_lpWorkBuffer[2],   1);
                                    memcpy (&ptr->class18.data[0], &_lpWorkBuffer[6], ptr->class14.dayRecordSize-6);

                                    // now, readjust my workbuffer
                                    memcpy (&buffer[0], &_lpWorkBuffer[ptr->class14.dayRecordSize], getTotalByteCount()-ptr->class14.dayRecordSize);
                                    memcpy (&_lpWorkBuffer[0], &buffer[0], getTotalByteCount()-ptr->class14.dayRecordSize);
                                    setTotalByteCount(getTotalByteCount()-ptr->class14.dayRecordSize);

                                    ptr->lastLPMessage = FALSE;
                                    setPreviousState (StateScanValueSet6FirstScan);
                                    setCurrentState (StateScanReturnLoadProfile);
                                    setAttemptsRemaining(3);
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

INT CtiDeviceAlphaPPlus::decodeResultScan   (INMESS *InMessage,
                                             RWTime &TimeNow,
                                             RWTPtrSlist< CtiMessage >   &vgList,
                                             RWTPtrSlist< CtiMessage > &retList,
                                             RWTPtrSlist< OUTMESS > &outList)
{
    char tmpCurrentState = InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1];
    CHAR     temp[100], buffer[100];

    BOOL     bDoStatusCheck = FALSE;
    BOOL     bDoResetStatus = FALSE;
    BOOL     isValidPoint = TRUE;;
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
    AlphaPPlusScanData_t  *ptr = (AlphaPPlusScanData_t *)DUPRep->Message;
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

                        verifyAndAddPointToReturnMsg (pNumericPoint->getPointID(),
                                                      PValue * pNumericPoint->getMultiplier(),
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

INT CtiDeviceAlphaPPlus::decodeResultLoadProfile (INMESS *InMessage,
                                                  RWTime &TimeNow,
                                                  RWTPtrSlist< CtiMessage >   &vgList,
                                                  RWTPtrSlist< CtiMessage > &retList,
                                                  RWTPtrSlist< OUTMESS > &outList)
{

    DIALUPREPLY        *DUPRep       = &InMessage->Buffer.DUPSt.DUPRep;
    AlphaPPlusLoadProfile_t  *ptr = (AlphaPPlusLoadProfile_t *)DUPRep->Message;

    int     intervalsPerDay = (60 / ptr->class14.intervalLength * 24);
    USHORT  intervalPulses;
    DOUBLE  intervalData;
    USHORT  dataOffset;
    BOOL    isIntervalValid=FALSE;
    BOOL  isNextInterval = FALSE;

    BOOL    eventFlag = FALSE;

    BYTEUSHORT  flip;


    // will only work for the next 100 years, sorry DLS
    ULONG   dayRecordTime = RWTime(RWDate(ptr->class18.recordDateTime.Day,
                                          ptr->class18.recordDateTime.Month,
                                          ptr->class18.recordDateTime.Year + 2000)).seconds();


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
    CtiReturnMsg   *pLastLPIntervals = CTIDBG_new CtiReturnMsg(getID(),
                                                        RWCString(InMessage->Return.CommandStr),
                                                        RWCString(),
                                                        InMessage->EventCode & 0x7fff,
                                                        InMessage->Return.RouteID,
                                                        InMessage->Return.MacroOffset,
                                                        InMessage->Return.Attempt,
                                                        InMessage->Return.TrxID,
                                                        InMessage->Return.UserID);
    // alpha only supports 4 channels
    AlphaLPPointInfo_t   validLPPointInfo[4] = { {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1}};

    // initialize as we go

    /*********************
    * if the current day record and one day is greater than our lp time,
    * this day record must be midnight on the day we want
    **********************
    */
    if ( dayRecordTime + 86400  >  getLastLPTime())
    {
        // now, loop through the channels and get the point offsets and multipliers
        for (int pointCnt=0; pointCnt < ptr->class14.numberOfChannels; pointCnt++)
        {
            findLPDataPoint (validLPPointInfo[pointCnt], ptr->class14.channelInput[pointCnt], ptr->class2);
        }

        // now walk the data
        for (int currentInterval = 0; currentInterval < intervalsPerDay; currentInterval++)
        {
            // check the interval
            if (getLastLPTime() < (dayRecordTime + ((currentInterval+1) * ptr->class14.intervalLength * 60)))
            {
                for (int currentChannel = 0;  currentChannel < ptr->class14.numberOfChannels; currentChannel++)
                {

                    /***********************
                    * offset is where in data buffer we current are
                    *
                    * current interval * # channels * 2 bytes per reading + current Channel * 2 bytes per reading
                    ***********************
                    */
                    dataOffset = (currentInterval * ptr->class14.numberOfChannels * 2) +
                                 (currentChannel * 2);

                    flip.ch[0] = ptr->class18.data[dataOffset+1];
                    flip.ch[1] = ptr->class18.data[dataOffset];

                    intervalPulses = flip.sh;

                    if (intervalPulses & 0x8000)
                    {
                        // and event occurred during this reading
                        eventFlag = TRUE;
                    }

                    intervalPulses &= 0x7FFF;

                    switch (intervalPulses)
                    {
                        case 0x7FFF:
                            {
                                // value for this time has not yet been recorded
                                break;
                            }
                        case 0x7FFE:
                            {
                                // pulse count overflow
                                break;
                            }
                        default:
                            {
                                intervalData = ((DOUBLE)intervalPulses * ptr->class14.scalingFactor *
                                                ptr->class0.wattHoursPerRevolution * (1.0/ (DOUBLE)ptr->class0.pulsesPerRevolution) *
                                                validLPPointInfo[currentChannel].multiplier *
                                                (DOUBLE)(60.0 /ptr->class14.intervalLength) / 1000.0);

                                if (verifyAndAddPointToReturnMsg (validLPPointInfo[currentChannel].pointId,
                                                                   intervalData,
                                                                   NormalQuality,
                                                                   (dayRecordTime + ((currentInterval+1) * ptr->class14.intervalLength * 60)),
                                                                   pPIL,
                                                                   TAG_POINT_LOAD_PROFILE_DATA))
                                {
                                    isIntervalValid = TRUE;
                                }

                                /****************************
                                *  on the last lp message, fill the point change
                                *  list for display apps to see the last load profile
                                *  interval
                                *
                                *  NOTE:  we only end up here if the interval was valid
                                *       so unitialized data will not accidently be
                                *       sent to the display apps
                                *****************************
                                */
                                if (ptr->lastLPMessage)
                                {
                                    /********************
                                    * after which, we allocate the memory for the second
                                    * return list and fill it as we go
                                    *********************
                                    */

                                    if (isNextInterval)
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
                                        isNextInterval = FALSE;
                                    }

                                    // add point change message
                                    verifyAndAddPointToReturnMsg (validLPPointInfo[currentChannel].pointId,
                                                                  intervalData,
                                                                  NormalQuality,
                                                                  (dayRecordTime + ((currentInterval+1) * ptr->class14.intervalLength * 60)),
                                                                  pLastLPIntervals);
                                }
                            }
                    }
                }

                // make sure there was data recorded at this interval
                if (isIntervalValid)
                {
                    // set the last lp time
                    setLastLPTime((dayRecordTime + ((currentInterval+1) * ptr->class14.intervalLength * 60)));
                }

                isIntervalValid = FALSE;
            }
            // tells me to remove the point changes if the data is valid
            isNextInterval = TRUE;
        }
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
    if (ptr->lastLPMessage)
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

    return NORMAL;
}



INT CtiDeviceAlphaPPlus::reformatDataBuffer(BYTE *aInMessBuffer, ULONG &aTotalBytes)
{
    AlphaPPlusScanData_t *ptr = (AlphaPPlusScanData_t *)(_dataBuffer);
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

        ptr->Real.class0.wattHoursPerRevolution = (ULONG) BCDtoBase10(ptr->Byte.class0.UKH,     3) / 1000.0;
        ptr->Real.class0.pulsesPerRevolution = (USHORT) BCDtoBase10(&ptr->Byte.class0.UPR,     1);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Unable to read " << getName() << " Class 0 defaulting decimals" << endl;
        }
    }

    // if we were able to retrieve class 6
    ptr->Real.class6.primaryFunction = PPLUS_PULSE_FUNCTIONALITY_KWH;
    ptr->Real.class6.secondaryFunction = PPLUS_PULSE_FUNCTIONALITY_KVARH;

    if (ptr->Real.class6.valid)
    {
        ptr->Real.class6.primaryFunction = primaryFunction(ptr->Byte.class6.XUOM[0]);
        ptr->Real.class6.secondaryFunction = secondaryFunction(ptr->Byte.class6.XUOM[0]);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Unable to read " << getName() << " Class 6 defaulting functionality" << endl;
        }
    }

    ptr->Real.class2.configTOUBlk1  = PPLUS_KW_DELIVERED;
    ptr->Real.class2.configTOUBlk2  = PPLUS_REACTIVE_DELIVERED;

    if (ptr->Real.class2.valid)
    {
        ptr->Real.class2.configTOUBlk1  = touBlockMapping (ptr->Byte.class2.EBLKCF1,ptr->Real.class6.primaryFunction);
        if (ptr->Real.class2.configTOUBlk1 == PPLUS_DISABLED)
        {
            ptr->Real.class2.configTOUBlk1  = touBlockMapping (ptr->Byte.class2.EBLKCF1,ptr->Real.class6.secondaryFunction);
        }

        ptr->Real.class2.configTOUBlk2  = touBlockMapping (ptr->Byte.class2.EBLKCF2, ptr->Real.class6.primaryFunction);
        if (ptr->Real.class2.configTOUBlk2 == PPLUS_DISABLED)
        {
            ptr->Real.class2.configTOUBlk2  = touBlockMapping (ptr->Byte.class2.EBLKCF2,ptr->Real.class6.secondaryFunction);
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Unable to read " << getName() << " Class 2 defaulting block mappings" << endl;
        }
    }

    ptr->Real.class82.BLK1Demand    = 0.0;
    ptr->Real.class82.BLK2Demand    = 0.0;
    if (ptr->Real.class82.valid)
    {
        // class 82
        ptr->Real.class82.BLK1Demand    = ((FLOAT) BCDtoBase10(ptr->Byte.class82.BLK1,    3) / ptr->Real.class0.demandDecimals);
        ptr->Real.class82.BLK2Demand    = ((FLOAT) BCDtoBase10(ptr->Byte.class82.BLK2,    3) / ptr->Real.class0.demandDecimals);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Unable to read " << getName() << " Class 82 demand data not collected" << endl;
        }
    }

    // class 11
    if (ptr->Real.class11.valid)
    {
        // class 11
        ptr->Real.class11.EAVGPF         = ((FLOAT) BCDtoBase10(ptr->Byte.class11.EAVGPF,      2)) / 1000.0;
        ptr->Real.class11.ETKWH2         = ((DOUBLE) PPlusBCDtoDouble(ptr->Byte.class11.ETKWH2,      7)) / (1000000.0 * ptr->Real.class0.energyDecimals);
        ptr->Real.class11.ETKWH1         = ((DOUBLE) PPlusBCDtoDouble(ptr->Byte.class11.ETKWH1,      7)) / (1000000.0 * ptr->Real.class0.energyDecimals);
        ptr->Real.class11.EKVARH1        = ((DOUBLE) PPlusBCDtoDouble(ptr->Byte.class11.EKVARH1,     7)) / (1000000.0 * ptr->Real.class0.energyDecimals);
        ptr->Real.class11.EKVARH2        = ((DOUBLE) PPlusBCDtoDouble(ptr->Byte.class11.EKVARH2,     7)) / (1000000.0 * ptr->Real.class0.energyDecimals);
        ptr->Real.class11.EKVARH3        = ((DOUBLE) PPlusBCDtoDouble(ptr->Byte.class11.EKVARH3,     7)) / (1000000.0 * ptr->Real.class0.energyDecimals);
        ptr->Real.class11.EKVARH4        = ((DOUBLE) PPlusBCDtoDouble(ptr->Byte.class11.EKVARH4,     7)) / (1000000.0 * ptr->Real.class0.energyDecimals);

        ptr->Real.class11.DKWC2          = ((FLOAT) BCDtoBase10(ptr->Byte.class11.DKWC2,       3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.DKWCUM2        = ((FLOAT) BCDtoBase10(ptr->Byte.class11.DKWCUM2,     3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.DTD2.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD2.Year,   1);
        ptr->Real.class11.DTD2.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD2.Month,  1);
        ptr->Real.class11.DTD2.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD2.Day,    1);
        ptr->Real.class11.DTD2.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD2.Hour,   1);
        ptr->Real.class11.DTD2.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD2.Minute, 1);
        ptr->Real.class11.DKW2           = ((FLOAT) BCDtoBase10(ptr->Byte.class11.DKW2,        3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.DKWH2          = ((DOUBLE) PPlusBCDtoDouble(ptr->Byte.class11.DKWH2,       7)) / (1000000.0 * ptr->Real.class0.energyDecimals);

        ptr->Real.class11.CKWC2          = ((FLOAT) BCDtoBase10(ptr->Byte.class11.CKWC2,       3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.CKWCUM2        = ((FLOAT) BCDtoBase10(ptr->Byte.class11.CKWCUM2,     3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.CTD2.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD2.Year,   1);
        ptr->Real.class11.CTD2.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD2.Month,  1);
        ptr->Real.class11.CTD2.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD2.Day,    1);
        ptr->Real.class11.CTD2.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD2.Hour,   1);
        ptr->Real.class11.CTD2.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD2.Minute, 1);
        ptr->Real.class11.CKW2           = ((FLOAT) BCDtoBase10(ptr->Byte.class11.CKW2,        3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.CKWH2          = ((DOUBLE) PPlusBCDtoDouble(ptr->Byte.class11.CKWH2,       7)) / (1000000.0 * ptr->Real.class0.energyDecimals);

        ptr->Real.class11.BKWC2          = ((FLOAT) BCDtoBase10(ptr->Byte.class11.BKWC2,       3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.BKWCUM2        = ((FLOAT) BCDtoBase10(ptr->Byte.class11.BKWCUM2,     3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.BTD2.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD2.Year,   1);
        ptr->Real.class11.BTD2.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD2.Month,  1);
        ptr->Real.class11.BTD2.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD2.Day,    1);
        ptr->Real.class11.BTD2.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD2.Hour,   1);
        ptr->Real.class11.BTD2.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD2.Minute, 1);
        ptr->Real.class11.BKW2           = ((FLOAT) BCDtoBase10(ptr->Byte.class11.BKW2,        3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.BKWH2          = ((DOUBLE) PPlusBCDtoDouble(ptr->Byte.class11.BKWH2,       7)) / (1000000.0 * ptr->Real.class0.energyDecimals);

        ptr->Real.class11.AKWC2          = ((FLOAT) BCDtoBase10(ptr->Byte.class11.AKWC2,       3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.AKWCUM2        = ((FLOAT) BCDtoBase10(ptr->Byte.class11.AKWCUM2,     3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.ATD2.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD2.Year,   1);
        ptr->Real.class11.ATD2.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD2.Month,  1);
        ptr->Real.class11.ATD2.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD2.Day,    1);
        ptr->Real.class11.ATD2.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD2.Hour,   1);
        ptr->Real.class11.ATD2.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD2.Minute, 1);
        ptr->Real.class11.AKW2           = ((FLOAT) BCDtoBase10(ptr->Byte.class11.AKW2,        3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.AKWH2          = ((DOUBLE) PPlusBCDtoDouble(ptr->Byte.class11.AKWH2,       7)) / (1000000.0 * ptr->Real.class0.energyDecimals);

        ptr->Real.class11.DKWC1          = ((FLOAT) BCDtoBase10(ptr->Byte.class11.DKWC1,       3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.DKWCUM1        = ((FLOAT) BCDtoBase10(ptr->Byte.class11.DKWCUM1,     3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.DTD1.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD1.Year,   1);
        ptr->Real.class11.DTD1.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD1.Month,  1);
        ptr->Real.class11.DTD1.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD1.Day,    1);
        ptr->Real.class11.DTD1.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD1.Hour,   1);
        ptr->Real.class11.DTD1.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.DTD1.Minute, 1);
        ptr->Real.class11.DKW1           = ((FLOAT) BCDtoBase10(ptr->Byte.class11.DKW1,        3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.DKWH1          = ((DOUBLE) PPlusBCDtoDouble(ptr->Byte.class11.DKWH1,       7)) / (1000000.0 * ptr->Real.class0.energyDecimals);

        ptr->Real.class11.CKWC1          = ((FLOAT) BCDtoBase10(ptr->Byte.class11.CKWC1,       3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.CKWCUM1        = ((FLOAT) BCDtoBase10(ptr->Byte.class11.CKWCUM1,     3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.CTD1.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD1.Year,   1);
        ptr->Real.class11.CTD1.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD1.Month,  1);
        ptr->Real.class11.CTD1.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD1.Day,    1);
        ptr->Real.class11.CTD1.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD1.Hour,   1);
        ptr->Real.class11.CTD1.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.CTD1.Minute, 1);
        ptr->Real.class11.CKW1           = ((FLOAT) BCDtoBase10(ptr->Byte.class11.CKW1,        3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.CKWH1          = ((DOUBLE) PPlusBCDtoDouble(ptr->Byte.class11.CKWH1,       7)) / (1000000.0 * ptr->Real.class0.energyDecimals);

        ptr->Real.class11.BKWC1          = ((FLOAT) BCDtoBase10(ptr->Byte.class11.BKWC1,       3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.BKWCUM1        = ((FLOAT) BCDtoBase10(ptr->Byte.class11.BKWCUM1,     3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.BTD1.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD1.Year,   1);
        ptr->Real.class11.BTD1.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD1.Month,  1);
        ptr->Real.class11.BTD1.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD1.Day,    1);
        ptr->Real.class11.BTD1.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD1.Hour,   1);
        ptr->Real.class11.BTD1.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.BTD1.Minute, 1);
        ptr->Real.class11.BKW1           = ((FLOAT) BCDtoBase10(ptr->Byte.class11.BKW1,        3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.BKWH1          = ((DOUBLE) PPlusBCDtoDouble(ptr->Byte.class11.BKWH1,       7)) / (1000000.0 * ptr->Real.class0.energyDecimals);

        ptr->Real.class11.AKWC1          = ((FLOAT) BCDtoBase10(ptr->Byte.class11.AKWC1,       3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.AKWCUM1        = ((FLOAT) BCDtoBase10(ptr->Byte.class11.AKWCUM1,     3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.ATD1.Year      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD1.Year,   1);
        ptr->Real.class11.ATD1.Month     = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD1.Month,  1);
        ptr->Real.class11.ATD1.Day       = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD1.Day,    1);
        ptr->Real.class11.ATD1.Hour      = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD1.Hour,   1);
        ptr->Real.class11.ATD1.Minute    = (UCHAR) BCDtoBase10(&ptr->Byte.class11.ATD1.Minute, 1);
        ptr->Real.class11.AKW1           = ((FLOAT) BCDtoBase10(ptr->Byte.class11.AKW1,        3)) / (ptr->Real.class0.demandDecimals);
        ptr->Real.class11.AKWH1          = ((DOUBLE) PPlusBCDtoDouble(ptr->Byte.class11.AKWH1,       7)) / (1000000.0 * ptr->Real.class0.energyDecimals);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Unable to read " << getName() << " Class 11 billing data not collected" << endl;
        }
    }

    memcpy (aInMessBuffer, _dataBuffer, sizeof (AlphaPPlusScanData_t));
    aTotalBytes = ptr->totalByteCount;
    ptr->bDataIsReal = TRUE;
    return NORMAL;
}


INT CtiDeviceAlphaPPlus::copyLoadProfileData(BYTE *aInMessBuffer, ULONG &aTotalBytes)
{
    memcpy(aInMessBuffer, _loadProfileBuffer, sizeof (AlphaPPlusLoadProfile_t));
    aTotalBytes = sizeof (AlphaPPlusLoadProfile_t);
    return NORMAL;
}


INT CtiDeviceAlphaPPlus::ResultDisplay(INMESS *InMessage)
{
    DIALUPREPLY        *DUPRep       = &InMessage->Buffer.DUPSt.DUPRep;
    /* Misc. definitions */
    ULONG i, j;
    CHAR buffer[200];

    // looking for billing data DLS
    AlphaPPlusScanData_t  *ptr = (AlphaPPlusScanData_t *)DUPRep->Message;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Result display for device " << getName() << " in progress " << endl;

        sprintf(buffer,"--------- Class 0 ---------");
        dout << endl << buffer << endl;
        sprintf(buffer,"DemandDecimals= %f",ptr->Real.class0.demandDecimals);
        dout << buffer << endl;
        sprintf(buffer,"EnergyDecimals= %f",ptr->Real.class0.energyDecimals);
        dout << buffer << endl;
        sprintf(buffer,"Kh           =   %f",ptr->Real.class0.wattHoursPerRevolution);
        dout << buffer << endl;
        sprintf(buffer,"Mp            = %d",ptr->Real.class0.pulsesPerRevolution);
        dout << buffer << endl;

        sprintf(buffer,"--------- Class 2 ---------");
        dout << endl << buffer << endl;
        sprintf(buffer,"Blk 1 Mapping= %d",ptr->Real.class2.configTOUBlk1);
        dout << buffer << endl;
        sprintf(buffer,"Blk 2 Mapping= %d",ptr->Real.class2.configTOUBlk2);
        dout << buffer << endl;

        sprintf(buffer,"--------- Class 82 ---------");
        dout << endl << buffer << endl;
        sprintf(buffer,"BLK1          = %f",ptr->Real.class82.BLK1Demand);
        dout << buffer << endl;
        sprintf(buffer,"BLK2          = %f",ptr->Real.class82.BLK2Demand);
        dout << buffer << endl;

        sprintf(buffer,"--------- Class 11 ---------");
        dout << endl << buffer << endl;
        sprintf(buffer,"EAVGPF   = %f",       ptr->Real.class11.EAVGPF   );
        dout << buffer << endl;
        sprintf(buffer,"ETKWH2   = %f",       ptr->Real.class11.ETKWH2   );
        dout << buffer << endl;
        sprintf(buffer,"ETKWH1   = %f",       ptr->Real.class11.ETKWH1   );
        dout << buffer << endl;
        sprintf(buffer,"EKVARH1  = %f",       ptr->Real.class11.EKVARH1  );
        dout << buffer << endl;
        sprintf(buffer,"EKVARH2  = %f",       ptr->Real.class11.EKVARH2  );
        dout << buffer << endl;
        sprintf(buffer,"EKVARH3  = %f",       ptr->Real.class11.EKVARH3  );
        dout << buffer << endl;
        sprintf(buffer,"EKVARH4  = %f",       ptr->Real.class11.EKVARH4  );
        dout << buffer << endl << endl;

        sprintf(buffer,"AKWH1    = %f",       ptr->Real.class11.AKWH1    );
        dout << buffer << endl;
        sprintf(buffer,"AKW1     = %f",       ptr->Real.class11.AKW1     );
        dout << buffer << endl;
        sprintf(buffer,"ATD1     = %d/%d/%d %02d:%02d",
                ptr->Real.class11.ATD1.Month,
                ptr->Real.class11.ATD1.Day,
                ptr->Real.class11.ATD1.Year,
                ptr->Real.class11.ATD1.Hour,
                ptr->Real.class11.ATD1.Minute);
        dout << buffer << endl;
        sprintf(buffer,"AKWCUM1  = %f",       ptr->Real.class11.AKWCUM1  );
        dout << buffer << endl;
        sprintf(buffer,"AKWC1    = %f",       ptr->Real.class11.AKWC1    );
        dout << buffer << endl;

        sprintf(buffer,"BKWH1    = %f",       ptr->Real.class11.BKWH1    );
        dout << buffer << endl;
        sprintf(buffer,"BKW1     = %f",       ptr->Real.class11.BKW1     );
        dout << buffer << endl;
        sprintf(buffer,"BTD1     = %d/%d/%d %02d:%02d",
                ptr->Real.class11.BTD1.Month,
                ptr->Real.class11.BTD1.Day,
                ptr->Real.class11.BTD1.Year,
                ptr->Real.class11.BTD1.Hour,
                ptr->Real.class11.BTD1.Minute);
        dout << buffer << endl;
        sprintf(buffer,"BKWCUM1  = %f",       ptr->Real.class11.BKWCUM1  );
        dout << buffer << endl;
        sprintf(buffer,"BKWC1    = %f",       ptr->Real.class11.BKWC1    );
        dout << buffer << endl;

        sprintf(buffer,"CKWH1    = %f",       ptr->Real.class11.CKWH1    );
        dout << buffer << endl;
        sprintf(buffer,"CKW1     = %f",       ptr->Real.class11.CKW1     );
        dout << buffer << endl;
        sprintf(buffer,"CTD1     = %d/%d/%d %02d:%02d",
                ptr->Real.class11.CTD1.Month,
                ptr->Real.class11.CTD1.Day,
                ptr->Real.class11.CTD1.Year,
                ptr->Real.class11.CTD1.Hour,
                ptr->Real.class11.CTD1.Minute);
        dout << buffer << endl;
        sprintf(buffer,"CKWCUM1  = %f",       ptr->Real.class11.CKWCUM1  );
        dout << buffer << endl;
        sprintf(buffer,"CKWC1    = %f",       ptr->Real.class11.CKWC1    );
        dout << buffer << endl;


        sprintf(buffer,"DKWH1    = %f",       ptr->Real.class11.DKWH1    );
        dout << buffer << endl;
        sprintf(buffer,"DKW1     = %f",       ptr->Real.class11.DKW1     );
        dout << buffer << endl;
        sprintf(buffer,"DTD1     = %d/%d/%d %02d:%02d",
                ptr->Real.class11.DTD1.Month,
                ptr->Real.class11.DTD1.Day,
                ptr->Real.class11.DTD1.Year,
                ptr->Real.class11.DTD1.Hour,
                ptr->Real.class11.DTD1.Minute);
        dout << buffer << endl;
        sprintf(buffer,"DKWCUM1  = %f",       ptr->Real.class11.DKWCUM1  );
        dout << buffer << endl;
        sprintf(buffer,"DKWC1    = %f",       ptr->Real.class11.DKWC1    );
        dout << buffer << endl;


        sprintf(buffer,"AKWH2    = %f",       ptr->Real.class11.AKWH2    );
        dout << buffer << endl;
        sprintf(buffer,"AKW2     = %f",       ptr->Real.class11.AKW2     );
        dout << buffer << endl;
        sprintf(buffer,"ATD2     = %d/%d/%d %02d:%02d",
                ptr->Real.class11.ATD2.Month,
                ptr->Real.class11.ATD2.Day,
                ptr->Real.class11.ATD2.Year,
                ptr->Real.class11.ATD2.Hour,
                ptr->Real.class11.ATD2.Minute);
        dout << buffer << endl;
        sprintf(buffer,"AKWCUM2  = %f",       ptr->Real.class11.AKWCUM2  );
        dout << buffer << endl;
        sprintf(buffer,"AKWC2    = %f",       ptr->Real.class11.AKWC2    );
        dout << buffer << endl;

        sprintf(buffer,"BKWH2    = %f",       ptr->Real.class11.BKWH2    );
        dout << buffer << endl;
        sprintf(buffer,"BKW2     = %f",       ptr->Real.class11.BKW2     );
        dout << buffer << endl;
        sprintf(buffer,"BTD2     = %d/%d/%d %02d:%02d",
                ptr->Real.class11.BTD2.Month,
                ptr->Real.class11.BTD2.Day,
                ptr->Real.class11.BTD2.Year,
                ptr->Real.class11.BTD2.Hour,
                ptr->Real.class11.BTD2.Minute);
        dout << buffer << endl;
        sprintf(buffer,"BKWCUM2  = %f",       ptr->Real.class11.BKWCUM2  );
        dout << buffer << endl;
        sprintf(buffer,"BKWC2    = %f",       ptr->Real.class11.BKWC2    );
        dout << buffer << endl;


        sprintf(buffer,"CKWH2    = %f",       ptr->Real.class11.CKWH2    );
        dout << buffer << endl;
        sprintf(buffer,"CKW2     = %f",       ptr->Real.class11.CKW2     );
        dout << buffer << endl;
        sprintf(buffer,"CTD2     = %d/%d/%d %02d:%02d",
                ptr->Real.class11.CTD2.Month,
                ptr->Real.class11.CTD2.Day,
                ptr->Real.class11.CTD2.Year,
                ptr->Real.class11.CTD2.Hour,
                ptr->Real.class11.CTD2.Minute);
        dout << buffer << endl;
        sprintf(buffer,"CKWCUM2  = %f",       ptr->Real.class11.CKWCUM2  );
        dout << buffer << endl;
        sprintf(buffer,"CKWC2    = %f",       ptr->Real.class11.CKWC2    );
        dout << buffer << endl;

        sprintf(buffer,"DKWH2    = %f",       ptr->Real.class11.DKWH2    );
        dout << buffer << endl;
        sprintf(buffer,"DKW2     = %f",       ptr->Real.class11.DKW2     );
        dout << buffer << endl;
        sprintf(buffer,"DTD2     = %d/%d/%d %02d:%02d",
                ptr->Real.class11.DTD2.Month,
                ptr->Real.class11.DTD2.Day,
                ptr->Real.class11.DTD2.Year,
                ptr->Real.class11.DTD2.Hour,
                ptr->Real.class11.DTD2.Minute);
        dout << buffer << endl;

        sprintf(buffer,"DKWCUM2  = %f",       ptr->Real.class11.DKWCUM2  );
        dout << buffer << endl;
        sprintf(buffer,"DKWC2    = %f",       ptr->Real.class11.DKWC2    );
        dout << buffer << endl;

    }
    return(NORMAL);
}


//  is this ever used?  it uses DIALUP_COMP_*, which are defined in DIALUP.H and therefore undesirable.
//    i was going to redefine them as an enum inside CtiDeviceAlphaPPlus, but this doesn't look like
//    it's used, so it gets the axe.
//    2001-oct-04 mskf
/*INT  CtiDeviceAlphaPPlus::ResultFailureDisplay(INT FailError)
{
    USHORT Mask = 0x8000;
    USHORT Err  = 0;

    do
    {

        if (FailError & Mask)
        {
            Err = FailError & Mask;

            switch (Err)
            {
                case DIALUP_COMP_START:
                    {
                        printf("No communication whatsoever with the remote device.\n");
                        printf("  Verify correct device type and or protocol.\n");
                        break;
                    }
                case DIALUP_COMP_ID:
                    {
                        printf("Communication failed during or following the ID sequence.\n");
                        printf("  Verify correct device type\n");
                        break;
                    }
                case DIALUP_COMP_PWD:
                    {
                        printf("Communication failed during or following the password sequence.\n");
                        printf("  Verify correct password entry in device database.\n");
                        break;
                    }
                case DIALUP_COMP_CLASS0:
                    {
                        printf("Communication failed after class 0 read.\n");
                        printf("  Try communications again, verify rules class is programmed.\n");
                        break;
                    }
                case DIALUP_COMP_SENDCLASS:
                    {
                        printf("Communication failed during or following a class request.\n");
                        printf("  Try communications again.\n");
                        break;
                    }
                case DIALUP_COMP_RECKNOWN:
                case DIALUP_COMP_RECUNKNOWN:
                    {
                        printf("Communication failed during or following receipt of class data.\n");
                        printf("  Try communications again.\n");
                        break;
                    }
                case DIALUP_COMP_CONTREAD:
                    {
                        printf("Communication failed during or following the password sequence.\n");
                        printf("  Try communications again, verify rules class is programmed.\n");
                        break;
                    }
                case DIALUP_COMP_RCREAD1:
                case DIALUP_COMP_RCREAD2:
                    {
                        printf("Communication failed following a rules class read.\n");
                        printf("  Try communications again.\n");
                        break;
                    }
            }
        }

        Mask >>= 1;

    } while (Mask != 0 && Err == 0);
    return(NORMAL);
}*/



INT CtiDeviceAlphaPPlus::getAPlusFuncOffset(UINT Key, VOID *ptr)
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
                for (i = 0; i < sizeof(APlusFunctions)/sizeof(CTI_alpha_func) ; i++)
                {
                    if (APlusFunctions[i].Function == inputFunction)
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
                for (i = 0; i < sizeof(APlusFunctions)/sizeof(CTI_alpha_func) ; i++)
                {
                    if (!strcmp((char*)ptr, APlusFunctions[i].Description))
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

INT CtiDeviceAlphaPPlus::getAPlusClassOffset(UINT Key, VOID *ptr)
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
                for (i = 0; i < sizeof(APlusClasses)/sizeof(CTI_alpha_class) ; i++)
                {
                    if (APlusClasses[i].Class == inputClass)
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
                for (i = 0; i < sizeof(APlusClasses)/sizeof(CTI_alpha_class) ; i++)
                {
                    if (!strcmp((char*)ptr, APlusClasses[i].Description))
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


UCHAR CtiDeviceAlphaPPlus::touBlockMapping (UCHAR config, USHORT type)
{
    USHORT retCode=PPLUS_DISABLED;

    // if 7 is set, we've got either watts delivered or KVA delivered
    if (config & 0x80)
    {
        retCode = PPLUS_KW_DELIVERED;
    }
    else if (config & 0x40)
    {
        retCode = PPLUS_KW_RECEIVED;
    }
    else if (type == PPLUS_PULSE_FUNCTIONALITY_KVARH)
    {
        if ((config & 0x01) && (config & 0x02))
        {
            retCode = PPLUS_REACTIVE_DELIVERED;
        }
        else if ((config & 0x04) && (config & 0x08))
        {
            retCode = PPLUS_REACTIVE_RECEIVED;
        }
        else if (config & 0x01)
        {
/*
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " ****** Alpha " << getName() << " configured for Q1 vars, defaulting to delivered vars ******" << endl;
            }
            retCode = PPLUS_REACTIVE_DELIVERED;
*/
            retCode = PPLUS_REACTIVE_QUADRANT1;
        }
        else if (config & 0x02)
        {
/*
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " ****** Alpha " << getName() << " configured for Q2 vars, defaulting to delivered vars ******" << endl;
            }
            retCode = PPLUS_REACTIVE_DELIVERED;
*/
            retCode = PPLUS_REACTIVE_QUADRANT2;
        }
        else if (config & 0x03)
        {
/*
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " ****** Alpha " << getName() << " configured for Q3 vars, defaulting to delivered vars ******" << endl;
            }
            retCode = PPLUS_REACTIVE_DELIVERED;
*/
            retCode = PPLUS_REACTIVE_QUADRANT3;
        }
        else if (config & 0x04)
        {
/*
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " ****** Alpha " << getName() << " configured for Q4 vars, defaulting to delivered vars ******" << endl;
            }
            retCode = PPLUS_REACTIVE_DELIVERED;
*/
            retCode = PPLUS_REACTIVE_QUADRANT4;
        }
        else
            retCode = PPLUS_DISABLED;

    }
    else if (type == PPLUS_PULSE_FUNCTIONALITY_KVAH)
    {
        if ((config & 0x01) && (config & 0x02))
        {
            retCode = PPLUS_KVA_DELIVERED;
        }
        else if ((config & 0x04) && (config & 0x08))
        {
            retCode = PPLUS_KVA_RECEIVED;
        }
        else
            retCode = PPLUS_DISABLED;
    }
    else
        retCode = PPLUS_DISABLED;

    return retCode;
}

USHORT CtiDeviceAlphaPPlus::primaryFunction (UCHAR function)
{
    BYTEUSHORT flip;
    USHORT retVal = PPLUS_PULSE_FUNCTIONALITY_NONE;

    flip.sh = 0;
    flip.ch[0] = (function & 0xF0) >> 4;
    switch (flip.sh)
    {
        case 1:
            {
                retVal = PPLUS_PULSE_FUNCTIONALITY_KWH;
                break;
            }
        case 2:
            {
                retVal = PPLUS_PULSE_FUNCTIONALITY_KVAH;
                break;
            }
        case 3:
            {
                retVal = PPLUS_PULSE_FUNCTIONALITY_KVARH;
                break;
            }
        default:
            retVal = PPLUS_PULSE_FUNCTIONALITY_NONE;

    }
    return retVal;
}

USHORT CtiDeviceAlphaPPlus::secondaryFunction (UCHAR function)
{
    BYTEUSHORT flip;
    USHORT retVal = PPLUS_PULSE_FUNCTIONALITY_NONE;

    flip.sh = 0;
    flip.ch[0] = function & 0x0F;

    switch (flip.sh)
    {
        case 1:
            {
                retVal = PPLUS_PULSE_FUNCTIONALITY_KWH;
                break;
            }
        case 2:
            {
                retVal = PPLUS_PULSE_FUNCTIONALITY_KVAH;
                break;
            }
        case 3:
            {
                retVal = PPLUS_PULSE_FUNCTIONALITY_KVARH;
                break;
            }
        default:
            retVal = PPLUS_PULSE_FUNCTIONALITY_NONE;

    }
    return retVal;
}



LONG CtiDeviceAlphaPPlus::findLPDataPoint (AlphaLPPointInfo_t &point, USHORT aMapping, AlphaPPlusClass2Real_t class2)
{
    LONG retCode = NORMAL;
    CtiPointNumeric   *pNumericPoint = NULL;

    // always set the metric
    point.mapping = aMapping;

    switch (aMapping)
    {
        case PPLUS_KW_DELIVERED:
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
        case PPLUS_REACTIVE_DELIVERED:
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
        case PPLUS_KVA_DELIVERED:
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
        case PPLUS_REACTIVE_QUADRANT1:
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
        case PPLUS_REACTIVE_QUADRANT2:
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
        case PPLUS_REACTIVE_QUADRANT3:
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
        case PPLUS_REACTIVE_QUADRANT4:
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

        case PPLUS_TOU_BLK1:
            {
                retCode = findLPDataPoint (point, class2.configTOUBlk1, class2);
                break;
            }
        case PPLUS_TOU_BLK2:
            {
                retCode = findLPDataPoint (point, class2.configTOUBlk2, class2);
                break;
            }

        case PPLUS_DISABLED:
        case PPLUS_KW_RECEIVED:
        case PPLUS_KVA_RECEIVED:
        case PPLUS_REACTIVE_RECEIVED:
        default:
            {
                point.pointId = 0;
                point.multiplier = 1.0;
                retCode = !NORMAL;
            }
    }
    return retCode;
}

BOOL CtiDeviceAlphaPPlus::getMeterDataFromScanStruct (int aOffset, DOUBLE &aValue, RWTime &peak, AlphaPPlusScanData_t *aScanData)
{
    BOOL isValidPoint = FALSE;

    // this is initial value
    peak = rwEpoch;

    /* Get the value from InMessage */
    switch (aOffset)
    {
        case OFFSET_QUADRANT1_LAST_INTERVAL_KVAR:
        case OFFSET_QUADRANT2_LAST_INTERVAL_KVAR:
        case OFFSET_QUADRANT3_LAST_INTERVAL_KVAR:
        case OFFSET_QUADRANT4_LAST_INTERVAL_KVAR:
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

        case OFFSET_QUADRANT1_TOTAL_KVARH:
        case OFFSET_QUADRANT2_TOTAL_KVARH:
        case OFFSET_QUADRANT3_TOTAL_KVARH:
        case OFFSET_QUADRANT4_TOTAL_KVARH:
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


BOOL CtiDeviceAlphaPPlus::getRateValueFromBlock (DOUBLE &aValue,
                                                 USHORT aValueType,
                                                 USHORT aBlockMapping,
                                                 USHORT aRate,
                                                 RWTime &aPeak,
                                                 AlphaPPlusScanData_t *data)
{
    int x;
    BOOL retCode=FALSE;

    if (data->Real.class2.configTOUBlk1 == aBlockMapping)
    {
        if (aValueType == ALPHA_DEMAND)
        {
            retCode = getDemandValueFromBlock1 (aValue, aRate, aPeak, data);
        }
        else
        {
            retCode = getEnergyValueFromBlock1 (aValue, aRate, aPeak, data);
        }

    }
    else if (data->Real.class2.configTOUBlk2 == aBlockMapping)
    {
        if (aValueType == ALPHA_DEMAND)
        {
            retCode = getDemandValueFromBlock2 (aValue, aRate, aPeak, data);
        }
        else
        {
            retCode = getEnergyValueFromBlock2 (aValue, aRate, aPeak, data);
        }
    }

    return retCode;
}

BOOL CtiDeviceAlphaPPlus::getDemandValueFromBlock1 (DOUBLE &aValue,
                                                    USHORT aRate,
                                                    RWTime &aPeak,
                                                    AlphaPPlusScanData_t *aScanData)
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
        case ALPHA_RATE_TOTAL:
            {
                aValue= aScanData->Real.class82.BLK1Demand;
                retCode = TRUE;
                break;
            }
        default:
            break;
    }

    return retCode;
}

BOOL CtiDeviceAlphaPPlus::getDemandValueFromBlock2 (DOUBLE &aValue,
                                                    USHORT aRate,
                                                    RWTime &aPeak,
                                                    AlphaPPlusScanData_t *aScanData)
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
        case ALPHA_RATE_TOTAL:
            {
                aValue= aScanData->Real.class82.BLK2Demand;
                retCode = TRUE;
                break;
            }
        default:
            break;
    }

    return retCode;
}

BOOL CtiDeviceAlphaPPlus::getEnergyValueFromBlock1 (DOUBLE &aValue,
                                                    USHORT aRate,
                                                    RWTime &aPeak,
                                                    AlphaPPlusScanData_t *aScanData)
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
                aValue= aScanData->Real.class11.ETKWH1;
                retCode = TRUE;
                break;
            }
        default:
            break;
    }

    return retCode;
}

BOOL CtiDeviceAlphaPPlus::getEnergyValueFromBlock2 (DOUBLE &aValue,
                                                    USHORT aRate,
                                                    RWTime &aPeak,
                                                    AlphaPPlusScanData_t *aScanData)
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
                aValue= aScanData->Real.class11.ETKWH2;
                retCode = TRUE;
                break;
            }
        default:
            break;
    }

    return retCode;
}


USHORT CtiDeviceAlphaPPlus::getOffsetMapping (int aOffset)
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
                type = PPLUS_KW_DELIVERED;
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
                type = PPLUS_REACTIVE_DELIVERED;
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
                type = PPLUS_KVA_DELIVERED;
                break;
            }
        case OFFSET_QUADRANT1_TOTAL_KVARH:
        case OFFSET_QUADRANT1_LAST_INTERVAL_KVAR:
            {
                type = PPLUS_REACTIVE_QUADRANT1;
                break;
            }
        case OFFSET_QUADRANT2_TOTAL_KVARH:
        case OFFSET_QUADRANT2_LAST_INTERVAL_KVAR:
            {
                type = PPLUS_REACTIVE_QUADRANT2;
                break;
            }
        case OFFSET_QUADRANT3_TOTAL_KVARH:
        case OFFSET_QUADRANT3_LAST_INTERVAL_KVAR:
            {
                type = PPLUS_REACTIVE_QUADRANT3;
                break;
            }
        case OFFSET_QUADRANT4_TOTAL_KVARH:
        case OFFSET_QUADRANT4_LAST_INTERVAL_KVAR:
            {
                type = PPLUS_REACTIVE_QUADRANT4;
                break;
            }
        default:
            break;
    }
    return type;
}

USHORT CtiDeviceAlphaPPlus::getRate (int aOffset)
{
    USHORT rate;

    /* Get the value from InMessage */
    switch (aOffset)
    {
        case OFFSET_QUADRANT1_TOTAL_KVARH:
        case OFFSET_QUADRANT1_LAST_INTERVAL_KVAR:
        case OFFSET_QUADRANT2_TOTAL_KVARH:
        case OFFSET_QUADRANT2_LAST_INTERVAL_KVAR:
        case OFFSET_QUADRANT3_TOTAL_KVARH:
        case OFFSET_QUADRANT3_LAST_INTERVAL_KVAR:
        case OFFSET_QUADRANT4_TOTAL_KVARH:
        case OFFSET_QUADRANT4_LAST_INTERVAL_KVAR:
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

DOUBLE PPlusBCDtoDouble(UCHAR* buffer, USHORT len)
{

    int i, j;
    DOUBLE temp;
    DOUBLE scratch = 0;

    for (i = (INT)len, j = 0; i > 0; j++, i--)
    {
        temp = 0.0;

        /* The high nibble */
        temp += (((buffer[j] & 0xf0) >> 4)  * 10.0);

        /* The Low nibble */
        temp += (buffer[j] & 0x0f);

        scratch = scratch * 100 + temp ;
    }

    return scratch;
}

