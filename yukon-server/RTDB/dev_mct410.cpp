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
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2004/07/12 19:30:37 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)


#include <windows.h>
#include "device.h"
#include "devicetypes.h"
#include "dev_mct410.h"
#include "logger.h"
#include "mgr_point.h"
#include "numstr.h"
#include "porter.h"
#include "utility.h"

set< CtiDLCCommandStore > CtiDeviceMCT410::_commandStore;

CtiDeviceMCT410::CtiDeviceMCT410( ) :
    _llpInterestTime(0),
    _llpInterestOffset(0),
    _llpInterestChannel(0)
{
}

CtiDeviceMCT410::CtiDeviceMCT410( const CtiDeviceMCT410 &aRef )
{
   *this = aRef;
}

CtiDeviceMCT410::~CtiDeviceMCT410( ) { }

CtiDeviceMCT410 &CtiDeviceMCT410::operator=( const CtiDeviceMCT410 &aRef )
{
   if( this != &aRef )
   {
      Inherited::operator=( aRef );
   }

   return *this;
}


bool CtiDeviceMCT410::initCommandStore( )
{
    bool failed = false;

    CtiDLCCommandStore cs;

    cs._cmd     = CtiProtocolEmetcon::Scan_Accum;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair( (int)MCT410_FuncReadMReadPos,
                             (int)MCT410_FuncReadMReadLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetValue_Default;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair( (int)MCT410_FuncReadMReadPos,
                             (int)MCT410_FuncReadMReadLen );
    _commandStore.insert( cs );

    cs._cmd      = CtiProtocolEmetcon::Scan_Integrity;
    cs._io       = IO_FCT_READ;
    cs._funcLen  = make_pair( (int)MCT410_FuncReadDemandPos,
                              (int)MCT410_FuncReadDemandLen );
    _commandStore.insert( cs );

    cs._cmd      = CtiProtocolEmetcon::GetValue_Demand;
    cs._io       = IO_FCT_READ;
    cs._funcLen  = make_pair( (int)MCT410_FuncReadDemandPos,
                              (int)MCT410_FuncReadDemandLen );
    _commandStore.insert( cs );

    cs._cmd      = CtiProtocolEmetcon::GetValue_PeakDemand;
    cs._io       = IO_FCT_READ;
    cs._funcLen  = make_pair( (int)MCT410_FuncReadPeakDemandPos,
                              (int)MCT410_FuncReadPeakDemandLen );
    _commandStore.insert( cs );

    cs._cmd      = CtiProtocolEmetcon::GetValue_Voltage;
    cs._io       = IO_FCT_READ;
    cs._funcLen  = make_pair( (int)MCT410_FuncReadVoltagePos,
                              (int)MCT410_FuncReadVoltageLen );
    _commandStore.insert( cs );

    cs._cmd      = CtiProtocolEmetcon::GetValue_Outage;
    cs._io       = IO_FCT_READ;
    cs._funcLen  = make_pair( (int)MCT410_FuncReadOutagePos,
                              (int)MCT410_FuncReadOutageLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetStatus_Internal;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT410_StatusLen,
                             (int)MCT410_StatusPos );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetStatus_LoadProfile;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair( (int)MCT410_FuncReadLPStatusPos,
                             (int)MCT410_FuncReadLPStatusLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_Raw;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair( 0, 0 );  //  filled in later
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_TSync;
    cs._io      = IO_FCT_WRITE;
    cs._funcLen = make_pair( (int)MCT410_FuncWriteTSyncPos,
                             (int)MCT410_FuncWriteTSyncLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetConfig_TSync;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT410_LastTSyncPos,
                             (int)MCT410_LastTSyncLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetConfig_Time;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT410_RTCPos,
                             (int)MCT410_RTCLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_DemandInterval;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair( (int)MCT410_DemandIntervalPos,
                             (int)MCT410_DemandIntervalLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetConfig_DemandInterval;
    cs._io      = IO_READ;
    cs._funcLen = make_pair((int)MCT410_DemandIntervalPos,
                            (int)MCT410_DemandIntervalLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_LoadProfileInterval;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT_Function_LPInt, 0);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetConfig_LoadProfileInterval;
    cs._io      = IO_READ;
    cs._funcLen = make_pair((int)MCT410_LPIntervalPos,
                            (int)MCT410_LPIntervalLen);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetValue_PFCount;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT410_PowerfailCountPos,
                             (int)MCT410_PowerfailCountLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutValue_ResetPFCount;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT4XX_CommandPowerfailReset, 0);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutStatus_Reset;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT4XX_CommandReset, 0);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutStatus_FreezeOne;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT4XX_FreezeOne, 0);
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutStatus_FreezeTwo;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT4XX_FreezeTwo, 0);
    _commandStore.insert( cs );

    return failed;
}


bool CtiDeviceMCT410::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    if( _commandStore.empty( ) )  // Must initialize!
    {
        CtiDeviceMCT410::initCommandStore( );
    }

    DLCCommandSet::iterator itr = _commandStore.find( CtiDLCCommandStore( cmd ) );

    if( itr != _commandStore.end( ) )
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


//  eventually make this like the 318 - we need to add voltage LP as well
ULONG CtiDeviceMCT410::calcNextLPScanTime( void )
{
    RWTime Now, blockStart, plannedLPTime, panicLPTime;
    unsigned long nextTime, midnightOffset;
    int lpBlockSize, lpDemandRate, lpMaxBlocks;

    //  if we're not collecting load profile, don't scan
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
        //    both were set to Now() at the constructor
        if( _lastLPRequestAttempt == _lastLPRequestBlockStart )
        {
            //  so we haven't talked to it yet
            _lastLPRequestAttempt = Now;
            _lastLPRequestBlockStart = (unsigned long)86400;
        }

        //  we read 6 intervals at a time
        lpDemandRate = getLoadProfile().getLoadProfileDemandRate();
        lpBlockSize  = lpDemandRate * 6;

        //  make sure we only request one day's data
        if( lpDemandRate > 900 )
            lpMaxBlocks = (86400 / lpDemandRate) / 6;
        else
            lpMaxBlocks = 16;


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


INT CtiDeviceMCT410::calcAndInsertLPRequests(OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList)
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

    lpBlockStartTime = getLastLPTime();
    lpDemandRate     = getLoadProfile().getLoadProfileDemandRate();

    //  we can read 6 intervals at a time
    lpBlockSize = lpDemandRate * 6;

    if( !_lpIntervalSent )
    {
        sendLPInterval( OutMessage, outList );
    }
    else
    {
        if( useScanFlags() )
        {
            //  make sure we only request one day's data
            if( lpDemandRate > 900 )
                lpMaxBlocks = (86400 / lpDemandRate) / 6;
            else
                lpMaxBlocks = 16;

            lpBlocksToCollect = (Now.seconds() - lpBlockStartTime.seconds()) / lpBlockSize;

            //  make sure we only ask for what remains in memory
            if( lpBlocksToCollect > (lpMaxBlocks - 1) )
            {
                //  start with everything but the current block
                lpBlockStartTime = Now.seconds() - (lpBlockSize * (lpMaxBlocks - 1));
            }

            //  figure out seconds from midnight UTC
            lpMidnightOffset  = lpBlockStartTime.seconds() % 86400;

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


bool CtiDeviceMCT410::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
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
INT CtiDeviceMCT410::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    switch(InMessage->Sequence)
    {
        case CtiProtocolEmetcon::Scan_Accum:
        case CtiProtocolEmetcon::GetValue_Default:
        {
            status = decodeGetValueKWH(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case CtiProtocolEmetcon::Scan_Integrity:
        case CtiProtocolEmetcon::GetValue_Demand:
        {
            status = decodeGetValueDemand(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case CtiProtocolEmetcon::GetValue_PeakDemand:
        {
            status = decodeGetValuePeakDemand(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case CtiProtocolEmetcon::GetValue_Voltage:
        {
            status = decodeGetValueVoltage(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case CtiProtocolEmetcon::GetValue_Outage:
        {
            status = decodeGetValueOutage(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case CtiProtocolEmetcon::GetValue_LoadProfile:
        {
            status = decodeGetValueLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case CtiProtocolEmetcon::Scan_LoadProfile:
        {
            status = decodeScanLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case CtiProtocolEmetcon::GetStatus_Internal:
        {
            status = decodeGetStatusInternal(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case CtiProtocolEmetcon::GetStatus_LoadProfile:
        {
            status = decodeGetStatusLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case CtiProtocolEmetcon::GetConfig_DemandInterval:
        case CtiProtocolEmetcon::GetConfig_LoadProfileInterval:
        {
            status = decodeGetConfigInterval(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (CtiProtocolEmetcon::GetConfig_Model):
        {
            status = decodeGetConfigModel(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (CtiProtocolEmetcon::GetConfig_Time):
        case (CtiProtocolEmetcon::GetConfig_TSync):
        {
            status = decodeGetConfigTime(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (CtiProtocolEmetcon::GetValue_PFCount):
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


PointQuality_t CtiDeviceMCT410::getDataQuality( unsigned char *buf, int len )
{
    PointQuality_t retval;

    unsigned long errorcode = 0xffffffff;

    for( int i = 0; i < len; i++ )
    {
        errorcode <<= 8;
        errorcode  |= buf[i];
    }

    //  i s'pose this could be a set sometime, eh?
    switch( errorcode )
    {
        case 0xFFFFFFFF:  //  Meter Communications Problem
        case 0xFFFFFFFE:  //  --
        case 0xFFFFFFFD:  //  No Data Yet Available
        case 0xFFFFFFFC:  //  --
        case 0xFFFFFFFB:  //  Data not available for interval
        case 0xFFFFFFFA:  //  --
        {
            retval = InvalidQuality;
            break;
        }
        case 0xFFFFFFF9:  //  Device Filler
        case 0xFFFFFFF8:  //  --
        {
            retval = DeviceFillerQuality;
            break;
        }
        case 0xFFFFFFF7:  //  Power failure occurred during interval
        case 0xFFFFFFF6:  //  --
        {
            retval = PowerfailQuality;
            break;
        }
        case 0xFFFFFFF5:  //  Power restored in this interval
        case 0xFFFFFFF4:  //  --
        {
            retval = PartialIntervalQuality;
            break;
        }
        case 0xFFFFFFE1:  //  Overflow
        case 0xFFFFFFE0:  //  --
        {
            retval = OverflowQuality;
            break;
        }
        default:
        case 0x00:
        {
            retval = NormalQuality;
            break;
        }
    }

    return retval;
}


unsigned long CtiDeviceMCT410::getDataValue( unsigned char *buf, int len )
{
    unsigned long val = 0;

    if( getDataQuality(buf, len) == NormalQuality )
    {
        for( int i = 0; i < len; i++ )
        {
            val <<= 8;
            val  |= buf[i];
        }
    }
    else
    {
        val = 0;
    }

    return val;
}


INT CtiDeviceMCT410::executeGetValueLoadProfile( CtiRequestMsg              *pReq,
                                                 CtiCommandParser           &parse,
                                                 OUTMESS                   *&OutMessage,
                                                 RWTPtrSlist< CtiMessage >  &vgList,
                                                 RWTPtrSlist< CtiMessage >  &retList,
                                                 RWTPtrSlist< OUTMESS >     &outList )
{
    INT    nRet = NoError;

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                   RWCString(OutMessage->Request.CommandStr),
                                                   RWCString(),
                                                   nRet,
                                                   OutMessage->Request.RouteID,
                                                   OutMessage->Request.MacroOffset,
                                                   OutMessage->Request.Attempt,
                                                   OutMessage->Request.TrxID,
                                                   OutMessage->Request.UserID,
                                                   OutMessage->Request.SOE,
                                                   RWOrdered( ));
    RWTime now_time;
    RWDate now_date;

    unsigned long request_time, relative_time;

    int request_channel = parse.getiValue("lp_channel");
    int year, month, day, hour, minute;
    int interval_len, block_len, function;

    RWCTokenizer date_tok(parse.getsValue("lp_date")),
                 time_tok(parse.getsValue("lp_time"));


    if( request_channel >  0 &&
        request_channel <= 4 )
    {
        month = atoi(RWCString(date_tok("-/")));
        day   = atoi(RWCString(date_tok("-/")));
        year  = atoi(RWCString(date_tok("-/")));

        hour   = atoi(RWCString(time_tok(":")));
        minute = atoi(RWCString(time_tok(":")));

        interval_len = getLoadProfile().getLoadProfileDemandRate();
        block_len    = 6 * interval_len;

        if( !month )    month = now_date.month();
        if( !day )      day   = now_date.dayOfMonth();
        if( !year )     year  = now_date.year();

        request_time  = RWTime(RWDate(day, month, year), hour, minute).seconds();
        request_time -= request_time % interval_len;
        request_time -= interval_len;  //  we report interval-ending, yet request interval-beginning...  so back that thing up

        //  this is the number of seconds from the current pointer
        relative_time = request_time - _llpInterestTime;

        if( (request_channel == _llpInterestChannel)  &&  //  correct channel
            (relative_time < (8 * block_len))         &&  //  within 8 blocks
            !(relative_time % block_len) )                //  aligned
        {
            //  it's aligned (and close enough) to the block we're pointing at
            function  = 0x40;
            function += relative_time / block_len;

            _llpInterestOffset = relative_time;
        }
        else
        {
            //  just read the first block - it'll be the one we're pointing at
            function  = 0x40;

            //  we need to set it to the requested interval
            CtiOutMessage *interest_om = new CtiOutMessage(*OutMessage);

            if( interest_om )
            {
                _llpInterestTime    = request_time;
                _llpInterestOffset  = 0;
                _llpInterestChannel = request_channel;

                interest_om->Sequence = CtiProtocolEmetcon::PutConfig_LoadProfileInterest;

                interest_om->Buffer.BSt.Function = 0x30;
                interest_om->Buffer.BSt.IO       = IO_WRITE;
                interest_om->Buffer.BSt.Length   = 5;

                unsigned long utc_time = request_time - rwEpoch;

                interest_om->Buffer.BSt.Message[0] = (utc_time >> 24) & 0x000000ff;
                interest_om->Buffer.BSt.Message[1] = (utc_time >> 16) & 0x000000ff;
                interest_om->Buffer.BSt.Message[2] = (utc_time >>  8) & 0x000000ff;
                interest_om->Buffer.BSt.Message[3] = (utc_time)       & 0x000000ff;

                interest_om->Buffer.BSt.Message[4] = request_channel & 0x000000ff;

                outList.append(interest_om);
                interest_om = 0;
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }

        OutMessage->Buffer.BSt.Function = function;
        OutMessage->Buffer.BSt.IO       = IO_FCT_READ;
        OutMessage->Buffer.BSt.Length   = 13;
    }
    else
    {
        if( errRet )
        {
            RWCString temp = "Bad channel specification - Acceptable values:  1-4";
            errRet->setResultString( temp );
            errRet->setStatus(NoMethod);
            retList.insert( errRet );
            errRet = NULL;
        }

        nRet = NoMethod;
    }

    return nRet;
}


INT CtiDeviceMCT410::decodeGetValueKWH(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;
    ULONG i,x;
    INT pid;
    RWCString resultString;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    unsigned long pulses;
    DOUBLE Value;
    PointQuality_t  quality;

    CtiPointBase         *pPoint = NULL;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Accumulator Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    //  ACH:  are these necessary?  /mskf
    resetScanFreezePending();
    resetScanFreezeFailed();


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

        pPoint = getDevicePointOffsetTypeEqual( 1, PulseAccumulatorPointType );

        pulses  = getDataValue  (DSt->Message, 3);
        quality = getDataQuality(DSt->Message, 3);

        // handle accumulator data here
        if( pPoint != NULL)
        {
            // 24 bit pulse value
            Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( pulses );
            RWTime pointTime;

            if( quality == NormalQuality )
            {
                resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
            }
            else
            {
                resultString = getName() + " / " + pPoint->getName() + " = (invalid data)";
            }

            if( quality != InvalidQuality )
            {
                pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, quality, PulseAccumulatorPointType, resultString);

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
                ReturnMsg->setResultString("Invalid quality");
            }
        }
        else
        {
            resultString = getName() + " / KYZ 1 = " + CtiNumStr(pulses) + "  --  POINT UNDEFINED IN DB";
            ReturnMsg->setResultString(resultString);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValueDemand(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int             status = NORMAL;
    double          Value;
    PointQuality_t  quality;
    RWCString resultString;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiPointBase         *pPoint = NULL;
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
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        Value   = getDataValue  (DSt->Message, 2);
        quality = getDataQuality(DSt->Message, 2);

        //  turn raw pulses into a demand reading
        Value *= DOUBLE(3600 / getDemandInterval());

        // look for first defined DEMAND accumulator
        pPoint = getDevicePointOffsetTypeEqual( 1, DemandAccumulatorPointType );

        if( pPoint != NULL)
        {
            RWRecursiveLock<RWMutexLock>::LockGuard pGuard( pPoint->getMux() );
            RWTime pointTime;

            Value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(Value);

            resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                     ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, quality, DemandAccumulatorPointType, resultString);

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

        Value   = getDataValue  (DSt->Message + 2, 2);
        quality = getDataQuality(DSt->Message + 2, 2);

        pPoint = getDevicePointOffsetTypeEqual( MCT4XX_VoltageOffset, DemandAccumulatorPointType );

        if( pPoint != NULL)
        {
            RWRecursiveLock<RWMutexLock>::LockGuard pGuard( pPoint->getMux() );
            RWTime pointTime;

            Value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(Value);

            resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                     ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, quality, DemandAccumulatorPointType, resultString);

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
            //  default multiplier for voltage
            Value *= 0.1;

            resultString = getName() + " / Voltage = " + CtiNumStr(Value) + "  --  POINT UNDEFINED IN DB";
            ReturnMsg->setResultString(ReturnMsg->ResultString() + "\n" + resultString);
        }

        Value   = getDataValue  (DSt->Message + 4, 2);
        quality = getDataQuality(DSt->Message + 4, 2);

        pPoint = getDevicePointOffsetTypeEqual( MCT_PointOffset_Accumulator_Powerfail, PulseAccumulatorPointType );

        if( pPoint != NULL)
        {
            RWRecursiveLock<RWMutexLock>::LockGuard pGuard( pPoint->getMux() );
            RWTime pointTime;

            Value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(Value);

            resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                     ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, quality, PulseAccumulatorPointType, resultString);

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
            resultString = getName() + " / Blink Counter = " + CtiNumStr(Value);
            ReturnMsg->setResultString(ReturnMsg->ResultString() + "\n" + resultString);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValuePeakDemand(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int             status = NORMAL;
    double          Value;
    unsigned long   timeOfPeak;
    PointQuality_t  quality;
    RWCString resultString;
    RWTime tmpTime;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiPointBase    *pPoint = NULL;
    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Peak Demand Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    //  resetScanPending();

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

        Value   = getDataValue  (DSt->Message, 2);
        quality = getDataQuality(DSt->Message, 2);

        timeOfPeak = DSt->Message[2] << 24 |
                     DSt->Message[3] << 16 |
                     DSt->Message[4] <<  8 |
                     DSt->Message[5];

        tmpTime = RWTime(timeOfPeak + rwEpoch);

        //  turn raw pulses into a demand reading
        Value *= DOUBLE(3600 / getDemandInterval());

        // look for first defined DEMAND accumulator
        pPoint = getDevicePointOffsetTypeEqual( 1 + MCT_PeakOffset, DemandAccumulatorPointType );

        if( pPoint != NULL)
        {
            RWRecursiveLock<RWMutexLock>::LockGuard pGuard( pPoint->getMux() );
            RWTime pointTime;

            Value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(Value);

            resultString = getName() + " / " + pPoint->getName() + " = "
                                     + CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces())
                                     + " @ " + tmpTime.asString();

            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, quality, DemandAccumulatorPointType, resultString);

            if(pData != NULL)
            {
                pData->setTime( tmpTime );
                ReturnMsg->PointData().insert(pData);
                pData = NULL;  // We just put it on the list...
            }
        }
        else
        {
            resultString = getName() + " / Peak Demand = " + CtiNumStr(Value) + " @ " + tmpTime.asString() + "  --  POINT UNDEFINED IN DB";
            ReturnMsg->setResultString(resultString);
        }

        Value   = getDataValue  (DSt->Message + 6, 3);
        quality = getDataQuality(DSt->Message + 6, 3);

        pPoint = getDevicePointOffsetTypeEqual( 1, PulseAccumulatorPointType );

        if( pPoint != NULL)
        {
            RWRecursiveLock<RWMutexLock>::LockGuard pGuard( pPoint->getMux() );
            RWTime pointTime;

            Value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(Value);

            resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                     ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, quality, PulseAccumulatorPointType, resultString);

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
            resultString = getName() + " / Current Meter Reading = " + CtiNumStr(Value) + "  --  POINT UNDEFINED IN DB";
            ReturnMsg->setResultString(ReturnMsg->ResultString() + "\n" + resultString);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValueVoltage( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    unsigned char *geneBuf = InMessage->Buffer.DSt.Message;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;
    PointQuality_t quality;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        double minVolts, maxVolts;
        RWTime minTime, maxTime;
        int tmpTime;
        RWCString resultString;

        CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg      *pData = NULL;

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        maxVolts = getDataValue  (DSt->Message, 2);
        quality  = getDataQuality(DSt->Message, 2);

        tmpTime = DSt->Message[2] << 24 |
                  DSt->Message[3] << 16 |
                  DSt->Message[4] <<  8 |
                  DSt->Message[5];

        maxTime = RWTime(tmpTime + rwEpoch);

        minVolts = getDataValue  (DSt->Message + 6, 2);
        quality  = getDataQuality(DSt->Message + 6, 2);

        tmpTime = DSt->Message[8]  << 24 |
                  DSt->Message[9]  << 16 |
                  DSt->Message[10] <<  8 |
                  DSt->Message[11];

        minTime = RWTime(tmpTime + rwEpoch);

        CtiPoint *pPoint = getDevicePointOffsetTypeEqual( MCT4XX_VoltageOffset, DemandAccumulatorPointType );

        if( pPoint != NULL)
        {
            RWRecursiveLock<RWMutexLock>::LockGuard pGuard( pPoint->getMux() );

            maxVolts = ((CtiPointNumeric*)pPoint)->computeValueForUOM(maxVolts);
            minVolts = ((CtiPointNumeric*)pPoint)->computeValueForUOM(minVolts);
        }
        else
        {
            maxVolts *= 0.1;
            minVolts *= 0.1;
        }

        resultString  = getName() + " / Min Voltage = " + CtiNumStr(minVolts) + " @ " + minTime.asString() + "\n";
        resultString += getName() + " / Max Voltage = " + CtiNumStr(maxVolts) + " @ " + maxTime.asString();

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValueOutage( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    unsigned char *msgbuf = InMessage->Buffer.DSt.Message;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        int outagenum;
        unsigned long  timestamp;
        unsigned short duration;
        RWCString resultString;
        RWTime outageTime;

        CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        outagenum = parse.getiValue("outage");

        if( !(outagenum % 2) )
        {
            outagenum--;
        }

        for( int i = 0; i < 2; i++ )
        {
            timestamp = msgbuf[(i*6)+0] << 24 |
                        msgbuf[(i*6)+1] << 16 |
                        msgbuf[(i*6)+2] <<  8 |
                        msgbuf[(i*6)+3];

            duration  = msgbuf[(i*6)+4] << 8 |
                        msgbuf[(i*6)+5];

            outageTime = RWTime(timestamp + rwEpoch);

            resultString += getName() + " / Outage " + CtiNumStr(outagenum + i) + " : " + outageTime.asString() + " for ";

            if( duration == 0x8000 )
            {
                resultString += "(unknown duration)\n";
            }
            else
            {
                int days, hours, minutes, seconds, cycles;

                if( duration & 0x8000 )
                {
                    cycles = (duration & 0x7fff) * 60;
                }
                else
                {
                    cycles = duration;
                }

                seconds = cycles  / 60;
                minutes = seconds / 60;
                hours   = minutes / 60;

                cycles  %= 60;
                seconds %= 60;
                minutes %= 60;
                hours   %= 24;

                resultString += CtiNumStr(hours).zpad(2) + ":" +
                                CtiNumStr(minutes).zpad(2) + ":" +
                                CtiNumStr(seconds).zpad(2);

                if( cycles )
                {
                    resultString += ", " + CtiNumStr(cycles) + " cycles\n";
                }
            }

            if( !i )    resultString += "\n";
        }

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValueLoadProfile(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    RWCString valReport, resultString;
    int       interval_len, block_len, function, channel,
              badData;
    double    Value;
    unsigned long timeStamp, pulses, decode_time;
    PointQuality_t quality;


    CtiPointNumeric *pPoint    = NULL;
    CtiReturnMsg    *ReturnMsg = NULL;  // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData     = NULL;

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

        interval_len = getLoadProfile().getLoadProfileDemandRate();
        block_len    = interval_len * 6;

        channel      = _llpInterestChannel;
        decode_time  = _llpInterestTime + _llpInterestOffset;

        pPoint = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( channel + OFFSET_LOADPROFILE_OFFSET, DemandAccumulatorPointType );

        for( int i = 0; i < 6; i++ )
        {
            //  this is where the block started...
            timeStamp  = decode_time + (interval_len * i);
            //  but we want interval *ending* times, so add on one more interval
            timeStamp += interval_len;

            pulses  = getDataValue  (DSt->Message + (i * 2) + 1, 2);
            quality = getDataQuality(DSt->Message + (i * 2) + 1, 2);

            if( quality == NormalQuality )
            {
                Value = pulses * (3600 / interval_len);
            }

            if( pPoint != NULL )
            {
                Value = pPoint->computeValueForUOM( Value );

                if( quality == NormalQuality )
                {
                    valReport = getName() + " / " + pPoint->getName() + " @ " + RWTime(timeStamp).asString() + " = " + CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
                }
                else
                {
                    valReport = getName() + " / " + pPoint->getName() + " @ " + RWTime(timeStamp).asString() + " = (invalid data)";
                }

                if( quality != InvalidQuality )
                {
                    pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(),
                                                       Value,
                                                       quality,
                                                       DemandAccumulatorPointType,
                                                       valReport);

                    pData->setTime( timeStamp );

                    ReturnMsg->insert( pData );
                }
                else
                {
                    resultString += valReport + "\n";
                }
            }
            else
            {
                if( quality == NormalQuality )
                {
                    resultString += getName() + " / LP channel " + CtiNumStr(channel) + " @ " + RWTime(timeStamp).asString() + " = " + CtiNumStr(Value, 0) + "\n";
                }
                else
                {
                    resultString += getName() + " / LP channel " + CtiNumStr(channel) + " @ " + RWTime(timeStamp).asString() + " = (invalid data)\n";
                }
            }
        }

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeScanLoadProfile(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    RWCString valReport;
    int       intervalOffset,
              lpDemandRate;
    bool      badData = false;
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

        if( pPoint != NULL )
        {
            for( intervalOffset = 0; intervalOffset < 6; intervalOffset++ )
            {
                //  error code in the top 5 bits - parsed by checkLoadProfileQuality
                pulses   = DSt->Message[intervalOffset*2 + 1];
                pulses <<= 8;
                pulses  |= DSt->Message[intervalOffset*2 + 2];

                if( badData )  //  load survey was halted - the rest of the data is bad
                {
                    pointQuality = DeviceFillerQuality;
                    Value = 0.0;
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


INT CtiDeviceMCT410::decodeGetStatusInternal( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
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

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        resultString  = getName() + " / Internal Status:\n";

        resultString += (InMessage->Buffer.DSt.Message[0] & 0x01)?"Group addressing disabled\n":"Group addressing enabled\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x04)?"DST active\n":"DST inactive\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x08)?"Holiday active\n":"";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x10)?"TOU disabled\n":"TOU enabled\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x20)?"Time sync needed\n":"In time sync\n";

        resultString += (InMessage->Buffer.DSt.Message[1] & 0x01)?"Power Fail occurred\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x02)?"Under-Voltage Event\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x04)?"Over-Voltage Event\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x08)?"Power Fail Carryover\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x10)?"RTC Adjusted\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x20)?"Holiday Event occurred\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x40)?"DST Change occurred\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x80)?"Tamper Flag set\n":"";

        resultString += (InMessage->Buffer.DSt.Message[3] & 0x01)?"Soft kWh Error, Data OK\n":"";
        resultString += (InMessage->Buffer.DSt.Message[3] & 0x02)?"Low AC Volts\n":"";
        resultString += (InMessage->Buffer.DSt.Message[3] & 0x04)?"Current Too High\n":"";
        resultString += (InMessage->Buffer.DSt.Message[3] & 0x08)?"Power Failure\n":"";
        resultString += (InMessage->Buffer.DSt.Message[3] & 0x10)?"Hard EEPROM Error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[3] & 0x20)?"Hard kWh Error, Data Lost\n":"";
        resultString += (InMessage->Buffer.DSt.Message[3] & 0x40)?"Configuration Error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[3] & 0x80)?"Reverse Power\n":"";

        resultString += (InMessage->Buffer.DSt.Message[4] & 0x01)?"7759 Calibration Error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[4] & 0x02)?"7759 Register Check Error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[4] & 0x04)?"7759 Reset Error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[4] & 0x08)?"RAM Bit Error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[4] & 0x10)?"General CRC Error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[4] & 0x20)?"Soft EEPROM Error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[4] & 0x40)?"Watchdog Restart\n":"";
        resultString += (InMessage->Buffer.DSt.Message[4] & 0x80)?"7759 Bit Checksum Error\n":"";

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetStatusLoadProfile( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    unsigned char *geneBuf = InMessage->Buffer.DSt.Message;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        RWCString resultString;
        unsigned long tmpTime;
        RWTime lpTime;

        CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg      *pData = NULL;

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        resultString += getName() + " / Demand Load Profile Status:\n";

        tmpTime = DSt->Message[0] << 24 |
                  DSt->Message[1] << 16 |
                  DSt->Message[2] <<  8 |
                  DSt->Message[3];

        lpTime = RWTime(tmpTime + rwEpoch);

        resultString += "Current Interval Time: " + lpTime.asString() + "\n";
        resultString += "Current Interval Pointer: " + CtiNumStr(DSt->Message[4]) + "\n";
        resultString += (DSt->Message[5] & 0x01)?"Boundary Error\n":"";
        resultString += (DSt->Message[5] & 0x02)?"Power Fail\n":"";
        resultString += (DSt->Message[5] & 0x80)?"Channel 1 Overflow\n":"";
        resultString += (DSt->Message[5] & 0x40)?"Channel 2 Overflow\n":"";
        resultString += (DSt->Message[5] & 0x20)?"Channel 3 Overflow\n":"";

        resultString += "\n";

        resultString += getName() + " / Voltage Load Profile Status:\n";

        tmpTime = DSt->Message[6] << 24 |
                  DSt->Message[7] << 16 |
                  DSt->Message[8] <<  8 |
                  DSt->Message[9];

        lpTime = RWTime(tmpTime + rwEpoch);

        resultString += "Current Interval Time: " + lpTime.asString() + "\n";
        resultString += "Current Interval Pointer: " + CtiNumStr(DSt->Message[10]) + "\n";
        resultString += (DSt->Message[11] & 0x01)?"Boundary Error\n":"";
        resultString += (DSt->Message[11] & 0x02)?"Power Fail\n":"";

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetConfigInterval(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        RWCString resultString;

        switch( InMessage->Sequence )
        {
            case (CtiProtocolEmetcon::GetConfig_DemandInterval):
            {
                resultString = getName() + " / Demand Interval: " + CtiNumStr(DSt->Message[0]);
                break;
            }
            case (CtiProtocolEmetcon::GetConfig_LoadProfileInterval):
            {
                resultString = getName() + " / Load Profile Interval: " + CtiNumStr(DSt->Message[0]);
                break;
            }
            default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

                status = NoMethod;
            }
        }

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetConfigTime(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        RWCString resultString;
        unsigned long time;
        RWTime tmpTime;

        time = InMessage->Buffer.DSt.Message[0] << 24 |
               InMessage->Buffer.DSt.Message[1] << 16 |
               InMessage->Buffer.DSt.Message[2] <<  8 |
               InMessage->Buffer.DSt.Message[3];

        tmpTime = RWTime(time + rwEpoch);

        if( InMessage->Sequence == CtiProtocolEmetcon::GetConfig_Time )
        {
            resultString = getName() + " / Current Time: " + tmpTime.asString();
        }
        else if( InMessage->Sequence == CtiProtocolEmetcon::GetConfig_TSync )
        {
            resultString = getName() + " / Time Last Synced at: " + tmpTime.asString();
        }

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetConfigModel(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        RWCString sspec;
        RWCString options;
        int  ssp;
        char rev;
        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi


        ssp  = InMessage->Buffer.DSt.Message[0];
        ssp |= InMessage->Buffer.DSt.Message[4] << 8;

        rev  = 'A' + InMessage->Buffer.DSt.Message[1] - 1;

        sspec = "\nSoftware Specification " + CtiNumStr(ssp) + "  Rom Revision " + RWCString(rev) + "\n";

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

