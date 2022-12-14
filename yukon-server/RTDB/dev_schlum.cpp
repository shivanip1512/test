/*-----------------------------------------------------------------------------*
*
* File:   dev_schlum
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_schlum.cpp-arc  $
* REVISION     :  $Revision: 1.17 $
* DATE         :  $Date: 2008/10/29 20:06:27 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"


#include "porter.h"
#include "dev_schlum.h"

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

using std::string;
using std::endl;
using std::list;

/*
 * Command Char, Command Name, Sent bytes (less CRC), Rec. Bytes (less CRC)
 */

INT CtiDeviceSchlumberger::getRetryAttempts () const
{
    return _retryAttempts;
}

CtiDeviceSchlumberger& CtiDeviceSchlumberger::setRetryAttempts (INT aRetry)
{
    _retryAttempts = aRetry;
    return *this;
}

INT CtiDeviceSchlumberger::getCRCErrors () const
{
    return _CRCErrors;
}

CtiDeviceSchlumberger& CtiDeviceSchlumberger::setCRCErrors (INT aError)
{
    _CRCErrors = aError;
    return *this;
}


SchlMeterStruct CtiDeviceSchlumberger::getMeterParams() const
{
    return _meterParams;
}

CtiDeviceSchlumberger&  CtiDeviceSchlumberger::setMeterParams (SchlMeterStruct &aParamSet)
{
    memcpy (&_meterParams, &aParamSet, sizeof (SchlMeterStruct));
    return *this;
}

ULONG    CtiDeviceSchlumberger::getTotalByteCount() const
{
    return _totalByteCount;
}

CtiDeviceSchlumberger& CtiDeviceSchlumberger::setTotalByteCount(ULONG c)
{
    _totalByteCount = c;
    return *this;
}



ULONG CtiDeviceSchlumberger::previousMassMemoryAddress( ULONG SA,     // Starting Address
                                                        ULONG CA,     // Current Address
                                                        ULONG MA,     // Maximum Address
                                                        ULONG RS)     // Record Size
{
    ULONG PA;



    PA = CA - RS;

    if (PA < SA || CA < RS)
    {
        // move to the last good address, then back one record
        PA = SA + MA -  RS;
    }

    return PA;
}

ULONG CtiDeviceSchlumberger::bytesToBase10(const UCHAR* buffer, ULONG len)
{
    /*
     *  len is the number of bytes to decode from.
     */

    int i, j;
    ULONG temp;
    ULONG scratch = 0;

    for (i = len, j = 0; i > 0; j++, i--)
    {
        temp = 0;

        /* The high nibble */
        temp += (((buffer[j] & 0xf0) >> 4)  * 16);

        /* The Low nibble */
        temp += (buffer[j] & 0x0f);

        scratch = scratch * 256 + temp ;
    }

    return scratch;
}

// This is really the only one Fulcrum uses.
#define BIT_FORMAT_COUNT   12

LONG CtiDeviceSchlumberger::nibblesAndBits(const unsigned char *bptr, INT MaxChannel, INT Channel, INT Interval)
{
    LONG           lRet              = 0;
    INT            BitsPerInterval   = MaxChannel * BIT_FORMAT_COUNT;
    INT            StartBit          = BitsPerInterval * Interval + (Channel * BIT_FORMAT_COUNT);
    INT            StartByte         = StartBit / 8;
    INT            ShiftBits         = StartBit % 8;

#if (BIT_FORMAT_COUNT == 12)

    switch (ShiftBits)
    {
        case 0:
            {
                /* Alignment is A-OK */
                lRet = bptr[StartByte];
                break;
            }
        case 4:
            {
                /* We have 4 bits to fetch from the StartByte location */

                lRet = (bptr[StartByte] & 0x0F);
                break;
            }
        default:
            {
                CTILOG_ERROR(dout, "Invalid ShiftBits = "<< ShiftBits);

                lRet = 0;
                break;
            }
    }

    switch (ShiftBits)
    {
        case 0:
            {
                /* Got the first 8, so Go get last 4 */
                // lRet <<= 4;

                lRet |= ((bptr[StartByte+1] & 0xF0) << 4);
                break;
            }
        case 4:
            {
                /* Got the first 4, so Go get last 8 */
                lRet <<= 8;
                lRet |= (bptr[StartByte+1]);
                break;
            }
        default:
            {
                CTILOG_ERROR(dout, "Invalid ShiftBits = "<< ShiftBits);

                lRet = 0;
                break;
            }
    }
#else
    CTILOG_ERROR(dout, "unexpected function call with BIT_FORMAT_COUNT != 12")
    lRet = 0;
#endif

    // fprintf(stderr,"%01X %01X = Pulses = %ld = 0x%03X  ",bptr[StartByte],bptr[StartByte+1] ,lRet, lRet);

    return lRet;
}


INT CtiDeviceSchlumberger::fillUploadTransferObject (CtiXfer  &aTransfer, ULONG aStart, ULONG aStop)
{
    SchlMeterStruct   MeterSt;
    BYTEULONG         temp;

    // fill the meter struct for the decode routine
    MeterSt.Start = aStart;
    MeterSt.Stop  = aStop;
    MeterSt.Length = MeterSt.Stop - MeterSt.Start + 1;

    setMeterParams (MeterSt);

    // fill the transfer structure accordingly
    memset(aTransfer.getInBuffer(), 0x00, MeterSt.Length);
    aTransfer.getOutBuffer()[0]      ='U';
    aTransfer.getOutCount()          = 7;
    aTransfer.getInCountExpected()   = MeterSt.Length + 3;

    // all schlum messages use the crc
    aTransfer.setCRCFlag( CtiXfer::XFER_ADD_CRC | CtiXfer::XFER_VERIFY_CRC );
    aTransfer.setInTimeout( 2 );

    temp.ul = MeterSt.Start;

    aTransfer.getOutBuffer()[1] = temp.ch[2];
    aTransfer.getOutBuffer()[2] = temp.ch[1];
    aTransfer.getOutBuffer()[3] = temp.ch[0];

    temp.ul = MeterSt.Stop;

    aTransfer.getOutBuffer()[4] = temp.ch[2];
    aTransfer.getOutBuffer()[5] = temp.ch[1];
    aTransfer.getOutBuffer()[6] = temp.ch[0];

    return ClientErrors::None;
}


YukonError_t CtiDeviceSchlumberger::checkReturnMsg(CtiXfer  &Transfer,
                                                   YukonError_t commReturnValue)
{
    YukonError_t retCode    = ClientErrors::None;

    if (commReturnValue || Transfer.getInBuffer()[0] == NAK)
    {
        // DEBUG DLS may be able to move this outside this function to make
        // simplier
        if (commReturnValue == ClientErrors::BadCrc)
        {
            // DEBUG DLS guy may have to live outside this function to work
            setCRCErrors (getCRCErrors()+1);
            if (getCRCErrors() > 20)
            {
                setCRCErrors (0);
                setCurrentState (StateScanAbort);
                setAttemptsRemaining (0);
                retCode = ClientErrors::Abnormal;
            }

            if (Transfer.doTrace(ClientErrors::BadCrc))
            {
                CTILOG_ERROR(dout, "Data failed CRC verification");
            }

        }
        else if (Transfer.getInBuffer()[0] == NAK)
        {
            CTILOG_ERROR(dout, "NAK on receive for " << getName() << ", tries left " << getAttemptsRemaining());
        }
        else
        {
            CTILOG_ERROR(dout, "Comm error " << getName() << ", tries left " << getAttemptsRemaining());
        }

        setAttemptsRemaining(getAttemptsRemaining()-1);


        if (getAttemptsRemaining() <= 0)
        {
            setCurrentState (StateScanAbort);
            retCode = ClientErrors::Abnormal;
        }
        else
        {
            // do this all again
            setPreviousState (getCurrentState());
            setCurrentState (StateScanResendRequest);
            retCode = ClientErrors::RetrySubmitted;
        }
    }
    else // Good Data read
    {
        setCRCErrors (0);

        // make sure something is there
        if (!Transfer.getInBuffer())
        {
            setCurrentState (StateScanAbort);
            setAttemptsRemaining (0);
            retCode = ClientErrors::Abnormal;
        }
        else
            retCode = ClientErrors::None;
    }
    return retCode;
}


YukonError_t CtiDeviceSchlumberger::GeneralScan(CtiRequestMsg     *pReq,
                                                CtiCommandParser  &parse,
                                                OUTMESS          *&OutMessage,
                                                CtiMessageList    &vgList,
                                                CtiMessageList    &retList,
                                                OutMessageList    &outList,
                                                INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;

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
        OutMessage->Buffer.DUPReq.Command[0] = CmdScanData;      // One call does it all...

        OutMessage->Buffer.DUPReq.LP_Time      = getLastLPTime().seconds();
        OutMessage->Buffer.DUPReq.LastFileTime = getLastLPTime().seconds();

        // Load all the other stuff that is needed
        populateRemoteOutMessage(*OutMessage);
        OutMessage->Retry = 3;  //  override

        EstablishOutMessagePriority( OutMessage, ScanPriority );

        // if this is a slave, drop the priority
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

BOOL CtiDeviceSchlumberger::verifyAndAddPointToReturnMsg (LONG          aPointId,
                                                          DOUBLE        aValue,
                                                          USHORT        aQuality,
                                                          CtiTime        aTime,
                                                          CtiReturnMsg *aReturnMsg,
                                                          USHORT        aIntervalType,
                                                          string     aValReport)

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

BOOL CtiDeviceSchlumberger::insertPointIntoReturnMsg (CtiMessage   *aDataPoint,
                                                      CtiReturnMsg *aReturnMsg)
{
    BOOL retCode = TRUE;

    if (aReturnMsg != NULL)
    {
        aReturnMsg->PointData().push_back(aDataPoint);
        aDataPoint = NULL;  // We just put it on the list...
    }
    else
    {
        CTILOG_ERROR(dout, "Invalid return message");

        delete aDataPoint;
        aDataPoint = NULL;
        retCode = FALSE;
    }
    return retCode;
}

YukonError_t CtiDeviceSchlumberger::ResultDecode(const INMESS   &InMessage,
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
    char tmpCurrentCommand = InMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[0];
    char tmpCurrentState   = InMessage.Buffer.DUPSt.DUPRep.ReqSt.Command[1];

    if( !_dstFlagValid )
    {
        _dstFlag = readDSTFile( getName() );
    }

    switch (tmpCurrentCommand)
    {
        case CmdScanData:
            {
                CTILOG_INFO(dout, "Scan decode for device "<< getName() <<" in progress");
                decodeResultScan (InMessage, TimeNow, vgList, retList, outList);
                break;
            }
        case CmdLoadProfileData:
            {
                CTILOG_INFO(dout, "LP decode for device "<< getName() <<" in progress");

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

YukonError_t CtiDeviceSchlumberger::ErrorDecode (const INMESS   &InMessage,
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

INT CtiDeviceSchlumberger::freeDataBins  ()
{
    if (_dataBuffer != NULL)
    {
        delete []_dataBuffer;
        _dataBuffer = NULL;
    }

    if (_massMemoryConfig != NULL)
    {
        // this guy points to a structure inside of the load profile buffer
        _massMemoryConfig = NULL;
    }

    if (_loadProfileTimeDate != NULL)
    {
        delete []_loadProfileTimeDate;
        _loadProfileTimeDate = NULL;
    }

    if (_massMemoryLoadProfile != NULL)
    {
        delete []_massMemoryLoadProfile;
        _massMemoryLoadProfile = NULL;
    }

    if (_massMemoryRequestInputs != NULL)
    {
        delete []_massMemoryRequestInputs;
        _massMemoryRequestInputs = NULL;
    }

    if (_loadProfileBuffer != NULL)
    {
        delete []_loadProfileBuffer;
        _loadProfileBuffer = NULL;
    }

    // re-init everything for next time through
    setTotalByteCount (0);
    setCurrentState (StateHandshakeInitialize);
    return ClientErrors::None;
}

// default constructor that takes 2 optional parameters
CtiDeviceSchlumberger::CtiDeviceSchlumberger ( BYTE         *dataPtr,
                        BYTE         *mmPtr,
                        BYTE         *timeDatePtr,
                        BYTE         *mmlProfilePtr,
                        BYTE         *mmlProfileInputPtr,
                        BYTE         *lProfilePtr,
                        ULONG        totalByteCount) :
_dataBuffer( dataPtr ),
_massMemoryConfig( mmPtr ),
_loadProfileTimeDate( timeDatePtr ),
_massMemoryLoadProfile ( mmlProfilePtr ),
_massMemoryRequestInputs ( mmlProfileInputPtr ),
_loadProfileBuffer ( lProfilePtr ),
_totalByteCount ( totalByteCount ),
_retryAttempts( SCHLUMBERGER_RETRIES ),
_CRCErrors(0)
{
    memset(&_meterParams, 0, sizeof(_meterParams));
}

CtiDeviceSchlumberger::~CtiDeviceSchlumberger()
{
   if(_dataBuffer != NULL)
   {
      delete []_dataBuffer;
      _dataBuffer = NULL;
   }

   if(_massMemoryConfig != NULL)
   {
      // this guy is pointing to a member of loadProfileBuffer
      _massMemoryConfig = NULL;
   }

   if(_loadProfileTimeDate != NULL)
   {
      delete []_loadProfileTimeDate;
      _loadProfileTimeDate = NULL;
   }

   if(_massMemoryLoadProfile != NULL)
   {
      delete []_massMemoryLoadProfile;
      _massMemoryLoadProfile = NULL;
   }

   if(_massMemoryRequestInputs != NULL)
   {
      delete []_massMemoryRequestInputs;
      _massMemoryRequestInputs = NULL;
   }

   if(_loadProfileBuffer != NULL)
   {
      delete []_loadProfileBuffer;
      _loadProfileBuffer = NULL;
   }
}

