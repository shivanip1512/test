/*-----------------------------------------------------------------------------*
*
* File:   dev_alpha
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_alpha.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2004/07/23 12:54:28 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*    History:
      $Log: dev_alpha.cpp,v $
      Revision 1.10  2004/07/23 12:54:28  cplender
      USHORT pointids are not large enough.  Changed to LONG.
      dispatch was having point type mismatch issues because of id truncation.

      Revision 1.9  2003/04/10 21:45:47  dsutton
      Added code to check the CRC on the multiple message classes (ones over 64 bytes)
      and added checks to make sure we had received the entire message


*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )


#include "yukon.h"
#include "porter.h"
#include "dev_alpha.h"

#include "pt_base.h"
#include "pt_status.h"
#include "pt_analog.h"
#include "pt_accum.h"

#include "msg_pcreturn.h"
#include "msg_cmd.h"
#include "msg_pdata.h"
#include "msg_multi.h"
#include "cmdparse.h"

#include "logger.h"
#include "guard.h"

//#include "dupreq.h"
#include "dlldefs.h"

#include "utility.h"


CtiDeviceAlpha::CtiDeviceAlpha(BYTE         *dataPtr,
                               BYTE          *lpPtr,
                               BYTE          *wPtr,
                               ULONG         cnt) :
_dataBuffer(dataPtr),
_loadProfileBuffer(lpPtr),
_lpWorkBuffer(wPtr),
_singleMsgBuffer (NULL),
_totalByteCount (cnt),
_readClass(0),
_readLength(0),
_readFunction(0),
_classReadComplete(FALSE)
{
}


CtiDeviceAlpha::~CtiDeviceAlpha()
{
    if (_dataBuffer != NULL)
    {
        delete []_dataBuffer;
        _dataBuffer = NULL;
    }

    if (_singleMsgBuffer != NULL)
    {
        delete []_singleMsgBuffer;
        _singleMsgBuffer = NULL;
    }

    if (_loadProfileBuffer != NULL)
    {
        delete []_loadProfileBuffer;
        _loadProfileBuffer = NULL;
    }

    if (_lpWorkBuffer != NULL)
    {
        delete []_lpWorkBuffer;
        _lpWorkBuffer = NULL;
    }
}

/***************************************************************
 all getters and setters
****************************************************************/
ULONG    CtiDeviceAlpha::getTotalByteCount() const
{
    return _totalByteCount;
}

CtiDeviceAlpha& CtiDeviceAlpha::setTotalByteCount(ULONG c)
{
    _totalByteCount = c;
    return *this;
}

USHORT    CtiDeviceAlpha::getBytesToRetrieve() const
{
    return _bytesToRetrieve;
}

CtiDeviceAlpha& CtiDeviceAlpha::setBytesToRetrieve( USHORT c)
{
    _bytesToRetrieve = c;
    return *this;
}

USHORT    CtiDeviceAlpha::getSingleMsgByteCount() const
{
    return _singleMsgBufferBytes;
}

CtiDeviceAlpha& CtiDeviceAlpha::setSingleMsgByteCount( USHORT c)
{
    _singleMsgBufferBytes = c;
    return *this;
}

USHORT CtiDeviceAlpha::getReadLength () const
{
    return _readLength;
}


CtiDeviceAlpha& CtiDeviceAlpha::setReadLength (USHORT aLength)
{
    _readLength = aLength;
    return *this;
}


USHORT CtiDeviceAlpha::getReadClass () const
{
    return _readClass;
}


CtiDeviceAlpha& CtiDeviceAlpha::setReadClass (USHORT aClass)
{
    _readClass = aClass;
    return *this;
}


USHORT CtiDeviceAlpha::getReadFunction () const
{
    return _readFunction;
}


CtiDeviceAlpha& CtiDeviceAlpha::setReadFunction (USHORT aFunction)
{
    _readFunction = aFunction;
    return *this;
}

BOOL CtiDeviceAlpha::isClassReadComplete () const
{
    return _classReadComplete;
}


CtiDeviceAlpha& CtiDeviceAlpha::setClassReadComplete (BOOL aFlag)
{
    _classReadComplete = aFlag;
    return *this;
}

bool CtiDeviceAlpha::isReturnedBufferValid (CtiXfer  &Transfer)
{
    bool retVal = false;

    if (Transfer.getInBuffer()[0] == STX)
    {
        if (!decodeAckNak (Transfer.getInBuffer()[2]))
        {
            retVal = true;
        }
    }
    return retVal;
}

INT CtiDeviceAlpha::GeneralScan(CtiRequestMsg *pReq,
                                CtiCommandParser &parse,
                                OUTMESS *&OutMessage,
                                RWTPtrSlist< CtiMessage > &vgList,
                                RWTPtrSlist< CtiMessage > &retList,
                                RWTPtrSlist< OUTMESS > &outList,
                                INT ScanPriority)
{
    ULONG BytesWritten;

    // load profile information
    time_t         RequestedTime = 0L;
    time_t         DeltaTime;
    time_t         NowTime;

    //OUTMESS *OutMessage = CTIDBG_new OUTMESS;

    if (OutMessage != NULL)
    {
        setCurrentCommand(CmdScanData);

        setReadClass (0);
        setReadLength (0);
        setReadFunction (0);

        // need all these when we get back to the scanner side so must set here
        OutMessage->Buffer.DUPReq.Command[0] = CmdScanData;
        // save command[1] for final state of communication attempt
        OutMessage->Buffer.DUPReq.Command[2] = getReadClass();
        OutMessage->Buffer.DUPReq.Command[3] = getReadLength();

        // whether this is needed is decided later
        OutMessage->Buffer.DUPReq.LP_Time = getLastLPTime().seconds();
        OutMessage->Buffer.DUPReq.LastFileTime = getLastLPTime().seconds();

        // Load all the other stuff that is needed
        OutMessage->DeviceID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->EventCode = RESULT | ENCODED;
        OutMessage->Sequence  = 0;
        OutMessage->Retry     = 3;
        EstablishOutMessagePriority( OutMessage, ScanPriority );

        outList.insert(OutMessage);
        OutMessage = NULL;
    }
    else
    {
        return MEMORY;
    }
    return NoError;
}



ULONG CtiDeviceAlpha::alphaCrypt(ULONG Key, ULONG PWord)
{
/*
   CRYPT.C
   5-11-94
   D. C. Olivier

This routine duplicates the encryption algorithm used in the A+.

*/

    unsigned long  pword;      /* Password */
    char*        stopper;   /* required by 'strtoul' */
    int              i;         /* loop index */
    int              j, k = 0; /* used to simulate rotate   */
    /*   through carry */

    union
    {
        unsigned long  key ;    /* encryption key */
        struct
        {           /* broken into bytes */
            unsigned char byta, bytb, bytc, bytd;
        } parts;
    } val;


/* Get input values, 8 hex digits each */

    val.key = Key;       // strtoul(argv[1], &stopper, 0x10);
    pword = PWord;       // strtoul(argv[2], &stopper, 0x10);


/* Add an arbitrary number to the key just for fun. */

    val.key  += 0xab41;


/* Generate a four bit checksum to be used as loop index. */

    i = val.parts.byta + val.parts.bytb + val.parts.bytc + val.parts.bytd;
    i = i & 0x0f;

    while (i >= 0)
    {

/* Set 'j' to the value of the high bit before shifting.
   Simulates carry flag. */

        if (val.parts.bytd >= 0x80) j = 1;
        else           j = 0;

/* Shift the key.  Add in the carry flag from the previous loop. */

        val.key = val.key << 1;
        val.key += k;

        k = j;


/* Apply the key to the password. */

        pword ^= val.key;

        i--;

    }
    return pword;
}

UCHAR CtiDeviceAlpha::decodeAckNak(UCHAR AckNak)
{
    UCHAR    ret = AckNak;     // TRUE will indicate a NAK response....

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        switch (AckNak)
        {
            case 0x00:
                // ACK - No Error
                break;
            case 0x01:
                dout << RWTime() << " " << getName() << " NAK: Bad CRC"<< endl;
                break;
            case 0x02:
                dout << RWTime() << " " << getName() << " NAK: Communications Lockout against this function"<< endl;
                break;
            case 0x03:
                dout << RWTime() << " " << getName() << " NAK: Illegal command, syntax, or length"<< endl;
                break;
            case 0x04:
                dout << RWTime() << " " << getName() << " NAK: Framing Error"<< endl;
                break;
            case 0x05:
                dout << RWTime() << " " << getName() << " NAK: Timeout Error"<< endl;
                break;
            case 0x06:
                dout << RWTime() << " " << getName() << " NAK: Invalid Password"<< endl;
                break;
            case 0x07:
                dout << RWTime() << " " << getName() << " NAK: NAK Received from computer"<< endl;
                break;
            case 0x0C:
                dout << RWTime() << " " << getName() << " NAK: Request in progress, try again later"<< endl;
                break;
            case 0x0D:
                dout << RWTime() << " " << getName() << " NAK: Too busy to honor request, try again later"<< endl;
                break;
            case 0x0F:
                dout << RWTime() << " " << getName() << " NAK: Rules Class NAK, Request not supported by current class 70/71 definition"<< endl;
                break;
            default:
                dout << RWTime() << " " << getName() << " NAK: Unknown NAK. Refer to ABB documentation"<< endl;
                break;
        }
    }
    return ret;
}


USHORT  CtiDeviceAlpha::addCRC(UCHAR* buffer, LONG length, BOOL bAdd)
{
   ULONG       i,j;
   BYTEUSHORT   CRC;

   BYTE CRCMSB = 0xff;
   BYTE CRCLSB = 0xff;
   BYTE Temp;
   BYTE Acc;

   CRC.sh = 0;

   if(length > 0)
   {
        for(i = 0; i < (ULONG)length; i++)
        {
           CRC.ch[1] ^= buffer[i];

           for(j = 0; j < 8; j++)
           {
              if(CRC.sh & 0x8000)
              {
                 CRC.sh = CRC.sh << 1;
                 CRC.sh ^= 0x1021;
              }
              else
              {
                 CRC.sh = CRC.sh << 1;
              }
           }

        }

        if(bAdd)
        {
           buffer[length]     = CRC.ch[1];
           buffer[length + 1] = CRC.ch[0];
        }

   }
   return CRC.sh;
}

INT CtiDeviceAlpha::checkCRC(BYTE *InBuffer,ULONG InCount)
{
   BYTEUSHORT  CRC;
   INT         retval = NORMAL;

    if(InCount > 3)
    {
        CRC.sh = addCRC(InBuffer, InCount - 2, FALSE);

        if( CRC.ch[0] == InBuffer[InCount - 1] &&
            CRC.ch[1] == InBuffer[InCount - 2] )
        {
            retval = NORMAL;
        }
        else
        {
            retval=!NORMAL;
        }
    }

   return retval;
}

INT CtiDeviceAlpha::decodeResponse (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    USHORT retCode=NORMAL;

    switch (getCurrentCommand())
    {
        case CmdSelectMeter:
            {
                // decode the return message
                retCode = decodeResponseSelectMeter (Transfer, commReturnValue, traceList);
                break;
            }
        case CmdLoadProfileData:
            {
                retCode = decodeResponseLoadProfile (Transfer, commReturnValue, traceList);
                break;
            }

            // need this to get through the terminate command send and receive
        case CmdLoadProfileTransition:
        case CmdScanData:
            {
                retCode = decodeResponseScan (Transfer, commReturnValue, traceList);
                break;
            }
        default:
            {
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState (StateScanAbort);
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Invalid command for " << getName() << " (" << __LINE__ << ") " << getCurrentCommand() << endl;
                }
                retCode = !NORMAL;
                break;
            }
    }
    return retCode;
}


INT CtiDeviceAlpha::freeDataBins  ()
{
    if (_dataBuffer != NULL)
    {
        delete []_dataBuffer;
        _dataBuffer = NULL;
    }

    if (_singleMsgBuffer != NULL)
    {
        delete []_singleMsgBuffer;
        _singleMsgBuffer = NULL;
    }

    if (_loadProfileBuffer != NULL)
    {
        delete []_loadProfileBuffer;
        _loadProfileBuffer = NULL;
    }

    // this should be null, but if not
    if (_lpWorkBuffer != NULL)
    {
        delete []_lpWorkBuffer;
        _lpWorkBuffer = NULL;
    }

    // re-init everything for next time through
    setTotalByteCount (0);
    setClassReadComplete (FALSE);
    setCurrentState (StateHandshakeInitialize);
    return NORMAL;
}


INT CtiDeviceAlpha::ResultDecode(INMESS *InMessage,
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
                decodeResultScan(InMessage, TimeNow, vgList, retList, outList);
                break;
            }
        case CmdLoadProfileData:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " LP decode for device " << getName() << " in progress " << endl;
                }
                decodeResultLoadProfile(InMessage, TimeNow, vgList, retList, outList);
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " (" << __LINE__ << ") *** ERROR *** Invalid decode for " << getName() << endl;
                }
            }
    }

    return NORMAL;
}

INT CtiDeviceAlpha::ErrorDecode (INMESS *InMessage,
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
        pMsg->insert(InMessage->EventCode);     // The error number from dsm2.h or yukon.h which was reported.
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


INT CtiDeviceAlpha::generateCommand (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList )
{
    USHORT retCode=NORMAL;
    int i;

    switch (getCurrentCommand())
    {
        // need this to get through the terminate command send and receive
        case CmdSelectMeter:
            {
                retCode = generateCommandSelectMeter (Transfer, traceList);
                break;
            }
        case CmdLoadProfileTransition:
        case CmdScanData:
            {
                retCode = generateCommandScan(Transfer, traceList);
                break;
            }
        case CmdLoadProfileData:
            {
                retCode = generateCommandLoadProfile (Transfer, traceList);
                break;
            }
#if 0
        case CmdAlphaNoData:
            {
                retCode = generateCommandWithNoData( Transfer, traceList );
                break;
            }
        case CmdAlphaWithData:
            {
                retCode = generateCommandWithData( Transfer, traceList );
                break;
            }
        case CmdAlphaPartialRead:
            {
                break;
            }
#endif

        default:
            //   set this to zero so nothing happens
            generateCommandTerminate (Transfer, traceList);
            setPreviousState (StateScanAbort);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Invalid command for " << getName() << " (" << __LINE__ << ") " << getCurrentCommand() << endl;
            }
            retCode = !NORMAL;
            break;
    }
    return retCode;
}

INT CtiDeviceAlpha::generateCommandHandshake (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList)
{
    BYTEULONG         passWord;
    BYTEULONG         passwordValue;
    BYTEULONG         keyValue;
    USHORT            retCode = NORMAL;

    switch (getCurrentState())
    {
        case StateHandshakeInitialize:
            {
                // zero out the flags
//         InMessage->Buffer.DUPSt.DUPRep.CompFlag = DIALUP_COMP_START;
                setAttemptsRemaining(6);
            }
        case StateHandshakeSendStart:
            {

//#define UTS_ID
#ifdef UTS_ID

                /*
                 * Get the boys talking.
                 *
                 * We are able to send the UTS "I" command at any time before handshake to ensure
                 * that an Alpha P+ is out there....  It will respond with an ASCII "ABB Alpha,01   "
                 * string....
                 *
                 */

                Transfer.getOutBuffer()[0] = 'I';
                Transfer.setOutCount(1);
                Transfer.setInCountExpected(16);
                Transfer.setInTimeout( 1 );
                Transfer.setCRCFlag(0);               // No CRC machinations
                setCurrentState (StateHandshakeDecodeStart);

#else

                // go straight to identify state
                Transfer.setOutCount(0);
                Transfer.setInCountExpected(0);
                setCurrentState (StateHandshakeDecodeAttn);

//         State = HANDSHAKE_WHO_STATE;
#endif
                break;
            }
        case StateHandshakeSendAttn:
            {
                // no attention state in an alpha
                break;
            }
        case StateHandshakeSendIdentify:
            {
                /*
                 * We send a "Who are You?" message to the meter
                 * Meter responds with an ID sequence and a password encryption key
                 * We respond with one of three passwords based upon the desired access.
                 */
                BYTEUSHORT devNum;
                devNum.sh = getIED().getSlaveAddress();
                sprintf((CHAR *)Transfer.getOutBuffer(),
                        "%c%c%c%c%c%c",
                        STX,
                        ALPHA_CMD_WITH_DATA,
                        ALPHA_FUNC_WHO_ARE_YOU,
                        0x00, // pad byte
                        0x01, // length of data
                        devNum.ch[0]);

                Transfer.setOutCount (6);
                Transfer.setInCountExpected (15);
                Transfer.setInTimeout (1);

                // add the CRC and update the outcount
                addCRC (Transfer.getOutBuffer(),Transfer.getOutCount(),true);
                Transfer.setOutCount (Transfer.getOutCount() +2);

                setCurrentState (StateHandshakeDecodeIdentify);
                break;
            }

        case StateHandshakeSendSecurity:
            {
                // build the key
                keyValue.ul = (MAKEULONG ( MAKEUSHORT (Transfer.getInBuffer()[12],
                                                       Transfer.getInBuffer()[11]),
                                           MAKEUSHORT (Transfer.getInBuffer()[10],
                                                       Transfer.getInBuffer()[9])));
                // retrive the password
                char * stopper;
                BYTEULONG flipper;
                flipper.ul = strtoul (getIED().getPassword().data(),&stopper,0x10);
                passWord.ch[0] = flipper.ch[1];
                passWord.ch[1] = flipper.ch[0];
                passWord.ch[2] = flipper.ch[3];
                passWord.ch[3] = flipper.ch[2];

                passwordValue.ul = alphaCrypt(keyValue.ul , passWord.ul );

                sprintf((CHAR *)Transfer.getOutBuffer(),"%c%c%c%c%c%c%c%c%c",
                        STX,
                        ALPHA_CMD_WITH_DATA,
                        ALPHA_FUNC_PASSWORD,
                        PAD,
                        0x04,
                        passwordValue.ch[3],
                        passwordValue.ch[2],
                        passwordValue.ch[1],
                        passwordValue.ch[0]);

                Transfer.setOutCount (9);
                Transfer.setInCountExpected (6);
                Transfer.setInTimeout (1);

                // add the CRC and update the outcount
                addCRC (Transfer.getOutBuffer(),Transfer.getOutCount(),true);
                Transfer.setOutCount (Transfer.getOutCount() +2);
                setCurrentState (StateHandshakeDecodeSecurity);
                break;
            }

        case StateHandshakeSendTerminate:
            {
                // problem with the message, terminate and try again
                sprintf((CHAR *)Transfer.getOutBuffer(),"%c%c", STX, ALPHA_CMD_TERMINATE);
                Transfer.setOutCount (2);
                Transfer.setInCountExpected (0);
                Transfer.setCRCFlag (XFER_ADD_CRC);
                setCurrentState (StateHandshakeDecodeTerminate);
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Invalid state for " << getName() << " (" << __LINE__ << ") " << getCurrentState() << endl;
                }

                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState (StateHandshakeAbort);
                retCode = StateHandshakeAbort;
                break;
            }
    }
    return retCode;
}

INT CtiDeviceAlpha::decodeResponseHandshake (CtiXfer  &Transfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    USHORT            retCode = NORMAL;

    switch (getCurrentState())
    {
        case StateHandshakeDecodeStart:
            {
                if ((Transfer.getInCountActual()) &&
                    strncmp((CHAR *)Transfer.getInBuffer(), "ABB ALPHA,01   \r", (Transfer.getInCountActual())))
                {
                    setAttemptsRemaining (getAttemptsRemaining()-1);

                    // if we're done, abort
                    if (getAttemptsRemaining() > 0)
                        // try again, same command
                        setCurrentState (StateHandshakeSendStart);
                    else
                        setCurrentState (StateHandshakeSendTerminate);
                }
                else
                {
                    // This effectively zero's out the other possibilities
                    setAttemptsRemaining(6);
                    setCurrentState (StateHandshakeSendIdentify);
                }
                break;
            }
        case StateHandshakeDecodeAttn:
            {
                // attention state only when not using "I"
                setAttemptsRemaining(6);
                setCurrentState (StateHandshakeSendIdentify);
                break;
            }
        case StateHandshakeDecodeIdentify:
            {
                int ret_crc;

                // decode message
                if ( commReturnValue ||
                   (Transfer.getInCountActual() != 15 ) ||
                   (ret_crc=checkCRC(Transfer.getInBuffer(),Transfer.getInCountActual())))

                {
                    if (ret_crc)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " CRC error for " << getName() << " while handshaking" << endl;
                        }
                    }
                    Transfer.doTrace(ERRUNKNOWN);
                    setAttemptsRemaining (getAttemptsRemaining()-1);
                    if (getAttemptsRemaining() > 0)
                    {
                        setCurrentState (StateHandshakeSendIdentify);
                        CTISleep(1000);
                    }
                    else
                    {
                        // unknown error, kill session
                        setCurrentState (StateHandshakeSendTerminate);
                    }
                }
                else    // We got a ID/ENC. Key combination
                {
                    setCurrentState (StateHandshakeSendSecurity);
                }
                break;
            }

        case StateHandshakeDecodeSecurity:
            {
                int ret_crc;

                if (decodeAckNak(Transfer.getInBuffer()[2]) ||
                    (Transfer.getInCountActual() != 6) ||
                    (ret_crc=checkCRC(Transfer.getInBuffer(),Transfer.getInCountActual())))

                {
                    if (ret_crc)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " CRC error for " << getName() << " while handshaking" << endl;
                    }

                    setAttemptsRemaining (getAttemptsRemaining()-1);

                    if (getAttemptsRemaining() > 0)
                    {
                        setCurrentState (StateHandshakeSendSecurity);
                    }
                    else
                    {
                        setCurrentState (StateHandshakeSendTerminate);
                    }
                }
                else
                {
                    setAttemptsRemaining (3);

                    // we're ready to go
                    setCurrentState (StateHandshakeComplete);
                }
                break;
            }
        case StateHandshakeDecodeTerminate:
            {
                setCurrentState (StateHandshakeAbort);
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Invalid state for " << getName() << " (" << __LINE__ << ") " << getCurrentState() << endl;
                }
                setCurrentState (StateHandshakeAbort);
                retCode = !NORMAL;
                break;
            }

    }
    return retCode;
}



INT CtiDeviceAlpha::generateCommandTerminate (CtiXfer  &Transfer, RWTPtrSlist< CtiMessage > &traceList )
{
    // Terminate the session
    sprintf((PCHAR)Transfer.getOutBuffer(),"%c%c", STX, ALPHA_CMD_TERMINATE);
    Transfer.setOutCount (2);
    Transfer.setInCountExpected (0);
    Transfer.setInTimeout (1);
    Transfer.setCRCFlag (XFER_ADD_CRC );

    // set our states
    setCurrentState (StateScanDecodeTerminate);
    return NORMAL;
}


BOOL CtiDeviceAlpha::verifyAndAddPointToReturnMsg (LONG   aPointId,
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
            if (insertPointIntoReturnMsg (pData, aReturnMsg))
            {
                validPointFound = TRUE;
            }

        }
    }
    return validPointFound;
}

BOOL CtiDeviceAlpha::insertPointIntoReturnMsg (CtiMessage   *aDataPoint,
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
        delete aDataPoint;
        aDataPoint = NULL;
        retCode = FALSE;
    }
    return retCode;
}

