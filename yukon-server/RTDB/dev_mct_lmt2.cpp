#include "precompiled.h"

#include "devicetypes.h"
#include "dev_mct_lmt2.h"
#include "logger.h"
#include "pt_numeric.h"
#include "numstr.h"

using Cti::Protocols::EmetconProtocol;
using std::string;
using std::endl;
using std::list;

namespace Cti {
namespace Devices {

const Lmt2Device::CommandSet Lmt2Device::_commandStore = Lmt2Device::initCommandStore();


Lmt2Device::Lmt2Device() {}

Lmt2Device::Lmt2Device(const Lmt2Device& aRef)
{
  *this = aRef;
}

Lmt2Device::~Lmt2Device() {}

Lmt2Device& Lmt2Device::operator=(const Lmt2Device& aRef)
{
  if(this != &aRef)
  {
     Inherited::operator=(aRef);
  }
  return *this;
}


Lmt2Device::CommandSet Lmt2Device::initCommandStore()
{
   CommandSet cs;

   cs.insert(CommandStore(EmetconProtocol::Scan_LoadProfile,                EmetconProtocol::IO_Read,   0,                          0));
   cs.insert(CommandStore(EmetconProtocol::PutStatus_ResetOverride,         EmetconProtocol::IO_Write,  MCT_LMT2_ResetOverrideFunc, 0));
   cs.insert(CommandStore(EmetconProtocol::GetStatus_LoadProfile,           EmetconProtocol::IO_Read,   MCT_LMT2_LPStatusPos,       MCT_LMT2_LPStatusLen));
   cs.insert(CommandStore(EmetconProtocol::GetConfig_LoadProfileInterval,   EmetconProtocol::IO_Read,   MCT_LMT2_LPIntervalPos,     MCT_LMT2_LPIntervalLen));
   cs.insert(CommandStore(EmetconProtocol::PutConfig_LoadProfileInterval,   EmetconProtocol::IO_Write,  Command_LPInt,              0));

   return cs;
}


bool Lmt2Device::getOperation( const UINT &cmd, BSTRUCT &bst) const
{
    if( getOperationFromStore(_commandStore, cmd, bst) )
    {
        return true;
    }

    return Inherited::getOperation(cmd, bst);
}


ULONG Lmt2Device::calcNextLPScanTime( void )
{
    CtiTime Now, blockStart, nextTime;
    unsigned long midnightOffset;
    int lpBlockSize, lpDemandRate, lpMaxBlocks;

    CtiPointSPtr pPoint = getDevicePointOffsetTypeEqual(1 + PointOffset_LoadProfileOffset, DemandAccumulatorPointType);

    //  make sure to completely recalculate this every time
    _nextLPScanTime = YUKONEOT;

    if( !_lpIntervalSent )
    {
        //  send load profile interval on the next 5 minute boundary
        _nextLPScanTime = Now.seconds() + 300;

        _nextLPScanTime -= _nextLPScanTime % 300;
    }
    else if( pPoint && getLoadProfile()->isChannelValid(0) )
    {
        //  we read 6 intervals at a time
        lpDemandRate = getLoadProfile()->getLoadProfileDemandRate();
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
        while( (nextTime <= (Now - LoadProfileCollectionWindow)) ||
               (nextTime <= _lastLPRequest) )
        {
            nextTime += getLPRetryRate(lpDemandRate);
        }

        _nextLPScanTime = nextTime.seconds();
    }

    return _nextLPScanTime;
}


INT Lmt2Device::calcAndInsertLPRequests(OUTMESS *&OutMessage, list< OUTMESS* > &outList)
{
    int nRet = NoError;

    int            lpDemandRate;
    unsigned int   lpBlockAddress;
    unsigned long  lpBlocksToCollect,
                   lpMaxBlocks,
                   lpBlockSize,
                   lpMidnightOffset;
    CtiTime         lpBlockStartTime;
    CtiTime         Now;
    OUTMESS       *tmpOutMess;
    string      lpDescriptorString;
    int            lpChannel;

    lpDemandRate     = getLoadProfile()->getLoadProfileDemandRate();

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

                lpDescriptorString = string(" block ") + CtiNumStr(lpBlockAddress+1);

                strncat( tmpOutMess->Request.CommandStr,
                         lpDescriptorString.c_str(),
                         sizeof(tmpOutMess->Request.CommandStr) - strlen(tmpOutMess->Request.CommandStr));

                if( isDebugLudicrous() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - command string check for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "\"" << tmpOutMess->Request.CommandStr << "\"" << endl;
                }

                outList.push_back(tmpOutMess);
                _lastLPRequest = Now;
            }
            else
            {
                if( isDebugLudicrous() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - LP scan too early for device \"" << getName() << "\", aborted **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


bool Lmt2Device::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
{
    bool retVal = false;
    int lpBlockAddress;

    if( getMCTDebugLevel(DebugLevel_LoadProfile) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "parse.getiValue(\"scan_loadprofile_block\",   0) = " << parse.getiValue("scan_loadprofile_block", 0) << endl;
    }

    if( lpBlockAddress = parse.getiValue("scan_loadprofile_block", 0) )
    {
        lpBlockAddress--;  //  adjust to be a zero-based offset

        lpBlockAddress *= 12;

        lpBlockAddress += 0x9A;

        OutMessage->Buffer.BSt.Function = lpBlockAddress;
        OutMessage->Buffer.BSt.Length   = 12;  //  2 bytes per interval
        OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Read;

        retVal = true;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Improperly formed LP request discarded for \"" << getName() << "\"." << endl;
        }

        retVal = false;
    }

    return retVal;
}


INT Lmt2Device::ModelDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    switch(InMessage->Sequence)
    {
        case (EmetconProtocol::Scan_LoadProfile):
        {
            status = decodeScanLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::GetStatus_Internal):
        {
            status = decodeGetStatusInternal(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::GetStatus_LoadProfile):
        {
            status = decodeGetStatusLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::GetConfig_Model):
        {
            status = decodeGetConfigModel(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        default:
        {
            status = Inherited::ModelDecode(InMessage, TimeNow, vgList, retList, outList);

            if(status != NORMAL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " IM->Sequence = " << InMessage->Sequence << " " << getName() << endl;
            }
            break;
        }
    }

    return status;
}


INT Lmt2Device::decodeScanLoadProfile(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int status = NORMAL;

    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    string val_report, result_string;

    int     demand_rate, block_size, max_blocks;
    int     current_block_num, retrieved_block_num, midnight_offset;
    bool    bad_data = false;
    double  value;
    unsigned long pulses, timestamp, current_block_start, retrieved_block_start;
    PointQuality_t quality;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    CtiPointNumericSPtr point;
    CtiReturnMsg    *return_msg = 0;  // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *point_data = 0;

    if( getMCTDebugLevel(DebugLevel_Scanrates | DebugLevel_LoadProfile) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Load Profile Scan Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if((return_msg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    return_msg->setUserMessageId(InMessage->Return.UserID);

    if( (retrieved_block_num = parse.getiValue("scan_loadprofile_block",   0)) )
    {
        if( isDebugLudicrous() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - retrieved_block_num " << retrieved_block_num << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retrieved_block_num--;

        demand_rate = getLoadProfile()->getLoadProfileDemandRate();
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

        point = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual( 1 + PointOffset_LoadProfileOffset, DemandAccumulatorPointType ));

        if( point )
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
                if( getMCTDebugLevel(DebugLevel_LoadProfile) )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - attempt to decode current load profile block for \"" << getName() << "\" - aborting decode **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << InMessage->Return.CommandStr << endl;
                }

                result_string = "Attempt to decode current load profile block for \"" + getName() + "\" - aborting decode ";
            }
            else if( retrieved_block_start < getLastLPTime() )
            {
                if( getMCTDebugLevel(DebugLevel_LoadProfile) )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - load profile debug for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

                    if( isDebugLudicrous() )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - load profile debug for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "value = " << value << endl;
                        dout << "interval_offset = " << interval_offset << endl;
                        dout << "timestamp = " << CtiTime(timestamp) << endl;
                    }

                    point_data->setTime(timestamp);

                    return_msg->insert(point_data);
                }

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
            dout << CtiTime() << " **** Checkpoint - scan_loadprofile tokens not found in command string \"" << InMessage->Return.CommandStr << "\" - cannot proceed with decode, aborting **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        result_string  = "scan_loadprofile tokens not found in command string \"";
        result_string += InMessage->Return.CommandStr;
        result_string += "\" - cannot proceed with decode, aborting";
    }

    return_msg->setResultString(result_string);

    retMsgHandler(InMessage->Return.CommandStr, status, return_msg, vgList, retList);

    return status;
}


INT Lmt2Device::decodeGetStatusInternal( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NORMAL;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    unsigned char *geneBuf = InMessage->Buffer.DSt.Message;

    ULONG pulseCount = 0;
    string resultString;

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

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

    return status;
}


INT Lmt2Device::decodeGetStatusLoadProfile( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NORMAL;

    CtiReturnMsg *ReturnMsg = NULL;
    string resultString;

    DSTRUCT *DSt = &InMessage->Buffer.DSt;

    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return MEMORY;
    }

    ReturnMsg->setUserMessageId(InMessage->Return.UserID);

    resultString  = getName() + " / Load Survey Control Parameters:\n";
    resultString += "Demand Interval : " + CtiNumStr((int)(DSt->Message[2] * 5)) + string("\n");
    resultString += "Current Interval: " + CtiNumStr((int)((DSt->Message[4] / 2) + 1)) + string("\n");
    resultString += "Current Value   : " + CtiNumStr((int)((DSt->Message[0] << 8) + DSt->Message[1])) + string("\n");

    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


INT Lmt2Device::decodeGetConfigModel(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
   INT status = NORMAL;

   INT ErrReturn  = InMessage->EventCode & 0x3fff;
   DSTRUCT *DSt   = &InMessage->Buffer.DSt;

  INT ssp;
  char rev;

  string sspec;
  string options("Options:\n");

  CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

  ssp = InMessage->Buffer.DSt.Message[0];
  rev = 64 + InMessage->Buffer.DSt.Message[1];

  sspec = "\nSoftware Specification " + CtiNumStr(ssp) + string("  Rom Revision ") + rev + "\n";

  if(InMessage->Buffer.DSt.Message[2] & 0x01)
  {
     options+= string("  Latched relay\n");
  }
  else
  {
     options+= string("  No latched relay\n");
  }

  if(InMessage->Buffer.DSt.Message[2] & 0x04)
  {
     options+= string("  No encoding meter\n");
  }
  else
  {
     options+= string("  Encoding meter\n");
  }

  if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
  {
     {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;
     }

     return MEMORY;
  }

  ReturnMsg->setUserMessageId(InMessage->Return.UserID);
  ReturnMsg->setResultString( sspec + options );

  retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

   return status;
}

}
}

