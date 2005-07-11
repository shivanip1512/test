/*-----------------------------------------------------------------------------*
*
* File:   dev_mct31X
*
* Date:   4/24/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_mct31X.cpp-arc  $
* REVISION     :  $Revision: 1.45 $
* DATE         :  $Date: 2005/07/11 20:06:44 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include "devicetypes.h"
#include "tbl_ptdispatch.h"
#include "dev_mct31X.h"
#include "logger.h"
#include "mgr_point.h"
#include "pt_numeric.h"
#include "numstr.h"
#include "dllyukon.h"

using Cti::Protocol::Emetcon;


const double CtiDeviceMCT31X::MCT360_GEKV_KWHMultiplier = 2000000.0;

set< CtiDLCCommandStore > CtiDeviceMCT31X::_commandStore;

CtiDeviceMCT31X::CtiDeviceMCT31X( )
{
    for( int i = 0; i < MCT31X_ChannelCount; i++ )
    {
        _lastLPTime[i] = RWTime(0UL);
    }
}

CtiDeviceMCT31X::CtiDeviceMCT31X( const CtiDeviceMCT31X &aRef )
{
    *this = aRef;
}

CtiDeviceMCT31X::~CtiDeviceMCT31X( ) { }

CtiDeviceMCT31X& CtiDeviceMCT31X::operator=( const CtiDeviceMCT31X &aRef )
{
    if( this != &aRef )
    {
        //  ACH
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - in CtiDeviceMCT31X::operator= **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        Inherited::operator=( aRef );
    }

    return *this;
}


CtiTableDeviceMCTIEDPort &CtiDeviceMCT31X::getIEDPort( void )
{
    return _iedPort;
}


bool CtiDeviceMCT31X::initCommandStore( )
{
    bool failed = false;

    CtiDLCCommandStore cs;

    cs._cmd     = Emetcon::Scan_General;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair( (int)MCT31X_FuncReadDemandPos,
                             (int)MCT31X_FuncReadStatusLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::Scan_Integrity;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair( (int)MCT31X_FuncReadDemandPos,
                             (int)MCT31X_FuncReadDemandLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::GetValue_Demand;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair( (int)MCT31X_FuncReadDemandPos,
                             (int)MCT31X_FuncReadDemandLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::Scan_LoadProfile;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair(0,0);
    _commandStore.insert( cs );

    cs._cmd      = Emetcon::GetValue_PeakDemand;
    cs._io       = Emetcon::IO_Function_Read;
    cs._funcLen  = make_pair((int)MCT3XX_FuncReadMinMaxDemandPos, 12);
    _commandStore.insert( cs );

    cs._cmd      = Emetcon::GetValue_FrozenPeakDemand;
    cs._io       = Emetcon::IO_Function_Read;
    cs._funcLen  = make_pair((int)MCT3XX_FuncReadFrozenDemandPos, 12);
    _commandStore.insert( cs );

    //  add the 2 other channels for 318s
    cs._cmd     = Emetcon::PutValue_KYZ2;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair( (int)MCT3XX_PutMRead2Pos,
                             (int)MCT3XX_PutMReadLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::PutValue_KYZ3;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair( (int)MCT3XX_PutMRead3Pos,
                             (int)MCT3XX_PutMReadLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::GetStatus_External;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair( (int)MCT31X_FuncReadDemandPos,
                             (int)MCT31X_FuncReadStatusLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::GetConfig_Multiplier2;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair( (int)MCT3XX_Mult2Pos,
                             (int)MCT3XX_MultLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::GetConfig_Multiplier3;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair( (int)MCT3XX_Mult3Pos,
                             (int)MCT3XX_MultLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::PutConfig_Multiplier2;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair( (int)MCT3XX_Mult2Pos,
                             (int)MCT3XX_MultLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::PutConfig_Multiplier3;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair( (int)MCT3XX_Mult3Pos,
                             (int)MCT3XX_MultLen );
    _commandStore.insert( cs );

    //  these are commands for the 360 and 370 only

    //  scan address and length are identical for the p+ and s4
    cs._cmd     = Emetcon::GetConfig_IEDScan;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair( (int)MCT360_IEDScanPos,
                             (int)MCT360_IEDScanLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::PutConfig_IEDScan;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair( (int)MCT360_IEDScanPos, 2 );  //  just 2 bytes - seconds and delay
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::PutConfig_IEDClass;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair( (int)MCT360_IEDClassPos,
                             (int)MCT360_IEDClassLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::GetConfig_IEDTime;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair( (int)MCT360_IEDTimePos,
                             (int)MCT360_IEDTimeLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::GetValue_IEDDemand;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair( (int)MCT360_IEDDemandPos,
                             (int)MCT360_IEDReqLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::GetValue_IEDKwh;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair( (int)MCT360_IEDKwhPos,
                             (int)MCT360_IEDReqLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::GetValue_IEDKvarh;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair( (int)MCT360_IEDKvarhPos,
                             (int)MCT360_IEDReqLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::GetValue_IEDKvah;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair( (int)MCT360_IEDKvahPos,
                             (int)MCT360_IEDReqLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::PutValue_IEDReset;
    cs._io      = Emetcon::IO_Function_Write;
    cs._funcLen = make_pair( 0, 0 );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::GetStatus_IEDLink;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair( (int)MCT360_IEDLinkPos,
                             (int)MCT360_IEDLinkLen );
    _commandStore.insert( cs );

    return failed;
}


bool CtiDeviceMCT31X::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    if( _commandStore.empty( ) )  // Must initialize!
    {
        CtiDeviceMCT31X::initCommandStore( );
    }

    DLCCommandSet::iterator itr = _commandStore.find( CtiDLCCommandStore( cmd ) );

    //  the 318L is the only 31x that supports load profile that we're concerned about here, and it'd be silly to make another class for one function
    //    we want it to stop here, no matter what, no Inherited::anything...
    if( getType( ) != TYPEMCT318L &&
        cmd == Emetcon::Scan_LoadProfile )
    {
        //  just for emphasis
        found = false;
    }
    else if( itr != _commandStore.end( ) )   //  It's prego!
    {
        CtiDLCCommandStore &cs = *itr;

        function = cs._funcLen.first;   //  Grab the funcLen pair we just found
        length   = cs._funcLen.second;  //
        io       = cs._io;

        found = true;
    }
    else                                //  Look in the parent if not found in the child!
    {
        found = Inherited::getOperation( cmd, function, length, io );
    }

    return found;
}


ULONG CtiDeviceMCT31X::calcNextLPScanTime( void )
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

       _nextLPScanTime -= _nextLPScanTime % 300;
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

        for( int i = 0; i < MCT31X_ChannelCount; i++ )
        {
            CtiPointBase *pPoint = getDevicePointOffsetTypeEqual((i+1) + MCT_PointOffset_LoadProfileOffset, DemandAccumulatorPointType);

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

                    CtiTablePointDispatch pd(pPoint->getPointID());

                    if(pd.Restore().errorCode() == RWDBStatus::ok)
                    {
                        _lastLPTime[i] = pd.getTimeStamp().rwtime().seconds();
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

/*
    if( getMCTDebugLevel(MCTDebug_LoadProfile) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << getName() << "'s next Load Profile request at " << RWTime(nextTime) << endl;
    }
*/
    return _nextLPScanTime;
}


INT CtiDeviceMCT31X::calcAndInsertLPRequests(OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList)
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

        for( int i = 0; i < MCT31X_ChannelCount; i++ )
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


bool CtiDeviceMCT31X::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
{
    bool retVal = false;
    int lpBlockAddress, lpChannel;

    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint - LP parse value check **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "parse.getiValue(\"scan_loadprofile_block\",   0) = " << parse.getiValue("scan_loadprofile_block", 0) << endl;
        dout << "parse.getiValue(\"scan_loadprofile_channel\", 0) = " << parse.getiValue("scan_loadprofile_channel", 0) << endl;
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
        OutMessage->Buffer.BSt.IO       = Emetcon::IO_Function_Read;

        retVal = true;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - Improperly formed LP request discarded for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;;
        }

        retVal = false;
    }

    return retVal;
}



INT CtiDeviceMCT31X::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    switch(InMessage->Sequence)
    {
        case Emetcon::Scan_General:
        case Emetcon::GetStatus_External:
        {
            //  A general scan for any MCT does status decode only.
            status = decodeStatus(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetStatus_IEDLink:
        {
            status = decodeGetStatusIED(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::Scan_Accum:
        case Emetcon::GetValue_Default:
        {
            status = decodeGetValueKWH(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::Scan_Integrity:
        {
            //  to catch the IED case
            setMCTScanPending(ScanRateIntegrity, false);  //  resetScanPending();
        }
        case Emetcon::GetValue_Demand:
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
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - status scan error codes for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                status = decodeGetValueDemand(InMessage, TimeNow, vgList, retList, outList);
            }
            break;
        }

        case (Emetcon::Scan_LoadProfile):
        {
            status = decodeScanLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::GetValue_PeakDemand):
        case (Emetcon::GetValue_FrozenPeakDemand):
        {
            status = decodeGetValuePeak(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::GetConfig_IEDTime):
        case (Emetcon::GetConfig_IEDScan):
        {
            status = decodeGetConfigIED(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::GetValue_IED):
        case (Emetcon::GetValue_IEDDemand):
        case (Emetcon::GetValue_IEDKvah):
        case (Emetcon::GetValue_IEDKvarh):
        case (Emetcon::GetValue_IEDKwh):
        {
            status = decodeGetValueIED(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        default:
        {
            status = Inherited::ResultDecode(InMessage, TimeNow, vgList, retList, outList);

            if(status != NORMAL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - errors on Inherited::ResultDecode for device \"" + getName() + "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    }

    return status;
}


INT CtiDeviceMCT31X::decodeStatus(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, bool expectMore)
{
    INT status = NORMAL;
    ULONG i;
    USHORT StatusData[8];
    USHORT SaveCount;
    RWCString resultString;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    DOUBLE Value;

    CtiPointBase    *pPoint;
    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Status Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    setMCTScanPending(ScanRateGeneral, false);  //resetScanPending();

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

        extractStatusData(InMessage, getType(), StatusData);

        //  the only status points we really care about - comm status is handled higher up by porter
        for( int i = 1; i <= 16; i++ )
        {
            if( (pPoint = getDevicePointOffsetTypeEqual(i, StatusPointType)) != NULL )
            {
                Value = translateStatusValue(pPoint->getPointOffset(), pPoint->getType(), getType(), StatusData);

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
                    ReturnMsg->PointData().insert(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList, expectMore );
    }

    return status;
}


INT CtiDeviceMCT31X::decodeGetStatusIED(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;
    INT pid, rateOffset;
    RWCString resultString, name, ratename;

    ULONG lValue;

    INT ErrReturn = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    CtiCommandParser parse( InMessage->Return.CommandStr );

    DOUBLE demandValue, Value;
    RWTime timestamp;
    RWDate datestamp;
    CtiPointBase    *pPoint       = NULL;
    CtiPointBase    *pDemandPoint = NULL;
    CtiReturnMsg    *ReturnMsg    = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData        = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** IED GetStatus Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

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

        for( int i = 0; i < 12; i++ )
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
            status = ALPHABUFFERERROR;
        }
        else
        {
            switch( InMessage->Sequence )
            {
                case Emetcon::GetStatus_IEDLink:
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
                                default:    resultString += "  Error code " + CtiNumStr((DSt->Message[1] & 0xf0) >> 4) + " not implemented\n";
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
                                    resultString += "  L&G S4 Product Code " + CtiNumStr((DSt->Message[4] & 0xf0)) + ", Rev ";

                                resultString += CtiNumStr((int)(DSt->Message[4] & 0x0f)) + "." + CtiNumStr((int)DSt->Message[5]) + "\n";

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
                                case 0xf:   resultString += "undefined error code " + CtiNumStr((DSt->Message[1] & 0xf0) >> 4) + "\n"; break;
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
                            else                                         resultString += "bad firmware ID byte: " + CtiNumStr(DSt->Message[4] & 0xf0).xhex() + "\n";

                            resultString += "Meter type:  ";
                            if(      (DSt->Message[4] & 0x0f) == 0x00 )  resultString += "GE kV\n";
                            else if( (DSt->Message[4] & 0x0f) == 0x01 )  resultString += "GE kV2\n";
                            else                                         resultString += "unknown meter type " + CtiNumStr(DSt->Message[4] & 0x0f) + "\n";

                            break;
                        }

                        default:
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - unhandled IED type " << getIEDPort().getIEDType() << " for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }

                    ReturnMsg->setResultString( resultString );

                    break;
                }

                default:
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - unhandled IM->Sequence " << InMessage->Sequence << " for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT31X::decodeGetConfigIED(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;
    INT pid, rateOffset;
    RWCString resultString, name, ratename;

    ULONG lValue;

    INT ErrReturn = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    CtiCommandParser parse( InMessage->Return.CommandStr );

    DOUBLE demandValue, Value;
    RWTime timestamp;
    RWDate datestamp;
    CtiPointBase    *pPoint       = NULL;
    CtiPointBase    *pDemandPoint = NULL;
    CtiReturnMsg    *ReturnMsg    = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData        = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** IED GetConfig Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

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

        switch( InMessage->Sequence )
        {
            case Emetcon::GetConfig_IEDTime:
            {
                for( int i = 0; i < 12; i++ )
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
                    status = ALPHABUFFERERROR;
                }
                else
                {
                    switch( getIEDPort().getIEDType() )
                    {
                        case CtiTableDeviceMCTIEDPort::AlphaPowerPlus:
                        {
                            for( int i = 0; i < 7; i++ )  //  excluding byte 7 - it's a bitfield, not BCD
                            {
                                //  Convert the bytes from BCD to normal byte values
                                DSt->Message[i] = (((DSt->Message[i] & 0xf0) / 16) * 10) + (DSt->Message[i] & 0x0f);
                            }

                            resultString += getName() + " / IED / current time: ";
                            resultString += CtiNumStr((int)DSt->Message[1]).zpad(2) + "/" +
                                            CtiNumStr((int)DSt->Message[2]).zpad(2) + "/" +
                                            CtiNumStr((int)DSt->Message[0]).zpad(2) + " " +
                                            CtiNumStr((int)DSt->Message[3]).zpad(2) + ":" +
                                            CtiNumStr((int)DSt->Message[4]).zpad(2) + ":" +
                                            CtiNumStr((int)DSt->Message[5]).zpad(2) + "\n";
                            resultString += "Demand Reset Count: " + CtiNumStr((int)DSt->Message[6]) + "\n";
                            resultString += "Current TOU Rate: " + RWCString((char)('A' + ((DSt->Message[7] & 0x0C) >> 2)));

                            break;
                        }

                        case CtiTableDeviceMCTIEDPort::LandisGyrS4:
                        {
                            for( int i = 0; i < 7; i++ )  //  excluding byte 7 - it's a bitfield, not BCD
                            {
                                //  Convert the bytes from BCD to normal byte values
                                DSt->Message[i] = (((DSt->Message[i] & 0xf0) / 16) * 10) + (DSt->Message[i] & 0x0f);
                            }

                            resultString += getName() + " / IED / current time: ";
                            if( DSt->Message[6] <= 5 )
                            {
                                resultString += "(autoread #" + CtiNumStr((int)DSt->Message[6] + 1) + ") ";
                            }

                            resultString += CtiNumStr((int)DSt->Message[5]).zpad(2) + "/" +
                                            CtiNumStr((int)DSt->Message[4]).zpad(2) + "/" +
                                            CtiNumStr((int)DSt->Message[3]).zpad(2) + " " +
                                            CtiNumStr((int)DSt->Message[2]).zpad(2) + ":" +
                                            CtiNumStr((int)DSt->Message[1]).zpad(2) + ":" +
                                            CtiNumStr((int)DSt->Message[0]).zpad(2) + "\n";

                            resultString += "Outage count: " + CtiNumStr((int)DSt->Message[7]) + "\n";
                            resultString += "Current TOU Rate: " + RWCString((char)('A' + (DSt->Message[8] & 0x07)));

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


                            resultString += CtiNumStr(hour) + ":" + CtiNumStr(minute).zpad(2) + ":" + CtiNumStr(second).zpad(2) + " ";
                            resultString += CtiNumStr(month) + "/" + CtiNumStr(day) + "/" + CtiNumStr(year + 2000) + "\n";

                            rate        =  (DSt->Message[5] & 0xe0) >> 5;

                            tou_rtp     =   DSt->Message[6] & 0x03;
                            meter_class =  (DSt->Message[6] & 0x74) >> 3;
                            frequency   =  (DSt->Message[6] & 0x80) >> 7;

                            resultString += "Current TOU Rate: " + RWCString((char)('A' + (DSt->Message[8] & 0x07))) + "\n";

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
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - unhandled IED type " << getIEDPort().getIEDType() << " for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }

                    ReturnMsg->setResultString( resultString );
                }

                break;
            }

            case Emetcon::GetConfig_IEDScan:
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

                        if( DSt->Message[5] == 0 )  resultString += "  Buffer Contains: CTI Billing Data Table #" + CtiNumStr((int)DSt->Message[4] + 1) + "\n";
                        else                        resultString += "  Buffer Contains: S4 Meter Read Cmd: "      + CtiNumStr((int)DSt->Message[4]) + "\n";

                        resultString += "  Scan Offset:     " + CtiNumStr(((int)DSt->Message[3] * 256) + (int)DSt->Message[4]).spad(3) + "\n";

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
                            resultString += "  Buffer contains table " + CtiNumStr(tableid) + "\n";
                        }

                        resultString += "  Scan Offset: " + CtiNumStr(offset) + "\n";

                        break;
                    }

                    default:
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - unhandled IED type " << getIEDPort().getIEDType() << " for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                resultString += "  Scan Rate: " + CtiNumStr(((int)DSt->Message[0] * 15) + 30).spad(4) + " seconds\n";
                resultString += "  Buffer refresh delay: " + CtiNumStr((int)DSt->Message[1] * 15).spad(4) + " seconds\n";

                if( DSt->Message[2] == 0 )
                    DSt->Message[2] = 128;

                resultString += "  Scan Length:     " + CtiNumStr((int)DSt->Message[2]).spad(3) + " bytes\n";

                ReturnMsg->setResultString( resultString );

                break;
            }

            default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - unhandled IM->Sequence " << InMessage->Sequence << " for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT31X::decodeGetValueIED(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;
    INT pid, rateOffset;
    RWCString pointDescriptor, resultString, name, ratename;

    ULONG lValue;

    INT ErrReturn = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    CtiCommandParser parse( InMessage->Return.CommandStr );

    DOUBLE demandValue, Value;
    RWTime timestamp;
    RWDate datestamp;

    CtiPointBase    *pPoint       = NULL;
    CtiPointBase    *pDemandPoint = NULL;
    CtiReturnMsg    *ReturnMsg    = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData        = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** IED GetValue Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

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

        for( int i = 0; i < 12; i++ )
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
            status = ALPHABUFFERERROR;
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

                setMCTScanPending(ScanRateIntegrity, false);

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
                        unsigned long tmp, exp;

                        tmp  = 0;
                        tmp |=  DSt->Message[0]         << 12;
                        tmp |=  DSt->Message[1]         <<  4;
                        tmp |= (DSt->Message[2] & 0xf0) >>  4;

                        exp  =  DSt->Message[2] & 0x0f;

                        Value = tmp * pow(2, exp);

                        break;
                    }
                    default:
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - unhandled IED type " << getIEDPort().getIEDType() << " for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                if( pPoint != NULL )
                {
                    Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                    pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                        CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                    pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor);

                    if(pData != NULL)
                    {
                        ReturnMsg->PointData().insert(pData);
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
                        unsigned long tmp, exp;

                        tmp  = 0;
                        tmp |=  DSt->Message[3]         << 12;
                        tmp |=  DSt->Message[4]         <<  4;
                        tmp |= (DSt->Message[5] & 0xf0) >>  4;
                        exp  =  DSt->Message[5] & 0x0f;

                        Value = tmp * pow(2, exp);

                        break;
                    }
                    default:
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - unhandled IED type " << getIEDPort().getIEDType() << " for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                if( pPoint != NULL )
                {
                    Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                    pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                        CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                    pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor);

                    if(pData != NULL)
                    {
                        ReturnMsg->PointData().insert(pData);
                        pData = NULL;  // We just put it on the list...
                    }
                }
                else
                {
                    pPoint = getDevicePointOffsetTypeEqual( pid + 20, AnalogPointType );

                    if( pPoint != NULL )
                    {
                        Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                        pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                            CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                        pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor);

                        if(pData != NULL)
                        {
                            ReturnMsg->PointData().insert(pData);
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

                        if( pPoint != NULL )
                        {
                            Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                            pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                                CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor);

                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().insert(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }
                        else
                        {
                            resultString += "Phase A Volts: " + CtiNumStr(Value) + "\n";
                        }

                        lValue = MAKEUSHORT(DSt->Message[6], DSt->Message[7]);
                        if( lValue == 0xffff )  lValue = 99999;
                        if( lValue == 0xfffe )  lValue = 88888;
                        if( lValue == 0xfffd )  lValue = 65534;
                        Value = lValue * 0.01;

                        pPoint = getDevicePointOffsetTypeEqual(MCT360_IED_VoltsPhaseB_PointOffset, AnalogPointType);

                        if( pPoint != NULL )
                        {
                            Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                            pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                                CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor);

                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().insert(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }
                        else
                        {
                            resultString += "Phase B Volts: " + CtiNumStr(Value) + "\n";
                        }

                        lValue = MAKEUSHORT(DSt->Message[8], DSt->Message[9]);
                        if( lValue == 0xffff )  lValue = 99999;
                        if( lValue == 0xfffe )  lValue = 88888;
                        if( lValue == 0xfffd )  lValue = 65534;
                        Value = lValue * 0.01;

                        pPoint = getDevicePointOffsetTypeEqual(MCT360_IED_VoltsPhaseC_PointOffset, AnalogPointType);

                        if( pPoint != NULL )
                        {
                            Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                            pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                            CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor);

                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().insert(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }
                        else
                        {
                            resultString += "Phase C Volts: " + CtiNumStr(Value) + "\n";
                        }

                        lValue = MAKEUSHORT(DSt->Message[10], DSt->Message[11]);
                        if( lValue == 0xffff )  lValue = 99999;
                        if( lValue == 0xfffe )  lValue = 88888;
                        if( lValue == 0xfffd )  lValue = 65534;
                        Value = lValue * 0.01;

                        pPoint = getDevicePointOffsetTypeEqual(MCT360_IED_VoltsNeutralCurrent_PointOffset, AnalogPointType);

                        if( pPoint != NULL )
                        {
                            Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                            pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                                CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor);

                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().insert(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }
                        else
                        {
                            resultString += "Neutral current: " + CtiNumStr(Value) + "\n";
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

                        if( pPoint != NULL )
                        {
                            Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                            pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                                CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor);

                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().insert(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }
                        else
                        {
                            resultString += "Phase A Volts: " + CtiNumStr(Value) + "\n";
                        }

                        Value = voltsB * kvVoltageMultiplier;
                        pPoint = getDevicePointOffsetTypeEqual(MCT360_IED_VoltsPhaseB_PointOffset, AnalogPointType);

                        if( pPoint != NULL )
                        {
                            Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                            pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                                CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor);

                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().insert(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }
                        else
                        {
                            resultString += "Phase B Volts: " + CtiNumStr(Value) + "\n";
                        }

                        Value = voltsC * kvVoltageMultiplier;
                        pPoint = getDevicePointOffsetTypeEqual(MCT360_IED_VoltsPhaseC_PointOffset, AnalogPointType);

                        if( pPoint != NULL )
                        {
                            Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                            pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                            CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor);

                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().insert(pData);
                                pData = NULL;  // We just put it on the list...
                            }
                        }
                        else
                        {
                            resultString += "Phase C Volts: " + CtiNumStr(Value) + "\n";
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
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - unhandled IED type " << getIEDPort().getIEDType() << " for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                        int tmp, i;

                        for( i = 0; i < 3; i++ )
                        {
                            tmp = DSt->Message[i];
                            DSt->Message[i] = DSt->Message[5-i];
                            DSt->Message[5-i] = tmp;
                        }
                        for( i = 0; i < 3; i++ )
                        {
                            tmp = DSt->Message[i+6];
                            DSt->Message[i+6] = DSt->Message[11-i];
                            DSt->Message[11-i] = tmp;
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
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - unhandled IED type " << getIEDPort().getIEDType() << " for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }


                if( pPoint != NULL )
                {
                    Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                    pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                        CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                    pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor);

                    if(pData != NULL)
                    {
                        ReturnMsg->PointData().insert(pData);
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
                        for( int i = 0; i < 3; i++ )
                        {
                            tmp = DSt->Message[i];
                            DSt->Message[i] = DSt->Message[5-i];
                            DSt->Message[5-i] = tmp;
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
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - unhandled IED type " << getIEDPort().getIEDType() << " for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        pPoint = NULL;  //  we can't do a point update - we don't know how to interpret the data
                        Value  = 0.0;

                        break;
                    }
                }

                if( pPoint != NULL )
                {
                    Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                    pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                        CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                    pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor);

                    if(pData != NULL)
                    {
                        ReturnMsg->PointData().insert(pData);
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

                        datestamp = RWDate((unsigned)DSt->Message[5],
                                           (unsigned)DSt->Message[4],
                                           (unsigned)DSt->Message[3] + 2000 );
                        timestamp = RWTime( datestamp,
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

                        datestamp = RWDate((unsigned)DSt->Message[11],
                                           (unsigned)DSt->Message[12],
                                           (unsigned)DSt->Message[10] + 2000);
                        timestamp = RWTime(datestamp,
                                           (unsigned)DSt->Message[9],
                                           (unsigned)DSt->Message[8]);

                        break;
                    }

                    case CtiTableDeviceMCTIEDPort::GeneralElectricKV:
                    {
                        unsigned long tmp, exp;
                        unsigned year, month, day, hour, minute, second;

                        tmp  = 0;
                        tmp |=  DSt->Message[6]         << 12;
                        tmp |=  DSt->Message[7]         <<  4;
                        tmp |= (DSt->Message[8] & 0xf0) >>  4;
                        exp  =  DSt->Message[8] & 0x0f;

                        Value = tmp * pow(2, exp);

                        year        =   DSt->Message[ 9]         >> 1;
                        month       = ((DSt->Message[ 9] & 0x01) << 3) |
                                      ((DSt->Message[10] & 0xe0) >> 5);
                        day         =  (DSt->Message[10] & 0x1f);

                        hour        =   DSt->Message[11];
                        minute      =   DSt->Message[12];

                        datestamp = RWDate(day, month, year + 2000 );
                        timestamp = RWTime(datestamp, hour, minute);

                        break;
                    }

                    default:
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - unhandled IED type " << getIEDPort().getIEDType() << " for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        pPoint = NULL;  //  we can't do a point update - we don't know how to interpret the data
                        Value  = 0.0;

                        break;
                    }
                }

                if( pPoint != NULL )
                {
                    Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                    pointDescriptor = getName() + " / " + pPoint->getName() + " = " +
                                        CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                    if( datestamp.isValid() )
                    {
                        pointDescriptor += " @ " + CtiNumStr(datestamp.month()).zpad(2)      + "/" +
                                                   CtiNumStr(datestamp.dayOfMonth()).zpad(2) + "/" +
                                                   CtiNumStr(datestamp.year()).zpad(2)       + " " +
                                                   CtiNumStr(timestamp.hour()).zpad(2)       + ":" +
                                                   CtiNumStr(timestamp.minute()).zpad(2);

                        pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, pointDescriptor);

                        if(pData != NULL)
                        {
                            pData->setTime( timestamp.seconds() );

                            ReturnMsg->PointData().insert(pData);
                            pData = NULL;  // We just put it on the list...
                        }
                    }
                    else
                    {
                        //  don't send a pointdata msg, it's uninitialized and doesn't matter
                        resultString += pointDescriptor + " @ 00/00/00 00:00 - data not sent, timestamp invalid\n";
                    }
                }
                else
                {
                    resultString += getName() + " / " + name + " " + ratename + ": " + CtiNumStr(Value);

                    if( datestamp.isValid() )
                    {
                        resultString += " @ " + CtiNumStr(datestamp.month()).zpad(2)      + "/" +
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

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT31X::decodeGetValueKWH(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;
    INT pid;
    RWCString resultString;

    ULONG accums[3];

    INT ErrReturn = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    DOUBLE Value;
    CtiPointBase    *pPoint    = NULL;
    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData     = NULL;

    //  ACH:  are these necessary?  /mskf
    resetScanFreezePending();
    resetScanFreezeFailed();

    setMCTScanPending(ScanRateAccum, false);  //resetScanPending();

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Accumulator Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

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

        decodeAccumulators(accums, 3, InMessage->Buffer.DSt.Message);

        RWTime pointTime;
        pointTime -= pointTime.seconds() % 300;

        for(pid = 1; pid <= 3; pid++)
        {
            // 24 bit pulse value
            Value = (double)accums[pid-1];

            //  look for each KYZ point as a PulseAccumulator
            pPoint = getDevicePointOffsetTypeEqual( pid, PulseAccumulatorPointType );

            //  if we can find the point, send a named/UOM'd resultString
            if( pPoint != NULL)
            {
                Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                         ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
                pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, PulseAccumulatorPointType, resultString);
                if(pData != NULL)
                {
                    pData->setTime(pointTime);
                    ReturnMsg->PointData().insert(pData);
                    pData = NULL;  // We just put it on the list...

                    //  clear out any KYZ 1 messages we might've appended before this point - we now have a real one
                    ReturnMsg->setResultString( RWCString() );
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

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT31X::decodeGetValueDemand(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;
    INT pnt_offset, byte_offset;
    ULONG i,x;
    INT pid;
    RWCString resultString;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    int demand_rate;
    unsigned long pulses;
    bool bad_data;
    PointQuality_t quality;

    DOUBLE Value;
    CtiPointNumeric      *pPoint = NULL;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    RWTime pointTime;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Demand Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    resetScanPending();  //  needed for dev_single...  sets this in initiateIntegrityScan
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

        demand_rate = getDemandInterval();

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        pointTime -= pointTime.seconds() % getDemandInterval();

        for(pnt_offset = 1; pnt_offset <= 3; pnt_offset++)
        {
            byte_offset = ((pnt_offset - 1) * 2) + 1;          // First byte is the status byte.

            pulses = MAKEUSHORT(DSt->Message[byte_offset + 1], DSt->Message[byte_offset]);

            //  look for the demand accumulator point for this offset
            pPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual( pnt_offset, DemandAccumulatorPointType );

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
                    Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM(Value);
                }
            }

            // handle demand data here
            if( pPoint != NULL)
            {
                resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                         ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                if(pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, DemandAccumulatorPointType, resultString))
                {
                    pData->setTime( pointTime );

                    ReturnMsg->PointData().insert(pData);
                    pData = NULL;  // We just put it on the list...

                    //  clear out any demand input 1 messages we may have appended - we have a real one
                    ReturnMsg->setResultString("");
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
                dout << RWTime() << " **** Checkpoint - No demand accumulators defined in DB for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            delete ReturnMsg;
        }
    }

    return status;
}


INT CtiDeviceMCT31X::decodeGetValuePeak(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int       status = NORMAL;
    double    Value;
    RWCString resultString;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiPointBase         *pPoint = NULL;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Min/Max On/Off-Peak Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    resetScanPending();

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        /* this means we are getting NON-demand accumulator points */
        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        for( int i = 0; i < 6; i++ )
        {
            //  ACH:  check error bits
            Value = MAKEUSHORT(DSt->Message[i*2+1], (DSt->Message[i*2+0] & 0x3f) );

            //  turn raw pulses into a demand reading
            Value *= DOUBLE(3600 / getDemandInterval());

            // look for the appropriate point
            pPoint = getDevicePointOffsetTypeEqual( 10 + i, DemandAccumulatorPointType );

            if( pPoint != NULL)
            {
                RWTime pointTime;

                Value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(Value);

                resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                         ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                if( InMessage->Sequence == Emetcon::GetValue_FrozenPeakDemand )
                {
                    pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, DemandAccumulatorPointType, resultString);
                    if(pData != NULL)
                    {
                        pointTime -= pointTime.seconds() % getDemandInterval();
                        pData->setTime( pointTime );
                        ReturnMsg->PointData().insert(pData);
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
    }

    retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


INT CtiDeviceMCT31X::decodeScanLoadProfile(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
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

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
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

            point = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( retrieved_channel + MCT_PointOffset_LoadProfileOffset, DemandAccumulatorPointType );

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
                    if( getMCTDebugLevel(MCTDebug_LoadProfile) )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - attempt to decode current load profile block for \"" << getName() << "\" - aborting decode **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << InMessage->Return.CommandStr << endl;
                    }

                    result_string = "Attempt to decode current load profile block for \"" + getName() + "\" - aborting decode ";
                }
                else if( retrieved_block_start < _lastLPTime[retrieved_channel - 1] )
                {
                    if( getMCTDebugLevel(MCTDebug_LoadProfile) )
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


void CtiDeviceMCT31X::DecodeDatabaseReader(RWDBReader &rdr)
{
    INT iTemp;
    RWDBNullIndicator isNull;

    Inherited::DecodeDatabaseReader(rdr);

    if( getType() == TYPEMCT360 ||
        getType() == TYPEMCT370 )
    {
        _iedPort.DecodeDatabaseReader( rdr );

        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - in CtiDeviceMCT31X::DecodeDatabaseReader for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Default data class: " << _iedPort.getDefaultDataClass() << endl;
            dout << "Default data offset: " << _iedPort.getDefaultDataOffset() << endl;
            dout << "Device ID: " << _iedPort.getDeviceID() << endl;
            dout << "IED Scan Rate: " << _iedPort.getIEDScanRate() << endl;
            dout << "IED Type: " << _iedPort.getIEDType() << endl;
            dout << "Password: " << _iedPort.getPassword() << endl;
            dout << "Real Time Scan Flag: " << _iedPort.getRealTimeScanFlag() << endl;
        }
    }
}

