/*-----------------------------------------------------------------------------*
*
* File:   dev_single
*
* Date:   10/4/2001
*
* PVCS KEYWORDS:
* REVISION     :  $Revision: 1.34 $
* DATE         :  $Date: 2004/09/30 21:37:21 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning ( disable : 4786 )


#include <vector>
using namespace std;
#include "dev_single.h"
#include "logger.h"
#include "porter.h"
#include "mgr_point.h"
#include "numstr.h"
#include "tbl_ptdispatch.h"
#include "utility.h"

RWTime CtiDeviceSingle::adjustNextScanTime(const INT scanType)
{
    RWTime    Now;
    RWTime    When(YUKONEOT);
    int       loop_count = 0;
    const int MaxLoopCount = 100;

    if( getScanRate(scanType) > 0 )
    {
        if(!(getNextScan(scanType).seconds() % getScanRate(scanType)))
        {   // Aligned to the correct boundary.
            do
            {
                setNextScan( scanType, getNextScan(scanType) + getScanRate(scanType) );
            }
            while(getNextScan(scanType) <= Now && ++loop_count < MaxLoopCount);

            if( loop_count >= MaxLoopCount )
            {
                setNextScan(scanType, firstScan(Now+120, scanType));

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - infinite loop averted in adjustNextScanTime **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
        else
        {
            setNextScan( scanType, firstScan(Now, scanType) );
        }

        When = getNextScan(scanType);
    }
    else if( getScanRate(scanType) == 0 )
    {
        When = When.now();
    }

    return When;
}


RWTime CtiDeviceSingle::firstScan( const RWTime &When, INT rate )
{
    RWTime first;

    if( getScanRate(rate) > 3600 )
    {
        RWTime hourstart = RWTime(When.seconds() - (When.seconds() % 3600)); // align to the hour.
        first = RWTime(hourstart.seconds() - ((hourstart.hour()* 3600) % getScanRate(rate)) + getScanRate(rate));
    }
    else if(getScanRate(rate) > 0 )    // Prevent a divide by zero with this check...
    {
        first = RWTime(When.seconds() - (When.seconds() % getScanRate(rate)) + getScanRate(rate));
    }
    else if(getScanRate(rate) == 0)
    {
        first = When.now();
    }
    else
    {
        first = RWTime(YUKONEOT);
    }

    while(first <= When)
    {
        first += getScanRate(rate);
    }

    return first;
}

void CtiDeviceSingle::validateScanTimes(bool force)
{
    RWTime Now;

    LockGuard guard(monitor());

    for(int rate = 0; rate < ScanRateInvalid; rate++)
    {
        /*
         *  Make sure we have not gone tardy.. nextScheduledScan is used to make sure we are within 1 (or 2)
         *  scan intervals of the expected next time...
         */

        LONG scanrate = getScanRate(rate);

        if(scanrate >= 0)
        {
            bool scanChanged = hasRateOrClockChanged(rate, Now);

            if( force == true || scanChanged )
            {
#if 0
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << getName() << (scanChanged ? " scanChanged " : " force " ) << "  rate " << rate << endl;
                }
#endif
                setNextScan(rate, firstScan(Now, rate));

                if(rate == ScanRateAccum && scanChanged)
                {
                    setLastFreezeNumber(0L);
                    setPrevFreezeNumber(0L);
                    setLastFreezeTime(RWTime());
                    setPrevFreezeTime(RWTime());
                }
            }
            else if(isScanPending() || isScanFreezePending())
            {
                // if we're pending, do the calculation anyway
                setNextScan(rate, firstScan(Now, rate));
            }
        }
    }
}

INT CtiDeviceSingle::initiateAccumulatorScan(RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT      nRet = 0;
    RWTime   Now;
    OUTMESS  *OutMessage = CTIDBG_new OUTMESS;
    /*
     *  This method will be called by each accumulator scanning device prior to the
     *  actual device specific code called by that device type.
     */

    RWTPtrSlist< CtiMessage > vgList;
    RWTPtrSlist< CtiMessage > retList;

    CtiRequestMsg *pReq = CTIDBG_new CtiRequestMsg (getID(),
                                             "scan accumulator",
                                             0,                        // This is a number used to track the message on the outbound request.
                                             0,                        // Client can track this request with this number
                                             getRouteID(),             // This is a specific route is desired.. Scanner does not have this ability
                                             0,                        // This is no longer relavant
                                             0,                        // Try the zeroeth macro offset.
                                             1);                       // One attempt on this route.

    CtiCommandParser parse( pReq->CommandString() );



    if(OutMessage != NULL)
    {
        if(isInhibited())
        {
            adjustNextScanTime(ScanRateAccum);
            nRet = DEVICEINHIBITED;
        }
        else if(!isScanWindowOpen())
        {
            adjustNextScanTime(ScanRateAccum);
            nRet=SCAN_ERROR_DEVICE_WINDOW_CLOSED;
        }
        else if( clearedForScan(ScanRateAccum) )
        {
            resetScanForced();            // Reset this guy since we're doing it

            if(getAddress() == RTUGLOBAL || getAddress() == CCUGLOBAL)
            {
                // CAN NOT scan a global address.
                setScanRate(ScanRateAccum, YUKONEOT);    // set him to the end of time!
                getNextScan(ScanRateAccum) = RWTime(YUKONEOT);
                return SCAN_ERROR_GLOBAL_ADDRESS; // Cannot scan a global address.
            }

            OutMessage->Request.CheckSum = getUniqueIdentifier();  // Mark the OUTMESS with this DEVICE's CRC Id.

            // Do the device's AccumulatorScan!
            nRet = AccumulatorScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority);
            OutMessage = NULL;      // Memory may be forgotten

            if(nRet && nRet != ACCUMSNOTSUPPORTED)
            {
                setScanFreezeFailed();
                // This should be done in Scanner 083199  ReportError(this, nRet);
            }
            else if(nRet != ACCUMSNOTSUPPORTED)
            {
                setScanFreezePending();
            }
        }
        else
        {
            if( getScanRate(ScanRateAccum) < 60 )
            {
                setNextScan(ScanRateAccum, firstScan(Now, ScanRateAccum));
            }
            else            /* Check to be sure we have not gone tardy... */
            if(Now.seconds() - getNextScan(ScanRateAccum).seconds() > 300)
            {
                resetForScan( ScanRateAccum );
            }
        }
    }
    else
    {
        nRet = MEMORY;
    }

    /*
     *  Calculate the next time we see this guy...
     *
     *  Since an accumulator scan is defined in our system to bring back a full dump
     *  of the device, we us the two scan times to do our work.
     */

    if(nRet != ACCUMSNOTSUPPORTED)
    {
        if(!isScanForced())
        {
            if(getNextScan(ScanRateGeneral) <= getNextScan(ScanRateAccum) &&
               !(getScanRate(ScanRateAccum) % getScanRate(ScanRateGeneral)))
            {
                adjustNextScanTime(ScanRateGeneral);
            }

            adjustNextScanTime(ScanRateAccum);
        }
    }
    else
    {
        setNextScan(ScanRateAccum, RWTime(YUKONEOT));
    }

    if(OutMessage != NULL)
    {
        delete OutMessage;
    }

    if(pReq != NULL)
    {
        delete pReq;
    }

    return nRet;
}


INT CtiDeviceSingle::initiateIntegrityScan(RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT      nRet = 0;
    RWTime   Now;
    OUTMESS  *OutMessage = CTIDBG_new OUTMESS;
    /*
     *  This method will be called by each accumulator scanning device prior to the
     *  actual device specific code called by that device type.
     */

    RWTPtrSlist< CtiMessage > vgList;
    RWTPtrSlist< CtiMessage > retList;

    CtiRequestMsg *pReq = CTIDBG_new CtiRequestMsg (getID(),
                                             "scan integrity",
                                             0,                        // This is a number used to track the message on the outbound request.
                                             0,                        // Client can track this request with this number
                                             getRouteID(),             // This is a specific route is desired.. Scanner does not have this ability
                                             0,                        // This is no longer relavant
                                             0,                        // Try the zeroeth macro offset.
                                             1);                       // One attempt on this route.

    CtiCommandParser parse( pReq->CommandString() );



    if(OutMessage != NULL)
    {
        OutMessage->Request.CheckSum = getUniqueIdentifier();  // Mark the OUTMESS with this DEVICE's CRC Id.

        if(isInhibited())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Integrity Scan aborted due to inhibited device: " << getName() << endl;
            }

            nRet =  SCAN_ERROR_DEVICE_INHIBITED;

        }
        else if(!isScanWindowOpen())
        {
            //          {
            //             CtiLockGuard<CtiLogger> doubt_guard(dout);
            //             dout << RWTime() << " " << getName() << "'s scan window is closed " << endl;
            //          }
            nRet=SCAN_ERROR_DEVICE_WINDOW_CLOSED;
        }
        else // if(isInhibited()) ... so it isn't inhibited
        {
            if( clearedForScan(ScanRateIntegrity) )
            {
                resetScanForced();            // Reset this guy since we're doing it

                if(getAddress() == RTUGLOBAL || getAddress() == CCUGLOBAL)
                {
                    // CAN NOT scan a global address.
                    setScanRate(ScanRateIntegrity, YUKONEOT);    // set him to the end of time!
                    setNextScan(ScanRateIntegrity, RWTime(YUKONEOT));

                    return SCAN_ERROR_GLOBAL_ADDRESS; // Cannot scan a global address.
                }

#if 0
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Integrity Scan. Port: " << getPortID() << " Device: " << getID() << ": " << getName() << endl;
                }
#endif

                // Do the devices integrity scan!
                nRet = IntegrityScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority);
                OutMessage = NULL;      // Memory may be forgotten

                if(nRet)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Integrity Scan error " << nRet << endl;
                }
                else
                {
                    setScanPending();
                }

            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Integrity Scan aborted due to scan in progress, device \"" << getName() << "\"" << endl;
                }
                if( getScanRate(ScanRateIntegrity) < 60 )
                {
                    setNextScan(ScanRateIntegrity, firstScan(Now, ScanRateIntegrity));
                }
                else if(Now.seconds() - getNextScan(ScanRateIntegrity).seconds() > 300)
                {
                    resetForScan( ScanRateIntegrity );
                }
            }
        }
    }
    else
    {
        nRet = MEMORY;
    }

    adjustNextScanTime(ScanRateIntegrity);


    if(OutMessage != NULL)
    {
        delete OutMessage;
    }

    if(pReq != NULL)
    {
        delete pReq;
    }

    return nRet;
}


INT CtiDeviceSingle::initiateGeneralScan(RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT      nRet = 0;
    RWTime   Now;
    OUTMESS  *OutMessage = CTIDBG_new OUTMESS;

    RWTPtrSlist< CtiMessage > vgList;
    RWTPtrSlist< CtiMessage > retList;

    CtiRequestMsg *pReq = CTIDBG_new CtiRequestMsg (getID(),
                                             "scan general",
                                             0,                        // This is a number used to track the message on the outbound request.
                                             0,                        // Client can track this request with this number
                                             getRouteID(),             // This is a specific route is desired.. Scanner does not have this ability
                                             0,                        // This is no longer relavant
                                             0,                        // Try the zeroeth macro offset.
                                             1);                       // One attempt on this route.

    CtiCommandParser parse( pReq->CommandString() );


    if(OutMessage != NULL)
    {
        OutMessage->Request.CheckSum = getUniqueIdentifier();  // Mark the OUTMESS with this DEVICE's CRC Id.

        if(getNextScan(ScanRateGeneral).seconds() == 0)
        {
            setNextScan(ScanRateGeneral, firstScan(Now, ScanRateGeneral));
        }
        else
        {
            if(isInhibited())
            {
                /*
                 *  this guy is out of service, so send the last values to VanGogh as a plug?
                 */

                /*
                 *  As a wild hair perhaps the plugging could best be done at VanGogh via a single
                 *  message from Scanner here.. The alternative is a multi-point update
                 *  being blasted out from here to be dispersed by VanGogh as he sees fit.
                 *
                 *  FIX FIX FIX 082899 CGP
                 */

                adjustNextScanTime(ScanRateGeneral);
                nRet = SCAN_ERROR_DEVICE_INHIBITED;
            }
            else if(!isScanWindowOpen())
            {
                adjustNextScanTime(ScanRateGeneral);
                nRet=SCAN_ERROR_DEVICE_WINDOW_CLOSED;
            }
            else // if(isInhibited()) ... so it isn't inhibited
            {
                if( clearedForScan(ScanRateGeneral) )    // FIX FIX FIX 082999 CGP We have no PostCount.
                {
                    resetScanForced();            // Reset this guy since we're doing it

                    if(getAddress() == RTUGLOBAL || getAddress() == CCUGLOBAL)
                    {
                        // CANNOT scan a global address.
                        setScanRate(ScanRateAccum, YUKONEOT);    // set him to the end of time!
                        setNextScan(ScanRateAccum, RWTime(YUKONEOT));

                        return SCAN_ERROR_GLOBAL_ADDRESS; // Cannot scan a global address.
                    }

                    // Do the devices general scan!
                    nRet = GeneralScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority);

                    OutMessage = NULL;      // Memory may be forgotten

                    if( !nRet )
                    {
                        setScanPending();
                    }
                    else     // Error occured
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << Now << " Error " << FormatError(nRet) << " sending general scan to " << getName() << endl;
                        }

                        // Report the comm error and plug any points!
                        // FIX FIX FIX CGP 082999
                        setNextScan(ScanRateGeneral, firstScan(Now, ScanRateGeneral));
                    }
                }
                else
                {
                    if( getScanRate(ScanRateGeneral) < 60 )
                    {
                        setNextScan(ScanRateGeneral, firstScan(Now, ScanRateGeneral));
                    }
                    else if(Now.seconds() - getNextScan(ScanRateGeneral).seconds() > 300) /* Check to be sure we have not gone tardy... */
                    {
                        resetForScan(ScanRateGeneral);
                    }
                }
            }
        }
    }
    else
    {
        nRet = MEMORY;
    }

    /*
     *  Calculate the next time we see this guy...
     *  But only if we are about to do something now (as indicated by OutMessages in the list!).
     */
    if(!isScanForced() && outList.entries() > 0)
    {
        adjustNextScanTime(ScanRateGeneral);
    }

    if(OutMessage != NULL)
    {
        delete OutMessage;
        OutMessage = NULL;
    }

    if(pReq != NULL)
    {
        delete pReq;
    }

    return nRet;
}


INT CtiDeviceSingle::initiateLoadProfileScan(RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT      nRet = 0;
    RWTime   Now;
    OUTMESS  *OutMessage = CTIDBG_new OUTMESS;
    /*
     *  This method will be called by each load profile device prior to the
     *  actual device specific code called by that device type.
     */

    RWTPtrSlist< CtiMessage > vgList;
    RWTPtrSlist< CtiMessage > retList;

    CtiRequestMsg *pReq = CTIDBG_new CtiRequestMsg (getID(),
                                             "scan loadprofile",
                                             0,                        // This is a number used to track the message on the outbound request.
                                             0,                        // Client can track this request with this number
                                             getRouteID(),             // This is a specific route is desired.. Scanner does not have this ability
                                             0,                        // This is no longer relavant
                                             0,                        // Try the zeroeth macro offset.
                                             1);                       // One attempt on this route.

    CtiCommandParser parse( pReq->CommandString() );



    if(OutMessage != NULL)
    {
        OutMessage->Request.CheckSum = getUniqueIdentifier();  // Mark the OUTMESS with this DEVICE's CRC Id.

        if(isInhibited())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Load Profile Scan aborted due to inhibited device: " << getName() << endl;
            }

            nRet =  SCAN_ERROR_DEVICE_INHIBITED;

        }
        else if(!isScanWindowOpen())
        {
            //          {
            //             CtiLockGuard<CtiLogger> doubt_guard(dout);
            //             dout << RWTime() << " " << getName() << "'s scan window is closed " << endl;
            //          }
            nRet = SCAN_ERROR_DEVICE_WINDOW_CLOSED;
        }
        else // if(isInhibited()) ... so it isn't inhibited
        {
            if( clearedForScan(ScanRateLoadProfile) )
            {
                resetScanForced();            // Reset this guy since we're doing it

                if(getAddress() == RTUGLOBAL || getAddress() == CCUGLOBAL)
                {
                    // CAN NOT scan a global address.
                    setScanRate(ScanRateLoadProfile, YUKONEOT);    // set him to the end of time!
                    setNextScan(ScanRateLoadProfile, RWTime(YUKONEOT));

                    return SCAN_ERROR_GLOBAL_ADDRESS; // Cannot scan a global address.
                }

#if 0
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Load Profile Scan. Port: " << getPortID() << " Device: " << getID() << ": " << getName() << endl;
                }
#endif

                // Do the devices load profile scan!
                nRet = LoadProfileScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority);
                OutMessage = NULL;      // Memory may be forgotten

                if(nRet)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Load Profile Scan error " << nRet << endl;
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Load Profile Scan aborted due to scan in progress " << endl;
                }
            }
        }
    }
    else
    {
        nRet = MEMORY;
    }

    adjustNextScanTime(ScanRateLoadProfile);


    if(OutMessage != NULL)
    {
        delete OutMessage;
    }

    if(pReq != NULL)
    {
        delete pReq;
    }

    return nRet;
}


inline CtiProtocolBase *CtiDeviceSingle::getProtocol() const
{
    return 0;
}


int CtiDeviceSingle::generate(CtiXfer &xfer)
{
    int retval = -1;

    if( getProtocol() )     retval = getProtocol()->generate(xfer);

    return retval;
}

int CtiDeviceSingle::decode(CtiXfer &xfer, int status)
{
    int retval = -1;

    if( getProtocol() )     retval = getProtocol()->decode(xfer, status);

    return retval;
}

int CtiDeviceSingle::recvCommRequest(OUTMESS *OutMessage)
{
    int retval = -1;

    if( getProtocol() )     retval = getProtocol()->recvCommRequest(OutMessage);

    return retval;
}

int CtiDeviceSingle::sendCommResult(INMESS *InMessage)
{
    int retval = -1;

    if( getProtocol() )     retval = getProtocol()->sendCommResult(InMessage);

    return retval;
}


bool CtiDeviceSingle::isTransactionComplete(void)
{
    bool retval = true;

    if( getProtocol() )     retval = getProtocol()->isTransactionComplete();

    return retval;
}


void CtiDeviceSingle::getVerificationObjects(queue< CtiVerificationBase * > &work_queue)
{
}


INT CtiDeviceSingle::ProcessResult(INMESS *InMessage,
                                   RWTime &TimeNow,
                                   RWTPtrSlist< CtiMessage >   &vgList,
                                   RWTPtrSlist< CtiMessage > &retList,
                                   RWTPtrSlist< OUTMESS > &outList)
{
    INT   nRet = InMessage->EventCode & 0x3fff;
    INT   DoAccums;
    INT   status = 0;
    bool  bLastFail = false;

    CtiPointDataMsg *commStatus;
    CtiPointBase    *commPoint;


    if( !nRet )
    {
        nRet = ResultDecode(InMessage, TimeNow, vgList, retList, outList);
    }

    if( nRet )
    {
        RWCString errStr;
        RWCString CmdStr("Unknown");
        char ErrStr[80];

        if(InMessage->Return.CommandStr[0] != '\0')
        {
            CmdStr = RWCString(InMessage->Return.CommandStr);
            CmdStr.toLower();
        }

        if( processAdditionalRoutes( InMessage ) )                      // InMessage->Return.MacroOffset != 0)
        {
            OUTMESS *OutTemplate = CTIDBG_new OUTMESS;

            InEchoToOut( InMessage, OutTemplate );

            CtiRequestMsg *pReq = CTIDBG_new CtiRequestMsg(InMessage->TargetID,
                                                    RWCString(InMessage->Return.CommandStr),
                                                    InMessage->Return.UserID,
                                                    InMessage->Return.TrxID,
                                                    InMessage->Return.RouteID,
                                                    InMessage->Return.MacroOffset,
                                                    InMessage->Return.Attempt,
                                                    0,
                                                    InMessage->Priority);


            pReq->setConnectionHandle( InMessage->Return.Connection );

            {
                RWCString msg;
                CtiReturnMsg *Ret = CTIDBG_new CtiReturnMsg( getID(), CmdStr, RWCString("Macro offset ") + CtiNumStr(InMessage->Return.MacroOffset - 1) + RWCString(" failed. Attempting next offset."), nRet, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt, InMessage->Return.TrxID, InMessage->Return.UserID, InMessage->Return.SOE, RWOrdered());

                msg = Ret->ResultString() + "\nError " + CtiNumStr(nRet) + ": " + FormatError(nRet);
                Ret->setResultString( msg );
                Ret->setExpectMore();           // Help MACS know this is intermediate.
                Ret->setStatus( nRet );

                retList.insert( Ret );
            }

            size_t cnt = outList.entries();

            if( 0 != (status = ExecuteRequest(pReq, CtiCommandParser(pReq->CommandString()), vgList, retList, outList, OutTemplate)) )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << RWTime() << "   Status = " << status << ": " << FormatError(status) << endl;
                }
            }

            if(cnt == outList.entries())
            {
                bLastFail = true;
            }

            delete pReq;

            if(OutTemplate != NULL)
            {
                delete OutTemplate;
                OutTemplate = 0;
            }
        }
        else if( CmdStr.contains("scan") && hasLongScanRate( CmdStr ) )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
        else
        {
            bLastFail = true;
        }

        if(bLastFail)
        {
            //  This comm status wasn't handled by portfield (didn't talk directly to this device - targetid != deviceid),
            //    so we have to send it here
            if( InMessage->DeviceID != InMessage->TargetID )
            {
                CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg( getID(), InMessage->Return.CommandStr, "", nRet, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt, InMessage->Return.TrxID, InMessage->Return.UserID, InMessage->Return.SOE, RWOrdered());

                //  Log the communication success on this route.
                if( commPoint = getDevicePointOffsetTypeEqual(COMM_FAIL_OFFSET, StatusPointType) )
                {
                    if( retMsg != NULL )
                    {
                        commStatus = CTIDBG_new CtiPointDataMsg(commPoint->getPointID(), 1.0, NormalQuality, StatusPointType, "", TAG_POINT_MAY_BE_EXEMPTED);

                        if( commStatus != NULL )
                        {
                            retMsg->PointData().insert(commStatus);
                            commStatus = NULL;
                        }

                        vgList.append(retMsg);
                        retMsg = 0;
                    }
                }

                if(retMsg)
                {
                    delete retMsg;
                    retMsg = 0;
                }
            }

            /* something went wrong so start by printing error */
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << TimeNow << " Error (" << (InMessage->EventCode & ~DECODED)  << ") to Remote: " << getName() <<": " << GetError(nRet) << endl;
            }

            /* Form up a general FAILURE message for the client to digest as needed. */
            // dout << "Error return is being sent" << endl;

            CtiReturnMsg *Ret = CTIDBG_new CtiReturnMsg(  getID(), CmdStr, FormatError(nRet), nRet, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt, InMessage->Return.TrxID, InMessage->Return.UserID, InMessage->Return.SOE, RWOrdered());

            retList.insert( Ret );

            /* see what handshake was */
            if( useScanFlags() )            // Do we care about any of the scannable flags?
            {
                if(isScanFreezePending())
                {
                    resetScanPending();
                    resetScanFreezePending();
                    setScanFreezeFailed();
                    setPrevFreezeTime(getLastFreezeTime());
                    setPrevFreezeNumber(getLastFreezeNumber());
                    setLastFreezeNumber(0);
                    setLastFreezeTime(InMessage->Time + rwEpoch);

                    if(getNextScan(ScanRateGeneral) - getScanRate(ScanRateGeneral) <= TimeNow)
                    {
                        /* this device was due for regular scan so scan */
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << TimeNow << " Forced Scan sent to Remote: " << getAddress() << endl;
                        }

                        resetScanException();                  // This is not an exception scan request...
                        initiateGeneralScan(outList, MAXPRIORITY - 4);
                    }
                }
                else if(isScanPending())
                {
                    resetScanPending();
                    // FIX FIX FIX 090899 CGP ???? DeviceRecord.LastFullScan = InMessage.Time;

                    /* Check if we need to plug accumulators */
                    if(isScanFreezeFailed() || isScanFrozen())
                    {
                        resetScanFreezeFailed();
                        resetScanFrozen();
                        setLastFreezeNumber(0);
                        DoAccums = TRUE;
                    }
                    else
                    {
                        DoAccums = FALSE;
                    }

                    ErrorDecode(InMessage, TimeNow, vgList, retList, outList);

                    // Add this to dev_lcu later CGP ..... 090899
                    /* Check if we need to issue a reset */
                    if(DoAccums && isLCU(getType()))
                    {
#if 1
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
#else
                        if((i = LCUReset (&RemoteRecord, &DeviceRecord, 0, MAXPRIORITY - 4)) != NORMAL)
                        {
                            /* Send Error to logger */
                            ReportError (&RemoteRecord, (USHORT)i);
                        }
                        else
                        {
                            setScanResetting();
                        }
#endif
                    }
                }
                else if(isScanResetting())
                {
                    resetScanResetting();
                    setScanResetFailed();
                }
            }
        }
    }
    else
    {
        //  This comm status wasn't handled by portfield (didn't talk directly to this device - targetid != deviceid),
        //    so we have to send it here
        if( InMessage->DeviceID != InMessage->TargetID )
        {
            CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg( getID(), InMessage->Return.CommandStr, "", nRet, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt, InMessage->Return.TrxID, InMessage->Return.UserID, InMessage->Return.SOE, RWOrdered());

            //  Log the communication success on this route.
            if( commPoint = getDevicePointOffsetTypeEqual(COMM_FAIL_OFFSET, StatusPointType) )
            {
                if( retMsg != NULL )
                {
                    commStatus = CTIDBG_new CtiPointDataMsg(commPoint->getPointID(), 0.0, NormalQuality, StatusPointType, "", TAG_POINT_MAY_BE_EXEMPTED);

                    if( commStatus != NULL )
                    {
                        retMsg->PointData().insert(commStatus);
                        commStatus = NULL;
                    }

                    vgList.append(retMsg);
                    retMsg = 0;
                }
            }

            if(retMsg)
            {
                delete retMsg;
            }
        }
    }

    return nRet;
}


INT CtiDeviceSingle::doDeviceInit(void)
{
    INT nRet = 0;
    INT i;

    // RefreshDevicePoints();                         // Get attached points now.

    RWTime   Now;

    for(i = 0; i < ScanRateInvalid; i++)
    {
        setNextScan(i, firstScan(Now, i));                 // Set them up...
    }

    return nRet;
}


BOOL     CtiDeviceSingle::isScanStarting() const
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData()->isScanStarting();

    return bRet;
}
BOOL     CtiDeviceSingle::setScanStarting(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanStarting(b);

    return bRet;
}
BOOL     CtiDeviceSingle::resetScanStarting(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanStarting(b);

    return bRet;
}

BOOL     CtiDeviceSingle::isScanIntegrity() const
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData()->isScanIntegrity();

    return bRet;
}
BOOL     CtiDeviceSingle::setScanIntegrity(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanIntegrity(b);

    return bRet;
}
BOOL     CtiDeviceSingle::resetScanIntegrity(BOOL b)
{
    return setScanIntegrity(b);
}

BOOL     CtiDeviceSingle::isScanFrozen() const
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData()->isScanFrozen();

    return bRet;
}
BOOL     CtiDeviceSingle::setScanFrozen(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanFrozen(b);

    return bRet;
}
BOOL     CtiDeviceSingle::resetScanFrozen(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanFrozen(b);

    return bRet;
}

BOOL     CtiDeviceSingle::isScanFreezePending() const
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData()->isScanFreezePending();

    return bRet;
}
BOOL     CtiDeviceSingle::setScanFreezePending(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanFreezePending(b);

    return bRet;
}
BOOL     CtiDeviceSingle::resetScanFreezePending(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanFreezePending(b);

    return bRet;
}

BOOL     CtiDeviceSingle::isScanPending() const
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData()->isScanPending();

    return bRet;
}
BOOL     CtiDeviceSingle::setScanPending(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanPending(b);

    return bRet;
}
BOOL     CtiDeviceSingle::resetScanPending(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanPending(b);

    return bRet;
}

BOOL     CtiDeviceSingle::isScanFreezeFailed() const
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData()->isScanFreezeFailed();

    return bRet;
}
BOOL     CtiDeviceSingle::setScanFreezeFailed(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanFreezeFailed(b);

    return bRet;
}
BOOL     CtiDeviceSingle::resetScanFreezeFailed(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanFreezeFailed(b);

    return bRet;
}

BOOL     CtiDeviceSingle::isScanResetting() const
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData()->isScanResetting();

    return bRet;
}
BOOL     CtiDeviceSingle::setScanResetting(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanResetting(b);

    return bRet;
}
BOOL     CtiDeviceSingle::resetScanResetting(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanResetting(b);

    return bRet;
}

BOOL     CtiDeviceSingle::isScanResetFailed() const
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData()->isScanResetFailed();

    return bRet;
}
BOOL     CtiDeviceSingle::setScanResetFailed(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanResetFailed(b);

    return bRet;
}
BOOL     CtiDeviceSingle::resetScanResetFailed(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanResetFailed(b);

    return bRet;
}

BOOL     CtiDeviceSingle::isScanForced() const
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData()->isScanForced();

    return bRet;
}
BOOL     CtiDeviceSingle::setScanForced(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanForced(b);

    return bRet;
}
BOOL     CtiDeviceSingle::resetScanForced(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanForced(b);

    return bRet;
}

BOOL     CtiDeviceSingle::isScanException() const
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData()->isScanException();

    return bRet;
}
BOOL     CtiDeviceSingle::setScanException(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanException(b);

    return bRet;
}
BOOL     CtiDeviceSingle::resetScanException(BOOL b)
{
    BOOL bRet = FALSE;
    if(useScanFlags())
        bRet = getScanData().setScanException(b);

    return bRet;
}


LONG  CtiDeviceSingle::getLastFreezeNumber() const
{
    return getScanData()->getLastFreezeNumber();
}
LONG& CtiDeviceSingle::getLastFreezeNumber()
{
    return getScanData().getLastFreezeNumber();
}
CtiDeviceSingle& CtiDeviceSingle::setLastFreezeNumber( const LONG aLastFreezeNumber )
{
    getScanData().setLastFreezeNumber(aLastFreezeNumber);
    return *this;
}

LONG  CtiDeviceSingle::getPrevFreezeNumber() const
{
    return getScanData()->getPrevFreezeNumber();
}
LONG& CtiDeviceSingle::getPrevFreezeNumber()
{
    return getScanData().getPrevFreezeNumber();
}
CtiDeviceSingle& CtiDeviceSingle::setPrevFreezeNumber( const LONG aPrevFreezeNumber )
{
    getScanData().setPrevFreezeNumber(aPrevFreezeNumber);
    return *this;
}

RWTime  CtiDeviceSingle::getLastFreezeTime() const
{
    return getScanData()->getLastFreezeTime();
}
CtiDeviceSingle& CtiDeviceSingle::setLastFreezeTime( const RWTime& aLastFreezeTime )
{
    getScanData().setLastFreezeTime(aLastFreezeTime);
    return *this;
}

RWTime  CtiDeviceSingle::getPrevFreezeTime() const
{
    return getScanData()->getPrevFreezeTime();
}
CtiDeviceSingle& CtiDeviceSingle::setPrevFreezeTime( const RWTime& aPrevFreezeTime )
{
    getScanData().setPrevFreezeTime(aPrevFreezeTime);
    return *this;
}

RWTime  CtiDeviceSingle::getLastLPTime()
{
    static bool bDone = false;

    RWTime rt;

    rt += (24 * 60 * 60);  //  default to tomorrow, i.e., no LP data returned

    if(useScanFlags())
    {
        if(!bDone)
        {
            RWTime dispatchTime = peekDispatchTime();

            if(getScanData().getLastLPTime() > dispatchTime)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Moving Last LP Time (" << getScanData().getLastLPTime() << ") back to " << dispatchTime << endl;
                }
                setLastLPTime(dispatchTime);
            }

            bDone = true;
        }
        rt = getScanData().getLastLPTime();
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return rt;
}
CtiDeviceSingle& CtiDeviceSingle::setLastLPTime( const RWTime& aTime )
{
    if(useScanFlags())
    {
        LockGuard guard(monitor());
        getScanData().setLastLPTime(aTime);
    }
    return *this;
}


RWTime CtiDeviceSingle::getNextScan(INT a)
{
    LockGuard guard(monitor());
    return getScanData().getNextScan(a);
}
CtiDeviceSingle& CtiDeviceSingle::setNextScan(INT a, const RWTime &b)
{
    LockGuard guard(monitor());

    scheduleSignaledAlternateScan(a);
    getScanData().setNextScan(a, b);
    return *this;
}

RWTime CtiDeviceSingle::nextRemoteScan() const
{
    RWTime nt = getScanData()->nextNearestTime(ScanRateLoadProfile);

    if(getDebugLevel() & 0x00100000)
    {
        if(nt < RWTime(YUKONEOT))
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << left << setw(30) << getName() << right << "'s next scan is to occur at " << nt << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << left << setw(30) << getName() << right << " is pending completion. " << endl;
        }
    }

    return nt;
}

void CtiDeviceSingle::invalidateScanRates()
{
    LockGuard guard(monitor());

    for(int i = 0; i < ScanRateInvalid; i++)
    {
        if(_scanRateTbl[i] != NULL)
        {
            _scanRateTbl[i]->setUpdated(FALSE);
        }
    }

    return;
}

void CtiDeviceSingle::deleteNonUpdatedScanRates()
{
    LockGuard guard(monitor());

    for(int i = 0; i < ScanRateInvalid; i++)
    {
        if(_scanRateTbl[i] != NULL)
        {
            if(_scanRateTbl[i]->getUpdated() == FALSE)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << getName() << " deleting scanrate. Scantype " << i << endl;
                }

                delete _scanRateTbl[i];
                _scanRateTbl[i] = NULL;

                setNextScan(i, RWTime(YUKONEOT));
            }
        }
    }

    return;
}

bool CtiDeviceSingle::clearedForScan(int scantype)
{
    bool status = false;

    switch(scantype)
    {
    case ScanRateGeneral:
        {
            status = (!isScanFreezePending() && !isScanPending() && !isScanResetting()) || ( isScanForced() );
            break;
        }
    case ScanRateIntegrity:
        {
            status = (!isScanFreezePending() && !isScanResetting());
            break;
        }
    case ScanRateAccum:
        {
            status = (!isScanFreezePending() && !isScanResetting());
            break;
        }
    }

    status = validatePendingStatus(status, scantype);

    return status;
}

void CtiDeviceSingle::resetForScan(int scantype)
{
    switch(scantype)
    {
    case ScanRateGeneral:
    case ScanRateAccum:
        {
            if(isScanFreezePending())
            {
                resetScanFreezePending();
                setScanFreezeFailed();
            }

            if(isScanPending())
            {
                resetScanPending();
            }

            if(isScanResetting())
            {
                resetScanResetting();
                setScanResetFailed();
            }
            break;
        }
    }
}

CtiDeviceSingle::CtiDeviceSingle() :
_useScanFlags(0),
_scanData(NULL)
{
    int i;

    LockGuard guard(monitor());

    #ifdef CTIOLDSTATS
    for(i = 0; i < StatTypeInvalid; i++)
    {
        _statistics[i] = NULL;
    }
    #endif

    for(i = 0; i < ScanRateInvalid; i++)
    {
        _scanRateTbl[i] = NULL;
    }
}

CtiDeviceSingle::CtiDeviceSingle(const CtiDeviceSingle& aRef) :
_scanData(NULL)
{
    int i;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** This is expensive. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    LockGuard guard(monitor());

    #ifdef CTIOLDSTATS
    for(i = 0; i < StatTypeInvalid; i++)
    {
        _statistics[i] = NULL;
    }
    #endif

    for(i = 0; i < ScanRateInvalid; i++)
    {
        _scanRateTbl[i] = NULL;
    }


    *this = aRef;
}

CtiDeviceSingle::~CtiDeviceSingle()
{
    int i;

    LockGuard guard(monitor());

    #ifdef CTIOLDSTATS
    for(i = 0; i < StatTypeInvalid; i++)
    {
        if(_statistics[i] != NULL)
        {
            delete _statistics[i];
            _statistics[i] = NULL;
        }
    }
    #endif

    for(i = 0; i < ScanRateInvalid; i++)
    {
        if( _scanRateTbl[i] != NULL)
        {
            delete _scanRateTbl[i];
            _scanRateTbl[i] = NULL;
        }
    }

    if(_scanData != NULL)
    {
        if(_scanData->isDirty())
        {
            if( _scanData->Update().errorCode() != RWDBStatus::ok )
            {
                if( _scanData->Insert().errorCode() != RWDBStatus::ok )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Unable to insert or update scandata for device " << this->getName() << endl;
                }
            }
        }
        delete _scanData;
        _scanData = NULL;
    }
    // dump the vector if needed
    _windowVector.erase(_windowVector.begin(), _windowVector.end());
}

CtiDeviceSingle& CtiDeviceSingle::operator=(const CtiDeviceSingle& aRef)
{
    int i;
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        LockGuard guard(monitor());

        _twoWay = aRef.getTwoWay();

#ifdef CTIOLDSTATS
        for(i = 0; i < StatTypeInvalid; i++)
        {
            if(aRef.isStatValid(i))
            {
                if(!_statistics[i])
                    _statistics[i] = CTIDBG_new CtiTableDeviceStatistics;

                if(_statistics[i])    // Make sure we got some.
                    *_statistics[i] = aRef.getStatistics(i);
            }
        }
#endif
        for(i = 0; i < ScanRateInvalid; i++)
        {
            if(aRef.isRateValid(i))
            {
                if(!_scanRateTbl[i])
                    _scanRateTbl[i] = CTIDBG_new CtiTableDeviceScanRate;

                if(_scanRateTbl[i])
                    *_scanRateTbl[i] = aRef.getRateTable(i);
            }
        }

        if( _scanData != NULL )
        {
            delete _scanData;       // Destructor stores it if it is dirty
            _scanData = NULL;
        }

        if(aRef.isScanDataValid())
        {
            _scanData = CTIDBG_new CtiTableDeviceScanData( (const)aRef.getScanData());
        }
    }
    return *this;
}

CtiTableDevice2Way    CtiDeviceSingle::getTwoWay() const
{
    return _twoWay;
}
CtiTableDevice2Way&   CtiDeviceSingle::getTwoWay()
{
    LockGuard guard(monitor());
    return _twoWay;
}

CtiDeviceSingle& CtiDeviceSingle::setTwoWay( const CtiTableDevice2Way & aTwoWay )
{
    LockGuard guard(monitor());
    _twoWay = aTwoWay;
    return *this;
}

#ifdef CTIOLDSTATS
BOOL CtiDeviceSingle::isStatValid(const INT stat) const
{
    BOOL status = FALSE;
    if(_statistics[stat] != NULL)
    {
        status = TRUE;
    }

    return status;
}

CtiTableDeviceStatistics CtiDeviceSingle::getStatistics(const INT i) const
{
    return *_statistics[i];
}
CtiTableDeviceStatistics&  CtiDeviceSingle::getStatistics(const INT i)
{
    LockGuard guard(monitor());
    return *_statistics[i];
}
CtiDeviceSingle&  CtiDeviceSingle::setStatistics(const INT i, CtiTableDeviceStatistics *aStatistics )
{
    LockGuard guard(monitor());

    if(_statistics[i])
    {
        delete _statistics[i];
        _statistics[i] = NULL;
    }

    _statistics[i] = aStatistics;

    return *this;
}
#endif

BOOL CtiDeviceSingle::isRateValid(const INT i) const
{

    BOOL status = FALSE;

    if(_scanRateTbl[i] != NULL)
    {
        status = TRUE;
    }

    return status;
}

LONG CtiDeviceSingle::getScanRate(int rate) const
{
    LockGuard guard(monitor());

    if(rate >= 0 && rate < ScanRateInvalid && _scanRateTbl[rate] != NULL)
    {
        bool bScanIsScheduled;
        if(isAlternateRateActive(bScanIsScheduled, RWTime(), rate))
        {
            // FirstScanInSignaledAlternateRate scan GOES NOW, once scheduled we report the normal rate.
            INT altrate = bScanIsScheduled ? _scanRateTbl[rate]->getAlternateRate() : 1L;
#if 0
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << getName() << " is using an altrate of " << altrate << endl;
            }
#endif
            return altrate;
        }
        else
        {
            return _scanRateTbl[rate]->getScanRate();
        }
    }
    else
    {
        return -1L;
    }
}

void CtiDeviceSingle::setScanRate(int a, LONG b)
{
    LockGuard guard(monitor());
    if(_scanRateTbl[a] != NULL)
    {
        _scanRateTbl[a]->setScanRate(b);
    }
}

CtiTableDeviceScanRate  CtiDeviceSingle::getRateTable(const INT i) const
{
    return *(_scanRateTbl[i]);
}
CtiTableDeviceScanRate& CtiDeviceSingle::getRateTable(const INT i)
{
    LockGuard guard(monitor());

    return(*(_scanRateTbl[i]));
}
CtiDeviceSingle&     CtiDeviceSingle::setRateTables(const INT i, const CtiTableDeviceScanRate* aScanRate)
{
    LockGuard guard(monitor());

    if(_scanRateTbl[i] == NULL)
    {
        _scanRateTbl[i] = CTIDBG_new CtiTableDeviceScanRate;
    }

    if(_scanRateTbl[i])
        *(_scanRateTbl[i]) = *aScanRate;

    return *this;
}

BOOL CtiDeviceSingle::isScanWindowOpen(RWTime &aNow) const
{
    BOOL status = TRUE;

    // loop the vector
    for(int x=0; x <_windowVector.size(); x++)
    {
        if(_windowVector[x].getType() == DeviceWindowScan)
        {
            RWTime lastMidnight(RWDate());
            RWTime open  (lastMidnight.seconds()+_windowVector[x].getOpen());
            RWTime close (open.seconds()+_windowVector[x].getDuration());

            if(open == close)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** DB Config Error **** " << getName() << " has a zero time scan window defined. "<< endl;
                }
            }
            else if((aNow < open) || (aNow > close))
            {
                status = FALSE;
            }
        }
    }
    return status;
}


void CtiDeviceSingle::checkSignaledAlternateRateForExpiration()
{
    BOOL status = FALSE;

    for(int x=0; x <_windowVector.size(); x++)
    {
        if(_windowVector[x].getType() == DeviceWindowSignaledAlternateRate)
        {
            RWTime lastMidnight(RWDate());
            RWTime open  (lastMidnight.seconds()+_windowVector[x].getOpen());
            RWTime close (open.seconds()+_windowVector[x].getDuration());
            RWTime now;

            // this was a scan once and remove
            if( (open <= now) && ((_windowVector[x].getDuration() == 0) || (now > close)) )
            {
                status = TRUE;
            }

            if(status && _windowVector[x].verifyWindowMatch())
            {
                _windowVector.erase(_windowVector.begin()+x);
                break;
            }
        }
    }
}

// this does a little more than it probably should but for now it will have to do DLS
BOOL CtiDeviceSingle::isAlternateRateActive( bool &bScanIsScheduled, RWTime &aNow, int rate) const
{
    BOOL status = FALSE;

    bScanIsScheduled = false;

    // loop the vector
    for(int x=0; x <_windowVector.size(); x++)
    {
        RWTime lastMidnight(RWDate());
        RWTime open  (lastMidnight.seconds()+_windowVector[x].getOpen());
        RWTime close (open.seconds()+_windowVector[x].getDuration());

        if(_windowVector[x].getType() == DeviceWindowAlternateRate)
        {
            if((open <= aNow) && (close > aNow))
            {
                status = TRUE;
                break;
            }
        }
        else if(_windowVector[x].getType() == DeviceWindowSignaledAlternateRate)
        {
            /*********************************
            * we have an alternate rate from the outside somewhere
            ***********************************/
            if((open <= aNow) && ( (close > aNow) || (_windowVector[x].getDuration() == 0)) )
            {
                _windowVector[x].addSignaledRateActive( rate );
                bScanIsScheduled = _windowVector[x].isSignaledRateScheduled(rate);

                status = TRUE;
                break;
            }
        }
    }
    return status;
}

RWTime CtiDeviceSingle::getNextWindowOpen() const
{
    RWTime now;
    RWTime lastMidnight(RWDate());
    RWTime windowOpens = RWTime(YUKONEOT);

    try
    {
        // loop the vector
        for(int x=0; x <_windowVector.size(); x++)
        {
            RWTime open (lastMidnight.seconds()+_windowVector[x].getOpen());

            if(now <= open && open < windowOpens)
            {
                windowOpens = open;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return windowOpens;
}

void CtiDeviceSingle::applySignaledRateChange(LONG aOpen, LONG aDuration)
{
    bool found=false;

    RWTime now;
    RWTime lastMidnight(RWDate());

    if(aOpen == -1 || aDuration == 0 || (lastMidnight.seconds()+aOpen+aDuration > now.seconds()))
    {
        for(int x=0; x <_windowVector.size(); x++)
        {
            if(_windowVector[x].getType() == DeviceWindowSignaledAlternateRate)
            {
                found = true;

                if(aOpen == -1)
                {
                    _windowVector[x].setOpen (now.seconds() - lastMidnight.seconds());
                    _windowVector[x].setAlternateOpen (now.seconds() - lastMidnight.seconds());
                }
                else
                {
                    _windowVector[x].setOpen (aOpen);
                    _windowVector[x].setAlternateOpen (aOpen);
                }

                _windowVector[x].setDuration (aDuration);
                _windowVector[x].setAlternateDuration (aDuration);
            }
        }

        if(!found)
        {
            CtiTableDeviceWindow newWindow;
            newWindow.setID (getID());
            newWindow.setType (DeviceWindowSignaledAlternateRate);
            newWindow.setDuration (aDuration);
            newWindow.setAlternateDuration (aDuration);

            if(aOpen == -1)
            {
                newWindow.setOpen (now.seconds() - lastMidnight.seconds());
                newWindow.setAlternateOpen (now.seconds() - lastMidnight.seconds());
            }
            else
            {
                newWindow.setOpen (aOpen);
                newWindow.setAlternateOpen (aOpen);
            }
            _windowVector.push_back (newWindow);
        }

        if(aOpen == -1)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << now << " " << getName() << " changing to alternate scan rate starting now for " << aDuration << " seconds" << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << now << " " << getName() << " changing to alternate scan rate starting " << RWTime(lastMidnight.seconds()+aOpen) << " for " << aDuration << " seconds" << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << now << " Signaled alternate scan rate stop time has already passed for " << getName() << " " << RWTime(lastMidnight.seconds()+aOpen+aDuration) << endl;
    }
}

void CtiDeviceSingle::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableDevice2Way::getSQL(db, keyTable, selector);
}

void CtiDeviceSingle::DecodeDatabaseReader(RWDBReader &rdr)
{

    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    LockGuard guard(monitor());
    if(getDebugLevel() & 0x0800)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    _twoWay.DecodeDatabaseReader(rdr);
}

#ifdef CTIOLDSTATS

void CtiDeviceSingle::DecodeStatisticsDatabaseReader(RWDBReader &rdr)
{
    LockGuard guard(monitor());

    RWCString rwsTemp;

    if(getDebugLevel() & 0x0800)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["statistictype"] >> rwsTemp;

    INT i = resolveStatisticsType(rwsTemp);

    if(i < StatTypeInvalid)
    {
        if(!_statistics[i])
        {
            _statistics[i] = CTIDBG_new CtiTableDeviceStatistics;
        }

        if(_statistics[i])
            _statistics[i]->DecodeDatabaseReader(rdr);
    }
}
#endif

void CtiDeviceSingle::DecodeScanRateDatabaseReader(RWDBReader &rdr)
{
    LockGuard guard(monitor());

    RWCString rwsTemp;

    if(getDebugLevel() & 0x0800)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    RWDBNullIndicator isNull;

    rdr["scantype"] >> isNull;

    if(!isNull)
    {
        rdr["scantype"] >> rwsTemp;
        INT i = resolveScanType(rwsTemp);

        if(i < ScanRateInvalid)
        {
            if(_scanRateTbl[i] == NULL)
            {
                _scanRateTbl[i] = CTIDBG_new CtiTableDeviceScanRate;
            }

            if(_scanRateTbl[i])
                _scanRateTbl[i]->DecodeDatabaseReader(rdr);
        }
    }
}
void CtiDeviceSingle::DecodeDeviceWindowDatabaseReader(RWDBReader &rdr)
{
    LockGuard guard(monitor());

    if(getDebugLevel() & 0x0800)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Decoding device windows " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    RWDBNullIndicator isNull;
    rdr["type"] >> isNull;        // 'windowtype' is the alias used for devicewindow.type to avoid a collision with yukonpaobject.type.

    if(!isNull)
    {
        CtiTableDeviceWindow newWindow;
        newWindow.DecodeDatabaseReader(rdr);

        /*************************
        * if open and close were equal, duration of the window will be 0
        *
        * sounds like a config problem to me, make the window always open
        **************************
        */
        if(newWindow.getDuration() == 0)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << getName() << "'s device window is invalid, open and close times are equal " << endl;
            }
        }
        else
        {
            _windowVector.push_back (newWindow);
        }
    }
}


void CtiDeviceSingle::DumpData()
{
    int i;
    Inherited::DumpData();

    LockGuard guard(monitor());

    #ifdef CTIOLDSTATS
    for(i = 0; i < StatTypeInvalid; i++)
    {
        if(_statistics[i])    // Make sure we got some.
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Stats Record                                : " << i  << endl;
            }
            _statistics[i]->DumpData();
        }
    }
    #endif

    for(i = 0; i < ScanRateInvalid; i++)
    {
        if(_scanRateTbl[i])
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Scan Rate Record                            : " << i  << endl;
            }
            _scanRateTbl[i]->DumpData();
        }
    }
}

/*
 *  Things which make me into a scannable object.
 */
INT  CtiDeviceSingle::resetScanFlags(INT i)
{
    INT flags = 0;

    if(useScanFlags())
        flags = getScanData().resetScanFlags(i);

    return flags;
}
BOOL CtiDeviceSingle::useScanFlags() const
{
    return _useScanFlags;
}

CtiTableDeviceScanData& CtiDeviceSingle::getScanData()
{
    setUseScanFlags(TRUE);
    validateScanData();

    LockGuard guard(monitor());
    return *_scanData;
}

const CtiTableDeviceScanData* CtiDeviceSingle::getScanData() const
{
    return _scanData;
}

BOOL     CtiDeviceSingle::setUseScanFlags(BOOL b)
{
    LockGuard guard(monitor());
    return _useScanFlags = b;
}
BOOL     CtiDeviceSingle::resetUseScanFlags(BOOL b)
{
    return setUseScanFlags(b);
}

bool CtiDeviceSingle::isScanDataValid() const
{
    return(_scanData != NULL);
}

INT CtiDeviceSingle::validateScanData()
{
    INT status = NORMAL;

    LockGuard guard(monitor());

    if(_scanData == NULL)
    {
        _scanData = CTIDBG_new CtiTableDeviceScanData( getID() );
        if(_scanData != NULL)
        {
            if( !(_scanData->Restore().errorCode() == RWDBStatus::ok ))
            {
                if( !(_scanData->Insert().errorCode() == RWDBStatus::ok ))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }
        }
        else
        {
            status = MEMORY;
        }
    }
    return status;
}

/*
 *  Look for the oldest point in the archive which is greater than the default date...
 */
RWTime CtiDeviceSingle::peekDispatchTime() const
{
    RWTime dispatchTime;

    if(_pointMgr != NULL)
    {
        CtiTablePointDispatch ptdefault(0);
        CtiTablePointDispatch pd;
        CtiPointBase *pPoint;
        CtiPointManager::CtiRTDBIterator itr( _pointMgr->getMap() );

        for(;itr();)
        {
            pPoint = itr.value();

            if(pPoint != NULL)
            {
                RWRecursiveLock<RWMutexLock>::LockGuard ptguard(pPoint->getMux());

                pd.setPointID( pPoint->getPointID() );
                RWDBStatus dbstat = pd.Restore();

                if(dbstat.errorCode() == RWDBStatus::ok)
                {
                    if(pd.getTimeStamp() > ptdefault.getTimeStamp() && pd.getTimeStamp().rwtime() < dispatchTime)
                    {
                        dispatchTime = pd.getTimeStamp().rwtime();
                    }
                }
            }
        }

        if(getDebugLevel() & 0x00000001)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "   Oldest dispatch write time is " << dispatchTime << endl;
        }
    }

    return dispatchTime;
}

bool CtiDeviceSingle::processAdditionalRoutes( INMESS *InMessage ) const
{
    return false;
}


bool CtiDeviceSingle::validatePendingStatus(bool status, int scantype, RWTime &now)
{
    if(!status)     // In this case we need to make sure we have not gone tardy on this scan type
    {
        if(getScanData().getLastCommunicationTime(scantype) + getTardyTime(scantype) < now )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << getName() << "'s pending flags reset due to timeout on prior scan" << endl;
            }
            resetForScan(scantype);
            getScanData().setLastCommunicationTime(scantype, now);
            status = true;
        }
    }
    else            // We are about to submit a scan of some form for the device.  Mark out the time!
    {
        getScanData().setLastCommunicationTime(scantype, now);
    }

    return status;
}


ULONG CtiDeviceSingle::getTardyTime(int scantype) const
{
    ULONG maxtardytime = getScanRate(scantype);

    if(maxtardytime < 60)
    {
        maxtardytime = 60;
    }
    else
    {
        switch(scantype)
        {
        case ScanRateGeneral:
        case ScanRateIntegrity:
            {
                maxtardytime = maxtardytime * 2 + 1;
                break;
            }
        case ScanRateAccum:
            {
                maxtardytime = maxtardytime + maxtardytime / 2;
                break;
            }
        }
    }

    if(maxtardytime > 7200)
    {
        maxtardytime = 7200;
    }

    return maxtardytime;
}

bool CtiDeviceSingle::hasLongScanRate(const RWCString &cmd) const
{
    bool bret = false;

    if( useScanFlags() )
    {
        // First decide what scan rate we are.
        INT scanratetype = ScanRateInvalid;

        if(cmd.contains(" general") || cmd.contains(" status"))
        {
            scanratetype = ScanRateGeneral;
        }
        else if(cmd.contains(" integrity"))
        {
            scanratetype = ScanRateIntegrity;
        }
        else if(cmd.contains(" accumulator"))
        {
            scanratetype = ScanRateAccum;
        }
        else if(cmd.contains(" loadprofile"))
        {
            //  not applicable
            scanratetype = ScanRateInvalid;
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " Cmd: " << cmd << endl;
            }
        }

        if(getScanRate(scanratetype) > 3600)
        {
            bret = true;
        }
    }

    return bret;
}


bool CtiDeviceSingle::hasRateOrClockChanged(int rate, RWTime &Now)
{
    RWTime previousScan;
    bool bstatus = false;

    LONG scanrate = getScanRate(rate);

    /*
     *  This is a computation of the "expected" previous scan time based upon the current scan rate.
     *  It should give an indication if the scan rate or clock has been altered in a manner that is significant.
     *  The Now < previousScan will ensure that we scan at the smaller of the current or the previous rate
     *  and then proceed to align to the current rate.
     */
    if(rate == ScanRateGeneral)
    {
        previousScan = getNextScan(rate) - scanrate - scanrate;
    }
    else
    {
        previousScan = getNextScan(rate) - scanrate;
    }

    if(Now < previousScan)
    {
        bstatus = true;
    }

    return bstatus;
}

BOOL CtiDeviceSingle::scheduleSignaledAlternateScan( int rate ) const
{
    BOOL status = FALSE;
    RWTime now;
    RWTime lastMidnight(RWDate());

    // loop the vector
    for(int x=0; x <_windowVector.size(); x++)
    {
        if(_windowVector[x].getType() == DeviceWindowSignaledAlternateRate)
        {
            RWTime open  (lastMidnight.seconds()+_windowVector[x].getOpen());
            RWTime close (open.seconds()+_windowVector[x].getDuration());

            /*********************************
            * we have an alternate rate from the outside somewhere
            ***********************************/
            if((open <= now) && ( (close > now) || (_windowVector[x].getDuration() == 0)) )
            {
                _windowVector[x].addSignaledRateSent( rate );
                status = TRUE;
                break;
            }
        }
    }
    return status;
}

