/*-----------------------------------------------------------------------------*
*
* File:   dev_fulcrum
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_fulcrum.cpp-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2004/07/27 16:53:54 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )


#include <rw/rwtime.h>
#include <rw/rwdate.h>

#include "yukon.h"
#include "porter.h"
#include "dev_schlum.h"
#include "dev_fulcrum.h"

#include "pt_base.h"
#include "pt_status.h"
#include "pt_analog.h"
#include "pt_accum.h"

#include "msg_pcreturn.h"
#include "msg_cmd.h"
#include "msg_pdata.h"
#include "msg_multi.h"
#include "cmdparse.h"

#include "dlldefs.h"

#include "dupreq.h"
#include "dialup.h"

#include "logger.h"
#include "guard.h"


INT CtiDeviceFulcrum::GeneralScan(CtiRequestMsg *pReq,
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
        OutMessage->Buffer.DUPReq.Identity = IDENT_FULCRUM;
        status = Inherited::GeneralScan (pReq, parse, OutMessage, vgList, retList, outList, ScanPriority);
        return status;
    }
    else
    {
        return MEMORY;
    }
}



INT CtiDeviceFulcrum::generateCommandHandshake (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList)
{
    USHORT retCode = NORMAL;

    switch (getCurrentState())
    {
        case StateHandshakeInitialize:
            {
                setRetryAttempts(SCHLUMBERGER_RETRIES);
                setAttemptsRemaining (getRetryAttempts());
            }
        case StateHandshakeSendStart:
            {
                /*
                 * get the boys talking.
                 *
                 * We are able to send the UTS "I" command at any time before handshake
                 * to ensure that an Vectron is out there....  It will respond with
                 * an ASCII "SI,VECTRON    <CR>" string....
                 */

#ifndef UTS_ID
#define UTS_ID 1
#endif

#ifdef UTS_ID

                Transfer.getOutBuffer()[0] = 'I';
                Transfer.setOutCount(1);
                Transfer.setInCountExpected(16);
                Transfer.setInTimeout( 1 );
                Transfer.setCRCFlag(0);               // No CRC machinations
                memset(Transfer.getInBuffer(), 0x00, 20);
#endif
                setCurrentState (StateHandshakeDecodeStart);
                break;
            }
        case StateHandshakeSendAttn:
            {
                /*
                 *  We need to get the meter's attention.....
                 */
                Transfer.getOutBuffer()[0] = ENQ;
                Transfer.setOutCount( 1 );
                Transfer.setInCountExpected( 1 );
                Transfer.setCRCFlag( 0 );
                Transfer.setInTimeout( 1 );
                memset(Transfer.getInBuffer(), 0x00, 20);

                setCurrentState (StateHandshakeDecodeAttn);
                break;
            }
        case StateHandshakeSendIdentify:
            {
                Transfer.getOutBuffer()[0 ] = 'I';

                Transfer.getOutBuffer()[1 ] = 0  ;
                Transfer.getOutBuffer()[2 ] = 0  ;
                Transfer.getOutBuffer()[3 ] = 0  ;
                Transfer.getOutBuffer()[4 ] = 0  ;
                Transfer.getOutBuffer()[5 ] = 0  ;
                Transfer.getOutBuffer()[6 ] = 0  ;
                Transfer.getOutBuffer()[7 ] = 0  ;
                Transfer.getOutBuffer()[8 ] = 0  ;
                Transfer.getOutBuffer()[9 ] = 0  ;
                Transfer.getOutBuffer()[10] = 0  ;
                Transfer.getOutBuffer()[11] = 0  ;

                Transfer.setOutCount(12);
                Transfer.setInCountExpected(20);
                Transfer.setInTimeout( 1 );
                Transfer.setCRCFlag( XFER_ADD_CRC | XFER_VERIFY_CRC );
                setCurrentState (StateHandshakeDecodeIdentify);
                break;
            }
        case StateHandshakeSendSecurity:
            {
                // send the security code
                Transfer.getOutBuffer()[0] = 'S';

                // The security Code lives here.... but it is ASCII, so we subtract 0x30 from it...
                USHORT passwordLength = getIED().getPassword().length();

                for (int z=0; z < 8; z++)
                {
                    /**********************
                    * editor puts a zero as the default password
                    ***********************
                    */
                    if (z < passwordLength)
                    {
                        if (passwordLength == 1)
                        {
                            if ((getIED().getPassword()(z) != ' ') && (getIED().getPassword()(z) != '0'))
                            {
                                Transfer.getOutBuffer()[z+1] = getIED().getPassword()(z);
                            }
                            else
                            {
                                Transfer.getOutBuffer()[z+1] = '\0';
                            }
                        }
                        else
                        {
                            Transfer.getOutBuffer()[z+1] = getIED().getPassword()(z);
                        }
                    }
                    else
                        Transfer.getOutBuffer()[z+1] = '\0';
                }

                Transfer.setOutCount( 9 );
                Transfer.setCRCFlag( XFER_ADD_CRC );

                Transfer.setInCountExpected( 1 );
                Transfer.setInTimeout( 1 );
                setCurrentState (StateHandshakeDecodeSecurity);
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
                }
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState (StateHandshakeAbort);
                retCode = !NORMAL;
            }
    }
    return retCode;
}


INT CtiDeviceFulcrum::generateCommand (CtiXfer  &Transfer , RWTPtrSlist< CtiMessage > &traceList)
{
    SchlMeterStruct   MeterSt;
    USHORT retCode=NORMAL;

    switch (getCurrentCommand())
    {
        case CmdSelectMeter:
            {
                retCode = generateCommandSelectMeter (Transfer, traceList);
                break;
            }

        case CmdScanData:
            {
                retCode = generateCommandScan (Transfer, traceList);
                break;
            }

        case CmdLoadProfileData:
            {
                retCode = generateCommandLoadProfile (Transfer, traceList);
                break;
            }

        case CmdSchlumbergerUploadData:
        case CmdSchlumbergerUploadAll:
            {
                // get appropriate data
                switch (getCurrentState())
                {
                    case StateHandshakeComplete:
                    case StateScanValueSet1FirstScan:
                        {
                            // get current data
                            memcpy (&MeterSt, &getMeterParams(), sizeof (MeterSt));

                            fillUploadTransferObject (Transfer, MeterSt.Start, MeterSt.Stop);

                            /*************************************************************
                            *  Differnet from the norm, reset here before sending out
                            **************************************************************
                            */

                            Transfer.getInCountExpected()   = 1;

                            setPreviousState (StateScanValueSet1);
                            setCurrentState (StateScanDecode1);
                            break;

                        }
                    case StateScanValueSet1:
                        {
                            // get our current meter structure
                            memcpy (&MeterSt, &getMeterParams(), sizeof (MeterSt));

                            fillUploadTransferObject (Transfer, MeterSt.Start, MeterSt.Stop-5);
                            /*************************************************************
                            *  Differnet from the norm, reset here before sending out
                            **************************************************************
                            */

                            Transfer.getInCountExpected()   = 1;

                            setPreviousState (StateScanValueSet1);
                            setCurrentState (StateScanDecode1);

                            break;
                        }

                    case StateScanValueSet2:
                        {
                            // get our current data
                            memcpy (&MeterSt, &getMeterParams(), sizeof (MeterSt));

                            fillUploadTransferObject (Transfer, MeterSt.Start, MeterSt.Stop);

                            /*************************************************************
                            *  Differnet from the norm, reset here before sending out
                            **************************************************************
                            */
                            Transfer.getInCountExpected()   = MeterSt.Length + 2;
                            Transfer.getOutCount()          = 0;

                            setPreviousState (StateScanValueSet2);
                            setCurrentState (StateScanDecode2);

                            break;
                        }

                    default:
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
                        }
                        Transfer.setOutCount( 0 );
                        Transfer.setInCountExpected( 0 );
                        setCurrentState (StateScanAbort);
                        retCode = StateScanAbort;
                }
                break;
            }
        default:
            {
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState (StateScanAbort);
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " (" << __LINE__ << ") Invalid command " << getCurrentCommand() << " scanning " << getName() << endl;
                }
                retCode = StateScanAbort;
                break;
            }

    }

    return retCode;
}

INT CtiDeviceFulcrum::generateCommandSelectMeter (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList)
{
    SchlMeterStruct   MeterSt;
    int               retCode = NORMAL;

    // get appropriate data
    switch (getCurrentState())
    {
        case StateHandshakeInitialize:
        case StateScanValueSet1FirstScan:
        case StateScanValueSet1:
            {
                // download the hangup flag
                fillUploadTransferObject (Transfer, 0x2111, 0x2111);
                setAttemptsRemaining(3); // = SclumbergerRetries;

                /*************************************************************
                *  Differnet from the norm, reset here before sending out
                **************************************************************
                */
                Transfer.getOutBuffer()[0]      = 'D';
                Transfer.getInCountExpected()   = 0;
                Transfer.setCRCFlag( 0 );

                setPreviousState (StateScanValueSet1);
                setCurrentState (StateScanDecode1);
                break;
            }
        case StateScanValueSet2FirstScan:
        case StateScanValueSet2:
            {
                /***********************************************
                *  Future reference for the switch command
                *
                *  Slaves are offset from the master 0-4
                *  The switch command tells which meter in the pool
                *  of meters should next pickup the phone line
                *
                *  Address are as follows:
                *                    address     ones compliment
                *     master          0          255
                *     slave0          8          247
                *     slave0          9          246
                *     slave0         10          245
                *     slave0         11          244
                *
                *  Message Structure
                *  byte 1 =  X
                *  byte 2 =  address
                *  byte 3 =  Y
                *  byte 4 =  ones compliment of address
                ************************************************
                */
                Transfer.getOutBuffer()[0] = 'X';
                Transfer.getOutBuffer()[2] = 'Y';

                Transfer.getOutBuffer()[1] = getIED().getSlaveAddress();
                Transfer.getOutBuffer()[3] = (~getIED().getSlaveAddress());

                /******************************************
                *  use attempts to track the timeout
                *  we're not supposed to wait over 30 seconds
                *  for the meter to answer
                *******************************************
                */
                setAttemptsRemaining(20);
                Transfer.setOutCount(4);
                Transfer.setInCountExpected(0);
                Transfer.setCRCFlag( 0 );

                setPreviousState (StateScanValueSet2);
                setCurrentState (StateScanDecode2);
                break;

            }
        case StateScanValueSet3FirstScan:
        case StateScanValueSet3:
            {
                // we're waiting until we get zero bytes in the connection
                CTISleep (1000l);
                Transfer.setOutCount(0);
                Transfer.setInCountExpected(8);
                Transfer.setCRCFlag( 0 );
                setPreviousState (StateScanValueSet3);
                setCurrentState (StateScanDecode3);
                break;
            }

        case StateScanValueSet4FirstScan:
        case StateScanValueSet4:
            {
                // we need to do this up to 10 times
                Transfer.getOutBuffer()[0] = ENQ;
                Transfer.setOutCount( 1 );
                Transfer.setInCountExpected( 1 );
                Transfer.setCRCFlag( 0 );
                Transfer.setInTimeout( 1 );
                setPreviousState (StateScanValueSet4);
                setCurrentState (StateScanDecode4);
                break;
            }
        case StateHandshakeComplete:
        case StateScanValueSet5FirstScan:
        case StateScanValueSet5:
            {
                /******************************
                *  we've now done the handshake, finish the switch command
                *  by making sure we're talking to the slave we
                *  asked for
                *******************************
                */
                fillUploadTransferObject (Transfer, 0x4B75, 0x4B76);
                setAttemptsRemaining(3);

                /*************************************************************
                *  Differnet from the norm, reset here before sending out
                **************************************************************
                */
                setPreviousState (StateScanValueSet5);

                // decode our address in state 7
                setCurrentState (StateScanDecode5);
                break;
            }



        default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
            }
            Transfer.setOutCount( 0 );
            Transfer.setInCountExpected( 0 );
            setCurrentState (StateScanAbort);
            retCode = StateScanAbort;
    }
    return retCode;
}


INT CtiDeviceFulcrum::generateCommandScan (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList)
{
    SchlMeterStruct   MeterSt;
    int               retCode = NORMAL;

    /*
     *  This is the scanner request... Scanned data comes through here
     */

    // get appropriate data
    switch (getCurrentState())
    {
        case StateHandshakeComplete:
        case StateScanValueSet1FirstScan:  // done to set attempts only first time thru DLS
            {
                setRetryAttempts(SCHLUMBERGER_RETRIES);

                setAttemptsRemaining(getRetryAttempts()); // = SclumbergerRetries;
                setTotalByteCount (0);
            }
        case StateScanValueSet1:
            {
                fillUploadTransferObject (Transfer, 0x210D, 0x215D);

                setPreviousState (StateScanValueSet1);
                setCurrentState (StateScanDecode1);
                break;
            }

        case StateScanValueSet2FirstScan:  // done to set attempts only first time thru DLS
            {
                setAttemptsRemaining(getRetryAttempts()); // = SclumbergerRetries;
            }
        case StateScanValueSet2:
            {
                fillUploadTransferObject (Transfer, 0x27FD, 0x28DC);

                setPreviousState (StateScanValueSet2);
                setCurrentState (StateScanDecode2);
                break;
            }
        case StateScanValueSet3FirstScan:  // done to set attempts only first time thru DLS
            {
                setAttemptsRemaining(getRetryAttempts()); // = SclumbergerRetries;
            }
        case StateScanValueSet3:
            {
                // get the first third (Watts) of the demand registers
                fillUploadTransferObject (Transfer, 0x28DD, 0x296A);
                setPreviousState (StateScanValueSet3);
                setCurrentState (StateScanDecode3);
                break;
            }

        case StateScanValueSet4FirstScan:  // done to set attempts only first time thru DLS
            {
                setAttemptsRemaining(getRetryAttempts()); // = SclumbergerRetries;
            }
        case StateScanValueSet4:
            {
                // get the second third (lagging VARS) of demand register
                fillUploadTransferObject (Transfer, 0x296B, 0x29F8);
                setPreviousState (StateScanValueSet4);
                setCurrentState (StateScanDecode4);
                break;
            }
        case StateScanValueSet5FirstScan:  // done to set attempts only first time thru DLS
            {
                setAttemptsRemaining(getRetryAttempts()); // = SclumbergerRetries;
            }
        case StateScanValueSet5:
            {
                // get the last third (VA) of the demand registers
                fillUploadTransferObject (Transfer, 0x29F9, 0x2A86);
                setPreviousState (StateScanValueSet5);
                setCurrentState (StateScanDecode5);
                break;
            }
        case StateScanValueSet6FirstScan:  // done to set attempts only first time thru DLS
            {
                setAttemptsRemaining(getRetryAttempts()); // = SclumbergerRetries;
            }
        case StateScanValueSet6:
            {
                // get the PF and Instantaneous Volts/Amps
                fillUploadTransferObject (Transfer, 0x2A87, 0x2AEB);
                setPreviousState (StateScanValueSet6);
                setCurrentState (StateScanDecode6);
                break;
            }

        case StateScanResendRequest:
            {
                // must reset out count because it will add CRC again
                Transfer.getOutCount()          = 7;
                setCurrentState (getPreviousState());
                break;
            }

        case StateScanTimeFirstScan:  // done to set attempts only first time thru DLS
            {
                setAttemptsRemaining(getRetryAttempts()); // = SclumbergerRetries;
            }
        case StateScanTime:
            {
                // get time
                fillUploadTransferObject (Transfer, 0x32BC, 0x32C2);
                setPreviousState (StateScanTime);
                setCurrentState (StateScanDecodeTime);
                break;
            }

        case StateScanTestFirstScan:  // done to set attempts only first time thru DLS
            {
                setAttemptsRemaining(getRetryAttempts()); // = SclumbergerRetries;
            }
        case StateScanTest:
            {
                fillUploadTransferObject (Transfer, 0x2814, 0x2817);
                setPreviousState (StateScanTest);
                setCurrentState (StateScanComplete);
                break;
            }

        default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
            }
            Transfer.setOutCount( 0 );
            Transfer.setInCountExpected( 0 );
            setCurrentState (StateScanAbort);
            retCode = StateScanAbort;
    }

    return retCode;
}

INT CtiDeviceFulcrum::generateCommandLoadProfile (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList )
{
    // make this easier to read in the following code
    SchlumbergerLProfileInput_t *localMMInputs      = ((SchlumbergerLProfileInput_t *)_massMemoryRequestInputs);
    FulcrumMMConfig_t           *localMMConfig      = ((FulcrumMMConfig_t *)_massMemoryConfig);
    SchlLoadProfile_t           *localMMLoadProfile = ((SchlLoadProfile_t *)_massMemoryLoadProfile);
    FulcrumLoadProfileMessage_t *localLProfile      = ((FulcrumLoadProfileMessage_t*)_loadProfileBuffer);
    FulcrumRealTimeRegister_t   *localRTReg         = ((FulcrumRealTimeRegister_t *)_loadProfileTimeDate);

    int               retCode = NORMAL;
    ULONG    MMTemp               = 0L;
    SchlMeterStruct   MeterSt;

    /*
     *  This is a load profile request...
     */

    // get appropriate data
    switch (getCurrentState())
    {
        case StateHandshakeComplete:
        case StateScanComplete:
        case StateScanValueSet1FirstScan:  // done to set attempts only first time thru DLS
            {
                setRetryAttempts(SCHLUMBERGER_RETRIES);
                setAttemptsRemaining(getRetryAttempts()); // = SclumbergerRetries;
                setTotalByteCount (0);
            }
        case StateScanValueSet1:
            {
                // time
                fillUploadTransferObject (Transfer, 0x32BC, 0x32C2);
                setPreviousState (StateScanValueSet1);
                setCurrentState (StateScanDecode1);
                break;
            }

        case StateScanValueSet2FirstScan:  // done to set attempts only first time thru DLS
            {
                setAttemptsRemaining(getRetryAttempts()); // = SclumbergerRetries;
            }
        case StateScanValueSet2:
            {
                // State = SCHL_SCAN_STATE_SETUP_MM_CONFIG;
                // grabbing the mass memory configuration
                if ((localLProfile->porterLPTime - rwEpoch) > 0)
                //            if (ScannerRequestTime > 0)
                {
                    fillUploadTransferObject (Transfer, 0x32C9, 0x335F);
                    setPreviousState (StateScanValueSet2);
                    setCurrentState (StateScanDecode2);
                }
                else
                {
                    Transfer.setOutCount( 0 );
                    Transfer.setInCountExpected( 0 );

                    // we're done, return the scan data
                    setCurrentState (StateScanDecode4);
                }
                break;
            }
        case StateScanValueSet3FirstScan:  // done to set attempts only first time thru DLS
            {
                setAttemptsRemaining(getRetryAttempts()); // = SclumbergerRetries;
            }
        case StateScanValueSet3:
            {
                ULONG LPTime;
                bool validMeterClock = true;

                // Number of records which fit in MM
                ULONG MMMaxRecord;

                // Last Valid Address Should be MaxRecord - MaxRecord % RecordLength
                ULONG MMMaxAddress;

                // Convert to values we can use here... (little endian)
                ULONG MMStart   = bytesToBase10( localMMConfig->LogicalStart, 3 );
                ULONG MMStop    = bytesToBase10( localMMConfig->LogicalEnd, 3 );
                ULONG MMCurrent = bytesToBase10( localMMConfig->CurrentLogical, 3);

                INT MMCurrentYear = 0;

                // Convert to values we can use here... (little endian)
                localMMConfig->RecordLength = (USHORT)bytesToBase10((UCHAR*) &(localMMConfig->RecordLength),2);
                localMMConfig->CurrentRecord = (USHORT)bytesToBase10((UCHAR*) &(localMMConfig->CurrentRecord),2);

                // Number of records which fit in MM
                MMMaxRecord = (MMStop - MMStart) / localMMConfig->RecordLength;

                // Last Valid Address Should be MaxRecord - MaxRecord % RecordLength
                MMMaxAddress = MMMaxRecord * localMMConfig->RecordLength;

                // initialize my inputs
                localMMInputs->MMIndex            = 0L;
                localMMInputs->MMVintage          = 0L;
                localMMInputs->MMCount            = 0L;
                localMMInputs->MMPos              = 0L;
                localMMInputs->MMBlockSize        = 0L;
                localMMInputs->MMScanStartAddress = 0L;

                if (shouldRetrieveLoadProfile (localLProfile->porterLPTime, localMMConfig->IntervalLength))
                {
                    // check to make sure we have channels to look at
                    if (localMMConfig->NumberOfChannels != 0)
                    {
                        // Decide on the preferred block size for our requests....
                        // May be a (Communication protocol) maximum 256 bytes less ACK & 2 byte CRC..
                        // I'm using 250, or the RecordLength to make life easy, and pretty.
                        if (localMMConfig->RecordLength < 252)
                            localMMInputs->MMBlockSize = localMMConfig->RecordLength;
                        else
                            localMMInputs->MMBlockSize = 250;

                        // allocate the mass memory profile
                        if (_massMemoryLoadProfile == NULL)
                        {
                            _massMemoryLoadProfile = CTIDBG_new BYTE[(MMMaxRecord + 1) * sizeof(SchlLoadProfile_t)];
                        }

                        // reset this so it points to where massMemoryLoadProfile points
                        localMMLoadProfile = (SchlLoadProfile_t *)_massMemoryLoadProfile;

                        if ( _massMemoryLoadProfile != NULL )
                        {

                            localMMInputs->MMIndex            = 0;
                            localMMInputs->MMScanStartAddress = MMCurrent;

                            do
                            {
                                //
                                //  Compute values for the MMLProfile array back to a UNIX time prior to the
                                //  Scanner requested time.  MMScanStartAddress is backed progressivly through
                                //  time and recorded to allow faster requests of the data.
                                //

                                localMMLoadProfile[localMMInputs->MMIndex].RecordAddress = localMMInputs->MMScanStartAddress;

                                if (localMMInputs->MMIndex == 0)
                                {
                                    /*****************************
                                    *
                                    * We are looking at the MMCurrent record.  This effectively "primes"
                                    * my computational pump and sets up our time base...  If this is wrong,
                                    * the load profile data will represent the wrong time intervals
                                    *
                                    * FulcrumScan is the Meter Configuration Data brought back in prior scans.
                                    *
                                     *  Schlumberger keeps modulo 100 years.. bad bad bad and I must fixee here
                                     ******************************
                                     */
                                    if (RWDate().year() > 1999)
                                    {
                                        /*
                                         *  Now build a unix time out of the prior settings.
                                         *  NOTE:  This is a UNIX time of RECORD START!
                                         */

                                        RWTime t(RWDate((INT)localRTReg->DayOfMonth,
                                                        (INT)localRTReg->Month,
                                                        (INT)localRTReg->Year+2000),
                                                 (INT)localRTReg->Hours, 0, 0);
                                        LPTime = t.seconds() +
                                                 (((INT)localRTReg->Minutes - ((INT)localRTReg->Minutes % (INT)localMMConfig->IntervalLength)
                                                   - ((INT)localMMConfig->CurrentInterval * (INT)localMMConfig->IntervalLength)) * 60) -
                                                 rwEpoch;
                                    }
                                    else
                                    {
                                        /*
                                         *  Now build a unix time out of the prior settings.
                                         *  NOTE:  This is a UNIX time of RECORD START!
                                         */

                                        LPTime = RWTime (RWDate ((INT)localRTReg->DayOfMonth,
                                                                 (INT)localRTReg->Month,
                                                                 (INT)localRTReg->Year + 1900),
                                                         (INT)localRTReg->Hours, 0, 0).seconds()
                                                 + (((INT)localRTReg->Minutes - ((INT)localRTReg->Minutes % (INT)localMMConfig->IntervalLength)
                                                     - ((INT)localMMConfig->CurrentInterval * (INT)localMMConfig->IntervalLength)) * 60)
                                                 - rwEpoch;
                                    }

                                    /*************************
                                    * Check the validity of the time received
                                    **************************
                                    */
                                    if ((RWTime(LPTime+rwEpoch) < (RWTime()-(2*86400))) ||
                                        (RWTime(LPTime+rwEpoch) > (RWTime()+(2*86400))))
                                    {
                                        /***********************
                                        * if meter time is not within a 2 day window, its
                                        * invalid
                                        ************************
                                        */
                                        validMeterClock = false;
                                    }
                                    else
                                    {
                                        localMMLoadProfile[localMMInputs->MMIndex].RecordTime    = LPTime;

                                        // And the next INDEX
                                        localMMInputs->MMIndex += 1;
                                    }

                                }
                                else
                                {

                                    /* *************************
                                     *
                                     *  Use some magic to figure out the time of the current interval.
                                     *
                                     *  Note that LPTime should represent the time of the last intervals start...
                                     *
                                     *  We need only to subtract off the number of whole hours which
                                     *  are filled in one record (60 intervals).  Pretty nice of the berger
                                     *  engineers don't you think?
                                     *
                                     *  since IntervalLength minutes/interval * 60 intervals/record * 1/60 hours/minute == hours/record
                                     *
                                     *  convert it all to seconds since we have no way of backing up for a negative
                                     *  hour like the tm structure in C allows
                                     ***************************
                                    */

                                    LPTime = LPTime - ((INT)localMMConfig->IntervalLength * 3600 );
                                    LPTime = RWTime (RWDate (RWTime(LPTime + rwEpoch)),
                                                     RWTime (LPTime + rwEpoch).hour(),
                                                     0,
                                                     0).seconds() - rwEpoch;

                                    localMMLoadProfile[localMMInputs->MMIndex].RecordTime    = LPTime;       // UNIX time of record start!
                                    localMMInputs->MMIndex += 1;

                                }

                                if (validMeterClock)
                                {
                                    // Back 1 Record with the Address.
                                    localMMInputs->MMScanStartAddress = previousMassMemoryAddress(MMStart,
                                                                                                  localMMInputs->MMScanStartAddress,
                                                                                                  MMMaxAddress,
                                                                                                  localMMConfig->RecordLength);

                                }
                                /*
                                 *  A note about our conditions here.  We stop computations if we find a time
                                 *  prior to the point scanner asked for.  i.e. if we get all this data
                                 *  scanner will already have some amount of it.
                                 *
                                 *  We also stop scanning if we loop through all MM and are once again
                                 *  looking at the Current Address
                                 */

                            } while (localMMInputs->MMScanStartAddress != MMCurrent && ((localLProfile->porterLPTime - rwEpoch) < LPTime) &&
                                     validMeterClock);

                            // if the meter's time was valid
                            if (validMeterClock)
                            {

                                // This allows us to only retrieve the MM Records we need for scanner
                                localMMInputs->MMVintage = localMMInputs->MMIndex - 1;    // Scanner gets oldest request first....
                                localMMInputs->MMIndex   = 0;              // Reset for usage later...


                                /*
                                 *  OK, now we know who is the "oldest" record we want to bring back.  He is
                                 *  MMVintage, and we also want to return the following interval's time.
                                 *  We can fill in the localLProfile struct's times now.
                                 */

                                localLProfile->RecordTime         = localMMLoadProfile[localMMInputs->MMVintage].RecordTime;

                                if (localMMInputs->MMVintage >= 1)
                                    localLProfile->NextRecordTime  = localMMLoadProfile[localMMInputs->MMVintage - 1].RecordTime;
                                else
                                    localLProfile->NextRecordTime  = 0L;  // Only get the CurrentAddress record.

                                localMMInputs->MMScanStartAddress = localMMLoadProfile[localMMInputs->MMVintage].RecordAddress;
                                localMMInputs->MMPos = localMMInputs->MMScanStartAddress + localMMInputs->MMBlockSize;


                                // MMPos points to the byte address in Schl. MM where the scan stopped.
                                // This is either the end of Record, or the most we can ask for in one read.
                                fillUploadTransferObject (Transfer,
                                                          localMMInputs->MMScanStartAddress,
                                                          localMMInputs->MMPos);
                                setPreviousState (StateScanValueSet3);
                                setCurrentState (StateScanDecode3);
                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " Aborting scan: Invalid clock reading " << getName()  << " " << RWTime(LPTime+rwEpoch).asString()<< endl;
                                }
                                Transfer.setOutCount( 0 );
                                Transfer.setInCountExpected( 0 );
                                setCurrentState (StateScanAbort);
                                setAttemptsRemaining (0);
                                retCode = StateScanAbort;
                            }

                        }
                        else
                        {
                            Transfer.setOutCount( 0 );
                            Transfer.setInCountExpected( 0 );
                            setCurrentState (StateScanAbort);
                            setAttemptsRemaining (0);
                            retCode = StateScanAbort;
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Meter " << getName() << " has no load profile channels configured" << endl;
                        }
                        Transfer.setOutCount( 0 );
                        Transfer.setInCountExpected( 0 );
                        setCurrentState (StateScanDecode4);
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Load profile for " << getName() << " will not be collected this scan" << endl;
                    }
                    Transfer.setOutCount( 0 );
                    Transfer.setInCountExpected( 0 );
                    setCurrentState (StateScanDecode4);
                }
                break;
            }
        case StateScanValueSet4FirstScan:  // done to set attempts only first time thru DLS
            {
                setAttemptsRemaining(getRetryAttempts()); // = SclumbergerRetries;
            }
        case StateScanValueSet4:
            {
                // setup from SCHL_SCAN_DECODE_MM_READ
                /*
                 *  We need to go out and get more data, because we did NOT get it all last time.
                 */

                // Ho much more to get???
                MMTemp = (localMMInputs->MMScanStartAddress + localMMConfig->RecordLength) - localMMInputs->MMPos;
                if (MMTemp > localMMInputs->MMBlockSize)
                {
                    fillUploadTransferObject (Transfer,
                                              localMMInputs->MMPos,
                                              localMMInputs->MMPos + localMMInputs->MMBlockSize);

                    localMMInputs->MMPos += localMMInputs->MMBlockSize;
                }
                else
                {
                    fillUploadTransferObject (Transfer,
                                              localMMInputs->MMPos,
                                              localMMInputs->MMPos + MMTemp);

                    localMMInputs->MMPos += MMTemp;
                }

                setPreviousState (StateScanValueSet4);
                setCurrentState (StateScanDecode3);

                break;
            }

        case StateScanValueSet5FirstScan:  // done to set attempts only first time thru DLS
            {
                setAttemptsRemaining(getRetryAttempts()); // = SclumbergerRetries;
            }
        case StateScanValueSet5:
            {
                // reset buffers since they are re-used everytime
                localMMInputs->MMCount = 0;

                /*
                 *  We have just completed an entire get on a record.
                 *  Point at the next one to get
                 */
                localMMInputs->MMIndex+=1;                                   // Next Address

                /*
                 *  Check if we've gotten as many as Scanner needs.
                 */

                if (localMMInputs->MMIndex <= localMMInputs->MMVintage)
                {
                    /*
                     *  Use our hard earned MMLProfile to set up the next read
                     */
                    localMMInputs->MMScanStartAddress = localMMLoadProfile[localMMInputs->MMVintage - localMMInputs->MMIndex].RecordAddress;

                    // MMPos points to the byte address in Schl. MM where the scan stopped.
                    // This is either the end or Record, or the most we can ask for in one read.
                    localMMInputs->MMPos = localMMInputs->MMScanStartAddress + localMMInputs->MMBlockSize;

                    fillUploadTransferObject (Transfer,
                                              localMMInputs->MMScanStartAddress,
                                              localMMInputs->MMPos);


                    localLProfile->RecordTime         = localMMLoadProfile[localMMInputs->MMVintage - localMMInputs->MMIndex].RecordTime;
                    if (localMMInputs->MMVintage - localMMInputs->MMIndex >= 1)
                    {
                        localLProfile->NextRecordTime  = localMMLoadProfile[localMMInputs->MMVintage - localMMInputs->MMIndex - 1].RecordTime;
                    }
                    else
                    {
                        localLProfile->NextRecordTime  = 0L;  // Only get the CurrentAddress record.
                    }

                    setPreviousState (StateScanValueSet4);
                    setCurrentState (StateScanDecode3);

                }
                else
                {
                    /*
                     *  We've gotten all the required MM Records.
                     *  This is the End of the MM request sequence and
                     *  all decoding is done after this on scanner side
                     */
                    Transfer.setOutCount( 0 );
                    Transfer.setInCountExpected( 0 );
                    setCurrentState (StateScanDecode4);
                }
                break;
            }

        case StateScanResendRequest:
            {
                // must reset out count because it will add CRC again
                Transfer.getOutCount()          = 7;
                setCurrentState (getPreviousState());
                break;
            }

        default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
            }
            Transfer.setOutCount( 0 );
            Transfer.setInCountExpected( 0 );
            setCurrentState (StateScanAbort);
            retCode = StateScanAbort;

    }
    return retCode;
}


INT CtiDeviceFulcrum::decodeResponseHandshake (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    SchlMeterStruct   MeterSt;
    int retCode = NORMAL;

    switch (getCurrentState())
    {
        case StateHandshakeDecodeStart:
            {
                if (Transfer.getInBuffer()[0] == NAK)
                {
                    setAttemptsRemaining(getRetryAttempts());
                    setCurrentState (StateHandshakeSendIdentify);
                    if (Transfer.doTrace(ERRUNKNOWN))
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " NAK: Fulcrum " << getName() << " already online"<< endl;
                    }
                    break;
                }
                else if (Transfer.getInBuffer()[0] == ACK)
                {
                    setAttemptsRemaining(getRetryAttempts());
                    setCurrentState (StateHandshakeSendIdentify);
                    if (Transfer.doTrace(ERRUNKNOWN))
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " ACK: Fulcrum " << getName() << " already online"<< endl;
                    }
                    break;
                }
                else if (strncmp((CHAR*)Transfer.getInBuffer(), "SI,FULCRUM     \r", 10)) // We go in if they are not identical
                {
                    setAttemptsRemaining(getAttemptsRemaining()-1);

                    if (0 == getAttemptsRemaining())
                    {
                        setAttemptsRemaining(getRetryAttempts());
                        setCurrentState (StateHandshakeSendAttn);
                    }
                    else
                    {
                        setCurrentState (StateHandshakeSendStart);
                    }
                }
                else
                {
                    // This effectively zero's out the other possibilities
                    setAttemptsRemaining(getRetryAttempts());
                    setCurrentState (StateHandshakeSendAttn);

                }
                break;
            }
        case StateHandshakeDecodeAttn:
            {
                if (Transfer.getInBuffer()[0] == ACK)
                {
                    setAttemptsRemaining(getRetryAttempts());
                    setCurrentState (StateHandshakeSendIdentify);
                }
                else if ((getAttemptsRemaining()-1) > 0)
                {
                    setAttemptsRemaining (getAttemptsRemaining()-1);
                    setCurrentState (StateHandshakeSendAttn);
                }
                else
                {
                    setAttemptsRemaining (0);
                    setCurrentState (StateHandshakeAbort);  // Couldn't get the job done... bail out here
                    retCode = StateHandshakeAbort;
                }
                break;
            }
        case StateHandshakeDecodeIdentify:
            {
                if (Transfer.getInBuffer()[0] == ACK)
                {
                    MeterSt.Start = StrToUlong(&Transfer.getInBuffer()[12], 3);
                    MeterSt.Stop  = StrToUlong(&Transfer.getInBuffer()[15], 3);
                    MeterSt.Length = MeterSt.Stop - MeterSt.Start + 1; // Return the maximum requirements of the meter.
                    memcpy(&MeterSt.UnitType, &Transfer.getInBuffer()[1], 3);
                    MeterSt.UnitType[3] = '\0';
                    memcpy(&MeterSt.UnitId, &Transfer.getInBuffer()[4], 8);
                    MeterSt.UnitId[8] = '\0';

                    // put it in the device for later
                    setMeterParams (MeterSt);

                    setAttemptsRemaining(getRetryAttempts());
                    setCurrentState (StateHandshakeSendSecurity);
                }
                else if ((getAttemptsRemaining()-1) > 0)
                {
                    setAttemptsRemaining (getAttemptsRemaining()-1);
                    setCurrentState (StateHandshakeSendIdentify);
                }
                else
                {
                    setAttemptsRemaining (0);
                    setCurrentState (StateHandshakeAbort);  // Couldn't get the job done... bail out here
                    retCode = StateHandshakeAbort;
                }
                break;
            }
        case StateHandshakeDecodeSecurity:
            {
                if (commReturnValue)
                {
                    // CGP Huh?? Transfer.InCountReceived = 0;
                    if (Transfer.doTrace (BADCRC))
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Data failed CRC " << getName() << endl;
                        }
                    }
                    else if ( Transfer.doTrace (READTIMEOUT))
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " No Reply from meter " << getName() << endl;
                        }
                    }
                    CTISleep(2000);
                }

                if (Transfer.getInBuffer()[0]== ACK)
                {
                    // handshake is done, move on
                    setCurrentState (StateHandshakeComplete);
                }
                else if ((getAttemptsRemaining()-1) > 0)
                {
                    setAttemptsRemaining (getAttemptsRemaining()-1);
                    setCurrentState (StateHandshakeSendSecurity);
                }
                else
                {
                    setAttemptsRemaining (0);
                    setCurrentState (StateHandshakeAbort);  // Couldn't get the job done... bail out here
                    retCode = StateHandshakeAbort;
                }
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
                }
                setCurrentState (StateHandshakeAbort);
                retCode = StateHandshakeAbort;
                break;
            }
    }
    return retCode;
}


INT CtiDeviceFulcrum::decodeResponse (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    SchlMeterStruct   MeterSt;
    USHORT retCode = NORMAL;

    switch (getCurrentCommand())
    {
        case CmdSelectMeter:
            {
                // decode the return message
                retCode = decodeResponseSelectMeter (Transfer, commReturnValue, traceList);
                break;
            }

        case CmdScanData:
            {
                // decode the return message
                retCode = decodeResponseScan (Transfer, commReturnValue, traceList);
                break;
            }

        case CmdLoadProfileData:
            {
                // decode the return message
                retCode = decodeResponseLoadProfile (Transfer, commReturnValue, traceList);
                break;
            }

        case CmdSchlumbergerUploadData:
        case CmdSchlumbergerUploadAll:
            {
                // get appropriate data
                switch (getCurrentState())
                {
                    case StateScanDecode1:
                        {
                            // if we nak, change some things and keep going
                            if (Transfer.getInBuffer()[0] == NAK)
                                setCurrentState (getPreviousState());
                            else
                                setCurrentState (StateScanValueSet2);

                            break;
                        }
                    case StateScanDecode2:
                        // put the data we received into the databuffer
                        memcpy (&MeterSt, &getMeterParams(), sizeof (MeterSt));
                        memcpy(&_dataBuffer, Transfer.getInBuffer(), MeterSt.Length);
                        setCurrentState (StateScanComplete);
                        break;

                    default:
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
                        }
                        setCurrentState (StateScanAbort);
                        retCode = StateScanAbort;
                }
                break;
            }

        default:
            {
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState (StateScanAbort);
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " (" << __LINE__ << ") Invalid command " << getCurrentCommand() << " scanning " << getName() << endl;
                }
                retCode = StateScanAbort;
                break;
            }
    }
    return retCode;
}



INT CtiDeviceFulcrum::decodeResponseSelectMeter(CtiXfer  &Transfer,INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)

{
    int               retCode    = NORMAL;

    // get appropriate data
    switch (getCurrentState())
    {
        case StateScanDecode1:
            {
                // sleep for two seconds then continue
                CTISleep (2000l);
                setPreviousState (StateScanDecode1);
                setCurrentState (StateScanValueSet2FirstScan);
                break;
            }

        case StateScanDecode2:
            {
                CTISleep (2000l);
                setPreviousState (StateScanDecode2);
                setCurrentState (StateScanValueSet3FirstScan);
                break;
            }

        case StateScanDecode3:
            {
                /*******************
                * we are reading all the garbage bytes from the
                * stream before we attempt to continue logging
                * on to the slave
                *
                * once the stream returns zero bytes, we can continue
                ********************
                */
                if (Transfer.getInCountActual() != 0)
                {
                    setAttemptsRemaining (getAttemptsRemaining() - 1);

                    // if we've done our attempts, bail out
                    if (getAttemptsRemaining() <= 0)
                    {
                        setCurrentState (StateScanAbort);
                        retCode = StateScanAbort;
                    }
                    else
                    {
                        setPreviousState (StateScanDecode3);
                        setCurrentState (StateScanValueSet3FirstScan);
                    }
                }
                else
                {
                    // we can now continue on with business
                    setPreviousState (StateScanDecode3);
                    setCurrentState (StateScanValueSet4FirstScan);

                    // 5 attempts to get the 10 ENQ/ACK combo's
                    setAttemptsRemaining (5);

                    // using this to track number of ACKs I get
                    setTotalByteCount (0);

                }
                break;
            }

        case StateScanDecode4:
            {
                /*********************
                * we waiting for 10 ENQ/ACK communications in a row to continue
                * with the switching process
                **********************
                */
                if (Transfer.getInBuffer()[0] == ACK)
                {
                    setTotalByteCount (getTotalByteCount() + 1);

                    // I need 10 ACKS in a row to assume the selected meter is ready to fly
                    if (getTotalByteCount() >=10)
                    {
                        // we're ready to continue
                        setCurrentState (StateScanComplete);
                        setTotalByteCount (0);
                    }
                    else
                    {
                        setCurrentState (StateScanValueSet4);
                    }
                }
                else
                {
                    setAttemptsRemaining (getAttemptsRemaining()-1);

                    if ((getAttemptsRemaining()) > 0)
                    {
                        /*****************
                        * if this isn't zero, it means we started the ENQ/ACK sequence and
                        * failed sometime during the process.  Because this process is supposed
                        * to happen within a certain time frame if we fail during the sequence
                        * we stop trying and go back through the process again
                        ******************
                        */
                        if (getTotalByteCount () != 0)
                        {
                            setCurrentState (StateScanAbort);  // Couldn't get the job done... bail out here
                            setTotalByteCount (0);
                            retCode = StateScanAbort;
                        }
                        else
                        {
                            setCurrentState (StateScanValueSet4);
                        }
                    }
                    else
                    {
                        setCurrentState (StateScanAbort);  // Couldn't get the job done... bail out here
                        setTotalByteCount (0);
                        retCode = StateScanAbort;
                    }
                }
                break;
            }
        case StateScanDecode5:
            {
                /*************************************
                * see if the slave we requested is the one talking
                * check bit 7 to see if the readings are ok
                **************************************
                */
                if ((Transfer.getInBuffer()[0] == ACK) && (Transfer.getInBuffer()[2] & 0x80))
                {
                    switch (getIED().getSlaveAddress())
                    {
                        case 0:
                            {
                                // make sure its a slave
                                if (!(Transfer.getInBuffer()[1] & 0x01) && (Transfer.getInBuffer()[1] & 0x02))
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " Successfully switched to master " << getName() << endl;
                                    }
                                    setCurrentState (StateScanComplete);

                                }
                                else
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " Failed switching to master " << getName() << endl;
                                    }
                                    setCurrentState (StateScanAbort);
                                    retCode = StateScanAbort;
                                }
                                break;
                            }

                        case 8:
                            {
                                // make sure its a slave
                                if ((Transfer.getInBuffer()[1] & 0x01) && (Transfer.getInBuffer()[1] & 0x02))
                                {
                                    // both bits 2 and 3 should be 0
                                    if ((Transfer.getInBuffer()[1] & 0x04) ||
                                        (Transfer.getInBuffer()[1] & 0x08))
                                    {
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " Failed switching to slave 1 " << getName() << endl;
                                        }
                                        setCurrentState (StateScanAbort);
                                        retCode = StateScanAbort;
                                    }
                                    else
                                    {
                                        // correct slave, move on
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " Successfully switched to slave 1 " << getName() << endl;
                                        }
                                        setCurrentState (StateScanComplete);
                                    }
                                }
                                else
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " Failed switching to slave 1 " << getName() << endl;
                                    }
                                    setCurrentState (StateScanAbort);
                                    retCode = StateScanAbort;
                                }


                                break;
                            }
                        case 9:
                            {
                                // make sure its a slave
                                if ((Transfer.getInBuffer()[1] & 0x01) && (Transfer.getInBuffer()[1] & 0x02))
                                {
                                    // bit 2 off bit 3 on
                                    if ((!(Transfer.getInBuffer()[1] & 0x04)) ||
                                        (Transfer.getInBuffer()[1] & 0x08))
                                    {
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " Failed switching to slave 2 " << getName() << endl;
                                        }
                                        setCurrentState (StateScanAbort);
                                        retCode = StateScanAbort;
                                    }
                                    else
                                    {
                                        // correct slave, move on
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " Successfully switched to slave 2 " << getName() << endl;
                                        }
                                        setCurrentState (StateScanComplete);
                                    }
                                }
                                else
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " Failed switching to slave 2 " << getName() << endl;
                                    }
                                    setCurrentState (StateScanAbort);
                                    retCode = StateScanAbort;
                                }
                                break;
                            }
                        case 10:
                            {
                                // make sure its a slave
                                if ((Transfer.getInBuffer()[1] & 0x01) && (Transfer.getInBuffer()[1] & 0x02))
                                {
                                    // bit 2 on bit 3 off
                                    if ((Transfer.getInBuffer()[1] & 0x04) ||
                                        (!(Transfer.getInBuffer()[1] & 0x08)))
                                    {
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " Failed switching to slave 3 " << getName() << endl;
                                        }
                                        setCurrentState (StateScanAbort);
                                        retCode = StateScanAbort;
                                    }
                                    else
                                    {
                                        // correct slave, move on
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " Successfully switched to slave 3 " << getName() << endl;
                                        }
                                        setCurrentState (StateScanComplete);
                                    }
                                }
                                else
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " Failed switching to slave 3 " << getName() << endl;
                                    }
                                    setCurrentState (StateScanAbort);
                                    retCode = StateScanAbort;
                                }

                                break;
                            }
                        case 11:
                            {
                                // make sure its a slave
                                if ((Transfer.getInBuffer()[1] & 0x01) && (Transfer.getInBuffer()[1] & 0x02))
                                {
                                    // bit 2 on bit 3 off
                                    if ((!(Transfer.getInBuffer()[1] & 0x04)) ||
                                        (!(Transfer.getInBuffer()[1] & 0x08)))
                                    {
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " Failed switching to slave 4 " << getName() << endl;
                                        }
                                        setCurrentState (StateScanAbort);
                                        retCode = StateScanAbort;
                                    }
                                    else
                                    {
                                        // correct slave, move on
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " Successfully switched to slave 4 " << getName() << endl;
                                        }
                                        setCurrentState (StateScanComplete);
                                    }
                                }
                                else
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " Failed switching to slave 4 " << getName() << endl;
                                    }
                                    setCurrentState (StateScanAbort);
                                    retCode = StateScanAbort;
                                }

                                break;
                            }
                    }
                }
                else
                {
                    retCode = StateScanAbort;
                    setCurrentState (StateScanAbort);
                }
                break;
            }

        default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
            }
            setCurrentState (StateScanAbort);
            retCode = StateScanAbort;

    }
    return retCode;
}


INT CtiDeviceFulcrum::decodeResponseScan (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    FulcrumLoadProfileMessage_t *localLProfile      = ((FulcrumLoadProfileMessage_t*)_loadProfileBuffer);
    SchlMeterStruct   MeterSt;
    int               retCode    = NORMAL;
    // run the return through the ringer
    retCode = checkReturnMsg (Transfer, commReturnValue);
    if (!retCode)
    {
        // retrieve the command params we just sent
        memcpy (&MeterSt, &getMeterParams(), sizeof (MeterSt));

        // get appropriate data
        switch (getCurrentState())
        {
            case StateScanDecode1:
                {
                    // Active Phases
                    _dataBuffer[getTotalByteCount()] = Transfer.getInBuffer()[1];
                    setTotalByteCount (getTotalByteCount()+1);

                    // Demand Reset Count
                    memcpy(&_dataBuffer[getTotalByteCount()], &Transfer.getInBuffer()[0x211E - MeterSt.Start + 1], 2);
                    setTotalByteCount (getTotalByteCount()+2);

                    // Counts
                    memcpy(&_dataBuffer[getTotalByteCount()], &Transfer.getInBuffer()[0x2124 - MeterSt.Start + 1], 4);
                    setTotalByteCount (getTotalByteCount()+4);

                    memcpy(&_dataBuffer[getTotalByteCount()], &Transfer.getInBuffer()[0x213c - MeterSt.Start + 1], 34);
                    setTotalByteCount (getTotalByteCount()+34);

                    setCurrentState (StateScanValueSet2FirstScan);
                    break;
                }
            case StateScanDecode2:
                {
                    // Register Multiplier
                    memcpy(&_dataBuffer[getTotalByteCount()], &Transfer.getInBuffer()[0x2814 - MeterSt.Start + 1], 4);
                    setTotalByteCount (getTotalByteCount()+4);

                    // Energy Registers
                    memcpy(&_dataBuffer[getTotalByteCount()], &Transfer.getInBuffer()[0x2819 - MeterSt.Start + 1], 196);
                    setTotalByteCount (getTotalByteCount()+196);

                    setCurrentState (StateScanValueSet3FirstScan);
                    break;
                }
            case StateScanDecode3:
                {
                    //Watts Demand registers
                    memcpy(&_dataBuffer[getTotalByteCount()], &Transfer.getInBuffer()[1], MeterSt.Length);
                    setTotalByteCount (getTotalByteCount()+MeterSt.Length);

                    setCurrentState (StateScanValueSet4FirstScan);
                    break;
                }
            case StateScanDecode4:
                {
                    // Lagging VARS Demand registers
                    memcpy(&_dataBuffer[getTotalByteCount()], &Transfer.getInBuffer()[1], MeterSt.Length);
                    setTotalByteCount (getTotalByteCount()+MeterSt.Length);

                    setCurrentState (StateScanValueSet5FirstScan);
                    break;
                }
            case StateScanDecode5:
                {
                    // VA Demand registers
                    memcpy(&_dataBuffer[getTotalByteCount()], &Transfer.getInBuffer()[1], MeterSt.Length);
                    setTotalByteCount (getTotalByteCount()+MeterSt.Length);

                    setCurrentState (StateScanValueSet6FirstScan);
                    break;
                }
            case StateScanDecode6:
                {
                    // PF & Instantaneous Volts/Amps
                    memcpy(&_dataBuffer[getTotalByteCount()], &Transfer.getInBuffer()[1], MeterSt.Length);
                    setTotalByteCount (getTotalByteCount()+MeterSt.Length);

                    setCurrentState (StateScanTimeFirstScan);
                    break;
                }
            case StateScanDecodeTime:
                {
                    // time
                    memcpy(&_dataBuffer[getTotalByteCount()], &Transfer.getInBuffer()[1], MeterSt.Length);
                    setTotalByteCount (getTotalByteCount()+MeterSt.Length);

                    setCurrentState (StateScanComplete);
                    setCurrentCommand(CmdLoadProfileTransition);
                    break;
                }

            default:
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
                }
                setCurrentState (StateScanAbort);
                retCode = StateScanAbort;
        }
    }
    else if (retCode == SCHLUM_RESEND_CMD)
    {
        // we don't want to abort, just try again
        retCode = NORMAL;
    }

    return retCode;
}


INT CtiDeviceFulcrum::decodeResponseLoadProfile (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    SchlumbergerLProfileInput_t *localMMInputs      = ((SchlumbergerLProfileInput_t *)_massMemoryRequestInputs);
    SchlLoadProfile_t           *localMMLoadProfile = ((SchlLoadProfile_t *)_massMemoryLoadProfile);
    FulcrumMMConfig_t           *localMMConfig      = ((FulcrumMMConfig_t *)_massMemoryConfig);
    FulcrumLoadProfileMessage_t *localLProfile      = ((FulcrumLoadProfileMessage_t*)_loadProfileBuffer);
    FulcrumRealTimeRegister_t   *localTimeDate      = ((FulcrumRealTimeRegister_t *)_loadProfileTimeDate);

    SchlMeterStruct   MeterSt;
    int               retCode  = NORMAL;


    // run the return through the ringer
    retCode = checkReturnMsg (Transfer, commReturnValue);
    if (!retCode)
    {
        // retrieve the command params we just sent
        memcpy (&MeterSt, &getMeterParams(), sizeof (MeterSt));

        // get appropriate data
        switch (getCurrentState())
        {
            case StateScanDecode1:
                {
                    //   Grab the time date
                    memcpy(_loadProfileTimeDate, &Transfer.getInBuffer()[1], MeterSt.Length);

                    setCurrentState (StateScanValueSet2FirstScan);
                    break;
                }
            case StateScanDecode2:
                {
                    // Mass memory configuration     SCHL_SCAN_STATE_DECODE_MM_CONFIG
                    memcpy(localMMConfig, &Transfer.getInBuffer()[1], MeterSt.Length);

                    setCurrentState (StateScanValueSet3FirstScan);
                    break;
                }
            case StateScanDecode3:
                {
                    // Mass memory configuration     SCHL_SCAN_STATE_DECODE_MM_READ

                    memcpy(&(localLProfile->MMBuffer[localMMInputs->MMCount]), &Transfer.getInBuffer()[1], MeterSt.Length - 1);

                    localMMInputs->MMCount += (MeterSt.Length - 1);

                    /*
                     * Now decide if we need more, or if we need to go back a record
                     */
                    if (localMMInputs->MMPos < localMMInputs->MMScanStartAddress + localMMConfig->RecordLength)
                    {
                        setCurrentState (StateScanValueSet4FirstScan);
                    }
                    else
                    {
                        /*
                         *  We've got the record! get it back to SCANNER & Let him decide what to do next.
                         */
                        setPreviousState (StateScanValueSet5FirstScan);
                        setCurrentState (StateScanReturnLoadProfile);
                        // we are now doing this in portfield.cpp  SCHL_SCAN_STATE_FULL_RECORD;
                    }
                    break;
                }
            case StateScanDecode4:
                {
                    // we're done
                    setCurrentState (StateScanComplete);
                    break;
                }
            default:
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
                }
                setCurrentState (StateScanAbort);
                retCode = StateScanAbort;
        }
    }
    else if (retCode == SCHLUM_RESEND_CMD)
    {
        // we don't want to abort, just try again
        retCode = NORMAL;
    }
    return retCode;
}


INT CtiDeviceFulcrum::decodeResultScan (INMESS *InMessage,
                                        RWTime &TimeNow,
                                        RWTPtrSlist< CtiMessage >   &vgList,
                                        RWTPtrSlist< CtiMessage > &retList,
                                        RWTPtrSlist< OUTMESS > &outList)
{
    char tmpCurrentState = InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1];
    CHAR     temp[100], buffer[60];

    BOOL     bDoStatusCheck = FALSE;
    BOOL     bDoResetStatus = FALSE;

    /* Misc. definitions */
    ULONG i, j;
    ULONG Pointer;

    /* Variables for decoding Messages */
    SHORT    Value;
    USHORT   UValue;
    FLOAT    PartHour;
    DOUBLE   PValue;

    DIALUPREQUEST      *DupReq = &InMessage->Buffer.DUPSt.DUPRep.ReqSt;
    DIALUPREPLY        *DUPRep = &InMessage->Buffer.DUPSt.DUPRep;


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

    FulcrumScanData_t  *Fsd = (FulcrumScanData_t*) DUPRep->Message;
    RWTime peakTime;


    if (isScanPending())
    {
        // if we bombed, we need an error condition and to plug values
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

            // through this into the return list
            insertPointIntoReturnMsg (pMsg, pPIL);
        }
        else
        {
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
                    if (getMeterDataFromScanStruct (i, PValue, peakTime, Fsd))
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

    if (pPIL->PointData().entries() > 0)
    {
        retList.insert( pPIL );
    }
    else
    {
        delete pPIL;
    }
    // set this to null
    pPIL = NULL;

    if( DebugLevel & 0x0001 )
        ResultDisplay(InMessage);
    return NORMAL;
}



INT CtiDeviceFulcrum::decodeResultLoadProfile (INMESS *InMessage,
                                               RWTime &TimeNow,
                                               RWTPtrSlist< CtiMessage >   &vgList,
                                               RWTPtrSlist< CtiMessage > &retList,
                                               RWTPtrSlist< OUTMESS > &outList)
{

    DIALUPREQUEST                 *dupReq = &InMessage->Buffer.DUPSt.DUPRep.ReqSt;
    DIALUPREPLY                   *dupRep = &InMessage->Buffer.DUPSt.DUPRep;

    FulcrumLoadProfileMessage_t   *fulcrumLProfile = (FulcrumLoadProfileMessage_t*)&dupRep->Message;
    FulcrumMMConfig_t             *fulcrumMMConfig = (FulcrumMMConfig_t *)&fulcrumLProfile->MMConfig;

    INT            numberOfIntervals;
    time_t         currentIntervalTime         = 0;
    INT            intervalLength       = -1;
    INT            currentInterval      = -1;
    INT            recordLength         = -1;

    INT            programNumber, numActiveChannels;
    INT            energyRegister       = -1;
    INT            pulseCount           = -1;
    FLOAT          pulseWeight          = -1.0;
    BYTE           *pulseData           = NULL;
    BYTE           *pulseTemp           = NULL;

    ULONG          goodPoint = !NORMAL;
    ULONG          lastLPTime;
    ULONG          startingLPTime = getLastLPTime().seconds() - rwEpoch;
    RWTime         intervalTime;

    BOOL           regTypeSupported = FALSE;

    DOUBLE        pValue;
    ULONG          i, j;
    CHAR           buffer[60];


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

    // initialize
    currentInterval   = (INT)fulcrumMMConfig->CurrentInterval;
    intervalLength    = (INT)fulcrumMMConfig->IntervalLength;
    recordLength      = (INT)fulcrumMMConfig->RecordLength;
    numActiveChannels = (INT)fulcrumMMConfig->NumberOfChannels;
    pulseData         = ((BYTE*)(&fulcrumLProfile->MMBuffer)) + (3 + 8 + (6 * numActiveChannels));

    if ((INT)fulcrumLProfile->MMBuffer[0] == 0)
    {
        numberOfIntervals = currentInterval;
    }
    else
    {
        numberOfIntervals = 60;
    }

    for (programNumber = 0; programNumber < numActiveChannels; programNumber++)
    {
        // initialize for each channel
        lastLPTime = startingLPTime;

        // needs to be reset each time through the loop
        goodPoint = !NORMAL;

        energyRegister = (INT)bytesToBase10(((BYTE*)&(fulcrumMMConfig->Program[programNumber].EnergyRegisterNumber)), 2);

        switch (energyRegister)
        {
            case 0:  // Wh
                {
                    if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_KW, AnalogPointType)) != NULL)
                        goodPoint = NORMAL;

                    regTypeSupported = TRUE;
                    break;
                }
            case 1:  // Lagging VARh
                {
                    if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_KVAR, AnalogPointType)) != NULL)
                        goodPoint = NORMAL;

                    regTypeSupported = TRUE;
                    break;
                }
            case 2:  // VAh
                {
                    if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_KVA, AnalogPointType)) != NULL)
                        goodPoint = NORMAL;

                    regTypeSupported = TRUE;
                    break;
                }
            case 3:  // Qh
            case 4:  // Lead or Total VARh
            case 5:  // Volt-squared hour
            case 6:  // Amp hour
                {
                    regTypeSupported = FALSE;
                    goodPoint       = !NORMAL;
                    break;
                }
        }

        if (
           regTypeSupported       &&
           (goodPoint == NORMAL)
           )
        {
            pulseTemp      = pulseData;
            pulseWeight    = FltLittleEndian(&fulcrumMMConfig->Program[programNumber].PulseWeight);

            for (i = 0; i < numberOfIntervals; i++)
            {
                currentIntervalTime = (fulcrumLProfile->RecordTime + (intervalLength * 60 * (i+1)));

                if (lastLPTime < currentIntervalTime)
                {
                    pulseCount = (INT)nibblesAndBits(pulseTemp, numActiveChannels, programNumber, i);
                    intervalTime = RWTime(currentIntervalTime + rwEpoch);

                    pValue = ((60.0 / (DOUBLE)intervalLength) * (DOUBLE)pulseWeight * (DOUBLE)pulseCount) / 1000.0;
                    pValue = pNumericPoint->computeValueForUOM(pValue);

                    if (verifyAndAddPointToReturnMsg (pNumericPoint->getPointID(),
                                                      pValue,
                                                      NormalQuality,
                                                      intervalTime,
                                                      pPIL,
                                                      TAG_POINT_LOAD_PROFILE_DATA))
                    {
                        lastLPTime = currentIntervalTime;

                        // save the last profile interval
                        if ((lastLPTime + rwEpoch) > getLastLPTime())
                        {
                            setLastLPTime (RWTime(lastLPTime + rwEpoch));
                        }
                    }
                }
            }
            /***************************
            *  we add the last intervals of each program to a regular
            *  point change message allowing us to display the last interals
            *  in a report because dispatch does not route load profile data
            *  to clients
            ****************************
            */
            verifyAndAddPointToReturnMsg (pNumericPoint->getPointID(),
                                          pValue,
                                          NormalQuality,
                                          intervalTime,
                                          pPIL);

        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Load Profile register type " << energyRegister << " type not supported" << getName() << endl;
            }
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
    // set this to null no matter what
    pPIL = NULL;

    return NORMAL;
}

// Routine to display or print the message
INT CtiDeviceFulcrum::ResultDisplay (INMESS *InMessage)

{
    ULONG          i, j;
    DIALUPREPLY    *DUPRep = &InMessage->Buffer.DUPSt.DUPRep;
    FulcrumScanData_t  *Fsd = (FulcrumScanData_t*) InMessage->Buffer.DUPSt.DUPRep.Message;
     CHAR buffer[200];

     /**************************
     * lazy way to do this
     ***************************
     */
     {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Result display for device " << getName() << " in progress " << endl;
        sprintf(buffer,"Meter ID          :   %.9s ",Fsd->MeterId);
        dout << endl << buffer << endl;
        sprintf(buffer,"Unit Type         :   %.3s ",Fsd->UnitType);
        dout << buffer << endl;
        sprintf(buffer,"Unit  ID          :   %.8s ",Fsd->UnitId);
        dout << buffer << endl;
        sprintf(buffer,"Time              :   %02d:%02d:%02d ",Fsd->TimeDate.Hours, Fsd->TimeDate.Minutes, Fsd->TimeDate.Seconds);
        dout << buffer << endl;
        sprintf(buffer,"Date              :   %02d/%02d/%02d ",Fsd->TimeDate.Month, Fsd->TimeDate.DayOfMonth, Fsd->TimeDate.Year);
        dout << buffer << endl;

        sprintf(buffer,"Register Multiplier:  %.5f ",Fsd->RegisterMultiplier);
        dout << buffer << endl;

        sprintf(buffer,"                               Watthour     VARhour      VAhour ");
        dout << buffer << endl;
        sprintf(buffer,"Energy Rate E              = %9.2f,  %9.2f,  %9.2f ",Fsd->Watthour.Energy.RateE_Energy ,Fsd->VARhourLag.Energy.RateE_Energy ,Fsd->VAhour.Energy.RateE_Energy);
        dout << buffer << endl;
        sprintf(buffer,"Energy Rate A              = %9.2f,  %9.2f,  %9.2f ",Fsd->Watthour.Energy.RateA_Energy ,Fsd->VARhourLag.Energy.RateA_Energy ,Fsd->VAhour.Energy.RateA_Energy);
        dout << buffer << endl;
        sprintf(buffer,"Energy Rate B              = %9.2f,  %9.2f,  %9.2f ",Fsd->Watthour.Energy.RateB_Energy ,Fsd->VARhourLag.Energy.RateB_Energy ,Fsd->VAhour.Energy.RateB_Energy);
        dout << buffer << endl;
        sprintf(buffer,"Energy Rate C              = %9.2f,  %9.2f,  %9.2f ",Fsd->Watthour.Energy.RateC_Energy ,Fsd->VARhourLag.Energy.RateC_Energy ,Fsd->VAhour.Energy.RateC_Energy);
        dout << buffer << endl;
        sprintf(buffer,"Energy Rate D              = %9.2f,  %9.2f,  %9.2f ",Fsd->Watthour.Energy.RateD_Energy ,Fsd->VARhourLag.Energy.RateD_Energy ,Fsd->VAhour.Energy.RateD_Energy);
        dout << buffer << endl;
        sprintf(buffer,"Energy Rate E Intermediate = %9.2f,  %9.2f,  %9.2f ",Fsd->Watthour.RateE_IntEnergy     ,Fsd->VARhourLag.RateE_IntEnergy     ,Fsd->VAhour.RateE_IntEnergy);
        dout << buffer << endl;


        sprintf(buffer,"Power Factor ");
        dout << endl << buffer << endl;
        sprintf(buffer,"Instantaneous              = %f ",Fsd->PowerFactor.INSTPF);
        dout << buffer << endl;
        sprintf(buffer,"Average                    = %f ",Fsd->PowerFactor.AVGPF);
        dout << buffer << endl;

        sprintf(buffer,"Demand                         Watts,       Lag VAR,      VA ");
        dout << endl << buffer << endl;
        sprintf(buffer,"Instantaneous               = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.Instantaneous               ,Fsd->VARLagDemand.Instantaneous               ,Fsd->VADemand.Instantaneous);
        dout << buffer << endl;
        sprintf(buffer,"Previous Interval Total     = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.TotalPreviousInterval       ,Fsd->VARLagDemand.TotalPreviousInterval       ,Fsd->VADemand.TotalPreviousInterval);
        dout << buffer << endl;
        sprintf(buffer,"Rate A Maximum              = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.A_Maximum.PeakValue       ,Fsd->VARLagDemand.A_Maximum.PeakValue       ,Fsd->VADemand.A_Maximum.PeakValue);
        dout << buffer << endl;
        sprintf(buffer,"Rate B Maximum              = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.B_Maximum.PeakValue       ,Fsd->VARLagDemand.B_Maximum.PeakValue       ,Fsd->VADemand.B_Maximum.PeakValue);
        dout << buffer << endl;
        sprintf(buffer,"Rate C Maximum              = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.C_Maximum.PeakValue       ,Fsd->VARLagDemand.C_Maximum.PeakValue       ,Fsd->VADemand.C_Maximum.PeakValue);
        dout << buffer << endl;
        sprintf(buffer,"Rate D Maximum              = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.D_Maximum.PeakValue       ,Fsd->VARLagDemand.D_Maximum.PeakValue       ,Fsd->VADemand.D_Maximum.PeakValue);
        dout << buffer << endl;
        sprintf(buffer,"Peak #1                     = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.Peak1.PeakValue           ,Fsd->VARLagDemand.Peak1.PeakValue           ,Fsd->VADemand.Peak1.PeakValue);
        dout << buffer << endl;
        sprintf(buffer,"Peak #2                     = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.Peak2.PeakValue           ,Fsd->VARLagDemand.Peak2.PeakValue           ,Fsd->VADemand.Peak2.PeakValue);
        dout << buffer << endl;
        sprintf(buffer,"Peak #3                     = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.Peak3.PeakValue           ,Fsd->VARLagDemand.Peak3.PeakValue           ,Fsd->VADemand.Peak3.PeakValue);
        dout << buffer << endl;
        sprintf(buffer,"Peak #4                     = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.Peak4.PeakValue           ,Fsd->VARLagDemand.Peak4.PeakValue           ,Fsd->VADemand.Peak4.PeakValue);
        dout << buffer << endl;
        sprintf(buffer,"Peak #5                     = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.Peak5.PeakValue           ,Fsd->VARLagDemand.Peak5.PeakValue           ,Fsd->VADemand.Peak5.PeakValue);
        dout << buffer << endl;
        sprintf(buffer,"Coincident                  = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.Coincident                  ,Fsd->VARLagDemand.Coincident                  ,Fsd->VADemand.Coincident);
        dout << buffer << endl;
        sprintf(buffer,"Total Cumulative            = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.TotalCumulative             ,Fsd->VARLagDemand.TotalCumulative             ,Fsd->VADemand.TotalCumulative);
        dout << buffer << endl;
        sprintf(buffer,"Rate A Cumulative           = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.A_Cumulative                ,Fsd->VARLagDemand.A_Cumulative                ,Fsd->VADemand.A_Cumulative);
        dout << buffer << endl;
        sprintf(buffer,"Rate B Cumulative           = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.B_Cumulative                ,Fsd->VARLagDemand.B_Cumulative                ,Fsd->VADemand.B_Cumulative);
        dout << buffer << endl;
        sprintf(buffer,"Rate C Cumulative           = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.C_Cumulative                ,Fsd->VARLagDemand.C_Cumulative                ,Fsd->VADemand.C_Cumulative);
        dout << buffer << endl;
        sprintf(buffer,"Rate D Cumulative           = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.D_Cumulative                ,Fsd->VARLagDemand.D_Cumulative                ,Fsd->VADemand.D_Cumulative);
        dout << buffer << endl;
        sprintf(buffer,"Total Continuous Cumulative = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.TotalContinuousCumulative   ,Fsd->VARLagDemand.TotalContinuousCumulative   ,Fsd->VADemand.TotalContinuousCumulative);
        dout << buffer << endl;
        sprintf(buffer,"Rate A Continuous Cum.      = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.A_ContinuousCumulative      ,Fsd->VARLagDemand.A_ContinuousCumulative      ,Fsd->VADemand.A_ContinuousCumulative);
        dout << buffer << endl;
        sprintf(buffer,"Rate B Continuous Cum.      = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.B_ContinuousCumulative      ,Fsd->VARLagDemand.B_ContinuousCumulative      ,Fsd->VADemand.B_ContinuousCumulative);
        dout << buffer << endl;
        sprintf(buffer,"Rate C Continuous Cum.      = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.C_ContinuousCumulative      ,Fsd->VARLagDemand.C_ContinuousCumulative      ,Fsd->VADemand.C_ContinuousCumulative);
        dout << buffer << endl;
        sprintf(buffer,"Rate D Continuous Cum.      = %9.2f,  %9.2f,  %9.2f ",Fsd->WattsDemand.D_ContinuousCumulative      ,Fsd->VARLagDemand.D_ContinuousCumulative      ,Fsd->VADemand.D_ContinuousCumulative);
        dout << buffer << endl << endl;
     }
//#endif


    return(NORMAL);
}


INT CtiDeviceFulcrum::allocateDataBins  (OUTMESS *outMess)
{
    if (_dataBuffer == NULL)
        _dataBuffer = CTIDBG_new BYTE[sizeof (FulcrumScanData_t)];

    if (_loadProfileTimeDate == NULL)
        _loadProfileTimeDate = CTIDBG_new BYTE[sizeof (FulcrumRealTimeRegister_t)];

    if (_loadProfileBuffer == NULL)
    {
        _loadProfileBuffer = CTIDBG_new BYTE[sizeof (FulcrumLoadProfileMessage_t)];

        if (_loadProfileBuffer != NULL)
        {
            ((FulcrumLoadProfileMessage_t *)_loadProfileBuffer)->porterLPTime = outMess->Buffer.DUPReq.LP_Time;
        }
    }

    // point to mm in the load profile buffer
    if (_massMemoryConfig == NULL)
    {
        if (_loadProfileBuffer != NULL)
        {
            // aaahhhh that is a nasty cast
            _massMemoryConfig = (BYTE *)&(((FulcrumLoadProfileMessage_t *)_loadProfileBuffer)->MMConfig);
        }
    }

    if (_massMemoryRequestInputs == NULL)
        _massMemoryRequestInputs = CTIDBG_new BYTE[sizeof (SchlumbergerLProfileInput_t)];


    /* NOTE:  The massMemoryLoadProfile block is initialized in the state machine
              and removed in the free memory block function
     */

    // set the command based in the out message command
    setTotalByteCount (0);
    setCurrentCommand ((CtiDeviceIED::CtiMeterCmdStates_t)outMess->Buffer.DUPReq.Command[0]);
    setCurrentState (StateHandshakeInitialize);
    return NORMAL;
}

INT CtiDeviceFulcrum::reformatDataBuffer(BYTE *aInMessBuffer, ULONG &aTotalBytes)

{
    /*
     *  Well God bless 'em but Intel is still Little Endian so we shuffle the bytes here
     */

    ShortLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->DemandResetCount);
    ShortLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->OutageCount);
    ShortLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->PhaseOutageCount);

    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->RegisterMultiplier);

    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Watthour.Energy.RateE_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Watthour.Energy.RateA_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Watthour.Energy.RateB_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Watthour.Energy.RateC_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Watthour.Energy.RateD_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Watthour.RateE_IntEnergy);

    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARhourLag.Energy.RateE_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARhourLag.Energy.RateA_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARhourLag.Energy.RateB_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARhourLag.Energy.RateC_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARhourLag.Energy.RateD_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARhourLag.RateE_IntEnergy);

    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VAhour.Energy.RateE_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VAhour.Energy.RateA_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VAhour.Energy.RateB_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VAhour.Energy.RateC_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VAhour.Energy.RateD_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VAhour.RateE_IntEnergy);

    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Qhour.Energy.RateE_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Qhour.Energy.RateA_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Qhour.Energy.RateB_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Qhour.Energy.RateC_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Qhour.Energy.RateD_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Qhour.RateE_IntEnergy);

    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->V2hour.Energy.RateE_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->V2hour.Energy.RateA_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->V2hour.Energy.RateB_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->V2hour.Energy.RateC_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->V2hour.Energy.RateD_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->V2hour.RateE_IntEnergy);

    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Amphour.Energy.RateE_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Amphour.Energy.RateA_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Amphour.Energy.RateB_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Amphour.Energy.RateC_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Amphour.Energy.RateD_Energy);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->Amphour.RateE_IntEnergy);

    //fprintf(stderr,"Watts Demand\n");
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.TotalMaximum.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.A_Maximum.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.B_Maximum.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.C_Maximum.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.D_Maximum.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.Peak1.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.Peak2.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.Peak3.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.Peak4.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.Peak5.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.Instantaneous);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.TotalPreviousInterval);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.Coincident);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.TotalCumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.A_Cumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.B_Cumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.C_Cumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.D_Cumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.TotalContinuousCumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.A_ContinuousCumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.B_ContinuousCumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.C_ContinuousCumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->WattsDemand.D_ContinuousCumulative);

    //fprintf(stderr,"VARLag Demand\n");
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.TotalMaximum.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.A_Maximum.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.B_Maximum.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.C_Maximum.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.D_Maximum.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.Peak1.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.Peak2.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.Peak3.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.Peak4.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.Peak5.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.Instantaneous);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.TotalPreviousInterval);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.Coincident);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.TotalCumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.A_Cumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.B_Cumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.C_Cumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.D_Cumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.TotalContinuousCumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.A_ContinuousCumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.B_ContinuousCumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.C_ContinuousCumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VARLagDemand.D_ContinuousCumulative);

    //fprintf(stderr,"VA Demand\n");
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.TotalMaximum.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.A_Maximum.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.B_Maximum.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.C_Maximum.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.D_Maximum.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.Peak1.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.Peak2.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.Peak3.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.Peak4.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.Peak5.PeakValue);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.Instantaneous);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.TotalPreviousInterval);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.Coincident);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.TotalCumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.A_Cumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.B_Cumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.C_Cumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.D_Cumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.TotalContinuousCumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.A_ContinuousCumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.B_ContinuousCumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.C_ContinuousCumulative);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->VADemand.D_ContinuousCumulative);

    FltLittleEndian (&((FulcrumScanData_t *)_dataBuffer)->PowerFactor.INSTPF);
    FltLittleEndian (&((FulcrumScanData_t *)_dataBuffer)->PowerFactor.AVGPF);

    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->InstReg.TotalVolts);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->InstReg.A_Volts);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->InstReg.B_Volts);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->InstReg.C_Volts);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->InstReg.TotalAmps);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->InstReg.A_Amps);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->InstReg.B_Amps);
    FltLittleEndian(&((FulcrumScanData_t *)_dataBuffer)->InstReg.C_Amps);

    memcpy(aInMessBuffer, _dataBuffer, sizeof(FulcrumScanData_t));
    aTotalBytes = getTotalByteCount();
    return NORMAL;
}


INT CtiDeviceFulcrum::copyLoadProfileData(BYTE *aInMessBuffer, ULONG &aTotalBytes)
{
    memcpy(aInMessBuffer, _loadProfileBuffer, sizeof (FulcrumLoadProfileMessage_t));
    aTotalBytes = sizeof (FulcrumLoadProfileMessage_t);

    return NORMAL;
}

BOOL CtiDeviceFulcrum::getMeterDataFromScanStruct (int aOffset, DOUBLE &aValue, RWTime &peak,  FulcrumScanData_t *aScanData)
{
    BOOL isValidPoint = FALSE;

    // this is initial value
    peak = rwEpoch;

    /* Get the value from InMessage */
    switch (aOffset)
    {
        case OFFSET_TOTAL_KWH:
        case OFFSET_RATE_E_KWH:
            {
                aValue = aScanData->Watthour.Energy.RateE_Energy / 1000.0;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_PEAK_KW_OR_RATE_A_KW:
            {
                // check if we have a valid date
                if (aScanData->WattsDemand.A_Maximum.Month > 0 &&
                    aScanData->WattsDemand.A_Maximum.Month < 13)
                {
                    peak = RWTime (RWDate (aScanData->WattsDemand.A_Maximum.Day,
                                           aScanData->WattsDemand.A_Maximum.Month,
                                           aScanData->WattsDemand.A_Maximum.Year+2000),
                                   aScanData->WattsDemand.A_Maximum.Hour,
                                   aScanData->WattsDemand.A_Maximum.Minute,
                                   0);
                    aValue = aScanData->WattsDemand.A_Maximum.PeakValue / 1000.0;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_A_KWH:
            {
                aValue = aScanData->Watthour.Energy.RateA_Energy / 1000.0;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_RATE_B_KW:
            {
                // check if we have a valid date
                if (aScanData->WattsDemand.B_Maximum.Month > 0 &&
                    aScanData->WattsDemand.B_Maximum.Month < 13)
                {
                    peak = RWTime (RWDate (aScanData->WattsDemand.B_Maximum.Day,
                                           aScanData->WattsDemand.B_Maximum.Month,
                                           aScanData->WattsDemand.B_Maximum.Year+2000),
                                   aScanData->WattsDemand.B_Maximum.Hour,
                                   aScanData->WattsDemand.B_Maximum.Minute,
                                   0);
                    aValue = aScanData->WattsDemand.B_Maximum.PeakValue / 1000.0;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_B_KWH:
            {
                aValue = aScanData->Watthour.Energy.RateB_Energy / 1000.0;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_RATE_C_KW:
            {
                // check if we have a valid date
                if (aScanData->WattsDemand.C_Maximum.Month > 0 &&
                    aScanData->WattsDemand.C_Maximum.Month < 13)
                {
                    peak = RWTime (RWDate (aScanData->WattsDemand.C_Maximum.Day,
                                           aScanData->WattsDemand.C_Maximum.Month,
                                           aScanData->WattsDemand.C_Maximum.Year+2000),
                                   aScanData->WattsDemand.C_Maximum.Hour,
                                   aScanData->WattsDemand.C_Maximum.Minute,
                                   0);
                    aValue = aScanData->WattsDemand.C_Maximum.PeakValue / 1000.0;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_C_KWH:
            {
                aValue = aScanData->Watthour.Energy.RateC_Energy / 1000.0;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_RATE_D_KW:
            {
                // check if we have a valid date
                if (aScanData->WattsDemand.D_Maximum.Month > 0 &&
                    aScanData->WattsDemand.D_Maximum.Month < 13)
                {
                    peak = RWTime (RWDate (aScanData->WattsDemand.D_Maximum.Day,
                                           aScanData->WattsDemand.D_Maximum.Month,
                                           aScanData->WattsDemand.D_Maximum.Year+2000),
                                   aScanData->WattsDemand.D_Maximum.Hour,
                                   aScanData->WattsDemand.D_Maximum.Minute,
                                   0);
                    aValue = aScanData->WattsDemand.D_Maximum.PeakValue / 1000.0;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_D_KWH:
            {
                aValue = aScanData->Watthour.Energy.RateD_Energy / 1000.0;
                isValidPoint = TRUE;
                break;
            }

        case OFFSET_RATE_E_KW:
            {
                // check if we have a valid date
                if (aScanData->WattsDemand.Peak1.Month > 0 &&
                    aScanData->WattsDemand.Peak1.Month < 13)
                {
                    peak = RWTime (RWDate (aScanData->WattsDemand.Peak1.Day,
                                           aScanData->WattsDemand.Peak1.Month,
                                           aScanData->WattsDemand.Peak1.Year+2000),
                                   aScanData->WattsDemand.Peak1.Hour,
                                   aScanData->WattsDemand.Peak1.Minute,
                                   0);
                    aValue = aScanData->WattsDemand.Peak1.PeakValue / 1000.0;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW:
            {
                aValue = aScanData->WattsDemand.TotalPreviousInterval / 1000.0;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_RATE_E_KVARH:
        case OFFSET_TOTAL_KVARH:
            {
                aValue = aScanData->VARhourLag.Energy.RateE_Energy / 1000.0;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_PEAK_KVAR_OR_RATE_A_KVAR:
            {
                // check if we have a valid date
                if (aScanData->VARLagDemand.A_Maximum.Month > 0 &&
                    aScanData->VARLagDemand.A_Maximum.Month < 13)
                {
                    peak = RWTime (RWDate (aScanData->VARLagDemand.A_Maximum.Day,
                                           aScanData->VARLagDemand.A_Maximum.Month,
                                           aScanData->VARLagDemand.A_Maximum.Year+2000),
                                   aScanData->VARLagDemand.A_Maximum.Hour,
                                   aScanData->VARLagDemand.A_Maximum.Minute,
                                   0);
                    aValue = aScanData->VARLagDemand.A_Maximum.PeakValue / 1000.0;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_A_KVARH:
            {
                {
                    aValue = aScanData->VARhourLag.Energy.RateA_Energy / 1000.0;
                    isValidPoint = TRUE;
                    break;
                }
            }
        case OFFSET_RATE_B_KVAR:
            {
                // check if we have a valid date
                if (aScanData->VARLagDemand.B_Maximum.Month > 0 &&
                    aScanData->VARLagDemand.B_Maximum.Month < 13)
                {
                    peak = RWTime (RWDate (aScanData->VARLagDemand.B_Maximum.Day,
                                           aScanData->VARLagDemand.B_Maximum.Month,
                                           aScanData->VARLagDemand.B_Maximum.Year+2000),
                                   aScanData->VARLagDemand.B_Maximum.Hour,
                                   aScanData->VARLagDemand.B_Maximum.Minute,
                                   0);
                    aValue = aScanData->VARLagDemand.B_Maximum.PeakValue / 1000.0;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_B_KVARH:
            {
                aValue = aScanData->VARhourLag.Energy.RateB_Energy / 1000.0;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_RATE_C_KVAR:
            {
                // check if we have a valid date
                if (aScanData->VARLagDemand.C_Maximum.Month > 0 &&
                    aScanData->VARLagDemand.C_Maximum.Month < 13)
                {
                    peak = RWTime (RWDate (aScanData->VARLagDemand.C_Maximum.Day,
                                           aScanData->VARLagDemand.C_Maximum.Month,
                                           aScanData->VARLagDemand.C_Maximum.Year+2000),
                                   aScanData->VARLagDemand.C_Maximum.Hour,
                                   aScanData->VARLagDemand.C_Maximum.Minute,
                                   0);
                    aValue = aScanData->VARLagDemand.C_Maximum.PeakValue / 1000.0;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_C_KVARH:
            {
                aValue = aScanData->VARhourLag.Energy.RateC_Energy / 1000.0;
                isValidPoint = TRUE;
                break;
            }

        case OFFSET_RATE_D_KVAR:
            {
                // check if we have a valid date
                if (aScanData->VARLagDemand.D_Maximum.Month > 0 &&
                    aScanData->VARLagDemand.D_Maximum.Month < 13)
                {
                    peak = RWTime (RWDate (aScanData->VARLagDemand.D_Maximum.Day,
                                           aScanData->VARLagDemand.D_Maximum.Month,
                                           aScanData->VARLagDemand.D_Maximum.Year+2000),
                                   aScanData->VARLagDemand.D_Maximum.Hour,
                                   aScanData->VARLagDemand.D_Maximum.Minute,
                                   0);
                    aValue = aScanData->VARLagDemand.D_Maximum.PeakValue / 1000.0;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_D_KVARH:
            {
                aValue = aScanData->VARhourLag.Energy.RateD_Energy / 1000.0;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_RATE_E_KVAR:
            {
                // check if we have a valid date
                if (aScanData->VARLagDemand.Peak1.Month > 0 &&
                    aScanData->VARLagDemand.Peak1.Month < 13)
                {
                    peak = RWTime (RWDate (aScanData->VARLagDemand.Peak1.Day,
                                           aScanData->VARLagDemand.Peak1.Month,
                                           aScanData->VARLagDemand.Peak1.Year+2000),
                                   aScanData->VARLagDemand.Peak1.Hour,
                                   aScanData->VARLagDemand.Peak1.Minute,
                                   0);
                    aValue = aScanData->VARLagDemand.Peak1.PeakValue / 1000.0;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR:
            {
                aValue = aScanData->VARLagDemand.TotalPreviousInterval / 1000.0;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_RATE_E_KVAH:
        case OFFSET_TOTAL_KVAH:
            {
                aValue = aScanData->VAhour.Energy.RateE_Energy / 1000.0;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_PEAK_KVA_OR_RATE_A_KVA:
            {
                // check if we have a valid date
                if (aScanData->VADemand.A_Maximum.Month > 0 &&
                    aScanData->VADemand.A_Maximum.Month < 13)
                {
                    peak = RWTime (RWDate (aScanData->VADemand.A_Maximum.Day,
                                           aScanData->VADemand.A_Maximum.Month,
                                           aScanData->VADemand.A_Maximum.Year+2000),
                                   aScanData->VADemand.A_Maximum.Hour,
                                   aScanData->VADemand.A_Maximum.Minute,
                                   0);
                    aValue = aScanData->VADemand.A_Maximum.PeakValue / 1000.0;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_A_KVAH:
            {
                aValue = aScanData->VAhour.Energy.RateA_Energy / 1000.0;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_RATE_B_KVA:
            {
                // check if we have a valid date
                if (aScanData->VADemand.B_Maximum.Month > 0 &&
                    aScanData->VADemand.B_Maximum.Month < 13)
                {
                    peak = RWTime (RWDate (aScanData->VADemand.B_Maximum.Day,
                                           aScanData->VADemand.B_Maximum.Month,
                                           aScanData->VADemand.B_Maximum.Year+2000),
                                   aScanData->VADemand.B_Maximum.Hour,
                                   aScanData->VADemand.B_Maximum.Minute,
                                   0);
                    aValue = aScanData->VADemand.B_Maximum.PeakValue / 1000.0;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_B_KVAH:
            {
                aValue = aScanData->VAhour.Energy.RateB_Energy / 1000.0;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_RATE_C_KVA:
            {
                // check if we have a valid date
                if (aScanData->VADemand.C_Maximum.Month > 0 &&
                    aScanData->VADemand.C_Maximum.Month < 13)
                {
                    peak = RWTime (RWDate (aScanData->VADemand.C_Maximum.Day,
                                           aScanData->VADemand.C_Maximum.Month,
                                           aScanData->VADemand.C_Maximum.Year+2000),
                                   aScanData->VADemand.C_Maximum.Hour,
                                   aScanData->VADemand.C_Maximum.Minute,
                                   0);
                    aValue = aScanData->VADemand.C_Maximum.PeakValue / 1000.0;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_C_KVAH:
            {
                aValue = aScanData->VAhour.Energy.RateC_Energy / 1000.0;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_RATE_D_KVA:
            {
                // check if we have a valid date
                if (aScanData->VADemand.D_Maximum.Month > 0 &&
                    aScanData->VADemand.D_Maximum.Month < 13)
                {
                    peak = RWTime (RWDate (aScanData->VADemand.D_Maximum.Day,
                                           aScanData->VADemand.D_Maximum.Month,
                                           aScanData->VADemand.D_Maximum.Year+2000),
                                   aScanData->VADemand.D_Maximum.Hour,
                                   aScanData->VADemand.D_Maximum.Minute,
                                   0);
                    aValue = aScanData->VADemand.D_Maximum.PeakValue / 1000.0;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_D_KVAH:
            {
                aValue = aScanData->VAhour.Energy.RateD_Energy / 1000.0;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_RATE_E_KVA:
            {
                // check if we have a valid date
                if (aScanData->VADemand.Peak1.Month > 0 &&
                    aScanData->VADemand.Peak1.Month < 13)
                {
                    peak = RWTime (RWDate (aScanData->VADemand.Peak1.Day,
                                           aScanData->VADemand.Peak1.Month,
                                           aScanData->VADemand.Peak1.Year+2000),
                                   aScanData->VADemand.Peak1.Hour,
                                   aScanData->VADemand.Peak1.Minute,
                                   0);
                    aValue = aScanData->VADemand.Peak1.PeakValue / 1000.0;
                    isValidPoint = TRUE;
                }
                break;
            }


        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA:
            {
                aValue = aScanData->VADemand.TotalPreviousInterval / 1000.0;
                isValidPoint = TRUE;
                break;
            }

        case OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE:
            {
                aValue = aScanData->InstReg.A_Volts;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE:
            {
                aValue = aScanData->InstReg.B_Volts;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE:
            {
                aValue = aScanData->InstReg.C_Volts;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_INSTANTANEOUS_PHASE_A_CURRENT:
            {
                aValue = aScanData->InstReg.A_Amps;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_INSTANTANEOUS_PHASE_B_CURRENT:
            {
                aValue = aScanData->InstReg.B_Amps;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_INSTANTANEOUS_PHASE_C_CURRENT:
            {
                aValue = aScanData->InstReg.C_Amps;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT:
        default:
            isValidPoint = FALSE;
            break;
    }
    return isValidPoint;
}
