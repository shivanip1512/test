/*-----------------------------------------------------------------------------*
*
* File:   dev_schlum
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_schlum.cpp-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2005/02/10 23:24:00 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include <rw/rwtime.h>
#include <rw/rwdate.h>

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

#include "dialup.h"

#include "logger.h"
#include "guard.h"
#include "utility.h"

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

ULONG CtiDeviceSchlumberger::bytesToBase10(UCHAR* buffer, ULONG len)
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

LONG CtiDeviceSchlumberger::nibblesAndBits(BYTE* bptr, INT MaxChannel, INT Channel, INT Interval)
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
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << "*** ERROR " << __FILE__ << __LINE__ << endl;
                }
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
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << "*** ERROR " << __FILE__ << __LINE__ << endl;
                }
                lRet = 0;
                break;
            }
    }
#else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << "*** ERROR " << __FILE__ << __LINE__ << endl;
    }
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
    aTransfer.setCRCFlag( XFER_ADD_CRC | XFER_VERIFY_CRC );
    aTransfer.setInTimeout( 2 );

    temp.ul = MeterSt.Start;

    aTransfer.getOutBuffer()[1] = temp.ch[2];
    aTransfer.getOutBuffer()[2] = temp.ch[1];
    aTransfer.getOutBuffer()[3] = temp.ch[0];

    temp.ul = MeterSt.Stop;

    aTransfer.getOutBuffer()[4] = temp.ch[2];
    aTransfer.getOutBuffer()[5] = temp.ch[1];
    aTransfer.getOutBuffer()[6] = temp.ch[0];

    return NORMAL;
}


INT CtiDeviceSchlumberger::checkReturnMsg(CtiXfer  &Transfer,
                                          INT       commReturnValue)
{
    int               retCode    = NORMAL;

    if (commReturnValue || Transfer.getInBuffer()[0] == NAK)
    {
        // DEBUG DLS may be able to move this outside this function to make
        // simplier
        if (commReturnValue == BADCRC)
        {
            // DEBUG DLS guy may have to live outside this function to work
            setCRCErrors (getCRCErrors()+1);
            if (getCRCErrors() > 20)
            {
                setCRCErrors (0);
                setCurrentState (StateScanAbort);
                setAttemptsRemaining (0);
                retCode = StateScanAbort;
            }

            if (Transfer.doTrace(BADCRC))
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << "Data failed CRC verification" << endl;
            }

        }
        else if (Transfer.getInBuffer()[0] == NAK)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << "NAK on receive " << getName() << " tries left " << getAttemptsRemaining() << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << "Comm error " << getName() << " tries left " << getAttemptsRemaining() << endl;
        }

        setAttemptsRemaining(getAttemptsRemaining()-1);


        if (getAttemptsRemaining() <= 0)
        {
            setCurrentState (StateScanAbort);
            retCode = StateScanAbort;
        }
        else
        {
            // do this all again
            setPreviousState (getCurrentState());
            setCurrentState (StateScanResendRequest);
            retCode = SCHLUM_RESEND_CMD;
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
            retCode = StateScanAbort;
        }
        else
            retCode = NORMAL;
    }
    return retCode;
}


INT CtiDeviceSchlumberger::GeneralScan(CtiRequestMsg *pReq,
                                       CtiCommandParser &parse,
                                       OUTMESS *&OutMessage,
                                       RWTPtrSlist< CtiMessage > &vgList,
                                       RWTPtrSlist< CtiMessage > &retList,
                                       RWTPtrSlist< OUTMESS > &outList,
                                       INT ScanPriority)
{
    INT status = NORMAL;

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
        OutMessage->DeviceID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();

        // if this is a slave, drop the priority
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

BOOL CtiDeviceSchlumberger::verifyAndAddPointToReturnMsg (LONG          aPointId,
                                                          DOUBLE        aValue,
                                                          USHORT        aQuality,
                                                          RWTime        aTime,
                                                          CtiReturnMsg *aReturnMsg,
                                                          USHORT        aIntervalType,
                                                          RWCString     aValReport)

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

BOOL CtiDeviceSchlumberger::insertPointIntoReturnMsg (CtiMessage   *aDataPoint,
                                                      CtiReturnMsg *aReturnMsg)
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
            dout << RWTime() << "ERROR: " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        delete aDataPoint;
        aDataPoint = NULL;
        retCode = FALSE;
    }
    return retCode;
}

INT CtiDeviceSchlumberger::ResultDecode(INMESS *InMessage,
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
    char tmpCurrentCommand = InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[0];
    char tmpCurrentState   = InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1];

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
                    dout << RWTime() << " (" << __LINE__ << ") *** ERROR *** Invalid decode for " << getName() << endl;
                }

            }
    }

    return NORMAL;
}

INT CtiDeviceSchlumberger::ErrorDecode (INMESS *InMessage,
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
    return NORMAL;
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
_retryAttempts( SCHLUMBERGER_RETRIES )
{
}

CtiDeviceSchlumberger::CtiDeviceSchlumberger(const CtiDeviceSchlumberger& aRef)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "copy constructor is invalid for this device" << endl;
    }
//      *this = aRef;
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

// need a copy constructor

CtiDeviceSchlumberger& CtiDeviceSchlumberger::operator=(const CtiDeviceSchlumberger& aRef)
{
   // DEBUG DLS    must come back to this !!!!!!!!!!!!!!!!

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << endl << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Running equality operator on a Schlumberger" << endl << endl;
    }

   if(this != &aRef)
   {
//         if (_dataBuffer != NULL)
//         {
//            aRef._dataBuffer = ((BYTE *) CTIDBG_new BYTE (sizeof (FulcrumScanData_t)));
//            memcpy (&aRef._dataBuffer, &_dataBuffer, sizeof (FulcrumScanData_t));
//         }
//         else
//            aRef._dataBuffer = NULL;

      Inherited::operator=(aRef);
   }
   return *this;
}

