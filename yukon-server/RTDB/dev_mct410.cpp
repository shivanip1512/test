/*-----------------------------------------------------------------------------*
*
* File:   dev_mct410
*
* Date:   4/24/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_mct310.cpp-arc  $
* REVISION     :  $Revision: 1.141 $
* DATE         :  $Date: 2007/04/20 23:09:26 $
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

const CtiDeviceMCT410::DynamicPaoAddressing_t         CtiDeviceMCT410::_dynPaoAddressing     = CtiDeviceMCT410::initDynPaoAddressing();
const CtiDeviceMCT410::DynamicPaoFunctionAddressing_t CtiDeviceMCT410::_dynPaoFuncAddressing = CtiDeviceMCT410::initDynPaoFuncAddressing();

CtiDeviceMCT410::CtiDeviceMCT410( ) :
    _intervalsSent(false)  //  whee!  you're going to be gone soon, sucker!
{
    _daily_read_info.in_progress = false;
    _daily_read_info.failed      = false;
    _daily_read_info.retry       = false;
    _daily_read_info.single_day = 0;
    _daily_read_info.multi_day_start = 0;
    _daily_read_info.multi_day_end   = 0;
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

CtiDeviceMCT410::DynamicPaoAddressing_t CtiDeviceMCT410::initDynPaoAddressing()
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

CtiDeviceMCT410::DynamicPaoFunctionAddressing_t CtiDeviceMCT410::initDynPaoFuncAddressing()
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
    //tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_usage); //Jess does not know what this was intended for...
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_centron);
    tempList.push_back(CtiDeviceMCT4xx::PutConfigPart_tou);

    return tempList;
}


CtiDeviceMCT410::ConfigPartsList CtiDeviceMCT410::getPartsList()
{
    return _config_parts;
}


int CtiDeviceMCT410::makeDynamicDemand(double input) const
{
    /*
    Bits   Resolution            Range
    13-12
    00     100   WHr   40,100.0 WHr - 400,000.0 WHr
    01      10   WHr    4,010.0 WHr -  40,000.0  WHr
    10       1   WHr      401.0 WHr -   4,000.0  WHr
    11       0.1 WHr        0.0 WHr -     400.0  WHr
    */

    int output;
    int resolution;
    float divisor;

    if( input > 40950.0 || input < 0.0 )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - input = " << input << " in CtiDeviceMCT4xx::makeDynamicDemand() for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        output = -1;
    }
    else if( input < 0.05 )
    {
        output = 0;
    }
    else
    {
        if(      input <=   400.0 )     {   divisor =   0.1;    resolution = 0x3;   }
        else if( input <=  4000.0 )     {   divisor =   1.0;    resolution = 0x2;   }
        else if( input <= 40000.0 )     {   divisor =  10.0;    resolution = 0x1;   }
        else                            {   divisor = 100.0;    resolution = 0x0;   }

        output = input / divisor;

        if( fmod( input, divisor ) >= (divisor / 2.0) )
        {
            output++;
        }

        output |= resolution << 12;
    }

    return output;
}


CtiDeviceMCT410::point_info CtiDeviceMCT410::getDemandData(unsigned char *buf, int len) const
{
    return getData(buf, len, ValueType_DynamicDemand);
}

CtiDeviceMCT410::point_info CtiDeviceMCT410::getData( unsigned char *buf, int len, ValueType410 vt ) const
{
    PointQuality_t quality = NormalQuality;
    unsigned long error_code = 0xffffffff,  //  filled with 0xff because some data types are less than 32 bits
                  min_error  = 0xffffffff;
    unsigned char quality_flags = 0,
                  resolution    = 0;
    unsigned char error_byte, value_byte;

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

        //  the first byte for some value types needs to be treated specially
        if( i == 0 )
        {
            if( vt == ValueType_DynamicDemand ||
                vt == ValueType_LoadProfile_DynamicDemand )
            {
                resolution    = value_byte & 0x30;
                quality_flags = value_byte & 0xc0;

                value_byte   &= 0x0f;  //  trim off the quality bits and the resolution bits
                error_byte   |= 0xf0;  //  fill in the quality bits to get the true error code
            }
            else if( vt == ValueType_LoadProfile_Voltage )
            {
                quality_flags = value_byte & 0xc0;

                value_byte   &= 0x3f;  //  trim off the quality bits
                error_code   |= 0xc0;  //  fill in the quality bits to get the true error code
            }
            else if( vt == ValueType_AccumulatorDelta )
            {
                value_byte   &= 0x3f;  //  trim off the channel bits
                error_code   |= 0xc0;  //  fill in the channel bits to get the true error code
            }
        }

        value      |= value_byte;
        error_code |= error_byte;
    }

    retval.freeze_bit = value & 0x01;

    switch( vt )
    {
        case ValueType_Voltage:                     min_error = 0xffffffe0; break;

        case ValueType_AccumulatorDelta:            min_error = 0xfffffffa; break;

        case ValueType_DynamicDemand:
        case ValueType_LoadProfile_DynamicDemand:
        case ValueType_LoadProfile_Voltage:         min_error = 0xffffffa1; break;
    }

    if( error_code >= min_error )
    {
        value       = 0;

        error_set::const_iterator es_itr = _mct_error_info.find(error_info(error_code));

        if( es_itr != _mct_error_info.end() )
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
    else
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

    if( vt == ValueType_DynamicDemand || vt == ValueType_LoadProfile_DynamicDemand )
    {
        if( getMCTDebugLevel(DebugLevel_Info) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - demand value " << (unsigned long)value << " resolution " << (int)resolution << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

        retval.value *= 10.0;  //  remove this whenever we implement the proper, pretty, nice 1.0 kWH output code
    }

    return retval;
}


CtiDeviceMCT410::point_info CtiDeviceMCT410::getLoadProfileData(unsigned channel, unsigned char *buf, unsigned len)
{
    point_info pi;

    // input channel is 0-based, enums are 1-based
    channel++;

    if( channel <= ChannelCount )
    {
        pi = getData(buf, len, ValueType_LoadProfile_DynamicDemand);
        pi.value *= 3600 / getLoadProfileInterval(channel);
    }
    else if( channel == Channel_Voltage )
    {
        pi = getData(buf, len, ValueType_LoadProfile_Voltage);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - channel = " << channel << " in CtiDeviceMCT410::getLoadProfileData() for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        pi = CtiDeviceMCT4xx::getData(buf, len, ValueType_Raw);
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
    cs.insert(CommandStore(Emetcon::GetConfig_Time,             Emetcon::IO_Read,           Memory_TimeZoneOffsetPos,       Memory_TimeZoneOffsetLen
                                                                                                                            + Memory_RTCLen));
    cs.insert(CommandStore(Emetcon::PutConfig_TimeZoneOffset,   Emetcon::IO_Write,          Memory_TimeZoneOffsetPos,       Memory_TimeZoneOffsetLen));
    cs.insert(CommandStore(Emetcon::PutConfig_Intervals,        Emetcon::IO_Function_Write, FuncWrite_IntervalsPos,         FuncWrite_IntervalsLen));
    cs.insert(CommandStore(Emetcon::GetConfig_Intervals,        Emetcon::IO_Read,           Memory_IntervalsPos,            Memory_IntervalsLen));
    cs.insert(CommandStore(Emetcon::GetValue_PFCount,           Emetcon::IO_Read,           Memory_PowerfailCountPos,       Memory_PowerfailCountLen));
    cs.insert(CommandStore(Emetcon::PutStatus_Reset,            Emetcon::IO_Write,          Command_Reset,                  0));
    cs.insert(CommandStore(Emetcon::PutStatus_FreezeOne,        Emetcon::IO_Write,          Command_FreezeOne,              0));
    cs.insert(CommandStore(Emetcon::PutStatus_FreezeTwo,        Emetcon::IO_Write,          Command_FreezeTwo,              0));
    cs.insert(CommandStore(Emetcon::PutStatus_FreezeVoltageOne, Emetcon::IO_Write,          Command_FreezeVoltageOne,       0));
    cs.insert(CommandStore(Emetcon::PutStatus_FreezeVoltageTwo, Emetcon::IO_Write,          Command_FreezeVoltageTwo,       0));

    cs.insert(CommandStore(Emetcon::PutConfig_UniqueAddress,    Emetcon::IO_Function_Write, FuncWrite_SetAddressPos,        FuncWrite_SetAddressLen));

    //******************************** Config Related starts here *************************
    cs.insert(CommandStore(Emetcon::PutConfig_Addressing,       Emetcon::IO_Write,          Memory_BronzeAddressPos,        Memory_BronzeAddressLen
                                                                                                                            + Memory_LeadAddressLen
                                                                                                                            + Memory_CollectionAddressLen
                                                                                                                            + Memory_SPIDAddressLen));

    cs.insert(CommandStore(Emetcon::PutConfig_LongLoadProfile,  Emetcon::IO_Function_Write, FuncWrite_LLPStoragePos,        FuncWrite_LLPStorageLen));
    cs.insert(CommandStore(Emetcon::GetConfig_LongLoadProfile,  Emetcon::IO_Function_Read,  FuncRead_LLPStatusPos,          FuncRead_LLPStatusLen));

    cs.insert(CommandStore(Emetcon::PutConfig_DST,              Emetcon::IO_Write,          Memory_DSTBeginPos,             Memory_DSTBeginLen
                                                                                                                            + Memory_DSTEndLen
                                                                                                                            + Memory_TimeZoneOffsetLen));

    cs.insert(CommandStore(Emetcon::PutConfig_VThreshold,       Emetcon::IO_Write,          Memory_OverVThresholdPos,       Memory_OverVThresholdLen +
                                                                                                                            Memory_UnderVThresholdLen));
    //  used by both the putconfig install and putconfig holiday commands
    cs.insert(CommandStore(Emetcon::PutConfig_Holiday,          Emetcon::IO_Write,          Memory_Holiday1Pos,             Memory_Holiday1Len
                                                                                                                            + Memory_Holiday2Len
                                                                                                                            + Memory_Holiday3Len));
    cs.insert(CommandStore(Emetcon::GetConfig_Holiday,          Emetcon::IO_Read,           Memory_Holiday1Pos,             Memory_Holiday1Len
                                                                                                                            + Memory_Holiday2Len
                                                                                                                            + Memory_Holiday3Len));
    cs.insert(CommandStore(Emetcon::PutConfig_Options,          Emetcon::IO_Function_Write, FuncWrite_ConfigAlarmMaskPos,   FuncWrite_ConfigAlarmMaskLen));
    cs.insert(CommandStore(Emetcon::PutConfig_Outage,           Emetcon::IO_Write,          Memory_OutageCyclesPos,         Memory_OutageCyclesLen));
    cs.insert(CommandStore(Emetcon::PutConfig_TimeAdjustTolerance, Emetcon::IO_Write,       Memory_TimeAdjustTolPos,        Memory_TimeAdjustTolLen));

    //************************************ End Config related *****************************

    return cs;
}


long CtiDeviceMCT410::getLoadProfileInterval( unsigned channel )
{
    int retval = 3600;

    //  input channel is 0-based, enums are 1-based
    channel++;

    if( channel == Channel_Voltage )
    {
        retval = getLoadProfile().getVoltageProfileRate();
    }
    else if( channel <= ChannelCount )
    {
        retval = getLoadProfile().getLoadProfileDemandRate();
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - channel = " << channel << " in getLoadProfileInterval() for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retval = 3600;
    }

    return retval;
}


bool CtiDeviceMCT410::getOperation( const UINT &cmd, BSTRUCT &bst ) const
{
    bool found = false;

    CommandSet::const_iterator itr = _commandStore.find( CommandStore( cmd ) );

    if( itr != _commandStore.end( ) )
    {
        bst.Function = itr->function;   //  Copy the relevant bits from the commandStore
        bst.Length   = itr->length;     //
        bst.IO       = itr->io;         //

        found = true;
    }
    else    //  Look in the parent if not found in the child
    {
        found = Inherited::getOperation( cmd, bst );
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
                    setDynamicInfo(foundKey, value);
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
        case Emetcon::GetValue_FrozenKWH:           status = decodeGetValueKWH(InMessage, TimeNow, vgList, retList, outList);           break;

        case Emetcon::Scan_Integrity:
        case Emetcon::GetValue_Demand:              status = decodeGetValueDemand(InMessage, TimeNow, vgList, retList, outList);        break;

        case Emetcon::GetValue_PeakDemand:
        case Emetcon::GetValue_FrozenPeakDemand:    status = decodeGetValuePeakDemand(InMessage, TimeNow, vgList, retList, outList);    break;

        case Emetcon::GetValue_Voltage:
        case Emetcon::GetValue_FrozenVoltage:       status = decodeGetValueVoltage(InMessage, TimeNow, vgList, retList, outList);       break;

        case Emetcon::GetValue_Outage:              status = decodeGetValueOutage(InMessage, TimeNow, vgList, retList, outList);        break;

        case Emetcon::GetValue_FreezeCounter:       status = decodeGetValueFreezeCounter(InMessage, TimeNow, vgList, retList, outList); break;

        case Emetcon::GetValue_LoadProfilePeakReport:   status = decodeGetValueLoadProfilePeakReport(InMessage, TimeNow, vgList, retList, outList); break;

        case Emetcon::GetValue_DailyRead:           status = decodeGetValueDailyRead(InMessage, TimeNow, vgList, retList, outList);     break;

        case Emetcon::GetStatus_Internal:           status = decodeGetStatusInternal(InMessage, TimeNow, vgList, retList, outList);     break;

        case Emetcon::GetStatus_LoadProfile:        status = decodeGetStatusLoadProfile(InMessage, TimeNow, vgList, retList, outList);  break;

        case Emetcon::GetStatus_Disconnect:         status = decodeGetStatusDisconnect(InMessage, TimeNow, vgList, retList, outList);   break;

        case Emetcon::GetConfig_Disconnect:         status = decodeGetConfigDisconnect(InMessage, TimeNow, vgList, retList, outList);   break;

        case Emetcon::GetConfig_UniqueAddress:      status = decodeGetConfigAddress(InMessage, TimeNow, vgList, retList, outList);      break;

        case Emetcon::GetConfig_Intervals:          status = decodeGetConfigIntervals(InMessage, TimeNow, vgList, retList, outList);    break;

        case Emetcon::GetConfig_Model:              status = decodeGetConfigModel(InMessage, TimeNow, vgList, retList, outList);        break;

        case Emetcon::GetConfig_Multiplier:
        case Emetcon::GetConfig_CentronParameters:  status = decodeGetConfigCentron(InMessage, TimeNow, vgList, retList, outList);      break;

        case Emetcon::Control_Connect:
        case Emetcon::Control_Disconnect:
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


INT CtiDeviceMCT410::ErrorDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList)
{
    int retVal = NoError;

    switch( InMessage->Sequence )
    {
        case Emetcon::GetValue_DailyRead:
        {
            if( !_daily_read_info.retry && _daily_read_info.current_request == daily_read_info_t::Request_MultiDay )
            {
                _daily_read_info.retry = true;

                string request_str = "getvalue daily read ";

                request_str += "channel " + CtiNumStr(_daily_read_info.channel) + " " + printable_date(_daily_read_info.multi_day_start)
                                                                                + " " + printable_date(_daily_read_info.multi_day_end);

                if( strstr(InMessage->Return.CommandStr, " noqueue") )  request_str += " noqueue";

                CtiRequestMsg newReq(getID(),
                                     request_str,
                                     InMessage->Return.UserID,
                                     0,
                                     InMessage->Return.RouteID,
                                     InMessage->Return.MacroOffset,
                                     0,
                                     0,
                                     InMessage->Priority);

                newReq.setConnectionHandle((void *)InMessage->Return.Connection);

                //  reset the "in progress" flag
                _daily_read_info.current_request = daily_read_info_t::Request_None;
                InterlockedExchange(&_daily_read_info.in_progress, false);

                CtiDeviceBase::ExecuteRequest(&newReq, CtiCommandParser(newReq.CommandString()), vgList, retList, outList);
            }
            else
            {
                _daily_read_info.failed = true;
                _daily_read_info.retry  = false;

                //  reset the "in progress" flag
                _daily_read_info.current_request = daily_read_info_t::Request_None;
                InterlockedExchange(&_daily_read_info.in_progress, false);
            }

            break;
        }

        default:
        {
            retVal = Inherited::ErrorDecode(InMessage, TimeNow, vgList, retList, outList);

            break;
        }
    }

    return retVal;
}


void CtiDeviceMCT410::returnErrorMessage( int retval, const CtiOutMessage *om, list< CtiMessage* > &retList, const string &error ) const
{
    CtiReturnMsg *errRet;

    errRet = CTIDBG_new CtiReturnMsg(getID(),
                                     string(om->Request.CommandStr),
                                     error,
                                     retval,
                                     om->Request.RouteID,
                                     om->Request.MacroOffset,
                                     om->Request.Attempt,
                                     om->Request.TrxID,
                                     om->Request.UserID,
                                     om->Request.SOE,
                                     CtiMultiMsg_vec());

    if( errRet )
    {
        retList.push_back(errRet);
    }
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

    if( parse.isKeyValid("address") && parse.isKeyValid("uniqueaddress") )
    {
        int uadd;

        function = Emetcon::PutConfig_UniqueAddress;
        found    = getOperation(function, OutMessage->Buffer.BSt);

        uadd = parse.getiValue("uniqueaddress");

        if( uadd > 0x3fffff || uadd < 0 )
        {
            found = false;
            nRet  = NoMethod;

            returnErrorMessage(NoMethod, OutMessage, retList,
                               "Invalid address \"" + CtiNumStr(uadd) + "\" for device \"" + getName() + "\", not sending");
        }
        else
        {
            OutMessage->Sequence = function;

            OutMessage->Buffer.BSt.Message[0] = gMCT400SeriesSPID;
            OutMessage->Buffer.BSt.Message[1] = ( uadd >> 16) & 0x0000ff;
            OutMessage->Buffer.BSt.Message[2] = ( uadd >>  8) & 0x0000ff;
            OutMessage->Buffer.BSt.Message[3] = ( uadd      ) & 0x0000ff;
        }
    }
    else if( parse.isKeyValid("centron_display") ||
             parse.isKeyValid("centron_ratio")   ||
             parse.isKeyValid("centron_test_duration") ||
             parse.isKeyValid("centron_error_display") )
    {
        unsigned char centron_config = 0x00;  //  default, see sspec for details

        OutMessage->Buffer.BSt.Function = FuncWrite_CentronParametersPos;
        OutMessage->Buffer.BSt.Length   = FuncWrite_CentronParametersLen;
        OutMessage->Buffer.BSt.IO       = Emetcon::IO_Function_Write;

        OutMessage->Sequence            = Emetcon::PutConfig_Multiplier;

        found = true;  //  default to true;  set to false in error cases

        OutMessage->Buffer.BSt.Message[0] = gMCT400SeriesSPID;

        if( parse.isKeyValid("centron_display") )
        {
            string display = parse.getsValue("centron_display");

            if(      !display.compare("5x1")  )     centron_config |= 0x00;
            else if( !display.compare("4x1")  )     centron_config |= 0x01;
            else if( !display.compare("4x10") )     centron_config |= 0x02;
            else
            {
                found = false;
                nRet  = BADPARAM;

                returnErrorMessage(BADPARAM, OutMessage, retList,
                                   "Invalid Centron display configuration \"" + display + "\"");
            }
        }

        if( parse.isKeyValid("centron_test_duration") )
        {
            int test = parse.getiValue("centron_test_duration");

            if(      test == 1 )  centron_config |= 0x04;
            else if( test == 7 )  centron_config |= 0x0c;
            else
            {
                found = false;
                nRet  = BADPARAM;

                returnErrorMessage(BADPARAM, OutMessage, retList,
                                   "Invalid Centron test duration \"" + CtiNumStr(test) + "\"");
            }
        }

        if( parse.isKeyValid("centron_error_display") )
        {
            centron_config |= 0x10;
        }

        OutMessage->Buffer.BSt.Message[1] = centron_config;

        if( parse.isKeyValid("centron_ratio") )
        {
            int centron_ratio = parse.getiValue("centron_ratio");

            if( centron_ratio > 0 && centron_ratio <= 255 )
            {
                OutMessage->Buffer.BSt.Message[2] = (unsigned char)parse.getiValue("centron_ratio");
            }
            else
            {
                found = false;
                nRet  = BADPARAM;

                returnErrorMessage(BADPARAM, OutMessage, retList,
                                   "Invalid Centron multiplier (" + CtiNumStr(centron_ratio) + ")");
            }
        }
        else
        {
            //  omit the multiplier ratio
            OutMessage->Buffer.BSt.Length--;
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
    else if( parse.isKeyValid("disconnect") )
    {
        found = getOperation(Emetcon::PutConfig_Disconnect, OutMessage->Buffer.BSt);

        OutMessage->Sequence = Emetcon::PutConfig_Disconnect;

        long   tmpaddress = _disconnectAddress & 0x3fffff;  //  make sure it's only 22 bits
        int    connect_delay, dynamic_demand_threshold;
        float  demand_threshold;

        //  default to no load limit
        demand_threshold = parse.getdValue("disconnect demand threshold",         0.0);
        //  default to a 5 minute connect delay
        connect_delay    = parse.getiValue("disconnect load limit connect delay", 5);

        //  make it Wh units
        demand_threshold *= 1000.0;

        demand_threshold /= 3600 / getDemandInterval();

        if( (dynamic_demand_threshold = makeDynamicDemand(demand_threshold)) < 0
            ||  connect_delay > 10 )
        {
            found = false;
            nRet  = BADPARAM;

            returnErrorMessage(BADPARAM, OutMessage, retList,
                               "Invalid disconnect parameters (" + CtiNumStr(demand_threshold) + ", " + CtiNumStr(connect_delay) + ")");
        }
        else
        {
            OutMessage->Buffer.BSt.Message[0] = (tmpaddress >> 16) & 0xff;
            OutMessage->Buffer.BSt.Message[1] = (tmpaddress >>  8) & 0xff;
            OutMessage->Buffer.BSt.Message[2] =  tmpaddress        & 0xff;

            OutMessage->Buffer.BSt.Message[3] = (dynamic_demand_threshold >> 8) & 0xff;
            OutMessage->Buffer.BSt.Message[4] =  dynamic_demand_threshold       & 0xff;

            OutMessage->Buffer.BSt.Message[5] = connect_delay & 0xff;

            int connect_minutes    = parse.getiValue("disconnect cycle connect minutes",    -1),
                disconnect_minutes = parse.getiValue("disconnect cycle disconnect minutes", -1);

            //  optional parameters
            if( connect_minutes < 0 && disconnect_minutes < 0 )
            {
                OutMessage->Buffer.BSt.Length -= 2;
            }
            else if( (disconnect_minutes && connect_minutes)
                     && (disconnect_minutes <  5 || connect_minutes <  5 ||
                         disconnect_minutes > 60 || connect_minutes > 60) )
            {
                found = false;
                nRet  = BADPARAM;

                returnErrorMessage(BADPARAM, OutMessage, retList,
                                   "Invalid disconnect cycle parameters (" + CtiNumStr(disconnect_minutes) + ", " + CtiNumStr(connect_minutes) + ")");
            }
            else
            {
                OutMessage->Buffer.BSt.Message[6] = disconnect_minutes & 0xff;
                OutMessage->Buffer.BSt.Message[7] = connect_minutes    & 0xff;
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
        found = getOperation(function, OutMessage->Buffer.BSt);

        if( parse.getFlags() & CMD_FLAG_FROZEN )    OutMessage->Buffer.BSt.Function += FuncRead_TOUFrozenOffset;

        //  no need to increment for rate A
        if( parse.getFlags() & CMD_FLAG_GV_RATEB )  OutMessage->Buffer.BSt.Function += 1;
        if( parse.getFlags() & CMD_FLAG_GV_RATEC )  OutMessage->Buffer.BSt.Function += 2;
        if( parse.getFlags() & CMD_FLAG_GV_RATED )  OutMessage->Buffer.BSt.Function += 3;
    }
    else if( parse.isKeyValid("daily_read") )
    {
        //  daily reads
        if( InterlockedCompareExchange((PVOID *)&_daily_read_info.in_progress, (PVOID)true, (PVOID)false) )
        {
            int channel = 1;

            string temp = getName() + " / Daily read request already in progress\n";

            switch( _daily_read_info.current_request )
            {
                case daily_read_info_t::Request_MultiDay:       channel = _daily_read_info.channel;  break;
                case daily_read_info_t::Request_SingleDayCh1:   channel = 1;  break;
                case daily_read_info_t::Request_SingleDayCh2:   channel = 2;  break;
                case daily_read_info_t::Request_SingleDayCh3:   channel = 3;  break;
                case daily_read_info_t::Request_RecentCh1:      channel = 1;  break;
            }

            temp += "Channel " + CtiNumStr(channel) + ", ";

            if( _daily_read_info.single_day )
            {
                temp += printable_date(_daily_read_info.single_day) + "\n";
            }
            else
            {
                temp += printable_date(_daily_read_info.multi_day_start) + " - " + printable_date(_daily_read_info.multi_day_end) + "\n";
            }

            returnErrorMessage(NOTNORMAL, OutMessage, retList, temp);
        }
        else
        {
            int day, month, year;
            int channel;
            CtiTime time_begin = CtiTime(86400),
                    time_end   = CtiTime(86400);

            //  grab the beginning date
            if( parse.isKeyValid("daily_read_date_begin") )
            {
                CtiTokenizer date_end_tok(parse.getsValue("daily_read_date_begin"));

                month = atoi(date_end_tok("-/").data());
                day   = atoi(date_end_tok("-/").data());
                year  = atoi(date_end_tok("-/").data());

                if( year < 100 )  year += 2000;  //  this will need to change in 2100

                time_begin = CtiTime(CtiDate(day, month, year));
            }

            //  grab the end date
            if( parse.isKeyValid("daily_read_date_end") )
            {
                CtiTokenizer date_end_tok(parse.getsValue("daily_read_date_end"));

                month = atoi(date_end_tok("-/").data());
                day   = atoi(date_end_tok("-/").data());
                year  = atoi(date_end_tok("-/").data());

                if( year < 100 )  year += 2000;  //  this will need to change in 2100

                time_end = CtiTime(CtiDate(day, month, year));
            }

            function = Emetcon::GetValue_DailyRead;
            OutMessage->Buffer.BSt.IO = Emetcon::IO_Function_Read;

            if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) )
            {
                returnErrorMessage(NoMethod, OutMessage, retList, getName() + " / Daily read requires SSPEC rev 2.1 or higher - execute \"getconfig model\" to verify");
            }
            else if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) < SspecRev_DailyRead )
            {
                returnErrorMessage(NoMethod, OutMessage, retList, getName() + " / Daily read requires SSPEC rev 2.1 or higher, MCT reports " + CtiNumStr(getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) / 10.0, 1));
            }
            else if( !time_begin.isValid() || !time_end.isValid() )
            {
                nRet  = BADPARAM;

                string error_string;

                if( parse.isKeyValid("daily_read_date_end") )
                {
                    error_string  = getName() + " / Invalid dates for daily read request ";

                    error_string += "(" + parse.getsValue("daily_read_date_begin") + " - " + parse.getsValue("daily_read_date_end") + ")";
                }
                else
                {
                    error_string  = getName() + " / Invalid date for daily read request ";

                    error_string += "(" + parse.getsValue("daily_read_date_begin") + ")";
                }

                returnErrorMessage(NoMethod, OutMessage, retList, error_string);
            }
            else if( !parse.isKeyValid("channel") )
            {
                //  single-day read for channel 1
                if( time_begin < (CtiTime::now() - 86400 * 8) ||   //  must be less than 8 days ago
                    time_begin >  CtiTime::now() - 86400 )         //  must begin on or before yesterday midnight
                {
                    returnErrorMessage(NoMethod, OutMessage, retList,
                                       getName() + " / Invalid date for daily read request (" + parse.getsValue("daily_read_date_begin")  + ") - must be less than 8 days ago");
                }
                else
                {
                    unsigned long day_offset;

                    day_offset = (CtiTime::now().seconds() - time_begin.seconds()) / 86400;

                    // should always be at least 1, since we're always yesterday midnight or before
                    day_offset--;

                    //  make sure the date we're requesting is aligned to a read boundary
                    OutMessage->Buffer.BSt.Function = FuncRead_Channel1SingleDayBasePos + day_offset;
                    OutMessage->Buffer.BSt.Length   = FuncRead_Channel1SingleDayLen;

                    _daily_read_info.current_request = daily_read_info_t::Request_RecentCh1;

                    found = true;
                }
            }
            else
            {
                channel = parse.getiValue("channel", 0);

                if( channel < 1 || channel > 3 )
                {
                    returnErrorMessage(NoMethod, OutMessage, retList,
                                       getName() + " / Invalid channel for daily read request (" + CtiNumStr(channel) + ")");
                }
                else if( time_begin < (CtiTime::now() - 86400 * 93) ||   //  must be less than 93 days ago
                         time_begin >  CtiTime::now() )                  //  must begin on or before yesterday midnight
                {
                    nRet  = BADPARAM;

                    string error_string;

                    error_string  = getName() + " / Beginning date out of range for daily read request - must be less than 3 months ago and before today ";

                    error_string += "(" + parse.getsValue("daily_read_date_begin") + ")";

                    returnErrorMessage(NoMethod, OutMessage, retList, error_string);
                }
                else if( time_end  > 86400 && (time_end <= time_begin ||         //  make sure it ends after it starts
                                               time_end >  CtiTime::now()) )     //  must end on or before yesterday midnight
                {
                    nRet  = BADPARAM;

                    string error_string;

                    error_string  = getName() + " / Ending date out of range for daily read request - must be after beginning time and before today";

                    error_string += "(" + parse.getsValue("daily_read_date_end") + ")";

                    returnErrorMessage(NoMethod, OutMessage, retList, error_string);
                }
                else
                {
                    //  if the channel doesn't match and it's a multi-day request,
                    //    and/or if this is a single-day request and our time doesn't match,
                    //    we must send the time and channel of interest
                    if( (time_end.seconds() >  86400 && _daily_read_info.channel    != channel) ||
                        (time_end.seconds() <= 86400 && _daily_read_info.single_day != time_begin.seconds()) )
                    {
                        //  we need to set it to the requested interval
                        CtiOutMessage *interest_om = new CtiOutMessage(*OutMessage);

                        interest_om->Sequence = Emetcon::PutConfig_DailyReadInterest;

                        interest_om->Buffer.BSt.Function = FuncWrite_DailyReadInterestPos;
                        interest_om->Buffer.BSt.Length   = FuncWrite_DailyReadInterestLen;
                        interest_om->Buffer.BSt.IO       = Emetcon::IO_Function_Write;
                        interest_om->MessageFlags |= MessageFlag_ExpectMore;

                        interest_om->Buffer.BSt.Message[0] = gMCT400SeriesSPID;

                        if( time_end.seconds() <= 86400 )
                        {
                            //  single-day request - send the time of interest
                            interest_om->Buffer.BSt.Message[1] = 0;
                            interest_om->Buffer.BSt.Message[2] = time_begin.date().dayOfMonth();
                            interest_om->Buffer.BSt.Message[3] = time_begin.date().month();

                            _daily_read_info.single_day = time_begin.seconds();
                        }
                        else
                        {
                            //  multi-day request - only set the channel
                            interest_om->Buffer.BSt.Message[1] = channel;
                            interest_om->Buffer.BSt.Message[2] = 0;
                            interest_om->Buffer.BSt.Message[3] = 0;

                            _daily_read_info.channel = channel;
                        }

                        outList.push_back(interest_om);
                        interest_om = 0;
                    }

                    if( time_end.seconds() > 86400 )
                    {
                        //  multi-day read

                        unsigned long day_offset;

                        day_offset = ((CtiTime::now().seconds() - time_begin.seconds()) / 86400) + 1;

                        //  multi-day read
                        _daily_read_info.multi_day_start  = time_begin.seconds();
                        _daily_read_info.multi_day_end    = time_end.seconds();

                        //  make sure the date we're requesting is aligned to a read boundary
                        if( day_offset % 6 )
                        {
                            _daily_read_info.multi_day_start -= (6 - (day_offset % 6)) * 86400;
                        }

                        OutMessage->Buffer.BSt.Function = FuncRead_MultiDayDailyReportingBasePos + ((day_offset - 1) / 6);
                        OutMessage->Buffer.BSt.Length   = FuncRead_MultiDayDailyReportingLen;

                        _daily_read_info.current_request = daily_read_info_t::Request_MultiDay;

                        found = true;
                    }
                    else
                    {
                        //  single-day read

                        if( channel == 1 )
                        {
                            OutMessage->Buffer.BSt.Function = FuncRead_SingleDayDailyReportCh1Pos;
                            OutMessage->Buffer.BSt.Length   = FuncRead_SingleDayDailyReportCh1Len;

                            _daily_read_info.current_request = daily_read_info_t::Request_SingleDayCh1;

                            found = true;
                        }
                        if( channel == 2 )
                        {
                            OutMessage->Buffer.BSt.Function = FuncRead_SingleDayDailyReportCh2Pos;
                            OutMessage->Buffer.BSt.Length   = FuncRead_SingleDayDailyReportCh2Len;

                            _daily_read_info.current_request = daily_read_info_t::Request_SingleDayCh2;

                            found = true;
                        }
                        if( channel == 3 )
                        {
                            OutMessage->Buffer.BSt.Function = FuncRead_SingleDayDailyReportCh3Pos;
                            OutMessage->Buffer.BSt.Length   = FuncRead_SingleDayDailyReportCh3Len;

                            _daily_read_info.current_request = daily_read_info_t::Request_SingleDayCh3;

                            found = true;
                        }
                    }
                }

                nRet = NoError;
            }

            if( !found )
            {
                InterlockedExchange(&_daily_read_info.in_progress, false);
            }
        }
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
                getOperation(Emetcon::GetConfig_Model, sspec_om->Buffer.BSt);

                sspec_om->Sequence = Emetcon::GetConfig_Model;

                sspec_om->MessageFlags |= MessageFlag_ExpectMore;

                outList.push_back(sspec_om);
                sspec_om = 0;
            }
        }

        int outagenum = parse.getiValue("outage");

        function = Emetcon::GetValue_Outage;
        found = getOperation(function, OutMessage->Buffer.BSt);

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

    if( errRet != NULL )
    {
        delete errRet;
        errRet = NULL;
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

    if(parse.isKeyValid("disconnect"))
    {
        found = getOperation(Emetcon::GetConfig_Disconnect, OutMessage->Buffer.BSt);

        OutMessage->Sequence = Emetcon::GetConfig_Disconnect;
    }
    else if( parse.isKeyValid("address_unique") )
    {
        found = true;

        OutMessage->Buffer.BSt.Function = Memory_UniqueAddressPos;
        OutMessage->Buffer.BSt.Length   = Memory_UniqueAddressLen;
        OutMessage->Buffer.BSt.IO       = Emetcon::IO_Read;

        OutMessage->Sequence = Emetcon::GetConfig_UniqueAddress;
    }
    else if( parse.isKeyValid("centron") )
    {
        found = true;

        if( parse.isKeyValid("centron_ratio") )
        {
            OutMessage->Buffer.BSt.Function = Memory_CentronMultiplierPos;
            OutMessage->Buffer.BSt.Length   = Memory_CentronMultiplierLen;
            OutMessage->Buffer.BSt.IO       = Emetcon::IO_Read;

            OutMessage->Sequence = Emetcon::GetConfig_Multiplier;
        }
        else if( parse.isKeyValid("centron_parameters") )
        {
            OutMessage->Buffer.BSt.Function = Memory_CentronParametersPos;
            OutMessage->Buffer.BSt.Length   = Memory_CentronParametersLen;
            OutMessage->Buffer.BSt.IO       = Emetcon::IO_Read;

            OutMessage->Sequence = Emetcon::GetConfig_CentronParameters;
        }
        else
        {
            OutMessage->Buffer.BSt.Function = Memory_CentronConfigPos;
            OutMessage->Buffer.BSt.Length   = Memory_CentronConfigLen;
            OutMessage->Buffer.BSt.IO       = Emetcon::IO_Read;

            OutMessage->Sequence = Emetcon::GetConfig_CentronParameters;
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
                        OutMessage->Buffer.BSt.Length     = revision >= SspecRev_Disconnect_Cycle ? FuncRead_DisconnectConfigLen : FuncRead_DisconnectConfigLen - 2;
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

            if( !getOperation(Emetcon::PutConfig_Options, OutMessage->Buffer.BSt) )
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
                        //  bstruct IO info was filled in above
                        OutMessage->Buffer.BSt.Message[0] = (configuration);
                        OutMessage->Buffer.BSt.Message[1] = (event1mask);
                        OutMessage->Buffer.BSt.Message[2] = (event2mask);
                        OutMessage->Buffer.BSt.Message[3] = (meterAlarmMask>>8);
                        OutMessage->Buffer.BSt.Message[4] = (meterAlarmMask);
                        OutMessage->Buffer.BSt.Message[5] = (options);
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        //  this should be changed to a lookup read
                        OutMessage->Buffer.BSt.IO         = Emetcon::IO_Read;
                        OutMessage->Buffer.BSt.Function   = Memory_OptionsPos;
                        OutMessage->Buffer.BSt.Length     = Memory_OptionsLen + Memory_ConfigurationLen;
                        OutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        //  as should this
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

            if( !getOperation(Emetcon::PutConfig_Outage, OutMessage->Buffer.BSt) )
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
                        //  bstruct IO info was filled in above
                        OutMessage->Buffer.BSt.Message[0] = (outage);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        //  this should probably be a lookup in a mapping table instead
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

            if( !getOperation(Emetcon::PutConfig_TimeAdjustTolerance, OutMessage->Buffer.BSt) )
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
                        //  bstruct io info was filled in above
                        OutMessage->Buffer.BSt.Message[0] = (timeAdjustTolerance);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        //  this should be a lookup in a mapping table
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
    INT tags = 0;
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

            pi_freezecount = CtiDeviceMCT4xx::getData(DSt->Message + 3, 1, ValueType_Raw);

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
                tags = TAG_POINT_MUST_ARCHIVE;

                pi = CtiDeviceMCT4xx::getData(DSt->Message + offset, 3, ValueType_Accumulator);

                pointTime -= pointTime.seconds() % 60;
            }
            else if( InMessage->Sequence == Cti::Protocol::Emetcon::GetValue_FrozenKWH )
            {
                //  but this is where the action is - frozen decode
                tags = 0;

                if( i ) offset++;  //  so that, for the frozen read, it goes 0, 4, 7 to step past the freeze counter in position 3

                pi = CtiDeviceMCT4xx::getData(DSt->Message + offset, 3, ValueType_FrozenAccumulator);

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
                bool reported = false;

                if( !i )    point_name = "Meter Reading";

                //  if kWh was returned as units, we could get rid of the default multiplier - it's messy
                insertPointDataReport(PulseAccumulatorPointType, i + 1,
                                      ReturnMsg, pi, point_name, pointTime, 0.1, tags);

                //  if the quality's invalid, throw the status to abnormal if it's the first channel OR there's a point defined
                if( pi.quality == InvalidQuality && (!i || getDevicePointOffsetTypeEqual(i + 1, PulseAccumulatorPointType)) )
                {
                    status = NOTNORMAL;
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
            int offset = (i > 1)?(i * 2 + 2):(0);  //  0, 6, 8

            pi = getData(DSt->Message + offset, 2, ValueType_DynamicDemand);

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

        pi = CtiDeviceMCT4xx::getData(DSt->Message + 4, 2, ValueType_Raw);

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
        pi = CtiDeviceMCT4xx::getData(DSt->Message + 2, 4, ValueType_Raw);
        maxTime = CtiTime((unsigned long)pi.value);


        min_volt_info = getData(DSt->Message + 6, 2, ValueType_Voltage);
        pi = CtiDeviceMCT4xx::getData(DSt->Message + 8, 4, ValueType_Raw);
        minTime = CtiTime((unsigned long)pi.value);

        if( InMessage->Sequence == Emetcon::GetValue_FrozenVoltage )
        {
            insertPointDataReport(DemandAccumulatorPointType, PointOffset_VoltageMax,
                                  ReturnMsg, max_volt_info, "Frozen Maximum Voltage", maxTime, 0.1);

            insertPointDataReport(DemandAccumulatorPointType, PointOffset_VoltageMin,
                                  ReturnMsg, min_volt_info, "Frozen Minimum Voltage", minTime, 0.1);
        }
        else
        {
            insertPointDataReport(DemandAccumulatorPointType, PointOffset_VoltageMax,
                                  ReturnMsg, max_volt_info, "Maximum Voltage", maxTime, 0.1);

            insertPointDataReport(DemandAccumulatorPointType, PointOffset_VoltageMin,
                                  ReturnMsg, min_volt_info, "Minimum Voltage", minTime, 0.1);
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

        if( max_demand_timestamp > _llpPeakInterest.time ||
            max_demand_timestamp < (_llpPeakInterest.time - _llpPeakInterest.period * 86400) )
        {
            result_string = "Peak timestamp (" + CtiTime(max_demand_timestamp).asString() + ") outside of requested range - retry report";
            status = NOTNORMAL;
        }
        else
        {
            switch( _llpPeakInterest.command )
            {
                case FuncRead_LLPPeakDayPos:
                {
                    result_string += "Peak day: " + CtiTime(max_demand_timestamp).asString() + "\n";
                    result_string += "Usage:  " + CtiNumStr(max_usage, 1) + string(" kWH\n");
                    result_string += "Demand: " + CtiNumStr(max_usage / 24, 2) + string(" kW\n");
                    result_string += "Average daily usage over range: " + CtiNumStr(avg_daily, 1) + string(" kWH\n");
                    result_string += "Total usage over range: " + CtiNumStr(total_usage, 1) + string(" kWH\n");

                    break;
                }
                case FuncRead_LLPPeakHourPos:
                {
                    result_string += "Peak hour: " + CtiTime(max_demand_timestamp).asString() + "\n";
                    result_string += "Usage:  " + CtiNumStr(max_usage, 1) + string(" kWH\n");
                    result_string += "Demand: " + CtiNumStr(max_usage, 1) + string(" kW\n");
                    result_string += "Average daily usage over range: " + CtiNumStr(avg_daily, 1) + string(" kWH\n");
                    result_string += "Total usage over range: " + CtiNumStr(total_usage, 1) + string(" kWH\n");

                    break;
                }
                case FuncRead_LLPPeakIntervalPos:
                {
                    int intervals_per_hour = 3600 / getLoadProfile().getLoadProfileDemandRate();

                    result_string += "Peak interval: " + CtiTime(max_demand_timestamp).asString() + "\n";
                    result_string += "Usage:  " + CtiNumStr(max_usage, 1) + string(" kWH\n");
                    result_string += "Demand: " + CtiNumStr(max_usage * intervals_per_hour, 1) + string(" kW\n");
                    result_string += "Average daily usage over range: " + CtiNumStr(avg_daily, 1) + string(" kWH\n");
                    result_string += "Total usage over range: " + CtiNumStr(total_usage, 1) + string(" kWH\n");

                    break;
                }
            }
        }

        ReturnMsg->setResultString(result_string);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetValueDailyRead(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    string valReport, resultString;
    bool   expectMore = false;

    int function = InMessage->Return.ProtocolInfo.Emetcon.Function;

    unsigned long start_time = 0;

    CtiReturnMsg    *ReturnMsg = NULL;  // Message sent to VanGogh, inherits from Multi

    //  add error handling for automated load profile retrieval... !
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

        //  if we succeeded, we should be okay for successive reads...
        _daily_read_info.retry = false;

        string demand_pointname, consumption_pointname;
        point_info pi;

        int expected_channel;

        //  assign the channel first...
        switch( _daily_read_info.current_request )
        {
            case daily_read_info_t::Request_MultiDay:       expected_channel = _daily_read_info.channel;  break;

            case daily_read_info_t::Request_RecentCh1:      expected_channel = 1;    break;

            case daily_read_info_t::Request_SingleDayCh1:   expected_channel = 1;    break;
            case daily_read_info_t::Request_SingleDayCh2:   expected_channel = 2;    break;
            case daily_read_info_t::Request_SingleDayCh3:   expected_channel = 3;    break;
        }

        //  then figure out the time this read started...
        switch( _daily_read_info.current_request )
        {
            case daily_read_info_t::Request_MultiDay:       start_time = _daily_read_info.multi_day_start;  break;

            case daily_read_info_t::Request_SingleDayCh1:
            case daily_read_info_t::Request_SingleDayCh2:
            case daily_read_info_t::Request_SingleDayCh3:   start_time = _daily_read_info.single_day;       break;

            case daily_read_info_t::Request_RecentCh1:
            {
                //  today's midnight minus however many days back we requested
                start_time = CtiTime(CtiDate()).seconds() - ((function + 1 - FuncRead_Channel1SingleDayBasePos) * 86400);

                break;
            }
        }

        int day, month;
        point_info reading, peak;
        int outage_count, voltage_min, voltage_max;
        int channel;

        int time_voltage_min, time_voltage_max, time_peak;

        if( expected_channel == 1 )
        {
            demand_pointname      = "kW";
            consumption_pointname = "kWh";
        }
        else
        {
            demand_pointname      = "Channel " + CtiNumStr(expected_channel) + " demand";
            consumption_pointname = "Channel " + CtiNumStr(expected_channel) + " consumption";
        }

        //  then group them by their decode type
        switch( _daily_read_info.current_request )
        {
            case daily_read_info_t::Request_MultiDay:
            {
                bool success;
                int day = 0, base_reading = 0, delta = 0;

                //  I process the multi-day values in reverse from how I want to return them,
                //    so I'm using this vector as temporary LIFO storage
                vector<point_info> days;

                for( day = 0; day < 5  && (DSt->Message[(day * 2) + 0] & 0x3f) == 0x3f
                                       &&  DSt->Message[(day * 2) + 1]         == 0xfa; day++ )
                {
                    //  although it's an error value, this is the first delta value, so grab the channel bits off the top
                    if( day == 0 )
                    {
                        channel = (DSt->Message[0] & 0xc0) >> 6;
                    }

                    pi = getData(DSt->Message + (day * 2), 2, ValueType_AccumulatorDelta);

                    days.push_back(pi);
                }

                pi = CtiDeviceMCT4xx::getData(DSt->Message + (day * 2), 3, ValueType_Accumulator);

                base_reading = pi.value;

                days.push_back(pi);

                day++;

                for( ; day < 6; day++ )
                {
                    //  no error, so this is the first delta value - grab the channel bits off the top
                    if( day == 1 )
                    {
                        channel = (DSt->Message[3] & 0xc0) >> 6;
                    }

                    pi = getData(DSt->Message + (day * 2) + 1, 2, ValueType_AccumulatorDelta);

                    if( pi.quality != InvalidQuality )
                    {
                        delta += pi.value;

                        pi.value = base_reading - delta;
                    }

                    days.push_back(pi);
                }

                channel++;

                if( channel != expected_channel )
                {
                    resultString  = getName() + " / Invalid channel returned by daily read ";
                    resultString += "(" + CtiNumStr(channel) + ", expecting " + CtiNumStr(expected_channel) + ")";

                    //  reset the "in progress" flag
                    _daily_read_info.current_request = daily_read_info_t::Request_None;
                    InterlockedExchange(&_daily_read_info.in_progress, false);
                }
                else
                {
                    for( day = 0; day < 6; day++ )
                    {
                        insertPointDataReport(PulseAccumulatorPointType, _daily_read_info.channel, ReturnMsg,
                                              days.back(), consumption_pointname, _daily_read_info.multi_day_start + (day * 86400));

                        days.pop_back();
                    }

                    if( (_daily_read_info.multi_day_start + (6 * 86400)) < _daily_read_info.multi_day_end )
                    {
                        string request_str = "getvalue daily read ";

                        request_str += "channel " + CtiNumStr(_daily_read_info.channel) + " " + printable_date(_daily_read_info.multi_day_start + 6 * 86400)
                                                                                              + " " + printable_date(_daily_read_info.multi_day_end);

                        if( strstr(InMessage->Return.CommandStr, " noqueue") )  request_str += " noqueue";

                        expectMore = true;
                        CtiRequestMsg newReq(getID(),
                                             request_str,
                                             InMessage->Return.UserID,
                                             0,
                                             InMessage->Return.RouteID,
                                             InMessage->Return.MacroOffset,
                                             0,
                                             0,
                                             InMessage->Priority);

                        newReq.setConnectionHandle((void *)InMessage->Return.Connection);

                        //  reset the "in progress" flag
                        _daily_read_info.current_request = daily_read_info_t::Request_None;
                        InterlockedExchange(&_daily_read_info.in_progress, false);

                        CtiDeviceBase::ExecuteRequest(&newReq, CtiCommandParser(newReq.CommandString()), vgList, retList, outList);
                    }
                    else
                    {
                        resultString += "Multi-day daily read request complete\n";

                        _daily_read_info.multi_day_start = 0;
                        _daily_read_info.multi_day_end   = 0;

                        //  reset the "in progress" flag
                        _daily_read_info.current_request = daily_read_info_t::Request_None;
                        InterlockedExchange(&_daily_read_info.in_progress, false);
                    }
                }

                break;
            }

            case daily_read_info_t::Request_SingleDayCh1:
            {
                month = (DSt->Message[8] & 0xc0) >> 6;  //  2 bits
                day   = (DSt->Message[8] & 0x3e) >> 1;  //  5 bits

                CtiDate d = CtiDate(CtiTime(_daily_read_info.single_day));

                if( day != d.dayOfMonth() || (month % 4) != ((d.month() - 1) % 4) )
                {
                    resultString  = getName() + " / Invalid day/month returned by daily read ";
                    resultString += "(" + CtiNumStr(day) + "/" + CtiNumStr(month + 1) + ", expecting " + CtiNumStr(d.dayOfMonth()) + "/" + CtiNumStr(((d.month() - 1) % 4) + 1) + ")";

                    _daily_read_info.single_day = 86400;  //  reset it - it doesn't match what the MCT has
                }
                else
                {
                    reading = CtiDeviceMCT4xx::getData(DSt->Message + 0, 3, ValueType_Accumulator);

                    insertPointDataReport(PulseAccumulatorPointType, 1, ReturnMsg,
                                          reading, consumption_pointname,  _daily_read_info.single_day + 86400);  //  add on 24 hours - end of day

                    peak    = getData(DSt->Message + 3, 2, ValueType_DynamicDemand);

                    //  adjust for the demand interval
                    peak.value *= 3600 / getDemandInterval();

                    time_peak        = ((DSt->Message[10] & 0xc0) >>  5) | //  2 bits
                                       ((DSt->Message[9]  & 0xff) <<  2) | //  8 bits
                                       ((DSt->Message[8]  & 0x01) << 10);  //  1 bit

                    insertPointDataReport(DemandAccumulatorPointType, 1, ReturnMsg,
                                          peak, demand_pointname,  _daily_read_info.single_day + (time_peak * 60));

                    voltage_min  = DSt->Message[7] | ((DSt->Message[6] & 0x0f) << 8);

                    voltage_max  = ((DSt->Message[6] & 0xf0) >> 4) | (DSt->Message[5] << 4);

                    time_voltage_min = ((DSt->Message[12] & 0xff) <<  0) | //  8 bits
                                       ((DSt->Message[11] & 0x07) <<  8);  //  3 bits

                    time_voltage_max = ((DSt->Message[11] & 0xf8) >>  3) | //  5 bits
                                       ((DSt->Message[10] & 0x3f) <<  5);  //  6 bits

                    if( voltage_min == 0x7fa )
                    {
                        pi.value   = 0;
                        pi.quality = InvalidQuality;
                        pi.description = "Requested interval outside of valid range";
                    }
                    else
                    {
                        pi.value   = voltage_min;
                        pi.quality = NormalQuality;
                    }

                    insertPointDataReport(DemandAccumulatorPointType, PointOffset_VoltageMin, ReturnMsg,
                                          pi, "Minimum Voltage", _daily_read_info.single_day + (time_voltage_min * 60), 0.1);

                    if( voltage_max == 0x7fa )
                    {
                        pi.value   = 0;
                        pi.quality = InvalidQuality;
                        pi.description = "Requested interval outside of valid range";
                    }
                    else
                    {
                        pi.value   = voltage_max;
                        pi.quality = NormalQuality;
                    }

                    insertPointDataReport(DemandAccumulatorPointType, PointOffset_VoltageMax, ReturnMsg,
                                          pi, "Maximum Voltage", _daily_read_info.single_day + (time_voltage_max * 60), 0.1);
                }

                //  reset the "in progress" flag
                _daily_read_info.current_request = daily_read_info_t::Request_None;
                InterlockedExchange(&_daily_read_info.in_progress, false);

                break;
            }

            case daily_read_info_t::Request_SingleDayCh2:
            case daily_read_info_t::Request_SingleDayCh3:
            case daily_read_info_t::Request_RecentCh1:
            {
                day     =  DSt->Message[9];
                month   =  DSt->Message[10] & 0x0f;
                channel = (DSt->Message[10] & 0x30) >> 4;

                CtiTime t = CtiTime(start_time);
                CtiDate d = CtiDate(t);
                channel++;  //  change to 1-based

                if( channel != expected_channel )
                {
                    resultString  = getName() + " / Invalid channel returned by daily read ";
                    resultString += "(" + CtiNumStr(channel) + "), expecting (" + CtiNumStr(expected_channel) + ")";
                }
                else if( day != d.dayOfMonth() || (month % 4) != ((d.month() - 1) % 4) )
                {
                    resultString  = getName() + " / Invalid day/month returned by daily read ";
                    resultString += "(" + CtiNumStr(day) + "/" + CtiNumStr(month + 1) + ", expecting " + CtiNumStr(d.dayOfMonth()) + "/" + CtiNumStr(((d.month() - 1) % 4) + 1) + ")";

                    if( _daily_read_info.current_request == daily_read_info_t::Request_SingleDayCh2 ||
                        _daily_read_info.current_request == daily_read_info_t::Request_SingleDayCh3 )
                    {
                        _daily_read_info.single_day = 86400;  //  reset it - it doesn't match what the MCT has
                    }
                }
                else
                {
                    reading = CtiDeviceMCT4xx::getData(DSt->Message + 0, 3, ValueType_Accumulator);

                    peak    = getData(DSt->Message + 3, 2, ValueType_DynamicDemand);

                    //  adjust for the demand interval
                    peak.value *= 3600 / getDemandInterval();

                    time_peak = (DSt->Message[5] << 8) | DSt->Message[6];

                    if( channel > 1
                        && !getDevicePointOffsetTypeEqual(channel, PulseAccumulatorPointType)
                        && !getDevicePointOffsetTypeEqual(channel, DemandAccumulatorPointType) )
                    {
                        resultString += "No points defined for channel " + CtiNumStr(channel) + "\n";
                    }
                    else
                    {
                        insertPointDataReport(PulseAccumulatorPointType, channel, ReturnMsg,
                                              reading, consumption_pointname,  start_time + 86400);  //  add on 24 hours - end of day

                        insertPointDataReport(DemandAccumulatorPointType, channel, ReturnMsg,
                                              peak, demand_pointname,  start_time + (time_peak * 60));
                    }

                    outage_count = (DSt->Message[7] << 8) | DSt->Message[8];

                    pi.value   = outage_count;
                    pi.quality = NormalQuality;

                    insertPointDataReport(PulseAccumulatorPointType, PointOffset_Accumulator_Powerfail, ReturnMsg,
                                          pi, "Blink Counter",  start_time + 86400);  //  add on 24 hours - end of day
                }

                //  reset the "in progress" flag
                _daily_read_info.current_request = daily_read_info_t::Request_None;
                InterlockedExchange(&_daily_read_info.in_progress, false);

                break;
            }

            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - !single_day == 0 && !multi_day_start **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                //  reset the "in progress" flag
                _daily_read_info.current_request = daily_read_info_t::Request_None;
                InterlockedExchange(&_daily_read_info.in_progress, false);

                break;
            }
        }

        //  this is gross
        if( !ReturnMsg->ResultString().empty() )
        {
            resultString = ReturnMsg->ResultString() + "\n" + resultString;
        }

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList, expectMore );
    }
    else
    {
        //  this code is replicated in ErrorDecode()
        if( !_daily_read_info.retry && _daily_read_info.current_request == daily_read_info_t::Request_MultiDay )
        {
            _daily_read_info.retry = true;

            string request_str = "getvalue daily read ";

            request_str += "channel " + CtiNumStr(_daily_read_info.channel) + " " + printable_date(_daily_read_info.multi_day_start)
                                                                            + " " + printable_date(_daily_read_info.multi_day_end);

            if( strstr(InMessage->Return.CommandStr, " noqueue") )      request_str += " noqueue";

            expectMore = true;
            CtiRequestMsg newReq(getID(),
                                 request_str,
                                 InMessage->Return.UserID,
                                 0,
                                 InMessage->Return.RouteID,
                                 InMessage->Return.MacroOffset,
                                 0,
                                 0,
                                 InMessage->Priority);

            newReq.setConnectionHandle((void *)InMessage->Return.Connection);

            //  reset the "in progress" flag
            _daily_read_info.current_request = daily_read_info_t::Request_None;
            InterlockedExchange(&_daily_read_info.in_progress, false);

            CtiDeviceBase::ExecuteRequest(&newReq, CtiCommandParser(newReq.CommandString()), vgList, retList, outList);
        }
        else
        {
            _daily_read_info.failed = true;
            _daily_read_info.retry  = false;

            //  reset the "in progress" flag
            _daily_read_info.current_request = daily_read_info_t::Request_None;
            InterlockedExchange(&_daily_read_info.in_progress, false);
        }
    }

    return status;
}


string CtiDeviceMCT410::describeStatusAndEvents(unsigned char *buf)
{
    string descriptor;

    if( buf )
    {
        // point offset 10
        descriptor += (buf[1] & 0x01)?"Power Fail occurred\n":"";
        descriptor += (buf[1] & 0x02)?"Under-Voltage Event\n":"";
        descriptor += (buf[1] & 0x04)?"Over-Voltage Event\n":"";
        descriptor += (buf[1] & 0x08)?"Power Fail Carryover\n":"";
        descriptor += (buf[1] & 0x10)?"RTC Adjusted\n":"";
        descriptor += (buf[1] & 0x20)?"Holiday Event occurred\n":"";
        descriptor += (buf[1] & 0x40)?"DST Change occurred\n":"";
        descriptor += (buf[1] & 0x80)?"Tamper Flag set\n":"";

        //  point offset 20
        descriptor += (buf[2] & 0x01)?"Zero usage stored for 24 hours\n":"";
        descriptor += (buf[2] & 0x02)?"Disconnect error (demand seen after disconnect)\n":"";
        descriptor += (buf[2] & 0x04)?"Last meter reading corrupt\n":"";
        //  0x08 - 0x80 aren't used yet

        //  starts at offset 30 - NOTE that this is byte 0
        descriptor += (buf[0] & 0x01)?"Group addressing disabled\n":"Group addressing enabled\n";
        descriptor += (buf[0] & 0x02)?"Phase detect in progress\n":"";
        descriptor += (buf[0] & 0x04)?"DST active\n":"DST inactive\n";
        descriptor += (buf[0] & 0x08)?"Holiday active\n":"";
        descriptor += (buf[0] & 0x10)?"TOU disabled\n":"TOU enabled\n";
        descriptor += (buf[0] & 0x20)?"Time sync needed\n":"In time sync\n";
        descriptor += (buf[0] & 0x40)?"Critical peak active\n":"";
        //  0x80 is not used yet
    }

    return descriptor;
}


INT CtiDeviceMCT410::decodeGetStatusInternal( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT &DSt = InMessage->Buffer.DSt;

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

        //  point offsets 10-39
        resultString += describeStatusAndEvents(DSt.Message);

        //  Eventually, we should exclude the iCon-specific bits from the Centron result

        //  point offset 40
        resultString += (DSt.Message[3] & 0x01)?"Soft kWh Error, Data OK\n":"";
        resultString += (DSt.Message[3] & 0x02)?"Low AC Volts\n":"";
        resultString += (DSt.Message[3] & 0x04)?"Current Too High\n":"";
        resultString += (DSt.Message[3] & 0x08)?"Power Failure\n":"";
        resultString += (DSt.Message[3] & 0x10)?"Hard EEPROM Error\n":"";
        resultString += (DSt.Message[3] & 0x20)?"Hard kWh Error, Data Lost\n":"";
        resultString += (DSt.Message[3] & 0x40)?"Configuration Error\n":"";
        resultString += (DSt.Message[3] & 0x80)?"Reverse Power\n":"";

        //  point offset 50
        resultString += (DSt.Message[4] & 0x01)?"7759 Calibration Error\n":"";
        resultString += (DSt.Message[4] & 0x02)?"7759 Register Check Error\n":"";
        resultString += (DSt.Message[4] & 0x04)?"7759 Reset Error\n":"";
        resultString += (DSt.Message[4] & 0x08)?"RAM Bit Error\n":"";
        resultString += (DSt.Message[4] & 0x10)?"General CRC Error\n":"";
        resultString += (DSt.Message[4] & 0x20)?"Soft EEPROM Error\n":"";
        resultString += (DSt.Message[4] & 0x40)?"Watchdog Restart\n":"";
        resultString += (DSt.Message[4] & 0x80)?"7759 Bit Checksum Error\n":"";

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
                if( (offset + j != 10) && (point = boost::static_pointer_cast<CtiPointStatus>(getDevicePointOffsetTypeEqual( offset + j, StatusPointType ))) )
                {
                    double value = (DSt.Message[i] >> j) & 0x01;

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
        int centron_multiplier = -1;

        if( InMessage->Sequence == Emetcon::GetConfig_CentronParameters )
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

            //  they did the long read, so assign the multiplier
            if( DSt->Length >= 11 )
            {
                centron_multiplier = DSt->Message[10];
            }
        }
        else if( InMessage->Sequence == Emetcon::GetConfig_Multiplier )
        {
            centron_multiplier = DSt->Message[0];
        }

        if( centron_multiplier >= 0 )
        {
            resultString += getName() + " / Centron Multiplier: " + CtiNumStr(centron_multiplier);
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
    INT status = NORMAL, state = 0;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    string resultStr;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

        resultStr  = getName() + " / Disconnect Config:\n";

        resultStr += "Load limit ";
        resultStr += (DSt->Message[0] & 0x04)?"active\n":"inactive\n";

        resultStr += "Disconnect status: ";

        switch( DSt->Message[0] & 0x03 )
        {
            case RawStatus_Connected:               resultStr += "connected\n";                 state = StateGroup_Connected;      break;
            case RawStatus_ConnectArmed:            resultStr += "connect armed\n";             state = StateGroup_ConnectArmed;   break;
            case RawStatus_DisconnectedUnconfirmed: resultStr += "unconfirmed disconnected\n";  state = StateGroup_DisconnectedUnconfirmed; break;
            case RawStatus_DisconnectedConfirmed:   resultStr += "confirmed disconnected\n";    state = StateGroup_DisconnectedConfirmed;   break;
        }

        point_info pi_disconnect;
        pi_disconnect.value   = state;
        pi_disconnect.quality = NormalQuality;

        insertPointDataReport(StatusPointType, 1, ReturnMsg, pi_disconnect, "Disconnect status", 0UL, 1.0, TAG_POINT_MUST_ARCHIVE);

        if( DSt->Message[1] & 0x02 )
        {
            resultStr += "Disconnect error - nonzero demand detected after disconnect command sent to collar\n";
        }

        long disconnectaddress = DSt->Message[2] << 16 |
                                 DSt->Message[3] <<  8 |
                                 DSt->Message[4];

        resultStr += "Disconnect receiver address: " + CtiNumStr(disconnectaddress) + string("\n");

        point_info pi = getData(DSt->Message + 5, 2, ValueType_DynamicDemand);

        //  adjust for the demand interval
        pi.value *= 3600 / getDemandInterval();
        //  adjust for the 0.1 kWh factor of getData()
        pi.value /= 10.0;

        resultStr += "Disconnect demand threshold: ";

        if( pi.value )
        {
            resultStr += CtiNumStr(pi.value) + string(" kW\n");
        }
        else
        {
            resultStr += "disabled\n";
        }

        resultStr += "Disconnect load limit connect delay: " + CtiNumStr(DSt->Message[7]) + string(" minutes\n");

        resultStr += "Disconnect load limit count: " + CtiNumStr(DSt->Message[8]) + string("\n");

        if( getDynamicInfo(Keys::Key_MCT_SSpecRevision) >= SspecRev_Disconnect_Cycle )
        {
            //  include the cycle information

            if( DSt->Message[9] || DSt->Message[10] )
            {
                resultStr += "Cycling mode - disconnect minutes: " + CtiNumStr(DSt->Message[9])  + string("\n");
                resultStr += "Cycling mode - connect minutes   : " + CtiNumStr(DSt->Message[10]) + string("\n");
            }
            else
            {
                resultStr += "Cycling mode inactive\n";
            }
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString(resultStr);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT410::decodeGetConfigAddress(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    string resultStr;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

        long address = DSt->Message[0] << 16 |
                       DSt->Message[1] <<  8 |
                       DSt->Message[2];

        resultStr  = getName() + " / Unique address: " + CtiNumStr(address);

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

    DSTRUCT &DSt = InMessage->Buffer.DSt;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!
        string descriptor;

        int  ssp;
        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

        ssp  = DSt.Message[0];
        ssp |= DSt.Message[4] << 8;

        descriptor += getName() + " / Model information:\n";
        descriptor += "Software Specification " + CtiNumStr(ssp) + " rev ";

        //  convert 10 to 1.0, 24 to 2.4
        descriptor += CtiNumStr(((double)DSt.Message[1]) / 10.0, 1);

        //  valid/released versions are 1.0 - 24.9
        if( DSt.Message[1] <   10 ||
            DSt.Message[1] >= 250 )
        {
            descriptor += " [possible development revision]";
        }

        descriptor += "\n";

        //  set the dynamic info for use later
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec,         (long)ssp);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, (long)DSt.Message[1]);

        descriptor += getName() + " / Physical meter configuration:\n";
        descriptor += "Base meter: ";

        switch( DSt.Message[2] & 0x03 )
        {
            case 0x00:  descriptor += "Sensus iCON";    break;
            case 0x01:  descriptor += "Itron Centron";  break;
            case 0x02:  descriptor += "L&G Focus";      break;
            case 0x03:  descriptor += "GE I-210";       break;
        }

        descriptor += "\n";

        if( DSt.Message[2] & 0x1c )
        {
            descriptor += "Channel 2: ";

            if( (DSt.Message[2] & 0x1c) == 0x1c )   descriptor += "Net metering mode";
            else                                    descriptor += "Water meter";

            descriptor += "\n";
        }

        if( DSt.Message[2] & 0xe0 )
        {
            descriptor += "Channel 3: Water meter\n";
        }

        if( DSt.Message[3] &  0x3f )
        {
            descriptor += getName() + " / Active options:\n";

            if( DSt.Message[3] & 0x01 )      descriptor += "DST observance enabled\n";
            if( DSt.Message[3] & 0x02 )      descriptor += "LED test enabled\n";
            if( DSt.Message[3] & 0x04 )      descriptor += "Reconnect button disabled\n";

            if(      DSt.Message[3] & 0x08 ) descriptor += "Demand limit mode active\n";
            else if( DSt.Message[3] & 0x10 ) descriptor += "Disconnect cycling mode active\n";

            if( DSt.Message[3] & 0x20 )      descriptor += "Role code enabled\n";
        }

        descriptor += getName() + " / Status and events:\n";

        descriptor += describeStatusAndEvents(DSt.Message + 5);

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString(descriptor);

        //  this is hackish, and should be handled in a more centralized manner...  retMsgHandler, for example
        if( InMessage->MessageFlags & MessageFlag_ExpectMore )
        {
            ReturnMsg->setExpectMore(true);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}

