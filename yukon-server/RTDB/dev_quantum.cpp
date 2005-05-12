/*-----------------------------------------------------------------------------*
 *
 * File:   dev_quantum.cpp
 *
 * Class:  CtiDeviceQuantum
 * Date:   06/05/2001
 *
 * Author: Matthew Fisher
 *
 *         Most architecture copied from dev_vectron.cpp
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw/rwtime.h>
#include <rw/rwdate.h>
#include <string.h>
#include "numstr.h"

#include "porter.h"
#include "dev_schlum.h"
#include "dev_quantum.h"

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
#include "utility.h"


SchlumbergerCommandBlk_t quantumScanCommands[] =
{
    //  block start addr, block end addr, offset, bytes to read, description
    {   0,  107, (  0 -   0),   1, "Software revision"},
    {   0,  107, ( 22 -   0),   3, "Current record"},
    {   0,  107, ( 25 -   0),   1, "Current interval"},
    {   0,  107, ( 26 -   0),  48, "Channel program"},
    {   0,  107, ( 74 -   0),   1, "Num of channels"},
    {   0,  107, ( 75 -   0),   1, "Bit size of mass mem"},
    {   0,  107, ( 76 -   0),   1, "Num of intervals in a record"},
    {   0,  107, ( 77 -   0),   1, "Interval length in min"},
    {   0,  107, ( 79 -   0),   3, "Mass mem start"},
    {   0,  107, ( 82 -   0),   3, "Mass mem end"},
    {   0,  107, ( 85 -   0),   7, "Real time"},
    {   0,  107, ( 92 -   0),  16, "Mass mem phase table"},
//    { 314.  326, (314 - 314),   1, "Meter type, minor revision"},
//    { 408,  413, (408 - 408),   3, "To DST"},     //  we'll never use these guys - we report only what time the meter says, and
//    { 408,  413, (411 - 408),   3, "From DST"},   //    apply no correction - leaving it in would be an unnecessary transfer at 1200 baud
    { 315,  326, (315 - 315),   4, "Inst reg mult, W, Var, V^2, A^2"},
    { 315,  326, (319 - 315),   4, "Inst reg mult, V"},
    { 315,  326, (323 - 315),   4, "Inst reg mult, A"},
    { 624,  830, (624 - 624),   2, "Kt Load Rate Indicator"},
    { 624,  830, (669 - 624),   5, "Amp demand reg"},
    { 624,  830, (680 - 624), 128, "Program table"},
    { 624,  830, (809 - 624),   2, "Demand interval setup"},
    { 624,  830, (811 - 624),  20, "Multipliers"},
    { 831, 1022, (831 - 831), 192, "Programmed registers"},
    { NULL, NULL, NULL, NULL, NULL }
};


SchlumbergerCommandBlk_t quantumLPCommands[] =
{
    //  block start addr, block end addr, offset, bytes to read, description
    {   0,  107, (  0 -   0),   1, "Software revision"},
    {   0,  107, ( 22 -   0),   3, "Current record"},
    {   0,  107, ( 25 -   0),   1, "Current interval"},
    {   0,  107, ( 26 -   0),  48, "Channel program"},
    {   0,  107, ( 74 -   0),   1, "Num of channels"},
    {   0,  107, ( 75 -   0),   1, "Bit size of mass mem"},
    {   0,  107, ( 76 -   0),   1, "Num of intervals in a record"},
    {   0,  107, ( 77 -   0),   1, "Interval length in min"},
    {   0,  107, ( 79 -   0),   3, "Mass mem start"},
    {   0,  107, ( 82 -   0),   3, "Mass mem end"},
    {   0,  107, ( 85 -   0),   7, "Real time"},
    {   0,  107, ( 92 -   0),  16, "Mass mem phase table"},
//    { 314.  326, (314 - 314),   1, "Meter type, minor revision"},
//    { 408,  413, (408 - 408),   3, "To DST"},     //  we'll never use these guys - we report only what time the meter says, and
//    { 408,  413, (411 - 408),   3, "From DST"},   //    apply no correction - leaving it in would be an unnecessary transfer at 1200 baud
    { 315,  326, (315 - 315),   4, "Inst reg mult, W, Var, V^2, A^2"},
    { 315,  326, (319 - 315),   4, "Inst reg mult, V"},
    { 315,  326, (323 - 315),   4, "Inst reg mult, A"},
    { 624,  830, (624 - 624),   2, "Kt Load Rate Indicator"},
    { 624,  830, (669 - 624),   5, "Amp demand reg"},
    { 624,  830, (680 - 624), 128, "Program table"},
    { 624,  830, (809 - 624),   2, "Demand interval setup"},
    { 624,  830, (811 - 624),  20, "Multipliers"},
    { NULL, NULL, NULL, NULL, NULL }
};

QuantumRegisterMappings_t regMap[] =
{
    //  mass memory phase is stored differently than normal registers -
    //    mm:     a:0 b:1 c:2 aggregate:3
    //    normal: a:1 b:2 c:4 aggregate:8 none:0 (turn 8 to 0 in code)

    //  regnum, phase, loadprofile, ctioffset
    { 1, 0, 0,  1},  //  OFFSET_TOTAL_KWH
    {22, 0, 0,  2},  //  OFFSET_PEAK_KW_OR_RATE_A_KW
    {16, 0, 0, 14},  //  OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW
    { 1, 3, 1, 15},  //  OFFSET_LOADPROFILE_KW
    { 3, 0, 0, 21},  //  OFFSET_TOTAL_KVARH
    {24, 0, 0, 22},  //  OFFSET_PEAK_KVAR_OR_RATE_A_KVAR
    {18, 0, 0, 34},  //  OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR
    { 3, 3, 1, 35},  //  OFFSET_LOADPROFILE_KVAR
/*    {37, 0, 1, 36},  //  OFFSET_LOADPROFILE_QUADRANT1_KVAR
    {38, 0, 1, 37},  //  OFFSET_LOADPROFILE_QUADRANT2_KVAR          ?????????????
    {39, 0, 1, 38},  //  OFFSET_LOADPROFILE_QUADRANT3_KVAR          ?????????????
    {40, 0, 1, 39},  //  OFFSET_LOADPROFILE_QUADRANT4_KVAR  */
    {11, 0, 0, 41},  //  OFFSET_TOTAL_KVAH
    {30, 0, 0, 42},  //  OFFSET_PEAK_KVA_OR_RATE_A_KVA
    {18, 0, 0, 54},  //  OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA
    {11, 3, 1, 55},  //  OFFSET_LOADPROFILE_KVA
/*    { 1, 0, 0, 56},  //  OFFSET_LOADPROFILE_QUADRANT1_KVA
    { 1, 0, 0, 57},  //  OFFSET_LOADPROFILE_QUADRANT2_KVA          xxxxx
    { 1, 0, 0, 58},  //  OFFSET_LOADPROFILE_QUADRANT3_KVA          xxxxx
    { 1, 0, 0, 59},  //  OFFSET_LOADPROFILE_QUADRANT4_KVA  */
    {71, 1, 0, 61},  //  OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE
    {71, 2, 0, 63},  //  OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE
    {71, 4, 0, 65},  //  OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE
    {72, 1, 0, 71},  //  OFFSET_INSTANTANEOUS_PHASE_A_CURRENT
    {72, 2, 0, 73},  //  OFFSET_INSTANTANEOUS_PHASE_B_CURRENT
    {72, 4, 0, 75},  //  OFFSET_INSTANTANEOUS_PHASE_C_CURRENT
    {94, 0, 1, 62},  //  OFFSET_LOADPROFILE_PHASE_A_VOLTAGE
    {94, 1, 1, 64},  //  OFFSET_LOADPROFILE_PHASE_B_VOLTAGE
    {94, 2, 1, 66},  //  OFFSET_LOADPROFILE_PHASE_C_VOLTAGE
    {93, 0, 1, 72},  //  OFFSET_LOADPROFILE_PHASE_A_CURRENT
    {93, 1, 1, 74},  //  OFFSET_LOADPROFILE_PHASE_B_CURRENT
    {93, 2, 1, 76},  //  OFFSET_LOADPROFILE_PHASE_C_CURRENT
/*    { 1, 0, 0, 91},  //  OFFSET_LOADPROFILE_CHANNEL_1
    { 1, 0, 0, 93},  //  OFFSET_LOADPROFILE_CHANNEL_2
    { 1, 0, 0, 95},  //  OFFSET_LOADPROFILE_CHANNEL_3
    { 1, 0, 0, 97},  //  OFFSET_LOADPROFILE_CHANNEL_4
    { 1, 0, 0, 90},  //  OFFSET_TOTAL_CHANNEL_1
    { 1, 0, 0, 92},  //  OFFSET_TOTAL_CHANNEL_2
    { 1, 0, 0, 94},  //  OFFSET_TOTAL_CHANNEL_3
    { 1, 0, 0, 96},   //  OFFSET_TOTAL_CHANNEL_4  */
    {-1, -1, -1, -1}
};


INT CtiDeviceQuantum::getCommandPacket( ) const
{
    return _commandPacket;
}

CtiDeviceQuantum &CtiDeviceQuantum::setCommandPacket( INT aCmd )
{
    _commandPacket = aCmd;
    return *this;
}


ULONG CtiDeviceQuantum::getBasePageStart( ) const
{
    return _basePageStart;
}


CtiDeviceQuantum &CtiDeviceQuantum::setBasePageStart( ULONG pageStart )
{
    _basePageStart = pageStart;
    return *this;
}


//  this routine was copied straight over from CtiDeviceSchlumberger, because
//    the Quantum has multiple ways of handling slave devices.
//  daisy-chained master-slave implemented.
INT CtiDeviceQuantum::GeneralScan( CtiRequestMsg *pReq,
                                   CtiCommandParser &parse,
                                   OUTMESS *&OutMessage,
                                   RWTPtrSlist< CtiMessage > &vgList,
                                   RWTPtrSlist< CtiMessage > &retList,
                                   RWTPtrSlist< OUTMESS > &outList,
                                   INT ScanPriority )
{
    INT status = NORMAL;

    ULONG BytesWritten;

    // load profile information
    time_t         RequestedTime = 0L;
    time_t         DeltaTime;
    time_t         NowTime;

    {
        CtiLockGuard<CtiLogger> dout_guard( dout );
        dout << RWTime( ) << " General Scan of device " << getName() << " in progress " << endl;
    }

    if( OutMessage != NULL )
    {
        OutMessage->Buffer.DUPReq.Identity = IDENT_QUANTUM;

        /*************************
         *
         *   setting the current command in hopes that someday we don't have to use the command
         *   bits in the in message to decide what we're doing DLS
         *
         *************************
         */
        setCurrentCommand(CmdScanData);
        OutMessage->Buffer.DUPReq.Command[0] = CmdScanData;      // One call does it all...

        OutMessage->Buffer.DUPReq.LP_Time      = getLastLPTime().seconds();
        OutMessage->Buffer.DUPReq.LastFileTime = getLastLPTime().seconds();

        // Load all the other stuff that is needed
        OutMessage->DeviceID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();



        // if this is a slave, drop the priority
        if( isMaster( ) )
        {
            EstablishOutMessagePriority( OutMessage, ScanPriority );
        }
        else
        {
            //  slave 1 = 1, slave 2 = 2, slave 3 = 3, slave 4 = 4 - so we're demoting priority based on
            //    position in the daisy chain.
            OverrideOutMessagePriority(OutMessage, ScanPriority - getIED( ).getSlaveAddress( ));
        }

        OutMessage->TimeOut   = 2;
        OutMessage->EventCode = RESULT | ENCODED;
        OutMessage->Sequence  = 0;
        OutMessage->Retry     = 3;

        outList.insert(OutMessage);
        OutMessage = NULL;
    }
    else
    {
        status = MEMORY;
    }

    return status;

}


INT CtiDeviceQuantum::generateCommandHandshake( CtiXfer &Transfer, RWTPtrSlist< CtiMessage > &traceList )
{
    USHORT retCode = NORMAL;

    switch( getCurrentState( ) )
    {
        case StateHandshakeInitialize:
            {
                //  initialize our retry attempts...
                setRetryAttempts( SCHLUMBERGER_RETRIES );
                //  ...  and attempts remaining
                setAttemptsRemaining( getRetryAttempts( ) );
                //  this for the ACK count
                setTotalByteCount( 0UL );
            }  //  fall through to next state
        case StateHandshakeSendStart:
        //  this gets no response from the quantums i've talked to so far - it times out.
        //    however, SCS protocol says that it's a valid method...  so it's here for reference
        //    in case we ever need to use it (i.e. phone line sharing or some such).
/*          {
#ifndef UTS_ID
#define UTS_ID 1
#endif

#ifdef UTS_ID
                //  send out a universal "I" command - the meter should respond with an
                //    ASCII string 15 bytes long of the form "<model>,<version> " (with
                //    spaces padding it out to 15 if necessary), followed by a carriage return.
                //    this is not a required part of the communications initiation - that starts
                //    at "StateHandshakeSendAttn" - we're just making sure we're talking to the right guy.
                Transfer.getOutBuffer( )[0] = 'I';
                Transfer.setOutCount( 1 );
                Transfer.setInCountExpected( 16 );
                //  wait 1 second for the reply
                Transfer.setInTimeout( 1 );
                //  No CRC machinations
                Transfer.setCRCFlag( 0 );
                memset( Transfer.getInBuffer( ), 0x00, 20 );
#endif
                setCurrentState( StateHandshakeDecodeStart );
                break;
            }
*/      case StateHandshakeSendAttn:
            {
                //  get the meter's attention...
                Transfer.getOutBuffer( )[0] = ENQ;
                Transfer.setOutCount( 1 );
                Transfer.setInCountExpected( 1 );
                Transfer.setCRCFlag( 0 );
                Transfer.setInTimeout( 1 );
                memset( Transfer.getInBuffer( ), 0x00, 20 );

                setCurrentState( StateHandshakeDecodeAttn );
                break;
            }
        case StateHandshakeSendIdentify:
            {
                Transfer.getOutBuffer( )[0]  = 'I';
                //  set the unit type and unit ID fields to all null (wildcard) so
                //    the meter will respond and tell us who he is
                Transfer.getOutBuffer( )[1]  = 0;
                Transfer.getOutBuffer( )[2]  = 0;
                Transfer.getOutBuffer( )[3]  = 0;
                Transfer.getOutBuffer( )[4]  = 0;
                Transfer.getOutBuffer( )[5]  = 0;
                Transfer.getOutBuffer( )[6]  = 0;
                Transfer.getOutBuffer( )[7]  = 0;
                Transfer.getOutBuffer( )[8]  = 0;
                Transfer.getOutBuffer( )[9]  = 0;
                Transfer.getOutBuffer( )[10] = 0;
                Transfer.getOutBuffer( )[11] = 0;

                Transfer.setOutCount( 12 );
                Transfer.setInCountExpected( 20 );
                Transfer.setInTimeout( 1 );
                //  the non-universal 'I' command uses the CRC on both send and receive.
                Transfer.setCRCFlag( XFER_ADD_CRC | XFER_VERIFY_CRC );
                setCurrentState( StateHandshakeDecodeIdentify );
                break;
            }
        case StateHandshakeSendSecurity:
            {
                //  send the security code command
                Transfer.getOutBuffer()[0] = 'S';

                USHORT passwordLength = getIED( ).getPassword( ).length( );

                //  the code is ASCII, so we have to subtract 0x30 while copying it
                for( int z=0; z < 8; z++ )
                {
                    if( z < passwordLength )
                    {
                        //  getPassword( ) returns an RWCString, and so is indexed with (z).
                        //    wacky syntax.
                        Transfer.getOutBuffer( )[z+1] = getIED( ).getPassword( )(z);
                    }
                    else
                    {
                        //  pad the password out with nulls, if need be
                        Transfer.getOutBuffer()[z+1] = '0' - 0x30;
                    }

                }

                Transfer.setOutCount( 9 );
                //  we should just get an ACK back from this, so there'll be no CRC attached
                Transfer.setCRCFlag( XFER_ADD_CRC );

                Transfer.setInCountExpected( 1 );
                Transfer.setInTimeout( 1 );
                setCurrentState( StateHandshakeDecodeSecurity );
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> dout_guard( dout );
                    dout << RWTime( ) << " (" << __LINE__ << ") " << getName() << " - generating handshake command - Invalid state " << getCurrentState( )  << endl;
                }
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState( StateHandshakeAbort );
                retCode = !NORMAL;
            }
    }
    return retCode;
}


INT CtiDeviceQuantum::generateCommand( CtiXfer &Transfer, RWTPtrSlist< CtiMessage > &traceList )
{
    SchlMeterStruct MeterSt;
    USHORT          retCode = NORMAL;

    //  generate the appropriate command
    switch( getCurrentCommand( ) )
    {
        case CmdSelectMeter:
            {
                retCode = generateCommandSelectMeter( Transfer, traceList );
                break;
            }

        case CmdScanData:
            {
                retCode = generateCommandScan( Transfer, traceList );
                break;
            }

        case CmdLoadProfileData:
            {
                retCode = generateCommandLoadProfile( Transfer, traceList );
                break;
            }

        default:
            {
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState( StateScanAbort );
                {
                    CtiLockGuard<CtiLogger> dout_guard( dout );
                    dout << RWTime( ) << " (" << __LINE__ << ") " << getName() << " - generating command - Invalid command " << getCurrentCommand( ) << endl;
                }
                retCode = StateScanAbort;
                break;
            }
    }

    return retCode;
}

INT CtiDeviceQuantum::generateCommandSelectMeter( CtiXfer &Transfer, RWTPtrSlist< CtiMessage > &traceList )
{
    SchlMeterStruct MeterSt;
    int             retCode = NORMAL;

    //  get appropriate data
    switch ( getCurrentState( ) )
    {
        case StateHandshakeInitialize:
        case StateHandshakeComplete:
        case StateScanValueSet1FirstScan:
            setAttemptsRemaining( 3 ); // = SclumbergerRetries;
        case StateHandshakeSendIdentify:
            {
                Transfer.getOutBuffer( )[0]  = 'I';
                //  set the unit type and unit ID fields to all null (wildcard) so
                //    the meter will respond and tell us who he is
                Transfer.getOutBuffer( )[1]  = 0;
                Transfer.getOutBuffer( )[2]  = 0;
                Transfer.getOutBuffer( )[3]  = 0;
                Transfer.getOutBuffer( )[4]  = 0;
                Transfer.getOutBuffer( )[5]  = 0;
                Transfer.getOutBuffer( )[6]  = 0;
                Transfer.getOutBuffer( )[7]  = 0;
                Transfer.getOutBuffer( )[8]  = 0;
                Transfer.getOutBuffer( )[9]  = 0;
                Transfer.getOutBuffer( )[10] = 0;
                Transfer.getOutBuffer( )[11] = 0;

                Transfer.setOutCount( 12 );
                Transfer.setInCountExpected( 20 );
                Transfer.setInTimeout( 1 );
                //  the non-universal 'I' command uses the CRC on both send and receive.
                Transfer.setCRCFlag( XFER_ADD_CRC | XFER_VERIFY_CRC );
                setCurrentState( StateHandshakeDecodeIdentify );
                break;
            }
        case StateHandshakeSendSecurity:
            {
                //  send the security code command
                Transfer.getOutBuffer()[0] = 'S';

                USHORT passwordLength = getIED( ).getPassword( ).length( );

                //  the code is ASCII, so we have to subtract 0x30 while copying it
                for( int z=0; z < 8; z++ )
                {
                    if( z < passwordLength )
                    {
                        //  getPassword( ) returns an RWCString, and so is indexed with (z).
                        //    wacky syntax.
                        Transfer.getOutBuffer( )[z+1] = getIED( ).getPassword( )(z);
                    }
                    else
                    {
                        //  pad the password out with nulls, if need be
                        Transfer.getOutBuffer()[z+1] = 0;
                    }
                }

                Transfer.setOutCount( 9 );
                //  we should just get an ACK back from this, so there'll be no CRC attached
                Transfer.setCRCFlag( XFER_ADD_CRC );

                Transfer.setInCountExpected( 1 );
                Transfer.setInTimeout( 1 );
                setCurrentState( StateHandshakeDecodeSecurity );
                break;
            }
        case StateScanValueSet1:
            {
                //  download the offline flag - the meter will transfer communications to
                //    the next meter in the daisy chain
                fillUploadTransferObject( Transfer, 406 + getBasePageStart( ), 406 + getBasePageStart( ) );

                //  set it to a download (meter receives data), thank you very much
                Transfer.getOutBuffer( )[0]    = 'D';
                //  we're expecting an ACK, you see
                Transfer.getInCountExpected( ) = 1;
                Transfer.setCRCFlag( XFER_ADD_CRC );

                setPreviousState( StateScanValueSet1 );
                setCurrentState( StateScanDecode1 );
                break;
            }
        case StateScanValueSet2FirstScan:
        case StateScanValueSet2:
            {
                //  the actual "offline" flag
                Transfer.getOutBuffer()[0] = 0xFF;

                Transfer.setOutCount( 1 );
                //  an ACK in response
                Transfer.setInCountExpected( 1 );
                Transfer.setCRCFlag( XFER_ADD_CRC );

                setPreviousState (StateScanValueSet2);
                setCurrentState (StateScanDecode2);
                break;

            }
        case StateScanValueSet3FirstScan:
            setAttemptsRemaining( 5 );
        case StateScanValueSet3:
            {
                //  we're waiting until we get zero bytes in the connection
                CTISleep( 500L );
                Transfer.setOutCount( 1 );
                Transfer.getOutBuffer( )[0] = ENQ;
                Transfer.setInCountExpected( 8 );
                Transfer.setCRCFlag( 0 );
                setPreviousState( StateScanValueSet3 );
                setCurrentState( StateScanDecode3 );
                break;
            }
        case StateScanValueSet4FirstScan:
        case StateScanValueSet4:
            {
                //  sending ENQs to get ACKs
                //    we need to do this up to 10 times
                Transfer.getOutBuffer( )[0] = ENQ;
                Transfer.setOutCount( 1 );
                Transfer.setInCountExpected( 1 );
                Transfer.setCRCFlag( 0 );
                Transfer.setInTimeout( 1 );
                setPreviousState( StateScanValueSet4 );
                setCurrentState( StateScanDecode4 );
                break;
            }
        default:
            {
                CtiLockGuard<CtiLogger> dout_guard( dout );
                dout << RWTime( ) << " (" << __LINE__ << ") " << getName() << " - generating select meter command - Invalid state " << getCurrentState( ) << endl;
            }
            Transfer.setOutCount( 0 );
            Transfer.setInCountExpected( 0 );
            setCurrentState( StateScanAbort );
            retCode = StateScanAbort;
    }

    return retCode;
}


INT CtiDeviceQuantum::generateCommandScan( CtiXfer &Transfer, RWTPtrSlist< CtiMessage > &traceList )
{
    int retCode = NORMAL;

    // get appropriate data
    switch( getCurrentState( ) )
    {
        case StateHandshakeComplete:
            {
                setRetryAttempts( SCHLUMBERGER_RETRIES );
                setCommandPacket( 0 );
                setTotalByteCount( 0 );
            }  //  we fall through...
        case StateScanValueSet1FirstScan:  // done to set attempts only first time thru DLS
            {
                setAttemptsRemaining( getRetryAttempts( ) ); // = SclumbergerRetries;
            }
        case StateScanValueSet1:
            {
                fillUploadTransferObject( Transfer,
                                          quantumScanCommands[getCommandPacket( )].startAddress + getBasePageStart( ),
                                          quantumScanCommands[getCommandPacket( )].stopAddress + getBasePageStart( ) );

                setPreviousState( StateScanValueSet1 );
                setCurrentState( StateScanDecode1 );
                break;
            }
        case StateScanResendRequest:
            {
                // must reset out count because it will add CRC again
                Transfer.setOutCount( 7 );
                setCurrentState( getPreviousState( ) );
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> dout_guard( dout );
                    dout << RWTime( ) << " (" << __LINE__ << ") " << getName() << " - generating scan command - Invalid state " << getCurrentState( ) << endl;
                }
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState( StateScanAbort );
                retCode = StateScanAbort;
            }
    }

    return retCode;
}


LONG CtiDeviceQuantum::getPreviousRecordLocation( LONG currentRecordAddress )
{
    QuantumConfigData_t  *mmCfg = ((QuantumConfigData_t *)_massMemoryConfig);

    LONG tmpAddress,
         recordSize;

    recordSize = 11 + ((6 + 90) * mmCfg->numChannels);

    tmpAddress = currentRecordAddress;

    if( tmpAddress == mmCfg->mmStart )
        tmpAddress = mmCfg->mmEnd - ((mmCfg->mmEnd - mmCfg->mmStart) % recordSize); //  the beginning of the memory after the last record

    tmpAddress -= recordSize;

    //  if it crosses any of the page boundaries, move again
    if( ((tmpAddress < 0x5fff) && ((tmpAddress + recordSize) > 0x5fff)) ||
        ((tmpAddress < 0xbeff) && ((tmpAddress + recordSize) > 0xbeff)) )
        tmpAddress = getPreviousRecordLocation( tmpAddress );

    return tmpAddress;
}


LONG CtiDeviceQuantum::getNextRecordLocation( LONG currentRecordAddress )
{
    QuantumConfigData_t  *mmCfg = ((QuantumConfigData_t *)_massMemoryConfig);

    LONG tmpAddress,
         recordSize;

    recordSize = 11 + ((6 + 90) * mmCfg->numChannels);

    tmpAddress = currentRecordAddress;

    tmpAddress += recordSize;

    if( tmpAddress > (mmCfg->mmEnd - recordSize) )
    {
        tmpAddress = mmCfg->mmStart;
    }

    //  if it crosses any of the page boundaries, move again
    if( ((tmpAddress < 0x5fff) && ((tmpAddress + recordSize) > 0x5fff)) ||
        ((tmpAddress < 0xbeff) && ((tmpAddress + recordSize) > 0xbeff)) )
        tmpAddress = getNextRecordLocation( tmpAddress );

    return tmpAddress;
}


INT CtiDeviceQuantum::generateCommandLoadProfile (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList )
{
    //  grab the mass memory config
    QuantumConfigData_t  *mmCfg       = (QuantumConfigData_t *)_massMemoryConfig;
    ULONG                *lastLPTime  = (ULONG *)_loadProfileTimeDate;

    INT     retCode = NORMAL;
    INT     recordSize;
    LONG    recordDuration;
    LONG    chunkStart,
            chunkEnd;
    LONG    tmpRecordAddress;
    ULONG   tmpRecordTime;
    INT     sanityCheck;


    switch( getCurrentState( ) )
    {
        case StateHandshakeComplete:
        case StateScanComplete:
            {
                setRetryAttempts( SCHLUMBERGER_RETRIES );
                setCommandPacket( 0 );
                setTotalByteCount( 0 );
            }  //  we fall through to...
        case StateScanValueSet1FirstScan:  // done to set attempts only first time thru DLS
            {
                setAttemptsRemaining( getRetryAttempts( ) ); // = SclumbergerRetries;
            }  //  and fall through again to...
        case StateScanValueSet1:
            {
                //  snag the next part of the config info
                fillUploadTransferObject( Transfer,
                                          quantumLPCommands[getCommandPacket( )].startAddress + getBasePageStart( ),
                                          quantumLPCommands[getCommandPacket( )].stopAddress + getBasePageStart( ) );

                setPreviousState( StateScanValueSet1 );
                setCurrentState( StateScanDecode1 );
                break;
            }
        case StateScanValueSet2FirstScan:
        case StateScanValueSet2:
            {
                //  start at the last record, which is guaranteed to be complete
                //    (unless it's uninitialized, but that's boundchecked in the decode)
                chunkStart = getPreviousRecordLocation( mmCfg->currentRecord );

                //  just get the time/date of the last (complete) record
                fillUploadTransferObject( Transfer, chunkStart, chunkStart + 2 );

                setCurrentState( StateScanDecode2 );

                break;
            }
        case StateScanValueSet3FirstScan:
            {
                //  this block walks back in memory to the first record we need to get.  then we proceed forward in the following
                //    LP requests until we've fetched the current record as indicated in the QuantumConfig data.
                //    this allows us to update porterLPTime on the scanner side with every record that comes back.


                //  we can't compute the beginning time of the current record from the current interval - apparently they're not
                //    synchronized, so we may end up off by an interval length, time-wise.  this is almost correctable, because
                //    the only start time allowed for records is the top of an hour (we can do a sanity check on values like 12:05).
                //    we can sync with that in all cases except when the interval itself is an hour - in which case we have no
                //    idea when it should be.
                //  so we have to go to the previous record's timestamp to get the goods, which is performed in StateScanValueSet2
                //    and StateScanDecode2.
/*
                //  today's date at the top of the current hour (in seconds, via RWTime)
                currentRecordTime = RWTime( RWDate( (INT)mmCfg->realTime.dayOfMonth,
                                                    (INT)mmCfg->realTime.month,
                                                    (INT)mmCfg->realTime.year + 2000 ),
                                            (INT)mmCfg->realTime.hour ).seconds( );

                //  set currentRecordTime to be the time of the start of the current record
                currentRecordTime += (mmCfg->realTime.minute
                                         - (mmCfg->realTime.minute % mmCfg->intervalLength)  //  get to the beginning of this interval
                                                                                             //    (60 is a multiple of all interval sizes,
                                                                                             //       so this will always work)
                                         - (mmCfg->currentInterval * mmCfg->intervalLength)  //  minus all other intervals in this record
                                         ) * 60;  //  into seconds
 */

                recordSize = 11 + ((6 + 90) * mmCfg->numChannels);
                recordDuration = mmCfg->intervalLength * mmCfg->numIntervalsInRecord * 60;  //  convert from minutes to seconds

                _currentRecordLocation = mmCfg->currentRecord;
                tmpRecordAddress       = _currentRecordLocation;
                tmpRecordTime          = _currentRecordTime;

                //  we want to grab the record just ahead of the lastLPTime/interval, because we might've just done a whole interval
                //    and also make sure we don't go back more than 31 days
                while( tmpRecordTime > (*lastLPTime + (mmCfg->intervalLength * 60)) &&
                       tmpRecordTime > (_currentRecordTime - (31 * 24 * 60 * 60)) )
                {
                    tmpRecordAddress = getPreviousRecordLocation( tmpRecordAddress );
                    //  if we've looped around (i.e. if the lastLPTime is longer ago than the memory can keep track of)
                    if( tmpRecordAddress == _currentRecordLocation )
                    {
                        tmpRecordAddress = getNextRecordLocation( tmpRecordAddress );
                        *lastLPTime = tmpRecordTime;
                    }
                    else
                    {
                        tmpRecordTime -= recordDuration;
                    }
                }

                _currentRecordTime     = tmpRecordTime;
                _currentRecordLocation = tmpRecordAddress;

                setTotalByteCount( 0 );

            }  //  fall through...
        case StateScanValueSet3:
            {
                recordSize = 11 + ((6 + 90) * mmCfg->numChannels);

                chunkStart = _currentRecordLocation + getTotalByteCount( );

                if( DebugLevel & 0x0001 )
                {
                    CtiLockGuard<CtiLogger> dout_guard( dout );
                    dout << RWTime( ) << " (" << __LINE__ << ") " << getName() << " - requesting record beginning at " << RWTime( _currentRecordTime ) << endl;
                }

                //  if we have the whole record this time
                if( (getTotalByteCount( ) + 250) > recordSize )
                {
                    //  get recordSize bytes
                    chunkEnd = _currentRecordLocation + recordSize - 1;
                }
                else
                {
                    //  get 250 bytes
                    chunkEnd = _currentRecordLocation + getTotalByteCount( ) + 250 - 1;
                }

                setPreviousState( StateScanValueSet3 );
                setCurrentState( StateScanDecode3 );

                fillUploadTransferObject( Transfer, chunkStart, chunkEnd );

                break;
            }
        case StateScanResendRequest:
            {
                // must reset out count because it will add CRC again
                Transfer.setOutCount( 7 );
                setCurrentState( getPreviousState( ) );
                break;
            }

        default:
            {
                {
                    CtiLockGuard<CtiLogger> dout_guard( dout );
                    dout << RWTime( ) << " (" << __LINE__ << ") " << getName() << " - generating load profile command - Invalid state " << getCurrentState( ) << endl;
                }
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState( StateScanAbort );
                retCode = StateScanAbort;
            }
    }

    return retCode;
}


INT CtiDeviceQuantum::decodeResponseHandshake( CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList )
{
    SchlMeterStruct   MeterSt;
    int retCode = NORMAL;

    setAttemptsRemaining( getAttemptsRemaining( ) - 1 );

    switch( getCurrentState( ) )
    {
        case StateHandshakeDecodeStart:
//  see generateCommandHandshake for why this is commented out
/*          {
                //  ID string seems correct for meters tested so far...
                //  strncmp returns zero (false) if strings match, so apply logical negation to check for match
                if( !strncmp( (CHAR *)Transfer.getInBuffer( ), "SI,QTM        \r", 10) )
                {
                    //  meter ID'd okay, keep going
                    setAttemptsRemaining( getRetryAttempts( ) );
                    setCurrentState( StateHandshakeSendAttn );
                }
                else if( Transfer.getInBuffer( )[0] == NAK ||
                         Transfer.getInBuffer( )[0] == ACK )
                {
                    //  if we get a response back with an ACK or NAK (unexpected/bad for the
                    //    Universal I command), we just skip ahead to StateHandshakeSendIdentify
                    //    and try to have him identify himself.
                    setAttemptsRemaining( getRetryAttempts( ) );
                    setCurrentState( StateHandshakeSendIdentify );

                    if( Transfer.doTrace( ERRUNKNOWN ) )
                    {
                        CtiLockGuard<CtiLogger> dout_guard( dout );
                        dout << RWTime( ) << ((Transfer.getInBuffer( )[0] == NAK)?" NAK:":" ACK:")
                             << " Quantum " << getName() << " already online" << endl;
                    }
                }
                else
                {
                    //  the ID string didn't match, but we didn't get anything crazy like an ACK or
                    //    NAK, either...  try again.
                    if( getAttemptsRemaining( ) > 0 )
                    {
                        setCurrentState( StateHandshakeSendStart );
                    }
                    else
                    {
                        //  otherwise, we've run out of attempts to Universal ID the sucker,
                        //    so give up and go on to the next handshake phase.
                        setAttemptsRemaining( getRetryAttempts( ) );
                        setCurrentState( StateHandshakeSendAttn );
                    }
                }
                break;
            }
*/      case StateHandshakeDecodeAttn:
            {
                //  if we got an ACK with no problems
                if( Transfer.getInBuffer( )[0] == ACK && !commReturnValue )
                {
                    //  we decrement retryattempts by default up above, so we increment (un-decrement?) when successful
                    setAttemptsRemaining( getRetryAttempts( ) + 1 );
                    //  increment the number of ACKs we've received
                    setTotalByteCount( getTotalByteCount( ) + 1 );

                    //  got what we were expecting, continue on
                    if( getTotalByteCount( ) < 10 )
                    {
                        setCurrentState( StateHandshakeSendAttn );
                        setAttemptsRemaining( getRetryAttempts( ) );
                    }
                    //  we need more ACKs
                    else
                    {
                        setCurrentState( StateHandshakeSendIdentify );
                    }
                }
                //  do we have more than one attempt left?
                else if( getAttemptsRemaining( ) > 0 )
                {
                    //  if so, try again...
                    setCurrentState( StateHandshakeSendAttn );
                }
                else
                {
                    //  otherwise, abort the handshake - this was our last attempt, and the
                    //    meter's not respoding to the ENQs.
                    setCurrentState( StateHandshakeAbort );
                    retCode = StateHandshakeAbort;
                }
                break;
            }
        case StateHandshakeDecodeIdentify:
            {
                if( Transfer.getInBuffer( )[0] == ACK && !commReturnValue )
                {
                    //  get the meter's mass memory beginning and end to compute its size
                    MeterSt.Start = StrToUlong( &Transfer.getInBuffer( )[12], 3 );

                    setBasePageStart( MeterSt.Start );

                    MeterSt.Stop  = StrToUlong( &Transfer.getInBuffer( )[15], 3 );
                    MeterSt.Length = MeterSt.Stop - MeterSt.Start + 1;
                    //  copy the unit type and device ID into the meter struct
                    memcpy( &MeterSt.UnitType, &Transfer.getInBuffer( )[1], 3 );
                    MeterSt.UnitType[3] = '\0';
                    memcpy( &MeterSt.UnitId, &Transfer.getInBuffer( )[4], 8 );
                    MeterSt.UnitId[8] = '\0';
                    // save it for later
                    setMeterParams( MeterSt );

                    //  reset our attempts for the next state
                    setAttemptsRemaining( getRetryAttempts( ) );
//                    setCurrentState( StateHandshakeComplete );
                    setCurrentState( StateHandshakeSendSecurity );
                }
                else if( getAttemptsRemaining( ) > 0 )
                {
                    //  we have at least one more shot at it, so try again
                    setCurrentState( StateHandshakeSendIdentify );
                }
                else
                {
                    //  we've run out of attempts, abort the handshake
                    setCurrentState( StateHandshakeAbort );
                    retCode = StateHandshakeAbort;
                }
                break;
            }
        case StateHandshakeDecodeSecurity:
            {
                if( !commReturnValue && Transfer.getInBuffer( )[0] == ACK )
                {
                    // handshake is done, move on
                    setCurrentState( StateHandshakeComplete );
                }
                else if( commReturnValue )
                {
                    if( Transfer.doTrace( READTIMEOUT ) )
                    {
                        CtiLockGuard<CtiLogger> dout_guard( dout );
                        dout << RWTime( ) << " " << getName() << " - no response to password" << endl;
                    }

                    //  if the meter doesn't respond, wait for 2 seconds before trying again
                    //    (2 seconds is the meter's intercharacter timeout)
                    CTISleep( 2000 );
                }
                else if( getAttemptsRemaining( ) > 0 )
                {
                    setCurrentState( StateHandshakeSendSecurity );
                }
                else
                {
                    //  no ACK from the meter...  abort, we can't verify security code
                    setCurrentState( StateHandshakeAbort );
                    retCode = StateHandshakeAbort;
                }
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> dout_guard( dout );
                    dout << RWTime( ) << " (" << __LINE__ << ") " << getName() << " - decoding handshake - Invalid state " << getCurrentState( ) << endl;
                }
                setCurrentState( StateHandshakeAbort );
                retCode = StateHandshakeAbort;
                break;
            }
    }
    return retCode;
}


INT CtiDeviceQuantum::decodeResponse( CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList )
{
    SchlMeterStruct MeterSt;
    USHORT retCode = NORMAL;

    //  decode the response with the appropriate function
    switch( getCurrentCommand( ) )
    {
        case CmdSelectMeter:
            {
                retCode = decodeResponseSelectMeter( Transfer, commReturnValue, traceList );
                break;
            }

        case CmdScanData:
            {
                retCode = decodeResponseScan( Transfer, commReturnValue, traceList );
                break;
            }

        case CmdLoadProfileData:
            {
                retCode = decodeResponseLoadProfile( Transfer, commReturnValue, traceList );
                break;
            }

        default:
            {
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState( StateScanAbort );
                {
                    CtiLockGuard<CtiLogger> dout_guard( dout );
                    dout << RWTime( ) << " (" << __LINE__ << ") " << getName() << " - decoding - Invalid command " << getCurrentCommand( ) << endl;
                }
                retCode = StateScanAbort;
                break;
            }
    }

    return retCode;
}



INT CtiDeviceQuantum::decodeResponseSelectMeter( CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList )
{
    int retCode = NORMAL;
    SchlMeterStruct   MeterSt;

    setAttemptsRemaining( getAttemptsRemaining( ) - 1 );

    // get appropriate data
    switch( getCurrentState( ) )
    {
        case StateHandshakeDecodeIdentify:
            {
                if( Transfer.getInBuffer( )[0] == ACK && !commReturnValue )
                {
                    //  get the meter's mass memory beginning and end to compute its size
                    MeterSt.Start = StrToUlong( &Transfer.getInBuffer( )[12], 3 );

                    setBasePageStart( MeterSt.Start );

                    MeterSt.Stop  = StrToUlong( &Transfer.getInBuffer( )[15], 3 );
                    MeterSt.Length = MeterSt.Stop - MeterSt.Start + 1;
                    //  copy the unit type and device ID into the meter struct
                    memcpy( &MeterSt.UnitType, &Transfer.getInBuffer( )[1], 3 );
                    MeterSt.UnitType[3] = '\0';
                    memcpy( &MeterSt.UnitId, &Transfer.getInBuffer( )[4], 8 );
                    MeterSt.UnitId[8] = '\0';
                    // save it for later
                    setMeterParams( MeterSt );

                    //  wipe out trailing spaces
                    *(strchr( MeterSt.UnitId, ' ' )) = '\0';

/*                    {
                        CtiLockGuard<CtiLogger> dout_guard( dout );
                        dout << RWTime( ) << " (" << __LINE__ << ") meterst.unitid: '" << MeterSt.UnitId << "'" << endl;
                        dout << RWTime( ) << " (" << __LINE__ << ") devicename    : '" << getName() << "'" << endl;
                        dout << RWTime( ) << " (" << __LINE__ << ") substring     : '" << getName().subString( MeterSt.UnitId, getName().length( ) - strlen( MeterSt.UnitId ) - 1 ) << "'" << endl;
                    }
 */
                    if( getName().subString( MeterSt.UnitId, getName().length( ) - strlen( MeterSt.UnitId ) - 1 ) == MeterSt.UnitId )
                    {
                        //  bad name for the state, but since we're not a handshake command, we get the catch-all ScanComplete state
                        //  this is our meter, we're done
                        setCurrentState( StateScanComplete );
                    }
                    else
                    {
                        //  reset our attempts for the next state
                        setAttemptsRemaining( getRetryAttempts( ) );
                        //  this isn't our meter, get ready to do the switch
                        setCurrentState( StateHandshakeSendSecurity );
                    }
                }
                else if( getAttemptsRemaining( ) > 0 )
                {
                    //  we have at least one more shot at it, so try again
                    setCurrentState( StateHandshakeSendIdentify );
                }
                else
                {
                    //  we've run out of attempts, abort the handshake
                    setCurrentState( StateHandshakeAbort );
                    retCode = StateHandshakeAbort;
                }
                break;
            }
        case StateHandshakeDecodeSecurity:
            {
                if( !commReturnValue && Transfer.getInBuffer( )[0] == ACK )
                {
                    // ready to send switch command
                    setCurrentState( StateScanValueSet1 );
                }
                else if( commReturnValue )
                {
                    if( Transfer.doTrace( READTIMEOUT ) )
                    {
                        CtiLockGuard<CtiLogger> dout_guard( dout );
                        dout << RWTime( ) << " " << getName() << " - no response to password" << endl;
                    }

                    //  if the meter doesn't respond, wait for 2 seconds before trying again
                    //    (2 seconds is the meter's intercharacter timeout)
                    CTISleep( 2000 );
                }
                else if( getAttemptsRemaining( ) > 0 )
                {
                    setCurrentState( StateHandshakeSendSecurity );
                }
                else
                {
                    //  no ACK from the meter...  abort, we can't verify security code
                    setCurrentState( StateHandshakeAbort );
                    retCode = StateHandshakeAbort;
                }
                break;
            }
        case StateScanDecode1:
            {
                //  soon as we send them the "get ready for this data" command,
                //    follow it right up with the data, no delays yet
                if( Transfer.getInBuffer( )[0] == ACK )
                {
                    setAttemptsRemaining( getRetryAttempts( ) );
                    setPreviousState( StateScanDecode1 );
                    setCurrentState( StateScanValueSet2FirstScan );
                }
                else if( getAttemptsRemaining( ) > 0 )
                {
                    //  no ACK, resend
                    setPreviousState( StateScanDecode1 );
                    setCurrentState( StateScanValueSet1 );
                }
                else
                {
                    //  no response to D command, abort
                    setCurrentState( StateScanAbort );
                    retCode = StateScanAbort;
                }

                break;
            }

        case StateScanDecode2:
            {
                //  get our ACK, or try again
                if( (Transfer.getInCountActual( )) == 1 && Transfer.getInBuffer( )[0] == ACK )
                {
                    //  once we get the ACK, we wait 3 seconds for the other meter to pick up and stabilize
                    CTISleep( 3000L );

                    setAttemptsRemaining( getRetryAttempts( ) );
                    setPreviousState( StateScanDecode2 );
                    setCurrentState( StateScanValueSet3FirstScan );
                }
                else
                {
                    setPreviousState( StateScanDecode2 );
                    setCurrentState( StateScanValueSet1 );
                }
                break;
            }

        case StateScanDecode3:
            {
                //  flush all noise/garbage bytes from the stream until we get only an ACK
                if( !(((Transfer.getInCountActual( )) == 1) && (Transfer.getInBuffer( )[0] == ACK)) )
                {
                    if( getAttemptsRemaining( ) > 0)
                    {
                        setPreviousState( StateScanDecode3 );
                        setCurrentState( StateScanValueSet3 );
                    }
                    else
                    {
                        //  it won't shut up, so we are aborting
                        setCurrentState( StateScanAbort );
                        retCode = StateScanAbort;
                    }
                }
                else
                {
                    //  we can now get on with business
                    setPreviousState( StateScanDecode3 );
                    setCurrentState( StateScanValueSet4FirstScan );

                    //  can afford 2 read delays to get the 10 ENQ/ACK combo's
                    setAttemptsRemaining( 2 );

                    //  using this to track number of ACKs received
                    setTotalByteCount( 0 );
                }
                break;
            }
        case StateScanDecode4:
            {
                //  waiting for 10 ENQ/ACK pairs communications in a row to continue
                if( Transfer.getInBuffer( )[0] == ACK )
                {
                    setAttemptsRemaining( 2 );
                    setTotalByteCount( getTotalByteCount( ) + 1 );

                    //  I need 10 ACKS in a row to assume the selected meter is ready to fly
                    if( getTotalByteCount( ) < 10 )
                    {
                        setCurrentState( StateScanValueSet4 );
                    }
                    else
                    {
                        // we good to go
                        setCurrentState( StateHandshakeSendIdentify );
                        setTotalByteCount( 0 );
                    }
                }
                else
                {
                    if( getAttemptsRemaining( ) > 0 )
                    {
                        setCurrentState (StateScanValueSet4);
                    }
                    else
                    {
                        //  timed out;  too many read delays and/or didn't read enough ACKs
                        setCurrentState( StateScanAbort );
                        setTotalByteCount( 0 );
                        retCode = StateScanAbort;
                    }
                }
                break;
            }

        default:
            {
                CtiLockGuard<CtiLogger> dout_guard( dout );
                dout << RWTime( ) << " (" << __LINE__ << ") " << getName() << " - decoding select meter - Invalid state " << getCurrentState( ) << endl;
            }
            setCurrentState( StateScanAbort );
            retCode = StateScanAbort;
    }

    return retCode;
}


INT CtiDeviceQuantum::decodeResponseScan( CtiXfer &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList )
{
    int retCode = NORMAL;

    //  run the return through the wringer
    retCode = checkReturnMsg( Transfer, commReturnValue );

    //  if we have no problems
    if( !retCode )
    {
        //  check for valid state
        switch( getCurrentState( ) )
        {
        case StateScanDecode1:
            {
                do
                {
                    //  copy data into the buffer
                    memcpy( &_dataBuffer[getTotalByteCount( )],
                            &Transfer.getInBuffer( )[quantumScanCommands[getCommandPacket( )].startOffset + 1], //  +1 to move past the ACK
                            quantumScanCommands[getCommandPacket( )].bytesToRead );

                    setTotalByteCount( getTotalByteCount( ) + quantumScanCommands[getCommandPacket( )].bytesToRead );

                    //  increment our command packet
                    setCommandPacket( getCommandPacket( ) + 1 );

                    //  loop through while in same block AND not at end of list
                } while( (quantumScanCommands[getCommandPacket( )].startAddress == quantumScanCommands[getCommandPacket( ) - 1].startAddress) &&
                          quantumScanCommands[getCommandPacket( )].name != NULL );

                //  if null, it's the last entry in the command list - we're done
                if( quantumScanCommands[getCommandPacket( )].name == NULL )
                {
                    setCurrentState( StateScanComplete );
                    //  next, grab the load profile data
                    setCurrentCommand( CmdLoadProfileTransition );
                }
                else
                {
                    setCurrentState( StateScanValueSet1FirstScan );
                }
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> dout_guard( dout );
                    dout << RWTime( ) << " (" << __LINE__ << ") " << getName() << " - decoding scan - Invalid state " << getCurrentState( ) << endl;
                }
                setCurrentState( StateScanAbort );
                retCode = StateScanAbort;
                break;
            }
        }
    }
    else if( retCode == SCHLUM_RESEND_CMD )
    {
        //  we don't want to abort, just try again
        retCode = NORMAL;
    }

    return retCode;
}


INT CtiDeviceQuantum::decodeResponseLoadProfile (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    QuantumConfigData_t  *mmCfg = ((QuantumConfigData_t *)_massMemoryConfig);

    int                   retCode = NORMAL;
    INT                   lastCommandStartAddress;
    INT                   recordSize;
    ULONG                 mmRealTime;
    INT                   timestampYear,
                          timestampMonth,
                          timestampDay,
                          timestampHour,
                          hoursSinceBeginningOfYear;
    RWDate                tmpDate;

    //  run the return through the wringer
    //    this will set the abort state if need be (no retry attempts left)
    retCode = checkReturnMsg( Transfer, commReturnValue );

    if( !retCode )
    {
        switch( getCurrentState( ) )
        {
            case StateScanDecode1:
                {
                    do
                    {
                        //  NOTE that this is overwriting the scan data buffer - but that's okay, because it's the same data -
                        //    we're basically just updating the scan data.
                        //    we read it again because we may just grab LP data alone someday
                        //  copy data into the buffer
                        memcpy( &_dataBuffer[getTotalByteCount( )],
                                &Transfer.getInBuffer( )[quantumLPCommands[getCommandPacket( )].startOffset + 1], //  +1 to move past the ACK
                                quantumLPCommands[getCommandPacket( )].bytesToRead );

                        setTotalByteCount( getTotalByteCount( ) + quantumLPCommands[getCommandPacket( )].bytesToRead );

                        //  increment our command packet
                        setCommandPacket( getCommandPacket( ) + 1 );

                        //  loop through while in same block or at end of command list
                    } while( (quantumLPCommands[getCommandPacket( )].startAddress == quantumLPCommands[getCommandPacket( ) - 1].startAddress) &&
                              quantumLPCommands[getCommandPacket( )].name != NULL );

                    //  if null, it's the last entry in the command list - we're done here
                    if( quantumLPCommands[getCommandPacket( )].name == NULL )
                    {
                        translateQuantumConfigData( (QuantumRawConfigData_t *)_dataBuffer, (QuantumConfigData_t *)_massMemoryConfig );

                        //  create a date with the meter's info
                        tmpDate = RWDate( mmCfg->realTime.dayOfMonth, mmCfg->realTime.month, mmCfg->realTime.year + 2000 );

                        if( RWTime( tmpDate ) > (RWTime( ) + (24 * 60 * 60)) ||
                            RWTime( tmpDate ) < (RWTime( ) - (24 * 60 * 60)) )
                        {
                            {
                                CtiLockGuard<CtiLogger> dout_guard( dout );
                                dout << RWTime( ) << " (" << __LINE__ << ") " << getName() << " - decoding load profile - invalid meter time (" << RWTime( tmpDate ) << "), aborting LP scan" <<  endl;
                            }
                            setCurrentState( StateScanAbort );
                        }
                        else
                        {
                            if( shouldRetrieveLoadProfile( *((unsigned long *)_loadProfileTimeDate), ((QuantumConfigData_t *)_massMemoryConfig)->intervalLength ) )
                            {
                                setCurrentState( StateScanValueSet2FirstScan );
                            }
                            else
                            {
                                if( getDebugLevel( ) & DEBUGLEVEL_LUDICROUS )
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " Skipping load profile for " << getName( ) << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }

                                setCurrentState( StateScanComplete );
                            }
                        }
                    }
                    else
                    {
                        setCurrentState( StateScanValueSet1 );
                    }
                    break;
                }
            case StateScanDecode2:
                {
                    //  decode the timestamp for the previous record so we can calculate the current record's time

                    timestampMonth = (INT)*(Transfer.getInBuffer( ) + 1);  //  start out past the ACK
                    timestampDay   = (INT)*(Transfer.getInBuffer( ) + 2);
                    timestampHour  = (INT)*(Transfer.getInBuffer( ) + 3);
                    timestampYear  = mmCfg->realTime.year;

                    //  if this record started last year
                    if( timestampMonth == 12 && mmCfg->realTime.month == 1 )
                        timestampYear--;

                    //  start out at the last record's last interval time, and then...
                    _currentRecordTime = RWTime( RWDate( timestampDay,
                                                         timestampMonth,
                                                         timestampYear + 2000 ),
                                                 timestampHour ).seconds( );

                    //  ... add on one interval length
                    _currentRecordTime += mmCfg->intervalLength * 60;

                    if( DebugLevel & 0x0001 )
                    {
                        CtiLockGuard<CtiLogger> dout_guard( dout );
                        dout << RWTime( ) << " (" << __LINE__ << ") " << getName() << " - current record time: " << RWTime( _currentRecordTime ) << " = " << _currentRecordTime << endl;
                    }

                    mmRealTime = RWTime( RWDate( (INT)mmCfg->realTime.dayOfMonth,
                                                 (INT)mmCfg->realTime.month,
                                                 (INT)mmCfg->realTime.year + 2000 ),
                                         (INT)mmCfg->realTime.hour,
                                         (INT)mmCfg->realTime.minute ).seconds( );

                    if( DebugLevel & 0x0001 )
                    {
                        CtiLockGuard<CtiLogger> dout_guard( dout );
                        dout << RWTime( ) << " (" << __LINE__ << ") " << getName() << " - mmrealtime: " << RWTime( mmRealTime ) << " = " << mmRealTime << endl;
                    }

                    //  if the calculated start time for this record is more than one record
                    //    length (plus meter fudge factor) in the past, or it's in the
                    //    future, it's not valid
                    if( (mmRealTime - _currentRecordTime) > (mmCfg->intervalLength * (mmCfg->numIntervalsInRecord + 2) * 60) ||
                        (mmRealTime - _currentRecordTime) < 0 )
                    {
                        //  we don't know what time the current record started...  so we have to give up
                        //    this record will be completed eventually, and the scan will succeed then
                        setCurrentState( StateScanAbort );
                    }
                    else
                    {
                        //  otherwise start getting records
                        setCurrentState( StateScanValueSet3FirstScan );
                    }

                    break;
                }
            case StateScanDecode3:
                {
                    recordSize = 11 + ((6 + 90) * mmCfg->numChannels);

                    memcpy( &_loadProfileBuffer[getTotalByteCount( )],
                            Transfer.getInBuffer( ) + 1,                       //  offset past the ACK
                            (INT)((Transfer.getInCountActual( )) - 1 - 2 ) ); //  don't copy the CRC

                    setTotalByteCount( getTotalByteCount( ) + (Transfer.getInCountActual( )) - 1 - 2 );  //  don't count either of the above

                    //  check if we've gotten the whole record
                    if( getTotalByteCount( ) >= recordSize )
                    {
                        //  check to see if this is the current record
                        if( _currentRecordLocation == mmCfg->currentRecord )
                        {
                            //  if so, we've read all we came here for, so we send the LP data...
                            setCurrentState( StateScanReturnLoadProfile );
                            //  ...  and indicate that we've completed
                            setPreviousState( StateScanComplete );
                        }
                        else
                        {
                            //  if not, we set up to send this one off, ...
                            setCurrentState( StateScanReturnLoadProfile );
                            //  ...  after which we come back for more
                            setPreviousState( StateScanValueSet3 );
                        }
                    }
                    else
                    {
                        //  we still need to get more, so we jump back
                        setCurrentState( StateScanValueSet3 );
                    }

                    break;
                }
            default:
                {
                    CtiLockGuard<CtiLogger> dout_guard( dout );
                    dout << RWTime( ) << " (" << __LINE__ << ") " << getName() << " - decoding load profile - Invalid state " << getCurrentState( ) << endl;
                }
                setCurrentState( StateScanAbort );
                retCode = StateScanAbort;
        }
    }
    else if( retCode == SCHLUM_RESEND_CMD )
    {
        //  we don't want to abort, just try again
        retCode = NORMAL;
    }

    return retCode;
}


LONG CtiDeviceQuantum::bytesToLong( BYTE *toConvert, INT numBytes )
{
    INT i;
    LONG result;

    result = 0;

    if( numBytes <= 4 )
    {
        for( i = 0; i < numBytes; i++ )
        {
            result *= 256;
            result += LONG( toConvert[i] );
        }
    }

    return result;
}


FLOAT CtiDeviceQuantum::bytesToFloat( BYTE *toConvert, INT numBytes )
{
    INT i;
    DOUBLE temp;
    FLOAT result;

    temp = 0.0;

    for( i = 0;  i < numBytes; i++ )
    {
        temp *= 256.0;
        temp += FLOAT( toConvert[i] );
    }

    result = FLOAT( temp );

    return result;
}


FLOAT CtiDeviceQuantum::bcdToFloat( BYTE *toConvert, INT numBytes )
{
    INT i;
    DOUBLE temp;
    FLOAT result;

    temp = 0.0;

    for( i = 0; i < numBytes; i++  )
    {
        temp *= 10.0;
        temp += FLOAT( (toConvert[i] & 0xF0) / 16 );
        temp *= 10.0;
        temp += FLOAT( (toConvert[i] & 0x0F) );
    }

    result = FLOAT( temp );

    return result;
}


FLOAT CtiDeviceQuantum::registerToFloat( BYTE *rawReg, QuantumConfigData_t *translated, INT programmedRegNum )
{
    FLOAT result;
    INT   regNum, revision;
    enum
    {
        NotInstantaneous,
        VoltsSquared,
        PowerFactor,
        Other
    } instType;

    instType = NotInstantaneous;

    result = 0.0;
    regNum = translated->programTable[programmedRegNum].regNum;
    revision = translated->softwareRevision;

    //  if it's an instantaneous register
    if( (regNum >= 16 && regNum <= 21) ||
        (regNum >= 71 && regNum <= 84) ||
        (regNum == 91) ||
        (regNum == 92) )
    {
        result = bytesToFloat( rawReg, 6 );
        if( regNum == 19 || regNum == 80)
            instType = PowerFactor;
        else if( regNum == 20 || regNum == 81 )
            instType = VoltsSquared;
        else
            instType = Other;
    }
    else
    {
        result = bcdToFloat( rawReg, 6 );
    }

    switch( instType )
    {
        case NotInstantaneous:
            {
                if( revision <= 13 )
                    result /= 1000.0;
                else
                    result /= 10000.0;
                break;
            }
        case VoltsSquared:
            {
                if( revision <= 9 )
                    result /= (256.0 * 256.0 * 256.0);
                else
                    result /= (256.0);
                break;
            }
        case PowerFactor:
            {
                result /= (256.0 * 256.0 * 256.0 * 256.0);
                break;
            }
        case Other:
            {
                if( revision <= 9 )
                    result /= (256.0 * 256.0 * 256.0 * 256.0);
                else
                    result /= (256.0 * 256.0);
                break;
            }
    }

    return result;
}


void CtiDeviceQuantum::translateQuantumConfigData( QuantumRawConfigData_t *rawConfig, QuantumConfigData_t *translated )
{
    if( translated != NULL )
    {
        translated->softwareRevision           = INT( rawConfig->softwareRevision );
        translated->currentRecord              = bytesToLong( rawConfig->currentRecord, 3 );
        translated->currentInterval            = INT( rawConfig->currentInterval );
        translated->realTime.dayOfWeek         = INT( rawConfig->realTime[0] );
        translated->realTime.year              = INT( rawConfig->realTime[1] );
        translated->realTime.month             = INT( rawConfig->realTime[2] );
        translated->realTime.dayOfMonth        = INT( rawConfig->realTime[3] );
        translated->realTime.hour              = INT( rawConfig->realTime[4] );
        translated->realTime.minute            = INT( rawConfig->realTime[5] );
        translated->realTime.second            = INT( rawConfig->realTime[6] );

        translated->numChannels                = INT( rawConfig->numChannels );
        translated->mmBitSize                  = INT( rawConfig->mmBitSize );
        translated->numIntervalsInRecord       = INT( rawConfig->numIntervalsInRecord );
        translated->intervalLength             = INT( rawConfig->intervalLength );
        translated->mmStart                    = bytesToLong( rawConfig->mmStart, 3 );
        translated->mmEnd                      = bytesToLong( rawConfig->mmEnd, 3 );

//  we don't care if we're in DST - we only care what time the meter says it is
/*        translated->toDST.month                = INT( rawConfig->toDST[0] );
        translated->toDST.day                  = INT( rawConfig->toDST[1] );
        translated->toDST.hour                 = INT( rawConfig->toDST[2] );

        translated->fromDST.month              = INT( rawConfig->fromDST[0] );
        translated->fromDST.day                = INT( rawConfig->fromDST[1] );
        translated->fromDST.hour               = INT( rawConfig->fromDST[2] );
 */
        translated->demandIntervalLength       = INT( rawConfig->demandIntervalSetup[0] & 127 );
        translated->demandIntervalSubintervals = INT( rawConfig->demandIntervalSetup[1] );

        translated->regMultInstW               = bytesToFloat( rawConfig->regMultInstW, 4 ) / 256.0;
        translated->regMultInstV               = bytesToFloat( rawConfig->regMultInstV, 4 ) / 256.0;
        translated->regMultInstA               = bytesToFloat( rawConfig->regMultInstA, 4 ) / 256.0;

        translated->kt                         = FLOAT( bytesToLong( rawConfig->kt, 2 ) ) * 0.025;

        translated->regMultAmpDemand           = bcdToFloat( rawConfig->regMultAmpDemand, 5 );
        translated->regMultDemand              = bcdToFloat( &(rawConfig->regMultipliers[0]),  5 );
        translated->regMultVSq                 = bcdToFloat( &(rawConfig->regMultipliers[5]),  5 );
        translated->regMultASq                 = bcdToFloat( &(rawConfig->regMultipliers[10]), 5 );
        translated->regMultEnergy              = bcdToFloat( &(rawConfig->regMultipliers[15]), 5 );

        if( DebugLevel & 0x0001 )
        {
            CtiLockGuard<CtiLogger> dout_guard( dout );
            dout << RWTime( ) << " " << getName() << " - Begin scan data block ---" << endl;
            dout << RWTime( ) << " software revision: " << translated->softwareRevision << endl;
            dout << RWTime( ) << " regMultInstW:      " << translated->regMultInstW << endl;
            dout << RWTime( ) << " regMultInstV:      " << translated->regMultInstV << endl;
            dout << RWTime( ) << " regMultInstA:      " << translated->regMultInstA << endl;
            dout << RWTime( ) << " kt:                " << translated->kt << endl;
            dout << RWTime( ) << " regMultAmpDemand:  " << translated->regMultAmpDemand << endl;
            dout << RWTime( ) << " regMultDemand:     " << translated->regMultDemand << endl;
            dout << RWTime( ) << " regMultVSq:        " << translated->regMultVSq << endl;
            dout << RWTime( ) << " regMultASq:        " << translated->regMultASq << endl;
            dout << RWTime( ) << " regMultEnergy:     " << translated->regMultEnergy << endl;
            dout << RWTime( ) << " time:              " << translated->realTime.year << " " <<
                                                           translated->realTime.month << " " <<
                                                           translated->realTime.dayOfMonth << ", " <<
                                                           translated->realTime.hour << ":" <<
                                                           translated->realTime.minute << ":" <<
                                                           translated->realTime.second << endl;
/*            dout << RWTime( ) << " to DST:            " << translated->toDST.month << "/"
                                                        << translated->toDST.day << " "
                                                        << translated->toDST.hour << ":00 " <<  endl;
            dout << RWTime( ) << " from DST:          " << translated->fromDST.month << "/"
                                                        << translated->fromDST.day << " "
                                                        << translated->fromDST.hour << ":00 " <<  endl;  */
            dout << RWTime( ) << " mmStart:           " << translated->mmStart << endl;
            dout << RWTime( ) << " mmEnd:             " << translated->mmEnd << endl;
            dout << RWTime( ) << " currentRecord:     " << translated->currentRecord << endl;
            dout << RWTime( ) << " currentInterval:   " << translated->currentInterval << endl;
            dout << RWTime( ) << " numChannels:       " << translated->numChannels << endl;
            dout << RWTime( ) << " " << getName() << " - End scan data block ---" << endl;
        }

        for( int i = 0; i < 32; i++ )
        {
            if( i < 16 )
            {
                translated->channelProgram[i].regNum        = rawConfig->channelProgram[i*3];
                //  convert the two bytes into an int
                translated->channelProgram[i].channelWeight = INT( bytesToLong( &(rawConfig->channelProgram[i*3+1]), 2 ) );

                translated->mmPhaseTable[i]       = INT( rawConfig->mmPhaseTable[i] & 0x03 );
            }

            translated->programTable[i].regNum = -1;
            translated->programTable[i].nonRegNum = -1;

            //  if it's a non-register (bit 6 set)
            if( rawConfig->programTable[i*4] & 0x40 )
            {
                translated->programTable[i].nonRegNum = INT( rawConfig->programTable[i*4+1] );
            }
            else
            {
                translated->programTable[i].regNum    = INT( rawConfig->programTable[i*4+1] );
            }

            translated->programTable[i].phase     = INT( rawConfig->programTable[i*4] ) & 0x0F;
            //  i'm lumping "aggregate" with the "no phase" setting - makes for easier coding later
            if( translated->programTable[i].phase == 8 )
                translated->programTable[i].phase = 0;

            //  if the upper nibble is set to 0xEx, it's a pulse output, and we don't touch those,
            //    so we set the phase to an impossible value - this way, it'll never match the table
            if( (rawConfig->programTable[i*4+3] & 0xE0) == 0xE0 )
            {
                translated->programTable[i].phase = -1;
            }
        }
    }
}


void CtiDeviceQuantum::translateQuantumProgrammedRegisters( QuantumRawScanData_t *rawScan, QuantumConfigData_t *translated, FLOAT *programmedRegisters )
{
    int i, j;
    for( i = 0, j = 0; i < 32; i++ )
    {
        //  note that we're only incrementing the register offset if it's NOT a pulse output
        //    pulse outputs apparently don't get register data
        if( translated->programTable[i].phase >= 0 )
        {
            programmedRegisters[i] = registerToFloat( &(rawScan->programmedRegisters[j*6]), translated, i );

            if( getDebugLevel( ) & DEBUGLEVEL_LUDICROUS )
            {
                CtiLockGuard<CtiLogger> dout_guard( dout );
                dout << RWTime( ) << " " << getName() << " -  register " << i << "(" << translated->programTable[i].regNum << "," <<
                                                                 translated->programTable[i].nonRegNum << "," <<
                                                                 translated->mmPhaseTable[i] << ") *" <<
                                                                 translated->programTable[i].displayMultiplier << ": " <<
                                                                 programmedRegisters[i] << endl;
            }
            j++;
        }
    }
}


INT CtiDeviceQuantum::decodeResultScan( INMESS *InMessage,
                                        RWTime &TimeNow,
                                        RWTPtrSlist< CtiMessage >   &vgList,
                                        RWTPtrSlist< CtiMessage > &retList,
                                        RWTPtrSlist< OUTMESS > &outList )
{
    CHAR     temp[100],
             buffer[60];

    char tmpCurrentState = InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1];

    /* Misc. definitions */
    ULONG    i,
             j,
             tmpRegNum;
    ULONG    Pointer;

    /* Variables for decoding Messages */
    SHORT    Value;
    USHORT   UValue;
    FLOAT    PartHour;
    DOUBLE   PValue;

    DIALUPREQUEST   *DupReq = &InMessage->Buffer.DUPSt.DUPRep.ReqSt;
    DIALUPREPLY     *DUPRep = &InMessage->Buffer.DUPSt.DUPRep;

    CtiPointDataMsg *pData         = NULL;
    CtiPointNumeric *pNumericPoint = NULL;

    CtiReturnMsg    *pPIL          = CTIDBG_new CtiReturnMsg( getID( ),
                                                       RWCString( InMessage->Return.CommandStr ),
                                                       RWCString( ),
                                                       InMessage->EventCode & 0x7fff,
                                                       InMessage->Return.RouteID,
                                                       InMessage->Return.MacroOffset,
                                                       InMessage->Return.Attempt,
                                                       InMessage->Return.TrxID,
                                                       InMessage->Return.UserID );

    QuantumRawScanData_t *rawScanData = (QuantumRawScanData_t *)DUPRep->Message;
    QuantumScanData_t    *processedScanData;
    RWTime peakTime;

    processedScanData = CTIDBG_new QuantumScanData_t;


    if( processedScanData == NULL )
    {
        CtiLockGuard<CtiLogger> dout_guard( dout );
        dout << RWTime( ) << " (" << __LINE__ << ") " << getName() << " - result decode scan - Unable to allocate memory to translate scan data" << endl;
    }
    else
    {
        translateQuantumConfigData( &(rawScanData->configData), &(processedScanData->configData) );
        translateQuantumProgrammedRegisters( rawScanData, &(processedScanData->configData), processedScanData->programmedRegisters );

        //  here's where we filter through the returned data.

        if( !useScanFlags( ) || isScanPending( ) )
        {
            //  if we bombed, we need an error condition and to plug values
            if( (tmpCurrentState == StateScanAbort)      ||
                (tmpCurrentState == StateHandshakeAbort) ||
                (InMessage->EventCode != 0) )
            {
                CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg( CtiCommandMsg::UpdateFailed );

                if( pMsg != NULL )
                {
                    pMsg->insert( -1 );                 // This is the dispatch token and is unimplemented at this time
                    pMsg->insert( OP_DEVICEID );        // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                    pMsg->insert( getID( ) );     // The id (device or point which failed)
                    pMsg->insert( ScanRateGeneral );    // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

                    if( InMessage->EventCode != 0 )
                    {
                        pMsg->insert( InMessage->EventCode );
                    }
                    else
                    {
                        pMsg->insert( GeneralScanAborted );
                    }
                }

                // through this into the return list
                insertPointIntoReturnMsg( pMsg, pPIL );
            }
            else
            {
                //  loop through the registers
                for( i = 0; processedScanData->configData.programTable[i].regNum > 0 ||
                            processedScanData->configData.programTable[i].nonRegNum > 0; i++ )
                {
                    //  there are no non-register fields we want
                    tmpRegNum = processedScanData->configData.programTable[i].regNum;
                    if( tmpRegNum > 0 )
                    {
                        //  loop through the Quantum->CTI register mapping table
                        for( j = 0; regMap[j].regNum > 0; j++ )
                        {
                                //  if it's the right register
                            if( (regMap[j].regNum == tmpRegNum) &&
                                //  and isn't phase-specific
                                (regMap[j].phase == processedScanData->configData.programTable[i].phase) &&
                                //  and it's not a load profile point
                                (regMap[j].loadProfile == 0) )
                            {
                                //  then check to see if we know about it
                                pNumericPoint = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( regMap[j].CTIOffset, AnalogPointType );
                                peakTime = RWTime( RWDate( processedScanData->configData.realTime.dayOfMonth,
                                                           processedScanData->configData.realTime.month,
                                                           processedScanData->configData.realTime.year + 2000 ),
                                                   processedScanData->configData.realTime.hour,
                                                   processedScanData->configData.realTime.minute,
                                                   0 );

                                if( pNumericPoint != NULL )
                                {
                                    double Value;
                                    RWCString resultString;

                                    Value = ((CtiPointNumeric*)pNumericPoint)->computeValueForUOM((DOUBLE)processedScanData->programmedRegisters[i]);

                                    resultString = getName() + " / " + pNumericPoint->getName() + " = " + CtiNumStr((int)Value);

                                    verifyAndAddPointToReturnMsg( pNumericPoint->getPointID( ),
                                                                  Value,
                                                                  NormalQuality,
                                                                  peakTime,
                                                                  pPIL,
                                                                  0,
                                                                  resultString );
                                }
                            }
                        }
                    }
                }
            }
        }

        delete processedScanData;
    }
    // reset this flag so device makes it on the queue later
    resetScanPending( );

    //  this is where the messages get sent to Dispatch
    if( pPIL != NULL )
    {
        if( pPIL->PointData( ).entries( ) > 0 )
        {
            retList.insert( pPIL );
        }
        else
        {
            delete pPIL;
        }
    }
    // set this to null
    pPIL = NULL;

//  ResultDisplay(InMessage);

    return NORMAL;
}



INT CtiDeviceQuantum::decodeResultLoadProfile (INMESS *InMessage,
                                               RWTime &TimeNow,
                                               RWTPtrSlist< CtiMessage >   &vgList,
                                               RWTPtrSlist< CtiMessage > &retList,
                                               RWTPtrSlist< OUTMESS > &outList)
{
    DIALUPREQUEST                 *dupReq = &InMessage->Buffer.DUPSt.DUPRep.ReqSt;
    DIALUPREPLY                   *dupRep = &InMessage->Buffer.DUPSt.DUPRep;

    QuantumConfigData_t         lpCfg;
    //  these pointers clean up a bunch of silly casts
    QuantumRawConfigData_t      *rawConfig;
    QuantumLoadProfileMessage_t *lpMess;
    BYTE                        *mmBuffer;
    INT                         dataOffset;
    INT                         yukonPoints[16];
    LONG                        recordNibblePos,
                                recordBytePos,
                                channelNum,
                                intervalNum,
                                nibblesRead,
                                nibblePos,
                                recordNibbleSize;
    INT                         tmpPulseData[16];
    ULONG                       lpTimestamp;
    CtiPointNumeric             **pNumericPoint;
    RWTime                      peakTime;
    CtiReturnMsg                *pPIL = CTIDBG_new CtiReturnMsg( getID( ),
                                                          RWCString( InMessage->Return.CommandStr ),
                                                          RWCString( ),
                                                          InMessage->EventCode & 0x7fff,
                                                          InMessage->Return.RouteID,
                                                          InMessage->Return.MacroOffset,
                                                          InMessage->Return.Attempt,
                                                          InMessage->Return.TrxID,
                                                          InMessage->Return.UserID );
    int                         i, j;


    lpMess = (QuantumLoadProfileMessage_t *)(dupRep->Message);

    rawConfig = &(lpMess->configData);

    translateQuantumConfigData( rawConfig, &lpCfg );

    lpTimestamp = lpMess->timestamp;
    mmBuffer = lpMess->MMBuffer;


    //  allocate an array of pointers for the points
    pNumericPoint = CTIDBG_new CtiPointNumeric *[lpCfg.numChannels];

    for( i = 0; i < lpCfg.numChannels; i++ )
    {
        pNumericPoint[i] = NULL;
        //  loop through the Quantum->CTI register mapping table
        for( j = 0; regMap[j].regNum > 0; j++ )
        {
                //  if it's the right register
            if( (regMap[j].regNum == lpCfg.channelProgram[i].regNum) &&
                //  and isn't phase-specific
                (regMap[j].phase == lpCfg.mmPhaseTable[i]) &&
                //  and it is a load profile point
                (regMap[j].loadProfile == 1) )
            {
                //  then check to see if we know about it
                pNumericPoint[i] = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( regMap[j].CTIOffset, AnalogPointType );
            }
        }
    }


    //  find out how big the interval is in bits...
    recordNibbleSize = lpCfg.numIntervalsInRecord * lpCfg.mmBitSize * lpCfg.numChannels;
    //  ... to find out how big it is in nibbles
    recordNibbleSize /= 4;

    //  move past the time, status field, and register snapshots
    dataOffset = 3 + 8 + lpCfg.numChannels * 6;

    if( DebugLevel & 0x0001 )
    {
        CtiLockGuard<CtiLogger> dout_guard( dout );
        dout << RWTime( ) << " " << getName() << " - current record address:  " << lpCfg.currentRecord << ", this record: " << lpMess->recordAddress << endl;
    }


    //  Note:  this code is hard-coded for 12 bits.  no allowances for 8 or 16.  quantums are all 12 anyway, afaik.
    //    this is because Schlumberger meters are kinda screwy when it comes to ordering nibbles.
    //    maybe it makes sense in the hardware, but it sure makes for interesting code.

    //  multiply offset by 2 to get size in nibbles
    for( recordNibblePos = 0, intervalNum = 1;
         !((lpCfg.currentRecord == lpMess->recordAddress)  //  send LP data until we get to the current interval
             && (intervalNum > lpCfg.currentInterval))    //    of the current record
         && (recordNibblePos < recordNibbleSize);          //  or we're at the end of the interval
         intervalNum++ )
    {
        for( channelNum = 0; channelNum < lpCfg.numChannels; channelNum++ )
        {
            tmpPulseData[channelNum] = 0;

            for( nibblesRead = 0; nibblesRead < 3; )
            {
                //  find out what byte we're in (nibbles / 2)
                recordBytePos = recordNibblePos + nibblesRead + (dataOffset * 2);
                recordBytePos >>= 1;

                //  if we've read the first byte (XX RR *UX XX) OR if we're at an odd nibble (XX X*U UU XX)
                //    (i.e., if we have part of a byte to read, it's the high-order nibble)
                //    legend:  *:before current nibble  R:read  U:unread  X:part of another pulse
                if( (nibblesRead & 2) || ((recordNibblePos + nibblesRead) & 1) )
                {
                    if( nibblesRead )
                        tmpPulseData[channelNum] |= (mmBuffer[recordBytePos] & 0xF0) << 4;
                    else
                        tmpPulseData[channelNum] |= (mmBuffer[recordBytePos] & 0x0F) << 8;
                    nibblesRead++;
                }
                //  otherwise, we're just reading a whole byte
                else
                {
                    tmpPulseData[channelNum] |= mmBuffer[recordBytePos];
                    nibblesRead += 2;
                }
            }

            //  outside of the loop

            //  ???  mskf 2001-aug-01: KCPL deals with raw pulses
            //                           then again, this "channel weight" may just be a setup parameter
//            tmpPulseData[channelNum] *= lpCfg.channelProgram[channelNum].channelWeight;

            //  will be incremented by 3 each time through the loop
            recordNibblePos += nibblesRead;
        }

        if( lpTimestamp >= getLastLPTime( ) )
        {
            for( i = 0; i < lpCfg.numChannels; i++ )
            {
                peakTime = RWTime( lpTimestamp );

                if( pNumericPoint[i] != NULL )
                {
                    double Value;

                    Value = ((CtiPointNumeric*)pNumericPoint[i])->computeValueForUOM((DOUBLE)tmpPulseData[i] * (DOUBLE)(60 / lpCfg.intervalLength));

                    verifyAndAddPointToReturnMsg( pNumericPoint[i]->getPointID( ),
                                                  Value,
                                                  NormalQuality,
                                                  peakTime,
                                                  pPIL,
                                                  TAG_POINT_LOAD_PROFILE_DATA );
                }

                if( DebugLevel & 0x0001 )
                {
                    CtiLockGuard<CtiLogger> dout_guard( dout );
                    //  mskf 2001-aug-01:  commented out display of channel multiplier
//                    dout << getName() << " " << peakTime.asString( ) << " channel " << i << ":  ("
//                                                                      << lpCfg.channelProgram[i].regNum << "[" << lpCfg.mmPhaseTable[i] << ",*"
//                                                                      << lpCfg.channelProgram[i].channelWeight << "],"
//                                                                      << tmpPulseData[i] << ")" << endl;
                    dout << getName() << " " << peakTime.asString( ) << " channel " << i << ":  ("
                                                                      << lpCfg.channelProgram[i].regNum << "[" << lpCfg.mmPhaseTable[i] << "],"
                                                                      << tmpPulseData[i] << ")" << endl;
                }
            }
        }

        lpTimestamp += lpCfg.intervalLength * 60;
    }

    setLastLPTime( RWTime(lpTimestamp) );


    delete [] pNumericPoint;

    //  send the whole mess to dispatch
    if( pPIL->PointData( ).entries( ) > 0 )
    {
        retList.insert( pPIL );
    }
    else
    {
        delete pPIL;
    }

    //  set this to null no matter what
    pPIL = NULL;

    return NORMAL;
}


INT CtiDeviceQuantum::allocateDataBins( OUTMESS *outMess )
{
    //  _dataBuffer holds other things as well, but memory records are the biggest
    if( _dataBuffer == NULL )
        _dataBuffer = CTIDBG_new BYTE[sizeof(QuantumScanData_t)];

    if( _loadProfileTimeDate == NULL )
    {
        _loadProfileTimeDate = CTIDBG_new BYTE[sizeof(ULONG)];

        if( _loadProfileTimeDate != NULL )
        {
            *((ULONG *)_loadProfileTimeDate) = (ULONG)(outMess->Buffer.DUPReq.LP_Time);

            if( DebugLevel & 0x0001 )
            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << RWTime( ) << " " << getName() << " - last LPtime is: " << RWTime( *((ULONG *)_loadProfileTimeDate) ) << endl;
            }
        }
    }


    if( _loadProfileBuffer == NULL )
    {
        _loadProfileBuffer = CTIDBG_new BYTE[sizeof(QuantumLoadProfileMessage_t)];
    }

    if( _massMemoryConfig == NULL )
    {
        _massMemoryConfig = CTIDBG_new BYTE[sizeof(QuantumConfigData_t)];
    }

    /* NOTE:  The massMemoryLoadProfile block is initialized in the state machine
              and removed in the free memory block function
     */

    // set the command based in the out message command
    setTotalByteCount( 0 );
    setCurrentCommand( (CtiDeviceIED::CtiMeterCmdStates_t)outMess->Buffer.DUPReq.Command[0] );
    setCurrentState( StateHandshakeInitialize );

    return NORMAL;
}


INT CtiDeviceQuantum::reformatDataBuffer(BYTE *aInMessBuffer, ULONG &aTotalBytes)
{
    //  note - max of 2K in a dialup reply message, but this is only ~400 bytes, so we're okay
    aTotalBytes = sizeof( QuantumRawScanData_t );
    memcpy( aInMessBuffer, _dataBuffer, aTotalBytes );
    return NORMAL;
}


INT CtiDeviceQuantum::copyLoadProfileData(BYTE *aInMessBuffer, ULONG &aTotalBytes)
{
    int mmDataLen;

    QuantumConfigData_t         *mmCfg  = (QuantumConfigData_t *)_massMemoryConfig;
    QuantumLoadProfileMessage_t *lpMess = (QuantumLoadProfileMessage_t *)aInMessBuffer;

    aTotalBytes = sizeof( QuantumLoadProfileMessage_t );

    //  11 status bytes, plus 6 bytes of register and (12bits * 60 =) 90 bytes of pulse data per channel
    mmDataLen = 11 + ((6 + 90) * ((QuantumConfigData_t *)_massMemoryConfig)->numChannels);

    //  copy the config data
    memcpy( &(lpMess->configData), _dataBuffer, sizeof(QuantumRawConfigData_t) );
    //  followed by the timestamp of this record
    memcpy( &(lpMess->timestamp), &_currentRecordTime, sizeof(ULONG) );
    //  followed by the address of this record
    memcpy( &(lpMess->recordAddress), &_currentRecordLocation, sizeof(LONG) );
    //  followed by the LP data
    memcpy( lpMess->MMBuffer, _loadProfileBuffer, mmDataLen );

    setTotalByteCount( 0 );

    //  increment the time/date for the next record
    //    (if there is no next record, it's okay - we won't reference these guys in that case)
    _currentRecordTime    += mmCfg->intervalLength * mmCfg->numIntervalsInRecord * 60;
    //  and increment the address
    _currentRecordLocation = getNextRecordLocation( _currentRecordLocation );

    return NORMAL;
}



