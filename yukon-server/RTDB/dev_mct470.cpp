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
* REVISION     :  $Revision: 1.62 $
* DATE         :  $Date: 2006/09/13 16:02:43 $
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
#include "mgr_point.h"
#include "pt_accum.h"
#include "numstr.h"
#include "porter.h"
#include "dllyukon.h"
#include "utility.h"
#include "numstr.h"
#include "config_base.h"
#include "config_parts.h"
#include "ctistring.h"
#include <string>

using Cti::Protocol::Emetcon;
using namespace Cti::Config::MCT;
using namespace Cti::Config;

const CtiDeviceMCT470::CommandSet      CtiDeviceMCT470::_commandStore = CtiDeviceMCT470::initCommandStore();
const CtiDeviceMCT470::ConfigPartsList CtiDeviceMCT470::_config_parts = CtiDeviceMCT470::initConfigParts();

const CtiDeviceMCT::DynamicPaoAddressing_t         CtiDeviceMCT470::_dynPaoAddressing     = CtiDeviceMCT470::initDynPaoAddressing();
const CtiDeviceMCT::DynamicPaoFunctionAddressing_t CtiDeviceMCT470::_dynPaoFuncAddressing = CtiDeviceMCT470::initDynPaoFuncAddressing();

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
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_options);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_addressing);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_holiday);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_llp);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_lpchannel);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_relays);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_precanned_table);

    return tempList;
}

CtiDeviceMCT470::ConfigPartsList CtiDeviceMCT470::getPartsList()
{
    return _config_parts;
}

CtiDeviceMCT470::CommandSet CtiDeviceMCT470::initCommandStore( )
{
    CommandSet cs;

    cs.insert(CommandStore(Emetcon::Command_Loop,               Emetcon::IO_Read,           Memory_ModelPos,             1));
    cs.insert(CommandStore(Emetcon::Scan_Accum,                 Emetcon::IO_Function_Read,  FuncRead_MReadPos,           FuncRead_MReadLen));
    cs.insert(CommandStore(Emetcon::GetValue_Default,           Emetcon::IO_Function_Read,  FuncRead_MReadPos,           FuncRead_MReadLen));
    cs.insert(CommandStore(Emetcon::Scan_Integrity,             Emetcon::IO_Function_Read,  MCT470_FuncRead_DemandPos,          MCT470_FuncRead_DemandLen));
    cs.insert(CommandStore(Emetcon::GetValue_Demand,            Emetcon::IO_Function_Read,  MCT470_FuncRead_DemandPos,          MCT470_FuncRead_DemandLen));
    cs.insert(CommandStore(Emetcon::Scan_LoadProfile,           Emetcon::IO_Function_Read,  0,                                  0));
    cs.insert(CommandStore(Emetcon::GetValue_Demand,            Emetcon::IO_Function_Read,  MCT470_FuncRead_DemandPos,          MCT470_FuncRead_DemandLen));
    cs.insert(CommandStore(Emetcon::GetValue_PeakDemand,        Emetcon::IO_Function_Read,  MCT470_FuncRead_PeakDemandBasePos,  MCT470_FuncRead_PeakDemandLen));
    cs.insert(CommandStore(Emetcon::PutValue_KYZ,               Emetcon::IO_Write,          MCT470_FuncWrite_CurrentReading,    MCT470_FuncWrite_CurrentReadingLen));
    cs.insert(CommandStore(Emetcon::PutConfig_Raw,              Emetcon::IO_Write,          0,                                  0));  //  filled in later
    cs.insert(CommandStore(Emetcon::GetConfig_Raw,              Emetcon::IO_Read,           0,                                  0));  //  filled in later
    cs.insert(CommandStore(Emetcon::GetConfig_Model,            Emetcon::IO_Read,           MCT470_Memory_ModelPos,             MCT470_Memory_ModelLen));
    cs.insert(CommandStore(Emetcon::GetConfig_Multiplier,       Emetcon::IO_Read,           MCT470_Memory_ChannelMultiplierPos, MCT470_Memory_ChannelMultiplierLen));
    cs.insert(CommandStore(Emetcon::PutConfig_Multiplier,       Emetcon::IO_Write,          MCT470_Memory_ChannelMultiplierPos, MCT470_Memory_ChannelMultiplierLen));
    cs.insert(CommandStore(Emetcon::GetConfig_Time,             Emetcon::IO_Read,           MCT470_Memory_TimeZoneOffsetPos,    MCT470_Memory_TimeZoneOffsetLen +
                                                                                                                                MCT470_Memory_RTCLen));
    cs.insert(CommandStore(Emetcon::GetConfig_TSync,            Emetcon::IO_Read,           MCT470_Memory_LastTSyncPos,         MCT470_Memory_LastTSyncLen));
    cs.insert(CommandStore(Emetcon::PutConfig_TimeZoneOffset,   Emetcon::IO_Write,          MCT470_Memory_TimeZoneOffsetPos,    MCT470_Memory_TimeZoneOffsetLen));
    cs.insert(CommandStore(Emetcon::PutConfig_Intervals,        Emetcon::IO_Function_Write, MCT470_FuncWrite_IntervalsPos,      MCT470_FuncWrite_IntervalsLen));
    cs.insert(CommandStore(Emetcon::GetConfig_Intervals,        Emetcon::IO_Read,           MCT470_Memory_IntervalsPos,         MCT470_Memory_IntervalsLen));
    cs.insert(CommandStore(Emetcon::GetConfig_ChannelSetup,     Emetcon::IO_Function_Read,  MCT470_FuncRead_ChannelSetupPos,    MCT470_FuncRead_ChannelSetupLen));
    cs.insert(CommandStore(Emetcon::GetValue_LoadProfile,       Emetcon::IO_Function_Read,  0,                                  0));
    cs.insert(CommandStore(Emetcon::GetStatus_LoadProfile,      Emetcon::IO_Function_Read,  MCT470_FuncRead_LPStatusCh1Ch2Pos,  MCT470_FuncRead_LPStatusLen));
    cs.insert(CommandStore(Emetcon::GetStatus_Internal,         Emetcon::IO_Read,           MCT470_Memory_StatusPos,            MCT470_Memory_StatusLen));
    cs.insert(CommandStore(Emetcon::GetValue_IED,               Emetcon::IO_Function_Read,  0,  13));  //  filled in by "getvalue ied" code
    cs.insert(CommandStore(Emetcon::GetValue_IEDDemand,         Emetcon::IO_Function_Read,  MCT470_FuncRead_IED_RealTime,       9));  //  magic number
    cs.insert(CommandStore(Emetcon::GetStatus_IEDDNP,           Emetcon::IO_Function_Read,  MCT470_FuncRead_IED_Precanned_Last, 13));
    cs.insert(CommandStore(Emetcon::GetConfig_IEDTime,          Emetcon::IO_Function_Read,  MCT470_FuncRead_IED_TOU_MeterStatus, 13));  //  magic number
    cs.insert(CommandStore(Emetcon::GetConfig_IEDDNP,           Emetcon::IO_Function_Read,  MCT470_FuncRead_IED_DNPTablePos,    MCT470_FuncRead_IED_DNPTableLen));
    cs.insert(CommandStore(Emetcon::PutValue_IEDReset,          Emetcon::IO_Function_Write, MCT470_FuncWrite_IEDCommand,        MCT470_FuncWrite_IEDCommandLen));
    cs.insert(CommandStore(Emetcon::PutStatus_FreezeOne,        Emetcon::IO_Write,          Command_FreezeOne,                  0));
    cs.insert(CommandStore(Emetcon::PutStatus_FreezeTwo,        Emetcon::IO_Write,          Command_FreezeTwo,                  0));

    //******************************** Config Related starts here *************************
    cs.insert(CommandStore(Emetcon::PutConfig_Addressing,       Emetcon::IO_Write,          MCT470_Memory_AddressingPos,        MCT470_Memory_AddressingLen));
    cs.insert(CommandStore(Emetcon::PutConfig_LongloadProfile,  Emetcon::IO_Function_Write, FuncWrite_LLPStoragePos,            FuncWrite_LLPStorageLen));
    cs.insert(CommandStore(Emetcon::GetConfig_LongloadProfile,  Emetcon::IO_Function_Read,  FuncRead_LLPStatusPos,              FuncRead_LLPStatusLen));
    cs.insert(CommandStore(Emetcon::PutConfig_DST,              Emetcon::IO_Write,          MCT470_Memory_DSTBeginPos,          MCT470_Memory_DSTBeginLen +
                                                                                                                                MCT470_Memory_DSTEndLen   +
                                                                                                                                MCT470_Memory_TimeZoneOffsetLen));
    //  used for both "putconfig install" and "putconfig holiday" commands
    cs.insert(CommandStore(Emetcon::PutConfig_Holiday,          Emetcon::IO_Write,          MCT470_Memory_Holiday1Pos,          MCT470_Memory_Holiday1Len +
                                                                                                                                MCT470_Memory_Holiday2Len +
                                                                                                                                MCT470_Memory_Holiday3Len));

    cs.insert(CommandStore(Emetcon::GetConfig_Holiday,          Emetcon::IO_Read,           MCT470_Memory_Holiday1Pos,          MCT470_Memory_Holiday1Len +
                                                                                                                                MCT470_Memory_Holiday2Len +
                                                                                                                                MCT470_Memory_Holiday3Len));

    cs.insert(CommandStore(Emetcon::PutConfig_Options,          Emetcon::IO_Write,          MCT470_FuncWrite_ConfigAlarmMaskPos, MCT470_FuncWrite_ConfigAlarmMaskLen));
    cs.insert(CommandStore(Emetcon::PutConfig_TimeAdjustTolerance, Emetcon::IO_Write,       MCT470_Memory_TimeAdjustTolPos,     MCT470_Memory_TimeAdjustTolLen));

    //************************************ End Config Related *****************************

    return cs;
}

CtiDeviceMCT::DynamicPaoFunctionAddressing_t CtiDeviceMCT470::initDynPaoFuncAddressing()
{
    DynamicPaoAddressing_t addressSet;
    DynamicPaoFunctionAddressing_t functionSet;

    // MCT470_FuncRead_ChannelSetupDataPos
    addressSet.insert(DynamicPaoAddressing(0, 1, Keys::Key_MCT_LoadProfileChannelConfig1));
    addressSet.insert(DynamicPaoAddressing(1, 1, Keys::Key_MCT_LoadProfileChannelConfig2));
    addressSet.insert(DynamicPaoAddressing(2, 1, Keys::Key_MCT_LoadProfileChannelConfig3));
    addressSet.insert(DynamicPaoAddressing(3, 1, Keys::Key_MCT_LoadProfileChannelConfig4));
    addressSet.insert(DynamicPaoAddressing(4, 1, Keys::Key_MCT_LoadProfileInterval));
    addressSet.insert(DynamicPaoAddressing(5, 1, Keys::Key_MCT_LoadProfileInterval2));
    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(MCT470_FuncRead_ChannelSetupDataPos,addressSet));

    addressSet.clear();

    // MCT470_FuncRead_LoadProfileChannel12Pos
    addressSet.insert(DynamicPaoAddressing(0, 1, Keys::Key_MCT_LoadProfileChannelConfig1));
    addressSet.insert(DynamicPaoAddressing(1, 2, Keys::Key_MCT_LoadProfileMeterRatio1));
    addressSet.insert(DynamicPaoAddressing(3, 2, Keys::Key_MCT_LoadProfileKRatio1));
    addressSet.insert(DynamicPaoAddressing(5, 1, Keys::Key_MCT_LoadProfileChannelConfig2));
    addressSet.insert(DynamicPaoAddressing(6, 2, Keys::Key_MCT_LoadProfileMeterRatio2));
    addressSet.insert(DynamicPaoAddressing(8, 2, Keys::Key_MCT_LoadProfileKRatio2));
    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(MCT470_FuncRead_LoadProfileChannel12Pos,addressSet));

    addressSet.clear();

    // MCT470_FuncRead_LoadProfileChannel34Pos
    addressSet.insert(DynamicPaoAddressing(0, 1, Keys::Key_MCT_LoadProfileChannelConfig3));
    addressSet.insert(DynamicPaoAddressing(1, 2, Keys::Key_MCT_LoadProfileMeterRatio3));
    addressSet.insert(DynamicPaoAddressing(3, 2, Keys::Key_MCT_LoadProfileKRatio3));
    addressSet.insert(DynamicPaoAddressing(5, 1, Keys::Key_MCT_LoadProfileChannelConfig4));
    addressSet.insert(DynamicPaoAddressing(6, 2, Keys::Key_MCT_LoadProfileMeterRatio4));
    addressSet.insert(DynamicPaoAddressing(8, 2, Keys::Key_MCT_LoadProfileKRatio4));
    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(MCT470_FuncRead_LoadProfileChannel34Pos,addressSet));

    addressSet.clear();

    // MCT470_FuncRead_PrecannedTablePos
    addressSet.insert(DynamicPaoAddressing(0, 1, Keys::Key_MCT_PrecannedTableReadInterval));
    addressSet.insert(DynamicPaoAddressing(1, 1, Keys::Key_MCT_PrecannedMeterNumber));
    addressSet.insert(DynamicPaoAddressing(2, 1, Keys::Key_MCT_PrecannedTableType));
    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(MCT470_FuncRead_LoadProfileChannel34Pos,addressSet));

    addressSet.clear();

    // FuncRead_LLPStatusPos
    addressSet.insert(DynamicPaoAddressing(4, 1, Keys::Key_MCT_LLPChannel1Len));
    addressSet.insert(DynamicPaoAddressing(5, 1, Keys::Key_MCT_LLPChannel2Len));
    addressSet.insert(DynamicPaoAddressing(6, 1, Keys::Key_MCT_LLPChannel3Len));
    addressSet.insert(DynamicPaoAddressing(7, 1, Keys::Key_MCT_LLPChannel4Len));
    functionSet.insert(DynamicPaoFunctionAddressing_t::value_type(FuncRead_LLPStatusPos,addressSet));

    addressSet.clear();

    return functionSet;
}

bool CtiDeviceMCT470::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    CommandSet::const_iterator itr = _commandStore.find( CommandStore( cmd ) );

    if( itr != _commandStore.end( ) )
    {
        function = itr->function;   //  Copy the relevant bits from the commandStore
        length   = itr->length;
        io       = itr->io;

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

CtiDeviceMCT::DynamicPaoAddressing_t CtiDeviceMCT470::initDynPaoAddressing()
{
    DynamicPaoAddressing_t addressSet;

//  The SSPEC is set in decodeGetConfigModel().
//    This code assumes that the values are contiguous, which the SSPEC is not.
/*
    addressSet.insert(DynamicPaoAddressing(Memory_SSpecPos,                  Memory_SSpecLen,                 Keys::Key_MCT_SSpec));
    addressSet.insert(DynamicPaoAddressing(Memory_RevisionPos,               Memory_RevisionLen,              Keys::Key_MCT_SSpecRevision));
*/
    addressSet.insert(DynamicPaoAddressing(Memory_OptionsPos,                Memory_OptionsLen,               Keys::Key_MCT_Options));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_ConfigurationPos,          MCT470_Memory_ConfigurationLen,         Keys::Key_MCT_Configuration));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_EventFlagsMask1Pos,        MCT470_Memory_EventFlagsMask1Len,       Keys::Key_MCT_EventFlagsMask1));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_EventFlagsMask2Pos,        MCT470_Memory_EventFlagsMask2Len,       Keys::Key_MCT_EventFlagsMask2));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_AddressBronzePos,          MCT470_Memory_AddressBronzePos,         Keys::Key_MCT_AddressBronze));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_AddressLeadPos,            MCT470_Memory_AddressLeadPos,           Keys::Key_MCT_AddressLead));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_AddressCollectionPos,      MCT470_Memory_AddressCollectionPos,     Keys::Key_MCT_AddressCollection));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_AddressSPIDPos,            MCT470_Memory_AddressSPIDPos,           Keys::Key_MCT_AddressServiceProviderID));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_DemandIntervalLen,         MCT470_Memory_DemandIntervalPos,        Keys::Key_MCT_DemandInterval));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_LoadProfileInterval1Pos,   MCT470_Memory_LoadProfileInterval1Len,  Keys::Key_MCT_LoadProfileInterval));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_LoadProfileInterval2Pos,   MCT470_Memory_LoadProfileInterval2Len,  Keys::Key_MCT_LoadProfileInterval2));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_TimeAdjustTolerancePos,    MCT470_Memory_TimeAdjustToleranceLen,   Keys::Key_MCT_TimeAdjustTolerance));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_DSTBeginPos,               MCT470_Memory_DSTBeginLen,              Keys::Key_MCT_DSTStartTime));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_DSTEndPos,                 MCT470_Memory_DSTEndLen,                Keys::Key_MCT_DSTEndTime));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_TimeZoneOffsetPos,         MCT470_Memory_TimeZoneOffsetLen,        Keys::Key_MCT_TimeZoneOffset));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_TOUDayTablePos,            MCT470_Memory_TOUDayTableLen,           Keys::Key_MCT_DayTable));
    addressSet.insert(DynamicPaoAddressing(Memory_TOUDailySched1Pos,                Memory_TOUDailySched1Len,               Keys::Key_MCT_DaySchedule1));
    addressSet.insert(DynamicPaoAddressing(Memory_TOUDailySched2Pos,                Memory_TOUDailySched2Len,               Keys::Key_MCT_DaySchedule2));
    addressSet.insert(DynamicPaoAddressing(Memory_TOUDailySched3Pos,                Memory_TOUDailySched3Len,               Keys::Key_MCT_DaySchedule3));
    addressSet.insert(DynamicPaoAddressing(Memory_TOUDailySched4Pos,                Memory_TOUDailySched4Len,               Keys::Key_MCT_DaySchedule4));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_TOUDefaultRatePos,         MCT470_Memory_TOUDefaultRateLen,        Keys::Key_MCT_DefaultTOURate));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_Holiday1Pos,               MCT470_Memory_Holiday1Len,              Keys::Key_MCT_Holiday1));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_Holiday2Pos,               MCT470_Memory_Holiday2Len,              Keys::Key_MCT_Holiday2));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_Holiday3Pos,               MCT470_Memory_Holiday3Len,              Keys::Key_MCT_Holiday3));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_KRatio1Pos,                MCT470_Memory_KRatio1Len,               Keys::Key_MCT_LoadProfileKRatio1));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_MeteringRatio1Pos,         MCT470_Memory_MeteringRatio1Len,        Keys::Key_MCT_LoadProfileMeterRatio1));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_ChannelConfig1Pos,         MCT470_Memory_ChannelConfig1Len,        Keys::Key_MCT_LoadProfileChannelConfig1));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_KRatio2Pos,                MCT470_Memory_KRatio2Len,               Keys::Key_MCT_LoadProfileKRatio2));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_MeteringRatio2Pos,         MCT470_Memory_MeteringRatio2Len,        Keys::Key_MCT_LoadProfileMeterRatio2));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_ChannelConfig2Pos,         MCT470_Memory_ChannelConfig2Len,        Keys::Key_MCT_LoadProfileChannelConfig2));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_KRatio3Pos,                MCT470_Memory_KRatio3Len,               Keys::Key_MCT_LoadProfileKRatio3));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_MeteringRatio3Pos,         MCT470_Memory_MeteringRatio3Len,        Keys::Key_MCT_LoadProfileMeterRatio3));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_ChannelConfig3Pos,         MCT470_Memory_ChannelConfig3Len,        Keys::Key_MCT_LoadProfileChannelConfig3));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_KRatio4Pos,                MCT470_Memory_KRatio4Len,               Keys::Key_MCT_LoadProfileKRatio4));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_MeteringRatio4Pos,         MCT470_Memory_MeteringRatio4Len,        Keys::Key_MCT_LoadProfileMeterRatio4));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_ChannelConfig4Pos,         MCT470_Memory_ChannelConfig4Len,        Keys::Key_MCT_LoadProfileChannelConfig4));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_RelayATimerPos,            MCT470_Memory_RelayATimerLen,           Keys::Key_MCT_RelayATimer));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_RelayBTimerPos,            MCT470_Memory_RelayBTimerLen,           Keys::Key_MCT_RelayBTimer));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_RelayATimerPos,            MCT470_Memory_RelayATimerLen,           Keys::Key_MCT_RelayATimer));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_TableReadIntervalPos,      MCT470_Memory_TableReadIntervalLen,     Keys::Key_MCT_PrecannedTableReadInterval));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_PrecannedMeterNumPos,      MCT470_Memory_PrecannedMeterNumLen,     Keys::Key_MCT_PrecannedMeterNumber));
    addressSet.insert(DynamicPaoAddressing(MCT470_Memory_PrecannedTableTypePos,     MCT470_Memory_PrecannedTableTypeLen,    Keys::Key_MCT_PrecannedTableType));
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

    //  note that we're verifying this against the interval that's in the database - more things will be used this way in the future
    retval &= (getDynamicInfo(Keys::Key_MCT_LoadProfileInterval) == getLoadProfile().getLoadProfileDemandRate());

    //  we don't use the second load profile rate yet
    //retval |= (getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval2) == getLoadProfile().getVoltageProfileDemandRate());

    if( retval && ((sspec == MCT470_Sspec && sspec_rev >= MCT470_SspecRevMin && sspec_rev <= MCT470_SspecRevMax)
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

    OutMessage->DeviceID  = getID();
    OutMessage->TargetID  = getID();
    OutMessage->Port      = getPortID();
    OutMessage->Remote    = getAddress();
    OutMessage->Priority  = ScanPriority_LoadProfile;
    OutMessage->TimeOut   = 2;
    OutMessage->Retry     = 2;

    // Tell the porter side to complete the assembly of the message.
    OutMessage->Request.BuildIt = TRUE;

    if( getMCTDebugLevel(MCTDebug_DynamicInfo) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" requesting key (" << key << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if( key == Keys::Key_MCT_SSpec )
    {
        strncpy(OutMessage->Request.CommandStr, "getconfig model", COMMAND_STR_SIZE );
        OutMessage->Sequence  = Emetcon::GetConfig_Model;     // Helps us figure it out later!
    }
    else
    {
        //  the ideal case - the correct, non-development sspec
        if( (getDynamicInfo(Keys::Key_MCT_SSpec)         == MCT470_Sspec       &&
             getDynamicInfo(Keys::Key_MCT_SSpecRevision) >= MCT470_SspecRevMin &&
             getDynamicInfo(Keys::Key_MCT_SSpecRevision) <= MCT470_SspecRevMax)
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
                    strncpy(OutMessage->Request.CommandStr, "getconfig channels", COMMAND_STR_SIZE );
                    OutMessage->Sequence = Emetcon::GetConfig_ChannelSetup;     // Helps us figure it out later!

                    break;
                }
                default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - unhandled key (" << key << ") in CtiDeviceMCT470::requestDynamicInfo **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    delete OutMessage;
                    OutMessage = 0;
                }
            }
        }
        else
        {
            switch( key )
            {
                case Keys::Key_MCT_LoadProfileInterval:
                {
                    strncpy(OutMessage->Request.CommandStr, "getconfig intervals", COMMAND_STR_SIZE );
                    OutMessage->Sequence = Emetcon::GetConfig_Intervals;     // Helps us figure it out later!

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
                    delete OutMessage;
                    OutMessage = 0;
                }
            }
        }
    }

    if( OutMessage )
    {
        outList.push_back(OutMessage);
        OutMessage = NULL;
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

    if( getMCTDebugLevel(MCTDebug_LoadProfile) )
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
            //  the at() function call can throw, so this isn't ideal yet - if we ever go to exception based error handling, we
            //    can be more graceful about this
            try
            {
                if( config.at(channel * 3) == '1' )
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
            catch(...)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" - OOB exception when accessing Key_MCT_LoadProfileConfig **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

    return retval;
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
            if( getMCTDebugLevel(MCTDebug_LoadProfile) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - config request too soon for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
        else
        {
            if( !hasDynamicInfo(Keys::Key_MCT_SSpec) )
            {
                requestDynamicInfo(Keys::Key_MCT_SSpec, OutMessage, outList);
            }
            else
            {
                //  check if we're the IED sspec
                if( (getDynamicInfo(Keys::Key_MCT_SSpec)         == MCT470_Sspec       &&
                     getDynamicInfo(Keys::Key_MCT_SSpecRevision) >= MCT470_SspecRevMin &&
                     getDynamicInfo(Keys::Key_MCT_SSpecRevision) <= MCT470_SspecRevMax)
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
                            OUTMESS *om = new CtiOutMessage(*OutMessage);

                            sendIntervals(om, outList);
                        }

                        requestDynamicInfo(Keys::Key_MCT_LoadProfileInterval, OutMessage, outList);
                    }
                }
            }

            _lastConfigRequest = Now.seconds();
        }
    }
    else
    {
        for( int i = 0; i < LPChannels; i++ )
        {
            demand_rate = getLoadProfileInterval(i);
            block_size  = demand_rate * 6;

            if( useScanFlags() && getLoadProfile().isChannelValid(i) )
            {
                if( _lp_info[i].current_schedule > Now )
                {
                    if( getMCTDebugLevel(MCTDebug_LoadProfile) )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - LP scan too early for device \"" << getName() << "\", aborted **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                else
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

                    if( getMCTDebugLevel(MCTDebug_LoadProfile) )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - LP variable check for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "Now.seconds() = " << Now.seconds() << endl;
                        dout << "_lp_info[" << i << "].archived_reading = " << _lp_info[i].archived_reading << endl;
                        dout << "MCT4XX_LPRecentBlocks * block_size = " << LPRecentBlocks * block_size << endl;
                        dout << "_lp_info[" << i << "].current_request = " << _lp_info[i].current_request << endl;
                    }

                    //  make sure we're aligned (note - rwEpoch is an even multiple of 86400, so no worries)
                    _lp_info[i].current_request -= _lp_info[i].current_request % block_size;

                    //  which block to grab?
                    channel = i + 1;
                    block   = (Now.seconds() - _lp_info[i].current_request) / block_size;

                    descriptor = " channel " + CtiNumStr(channel) + string(" block ") + CtiNumStr(block);

                    strncat( tmpOutMess->Request.CommandStr,
                             descriptor.c_str(),
                             sizeof(tmpOutMess->Request.CommandStr) - ::strlen(tmpOutMess->Request.CommandStr));

                    if( getMCTDebugLevel(MCTDebug_LoadProfile) )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - command string check for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "\"" << tmpOutMess->Request.CommandStr << "\"" << endl;
                    }

                    outList.push_back(tmpOutMess);
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

    if( getMCTDebugLevel(MCTDebug_LoadProfile) )
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
        if( getMCTDebugLevel(MCTDebug_LoadProfile) )
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

        case Emetcon::GetValue_PeakDemand:
        {
            status = decodeGetValuePeakDemand(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetValue_IED:
        case Emetcon::GetValue_IEDDemand:
        {
            status = decodeGetValueIED(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetConfig_IEDDNP:
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

        case Emetcon::GetStatus_IEDDNP:
        {
            status = decodeGetStatusDNP(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetStatus_LoadProfile:
        {
            status = decodeGetStatusLoadProfile(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetConfig_Intervals:
        {
            status = decodeGetConfigIntervals(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case Emetcon::GetConfig_ChannelSetup:
        {
            status = decodeGetConfigChannelSetup(InMessage, TimeNow, vgList, retList, outList);
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

    if( parse.getFlags() & CMD_FLAG_GV_IED )  //  This parse has the token "IED" in it!
    {
        if( getDynamicInfo(Keys::Key_MCT_SSpecRevision) < MCT470_SspecRev_IEDZeroWriteMin )
        {
            //If we need to read out the time, do so.
            function = Emetcon::GetConfig_IEDTime;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
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
            found = getOperation( function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO );
        }
        else if( parse.isKeyValid("ied_dnp") )
        {
            int i = 0;
            if( (i = parse.getiValue("collectionnumber")) != INT_MIN )
            {
                if( i == 1 )
                {
                    function = Emetcon::GetValue_IED;
                    found = getOperation( function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO );
                    OutMessage->Buffer.BSt.Function = MCT470_FuncRead_IED_RealTime;
                }
                else if( i == 2 )
                {
                    function = Emetcon::GetValue_IED;
                    found = getOperation( function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO );
                    OutMessage->Buffer.BSt.Function = MCT470_FuncRead_IED_RealTime2;
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
                found = getOperation( function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO );

                OutMessage->Buffer.BSt.Function = MCT470_FuncRead_IED_Precanned_Base + (i-1) + MCT470_DNP_Analog_Precanned_Offset;
                if( OutMessage->Buffer.BSt.Function >= MCT470_FuncRead_IED_Precanned_Base + MCT470_DNP_Counter_Precanned_Offset )
                {
                    nRet = BADRANGE;
                    found = false;
                }
            }
            else if( (i = parse.getiValue("accumulatornumber")) != INT_MIN )
            {
                function = Emetcon::GetValue_IED;   //This means we have to fill in the function
                found = getOperation( function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO );

                OutMessage->Buffer.BSt.Function = MCT470_FuncRead_IED_Precanned_Base + (i-1) + MCT470_DNP_Counter_Precanned_Offset;
                if( i > MCT470_DNP_Counter_Precanned_Reads ) //only 8 reads possible.
                {
                    nRet = BADRANGE;
                    found = false;
                }
            }
            else if( (i = parse.getiValue("statusnumber")) != INT_MIN )
            {
                function = Emetcon::GetValue_IED;   //This means we have to fill in the function
                found = getOperation( function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO );

                OutMessage->Buffer.BSt.Function = MCT470_FuncRead_IED_Precanned_Base + MCT470_DNP_Status_Precanned_Offset;
            }
            else if( parse.isKeyValid("dnp_crc") )
            {
                function = Emetcon::GetValue_IED;
                OutMessage->Buffer.BSt.Function = MCT470_FuncRead_IED_CRCPos;
                OutMessage->Buffer.BSt.Length = MCT470_FuncRead_IED_CRCLen;
                OutMessage->Buffer.BSt.IO = Emetcon::IO_Function_Read;
                found = true;
            }
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
    else if( parse.getFlags() & CMD_FLAG_GV_PEAK )
    {
        function = Emetcon::GetValue_PeakDemand;
        found = getOperation( function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO );

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
            MCTSystemOptionsSPtr options;

            CtiString originalString = pReq->CommandString();
            boost::regex re_scan ("scan integrity");

            if( deviceConfig )
            {
                options = boost::static_pointer_cast< ConfigurationPart<MCTSystemOptions> >(deviceConfig->getConfigFromType(ConfigTypeMCTSystemOptions));
            }

            if( !options || (options && ( !stringCompareIgnoreCase(options->getValueFromKey(DemandMetersToScan), "all")
                          || !stringCompareIgnoreCase(options->getValueFromKey(DemandMetersToScan), "pulse"))) )
            {
                //Read the pulse demand
                function = Emetcon::GetValue_Demand;
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
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
            }

            if( !options || (options && ( !stringCompareIgnoreCase(options->getValueFromKey(DemandMetersToScan), "all")
                          || !stringCompareIgnoreCase(options->getValueFromKey(DemandMetersToScan), "ied"))) )
            {
                if( getDynamicInfo(Keys::Key_MCT_SSpecRevision) < MCT470_SspecRev_IEDZeroWriteMin )
                {
                    //If we need to read out the time, do so.
                    function = Emetcon::GetConfig_IEDTime;
                    found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
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
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
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
            }
            else
            {
                nRet = NoConfigData;
            }

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

/*
    if( parse.isKeyValid("channels") )
    {

    }
*/
    if(parse.isKeyValid("multiplier"))
    {
        function = Emetcon::GetConfig_Multiplier;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        if( parse.isKeyValid("multchannel") )
        {
            if( parse.getiValue("multchannel") >= 1 &&
                parse.getiValue("multchannel") <= ChannelCount )
            {
                OutMessage->Buffer.BSt.Function += (parse.getiValue("multchannel") - 1) * MCT470_Memory_ChannelOffset;
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
        else if( parse.isKeyValid("dnp") )
        {

            if( parse.isKeyValid("start address") )
            {
                function = Emetcon::PutConfig_Raw;
                OutMessage->Buffer.BSt.Function = MCT470_FuncWrite_DNPReqTable;
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
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
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

    if(parse.isKeyValid("multiplier"))
    {
        unsigned long multbytes;
        double multiplier = parse.getdValue("multiplier");
        int numerator, denominator;

        function = Emetcon::PutConfig_Multiplier;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

        if( found )
        {
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
                errRet->setResultString("Multiplier too large - must be less than 50000");
                errRet->setStatus(NoMethod);
                retList.push_back( errRet );
                errRet = NULL;
            }
            if( multiplier < 0.0001 )
            {
                errRet->setResultString("Multiplier too small - must be at least 0.0001");
                errRet->setStatus(NoMethod);
                retList.push_back( errRet );
                errRet = NULL;
            }

            denominator = 10000;

            //  ex:  multiplier = 4.097, denominator = 10000
            //         result = 40,970 <--  suitable numerator
            //       multiplier = 689,   denominator = 10000
            //         result = 6,890,000  <--  unsuitable numerator, divide denominator by 10
            //       multiplier = 689,   denominator =  1000
            //         result = 689,000    <--  unsuitable numerator, divide denominator by 10
            //       multiplier = 689,   denominator =   100
            //         result = 68,900     <--  unsuitable numerator, divide denominator by 10
            //       multiplier = 689,   denominator =    10
            //         result = 6,890      <--  suitable numerator

            while( (multiplier * denominator) > 50000 )
            {
                denominator /= 10;
            }

            numerator = (int)(multiplier * denominator);

            OutMessage->Buffer.BSt.Message[0] = (numerator   >> 8) & 0xff;
            OutMessage->Buffer.BSt.Message[1] =  numerator         & 0xff;

            OutMessage->Buffer.BSt.Message[2] = (denominator >> 8) & 0xff;
            OutMessage->Buffer.BSt.Message[3] =  denominator       & 0xff;

            OutMessage->Buffer.BSt.Function += (parse.getiValue("multoffset") - 1) * CtiDeviceMCT470::MCT470_Memory_ChannelOffset;
        }
    }
    else
    {
        nRet = Inherited::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
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
        if( found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO) )
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

    return nRet;
}

int CtiDeviceMCT470::executePutConfigLoadProfileChannel(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTLoadProfileChannels);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTLoadProfileChannels )
        {
            long channel1, ratio1, kRatio1, channel2, ratio2, kRatio2, spid;

            MCTLoadProfileChannelsSPtr config = boost::static_pointer_cast< ConfigurationPart<MCTLoadProfileChannels> >(tempBasePtr);
            channel1 = config->getLongValueFromKey(ChannelConfig1);
            channel2 = config->getLongValueFromKey(ChannelConfig2);
            ratio1   = config->getLongValueFromKey(MeterRatio1);
            ratio2   = config->getLongValueFromKey(MeterRatio2);
            kRatio1  = config->getLongValueFromKey(KRatio1);
            kRatio2  = config->getLongValueFromKey(KRatio2);
            spid = CtiDeviceBase::getDynamicInfo(Keys::Key_MCT_AddressServiceProviderID);

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

            if( channel1   == std::numeric_limits<long>::min() || channel2 == std::numeric_limits<long>::min()
                || ratio1  == std::numeric_limits<long>::min() || ratio2   == std::numeric_limits<long>::min()
                || kRatio1 == std::numeric_limits<long>::min() || kRatio2  == std::numeric_limits<long>::min()
                ||    spid == std::numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                    OutMessage->Buffer.BSt.Function   = MCT470_FuncWrite_LoadProfileChannelsPos;
                    OutMessage->Buffer.BSt.Length     = MCT470_FuncWrite_LoadProfileChannelsLen;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0] = spid;
                    OutMessage->Buffer.BSt.Message[1] = 1;
                    OutMessage->Buffer.BSt.Message[2] = (channel1);
                    OutMessage->Buffer.BSt.Message[3] = (ratio1>>8);
                    OutMessage->Buffer.BSt.Message[4] = (ratio1);
                    OutMessage->Buffer.BSt.Message[5] = (kRatio1>>8);
                    OutMessage->Buffer.BSt.Message[6] = (kRatio1);
                    OutMessage->Buffer.BSt.Message[7] = 2;
                    OutMessage->Buffer.BSt.Message[8] = (channel2);
                    OutMessage->Buffer.BSt.Message[9] = (ratio2>>8);
                    OutMessage->Buffer.BSt.Message[10] =(ratio2);
                    OutMessage->Buffer.BSt.Message[11] =(kRatio2>>8);
                    OutMessage->Buffer.BSt.Message[12] =(kRatio2);

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Function   = MCT470_FuncRead_LoadProfileChannel12Pos;
                    OutMessage->Buffer.BSt.Length     = MCT470_FuncRead_LoadProfileChannel12Len;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Read;
                    OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                    OutMessage->Priority             += 1;//return to normal
                }
            }


            channel1 = config->getLongValueFromKey(ChannelConfig3);
            channel2 = config->getLongValueFromKey(ChannelConfig4);
            ratio1 =   config->getLongValueFromKey(MeterRatio3);
            ratio2 =   config->getLongValueFromKey(MeterRatio4);
            kRatio1 =  config->getLongValueFromKey(KRatio3);
            kRatio2 =  config->getLongValueFromKey(KRatio4);

            if(   channel1 == std::numeric_limits<long>::min() || channel2 == std::numeric_limits<long>::min()
                ||  ratio1 == std::numeric_limits<long>::min() ||   ratio2 == std::numeric_limits<long>::min()
                || kRatio1 == std::numeric_limits<long>::min() ||  kRatio2 == std::numeric_limits<long>::min()
                ||    spid == std::numeric_limits<long>::min() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - no or bad value stored **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                    OutMessage->Buffer.BSt.Function   = MCT470_FuncWrite_LoadProfileChannelsPos;
                    OutMessage->Buffer.BSt.Length     = MCT470_FuncWrite_LoadProfileChannelsLen;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0] = spid;
                    OutMessage->Buffer.BSt.Message[1] = 3;
                    OutMessage->Buffer.BSt.Message[2] = (channel1);
                    OutMessage->Buffer.BSt.Message[3] = (ratio1>>8);
                    OutMessage->Buffer.BSt.Message[4] = (ratio1);
                    OutMessage->Buffer.BSt.Message[5] = (kRatio1>>8);
                    OutMessage->Buffer.BSt.Message[6] = (kRatio1);
                    OutMessage->Buffer.BSt.Message[7] = 4;
                    OutMessage->Buffer.BSt.Message[8] = (channel2);
                    OutMessage->Buffer.BSt.Message[9] = (ratio2>>8);
                    OutMessage->Buffer.BSt.Message[10] =(ratio2);
                    OutMessage->Buffer.BSt.Message[11] =(kRatio2>>8);
                    OutMessage->Buffer.BSt.Message[12] =(kRatio2);

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Function   = MCT470_FuncRead_LoadProfileChannel34Pos;
                    OutMessage->Buffer.BSt.Length     = MCT470_FuncRead_LoadProfileChannel34Len;
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

    return NORMAL;
}

int CtiDeviceMCT470::executePutConfigRelays(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
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
                    OutMessage->Buffer.BSt.Function   = MCT470_FuncWrite_RelaysPos;
                    OutMessage->Buffer.BSt.Length     = MCT470_FuncWrite_RelaysLen;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0] = spid;
                    OutMessage->Buffer.BSt.Message[1] = relayATimer;
                    OutMessage->Buffer.BSt.Message[2] = relayBTimer;

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Function   = MCT470_Memory_RelayATimerPos;
                    OutMessage->Buffer.BSt.Length     = MCT470_Memory_RelayATimerLen + MCT470_Memory_RelayBTimerLen;
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

    return NORMAL;
}

int CtiDeviceMCT470::executePutConfigDemandLP(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,list< CtiMessage* >&vgList,list< CtiMessage* >&retList,list< OUTMESS* >   &outList)
{
    int nRet = NORMAL;
    long value;
    CtiConfigDeviceSPtr deviceConfig = getDeviceConfig();

    if( deviceConfig )
    {
        BaseSPtr tempBasePtr = deviceConfig->getConfigFromType(ConfigTypeMCTDemandLP);

        if( tempBasePtr && tempBasePtr->getType() == ConfigTypeMCTDemandLP )
        {
            long demand, loadProfile1, loadProfile2;

            MCTDemandLoadProfileSPtr config = boost::static_pointer_cast< ConfigurationPart<MCTDemandLoadProfile> >(tempBasePtr);
            demand = config->getLongValueFromKey(DemandInterval);
            loadProfile1 = config->getLongValueFromKey(LoadProfileInterval);
            loadProfile2 = config->getLongValueFromKey(LoadProfileInterval2);

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
                    OutMessage->Buffer.BSt.Function   = MCT470_FuncWrite_IntervalsPos;
                    OutMessage->Buffer.BSt.Length     = MCT470_FuncWrite_IntervalsLen;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0] = (demand);
                    OutMessage->Buffer.BSt.Message[1] = (loadProfile1);
                    OutMessage->Buffer.BSt.Message[2] = (loadProfile2);

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Function   = MCT470_Memory_IntervalsPos;
                    OutMessage->Buffer.BSt.Length     = MCT470_Memory_IntervalsLen;
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
                    OutMessage->Buffer.BSt.Function   = MCT470_FuncWrite_PrecannedTablePos;
                    OutMessage->Buffer.BSt.Length     = MCT470_FuncWrite_PrecannedTableLen;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0] = spid;
                    OutMessage->Buffer.BSt.Message[1] = tableReadInterval;
                    OutMessage->Buffer.BSt.Message[2] = meterNumber;
                    OutMessage->Buffer.BSt.Message[3] = tableType;

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Function   = MCT470_FuncRead_PrecannedTablePos;
                    OutMessage->Buffer.BSt.Length     = MCT470_FuncRead_PrecannedTableLen;
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

            if( !getOperation(Emetcon::PutConfig_Options, function, length, io) )
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
                    OutMessage->Buffer.BSt.Function   = function;
                    OutMessage->Buffer.BSt.Length     = length;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0] = (configuration);
                    OutMessage->Buffer.BSt.Message[1] = (event1mask);
                    OutMessage->Buffer.BSt.Message[2] = (event2mask);

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Function   = Memory_ConfigurationPos;
                    OutMessage->Buffer.BSt.Length     = Memory_ConfigurationLen;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                    OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Function   = MCT470_Memory_EventFlagsMaskPos;
                    OutMessage->Buffer.BSt.Length     = MCT470_Memory_EventFlagsMaskLen;
                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                    OutMessage->Priority             += 1;//return to normal
                }
            }

            if( !getOperation(Emetcon::PutConfig_TimeAdjustTolerance, function, length, io) )
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

int CtiDeviceMCT470::executePutConfigDNP(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList)
{
    //To anyone who needs to change this later, unfortunatelly a lot of the values here are used as "magic" numbers.
    //Unfortunatelly any changes will require changes to the code and really non-magic numbers are of very little use to us here.
    bool doRead = false;//Do the function crc read.
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
            CtiString collectionBinarya, collectionBinaryb, collectionAnalog, collectionAccumulator;
            CtiString binaryA, binaryB, analogA, analogB, accumulatorA, accumulatorB;

            //***** Configure the Collection A (real time read 1) data
            MCT_DNP_SPtr config = boost::static_pointer_cast< ConfigurationPart<MCT_DNP> >(tempBasePtr);
            collectionBinarya = config->getValueFromKey(DNPCollection1BinaryA);
            collectionBinaryb = config->getValueFromKey(DNPCollection1BinaryB);
            collectionAnalog = config->getValueFromKey(DNPCollection1Analog);
            collectionAccumulator = config->getValueFromKey(DNPCollection1Accumulator);

            if( collectionBinarya.empty() || collectionBinaryb.empty()
                || collectionAnalog.empty() || collectionAccumulator.empty() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Configuration not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                int tempCount, valCount = 0;
                getBytesFromString(collectionBinarya, buffer, BufferSize, valCount, 15, 2);
                tempCount = valCount;
                if( tempCount == 0 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Binary Collection A improperly set up **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }
                getBytesFromString(collectionBinaryb, buffer+(valCount*2), BufferSize-(valCount*2), valCount, 15-valCount, 2);
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
                    || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_RealTime1CRC) != crc8(buffer, 40) )
                {
                    OutMessage->Buffer.BSt.Function   = MCT470_FuncWrite_DNPReqTable;
                    OutMessage->Buffer.BSt.Length     = 14;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0] = (MCT470_DNP_MCTPoint_RealTimeBinary);//This is defined in the 470 doc.
                    OutMessage->Buffer.BSt.Message[1] = (0);//8 bit
                    OutMessage->Buffer.BSt.Message[2] = (buffer[1]);
                    OutMessage->Buffer.BSt.Message[3] = (buffer[3]);
                    OutMessage->Buffer.BSt.Message[4] = (buffer[5]);
                    OutMessage->Buffer.BSt.Message[5] = (buffer[7]);
                    OutMessage->Buffer.BSt.Message[6] = (buffer[9]);
                    OutMessage->Buffer.BSt.Message[7] = (buffer[11]);
                    OutMessage->Buffer.BSt.Message[8] = (buffer[13]);
                    OutMessage->Buffer.BSt.Message[9] = (buffer[15]);
                    OutMessage->Buffer.BSt.Message[10] = (buffer[17]);
                    OutMessage->Buffer.BSt.Message[11] = (buffer[19]);
                    OutMessage->Buffer.BSt.Message[12] = (buffer[21]);
                    OutMessage->Buffer.BSt.Message[13] = (buffer[23]);

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Length     = 5;
                    OutMessage->Buffer.BSt.Message[0] = (MCT470_DNP_MCTPoint_RealTimeBinary+12);//This is defined in the 470 doc.
                    OutMessage->Buffer.BSt.Message[1] = (0);//8 bit
                    OutMessage->Buffer.BSt.Message[2] = (buffer[25]);
                    OutMessage->Buffer.BSt.Message[3] = (buffer[27]);
                    OutMessage->Buffer.BSt.Message[4] = (buffer[29]);

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Length     = 8;
                    OutMessage->Buffer.BSt.Message[0] = (MCT470_DNP_MCTPoint_RealTimeAnalog);//This is defined in the 470 doc.
                    OutMessage->Buffer.BSt.Message[1] = (2);//16 bit
                    OutMessage->Buffer.BSt.Message[2] = (buffer[31]);
                    OutMessage->Buffer.BSt.Message[3] = (buffer[30]);
                    OutMessage->Buffer.BSt.Message[4] = (buffer[33]);
                    OutMessage->Buffer.BSt.Message[5] = (buffer[32]);
                    OutMessage->Buffer.BSt.Message[6] = (buffer[35]);
                    OutMessage->Buffer.BSt.Message[7] = (buffer[34]);
                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Length     = 6;
                    OutMessage->Buffer.BSt.Message[0] = (MCT470_DNP_MCTPoint_RealTimeAccumulator);//This is defined in the 470 doc.
                    OutMessage->Buffer.BSt.Message[1] = (2);//16 bit
                    OutMessage->Buffer.BSt.Message[2] = (buffer[37]);
                    OutMessage->Buffer.BSt.Message[3] = (buffer[36]);
                    OutMessage->Buffer.BSt.Message[4] = (buffer[39]);
                    OutMessage->Buffer.BSt.Message[5] = (buffer[38]);
                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                }
            }

            //***** Configure the collection B (real time read 2) data.
            collectionBinarya = config->getValueFromKey(DNPCollection2BinaryA);
            collectionBinaryb = config->getValueFromKey(DNPCollection2BinaryB);
            collectionAnalog = config->getValueFromKey(DNPCollection2Analog);
            collectionAccumulator = config->getValueFromKey(DNPCollection2Accumulator);

            if( collectionBinarya.empty() || collectionBinaryb.empty()
                || collectionAnalog.empty() || collectionAccumulator.empty() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Configuration not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                nRet = NoConfigData;
            }
            else
            {
                int tempCount, valCount = 0;
                getBytesFromString(collectionBinarya, buffer, BufferSize, valCount, 15, 2);
                tempCount = valCount;
                if( tempCount == 0 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - Binary Collection A improperly set up **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    nRet = NoConfigData;
                }
                getBytesFromString(collectionBinaryb, buffer+(valCount*2), BufferSize-(valCount*2), valCount, 15-valCount, 2);
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
                    OutMessage->Buffer.BSt.Function   = MCT470_FuncWrite_DNPReqTable;
                    OutMessage->Buffer.BSt.Length     = 14;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0] = (MCT470_DNP_MCTPoint_RealTimeBinary+15);//This is defined in the 470 doc.
                    OutMessage->Buffer.BSt.Message[1] = (0);//8 bit
                    OutMessage->Buffer.BSt.Message[2] = (buffer[1]);
                    OutMessage->Buffer.BSt.Message[3] = (buffer[3]);
                    OutMessage->Buffer.BSt.Message[4] = (buffer[5]);
                    OutMessage->Buffer.BSt.Message[5] = (buffer[7]);
                    OutMessage->Buffer.BSt.Message[6] = (buffer[9]);
                    OutMessage->Buffer.BSt.Message[7] = (buffer[11]);
                    OutMessage->Buffer.BSt.Message[8] = (buffer[13]);
                    OutMessage->Buffer.BSt.Message[9] = (buffer[15]);
                    OutMessage->Buffer.BSt.Message[10] = (buffer[17]);
                    OutMessage->Buffer.BSt.Message[11] = (buffer[19]);
                    OutMessage->Buffer.BSt.Message[12] = (buffer[21]);
                    OutMessage->Buffer.BSt.Message[13] = (buffer[23]);

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Length     = 5;
                    OutMessage->Buffer.BSt.Message[0] = (MCT470_DNP_MCTPoint_RealTimeBinary+27);//This is defined in the 470 doc.
                    OutMessage->Buffer.BSt.Message[1] = (0);//8 bit
                    OutMessage->Buffer.BSt.Message[2] = (buffer[25]);
                    OutMessage->Buffer.BSt.Message[3] = (buffer[27]);
                    OutMessage->Buffer.BSt.Message[4] = (buffer[29]);

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Length     = 8;
                    OutMessage->Buffer.BSt.Message[0] = (MCT470_DNP_MCTPoint_RealTimeAnalog+3);//This is defined in the 470 doc.
                    OutMessage->Buffer.BSt.Message[1] = (2);//16 bit
                    OutMessage->Buffer.BSt.Message[2] = (buffer[31]);
                    OutMessage->Buffer.BSt.Message[3] = (buffer[30]);
                    OutMessage->Buffer.BSt.Message[4] = (buffer[33]);
                    OutMessage->Buffer.BSt.Message[5] = (buffer[32]);
                    OutMessage->Buffer.BSt.Message[6] = (buffer[35]);
                    OutMessage->Buffer.BSt.Message[7] = (buffer[34]);
                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    OutMessage->Buffer.BSt.Length     = 6;
                    OutMessage->Buffer.BSt.Message[0] = (MCT470_DNP_MCTPoint_RealTimeAccumulator+2);//This is defined in the 470 doc.
                    OutMessage->Buffer.BSt.Message[1] = (2);//16 bit
                    OutMessage->Buffer.BSt.Message[2] = (buffer[37]);
                    OutMessage->Buffer.BSt.Message[3] = (buffer[36]);
                    OutMessage->Buffer.BSt.Message[4] = (buffer[39]);
                    OutMessage->Buffer.BSt.Message[5] = (buffer[38]);
                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
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
                    OutMessage->Buffer.BSt.Function   = MCT470_FuncWrite_DNPReqTable;
                    OutMessage->Buffer.BSt.Length     = 14;
                    OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0] = (MCT470_DNP_MCTPoint_PreCannedBinary);//This is defined in the 470 doc.
                    OutMessage->Buffer.BSt.Message[1] = (0);//8 bit
                    OutMessage->Buffer.BSt.Message[2] = (buffer[1]);
                    OutMessage->Buffer.BSt.Message[3] = (buffer[3]);
                    OutMessage->Buffer.BSt.Message[4] = (buffer[5]);
                    OutMessage->Buffer.BSt.Message[5] = (buffer[7]);
                    OutMessage->Buffer.BSt.Message[6] = (buffer[9]);
                    OutMessage->Buffer.BSt.Message[7] = (buffer[11]);
                    OutMessage->Buffer.BSt.Message[8] = (buffer[13]);
                    OutMessage->Buffer.BSt.Message[9] = (buffer[15]);
                    OutMessage->Buffer.BSt.Message[10] = (buffer[17]);
                    OutMessage->Buffer.BSt.Message[11] = (buffer[19]);
                    OutMessage->Buffer.BSt.Message[12] = (buffer[21]);
                    OutMessage->Buffer.BSt.Message[13] = (buffer[23]);

                    outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                }
            }

            //***** Configure the analog first set (precanned analog 1 and 2)
            analogA = config->getValueFromKey(DNPAnalog1);
            analogB = config->getValueFromKey(DNPAnalog2);
            nRet |= sendDNPConfigMessages(AnalogStartValue, outList, OutMessage, analogA, analogB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC1, force);
            analogA = config->getValueFromKey(DNPAnalog3);
            analogB = config->getValueFromKey(DNPAnalog4);
            nRet |= sendDNPConfigMessages(AnalogStartValue+12, outList, OutMessage, analogA, analogB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC2, force);
            analogA = config->getValueFromKey(DNPAnalog5);
            analogB = config->getValueFromKey(DNPAnalog6);
            nRet |= sendDNPConfigMessages(AnalogStartValue+24, outList, OutMessage, analogA, analogB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC3, force);
            analogA = config->getValueFromKey(DNPAnalog7);
            analogB = config->getValueFromKey(DNPAnalog8);
            nRet |= sendDNPConfigMessages(AnalogStartValue+36, outList, OutMessage, analogA, analogB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC4, force);
            analogA = config->getValueFromKey(DNPAnalog9);
            analogB = config->getValueFromKey(DNPAnalog10);
            nRet |= sendDNPConfigMessages(AnalogStartValue+48, outList, OutMessage, analogA, analogB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC5, force);

            accumulatorA = config->getValueFromKey(DNPAccumulator1);
            accumulatorB = config->getValueFromKey(DNPAccumulator2);
            nRet |= sendDNPConfigMessages(AccumulatorStartValue, outList, OutMessage, accumulatorA, accumulatorB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC1, force);
            accumulatorA = config->getValueFromKey(DNPAccumulator3);
            accumulatorB = config->getValueFromKey(DNPAccumulator4);
            nRet |= sendDNPConfigMessages(AccumulatorStartValue+12, outList, OutMessage, accumulatorA, accumulatorB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC2, force);
            accumulatorA = config->getValueFromKey(DNPAccumulator5);
            accumulatorB = config->getValueFromKey(DNPAccumulator6);
            nRet |= sendDNPConfigMessages(AccumulatorStartValue+24, outList, OutMessage, accumulatorA, accumulatorB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC3, force);
            accumulatorA = config->getValueFromKey(DNPAccumulator7);
            accumulatorB = config->getValueFromKey(DNPAccumulator8);
            nRet |= sendDNPConfigMessages(AccumulatorStartValue+36, outList, OutMessage, accumulatorA, accumulatorB, CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC4, force);

            //Read out the CRC
            strncpy(OutMessage->Request.CommandStr, "getvalue ied dnp crc", COMMAND_STR_SIZE );
            OutMessage->Priority--;
            OutMessage->Sequence = Emetcon::GetValue_IED;
            OutMessage->Buffer.BSt.Function = MCT470_FuncRead_IED_CRCPos;
            OutMessage->Buffer.BSt.Length = MCT470_FuncRead_IED_CRCLen;
            OutMessage->Buffer.BSt.IO = Emetcon::IO_Function_Read;
            outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
            OutMessage->Priority++;
        }
        else
            nRet = NoConfigData;
    }
    else
        nRet = NoConfigData;

    return nRet;
}

int CtiDeviceMCT470::sendDNPConfigMessages(int startMCTID, list< OUTMESS * > &outList, OUTMESS *&OutMessage, string &dataA, string &dataB, CtiTableDynamicPaoInfo::Keys key, bool force)
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
                OutMessage->Buffer.BSt.Function   = MCT470_FuncWrite_DNPReqTable;
                OutMessage->Buffer.BSt.Length     = 14;
                OutMessage->Buffer.BSt.IO         = Emetcon::IO_Function_Write;
                OutMessage->Buffer.BSt.Message[0] = (startMCTID);//This is defined in the 470 doc.
                OutMessage->Buffer.BSt.Message[1] = (0);//8 bit
                OutMessage->Buffer.BSt.Message[2] = (buffer[1]);
                OutMessage->Buffer.BSt.Message[3] = (buffer[3]);
                OutMessage->Buffer.BSt.Message[4] = (buffer[5]);
                OutMessage->Buffer.BSt.Message[5] = (buffer[7]);
                OutMessage->Buffer.BSt.Message[6] = (buffer[9]);
                OutMessage->Buffer.BSt.Message[7] = (buffer[11]);
                OutMessage->Buffer.BSt.Message[8] = (buffer[13]);
                OutMessage->Buffer.BSt.Message[9] = (buffer[15]);
                OutMessage->Buffer.BSt.Message[10] = (buffer[17]);
                OutMessage->Buffer.BSt.Message[11] = (buffer[19]);
                OutMessage->Buffer.BSt.Message[12] = (buffer[21]);
                OutMessage->Buffer.BSt.Message[13] = (buffer[23]);

                outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
            }
            else
            {
                OutMessage->Buffer.BSt.Function   = MCT470_FuncWrite_DNPReqTable;
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
    point_info_t  pi;

    CtiPointSPtr          pPoint;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
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

            point_info_t pi = getData(DSt->Message + (i * 3), 3, ValueType_Accumulator);

            // handle accumulator data here
            if( pPoint )
            {
                // 24 bit pulse value
                pi.value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(pi.value);

                resultString = "";

                if( pi.quality != InvalidQuality )
                {
                    resultString += getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

                    if( pData = makePointDataMsg(pPoint, pi, resultString) )
                    {
                        pData->setTime(CtiTime::now() - (CtiTime::now().seconds() % 300) );

                        ReturnMsg->PointData().push_back(pData);
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


INT CtiDeviceMCT470::decodeGetValueDemand(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int          status = NORMAL, i;
    point_info_t pi;
    string    resultString, pointString, stateName;

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

        for( i = 0; i < 8; i++ )
        {
            if( pPoint = getDevicePointOffsetTypeEqual(i + 1, StatusPointType) )
            {
                pi.value   = (DSt->Message[0] >> i) & 0x01;
                pi.quality = NormalQuality;

                stateName = ResolveStateName(pPoint->getStateGroupID(), pi.value);

                if( !stateName.empty() )
                {
                    resultString += getName() + " / " + pPoint->getName() + ":" + stateName;
                }
                else
                {
                    resultString += getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value);
                }

                if(pData = makePointDataMsg(pPoint, pi, resultString))
                {
                    ReturnMsg->PointData().push_back(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
        }

        for(int i = 0; i < 5; i++)
        {
            if( i == 4 )
            {
                pi = getData(DSt->Message + (i * 2) + 1, 2, ValueType_Raw);

                pPoint = getDevicePointOffsetTypeEqual(PointOffset_Accumulator_Powerfail, PulseAccumulatorPointType);
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
                CtiTime pointTime;

                pi.value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(pi.value);

                pointString  = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());
                //  if the point exists, we don't need the "POINT UNDEFINED" guys hanging around
                resultString = "";

                if( pData = makePointDataMsg(pPoint, pi, pointString) )
                {
                    if( i != 4 )
                    {
                        pointTime -= pointTime.seconds() % getDemandInterval();
                        pData->setTime( pointTime );
                    }

                    ReturnMsg->PointData().push_back(pData);
                    pData = NULL;  // We just put it on the list...
                }
            }
            else if( i == 4 )
            {
                resultString += getName() + " / Blink Counter = " + CtiNumStr(pi.value) + "\n";
            }
            else if( i == 0 )
            {
                resultString += getName() + " / Channel 1 Demand = " + CtiNumStr(pi.value) + "  --  POINT UNDEFINED IN DB\n";
            }
        }

        ReturnMsg->setResultString(resultString);
        decrementGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList, getGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection ));
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetValuePeakDemand(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int          status = NORMAL, base_offset, point_offset;
    point_info_t pi, pi_time;
    string    resultString, pointString, stateName;
    CtiTime       pointTime;

    INT ErrReturn = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    CtiPointSPtr     pPoint;
    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Peak Demand Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

        point_offset = base_offset + PointOffset_PeakOffset;

        pi      = getData(DSt->Message + 0, 2, ValueType_KW);
        pi_time = getData(DSt->Message + 2, 4, ValueType_Raw);

        //  turn raw pulses into a demand reading
        pi.value *= double(3600 / getDemandInterval());

        pointTime = CtiTime(pi_time.value);

        //  we can do a rudimentary frozen peak time check here with the dynamicInfo stuff - we can't
        //    do much more, since we don't get the freeze count back with the frozen demand, so we have to
        //    take the device's word for it

        if( pPoint = getDevicePointOffsetTypeEqual(point_offset, DemandAccumulatorPointType) )
        {
            pi.value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(pi.value);

            pointString  = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());
            if( pi_time.value > DawnOfTime )
            {
                pointString += " @ " + pointTime.asString();
            }
            else
            {
                pointString += " @ (invalid time)";
            }

            if( pData = makePointDataMsg(pPoint, pi, pointString) )
            {
                pointTime -= pointTime.seconds() % getDemandInterval();
                pData->setTime(pointTime);

                ReturnMsg->PointData().push_back(pData);
                pData = 0;  // We just put it on the list...
            }
        }
        else
        {
            resultString += getName() + " / Channel " + CtiNumStr(base_offset) + " Max Demand = " + CtiNumStr(pi.value) + " @ " + pointTime.asString() + "  --  POINT UNDEFINED IN DB\n";
        }

        pi      = getData(DSt->Message + 6, 2, ValueType_KW);
        pi_time = getData(DSt->Message + 8, 4, ValueType_Raw);

        //  turn raw pulses into a demand reading
        pi.value *= double(3600 / getDemandInterval());

        pointTime = CtiTime(pi_time.value);

        //  use the max point for the computation, if we've got it
        if( pPoint )
        {
            pi.value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(pi.value);

            pointString  = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(pi.value, boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());
            if( pi_time.value > DawnOfTime )
            {
                pointString += " @ " + pointTime.asString();
            }
            else
            {
                pointString += " @ (invalid time)";
            }

            //  we don't actually send a pointdata message for the min point, so we tack the results onto the result string instead
            resultString += pointString;
        }
        else
        {
            resultString += getName() + " / Channel " + CtiNumStr(base_offset) + " Min Demand = " + CtiNumStr(pi.value) + " @ " + pointTime.asString() + "  --  POINT UNDEFINED IN DB\n";
        }

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetValueIED(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int             status = NORMAL,
                    frozen = 0,
                    offset = 0,
                    rate   = 0;
    point_info_t    pi;
    string       point_string, resultString;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
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

        if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) >= MCT470_SspecRev_IEDErrorPadding )
        {
            for( int i = 0; i < ied_data_end; i++)
            {
                if( (i && DSt->Message[i] != DSt->Message[i-1]) ||
                    DSt->Message[i] != 0xFF &&
                    DSt->Message[i] != 0xFE &&
                    DSt->Message[i] != 0xFD )
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
        if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) < MCT470_SspecRev_IEDZeroWriteMin
            && (CtiTime::now().seconds() > (_iedTime.seconds() + MCT470_MaxIEDReadAge)) )
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

                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_TotalKW, AnalogPointType);
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_TotalKM, AnalogPointType);
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

                if(kw = getDevicePointOffsetTypeEqual(PointOffset_TotalKW, AnalogPointType))
                {
                    pi.value = boost::static_pointer_cast<CtiPointNumeric>(kw)->computeValueForUOM(pi.value);

                    point_string = getName() + " / " + kw->getName() + " = " + CtiNumStr(pi.value, boost::static_pointer_cast<CtiPointNumeric>(kw)->getPointUnits().getDecimalPlaces());

                    ReturnMsg->PointData().push_back(makePointDataMsg(kw, pi, point_string));
                }
                else
                {
                    resultString += getName() + " / current KW = " + CtiNumStr(pi.value) + "\n";
                }

                //  get selectable metric (kM, kVAR, etc)
                pi = getData(DSt->Message + 3, 3, ValueType_IED);

                if(km = getDevicePointOffsetTypeEqual(PointOffset_TotalKM, AnalogPointType))
                {
                    pi.value = boost::static_pointer_cast<CtiPointNumeric>(km)->computeValueForUOM(pi.value);

                    point_string = getName() + " / " + km->getName() + " = " + CtiNumStr(pi.value, boost::static_pointer_cast<CtiPointNumeric>(km)->getPointUnits().getDecimalPlaces());

                    ReturnMsg->PointData().push_back(makePointDataMsg(km, pi, point_string));
                }
                else
                {
                    resultString += getName() + " / current KM = " + CtiNumStr(pi.value) + "\n";
                }

                //  S4-specific - get voltage
                pi = getData(DSt->Message + 6, 2, ValueType_IED);
                pi.value /= 100.0;

                if(volts = getDevicePointOffsetTypeEqual(PointOffset_VoltsPhaseA, AnalogPointType))
                {
                    send_outages = false;

                    pi.value  = boost::static_pointer_cast<CtiPointNumeric>(volts)->computeValueForUOM(pi.value);

                    point_string = getName() + " / " + volts->getName() + " = " + CtiNumStr(pi.value, boost::static_pointer_cast<CtiPointNumeric>(volts)->getPointUnits().getDecimalPlaces());

                    ReturnMsg->PointData().push_back(makePointDataMsg(volts, pi, point_string));
                }
                //  don't send the point if it's not defined - this is a hack to allow the S4 and Alpha decodes to
                //    both happen (until we have the configs to tell us which one to use)
                /*else
                {
                    resultString += getName() + " / Phase A Volts = " + CtiNumStr(pi.value) + "\n";
                }*/

                pi = getData(DSt->Message + 8, 2, ValueType_IED);
                pi.value /= 100.0;

                if(volts = getDevicePointOffsetTypeEqual(PointOffset_VoltsPhaseB, AnalogPointType))
                {
                    send_outages = false;

                    pi.value  = boost::static_pointer_cast<CtiPointNumeric>(volts)->computeValueForUOM(pi.value);

                    point_string = getName() + " / " + volts->getName() + " = " + CtiNumStr(pi.value, boost::static_pointer_cast<CtiPointNumeric>(volts)->getPointUnits().getDecimalPlaces());

                    ReturnMsg->PointData().push_back(makePointDataMsg(volts, pi, point_string));
                }
                //  don't send the point if it's not defined - this is a hack to allow the S4 and Alpha decodes to
                //    both happen (until we have the configs to tell us which one to use)
                /*else
                {
                    resultString += getName() + " / Phase B Volts = " + CtiNumStr(pi.value) + "\n";
                }*/

                pi = getData(DSt->Message + 10, 2, ValueType_IED);
                pi.value /= 100.0;

                if(volts = getDevicePointOffsetTypeEqual(PointOffset_VoltsPhaseC, AnalogPointType))
                {
                    send_outages = false;

                    pi.value  = boost::static_pointer_cast<CtiPointNumeric>(volts)->computeValueForUOM(pi.value);

                    point_string = getName() + " / " + volts->getName() + " = " + CtiNumStr(pi.value, boost::static_pointer_cast<CtiPointNumeric>(volts)->getPointUnits().getDecimalPlaces());

                    ReturnMsg->PointData().push_back(makePointDataMsg(volts, pi, point_string));
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

                    resultString += getName() + " / outage count: " + CtiNumStr(pi.value) + "\n";
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

                if(kwh = getDevicePointOffsetTypeEqual(PointOffset_TotalKWH, AnalogPointType))
                {
                    pi.value = boost::static_pointer_cast<CtiPointNumeric>(kwh)->computeValueForUOM(pi.value);

                    point_string = getName() + " / " + kwh->getName() + " = " + CtiNumStr(pi.value, boost::static_pointer_cast<CtiPointNumeric>(kwh)->getPointUnits().getDecimalPlaces());

                    ReturnMsg->PointData().push_back(makePointDataMsg(kwh, pi, point_string));
                }
                else
                {
                    resultString += getName() + " / KWH total = " + CtiNumStr(pi.value) + "\n";
                }

                pi = getData(DSt->Message + 5, 5, ValueType_IED);

                if(kmh = getDevicePointOffsetTypeEqual(PointOffset_TotalKMH, AnalogPointType))
                {
                    pi.value = boost::static_pointer_cast<CtiPointNumeric>(kmh)->computeValueForUOM(pi.value);

                    point_string = getName() + " / " + kmh->getName() + " = " + CtiNumStr(pi.value, boost::static_pointer_cast<CtiPointNumeric>(kmh)->getPointUnits().getDecimalPlaces());

                    ReturnMsg->PointData().push_back(makePointDataMsg(kmh, pi, point_string));
                }
                else
                {
                    resultString += getName() + " / KMH total = " + CtiNumStr(pi.value) + "\n";
                }

                pi = getData(DSt->Message + 10, 2, ValueType_IED);

                resultString += getName() + " / Average power factor since last freeze = " + CtiNumStr(pi.value) + "\n";
            }
        }
        else if( parse.isKeyValid("ied_dnp") )
        {
            int i = 0;
            CtiPointSPtr tempPoint;
            int status = DSt->Message[12];//Applies only to precanned table reads.
            if( (i = parse.getiValue("collectionnumber")) != INT_MIN )
            {
                decodeDNPRealTimeRead(DSt->Message, i, resultString, ReturnMsg, InMessage);
            }
            else if( (i = parse.getiValue("analognumber")) != INT_MIN )
            {
                if( status == 0 )
                {
                    for( int byte = 0; byte < 6; byte++ )
                    {
                        pi = getData(DSt->Message + byte*2, 2, ValueType_Raw);
                        if( !status && (tempPoint = getDevicePointOffsetTypeEqual(PointOffset_DNPAnalog_Precanned1+byte+(i-1)*6, AnalogPointType)))
                        {
                            pi.value = boost::static_pointer_cast<CtiPointNumeric>(tempPoint)->computeValueForUOM(pi.value);
    
                            point_string = getName() + " / " + tempPoint->getName() + " = " + CtiNumStr(pi.value, boost::static_pointer_cast<CtiPointNumeric>(tempPoint)->getPointUnits().getDecimalPlaces());
    
                            ReturnMsg->PointData().push_back(makePointDataMsg(tempPoint, pi, point_string));
                        }
                        else
                        {
                            resultString += getName() + " / Analog point " + CtiNumStr(PointOffset_DNPAnalog_Precanned1+byte+(i-1)*6) + " = " + CtiNumStr(pi.value) + "\n";
                        }
                    }
                }
                else
                {
                    resultString += "DNP status returned " + CtiNumStr(status) + ": " + resolveDNPStatus(status);
                    for( int byte = 0; byte < 6; byte++ )
                    {
                        insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_DNPAnalog_Precanned1+byte+(i-1)*6, AnalogPointType);
                    }
                }
            }
            else if( (i = parse.getiValue("accumulatornumber")) != INT_MIN )
            {
                if( status == 0 )
                {
                    for( int byte = 0; byte < 6; byte++ )
                    {
                        pi = getData(DSt->Message + byte*2, 2, ValueType_Raw);
                        if( !status && (tempPoint = getDevicePointOffsetTypeEqual(PointOffset_DNPCounter_Precanned1+byte+(i-1)*6, PulseAccumulatorPointType)))
                        {
                            pi.value = boost::static_pointer_cast<CtiPointNumeric>(tempPoint)->computeValueForUOM(pi.value);
    
                            point_string = getName() + " / " + tempPoint->getName() + " = " + CtiNumStr(pi.value, boost::static_pointer_cast<CtiPointNumeric>(tempPoint)->getPointUnits().getDecimalPlaces());
    
                            ReturnMsg->PointData().push_back(makePointDataMsg(tempPoint, pi, point_string));
                        }
                        else
                        {
                            resultString += getName() + " / Pulse Accumulator point " + CtiNumStr(PointOffset_DNPCounter_Precanned1+byte+(i-1)*6) + " = " + CtiNumStr(pi.value) + "\n";
                        }
                    }
                }
                else
                {
                    resultString += "DNP status returned " + CtiNumStr(status) + ": " + resolveDNPStatus(status);
                    for( int byte = 0; byte < 6; byte++ )
                    {
                        insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_DNPCounter_Precanned1+byte+(i-1)*6, AnalogPointType);
                    }
                }
            }
            else if( (i = parse.getiValue("statusnumber")) != INT_MIN )
            {
                if( status == 0 )
                {
                    pi.quality = NormalQuality;
                    for( int byte = 0; byte < 12; byte++ )
                    {
                        for( int bit = 0; bit < 8; bit++ )
                        {
                            pi.value = (DSt->Message[byte] >> bit) & 0x01;
                            if( !status && (tempPoint = getDevicePointOffsetTypeEqual(PointOffset_DNPStatus_PrecannedStart+byte*8+bit, StatusPointType)))
                            {
                                point_string = getName() + " / " + tempPoint->getName() + " = " + CtiNumStr(pi.value);
    
                                ReturnMsg->PointData().push_back(makePointDataMsg(tempPoint, pi, point_string));
                            }
                        }
    
                        resultString += getName() + " / Binary Data Byte " + CtiNumStr(byte) + " = " + CtiNumStr(DSt->Message[byte]) + "\n";
                    }
                }
                else
                {
                    resultString += "DNP status returned " + CtiNumStr(status) + ": " + resolveDNPStatus(status);
                    for( int byte = 0; byte < 12; byte++ )
                    {
                        for( int bit = 0; bit < 8; bit++ )
                        {
                            insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_DNPStatus_PrecannedStart+byte*8+bit, StatusPointType);
                        }
                    }
                }
            }
            else if( parse.isKeyValid("dnp_crc") )
            {
                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_RealTime1CRC, InMessage->Buffer.DSt.Message[0]);
                resultString += "CRC's Returned: " + CtiNumStr(InMessage->Buffer.DSt.Message[0]);

                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_RealTime2CRC, InMessage->Buffer.DSt.Message[1]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[1]);

                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_BinaryCRC, InMessage->Buffer.DSt.Message[2]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[2]);

                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC1, InMessage->Buffer.DSt.Message[3]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[3]);

                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC2, InMessage->Buffer.DSt.Message[4]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[4]);

                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC3, InMessage->Buffer.DSt.Message[5]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[5]);

                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC4, InMessage->Buffer.DSt.Message[6]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[6]);

                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC5, InMessage->Buffer.DSt.Message[7]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[7]);

                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC1, InMessage->Buffer.DSt.Message[8]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[8]);

                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC2, InMessage->Buffer.DSt.Message[9]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[9]);

                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC3, InMessage->Buffer.DSt.Message[10]);
                resultString += ", " + CtiNumStr(InMessage->Buffer.DSt.Message[10]);

                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC4, InMessage->Buffer.DSt.Message[11]);
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
                point_info_t time_info;
                CtiTime peak_time;

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

                pi = getData(DSt->Message, 5, ValueType_Raw);

                if(kwh = getDevicePointOffsetTypeEqual(offset + rate * 2 + 1, AnalogPointType))
                {
                    pi.value = boost::static_pointer_cast<CtiPointNumeric>(kwh)->computeValueForUOM(pi.value);

                    point_string = getName() + " / " + kwh->getName() + " = " + CtiNumStr(pi.value, boost::static_pointer_cast<CtiPointNumeric>(kwh)->getPointUnits().getDecimalPlaces());

                    ReturnMsg->PointData().push_back(makePointDataMsg(kwh, pi, point_string));
                }
                else
                {
                    resultString += getName() + " / KWH rate " + (char)('A' + rate) + " total = " + CtiNumStr(pi.value) + "\n";
                }

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
                time_info = getData(DSt->Message + 8, 4, ValueType_Raw);
                peak_time = CtiTime((unsigned long)time_info.value + timezone_offset);

                if(kw = getDevicePointOffsetTypeEqual(offset + rate * 2, AnalogPointType))
                {
                    CtiPointDataMsg *peak_msg;

                    pi.value = boost::static_pointer_cast<CtiPointNumeric>(kw)->computeValueForUOM(pi.value);

                    point_string  = getName() + " / " + kw->getName() + " = " + CtiNumStr(pi.value, boost::static_pointer_cast<CtiPointNumeric>(kw)->getPointUnits().getDecimalPlaces());
                    point_string += " @ " + peak_time.asString() + "\n";

                    peak_msg = makePointDataMsg(kw, pi, point_string);
                    peak_msg->setTime(peak_time);

                    ReturnMsg->PointData().push_back(peak_msg);
                }
                else
                {
                    resultString += getName() + " / KW rate " + (char)('A' + rate) + " peak = " + CtiNumStr(pi.value);

                    resultString += " @ " + peak_time.asString() + "\n";
                }
            }
        }

        ReturnMsg->setResultString(resultString);
        decrementGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList, getGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection) );
    }

    return status;
}


INT CtiDeviceMCT470::decodeGetConfigIED(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    int             status = NORMAL,
                    frozen = 0,
                    offset = 0,
                    rate   = 0;
    point_info_t    pi;
    PointQuality_t  quality;
    string       point_string, resultString;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

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

        //  should we archive non-frozen points?

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

                point_info_t pi_time  = getData(DSt->Message, 4, ValueType_Raw);
                CtiTime      ied_time = CtiTime((unsigned long)pi_time.value + timezone_offset);

                _iedTime = ied_time;

                resultString += getName() + " / current time: " + ied_time.asString() + "\n";

                resultString += getName() + " / current TOU rate: " + string(1, 'A' + (DSt->Message[4] & 0x07) - 1).c_str() + "\n";

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

                pi = getData(DSt->Message + 5, 2, ValueType_Raw);

                resultString += getName() + " / demand reset count: " + CtiNumStr((int)pi.value) + "\n";

                pi_time  = getData(DSt->Message + 7, 4, ValueType_Raw);
                ied_time = CtiTime((unsigned long)pi_time.value + timezone_offset);

                resultString += getName() + " / time of last reset: " + ied_time.asString() + "\n";

                pi = getData(DSt->Message + 11, 2, ValueType_Raw);

                resultString += getName() + " / outage count: " + CtiNumStr((int)pi.value) + "\n";

                break;
            }

            case Emetcon::GetConfig_IEDDNP:
            {
                int mctPoint = DSt->Message[0];
                for( int i = 0; i < 6; i++ )
                {
                    pi = getData(DSt->Message + 1 + 2*i, 2, ValueType_Raw);
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
        unsigned long tmpTime;
        CtiTime lpTime;

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

        tmpTime = DSt->Message[0] << 24 |
                  DSt->Message[1] << 16 |
                  DSt->Message[2] <<  8 |
                  DSt->Message[3];

        lpTime = CtiTime(tmpTime);

        resultString += "Current Interval Time: " + lpTime.asString() + "\n";
        resultString += "Current Interval Pointer: " + CtiNumStr(DSt->Message[4]) + string("\n");

        resultString += "\n";

        resultString += getName() + " / Load Profile Channel " + CtiNumStr(lp_channel + 1) + string(" Status:\n");

        tmpTime = DSt->Message[5] << 24 |
                  DSt->Message[6] << 16 |
                  DSt->Message[7] <<  8 |
                  DSt->Message[8];

        lpTime = CtiTime(tmpTime);

        resultString += "Current Interval Time: " + lpTime.asString() + "\n";
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
        unsigned long tmpTime;
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

        int dnpStatus =     DSt->Message[0];
        int mctPoint =      DSt->Message[1];
        int dnpPoint =      DSt->Message[2] << 8 |
                            DSt->Message[3];
        int commandStatus = DSt->Message[4];
        tmpTime =           DSt->Message[5]  << 24 |
                            DSt->Message[6]  << 16 |
                            DSt->Message[7]  <<  8 |
                            DSt->Message[8];

        CtiTime      ied_time = CtiTime(tmpTime + rwEpoch);

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
        resultString += "Last Successful Read: " + ied_time.asString() + "(" + CtiNumStr(tmpTime) + ")" + "\n";

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

            if( DSt->Message[i] & 0x03 )
            {
                if     ( (DSt->Message[i] & 0x03) == 1 ) { result_string += "Electronic meter ";      dynamic_info += "1"; }
                else if( (DSt->Message[i] & 0x03) == 2 ) { result_string += "2-wire KYZ (form A) ";   dynamic_info += "2"; }
                else if( (DSt->Message[i] & 0x03) == 3 ) { result_string += "3-wire KYZ (form C) ";   dynamic_info += "3"; }

                result_string += "channel " + CtiNumStr((DSt->Message[i] >> 2) & 0x0f);
                dynamic_info  += CtiNumStr((DSt->Message[i] >> 2) & 0x0f).hex();

                result_string += " load profile interval #";
                result_string += (DSt->Message[i] & 0x40)?"1":"0";
                dynamic_info  += (DSt->Message[i] & 0x40)?"1":"0";

                result_string += "\n";
            }
            else
            {
                result_string += "Channel not used\n";
                dynamic_info  += "000";
            }
        }

        if( getMCTDebugLevel(MCTDebug_DynamicInfo) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - device \"" << getName() << "\" LP config decode - \"" << dynamic_info << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

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
        int  ssp;
        char rev;
        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi


        ssp  = InMessage->Buffer.DSt.Message[0];
        ssp |= InMessage->Buffer.DSt.Message[4] << 8;

        rev  = InMessage->Buffer.DSt.Message[1];  //  1 = A, 2 = B, etc...  > 26 is undefined

        //  set the dynamic info for use later
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec,         (long)ssp);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, (long)rev);

        if( rev && rev <= 26 )
        {
            rev += 'A' - 1;

            sspec = string("\nSoftware Specification " + CtiNumStr(ssp).toString() + "  Rom Revision " + string(1, rev) + "\n");
        }
        else
        {
            sspec = "\nSoftware Specification " + CtiNumStr(ssp).toString() + "  Rom Revision " + CtiNumStr(rev).toString() + " (development)\n";
        }

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

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


//I hate to do this, we must ensure no one ever passes a null buffer into here...
void CtiDeviceMCT470::decodeDNPRealTimeRead(BYTE *buffer, int readNumber, string &resultString, CtiReturnMsg *ReturnMsg, INMESS *InMessage)
{
    point_info_t   pi;
    CtiPointSPtr   tempPoint;
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

        pi = getData(buffer, 1, ValueType_Raw);//Gets pi built up properly...
        for( int i = 0; i < 7; i++ )//only 7 in the first byte
        {
            if( !errorFlagSet )
            {
                if( (tempPoint = getDevicePointOffsetTypeEqual(binaryoffset+i, StatusPointType)))
                {
                    pi.value = (buffer[0] >> (i+1)) & 0x01;
                    point_string = getName() + " / " + tempPoint->getName() + " = " + CtiNumStr(pi.value);
    
                    ReturnMsg->PointData().push_back(makePointDataMsg(tempPoint, pi, point_string));
                }
                else
                {
                    resultString += getName() + " / Status point " + CtiNumStr(binaryoffset+i) + " = " + CtiNumStr(pi.value) + "\n";
                }
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
                if( (tempPoint = getDevicePointOffsetTypeEqual(binaryoffset+i+7, StatusPointType)))
                {
                    pi.value = (buffer[1] >> i) & 0x01;
                    point_string = getName() + " / " + tempPoint->getName() + " = " + CtiNumStr(pi.value);
    
                    ReturnMsg->PointData().push_back(makePointDataMsg(tempPoint, pi, point_string));
                }
                else
                {
                    resultString += getName() + " / Status point " + CtiNumStr(binaryoffset+i+7) + " = " + CtiNumStr(pi.value) + "\n";
                }
            }
            else
            {
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, binaryoffset+i+7, StatusPointType);
            }
        }


        for( i = 0; i < 3; i++ )
        {
            if( !errorFlagSet )
            {
                pi = getData(buffer+2*(i+1), 2, ValueType_Raw);
                if( !errorFlagSet && (tempPoint = getDevicePointOffsetTypeEqual(analogoffset+i, AnalogPointType)))
                {
                    pi.value = boost::static_pointer_cast<CtiPointNumeric>(tempPoint)->computeValueForUOM(pi.value);

                    point_string = getName() + " / " + tempPoint->getName() + " = " + CtiNumStr(pi.value);

                    ReturnMsg->PointData().push_back(makePointDataMsg(tempPoint, pi, point_string));
                }
                else
                {
                    resultString += getName() + " / Analog point " + CtiNumStr(analogoffset+i) + " = " + CtiNumStr(pi.value) + "\n";
                }
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
                pi = getData(buffer+2*i+8, 2, ValueType_Raw);
                if( (tempPoint = getDevicePointOffsetTypeEqual(counteroffset+i, PulseAccumulatorPointType)))
                {
                    pi.value = boost::static_pointer_cast<CtiPointNumeric>(tempPoint)->computeValueForUOM(pi.value);

                    point_string = getName() + " / " + tempPoint->getName() + " = " + CtiNumStr(pi.value);

                    ReturnMsg->PointData().push_back(makePointDataMsg(tempPoint, pi, point_string));
                }
                else
                {
                    resultString += getName() + " / Pulse Accumulator point " + CtiNumStr(counteroffset+i) + " = " + CtiNumStr(pi.value) + "\n";
                }
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

string CtiDeviceMCT470::resolveDNPStatus(int status)
{
    string result;
    switch(status)
    {
        case 0:
        {
            result = "Success";
            break;
        }
        case 1:
        {
            result = "Failed Data Link Header";
            break;
        }
        case 2:
        {
            result = "Failed Application Header";
            break;
        }
        case 3:
        {
            result = "Failed Object Header";
            break;
        }
        case 4:
        {
            result = "Invalid Point Configuration";
            break;
        }
        case 5:
        {
            result = "Command Failed";
            break;
        }
        case 6:
        {
            result = "Communications Failure";
            break;
        }
        case 7:
        {
            result = "Application Message Length Error";
            break;
        }
        case 8:
        {
            result = "Data Request Failed";
            break;
        }
        case 9:
        {
            result = "Data Storage Failed";
            break;
        }
        default:
        {
            result = "Unknown";
            break;
        }
    }

    return result;
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
        dout << CtiTime() << " **** Checkpoint - in CtiDeviceMCT31X::DecodeDatabaseReader for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Default data class: " << _iedPort.getDefaultDataClass() << endl;
        dout << "Default data offset: " << _iedPort.getDefaultDataOffset() << endl;
        dout << "Device ID: " << _iedPort.getDeviceID() << endl;
        dout << "IED Scan Rate: " << _iedPort.getIEDScanRate() << endl;
        dout << "IED Type: " << _iedPort.getIEDType() << endl;
        dout << "Password: " << _iedPort.getPassword() << endl;
        dout << "Real Time Scan Flag: " << _iedPort.getRealTimeScanFlag() << endl;
    }
}

