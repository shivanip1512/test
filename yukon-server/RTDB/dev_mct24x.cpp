/*-----------------------------------------------------------------------------*
*
* File:   dev_mct2XX
*
* Date:   5/3/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_mct2XX.cpp-arc  $
* REVISION     :  $Revision: 1.27 $
* DATE         :  $Date: 2005/02/10 23:24:00 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include <windows.h>
#include "device.h"
#include "devicetypes.h"
#include "dev_mct24X.h"
#include "logger.h"
#include "mgr_point.h"
#include "porter.h"
#include "pt_numeric.h"
#include "numstr.h"
#include "dllyukon.h"

set< CtiDLCCommandStore > CtiDeviceMCT24X::_commandStore;


CtiDeviceMCT24X::CtiDeviceMCT24X( )
{
}

CtiDeviceMCT24X::CtiDeviceMCT24X( const CtiDeviceMCT24X &aRef )
{
    *this = aRef;
}

CtiDeviceMCT24X::~CtiDeviceMCT24X( )
{
}

CtiDeviceMCT24X& CtiDeviceMCT24X::operator=(const CtiDeviceMCT24X& aRef)
{
    if( this != &aRef )
    {
        Inherited::operator=( aRef );
    }

    return *this;
}


bool CtiDeviceMCT24X::initCommandStore()
{
    bool failed = false;

    CtiDLCCommandStore cs;

    cs._cmd     = CtiProtocolEmetcon::GetConfig_GroupAddress;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT2XX_GroupAddrPos,
                             (int)MCT2XX_GroupAddrLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_GroupAddr_GoldSilver;
    cs._io      = IO_WRITE | Q_ARMC;
    cs._funcLen = make_pair( (int)MCT2XX_GroupAddrGoldSilverPos,
                             (int)MCT2XX_GroupAddrGoldSilverLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_GroupAddr_Bronze;
    cs._io      = IO_WRITE | Q_ARMC;
    cs._funcLen = make_pair( (int)MCT2XX_GroupAddrBronzePos,
                             (int)MCT2XX_GroupAddrBronzeLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_GroupAddr_Lead;
    cs._io      = IO_WRITE | Q_ARMC;
    cs._funcLen = make_pair( (int)MCT2XX_GroupAddrLeadPos,
                             (int)MCT2XX_GroupAddrLeadLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::Scan_General;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT24X_StatusPos,
                             (int)MCT24X_StatusLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::Scan_Accum;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair( (int)MCT24X_MReadPos,
                             (int)MCT24X_MReadLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetValue_Default;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair( (int)MCT24X_MReadPos,
                             (int)MCT24X_MReadLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutValue_KYZ;
    cs._io      = IO_WRITE | Q_ARMC;
    cs._funcLen = make_pair( (int)MCT24X_PutMReadPos,
                             (int)MCT24X_PutMReadLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::Scan_Integrity;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT24X_DemandPos,
                             (int)MCT24X_DemandLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetValue_Demand;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT24X_DemandPos,
                             (int)MCT24X_DemandLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::Scan_LoadProfile;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( 0,0 );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetStatus_External;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT24X_StatusPos,
                             (int)MCT24X_StatusLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetStatus_LoadProfile;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT24X_LPStatusPos,
                             (int)MCT24X_LPStatusLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetConfig_DemandInterval;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT24X_DemandIntervalPos,
                             (int)MCT24X_DemandIntervalLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_DemandInterval;
    cs._io      = IO_WRITE | Q_ARMC;
    cs._funcLen = make_pair( (int)MCT24X_DemandIntervalPos,
                             (int)MCT24X_DemandIntervalLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetConfig_LoadProfileInterval;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT24X_LPIntervalPos,
                             (int)MCT24X_LPIntervalLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_LoadProfileInterval;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair( (int)MCT_Function_LPInt, 0 );
    _commandStore.insert( cs );


    return failed;
}


bool CtiDeviceMCT24X::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    if(_commandStore.empty())  // Must initialize!
    {
        CtiDeviceMCT24X::initCommandStore();
    }

    DLCCommandSet::iterator itr = _commandStore.find(CtiDLCCommandStore(cmd));

    if( itr != _commandStore.end() )
    {
        CtiDLCCommandStore &cs = *itr;
        function = cs._funcLen.first;             // Copy over the found function!
        length = cs._funcLen.second;              // Copy over the found length!
        io = cs._io;                              // Copy over the found io indicator!

        found = true;

        if( getType() == TYPEMCT250 &&
            (cmd == CtiProtocolEmetcon::Scan_General || cmd == CtiProtocolEmetcon::GetStatus_External) )
        {
            function = MCT250_StatusPos;
            length   = MCT250_StatusLen;
        }
    }
    else                                         // Look in the parent if not found in the child!
    {
        found = Inherited::getOperation(cmd, function, length, io);
    }

    return found;
}


ULONG CtiDeviceMCT24X::calcNextLPScanTime( void )
{
    RWTime Now, blockStart, nextTime;
    unsigned long midnightOffset;
    int lpBlockSize, lpDemandRate, lpMaxBlocks;

    CtiPointBase *pPoint = getDevicePointOffsetTypeEqual(1 + OFFSET_LOADPROFILE_OFFSET, DemandAccumulatorPointType);

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


INT CtiDeviceMCT24X::calcAndInsertLPRequests(OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList)
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


bool CtiDeviceMCT24X::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
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


/*
 *  ResultDecode MUST decode all CtiDLCCommand_t which are defined in the initCommandStore object.  The only exception to this
 *  would be a child whose decode was identical to the parent, but whose request was done differently..
 *  This MAY be the case for example in an IED scan.
 */
INT CtiDeviceMCT24X::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;


    switch(InMessage->Sequence)
    {
        case (CtiProtocolEmetcon::Scan_LoadProfile):
        {
            status = decodeScanLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (CtiProtocolEmetcon::Scan_General):
        case (CtiProtocolEmetcon::GetStatus_External):
        {
            status = decodeScanStatus(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (CtiProtocolEmetcon::GetStatus_Disconnect):
        {
            status = decodeGetStatusDisconnect(InMessage, TimeNow, vgList, retList, outList);
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

        case (CtiProtocolEmetcon::GetConfig_DemandInterval):  //  handled in dev_mct
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


INT CtiDeviceMCT24X::decodeScanLoadProfile(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
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

            point = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( 1 + OFFSET_LOADPROFILE_OFFSET, DemandAccumulatorPointType );

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


INT CtiDeviceMCT24X::decodeScanStatus(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    unsigned long timeStamp;
    double Value;
    RWCString valReport;

    CtiPointNumeric *pPoint    = NULL;
    CtiReturnMsg    *ReturnMsg = NULL;  // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData     = NULL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** General/Status Scan Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    //  ACH:  are these necessary?  /mskf
    resetScanFreezePending( );
    resetScanFreezeFailed( );

    resetScanPending();
    setMCTScanPending(ScanRateGeneral, false);

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiPointBase    *pPoint;
        CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg *pData     = NULL;

        double Value;
        RWCString rwtemp, disc;

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        if( getType() == TYPEMCT250 )
        {
            int status[4], i;

            status[0] = InMessage->Buffer.DSt.Message[0] & 0x40;
            status[1] = InMessage->Buffer.DSt.Message[0] & 0x80;
            status[2] = InMessage->Buffer.DSt.Message[2] & 0x02;
            status[3] = InMessage->Buffer.DSt.Message[2] & 0x04;

            for( i = 0; i < 4; i++ )
            {
                pPoint = getDevicePointOffsetTypeEqual( i + 1, StatusPointType );

                Value = CLOSED;

                if( status[i] )
                {
                    Value = CLOSED;
                    disc = " CLOSED";
                }
                else
                {
                    Value = OPENED;
                    disc = " OPEN";
                }

                //  Send this value to requestor via retList.
                if(pPoint != NULL)
                {
                    rwtemp = ResolveStateName(pPoint->getStateGroupID(), Value);

                    if( rwtemp != "" )
                    {
                        rwtemp = getName() + " / " + pPoint->getName() + ":" + rwtemp;
                    }
                    else
                    {
                        rwtemp = getName() + " / " + pPoint->getName() + ":" + disc;
                    }


                    pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, StatusPointType, rwtemp);
                    if(pData != NULL)
                    {
                        ReturnMsg->PointData().insert(pData);
                        pData = NULL;  // We just put it on the list...

                        //  blank out any complaints that may have been generated
                        ReturnMsg->setResultString( RWCString() );
                    }
                }
                else if( i == 0 )
                {
                    //  complain if point 1 isn't defined (this will be stomped later if any of 2-4 are defined)
                    rwtemp  = getName() + " / No external status points defined in database";
                    ReturnMsg->setResultString( rwtemp );
                }
            }
        }
        else
        {
            pPoint = getDevicePointOffsetTypeEqual(1, StatusPointType);

            Value = CLOSED;

            switch(InMessage->Buffer.DSt.Message[0])
            {
                case MCT24X_Status_Closed:
                {
                    Value  = CLOSED;
                    disc   = " SERVICE CONNECT ENABLED";
                    break;
                }
                case MCT24X_Status_Open:
                {
                    Value  = OPENED;
                    disc   = " SERVICE DISCONNECTED";
                    break;
                }
                default:
                {
                    Value  = INVALID;
                    disc   = " UNKNOWN / No Disconnect Status";
                    break;
                }
            }


            //  Send this value to requestor via retList.
            if(pPoint != NULL)
            {
                rwtemp = ResolveStateName(pPoint->getStateGroupID(), Value);

                if( rwtemp != "" )
                {
                    rwtemp = getName() + " / " + pPoint->getName() + ":" + rwtemp;
                }
                else
                {
                    rwtemp = getName() + " / " + pPoint->getName() + ":" + disc;
                }

                pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, StatusPointType, rwtemp);

                if(pData != NULL)
                {
                    ReturnMsg->PointData().insert(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            else
            {
                rwtemp = getName() + " / Disconnect:" + disc + "  --  point undefined in DB";
                ReturnMsg->setResultString( rwtemp );
            }
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT24X::decodeGetStatusLoadProfile( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
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


/*
 *  This code handles the decode for all 24X series model configs..
 */
INT CtiDeviceMCT24X::decodeGetConfigModel(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if( !(status = decodeCheckErrorReturn(InMessage, retList, outList)) )
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

        if( InMessage->Buffer.DSt.Message[2] & 0x01 )
        {
            options+= RWCString("  Latched loads\n");
        }
        if( InMessage->Buffer.DSt.Message[2] & 0x02 )
        {
            options+= RWCString("  Timed loads\n");
        }
        if( InMessage->Buffer.DSt.Message[2] & 0x40 )
        {
            options+= RWCString("  Extended addressing\n");
        }
        if( InMessage->Buffer.DSt.Message[2] & 0x80 )
        {
            options+= RWCString("  Metering of basic kWh\n");
        }

        if( InMessage->Buffer.DSt.Message[3] & 0x01 )
        {
            options+= RWCString("  Time-of-demand\n");
        }
        if( InMessage->Buffer.DSt.Message[3] & 0x04 )
        {
            options+= RWCString("  Load survey\n");
        }
        if( InMessage->Buffer.DSt.Message[3] & 0x08 )
        {
            options+= RWCString("  Full group address support\n");
        }
        if( InMessage->Buffer.DSt.Message[3] & 0x10 )
        {
            options+= RWCString("  Feedback load control\n");
        }
        if( InMessage->Buffer.DSt.Message[3] & 0x40 )
        {
            options+= RWCString("  Volt/VAR control\n");
        }
        if( InMessage->Buffer.DSt.Message[3] & 0x80 )
        {
            options+= RWCString("  Capacitor control\n");
        }

        if( (ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL )
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


