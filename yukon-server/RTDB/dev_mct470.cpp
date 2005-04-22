/*-----------------------------------------------------------------------------*
*
* File:   dev_mct470
*
* Date:   2005-jan-03
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_mct310.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/04/22 19:00:28 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include "device.h"
#include "devicetypes.h"
#include "dev_mct470.h"
#include "logger.h"
#include "mgr_point.h"
#include "numstr.h"
#include "porter.h"
#include "dllyukon.h"
#include "utility.h"
#include "numstr.h"
#include <string>

using namespace std;
using Cti::Protocol::Emetcon;


const CtiDeviceMCT470::DLCCommandSet CtiDeviceMCT470::_commandStore   = CtiDeviceMCT470::initCommandStore();

CtiDeviceMCT470::CtiDeviceMCT470( ) :
    _intervalsSent(false)
{
}

CtiDeviceMCT470::CtiDeviceMCT470( const CtiDeviceMCT470 &aRef )
{
    *this = aRef;
}

CtiDeviceMCT470::~CtiDeviceMCT470( )
{
}

CtiDeviceMCT470 &CtiDeviceMCT470::operator=( const CtiDeviceMCT470 &aRef )
{
    if( this != &aRef )
    {
        Inherited::operator=( aRef );
    }

    return *this;
}


CtiDeviceMCT470::DLCCommandSet CtiDeviceMCT470::initCommandStore( )
{
    CtiDLCCommandStore cs;
    DLCCommandSet s;

    cs._cmd     = Emetcon::Scan_Accum;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)MCT470_FuncRead_MReadPos,
                            (int)MCT470_FuncRead_MReadLen );
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_Default;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)MCT470_FuncRead_MReadPos,
                            (int)MCT470_FuncRead_MReadLen );
    s.insert(cs);

    cs._cmd     = Emetcon::Scan_Integrity;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)MCT470_FuncRead_DemandPos,
                            (int)MCT470_FuncRead_DemandLen );
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_Demand;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)MCT470_FuncRead_DemandPos,
                            (int)MCT470_FuncRead_DemandLen );
    s.insert(cs);

    cs._cmd     = Emetcon::Scan_LoadProfile;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair(0, 0);
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_Demand;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)MCT470_FuncRead_DemandPos,
                            (int)MCT470_FuncRead_DemandLen );
    s.insert(cs);
/*
    cs._cmd      = Emetcon::GetValue_PeakDemand;
    cs._io       = Emetcon::IO_Function_Read;
    cs._funcLen  = make_pair((int)MCT470_FuncRead_PeakDemandPos,
                             (int)MCT470_FuncRead_PeakDemandLen );
    s.insert(cs);
*/
    cs._cmd     = Emetcon::PutConfig_Raw;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair( 0, 0 );  //  filled in later
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_Raw;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair( 0, 0 );  //  filled in later
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_Model;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair((int)MCT470_Memory_ModelPos,
                            (int)MCT470_Memory_ModelLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_Multiplier;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair((int)MCT470_Memory_ChannelMultiplierPos,
                            (int)MCT470_Memory_ChannelMultiplierLen);
    s.insert(cs);

    cs._cmd     = Emetcon::PutConfig_Multiplier;
    cs._io      = Emetcon::IO_Function_Write;
    cs._funcLen = make_pair((int)MCT470_Memory_ChannelMultiplierPos,
                            (int)MCT470_Memory_ChannelMultiplierLen);
    s.insert(cs);

    cs._cmd     = Emetcon::PutConfig_TSync;
    cs._io      = Emetcon::IO_Function_Write;
    cs._funcLen = make_pair((int)CtiDeviceMCT410::FuncWrite_TSyncPos,
                            (int)CtiDeviceMCT410::FuncWrite_TSyncLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_Time;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair((int)MCT470_Memory_RTCPos,
                            (int)MCT470_Memory_RTCLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_TSync;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair((int)MCT470_Memory_LastTSyncPos,
                            (int)MCT470_Memory_LastTSyncLen);
    s.insert(cs);


    cs._cmd     = Emetcon::PutConfig_Intervals;
    cs._io      = Emetcon::IO_Function_Write;
    cs._funcLen = make_pair((int)MCT470_FuncWrite_IntervalsPos,
                            (int)MCT470_FuncWrite_IntervalsLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_Intervals;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair((int)MCT470_Memory_IntervalsPos,
                            (int)MCT470_Memory_IntervalsLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetStatus_LoadProfile;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)MCT470_FuncRead_LPStatusCh1Ch2Pos,
                            (int)MCT470_FuncRead_LPStatusLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetStatus_Internal;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair((int)MCT470_Memory_StatusPos,
                            (int)MCT470_Memory_StatusLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_IED;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair(0, 13);  //  filled in by "getvalue ied" code
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_IEDDemand;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)MCT470_FuncRead_IED_RealTime, 9);  //  magic number
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_IEDTime;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)MCT470_FuncRead_IED_TOU_MeterStatus, 13);  //  magic number
    s.insert(cs);

    cs._cmd     = Emetcon::PutValue_IEDReset;
    cs._io      = Emetcon::IO_Function_Write;
    cs._funcLen = make_pair(0, 0);
    s.insert(cs);

    return s;
}


bool CtiDeviceMCT470::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    DLCCommandSet::const_iterator itr = _commandStore.find( CtiDLCCommandStore( cmd ) );

    if( itr != _commandStore.end( ) )
    {
        const CtiDLCCommandStore &cs = *itr;

        function = cs._funcLen.first;   //  Copy the relevant bits from the commandStore
        length   = cs._funcLen.second;  //
        io       = cs._io;              //

        found = true;
    }
    //  add this back in later - for now, we want to limit the interaction between the 470 and the 410
/*    else                                //  Look in the parent if not found in the child!
    {
        found = Inherited::getOperation( cmd, function, length, io );
    }
*/
    return found;
}


ULONG CtiDeviceMCT470::calcNextLPScanTime( void )
{
    RWTime        Now;
    unsigned long next_time, planned_time;
    int           demand_rate, block_size;

    next_time = YUKONEOT;

    demand_rate = getLoadProfile().getLoadProfileDemandRate();
    block_size  = demand_rate * 6;
    next_time   = YUKONEOT;

    if( !_intervalsSent )
    {
        //  send load profile interval on the next 5 minute boundary
        next_time  = (Now.seconds() - MCT_LPWindow) + 300;

        next_time -= next_time % 300;
    }
    else
    {
        for( int i = 0; i < MCT4XX_LPChannels; i++ )
        {
            CtiPointBase *pPoint = getDevicePointOffsetTypeEqual((i+1) + MCT_PointOffset_LoadProfileOffset, DemandAccumulatorPointType);

            //  if we're not collecting load profile, or there's no point defined, don't scan
            if( !getLoadProfile().isChannelValid(i) || pPoint == NULL )
            {
                _lp_info[i].current_schedule = YUKONEOT;

                continue;
            }

            //  uninitialized - get the readings from the DB
            if( !_lp_info[i].archived_reading )
            {
                //  so we haven't talked to it yet
                _lp_info[i].archived_reading = 86400;
                _lp_info[i].current_request  = 86400;

                {
                    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                    RWDBConnection conn = getConnection();

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
                        RWTime tmp;

                        rdr >> tmp;

                        if( tmp.seconds() != 0 )  //  make sure it's not zero - causes problems with time zones
                        {
                            _lp_info[i].archived_reading = tmp.seconds();
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "**** Checkpoint: Invalid Reader **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }

            //  basically, we plan to request again after a whole block has been recorded...
            //    then we add on a little bit to make sure the MCT is out of the memory
            planned_time  = _lp_info[i].archived_reading + block_size;
            planned_time -= planned_time % block_size;  //  make double sure we're block-aligned
            planned_time += LPBlockEvacuationTime;      //  add on the safeguard time

            /*
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint - lp calctime check... **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "planned_time = " << planned_time << endl;
                dout << "_lp_info[" << i << "].archived_reading = " << _lp_info[i].archived_reading << endl;
                dout << "_lp_info[" << i << "].current_schedule = " << _lp_info[i].current_schedule << endl;
            }
            */

            _lp_info[i].current_schedule = planned_time;

            //  if we've already made the request for a block, or we're overdue...  almost the same thing
            if( (_lp_info[i].current_request >= _lp_info[i].archived_reading) ||
                (planned_time <= (Now.seconds() - MCT_LPWindow)) )
            {
                unsigned int overdue_rate = getLPRetryRate(demand_rate);

                _lp_info[i].current_schedule  = (Now.seconds() - LPBlockEvacuationTime) + overdue_rate;
                _lp_info[i].current_schedule -= (Now.seconds() - LPBlockEvacuationTime) % overdue_rate;

                _lp_info[i].current_schedule += LPBlockEvacuationTime;
            }

            if( next_time > _lp_info[i].current_schedule )
            {
                next_time = _lp_info[i].current_schedule;
            }

            /*
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint - lp calctime check... **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "planned_time = " << planned_time << endl;
                dout << "_lp_info[" << i << "].archived_reading = " << _lp_info[i].archived_reading << endl;
                dout << "_lp_info[" << i << "].current_schedule = " << _lp_info[i].current_schedule << endl;
                dout << "next_time = " << next_time << endl;
            }
            */
        }
    }
/*
    if( getMCTDebugLevel(MCTDebug_LoadProfile) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << getName() << "'s next Load Profile request at " << RWTime(nextTime) << endl;
    }
*/
    return (_nextLPScanTime = next_time);
}


void CtiDeviceMCT470::sendIntervals( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList )
{
    // Load all the other stuff that is needed
    OutMessage->DeviceID  = getID();
    OutMessage->TargetID  = getID();
    OutMessage->Port      = getPortID();
    OutMessage->Remote    = getAddress();
    // 082002 CGP // OutMessage->RouteID   = getRouteID();
    OutMessage->TimeOut   = 2;
    OutMessage->Sequence  = Emetcon::PutConfig_Intervals;     // Helps us figure it out later!
    OutMessage->Retry     = 2;

    // Tell the porter side to complete the assembly of the message.
    OutMessage->Request.BuildIt = TRUE;
    strncpy(OutMessage->Request.CommandStr, "putconfig emetcon intervals", COMMAND_STR_SIZE );

    outList.insert(OutMessage);
    OutMessage = NULL;
}


INT CtiDeviceMCT470::calcAndInsertLPRequests(OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList)
{
    int nRet = NoError;

    RWTime         Now;
    unsigned int   demand_rate, block_size;
    int            channel, block;
    string         descriptor;
    OUTMESS       *tmpOutMess;

    demand_rate = getLoadProfile().getLoadProfileDemandRate();
    block_size  = demand_rate * 6;

    if( !_intervalsSent )
    {
        sendIntervals(OutMessage, outList);

        _intervalsSent = true;
    }
    else
    {
        for( int i = 0; i < MCT4XX_LPChannels; i++ )
        {
            if( useScanFlags() )
            {
                if( _lp_info[i].current_schedule <= Now )
                {
                    tmpOutMess = CTIDBG_new OUTMESS(*OutMessage);

                    _lp_info[i].current_request  = _lp_info[i].archived_reading;

                    //  make sure we only ask for what the function reads can access
                    if( (Now.seconds() - _lp_info[i].current_request) >= (unsigned long)(MCT4XX_LPRecentBlocks * block_size) )
                    {
                        //  go back as far as we can
                        _lp_info[i].current_request  = Now.seconds();
                        _lp_info[i].current_request -= MCT4XX_LPRecentBlocks * block_size;
                    }

                    if( getMCTDebugLevel(MCTDebug_LoadProfile) )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - LP variable check for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Now.seconds() = " << Now.seconds() << endl;
                        dout << "_lp_info[" << i << "].archived_reading = " << _lp_info[i].archived_reading << endl;
                        dout << "MCT4XX_LPRecentBlocks * block_size = " << MCT4XX_LPRecentBlocks * block_size << endl;
                        dout << "_lp_info[" << i << "].current_request = " << _lp_info[i].current_request << endl;
                    }

                    //  make sure we're aligned (note - rwEpoch is an even multiple of 86400, so no worries)
                    _lp_info[i].current_request -= _lp_info[i].current_request % block_size;

                    //  which block to grab?
                    channel = i + 1;
                    block   = (Now.seconds() - _lp_info[i].current_request) / block_size;

                    descriptor = " channel " + CtiNumStr(channel) + " block " + CtiNumStr(block);

                    strncat( tmpOutMess->Request.CommandStr,
                             descriptor.data(),
                             sizeof(tmpOutMess->Request.CommandStr) - strlen(tmpOutMess->Request.CommandStr));

                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS  );
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - command string check for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "\"" << tmpOutMess->Request.CommandStr << "\"" << endl;
                    }

                    outList.insert(tmpOutMess);
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


bool CtiDeviceMCT470::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
{
    bool retVal = false;
    int  address, block, channel;

    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint - LP parse value check **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "parse.getiValue(\"scan_loadprofile_block\",   0) = " << parse.getiValue("scan_loadprofile_block", 0) << endl;
        dout << "parse.getiValue(\"scan_loadprofile_channel\", 0) = " << parse.getiValue("scan_loadprofile_channel", 0) << endl;
    }

    block   = parse.getiValue("scan_loadprofile_block",   0);
    channel = parse.getiValue("scan_loadprofile_channel", 0);

    if( block && channel && block <= MCT4XX_LPRecentBlocks && channel <= MCT4XX_LPChannels )
    {
        channel--;
        block--;    //  adjust to be a zero-based offset

        address  = 0x50 + (0x10 * channel);
        address += block;

        OutMessage->Buffer.BSt.Function = address;
        OutMessage->Buffer.BSt.Length   = 13;  //  2 bytes per interval, and the table pointer
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


/*
 *  ResultDecode MUST decode all CtiDLCCommand_t which are defined in the initCommandStore object.  The only exception to this
 *  would be a child whose decode was identical to the parent, but whose request was done differently..
 *  This MAY be the case for example in an IED scan.
 */
INT CtiDeviceMCT470::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    switch(InMessage->Sequence)
    {
        case Emetcon::Scan_Accum:
        case Emetcon::GetValue_Default:
        {
            status = decodeGetValueKWH(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::Scan_Integrity:
        case Emetcon::GetValue_Demand:
        {
            status = decodeGetValueDemand(InMessage, TimeNow, vgList, retList, outList);
            break;
        }
/*
        case Emetcon::GetValue_PeakDemand:
        {
            status = decodeGetValuePeakDemand(InMessage, TimeNow, vgList, retList, outList);
            break;
        }
*/
        case Emetcon::GetValue_Outage:
        {
            status = decodeGetValueOutage(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetValue_IED:
        case Emetcon::GetValue_IEDDemand:
        {
            status = decodeGetValueIED(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetConfig_IEDTime:
        case Emetcon::GetConfig_IEDScan:
        {
            status = decodeGetConfigIED(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetValue_LoadProfile:
        {
            status = decodeGetValueLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::Scan_LoadProfile:
        {
            status = decodeScanLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetStatus_Internal:
        {
            status = decodeGetStatusInternal(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetStatus_LoadProfile:
        {
            status = decodeGetStatusLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetConfig_Disconnect:
        {
            status = decodeGetConfigDisconnect(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetConfig_Intervals:
        {
            status = decodeGetConfigIntervals(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::GetConfig_Model):
        {
            status = decodeGetConfigModel(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::GetConfig_Time):
        case (Emetcon::GetConfig_TSync):
        {
            //  ooo, but it works
            status = CtiDeviceMCT410::decodeGetConfigTime(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::GetValue_PFCount):
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


INT CtiDeviceMCT470::executeGetValue( CtiRequestMsg              *pReq,
                                      CtiCommandParser           &parse,
                                      OUTMESS                   *&OutMessage,
                                      RWTPtrSlist< CtiMessage >  &vgList,
                                      RWTPtrSlist< CtiMessage >  &retList,
                                      RWTPtrSlist< OUTMESS >     &outList )
{
    INT nRet = NoMethod;

    bool found = false;
    int function;

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

    if( parse.getFlags() & CMD_FLAG_GV_IED )  //  This parse has the token "IED" in it!
    {
        if( parse.getFlags() & CMD_FLAG_GV_DEMAND )
        {
            //  this will be for the real-time table
            function = Emetcon::GetValue_IEDDemand;
            found = getOperation( function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO );
        }
        // else if()  //  this is where the IED status would be handled
        else
        {
            function = Emetcon::GetValue_IED;

            found = getOperation( function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO );

            if( parse.getFlags() & CMD_FLAG_GV_RATET )
            {
                OutMessage->Buffer.BSt.Function = MCT470_FuncRead_IED_TOU_CurrentTotals;
            }
            else
            {
                if( parse.getFlags() & CMD_FLAG_GV_KVARH || parse.getFlags() & CMD_FLAG_GV_KVAH  )
                {
                    OutMessage->Buffer.BSt.Function = MCT470_FuncRead_IED_TOU_CurrentKMBase;
                }
                else
                {
                    OutMessage->Buffer.BSt.Function = MCT470_FuncRead_IED_TOU_CurrentKWBase;
                }

                if(      parse.getFlags() & CMD_FLAG_GV_RATEA )  OutMessage->Buffer.BSt.Function += 0;
                else if( parse.getFlags() & CMD_FLAG_GV_RATEB )  OutMessage->Buffer.BSt.Function += 1;
                else if( parse.getFlags() & CMD_FLAG_GV_RATEC )  OutMessage->Buffer.BSt.Function += 2;
                else if( parse.getFlags() & CMD_FLAG_GV_RATED )  OutMessage->Buffer.BSt.Function += 3;
            }

            if( parse.getFlags() & CMD_FLAG_FROZEN )
            {
                OutMessage->Buffer.BSt.Function += MCT470_FuncRead_IED_TOU_PreviousOffset;
            }
        }
    }
    else if( parse.isKeyValid("lp_command") )  //  kinda clunky...  the 400 series needs a base object
    {
        nRet = CtiDeviceMCT410::executeGetValue(pReq, parse, OutMessage, vgList, retList, outList);
    }
    else
    {
        nRet = CtiDeviceMCT::executeGetValue(pReq, parse, OutMessage, vgList, retList, outList);
    }

    if( found )
    {
        // Load all the other stuff that is needed
        //  FIXME:  most of this is taken care of in propagateRequest - we could probably trim a lot of this out
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString(), COMMAND_STR_SIZE);

        nRet = NoError;
    }

    return nRet;
}


INT CtiDeviceMCT470::executeGetConfig( CtiRequestMsg              *pReq,
                                       CtiCommandParser           &parse,
                                       OUTMESS                   *&OutMessage,
                                       RWTPtrSlist< CtiMessage >  &vgList,
                                       RWTPtrSlist< CtiMessage >  &retList,
                                       RWTPtrSlist< OUTMESS >     &outList )
{
    INT nRet = NoMethod;

    bool found = false;
    int function;

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

    if(parse.isKeyValid("ied"))
    {
        //  ACH:  add a check to ensure that we're reading from Precanned Table 1
        if(parse.isKeyValid("time"))
        {
            function = Emetcon::GetConfig_IEDTime;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
        else if( parse.isKeyValid("scan"))
        {
            function = Emetcon::GetConfig_IEDScan;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
    }
    else
    {
        nRet = CtiDeviceMCT::executeGetConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }

    if( found )
    {
        // Load all the other stuff that is needed
        //  FIXME:  most of this is taken care of in propagateRequest - we could probably trim a lot of this out
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString(), COMMAND_STR_SIZE);

        nRet = NoError;
    }

    return nRet;
}


INT CtiDeviceMCT470::decodeGetValueKWH(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;
    ULONG i,x;
    INT pid;
    RWCString resultString;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    unsigned long pulses;
    point_info_t  pi;

    CtiPointBase         *pPoint = NULL;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
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

        for( i = 0; i < MCT470_ChannelCount; i++ )
        {
            pPoint = getDevicePointOffsetTypeEqual( 1 + i, PulseAccumulatorPointType );

            point_info_t pi = getData(DSt->Message + (i * 3), 3, ValueType_Accumulator);

            // handle accumulator data here
            if( pPoint != NULL)
            {
                // 24 bit pulse value
                pi.value = ((CtiPointNumeric *)pPoint)->computeValueForUOM(pi.value);

                resultString = "";

                if( pi.quality != InvalidQuality )
                {
                    resultString += getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                    if( pData = makePointDataMsg(pPoint, pi, resultString) )
                    {
                        pData->setTime(RWTime::now() - (RWTime::now().seconds() % 300) );

                        ReturnMsg->PointData().insert(pData);
                        pData = NULL;  // We just put it on the list...
                    }
                }
                else
                {
                    resultString += ReturnMsg->ResultString();
                    resultString += getName() + " / " + pPoint->getName() + " = (invalid data)";

                    ReturnMsg->setResultString(resultString);
                }
            }
            else if( i == 0 )
            {
                if( pi.quality != InvalidQuality )
                {
                    resultString += getName() + " / KYZ 1 = " + CtiNumStr(pi.value) + "  --  POINT UNDEFINED IN DB";
                }
                else
                {
                    resultString += getName() + " / KYZ 1 = (invalid data) --  POINT UNDEFINED IN DB";
                }

                ReturnMsg->setResultString(resultString);
            }
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetValueDemand(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int          status = NORMAL, i;
    point_info_t pi;
    RWCString    resultString, stateName;

    INT ErrReturn = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    CtiPointBase    *pPoint = NULL;
    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;

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
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        for( i = 0; i < 8; i++ )
        {
            if( pPoint = getDevicePointOffsetTypeEqual(i + 1, StatusPointType) )
            {
                pi.value   = (DSt->Message[0] >> i) & 0x01;
                pi.quality = NormalQuality;

                stateName = ResolveStateName(pPoint->getStateGroupID(), pi.value);

                if( !stateName.isNull() )
                {
                    resultString += getName() + " / " + pPoint->getName() + ":" + stateName;
                }
                else
                {
                    resultString += getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value);
                }

                if(pData = makePointDataMsg(pPoint, pi, resultString))
                {
                    ReturnMsg->PointData().insert(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
        }

        for(int i = 0; i < 5; i++)
        {
            if( i == 4 )
            {
                pi = getData(DSt->Message + (i * 2) + 1, 2, ValueType_Raw);

                pPoint = getDevicePointOffsetTypeEqual(MCT_PointOffset_Accumulator_Powerfail, PulseAccumulatorPointType);
            }
            else
            {
                pi = getData(DSt->Message + (i * 2) + 1, 2, ValueType_KW);

                pPoint = getDevicePointOffsetTypeEqual(i + 1, DemandAccumulatorPointType);

                //  turn raw pulses into a demand reading
                pi.value *= double(3600 / getDemandInterval());
            }

            if( pPoint )
            {
                RWTime pointTime;

                pi.value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(pi.value);

                resultString += getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                if( pData = makePointDataMsg(pPoint, pi, resultString) )
                {
                    if( i != 4 )
                    {
                        pointTime -= pointTime.seconds() % getDemandInterval();
                        pData->setTime( pointTime );
                    }

                    ReturnMsg->PointData().insert(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            else if( i == 4 )
            {
                resultString  = ReturnMsg->ResultString();
                resultString += getName() + " / Blink Counter = " + CtiNumStr(pi.value) + "\n";
                ReturnMsg->setResultString(resultString);
            }
            else if( i == 0 )
            {
                resultString  = getName() + " / Channel 1 Demand = " + CtiNumStr(pi.value) + "  --  POINT UNDEFINED IN DB\n";
                ReturnMsg->setResultString(resultString);
            }
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}

/*
INT CtiDeviceMCT470::decodeGetValuePeakDemand(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int           status = NORMAL;
    unsigned long timeOfPeak;

    point_info_t  pi;
    RWCString     resultString;
    RWTime        tmpTime;

    INT ErrReturn = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

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

        pi = getData(DSt->Message, 2, ValueType_KW);

        timeOfPeak = DSt->Message[2] << 24 |
                     DSt->Message[3] << 16 |
                     DSt->Message[4] <<  8 |
                     DSt->Message[5];

        tmpTime = RWTime(timeOfPeak + rwEpoch);

        //  turn raw pulses into a demand reading
        pi.value *= 3600 / getDemandInterval();

        // look for first defined DEMAND accumulator
        if( pPoint = getDevicePointOffsetTypeEqual(1 + MCT_PeakOffset, DemandAccumulatorPointType) )
        {
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

        dp = getData(DSt->Message + 6, 3, ValueType_Accumulator);

        Value   = dp.first;
        quality = dp.second;

        pPoint = getDevicePointOffsetTypeEqual( 1, PulseAccumulatorPointType );

        if( pPoint != NULL)
        {
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
*/


INT CtiDeviceMCT470::decodeGetValueIED(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int             status = NORMAL,
                    frozen = 0,
                    offset = 0,
                    rate   = 0;
    point_info_t    pi;
    RWCString       point_string, resultString;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** IED Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    resetScanPending();  //  needed for dev_single...  sets this in initiateIntegrityScan
    setMCTScanPending(ScanRateIntegrity, false);

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

        //  should we archive non-frozen points?

        if( parse.getFlags() & CMD_FLAG_GV_DEMAND )
        {
            CtiPointBase *kw, *km;

            pi = getData(DSt->Message, 3, ValueType_Raw);

            if(kw = getDevicePointOffsetTypeEqual(MCT470_PointOffset_TotalKW, AnalogPointType))
            {
                pi.value = ((CtiPointNumeric*)kw)->computeValueForUOM(pi.value);

                point_string = getName() + " / " + kw->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)kw)->getPointUnits().getDecimalPlaces());

                ReturnMsg->PointData().insert(makePointDataMsg(kw, pi, point_string));
            }
            else
            {
                resultString += getName() + " / current KW = " + CtiNumStr(pi.value) + "\n";
            }

            pi = getData(DSt->Message + 3, 3, ValueType_Raw);

            if(km = getDevicePointOffsetTypeEqual(MCT470_PointOffset_TotalKM, AnalogPointType))
            {
                pi.value = ((CtiPointNumeric*)km)->computeValueForUOM(pi.value);

                point_string = getName() + " / " + km->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)km)->getPointUnits().getDecimalPlaces());

                ReturnMsg->PointData().insert(makePointDataMsg(km, pi, point_string));
            }
            else
            {
                resultString += getName() + " / current KM = " + CtiNumStr(pi.value) + "\n";
            }

            pi = getData(DSt->Message + 6, 2, ValueType_Raw);

            resultString += getName() + " / outage count: " + CtiNumStr(pi.value) + "\n";
        }
        else if( parse.getFlags() & CMD_FLAG_GV_RATET )
        {
            CtiPointBase *kwh, *kmh;

            pi = getData(DSt->Message, 5, ValueType_Raw);

            if(kwh = getDevicePointOffsetTypeEqual(MCT470_PointOffset_TotalKWH, AnalogPointType))
            {
                pi.value = ((CtiPointNumeric*)kwh)->computeValueForUOM(pi.value);

                point_string = getName() + " / " + kwh->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)kwh)->getPointUnits().getDecimalPlaces());

                ReturnMsg->PointData().insert(makePointDataMsg(kwh, pi, point_string));
            }
            else
            {
                resultString += getName() + " / KWH total = " + CtiNumStr(pi.value) + "\n";
            }

            pi = getData(DSt->Message + 5, 5, ValueType_Raw);

            if(kmh = getDevicePointOffsetTypeEqual(MCT470_PointOffset_TotalKMH, AnalogPointType))
            {
                pi.value = ((CtiPointNumeric*)kmh)->computeValueForUOM(pi.value);

                point_string = getName() + " / " + kmh->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)kmh)->getPointUnits().getDecimalPlaces());

                ReturnMsg->PointData().insert(makePointDataMsg(kmh, pi, point_string));
            }
            else
            {
                resultString += getName() + " / KMH total = " + CtiNumStr(pi.value) + "\n";
            }

            pi = getData(DSt->Message + 10, 2, ValueType_Raw);

            resultString += getName() + " / Average power factor since last freeze = " + CtiNumStr(pi.value) + "\n";
        }
        else
        {
            CtiPointBase *kwh, *kw;
            point_info_t time_info;
            RWTime peak_time;

            if( parse.getFlags() & CMD_FLAG_GV_KVARH || parse.getFlags() & CMD_FLAG_GV_KVAH  )
            {
                offset = MCT470_PointOffset_TOU_KMBase;
            }
            else
            {
                offset = MCT470_PointOffset_TOU_KWBase;
            }

            if(      parse.getFlags() & CMD_FLAG_GV_RATEA )  rate = 0;
            else if( parse.getFlags() & CMD_FLAG_GV_RATEB )  rate = 1;
            else if( parse.getFlags() & CMD_FLAG_GV_RATEC )  rate = 2;
            else if( parse.getFlags() & CMD_FLAG_GV_RATED )  rate = 3;

            pi = getData(DSt->Message, 5, ValueType_Raw);

            if(kwh = getDevicePointOffsetTypeEqual(offset + rate + 1, AnalogPointType))
            {
                pi.value = ((CtiPointNumeric*)kwh)->computeValueForUOM(pi.value);

                point_string = getName() + " / " + kwh->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)kwh)->getPointUnits().getDecimalPlaces());

                ReturnMsg->PointData().insert(makePointDataMsg(kwh, pi, point_string));
            }
            else
            {
                resultString += getName() + " / KWH rate " + RWCString((char)('A' + rate)) + " total = " + CtiNumStr(pi.value) + "\n";
            }

            pi        = getData(DSt->Message + 5, 3, ValueType_Raw);
            time_info = getData(DSt->Message + 8, 4, ValueType_Raw);
            peak_time = RWTime((unsigned long)time_info.value + rwEpoch);

            if(kw = getDevicePointOffsetTypeEqual(offset + rate, AnalogPointType))
            {
                CtiPointDataMsg *peak_msg;

                pi.value = ((CtiPointNumeric*)kw)->computeValueForUOM(pi.value);

                point_string  = getName() + " / " + kw->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)kw)->getPointUnits().getDecimalPlaces());
                point_string += " @ " + peak_time.asString() + "\n";

                peak_msg = makePointDataMsg(kw, pi, point_string);
                peak_msg->setTime(peak_time);

                ReturnMsg->PointData().insert(peak_msg);
            }
            else
            {
                resultString += getName() + " / KW rate " + RWCString((char)('A' + rate)) + " total = " + CtiNumStr(pi.value);

                resultString += " @ " + peak_time.asString() + "\n";
            }
        }

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetConfigIED(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int             status = NORMAL,
                    frozen = 0,
                    offset = 0,
                    rate   = 0;
    point_info_t    pi;
    PointQuality_t  quality;
    RWCString       point_string, resultString;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

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
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        //  should we archive non-frozen points?

        switch( InMessage->Sequence )
        {
            case Emetcon::GetConfig_IEDTime:
            {
                point_info_t pi_time  = getData(DSt->Message, 4, ValueType_Raw);
                RWTime       ied_time = RWTime((unsigned long)pi_time.value + rwEpoch);

                resultString += getName() + " / current time: " + ied_time.asString() + "\n";

                /*
                resultString = getName() + " / phase A: " + (DSt->Message[] & 0x02)?"present":"not present" + "\n";
                resultString = getName() + " / phase B: " + (DSt->Message[] & 0x04)?"present":"not present" + "\n";
                resultString = getName() + " / phase C: " + (DSt->Message[] & 0x08)?"present":"not present" + "\n";
                */

                pi = getData(DSt->Message + 5, 2, ValueType_Raw);

                resultString += getName() + " / demand reset count: " + CtiNumStr((int)pi.value) + "\n";

                pi_time  = getData(DSt->Message + 7, 4, ValueType_Raw);
                ied_time = RWTime((unsigned long)pi_time.value + rwEpoch);

                resultString += getName() + " / time of last reset: " + ied_time.asString() + "\n";

                pi = getData(DSt->Message + 11, 2, ValueType_Raw);

                resultString += getName() + " / outage count: " + CtiNumStr((int)pi.value) + "\n";

                break;
            }

            case Emetcon::GetConfig_IEDScan:
            {
                resultString += getName() + " / function not implemented yet\n";

                break;
            }
        }

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetValueLoadProfile(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    RWCString valReport, resultString;
    int interval_len, block_len, function, channel, badData;

    point_info_t  pi;
    unsigned long timeStamp, decode_time;


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

        channel      = _llpInterest.channel;
        decode_time  = _llpInterest.time + _llpInterest.offset;

        pPoint = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( channel + MCT_PointOffset_LoadProfileOffset, DemandAccumulatorPointType );

        for( int i = 0; i < 6; i++ )
        {
            //  this is where the block started...
            timeStamp  = decode_time + (interval_len * i);
            //  but we want interval *ending* times, so add on one more interval
            timeStamp += interval_len;

            pi = getData(DSt->Message + (i * 2) + 1, 2, ValueType_KW);

            pi.value *= 3600 / interval_len;

            if( pPoint != NULL )
            {
                if( pi.quality != InvalidQuality )
                {
                    pi.value = pPoint->computeValueForUOM(pi.value);

                    valReport = getName() + " / " + pPoint->getName() + " @ " + RWTime(timeStamp).asString() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                    if( pData = makePointDataMsg(pPoint, pi, valReport) )
                    {
                        pData->setTime(timeStamp);

                        ReturnMsg->insert(pData);
                    }
                }
                else
                {
                    resultString += getName() + " / " + pPoint->getName() + " @ " + RWTime(timeStamp).asString() + " = (invalid data)\n";
                }
            }
            else
            {
                if( pi.quality == NormalQuality )
                {
                    resultString += getName() + " / LP channel " + CtiNumStr(channel) + " @ " + RWTime(timeStamp).asString() + " = " + CtiNumStr(pi.value, 0) + "\n";
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


INT CtiDeviceMCT470::decodeScanLoadProfile(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    string         val_report;
    int            channel, block, interval_len;
    unsigned long  timestamp, pulses;
    point_info_t   pi;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    CtiPointNumeric *point   = 0;
    CtiReturnMsg    *ret_msg = 0;  // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pdata   = 0;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
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

        if((ret_msg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ret_msg->setUserMessageId(InMessage->Return.UserID);

        if( (channel = parse.getiValue("scan_loadprofile_channel", 0)) &&
            (block   = parse.getiValue("scan_loadprofile_block",   0)) )
        {
            interval_len = getLoadProfile().getLoadProfileDemandRate();

            point = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( channel + MCT_PointOffset_LoadProfileOffset, DemandAccumulatorPointType );

            if( point )
            {
                //  this is where the block started...
                timestamp  = TimeNow.seconds();
                timestamp -= interval_len * 6 * block;
                timestamp -= timestamp % (interval_len * 6);

                if( timestamp == _lp_info[channel - 1].current_request )
                {
                    for( int offset = 5; offset >= 0; offset-- )
                    {
                        pi = getData(DSt->Message + offset*2 + 1, 2, ValueType_KW);

                        //  adjust for the demand interval
                        pi.value *= 3600 / interval_len;

                        //  compute for the UOM
                        pi.value = point->computeValueForUOM(pi.value);

                        if( pdata = makePointDataMsg(point, pi, "") )
                        {
                            pdata->setTags(TAG_POINT_LOAD_PROFILE_DATA);

                            //  the data goes from latest to earliest...  it's kind of backwards
                            pdata->setTime(timestamp + interval_len * (6 - offset));

                            ret_msg->insert(pdata);
                        }
                    }

                    //  insert a point data message for TDC and the like
                    //    note that timeStamp, pointQuality, and Value are set in the final iteration of the above for loop
                    val_report = getName() + " / " + point->getName() + " = " + CtiNumStr(pi.value,
                                                                                          ((CtiPointNumeric *)point)->getPointUnits().getDecimalPlaces());
                    if( pdata = makePointDataMsg(point, pi, val_report.c_str()) )
                    {
                        pdata->setTime(timestamp + interval_len * 6);
                        ret_msg->insert(pdata);
                    }

                    setLastLPTime (timestamp + interval_len * 6);
                    _lp_info[channel - 1].archived_reading = timestamp + interval_len * 6;
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - possible LP logic error for device \"" << getName() << "\";  calculated timestamp=" << RWTime(timestamp) << "; current_request=" << RWTime(_lp_info[channel - 1].current_request) << endl;
                        dout << "commandstr = " << InMessage->Return.CommandStr << endl;
                    }
                }
            }
            else
            {
                ret_msg->setResultString("No load profile point defined for '" + getName() + "'");
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - missing scan_loadprofile token in decodeScanLoadProfile for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            ret_msg->setResultString("Malformed LP command string for '" + getName() + "'");
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ret_msg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetStatusInternal( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
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

        resultString += getName() + " / Internal Status:\n";

        resultString += (InMessage->Buffer.DSt.Message[0] & 0x01)?"Group addressing disabled\n":"Group addressing enabled\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x04)?"DST active\n":"DST inactive\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x08)?"Holiday active\n":"";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x10)?"TOU disabled\n":"TOU enabled\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x20)?"Time sync needed\n":"In time sync\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x40)?"Critical peak active\n":"Critical peak inactive\n";

        resultString += (InMessage->Buffer.DSt.Message[1] & 0x01)?"Power fail occurred\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x02)?"Electronic meter communication error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x08)?"Power fail carryover\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x10)?"RTC adjusted\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x20)?"Holiday Event occurred\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x40)?"DST Change occurred\n":"";

        resultString += (InMessage->Buffer.DSt.Message[2] & 0x01)?"Zero usage on channel 1 for 24 hours\n":"";
        resultString += (InMessage->Buffer.DSt.Message[2] & 0x02)?"Zero usage on channel 2 for 24 hours\n":"";
        resultString += (InMessage->Buffer.DSt.Message[2] & 0x04)?"Zero usage on channel 3 for 24 hours\n":"";
        resultString += (InMessage->Buffer.DSt.Message[2] & 0x08)?"Zero usage on channel 4 for 24 hours\n":"";

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetStatusLoadProfile( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT status = NORMAL, lp_channel;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    unsigned char *geneBuf = InMessage->Buffer.DSt.Message;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        lp_channel = (parse.getiValue("loadprofile_offset") < 3)?1:3;

        RWCString resultString;
        unsigned long tmpTime;
        RWTime lpTime;

        CtiReturnMsg     *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg  *pData = NULL;

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        resultString += getName() + " / Load Profile Channel " + CtiNumStr(lp_channel) + " Status:\n";

        tmpTime = DSt->Message[0] << 24 |
                  DSt->Message[1] << 16 |
                  DSt->Message[2] <<  8 |
                  DSt->Message[3];

        lpTime = RWTime(tmpTime + rwEpoch);

        resultString += "Current Interval Time: " + lpTime.asString() + "\n";
        resultString += "Current Interval Pointer: " + CtiNumStr(DSt->Message[4]) + "\n";

        resultString += "\n";

        resultString += getName() + " / Load Profile Channel " + CtiNumStr(lp_channel + 1) + " Status:\n";

        tmpTime = DSt->Message[5] << 24 |
                  DSt->Message[6] << 16 |
                  DSt->Message[7] <<  8 |
                  DSt->Message[8];

        lpTime = RWTime(tmpTime + rwEpoch);

        resultString += "Current Interval Time: " + lpTime.asString() + "\n";
        resultString += "Current Interval Pointer: " + CtiNumStr(DSt->Message[9]) + "\n";

        resultString += "\n";

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetConfigIntervals(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        RWCString resultString;

        resultString += getName() + " / Demand Interval: ";

        if( DSt->Message[0] )
        {
            resultString += CtiNumStr(DSt->Message[0]) + " minutes\n";
        }
        else
        {
            resultString += "disabled\n";
        }

        resultString += getName() + " / Load Profile Interval 1: " + CtiNumStr(DSt->Message[1]) + " minutes\n";
        resultString += getName() + " / Load Profile Interval 2: " + CtiNumStr(DSt->Message[2]) + " minutes\n";
        resultString += getName() + " / Table Read Interval: " + CtiNumStr(DSt->Message[3] * 15) + " seconds\n";

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


INT CtiDeviceMCT470::decodeGetConfigModel(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
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

        rev  = ('A' - 1) + InMessage->Buffer.DSt.Message[1];

        sspec = "\nSoftware Specification " + CtiNumStr(ssp) + "  Rom Revision " + RWCString(rev) + "\n";

        options += "Connected Meter: ";
        switch( InMessage->Buffer.DSt.Message[3] & 0xf0 )
        {
            case 0x00:  options += "None\n";                break;
            case 0x10:  options += "Landis and Gyr S4\n";   break;
            case 0x20:  options += "Alpha A1\n";            break;
            case 0x30:  options += "Alpha Power Plus\n";    break;
            case 0x40:  options += "GE kV\n";               break;
            case 0x50:  options += "GE kV2\n";              break;
            default:    options += "Unknown (" + CtiNumStr((int)InMessage->Buffer.DSt.Message[3]).xhex().zpad(2) + ")\n";   break;
        }

        if( InMessage->Buffer.DSt.Message[3] & 0x04 )
        {
            options += "Multiple electronic meters attached\n";
        }

        options += "DST " + (InMessage->Buffer.DSt.Message[3] & 0x04)?"enabled\n":"disabled\n";

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


void CtiDeviceMCT470::DecodeDatabaseReader(RWDBReader &rdr)
{
    INT iTemp;
    RWDBNullIndicator isNull;

    Inherited::DecodeDatabaseReader(rdr);

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

