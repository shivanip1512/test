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
* REVISION     :  $Revision: 1.30 $
* DATE         :  $Date: 2005/04/21 20:49:03 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include "device.h"
#include "devicetypes.h"
#include "dev_mct410.h"
#include "logger.h"
#include "mgr_point.h"
#include "numstr.h"
#include "porter.h"
#include "portglob.h"
#include "utility.h"
#include <string>

using namespace std;
using Cti::Protocol::Emetcon;


const CtiDeviceMCT410::DLCCommandSet CtiDeviceMCT410::_commandStore   = CtiDeviceMCT410::initCommandStore();
const CtiDeviceMCT410::QualityMap    CtiDeviceMCT410::_errorQualities = CtiDeviceMCT410::initErrorQualities();

CtiDeviceMCT410::CtiDeviceMCT410( ) :
    _intervalsSent(false),
    _sspec(0),
    _rev(0)
{
    _llpInterest.time    = 0;
    _llpInterest.channel = 0;
    _llpInterest.offset  = 0;

    _llpPeakInterest.channel = 0;
    _llpPeakInterest.command = 0;
    _llpPeakInterest.period  = 0;
    _llpPeakInterest.time    = 0;

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

CtiDeviceMCT410::~CtiDeviceMCT410( )
{
}

CtiDeviceMCT410 &CtiDeviceMCT410::operator=( const CtiDeviceMCT410 &aRef )
{
    if( this != &aRef )
    {
        Inherited::operator=( aRef );
    }

    return *this;
}


//  eventually allow this function to construct the pointdata string as well
CtiPointDataMsg *CtiDeviceMCT410::makePointDataMsg(CtiPoint *p, const point_info_t &pi, const RWCString &pointString)
{
    CtiPointDataMsg *pdm = 0;

    if( p )
    {
        pdm = CTIDBG_new CtiPointDataMsg(p->getID(), pi.value, pi.quality, p->getType(), pointString);
    }

    return pdm;
}

void CtiDeviceMCT410::setDisconnectAddress( unsigned long address )
{
    _disconnectAddress = address;
}


CtiDeviceMCT410::DLCCommandSet CtiDeviceMCT410::initCommandStore( )
{
    CtiDLCCommandStore cs;
    DLCCommandSet s;

    cs._cmd     = Emetcon::Scan_Accum;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)FuncRead_MReadPos,
                            (int)FuncRead_MReadLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_Default;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)FuncRead_MReadPos,
                            (int)FuncRead_MReadLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_FrozenKWH;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)FuncRead_FrozenMReadPos,
                            (int)FuncRead_FrozenMReadLen);
    s.insert(cs);

    cs._cmd     = Emetcon::Scan_Integrity;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)FuncRead_DemandPos,
                            (int)FuncRead_DemandLen);
    s.insert(cs);

    cs._cmd     = Emetcon::Scan_LoadProfile;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair(0, 0);
    s.insert( cs );

    cs._cmd     = Emetcon::GetValue_LoadProfile;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair(0, 0);
    s.insert( cs );

    cs._cmd     = Emetcon::GetValue_LoadProfilePeakReport;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair(0, 0);
    s.insert( cs );

    cs._cmd     = Emetcon::GetValue_Demand;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)FuncRead_DemandPos,
                            (int)FuncRead_DemandLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_PeakDemand;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)FuncRead_PeakDemandPos,
                            (int)FuncRead_PeakDemandLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_FrozenKWH;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)FuncRead_FrozenPos,
                            (int)FuncRead_FrozenLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_Voltage;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)FuncRead_VoltagePos,
                            (int)FuncRead_VoltageLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_FrozenVoltage;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)FuncRead_FrozenVoltagePos,
                            (int)FuncRead_FrozenVoltageLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_Outage;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)FuncRead_OutagePos,
                            (int)FuncRead_OutageLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetStatus_Internal;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair((int)Memory_StatusLen,
                            (int)Memory_StatusPos);
    s.insert(cs);

    cs._cmd     = Emetcon::GetStatus_LoadProfile;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)FuncRead_LPStatusPos,
                            (int)FuncRead_LPStatusLen);
    s.insert(cs);

    //  These need to be duplicated from DeviceMCT because the 400 doesn't need the ARML.
    cs._cmd     = Emetcon::Control_Close;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair((int)Command_Connect, 0);
    s.insert( cs );

    cs._cmd     = Emetcon::Control_Open;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair((int)Command_Disconnect, 0);
    s.insert( cs );

    cs._cmd     = Emetcon::Control_Conn;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair((int)Command_Connect, 0);
    s.insert( cs );

    cs._cmd     = Emetcon::Control_Disc;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair((int)Command_Disconnect, 0);
    s.insert( cs );

    cs._cmd     = Emetcon::GetStatus_Disconnect;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)FuncRead_DisconnectStatusPos,
                            (int)FuncRead_DisconnectStatusLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_Disconnect;
    cs._io      = Emetcon::IO_Function_Read;
    cs._funcLen = make_pair((int)FuncRead_DisconnectConfigPos,
                            (int)FuncRead_DisconnectConfigLen);
    s.insert(cs);

    cs._cmd     = Emetcon::PutConfig_Disconnect;
    cs._io      = Emetcon::IO_Function_Write;
    cs._funcLen = make_pair((int)FuncWrite_DisconnectConfigPos,
                            (int)FuncWrite_DisconnectConfigLen);
    s.insert(cs);

    cs._cmd     = Emetcon::PutConfig_Raw;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair(0, 0);  //  filled in later
    s.insert(cs);

    cs._cmd     = Emetcon::PutConfig_TSync;
    cs._io      = Emetcon::IO_Function_Write;
    cs._funcLen = make_pair((int)FuncWrite_TSyncPos,
                            (int)FuncWrite_TSyncLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_TSync;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair((int)Memory_LastTSyncPos,
                            (int)Memory_LastTSyncLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_Time;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair((int)Memory_RTCPos,
                            (int)Memory_RTCLen);
    s.insert(cs);

    cs._cmd     = Emetcon::PutConfig_Intervals;
    cs._io      = Emetcon::IO_Function_Write;
    cs._funcLen = make_pair((int)FuncWrite_IntervalsPos,
                            (int)FuncWrite_IntervalsLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetConfig_Intervals;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair((int)Memory_IntervalsPos,
                            (int)Memory_IntervalsLen);
    s.insert(cs);

    cs._cmd     = Emetcon::GetValue_PFCount;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair((int)Memory_PowerfailCountPos,
                            (int)Memory_PowerfailCountLen);
    s.insert(cs);

    cs._cmd     = Emetcon::PutValue_ResetPFCount;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair((int)MCT4XX_Command_PowerfailReset, 0);
    s.insert(cs);

    cs._cmd     = Emetcon::PutStatus_Reset;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair((int)MCT4XX_Command_Reset, 0);
    s.insert(cs);

    cs._cmd     = Emetcon::PutStatus_FreezeOne;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair((int)MCT_Command_FreezeOne, 0);
    s.insert(cs);

    cs._cmd     = Emetcon::PutStatus_FreezeTwo;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair((int)MCT_Command_FreezeTwo, 0);
    s.insert(cs);

    cs._cmd     = Emetcon::PutStatus_FreezeVoltageOne;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair((int)MCT4XX_Command_FreezeVoltageOne, 0);
    s.insert(cs);

    cs._cmd     = Emetcon::PutStatus_FreezeVoltageTwo;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair((int)MCT4XX_Command_FreezeVoltageTwo, 0);
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

    DLCCommandSet::const_iterator itr = _commandStore.find( CtiDLCCommandStore( cmd ) );

    if( itr != _commandStore.end( ) )
    {
        const CtiDLCCommandStore &cs = *itr;

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
// ---
            demand_rate = getLoadProfile().getLoadProfileDemandRate();
/*
            if( i == MCT410_LPVoltageChannel )
            {
                demand_rate = getLoadProfile().getVoltageLoadProfileRate();
            }
            else
            {
                demand_rate = getLoadProfile().getLoadProfileDemandRate();
            }
*/
// ---

            block_size  = demand_rate * 6;

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
                dout << "_lp_info[" << i << "].current_request  = " << _lp_info[i].current_request << endl;
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
                dout << "_lp_info[" << i << "].current_request  = " << _lp_info[i].current_request << endl;
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
    OutMessage->Sequence  = Emetcon::PutConfig_Intervals;     // Helps us figure it out later!
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

    if( !_intervalsSent )
    {
        sendIntervals(OutMessage, outList);

        _intervalsSent = true;
    }
    else
    {
        for( int i = 0; i < MCT4XX_LPChannels; i++ )
        {
// ---
            demand_rate = getLoadProfile().getLoadProfileDemandRate();
/*
            if( (i + 1) == MCT410_LPVoltageChannel )
            {
                demand_rate = getLoadProfile().getVoltageLoadProfileRate();
            }
            else
            {
                demand_rate = getLoadProfile().getLoadProfileDemandRate();
            }
*/
// ---
            block_size  = demand_rate * 6;

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
INT CtiDeviceMCT410::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    switch(InMessage->Sequence)
    {
        case Emetcon::Scan_Accum:
        case Emetcon::GetValue_Default:
        case Emetcon::GetValue_FrozenKWH:
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

        case Emetcon::GetValue_PeakDemand:
        case Emetcon::GetValue_FrozenPeakDemand:
        {
            status = decodeGetValuePeakDemand(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetValue_Voltage:
        case Emetcon::GetValue_FrozenVoltage:
        {
            status = decodeGetValueVoltage(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetValue_Outage:
        {
            status = decodeGetValueOutage(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetValue_LoadProfile:
        {
            status = decodeGetValueLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetValue_LoadProfilePeakReport:
        {
            status = decodeGetValueLoadProfilePeakReport(InMessage, TimeNow, vgList, retList, outList);
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

        case Emetcon::GetStatus_Disconnect:
        {
            status = decodeGetStatusDisconnect(InMessage, TimeNow, vgList, retList, outList);
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
            status = decodeGetConfigTime(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::Control_Conn):
        case (Emetcon::Control_Disc):
        {
            CtiReturnMsg *ReturnMsg;
/*
            if( ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr) )
            {
                ReturnMsg->setUserMessageId(InMessage->Return.UserID);
                ReturnMsg->setResultString(getName() + " / Control sent");

                retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList, true );
            }
*/
            //  this is distasteful, but it works...
            //  we need to allow downtime for the MCT to transmit to its disconnect collar
            CTISleep(3000);

            CtiRequestMsg newReq(getID(),
                                 "getstatus disconnect noqueue",
                                 InMessage->Return.UserID,
                                 0,
                                 InMessage->Return.RouteID,
                                 InMessage->Return.MacroOffset);

            newReq.setConnectionHandle((void *)InMessage->Return.Connection);

            CtiDeviceBase::ExecuteRequest(&newReq, CtiCommandParser(newReq.CommandString()), vgList, retList, outList);

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


unsigned char CtiDeviceMCT410::crc8( const unsigned char *buf, unsigned int len )
{
    const unsigned char llpcrc_poly = 0x07;

    /* Function to calclulate the 8 bit crc of the channel of interest
         and the period of interest start time.  This 8 bit crc will
         be sent back in byte 1 of a long load profile read.

       Parameters: uint8_t channel - This is the channel that is going
                                       to be returned.
                   uint8_t *time - A pointer to the period of interest start time

       Return: uint8_t crc
       */

    unsigned short crc = 0;
    unsigned char  current_byte;

    // Walk through the bytes of data
    for( int i = 0; i <= len; i++ )
    {
        if( i < len )
        {
            current_byte = buf[i];
        }
        else
        {
            current_byte = 0;
        }

        // Walk through each bit of the byte
        for(int bit = 0; bit < 8; bit++ )
        {
            // Move the data bits into the crc
            crc <<= 1;
            if( current_byte & 0x80 )
            {
                crc |= 0x0001;
            }

            // Do the crc calculation
            if( crc & 0x0100 )
            {
                crc ^= llpcrc_poly;
            }

            // Move the data byte ahead 1 bit
            current_byte <<= 1;
        }
    }

    return (unsigned char)(crc & 0xff);
}


CtiDeviceMCT410::point_info_t CtiDeviceMCT410::getData( unsigned char *buf, int len, ValueType vt )
{
    PointQuality_t quality    = NormalQuality;
    __int64 value = 0;
    unsigned long error_code = 0xffffffff;  //  filled with 0xff because some data types are less than 32 bits
    unsigned char quality_flags, resolution;
    point_info_t  retval;

    for( int i = 0; i < len; i++ )
    {
        //  input data is in MSB order
        error_code <<= 8;
        value      <<= 8;

        //  the first byte for some value types needs to be treated specially
        if( !i && (vt == ValueType_KW ||
                   vt == ValueType_LoadProfile_KW) )
        {
            //  save these for use later
            quality_flags = buf[i] & 0xc0;
            resolution    = buf[i] & 0x30;

            value        |= buf[i] & 0x0f;  //  trim off the quality bits and the resolution bits
            error_code   |= buf[i] | 0xc0;  //  fill in the quality bits to get the true error code
        }
        else if( !i && (vt == ValueType_LoadProfile_Voltage) )
        {
            quality_flags = buf[i] & 0xc0;

            value        |= buf[i] & 0x3f;  //  trim off the quality bits
            error_code   |= buf[i] | 0xc0;  //  fill in the quality bits to get the true error code
        }
        else
        {
            value        |= buf[i];
            error_code   |= buf[i];
        }
    }

    //  i s'pose this could be a set sometime, eh?
    QualityMap::const_iterator q_itr = _errorQualities.find(error_code);

    if( q_itr != _errorQualities.end() )
    {
        quality = q_itr->second.first;
        value   = 0;
    }
    else
    {
        //  only take the demand bits into account if everything else is cool
        switch( quality_flags )
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

    retval.value   = value;
    retval.quality = quality;

    if( vt == ValueType_KW || vt == ValueType_LoadProfile_KW )
    {
        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - demand value " << (unsigned long)value << " resolution " << (int)resolution << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        //  we need to do this here because value is an unsigned long and retval.first is a double
        switch( resolution )
        {
            default:
            case 0x00:  retval.value /=    10.0;    break;  //  100 WH units -> kWH
            case 0x10:  retval.value /=   100.0;    break;  //   10 WH units -> kWH
            case 0x20:  retval.value /=  1000.0;    break;  //    1 WH units -> kWH
            case 0x30:  retval.value /= 10000.0;    break;  //  0.1 WH units -> kWH
        }

        retval.value *= 10.0;  //  REMOVE THIS for HEAD or WHATEVER build HAS the proper, pretty, nice 1.0 kWH output code
    }

    return retval;
}


INT CtiDeviceMCT410::executeGetValue( CtiRequestMsg              *pReq,
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

    if( parse.isKeyValid("lp_command") )  //  load profile
    {
        RWTime now_time;
        RWDate now_date;

        unsigned long request_time, relative_time;

        int request_channel = parse.getiValue("lp_channel");
        int year, month, day, hour, minute;
        int interval_len, block_len;

        RWCTokenizer date_tok(parse.getsValue("lp_date")),
                     time_tok(parse.getsValue("lp_time"));

        string cmd = parse.getsValue("lp_command");

        /*
        //  getvalue lp channel 1 12/14/04 12:00
        //  getvalue lp channel 1 8-13-2005 14:45
        RWCTokenizer cmdtok(token);

        cmdtok();  //  move past lp
        cmdtok();  //  move past channel

        _cmd["lp_command"] = "lp";
        _cmd["lp_channel"] = atoi(RWCString(cmdtok()));
        _cmd["lp_date"]    = cmdtok();
        _cmd["lp_time"]    = cmdtok();
        }
        else if(!(token = CmdStr.match(re_lp_peak)).isNull())
        {
        //  getvalue lp peak daily channel 2 9/30/04 30
        //  getvalue lp peak hourly channel 3 10-15-2003 15
        RWCTokenizer cmdtok(token);

        cmdtok();  //  move past lp
        cmdtok();  //  move past peak

        _cmd["lp_peaktype"] = cmdtok();

        cmdtok();  //  move past channel

        _cmd["lp_command"] = "peak";
        _cmd["lp_channel"] = atoi(RWCString(cmdtok()));
        _cmd["lp_date"]    = cmdtok();
        _cmd["lp_range"]   = atoi(RWCString(cmdtok()));
        */


        if( request_channel >  0 &&
            request_channel <= MCT4XX_LPChannels )
        {
            month = atoi(RWCString(date_tok("-/")));
            day   = atoi(RWCString(date_tok("-/")));
            year  = atoi(RWCString(date_tok("-/")));

            if( year < 100 )
            {
                //  watch out for the y2.1k bug
                year += 2000;
            }

// ---
            interval_len = getLoadProfile().getLoadProfileDemandRate();
/*
            if( request_channel == MCT410_LPVoltageChannel )
            {
                interval_len = getLoadProfile().getVoltageProfileRate();
            }
            else
            {
                interval_len = getLoadProfile().getLoadProfileDemandRate();
            }
*/
// ---
            block_len    = 6 * interval_len;

            if( !cmd.compare("lp") )
            {
                function = Emetcon::GetValue_LoadProfile;
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

                if( found )
                {
                    hour   = atoi(RWCString(time_tok(":")));
                    minute = atoi(RWCString(time_tok(":")));

                    request_time  = RWTime(RWDate(day, month, year), hour, minute).seconds();
                    request_time -= request_time % interval_len;
                    request_time -= interval_len;  //  we report interval-ending, yet request interval-beginning...  so back that thing up

                    //  this is the number of seconds from the current pointer
                    relative_time = request_time - _llpInterest.time;

                    if( (request_channel == _llpInterest.channel) &&  //  correct channel
                        (relative_time < (16 * block_len))        &&  //  within 16 blocks
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

                            interest_om->Sequence = Emetcon::PutConfig_LoadProfileInterest;

                            interest_om->Buffer.BSt.Function = FuncWrite_LLPInterestPos;
                            interest_om->Buffer.BSt.IO       = Emetcon::IO_Function_Write;
                            interest_om->Buffer.BSt.Length   = FuncWrite_LLPInterestLen;
                            interest_om->MessageFlags |= MSGFLG_EXPECT_MORE;

                            unsigned long utc_time = request_time - rwEpoch;

                            interest_om->Buffer.BSt.Message[0] = gMCT400SeriesSPID;

                            interest_om->Buffer.BSt.Message[1] = request_channel  & 0x000000ff;

                            interest_om->Buffer.BSt.Message[2] = (utc_time >> 24) & 0x000000ff;
                            interest_om->Buffer.BSt.Message[3] = (utc_time >> 16) & 0x000000ff;
                            interest_om->Buffer.BSt.Message[4] = (utc_time >>  8) & 0x000000ff;
                            interest_om->Buffer.BSt.Message[5] = (utc_time)       & 0x000000ff;

                            outList.append(interest_om);
                            interest_om = 0;
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - unable to create outmessage, cannot set interval **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }
                    }

                    OutMessage->Buffer.BSt.Function = function;
                    OutMessage->Buffer.BSt.IO       = Emetcon::IO_Function_Read;
                    OutMessage->Buffer.BSt.Length   = 13;

                    function = Emetcon::GetValue_LoadProfile;

                    nRet = NoError;
                }
            }
            else if( !cmd.compare("peak") )
            {
                //  !!!  FIXME: this will not allow reporting on any load profile interval size smaller than 1 hour  !!!
                if( getLoadProfile().getLoadProfileDemandRate() >= 3600 )
                {
                    function = Emetcon::GetValue_LoadProfilePeakReport;
                    found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
                }
                else
                {
                    CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), OutMessage->Request.CommandStr);

                    if( ReturnMsg )
                    {
                        ReturnMsg->setUserMessageId(OutMessage->Request.UserID);
                        ReturnMsg->setResultString(getName() + " / Load profile reporting currently only supported for 60-minute load profile");

                        retMsgHandler( OutMessage->Request.CommandStr, NoMethod, ReturnMsg, vgList, retList, true );
                    }
                }

                if( found )
                {
                    int lp_peak_command = -1;
                    string lp_peaktype = parse.getsValue("lp_peaktype");
                    int request_range  = parse.getiValue("lp_range");  //  add safeguards to check that we're not >30 days... ?

                    if( !lp_peaktype.compare("day") )
                    {
                        lp_peak_command = FuncRead_LLPPeakDayPos;
                    }
                    else if( !lp_peaktype.compare("hour") )
                    {
                        lp_peak_command = FuncRead_LLPPeakHourPos;
                    }
                    else if( !lp_peaktype.compare("interval") )
                    {
                        lp_peak_command = FuncRead_LLPPeakIntervalPos;
                    }

                    if( lp_peak_command > 0 )
                    {
                        //  add on a day - this is the end of the interval, not the beginning,
                        //    so we need to start at midnight of the following day...  minus one second
                        request_time  = RWTime(RWDate(day + 1, month, year)).seconds() - 1;

                        if( request_time    != _llpPeakInterest.time    ||
                            request_channel != _llpPeakInterest.channel ||
                            request_range   != _llpPeakInterest.period )
                        {
                            //  we need to set it to the requested interval
                            CtiOutMessage *interest_om = new CtiOutMessage(*OutMessage);

                            if( interest_om )
                            {
                                _llpPeakInterest.time    = request_time;
                                _llpPeakInterest.channel = request_channel;
                                _llpPeakInterest.period  = request_range;

                                interest_om->Priority = OutMessage->Priority + 1;  //  just make sure this goes out first
                                interest_om->Sequence = Emetcon::PutConfig_LoadProfileReportPeriod;
                                interest_om->Request.Connection = 0;

                                interest_om->Buffer.BSt.Function = FuncWrite_LLPPeakInterestPos;
                                interest_om->Buffer.BSt.IO       = Emetcon::IO_Function_Write;
                                interest_om->Buffer.BSt.Length   = FuncWrite_LLPPeakInterestLen;
                                interest_om->MessageFlags |= MSGFLG_EXPECT_MORE;

                                unsigned long utc_time = request_time - rwEpoch;

                                interest_om->Buffer.BSt.Message[0] = gMCT400SeriesSPID;

                                interest_om->Buffer.BSt.Message[1] = request_channel  & 0x000000ff;

                                interest_om->Buffer.BSt.Message[2] = (utc_time >> 24) & 0x000000ff;
                                interest_om->Buffer.BSt.Message[3] = (utc_time >> 16) & 0x000000ff;
                                interest_om->Buffer.BSt.Message[4] = (utc_time >>  8) & 0x000000ff;
                                interest_om->Buffer.BSt.Message[5] = (utc_time)       & 0x000000ff;

                                interest_om->Buffer.BSt.Message[6] = request_range    & 0x000000ff;

                                outList.append(interest_om);
                                interest_om = 0;
                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint - unable to create outmessage, cannot set interval **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                            }
                        }

                        _llpPeakInterest.command = lp_peak_command;

                        OutMessage->Buffer.BSt.Function = lp_peak_command;
                        OutMessage->Buffer.BSt.IO       = Emetcon::IO_Function_Read;
                        OutMessage->Buffer.BSt.Length   = 13;

                        nRet = NoError;
                    }
                }
            }
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
        }
    }
    else if( parse.isKeyValid("outage") )  //  outages
    {
        if( !_sspec )
        {
            //  we need to set it to the requested interval
            CtiOutMessage *sspec_om = new CtiOutMessage(*OutMessage);

            if( sspec_om )
            {
                getOperation(Emetcon::GetConfig_Model, sspec_om->Buffer.BSt.Function, sspec_om->Buffer.BSt.Length, sspec_om->Buffer.BSt.IO);

                sspec_om->Sequence = Emetcon::GetConfig_Model;

                sspec_om->MessageFlags |= MSGFLG_EXPECT_MORE;

                outList.append(sspec_om);
                sspec_om = 0;
            }
        }

        int outagenum = parse.getiValue("outage");

        function = Emetcon::GetValue_Outage;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        if( outagenum < 0 || outagenum > 6 )
        {
            found = false;

            if( errRet )
            {
                RWCString temp = "Bad outage specification - Acceptable values:  1-6";
                errRet->setResultString( temp );
                errRet->setStatus(NoMethod);
                retList.insert( errRet );
                errRet = NULL;
            }
        }
        else if(outagenum > 4 )
        {
            OutMessage->Buffer.BSt.Function += 2;
        }
        else if(outagenum > 2 )
        {
            OutMessage->Buffer.BSt.Function += 1;
        }
    }
    else
    {
        nRet = Inherited::executeGetValue(pReq, parse, OutMessage, vgList, retList, outList);
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


INT CtiDeviceMCT410::decodeGetValueKWH(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;
    ULONG i,x;
    INT pid;
    RWCString resultString, freeze_info_string;
    RWTime pointTime;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    unsigned long pulses;

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

        point_info_t pi = getData(DSt->Message, 3, ValueType_Accumulator);

        if( InMessage->Sequence == Emetcon::GetValue_FrozenKWH )
        {
            pPoint = getDevicePointOffsetTypeEqual( 1 + MCT4XX_PointOffset_FrozenOffset, PulseAccumulatorPointType );

            //  assign time from the last freeze time, if the lower bit of dp.first matches the last freeze
            //    and the freeze counter (DSt->Message[3]) is what we expect
            //  also, archive the received freeze and the freeze counter into the dynamicpaoinfo table

            //pointTime = time of freeze
            //freeze_info_string = " @ " + pointTime;
        }
        else
        {
            pPoint = getDevicePointOffsetTypeEqual( 1, PulseAccumulatorPointType );

            pointTime -= pointTime.seconds() % 300;
        }

        // handle accumulator data here
        if( pPoint != NULL)
        {
            // 24 bit pulse value
            pi.value = ((CtiPointNumeric *)pPoint)->computeValueForUOM(pi.value);

            if( pi.quality != InvalidQuality )
            {
                resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces()) + freeze_info_string;

                pData = makePointDataMsg(pPoint, pi, resultString);

                if(pData != NULL)
                {
                    pData->setTime( pointTime );
                    ReturnMsg->PointData().insert(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            else
            {
                resultString = getName() + " / " + pPoint->getName() + " = (invalid data)" + freeze_info_string;

                ReturnMsg->setResultString(resultString);
            }
        }
        else
        {
            if( pi.quality != InvalidQuality )
            {
                resultString = getName() + " / KYZ 1 = " + CtiNumStr(pi.value) + freeze_info_string + "  --  POINT UNDEFINED IN DB";
            }
            else
            {
                resultString = getName() + " / KYZ 1 = (invalid data) --  POINT UNDEFINED IN DB";
            }

            ReturnMsg->setResultString(resultString);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValueFrozen(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
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

        pPoint = getDevicePointOffsetTypeEqual( 1 + MCT4XX_PointOffset_FrozenOffset, PulseAccumulatorPointType );

        pi = getData(DSt->Message, 3, ValueType_Accumulator);

        // handle accumulator data here
        if( pPoint != NULL)
        {
            // 24 bit pulse value
            pi.value = ((CtiPointNumeric *)pPoint)->computeValueForUOM(pi.value);

            RWTime pointTime;

            if( pi.quality != InvalidQuality )
            {
                resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

                pData = makePointDataMsg(pPoint, pi, resultString);

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
            if( pi.quality != InvalidQuality )
            {
                resultString = getName() + " / KYZ 1 = " + CtiNumStr(pi.value) +  "  --  POINT UNDEFINED IN DB";
            }
            else
            {
                resultString = getName() + " / KYZ 1 = (invalid data) --  POINT UNDEFINED IN DB";
            }

            ReturnMsg->setResultString(resultString);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValueDemand(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int status = NORMAL;

    point_info_t pi;
    RWCString resultString,
              pointString;

    INT ErrReturn = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    CtiPointBase    *pPoint = NULL;
    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;

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

        pi = getData(DSt->Message, 2, ValueType_KW);

        //  turn raw pulses into a demand reading
        pi.value *= double(3600 / getDemandInterval());

        // look for first defined DEMAND accumulator
        if( pPoint = getDevicePointOffsetTypeEqual( 1, DemandAccumulatorPointType ) )
        {
            RWTime pointTime;

            pi.value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(pi.value);

            pointString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value,
                                                                                    ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

            if(pData = makePointDataMsg(pPoint, pi, pointString))
            {
                pointTime -= pointTime.seconds() % getDemandInterval();
                pData->setTime( pointTime );
                ReturnMsg->PointData().insert(pData);
                pData = NULL;  // We just put it on the list...
            }
        }
        else
        {
            if( pi.quality != InvalidQuality )
            {
                resultString += getName() + " / Demand = " + CtiNumStr(pi.value) +  "  --  POINT UNDEFINED IN DB";
            }
            else
            {
                resultString += getName() + " / Demand = (invalid data) --  POINT UNDEFINED IN DB";
            }
        }

        pi = getData(DSt->Message + 2, 2, ValueType_Voltage);

        if( pPoint = getDevicePointOffsetTypeEqual( MCT410_PointOffset_Voltage, DemandAccumulatorPointType ) )
        {
            RWTime pointTime;

            pi.value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(pi.value);

            pointString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value,
                                                                                    ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

            if( pData = makePointDataMsg(pPoint, pi, pointString) )
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
            pi.value *= 0.1;

            resultString = getName() + " / Voltage = " + CtiNumStr(pi.value) + "  --  POINT UNDEFINED IN DB\n";
            ReturnMsg->setResultString(ReturnMsg->ResultString() + "\n" + resultString);
        }

        pi = getData(DSt->Message + 4, 2, ValueType_Raw);

        if( pPoint = getDevicePointOffsetTypeEqual( MCT_PointOffset_Accumulator_Powerfail, PulseAccumulatorPointType ) )
        {
            RWTime pointTime;

            pi.value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(pi.value);

            resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value,
                                                                                     ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

            if( pData = makePointDataMsg(pPoint, pi, resultString) )
            {
                ReturnMsg->PointData().insert(pData);
                pData = NULL;  // We just put it on the list...
            }
        }
        else
        {
            resultString += getName() + " / Blink Counter = " + CtiNumStr(pi.value) + "\n";
        }

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValuePeakDemand(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    int             status = NORMAL, pointoffset;
    unsigned long   timeOfPeak;
    point_info_t    pi;
    RWCString resultString, freeze_info_string;
    RWTime    pointTime;

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

        pi = getData(DSt->Message, 2, ValueType_KW);

        timeOfPeak = DSt->Message[2] << 24 |
                     DSt->Message[3] << 16 |
                     DSt->Message[4] <<  8 |
                     DSt->Message[5];

        pointTime = RWTime(timeOfPeak + rwEpoch);

        //  turn raw pulses into a demand reading
        pi.value *= DOUBLE(3600 / getDemandInterval());

        //  first defined peak demand accumulator
        pointoffset = 1 + MCT4XX_PointOffset_PeakOffset;

        if( InMessage->Sequence == Emetcon::GetValue_FrozenPeakDemand )
        {
            //  add on the frozen offset
            pointoffset += MCT4XX_PointOffset_FrozenOffset;
        }

        if( pPoint = getDevicePointOffsetTypeEqual(pointoffset, DemandAccumulatorPointType) )
        {
            pi.value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(pi.value);

            resultString = getName() + " / " + pPoint->getName() + " = "
                                     + CtiNumStr(pi.value, ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces())
                                     + " @ " + pointTime.asString();

            if( pData = makePointDataMsg(pPoint, pi, resultString) )
            {
                pData->setTime(pointTime);
                ReturnMsg->PointData().insert(pData);
                pData = NULL;  // We just put it on the list...
            }
        }
        else
        {
            resultString = getName() + " / Peak Demand = " + CtiNumStr(pi.value) + " @ " + pointTime.asString() + "  --  POINT UNDEFINED IN DB";
            ReturnMsg->setResultString(resultString);
        }

        pi = getData(DSt->Message + 6, 3, ValueType_Accumulator);

        if( InMessage->Sequence == Emetcon::GetValue_FrozenPeakDemand )
        {
            pPoint = getDevicePointOffsetTypeEqual( 1 + MCT4XX_PointOffset_FrozenOffset, PulseAccumulatorPointType );

            //  assign time from the last freeze time, if the lower bit of dp.first matches the last freeze
            //    and the freeze counter (DSt->Message[8]) is what we expect
            //  also, archive the received freeze and the freeze counter into the dynamicpaoinfo table

            //freeze_info_string = " @ " + pointTime();
            //pointTime = last freeze time, if it matches
            pointTime  = RWTime::now();
            pointTime -= pointTime.seconds() % 300;
        }
        else
        {
            pPoint = getDevicePointOffsetTypeEqual( 1, PulseAccumulatorPointType );

            pointTime  = RWTime::now();
            pointTime -= pointTime.seconds() % 300;
        }

        if( pPoint )
        {
            pi.value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(pi.value);

            resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value,
                                                                                     ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

            if( pData = makePointDataMsg(pPoint, pi, resultString) )
            {
                pointTime -= pointTime.seconds() % getDemandInterval();
                pData->setTime( pointTime );
                ReturnMsg->PointData().insert(pData);
                pData = NULL;  // We just put it on the list...
            }
        }
        else
        {
            resultString = getName() + " / Meter Reading = " + CtiNumStr(pi.value) + freeze_info_string + "  --  POINT UNDEFINED IN DB";
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
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    point_info_t pi, max_volt_info, min_volt_info;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        RWTime minTime, maxTime;
        int minPointOffset, maxPointOffset;
        RWCString resultString, pointString;

        CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg *pData = NULL;

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        max_volt_info  = getData(DSt->Message, 2, ValueType_Voltage);
        maxPointOffset = MCT410_PointOffset_MaxVoltage;

        pi = getData(DSt->Message + 2, 4, ValueType_Raw);
        maxTime = RWTime((unsigned long)pi.value + rwEpoch);


        min_volt_info  = getData(DSt->Message + 6, 2, ValueType_Voltage);
        minPointOffset = MCT410_PointOffset_MinVoltage;

        pi = getData(DSt->Message + 8, 4, ValueType_Raw);
        minTime = RWTime((unsigned long)pi.value + rwEpoch);

        if( InMessage->Sequence == Emetcon::GetValue_FrozenVoltage )
        {
            minPointOffset += MCT4XX_PointOffset_FrozenOffset;
            maxPointOffset += MCT4XX_PointOffset_FrozenOffset;
        }

        CtiPointDataMsg *pdm;
        CtiPointNumeric *pt_max_volts = (CtiPointNumeric*)getDevicePointOffsetTypeEqual( maxPointOffset, DemandAccumulatorPointType ),
                        *pt_min_volts = (CtiPointNumeric*)getDevicePointOffsetTypeEqual( minPointOffset, DemandAccumulatorPointType );

        if( pt_max_volts )
        {
            max_volt_info.value = pt_max_volts->computeValueForUOM(max_volt_info.value);

            pointString = getName() + " / " + pt_max_volts->getName() + " = " + CtiNumStr(max_volt_info.value) + " @ " + maxTime.asString();

            if( pdm = makePointDataMsg(pt_max_volts, max_volt_info, pointString) )
            {
                pdm->setTime(maxTime);

                ReturnMsg->PointData().insert(pdm);
                pdm = 0;
            }
        }
        else
        {
            max_volt_info.value *= 0.1;

            if( InMessage->Sequence == Emetcon::GetValue_FrozenVoltage )
            {
                resultString += getName() + " / Frozen Max Voltage = " + CtiNumStr(max_volt_info.value) + " @ " + maxTime.asString();
            }
            else
            {
                resultString += getName() + " / Max Voltage = " + CtiNumStr(max_volt_info.value) + " @ " + maxTime.asString();
            }
        }

        if( pt_min_volts )
        {
            min_volt_info.value = pt_min_volts->computeValueForUOM(min_volt_info.value);

            pointString = getName() + " / " + pt_min_volts->getName() + " = " + CtiNumStr(min_volt_info.value) + " @ " + minTime.asString();

            if( pdm = makePointDataMsg(pt_min_volts, min_volt_info, pointString) )
            {
                pdm->setTime(minTime);

                ReturnMsg->PointData().insert(pdm);
                pdm = 0;
            }
        }
        else
        {
            min_volt_info.value *= 0.1;

            if( InMessage->Sequence == Emetcon::GetValue_FrozenVoltage )
            {
                resultString += getName() + " / Frozen Min Voltage = " + CtiNumStr(min_volt_info.value) + " @ " + minTime.asString();
            }
            else
            {
                resultString += getName() + " / Min Voltage = " + CtiNumStr(min_volt_info.value) + " @ " + minTime.asString();
            }
        }


        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValueOutage( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT status = NORMAL;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        //  no error, time for decode
        INT ErrReturn =  InMessage->EventCode & 0x3fff;
        unsigned char   *msgbuf = InMessage->Buffer.DSt.Message;
        CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointBase    *pPoint = NULL;
        CtiPointDataMsg *pData = NULL;
        double value;

        int outagenum, multiplier;
        unsigned long  timestamp;
        unsigned short duration;
        RWCString pointString, resultString, timeString;
        RWTime outageTime;

        ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        if( _sspec )
        {
            outagenum = parse.getiValue("outage");

            if( !(outagenum % 2) )
            {
                outagenum--;
            }

            pPoint = getDevicePointOffsetTypeEqual( MCT410_PointOffset_Analog_Outage, AnalogPointType );

            for( int i = 0; i < 2; i++ )
            {
                int days, hours, minutes, seconds, cycles;

                timestamp = msgbuf[(i*6)+0] << 24 |
                            msgbuf[(i*6)+1] << 16 |
                            msgbuf[(i*6)+2] <<  8 |
                            msgbuf[(i*6)+3];

                duration  = msgbuf[(i*6)+4] << 8 |
                            msgbuf[(i*6)+5];

                outageTime = RWTime(timestamp + rwEpoch);

                if( timestamp > MCT4XX_DawnOfTime )
                {
                    timeString = outageTime.asString();
                }
                else
                {
                    timeString = "(invalid time)";
                }

                pointString = getName() + " / Outage " + CtiNumStr(outagenum + i) + " : " + timeString + " for ";

                if( _sspec == MCT410_Sspec &&
                    _rev   >= MCT410_Min_NewOutageRev &&
                    _rev   <= MCT410_Max_NewOutageRev )
                {
                    if( duration == 0x8000 )
                    {
                        pointString += "(unknown duration)\n";
                    }
                    else
                    {
                        /*
                        Units of outage:
                        Bits 0-1 Units of outage (-2) 0=cycles, 1=seconds; 2=minutes; 3=waiting time sync
                        Bits 2-3 Unused
                        Bits 4-5 Units of outage (-1) 0=cycles, 1=seconds; 2=minutes; 3=waiting time sync
                        Bits 6-7 Unused
                        */

                        cycles = duration;

                        if( i == 0 )
                        {
                            multiplier = (msgbuf[12] >> 4) & 0x03;
                        }
                        else
                        {
                            multiplier =  msgbuf[12] & 0x03;
                        }

                        switch( multiplier )
                        {
                            case 2: cycles *= 60;  //  minutes falls through to...
                            case 1: cycles *= 60;  //  seconds, which falls through to...
                            case 0: break;         //  cycles, which does nothing

                            case 3: cycles = -1;   //  waiting time sync - don't report a value
                        }

                        if( cycles < 0 )
                        {
                            pointString += "(waiting for time sync to calculate outage duration)";
                        }
                        else
                        {
                            seconds = cycles  / 60;
                            minutes = seconds / 60;
                            hours   = minutes / 60;

                            seconds %= 60;
                            minutes %= 60;
                            hours   %= 24;

                            pointString += CtiNumStr(hours).zpad(2) + ":" +
                                           CtiNumStr(minutes).zpad(2) + ":" +
                                           CtiNumStr(seconds).zpad(2);

                            if( cycles % 60 )
                            {
                                pointString += ", " + CtiNumStr(cycles % 60) + " cycles";
                            }
                        }
                    }
                }
                else
                {
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

                    pointString += CtiNumStr(hours).zpad(2) + ":" +
                                   CtiNumStr(minutes).zpad(2) + ":" +
                                   CtiNumStr(seconds).zpad(2);

                    if( cycles )
                    {
                        pointString += ", " + CtiNumStr(cycles) + " cycles\n";
                    }
                }

                if( !i )    pointString += "\n";

                if( pPoint )
                {
                    value  = cycles;
                    value /= 60;
                    value  = ((CtiPointNumeric*)pPoint)->computeValueForUOM(value);

                    if( pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), value, NormalQuality, AnalogPointType, pointString) )
                    {
                        pData->setTime( outageTime );

                        ReturnMsg->PointData().insert(pData);
                        pData = 0;
                    }
                }
                else
                {
                    resultString += pointString;
                }
            }

            ReturnMsg->setResultString(resultString);
        }
        else
        {
            ReturnMsg->setResultString(getName() + " / Did not read sspec, could not reliably decode outages; try read again");
        }

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

        unsigned char interest[5];
        unsigned long tmptime = _llpInterest.time - rwEpoch;

        interest[0] = (tmptime >> 24) & 0x000000ff;
        interest[1] = (tmptime >> 16) & 0x000000ff;
        interest[2] = (tmptime >>  8) & 0x000000ff;
        interest[3] = (tmptime)       & 0x000000ff;
        interest[4] = _llpInterest.channel;

        /*
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);

            for( int i = 0; i < 5; i++ )
            {
                dout << "interest " << i << ": " << (int)interest[i] << endl;
            }

            dout << "crc8 = " << (int)crc8(interest, 5) << endl;
            dout << "dst  = " << (int)DSt->Message[0] << endl;
        }
        */

        if( crc8(interest, 5) == DSt->Message[0] )
        {
            channel      = _llpInterest.channel;
            decode_time  = _llpInterest.time + _llpInterest.offset;

// ---
            interval_len = getLoadProfile().getLoadProfileDemandRate();
/*
            if( channel == MCT410_LPVoltageChannel )
            {
                interval_len = getLoadProfile().getVoltageProfileRate();
            }
            else
            {
                interval_len = getLoadProfile().getLoadProfileDemandRate();
            }
*/
// ---

            block_len    = interval_len * 6;

            pPoint = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( channel + MCT_PointOffset_LoadProfileOffset, DemandAccumulatorPointType );

            for( int i = 0; i < 6; i++ )
            {
                //  this is where the block started...
                timeStamp  = decode_time + (interval_len * i);
                //  but we want interval *ending* times, so add on one more interval
                timeStamp += interval_len;

                if( channel == MCT410_LPVoltageChannel )
                {
                    pi = getData(DSt->Message + (i * 2) + 1, 2, ValueType_LoadProfile_Voltage);
                }
                else
                {
                    pi = getData(DSt->Message + (i * 2) + 1, 2, ValueType_LoadProfile_KW);
                }

                if( channel != MCT410_LPVoltageChannel )
                {
                    pi.value *= 3600 / interval_len;
                }

                if( pPoint )
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
                    if( pi.quality != InvalidQuality )
                    {
                        resultString += getName() + " / LP channel " + CtiNumStr(channel) + " @ " + RWTime(timeStamp).asString() + " = " + CtiNumStr(pi.value, 0) + "\n";
                    }
                    else
                    {
                        resultString += getName() + " / LP channel " + CtiNumStr(channel) + " @ " + RWTime(timeStamp).asString() + " = (invalid data)\n";
                    }
                }
            }
        }
        else
        {
            resultString = "Load Profile Interest check does not match - try read again";

            _llpInterest.channel = 0;
            _llpInterest.time    = 0;
        }

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValueLoadProfilePeakReport(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    RWCString result_string;
    int       interval_len;
    double    max_usage, avg_daily, total_usage;
    unsigned long max_demand_timestamp, pulses;

    CtiReturnMsg    *ReturnMsg = NULL;  // Message sent to VanGogh, inherits from Multi

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        pulses = DSt->Message[0] << 16 |
                 DSt->Message[1] <<  8 |
                 DSt->Message[2];

        if( _llpPeakInterest.command == FuncRead_LLPPeakDayPos )
        {
            //  daily usage is in 0.1 kWH units
            max_usage = (double)pulses / 10;
        }
        else  //  everything else is in 0.1 WH units
        {
            max_usage = (double)pulses / 10000;
        }

        max_demand_timestamp =  DSt->Message[3] << 24 |
                                DSt->Message[4] << 16 |
                                DSt->Message[5] <<  8 |
                                DSt->Message[6];

        max_demand_timestamp += rwEpoch;

        pulses = DSt->Message[7] << 16 |
                 DSt->Message[8] <<  8 |
                 DSt->Message[9];

        avg_daily = (double)pulses / 10;

        pulses = DSt->Message[10] << 16 |
                 DSt->Message[11] <<  8 |
                 DSt->Message[12];

        total_usage = (double)pulses / 10;

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        result_string  = getName() + " / Channel " + CtiNumStr(_llpPeakInterest.channel) + " Load Profile Report\n";
        result_string += "Report range: " + RWTime(_llpPeakInterest.time - (_llpPeakInterest.period * 86400)).asString() + " - " +
                                            RWTime(_llpPeakInterest.time).asString() + "\n";

        switch( _llpPeakInterest.command )
        {
            case FuncRead_LLPPeakDayPos:
            {
                result_string += "Peak day: " + RWTime(max_demand_timestamp).asString() + "\n";
                result_string += "Usage:  " + CtiNumStr(max_usage) + " kWH\n";
                result_string += "Demand: " + CtiNumStr(max_usage / 24) + " kW\n";
                result_string += "Average daily usage over range: " + CtiNumStr(avg_daily) + " kWH\n";
                result_string += "Total usage over range: " + CtiNumStr(total_usage) + " kWH\n";

                break;
            }
            case FuncRead_LLPPeakHourPos:
            {
                result_string += "Peak hour: " + RWTime(max_demand_timestamp).asString() + "\n";
                result_string += "Usage:  " + CtiNumStr(max_usage) + " kWH\n";
                result_string += "Demand: " + CtiNumStr(max_usage) + " kW\n";
                result_string += "Average daily usage over range: " + CtiNumStr(avg_daily) + " kWH\n";
                result_string += "Total usage over range: " + CtiNumStr(total_usage) + " kWH\n";

                break;
            }
            case FuncRead_LLPPeakIntervalPos:
            {
                int intervals_per_hour = 3600 / getLoadProfile().getLoadProfileDemandRate();

                result_string += "Peak interval: " + RWTime(max_demand_timestamp).asString() + "\n";
                result_string += "Usage:  " + CtiNumStr(max_usage) + " kWH\n";
                result_string += "Demand: " + CtiNumStr(max_usage * intervals_per_hour) + " kW\n";
                result_string += "Average daily usage over range: " + CtiNumStr(avg_daily) + " kWH\n";
                result_string += "Total usage over range: " + CtiNumStr(total_usage) + " kWH\n";

                break;
            }
        }

        ReturnMsg->setResultString(result_string);

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
    unsigned long  timestamp, pulses;
    point_info_t   pi;

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
// --
            interval_len = getLoadProfile().getLoadProfileDemandRate();
/*
            if( channel == MCT410_LPVoltageChannel )
            {
                interval_len = getLoadProfile().getVoltageProfileRate();
            }
            else
            {
                interval_len = getLoadProfile().getLoadProfileDemandRate();
            }
*/
// ---

            if( point = (CtiPointNumeric *)getDevicePointOffsetTypeEqual( channel + MCT_PointOffset_LoadProfileOffset, DemandAccumulatorPointType ) )
            {
                //  this is where the block started...
                timestamp  = TimeNow.seconds();
                timestamp -= interval_len * 6 * block;
                timestamp -= timestamp % (interval_len * 6);

                if( timestamp == _lp_info[channel - 1].current_request )
                {
                    for( int offset = 5; offset >= 0; offset-- )
                    {
                        if( channel == MCT410_LPVoltageChannel )
                        {
                            pi = getData(DSt->Message + offset*2 + 1, 2, ValueType_LoadProfile_Voltage);
                        }
                        else
                        {
                            pi = getData(DSt->Message + offset*2 + 1, 2, ValueType_LoadProfile_KW);

                            //  adjust for the demand interval
                            pi.value *= 3600 / interval_len;
                        }

                        //  compute for the UOM
                        pi.value = point->computeValueForUOM(pi.value);

                        if( pdata = makePointDataMsg(point, pi, "") )
                        {
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

        resultString += getName() + " / Voltage Profile Status:\n";

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


INT CtiDeviceMCT410::decodeGetConfigIntervals(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        RWCString resultString;

        resultString  = getName() + " / Demand Interval:       " + CtiNumStr(DSt->Message[0]) + " minutes\n";
        resultString += getName() + " / Load Profile Interval: " + CtiNumStr(DSt->Message[1]) + " minutes\n";
        resultString += getName() + " / Voltage Demand Interval:       ";
        if( DSt->Message[2] / 4 > 0 )
        {
            resultString += CtiNumStr(DSt->Message[2] / 4) + " minutes";
        }
        if( DSt->Message[2] % 4 > 0 )
        {
            resultString += CtiNumStr((DSt->Message[2] % 4) * 15) + " seconds";
        }
        resultString += "\n";

        resultString += getName() + " / Voltage Profile Interval: " + CtiNumStr(DSt->Message[3]) + " minutes\n";

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

        if( InMessage->Sequence == Emetcon::GetConfig_Time )
        {
            resultString = getName() + " / Current Time: " + tmpTime.asString();
        }
        else if( InMessage->Sequence == Emetcon::GetConfig_TSync )
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


INT CtiDeviceMCT410::decodeGetConfigDisconnect(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    RWCString resultStr;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

        resultStr  = getName() + " / Disconnect Config:\n";

        resultStr += "Load limit ";
        resultStr += (DSt->Message[0] & 0x04)?"active\n":"inactive\n";

        resultStr += "Disconnect status: ";

        switch( DSt->Message[0] & 0x03 )
        {
            case MCT410_StatusConnected:                resultStr += "connected\n";                 break;
            case MCT410_StatusConnectArmed:             resultStr += "connect armed\n";             break;
            case MCT410_StatusDisconnected:             resultStr += "disconnected\n";              break;
            case MCT410_StatusDisconnectedConfirmed:    resultStr += "confirmed disconnected\n";    break;
        }

        if( DSt->Message[1] & 0x02 )
        {
            resultStr += "Disconnect error - nonzero demand detected after disconnect command sent to collar\n";
        }

        long disconnectaddress = DSt->Message[2] << 16 |
                                 DSt->Message[3] <<  8 |
                                 DSt->Message[4];

        resultStr += "Disconnect receiver address: " + CtiNumStr(disconnectaddress) + "\n";

        int demandlimit = DSt->Message[5] << 8 |
                          DSt->Message[6];

        resultStr += "Disconnect demand threshold: ";
        resultStr += demandlimit?RWCString(CtiNumStr(demandlimit)):RWCString("disabled");
        resultStr += "\n";

        resultStr += "Disconnect load limit connect delay: " + CtiNumStr(DSt->Message[7]) + " minutes\n";

        resultStr += "Disconnect load limit count: " + CtiNumStr(DSt->Message[8]) + "\n";

        if(ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr))
        {
            ReturnMsg->setUserMessageId(InMessage->Return.UserID);
            ReturnMsg->setResultString(resultStr);

            retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            status = MEMORY;
        }
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
        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

        ssp  = InMessage->Buffer.DSt.Message[0];
        ssp |= InMessage->Buffer.DSt.Message[4] << 8;

        sspec  = "\nSoftware Specification ";
        sspec += CtiNumStr(ssp);
        sspec += " Rom Revision ";

        if( InMessage->Buffer.DSt.Message[1] <= 26 )
        {
            sspec += RWCString((char)(InMessage->Buffer.DSt.Message[1] + 'A' - 1));
        }
        else
        {
            sspec += CtiNumStr((int)InMessage->Buffer.DSt.Message[1]);
            sspec += " (unreleased/unverified revision)";
        }

        sspec += "\n";

        _sspec = ssp;
        _rev   = InMessage->Buffer.DSt.Message[1];

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString( sspec + options );

        //  this is hackish, and should be handled in a more centralized manner...  retMsgHandler, for example
        if( InMessage->MessageFlags & MSGFLG_EXPECT_MORE )
        {
            ReturnMsg->setExpectMore(true);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}

