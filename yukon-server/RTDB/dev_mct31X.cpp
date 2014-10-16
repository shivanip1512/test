#include "precompiled.h"

#include "devicetypes.h"
#include "tbl_ptdispatch.h"
#include "dev_mct31X.h"
#include "logger.h"
#include "pt_numeric.h"
#include "numstr.h"
#include "dllyukon.h"
#include "ctidate.h"
#include "ctitime.h"

#include <boost/assign/list_of.hpp>

using namespace Cti::Protocols;
using std::string;
using std::endl;
using std::list;
using std::vector;
using std::pair;
using std::make_pair;
using boost::assign::list_of;

namespace Cti {
namespace Devices {

static const double MCT360_GEKV_KWHMultiplier = 2000000.0;

const Mct31xDevice::CommandSet Mct31xDevice::_commandStore = Mct31xDevice::initCommandStore();

const Mct31xDevice::IedTypesToCommands Mct31xDevice::ResetCommandsByIedType = Mct31xDevice::initIedResetCommands();

Mct31xDevice::Mct31xDevice( )
{
    for( int i = 0; i < ChannelCount; i++ )
    {
        _lastLPTime[i] = CtiTime(0UL);
    }
}


CtiTableDeviceMCTIEDPort &Mct31xDevice::getIEDPort( void )
{
    return _iedPort;
}


Mct31xDevice::CommandSet Mct31xDevice::initCommandStore( )
{
    CommandSet cs;

    cs.insert(CommandStore(EmetconProtocol::Scan_General,               EmetconProtocol::IO_Function_Read,  FuncRead_DemandPos,         FuncRead_StatusLen));

    cs.insert(CommandStore(EmetconProtocol::Scan_Integrity,             EmetconProtocol::IO_Function_Read,  FuncRead_DemandPos,         FuncRead_DemandLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_Demand,            EmetconProtocol::IO_Function_Read,  FuncRead_DemandPos,         FuncRead_DemandLen));

    cs.insert(CommandStore(EmetconProtocol::Scan_LoadProfile,           EmetconProtocol::IO_Function_Read,  0,                          0));

    cs.insert(CommandStore(EmetconProtocol::GetValue_PeakDemand,        EmetconProtocol::IO_Function_Read,  FuncRead_MinMaxDemandPos,   FuncRead_MinMaxDemandLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_FrozenPeakDemand,  EmetconProtocol::IO_Function_Read,  FuncRead_FrozenDemandPos,   FuncRead_FrozenDemandLen));

    //  add the 2 other channels for 318s
    cs.insert(CommandStore(EmetconProtocol::PutValue_KYZ2,              EmetconProtocol::IO_Write,          MCT3XX_PutMRead2Pos,        MCT3XX_PutMReadLen));
    cs.insert(CommandStore(EmetconProtocol::PutValue_KYZ3,              EmetconProtocol::IO_Write,          MCT3XX_PutMRead3Pos,        MCT3XX_PutMReadLen));

    cs.insert(CommandStore(EmetconProtocol::GetStatus_External,         EmetconProtocol::IO_Function_Read,  FuncRead_DemandPos,         FuncRead_StatusLen));

    cs.insert(CommandStore(EmetconProtocol::GetConfig_Multiplier2,      EmetconProtocol::IO_Read,           MCT3XX_Mult2Pos,            MCT3XX_MultLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Multiplier3,      EmetconProtocol::IO_Read,           MCT3XX_Mult3Pos,            MCT3XX_MultLen));

    cs.insert(CommandStore(EmetconProtocol::PutConfig_Multiplier2,      EmetconProtocol::IO_Write,          MCT3XX_Mult2Pos,            MCT3XX_MultLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Multiplier3,      EmetconProtocol::IO_Write,          MCT3XX_Mult3Pos,            MCT3XX_MultLen));

    //  these are commands for the 360 and 370 only

    //  scan address and length are identical for the p+ and s4
    cs.insert(CommandStore(EmetconProtocol::GetConfig_IEDScan,          EmetconProtocol::IO_Read,           MCT360_IEDScanPos,          MCT360_IEDScanLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_IEDScan,          EmetconProtocol::IO_Write,          MCT360_IEDScanPos, 2));  //  just 2 bytes - seconds and delay

    cs.insert(CommandStore(EmetconProtocol::PutConfig_IEDClass,         EmetconProtocol::IO_Write,          MCT360_IEDClassPos,         MCT360_IEDClassLen));

    cs.insert(CommandStore(EmetconProtocol::GetConfig_IEDTime,          EmetconProtocol::IO_Function_Read,  MCT360_IEDTimePos,          MCT360_IEDTimeLen));

    cs.insert(CommandStore(EmetconProtocol::GetValue_IEDDemand,         EmetconProtocol::IO_Function_Read,  MCT360_IEDDemandPos,        MCT360_IEDReqLen));

    cs.insert(CommandStore(EmetconProtocol::GetValue_IEDKwh,            EmetconProtocol::IO_Function_Read,  MCT360_IEDKwhPos,           MCT360_IEDReqLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_IEDKvarh,          EmetconProtocol::IO_Function_Read,  MCT360_IEDKvarhPos,         MCT360_IEDReqLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_IEDKvah,           EmetconProtocol::IO_Function_Read,  MCT360_IEDKvahPos,          MCT360_IEDReqLen));

    cs.insert(CommandStore(EmetconProtocol::PutValue_IEDReset,          EmetconProtocol::IO_Function_Write, 0,                          0));

    cs.insert(CommandStore(EmetconProtocol::GetStatus_IEDLink,          EmetconProtocol::IO_Function_Read,  MCT360_IEDLinkPos,          MCT360_IEDLinkLen));

    return cs;
}


bool Mct31xDevice::getOperation( const UINT &cmd, BSTRUCT &bst ) const
{
    //  the 318L is the only 31x that supports load profile that we're concerned about here, and it'd be silly to make another class for one function
    //    we want it to stop here, no matter what, no Inherited::anything...
    if( getType( ) != TYPEMCT318L &&
        cmd == EmetconProtocol::Scan_LoadProfile )
    {
        return false;
    }

    if( getOperationFromStore(_commandStore, cmd, bst) )
    {
        return true;
    }

    return Inherited::getOperation(cmd, bst);
}


ULONG Mct31xDevice::calcNextLPScanTime( void )
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

        for( int i = 0; i < ChannelCount; i++ )
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
                blockStart -= midnightOffset % lpBlockSize;

                //  we can only request in blocks, so we plan to request LP data
                //    after one block (6 intervals) has passed
                _nextLPTime[i]  = blockStart + lpBlockSize;
                //  also add on time for it to move out of the memory we're requesting
                _nextLPTime[i] += LPBlockEvacuationTime;

                //  if we're overdue
                while( (_nextLPTime[i] <= (Now - LoadProfileCollectionWindow)) ||
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

    return _nextLPScanTime;
}


void Mct31xDevice::calcAndInsertLPRequests(OUTMESS *&OutMessage, OutMessageList &outList)
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

        for( int i = 0; i < ChannelCount; i++ )
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

                    lpDescriptorString = string(" channel ") + CtiNumStr(i+1) +
                                         string(" block ") + CtiNumStr(lpBlockAddress+1);

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
                        CTILOG_DEBUG(dout, "LP scan too early for device \""<< getName() <<"\", aborted");
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


bool Mct31xDevice::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
{
    bool retVal = false;
    int lpBlockAddress, lpChannel;

    if( isDebugLudicrous() )
    {
        CTILOG_DEBUG(dout,
                endl <<"parse.getiValue(\"scan_loadprofile_block\",   0) = "<< parse.getiValue("scan_loadprofile_block", 0) <<
                endl <<"parse.getiValue(\"scan_loadprofile_channel\", 0) = "<< parse.getiValue("scan_loadprofile_channel", 0)
                );
    }

    if( (lpBlockAddress = parse.getiValue("scan_loadprofile_block",   0)) &&
        (lpChannel      = parse.getiValue("scan_loadprofile_channel", 0)) )
    {
        lpChannel--;
        lpBlockAddress--;  //  adjust to be a zero-based offset

        switch( lpChannel )
        {
            case 0:
                lpBlockAddress += 0x50;
                break;
            case 1:
                lpBlockAddress += 0x60;
                break;
            case 2:
                lpBlockAddress += 0x70;
                break;
        }

        OutMessage->Buffer.BSt.Function = lpBlockAddress;
        OutMessage->Buffer.BSt.Length   = 13;  //  2 bytes per interval, and a status byte to boot
        OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;

        retVal = true;
    }
    else
    {
        CTILOG_ERROR(dout, "Improperly formed LP request discarded for \""<< getName() <<"\"");

        retVal = false;
    }

    return retVal;
}


Mct31xDevice::IedTypesToCommands Mct31xDevice::initIedResetCommands()
{
    return boost::assign::map_list_of
        (CtiTableDeviceMCTIEDPort::AlphaPowerPlus, IedResetCommand
            (MCT360_AlphaResetPos, list_of<unsigned char>
                (60)    //  delay timer won't allow a reset for 15 minutes (in 15 sec ticks)
                 (1)))  //  Demand Reset function code for the Alpha
        (CtiTableDeviceMCTIEDPort::LandisGyrS4, IedResetCommand
            (MCT360_LGS4ResetPos, list_of<unsigned char>
                (MCT360_LGS4ResetID)
                (60)    //  delay timer won't allow a reset for 15 minutes (in 15 sec ticks)
                (43)))  //  Demand Reset function code for the LG S4
        (CtiTableDeviceMCTIEDPort::GeneralElectricKV, IedResetCommand
            (MCT360_GEKVResetPos, list_of<unsigned char>
                (MCT360_GEKVResetID)
                (60)    //  delay timer won't allow a reset for 15 minutes (in 15 sec ticks)
                 (0)    //  sequence, standard proc, and uppoer bits of proc are 0
                 (9)    //  procedure 9
                 (1)    //  parameter length 1
                 (1))); //  demand reset bit set
}


YukonError_t Mct31xDevice::executePutValue(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    if( parse.isKeyValid("ied") && parse.isKeyValid("reset")
          && (getType() == TYPEMCT360 || getType() == TYPEMCT370) )
    {
        const int function = EmetconProtocol::PutValue_IEDReset;

        if( getOperation(function, OutMessage->Buffer.BSt) )
        {
            const int iedtype = getIEDPort().getIEDType();

            IedTypesToCommands::const_iterator itr = ResetCommandsByIedType.find(iedtype);

            if( itr == ResetCommandsByIedType.end() )
            {
                CTILOG_ERROR(dout, "Invalid IED type "<< iedtype <<" on device \""<< getName() <<"\"");

                return ClientErrors::MissingConfig;
            }

            const IedResetCommand &command = itr->second;

            OutMessage->Buffer.BSt.Function = command.function;
            OutMessage->Buffer.BSt.Length   = command.payload.size();
            std::copy(command.payload.begin(), command.payload.end(), OutMessage->Buffer.BSt.Message);

            populateDlcOutMessage(*OutMessage);
            OutMessage->Sequence  = function;         // Helps us figure it out later!

            OutMessage->Request.RouteID   = getRouteID();
            strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

            return ClientErrors::None;
        }
    }

    return Inherited::executePutValue(pReq, parse, OutMessage, vgList, retList, outList);
}


YukonError_t Mct31xDevice::ModelDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    switch(InMessage.Sequence)
    {
        case EmetconProtocol::Scan_General:
        case EmetconProtocol::GetStatus_External:
        {
            //  A general scan for any MCT does status decode only.
            status = decodeStatus(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case EmetconProtocol::GetStatus_IEDLink:
        {
            status = decodeGetStatusIED(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case EmetconProtocol::Scan_Accum:
        case EmetconProtocol::GetValue_KWH:
        {
            status = decodeGetValueKWH(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case EmetconProtocol::Scan_Integrity:
        {
            //  to catch the IED case
            setScanFlag(ScanRateIntegrity, false);  //  resetScanFlag(ScanPending);
        }
        case EmetconProtocol::GetValue_Demand:
        {
            //  we only have status info if we're not getting the demand from the IED
            if( (getType() == TYPEMCT360 || getType() == TYPEMCT370) && getIEDPort().getRealTimeScanFlag() )
            {
                status = decodeGetValueIED(InMessage, TimeNow, vgList, retList, outList);
            }
            else
            {
                status = decodeStatus(InMessage, TimeNow, vgList, retList, outList, true);

                if(status)  //  FIX - OR these or something, we should be smarter
                {
                    CTILOG_ERROR(dout, "status scan error codes for device \""<< getName() <<"\"");
                }

                status = decodeGetValueDemand(InMessage, TimeNow, vgList, retList, outList);
            }
            break;
        }

        case (EmetconProtocol::Scan_LoadProfile):
        {
            status = decodeScanLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::GetValue_PeakDemand):
        case (EmetconProtocol::GetValue_FrozenPeakDemand):
        {
            status = decodeGetValuePeak(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::GetConfig_IEDTime):
        case (EmetconProtocol::GetConfig_IEDScan):
        {
            status = decodeGetConfigIED(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (EmetconProtocol::GetValue_IED):
        case (EmetconProtocol::GetValue_IEDDemand):
        case (EmetconProtocol::GetValue_IEDKvah):
        case (EmetconProtocol::GetValue_IEDKvarh):
        case (EmetconProtocol::GetValue_IEDKwh):
        {
            status = decodeGetValueIED(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        default:
        {
            status = Inherited::ModelDecode(InMessage, TimeNow, vgList, retList, outList);

            if( status )
            {
                CTILOG_ERROR(dout, "errors on Inherited::ModelDecode for device \"" + getName() + "\"");
            }
            break;
        }
    }

    return status;
}


YukonError_t Mct31xDevice::decodeStatus(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool expectMore)
{
    YukonError_t status = ClientErrors::None;
    USHORT SaveCount;
    string resultString;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    DOUBLE Value;

    CtiPointSPtr    pPoint;
    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CTILOG_DEBUG(dout, "Status Decode for \""<< getName() <<"\"");
    }

    setScanFlag(ScanRateGeneral, false);  //resetScanFlag(ScanPending);

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    //  the only status points we really care about - comm status is handled higher up by porter
    for( int i = 1; i <= 8; i++ )
    {
        if( (pPoint = getDevicePointOffsetTypeEqual(i, StatusPointType)) )
        {
            Value = translateStatusValue(pPoint->getPointOffset(), pPoint->getType(), getType(), InMessage.Buffer.DSt.Message);

            if( Value == STATE_INVALID )
            {
                CTILOG_ERROR(dout, "status is INVALID for device \""<< getName() <<"\", offset " << i);
            }
            else
            {
                resultString = ResolveStateName(pPoint->getStateGroupID(), Value);

                if( resultString != "" )
                {
                    resultString = getName() + " / " + pPoint->getName() + ":" + resultString;
                }
                else
                {
                    resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value);
                }

                pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, StatusPointType, resultString);

                if(pData != NULL)
                {
                    ReturnMsg->PointData().push_back(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }

        }
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList, expectMore );

    return status;
}


YukonError_t Mct31xDevice::decodeGetStatusIED(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    INT pid, rateOffset;
    string resultString, name, ratename;

    ULONG lValue;

    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    CtiCommandParser parse( InMessage.Return.CommandStr );

    DOUBLE demandValue, Value;
    CtiTime timestamp;
    CtiDate datestamp;
    CtiPointSPtr    pPoint;
    CtiPointSPtr    pDemandPoint;
    CtiReturnMsg    *ReturnMsg    = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData        = NULL;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CTILOG_DEBUG(dout, "IED GetStatus Decode for \""<< getName() <<"\"");
    }

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    int i;
    for( i = 0; i < 12; i++ )
    {
        //  break if any bytes are unequal or all zero (therefore, any
        //    zero is a break condition in addition to inequality)
        if( (DSt->Message[i] != DSt->Message[i+1]) ||
            (DSt->Message[i] == 0) )
            break;
    }
    if( i == 12 )
    {
        //  we never broke out of the loop - all bytes are equal, the buffer is busted
        ReturnMsg->setResultString( "Device: " + getName() + "\nData buffer is bad, retry command" );
        status = ClientErrors::IedBufferBad;
    }
    else
    {
        switch( InMessage.Sequence )
        {
            case EmetconProtocol::GetStatus_IEDLink:
            {
                //  i don't know if this is valid for the status bytes
#if 0
                for( int i = 0; i < 4; i++ )  //  excluding byte 7 - it's a bitfield, not BCD
                {
                    //  Convert the bytes from BCD to normal byte values
                    DSt->Message[i] = (((DSt->Message[i] & 0xf0) / 16) * 10) + (DSt->Message[i] & 0x0f);
                }
#endif

                switch( getIEDPort().getIEDType() )
                {
                    case (CtiTableDeviceMCTIEDPort::AlphaPowerPlus):
                    {
                        resultString += getName() + " / IED / Alpha status:\n";

                        if( DSt->Message[0] & 0x01 )    resultString += "  Time Change\n";
                        if( DSt->Message[0] & 0x02 )    resultString += "  Autoread or Season change Demand Reset\n";
                        if( DSt->Message[0] & 0x08 )    resultString += "  Write Protected\n";
                        if( DSt->Message[0] & 0x20 )    resultString += "  Power Fail Flag\n";
                        if( DSt->Message[0] & 0x40 )    resultString += "  Season Change Flag\n";
                        if( DSt->Message[0] & 0x80 )    resultString += "  Autoread Flag\n";

                        resultString += getName() + " / IED / Comm status to Alpha:\n";

                        switch( (DSt->Message[1] & 0xf0) >> 4 )
                        {
                            case 0:     resultString += "  Normal communications\n";                break;
                            case 1:     resultString += "  Bad CRC from Alpha\n";                   break;
                            case 2:     resultString += "  Comm lockout for Function\n";            break;
                            case 3:     resultString += "  Illegal command, syntax, or length\n";   break;
                            case 4:     resultString += "  Framing error\n";                        break;
                            case 5:     resultString += "  Timeout error\n";                        break;
                            case 6:     resultString += "  Invalid password\n";                     break;
                            case 7:     resultString += "  NAK received from MCT\n";                break;
                            case 15:    resultString += "  Normal communications\n";                break;
                            default:    resultString += "  Error code " + CtiNumStr((DSt->Message[1] & 0xf0) >> 4) + string(" not implemented\n");
                        }

                        if( DSt->Message[1] & 0x80 )    resultString += "  Last IED write failed\n";

                        resultString += "MCT to Alpha Data Link: ";

                        if( DSt->Message[3] & 0x01 )
                        {
                            switch( DSt->Message[1] & 0x07 )
                            {
                                case 0:     resultString += "Communication failed\n";                   break;
                                case 1:     resultString += "Communication failed (baud rate)\n";       break;
                                case 2:     resultString += "Communication failed (take ctrl)\n";       break;
                                case 3:     resultString += "Communication failed (bad password)\n";    break;
                                case 4:     resultString += "Communication Successful (active now)\n";  break;
                                case 5:     resultString += "Communication Successful\n";               break;
                            }
                        }
                        else
                        {
                            resultString += "MCT's Serial Port to Alpha Disabled";
                        }

                        break;
                    }

                    case (CtiTableDeviceMCTIEDPort::LandisGyrS4):
                    {
                        resultString += getName() + " / IED / LGS4 status:  \n";

                        if( DSt->Message[0] & 0x01 )    resultString += "S4 Low battery\n";
                        if( DSt->Message[0] & 0x02 )    resultString += "No S4 Programming\n";
                        if( DSt->Message[0] & 0x04 )    resultString += "S4 Memory Failure\n";
                        if( DSt->Message[0] & 0x08 )    resultString += "S4 Demand Overflow\n";
                        if( DSt->Message[0] & 0x10 )    resultString += "S4 Stuck Switch\n";
                        if( DSt->Message[0] & 0x20 )    resultString += "S4 Unsafe Power Fail\n";

                        switch( (DSt->Message[1] & 0xf0) >> 4 )
                        {
                            case 0:     resultString += "  Normal IED Communications\n";    break;
                            case 1:     resultString += "  NAK Bad TX to IED\n";            break;
                            case 2:     resultString += "  Comm lockout/Bad Cmd\n";         break;
                            case 3:     resultString += "  Unexpected Serial Int\n";        break;
                            case 4:     resultString += "  Framing Error\n";                break;
                            case 5:     resultString += "  Timeout Error\n";                break;
                            case 6:     resultString += "  Invalid security key\n";         break;
                            case 7:     resultString += "  sci data overrun\n";             break;
                        }

                        if( DSt->Message[1] & 0x08 )    resultString += "  Last IED write failed\n";

                        if( DSt->Message[3] & 0x01 )
                        {
                            if( (DSt->Message[4] & 0xf0) == 0x30 )
                                resultString += "  L&G S4 RX Firmware Rev: ";
                            else
                                resultString += "  L&G S4 Product Code " + CtiNumStr((DSt->Message[4] & 0xf0)) + string(", Rev ");

                            resultString += CtiNumStr((int)(DSt->Message[4] & 0x0f)) + "." + CtiNumStr((int)DSt->Message[5]) + string("\n");

                            resultString += "  MCT to S4 Data Link:  ";

                            switch( DSt->Message[1] & 0x07 )
                            {
                                case 0: resultString += "MCT to S4 Session failed ($55)\n";               break;
                                case 1: resultString += "MCT to S4 Session failed ($AA)\n";               break;
                                case 2: resultString += "MCT to S4 Session failed (bad Security key)\n";  break;
                                case 3: resultString += "MCT to S4 Session failed (get status)\n";        break;
                                case 4: resultString += "MCT to S4 Session in progress\n";                break;
                                case 5: resultString += "MCT to S4 communication Successful\n";           break;
                            }
                        }
                        else
                        {
                            resultString += "  MCT to S4 Data Link:  MCT IED Meter Port Disabled\n";
                        }

                        break;
                    }

                    case (CtiTableDeviceMCTIEDPort::GeneralElectricKV):
                    {
                        resultString += getName() + " / IED / GEKV status:\n";

                        if( (DSt->Message[0] & 0xff) == 0xff )
                        {
                            resultString += "  KV Status byte uninitialized in MCT\n";
                        }
                        else if( DSt->Message[0] & 0x7f )
                        {
                            if( DSt->Message[0] & 0x01 )    resultString += "  KV Unprogrammed\n";
                            if( DSt->Message[0] & 0x02 )    resultString += "  KV Memory failure\n";
                            if( DSt->Message[0] & 0x04 )    resultString += "  KV Clock error\n";
                            if( DSt->Message[0] & 0x08 )    resultString += "  KV Measurement element error\n";
                            if( DSt->Message[0] & 0x10 )    resultString += "  KV Low battery error\n";
                            if( DSt->Message[0] & 0x20 )    resultString += "  KV Low potential\n";
                            if( DSt->Message[0] & 0x40 )    resultString += "  KV Demand threshold overload\n";
                        }

                        resultString += "Result of last communication with KV:  ";

                        switch( (DSt->Message[1] & 0xf0) >> 4 )
                        {
                            case 0x0:   resultString += "communication successful\n"; break;
                            case 0x1:   resultString += "MCT waiting 2 seconds for refresh after programming write\n"; break;
                            case 0x2:   resultString += "communication terminated after NAK from KV\n"; break;
                            case 0x3:   resultString += "communication terminated after incorrect CRC, cksum, or a NOK\n"; break;
                            case 0x4:   resultString += "communication terminated after SCI framing or overrun error\n"; break;
                            case 0x5:   resultString += "communication did not complete before buffer refresh\n"; break;
                            case 0x6:   resultString += "MCT security key does not match KV key\n"; break;
                            case 0x7:   resultString += "communication terminated after procedure write returned write failure\n"; break;
                            case 0x8:   resultString += "MCT did not recognize type of connected meter\n"; break;
                            case 0x9:   resultString += "MCT contains invalid NVRAM protocol setting\n"; break;
                            case 0xa:   resultString += "communication terminated after partial loss of response packet\n"; break;
                            case 0xb:   resultString += "communication terminated after invalid character received in response packet\n"; break;
                            case 0xd:   resultString += "communication terminated after duplicate packet received from KV\n"; break;

                            case 0xc:
                            case 0xe:
                            case 0xf:   resultString += "undefined error code " + CtiNumStr((DSt->Message[1] & 0xf0) >> 4) + string("\n"); break;
                        }

                        if( DSt->Message[1] & 0x08 )    resultString += "  Last communication failed\n";

                        resultString += "Level of communication reached:";

                        switch( DSt->Message[1] & 0x07 )
                        {
                            case 0x0:   resultString += "  Identification\n"; break;
                            case 0x1:   resultString += "  Negotiate\n"; break;
                            case 0x2:   resultString += "  Logon\n"; break;
                            case 0x3:   resultString += "  Security\n"; break;
                            case 0x4:   resultString += "  Determine meter type/mode of operation\n"; break;
                            case 0x5:   resultString += "  Read GE status byte\n"; break;
                            case 0x6:   resultString += "  Full session established\n"; break;
                            case 0x7:   resultString += "  Logoff/terminate\n"; break;
                        }

                        if( DSt->Message[2] & 0x10 )  resultString += "  Communication with KV terminated after BCH error:\n";

                        resultString += "Firmware version:  ";
                        if(      (DSt->Message[4] & 0xf0) == 0x00 )  resultString += "<= 5.2\n";
                        else if( (DSt->Message[4] & 0xf0) == 0x10 )  resultString += "5.3\n";
                        else if( (DSt->Message[4] & 0xf0) == 0x20 )  resultString += ">= 5.4\n";
                        else if( (DSt->Message[4] & 0xf0) == 0xf0 )  resultString += "unrecognized version\n";
                        else                                         resultString += "bad firmware ID byte: " + CtiNumStr(DSt->Message[4] & 0xf0).xhex() + string("\n");

                        resultString += "Meter type:  ";
                        if(      (DSt->Message[4] & 0x0f) == 0x00 )  resultString += "GE kV\n";
                        else if( (DSt->Message[4] & 0x0f) == 0x01 )  resultString += "GE kV2\n";
                        else                                         resultString += "unknown meter type " + CtiNumStr(DSt->Message[4] & 0x0f) + string("\n");

                        break;
                    }

                    default:
                    {
                        CTILOG_ERROR(dout, "unhandled IED type "<< getIEDPort().getIEDType() <<" for device \""<< getName() <<"\"");
                    }
                }

                ReturnMsg->setResultString( resultString );

                break;
            }

            default:
            {
                CTILOG_ERROR(dout, "unhandled IM->Sequence "<< InMessage.Sequence << " for device \"" << getName() << "\"");
            }
        }
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Mct31xDevice::decodeGetConfigIED(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    string resultString;

    std::auto_ptr<DSTRUCT> DSt(
        new DSTRUCT(InMessage.Buffer.DSt));

    CtiCommandParser parse( InMessage.Return.CommandStr );

    CtiReturnMsg    *ReturnMsg    = NULL;    // Message sent to VanGogh, inherits from Multi

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CTILOG_ERROR(dout, "IED GetConfig Decode for \""<< getName() <<"\"");
    }

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    switch( InMessage.Sequence )
    {
        case EmetconProtocol::GetConfig_IEDTime:
        {
            int i;
            for( i = 0; i < 12; i++ )
            {
                //  break if any bytes are unequal or all zero (therefore, any
                //    zero is a break condition in addition to inequality)
                if( (DSt->Message[i] != DSt->Message[i+1]) ||
                    (DSt->Message[i] == 0) )
                    break;
            }
            if( i == 12 )
            {
                //  we never broke out of the loop - all bytes are equal, the buffer is busted
                ReturnMsg->setResultString( "Device: " + getName() + "\nData buffer is bad, retry command" );
                status = ClientErrors::IedBufferBad;
            }
            else
            {
                switch( getIEDPort().getIEDType() )
                {
                    case CtiTableDeviceMCTIEDPort::AlphaPowerPlus:
                    {
                        for( int j = 0; j < 7; j++ )  //  excluding byte 7 - it's a bitfield, not BCD
                        {
                            //  Convert the bytes from BCD to normal byte values
                            DSt->Message[j] = (((DSt->Message[j] & 0xf0) / 16) * 10) + (DSt->Message[j] & 0x0f);
                        }

                        resultString += getName() + " / IED / current time: ";
                        resultString += CtiNumStr((int)DSt->Message[1]).zpad(2) + string("/") +
                                        CtiNumStr((int)DSt->Message[2]).zpad(2) + "/" +
                                        CtiNumStr((int)DSt->Message[0]).zpad(2) + " " +
                                        CtiNumStr((int)DSt->Message[3]).zpad(2) + ":" +
                                        CtiNumStr((int)DSt->Message[4]).zpad(2) + ":" +
                                        CtiNumStr((int)DSt->Message[5]).zpad(2) + "\n";
                        resultString += "Demand Reset Count: " + CtiNumStr((int)DSt->Message[6]) + string("\n");
                        resultString += "Current TOU Rate: " + (char)('A' + ((DSt->Message[7] & 0x0C) >> 2));

                        break;
                    }

                    case CtiTableDeviceMCTIEDPort::LandisGyrS4:
                    {
                        for( int j = 0; j < 7; j++ )  //  excluding byte 7 - it's a bitfield, not BCD
                        {
                            //  Convert the bytes from BCD to normal byte values
                            DSt->Message[j] = (((DSt->Message[j] & 0xf0) / 16) * 10) + (DSt->Message[j] & 0x0f);
                        }

                        resultString += getName() + " / IED / current time: ";
                        if( DSt->Message[6] <= 5 )
                        {
                            resultString += "(autoread #" + CtiNumStr((int)DSt->Message[6] + 1) + string(") ");
                        }

                        resultString += CtiNumStr((int)DSt->Message[5]).zpad(2) + string("/") +
                                        CtiNumStr((int)DSt->Message[4]).zpad(2) + "/" +
                                        CtiNumStr((int)DSt->Message[3]).zpad(2) + " " +
                                        CtiNumStr((int)DSt->Message[2]).zpad(2) + ":" +
                                        CtiNumStr((int)DSt->Message[1]).zpad(2) + ":" +
                                        CtiNumStr((int)DSt->Message[0]).zpad(2) + "\n";

                        resultString += "Outage count: " + CtiNumStr((int)DSt->Message[7]) + string("\n");
                        resultString += "Current TOU Rate: " + (char)('A' + (DSt->Message[8] & 0x07));

                        break;
                    }

                    case CtiTableDeviceMCTIEDPort::GeneralElectricKV:
                    {
                        resultString += getName() + " / IED / current time: ";

                        int year, month, day, hour, minute, second;
                        int rate,    tou_rtp, meter_class, frequency,
                            holiday, season,  outages,     service;

                        year        =   DSt->Message[0]         >> 1;
                        month       = ((DSt->Message[0] & 0x01) << 3) |
                                      ((DSt->Message[1] & 0xe0) >> 5);
                        day         =  (DSt->Message[1] & 0x1f);

                        hour        =   DSt->Message[2];
                        minute      =   DSt->Message[3];
                        second      =   DSt->Message[4];


                        resultString += CtiNumStr(hour) + ":" + CtiNumStr(minute).zpad(2) + string(":") + CtiNumStr(second).zpad(2) + " ";
                        resultString += CtiNumStr(month) + "/" + CtiNumStr(day) + string("/") + CtiNumStr(year + 2000) + "\n";

                        rate        =  (DSt->Message[5] & 0xe0) >> 5;

                        tou_rtp     =   DSt->Message[6] & 0x03;
                        meter_class =  (DSt->Message[6] & 0x74) >> 3;
                        frequency   =  (DSt->Message[6] & 0x80) >> 7;

                        resultString += "Current TOU Rate: " + (char)('A' + (DSt->Message[8] & 0x07)) + string("\n");

                        holiday     =  (DSt->Message[7] & 0xf0) >> 4;
                        season      =   DSt->Message[7] & 0x0f;

                        outages     =  (DSt->Message[8]         << 8) |
                                        DSt->Message[9];

                        service     =   DSt->Message[10];


                        resultString += "Electrical Service: ";

                        switch( service )
                        {
                            case 0x00:  resultString += "1-Phase, 2-Wire";          break;
                            case 0x01:  resultString += "1-Phase, 3-Wire";          break;
                            case 0x02:  resultString += "3-Phase, 3-Wire (Delta)";  break;
                            case 0x03:  resultString += "3-Phase, 4-Wire (Delta)";  break;
                            case 0x04:  resultString += "3-Phase, 4-Wire (Wye)";    break;
                            case 0x05:  resultString += "Network";                  break;
                            case 0xff:  resultString += "Service Error";            break;
                            default:    resultString += "Unknown service status " + CtiNumStr(service);  break;
                        }

                        resultString += "\n";

                        break;
                    }

                    default:
                    {
                        CTILOG_ERROR(dout, "unhandled IED type "<< getIEDPort().getIEDType() <<" for device \""<< getName() <<"\"");
                    }
                }

                ReturnMsg->setResultString( resultString );
            }

            break;
        }

        case EmetconProtocol::GetConfig_IEDScan:
        {
            switch( getIEDPort().getIEDType() )
            {
                case CtiTableDeviceMCTIEDPort::AlphaPowerPlus:
                {
                    resultString += getName() + " / Alpha Power Plus scan info:\n";

                    if(      DSt->Message[5] == 11 )    resultString += "  Buffer Contains: Alpha Class 11 Billing (Current)\n";
                    else if( DSt->Message[5] == 12 )    resultString += "  Buffer Contains: Alpha Class 12 Billing (Previous)\n";
                    else                                resultString += "  Buffer Contains: Alpha Class " + CtiNumStr((int)DSt->Message[5]) + "\n";

                    resultString += "  Scan Offset:     " + CtiNumStr(((int)DSt->Message[3] * 256) + (int)DSt->Message[4]).spad(3) + "\n";

                    break;
                }

                case CtiTableDeviceMCTIEDPort::LandisGyrS4:
                {
                    resultString += getName() + " / Landis and Gyr S4 scan info:\n";

                    if( DSt->Message[5] == 0 )  resultString += "  Buffer Contains: CTI Billing Data Table #" + CtiNumStr((int)DSt->Message[4] + 1) + string("\n");
                    else                        resultString += "  Buffer Contains: S4 Meter Read Cmd: "      + CtiNumStr((int)DSt->Message[4]) + string("\n");

                    resultString += "  Scan Offset:     " + CtiNumStr(((int)DSt->Message[3] * 256) + (int)DSt->Message[4]).spad(3) + string("\n");

                    break;
                }

                case CtiTableDeviceMCTIEDPort::GeneralElectricKV:
                {
                    int offset, tableid;

                    offset  = DSt->Message[3] << 16 |
                              DSt->Message[4] << 8  |
                              DSt->Message[5];

                    tableid = DSt->Message[6] << 8  |
                              DSt->Message[7];

                    resultString += getName() + " / General Electric KV scan info:\n";

                    if( tableid == 0xffff )
                    {
                        resultString += "  Buffer contains pre-canned TOU data\n";
                    }
                    else
                    {
                        resultString += "  Buffer contains table " + CtiNumStr(tableid) + string("\n");
                    }

                    resultString += "  Scan Offset: " + CtiNumStr(offset) + string("\n");

                    break;
                }

                default:
                {
                    CTILOG_ERROR(dout, "unhandled IED type "<< getIEDPort().getIEDType() <<" for device \""<< getName() <<"\"");
                }
            }

            resultString += "  Scan Rate: " + CtiNumStr(((int)DSt->Message[0] * 15) + 30).spad(4) + string(" seconds\n");
            resultString += "  Buffer refresh delay: " + CtiNumStr((int)DSt->Message[1] * 15).spad(4) + string(" seconds\n");

            if( DSt->Message[2] == 0 )
                DSt->Message[2] = 128;

            resultString += "  Scan Length:     " + CtiNumStr((int)DSt->Message[2]).spad(3) + string(" bytes\n");

            ReturnMsg->setResultString( resultString );

            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unhandled IM->Sequence "<< InMessage.Sequence <<" for device \"" << getName() << "\"");
        }
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Mct31xDevice::decodeGetValueIED(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    INT pid, rateOffset;
    string pointDescriptor, resultString, name, ratename;

    ULONG lValue;

    std::auto_ptr<DSTRUCT> DSt(
        new DSTRUCT(InMessage.Buffer.DSt));

    CtiCommandParser parse( InMessage.Return.CommandStr );

    DOUBLE Value = 0.0;
    CtiTime timestamp;
    CtiDate datestamp;

    CtiPointSPtr    pPoint;
    CtiReturnMsg    *ReturnMsg    = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData        = NULL;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CTILOG_DEBUG(dout, "IED GetValue Decode for \""<< getName() <<"\"");
    }

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    int i;
    for( i = 0; i < 12; i++ )
    {
        //  break if any bytes are unequal or all zero (therefore, any
        //    zero is a break condition in addition to inequality)
        if( (DSt->Message[i] != DSt->Message[i+1]) ||
            (DSt->Message[i] == 0) )
            break;
    }
    if( i == 12 )
    {
        //  we never broke out of the loop - all bytes are equal, the buffer is busted
        resultString += "Device: " + getName() + "\nData buffer is bad, retry command";
        status = ClientErrors::IedBufferBad;
    }
    else
    {
        if( (parse.getCommand() == ScanRequest && getIEDPort().getRealTimeScanFlag()) ||
             parse.getFlags() & CMD_FLAG_GV_DEMAND )
        {
            pid = 10;
        }
        else  //  GV_IEDKwh, GV_IEDKvarh, GV_IEDKvah
        {
            if( parse.getFlags() & CMD_FLAG_GV_KVARH )
            {
                pid = 11;
                name = "kvar";
            }
            else if( parse.getFlags() & CMD_FLAG_GV_KVAH )
            {
                pid = 21;
                name = "kva";
            }
            else //  default request - if( parse.getFlags() & CMD_FLAG_GV_KWH )
            {
                pid =  1;
                name = "kw";
            }

            if( parse.getFlags() & CMD_FLAG_GV_RATET )
            {
                //  NOOP, leave the pid alone
            }
            else if( parse.getFlags() & CMD_FLAG_GV_RATED )
            {
                pid += 8;
                ratename = "rate d";
            }
            else if( parse.getFlags() & CMD_FLAG_GV_RATEC )
            {
                pid += 6;
                ratename = "rate c";
            }
            else if( parse.getFlags() & CMD_FLAG_GV_RATEB )
            {
                pid += 4;
                ratename = "rate b";
            }
            else /*  CMD_FLAG_GV_RATEA  */
            {
                pid += 2;
                ratename = "rate a";
            }
        }

        if( (parse.getCommand() == ScanRequest && getIEDPort().getRealTimeScanFlag()) ||
            parse.getFlags() & CMD_FLAG_GV_DEMAND )
        {
            double demandValue, kvarValue;

            setScanFlag(ScanRateIntegrity, false);

            //  get KW
            pPoint = getDevicePointOffsetTypeEqual( pid, AnalogPointType );

            switch( getIEDPort().getIEDType() )
            {
                case CtiTableDeviceMCTIEDPort::AlphaPowerPlus:
                {
                    Value  = BCDtoBase10( DSt->Message, 3 );
                    Value /= 1000.0;

                    break;
                }
                case CtiTableDeviceMCTIEDPort::LandisGyrS4:
                {
                    Value = MAKEUSHORT(DSt->Message[0], DSt->Message[1]);

                    break;
                }
                case CtiTableDeviceMCTIEDPort::GeneralElectricKV:
                {
                    unsigned long tmp, exponent;

                    tmp  = 0;
                    tmp |=  DSt->Message[0]         << 12;
                    tmp |=  DSt->Message[1]         <<  4;
                    tmp |= (DSt->Message[2] & 0xf0) >>  4;

                    exponent  =  DSt->Message[2] & 0x0f;

                    Value = tmp * pow(2.0, (double) exponent);

                    break;
                }
                default:
                {
                    CTILOG_ERROR(dout, "unhandled IED type "<< getIEDPort().getIEDType() <<" for device \""<< getName() <<"\"");
                }
            }

            if( pPoint )
            {
                Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM( Value );

                pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                    CtiNumStr(Value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

                if(pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor))
                {
                    ReturnMsg->PointData().push_back(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            else
            {
                resultString += getName() + " / IED KW  : " + CtiNumStr(Value) + " - point undefined in DB\n";
            }


            //  get KVAR
            pPoint = getDevicePointOffsetTypeEqual( pid + 10, AnalogPointType );

            switch( getIEDPort().getIEDType() )
            {
                case CtiTableDeviceMCTIEDPort::AlphaPowerPlus:
                {
                    Value  = BCDtoBase10( DSt->Message + 3, 3 );
                    Value /= 1000.0;

                    break;
                }
                case CtiTableDeviceMCTIEDPort::LandisGyrS4:
                {
                    Value = MAKEUSHORT(DSt->Message[2], DSt->Message[3]);

                    break;
                }
                case CtiTableDeviceMCTIEDPort::GeneralElectricKV:
                {
                    unsigned long tmp, exponent;

                    tmp  = 0;
                    tmp |=  DSt->Message[3]         << 12;
                    tmp |=  DSt->Message[4]         <<  4;
                    tmp |= (DSt->Message[5] & 0xf0) >>  4;
                    exponent  =  DSt->Message[5] & 0x0f;

                    Value = tmp * pow(2.0, (double) exponent);

                    break;
                }
                default:
                {
                    CTILOG_ERROR(dout, "unhandled IED type "<< getIEDPort().getIEDType() <<" for device \""<< getName() <<"\"");
                }
            }

            if( pPoint )
            {
                Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM( Value );

                pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                    CtiNumStr(Value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

                if(pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor))
                {
                    ReturnMsg->PointData().push_back(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            else
            {
                pPoint = getDevicePointOffsetTypeEqual( pid + 20, AnalogPointType );

                if( pPoint )
                {
                    Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM( Value );

                    pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                        CtiNumStr(Value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

                    if(pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor))
                    {
                        ReturnMsg->PointData().push_back(pData);
                        pData = NULL;  // We just put it on the list...
                    }
                }
                else
                {
                    //  maybe look for KVA?
                    pPoint = getDevicePointOffsetTypeEqual( pid + 20, AnalogPointType );

                    resultString += getName() + " / IED KVAR/KVA: " + CtiNumStr(Value) + " - point undefined in DB\n";
                }
            }

            switch( getIEDPort().getIEDType() )
            {
                case CtiTableDeviceMCTIEDPort::AlphaPowerPlus:
                {
                    lValue  = BCDtoBase10( DSt->Message + 9, 2 );

                    resultString += getName() + " / IED Power Outage Count: " + CtiNumStr(lValue) + "\n";

                    lValue  = BCDtoBase10( DSt->Message + 11, 1 );

                    if( lValue & 0x08 ) resultString += "Phase A potential is missing\n";
                    if( lValue & 0x04 ) resultString += "Phase B potential is missing\n";
                    if( lValue & 0x02 ) resultString += "Phase C potential is missing\n";

                    break;
                }

                case CtiTableDeviceMCTIEDPort::LandisGyrS4:
                {
                    lValue = MAKEUSHORT(DSt->Message[4], DSt->Message[5]);
                    if( lValue == 0xffff )  lValue = 99999;
                    if( lValue == 0xfffe )  lValue = 88888;
                    if( lValue == 0xfffd )  lValue = 65534;
                    Value = lValue * 0.01;

                    pPoint = getDevicePointOffsetTypeEqual(MCT360_IED_VoltsPhaseA_PointOffset, AnalogPointType);

                    if( pPoint )
                    {
                        Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM( Value );

                        pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                            CtiNumStr(Value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

                        if(pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor))
                        {
                            ReturnMsg->PointData().push_back(pData);
                            pData = NULL;  // We just put it on the list...
                        }
                    }
                    else
                    {
                        resultString += "Phase A Volts: " + CtiNumStr(Value) + string("\n");
                    }

                    lValue = MAKEUSHORT(DSt->Message[6], DSt->Message[7]);
                    if( lValue == 0xffff )  lValue = 99999;
                    if( lValue == 0xfffe )  lValue = 88888;
                    if( lValue == 0xfffd )  lValue = 65534;
                    Value = lValue * 0.01;

                    pPoint = getDevicePointOffsetTypeEqual(MCT360_IED_VoltsPhaseB_PointOffset, AnalogPointType);

                    if( pPoint )
                    {
                        Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM( Value );

                        pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                            CtiNumStr(Value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

                        if(pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor))
                        {
                            ReturnMsg->PointData().push_back(pData);
                            pData = NULL;  // We just put it on the list...
                        }
                    }
                    else
                    {
                        resultString += "Phase B Volts: " + CtiNumStr(Value) + string("\n");
                    }

                    lValue = MAKEUSHORT(DSt->Message[8], DSt->Message[9]);
                    if( lValue == 0xffff )  lValue = 99999;
                    if( lValue == 0xfffe )  lValue = 88888;
                    if( lValue == 0xfffd )  lValue = 65534;
                    Value = lValue * 0.01;

                    pPoint = getDevicePointOffsetTypeEqual(MCT360_IED_VoltsPhaseC_PointOffset, AnalogPointType);

                    if( pPoint )
                    {
                        Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM( Value );

                        pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                        CtiNumStr(Value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

                        if(pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor))
                        {
                            ReturnMsg->PointData().push_back(pData);
                            pData = NULL;  // We just put it on the list...
                        }
                    }
                    else
                    {
                        resultString += "Phase C Volts: " + CtiNumStr(Value) + string("\n");
                    }

                    lValue = MAKEUSHORT(DSt->Message[10], DSt->Message[11]);
                    if( lValue == 0xffff )  lValue = 99999;
                    if( lValue == 0xfffe )  lValue = 88888;
                    if( lValue == 0xfffd )  lValue = 65534;
                    Value = lValue * 0.01;

                    pPoint = getDevicePointOffsetTypeEqual(MCT360_IED_VoltsNeutralCurrent_PointOffset, AnalogPointType);

                    if( pPoint )
                    {
                        Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM( Value );

                        pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                            CtiNumStr(Value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

                        if(pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor))
                        {
                            ReturnMsg->PointData().push_back(pData);
                            pData = NULL;  // We just put it on the list...
                        }
                    }
                    else
                    {
                        resultString += "Neutral current: " + CtiNumStr(Value) + string("\n");
                    }

                    break;
                }

                case CtiTableDeviceMCTIEDPort::GeneralElectricKV:
                {
                    int voltsA, voltsB, voltsC;
                    double kvVoltageMultiplier = 0.10;

                    voltsA = MAKEUSHORT(DSt->Message[ 7], DSt->Message[ 6]);
                    voltsB = MAKEUSHORT(DSt->Message[ 9], DSt->Message[ 8]);
                    voltsC = MAKEUSHORT(DSt->Message[11], DSt->Message[10]);

                    Value = voltsA * kvVoltageMultiplier;
                    pPoint = getDevicePointOffsetTypeEqual(MCT360_IED_VoltsPhaseA_PointOffset, AnalogPointType);

                    if( pPoint )
                    {
                        Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM( Value );

                        pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                            CtiNumStr(Value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

                        if(pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor))
                        {
                            ReturnMsg->PointData().push_back(pData);
                            pData = NULL;  // We just put it on the list...
                        }
                    }
                    else
                    {
                        resultString += "Phase A Volts: " + CtiNumStr(Value) + string("\n");
                    }

                    Value = voltsB * kvVoltageMultiplier;
                    pPoint = getDevicePointOffsetTypeEqual(MCT360_IED_VoltsPhaseB_PointOffset, AnalogPointType);

                    if( pPoint )
                    {
                        Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM( Value );

                        pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                            CtiNumStr(Value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

                        if(pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor))
                        {
                            ReturnMsg->PointData().push_back(pData);
                            pData = NULL;  // We just put it on the list...
                        }
                    }
                    else
                    {
                        resultString += "Phase B Volts: " + CtiNumStr(Value) + string("\n");
                    }

                    Value = voltsC * kvVoltageMultiplier;
                    pPoint = getDevicePointOffsetTypeEqual(MCT360_IED_VoltsPhaseC_PointOffset, AnalogPointType);

                    if( pPoint )
                    {
                        Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM( Value );

                        pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                        CtiNumStr(Value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

                        if(pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor))
                        {
                            ReturnMsg->PointData().push_back(pData);
                            pData = NULL;  // We just put it on the list...
                        }
                    }
                    else
                    {
                        resultString += "Phase C Volts: " + CtiNumStr(Value) + string("\n");
                    }

                    if( DSt->Message[12] == 0xff )
                    {
                        resultString += "Electrical Service Error detected\n";
                    }
                    else if( DSt->Message[12] )
                    {
                        resultString += "Cautions:\n";

                        if( DSt->Message[12] & 0x01 ) resultString += "Polarity, Cross Phase, Reverse Energy Flow\n";
                        if( DSt->Message[12] & 0x02 ) resultString += "Voltage Imbalance\n";
                        if( DSt->Message[12] & 0x04 ) resultString += "Inactive Phase Current\n";
                        if( DSt->Message[12] & 0x08 ) resultString += "Current Imbalance\n";
                        if( DSt->Message[12] & 0x10 ) resultString += "High Distortion\n";
                        if( DSt->Message[12] & 0x20 ) resultString += "Under Voltage\n";
                        if( DSt->Message[12] & 0x40 ) resultString += "Over Voltage\n";
                        if( DSt->Message[12] & 0x80 ) resultString += "High Neutral Current\n";
                    }

                    break;
                }

                default:
                {
                    CTILOG_ERROR(dout, "unhandled IED type "<< getIEDPort().getIEDType() <<" for device \""<< getName() <<"\"");
                }
            }
        }
        else if( parse.getFlags() & CMD_FLAG_GV_RATET )
        {
            pPoint = getDevicePointOffsetTypeEqual( pid, AnalogPointType );

            switch( getIEDPort().getIEDType() )
            {
                case CtiTableDeviceMCTIEDPort::AlphaPowerPlus:
                {
                    if( parse.getFlags() & CMD_FLAG_GV_KVAH || parse.getFlags() & CMD_FLAG_GV_KVARH )
                    {
                        Value  = BCDtoBase10( DSt->Message + 7, 6 );
                    }
                    else
                    {
                        Value  = BCDtoBase10( DSt->Message, 7 );
                        Value /= 100.0;
                    }

                    break;
                }

                case CtiTableDeviceMCTIEDPort::LandisGyrS4:
                {
                    int tmp, j;

                    for( j = 0; j < 3; j++ )
                    {
                        tmp = DSt->Message[j];
                        DSt->Message[j] = DSt->Message[5-j];
                        DSt->Message[5-j] = tmp;
                    }
                    for( j = 0; j < 3; j++ )
                    {
                        tmp = DSt->Message[j+6];
                        DSt->Message[j+6] = DSt->Message[11-j];
                        DSt->Message[11-j] = tmp;
                    }

                    if( parse.getFlags() & CMD_FLAG_GV_KVAH || parse.getFlags() & CMD_FLAG_GV_KVARH )
                    {
                        Value  = BCDtoBase10( DSt->Message + 6, 6 );
                    }
                    else
                    {
                        Value  = BCDtoBase10( DSt->Message, 6 );
                    }

                    break;
                }

                case CtiTableDeviceMCTIEDPort::GeneralElectricKV:
                {
                    long     tmpLong;
                    unsigned tmpUInt;

                    if( parse.getFlags() & CMD_FLAG_GV_KVAH || parse.getFlags() & CMD_FLAG_GV_KVARH )
                    {
                        tmpLong = (DSt->Message[ 6] << 24) |
                                  (DSt->Message[ 7] << 16) |
                                  (DSt->Message[ 8] <<  8) |
                                   DSt->Message[ 9];
                        tmpUInt = (DSt->Message[10] <<  8) |
                                   DSt->Message[11];

                        Value  = tmpLong;
                        Value *= 256.0 * 256.0;  //  effectively, Value << 16
                        Value += tmpUInt;

                        Value /= MCT360_GEKV_KWHMultiplier;
                    }
                    else
                    {
                        tmpLong = (DSt->Message[0] << 24) |
                                  (DSt->Message[1] << 16) |
                                  (DSt->Message[2] <<  8) |
                                   DSt->Message[3];
                        tmpUInt = (DSt->Message[4] <<  8) |
                                   DSt->Message[5];

                        Value  = tmpLong;
                        Value *= 256.0 * 256.0;  //  effectively, Value << 16
                        Value += tmpUInt;

                        Value /= MCT360_GEKV_KWHMultiplier;
                    }

                    break;
                }

                default:
                {
                    CTILOG_ERROR(dout, "unhandled IED type "<< getIEDPort().getIEDType() <<" for device \""<< getName() <<"\"");
                }
            }


            if( pPoint )
            {
                Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM( Value );

                pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                    CtiNumStr(Value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

                if(pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor))
                {
                    ReturnMsg->PointData().push_back(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            else
            {
                resultString += getName() + " / total " + name + "h: " + CtiNumStr(Value) + " - point undefined in DB\n";
            }
        }
        else  //  ye olde typicale KWH reade
        {
            pPoint = getDevicePointOffsetTypeEqual( pid, AnalogPointType );

            switch( getIEDPort().getIEDType() )
            {
                case CtiTableDeviceMCTIEDPort::AlphaPowerPlus:
                {
                    Value  = BCDtoBase10( DSt->Message + 8, 5 );
                    Value /= 100.0;

                    break;
                }

                case CtiTableDeviceMCTIEDPort::LandisGyrS4:
                {
                    Value = 0.0;

                    //  reverse the order so it works with a standard BCD->base10 call
                    char tmp;
                    for( int j = 0; j < 3; j++ )
                    {
                        tmp = DSt->Message[j];
                        DSt->Message[j] = DSt->Message[5-j];
                        DSt->Message[5-j] = tmp;
                    }

                    Value  = BCDtoBase10( DSt->Message, 6 );

                    break;
                }

                case CtiTableDeviceMCTIEDPort::GeneralElectricKV:
                {
                    long     tmpLong;
                    unsigned tmpUInt;

                    tmpLong = (DSt->Message[0] << 24) |
                              (DSt->Message[1] << 16) |
                              (DSt->Message[2] <<  8) |
                               DSt->Message[3];
                    tmpUInt = (DSt->Message[4] <<  8) |
                               DSt->Message[5];

                    Value  = tmpLong;
                    Value *= 256.0 * 256.0;  //  effectively, Value << 16
                    Value += tmpUInt;

                    Value /= MCT360_GEKV_KWHMultiplier;

                    break;
                }

                default:
                {
                    CTILOG_ERROR(dout, "unhandled IED type "<< getIEDPort().getIEDType() <<" for device \""<< getName() <<"\"");

                    pPoint.reset();  //  we can't do a point update - we don't know how to interpret the data
                    Value  = 0.0;

                    break;
                }
            }

            if( pPoint )
            {
                Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM( Value );

                pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                    CtiNumStr(Value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

                if(pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor))
                {
                    ReturnMsg->PointData().push_back(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            else
            {
                resultString += getName() + " / " + name + "h " + ratename + ": " + CtiNumStr(Value) + " - point undefined in DB\n";
            }

            pPoint = getDevicePointOffsetTypeEqual( pid - 1, AnalogPointType );

            switch( getIEDPort().getIEDType() )
            {
                case CtiTableDeviceMCTIEDPort::AlphaPowerPlus:
                {
                    Value  = BCDtoBase10( DSt->Message, 3 );
                    Value /= 1000.0;

                    for( i = 3; i < 8; i++ )
                    {
                        //  Convert the bytes from BCD to normal byte values
                        DSt->Message[i] = (((DSt->Message[i] & 0xf0) / 16) * 10) + (DSt->Message[i] & 0x0f);
                    }

                    datestamp = CtiDate((unsigned)DSt->Message[5],
                                       (unsigned)DSt->Message[4],
                                       (unsigned)DSt->Message[3] + 2000 );

                    timestamp = CtiTime(datestamp,
                                       (unsigned)DSt->Message[6],
                                       (unsigned)DSt->Message[7] );


                    break;
                }

                case CtiTableDeviceMCTIEDPort::LandisGyrS4:
                {
                    Value  = (DSt->Message[7] << 8) + DSt->Message[6];

                    for( i = 8; i < 13; i++ )
                    {
                        //  Convert the bytes from BCD to normal byte values
                        DSt->Message[i] = (((DSt->Message[i] & 0xf0) / 16) * 10) + (DSt->Message[i] & 0x0f);
                    }

                    datestamp = CtiDate((unsigned)DSt->Message[11],
                                       (unsigned)DSt->Message[12],
                                       (unsigned)DSt->Message[10] + 2000);
                    timestamp = CtiTime(datestamp,
                                       (unsigned)DSt->Message[9],
                                       (unsigned)DSt->Message[8]);

                    break;
                }

                case CtiTableDeviceMCTIEDPort::GeneralElectricKV:
                {
                    unsigned long tmp, exponent;
                    unsigned year, month, day, hour, minute, second;

                    tmp  = 0;
                    tmp |=  DSt->Message[6]         << 12;
                    tmp |=  DSt->Message[7]         <<  4;
                    tmp |= (DSt->Message[8] & 0xf0) >>  4;
                    exponent  =  DSt->Message[8] & 0x0f;

                    Value = tmp * pow(2.0, (double) exponent);

                    year        =   DSt->Message[ 9]         >> 1;
                    month       = ((DSt->Message[ 9] & 0x01) << 3) |
                                  ((DSt->Message[10] & 0xe0) >> 5);
                    day         =  (DSt->Message[10] & 0x1f);

                    hour        =   DSt->Message[11];
                    minute      =   DSt->Message[12];


                    datestamp = CtiDate(day, month, year + 2000);
                    timestamp = CtiTime(datestamp, hour, minute);

                    break;
                }

                default:
                {
                    CTILOG_ERROR(dout, "unhandled IED type "<< getIEDPort().getIEDType() <<" for device \""<< getName() <<"\"");

                    pPoint.reset();  //  we can't do a point update - we don't know how to interpret the data
                    Value  = 0.0;

                    break;
                }
            }

            if( pPoint )
            {
                Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM( Value );

                pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                    CtiNumStr(Value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

                if( datestamp.isValid() && timestamp.isValid() )
                {
                    pointDescriptor += " @ " + CtiNumStr(datestamp.month()).zpad(2)      + string("/") +
                                               CtiNumStr(datestamp.dayOfMonth()).zpad(2) + "/" +
                                               CtiNumStr(datestamp.year()).zpad(2)       + " " +
                                               CtiNumStr(timestamp.hour()).zpad(2)       + ":" +
                                               CtiNumStr(timestamp.minute()).zpad(2);

                    if(pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor))
                    {
                        pData->setTime( timestamp.seconds() );

                        ReturnMsg->PointData().push_back(pData);
                        pData = NULL;  // We just put it on the list...
                    }
                }
                else
                {
                    if( !datestamp.isValid() || !timestamp.isValid() )
                    {
                        std::ostringstream logMessage;

                        //  code BL346H is really just the word "bleagh," placed there at the request of CGP and BA
                        logMessage <<"invalid time on device \""<< getName() <<"\", code BL346H"<<
                                endl <<"data:"<< std::hex << std::setfill('0');

                        for( int j = 0; j < 13; j++ )
                        {
                            logMessage <<" "<< std::setw(2) << (int)DSt->Message[j];
                        }

                        CTILOG_ERROR(dout, logMessage);
                    }

                    //  don't send a pointdata msg, it's uninitialized and doesn't matter
                    resultString += pointDescriptor + " @ 00/00/00 00:00 - data not sent, timestamp invalid\n";
                }
            }
            else
            {
                resultString += getName() + " / " + name + " " + ratename + ": " + CtiNumStr(Value);

                if( datestamp.isValid() )
                {
                    resultString += " @ " + CtiNumStr(datestamp.month()).zpad(2)      + string("/") +
                                            CtiNumStr(datestamp.dayOfMonth()).zpad(2) + "/" +
                                            CtiNumStr(datestamp.year()).zpad(2)       + " " +
                                            CtiNumStr(timestamp.hour()).zpad(2)       + ":" +
                                            CtiNumStr(timestamp.minute()).zpad(2);
                }
                else
                {
                    resultString += " @ 00/00/00 00:00";
                }

                resultString += " -- point undefined in DB\n";
            }
        }
    }

    ReturnMsg->setResultString( resultString );

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Mct31xDevice::decodeGetValueKWH(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    INT pid;
    string resultString;

    ULONG accums[3];

    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    DOUBLE Value;
    CtiPointSPtr    pPoint;
    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData     = NULL;

    if( InMessage.Sequence == EmetconProtocol::Scan_Accum )
    {
        setScanFlag(ScanRateAccum, false);
    }

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CTILOG_DEBUG(dout, "Accumulator Decode for \""<< getName() <<"\"");
    }

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    decodeAccumulators(accums, 3, InMessage.Buffer.DSt.Message);

    CtiTime pointTime;
    pointTime -= pointTime.seconds() % 300;

    for(pid = 1; pid <= 3; pid++)
    {
        // 24 bit pulse value
        Value = (double)accums[pid-1];

        //  look for each KYZ point as a PulseAccumulator
        pPoint = getDevicePointOffsetTypeEqual( pid, PulseAccumulatorPointType );

        //  if we can find the point, send a named/UOM'd resultString
        if( pPoint)
        {
            Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM( Value );

            resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                     boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());
            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, PulseAccumulatorPointType, resultString, TAG_POINT_MUST_ARCHIVE);
            if(pData != NULL)
            {
                pData->setTime(pointTime);
                ReturnMsg->PointData().push_back(pData);
                pData = NULL;  // We just put it on the list...

                //  clear out any KYZ 1 messages we might've appended before this point - we now have a real one
                ReturnMsg->setResultString( string() );
            }
        }
        //  else send the raw pulses for pulse input 1
        else if( pid == 1 )
        {
            resultString = ReturnMsg->ResultString( );
            if( resultString != "" )
                resultString += "\n";

            resultString += getName() + " / KYZ " + CtiNumStr(pid) + " = " + CtiNumStr((int)Value) + "  --  POINT UNDEFINED IN DB";
            ReturnMsg->setResultString( resultString );
        }
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Mct31xDevice::decodeGetValueDemand(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    INT pnt_offset, byte_offset;
    ULONG i,x;
    INT pid;
    string resultString;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    int demand_rate;
    unsigned long pulses;
    bool bad_data;
    PointQuality_t quality;

    DOUBLE Value;
    CtiPointNumericSPtr   pPoint;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    CtiTime pointTime;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CTILOG_DEBUG(dout, "Demand Decode for \""<< getName() <<"\"");
    }

    setScanFlag(ScanRateIntegrity, false);

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    demand_rate = getDemandInterval();

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    pointTime -= pointTime.seconds() % getDemandInterval();

    for(pnt_offset = 1; pnt_offset <= 3; pnt_offset++)
    {
        byte_offset = ((pnt_offset - 1) * 2) + 1;          // First byte is the status byte.

        pulses = MAKEUSHORT(DSt->Message[byte_offset + 1], DSt->Message[byte_offset]);

        //  look for the demand accumulator point for this offset
        pPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual( pnt_offset, DemandAccumulatorPointType ));

        if( checkDemandQuality(pulses, quality, bad_data) )
        {
            Value = 0.0;
        }
        else
        {
            //  if no fatal problems with the quality,
            //    adjust for the demand interval
            Value = pulses * (3600 / demand_rate);

            if( pPoint )
            {
                //    and the UOM
                Value = pPoint->computeValueForUOM(Value);
            }
        }

        // handle demand data here
        if( pPoint)
        {
            resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                     pPoint->getPointUnits().getDecimalPlaces());

            if(pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, DemandAccumulatorPointType, resultString))
            {
                pData->setTime( pointTime );

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

            resultString += getName() + " / Demand " + CtiNumStr(pnt_offset) + " = " + CtiNumStr(Value) + "  --  POINT UNDEFINED IN DB";
            ReturnMsg->setResultString(resultString);
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
            CTILOG_ERROR(dout, "No demand accumulators defined in DB for \""<< getName() <<"\"");

            delete ReturnMsg;
        }
    }

    return status;
}


YukonError_t Mct31xDevice::decodeGetValuePeak(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    double    Value;
    string resultString;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    CtiPointSPtr         pPoint;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CTILOG_DEBUG(dout, "Min/Max On/Off-Peak Decode for \""<< getName() <<"\"");
    }

    resetScanFlag(ScanRateGeneral);

    /* this means we are getting NON-demand accumulator points */
    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    for( int i = 0; i < 6; i++ )
    {
        //  ACH:  check error bits
        Value = MAKEUSHORT(DSt->Message[i*2+1], (DSt->Message[i*2+0] & 0x3f) );

        //  turn raw pulses into a demand reading
        Value *= DOUBLE(3600 / getDemandInterval());

        // look for the appropriate point
        pPoint = getDevicePointOffsetTypeEqual( 10 + i, DemandAccumulatorPointType );

        if( pPoint )
        {
            CtiTime pointTime;

            Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(Value);

            resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                     boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

            if( InMessage.Sequence == EmetconProtocol::GetValue_FrozenPeakDemand )
            {
                pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, DemandAccumulatorPointType, resultString);
                if(pData != NULL)
                {
                    pointTime -= pointTime.seconds() % getDemandInterval();
                    pData->setTime( pointTime );
                    ReturnMsg->PointData().push_back(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            else
            {
                resultString = ReturnMsg->ResultString() + resultString + "\n";
                ReturnMsg->setResultString(resultString);
            }
        }
        else
        {
            resultString += getName() + " / demand accumulator offset " + CtiNumStr(10+i) + " = " + CtiNumStr(Value) + "  --  point undefined in DB\n";
            ReturnMsg->setResultString(resultString);
        }
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Mct31xDevice::decodeScanLoadProfile(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
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
    CtiReturnMsg    *return_msg = 0;  // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *point_data = 0;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CTILOG_DEBUG(dout, "Load Profile Scan Decode for \"" << getName() << "\"");
    }

    return_msg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    return_msg->setUserMessageId(InMessage.Return.UserID);

    if( (retrieved_channel   = parse.getiValue("scan_loadprofile_channel", 0)) &&
        (retrieved_block_num = parse.getiValue("scan_loadprofile_block",   0)) )
    {
        if( isDebugLudicrous() )
        {
            CTILOG_DEBUG(dout,
                    endl <<"retrieved_channel   = "<< retrieved_channel <<
                    endl <<"retrieved_block_num = "<< retrieved_block_num
                    );
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

        point = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual( retrieved_channel + PointOffset_LoadProfileOffset, DemandAccumulatorPointType ));

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
                    CTILOG_DEBUG(dout, "attempt to decode current load profile block for \""<< getName() <<"\" - aborting decode"<<
                            endl <<"commandstr = "<< InMessage.Return.CommandStr
                            );
                }

                result_string = "Attempt to decode current load profile block for \"" + getName() + "\" - aborting decode ";
            }
            else if( retrieved_block_start < _lastLPTime[retrieved_channel - 1] )
            {
                result_string  = "Block < lastLPTime for device \"" + getName() + "\" - aborting decode";

                if( getMCTDebugLevel(DebugLevel_LoadProfile) )
                {
                    CTILOG_DEBUG(dout, result_string <<
                            endl <<"commandstr            = "<< InMessage.Return.CommandStr <<
                            endl <<"retrieved_block_num   = "<< retrieved_block_num <<
                            endl <<"retrieved_channel     = "<< retrieved_channel <<
                            endl <<"retrieved_block_start = "<< retrieved_block_start <<
                            endl <<"_lastLPTime           = "<< _lastLPTime[retrieved_channel - 1]
                            );
                }


            }
            else
            {
                for( int interval_offset = 0; interval_offset < 6; interval_offset++ )
                {
                    //  error code in the top 5 bits - parsed by checkLoadProfileQuality
                    pulses   = DSt->Message[interval_offset*2 + 1];
                    pulses <<= 8;
                    pulses  |= DSt->Message[interval_offset*2 + 2];

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
            result_string = "No load profile point defined for '" + getName() + "' demand accumulator " + CtiNumStr(retrieved_channel);
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


void Mct31xDevice::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    INT iTemp;

    Inherited::DecodeDatabaseReader(rdr);

    if( getType() == TYPEMCT360 ||
        getType() == TYPEMCT370 )
    {
        _iedPort.DecodeDatabaseReader( rdr );

        if( isDebugLudicrous() )
        {
            Cti::FormattedList itemList;

            itemList.add("Default data class")  << _iedPort.getDefaultDataClass();
            itemList.add("Default data offset") << _iedPort.getDefaultDataOffset();
            itemList.add("Device ID")           << _iedPort.getDeviceID();
            itemList.add("IED Scan Rate")       << _iedPort.getIEDScanRate();
            itemList.add("IED Type")            << _iedPort.getIEDType();
            itemList.add("Password")            << _iedPort.getPassword();
            itemList.add("Real Time Scan Flag") << _iedPort.getRealTimeScanFlag();

            CTILOG_DEBUG(dout, "DB read for \""<< getName() <<"\""<<
                    itemList
                    );
        }
    }
}

}
}

