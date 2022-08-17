#include "precompiled.h"

#include "logger.h"
#include "numstr.h"
#include "dllyukon.h"  //  for ResolveStateName()
#include "date_utility.h"
#include "utility.h"
#include "config_data_mct.h"
#include "config_helpers.h"

#include "dev_mct410.h"
#include "dev_mct410_commands.h"

#include "tbl_ptdispatch.h"
#include "pt_status.h"

#include "portglob.h"

#include "std_helper.h"

#include <boost/assign/list_of.hpp>

#include <stack>

using namespace std;

using namespace Cti::Config;
using namespace Cti::Devices::Commands;
using namespace Cti::Protocols;

namespace Cti {
namespace Devices {

const Mct410Device::CommandSet       Mct410Device::_commandStore = Mct410Device::initCommandStore();
const Mct410Device::ConfigPartsList  Mct410Device::_config_parts = Mct410Device::initConfigParts();

const Mct410Device::ValueMapping              Mct410Device::_memoryMap             = Mct410Device::initMemoryMap();
const Mct410Device::FunctionReadValueMappings Mct410Device::_functionReadValueMaps = Mct410Device::initFunctionReadValueMaps();

namespace {

const std::map<std::string, bool> ReconnectButtonLookup = boost::assign::map_list_of
    ( "ARM",       true )
    ( "IMMEDIATE", false );

typedef Commands::Mct410DisconnectConfigurationCommand Disc;

typedef boost::bimap<Disc::DisconnectMode, std::string> DisconnectModeStrings;

const DisconnectModeStrings DisconnectModes = boost::assign::list_of< DisconnectModeStrings::relation >
    (Disc::OnDemand,        "ON_DEMAND")
    (Disc::DemandThreshold, "DEMAND_THRESHOLD")
    (Disc::Cycling,         "CYCLING");
}


Mct410Device::Mct410Device( ) :
    _intervalsSent(false)
{
    _daily_read_info.request.multi_day_retries = -1;
    _daily_read_info.request.in_progress = false;
    _daily_read_info.request.channel = -1;
    _daily_read_info.request.type = daily_read_info_t::Request_None;
    _daily_read_info.request.user_id = INT_MIN;

    _daily_read_info.interest.date = DawnOfTime_Date;
    _daily_read_info.interest.needs_verification = false;
}

void Mct410Device::setDisconnectAddress( unsigned long address )
{
    _disconnectAddress = address;
}

Mct410Device::ValueMapping Mct410Device::initMemoryMap()
{
    struct memory_read_value
    {
        unsigned offset;
        value_descriptor value;
    }
    const values[] =
    {
        { Memory_SspecPos,                 { Memory_SspecLen,                CtiTableDynamicPaoInfo::Key_MCT_SSpec                    } },
        { Memory_RevisionPos,              { Memory_RevisionLen,             CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision            } },

        { Memory_DayOfScheduledFreezePos,  { Memory_DayOfScheduledFreezeLen, CtiTableDynamicPaoInfo::Key_MCT_ScheduledFreezeDay       } },

        { Memory_OptionsPos,               { Memory_OptionsLen,              CtiTableDynamicPaoInfo::Key_MCT_Options                  } },
        { Memory_ConfigurationPos,         { Memory_ConfigurationLen,        CtiTableDynamicPaoInfo::Key_MCT_Configuration            } },

        { Memory_EventFlagsMask1Pos,       { Memory_EventFlagsMask1Len,      CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1          } },
        { Memory_EventFlagsMask2Pos,       { Memory_EventFlagsMask2Len,      CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2          } },
        { Memory_MeterAlarmMaskPos,        { Memory_MeterAlarmMaskLen,       CtiTableDynamicPaoInfo::Key_MCT_MeterAlarmMask           } },

        { Memory_BronzeAddressPos,         { Memory_BronzeAddressLen,        CtiTableDynamicPaoInfo::Key_MCT_AddressBronze            } },
        { Memory_LeadAddressPos,           { Memory_LeadAddressLen,          CtiTableDynamicPaoInfo::Key_MCT_AddressLead              } },
        { Memory_CollectionAddressPos,     { Memory_CollectionAddressLen,    CtiTableDynamicPaoInfo::Key_MCT_AddressCollection        } },
        { Memory_SPIDAddressPos,           { Memory_SPIDAddressLen,          CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID } },

        { Memory_DemandIntervalPos,        { Memory_DemandIntervalLen,       CtiTableDynamicPaoInfo::Key_MCT_DemandInterval           } },
        { Memory_LoadProfileIntervalPos,   { Memory_LoadProfileIntervalLen,  CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval      } },
        { Memory_VoltageDemandIntervalPos, { Memory_VoltageDemandIntervalLen, CtiTableDynamicPaoInfo::Key_MCT_VoltageDemandInterval   } },
        { Memory_VoltageLPIntervalPos,     { Memory_VoltageLPIntervalLen,    CtiTableDynamicPaoInfo::Key_MCT_VoltageLPInterval        } },

        { Memory_OverVThresholdPos,        { Memory_OverVThresholdLen,       CtiTableDynamicPaoInfo::Key_MCT_OverVoltageThreshold     } },
        { Memory_UnderVThresholdPos,       { Memory_UnderVThresholdLen,      CtiTableDynamicPaoInfo::Key_MCT_UnderVoltageThreshold    } },

        { Memory_OutageCyclesPos,          { Memory_OutageCyclesLen,         CtiTableDynamicPaoInfo::Key_MCT_OutageCycles             } },

        { Memory_TimeAdjustTolPos,         { Memory_TimeAdjustTolLen,        CtiTableDynamicPaoInfo::Key_MCT_TimeAdjustTolerance      } },

        { Memory_DSTBeginPos,              { Memory_DSTBeginLen,             CtiTableDynamicPaoInfo::Key_MCT_DSTStartTime             } },
        { Memory_DSTEndPos,                { Memory_DSTEndLen,               CtiTableDynamicPaoInfo::Key_MCT_DSTEndTime               } },
        { Memory_TimeZoneOffsetPos,        { Memory_TimeZoneOffsetLen,       CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset           } },

        { Memory_TOUDayTablePos,           { Memory_TOUDayTableLen,          CtiTableDynamicPaoInfo::Key_MCT_DayTable                 } },

        { Memory_Holiday1Pos,              { Memory_Holiday1Len,             CtiTableDynamicPaoInfo::Key_MCT_Holiday1                 } },
        { Memory_Holiday2Pos,              { Memory_Holiday2Len,             CtiTableDynamicPaoInfo::Key_MCT_Holiday2                 } },
        { Memory_Holiday3Pos,              { Memory_Holiday3Len,             CtiTableDynamicPaoInfo::Key_MCT_Holiday3                 } },

        { Memory_DisplayParametersPos,     { Memory_DisplayParametersLen,    CtiTableDynamicPaoInfo::Key_MCT_DisplayParameters        } },
        { Memory_TransformerRatioPos,      { Memory_TransformerRatioLen,     CtiTableDynamicPaoInfo::Key_MCT_TransformerRatio         } },

        { Memory_WaterMeterReadIntervalPos, { Memory_WaterMeterReadIntervalLen, CtiTableDynamicPaoInfo::Key_MCT_WaterMeterReadInterval  } },
    };

    ValueMapping memoryMap;

    for each( memory_read_value mrv in values )
    {
        memoryMap.insert(make_pair(mrv.offset,  mrv.value));
    }

    return memoryMap;
}

Mct410Device::FunctionReadValueMappings Mct410Device::initFunctionReadValueMaps()
{
    struct function_read_value
    {
        unsigned function;
        unsigned offset;
        value_descriptor value;
    }
    const values[] =
    {
        { FuncRead_TOUDaySchedulePos,    0, { 2, CtiTableDynamicPaoInfo::Key_MCT_DayTable          } },
        { FuncRead_TOUDaySchedulePos,    2, { 1, CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate    } },
        { FuncRead_TOUDaySchedulePos,   10, { 1, CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset    } },

        { FuncRead_LLPStatusPos,         4, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel1Len    } },
        { FuncRead_LLPStatusPos,         5, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel2Len    } },
        { FuncRead_LLPStatusPos,         6, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel3Len    } },
        { FuncRead_LLPStatusPos,         7, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len    } },

        //{ FuncRead_DisconnectConfigPos,  5, { 2, CtiTableDynamicPaoInfo::Key_MCT_DemandThreshold   } },  //  stored by Mct410DisconnectConfigurationCommand
        { FuncRead_DisconnectConfigPos,  7, { 1, CtiTableDynamicPaoInfo::Key_MCT_ConnectDelay      } },
        { FuncRead_DisconnectConfigPos,  9, { 1, CtiTableDynamicPaoInfo::Key_MCT_DisconnectMinutes } },
        { FuncRead_DisconnectConfigPos, 10, { 1, CtiTableDynamicPaoInfo::Key_MCT_ConnectMinutes    } },
        { FuncRead_DisconnectConfigPos, 11, { 1, CtiTableDynamicPaoInfo::Key_MCT_Configuration     } },
    };

    FunctionReadValueMappings fr;

    for each( function_read_value frv in values )
    {
        fr[frv.function].insert(make_pair(frv.offset, frv.value));
    }

    return fr;
}


Mct410Device::ConfigPartsList Mct410Device::initConfigParts()
{
    Mct410Device::ConfigPartsList tempList;

    tempList.push_back(Mct4xxDevice::PutConfigPart_disconnect);
    tempList.push_back(Mct4xxDevice::PutConfigPart_freeze_day);
    tempList.push_back(Mct4xxDevice::PutConfigPart_timezone);

    return tempList;
}

Mct410Device::ConfigPartsList Mct410Device::getPartsList()
{
    return _config_parts;
}

int Mct410Device::Utility::makeDynamicDemand(double input)
{
    /*
    Bits   Resolution            Range
    13-12
    00     100   WHr   40,100.0 WHr - 409,500.0  WHr
    01      10   WHr    4,010.0 WHr -  40,000.0  WHr
    10       1   WHr      401.0 WHr -   4,000.0  WHr
    11       0.1 WHr        0.0 WHr -     400.0  WHr
    */

    int output;
    int resolution;
    float divisor;

    if( input > 409500.0 || input < 0.0 )
    {
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

        if( fmod( (double) input, (double) divisor ) >= (divisor / 2.0) )
        {
            output++;
        }

        output |= resolution << 12;
    }

    return output;
}


Mct410Device::frozen_point_info Mct410Device::getDemandData(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const
{
    return freeze_counter ?
        getData(buf, len, ValueType_FrozenDynamicDemand) :
        getData(buf, len, ValueType_DynamicDemand);
}


Mct410Device::frozen_point_info Mct410Device::getAccumulatorData(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const
{
    return Mct410Device::decodePulseAccumulator(buf, len, freeze_counter);
}


Mct410Device::frozen_point_info Mct410Device::decodePulseAccumulator(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter)
{
    frozen_point_info pi = Mct4xxDevice::decodePulseAccumulator(buf, len, freeze_counter);

    const long value = static_cast<long>(pi.value);

    pi.freeze_bit  = value &  0x01;
    pi.value       = value & ~0x01;

    return pi;
}


Mct4xxDevice::frozen_point_info Mct410Device::getData(const unsigned char *buf, const unsigned len, const ValueType410 vt) const
{
    PointQuality_t quality = NormalQuality;
    unsigned long error_code = 0xffffffff,  //  filled with 0xff because some data types are less than 32 bits
                  min_error  = 0xffffffff;
    unsigned char quality_flags = 0,
                  resolution    = 0;
    unsigned char error_byte, value_byte;

    string description;
    long long value = 0;
    frozen_point_info  retval;

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
                vt == ValueType_FrozenDynamicDemand ||
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

    if( vt == ValueType_FrozenDynamicDemand )
    {
        //  clear the bottom bit - that's the freeze bit
        value &= ~1;
    }

    switch( vt )
    {
        case ValueType_Voltage:                     min_error = 0xffffffe0; break;

        case ValueType_OutageCount:                 min_error = 0xfffffffa; break;

        case ValueType_AccumulatorDelta:
        case ValueType_DynamicDemand:
        case ValueType_FrozenDynamicDemand:
        case ValueType_LoadProfile_DynamicDemand:
        case ValueType_LoadProfile_Voltage:         min_error = 0xffffffa1; break;
    }

    if( error_code >= min_error )
    {
        return getDataError(error_code, error_codes);
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

    if( vt == ValueType_DynamicDemand ||
        vt == ValueType_FrozenDynamicDemand ||
        vt == ValueType_LoadProfile_DynamicDemand )
    {
        CTILOG_TRACE(dout, "demand value "<< (unsigned long)value <<" resolution "<< (int)resolution);

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


Mct410Device::point_info Mct410Device::getLoadProfileData(unsigned channel, long interval_len, const unsigned char *buf, unsigned len)
{
    point_info pi;

    // input channel is 0-based, enums are 1-based
    channel++;

    if( channel <= ChannelCount )
    {
        pi = getData(buf, len, ValueType_LoadProfile_DynamicDemand);

        if( interval_len <= 0 )
        {
            CTILOG_ERROR(dout, "invalid interval_len ("<< interval_len <<") for device \""<< getName() <<"\"");

            pi.quality = InvalidQuality;
            pi.description = "Invalid demand interval, cannot adjust reading";
        }
        else
        {
            pi.value *= 3600 / interval_len;
        }
    }
    else if( channel == Channel_Voltage )
    {
        pi = getData(buf, len, ValueType_LoadProfile_Voltage);
    }
    else
    {
        CTILOG_ERROR(dout, "invalid channel = "<< channel <<" for device \""<< getName() <<"\"");

        pi = Mct4xxDevice::getData(buf, len, ValueType_Raw);
    }

    return pi;
}


Mct410Device::CommandSet Mct410Device::initCommandStore()
{
    CommandSet cs;

    cs.insert(CommandStore(EmetconProtocol::Scan_Accum,                     EmetconProtocol::IO_Function_Read, FuncRead_MReadPos,             FuncRead_MReadLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_KWH,                   EmetconProtocol::IO_Function_Read, FuncRead_MReadPos,             FuncRead_MReadLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_FrozenKWH,             EmetconProtocol::IO_Function_Read, FuncRead_FrozenMReadPos,       FuncRead_FrozenMReadLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_TOUkWh,                EmetconProtocol::IO_Function_Read, FuncRead_TOUkWhPos,            FuncRead_TOUkWhLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_FrozenTOUkWh,          EmetconProtocol::IO_Function_Read, FuncRead_TOUkWhFrozenPos,      FuncRead_TOUkWhFrozenLen));
    cs.insert(CommandStore(EmetconProtocol::Scan_Integrity,                 EmetconProtocol::IO_Function_Read, FuncRead_DemandPos,            FuncRead_DemandLen));
    cs.insert(CommandStore(EmetconProtocol::Scan_LoadProfile,               EmetconProtocol::IO_Function_Read, 0,                             0));
    cs.insert(CommandStore(EmetconProtocol::GetValue_LoadProfile,           EmetconProtocol::IO_Function_Read, 0,                             0));
    cs.insert(CommandStore(EmetconProtocol::GetValue_LoadProfilePeakReport, EmetconProtocol::IO_Function_Read, 0,                             0));
    cs.insert(CommandStore(EmetconProtocol::GetValue_Demand,                EmetconProtocol::IO_Function_Read, FuncRead_DemandPos,            FuncRead_DemandLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_PeakDemand,            EmetconProtocol::IO_Function_Read, FuncRead_PeakDemandPos,        FuncRead_PeakDemandLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_FrozenPeakDemand,      EmetconProtocol::IO_Function_Read, FuncRead_FrozenPeakDemandPos,  FuncRead_FrozenPeakDemandLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_Voltage,               EmetconProtocol::IO_Function_Read, FuncRead_VoltagePos,           FuncRead_VoltageLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_FrozenVoltage,         EmetconProtocol::IO_Function_Read, FuncRead_FrozenVoltagePos,     FuncRead_FrozenVoltageLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_Outage,                EmetconProtocol::IO_Function_Read, FuncRead_OutagePos,            FuncRead_OutageLen));
    cs.insert(CommandStore(EmetconProtocol::GetStatus_Internal,             EmetconProtocol::IO_Read,          Memory_StatusPos,              Memory_StatusLen));
    cs.insert(CommandStore(EmetconProtocol::GetStatus_LoadProfile,          EmetconProtocol::IO_Function_Read, FuncRead_LPStatusPos,          FuncRead_LPStatusLen));
    cs.insert(CommandStore(EmetconProtocol::GetStatus_Freeze,               EmetconProtocol::IO_Read,          Memory_LastFreezeTimestampPos, Memory_LastFreezeTimestampLen
                                                                                                                              + Memory_FreezeCounterLen
                                                                                                                              + Memory_LastVoltageFreezeTimestampLen
                                                                                                                              + Memory_VoltageFreezeCounterLen));

    //  These need to be duplicated from DeviceMCT because the 400 doesn't need the ARML.
    cs.insert(CommandStore(EmetconProtocol::Control_Connect,            EmetconProtocol::IO_Write,          Command_Connect,                0));
    cs.insert(CommandStore(EmetconProtocol::Control_Disconnect,         EmetconProtocol::IO_Write,          Command_Disconnect,             0));

    cs.insert(CommandStore(EmetconProtocol::GetStatus_Disconnect,       EmetconProtocol::IO_Function_Read,  FuncRead_DisconnectStatusPos,   FuncRead_DisconnectStatusLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Disconnect,       EmetconProtocol::IO_Function_Read,  FuncRead_DisconnectConfigPos,   FuncRead_DisconnectConfigLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Disconnect,       EmetconProtocol::IO_Function_Write, FuncWrite_DisconnectConfigPos,  FuncWrite_DisconnectConfigLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Raw,              EmetconProtocol::IO_Write,          0,                              0));  //  filled in later
    cs.insert(CommandStore(EmetconProtocol::GetConfig_TSync,            EmetconProtocol::IO_Read,           Memory_LastTSyncPos,            Memory_LastTSyncLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Time,             EmetconProtocol::IO_Read,           Memory_TimeZoneOffsetPos,       Memory_TimeZoneOffsetLen
                                                                                                                            + Memory_RTCLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_FreezeDay,        EmetconProtocol::IO_Write,          Memory_DayOfScheduledFreezePos, Memory_DayOfScheduledFreezeLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_TimeZoneOffset,   EmetconProtocol::IO_Write,          Memory_TimeZoneOffsetPos,       Memory_TimeZoneOffsetLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Intervals,        EmetconProtocol::IO_Function_Write, FuncWrite_IntervalsPos,         FuncWrite_IntervalsLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Intervals,        EmetconProtocol::IO_Read,           Memory_IntervalsPos,            Memory_IntervalsLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_OutageThreshold,  EmetconProtocol::IO_Write,          Memory_OutageCyclesPos,         Memory_OutageCyclesLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Thresholds,       EmetconProtocol::IO_Read,           Memory_ThresholdsPos,           Memory_ThresholdsLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_DailyReadInterest,EmetconProtocol::IO_Read,           Memory_DailyReadInterestPos,    Memory_DailyReadInterestLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_PFCount,           EmetconProtocol::IO_Read,           Memory_PowerfailCountPos,       Memory_PowerfailCountLen));
    cs.insert(CommandStore(EmetconProtocol::PutStatus_Reset,            EmetconProtocol::IO_Write,          Command_Reset,                  0));
    cs.insert(CommandStore(EmetconProtocol::PutStatus_ResetAlarms,      EmetconProtocol::IO_Write,          Memory_MeterAlarmsPos,          Memory_MeterAlarmsLen));
    cs.insert(CommandStore(EmetconProtocol::PutStatus_FreezeOne,        EmetconProtocol::IO_Write,          Command_FreezeOne,              0));
    cs.insert(CommandStore(EmetconProtocol::PutStatus_FreezeTwo,        EmetconProtocol::IO_Write,          Command_FreezeTwo,              0));
    cs.insert(CommandStore(EmetconProtocol::PutStatus_FreezeVoltageOne, EmetconProtocol::IO_Write,          Command_FreezeVoltageOne,       0));
    cs.insert(CommandStore(EmetconProtocol::PutStatus_FreezeVoltageTwo, EmetconProtocol::IO_Write,          Command_FreezeVoltageTwo,       0));

    cs.insert(CommandStore(EmetconProtocol::PutConfig_UniqueAddress,    EmetconProtocol::IO_Function_Write, FuncWrite_SetAddressPos,        FuncWrite_SetAddressLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_UniqueAddress,    EmetconProtocol::IO_Read,           Memory_UniqueAddressPos,        Memory_UniqueAddressLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Multiplier,       EmetconProtocol::IO_Read,           Memory_TransformerRatioPos,     Memory_TransformerRatioLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_MeterParameters,  EmetconProtocol::IO_Read,           Memory_DisplayParametersPos,    Memory_DisplayParametersLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Freeze,           EmetconProtocol::IO_Read,           Memory_DayOfScheduledFreezePos, Memory_DayOfScheduledFreezeLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_LoadProfileExistingPeak,
                                                                        EmetconProtocol::IO_Function_Read,  0,                              7));

    //******************************** Config Related starts here *************************
    cs.insert(CommandStore(EmetconProtocol::PutConfig_LongLoadProfileStorage,  EmetconProtocol::IO_Function_Write, FuncWrite_LLPStoragePos,        FuncWrite_LLPStorageLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_LongLoadProfileStorage,  EmetconProtocol::IO_Function_Read,  FuncRead_LLPStatusPos,          FuncRead_LLPStatusLen));

    cs.insert(CommandStore(EmetconProtocol::PutConfig_VThreshold,       EmetconProtocol::IO_Write,          Memory_OverVThresholdPos,       Memory_OverVThresholdLen +
                                                                                                                            Memory_UnderVThresholdLen));
    //  used by both the putconfig install and putconfig holiday commands
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Holiday,          EmetconProtocol::IO_Write,          Memory_Holiday1Pos,             Memory_Holiday1Len
                                                                                                                            + Memory_Holiday2Len
                                                                                                                            + Memory_Holiday3Len));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Holiday,          EmetconProtocol::IO_Read,           Memory_Holiday1Pos,             Memory_Holiday1Len
                                                                                                                            + Memory_Holiday2Len
                                                                                                                            + Memory_Holiday3Len));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Options,          EmetconProtocol::IO_Function_Write, FuncWrite_ConfigAlarmMaskPos,   FuncWrite_ConfigAlarmMaskLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_AutoReconnect,    EmetconProtocol::IO_Function_Write, FuncWrite_ConfigPos,            FuncWrite_ConfigLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Outage,           EmetconProtocol::IO_Write,          Memory_OutageCyclesPos,         Memory_OutageCyclesLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_TimeAdjustTolerance, EmetconProtocol::IO_Write,       Memory_TimeAdjustTolPos,        Memory_TimeAdjustTolLen));

    cs.insert(CommandStore(EmetconProtocol::GetConfig_PhaseDetect, EmetconProtocol::IO_Function_Read,       FuncRead_PhaseDetect,           FuncRead_PhaseDetectLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_PhaseDetect, EmetconProtocol::IO_Function_Write,      FuncWrite_PhaseDetect,          FuncWrite_PhaseDetectLen));

    cs.insert(CommandStore(EmetconProtocol::PutConfig_PhaseDetectClear,   EmetconProtocol::IO_Function_Write,    FuncWrite_PhaseDetectClear,             FuncWrite_PhaseDetectClearLen));

    //************************************ End Config related *****************************


    cs.insert(CommandStore(EmetconProtocol::PutConfig_WaterMeterReadInterval,   EmetconProtocol::IO_Write,
                           Memory_WaterMeterReadIntervalPos,                    Memory_WaterMeterReadIntervalLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_WaterMeterReadInterval,   EmetconProtocol::IO_Read,
                           Memory_WaterMeterReadIntervalPos,                    Memory_WaterMeterReadIntervalLen));

    cs.insert(CommandStore(EmetconProtocol::PutConfig_Channel2NetMetering,  EmetconProtocol::IO_Write,  Memory_OptionsPos,  Memory_OptionsLen));

    return cs;
}


long Mct410Device::getLoadProfileInterval( unsigned channel )
{
    int retval = 3600;

    //  input channel is 0-based, enums are 1-based
    channel++;

    if( channel == Channel_Voltage )
    {
        retval = getLoadProfile()->getVoltageProfileRate();
    }
    else if( channel <= ChannelCount )
    {
        retval = getLoadProfile()->getLoadProfileDemandRate();
    }
    else
    {
        CTILOG_ERROR(dout, "invalid channel = "<< channel <<" for device \""<< getName() <<"\"");

        retval = 3600;
    }

    return retval;
}


bool Mct410Device::getOperation( const UINT &cmd, BSTRUCT &bst ) const
{
    if( getOperationFromStore(_commandStore, cmd, bst) )
    {
        return true;
    }

    return Parent::getOperation(cmd, bst);
}


ULONG Mct410Device::calcNextLPScanTime( void )
{
    CtiTime       Now;
    unsigned long next_time, planned_time;

    next_time = YUKONEOT;

    if( !_intervalsSent )
    {
        //  send load profile interval on the next 5 minute boundary
        next_time  = (Now.seconds() - LoadProfileCollectionWindow) + 300;

        next_time -= next_time % 300;
    }
    else
    {
        for( int channel = 0; channel < LPChannels; channel++ )
        {
            const int interval_len = getLoadProfileInterval(channel);
            const int block_len    = interval_len * 6;

            boost::optional<long> pointId = getPointIdForOffsetAndType((channel + 1) + PointOffset_LoadProfileOffset, DemandAccumulatorPointType);

            if( interval_len <= 0 )
            {
                CTILOG_WARN(dout, "device \""<< getName() <<"\" has invalid LP rate ("<< interval_len <<") for channel ("<< channel <<") - setting nextLPtime out 30 minutes");

                _lp_info[channel].current_schedule = Now.seconds() + (30 * 60);
            }
            else if( !getLoadProfile()->isChannelValid(channel) || ! pointId )
            {
                //  if we're not collecting load profile, or there's no point defined, don't scan
                _lp_info[channel].current_schedule = YUKONEOT;
            }
            else
            {
                //  uninitialized - get the readings from the DB
                if( !_lp_info[channel].collection_point )
                {
                    //  so we haven't talked to it yet
                    _lp_info[channel].collection_point = 86400;

                    CtiTablePointDispatch pd(*pointId);

                    if(pd.Restore())
                    {
                        _lp_info[channel].collection_point = pd.getTimeStamp().seconds();
                    }

                    //  allow us to have missed up to 4 days before giving up and starting at the current time again
                    //    this should only affect us on startup or when someone (re-)enables LP on a device
                    if( _lp_info[channel].collection_point < (CtiTime::now().seconds() - 86400 * 4) )
                    {
                        //  start collecting the most recent block
                        _lp_info[channel].collection_point  = CtiTime::now().seconds();
                        _lp_info[channel].collection_point -= _lp_info[channel].collection_point % block_len;
                    }
                }

                //  basically, we plan to request again after a whole block has been recorded...
                //    then we add on a little bit to make sure the MCT is out of the memory
                planned_time  = _lp_info[channel].collection_point + block_len;
                planned_time -= planned_time % block_len;  //  make double sure we're block-aligned
                planned_time += LPBlockEvacuationTime;      //  add on the safeguard time

                if( getMCTDebugLevel(DebugLevel_LoadProfile) )
                {
                    CTILOG_DEBUG(dout, "lp calctime check..."<<
                            endl <<"planned_time     = "<< planned_time <<
                            endl <<"channel          = "<< channel <<
                            endl <<"collection_point = "<< _lp_info[channel].collection_point <<
                            endl <<"current_schedule = "<< _lp_info[channel].current_schedule
                            );
                }

                _lp_info[channel].current_schedule = planned_time;

                //  if we're overdue, request at the overdue_rate
                if( _lp_info[channel].collection_point <= (Now.seconds() - block_len - LPBlockEvacuationTime) )
                {
                    unsigned int overdue_rate = getLPRetryRate(interval_len);

                    _lp_info[channel].current_schedule  = (Now.seconds() - LPBlockEvacuationTime) + overdue_rate;
                    _lp_info[channel].current_schedule -= (Now.seconds() - LPBlockEvacuationTime) % overdue_rate;

                    _lp_info[channel].current_schedule += LPBlockEvacuationTime;
                }
            }

            if( next_time > _lp_info[channel].current_schedule )
            {
                next_time = _lp_info[channel].current_schedule;
            }

            if( getMCTDebugLevel(DebugLevel_LoadProfile) )
            {
                CTILOG_DEBUG(dout, "lp calctime check..."<<
                        endl <<"planned_time = "<< planned_time <<
                        endl <<"_lp_info["<< channel <<"].collection_point = "<< _lp_info[channel].collection_point <<
                        endl <<"_lp_info["<< channel <<"].current_schedule = "<< _lp_info[channel].current_schedule <<
                        endl << "next_time = "<< next_time
                        );
            }
        }
    }

    if( getMCTDebugLevel(DebugLevel_LoadProfile) )
    {
        CTILOG_DEBUG(dout, getName() <<"'s next Load Profile request at "<< CtiTime(next_time));
    }

    return (_nextLPScanTime = next_time);
}


void Mct410Device::sendIntervals( OUTMESS *&OutMessage, OutMessageList &outList )
{
    populateDlcOutMessage(*OutMessage);
    OutMessage->Sequence  = EmetconProtocol::PutConfig_Intervals;     // Helps us figure it out later!

    // Tell the porter side to complete the assembly of the message.
    OutMessage->Request.BuildIt = TRUE;
    strncpy(OutMessage->Request.CommandStr, "putconfig emetcon intervals", COMMAND_STR_SIZE );

    outList.push_back(OutMessage);
    OutMessage = NULL;
}


void Mct410Device::calcAndInsertLPRequests(OUTMESS *&OutMessage, OutMessageList &outList)
{
    CtiTime Now;

    if( !_intervalsSent )
    {
        sendIntervals(OutMessage, outList);

        _intervalsSent = true;
    }
    else if( !useScanFlags() )
    {
        CTILOG_WARN(dout, "Function called from outside Scanner for device \""<< getName() <<"\", ignoring");
    }
    else
    {
        for( int channel = 0; channel < LPChannels; channel++ )
        {
            const int interval_len = getLoadProfileInterval(channel);
            const int block_len    = interval_len * 6;

            if( interval_len <= 0 )
            {
                CTILOG_ERROR(dout, "invalid interval_len ("<< interval_len <<") for device \""<< getName() <<"\"");
            }
            else if( getLoadProfile()->isChannelValid(channel) )
            {
                if( _lp_info[channel].collection_point <= (Now - block_len - LPBlockEvacuationTime) &&
                    _lp_info[channel].current_schedule <= Now )
                {
                    OUTMESS* tmpOutMess = CTIDBG_new OUTMESS(*OutMessage);

                    //  make sure we only ask for what the function reads can access
                    if( (Now.seconds() - _lp_info[channel].collection_point) >= (unsigned long)(LPRecentBlocks * block_len) )
                    {
                        //  go back as far as we can
                        _lp_info[channel].collection_point  = Now.seconds();
                        _lp_info[channel].collection_point -= LPRecentBlocks * block_len;
                    }

                    if( getMCTDebugLevel(DebugLevel_LoadProfile) )
                    {
                        CTILOG_DEBUG(dout, "LP variable check for device \""<< getName() <<"\""<<
                                endl <<"Now.seconds() = "<< Now.seconds() <<
                                endl <<"_lp_info["<< channel <<"].collection_point = "<< _lp_info[channel].collection_point <<
                                endl <<"MCT4XX_LPRecentBlocks * block_len = "<< LPRecentBlocks * block_len
                                );
                    }

                    //  make sure we're aligned
                    _lp_info[channel].collection_point -= _lp_info[channel].collection_point % block_len;

                    //  which block to grab?
                    const int block = (Now.seconds() - _lp_info[channel].collection_point) / block_len;

                    string descriptor = " channel " + CtiNumStr(channel + 1) + string(" block ") + CtiNumStr(block);

                    strncat( tmpOutMess->Request.CommandStr,
                             descriptor.c_str(),
                             sizeof(tmpOutMess->Request.CommandStr) - ::strlen(tmpOutMess->Request.CommandStr));

                    if( getMCTDebugLevel(DebugLevel_LoadProfile) )
                    {
                        CTILOG_DEBUG(dout, "command string check for device \""<< getName() <<"\":"<<
                                endl << tmpOutMess->Request.CommandStr
                                );
                    }

                    outList.push_back(tmpOutMess);
                }
            }
        }

        if( outList.empty() )
        {
            if( getMCTDebugLevel(DebugLevel_LoadProfile) )
            {
                CTILOG_DEBUG(dout, "LP collection up to date for device \""<< getName() <<"\", no scans generated");
            }
        }
    }

    if( OutMessage != NULL )
    {
        delete OutMessage;
        OutMessage = NULL;
    }
}


bool Mct410Device::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
{
    bool retVal = false;
    int  address, block, channel;

    if( getMCTDebugLevel(DebugLevel_LoadProfile) )
    {
        CTILOG_DEBUG(dout,
                endl <<"parse.getiValue(\"scan_loadprofile_block\",   0) = "<< parse.getiValue("scan_loadprofile_block", 0) <<
                endl <<"parse.getiValue(\"scan_loadprofile_channel\", 0) = "<< parse.getiValue("scan_loadprofile_channel", 0)
                );
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
        OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;

        retVal = true;
    }
    else
    {
        if( getMCTDebugLevel(DebugLevel_LoadProfile) )
        {
            CTILOG_DEBUG(dout, "Improperly formed LP request discarded for \""<< getName() <<"\"");
        }

        retVal = false;
    }

    return retVal;
}


const Mct410Device::ValueMapping *Mct410Device::getMemoryMap() const
{
    return &_memoryMap;
}


const Mct410Device::FunctionReadValueMappings *Mct410Device::getFunctionReadValueMaps() const
{
    return &_functionReadValueMaps;
}

namespace EP = ::Cti::Protocols::EmetconProtocol;

const Mct410Device::DecodeMapping Mct410Device::_decodeMethods {
        {EP::GetConfig_DailyReadInterest,       &Self::decodeGetConfigDailyReadInterest},    
        {EP::GetConfig_Disconnect,              &Self::decodeGetConfigDisconnect},
        {EP::GetConfig_Freeze,                  &Self::decodeGetConfigFreeze},
        {EP::GetConfig_Intervals,               &Self::decodeGetConfigIntervals},
        {EP::GetConfig_LoadProfileExistingPeak, &Self::decodeGetConfigLoadProfileExistingPeak},
        {EP::GetConfig_LongLoadProfileStorage,  &Self::decodeGetConfigLongLoadProfileStorageDays},
        {EP::GetConfig_MeterParameters,         &Self::decodeGetConfigMeterParameters},
        {EP::GetConfig_Model,                   &Self::decodeGetConfigModel},
        {EP::GetConfig_Multiplier,              &Self::decodeGetConfigMeterParameters},
        {EP::GetConfig_PhaseDetect,             &Self::decodeGetConfigPhaseDetect},
        {EP::GetConfig_PhaseDetectArchive,      &Self::decodeGetConfigPhaseDetect},
        {EP::GetConfig_Thresholds,              &Self::decodeGetConfigThresholds},
        {EP::GetConfig_UniqueAddress,           &Self::decodeGetConfigAddress},
        {EP::GetConfig_WaterMeterReadInterval,  &Self::decodeGetConfigWaterMeterReadInterval},
        // ---
        {EP::GetStatus_Disconnect,              &Self::decodeGetStatusDisconnect},
        {EP::GetStatus_Internal,                &Self::decodeGetStatusInternal},
        {EP::GetStatus_LoadProfile,             &Self::decodeGetStatusLoadProfile},
        // ---
        {EP::GetValue_DailyRead,                &Self::decodeGetValueDailyRead},
        {EP::GetValue_Demand,                   &Self::decodeGetValueDemand},
        {EP::GetValue_FrozenKWH,                &Self::decodeGetValueKWH},
        {EP::GetValue_FrozenPeakDemand,         &Self::decodeGetValuePeakDemand},
        {EP::GetValue_FrozenTOUkWh,             &Self::decodeGetValueTOUkWh},
        {EP::GetValue_FrozenVoltage,            &Self::decodeGetValueVoltage},
        {EP::GetValue_KWH,                      &Self::decodeGetValueKWH},
        {EP::GetValue_LoadProfilePeakReport,    &Self::decodeGetValueLoadProfilePeakReport},
        {EP::GetValue_Outage,                   &Self::decodeGetValueOutage},
        {EP::GetValue_PeakDemand,               &Self::decodeGetValuePeakDemand},
        {EP::GetValue_TOUkWh,                   &Self::decodeGetValueTOUkWh},
        {EP::GetValue_Voltage,                  &Self::decodeGetValueVoltage},
        // ---
        {EP::Scan_Accum,                        &Self::decodeGetValueKWH},
        {EP::Scan_Integrity,                    &Self::decodeGetValueDemand}};


YukonError_t Mct410Device::ModelDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    DecodeMapping::const_iterator itr = _decodeMethods.find(InMessage.Sequence);

    if( itr != _decodeMethods.end() )
    {
        const DecodeMethod decoder = itr->second;

        return (this->*decoder)(InMessage, TimeNow, vgList, retList, outList);
    }

    return Parent::ModelDecode(InMessage, TimeNow, vgList, retList, outList);
}


YukonError_t Mct410Device::SubmitRetry(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t retVal = ClientErrors::None;

    switch( InMessage.Sequence )
    {
        case EmetconProtocol::GetValue_DailyRead:
        {
            //  submit a retry if we're multi-day and we have any retries left
            if( _daily_read_info.request.type == daily_read_info_t::Request_MultiDay )
            {
                if( _daily_read_info.request.multi_day_retries-- > 0  && InMessage.ErrorCode != ClientErrors::RequestCancelled)
                {
                    string request_str = "getvalue daily read ";

                    request_str += "channel " + CtiNumStr(_daily_read_info.request.channel)
                                        + " " + printDate(_daily_read_info.request.begin + 1)
                                        + " " + printDate(_daily_read_info.request.end);

                    if( strstr(InMessage.Return.CommandStr, " noqueue") )  request_str += " noqueue";

                    CtiRequestMsg newReq(getID(),
                                         request_str,
                                         InMessage.Return.UserID,
                                         InMessage.Return.GrpMsgID,
                                         InMessage.Return.RouteID,
                                         selectInitialMacroRouteOffset(InMessage.Return.RouteID),
                                         0,
                                         0,
                                         InMessage.Priority);

                    newReq.setConnectionHandle(InMessage.Return.Connection);
                    newReq.setUserMessageId(InMessage.Return.UserID);

                    CtiReturnMsg *ret = CTIDBG_new CtiReturnMsg(getID(),
                                                                InMessage.Return.CommandStr,
                                                                "Submitting retry, " + CtiNumStr(_daily_read_info.request.multi_day_retries) + " remaining",
                                                                ClientErrors::None,
                                                                InMessage.Return.RouteID,
                                                                InMessage.Return.RetryMacroOffset,
                                                                InMessage.Return.Attempt,
                                                                InMessage.Return.GrpMsgID,
                                                                InMessage.Return.UserID,
                                                                InMessage.Return.SOE);

                    //  NOT setting ErrorDecode()'s overrideExpectMore in case ExecuteRequest() fails
                    ret->setExpectMore(true);
                    retList.push_back(ret);

                    //  same UserMessageID, no need to reset the in_progress flag
                    retVal = beginExecuteRequest(&newReq, CtiCommandParser(newReq.CommandString()), vgList, retList, outList);
                }
                else
                {
                    retList.push_back(CTIDBG_new CtiReturnMsg(getID(),
                                                              InMessage.Return.CommandStr,
                                                              "Multi-day daily read request failed",
                                                              ClientErrors::Abnormal,
                                                              InMessage.Return.RouteID,
                                                              InMessage.Return.RetryMacroOffset,
                                                              InMessage.Return.Attempt,
                                                              InMessage.Return.GrpMsgID,
                                                              InMessage.Return.UserID,
                                                              InMessage.Return.SOE));

                    _daily_read_info.request.in_progress = false;
                }
            }
            else
            {
                _daily_read_info.request.in_progress = false;
            }

            break;
        }

        default:
        {
            retVal = Parent::SubmitRetry(InMessage, TimeNow, vgList, retList, outList);
        }
    }

    return retVal;
}


void Mct410Device::handleCommandResult(const Mct410Command &command)
{
    command.invokeResultHandler(static_cast<Mct410Command::ResultHandler &>(*this));
}


void Mct410Device::handleCommandResult(const Mct410DisconnectConfigurationCommand &command)
{
    if( const boost::optional<Disc::DisconnectMode> disconnectMode = command.getDisconnectMode() )
    {
        if( const boost::optional<std::string> disconnectStr = bimapFind<std::string>(DisconnectModes.left, *disconnectMode) )
        {
            setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DisconnectMode, *disconnectStr);
        }
    }
    if( boost::optional<float> disconnectDemandThreshold = command.getDisconnectDemandThreshold() )
    {
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DemandThreshold, *disconnectDemandThreshold);
    }
}


YukonError_t Mct410Device::executePutConfig( CtiRequestMsg              *pReq,
                                             CtiCommandParser           &parse,
                                             OUTMESS                   *&OutMessage,
                                             CtiMessageList &vgList,
                                             CtiMessageList &retList,
                                             OutMessageList &outList )
{
    YukonError_t nRet = ClientErrors::NoMethod;

    bool found = false;
    int function = -1;

    populateDlcOutMessage(*OutMessage);

    OutMessage->Request.RouteID   = getRouteID();
    strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

    if( parse.isKeyValid("address") && parse.isKeyValid("uniqueaddress") )
    {
        int uadd;

        function = EmetconProtocol::PutConfig_UniqueAddress;
        found    = getOperation(function, OutMessage->Buffer.BSt);

        uadd = parse.getiValue("uniqueaddress");

        if( uadd > 0x3fffff || uadd < 0 )
        {
            found = false;
            nRet  = ExecutionComplete;

            insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                               "Invalid address \"" + CtiNumStr(uadd) + "\", not sending");
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
    else if( parse.isKeyValid("display_resolution") &&
             parse.isKeyValid("display_test_duration") )
    {
        unsigned char meter_parameters = 0x00;  //  default, see sspec for details

        OutMessage->Buffer.BSt.Function = FuncWrite_MeterParametersPos;
        OutMessage->Buffer.BSt.Length   = FuncWrite_MeterParametersLen;
        OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Write;

        OutMessage->Sequence            = EmetconProtocol::PutConfig_Parameters;

        found = true;  //  default to true;  set to false in error cases

        OutMessage->Buffer.BSt.Message[0] = gMCT400SeriesSPID;

        string display = parse.getsValue("display_resolution");

        if(      !display.compare("5x1")  )     meter_parameters |= 0x00;
        else if( !display.compare("4x1")  )     meter_parameters |= 0x01;
        else if( !display.compare("4x10") )     meter_parameters |= 0x02;
        else if( !display.compare("6x1") )      meter_parameters |= 0x03;
        else
        {
            found = false;
            nRet  = ExecutionComplete;

            insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                               "Invalid display configuration \"" + display + "\"");
        }

        if( nRet != ExecutionComplete )
        {
            int test = parse.getiValue("display_test_duration");

            if(      test == 0 )  meter_parameters |= 0x00;
            else if( test == 1 )  meter_parameters |= 0x04;
            else if( test == 7 )  meter_parameters |= 0x0c;
            else
            {
                found = false;
                nRet  = ExecutionComplete;

                insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                   "Invalid test duration \"" + CtiNumStr(test) + "\"");
            }

            if( nRet != ExecutionComplete )
            {
                if( parse.isKeyValid("display_errors") )
                {
                    meter_parameters |= 0x10;
                }

                OutMessage->Buffer.BSt.Message[1] = meter_parameters;

                if( parse.isKeyValid("transformer_ratio") )
                {
                    int transformer_ratio = parse.getiValue("transformer_ratio");

                    if( transformer_ratio > 0 && transformer_ratio <= 255 )
                    {
                        OutMessage->Buffer.BSt.Message[2] = (unsigned char)parse.getiValue("transformer_ratio");
                    }
                    else
                    {
                        found = false;
                        nRet  = ExecutionComplete;

                        insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                           "Invalid transformer ratio (" + CtiNumStr(transformer_ratio) + ")");
                    }
                }
                else
                {
                    //  omit the multiplier ratio
                    OutMessage->Buffer.BSt.Length--;
                }
            }
        }
    }
    else if( parse.isKeyValid("centron_reading_forward") )
    {
        double reading_forward = parse.getdValue("centron_reading_forward"),
               reading_reverse = parse.getdValue("centron_reading_reverse");

        long   pulses_forward,
               pulses_reverse;

        boost::shared_ptr<CtiPointNumeric> tmpPoint = boost::static_pointer_cast<CtiPointNumeric>(getDevicePointOffsetTypeEqual(1, PulseAccumulatorPointType));

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

        OutMessage->Sequence = Cti::Protocols::EmetconProtocol::PutValue_KYZ;

        OutMessage->Buffer.BSt.Function = FuncWrite_CentronReadingPos;
        OutMessage->Buffer.BSt.Length   = FuncWrite_CentronReadingLen;

        OutMessage->Buffer.BSt.IO = Cti::Protocols::EmetconProtocol::IO_Function_Write;

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
        found = getOperation(EmetconProtocol::PutConfig_Disconnect, OutMessage->Buffer.BSt);

        OutMessage->Sequence = EmetconProtocol::PutConfig_Disconnect;

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

        dynamic_demand_threshold = Utility::makeDynamicDemand(demand_threshold);

        if( dynamic_demand_threshold < 0 ||  connect_delay > 10 )
        {
            found = false;
            nRet  = ExecutionComplete;

            insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
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
                nRet  = ExecutionComplete;

                insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                   "Invalid disconnect cycle parameters (" + CtiNumStr(disconnect_minutes) + ", " + CtiNumStr(connect_minutes) + ")");
            }
            else
            {
                OutMessage->Buffer.BSt.Message[6] = disconnect_minutes & 0xff;
                OutMessage->Buffer.BSt.Message[7] = connect_minutes    & 0xff;
            }
        }
    }
    else if( parse.isKeyValid("outage_threshold") )
    {
        function = EmetconProtocol::PutConfig_OutageThreshold;
        found    = getOperation(function, OutMessage->Buffer.BSt);

        int threshold = parse.getiValue("outage_threshold");

        if( threshold && (threshold < 15 ||
                          threshold > 60) )
        {
            found = false;
            nRet  = ExecutionComplete;

            insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                               "Invalid outage threshold (" + CtiNumStr(threshold) + "), not sending");
        }
        else
        {
            OutMessage->Sequence = function;

            OutMessage->Buffer.BSt.Message[0] = threshold;
        }
    }
    else if( parse.isKeyValid("freeze_day") )
    {
        int threshold;

        function = EmetconProtocol::PutConfig_FreezeDay;
        found    = getOperation(function, OutMessage->Buffer.BSt);

        int freeze_day = parse.getiValue("freeze_day");

        if( freeze_day > 255 || freeze_day < 0 )
        {
            found = false;
            nRet  = ExecutionComplete;

            insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                               "Invalid day of scheduled freeze (" + CtiNumStr(freeze_day) + "), not sending");
        }
        else
        {
            OutMessage->Sequence = function;

            OutMessage->Buffer.BSt.Message[0] = freeze_day;
        }
    }
    else if(parse.isKeyValid("phasedetect"))
    {
        found = buildPhaseDetectOutMessage(parse,OutMessage);
    }
    else if( parse.isKeyValid("alarm_mask") )
    {
        if( parse.isKeyValid("config_byte") ||
            hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration))
        {
            char configuration = (parse.isKeyValid("config_byte") ?
                                  parse.getiValue("config_byte") : getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) );

            int alarmMask = parse.getiValue("alarm_mask", 0);

            OutMessage->Buffer.BSt.Message[0] = (configuration);
            OutMessage->Buffer.BSt.Message[1] = (alarmMask & 0xFF);
            OutMessage->Buffer.BSt.Message[2] = ((alarmMask >> 8) & 0xFF);

            if( parse.isKeyValid("alarm_mask_meter") )
            {
                int meterAlarmMask = parse.getiValue("alarm_mask_meter", 0);
                OutMessage->Buffer.BSt.Message[3] = (meterAlarmMask & 0xFF);
                OutMessage->Buffer.BSt.Message[4] = ((meterAlarmMask >> 8) & 0xFF);
            }

            function = EmetconProtocol::PutConfig_Options;
            getOperation(function, OutMessage->Buffer.BSt);

            OutMessage->Sequence = EmetconProtocol::PutConfig_AlarmMask;
            found = true;
        }
        else
        {
            found = false;
            nRet  = ExecutionComplete;

            insertReturnMsg(ClientErrors::MissingParameter, OutMessage, retList,
                               "Invalid request: Config Byte needs to be specified");
        }

    }
    else if(parse.isKeyValid("autoreconnect_enable"))
    {
        if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration))
        {
            // the config byte is needed for the autoreconnect command
            // sending a getconfig model message and returning an error to
            // reissue the autoreconnect command
            CtiOutMessage *interest_om = new CtiOutMessage(*OutMessage);

            if( getOperation(EmetconProtocol::GetConfig_Model, interest_om->Buffer.BSt) )
            {
                interest_om->Sequence = EmetconProtocol::GetConfig_Model;

                //  make this return message disappear so it doesn't confuse the client
                interest_om->Request.Connection.reset();

                outList.push_back(interest_om);
            }
            else
            {
                delete interest_om;
            }
            nRet  = ExecutionComplete;

            insertReturnMsg(ClientErrors::MissingConfig, OutMessage, retList,
                               "Invalid request: Config Byte has been requested.  Reissue autoreconnect command.");
        }
        else
        {
            function = EmetconProtocol::PutConfig_AutoReconnect;
            getOperation(function, OutMessage->Buffer.BSt);
            OutMessage->Buffer.BSt.Message[0] = getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration);
            //bit 2 is the disable reconnect button
            //when this bit is set, the disconnect does not require a button to be pressed to reconnect the meter
            if(parse.getiValue("autoreconnect_enable"))
            {
                OutMessage->Buffer.BSt.Message[0] |= 0x04;
            }
            else
            {
                OutMessage->Buffer.BSt.Message[0] &= 0xFB;
            }

            OutMessage->Sequence = EmetconProtocol::PutConfig_AutoReconnect;
            found = true;
        }

    }
    else if ( parse.isKeyValid("water_meter_read_interval") )
    {
        if ( isMct410(getType()) )
        {
            function = EmetconProtocol::PutConfig_WaterMeterReadInterval;
            found    = getOperation(function, OutMessage->Buffer.BSt);

            int duration = 0;       // default - MCT interprets this as 12 hours

            if ( parse.isKeyValid("read_interval_duration_seconds") )
            {
                duration = parse.getiValue("read_interval_duration_seconds");

                duration /= 60;     // minutes

                if ( ( 5 <= duration && duration <= 600 ) && !( duration % 5 ) )    // use 5 minute granularity up to 10 hours
                {
                    duration /= 5;
                }
                else if ( ( 15 <= duration && duration <= 1860 ) && !( duration % 15 ) )    // 15 minute granularity up to 31 hours
                {
                    duration /= 15;
                    duration |= 0x80;
                }
                else
                {
                    found = false;
                    nRet  = ExecutionComplete;

                    insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                       "Invalid interval length (" + CtiNumStr(duration) + " minutes), not sending");
                }
            }

            if( found )
            {
                OutMessage->Sequence = function;

                OutMessage->Buffer.BSt.Message[0] = duration;
            }
        }
    }
    else if ( parse.isKeyValid("load_profile_allocation") )
    {
        function = EmetconProtocol::PutConfig_LongLoadProfileStorage;
        found    = getOperation(function, OutMessage->Buffer.BSt);

        // sanity check -- these should all be here

        if ( parse.isKeyValid("load_profile_allocation_channel_1") &&
             parse.isKeyValid("load_profile_allocation_channel_2") &&
             parse.isKeyValid("load_profile_allocation_channel_3") &&
             parse.isKeyValid("load_profile_allocation_channel_4") )
        {
            OutMessage->Sequence = function;

            OutMessage->Buffer.BSt.Message[0] = 0xff;       // all SPIDs listen
            OutMessage->Buffer.BSt.Message[1] = parse.getiValue("load_profile_allocation_channel_1");
            OutMessage->Buffer.BSt.Message[2] = parse.getiValue("load_profile_allocation_channel_2");
            OutMessage->Buffer.BSt.Message[3] = parse.getiValue("load_profile_allocation_channel_3");
            OutMessage->Buffer.BSt.Message[4] = parse.getiValue("load_profile_allocation_channel_4");
        }
        else
        {
            found = false;
            nRet  = ExecutionComplete;

            insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                               "Missing channel in load profile allocation, not sending");
        }
    }
    else if ( parse.isKeyValid("channel_2_configuration") )
    {
        if ( isMct410(getType()) )
        {
            // we need to have the options byte in DynamicPaoInfo

            if( ! hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Options) )
            {
                // don't have it - go get it...
                // sending a getconfig model message and returning an error to
                // reissue the channel 2 configuration command
                CtiOutMessage *interest_om = new CtiOutMessage(*OutMessage);

                if( getOperation(EmetconProtocol::GetConfig_Model, interest_om->Buffer.BSt) )
                {
                    interest_om->Sequence = EmetconProtocol::GetConfig_Model;

                    //  make this return message disappear so it doesn't confuse the client
                    interest_om->Request.Connection.reset();

                    outList.push_back(interest_om);
                }
                else
                {
                    delete interest_om;
                }
                nRet  = ExecutionComplete;

                insertReturnMsg(ClientErrors::MissingConfig, OutMessage, retList,
                                   "Invalid request: Options Byte not retrieved yet, attempting to read automatically.  Reissue channel 2 configuration command.");
            }
            else
            {
                function = EmetconProtocol::PutConfig_Channel2NetMetering;
                found    = getOperation(function, OutMessage->Buffer.BSt);

                std::string new_option_string = parse.getsValue("channel_2_configuration_setting");

                int new_option_bit_pattern = 0;

                if( new_option_string == "netmetering" )
                {
                    new_option_bit_pattern = 0x07;      //  111 -- Net Metering Mode Enabled
                }
                else if ( new_option_string == "ui1203" )
                {
                    new_option_bit_pattern = 0x01;      //  001 -- Wired UI1203 Sensus Water Meter
                }
                else if ( new_option_string == "ui1204" )
                {
                    new_option_bit_pattern = 0x02;      //  010 -- Wired UI1204 (Touchread) Sensus or Badger Water Meter
                }
                else    //  if ( new_option_string == "none" )
                {
                    new_option_bit_pattern = 0x00;      //  000 -- No Meter Attached
                }


                long current_options = getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Options);

                // clear out existing channel 2 info
                current_options &= 0xe3;

                // OR in the new options
                current_options |= ( new_option_bit_pattern << 2 );

                OutMessage->Sequence = function;

                OutMessage->Buffer.BSt.Message[0] = current_options;
            }
        }
    }
    else
    {
        nRet = Parent::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }

    if( found )
    {
        nRet = ClientErrors::None;
    }

    return nRet;
}

YukonError_t Mct410Device::executePutConfigInstallDisconnect(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    if (!isSupported(Feature_Disconnect))
    {
        return ClientErrors::UnsupportedDevice;
    }

    Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();
    if( !deviceConfig )
    {
        return ClientErrors::NoConfigData;
    }

    DlcCommandPtr disconnectCommand;

    if( ! readsOnly )
    {
        try
        {
            const std::string disconnectModeStr = getConfigData<std::string>(deviceConfig, MCTStrings::DisconnectMode);
            const Commands::Mct410DisconnectConfigurationCommand::DisconnectMode disconnectMode =
                    resolveConfigData(
                            DisconnectModes.right,
                            disconnectModeStr,
                            MCTStrings::DisconnectMode);

            const float disconnectDemandThreshold = getConfigData<double>(deviceConfig, MCTStrings::DisconnectDemandThreshold);

            const long disconnectLoadLimitDelay = getConfigData<long>(deviceConfig, MCTStrings::DisconnectLoadLimitConnectDelay);
            if( disconnectLoadLimitDelay < 0 )
            {
                throw InvalidConfigDataException(MCTStrings::DisconnectLoadLimitConnectDelay, "disconnectLoadLimitDelay < 0");
            }

            const long disconnectMinutes = getConfigData<long>(deviceConfig, MCTStrings::DisconnectMinutes);
            if( disconnectMinutes < 0 )
            {
                throw InvalidConfigDataException(MCTStrings::DisconnectMinutes, "disconnectMinutes < 0");
            }

            const long connectMinutes = getConfigData<long>(deviceConfig, MCTStrings::ConnectMinutes);
            if( connectMinutes < 0 )
            {
                throw InvalidConfigDataException(MCTStrings::ConnectMinutes, "connectMinutes < 0");
            }

            const bool reconnectButtonRequired =
                    resolveConfigData(
                            ReconnectButtonLookup,
                            getConfigData<std::string>(
                                    deviceConfig,
                                    MCTStrings::ReconnectParameter),
                            MCTStrings::ReconnectParameter);

            std::string dpi_disconnectModeStr;
            double dpi_demandThreshold;
            unsigned dpi_connectDelay, dpi_disconnectMinutes, dpi_connectMinutes, dpi_configurationByte;

            getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DisconnectMode,    dpi_disconnectModeStr);
            getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DemandThreshold,   dpi_demandThreshold);
            getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_ConnectDelay,      dpi_connectDelay);
            getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DisconnectMinutes, dpi_disconnectMinutes);
            getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_ConnectMinutes,    dpi_connectMinutes);
            getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration,     dpi_configurationByte);

            // Bit CLEARED = button must be pressed for reconnect.
            // Bit ACTIVE  = button doesn't need to be pressed for reconnect.
            // So if the database value is true, we expect bit two to be clear.
            const bool dpi_reconnectButtonRequired = ! ((dpi_configurationByte >> 2) & 0x01);

            bool matches = dpi_disconnectModeStr == disconnectModeStr;

            if( matches )
            {
                switch( disconnectMode )
                {
                    case Commands::Mct410DisconnectConfigurationCommand::DemandThreshold:
                    {
                        matches &= fabs(dpi_demandThreshold - disconnectDemandThreshold) < 1e-2;
                        matches &= (dpi_connectDelay == disconnectLoadLimitDelay);
                    }  //  fall through
                    case Commands::Mct410DisconnectConfigurationCommand::OnDemand:
                    {
                        matches &= (dpi_reconnectButtonRequired == reconnectButtonRequired);
                        break;
                    }
                    case Commands::Mct410DisconnectConfigurationCommand::Cycling:
                    {
                        matches &= (dpi_disconnectMinutes == disconnectMinutes);
                        matches &= (dpi_connectMinutes == connectMinutes);
                    }
                }
            }

            if( matches )
            {
                if( ! parse.isKeyValid("force") )
                {
                    return ClientErrors::ConfigCurrent;
                }
            }
            else
            {
                if( parse.isKeyValid("verify") )
                {
                    return ClientErrors::ConfigNotCurrent;
                }
            }

            Mct410DisconnectConfigurationCommand::ReconnectButtonOptions buttonRequired =
                    reconnectButtonRequired
                        ? Mct410DisconnectConfigurationCommand::ButtonRequired
                        : Mct410DisconnectConfigurationCommand::ButtonNotRequired;

            disconnectCommand =
                    std::make_unique<Mct410DisconnectConfigurationCommand>(
                            disconnectMode,
                            _disconnectAddress,
                            disconnectDemandThreshold,
                            static_cast<unsigned>(disconnectLoadLimitDelay),
                            static_cast<unsigned>(disconnectMinutes),
                            static_cast<unsigned>(connectMinutes),
                            buttonRequired,
                            getDemandInterval());

            auto om = std::make_unique<OUTMESS>(*OutMessage);

            if( ! tryExecuteCommand(*om, std::move(disconnectCommand)) )
            {
                return ClientErrors::NoMethod;
            }

            outList.push_back(om.release());
        }
        catch( InvalidConfigDataException &ex )
        {
            CTILOG_EXCEPTION_ERROR(dout, ex, "Device \""<< getName() <<"\"");

            return ClientErrors::InvalidConfigData;
        }
        catch( MissingConfigDataException &ex )
        {
            CTILOG_EXCEPTION_ERROR(dout, ex, "Device \""<< getName() <<"\"");

            return ClientErrors::NoConfigData;
        }
    }

    // This is a read
    disconnectCommand = std::make_unique<Mct410DisconnectConfigurationCommand>();

    auto om = std::make_unique<OUTMESS>(*OutMessage);
    om->Priority -= 1;      //decrease for read. Only want read after a successful write.

    if( ! tryExecuteCommand(*om, std::move(disconnectCommand)) )
    {
        return ClientErrors::NoMethod;
    }

    outList.push_back(om.release());

    return ClientErrors::None;
}

YukonError_t Mct410Device::executePutConfigInstallFreezeDay(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if ( ! deviceConfig )
    {
        return ClientErrors::NoConfigData;
    }

    if ( ! readsOnly )
    {
        boost::optional<unsigned char>  configFreezeDay,
                                        paoFreezeDay;

        {
            const std::string           configKey( MCTStrings::DemandFreezeDay );
            const boost::optional<long> configValue = deviceConfig->findValue<long>( configKey );

            if ( ! configValue  )
            {
                CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - no value found for config key \""<< configKey <<"\"");

                return ClientErrors::NoConfigData;
            }

            if ( *configValue < 0 || *configValue > std::numeric_limits<unsigned char>::max() )
            {
                CTILOG_ERROR(dout, "Device \""<< getName() <<"\" - invalid value ("<< *configValue <<") found for config key \""<< configKey << "\"");

                return ClientErrors::BadParameter;
            }

            configFreezeDay = static_cast<unsigned>( *configValue );
        }

        {
            long pao_value;

            if ( getDynamicInfo( CtiTableDynamicPaoInfo::Key_MCT_ScheduledFreezeDay, pao_value ) )
            {
                paoFreezeDay = static_cast<unsigned>( pao_value );
            }
        }

        if( configFreezeDay == paoFreezeDay )  // both exist and are equal
        {
            if( ! parse.isKeyValid("force") )
            {
                return ClientErrors::ConfigCurrent;
            }
        }
        else
        {
            if( parse.isKeyValid("verify") )
            {
                return ClientErrors::ConfigNotCurrent;
            }
        }

        // send the new value

        OutMessage->Sequence = EmetconProtocol::PutConfig_FreezeDay;
        if ( ! getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt) )
        {
            return ClientErrors::NoMethod;
        }
        OutMessage->Buffer.BSt.Message[0] = *configFreezeDay;
        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
    }

    OutMessage->Sequence = EmetconProtocol::GetConfig_Freeze;
    if ( ! getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt) )
    {
        return ClientErrors::NoMethod;
    }

    insertConfigReadOutMessage("getconfig freeze", *OutMessage, outList);

    return ClientErrors::None;
}


YukonError_t Mct410Device::executePutStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    if( parse.getFlags() & CMD_FLAG_PS_RESET_ALARMS )
    {
        const int function = EmetconProtocol::PutStatus_ResetAlarms;

        if( getOperation(function, OutMessage->Buffer.BSt) )
        {
            OutMessage->Buffer.BSt.Message[0] = 0;
            OutMessage->Buffer.BSt.Message[1] = 0;

            populateDlcOutMessage(*OutMessage);
            OutMessage->Sequence  = function;         // Helps us figure it out later!

            OutMessage->Request.RouteID   = getRouteID();
            strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

            return ClientErrors::None;
        }

        return ClientErrors::NoMethod;
    }

    return Parent::executePutStatus(pReq, parse, OutMessage, vgList, retList, outList);
}


bool Mct410Device::buildPhaseDetectOutMessage(CtiCommandParser & parse, OUTMESS *& OutMessage)
{
    bool found = false;

    if(parse.isKeyValid("phasedetectclear"))
    {
        found = true;
        // This should be using the getOperation but the virtual function in a static function prevents it.
        OutMessage->Buffer.BSt.Function = FuncWrite_PhaseDetectClear;
        OutMessage->Buffer.BSt.Length = FuncWrite_PhaseDetectClearLen;
        OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Function_Write;

        OutMessage->Buffer.BSt.Message[0] = gMCT400SeriesSPID;
        OutMessage->Buffer.BSt.Message[1] = Command_PhaseDetectClear;
        OutMessage->Sequence = EmetconProtocol::PutConfig_PhaseDetectClear;
    }
    else if(parse.isKeyValid("phase") )
    {
        found = true;
        string phase = parse.getsValue("phase");
        int phaseVal = 0;

        switch( phase[0] )
        {
            case 'a':  phaseVal = 1;  break;
            case 'b':  phaseVal = 2;  break;
            case 'c':  phaseVal = 3;  break;
            default:  phaseVal = 0;  break;
        }

        // This should be using the getOperation but the virtual function in a static function prevents it.
        OutMessage->Buffer.BSt.Function = FuncWrite_PhaseDetect;
        OutMessage->Buffer.BSt.Length = FuncWrite_PhaseDetectLen;
        OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Function_Write;

        OutMessage->Buffer.BSt.Message[0] = gMCT400SeriesSPID;
        OutMessage->Buffer.BSt.Message[1] = phaseVal  & 0xff;
        OutMessage->Buffer.BSt.Message[2] = parse.getiValue("phasedelta")  & 0xff;
        //demand interval in 15 secs increments (1=15secs, 2=30secs, 3=45secs, etc)
        OutMessage->Buffer.BSt.Message[3] = (parse.getiValue("phaseinterval") / 15 )  & 0xff;
        OutMessage->Buffer.BSt.Message[4] = parse.getiValue("phasenum")  & 0xff;
        OutMessage->Sequence = EmetconProtocol::PutConfig_PhaseDetect;
    }

    return found;
}


unsigned Mct410Device::getUsageReportDelay(const unsigned interval_length, const unsigned days) const
{
    static const unsigned millis_per_second = 1000;

    //  don't allow a negative delay
    const unsigned base_delay          = std::max(0, gConfigParms.getValueAsInt("PORTER_MCT_PEAK_REPORT_DELAY", 10));

    const unsigned report_intervals    = days * intervalsPerDay(interval_length);

    const unsigned millis_per_interval = (getType() == TYPEMCT410FL) ? 2 : 1;

    return base_delay + (report_intervals * millis_per_interval / millis_per_second);
}


void Mct410Device::readSspec(const OUTMESS &OutMessage, OutMessageList &outList) const
{
    unique_ptr<CtiOutMessage> sspec_om(new CtiOutMessage(OutMessage));

    //  we need to read the IED info byte out of the MCT
    if( getOperation(EmetconProtocol::GetConfig_Model, sspec_om->Buffer.BSt) )
    {
        sspec_om->Sequence      = EmetconProtocol::GetConfig_Model;
        sspec_om->MessageFlags |= MessageFlag_ExpectMore;
        sspec_om->Retry         = 2;

        //  Make sure the response doesn't show in Commander.
        //    Because this OM's response is discarded, it will not affect
        //    the message count/expect more behavior to the client.
        //  Otherwise, it would need to be added to the group message count.
        sspec_om->Request.Connection.reset();

        strncpy(sspec_om->Request.CommandStr, "getconfig model", COMMAND_STR_SIZE );

        outList.push_back(sspec_om.release());
    }
}


// There are 3 months of daily read slots in the MCT-410.
// There are 2 check bits returned with the detail reads
// that tell which of the last 3 months the read is for.
// However, not all months have 31 days, so some slots
// are roll over from month to month.
// Also, the old period of interest pointer can remain in
// the meter if it doesn't hear the new period-of-interest command.
// Because of that, an old pointer of January 31 can look like May 31 (1 % 4 == 5 % 4).
bool Mct410Device::isDailyReadVulnerableToAliasing(const CtiDate &date, const CtiTime &now)
{
    // There are 5 days that can alias:
    //
    //   01/31 can alias to 05/31 during 06/01-07/30
    //   11/29 can alias to 03/29 during 03/30-05/28
    //   11/30 can alias to 03/30 during 03/31-05/29
    //   08/31 can alias to 12/31 during 01/01-03/31
    //   03/31 can alias to 07/31 during 08/01-10/31

    const CtiDate nowDate(now.date());

    //  01/31 case
    if( date.month() == 5 && date.dayOfMonth() == 31 )
    {
        //  overwritten on 7/31
        return nowDate.month() < 7 || nowDate.month() == 7 && nowDate.dayOfMonth() < 31;
    }

    //  11/29 case - only unsafe if this isn't a leap year
    if( date.month() == 3 && date.dayOfMonth() == 29 && CtiDate::daysInMonthYear(2, date.year()) == 28 )
    {
        //  overwritten on 5/29
        return nowDate.month() < 5 || nowDate.month() == 5 && nowDate.dayOfMonth() < 29;
    }

    //  11/30 case
    if( date.month() == 3 && date.dayOfMonth() == 30 )
    {
        //  overwritten on 5/30
        return nowDate.month() < 5 || nowDate.month() == 5 && nowDate.dayOfMonth() < 30;
    }

    //  08/31 case
    if( date.month() == 12 && date.dayOfMonth() == 31 )
    {
        //  alias possible the whole time 12/31 is valid
        return true;
    }

    //  03/31 case
    if( date.month() == 7 && date.dayOfMonth() == 31 )
    {
        //  alias possible the whole time 7/31 is valid
        return true;
    }

    return false;
}



YukonError_t Mct410Device::executeGetValue( CtiRequestMsg              *pReq,
                                            CtiCommandParser           &parse,
                                            OUTMESS                   *&OutMessage,
                                            CtiMessageList &vgList,
                                            CtiMessageList &retList,
                                            OutMessageList &outList )
{
    YukonError_t nRet = ClientErrors::NoMethod;

    bool found = false;
    int function;

    static const string str_daily_read = "daily_read",
                        str_hourly_read = "hourly_read",
                        str_outage     = "outage";

    const CtiDate Today     = CtiDate(),
                  Yesterday = Today - 1;

    if( (parse.getFlags() &  CMD_FLAG_GV_KWH) &&
        (parse.getFlags() & (CMD_FLAG_GV_RATEMASK ^ CMD_FLAG_GV_RATET)) )
    {
        //  if it's a KWH request for rate ABCD - rate T should fall through to a normal KWH request

        function = EmetconProtocol::GetValue_TOUPeak;
        found = getOperation(function, OutMessage->Buffer.BSt);

        if( parse.getFlags() & CMD_FLAG_FROZEN )    OutMessage->Buffer.BSt.Function += FuncRead_TOUFrozenOffset;

        //  no need to increment for rate A
        if( parse.getFlags() & CMD_FLAG_GV_RATEB )  OutMessage->Buffer.BSt.Function += 1;
        if( parse.getFlags() & CMD_FLAG_GV_RATEC )  OutMessage->Buffer.BSt.Function += 2;
        if( parse.getFlags() & CMD_FLAG_GV_RATED )  OutMessage->Buffer.BSt.Function += 3;
    }
    else if( (parse.getFlags() & CMD_FLAG_GV_KWH) &&
             (parse.getFlags() & CMD_FLAG_GV_TOU) )
    {
        //  if it's a KWH request for all TOU rates - again, rate T should fall through to a normal KWH request
        if( parse.getFlags() & CMD_FLAG_FROZEN )
        {
            function = EmetconProtocol::GetValue_FrozenTOUkWh;
            found = getOperation(function, OutMessage->Buffer.BSt);
        }
        else
        {
            function = EmetconProtocol::GetValue_TOUkWh;
            found = getOperation(function, OutMessage->Buffer.BSt);
        }
    }
    else if( parse.isKeyValid("lp_command") )
    {
        // Disable load profile peak report for channel 4. (see YUK-4569)
        if ( parse.getsValue("lp_command") == "peak" && parse.getiValue("lp_channel") == 4 )
        {
            nRet = ClientErrors::BadParameter;
            insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList, "Channel 4 Load Profile Peak Report is Unavailable.");
        }
        else
        {
            nRet = Parent::executeGetValue(pReq, parse, OutMessage, vgList, retList, outList);
        }
    }
    else if( parse.getFlags() & CMD_FLAG_GV_PEAK )
    {
        if( parse.getFlags() & CMD_FLAG_FROZEN )
        {
            function = EmetconProtocol::GetValue_FrozenPeakDemand;
        }
        else
        {
            function = EmetconProtocol::GetValue_PeakDemand;
        }

        found = getOperation(function, OutMessage->Buffer.BSt);

        if( parse.isKeyValid("channel") )
        {
            switch( parse.getiValue("channel") )
            {
                //  both 0 and 1 resolve to the first register
                case 0:     break;                                          //  0x93
                case 1:     break;
                //  please note the magic numbers
                case 2:     OutMessage->Buffer.BSt.Function += 5;   break;  //  0x98
                case 3:     OutMessage->Buffer.BSt.Function += 7;   break;  //  0x9a

                //  anything outside the range from 0-3 is invalid
                default:    found = false;  break;
            }
        }
    }
    else if( parse.isKeyValid(str_hourly_read) )
    {
        if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) &&
            !isSupported(Feature_HourlyKwh) )
        {
            insertReturnMsg(ClientErrors::InvalidSSPEC, OutMessage, retList,
                               "Hourly read requires SSPEC rev 4.0 or higher; MCT reports " + CtiNumStr(getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) / 10.0, 1));

            return ExecutionComplete;
        }

        //  We've got the classMutex, so this pointer won't be modified by any other threads.
        DlcCommand *hourlyReadCommand = findExecutingCommand(_hourlyReadId);

        if( parse.isKeyValid("hourly_read_cancel") )
        {
            if( hourlyReadCommand )
            {
                hourlyReadCommand->cancel();
            }

            insertReturnMsg(
                    ClientErrors::None, OutMessage, retList, 
                    hourlyReadCommand 
                        ? "Hourly read cancelled" 
                        : "No active hourly read to cancel");

            return ExecutionComplete;
        }
        else if( hourlyReadCommand )
        {
            insertReturnMsg(ClientErrors::CommandAlreadyInProgress, OutMessage, retList, "Hourly read already in progress, send \"getvalue hourly read cancel\" to cancel");

            return ExecutionComplete;
        }
        else
        {
            _hourlyReadId = 0;
        }

        //  read the SSPEC revision if we don't have it yet
        if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) )
        {
            readSspec(*OutMessage, outList);
        }

        //  parseDateString will return CtiDate::neg_infin if the parse is invalid
        const CtiDate date_begin = parseDateString(parse.getsValue("hourly_read_date_begin"));
        const CtiDate date_end   = parseDateString(parse.getsValue("hourly_read_date_end"));
        const unsigned channel   = parse.getiValue("channel", 1);

        auto hourlyRead = makeHourlyReadCommand(date_begin, date_end, channel);

        //  this call might be able to move out to ExecuteRequest() at some point - maybe we just return
        //    a DlcCommand object that it can execute out there
        if( auto id = tryExecuteCommand(*OutMessage, std::move(hourlyRead)) )
        {
            _hourlyReadId = *id;

            found = true;
        }

        function = OutMessage->Sequence;
    }
    else if( parse.isKeyValid(str_daily_read) )
    {
        bool existing_request = false;

        if( parse.isKeyValid("daily_read_cancel") )
        {
            std::unique_ptr<CtiReturnMsg> ReturnMsg(
                new CtiReturnMsg(getID(), OutMessage->Request.CommandStr));

            ReturnMsg->setUserMessageId(OutMessage->Request.UserID);

            if( _daily_read_info.request.in_progress.exchange(false) )
            {
                ReturnMsg->setResultString(getName() + " / Daily read request cancelled\n");
            }
            else
            {
                ReturnMsg->setResultString(getName() + " / No active daily read requests to cancel\n");
            }

            retMsgHandler( OutMessage->Request.CommandStr, ClientErrors::None, ReturnMsg.release(), vgList, retList );

            delete OutMessage;
            OutMessage = 0;

            return ClientErrors::None;
        }
        //  if a request is already in progress and we're not submitting a continuation/retry
        else if( ! _daily_read_info.request.in_progress.compare_exchange_strong(existing_request, true)
                 && _daily_read_info.request.user_id != pReq->UserMessageId() )
        {
            string temp = getName() + " / Daily read request already in progress\n";

            temp += "Channel " + CtiNumStr(_daily_read_info.request.channel) + ", ";

            if( _daily_read_info.request.type == daily_read_info_t::Request_MultiDay )
            {
                temp += printDate(_daily_read_info.request.begin + 1) + " - " +
                        printDate(_daily_read_info.request.end) + "\n";
            }
            else
            {
                temp += printDate(_daily_read_info.request.begin);
            }

            nRet  = ExecutionComplete;
            insertReturnMsg(ClientErrors::CommandAlreadyInProgress, OutMessage, retList, temp);
        }
        else
        {
            int channel = parse.getiValue("channel", 1);

            // If the date is not specified, we use yesterday (last full day)
            CtiDate date_begin = Yesterday;

            //  grab the beginning date
            if( parse.isKeyValid("daily_read_date_begin") )
            {
                date_begin = parseDateString(parse.getsValue("daily_read_date_begin"));
            }

            if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision)
                && getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) < SspecRev_DailyRead )
            {
                nRet  = ExecutionComplete;
                insertReturnMsg(ClientErrors::InvalidSSPEC, OutMessage, retList,
                                   "Daily read requires SSPEC rev 2.1 or higher; MCT reports " + CtiNumStr(getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) / 10.0, 1));
            }
            else if( channel < 1 || channel > 3 )
            {
                nRet  = ExecutionComplete;
                insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                   "Invalid channel for daily read request; must be 1-3 (" + CtiNumStr(channel) + ")");
            }
            else if( date_begin > Yesterday )  //  must begin on or before yesterday
            {
                nRet  = ExecutionComplete;
                insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                   "Invalid date for daily read request; must be before today (" + parse.getsValue("daily_read_date_begin") + ")");
            }
            else if( parse.isKeyValid("daily_read_detail") )
            {
                //  single-day detail read - we'll send the period-of-interest OM later, if needed

                _daily_read_info.request.type    = daily_read_info_t::Request_SingleDay;
                _daily_read_info.request.channel = channel;
                _daily_read_info.request.begin   = date_begin;

                if( date_begin < Today - 92 )  //  must be no more than 92 days ago
                {
                    nRet  = ExecutionComplete;
                    insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                       "Date out of range for daily read detail request; must be less than 3 months ago (" + parse.getsValue("daily_read_date_begin") + ")");
                }
                else if( channel == 1 )
                {
                    OutMessage->Buffer.BSt.Function = FuncRead_SingleDayDailyReportCh1Pos;
                    OutMessage->Buffer.BSt.Length   = FuncRead_SingleDayDailyReportCh1Len;

                    found = true;
                }
                else if( channel == 2 )
                {
                    OutMessage->Buffer.BSt.Function = FuncRead_SingleDayDailyReportCh2Pos;
                    OutMessage->Buffer.BSt.Length   = FuncRead_SingleDayDailyReportCh2Len;

                    found = true;
                }
                else if( channel == 3 )
                {
                    OutMessage->Buffer.BSt.Function = FuncRead_SingleDayDailyReportCh3Pos;
                    OutMessage->Buffer.BSt.Length   = FuncRead_SingleDayDailyReportCh3Len;

                    found = true;
                }
            }
            else if( parse.isKeyValid("daily_read_date_end") ||
                     parse.isKeyValid("daily_reads") )
            {
                //  multi-day read - we'll send the period-of-interest OM later, if needed

                CtiDate date_end;

                if( parse.isKeyValid("daily_read_date_end") )
                {
                    date_end = parseDateString(parse.getsValue("daily_read_date_end"));
                }
                else
                {
                    //  no dates specified - collect 6 most recent daily reads
                    date_begin = Today - 5;
                    date_end   = Today;
                }

                if( date_begin < Today - 92 )
                {
                    nRet  = ExecutionComplete;
                    insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                       "Invalid begin date for multi-day daily read request, must be less than 92 days ago (" + parse.getsValue("daily_read_date_begin") + ")");
                }
                else if( date_end < date_begin )
                {
                    nRet  = ExecutionComplete;
                    insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                       "Invalid end date for multi-day daily read request; must be after begin date (" + parse.getsValue("daily_read_date_begin") + ", " + parse.getsValue("daily_read_date_end") + ")");
                }
                else if( date_end > Today )    //  must end on or before today
                {
                    nRet  = ExecutionComplete;
                    insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                       "Invalid end date for multi-day daily read request; must be on or before today (" + parse.getsValue("daily_read_date_end") + ")");
                }
                else
                {
                    //  only initialize the multi-day retries on first submission (or after a success, see decodeGetValueDailyRead() )
                    if( _daily_read_info.request.multi_day_retries < 0 )
                    {
                        _daily_read_info.request.multi_day_retries = 2;
                    }

                    //  translate days-past to the block offset
                    unsigned long block_offset = (Today.daysFrom1970() - date_begin.daysFrom1970()) / 6;

                    _daily_read_info.request.type    = daily_read_info_t::Request_MultiDay;
                    _daily_read_info.request.channel = channel;

                    //  make sure the date we're requesting is aligned to a read boundary
                    _daily_read_info.request.begin  = Today - ((block_offset + 1) * 6);
                    _daily_read_info.request.end    = date_end;

                    OutMessage->Buffer.BSt.Function = FuncRead_MultiDayDailyReportingBasePos + block_offset;
                    OutMessage->Buffer.BSt.Length   = FuncRead_MultiDayDailyReportingLen;

                    found = true;
                }
            }
            else if( channel != 1 )
            {
                nRet  = ExecutionComplete;
                insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                   "Invalid channel for recent daily read request; only valid for channel 1 (" + CtiNumStr(channel)  + ")");
            }
            else if( date_begin < Today - 8 )  //  must be no more than 8 days ago
            {
                nRet  = ExecutionComplete;
                insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList,
                                   "Invalid date for recent daily read request; must be less than 8 days ago (" + parse.getsValue("daily_read_date_begin") + ")");
            }
            else
            {
                unsigned long day_offset = Yesterday.daysFrom1970() - date_begin.daysFrom1970();

                OutMessage->Buffer.BSt.Function = FuncRead_Channel1SingleDayBasePos + day_offset;
                OutMessage->Buffer.BSt.Length   = FuncRead_Channel1SingleDayLen;

                _daily_read_info.request.type    = daily_read_info_t::Request_Recent;
                _daily_read_info.request.channel = 1;
                _daily_read_info.request.begin   = date_begin;

                found = true;
            }

            if( !found )
            {
                _daily_read_info.request.in_progress = false;
            }
            else
            {
                _daily_read_info.request.user_id = pReq->UserMessageId();

                function = EmetconProtocol::GetValue_DailyRead;
                OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Function_Read;

                //  read the SSPEC revision if we don't have it yet
                if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) )
                {
                    readSspec(*OutMessage, outList);
                }

                //  we need to set it to the requested interval
                unique_ptr<CtiOutMessage> interest_om(new CtiOutMessage(*OutMessage));

                interest_om->Sequence = EmetconProtocol::PutConfig_DailyReadInterest;

                interest_om->Buffer.BSt.Function = FuncWrite_DailyReadInterestPos;
                interest_om->Buffer.BSt.Length   = FuncWrite_DailyReadInterestLen;
                interest_om->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Write;
                interest_om->MessageFlags |= MessageFlag_ExpectMore;

                interest_om->Buffer.BSt.Message[0] = gMCT400SeriesSPID;

                if( _daily_read_info.request.type == daily_read_info_t::Request_SingleDay )
                {
                    if( _daily_read_info.request.begin != _daily_read_info.interest.date )
                    {
                        _daily_read_info.interest.date = _daily_read_info.request.begin;
                        _daily_read_info.interest.needs_verification = isDailyReadVulnerableToAliasing(_daily_read_info.request.begin, CtiTime());

                        //  single-day request - send the time of interest
                        interest_om->Buffer.BSt.Message[1] = 0;
                        interest_om->Buffer.BSt.Message[2] = _daily_read_info.interest.date.dayOfMonth();
                        interest_om->Buffer.BSt.Message[3] = _daily_read_info.interest.date.month();

                        outList.push_back(interest_om.release());
                    }

                    if( _daily_read_info.interest.needs_verification && _daily_read_info.request.channel == 1 )
                    {
                        //  we need to read the period of interest and validate it to prevent aliasing errors
                        unique_ptr<CtiOutMessage> alias_check_om(new CtiOutMessage(*OutMessage));

                        alias_check_om->Sequence = EmetconProtocol::GetConfig_DailyReadInterest;

                        getOperation(EmetconProtocol::GetConfig_DailyReadInterest, alias_check_om->Buffer.BSt);

                        alias_check_om->Request.Connection.reset();

                        outList.push_back(alias_check_om.release());
                    }
                }
                else if( _daily_read_info.request.type == daily_read_info_t::Request_MultiDay &&
                         _daily_read_info.request.channel != getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel)  )
                {
                    //  multi-day request - only set the channel
                    interest_om->Buffer.BSt.Message[1] = _daily_read_info.request.channel;
                    interest_om->Buffer.BSt.Message[2] = 0;
                    interest_om->Buffer.BSt.Message[3] = 0;

                    outList.push_back(interest_om.release());
                }
            }
        }
    }
    else if( parse.isKeyValid(str_outage) )  //  outages
    {
        if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec) )
        {
            //  we need to set it to the requested interval
            CtiOutMessage *sspec_om = new CtiOutMessage(*OutMessage);

            if( sspec_om )
            {
                getOperation(EmetconProtocol::GetConfig_Model, sspec_om->Buffer.BSt);

                sspec_om->Sequence = EmetconProtocol::GetConfig_Model;

                sspec_om->MessageFlags |= MessageFlag_ExpectMore;

                outList.push_back(sspec_om);
                sspec_om = 0;
            }
        }

        int outagenum = parse.getiValue(str_outage);

        function = EmetconProtocol::GetValue_Outage;
        found = getOperation(function, OutMessage->Buffer.BSt);

        if( outagenum < 0 || outagenum > 6 )
        {
            found = false;

            CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                           string(OutMessage->Request.CommandStr),
                                                           string(),
                                                           nRet,
                                                           OutMessage->Request.RouteID,
                                                           OutMessage->Request.RetryMacroOffset,
                                                           OutMessage->Request.Attempt,
                                                           OutMessage->Request.GrpMsgID,
                                                           OutMessage->Request.UserID,
                                                           OutMessage->Request.SOE,
                                                           CtiMultiMsg_vec( ));

            if( errRet )
            {
                string temp = "Bad outage specification - Acceptable values:  1-6";
                errRet->setResultString( temp );
                errRet->setStatus(ClientErrors::NoMethod);
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
        nRet = Parent::executeGetValue(pReq, parse, OutMessage, vgList, retList, outList);
    }

    if( found )
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Sequence  = function;         // Helps us figure it out later!

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        nRet = ClientErrors::None;
    }

    return nRet;
}


DlcBaseDevice::DlcCommandPtr Mct410Device::makeHourlyReadCommand(const CtiDate date_begin, const CtiDate date_end, const unsigned channel) const
{
    return std::make_unique<Mct410HourlyReadCommand>(date_begin, date_end, channel);
}


YukonError_t Mct410Device::executeGetConfig( CtiRequestMsg              *pReq,
                                             CtiCommandParser           &parse,
                                             OUTMESS                   *&OutMessage,
                                             CtiMessageList &vgList,
                                             CtiMessageList &retList,
                                             OutMessageList &outList )
{
    YukonError_t nRet = ClientErrors::NoMethod;


    bool found = false;

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                   string(OutMessage->Request.CommandStr),
                                                   string(),
                                                   nRet,
                                                   OutMessage->Request.RouteID,
                                                   OutMessage->Request.RetryMacroOffset,
                                                   OutMessage->Request.Attempt,
                                                   OutMessage->Request.GrpMsgID,
                                                   OutMessage->Request.UserID,
                                                   OutMessage->Request.SOE,
                                                   CtiMultiMsg_vec( ));

    if(parse.isKeyValid("disconnect"))
    {
        found = getOperation(EmetconProtocol::GetConfig_Disconnect, OutMessage->Buffer.BSt);

        OutMessage->Sequence = EmetconProtocol::GetConfig_Disconnect;

        if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) >= SspecRev_Disconnect_ConfigReadEnhanced )
        {
            //  adds the config byte and the disconnect verification threshold
            OutMessage->Buffer.BSt.Length += 2;
        }
        else if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) )
        {
            //  since we won't be getting the config byte back in the extended read, we need to read
            //    it out ourselves so we can display the disconnect button status in the decode
            CtiOutMessage *interest_om = new CtiOutMessage(*OutMessage);

            if( getOperation(EmetconProtocol::GetConfig_Model, interest_om->Buffer.BSt) )
            {
                interest_om->Sequence = EmetconProtocol::GetConfig_Model;

                //  make this return message disappear so it doesn't confuse the client
                interest_om->Request.Connection.reset();

                outList.push_back(interest_om);
            }
            else
            {
                delete interest_om;
            }
        }
    }
    else if( parse.isKeyValid("thresholds") )
    {
        OutMessage->Sequence = EmetconProtocol::GetConfig_Thresholds;

        found = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);
    }
    else if( parse.isKeyValid("address_unique") )
    {
        OutMessage->Sequence = EmetconProtocol::GetConfig_UniqueAddress;

        found = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);
    }
    else if( parse.isKeyValid("transformer_ratio") )
    {
        OutMessage->Sequence = EmetconProtocol::GetConfig_Multiplier;

        found = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);
    }
    else if( parse.isKeyValid("meter_parameters") )
    {
        OutMessage->Sequence = EmetconProtocol::GetConfig_MeterParameters;

        found = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);
    }
    else if( parse.isKeyValid("freeze") )
    {
        OutMessage->Sequence = EmetconProtocol::GetConfig_Freeze;

        found = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);
    }
    else if(parse.isKeyValid("phasedetectread"))
    {
        OutMessage->Sequence = EmetconProtocol::GetConfig_PhaseDetect;
        found = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);

        if(parse.isKeyValid("phasedetectarchive"))
        {
            OutMessage->Sequence = EmetconProtocol::GetConfig_PhaseDetectArchive;
        }
    }
    else if( parse.isKeyValid("water_meter_read_interval") )
    {
        if ( isMct410(getType()) )
        {
            OutMessage->Sequence = EmetconProtocol::GetConfig_WaterMeterReadInterval;

            found = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);
        }
    }
    else if( parse.isKeyValid("load_profile_allocation") )
    {
        OutMessage->Sequence = EmetconProtocol::GetConfig_LongLoadProfileStorage;

        found = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);
    }
    else
    {
        nRet = Parent::executeGetConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }

    if( found )
    {
        populateDlcOutMessage(*OutMessage);

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        nRet = ClientErrors::None;
    }

    if( errRet )
    {
        delete errRet;
        errRet = 0;
    }

    return nRet;
}


YukonError_t Mct410Device::decodeGetValueKWH(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    CtiTime pointTime;
    bool valid_data = true;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    frozen_point_info pi, pi_freezecount;

    CtiPointSPtr   pPoint;
    CtiReturnMsg  *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    CTILOG_TRACE(dout, "Accumulator Decode for \""<< getName() <<"\"");

    if( InMessage.Sequence == EmetconProtocol::Scan_Accum )
    {
        setScanFlag(ScanRateAccum, false);
    }

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    const unsigned char *freeze_counter = 0;

    if( InMessage.Sequence == Cti::Protocols::EmetconProtocol::GetValue_FrozenKWH )
    {
        string freeze_error;

        freeze_counter = DSt->Message + 3;

        if( status = checkFreezeLogic(TimeNow, *freeze_counter, freeze_error) )
        {
            ReturnMsg->setResultString(freeze_error);
        }
    }

    if( !status )
    {
        int channels = ChannelCount;

        //  cheaper than looking for parse.getFlags() & CMD_FLAG_GV_KWH
        if( stringContainsIgnoreCase(InMessage.Return.CommandStr, " kwh") )
        {
            channels = 1;
        }

        for( int i = 0; i < channels; i++ )
        {
            int offset = (i * 3);

            if( !i || getDevicePointOffsetTypeEqual(i + 1, PulseAccumulatorPointType) )
            {
                if( InMessage.Sequence == Cti::Protocols::EmetconProtocol::Scan_Accum ||
                    InMessage.Sequence == Cti::Protocols::EmetconProtocol::GetValue_KWH )
                {
                    pi = getAccumulatorData(DSt->Message + offset, 3, 0);

                    pointTime -= pointTime.seconds() % 60;
                }
                else if( InMessage.Sequence == Cti::Protocols::EmetconProtocol::GetValue_FrozenKWH )
                {
                    if( i ) offset++;  //  so that, for the frozen read, it goes 0, 4, 7 to step past the freeze counter in position 3

                    pi = getAccumulatorData(DSt->Message + offset, 3, freeze_counter);

                    if( pi.freeze_bit != getExpectedFreezeParity() )
                    {
                        CTILOG_ERROR(dout, "incoming freeze parity bit ("<< pi.freeze_bit <<") does not match expected freeze bit ("<< getExpectedFreezeParity() <<") on device \""<< getName() <<"\" - not sending data");

                        pi.description  = "Freeze parity does not match (";
                        pi.description += CtiNumStr(pi.freeze_bit) + " != " + CtiNumStr(getExpectedFreezeParity());
                        pi.description += "/" + CtiNumStr(getExpectedFreezeCounter()) + ")";
                        pi.quality = InvalidQuality;
                        pi.value = 0;

                        ReturnMsg->setResultString("Invalid freeze parity; last recorded freeze sent at " + getLastFreezeTimestamp(TimeNow).asString());
                        status = ClientErrors::InvalidFrozenReadingParity;
                    }
                    else
                    {
                        pointTime  = getLastFreezeTimestamp(TimeNow);
                        pointTime -= pointTime.seconds() % 60;
                    }
                }

                string point_name;
                bool reported = false;

                if( !i )    point_name = "Meter Reading";

                //  if kWh was returned as units, we could get rid of the default multiplier - it's messy
                insertPointDataReport(PulseAccumulatorPointType, i + 1,
                                      ReturnMsg, pi, point_name, pointTime, 0.1, TAG_POINT_MUST_ARCHIVE);

                //  if the quality's invalid, throw the status to abnormal if it's the first channel OR there's a point defined
                if( pi.quality == InvalidQuality && !status && (!i || getDevicePointOffsetTypeEqual(i + 1, PulseAccumulatorPointType)) )
                {
                    ReturnMsg->setResultString("Invalid data returned for channel " + CtiNumStr(i + 1) + "\n" + ReturnMsg->ResultString());
                    status = ClientErrors::InvalidData;
                }
            }
        }
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Mct410Device::decodeGetValueTOUkWh(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    CtiTime pointTime;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    frozen_point_info pi, pi_freezecount;

    CtiPointSPtr   pPoint;
    CtiReturnMsg  *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    const unsigned char *freeze_counter = 0;

    if( InMessage.Sequence == Cti::Protocols::EmetconProtocol::GetValue_FrozenTOUkWh )
    {
        string freeze_error;

        freeze_counter = DSt->Message + 12;

        if( status = checkFreezeLogic(TimeNow, *freeze_counter, freeze_error) )
        {
            ReturnMsg->setResultString(freeze_error);
        }
    }

    if( !status )
    {
        for( int i = 0; i < 4; i++ )
        {
            int offset = (i * 3);

            if( InMessage.Sequence == Cti::Protocols::EmetconProtocol::GetValue_TOUkWh )
            {
                pi = getAccumulatorData(DSt->Message + offset, 3, 0);

                pointTime -= pointTime.seconds() % 60;
            }
            else if( InMessage.Sequence == Cti::Protocols::EmetconProtocol::GetValue_FrozenTOUkWh )
            {
                pi = getAccumulatorData(DSt->Message + offset, 3, freeze_counter);

                if( pi.freeze_bit != getExpectedFreezeParity() )
                {
                    CTILOG_ERROR(dout, "incoming freeze parity bit ("<< pi.freeze_bit <<") does not match expected freeze bit ("<< getExpectedFreezeParity() <<") on device \""<< getName() <<"\" - not sending data");

                    pi.description  = "Freeze parity does not match (";
                    pi.description += CtiNumStr(pi.freeze_bit) + " != " + CtiNumStr(getExpectedFreezeParity());
                    pi.description += "/" + CtiNumStr(getExpectedFreezeCounter()) + ")";
                    pi.quality = InvalidQuality;
                    pi.value = 0;

                    ReturnMsg->setResultString("Invalid freeze parity; last recorded freeze sent at " + getLastFreezeTimestamp(TimeNow).asString());
                    status = ClientErrors::InvalidFrozenReadingParity;
                }
                else
                {
                    pointTime  = getLastFreezeTimestamp(TimeNow);
                    pointTime -= pointTime.seconds() % 60;
                }
            }

            //  if kWh was returned as units, we could get rid of the default multiplier - it's messy
            insertPointDataReport(PulseAccumulatorPointType, 1 + PointOffset_TOUBase + i * PointOffset_RateOffset,
                                  ReturnMsg, pi, string("TOU rate ") + (char)('A' + i) + " kWh", pointTime, 0.1,
                                  TAG_POINT_MUST_ARCHIVE);

            //  if the quality's invalid, throw the status to abnormal if there's a point defined
            if( pi.quality == InvalidQuality && !status && getDevicePointOffsetTypeEqual(1 + PointOffset_TOUBase + i * PointOffset_RateOffset, PulseAccumulatorPointType) )
            {
                ReturnMsg->setResultString("Invalid data returned\n" + ReturnMsg->ResultString());
                status = ClientErrors::InvalidData;
            }
        }
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Mct410Device::decodeGetValueDemand(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    point_info pi;

    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    CtiPointSPtr    pPoint;
    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;

    CTILOG_TRACE(dout, "Demand Decode for \""<< getName() <<"\"");

    setScanFlag(ScanRateGeneral, false);
    setScanFlag(ScanRateIntegrity, false);

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

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
                          ReturnMsg, pi, "Voltage", CtiTime(), 0.1);

    pi = Mct4xxDevice::getData(DSt->Message + 4, 2, ValueType_Raw);

    insertPointDataReport(PulseAccumulatorPointType, PointOffset_Accumulator_Powerfail,
                          ReturnMsg, pi, "Blink Counter");

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Mct410Device::decodeGetValueVoltage( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    point_info pi, max_volt_info, min_volt_info;

    CtiTime minTime, maxTime;

    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    max_volt_info = getData(DSt->Message, 2, ValueType_Voltage);
    pi = Mct4xxDevice::getData(DSt->Message + 2, 4, ValueType_Raw);
    maxTime = CtiTime((unsigned long)pi.value);


    min_volt_info = getData(DSt->Message + 6, 2, ValueType_Voltage);
    pi = Mct4xxDevice::getData(DSt->Message + 8, 4, ValueType_Raw);
    minTime = CtiTime((unsigned long)pi.value);

    if( InMessage.Sequence == EmetconProtocol::GetValue_FrozenVoltage )
    {
        insertPointDataReport(DemandAccumulatorPointType, PointOffset_VoltageMax,
                              ReturnMsg, max_volt_info, "", maxTime, 0.1);
        insertPointDataReport(DemandAccumulatorPointType, PointOffset_VoltageMaxFrozen,
                              ReturnMsg, max_volt_info, "Frozen Maximum Voltage", maxTime, 0.1);

        insertPointDataReport(DemandAccumulatorPointType, PointOffset_VoltageMin,
                              ReturnMsg, min_volt_info, "", minTime, 0.1);
        insertPointDataReport(DemandAccumulatorPointType, PointOffset_VoltageMinFrozen,
                              ReturnMsg, min_volt_info, "Frozen Minimum Voltage", minTime, 0.1);
    }
    else
    {
        insertPointDataReport(DemandAccumulatorPointType, PointOffset_VoltageMax,
                              ReturnMsg, max_volt_info, "Maximum Voltage", maxTime, 0.1);

        insertPointDataReport(DemandAccumulatorPointType, PointOffset_VoltageMin,
                              ReturnMsg, min_volt_info, "Minimum Voltage", minTime, 0.1);
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Mct410Device::decodeGetValueOutage( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    CtiCommandParser parse(InMessage.Return.CommandStr);

    const unsigned char   *msgbuf = InMessage.Buffer.DSt.Message;
    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointSPtr    pPoint;
    CtiPointDataMsg *pData = NULL;
    double value;

    int outagenum, multiplier = 0;
    unsigned long  timestamp;
    unsigned short duration;
    string pointString, resultString, timeString;
    CtiTime outageTime;

    ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

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
            int cycles;

            timestamp  = msgbuf[(i*6)+0] << 24;
            timestamp |= msgbuf[(i*6)+1] << 16;
            timestamp |= msgbuf[(i*6)+2] <<  8;
            timestamp |= msgbuf[(i*6)+3];

            duration   = msgbuf[(i*6)+4] <<  8;
            duration  |= msgbuf[(i*6)+5];

            outageTime = CtiTime(timestamp);

            if( is_valid_time(timestamp) )
            {
                timeString = outageTime.asString();
            }
            else
            {
                timeString = "(invalid time)";
            }

            pointString = getName() + " / Outage " + CtiNumStr(outagenum + i) + " : " + timeString + " for ";

            if( isSupported(Feature_OutageUnits) )
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

                if( multiplier == 2 && cycles == 0xffff )
                {
                    pointString += "(outage greater than 45 days)";
                }
                else
                {
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
                if( multiplier != 2 || cycles != 0xffff )
                {
                    int ss = cycles / 60;
                    int mm = ss / 60;
                    int hh = mm / 60;
                    int dd = hh / 24;

                    ss %= 60;
                    mm %= 60;
                    hh %= 24;

                    if( dd == 1 )
                    {
                        pointString += CtiNumStr(dd) + " day, ";
                    }
                    else if( dd > 1 )
                    {
                        pointString += CtiNumStr(dd) + " days, ";
                    }

                    pointString += CtiNumStr(hh).zpad(2) + string(":") +
                                   CtiNumStr(mm).zpad(2) + ":" +
                                   CtiNumStr(ss).zpad(2);

                    if( cycles % 60 )
                    {
                        int millis = (cycles % 60) * 1000;
                        millis /= 60;

                        pointString += "." + CtiNumStr(millis).zpad(3);
                    }
                }

                if( (pPoint = getDevicePointOffsetTypeEqual(PointOffset_Analog_Outage, AnalogPointType))
                    && is_valid_time(outageTime) )
                {
                    value  = cycles;
                    value /= 60.0;
                    value  = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(value);

                    //  need to do this the old way so that we can set the point data message manually
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

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Mct410Device::decodeGetConfigLoadProfileExistingPeak(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    long requestId = InMessage.Return.OptionsField;

    if( ! _llpPeakInterest.tryContinueRequest(requestId) )
    {
        CTILOG_WARN(dout, "orphaned GetConfig_LoadProfileExistingPeak for device \""<< getName() <<"\"");

        //  We're not executing any more, just disappear.
        return ClientErrors::None;
    }

    unsigned long peak_time = ntohl(*reinterpret_cast<const unsigned long *>(InMessage.Buffer.DSt.Message + 3));

    unsigned new_range   = _llpPeakInterest.range;
    unsigned new_channel = _llpPeakInterest.channel + 1;
    CtiDate  new_date    = _llpPeakInterest.end_date;

    const bool overlap =
        peak_time >= CtiTime(new_date - new_range + 1).seconds() &&
        peak_time <= CtiTime(new_date + 1).seconds();

    if( ! overlap )
    {
        _llpPeakInterest.no_overlap = true;
    }
    else
    {
        //  Set the report date outside of the requested range to ensure we won't have an overlap when they retry the request.
        new_date -= (new_range + 10);
        new_range = 1;

        std::unique_ptr<CtiReturnMsg> ReturnMsg(new CtiReturnMsg(getID()));

        ReturnMsg->setUserMessageId(InMessage.Return.UserID);

        ReturnMsg->setResultString("Requested date range overlaps the device's previous peak.  Resetting automatically, please retry command.");

        retMsgHandler( InMessage.Return.CommandStr, ClientErrors::NeedsDateRangeReset, ReturnMsg.release(), vgList, retList );
    }

    stringstream request;

    //  EmetconProtocol::PutConfig_LoadProfileReportPeriod
    request << "putconfig emetcon llp peak interest";
    request << " channel " << new_channel;
    request << " date "    << new_date.asStringUSFormat();
    request << " range "   << new_range;

    if( strstr(InMessage.Return.CommandStr, " noqueue") )
    {
        request << " noqueue";
    }

    CtiRequestMsg newReq(getID(),
                         request.str(),
                         InMessage.Return.UserID,
                         InMessage.Return.GrpMsgID,
                         0,
                         MacroOffset::none,
                         0,
                         requestId,  //  smuggle the request ID in OptionsField
                         InMessage.Priority);

    if( ! overlap )
    {
        newReq.setConnectionHandle(InMessage.Return.Connection);
    }

    beginExecuteRequest(&newReq, CtiCommandParser(newReq.CommandString()), vgList, retList, outList);

    return ClientErrors::None;  //  Return NoError even in case of overlap so macro subroutes don't consider this successful decode a failure
}


YukonError_t Mct410Device::decodeGetValueLoadProfilePeakReport(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    long requestId = InMessage.Return.OptionsField;

    if( ! _llpPeakInterest.tryContinueRequest(requestId) )
    {
        CTILOG_WARN(dout, "orphaned GetValue_LoadProfilePeakReport for device \""<< getName() <<"\"");

        //  We're not executing any more, just disappear.
        return ClientErrors::None;
    }

    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    string result_string;
    int       interval_len;
    double    max_usage, avg_daily, total_usage;
    unsigned long max_demand_timestamp, pulses;

    CtiReturnMsg    *ReturnMsg = NULL;  // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    pulses = DSt->Message[0] << 16 |
             DSt->Message[1] <<  8 |
             DSt->Message[2];

    if( InMessage.Return.ProtocolInfo.Emetcon.Function == FuncRead_LLPPeakDayPos )
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

    const double expected_avg_daily = total_usage / _llpPeakInterest.range;
    const double effective_range = avg_daily ? total_usage / avg_daily : 0.0;

    const double avg_daily_difference = std::abs(expected_avg_daily - avg_daily);

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    result_string  = getName() + " / Channel " + CtiNumStr(_llpPeakInterest.channel + 1) + string(" Load Profile Report\n");
    result_string += "Report range: " + (_llpPeakInterest.end_date - _llpPeakInterest.range).asStringUSFormat() + " - " +
                                         _llpPeakInterest.end_date.asStringUSFormat() + "\n";

    if( max_demand_timestamp > CtiTime(_llpPeakInterest.end_date + 1).seconds() ||
        max_demand_timestamp < CtiTime(_llpPeakInterest.end_date + 1 - _llpPeakInterest.range).seconds() )
    {
        result_string = "Peak timestamp (" + CtiTime(max_demand_timestamp).asString() + ") outside of requested range - retry report";
        status = ClientErrors::InvalidTimestamp;
    }
    else if( avg_daily_difference > 0.1 )
    {
        result_string  = "Mismatch between expected and average daily usage:\n";
        result_string += "Reported total usage over range: " + CtiNumStr(total_usage, 1) + string(" kWH\n");
        result_string += "Reported average daily usage over range: " + CtiNumStr(avg_daily, 1) + string(" kWH\n");
        result_string += "Expected average daily usage: " + CtiNumStr(expected_avg_daily, 1) + string(" kWH\n");
        result_string += "Effective days: " + CtiNumStr(effective_range) + string("\n");

        status = ClientErrors::InvalidTimestamp;
    }
    else
    {
        switch( InMessage.Return.ProtocolInfo.Emetcon.Function )
        {
            case FuncRead_LLPPeakDayPos:
            {
                result_string += "Peak day: " + CtiDate(CtiTime(max_demand_timestamp)).asStringUSFormat() + "\n";
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
                result_string += "Peak interval: " + CtiTime(max_demand_timestamp).asString() + "\n";
                result_string += "Usage:  " + CtiNumStr(max_usage, 1) + string(" kWH\n");

                const int interval_len = getLoadProfile()->getLoadProfileDemandRate();

                if( interval_len <= 0 )
                {
                    CTILOG_ERROR(dout, "invalid interval_len ("<< interval_len <<") for device \""<< getName() <<"\"");

                    result_string += "Demand: (cannot calculate, invalid load profile interval)\n";
                }
                else
                {
                    const int intervals_per_hour = 3600 / interval_len;

                    result_string += "Demand: " + CtiNumStr(max_usage * intervals_per_hour, 1) + string(" kW\n");
                }

                result_string += "Average daily usage over range: " + CtiNumStr(avg_daily, 1) + string(" kWH\n");
                result_string += "Total usage over range: " + CtiNumStr(total_usage, 1) + string(" kWH\n");

                break;
            }
        }
    }

    ReturnMsg->setResultString(result_string);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    _llpPeakInterest.tryEndRequest(requestId);

    return status;
}


YukonError_t Mct410Device::decodeGetConfigDailyReadInterest(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    const DSTRUCT &DSt = InMessage.Buffer.DSt;

    unique_ptr<CtiReturnMsg> ReturnMsg(new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    string resultString;

    const unsigned interest_day     =   DSt.Message[0] & 0x1f;
    const unsigned interest_month   =  (DSt.Message[1] & 0x0f) + 1;
    const unsigned interest_channel = ((DSt.Message[1] & 0x30) >> 4) + 1;

    resultString  = getName() + " / Daily read interest channel: " + CtiNumStr(interest_channel) + "\n";
    resultString += getName() + " / Daily read interest month: "   + CtiNumStr(interest_month) + "\n";
    resultString += getName() + " / Daily read interest day: "     + CtiNumStr(interest_day) + "\n";

    tryVerifyDailyReadInterestDate(interest_day, interest_month, TimeNow);

    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, ClientErrors::None, ReturnMsg.release(), vgList, retList );

    return ClientErrors::None;
}


void Mct410Device::tryVerifyDailyReadInterestDate(const unsigned interest_day, const unsigned interest_month, const CtiTime TimeNow)
{
    const CtiDate DateNow(TimeNow);

    unsigned interest_year = DateNow.year();

    if( DateNow.month() < interest_month )
    {
        //  interest month is greater than the current month - must've been last year
        interest_year--;
    }

    const CtiDate interest_date(interest_day, interest_month, interest_year);

    if( interest_date == _daily_read_info.interest.date )
    {
        _daily_read_info.interest.needs_verification = false;
    }
}


YukonError_t Mct410Device::decodeGetValueDailyRead(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    string resultString;
    bool expectMore = false;

    const DSTRUCT * const DSt  = &InMessage.Buffer.DSt;

    CtiReturnMsg    *ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) )
    {
        resultString = getName() + " / Daily read requires SSPEC rev 2.1 or higher, command could not automatically retrieve SSPEC; retry command or execute \"getconfig model\" to verify SSPEC";
        status = ClientErrors::VerifySSPEC;
    }
    else if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) < SspecRev_DailyRead )
    {
        resultString = getName() + " / Daily read requires SSPEC rev 2.1 or higher; MCT reports " + CtiNumStr(getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) / 10.0, 1);
        status = ClientErrors::InvalidSSPEC;
    }
    else
    {
        string demand_pointname, consumption_pointname;

        if( _daily_read_info.request.channel == 1 )
        {
            demand_pointname      = "kW";
            consumption_pointname = "kWh";
        }
        else
        {
            demand_pointname      = "Channel " + CtiNumStr(_daily_read_info.request.channel) + " demand";
            consumption_pointname = "Channel " + CtiNumStr(_daily_read_info.request.channel) + " consumption";
        }

        if( _daily_read_info.request.type == daily_read_info_t::Request_MultiDay )
        {
            decodeGetValueDailyRead_MultiDay(InMessage, resultString, DSt, status, expectMore, ReturnMsg, consumption_pointname, vgList, retList, outList);
        }
        else
        {
            decodeGetValueDailyRead_SingleDay(InMessage, resultString, DSt, status, expectMore, ReturnMsg, consumption_pointname, demand_pointname);
        }
    }

    //  this is gross
    if( !ReturnMsg->ResultString().empty() )
    {
        resultString = ReturnMsg->ResultString() + "\n" + resultString;
    }

    ReturnMsg->setResultString(resultString);

    retMsgHandler(InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList, expectMore);

    return status;
}


void Mct410Device::decodeGetValueDailyRead_MultiDay(const INMESS& InMessage, std::string& resultString, const DSTRUCT* DSt, YukonError_t& status, bool& expectMore, CtiReturnMsg* ReturnMsg, std::string consumption_pointname, CtiMessageList& vgList, CtiMessageList& retList, OutMessageList& outList)
{
    if( ! _daily_read_info.request.in_progress || _daily_read_info.request.user_id != InMessage.Return.UserID )
    {
        resultString = getName() + " / Daily read request cancelled\n";
    }
    else
    {
        boost::optional<point_info> kwh;
        unsigned channel = 0;

        //  I have to process the readings newest-to-oldest, but I want to send them oldest-to-newest
        std::stack<point_info> daily_readings;

        for( unsigned day = 0; day < 6; ++day )
        {
            const unsigned char * const pos = DSt->Message + (day * 2) + (kwh ? 1 : 0);

            bool bad_delta = (((pos[0] & 0x3f) == 0x3f) && pos[1] == 0xfa);

            if( kwh || bad_delta )
            {
                //  Read the channel out of the high bits of the delta if we haven't already
                if( !channel )
                {
                    channel = 1 + ((*pos & 0xc0) >> 6);

                    setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, channel);
                }

                point_info delta = getData(pos, 2, ValueType_AccumulatorDelta);

                if( ! kwh || delta.quality != NormalQuality )
                {
                    //  This is a bad delta - put it on the list unmodified
                    daily_readings.push(delta);
                }
                else
                {
                    if( kwh->quality == NormalQuality )
                    {
                        if( kwh->value < delta.value  )
                        {
                            kwh->value = 0;
                            kwh->quality = OverflowQuality;
                            kwh->description = "Underflow";
                        }
                        else
                        {
                            kwh->value -= delta.value;
                        }
                    }

                    daily_readings.push(*kwh);
                }
            }
            else
            {
                //  get the un-rounded data, we'll round it when we send the pointdata
                kwh = Mct4xxDevice::decodePulseAccumulator(pos, 3, 0);

                daily_readings.push(*kwh);
            }
        }

        if( channel != _daily_read_info.request.channel )
        {
            resultString  = getName() + " / Invalid channel returned by daily read ";
            resultString += "(" + CtiNumStr(channel) + ", expecting " + CtiNumStr(_daily_read_info.request.channel) + ")";

            status = ClientErrors::InvalidChannel;
        }
        else
        {
            //  we reset the retry count any time there's a success
            _daily_read_info.request.multi_day_retries = -1;

            bool all_out_of_range = true;

            while( !daily_readings.empty() )
            {
                point_info pi = daily_readings.top();

                if( isMct410(getType()) )
                {
                    //  round down to make this match the MCT-410's normal kWh readings
                    pi.value -= static_cast<long>(pi.value) % 2;
                }

                all_out_of_range &= (pi.description == ErrorText_OutOfRange);

                insertPointDataReport(PulseAccumulatorPointType, _daily_read_info.request.channel, ReturnMsg,
                                        pi, consumption_pointname, CtiTime(_daily_read_info.request.begin + 1),  //  add on 24 hours - end of day
                                        0.1, TAG_POINT_MUST_ARCHIVE);

                ++_daily_read_info.request.begin;

                daily_readings.pop();
            }

            if( all_out_of_range )
            {
                status = ClientErrors::InvalidTimestamp;
            }

            if( _daily_read_info.request.begin < _daily_read_info.request.end )
            {
                string request_str = "getvalue daily read ";

                request_str += "channel " + CtiNumStr(_daily_read_info.request.channel)
                                    + " " + printDate(_daily_read_info.request.begin + 1)
                                    + " " + printDate(_daily_read_info.request.end);

                if( strstr(InMessage.Return.CommandStr, " noqueue") )  request_str += " noqueue";

                expectMore = true;
                CtiRequestMsg newReq(getID(),
                                        request_str,
                                        InMessage.Return.UserID,
                                        InMessage.Return.GrpMsgID,
                                        InMessage.Return.RouteID,
                                        selectInitialMacroRouteOffset(InMessage.Return.RouteID),
                                        0,
                                        0,
                                        InMessage.Priority);

                newReq.setConnectionHandle(InMessage.Return.Connection);

                //  same UserMessageID, no need to reset the in_progress flag
                beginExecuteRequest(&newReq, CtiCommandParser(newReq.CommandString()), vgList, retList, outList);
            }
            else
            {
                resultString += "Multi-day daily read request complete\n";

                _daily_read_info.request.in_progress = false;
            }
        }
    }
}

void Mct410Device::decodeGetValueDailyRead_SingleDay(const INMESS& InMessage, std::string& resultString, const DSTRUCT* DSt, YukonError_t& status, bool& expectMore, CtiReturnMsg* ReturnMsg, std::string consumption_pointname, std::string demand_pointname)
{
    int day, month, channel;

    int       expected_month   = _daily_read_info.request.begin.month() - 1;
    const int expected_day     = _daily_read_info.request.begin.dayOfMonth();

    if( _daily_read_info.request.channel == 1 &&
        _daily_read_info.request.type == daily_read_info_t::Request_SingleDay )
    {
        channel = 1;
        month   =  (DSt->Message[8] & 0xc0) >> 6;  //  2 bits
        day     =  (DSt->Message[8] & 0x3e) >> 1;  //  5 bits

        expected_month &= 0x03;  //  only 2 bits returned for month, so mask the expected month to match
    }
    else
    {
        channel = ((DSt->Message[10] & 0x30) >> 4) + 1;
        month   =   DSt->Message[10] & 0x0f;  //  4 bits, all 12 months can be represented
        day     =   DSt->Message[9];

        setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, channel);
    }

    //  These two need to be available to be checked against ErrorText_OutOfRange below
    point_info reading = getAccumulatorData(DSt->Message + 0, 3, 0);
    point_info peak    = getData(DSt->Message + 3, 2, ValueType_DynamicDemand);

    if( channel != _daily_read_info.request.channel )
    {
        resultString  = getName() + " / Invalid channel returned by daily read ";
        resultString += "(" + CtiNumStr(channel) + "), expecting (" + CtiNumStr(_daily_read_info.request.channel) + ")";

        status = ClientErrors::InvalidChannel;
    }
    else if( day   != expected_day ||
                month != expected_month )
    {
        resultString  = getName() + " / Invalid day/month returned by daily read ";
        resultString += "(" + CtiNumStr(day) + "/" + CtiNumStr(month + 1) + ", expecting " + CtiNumStr(expected_day) + "/" + CtiNumStr(expected_month + 1) + ")";

        //  These come back as 0x7ff.. if daily reads are disabled, but also if the request really was out of range
        //  Ideally, this check would be against an enum or similar rather than a string,
        //    but unless getAccumulatorData is refactored to provide more than a text description and quality, this will have to do.
        if( reading.description == ErrorText_OutOfRange
            && peak.description == ErrorText_OutOfRange )
        {
            resultString += "\n";
            resultString += getName() + " / Daily reads might be disabled, check device configuration";
        }

        _daily_read_info.interest.date = DawnOfTime_Date;  //  reset it - it doesn't match what the MCT has

        status = ClientErrors::InvalidTimestamp;
    }
    else if( _daily_read_info.request.channel == 1
                && _daily_read_info.request.type == daily_read_info_t::Request_SingleDay
                && _daily_read_info.interest.needs_verification )
    {
        resultString  = getName() + " / Daily read period of interest date was not verified, try read again";

        //  when they try the read again, it'll re-read the period of interest from the meter

        status = ClientErrors::InvalidTimestamp;
    }
    else
    {
        int time_peak;

        if( _daily_read_info.request.channel == 1 &&
            _daily_read_info.request.type == daily_read_info_t::Request_SingleDay )
        {
            time_peak        = ((DSt->Message[8]  & 0x01) << 10) |  //  1 bit
                                ((DSt->Message[9]  & 0xff) <<  2) |  //  8 bits
                                ((DSt->Message[10] & 0xc0) >>  6);   //  2 bits

            int time_voltage_max = ((DSt->Message[10] & 0x3f) <<  5) |  //  6 bits
                                    ((DSt->Message[11] & 0xf8) >>  3);   //  5 bits

            int time_voltage_min = ((DSt->Message[11] & 0x07) <<  8) |  //  3 bits
                                    ((DSt->Message[12] & 0xff) <<  0);   //  8 bits

            point_info voltage;

            voltage.value = DSt->Message[7] | ((DSt->Message[6] & 0x0f) << 8);

            if( voltage.value == 0x7fa )
            {
                voltage.value   = 0;
                voltage.quality = InvalidQuality;
                voltage.description = ErrorText_OutOfRange;
            }
            else
            {
                voltage.quality = NormalQuality;
            }

            insertPointDataReport(DemandAccumulatorPointType, PointOffset_VoltageMin, ReturnMsg,
                                    voltage, "Minimum Voltage", CtiTime(_daily_read_info.request.begin) + (time_voltage_min * 60), 0.1);

            voltage.value = ((DSt->Message[6] & 0xf0) >> 4) | (DSt->Message[5] << 4);

            if( voltage.value == 0x7fa )
            {
                voltage.value   = 0;
                voltage.quality = InvalidQuality;
                voltage.description = ErrorText_OutOfRange;
            }
            else
            {
                voltage.quality = NormalQuality;
            }

            insertPointDataReport(DemandAccumulatorPointType, PointOffset_VoltageMax, ReturnMsg,
                                    voltage, "Maximum Voltage", CtiTime(_daily_read_info.request.begin) + (time_voltage_max * 60), 0.1);
        }
        else
        {
            time_peak = (DSt->Message[5] << 8) | DSt->Message[6];

            point_info outage_count = getData(DSt->Message + 7, 2, ValueType_OutageCount);

            insertPointDataReport(PulseAccumulatorPointType, PointOffset_Accumulator_Powerfail, ReturnMsg,
                                    outage_count, "Blink Counter",  CtiTime(_daily_read_info.request.begin + 1));  //  add on 24 hours - end of day
        }

        //  adjust for the demand interval
        peak.value *= 3600 / getDemandInterval();

        if( channel > 1
            && !getDevicePointOffsetTypeEqual(channel, PulseAccumulatorPointType)
            && !getDevicePointOffsetTypeEqual(channel, DemandAccumulatorPointType) )
        {
            resultString += "No points defined for channel " + CtiNumStr(channel);
        }
        else
        {
            insertPointDataReport(PulseAccumulatorPointType, channel, ReturnMsg,
                                    reading, consumption_pointname,  CtiTime(_daily_read_info.request.begin + 1), //  add on 24 hours - end of day
                                    0.1, TAG_POINT_MUST_ARCHIVE);

            insertPointDataReport(DemandAccumulatorPointType, channel, ReturnMsg,
                                    peak, demand_pointname,  CtiTime(_daily_read_info.request.begin) + (time_peak * 60));

            if( channel == 1 && getDevicePointOffsetTypeEqual(PointOffset_PeakDemandDaily, DemandAccumulatorPointType) )
            {
                //  Insert the peak demand daily report for channel 1
                insertPointDataReport(DemandAccumulatorPointType, PointOffset_PeakDemandDaily, ReturnMsg,
                                        peak, demand_pointname, CtiTime(_daily_read_info.request.begin) + (time_peak * 60),
                                        0.1, TAG_POINT_MUST_ARCHIVE);
            }
        }

        _daily_read_info.request.in_progress = false;
    }
}


string Mct410Device::describeStatusAndEvents(const unsigned char *buf)
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
        descriptor += (buf[2] & 0x08)?"Reverse power flag set\n":"";
        //  0x10 - 0x80 aren't used yet

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


YukonError_t Mct410Device::decodeGetStatusInternal( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT &DSt = InMessage.Buffer.DSt;

    string resultString;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

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

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Mct410Device::decodeGetStatusLoadProfile( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    string resultString;
    unsigned long tmpTime;
    CtiTime lpTime;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

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

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

YukonError_t Mct410Device::decodeGetStatusFreeze( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
     YukonError_t status = ClientErrors::None;

     const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

     string resultString;
     unsigned long tmpTime;
     CtiTime lpTime;

     CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
     CtiPointDataMsg      *pData = NULL;

     ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
     ReturnMsg->setUserMessageId(InMessage.Return.UserID);

     resultString += getName() + " / Freeze status:\n";

     tmpTime = DSt->Message[0] << 24 |
               DSt->Message[1] << 16 |
               DSt->Message[2] <<  8 |
               DSt->Message[3];

     updateFreezeInfo(DSt->Message[4], tmpTime);

     CtiTime lastFreeze(tmpTime);
     if( lastFreeze.isValid() )
     {
         resultString += "Last freeze timestamp: " + lastFreeze.asString() + "\n";
     }
     else
     {
         resultString += "Last freeze timestamp: (no freeze recorded)\n";
     }

     resultString += "Freeze counter: " + CtiNumStr(getCurrentFreezeCounter()) + "\n";
     resultString += "Next freeze expected: freeze ";
     resultString += ((getCurrentFreezeCounter() % 2)?("two"):("one"));
     resultString += "\n";

     tmpTime = DSt->Message[5] << 24 |
               DSt->Message[6] << 16 |
               DSt->Message[7] <<  8 |
               DSt->Message[8];

     //  we should eventually save the voltage freeze info as well

     CtiTime lastVoltageFreeze(tmpTime);
     if( lastVoltageFreeze.isValid() )
     {
         resultString += "Last voltage freeze timestamp: " + lastVoltageFreeze.asString() + "\n";
     }
     else
     {
         resultString += "Last voltage freeze timestamp: (no freeze recorded)\n";
     }

     resultString += "Voltage freeze counter: " + CtiNumStr(static_cast<unsigned>(DSt->Message[9])) + "\n";
     resultString += "Next voltage freeze expected: freeze ";
     resultString += ((DSt->Message[9] % 2)?("two"):("one"));
     resultString += "\n";

     resultString += "\n";

     ReturnMsg->setResultString(resultString);

     retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

     return status;
}

YukonError_t Mct410Device::decodeGetConfigIntervals(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

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

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

YukonError_t Mct410Device::decodeGetConfigThresholds(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    string resultString;

    float ov = (DSt->Message[0]) << 8 | DSt->Message[1],
          uv = (DSt->Message[2]) << 8 | DSt->Message[3];

    ov /= 10.0;
    uv /= 10.0;

    resultString  = getName() + " / Over Voltage Threshold: " + CtiNumStr(ov, 1) + string(" volts\n");
    resultString += getName() + " / Under Voltage Threshold: " + CtiNumStr(uv, 1) + string(" volts\n");

    if( DSt->Message[4] )
    {
        resultString += getName() + " / Outage threshold: " + CtiNumStr(DSt->Message[4]) + " cycles\n";
    }
    else
    {
        resultString += getName() + " / Outage threshold: disabled\n";
    }

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

YukonError_t Mct410Device::decodeGetConfigFreeze(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    string resultString;
    unsigned day = DSt->Message[0];

    if( day > 31 )
    {
        resultString  = getName() + " / Scheduled day of freeze: (last day of month)\n";
    }
    else if( day )
    {
        resultString  = getName() + " / Scheduled day of freeze: " + CtiNumStr(day) + string("\n");
    }
    else
    {
        resultString  = getName() + " / Scheduled day of freeze: (disabled)\n";
    }

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

YukonError_t Mct410Device::decodeGetConfigMeterParameters(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    string resultString;
    int transformer_ratio = -1;

    if( InMessage.Sequence == EmetconProtocol::GetConfig_MeterParameters )
    {
        resultString = getName() + " / Meter Parameters:\n";

        switch( DSt->Message[0] & 0x03 )
        {
            case 0x0:   resultString += "5x1 display (5 digits, 1kWHr resolution)\n";   break;
            case 0x1:   resultString += "4x1 display (4 digits, 1kWHr resolution)\n";   break;
            case 0x2:   resultString += "4x10 display (4 digits, 10kWHr resolution)\n"; break;
            case 0x3:   resultString += "6x1 display (6 digits, 1kWHr resolution)\n";   break;
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
            transformer_ratio = DSt->Message[10];
        }
    }
    else if( InMessage.Sequence == EmetconProtocol::GetConfig_Multiplier )
    {
        transformer_ratio = DSt->Message[0];
    }

    if( transformer_ratio >= 0 )
    {
        resultString += getName() + " / Transformer ratio: " + CtiNumStr(transformer_ratio);
    }

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}



YukonError_t Mct410Device::decodeGetConfigDisconnect(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    int state = 0;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    string resultStr;

    CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    switch( DSt->Message[0] & 0x03 )
    {
        case RawStatus_Connected:               state = StateGroup_Connected;      break;
        case RawStatus_ConnectArmed:            state = StateGroup_ConnectArmed;   break;
        case RawStatus_DisconnectedUnconfirmed: state = StateGroup_DisconnectedUnconfirmed; break;
        case RawStatus_DisconnectedConfirmed:   state = StateGroup_DisconnectedConfirmed;   break;
    }

    point_info pi_disconnect;
    pi_disconnect.value   = state;
    pi_disconnect.quality = NormalQuality;

    insertPointDataReport(StatusPointType, 1, ReturnMsg, pi_disconnect, "Disconnect status", TimeNow, 1.0, TAG_POINT_MUST_ARCHIVE);

    resultStr  = getName() + " / Disconnect Info:\n";

    resultStr += decodeDisconnectStatus(*DSt);

    resultStr += getName() + " / Disconnect Config:\n";

    resultStr += decodeDisconnectConfig(*DSt);

    ReturnMsg->setResultString(resultStr);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


string Mct410Device::decodeDisconnectStatus(const DSTRUCT &DSt) const
{
    string resultStr;

    if( DSt.Message[0] & 0x04 )
    {
        resultStr += "Load limiting mode active\n";
    }

    if( DSt.Message[0] & 0x08 )
    {
        resultStr += "Cycling mode active, currently ";

        if( DSt.Message[0] & 0x10 )  resultStr += "connected\n";
        else                         resultStr += "disconnected\n";
    }

    if( DSt.Message[0] & 0x20 )
    {
        resultStr += "Disconnect state uncertain (powerfail during disconnect)\n";
    }

    if( DSt.Message[1] & 0x02 )
    {
        resultStr += "Disconnect error - demand detected after disconnect command sent to collar\n";
    }

    resultStr += "Disconnect load limit count: " + CtiNumStr(DSt.Message[8]) + string("\n");

    return resultStr;
}



string Mct410Device::decodeDisconnectConfig(const DSTRUCT &DSt)
{
    string resultStr;

    if( isSupported(Feature_DisconnectCollar) )
    {
        long disconnectaddress = DSt.Message[2] << 16 |
                                 DSt.Message[3] <<  8 |
                                 DSt.Message[4];

        resultStr += "Disconnect receiver address: " + CtiNumStr(disconnectaddress) + string("\n");
    }

    resultStr += "Disconnect load limit connect delay: " + CtiNumStr(DSt.Message[7]) + string(" minutes\n");

    boost::optional<int> config_byte;

    if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) >= SspecRev_Disconnect_ConfigReadEnhanced
        && DSt.Length >= 13 )
    {
        //  assign over the top of the DynamicPaoInfo value - it will either match or be more recent
        //    extractDynamicPaoInfo() gets called AFTER ModelDecode(), so
        //    the new value won't be available via getDynamicInfo() until after this decode
        config_byte = DSt.Message[11];

        //  threshold is in units of Wh/minute, so we convert it into kW
        resultStr += "Disconnect verification threshold: " + CtiNumStr((float)DSt.Message[12] / 16.667, 3) + string(" kW (" + CtiNumStr(DSt.Message[12]) + " Wh/minute)\n");
    }
    else if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) )
    {
        config_byte = getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration);
    }

    point_info demand_threshold = getData(DSt.Message + 5, 2, ValueType_DynamicDemand);

    //  adjust for the demand interval
    demand_threshold.value *= 3600 / getDemandInterval();
    //  adjust for the 0.1 kWh factor of getData()
    demand_threshold.value /= 10.0;

    //  no need to check if it's not supported
    if( isSupported(Feature_DisconnectCollar) )
    {
        if( config_byte && *config_byte & 0x04 )
        {
            resultStr += "Autoreconnect enabled\n";
        }
    }

    resultStr += decodeDisconnectDemandLimitConfig(config_byte, demand_threshold.value);

    //  include the cycle information
    if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) >= SspecRev_Disconnect_Cycle )
    {
        resultStr += decodeDisconnectCyclingConfig(config_byte, DSt.Message[9], DSt.Message[10]);
    }

    return resultStr;
}


string Mct410Device::decodeDisconnectDemandLimitConfig(const boost::optional<int> config_byte, double demand_threshold)
{
    string resultStr;

    //  only the config byte tells us if demand limit mode is really enabled, so punt otherwise
    const bool demand_enabled  = config_byte && (*config_byte & 0x08);

    if( demand_threshold && (demand_enabled || !config_byte) )
    {
        if( demand_enabled  )
        {
            resultStr += "Demand limit mode enabled\n";
        }

        resultStr += "Disconnect demand threshold: ";
        resultStr += CtiNumStr(demand_threshold) + string(" kW\n");
    }
    else
    {
        resultStr += "Disconnect demand threshold disabled\n";
    }

    return resultStr;
}


string Mct410Device::decodeDisconnectCyclingConfig(const boost::optional<int> config_byte, const int disconnect_minutes, const int connect_minutes)
{
    string resultStr;

    //  cycling only - demand limit mode takes precedence, if it's set
    const bool cycling_enabled = config_byte && (*config_byte & 0x18) == 0x10;

    const bool has_minutes = disconnect_minutes && connect_minutes;

    //  if we don't have the config byte, we report the cycling minutes if they're set
    if( cycling_enabled || (!config_byte && has_minutes) )
    {
        if( cycling_enabled )
        {
            resultStr += "Disconnect cycling mode enabled\n";
        }

        resultStr += "Cycling mode - disconnect minutes: " + CtiNumStr(disconnect_minutes)  + string("\n");
        resultStr += "Cycling mode - connect minutes   : " + CtiNumStr(connect_minutes) + string("\n");
    }
    else
    {
        resultStr += "Disconnect cycling mode disabled\n";
    }

    return resultStr;
}


YukonError_t Mct410Device::decodeGetConfigAddress(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    string resultStr;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    long address = DSt->Message[0] << 16 |
                   DSt->Message[1] <<  8 |
                   DSt->Message[2];

    resultStr  = getName() + " / Unique address: " + CtiNumStr(address);

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultStr);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Mct410Device::decodeGetConfigPhaseDetect(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    string resultStr;
//Voltage Phase(byte0)
//Voltage Phase Timestamp(byte 1-4)
//First Interval Voltage (byte 5-6)
//Last Interval Voltage (byte 7-8)
    string phaseStr;
    unsigned long volt_timestamp;
    float first_interval_voltage, last_interval_voltage;

    CtiReturnMsg    *ReturnMsg = NULL;  // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

//       Binary Values: 00 - Phase Unknown (default)
//                      01 - Phase A
//                      10 - Phase B
//                      11 - Phase C
//                      Other - Invalid
    int phase = DSt->Message[0] & 0xFF;
    point_info pi_phase;
    pi_phase.quality = NormalQuality;
    pi_phase.value = phase;
    switch( phase )
    {
        case 1:
        {
            phaseStr = "A";
            break;
        }
        case 2:
        {
            phaseStr = "B";
            break;
        }
        case 3:
        {
            phaseStr = "C";
            break;
        }
        case 0:
        default:
        {
            phaseStr = "Unknown";
            break;
        }
    }

    volt_timestamp =  DSt->Message[1] << 24 |
                      DSt->Message[2] << 16 |
                      DSt->Message[3] <<  8 |
                      DSt->Message[4];
    first_interval_voltage = (DSt->Message[5] <<  8 |
                              DSt->Message[6] ) * 0.1;
    last_interval_voltage =  (DSt->Message[7] <<  8 |
                              DSt->Message[8] ) * 0.1;

    if (InMessage.Sequence == EmetconProtocol::GetConfig_PhaseDetectArchive)
    {
        insertPointDataReport(StatusPointType, PointOffset_Status_PhaseDetect, ReturnMsg, pi_phase, "Phase", CtiTime(volt_timestamp), 1.0, TAG_POINT_MUST_ARCHIVE);
    }
    resultStr  = getName() + " / Phase = " + phaseStr ;
    resultStr += " @ " + CtiTime(volt_timestamp).asString();
    resultStr  +="\nFirst Interval Voltage: " + CtiNumStr(first_interval_voltage, 1);
    resultStr  += " / Last Interval Voltage: " + CtiNumStr(last_interval_voltage, 1);

    ReturnMsg->setResultString(resultStr);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

YukonError_t Mct410Device::decodeGetConfigModel(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT &DSt = InMessage.Buffer.DSt;

    string descriptor;

    int ssp, rev;
    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    ssp  = DSt.Message[0];
    ssp |= DSt.Message[4] << 8;

    rev  = (unsigned)DSt.Message[1];

    descriptor += getName() + " / Model information:\n";
    descriptor += "Software Specification " + CtiNumStr(ssp) + " rev ";

    //  convert 10 to 1.0, 24 to 2.4
    descriptor += CtiNumStr(((double)rev) / 10.0, 1);

    //  valid/released versions are 1.0 - 24.9
    if( rev <= SspecRev_BetaLo ||
        rev >= SspecRev_BetaHi )
    {
        descriptor += " [possible development revision]";
    }

    descriptor += "\n";

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
        if( DSt.Message[3] & 0x04 )      descriptor += "Autoreconnect enabled\n";

        if(      DSt.Message[3] & 0x08 ) descriptor += "Demand limit mode active\n";
        else if( DSt.Message[3] & 0x10 ) descriptor += "Disconnect cycling mode active\n";

        if( DSt.Message[3] & 0x20 )      descriptor += "Role code enabled\n";

        if( rev >= SspecRev_DailyRead )
        {
            descriptor += "Daily reporting ";
            descriptor += (DSt.Message[3] & 0x80) ? "enabled\n" : "disabled\n";
        }
    }

    descriptor += getName() + " / Status and events:\n";

    descriptor += describeStatusAndEvents(DSt.Message + 5);

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(descriptor);

    //  this is hackish, and should be handled in a more centralized manner...  retMsgHandler, for example
    if( InMessage.MessageFlags & MessageFlag_ExpectMore )
    {
        ReturnMsg->setExpectMore(true);
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


bool Mct410Device::disconnectRequiresCollar() const
{
    return isSupported(Feature_DisconnectCollar);
}


bool Mct410Device::isSupported(const Features feature) const
{
    switch( feature )
    {
        case Feature_HourlyKwh:
        {
            return sspecAtLeast(SspecRev_HourlyKwh);
        }
        case Feature_Disconnect:
        case Feature_DisconnectCollar:
        {
            //  all MCT-410s support the disconnect collar
            return true;
        }
        case Feature_OutageUnits:
        {
            return sspecAtLeast(SspecRev_NewOutage_Min);
        }
        default:
        {
            return false;
        }
    }
}


bool Mct410Device::isSupported(const Mct4xxDevice::Features feature) const
{
    switch( feature )
    {
        case Feature_LoadProfilePeakReport:
        {
            return sspecAtLeast(SspecRev_NewLLP_Min);
        }
        case Feature_TouPeaks:
        {
            return sspecAtLeast(SspecRev_TOUPeak_Min);
        }
        default:
        {
            //  does not call the parent, since it's a pure virtual
            return false;
        }
    }
}


bool Mct410Device::sspecValid(const unsigned sspec, const unsigned rev) const
{
    return sspec == Mct410Device::Sspec;
}

YukonError_t Mct410Device::decodeGetConfigWaterMeterReadInterval( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    unsigned duration = DSt->Message[0];

    std::string resultString( getName() + " / Water Meter Read Interval: " );

    int minutes = ( duration & 0x7f );

    if ( minutes == 0 )
    {
        minutes = ( 12 * 60 );      // 12 hours by default
    }
    else if ( duration & 0x80 )     // 15 minute granularity
    {
        minutes *= 15;
    }
    else                            // 5 minute granularity
    {
        minutes *= 5;
    }

    int hours = minutes / 60;
    minutes %= 60;

    if ( hours )
    {
        resultString += CtiNumStr(hours) + " hour";
        if ( hours > 1 )
        {
            resultString += "s";
        }
    }

    if ( minutes )
    {
        resultString +=  " " + CtiNumStr(minutes) + " minutes";
    }
    resultString += "\n";


    CtiReturnMsg * ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

YukonError_t Mct410Device::decodeGetConfigLongLoadProfileStorageDays( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    std::string resultString( getName() + " / Long Load Profile Allocation:\n" );

    resultString += "Channel 1: " + CtiNumStr(DSt->Message[4]) + " days at 15 minute allocation\n";
    resultString += "Channel 2: " + CtiNumStr(DSt->Message[5]) + " days at 15 minute allocation\n";
    resultString += "Channel 3: " + CtiNumStr(DSt->Message[6]) + " days at 15 minute allocation\n";
    resultString += "Channel 4: " + CtiNumStr(DSt->Message[7]) + " days at 15 minute allocation\n";


    CtiReturnMsg * ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

}
}

