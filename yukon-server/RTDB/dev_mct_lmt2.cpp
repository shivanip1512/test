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
* REVISION     :  $Revision: 1.18 $
* DATE         :  $Date: 2004/01/06 20:28:29 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)


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
   cs._funcLen = make_pair( (int)MCT_Function_LPInt, 0 );
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
    RWTime Now, blockStart, plannedLPTime, panicLPTime;
    unsigned long nextTime, midnightOffset;
    int lpBlockSize, lpDemandRate;

    //  if we're not collecting load profile, we never scan
    if( !getLoadProfile().isChannelValid(0) )
        return (_nextLPScanTime = YUKONEOT);

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
        //  the only way for this to have happened is if they haven't been initialized yet...
        //    (i.e. both were set to Now() at the constructor)
        if( _lastLPRequestAttempt == _lastLPRequestBlockStart )
        {
            //  pretend like we just got a block from January 1, 1901 - that will
            //    scare us into fetching stuff immediately
            _lastLPRequestAttempt = Now;
            _lastLPRequestBlockStart = (unsigned long)86400;
        }

        //  we read 6 intervals at a time
        lpDemandRate = getLoadProfile().getLoadProfileDemandRate();
        lpBlockSize  = lpDemandRate * 6;

        blockStart   = getLastLPTime();

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
        if( _lastLPRequestAttempt < plannedLPTime &&
            _lastLPRequestBlockStart < blockStart )
        {
            nextTime = plannedLPTime.seconds();
        }
        //  we're overdue
        else
        {
            unsigned int overdueLPRetryRate = getLPRetryRate(lpDemandRate);

            nextTime  = (Now.seconds() - LPBlockEvacuationTime) + overdueLPRetryRate;
            nextTime -= (Now.seconds() - LPBlockEvacuationTime) % overdueLPRetryRate;

            nextTime += LPBlockEvacuationTime;
        }
    }

    return (_nextLPScanTime = nextTime);
}


INT CtiDeviceMCT_LMT2::calcAndInsertLPRequests(OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList)
{
    int nRet = NoError;

    int           lpDemandRate;
    unsigned int  lpBlockAddress;
    unsigned long lpBlocksToCollect,
                  lpMaxBlocks,
                  lpBlockSize,
                  lpMidnightOffset;
    RWTime        lpBlockStartTime;
    RWTime        Now;
    RWCString     lpDescriptorString;

    lpDemandRate = getLoadProfile().getLoadProfileDemandRate();
    lpBlockSize  = lpDemandRate * 6;

    if( !_lpIntervalSent )
    {
        sendLPInterval( OutMessage, outList );
    }
    else
    {
        if( useScanFlags() )
        {
            lpBlockStartTime = getLastLPTime();

            //  if we collect hour data, we only keep a day;  anything less, we keep 48 intervals
            if( lpDemandRate == 3600 )
                lpMaxBlocks = 4;
            else
                lpMaxBlocks = 8;

            lpBlocksToCollect = (Now.seconds() - lpBlockStartTime.seconds()) / lpBlockSize;

            //  make sure we only ask for what remains in memory
            if( lpBlocksToCollect > (lpMaxBlocks - 1) )
            {
                //  start with everything but the current block
                lpBlockStartTime = Now.seconds() - (lpBlockSize * (lpMaxBlocks - 1));
            }

            //  figure out seconds from midnight
            lpMidnightOffset  = lpBlockStartTime.hour() * 3600;
            lpMidnightOffset += lpBlockStartTime.minute() * 60;
            lpMidnightOffset += lpBlockStartTime.second();

            //  make sure our reported "start time" is at the beginning of a block
            lpBlockStartTime -= lpMidnightOffset % lpBlockSize;
            lpMidnightOffset -= lpMidnightOffset % lpBlockSize;

            if( getNextLPScanTime() <= Now )
            {
                _lastLPRequestBlockStart = lpBlockStartTime;
                _lastLPRequestAttempt    = Now;

                //  adjust for wraparound
                lpBlockAddress = lpMidnightOffset % (lpBlockSize * lpMaxBlocks);

                lpBlockAddress /= lpBlockSize;

                lpDescriptorString = RWCString(" block ") + CtiNumStr(lpBlockAddress+1);

                strncat( OutMessage->Request.CommandStr,
                         lpDescriptorString.data(),
                         sizeof(OutMessage->Request.CommandStr) - strlen(OutMessage->Request.CommandStr));
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "\"" << OutMessage->Request.CommandStr << "\"" << endl;
                }

                outList.insert(OutMessage);
                OutMessage = NULL;
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "LP scan too early." << endl;
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
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    int intervalOffset, lpDemandRate;
    unsigned long timeStamp, pulses, qualityBits;
    double Value;
    PointQuality_t pointQuality;
    RWCString valReport;

    int badData;

    CtiPointNumeric *pPoint    = NULL;
    CtiReturnMsg    *ReturnMsg = NULL;  // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData     = NULL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Load Profile Scan Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    //  ACH:  are these necessary?  /mskf
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


        lpDemandRate = getLoadProfile().getLoadProfileDemandRate();

        pPoint = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( 1 + OFFSET_LOADPROFILE_OFFSET, DemandAccumulatorPointType );

        badData = FALSE;

        if( pPoint != NULL )
        {
            for( intervalOffset = 0; intervalOffset < 6; intervalOffset++ )
            {
                //  error code in the top 5 bits - parsed by checkLoadProfileQuality
                pulses   = DSt->Message[intervalOffset*2];
                pulses <<= 8;
                pulses  |= DSt->Message[intervalOffset*2 + 1];

                if( badData )  //  load survey was halted - the rest of the data is bad
                {
                    pointQuality = DeviceFillerQuality;
                    pulses = 0;
                }
                else if( !checkLoadProfileQuality( pulses, pointQuality, badData ) )
                {
                    //  if no fatal problems with the quality,
                    //    adjust for the demand interval
                    Value = pulses * (3600 / lpDemandRate);
                    //    and the UOM
                    Value = pPoint->computeValueForUOM( Value );
                }
                else
                {
                    Value = 0.0;
                }

                pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(),
                                            Value,
                                            pointQuality,
                                            DemandAccumulatorPointType,
                                            "",
                                            TAG_POINT_LOAD_PROFILE_DATA );

                //  this is where the block started...
                timeStamp  = _lastLPRequestBlockStart.seconds() + (lpDemandRate * intervalOffset);
                //  but we want interval *ending* times, so add on one more interval
                timeStamp += lpDemandRate;
                pData->setTime( timeStamp );

                ReturnMsg->insert( pData );
            }

            //  insert a point data message for TDC and the like
            //    note that timeStamp, pointQuality, and Value are set in the final iteration of the above for loop
            valReport = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                  ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(),
                                        Value,
                                        pointQuality,
                                        DemandAccumulatorPointType,
                                        valReport );
            pData->setTime( timeStamp );
            setLastLPTime( timeStamp );
            ReturnMsg->insert( pData );
        }
        else
        {
            ReturnMsg->setResultString("No load profile point defined for '" + getName() + "'");
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
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



