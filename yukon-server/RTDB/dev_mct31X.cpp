#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:38 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "devicetypes.h"
#include "dev_mct31X.h"
#include "logger.h"
#include "mgr_point.h"
#include "prot_emetcon.h"
#include "pt_numeric.h"
#include "numstr.h"
#include "portsup.h"


set< CtiDLCCommandStore > CtiDeviceMCT31X::_commandStore;

CtiDeviceMCT31X::CtiDeviceMCT31X( ) { }

CtiDeviceMCT31X::CtiDeviceMCT31X( const CtiDeviceMCT31X &aRef )
{
    *this = aRef;
}

CtiDeviceMCT31X::~CtiDeviceMCT31X( ) { }

CtiDeviceMCT31X& CtiDeviceMCT31X::operator=( const CtiDeviceMCT31X &aRef )
{
    if( this != &aRef )
    {
        //  ACH, if needed
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

    cs._cmd     = CtiProtocolEmetcon::Scan_General;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair( (int)MCT31X_FuncReadDemandAddr,
                             (int)MCT31X_FuncReadStatusLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::Scan_Integrity;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair( (int)MCT31X_FuncReadDemandAddr,
                             (int)MCT31X_FuncReadDemandLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetValue_Demand;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair( (int)MCT31X_FuncReadDemandAddr,
                             (int)MCT31X_FuncReadDemandLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::Scan_LoadProfile;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair(0,0);
    _commandStore.insert( cs );

    //  add the 2 other channels for 318s
    cs._cmd     = CtiProtocolEmetcon::PutValue_KYZ2;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair( (int)MCT3XX_PutMRead2Addr,
                             (int)MCT3XX_PutMReadLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutValue_KYZ3;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair( (int)MCT3XX_PutMRead3Addr,
                             (int)MCT3XX_PutMReadLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetStatus_External;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair( (int)MCT31X_FuncReadDemandAddr,
                             (int)MCT31X_FuncReadStatusLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetConfig_Multiplier2;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT3XX_Mult2Addr,
                             (int)MCT3XX_MultLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetConfig_Multiplier3;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT3XX_Mult3Addr,
                             (int)MCT3XX_MultLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_Multiplier2;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair( (int)MCT3XX_Mult2Addr,
                             (int)MCT3XX_MultLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_Multiplier3;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair( (int)MCT3XX_Mult3Addr,
                             (int)MCT3XX_MultLen );
    _commandStore.insert( cs );

    //  these are commands for the 360 and 370 only

    //  scan address and length are identical for the p+ and s4
    cs._cmd     = CtiProtocolEmetcon::GetConfig_IEDScan;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT360_IEDScanAddr,
                             (int)MCT360_IEDScanLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_IEDScan;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair( (int)MCT360_IEDScanAddr, 2 );  //  just 2 bytes - seconds and delay
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_IEDClass;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair( (int)MCT360_IEDClassAddr,
                             (int)MCT360_IEDClassLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetConfig_IEDTime;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair( (int)MCT360_IEDTimeAddr,
                             (int)MCT360_IEDTimeLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetValue_IEDDemand;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair( (int)MCT360_IEDDemandAddr,
                             (int)MCT360_IEDReqLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetValue_IEDKwh;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair( (int)MCT360_IEDKwhAddr,
                             (int)MCT360_IEDReqLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetValue_IEDKvarh;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair( (int)MCT360_IEDKvarhAddr,
                             (int)MCT360_IEDReqLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetValue_IEDKvah;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair( (int)MCT360_IEDKvahAddr,
                             (int)MCT360_IEDReqLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutValue_IEDReset;
    cs._io      = IO_FCT_WRITE;
    cs._funcLen = make_pair( (int)MCT360_IEDResetAddr,
                             (int)MCT360_IEDResetLen );
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

    CTICMDSET::iterator itr = _commandStore.find( CtiDLCCommandStore( cmd ) );

    //  the 318L is the only 31x that supports load profile that we're concerned about here, and it'd be silly to make another class for one function
    //    we want it to stop here, no matter what, no Inherited::anything...
    if( getType( ) != TYPEMCT318L &&
        cmd == CtiProtocolEmetcon::Scan_LoadProfile )
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
    RWTime        Now, blockStart, plannedLPTime, panicLPTime;
    unsigned long channelTime, nextTime, midnightOffset;
    int           lpBlockSize, lpDemandRate;

    nextTime = YUKONEOT;

    lpDemandRate = getLoadProfile().getLoadProfileDemandRate();
    //  we read 6 intervals at a time - it's all the function reads will allow
    lpBlockSize  = lpDemandRate * 6;

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
        for( int i = 0; i < 3; i++ )
        {
            //  if we're not collecting load profile, don't scan
            if( !getLoadProfile().isChannelValid(i) )
            {
                _nextLPTime[i] = YUKONEOT;
                continue;
            }

            //  the only way for this to have happened is if they haven't been initialized yet...
            //    both were set to ~Now() at the constructor
            if( (_lastLPRequestAttempt[i].seconds() - _lastLPRequestBlockStart[i].seconds()) < 5 ||
                (_lastLPRequestBlockStart[i].seconds() - _lastLPRequestAttempt[i].seconds()) < 5 )
            {
                //  so we haven't talked to it yet
                _lastLPRequestAttempt[i] = Now;
                //  and the last block we requested was sometime back in 1901
                _lastLPRequestBlockStart[i] = (unsigned long)86400;
                _nextLPTime[i] = Now;

                CtiPointBase *pPoint = getDevicePointOffsetTypeEqual((i+1) + OFFSET_LOADPROFILE_OFFSET, DemandAccumulatorPointType);

                if( pPoint != NULL )
                {
                    RWDBConnection conn = getConnection();
                    RWLockGuard<RWDBConnection> conn_guard(conn);

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
                        rdr >> _lastLPTime[i];

                        if( !(_lastLPTime[i].seconds()) )  //  make sure it's not zero - causes problems with time zones
                        {
                            _lastLPTime[i] = (unsigned long)86400;
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "**** Checkpoint: Invalid Reader **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        //  so assume we haven't collected any data, start from scratch
                        _lastLPTime[i] = (unsigned long)86400;
                    }
                }
                else
                {
                    //  no point in DB, don't request LP data for this channel
                    continue;
                }
            }

            blockStart = _lastLPTime[i];

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
            plannedLPTime += 300;

            //  we start to worry if 30 minutes have passed and we haven't heard back
            panicLPTime    = plannedLPTime + 30 * 60;

            //  if we're still on schedule for our normal request
            //    (and we haven't already made our request for this block)
            if( _lastLPRequestAttempt[i] < plannedLPTime &&
                _lastLPRequestBlockStart[i] < blockStart )
            {
                channelTime = plannedLPTime.seconds();
            }
            //  if we need to initiate a repeat request
            else if( _lastLPRequestAttempt[i] < panicLPTime &&
                     _lastLPRequestBlockStart[i] == blockStart )
            {
                channelTime = panicLPTime.seconds();
            }
            //  we're overdue
            else
            {
                //  try again on the next 'loadprofileinterval' minutes boundary
                channelTime  = Now.seconds() + lpDemandRate;
                if( channelTime % lpDemandRate )
                {
                    channelTime -= channelTime % lpDemandRate;
                }
            }

            _nextLPTime[i] = channelTime;

            if( channelTime < nextTime )
                nextTime = channelTime;
        }
    }

    return (_nextLPScanTime = nextTime);
}


INT CtiDeviceMCT31X::calcAndInsertLPRequests(OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList)
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
    OUTMESS       *tmpOutMess;

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

        for( int i = 0; i < 3; i++ )
        {
            if( _nextLPTime[i] <= Now )
            {
                tmpOutMess = new OUTMESS(*OutMessage);

                lpBlockStartTime = _lastLPTime[i];

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

                _lastLPRequestBlockStart[i] = lpBlockStartTime;
                _lastLPRequestAttempt[i]    = Now;

                //  adjust for wraparound
                lpBlockAddress = lpMidnightOffset % (lpBlockSize * lpMaxBlocks);

                //  which block to grab?
                lpBlockAddress /= lpBlockSize;

                switch( i )
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

                tmpOutMess->Buffer.BSt.Function = lpBlockAddress;
                tmpOutMess->Buffer.BSt.Length   = 13;  //  2 bytes per interval, and a status byte to boot

                strcat( tmpOutMess->Request.CommandStr, RWCString(" ") + CtiNumStr(i) );  //  save the offset of this channel

                outList.append(tmpOutMess);

                tmpOutMess = NULL;
            }
            else
            {
                if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
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



INT CtiDeviceMCT31X::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    switch(InMessage->Sequence)
    {
        case CtiProtocolEmetcon::Scan_General:
        case CtiProtocolEmetcon::GetStatus_External:
        {
            //  A general scan for any MCT does status decode only.
            status = decodeStatus(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case CtiProtocolEmetcon::Scan_Accum:
        case CtiProtocolEmetcon::GetValue_Default:
        {
            status = decodeGetValueKWH(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case CtiProtocolEmetcon::Scan_Integrity:
        case CtiProtocolEmetcon::GetValue_Demand:
        {
            //  we only have status info if we're not getting the demand from the IED
            if( getType() == TYPEMCT360 ||
                getType() == TYPEMCT370 )
            {
                if( getIEDPort().getRealTimeScanFlag() )
                {
                    status = decodeGetValueIED(InMessage, TimeNow, vgList, retList, outList);
                }
            }
            else
            {
                status = decodeStatus(InMessage, TimeNow, vgList, retList, outList);

                if(status)  //  OR these or something, we should be smarter
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                status = decodeGetValueDemand(InMessage, TimeNow, vgList, retList, outList);
            }
            break;
        }

        case (CtiProtocolEmetcon::Scan_LoadProfile):
        {
            status = decodeScanLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (CtiProtocolEmetcon::GetConfig_IEDTime):
        case (CtiProtocolEmetcon::GetConfig_IEDScan):
        {
            status = decodeGetConfigIED(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (CtiProtocolEmetcon::GetValue_IED):
        case (CtiProtocolEmetcon::GetValue_IEDDemand):
        case (CtiProtocolEmetcon::GetValue_IEDKvah):
        case (CtiProtocolEmetcon::GetValue_IEDKvarh):
        case (CtiProtocolEmetcon::GetValue_IEDKwh):
        {
            status = decodeGetValueIED(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case CtiProtocolEmetcon::GetValue_Frozen:
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


INT CtiDeviceMCT31X::decodeStatus(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;
    ULONG i;
    USHORT StatusData[8];
    USHORT SaveCount;
    RWCString resultString;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    DOUBLE Value;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Status Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    resetScanPending();


    if(!decodeCheckErrorReturn(InMessage, retList))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;
            }

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        extractStatusData(InMessage, getType(), StatusData);

        //  Now sweep the points on this device looking for any status points!  Each gets updated to dispatch via retList.

        if(_pointMgr == NULL)      // Attached via the dev_base object.
        {
            RefreshDevicePoints(  );
        }

        if(_pointMgr != NULL)      // Attached via the dev_base object.
        {
            LockGuard guard(monitor());               // Lock the MCT device!
            CtiPointBase *pPoint;

            CtiPointManager::CtiRTDBIterator itr( _pointMgr->getMap() );

            for(;itr();)
            {
                pPoint = itr.value();
                {
                    RWRecursiveLock<RWMutexLock>::LockGuard pGuard( pPoint->getMux() );
                    switch( pPoint->getType() )
                    {
                        case StatusPointType:
                        {
                            //  good data must translate it
                            Value = translateStatusValue(pPoint->getPointOffset(), pPoint->getType(), getType(), StatusData);

                            resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value);

                            pData = new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, StatusPointType, resultString);

                            if(pData != NULL)
                            {
                                ReturnMsg->PointData().insert(pData);
                                pData = NULL;  // We just put it on the list...
                            }

                            break;
                        }

                        default:
                        {
                            break;
                        }
                    }
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retMsgHandler( InMessage->Return.CommandStr, ReturnMsg, vgList, retList );
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

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** IED GetConfig Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if(!decodeCheckErrorReturn(InMessage, retList))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        if( parse.isKeyValid("time") )
        {
            for( int i = 0; i < 7; i++ )  //  excluding byte 7 - it's a bitfield, not BCD
            {
                //  BCD is not make happiness.  so FIX!  Whee!
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

            ReturnMsg->setResultString( resultString );
        }
        else if( parse.isKeyValid("scan") )
        {
            switch( getIEDPort().getIEDType() )
            {
                case (CtiTableDeviceMCTIEDPort::AlphaPowerPlus):
                {
                    resultString += getName() + " / Alpha Power Plus scan info:\n";

                    if( DSt->Message[5] == 11 )
                        resultString += "  Buffer Contains: Alpha Class 11 Billing (Current)\n";
                    else if( DSt->Message[5] == 12 )
                        resultString += "  Buffer Contains: Alpha Class 12 Billing (Previous)\n";
                    else
                        resultString += "  Buffer Contains: Alpha Class " + CtiNumStr((int)DSt->Message[5]) + "\n";

                    break;
                }
                case (CtiTableDeviceMCTIEDPort::LandisGyrS4):
                {
                    resultString += getName() + " / Landis and Gyr S4 scan info:\n";

                    if( DSt->Message[5] == 0 )
                        resultString += "  Buffer Contains: CTI Billing Data Table #" + CtiNumStr((int)DSt->Message[4] + 1) + "\n";
                    else
                        resultString += "  Buffer Contains: S4 Meter Read Cmd: " + CtiNumStr((int)DSt->Message[4]) + "\n";
                    break;
                }
            }

            resultString += "  Scan Rate:            " + CtiNumStr(((int)DSt->Message[0] * 15) + 30).spad(4) + " seconds\n";
            resultString += "  Buffer refresh delay: " + CtiNumStr((int)DSt->Message[1] * 15).spad(4) + " seconds\n";

            if( DSt->Message[2] == 0 )
                DSt->Message[2] = 128;

            resultString += "  Scan Length:     " + CtiNumStr((int)DSt->Message[2]).spad(3) + " bytes\n";
            resultString += "  Scan Offset:     " + CtiNumStr( ((int)DSt->Message[3] * 256) + (int)DSt->Message[4] ).spad(3);

            ReturnMsg->setResultString( resultString );
        }
    }

    retMsgHandler( InMessage->Return.CommandStr, ReturnMsg, vgList, retList );

    return status;
}


INT CtiDeviceMCT31X::decodeGetValueIED(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
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

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** IED GetValue Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if(!decodeCheckErrorReturn(InMessage, retList))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
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
                    //  NOOP
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

            if( parse.getFlags() & CMD_FLAG_GV_DEMAND )
            {
                //  get KW
                pPoint = getDevicePointOffsetTypeEqual( pid, AnalogPointType );

                Value  = BCDtoBase10( DSt->Message, 3 );
                Value /= 1000.0;

                if( pPoint != NULL )
                {
                    Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                    resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                             ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
                    pData = new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, resultString);
                    if(pData != NULL)
                    {
                        ReturnMsg->PointData().insert(pData);
                        pData = NULL;  // We just put it on the list...
                    }
                }
                else
                {
                    resultString = getName() + " / IED KW  : " + CtiNumStr(Value) + " - point undefined in DB\n";
                    ReturnMsg->setResultString( resultString );
                }

                //  get KVAR
                pPoint = getDevicePointOffsetTypeEqual( pid + 10, AnalogPointType );

                Value  = BCDtoBase10( DSt->Message + 3, 3 );
                Value /= 1000.0;

                if( pPoint != NULL )
                {
                    Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                    resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                             ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
                    pData = new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, resultString);
                    if(pData != NULL)
                    {
                        ReturnMsg->PointData().insert(pData);
                        pData = NULL;  // We just put it on the list...
                    }
                }
                else
                {
                    resultString  = ReturnMsg->ResultString();
                    resultString += getName() + " / IED KVAR: " + CtiNumStr(Value) + " - point undefined in DB\n";
                    ReturnMsg->setResultString( resultString );
                }

                lValue  = BCDtoBase10( DSt->Message + 9, 2 );

                resultString  = ReturnMsg->ResultString();
                resultString += getName() + " / IED Power Outage Count: " + CtiNumStr(lValue) + "\n";

                lValue  = BCDtoBase10( DSt->Message + 11, 1 );

                if( lValue & 0x08 ) resultString += "Phase A potential is missing\n";
                if( lValue & 0x04 ) resultString += "Phase B potential is missing\n";
                if( lValue & 0x02 ) resultString += "Phase C potential is missing\n";

                ReturnMsg->setResultString( resultString );
            }
            else if( parse.getFlags() & CMD_FLAG_GV_RATET )
            {
                pPoint = getDevicePointOffsetTypeEqual( pid, AnalogPointType );

                if( parse.getFlags() & CMD_FLAG_GV_KVAH || parse.getFlags() & CMD_FLAG_GV_KVARH )
                {
                    Value  = BCDtoBase10( DSt->Message + 7, 6 );
                }
                else
                {
                    Value  = BCDtoBase10( DSt->Message, 7 );
                    Value /= 100.0;
                }

                if( pPoint != NULL )
                {
                    Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                    resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                             ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
                    pData = new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, resultString);
                    if(pData != NULL)
                    {
                        ReturnMsg->PointData().insert(pData);
                        pData = NULL;  // We just put it on the list...
                    }
                }
                else
                {
                    resultString = getName() + " / total " + name + "h: " + CtiNumStr(Value) + " - point undefined in DB\n";
                    ReturnMsg->setResultString( resultString );
                }
            }
            else  //  ye olde typicale KWH reade
            {
                pPoint = getDevicePointOffsetTypeEqual( pid, AnalogPointType );

                Value  = BCDtoBase10( DSt->Message + 8, 5 );
                Value /= 100.0;

                if( pPoint != NULL )
                {
                    Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                    resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                             ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
                    pData = new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, resultString);
                    if(pData != NULL)
                    {
                        ReturnMsg->PointData().insert(pData);
                        pData = NULL;  // We just put it on the list...
                    }
                }
                else
                {
                    resultString = getName() + " / " + name + "h " + ratename + ": " + CtiNumStr(Value) + " - point undefined in DB\n";
                    ReturnMsg->setResultString( resultString );
                }

                pPoint = getDevicePointOffsetTypeEqual( pid - 1, AnalogPointType );

                Value  = BCDtoBase10( DSt->Message, 3 );
                Value /= 1000.0;

                for( i = 3; i < 8; i++ )
                {
                    //  BCD is not make happiness.  so FIX!  Whee!
                    DSt->Message[i] = (((DSt->Message[i] & 0xf0) / 16) * 10) + (DSt->Message[i] & 0x0f);
                }

                datestamp = RWDate((unsigned)DSt->Message[5], (unsigned)DSt->Message[4], (unsigned)DSt->Message[3] + 2000 );
                timestamp = RWTime( datestamp, (unsigned)DSt->Message[6], (unsigned)DSt->Message[7] );

                if( pPoint != NULL )
                {
                    Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM( Value );

                    resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                             ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
                    resultString += " @ " + CtiNumStr((int)DSt->Message[4]).zpad(2) + "/" +
                                            CtiNumStr((int)DSt->Message[5]).zpad(2) + "/" +
                                            CtiNumStr((int)DSt->Message[3]).zpad(2) + " " +
                                            CtiNumStr((int)DSt->Message[6]).zpad(2) + ":" +
                                            CtiNumStr((int)DSt->Message[7]).zpad(2);
                    pData = new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, AnalogPointType, resultString);
                    if(pData != NULL)
                    {
                        pData->setTime( timestamp.seconds() );

                        ReturnMsg->PointData().insert(pData);
                        pData = NULL;  // We just put it on the list...
                    }
                }
                else
                {
                    resultString  = ReturnMsg->ResultString();
                    resultString += getName() + " / " + name + " " + ratename + ": " + CtiNumStr(Value);
                    resultString += " @ " + CtiNumStr((int)DSt->Message[4]).zpad(2) + "/" +
                                            CtiNumStr((int)DSt->Message[5]).zpad(2) + "/" +
                                            CtiNumStr((int)DSt->Message[3]).zpad(2) + " " +
                                            CtiNumStr((int)DSt->Message[6]).zpad(2) + ":" +
                                            CtiNumStr((int)DSt->Message[7]).zpad(2) + " - point undefined in DB\n";
                    ReturnMsg->setResultString( resultString );
                }

            }
        }
    }

    retMsgHandler( InMessage->Return.CommandStr, ReturnMsg, vgList, retList );

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

    resetScanFreezePending();
    resetScanFreezeFailed();

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Accumulator Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if(!decodeCheckErrorReturn(InMessage, retList))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        decodeAccumulators(accums, 3, InMessage->Buffer.DSt.Message);

        RWTime pointTime;
        pointTime = pointTime.seconds() % 300;

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
                pData = new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, PulseAccumulatorPointType, resultString);
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
    }

    retMsgHandler( InMessage->Return.CommandStr, ReturnMsg, vgList, retList );

    return status;
}


INT CtiDeviceMCT31X::decodeGetValueDemand(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
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

    RWTime pointTime;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Demand Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    resetScanPending();

    if(!decodeCheckErrorReturn(InMessage, retList))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;
            }

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        pointTime -= pointTime.seconds() % getDemandInterval();

        for(pnt_offset = 1; pnt_offset <= 3; pnt_offset++)
        {
            byte_offset = ((pnt_offset - 1) * 2) + 1;          // First byte is the status byte.

            Error = (DSt->Message[byte_offset] & 0xc0);

            // 2 byte demand value.  Upper 2 bits are error indicators.
            Value = MAKEUSHORT(DSt->Message[byte_offset + 1], (DSt->Message[byte_offset] & 0x3f) );
            //  turn raw pulses into a demand reading
            Value *= DOUBLE(3600 / getDemandInterval());

            /* look for first defined DEMAND accumulator */
            pPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual( pnt_offset, DemandAccumulatorPointType );

            // handle demand data here
            if( pPoint != NULL)
            {
                // Adjust for the unit of measure!
                Value = pPoint->computeValueForUOM(Value);


                if(Error == 0)
                {
                    resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                             ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
                    pData = new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, DemandAccumulatorPointType, resultString);
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    resultString = "Error indicated on MCT " + getName() + " / " + pPoint->getName() + "  Error " + CtiNumStr(Error);
                    pData = new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, DemandAccumulatorPointType, resultString);
                }

                if(pData != NULL)
                {
                    pData->setTime( pointTime );

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

                resultString += getName() + " / Demand " + CtiNumStr(pnt_offset) + " = " + CtiNumStr(Value) + "  --  POINT UNDEFINED IN DB";
                ReturnMsg->setResultString( resultString );
            }
        }
    }


    if(ReturnMsg != NULL)
    {
        if(!(ReturnMsg->ResultString().isNull()) || ReturnMsg->getData().entries() > 0)
        {
            retMsgHandler( InMessage->Return.CommandStr, ReturnMsg, vgList, retList );
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


INT CtiDeviceMCT31X::decodeScanLoadProfile(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    RWCString valReport, resultString;
    int       intervalOffset,
              lpDemandRate,
              pointOffset,
              charOffset,
              badData;
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

    resetScanFreezePending( );
    resetScanFreezeFailed( );

    if( !decodeCheckErrorReturn( InMessage, retList ) )
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        charOffset  = strlen(InMessage->Return.CommandStr) - 1;  //  point to the last character in the string
        pointOffset = InMessage->Return.CommandStr[charOffset] - '0';  //  convert to numeric from ASCII

        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "channel " << pointOffset << endl;
        }

        lpDemandRate = getLoadProfile().getLoadProfileDemandRate();

        pPoint = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( 1 + pointOffset + OFFSET_LOADPROFILE_OFFSET, DemandAccumulatorPointType );

        if( pPoint != NULL )
        {
            badData = FALSE;

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

                pData = new CtiPointDataMsg(pPoint->getPointID(),
                                            Value,
                                            pointQuality,
                                            DemandAccumulatorPointType,
                                            "",
                                            TAG_POINT_LOAD_PROFILE_DATA );

                //  this is where the block started...
                timeStamp  = _lastLPRequestBlockStart[pointOffset].seconds() + (lpDemandRate * intervalOffset);

                //  but we want interval *ending* times, so add on one more interval
                timeStamp += lpDemandRate;

                if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "_lastLPRequestBlockStart[" << pointOffset << "] = " << RWTime(_lastLPRequestBlockStart[pointOffset]) << endl;
                    dout << "Value = " << Value << endl;
                    dout << "intervalOffset = " << intervalOffset << endl;
                    dout << "timeStamp = " << RWTime(timeStamp) << endl;
                }

                pData->setTime( timeStamp );

                ReturnMsg->insert( pData );
            }
            //  insert a point data message for TDC and the like
            //    note that timeStamp, Value, and pointQuality are set in the final iteration of the above for loop
            valReport = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                  ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());
            pData = new CtiPointDataMsg(pPoint->getPointID(),
                                        Value,
                                        pointQuality,
                                        DemandAccumulatorPointType,
                                        valReport );
            pData->setTime( timeStamp );
            ReturnMsg->insert( pData );

            _lastLPTime[pointOffset] = timeStamp;
        }
        else
        {
            resultString = "No load profile point defined for '" + getName() + "' demand accumulator " + CtiNumStr( pointOffset + 1 );
            ReturnMsg->setResultString( resultString );
        }
    }


    retMsgHandler( InMessage->Return.CommandStr, ReturnMsg, vgList, retList );

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
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

