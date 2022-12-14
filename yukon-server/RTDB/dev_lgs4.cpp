#include "precompiled.h"
#include <math.h>

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

#include "logger.h"
#include "guard.h"
#include "utility.h"
#include "ctidate.h"
#include "ctitime.h"

using std::string;
using std::endl;
using std::list;


void reverseCharacters (UCHAR *source, PCHAR dest);
DOUBLE S4BCDtoDouble(const UCHAR* buffer, ULONG len);
ULONG S4BCDtoBase10(const UCHAR* buffer, ULONG len);
FLOAT S4Float (const UCHAR byteArray[3]);
USHORT S4UShort (const UCHAR byteArray[2]);
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



YukonError_t CtiDeviceLandisGyrS4::generateCommandHandshake (CtiXfer  &Transfer, CtiMessageList &traceList)
{
    YukonError_t retCode = ClientErrors::None;


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
                CTILOG_ERROR(dout, "Invalid state "<< getCurrentState() <<" scanning "<< getName());

                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState (StateHandshakeAbort);
                retCode = ClientErrors::Abnormal;
            }
    }
    return retCode;
}

YukonError_t CtiDeviceLandisGyrS4::generateCommand (CtiXfer  &Transfer, CtiMessageList &traceList )
{
    YukonError_t retCode = ClientErrors::None;

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
                CTILOG_ERROR(dout, "Invalid command "<< getCurrentCommand() <<" scanning "<< getName());

                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState (StateScanAbort);
                retCode = ClientErrors::Abnormal;
                break;
            }

    }
    return retCode;
}


YukonError_t CtiDeviceLandisGyrS4::generateCommandScan (CtiXfer  &Transfer, CtiMessageList &traceList)
{
    YukonError_t retCode = ClientErrors::None;
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
                Transfer.setCRCFlag( CtiXfer::XFER_ADD_CRC );
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
                Transfer.setCRCFlag( CtiXfer::XFER_VERIFY_CRC );
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
                Transfer.setCRCFlag( CtiXfer::XFER_VERIFY_CRC );
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
            CTILOG_ERROR(dout, "Invalid state "<< getCurrentState() <<" scanning "<< getName());

            Transfer.setOutCount( 0 );
            Transfer.setInCountExpected( 0 );
            setCurrentState (StateScanAbort);
            retCode = ClientErrors::Abnormal;
    }

    return retCode;
}


YukonError_t CtiDeviceLandisGyrS4::generateCommandLoadProfile (CtiXfer  &Transfer, CtiMessageList &traceList)
{
    YukonError_t retCode = ClientErrors::None;
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
                Transfer.setCRCFlag( CtiXfer::XFER_ADD_CRC );
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
                Transfer.setCRCFlag( CtiXfer::XFER_VERIFY_CRC );
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
                Transfer.setCRCFlag( CtiXfer::XFER_VERIFY_CRC );
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
            CTILOG_ERROR(dout, "Invalid state "<< getCurrentState() <<" scanning "<< getName());

            Transfer.setOutCount( 0 );
            Transfer.setInCountExpected( 0 );
            setCurrentState (StateScanAbort);
            retCode = ClientErrors::Abnormal;
    }

    return retCode;
}




YukonError_t CtiDeviceLandisGyrS4::decodeResponseHandshake (CtiXfer  &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList)
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
                CTILOG_ERROR(dout, "Invalid state "<< getCurrentState() <<" scanning "<< getName());

                setCurrentState (StateHandshakeAbort);
                break;
            }
    }
    return ClientErrors::None;
}



YukonError_t CtiDeviceLandisGyrS4::decodeResponse (CtiXfer  &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList)
{
    YukonError_t retCode = ClientErrors::None;

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
                CTILOG_ERROR(dout, "Invalid command "<< getCurrentCommand() <<" scanning "<< getName());

                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState (StateScanAbort);
                retCode = ClientErrors::Abnormal;
                break;
            }
    }
    return retCode;
}


YukonError_t CtiDeviceLandisGyrS4::decodeResponseScan (CtiXfer  &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList)
{
    YukonError_t  retCode    = ClientErrors::None;
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
                    if (Transfer.doTrace(ClientErrors::BadState))
                    {
                        CTILOG_ERROR(dout, "NAK: (no 0x55) attempting to scan "<< getName());
                    }

                    setAttemptsRemaining(getAttemptsRemaining()-1);

                    if (getAttemptsRemaining() <= 0)
                    {
                        setCurrentState (StateScanAbort);
                        retCode = ClientErrors::Abnormal;
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
                    if (Transfer.doTrace(ClientErrors::BadState))
                    {
                        CTILOG_ERROR(dout, "NAK: (no 0xAA) attempting to scan " << getName());
                    }

                    setAttemptsRemaining(getAttemptsRemaining()-1);

                    if (getAttemptsRemaining() <= 0)
                    {
                        setCurrentState (StateScanAbort);
                        retCode = ClientErrors::Abnormal;
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
                            retCode = ClientErrors::Abnormal;
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
                        retCode = ClientErrors::Abnormal;
                    }
                    else
                    {
                        // comm return failed, send command again if attempts remaining
                        setAttemptsRemaining(getAttemptsRemaining()-1);

                        if (getAttemptsRemaining() <= 0)
                        {
                            setCurrentState (StateScanAbort);
                            retCode = ClientErrors::Abnormal;
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
                if (commReturnValue == ClientErrors::BadCrc)
                {
                    // comm return failed, send command again if attempts remaining
                    setAttemptsRemaining(getAttemptsRemaining()-1);

                    if (getAttemptsRemaining() <= 0)
                    {
                        setCurrentState (StateScanAbort);
                        retCode = ClientErrors::Abnormal;
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
                        retCode = ClientErrors::Abnormal;
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
                CTILOG_ERROR(dout, "Invalid state "<< getCurrentState() <<" scanning "<< getName());

                setCurrentState (StateScanAbort);
                retCode = ClientErrors::Abnormal;
            }
    }

    return retCode;
}

YukonError_t CtiDeviceLandisGyrS4::decodeResponseLoadProfile (CtiXfer  &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList)
{
    YukonError_t retCode    = ClientErrors::None;
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
                    if (Transfer.doTrace(ClientErrors::BadState))
                    {
                        CTILOG_ERROR(dout, "NAK: (no 0x55) attempting to scan");
                    }

                    setAttemptsRemaining(getAttemptsRemaining()-1);

                    if (getAttemptsRemaining() <= 0)
                    {
                        setCurrentState (StateScanAbort);
                        retCode = ClientErrors::Abnormal;
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
                    if (Transfer.doTrace(ClientErrors::BadState))
                    {
                        CTILOG_ERROR(dout, "NAK: (no 0xAA) attempting to scan " << getName());
                    }

                    setAttemptsRemaining(getAttemptsRemaining()-1);

                    if (getAttemptsRemaining() <= 0)
                    {
                        setCurrentState (StateScanAbort);
                        retCode = ClientErrors::Abnormal;
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
                            retCode = ClientErrors::Abnormal;
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
                        retCode = ClientErrors::Abnormal;
                    }
                    else
                    {
                        // comm return failed, send command again if attempts remaining
                        setAttemptsRemaining(getAttemptsRemaining()-1);

                        if (getAttemptsRemaining() <= 0)
                        {
                            setCurrentState (StateScanAbort);
                            retCode = ClientErrors::Abnormal;
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
                if (commReturnValue == ClientErrors::BadCrc)
                {
                    // comm return failed, send command again if attempts remaining
                    setAttemptsRemaining(getAttemptsRemaining()-1);

                    if (getAttemptsRemaining() <= 0)
                    {
                        setCurrentState (StateScanAbort);
                        retCode = ClientErrors::Abnormal;
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
                        retCode = ClientErrors::Abnormal;
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
                                        CTILOG_ERROR(dout, "Meter "<< getName() <<" has no load profile channels configured");

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
                                        CtiTime midnightOnLastLP (CtiDate(CtiDate(CtiTime(localLP->porterLPTime)).day(), CtiDate(CtiTime(localLP->porterLPTime)).year()));
                                        ULONG difference = ((((CtiTime::now().seconds()) - (midnightOnLastLP.seconds())) / 86400) + 1);

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
                                    CTILOG_ERROR(dout, "Load profile for "<< getName() <<" will not be collected with this scan");

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
                                    Cti::FormattedList itemlist;
                                    itemlist <<"----- S3 Calibration Constants -----";

                                    {
                                        Cti::FormattedTable table;
                                        table.setCell(0,1) <<"Current";
                                        table.setCell(0,2) <<"Voltage";

                                        table.setCell(1,0) <<"Phase A";
                                        table.setCell(1,1) << localLPConfig->phaseA.currentConstant;
                                        table.setCell(1,2) << localLPConfig->phaseA.voltageConstant;

                                        table.setCell(2,0) <<"Phase B";
                                        table.setCell(2,1) << localLPConfig->phaseB.currentConstant;
                                        table.setCell(2,2) << localLPConfig->phaseB.voltageConstant;

                                        table.setCell(3,0) <<"Phase C";
                                        table.setCell(3,1) << localLPConfig->phaseC.currentConstant;
                                        table.setCell(3,2) << localLPConfig->phaseC.voltageConstant;

                                        itemlist << table;
                                    }

                                    itemlist.add("Class Code")   << localLPConfig->classCode;
                                    itemlist.add("Voltage Code") << localLPConfig->voltageCode;

                                    CTILOG_DEBUG(dout, itemlist);
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
                                    setPreviousState (StateScanValueSet6);
                                }
                                else
                                {

                                    setPreviousState (StateScanValueSet7);
                                }
                                // always returning the load profile
                                setCurrentState (StateScanReturnLoadProfile);

                                break;
                            }
                        default:
                            {
                                CTILOG_ERROR(dout, "Unknown command sent to "<< getName());
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
                CTILOG_ERROR(dout, "Invalid state "<< getCurrentState() <<" scanning "<< getName());
            }
            setCurrentState (StateScanAbort);
            retCode = ClientErrors::Abnormal;
    }
    return retCode;
}

YukonError_t CtiDeviceLandisGyrS4::GeneralScan(CtiRequestMsg     *pReq,
                                               CtiCommandParser  &parse,
                                               OUTMESS          *&OutMessage,
                                               CtiMessageList    &vgList,
                                               CtiMessageList    &retList,
                                               OutMessageList    &outList,
                                               INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;

    CTILOG_INFO(dout, "General Scan of device "<< getName() <<" in progress");

    ULONG BytesWritten;

    // load profile information
    time_t         RequestedTime = 0L;
    time_t         DeltaTime;
    time_t         NowTime;

    if (OutMessage != NULL)
    {
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
        populateRemoteOutMessage(*OutMessage);
        OutMessage->Retry = 3;  //  override

        EstablishOutMessagePriority( OutMessage, ScanPriority );
        if (!isMaster())
        {
            OverrideOutMessagePriority( OutMessage, OutMessage->Priority - 1 );
        }

        outList.push_back(OutMessage);
        OutMessage = NULL;
    }
    else
    {
        status = ClientErrors::MemoryAccess;
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
    return ClientErrors::None;
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
    return ClientErrors::None;
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
DOUBLE S4BCDtoDouble(const UCHAR* buffer, ULONG len)
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
ULONG S4BCDtoBase10(const UCHAR* buffer, ULONG len)
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

FLOAT S4Float (const UCHAR byteArray[3])
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
            tmp += (1.0 / pow (2.0,i));
    }

    // add the exponent
    tmp = tmp * pow ((float)2, (int) ((USHORT)exponent - 128));

    if (mantissa & (0x01 << 15))
        tmp = -tmp;
    return tmp;
}


USHORT S4UShort (const UCHAR byteArray[2])
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
    (FLOAT)S4BCDtoBase10(ptr->Byte.rateAPowerFactorAtMax,      2) / (pow (10.0,findImpliedDecimal (ptr->Byte.rateAPowerFactorAtMax)));
    ptr->Real.rateBPowerFactorAtMax =
    (FLOAT)S4BCDtoBase10(ptr->Byte.rateBPowerFactorAtMax,      2) / (pow (10.0,findImpliedDecimal (ptr->Byte.rateBPowerFactorAtMax)));
    ptr->Real.rateCPowerFactorAtMax =
    (FLOAT)S4BCDtoBase10(ptr->Byte.rateCPowerFactorAtMax,      2) / (pow (10.0,findImpliedDecimal (ptr->Byte.rateCPowerFactorAtMax)));
    ptr->Real.rateDPowerFactorAtMax =
    (FLOAT)S4BCDtoBase10(ptr->Byte.rateDPowerFactorAtMax,      2) / (pow (10.0,findImpliedDecimal (ptr->Byte.rateDPowerFactorAtMax)));
    ptr->Real.rateEPowerFactorAtMax =
    (FLOAT)S4BCDtoBase10(ptr->Byte.rateEPowerFactorAtMax,      2) / (pow (10.0,findImpliedDecimal (ptr->Byte.rateEPowerFactorAtMax)));

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
    (FLOAT)S4BCDtoBase10(ptr->Byte.powerFactorAtMaxkM3,      2) / (pow (10.0,findImpliedDecimal (ptr->Byte.powerFactorAtMaxkM3)));

    ptr->Real.coincidentkM3atMaxDemand =
    (DOUBLE) (S4UShort(ptr->Byte.coincidentkM3atMaxDemand) * ptr->Real.kFactor / 1000.0) * (60 / ptr->Real.demandInterval);

    ptr->Real.totalkMh3 = S4BCDtoDouble(ptr->Byte.totalkMh3,      6) * (ptr->Real.kFactor) / 1000.0;

    ptr->bDataIsReal = TRUE;
    memcpy (aInMessBuffer, iDataBuffer, sizeof (LGS4ScanData_t));
    aTotalBytes = getTotalByteCount();
    return ClientErrors::None;
}


INT CtiDeviceLandisGyrS4::copyLoadProfileData(BYTE *aInMessBuffer, ULONG &aTotalBytes)
{
    memcpy(aInMessBuffer, iLoadProfileBuffer, sizeof (LGS4LoadProfile_t));
    aTotalBytes = sizeof (LGS4LoadProfile_t);
    return ClientErrors::None;
}


YukonError_t CtiDeviceLandisGyrS4::ResultDecode(const INMESS   &InMessage,
                                                const CtiTime   TimeNow,
                                                CtiMessageList &vgList,
                                                CtiMessageList &retList,
                                                OutMessageList &outList)
{

    /****************************
    *
    *  intialize the command based on the in message
    *  someday the command will be pulled from the device object directly
    *
    *****************************
    */
    char tmpCurrentCommand = InMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[0],
         tmpCurrentState   = InMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[1];

    if( !_dstFlagValid )
    {
        _dstFlag = readDSTFile( getName() );
    }

    switch (tmpCurrentCommand)
    {
        case CmdScanData:
            {
                CTILOG_INFO(dout, "Scan decode for device "<< getName() <<" in progress ");

                decodeResultScan (InMessage, TimeNow, vgList, retList, outList);
                break;
            }
        case CmdLoadProfileData:
            {
                CTILOG_INFO(dout, "LP decode for device "<< getName() <<" in progress ");

                // just in case we're getting an empty message
                if (tmpCurrentState == StateScanReturnLoadProfile)
                {
                    decodeResultLoadProfile (InMessage, TimeNow, vgList, retList, outList);
                }
                else
                {
                    CTILOG_ERROR(dout, "LP decode failed device "<< getName() <<" invalid state");
                }

                break;
            }
        default:
            {
                CTILOG_ERROR(dout, "Invalid decode for "<< getName());
            }
    }

    return ClientErrors::None;
}

YukonError_t CtiDeviceLandisGyrS4::ErrorDecode (const INMESS   &InMessage,
                                                const CtiTime   TimeNow,
                                                CtiMessageList &retList)
{
    CTILOG_INFO(dout, "ErrorDecode for device "<< getName() <<" in progress");

    YukonError_t retCode = ClientErrors::None;
    CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);
    CtiReturnMsg   *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                            string(InMessage.Return.CommandStr),
                                            string(),
                                            InMessage.ErrorCode,
                                            InMessage.Return.RouteID,
                                            InMessage.Return.RetryMacroOffset,
                                            InMessage.Return.Attempt,
                                            InMessage.Return.GrpMsgID,
                                            InMessage.Return.UserID);


    if (pMsg != NULL)
    {
        pMsg->insert( -1 );           // This is the dispatch token and is unimplemented at this time
        pMsg->insert(CtiCommandMsg::OP_DEVICEID);    // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
        pMsg->insert(getID());             // The id (device or point which failed)
        pMsg->insert(ScanRateGeneral);      // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h
        pMsg->insert(InMessage.ErrorCode);
    }

    insertPointIntoReturnMsg (pMsg, pPIL);

    // send the whole mess to dispatch
    if (pPIL->PointData().size() > 0)
    {
        retList.push_back( pPIL );
    }
    else
    {
        delete pPIL;
    }
    pPIL = NULL;

    return retCode;
}



INT CtiDeviceLandisGyrS4::decodeResultScan (const INMESS   &InMessage,
                                            const CtiTime   TimeNow,
                                            CtiMessageList &vgList,
                                            CtiMessageList &retList,
                                            OutMessageList &outList)
{
    char tmpCurrentState = InMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[1];
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
    CtiTime    peakTime;

    const DIALUPREQUEST      *DupReq = &InMessage.Buffer.DUPSt.DUPRep.ReqSt;
    const DIALUPREPLY        *DUPRep = &InMessage.Buffer.DUPSt.DUPRep;


    CtiPointDataMsg   *pData    = NULL;
    CtiPointNumericSPtr pNumericPoint;

    CtiReturnMsg   *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                            string(InMessage.Return.CommandStr),
                                            string(),
                                            InMessage.ErrorCode,
                                            InMessage.Return.RouteID,
                                            InMessage.Return.RetryMacroOffset,
                                            InMessage.Return.Attempt,
                                            InMessage.Return.GrpMsgID,
                                            InMessage.Return.UserID);

    const LGS4ScanData_t  *scanData = (const LGS4ScanData_t*) DUPRep->Message;

    if (isScanFlagSet(ScanRateGeneral))
    {
        // if we bombed, we need an error condition and to plug values
        if ((tmpCurrentState == StateScanAbort)  ||
            (tmpCurrentState == StateHandshakeAbort) ||
            InMessage.ErrorCode)
        {

            CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

            if (pMsg != NULL)
            {
                pMsg->insert( -1 );           // This is the dispatch token and is unimplemented at this time
                pMsg->insert(CtiCommandMsg::OP_DEVICEID);    // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                pMsg->insert(getID());             // The id (device or point which failed)
                pMsg->insert(ScanRateGeneral);      // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

                pMsg->insert(
                        InMessage.ErrorCode
                            ? InMessage.ErrorCode
                            : ClientErrors::GeneralScanAborted);
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
                if (pNumericPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(i, AnalogPointType)))
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
    resetScanFlag(ScanRateGeneral);

    if (pPIL->PointData().size() > 0)
    {
        retList.push_back( pPIL );
    }
    else
    {
        delete pPIL;
    }
    pPIL = NULL;

    if( DebugLevel & 0x0001 )
      ResultDisplay(InMessage);
    return ClientErrors::None;
}

INT CtiDeviceLandisGyrS4::decodeResultLoadProfile (const INMESS   &InMessage,
                                                   const CtiTime   TimeNow,
                                                   CtiMessageList &vgList,
                                                   CtiMessageList &retList,
                                                   OutMessageList &outList)
{
    const DIALUPREQUEST     *dupReq = &InMessage.Buffer.DUPSt.DUPRep.ReqSt;
    const DIALUPREPLY       *dupRep = &InMessage.Buffer.DUPSt.DUPRep;

    const LGS4LoadProfile_t *localLP = (const LGS4LoadProfile_t*)&dupRep->Message;


    // power outages tracked by 6 second interval since midnight
    ULONG secondsSinceMidnight = 0;
    BYTE  temp[2];
    USHORT  intervalPulses;
    DOUBLE  intervalData;
    USHORT   expectedLastInterval;
    ULONG lastLPTime = getLastLPTime().seconds();
    CtiTime   intervalTime;

    CHAR    buffer[60];
    BOOL  validPointFound=FALSE;

    CtiPointDataMsg   *pData    = NULL;
    CtiPointNumeric   *pNumericPoint = NULL;

    CtiReturnMsg   *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                            string(InMessage.Return.CommandStr),
                                            string(),
                                            InMessage.ErrorCode,
                                            InMessage.Return.RouteID,
                                            InMessage.Return.RetryMacroOffset,
                                            InMessage.Return.Attempt,
                                            InMessage.Return.GrpMsgID,
                                            InMessage.Return.UserID);

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
                    // if new date is empty, we're doing an inter interval power outage
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

                            //check for intervals to make sure it isn't outrageous - 1 year
                            //Example (1440 mins in a day / 15 min intervals) * 365 days ==> 35040 intervals
                            ULONG maxMissingIntervals = (1440 / localLP->configuration.intervalLength) * 365;
                            if (dateChangeMissingIntervals > maxMissingIntervals)
                            {
                                dateChangeMissingIntervals = maxMissingIntervals;
                            }

                            for (int x=0; x < dateChangeMissingIntervals; x++)
                            {
                                // fill message with plug data to next interval
                                for (int y=0; y < localLP->configuration.numberOfChannels;y++)
                                {
                                    intervalTime = CtiTime (getCurrentLPDate() +
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
                                if (getCurrentLPInterval() >= intervalsPerDay(localLP->configuration.intervalLength*60))
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
                int year = CtiDate().year();

                // check what year we are in
                if (S4BCDtoBase10 (&mon,1) > localLP->meterDate.month)
                {
                    // if the month on the record is > the current month, we must be using a different year
                    year -= 1;
                }

                CtiDate recordDate (S4BCDtoBase10 (&localLP->loadProfileData[dataWalker],1),
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
                    CtiTime recordTime(recordDate);
                    ULONG  powerDown = getCurrentLPDate() + getPowerDownTime();
                    ULONG  missingIntervals = (recordTime.seconds() - powerDown) /
                                              (localLP->configuration.intervalLength * 60) ;

                    // ????? need a check for intervals to make sure it isn't outrageous??????
                    for (int x=0; x < missingIntervals; x++)
                    {
                        for (int y=0; y < localLP->configuration.numberOfChannels;y++)
                        {

                            intervalTime = CtiTime (getCurrentLPDate() +
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
                        if (getCurrentLPInterval() >= intervalsPerDay(localLP->configuration.intervalLength*60))
                        {
                            setCurrentLPInterval (0);
                        }
                    }
                }

                // always set this because it gets zeroed out if a data packet comes in
                setNewDate (CtiTime(recordDate).seconds());

                // set previous date only if newDate doesn't exists
                if (getOldTime() != 0)
                {
                    if (getPreviousLPDate() != 0)// we have a new date, figure out how long the change is
                    {
                        setCurrentLPDate (getPreviousLPDate());
                    }
                }
                else
                {
                    // set the previous load profile date
                    setPreviousLPDate (getCurrentLPDate());
                    setCurrentLPDate (CtiTime(recordDate).seconds());
                }

                // new day start from zero
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
                    const CtiTime todayMidnight(0,0);
                    expectedLastInterval = (CtiTime::now().seconds() - todayMidnight.seconds()) /
                                             (localLP->configuration.intervalLength * 60.0);


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

                                // plug data from current to new interval
                                for (int x = getCurrentLPInterval(); x < newInterval; x++)
                                {
                                    // fill message with plug data to next interval
                                    for (int y=0; y < localLP->configuration.numberOfChannels;y++)
                                    {
                                        intervalTime = CtiTime (getCurrentLPDate() +
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

                                intervalTime = CtiTime (getCurrentLPDate() +
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

                                intervalTime = CtiTime (getCurrentLPDate() +
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
                            }
                        }
                        else
                        {
                            // we are one today, check for repeated data
                            if (todayMidnight.seconds() == getCurrentLPDate())
                            {
                                if (expectedLastInterval < getCurrentLPInterval())
                                {
                                    CTILOG_ERROR(dout, "Repeated data possible for " << getName());
                                }
                                else
                                {
                                    // data is good, put it in the point and send it
                                    // if our offset if valid, add the point
                                    intervalTime = CtiTime (getCurrentLPDate() +
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
                                }
                            }
                            else
                            {
                                // data is good, put it in the point and send it
                                intervalTime = CtiTime (getCurrentLPDate() +
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
                            }
                        }
                    }
                    else
                    {
                        CTILOG_ERROR(dout, "Parity calculation failed interval "<< getCurrentLPInterval() <<" channel "<< getCurrentLPChannel() <<" for "<< getName());
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
    if (pPIL->PointData().size() > 0)
    {
        retList.push_back( pPIL );
    }
    else
    {
        delete pPIL;
    }
    pPIL = NULL;

    return ClientErrors::None;
}

// Routine to display or print the message
INT CtiDeviceLandisGyrS4::ResultDisplay (const INMESS &InMessage)
{
    const LGS4ScanData_t  *ptr = (const LGS4ScanData_t*) InMessage.Buffer.DUPSt.DUPRep.Message;

    Cti::FormattedList itemList;

    /// meter info ///
    itemList.add("Meter ID")                        << ptr->Real.deviceID;
    itemList.add("Unit SN")                         << ptr->Real.serialNumber;
    itemList.add("Time")                            << ptr->Real.dateTime.hours <<":"<< ptr->Real.dateTime.minutes;
    itemList.add("Date")                            << ptr->Real.dateTime.month <<"/"<< ptr->Real.dateTime.day <<"/"<< ptr->Real.dateTime.year+2000;
    itemList.add("Outages")                         << ptr->Real.powerOutages;
    itemList.add("Demand prev interval")            << ptr->Real.prevIntervalDemand;
    itemList.add("Selectable Metric prev interval") << ptr->Real.prevIntervalSelectedMetric;
    itemList.add("Third Metric prev interval")      << ptr->Real.prevIntervalThirdMetric;

    /// phase info ///
    {
        Cti::FormattedTable table;

        table.setCell(0,0) <<"Phase";
        table.setCell(0,1) <<"Voltage";
        table.setCell(0,2) <<"Current";

        table.setCell(1,0) <<"A";
        table.setCell(1,1) << ptr->Real.phaseAVoltage;
        table.setCell(1,2) << ptr->Real.phaseACurrent;

        table.setCell(2,0) <<"B";
        table.setCell(2,1) << ptr->Real.phaseBVoltage;
        table.setCell(2,2) << ptr->Real.phaseBCurrent;

        table.setCell(3,0) <<"C";
        table.setCell(3,1) << ptr->Real.phaseCVoltage;
        table.setCell(3,2) << ptr->Real.phaseCCurrent;

        table.setCell(4,0) <<"Neutral";
        table.setCell(4,2) << ptr->Real.neutralCurrent;

        itemList << table;
    }

    /// rate kW info ///
    {
        Cti::FormattedTable table;

        table.setCell(0,0) <<"Rate";
        table.setCell(0,1) <<"Time";
        table.setCell(0,2) <<"Date";
        table.setCell(0,3) <<"MaxkW";
        table.setCell(0,4) <<"CDem";
        table.setCell(0,5) <<"PF";
        table.setCell(0,6) <<"kWh";

        table.setCell(1,0) <<"A";
        table.setCell(1,1) << ptr->Real.dateTimeRateAMaxkW.hours <<":"<< ptr->Real.dateTimeRateAMaxkW.minutes;
        table.setCell(1,2) << ptr->Real.dateTimeRateAMaxkW.month <<"/"<< ptr->Real.dateTimeRateAMaxkW.day <<"/"<< ptr->Real.dateTimeRateAMaxkW.year+2000;
        table.setCell(1,3) << ptr->Real.rateAMaxkW;
        table.setCell(1,4) << ptr->Real.rateACoincidentDemand;
        table.setCell(1,5) << ptr->Real.rateAPowerFactorAtMax;
        table.setCell(1,6) << ptr->Real.rateAkWh;

        table.setCell(2,0) <<"B";
        table.setCell(2,1) << ptr->Real.dateTimeRateBMaxkW.hours <<":"<< ptr->Real.dateTimeRateBMaxkW.minutes;
        table.setCell(2,2) << ptr->Real.dateTimeRateBMaxkW.month <<"/"<< ptr->Real.dateTimeRateBMaxkW.day <<"/"<< ptr->Real.dateTimeRateBMaxkW.year+2000;
        table.setCell(2,3) << ptr->Real.rateBMaxkW;
        table.setCell(2,4) << ptr->Real.rateBCoincidentDemand;
        table.setCell(2,5) << ptr->Real.rateBPowerFactorAtMax;
        table.setCell(2,6) << ptr->Real.rateBkWh;

        table.setCell(3,0) <<"C";
        table.setCell(3,1) << ptr->Real.dateTimeRateCMaxkW.hours <<":"<< ptr->Real.dateTimeRateCMaxkW.minutes;
        table.setCell(3,2) << ptr->Real.dateTimeRateCMaxkW.month <<"/"<< ptr->Real.dateTimeRateCMaxkW.day <<"/"<< ptr->Real.dateTimeRateCMaxkW.year+2000;
        table.setCell(3,3) << ptr->Real.rateCMaxkW;
        table.setCell(3,4) << ptr->Real.rateCCoincidentDemand;
        table.setCell(3,5) << ptr->Real.rateCPowerFactorAtMax;
        table.setCell(3,6) << ptr->Real.rateCkWh;

        table.setCell(4,0) <<"D";
        table.setCell(4,1) << ptr->Real.dateTimeRateDMaxkW.hours <<":"<< ptr->Real.dateTimeRateDMaxkW.minutes;
        table.setCell(4,2) << ptr->Real.dateTimeRateDMaxkW.month <<"/"<< ptr->Real.dateTimeRateDMaxkW.day <<"/"<< ptr->Real.dateTimeRateDMaxkW.year+2000;
        table.setCell(4,3) << ptr->Real.rateDMaxkW;
        table.setCell(4,4) << ptr->Real.rateDCoincidentDemand;
        table.setCell(4,5) << ptr->Real.rateDPowerFactorAtMax;
        table.setCell(4,6) << ptr->Real.rateDkWh;

        table.setCell(5,0) <<"E";
        table.setCell(5,1) << ptr->Real.dateTimeRateEMaxkW.hours <<":"<< ptr->Real.dateTimeRateEMaxkW.minutes;
        table.setCell(5,2) << ptr->Real.dateTimeRateEMaxkW.month <<"/"<< ptr->Real.dateTimeRateEMaxkW.day <<"/"<< ptr->Real.dateTimeRateEMaxkW.year+2000;
        table.setCell(5,3) << ptr->Real.rateEMaxkW;
        table.setCell(5,4) << ptr->Real.rateECoincidentDemand;
        table.setCell(5,5) << ptr->Real.rateEPowerFactorAtMax;
        table.setCell(5,6) << ptr->Real.rateEkWh;

        itemList << table;
    }

    /// selectable metric ///
    {
        Cti::StreamBuffer sb;
        switch (ptr->Real.selectableMetric)
        {
        case LGS4_METRIC_KWH:
            sb <<"kwh";
            break;
        case LGS4_METRIC_KVARH:
            sb <<"kvarh";
            break;
        case LGS4_METRIC_RMSKVAH:
            sb <<"kvah";
            break;
        default:
            sb << ptr->Real.selectableMetric;
        }

        itemList.add("Selectable Metric") << sb;
    }

    {
        Cti::FormattedTable table;

        table.setCell(0,0) <<"Rate";
        table.setCell(0,1) <<"Time";
        table.setCell(0,2) <<"Date";
        table.setCell(0,3) <<"MaxkM";
        table.setCell(0,4) <<"kMh";

        table.setCell(1,0) <<"A";
        table.setCell(1,1) << ptr->Real.dateTimeRateAMaxkM.hours <<":"<< ptr->Real.dateTimeRateAMaxkM.minutes;
        table.setCell(1,2) << ptr->Real.dateTimeRateAMaxkM.month <<"/"<< ptr->Real.dateTimeRateAMaxkM.day <<"/"<< ptr->Real.dateTimeRateAMaxkM.year+2000;
        table.setCell(1,3) << ptr->Real.rateAMaxkM;
        table.setCell(1,4) << ptr->Real.rateAkMh;

        table.setCell(2,0) <<"B";
        table.setCell(2,1) << ptr->Real.dateTimeRateBMaxkM.hours <<":"<< ptr->Real.dateTimeRateBMaxkM.minutes;
        table.setCell(2,2) << ptr->Real.dateTimeRateBMaxkM.month <<"/"<< ptr->Real.dateTimeRateBMaxkM.day <<"/"<< ptr->Real.dateTimeRateBMaxkM.year+2000;
        table.setCell(2,3) << ptr->Real.rateBMaxkM;
        table.setCell(2,4) << ptr->Real.rateBkMh;

        table.setCell(3,0) <<"C";
        table.setCell(3,1) << ptr->Real.dateTimeRateCMaxkM.hours <<":"<< ptr->Real.dateTimeRateCMaxkM.minutes;
        table.setCell(3,2) << ptr->Real.dateTimeRateCMaxkM.month <<"/"<< ptr->Real.dateTimeRateCMaxkM.day <<"/"<< ptr->Real.dateTimeRateCMaxkM.year+2000;
        table.setCell(3,3) << ptr->Real.rateCMaxkM;
        table.setCell(3,4) << ptr->Real.rateCkMh;

        table.setCell(4,0) <<"D";
        table.setCell(4,1) << ptr->Real.dateTimeRateDMaxkM.hours <<":"<< ptr->Real.dateTimeRateDMaxkM.minutes;
        table.setCell(4,2) << ptr->Real.dateTimeRateDMaxkM.month <<"/"<< ptr->Real.dateTimeRateDMaxkM.day <<"/"<< ptr->Real.dateTimeRateDMaxkM.year+2000;
        table.setCell(4,3) << ptr->Real.rateDMaxkM;
        table.setCell(4,4) << ptr->Real.rateDkMh;

        table.setCell(5,0) <<"E";
        table.setCell(5,1) << ptr->Real.dateTimeRateEMaxkM.hours <<":"<< ptr->Real.dateTimeRateEMaxkM.minutes;
        table.setCell(5,2) << ptr->Real.dateTimeRateEMaxkM.month <<"/"<< ptr->Real.dateTimeRateEMaxkM.day <<"/"<< ptr->Real.dateTimeRateEMaxkM.year+2000;
        table.setCell(5,3) << ptr->Real.rateEMaxkM;
        table.setCell(5,4) << ptr->Real.rateEkMh;

        itemList << table;
    }

    /// third metric ///
    {
        Cti::StreamBuffer sb;
        switch (ptr->Real.thirdMetric)
        {
        case LGS4_METRIC_KWH:
            sb <<"kwh";
            break;
        case LGS4_METRIC_KVARH:
            sb <<"kvarh";
            break;
        case LGS4_METRIC_RMSKVAH:
            sb <<"kvah";
            break;
        default:
            sb << ptr->Real.thirdMetric;
        }

        itemList.add("Third Metric") << sb;
    }

    {
        Cti::FormattedTable table;

        table.setCell(0,1) <<"Time";
        table.setCell(0,2) <<"Date";
        table.setCell(0,3) <<"MaxkM3";
        table.setCell(0,4) <<"CDem";
        table.setCell(0,5) <<"PF";
        table.setCell(0,6) <<"kM3h";

        table.setCell(1,0) <<"M3";
        table.setCell(1,1) << ptr->Real.dateTimeMaxkM3.hours <<":"<< ptr->Real.dateTimeMaxkM3.minutes;
        table.setCell(1,2) << ptr->Real.dateTimeMaxkM3.month <<"/"<< (USHORT)ptr->Real.dateTimeMaxkM3.day <<"/"<< ptr->Real.dateTimeMaxkM3.year+2000;
        table.setCell(1,3) << ptr->Real.maxkM3;
        table.setCell(1,4) << ptr->Real.coincidentkM3atMaxDemand;
        table.setCell(1,5) << ptr->Real.powerFactorAtMaxkM3;
        table.setCell(1,6) << ptr->Real.totalkMh3;

        itemList << table;
    }

    CTILOG_INFO(dout, "Result display for device "<< getName() <<
            itemList
            );

    return ClientErrors::None;

}

LONG CtiDeviceLandisGyrS4::findLPDataPoint (LGS4LPPointInfo_t &point, USHORT aMetric)
{
    LONG retCode = ClientErrors::None;
    CtiPointNumericSPtr   pNumericPoint;

    // always set the metric
    point.metric = aMetric;

    switch (aMetric)
    {
        case LGS4_METRIC_KWH:
            {
                // looking for demand point
                if (pNumericPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_KW, AnalogPointType)))
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = ClientErrors::Abnormal;
                }

                break;
            }
        case LGS4_METRIC_KVARH:
            {
                if (pNumericPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_KVAR, AnalogPointType)))
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = ClientErrors::Abnormal;
                }

                break;
            }
        case LGS4_METRIC_RMSKVAH:
            {
                if (pNumericPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_KVA, AnalogPointType)))
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = ClientErrors::Abnormal;
                }

                break;
            }

        case LGS4_METRIC_VASQRH:
            {
                if (pNumericPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_PHASE_A_VOLTAGE, AnalogPointType)))
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = ClientErrors::Abnormal;
                }

                break;
            }

        case LGS4_METRIC_VBSQRH:
            {
                if (pNumericPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_PHASE_B_VOLTAGE, AnalogPointType)))
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = ClientErrors::Abnormal;
                }

                break;
            }

        case LGS4_METRIC_VCSQRH:
            {
                if (pNumericPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_PHASE_C_VOLTAGE, AnalogPointType)))
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = ClientErrors::Abnormal;
                }

                break;
            }

        case LGS4_METRIC_IASQRH:
            {
                if (pNumericPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_PHASE_A_CURRENT, AnalogPointType)))
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = ClientErrors::Abnormal;
                }

                break;
            }

        case LGS4_METRIC_IBSQRH:
            {
                if (pNumericPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_PHASE_B_CURRENT, AnalogPointType)))
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = ClientErrors::Abnormal;
                }

                break;
            }

        case LGS4_METRIC_ICSQRH:
            {
                if (pNumericPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_PHASE_C_CURRENT, AnalogPointType)))
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = ClientErrors::Abnormal;
                }

                break;
            }
        case LGS4_METRIC_INSQRH:
            {
                if (pNumericPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(OFFSET_LOADPROFILE_NEUTRAL_CURRENT, AnalogPointType)))
                {
                    point.pointId = pNumericPoint->getPointID();
                    point.multiplier = pNumericPoint->getMultiplier();
                    point.metric = aMetric;
                }
                else
                {
                    point.pointId = 0;
                    point.multiplier = 1.0;
                    retCode = ClientErrors::Abnormal;
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
                retCode = ClientErrors::Abnormal;
            }
    }
    return retCode;
}

DOUBLE CtiDeviceLandisGyrS4::calculateIntervalData (USHORT aInterval,
                                                    const LGS4LoadProfile_t *aLPConfig,
                                                    LGS4LPPointInfo_t aLPPoint)
{
    DOUBLE constant;
    DOUBLE finalValue = 0.0;

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


BOOL CtiDeviceLandisGyrS4::getMeterDataFromScanStruct (int aOffset, DOUBLE &aValue, CtiTime &peak, const LGS4ScanData_t *aScanData)
{
    BOOL isValidPoint = FALSE;

    // this is initial value
    peak = PASTDATE;

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
                    peak = CtiTime (CtiDate (aScanData->Real.dateTimeRateAMaxkW.day,
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
                    peak = CtiTime (CtiDate (aScanData->Real.dateTimeRateBMaxkW.day,
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
                    peak = CtiTime (CtiDate (aScanData->Real.dateTimeRateCMaxkW.day,
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
                    peak = CtiTime (CtiDate (aScanData->Real.dateTimeRateDMaxkW.day,
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
                    peak = CtiTime (CtiDate (aScanData->Real.dateTimeRateEMaxkW.day,
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
                        peak = CtiTime (CtiDate (aScanData->Real.dateTimeRateAMaxkM.day,
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
                        peak = CtiTime (CtiDate (aScanData->Real.dateTimeMaxkM3.day,
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
                        peak = CtiTime (CtiDate (aScanData->Real.dateTimeRateBMaxkM.day,
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
                        peak = CtiTime (CtiDate (aScanData->Real.dateTimeRateCMaxkM.day,
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
                        peak = CtiTime (CtiDate (aScanData->Real.dateTimeRateDMaxkM.day,
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
                        peak = CtiTime (CtiDate (aScanData->Real.dateTimeRateEMaxkM.day,
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
                        peak = CtiTime (CtiDate (aScanData->Real.dateTimeRateAMaxkM.day,
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
                        peak = CtiTime (CtiDate (aScanData->Real.dateTimeMaxkM3.day,
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
                        peak = CtiTime (CtiDate (aScanData->Real.dateTimeRateBMaxkM.day,
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
                        peak = CtiTime (CtiDate (aScanData->Real.dateTimeRateCMaxkM.day,
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
                        peak = CtiTime (CtiDate (aScanData->Real.dateTimeRateDMaxkM.day,
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
                        peak = CtiTime (CtiDate (aScanData->Real.dateTimeRateEMaxkM.day,
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


void CtiDeviceLandisGyrS4::syncAppropriateTime (ULONG timestamp)
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
        setPowerDownTime(timestamp);
    }
    else if (getPowerUpTime() == 0)
    {
        setPowerUpTime(timestamp);
    }
    else
    {
        // we must be another pair of timestamps
        setPowerDownTime(timestamp);
        setPowerUpTime(0);
    }
}


BOOL CtiDeviceLandisGyrS4::verifyAndAddPointToReturnMsg (LONG   aPointId,
                                                         DOUBLE aValue,
                                                         USHORT aQuality,
                                                         CtiTime aTime,
                                                         CtiReturnMsg *aReturnMsg,
                                                         USHORT aIntervalType,
                                                         string aValReport)

{
    BOOL validPointFound = FALSE;
    CtiPointDataMsg   *pData    = NULL;

    // if our offset if valid, add the point
    if (aPointId)
    {
        //create a new message
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

            if (aTime != PASTDATE)
            {
                //
                //  hack fix for non-DST compliant meters - someday to be absorbed by a big, global timekeeper
                //
                if( _dstFlag && aTime.isDST( ) )
                {
                    aTime = aTime +  60 * 60;
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
        aReturnMsg->PointData().push_back(aDataPoint);
        aDataPoint = NULL;  // We just put it on the list...
    }
    else
    {
        CTILOG_ERROR(dout, "Unexpected return message is Null");

        delete aDataPoint;
        aDataPoint = NULL;
        retCode = FALSE;
    }
    return retCode;
}


