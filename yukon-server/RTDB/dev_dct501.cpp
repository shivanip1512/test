/*-----------------------------------------------------------------------------*
*
* File:   dev_dct501
*
* Date:   2002-feb-19
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_dct501.cpp-arc  $
* REVISION     :  $Revision: 1.17 $
* DATE         :  $Date: 2004/01/06 20:28:29 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)


#include <windows.h>
#include "device.h"
#include "devicetypes.h"
#include "dev_dct501.h"
#include "logger.h"
#include "mgr_point.h"
#include "porter.h"
#include "pt_numeric.h"
#include "yukon.h"
#include "numstr.h"

set< CtiDLCCommandStore > CtiDeviceDCT501::_commandStore;


CtiDeviceDCT501::CtiDeviceDCT501( ) {}

CtiDeviceDCT501::CtiDeviceDCT501( const CtiDeviceDCT501 &aRef )
{
    *this = aRef;
}

CtiDeviceDCT501::~CtiDeviceDCT501( ) {}

CtiDeviceDCT501& CtiDeviceDCT501::operator=(const CtiDeviceDCT501& aRef)
{
    if( this != &aRef )
    {
        Inherited::operator=( aRef );
    }

    return *this;
}


bool CtiDeviceDCT501::initCommandStore()
{
    bool failed = false;

    CtiDLCCommandStore cs;

    cs._cmd     = CtiProtocolEmetcon::Scan_Integrity;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)DCT_AnalogsPos,
                             (int)DCT_AnalogsLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetValue_Demand;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)DCT_AnalogsPos,
                             (int)DCT_AnalogsLen );
    _commandStore.insert( cs );

//    cs._cmd     = CtiProtocolEmetcon::PutConfig_LoadProfileInterval;
//    cs._io      = IO_WRITE;
//    cs._funcLen = make_pair( (int)MCT_LPInt_Func, 0 );
//    _commandStore.insert( cs );

    return failed;
}


bool CtiDeviceDCT501::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    if(_commandStore.empty())  // Must initialize!
    {
        CtiDeviceDCT501::initCommandStore();
    }

    DLCCommandSet::iterator itr = _commandStore.find(CtiDLCCommandStore(cmd));

    if( itr != _commandStore.end() )
    {
        CtiDLCCommandStore &cs = *itr;
        function = cs._funcLen.first;             // Copy over the found function!
        length = cs._funcLen.second;              // Copy over the found length!
        io = cs._io;                              // Copy over the found io indicator!

        found = true;
    }
    else                                         // Look in the parent if not found in the child!
    {
        found = Inherited::getOperation(cmd, function, length, io);
    }

    return found;
}


ULONG CtiDeviceDCT501::calcNextLPScanTime( void )
{
    RWTime        Now,
    blockStart,
    plannedLPTime,
    panicLPTime;
    unsigned long channelTime,
    nextTime,
    midnightOffset;
    int lpBlockSize, lpDemandRate, lpMaxBlocks;

    nextTime = YUKONEOT;

    if( !_lpIntervalSent )
    {
        //  send load profile interval on the next 5 minute boundary
        nextTime = Now.seconds() + 300;
        if( nextTime % 300 )
        {
            nextTime -= nextTime % 300;
        }
    }
    else
    {
        lpDemandRate = getLoadProfile().getLoadProfileDemandRate();
        //  we read 6 intervals at a time - it's all the reads will allow
        lpBlockSize  = lpDemandRate * 6;

        //  DCT is quite limited, and only keeps 12 intervals per channel
        lpMaxBlocks = 2;

        for( int i = 0; i < 4; i++ )
        {
            //  if we're not collecting load profile, don't scan
            if( !getLoadProfile().isChannelValid(i) )
            {
                _nextLPTime[i] = YUKONEOT;
                continue;
            }

            //  the only way for this to have happened is if they haven't been initialized yet...
            //    both were set to ~Now() at the constructor
            if( (_lastLPRequestAttempt[i].seconds() - _lastLPRequestBlockStart[i].seconds()) < 5 ||
                (_lastLPRequestBlockStart[i].seconds() - _lastLPRequestAttempt[i].seconds()) < 5 )
            {
                //  so we haven't talked to it yet
                _lastLPRequestAttempt[i] = Now;
                //  and the last block we requested was sometime back in 1901
                _lastLPRequestBlockStart[i] = (unsigned long)86400;
                _nextLPTime[i] = Now;

                CtiPointBase *pPoint = getDevicePointOffsetTypeEqual((i+1) + OFFSET_LOADPROFILE_OFFSET, DemandAccumulatorPointType);

                if( pPoint != NULL )
                {
                    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                    RWDBConnection conn = getConnection();

                    RWCString sql       = "select max(timestamp) as maxtimestamp from rawpointhistory where pointid=" + CtiNumStr(pPoint->getPointID());
                    RWDBResult results  = conn.executeSql( sql );
                    RWDBTable  resTable = results.table();

                    if(!results.isValid())
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** ERROR **** RWDB Error #" << results.status().errorCode() << " in query:" << endl;
                        dout << sql << endl << endl;
                    }

                    RWDBReader rdr = resTable.reader();

                    if(rdr() && rdr.isValid())
                    {
                        rdr >> _lastLPTime[i];

                        if( !(_lastLPTime[i].seconds()) )  //  make sure it's not zero - causes problems with time zones
                        {
                            _lastLPTime[i] = (unsigned long)86400;
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "**** Checkpoint: Invalid Reader **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        //  so assume we haven't collected any data, start from scratch
                        _lastLPTime[i] = (unsigned long)86400;
                    }
                }
                else
                {
                    //  no point in DB, don't request LP data for this channel
                    continue;
                }
            }

            blockStart = _lastLPTime[i];

            //  figure out seconds from midnight
            midnightOffset  = blockStart.hour() * 3600;
            midnightOffset += blockStart.minute() * 60;
            midnightOffset += blockStart.second();

            //  make sure we're actually at the beginning of a block
            blockStart -= midnightOffset % lpBlockSize;

            //  we can only request in blocks, so we plan to request LP data
            //    after one block (6 intervals) has passed
            plannedLPTime  = blockStart + lpBlockSize;
            //  also make sure we allow time for it to move out of the memory we're requesting
            plannedLPTime += LPBlockEvacuationTime;

            //  if we're still on schedule for our normal request
            //    (and we haven't already made our request for this block)
            if( _lastLPRequestAttempt[i] < plannedLPTime &&
                _lastLPRequestBlockStart[i] < blockStart )
            {
                channelTime = plannedLPTime.seconds();
            }
            else
            {
                unsigned int overdueLPRetryRate = getLPRetryRate(lpDemandRate);

                channelTime  = (Now.seconds() - LPBlockEvacuationTime) + overdueLPRetryRate;
                channelTime -= (Now.seconds() - LPBlockEvacuationTime) % overdueLPRetryRate;

                channelTime += LPBlockEvacuationTime;
            }

            _nextLPTime[i] = channelTime;

            if( channelTime < nextTime )
                nextTime = channelTime;
        }
    }

    return(_nextLPScanTime = nextTime);
}


INT CtiDeviceDCT501::calcAndInsertLPRequests(OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList)
{
    int nRet = NoError;

    int            lpDemandRate;
    unsigned int   lpBlockAddress;
    unsigned long  lpBlocksToCollect,
                   lpMaxBlocks,
                   lpBlockSize,
                   lpMidnightOffset;
    RWTime         lpBlockStartTime;
    RWTime         Now;
    OUTMESS       *tmpOutMess;
    RWCString      lpDescriptorString;
    int            lpChannel;

    lpDemandRate     = getLoadProfile().getLoadProfileDemandRate();

    if( !_lpIntervalSent )
    {
        sendLPInterval( OutMessage, outList );
    }
    else
    {
        //  we can read 6 intervals at a time
        lpBlockSize = lpDemandRate * 6;

        for( int i = 0; i < 4; i++ )
        {
            if( useScanFlags() )
            {
                if( _nextLPTime[i] <= Now )
                {
                    tmpOutMess = CTIDBG_new OUTMESS(*OutMessage);

                    lpBlockStartTime = _lastLPTime[i];

                    lpBlocksToCollect = (Now.seconds() - lpBlockStartTime.seconds()) / lpBlockSize;

                    //  make sure we don't ask for more than the one block
                    //    (the "other" block that's not being written to)
                    if( lpBlocksToCollect > 1 )
                    {
                        //  offset one block into the past
                        lpBlockStartTime = Now.seconds() - lpBlockSize;
                    }

                    //  figure out seconds from midnight
                    lpMidnightOffset  = lpBlockStartTime.hour() * 3600;
                    lpMidnightOffset += lpBlockStartTime.minute() * 60;
                    lpMidnightOffset += lpBlockStartTime.second();

                    //  make sure our reported "start time" is at the beginning of a block
                    lpBlockStartTime -= lpMidnightOffset % lpBlockSize;
                    lpMidnightOffset -= lpMidnightOffset % lpBlockSize;

                    _lastLPRequestBlockStart[i] = lpBlockStartTime;
                    _lastLPRequestAttempt[i]    = Now;

                    //  adjust for wraparound
                    lpBlockAddress = lpMidnightOffset % (lpBlockSize * 2);  //  only has 12 (= 2 * 6) blocks for each channel

                    //  which block to grab?
                    lpBlockAddress /= lpBlockSize;

                    lpDescriptorString = RWCString(" channel ") + CtiNumStr(i+1) +
                                         RWCString(" block ") + CtiNumStr(lpBlockAddress+1);

                    strncat( tmpOutMess->Request.CommandStr,
                             lpDescriptorString.data(),
                             sizeof(tmpOutMess->Request.CommandStr) - strlen(tmpOutMess->Request.CommandStr));
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "\"" << OutMessage->Request.CommandStr << "\"" << endl;
                    }

                    outList.insert(tmpOutMess);
                }
                else
                {
                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "LP scan too early." << endl;
                    }
                }
            }
        }
    }

    if( OutMessage != NULL )
    {
        delete OutMessage;
        OutMessage = NULL;
    }

    return nRet;
}


bool CtiDeviceDCT501::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
{
    bool retVal = false;
    int lpBlockAddress, lpChannel;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "parse.getiValue(\"scan_loadprofile_block\",   0) = " << parse.getiValue("scan_loadprofile_block", 0) << endl;
        dout << "parse.getiValue(\"scan_loadprofile_channel\", 0) = " << parse.getiValue("scan_loadprofile_channel", 0) << endl;
    }

    if( (lpBlockAddress = parse.getiValue("scan_loadprofile_block",   0)) &&
        (lpChannel      = parse.getiValue("scan_loadprofile_channel", 0)) )
    {
        lpChannel--;
        lpBlockAddress--;  //  adjust to be a zero-based offset

        lpBlockAddress *= 12;

        switch( lpChannel )
        {
            case 3:     lpBlockAddress += 0x18;  //  add on the appropriate offset for the requested channel
            case 2:     lpBlockAddress += 0x18;
            case 1:     lpBlockAddress += 0x18;
                //  all of the above fall through to this:
            case 0:     lpBlockAddress += 0x9a;  //  offset for first channel
        }

        OutMessage->Buffer.BSt.Function = lpBlockAddress;
        OutMessage->Buffer.BSt.Length   = 12;  //  2 bytes per interval
        OutMessage->Buffer.BSt.IO       = IO_READ;

        retVal = true;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Improperly formed LP request discarded for \"" << getName() << "\"." << endl;
        }

        retVal = false;
    }

    return retVal;
}

/*
 *  ResultDecode MUST decode all CtiDLCCommand_t which are defined in the initCommandStore object.  The only exception to this
 *  would be a child whose decode was identical to the parent, but whose request was done differently..
 *  This MAY be the case for example in an IED scan.
 */
INT CtiDeviceDCT501::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;


    switch(InMessage->Sequence)
    {
    case (CtiProtocolEmetcon::Scan_Integrity):
    case (CtiProtocolEmetcon::GetValue_Demand):
        {
            status = decodeGetValueDemand(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

    case (CtiProtocolEmetcon::Scan_LoadProfile):
        {
            status = decodeScanLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

    case (CtiProtocolEmetcon::GetConfig_Model):
        {
            status = decodeGetConfigModel(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

    default:
        {
            status = Inherited::ResultDecode(InMessage, TimeNow, vgList, retList, outList);

            if(status != NORMAL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " IM->Sequence = " << InMessage->Sequence << " " << getName() << endl;
            }
            break;
        }
    }

    return status;
}


INT CtiDeviceDCT501::decodeGetValueDemand(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;
    INT pnt_offset, byte_offset;
    ULONG i,x;
    INT pid;
    INT Error;
    USHORT StatusData[8];
    USHORT SaveCount;
    RWCString resultString;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    DOUBLE Value;
    CtiPointNumeric      *pPoint = NULL;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Demand Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    resetScanPending();

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;
            }

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        for(pnt_offset = 1; pnt_offset <= 4; pnt_offset++)
        {
            byte_offset = (pnt_offset - 1) * 2;

            Error = (DSt->Message[byte_offset] & 0xc0);

            // 2 byte demand value.  Upper 2 bits are error indicators.
            Value = MAKEUSHORT(DSt->Message[byte_offset + 1], (DSt->Message[byte_offset] & 0x3f) );
            //  normalize the range
            Value /= 16000.0;

            /* look for analog points */
            pPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual( pnt_offset, AnalogPointType );

            // handle demand data here
            if( pPoint != NULL)
            {
                // Adjust for the unit of measure!
                Value = pPoint->computeValueForUOM(Value);

                if(Error == 0)
                {
                    resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                             ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
                    pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, resultString);
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    resultString = "Error indicated on DCT " + getName() + " / " + pPoint->getName() + ";  Error " + CtiNumStr(Error);
                    pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, UnknownQuality, AnalogPointType, resultString);
                }

                if(pData != NULL)
                {
                    ReturnMsg->PointData().insert(pData);
                    pData = NULL;  // We just put it on the list...

                    //  clear out any demand input 1 messages we may have appended - we have a real one
                    ReturnMsg->setResultString( RWCString() );
                }
            }
            //  else send the raw pulses for offset 1
            else if( pnt_offset == 1 )
            {
                resultString = ReturnMsg->ResultString( );
                if( resultString != "" )
                    resultString += "\n";

                resultString += getName() + " / Analog 1 " + CtiNumStr(pnt_offset) + " = " + CtiNumStr(Value) + "  --  POINT UNDEFINED IN DB";
                ReturnMsg->setResultString( resultString );
            }
        }
    }


    if(ReturnMsg != NULL)
    {
        if(!(ReturnMsg->ResultString().isNull()) || ReturnMsg->getData().entries() > 0)
        {
            retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
            //  retList.append( ReturnMsg );
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  No demand accumulators are defined in the DB" << endl;
            }
            delete ReturnMsg;
        }
    }

    return status;
}


INT CtiDeviceDCT501::decodeScanLoadProfile(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    RWCString valReport, resultString;
    int       intervalOffset,
    lpDemandRate,
    pointOffset,
    charOffset,
    badData;
    double    Value;
    unsigned long timeStamp, pulses;
    PointQuality_t pointQuality;

    CtiPointNumeric *pPoint    = NULL;
    CtiReturnMsg    *ReturnMsg = NULL;  // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData     = NULL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Load Profile Scan Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    resetScanFreezePending( );
    resetScanFreezeFailed( );

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        charOffset  = strlen(InMessage->Return.CommandStr) - 1;  //  point to the last character in the string
        pointOffset = InMessage->Return.CommandStr[charOffset] - '0';  //  convert to numeric from ASCII

        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "channel " << pointOffset << endl;
        }

        lpDemandRate = getLoadProfile().getLoadProfileDemandRate();

        pPoint = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( 1 + pointOffset + OFFSET_LOADPROFILE_OFFSET, AnalogPointType );

        if( pPoint != NULL )
        {
            badData = FALSE;

            for( intervalOffset = 0; intervalOffset < 6; intervalOffset++ )
            {
                //  error code in the top 5 bits - parsed by checkLoadProfileQuality
                pulses   = DSt->Message[intervalOffset*2];
                pulses <<= 8;
                pulses  |= DSt->Message[intervalOffset*2 + 1];

                if( badData )  //  load survey was halted - the rest of the data is bad
                {
                    pointQuality = DeviceFillerQuality;
                    Value = 0.0;
                }
                else if( !checkLoadProfileQuality( pulses, pointQuality, badData ) )
                {
                    //  if no fatal problems with the quality,
                    //    normalize the range
                    Value  = pulses;
                    Value /= 16000.0;
                    //    and adjust for the UOM
                    Value = pPoint->computeValueForUOM( Value );
                }
                else
                {
                    Value = 0.0;
                }

                pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(),
                                            Value,
                                            pointQuality,
                                            AnalogPointType,
                                            "",
                                            TAG_POINT_LOAD_PROFILE_DATA );

                //  this is where the block started...
                timeStamp  = _lastLPRequestBlockStart[pointOffset].seconds() + (lpDemandRate * intervalOffset);

                //  but we want interval *ending* times, so add on one more interval
                timeStamp += lpDemandRate;

                if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "_lastLPRequestBlockStart[" << pointOffset << "] = " << RWTime(_lastLPRequestBlockStart[pointOffset]) << endl;
                    dout << "Value = " << Value << endl;
                    dout << "intervalOffset = " << intervalOffset << endl;
                    dout << "timeStamp = " << RWTime(timeStamp) << endl;
                }

                pData->setTime( timeStamp );

                ReturnMsg->insert( pData );
            }
            //  insert a point data message for TDC and the like
            //    note that timeStamp, Value, and pointQuality are set in the final iteration of the above for loop
            valReport = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                  ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(),
                                        Value,
                                        pointQuality,
                                        AnalogPointType,
                                        valReport );
            pData->setTime( timeStamp );
            ReturnMsg->insert( pData );

            _lastLPTime[pointOffset] = timeStamp;
        }
        else
        {
            resultString = "No load profile point defined for '" + getName() + "' demand accumulator " + CtiNumStr( pointOffset + 1 );
            ReturnMsg->setResultString( resultString );
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceDCT501::decodeGetConfigModel(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    resetScanFreezePending();
    resetScanFreezeFailed();

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        INT ssp;
        char rev;
        char temp[80];

        RWCString sspec;
        RWCString options("Options:\n");

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi


        ssp = InMessage->Buffer.DSt.Message[4] * 256 + InMessage->Buffer.DSt.Message[0];
        rev = 64 + InMessage->Buffer.DSt.Message[1];

        sspec = "\nSoftware Specification " + CtiNumStr(ssp) + "  Rom Revision " + RWCString::RWCString(rev) + "\n";

        if(InMessage->Buffer.DSt.Message[2] & 0x01)
        {
            options+= RWCString("  Latched loads\n");
        }
        if(InMessage->Buffer.DSt.Message[2] & 0x02)
        {
            options+= RWCString("  Timed loads\n");
        }
        if(InMessage->Buffer.DSt.Message[2] & 0x40)
        {
            options+= RWCString("  Extended addressing\n");
        }
        if(InMessage->Buffer.DSt.Message[2] & 0x80)
        {
            options+= RWCString("  Metering of basic kWh\n");
        }

        if(InMessage->Buffer.DSt.Message[3] & 0x01)
        {
            options+= RWCString("  Time-of-demand\n");
        }
        if(InMessage->Buffer.DSt.Message[3] & 0x04)
        {
            options+= RWCString("  Load survey\n");
        }
        if(InMessage->Buffer.DSt.Message[3] & 0x08)
        {
            options+= RWCString("  Full group address support\n");
        }
        if(InMessage->Buffer.DSt.Message[3] & 0x10)
        {
            options+= RWCString("  Feedback load control\n");
        }
        if(InMessage->Buffer.DSt.Message[3] & 0x40)
        {
            options+= RWCString("  Volt/VAR control\n");
        }
        if(InMessage->Buffer.DSt.Message[3] & 0x80)
        {
            options+= RWCString("  Capacitor control\n");
        }

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;
            }

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString( sspec + options );

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }


    return status;
}

