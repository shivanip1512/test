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
* REVISION     :  $Revision: 1.107 $
* DATE         :  $Date: 2006/11/20 15:16:24 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include "device.h"
#include "devicetypes.h"
#include "dev_mct410.h"
#include "dev_base.h"
#include "tbl_ptdispatch.h"
#include "tbl_dyn_paoinfo.h"
#include "logger.h"
#include "mgr_point.h"
#include "pt_status.h"
#include "numstr.h"
#include "porter.h"
#include "portglob.h"
#include "utility.h"
#include "dllyukon.h"

#include "ctidate.h"
#include "ctitime.h"
#include <string>

using std::string;

using namespace std;

using namespace Cti;
using namespace Config;
using namespace MCT;

using Protocol::Emetcon;


const CtiDeviceMCT410::CommandSet      CtiDeviceMCT410::_commandStore   = CtiDeviceMCT410::initCommandStore();

const CtiDeviceMCT410::ConfigPartsList CtiDeviceMCT410::_config_parts   = CtiDeviceMCT410::initConfigParts();

const CtiDeviceMCT::DynamicPaoAddressing_t         CtiDeviceMCT410::_dynPaoAddressing     = CtiDeviceMCT410::initDynPaoAddressing();
const CtiDeviceMCT::DynamicPaoFunctionAddressing_t CtiDeviceMCT410::_dynPaoFuncAddressing = CtiDeviceMCT410::initDynPaoFuncAddressing();

CtiDeviceMCT410::CtiDeviceMCT410( ) :
    _intervalsSent(false)  //  whee!  you're going to be gone soon, sucker!
{
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


void CtiDeviceMCT410::setDisconnectAddress( unsigned long address )
{
    _disconnectAddress = address;
}

CtiDeviceMCT::DynamicPaoAddressing_t CtiDeviceMCT410::initDynPaoAddressing()
{
    DynamicPaoAddressing_t addressSet;

//  these cannot be properly decoded by the dynamicPaoAddressing code
//    addressSet.insert(DynamicPaoAddressing(Memory_SSpecPos,                 Memory_SSpecLen,                Keys::Key_MCT_SSpec));
//    addressSet.insert(DynamicPaoAddressing(Memory_RevisionPos,              Memory_RevisionLen,             Keys::Key_MCT_SSpecRevision));

    addressSet.insert(DynamicPaoAddressing(Memory_OptionsPos,               Memory_OptionsLen,              Keys::Key_MCT_Options));
    addressSet.insert(DynamicPaoAddressing(Memory_ConfigurationPos,         Memory_ConfigurationLen,        Keys::Key_MCT_Configuration));

    addressSet.insert(DynamicPaoAddressing(Memory_EventFlagsMask1Pos,       Memory_EventFlagsMask1Len,      Keys::Key_MCT_EventFlagsMask1));
    addressSet.insert(DynamicPaoAddressing(Memory_EventFlagsMask2Pos,       Memory_EventFlagsMask2Len,      Keys::Key_MCT_EventFlagsMask2));
    addressSet.insert(DynamicPaoAddressing(Memory_MeterAlarmMaskPos,        Memory_MeterAlarmMaskLen,       Keys::Key_MCT_MeterAlarmMask));

    addressSet.insert(DynamicPaoAddressing(Memory_BronzeAddressPos,         Memory_BronzeAddressLen,        Keys::Key_MCT_AddressBronze));
    addressSet.insert(DynamicPaoAddressing(Memory_LeadAddressPos,           Memory_LeadAddressLen,          Keys::Key_MCT_AddressLead));
    addressSet.insert(DynamicPaoAddressing(Memory_CollectionAddressPos,     Memory_CollectionAddressLen,    Keys::Key_MCT_AddressCollection));
    addressSet.insert(DynamicPaoAddressing(Memory_SPIDAddressPos,           Memory_SPIDAddressLen,          Keys::Key_MCT_AddressServiceProviderID));

    addressSet.insert(DynamicPaoAddressing(Memory_DemandIntervalPos,        Memory_DemandIntervalLen,       Keys::Key_MCT_DemandInterval));
    addressSet.insert(DynamicPaoAddressing(Memory_LoadProfileIntervalPos,   Memory_LoadProfileIntervalLen,  Keys::Key_MCT_LoadProfileInterval));
    addressSet.insert(DynamicPaoAddressing(Memory_VoltageDemandIntervalPos, Memory_VoltageDemandIntervalLen, Keys::Key_MCT_VoltageDemandInterval));
    addressSet.insert(DynamicPaoAddressing(Memory_VoltageLPIntervalPos,     Memory_VoltageLPIntervalLen,    Keys::Key_MCT_VoltageLPInterval));

    addressSet.insert(DynamicPaoAddressing(Memory_OverVThresholdPos,        Memory_OverVThresholdLen,       Keys::Key_MCT_OverVoltageThreshold));
    addressSet.insert(DynamicPaoAddressing(Memory_UnderVThresholdPos,       Memory_UnderVThresholdLen,      Keys::Key_MCT_UnderVoltageThreshold));

    addressSet.insert(DynamicPaoAddressing(Memory_OutageCyclesPos,          Memory_OutageCyclesLen,         Keys::Key_MCT_OutageCycles));

    addressSet.insert(DynamicPaoAddressing(Memory_TimeAdjustTolPos,         Memory_TimeAdjustTolLen,        Keys::Key_MCT_TimeAdjustTolerance));

    addressSet.insert(DynamicPaoAddressing(Memory_DSTBeginPos,              Memory_DSTBeginLen,             Keys::Key_MCT_DSTStartTime));
    addressSet.insert(DynamicPaoAddressing(Memory_DSTEndPos,                Memory_DSTEndLen,               Keys::Key_MCT_DSTEndTime));
    addressSet.insert(DynamicPaoAddressing(Memory_TimeZoneOffsetPos,        Memory_TimeZoneOffsetLen,       Keys::Key_MCT_TimeZoneOffset));

    addressSet.insert(DynamicPaoAddressing(Memory_TOUDayTablePos,           Memory_TOUDayTableLen,          Keys::Key_MCT_DayTable));

    addressSet.insert(DynamicPaoAddressing(Memory_Holiday1Pos,              Memory_Holiday1Len,             Keys::Key_MCT_Holiday1));
    addressSet.insert(DynamicPaoAddressing(Memory_Holiday2Pos,              Memory_Holiday2Len,             Keys::Key_MCT_Holiday2));
    addressSet.insert(DynamicPaoAddressing(Memory_Holiday3Pos,              Memory_Holiday3Len,             Keys::Key_MCT_Holiday3));

    addressSet.insert(DynamicPaoAddressing(Memory_CentronParametersPos,     Memory_CentronParametersLen,    Keys::Key_MCT_CentronParameters));
    addressSet.insert(DynamicPaoAddressing(Memory_CentronMultiplierPos,     Memory_CentronMultiplierLen,    Keys::Key_MCT_CentronRatio));

    return addressSet;
}

CtiDeviceMCT::DynamicPaoFunctionAddressing_t CtiDeviceMCT410::initDynPaoFuncAddressing()
{
    DynamicPaoAddressing_t addressSet;
    DynamicPaoFunctionAddressing_t functionSet;

    // FuncRead_TOUDaySchedulePos
    addressSet.insert(DynamicPaoAddressing( 0, 2, Keys::Key_MCT_DayTable));
    addressSet.insert(DynamicPaoAddressing( 2, 1, Keys::Key_MCT_DefaultTOURate));
    addressSet.insert(DynamicPaoAddressing(10, 1, Keys::Key_MCT_TimeZoneOffset));

    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(FuncRead_TOUDaySchedulePos,addressSet));

    addressSet.clear();

    // FuncRead_LLPStatusPos
    addressSet.insert(DynamicPaoAddressing( 4, 1, Keys::Key_MCT_LLPChannel1Len));
    addressSet.insert(DynamicPaoAddressing( 5, 1, Keys::Key_MCT_LLPChannel2Len));
    addressSet.insert(DynamicPaoAddressing( 6, 1, Keys::Key_MCT_LLPChannel3Len));
    addressSet.insert(DynamicPaoAddressing( 7, 1, Keys::Key_MCT_LLPChannel4Len));

    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(FuncRead_LLPStatusPos,addressSet));

    addressSet.clear();

    // FuncRead_DisconnectConfigPos
    addressSet.insert(DynamicPaoAddressing( 5, 2, Keys::Key_MCT_DemandThreshold));
    addressSet.insert(DynamicPaoAddressing( 7, 1, Keys::Key_MCT_ConnectDelay));
    addressSet.insert(DynamicPaoAddressing( 9, 1, Keys::Key_MCT_DisconnectMinutes));
    addressSet.insert(DynamicPaoAddressing(10, 1, Keys::Key_MCT_ConnectMinutes));

    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(FuncRead_DisconnectConfigPos,addressSet));

    return functionSet;
}

//Function returns first address after the given address and the data associated with that address
void CtiDeviceMCT410::getDynamicPaoAddressing(int address, int &foundAddress, int &foundLength, CtiTableDynamicPaoInfo::Keys &foundKey)
{
    foundAddress = 0;
    foundLength = 0;
    foundKey = Keys::Key_Invalid;//If nothing happens, this is what we want.

    DynamicPaoAddressing tempDynAddr(address, 0, Keys::Key_Invalid);

    DynamicPaoAddressing_t::const_iterator iter;
    if((iter = _dynPaoAddressing.find(tempDynAddr)) != _dynPaoAddressing.end())
    {
        foundAddress = iter->address;
        foundLength  = iter->length;
        foundKey     = iter->key;
    }
    else if((iter = _dynPaoAddressing.upper_bound(tempDynAddr)) != _dynPaoAddressing.end())
    {
        foundAddress = iter->address;
        foundLength  = iter->length;
        foundKey     = iter->key;
    }
}

void CtiDeviceMCT410::getDynamicPaoFunctionAddressing(int function, int address, int &foundAddress, int &foundLength, CtiTableDynamicPaoInfo::Keys &foundKey)
{
    foundAddress = 0;
    foundLength  = 0;
    foundKey     = Keys::Key_Invalid;//If nothing happens, this is what we want.

    DynamicPaoAddressing tempDynAddr(address, 0, Keys::Key_Invalid);

    DynamicPaoFunctionAddressing_t::const_iterator funcIter;
    if((funcIter = _dynPaoFuncAddressing.find(function)) != _dynPaoFuncAddressing.end())
    {
        DynamicPaoAddressing_t::const_iterator addressIter;
        if((addressIter = funcIter->second.find(tempDynAddr)) != funcIter->second.end())
        {
            foundAddress = addressIter->address;
            foundLength  = addressIter->length;
            foundKey     = addressIter->key;
        }
        else if((addressIter = funcIter->second.upper_bound(tempDynAddr)) != funcIter->second.end())
        {
            foundAddress = addressIter->address;
            foundLength  = addressIter->length;
            foundKey     = addressIter->key;
        }
    }
}

CtiDeviceMCT410::ConfigPartsList CtiDeviceMCT410::initConfigParts()
{
    CtiDeviceMCT410::ConfigPartsList tempList;

    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_dst);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_vthreshold);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_demand_lp);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_options);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_addressing);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_disconnect);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_holiday);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_llp);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_usage);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_centron);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_tou);

    return tempList;
}


CtiDeviceMCT410::ConfigPartsList CtiDeviceMCT410::getPartsList()
{
    return _config_parts;
}


CtiDeviceMCT410::point_info CtiDeviceMCT410::getLoadProfileData(unsigned channel, unsigned char *buf, unsigned len)
{
    point_info pi;

    //  input channel is 0-based, enums are 1-based
    if( (channel + 1) == Channel_Voltage )
    {
        pi = getData(buf, len, ValueType_LoadProfile_Voltage);
    }
    else
    {
        pi = getData(buf, len, ValueType_LoadProfile_Demand);
        pi.value *= 3600 / getLoadProfileInterval(channel);
    }

    return pi;
}


CtiDeviceMCT410::CommandSet CtiDeviceMCT410::initCommandStore()
{
    CommandSet cs;

    cs.insert(CommandStore(Emetcon::Scan_Accum,                     Emetcon::IO_Function_Read, FuncRead_MReadPos,           FuncRead_MReadLen));
    cs.insert(CommandStore(Emetcon::GetValue_KWH,                   Emetcon::IO_Function_Read, FuncRead_MReadPos,           FuncRead_MReadLen));
    cs.insert(CommandStore(Emetcon::GetValue_FrozenKWH,             Emetcon::IO_Function_Read, FuncRead_FrozenMReadPos,     FuncRead_FrozenMReadLen));
    cs.insert(CommandStore(Emetcon::Scan_Integrity,                 Emetcon::IO_Function_Read, FuncRead_DemandPos,          FuncRead_DemandLen));
    cs.insert(CommandStore(Emetcon::Scan_LoadProfile,               Emetcon::IO_Function_Read, 0,                           0));
    cs.insert(CommandStore(Emetcon::GetValue_LoadProfile,           Emetcon::IO_Function_Read, 0,                           0));
    cs.insert(CommandStore(Emetcon::GetValue_LoadProfilePeakReport, Emetcon::IO_Function_Read, 0,                           0));
    cs.insert(CommandStore(Emetcon::GetValue_Demand,                Emetcon::IO_Function_Read, FuncRead_DemandPos,          FuncRead_DemandLen));
    cs.insert(CommandStore(Emetcon::GetValue_PeakDemand,            Emetcon::IO_Function_Read, FuncRead_PeakDemandPos,      FuncRead_PeakDemandLen));
    cs.insert(CommandStore(Emetcon::GetValue_FrozenPeakDemand,      Emetcon::IO_Function_Read, FuncRead_FrozenPos,          FuncRead_FrozenLen));
    cs.insert(CommandStore(Emetcon::GetValue_Voltage,               Emetcon::IO_Function_Read, FuncRead_VoltagePos,         FuncRead_VoltageLen));
    cs.insert(CommandStore(Emetcon::GetValue_FrozenVoltage,         Emetcon::IO_Function_Read, FuncRead_FrozenVoltagePos,   FuncRead_FrozenVoltageLen));
    cs.insert(CommandStore(Emetcon::GetValue_Outage,                Emetcon::IO_Function_Read, FuncRead_OutagePos,          FuncRead_OutageLen));
    cs.insert(CommandStore(Emetcon::GetStatus_Internal,             Emetcon::IO_Read,          Memory_StatusPos,            Memory_StatusLen));
    cs.insert(CommandStore(Emetcon::GetStatus_LoadProfile,          Emetcon::IO_Function_Read, FuncRead_LPStatusPos,        FuncRead_LPStatusLen));

    //  These need to be duplicated from DeviceMCT because the 400 doesn't need the ARML.
    cs.insert(CommandStore(Emetcon::Control_Connect,            Emetcon::IO_Write,          Command_Connect,                0));
    cs.insert(CommandStore(Emetcon::Control_Disconnect,         Emetcon::IO_Write,          Command_Disconnect,             0));

    cs.insert(CommandStore(Emetcon::GetStatus_Disconnect,       Emetcon::IO_Function_Read,  FuncRead_DisconnectStatusPos,   FuncRead_DisconnectStatusLen));
    cs.insert(CommandStore(Emetcon::GetConfig_Disconnect,       Emetcon::IO_Function_Read,  FuncRead_DisconnectConfigPos,   FuncRead_DisconnectConfigLen));
    cs.insert(CommandStore(Emetcon::PutConfig_Disconnect,       Emetcon::IO_Function_Write, FuncWrite_DisconnectConfigPos,  FuncWrite_DisconnectConfigLen));
    cs.insert(CommandStore(Emetcon::PutConfig_Raw,              Emetcon::IO_Write,          0,                              0));  //  filled in later
    cs.insert(CommandStore(Emetcon::GetConfig_TSync,            Emetcon::IO_Read,           Memory_LastTSyncPos,            Memory_LastTSyncLen));
    cs.insert(CommandStore(Emetcon::GetConfig_Time,             Emetcon::IO_Read,           Memory_TimeZoneOffsetPos,       Memory_TimeZoneOffsetLen +
                                                                                                                            Memory_RTCLen));
    cs.insert(CommandStore(Emetcon::PutConfig_TimeZoneOffset,   Emetcon::IO_Write,          Memory_TimeZoneOffsetPos,       Memory_TimeZoneOffsetLen));
    cs.insert(CommandStore(Emetcon::PutConfig_Intervals,        Emetcon::IO_Function_Write, FuncWrite_IntervalsPos,         FuncWrite_IntervalsLen));
    cs.insert(CommandStore(Emetcon::GetConfig_Intervals,        Emetcon::IO_Read,           Memory_IntervalsPos,            Memory_IntervalsLen));
    cs.insert(CommandStore(Emetcon::GetValue_PFCount,           Emetcon::IO_Read,           Memory_PowerfailCountPos,       Memory_PowerfailCountLen));
    cs.insert(CommandStore(Emetcon::PutStatus_Reset,            Emetcon::IO_Write,          Command_Reset,                  0));
    cs.insert(CommandStore(Emetcon::PutStatus_FreezeOne,        Emetcon::IO_Write,          Command_FreezeOne,              0));
    cs.insert(CommandStore(Emetcon::PutStatus_FreezeTwo,        Emetcon::IO_Write,          Command_FreezeTwo,              0));
    cs.insert(CommandStore(Emetcon::PutStatus_FreezeVoltageOne, Emetcon::IO_Write,          Command_FreezeVoltageOne,       0));
    cs.insert(CommandStore(Emetcon::PutStatus_FreezeVoltageTwo, Emetcon::IO_Write,          Command_FreezeVoltageTwo,       0));

    //******************************** Config Related starts here *************************
    cs.insert(CommandStore(Emetcon::PutConfig_Addressing,       Emetcon::IO_Write,          Memory_AddressingPos,           Memory_AddressingLen));
    cs.insert(CommandStore(Emetcon::PutConfig_LongloadProfile,  Emetcon::IO_Function_Write, FuncWrite_LLPStoragePos,        FuncWrite_LLPStorageLen));
    cs.insert(CommandStore(Emetcon::GetConfig_LongloadProfile,  Emetcon::IO_Function_Read,  FuncRead_LLPStatusPos,          FuncRead_LLPStatusLen));

    cs.insert(CommandStore(Emetcon::PutConfig_DST,              Emetcon::IO_Write,          Memory_DSTBeginPos,             Memory_DSTBeginLen +
                                                                                                                            Memory_DSTEndLen   +
                                                                                                                            Memory_TimeZoneOffsetLen));

    cs.insert(CommandStore(Emetcon::PutConfig_VThreshold,       Emetcon::IO_Write,          Memory_OverVThresholdPos,       Memory_OverVThresholdLen +
                                                                                                                            Memory_UnderVThresholdLen));
    //  used by both the putconfig install and putconfig holiday commands
    cs.insert(CommandStore(Emetcon::PutConfig_Holiday,          Emetcon::IO_Write,          Memory_Holiday1Pos,             Memory_Holiday1Len +
                                                                                                                            Memory_Holiday2Len +
                                                                                                                            Memory_Holiday3Len));
    cs.insert(CommandStore(Emetcon::GetConfig_Holiday,          Emetcon::IO_Read,           Memory_Holiday1Pos,             Memory_Holiday1Len +
                                                                                                                            Memory_Holiday2Len +
                                                                                                                            Memory_Holiday3Len));
    cs.insert(CommandStore(Emetcon::PutConfig_Options,          Emetcon::IO_Write,          FuncWrite_ConfigAlarmMaskPos,   FuncWrite_ConfigAlarmMaskLen));
    cs.insert(CommandStore(Emetcon::PutConfig_Outage,           Emetcon::IO_Write,          Memory_OutageCyclesPos,         Memory_OutageCyclesLen));
    cs.insert(CommandStore(Emetcon::PutConfig_TimeAdjustTolerance, Emetcon::IO_Write,       Memory_TimeAdjustTolPos,        Memory_TimeAdjustTolLen));

    //************************************ End Config related *****************************

    return cs;
}


long CtiDeviceMCT410::getLoadProfileInterval( unsigned channel )
{
    int retval;

    //  input channel is 0-based, enums are 1-based
    channel++;

    if(  channel == Channel_Voltage )  retval = getLoadProfile().getVoltageProfileRate();
    else                               retval = getLoadProfile().getLoadProfileDemandRate();

    return retval;
}


bool CtiDeviceMCT410::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    CommandSet::const_iterator itr = _commandStore.find( CommandStore( cmd ) );

    if( itr != _commandStore.end( ) )
    {
        function = itr->function;   //  Copy the relevant bits from the commandStore
        length   = itr->length;     //
        io       = itr->io;         //

        found = true;
    }
    else    //  Look in the parent if not found in the child
    {
        found = Inherited::getOperation( cmd, function, length, io );
    }

    return found;
}


ULONG CtiDeviceMCT410::calcNextLPScanTime( void )
{
    CtiTime       Now;
    unsigned long next_time, planned_time;
    int           interval_len, block_len;

    next_time = YUKONEOT;

    if( !_intervalsSent )
    {
        //  send load profile interval on the next 5 minute boundary
        next_time  = (Now.seconds() - LoadProfileCollectionWindow) + 300;

        next_time -= next_time % 300;
    }
    else
    {
        for( int i = 0; i < LPChannels; i++ )
        {
            interval_len = getLoadProfileInterval(i);
            block_len    = interval_len * 6;

            CtiPointSPtr pPoint = getDevicePointOffsetTypeEqual((i + 1) + PointOffset_LoadProfileOffset, DemandAccumulatorPointType);

            //  if we're not collecting load profile, or there's no point defined, don't scan
            if( !getLoadProfile().isChannelValid(i) || !pPoint )
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

                CtiTablePointDispatch pd(pPoint->getPointID());

                if(pd.Restore().errorCode() == RWDBStatus::ok)
                {
                    _lp_info[i].archived_reading = pd.getTimeStamp().seconds();
                }
            }

            //  basically, we plan to request again after a whole block has been recorded...
            //    then we add on a little bit to make sure the MCT is out of the memory
            planned_time  = _lp_info[i].archived_reading + block_len;
            planned_time -= planned_time % block_len;  //  make double sure we're block-aligned
            planned_time += LPBlockEvacuationTime;      //  add on the safeguard time

            if( getMCTDebugLevel(DebugLevel_LoadProfile) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint - lp calctime check... **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "planned_time = " << planned_time << endl;
                dout << "_lp_info[" << i << "].archived_reading = " << _lp_info[i].archived_reading << endl;
                dout << "_lp_info[" << i << "].current_schedule = " << _lp_info[i].current_schedule << endl;
                dout << "_lp_info[" << i << "].current_request  = " << _lp_info[i].current_request << endl;
            }

            _lp_info[i].current_schedule = planned_time;

            //  if we've already made the request for a block, or we're overdue...  almost the same thing
            if( (_lp_info[i].current_request >= _lp_info[i].archived_reading) ||
                (planned_time <= (Now.seconds() - LoadProfileCollectionWindow)) )
            {
                unsigned int overdue_rate = getLPRetryRate(interval_len);

                _lp_info[i].current_schedule  = (Now.seconds() - LPBlockEvacuationTime) + overdue_rate;
                _lp_info[i].current_schedule -= (Now.seconds() - LPBlockEvacuationTime) % overdue_rate;

                _lp_info[i].current_schedule += LPBlockEvacuationTime;
            }

            if( next_time > _lp_info[i].current_schedule )
            {
                next_time = _lp_info[i].current_schedule;
            }

            if( getMCTDebugLevel(DebugLevel_LoadProfile) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint - lp calctime check... **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "planned_time = " << planned_time << endl;
                dout << "_lp_info[" << i << "].archived_reading = " << _lp_info[i].archived_reading << endl;
                dout << "_lp_info[" << i << "].current_schedule = " << _lp_info[i].current_schedule << endl;
                dout << "_lp_info[" << i << "].current_request  = " << _lp_info[i].current_request << endl;
                dout << "next_time = " << next_time << endl;
            }
        }
    }

    if( getMCTDebugLevel(DebugLevel_LoadProfile) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << getName() << "'s next Load Profile request at " << CtiTime(next_time) << endl;
    }

    return (_nextLPScanTime = next_time);
}


void CtiDeviceMCT410::sendIntervals( OUTMESS *&OutMessage, list< OUTMESS* > &outList )
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

    outList.push_back(OutMessage);
    OutMessage = NULL;
}


INT CtiDeviceMCT410::calcAndInsertLPRequests(OUTMESS *&OutMessage, list< OUTMESS* > &outList)
{
    int nRet = NoError;

    CtiTime       Now;
    unsigned int  interval_len, block_len;
    int           channel, block;
    string        descriptor;
    OUTMESS      *tmpOutMess;

    if( !_intervalsSent )
    {
        sendIntervals(OutMessage, outList);

        _intervalsSent = true;
    }
    else if( !useScanFlags() )
    {
        if( getMCTDebugLevel(DebugLevel_LoadProfile) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - calcAndInsetLPRequests() called from outside Scanner for device \"" << getName() << "\", ignoring **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else
    {
        for( int i = 0; i < LPChannels; i++ )
        {
            interval_len = getLoadProfileInterval(i);
            block_len    = interval_len * 6;

            if( getLoadProfile().isChannelValid(i) )
            {
                if( _lp_info[i].current_schedule <= Now )
                {
                    tmpOutMess = CTIDBG_new OUTMESS(*OutMessage);

                    _lp_info[i].current_request  = _lp_info[i].archived_reading;

                    //  make sure we only ask for what the function reads can access
                    if( (Now.seconds() - _lp_info[i].current_request) >= (unsigned long)(LPRecentBlocks * block_len) )
                    {
                        //  go back as far as we can
                        _lp_info[i].current_request  = Now.seconds();
                        _lp_info[i].current_request -= LPRecentBlocks * block_len;
                    }

                    if( getMCTDebugLevel(DebugLevel_LoadProfile) )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - LP variable check for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Now.seconds() = " << Now.seconds() << endl;
                        dout << "_lp_info[" << i << "].archived_reading = " << _lp_info[i].archived_reading << endl;
                        dout << "MCT4XX_LPRecentBlocks * block_len = " << LPRecentBlocks * block_len << endl;
                        dout << "_lp_info[" << i << "].current_request = " << _lp_info[i].current_request << endl;
                    }

                    //  make sure we're aligned
                    _lp_info[i].current_request -= _lp_info[i].current_request % block_len;

                    //  which block to grab?
                    channel = i + 1;
                    block   = (Now.seconds() - _lp_info[i].current_request) / block_len;

                    descriptor = " channel " + CtiNumStr(channel) + string(" block ") + CtiNumStr(block);

                    strncat( tmpOutMess->Request.CommandStr,
                             descriptor.c_str(),
                             sizeof(tmpOutMess->Request.CommandStr) - ::strlen(tmpOutMess->Request.CommandStr));

                    if( getMCTDebugLevel(DebugLevel_LoadProfile) )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - command string check for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "\"" << tmpOutMess->Request.CommandStr << "\"" << endl;
                    }

                    outList.push_back(tmpOutMess);
                }
            }
        }

        if( outList.empty() )
        {
            if( getMCTDebugLevel(DebugLevel_LoadProfile) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - LP scan too early for device \"" << getName() << "\", no OutMessages created **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

    if( getMCTDebugLevel(DebugLevel_LoadProfile) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - LP parse value check **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "parse.getiValue(\"scan_loadprofile_block\",   0) = " << parse.getiValue("scan_loadprofile_block", 0) << endl;
        dout << "parse.getiValue(\"scan_loadprofile_channel\", 0) = " << parse.getiValue("scan_loadprofile_channel", 0) << endl;
    }

    block   = parse.getiValue("scan_loadprofile_block",   0);
    channel = parse.getiValue("scan_loadprofile_channel", 0);

    if( block && channel && block <= LPRecentBlocks && channel <= LPChannels )
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
        if( getMCTDebugLevel(DebugLevel_LoadProfile) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - Improperly formed LP request discarded for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;;
        }

        retVal = false;
    }

    return retVal;
}


/*
 *  ModelDecode MUST decode all CtiDLCCommand_t which are defined in the initCommandStore object.  The only exception to this
 *  would be a child whose decode was identical to the parent, but whose request was done differently..
 *  This MAY be the case for example in an IED scan.
 */
INT CtiDeviceMCT410::ModelDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    int ioType, location;
    if( restoreMessageRead(InMessage, ioType, location) )
    {
        int foundAddress, foundLength = 0;
        CtiTableDynamicPaoInfo::Keys foundKey = CtiTableDynamicPaoInfo::Key_Invalid;

         //ioType and location were set by the function.
        if(ioType == Emetcon::IO_Read)
        {
            int searchLocation = location;

            do
            {
                getDynamicPaoAddressing(searchLocation, foundAddress, foundLength, foundKey);

                if( foundAddress >= 0 && foundLength > 0 && foundKey != CtiTableDynamicPaoInfo::Key_Invalid
                   && (foundAddress - location + foundLength) <= InMessage->Buffer.DSt.Length && foundLength <=8)
                {
                    unsigned long value = 0;
                    for( int i=0; i < foundLength; i++ )
                    {
                        value += (((unsigned int)InMessage->Buffer.DSt.Message[(foundAddress-location+foundLength-1)-i]) << (i*8));
                    }
                    CtiDeviceBase::setDynamicInfo(foundKey, value);
                    searchLocation = foundAddress+1;
                }
                else
                {
                    foundKey = CtiTableDynamicPaoInfo::Key_Invalid;
                }

            }while( foundKey != CtiTableDynamicPaoInfo::Key_Invalid );
        }
        else if( ioType == Emetcon::IO_Function_Read )
        {
            int searchLocation = 0;

            do
            {
                //Note that this does not currently take into account SSpec based reads which can have varying length.
                //It is assumed that SSPec changes do NOT change the order of recieved bytes.
                getDynamicPaoFunctionAddressing(location, searchLocation, foundAddress, foundLength, foundKey);

                if( foundAddress >= 0 && foundLength > 0 && foundKey != CtiTableDynamicPaoInfo::Key_Invalid &&
                    (searchLocation + foundLength) <= InMessage->Buffer.DSt.Length && foundLength <=8 )
                {
                    unsigned long value = 0;
                    for( int i=0; i<foundLength; i++)
                    {
                        value += (((unsigned int)InMessage->Buffer.DSt.Message[(foundAddress + foundLength-1)-i]) << (i*8));
                    }
                    CtiDeviceBase::setDynamicInfo(foundKey, value);
                    searchLocation = foundAddress+1;
                }
                else
                {
                    foundKey = CtiTableDynamicPaoInfo::Key_Invalid;
                }

            }while( foundKey != CtiTableDynamicPaoInfo::Key_Invalid );
        }
    }

    switch(InMessage->Sequence)
    {
        case Emetcon::Scan_Accum:
        case Emetcon::GetValue_KWH:
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

        case Emetcon::GetValue_FreezeCounter:
        {
            status = decodeGetValueFreezeCounter(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetValue_LoadProfilePeakReport:
        {
            status = decodeGetValueLoadProfilePeakReport(InMessage, TimeNow, vgList, retList, outList);
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

        case (Emetcon::GetConfig_Multiplier):
        case (Emetcon::GetConfig_CentronParameters):
        {
            status = decodeGetConfigCentron(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::Control_Connect):
        case (Emetcon::Control_Disconnect):
        {
            CtiReturnMsg *ReturnMsg;

            CtiRequestMsg newReq(getID(),
                                 "getstatus disconnect noqueue",
                                 InMessage->Return.UserID,
                                 0,
                                 InMessage->Return.RouteID,
                                 InMessage->Return.MacroOffset,
                                 0,
                                 0,
                                 InMessage->Priority);

            newReq.setConnectionHandle((void *)InMessage->Return.Connection);

            CtiDeviceBase::ExecuteRequest(&newReq, CtiCommandParser(newReq.CommandString()), vgList, retList, outList);

            break;
        }

        case (Emetcon::GetValue_PFCount):
        default:
        {
            status = Inherited::ModelDecode(InMessage, TimeNow, vgList, retList, outList);

            if(status != NORMAL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " IM->Sequence = " << InMessage->Sequence << " " << getName() << endl;
            }
            break;
        }
    }


    return status;
}


INT CtiDeviceMCT410::executePutConfig( CtiRequestMsg              *pReq,
                                       CtiCommandParser           &parse,
                                       OUTMESS                   *&OutMessage,
                                       list< CtiMessage* >  &vgList,
                                       list< CtiMessage* >  &retList,
                                       list< OUTMESS* >     &outList )
{
    INT nRet = NoMethod;

    bool found = false;
    int function = -1;

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID(),
                                                   string(OutMessage->Request.CommandStr),
                                                   string(),
                                                   nRet,
                                                   OutMessage->Request.RouteID,
                                                   OutMessage->Request.MacroOffset,
                                                   OutMessage->Request.Attempt,
                                                   OutMessage->Request.TrxID,
                                                   OutMessage->Request.UserID,
                                                   OutMessage->Request.SOE,
                                                   CtiMultiMsg_vec());

    //  Load all the other stuff that is needed
    //    we're doing this up here so that the TOU writes can copy a fully-formed outmessage
    OutMessage->DeviceID  = getID();
    OutMessage->TargetID  = getID();
    OutMessage->Port      = getPortID();
    OutMessage->Remote    = getAddress();
    OutMessage->TimeOut   = 2;
    OutMessage->Retry     = 2;

    OutMessage->Request.RouteID   = getRouteID();
    strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

    if( parse.isKeyValid("holiday_offset") )
    {
        function = Emetcon::PutConfig_Holiday;

        if( found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO) )
        {
            unsigned long holidays[3];
            int holiday_count = 0;

            int holiday_offset = parse.getiValue("holiday_offset");

            OutMessage->Sequence = Cti::Protocol::Emetcon::PutConfig_Holiday;

            //  grab up to three potential dates
            for( int i = 0; i < 3 && parse.isKeyValid("holiday_date" + CtiNumStr(i)); i++ )
            {
                CtiTokenizer date_tokenizer(parse.getsValue("holiday_date" + CtiNumStr(i)));

                int month = atoi(date_tokenizer("/").data()),
                    day   = atoi(date_tokenizer("/").data()),
                    year  = atoi(date_tokenizer("/").data());

                if( year > 2000 )
                {
                    CtiDate holiday_date(day, month, year);

                    if( holiday_date.isValid() && holiday_date > CtiDate::now() )
                    {
                        holidays[holiday_count++] = CtiTime(holiday_date).seconds();
                    }
                }
            }

            if( holiday_offset >= 1 && holiday_offset <= 3 )
            {
                if( holiday_count > 0 )
                {
                    //  change to 0-based offset;  it just makes things easier
                    holiday_offset--;

                    if( holiday_count > (3 - holiday_offset) )
                    {
                        holiday_count = 3 - holiday_offset;
                    }

                    OutMessage->Buffer.BSt.Function += holiday_offset * 4;
                    OutMessage->Buffer.BSt.Length    = holiday_count  * 4;

                    for( int i = 0; i < holiday_count; i++ )
                    {
                        OutMessage->Buffer.BSt.Message[i*4+0] = holidays[i] >> 24;
                        OutMessage->Buffer.BSt.Message[i*4+1] = holidays[i] >> 16;
                        OutMessage->Buffer.BSt.Message[i*4+2] = holidays[i] >>  8;
                        OutMessage->Buffer.BSt.Message[i*4+3] = holidays[i] >>  0;
                    }
                }
                else
                {
                    found = false;

                    if( errRet )
                    {
                        errRet->setResultString("Specified dates are invalid");
                        errRet->setStatus(NoMethod);
                        retList.push_back(errRet);

                        errRet = NULL;
                    }
                }
            }
            else
            {
                found = false;

                if( errRet )
                {
                    errRet->setResultString("Invalid holiday offset specified");
                    errRet->setStatus(NoMethod);
                    retList.push_back(errRet);

                    errRet = NULL;
                }
            }
        }
    }
    else if( parse.isKeyValid("centron_ratio") )
    {
        //  these Centron guys are very specialized writes, so we won't put them in the command store at the
        //    moment - at least not until we get a better command store than the Cti::Protocol::Emetcon:: thing...
        //  it's too flat, too much is exposed

        int centron_ratio = parse.getiValue("centron_ratio");

        if( centron_ratio > 0 && centron_ratio <= 255 )
        {
            OutMessage->Sequence = Cti::Protocol::Emetcon::PutConfig_Multiplier;

            OutMessage->Buffer.BSt.Function = FuncWrite_CentronParametersPos;
            OutMessage->Buffer.BSt.Length   = FuncWrite_CentronParametersLen;

            OutMessage->Buffer.BSt.IO = Cti::Protocol::Emetcon::IO_Function_Write;

            OutMessage->Buffer.BSt.Message[0] = gMCT400SeriesSPID;
            OutMessage->Buffer.BSt.Message[1] = 0x00;  //  default, see sspec for details
            OutMessage->Buffer.BSt.Message[2] = (unsigned char)parse.getiValue("centron_ratio");

            //  hackabout because we don't have the command store
            found = true;
        }
        else
        {
            found = false;

            if( errRet )
            {
                errRet->setResultString("Invalid Centron multiplier (" + CtiNumStr(centron_ratio) + ")");
                errRet->setStatus(NoMethod);
                retList.push_back(errRet);

                errRet = NULL;
            }
        }
    }
    else if( parse.isKeyValid("centron_reading_forward") )
    {
        double reading_forward = parse.getdValue("centron_reading_forward"),
               reading_reverse = parse.getdValue("centron_reading_reverse");

        long   pulses_forward,
               pulses_reverse;

        shared_ptr<CtiPointNumeric> tmpPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(1, PulseAccumulatorPointType));

        if( tmpPoint )
        {
            //  adjust for the multiplier, if the point exists
            pulses_forward = (long)((reading_forward * 100.0) / tmpPoint->getMultiplier());
            pulses_reverse = (long)((reading_reverse * 100.0) / tmpPoint->getMultiplier());
        }
        else
        {
            pulses_forward = (long)(reading_forward * 1000.0);
            pulses_reverse = (long)(reading_reverse * 1000.0);
        }

        OutMessage->Sequence = Cti::Protocol::Emetcon::PutValue_KYZ;

        OutMessage->Buffer.BSt.Function = FuncWrite_CentronReadingPos;
        OutMessage->Buffer.BSt.Length   = FuncWrite_CentronReadingLen;

        OutMessage->Buffer.BSt.IO = Cti::Protocol::Emetcon::IO_Function_Write;

        OutMessage->Buffer.BSt.Message[0] = (pulses_forward >> 24) & 0xff;
        OutMessage->Buffer.BSt.Message[1] = (pulses_forward >> 16) & 0xff;
        OutMessage->Buffer.BSt.Message[2] = (pulses_forward >>  8) & 0xff;
        OutMessage->Buffer.BSt.Message[3] =  pulses_forward        & 0xff;

        OutMessage->Buffer.BSt.Message[4] = (pulses_reverse >> 24) & 0xff;
        OutMessage->Buffer.BSt.Message[5] = (pulses_reverse >> 16) & 0xff;
        OutMessage->Buffer.BSt.Message[6] = (pulses_reverse >>  8) & 0xff;
        OutMessage->Buffer.BSt.Message[7] =  pulses_reverse        & 0xff;

        found = true;
    }
    else
    {
        nRet = Inherited::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }

    if( found )
    {
        nRet = NoError;
    }

    if( errRet )
    {
        delete errRet;
    }

    return nRet;
}



INT CtiDeviceMCT410::executeGetValue( CtiRequestMsg              *pReq,
                                      CtiCommandParser           &parse,
                                      OUTMESS                   *&OutMessage,
                                      list< CtiMessage* >  &vgList,
                                      list< CtiMessage* >  &retList,
                                      list< OUTMESS* >     &outList )
{
    INT nRet = NoMethod;

    bool found = false;
    int function;

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                   string(OutMessage->Request.CommandStr),
                                                   string(),
                                                   nRet,
                                                   OutMessage->Request.RouteID,
                                                   OutMessage->Request.MacroOffset,
                                                   OutMessage->Request.Attempt,
                                                   OutMessage->Request.TrxID,
                                                   OutMessage->Request.UserID,
                                                   OutMessage->Request.SOE,
                                                   CtiMultiMsg_vec( ));

    if( (parse.getFlags() &  CMD_FLAG_GV_KWH) &&
        (parse.getFlags() & (CMD_FLAG_GV_RATEMASK ^ CMD_FLAG_GV_RATET)) )
    {
        //  if it's a KWH request for rate ABCD - rate T should fall through to a normal KWH request

        function = Emetcon::GetValue_TOU;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        if( parse.getFlags() & CMD_FLAG_FROZEN )    OutMessage->Buffer.BSt.Function += FuncRead_TOUFrozenOffset;

        //  no need to increment for rate A
        if( parse.getFlags() & CMD_FLAG_GV_RATEB )  OutMessage->Buffer.BSt.Function += 1;
        if( parse.getFlags() & CMD_FLAG_GV_RATEC )  OutMessage->Buffer.BSt.Function += 2;
        if( parse.getFlags() & CMD_FLAG_GV_RATED )  OutMessage->Buffer.BSt.Function += 3;
    }
    else if( parse.isKeyValid("freeze_counter") )
    {
        found = true;

        function = Emetcon::GetValue_FreezeCounter;

        OutMessage->Buffer.BSt.Function = 0x2a;
        OutMessage->Buffer.BSt.Length   = 1;
        OutMessage->Buffer.BSt.IO       = Emetcon::IO_Read;
    }
    else if( parse.isKeyValid("outage") )  //  outages
    {
        if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec) )
        {
            //  we need to set it to the requested interval
            CtiOutMessage *sspec_om = new CtiOutMessage(*OutMessage);

            if( sspec_om )
            {
                getOperation(Emetcon::GetConfig_Model, sspec_om->Buffer.BSt.Function, sspec_om->Buffer.BSt.Length, sspec_om->Buffer.BSt.IO);

                sspec_om->Sequence = Emetcon::GetConfig_Model;

                sspec_om->MessageFlags |= MessageFlag_ExpectMore;

                outList.push_back(sspec_om);
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
                string temp = "Bad outage specification - Acceptable values:  1-6";
                errRet->setResultString( temp );
                errRet->setStatus(NoMethod);
                retList.push_back( errRet );
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
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        nRet = NoError;
    }

    return nRet;
}

INT CtiDeviceMCT410::executeGetConfig( CtiRequestMsg              *pReq,
                                       CtiCommandParser           &parse,
                                       OUTMESS                   *&OutMessage,
                                       list< CtiMessage* >  &vgList,
                                       list< CtiMessage* >  &retList,
                                       list< OUTMESS* >     &outList )
{
    INT nRet = NoMethod;


    bool found = false;

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                   string(OutMessage->Request.CommandStr),
                                                   string(),
                                                   nRet,
                                                   OutMessage->Request.RouteID,
                                                   OutMessage->Request.MacroOffset,
                                                   OutMessage->Request.Attempt,
                                                   OutMessage->Request.TrxID,
                                                   OutMessage->Request.UserID,
                                                   OutMessage->Request.SOE,
                                                   CtiMultiMsg_vec( ));

    if( parse.isKeyValid("centron_ratio") )
    {
        found = true;

        OutMessage->Buffer.BSt.Function = Memory_CentronMultiplierPos;
        OutMessage->Buffer.BSt.Length   = Memory_CentronMultiplierLen;
        OutMessage->Buffer.BSt.IO       = Emetcon::IO_Read;

        OutMessage->Sequence = Emetcon::GetConfig_Multiplier;
    }
    else if( parse.isKeyValid("centron_parameters") )
    {
        found = true;

        OutMessage->Buffer.BSt.Function = Memory_CentronParametersPos;
        OutMessage->Buffer.BSt.Length   = Memory_CentronParametersLen;
        OutMessage->Buffer.BSt.IO       = Emetcon::IO_Read;

        OutMessage->Sequence = Emetcon::GetConfig_CentronParameters;
    }
    else
    {
        nRet = Inherited::executeGetConfig(pReq, parse, OutMessage, vgList, retList, outList);
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
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        nRet = NoError;
    }

    if( errRet )
    {
        delete errRet;
        errRet = 0;
    }

    return nRet;
}


int CtiDeviceMCT410::executePutConfigDemandLP(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTDemandLP);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTDemandLP )
        {
            long demand, loadProfile, voltageDemand, voltageLoadProfile;

            MCTDemandLoadProfileSPtr config = boost::static_pointer_cast< ConfigurationPart<MCTDemandLoadProfile> >(tempBasePtr);
            demand = config->getLongValueFromKey(DemandInterval);
            loadProfile = config->getLongValueFromKey(LoadProfileInterval);
            voltageDemand = config->getLongValueFromKey(VoltageDemandInterval);
            voltageLoadProfile = config->getLongValueFromKey(VoltageLPInterval);

            if( demand == numeric_limits<long>::min() || loadProfile == numeric_limits<long>::min() || voltageDemand == numeric_limits<long>::min() || voltageLoadProfile== numeric_limits<long>::min())
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if( parse.isKeyValid("force")
                    || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DemandInterval)        != demand
                    || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval)   != loadProfile
                    || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_VoltageLPInterval)     != voltageDemand
                    || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_VoltageDemandInterval) != voltageLoadProfile )
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        OutMessage->Buffer.BSt.Function   = FuncWrite_IntervalsPos;
                        OutMessage->Buffer.BSt.Length     = FuncWrite_IntervalsLen;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0] = (demand);
                        OutMessage->Buffer.BSt.Message[1] = (loadProfile);
                        OutMessage->Buffer.BSt.Message[2] = (voltageDemand);
                        OutMessage->Buffer.BSt.Message[3] = (voltageLoadProfile);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Function   = Memory_IntervalsPos;
                        OutMessage->Buffer.BSt.Length     = Memory_IntervalsLen;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                        OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                        OutMessage->Priority             += 1;//return to normal
                    }
                    else
                    {
                        nRet = ConfigNotCurrent;
                    }
                }
                else
                {
                    nRet = ConfigCurrent;
                }
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;

    return nRet;
}


int CtiDeviceMCT410::executePutConfigDisconnect(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{

    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTDisconnect);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTDisconnect )
        {
            long threshold, delay, cycleDisconnectMinutes, cycleConnectMinutes;

            MCTDisconnectSPtr config = boost::static_pointer_cast< ConfigurationPart<MCTDisconnect> >(tempBasePtr);
            threshold = config->getLongValueFromKey(DemandThreshold);
            delay = config->getLongValueFromKey(ConnectDelay);
            cycleDisconnectMinutes = config->getLongValueFromKey(CyclingDisconnectMinutes);
            cycleConnectMinutes = config->getLongValueFromKey(CyclingConnectMinutes);
            long revision = getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision);

            if( revision >= SspecRev_Disconnect_Min && revision < SspecRev_Disconnect_Cycle
                && (cycleDisconnectMinutes | cycleConnectMinutes) != 0
                && (cycleDisconnectMinutes | cycleConnectMinutes) != numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Cycle requires revision >= " << CtiNumStr((double)(SspecRev_Disconnect_Cycle) / 10.0, 1).toString() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << CtiTime() << " Device: " << getName() << " Revision: " << CtiNumStr((double)revision / 10.0, 1).toString() << endl;
            }

            if( revision >= SspecRev_Disconnect_Cycle
                && (cycleDisconnectMinutes == numeric_limits<long>::min() || cycleConnectMinutes == numeric_limits<long>::min()) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else if( threshold == numeric_limits<long>::min() || delay == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else if( revision > 0 && revision < SspecRev_Disconnect_Min )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Disconnect requires revision >= " << CtiNumStr((double)(SspecRev_Disconnect_Min) / 10.0, 1).toString() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << CtiTime() << " Device: " << getName() << " Revision: " << CtiNumStr((double)revision / 10.0, 1).toString() << endl;
                nRet = FNI;
            }
            else
            {
                if( parse.isKeyValid("force")
                    || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DemandThreshold) != threshold
                    || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_ConnectDelay)    != delay )
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        OutMessage->Buffer.BSt.Function   = FuncWrite_DisconnectConfigPos;
                        OutMessage->Buffer.BSt.Length     = FuncWrite_DisconnectConfigLen;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0] = (_disconnectAddress>>16);
                        OutMessage->Buffer.BSt.Message[1] = (_disconnectAddress>>8);
                        OutMessage->Buffer.BSt.Message[2] = (_disconnectAddress);
                        OutMessage->Buffer.BSt.Message[3] = (threshold>>8);
                        OutMessage->Buffer.BSt.Message[4] = (threshold);
                        OutMessage->Buffer.BSt.Message[5] = (delay);
                        //Seeing as these cost me nothing to send (2 c-words either way) I will always send them.
                        //Also note, if we are of a revision with no cycle, these will simply be ignored
                        OutMessage->Buffer.BSt.Message[6] = (cycleDisconnectMinutes == numeric_limits<long>::min() ? 0 : cycleDisconnectMinutes);
                        OutMessage->Buffer.BSt.Message[7] = (cycleConnectMinutes == numeric_limits<long>::min() ? 0 : cycleConnectMinutes);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Function   = FuncRead_DisconnectConfigPos;
                        OutMessage->Buffer.BSt.Length     = revision >= SspecRev_Disconnect_Cycle ? FuncRead_DisconnectConfigLen + 2 : FuncRead_DisconnectConfigLen;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Read;
                        OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                        OutMessage->Priority             += 1;//return to normal
                    }
                    else
                    {
                        nRet = ConfigNotCurrent;
                    }
                }
                else
                {
                    nRet = ConfigCurrent;
                }
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;

    return nRet;
}

int CtiDeviceMCT410::executePutConfigCentron(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;

    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTCentron);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTCentron )
        {
            long parameters, ratio, spid;

            MCTCentronSPtr config = boost::static_pointer_cast< ConfigurationPart<MCTCentron> >(tempBasePtr);
            parameters = config->getLongValueFromKey(CentronParameters);
            ratio = config->getLongValueFromKey(CentronTransformerRatio);
            spid = CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID);

            if( spid == numeric_limits<long>::min() )
            {
                //We dont have it in dynamic pao info yet, we will get it from the config tables
                BaseSPtr addressTempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTAddressing);

                if(addressTempBasePtr && addressTempBasePtr->getType() == ConfigTypeMCTAddressing)
                {
                    MCTAddressingSPtr addressConfig = boost::static_pointer_cast< ConfigurationPart<MCTAddressing> >(addressTempBasePtr);
                    spid = addressConfig->getLongValueFromKey(ServiceProviderID);
                }
            }

            if( parameters == numeric_limits<long>::min() || ratio == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if(parse.isKeyValid("force")
                   || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_CentronParameters) != parameters
                   || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_CentronRatio)      != ratio )
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        OutMessage->Buffer.BSt.Function   = FuncWrite_CentronParametersPos;
                        OutMessage->Buffer.BSt.Length     = FuncWrite_CentronParametersLen;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0] = (spid);
                        OutMessage->Buffer.BSt.Message[1] = (parameters);
                        OutMessage->Buffer.BSt.Message[2] = (ratio);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Function   = Memory_CentronParametersPos;
                        OutMessage->Buffer.BSt.Length     = Memory_CentronParametersLen;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                        OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Function   = Memory_CentronMultiplierPos;
                        OutMessage->Buffer.BSt.Length     = Memory_CentronMultiplierLen;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                        OutMessage->Priority             += 1;//return to normal
                    }
                    else
                    {
                        nRet = ConfigNotCurrent;
                    }
                }
                else
                {
                    nRet = ConfigCurrent;
                }
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;

    return nRet;
}

int CtiDeviceMCT410::executePutConfigOptions(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTOptions);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTOptions )
        {
            long options, configuration, outage, timeAdjustTolerance, event1mask, event2mask, meterAlarmMask;
            USHORT function, length, io;

            MCTOptionsSPtr config = boost::static_pointer_cast< ConfigurationPart<MCTOptions> >(tempBasePtr);
            options = config->getLongValueFromKey(Options);
            event1mask = config->getLongValueFromKey(AlarmMaskEvent1);
            event2mask = config->getLongValueFromKey(AlarmMaskEvent2);
            meterAlarmMask = config->getLongValueFromKey(AlarmMaskMeter);
            configuration = config->getLongValueFromKey(Configuration);
            outage = config->getLongValueFromKey(OutageCycles);
            timeAdjustTolerance = config->getLongValueFromKey(TimeAdjustTolerance);

            if( !getOperation(Emetcon::PutConfig_Options, function, length, io) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Operation PutConfig_Options not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            if( options == numeric_limits<long>::min() || configuration == numeric_limits<long>::min() || event1mask == numeric_limits<long>::min()
                || event2mask == numeric_limits<long>::min() || meterAlarmMask == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Necessary data not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if( parse.isKeyValid("force")
                    || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Options)         != options
                    || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1) != event1mask
                    || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2) != event2mask
                    || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask)  != meterAlarmMask
                    || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration)   != configuration )
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        OutMessage->Buffer.BSt.Function   = function;
                        OutMessage->Buffer.BSt.Length     = length;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0] = (configuration);
                        OutMessage->Buffer.BSt.Message[1] = (event1mask);
                        OutMessage->Buffer.BSt.Message[2] = (event2mask);
                        OutMessage->Buffer.BSt.Message[3] = (meterAlarmMask>>8);
                        OutMessage->Buffer.BSt.Message[4] = (meterAlarmMask);
                        OutMessage->Buffer.BSt.Message[5] = (options);
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                        OutMessage->Buffer.BSt.Function   = Memory_OptionsPos;
                        OutMessage->Buffer.BSt.Length     = Memory_OptionsLen + Memory_ConfigurationLen;
                        OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Function   = Memory_EventFlagsMask1Pos;
                        OutMessage->Buffer.BSt.Length     = Memory_EventFlagsMask1Len + Memory_EventFlagsMask2Len + Memory_MeterAlarmMaskLen;
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                        OutMessage->Priority             += 1;//return to normal
                    }
                    else
                    {
                        nRet = ConfigNotCurrent;
                    }
                }
                else if( nRet == NORMAL )
                {
                    nRet = ConfigCurrent;
                }
            }

            if( !getOperation(Emetcon::PutConfig_Outage, function, length, io) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Operation PutConfig_Outage not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            if( outage == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Operation outage not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if( parse.isKeyValid("force")
                    || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_OutageCycles) != outage )
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        OutMessage->Buffer.BSt.Function   = function;
                        OutMessage->Buffer.BSt.Length     = length;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Write;
                        OutMessage->Buffer.BSt.Message[0] = (outage);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                        OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                        OutMessage->Priority             += 1;//return to normal
                    }
                    else
                    {
                        nRet = ConfigNotCurrent;
                    }
                }
                else if( nRet == NORMAL )
                {
                    nRet = ConfigCurrent;
                }
            }

            if( !getOperation(Emetcon::PutConfig_TimeAdjustTolerance, function, length, io) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Operation PutConfig_TimeAdjustTolerance not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            if( timeAdjustTolerance == numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Operation time adjust tolerance not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if( parse.isKeyValid("force")
                    || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TimeAdjustTolerance) != timeAdjustTolerance )
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        OutMessage->Buffer.BSt.Function   = function;
                        OutMessage->Buffer.BSt.Length     = length;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Write;
                        OutMessage->Buffer.BSt.Message[0] = (timeAdjustTolerance);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                        OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                        OutMessage->Priority             += 1;//return to normal
                    }
                    else
                    {
                        nRet = ConfigNotCurrent;
                    }
                }
                else if( nRet == NORMAL )
                {
                    nRet = ConfigCurrent;
                }
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;

    return nRet;
}


INT CtiDeviceMCT410::decodeGetValueKWH(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;
    ULONG i,x;
    INT pid;
    CtiTime pointTime;
    bool valid_data = true;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    point_info pi, pi_freezecount;

    CtiPointSPtr         pPoint;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Accumulator Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if( InMessage->Sequence == Emetcon::Scan_Accum )
    {
        setScanFlag(ScanRateAccum, false);
    }

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!
        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        if( InMessage->Sequence == Cti::Protocol::Emetcon::GetValue_FrozenKWH )
        {
            if( _expected_freeze < 0 && hasDynamicInfo(CtiTableDynamicPaoInfo::Key_ExpectedFreeze) )
            {
                _expected_freeze = getDynamicInfo(CtiTableDynamicPaoInfo::Key_ExpectedFreeze);
            }

            if( _freeze_counter  < 0 && hasDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeCounter) )
            {
                _freeze_counter = getDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeCounter);
            }

            pi_freezecount = getData(DSt->Message + 3, 1, ValueType_Raw);

            if( _freeze_counter < 0 || pi_freezecount.value >= _freeze_counter )
            {
                if( pi_freezecount.value > (_freeze_counter + 1) )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - incoming freeze counter (" << pi_freezecount.value <<
                                            ") has increased by more than expected value (" << _freeze_counter + 1 <<
                                            ") on device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                _freeze_counter = pi_freezecount.value;

                setDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeCounter, _freeze_counter);
            }
            else
            {
                valid_data = false;

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - incoming freeze counter (" << pi_freezecount.value <<
                                        ") less than expected value (" << _freeze_counter <<
                                        ") on device \"" << getName() << "\", not sending data **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                ReturnMsg->setResultString("Freeze counter mismatch error (" + CtiNumStr(pi_freezecount.value) + ") < (" + CtiNumStr(_freeze_counter) + ")");
                status = NOTNORMAL;
            }
        }

        for( int i = 0; i < ChannelCount; i++ )
        {
            int offset = (i * 3);

            if( InMessage->Sequence == Cti::Protocol::Emetcon::Scan_Accum ||
                InMessage->Sequence == Cti::Protocol::Emetcon::GetValue_KWH )
            {
                //  normal KWH read, nothing too special

                pi = getData(DSt->Message + offset, 3, ValueType_Accumulator);

                pointTime -= pointTime.seconds() % 60;
            }
            else if( InMessage->Sequence == Cti::Protocol::Emetcon::GetValue_FrozenKWH )
            {
                //  but this is where the action is - frozen decode

                if( i ) offset++;  //  so that, for the frozen read, it goes 0, 4, 7 to step past the freeze counter in position 3

                pi = getData(DSt->Message + offset, 3, ValueType_FrozenAccumulator);

                if( pi.freeze_bit == _expected_freeze )  //  low-order bit indicates which freeze caused the value to be stored
                {
                    //  assign time from the last freeze time, if the lower bit of dp.first matches the last freeze
                    //    and the freeze counter (DSt->Message[3]) is what we expect
                    //  also, archive the received freeze and the freeze counter into the dynamicpaoinfo table

                    if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp) )
                    {
                        pointTime  = CtiTime(getDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp));
                        pointTime -= pointTime.seconds() % 60;
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" does not have a freeze timestamp for KWH timestamp, defaulting to current time **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        pointTime -= pointTime.seconds() % 60;
                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - incoming freeze parity bit (" << pi.freeze_bit <<
                                            ") does not match expected freeze bit (" << _expected_freeze <<
                                            ") on device \"" << getName() << "\", not sending data **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    valid_data = false;
                    ReturnMsg->setResultString("Freeze parity check failed (" + CtiNumStr(pi.freeze_bit) + ") != (" + CtiNumStr(_expected_freeze) + "), last recorded freeze sent at " + CtiTime(getDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp)).asString());
                    status = NOTNORMAL;
                }
            }

            if( valid_data )
            {
                string point_name;

                if( !i )    point_name = "Meter Reading";

                //  if kWh was returned as units, we could get rid of the default multiplier - it's messy
                insertPointDataReport(PulseAccumulatorPointType, i + 1,
                                      ReturnMsg, pi, point_name, pointTime, 0.1);
            }
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValueDemand(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int status = NORMAL;

    point_info pi;

    INT ErrReturn = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    CtiPointSPtr    pPoint;
    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Demand Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    setScanFlag(ScanRateGeneral, false);
    setScanFlag(ScanRateIntegrity, false);

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        for( int i = 1; i <= 3; i++ )
        {
            int offset = (i > 1)?(i * 2 + 4):(0);  //  0, 6, 8

            pi = getData(DSt->Message + offset, 2, ValueType_Demand);

            //  turn raw pulses into a demand reading
            pi.value *= double(3600 / getDemandInterval());

            CtiTime pointTime;

            pointTime -= pointTime.seconds() % getDemandInterval();

            string point_name;

            if( i == 1 )    point_name = "Demand";

            insertPointDataReport(DemandAccumulatorPointType, i,
                                  ReturnMsg, pi, point_name, pointTime);
        }

        pi = getData(DSt->Message + 2, 2, ValueType_Voltage);

        insertPointDataReport(DemandAccumulatorPointType, PointOffset_Voltage,
                              ReturnMsg, pi, "Voltage", 0UL, 0.1);

        pi = getData(DSt->Message + 4, 2, ValueType_Raw);

        insertPointDataReport(PulseAccumulatorPointType, PointOffset_Accumulator_Powerfail,
                              ReturnMsg, pi, "Blink Counter");

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValueVoltage( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    point_info pi, max_volt_info, min_volt_info;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiTime minTime, maxTime;

        CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        max_volt_info = getData(DSt->Message, 2, ValueType_Voltage);
        pi = getData(DSt->Message + 2, 4, ValueType_Raw);
        maxTime = CtiTime((unsigned long)pi.value);


        min_volt_info = getData(DSt->Message + 6, 2, ValueType_Voltage);
        pi = getData(DSt->Message + 8, 4, ValueType_Raw);
        minTime = CtiTime((unsigned long)pi.value);

        if( InMessage->Sequence == Emetcon::GetValue_FrozenVoltage )
        {
            insertPointDataReport(DemandAccumulatorPointType, PointOffset_MaxVoltage,
                                  ReturnMsg, max_volt_info, "Frozen Max Voltage", maxTime, 0.1);

            insertPointDataReport(DemandAccumulatorPointType, PointOffset_MinVoltage,
                                  ReturnMsg, min_volt_info, "Frozen Min Voltage", minTime, 0.1);
        }
        else
        {
            insertPointDataReport(DemandAccumulatorPointType, PointOffset_MaxVoltage,
                                  ReturnMsg, max_volt_info, "Max Voltage", maxTime, 0.1);

            insertPointDataReport(DemandAccumulatorPointType, PointOffset_MinVoltage,
                                  ReturnMsg, min_volt_info, "Min Voltage", minTime, 0.1);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValueOutage( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NORMAL;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        //  no error, time for decode
        INT ErrReturn =  InMessage->EventCode & 0x3fff;
        unsigned char   *msgbuf = InMessage->Buffer.DSt.Message;
        CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointSPtr    pPoint;
        CtiPointDataMsg *pData = NULL;
        double value;

        int outagenum, multiplier;
        unsigned long  timestamp;
        unsigned short duration;
        string pointString, resultString, timeString;
        CtiTime outageTime;

        ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec) )
        {
            outagenum = parse.getiValue("outage");

            if( (outagenum % 2) == 0 )
            {
                //  if they specified an even number, start with the corresponding odd number
                outagenum--;
            }

            for( int i = 0; i < 2; i++ )
            {
                int days, hours, minutes, seconds, cycles;

                timestamp  = msgbuf[(i*6)+0] << 24;
                timestamp |= msgbuf[(i*6)+1] << 16;
                timestamp |= msgbuf[(i*6)+2] <<  8;
                timestamp |= msgbuf[(i*6)+3];

                duration   = msgbuf[(i*6)+4] <<  8;
                duration  |= msgbuf[(i*6)+5];

                outageTime = CtiTime(timestamp);

                if( timestamp > DawnOfTime )
                {
                    timeString = outageTime.asString();
                }
                else
                {
                    timeString = "(invalid time)";
                }

                pointString = getName() + " / Outage " + CtiNumStr(outagenum + i) + " : " + timeString + " for ";

                if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec)         == Sspec &&
                    getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) >= SspecRev_NewOutage_Min &&
                    getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) <= SspecRev_NewOutage_Max )
                {
                    if( duration == 0x8000 )
                    {
                        pointString += "(unknown duration)";
                        cycles = -1;
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
                }

                if( cycles >= 0 )
                {
                    seconds = cycles  / 60;
                    minutes = seconds / 60;
                    hours   = minutes / 60;
                    days    = hours   / 24;

                    seconds %= 60;
                    minutes %= 60;
                    hours   %= 24;

                    if( days == 1 )
                    {
                        pointString += CtiNumStr(days) + " day, ";
                    }
                    else if( days > 1 )
                    {
                        pointString += CtiNumStr(days) + " days, ";
                    }

                    pointString += CtiNumStr(hours).zpad(2) + string(":") +
                                   CtiNumStr(minutes).zpad(2) + ":" +
                                   CtiNumStr(seconds).zpad(2);

                    if( cycles % 60 )
                    {
                        int millis = (cycles % 60) * 1000;
                        millis /= 60;

                        pointString += "." + CtiNumStr(millis).zpad(3);
                    }

                    if( pPoint = getDevicePointOffsetTypeEqual(PointOffset_Analog_Outage, AnalogPointType) )
                    {
                        value  = cycles;
                        value /= 60.0;
                        value  = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(value);

                        if( pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), value, NormalQuality, AnalogPointType, pointString) )
                        {
                            pData->setTime( outageTime );

                            ReturnMsg->PointData().push_back(pData);
                            pData = 0;
                        }
                    }
                    else
                    {
                        resultString += pointString + "\n";
                    }
                }
                else
                {
                    resultString += pointString + "\n";
                }
            }

            ReturnMsg->setResultString(resultString);
        }
        else
        {
            ReturnMsg->setResultString(getName() + " / Sspec not stored, could not reliably decode outages; try read again");
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValueFreezeCounter( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NORMAL;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        //  no error, time for decode
        INT ErrReturn =  InMessage->EventCode & 0x3fff;
        unsigned char   *msgbuf = InMessage->Buffer.DSt.Message;
        CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

        ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        ReturnMsg->setResultString(getName() + " / Freeze counter: " + CtiNumStr(msgbuf[0]));

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValueLoadProfilePeakReport(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    string result_string;
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
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

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

        max_demand_timestamp += PASTDATE;//TS

        pulses = DSt->Message[7] << 16 |
                 DSt->Message[8] <<  8 |
                 DSt->Message[9];

        avg_daily = (double)pulses / 10;

        pulses = DSt->Message[10] << 16 |
                 DSt->Message[11] <<  8 |
                 DSt->Message[12];

        total_usage = (double)pulses / 10;

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        result_string  = getName() + " / Channel " + CtiNumStr(_llpPeakInterest.channel + 1) + string(" Load Profile Report\n");
        result_string += "Report range: " + CtiTime(_llpPeakInterest.time - (_llpPeakInterest.period * 86400)).asString() + " - " +
                                            CtiTime(_llpPeakInterest.time).asString() + "\n";

        switch( _llpPeakInterest.command )
        {
            case FuncRead_LLPPeakDayPos:
            {
                result_string += "Peak day: " + CtiTime(max_demand_timestamp).asString() + "\n";
                result_string += "Usage:  " + CtiNumStr(max_usage) + string(" kWH\n");
                result_string += "Demand: " + CtiNumStr(max_usage / 24) + string(" kW\n");
                result_string += "Average daily usage over range: " + CtiNumStr(avg_daily) + string(" kWH\n");
                result_string += "Total usage over range: " + CtiNumStr(total_usage) + string(" kWH\n");

                break;
            }
            case FuncRead_LLPPeakHourPos:
            {
                result_string += "Peak hour: " + CtiTime(max_demand_timestamp).asString() + "\n";
                result_string += "Usage:  " + CtiNumStr(max_usage) + string(" kWH\n");
                result_string += "Demand: " + CtiNumStr(max_usage) + string(" kW\n");
                result_string += "Average daily usage over range: " + CtiNumStr(avg_daily) + string(" kWH\n");
                result_string += "Total usage over range: " + CtiNumStr(total_usage) + string(" kWH\n");

                break;
            }
            case FuncRead_LLPPeakIntervalPos:
            {
                int intervals_per_hour = 3600 / getLoadProfile().getLoadProfileDemandRate();

                result_string += "Peak interval: " + CtiTime(max_demand_timestamp).asString() + "\n";
                result_string += "Usage:  " + CtiNumStr(max_usage) + string(" kWH\n");
                result_string += "Demand: " + CtiNumStr(max_usage * intervals_per_hour) + string(" kW\n");
                result_string += "Average daily usage over range: " + CtiNumStr(avg_daily) + string(" kWH\n");
                result_string += "Total usage over range: " + CtiNumStr(total_usage) + string(" kWH\n");

                break;
            }
        }

        ReturnMsg->setResultString(result_string);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetStatusInternal( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    unsigned char *geneBuf = InMessage->Buffer.DSt.Message;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        string resultString;

        CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg      *pData = NULL;

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        resultString  = getName() + " / Internal Status:\n";

        //  point offset 10
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x01)?"Power Fail occurred\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x02)?"Under-Voltage Event\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x04)?"Over-Voltage Event\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x08)?"Power Fail Carryover\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x10)?"RTC Adjusted\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x20)?"Holiday Event occurred\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x40)?"DST Change occurred\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x80)?"Tamper Flag set\n":"";

        //  point offset 20
        resultString += (InMessage->Buffer.DSt.Message[2] & 0x01)?"Zero usage stored for 24 hours\n":"";
        resultString += (InMessage->Buffer.DSt.Message[2] & 0x02)?"Disconnect error (demand seen after disconnect)\n":"";
        resultString += (InMessage->Buffer.DSt.Message[2] & 0x04)?"Last meter reading corrupt\n":"";
        //  0x08 - 0x80 aren't used yet

        //  starts at offset 30 - NOTE that this is byte 0
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x01)?"Group addressing disabled\n":"Group addressing enabled\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x02)?"Phase detect in progress\n":"";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x04)?"DST active\n":"DST inactive\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x08)?"Holiday active\n":"";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x10)?"TOU disabled\n":"TOU enabled\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x20)?"Time sync needed\n":"In time sync\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x40)?"Critical peak active\n":"";
        //  0x80 is not used yet

        //  Eventually, we should exclude the iCon-specific bits from the Centron result

        //  point offset 40
        resultString += (InMessage->Buffer.DSt.Message[3] & 0x01)?"Soft kWh Error, Data OK\n":"";
        resultString += (InMessage->Buffer.DSt.Message[3] & 0x02)?"Low AC Volts\n":"";
        resultString += (InMessage->Buffer.DSt.Message[3] & 0x04)?"Current Too High\n":"";
        resultString += (InMessage->Buffer.DSt.Message[3] & 0x08)?"Power Failure\n":"";
        resultString += (InMessage->Buffer.DSt.Message[3] & 0x10)?"Hard EEPROM Error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[3] & 0x20)?"Hard kWh Error, Data Lost\n":"";
        resultString += (InMessage->Buffer.DSt.Message[3] & 0x40)?"Configuration Error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[3] & 0x80)?"Reverse Power\n":"";

        //  point offset 50
        resultString += (InMessage->Buffer.DSt.Message[4] & 0x01)?"7759 Calibration Error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[4] & 0x02)?"7759 Register Check Error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[4] & 0x04)?"7759 Reset Error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[4] & 0x08)?"RAM Bit Error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[4] & 0x10)?"General CRC Error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[4] & 0x20)?"Soft EEPROM Error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[4] & 0x40)?"Watchdog Restart\n":"";
        resultString += (InMessage->Buffer.DSt.Message[4] & 0x80)?"7759 Bit Checksum Error\n":"";

        ReturnMsg->setResultString(resultString);

        for( int i = 0; i < 5; i++ )
        {
            int offset;
            boost::shared_ptr<CtiPointStatus> point;
            CtiPointDataMsg *pData;
            string pointResult;

            if( i == 0 )  offset = 30;
            if( i == 1 )  offset = 10;
            if( i == 2 )  offset = 20;
            if( i == 3 )  offset = 40;
            if( i == 4 )  offset = 50;

            for( int j = 0; j < 8; j++ )
            {
                //  Don't send the powerfail status again - it's being sent by dev_mct in ResultDecode()
                if( (i + j != 10) && (point = boost::static_pointer_cast<CtiPointStatus>(getDevicePointOffsetTypeEqual( i + j, StatusPointType ))) )
                {
                    double value = (InMessage->Buffer.DSt.Message[i] >> j) & 0x01;

                    pointResult = getName() + " / " + point->getName() + ": " + ResolveStateName((point)->getStateGroupID(), value);

                    if( pData = CTIDBG_new CtiPointDataMsg(point->getPointID(), value, NormalQuality, StatusPointType, pointResult) )
                    {
                        ReturnMsg->PointData().push_back(pData);
                    }
                }
            }
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetStatusLoadProfile( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    unsigned char *geneBuf = InMessage->Buffer.DSt.Message;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        string resultString;
        unsigned long tmpTime;
        CtiTime lpTime;

        CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg      *pData = NULL;

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        resultString += getName() + " / Demand Load Profile Status:\n";

        tmpTime = DSt->Message[0] << 24 |
                  DSt->Message[1] << 16 |
                  DSt->Message[2] <<  8 |
                  DSt->Message[3];

        lpTime = CtiTime(tmpTime);

        resultString += "Current Interval Time: " + lpTime.asString() + "\n";
        //resultString += "Current Interval Pointer: " + CtiNumStr(DSt->Message[4]) + string("\n");
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

        lpTime = CtiTime(tmpTime);

        resultString += "Current Interval Time: " + lpTime.asString() + "\n";
        //resultString += "Current Interval Pointer: " + CtiNumStr(DSt->Message[10]) + string("\n");
        resultString += (DSt->Message[11] & 0x01)?"Boundary Error\n":"";
        resultString += (DSt->Message[11] & 0x02)?"Power Fail\n":"";

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetConfigIntervals(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        string resultString;

        resultString  = getName() + " / Demand Interval:       " + CtiNumStr(DSt->Message[0]) + string(" minutes\n");
        resultString += getName() + " / Load Profile Interval: " + CtiNumStr(DSt->Message[1]) + string(" minutes\n");
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

        resultString += getName() + " / Voltage Profile Interval: " + CtiNumStr(DSt->Message[3]) + string(" minutes\n");

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}

INT CtiDeviceMCT410::decodeGetConfigCentron(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        string resultString;

        if( InMessage->Sequence == Emetcon::GetConfig_Multiplier )
        {
            resultString = getName() + " / Centron Multiplier: " + CtiNumStr(DSt->Message[0]);
        }
        else if( InMessage->Sequence == Emetcon::GetConfig_CentronParameters )
        {
            resultString = getName() + " / Centron Parameters:\n";

            switch( DSt->Message[0] & 0x03 )
            {
                case 0x0:   resultString += "5x1 display (5 digits, 1kWHr resolution)\n";   break;
                case 0x1:   resultString += "4x1 display (4 digits, 1kWHr resolution)\n";   break;
                case 0x2:   resultString += "4x10 display (4 digits, 10kWHr resolution)\n"; break;
                case 0x3:
                default:    resultString += "Unknown display resolution (" + CtiNumStr(DSt->Message[0] & 0x03) + ")\n";
            }

            resultString += "LCD segment test ";

            if( DSt->Message[0] & 0x04 )
            {
                resultString += (DSt->Message[0] & 0x08)?("required, seven seconds\n"):("required, one second\n");
            }
            else
            {
                resultString += "not required\n";
            }

            resultString += "LCD error display ";
            resultString += (DSt->Message[0] & 0x10)?("enabled\n"):("disabled\n");
        }

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetConfigDisconnect(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    string resultStr;

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
            case RawStatus_Connected:                resultStr += "connected\n";                 break;
            case RawStatus_ConnectArmed:             resultStr += "connect armed\n";             break;
            case RawStatus_DisconnectedUnconfirmed:  resultStr += "unconfirmed disconnected\n";  break;
            case RawStatus_DisconnectedConfirmed:    resultStr += "confirmed disconnected\n";    break;
        }

        if( DSt->Message[1] & 0x02 )
        {
            resultStr += "Disconnect error - nonzero demand detected after disconnect command sent to collar\n";
        }

        long disconnectaddress = DSt->Message[2] << 16 |
                                 DSt->Message[3] <<  8 |
                                 DSt->Message[4];

        resultStr += "Disconnect receiver address: " + CtiNumStr(disconnectaddress) + string("\n");

        point_info pi = getData(DSt->Message + 5, 2);

        resultStr += "Disconnect demand threshold: ";
        resultStr += pi.value?CtiNumStr(pi.value):"disabled";
        resultStr += "\n";

        resultStr += "Disconnect load limit connect delay: " + CtiNumStr(DSt->Message[7]) + string(" minutes\n");

        resultStr += "Disconnect load limit count: " + CtiNumStr(DSt->Message[8]) + string("\n");

        if(ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr))
        {
            ReturnMsg->setUserMessageId(InMessage->Return.UserID);
            ReturnMsg->setResultString(resultStr);

            retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            status = MEMORY;
        }
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetConfigModel(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        string sspec;
        string options;
        int  ssp;
        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

        ssp  = InMessage->Buffer.DSt.Message[0];
        ssp |= InMessage->Buffer.DSt.Message[4] << 8;

        sspec  = "\nSoftware Specification ";
        sspec += CtiNumStr(ssp);
        sspec += " Rom Revision ";

        //  convert 10 to 1.0, 24 to 2.4
        sspec += CtiNumStr(((double)InMessage->Buffer.DSt.Message[1]) / 10.0, 1);

        //  valid/released versions are 1.0 - 24.9
        if( InMessage->Buffer.DSt.Message[1] <   10 ||
            InMessage->Buffer.DSt.Message[1] >= 250 )
        {
            sspec += " [possible development revision]\n";
        }

        sspec += "\n";

        //  set the dynamic info for use later
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec,         (long)ssp);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, (long)InMessage->Buffer.DSt.Message[1]);

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString( sspec + options );

        //  this is hackish, and should be handled in a more centralized manner...  retMsgHandler, for example
        if( InMessage->MessageFlags & MessageFlag_ExpectMore )
        {
            ReturnMsg->setExpectMore(true);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}

