/*-----------------------------------------------------------------------------*
*
* File:   dev_mct310
*
* Date:   4/24/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_mct310.cpp-arc  $
* REVISION     :  $Revision: 1.35 $
* DATE         :  $Date: 2005/02/10 23:24:00 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include <windows.h>
#include "device.h"
#include "devicetypes.h"
#include "dev_mct310.h"
#include "logger.h"
#include "mgr_point.h"
#include "pt_status.h"
#include "numstr.h"
#include "porter.h"
#include "utility.h"
#include "dllyukon.h"

set< CtiDLCCommandStore > CtiDeviceMCT310::_commandStore;

CtiDeviceMCT310::CtiDeviceMCT310( )
{
}

CtiDeviceMCT310::CtiDeviceMCT310( const CtiDeviceMCT310 &aRef )
{
    *this = aRef;
}

CtiDeviceMCT310::~CtiDeviceMCT310( )
{
}

CtiDeviceMCT310& CtiDeviceMCT310::operator=( const CtiDeviceMCT310 &aRef )
{
    if( this != &aRef )
    {
        Inherited::operator=( aRef );
    }

    return *this;
}


bool CtiDeviceMCT310::initCommandStore( )
{
    bool failed = false;

    CtiDLCCommandStore cs;

    cs._cmd     = CtiProtocolEmetcon::PutConfig_Raw;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair( 0, 0 );  //  this will be filled in by executePutConfig
    _commandStore.insert( cs );

    //  300 series common commands
    cs._cmd     = CtiProtocolEmetcon::GetConfig_Time;
    cs._io      = IO_READ;
    cs._funcLen = make_pair((int)MCT3XX_TimePos,
                            (int)MCT3XX_TimeLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetConfig_DemandInterval;
    cs._io      = IO_READ;
    cs._funcLen = make_pair((int)MCT3XX_DemandIntervalPos,
                            (int)MCT3XX_DemandIntervalLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetConfig_LoadProfileInterval;
    cs._io      = IO_READ;
    cs._funcLen = make_pair((int)MCT3XX_LPIntervalPos,
                            (int)MCT3XX_LPIntervalLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetConfig_Multiplier;
    cs._io      = IO_READ;
    cs._funcLen = make_pair((int)MCT3XX_Mult1Pos,
                            (int)MCT3XX_MultLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetConfig_Options;
    cs._io      = IO_READ;
    cs._funcLen = make_pair((int)MCT3XX_OptionPos,
                            (int)MCT3XX_OptionLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetConfig_GroupAddress;
    cs._io      = IO_READ;
    cs._funcLen = make_pair((int)MCT3XX_GroupAddrPos,
                            (int)MCT3XX_GroupAddrLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_UniqueAddr;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT3XX_UniqAddrPos,
                            (int)MCT3XX_UniqAddrLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_GroupAddr_GoldSilver;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT3XX_GroupAddrGoldSilverPos,
                            (int)MCT3XX_GroupAddrGoldSilverLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_GroupAddr_Bronze;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT3XX_GroupAddrBronzePos,
                            (int)MCT3XX_GroupAddrBronzeLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_GroupAddr_Lead;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT3XX_GroupAddrLeadPos,
                            (int)MCT3XX_GroupAddrLeadLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_DemandInterval;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT3XX_DemandIntervalPos,
                            (int)MCT3XX_DemandIntervalLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_LoadProfileInterval;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT_Function_LPInt, 0);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_Multiplier;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT3XX_Mult1Pos,
                            (int)MCT3XX_MultLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_TSync;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT_TSyncPos,
                            (int)MCT_TSyncLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_OnOffPeak;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT3XX_MinMaxPeakConfigPos, 1);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_MinMax;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT3XX_MinMaxPeakConfigPos, 1);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::Scan_Accum;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair((int)MCT3XX_FuncReadMReadPos,
                            (int)MCT3XX_FuncReadMReadLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetValue_Default;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair((int)MCT3XX_FuncReadMReadPos,
                            (int)MCT3XX_FuncReadMReadLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetValue_Frozen;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair((int)MCT3XX_FuncReadFrozenPos,
                            (int)MCT3XX_FuncReadFrozenLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetStatus_Internal;
    cs._io      = IO_READ;
    cs._funcLen = make_pair((int)MCT3XX_GenStatPos,
                            (int)MCT3XX_GenStatLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutStatus_Reset;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT3XX_ResetPos,
                            (int)MCT3XX_ResetLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetValue_PFCount;
    cs._io      = IO_READ;
    cs._funcLen = make_pair((int)MCT3XX_PFCountPos,
                            (int)MCT3XX_PFCountLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutValue_ResetPFCount;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT3XX_PFCountPos,
                            (int)MCT3XX_PFCountLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutStatus_PeakOn;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT3XX_FunctionPeakOn, 0);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutStatus_PeakOff;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT3XX_FunctionPeakOff, 0);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutStatus_FreezeOne;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT3XX_FreezeOne, 0);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutStatus_FreezeTwo;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT3XX_FreezeTwo, 0);
    _commandStore.insert( cs );

    //  only valid for sspec 1007 (and above?)
    cs._cmd      = CtiProtocolEmetcon::Scan_Integrity;
    cs._io       = IO_READ;
    cs._funcLen  = make_pair((int)MCT310_DemandPos,
                             (int)MCT310_DemandLen);
    _commandStore.insert( cs );

    //  310 specific commands
    //  310 cannot do a FR0x92 (MCT31X_FuncReadDemand) and can only collect 1 demand reading!
    cs._cmd      = CtiProtocolEmetcon::Scan_Integrity;
    cs._io       = IO_READ;
    cs._funcLen  = make_pair((int)MCT310_DemandPos,
                             (int)MCT310_DemandLen);
    _commandStore.insert( cs );

    cs._cmd      = CtiProtocolEmetcon::GetValue_Demand;
    cs._io       = IO_READ;
    cs._funcLen  = make_pair((int)MCT310_DemandPos,
                             (int)MCT310_DemandLen);
    _commandStore.insert( cs );

    cs._cmd      = CtiProtocolEmetcon::PutValue_KYZ;
    cs._io       = IO_WRITE;
    cs._funcLen  = make_pair((int)MCT3XX_PutMRead1Pos,
                             (int)MCT3XX_PutMReadLen);
    _commandStore.insert( cs );

    cs._cmd      = CtiProtocolEmetcon::GetValue_PeakDemand;
    cs._io       = IO_FCT_READ;
    cs._funcLen  = make_pair((int)MCT3XX_FuncReadMinMaxDemandPos, 4);
    _commandStore.insert( cs );

    cs._cmd      = CtiProtocolEmetcon::GetValue_FrozenPeakDemand;
    cs._io       = IO_FCT_READ;
    cs._funcLen  = make_pair((int)MCT3XX_FuncReadFrozenDemandPos, 4);
    _commandStore.insert( cs );

    //  only valid for 310IL, this case handled in getOperation
    cs._cmd      = CtiProtocolEmetcon::Scan_LoadProfile;
    cs._io       = IO_FCT_READ;
    cs._funcLen  = make_pair(0, 0);
    _commandStore.insert( cs );

    cs._cmd      = CtiProtocolEmetcon::GetStatus_Disconnect;
    cs._io       = IO_READ;
    cs._funcLen  = make_pair((int)MCT310_StatusPos,
                             (int)MCT310_StatusLen);
    _commandStore.insert( cs );

    cs._cmd      = CtiProtocolEmetcon::GetStatus_LoadProfile;
    cs._io       = IO_READ;
    cs._funcLen  = make_pair((int)MCT3XX_LPStatusPos,
                             (int)MCT3XX_LPStatusLen);
    _commandStore.insert( cs );

    return failed;
}


bool CtiDeviceMCT310::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    if( _commandStore.empty( ) )  // Must initialize!
    {
        CtiDeviceMCT310::initCommandStore( );
    }

    DLCCommandSet::iterator itr = _commandStore.find( CtiDLCCommandStore( cmd ) );

    //  the 310IL/IDL is the only 310 that supports load profile, and i didn't want to add a seperate class for the one action
    if( getType( ) != TYPEMCT310IL  &&
        getType( ) != TYPEMCT310IDL &&
        cmd == CtiProtocolEmetcon::Scan_LoadProfile )
    {
        //  for emphasis...
        found = false;
    }
    else if( itr != _commandStore.end( ) )
    {
        CtiDLCCommandStore &cs = *itr;
        function = cs._funcLen.first;   //  Copy the relevant bits from the commandStore
        length   = cs._funcLen.second;  //
        io       = cs._io;              //

        found = true;
    }
    else                                //  Look in the parent if not found in the child!
    {
        found = Inherited::getOperation( cmd, function, length, io );
    }

    return found;
}


ULONG CtiDeviceMCT310::calcNextLPScanTime( void )
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


INT CtiDeviceMCT310::calcAndInsertLPRequests(OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList)
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


bool CtiDeviceMCT310::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
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

        lpBlockAddress += 0x50;

        OutMessage->Buffer.BSt.Function = lpBlockAddress;
        OutMessage->Buffer.BSt.Length   = 13;  //  2 bytes per interval, and a status byte to boot
        OutMessage->Buffer.BSt.IO       = IO_FCT_READ;

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
INT CtiDeviceMCT310::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    switch(InMessage->Sequence)
    {
        case (CtiProtocolEmetcon::Scan_Accum):
        case (CtiProtocolEmetcon::GetValue_Default):
        {
            status = decodeGetValueKWH(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

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

        case (CtiProtocolEmetcon::GetValue_PeakDemand):
        case (CtiProtocolEmetcon::GetValue_FrozenPeakDemand):
        {
            status = decodeGetValuePeak(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (CtiProtocolEmetcon::PutConfig_OnOffPeak):
        case (CtiProtocolEmetcon::PutConfig_MinMax):
        {
            status = decodePutConfigPeakMode(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (CtiProtocolEmetcon::PutStatus_FreezeOne):
        case (CtiProtocolEmetcon::PutStatus_FreezeTwo):
        {
            status = decodePutStatus(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (CtiProtocolEmetcon::GetStatus_Disconnect):
        {
            status = decodeGetStatusDisconnect(InMessage, TimeNow, vgList, retList, outList);
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

        case (CtiProtocolEmetcon::GetConfig_Options):
        {
            status = decodeGetConfigOptions(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (CtiProtocolEmetcon::Control_Conn):
        case (CtiProtocolEmetcon::Control_Disc):
        {
            CtiRequestMsg newReq(getID(),
                                 "getstatus disconnect noqueue",
                                 InMessage->Return.UserID,
                                 InMessage->Return.TrxID,
                                 InMessage->Return.RouteID,
                                 InMessage->Return.MacroOffset,
                                 InMessage->Return.Attempt);

            newReq.setConnectionHandle((void *)InMessage->Return.Connection);

            CtiDeviceBase::ExecuteRequest(&newReq, CtiCommandParser(newReq.CommandString()), vgList, retList, outList);

            break;
        }
        case (CtiProtocolEmetcon::GetValue_Frozen):
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


INT CtiDeviceMCT310::decodePutConfigPeakMode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;
    ULONG i,x;
    RWCString resultString;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi


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

        if( InMessage->Sequence == CtiProtocolEmetcon::PutConfig_MinMax )
        {
            resultString = getName() + " / peak mode set to \"minmax\"";
        }
        else
        {
            resultString = getName() + " / peak mode set to \"on/off peak\"";
        }

        resultString += " - also, wire config and cold load options reset to default;  use DB MCT Config and \"putconfig emetcon install\" instead to avoid this";

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT310::decodeGetValueKWH(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;
    ULONG i,x;
    INT pid;
    RWCString resultString;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    DOUBLE Value;
    CtiPointBase         *pPoint = NULL;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Accumulator Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    ULONG RecentValue = 0;
    USHORT TempDevType;

    //  ACH:  are these necessary?  /mskf
    resetScanFreezePending();
    resetScanFreezeFailed();

    setMCTScanPending(ScanRateAccum, false);

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

        pPoint = getDevicePointOffsetTypeEqual( 1, PulseAccumulatorPointType );

        RecentValue = MAKEULONG(MAKEUSHORT (DSt->Message[2], DSt->Message[1]), (USHORT)(DSt->Message[0]));

        // handle accumulator data here
        if( pPoint != NULL)
        {
            // 24 bit pulse value
            Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( RecentValue );
            RWTime pointTime;

            if( getType() == TYPEMCT310ID ||
                getType() == TYPEMCT310IDL ||
                getType() == TYPEMCT310IL )
            {
                while( Value > MCT_Rollover )
                {
                    Value -= MCT_Rollover;
                }
            }

            resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                     ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, PulseAccumulatorPointType, resultString);

            if(pData != NULL)
            {
                pointTime -= pointTime.seconds() % 300;
                pData->setTime( pointTime );
                ReturnMsg->PointData().insert(pData);
                pData = NULL;  // We just put it on the list...
            }
        }
        else
        {
            if( getType() == TYPEMCT310ID ||
                getType() == TYPEMCT310IDL ||
                getType() == TYPEMCT310IL )
            {
                while( RecentValue > MCT_Rollover )
                {
                    RecentValue -= MCT_Rollover;
                }
            }

            resultString = getName() + " / KYZ 1 = " + CtiNumStr(RecentValue) + "  --  POINT UNDEFINED IN DB";
            ReturnMsg->setResultString(resultString);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT310::decodeGetValueDemand(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int       status = NORMAL, demand_interval;
    double    Value;
    RWCString resultString;
    unsigned long pulses;
    PointQuality_t quality;
    bool bad_data;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiPointBase         *pPoint = NULL;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Demand Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    setMCTScanPending(ScanRateIntegrity, false);
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

        // 2 byte demand value.  Upper 2 bits are error indicators.
        pulses = MAKEUSHORT(DSt->Message[1], (DSt->Message[0] & 0x3f) );

        demand_interval = getDemandInterval();

        // look for first defined DEMAND accumulator
        pPoint = getDevicePointOffsetTypeEqual( 1, DemandAccumulatorPointType );

        if( checkDemandQuality( pulses, quality, bad_data ) )
        {
            Value = 0.0;
        }
        else
        {
            //  if no fatal problems with the quality,
            //    adjust for the demand interval
            Value = pulses * (3600 / demand_interval);

            if( pPoint )
            {
                //    and the UOM
                Value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(Value);
            }
        }

        if( pPoint != NULL)
        {
            RWTime pointTime;

            resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                     ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

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
            resultString = getName() + " / Demand = " + CtiNumStr(Value) + "  --  POINT UNDEFINED IN DB";
            ReturnMsg->setResultString(resultString);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT310::decodeGetValuePeak(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int       status = NORMAL;
    double    Value;
    RWCString resultString;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiPointBase         *pPoint = NULL;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

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

        for( int i = 0; i < 2; i++ )
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

                if( InMessage->Sequence == CtiProtocolEmetcon::GetValue_FrozenPeakDemand )
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


INT CtiDeviceMCT310::decodeScanLoadProfile(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
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


void CtiDeviceMCT310::decodeAccumulators(ULONG result[], INT accum_cnt, BYTE *Data)
{
    int i, j, maxj;

    for(i = 0; i < accum_cnt && i < 3; i++)
    {
        result[i] = 0;
        maxj = (i * 3) + 3;

        for(j = i * 3; j < maxj; j++)
        {
            result[i] = (result[i] << 8) + Data[j];
        }

        if(result[i] > MCT_MaxPulseCount)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
}


INT CtiDeviceMCT310::decodeGetStatusInternal( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    unsigned char *geneBuf = InMessage->Buffer.DSt.Message;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        RWCString resultString;

        CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg      *pData = NULL;
        CtiPointStatus       *point = NULL;

        int powerfailStatus;

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        resultString  = getName() + " / Internal Status:\n";

        if( geneBuf[5] )        resultString += "  Active\n";
        else                    resultString += "  Halted\n";

        if( geneBuf[0] & 0x04 ) resultString += "  Metering Overflow\n";
        if( geneBuf[0] & 0x08 ) resultString += "  Powerfail flag set\n";
        if( geneBuf[0] & 0x10 ) resultString += "  Reset Flag set\n";
        if( geneBuf[0] & 0x20 ) resultString += "  Self Test Failed\n";

        if( geneBuf[1] & 0x80 ) resultString += "  UADD fail - NovRam\n";
        if( geneBuf[1] & 0x40 ) resultString += "  UADD fail - Ram\n";
        if( geneBuf[1] & 0x20 ) resultString += "  SPI interrupt flag\n";
        if( geneBuf[1] & 0x10 ) resultString += "  SCI interrupt flag\n";
        if( geneBuf[1] & 0x08 ) resultString += "  SWI interrupt flag\n";
        if( geneBuf[1] & 0x02 ) resultString += "  Watchdog Reset flag\n";

        if( !(geneBuf[0] | geneBuf[1] | geneBuf[5]) )
            resultString += "  Normal Operating Mode\n";

        if( geneBuf[0] & 0x08 )
        {
            powerfailStatus = 1;
        }
        else
        {
            powerfailStatus = 0;
        }

        if( point = (CtiPointStatus *)getDevicePointOffsetTypeEqual( MCT_PointOffset_Status_Powerfail, StatusPointType ) )
        {
            RWCString pointResult;

            pointResult = getName() + " / " + point->getName() + ": " + ResolveStateName(((CtiPointStatus *)point)->getStateGroupID(), powerfailStatus);

            pData = CTIDBG_new CtiPointDataMsg(point->getPointID(), powerfailStatus, NormalQuality, DemandAccumulatorPointType, resultString);

            ReturnMsg->PointData().insert(pData);
        }


        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT310::decodeGetStatusLoadProfile( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
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

        INT   j;
        ULONG mread = 0;

        CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg      *pData = NULL;

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        RWCString resultString;
        double Value;

        resultString  = getName() + " / Load Survey Control Parameters:\n";
        resultString += "Demand Interval   : " + CtiNumStr((int)(DSt->Message[6] * 5)) + "\n";
        resultString += "Current Interval  : " + CtiNumStr((int)((DSt->Message[8] / 2) + 1)) + "\n";
        resultString += "PI 1 Current Value: " + CtiNumStr((int)((DSt->Message[0] << 8) + DSt->Message[1])) + "\n";
        resultString += "PI 2 Current Value: " + CtiNumStr((int)((DSt->Message[2] << 8) + DSt->Message[3])) + "\n";
        resultString += "PI 3 Current Value: " + CtiNumStr((int)((DSt->Message[4] << 8) + DSt->Message[5])) + "\n";

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT310::decodeGetConfigModel(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        INT ssp;
        char rev;
        char temp[80];

        RWCString sspec;
        RWCString options("Options:\n");

        CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi


        ssp = InMessage->Buffer.DSt.Message[4] * 256 + InMessage->Buffer.DSt.Message[0];
        rev = 64 + InMessage->Buffer.DSt.Message[1];

        sspec = "\nSoftware Specification " + CtiNumStr(ssp) + "  Rom Revision " + RWCString(rev) + "\n";

        if(InMessage->Buffer.DSt.Message[2] & 0x01)
        {
            options += "  Metering channel #1 available\n";
        }
        if(InMessage->Buffer.DSt.Message[2] & 0x02)
        {
            options += "  Metering channel #2 available\n";
        }
        if(InMessage->Buffer.DSt.Message[2] & 0x04)
        {
            options += "  Metering channel #3 available\n";
        }
        if(InMessage->Buffer.DSt.Message[2] & 0x08)
        {
            options += "  Feedback load control available\n";
        }
        if(InMessage->Buffer.DSt.Message[2] & 0x10)
        {
            options += "  4-state latch relays available\n";
        }
        if(InMessage->Buffer.DSt.Message[2] & 0x20)
        {
            options += "  Capacitor control available\n";
        }
        if(InMessage->Buffer.DSt.Message[2] & 0x40)
        {
            options += "  Service disconnect available\n";
        }
        if(InMessage->Buffer.DSt.Message[2] & 0x80)
        {
            options += "  Timed relays available\n";
        }

        if( InMessage->Buffer.DSt.Message[3] & 0x08 )
        {
            options += "  Pulse input 1: 3-wire\n";
        }
        else
        {
            options += "  Pulse input 1: 2-wire\n";
        }

        if( getType() == TYPEMCT318 || getType() == TYPEMCT318L || getType() == TYPEMCT360 || getType() == TYPEMCT370 )
        {
            if( InMessage->Buffer.DSt.Message[3] & 0x10 )
            {
                options += "  Pulse input 2: 3-wire\n";
            }
            else
            {
                options += "  Pulse input 2: 2-wire\n";
            }
            if( InMessage->Buffer.DSt.Message[3] & 0x20 )
            {
                options += "  Pulse input 3: 3-wire\n";
            }
            else
            {
                options += "  Pulse input 3: 2-wire\n";
            }

            if( InMessage->Buffer.DSt.Message[3] & 0x04 )
            {
                options += "  Peak mode:  Min/Max\n";
            }
            else
            {
                options += "  Peak mode:  On/Off Peak\n";
            }
        }

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString( sspec + options );

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT310::decodeGetConfigOptions(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn = InMessage->EventCode & 0x3fff;
    unsigned char *optBuf  = InMessage->Buffer.DSt.Message;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        RWCString options;

        options  = "Device: \"" + getName() + "\" Configuration:\n";

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        //  310s don't use this bit
        if( getType() != TYPEMCT310 && getType() != TYPEMCT310ID && getType() != TYPEMCT310IL && getType() != TYPEMCT310IDL )
        {
            if( optBuf[1] & 0x04 )
                options += "  In on/off peak mode\n";
            else
                options += "  In min/max mode\n";
        }

        if( optBuf[3] & 0x01 && optBuf[3] & 0x02 )  //  00
            options += "  Address restrict flag invalid\n";
        else if( optBuf[3] & 0x01 )                 //  01
            options += "  No Group Addressing\n";
        else if( optBuf[3] & 0x02 )                 //  10
            options += "  FCT Addressing Only\n";
        else                                        //  11
            options += "  Normal Addressing\n";

        if( optBuf[1] & 0x40 )
            options += "  Latch relay requires ARM\n";

        if( optBuf[1] & 0x80 )
            options += "  Latch relay by Unique Only\n";

        if( optBuf[0] & 0x80 )
        {
            if( !(optBuf[1] & 0x01) )
                options += "  No\n";
            options += "  Cold Load Pick-up Load A\n";

            if( !(optBuf[1] & 0x02) )
                options += "  No\n";
            options += "  Cold Load Pick-up Load B\n";
        }

        if( optBuf[0] & 0x01 )
        {
            options += "  Metering input #1:";
            if( optBuf[1] & 0x08 )
                options += " 3-wire\n";
            else
                options += " 2-wire\n";
        }

        if( optBuf[0] & 0x02 )
        {
            options += "  Metering input #2:";
            if( optBuf[1] & 0x10 )
                options += " 3-wire\n";
            else
                options += " 2-wire\n";
        }

        if( optBuf[0] & 0x04 )
        {
            options += "  Metering input #3:";
            if( optBuf[1] & 0x20 )
                options += " 3-wire\n";
            else
                options += " 2-wire\n";
        }

        ReturnMsg->setUserMessageId( InMessage->Return.UserID );
        ReturnMsg->setResultString( options );

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }


    return status;
}

