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
* REVISION     :  $Revision: 1.21 $
* DATE         :  $Date: 2004/01/06 20:28:29 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)


#include <windows.h>
#include "device.h"
#include "devicetypes.h"
#include "dev_mct24X.h"
#include "logger.h"
#include "mgr_point.h"
#include "porter.h"
#include "pt_numeric.h"
#include "yukon.h"
#include "numstr.h"
#include "dllyukon.h"

set< CtiDLCCommandStore > CtiDeviceMCT24X::_commandStore;


CtiDeviceMCT24X::CtiDeviceMCT24X( ) { }

CtiDeviceMCT24X::CtiDeviceMCT24X( const CtiDeviceMCT24X &aRef )
{
    *this = aRef;
}

CtiDeviceMCT24X::~CtiDeviceMCT24X( ) { }

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

    cs._cmd     = CtiProtocolEmetcon::GetStatus_Disconnect;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT24X_DiscPos,
                             (int)MCT24X_DiscLen );
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
    RWTime Now, blockStart, plannedLPTime, panicLPTime;
    unsigned long nextTime, midnightOffset;
    int lpBlockSize, lpDemandRate, lpMaxBlocks;

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

        //  if we collect hour data, we only keep a day;  anything less, we keep 48 intervals
        if( lpDemandRate == 3600 )
            lpMaxBlocks = 4;
        else
            lpMaxBlocks = 8;

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


INT CtiDeviceMCT24X::calcAndInsertLPRequests(OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList)
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

    setMCTScanPending(ScanRateGeneral, false);  //resetScanPending();

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiPointBase    *pPoint;
        CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg *pData     = NULL;

        double Value;
        RWCString rwtemp;

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
            RWCString disc;

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
                case 0x42:
                {
                    Value  = CLOSED;
                    rwtemp = " SERVICE CONNECT ENABLED";
                    break;
                }
                case 0x41:
                {
                    Value  = OPENED;
                    rwtemp = " SERVICE DISCONNECTED";
                    break;
                }
                default:
                {
                    Value  = INVALID;
                    rwtemp = " UNKNOWN / No Disconnect Status";
                    break;
                }
            }


            //  Send this value to requestor via retList.
            if(pPoint != NULL)
            {
                rwtemp = getName() + " / " + pPoint->getName() + ":" + rwtemp;

                pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, StatusPointType, rwtemp);

                if(pData != NULL)
                {
                    ReturnMsg->PointData().insert(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            else
            {
                rwtemp = getName() + " / Disconnect:" + rwtemp + "  --  point undefined in DB";
                ReturnMsg->setResultString( rwtemp );
            }
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT24X::decodeGetStatusDisconnect(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;


    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiPointBase    *pPoint;
        CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg *pData     = NULL;

        double Value;
        RWCString rwtemp;

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        pPoint = getDevicePointOffsetTypeEqual(1, StatusPointType);

        Value = CLOSED;

        switch(InMessage->Buffer.DSt.Message[0])
        {
            case 0x42:
            {
                Value = CLOSED;
                rwtemp += " SERVICE CONNECT ENABLED";
                break;
            }
            case 0x41:
            {
                Value = OPENED;
                rwtemp += " SERVICE DISCONNECTED";
                break;
            }
            default:
            {
                Value = INVALID;
                rwtemp += " UNKNOWN / No Disconnect Status";
                break;
            }
        }


        //  Send this value to requestor via retList.
        if(pPoint != NULL)
        {
            rwtemp = getName() + " / " + pPoint->getName() + ":" + rwtemp;

            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, StatusPointType, rwtemp);
            if(pData != NULL)
            {
                ReturnMsg->PointData().insert(pData);
                pData = NULL;  // We just put it on the list...
            }
        }
        else
        {
            rwtemp = getName() + " / Disconnect:" + rwtemp + "  --  point undefined in DB";
            ReturnMsg->setResultString( rwtemp );
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


