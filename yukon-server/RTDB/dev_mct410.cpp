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
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2004/10/26 21:18:03 $
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
#include <string>

using namespace std;

CtiDeviceMCT410::DLCCommandSet CtiDeviceMCT410::_commandStore   = CtiDeviceMCT410::initCommandStore();
CtiDeviceMCT410::QualityMap    CtiDeviceMCT410::_errorQualities = CtiDeviceMCT410::initErrorQualities();

CtiDeviceMCT410::CtiDeviceMCT410( ) :
    _intervalsSent(false)
{
    _llpInterest.time    = 0;
    _llpInterest.channel = 0;
    _llpInterest.offset  = 0;

    for( int i = 0; i < MCT4XX_LPChannels; i++ )
    {
        //  initialize them to 0
        _lp_info[i].archived_reading = 0;
        _lp_info[i].current_request  = 0;
        _lp_info[i].current_schedule = 0;
    }
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


CtiDeviceMCT410::DLCCommandSet CtiDeviceMCT410::initCommandStore( )
{
    CtiDLCCommandStore cs;
    DLCCommandSet s;

    cs._cmd     = CtiProtocolEmetcon::Scan_Accum;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair( (int)MCT410_FuncReadMReadPos,
                             (int)MCT410_FuncReadMReadLen );
    s.insert(cs);

    cs._cmd     = CtiProtocolEmetcon::GetValue_Default;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair( (int)MCT410_FuncReadMReadPos,
                             (int)MCT410_FuncReadMReadLen );
    s.insert(cs);

    cs._cmd      = CtiProtocolEmetcon::Scan_Integrity;
    cs._io       = IO_FCT_READ;
    cs._funcLen  = make_pair( (int)MCT410_FuncReadDemandPos,
                              (int)MCT410_FuncReadDemandLen );
    s.insert(cs);

    cs._cmd      = CtiProtocolEmetcon::Scan_LoadProfile;
    cs._io       = IO_FCT_READ;
    cs._funcLen  = make_pair(0, 0);
    s.insert( cs );

    cs._cmd      = CtiProtocolEmetcon::GetValue_Demand;
    cs._io       = IO_FCT_READ;
    cs._funcLen  = make_pair( (int)MCT410_FuncReadDemandPos,
                              (int)MCT410_FuncReadDemandLen );
    s.insert(cs);

    cs._cmd      = CtiProtocolEmetcon::GetValue_PeakDemand;
    cs._io       = IO_FCT_READ;
    cs._funcLen  = make_pair( (int)MCT410_FuncReadPeakDemandPos,
                              (int)MCT410_FuncReadPeakDemandLen );
    s.insert(cs);

    cs._cmd      = CtiProtocolEmetcon::GetValue_Voltage;
    cs._io       = IO_FCT_READ;
    cs._funcLen  = make_pair( (int)MCT410_FuncReadVoltagePos,
                              (int)MCT410_FuncReadVoltageLen );
    s.insert(cs);

    cs._cmd      = CtiProtocolEmetcon::GetValue_Outage;
    cs._io       = IO_FCT_READ;
    cs._funcLen  = make_pair( (int)MCT410_FuncReadOutagePos,
                              (int)MCT410_FuncReadOutageLen );
    s.insert(cs);

    cs._cmd     = CtiProtocolEmetcon::GetStatus_Internal;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT410_StatusLen,
                             (int)MCT410_StatusPos );
    s.insert(cs);

    cs._cmd     = CtiProtocolEmetcon::GetStatus_LoadProfile;
    cs._io      = IO_FCT_READ;
    cs._funcLen = make_pair( (int)MCT410_FuncReadLPStatusPos,
                             (int)MCT410_FuncReadLPStatusLen );
    s.insert(cs);

    cs._cmd     = CtiProtocolEmetcon::PutConfig_Raw;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair( 0, 0 );  //  filled in later
    s.insert(cs);

    cs._cmd     = CtiProtocolEmetcon::PutConfig_TSync;
    cs._io      = IO_FCT_WRITE;
    cs._funcLen = make_pair( (int)MCT410_FuncWriteTSyncPos,
                             (int)MCT410_FuncWriteTSyncLen );
    s.insert(cs);

    cs._cmd     = CtiProtocolEmetcon::GetConfig_TSync;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT410_LastTSyncPos,
                             (int)MCT410_LastTSyncLen );
    s.insert(cs);

    cs._cmd     = CtiProtocolEmetcon::GetConfig_Time;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT410_RTCPos,
                             (int)MCT410_RTCLen );
    s.insert(cs);

    cs._cmd     = CtiProtocolEmetcon::PutConfig_Intervals;
    cs._io      = IO_FCT_WRITE;
    cs._funcLen = make_pair((int)MCT410_FuncWriteIntervalsPos,
                            (int)MCT410_FuncWriteIntervalsLen);
    s.insert(cs);

    cs._cmd     = CtiProtocolEmetcon::GetConfig_Intervals;
    cs._io      = IO_READ;
    cs._funcLen = make_pair((int)MCT410_IntervalsPos,
                            (int)MCT410_IntervalsLen);
    s.insert(cs);

    cs._cmd     = CtiProtocolEmetcon::GetValue_PFCount;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT410_PowerfailCountPos,
                             (int)MCT410_PowerfailCountLen );
    s.insert(cs);

    cs._cmd     = CtiProtocolEmetcon::PutValue_ResetPFCount;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT4XX_CommandPowerfailReset, 0);
    s.insert(cs);

    cs._cmd     = CtiProtocolEmetcon::PutStatus_Reset;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT4XX_CommandReset, 0);
    s.insert(cs);

    cs._cmd     = CtiProtocolEmetcon::PutStatus_FreezeOne;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT4XX_FreezeOne, 0);
    s.insert(cs);

    cs._cmd     = CtiProtocolEmetcon::PutStatus_FreezeTwo;
    cs._io      = IO_WRITE;
    cs._funcLen = make_pair((int)MCT4XX_FreezeTwo, 0);
    s.insert(cs);

    return s;
}


CtiDeviceMCT410::QualityMap CtiDeviceMCT410::initErrorQualities( void )
{
    QualityMap m;

    //  Meter Communications Problem
    m.insert(make_pair(0xFFFFFFFF, make_pair(InvalidQuality, 0)));
    m.insert(make_pair(0xFFFFFFFE, make_pair(InvalidQuality, EC_MeterReading  |
                                                             EC_DemandReading |
                                                             EC_LoadProfile)));

    //  No Data Yet Available
    m.insert(make_pair(0xFFFFFFFD, make_pair(InvalidQuality, EC_TOUFrozenDemand)));
    m.insert(make_pair(0xFFFFFFFC, make_pair(InvalidQuality, EC_MeterReading    |
                                                             EC_DemandReading   |
                                                             EC_TOUDemand       |
                                                             EC_TOUFrozenDemand |
                                                             EC_LoadProfile)));

    //  Data not available for interval
    m.insert(make_pair(0xFFFFFFFB, make_pair(InvalidQuality, 0)));
    m.insert(make_pair(0xFFFFFFFA, make_pair(InvalidQuality, EC_LoadProfile)));

    //  Device Filler
    m.insert(make_pair(0xFFFFFFF9, make_pair(DeviceFillerQuality, 0)));
    m.insert(make_pair(0xFFFFFFF8, make_pair(DeviceFillerQuality, EC_LoadProfile)));

    //  Power failure occurred during interval
    m.insert(make_pair(0xFFFFFFF7, make_pair(PowerfailQuality, 0)));
    m.insert(make_pair(0xFFFFFFF6, make_pair(PowerfailQuality, EC_LoadProfile)));

    //  Power restored in this interval
    m.insert(make_pair(0xFFFFFFF5, make_pair(PartialIntervalQuality, 0)));
    m.insert(make_pair(0xFFFFFFF4, make_pair(PartialIntervalQuality, EC_LoadProfile)));

    //  Overflow
    m.insert(make_pair(0xFFFFFFE1, make_pair(OverflowQuality, EC_TOUFrozenDemand)));
    m.insert(make_pair(0xFFFFFFE0, make_pair(OverflowQuality, EC_DemandReading   |
                                                              EC_TOUDemand       |
                                                              EC_TOUFrozenDemand |
                                                              EC_LoadProfile)));

    return m;
}



bool CtiDeviceMCT410::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;
    /*
    if( _commandStore.empty( ) )  // Must initialize!
    {
        CtiDeviceMCT410::initCommandStore( );
    }
    */
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


ULONG CtiDeviceMCT410::calcNextLPScanTime( void )
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
            CtiPointBase *pPoint = getDevicePointOffsetTypeEqual((i+1) + OFFSET_LOADPROFILE_OFFSET, DemandAccumulatorPointType);

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

            //  if we're still on schedule for our normal request - and we haven't already made the request
            if( (_lp_info[i].current_schedule != planned_time) &&
                (planned_time > (Now.seconds() - MCT_LPWindow)) )
            {
                _lp_info[i].current_schedule = planned_time;
            }
            //  we're overdue
            else
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

#if 0
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << getName() << "'s next Load Profile request at " << RWTime(nextTime) << endl;
    }
#endif

    return (_nextLPScanTime = next_time);
}


void CtiDeviceMCT410::sendIntervals( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList )
{
    // Load all the other stuff that is needed
    OutMessage->DeviceID  = getID();
    OutMessage->TargetID  = getID();
    OutMessage->Port      = getPortID();
    OutMessage->Remote    = getAddress();
    // 082002 CGP // OutMessage->RouteID   = getRouteID();
    OutMessage->TimeOut   = 2;
    OutMessage->Sequence  = CtiProtocolEmetcon::PutConfig_Intervals;     // Helps us figure it out later!
    OutMessage->Retry     = 2;

    // Tell the porter side to complete the assembly of the message.
    OutMessage->Request.BuildIt = TRUE;
    strncpy(OutMessage->Request.CommandStr, "putconfig emetcon intervals", COMMAND_STR_SIZE );

    outList.insert(OutMessage);
    OutMessage = NULL;
}


INT CtiDeviceMCT410::calcAndInsertLPRequests(OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList)
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


bool CtiDeviceMCT410::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
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
        OutMessage->Buffer.BSt.IO       = IO_FCT_READ;

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


CtiDeviceMCT410::data_pair CtiDeviceMCT410::getData( unsigned char *buf, int len, ValueType vt )
{
    PointQuality_t quality   = NormalQuality;
    unsigned long  value     = 0,
                   errorcode = 0xffffffff;
    unsigned char  demandbits, resolution;
    data_pair retval;

    for( int i = 0; i < len; i++ )
    {
        //  we're pulling the bytes in MSB-wise
        errorcode <<= 8;
        value     <<= 8;

        if( vt == ValueType_KW && !i )
        {
            //  save these for use later
            demandbits  = buf[i] & 0xc0;

            //  if we fill in the demand bits in the error code, we can run it through
            //    the same switch statement as anything else
            errorcode  |= buf[i] | 0xc0;
            resolution  = buf[i] & 0x30;
            value      |= buf[i] & 0x0f;
        }
        else if( vt == ValueType_Voltage && !i )
        {
            errorcode  |= buf[i] | 0x80;
            value      |= buf[i] & 0x7f;
        }
        else
        {
            errorcode  |= buf[i];
            value      |= buf[i];
        }
    }

    //  i s'pose this could be a set sometime, eh?
    QualityMap::iterator q_itr = _errorQualities.find(errorcode);

    if( q_itr != _errorQualities.end() )
    {
        quality = q_itr->second.first;
        value   = 0;
    }
    else
    {
        //  only take the demand bits into account if everything else is cool
        switch( demandbits )
        {
            //  time was adjusted in this interval
            case 0xc0:  quality = PartialIntervalQuality;    break;
            //  power was restored in this interval
            case 0x80:  quality = PartialIntervalQuality;    break;
            //  power failed in this interval
            case 0x40:  quality = PowerfailQuality;          break;
        }

        if( vt == ValueType_Voltage && value > 0x7fff )
        {
            value = 0;
            quality = AbnormalQuality;
        }
    }

    retval.first  = value;
    retval.second = quality;

    if( vt == ValueType_KW )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - demand value " << value << " resolution " << resolution << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }


        switch( resolution )
        {
            default:
            case 0x00:  retval.first /=    10.0;    break;  //  100 WH units -> kWH
            case 0x10:  retval.first /=   100.0;    break;  //   10 WH units -> kWH
            case 0x20:  retval.first /=  1000.0;    break;  //    1 WH units -> kWH
            case 0x30:  retval.first /= 10000.0;    break;  //  0.1 WH units -> kWH
        }

        retval.first *= 10.0;  //  REMOVE THIS for HEAD or WHATEVER build HAS the proper, pretty, nice 1.0 kWH output code
    }

    return retval;
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
        relative_time = request_time - _llpInterest.time;

        if( (request_channel == _llpInterest.channel) &&  //  correct channel
            (relative_time < (8 * block_len))         &&  //  within 8 blocks
            !(relative_time % block_len) )                //  aligned
        {
            //  it's aligned (and close enough) to the block we're pointing at
            function  = 0x40;
            function += relative_time / block_len;

            _llpInterest.offset = relative_time;
        }
        else
        {
            //  just read the first block - it'll be the one we're pointing at
            function  = 0x40;

            //  we need to set it to the requested interval
            CtiOutMessage *interest_om = new CtiOutMessage(*OutMessage);

            if( interest_om )
            {
                _llpInterest.time    = request_time;
                _llpInterest.offset  = 0;
                _llpInterest.channel = request_channel;

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

        data_pair dp = getData(DSt->Message, 3, ValueType_Accumulator);

        // handle accumulator data here
        if( pPoint != NULL)
        {
            // 24 bit pulse value
            Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM(dp.first);
            quality = dp.second;
            RWTime pointTime;

            if( quality != InvalidQuality )
            {
                resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

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
                resultString = getName() + " / " + pPoint->getName() + " = (invalid data)";

                ReturnMsg->setResultString(resultString);
            }
        }
        else
        {
            resultString = getName() + " / KYZ 1 = " + CtiNumStr(dp.first) + "  --  POINT UNDEFINED IN DB";
            ReturnMsg->setResultString(resultString);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValueDemand(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int             status = NORMAL;
    data_pair       dp;
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

        dp = getData(DSt->Message, 2, ValueType_KW);

        //  turn raw pulses into a demand reading
        Value   = dp.first * double(3600 / getDemandInterval());
        quality = dp.second;

        // look for first defined DEMAND accumulator
        pPoint = getDevicePointOffsetTypeEqual( 1, DemandAccumulatorPointType );

        if( pPoint != NULL)
        {
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

        dp = getData(DSt->Message + 2, 2, ValueType_Voltage);

        Value   = dp.first;
        quality = dp.second;

        pPoint = getDevicePointOffsetTypeEqual( MCT4XX_VoltageOffset, DemandAccumulatorPointType );

        if( pPoint != NULL)
        {
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

        dp = getData(DSt->Message + 4, 2, ValueType_Raw);

        Value   = dp.first;
        quality = dp.second;

        pPoint = getDevicePointOffsetTypeEqual( MCT_PointOffset_Accumulator_Powerfail, PulseAccumulatorPointType );

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
    data_pair       dp;
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

        dp = getData(DSt->Message, 2, ValueType_KW);

        Value   = dp.first;
        quality = dp.second;

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


INT CtiDeviceMCT410::decodeGetValueVoltage( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    unsigned char *geneBuf = InMessage->Buffer.DSt.Message;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;
    PointQuality_t quality;
    data_pair      dp;

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

        dp = getData(DSt->Message, 2, ValueType_Voltage);
        maxVolts = dp.first;
        quality  = dp.second;

        tmpTime = DSt->Message[2] << 24 |
                  DSt->Message[3] << 16 |
                  DSt->Message[4] <<  8 |
                  DSt->Message[5];

        maxTime = RWTime(tmpTime + rwEpoch);

        dp = getData(DSt->Message + 6, 2, ValueType_Voltage);
        minVolts = dp.first;
        quality  = dp.second;

        tmpTime = DSt->Message[8]  << 24 |
                  DSt->Message[9]  << 16 |
                  DSt->Message[10] <<  8 |
                  DSt->Message[11];

        minTime = RWTime(tmpTime + rwEpoch);

        CtiPoint *pPoint = getDevicePointOffsetTypeEqual( MCT4XX_VoltageOffset, DemandAccumulatorPointType );

        if( pPoint != NULL)
        {
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
    data_pair dp;
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

        channel      = _llpInterest.channel;
        decode_time  = _llpInterest.time + _llpInterest.offset;

        pPoint = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( channel + OFFSET_LOADPROFILE_OFFSET, DemandAccumulatorPointType );

        for( int i = 0; i < 6; i++ )
        {
            //  this is where the block started...
            timeStamp  = decode_time + (interval_len * i);
            //  but we want interval *ending* times, so add on one more interval
            timeStamp += interval_len;

            dp = getData(DSt->Message + (i * 2) + 1, 2, ValueType_Voltage);
            pulses  = dp.first;
            quality = dp.second;

            Value = pulses;

            if( channel != MCT4XX_LPVoltageChannel )
            {
                Value *= 3600 / interval_len;
            }

            if( pPoint != NULL )
            {
                if( quality != InvalidQuality )
                {
                    Value = pPoint->computeValueForUOM( Value );

                    valReport = getName() + " / " + pPoint->getName() + " @ " + RWTime(timeStamp).asString() + " = " + CtiNumStr(Value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                    pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(),
                                                       Value,
                                                       quality,
                                                       DemandAccumulatorPointType,
                                                       valReport);

                    pData->setTime(timeStamp);

                    ReturnMsg->insert(pData);
                }
                else
                {
                    resultString += getName() + " / " + pPoint->getName() + " @ " + RWTime(timeStamp).asString() + " = (invalid data)\n";
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

    string         val_report;
    int            channel, block, interval_len;
    double         value;
    unsigned long  timestamp, pulses;
    data_pair      dp;
    PointQuality_t quality;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    CtiPointNumeric *point   = 0;
    CtiReturnMsg    *ret_msg = 0;  // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pdata   = 0;

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

            point = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( channel + OFFSET_LOADPROFILE_OFFSET, DemandAccumulatorPointType );

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
                        if( channel == MCT4XX_LPVoltageChannel )
                        {
                            dp = getData(DSt->Message + offset*2 + 1, 2, ValueType_Voltage);

                            value   = dp.first;
                            quality = dp.second;
                        }
                        else
                        {
                            dp = getData(DSt->Message + offset*2 + 1, 2, ValueType_KW);

                            //  adjust for the demand interval
                            value   = dp.first * 3600 / interval_len;
                            quality = dp.second;
                        }

                        //  compute for the UOM
                        value = point->computeValueForUOM( value );

                        pdata = CTIDBG_new CtiPointDataMsg(point->getPointID(),
                                                           value,
                                                           quality,
                                                           DemandAccumulatorPointType,
                                                           "",
                                                           TAG_POINT_LOAD_PROFILE_DATA );

                        if( pdata )
                        {
                            //  the data goes from latest to earliest...  it's kind of backwards
                            pdata->setTime(timestamp + interval_len * (6 - offset));

                            ret_msg->insert(pdata);
                        }
                    }

                    //  insert a point data message for TDC and the like
                    //    note that timeStamp, pointQuality, and Value are set in the final iteration of the above for loop
                    val_report = getName() + " / " + point->getName() + " = " + CtiNumStr(value,
                                                                                          ((CtiPointNumeric *)point)->getPointUnits().getDecimalPlaces());
                    pdata = CTIDBG_new CtiPointDataMsg(point->getPointID(),
                                                       value,
                                                       quality,
                                                       DemandAccumulatorPointType,
                                                       val_report.data() );
                    pdata->setTime(timestamp + interval_len * 6);
                    setLastLPTime (timestamp + interval_len * 6);
                    ret_msg->insert( pdata );

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

