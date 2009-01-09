/*-----------------------------------------------------------------------------*
*
* File:   dev_single
*
* Date:   10/4/2001
*
* PVCS KEYWORDS:
* REVISION     :  $Revision: 1.71 $
* DATE         :  $Date: 2008/10/30 15:44:16 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <vector>

#include "cparms.h"
#include "dev_single.h"
#include "logger.h"
#include "porter.h"
#include "numstr.h"
#include "tbl_ptdispatch.h"
#include "utility.h"
#include "ctidate.h"
#include "ctitime.h"
#include "scanglob.h"

// see bottom of file
bool isDeviceAddressGlobal(const int deviceType, const int address);

using namespace std;


using namespace Cti;  //  in preparation for moving devices to their own namespace


CtiTime CtiDeviceSingle::adjustNextScanTime(const INT scanType)
{
    CtiTime    Now;
    CtiTime    When(YUKONEOT);
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
                    dout << CtiTime() << " **** Checkpoint - infinite loop averted in adjustNextScanTime **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


CtiTime CtiDeviceSingle::firstScan( const CtiTime &When, INT rate )
{
    CtiTime first;

    if( getScanRate(rate) > 3600 )
    {
        CtiTime hourstart = CtiTime(When.seconds() - (When.seconds() % 3600)); // align to the hour.
        first = CtiTime(hourstart.seconds() - ((hourstart.hour()* 3600) % getScanRate(rate)) + getScanRate(rate));
    }
    else if(getScanRate(rate) > 0 )    // Prevent a divide by zero with this check...
    {
        first = CtiTime(When.seconds() - (When.seconds() % getScanRate(rate)) + getScanRate(rate));
    }
    else if(getScanRate(rate) == 0)
    {
        first = When.now();
    }
    else
    {
        first = CtiTime(YUKONEOT);
    }

    while(first <= When)
    {
        first += getScanRate(rate);
    }

    return first;
}

void CtiDeviceSingle::validateScanTimes(bool force)
{
    CtiTime Now;

    LockGuard guard(monitor());

    for(int rate = 0; rate < ScanRateInvalid; rate++)
    {
        /*
         *  Make sure we have not gone tardy.. nextScheduledScan is used to make sure we are within 1 (or 2)
         *  scan intervals of the expected next time...
         */

        LONG scanrate = getScanRate(rate);

        if( scanrate >= 0 && isWindowOpen() )
        {
            bool scanChanged = hasRateOrClockChanged(rate, Now);

            if( force == true || scanChanged )
            {
                setNextScan(rate, firstScan(Now, rate));

                if(rate == ScanRateAccum && scanChanged)
                {
                    setLastFreezeNumber(0L);
                    setPrevFreezeNumber(0L);
                    setLastFreezeTime(CtiTime());
                    setPrevFreezeTime(CtiTime());
                }
            }
            else if(isScanFlagSet(rate) || isScanFlagSet(ScanFreezePending))
            {
                // if we're pending, do the calculation anyway
                setNextScan(rate, firstScan(Now, rate));
            }
        }
    }
}

INT CtiDeviceSingle::initiateAccumulatorScan(list< OUTMESS* > &outList, INT ScanPriority)
{
    INT      nRet = 0;
    CtiTime   Now;
    OUTMESS  *OutMessage = CTIDBG_new OUTMESS;
    /*
     *  This method will be called by each accumulator scanning device prior to the
     *  actual device specific code called by that device type.
     */

    list< CtiMessage* > vgList;
    list< CtiMessage* > retList;

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
        else if(!isWindowOpen())
        {
            adjustNextScanTime(ScanRateAccum);
            nRet=SCAN_ERROR_DEVICE_WINDOW_CLOSED;
        }
        else if( clearedForScan(ScanRateAccum) )
        {
            resetScanFlag(ScanForced);            // Reset this guy since we're doing it

            if (isDeviceAddressGlobal(getType(), getAddress()))
            {
                // CAN NOT scan a global address.
                setScanRate(ScanRateAccum, YUKONEOT);    // set him to the end of time!
                getNextScan(ScanRateAccum) = CtiTime(YUKONEOT);
                return SCAN_ERROR_GLOBAL_ADDRESS; // Cannot scan a global address.
            }

            strncpy(OutMessage->Request.CommandStr, "scan accumulator", sizeof(OutMessage->Request.CommandStr));
            OutMessage->Request.CheckSum = getUniqueIdentifier();  // Mark the OUTMESS with this DEVICE's CRC Id.

            // Do the device's AccumulatorScan!
            nRet = AccumulatorScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority);
            OutMessage = NULL;      // Memory may be forgotten

            if(nRet && nRet != ACCUMSNOTSUPPORTED)
            {
                setScanFlag(ScanFreezeFailed);
            }
        }
        else
        {
            if( SCANNER_DEBUG_ACCUMSCAN & gConfigParms.getValueAsULong("SCANNER_DEBUGLEVEL",0,16) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Accumulator Scan aborted due to scan in progress, device \"" << getName() << "\"" << endl;
            }

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
        if(!isScanFlagSet(ScanForced))
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
        setNextScan(ScanRateAccum, CtiTime(YUKONEOT));
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


INT CtiDeviceSingle::initiateIntegrityScan(list< OUTMESS* > &outList, INT ScanPriority)
{
    INT      nRet = 0;
    CtiTime   Now;
    OUTMESS  *OutMessage = CTIDBG_new OUTMESS;
    /*
     *  This method will be called by each accumulator scanning device prior to the
     *  actual device specific code called by that device type.
     */

    list< CtiMessage* > vgList;
    list< CtiMessage* > retList;

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
        strncpy(OutMessage->Request.CommandStr, "scan integrity", sizeof(OutMessage->Request.CommandStr));
        OutMessage->Request.CheckSum = getUniqueIdentifier();  // Mark the OUTMESS with this DEVICE's CRC Id.

        if(isInhibited())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Integrity Scan aborted due to inhibited device: " << getName() << endl;
            }

            nRet =  SCAN_ERROR_DEVICE_INHIBITED;

        }
        else if(!isWindowOpen())
        {
            //          {
            //             CtiLockGuard<CtiLogger> doubt_guard(dout);
            //             dout << CtiTime() << " " << getName() << "'s scan window is closed " << endl;
            //          }
            nRet=SCAN_ERROR_DEVICE_WINDOW_CLOSED;
        }
        else // if(isInhibited()) ... so it isn't inhibited
        {
            if( clearedForScan(ScanRateIntegrity) )
            {
                resetScanFlag(ScanForced);            // Reset this guy since we're doing it

                if (isDeviceAddressGlobal(getType(), getAddress()))
                {
                    // CAN NOT scan a global address.
                    setScanRate(ScanRateIntegrity, YUKONEOT);    // set him to the end of time!
                    setNextScan(ScanRateIntegrity, CtiTime(YUKONEOT));

                    return SCAN_ERROR_GLOBAL_ADDRESS; // Cannot scan a global address.
                }

#if 0
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Integrity Scan. Port: " << getPortID() << " Device: " << getID() << ": " << getName() << endl;
                }
#endif

                // Do the devices integrity scan!
                nRet = IntegrityScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority);
                OutMessage = NULL;      // Memory may be forgotten

                if(nRet)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Integrity Scan error " << nRet << endl;
                }
                else
                {
                    setScanFlag(ScanRateIntegrity);
                }

            }
            else
            {
                if(  SCANNER_DEBUG_INTEGRITYSCAN & gConfigParms.getValueAsULong("SCANNER_DEBUGLEVEL",0,16) )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Integrity Scan aborted due to scan in progress, device \"" << getName() << "\"" << endl;
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


INT CtiDeviceSingle::initiateGeneralScan(list< OUTMESS* > &outList, INT ScanPriority)
{
    INT      nRet = 0;
    CtiTime   Now;
    OUTMESS  *OutMessage = CTIDBG_new OUTMESS;

    list< CtiMessage* > vgList;
    list< CtiMessage* > retList;

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
        strncpy(OutMessage->Request.CommandStr, "scan general", sizeof(OutMessage->Request.CommandStr));
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
            else if(!isWindowOpen())
            {
                adjustNextScanTime(ScanRateGeneral);
                nRet=SCAN_ERROR_DEVICE_WINDOW_CLOSED;
            }
            else // if(isInhibited()) ... so it isn't inhibited
            {
                if( clearedForScan(ScanRateGeneral) )
                {
                    resetScanFlag(ScanForced);            // Reset this guy since we're doing it

                    if (isDeviceAddressGlobal(getType(), getAddress()))
                    {
                        // CANNOT scan a global address.
                        setScanRate(ScanRateAccum, YUKONEOT);    // set him to the end of time!
                        setNextScan(ScanRateAccum, CtiTime(YUKONEOT));

                        return SCAN_ERROR_GLOBAL_ADDRESS; // Cannot scan a global address.
                    }

                    // Do the devices general scan!
                    nRet = GeneralScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority);

                    if( !vgList.empty() || !retList.empty() )
                    {
                        delete_container( vgList  );
                        delete_container( retList );
                        vgList.clear();
                        retList.clear();
                    }

                    OutMessage = NULL;      // Memory may be forgotten

                    if( !nRet )
                    {
                        setScanFlag(ScanRateGeneral);
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
                    if(  SCANNER_DEBUG_GENERALSCAN & gConfigParms.getValueAsULong("SCANNER_DEBUGLEVEL",0,16) )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " General Scan aborted due to scan in progress, device \"" << getName() << "\"" << endl;
                    }

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
    if(!isScanFlagSet(ScanForced) && outList.size() > 0)
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


INT CtiDeviceSingle::initiateLoadProfileScan(list< OUTMESS* > &outList, INT ScanPriority)
{
    INT      nRet = 0;
    CtiTime   Now;
    OUTMESS  *OutMessage = CTIDBG_new OUTMESS;
    /*
     *  This method will be called by each load profile device prior to the
     *  actual device specific code called by that device type.
     */

    list< CtiMessage* > vgList;
    list< CtiMessage* > retList;

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
        strncpy(OutMessage->Request.CommandStr, "scan loadprofile", sizeof(OutMessage->Request.CommandStr));
        OutMessage->Request.CheckSum = getUniqueIdentifier();  // Mark the OUTMESS with this DEVICE's CRC Id.

        if(isInhibited())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Load Profile Scan aborted due to inhibited device: " << getName() << endl;
            }

            nRet =  SCAN_ERROR_DEVICE_INHIBITED;

        }
        else if(!isWindowOpen())
        {
            //          {
            //             CtiLockGuard<CtiLogger> doubt_guard(dout);
            //             dout << CtiTime() << " " << getName() << "'s scan window is closed " << endl;
            //          }
            nRet = SCAN_ERROR_DEVICE_WINDOW_CLOSED;
        }
        else // if(isInhibited()) ... so it isn't inhibited
        {
            if( clearedForScan(ScanRateLoadProfile) )
            {
                resetScanFlag(ScanForced);            // Reset this guy since we're doing it

                if (isDeviceAddressGlobal(getType(), getAddress()))
                {
                    // CAN NOT scan a global address.
                    setScanRate(ScanRateLoadProfile, YUKONEOT);    // set him to the end of time!
                    setNextScan(ScanRateLoadProfile, CtiTime(YUKONEOT));

                    return SCAN_ERROR_GLOBAL_ADDRESS; // Cannot scan a global address.
                }

#if 0
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Load Profile Scan. Port: " << getPortID() << " Device: " << getID() << ": " << getName() << endl;
                }
#endif

                // Do the devices load profile scan!
                nRet = LoadProfileScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority);
                OutMessage = NULL;      // Memory may be forgotten

                if(nRet)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Load Profile Scan error " << nRet << endl;
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Load Profile Scan aborted due to scan in progress " << endl;
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


inline Protocol::Interface *CtiDeviceSingle::getProtocol()
{
    return 0;
}


int CtiDeviceSingle::generate(CtiXfer &xfer)
{
    int retval = -1;
    Protocol::Interface *prot = getProtocol();

    if( prot )  retval = prot->generate(xfer);

    return retval;
}

int CtiDeviceSingle::decode(CtiXfer &xfer, int status)
{
    int retval = -1;
    Protocol::Interface *prot = getProtocol();

    if( prot )  retval = prot->decode(xfer, status);

    return retval;
}

int CtiDeviceSingle::recvCommRequest(OUTMESS *OutMessage)
{
    int retval = -1;
    Protocol::Interface *prot = getProtocol();

    if( prot )  retval = prot->recvCommRequest(OutMessage);

    return retval;
}

int CtiDeviceSingle::sendCommResult(INMESS *InMessage)
{
    int retval = NOTNORMAL;
    Protocol::Interface *prot = getProtocol();

    if( prot )  retval = prot->sendCommResult(InMessage);

    return retval;
}

bool CtiDeviceSingle::isTransactionComplete(void)
{
    bool retval = true;
    Protocol::Interface *prot = getProtocol();

    if( prot )  retval = prot->isTransactionComplete();

    return retval;
}


void CtiDeviceSingle::sendDispatchResults(CtiConnection &vg_connection)                     { }
void CtiDeviceSingle::getVerificationObjects(queue< CtiVerificationBase * > &work_queue)    { }
void CtiDeviceSingle::getTargetDeviceStatistics(vector< OUTMESS > &om_statistics)           { }


INT CtiDeviceSingle::ProcessResult(INMESS *InMessage,
                                   CtiTime &TimeNow,
                                   list< CtiMessage* >   &vgList,
                                   list< CtiMessage* > &retList,
                                   list< OUTMESS* > &outList)
{
    INT   nRet = InMessage->EventCode & 0x3fff;
    INT   DoAccums;
    INT   status = 0;
    bool  bLastFail = false;

    CtiPointDataMsg *commStatus;
    CtiPointSPtr    commPoint;

    if( !nRet )
    {
        nRet = ResultDecode(InMessage, TimeNow, vgList, retList, outList);
    }

    if( nRet )
    {
        string errStr;
        string CmdStr("Unknown");
        char ErrStr[80];

        if(InMessage->Return.CommandStr[0] != '\0')
        {
            CmdStr = string(InMessage->Return.CommandStr);
            std::transform(CmdStr.begin(), CmdStr.end(), CmdStr.begin(), ::tolower);

        }

        if( processAdditionalRoutes( InMessage ) )                      // InMessage->Return.MacroOffset != 0)
        {
            OUTMESS *OutTemplate = CTIDBG_new OUTMESS;

            InEchoToOut( InMessage, OutTemplate );

            CtiRequestMsg *pReq = CTIDBG_new CtiRequestMsg(InMessage->TargetID, string(InMessage->Return.CommandStr), InMessage->Return.UserID, InMessage->Return.GrpMsgID, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt, 0, InMessage->Priority);

            pReq->setConnectionHandle( InMessage->Return.Connection );

            {
                string msg;
                CtiReturnMsg *Ret = CTIDBG_new CtiReturnMsg( getID(), CmdStr, string("Macro offset ") + CtiNumStr(InMessage->Return.MacroOffset - 1) + string(" failed. Attempting next offset."), nRet, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt, InMessage->Return.GrpMsgID, InMessage->Return.UserID, InMessage->Return.SOE, CtiMultiMsg_vec());

                msg = Ret->ResultString() + "\nError " + CtiNumStr(nRet) + ": " + FormatError(nRet);
                Ret->setResultString( msg );
                Ret->setExpectMore();           // Help MACS know this is intermediate.
                Ret->setStatus( nRet );

                retList.push_back( Ret );
            }

            size_t cnt = outList.size();

            if( 0 != (status = ExecuteRequest(pReq, CtiCommandParser(pReq->CommandString()), vgList, retList, outList, OutTemplate)) )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << CtiTime() << "   Status = " << status << ": " << FormatError(status) << endl;
                }
            }

            // The call to getType here is really asking if this device can have valid executions
            // with no out messages generated. This is a complete hack and should be re-thought. There
            // must be a better way to achieve this. The basic problem is that when load profile
            // is canceled, no message is returned on the final execution and this is NOT an error!
            // The code below overrides the outlist checking for only 410's and 470's. If this is not
            // done, canceling a LP request that is on a macro route is impossible.
            if(cnt == outList.size() && (!(getType() == TYPEMCT410 || getType() == TYPEMCT470) && status != NoError) )
            {
                bLastFail = true;
            }
            else
            {
                // if blastfail is not set, we need to decrement the message we are retrying here.
                decrementGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection);
            }

            delete pReq;

            if(OutTemplate != NULL)
            {
                delete OutTemplate;
                OutTemplate = 0;
            }
        }
        else if( CmdStr.find("scan")!=string::npos && hasLongScanRate( CmdStr ))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
        else
        {
            bLastFail = true;
        }

        if(bLastFail)
        {
            /* something went wrong so start by printing error */
            if( (InMessage->EventCode & ~DECODED) != ErrPortSimulated)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << TimeNow << " Error (" << (InMessage->EventCode & ~DECODED)  << ") to Remote: " << getName() <<": " << GetError(nRet) << endl;
            }

            CtiReturnMsg *Ret = CTIDBG_new CtiReturnMsg(  getID(), CmdStr, FormatError(nRet), nRet, InMessage->Return.RouteID, InMessage->Return.MacroOffset, InMessage->Return.Attempt, InMessage->Return.GrpMsgID, InMessage->Return.UserID, InMessage->Return.SOE, CtiMultiMsg_vec());

            decrementGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection);
            if(getGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection)>0)
            {
                Ret->setExpectMore();
            }

            retList.push_back( Ret );

            bool overrideExpectMore = false;
            ErrorDecode(InMessage, TimeNow, vgList, retList, outList, overrideExpectMore);

            list< CtiMessage* >::iterator iter;
            if( overrideExpectMore )
            {
                for( iter = retList.begin(); iter != retList.end(); iter++ )
                {
                    if( (*iter)->isA() == MSG_PCRETURN )
                    {
                        ((CtiReturnMsg *)(*iter))->setExpectMore(true);
                    }
                }
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

    CtiTime   Now;

    for(i = 0; i < ScanRateInvalid; i++)
    {
        setNextScan(i, firstScan(Now, i));                 // Set them up...
    }

    return nRet;
}

BOOL CtiDeviceSingle::isScanFlagSet(int scantype) const
{
    BOOL val = FALSE;

    try
    {
        if(scantype != -1)
        {
            if(_pending_map.find(scantype) != _pending_map.end())
            {
                val = _pending_map[scantype];                       // The specific pending will be checked if asked for...
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return val;
}
BOOL     CtiDeviceSingle::setScanFlag(int scantype, BOOL b)
{
    BOOL val = b;

    try
    {
        if(scantype != -1)
        {
            _pending_map[scantype] = b; // The specific pending is also set.
        }
        else
        {
            if(b == FALSE)
            {
                _pending_map.clear();   // If we were nonspecific and asked for a clear, we must blank all pending flags.
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return val;
}
BOOL CtiDeviceSingle::resetScanFlag(int scantype)
{
    return setScanFlag(scantype,FALSE);
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

CtiTime  CtiDeviceSingle::getLastFreezeTime() const
{
    return getScanData()->getLastFreezeTime();
}
CtiDeviceSingle& CtiDeviceSingle::setLastFreezeTime( const CtiTime& aLastFreezeTime )
{
    getScanData().setLastFreezeTime(aLastFreezeTime);
    return *this;
}

CtiTime  CtiDeviceSingle::getPrevFreezeTime() const
{
    return getScanData()->getPrevFreezeTime();
}
CtiDeviceSingle& CtiDeviceSingle::setPrevFreezeTime( const CtiTime& aPrevFreezeTime )
{
    getScanData().setPrevFreezeTime(aPrevFreezeTime);
    return *this;
}

CtiTime  CtiDeviceSingle::getLastLPTime()
{
    static bool bDone = false;

    CtiTime rt;

    rt += (24 * 60 * 60);  //  default to tomorrow, i.e., no LP data returned

    if(useScanFlags())
    {
        if(!bDone)
        {
            CtiTime dispatchTime = peekDispatchTime();

            if(getScanData().getLastLPTime() > dispatchTime)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Moving Last LP Time (" << getScanData().getLastLPTime() << ") back to " << dispatchTime << endl;
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
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return rt;
}
CtiDeviceSingle& CtiDeviceSingle::setLastLPTime( const CtiTime& aTime )
{
    if(useScanFlags())
    {
        LockGuard guard(monitor());
        getScanData().setLastLPTime(aTime);
    }
    return *this;
}


CtiTime CtiDeviceSingle::getNextScan(INT a)
{
    LockGuard guard(monitor());
    return getScanData().getNextScan(a);
}
CtiDeviceSingle& CtiDeviceSingle::setNextScan(INT a, const CtiTime &b)
{
    LockGuard guard(monitor());

    scheduleSignaledAlternateScan(a);

    CtiTime Now, When;

    if( !isWindowOpen(Now, When) )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getName() << " scan window opens at " << When << ".  Scanning suspended." << endl;
        }

        getScanData().setNextScan(a, When);
    }
    else
        getScanData().setNextScan(a, b);

    return *this;
}

CtiTime CtiDeviceSingle::nextRemoteScan() const
{
    CtiTime nt = YUKONEOT;

    if( useScanFlags() )
    {
        nt = getScanData()->nextNearestTime(ScanRateLoadProfile);

        if(getDebugLevel() & 0x00100000)
        {
            if(nt < CtiTime(YUKONEOT))
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << left << setw(30) << getName() << right << "'s next scan is to occur at " << nt << endl;
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << left << setw(30) << getName() << right << " is pending completion. " << endl;
            }
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
                    dout << CtiTime() << " " << getName() << " deleting scanrate. Scantype " << i << endl;
                }

                delete _scanRateTbl[i];
                _scanRateTbl[i] = NULL;

                setNextScan(i, CtiTime(YUKONEOT));
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
            status = !isScanFlagSet(scantype);
            break;
        }
        case ScanRateIntegrity:
        {
            status = !isScanFlagSet(scantype);
            break;
        }
        case ScanRateAccum:
        {
            status = !isScanFlagSet(scantype);
            break;
        }
        case ScanRateLoadProfile:
        {
           status = true;
           break;
        }
    }

    status = validatePendingStatus(status, scantype);

    return status;
}

void CtiDeviceSingle::resetForScan(int scantype = -1)
{
    resetScanFlag(scantype);        // Clears ALL scan flags!
}

CtiDeviceSingle::CtiDeviceSingle() :
_useScanFlags(0)
{
    int i;
    _lastExpirationCheckTime = _lastExpirationCheckTime.now();
    LockGuard guard(monitor());

    for(i = 0; i < ScanRateInvalid; i++)
    {
        _scanRateTbl[i] = NULL;
    }
}

CtiDeviceSingle::CtiDeviceSingle(const CtiDeviceSingle& aRef)
{
    int i;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** This is expensive. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    LockGuard guard(monitor());

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

    for(i = 0; i < ScanRateInvalid; i++)
    {
        if( _scanRateTbl[i] != NULL)
        {
            delete _scanRateTbl[i];
            _scanRateTbl[i] = NULL;
        }
    }

    if(_scanData.isDirty())
    {
        if( _scanData.Update().errorCode() != RWDBStatus::ok )
        {
            if( _scanData.Insert().errorCode() != RWDBStatus::ok )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Unable to insert or update scandata for device " << this->getName() << endl;
            }
        }
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

        if(aRef.isScanDataValid())
        {
            _scanData = (const)aRef.getScanData();
        }
    }
    return *this;
}

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
        if(isAlternateRateActive(bScanIsScheduled, CtiTime(), rate))
        {
            // FirstScanInSignaledAlternateRate scan GOES NOW, once scheduled we report the normal rate.
            INT altrate = bScanIsScheduled ? _scanRateTbl[rate]->getAlternateRate() : 1L;
#if 0
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

BOOL CtiDeviceSingle::isWindowOpen(CtiTime &aNow, CtiTime &opensAt, CtiDeviceWindow_t windowType) const
{
    BOOL status = TRUE;


    try
    {
        // loop the vector
        for(int x=0; x <_windowVector.size(); x++)
        {
            if(_windowVector[x].getType() == windowType)
            {
                CtiTime lastMidnight(CtiDate());
                CtiTime open  (lastMidnight.seconds()+_windowVector[x].getOpen());
                CtiTime close (open.seconds()+_windowVector[x].getDuration());

                if(open == close)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** DB Config Error **** " << getName() << " has a zero time scan window defined. "<< endl;
                    }
                }
                else if((aNow < open) || (aNow > close))
                {
                    opensAt = open + 86400L;    // The next Window open time (open is today's open).
                    status = FALSE;
                }
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        status = FALSE;
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
            CtiTime lastMidnight(CtiDate());
            CtiTime open  (lastMidnight.seconds()+_windowVector[x].getOpen());
            CtiTime close (open.seconds()+_windowVector[x].getDuration());
            CtiTime now;

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
BOOL CtiDeviceSingle::isAlternateRateActive( bool &bScanIsScheduled, CtiTime &aNow, int rate) const
{
    BOOL status = FALSE;

    bScanIsScheduled = false;

    // loop the vector
    for(int x=0; x <_windowVector.size(); x++)
    {
        CtiTime lastMidnight(CtiDate());
        CtiTime open  (lastMidnight.seconds()+_windowVector[x].getOpen());
        CtiTime close (open.seconds()+_windowVector[x].getDuration());

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

CtiTime CtiDeviceSingle::getNextWindowOpen() const
{
    CtiTime now;
    CtiTime lastMidnight(CtiDate());
    CtiTime windowOpens = CtiTime(YUKONEOT);

    try
    {
        // loop the vector
        for(int x=0; x <_windowVector.size(); x++)
        {
            CtiTime open (lastMidnight.seconds()+_windowVector[x].getOpen());

            if(now <= open && open < windowOpens)
            {
                windowOpens = open;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return windowOpens;
}

void CtiDeviceSingle::applySignaledRateChange(LONG aOpen, LONG aDuration)
{
    bool found=false;

    CtiTime now;
    CtiTime lastMidnight(CtiDate());

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
            dout << now << " " << getName() << " changing to alternate scan rate starting " << CtiTime(lastMidnight.seconds()+aOpen) << " for " << aDuration << " seconds" << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << now << " Signaled alternate scan rate stop time has already passed for " << getName() << " " << CtiTime(lastMidnight.seconds()+aOpen+aDuration) << endl;
    }
}

void CtiDeviceSingle::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const
{
    Inherited::getSQL(db, keyTable, selector);
}

void CtiDeviceSingle::DecodeDatabaseReader(RWDBReader &rdr)
{

    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
    _scanData.setDeviceID(getID());

    LockGuard guard(monitor());
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

void CtiDeviceSingle::DecodeScanRateDatabaseReader(RWDBReader &rdr)
{
    LockGuard guard(monitor());

    string rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Decoding device windows " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                dout << CtiTime() << " " << getName() << "'s device window is invalid, open and close times are equal " << endl;
            }
        }
        else
        {
            removeWindowType( newWindow.getType() );
            _windowVector.push_back (newWindow);
        }
    }
}


void CtiDeviceSingle::DumpData()
{
    int i;
    Inherited::DumpData();

    LockGuard guard(monitor());

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

BOOL CtiDeviceSingle::useScanFlags() const
{
    return _useScanFlags;
}

CtiTableDeviceScanData& CtiDeviceSingle::getScanData()
{
    setUseScanFlags(TRUE);
    validateScanData();

    LockGuard guard(monitor());
    return _scanData;
}

const CtiTableDeviceScanData* CtiDeviceSingle::getScanData() const
{
    return &_scanData;
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
    return true;
}

INT CtiDeviceSingle::validateScanData()
{
    INT status = NORMAL;

    LockGuard guard(monitor());

    if( !isScanFlagSet(ScanDataValid) )
    {
        _scanData.setDeviceID(getID());
        setScanFlag(ScanDataValid, true);
        if( !(_scanData.Restore().errorCode() == RWDBStatus::ok ))
        {
            if( !(_scanData.Insert().errorCode() == RWDBStatus::ok ))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
    return status;
}

/*
 *  Look for the oldest point in the archive which is greater than the default date...
 */
CtiTime CtiDeviceSingle::peekDispatchTime() const
{
    CtiTime dispatchTime;

    vector<CtiPointSPtr> points;

    getDevicePoints(points);

    vector<CtiPointSPtr>::iterator itr;

    for( itr = points.begin(); itr != points.end(); itr++ )
    {
        CtiTablePointDispatch ptdefault(0);
        CtiTablePointDispatch pd;
        CtiPointSPtr pPoint;

        pPoint = *itr;

        if( pPoint )
        {
            pd.setPointID( pPoint->getPointID() );
            RWDBStatus dbstat = pd.Restore();

            if(dbstat.errorCode() == RWDBStatus::ok)
            {
                if(pd.getTimeStamp() > ptdefault.getTimeStamp() && pd.getTimeStamp() < dispatchTime)
                {
                    dispatchTime = pd.getTimeStamp();
                }
            }
        }
    }

    if(getDebugLevel() & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "   Oldest dispatch write time is " << dispatchTime << endl;
    }

    return dispatchTime;
}

bool CtiDeviceSingle::processAdditionalRoutes( INMESS *InMessage ) const
{
    return false;
}


bool CtiDeviceSingle::validatePendingStatus(bool status, int scantype, CtiTime &now)
{
    if(!status)     // In this case we need to make sure we have not gone tardy on this scan type
    {
        if( gScanForceDevices.find(getID()) != gScanForceDevices.end() )
        {
            status = true;
        }
        else
        {
            if(getScanData().getLastCommunicationTime(scantype) + getTardyTime(scantype) < now )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << getName() << "'s pending flags (" << scantype << ") reset due to timeout on prior scan (" << getScanData().getLastCommunicationTime(scantype) << " + " << getTardyTime(scantype) << " < " << now << ")" << endl;
                }
                resetForScan(scantype);
                getScanData().setLastCommunicationTime(scantype, now);
                status = true;
            }
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

    if(maxtardytime < gConfigParms.getValueAsInt("SCANNER_MAX_TARDY_TIME", 60))
    {
        maxtardytime = gConfigParms.getValueAsInt("SCANNER_MAX_TARDY_TIME", 60);
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

bool CtiDeviceSingle::hasLongScanRate(const string &cmd) const
{
    bool bret = false;

    if( useScanFlags() )
    {
        int scanratetype = desolveScanRateType(cmd);

        if(getScanRate(scanratetype) > 3600)
        {
            bret = true;
        }
    }

    return bret;
}


bool CtiDeviceSingle::hasRateOrClockChanged(int rate, CtiTime &Now)
{
    CtiTime previousScan;
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
    CtiTime now;
    CtiTime lastMidnight(CtiDate());

    // loop the vector
    for(int x=0; x <_windowVector.size(); x++)
    {
        if(_windowVector[x].getType() == DeviceWindowSignaledAlternateRate)
        {
            CtiTime open  (lastMidnight.seconds()+_windowVector[x].getOpen());
            CtiTime close (open.seconds()+_windowVector[x].getDuration());

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

/*
 * This method allows the command string to re-discover the scan type that was asked for.
 */
int CtiDeviceSingle::desolveScanRateType( const string &cmd )
{
    // First decide what scan rate we are.
    int scanratetype = ScanRateInvalid;

    if(findStringIgnoreCase(cmd," general") || findStringIgnoreCase(cmd," status"))
    {
        scanratetype = ScanRateGeneral;
    }
    else if(findStringIgnoreCase(cmd," integrity"))
    {
        scanratetype = ScanRateIntegrity;
    }
    else if(findStringIgnoreCase(cmd," accumulator"))
    {
        scanratetype = ScanRateAccum;
    }
    else if(findStringIgnoreCase(cmd," loadprofile"))
    {
        //  not applicable
        scanratetype = ScanRateLoadProfile;
    }

    return scanratetype;
}

bool CtiDeviceSingle::removeWindowType( int window_type )
{
    bool found = false;
    bool detect = false;
    bool again = true;

    while(again)
    {
        detect = false;
        for(int x = 0; x < _windowVector.size(); x++)
        {
            if(window_type < 0 || _windowVector[x].getType() == window_type)    // if window_type < 0, we remove all, this is the default argument.
            {
                _windowVector.erase(_windowVector.begin()+x);
                found = true;
                detect = true;
                break;
            }
        }

        if(!detect) again = false;
    }

    return found;
}

int CtiDeviceSingle::getGroupMessageCount(long userID, long comID)
{

    int retVal = 0;
    channelWithID temp;
    temp.channel = comID;
    temp.identifier = userID;
    MessageCount_t::iterator iterator = _messageCount.find(temp);

    if(iterator != _messageCount.end())
    {
        retVal = iterator->second;
    }
    return retVal;
}

CtiDeviceSingle::incrementGroupMessageCount(long userID, long comID, int entries /*=1*/ )
{
    channelWithID temp;
    temp.channel = comID;
    temp.identifier = userID;
    temp.creationTime = temp.creationTime.now();

    if((_lastExpirationCheckTime.now().seconds()-_lastExpirationCheckTime.seconds())>=2*60*60)//2 hour span
    {
        //Clears out every data member that is over 1 hour old, operates at most every 2 hours.
        _lastExpirationCheckTime = _lastExpirationCheckTime.now();

        for(MessageCount_t::iterator i = _messageCount.begin();i != _messageCount.end();)
        {
            if((_lastExpirationCheckTime.seconds() - i->first.creationTime.seconds()) >= 1*60*60)
                i = _messageCount.erase(i);
            else
                i++;
        }
    }

    MessageCount_t::iterator iterator = _messageCount.find(temp);

    if(iterator != _messageCount.end())
    {
        iterator->second += entries;
    }
    else
    {
        //This object was not here before. insert it.
        _messageCount.insert(MessageCount_t::value_type(temp, entries));
    }
}

CtiDeviceSingle::decrementGroupMessageCount(long userID, long comID, int entries /*=1*/ )
{
    int count;
    channelWithID temp;
    temp.channel = comID;
    temp.identifier = userID;
    MessageCount_t::iterator iterator = _messageCount.find(temp);

    if(iterator != _messageCount.end())
    {
        count = iterator->second;
        count -= entries;
        if(count <=0)
        {
            _messageCount.erase(temp);
        }
        else
        {
            iterator->second = count;
        }
    }
}

/*
    Use this predicate to identify globally addressable devices who are trying
    to scan at their corresponding global address.  This is a no-no.
    Currently the filter is pretty rough.  It blocks every global addressing capable
    device at every global address.
    Future improvement: Segregate each device to its exact address.
*/
bool isDeviceAddressGlobal(const int deviceType, const int address)
{
    switch (deviceType)
    {
        case TYPE_CCU700:
        case TYPE_CCU710:
        case TYPE_CCU711:
        case TYPE_ILEXRTU:
        case TYPE_WELCORTU:
        case TYPE_SES92RTU:
        case TYPE_LCU415:
        case TYPE_LCU415LG:
        case TYPE_LCU415ER:
        case TYPE_LCUT3026:
        case TYPE_TCU5000:
        case TYPE_TCU5500:
        case TYPE_DAVIS:
        case TYPE_VTU:
        {
            switch (address)
            {
                case RTUGLOBAL:
                case CCUGLOBAL:
            
                    return true;            
            }
        }
    }
 
    return false;
}

