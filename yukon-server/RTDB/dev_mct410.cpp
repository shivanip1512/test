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
* REVISION     :  $Revision: 1.83 $
* DATE         :  $Date: 2006/08/29 22:31:34 $
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
#include "numstr.h"
#include "porter.h"
#include "portglob.h"
#include "utility.h"
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

const CtiDeviceMCT4xx::ConfigPartsList CtiDeviceMCT410::_config_parts   = CtiDeviceMCT410::initConfigParts();

const CtiDeviceMCT::DynamicPaoAddressing_t         CtiDeviceMCT410::_dynPaoAddressing     = CtiDeviceMCT410::initDynPaoAddressing();
const CtiDeviceMCT::DynamicPaoFunctionAddressing_t CtiDeviceMCT410::_dynPaoFuncAddressing = CtiDeviceMCT410::initDynPaoFuncAddressing();

CtiDeviceMCT410::CtiDeviceMCT410( ) :
    _intervalsSent(false)  //  whee!  you're going to be gone soon, sucker!
{
    _llpInterest.time     = 0;
    _llpInterest.time_end = 0;
    _llpInterest.channel  = 0;
    _llpInterest.offset   = 0;
    _llpInterest.retry    = false;
    _llpInterest.failed   = false;

    _llpPeakInterest.channel = 0;
    _llpPeakInterest.command = 0;
    _llpPeakInterest.period  = 0;
    _llpPeakInterest.time    = 0;

    for( int i = 0; i < LPChannels; i++ )
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


void CtiDeviceMCT410::setDisconnectAddress( unsigned long address )
{
    _disconnectAddress = address;
}

CtiDeviceMCT::DynamicPaoAddressing_t CtiDeviceMCT410::initDynPaoAddressing()
{
    DynamicPaoAddressing_t addressSet;
    DynamicPaoAddressing   addressData;

    addressData.address = Memory_SSpecPos;
    addressData.length = Memory_SSpecLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_SSpec;
    addressSet.insert(addressData);

    addressData.address = Memory_RevisionPos;
    addressData.length = Memory_RevisionLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision;
    addressSet.insert(addressData);

    addressData.address = Memory_OptionsPos;
    addressData.length = Memory_OptionsLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_Options;
    addressSet.insert(addressData);

    addressData.address = Memory_ConfigurationPos;
    addressData.length = Memory_ConfigurationLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_Configuration;
    addressSet.insert(addressData);

    addressData.address = Memory_EventFlagsMask1Pos;
    addressData.length = Memory_EventFlagsMask1Len;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1;
    addressSet.insert(addressData);

    addressData.address = Memory_EventFlagsMask2Pos;
    addressData.length = Memory_EventFlagsMask2Len;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2;
    addressSet.insert(addressData);

    addressData.address = Memory_MeterAlarmMaskPos;
    addressData.length = Memory_MeterAlarmMaskLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask;
    addressSet.insert(addressData);

    addressData.address = Memory_BronzeAddressPos;
    addressData.length = Memory_BronzeAddressLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_AddressBronze;
    addressSet.insert(addressData);

    addressData.address = Memory_LeadAddressPos;
    addressData.length = Memory_LeadAddressLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_AddressLead;
    addressSet.insert(addressData);

    addressData.address = Memory_CollectionAddressPos;
    addressData.length = Memory_CollectionAddressLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_AddressCollection;
    addressSet.insert(addressData);

    addressData.address = Memory_SPIDAddressPos;
    addressData.length = Memory_SPIDAddressLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID;
    addressSet.insert(addressData);

    addressData.address = Memory_DemandIntervalPos;
    addressData.length = Memory_DemandIntervalLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_DemandInterval;
    addressSet.insert(addressData);

    addressData.address = Memory_LoadProfileIntervalPos;
    addressData.length = Memory_LoadProfileIntervalLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval;
    addressSet.insert(addressData);

    addressData.address = Memory_VoltageDemandIntervalPos;
    addressData.length = Memory_VoltageDemandIntervalLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_VoltageDemandInterval;
    addressSet.insert(addressData);

    addressData.address = Memory_VoltageLPIntervalPos;
    addressData.length = Memory_VoltageLPIntervalLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_VoltageLPInterval;
    addressSet.insert(addressData);

    addressData.address = Memory_OverVThresholdPos;
    addressData.length = Memory_OverVThresholdLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_OverVoltageThreshold;
    addressSet.insert(addressData);

    addressData.address = Memory_UnderVThresholdPos;
    addressData.length = Memory_UnderVThresholdLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_UnderVoltageThreshold;
    addressSet.insert(addressData);

    addressData.address = Memory_OutageCyclesPos;
    addressData.length = Memory_OutageCyclesLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_OutageCycles;
    addressSet.insert(addressData);

    addressData.address = Memory_TimeAdjustTolPos;
    addressData.length = Memory_TimeAdjustTolLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_TimeAdjustTolerance;
    addressSet.insert(addressData);

    addressData.address = Memory_DSTBeginPos;
    addressData.length = Memory_DSTBeginLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_DSTStartTime,
    addressSet.insert(addressData);

    addressData.address = Memory_DSTEndPos;
    addressData.length = Memory_DSTEndLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_DSTEndTime;
    addressSet.insert(addressData);

    addressData.address = Memory_TimeZoneOffsetPos;
    addressData.length = Memory_TimeZoneOffsetLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset;
    addressSet.insert(addressData);

    addressData.address = Memory_TOUDayTablePos;
    addressData.length = Memory_TOUDayTableLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_DayTable;
    addressSet.insert(addressData);

    addressData.address = Memory_TOUDailySched1Pos;
    addressData.length = Memory_TOUDailySched1Len;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1;
    addressSet.insert(addressData);

    addressData.address = Memory_TOUDailySched2Pos;
    addressData.length = Memory_TOUDailySched2Len;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2;
    addressSet.insert(addressData);

    addressData.address = Memory_TOUDailySched3Pos;
    addressData.length = Memory_TOUDailySched3Len;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3;
    addressSet.insert(addressData);

    addressData.address = Memory_TOUDailySched4Pos;
    addressData.length = Memory_TOUDailySched4Len;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4;
    addressSet.insert(addressData);

    addressData.address = Memory_DefaultTOURatePos;
    addressData.length = Memory_DefaultTOURateLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate;
    addressSet.insert(addressData);

    addressData.address = Memory_Holiday1Pos;
    addressData.length = Memory_Holiday1Len;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_Holiday1;
    addressSet.insert(addressData);

    addressData.address = Memory_Holiday2Pos;
    addressData.length = Memory_Holiday2Len;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_Holiday2;
    addressSet.insert(addressData);

    addressData.address = Memory_Holiday3Pos;
    addressData.length = Memory_Holiday3Len;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_Holiday3;
    addressSet.insert(addressData);

    addressData.address = Memory_CentronParametersPos;
    addressData.length = Memory_CentronParametersLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_CentronParameters;
    addressSet.insert(addressData);

    addressData.address = Memory_CentronMultiplierPos;
    addressData.length = Memory_CentronMultiplierLen;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_CentronRatio;
    addressSet.insert(addressData);

    return addressSet;
}

CtiDeviceMCT::DynamicPaoFunctionAddressing_t CtiDeviceMCT410::initDynPaoFuncAddressing()
{
    DynamicPaoAddressing_t addressSet;
    DynamicPaoAddressing   addressData;
    DynamicPaoFunctionAddressing_t functionSet;

    // FuncRead_TOUDaySchedulePos
    addressData.address = 0;
    addressData.length = 2;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_DayTable;
    addressSet.insert(addressData);

    addressData.address = 2;
    addressData.length = 1;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate;
    addressSet.insert(addressData);

    addressData.address = 10;
    addressData.length = 1;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset;
    addressSet.insert(addressData);

    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(FuncRead_TOUDaySchedulePos,addressSet));

    addressSet.clear();

    // FuncRead_LLPStatusPos
    addressData.address = 4;
    addressData.length = 1;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_LLPChannel1Len;
    addressSet.insert(addressData);

    addressData.address = 5;
    addressData.length = 1;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_LLPChannel2Len;
    addressSet.insert(addressData);

    addressData.address = 6;
    addressData.length = 1;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_LLPChannel3Len;
    addressSet.insert(addressData);

    addressData.address = 7;
    addressData.length = 1;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len;
    addressSet.insert(addressData);

    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(FuncRead_LLPStatusPos,addressSet));

    addressSet.clear();

    // FuncRead_DisconnectConfigPos
    addressData.address = 5;
    addressData.length = 2;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_DemandThreshold;
    addressSet.insert(addressData);

    addressData.address = 7;
    addressData.length = 1;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_ConnectDelay;
    addressSet.insert(addressData);

    addressData.address = 9;
    addressData.length = 1;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_DisconnectMinutes;
    addressSet.insert(addressData);

    addressData.address = 10;
    addressData.length = 1;
    addressData.key = CtiTableDynamicPaoInfo::Key_MCT_ConnectMinutes;
    addressSet.insert(addressData);


    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(FuncRead_DisconnectConfigPos,addressSet));

    return functionSet;
}

//Function returns first address after the given address and the data associated with that address
void CtiDeviceMCT410::getDynamicPaoAddressing(int address, int &foundAddress, int &foundLength, CtiTableDynamicPaoInfo::Keys &foundKey)
{
    foundAddress = 0;
    foundLength = 0;
    foundKey = CtiTableDynamicPaoInfo::Key_Invalid;//If nothing happens, this is what we want.

    DynamicPaoAddressing tempDynAddr;
    tempDynAddr.address = address;

    DynamicPaoAddressing_t::const_iterator iter;
    if((iter = _dynPaoAddressing.find(tempDynAddr)) != _dynPaoAddressing.end())
    {
        foundAddress = iter->address;
        foundLength = iter->length;
        foundKey = iter->key;
    }
    else if((iter = _dynPaoAddressing.upper_bound(tempDynAddr)) != _dynPaoAddressing.end())
    {
        foundAddress = iter->address;
        foundLength = iter->length;
        foundKey = iter->key;
    }
}

void CtiDeviceMCT410::getDynamicPaoFunctionAddressing(int function, int address, int &foundAddress, int &foundLength, CtiTableDynamicPaoInfo::Keys &foundKey)
{
    foundAddress = 0;
    foundLength = 0;
    foundKey = CtiTableDynamicPaoInfo::Key_Invalid;//If nothing happens, this is what we want.

    DynamicPaoAddressing tempDynAddr;
    tempDynAddr.address = address;

    DynamicPaoFunctionAddressing_t::const_iterator funcIter;
    if((funcIter = _dynPaoFuncAddressing.find(function)) != _dynPaoFuncAddressing.end())
    {
        DynamicPaoAddressing_t::const_iterator addressIter;
        if((addressIter = funcIter->second.find(tempDynAddr)) != funcIter->second.end())
        {
            foundAddress = addressIter->address;
            foundLength = addressIter->length;
            foundKey = addressIter->key;
        }
        else if((addressIter = funcIter->second.upper_bound(tempDynAddr)) != funcIter->second.end())
        {
            foundAddress = addressIter->address;
            foundLength = addressIter->length;
            foundKey = addressIter->key;
        }
    }
}

CtiDeviceMCT4xx::ConfigPartsList CtiDeviceMCT410::initConfigParts()
{
    CtiDeviceMCT4xx::ConfigPartsList tempList;

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


CtiDeviceMCT4xx::ConfigPartsList CtiDeviceMCT410::getPartsList()
{
    return _config_parts;
}


CtiDeviceMCT410::point_info_t CtiDeviceMCT410::getLoadProfileData(unsigned channel, unsigned char *buf, unsigned len)
{
    point_info_t pi;

    if( channel == Channel_Voltage )
    {
        pi = getData(buf, len, ValueType_LoadProfile_Voltage);
    }
    else
    {
        pi = getData(buf, len, ValueType_LoadProfile_KW);
        pi.value *= 3600 / getLoadProfileInterval(channel);
    }

    return pi;
}


CtiDeviceMCT410::CommandSet CtiDeviceMCT410::initCommandStore()
{
    CommandSet cs;

    cs.insert(CommandStore(Emetcon::Scan_Accum,                     Emetcon::IO_Function_Read, FuncRead_MReadPos,           FuncRead_MReadLen));
    cs.insert(CommandStore(Emetcon::GetValue_Default,               Emetcon::IO_Function_Read, FuncRead_MReadPos,           FuncRead_MReadLen));
    cs.insert(CommandStore(Emetcon::GetValue_FrozenKWH,             Emetcon::IO_Function_Read, FuncRead_FrozenMReadPos,     FuncRead_FrozenMReadLen));
    cs.insert(CommandStore(Emetcon::Scan_Integrity,                 Emetcon::IO_Function_Read, FuncRead_DemandPos,          FuncRead_DemandLen));
    cs.insert(CommandStore(Emetcon::Scan_LoadProfile,               Emetcon::IO_Function_Read, 0,                           0));
    cs.insert(CommandStore(Emetcon::GetValue_TOU,                   Emetcon::IO_Function_Read, FuncRead_TOUBasePos,         FuncRead_TOULen));
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
    cs.insert(CommandStore(Emetcon::Control_Close,              Emetcon::IO_Write,          Command_Connect,                0));
    cs.insert(CommandStore(Emetcon::Control_Open,               Emetcon::IO_Write,          Command_Disconnect,             0));
    cs.insert(CommandStore(Emetcon::Control_Conn,               Emetcon::IO_Write,          Command_Connect,                0));
    cs.insert(CommandStore(Emetcon::Control_Disc,               Emetcon::IO_Write,          Command_Disconnect,             0));
    cs.insert(CommandStore(Emetcon::GetStatus_Disconnect,       Emetcon::IO_Function_Read,  FuncRead_DisconnectStatusPos,   FuncRead_DisconnectStatusLen));
    cs.insert(CommandStore(Emetcon::GetConfig_Disconnect,       Emetcon::IO_Function_Read,  FuncRead_DisconnectConfigPos,   FuncRead_DisconnectConfigLen));
    cs.insert(CommandStore(Emetcon::PutConfig_Disconnect,       Emetcon::IO_Function_Write, FuncWrite_DisconnectConfigPos,  FuncWrite_DisconnectConfigLen));
    cs.insert(CommandStore(Emetcon::PutConfig_Raw,              Emetcon::IO_Write,          0,                              0));  //  filled in later
    cs.insert(CommandStore(Emetcon::PutConfig_TSync,            Emetcon::IO_Function_Write, FuncWrite_TSyncPos,      FuncWrite_TSyncLen));
    cs.insert(CommandStore(Emetcon::GetConfig_TSync,            Emetcon::IO_Read,           Memory_LastTSyncPos,            Memory_LastTSyncLen));
    cs.insert(CommandStore(Emetcon::GetConfig_Time,             Emetcon::IO_Read,           Memory_TimeZoneOffsetPos,       Memory_TimeZoneOffsetLen +
                                                                                                                            Memory_RTCLen));
    cs.insert(CommandStore(Emetcon::PutConfig_TimeZoneOffset,   Emetcon::IO_Write,          Memory_TimeZoneOffsetPos,       Memory_TimeZoneOffsetLen));
    cs.insert(CommandStore(Emetcon::PutConfig_Intervals,        Emetcon::IO_Function_Write, FuncWrite_IntervalsPos,         FuncWrite_IntervalsLen));
    cs.insert(CommandStore(Emetcon::GetConfig_Intervals,        Emetcon::IO_Read,           Memory_IntervalsPos,            Memory_IntervalsLen));
    cs.insert(CommandStore(Emetcon::GetValue_PFCount,           Emetcon::IO_Read,           Memory_PowerfailCountPos,       Memory_PowerfailCountLen));
    cs.insert(CommandStore(Emetcon::PutValue_ResetPFCount,      Emetcon::IO_Write,          Command_PowerfailReset,  0));
    cs.insert(CommandStore(Emetcon::PutStatus_Reset,            Emetcon::IO_Write,          Command_Reset,           0));
    cs.insert(CommandStore(Emetcon::PutStatus_FreezeOne,        Emetcon::IO_Write,          Command_FreezeOne,              0));
    cs.insert(CommandStore(Emetcon::PutStatus_FreezeTwo,        Emetcon::IO_Write,          Command_FreezeTwo,              0));
    cs.insert(CommandStore(Emetcon::PutStatus_FreezeVoltageOne, Emetcon::IO_Write,          Command_FreezeVoltageOne, 0));
    cs.insert(CommandStore(Emetcon::PutStatus_FreezeVoltageTwo, Emetcon::IO_Write,          Command_FreezeVoltageTwo, 0));

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

    if( (channel + 1) == Channel_Voltage )  retval = getLoadProfile().getVoltageProfileRate();
    else                                    retval = getLoadProfile().getLoadProfileDemandRate();

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
            if( !getLoadProfile().isChannelValid(i) || pPoint )
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

            if( getMCTDebugLevel(MCTDebug_LoadProfile) )
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

            if( getMCTDebugLevel(MCTDebug_LoadProfile) )
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

    if( getMCTDebugLevel(MCTDebug_LoadProfile) )
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
    else
    {
        for( int i = 0; i < LPChannels; i++ )
        {
            interval_len = getLoadProfileInterval(i);
            block_len    = interval_len * 6;

            if( useScanFlags() )
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

                    if( getMCTDebugLevel(MCTDebug_LoadProfile) )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - LP variable check for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Now.seconds() = " << Now.seconds() << endl;
                        dout << "_lp_info[" << i << "].archived_reading = " << _lp_info[i].archived_reading << endl;
                        dout << "MCT4XX_LPRecentBlocks * block_len = " << LPRecentBlocks * block_len << endl;
                        dout << "_lp_info[" << i << "].current_request = " << _lp_info[i].current_request << endl;
                    }

                    //  make sure we're aligned (note - rwEpoch is an even multiple of 86400, so no worries)
                    _lp_info[i].current_request -= _lp_info[i].current_request % block_len;

                    //  which block to grab?
                    channel = i + 1;
                    block   = (Now.seconds() - _lp_info[i].current_request) / block_len;

                    descriptor = " channel " + CtiNumStr(channel) + string(" block ") + CtiNumStr(block);

                    strncat( tmpOutMess->Request.CommandStr,
                             descriptor.c_str(),
                             sizeof(tmpOutMess->Request.CommandStr) - ::strlen(tmpOutMess->Request.CommandStr));

                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS  );
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - command string check for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "\"" << tmpOutMess->Request.CommandStr << "\"" << endl;
                    }

                    outList.push_back(tmpOutMess);
                }
                else
                {
                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - LP scan too early for device \"" << getName() << "\", aborted **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

        case Emetcon::GetValue_TOU:  //  decoding the TOU peaks
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

        case (Emetcon::GetConfig_Time):
        case (Emetcon::GetConfig_TSync):
        {
            status = decodeGetConfigTime(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::GetConfig_TOU):
        {
            status = decodeGetConfigTOU(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::Control_Open):
        case (Emetcon::Control_Close):
        case (Emetcon::Control_Conn):
        case (Emetcon::Control_Disc):
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


struct ratechange_t
{
    int schedule;
    int time;
    int rate;

    bool operator<(const ratechange_t &rhs) const
    {
        bool retval = false;

        if( schedule < rhs.schedule )
        {
            retval = true;
        }
        else if( schedule == rhs.schedule )
        {
            if( time < rhs.time )
            {
                retval = true;
            }
        }

        return retval;
    }
} ratechange;


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
                        holidays[holiday_count++] = CtiTime(holiday_date).seconds() - rwEpoch;
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
        double reading_forward = parse.getiValue("centron_reading_forward"),
               reading_reverse = parse.getiValue("centron_reading_reverse");

        long   pulses_forward,
               pulses_reverse;

        shared_ptr<CtiPointNumeric> tmpPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(1, PulseAccumulatorPointType));

        if( tmpPoint )
        {
            //  adjust for the multiplier, if the point exists
            pulses_forward = (long)(reading_forward / tmpPoint->getMultiplier());
            pulses_reverse = (long)(reading_reverse / tmpPoint->getMultiplier());
        }
        else
        {
            pulses_forward = (long)reading_forward;
            pulses_reverse = (long)reading_reverse;
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
    else if( parse.isKeyValid("tou") )
    {
        set< ratechange_t > ratechanges;
        int default_rate, day_schedules[8];

        string schedule_name, change_name, daytable(parse.getsValue("tou_days"));

        switch( parse.getsValue("tou_default").data()[0] )
        {
            case 'a':   default_rate =  0;  break;
            case 'b':   default_rate =  1;  break;
            case 'c':   default_rate =  2;  break;
            case 'd':   default_rate =  3;  break;
            default:    default_rate = -1;  break;
        }

        if( default_rate < 0 )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - TOU default rate \"" << parse.getsValue("tou_default") << "\" specified is invalid for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
        else
        {
            if( daytable.length() < 8 || daytable.find_first_not_of("1234") != string::npos )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - day table \"" << daytable << "\" specified is invalid for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
            else
            {
                for( int day = 0; day < 8; day++ )
                {
                    day_schedules[day] = atoi(daytable.substr(day, 1).data()) - 1;
                }

                int schedulenum = 0;
                schedule_name.assign("tou_schedule_");
                schedule_name.append(CtiNumStr(schedulenum).zpad(2));
                while(parse.isKeyValid(schedule_name.data()))
                {
                    int schedule_number = parse.getiValue(schedule_name.data());

                    if( schedule_number > 0 && schedule_number <= 4 )
                    {
                        int changenum = 0;
                        change_name.assign(schedule_name);
                        change_name.append("_");
                        change_name.append(CtiNumStr(changenum).zpad(2));
                        while(parse.isKeyValid(change_name.data()))
                        {
                            string ratechangestr = parse.getsValue(change_name.data()).data();
                            int rate, hour, minute;

                            switch(ratechangestr.at(0))
                            {
                                case 'a':   rate =  0;  break;
                                case 'b':   rate =  1;  break;
                                case 'c':   rate =  2;  break;
                                case 'd':   rate =  3;  break;
                                default:    rate = -1;  break;
                            }

                            hour   = atoi(ratechangestr.substr(2).data());

                            int minute_index = ratechangestr.substr(4).find_first_not_of(":") + 4;
                            minute = atoi(ratechangestr.substr(minute_index).data());

                            if( rate   >= 0 &&
                                hour   >= 0 && hour   < 23 &&
                                minute >= 0 && minute < 60 )
                            {
                                ratechange_t ratechange;

                                ratechange.schedule = schedule_number - 1;
                                ratechange.rate = rate;
                                ratechange.time = hour * 3600 + minute * 60;

                                ratechanges.insert(ratechange);
                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " **** Checkpoint - schedule \"" << schedule_number << "\" has invalid rate change \"" << ratechangestr << "\"for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                            }

                            changenum++;
                            change_name.assign(schedule_name);
                            change_name.append("_");
                            change_name.append(CtiNumStr(changenum).zpad(2));
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - schedule \"" << schedule_number << "\" specified is out of range for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }

                    schedulenum++;
                    schedule_name.assign("tou_schedule_");
                    schedule_name.append(CtiNumStr(schedulenum).zpad(2));
                }

                OUTMESS *TOU_OutMessage1 = CTIDBG_new OUTMESS(*OutMessage),
                        *TOU_OutMessage2 = CTIDBG_new OUTMESS(*OutMessage);

                TOU_OutMessage1->Sequence = Cti::Protocol::Emetcon::PutConfig_TOU;
                TOU_OutMessage2->Sequence = Cti::Protocol::Emetcon::PutConfig_TOU;

                TOU_OutMessage1->Buffer.BSt.Function = FuncWrite_TOUSchedule1Pos;
                TOU_OutMessage1->Buffer.BSt.Length   = FuncWrite_TOUSchedule1Len;

                TOU_OutMessage2->Buffer.BSt.Function = FuncWrite_TOUSchedule2Pos;
                TOU_OutMessage2->Buffer.BSt.Length   = FuncWrite_TOUSchedule2Len;

                TOU_OutMessage1->Buffer.BSt.IO = Cti::Protocol::Emetcon::IO_Function_Write;
                TOU_OutMessage2->Buffer.BSt.IO = Cti::Protocol::Emetcon::IO_Function_Write;

                set< ratechange_t >::iterator itr;

                //  There's much more intelligence and safeguarding that could be added to the below,
                //    but it's a temporary fix, to be handled soon by the proper MCT Configs,
                //    so I don't think it's worth it at the moment to add all of the smarts.
                //  We'll handle a good string, and kick out on anything else.

                int durations[4][5], rates[4][6];
                for( int i = 0; i < 4; i++ )
                {
                    for( int j = 0; j < 6; j++ )
                    {
                        if( j < 5 )
                        {
                            durations[i][j] = 255;
                        }

                        rates[i][j] = default_rate;
                    }
                }

                int current_schedule = -1;
                int offset = 0, time_offset = 0;
                for( itr = ratechanges.begin(); itr != ratechanges.end(); itr++ )
                {
                    ratechange_t &rc = *itr;

                    if( rc.schedule != current_schedule )
                    {
                        offset      = 0;
                        time_offset = 0;

                        current_schedule = rc.schedule;
                    }
                    else
                    {
                        offset++;
                    }

                    if( offset > 5 || rc.schedule < 0 || rc.schedule > 3 )
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        continue;
                    }

                    if( offset == 0 && rc.time == 0 )
                    {
                        //  this is a special case, because we can't access
                        //    durations[rc.schedule][offset-1] yet - offset isn't 1 yet

                        rates[rc.schedule][0] = rc.rate;
                    }
                    else
                    {
                        if( offset == 0 )
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - first rate change time for schedule (" << rc.schedule <<
                                                    ") is not midnight, assuming default rate (" << default_rate <<
                                                    ") for midnight until (" << rc.time << ") for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

                            //  rates[rc.schedule][0] was already initialized to default_rate, so just move along
                            offset++;
                        }

                        durations[rc.schedule][offset - 1] = (rc.time - time_offset) / 300;
                        rates[rc.schedule][offset] = rc.rate;

                        if( (offset + 1) <= 5 )
                        {
                            //  this is to work around the 255 * 5 min limitation for switches - this way it doesn't
                            //    jump back to the default rate if only a midnight rate is specified
                            rates[rc.schedule][offset + 1] = rc.rate;
                        }

                        time_offset = rc.time - (rc.time % 300);  //  make sure we don't miss the 5-minute marks
                    }
                }


                for( offset = 0; offset < 8; offset++ )
                {
                    //  write the day table

                    int byte = 1 - (offset / 4);
                    int bitoffset = (2 * offset) % 8;

                    TOU_OutMessage1->Buffer.BSt.Message[byte] |= (day_schedules[offset] & 0x03) << bitoffset;
                }

                for( offset = 0; offset < 5; offset++ )
                {
                    //  write the durations

                    TOU_OutMessage1->Buffer.BSt.Message[offset + 2] = durations[0][offset];
                    TOU_OutMessage1->Buffer.BSt.Message[offset + 9] = durations[1][offset];

                    TOU_OutMessage2->Buffer.BSt.Message[offset + 0] = durations[2][offset];
                    TOU_OutMessage2->Buffer.BSt.Message[offset + 7] = durations[3][offset];
                }

                //  write the rates for schedules 1 and 2
                TOU_OutMessage1->Buffer.BSt.Message[7]  = ((rates[1][5] & 0x03)  << 6) |
                                                          ((rates[1][4] & 0x03)  << 4) |
                                                          ((rates[0][5] & 0x03)  << 2) |
                                                          ((rates[0][4] & 0x03)  << 0);

                TOU_OutMessage1->Buffer.BSt.Message[8]  = ((rates[0][3] & 0x03)  << 6) |
                                                          ((rates[0][2] & 0x03)  << 4) |
                                                          ((rates[0][1] & 0x03)  << 2) |
                                                          ((rates[0][0] & 0x03)  << 0);

                TOU_OutMessage1->Buffer.BSt.Message[14] = ((rates[1][3] & 0x03)  << 6) |
                                                          ((rates[1][2] & 0x03)  << 4) |
                                                          ((rates[1][1] & 0x03)  << 2) |
                                                          ((rates[1][0] & 0x03)  << 0);

                //  write the rates for schedule 3
                TOU_OutMessage2->Buffer.BSt.Message[5]  = ((rates[2][5] & 0x03)  << 2) |
                                                          ((rates[2][4] & 0x03)  << 0);

                TOU_OutMessage2->Buffer.BSt.Message[6]  = ((rates[2][3] & 0x03)  << 6) |
                                                          ((rates[2][2] & 0x03)  << 4) |
                                                          ((rates[2][1] & 0x03)  << 2) |
                                                          ((rates[2][0] & 0x03)  << 0);

                //  write the rates for schedule 4
                TOU_OutMessage2->Buffer.BSt.Message[12] = ((rates[3][5] & 0x03)  << 2) |
                                                          ((rates[3][4] & 0x03)  << 0);

                TOU_OutMessage2->Buffer.BSt.Message[13] = ((rates[3][3] & 0x03)  << 6) |
                                                          ((rates[3][2] & 0x03)  << 4) |
                                                          ((rates[3][1] & 0x03)  << 2) |
                                                          ((rates[3][0] & 0x03)  << 0);

                TOU_OutMessage2->Buffer.BSt.Message[14] = default_rate;

                outList.push_back(TOU_OutMessage2);
                outList.push_back(TOU_OutMessage1);

                TOU_OutMessage1 = 0;
                TOU_OutMessage2 = 0;

                delete OutMessage;  //  we didn't use it, we made our own
                OutMessage = 0;

                found = true;
            }
        }
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

    //  if it's a KWH request for rate ABCD - rate T should fall through to a normal KWH request
    if( (parse.getFlags() &  CMD_FLAG_GV_KWH) &&
        (parse.getFlags() & (CMD_FLAG_GV_RATEMASK ^ CMD_FLAG_GV_RATET)) )
    {
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
    else if( parse.isKeyValid("lp_command") )  //  load profile
    {
        unsigned long request_time, relative_time;

        int request_channel;
        int year, month, day, hour, minute;
        int interval_len, block_len;

        string cmd = parse.getsValue("lp_command");

        if( !cmd.compare("status") )
        {
            CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), OutMessage->Request.CommandStr);

            ReturnMsg->setUserMessageId(OutMessage->Request.UserID);

            string lp_status_string;
            lp_status_string += getName() + " / Load profile request status:\n";

            interval_len = getLoadProfileInterval(_llpInterest.channel);

            if( _llpInterest.time_end > (_llpInterest.time + _llpInterest.offset + (interval_len * 6) + interval_len) )
            {
                lp_status_string += "Current interval: " + CtiTime(_llpInterest.time + _llpInterest.offset + interval_len).asString() + "\n";
                lp_status_string += "Ending interval:  " + CtiTime(_llpInterest.time_end).asString() + "\n";
            }
            else
            {
                lp_status_string += "No active load profile requests for this device\n";
                if( _llpInterest.failed )
                {
                    lp_status_string += "Last request failed at interval: " + CtiTime(_llpInterest.time + _llpInterest.offset + interval_len).asString() + "\n";
                }

                if( _llpInterest.time_end > (DawnOfTime + rwEpoch) )
                {
                    lp_status_string += "Last request end time: " + CtiTime(_llpInterest.time_end).asString() + "\n";
                }
            }

            ReturnMsg->setResultString(lp_status_string.c_str());

            retMsgHandler( OutMessage->Request.CommandStr, NoError, ReturnMsg, vgList, retList, true );

            delete OutMessage;
            OutMessage = 0;
            found = false;
            nRet  = NoError;
        }
        else if( !cmd.compare("cancel") )
        {
            //  reset it, that way it'll end immediately
            _llpInterest.time_end = 0;

            CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), OutMessage->Request.CommandStr);

            ReturnMsg->setUserMessageId(OutMessage->Request.UserID);

            ReturnMsg->setResultString(getName() + " / Load profile request cancelled\n");

            retMsgHandler( OutMessage->Request.CommandStr, NoError, ReturnMsg, vgList, retList, true );

            delete OutMessage;
            OutMessage = 0;
            found = false;
            nRet  = NoError;
        }
        else
        {
            request_channel = parse.getiValue("lp_channel");

            if( request_channel >  0 &&
                request_channel <= LPChannels )
            {
                interval_len = getLoadProfileInterval(request_channel);

                block_len = 6 * interval_len;

                //  grab the beginning date
                CtiTokenizer date_tok(parse.getsValue("lp_date_start"));
                month = atoi(date_tok("-/").data());
                day   = atoi(date_tok("-/").data());
                year  = atoi(date_tok("-/").data());
                //  note that this code assumes that the current century is 20xx - this will need to change in 2100
                if( year < 100 )    year += 2000;

                if( !cmd.compare("lp") )
                {
                    CtiTime time_start, time_end;

                    function = Emetcon::GetValue_LoadProfile;
                    found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

                    //  grab the beginning time, if available
                    if( parse.isKeyValid("lp_time_start") )
                    {
                        CtiTokenizer time_start_tok(parse.getsValue("lp_time_start"));
                        hour   = atoi(time_start_tok(":").data());
                        minute = atoi(time_start_tok(":").data());
                    }
                    else
                    {
                        //  otherwise, default to midnight
                        hour   = 0;
                        minute = 0;
                    }

                    time_start = CtiTime(CtiDate(day, month, year), hour, minute);

                    //  grab the end date, if available
                    if( parse.isKeyValid("lp_date_end") )
                    {
                        CtiTokenizer date_end_tok(parse.getsValue("lp_date_end"));

                        month = atoi(date_end_tok("-/").data());
                        day   = atoi(date_end_tok("-/").data());
                        year  = atoi(date_end_tok("-/").data());
                        //  note that this code assumes that the current century is 20xx - this will need to change in 2100
                        if( year < 100 )    year += 2000;

                        //  grab the end time, if available
                        if( parse.isKeyValid("lp_time_end") )
                        {
                            CtiTokenizer time_end_tok(parse.getsValue("lp_time_end"));

                            hour   = atoi(time_end_tok(":").data());
                            minute = atoi(time_end_tok(":").data());

                            time_end  = CtiTime(CtiDate(day, month, year), hour, minute);
                        }
                        else
                        {
                            //  otherwise, default to the end of the day
                            time_end  = CtiTime(CtiDate(day, month, year));
                            time_end += 86400;  //  end of the day/beginning of the next day
                        }
                    }
                    else
                    {
                        //  otherwise default to the end of the block
                        time_end  = time_start;

                        if( parse.isKeyValid("lp_time_start") )
                        {
                            //  did they want a specific time?
                            time_end += block_len;
                        }
                        else
                        {
                            //  no time specified, they must've wanted a whole day
                            time_end += 86400;
                        }
                    }

                    if( !time_start.isValid() || !time_end.isValid() || (time_start >= time_end) )
                    {
                        CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), OutMessage->Request.CommandStr);

                        ReturnMsg->setUserMessageId(OutMessage->Request.UserID);

                        found = false;
                        nRet  = BADPARAM;

                        string time_error_string = getName() + " / Invalid date/time for LP request (" + parse.getsValue("lp_date_start");

                        if( parse.isKeyValid("lp_time_start") )
                        {
                            time_error_string += " @ " + parse.getsValue("lp_time_start");
                        }

                        if( parse.isKeyValid("lp_date_end") )
                        {
                            time_error_string += " - ";

                            time_error_string += parse.getsValue("lp_date_end");

                            if( parse.isKeyValid("lp_time_end") )
                            {
                                time_error_string += " @ " + parse.getsValue("lp_time_end");
                            }
                        }

                        time_error_string += ")";

                        ReturnMsg->setResultString(time_error_string);

                        retMsgHandler( OutMessage->Request.CommandStr, NoMethod, ReturnMsg, vgList, retList, true );
                    }
                    else
                    {
                        //  FIXME:  we must replicate this functionality in the decode portion - right now, _llpInterest.offset
                        //            is being overwritten, resulting in faulty decodes
                        request_time  = time_start.seconds();
                        request_time -= request_time % interval_len;
                        request_time -= interval_len;  //  we report interval-ending, yet request interval-beginning...  so back that thing up

                        _llpInterest.time_end = time_end.seconds();

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

                                interest_om->Buffer.BSt.Function  = FuncWrite_LLPInterestPos;
                                interest_om->Buffer.BSt.IO        = Emetcon::IO_Function_Write;
                                interest_om->Buffer.BSt.Length    = FuncWrite_LLPInterestLen;
                                interest_om->MessageFlags        |= MessageFlag_ExpectMore;

                                unsigned long utc_time = request_time;

                                interest_om->Buffer.BSt.Message[0] = gMCT400SeriesSPID;

                                interest_om->Buffer.BSt.Message[1] = request_channel  & 0x000000ff;

                                interest_om->Buffer.BSt.Message[2] = (utc_time >> 24) & 0x000000ff;
                                interest_om->Buffer.BSt.Message[3] = (utc_time >> 16) & 0x000000ff;
                                interest_om->Buffer.BSt.Message[4] = (utc_time >>  8) & 0x000000ff;
                                interest_om->Buffer.BSt.Message[5] = (utc_time)       & 0x000000ff;

                                outList.push_back(interest_om);
                                interest_om = 0;
                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " **** Checkpoint - unable to create outmessage, cannot set interval **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                            }
                        }

                        OutMessage->Buffer.BSt.Function = function;
                        OutMessage->Buffer.BSt.IO       = Emetcon::IO_Function_Read;
                        OutMessage->Buffer.BSt.Length   = 13;

                        function = Emetcon::GetValue_LoadProfile;

                        if( strstr(OutMessage->Request.CommandStr, " background") )
                        {
                            CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), OutMessage->Request.CommandStr);

                            ReturnMsg->setUserMessageId(OutMessage->Request.UserID);
                            ReturnMsg->setConnectionHandle(OutMessage->Request.Connection);
                            ReturnMsg->setResultString(getName() + " / Load profile request submitted for background processing - use \"getvalue lp status\" to check progress");

                            retMsgHandler( OutMessage->Request.CommandStr, NoError, ReturnMsg, vgList, retList, true );

                            OutMessage->Priority = 8;
                            //  make sure the OM doesn't report back to Commander
                            OutMessage->Request.Connection = 0;
                        }

                        nRet = NoError;
                    }
                }
                else if( !cmd.compare("peak") )
                {
                    //  !!!  FIXME: this will not allow reporting on any load profile interval size smaller than 1 hour  !!!
                    if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec)         == Sspec &&
                        (getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) >= SspecRev_NewLLP_Min ||
                         getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) == 253) )  //  Chef's Special for JSW
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
                            ReturnMsg->setResultString(string(getName()
                                                              + " / Load profile reporting currently only supported for SSPEC "
                                                              + CtiNumStr(Sspec).toString().c_str()
                                                              + " revision "
                                                              + (char)('A' + SspecRev_NewLLP_Min - 1)
                                                              + " and up"));

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
                            request_time  = CtiTime(CtiDate(day + 1, month, year)).seconds() - 1;

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

                                    interest_om->Buffer.BSt.Function  = FuncWrite_LLPPeakInterestPos;
                                    interest_om->Buffer.BSt.IO        = Emetcon::IO_Function_Write;
                                    interest_om->Buffer.BSt.Length    = FuncWrite_LLPPeakInterestLen;
                                    interest_om->MessageFlags        |= MessageFlag_ExpectMore;

                                    unsigned long utc_time = request_time;

                                    interest_om->Buffer.BSt.Message[0] = gMCT400SeriesSPID;

                                    interest_om->Buffer.BSt.Message[1] = request_channel  & 0x000000ff;

                                    interest_om->Buffer.BSt.Message[2] = (utc_time >> 24) & 0x000000ff;
                                    interest_om->Buffer.BSt.Message[3] = (utc_time >> 16) & 0x000000ff;
                                    interest_om->Buffer.BSt.Message[4] = (utc_time >>  8) & 0x000000ff;
                                    interest_om->Buffer.BSt.Message[5] = (utc_time)       & 0x000000ff;

                                    interest_om->Buffer.BSt.Message[6] = request_range    & 0x000000ff;

                                    //  add a bit of a delay so the 410 can calculate...
                                    //    this delay may need to be increased by other means, depending
                                    //    on how long the larger peak report calculations take
                                    interest_om->MessageFlags |= MessageFlag_AddSilence;

                                    outList.push_back(interest_om);
                                    interest_om = 0;
                                }
                                else
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " **** Checkpoint - unable to create outmessage, cannot set interval **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                    string temp = "Bad channel specification - Acceptable values:  1-4";
                    errRet->setResultString( temp );
                    errRet->setStatus(NoMethod);
                    retList.push_back( errRet );
                    errRet = NULL;
                }
            }
        }
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


    //  if it's a KWH request for rate ABCD - rate T should fall through to a normal KWH request
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
    else if( parse.isKeyValid("tou") )
    {
        found = true;

        if( parse.isKeyValid("tou_schedule") )
        {
            int schedulenum = parse.getiValue("tou_schedule");

            if( schedulenum == 1 || schedulenum == 2 )
            {
                OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Pos;
                OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Len;
                OutMessage->Buffer.BSt.IO       = Emetcon::IO_Function_Read;
            }
            else if( schedulenum == 3 || schedulenum == 4 )
            {
                OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Pos;
                OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Len;
                OutMessage->Buffer.BSt.IO       = Emetcon::IO_Function_Read;
            }
            else
            {
                errRet->setResultString("invalid schedule number " + CtiNumStr(schedulenum));
                retList.push_back(errRet);
                errRet = 0;

                found = false;
            }
        }
        else
        {
            OutMessage->Buffer.BSt.Function = FuncRead_TOUStatusPos;
            OutMessage->Buffer.BSt.Length   = FuncRead_TOUStatusLen;
            OutMessage->Buffer.BSt.IO       = Emetcon::IO_Function_Read;
        }

        OutMessage->Sequence = Emetcon::GetConfig_TOU;
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
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;

    return nRet;
}

int CtiDeviceMCT410::executePutConfigTOU(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;

    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    long value;
    if(deviceConfig)
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTTOU);

        if(tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTTOU)
        {
            long dayTable, daySchedule1, daySchedule2, daySchedule3, daySchedule4;

            MCT_TOU_SPtr config = boost::static_pointer_cast< ConfigurationPart<MCT_TOU> >(tempBasePtr);
            dayTable = config->getLongValueFromKey(DayTable);
            daySchedule1 = config->getLongValueFromKey(DaySchedule1);
            daySchedule2 = config->getLongValueFromKey(DaySchedule2);
            daySchedule3 = config->getLongValueFromKey(DaySchedule3);
            daySchedule4 = config->getLongValueFromKey(DaySchedule4);

            if(dayTable == numeric_limits<long>::min() || daySchedule1 == numeric_limits<long>::min() || daySchedule2 == numeric_limits<long>::min()
               || daySchedule3 == numeric_limits<long>::min() || daySchedule4 == numeric_limits<long>::min())
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if(parse.isKeyValid("force") || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DayTable) != dayTable
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1) != daySchedule1
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2) != daySchedule2
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3) != daySchedule3
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4) != daySchedule4)
                {
                    OutMessage->Buffer.BSt.Function   = FuncWrite_TOUSchedule1Pos;
                    OutMessage->Buffer.BSt.Length     = FuncWrite_TOUSchedule1Len;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0] = (dayTable>>8);
                    OutMessage->Buffer.BSt.Message[1] = (dayTable);
                    OutMessage->Buffer.BSt.Message[2] = (daySchedule1>>48);//Fun!
                    OutMessage->Buffer.BSt.Message[3] = (daySchedule1>>40);
                    OutMessage->Buffer.BSt.Message[4] = (daySchedule1>>32);
                    OutMessage->Buffer.BSt.Message[5] = (daySchedule1>>24);
                    OutMessage->Buffer.BSt.Message[6] = (daySchedule1>>16);
                    OutMessage->Buffer.BSt.Message[7] = ((daySchedule1>>4)&0xF0 || (daySchedule2>>8)&0x0F);//Fun!
                    OutMessage->Buffer.BSt.Message[8] = (daySchedule1);
                    OutMessage->Buffer.BSt.Message[9] = (daySchedule2>>48);
                    OutMessage->Buffer.BSt.Message[10] = (daySchedule2>>40);
                    OutMessage->Buffer.BSt.Message[11] = (daySchedule2>>32);
                    OutMessage->Buffer.BSt.Message[12] = (daySchedule2>>24);
                    OutMessage->Buffer.BSt.Message[13] = (daySchedule2>>16);
                    OutMessage->Buffer.BSt.Message[14] = (daySchedule2);

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Function   = Memory_TOUDayTablePos;
                    OutMessage->Buffer.BSt.Function   = Memory_TOUDayTableLen + Memory_TOUDailySched1Len + Memory_TOUDailySched2Len;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                    OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                    OutMessage->Priority             += 1;//return to normal
                }

                if(parse.isKeyValid("force") || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3) != daySchedule3
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4) != daySchedule4)
                {
                    OutMessage->Buffer.BSt.Function   = FuncWrite_TOUSchedule2Pos;
                    OutMessage->Buffer.BSt.Length     = FuncWrite_TOUSchedule2Len;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0] = (daySchedule3>>48);//Fun!
                    OutMessage->Buffer.BSt.Message[1] = (daySchedule3>>40);
                    OutMessage->Buffer.BSt.Message[2] = (daySchedule3>>32);
                    OutMessage->Buffer.BSt.Message[3] = (daySchedule3>>24);
                    OutMessage->Buffer.BSt.Message[4] = (daySchedule3>>16);
                    OutMessage->Buffer.BSt.Message[5] = (daySchedule3>>8);
                    OutMessage->Buffer.BSt.Message[6] = (daySchedule3);

                    OutMessage->Buffer.BSt.Message[7] = (daySchedule4>>48);
                    OutMessage->Buffer.BSt.Message[8] = (daySchedule4>>40);
                    OutMessage->Buffer.BSt.Message[9] = (daySchedule4>>32);
                    OutMessage->Buffer.BSt.Message[10] = (daySchedule4>>24);
                    OutMessage->Buffer.BSt.Message[11] = (daySchedule4>>16);
                    OutMessage->Buffer.BSt.Message[12] = (daySchedule4>>8);
                    OutMessage->Buffer.BSt.Message[13] = (daySchedule4);
                    OutMessage->Buffer.BSt.Message[14] = (DefaultTOURate);

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Function   = Memory_TOUDailySched1Pos;
                    OutMessage->Buffer.BSt.Function   = Memory_TOUDailySched3Len + Memory_TOUDailySched4Len + Memory_DefaultTOURateLen;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                    OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                    OutMessage->Priority             += 1;//return to normal
                }
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;

    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << CtiTime() << " **** Checkpoint - TOU config not defined for MCT410 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

    return NORMAL;
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
                dout << CtiTime() << " **** Checkpoint - Cycle Requires revision > " << (string)CtiNumStr(SspecRev_Disconnect_Cycle-1) << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << CtiTime() << " Device: " << getName() << " Revision: " << (string)CtiNumStr(revision) << endl;
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
                dout << CtiTime() << " **** Checkpoint - Disconnect Requires revision > " << (string)CtiNumStr(SspecRev_Disconnect_Min-1) << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << CtiTime() << " Device: " << getName() << " Revision: " << (string)CtiNumStr(revision) << endl;
                nRet = FNI;
            }
            else
            {
                if(parse.isKeyValid("force")
                   || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DemandThreshold) != threshold
                   || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_ConnectDelay)    != delay )
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
    string resultString, freeze_info_string;
    CtiTime pointTime;
    bool valid_data = true;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    point_info_t pi, pi_freezecount;

    CtiPointSPtr         pPoint;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
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
                InMessage->Sequence == Cti::Protocol::Emetcon::GetValue_Default )
            {
                //  normal KWH read, nothing too special

                pi = getData(DSt->Message + offset, 3, ValueType_Accumulator);

                pPoint = getDevicePointOffsetTypeEqual( 1 + i, PulseAccumulatorPointType );

                pointTime -= pointTime.seconds() % 60;
            }
            else if( InMessage->Sequence == Cti::Protocol::Emetcon::GetValue_FrozenKWH )
            {
                //  but this is where the action is - frozen decode

                if( i ) offset++;  //  so that, for the frozen read, it goes 0, 4, 7 to step past the freeze counter in position 3

                pi = getData(DSt->Message + offset, 3, ValueType_FrozenAccumulator);

                if( pi.freeze_bit == _expected_freeze )  //  low-order bit indicates which freeze caused the value to be stored
                {
                    pPoint = getDevicePointOffsetTypeEqual( 1 + i, PulseAccumulatorPointType );

                    //  assign time from the last freeze time, if the lower bit of dp.first matches the last freeze
                    //    and the freeze counter (DSt->Message[3]) is what we expect
                    //  also, archive the received freeze and the freeze counter into the dynamicpaoinfo table

                    if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp) )
                    {
                        pointTime  = CtiTime(getDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp) + rwEpoch);
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

                    freeze_info_string = " @ " + pointTime.asString();
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
                // handle accumulator data here
                if( pPoint )
                {
                    // 24 bit pulse value
                    pi.value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(pi.value);

                    if( pi.quality != InvalidQuality )
                    {
                        resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces()) + freeze_info_string;

                        pData = makePointDataMsg(pPoint, pi, resultString);

                        if(pData != NULL)
                        {
                            pData->setTime( pointTime );
                            ReturnMsg->PointData().push_back(pData);
                            pData = NULL;  // We just put it on the list...
                        }
                    }
                    else
                    {
                        //  !!!  //  !!!  add info here  !!!

                        resultString = getName() + " / " + pPoint->getName() + " = (invalid data)" + freeze_info_string;

                        ReturnMsg->setResultString(ReturnMsg->ResultString() + resultString);
                    }
                }
                else if( i == 0 )
                {
                    if( pi.quality != InvalidQuality )
                    {
                        resultString = getName() + " / Meter Reading = " + CtiNumStr(pi.value) + freeze_info_string + "  --  POINT UNDEFINED IN DB";
                    }
                    else
                    {
                        resultString = getName() + " / Meter Reading = (invalid data) --  POINT UNDEFINED IN DB";
                    }

                    ReturnMsg->setResultString(ReturnMsg->ResultString() + resultString);
                }
            }
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValueDemand(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int status = NORMAL;

    point_info_t pi;
    string resultString,
              pointString;

    INT ErrReturn = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    CtiPointSPtr    pPoint;
    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
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

            pi = getData(DSt->Message + offset, 2, ValueType_KW);

            //  turn raw pulses into a demand reading
            pi.value *= double(3600 / getDemandInterval());

            // look for first defined DEMAND accumulator
            if( pPoint = getDevicePointOffsetTypeEqual( i, DemandAccumulatorPointType ) )
            {
                CtiTime pointTime;

                pi.value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(pi.value);

                pointString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value,
                                                                                        boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

                if(pData = makePointDataMsg(pPoint, pi, pointString))
                {
                    pointTime -= pointTime.seconds() % getDemandInterval();
                    pData->setTime( pointTime );
                    ReturnMsg->PointData().push_back(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            else if( i == 1 )
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
        }

        pi = getData(DSt->Message + 2, 2, ValueType_Voltage);

        if( pPoint = getDevicePointOffsetTypeEqual( PointOffset_Voltage, DemandAccumulatorPointType ) )
        {
            CtiTime pointTime;

            pi.value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(pi.value);

            pointString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value,
                                                                                    boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

            if( pData = makePointDataMsg(pPoint, pi, pointString) )
            {
                //  change this to be the voltage averaging rate - this will be in the configs
                //  pointTime -= pointTime.seconds() % getDemandInterval();
                pData->setTime( pointTime );
                ReturnMsg->PointData().push_back(pData);
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

        if( pPoint = getDevicePointOffsetTypeEqual( PointOffset_Accumulator_Powerfail, PulseAccumulatorPointType ) )
        {
            CtiTime pointTime;

            pi.value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(pi.value);

            pointString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value,
                                                                                     boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

            if( pData = makePointDataMsg(pPoint, pi, pointString) )
            {
                ReturnMsg->PointData().push_back(pData);
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


INT CtiDeviceMCT410::decodeGetValuePeakDemand(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int          status = NORMAL,
                 pointoffset;
    point_info_t pi_kw,
                 pi_kw_time,
                 pi_kwh,
                 pi_freezecount;
    CtiTime       kw_time,
                 kwh_time;
    bool         valid_data = true;

    CtiTableDynamicPaoInfo::Keys key_peak_timestamp;

    string result_string, freeze_info_string;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiPointSPtr    kw_point,
                    kwh_point;
    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData     = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** TOU/Peak Demand Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    /*
        Computed TOU point offsets, for reference:

            pulse accumulators:

                kwh rate a: 101
                kwh rate b: 121
                kwh rate c: 141
                kwh rate d: 161

            demand accumulators:

                peak kw rate a: 111
                peak kw rate b: 131
                peak kw rate c: 151
                peak kw rate d: 171

    */

    pointoffset = 1;

    if( parse.getFlags() & (CMD_FLAG_GV_RATEMASK ^ CMD_FLAG_GV_RATET) )
    {
        pointoffset += PointOffset_TOUBase;

        //  need to add smarts for multiple channels when applicable
        if(      parse.getFlags() & CMD_FLAG_GV_RATEA )  pointoffset += 0; //  no increment for rate A
        else if( parse.getFlags() & CMD_FLAG_GV_RATEB )  pointoffset += PointOffset_RateOffset * 1;
        else if( parse.getFlags() & CMD_FLAG_GV_RATEC )  pointoffset += PointOffset_RateOffset * 2;
        else if( parse.getFlags() & CMD_FLAG_GV_RATED )  pointoffset += PointOffset_RateOffset * 3;
    }

    if(      parse.getFlags() & CMD_FLAG_GV_RATEA )  key_peak_timestamp = CtiTableDynamicPaoInfo::Key_FrozenRateAPeakTimestamp;
    else if( parse.getFlags() & CMD_FLAG_GV_RATEB )  key_peak_timestamp = CtiTableDynamicPaoInfo::Key_FrozenRateBPeakTimestamp;
    else if( parse.getFlags() & CMD_FLAG_GV_RATEC )  key_peak_timestamp = CtiTableDynamicPaoInfo::Key_FrozenRateCPeakTimestamp;
    else if( parse.getFlags() & CMD_FLAG_GV_RATED )  key_peak_timestamp = CtiTableDynamicPaoInfo::Key_FrozenRateDPeakTimestamp;
    else                                             key_peak_timestamp = CtiTableDynamicPaoInfo::Key_FrozenDemandPeakTimestamp;

    if( !(status = decodeCheckErrorReturn(InMessage, retList, outList)) )
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        if( parse.getFlags() & (CMD_FLAG_GV_RATEMASK ^ CMD_FLAG_GV_RATET) )
        {
            //  TOU memory layout
            pi_kwh         = getData(DSt->Message,     3, ValueType_Accumulator);

            pi_kw          = getData(DSt->Message + 3, 2, ValueType_KW);
            pi_kw_time     = getData(DSt->Message + 5, 4, ValueType_Raw);
        }
        else
        {
            //  normal peak memory layout
            pi_kw          = getData(DSt->Message,     2, ValueType_KW);
            pi_kw_time     = getData(DSt->Message + 2, 4, ValueType_Raw);

            pi_kwh         = getData(DSt->Message + 6, 3, ValueType_Accumulator);

            pi_freezecount = getData(DSt->Message + 9, 1, ValueType_Raw);
        }

        //  turn raw pulses into a demand reading
        pi_kw.value *= double(3600 / getDemandInterval());

        kw_time      = CtiTime(pi_kw_time.value);

        if( parse.getFlags() & CMD_FLAG_FROZEN )
        {
            if( _expected_freeze < 0 && hasDynamicInfo(CtiTableDynamicPaoInfo::Key_ExpectedFreeze) )
            {
                _expected_freeze = getDynamicInfo(CtiTableDynamicPaoInfo::Key_ExpectedFreeze);
            }

            if( _freeze_counter  < 0 && hasDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeCounter) )
            {
                _freeze_counter = getDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeCounter);
            }

            if( _freeze_counter < 0 || (pi_freezecount.value >= _freeze_counter) )
            {
                if( pi_freezecount.value > (_freeze_counter + 1) )
                {
                    //  it's incremented by more than one, yet the reading seems to be valid, parity-wise - we need to yelp, at least

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - incoming freeze counter (" << pi_freezecount.value <<
                                            ") has increased by more than expected value (" << _freeze_counter + 1 <<
                                            ") on device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
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

            if( kw_time.seconds() >= (getDynamicInfo(key_peak_timestamp) ) )
            {
                if( kw_time.seconds() <= (getDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp)) )
                {
                    if( pi_kwh.freeze_bit == _expected_freeze )  //  LSB indicates which freeze caused the value to be stored
                    {
                        //  success - allow normal processing

                        kw_point  = getDevicePointOffsetTypeEqual(pointoffset + PointOffset_PeakOffset, DemandAccumulatorPointType);

                        kwh_point = getDevicePointOffsetTypeEqual(pointoffset, PulseAccumulatorPointType );

                        setDynamicInfo(key_peak_timestamp, kw_time.seconds());

                        if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp) )
                        {
                            kwh_time  = CtiTime(getDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp));
                            kwh_time -= kwh_time.seconds() % 300;
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" does not have a freeze timestamp for KWH timestamp, defaulting to current time **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }

                        freeze_info_string  = " @ " + kwh_time.asString();

                        _freeze_counter = pi_freezecount.value;

                        setDynamicInfo(CtiTableDynamicPaoInfo::Key_FreezeCounter, _freeze_counter);
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - incoming freeze parity bit (" << pi_kwh.freeze_bit <<
                                                ") does not match expected freeze bit (" << _expected_freeze <<
                                                ") on device \"" << getName() << "\", not sending data **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        valid_data = false;
                        ReturnMsg->setResultString("Freeze parity check failed (" + CtiNumStr(pi_kwh.freeze_bit) + ") != (" + CtiNumStr(_expected_freeze) + "), last recorded freeze sent at " + RWTime(getDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp)).asString());
                        status = NOTNORMAL;
                    }
                }
                else
                {
                    valid_data = false;

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - KW peak time \"" << kw_time << "\" is before KW freeze time \"" << RWTime(getDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp)) << ", not sending data **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    ReturnMsg->setResultString("Peak time after freeze (" + kw_time.asString() + ") < (" + RWTime(getDynamicInfo(CtiTableDynamicPaoInfo::Key_DemandFreezeTimestamp)).asString() + ")");
                    status = NOTNORMAL;
                }
            }
            else
            {
                valid_data = false;

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - new KW peak time \"" << kw_time << "\" is before old KW peak time \"" << RWTime(getDynamicInfo(key_peak_timestamp)) << ", not sending data **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                ReturnMsg->setResultString("New KW peak earlier than old KW peak (" + kw_time.asString() + ") < (" + RWTime(getDynamicInfo(key_peak_timestamp)).asString() + ")");
                status = NOTNORMAL;
            }
        }
        else
        {
            //  just a normal peak read, no freeze-related work needed

            kw_point = getDevicePointOffsetTypeEqual(pointoffset + PointOffset_PeakOffset, DemandAccumulatorPointType);

            kwh_point = getDevicePointOffsetTypeEqual( pointoffset, PulseAccumulatorPointType );

            kwh_time  = CtiTime::now();
        }

        if( valid_data )  //  valid
        {
            bool kw_valid = true;

            //  if it's a TOU read and the MCT isn't at least at MCT410_SspecRev_TOUPeak_Min, we omit the data
            if( parse.getFlags() & (CMD_FLAG_GV_RATEMASK ^ CMD_FLAG_GV_RATET) )
            {
                if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec) != Sspec ||
                    getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) < SspecRev_TOUPeak_Min )
                {
                    kw_valid = false;
                }
            }

            if( kw_valid )
            {
                if( kw_point )
                {
                    pi_kw.value = boost::static_pointer_cast<CtiPointNumeric>(kw_point)->computeValueForUOM(pi_kw.value);

                    result_string = getName() + " / " + kw_point->getName() + " = "
                                              + CtiNumStr(pi_kw.value, boost::static_pointer_cast<CtiPointNumeric>(kw_point)->getPointUnits().getDecimalPlaces())
                                              + " @ " + kw_time.asString();

                    if( pData = makePointDataMsg(kw_point, pi_kw, result_string) )
                    {
                        pData->setTime(kw_time);
                        ReturnMsg->PointData().push_back(pData);
                        pData = NULL;  // We just put it on the list...
                    }
                }
                else
                {
                    result_string = getName() + " / Peak Demand = " + CtiNumStr(pi_kw.value) + " @ " + kw_time.asString() + "  --  POINT UNDEFINED IN DB";
                    ReturnMsg->setResultString(result_string);
                }
            }

            if( kwh_point )
            {
                pi_kwh.value = boost::static_pointer_cast<CtiPointNumeric>(kwh_point)->computeValueForUOM(pi_kwh.value);

                result_string  = getName() + " / " + kwh_point->getName() + " = " + CtiNumStr(pi_kwh.value,
                                                                                              boost::static_pointer_cast<CtiPointNumeric>(kwh_point)->getPointUnits().getDecimalPlaces());
                result_string += freeze_info_string;

                if( pData = makePointDataMsg(kwh_point, pi_kwh, result_string) )
                {
                    kwh_time -= kwh_time.seconds() % getDemandInterval();
                    pData->setTime(kwh_time);
                    ReturnMsg->PointData().push_back(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            else
            {
                result_string = getName() + " / Meter Reading = " + CtiNumStr(pi_kwh.value) + freeze_info_string + "  --  POINT UNDEFINED IN DB";
                ReturnMsg->setResultString(ReturnMsg->ResultString() + "\n" + result_string);
            }
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValueVoltage( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    point_info_t pi, max_volt_info, min_volt_info;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiTime minTime, maxTime;
        int minPointOffset, maxPointOffset;
        string resultString, pointString;

        CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg *pData = NULL;

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        max_volt_info  = getData(DSt->Message, 2, ValueType_Voltage);
        maxPointOffset = PointOffset_MaxVoltage;

        pi = getData(DSt->Message + 2, 4, ValueType_Raw);
        maxTime = CtiTime((unsigned long)pi.value);


        min_volt_info  = getData(DSt->Message + 6, 2, ValueType_Voltage);
        minPointOffset = PointOffset_MinVoltage;

        pi = getData(DSt->Message + 8, 4, ValueType_Raw);
        minTime = CtiTime((unsigned long)pi.value);

        CtiPointDataMsg *pdm;
        CtiPointNumericSPtr pt_max_volts = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual( maxPointOffset, DemandAccumulatorPointType )),
                            pt_min_volts = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual( minPointOffset, DemandAccumulatorPointType ));

        if( pt_max_volts )
        {
            max_volt_info.value = pt_max_volts->computeValueForUOM(max_volt_info.value);

            pointString = getName() + " / " + pt_max_volts->getName() + " = " + CtiNumStr(max_volt_info.value) + " @ " + maxTime.asString();

            if( pdm = makePointDataMsg(pt_max_volts, max_volt_info, pointString) )
            {
                pdm->setTime(maxTime);

                ReturnMsg->PointData().push_back(pdm);
                pdm = 0;
            }
        }
        else
        {
            max_volt_info.value *= 0.1;

            if( InMessage->Sequence == Emetcon::GetValue_FrozenVoltage )
            {
                resultString += getName() + " / Frozen Max Voltage = " + CtiNumStr(max_volt_info.value) + " @ " + maxTime.asString() + "\n";
            }
            else
            {
                resultString += getName() + " / Max Voltage = " + CtiNumStr(max_volt_info.value) + " @ " + maxTime.asString() + "\n";
            }
        }

        if( pt_min_volts )
        {
            min_volt_info.value = pt_min_volts->computeValueForUOM(min_volt_info.value);

            pointString = getName() + " / " + pt_min_volts->getName() + " = " + CtiNumStr(min_volt_info.value) + " @ " + minTime.asString();

            if( pdm = makePointDataMsg(pt_min_volts, min_volt_info, pointString) )
            {
                pdm->setTime(minTime);

                ReturnMsg->PointData().push_back(pdm);
                pdm = 0;
            }
        }
        else
        {
            min_volt_info.value *= 0.1;

            if( InMessage->Sequence == Emetcon::GetValue_FrozenVoltage )
            {
                resultString += getName() + " / Frozen Min Voltage = " + CtiNumStr(min_volt_info.value) + " @ " + minTime.asString() + "\n";
            }
            else
            {
                resultString += getName() + " / Min Voltage = " + CtiNumStr(min_volt_info.value) + " @ " + minTime.asString() + "\n";
            }
        }


        ReturnMsg->setResultString(resultString);

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

                    seconds %= 60;
                    minutes %= 60;
                    hours   %= 24;

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

        result_string  = getName() + " / Channel " + CtiNumStr(_llpPeakInterest.channel) + string(" Load Profile Report\n");
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


INT CtiDeviceMCT410::decodeGetConfigTOU(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        string resultString;
        unsigned long time;
        CtiTime tmpTime;

        int schedulenum = parse.getiValue("tou_schedule");

        if( schedulenum > 0 && schedulenum <= 4 )
        {
            schedulenum -= (schedulenum - 1) % 2;

            for( int offset = 0; offset < 2; offset++ )
            {
                int rates, current_rate, previous_rate, byte_offset, time_offset;

                resultString += getName() + " / TOU Schedule " + CtiNumStr(schedulenum + offset) + ":\n";

                if( offset == 0 )
                {
                    rates = InMessage->Buffer.DSt.Message[5] & 0x0f << 8 | InMessage->Buffer.DSt.Message[6];
                    byte_offset = 0;
                }
                else
                {
                    rates = InMessage->Buffer.DSt.Message[5] & 0xf0 << 4 | InMessage->Buffer.DSt.Message[12];
                    byte_offset = 7;
                }

                current_rate = rates & 0x03;
                resultString += "00:00: ";
                resultString += (char)('A' + current_rate);
                resultString += "\n";
                rates >>= 2;

                time_offset = 0;
                previous_rate = current_rate;
                for( int switchtime = 0; switchtime < 5; switchtime++ )
                {
                    int hour, minute;

                    time_offset += InMessage->Buffer.DSt.Message[byte_offset + switchtime] * 300;

                    hour   = time_offset / 3600;
                    minute = (time_offset / 60) % 60;

                    current_rate = rates & 0x03;

                    if( (hour <= 23) && (current_rate != previous_rate) )
                    {
                        resultString += CtiNumStr(hour).zpad(2) + ":" + CtiNumStr(minute).zpad(2) + ": " + (char)('A' + current_rate) + "\n";
                    }

                    previous_rate = current_rate;

                    rates >>= 2;
                }

                resultString += "- end of day - \n\n";
            }
        }
        else
        {
            resultString = getName() + " / TOU Status:\n\n";

            time = InMessage->Buffer.DSt.Message[6] << 24 |
                   InMessage->Buffer.DSt.Message[7] << 16 |
                   InMessage->Buffer.DSt.Message[8] <<  8 |
                   InMessage->Buffer.DSt.Message[9];

            resultString += "Current time: " + CtiTime(time).asString() + "\n";

            int tz_offset = (char)InMessage->Buffer.DSt.Message[10] * 15;

            resultString += "Time zone offset: " + CtiNumStr((float)tz_offset / 60.0, 1) + " hours ( " + CtiNumStr(tz_offset) + " minutes)\n";

            if( InMessage->Buffer.DSt.Message[3] & 0x80 )
            {
                resultString += "Critical peak active\n";
            }
            if( InMessage->Buffer.DSt.Message[4] & 0x80 )
            {
                resultString += "Holiday active\n";
            }
            if( InMessage->Buffer.DSt.Message[4] & 0x40 )
            {
                resultString += "DST active\n";
            }

            resultString += "Current rate: " + string(1, (char)('A' + (InMessage->Buffer.DSt.Message[3] & 0x7f))) + "\n";

            resultString += "Current schedule: " + CtiNumStr((int)(InMessage->Buffer.DSt.Message[4] & 0x03) + 1) + "\n";
/*
            resultString += "Current switch time: ";

            if( InMessage->Buffer.DSt.Message[5] == 0xff )
            {
                resultString += "not active\n";
            }
            else
            {
                 resultString += CtiNumStr((int)InMessage->Buffer.DSt.Message[5]) + "\n";
            }
*/
            resultString += "Default rate: ";

            if( InMessage->Buffer.DSt.Message[2] == 0xff )
            {
                resultString += "No TOU active\n";
            }
            else
            {
                resultString += string(1, (char)('A' + InMessage->Buffer.DSt.Message[2])) + "\n";
            }

            resultString += "\nDay table: \n";

            char *(daynames[8]) = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Holiday"};

            for( int i = 0; i < 8; i++ )
            {
                int dayschedule = InMessage->Buffer.DSt.Message[1 - i/4] >> ((i % 4) * 2) & 0x03;

                resultString += "Schedule " + CtiNumStr(dayschedule + 1) + " - " + daynames[i] + "\n";
            }
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

        point_info_t pi = getData(DSt->Message + 5, 2);

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

        if( InMessage->Buffer.DSt.Message[1] <= 26 )
        {
            sspec += (char)(InMessage->Buffer.DSt.Message[1] + 'A' - 1);
        }
        else
        {
            sspec += CtiNumStr((int)InMessage->Buffer.DSt.Message[1]);
            sspec += " (unreleased/unverified revision)";
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

