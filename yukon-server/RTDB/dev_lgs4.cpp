#pragma warning( disable : 4786 )
#include <math.h>
#include <rw/rwtime.h>
#include <rw/rwdate.h>

#include "yukon.h"
#include "porter.h"
#include "dev_lgs4.h"

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


void reverseCharacters (UCHAR *source, PCHAR dest);
DOUBLE S4BCDtoDouble(UCHAR* buffer, ULONG len);
ULONG S4BCDtoBase10(UCHAR* buffer, ULONG len);
FLOAT S4Float (UCHAR byteArray[3]);
USHORT S4UShort (UCHAR byteArray[2]);
INT findImpliedDecimal (UCHAR buffer[2]);



// order is important
LGS4CommandArray    LGS4Base[] = {
    {  0x01,          3,                1,          2,       "Time"},
    {  0x02,          4,                0,          4,       "Date"},
    {  0x12,         15,                0,         15,       "Device ID"},
    {  0xC6,         10,                0,         10,       "Previous Interval Demand"},
    {  0x96,          8,                0,          1,       "Power Outages/ Test Entries"},
    {  0x80,         44,                0,          21,       "Voltage/Current"},
    {  0x81,          3,                0,          3,       "TOU Status"},
    {  0x87,          6,                0,          4,       "Serial Number"},
    {  0xA0,         72,                0,         72,       "Rate Bins / Total Energy"},
    {  0xAC,        100,                0,        100,       "Current Season TOU Demand"},
    {  0x1F,          3,                0,          3,       "K Factor"},
    {  0xC0,          4,                0,          3,       "Demand Interval"},
    {  0xC4,          3,                0,          3,       "Measurement Units"},
    {  0x99,         18,                0,          18,      "Third Metric"},
    {  LGS4_HANGUP,   0,                0,          0,       "Logoff"}

};

// order is important
LGS4CommandArray    LGS4LoadProfile[] = {
    {  0x02,          4,                0,          4,       "Date"},
    {  0xC3,          7,                0,          3,       "Load Profile Options"},
    {  0xA6,         15,                0,          15,      "Metric Selection"},
    {  0x1F,          3,                0,          3,       "K Factor"},
    {  0xAF,         25,                0,          24,      "S3 Calibration"},
    {  0x03,        128,                0,          128,     "Load Profile"},
    {  LGS4_HANGUP,   0,                0,          0,       "Logoff"}
};


INT             LandisGyrS4Retries = 7;


ULONG    CtiDeviceLandisGyrS4::getTotalByteCount() const
{
    return iTotalByteCount;
}

CtiDeviceLandisGyrS4& CtiDeviceLandisGyrS4::setTotalByteCount(ULONG c)
{
    iTotalByteCount = c;
    return *this;
}

BOOL    CtiDeviceLandisGyrS4::isLPTimeUpdateFlag() const
{
    return iUpdateLPFlag;
}

CtiDeviceLandisGyrS4& CtiDeviceLandisGyrS4::setLPTimeUpdateFlag(BOOL c)
{
    iUpdateLPFlag = c;
    return *this;
}

ULONG    CtiDeviceLandisGyrS4::getPowerDownTime() const
{
    return iPowerDownTime;
}

CtiDeviceLandisGyrS4& CtiDeviceLandisGyrS4::setPowerDownTime(ULONG c)
{
    iPowerDownTime = c;
    return *this;
}

ULONG    CtiDeviceLandisGyrS4::getPowerUpTime() const
{
    return iPowerUpTime;
}

CtiDeviceLandisGyrS4& CtiDeviceLandisGyrS4::setPowerUpTime(ULONG c)
{
    iPowerUpTime = c;
    return *this;
}

ULONG    CtiDeviceLandisGyrS4::getPowerUpDate() const
{
    return iPowerUpDate;
}

CtiDeviceLandisGyrS4& CtiDeviceLandisGyrS4::setPowerUpDate(ULONG c)
{
    iPowerUpDate = c;
    return *this;
}

ULONG    CtiDeviceLandisGyrS4::getNewDate() const
{
    return iNewDate;
}

CtiDeviceLandisGyrS4& CtiDeviceLandisGyrS4::setNewDate(ULONG c)
{
    iNewDate = c;
    return *this;
}
ULONG    CtiDeviceLandisGyrS4::getOldTime() const
{
    return iOldTime;
}

CtiDeviceLandisGyrS4& CtiDeviceLandisGyrS4::setOldTime(ULONG c)
{
    iOldTime = c;
    return *this;
}


INT CtiDeviceLandisGyrS4::getRetryAttempts () const
{
    return iRetryAttempts;
}

CtiDeviceLandisGyrS4& CtiDeviceLandisGyrS4::setRetryAttempts (INT aRetry)
{
    iRetryAttempts = aRetry;
    return *this;
}


INT CtiDeviceLandisGyrS4::getCommandPacket () const
{
    return iCommandPacket;
}

CtiDeviceLandisGyrS4& CtiDeviceLandisGyrS4::setCommandPacket (INT aCmd)
{
    iCommandPacket = aCmd;
    return *this;
}

INT CtiDeviceLandisGyrS4::getCurrentLPChannel () const
{
    return iCurrentLPChannel;
}

CtiDeviceLandisGyrS4& CtiDeviceLandisGyrS4::setCurrentLPChannel (INT aChannel)
{
    iCurrentLPChannel = aChannel;
    return *this;
}
INT CtiDeviceLandisGyrS4::getCurrentLPInterval () const
{
    return iCurrentLPInterval;
}

CtiDeviceLandisGyrS4& CtiDeviceLandisGyrS4::setCurrentLPInterval (INT aInterval)
{
    iCurrentLPInterval = aInterval;
    return *this;
}


ULONG    CtiDeviceLandisGyrS4::getPreviousLPDate() const
{
    return iPreviousLPDate;
}

CtiDeviceLandisGyrS4& CtiDeviceLandisGyrS4::setPreviousLPDate(ULONG c)
{
    iPreviousLPDate = c;
    return *this;
}


ULONG CtiDeviceLandisGyrS4::getCurrentLPDate () const
{
    return iCurrentLPDate;
}

CtiDeviceLandisGyrS4& CtiDeviceLandisGyrS4::setCurrentLPDate (ULONG aDate)
{
    iCurrentLPDate = aDate;
    return *this;
}



INT CtiDeviceLandisGyrS4::generateCommandHandshake (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList)
{
    USHORT retCode = NORMAL;


    switch (getCurrentState())
    {
        case StateHandshakeInitialize:
            {
                // we send the same things each time we send a command so no real handshake
                setRetryAttempts(3);
                setAttemptsRemaining (getRetryAttempts());
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                Transfer.setCRCFlag( 0 );
                Transfer.setInTimeout( 1 );
                setCurrentState (StateHandshakeDecodeStart);
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

INT CtiDeviceLandisGyrS4::generateCommand (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList )
{
    USHORT retCode=NORMAL;

    switch (getCurrentCommand())
    {
        case CmdSelectMeter:
            {
//            retCode = generateCommandSelectMeter (Transfer);
                break;
            }

        case CmdScanData:
            {
//          retCode = generateCommandScan (Transfer);
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
                retCode = !NORMAL;
                break;
            }

    }
    return retCode;
}


INT CtiDeviceLandisGyrS4::generateCommandScan (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList)
{
    int               retCode = NORMAL;
    BYTEUSHORT checkSum;

    /*
     *  This is the scanner request... Scanned data comes through here
     */

    // get appropriate data
    switch (getCurrentState())
    {
        case StateHandshakeComplete:
        case StateScanValueSet1FirstScan:  // done to set attempts only first time thru DLS
            {
                setRetryAttempts(7);


                setAttemptsRemaining(getRetryAttempts());
                setTotalByteCount (0);
                setCommandPacket(0);
            }

        case StateScanValueSet1:
            {
                Transfer.getOutBuffer()[0]=LGS4_ENQ1;
                Transfer.setOutCount( 1 );
                Transfer.setInCountExpected( 1 );
                Transfer.setCRCFlag( 0 );
                Transfer.setInTimeout( 1 );

                setPreviousState (StateScanValueSet1);
                setCurrentState (StateScanDecode1);

                break;
            }

        case StateScanValueSet2:
            {
                Transfer.getOutBuffer()[0] = LGS4_ENQ2;
                Transfer.setOutCount( 1 );
                Transfer.setInCountExpected( 1 );
                Transfer.setCRCFlag( 0 );
                Transfer.setInTimeout( 1 );
                setPreviousState (StateScanValueSet2);
                setCurrentState (StateScanDecode2);

                break;
            }

        case StateScanValueSet3:
            {
                Transfer.getOutBuffer()[0]= LGS4Base[getCommandPacket()].command;
                Transfer.getOutBuffer()[1]=0x00;
                Transfer.getOutBuffer()[2]=0x00;
                Transfer.getOutBuffer()[3]=0x00;
                Transfer.getOutBuffer()[4]=0x00;
                Transfer.getOutBuffer()[5]=0x00;
                Transfer.getOutBuffer()[6]=0x00;
                Transfer.getOutBuffer()[7]=0x00;
                Transfer.getOutBuffer()[8]=0x00;

                // grabbing only the confirmation byte for the moment
                Transfer.setOutCount( 9 );
                Transfer.setInCountExpected( 1 );

                // the confirmation byte will always come in the front of the message
                Transfer.setCRCFlag( XFER_ADD_CRC );
                Transfer.setInTimeout( 1 );

                setPreviousState (StateScanValueSet3);
                setCurrentState (StateScanDecode3);
                break;
            }
        case StateScanValueSet4:
            {
                /***************************
                *  in count is lenght of message returned plus 1 for length byte plus 2 for CRC
                ****************************
                */

                Transfer.setInCountExpected( LGS4Base[getCommandPacket()].totalBytesReturned + 1 + 2);
                Transfer.setOutCount(0);

                // the confirmation byte will always come in the front of the message
                Transfer.setCRCFlag( XFER_VERIFY_CRC );
                Transfer.setInTimeout( 1 );

                setPreviousState (StateScanValueSet4);
                setCurrentState (StateScanDecode4);
                break;
            }
        case StateScanValueSet5:
            {
                // this asking the meter to resend, bad checksum
                Transfer.getOutBuffer()[0]=LGS4_CHECKSUM_ERROR;
                Transfer.setOutCount( 1 );

                /***************************
                *  in count is lenght of message returned plus 1 for length byte plus 2 for CRC
                ****************************
                */
                Transfer.setInCountExpected( LGS4Base[getCommandPacket()].totalBytesReturned + 1 + 2);
                Transfer.setCRCFlag( XFER_VERIFY_CRC );
                Transfer.setInTimeout( 1 );

                setPreviousState (StateScanValueSet5);
                // back to decode state
                setCurrentState (StateScanDecode4);
                break;
            }
        case StateScanValueSet6:
            {
                // this asking the meter to resend, bad checksum
                Transfer.getOutBuffer()[0]=LGS4_TRANSMISSION_ACCEPTED;
                Transfer.setOutCount( 1 );
                Transfer.setInCountExpected( 0 );
                Transfer.setCRCFlag( 0 );
                Transfer.setInTimeout( 1 );

                setPreviousState (StateScanValueSet6);
                // back to decode state
                setCurrentState (StateScanDecode5);
                break;
            }

        case StateScanValueSet7:
            {
                // this asking the meter to resend, bad checksum
                Transfer.getOutBuffer()[0]=LGS4_TRANSMISSION_ACCEPTED;
                Transfer.setOutCount( 1 );
                Transfer.setInCountExpected( 0 );
                Transfer.setCRCFlag( 0 );
                Transfer.setInTimeout( 1 );

                setPreviousState (StateScanValueSet6);
                // back to decode state
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


INT CtiDeviceLandisGyrS4::generateCommandLoadProfile (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList)
{
    int               retCode = NORMAL;
    LGS4LProfileConfig_t * localLPConfig = (LGS4LProfileConfig_t*)iLoadProfileConfig;

    /*
     *  This is the load profile request
     */
    // get appropriate data
    switch (getCurrentState())
    {
        case StateHandshakeComplete:
        case StateScanComplete:
        case StateScanValueSet1FirstScan:
            {
                setRetryAttempts(6);
                setAttemptsRemaining(getRetryAttempts());
                setTotalByteCount (0);
                setCommandPacket(0);
            }

        case StateScanValueSet1:
            {
                Transfer.getOutBuffer()[0]=LGS4_ENQ1;
                Transfer.setOutCount( 1 );
                Transfer.setInCountExpected( 1 );
                Transfer.setCRCFlag( 0 );
                Transfer.setInTimeout( 1 );

                setPreviousState (StateScanValueSet1);
                setCurrentState (StateScanDecode1);

                break;
            }

        case StateScanValueSet2:
            {
                Transfer.getOutBuffer()[0] = LGS4_ENQ2;
                Transfer.setOutCount( 1 );
                Transfer.setInCountExpected( 1 );
                Transfer.setCRCFlag( 0 );
                Transfer.setInTimeout( 1 );
                setPreviousState (StateScanValueSet2);
                setCurrentState (StateScanDecode2);

                break;
            }

        case StateScanValueSet3:
            {
                Transfer.getOutBuffer()[0]=LGS4LoadProfile[getCommandPacket()].command;
                Transfer.getOutBuffer()[1]=0x00;
                Transfer.getOutBuffer()[2]=0x00;
                Transfer.getOutBuffer()[3]=0x00;
                Transfer.getOutBuffer()[4]=0x00;
                Transfer.getOutBuffer()[5]=0x00;
                Transfer.getOutBuffer()[6]=0x00;
                Transfer.getOutBuffer()[7]=0x00;
                Transfer.getOutBuffer()[8]=0x00;

                // hack for the moment to make it work
                if (LGS4LoadProfile[getCommandPacket()].command == 0x03)
                {
                    Transfer.getOutBuffer()[1]= localLPConfig->totalKBytesRequested;
//             Transfer.getOutBuffer()[1]= 0;
//             Transfer.getOutBuffer()[2]= localLPConfig->totalKBytesRequested;
                }

                Transfer.setOutCount( 9 );

                // grabbing only the confirmation byte for the moment
                Transfer.setInCountExpected( 1 );

                // the confirmation byte will always come in the front of the message
                Transfer.setCRCFlag( XFER_ADD_CRC );
                Transfer.setInTimeout( 1 );

                setPreviousState (StateScanValueSet3);
                setCurrentState (StateScanDecode3);
                break;
            }
        case StateScanValueSet4:
            {
                /***************************
                *  in count is lenght of message returned plus 1 for length byte plus 2 for verified CRC
                ****************************
                */
                Transfer.setInCountExpected( LGS4LoadProfile[getCommandPacket()].totalBytesReturned + 1 + 2);

                Transfer.setOutCount(0);

                // the confirmation byte will always come in the front of the message
                Transfer.setCRCFlag( XFER_VERIFY_CRC );
                Transfer.setInTimeout( 1 );

                setPreviousState (StateScanValueSet4);
                setCurrentState (StateScanDecode4);
                break;
            }

        case StateScanValueSet5:
            {
                // this asking the meter to resend, bad checksum
                Transfer.getOutBuffer()[0]=LGS4_CHECKSUM_ERROR;
                Transfer.setOutCount( 1 );

                /***************************
                *  in count is lenght of message returned plus 1 for length byte plus 2 for verified CRC
                ****************************
                */
                Transfer.setInCountExpected( LGS4LoadProfile[getCommandPacket()].totalBytesReturned+1+ 2);
                Transfer.setCRCFlag( XFER_VERIFY_CRC );
                Transfer.setInTimeout( 1 );

                setPreviousState (StateScanValueSet5);
                setCurrentState (StateScanDecode4);

                break;
            }
        case StateScanValueSet6:
            {
                // done, send transmission accepted
                Transfer.getOutBuffer()[0]=LGS4_TRANSMISSION_ACCEPTED;
                Transfer.setOutCount( 1 );
                // add two for the checksum that is added to the back since I am doing that right now
                // add one for the length byte
                Transfer.setInCountExpected( 0 );
                Transfer.setCRCFlag( 0 );
                Transfer.setInTimeout( 1 );

                setPreviousState (StateScanValueSet6);
                // back to decode state
                setCurrentState (StateScanDecode5);
                break;
            }

        case StateScanValueSet7:
            {
                // tell the meter we are ready for the next packet
                Transfer.getOutBuffer()[0]=LGS4_READY_FOR_NEXT_PACKET;
                Transfer.setOutCount( 1 );
                // add two for the checksum that is added to the back since I am doing that right now
                // add one for the length byte
                Transfer.setInCountExpected( 0 );
                Transfer.setCRCFlag( 0 );
                Transfer.setInTimeout( 1 );

                setPreviousState (StateScanValueSet7);
                // back to decode state
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




INT CtiDeviceLandisGyrS4::decodeResponseHandshake (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
//   BYTE  Command;

    switch (getCurrentState())
    {
        case StateHandshakeDecodeStart:
            {
                setCurrentState (StateHandshakeComplete);
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " (" << __LINE__ << ") Invalid state " << getCurrentState() << " scanning " << getName() << endl;
                }
                setCurrentState (StateHandshakeAbort);
                break;
            }
    }
    return NORMAL;
}



INT CtiDeviceLandisGyrS4::decodeResponse (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    USHORT retCode = NORMAL;

    switch (getCurrentCommand())
    {
        case CmdSelectMeter:
            {
                // decode the return message
//            retCode = decodeResponseSelectMeter (Transfer, commReturnValue);
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

        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " (" << __LINE__ << ") Invalid command " << getCurrentCommand() << " scanning " << getName() << endl;
                }
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState (StateScanAbort);
                retCode = !NORMAL;
                break;
            }
    }
    return retCode;
}


INT CtiDeviceLandisGyrS4::decodeResponseScan (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    int               retCode    = NORMAL;
    LGS4LoadProfile_t    *localLP         = ((LGS4LoadProfile_t *)iLoadProfileBuffer);
    CHAR  dataBuffer[100];

    // get appropriate data
    switch (getCurrentState())
    {
        case StateScanDecode1:
            {
                if (Transfer.getInBuffer()[0] == LGS4_ENQ1)
                {
                    setCurrentState (StateScanValueSet2);
                }
                else
                {
                    if (Transfer.doTrace(ERRUNKNOWN))
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " NAK: (no 0x55) attempting to scan " << getName() << endl;
                    }

                    setAttemptsRemaining(getAttemptsRemaining()-1);

                    if (getAttemptsRemaining() <= 0)
                    {
                        setCurrentState (StateScanAbort);
                        retCode = StateScanAbort;
                    }
                    else
                    {
                        setCurrentState (getPreviousState());
                    }
                }
                break;
            }

        case StateScanDecode2:
            {
                if (Transfer.getInBuffer()[0] == LGS4_ENQ2)
                {
                    setAttemptsRemaining(getRetryAttempts());
                    setCurrentState (StateScanValueSet3);
                }
                else
                {
                    if (Transfer.doTrace(ERRUNKNOWN))
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " NAK: (no 0xAA) attempting to scan " << getName() << endl;
                    }

                    setAttemptsRemaining(getAttemptsRemaining()-1);

                    if (getAttemptsRemaining() <= 0)
                    {
                        setCurrentState (StateScanAbort);
                        retCode = StateScanAbort;
                    }
                    else
                    {
                        setCurrentState (StateScanValueSet1);
                    }
                }
                break;
            }

        case StateScanDecode3:
            {
                // check for comm port error or bad return value
                if (commReturnValue || Transfer.getInBuffer()[0] != LGS4_TRANSMISSION_ACCEPTED)
                {
                    if (Transfer.getInBuffer()[0] == LGS4_CHECKSUM_ERROR)
                    {
                        // failed on the checksum, resend the packet
                        setAttemptsRemaining(getAttemptsRemaining()-1);

                        if (getAttemptsRemaining() <= 0)
                        {
                            setCurrentState (StateScanAbort);
                            retCode = StateScanAbort;
                        }
                        else
                        {
                            // do this all again
                            setCurrentState (getPreviousState());
                        }
                    }
                    else if (Transfer.getInBuffer()[0] == LGS4_COMMAND_NOT_ACCEPTED)
                    {
                        // failed on the checksum, resend the packet
                        setAttemptsRemaining(0);

                        setCurrentState (StateScanAbort);
                        retCode = StateScanAbort;
                    }
                    else
                    {
                        // comm return failed, send command again if attempts remaining
                        setAttemptsRemaining(getAttemptsRemaining()-1);

                        if (getAttemptsRemaining() <= 0)
                        {
                            setCurrentState (StateScanAbort);
                            retCode = StateScanAbort;
                        }
                        else
                        {
                            // do this all again
                            setCurrentState (getPreviousState());
//                        retCode = LGS4_RESEND_PREVIOUS_CMD;
                        }

                    }
                }
                else // Good Data read
                {
                    if (LGS4Base[getCommandPacket()].command == LGS4_HANGUP)
                    {
                        setCurrentState (StateScanComplete);
                        setCurrentCommand(CmdLoadProfileTransition);
                    }
                    else
                    {
                        setPreviousState (StateScanDecode3);
                        setCurrentState (StateScanValueSet4);
                    }
                }
                break;
            }
        case StateScanDecode4:
            {
                // message comes in byte zero being a check code
                if (commReturnValue == BADCRC)
                {
                    // comm return failed, send command again if attempts remaining
                    setAttemptsRemaining(getAttemptsRemaining()-1);

                    if (getAttemptsRemaining() <= 0)
                    {
                        setCurrentState (StateScanAbort);
                        retCode = StateScanAbort;
                    }
                    else
                    {
                        setPreviousState (getCurrentState());
                        setCurrentState (StateScanValueSet5);
                    }
                }
                else if (commReturnValue)
                {
                    // comm return failed, send command again if attempts remaining
                    setAttemptsRemaining(getAttemptsRemaining()-1);

                    if (getAttemptsRemaining() <= 0)
                    {
                        setCurrentState (StateScanAbort);
                        retCode = StateScanAbort;
                    }
                    else
                    {
                        // do this all again
                        setCurrentState (getPreviousState());
                    }
                }
                else
                {
                    memcpy(&iDataBuffer[getTotalByteCount()],
                           &Transfer.getInBuffer()[1 + LGS4Base[getCommandPacket()].startOffset],
                           LGS4Base[getCommandPacket()].bytesToRead);
                    setTotalByteCount (getTotalByteCount() + LGS4Base[getCommandPacket()].bytesToRead);

                    // next command packet in the array
                    setCommandPacket (getCommandPacket() + 1);

                    setPreviousState (StateScanDecode4);
                    setCurrentState (StateScanValueSet6);
                }
                break;
            }
        case StateScanDecode5:
            {
                // message comes in byte zero being a check code
                setPreviousState (StateScanDecode5);
                setCurrentState (StateScanValueSet1);
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

INT CtiDeviceLandisGyrS4::decodeResponseLoadProfile (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    int               retCode    = NORMAL;
    LGS4LProfileConfig_t *localLPConfig   = ((LGS4LProfileConfig_t*)iLoadProfileConfig);
    LGS4LoadProfile_t    *localLP         = ((LGS4LoadProfile_t *)iLoadProfileBuffer);

    // get appropriate data
    switch (getCurrentState())
    {
        case StateScanDecode1:
            {

                if (Transfer.getInBuffer()[0] == LGS4_ENQ1)
                {
                    setCurrentState (StateScanValueSet2);
                }
                else
                {
                    if (Transfer.doTrace(ERRUNKNOWN))
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " NAK: (no 0x55) attempting to scan " << getName() << endl;
                    }

                    setAttemptsRemaining(getAttemptsRemaining()-1);

                    if (getAttemptsRemaining() <= 0)
                    {
                        setCurrentState (StateScanAbort);
                        retCode = StateScanAbort;
                    }
                    else
                    {
                        setCurrentState (getPreviousState());
                    }
                }
                break;
            }

        case StateScanDecode2:
            {
                if (Transfer.getInBuffer()[0] == LGS4_ENQ2)
                {
                    setAttemptsRemaining(getRetryAttempts());
                    setCurrentState (StateScanValueSet3);

                }
                else
                {
                    if (Transfer.doTrace(ERRUNKNOWN))
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " NAK: (no 0xAA) attempting to scan " << getName() << endl;
                    }

                    setAttemptsRemaining(getAttemptsRemaining()-1);

                    if (getAttemptsRemaining() <= 0)
                    {
                        setCurrentState (StateScanAbort);
                        retCode = StateScanAbort;
                    }
                    else
                    {
                        setCurrentState (StateScanValueSet1);
                    }
                }

                break;
            }

        case StateScanDecode3:
            {
                // check for comm port error or bad return value
                if (commReturnValue || Transfer.getInBuffer()[0] != LGS4_TRANSMISSION_ACCEPTED)
                {
                    if (Transfer.getInBuffer()[0] == LGS4_CHECKSUM_ERROR)
                    {
                        // failed on the checksum, resend the packet
                        setAttemptsRemaining(getAttemptsRemaining()-1);

                        if (getAttemptsRemaining() <= 0)
                        {
                            setCurrentState (StateScanAbort);
                            retCode = StateScanAbort;
                        }
                        else
                        {
                            // do this all again
                            setCurrentState (getPreviousState());
                        }
                    }
                    else if (Transfer.getInBuffer()[0] == LGS4_COMMAND_NOT_ACCEPTED)
                    {
                        // failed on the checksum, resend the packet
                        setAttemptsRemaining(0);
                        setCurrentState (StateScanAbort);
                        retCode = StateScanAbort;
                    }
                    else
                    {
                        // comm return failed, send command again if attempts remaining
                        setAttemptsRemaining(getAttemptsRemaining()-1);

                        if (getAttemptsRemaining() <= 0)
                        {
                            setCurrentState (StateScanAbort);
                            retCode = StateScanAbort;
                        }
                        else
                        {
                            // do this all again
                            setCurrentState (getPreviousState());
                        }

                    }
                }
                else // Good Data read
                {
                    if (LGS4LoadProfile[getCommandPacket()].command == LGS4_HANGUP)
                    {
                        setCurrentState (StateScanComplete);

                    }
                    else
                    {
                        setPreviousState (StateScanDecode3);
                        setCurrentState (StateScanValueSet4);
                    }
                }
                break;
            }
        case StateScanDecode4:
            {
                // check for bad checksum
                if (commReturnValue == BADCRC)
                {
                    // comm return failed, send command again if attempts remaining
                    setAttemptsRemaining(getAttemptsRemaining()-1);

                    if (getAttemptsRemaining() <= 0)
                    {
                        setCurrentState (StateScanAbort);
                        retCode = StateScanAbort;
                    }
                    else
                    {
                        setPreviousState (getCurrentState());
                        setCurrentState (StateScanValueSet5);
                    }
                }
                else if (commReturnValue)
                {
                    // comm return failed, send command again if attempts remaining
                    setAttemptsRemaining(getAttemptsRemaining()-1);

                    if (getAttemptsRemaining() <= 0)
                    {
                        setCurrentState (StateScanAbort);
                        retCode = StateScanAbort;
                    }
                    else
                    {
                        // do this all again
                        setCurrentState (getPreviousState());
                    }
                }
                else
                {

                    // move this to a function
                    switch (LGS4LoadProfile[getCommandPacket()].command)
                    {
                        case 0x02:
                            {
                                localLP->meterDate.year       =  (UCHAR)S4BCDtoBase10(&Transfer.getInBuffer()[2]     ,     1);
                                localLP->meterDate.day        =  (UCHAR)S4BCDtoBase10(&Transfer.getInBuffer()[3]     ,     1);
                                localLP->meterDate.month      =  (UCHAR)S4BCDtoBase10(&Transfer.getInBuffer()[4]     ,     1);
                                // send the confirmation
                                setPreviousState (StateScanDecode4);
                                setCurrentState (StateScanValueSet6);

                                break;
                            }
                        case 0xC3:
                            {
                                // fill our lp configuration
                                // if the third byte is zero, we retrieve the lp interval from byte 1
                                localLPConfig->intervalLength=0;
                                localLPConfig->intervalLength = (USHORT)S4BCDtoBase10 (&Transfer.getInBuffer()[3],1);


                                if (localLPConfig->intervalLength == 0)
                                {
                                    // get the interval based on the bit pattern
                                    if (Transfer.getInBuffer()[1] & 0x02)
                                        localLPConfig->intervalLength = 15;
                                    else if (Transfer.getInBuffer()[1] & 0x04)
                                        localLPConfig->intervalLength = 30;
                                    else if (Transfer.getInBuffer()[1] & 0x20)
                                        localLPConfig->intervalLength = 5;
                                    else if (Transfer.getInBuffer()[1] & 0x40)
                                        localLPConfig->intervalLength = 1;
                                }

                                if (shouldRetrieveLoadProfile (localLP->porterLPTime, localLPConfig->intervalLength))
                                {
                                    // number of channels
                                    localLPConfig->numberOfChannels=(USHORT)Transfer.getInBuffer()[2];

                                    if (localLPConfig->numberOfChannels == 0 || localLPConfig->intervalLength == 0)
                                    {
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " Meter " << getName() << " has no load profile channels configured" << endl;
                                        }
                                        setCommandPacket (getCommandPacket()+5);
                                        setPreviousState (StateScanDecode4);
                                        setCurrentState (StateScanValueSet6);
                                    }
                                    else
                                    {
                                        /*********************************
                                        *  number of kilobytes to request formula
                                        *
                                        *  days * (intervals/day +1) / 512 * # of channels
                                        *
                                        *  Plus I need to round up so I get all the data
                                        **********************************
                                        */
                                        // do this from midnight, we don't want to miss data because of rounding problems
                                        RWTime midnightOnLastLP (RWDate(RWDate(RWTime(localLP->porterLPTime)).day(), RWDate(RWTime(localLP->porterLPTime)).year()));
                                        ULONG difference = (((RWTime().seconds() - midnightOnLastLP.seconds()) / 86400) + 1);

                                        // no more than thirty one days
                                        if (difference > 31)
                                        {
                                            difference = 31;
                                        }

                                        // calculate number of kbytes needed (add one to round up to next kbyte)
                                        localLPConfig->totalKBytesRequested =
                                        (difference * ((((24*60)/localLPConfig->intervalLength)+1) /
                                                       512.0 * localLPConfig->numberOfChannels)) + 1;

                                        localLPConfig->totalBytesReceived = 0;

                                        // send the confirmation
                                        setPreviousState (StateScanDecode4);
                                        setCurrentState (StateScanValueSet6);
                                    }
                                }
                                else
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " Load profile for " << getName() << " will not be collected this scan" << endl;
                                    }
                                    setCommandPacket (getCommandPacket()+5);
                                    setPreviousState (StateScanDecode4);
                                    setCurrentState (StateScanValueSet6);
                                }
                                break;

                            }
                        case 0xA6:
                            {
                                for (int i=0; i <15; i++)
                                {
                                    localLPConfig->channelMetrics[i] = (USHORT)Transfer.getInBuffer()[i+1];
                                }

                                // send the confirmation
                                setPreviousState (StateScanDecode4);
                                setCurrentState (StateScanValueSet6);
                                break;
                            }
                        case 0x1F:
                            {
                                BYTE k[3];

                                memcpy (&k,
                                        &Transfer.getInBuffer()[1 + LGS4LoadProfile[getCommandPacket()].startOffset],
                                        LGS4LoadProfile[getCommandPacket()].bytesToRead);

                                localLPConfig->kFactor = (FLOAT)S4BCDtoBase10(k, 3) / 1000.0;

                                // send the confirmation
                                setPreviousState (StateScanDecode4);
                                setCurrentState (StateScanValueSet6);
                                break;
                            }
                        case 0xAF:
                            {
                                // fill the structure
                                memcpy (&localLPConfig->phaseC.voltageConstant, &Transfer.getInBuffer()[1],2);
                                memcpy (&localLPConfig->phaseB.voltageConstant, &Transfer.getInBuffer()[3],2);
                                memcpy (&localLPConfig->phaseA.voltageConstant, &Transfer.getInBuffer()[5],2);

                                memcpy (&localLPConfig->phaseC.currentConstant, &Transfer.getInBuffer()[7],2);
                                memcpy (&localLPConfig->phaseB.currentConstant, &Transfer.getInBuffer()[9],2);
                                memcpy (&localLPConfig->phaseA.currentConstant, &Transfer.getInBuffer()[11],2);

                                localLPConfig->classCode = (USHORT)Transfer.getInBuffer()[23];
                                localLPConfig->voltageCode = (USHORT)Transfer.getInBuffer()[24];

                                if( DebugLevel & 0x0001 )
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout <<"----- S3 Calibration Constants----" << endl;
                                    dout <<"          Current  Voltage" << endl;
                                    dout <<"Phase A:    " << localLPConfig->phaseA.currentConstant << "       " << localLPConfig->phaseA.voltageConstant << endl;
                                    dout <<"Phase B:    " << localLPConfig->phaseB.currentConstant << "       " << localLPConfig->phaseB.voltageConstant << endl;
                                    dout <<"Phase C:    " << localLPConfig->phaseC.currentConstant << "       " << localLPConfig->phaseC.voltageConstant << endl;
                                    dout << "Class Code:   "<<localLPConfig->classCode << endl;
                                    dout << "Voltage Code: "<<localLPConfig->voltageCode << endl;
                                }
                                setPreviousState (StateScanDecode4);
                                setCurrentState (StateScanValueSet6);
                                break;
                            }
                        case 0x03:
                            {
                                localLPConfig->totalBytesReceived += LGS4_LOADPROFILE_BUFFER_SIZE;

                                memcpy (localLP->loadProfileData, &Transfer.getInBuffer()[1], LGS4_LOADPROFILE_BUFFER_SIZE);

                                if (localLPConfig->totalBytesReceived >= (localLPConfig->totalKBytesRequested *1000))
                                {
                                    // flag this as the last load profile message
                                    localLP->lastLPMessage = TRUE;
                                    setPreviousState (StateScanValueSet6);
                                }
                                else
                                {

                                    localLP->lastLPMessage = FALSE;
                                    setPreviousState (StateScanValueSet7);
                                }
                                // always returning the load profile
                                setCurrentState (StateScanReturnLoadProfile);

                                break;
                            }
                        default:
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " (" << __LINE__ << ") Unknown command sent to " << getName() << endl;
                            }
                    }
                }

                // check if we are through our command packet array (zero based so add one)
                break;
            }
        case StateScanDecode5:
            {
                // message comes in byte zero being a check code

                if (LGS4LoadProfile[getCommandPacket()].command == 0x03)
                {
                    if (localLPConfig->totalBytesReceived >= (localLPConfig->totalKBytesRequested*1000))
                    {
                        setPreviousState (StateScanDecode5);
                        setCurrentState (StateScanValueSet1);
                        setCommandPacket (getCommandPacket() + 1);

                    }
                    else
                    {
                        setPreviousState (StateScanDecode5);
                        setCurrentState (StateScanValueSet4);
                    }
                }
                else if (LGS4LoadProfile[getCommandPacket()].command == LGS4_HANGUP)
                {
                    setPreviousState (StateScanDecode5);
                    setCurrentState (StateScanValueSet1);
                }
                else
                {
                    setCommandPacket (getCommandPacket() + 1);
                    setPreviousState (StateScanDecode5);
                    setCurrentState (StateScanValueSet1);
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

INT CtiDeviceLandisGyrS4::GeneralScan(CtiRequestMsg *pReq,
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

    ULONG BytesWritten;

    // load profile information
    time_t         RequestedTime = 0L;
    time_t         DeltaTime;
    time_t         NowTime;

    if (OutMessage != NULL)
    {
        OutMessage->Buffer.DUPReq.Identity = IDENT_LANDISGYRS4;

        /*************************
         *
         *   setting the current command in hopes that someday we don't have to use the command
         *   bits in the in message to decide what we're doing DLS
         *
         *************************
         */
        setCurrentCommand(CmdScanData);
        OutMessage->Buffer.DUPReq.Command[0] = CmdScanData;       // One call does it all...

        /**************************
        * default to 0 on the scan because we don't know where in the
        * load profile memory map we will end up
        *
        * this will be reset as soon as we find a date stamp in the return data
        * channel is to keep track of which channel I am on as soon as the next
        * data stream comes in
        ***************************
        */
        setCurrentLPDate (0L);
        setCurrentLPChannel (0L);
        setLPTimeUpdateFlag (FALSE);

        // other initializations that need to take place
        setPowerDownTime(0);
        setPowerUpTime(0);
        setPowerUpDate(0);

        setNewDate(0);
        setOldTime(0);

        // whether this is needed is decided later
        OutMessage->Buffer.DUPReq.LP_Time = getLastLPTime().seconds();
        OutMessage->Buffer.DUPReq.LastFileTime = getLastLPTime().seconds();

        // Load all the other stuff that is needed
        OutMessage->DeviceID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();

        EstablishOutMessagePriority( OutMessage, ScanPriority );
        if (!isMaster())
        {
            OverrideOutMessagePriority( OutMessage, OutMessage->Priority - 1 );
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

INT CtiDeviceLandisGyrS4::allocateDataBins  (OUTMESS *outMess)
{
    if (iDataBuffer == NULL)
        iDataBuffer = CTIDBG_new BYTE[sizeof (LGS4ScanData_t)];

    if (iLoadProfileBuffer == NULL)
    {
        iLoadProfileBuffer = CTIDBG_new BYTE[sizeof (LGS4LoadProfile_t)];

        if (iLoadProfileBuffer != NULL)
        {
            ((LGS4LoadProfile_t *)iLoadProfileBuffer)->porterLPTime = outMess->Buffer.DUPReq.LP_Time;
        }
    }

    if (iLoadProfileConfig == NULL)
    {
        if (iLoadProfileBuffer != NULL)
        {
            iLoadProfileConfig = (BYTE *)&(((LGS4LoadProfile_t *)iLoadProfileBuffer)->configuration);
        }
    }

    // set the command based in the out message command
    setTotalByteCount (0);
    setCurrentCommand ((CtiDeviceIED::CtiMeterCmdStates_t)outMess->Buffer.DUPReq.Command[0]);
    setCurrentState (StateHandshakeInitialize);
    return NORMAL;
}


INT CtiDeviceLandisGyrS4::freeDataBins  ()
{
    if (iDataBuffer != NULL)
    {
        delete []iDataBuffer;
        iDataBuffer = NULL;
    }

    if (iLoadProfileBuffer != NULL)
    {
        iLoadProfileConfig = NULL;
        delete []iLoadProfileBuffer;
        iLoadProfileBuffer = NULL;
    }

    // re-init everything for next time through
    setTotalByteCount (0);
    setCurrentLPChannel (0);
    setCurrentLPInterval(0);
    setCurrentState (StateHandshakeInitialize);
    return NORMAL;
}

void reverseCharacters (UCHAR *source, PCHAR dest)
{
    int y = 14;
    for (int x=0; x <15; x++)
    {
        dest[x] = (CHAR)source[y];
        y--;
    }
}

/****************************************
*
*  LSB is low byte
*
*****************************************
*/
DOUBLE S4BCDtoDouble(UCHAR* buffer, ULONG len)
{

    int i;
    DOUBLE temp;
    DOUBLE scratch = 0.0;

    for (i = (INT)len; i > 0; i--)
    {
        temp = 0.0;

        /* The high nibble */
        temp += (((buffer[i-1] & 0xf0) >> 4)  * 10);

        /* The Low nibble */
        temp += (buffer[i-1] & 0x0f);

        scratch = scratch * 100 + temp ;
    }

    return scratch;
}

/****************************************
*
*  LSB is low byte
*
*****************************************
*/
ULONG S4BCDtoBase10(UCHAR* buffer, ULONG len)
{

    int i;
    ULONG temp;
    ULONG scratch = 0;

    for (i = (INT)len; i > 0; i--)
    {
        temp = 0;

        /* The high nibble */
        temp += (((buffer[i-1] & 0xf0) >> 4)  * 10);

        /* The Low nibble */
        temp += (buffer[i-1] & 0x0f);

        scratch = scratch * 100 + temp ;
    }

    return scratch;
}


/**********************************************************************************
*
*  Explanation what happens here
*
*  S4 has a variation of the IEEE 754 floating point conversion.  The S4 only uses
*  3 bytes instead of 4.  Order is:
*
*   1  15                   8
*  ------------------------------------
*  |s|F                    | e        |
*  ------------------------------------
*     Msb      lsb         msb  lsb
*
*  F is the mantissa representing the float by set bits and the formula 1 / POW (2,bitNumber)
*        (ie.  bit 14 set = 1/2
*              bit 13 set = 1/4
*              bit 12 set = 1/8
*              .
*              .
*              .
*              bit 0 set = 1/ POW (2,15)
*
*  e = binary representation of number between 0 - 255
*
*  s = sign bit
*
* Formula for conversion is newFloat = POW (-1,s) * 1.F * POW (2,e-128)
*
*  NOTE:  Implied 1. added to F and the S4 varies from IEEE by a degree of one
************************************************************************************
*/

FLOAT S4Float (UCHAR byteArray[3])
{
    FLOAT tmp = 1.0;
    int i;
    USHORT mantissa;
    UCHAR exponent = byteArray[2];

    union
    {
        USHORT sh;
        UCHAR   ch[2];
    } Flipper;

    Flipper.ch[0] = byteArray[1];
    Flipper.ch[1] = byteArray[0];
    mantissa = Flipper.sh;

    for (i=1; i <=15; i++)
    {
        // check for set bits
        if (mantissa & (0x01 << (15-i)))
            tmp += (1 / pow (2,i));
    }

    // add the exponent
    tmp = tmp * pow (2, (USHORT)exponent - 128);

    if (mantissa & (0x01 << 15))
        tmp = -tmp;
    return tmp;
}


USHORT S4UShort (UCHAR byteArray[2])
{
    union
    {
        USHORT sh;
        UCHAR   ch[2];
    } Flipper;

    Flipper.ch[0] = byteArray[0];
    Flipper.ch[1] = byteArray[1];
    return Flipper.sh;
}

/*********************************
*
* Decimal for power factor is implied after most significant digit
*
**********************************
*/
INT findImpliedDecimal (UCHAR buffer[2])
{
    UCHAR tmp[2];
    int retVal=1;

    // work on a copy
    memcpy (tmp, buffer, 2);

    // get rid of most significant bit
    tmp[1] &= 0x7f;

    if (tmp[1] & 0xf0)
        retVal = 4;
    else if (tmp[1] & 0x0f)
        retVal = 3;
    else if (tmp[0] & 0xf0)
        retVal = 2;
    else if (tmp[0] & 0x0f)
        retVal = 1;


    return retVal;


}

INT CtiDeviceLandisGyrS4::reformatDataBuffer(BYTE *aInMessBuffer, ULONG &aTotalBytes)

{
    LGS4ScanData_t *ptr = (LGS4ScanData_t *)(iDataBuffer);

    ptr->Real.dateTime.minutes    =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTime.minutes    ,     1);
    ptr->Real.dateTime.hours      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTime.hours      ,     1);
    ptr->Real.dateTime.dayOfWeek  =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTime.dayOfWeek  ,     1);
    ptr->Real.dateTime.year       =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTime.year       ,     1);
    ptr->Real.dateTime.day        =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTime.day        ,     1);
    ptr->Real.dateTime.month      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTime.month    ,     1);

    // one byte entries are ok just to cast as a ushort
    ptr->Real.powerOutages        =  (USHORT) ptr->Byte.powerOutages;
    ptr->Real.selectableMetric    =  (USHORT) ptr->Byte.selectableMetric;
    ptr->Real.billingMetric       =  (USHORT) ptr->Byte.billingMetric;
    ptr->Real.thirdMetric         =  (USHORT) ptr->Byte.thirdMetric;

    // implied decimal between digits 3 and 4
    ptr->Real.kFactor =
    (FLOAT)S4BCDtoBase10(ptr->Byte.kFactor, 3) / 1000.0;

    ptr->Real.demandInterval = (USHORT)ptr->Byte.subIntervals * ((USHORT)ptr->Byte.numberOfSubIntervals + 1);

    ptr->Real.prevIntervalDemand =  (S4UShort(ptr->Byte.prevIntervalDemand)  * ptr->Real.kFactor / 1000) * (60.0 / ptr->Real.demandInterval);


    ptr->Real.prevIntervalSelectedMetric =  (S4UShort(ptr->Byte.prevIntervalSelectedMetric) *  ptr->Real.kFactor / 1000) * (60.0 / ptr->Real.demandInterval);

    ptr->Real.prevIntervalThirdMetric =  (S4UShort(ptr->Byte.prevIntervalThirdMetric) *  ptr->Real.kFactor / 1000) * (60.0 / ptr->Real.demandInterval);

    ptr->Real.serialNumber =
    (ULONG)S4BCDtoBase10(ptr->Byte.serialNumber,      4);

    reverseCharacters (ptr->Byte.deviceID, ptr->Real.deviceID);

    ptr->Real.neutralCurrent = S4Float( ptr->Byte.neutralCurrent);
    ptr->Real.phaseACurrent = S4Float( ptr->Byte.phaseACurrent);
    ptr->Real.phaseBCurrent = S4Float( ptr->Byte.phaseBCurrent);
    ptr->Real.phaseCCurrent = S4Float( ptr->Byte.phaseCCurrent);

    ptr->Real.phaseAVoltage = S4Float( ptr->Byte.phaseAVoltage);
    ptr->Real.phaseBVoltage = S4Float( ptr->Byte.phaseBVoltage);
    ptr->Real.phaseCVoltage = S4Float( ptr->Byte.phaseCVoltage);


    ptr->Real.dateTimeRateAMaxkW.minutes    =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateAMaxkW.minutes    ,     1);
    ptr->Real.dateTimeRateAMaxkW.hours      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateAMaxkW.hours      ,     1);
    ptr->Real.dateTimeRateAMaxkW.dayOfWeek  =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateAMaxkW.dayOfWeek  ,     1);
    ptr->Real.dateTimeRateAMaxkW.year       =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateAMaxkW.year       ,     1);
    ptr->Real.dateTimeRateAMaxkW.day        =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateAMaxkW.day        ,     1);
    ptr->Real.dateTimeRateAMaxkW.month      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateAMaxkW.month    ,     1);


    ptr->Real.dateTimeRateBMaxkW.minutes    =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateBMaxkW.minutes    ,     1);
    ptr->Real.dateTimeRateBMaxkW.hours      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateBMaxkW.hours      ,     1);
    ptr->Real.dateTimeRateBMaxkW.dayOfWeek  =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateBMaxkW.dayOfWeek  ,     1);
    ptr->Real.dateTimeRateBMaxkW.year       =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateBMaxkW.year       ,     1);
    ptr->Real.dateTimeRateBMaxkW.day        =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateBMaxkW.day        ,     1);
    ptr->Real.dateTimeRateBMaxkW.month      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateBMaxkW.month    ,     1);

    ptr->Real.dateTimeRateCMaxkW.minutes    =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateCMaxkW.minutes    ,     1);
    ptr->Real.dateTimeRateCMaxkW.hours      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateCMaxkW.hours      ,     1);
    ptr->Real.dateTimeRateCMaxkW.dayOfWeek  =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateCMaxkW.dayOfWeek  ,     1);
    ptr->Real.dateTimeRateCMaxkW.year       =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateCMaxkW.year       ,     1);
    ptr->Real.dateTimeRateCMaxkW.day        =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateCMaxkW.day        ,     1);
    ptr->Real.dateTimeRateCMaxkW.month      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateCMaxkW.month    ,     1);

    ptr->Real.dateTimeRateDMaxkW.minutes    =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateDMaxkW.minutes    ,     1);
    ptr->Real.dateTimeRateDMaxkW.hours      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateDMaxkW.hours      ,     1);
    ptr->Real.dateTimeRateDMaxkW.dayOfWeek  =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateDMaxkW.dayOfWeek  ,     1);
    ptr->Real.dateTimeRateDMaxkW.year       =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateDMaxkW.year       ,     1);
    ptr->Real.dateTimeRateDMaxkW.day        =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateDMaxkW.day        ,     1);
    ptr->Real.dateTimeRateDMaxkW.month      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateDMaxkW.month    ,     1);

    ptr->Real.dateTimeRateEMaxkW.minutes    =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateEMaxkW.minutes    ,     1);
    ptr->Real.dateTimeRateEMaxkW.hours      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateEMaxkW.hours      ,     1);
    ptr->Real.dateTimeRateEMaxkW.dayOfWeek  =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateEMaxkW.dayOfWeek  ,     1);
    ptr->Real.dateTimeRateEMaxkW.year       =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateEMaxkW.year       ,     1);
    ptr->Real.dateTimeRateEMaxkW.day        =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateEMaxkW.day        ,     1);
    ptr->Real.dateTimeRateEMaxkW.month      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateEMaxkW.month    ,     1);

    ptr->Real.rateAMaxkW =
    (DOUBLE) (S4UShort(ptr->Byte.rateAMaxkW) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);
    ptr->Real.rateBMaxkW =
    (DOUBLE) (S4UShort(ptr->Byte.rateBMaxkW) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);
    ptr->Real.rateCMaxkW =
    (DOUBLE) (S4UShort(ptr->Byte.rateCMaxkW) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);
    ptr->Real.rateDMaxkW =
    (DOUBLE) (S4UShort(ptr->Byte.rateDMaxkW) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);
    ptr->Real.rateEMaxkW =
    (DOUBLE) (S4UShort(ptr->Byte.rateEMaxkW) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);

    ptr->Real.dateTimeRateAMaxkM.minutes    =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateAMaxkM.minutes    ,     1);
    ptr->Real.dateTimeRateAMaxkM.hours      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateAMaxkM.hours      ,     1);
    ptr->Real.dateTimeRateAMaxkM.dayOfWeek  =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateAMaxkM.dayOfWeek  ,     1);
    ptr->Real.dateTimeRateAMaxkM.year       =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateAMaxkM.year       ,     1);
    ptr->Real.dateTimeRateAMaxkM.day        =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateAMaxkM.day        ,     1);
    ptr->Real.dateTimeRateAMaxkM.month      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateAMaxkM.month    ,     1);

    ptr->Real.dateTimeRateBMaxkM.minutes    =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateBMaxkM.minutes    ,     1);
    ptr->Real.dateTimeRateBMaxkM.hours      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateBMaxkM.hours      ,     1);
    ptr->Real.dateTimeRateBMaxkM.dayOfWeek  =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateBMaxkM.dayOfWeek  ,     1);
    ptr->Real.dateTimeRateBMaxkM.year       =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateBMaxkM.year       ,     1);
    ptr->Real.dateTimeRateBMaxkM.day        =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateBMaxkM.day        ,     1);
    ptr->Real.dateTimeRateBMaxkM.month      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateBMaxkM.month    ,     1);

    ptr->Real.dateTimeRateCMaxkM.minutes    =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateCMaxkM.minutes    ,     1);
    ptr->Real.dateTimeRateCMaxkM.hours      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateCMaxkM.hours      ,     1);
    ptr->Real.dateTimeRateCMaxkM.dayOfWeek  =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateCMaxkM.dayOfWeek  ,     1);
    ptr->Real.dateTimeRateCMaxkM.year       =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateCMaxkM.year       ,     1);
    ptr->Real.dateTimeRateCMaxkM.day        =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateCMaxkM.day        ,     1);
    ptr->Real.dateTimeRateCMaxkM.month      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateCMaxkM.month    ,     1);

    ptr->Real.dateTimeRateDMaxkM.minutes    =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateDMaxkM.minutes    ,     1);
    ptr->Real.dateTimeRateDMaxkM.hours      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateDMaxkM.hours      ,     1);
    ptr->Real.dateTimeRateDMaxkM.dayOfWeek  =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateDMaxkM.dayOfWeek  ,     1);
    ptr->Real.dateTimeRateDMaxkM.year       =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateDMaxkM.year       ,     1);
    ptr->Real.dateTimeRateDMaxkM.day        =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateDMaxkM.day        ,     1);
    ptr->Real.dateTimeRateDMaxkM.month      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateDMaxkM.month    ,     1);

    ptr->Real.dateTimeRateEMaxkM.minutes    =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateEMaxkM.minutes    ,     1);
    ptr->Real.dateTimeRateEMaxkM.hours      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateEMaxkM.hours      ,     1);
    ptr->Real.dateTimeRateEMaxkM.dayOfWeek  =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateEMaxkM.dayOfWeek  ,     1);
    ptr->Real.dateTimeRateEMaxkM.year       =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateEMaxkM.year       ,     1);
    ptr->Real.dateTimeRateEMaxkM.day        =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateEMaxkM.day        ,     1);
    ptr->Real.dateTimeRateEMaxkM.month      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeRateEMaxkM.month    ,     1);

    ptr->Real.rateAMaxkM =
    (DOUBLE) (S4UShort(ptr->Byte.rateAMaxkM) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);
    ptr->Real.rateBMaxkM =
    (DOUBLE) (S4UShort(ptr->Byte.rateBMaxkM) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);
    ptr->Real.rateCMaxkM =
    (DOUBLE) (S4UShort(ptr->Byte.rateCMaxkM) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);
    ptr->Real.rateDMaxkM =
    (DOUBLE) (S4UShort(ptr->Byte.rateDMaxkM) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);
    ptr->Real.rateEMaxkM =
    (DOUBLE) (S4UShort(ptr->Byte.rateEMaxkM) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);

    ptr->Real.rateACoincidentDemand =
    (DOUBLE) (S4UShort(ptr->Byte.rateACoincidentDemand) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);
    ptr->Real.rateBCoincidentDemand =
    (DOUBLE) (S4UShort(ptr->Byte.rateBCoincidentDemand) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);
    ptr->Real.rateCCoincidentDemand =
    (DOUBLE) (S4UShort(ptr->Byte.rateCCoincidentDemand) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);
    ptr->Real.rateDCoincidentDemand =
    (DOUBLE) (S4UShort(ptr->Byte.rateDCoincidentDemand) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);
    ptr->Real.rateECoincidentDemand =
    (DOUBLE) (S4UShort(ptr->Byte.rateECoincidentDemand) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);

    ptr->Real.rateAPowerFactorAtMax =
    (FLOAT)S4BCDtoBase10(ptr->Byte.rateAPowerFactorAtMax,      2) / (pow (10,findImpliedDecimal (ptr->Byte.rateAPowerFactorAtMax)));
    ptr->Real.rateBPowerFactorAtMax =
    (FLOAT)S4BCDtoBase10(ptr->Byte.rateBPowerFactorAtMax,      2) / (pow (10,findImpliedDecimal (ptr->Byte.rateBPowerFactorAtMax)));
    ptr->Real.rateCPowerFactorAtMax =
    (FLOAT)S4BCDtoBase10(ptr->Byte.rateCPowerFactorAtMax,      2) / (pow (10,findImpliedDecimal (ptr->Byte.rateCPowerFactorAtMax)));
    ptr->Real.rateDPowerFactorAtMax =
    (FLOAT)S4BCDtoBase10(ptr->Byte.rateDPowerFactorAtMax,      2) / (pow (10,findImpliedDecimal (ptr->Byte.rateDPowerFactorAtMax)));
    ptr->Real.rateEPowerFactorAtMax =
    (FLOAT)S4BCDtoBase10(ptr->Byte.rateEPowerFactorAtMax,      2) / (pow (10,findImpliedDecimal (ptr->Byte.rateEPowerFactorAtMax)));

    ptr->Real.rateAkWh = S4BCDtoDouble(ptr->Byte.rateAkWh,      6) * (ptr->Real.kFactor) / 1000.0;
    ptr->Real.rateBkWh = S4BCDtoDouble(ptr->Byte.rateBkWh,      6) * (ptr->Real.kFactor) / 1000.0;
    ptr->Real.rateCkWh = S4BCDtoDouble(ptr->Byte.rateCkWh,      6) * (ptr->Real.kFactor) / 1000.0;
    ptr->Real.rateDkWh = S4BCDtoDouble(ptr->Byte.rateDkWh,      6) * (ptr->Real.kFactor) / 1000.0;
    ptr->Real.rateEkWh = S4BCDtoDouble(ptr->Byte.rateEkWh,      6) * (ptr->Real.kFactor) / 1000.0;
    ptr->Real.totalkWh = S4BCDtoDouble(ptr->Byte.totalkWh,      6) * (ptr->Real.kFactor) / 1000.0;

    ptr->Real.rateAkMh = S4BCDtoDouble(ptr->Byte.rateAkMh,      6) * (ptr->Real.kFactor) / 1000.0;
    ptr->Real.rateBkMh = S4BCDtoDouble(ptr->Byte.rateBkMh,      6) * (ptr->Real.kFactor) / 1000.0;
    ptr->Real.rateCkMh = S4BCDtoDouble(ptr->Byte.rateCkMh,      6) * (ptr->Real.kFactor) / 1000.0;
    ptr->Real.rateDkMh = S4BCDtoDouble(ptr->Byte.rateDkMh,      6) * (ptr->Real.kFactor) / 1000.0;
    ptr->Real.rateEkMh = S4BCDtoDouble(ptr->Byte.rateEkMh,      6) * (ptr->Real.kFactor) / 1000.0;
    ptr->Real.totalkMh = S4BCDtoDouble(ptr->Byte.totalkMh,      6) * (ptr->Real.kFactor) / 1000.0;

    ptr->Real.dateTimeMaxkM3.minutes    =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeMaxkM3.minutes    ,     1);
    ptr->Real.dateTimeMaxkM3.hours      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeMaxkM3.hours      ,     1);
    ptr->Real.dateTimeMaxkM3.dayOfWeek  =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeMaxkM3.dayOfWeek  ,     1);
    ptr->Real.dateTimeMaxkM3.year       =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeMaxkM3.year       ,     1);
    ptr->Real.dateTimeMaxkM3.day        =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeMaxkM3.day        ,     1);
    ptr->Real.dateTimeMaxkM3.month      =  (UCHAR)S4BCDtoBase10(&ptr->Byte.dateTimeMaxkM3.month    ,     1);

    // third metric stuff
    ptr->Real.maxkM3 =
    (DOUBLE) (S4UShort(ptr->Byte.maxkM3) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);

    ptr->Real.powerFactorAtMaxkM3 =
    (FLOAT)S4BCDtoBase10(ptr->Byte.powerFactorAtMaxkM3,      2) / (pow (10,findImpliedDecimal (ptr->Byte.powerFactorAtMaxkM3)));

    ptr->Real.coincidentkM3atMaxDemand =
    (DOUBLE) (S4UShort(ptr->Byte.coincidentkM3atMaxDemand) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);

    ptr->Real.totalkMh3 = S4BCDtoDouble(ptr->Byte.totalkMh3,      6) * (ptr->Real.kFactor) / 1000.0;

    ptr->bDataIsReal = TRUE;
    memcpy (aInMessBuffer, iDataBuffer, sizeof (LGS4ScanData_t));
    aTotalBytes = getTotalByteCount();
    return NORMAL;
}


INT CtiDeviceLandisGyrS4::copyLoadProfileData(BYTE *aInMessBuffer, ULONG &aTotalBytes)
{
    memcpy(aInMessBuffer, iLoadProfileBuffer, sizeof (LGS4LoadProfile_t));
    aTotalBytes = sizeof (LGS4LoadProfile_t);
    return NORMAL;
}


INT  CtiDeviceLandisGyrS4::ResultDecode(INMESS *InMessage,
                                        RWTime &TimeNow,
                                        RWTPtrSlist< CtiMessage >   &vgList,
                                        RWTPtrSlist< CtiMessage > &retList,
                                        RWTPtrSlist< OUTMESS > &outList)
{

    /****************************
    *
    *  intialize the command based on the in message
    *  someday the command will be pulled from the device object directly
    *
    *****************************
    */
    char tmpCurrentCommand = InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[0],
         tmpCurrentState   = InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1];

    if( !_dstFlagValid )
    {
        _dstFlag = readDSTFile( getName() );
    }

    switch (tmpCurrentCommand)
    {
        case CmdScanData:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Scan decode for device " << getName() << " in progress " << endl;
                }

                decodeResultScan (InMessage, TimeNow, vgList, retList, outList);
                break;
            }
        case CmdLoadProfileData:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " LP decode for device " << getName() << " in progress " << endl;
                }


                // just in case we're getting an empty message
                if (tmpCurrentState == StateScanReturnLoadProfile)
                {
                    decodeResultLoadProfile (InMessage, TimeNow, vgList, retList, outList);
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " LP decode failed device " << getName() << " invalid state " << endl;
                    }
                }

                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << "(" << __LINE__ << ") *** ERROR *** Invalid decode for " << getName() << endl;
                }
            }
    }

    return NORMAL;
}

INT CtiDeviceLandisGyrS4::ErrorDecode (INMESS *InMessage,
                                       RWTime &TimeNow,
                                       RWTPtrSlist< CtiMessage >   &vgList,
                                       RWTPtrSlist< CtiMessage > &retList,
                                       RWTPtrSlist< OUTMESS > &outList)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Error decode for device " << getName() << " in progress " << endl;
    }

    INT retCode = NORMAL;
    CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);
    CtiReturnMsg   *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                            RWCString(InMessage->Return.CommandStr),
                                            RWCString(),
                                            InMessage->EventCode & 0x7fff,
                                            InMessage->Return.RouteID,
                                            InMessage->Return.MacroOffset,
                                            InMessage->Return.Attempt,
                                            InMessage->Return.TrxID,
                                            InMessage->Return.UserID);


    if (pMsg != NULL)
    {
        pMsg->insert( -1 );           // This is the dispatch token and is unimplemented at this time
        pMsg->insert(OP_DEVICEID);    // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
        pMsg->insert(getID());             // The id (device or point which failed)
        pMsg->insert(ScanRateGeneral);      // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h
        pMsg->insert(InMessage->EventCode);
    }

    insertPointIntoReturnMsg (pMsg, pPIL);

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

    return retCode;
}



INT CtiDeviceLandisGyrS4::decodeResultScan (INMESS *InMessage,
                                            RWTime &TimeNow,
                                            RWTPtrSlist< CtiMessage >   &vgList,
                                            RWTPtrSlist< CtiMessage > &retList,
                                            RWTPtrSlist< OUTMESS > &outList)
{
    char tmpCurrentState = InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1];
    CHAR           temp[100], buffer[60];

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
    RWTime    peakTime;

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

    LGS4ScanData_t  *scanData = (LGS4ScanData_t*) DUPRep->Message;

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
                    if (getMeterDataFromScanStruct (i, PValue, peakTime, scanData))
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

INT CtiDeviceLandisGyrS4::decodeResultLoadProfile (INMESS *InMessage,
                                                   RWTime &TimeNow,
                                                   RWTPtrSlist< CtiMessage >   &vgList,
                                                   RWTPtrSlist< CtiMessage > &retList,
                                                   RWTPtrSlist< OUTMESS > &outList)
{
    DIALUPREQUEST     *dupReq = &InMessage->Buffer.DUPSt.DUPRep.ReqSt;
    DIALUPREPLY       *dupRep = &InMessage->Buffer.DUPSt.DUPRep;

    LGS4LoadProfile_t *localLP = (LGS4LoadProfile_t*)&dupRep->Message;


    // power outages tracked by 6 second interval since midnight
    ULONG secondsSinceMidnight = 0;
    BYTE  temp[2];
    USHORT  intervalPulses;
    DOUBLE  intervalData;
    USHORT   expectedLastInterval;
    ULONG lastLPTime = getLastLPTime().seconds();
    RWTime   intervalTime;

    CHAR    buffer[60];
    BOOL  validPointFound=FALSE;

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

    CtiReturnMsg   *pLastLPIntervals = NULL;

    // our array of valid points
    LGS4LPPointInfo_t validLPPointInfo[15] = { {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1},
        {0,1.0,-1}};
    // initialize as we go
    for (int offsetWalker=0; offsetWalker < localLP->configuration.numberOfChannels; offsetWalker++)
    {
        findLPDataPoint (validLPPointInfo[offsetWalker], localLP->configuration.channelMetrics[offsetWalker]);
    }

    // print out when we see a time or date stamp
    for (int dataWalker=0;dataWalker <128; dataWalker+=2)
    {
        // reset this for each piece of data we look at
        validPointFound=FALSE;

        /********************
        * check if we are on the last message
        *
        * after which, we allocate the memory for the second
        * return list and fill it as we go
        *********************
        */
        if (localLP->lastLPMessage)
        {
            if (pLastLPIntervals == NULL)
            {
                pLastLPIntervals = CTIDBG_new CtiReturnMsg(getID(),
                                                    RWCString(InMessage->Return.CommandStr),
                                                    RWCString(),
                                                    InMessage->EventCode & 0x7fff,
                                                    InMessage->Return.RouteID,
                                                    InMessage->Return.MacroOffset,
                                                    InMessage->Return.Attempt,
                                                    InMessage->Return.TrxID,
                                                    InMessage->Return.UserID);
            }
            else if (getCurrentLPChannel() == 0)
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
            }
        }

        memcpy (&intervalPulses, &localLP->loadProfileData[dataWalker], 2);

        // checking for a time or date stamp
        if (intervalPulses & 0x8000)
        {
            // we must have a time or date stamp
            if (intervalPulses & 0x4000)
            {
                /*************************
                * bit 14 is set, we have a timestamp
                *
                *  timestamp is number of 6 seconds intervals
                *  since midnight kept in bits 0 -14
                **************************
                */
                secondsSinceMidnight = (0x3FFF & intervalPulses) * 6;

                if (getOldTime() == 0)
                {
                    // set this every time through
                    setOldTime (secondsSinceMidnight);

                    syncAppropriateTime(secondsSinceMidnight);
                }
                else
                {
                    // if CTIDBG_new date is empty, we're doing an inter interval power outage
                    if (getNewDate() == 0)
                    {
                        syncAppropriateTime(secondsSinceMidnight);
                    }
                    else
                    {
                        /***************************
                        * we must have had either a power outage that crossed a
                        * day or someone changed the time
                        * **************************
                        */
                        // route filler if needed
                        LONG dateTimeDifference = (getNewDate() + secondsSinceMidnight) - (getCurrentLPDate() + getOldTime());

                        if (dateTimeDifference < 0)
                        {
                            /******************
                            * time or date was set back
                            *
                            * currently don't handle updating old readings so just
                            * set currentlpdate to CTIDBG_new date and calculate which interval
                            * we are on based on secondsSinceMidnight
                            * and allow the decode routine to work through the data
                            * until it finds what it thinks is the next valid date
                            *******************
                            */

                            setCurrentLPDate (getNewDate());
                            setCurrentLPInterval (secondsSinceMidnight / (localLP->configuration.intervalLength * 60));
                        }
                        else if (dateTimeDifference > (localLP->configuration.intervalLength * 60))
                        {
                            /*******************
                            *  we moved forward more than the length of an interval
                            *
                            *  route filler data and set interval appropriately
                            ********************
                            */
                            ULONG dateChangeMissingIntervals =
                            dateTimeDifference / (localLP->configuration.intervalLength * 60);

                            // ????? need a check for intervals to make sure it isn't outrageous??????
                            for (int x=0; x < dateChangeMissingIntervals; x++)
                            {
                                // fill message with plug data to next interval
                                for (int y=0; y < localLP->configuration.numberOfChannels;y++)
                                {
                                    intervalTime = RWTime (getCurrentLPDate() +
                                                           ((x+1) * localLP->configuration.intervalLength * 60));

                                    verifyAndAddPointToReturnMsg (validLPPointInfo[y].pointId,
                                                                  0.0,
                                                                  DeviceFillerQuality,
                                                                  intervalTime,
                                                                  pPIL,
                                                                  TAG_POINT_LOAD_PROFILE_DATA);
                                }

                                // increment interval and reset if necessary
                                setCurrentLPInterval (getCurrentLPInterval() + 1);
                                if (getCurrentLPInterval() >= (86400 / localLP->configuration.intervalLength*60))
                                {
                                    setCurrentLPInterval (0);
                                }
                            }
                        }
                        else
                        {
                            /******************
                            * small time change within an interval
                            *******************
                            */
                            setCurrentLPDate (getNewDate());
                        }

                        // set these and the next interval will be marked partial
                        setPowerDownTime (secondsSinceMidnight - 1);
                        setPowerUpTime (secondsSinceMidnight);
                    }
                }

            }
            else
            {
                /****************************
                * bit 14 is not set, we have a datestamp
                *
                *  day of month is BCD bit 0 - 7
                *  month is BCD bit 8 - 12
                *****************************
                */

                BYTE mon = (0x1F & localLP->loadProfileData[dataWalker+1]);
                int year = RWDate().year();

                // check what year we are in
                if (S4BCDtoBase10 (&mon,1) > localLP->meterDate.month)
                {
                    // if the month on the record is > the current month, we must be using a different year
                    year -= 1;
                }

                RWDate recordDate (S4BCDtoBase10 (&localLP->loadProfileData[dataWalker],1),
                                   S4BCDtoBase10 (&mon,1),
                                   year);

                /******************
                * if we haven't found a date yet, don't try and fill any info in
                *******************
                */
                // see if we are checking a power outage date
                if ((getPowerDownTime() != 0) && (getCurrentLPDate() !=0) && (getNewDate() == 0))
                {
                    /*******************
                    *  this is a power up date, we need to do some math and filler data
                    ********************
                    */

                    // power down is date plus seconds past midnight
                    ULONG powerDown = getCurrentLPDate() + getPowerDownTime();
                    ULONG missingIntervals = (RWTime(recordDate).seconds() - powerDown) /
                                             (localLP->configuration.intervalLength * 60) ;

                    // ????? need a check for intervals to make sure it isn't outrageous??????
                    for (int x=0; x < missingIntervals; x++)
                    {
                        for (int y=0; y < localLP->configuration.numberOfChannels;y++)
                        {

                            intervalTime = RWTime (getCurrentLPDate() +
                                                   ((x+1) * localLP->configuration.intervalLength * 60));

                            verifyAndAddPointToReturnMsg (validLPPointInfo[y].pointId,
                                                          0.0,
                                                          DeviceFillerQuality,
                                                          intervalTime,
                                                          pPIL,
                                                          TAG_POINT_LOAD_PROFILE_DATA);

                        }

                        // increment interval and reset if necessary
                        setCurrentLPInterval (getCurrentLPInterval() + 1);
                        if (getCurrentLPInterval() >= (86400 / localLP->configuration.intervalLength*60))
                        {
                            setCurrentLPInterval (0);
                        }
                    }
                }

                // always set this because it gets zeroed out if a data packet comes in
                setNewDate (RWTime(recordDate).seconds());

                // set previous date only if newDate doesn't exists
                if (getOldTime() != 0)
                {
                    // we have a CTIDBG_new date, figure out how long the change is
                    setCurrentLPDate (getPreviousLPDate());
                }
                else
                {
                    // set the previous load profile date
                    setPreviousLPDate (getCurrentLPDate());
                    setCurrentLPDate (RWTime(recordDate).seconds());
                }

                // CTIDBG_new day start from zero
                setCurrentLPChannel (0);
                setCurrentLPInterval (0);
            }
        }
        else
        {
            /********************
            * if I get a data interval, the change of date or time can't be happening
            * because they must all go in a row  see pg 86 of protocol doc
            *********************
            */
            setNewDate(0);
            setOldTime(0);

            // we have data
            if (getCurrentLPDate() != 0)
            {
                /*******************
                * current lp interval is zero based, add one to match valid times  DLS
                ********************
                */

                if (getLastLPTime ()  <
                    (getCurrentLPDate() + ((getCurrentLPInterval()+1) * localLP->configuration.intervalLength * 60)))
                {

                    /***************************
                    * protocol doc notes we could end up with repeated data
                    *
                    * because of this, we need to know what our last interval should
                    * be and not go beyound that even though we received data
                    *
                    * zero based calculation (track 15 minute data (96 intervals)
                    * as interval 0 - 95  ) so we must subtract one from this
                    *
                    *****************************
                    */
                    expectedLastInterval = ((RWTime().seconds() - RWTime(0,0).seconds()) /
                                            (localLP->configuration.intervalLength * 60.0));


                    // perform parity checking on reading
                    int cnt=0;
                    for (USHORT scratch = 0x0001; scratch < 0x8000; scratch <<=1)
                    {
                        if (intervalPulses & scratch)
                        {
                            cnt++;
                        }
                    }

                    // check for even parity for each reading
                    if ((cnt % 2) == 0)
                    {
                        // pulse count is first 13 bits
                        intervalPulses &= 0x3FFF;

                        // if we have a power down stamp, we have partial interval
                        if (0 != getPowerDownTime())
                        {
                            if (0 != getPowerUpTime())
                            {

                                /****************************
                                *
                                * calculation is zero based (ie 15 minute data, 96 intervals, 0-95)
                                *
                                *****************************
                                */
                                ULONG newInterval (getPowerUpTime() / (localLP->configuration.intervalLength * 60));

                                // plug data from current to CTIDBG_new interval
                                for (int x = getCurrentLPInterval(); x < newInterval; x++)
                                {
                                    // fill message with plug data to next interval
                                    for (int y=0; y < localLP->configuration.numberOfChannels;y++)
                                    {
                                        intervalTime = RWTime (getCurrentLPDate() +
                                                               ((x+1) * localLP->configuration.intervalLength * 60));

                                        verifyAndAddPointToReturnMsg (validLPPointInfo[y].pointId,
                                                                      0.0,
                                                                      DeviceFillerQuality,
                                                                      intervalTime,
                                                                      pPIL,
                                                                      TAG_POINT_LOAD_PROFILE_DATA);

                                    }
                                }
                                /***************************
                                *  reset this once out of the loop
                                ****************************
                                */
                                setCurrentLPInterval ((ULONG)newInterval);

                                intervalTime = RWTime (getCurrentLPDate() +
                                                       ((getCurrentLPInterval()+1) * localLP->configuration.intervalLength * 60));

                                validPointFound =
                                verifyAndAddPointToReturnMsg (validLPPointInfo[getCurrentLPChannel()].pointId,
                                                              calculateIntervalData (intervalPulses,
                                                                                     localLP,
                                                                                     validLPPointInfo[getCurrentLPChannel()]),
                                                              PartialIntervalQuality,
                                                              intervalTime,
                                                              pPIL,
                                                              TAG_POINT_LOAD_PROFILE_DATA);
                                /*****************************
                                * if we are on the last set of intervals
                                * save them in another message so
                                * they can be displayed on tdc
                                ******************************
                                */
                                if (localLP->lastLPMessage)
                                {
                                    verifyAndAddPointToReturnMsg (validLPPointInfo[getCurrentLPChannel()].pointId,
                                                                  calculateIntervalData (intervalPulses,
                                                                                         localLP,
                                                                                         validLPPointInfo[getCurrentLPChannel()]),
                                                                  PartialIntervalQuality,
                                                                  intervalTime,
                                                                  pLastLPIntervals);
                                }
                                /********************
                                * must go through all the channels before I reset these guys
                                *********************
                                */

                                if ((getCurrentLPChannel() + 1) >= localLP->configuration.numberOfChannels)
                                {
                                    setPowerDownTime (0);
                                    setPowerUpTime (0);
                                    setPowerUpDate (0);
                                }
                            }
                            else
                            {

                                intervalTime = RWTime (getCurrentLPDate() +
                                                       ((getCurrentLPInterval()+1) * localLP->configuration.intervalLength * 60));

                                validPointFound =
                                verifyAndAddPointToReturnMsg (validLPPointInfo[getCurrentLPChannel()].pointId,
                                                              calculateIntervalData (intervalPulses,
                                                                                     localLP,
                                                                                     validLPPointInfo[getCurrentLPChannel()]),
                                                              PartialIntervalQuality,
                                                              intervalTime,
                                                              pPIL,
                                                              TAG_POINT_LOAD_PROFILE_DATA);

                                /*****************************
                                * if we are on the last set of intervals
                                * save them in another message so
                                * they can be displayed on tdc
                                ******************************
                                */
                                if (localLP->lastLPMessage)
                                {
                                    verifyAndAddPointToReturnMsg (validLPPointInfo[getCurrentLPChannel()].pointId,
                                                                  calculateIntervalData (intervalPulses,
                                                                                         localLP,
                                                                                         validLPPointInfo[getCurrentLPChannel()]),
                                                                  PartialIntervalQuality,
                                                                  intervalTime,
                                                                  pLastLPIntervals);
                                }
                            }
                        }
                        else
                        {
                            // we are one today, check for repeated data
                            if ((RWTime(0,0).seconds()) == getCurrentLPDate())
                            {
                                if (expectedLastInterval < getCurrentLPInterval())
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " (" << __LINE__ << ") *** ERROR *** Repeated data possible for " << getName() << endl;
                                    }

                                }
                                else
                                {
                                    // data is good, put it in the point and send it
                                    // if our offset if valid, add the point
                                    intervalTime = RWTime (getCurrentLPDate() +
                                                           ((getCurrentLPInterval()+1) * localLP->configuration.intervalLength * 60));

                                    validPointFound =
                                    verifyAndAddPointToReturnMsg (validLPPointInfo[getCurrentLPChannel()].pointId,
                                                                  calculateIntervalData (intervalPulses,
                                                                                         localLP,
                                                                                         validLPPointInfo[getCurrentLPChannel()]),
                                                                  NormalQuality,
                                                                  intervalTime,
                                                                  pPIL,
                                                                  TAG_POINT_LOAD_PROFILE_DATA);

                                    /*****************************
                                    * if we are on the last set of intervals
                                    * save them in another message so
                                    * they can be displayed on tdc
                                    ******************************
                                    */
                                    if (localLP->lastLPMessage)
                                    {
                                        verifyAndAddPointToReturnMsg (validLPPointInfo[getCurrentLPChannel()].pointId,
                                                                      calculateIntervalData (intervalPulses,
                                                                                             localLP,
                                                                                             validLPPointInfo[getCurrentLPChannel()]),
                                                                      NormalQuality,
                                                                      intervalTime,
                                                                      pLastLPIntervals);
                                    }
                                }
                            }
                            else
                            {
                                // data is good, put it in the point and send it
                                intervalTime = RWTime (getCurrentLPDate() +
                                                       ((getCurrentLPInterval()+1) * localLP->configuration.intervalLength * 60));
                                // if our offset if valid, add the point
                                validPointFound =
                                verifyAndAddPointToReturnMsg (validLPPointInfo[getCurrentLPChannel()].pointId,
                                                              calculateIntervalData (intervalPulses,
                                                                                     localLP,
                                                                                     validLPPointInfo[getCurrentLPChannel()]),
                                                              NormalQuality,
                                                              intervalTime,
                                                              pPIL,
                                                              TAG_POINT_LOAD_PROFILE_DATA);

                                /*****************************
                                * if we are on the last set of intervals
                                * save them in another message so
                                * they can be displayed on tdc
                                ******************************
                                */
                                if (localLP->lastLPMessage)
                                {
                                    verifyAndAddPointToReturnMsg (validLPPointInfo[getCurrentLPChannel()].pointId,
                                                                  calculateIntervalData (intervalPulses,
                                                                                         localLP,
                                                                                         validLPPointInfo[getCurrentLPChannel()]),
                                                                  NormalQuality,
                                                                  intervalTime,
                                                                  pLastLPIntervals);
                                }
                            }
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Parity calculation failed interval "<<  getCurrentLPInterval() << " channel " << getCurrentLPChannel() << " for " << getName() << endl;
                        }
                    }

                    /********************
                    *  if one channel set this to true
                    *  we want to update the lastlptime
                    *
                    *  if not, update it with whatever the current validPoint flag is
                    *********************
                    */
                    if (!isLPTimeUpdateFlag())
                    {
                        setLPTimeUpdateFlag (validPointFound);
                    }
                    setCurrentLPChannel (getCurrentLPChannel() + 1);

                    if (getCurrentLPChannel() >= localLP->configuration.numberOfChannels)
                    {
                        // don't update if we haven't sent a valid point across
                        if (isLPTimeUpdateFlag())
                        {
                            /*************************
                            * data intervals are zero based, the time must
                            * be calculated being 1 based
                            * so we must add on when calculating the time
                            **************************
                            */
                            setLastLPTime(getCurrentLPDate() +
                                          ((getCurrentLPInterval()+1) * localLP->configuration.intervalLength * 60));
                        }

                        // we're thru the channels, reset and move up an interval
                        setCurrentLPChannel (0);
                        setLPTimeUpdateFlag (FALSE);
                        setCurrentLPInterval (getCurrentLPInterval() + 1);
                    }
                }
                else
                {
                    // if we have a power down stamp, we have partial interval
                    if (0 != getPowerDownTime())
                    {
                        if (0 != getPowerUpTime())
                        {
                            /****************************
                            *
                            * calculation is zero based (ie 15 minute data, 96 intervals, 0-95)
                            *
                            *****************************
                            */
                            ULONG newInterval (getPowerUpTime() / (localLP->configuration.intervalLength * 60));

                            setCurrentLPInterval ((ULONG)newInterval);

                            /********************
                            * must go through all the channels before I reset these guys
                            *********************
                            */

                            if ((getCurrentLPChannel() + 1) >= localLP->configuration.numberOfChannels)
                            {
                                setPowerDownTime (0);
                                setPowerUpTime (0);
                                setPowerUpDate (0);
                            }
                        }
                    }

                    // same interval different channel, keep going
                    setCurrentLPChannel (getCurrentLPChannel() + 1);

                    // go thru all channels who have same time stamp
                    if (getCurrentLPChannel() >= localLP->configuration.numberOfChannels)
                    {
                        // we're thru the channels, reset and move up an interval
                        setCurrentLPChannel (0);
                        setCurrentLPInterval (getCurrentLPInterval() + 1);
                    }
                }
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

    /***************************
    *  this list of point changes will be sent without the load profile flag set
    *  allowing us to display the last interals in a report
    *  currently dispatch does not route load profile data
    ****************************
    */
    if (localLP->lastLPMessage)
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

// Routine to display or print the message
INT CtiDeviceLandisGyrS4::ResultDisplay (INMESS *InMessage)

{
    ULONG          i, j;
    DIALUPREPLY    *DUPRep = &InMessage->Buffer.DUPSt.DUPRep;

    LGS4ScanData_t  *ptr = (LGS4ScanData_t*) InMessage->Buffer.DUPSt.DUPRep.Message;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Result display for device " << getName() << " in progress " << endl;

        dout << "Meter ID          :   " << ptr->Real.deviceID << endl;
        dout << "Unit  SN          :   " << ptr->Real.serialNumber << endl;
        dout << "Time              :   " << (USHORT)ptr->Real.dateTime.hours << ":" << (USHORT)ptr->Real.dateTime.minutes << endl;
        dout << "Date              :   " << (USHORT)ptr->Real.dateTime.month << "/" << (USHORT)ptr->Real.dateTime.day <<
        "/" << (USHORT)ptr->Real.dateTime.year+2000 << endl << endl;

        dout << "Outages           :    " << ptr->Real.powerOutages << endl;
        dout << "Demand            :    " << ptr->Real.prevIntervalDemand  << endl;
        dout << "SelectableMetric  :    " << ptr->Real.prevIntervalSelectedMetric  << endl;
        dout << "ThirdMetric       :    " << ptr->Real.prevIntervalThirdMetric  << endl << endl;

        dout << "Phase             Voltage            Current"  << endl;
        dout << "  A                " << ptr->Real.phaseAVoltage << "            " << ptr->Real.phaseACurrent  << endl;
        dout << "  B                " << ptr->Real.phaseBVoltage << "            " << ptr->Real.phaseBCurrent  << endl;
        dout << "  C                " << ptr->Real.phaseCVoltage << "            " << ptr->Real.phaseCCurrent  << endl;
        dout << "Neutral                               " << ptr->Real.neutralCurrent  << endl << endl;


        dout <<"Rate  Time    Date         MaxkW      CDem      PF       kWh"  << endl;
        dout <<"-----------------------------------------------------------------"  << endl;
        dout <<"  A  " << (USHORT)ptr->Real.dateTimeRateAMaxkW.hours << ":" << (USHORT)ptr->Real.dateTimeRateAMaxkW.minutes <<
        "   " << (USHORT)ptr->Real.dateTimeRateAMaxkW.month << "/" << (USHORT)ptr->Real.dateTimeRateAMaxkW.day << "/" <<
        (USHORT)ptr->Real.dateTimeRateAMaxkW.year+2000 << "     " << ptr->Real.rateAMaxkW << "     " <<
        ptr->Real.rateACoincidentDemand << "    " << ptr->Real.rateAPowerFactorAtMax << "     " <<
        ptr->Real.rateAkWh  << endl;

        dout <<"  B  " << (USHORT)ptr->Real.dateTimeRateBMaxkW.hours << ":" << (USHORT)ptr->Real.dateTimeRateBMaxkW.minutes <<
        "   " << (USHORT)ptr->Real.dateTimeRateBMaxkW.month << "/" << (USHORT)ptr->Real.dateTimeRateBMaxkW.day << "/" <<
        (USHORT)ptr->Real.dateTimeRateBMaxkW.year+2000 << "     " << ptr->Real.rateBMaxkW << "     " <<
        ptr->Real.rateBCoincidentDemand << "    " << ptr->Real.rateBPowerFactorAtMax << "     " <<
        ptr->Real.rateBkWh  << endl;

        dout <<"  C  " << (USHORT)ptr->Real.dateTimeRateCMaxkW.hours << ":" << (USHORT)ptr->Real.dateTimeRateCMaxkW.minutes <<
        "   " << (USHORT)ptr->Real.dateTimeRateCMaxkW.month << "/" << (USHORT)ptr->Real.dateTimeRateCMaxkW.day << "/" <<
        (USHORT)ptr->Real.dateTimeRateCMaxkW.year+2000 << "     " << ptr->Real.rateCMaxkW << "     " <<
        ptr->Real.rateCCoincidentDemand << "    " << ptr->Real.rateCPowerFactorAtMax << "     " <<
        ptr->Real.rateCkWh  << endl;

        dout <<"  D  " << (USHORT)ptr->Real.dateTimeRateDMaxkW.hours << ":" << (USHORT)ptr->Real.dateTimeRateDMaxkW.minutes <<
        "   " << (USHORT)ptr->Real.dateTimeRateDMaxkW.month << "/" << (USHORT)ptr->Real.dateTimeRateDMaxkW.day << "/" <<
        (USHORT)ptr->Real.dateTimeRateDMaxkW.year+2000 << "     " << ptr->Real.rateDMaxkW << "     " <<
        ptr->Real.rateDCoincidentDemand << "    " << ptr->Real.rateDPowerFactorAtMax << "     " <<
        ptr->Real.rateDkWh  << endl;

        dout <<"  E  " << (USHORT)ptr->Real.dateTimeRateEMaxkW.hours << ":" << (USHORT)ptr->Real.dateTimeRateEMaxkW.minutes <<
        "   " << (USHORT)ptr->Real.dateTimeRateEMaxkW.month << "/" << (USHORT)ptr->Real.dateTimeRateEMaxkW.day << "/" <<
        (USHORT)ptr->Real.dateTimeRateEMaxkW.year+2000 << "     " << ptr->Real.rateEMaxkW << "     " <<
        ptr->Real.rateECoincidentDemand << "    " << ptr->Real.rateEPowerFactorAtMax << "     " <<
        ptr->Real.rateEkWh  << endl;
        dout <<"-----------------------------------------------------------------"  << endl;
        dout <<"                                                         " << ptr->Real.totalkWh  << endl << endl;

        switch (ptr->Real.selectableMetric)
        {
            case LGS4_METRIC_KWH:
                dout << "Selectable Metric:  kwh" << endl;
                break;
            case LGS4_METRIC_KVARH:
                dout << "Selectable Metric:  kvarh" << endl;
                break;
            case LGS4_METRIC_RMSKVAH:
                dout << "Selectable Metric:  kvah" << endl;
                break;
            default:
                dout << "Selectable Metric:  " << ptr->Real.selectableMetric << endl;
        }
        dout <<"Rate   Time   Date        MaxkM      kMh"  << endl;
        dout <<"-----------------------------------------------------------------"  << endl;
        dout <<"  A  " << (USHORT)ptr->Real.dateTimeRateAMaxkM.hours << ":" << (USHORT)ptr->Real.dateTimeRateAMaxkM.minutes <<
        "   " << (USHORT)ptr->Real.dateTimeRateAMaxkM.month << "/" << (USHORT)ptr->Real.dateTimeRateAMaxkM.day << "/" <<
                (USHORT)ptr->Real.dateTimeRateAMaxkM.year+2000 << "     " << ptr->Real.rateAMaxkM << "     " <<
                ptr->Real.rateAkMh  << endl;

        dout <<"  B  " << (USHORT)ptr->Real.dateTimeRateBMaxkM.hours << ":" << (USHORT)ptr->Real.dateTimeRateBMaxkM.minutes <<
        "   " << (USHORT)ptr->Real.dateTimeRateBMaxkM.month << "/" << (USHORT)ptr->Real.dateTimeRateBMaxkM.day << "/" <<
                (USHORT)ptr->Real.dateTimeRateBMaxkM.year+2000 << "     " << ptr->Real.rateBMaxkM << "     " <<
                ptr->Real.rateBkMh  << endl;

        dout <<"  C  " << (USHORT)ptr->Real.dateTimeRateCMaxkM.hours << ":" << (USHORT)ptr->Real.dateTimeRateCMaxkM.minutes <<
                "   " << (USHORT)ptr->Real.dateTimeRateCMaxkM.month << "/" << (USHORT)ptr->Real.dateTimeRateCMaxkM.day << "/" <<
                (USHORT)ptr->Real.dateTimeRateCMaxkM.year+2000 << "     " << ptr->Real.rateCMaxkM << "     " <<
                ptr->Real.rateCkMh  << endl;

        dout <<"  D  " << (USHORT)ptr->Real.dateTimeRateDMaxkM.hours << ":" << (USHORT)ptr->Real.dateTimeRateDMaxkM.minutes <<
                "   " << (USHORT)ptr->Real.dateTimeRateDMaxkM.month << "/" << (USHORT)ptr->Real.dateTimeRateDMaxkM.day << "/" <<
                (USHORT)ptr->Real.dateTimeRateDMaxkM.year+2000 << "     " << ptr->Real.rateDMaxkM << "     " <<
                ptr->Real.rateDkMh  << endl;

        dout <<"  E  " << (USHORT)ptr->Real.dateTimeRateEMaxkM.hours << ":" << (USHORT)ptr->Real.dateTimeRateEMaxkM.minutes <<
                "   " << (USHORT)ptr->Real.dateTimeRateEMaxkM.month << "/" << (USHORT)ptr->Real.dateTimeRateEMaxkM.day << "/" <<
                (USHORT)ptr->Real.dateTimeRateEMaxkM.year+2000 << "     " << ptr->Real.rateEMaxkM << "     " <<
                ptr->Real.rateEkMh  << endl;


        dout <<"-----------------------------------------------------------------"  << endl;
        dout <<"                                    " << ptr->Real.totalkMh  << endl << endl;

        switch (ptr->Real.thirdMetric)
        {
            case LGS4_METRIC_KWH:
                dout << "Third Metric:  kwh" << endl;
                break;
            case LGS4_METRIC_KVARH:
                dout << "Third Metric:  kvarh" << endl;
                break;
            case LGS4_METRIC_RMSKVAH:
                dout << "Third Metric:  kvah" << endl;
                break;
            default:
                dout << "Third Metric:  " << ptr->Real.thirdMetric << endl;
        }

        dout <<"     Time     Date        MaxkM3     CDem       PF       kM3h"  << endl;
        dout <<"-----------------------------------------------------------------"  << endl;
        dout <<" M3  " << (USHORT)ptr->Real.dateTimeMaxkM3.hours << ":" << (USHORT)ptr->Real.dateTimeMaxkM3.minutes <<
        "   " << (USHORT)ptr->Real.dateTimeMaxkM3.month << "/" << (USHORT)ptr->Real.dateTimeMaxkM3.day << "/" <<
            (USHORT)ptr->Real.dateTimeMaxkM3.year+2000 << "     " << ptr->Real.maxkM3 << "     " <<
            ptr->Real.coincidentkM3atMaxDemand << "    " << ptr->Real.powerFactorAtMaxkM3 << "     " <<
            ptr->Real.totalkMh3  << endl;
    }


    return NORMAL;

}

LONG CtiDeviceLandisGyrS4::findLPDataPoint (LGS4LPPointInfo_t &point, USHORT aMetric)
{
    LONG retCode = NORMAL;
    CtiPointNumeric   *pNumericPoint = NULL;

    // always set the metric
    point.metric = aMetric;

    switch (aMetric)
    {
        case LGS4_METRIC_KWH:
            {
                // looking for demand point
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_KW, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }
        case LGS4_METRIC_KVARH:
            {
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_KVAR, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }
        case LGS4_METRIC_RMSKVAH:
            {
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_KVA, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }

        case LGS4_METRIC_VASQRH:
            {
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_PHASE_A_VOLTAGE, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }

        case LGS4_METRIC_VBSQRH:
            {
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_PHASE_B_VOLTAGE, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }

        case LGS4_METRIC_VCSQRH:
            {
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_PHASE_C_VOLTAGE, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }

        case LGS4_METRIC_IASQRH:
            {
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_PHASE_A_CURRENT, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }

        case LGS4_METRIC_IBSQRH:
            {
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_PHASE_B_CURRENT, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }

        case LGS4_METRIC_ICSQRH:
            {
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_PHASE_C_CURRENT, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }

                break;
            }
        case LGS4_METRIC_INSQRH:
            {
                if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_NEUTRAL_CURRENT, AnalogPointType)) != NULL)
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = !NORMAL;
                }
                break;
            }
        case LGS4_METRIC_NEGATIVEKWH:
        case LGS4_METRIC_RMSKVARH:
        case LGS4_METRIC_KQH:
        case LGS4_METRIC_LEAD_KVARH:
        case LGS4_METRIC_INPUT1:
        case LGS4_METRIC_INPUT2:
        case LGS4_METRIC_TD_LAG_KVAH:
        case LGS4_METRIC_TD_LEAD_KVAH:
        case LGS4_METRIC_VOLTAGEA_SAG:
        case LGS4_METRIC_VOLTAGEB_SAG:
        case LGS4_METRIC_VOLTAGEC_SAG:
        case LGS4_METRIC_VOLTAGEA_SWELL:
        case LGS4_METRIC_VOLTAGEB_SWELL:
        case LGS4_METRIC_VOLTAGEC_SWELL:
        case LGS4_METRIC_VOLTAGE_SAG:
        case LGS4_METRIC_VOLTAGE_SWELL:
        default:
            {
                point.pointId = 0;
                point.multiplier = 1.0;
                retCode = !NORMAL;
            }
    }
    return retCode;
}

DOUBLE CtiDeviceLandisGyrS4::calculateIntervalData (USHORT aInterval,
                                                    LGS4LoadProfile_t *aLPConfig,
                                                    LGS4LPPointInfo_t aLPPoint)
{
    DOUBLE constant;
    DOUBLE finalValue;

    switch (aLPPoint.metric)
    {
        case LGS4_METRIC_VASQRH:
        case LGS4_METRIC_VBSQRH:
        case LGS4_METRIC_VCSQRH:
            {
                if (aLPConfig->configuration.voltageCode & 0x01)
                    constant = 1.0;
                else if (aLPConfig->configuration.voltageCode & 0x02)
                    constant = 4.0;
                else if (aLPConfig->configuration.voltageCode & 0x04)
                    constant = 5.3284;
                else if (aLPConfig->configuration.voltageCode & 0x08)
                    constant = 16.0;
                else
                    constant = 1.0;

                finalValue =(DOUBLE) (sqrt((DOUBLE)aInterval * constant *
                                           (DOUBLE)(60.0 / aLPConfig->configuration.intervalLength)) * aLPPoint.multiplier);
                break;
            }
        case LGS4_METRIC_INSQRH:
        case LGS4_METRIC_IASQRH:
        case LGS4_METRIC_IBSQRH:
        case LGS4_METRIC_ICSQRH:
            {
                if (aLPConfig->configuration.voltageCode & 0x01)
                    constant = 0.0278;
                else if (aLPConfig->configuration.voltageCode & 0x02)
                    constant = 0.1736;
                else if (aLPConfig->configuration.voltageCode & 0x04)
                    constant = 0.1736;
                else
                    constant = 1.0;

                finalValue =(DOUBLE) (sqrt((DOUBLE)aInterval * constant *
                                           (DOUBLE)(60.0 / aLPConfig->configuration.intervalLength)) * aLPPoint.multiplier);
                break;
            }
        case LGS4_METRIC_KWH:
        case LGS4_METRIC_KVARH:
        case LGS4_METRIC_RMSKVAH:
            {
                finalValue =((DOUBLE)aInterval * aLPConfig->configuration.kFactor *
                             aLPPoint.multiplier *
                             (DOUBLE)(60.0 / aLPConfig->configuration.intervalLength)) / 1000.0;
                break;
            }
        case LGS4_METRIC_NEGATIVEKWH:
        case LGS4_METRIC_RMSKVARH:
        case LGS4_METRIC_KQH:
        case LGS4_METRIC_LEAD_KVARH:
        case LGS4_METRIC_INPUT1:
        case LGS4_METRIC_INPUT2:
        case LGS4_METRIC_TD_LAG_KVAH:
        case LGS4_METRIC_TD_LEAD_KVAH:
        case LGS4_METRIC_VOLTAGEA_SAG:
        case LGS4_METRIC_VOLTAGEB_SAG:
        case LGS4_METRIC_VOLTAGEC_SAG:
        case LGS4_METRIC_VOLTAGEA_SWELL:
        case LGS4_METRIC_VOLTAGEB_SWELL:
        case LGS4_METRIC_VOLTAGEC_SWELL:
        case LGS4_METRIC_VOLTAGE_SAG:
        case LGS4_METRIC_VOLTAGE_SWELL:
        default:
            break;
    }

    return finalValue;
}


BOOL CtiDeviceLandisGyrS4::getMeterDataFromScanStruct (int aOffset, DOUBLE &aValue, RWTime &peak, LGS4ScanData_t *aScanData)
{
    BOOL isValidPoint = FALSE;

    // this is initial value
    peak = rwEpoch;

    /* Get the value from InMessage */
    switch (aOffset)
    {
        case OFFSET_TOTAL_KWH:
            {
                aValue = aScanData->Real.totalkWh;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_PEAK_KW_OR_RATE_A_KW:
            {
                // check if we have a valid date
                if (aScanData->Real.dateTimeRateAMaxkW.month > 0 &&
                    aScanData->Real.dateTimeRateAMaxkW.month < 13)
                {
                    peak = RWTime (RWDate (aScanData->Real.dateTimeRateAMaxkW.day,
                                           aScanData->Real.dateTimeRateAMaxkW.month,
                                           aScanData->Real.dateTimeRateAMaxkW.year+2000),
                                   aScanData->Real.dateTimeRateAMaxkW.hours,
                                   aScanData->Real.dateTimeRateAMaxkW.minutes,
                                   0);
                    aValue = aScanData->Real.rateAMaxkW;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_A_KWH:
            {
                aValue = aScanData->Real.rateAkWh;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_RATE_B_KW:
            {
                // check if we have a valid date
                if (aScanData->Real.dateTimeRateBMaxkW.month > 0 &&
                    aScanData->Real.dateTimeRateBMaxkW.month < 13)
                {
                    peak = RWTime (RWDate (aScanData->Real.dateTimeRateBMaxkW.day,
                                           aScanData->Real.dateTimeRateBMaxkW.month,
                                           aScanData->Real.dateTimeRateBMaxkW.year+2000),
                                   aScanData->Real.dateTimeRateBMaxkW.hours,
                                   aScanData->Real.dateTimeRateBMaxkW.minutes,
                                   0);
                    aValue = aScanData->Real.rateBMaxkW;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_B_KWH:
            {
                aValue = aScanData->Real.rateBkWh;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_RATE_C_KW:
            {
                // check if we have a valid date
                if (aScanData->Real.dateTimeRateCMaxkW.month > 0 &&
                    aScanData->Real.dateTimeRateCMaxkW.month < 13)
                {
                    peak = RWTime (RWDate (aScanData->Real.dateTimeRateCMaxkW.day,
                                           aScanData->Real.dateTimeRateCMaxkW.month,
                                           aScanData->Real.dateTimeRateCMaxkW.year+2000),
                                   aScanData->Real.dateTimeRateCMaxkW.hours,
                                   aScanData->Real.dateTimeRateCMaxkW.minutes,
                                   0);

                    aValue = aScanData->Real.rateCMaxkW;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_C_KWH:
            {
                aValue = aScanData->Real.rateCkWh;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_RATE_D_KW:
            {
                // check if we have a valid date
                if (aScanData->Real.dateTimeRateDMaxkW.month > 0 &&
                    aScanData->Real.dateTimeRateDMaxkW.month < 13)
                {
                    peak = RWTime (RWDate (aScanData->Real.dateTimeRateDMaxkW.day,
                                           aScanData->Real.dateTimeRateDMaxkW.month,
                                           aScanData->Real.dateTimeRateDMaxkW.year+2000),
                                   aScanData->Real.dateTimeRateDMaxkW.hours,
                                   aScanData->Real.dateTimeRateDMaxkW.minutes,
                                   0);
                    aValue = aScanData->Real.rateDMaxkW;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_D_KWH:
            {
                aValue = aScanData->Real.rateDkWh;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_RATE_E_KW:
            {
                // check if we have a valid date
                if (aScanData->Real.dateTimeRateEMaxkW.month > 0 &&
                    aScanData->Real.dateTimeRateEMaxkW.month < 13)
                {
                    peak = RWTime (RWDate (aScanData->Real.dateTimeRateEMaxkW.day,
                                           aScanData->Real.dateTimeRateEMaxkW.month,
                                           aScanData->Real.dateTimeRateEMaxkW.year+2000),
                                   aScanData->Real.dateTimeRateEMaxkW.hours,
                                   aScanData->Real.dateTimeRateEMaxkW.minutes,
                                   0);
                    aValue = aScanData->Real.rateEMaxkW;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_E_KWH:
            {
                aValue = aScanData->Real.rateEkWh;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW:
            {
                aValue = aScanData->Real.prevIntervalDemand;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_TOTAL_KVARH:
            {
                // check if either selectable metric is kvar
                if (aScanData->Real.selectableMetric == LGS4_METRIC_KVARH)
                {
                    aValue = aScanData->Real.totalkMh;
                    isValidPoint = TRUE;
                }
                else if (aScanData->Real.thirdMetric == LGS4_METRIC_KVARH)
                {
                    aValue = aScanData->Real.totalkMh3;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_PEAK_KVAR_OR_RATE_A_KVAR:
            {
                // check if either selectable metric is kvar
                if (aScanData->Real.selectableMetric == LGS4_METRIC_KVARH)
                {
                    if (aScanData->Real.dateTimeRateAMaxkM.month > 0 &&
                        aScanData->Real.dateTimeRateAMaxkM.month < 13)
                    {
                        peak = RWTime (RWDate (aScanData->Real.dateTimeRateAMaxkM.day,
                                               aScanData->Real.dateTimeRateAMaxkM.month,
                                               aScanData->Real.dateTimeRateAMaxkM.year+2000),
                                       aScanData->Real.dateTimeRateAMaxkM.hours,
                                       aScanData->Real.dateTimeRateAMaxkM.minutes,
                                       0);
                        aValue = aScanData->Real.rateAMaxkM;
                        isValidPoint = TRUE;
                    }
                }
                else if (aScanData->Real.thirdMetric == LGS4_METRIC_KVARH)
                {
                    // make sure we have a valid date
                    if (aScanData->Real.dateTimeMaxkM3.month > 0 &&
                        aScanData->Real.dateTimeMaxkM3.month < 13)
                    {
                        peak = RWTime (RWDate (aScanData->Real.dateTimeMaxkM3.day,
                                               aScanData->Real.dateTimeMaxkM3.month,
                                               aScanData->Real.dateTimeMaxkM3.year+2000),
                                       aScanData->Real.dateTimeMaxkM3.hours,
                                       aScanData->Real.dateTimeMaxkM3.minutes,
                                       0);
                        aValue = aScanData->Real.maxkM3;
                        isValidPoint = TRUE;
                    }
                }
                break;
            }
        case OFFSET_RATE_A_KVARH:
            {
                // check if either selectable metric is kvar
                if (aScanData->Real.selectableMetric == LGS4_METRIC_KVARH)
                {
                    aValue = aScanData->Real.rateAkMh;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_B_KVAR:
            {
                // check if either selectable metric is kvar
                if (aScanData->Real.selectableMetric == LGS4_METRIC_KVARH)
                {
                    if (aScanData->Real.dateTimeRateBMaxkM.month > 0 &&
                        aScanData->Real.dateTimeRateBMaxkM.month < 13)
                    {
                        peak = RWTime (RWDate (aScanData->Real.dateTimeRateBMaxkM.day,
                                               aScanData->Real.dateTimeRateBMaxkM.month,
                                               aScanData->Real.dateTimeRateBMaxkM.year+2000),
                                       aScanData->Real.dateTimeRateBMaxkM.hours,
                                       aScanData->Real.dateTimeRateBMaxkM.minutes,
                                       0);
                        aValue = aScanData->Real.rateBMaxkM;
                        isValidPoint = TRUE;
                    }
                }
                break;
            }
        case OFFSET_RATE_B_KVARH:
            {
                // check if either selectable metric is kvar
                if (aScanData->Real.selectableMetric == LGS4_METRIC_KVARH)
                {
                    aValue = aScanData->Real.rateBkMh;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_C_KVAR:
            {
                // check if either selectable metric is kvar
                if (aScanData->Real.selectableMetric == LGS4_METRIC_KVARH)
                {
                    if (aScanData->Real.dateTimeRateCMaxkM.month > 0 &&
                        aScanData->Real.dateTimeRateCMaxkM.month < 13)
                    {
                        peak = RWTime (RWDate (aScanData->Real.dateTimeRateCMaxkM.day,
                                               aScanData->Real.dateTimeRateCMaxkM.month,
                                               aScanData->Real.dateTimeRateCMaxkM.year+2000),
                                       aScanData->Real.dateTimeRateCMaxkM.hours,
                                       aScanData->Real.dateTimeRateCMaxkM.minutes,
                                       0);
                        aValue = aScanData->Real.rateCMaxkM;
                        isValidPoint = TRUE;
                    }
                }
                break;
            }
        case OFFSET_RATE_C_KVARH:
            {
                // check if either selectable metric is kvar
                if (aScanData->Real.selectableMetric == LGS4_METRIC_KVARH)
                {
                    aValue = aScanData->Real.rateCkMh;
                    isValidPoint = TRUE;
                }
                break;
            }

        case OFFSET_RATE_D_KVAR:
            {
                // check if either selectable metric is kvar
                if (aScanData->Real.selectableMetric == LGS4_METRIC_KVARH)
                {
                    if (aScanData->Real.dateTimeRateDMaxkM.month > 0 &&
                        aScanData->Real.dateTimeRateDMaxkM.month < 13)
                    {
                        peak = RWTime (RWDate (aScanData->Real.dateTimeRateDMaxkM.day,
                                               aScanData->Real.dateTimeRateDMaxkM.month,
                                               aScanData->Real.dateTimeRateDMaxkM.year+2000),
                                       aScanData->Real.dateTimeRateDMaxkM.hours,
                                       aScanData->Real.dateTimeRateDMaxkM.minutes,
                                       0);
                        aValue = aScanData->Real.rateDMaxkM;
                        isValidPoint = TRUE;
                    }
                }
                break;
            }
        case OFFSET_RATE_D_KVARH:
            {
                // check if either selectable metric is kvar
                if (aScanData->Real.selectableMetric == LGS4_METRIC_KVARH)
                {
                    aValue = aScanData->Real.rateDkMh;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_E_KVAR:
            {
                // check if either selectable metric is kvar
                if (aScanData->Real.selectableMetric == LGS4_METRIC_KVARH)
                {
                    if (aScanData->Real.dateTimeRateEMaxkM.month > 0 &&
                        aScanData->Real.dateTimeRateEMaxkM.month < 13)
                    {
                        peak = RWTime (RWDate (aScanData->Real.dateTimeRateEMaxkM.day,
                                               aScanData->Real.dateTimeRateEMaxkM.month,
                                               aScanData->Real.dateTimeRateEMaxkM.year+2000),
                                       aScanData->Real.dateTimeRateEMaxkM.hours,
                                       aScanData->Real.dateTimeRateEMaxkM.minutes,
                                       0);
                        aValue = aScanData->Real.rateEMaxkM;
                        isValidPoint = TRUE;
                    }
                }
                break;
            }
        case OFFSET_RATE_E_KVARH:
            {
                // check if either selectable metric is kvar
                if (aScanData->Real.selectableMetric == LGS4_METRIC_KVARH)
                {
                    aValue = aScanData->Real.rateEkMh;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR:
            {
                // check if either selectable metric is kvar
                if (aScanData->Real.selectableMetric == LGS4_METRIC_KVARH)
                {
                    aValue = aScanData->Real.prevIntervalSelectedMetric;
                    isValidPoint = TRUE;
                }
                else if (aScanData->Real.thirdMetric == LGS4_METRIC_KVARH)
                {
                    aValue = aScanData->Real.prevIntervalThirdMetric;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_TOTAL_KVAH:
            {
                if (aScanData->Real.selectableMetric == LGS4_METRIC_RMSKVAH ||
                    aScanData->Real.selectableMetric == LGS4_METRIC_TD_LAG_KVAH)
                {
                    aValue = aScanData->Real.totalkMh;
                    isValidPoint = TRUE;
                }
                else if (aScanData->Real.thirdMetric == LGS4_METRIC_RMSKVAH ||
                         aScanData->Real.selectableMetric == LGS4_METRIC_TD_LAG_KVAH)
                {
                    aValue = aScanData->Real.totalkMh3;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_PEAK_KVA_OR_RATE_A_KVA:
            {
                if (aScanData->Real.selectableMetric == LGS4_METRIC_RMSKVAH ||
                    aScanData->Real.selectableMetric == LGS4_METRIC_TD_LAG_KVAH)
                {
                    if (aScanData->Real.dateTimeRateAMaxkM.month > 0 &&
                        aScanData->Real.dateTimeRateAMaxkM.month < 13)
                    {
                        peak = RWTime (RWDate (aScanData->Real.dateTimeRateAMaxkM.day,
                                               aScanData->Real.dateTimeRateAMaxkM.month,
                                               aScanData->Real.dateTimeRateAMaxkM.year+2000),
                                       aScanData->Real.dateTimeRateAMaxkM.hours,
                                       aScanData->Real.dateTimeRateAMaxkM.minutes,
                                       0);
                        aValue = aScanData->Real.rateAMaxkM;
                        isValidPoint = TRUE;
                    }
                }
                else if (aScanData->Real.thirdMetric == LGS4_METRIC_RMSKVAH ||
                         aScanData->Real.selectableMetric == LGS4_METRIC_TD_LAG_KVAH)
                {
                    // make sure we have a valid date
                    if (aScanData->Real.dateTimeMaxkM3.month > 0 &&
                        aScanData->Real.dateTimeMaxkM3.month < 13)
                    {
                        peak = RWTime (RWDate (aScanData->Real.dateTimeMaxkM3.day,
                                               aScanData->Real.dateTimeMaxkM3.month,
                                               aScanData->Real.dateTimeMaxkM3.year+2000),
                                       aScanData->Real.dateTimeMaxkM3.hours,
                                       aScanData->Real.dateTimeMaxkM3.minutes,
                                       0);
                        aValue = aScanData->Real.maxkM3;
                        isValidPoint = TRUE;
                    }
                }
                break;
            }
        case OFFSET_RATE_A_KVAH:
            {
                if (aScanData->Real.selectableMetric == LGS4_METRIC_RMSKVAH ||
                    aScanData->Real.selectableMetric == LGS4_METRIC_TD_LAG_KVAH)
                {
                    aValue = aScanData->Real.rateAkMh;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_B_KVA:
            {
                if (aScanData->Real.selectableMetric == LGS4_METRIC_RMSKVAH ||
                    aScanData->Real.selectableMetric == LGS4_METRIC_TD_LAG_KVAH)
                {
                    if (aScanData->Real.dateTimeRateBMaxkM.month >  0 &&
                        aScanData->Real.dateTimeRateBMaxkM.month < 13)
                    {
                        peak = RWTime (RWDate (aScanData->Real.dateTimeRateBMaxkM.day,
                                               aScanData->Real.dateTimeRateBMaxkM.month,
                                               aScanData->Real.dateTimeRateBMaxkM.year+2000),
                                       aScanData->Real.dateTimeRateBMaxkM.hours,
                                       aScanData->Real.dateTimeRateBMaxkM.minutes,
                                       0);
                        aValue = aScanData->Real.rateBMaxkM;
                        isValidPoint = TRUE;
                    }
                }
                break;
            }
        case OFFSET_RATE_B_KVAH:
            {
                if (aScanData->Real.selectableMetric == LGS4_METRIC_RMSKVAH ||
                    aScanData->Real.selectableMetric == LGS4_METRIC_TD_LAG_KVAH)
                {
                    aValue = aScanData->Real.rateBkMh;
                    isValidPoint = TRUE;
                }
                break;
            }

        case OFFSET_RATE_C_KVA:
            {
                if (aScanData->Real.selectableMetric == LGS4_METRIC_RMSKVAH ||
                    aScanData->Real.selectableMetric == LGS4_METRIC_TD_LAG_KVAH)
                {
                    if (aScanData->Real.dateTimeRateCMaxkM.month > 0 &&
                        aScanData->Real.dateTimeRateCMaxkM.month < 13)
                    {
                        peak = RWTime (RWDate (aScanData->Real.dateTimeRateCMaxkM.day,
                                               aScanData->Real.dateTimeRateCMaxkM.month,
                                               aScanData->Real.dateTimeRateCMaxkM.year+2000),
                                       aScanData->Real.dateTimeRateCMaxkM.hours,
                                       aScanData->Real.dateTimeRateCMaxkM.minutes,
                                       0);
                        aValue = aScanData->Real.rateCMaxkM;
                        isValidPoint = TRUE;
                    }
                }
                break;
            }
        case OFFSET_RATE_C_KVAH:
            {
                if (aScanData->Real.selectableMetric == LGS4_METRIC_RMSKVAH ||
                    aScanData->Real.selectableMetric == LGS4_METRIC_TD_LAG_KVAH)
                {
                    aValue = aScanData->Real.rateCkMh;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_D_KVA:
            {
                if (aScanData->Real.selectableMetric == LGS4_METRIC_RMSKVAH ||
                    aScanData->Real.selectableMetric == LGS4_METRIC_TD_LAG_KVAH)
                {
                    if (aScanData->Real.dateTimeRateDMaxkM.month > 0 &&
                        aScanData->Real.dateTimeRateDMaxkM.month < 13)
                    {
                        peak = RWTime (RWDate (aScanData->Real.dateTimeRateDMaxkM.day,
                                               aScanData->Real.dateTimeRateDMaxkM.month,
                                               aScanData->Real.dateTimeRateDMaxkM.year+2000),
                                       aScanData->Real.dateTimeRateDMaxkM.hours,
                                       aScanData->Real.dateTimeRateDMaxkM.minutes,
                                       0);
                        aValue = aScanData->Real.rateDMaxkM;
                        isValidPoint = TRUE;
                    }
                }
                break;
            }
        case OFFSET_RATE_D_KVAH:
            {
                if (aScanData->Real.selectableMetric == LGS4_METRIC_RMSKVAH ||
                    aScanData->Real.selectableMetric == LGS4_METRIC_TD_LAG_KVAH)
                {
                    aValue = aScanData->Real.rateDkMh;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_RATE_E_KVA:
            {
                if (aScanData->Real.selectableMetric == LGS4_METRIC_RMSKVAH ||
                    aScanData->Real.selectableMetric == LGS4_METRIC_TD_LAG_KVAH)
                {
                    if (aScanData->Real.dateTimeRateEMaxkM.month > 0 &&
                        aScanData->Real.dateTimeRateEMaxkM.month < 13)
                    {
                        peak = RWTime (RWDate (aScanData->Real.dateTimeRateEMaxkM.day,
                                               aScanData->Real.dateTimeRateEMaxkM.month,
                                               aScanData->Real.dateTimeRateEMaxkM.year+2000),
                                       aScanData->Real.dateTimeRateEMaxkM.hours,
                                       aScanData->Real.dateTimeRateEMaxkM.minutes,
                                       0);
                        aValue = aScanData->Real.rateEMaxkM;
                        isValidPoint = TRUE;
                    }
                }
                break;
            }
        case OFFSET_RATE_E_KVAH:
            {
                if (aScanData->Real.selectableMetric == LGS4_METRIC_RMSKVAH ||
                    aScanData->Real.selectableMetric == LGS4_METRIC_TD_LAG_KVAH)
                {
                    aValue = aScanData->Real.rateEkMh;
                    isValidPoint = TRUE;
                }
                break;
            }

        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA:
            {
                if (aScanData->Real.selectableMetric == LGS4_METRIC_RMSKVAH ||
                    aScanData->Real.selectableMetric == LGS4_METRIC_TD_LAG_KVAH)
                {
                    aValue = aScanData->Real.prevIntervalSelectedMetric;
                    isValidPoint = TRUE;
                }
                else if (aScanData->Real.thirdMetric == LGS4_METRIC_RMSKVAH ||
                         aScanData->Real.selectableMetric == LGS4_METRIC_TD_LAG_KVAH)
                {
                    aValue = aScanData->Real.prevIntervalThirdMetric;
                    isValidPoint = TRUE;
                }
                break;
            }
        case OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE:
            {
                aValue = aScanData->Real.phaseAVoltage;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE:
            {
                aValue = aScanData->Real.phaseBVoltage;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE:
            {
                aValue = aScanData->Real.phaseCVoltage;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_INSTANTANEOUS_PHASE_A_CURRENT:
            {
                aValue = aScanData->Real.phaseACurrent;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_INSTANTANEOUS_PHASE_B_CURRENT:
            {
                aValue = aScanData->Real.phaseBCurrent;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_INSTANTANEOUS_PHASE_C_CURRENT:
            {
                aValue = aScanData->Real.phaseCCurrent;
                isValidPoint = TRUE;
                break;
            }
        case OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT:
            {
                aValue = aScanData->Real.neutralCurrent;
                isValidPoint = TRUE;
                break;
            }

        default:
            isValidPoint = FALSE;
            break;
    }
    return isValidPoint;
}


void CtiDeviceLandisGyrS4::syncAppropriateTime (ULONG seconds)
{
    /****************************
    * here we decide which timestamp we came across
    * in the data stream.
    *
    * power outages and date changes are both possibilities
    *****************************
    */
    // first of the pair
    if (getPowerDownTime() == 0)
    {
        setPowerDownTime(seconds);
    }
    else if (getPowerUpTime() == 0)
    {
        setPowerUpTime(seconds);
    }
    else
    {
        // we must be another pair of timestamps
        setPowerDownTime(seconds);
        setPowerUpTime(0);
    }
}


BOOL CtiDeviceLandisGyrS4::verifyAndAddPointToReturnMsg (LONG   aPointId,
                                                         DOUBLE aValue,
                                                         USHORT aQuality,
                                                         RWTime aTime,
                                                         CtiReturnMsg *aReturnMsg,
                                                         USHORT aIntervalType,
                                                         RWCString aValReport)

{
    BOOL validPointFound = FALSE;
    CtiPointDataMsg   *pData    = NULL;

    // if our offset if valid, add the point
    if (aPointId)
    {
        //create a CTIDBG_new message
        pData = CTIDBG_new CtiPointDataMsg(aPointId,
                                    aValue,
                                    aQuality,
                                    AnalogPointType,
                                    aValReport);

        if (pData != NULL)
        {
            // set to load profile if needed
            if (aIntervalType == TAG_POINT_LOAD_PROFILE_DATA)
            {
                pData->setTags(TAG_POINT_LOAD_PROFILE_DATA);
            }

            if (aTime != rwEpoch)
            {
                //
                //  hack fix for non-DST compliant meters - someday to be absorbed by a big, global timekeeper
                //
                if( _dstFlag && aTime.isDST( ) )
                {
                    aTime += 60 * 60;
                }

                pData->setTime (aTime);
            }

            // aData is deleted in this function
            validPointFound = insertPointIntoReturnMsg (pData, aReturnMsg);
        }
    }
    return validPointFound;
}


BOOL CtiDeviceLandisGyrS4::insertPointIntoReturnMsg (CtiMessage   *aDataPoint,
                                                     CtiReturnMsg   *aReturnMsg)
{
    BOOL retCode = TRUE;

    if (aReturnMsg != NULL)
    {
        aReturnMsg->PointData().insert(aDataPoint);
        aDataPoint = NULL;  // We just put it on the list...
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "ERROR: " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        delete aDataPoint;
        aDataPoint = NULL;
        retCode = FALSE;
    }
    return retCode;
}


