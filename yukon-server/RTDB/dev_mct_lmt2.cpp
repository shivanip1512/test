/*-----------------------------------------------------------------------------*
*
* File:   dev_mct_lmt2
*
* Date:   6/19/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_mct_lmt2.cpp-arc  $
* REVISION     :  $Revision: 1.23 $
* DATE         :  $Date: 2005/02/25 21:48:32 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include "devicetypes.h"
#include "dev_mct_lmt2.h"
#include "logger.h"
#include "mgr_point.h"
#include "pt_numeric.h"
#include "numstr.h"


set< CtiDLCCommandStore > CtiDeviceMCT_LMT2::_commandStore;


CtiDeviceMCT_LMT2::CtiDeviceMCT_LMT2() {}

CtiDeviceMCT_LMT2::CtiDeviceMCT_LMT2(const CtiDeviceMCT_LMT2& aRef)
{
  *this = aRef;
}

CtiDeviceMCT_LMT2::~CtiDeviceMCT_LMT2() {}

CtiDeviceMCT_LMT2& CtiDeviceMCT_LMT2::operator=(const CtiDeviceMCT_LMT2& aRef)
{
  if(this != &aRef)
  {
     Inherited::operator=(aRef);
  }
  return *this;
}


bool CtiDeviceMCT_LMT2::initCommandStore()
{
   bool failed = false;

   CtiDLCCommandStore cs;

   cs._cmd     = CtiProtocolEmetcon::Scan_LoadProfile;
   cs._io      = IO_READ;
   cs._funcLen = make_pair( 0,0 );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::PutStatus_ResetOverride;
   cs._io      = IO_WRITE;
   cs._funcLen = make_pair( (int)MCT_LMT2_ResetOverrideFunc, 0 );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::GetStatus_LoadProfile;
   cs._io      = IO_READ;
   cs._funcLen = make_pair( (int)MCT_LMT2_LPStatusPos,
                            (int)MCT_LMT2_LPStatusLen );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::GetConfig_LoadProfileInterval;
   cs._io      = IO_READ;
   cs._funcLen = make_pair( (int)MCT_LMT2_LPIntervalPos,
                            (int)MCT_LMT2_LPIntervalLen );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::PutConfig_LoadProfileInterval;
   cs._io      = IO_WRITE;
   cs._funcLen = make_pair( (int)MCT_Command_LPInt, 0 );
   _commandStore.insert( cs );


   return failed;
}


bool CtiDeviceMCT_LMT2::getOperation( const UINT &cmd, USHORT &function, USHORT &length , USHORT &io)
{
   bool found = false;

   if(_commandStore.empty())  // Must initialize!
   {
      CtiDeviceMCT_LMT2::initCommandStore();
   }

   DLCCommandSet::iterator itr = _commandStore.find(CtiDLCCommandStore(cmd));

   if( itr != _commandStore.end() )    // It's prego!
   {
      CtiDLCCommandStore &cs = *itr;
      function = cs._funcLen.first;          // Copy over the found funcLen pair!
      length = cs._funcLen.second;           // Copy over the found funcLen pair!
      io = cs._io;
      found = true;
   }

   // Look in the parent if not found in the child!
   if(!found)
   {
      found = Inherited::getOperation(cmd, function, length, io);
   }

   return found;
}


ULONG CtiDeviceMCT_LMT2::calcNextLPScanTime( void )
{
    RWTime Now, blockStart, nextTime;
    unsigned long midnightOffset;
    int lpBlockSize, lpDemandRate, lpMaxBlocks;

    CtiPointBase *pPoint = getDevicePointOffsetTypeEqual(1 + MCT_PointOffset_LoadProfileOffset, DemandAccumulatorPointType);

    //  make sure to completely recalculate this every time
    _nextLPScanTime = YUKONEOT;

    if( !_lpIntervalSent )
    {
        //  send load profile interval on the next 5 minute boundary
        _nextLPScanTime = Now.seconds() + 300;

        if( _nextLPScanTime % 300 )
        {
            _nextLPScanTime -= _nextLPScanTime % 300;
        }
    }
    else if( pPoint && getLoadProfile().isChannelValid(0) )
    {
        //  we read 6 intervals at a time
        lpDemandRate = getLoadProfile().getLoadProfileDemandRate();
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

        blockStart   = getLastLPTime();

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
        nextTime  = blockStart + lpBlockSize;
        //  also add on time for it to move out of the memory we're requesting
        nextTime += LPBlockEvacuationTime;

        //  if we're overdue
        while( (nextTime <= (Now - MCT_LPWindow)) ||
               (nextTime <= _lastLPRequest) )
        {
            nextTime += getLPRetryRate(lpDemandRate);
        }

        _nextLPScanTime = nextTime.seconds();
    }

    return _nextLPScanTime;
}


INT CtiDeviceMCT_LMT2::calcAndInsertLPRequests(OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList)
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

        if( useScanFlags() )
        {
            if( _nextLPScanTime <= Now )
            {
                tmpOutMess = CTIDBG_new OUTMESS(*OutMessage);

                lpBlockStartTime = getLastLPTime();

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

                lpDescriptorString = RWCString(" block ") + CtiNumStr(lpBlockAddress+1);

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
                _lastLPRequest = Now;
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

    if( OutMessage != NULL )
    {
        delete OutMessage;
        OutMessage = NULL;
    }

    return nRet;
}


bool CtiDeviceMCT_LMT2::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
{
    bool retVal = false;
    int lpBlockAddress;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "parse.getiValue(\"scan_loadprofile_block\",   0) = " << parse.getiValue("scan_loadprofile_block", 0) << endl;
    }

    if( lpBlockAddress = parse.getiValue("scan_loadprofile_block", 0) )
    {
        lpBlockAddress--;  //  adjust to be a zero-based offset

        lpBlockAddress *= 12;

        lpBlockAddress += 0x9A;

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


INT CtiDeviceMCT_LMT2::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    switch(InMessage->Sequence)
    {
        case (CtiProtocolEmetcon::Scan_LoadProfile):
        {
            status = decodeScanLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (CtiProtocolEmetcon::GetStatus_Internal):
        {
            status = decodeGetStatusInternal(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (CtiProtocolEmetcon::GetStatus_LoadProfile):
        {
            status = decodeGetStatusLoadProfile(InMessage, TimeNow, vgList, retList, outList);
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


INT CtiDeviceMCT_LMT2::decodeScanLoadProfile(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int status = NORMAL;

    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    RWCString val_report, result_string;

    int     demand_rate, block_size, max_blocks;
    int     current_block_num, retrieved_block_num, midnight_offset;
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

        if( (retrieved_block_num = parse.getiValue("scan_loadprofile_block",   0)) )
        {
            if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - retrieved_block_num " << retrieved_block_num << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

            point = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( 1 + MCT_PointOffset_LoadProfileOffset, DemandAccumulatorPointType );

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
                else if( retrieved_block_start < getLastLPTime() )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - load profile debug for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << InMessage->Return.CommandStr << endl;
                        dout << "retrieved_block_num = " << retrieved_block_num << endl;
                        dout << "retrieved_block_start = " << retrieved_block_start << endl;
                        dout << "lastLPTime = " << getLastLPTime()  << endl;
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

                    setLastLPTime(timestamp);
                }
            }
            else
            {
                result_string = "No load profile point defined for '" + getName() + "'";
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


INT CtiDeviceMCT_LMT2::decodeGetStatusInternal( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT status = NORMAL;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    unsigned char *geneBuf = InMessage->Buffer.DSt.Message;

    ULONG pulseCount = 0;
    RWCString resultString;

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

        resultString  = getName() + " / Internal Status:\n";

        if( geneBuf[4] )        resultString += "  Remote Override in effect\n";

        if( geneBuf[2] )        resultString += "  Waiting for Time Sync\n";
        else                    resultString += "  In Time Sync\n";

        if( geneBuf[5] )        resultString += "  Load Survey Active\n";
        else                    resultString += "  Load Survey Halted\n";

        if( geneBuf[6] & 0x01 ) resultString += "  Reading Overflow\n";
        if( geneBuf[6] & 0x02 ) resultString += "  Long Power Fail\n";
        if( geneBuf[6] & 0x04 ) resultString += "  Short Power Fail/Reset\n";
        if( geneBuf[6] & 0x08 ) resultString += "  Tamper latched\n";
        if( geneBuf[6] & 0x10 ) resultString += "  Self Test Error\n";

        if( geneBuf[7] & 0x01 ) resultString += "  NovRam Fault\n";
        if( geneBuf[7] & 0x02 ) resultString += "  \n";
        if( geneBuf[7] & 0x04 ) resultString += "  Bad opcode\n";
        if( geneBuf[7] & 0x08 ) resultString += "  Power Fail Detected By Hardware\n";
        if( geneBuf[7] & 0x10 ) resultString += "  Deadman/Watchdog Reset\n";
        if( geneBuf[7] & 0x20 ) resultString += "  Software Interrupt (malfunction)\n";
        if( geneBuf[7] & 0x40 ) resultString += "  NM1 Interrupt (malfunction)\n";
        if( geneBuf[7] & 0x80 ) resultString += "  \n";

        if( geneBuf[8] & 0x40 ) resultString += "  Cold Load Flag\n";
        if( geneBuf[8] & 0x80 ) resultString += "  Frequency Fault\n";

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT_LMT2::decodeGetStatusLoadProfile( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT status = NORMAL;

    CtiReturnMsg *ReturnMsg = NULL;
    RWCString resultString;

    DSTRUCT *DSt = &InMessage->Buffer.DSt;

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

        resultString  = getName() + " / Load Survey Control Parameters:\n";
        resultString += "Demand Interval : " + CtiNumStr((int)(DSt->Message[2] * 5)) + "\n";
        resultString += "Current Interval: " + CtiNumStr((int)((DSt->Message[4] / 2) + 1)) + "\n";
        resultString += "Current Value   : " + CtiNumStr((int)((DSt->Message[0] << 8) + DSt->Message[1])) + "\n";

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT_LMT2::decodeGetConfigModel(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
   INT status = NORMAL;

   INT ErrReturn  = InMessage->EventCode & 0x3fff;
   DSTRUCT *DSt   = &InMessage->Buffer.DSt;

   //  ACH:  are these necessary?  /mskf
   resetScanFreezePending();
   resetScanFreezeFailed();

   if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
   {
      // No error occured, we must do a real decode!

      INT ssp;
      char rev;

      RWCString sspec;
      RWCString options("Options:\n");

      CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

      ssp = InMessage->Buffer.DSt.Message[0];
      rev = 64 + InMessage->Buffer.DSt.Message[1];

      sspec = "\nSoftware Specification " + CtiNumStr(ssp) + "  Rom Revision " + RWCString(rev) + "\n";

      if(InMessage->Buffer.DSt.Message[2] & 0x01)
      {
         options+= RWCString("  Latched relay\n");
      }
      else
      {
         options+= RWCString("  No latched relay\n");
      }

      if(InMessage->Buffer.DSt.Message[2] & 0x04)
      {
         options+= RWCString("  No encoding meter\n");
      }
      else
      {
         options+= RWCString("  Encoding meter\n");
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



