#include "precompiled.h"

#include "devicetypes.h"
#include "tbl_ptdispatch.h"
#include "dev_dct501.h"
#include "logger.h"
#include "porter.h"
#include "pt_numeric.h"
#include "numstr.h"

using std::string;
using std::endl;
using std::list;
using std::make_pair;
using namespace Cti::Protocols;

namespace Cti {
namespace Devices {

const Dct501Device::CommandSet Dct501Device::_commandStore = Dct501Device::initCommandStore();


Dct501Device::Dct501Device( )
{}

Dct501Device::CommandSet Dct501Device::initCommandStore()
{
    CommandSet cs;

    cs.insert(CommandStore(EmetconProtocol::Scan_Integrity,  EmetconProtocol::IO_Read, DCT_AnalogsPos, DCT_AnalogsLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_Demand, EmetconProtocol::IO_Read, DCT_AnalogsPos, DCT_AnalogsLen));

    return cs;
}


bool Dct501Device::getOperation( const UINT &cmd, BSTRUCT &bst ) const
{
    if( getOperationFromStore(_commandStore, cmd, bst) )
    {
        return true;
    }

    return Inherited::getOperation(cmd, bst);
}


ULONG Dct501Device::calcNextLPScanTime( void )
{
    CtiTime        Now, channelTime, blockStart;
    unsigned long midnightOffset;
    int           lpBlockSize, lpDemandRate, lpMaxBlocks;

    //  make sure to completely reset it every time we recalculate
    _nextLPScanTime = YUKONEOT;

    if( !_lpIntervalSent )
    {
        //  send load profile interval on the next 5 minute boundary
        _nextLPScanTime = (Now.seconds() - LoadProfileCollectionWindow) + 300;

        _nextLPScanTime -= _nextLPScanTime % 300;
    }
    else
    {
        lpDemandRate = getLoadProfile()->getLoadProfileDemandRate();
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
            CtiPointSPtr pPoint = getDevicePointOffsetTypeEqual((i+1) + PointOffset_LoadProfileOffset, DemandAccumulatorPointType);

            //  safe default
            _nextLPTime[i] = YUKONEOT;

            //  if there's no point defined or we're not collecting load profile, don't scan
            if( pPoint && getLoadProfile()->isChannelValid(i) )
            {
                //  uninitialized
                if( !_lastLPTime[i].seconds() )
                {
                    _nextLPTime[i]    = 86400;  //  safe defaults
                    _lastLPTime[i]    = 86400;  //
                    _lastLPRequest[i] = Now - 86400;

                    CtiTablePointDispatch pd(pPoint->getPointID());

                    if(pd.Restore())
                    {
                        _lastLPTime[i] = pd.getTimeStamp().seconds();
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
                blockStart = blockStart - midnightOffset % lpBlockSize;

                //  we can only request in blocks, so we plan to request LP data
                //    after one block (6 intervals) has passed
                _nextLPTime[i]  = blockStart + lpBlockSize;
                //  also add on time for it to move out of the memory we're requesting
                _nextLPTime[i] = _nextLPTime[i] + LPBlockEvacuationTime;

                //  if we're overdue
                while( (_nextLPTime[i] <= (Now - LoadProfileCollectionWindow)) ||
                       (_nextLPTime[i] <= _lastLPRequest[i]) )
                {
                    _nextLPTime[i] = _nextLPTime[i] + getLPRetryRate(lpDemandRate);
                }
            }

            //  if we're sooner than the next-closest scan
            if( _nextLPScanTime > _nextLPTime[i].seconds() )
            {
                _nextLPScanTime = _nextLPTime[i].seconds();
            }
        }
    }

    return _nextLPScanTime;
}


void Dct501Device::calcAndInsertLPRequests(OUTMESS *&OutMessage, OutMessageList &outList)
{
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
                        lpBlockStartTime  = lpBlockStartTime - (lpMaxBlocks - 1) * lpBlockSize;
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

                    lpDescriptorString = " channel " + CtiNumStr(i+1);
                    lpDescriptorString += " block " + CtiNumStr(lpBlockAddress+1);

                    strncat( tmpOutMess->Request.CommandStr,
                             lpDescriptorString.c_str(),
                             sizeof(tmpOutMess->Request.CommandStr) - strlen(tmpOutMess->Request.CommandStr));

                    if( isDebugLudicrous() )
                    {
                        CTILOG_DEBUG(dout, "command string check for device \""<< getName() <<"\":"<<
                                endl << tmpOutMess->Request.CommandStr
                                );
                    }

                    outList.push_back(tmpOutMess);
                    _lastLPRequest[i] = Now;
                }
                else
                {
                    if( isDebugLudicrous() )
                    {
                        CTILOG_DEBUG(dout, "LP scan too early for device \"" << getName() << "\", aborted");
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
}


bool Dct501Device::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
{
    bool retVal = false;
    int lpBlockAddress, lpChannel;

    CTILOG_INFO(dout,
            endl <<"parse.getiValue(\"scan_loadprofile_block\",   0) = "<< parse.getiValue("scan_loadprofile_block", 0) <<
            endl <<"parse.getiValue(\"scan_loadprofile_channel\", 0) = "<< parse.getiValue("scan_loadprofile_channel", 0)
            );

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
        OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Read;

        retVal = true;
    }
    else
    {
        CTILOG_ERROR(dout, "Improperly formed LP request discarded for \""<< getName() <<"\"");

        retVal = false;
    }

    return retVal;
}

/*
 *  ResultDecode MUST decode all CtiDLCCommand_t which are defined in the initCommandStore object.  The only exception to this
 *  would be a child whose decode was identical to the parent, but whose request was done differently..
 *  This MAY be the case for example in an IED scan.
 */
YukonError_t Dct501Device::ModelDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;


    switch(InMessage.Sequence)
    {
    case (EmetconProtocol::Scan_Integrity):
    case (EmetconProtocol::GetValue_Demand):
        {
            status = decodeGetValueDemand(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

    case (EmetconProtocol::Scan_LoadProfile):
        {
            status = decodeScanLoadProfile(InMessage, TimeNow, vgList, retList, outList);
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

            if( status )
            {
                CTILOG_DEBUG(dout, "IM->Sequence = "<< InMessage.Sequence <<" for "<< getName());
            }
        }
    }

    return status;
}


YukonError_t Dct501Device::decodeGetValueDemand(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    INT pnt_offset, byte_offset;
    ULONG i,x;
    INT pid;
    INT Error;
    USHORT StatusData[8];
    USHORT SaveCount;
    string resultString;

    const DSTRUCT *DSt = &InMessage.Buffer.DSt;

    DOUBLE Value;
    CtiPointNumericSPtr   pPoint;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    CTILOG_INFO(dout, "Demand Decode for \""<< getName() <<"\"");

    resetScanFlag(ScanRateGeneral);
    resetScanFlag(ScanRateIntegrity);

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    for(pnt_offset = 1; pnt_offset <= 4; pnt_offset++)
    {
        byte_offset = (pnt_offset - 1) * 2;

        Error = (DSt->Message[byte_offset] & 0xc0);

        // 2 byte demand value.  Upper 2 bits are error indicators.
        Value = MAKEUSHORT(DSt->Message[byte_offset + 1], (DSt->Message[byte_offset] & 0x3f) );
        //  normalize the range
        Value /= 16000.0;

        /* look for analog points */
        pPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual( pnt_offset, AnalogPointType ));

        // handle demand data here
        if( pPoint)
        {
            // Adjust for the unit of measure!
            Value = pPoint->computeValueForUOM(Value);

            if(Error == 0)
            {
                resultString = getName() + " / " + pPoint->getName();
                resultString += " = " + CtiNumStr(Value,pPoint->getPointUnits().getDecimalPlaces());
                pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, resultString);
            }
            else
            {
                resultString = "Error indicated on DCT " + getName() + " / " + pPoint->getName();
                resultString += ";  Error " + CtiNumStr(Error);

                CTILOG_ERROR(dout, resultString);

                pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, UnknownQuality, AnalogPointType, resultString);
            }

            if(pData != NULL)
            {
                ReturnMsg->PointData().push_back(pData);
                pData = NULL;  // We just put it on the list...

                //  clear out any demand input 1 messages we may have appended - we have a real one
                ReturnMsg->setResultString( string() );
            }
        }
        //  else send the raw pulses for offset 1
        else if( pnt_offset == 1 )
        {
            resultString = ReturnMsg->ResultString( );
            if( resultString != "" )
                resultString += "\n";

            resultString += getName() + " / Analog 1 " + CtiNumStr(pnt_offset);
            resultString += " = " + CtiNumStr(Value);
            resultString += "  --  POINT UNDEFINED IN DB";
            ReturnMsg->setResultString( resultString );
        }
    }

    if(ReturnMsg != NULL)
    {
        if(!(ReturnMsg->ResultString().empty()) || ReturnMsg->getData().size() > 0)
        {
            retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );
            //  retList.push_back( ReturnMsg );
        }
        else
        {
            CTILOG_ERROR(dout, "No demand accumulators are defined in the DB");

            delete ReturnMsg;
        }
    }

    return status;
}


YukonError_t Dct501Device::decodeScanLoadProfile(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    string val_report, result_string;

    int     demand_rate, block_size, max_blocks;
    int     current_block_num, retrieved_block_num, retrieved_channel, midnight_offset;
    bool    bad_data = false;
    double  value;
    unsigned long pulses, timestamp, current_block_start, retrieved_block_start;
    PointQuality_t quality;

    CtiCommandParser parse(InMessage.Return.CommandStr);

    CtiPointNumericSPtr point;
    CtiReturnMsg        *return_msg = 0;  // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg     *point_data = 0;

    CTILOG_INFO(dout, "Load Profile Scan Decode for \""<< getName() <<"\"");

    return_msg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    return_msg->setUserMessageId(InMessage.Return.UserID);

    if( (retrieved_channel   = parse.getiValue("scan_loadprofile_channel", 0)) &&
        (retrieved_block_num = parse.getiValue("scan_loadprofile_block",   0)) )
    {
        if( isDebugLudicrous() )
        {
            CTILOG_DEBUG(dout, "retrieved_channel " << retrieved_channel << ", retrieved_block_num " << retrieved_block_num);
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


        point = boost::static_pointer_cast< CtiPointNumeric >(getDevicePointOffsetTypeEqual( retrieved_channel + PointOffset_LoadProfileOffset, DemandAccumulatorPointType ));

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
                result_string = "Attempt to decode current load profile block for \"" + getName() + "\" - aborting decode ";

                CTILOG_ERROR(dout, result_string <<
                        endl <<"commandstr = "<< InMessage.Return.CommandStr
                        );
            }
            else if( retrieved_block_start < _lastLPTime[retrieved_channel - 1] )
            {
                result_string  = "Block < lastLPTime for device \"" + getName() + "\" - aborting decode";

                CTILOG_ERROR(dout, result_string <<
                        endl <<"commandstr            = "<< InMessage.Return.CommandStr <<
                        endl <<"retrieved_block_num   = "<< retrieved_block_num <<
                        endl <<"retrieved_channel     = "<< retrieved_channel <<
                        endl <<"retrieved_block_start = "<< retrieved_block_start <<
                        endl <<"_lastLPTime           = "<< _lastLPTime[retrieved_channel - 1]
                        );
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
                        CTILOG_DEBUG(dout, "load profile debug for \""<< getName() <<"\""<<
                                endl <<"value           = "<< value <<
                                endl <<"interval_offset = "<< interval_offset <<
                                endl <<"timestamp       = "<< CtiTime(timestamp)
                                );
                    }

                    point_data->setTime(timestamp);

                    return_msg->insert(point_data);
                }

                _lastLPTime[retrieved_channel - 1] = timestamp;
            }
        }
        else
        {
            result_string = "No load profile point defined for '" + getName();
            result_string += "' demand accumulator " + CtiNumStr(retrieved_channel);
        }
    }
    else
    {
        result_string  = "scan_loadprofile tokens not found in command string \"";
        result_string += InMessage.Return.CommandStr;
        result_string += "\" - cannot proceed with decode, aborting";

        CTILOG_ERROR(dout, result_string);
    }

    return_msg->setResultString(result_string);

    retMsgHandler(InMessage.Return.CommandStr, status, return_msg, vgList, retList);

    return status;
}


YukonError_t Dct501Device::decodeGetConfigModel(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt = &InMessage.Buffer.DSt;

    INT ssp;
    char rev;
    char temp[80];

    string sspec;
    string options("Options:\n");

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi


    ssp = InMessage.Buffer.DSt.Message[4] * 256 + InMessage.Buffer.DSt.Message[0];
    rev = 64 + InMessage.Buffer.DSt.Message[1];

    sspec = "\nSoftware Specification " + CtiNumStr(ssp);
    sspec += "  Rom Revision " + rev;
    sspec += "\n";

    if(InMessage.Buffer.DSt.Message[2] & 0x01)
    {
        options+= string("  Latched loads\n");
    }
    if(InMessage.Buffer.DSt.Message[2] & 0x02)
    {
        options+= string("  Timed loads\n");
    }
    if(InMessage.Buffer.DSt.Message[2] & 0x40)
    {
        options+= string("  Extended addressing\n");
    }
    if(InMessage.Buffer.DSt.Message[2] & 0x80)
    {
        options+= string("  Metering of basic kWh\n");
    }

    if(InMessage.Buffer.DSt.Message[3] & 0x01)
    {
        options+= string("  Time-of-demand\n");
    }
    if(InMessage.Buffer.DSt.Message[3] & 0x04)
    {
        options+= string("  Load survey\n");
    }
    if(InMessage.Buffer.DSt.Message[3] & 0x08)
    {
        options+= string("  Full group address support\n");
    }
    if(InMessage.Buffer.DSt.Message[3] & 0x10)
    {
        options+= string("  Feedback load control\n");
    }
    if(InMessage.Buffer.DSt.Message[3] & 0x40)
    {
        options+= string("  Volt/VAR control\n");
    }
    if(InMessage.Buffer.DSt.Message[3] & 0x80)
    {
        options+= string("  Capacitor control\n");
    }

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString( sspec + options );

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

}
}
