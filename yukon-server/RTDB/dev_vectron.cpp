/*-----------------------------------------------------------------------------*
*
* File:   dev_vectron
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_vectron.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2004/10/12 20:14:17 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )


#include <math.h>
#include <rw/rwtime.h>
#include <rw/rwdate.h>

#include "yukon.h"
#include "porter.h"
#include "dev_schlum.h"
#include "dev_vectron.h"

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

#include "logger.h"
#include "guard.h"

#include "numstr.h"

/*
 * Command Char, Command Name, Sent bytes (less CRC), Rec. Bytes (less CRC)
 */

// note if the start and stop address stays the same, we're reading from the same block
SchlumbergerCommandBlk_t   vectronScanCommands[] =
{
    {0x1B1F,             0x1B36,        (0x1B1F-0x1B1F),         24,      "Voltage/Amps  Instantaneous"},
    {0x1D18,             0x1D9D,        (0x1D18-0x1D18),         11,      "Unit Type/ID"},
    {0x1D18,             0x1D9D,        (0x1D28-0x1D18),          4,      "Register Multiplier"},
    {0x1D18,             0x1D9D,        (0x1D98-0x1D18),          6,      "Register Mapping"},
    {0x2118,             0x21DF,        (0x2118-0x2118),          8,      "Reg 2 Rate E Demand or Energy"},
    {0x2118,             0x21DF,        (0x2120-0x2118),         12,      "Reg 1 Rate E Max Demand, Cum"},
    {0x2118,             0x21DF,        (0x2130-0x2118),          4,      "Demand Reset Count/Outage Count"},
    {0x2118,             0x21DF,        (0x2134-0x2118),          4,      "Reg 3 Rate E Cum"},
    {0x2118,             0x21DF,        (0x2138-0x2118),          8,      "Reg 1 Rate A Max Demand"},
    {0x2118,             0x21DF,        (0x2140-0x2118),          4,      "Reg 4 Rate E Cum"},
    {0x2118,             0x21DF,        (0x2144-0x2118),          8,      "Reg 1 Rate B Max Demand"},
    {0x2118,             0x21DF,        (0x2154-0x2118),          8,      "Reg 1 Rate C Max Demand"},
    {0x2118,             0x21DF,        (0x2164-0x2118),          8,      "Reg 1 Rate D Max Demand"},
    {0x2118,             0x21DF,        (0x2172-0x2118),         28,      "Reg 2 Rate A,B Demand or A-D Energy"},
    {0x2118,             0x21DF,        (0x21CB-0x2118),          1,      "Register Flags"},
    {0x2118,             0x21DF,        (0x21D0-0x2118),          8,      "Reg 3 Rate E Demand or Energy"},
    {0x2118,             0x21DF,        (0x21D8-0x2118),          8,      "Reg 4 Rate E Demand or Energy"},
    {0x2201,             0x222A,        (0x2201-0x2201),          4,      "Software/Firmware Revision"},
    {0x2201,             0x222A,        (0x2220-0x2201),         11,      "ProgramID/MeterID"},
    {NULL,               NULL,          NULL,            0,      ""}     // last command packet
};



// local functions to this file only
DOUBLE VectronBCDtoDouble(UCHAR* buffer, ULONG len);
CHAR * displayRegisterMapping (USHORT map, CHAR *retString);

INT CtiDeviceVectron::getCommandPacket () const
{
    return iCommandPacket;
}

CtiDeviceVectron& CtiDeviceVectron::setCommandPacket (INT aCmd)
{
    iCommandPacket = aCmd;
    return *this;
}


INT CtiDeviceVectron::GeneralScan(CtiRequestMsg *pReq,
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
        OutMessage->Buffer.DUPReq.Identity = IDENT_VECTRON;
        status = Inherited::GeneralScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority);
        return status;
    }
    else
    {
        return MEMORY;
    }
}



INT CtiDeviceVectron::generateCommandHandshake (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList)
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
                /*
                 * OK, now we need to send the security code to habla con este
                 * muhere gorda
                 */
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


INT CtiDeviceVectron::generateCommand (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList )
{
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
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " (" << __LINE__ << ") Invalid command " << getCurrentCommand() << " scanning " << getName() << endl;
                }
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState (StateScanAbort);
                retCode = StateScanAbort;
                break;
            }

    }
    return retCode;
}

INT CtiDeviceVectron::generateCommandSelectMeter (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList)
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
                fillUploadTransferObject (Transfer, 0x1B03, 0x1B03);
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
                fillUploadTransferObject (Transfer, 0x262B, 0x262D);
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


INT CtiDeviceVectron::generateCommandScan (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList)
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
            {
                setRetryAttempts(SCHLUMBERGER_RETRIES);
                setCommandPacket(0);
                setTotalByteCount (0);
            }
        case StateScanValueSet1FirstScan:  // done to set attempts only first time thru DLS
            {
                setAttemptsRemaining(getRetryAttempts()); // = SclumbergerRetries;
            }
        case StateScanValueSet1:
            {
                fillUploadTransferObject (Transfer,
                                          vectronScanCommands[getCommandPacket()].startAddress,
                                          vectronScanCommands[getCommandPacket()].stopAddress);

                setPreviousState (StateScanValueSet1);
                setCurrentState (StateScanDecode1);
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

INT CtiDeviceVectron::generateCommandLoadProfile (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList )
{
    // make this easier to read in the following code
    SchlumbergerLProfileInput_t *localMMInputs      = ((SchlumbergerLProfileInput_t *)_massMemoryRequestInputs);
    VectronMMConfig_t           *localMMConfig      = ((VectronMMConfig_t *)_massMemoryConfig);
    SchlLoadProfile_t           *localMMLoadProfile = ((SchlLoadProfile_t *)_massMemoryLoadProfile);
    VectronLoadProfileMessage_t *localLProfile      = ((VectronLoadProfileMessage_t*)_loadProfileBuffer);
    VectronRealTimeRegister_t   *localRTReg         = ((VectronRealTimeRegister_t *)_loadProfileTimeDate);

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
                fillUploadTransferObject (Transfer, 0x20F9, 0x20FF);
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
                // time
                fillUploadTransferObject (Transfer, 0x1D86, 0x1D87);
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
                // grabbing the mass memory configuration
                if ((localLProfile->porterLPTime - rwEpoch) > 0)
                {
                    fillUploadTransferObject (Transfer, 0x2500, 0x2540);
                    setPreviousState (StateScanValueSet3);
                    setCurrentState (StateScanDecode3);
                }
                else
                {
                    Transfer.setOutCount( 0 );
                    Transfer.setInCountExpected( 0 );

                    // we're done, return the scan data
                    setCurrentState (StateScanDecode5);
                }
                break;
            }
        case StateScanValueSet4FirstScan:  // done to set attempts only first time thru DLS
            {
                setAttemptsRemaining(getRetryAttempts()); // = SclumbergerRetries;
            }
        case StateScanValueSet4:
            {
                // initialize mass memory     SCHL_SCAN_STATE_SETUP_MM_INIT
                ULONG LPTime;

                // flag whether meter's time is valid
                bool validMeterClock = true;

                // Number of records which fit in MM
                ULONG MMMaxRecord;

                // Last Valid Address Should be MaxRecord - MaxRecord % RecordLength
                ULONG MMMaxAddress;

                // Convert to values we can use here... (little endian)
                ULONG MMStart   = localMMConfig->Real.logicalStart;
                ULONG MMStop    = localMMConfig->Real.logicalEnd;
                ULONG MMCurrent = localMMConfig->Real.logicalCurrent;

                INT MMCurrentYear = 0;

                // Number of records which fit in MM
                MMMaxRecord = (MMStop - MMStart) / localMMConfig->Real.recordLength;

                // Last Valid Address Should be MaxRecord - MaxRecord % RecordLength
                MMMaxAddress = MMMaxRecord * localMMConfig->Real.recordLength;

                // initialize my inputs
                localMMInputs->MMIndex            = 0L;
                localMMInputs->MMVintage          = 0L;
                localMMInputs->MMCount            = 0L;
                localMMInputs->MMPos              = 0L;
                localMMInputs->MMBlockSize        = 0L;
                localMMInputs->MMScanStartAddress = 0L;

                if (shouldRetrieveLoadProfile (localLProfile->porterLPTime, localMMConfig->Real.intervalLength ))
                {
                    /**************************************
                    * Decide on the preferred block size for our requests....
                    *
                    * one channel is 108 bytes
                    * two channels is 204 bytes
                    *
                    * all plus the ack and 2 byte CRC
                    ***************************************
                    */
                    localMMInputs->MMBlockSize = localMMConfig->Real.recordLength;

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
                                * VectronScan is the Meter Configuration Data brought back in prior scans.
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

                                    LPTime = RWTime (RWDate ((INT)localRTReg->DayOfMonth,
                                                             (INT)localRTReg->Month,
                                                             (INT)localRTReg->Year+2000),
                                                     (INT)localRTReg->Hours, 0, 0).seconds()
                                             + (((INT)localRTReg->Minutes - ((INT)localRTReg->Minutes % (INT)localMMConfig->Real.intervalLength)
                                                 - ((INT)localMMConfig->Real.currentInterval * (INT)localMMConfig->Real.intervalLength)) * 60)
                                             - rwEpoch;

                                }
                                else
                                {
                                    /*
                                     *  Now build a unix time out of the prior settings.
                                     *  NOTE:  This is a UNIX time of RECORD START!
                                     */

                                    LPTime = RWTime (RWDate ((INT)localRTReg->DayOfMonth,
                                                             (INT)localRTReg->Month,
                                                             (INT)localRTReg->Year+1900),
                                                     (INT)localRTReg->Hours, 0, 0).seconds()
                                             + (((INT)localRTReg->Minutes - ((INT)localRTReg->Minutes % (INT)localMMConfig->Real.intervalLength)
                                                 - ((INT)localMMConfig->Real.currentInterval * (INT)localMMConfig->Real.intervalLength)) * 60)
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

                                LPTime = LPTime - ((INT)localMMConfig->Real.intervalLength * 3600 );
                                LPTime = RWTime (RWDate (RWTime(LPTime + rwEpoch)),
                                                 RWTime (LPTime + rwEpoch).hour(),
                                                 0,
                                                 0).seconds() - rwEpoch;

                                localMMLoadProfile[localMMInputs->MMIndex].RecordTime    = LPTime;       // UNIX time of record start!
                                localMMInputs->MMIndex += 1;

                            }

                            // Back 1 Record with the Address.
                            if (validMeterClock)
                                localMMInputs->MMScanStartAddress = previousMassMemoryAddress(MMStart,
                                                                                              localMMInputs->MMScanStartAddress,
                                                                                              MMMaxAddress,
                                                                                              localMMConfig->Real.recordLength);

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

                            setPreviousState (StateScanValueSet4);
                            setCurrentState (StateScanDecode4);
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
                        dout << RWTime() << " Load profile for " << getName() << " will not be collected this scan" << endl;
                    }

                    Transfer.setOutCount( 0 );
                    Transfer.setInCountExpected( 0 );

                    // we're done, return the scan data
                    setCurrentState (StateScanDecode5);
                }
                break;
            }
        case StateScanValueSet5FirstScan:  // done to set attempts only first time thru DLS
            {
                setAttemptsRemaining(getRetryAttempts()); // = SclumbergerRetries;
            }
        case StateScanValueSet5:
            {
                // setup from SCHL_SCAN_DECODE_MM_READ
                /*
                 *  We need to go out and get more data, because we did NOT get it all last time.
                 */

                // Ho much more to get???
                MMTemp = (localMMInputs->MMScanStartAddress + localMMConfig->Real.recordLength) - localMMInputs->MMPos;
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
                setPreviousState (StateScanValueSet5);
                setCurrentState (StateScanDecode4);

                break;
            }

        case StateScanValueSet6FirstScan:  // done to set attempts only first time thru DLS
            {
                setAttemptsRemaining(getRetryAttempts()); // = SclumbergerRetries;
            }
        case StateScanValueSet6:
            {
                // reset buffers since they are re-used everytime
                //  SCHL_SCAN_STATE_SETUP_MM_READ:

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
                        localLProfile->NextRecordTime  = localMMLoadProfile[localMMInputs->MMVintage - localMMInputs->MMIndex - 1].RecordTime;
                    else
                        localLProfile->NextRecordTime  = 0L;  // Only get the CurrentAddress record.

                    setPreviousState (StateScanValueSet5);
                    setCurrentState (StateScanDecode4);
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
                    setCurrentState (StateScanDecode5);
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


INT CtiDeviceVectron::decodeResponseHandshake (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
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
                        dout << RWTime() << " NAK: Vectron " << getName() << " already online"<< endl;
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
                        dout << RWTime() << " ACK: Vectron " << getName() << " already online"<< endl;
                    }
                    break;
                }
                else if (strncmp((CHAR*)Transfer.getInBuffer(), "SI,VECTRON     \r", 10)) // We go in if they are not identical
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


INT CtiDeviceVectron::decodeResponse (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
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
#if 0

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
#endif
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " (" << __LINE__ << ") Invalid command " << getCurrentCommand() << " scanning " << getName() << endl;
                }
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState (StateScanAbort);
                retCode = StateScanAbort;
                break;
            }
    }
    return retCode;
}


INT CtiDeviceVectron::decodeResponseSelectMeter(CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)

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
                if ((Transfer.getInBuffer()[0] == ACK) && (Transfer.getInBuffer()[1] & 0x80))
                {
                    // the modem switches are valid
                    switch (getIED().getSlaveAddress())
                    {
                        case 0:
                            {
                                // make sure this is the master
                                if ((Transfer.getInBuffer()[3] & 0x01) && !(Transfer.getInBuffer()[3] & 0x02))
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
                                if ((Transfer.getInBuffer()[3] & 0x01) && (Transfer.getInBuffer()[3] & 0x02))
                                {
                                    // both bits 2 and 3 should be 0
                                    if ((Transfer.getInBuffer()[3] & 0x04) ||
                                        (Transfer.getInBuffer()[3] & 0x08))
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
                                if ((Transfer.getInBuffer()[3] & 0x01) && (Transfer.getInBuffer()[3] & 0x02))
                                {
                                    // bit 2 off bit 3 on
                                    if ((!(Transfer.getInBuffer()[3] & 0x04)) ||
                                        (Transfer.getInBuffer()[3] & 0x08))
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
                                if ((Transfer.getInBuffer()[3] & 0x01) && (Transfer.getInBuffer()[3] & 0x02))
                                {
                                    // bit 2 on bit 3 off
                                    if ((Transfer.getInBuffer()[3] & 0x04) ||
                                        (!(Transfer.getInBuffer()[3] & 0x08)))
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
                                if ((Transfer.getInBuffer()[3] & 0x01) && (Transfer.getInBuffer()[3] & 0x02))
                                {
                                    // bit 2 on bit 3 off
                                    if ((!(Transfer.getInBuffer()[3] & 0x04)) ||
                                        (!(Transfer.getInBuffer()[3] & 0x08)))
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

INT CtiDeviceVectron::decodeResponseScan (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    SchlMeterStruct   MeterSt;
    VectronLoadProfileMessage_t *localLP      = ((VectronLoadProfileMessage_t*)_loadProfileBuffer);
    int               retCode    = NORMAL;
    INT               lastCommandStartAddress;

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
                    do
                    {
                        // copy data into the buffer
                        memcpy(&_dataBuffer[getTotalByteCount()],
                               &Transfer.getInBuffer()[vectronScanCommands[getCommandPacket()].startOffset + 1],
                               vectronScanCommands[getCommandPacket()].bytesToRead);

                        setTotalByteCount (getTotalByteCount()+vectronScanCommands[getCommandPacket()].bytesToRead);

                        // increment our command packet
                        setCommandPacket(getCommandPacket()+1);

                        // loop through while in same block or at end of command list
                    } while ((vectronScanCommands[getCommandPacket()].startAddress == vectronScanCommands[getCommandPacket()-1].startAddress) &&
                             vectronScanCommands[getCommandPacket()].startAddress != NULL);

                    // if null, we're done
                    if (vectronScanCommands[getCommandPacket()].startAddress == NULL)
                    {
                        setCurrentState (StateScanComplete);
                        setCurrentCommand(CmdLoadProfileTransition);
                    }
                    else
                    {
                        setCurrentState (StateScanValueSet1FirstScan);
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
    }
    else if (retCode == SCHLUM_RESEND_CMD)
    {
        // we don't want to abort, just try again
        retCode = NORMAL;
    }

    return retCode;
}



INT CtiDeviceVectron::decodeResponseLoadProfile (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    int               retCode  = NORMAL;
    SchlMeterStruct   MeterSt;
    SchlumbergerLProfileInput_t *localMMInputs      = ((SchlumbergerLProfileInput_t *)_massMemoryRequestInputs);
    SchlLoadProfile_t           *localMMLoadProfile = ((SchlLoadProfile_t *)_massMemoryLoadProfile);
    VectronMMConfig_t           *localMMConfig      = ((VectronMMConfig_t *)_massMemoryConfig);
    VectronLoadProfileMessage_t *localLProfile      = ((VectronLoadProfileMessage_t*)_loadProfileBuffer);
    VectronRealTimeRegister_t   *localTimeDate      = ((VectronRealTimeRegister_t *)_loadProfileTimeDate);



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
                    decodeResultRealTime ((VectronRealTimeRegister_t *)_loadProfileTimeDate);
                    setCurrentState (StateScanValueSet2FirstScan);
                    break;
                }
            case StateScanDecode2:
                {
                    // Mass memory configuration     SCHL_SCAN_STATE_DECODE_MM_CONFIG
                    memcpy(_massMemoryConfig, &Transfer.getInBuffer()[1], MeterSt.Length);
                    setTotalByteCount (getTotalByteCount()+MeterSt.Length);
                    setCurrentState (StateScanValueSet3FirstScan);
                    break;
                }

            case StateScanDecode3:
                {
                    // Mass memory configuration     SCHL_SCAN_STATE_DECODE_MM_CONFIG
                    memcpy(&_massMemoryConfig[getTotalByteCount()], &Transfer.getInBuffer()[1], MeterSt.Length);
                    setTotalByteCount(0);
                    if (decodeResultMMConfig (localMMConfig))
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Meter " << getName() << " has no load profile channels configured" << endl;
                        }
                        setCurrentState (StateScanComplete);
                    }
                    else
                    {
                        setCurrentState (StateScanValueSet4FirstScan);
                    }
                    break;
                }

            case StateScanDecode4:
                {
                    // Mass memory configuration     SCHL_SCAN_STATE_DECODE_MM_READ
                    memcpy(&(localLProfile->MMBuffer[localMMInputs->MMCount]), &Transfer.getInBuffer()[1], MeterSt.Length - 1);

                    localMMInputs->MMCount += (MeterSt.Length - 1);

                    /*
                     * Now decide if we need more, or if we need to go back a record
                     */
                    if (localMMInputs->MMPos < localMMInputs->MMScanStartAddress + localMMConfig->Real.recordLength)
                    {
                        setCurrentState (StateScanValueSet5FirstScan);
                    }
                    else
                    {
                        /*
                         *  We've got the record! get it back to SCANNER & Let him decide what to do next.
                         */
                        setPreviousState (StateScanValueSet6FirstScan);
                        setCurrentState (StateScanReturnLoadProfile);
                    }
                    break;
                }
            case StateScanDecode5:
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

INT CtiDeviceVectron::decodeResultScan (INMESS *InMessage,
                                        RWTime &TimeNow,
                                        RWTPtrSlist< CtiMessage >   &vgList,
                                        RWTPtrSlist< CtiMessage > &retList,
                                        RWTPtrSlist< OUTMESS > &outList)
{
    char tmpCurrentState = InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1];

    CHAR     temp[100], buffer[60];

    BOOL     bDoStatusCheck = FALSE;
    BOOL     bDoResetStatus = FALSE;
    BOOL     isValidPoint = TRUE;

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

    VectronScanData_t  *vsd = (VectronScanData_t*) DUPRep->Message;
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
                    if (getMeterDataFromScanStruct (i, PValue, peakTime, vsd))
                    {
                        double Value;
                        RWCString valReport;

                        Value = ((CtiPointNumeric*)pNumericPoint)->computeValueForUOM((DOUBLE)PValue);
                        valReport = getName() + " / " + pNumericPoint->getName() + " = " + CtiNumStr((int)Value);

                        verifyAndAddPointToReturnMsg( pNumericPoint->getPointID( ),
                                                      Value,
                                                      NormalQuality,
                                                      peakTime,
                                                      pPIL,
                                                      0,
                                                      valReport );
                    }
                }
                //  ACH:  default no-points-defined valReport
/*                else if(i == 1)
                {
                    sprintf(
                }*/
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
    pPIL = NULL;

    if( DebugLevel & 0x0001 )
        ResultDisplay(InMessage);
    return NORMAL;
}



INT CtiDeviceVectron::decodeResultLoadProfile (INMESS *InMessage,
                                               RWTime &TimeNow,
                                               RWTPtrSlist< CtiMessage >   &vgList,
                                               RWTPtrSlist< CtiMessage > &retList,
                                               RWTPtrSlist< OUTMESS > &outList)
{

    DIALUPREQUEST                 *dupReq = &InMessage->Buffer.DUPSt.DUPRep.ReqSt;
    DIALUPREPLY                   *dupRep = &InMessage->Buffer.DUPSt.DUPRep;

    VectronLoadProfileMessage_t   *vectronLProfile = (VectronLoadProfileMessage_t*)&dupRep->Message;
    VectronMMConfig_t             *vectronMMConfig = (VectronMMConfig_t *)&vectronLProfile->MMConfig;

    INT            numberOfIntervals;
    time_t         currentIntervalTime  = 0;
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

    DOUBLE         pValue;
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
    currentInterval   = (INT)vectronMMConfig->Real.currentInterval;
    intervalLength    = (INT)vectronMMConfig->Real.intervalLength;
    recordLength      = (INT)vectronMMConfig->Real.recordLength;
    numActiveChannels = (INT)vectronMMConfig->Real.numberOfChannels;
    pulseData         = ((BYTE*)(&vectronLProfile->MMBuffer)) + (3 + 9 + (6 * numActiveChannels));

    if ((INT)vectronLProfile->MMBuffer[0] == 0)
    {
        numberOfIntervals = currentInterval;
    }
    else
    {
        numberOfIntervals = 60;
    }

    for (programNumber = 0; programNumber < numActiveChannels; programNumber++)
    {
        // initialize each channel
        lastLPTime = startingLPTime;

        // needs to be reset each time through the loop
        goodPoint = !NORMAL;

        energyRegister = vectronMMConfig->Real.program[programNumber].energyRegister;

        switch (energyRegister)
        {
            case 1:  // Wh
                {
                    if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_KW, AnalogPointType)) != NULL)
                        goodPoint = NORMAL;

                    regTypeSupported = TRUE;
                    break;
                }

            case 2:  // Lagging VARh
                {
                    if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_KVAR, AnalogPointType)) != NULL)
                        goodPoint = NORMAL;

                    regTypeSupported = TRUE;
                    break;
                }
/*    case 3:  // Leading VARh
             {
                offset = 20;
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(20, AnalogPointType)) != NULL)
                   goodPoint = NORMAL;

                regTypeSupported = TRUE;
                break;
             }
*/
            case 4:  // VAh
                {
                    if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_KVA, AnalogPointType)) != NULL)
                        goodPoint = NORMAL;

                    regTypeSupported = TRUE;
                    break;
                }
            case 0:
                {
                    regTypeSupported = FALSE;
                    goodPoint       = !NORMAL;
                    break;
                }
            case 5:  // watt demand
            case 6:  // watt tou demand
            case 7:  // var lag demand
            case 8:  // va lag demand
            case 9:  // va total demand
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
            pulseWeight    = vectronMMConfig->Real.program[programNumber].pulseWeight;

            for (i = 0; i < numberOfIntervals; i++)
            {
                currentIntervalTime = (vectronLProfile->RecordTime + (intervalLength * 60 * (i+1)));

                if (lastLPTime < currentIntervalTime)
                {
                    pulseCount = (INT)nibblesAndBits(pulseTemp, numActiveChannels, programNumber, i);
                    intervalTime = RWTime(currentIntervalTime + rwEpoch);

                    pValue = ((60.0 / (DOUBLE)intervalLength) * (DOUBLE)pulseWeight * (DOUBLE)pulseCount * pNumericPoint->getMultiplier()) / 1000.0;

                    // update the last lp time if we are successful
                    if (verifyAndAddPointToReturnMsg (pNumericPoint->getPointID(),
                                                      pValue,
                                                      NormalQuality,
                                                      intervalTime,
                                                      pPIL,
                                                      TAG_POINT_LOAD_PROFILE_DATA))
                    {
                        lastLPTime = currentIntervalTime;

                        // save the last profile interval
                        if (lastLPTime + rwEpoch > getLastLPTime())
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
    pPIL = NULL;

    return NORMAL;
}

// Routine to display or print the message
INT CtiDeviceVectron::ResultDisplay (INMESS *InMessage)

{
    ULONG          i, j;
    VectronScanData_t  *vsd = (VectronScanData_t*) InMessage->Buffer.DUPSt.DUPRep.Message;
    CHAR workString[10];
    CHAR buffer[200];

    /**************************
    * lazy way to do this
    ***************************
    */
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Result display for device " << getName() << " in progress " << endl;
        sprintf(buffer,"Meter ID          :   %.9s",vsd->Real.meterId);
        dout << endl << buffer << endl;
        sprintf(buffer,"Unit Type         :   %.3s",vsd->Real.unitType);
        dout << buffer << endl;
        sprintf(buffer,"Unit  ID          :   %.8s",vsd->Real.unitId);
        dout << buffer << endl;
        sprintf(buffer,"Program ID        :   %d",vsd->Real.progId);
        dout << buffer << endl;
        sprintf(buffer,"Revision Level");
        dout << endl << buffer << endl;
        sprintf (buffer,"    Software: %4.2f\n    Firmware: %4.2f",vsd->Real.sWRev,vsd->Real.fWRev);
        dout << buffer << endl;
        sprintf(buffer,"Register Multiplier:  %9.5f",vsd->Real.registerMultiplier);
        dout << endl << buffer << endl;

        sprintf(buffer,"            Inst. Voltage         Inst. Current");
        dout << endl << buffer << endl;
        sprintf(buffer,"Phase A         %6.3f             %6.3f",vsd->Real.phaseA.voltage,vsd->Real.phaseA.current);
        dout << buffer << endl;
        sprintf(buffer,"Phase B         %6.3f             %6.3f",vsd->Real.phaseB.voltage,vsd->Real.phaseB.current);
        dout << buffer << endl;
        sprintf(buffer,"Phase C         %6.3f             %6.3f",vsd->Real.phaseC.voltage,vsd->Real.phaseC.current);
        dout << buffer << endl;


        sprintf(buffer,"Register 1       Max        Date    Time      Mapping:%s",displayRegisterMapping(vsd->Real.register1.mapping, workString));
        dout << endl << buffer << endl;
        sprintf(buffer,"Rate E      %9.2f       %02d-%02d   %02d:%02d",
                vsd->Real.register1.data.demand.rateE.PeakValue,
                vsd->Real.register1.data.demand.rateE.Month,
                vsd->Real.register1.data.demand.rateE.Day,
                vsd->Real.register1.data.demand.rateE.Hour,
                vsd->Real.register1.data.demand.rateE.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate A      %9.2f       %02d-%02d   %02d:%02d",
                vsd->Real.register1.data.demand.rateA.PeakValue,
                vsd->Real.register1.data.demand.rateA.Month,
                vsd->Real.register1.data.demand.rateA.Day,
                vsd->Real.register1.data.demand.rateA.Hour,
                vsd->Real.register1.data.demand.rateA.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate B      %9.2f       %02d-%02d   %02d:%02d",
                vsd->Real.register1.data.demand.rateB.PeakValue,
                vsd->Real.register1.data.demand.rateB.Month,
                vsd->Real.register1.data.demand.rateB.Day,
                vsd->Real.register1.data.demand.rateB.Hour,
                vsd->Real.register1.data.demand.rateB.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate C      %9.2f       %02d-%02d   %02d:%02d",
                vsd->Real.register1.data.demand.rateC.PeakValue,
                vsd->Real.register1.data.demand.rateC.Month,
                vsd->Real.register1.data.demand.rateC.Day,
                vsd->Real.register1.data.demand.rateC.Hour,
                vsd->Real.register1.data.demand.rateC.Minute);
        dout << buffer << endl;
        sprintf(buffer,"Rate D      %9.2f       %02d-%02d   %02d:%02d",
                vsd->Real.register1.data.demand.rateD.PeakValue,
                vsd->Real.register1.data.demand.rateD.Month,
                vsd->Real.register1.data.demand.rateD.Day,
                vsd->Real.register1.data.demand.rateD.Hour,
                vsd->Real.register1.data.demand.rateD.Minute);
        dout << buffer << endl;


        // set to one if demand
        if (vsd->Real.register2.configFlag)
        {
            sprintf(buffer,"Register 2       Max        Date    Time       Mapping:%s",displayRegisterMapping(vsd->Real.register2.mapping, workString));
            dout << endl << buffer << endl;
            sprintf(buffer,"Rate E      %9.2f       %02d-%02d   %02d:%02d",
                    vsd->Real.register2.data.demand.rateE.PeakValue,
                    vsd->Real.register2.data.demand.rateE.Month,
                    vsd->Real.register2.data.demand.rateE.Day,
                    vsd->Real.register2.data.demand.rateE.Hour,
                    vsd->Real.register2.data.demand.rateE.Minute);
            dout << buffer << endl;
            sprintf(buffer,"Rate A      %9.2f       %02d-%02d   %02d:%02d",
                    vsd->Real.register2.data.demand.rateA.PeakValue,
                    vsd->Real.register2.data.demand.rateA.Month,
                    vsd->Real.register2.data.demand.rateA.Day,
                    vsd->Real.register2.data.demand.rateA.Hour,
                    vsd->Real.register2.data.demand.rateA.Minute);
            dout << buffer << endl;
            sprintf(buffer,"Rate B      %9.2f       %02d-%02d   %02d:%02d",
                    vsd->Real.register2.data.demand.rateB.PeakValue,
                    vsd->Real.register2.data.demand.rateB.Month,
                    vsd->Real.register2.data.demand.rateB.Day,
                    vsd->Real.register2.data.demand.rateB.Hour,
                    vsd->Real.register2.data.demand.rateB.Minute);
            dout << buffer << endl;
        }
        else
        {
            sprintf(buffer,"Register 2                     Energy       Mapping:%s",displayRegisterMapping(vsd->Real.register2.mapping, workString));
            dout << endl << buffer << endl;
            sprintf(buffer,"Rate E               %17.7f",vsd->Real.register2.data.energy.rateE);
            dout << buffer << endl;
            sprintf(buffer,"Rate A               %17.7f",vsd->Real.register2.data.energy.rateA);
            dout << buffer << endl;
            sprintf(buffer,"Rate B               %17.7f",vsd->Real.register2.data.energy.rateB);
            dout << buffer << endl;
            sprintf(buffer,"Rate C               %17.7f",vsd->Real.register2.data.energy.rateC);
            dout << buffer << endl;
            sprintf(buffer,"Rate D               %17.7f",vsd->Real.register2.data.energy.rateD);
            dout << buffer << endl;

        }

        // set to one if demand
        if (vsd->Real.register3.configFlag)
        {
            sprintf(buffer,"Register 3       Max        Date    Time      Mapping:%s",displayRegisterMapping(vsd->Real.register3.mapping, workString));
            dout << endl << buffer << endl;
            sprintf(buffer,"Rate E      %9.2f       %02d-%02d   %02d:%02d",
                    vsd->Real.register3.data.demand.rateE.PeakValue,
                    vsd->Real.register3.data.demand.rateE.Month,
                    vsd->Real.register3.data.demand.rateE.Day,
                    vsd->Real.register3.data.demand.rateE.Hour,
                    vsd->Real.register3.data.demand.rateE.Minute);
            dout << buffer << endl;
        }
        else
        {
            sprintf(buffer,"Register 3                     Energy        Mapping:%s",displayRegisterMapping(vsd->Real.register3.mapping, workString));
            dout << endl << buffer << endl;
            sprintf(buffer,"Rate E               %17.7f",vsd->Real.register3.data.energy.rateE);
            dout << buffer << endl;

        }

        // set to one if demand
        if (vsd->Real.register4.configFlag)
        {
            sprintf(buffer,"Register 4       Max        Date    Time      Mapping:%s",displayRegisterMapping(vsd->Real.register4.mapping, workString));
            dout << endl << buffer << endl;
            sprintf(buffer,"Rate E      %9.2f       %02d-%02d   %02d:%02d",
                    vsd->Real.register4.data.demand.rateE.PeakValue,
                    vsd->Real.register4.data.demand.rateE.Month,
                    vsd->Real.register4.data.demand.rateE.Day,
                    vsd->Real.register4.data.demand.rateE.Hour,
                    vsd->Real.register4.data.demand.rateE.Minute);
            dout << buffer << endl;
        }
        else
        {
            sprintf(buffer,"Register 4                     Energy        Mapping:%s",displayRegisterMapping(vsd->Real.register4.mapping, workString));
            dout << endl << buffer << endl;
            sprintf(buffer,"Rate E               %17.7f",vsd->Real.register4.data.energy.rateE);
            dout << buffer << endl;
        }
    }

    return(NORMAL);
}

CHAR * displayRegisterMapping (USHORT map, CHAR *retString)
{
    switch (map)
    {
        case VECTRON_WATTHOUR_ENERGY:
            strcpy (retString, "Watt h");
            break;
        case VECTRON_VARH_LAG_ENERGY:
            strcpy (retString, "Varh lag");
            break;
        case VECTRON_VARH_LEAD_ENERGY:
            strcpy (retString, "Varh lead");
            break;
        case VECTRON_VAH_ENERGY:
            strcpy (retString, "VAh lag");
            break;
        case VECTRON_WATT_DEMAND:
            strcpy (retString, "Watts");
            break;
        case VECTRON_WATT_TOU_DEMAND:
            strcpy (retString, "Watt TOU");
            break;
        case VECTRON_VAR_LAG_DEMAND:
            strcpy (retString, "Var lag");
            break;
        case VECTRON_VA_LAG_DEMAND:
            strcpy (retString, "VA lag");
            break;
        case VECTRON_VA_TOTAL_DEMAND:
            strcpy (retString, "VA total");
            break;
        default:
            strcpy (retString, "Not Mapped");
            break;
    }
    return retString;
}

INT CtiDeviceVectron::allocateDataBins  (OUTMESS *outMess)
{

    if (_dataBuffer == NULL)
        _dataBuffer = CTIDBG_new BYTE[sizeof (VectronScanData_t)];


    if (_loadProfileTimeDate == NULL)
        _loadProfileTimeDate = CTIDBG_new BYTE[sizeof (VectronRealTimeRegister_t)];

    if (_loadProfileBuffer == NULL)
    {
        _loadProfileBuffer = CTIDBG_new BYTE[sizeof (VectronLoadProfileMessage_t)];

        if (_loadProfileBuffer != NULL)
        {
            ((VectronLoadProfileMessage_t *)_loadProfileBuffer)->porterLPTime = outMess->Buffer.DUPReq.LP_Time;
        }
    }

    // point to mm in the load profile buffer
    if (_massMemoryConfig == NULL)
        if (_loadProfileBuffer != NULL)
        {
            // aaahhhh that is a nasty cast
            _massMemoryConfig = (BYTE *)&(((VectronLoadProfileMessage_t *)_loadProfileBuffer)->MMConfig);
        }


        /* NOTE:  The massMemoryLoadProfile block is initialized in the state machine
                  and removed in the free memory block function
         */


    if (_massMemoryRequestInputs == NULL)
        _massMemoryRequestInputs = CTIDBG_new BYTE[sizeof (SchlumbergerLProfileInput_t)];

    // set the command based in the out message command
    setTotalByteCount (0);
    setCurrentCommand ((CtiDeviceIED::CtiMeterCmdStates_t)outMess->Buffer.DUPReq.Command[0]);
    setCurrentState (StateHandshakeInitialize);
    return NORMAL;
}

INT CtiDeviceVectron::reformatDataBuffer(BYTE *aInMessBuffer, ULONG &aTotalBytes)

{
    BYTEUSHORT config;
    BYTEUSHORT mapping;
    BYTE    workBuffer[8] = {0,0,0,0,0,0,0,0};

    VectronScanData_t *ptr = (VectronScanData_t *)(_dataBuffer);

    memcpy (ptr->Real.unitType,     ptr->Byte.unitType,     3);
    memcpy (ptr->Real.unitId,       ptr->Byte.unitId,       8);
    memcpy (ptr->Real.meterId,      ptr->Byte.meterId,      9);

    ptr->Real.sWRev              = (FLOAT)(BCDtoBase10 (ptr->Byte.sWRev,2)  / 100.0);
    ptr->Real.fWRev              = (FLOAT)(BCDtoBase10 (ptr->Byte.fWRev,2)  / 100.0);
    ptr->Real.progId             = (USHORT)BCDtoBase10 (ptr->Byte.progId,2);

    ptr->Real.registerMultiplier = FltLittleEndian ((FLOAT *)&ptr->Byte.registerMultiplier);

    ptr->Real.phaseA.voltage     = FltLittleEndian ((FLOAT *)&ptr->Byte.phaseAVoltage);
    ptr->Real.phaseB.voltage     = FltLittleEndian ((FLOAT *)&ptr->Byte.phaseBVoltage);
    ptr->Real.phaseC.voltage     = FltLittleEndian ((FLOAT *)&ptr->Byte.phaseCVoltage);
    ptr->Real.phaseA.current     = FltLittleEndian ((FLOAT *)&ptr->Byte.phaseACurrent);
    ptr->Real.phaseB.current     = FltLittleEndian ((FLOAT *)&ptr->Byte.phaseBCurrent);
    ptr->Real.phaseC.current     = FltLittleEndian ((FLOAT *)&ptr->Byte.phaseCCurrent);

    /***********************
    * register configuration decides if we're finding demand or energy
    * values are 1 if demand, 0 if energy
    *
    * register one is always demand
    ************************
    */

    config.sh = 0;
    memcpy (&config.ch[0], &ptr->Byte.registerConfiguration,1);;

    /******************
    * Register 1
    *******************
    */
    ptr->Real.register1.configFlag =1;
    mapping.sh = 0;
    memcpy (&mapping.ch[0], &ptr->Byte.registerMapping[0],1);;
    ptr->Real.register1.mapping = mapping.sh;

    memcpy (&workBuffer[0],&ptr->Byte.register1RateE[0],4);
    workBuffer[0] &= 0x0F;

    ptr->Real.register1.data.demand.rateE.Cumulative =
    (FLOAT)((BCDtoBase10 (workBuffer,4) / pow (10,7)) * pow (2,(USHORT)ptr->Byte.register1RateE[0] >> 4));

    memcpy (&workBuffer[0],&ptr->Byte.register1RateE[4],4);
    workBuffer[0] &= 0x0F;

    ptr->Real.register1.data.demand.rateE.PeakValue =
    (FLOAT)((BCDtoBase10 (workBuffer,4) / pow (10,7)) * pow (2,(USHORT)ptr->Byte.register1RateE[4] >> 4));

    ptr->Real.register1.data.demand.rateE.Month = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateE[8],1));
    ptr->Real.register1.data.demand.rateE.Day = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateE[9],1));
    ptr->Real.register1.data.demand.rateE.Hour = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateE[10],1));
    ptr->Real.register1.data.demand.rateE.Minute = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateE[11],1));

    // register 1 rate A
    memcpy (&workBuffer[0],&ptr->Byte.register1RateA[0],4);
    workBuffer[0] &= 0x0F;

    ptr->Real.register1.data.demand.rateA.PeakValue =
    (FLOAT)((BCDtoBase10 (workBuffer,4) / pow (10,7)) * pow (2,(USHORT)ptr->Byte.register1RateA[0] >> 4));

    ptr->Real.register1.data.demand.rateA.Month = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateA[4],1));
    ptr->Real.register1.data.demand.rateA.Day = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateA[5],1));
    ptr->Real.register1.data.demand.rateA.Hour = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateA[6],1));
    ptr->Real.register1.data.demand.rateA.Minute = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateA[7],1));

    // register 1 rate B
    memcpy (&workBuffer[0],&ptr->Byte.register1RateB[0],4);
    workBuffer[0] &= 0x0F;

    ptr->Real.register1.data.demand.rateB.PeakValue =
    (FLOAT)((BCDtoBase10 (workBuffer,4) / pow (10,7)) * pow (2,(USHORT)ptr->Byte.register1RateB[0] >> 4));

    ptr->Real.register1.data.demand.rateB.Month = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateB[4],1));
    ptr->Real.register1.data.demand.rateB.Day = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateB[5],1));
    ptr->Real.register1.data.demand.rateB.Hour = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateB[6],1));
    ptr->Real.register1.data.demand.rateB.Minute = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateB[7],1));

    // register 1 rate C
    memcpy (&workBuffer[0],&ptr->Byte.register1RateC[0],4);
    workBuffer[0] &= 0x0F;

    ptr->Real.register1.data.demand.rateC.PeakValue =
    (FLOAT)((BCDtoBase10 (workBuffer,4) / pow (10,7)) * pow (2,(USHORT)ptr->Byte.register1RateC[0] >> 4));

    ptr->Real.register1.data.demand.rateC.Month = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateC[4],1));
    ptr->Real.register1.data.demand.rateC.Day = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateC[5],1));
    ptr->Real.register1.data.demand.rateC.Hour = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateC[6],1));
    ptr->Real.register1.data.demand.rateC.Minute = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateC[7],1));

    // register 1 rate D
    memcpy (&workBuffer[0],&ptr->Byte.register1RateD[0],4);
    workBuffer[0] &= 0x0F;

    ptr->Real.register1.data.demand.rateD.PeakValue =
    (FLOAT)((BCDtoBase10 (workBuffer,4) / pow (10,7)) * pow (2,(USHORT)ptr->Byte.register1RateD[0] >> 4));

    ptr->Real.register1.data.demand.rateD.Month = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateD[4],1));
    ptr->Real.register1.data.demand.rateD.Day = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateD[5],1));
    ptr->Real.register1.data.demand.rateD.Hour = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateD[6],1));
    ptr->Real.register1.data.demand.rateD.Minute = (USHORT)(BCDtoBase10 (&ptr->Byte.register1RateD[7],1));

    /******************
    * Register 2
    *******************
    */
    mapping.sh = 0;
    memcpy (&mapping.ch[0], &ptr->Byte.registerMapping[2],1);;
    ptr->Real.register2.mapping = mapping.sh;

    if (config.sh & 0x02)
    {
        // rate E
        memcpy (&workBuffer[0],&ptr->Byte.register2Info1[0],4);
        workBuffer[0] &= 0x0F;

        ptr->Real.register2.data.demand.rateE.PeakValue =
        (FLOAT)((BCDtoBase10 (workBuffer,4) / pow (10,7)) * pow (2,(USHORT)ptr->Byte.register2Info1[0] >> 4));

        ptr->Real.register2.data.demand.rateE.Month = (USHORT)(BCDtoBase10 (&ptr->Byte.register2Info1[4],1));
        ptr->Real.register2.data.demand.rateE.Day = (USHORT)(BCDtoBase10 (&ptr->Byte.register2Info1[5],1));
        ptr->Real.register2.data.demand.rateE.Hour = (USHORT)(BCDtoBase10 (&ptr->Byte.register2Info1[6],1));
        ptr->Real.register2.data.demand.rateE.Minute = (USHORT)(BCDtoBase10 (&ptr->Byte.register2Info1[7],1));

        // cum
        memcpy (&workBuffer[0],&ptr->Byte.register2Info2[8],4);
        workBuffer[0] &= 0x0F;

        ptr->Real.register2.data.demand.rateE.Cumulative =
        (FLOAT)((BCDtoBase10 (workBuffer,4) / pow (10,7)) * pow (2,(USHORT)ptr->Byte.register2Info2[8] >> 4));

        //rate A
        memcpy (&workBuffer[0],&ptr->Byte.register2Info2[0],4);
        workBuffer[0] &= 0x0F;

        ptr->Real.register2.data.demand.rateA.PeakValue =
        (FLOAT)((BCDtoBase10 (workBuffer,4) / pow (10,7)) * pow (2,(USHORT)ptr->Byte.register2Info2[0] >> 4));

        ptr->Real.register2.data.demand.rateA.Month = (USHORT)(BCDtoBase10 (&ptr->Byte.register2Info2[4],1));
        ptr->Real.register2.data.demand.rateA.Day = (USHORT)(BCDtoBase10 (&ptr->Byte.register2Info2[5],1));
        ptr->Real.register2.data.demand.rateA.Hour = (USHORT)(BCDtoBase10 (&ptr->Byte.register2Info2[6],1));
        ptr->Real.register2.data.demand.rateA.Minute = (USHORT)(BCDtoBase10 (&ptr->Byte.register2Info2[7],1));

        // rate B
        memcpy (&workBuffer[0],&ptr->Byte.register2Info2[14],4);
        workBuffer[0] &= 0x0F;

        ptr->Real.register2.data.demand.rateB.PeakValue =
        (FLOAT)((BCDtoBase10 (workBuffer,4) / pow (10,7)) * pow (2,(USHORT)ptr->Byte.register2Info2[14] >> 4));

        ptr->Real.register2.data.demand.rateB.Month = (USHORT)(BCDtoBase10 (&ptr->Byte.register2Info2[18],1));
        ptr->Real.register2.data.demand.rateB.Day = (USHORT)(BCDtoBase10 (&ptr->Byte.register2Info2[19],1));
        ptr->Real.register2.data.demand.rateB.Hour = (USHORT)(BCDtoBase10 (&ptr->Byte.register2Info2[20],1));
        ptr->Real.register2.data.demand.rateB.Minute = (USHORT)(BCDtoBase10 (&ptr->Byte.register2Info2[21],1));

        ptr->Real.register2.configFlag = 1;

    }
    else
    {
        // energy
        ptr->Real.register2.data.energy.rateE = VectronBCDtoDouble (&ptr->Byte.register2Info1[1],7) / pow (10,8);
        ptr->Real.register2.data.energy.rateA = VectronBCDtoDouble (&ptr->Byte.register2Info2[0],7) / pow (10,8);
        ptr->Real.register2.data.energy.rateB = VectronBCDtoDouble (&ptr->Byte.register2Info2[7],7) / pow (10,8);
        ptr->Real.register2.data.energy.rateC = VectronBCDtoDouble (&ptr->Byte.register2Info2[14],7) / pow (10,8);
        ptr->Real.register2.data.energy.rateD = VectronBCDtoDouble (&ptr->Byte.register2Info2[21],7) / pow (10,8);

        ptr->Real.register2.configFlag  = 0;
    }

    /******************
    * Register 3
    *******************
    */
    mapping.sh = 0;
    memcpy (&mapping.ch[0], &ptr->Byte.registerMapping[4],1);;
    ptr->Real.register3.mapping = mapping.sh;

    if (config.sh & 0x04)
    {
        // rate E
        memcpy (&workBuffer[0],&ptr->Byte.register3Info1[0],4);
        workBuffer[0] &= 0x0F;

        ptr->Real.register3.data.demand.rateE.PeakValue =
        (FLOAT)((BCDtoBase10 (workBuffer,4) / pow (10,7)) * pow (2,(USHORT)ptr->Byte.register3Info1[0] >> 4));

        ptr->Real.register3.data.demand.rateE.Month = (USHORT)(BCDtoBase10 (&ptr->Byte.register3Info1[4],1));
        ptr->Real.register3.data.demand.rateE.Day = (USHORT)(BCDtoBase10 (&ptr->Byte.register3Info1[5],1));
        ptr->Real.register3.data.demand.rateE.Hour = (USHORT)(BCDtoBase10 (&ptr->Byte.register3Info1[6],1));
        ptr->Real.register3.data.demand.rateE.Minute = (USHORT)(BCDtoBase10 (&ptr->Byte.register3Info1[7],1));


        ptr->Real.register3.configFlag = 1;
    }
    else
    {
        // energy
        ptr->Real.register3.data.energy.rateE = VectronBCDtoDouble (&ptr->Byte.register3Info1[0],7) / pow (10,8);

        ptr->Real.register3.configFlag  = 0;
    }

    /******************
    * Register 4
    *******************
    */
    mapping.sh = 0;
    memcpy (&mapping.ch[0], &ptr->Byte.registerMapping[5],1);;
    ptr->Real.register4.mapping = mapping.sh;

    if (config.sh & 0x08)
    {
        //rate 3
        memcpy (&workBuffer[0],&ptr->Byte.register4Info1[0],4);
        workBuffer[0] &= 0x0F;

        ptr->Real.register4.data.demand.rateE.PeakValue =
        (FLOAT)((BCDtoBase10 (workBuffer,4) / pow (10,7)) * pow (2,(USHORT)ptr->Byte.register4Info1[0] >> 4));

        ptr->Real.register4.data.demand.rateE.Month = (USHORT)(BCDtoBase10 (&ptr->Byte.register4Info1[4],1));
        ptr->Real.register4.data.demand.rateE.Day = (USHORT)(BCDtoBase10 (&ptr->Byte.register4Info1[5],1));
        ptr->Real.register4.data.demand.rateE.Hour = (USHORT)(BCDtoBase10 (&ptr->Byte.register4Info1[6],1));
        ptr->Real.register4.data.demand.rateE.Minute = (USHORT)(BCDtoBase10 (&ptr->Byte.register4Info1[7],1));


        ptr->Real.register4.configFlag = 1;
    }
    else
    {
        ptr->Real.register4.data.energy.rateE = VectronBCDtoDouble (&ptr->Byte.register4Info1[0],7) / pow (10,8);
        ptr->Real.register4.configFlag = 0;
    }


    ptr->dataIsReal = TRUE;

    memcpy(aInMessBuffer, _dataBuffer, sizeof(VectronScanData_t));
    aTotalBytes = getTotalByteCount();
    return NORMAL;
}



INT CtiDeviceVectron::copyLoadProfileData(BYTE *aInMessBuffer, ULONG &aTotalBytes)
{

    memcpy(aInMessBuffer, _loadProfileBuffer, sizeof (VectronLoadProfileMessage_t));
    aTotalBytes = sizeof (VectronLoadProfileMessage_t);
    return NORMAL;
}





INT CtiDeviceVectron::decodeResultMMConfig (VectronMMConfig_t *config)
{
    int retCode=NORMAL;

    // lets decode and display this stuff
    config->Real.program[0].pulseWeight     = VectronBCDtoDouble (config->Byte.pulseWeightChannel1,4) / 100.0;
    config->Real.program[1].pulseWeight     = VectronBCDtoDouble (config->Byte.pulseWeightChannel2,4) / 100.0;

    config->Real.intervalLength             = (USHORT)BCDtoBase10 (&config->Byte.intervalLength,1);

    config->Real.logicalStart               = (USHORT)bytesToBase10(config->Byte.logicalStart,2);
    config->Real.logicalEnd                 = (USHORT)bytesToBase10(config->Byte.logicalEnd,2);

    config->Real.currentRecord              = (USHORT)bytesToBase10(config->Byte.currentRecord,2);
    config->Real.currentInterval            = (USHORT)(config->Byte.currentInterval);

    config->Real.numberOfChannels           = (USHORT)(config->Byte.numberOfChannels);

    config->Real.program[0].registerAddress = (USHORT)config->Byte.registerAddressChannel1;
    config->Real.program[1].registerAddress = (USHORT)config->Byte.registerAddressChannel2;

    config->Real.program[0].energyRegister  = (USHORT)config->Byte.energyRegisterChannel1;
    config->Real.program[1].energyRegister  = (USHORT)config->Byte.energyRegisterChannel2;


    if (config->Real.numberOfChannels == 2)
    {
        config->Real.logicalCurrent = config->Real.logicalStart + (204 * config->Real.currentRecord);
        config->Real.recordLength = 204;
    }
    else if (config->Real.numberOfChannels == 1)
    {
        config->Real.logicalCurrent = config->Real.logicalStart + (108 * config->Real.currentRecord);
        config->Real.recordLength = 108;
    }
    else
        retCode = !NORMAL;

    config->dataIsReal = TRUE;

//#define DEBUG
#ifdef DEBUG

    printf("Addresses:\n");
    printf("   Start           : 0x%04X\n", config->Real.logicalStart);
    printf("   Stop            : 0x%04X\n", config->Real.logicalEnd);
    printf("   Current         : 0x%04X\n", config->Real.logicalCurrent);

    printf("\nProfile Info:\n");
    printf("   Channels        : %d\n",config->Real.numberOfChannels);
    printf("   Pulse Weight 1  : %8.2f\n",config->Real.program[0].pulseWeight);
    printf("   Energy Reg 1    : %d\n",config->Real.program[0].energyRegister);
    printf("   Reg Address 1   : %d or 0x%02X\n\n",config->Real.program[0].registerAddress, config->Real.program[0].registerAddress);
    printf("   Pulse Weight 2  : %8.2f\n",config->Real.program[1].pulseWeight);
    printf("   Energy Reg 1    : %d\n",config->Real.program[1].energyRegister);
    printf("   Reg Address 2   : %d or 0x%02X\n\n",config->Real.program[1].registerAddress, config->Real.program[1].registerAddress);
    printf("   Interval Length : %d\n", config->Real.intervalLength);
    printf("   Current Interval: %d\n", config->Real.currentInterval);
    printf("   Current Record  : %d\n\n", config->Real.currentRecord);

#endif
    return retCode;
}

INT CtiDeviceVectron::decodeResultRealTime (VectronRealTimeRegister_t *localTimeDate)
{
    int retCode=NORMAL;


    localTimeDate->Hours = BCDtoBase10 (&localTimeDate->Hours,1);
    localTimeDate->Minutes =  BCDtoBase10 (&localTimeDate->Minutes,1);
    localTimeDate->Seconds =  BCDtoBase10 (&localTimeDate->Seconds,1);
    localTimeDate->Month =  BCDtoBase10 (&localTimeDate->Month,1);
    localTimeDate->DayOfMonth = BCDtoBase10 (&localTimeDate->DayOfMonth,1);
    localTimeDate->Year = BCDtoBase10 (&localTimeDate->Year,1);
    return retCode;
}


DOUBLE VectronBCDtoDouble(UCHAR* buffer, ULONG len)
{

    int i, j;
    DOUBLE temp;
    DOUBLE scratch = 0;

    for (i = (INT)len, j = 0; i > 0; j++, i--)
    {
        temp = 0;

        /* The high nibble */
        temp += (((buffer[j] & 0xf0) >> 4)  * 10);

        /* The Low nibble */
        temp += (buffer[j] & 0x0f);

        scratch = scratch * 100 + temp ;
    }

    return scratch;
}


USHORT CtiDeviceVectron::getType (int aOffset)
{
    USHORT type=0;

    switch (aOffset)
    {
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW:
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR:
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA:
        case OFFSET_PEAK_KW_OR_RATE_A_KW:
        case OFFSET_RATE_B_KW:
        case OFFSET_RATE_C_KW:
        case OFFSET_RATE_D_KW:
        case OFFSET_RATE_E_KW:
            {
                type = VECTRON_WATT_DEMAND;
                break;
            }
        case OFFSET_TOTAL_KWH:
        case OFFSET_RATE_A_KWH:
        case OFFSET_RATE_B_KWH:
        case OFFSET_RATE_C_KWH:
        case OFFSET_RATE_D_KWH:
        case OFFSET_RATE_E_KWH:
            {
                type = VECTRON_WATTHOUR_ENERGY;
                break;
            }
        case OFFSET_PEAK_KVAR_OR_RATE_A_KVAR:
        case OFFSET_RATE_B_KVAR:
        case OFFSET_RATE_C_KVAR:
        case OFFSET_RATE_D_KVAR:
        case OFFSET_RATE_E_KVAR:
            {
                type = VECTRON_VAR_LAG_DEMAND;
                break;
            }
        case OFFSET_TOTAL_KVARH:
        case OFFSET_RATE_A_KVARH:
        case OFFSET_RATE_B_KVARH:
        case OFFSET_RATE_C_KVARH:
        case OFFSET_RATE_D_KVARH:
        case OFFSET_RATE_E_KVARH:
            {
                type = VECTRON_VARH_LAG_ENERGY;
                break;
            }
        case OFFSET_PEAK_KVA_OR_RATE_A_KVA:
        case OFFSET_RATE_B_KVA:
        case OFFSET_RATE_C_KVA:
        case OFFSET_RATE_D_KVA:
        case OFFSET_RATE_E_KVA:
            {
                type = VECTRON_VA_LAG_DEMAND;
                break;
            }
        case OFFSET_TOTAL_KVAH:
        case OFFSET_RATE_A_KVAH:
        case OFFSET_RATE_B_KVAH:
        case OFFSET_RATE_C_KVAH:
        case OFFSET_RATE_D_KVAH:
        case OFFSET_RATE_E_KVAH:
            {
                type = VECTRON_VAH_ENERGY;
                break;
            }
        default:
            break;
    }
    return type;
}

USHORT CtiDeviceVectron::getRate (int aOffset)
{
    USHORT rate = SCHLUMBERGER_UNDEFINED;

    /* Get the value from InMessage */
    switch (aOffset)
    {
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW:
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR:
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA:
            {
                // there is no data point for these offsets
                rate = SCHLUMBERGER_UNDEFINED;
                break;
            }
        case OFFSET_PEAK_KW_OR_RATE_A_KW:
        case OFFSET_PEAK_KVAR_OR_RATE_A_KVAR:
        case OFFSET_PEAK_KVA_OR_RATE_A_KVA:
        case OFFSET_RATE_A_KWH:
        case OFFSET_RATE_A_KVARH:
        case OFFSET_RATE_A_KVAH:
            {
                rate = SCHLUMBERGER_RATE_A;
                break;
            }
        case OFFSET_RATE_B_KW:
        case OFFSET_RATE_B_KVAR:
        case OFFSET_RATE_B_KVA:
        case OFFSET_RATE_B_KWH:
        case OFFSET_RATE_B_KVARH:
        case OFFSET_RATE_B_KVAH:
            {
                rate = SCHLUMBERGER_RATE_B;
                break;
            }
        case OFFSET_RATE_C_KW:
        case OFFSET_RATE_C_KVAR:
        case OFFSET_RATE_C_KVA:
        case OFFSET_RATE_C_KWH:
        case OFFSET_RATE_C_KVARH:
        case OFFSET_RATE_C_KVAH:
            {
                rate = SCHLUMBERGER_RATE_C;
                break;
            }
        case OFFSET_RATE_D_KW:
        case OFFSET_RATE_D_KVAR:
        case OFFSET_RATE_D_KVA:
        case OFFSET_RATE_D_KWH:
        case OFFSET_RATE_D_KVARH:
        case OFFSET_RATE_D_KVAH:
            {
                rate = SCHLUMBERGER_RATE_D;
                break;
            }
        case OFFSET_RATE_E_KW:
        case OFFSET_RATE_E_KVAR:
        case OFFSET_RATE_E_KVA:
        case OFFSET_RATE_E_KWH:
        case OFFSET_RATE_E_KVARH:
        case OFFSET_RATE_E_KVAH:
        case OFFSET_TOTAL_KWH:
        case OFFSET_TOTAL_KVARH:
        case OFFSET_TOTAL_KVAH:

            {
                rate = SCHLUMBERGER_RATE_E;
                break;
            }

        default:
            break;
    }
    return rate;
}


BOOL CtiDeviceVectron::getMeterDataFromScanStruct (int aOffset, DOUBLE &aValue, RWTime &peak, VectronScanData_t *aScanData)
{
    BOOL isValidPoint = FALSE;


    // this is initial value
    peak = rwEpoch;
    aValue = 0.0;

    /* Get the value from InMessage */
    switch (aOffset)
    {
        case OFFSET_RATE_A_KWH:
        case OFFSET_RATE_B_KWH:
        case OFFSET_RATE_C_KWH:
        case OFFSET_RATE_D_KWH:
        case OFFSET_RATE_E_KWH:
        case OFFSET_RATE_A_KVARH:
        case OFFSET_RATE_B_KVARH:
        case OFFSET_RATE_C_KVARH:
        case OFFSET_RATE_D_KVARH:
        case OFFSET_RATE_E_KVARH:
        case OFFSET_RATE_A_KVAH:
        case OFFSET_RATE_B_KVAH:
        case OFFSET_RATE_C_KVAH:
        case OFFSET_RATE_D_KVAH:
        case OFFSET_RATE_E_KVAH:
        case OFFSET_TOTAL_KWH:
        case OFFSET_TOTAL_KVARH:
        case OFFSET_TOTAL_KVAH:

        case OFFSET_PEAK_KW_OR_RATE_A_KW:
        case OFFSET_RATE_B_KW:
        case OFFSET_RATE_C_KW:
        case OFFSET_RATE_D_KW:
        case OFFSET_RATE_E_KW:
        case OFFSET_PEAK_KVAR_OR_RATE_A_KVAR:
        case OFFSET_RATE_B_KVAR:
        case OFFSET_RATE_C_KVAR:
        case OFFSET_RATE_D_KVAR:
        case OFFSET_RATE_E_KVAR:
        case OFFSET_PEAK_KVA_OR_RATE_A_KVA:
        case OFFSET_RATE_B_KVA:
        case OFFSET_RATE_C_KVA:
        case OFFSET_RATE_D_KVA:
        case OFFSET_RATE_E_KVA:
            {
                // if we find a matching mapping
                if (getRateValueFromRegister(aValue,
                                             getType (aOffset),
                                             getRate(aOffset),
                                             peak,
                                             aScanData))
                {
                    isValidPoint = TRUE;
                }
                break;
            }

        case OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE:
            {
                aValue = aScanData->Real.phaseA.voltage;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE:
            {
                aValue = aScanData->Real.phaseB.voltage;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE:
            {
                aValue = aScanData->Real.phaseC.voltage;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_INSTANTANEOUS_PHASE_A_CURRENT:
            {
                aValue = aScanData->Real.phaseA.current;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_INSTANTANEOUS_PHASE_B_CURRENT:
            {
                aValue = aScanData->Real.phaseB.current;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_INSTANTANEOUS_PHASE_C_CURRENT:
            {
                aValue = aScanData->Real.phaseC.current;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW:
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR:
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA:
        case OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT:
        default:
            isValidPoint = FALSE;
            break;
    }

    return isValidPoint;
}


BOOL CtiDeviceVectron::getRateValueFromRegister (DOUBLE &aValue, USHORT aType, USHORT aRate, RWTime &aPeak, VectronScanData_t *data)
{
    int x;
    BOOL retCode=FALSE;
    aPeak = rwEpoch;


    // check all registers
    if (data->Real.register1.mapping == aType)
    {
        // register 1 is always a demand value of some sort
        retCode = getRateValueFromRegister1 (aValue, aRate, aPeak, data);
    }
    else if (data->Real.register2.mapping == aType)
    {
        // found it
        retCode = getRateValueFromRegister2 (aValue, aRate, aPeak, data);
    }
    else if (data->Real.register3.mapping == aType)
    {
        // found it
        retCode = getRateValueFromRegister3 (aValue, aRate, aPeak, data);
    }
    else if (data->Real.register4.mapping == aType)
    {
        // found it
        retCode = getRateValueFromRegister4 (aValue, aRate, aPeak, data);
    }

    return retCode;
}

BOOL CtiDeviceVectron::getRateValueFromRegister1 (DOUBLE &aValue,
                                                  USHORT aRate,
                                                  RWTime &aPeak,
                                                  VectronScanData_t *aScanData)
{
    BOOL retCode = FALSE;
    aValue = 0.0;
    aPeak = rwEpoch;

    switch (aRate)
    {
        case SCHLUMBERGER_RATE_A:
            {
                // make sure we have valid months
                if (aScanData->Real.register1.data.demand.rateA.Month > 0 &&
                    aScanData->Real.register1.data.demand.rateA.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.register1.data.demand.rateA.Day,
                                            aScanData->Real.register1.data.demand.rateA.Month,
                                            RWDate().year()),
                                    aScanData->Real.register1.data.demand.rateA.Hour,
                                    aScanData->Real.register1.data.demand.rateA.Minute,
                                    0);
                    aValue= aScanData->Real.register1.data.demand.rateA.PeakValue;
                    retCode = TRUE;
                }
                break;
            }
        case SCHLUMBERGER_RATE_B:
            {
                // make sure we have valid months
                if (aScanData->Real.register1.data.demand.rateB.Month > 0 &&
                    aScanData->Real.register1.data.demand.rateB.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.register1.data.demand.rateB.Day,
                                            aScanData->Real.register1.data.demand.rateB.Month,
                                            RWDate().year()),
                                    aScanData->Real.register1.data.demand.rateB.Hour,
                                    aScanData->Real.register1.data.demand.rateB.Minute,
                                    0);
                    aValue= aScanData->Real.register1.data.demand.rateB.PeakValue;
                    retCode = TRUE;
                }
                break;
            }
        case SCHLUMBERGER_RATE_C:
            {
                // make sure we have valid months
                if (aScanData->Real.register1.data.demand.rateC.Month > 0 &&
                    aScanData->Real.register1.data.demand.rateC.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.register1.data.demand.rateC.Day,
                                            aScanData->Real.register1.data.demand.rateC.Month,
                                            RWDate().year()),
                                    aScanData->Real.register1.data.demand.rateC.Hour,
                                    aScanData->Real.register1.data.demand.rateC.Minute,
                                    0);

                    aValue= aScanData->Real.register1.data.demand.rateC.PeakValue;
                    retCode = TRUE;
                }
                break;
            }
        case SCHLUMBERGER_RATE_D:
            {
                // make sure we have valid months
                if (aScanData->Real.register1.data.demand.rateD.Month > 0 &&
                    aScanData->Real.register1.data.demand.rateD.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.register1.data.demand.rateD.Day,
                                            aScanData->Real.register1.data.demand.rateD.Month,
                                            RWDate().year()),
                                    aScanData->Real.register1.data.demand.rateD.Hour,
                                    aScanData->Real.register1.data.demand.rateD.Minute,
                                    0);
                    aValue= aScanData->Real.register1.data.demand.rateD.PeakValue;
                    retCode = TRUE;
                }
                break;
            }
        case SCHLUMBERGER_RATE_E:
            {
                // make sure we have valid months
                if (aScanData->Real.register1.data.demand.rateE.Month > 0 &&
                    aScanData->Real.register1.data.demand.rateE.Month < 13)
                {
                    aPeak = RWTime (RWDate (aScanData->Real.register1.data.demand.rateE.Day,
                                            aScanData->Real.register1.data.demand.rateE.Month,
                                            RWDate().year()),
                                    aScanData->Real.register1.data.demand.rateE.Hour,
                                    aScanData->Real.register1.data.demand.rateE.Minute,
                                    0);
                    aValue= aScanData->Real.register1.data.demand.rateE.PeakValue;
                    retCode = TRUE;
                }
                break;
            }
        default:
            break;
    }

    return retCode;
}

BOOL CtiDeviceVectron::getRateValueFromRegister2 (DOUBLE &aValue,
                                                  USHORT aRate,
                                                  RWTime &aPeak,
                                                  VectronScanData_t *aScanData)
{
    BOOL retCode = FALSE;
    aValue = 0.0;
    aPeak = rwEpoch;

    switch (aRate)
    {
        case SCHLUMBERGER_RATE_A:
            {
                if (aScanData->Real.register2.configFlag == 1)
                {
                    // make sure we have valid months
                    if (aScanData->Real.register2.data.demand.rateA.Month > 0 &&
                        aScanData->Real.register2.data.demand.rateA.Month < 13)
                    {
                        aPeak = RWTime (RWDate (aScanData->Real.register2.data.demand.rateA.Day,
                                                aScanData->Real.register2.data.demand.rateA.Month,
                                                RWDate().year()),
                                        aScanData->Real.register2.data.demand.rateA.Hour,
                                        aScanData->Real.register2.data.demand.rateA.Minute,
                                        0);
                        aValue = aScanData->Real.register2.data.demand.rateA.PeakValue;
                        retCode = TRUE;
                    }
                }
                else
                {
                    aValue=  aScanData->Real.register2.data.energy.rateA;
                    retCode = TRUE;
                }
                break;
            }
        case SCHLUMBERGER_RATE_B:
            {
                if (aScanData->Real.register2.configFlag == 1)
                {
                    // make sure we have valid months
                    if (aScanData->Real.register2.data.demand.rateB.Month > 0 &&
                        aScanData->Real.register2.data.demand.rateB.Month < 13)
                    {
                        aPeak = RWTime (RWDate (aScanData->Real.register2.data.demand.rateB.Day,
                                                aScanData->Real.register2.data.demand.rateB.Month,
                                                RWDate().year()),
                                        aScanData->Real.register2.data.demand.rateB.Hour,
                                        aScanData->Real.register2.data.demand.rateB.Minute,
                                        0);
                        aValue = aScanData->Real.register2.data.demand.rateB.PeakValue;
                        retCode = TRUE;
                    }
                }
                else
                {
                    aValue=  aScanData->Real.register2.data.energy.rateB;
                    retCode = TRUE;
                }
                break;
            }
        case SCHLUMBERGER_RATE_C:
            {
                if (aScanData->Real.register2.configFlag == 0)
                {
                    aValue=  aScanData->Real.register2.data.energy.rateC;
                    retCode = TRUE;
                }
                break;
            }
        case SCHLUMBERGER_RATE_D:
            {
                if (aScanData->Real.register2.configFlag == 0)
                {
                    aValue=  aScanData->Real.register2.data.energy.rateD;
                    retCode = TRUE;
                }
                break;
            }
        case SCHLUMBERGER_RATE_E:
            {

                if (aScanData->Real.register2.configFlag == 1)
                {
                    // make sure we have valid months
                    if (aScanData->Real.register2.data.demand.rateE.Month > 0 &&
                        aScanData->Real.register2.data.demand.rateE.Month < 13)
                    {
                        aPeak = RWTime (RWDate (aScanData->Real.register2.data.demand.rateE.Day,
                                                aScanData->Real.register2.data.demand.rateE.Month,
                                                RWDate().year()),
                                        aScanData->Real.register2.data.demand.rateE.Hour,
                                        aScanData->Real.register2.data.demand.rateE.Minute,
                                        0);
                        aValue = aScanData->Real.register2.data.demand.rateE.PeakValue;
                        retCode = TRUE;
                    }
                }
                else
                {
                    aValue=  aScanData->Real.register2.data.energy.rateE;
                    retCode = TRUE;
                }
                break;
            }
        default:
            break;

    }
    return retCode;
}

BOOL CtiDeviceVectron::getRateValueFromRegister3 (DOUBLE &aValue,
                                                  USHORT aRate,
                                                  RWTime &aPeak,
                                                  VectronScanData_t *aScanData)
{
    BOOL retCode = FALSE;
    aValue = 0.0;
    aPeak = rwEpoch;

    switch (aRate)
    {
        case SCHLUMBERGER_RATE_A:
        case SCHLUMBERGER_RATE_B:
        case SCHLUMBERGER_RATE_C:
        case SCHLUMBERGER_RATE_D:
            break;
        case SCHLUMBERGER_RATE_E:
            {
                if (aScanData->Real.register3.configFlag == 1)
                {
                    // make sure we have valid months
                    if (aScanData->Real.register3.data.demand.rateE.Month > 0 &&
                        aScanData->Real.register3.data.demand.rateE.Month < 13)
                    {
                        aPeak = RWTime (RWDate (aScanData->Real.register3.data.demand.rateE.Day,
                                                aScanData->Real.register3.data.demand.rateE.Month,
                                                RWDate().year()),
                                        aScanData->Real.register3.data.demand.rateE.Hour,
                                        aScanData->Real.register3.data.demand.rateE.Minute,
                                        0);
                        aValue = aScanData->Real.register3.data.demand.rateE.PeakValue;
                        retCode = TRUE;
                    }
                }
                else
                {
                    aValue=  aScanData->Real.register3.data.energy.rateE;
                    retCode = TRUE;
                }
                break;
            }
        default:
            break;
    }
    return retCode;
}

BOOL CtiDeviceVectron::getRateValueFromRegister4 (DOUBLE &aValue,
                                                  USHORT aRate,
                                                  RWTime &aPeak,
                                                  VectronScanData_t *aScanData)
{
    BOOL retCode = FALSE;
    aValue = 0.0;
    aPeak = rwEpoch;

    switch (aRate)
    {
        case SCHLUMBERGER_RATE_A:
        case SCHLUMBERGER_RATE_B:
        case SCHLUMBERGER_RATE_C:
        case SCHLUMBERGER_RATE_D:
            break;
        case SCHLUMBERGER_RATE_E:
            {
                if (aScanData->Real.register4.configFlag == 1)
                {
                    // make sure we have valid months
                    if (aScanData->Real.register4.data.demand.rateE.Month > 0 &&
                        aScanData->Real.register4.data.demand.rateE.Month < 13)
                    {
                        aPeak = RWTime (RWDate (aScanData->Real.register4.data.demand.rateE.Day,
                                                aScanData->Real.register4.data.demand.rateE.Month,
                                                RWDate().year()),
                                        aScanData->Real.register4.data.demand.rateE.Hour,
                                        aScanData->Real.register4.data.demand.rateE.Minute,
                                        0);
                        aValue = aScanData->Real.register4.data.demand.rateE.PeakValue;
                        retCode = TRUE;
                    }
                }
                else
                {
                    aValue=  aScanData->Real.register4.data.energy.rateE;
                    retCode = TRUE;
                }
                break;
            }
        default:
            break;
    }
    return retCode;
}

// default constructor that takes 2 optional parameters
CtiDeviceVectron::CtiDeviceVectron ( BYTE         *dataPtr,
                   BYTE         *mmPtr,
                   BYTE         *timeDatePtr,
                   BYTE         *mmlProfilePtr,
                   BYTE         *mmlProfileInputPtr,
                   BYTE         *lProfilePtr,
                   ULONG        totalByteCount) :
    iCommandPacket (0),
   CtiDeviceSchlumberger(dataPtr,
                  mmPtr,
                  timeDatePtr,
                  mmlProfilePtr,
                  mmlProfileInputPtr,
                  lProfilePtr,
                  totalByteCount)
{
   setRetryAttempts(SCHLUMBERGER_RETRIES);
}

CtiDeviceVectron::~CtiDeviceVectron()
{
   // all the databuffers are destroyed in base if needed

}





