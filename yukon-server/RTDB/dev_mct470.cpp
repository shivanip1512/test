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
* REVISION     :  $Revision: 1.114 $
* DATE         :  $Date: 2008/03/26 14:07:39 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include "device.h"
#include "devicetypes.h"
#include "tbl_ptdispatch.h"
#include "dev_mct470.h"
#include "logger.h"
#include "pt_accum.h"
#include "pt_status.h"
#include "numstr.h"
#include "porter.h"
#include "dllyukon.h"
#include "utility.h"
#include "numstr.h"
#include "ctistring.h"
#include "config_data_mct.h"
#include <string>

using Cti::Protocol::Emetcon;
using namespace Cti::Config;

const CtiDeviceMCT470::CommandSet      CtiDeviceMCT470::_commandStore = CtiDeviceMCT470::initCommandStore();
const CtiDeviceMCT470::ConfigPartsList CtiDeviceMCT470::_config_parts = CtiDeviceMCT470::initConfigParts();

const CtiDeviceMCT470::DynamicPaoAddressing_t         CtiDeviceMCT470::_dynPaoAddressing     = CtiDeviceMCT470::initDynPaoAddressing();
const CtiDeviceMCT470::DynamicPaoFunctionAddressing_t CtiDeviceMCT470::_dynPaoFuncAddressing = CtiDeviceMCT470::initDynPaoFuncAddressing();

const CtiDeviceMCT470::error_set CtiDeviceMCT470::_error_info_old_lp    = CtiDeviceMCT470::initErrorInfoOldLP();
const CtiDeviceMCT470::error_set CtiDeviceMCT470::_error_info_lgs4      = CtiDeviceMCT470::initErrorInfoLGS4();
const CtiDeviceMCT470::error_set CtiDeviceMCT470::_error_info_alphaa3   = CtiDeviceMCT470::initErrorInfoAlphaA3();
const CtiDeviceMCT470::error_set CtiDeviceMCT470::_error_info_alphapp   = CtiDeviceMCT470::initErrorInfoAlphaPP();
const CtiDeviceMCT470::error_set CtiDeviceMCT470::_error_info_gekv      = CtiDeviceMCT470::initErrorInfoGEkV();
const CtiDeviceMCT470::error_set CtiDeviceMCT470::_error_info_sentinel  = CtiDeviceMCT470::initErrorInfoSentinel();

CtiDeviceMCT470::CtiDeviceMCT470( ) :
    _lastConfigRequest(0)
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

CtiDeviceMCT470::ConfigPartsList CtiDeviceMCT470::initConfigParts()
{
    CtiDeviceMCT470::ConfigPartsList tempList;

    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_dst);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_demand_lp);
    //tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_options);
    //tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_addressing);
    //tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_holiday);
    //tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_llp);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_lpchannel);
    //tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_relays);
    //tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_precanned_table);

    return tempList;
}

CtiDeviceMCT470::ConfigPartsList CtiDeviceMCT470::getPartsList()
{
    return _config_parts;
}

CtiDeviceMCT470::ConfigPartsList CtiDeviceMCT470::getBasicPartsList()
{
    ConfigPartsList tempList;
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_timezone);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_demand_lp);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_lpchannel);
    CtiString type = getTypeStr();
    if( type.contains("470") )
    {
        tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_configbyte);
    }
    return tempList;
}

CtiDeviceMCT470::CommandSet CtiDeviceMCT470::initCommandStore( )
{
    CommandSet cs;

    cs.insert(CommandStore(Emetcon::Scan_Accum,                 Emetcon::IO_Function_Read,  FuncRead_MReadPos,            FuncRead_MReadLen));
    cs.insert(CommandStore(Emetcon::GetValue_KWH,               Emetcon::IO_Function_Read,  FuncRead_MReadPos,            FuncRead_MReadLen));
    cs.insert(CommandStore(Emetcon::Scan_Integrity,             Emetcon::IO_Function_Read,  FuncRead_DemandPos,           FuncRead_DemandLen));
    cs.insert(CommandStore(Emetcon::GetValue_Demand,            Emetcon::IO_Function_Read,  FuncRead_DemandPos,           FuncRead_DemandLen));
    cs.insert(CommandStore(Emetcon::Scan_LoadProfile,           Emetcon::IO_Function_Read,  0,                             0));
    cs.insert(CommandStore(Emetcon::GetValue_Demand,            Emetcon::IO_Function_Read,  FuncRead_DemandPos,           FuncRead_DemandLen));
    cs.insert(CommandStore(Emetcon::GetValue_PeakDemand,        Emetcon::IO_Function_Read,  FuncRead_PeakDemandPos,       FuncRead_PeakDemandLen));
    cs.insert(CommandStore(Emetcon::PutValue_KYZ,               Emetcon::IO_Function_Write, FuncWrite_CurrentReading,     FuncWrite_CurrentReadingLen));
    cs.insert(CommandStore(Emetcon::PutConfig_Raw,              Emetcon::IO_Write,          0,                             0));  //  filled in later
    cs.insert(CommandStore(Emetcon::GetConfig_Raw,              Emetcon::IO_Read,           0,                             0));  //  filled in later
    cs.insert(CommandStore(Emetcon::GetConfig_Multiplier,       Emetcon::IO_Function_Read,  FuncRead_LoadProfileChannel12Pos, FuncRead_LoadProfileChannel12Len));
    cs.insert(CommandStore(Emetcon::PutConfig_Multiplier,       Emetcon::IO_Write,          Memory_ChannelMultiplierPos,  Memory_ChannelMultiplierLen));
    cs.insert(CommandStore(Emetcon::GetConfig_Time,             Emetcon::IO_Read,           Memory_TimeZoneOffsetPos,     Memory_TimeZoneOffsetLen +
                                                                                                                          Memory_RTCLen));
    cs.insert(CommandStore(Emetcon::GetConfig_TSync,            Emetcon::IO_Read,           Memory_LastTSyncPos,          Memory_LastTSyncLen));
    cs.insert(CommandStore(Emetcon::PutConfig_TimeZoneOffset,   Emetcon::IO_Write,          Memory_TimeZoneOffsetPos,     Memory_TimeZoneOffsetLen));
    cs.insert(CommandStore(Emetcon::PutConfig_Intervals,        Emetcon::IO_Function_Write, FuncWrite_IntervalsPos,       FuncWrite_IntervalsLen));
    cs.insert(CommandStore(Emetcon::GetConfig_Intervals,        Emetcon::IO_Read,           Memory_IntervalsPos,          Memory_IntervalsLen));
    cs.insert(CommandStore(Emetcon::GetConfig_ChannelSetup,     Emetcon::IO_Function_Read,  FuncRead_ChannelSetupDataPos, FuncRead_ChannelSetupDataLen));
    cs.insert(CommandStore(Emetcon::PutConfig_ChannelSetup,     Emetcon::IO_Function_Write, FuncWrite_SetupLPChannelsPos, FuncWrite_SetupLPChannelLen));
    cs.insert(CommandStore(Emetcon::GetValue_LoadProfile,       Emetcon::IO_Function_Read,  0,                             0));
    cs.insert(CommandStore(Emetcon::GetStatus_LoadProfile,      Emetcon::IO_Function_Read,  FuncRead_LPStatusCh1Ch2Pos,   FuncRead_LPStatusLen));
    cs.insert(CommandStore(Emetcon::GetStatus_Internal,         Emetcon::IO_Read,           Memory_StatusPos,             Memory_StatusLen));
    cs.insert(CommandStore(Emetcon::GetValue_PFCount,           Emetcon::IO_Read,           Memory_PowerfailCountPos,     Memory_PowerfailCountLen));
    cs.insert(CommandStore(Emetcon::GetValue_IED,               Emetcon::IO_Function_Read,  0,                            13));  //  filled in by "getvalue ied" code
    cs.insert(CommandStore(Emetcon::GetValue_IEDDemand,         Emetcon::IO_Function_Read,  FuncRead_IED_RealTime,        12));   //  magic number
    cs.insert(CommandStore(Emetcon::GetStatus_IEDDNP,           Emetcon::IO_Function_Read,  FuncRead_IED_Precanned_Last,  13));
    cs.insert(CommandStore(Emetcon::GetConfig_IEDTime,          Emetcon::IO_Function_Read,  FuncRead_IED_TOU_MeterStatus, 13));  //  magic number
    cs.insert(CommandStore(Emetcon::GetConfig_IEDDNP,           Emetcon::IO_Function_Read,  FuncRead_IED_DNPTablePos,     FuncRead_IED_DNPTableLen));
    cs.insert(CommandStore(Emetcon::PutValue_IEDReset,          Emetcon::IO_Function_Write, FuncWrite_IEDCommand,         FuncWrite_IEDCommandLen));
    cs.insert(CommandStore(Emetcon::PutStatus_FreezeOne,        Emetcon::IO_Write,          Command_FreezeOne,             0));
    cs.insert(CommandStore(Emetcon::PutStatus_FreezeTwo,        Emetcon::IO_Write,          Command_FreezeTwo,             0));

    //******************************** Config Related starts here *************************
    cs.insert(CommandStore(Emetcon::PutConfig_Addressing,       Emetcon::IO_Write,          Memory_AddressingPos,        Memory_AddressingLen));
    cs.insert(CommandStore(Emetcon::PutConfig_LongLoadProfile,  Emetcon::IO_Function_Write, FuncWrite_LLPStoragePos,     FuncWrite_LLPStorageLen));
    cs.insert(CommandStore(Emetcon::GetConfig_LongLoadProfile,  Emetcon::IO_Function_Read,  FuncRead_LLPStatusPos,       FuncRead_LLPStatusLen));
    cs.insert(CommandStore(Emetcon::PutConfig_DST,              Emetcon::IO_Write,          Memory_DSTBeginPos,          Memory_DSTBeginLen
                                                                                                                           + Memory_DSTEndLen));

    //  used for both "putconfig install" and "putconfig holiday" commands
    cs.insert(CommandStore(Emetcon::PutConfig_Holiday,          Emetcon::IO_Write,          Memory_Holiday1Pos,          Memory_Holiday1Len
                                                                                                                           + Memory_Holiday2Len
                                                                                                                           + Memory_Holiday3Len));

    cs.insert(CommandStore(Emetcon::GetConfig_Holiday,          Emetcon::IO_Read,           Memory_Holiday1Pos,          Memory_Holiday1Len
                                                                                                                           + Memory_Holiday2Len
                                                                                                                           + Memory_Holiday3Len));

    cs.insert(CommandStore(Emetcon::PutConfig_Options,             Emetcon::IO_Function_Write,  FuncWrite_ConfigAlarmMaskPos,  FuncWrite_ConfigAlarmMaskLen));
    cs.insert(CommandStore(Emetcon::PutConfig_TimeAdjustTolerance, Emetcon::IO_Write,           Memory_TimeAdjustTolerancePos, Memory_TimeAdjustToleranceLen));

    //************************************ End Config Related *****************************

    return cs;
}

CtiDeviceMCT470::DynamicPaoFunctionAddressing_t CtiDeviceMCT470::initDynPaoFuncAddressing()
{
    DynamicPaoAddressing_t addressSet;
    DynamicPaoFunctionAddressing_t functionSet;

    // FuncRead_ChannelSetupDataPos
    addressSet.insert(DynamicPaoAddressing(0, 1, Keys::Key_MCT_LoadProfileChannelConfig1));
    addressSet.insert(DynamicPaoAddressing(1, 1, Keys::Key_MCT_LoadProfileChannelConfig2));
    addressSet.insert(DynamicPaoAddressing(2, 1, Keys::Key_MCT_LoadProfileChannelConfig3));
    addressSet.insert(DynamicPaoAddressing(3, 1, Keys::Key_MCT_LoadProfileChannelConfig4));
    addressSet.insert(DynamicPaoAddressing(4, 1, Keys::Key_MCT_LoadProfileInterval));
    addressSet.insert(DynamicPaoAddressing(5, 1, Keys::Key_MCT_LoadProfileInterval2));
    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(FuncRead_ChannelSetupDataPos,addressSet));
    addressSet.clear();

    // FuncRead_LoadProfileChannel12Pos
    addressSet.insert(DynamicPaoAddressing(0, 1, Keys::Key_MCT_LoadProfileChannelConfig1));
    addressSet.insert(DynamicPaoAddressing(1, 2, Keys::Key_MCT_LoadProfileMeterRatio1));
    addressSet.insert(DynamicPaoAddressing(3, 2, Keys::Key_MCT_LoadProfileKRatio1));
    addressSet.insert(DynamicPaoAddressing(5, 1, Keys::Key_MCT_LoadProfileChannelConfig2));
    addressSet.insert(DynamicPaoAddressing(6, 2, Keys::Key_MCT_LoadProfileMeterRatio2));
    addressSet.insert(DynamicPaoAddressing(8, 2, Keys::Key_MCT_LoadProfileKRatio2));
    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(FuncRead_LoadProfileChannel12Pos,addressSet));
    addressSet.clear();

    // FuncRead_LoadProfileChannel34Pos
    addressSet.insert(DynamicPaoAddressing(0, 1, Keys::Key_MCT_LoadProfileChannelConfig3));
    addressSet.insert(DynamicPaoAddressing(1, 2, Keys::Key_MCT_LoadProfileMeterRatio3));
    addressSet.insert(DynamicPaoAddressing(3, 2, Keys::Key_MCT_LoadProfileKRatio3));
    addressSet.insert(DynamicPaoAddressing(5, 1, Keys::Key_MCT_LoadProfileChannelConfig4));
    addressSet.insert(DynamicPaoAddressing(6, 2, Keys::Key_MCT_LoadProfileMeterRatio4));
    addressSet.insert(DynamicPaoAddressing(8, 2, Keys::Key_MCT_LoadProfileKRatio4));
    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(FuncRead_LoadProfileChannel34Pos,addressSet));
    addressSet.clear();

    // FuncRead_PrecannedTablePos
    addressSet.insert(DynamicPaoAddressing(0, 1, Keys::Key_MCT_PrecannedTableReadInterval));
    addressSet.insert(DynamicPaoAddressing(1, 1, Keys::Key_MCT_PrecannedMeterNumber));
    addressSet.insert(DynamicPaoAddressing(2, 1, Keys::Key_MCT_PrecannedTableType));
    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(FuncRead_PrecannedTablePos,addressSet));
    addressSet.clear();

    // FuncRead_LLPStatusPos
    addressSet.insert(DynamicPaoAddressing(4, 1, Keys::Key_MCT_LLPChannel1Len));
    addressSet.insert(DynamicPaoAddressing(5, 1, Keys::Key_MCT_LLPChannel2Len));
    addressSet.insert(DynamicPaoAddressing(6, 1, Keys::Key_MCT_LLPChannel3Len));
    addressSet.insert(DynamicPaoAddressing(7, 1, Keys::Key_MCT_LLPChannel4Len));
    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(FuncRead_LLPStatusPos,addressSet));
    addressSet.clear();

    // FuncRead_IED_CRCPos
    addressSet.insert(DynamicPaoAddressing( 1, 1, Keys::Key_MCT_LLPChannel1Len));
    addressSet.insert(DynamicPaoAddressing( 2, 1, Keys::Key_MCT_LLPChannel2Len));
    addressSet.insert(DynamicPaoAddressing( 3, 1, Keys::Key_MCT_LLPChannel3Len));
    addressSet.insert(DynamicPaoAddressing( 4, 1, Keys::Key_MCT_LLPChannel4Len));
    addressSet.insert(DynamicPaoAddressing( 5, 1, Keys::Key_MCT_LLPChannel4Len));
    addressSet.insert(DynamicPaoAddressing( 6, 1, Keys::Key_MCT_LLPChannel4Len));
    addressSet.insert(DynamicPaoAddressing( 7, 1, Keys::Key_MCT_LLPChannel4Len));
    addressSet.insert(DynamicPaoAddressing( 8, 1, Keys::Key_MCT_LLPChannel4Len));
    addressSet.insert(DynamicPaoAddressing( 9, 1, Keys::Key_MCT_LLPChannel4Len));
    addressSet.insert(DynamicPaoAddressing(10, 1, Keys::Key_MCT_LLPChannel4Len));
    addressSet.insert(DynamicPaoAddressing(11, 1, Keys::Key_MCT_LLPChannel4Len));
    addressSet.insert(DynamicPaoAddressing(12, 1, Keys::Key_MCT_LLPChannel4Len));
    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(FuncRead_IED_CRCPos,addressSet));
    addressSet.clear();

    // FuncRead_TOUDaySchedulePos
    addressSet.insert(DynamicPaoAddressing( 0, 2, Keys::Key_MCT_DayTable));
    addressSet.insert(DynamicPaoAddressing( 2, 1, Keys::Key_MCT_DefaultTOURate));
    addressSet.insert(DynamicPaoAddressing(10, 1, Keys::Key_MCT_TimeZoneOffset));
    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(FuncRead_TOUDaySchedulePos,addressSet));
    addressSet.clear();

    return functionSet;
}

CtiDeviceMCT470::error_set CtiDeviceMCT470::initErrorInfoOldLP( void )
{
    error_set es;

    es.insert(error_info(0xffff7fff, "Interval not recorded",   InvalidQuality));
    es.insert(error_info(0xffff7ffe, "Pulse count overflow",    OverflowQuality));
    es.insert(error_info(0xffff7ffd, "Time adjusted",           DeviceFillerQuality));

    return es;
}


CtiDeviceMCT470::error_set CtiDeviceMCT470::initErrorInfoLGS4( void )
{
    error_set es;

    es.insert(error_info(0xffffffff, "Interval not recorded",   InvalidQuality));
    es.insert(error_info(0xfffffffe, "Pulse count overflow",    OverflowQuality));
    es.insert(error_info(0xfffffffd, "Time adjusted",           DeviceFillerQuality));

    //  not available for LLP - indicate somehow?
    es.insert(error_info(0xfffffffb, "Time adjusted",           DeviceFillerQuality));

    es.insert(error_info(0xfffffffa, "LP data not available",   InvalidQuality));
    es.insert(error_info(0xfffffff0, "Overflow",                OverflowQuality));

    return es;
}

CtiDeviceMCT470::error_set CtiDeviceMCT470::initErrorInfoAlphaA3( void )
{
    error_set es;

    es.insert(error_info(0xffffffff, "LP data reset",           InvalidQuality));
    //
    // not available for LP
    es.insert(error_info(0xfffffffd, "LP error",                InvalidQuality));

    //  not available for LLP
    es.insert(error_info(0xfffffffb, "Time adjusted",           DeviceFillerQuality));

    es.insert(error_info(0xfffffffa, "LP data not available",   InvalidQuality));
    es.insert(error_info(0xfffffff0, "Overflow",                OverflowQuality));

    return es;
}

CtiDeviceMCT470::error_set CtiDeviceMCT470::initErrorInfoAlphaPP( void )
{
    error_set es;

    es.insert(error_info(0xffffffff, "LP data reset",           InvalidQuality));
    es.insert(error_info(0xfffffffe, "LP config error",         InvalidQuality));
    es.insert(error_info(0xfffffffd, "LP error",                InvalidQuality));

    //  not available for LLP
    es.insert(error_info(0xfffffffb, "Time adjusted",           DeviceFillerQuality));

    es.insert(error_info(0xfffffffa, "LP data not available",   InvalidQuality));
    es.insert(error_info(0xfffffff2, "Interval not recorded",   InvalidQuality));
    es.insert(error_info(0xfffffff1, "Pulse count overflow",    OverflowQuality));
    es.insert(error_info(0xfffffff0, "Overflow",                OverflowQuality));

    return es;
}

CtiDeviceMCT470::error_set CtiDeviceMCT470::initErrorInfoGEkV( void )
{
    error_set es;

    es.insert(error_info(0xffffffff, "LP data reset",           InvalidQuality));

    //  not available for LP
    es.insert(error_info(0xfffffffd, "LP error",                InvalidQuality));

    //  not available for LLP
    es.insert(error_info(0xfffffffb, "Time adjusted",           DeviceFillerQuality));

    es.insert(error_info(0xfffffffa, "LP data not available",   InvalidQuality));
    es.insert(error_info(0xfffffff0, "Overflow",                OverflowQuality));

    return es;
}

CtiDeviceMCT470::error_set CtiDeviceMCT470::initErrorInfoSentinel( void )
{
    error_set es;

    es.insert(error_info(0xffffffff, "LP data reset",           InvalidQuality));
    es.insert(error_info(0xfffffffe, "LP config error",         InvalidQuality));
    es.insert(error_info(0xfffffffd, "LP error",                InvalidQuality));

    //  not available for LLP
    es.insert(error_info(0xfffffffb, "Time adjusted",           DeviceFillerQuality));

    es.insert(error_info(0xfffffffa, "LP data not available",   InvalidQuality));
    es.insert(error_info(0xfffffff0, "Overflow",                OverflowQuality));

    return es;
}


bool CtiDeviceMCT470::getOperation( const UINT &cmd, BSTRUCT &bst ) const
{
    bool found = false;

    CommandSet::const_iterator itr = _commandStore.find(CommandStore(cmd));

    if( itr != _commandStore.end() )
    {
        bst.Function = itr->function;   //  Copy the relevant bits from the commandStore
        bst.Length   = itr->length;
        bst.IO       = itr->io;

        found = true;
    }
    else  //  Look in the parent if not found in the child!
    {
        found = Inherited::getOperation(cmd, bst);
    }

    return found;
}

CtiDeviceMCT470::DynamicPaoAddressing_t CtiDeviceMCT470::initDynPaoAddressing()
{
    DynamicPaoAddressing_t addressSet;

//  The SSPEC is set in decodeGetConfigModel().
//    This code assumes that the values are contiguous, which the SSPEC is not.
/*
    addressSet.insert(DynamicPaoAddressing(Memory_SSpecPos,                  Memory_SSpecLen,                 Keys::Key_MCT_SSpec));
    addressSet.insert(DynamicPaoAddressing(Memory_RevisionPos,               Memory_RevisionLen,              Keys::Key_MCT_SSpecRevision));
*/
    addressSet.insert(DynamicPaoAddressing(Memory_OptionsPos,                Memory_OptionsLen,               Keys::Key_MCT_Options));
    addressSet.insert(DynamicPaoAddressing(Memory_ConfigurationPos,          Memory_ConfigurationLen,         Keys::Key_MCT_Configuration));
    addressSet.insert(DynamicPaoAddressing(Memory_EventFlagsMask1Pos,        Memory_EventFlagsMask1Len,       Keys::Key_MCT_EventFlagsMask1));
    addressSet.insert(DynamicPaoAddressing(Memory_EventFlagsMask2Pos,        Memory_EventFlagsMask2Len,       Keys::Key_MCT_EventFlagsMask2));
    addressSet.insert(DynamicPaoAddressing(Memory_AddressBronzePos,          Memory_AddressBronzeLen,         Keys::Key_MCT_AddressBronze));
    addressSet.insert(DynamicPaoAddressing(Memory_AddressLeadPos,            Memory_AddressLeadLen,           Keys::Key_MCT_AddressLead));
    addressSet.insert(DynamicPaoAddressing(Memory_AddressCollectionPos,      Memory_AddressCollectionLen,     Keys::Key_MCT_AddressCollection));
    addressSet.insert(DynamicPaoAddressing(Memory_AddressSPIDPos,            Memory_AddressSPIDLen,           Keys::Key_MCT_AddressServiceProviderID));
    addressSet.insert(DynamicPaoAddressing(Memory_DemandIntervalPos,         Memory_DemandIntervalLen,        Keys::Key_MCT_DemandInterval));
    addressSet.insert(DynamicPaoAddressing(Memory_LoadProfileInterval1Pos,   Memory_LoadProfileInterval1Len,  Keys::Key_MCT_LoadProfileInterval));
    addressSet.insert(DynamicPaoAddressing(Memory_LoadProfileInterval2Pos,   Memory_LoadProfileInterval2Len,  Keys::Key_MCT_LoadProfileInterval2));
    addressSet.insert(DynamicPaoAddressing(Memory_TimeAdjustTolerancePos,    Memory_TimeAdjustToleranceLen,   Keys::Key_MCT_TimeAdjustTolerance));
    addressSet.insert(DynamicPaoAddressing(Memory_DSTBeginPos,               Memory_DSTBeginLen,              Keys::Key_MCT_DSTStartTime));
    addressSet.insert(DynamicPaoAddressing(Memory_DSTEndPos,                 Memory_DSTEndLen,                Keys::Key_MCT_DSTEndTime));
    addressSet.insert(DynamicPaoAddressing(Memory_TimeZoneOffsetPos,         Memory_TimeZoneOffsetLen,        Keys::Key_MCT_TimeZoneOffset));
    addressSet.insert(DynamicPaoAddressing(Memory_TOUDayTablePos,            Memory_TOUDayTableLen,           Keys::Key_MCT_DayTable));
    addressSet.insert(DynamicPaoAddressing(Memory_TOUDailySched1Pos,         Memory_TOUDailySched1Len,        Keys::Key_MCT_DaySchedule1));
    addressSet.insert(DynamicPaoAddressing(Memory_TOUDailySched2Pos,         Memory_TOUDailySched2Len,        Keys::Key_MCT_DaySchedule2));
    addressSet.insert(DynamicPaoAddressing(Memory_TOUDailySched3Pos,         Memory_TOUDailySched3Len,        Keys::Key_MCT_DaySchedule3));
    addressSet.insert(DynamicPaoAddressing(Memory_TOUDailySched4Pos,         Memory_TOUDailySched4Len,        Keys::Key_MCT_DaySchedule4));
    addressSet.insert(DynamicPaoAddressing(Memory_TOUDefaultRatePos,         Memory_TOUDefaultRateLen,        Keys::Key_MCT_DefaultTOURate));
    addressSet.insert(DynamicPaoAddressing(Memory_Holiday1Pos,               Memory_Holiday1Len,              Keys::Key_MCT_Holiday1));
    addressSet.insert(DynamicPaoAddressing(Memory_Holiday2Pos,               Memory_Holiday2Len,              Keys::Key_MCT_Holiday2));
    addressSet.insert(DynamicPaoAddressing(Memory_Holiday3Pos,               Memory_Holiday3Len,              Keys::Key_MCT_Holiday3));
    addressSet.insert(DynamicPaoAddressing(Memory_KRatio1Pos,                Memory_KRatio1Len,               Keys::Key_MCT_LoadProfileKRatio1));
    addressSet.insert(DynamicPaoAddressing(Memory_MeteringRatio1Pos,         Memory_MeteringRatio1Len,        Keys::Key_MCT_LoadProfileMeterRatio1));
    addressSet.insert(DynamicPaoAddressing(Memory_ChannelConfig1Pos,         Memory_ChannelConfig1Len,        Keys::Key_MCT_LoadProfileChannelConfig1));
    addressSet.insert(DynamicPaoAddressing(Memory_KRatio2Pos,                Memory_KRatio2Len,               Keys::Key_MCT_LoadProfileKRatio2));
    addressSet.insert(DynamicPaoAddressing(Memory_MeteringRatio2Pos,         Memory_MeteringRatio2Len,        Keys::Key_MCT_LoadProfileMeterRatio2));
    addressSet.insert(DynamicPaoAddressing(Memory_ChannelConfig2Pos,         Memory_ChannelConfig2Len,        Keys::Key_MCT_LoadProfileChannelConfig2));
    addressSet.insert(DynamicPaoAddressing(Memory_KRatio3Pos,                Memory_KRatio3Len,               Keys::Key_MCT_LoadProfileKRatio3));
    addressSet.insert(DynamicPaoAddressing(Memory_MeteringRatio3Pos,         Memory_MeteringRatio3Len,        Keys::Key_MCT_LoadProfileMeterRatio3));
    addressSet.insert(DynamicPaoAddressing(Memory_ChannelConfig3Pos,         Memory_ChannelConfig3Len,        Keys::Key_MCT_LoadProfileChannelConfig3));
    addressSet.insert(DynamicPaoAddressing(Memory_KRatio4Pos,                Memory_KRatio4Len,               Keys::Key_MCT_LoadProfileKRatio4));
    addressSet.insert(DynamicPaoAddressing(Memory_MeteringRatio4Pos,         Memory_MeteringRatio4Len,        Keys::Key_MCT_LoadProfileMeterRatio4));
    addressSet.insert(DynamicPaoAddressing(Memory_ChannelConfig4Pos,         Memory_ChannelConfig4Len,        Keys::Key_MCT_LoadProfileChannelConfig4));
    addressSet.insert(DynamicPaoAddressing(Memory_RelayATimerPos,            Memory_RelayATimerLen,           Keys::Key_MCT_RelayATimer));
    addressSet.insert(DynamicPaoAddressing(Memory_RelayBTimerPos,            Memory_RelayBTimerLen,           Keys::Key_MCT_RelayBTimer));
    addressSet.insert(DynamicPaoAddressing(Memory_TableReadIntervalPos,      Memory_TableReadIntervalLen,     Keys::Key_MCT_PrecannedTableReadInterval));
    addressSet.insert(DynamicPaoAddressing(Memory_PrecannedMeterNumPos,      Memory_PrecannedMeterNumLen,     Keys::Key_MCT_PrecannedMeterNumber));
    addressSet.insert(DynamicPaoAddressing(Memory_PrecannedTableTypePos,     Memory_PrecannedTableTypeLen,    Keys::Key_MCT_PrecannedTableType));

    return addressSet;
}

//Function returns first address after the given address and the data associated with that address
void CtiDeviceMCT470::getDynamicPaoAddressing(int address, int &foundAddress, int &foundLength, CtiTableDynamicPaoInfo::Keys &foundKey)
{
    foundAddress = 0;
    foundLength  = 0;
    foundKey     = Keys::Key_Invalid;//If nothing happens, this is what we want.

    DynamicPaoAddressing tempDynAddr(address, 0, Keys::Key_Invalid);

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

void CtiDeviceMCT470::getDynamicPaoFunctionAddressing(int function, int address, int &foundAddress, int &foundLength, CtiTableDynamicPaoInfo::Keys &foundKey)
{
    foundAddress = 0;
    foundLength = 0;
    foundKey = Keys::Key_Invalid;//If nothing happens, this is what we want.

    DynamicPaoAddressing tempDynAddr(address, 0, Keys::Key_Invalid);

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

//  this function checks to see if we have all of the parameters we need to correctly request and interpret
//    load profile date from the MCT
bool CtiDeviceMCT470::isLPDynamicInfoCurrent( void )
{
    bool retval = true;
    long sspec = 0,
         sspec_rev = 0;

    //  grab these two because we'll use them later...
    //    also, the return value is identical to hasDynamicInfo, so we'll just use it the same
    retval &= getDynamicInfo(Keys::Key_MCT_SSpec,         sspec);
    retval &= getDynamicInfo(Keys::Key_MCT_SSpecRevision, sspec_rev);

    retval &= hasDynamicInfo(Keys::Key_MCT_Configuration);

    //  note that we're verifying this against the interval that's in the database - more things will be used this way in the future
    retval &= (getDynamicInfo(Keys::Key_MCT_LoadProfileInterval) == getLoadProfile().getLoadProfileDemandRate());

    //  we don't use the second load profile rate yet
    //retval |= (getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval2) == getLoadProfile().getVoltageProfileDemandRate());

    if( retval && ((sspec == Sspec && sspec_rev >= SspecRev_Min && sspec_rev <= SspecRev_Max)
                   || sspec == MCT430A_Sspec
                   || sspec == MCT430S_Sspec) )
    {
        //  we only care about these if we're the correct rev...  otherwise, we ignore everything
        //    we would've done with it.  everything pre-rev E is development only, and needs to be treated with kid gloves

        //  we will need to verify this eventually, and if it doesn't match the 470 config, we'll reconfig the 470 (and complain)
        retval &= hasDynamicInfo(Keys::Key_MCT_LoadProfileConfig);
        retval &= hasDynamicInfo(Keys::Key_MCT_IEDLoadProfileInterval);
    }

    return retval;
}


void CtiDeviceMCT470::requestDynamicInfo(CtiTableDynamicPaoInfo::Keys key, OUTMESS *&OutMessage, list< OUTMESS* > &outList )
{
    bool valid = true;

    OUTMESS *newOutMessage = CTIDBG_new OUTMESS(*OutMessage);

    newOutMessage->DeviceID  = getID();
    newOutMessage->TargetID  = getID();
    newOutMessage->Port      = getPortID();
    newOutMessage->Remote    = getAddress();
    newOutMessage->Priority  = ScanPriority_LoadProfile;
    newOutMessage->TimeOut   = 2;
    newOutMessage->Retry     = 2;

    // Tell the porter side to complete the assembly of the message.
    newOutMessage->Request.BuildIt = TRUE;

    if( getMCTDebugLevel(DebugLevel_DynamicInfo) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" requesting key (" << key << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if( key == Keys::Key_MCT_SSpec || key == Keys::Key_MCT_Configuration )
    {
        strncpy(newOutMessage->Request.CommandStr, "getconfig model", COMMAND_STR_SIZE );
        newOutMessage->Sequence  = Emetcon::GetConfig_Model;     // Helps us figure it out later!
    }
    else
    {
        //  the ideal case - the correct, non-development sspec
        if( (getDynamicInfo(Keys::Key_MCT_SSpec)         == Sspec       &&
             getDynamicInfo(Keys::Key_MCT_SSpecRevision) >= SspecRev_Min &&
             getDynamicInfo(Keys::Key_MCT_SSpecRevision) <= SspecRev_Max)
            || getDynamicInfo(Keys::Key_MCT_SSpec) == MCT430A_Sspec
            || getDynamicInfo(Keys::Key_MCT_SSpec) == MCT430S_Sspec )
        {
            switch( key )
            {
                //  all of these three are retrieved by this read
                case Keys::Key_MCT_IEDLoadProfileInterval:
                case Keys::Key_MCT_LoadProfileInterval:
                case Keys::Key_MCT_LoadProfileConfig:
                {
                    strncpy(newOutMessage->Request.CommandStr, "getconfig channels", COMMAND_STR_SIZE );
                    newOutMessage->Sequence = Emetcon::GetConfig_ChannelSetup;     // Helps us figure it out later!

                    break;
                }
                default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - unhandled key (" << key << ") in CtiDeviceMCT470::requestDynamicInfo **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    delete newOutMessage;
                    newOutMessage = 0;
                }
            }
        }
        else
        {
            switch( key )
            {
                case Keys::Key_MCT_LoadProfileInterval:
                {
                    strncpy(newOutMessage->Request.CommandStr, "getconfig intervals", COMMAND_STR_SIZE );
                    newOutMessage->Sequence = Emetcon::GetConfig_Intervals;     // Helps us figure it out later!

                    break;
                }
                default:
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - unhandled key (" << key << ") in CtiDeviceMCT470::requestDynamicInfo **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                //  we don't care about these keys, since this sspec should only be used for pulse inputs...
                //    they only matter for IED reads
                case Keys::Key_MCT_LoadProfileConfig:
                case Keys::Key_MCT_IEDLoadProfileInterval:
                {
                    delete newOutMessage;
                    newOutMessage = 0;
                }
            }
        }
    }

    if( newOutMessage )
    {
        outList.push_back(newOutMessage);
        newOutMessage = NULL;
    }
}


ULONG CtiDeviceMCT470::calcNextLPScanTime( void )
{
    CtiTime        Now;
    unsigned long next_time    = YUKONEOT,
                  planned_time = YUKONEOT;

    if( !isLPDynamicInfoCurrent() )
    {
        //  i'm purposely using the DB rate instead of the fancy getLoadProfileInterval() call here, so it can be ignorant
        //    of the dynamic LP config info - this could probably be improved to look at the minimum of the available
        //    LP intervals (interval 1, interval 2, electronic)
        unsigned int overdue_rate = getLPRetryRate(getLoadProfile().getLoadProfileDemandRate());

        //  only try for the dynamic info at the retry rate, no faster
        next_time  = (Now.seconds() - LPBlockEvacuationTime) + overdue_rate;
        next_time -= (Now.seconds() - LPBlockEvacuationTime) % overdue_rate;

        //  only for consistency...  we don't /really/ have to add on the evacuation time for config reads, but it keeps
        //    outbound requests predictable, which is helpful
        next_time += LPBlockEvacuationTime;
    }
    else
    {
        int demand_rate, block_size;

        for( int i = 0; i < LPChannels; i++ )
        {
            demand_rate = getLoadProfileInterval(i);
            block_size  = demand_rate * 6;

            if( demand_rate <= 0 )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" has invalid LP rate (" << demand_rate << ") for channel (" << i << ") - setting nextLPtime out 30 minutes **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                next_time = Now.seconds() + (30 * 60);
            }
            else
            {
                CtiPointSPtr pPoint = getDevicePointOffsetTypeEqual((i+1) + PointOffset_LoadProfileOffset, DemandAccumulatorPointType);

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
                    (planned_time <= Now.seconds()) )
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
    }

    if( getMCTDebugLevel(DebugLevel_LoadProfile) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << getName() << "'s next Load Profile request at " << CtiTime(next_time) << endl;
    }

    return (_nextLPScanTime = next_time);
}


void CtiDeviceMCT470::sendIntervals( OUTMESS *&OutMessage, list< OUTMESS* > &outList )
{
    // Load all the other stuff that is needed
    OutMessage->DeviceID  = getID();
    OutMessage->TargetID  = getID();
    OutMessage->Port      = getPortID();
    OutMessage->Remote    = getAddress();
    // 082002 CGP // OutMessage->RouteID   = getRouteID();
    OutMessage->Priority  = ScanPriority_LoadProfile;
    OutMessage->TimeOut   = 2;
    OutMessage->Sequence  = Emetcon::GetStatus_LoadProfile;     // Helps us figure it out later!
    OutMessage->Retry     = 2;

    // Tell the porter side to complete the assembly of the message.
    OutMessage->Request.BuildIt = TRUE;
    strncpy(OutMessage->Request.CommandStr, "putconfig emetcon intervals", COMMAND_STR_SIZE );

    outList.push_back(OutMessage);
    OutMessage = NULL;
}


//  zero-based channel offset
long CtiDeviceMCT470::getLoadProfileInterval( unsigned channel )
{
    long retval = -1;
    string config;

    if( channel < LPChannels )
    {
        if( getDynamicInfo(Keys::Key_MCT_LoadProfileConfig, config) )
        {
            if( config.length() > channel * 3 )
            {
                if( config[channel*3] == '1' )
                {
                    //  leaves it untouched (i.e., -1) if it doesn't have it
                    getDynamicInfo(Keys::Key_MCT_IEDLoadProfileInterval, retval);
                }
                //  uncomment when we care about LP interval #2
                /*else if( config.at(channel * 3 + 2) == '1' )
                {
                    getDynamicInfo(Keys::Key_MCT_LoadProfileInterval2, retval);
                }*/
                else
                {
                    getDynamicInfo(Keys::Key_MCT_LoadProfileInterval, retval);
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" - config.length() < channel * 3 (" << config.length() << " < " << channel * 3 << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" - dynamic LP interval not stored for channel " << channel << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" - channel " << channel << " not in range **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if( retval < 0 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" - load profile interval requested, but value not retrieved...  sending DB value **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retval = getLoadProfile().getLoadProfileDemandRate();
    }
    else if( retval == 0 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" - channel " << channel << " LP interval returned zero, returning DB interval **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retval = getLoadProfile().getLoadProfileDemandRate();
    }

    return retval;
}


CtiDeviceMCT470::point_info CtiDeviceMCT470::getDemandData(unsigned char *buf, int len) const
{
    return getData(buf, len, ValueType_PulseDemand);
}

CtiDeviceMCT470::point_info CtiDeviceMCT470::getData( unsigned char *buf, int len, ValueType470 vt ) const
{
    PointQuality_t quality = NormalQuality;
    unsigned long error_code = 0xffffffff,  //  filled with 0xff because some data types are less than 32 bits
                  min_error  = 0xffffffff;
    unsigned char quality_flags = 0,
                  resolution    = 0;
    unsigned char error_pad, error_byte, value_byte;

    const error_set *errors = &_mct_error_info;

    string description;
    __int64 value = 0;
    point_info  retval;

    for( int i = 0; i < len; i++ )
    {
        //  input data is in MSB order
        value      <<= 8;
        error_code <<= 8;

        value_byte = buf[i];
        error_byte = buf[i];

        if( i == 0 )
        {
            error_pad = value_byte;

            //  the first byte for some value types needs to be treated specially
            if( vt == ValueType_PulseDemand ||
                vt == ValueType_LoadProfile_PulseDemand )
            {
                quality_flags = value_byte & 0xc0;

                value_byte   &= 0x3f;  //  trim off the quality bits
                error_code   |= 0xc0;  //  fill in the quality bits to get the true error code
            }
        }
        else if( error_pad != value_byte )
        {
            error_pad = 0;
        }

        value      |= value_byte;
        error_code |= error_byte;
    }

    retval.freeze_bit = value & 0x01;

    switch( vt )
    {
        case ValueType_PulseDemand:
        case ValueType_LoadProfile_PulseDemand:      min_error = 0xffffffa1; break;

        case ValueType_LoadProfile_IED_Alpha_A3:
        case ValueType_LoadProfile_IED_Alpha_PP:
        case ValueType_LoadProfile_IED_GE_kV:
        case ValueType_LoadProfile_IED_GE_kV2:
        case ValueType_LoadProfile_IED_GE_kV2c:
        case ValueType_LoadProfile_IED_LG_S4:
        case ValueType_LoadProfile_IED_Sentinel:
        {
            if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) > SspecRev_IED_LPExtendedRange )
            {
                min_error = 0xfffffff0; break;
            }
            else
            {
                min_error = 0xffff7ff0; break;
            }
        }
    }

    if( vt == ValueType_LoadProfile_PulseDemand ||
        vt == ValueType_PulseDemand )
    {
        //  this was already set in errors' initializer, but this is for clarity
        errors = &_mct_error_info;
    }
    else if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) > SspecRev_IED_LPExtendedRange )
    {
        switch( vt )
        {
            case ValueType_LoadProfile_IED_Alpha_A3:    errors = &_error_info_alphaa3;  break;
            case ValueType_LoadProfile_IED_Alpha_PP:    errors = &_error_info_alphapp;  break;
            case ValueType_LoadProfile_IED_GE_kV:
            case ValueType_LoadProfile_IED_GE_kV2:
            case ValueType_LoadProfile_IED_GE_kV2c:     errors = &_error_info_gekv;     break;
            case ValueType_LoadProfile_IED_LG_S4:       errors = &_error_info_lgs4;     break;
            case ValueType_LoadProfile_IED_Sentinel:    errors = &_error_info_sentinel; break;
        }
    }
    else
    {
        errors = &_error_info_old_lp;
    }

    if( error_code >= min_error )
    {
        value       = 0;

        error_set::const_iterator es_itr = errors->find(error_info(error_code));

        if( es_itr != errors->end() )
        {
            quality     = es_itr->quality;
            description = es_itr->description;
        }
        else
        {
            quality     = InvalidQuality;
            description = "Unknown/reserved error [" + CtiNumStr(error_code).hex() + "]";
        }
    }
    else if( vt == ValueType_IED )
    {
        value   = 0;
        quality = InvalidQuality;

        if( error_pad == 0xfc )  description = "IED time invalid";
        if( error_pad == 0xfd )  description = "Error reading IED";
        if( error_pad == 0xfe )  description = "Data not available";
        if( error_pad == 0xff )  description = "Meter busy or not configured";
    }
    else if( vt == ValueType_LoadProfile_PulseDemand ||
             vt == ValueType_PulseDemand )
    {
        //  only take the demand bits into account if everything else is cool
        switch( quality_flags )
        {
            case 0xc0:  quality = PartialIntervalQuality;
                        description = "Time was adjusted in this interval";
                        break;

            case 0x80:  quality = PartialIntervalQuality;
                        description = "Power was restored in this interval";
                        break;

            case 0x40:  quality = PowerfailQuality;
                        description = "Power failed in this interval";
                        break;
        }
    }

    retval.value       = value;
    retval.quality     = quality;
    retval.description = description;

    return retval;
}


CtiDeviceMCT470::point_info CtiDeviceMCT470::getLoadProfileData(unsigned channel, unsigned char *buf, unsigned len)
{
    point_info pi;
    string config;

    ValueType470 vt = ValueType_LoadProfile_PulseDemand;

    if( getDynamicInfo(Keys::Key_MCT_LoadProfileConfig, config) )
    {
        if( config.length() > channel * 3 )
        {
            if( config[channel*3] == '1' )
            {
                if( hasDynamicInfo(Keys::Key_MCT_Configuration) )
                {
                    switch( getDynamicInfo(Keys::Key_MCT_Configuration) >> 4 )
                    {
                        case IED_Type_Alpha_A3:     vt = ValueType_LoadProfile_IED_Alpha_A3;    break;
                        case IED_Type_Alpha_PP:     vt = ValueType_LoadProfile_IED_Alpha_PP;    break;
                        case IED_Type_GE_kV:        vt = ValueType_LoadProfile_IED_GE_kV;       break;
                        case IED_Type_GE_kV2:       vt = ValueType_LoadProfile_IED_GE_kV2;      break;
                        case IED_Type_GE_kV2c:      vt = ValueType_LoadProfile_IED_GE_kV2c;     break;
                        case IED_Type_LG_S4:        vt = ValueType_LoadProfile_IED_LG_S4;       break;
                        case IED_Type_Sentinel:     vt = ValueType_LoadProfile_IED_Sentinel;    break;

                        case IED_Type_DNP:
                        case IED_Type_None:
                        default:
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" is reporting an invalid IED type (" << (getDynamicInfo(Keys::Key_MCT_Configuration) >> 4) << "); aborting decode **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" does not have Key_MCT_Configuration; attempting decode **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
            else
            {
                vt = ValueType_LoadProfile_PulseDemand;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" - config.length() < channel * 3 (" << config.length() << " < " << channel * 3 << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" - dynamic LP config not stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pi = getData(buf, len, vt);

    //  adjust for the demand interval
    pi.value *= 3600 / getLoadProfileInterval(channel);

    return pi;
}


INT CtiDeviceMCT470::calcAndInsertLPRequests(OUTMESS *&OutMessage, list< OUTMESS* > &outList)
{
    int nRet = NoError;

    CtiTime         Now;
    unsigned int   demand_rate, block_size;
    int            channel, block;
    string         descriptor;
    OUTMESS       *tmpOutMess;

    if( !isLPDynamicInfoCurrent() )
    {
        if( _lastConfigRequest >= (Now.seconds()) )
        {
            if( getMCTDebugLevel(DebugLevel_LoadProfile) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - config request too soon for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
        else
        {
            _lastConfigRequest = Now.seconds();

            if( !hasDynamicInfo(Keys::Key_MCT_SSpec) )
            {
                requestDynamicInfo(Keys::Key_MCT_SSpec, OutMessage, outList);
            }
            else if( !hasDynamicInfo(Keys::Key_MCT_Configuration) )
            {
                requestDynamicInfo(Keys::Key_MCT_Configuration, OutMessage, outList);
            }
            //  check if we're the IED sspec
            else if( (getDynamicInfo(Keys::Key_MCT_SSpec)   == Sspec
                        && getDynamicInfo(Keys::Key_MCT_SSpecRevision) >= SspecRev_Min
                        && getDynamicInfo(Keys::Key_MCT_SSpecRevision) <= SspecRev_Max)
                     || getDynamicInfo(Keys::Key_MCT_SSpec) == MCT430A_Sspec
                     || getDynamicInfo(Keys::Key_MCT_SSpec) == MCT430S_Sspec )
            {
                if( getDynamicInfo(Keys::Key_MCT_LoadProfileInterval) != getLoadProfile().getLoadProfileDemandRate() )
                {
                    OUTMESS *om = CTIDBG_new OUTMESS(*OutMessage);

                    //  send the intervals....
                    sendIntervals(om, outList);
                    //  then verify them - the ordering here does matter
                    requestDynamicInfo(Keys::Key_MCT_LoadProfileInterval, OutMessage, outList);
                }

                if( !hasDynamicInfo(Keys::Key_MCT_IEDLoadProfileInterval) )
                {
                    //  as i understand it, we can only read this, not write it, so we'll never do a write-then-read confirmation
                    requestDynamicInfo(Keys::Key_MCT_IEDLoadProfileInterval, OutMessage, outList);
                }

                if( !hasDynamicInfo(Keys::Key_MCT_LoadProfileConfig) )
                {
                    //  this will need to be changed to check for a match like LoadProfileInterval above -
                    //    if it doesn't match, then re-send and re-read
                    //    (which will happen when the 470 config is added)
                    requestDynamicInfo(Keys::Key_MCT_LoadProfileConfig, OutMessage, outList);
                }
            }
            else
            {
                if( getDynamicInfo(Keys::Key_MCT_LoadProfileInterval) != getLoadProfile().getLoadProfileDemandRate() )
                {
                    if( hasDynamicInfo(Keys::Key_MCT_LoadProfileInterval) )
                    {
                        OUTMESS *om = CTIDBG_new CtiOutMessage(*OutMessage);

                        sendIntervals(om, outList);
                    }

                    requestDynamicInfo(Keys::Key_MCT_LoadProfileInterval, OutMessage, outList);
                }
            }

            if( outList.empty() )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - !isLPDynamicInfoCurrent(), but no dynamic info requested **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
    else if( useScanFlags() )
    {
        for( int i = 0; i < LPChannels; i++ )
        {
            demand_rate = getLoadProfileInterval(i);
            block_size  = demand_rate * 6;

            if( getLoadProfile().isChannelValid(i) )
            {
                if( _lp_info[i].current_schedule <= Now )
                {
                    tmpOutMess = CTIDBG_new OUTMESS(*OutMessage);

                    _lp_info[i].current_request  = _lp_info[i].archived_reading;

                    //  make sure we only ask for what the function reads can access
                    if( (Now.seconds() - _lp_info[i].current_request) >= (unsigned long)(LPRecentBlocks * block_size) )
                    {
                        //  go back as far as we can
                        _lp_info[i].current_request  = Now.seconds();
                        _lp_info[i].current_request -= LPRecentBlocks * block_size;
                    }

                    if( getMCTDebugLevel(DebugLevel_LoadProfile) )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - LP variable check for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Now.seconds() = " << Now.seconds() << endl;
                        dout << "_lp_info[" << i << "].archived_reading = " << _lp_info[i].archived_reading << endl;
                        dout << "MCT4XX_LPRecentBlocks * block_size = " << LPRecentBlocks * block_size << endl;
                        dout << "_lp_info[" << i << "].current_request = " << _lp_info[i].current_request << endl;
                    }

                    //  make sure we're aligned
                    _lp_info[i].current_request -= _lp_info[i].current_request % block_size;

                    //  which block to grab?
                    channel = i + 1;
                    block   = (Now.seconds() - _lp_info[i].current_request) / block_size;

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


bool CtiDeviceMCT470::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
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
INT CtiDeviceMCT470::ModelDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    int ioType, location;
    if( restoreMessageRead(InMessage, ioType, location) )
    {
        int foundAddress, foundLength = 0;

        CtiTableDynamicPaoInfo::Keys foundKey = CtiTableDynamicPaoInfo::Key_Invalid;

         //ioType and location were set by the function.
        if( ioType == Emetcon::IO_Read )
        {
            int searchLocation = location;

            do
            {
                getDynamicPaoAddressing(searchLocation, foundAddress, foundLength, foundKey);

                if( foundAddress >=0 && foundLength >0 && foundKey != CtiTableDynamicPaoInfo::Key_Invalid
                    && (foundAddress - location + foundLength) <= InMessage->Buffer.DSt.Length && foundLength <=8 )
                {
                    unsigned long value = 0;
                    for( int i=0; i<foundLength; i++)
                    {
                        value += ( ((unsigned int)InMessage->Buffer.DSt.Message[(foundAddress-location+foundLength-1)-i]) << (i*8) );
                    }
                    CtiDeviceBase::setDynamicInfo(foundKey, value);
                    searchLocation = foundAddress+1;
                }
                else
                {
                    foundKey = CtiTableDynamicPaoInfo::Key_Invalid;
                }

            } while( foundKey != CtiTableDynamicPaoInfo::Key_Invalid );
        }
        else if( ioType == Emetcon::IO_Function_Read )
        {
            int searchLocation = 0;

            do
            {
                getDynamicPaoFunctionAddressing(location, searchLocation, foundAddress, foundLength, foundKey);

                if( foundAddress >=0 && foundLength >0 && foundKey != CtiTableDynamicPaoInfo::Key_Invalid
                    && (searchLocation + foundLength) <= InMessage->Buffer.DSt.Length && foundLength <=8 )
                {
                    unsigned long value = 0;
                    for( int i=0; i<foundLength; i++)
                    {
                        value += ( ((unsigned int)InMessage->Buffer.DSt.Message[(foundAddress+foundLength-1)-i]) << (i*8) );
                    }
                    CtiDeviceBase::setDynamicInfo(foundKey, value);
                    searchLocation = foundAddress+1;
                }
                else
                {
                    foundKey = CtiTableDynamicPaoInfo::Key_Invalid;
                }

            } while( foundKey != CtiTableDynamicPaoInfo::Key_Invalid );
        }
    }

    switch(InMessage->Sequence)
    {
        case Emetcon::Scan_Accum:
        case Emetcon::GetValue_KWH:             status = decodeGetValueKWH(InMessage, TimeNow, vgList, retList, outList);           break;

        case Emetcon::Scan_Integrity:
        case Emetcon::GetValue_Demand:          status = decodeGetValueDemand(InMessage, TimeNow, vgList, retList, outList);        break;

        case Emetcon::GetValue_IED:
        case Emetcon::GetValue_IEDDemand:       status = decodeGetValueIED(InMessage, TimeNow, vgList, retList, outList);           break;

        case Emetcon::GetValue_PeakDemand:
        case Emetcon::GetValue_FrozenPeakDemand: status = decodeGetValueMinMaxDemand(InMessage, TimeNow, vgList, retList, outList); break;

        case Emetcon::GetConfig_IEDDNP:
        case Emetcon::GetConfig_IEDTime:
        case Emetcon::GetConfig_IEDScan:        status = decodeGetConfigIED(InMessage, TimeNow, vgList, retList, outList);          break;

        case Emetcon::GetValue_LoadProfile:     status = decodeGetValueLoadProfile(InMessage, TimeNow, vgList, retList, outList);   break;

        case Emetcon::Scan_LoadProfile:         status = decodeScanLoadProfile(InMessage, TimeNow, vgList, retList, outList);       break;

        case Emetcon::GetStatus_Internal:       status = decodeGetStatusInternal(InMessage, TimeNow, vgList, retList, outList);     break;

        case Emetcon::GetStatus_IEDDNP:         status = decodeGetStatusDNP(InMessage, TimeNow, vgList, retList, outList);          break;

        case Emetcon::GetStatus_LoadProfile:    status = decodeGetStatusLoadProfile(InMessage, TimeNow, vgList, retList, outList);  break;

        case Emetcon::GetConfig_Intervals:      status = decodeGetConfigIntervals(InMessage, TimeNow, vgList, retList, outList);    break;

        case Emetcon::GetConfig_ChannelSetup:   status = decodeGetConfigChannelSetup(InMessage, TimeNow, vgList, retList, outList); break;

        case Emetcon::GetConfig_Multiplier:     status = decodeGetConfigMultiplier(InMessage, TimeNow, vgList, retList, outList);   break;

        case Emetcon::GetConfig_Model:          status = decodeGetConfigModel(InMessage, TimeNow, vgList, retList, outList);        break;

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

INT CtiDeviceMCT470::ErrorDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int retVal = NoError;
    CtiCommandParser  parse(InMessage->Return.CommandStr);
    CtiReturnMsg     *retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                CtiString(InMessage->Return.CommandStr),
                                                CtiString(),
                                                InMessage->EventCode & 0x7fff,
                                                InMessage->Return.RouteID,
                                                InMessage->Return.MacroOffset,
                                                InMessage->Return.Attempt,
                                                InMessage->Return.TrxID,
                                                InMessage->Return.UserID);
    int ioType, location;
    restoreMessageRead(InMessage, ioType, location);//used to remove this message from the list (we dont care about the data).

    if( retMsg != NULL )
    {
        if( parse.getCommand() == ScanRequest )  //  we only plug values for failed scans
        {
            switch( parse.getiValue("scantype") )
            {
                case ScanRateGeneral:
                case ScanRateStatus:
                {
                    insertPointFail( InMessage, retMsg, ScanRateStatus, 8, StatusPointType );
                    insertPointFail( InMessage, retMsg, ScanRateStatus, 7, StatusPointType );
                    insertPointFail( InMessage, retMsg, ScanRateStatus, 6, StatusPointType );
                    insertPointFail( InMessage, retMsg, ScanRateStatus, 5, StatusPointType );

                    resetForScan(ScanRateGeneral);
                    break;
                }

                case ScanRateAccum:
                {
                    insertPointFail( InMessage, retMsg, ScanRateAccum, 1, PulseAccumulatorPointType );
                    insertPointFail( InMessage, retMsg, ScanRateAccum, 2, PulseAccumulatorPointType );
                    insertPointFail( InMessage, retMsg, ScanRateAccum, 3, PulseAccumulatorPointType );
                    insertPointFail( InMessage, retMsg, ScanRateAccum, 4, PulseAccumulatorPointType );

                    resetForScan(ScanRateAccum);
                    break;
                }

                case ScanRateIntegrity:
                {
                    insertPointFail( InMessage, retMsg, ScanRateIntegrity, PointOffset_Accumulator_Powerfail, PulseAccumulatorPointType);
                    insertPointFail( InMessage, retMsg, ScanRateIntegrity, 1, DemandAccumulatorPointType);
                    insertPointFail( InMessage, retMsg, ScanRateIntegrity, 2, DemandAccumulatorPointType);
                    insertPointFail( InMessage, retMsg, ScanRateIntegrity, 3, DemandAccumulatorPointType);
                    insertPointFail( InMessage, retMsg, ScanRateIntegrity, 4, DemandAccumulatorPointType);
                    insertPointFail( InMessage, retMsg, ScanRateGeneral, CtiDeviceMCT470::PointOffset_TotalKW,     AnalogPointType);
                    insertPointFail( InMessage, retMsg, ScanRateGeneral, CtiDeviceMCT470::PointOffset_TotalKM,     AnalogPointType);
                    insertPointFail( InMessage, retMsg, ScanRateGeneral, CtiDeviceMCT470::PointOffset_VoltsPhaseA, AnalogPointType);
                    insertPointFail( InMessage, retMsg, ScanRateGeneral, CtiDeviceMCT470::PointOffset_VoltsPhaseB, AnalogPointType);
                    insertPointFail( InMessage, retMsg, ScanRateGeneral, CtiDeviceMCT470::PointOffset_VoltsPhaseC, AnalogPointType);

                    resetForScan(ScanRateIntegrity);
                    break;
                }

                case ScanRateLoadProfile:
                {
                    int channel = parse.getiValue("loadprofile_channel", 0);

                    if( channel )
                    {
                        insertPointFail( InMessage, retMsg, ScanRateLoadProfile, channel + PointOffset_LoadProfileOffset, DemandAccumulatorPointType );
                    }
                    break;
                }

                default:
                {
                    retVal = Inherited::ErrorDecode(InMessage, TimeNow, vgList, retList, outList);

                    break;
                }
            }
        }
        else if( InMessage->Sequence == Emetcon::Scan_Integrity || InMessage->Sequence == Emetcon::GetValue_Demand )
        {
            insertPointFail( InMessage, retMsg, ScanRateIntegrity, PointOffset_Accumulator_Powerfail, PulseAccumulatorPointType);
            insertPointFail( InMessage, retMsg, ScanRateIntegrity, 1, DemandAccumulatorPointType);
            insertPointFail( InMessage, retMsg, ScanRateIntegrity, 2, DemandAccumulatorPointType);
            insertPointFail( InMessage, retMsg, ScanRateIntegrity, 3, DemandAccumulatorPointType);
            insertPointFail( InMessage, retMsg, ScanRateIntegrity, 4, DemandAccumulatorPointType);
        }
        else if( InMessage->Sequence == Emetcon::GetValue_IEDDemand )
        {
            insertPointFail( InMessage, retMsg, ScanRateGeneral, CtiDeviceMCT470::PointOffset_TotalKW,     AnalogPointType);
            insertPointFail( InMessage, retMsg, ScanRateGeneral, CtiDeviceMCT470::PointOffset_TotalKM,     AnalogPointType);
            insertPointFail( InMessage, retMsg, ScanRateGeneral, CtiDeviceMCT470::PointOffset_VoltsPhaseA, AnalogPointType);
            insertPointFail( InMessage, retMsg, ScanRateGeneral, CtiDeviceMCT470::PointOffset_VoltsPhaseB, AnalogPointType);
            insertPointFail( InMessage, retMsg, ScanRateGeneral, CtiDeviceMCT470::PointOffset_VoltsPhaseC, AnalogPointType);
        }
        else
        {
            retVal = Inherited::ErrorDecode(InMessage, TimeNow, vgList, retList, outList);
        }

        // send the whole mess to dispatch
        if( retMsg->PointData().size() > 0 )
        {
            retList.push_back(retMsg);
        }
        else
        {
            delete retMsg;
        }

        //  set it to null, it's been sent off
        retMsg = NULL;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - null retMsg() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return retVal;
}

INT CtiDeviceMCT470::executeGetValue( CtiRequestMsg        *pReq,
                                      CtiCommandParser     &parse,
                                      OUTMESS             *&OutMessage,
                                      list< CtiMessage* >  &vgList,
                                      list< CtiMessage* >  &retList,
                                      list< OUTMESS* >     &outList )
{
    INT nRet = NoMethod;

    bool found = false;
    int function;


    if( parse.getFlags() & CMD_FLAG_GV_IED )  //  This parse has the token "IED" in it!
    {
        if( parse.isKeyValid("outage") )
        {
            if( !hasDynamicInfo(Keys::Key_MCT_Configuration) )
            {
                //  we need to read the IED info byte out of the MCT
                function = Emetcon::GetConfig_Model;
                found = getOperation(function, OutMessage->Buffer.BSt);

                if( found )
                {
                    OutMessage->DeviceID  = getID();
                    OutMessage->TargetID  = getID();
                    OutMessage->Port      = getPortID();
                    OutMessage->Remote    = getAddress();
                    OutMessage->TimeOut   = 2;
                    OutMessage->Sequence  = function;         // Helps us figure it out later!
                    OutMessage->Retry     = 2;
                    OutMessage->Request.RouteID   = getRouteID();

                    strncpy(OutMessage->Request.CommandStr, "getconfig model", COMMAND_STR_SIZE );
                    outList.push_back(CTIDBG_new OUTMESS(*OutMessage));
                    incrementGroupMessageCount(pReq->UserMessageId(), (long)pReq->getConnectionHandle());
                }
            }

            function = Emetcon::GetConfig_IEDTime;
            found = getOperation(function, OutMessage->Buffer.BSt);
        }
        else
        {
            if( getDynamicInfo(Keys::Key_MCT_SSpecRevision) < SspecRev_IED_ZeroWriteMin )
            {
                //If we need to read out the time, do so.
                function = Emetcon::GetConfig_IEDTime;
                found = getOperation(function, OutMessage->Buffer.BSt);
                if( found )
                {
                    OutMessage->DeviceID  = getID();
                    OutMessage->TargetID  = getID();
                    OutMessage->Port      = getPortID();
                    OutMessage->Remote    = getAddress();
                    OutMessage->TimeOut   = 2;
                    OutMessage->Sequence  = function;         // Helps us figure it out later!
                    OutMessage->Retry     = 2;
                    OutMessage->Request.RouteID   = getRouteID();

                    strncpy(OutMessage->Request.CommandStr, "getconfig ied time", COMMAND_STR_SIZE );
                    outList.push_back(CTIDBG_new OUTMESS(*OutMessage));
                    incrementGroupMessageCount(pReq->UserMessageId(), (long)pReq->getConnectionHandle());
                }
            }

            if( parse.getFlags() & CMD_FLAG_GV_DEMAND )
            {
                //  this will be for the real-time table
                function = Emetcon::GetValue_IEDDemand;
                found = getOperation(function, OutMessage->Buffer.BSt);
            }
            else if( parse.isKeyValid("ied_dnp") )
            {
                int i = 0;
                if( (i = parse.getiValue("collectionnumber")) != INT_MIN )
                {
                    if( i == 1 )
                    {
                        function = Emetcon::GetValue_IED;
                        found = getOperation(function, OutMessage->Buffer.BSt);
                        OutMessage->Buffer.BSt.Function = FuncRead_IED_RealTime;
                    }
                    else if( i == 2 )
                    {
                        function = Emetcon::GetValue_IED;
                        found = getOperation(function, OutMessage->Buffer.BSt);
                        OutMessage->Buffer.BSt.Function = FuncRead_IED_RealTime2;
                    }
                    else
                    {
                        nRet = BADRANGE;
                        found = false;
                    }
                }
                else if( (i = parse.getiValue("analognumber")) != INT_MIN )
                {
                    function = Emetcon::GetValue_IED;   //This means we have to fill in the function
                    found = getOperation(function, OutMessage->Buffer.BSt);

                    OutMessage->Buffer.BSt.Function = FuncRead_IED_Precanned_Base + (i-1) + MCT470_DNP_Analog_Precanned_Offset;
                    if( i > (MCT470_DNP_Counter_Precanned_Offset - MCT470_DNP_Analog_Precanned_Offset) || i < 1 )
                    {
                        nRet = BADRANGE;
                        found = false;
                    }
                }
                else if( (i = parse.getiValue("accumulatornumber")) != INT_MIN )
                {
                    function = Emetcon::GetValue_IED;   //This means we have to fill in the function
                    found = getOperation(function, OutMessage->Buffer.BSt);

                    OutMessage->Buffer.BSt.Function = FuncRead_IED_Precanned_Base + (i-1) + MCT470_DNP_Counter_Precanned_Offset;
                    if( i > MCT470_DNP_Counter_Precanned_Reads || i < 1 ) //only 8 reads possible.
                    {
                        nRet = BADRANGE;
                        found = false;
                    }
                }
                else if( (i = parse.getiValue("statusnumber")) != INT_MIN )
                {
                    function = Emetcon::GetValue_IED;   //This means we have to fill in the function
                    found = getOperation(function, OutMessage->Buffer.BSt);

                    OutMessage->Buffer.BSt.Function = FuncRead_IED_Precanned_Base + MCT470_DNP_Status_Precanned_Offset;
                }
                else if( parse.isKeyValid("dnp_crc") )
                {
                    function = Emetcon::GetValue_IED;
                    OutMessage->Buffer.BSt.Function = FuncRead_IED_CRCPos;
                    OutMessage->Buffer.BSt.Length   = FuncRead_IED_CRCLen;
                    OutMessage->Buffer.BSt.IO = Emetcon::IO_Function_Read;
                    found = true;
                }
            }
            // else if()  //  this is where the IED status would be handled
            else
            {
                function = Emetcon::GetValue_IED;

                found = getOperation(function, OutMessage->Buffer.BSt);

                if( parse.getFlags() & CMD_FLAG_GV_RATET )
                {
                    OutMessage->Buffer.BSt.Function = FuncRead_IED_TOU_CurrentTotals;
                }
                else
                {
                    if( parse.getFlags() & CMD_FLAG_GV_KVARH || parse.getFlags() & CMD_FLAG_GV_KVAH  )
                    {
                        OutMessage->Buffer.BSt.Function = FuncRead_IED_TOU_CurrentKMBase;
                    }
                    else
                    {
                        OutMessage->Buffer.BSt.Function = FuncRead_IED_TOU_CurrentKWBase;
                    }

                    if(      parse.getFlags() & CMD_FLAG_GV_RATEA )  OutMessage->Buffer.BSt.Function += 0;
                    else if( parse.getFlags() & CMD_FLAG_GV_RATEB )  OutMessage->Buffer.BSt.Function += 1;
                    else if( parse.getFlags() & CMD_FLAG_GV_RATEC )  OutMessage->Buffer.BSt.Function += 2;
                    else if( parse.getFlags() & CMD_FLAG_GV_RATED )  OutMessage->Buffer.BSt.Function += 3;
                }

                if( parse.getFlags() & CMD_FLAG_FROZEN )
                {
                    OutMessage->Buffer.BSt.Function += FuncRead_IED_TOU_PreviousOffset;
                }
            }
        }
    }
    else if( (parse.getFlags() &  CMD_FLAG_GV_KWH) &&
             (parse.getFlags() & (CMD_FLAG_GV_RATEMASK ^ CMD_FLAG_GV_RATET)) )
    {
        //  if it's a KWH request for rate ABCD - rate T should fall through to a normal KWH request

        //  note that this is below the IED requests - we do IED KWH rate requests, so those need to be
        //    handled first...
        function = Emetcon::GetValue_TOU;
        found = getOperation(function, OutMessage->Buffer.BSt);

        if( parse.getFlags() & CMD_FLAG_FROZEN )    OutMessage->Buffer.BSt.Function += FuncRead_TOUFrozenOffset;

        //  no need to increment for rate A
        if( parse.getFlags() & CMD_FLAG_GV_RATEB )  OutMessage->Buffer.BSt.Function += 1;
        if( parse.getFlags() & CMD_FLAG_GV_RATEC )  OutMessage->Buffer.BSt.Function += 2;
        if( parse.getFlags() & CMD_FLAG_GV_RATED )  OutMessage->Buffer.BSt.Function += 3;

        //  if they request channel 2, go to the next TOU block
        if( parse.isKeyValid("channel") &&
            parse.getiValue ("channel") == 2 )
        {
            OutMessage->Buffer.BSt.Function += FuncRead_TOUChannelOffset;
        }
    }
    else if( parse.getFlags() & CMD_FLAG_GV_PEAK )
    {
        function = Emetcon::GetValue_PeakDemand;
        found = getOperation(function, OutMessage->Buffer.BSt);

        if( parse.getFlags() & CMD_FLAG_OFFSET )
        {
            switch( parse.getOffset() )
            {
                //  both 0 and 1 resolve to the first register
                case 0:     break;                                          //  0x93
                case 1:     break;
                //  please note the magic numbers
                case 2:     OutMessage->Buffer.BSt.Function += 2;   break;  //  0x95
                case 3:     OutMessage->Buffer.BSt.Function += 5;   break;  //  0x98
                case 4:     OutMessage->Buffer.BSt.Function += 7;   break;  //  0x9a

                //  anything outside the range from 0-4 is invalid
                default:    found = false;  break;
            }
        }

        if( parse.getFlags() & CMD_FLAG_FROZEN )
        {
            //  magic number
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
        ::strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
        incrementGroupMessageCount(pReq->UserMessageId(), (long)pReq->getConnectionHandle());

        nRet = NoError;
    }

    return nRet;
}

INT CtiDeviceMCT470::executeScan(CtiRequestMsg      *pReq,
                              CtiCommandParser      &parse,
                              OUTMESS              *&OutMessage,
                              list< CtiMessage* >   &vgList,
                              list< CtiMessage* >   &retList,
                              list< OUTMESS* >      &outList)
{
    bool found = false;
    INT  nRet  = NoError;
    string tester;

    INT            function;
    unsigned short stub;

    // The following switch fills in the BSTRUCT's Function, Length, and IO parameters.
    switch(parse.getiValue("scantype"))
    {
        case ScanRateIntegrity:
        {
            // Load all the other stuff that is needed
            OutMessage->DeviceID  = getID();
            OutMessage->TargetID  = getID();
            OutMessage->Port      = getPortID();
            OutMessage->Remote    = getAddress();
            OutMessage->TimeOut   = 2;
            OutMessage->Retry     = 2;
            OutMessage->Request.RouteID   = getRouteID();

            CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();
            //MCTSystemOptionsSPtr options;

            CtiString originalString = pReq->CommandString();
            boost::regex re_scan ("scan integrity");

           /* if( deviceConfig )
            {
                options = boost::static_pointer_cast< ConfigurationPart<MCTSystemOptions> >(deviceConfig->getConfigFromType(ConfigTypeMCTSystemOptions));
            }

            if( !options || (options && ( !stringCompareIgnoreCase(options->getValueFromKey(DemandMetersToScan), "all")
                          || !stringCompareIgnoreCase(options->getValueFromKey(DemandMetersToScan), "pulse"))) )
            {*/
                //Read the pulse demand
                function = Emetcon::GetValue_Demand;
                found = getOperation(function, OutMessage->Buffer.BSt);

                if( found )
                {
                    OutMessage->Sequence  = function;     // Helps us figure it out later!

                    CtiString createdString = originalString;
                    CtiString replaceString = "getvalue demand";

                    createdString.toLower();
                    createdString.replace(re_scan, replaceString);//This had better be here, or we have issues.
                    strncpy(OutMessage->Request.CommandStr, createdString.data(), COMMAND_STR_SIZE);

                    outList.push_back(CTIDBG_new OUTMESS(*OutMessage));
                    incrementGroupMessageCount(pReq->UserMessageId(), (long)pReq->getConnectionHandle());
                }
                else
                {
                    nRet = NoMethod;
                }
            //}

            /*if( !options || (options && ( !stringCompareIgnoreCase(options->getValueFromKey(DemandMetersToScan), "all")
                          || !stringCompareIgnoreCase(options->getValueFromKey(DemandMetersToScan), "ied"))) )
            {*/
                if( getDynamicInfo(Keys::Key_MCT_SSpecRevision) < SspecRev_IED_ZeroWriteMin )
                {
                    //If we need to read out the time, do so.
                    function = Emetcon::GetConfig_IEDTime;
                    found = getOperation(function, OutMessage->Buffer.BSt);
                    if( found )
                    {
                        OutMessage->Sequence  = function;     // Helps us figure it out later!
                        CtiString createdString = originalString;
                        CtiString replaceString = "getconfig ied time";

                        createdString.toLower();
                        createdString.replace(re_scan, replaceString);//This had better be here, or we have issues.
                        strncpy(OutMessage->Request.CommandStr, createdString.data(), COMMAND_STR_SIZE);

                        outList.push_back(CTIDBG_new OUTMESS(*OutMessage));
                        incrementGroupMessageCount(pReq->UserMessageId(), (long)pReq->getConnectionHandle());
                    }
                    else
                    {
                        nRet = NoMethod;
                    }
                }

                //Read the IED demand
                function = Emetcon::GetValue_IEDDemand;
                found = getOperation(function, OutMessage->Buffer.BSt);

                if( found )
                {
                    OutMessage->Sequence  = function;     // Helps us figure it out later!
                    CtiString createdString = originalString;
                    CtiString replaceString = "getvalue ied demand";

                    createdString.toLower();
                    createdString.replace(re_scan, replaceString);//This had better be here, or we have issues.
                    strncpy(OutMessage->Request.CommandStr, createdString.data(), COMMAND_STR_SIZE);

                    outList.push_back(CTIDBG_new OUTMESS(*OutMessage));
                    incrementGroupMessageCount(pReq->UserMessageId(), (long)pReq->getConnectionHandle());
                }
                else
                {
                    nRet = NoMethod;
                }
            /*}
            else
            {
                nRet = NoConfigData;
            }*/

            if( OutMessage )
            {
                delete OutMessage;
                OutMessage = NULL;
            }

            break;
        }
        default:
        {
            found = true; //This is an odd setting, but it flows right, and we did find the right execute scan to run
            nRet = Inherited::executeScan(pReq, parse, OutMessage, vgList, retList, outList);
            break;
        }
    }

    if(!found)
    {
        nRet = NoMethod;
    }

    return nRet;
}

INT CtiDeviceMCT470::executeGetConfig( CtiRequestMsg         *pReq,
                                       CtiCommandParser      &parse,
                                       OUTMESS              *&OutMessage,
                                       list< CtiMessage* >   &vgList,
                                       list< CtiMessage* >   &retList,
                                       list< OUTMESS* >      &outList )
{
    INT nRet = NoMethod;

    bool found = false;
    int function;


/*
    if( parse.isKeyValid("channels") )
    {

    }
*/
    if(parse.isKeyValid("multiplier"))
    {
        function = Emetcon::GetConfig_Multiplier;
        found = getOperation(function, OutMessage->Buffer.BSt);

        if( parse.isKeyValid("multchannel") )
        {
            int channel = parse.getiValue("multchannel");

            if( channel >= 1 && channel <= ChannelCount )
            {
                if( channel > 2 )
                {
                    //  this refers to a pair of functions that retrieve channels 1/2, 3/4 respectively
                    OutMessage->Buffer.BSt.Function = FuncRead_LoadProfileChannel34Pos;
                    OutMessage->Buffer.BSt.Length   = FuncRead_LoadProfileChannel34Len;
                }
            }
            else
            {
                found = false;
            }
        }
    }
    else if( parse.isKeyValid("ied") )
    {
        //  ACH:  add a dynamic info check to ensure that we're reading from Precanned Table 1
        if( parse.isKeyValid("time") )
        {
            if( !hasDynamicInfo(Keys::Key_MCT_Configuration) )
            {
                //  we need to read the IED info byte out of the MCT
                function = Emetcon::GetConfig_Model;
                found = getOperation(function, OutMessage->Buffer.BSt);

                if( found )
                {
                    OutMessage->DeviceID  = getID();
                    OutMessage->TargetID  = getID();
                    OutMessage->Port      = getPortID();
                    OutMessage->Remote    = getAddress();
                    OutMessage->TimeOut   = 2;
                    OutMessage->Sequence  = function;         // Helps us figure it out later!
                    OutMessage->Retry     = 2;
                    OutMessage->Request.RouteID   = getRouteID();

                    strncpy(OutMessage->Request.CommandStr, "getconfig model", COMMAND_STR_SIZE );
                    outList.push_back(CTIDBG_new OUTMESS(*OutMessage));
                    incrementGroupMessageCount(pReq->UserMessageId(), (long)pReq->getConnectionHandle());
                }
            }

            function = Emetcon::GetConfig_IEDTime;
            found = getOperation(function, OutMessage->Buffer.BSt);
            incrementGroupMessageCount(pReq->UserMessageId(), (long)pReq->getConnectionHandle());
        }
        else if( parse.isKeyValid("scan"))
        {
            function = Emetcon::GetConfig_IEDScan;
            found = getOperation(function, OutMessage->Buffer.BSt);
        }
        else if( parse.isKeyValid("dnp") )
        {

            if( parse.isKeyValid("start address") )
            {
                function = Emetcon::PutConfig_Raw;
                OutMessage->Buffer.BSt.Function = FuncWrite_DNPReqTable;
                OutMessage->Buffer.BSt.Length = 1;
                OutMessage->Buffer.BSt.IO = Emetcon::IO_Function_Write;
                OutMessage->Buffer.BSt.Message[0] = parse.getiValue("start address");
                OutMessage->DeviceID  = getID();
                OutMessage->TargetID  = getID();
                OutMessage->Port      = getPortID();
                OutMessage->Remote    = getAddress();
                OutMessage->TimeOut   = 2;
                OutMessage->Sequence  = function;         // Helps us figure it out later!
                OutMessage->Retry     = 2;
                OutMessage->Request.RouteID   = getRouteID();

                strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
                outList.push_back(CTIDBG_new OUTMESS(*OutMessage));
                incrementGroupMessageCount(pReq->UserMessageId(), (long)pReq->getConnectionHandle());
            }

            function = Emetcon::GetConfig_IEDDNP;
            found = getOperation(function, OutMessage->Buffer.BSt);
            incrementGroupMessageCount(pReq->UserMessageId(), (long)pReq->getConnectionHandle());
        }
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
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        ::strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        nRet = NoError;
    }

    return nRet;
}


bool CtiDeviceMCT470::computeMultiplierFactors(double multiplier, unsigned &numerator, unsigned &denominator) const
{
    bool retval = true;

    /*

    If Mp is greater than Kh, then the accumulator will accrue the difference, Mp - Kh, whenever a pulse is received.
    If Mp = Kh + 1, then the accumulator will be able to hold Kh - 1 as a maximum before the pulse is moved to the reading.
    However, on that next pulse, the meter will add Mp to Kh - 1, so the maximum in the accumulator can be stated as:

    Mp + Kh - 1

    The maximum range of the accumulator is:

    65535

    So the relationship between Mp and Kh can at most be:

    Mp + Kh - 1 = 65535
    Mp + Kh     = 65536

    So I choose to fix the upper bound for the denominator at 10,000 and allow the numerator to range from 1-50,000.


       Mp    Kh
         1/10000 =     0.0001
         9/10000 =     0.0009
        99/10000 =     0.0099
       999/10000 =     0.0999
      9999/10000 =     0.9999
     49999/10000 =     4.9999
     49999/ 1000 =    49.999
     49999/  100 =   499.99
     49999/   10 =  4999.9
     49999/    1 = 49999.

    */


    if( multiplier > 50000 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" - Multiplier too large - must be less than 50000 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retval = false;
    }
    else if( multiplier < 0.0001 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" - Multiplier too small - must be at least 0.0001 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retval = false;
    }
    else if( int(multiplier) == multiplier )
    {
        //  this is an integer, so don't make it too hard on us
        numerator   = multiplier;
        denominator = 1;
    }
    else
    {
        denominator = 1;

        //  this will break once we cross 5000 or if we have no significant digits left
        while( (multiplier * denominator) < 5000 &&
               fmod(multiplier * double(denominator), 1.0) != 0.0 )
        {
            denominator *= 10;
        }

        numerator = (unsigned)(multiplier * denominator);
    }

    return retval;
}


INT CtiDeviceMCT470::executePutConfig( CtiRequestMsg         *pReq,
                                       CtiCommandParser      &parse,
                                       OUTMESS              *&OutMessage,
                                       list< CtiMessage* >   &vgList,
                                       list< CtiMessage* >   &retList,
                                       list< OUTMESS* >      &outList )
{
    INT nRet = NoMethod;

    bool found = false;
    int function;

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                   CtiString(OutMessage->Request.CommandStr),
                                                   CtiString(),
                                                   nRet,
                                                   OutMessage->Request.RouteID,
                                                   OutMessage->Request.MacroOffset,
                                                   OutMessage->Request.Attempt,
                                                   OutMessage->Request.TrxID,
                                                   OutMessage->Request.UserID,
                                                   OutMessage->Request.SOE,
                                                   CtiMultiMsg_vec( ));

    if( parse.isKeyValid("channel_config") )
    {
        int channel, input, channel_config;
        unsigned numerator, denominator;
        double multiplier;
        string type;

        channel = parse.getiValue("channel_offset");

        //  will be one of "ied", "2-wire", "3-wire", or "none"
        type = parse.getsValue("channel_type");

        //  default the input to the same as the channel if not specified
        input = parse.getiValue("channel_input", channel);

        multiplier = parse.getdValue("channel_multiplier", 1.0);

        function = Emetcon::PutConfig_ChannelSetup;

        if( found = getOperation(function, OutMessage->Buffer.BSt) )
        {
            if( !computeMultiplierFactors(multiplier, numerator, denominator) )
            {
                errRet->setResultString("Invalid multiplier");
                nRet = BADPARAM;
                found = false;

                retList.push_back(errRet);
                errRet = 0;
            }
            else if( channel < 1 || channel > ChannelCount )
            {
                errRet->setResultString("Channel out of range (1-4)");
                nRet = BADPARAM;
                found = false;

                retList.push_back(errRet);
                errRet = 0;
            }
            else if( input < 1 || input > 16 )
            {
                errRet->setResultString("Input out of range (1-16)");
                nRet = BADPARAM;
                found = false;

                retList.push_back(errRet);
                errRet = 0;
            }
            else
            {
                if( type == "none" )    channel_config = 0;
                if( type == "ied" )     channel_config = 1;
                if( type == "2-wire" )  channel_config = 2;
                if( type == "3-wire" )  channel_config = 3;

                channel_config |= (input - 1) << 2;

                OutMessage->Buffer.BSt.Message[0] = gMCT400SeriesSPID;
                OutMessage->Buffer.BSt.Message[1] = channel;
                OutMessage->Buffer.BSt.Message[2] = channel_config;
                OutMessage->Buffer.BSt.Message[3] = (numerator   >> 8) & 0xff;
                OutMessage->Buffer.BSt.Message[4] =  numerator         & 0xff;
                OutMessage->Buffer.BSt.Message[5] = (denominator >> 8) & 0xff;
                OutMessage->Buffer.BSt.Message[6] =  denominator       & 0xff;
            }
        }
    }
    else if( parse.isKeyValid("multiplier") )
    {
        unsigned long multbytes;
        double multiplier = parse.getdValue("multiplier"), channel = parse.getiValue("multoffset");
        unsigned numerator, denominator;

        function = Emetcon::PutConfig_Multiplier;

        if( found = getOperation(function, OutMessage->Buffer.BSt) )
        {
            if( !computeMultiplierFactors(multiplier, numerator, denominator) )
            {
                errRet->setResultString("Invalid multiplier");
                nRet = BADPARAM;
                found = false;

                retList.push_back(errRet);
                errRet = 0;
            }
            else if( channel < 1 || channel > ChannelCount )
            {
                errRet->setResultString("Channel out of range (1-4)");
                nRet = BADPARAM;
                found = false;

                retList.push_back(errRet);

                errRet = 0;
            }
            else
            {
                OutMessage->Buffer.BSt.Message[0] = (numerator   >> 8) & 0xff;
                OutMessage->Buffer.BSt.Message[1] =  numerator         & 0xff;

                OutMessage->Buffer.BSt.Message[2] = (denominator >> 8) & 0xff;
                OutMessage->Buffer.BSt.Message[3] =  denominator       & 0xff;

                OutMessage->Buffer.BSt.Function += (channel - 1) * Memory_ChannelOffset;
            }
        }
    }
    else if( parse.isKeyValid("ied") )
    {
        IED_Types iedType = IED_Type_None;
        string tempVal = parse.getsValue("iedtype");

        if( tempVal.length() != 0 )
        {
            found = true;
            iedType = resolveIEDType(tempVal);

            char multipleMeters = parse.getiValue("hasmultiplemeters", 0);
            char dstEnabled = parse.getiValue("dstenabled", 1);

            char configuration = (iedType << 4) | (multipleMeters << 2) | dstEnabled;

            int event1Mask = parse.getiValue("eventmask1",  0xFF);
            int event2Mask = parse.getiValue("eventmask2",  0xFF);

            OutMessage->Buffer.BSt.Message[0] = (configuration);
            OutMessage->Buffer.BSt.Message[1] = (event1Mask);
            OutMessage->Buffer.BSt.Message[2] = (event2Mask);

            function = Emetcon::PutConfig_Options;
            getOperation(function, OutMessage->Buffer.BSt);
        }
    }
    else
    {
        nRet = Inherited::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }

    if( errRet )
    {
        delete errRet;
        errRet = 0;
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



INT CtiDeviceMCT470::executePutValue( CtiRequestMsg         *pReq,
                                      CtiCommandParser      &parse,
                                      OUTMESS              *&OutMessage,
                                      list< CtiMessage* >   &vgList,
                                      list< CtiMessage* >   &retList,
                                      list< OUTMESS* >      &outList )
{
    INT nRet = NoMethod;

    bool found = false;
    int function;

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                   CtiString(OutMessage->Request.CommandStr),
                                                   CtiString(),
                                                   nRet,
                                                   OutMessage->Request.RouteID,
                                                   OutMessage->Request.MacroOffset,
                                                   OutMessage->Request.Attempt,
                                                   OutMessage->Request.TrxID,
                                                   OutMessage->Request.UserID,
                                                   OutMessage->Request.SOE,
                                                   CtiMultiMsg_vec( ));

    if( parse.isKeyValid("kyz") )
    {
        function = Emetcon::PutValue_KYZ;
        if( found = getOperation(function, OutMessage->Buffer.BSt) )
        {
            if(parse.isKeyValid("kyz_offset") && parse.isKeyValid("kyz_reading") )
            {
                int    offset = parse.getiValue("kyz_offset");
                double dial   = parse.getdValue("kyz_reading", -1.0);

                if( offset > 0 && offset <= ChannelCount )
                {
                    if( dial >= 0.0 )
                    {
                        long pulses;

                        CtiPointSPtr tmpPoint = getDevicePointOffsetTypeEqual(offset, PulseAccumulatorPointType);

                        if( tmpPoint && tmpPoint->isA() == PulseAccumulatorPointType)
                        {
                            pulses = (long)(dial / (boost::static_pointer_cast<CtiPointAccumulator>(tmpPoint)->getMultiplier()));
                        }
                        else
                        {
                            pulses = (long)dial;
                        }

                        OutMessage->Buffer.BSt.Message[0] = gMCT400SeriesSPID;
                        OutMessage->Buffer.BSt.Message[1] = offset & 0xff;

                        OutMessage->Buffer.BSt.Message[2] = (pulses >> 16) & 0xff;
                        OutMessage->Buffer.BSt.Message[3] = (pulses >>  8) & 0xff;
                        OutMessage->Buffer.BSt.Message[4] = (pulses)       & 0xff;

                        OutMessage->Buffer.BSt.Length = 5;
                    }
                    else
                    {
                        found = false;

                        if( errRet )
                        {
                            errRet->setResultString("Invalid reading specified for command");
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
                        errRet->setResultString("Invalid offset specified (" + CtiNumStr(offset) + ")");
                        errRet->setStatus(NoMethod);
                        retList.push_back(errRet);

                        errRet = NULL;
                    }
                }
            }
        }
    }
    else
    {
        nRet = Inherited::executePutValue(pReq, parse, OutMessage, vgList, retList, outList);
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
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().data(), COMMAND_STR_SIZE);

        nRet = NoError;
    }

    if( errRet != NULL )
    {
        delete errRet;
        errRet = NULL;
    }

    return nRet;
}

int CtiDeviceMCT470::executePutConfigLoadProfileChannel(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        long channel1, channel2, spid;
        unsigned ratio1, kRatio1, ratio2, kRatio2;
        double multiplier1, multiplier2;

        channel1 = deviceConfig->getLongValueFromKey(MCTStrings::ChannelConfig1);
        channel2 = deviceConfig->getLongValueFromKey(MCTStrings::ChannelConfig2);
        multiplier1 = deviceConfig->getFloatValueFromKey(MCTStrings::ChannelMultiplier1);
        multiplier2 = deviceConfig->getFloatValueFromKey(MCTStrings::ChannelMultiplier2);

        spid = CtiDeviceBase::getDynamicInfo(Keys::Key_MCT_AddressServiceProviderID);

        if( spid == std::numeric_limits<long>::min() )
        {
            //We dont have it in dynamic pao info yet, we will get it from the config tables
            if(deviceConfig->getLongValueFromKey(MCTStrings::ServiceProviderID) != std::numeric_limits<long>::min() )
            {
                spid = deviceConfig->getLongValueFromKey(MCTStrings::ServiceProviderID);
            }
            else
            {
                spid = gMCT400SeriesSPID;
            }
        }

        if( channel1   == std::numeric_limits<long>::min() || channel2 == std::numeric_limits<long>::min()
            || multiplier1 == std::numeric_limits<double>::min() || multiplier2 == std::numeric_limits<double>::min()
            ||    spid == std::numeric_limits<long>::min() )
        {
            if( getMCTDebugLevel(DebugLevel_Configs) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Configs - no or bad value stored, LPChannel " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            nRet = NoConfigData;
        }
        else if( !computeMultiplierFactors(multiplier1, ratio1, kRatio1) || !computeMultiplierFactors(multiplier2, ratio2, kRatio2) )
        {
            if( getMCTDebugLevel(DebugLevel_Configs) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Configs - Cannot compute multiplier " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            nRet = NoConfigData;
        }
        else
        {
            if( parse.isKeyValid("force")
                || CtiDeviceBase::getDynamicInfo(Keys::Key_MCT_LoadProfileChannelConfig1) != channel1
                || CtiDeviceBase::getDynamicInfo(Keys::Key_MCT_LoadProfileChannelConfig2) != channel2
                || CtiDeviceBase::getDynamicInfo(Keys::Key_MCT_LoadProfileMeterRatio1)    != ratio1
                || CtiDeviceBase::getDynamicInfo(Keys::Key_MCT_LoadProfileMeterRatio2)    != ratio2
                || CtiDeviceBase::getDynamicInfo(Keys::Key_MCT_LoadProfileKRatio1)        != kRatio1
                || CtiDeviceBase::getDynamicInfo(Keys::Key_MCT_LoadProfileKRatio2)        != kRatio2 )
            {
                if( !parse.isKeyValid("verify") )
                {
                    OutMessage->Buffer.BSt.Function    = FuncWrite_SetupLPChannelsPos;
                    OutMessage->Buffer.BSt.Length      = FuncWrite_SetupLPChannelsLen;
                    OutMessage->Buffer.BSt.IO          = Emetcon::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0]  = spid;
                    OutMessage->Buffer.BSt.Message[1]  = 1;
                    OutMessage->Buffer.BSt.Message[2]  = (channel1);
                    OutMessage->Buffer.BSt.Message[3]  = (ratio1>>8);
                    OutMessage->Buffer.BSt.Message[4]  = (ratio1);
                    OutMessage->Buffer.BSt.Message[5]  = (kRatio1>>8);
                    OutMessage->Buffer.BSt.Message[6]  = (kRatio1);
                    OutMessage->Buffer.BSt.Message[7]  = 2;
                    OutMessage->Buffer.BSt.Message[8]  = (channel2);
                    OutMessage->Buffer.BSt.Message[9]  = (ratio2>>8);
                    OutMessage->Buffer.BSt.Message[10] = (ratio2);
                    OutMessage->Buffer.BSt.Message[11] = (kRatio2>>8);
                    OutMessage->Buffer.BSt.Message[12] = (kRatio2);

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Function   = FuncRead_LoadProfileChannel12Pos;
                    OutMessage->Buffer.BSt.Length     = FuncRead_LoadProfileChannel12Len;
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
            else if( nRet == NORMAL )
            {
                nRet = ConfigCurrent;
            }
        }


        channel1 = deviceConfig->getLongValueFromKey(MCTStrings::ChannelConfig3);
        channel2 = deviceConfig->getLongValueFromKey(MCTStrings::ChannelConfig4);
        multiplier1 = deviceConfig->getFloatValueFromKey(MCTStrings::ChannelMultiplier3);
        multiplier2 = deviceConfig->getFloatValueFromKey(MCTStrings::ChannelMultiplier4);

        if( channel1   == std::numeric_limits<long>::min() || channel2 == std::numeric_limits<long>::min()
            || multiplier1 == std::numeric_limits<double>::min() || multiplier2 == std::numeric_limits<double>::min()
            ||    spid == std::numeric_limits<long>::min() )
        {
            if( getMCTDebugLevel(DebugLevel_Configs) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Configs - no or bad value stored, LPChannel " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            nRet = NoConfigData;
        }
        else if( !computeMultiplierFactors(multiplier1, ratio1, kRatio1) || !computeMultiplierFactors(multiplier2, ratio2, kRatio2) )
        {
            if( getMCTDebugLevel(DebugLevel_Configs) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Configs - Cannot compute multiplier " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            nRet = NoConfigData;
        }
        else
        {
            if( parse.isKeyValid("force")
                || CtiDeviceBase::getDynamicInfo(Keys::Key_MCT_LoadProfileChannelConfig3) != channel1
                || CtiDeviceBase::getDynamicInfo(Keys::Key_MCT_LoadProfileChannelConfig4) != channel2
                || CtiDeviceBase::getDynamicInfo(Keys::Key_MCT_LoadProfileMeterRatio3)    != ratio1
                || CtiDeviceBase::getDynamicInfo(Keys::Key_MCT_LoadProfileMeterRatio4)    != ratio2
                || CtiDeviceBase::getDynamicInfo(Keys::Key_MCT_LoadProfileKRatio3)        != kRatio1
                || CtiDeviceBase::getDynamicInfo(Keys::Key_MCT_LoadProfileKRatio4)        != kRatio2 )
            {
                if( !parse.isKeyValid("verify") )
                {
                    OutMessage->Buffer.BSt.Function    = FuncWrite_SetupLPChannelsPos;
                    OutMessage->Buffer.BSt.Length      = FuncWrite_SetupLPChannelsLen;
                    OutMessage->Buffer.BSt.IO          = Emetcon::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0]  = spid;
                    OutMessage->Buffer.BSt.Message[1]  = 3;
                    OutMessage->Buffer.BSt.Message[2]  = (channel1);
                    OutMessage->Buffer.BSt.Message[3]  = (ratio1>>8);
                    OutMessage->Buffer.BSt.Message[4]  = (ratio1);
                    OutMessage->Buffer.BSt.Message[5]  = (kRatio1>>8);
                    OutMessage->Buffer.BSt.Message[6]  = (kRatio1);
                    OutMessage->Buffer.BSt.Message[7]  = 4;
                    OutMessage->Buffer.BSt.Message[8]  = (channel2);
                    OutMessage->Buffer.BSt.Message[9]  = (ratio2>>8);
                    OutMessage->Buffer.BSt.Message[10] = (ratio2);
                    OutMessage->Buffer.BSt.Message[11] = (kRatio2>>8);
                    OutMessage->Buffer.BSt.Message[12] = (kRatio2);

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Function   = FuncRead_LoadProfileChannel34Pos;
                    OutMessage->Buffer.BSt.Length     = FuncRead_LoadProfileChannel34Len;
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
            else if( nRet == NORMAL )
            {
                nRet = ConfigCurrent;
            }
        }
    }
    else
        nRet = NoConfigData;

    return NORMAL;
}

/*int CtiDeviceMCT470::executePutConfigRelays(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTRelays);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTRelays )
        {
            long relayATimer, relayBTimer, spid;

            MCTRelaysSPtr config = boost::static_pointer_cast< ConfigurationPart<MCTRelays> >(tempBasePtr);
            relayATimer = config->getLongValueFromKey(RelayATimer);
            relayBTimer = config->getLongValueFromKey(RelayBTimer);
            spid = CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID);

            if( spid == std::numeric_limits<long>::min() )
            {
                //We dont have it in dynamic pao info yet, we will get it from the config tables
                BaseSPtr addressTempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTAddressing);

                if(addressTempBasePtr && addressTempBasePtr->getType() == ConfigTypeMCTAddressing)
                {
                    MCTAddressingSPtr addressConfig = boost::static_pointer_cast< ConfigurationPart<MCTAddressing> >(addressTempBasePtr);
                    spid = addressConfig->getLongValueFromKey(ServiceProviderID);
                }
            }

            if( relayATimer == std::numeric_limits<long>::min() || relayBTimer == std::numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if( parse.isKeyValid("force")
                    || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_RelayATimer) != relayATimer
                    || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_RelayBTimer) != relayBTimer )
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        OutMessage->Buffer.BSt.Function   = FuncWrite_RelaysPos;
                        OutMessage->Buffer.BSt.Length     = FuncWrite_RelaysLen;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0] = spid;
                        OutMessage->Buffer.BSt.Message[1] = relayATimer;
                        OutMessage->Buffer.BSt.Message[2] = relayBTimer;

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Function   = Memory_RelayATimerPos;
                        OutMessage->Buffer.BSt.Length     = Memory_RelayATimerLen + Memory_RelayBTimerLen;
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

    return NORMAL;
}*/

int CtiDeviceMCT470::executePutConfigDemandLP(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        long demand, loadProfile1, loadProfile2;

        demand = deviceConfig->getLongValueFromKey(MCTStrings::DemandInterval);
        loadProfile1 = deviceConfig->getLongValueFromKey(MCTStrings::LoadProfileInterval);
        loadProfile2 = deviceConfig->getLongValueFromKey(MCTStrings::LoadProfileInterval2);

        if( demand == std::numeric_limits<long>::min()
            || loadProfile1 == std::numeric_limits<long>::min()
            || loadProfile2 == std::numeric_limits<long>::min() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            nRet = NoConfigData;
        }
        else
        {
            if( parse.isKeyValid("force")
                || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DemandInterval) != demand
                || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval) != loadProfile1
                || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval2) != loadProfile2)
            {
                if( !parse.isKeyValid("verify") )
                {
                    OutMessage->Buffer.BSt.Function   = FuncWrite_IntervalsPos;
                    OutMessage->Buffer.BSt.Length     = FuncWrite_IntervalsLen;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0] = (demand);
                    OutMessage->Buffer.BSt.Message[1] = (loadProfile1);
                    OutMessage->Buffer.BSt.Message[2] = (loadProfile2);

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

    return nRet;
}

/*
int CtiDeviceMCT470::executePutConfigPrecannedTable(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTPrecannedTable);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTPrecannedTable )
        {
            long tableReadInterval, meterNumber, tableType, spid;

            MCTPrecannedTableSPtr config = boost::static_pointer_cast< ConfigurationPart<MCTPrecannedTable> >(tempBasePtr);
            tableReadInterval = config->getLongValueFromKey(TableReadInterval);
            meterNumber = config->getLongValueFromKey(MeterNumber);
            tableType = config->getLongValueFromKey(TableType);
            spid = CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID);

            if( spid == std::numeric_limits<long>::min() )
            {
                //We dont have it in dynamic pao info yet, we will get it from the config tables
                BaseSPtr addressTempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTAddressing);

                if(addressTempBasePtr && addressTempBasePtr->getType() == ConfigTypeMCTAddressing)
                {
                    MCTAddressingSPtr addressConfig = boost::static_pointer_cast< ConfigurationPart<MCTAddressing> >(addressTempBasePtr);
                    spid = addressConfig->getLongValueFromKey(ServiceProviderID);
                }
            }

            if( tableReadInterval == std::numeric_limits<long>::min()
                || meterNumber == std::numeric_limits<long>::min()
                || tableType   == std::numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if( parse.isKeyValid("force")
                    || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_PrecannedTableReadInterval) != tableReadInterval
                    || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_PrecannedMeterNumber) != meterNumber
                    || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_PrecannedTableType) != tableType )
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        OutMessage->Buffer.BSt.Function   = FuncWrite_PrecannedTablePos;
                        OutMessage->Buffer.BSt.Length     = FuncWrite_PrecannedTableLen;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0] = spid;
                        OutMessage->Buffer.BSt.Message[1] = tableReadInterval;
                        OutMessage->Buffer.BSt.Message[2] = meterNumber;
                        OutMessage->Buffer.BSt.Message[3] = tableType;

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Function   = FuncRead_PrecannedTablePos;
                        OutMessage->Buffer.BSt.Length     = FuncRead_PrecannedTableLen;
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

    return NORMAL;
}

int CtiDeviceMCT470::executePutConfigDisconnect(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    //This can be removed as soon as the 470 does not inherit from the 410
    return NoMethod;
}

int CtiDeviceMCT470::executePutConfigOptions(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTOptions);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTOptions )
        {
            long event1mask, event2mask, configuration, outage, timeAdjustTolerance;
            USHORT function, length, io;

            MCTOptionsSPtr config = boost::static_pointer_cast< ConfigurationPart<MCTOptions> >(tempBasePtr);
            event1mask = config->getLongValueFromKey(AlarmMaskEvent1);
            event2mask = config->getLongValueFromKey(AlarmMaskEvent2);
            configuration = config->getLongValueFromKey(Configuration);
            timeAdjustTolerance = config->getLongValueFromKey(TimeAdjustTolerance);

            if( !getOperation(Emetcon::PutConfig_Options, OutMessage->Buffer.BSt) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Operation PutConfig_Options not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            if( event1mask == std::numeric_limits<long>::min()
                || configuration == std::numeric_limits<long>::min()
                || event2mask    == std::numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Options or Configuration not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if( parse.isKeyValid("force")
                    || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1) != event1mask
                    || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) != configuration
                    || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2) != event2mask )
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        OutMessage->Buffer.BSt.Message[0] = (configuration);
                        OutMessage->Buffer.BSt.Message[1] = (event1mask);
                        OutMessage->Buffer.BSt.Message[2] = (event2mask);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Function   = Memory_ConfigurationPos;
                        OutMessage->Buffer.BSt.Length     = Memory_ConfigurationLen;
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                        OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Function   = Memory_EventFlagsMask1Pos;
                        OutMessage->Buffer.BSt.Length     = Memory_EventFlagsMask1Len +
                                                            Memory_EventFlagsMask2Len;
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

            if( !getOperation(Emetcon::PutConfig_TimeAdjustTolerance, OutMessage->Buffer.BSt) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Time Adjust Tolerance not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            if( timeAdjustTolerance == std::numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Bad value for Time Adjust Tolerance **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                if( parse.isKeyValid("force")
                    || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TimeAdjustTolerance) != timeAdjustTolerance )
                {
                    if( !parse.isKeyValid("verify") )
                    {
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

int CtiDeviceMCT470::executePutConfigDNP(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList)
{
    //To anyone who needs to change this later, unfortunatelly a lot of the values here are used as "magic" numbers.
    //Unfortunatelly any changes will require changes to the code and really non-magic numbers are of very little use to us here.
    bool doRead = false;//Do the function crc read.
    bool verifyOnly = parse.isKeyValid("verify");
    int nRet = NORMAL;
    long value;
    const int BufferSize = 41;
    const int AnalogStartValue = MCT470_DNP_MCTPoint_PreCannedAnalog;
    const int AccumulatorStartValue = MCT470_DNP_MCTPoint_PreCannedAccumulator;
    bool force = parse.isKeyValid("force");
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTDNP);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTDNP )
        {
            BYTE buffer[BufferSize];
            CtiString collectionBinaryA, collectionBinaryB, collectionAnalog, collectionAccumulator;
            CtiString binaryA, binaryB, analogA, analogB, accumulatorA, accumulatorB;

            //***** Configure the Collection A (real time read 1) data
            MCT_DNP_SPtr config = boost::static_pointer_cast< ConfigurationPart<MCT_DNP> >(tempBasePtr);
            collectionBinaryA     = config->getValueFromKey(DNPCollection1BinaryA);
            collectionBinaryB     = config->getValueFromKey(DNPCollection1BinaryB);
            collectionAnalog      = config->getValueFromKey(DNPCollection1Analog);
            collectionAccumulator = config->getValueFromKey(DNPCollection1Accumulator);

            if( collectionBinaryA.empty() || collectionBinaryA.empty()
                || collectionAnalog.empty() || collectionAccumulator.empty() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Configuration not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                int tempCount, valCount = 0;
                getBytesFromString(collectionBinaryA, buffer, BufferSize, valCount, 15, 2);
                tempCount = valCount;
                if( tempCount == 0 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Binary Collection A improperly set up **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }
                getBytesFromString(collectionBinaryB, buffer+(valCount*2), BufferSize-(valCount*2), valCount, 15-valCount, 2);
                tempCount += valCount;
                if( tempCount != 15 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Binary Collections improperly set up **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }
                getBytesFromString(collectionAnalog, buffer+30, BufferSize-30, valCount, 3, 2);
                if( valCount != 3 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Analog Collection improperly set up **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }
                getBytesFromString(collectionAccumulator, buffer+36, BufferSize-36, valCount, 2, 2);
                if( valCount != 2 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Accumulator Collection improperly set up **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }

                if( parse.isKeyValid("force")
                    || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_RealTime1CRC) != crc8(buffer, 40) )
                {
                    if( !verifyOnly )
                    {
                        OutMessage->Buffer.BSt.Function    = FuncWrite_DNPReqTable;
                        OutMessage->Buffer.BSt.Length      = 14;
                        OutMessage->Buffer.BSt.IO          = Emetcon::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0]  = (MCT470_DNP_MCTPoint_RealTimeBinary);//This is defined in the 470 doc.
                        OutMessage->Buffer.BSt.Message[1]  = (0);//8 bit
                        OutMessage->Buffer.BSt.Message[2]  = (buffer[1]);
                        OutMessage->Buffer.BSt.Message[3]  = (buffer[3]);
                        OutMessage->Buffer.BSt.Message[4]  = (buffer[5]);
                        OutMessage->Buffer.BSt.Message[5]  = (buffer[7]);
                        OutMessage->Buffer.BSt.Message[6]  = (buffer[9]);
                        OutMessage->Buffer.BSt.Message[7]  = (buffer[11]);
                        OutMessage->Buffer.BSt.Message[8]  = (buffer[13]);
                        OutMessage->Buffer.BSt.Message[9]  = (buffer[15]);
                        OutMessage->Buffer.BSt.Message[10] = (buffer[17]);
                        OutMessage->Buffer.BSt.Message[11] = (buffer[19]);
                        OutMessage->Buffer.BSt.Message[12] = (buffer[21]);
                        OutMessage->Buffer.BSt.Message[13] = (buffer[23]);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Length      = 5;
                        OutMessage->Buffer.BSt.Message[0]  = (MCT470_DNP_MCTPoint_RealTimeBinary+12);//This is defined in the 470 doc.
                        OutMessage->Buffer.BSt.Message[1]  = (0);//8 bit
                        OutMessage->Buffer.BSt.Message[2]  = (buffer[25]);
                        OutMessage->Buffer.BSt.Message[3]  = (buffer[27]);
                        OutMessage->Buffer.BSt.Message[4]  = (buffer[29]);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Length      = 8;
                        OutMessage->Buffer.BSt.Message[0]  = (MCT470_DNP_MCTPoint_RealTimeAnalog);//This is defined in the 470 doc.
                        OutMessage->Buffer.BSt.Message[1]  = (2);//16 bit
                        OutMessage->Buffer.BSt.Message[2]  = (buffer[31]);
                        OutMessage->Buffer.BSt.Message[3]  = (buffer[30]);
                        OutMessage->Buffer.BSt.Message[4]  = (buffer[33]);
                        OutMessage->Buffer.BSt.Message[5]  = (buffer[32]);
                        OutMessage->Buffer.BSt.Message[6]  = (buffer[35]);
                        OutMessage->Buffer.BSt.Message[7]  = (buffer[34]);
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Length      = 6;
                        OutMessage->Buffer.BSt.Message[0]  = (MCT470_DNP_MCTPoint_RealTimeAccumulator);//This is defined in the 470 doc.
                        OutMessage->Buffer.BSt.Message[1]  = (2);//16 bit
                        OutMessage->Buffer.BSt.Message[2]  = (buffer[37]);
                        OutMessage->Buffer.BSt.Message[3]  = (buffer[36]);
                        OutMessage->Buffer.BSt.Message[4]  = (buffer[39]);
                        OutMessage->Buffer.BSt.Message[5]  = (buffer[38]);
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
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

            //***** Configure the collection B (real time read 2) data.
            collectionBinaryA     = config->getValueFromKey(DNPCollection2BinaryA);
            collectionBinaryB     = config->getValueFromKey(DNPCollection2BinaryB);
            collectionAnalog      = config->getValueFromKey(DNPCollection2Analog);
            collectionAccumulator = config->getValueFromKey(DNPCollection2Accumulator);

            if( collectionBinaryA.empty() || collectionBinaryB.empty()
                || collectionAnalog.empty() || collectionAccumulator.empty() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Configuration not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                int tempCount, valCount = 0;
                getBytesFromString(collectionBinaryA, buffer, BufferSize, valCount, 15, 2);
                tempCount = valCount;
                if( tempCount == 0 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Binary Collection A improperly set up **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }
                getBytesFromString(collectionBinaryB, buffer+(valCount*2), BufferSize-(valCount*2), valCount, 15-valCount, 2);
                tempCount += valCount;
                if( tempCount != 15 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Binary Collection's improperly set up **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }
                getBytesFromString(collectionAnalog, buffer+30, BufferSize-30, valCount, 3, 2);
                if( valCount != 3 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Analog Collection improperly set up **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }
                getBytesFromString(collectionAccumulator, buffer+36, BufferSize-36, valCount, 2, 2);
                if( valCount != 2 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Accumulator Collection improperly set up **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }

                if( parse.isKeyValid("force")
                    || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_RealTime2CRC) != crc8(buffer, 40) )
                {
                    if( !verifyOnly )
                    {
                        OutMessage->Buffer.BSt.Function    = FuncWrite_DNPReqTable;
                        OutMessage->Buffer.BSt.Length      = 14;
                        OutMessage->Buffer.BSt.IO          = Emetcon::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0]  = (MCT470_DNP_MCTPoint_RealTimeBinary+15);//This is defined in the 470 doc.
                        OutMessage->Buffer.BSt.Message[1]  = (0);//8 bit
                        OutMessage->Buffer.BSt.Message[2]  = (buffer[1]);
                        OutMessage->Buffer.BSt.Message[3]  = (buffer[3]);
                        OutMessage->Buffer.BSt.Message[4]  = (buffer[5]);
                        OutMessage->Buffer.BSt.Message[5]  = (buffer[7]);
                        OutMessage->Buffer.BSt.Message[6]  = (buffer[9]);
                        OutMessage->Buffer.BSt.Message[7]  = (buffer[11]);
                        OutMessage->Buffer.BSt.Message[8]  = (buffer[13]);
                        OutMessage->Buffer.BSt.Message[9]  = (buffer[15]);
                        OutMessage->Buffer.BSt.Message[10] = (buffer[17]);
                        OutMessage->Buffer.BSt.Message[11] = (buffer[19]);
                        OutMessage->Buffer.BSt.Message[12] = (buffer[21]);
                        OutMessage->Buffer.BSt.Message[13] = (buffer[23]);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Length      = 5;
                        OutMessage->Buffer.BSt.Message[0]  = (MCT470_DNP_MCTPoint_RealTimeBinary+27);//This is defined in the 470 doc.
                        OutMessage->Buffer.BSt.Message[1]  = (0);//8 bit
                        OutMessage->Buffer.BSt.Message[2]  = (buffer[25]);
                        OutMessage->Buffer.BSt.Message[3]  = (buffer[27]);
                        OutMessage->Buffer.BSt.Message[4]  = (buffer[29]);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Length      = 8;
                        OutMessage->Buffer.BSt.Message[0]  = (MCT470_DNP_MCTPoint_RealTimeAnalog+3);//This is defined in the 470 doc.
                        OutMessage->Buffer.BSt.Message[1]  = (2);//16 bit
                        OutMessage->Buffer.BSt.Message[2]  = (buffer[31]);
                        OutMessage->Buffer.BSt.Message[3]  = (buffer[30]);
                        OutMessage->Buffer.BSt.Message[4]  = (buffer[33]);
                        OutMessage->Buffer.BSt.Message[5]  = (buffer[32]);
                        OutMessage->Buffer.BSt.Message[6]  = (buffer[35]);
                        OutMessage->Buffer.BSt.Message[7]  = (buffer[34]);
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        OutMessage->Buffer.BSt.Length      = 6;
                        OutMessage->Buffer.BSt.Message[0]  = (MCT470_DNP_MCTPoint_RealTimeAccumulator+2);//This is defined in the 470 doc.
                        OutMessage->Buffer.BSt.Message[1]  = (2);//16 bit
                        OutMessage->Buffer.BSt.Message[2]  = (buffer[37]);
                        OutMessage->Buffer.BSt.Message[3]  = (buffer[36]);
                        OutMessage->Buffer.BSt.Message[4]  = (buffer[39]);
                        OutMessage->Buffer.BSt.Message[5]  = (buffer[38]);
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
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

            //***** Configure the binary precanned table data.
            binaryA = config->getValueFromKey(DNPBinaryByte1A);
            binaryB = config->getValueFromKey(DNPBinaryByte1B);

            if( binaryA.empty() || binaryB.empty() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Configuration not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                int tempCount, valCount = 0;
                getBytesFromString(binaryA, buffer, BufferSize, valCount, 12, 2);
                tempCount = valCount;
                if( tempCount == 0 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Binary CollectionA improperly set up **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }

                getBytesFromString(binaryB, buffer+valCount*2, BufferSize-valCount*2, valCount, 12-valCount, 2);
                tempCount += valCount;
                if( tempCount != 12 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Binary CollectionB improperly set up **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }

                if( parse.isKeyValid("force")
                    || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_BinaryCRC) != crc8(buffer, 24) )
                {
                    if( !verifyOnly )
                    {
                        OutMessage->Buffer.BSt.Function    = FuncWrite_DNPReqTable;
                        OutMessage->Buffer.BSt.Length      = 14;
                        OutMessage->Buffer.BSt.IO          = Emetcon::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0]  = (MCT470_DNP_MCTPoint_PreCannedBinary);//This is defined in the 470 doc.
                        OutMessage->Buffer.BSt.Message[1]  = (0);//8 bit
                        OutMessage->Buffer.BSt.Message[2]  = (buffer[1]);
                        OutMessage->Buffer.BSt.Message[3]  = (buffer[3]);
                        OutMessage->Buffer.BSt.Message[4]  = (buffer[5]);
                        OutMessage->Buffer.BSt.Message[5]  = (buffer[7]);
                        OutMessage->Buffer.BSt.Message[6]  = (buffer[9]);
                        OutMessage->Buffer.BSt.Message[7]  = (buffer[11]);
                        OutMessage->Buffer.BSt.Message[8]  = (buffer[13]);
                        OutMessage->Buffer.BSt.Message[9]  = (buffer[15]);
                        OutMessage->Buffer.BSt.Message[10] = (buffer[17]);
                        OutMessage->Buffer.BSt.Message[11] = (buffer[19]);
                        OutMessage->Buffer.BSt.Message[12] = (buffer[21]);
                        OutMessage->Buffer.BSt.Message[13] = (buffer[23]);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
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

            //***** Configure the analog first set (precanned analog 1 and 2)
            analogA = config->getValueFromKey(DNPAnalog1);
            analogB = config->getValueFromKey(DNPAnalog2);
            nRet |= sendDNPConfigMessages(AnalogStartValue, outList, OutMessage, analogA, analogB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC1, force, verifyOnly);
            analogA = config->getValueFromKey(DNPAnalog3);
            analogB = config->getValueFromKey(DNPAnalog4);
            nRet |= sendDNPConfigMessages(AnalogStartValue+12, outList, OutMessage, analogA, analogB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC2, force, verifyOnly);
            analogA = config->getValueFromKey(DNPAnalog5);
            analogB = config->getValueFromKey(DNPAnalog6);
            nRet |= sendDNPConfigMessages(AnalogStartValue+24, outList, OutMessage, analogA, analogB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC3, force, verifyOnly);
            analogA = config->getValueFromKey(DNPAnalog7);
            analogB = config->getValueFromKey(DNPAnalog8);
            nRet |= sendDNPConfigMessages(AnalogStartValue+36, outList, OutMessage, analogA, analogB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC4, force, verifyOnly);
            analogA = config->getValueFromKey(DNPAnalog9);
            analogB = config->getValueFromKey(DNPAnalog10);
            nRet |= sendDNPConfigMessages(AnalogStartValue+48, outList, OutMessage, analogA, analogB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC5, force, verifyOnly);

            accumulatorA = config->getValueFromKey(DNPAccumulator1);
            accumulatorB = config->getValueFromKey(DNPAccumulator2);
            nRet |= sendDNPConfigMessages(AccumulatorStartValue, outList, OutMessage, accumulatorA, accumulatorB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC1, force, verifyOnly);
            accumulatorA = config->getValueFromKey(DNPAccumulator3);
            accumulatorB = config->getValueFromKey(DNPAccumulator4);
            nRet |= sendDNPConfigMessages(AccumulatorStartValue+12, outList, OutMessage, accumulatorA, accumulatorB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC2, force, verifyOnly);
            accumulatorA = config->getValueFromKey(DNPAccumulator5);
            accumulatorB = config->getValueFromKey(DNPAccumulator6);
            nRet |= sendDNPConfigMessages(AccumulatorStartValue+24, outList, OutMessage, accumulatorA, accumulatorB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC3, force, verifyOnly);
            accumulatorA = config->getValueFromKey(DNPAccumulator7);
            accumulatorB = config->getValueFromKey(DNPAccumulator8);
            nRet |= sendDNPConfigMessages(AccumulatorStartValue+36, outList, OutMessage, accumulatorA, accumulatorB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC4, force, verifyOnly);

            if( !parse.isKeyValid("verify") )
            {
                //Read out the CRC
                strncpy(OutMessage->Request.CommandStr, "getvalue ied dnp crc", COMMAND_STR_SIZE );
                OutMessage->Priority--;
                OutMessage->Sequence = Emetcon::GetValue_IED;
                OutMessage->Buffer.BSt.Function = FuncRead_IED_CRCPos;
                OutMessage->Buffer.BSt.Length   = FuncRead_IED_CRCLen;
                OutMessage->Buffer.BSt.IO = Emetcon::IO_Function_Read;
                outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                OutMessage->Priority++;
            }
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;

    return nRet;
}
*/
int CtiDeviceMCT470::sendDNPConfigMessages(int startMCTID, list< OUTMESS * > &outList, OUTMESS *&OutMessage, string &dataA, string &dataB, CtiTableDynamicPaoInfo::Keys key, bool force, bool verifyOnly)
{
    int nRet = NORMAL;
    int valCount;
    const int bufferSize = 26;
    BYTE buffer[bufferSize];

    if( dataA.empty() || dataB.empty() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - Configuration not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        nRet = NoConfigData;
    }
    else if( bufferSize >= 24 )
    {
        int valCount = 0;
        getBytesFromString(dataA, buffer, bufferSize, valCount, 6, 2);//16 bit, 6 entries min
        if( valCount != 6 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - Collection improperly set up **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        //offset by 12 as we must have 6 entries in previous table, 6 in this one.
        getBytesFromString(dataB, buffer+12, bufferSize-12, valCount, 6, 2);
        if( valCount != 6 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - Collection improperly set up **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        if( force
            || CtiDeviceBase::getDynamicInfo(key) != crc8(buffer, 24) )
        {
            if( !verifyOnly )
            {
                //To make sending efficient, we will check if we can use 8 bit (we ignore 12 bit for now)
                bool canUse8Bit = true;
                for( int i = 0; i<24; i+=2 )
                {
                    if( buffer[i] != 0)
                    {
                        canUse8Bit = false;
                    }
                }

                if( canUse8Bit )
                {
                    OutMessage->Buffer.BSt.Function    = FuncWrite_DNPReqTable;
                    OutMessage->Buffer.BSt.Length      = 14;
                    OutMessage->Buffer.BSt.IO          = Emetcon::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0]  = (startMCTID);//This is defined in the 470 doc.
                    OutMessage->Buffer.BSt.Message[1]  = (0);//8 bit
                    OutMessage->Buffer.BSt.Message[2]  = (buffer[1]);
                    OutMessage->Buffer.BSt.Message[3]  = (buffer[3]);
                    OutMessage->Buffer.BSt.Message[4]  = (buffer[5]);
                    OutMessage->Buffer.BSt.Message[5]  = (buffer[7]);
                    OutMessage->Buffer.BSt.Message[6]  = (buffer[9]);
                    OutMessage->Buffer.BSt.Message[7]  = (buffer[11]);
                    OutMessage->Buffer.BSt.Message[8]  = (buffer[13]);
                    OutMessage->Buffer.BSt.Message[9]  = (buffer[15]);
                    OutMessage->Buffer.BSt.Message[10] = (buffer[17]);
                    OutMessage->Buffer.BSt.Message[11] = (buffer[19]);
                    OutMessage->Buffer.BSt.Message[12] = (buffer[21]);
                    OutMessage->Buffer.BSt.Message[13] = (buffer[23]);

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                }
                else
                {
                    OutMessage->Buffer.BSt.Function   = FuncWrite_DNPReqTable;
                    OutMessage->Buffer.BSt.Length     = 14;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0] = (startMCTID);//This is defined in the 470 doc.
                    OutMessage->Buffer.BSt.Message[1] = (2);//16 bit
                    OutMessage->Buffer.BSt.Message[2] = (buffer[0]);
                    OutMessage->Buffer.BSt.Message[3] = (buffer[1]);
                    OutMessage->Buffer.BSt.Message[4] = (buffer[2]);
                    OutMessage->Buffer.BSt.Message[5] = (buffer[3]);
                    OutMessage->Buffer.BSt.Message[6] = (buffer[4]);
                    OutMessage->Buffer.BSt.Message[7] = (buffer[5]);
                    OutMessage->Buffer.BSt.Message[8] = (buffer[6]);
                    OutMessage->Buffer.BSt.Message[9] = (buffer[7]);
                    OutMessage->Buffer.BSt.Message[10] = (buffer[8]);
                    OutMessage->Buffer.BSt.Message[11] = (buffer[9]);
                    OutMessage->Buffer.BSt.Message[12] = (buffer[10]);
                    OutMessage->Buffer.BSt.Message[13] = (buffer[11]);
                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Message[0] = (startMCTID + 6);//This is defined in the 470 doc.
                    OutMessage->Buffer.BSt.Message[1] = (2);//16 bit
                    OutMessage->Buffer.BSt.Message[2] = (buffer[12]);
                    OutMessage->Buffer.BSt.Message[3] = (buffer[13]);
                    OutMessage->Buffer.BSt.Message[4] = (buffer[14]);
                    OutMessage->Buffer.BSt.Message[5] = (buffer[15]);
                    OutMessage->Buffer.BSt.Message[6] = (buffer[16]);
                    OutMessage->Buffer.BSt.Message[7] = (buffer[17]);
                    OutMessage->Buffer.BSt.Message[8] = (buffer[18]);
                    OutMessage->Buffer.BSt.Message[9] = (buffer[19]);
                    OutMessage->Buffer.BSt.Message[10] = (buffer[20]);
                    OutMessage->Buffer.BSt.Message[11] = (buffer[21]);
                    OutMessage->Buffer.BSt.Message[12] = (buffer[22]);
                    OutMessage->Buffer.BSt.Message[13] = (buffer[23]);
                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                }
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
    return nRet;
}

INT CtiDeviceMCT470::decodeGetValueKWH(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;
    ULONG i,x;
    INT pid;
    string resultString;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    unsigned long pulses;
    point_info  pi;

    CtiPointSPtr          pPoint;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Accumulator Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    //  ACH:  are these necessary?  /mskf
    resetScanFlag(ScanFreezePending);
    resetScanFlag(ScanFreezeFailed);

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

        //  add freeze smarts here

        for( i = 0; i < ChannelCount; i++ )
        {
            pPoint = getDevicePointOffsetTypeEqual( 1 + i, PulseAccumulatorPointType );

            point_info pi = CtiDeviceMCT4xx::getData(DSt->Message + (i * 3), 3, ValueType_Accumulator);

            CtiTime pointTime;
            pointTime -= pointTime.seconds() % 300;

            string point_name;

            if( !i )  point_name = "KYZ 1";

            insertPointDataReport(PulseAccumulatorPointType, i + 1,
                                  ReturnMsg, pi, point_name, pointTime, 1.0, TAG_POINT_MUST_ARCHIVE);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetValueDemand(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int  status = NORMAL, i;
    bool demand_defined;
    point_info pi;

    INT ErrReturn = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

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

        for( i = 0; i < 8; i++ )
        {
            pi.value   = (DSt->Message[0] >> i) & 0x01;
            pi.quality = NormalQuality;

            insertPointDataReport(StatusPointType, i + 1,
                                  ReturnMsg, pi);
        }

        //  check to see if there are any demand points defined
        for( i = 0; !demand_defined && i < 4; i++ )
        {
            if( getDevicePointOffsetTypeEqual(i + 1, DemandAccumulatorPointType) )
            {
                demand_defined = true;
            }
        }

        CtiTime demand_time;
        demand_time -= demand_time.seconds() % getDemandInterval();

        for( i = 0; i < 4; i++ )
        {
            pi = getData(DSt->Message + (i * 2) + 1, 2, ValueType_PulseDemand);

            //  turn raw pulses into a demand reading
            pi.value *= double(3600 / getDemandInterval());

            //  if there's no other demand defined, we need to at least send point 1
            if( !i && !demand_defined )
            {
                insertPointDataReport(DemandAccumulatorPointType, i + 1,
                                      ReturnMsg, pi, "Channel 1 Demand", demand_time);
            }
            else
            {
                insertPointDataReport(DemandAccumulatorPointType, i + 1,
                                      ReturnMsg, pi, "", demand_time);
            }
        }

        pi = CtiDeviceMCT4xx::getData(DSt->Message + 9, 2, ValueType_Raw);

        insertPointDataReport(PulseAccumulatorPointType, PointOffset_Accumulator_Powerfail,
                              ReturnMsg, pi, "Blink Counter");

        decrementGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList, getGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection ));
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetValueMinMaxDemand(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int         status = NORMAL, base_offset;
    point_info  pi, pi_time;
    CtiTime     pointTime;

    INT ErrReturn = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Min/Max Demand Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

        base_offset = parse.getOffset();

        if( base_offset < 1 || base_offset > 4 )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - offset = " << base_offset << " - resetting to 1 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            base_offset = 1;
        }

        pi      = getData(DSt->Message + 0, 2, ValueType_PulseDemand);
        pi_time = CtiDeviceMCT4xx::getData(DSt->Message + 2, 4, ValueType_Raw);

        //  turn raw pulses into a demand reading
        pi.value *= double(3600 / getDemandInterval());

        pointTime = CtiTime(pi_time.value);

        //  we can do a rudimentary frozen peak time check here with the dynamicInfo stuff - we can't
        //    do much more, since we don't get the freeze count back with the frozen demand, so we have to
        //    take the device's word for it

        string pointname;

        pointname = "Channel " + CtiNumStr(base_offset) + " Max Demand";

        insertPointDataReport(DemandAccumulatorPointType, base_offset + PointOffset_MaxOffset,
                              ReturnMsg, pi, pointname, pointTime);

        pi      = getData(DSt->Message + 6, 2, ValueType_PulseDemand);
        pi_time = CtiDeviceMCT4xx::getData(DSt->Message + 8, 4, ValueType_Raw);

        //  turn raw pulses into a demand reading
        pi.value *= double(3600 / getDemandInterval());

        pointTime = CtiTime(pi_time.value);

        pointname = "Channel " + CtiNumStr(base_offset) + " Min Demand";

        //  if the min point doesn't exist...
        if( !getDevicePointOffsetTypeEqual(base_offset + PointOffset_MinOffset, DemandAccumulatorPointType) )
        {
            //  first look for the max point
            CtiPointNumericSPtr p = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(base_offset + PointOffset_MaxOffset, DemandAccumulatorPointType));

            //  if that doesn't exist, go after the normal demand point
            if( !p )
            {
                p = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(base_offset, DemandAccumulatorPointType));
            }

            //  if we found a stand-in point, compute the multiplier before we send it off
            if( p )
            {
                pi.value = p->computeValueForUOM(pi.value);
            }
        }

        insertPointDataReport(DemandAccumulatorPointType, base_offset + PointOffset_MinOffset,
                              ReturnMsg, pi, pointname, pointTime);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetValueIED(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int        status = NORMAL,
               frozen = 0,
               offset = 0,
               rate   = 0;
    point_info pi;
    string     point_string, resultString;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** IED Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


        bool dataInvalid = true;
        int  ied_data_end;

        if( parse.getFlags() & CMD_FLAG_GV_DEMAND )
        {
            //  Reads 0xd5-0xd9 have 12 bytes of IED data, then the status byte at the end
            //    Read 0xd4 has only 9 bytes of actual data, so when we do that read, it'll need to accomodate that
            //  Ideally, I'd like to do this check directly based on where the read is coming from, instead of
            //    what command we resolve it to
            ied_data_end = 12;
        }
        else
        {
            ied_data_end = 13;
        }

        if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) >= SspecRev_IED_ErrorPadding )
        {
            for( int i = 0; i < ied_data_end; i++)
            {
                if( (i && DSt->Message[i] != DSt->Message[i-1]) ||
                    DSt->Message[i] != 0xFF &&
                    DSt->Message[i] != 0xFE &&
                    DSt->Message[i] != 0xFD &&
                    DSt->Message[i] != 0xFC )
                {
                    //  we need to handle the case where the collected data is all 0xfe - means its quality is bad
                    dataInvalid = false;
                    break;
                }
            }
        }
        else
        {
            for( int i = 0; i < ied_data_end; i++)
            {
                if( DSt->Message[i] != 0 )
                {
                    dataInvalid = false;
                    break;
                }
            }
        }

        //  If this rev is before the SSPEC fix and the timestamp is at least 10 minutes old
        if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) < SspecRev_IED_ZeroWriteMin
            && (CtiTime::now().seconds() > (_iedTime.seconds() + MaxIEDReadAge)) )
        {
            dataInvalid = true;
        }

        //  should we archive non-frozen points?

        if( parse.getFlags() & CMD_FLAG_GV_DEMAND )
        {
            if( dataInvalid )
            {
                //  If we are here, we believe the data is incorrect!
                resultString += "Device: " + getName() + "\nData buffer is bad, retry command" ;
                status = ALPHABUFFERERROR;

                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_TotalKW,     AnalogPointType);
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_TotalKM,     AnalogPointType);
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_VoltsPhaseA, AnalogPointType);
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_VoltsPhaseB, AnalogPointType);
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_VoltsPhaseC, AnalogPointType);
            }
            else
            {
                CtiPointSPtr kw, km, volts;
                bool send_outages = true;

                //  get demand
                pi = getData(DSt->Message, 3, ValueType_IED);

                insertPointDataReport(AnalogPointType, PointOffset_TotalKW,
                                      ReturnMsg, pi, "current kW");

                if( pi.quality == InvalidQuality )
                {
                    insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_TotalKW, AnalogPointType);
                }

                //  get selectable metric (kM, kVAR, etc)
                pi = getData(DSt->Message + 3, 3, ValueType_IED);

                insertPointDataReport(AnalogPointType, PointOffset_TotalKM,
                                      ReturnMsg, pi, "current kM");

                if( pi.quality == InvalidQuality )
                {
                    insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_TotalKM, AnalogPointType);
                }

                //  S4-specific - get voltage
                pi = getData(DSt->Message + 6, 2, ValueType_IED);

                if( getDevicePointOffsetTypeEqual(PointOffset_VoltsPhaseA, AnalogPointType) )
                {
                    send_outages = false;

                    pi.value /= 100.0;
                    insertPointDataReport(AnalogPointType, PointOffset_VoltsPhaseA,
                                          ReturnMsg, pi);

                    if( pi.quality == InvalidQuality )
                    {
                        insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_VoltsPhaseA, AnalogPointType);
                    }
                }
                //  don't send the point if it's not defined - this is a hack to allow the S4 and Alpha decodes to
                //    both happen (until we have the configs to tell us which one to use)
                /*else
                {
                    resultString += getName() + " / Phase A Volts = " + CtiNumStr(pi.value) + "\n";
                }*/

                pi = getData(DSt->Message + 8, 2, ValueType_IED);

                if( volts = getDevicePointOffsetTypeEqual(PointOffset_VoltsPhaseB, AnalogPointType) )
                {
                    send_outages = false;

                    pi.value /= 100.0;
                    insertPointDataReport(AnalogPointType, PointOffset_VoltsPhaseB,
                                          ReturnMsg, pi);

                    if( pi.quality == InvalidQuality )
                    {
                        insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_VoltsPhaseB, AnalogPointType);
                    }
                }
                //  don't send the point if it's not defined - this is a hack to allow the S4 and Alpha decodes to
                //    both happen (until we have the configs to tell us which one to use)
                /*else
                {
                    resultString += getName() + " / Phase B Volts = " + CtiNumStr(pi.value) + "\n";
                }*/

                pi = getData(DSt->Message + 10, 2, ValueType_IED);

                if(volts = getDevicePointOffsetTypeEqual(PointOffset_VoltsPhaseC, AnalogPointType))
                {
                    send_outages = false;

                    pi.value /= 100.0;
                    insertPointDataReport(AnalogPointType, PointOffset_VoltsPhaseC,
                                          ReturnMsg, pi);

                    if( pi.quality == InvalidQuality )
                    {
                        insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_VoltsPhaseC, AnalogPointType);
                    }
                }
                //  don't send the point if it's not defined - this is a hack to allow the S4 and Alpha decodes to
                //    both happen (until we have the configs to tell us which one to use)
                /*else
                {
                    resultString += getName() + " / current KM = " + CtiNumStr(pi.value) + "\n";
                }*/

                if( send_outages )
                {
                    pi = getData(DSt->Message + 6, 2, ValueType_IED);

                    insertPointDataReport(AnalogPointType, PointOffset_OutageCount,
                                          ReturnMsg, pi, "Outage Count");

                    if( pi.quality == InvalidQuality )
                    {
                        insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_OutageCount, AnalogPointType);
                    }
                }
            }
        }
        else if( parse.getFlags() & CMD_FLAG_GV_RATET )
        {
            if( dataInvalid )
            {
                //If we are here, we believe the data is incorrect!
                resultString += "Device: " + getName() + "\nData buffer is bad, retry command" ;
                status = ALPHABUFFERERROR;

                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_TotalKWH, AnalogPointType);
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_TotalKMH, AnalogPointType);
            }
            else
            {
                CtiPointSPtr kwh, kmh;

                pi = getData(DSt->Message, 5, ValueType_IED);

                insertPointDataReport(AnalogPointType, PointOffset_TotalKWH,
                                      ReturnMsg, pi, "kWh total", 0UL, 1.0, TAG_POINT_MUST_ARCHIVE);

                if( pi.quality == InvalidQuality )
                {
                    insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_TotalKWH, AnalogPointType);
                }

                pi = getData(DSt->Message + 5, 5, ValueType_IED);

                insertPointDataReport(AnalogPointType, PointOffset_TotalKMH,
                                      ReturnMsg, pi, "kMh total");

                if( pi.quality == InvalidQuality )
                {
                    insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_TotalKMH, AnalogPointType);
                }

                pi = getData(DSt->Message + 10, 2, ValueType_IED);

                insertPointDataReport(AnalogPointType, PointOffset_AveragePowerFactor,
                                      ReturnMsg, pi, "Average power factor since last freeze");

                if( pi.quality == InvalidQuality )
                {
                    insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_TotalKMH, AnalogPointType);
                }
            }
        }
        else if( parse.isKeyValid("ied_dnp") )
        {
            int i = 0;
            CtiPointSPtr tempPoint;
            int dnp_status = DSt->Message[12];  //  Applies only to precanned table reads.

            if( (i = parse.getiValue("collectionnumber")) != INT_MIN )
            {
                decodeDNPRealTimeRead(DSt->Message, i, resultString, ReturnMsg, InMessage);
            }
            else if( (i = parse.getiValue("analognumber")) != INT_MIN )
            {
                if( dnp_status == 0 )
                {
                    for( int byte = 0; byte < 6; byte++ )
                    {
                        int pointoffset = PointOffset_DNPAnalog_Precanned1+byte+(i-1)*6;

                        string pointname;
                        pointname  = "Analog point ";
                        pointname += CtiNumStr(pointoffset);

                        pi = CtiDeviceMCT4xx::getData(DSt->Message + byte*2, 2, ValueType_Raw);

                        insertPointDataReport(AnalogPointType, pointoffset,
                                              ReturnMsg, pi, pointname);
                    }
                }
                else
                {
                    resultString += "DNP status returned " + CtiNumStr(dnp_status) + ": " + resolveDNPStatus(status);

                    for( int byte = 0; byte < 6; byte++ )
                    {
                        insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_DNPAnalog_Precanned1+byte+(i-1)*6, AnalogPointType);
                    }
                }
            }
            else if( (i = parse.getiValue("accumulatornumber")) != INT_MIN )
            {
                if( dnp_status == 0 )
                {
                    for( int byte = 0; byte < 6; byte++ )
                    {
                        int pointoffset = PointOffset_DNPAnalog_Precanned1+byte+(i-1)*6;

                        string pointname;
                        pointname  = "Pulse Accumulator point ";
                        pointname += CtiNumStr(pointoffset);

                        pi = CtiDeviceMCT4xx::getData(DSt->Message + byte*2, 2, ValueType_Raw);

                        insertPointDataReport(PulseAccumulatorPointType, pointoffset,
                                              ReturnMsg, pi, pointname);
                    }
                }
                else
                {
                    resultString += "DNP status returned " + CtiNumStr(dnp_status) + ": " + resolveDNPStatus(dnp_status);

                    for( int byte = 0; byte < 6; byte++ )
                    {
                        insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_DNPCounter_Precanned1+byte+(i-1)*6, AnalogPointType);
                    }
                }
            }
            else if( (i = parse.getiValue("statusnumber")) != INT_MIN )
            {
                if( dnp_status == 0 )
                {
                    pi.quality = NormalQuality;

                    for( int byte = 0; byte < 12; byte++ )
                    {
                        for( int bit = 0; bit < 8; bit++ )
                        {
                            pi.value = (DSt->Message[byte] >> bit) & 0x01;

                            insertPointDataReport(StatusPointType, PointOffset_DNPStatus_PrecannedStart+byte*8+bit,
                                                  ReturnMsg, pi);
                        }

                        resultString += getName() + " / Binary Data Byte " + CtiNumStr(byte) + " = " + CtiNumStr(DSt->Message[byte]) + "\n";
                    }
                }
                else
                {
                    resultString += "DNP status returned " + CtiNumStr(dnp_status) + ": " + resolveDNPStatus(dnp_status);

                    for( int byte = 0; byte < 12; byte++ )
                    {
                        for( int bit = 0; bit < 8; bit++ )
                        {
                            insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_DNPStatus_PrecannedStart+(byte*8)+bit, StatusPointType);
                        }
                    }
                }
            }
            else if( parse.isKeyValid("dnp_crc") )
            {
                using CtiTableDynamicPaoInfo::Keys;

                setDynamicInfo(Keys::Key_MCT_DNP_RealTime1CRC, InMessage->Buffer.DSt.Message[0]);
                resultString += "CRCs Returned: " + CtiNumStr(InMessage->Buffer.DSt.Message[0]);

                setDynamicInfo(Keys::Key_MCT_DNP_RealTime2CRC, InMessage->Buffer.DSt.Message[1]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[1]);

                setDynamicInfo(Keys::Key_MCT_DNP_BinaryCRC, InMessage->Buffer.DSt.Message[2]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[2]);

                setDynamicInfo(Keys::Key_MCT_DNP_AnalogCRC1, InMessage->Buffer.DSt.Message[3]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[3]);

                setDynamicInfo(Keys::Key_MCT_DNP_AnalogCRC2, InMessage->Buffer.DSt.Message[4]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[4]);

                setDynamicInfo(Keys::Key_MCT_DNP_AnalogCRC3, InMessage->Buffer.DSt.Message[5]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[5]);

                setDynamicInfo(Keys::Key_MCT_DNP_AnalogCRC4, InMessage->Buffer.DSt.Message[6]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[6]);

                setDynamicInfo(Keys::Key_MCT_DNP_AnalogCRC5, InMessage->Buffer.DSt.Message[7]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[7]);

                setDynamicInfo(Keys::Key_MCT_DNP_AccumulatorCRC1, InMessage->Buffer.DSt.Message[8]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[8]);

                setDynamicInfo(Keys::Key_MCT_DNP_AccumulatorCRC2, InMessage->Buffer.DSt.Message[9]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[9]);

                setDynamicInfo(Keys::Key_MCT_DNP_AccumulatorCRC3, InMessage->Buffer.DSt.Message[10]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[10]);

                setDynamicInfo(Keys::Key_MCT_DNP_AccumulatorCRC4, InMessage->Buffer.DSt.Message[11]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[11]);
            }
        }
        else
        {
            if( dataInvalid )
            {
                //If we are here, we believe the data is incorrect!
                resultString += "Device: " + getName() + "\nData buffer is bad, retry command" ;
                status = ALPHABUFFERERROR;

                int rate;

                if( parse.getFlags() & CMD_FLAG_GV_KVARH || parse.getFlags() & CMD_FLAG_GV_KVAH  )
                {
                    offset = PointOffset_TOU_KMBase;
                }
                else
                {
                    offset = PointOffset_TOU_KWBase;
                }

                if(      parse.getFlags() & CMD_FLAG_GV_RATEA )  rate = 0;
                else if( parse.getFlags() & CMD_FLAG_GV_RATEB )  rate = 1;
                else if( parse.getFlags() & CMD_FLAG_GV_RATEC )  rate = 2;
                else if( parse.getFlags() & CMD_FLAG_GV_RATED )  rate = 3;

                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, (offset + rate * 2 + 1), AnalogPointType);
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, (offset + rate * 2), AnalogPointType);
            }
            else
            {
                CtiPointSPtr kwh, kw;
                point_info time_info;
                unsigned long peak_time;
                string pointname;
                int tags = 0;

                if( parse.getFlags() & CMD_FLAG_GV_KVARH || parse.getFlags() & CMD_FLAG_GV_KVAH  )
                {
                    offset = PointOffset_TOU_KMBase;
                    pointname  = "kMH rate ";
                }
                else
                {
                    offset = PointOffset_TOU_KWBase;
                    pointname  = "kWH rate ";
                    tags = TAG_POINT_MUST_ARCHIVE;
                }

                if(      parse.getFlags() & CMD_FLAG_GV_RATEA )  rate = 0;
                else if( parse.getFlags() & CMD_FLAG_GV_RATEB )  rate = 1;
                else if( parse.getFlags() & CMD_FLAG_GV_RATEC )  rate = 2;
                else if( parse.getFlags() & CMD_FLAG_GV_RATED )  rate = 3;

                pi = CtiDeviceMCT470::getData(DSt->Message, 5, ValueType_IED);

                pointname += string(1, (char)('A' + rate));
                pointname += " total";

                insertPointDataReport(AnalogPointType, offset + rate * 2 + 1,
                                      ReturnMsg, pi, pointname, 0UL, 1.0, tags);

                //  this is CRAZY WIN32 SPECIFIC
                _TIME_ZONE_INFORMATION tzinfo;
                int timezone_offset = 0;

                switch( GetTimeZoneInformation(&tzinfo) )
                {
                    //  Bias is in minutes
                    case TIME_ZONE_ID_STANDARD:     timezone_offset = (tzinfo.Bias + tzinfo.StandardBias) * 60; break;
                    case TIME_ZONE_ID_DAYLIGHT:     timezone_offset = (tzinfo.Bias + tzinfo.DaylightBias) * 60; break;

                    case TIME_ZONE_ID_INVALID:
                    case TIME_ZONE_ID_UNKNOWN:
                    default:
                        break;
                }

                pi        = getData(DSt->Message + 5, 3, ValueType_IED);
                time_info = CtiDeviceMCT4xx::getData(DSt->Message + 8, 4, ValueType_Raw);
                peak_time = (unsigned long)time_info.value + timezone_offset;

                pointname  = "kW rate ";
                pointname += string(1, (char)('A' + rate));
                pointname += " peak";

                insertPointDataReport(AnalogPointType, offset + rate * 2,
                                      ReturnMsg, pi, pointname, peak_time);
            }
        }

        ReturnMsg->setResultString(ReturnMsg->ResultString() + resultString);
        decrementGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList, getGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection) );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetConfigIED(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int status = NORMAL,
        rate   = 0;

    point_info     pi;
    string         resultString;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    INT ErrReturn  =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData     = NULL;

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

        switch( InMessage->Sequence )
        {
            case Emetcon::GetConfig_IEDTime:
            {
                //  this is CRAZY WIN32 SPECIFIC
                _TIME_ZONE_INFORMATION tzinfo;
                int timezone_offset = 0;

                switch( GetTimeZoneInformation(&tzinfo) )
                {
                    //  Bias is in minutes
                    case TIME_ZONE_ID_STANDARD:     timezone_offset = (tzinfo.Bias + tzinfo.StandardBias) * 60; break;
                    case TIME_ZONE_ID_DAYLIGHT:     timezone_offset = (tzinfo.Bias + tzinfo.DaylightBias) * 60; break;

                    case TIME_ZONE_ID_INVALID:
                    case TIME_ZONE_ID_UNKNOWN:
                    default:
                        break;
                }

                point_info  pi_time  = CtiDeviceMCT4xx::getData(DSt->Message, 4, ValueType_Raw);
                unsigned long ied_time = (unsigned long)pi_time.value + timezone_offset;

                _iedTime = CtiTime(ied_time);

                resultString += getName() + " / current time: " + printable_time(ied_time) + "\n";

                if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) )
                {
                    resultString += getName() + " / configuration byte not read, cannot decode IED TOU rate\n";
                }
                else
                {
                    int rate;

                    switch( (getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) & 0xf0) >> 4 )
                    {
                        case IED_Type_LG_S4:
                        {
                            rate = (DSt->Message[4] & 0x0f);

                            resultString += getName() + " / current TOU rate: " + string(1, 'A' + rate) + "\n";

                            break;
                        }

                        case IED_Type_Alpha_A3:
                        case IED_Type_Alpha_PP:
                        {
                            rate = (DSt->Message[4] >> 2) & 0x03;

                            resultString += getName() + " / current TOU rate: " + string(1, 'A' + rate) + "\n";

                            break;
                        }

                        case IED_Type_GE_kV2c:
                        {
                            rate = DSt->Message[4] & 0x07;

                            resultString += getName() + " / current TOU rate: ";

                            if( rate )  resultString += string(1, 'A' + rate - 1) + "\n";
                            else        resultString += "demand only\n";

                            break;
                        }

                        case IED_Type_Sentinel:
                        {
                            //  doesn't support TOU rate reporting
                            break;
                        }

                        default:
                        {
                            resultString += getName() + " / current TOU rate: (unhandled IED type (" + CtiNumStr(getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) >> 4) + "), cannot decode)\n";
                            break;
                        }
                    }
                }

                //  as soon as we know what type of IED this is, we can do all of the fancy IED-specific stuff
                /*

                Byte 4 for the Alpha is the current TOU rate

                Byte 4 for the S4 contains:
                0..2
                Binary
                0 for rate bin A
                1 for rate bin B
                2 for rate bin C
                3 for rate bin D
                4 for rate bin E (RX Only)
                3
                Binary
                Frequency of line voltage
                0 for 60Hz, 1 for 50Hz
                (used for DX and RX 3.00 and greater;
                not used in RX prior to 3.00)
                4..5
                Binary
                Active season,
                0 for season 1
                1 for season 2
                2 for season 3
                3 for season 4
                6 Binary Active holiday type
                Zero if type 1, one if type 2

                */

                /*
                resultString = getName() + " / phase A: " + (DSt->Message[] & 0x02)?"present":"not present" + "\n";
                resultString = getName() + " / phase B: " + (DSt->Message[] & 0x04)?"present":"not present" + "\n";
                resultString = getName() + " / phase C: " + (DSt->Message[] & 0x08)?"present":"not present" + "\n";
                */

                pi = CtiDeviceMCT470::getData(DSt->Message + 5, 2, ValueType_IED);

                resultString += valueReport("demand reset count", pi);

                pi_time  = CtiDeviceMCT4xx::getData(DSt->Message + 7, 4, ValueType_Raw);
                ied_time = (unsigned long)pi_time.value + timezone_offset;

                resultString += "\n" + getName() + " / time of last reset: " + printable_time(ied_time) + "\n";

                pi = CtiDeviceMCT470::getData(DSt->Message + 11, 2, ValueType_IED);

                insertPointDataReport(AnalogPointType, PointOffset_OutageCount,
                                      ReturnMsg, pi, "Outage Count");

                if( pi.quality == InvalidQuality )
                {
                    insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_OutageCount, AnalogPointType);
                }

                break;
            }

            case Emetcon::GetConfig_IEDDNP:
            {
                int mctPoint = DSt->Message[0];
                for( int i = 0; i < 6; i++ )
                {
                    pi = CtiDeviceMCT4xx::getData(DSt->Message + 1 + 2*i, 2, ValueType_Raw);
                    if( mctPoint + i < MCT470_DNP_MCTPoint_RealTimeAnalog )
                    {
                        resultString += "Yukon Binary Point: " + CtiNumStr(mctPoint + i + PointOffset_DNPStatus_RealTime1) + " set to DNP point " + CtiNumStr(pi.value-1) + (pi.value <= 0 ? " (disabled) " : "") + "\n";
                    }
                    else if( mctPoint + i  < MCT470_DNP_MCTPoint_RealTimeAccumulator )
                    {
                        resultString += "Yukon Analog Point: " + CtiNumStr(mctPoint + i + PointOffset_DNPAnalog_RealTime1 - MCT470_DNP_MCTPoint_RealTimeAnalog) + " set to DNP point " + CtiNumStr(pi.value-1) + (pi.value <= 0 ? " (disabled) " : "") + "\n";
                    }
                    else if( mctPoint + i  >= MCT470_DNP_MCTPoint_RealTimeAccumulator )
                    {
                        resultString += "Yukon Accumulator Point: " + CtiNumStr(mctPoint + i + PointOffset_DNPCounter_RealTime1 - MCT470_DNP_MCTPoint_RealTimeAccumulator) + " set to DNP point " + CtiNumStr(pi.value-1) + (pi.value <= 0 ? " (disabled) " : "") + "\n";
                    }
                }
                resultString += "For this read, the MCT point in the 470 was: " + CtiNumStr(mctPoint) + ". DNP points are real DNP point id's (0 based).\n";

                break;
            }

            case Emetcon::GetConfig_IEDScan:
            {
                resultString += getName() + " / function not implemented yet\n";

                break;
            }
        }

        if( !ReturnMsg->ResultString().empty() )
        {
            resultString = ReturnMsg->ResultString() + "\n" + resultString;
        }

        ReturnMsg->setResultString(resultString);

        decrementGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList, getGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection) );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetStatusInternal( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
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

        resultString += getName() + " / Internal Status:\n";

        //  Point offset 10
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x01)?"Power fail occurred\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x02)?"Electronic meter communication error\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x04)?"Stack overflow\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x08)?"Power fail carryover\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x10)?"RTC adjusted\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x20)?"Holiday Event occurred\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x40)?"DST Change occurred\n":"";
        resultString += (InMessage->Buffer.DSt.Message[1] & 0x80)?"Negative time sync occurred\n":"";

        //  Point offset 20
        resultString += (InMessage->Buffer.DSt.Message[2] & 0x01)?"Zero usage on channel 1 for 24 hours\n":"";
        resultString += (InMessage->Buffer.DSt.Message[2] & 0x02)?"Zero usage on channel 2 for 24 hours\n":"";
        resultString += (InMessage->Buffer.DSt.Message[2] & 0x04)?"Zero usage on channel 3 for 24 hours\n":"";
        resultString += (InMessage->Buffer.DSt.Message[2] & 0x08)?"Zero usage on channel 4 for 24 hours\n":"";
        resultString += (InMessage->Buffer.DSt.Message[2] & 0x10)?"Address corruption\n":"";
        //  0x20-0x80 aren't used yet

        //  Point offset 30
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x01)?"Group addressing disabled\n":"Group addressing enabled\n";
        //  0x02 is not used yet
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x04)?"DST active\n":"DST inactive\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x08)?"Holiday active\n":"";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x10)?"TOU disabled\n":"TOU enabled\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x20)?"Time sync needed\n":"In time sync\n";
        resultString += (InMessage->Buffer.DSt.Message[0] & 0x40)?"Critical peak active\n":"Critical peak inactive\n";
        //  0x80 is used internally

        ReturnMsg->setResultString(resultString);

        for( int i = 0; i < 3; i++ )
        {
            int offset;
            boost::shared_ptr<CtiPointStatus> point;
            CtiPointDataMsg *pData;
            string pointResult;

            if( i == 0 )  offset = 30;
            if( i == 1 )  offset = 10;
            if( i == 2 )  offset = 20;

            for( int j = 0; j < 8; j++ )
            {
                //  Don't send the powerfail status again - it's being sent by dev_mct in ResultDecode()
                if( (offset + j != 10) && (point = boost::static_pointer_cast<CtiPointStatus>(getDevicePointOffsetTypeEqual( offset + j, StatusPointType ))) )
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


INT CtiDeviceMCT470::decodeGetStatusLoadProfile( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
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

        string resultString;
        unsigned long lpTime;

        CtiReturnMsg     *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg  *pData = NULL;

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        resultString += getName() + " / Load Profile Channel " + CtiNumStr(lp_channel) + " Status:\n";

        lpTime = DSt->Message[0] << 24 |
                 DSt->Message[1] << 16 |
                 DSt->Message[2] <<  8 |
                 DSt->Message[3];

        resultString += "Current Interval Time: " + printable_time(lpTime) + "\n";
        resultString += "Current Interval Pointer: " + CtiNumStr(DSt->Message[4]) + string("\n");

        resultString += "\n";

        resultString += getName() + " / Load Profile Channel " + CtiNumStr(lp_channel + 1) + string(" Status:\n");

        lpTime = DSt->Message[5] << 24 |
                 DSt->Message[6] << 16 |
                 DSt->Message[7] <<  8 |
                 DSt->Message[8];

        resultString += "Current Interval Time: " + printable_time(lpTime) + "\n";
        resultString += "Current Interval Pointer: " + CtiNumStr(DSt->Message[9]) + string("\n");

        resultString += "\n";

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetStatusDNP( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NORMAL, lp_channel;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    unsigned char *geneBuf = InMessage->Buffer.DSt.Message;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiString resultString;
        CtiTime errTime;

        CtiReturnMsg     *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg  *pData = NULL;

        if( (ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        int dnpStatus          = DSt->Message[0];
        int mctPoint           = DSt->Message[1];
        int dnpPoint           = DSt->Message[2] << 8 |
                                 DSt->Message[3];
        int commandStatus      = DSt->Message[4];
        unsigned long ied_time = DSt->Message[5]  << 24 |
                                 DSt->Message[6]  << 16 |
                                 DSt->Message[7]  <<  8 |
                                 DSt->Message[8];

        resultString += getName() + " / DNP status returned: " + resolveDNPStatus(dnpStatus) + "(" + CtiNumStr(dnpStatus) + ")" + "\n";

        if( mctPoint <= 40 )
        {
            resultString += "Point Type: Status Point\n";
        }
        else if( mctPoint <= 102 )
        {
            resultString += "Point Type: Analog Point\n";
        }
        else if( mctPoint <= 148 )
        {
            resultString += "Point Type: Accumulator Point\n";
        }
        resultString += "DNP point: " + CtiNumStr(dnpPoint) + "\n";
        resultString += "Command status: " + resolveDNPStatus(commandStatus) + "(" + CtiNumStr(commandStatus) + ")" + "\n";
        resultString += "Last Successful Read: " + printable_time(ied_time) + "(" + CtiNumStr(ied_time) + ")" + "\n";

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetConfigIntervals(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        string resultString;

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
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval, (long)DSt->Message[1] * 60L);

        resultString += getName() + " / Load Profile Interval 2: " + CtiNumStr(DSt->Message[2]) + " minutes\n";
        // setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval, (long)DSt->Message[2] * 60L);  //  eventually?

        resultString += getName() + " / Table Read Interval: " + CtiNumStr(DSt->Message[3] * 15) + " seconds\n";

        resultString += getName() + " / Precanned Meter Number: " + CtiNumStr(DSt->Message[4]) + "\n";
        resultString += getName() + " / Precanned Table Type: "   + CtiNumStr(DSt->Message[5]) + "\n";

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


string CtiDeviceMCT470::describeChannel(unsigned char channel_config) const
{
    string result_string;

    /*
    Bits 0-1 - Type:    00=Channel Not Used
                        01 = Electronic Meter
                        10 = 2-wire KYZ (form A)
                        11 = 3-wire KYZ (form C)
    Bits 2-5 - Physical Channel / Attached Meter's Channel: 0000=1      0100=5      1000=9      1100=13
                                                            0001=2      0101=6      1001=10     1101=14
                                                            0010=3      0110=7      1010=11     1110=15
                                                            0011=4      0111=8      1011=12     1111=16
    Bit 6 - Load Profile Interval #0 or #1 (0, 1)
    */

    if( channel_config & 0x03 )
    {
        switch( channel_config & 0x03 )
        {
            case 1:
            {
                if( hasDynamicInfo(Keys::Key_MCT_Configuration) )
                {
                    result_string += resolveIEDName(getDynamicInfo(Keys::Key_MCT_Configuration) >> 4) + " ";
                }
                else
                {
                    result_string += "Electronic meter ";
                }

                break;
            }

            case 2:     result_string += "2-wire KYZ (form A) ";    break;
            case 3:     result_string += "3-wire KYZ (form C) ";    break;
        }

        if( channel_config & 0x02 ) result_string += "physical input ";
        else                        result_string += "channel ";

        result_string += CtiNumStr(((channel_config >> 2) & 0x0f) + 1);

        if( channel_config & 0x02 )
        {
            //  TODO:  add support for LP interval 2
            if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval) )
            {
                result_string += ", " + CtiNumStr(getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval) / 60) + " minute interval";
            }
            else
            {
                result_string += ", LP interval ";
                result_string += (channel_config & 0x40)?"2":"1";
            }
        }
        else if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval) )
        {
            result_string += ", " + CtiNumStr(getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval) / 60) + " minute interval";
        }
    }
    else
    {
        result_string += "Channel not used";
    }

    return result_string;
}


INT CtiDeviceMCT470::decodeGetConfigChannelSetup(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        string result_string, dynamic_info;

        result_string += getName() + " / Load Profile Interval 1: " + CtiNumStr(DSt->Message[4]) + " minutes\n";
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval, (long)DSt->Message[4] * 60);

        result_string += getName() + " / Load Profile Interval 2: " + CtiNumStr(DSt->Message[5]) + " minutes\n";
        //  setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval, DSt->Message[5] * 60);  //  someday?

        result_string += getName() + " / Electronic Meter Load Profile Interval: " + CtiNumStr(DSt->Message[6]) + " minutes\n";
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval, (long)DSt->Message[6] * 60);

        for( int i = 0; i < ChannelCount; i++ )
        {
            result_string += getName() + " / LP Channel " + CtiNumStr(i+1) + " config: ";

            result_string += describeChannel(DSt->Message[i]) + "\n";

            /*
            Bits 0-1 - Type:    00=Channel Not Used
                                01 = Electronic Meter
                                10 = 2-wire KYZ (form A)
                                11 = 3-wire KYZ (form C)
            Bits 2-5 - Physical Channel / Attached Meter's Channel
            Bit 6 - Load Profile Interval #0 or #1 (0, 1)
            */

            //  type
            dynamic_info += CtiNumStr(DSt->Message[i] & 0x03);

            if( DSt->Message[i] & 0x03 )
            {
                //  input
                dynamic_info  += CtiNumStr((DSt->Message[i] >> 2) & 0x0f).hex();
                //  load profile interval
                dynamic_info  += (DSt->Message[i] & 0x40)?"1":"0";
            }
            else
            {
                dynamic_info  += "00";
            }
        }

        if( getMCTDebugLevel(DebugLevel_DynamicInfo) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" LP config decode - \"" << dynamic_info << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        //  I don't like how this bit of dynamic info is set and used...  we should use a generic translation function instead
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, dynamic_info);

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString(result_string.c_str());

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetConfigModel(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        string sspec;
        string options;
        int ssp, rev;
        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi


        ssp  = InMessage->Buffer.DSt.Message[0];
        ssp |= InMessage->Buffer.DSt.Message[4] << 8;

        rev  = (unsigned)InMessage->Buffer.DSt.Message[1];

        //  set the dynamic info for use later
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec,         ssp);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, rev);

        //  convert 10 to 1.0, 24 to 2.4
        sspec  = "Software Specification " + CtiNumStr(ssp) + "  Rom Revision " + CtiNumStr(((double)rev) / 10.0, 1);

        //  valid/released versions are 1.0 - 24.9
        if( rev <= SspecRev_BetaLo ||
            rev >= SspecRev_BetaHi )
        {
            sspec += " [possible development revision]\n";
        }
        else
        {
            sspec += "\n";
        }

        //  this will need to be removed when we do this automatically
        setDynamicInfo(Keys::Key_MCT_Configuration, InMessage->Buffer.DSt.Message[3]);

        options += "Connected Meter: " + resolveIEDName(DSt->Message[3] >> 4) + "\n";

        if( InMessage->Buffer.DSt.Message[3] & 0x04 )
        {
            options += "Multiple electronic meters attached\n";
        }

        options += "DST ";
        options += (InMessage->Buffer.DSt.Message[3] & 0x01)?"enabled\n":"disabled\n";

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString( sspec + options );

        decrementGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection);
        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetConfigMultiplier(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    string descriptor;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT &DSt   = InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

        int channel = parse.getiValue("multchannel");

        //  make sure it starts with 1 or 3
        channel -= (channel - 1) % 2;

        for( int i = 0; i < 2; i++ )
        {
            int offset = i * 5;

            descriptor += getName() + " / channel " + CtiNumStr(channel + i).toString() + " configuration:\n";

            descriptor += describeChannel(DSt.Message[offset]) + "\n";

            //  is it a pulse input?
            if( DSt.Message[offset] & 0x02 )
            {
                int meter_ratio, k;

                meter_ratio = DSt.Message[offset+1] << 8 | DSt.Message[offset+2];
                k           = DSt.Message[offset+3] << 8 | DSt.Message[offset+4];

                descriptor += "Meter ratio / K: " + CtiNumStr(meter_ratio) + " / " + CtiNumStr(k) + "\n";

                if( k )
                {
                    descriptor += "Effective multiplier: " + CtiNumStr((double)meter_ratio / (double)k, 4).toString() + "\n";
                }
            }
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString(descriptor);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


//I hate to do this, we must ensure no one ever passes a null buffer into here...
void CtiDeviceMCT470::decodeDNPRealTimeRead(BYTE *buffer, int readNumber, string &resultString, CtiReturnMsg *ReturnMsg, INMESS *InMessage)
{
    point_info   pi;
    CtiPointSPtr tempPoint;
    string point_string;
    if( buffer != NULL && ReturnMsg != NULL)
    {
        bool errorFlagSet = (buffer[0] & 0x01) ? true : false;

        if( errorFlagSet )
        {
            resultString += "Error flag was set, points will not be updated \n";
        }

        int binaryoffset  = readNumber == 1 ? PointOffset_DNPStatus_RealTime1  : PointOffset_DNPStatus_RealTime2;
        int analogoffset  = readNumber == 1 ? PointOffset_DNPAnalog_RealTime1  : PointOffset_DNPAnalog_RealTime2;
        int counteroffset = readNumber == 1 ? PointOffset_DNPCounter_RealTime1 : PointOffset_DNPCounter_RealTime2;

        pi = CtiDeviceMCT4xx::getData(buffer, 1, ValueType_Raw);//Gets pi built up properly...
        for( int i = 0; i < 7; i++ )//only 7 in the first byte
        {
            if( !errorFlagSet )
            {
                string pointname;
                pointname  = "Status point ";
                pointname += CtiNumStr(binaryoffset+i);

                pi.value = (buffer[0] >> (i+1)) & 0x01;

                insertPointDataReport(StatusPointType, binaryoffset + i,
                                      ReturnMsg, pi, pointname);
            }
            else
            {
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, binaryoffset+i, StatusPointType);
            }
        }

        for( i = 0; i < 8; i++ )
        {
            if( !errorFlagSet )
            {
                string pointname;
                pointname  = "Status point ";
                pointname += CtiNumStr(binaryoffset + i + 7);

                pi.value = (buffer[1] >> i) & 0x01;

                insertPointDataReport(StatusPointType, binaryoffset + i + 7,
                                      ReturnMsg, pi, pointname);
            }
            else
            {
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, binaryoffset+i, StatusPointType);
            }
        }


        for( i = 0; i < 3; i++ )
        {
            if( !errorFlagSet )
            {
                string pointname;
                pointname  = "Analog point ";
                pointname += CtiNumStr(analogoffset + i);

                pi = CtiDeviceMCT4xx::getData(buffer + 2 * (i + 1), 2, ValueType_Raw);

                insertPointDataReport(AnalogPointType, analogoffset + i,
                                      ReturnMsg, pi, pointname);
            }
            else
            {
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, analogoffset+i, AnalogPointType);
            }
        }

        for( i = 0; i < 2; i++ )
        {
            if( !errorFlagSet )
            {
                string pointname;
                pointname  = "Pulse Accumulator point ";
                pointname += CtiNumStr(counteroffset + i);

                pi = CtiDeviceMCT4xx::getData(buffer + 2 * i + 8, 2, ValueType_Raw);

                insertPointDataReport(PulseAccumulatorPointType, counteroffset + i,
                                      ReturnMsg, pi, pointname);
            }
            else
            {
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, counteroffset+i, PulseAccumulatorPointType);
            }
        }

    }
}

//This expects a string in the format: "01 0x01 01 020 055 0x0040" ect..
void CtiDeviceMCT470::getBytesFromString(string &values, BYTE* buffer, int buffLen, int &numValues, int fillCount, int bytesPerValue)
{
    CtiString   valueCopy(values);
    CtiString   temp;
    CtiString   token;
    int iValue;
    numValues = 0;

    if( buffer != NULL )
    {
        CtiString anyNum;
        anyNum = "(([0-9]+) *|(0x[0-9a-f]+) *)+";

        if(!(token = valueCopy.match(anyNum)).empty())
        {
            CtiTokenizer cmdtok(token);

            while( !(temp = cmdtok()).empty() && numValues*bytesPerValue < buffLen )
            {
                iValue = atoi(temp.data());
                for( int i=0; i<bytesPerValue; i++ )
                {
                    buffer[numValues*bytesPerValue + i] = iValue>>(8*(bytesPerValue-1-i));
                }
                numValues++;
            }
        }

        if( numValues < fillCount)
        {
            for( int i=numValues; i<fillCount; i++)
            {
                for( int a=0; a<bytesPerValue; a++ )
                {
                    buffer[i*bytesPerValue + a] = 0;
                }

            }
        }
    }
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
        dout << CtiTime() << " **** Checkpoint - in CtiDeviceMCT470::DecodeDatabaseReader for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Default data class: " << _iedPort.getDefaultDataClass() << endl;
        dout << "Default data offset: " << _iedPort.getDefaultDataOffset() << endl;
        dout << "Device ID: " << _iedPort.getDeviceID() << endl;
        dout << "IED Scan Rate: " << _iedPort.getIEDScanRate() << endl;
        dout << "IED Type: " << _iedPort.getIEDType() << endl;
        dout << "Password: " << _iedPort.getPassword() << endl;
        dout << "Real Time Scan Flag: " << _iedPort.getRealTimeScanFlag() << endl;
    }
}


string CtiDeviceMCT470::resolveDNPStatus(int status)
{
    string result;

    switch(status)
    {
        case 0:     result = "Success";                     break;
        case 1:     result = "Failed Data Link Header";     break;
        case 2:     result = "Failed Application Header";   break;
        case 3:     result = "Failed Object Header";        break;
        case 4:     result = "Invalid Point Configuration"; break;
        case 5:     result = "Command Failed";              break;
        case 6:     result = "Communications Failure";      break;
        case 7:     result = "Application Message Length Error";    break;
        case 8:     result = "Data Request Failed";         break;
        case 9:     result = "Data Storage Failed";         break;
        default:    result = "Unknown";                     break;
    }

    return result;
}


string CtiDeviceMCT470::resolveIEDName(int bits)
{
    string name;

    switch( bits & 0x0f )
    {
        case IED_Type_Alpha_A3: name += "Alpha A3";             break;
        case IED_Type_Alpha_PP: name += "Alpha Power Plus";     break;
        case IED_Type_DNP:      name += "DNP";                  break;
        case IED_Type_GE_kV:    name += "GE kV";                break;
        case IED_Type_GE_kV2:   name += "GE kV2";               break;
        case IED_Type_GE_kV2c:  name += "GE kV2c";              break;
        case IED_Type_LG_S4:    name += "Landis and Gyr S4";    break;
        case IED_Type_Sentinel: name += "Sentinel";             break;

        case IED_Type_None:     name += "None";                 break;

        default:                name += "Unknown (" + CtiNumStr(bits).xhex().zpad(2) + ")";   break;
    }

    return name;
}


CtiDeviceMCT470::IED_Types CtiDeviceMCT470::resolveIEDType(const string &iedType)
{
    IED_Types retVal = IED_Type_None;
    CtiString iedString = iedType;
    iedString.toLower();

    if(      iedString == "s4" )        retVal = IED_Type_LG_S4;
    else if( iedString == "alphaa3" )   retVal = IED_Type_Alpha_A3;
    else if( iedString == "alphapp" )   retVal = IED_Type_Alpha_PP;
    else if( iedString == "gekv" )      retVal = IED_Type_GE_kV;
    else if( iedString == "gekv2" )     retVal = IED_Type_GE_kV2;
    else if( iedString == "gekv2c" )    retVal = IED_Type_GE_kV2c;
    else if( iedString == "sentinel" )  retVal = IED_Type_Sentinel;

    return retVal;
}
