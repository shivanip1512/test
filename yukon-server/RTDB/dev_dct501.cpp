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
* REVISION     :  $Revision: 1.20 $
* DATE         :  $Date: 2005/02/10 23:23:59 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include <windows.h>
#include "device.h"
#include "devicetypes.h"
#include "dev_dct501.h"
#include "logger.h"
#include "mgr_point.h"
#include "porter.h"
#include "pt_numeric.h"
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
    RWTime        Now, channelTime, blockStart;
    unsigned long midnightOffset;
    int           lpBlockSize, lpDemandRate, lpMaxBlocks;

    //  make sure to completely reset it every time we recalculate
    _nextLPScanTime = YUKONEOT;

    if( !_lpIntervalSent )
    {
        //  send load profile interval on the next 5 minute boundary
        _nextLPScanTime = (Now.seconds() - MCT_LPWindow) + 300;

        if( _nextLPScanTime % 300 )
        {
            _nextLPScanTime -= _nextLPScanTime % 300;
        }
    }
    else
    {
        lpDemandRate = getLoadProfile().getLoadProfileDemandRate();
        //  we read 6 intervals at a time - it's all the function reads will allow
        lpBlockSize  = lpDemandRate * 6;

        //  if we collect hour data, we only keep a day;  anything less, we keep 48 intervals
        if( lpDemandRate == 3600 )
        {
            lpMaxBlocks = 4;
        }
        else
        {
            lpMaxBlocks = 8;
        }

        for( int i = 0; i < DCT_LPChannels; i++ )
        {
            CtiPointBase *pPoint = getDevicePointOffsetTypeEqual((i+1) + OFFSET_LOADPROFILE_OFFSET, DemandAccumulatorPointType);

            //  safe default
            _nextLPTime[i] = YUKONEOT;

            //  if there's no point defined or we're not collecting load profile, don't scan
            if( pPoint && getLoadProfile().isChannelValid(i) )
            {
                //  uninitialized
                if( !_lastLPTime[i].seconds() )
                {
                    _nextLPTime[i]    = 86400;  //  safe defaults
                    _lastLPTime[i]    = 86400;  //
                    _lastLPRequest[i] = Now - 86400;

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
                        RWTime tmpTime;

                        rdr >> tmpTime;

                        if( tmpTime.seconds() )  //  make sure it's not zero - causes problems with time zones
                        {
                            _lastLPTime[i] = tmpTime;
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "**** Checkpoint: Invalid Reader **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                }

                blockStart = _lastLPTime[i];

                if( blockStart < (Now - ((lpMaxBlocks - 1) * lpBlockSize)) )
                {
                    blockStart = Now - ((lpMaxBlocks - 1) * lpBlockSize);
                }

                //  figure out seconds from midnight
                midnightOffset  = blockStart.hour() * 3600;
                midnightOffset += blockStart.minute() * 60;
                midnightOffset += blockStart.second();

                //  make sure we're actually at the beginning of a block
                blockStart -= midnightOffset % lpBlockSize;

                //  we can only request in blocks, so we plan to request LP data
                //    after one block (6 intervals) has passed
                _nextLPTime[i]  = blockStart + lpBlockSize;
                //  also add on time for it to move out of the memory we're requesting
                _nextLPTime[i] += LPBlockEvacuationTime;

                //  if we're overdue
                while( (_nextLPTime[i] <= (Now - MCT_LPWindow)) ||
                       (_nextLPTime[i] <= _lastLPRequest[i]) )
                {
                    _nextLPTime[i] += getLPRetryRate(lpDemandRate);
                }
            }

            //  if we're sooner than the next-closest scan
            if( _nextLPScanTime > _nextLPTime[i].seconds() )
            {
                _nextLPScanTime = _nextLPTime[i].seconds();
            }
        }
    }

#if 0
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << getName() << "'s next Load Profile request at " << RWTime(nextTime) << endl;
    }
#endif

    return _nextLPScanTime;
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

        //  if we collect hour data, we only keep a day;  anything less, we keep 48 intervals
        if( lpDemandRate == 3600 )
            lpMaxBlocks = 4;
        else
            lpMaxBlocks = 8;

        for( int i = 0; i < DCT_LPChannels; i++ )
        {
            if( useScanFlags() )
            {
                if( _nextLPTime[i] <= Now )
                {
                    tmpOutMess = CTIDBG_new OUTMESS(*OutMessage);

                    lpBlockStartTime = _lastLPTime[i];

                    lpBlocksToCollect = (Now.seconds() - lpBlockStartTime.seconds()) / lpBlockSize;

                    //  make sure we only ask for what remains in memory
                    if( lpBlocksToCollect >= lpMaxBlocks )
                    {
                        //  start with everything but the current block
                        lpBlockStartTime  = Now.seconds();
                        lpBlockStartTime -= (lpMaxBlocks - 1) * lpBlockSize;
                    }

                    //  figure out seconds from midnight
                    lpMidnightOffset  = lpBlockStartTime.hour() * 3600;
                    lpMidnightOffset += lpBlockStartTime.minute() * 60;
                    lpMidnightOffset += lpBlockStartTime.second();

                    //  make sure our reported "start time" is at the beginning of a block
                    lpMidnightOffset -= lpMidnightOffset % lpBlockSize;

                    //  which block to grab?
                    lpBlockAddress  = lpMidnightOffset / lpBlockSize;
                    //  adjust for wraparound
                    lpBlockAddress %= lpMaxBlocks;

                    lpDescriptorString = RWCString(" channel ") + CtiNumStr(i+1) +
                                         RWCString(" block ") + CtiNumStr(lpBlockAddress+1);

                    strncat( tmpOutMess->Request.CommandStr,
                             lpDescriptorString.data(),
                             sizeof(tmpOutMess->Request.CommandStr) - strlen(tmpOutMess->Request.CommandStr));

                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS  );
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - command string check for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "\"" << tmpOutMess->Request.CommandStr << "\"" << endl;
                    }

                    outList.insert(tmpOutMess);
                    _lastLPRequest[i] = Now;
                }
                else
                {
                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - LP scan too early for device \"" << getName() << "\", aborted **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    setMCTScanPending(ScanRateIntegrity, false);

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
    int status = NORMAL;

    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    RWCString val_report, result_string;

    int     demand_rate, block_size, max_blocks;
    int     current_block_num, retrieved_block_num, retrieved_channel, midnight_offset;
    bool    bad_data = false;
    double  value;
    unsigned long pulses, timestamp, current_block_start, retrieved_block_start;
    PointQuality_t quality;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    CtiPointNumeric *point      = 0;
    CtiReturnMsg    *return_msg = 0;  // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *point_data = 0;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Load Profile Scan Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    //  ACH:  are these necessary?  /mskf
    resetScanFreezePending();
    resetScanFreezeFailed();

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((return_msg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        return_msg->setUserMessageId(InMessage->Return.UserID);

        if( (retrieved_channel   = parse.getiValue("scan_loadprofile_channel", 0)) &&
            (retrieved_block_num = parse.getiValue("scan_loadprofile_block",   0)) )
        {
            if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - retrieved_channel " << retrieved_channel << ", retrieved_block_num " << retrieved_block_num << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            retrieved_block_num--;

            demand_rate = getLoadProfile().getLoadProfileDemandRate();
            block_size  = demand_rate * 6;

            //  if we collect hour data, we only keep a day;  anything less, we keep 48 intervals
            if( demand_rate == 3600 )
            {
                max_blocks = 4;
            }
            else
            {
                max_blocks = 8;
            }

            point = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( retrieved_channel + OFFSET_LOADPROFILE_OFFSET, DemandAccumulatorPointType );

            if( point != NULL )
            {
                //  figure out current seconds from midnight
                midnight_offset  = TimeNow.hour() * 3600;
                midnight_offset += TimeNow.minute() * 60;
                midnight_offset += TimeNow.second();

                //  make sure the alignment is correct
                current_block_start  = TimeNow.seconds();
                current_block_start -= midnight_offset % block_size;
                midnight_offset     -= midnight_offset % block_size;

                current_block_num    = midnight_offset / block_size;
                current_block_num   %= max_blocks;

                //  work backwards to find the retrieved block
                retrieved_block_start  = current_block_start;
                retrieved_block_start -= ((current_block_num + max_blocks - retrieved_block_num) % max_blocks) * block_size;

                if( current_block_num == retrieved_block_num )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - attempt to decode current load profile block for \"" << getName() << "\" - aborting decode **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << InMessage->Return.CommandStr << endl;
                    }

                    result_string = "Attempt to decode current load profile block for \"" + getName() + "\" - aborting decode ";
                }
                else if( retrieved_block_start < _lastLPTime[retrieved_channel - 1] )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - load profile debug for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << InMessage->Return.CommandStr << endl;
                        dout << "retrieved_block_num = " << retrieved_block_num << endl;
                        dout << "retrieved_channel = " << retrieved_channel << endl;
                        dout << "retrieved_block_start = " << retrieved_block_start << endl;
                        dout << "_lastLPTime = " << _lastLPTime[retrieved_channel - 1] << endl;
                    }

                    result_string  = "Block < lastLPTime for device \"" + getName() + "\" - aborting decode";
                }
                else
                {
                    for( int interval_offset = 0; interval_offset < 6; interval_offset++ )
                    {
                        //  error code in the top 5 bits - parsed by checkLoadProfileQuality
                        pulses   = DSt->Message[interval_offset*2];
                        pulses <<= 8;
                        pulses  |= DSt->Message[interval_offset*2 + 1];

                        if( bad_data )  //  load survey was halted - the rest of the data is bad
                        {
                            quality = DeviceFillerQuality;
                            value = 0.0;
                        }
                        else if( checkDemandQuality( pulses, quality, bad_data ) )
                        {
                            value = 0.0;
                        }
                        else
                        {
                            //  if no fatal problems with the quality,
                            //    adjust for the demand interval
                            value = pulses * (3600 / demand_rate);
                            //    and the UOM
                            value = point->computeValueForUOM(value);
                        }

                        point_data = CTIDBG_new CtiPointDataMsg(point->getPointID(),
                                                                value,
                                                                quality,
                                                                DemandAccumulatorPointType,
                                                                "",
                                                                TAG_POINT_LOAD_PROFILE_DATA );

                        //  this is where the block started...
                        timestamp  = retrieved_block_start + (demand_rate * interval_offset);

                        //  but we want interval *ending* times, so add on one more interval
                        timestamp += demand_rate;

                        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - load profile debug for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << "value = " << value << endl;
                            dout << "interval_offset = " << interval_offset << endl;
                            dout << "timestamp = " << RWTime(timestamp) << endl;
                        }

                        point_data->setTime(timestamp);

                        return_msg->insert(point_data);
                    }

                    //  insert a point data message for TDC and the like
                    //    note that timeStamp, Value, and pointQuality are set in the final iteration of the above for loop
                    val_report = getName() + " / " + point->getName() + " = " + CtiNumStr(value, ((CtiPointNumeric *)point)->getPointUnits().getDecimalPlaces());

                    point_data = CTIDBG_new CtiPointDataMsg(point->getPointID(),
                                                            value,
                                                            quality,
                                                            DemandAccumulatorPointType,
                                                            val_report);
                    point_data->setTime(timestamp);
                    return_msg->insert(point_data);

                    _lastLPTime[retrieved_channel - 1] = timestamp;
                }
            }
            else
            {
                result_string = "No load profile point defined for '" + getName() + "' demand accumulator " + CtiNumStr(retrieved_channel);
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - scan_loadprofile tokens not found in command string \"" << InMessage->Return.CommandStr << "\" - cannot proceed with decode, aborting **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            result_string  = "scan_loadprofile tokens not found in command string \"";
            result_string += InMessage->Return.CommandStr;
            result_string += "\" - cannot proceed with decode, aborting";
        }

        return_msg->setResultString(result_string);

        retMsgHandler(InMessage->Return.CommandStr, status, return_msg, vgList, retList);
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

