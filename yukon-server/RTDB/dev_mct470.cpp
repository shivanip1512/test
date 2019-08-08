#include "precompiled.h"

#include "devicetypes.h"
#include "tbl_ptdispatch.h"
#include "dev_mct470.h"
#include "logger.h"
#include "pt_accum.h"
#include "pt_status.h"
#include "dllyukon.h"
#include "config_data_mct.h"
#include "config_exceptions.h"
#include "da_lp_deviceconfig.h"

#include "std_helper.h"

#include <string>

#include <boost/algorithm/string/predicate.hpp>
#include <boost/make_shared.hpp>

using namespace Cti::Protocols;
using namespace Cti::Config;
using std::string;
using std::endl;
using std::list;
using std::pair;
using std::vector;
using std::make_pair;

namespace Cti {
namespace Devices {

const Mct470Device::CommandSet       Mct470Device::_commandStore = Mct470Device::initCommandStore();
const Mct470Device::ConfigPartsList  Mct470Device::_config_parts_430 = Mct470Device::initConfigParts430();
const Mct470Device::ConfigPartsList  Mct470Device::_config_parts_470 = Mct470Device::initConfigParts470();

const Mct470Device::ValueMapping Mct470Device::_memoryMap = Mct470Device::initMemoryMap();
const Mct470Device::FunctionReadValueMappings Mct470Device::_functionReadValueMaps = Mct470Device::initFunctionReadValueMaps();

const Mct470Device::error_map Mct470Device::_error_info_old_lp    = Mct470Device::initErrorInfoOldLP();
const Mct470Device::error_map Mct470Device::_error_info_lgs4      = Mct470Device::initErrorInfoLGS4();
const Mct470Device::error_map Mct470Device::_error_info_alphaa3   = Mct470Device::initErrorInfoAlphaA3();
const Mct470Device::error_map Mct470Device::_error_info_alphapp   = Mct470Device::initErrorInfoAlphaPP();
const Mct470Device::error_map Mct470Device::_error_info_gekv      = Mct470Device::initErrorInfoGEkV();
const Mct470Device::error_map Mct470Device::_error_info_sentinel  = Mct470Device::initErrorInfoSentinel();

/** Map to translate Device Config Electronic Meter Types to an enuneration */
const std::map<std::string, long> Mct470Device::IED_TypeMap {
    {"NONE", Mct470Device::IED_Types::IED_Type_None},
    {"S4", Mct470Device::IED_Types::IED_Type_LG_S4},
    {"ALPHA_A3", Mct470Device::IED_Types::IED_Type_Alpha_A3},
    {"ALPHA_P_PLUS", Mct470Device::IED_Types::IED_Type_Alpha_PP},
    {"GEKV", Mct470Device::IED_Types::IED_Type_GE_kV},
    {"GEKV2", Mct470Device::IED_Types::IED_Type_GE_kV2},
    {"SENTINEL", Mct470Device::IED_Types::IED_Type_Sentinel},
    {"DNP", Mct470Device::IED_Types::IED_Type_DNP},
    {"GEKV2C", Mct470Device::IED_Types::IED_Type_GE_kV2c}
};

/** Map to translate Device Config Meter Types to an enuneration */
const std::map<std::string, long> Mct470Device::channelTypeMap {
    {"CHANNEL_NOT_USED", 0},
    {"ELECTRONIC_METER", 1},
    {"TWO_WIRE_KYZ_FORM_A", 2},
    {"THREE_WIRE_KYZ_FORM_C", 3}
};

/** Map to translate Device Config Resolutions to an enuneration */
const std::map<std::string, double> Mct470Device::resolutionMap {
    {"MINUS_TWO", 0.01},
    {"MINUS_ONE", 0.1},
    {"ZERO", 1},
    {"ONE", 10}
};

Mct470Device::Mct470Device( ) :
    _lastConfigRequest(0)
{
}

Mct470Device::ConfigPartsList Mct470Device::initConfigParts430()
{
    Mct470Device::ConfigPartsList tempList;

    tempList.push_back(Mct4xxDevice::PutConfigPart_timezone);
    tempList.push_back(Mct4xxDevice::PutConfigPart_time_adjust_tolerance);
    tempList.push_back(Mct4xxDevice::PutConfidPart_spid);//Before Precanned Table
    tempList.push_back(Mct4xxDevice::PutConfigPart_demand_lp);
    tempList.push_back(Mct4xxDevice::PutConfigPart_lpchannel);
    tempList.push_back(Mct4xxDevice::PutConfigPart_precanned_table);
    tempList.push_back(Mct4xxDevice::PutConfigPart_configbyte);

    return tempList;
}

Mct470Device::ConfigPartsList Mct470Device::initConfigParts470()
{
    Mct470Device::ConfigPartsList tempList;

    tempList.push_back(Mct4xxDevice::PutConfigPart_timezone);
    tempList.push_back(Mct4xxDevice::PutConfigPart_time_adjust_tolerance);
    tempList.push_back(Mct4xxDevice::PutConfidPart_spid);//Before Precanned Table
    tempList.push_back(Mct4xxDevice::PutConfigPart_demand_lp);
    tempList.push_back(Mct4xxDevice::PutConfigPart_lpchannel);
    tempList.push_back(Mct4xxDevice::PutConfigPart_precanned_table);
    tempList.push_back(Mct4xxDevice::PutConfigPart_configbyte);
    tempList.push_back(Mct4xxDevice::PutConfigPart_relays);
    tempList.push_back(Mct4xxDevice::PutConfigPart_tou);

    return tempList;
}

Mct470Device::ConfigPartsList Mct470Device::getPartsList()
{
    if (getType() == TYPEMCT470)
    {
        return _config_parts_470;
    }
    else
    {
        return _config_parts_430;
    }
}

Mct470Device::CommandSet Mct470Device::initCommandStore( )
{
    CommandSet cs;

    cs.insert(CommandStore(EmetconProtocol::Scan_Accum,                 EmetconProtocol::IO_Function_Read,  FuncRead_MReadPos,            FuncRead_MReadLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_KWH,               EmetconProtocol::IO_Function_Read,  FuncRead_MReadPos,            FuncRead_MReadLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_FrozenKWH,         EmetconProtocol::IO_Function_Read,  FuncRead_MReadFrozenPos,      FuncRead_MReadFrozenLen));
    cs.insert(CommandStore(EmetconProtocol::Scan_Integrity,             EmetconProtocol::IO_Function_Read,  FuncRead_DemandPos,           FuncRead_DemandLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_Demand,            EmetconProtocol::IO_Function_Read,  FuncRead_DemandPos,           FuncRead_DemandLen));
    cs.insert(CommandStore(EmetconProtocol::Scan_LoadProfile,           EmetconProtocol::IO_Function_Read,  0,                             0));
    cs.insert(CommandStore(EmetconProtocol::GetValue_Demand,            EmetconProtocol::IO_Function_Read,  FuncRead_DemandPos,           FuncRead_DemandLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_PeakDemand,        EmetconProtocol::IO_Function_Read,  FuncRead_PeakDemandPos,       FuncRead_PeakDemandLen));
    cs.insert(CommandStore(EmetconProtocol::PutValue_KYZ,               EmetconProtocol::IO_Function_Write, FuncWrite_CurrentReading,     FuncWrite_CurrentReadingLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Raw,              EmetconProtocol::IO_Write,          0,                             0));  //  filled in later
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Raw,              EmetconProtocol::IO_Read,           0,                             0));  //  filled in later
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Multiplier,       EmetconProtocol::IO_Function_Read,  FuncRead_LoadProfileChannel12Pos, FuncRead_LoadProfileChannel12Len));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Multiplier,       EmetconProtocol::IO_Write,          Memory_ChannelMultiplierPos,  Memory_ChannelMultiplierLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Time,             EmetconProtocol::IO_Read,           Memory_TimeZoneOffsetPos,     Memory_TimeZoneOffsetLen +
                                                                                                                          Memory_RTCLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_TSync,            EmetconProtocol::IO_Read,           Memory_LastTSyncPos,          Memory_LastTSyncLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_TimeZoneOffset,   EmetconProtocol::IO_Write,          Memory_TimeZoneOffsetPos,     Memory_TimeZoneOffsetLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Intervals,        EmetconProtocol::IO_Function_Write, FuncWrite_IntervalsPos,       FuncWrite_IntervalsLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Intervals,        EmetconProtocol::IO_Read,           Memory_IntervalsPos,          Memory_IntervalsLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_PrecannedTable,   EmetconProtocol::IO_Function_Write, FuncWrite_PrecannedTablePos,  FuncWrite_PrecannedTableLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_ChannelSetup,     EmetconProtocol::IO_Function_Read,  FuncRead_ChannelSetupDataPos, FuncRead_ChannelSetupDataLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_ChannelSetup,     EmetconProtocol::IO_Function_Write, FuncWrite_SetupLPChannelsPos, FuncWrite_SetupLPChannelLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_LoadProfile,       EmetconProtocol::IO_Function_Read,  0,                             0));
    cs.insert(CommandStore(EmetconProtocol::GetStatus_LoadProfile,      EmetconProtocol::IO_Function_Read,  FuncRead_LPStatusCh1Ch2Pos,   FuncRead_LPStatusLen));
    cs.insert(CommandStore(EmetconProtocol::GetStatus_Internal,         EmetconProtocol::IO_Read,           Memory_StatusPos,             Memory_StatusLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_PFCount,           EmetconProtocol::IO_Read,           Memory_PowerfailCountPos,     Memory_PowerfailCountLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_IED,               EmetconProtocol::IO_Function_Read,  0,                            13));  //  filled in by "getvalue ied" code
    cs.insert(CommandStore(EmetconProtocol::GetValue_IEDDemand,         EmetconProtocol::IO_Function_Read,  FuncRead_IED_RealTime,        12));   //  magic number
    cs.insert(CommandStore(EmetconProtocol::GetStatus_IEDDNP,           EmetconProtocol::IO_Function_Read,  FuncRead_IED_Precanned_Last,  13));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_IEDTime,          EmetconProtocol::IO_Function_Read,  FuncRead_IED_TOU_MeterStatus, 13));  //  magic number
    cs.insert(CommandStore(EmetconProtocol::GetConfig_IEDDNP,           EmetconProtocol::IO_Function_Read,  FuncRead_IED_DNPTablePos,     FuncRead_IED_DNPTableLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_IEDDNPAddress,    EmetconProtocol::IO_Read,           Memory_IedDnpAddressPos,      Memory_IedDnpAddressLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_IEDDNPAddress,    EmetconProtocol::IO_Write,          Memory_IedDnpAddressPos,      Memory_IedDnpAddressLen));
    cs.insert(CommandStore(EmetconProtocol::PutValue_IEDReset,          EmetconProtocol::IO_Function_Write, FuncWrite_IEDCommand,         FuncWrite_IEDCommandLen));
    cs.insert(CommandStore(EmetconProtocol::PutStatus_FreezeOne,        EmetconProtocol::IO_Write,          Command_FreezeOne,             0));
    cs.insert(CommandStore(EmetconProtocol::PutStatus_FreezeTwo,        EmetconProtocol::IO_Write,          Command_FreezeTwo,             0));
    cs.insert(CommandStore(EmetconProtocol::GetStatus_Freeze,           EmetconProtocol::IO_Read,           Memory_LastFreezeTimestampPos, Memory_LastFreezeTimestampLen
                                                                                                                             + Memory_FreezeCounterLen));

    cs.insert(CommandStore(EmetconProtocol::GetValue_PhaseCurrent,      EmetconProtocol::IO_Function_Read,  FuncRead_PhaseCurrent,        FuncRead_PhaseCurrentLen));

    //******************************** Config Related starts here *************************
    cs.insert(CommandStore(EmetconProtocol::PutConfig_LongLoadProfileStorage,  EmetconProtocol::IO_Function_Write, FuncWrite_LLPStoragePos,     FuncWrite_LLPStorageLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_LongLoadProfileStorage,  EmetconProtocol::IO_Function_Read,  FuncRead_LLPStatusPos,       FuncRead_LLPStatusLen));

    //  used for both "putconfig install" and "putconfig holiday" commands
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Holiday,          EmetconProtocol::IO_Write,          Memory_Holiday1Pos,          Memory_Holiday1Len
                                                                                                                           + Memory_Holiday2Len
                                                                                                                           + Memory_Holiday3Len));

    cs.insert(CommandStore(EmetconProtocol::GetConfig_Holiday,          EmetconProtocol::IO_Read,           Memory_Holiday1Pos,          Memory_Holiday1Len
                                                                                                                           + Memory_Holiday2Len
                                                                                                                           + Memory_Holiday3Len));

    cs.insert(CommandStore(EmetconProtocol::PutConfig_Options,               EmetconProtocol::IO_Function_Write,  FuncWrite_ConfigAlarmMaskPos,  FuncWrite_ConfigAlarmMaskLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_TimeAdjustTolerance,   EmetconProtocol::IO_Write,           Memory_TimeAdjustTolerancePos, Memory_TimeAdjustToleranceLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_SPID,                  EmetconProtocol::IO_Write,           Memory_AddressSPIDPos, Memory_AddressSPIDLen));

    //************************************ End Config Related *****************************

    return cs;
}

Mct470Device::ValueMapping Mct470Device::initMemoryMap()
{
    struct memory_read_value
    {
        unsigned offset;
        value_descriptor value;
    }
    const values[] =
    {
        { Memory_SspecPos,                  { Memory_SspecLen,                 CtiTableDynamicPaoInfo::Key_MCT_SSpec                      } },
        { Memory_RevisionPos,               { Memory_RevisionLen,              CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision              } },
        { Memory_OptionsPos,                { Memory_OptionsLen,               CtiTableDynamicPaoInfo::Key_MCT_Options                    } },
        { Memory_ConfigurationPos,          { Memory_ConfigurationLen,         CtiTableDynamicPaoInfo::Key_MCT_Configuration              } },
        { Memory_EventFlagsMask1Pos,        { Memory_EventFlagsMask1Len,       CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask1            } },
        { Memory_EventFlagsMask2Pos,        { Memory_EventFlagsMask2Len,       CtiTableDynamicPaoInfo::Key_MCT_EventFlagsMask2            } },
        { Memory_AddressBronzePos,          { Memory_AddressBronzeLen,         CtiTableDynamicPaoInfo::Key_MCT_AddressBronze              } },
        { Memory_AddressLeadPos,            { Memory_AddressLeadLen,           CtiTableDynamicPaoInfo::Key_MCT_AddressLead                } },
        { Memory_AddressCollectionPos,      { Memory_AddressCollectionLen,     CtiTableDynamicPaoInfo::Key_MCT_AddressCollection          } },
        { Memory_AddressSPIDPos,            { Memory_AddressSPIDLen,           CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID   } },
        { Memory_DemandIntervalPos,         { Memory_DemandIntervalLen,        CtiTableDynamicPaoInfo::Key_MCT_DemandInterval             } },
        { Memory_LoadProfileInterval1Pos,   { Memory_LoadProfileInterval1Len,  CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval        } },
        { Memory_LoadProfileInterval2Pos,   { Memory_LoadProfileInterval2Len,  CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval2       } },
        { Memory_TimeAdjustTolerancePos,    { Memory_TimeAdjustToleranceLen,   CtiTableDynamicPaoInfo::Key_MCT_TimeAdjustTolerance        } },
        { Memory_DSTBeginPos,               { Memory_DSTBeginLen,              CtiTableDynamicPaoInfo::Key_MCT_DSTStartTime               } },
        { Memory_DSTEndPos,                 { Memory_DSTEndLen,                CtiTableDynamicPaoInfo::Key_MCT_DSTEndTime                 } },
        { Memory_TimeZoneOffsetPos,         { Memory_TimeZoneOffsetLen,        CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset             } },
        { Memory_TOUDayTablePos,            { Memory_TOUDayTableLen,           CtiTableDynamicPaoInfo::Key_MCT_DayTable                   } },
        { Memory_TOUDailySched1Pos,         { Memory_TOUDailySched1Len,        CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1               } },
        { Memory_TOUDailySched2Pos,         { Memory_TOUDailySched2Len,        CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2               } },
        { Memory_TOUDailySched3Pos,         { Memory_TOUDailySched3Len,        CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3               } },
        { Memory_TOUDailySched4Pos,         { Memory_TOUDailySched4Len,        CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4               } },
        { Memory_TOUDefaultRatePos,         { Memory_TOUDefaultRateLen,        CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate             } },
        { Memory_Holiday1Pos,               { Memory_Holiday1Len,              CtiTableDynamicPaoInfo::Key_MCT_Holiday1                   } },
        { Memory_Holiday2Pos,               { Memory_Holiday2Len,              CtiTableDynamicPaoInfo::Key_MCT_Holiday2                   } },
        { Memory_Holiday3Pos,               { Memory_Holiday3Len,              CtiTableDynamicPaoInfo::Key_MCT_Holiday3                   } },
        { Memory_KRatio1Pos,                { Memory_KRatio1Len,               CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio1         } },
        { Memory_MeteringRatio1Pos,         { Memory_MeteringRatio1Len,        CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio1     } },
        { Memory_ChannelConfig1Pos,         { Memory_ChannelConfig1Len,        CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1  } },
        { Memory_KRatio2Pos,                { Memory_KRatio2Len,               CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio2         } },
        { Memory_MeteringRatio2Pos,         { Memory_MeteringRatio2Len,        CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio2     } },
        { Memory_ChannelConfig2Pos,         { Memory_ChannelConfig2Len,        CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2  } },
        { Memory_KRatio3Pos,                { Memory_KRatio3Len,               CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio3         } },
        { Memory_MeteringRatio3Pos,         { Memory_MeteringRatio3Len,        CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio3     } },
        { Memory_ChannelConfig3Pos,         { Memory_ChannelConfig3Len,        CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3  } },
        { Memory_KRatio4Pos,                { Memory_KRatio4Len,               CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio4         } },
        { Memory_MeteringRatio4Pos,         { Memory_MeteringRatio4Len,        CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio4     } },
        { Memory_ChannelConfig4Pos,         { Memory_ChannelConfig4Len,        CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4  } },

        { Memory_RelayATimerPos,            { Memory_RelayATimerLen,           CtiTableDynamicPaoInfo::Key_MCT_RelayATimer                } },
        { Memory_RelayBTimerPos,            { Memory_RelayBTimerLen,           CtiTableDynamicPaoInfo::Key_MCT_RelayBTimer                } },
        { Memory_TableReadIntervalPos,      { Memory_TableReadIntervalLen,     CtiTableDynamicPaoInfo::Key_MCT_PrecannedTableReadInterval } },
        { Memory_PrecannedMeterNumPos,      { Memory_PrecannedMeterNumLen,     CtiTableDynamicPaoInfo::Key_MCT_PrecannedMeterNumber       } },
        { Memory_PrecannedTableTypePos,     { Memory_PrecannedTableTypeLen,    CtiTableDynamicPaoInfo::Key_MCT_PrecannedTableType         } },
    };

    ValueMapping memoryMap;

    for each( memory_read_value mrv in values )
    {
        memoryMap.insert(std::make_pair(mrv.offset, mrv.value));
    }

    return memoryMap;
}


Mct470Device::FunctionReadValueMappings Mct470Device::initFunctionReadValueMaps()
{
    struct function_read_value
    {
        unsigned function;
        unsigned offset;
        value_descriptor value;
    }
    const values[] =
    {
        { FuncRead_ChannelSetupDataPos,      0, { 4, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig          } },
        //  duplicated on purpose - load profile config is decoded into these 5 keys
        { FuncRead_ChannelSetupDataPos,      0, { 1, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1  } },
        { FuncRead_ChannelSetupDataPos,      1, { 1, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2  } },
        { FuncRead_ChannelSetupDataPos,      2, { 1, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3  } },
        { FuncRead_ChannelSetupDataPos,      3, { 1, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4  } },
        { FuncRead_ChannelSetupDataPos,      4, { 1, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval        } },
        { FuncRead_ChannelSetupDataPos,      5, { 1, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval2       } },
        { FuncRead_ChannelSetupDataPos,      6, { 1, CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval     } },

        { FuncRead_LoadProfileChannel12Pos,  0, { 1, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1  } },
        { FuncRead_LoadProfileChannel12Pos,  1, { 2, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio1     } },
        { FuncRead_LoadProfileChannel12Pos,  3, { 2, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio1         } },
        { FuncRead_LoadProfileChannel12Pos,  5, { 1, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2  } },
        { FuncRead_LoadProfileChannel12Pos,  6, { 2, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio2     } },
        { FuncRead_LoadProfileChannel12Pos,  8, { 2, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio2         } },

        { FuncRead_LoadProfileChannel34Pos,  0, { 1, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3  } },
        { FuncRead_LoadProfileChannel34Pos,  1, { 2, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio3     } },
        { FuncRead_LoadProfileChannel34Pos,  3, { 2, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio3         } },
        { FuncRead_LoadProfileChannel34Pos,  5, { 1, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4  } },
        { FuncRead_LoadProfileChannel34Pos,  6, { 2, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio4     } },
        { FuncRead_LoadProfileChannel34Pos,  8, { 2, CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio4         } },

        { FuncRead_PrecannedTablePos,        0, { 1, CtiTableDynamicPaoInfo::Key_MCT_PrecannedTableReadInterval } },
        { FuncRead_PrecannedTablePos,        1, { 1, CtiTableDynamicPaoInfo::Key_MCT_PrecannedMeterNumber       } },
        { FuncRead_PrecannedTablePos,        2, { 1, CtiTableDynamicPaoInfo::Key_MCT_PrecannedTableType         } },

        { FuncRead_LLPStatusPos,             4, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel1Len             } },
        { FuncRead_LLPStatusPos,             5, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel2Len             } },
        { FuncRead_LLPStatusPos,             6, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel3Len             } },
        { FuncRead_LLPStatusPos,             7, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len             } },

        { FuncRead_IED_CRCPos,               1, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel1Len             } },
        { FuncRead_IED_CRCPos,               2, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel2Len             } },
        { FuncRead_IED_CRCPos,               3, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel3Len             } },
        { FuncRead_IED_CRCPos,               4, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len             } },
        { FuncRead_IED_CRCPos,               5, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len             } },
        { FuncRead_IED_CRCPos,               6, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len             } },
        { FuncRead_IED_CRCPos,               7, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len             } },
        { FuncRead_IED_CRCPos,               8, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len             } },
        { FuncRead_IED_CRCPos,               9, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len             } },
        { FuncRead_IED_CRCPos,              10, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len             } },
        { FuncRead_IED_CRCPos,              11, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len             } },
        { FuncRead_IED_CRCPos,              12, { 1, CtiTableDynamicPaoInfo::Key_MCT_LLPChannel4Len             } },

        { FuncRead_TOUDaySchedulePos,        0, { 2, CtiTableDynamicPaoInfo::Key_MCT_DayTable                   } },
        { FuncRead_TOUDaySchedulePos,        2, { 1, CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate             } },
        //  the existence of a default TOU rate also indicates that TOU is enabled (see decodeReadDataForKey() for details)
        { FuncRead_TOUDaySchedulePos,        2, { 1, CtiTableDynamicPaoInfo::Key_MCT_TouEnabled                 } },
        { FuncRead_TOUDaySchedulePos,       10, { 1, CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset             } },
    };

    FunctionReadValueMappings fr;

    for each( function_read_value frv in values )
    {
        fr[frv.function].insert(std::make_pair(frv.offset, frv.value));
    }

    return fr;
}


Mct470Device::error_map Mct470Device::initErrorInfoOldLP( void )
{
    error_map e;

    e.insert(error_map::value_type(0xffff7fff, error_details("Interval not recorded",   InvalidQuality)));
    e.insert(error_map::value_type(0xffff7ffe, error_details("Pulse count overflow",    OverflowQuality)));
    e.insert(error_map::value_type(0xffff7ffd, error_details("Time adjusted",           DeviceFillerQuality)));

    return e;
}


Mct470Device::error_map Mct470Device::initErrorInfoLGS4( void )
{
    error_map e;

    e.insert(error_map::value_type(0xffffffff, error_details("Interval not recorded",   InvalidQuality)));
    e.insert(error_map::value_type(0xfffffffe, error_details("Pulse count overflow",    OverflowQuality)));
    e.insert(error_map::value_type(0xfffffffd, error_details("Time adjusted",           DeviceFillerQuality)));

    //  not available for LLP - indicate somehow?
    e.insert(error_map::value_type(0xfffffffb, error_details("Time adjusted",           DeviceFillerQuality)));

    e.insert(error_map::value_type(0xfffffffa, error_details("LP data not available",   InvalidQuality)));
    e.insert(error_map::value_type(0xfffffff0, error_details("Overflow",                OverflowQuality)));

    return e;
}

Mct470Device::error_map Mct470Device::initErrorInfoAlphaA3( void )
{
    error_map e;

    e.insert(error_map::value_type(0xffffffff, error_details("LP data reset",           InvalidQuality)));
    //
    // not available for LP
    e.insert(error_map::value_type(0xfffffffd, error_details("LP error",                InvalidQuality)));

    //  not available for LLP
    e.insert(error_map::value_type(0xfffffffb, error_details("Time adjusted",           DeviceFillerQuality)));

    e.insert(error_map::value_type(0xfffffffa, error_details("LP data not available",   InvalidQuality)));
    e.insert(error_map::value_type(0xfffffff0, error_details("Overflow",                OverflowQuality)));

    return e;
}

Mct470Device::error_map Mct470Device::initErrorInfoAlphaPP( void )
{
    error_map e;

    e.insert(error_map::value_type(0xffffffff, error_details("LP data reset",           InvalidQuality)));
    e.insert(error_map::value_type(0xfffffffe, error_details("LP config error",         InvalidQuality)));
    e.insert(error_map::value_type(0xfffffffd, error_details("LP error",                InvalidQuality)));

    //  not available for LLP
    e.insert(error_map::value_type(0xfffffffb, error_details("Time adjusted",           DeviceFillerQuality)));

    e.insert(error_map::value_type(0xfffffffa, error_details("LP data not available",   InvalidQuality)));
    e.insert(error_map::value_type(0xfffffff2, error_details("Interval not recorded",   InvalidQuality)));
    e.insert(error_map::value_type(0xfffffff1, error_details("Pulse count overflow",    OverflowQuality)));
    e.insert(error_map::value_type(0xfffffff0, error_details("Overflow",                OverflowQuality)));

    return e;
}

Mct470Device::error_map Mct470Device::initErrorInfoGEkV( void )
{
    error_map e;

    e.insert(error_map::value_type(0xffffffff, error_details("LP data reset",           InvalidQuality)));

    //  not available for LP
    e.insert(error_map::value_type(0xfffffffd, error_details("LP error",                InvalidQuality)));

    //  not available for LLP
    e.insert(error_map::value_type(0xfffffffb, error_details("Time adjusted",           DeviceFillerQuality)));

    e.insert(error_map::value_type(0xfffffffa, error_details("LP data not available",   InvalidQuality)));
    e.insert(error_map::value_type(0xfffffff0, error_details("Overflow",                OverflowQuality)));

    return e;
}

Mct470Device::error_map Mct470Device::initErrorInfoSentinel( void )
{
    error_map e;

    e.insert(error_map::value_type(0xffffffff, error_details("LP data reset",           InvalidQuality)));
    e.insert(error_map::value_type(0xfffffffe, error_details("LP config error",         InvalidQuality)));
    e.insert(error_map::value_type(0xfffffffd, error_details("LP error",                InvalidQuality)));

    //  not available for LLP
    e.insert(error_map::value_type(0xfffffffb, error_details("Time adjusted",           DeviceFillerQuality)));

    e.insert(error_map::value_type(0xfffffffa, error_details("LP data not available",   InvalidQuality)));
    e.insert(error_map::value_type(0xfffffff0, error_details("Overflow",                OverflowQuality)));

    return e;
}


bool Mct470Device::getOperation( const UINT &cmd, BSTRUCT &bst ) const
{
    if( getOperationFromStore(_commandStore, cmd, bst) )
    {
        return true;
    }

    return Inherited::getOperation(cmd, bst);
}


//  this function checks to see if we have all of the parameters we need to correctly request and interpret
//    load profile date from the MCT
bool Mct470Device::isLPDynamicInfoCurrent( void )
{
    bool retval = true;
    long sspec = 0,
         sspec_rev = 0;

    //  grab these two because we'll use them later...
    //    also, the return value is identical to hasDynamicInfo, so we'll just use it the same
    retval &= getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec,         sspec);
    retval &= getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, sspec_rev);

    retval &= hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration);

    //  only MCT-470s have a load profile interval in their device config
    if( retval && isMct470(getType()) )
    {
        //  This compares against either the device config, if available, or the database table
        retval &= ((getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval) * 60) == getLoadProfile()->getLoadProfileDemandRate());
    }

    if( retval && ((sspec == Sspec_MCT470_128k && sspec_rev >= SspecRev_Min && sspec_rev <= SspecRev_Max)
                   || sspec == Sspec_MCT430A
                   || sspec == Sspec_MCT430S
                   || sspec == Sspec_MCT470_256k) )
    {
        //  we only care about these if we're the correct rev...  otherwise, we ignore everything
        //    we would've done with it.  everything pre-rev E is development only, and needs to be treated with kid gloves

        //  we will need to verify this eventually, and if it doesn't match the 470 config, we'll reconfig the 470 (and complain)
        retval &= hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig);
        retval &= hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval);
    }

    return retval;
}


void Mct470Device::requestDynamicInfo(CtiTableDynamicPaoInfo::PaoInfoKeys key, OUTMESS *&OutMessage, OutMessageList &outList )
{
    bool valid = true;

    OUTMESS *newOutMessage = CTIDBG_new OUTMESS(*OutMessage);

    populateDlcOutMessage(*newOutMessage);
    newOutMessage->Priority  = ScanPriority_LoadProfile;

    // Tell the porter side to complete the assembly of the message.
    newOutMessage->Request.BuildIt = TRUE;

    CTILOG_TRACE(dout, "device \""<< getName() <<"\" requesting key (" << key << ")");

    if( key == CtiTableDynamicPaoInfo::Key_MCT_SSpec || key == CtiTableDynamicPaoInfo::Key_MCT_Configuration )
    {
        strncpy(newOutMessage->Request.CommandStr, "getconfig model", COMMAND_STR_SIZE );
        newOutMessage->Sequence  = EmetconProtocol::GetConfig_Model;     // Helps us figure it out later!
    }
    else
    {
        //  the ideal case - the correct, non-development sspec
        if( (getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec)         == Sspec_MCT470_128k &&
             getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) >= SspecRev_Min &&
             getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) <= SspecRev_Max)
            || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec) == Sspec_MCT430A
            || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec) == Sspec_MCT430S
            || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec) == Sspec_MCT470_256k )
        {
            switch( key )
            {
                //  all of these three are retrieved by this read
                case CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval:
                case CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval:
                case CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig:
                {
                    strncpy(newOutMessage->Request.CommandStr, "getconfig channels", COMMAND_STR_SIZE );
                    newOutMessage->Sequence = EmetconProtocol::GetConfig_ChannelSetup;     // Helps us figure it out later!

                    break;
                }
                default:
                {
                    CTILOG_ERROR(dout, "unhandled key ("<< key <<")");

                    delete newOutMessage;
                    newOutMessage = 0;
                }
            }
        }
        else
        {
            switch( key )
            {
                case CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval:
                {
                    strncpy(newOutMessage->Request.CommandStr, "getconfig intervals", COMMAND_STR_SIZE );
                    newOutMessage->Sequence = EmetconProtocol::GetConfig_Intervals;     // Helps us figure it out later!

                    break;
                }
                default:
                {
                    CTILOG_ERROR(dout, "unhandled key ("<< key <<")");
                }
                //  we don't care about these keys, since this sspec should only be used for pulse inputs...
                //    they only matter for IED reads
                case CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig:
                case CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval:
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


ULONG Mct470Device::calcNextLPScanTime( void )
{
    const CtiTime Now;
    unsigned long next_time = YUKONEOT;

    if( !isLPDynamicInfoCurrent() )
    {
        //  i'm purposely using the DB rate instead of the fancy getLoadProfileInterval() call here, so it can be ignorant
        //    of the dynamic LP config info - this could probably be improved to look at the minimum of the available
        //    LP intervals (interval 1, interval 2, electronic)
        unsigned int overdue_rate;

        if( isMct430(getType()) )
        {
            int lp_rate = Default_IedLoadProfileRate;  //  default to 15 minutes

            //  try to get the device's value if we have it
            getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval, lp_rate);

            overdue_rate = getLPRetryRate(lp_rate);
        }
        else
        {
            overdue_rate = getLPRetryRate(getLoadProfile()->getLoadProfileDemandRate());
        }

        //  only try for the dynamic info at the retry rate, no faster
        next_time  = (Now.seconds() - LPBlockEvacuationTime) + overdue_rate;
        next_time -= (Now.seconds() - LPBlockEvacuationTime) % overdue_rate;

        //  only for consistency...  we don't /really/ have to add on the evacuation time for config reads, but it keeps
        //    outbound requests predictable, which is helpful
        next_time += LPBlockEvacuationTime;
    }
    else
    {
        for( int channel = 0; channel < LPChannels; channel++ )
        {
            const int interval_len = getLoadProfileInterval(channel);
            const int block_len = interval_len * 6;

            if( interval_len <= 0 )
            {
                CTILOG_ERROR(dout, "device \""<< getName() <<"\" has invalid LP rate ("<< interval_len <<") for channel ("<< channel <<") - setting nextLPtime out 30 minutes");

                next_time = Now.seconds() + (30 * 60);
            }
            else
            {
                CtiPointSPtr pPoint = getDevicePointOffsetTypeEqual(1 + channel + PointOffset_LoadProfileOffset, DemandAccumulatorPointType);

                //  if we're not collecting load profile, or there's no point defined, don't scan
                if( !getLoadProfile()->isChannelValid(channel) || !pPoint )
                {
                    _lp_info[channel].collection_point = YUKONEOT;

                    continue;
                }

                //  uninitialized - get the readings from the DB
                if( !_lp_info[channel].collection_point )
                {
                    //  so we haven't talked to it yet
                    _lp_info[channel].collection_point = 86400;

                    CtiTablePointDispatch pd(pPoint->getPointID());

                    if(pd.Restore())
                    {
                        _lp_info[channel].collection_point = pd.getTimeStamp().seconds();
                    }

                    if( _lp_info[channel].collection_point < (CtiTime::now().seconds() - block_len * 16) )
                    {
                        //  start collecting the most recent block
                        _lp_info[channel].collection_point  = CtiTime::now().seconds();
                        _lp_info[channel].collection_point -= _lp_info[channel].collection_point % block_len;
                    }
                }

                //  basically, we plan to request again after a whole block has been recorded...
                //    then we add on a little bit to make sure the MCT is out of the memory
                unsigned long planned_time;
                planned_time  = _lp_info[channel].collection_point + block_len;
                planned_time -= planned_time % block_len;  //  make double sure we're block-aligned
                planned_time += LPBlockEvacuationTime;      //  add on the safeguard time

                _lp_info[channel].current_schedule = planned_time;

                //  if we're overdue, request at the overdue_rate
                if( _lp_info[channel].collection_point <= (Now.seconds() - block_len - LPBlockEvacuationTime) )
                {
                    unsigned overdue_rate = getLPRetryRate(interval_len);

                    _lp_info[channel].current_schedule  = (Now.seconds() - LPBlockEvacuationTime) + overdue_rate;
                    _lp_info[channel].current_schedule -= (Now.seconds() - LPBlockEvacuationTime) % overdue_rate;

                    _lp_info[channel].current_schedule += LPBlockEvacuationTime;
                }

                if( next_time > _lp_info[channel].current_schedule )
                {
                    next_time = _lp_info[channel].current_schedule;
                }
            }
        }
    }

    if( getMCTDebugLevel(DebugLevel_LoadProfile) )
    {
        CTILOG_DEBUG(dout, getName() <<"'s next Load Profile request at "<< CtiTime(next_time));
    }

    return (_nextLPScanTime = next_time);
}


void Mct470Device::sendIntervals( OUTMESS *&OutMessage, OutMessageList &outList )
{
    populateDlcOutMessage(*OutMessage);
    OutMessage->Priority  = ScanPriority_LoadProfile;
    OutMessage->Sequence  = EmetconProtocol::GetStatus_LoadProfile;     // Helps us figure it out later!

    // Tell the porter side to complete the assembly of the message.
    OutMessage->Request.BuildIt = TRUE;
    strncpy(OutMessage->Request.CommandStr, "putconfig emetcon intervals", COMMAND_STR_SIZE );

    outList.push_back(OutMessage);
    OutMessage = NULL;
}

void Mct470Device::sendBackground(const OUTMESS &TemplateOutMessage, OutMessageList &outList) const
{
    std::unique_ptr<OUTMESS> OutMessage(new OUTMESS(TemplateOutMessage));

    //  Set the connection to NULL so it doesn't make it back to the requesting client,
    //    but leave the GroupMsgID alone so it can still be canceled by MACS or the web
    OutMessage->Request.Connection.reset();

    outList.push_back(OutMessage.release());
}

boost::shared_ptr<DataAccessLoadProfile> Mct470Device::getLoadProfile()
{
    boost::shared_ptr<DataAccessLoadProfile> lp = Inherited::getLoadProfile();
    Cti::Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if (deviceConfig)
    {
        return boost::make_shared<DeviceConfigurationLoadProfileData>(deviceConfig, lp);
    }

    return lp;
}

//  zero-based channel offset, returns interval length in seconds
long Mct470Device::getLoadProfileInterval( unsigned channel )
{
    long retval = -1;

    if( channel < LPChannels )
    {
        if( isIedChannel(channel) )
        {
            //  leaves it untouched (i.e., -1) if it doesn't have it
            getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval, retval);
        }
        else
        {
            if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval, retval) )
            {
                //  Value is stored in minutes, return it as seconds
                retval *= 60;
            }
        }
    }
    else
    {
        CTILOG_ERROR(dout, "device \""<< getName() <<"\" - channel "<< channel <<" not in range");
    }

    if( retval < 0 )
    {
        CTILOG_WARN(dout, "device \""<< getName() <<"\" - load profile interval requested, but value not retrieved...  sending DB value");

        if( isMct470(getType()) )
        {
            retval = getLoadProfile()->getLoadProfileDemandRate();
        }
        else
        {
            retval = Default_IedLoadProfileRate;  //  default to 15 minutes
        }
    }
    else if( retval == 0 )
    {
        CTILOG_ERROR(dout, "device \""<< getName() <<"\" - channel "<< channel <<" LP interval returned zero");
    }

    return retval;
}


bool Mct470Device::isIedChannel(unsigned channel) const
{
    string config;

    if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, config) )
    {
        if( config.length() > channel * 3 )
        {
            return config[channel*3] == '1';
        }
        else
        {
            CTILOG_ERROR(dout, "device \""<< getName() <<"\" - config.length() < channel * 3 (" << config.length() << " < " << channel * 3 << ")");
        }
    }
    else
    {
        CTILOG_ERROR(dout, "device \"" << getName() << "\" - load profile config not retrieved - channel = " << channel);
    }

    return false;
}


bool Mct470Device::needsChannelConfig(const unsigned channel) const
{
    if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig) )
    {
        if( isIedChannel(channel) )
        {
            return ! hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval);
        }
        else
        {
            return false;
        }
    }

    return true;
}


bool Mct470Device::requestChannelConfig(const unsigned channel, const CtiRequestMsg &originalRequest, OutMessageList &outList, CtiMessageList &retList)
{
    return executeAuxiliaryRequest("getconfig channels", originalRequest, outList, retList);
}


unsigned Mct470Device::getUsageReportDelay(const unsigned interval_length, const unsigned days) const
{
    const int fixed_delay    = gConfigParms.getValueAsInt("PORTER_MCT_PEAK_REPORT_DELAY", 10);
    const int variable_delay = intervalsPerDay(interval_length) * days / 1000;  //  1 ms per interval

    return fixed_delay + variable_delay;
}


Mct470Device::frozen_point_info Mct470Device::getDemandData(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const
{
    return getData(buf, len, ValueType_PulseDemand);
}


Mct470Device::frozen_point_info Mct470Device::getAccumulatorData( const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter ) const
{
    return decodePulseAccumulator(buf, len, freeze_counter);
}


Mct470Device::frozen_point_info Mct470Device::decodePulseAccumulator( const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter )
{
    frozen_point_info pi = Mct4xxDevice::decodePulseAccumulator( buf, len, freeze_counter );

    const long value = static_cast<long>(pi.value);

    pi.freeze_bit  = value &  0x01;
    pi.value       = value & ~0x01;

    return pi;
}


Mct470Device::frozen_point_info Mct470Device::getData( const unsigned char *buf, const unsigned len, const ValueType470 vt ) const
{
    PointQuality_t quality = NormalQuality;
    unsigned long error_code = 0xffffffff,  //  filled with 0xff because some data types are less than 32 bits
                  min_error  = 0xffffffff;
    unsigned char quality_flags = 0,
                  resolution    = 0;
    unsigned char error_pad, error_byte, value_byte;

    //  This are the default errors for non-load-profile points.
    //    Load profile error maps are assigned lower.
    const error_map *errors = &error_codes;

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
        case ValueType_LoadProfile_PulseDemand:
        {
            min_error = 0xffffffa1;
            break;
        }

        case ValueType_IED:
        {
            min_error = 0xffffffe0;
            break;
        }

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
                min_error = 0xfffffff0;

                switch( vt )
                {
                    case ValueType_LoadProfile_IED_Alpha_A3:
                    {
                        errors = &_error_info_alphaa3;
                        break;
                    }
                    case ValueType_LoadProfile_IED_Alpha_PP:
                    {
                        errors = &_error_info_alphapp;
                        break;
                    }
                    case ValueType_LoadProfile_IED_GE_kV:
                    case ValueType_LoadProfile_IED_GE_kV2:
                    case ValueType_LoadProfile_IED_GE_kV2c:
                    {
                        errors = &_error_info_gekv;
                        break;
                    }
                    case ValueType_LoadProfile_IED_LG_S4:
                    {
                        errors = &_error_info_lgs4;
                        break;
                    }
                    case ValueType_LoadProfile_IED_Sentinel:
                    {
                        errors = &_error_info_sentinel;
                        break;
                    }
                }
            }
            else
            {
                min_error = 0xffff7ff0;

                errors = &_error_info_old_lp;
            }

            break;
        }
    }

    if( error_code >= min_error )
    {
        return getDataError(error_code, *errors);
    }
    else if( vt == ValueType_IED && error_pad >= 0xfc )
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


void Mct470Device::decodeReadDataForKey(const CtiTableDynamicPaoInfo::PaoInfoKeys key, const unsigned char *begin, const unsigned char *end)
{
    if( end <= begin )
    {
        CTILOG_ERROR(dout, "end <= begin for device \""<< getName() <<"\" (begin - end = "<< static_cast<int>(begin - end));

        return;
    }

    switch( key )
    {
        case CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval:
        {
            //  Comes back as minutes, stored as seconds
            setDynamicInfo(key, begin[0] * 60);

            return;
        }
        case CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig:
        {
            if( (end - begin) != Mct470Device::ChannelCount )
            {
                //  default processing
                break;
            }

            std::ostringstream channel_info;

            for( const unsigned char *itr = begin; itr < end; ++itr )
            {
                /*
                Bits 0-1 - Type:    00 = Channel Not Used
                                    01 = Electronic Meter
                                    10 = 2-wire KYZ (form A)
                                    11 = 3-wire KYZ (form C)
                Bits 2-5 - Physical Channel / Attached Meter's Channel
                Bit 6 - Load Profile Interval #0 or #1 (0, 1)
                */

                //  type
                channel_info << (int)(*itr & 0x03);

                if( *itr & 0x03 )
                {
                    //  input
                    channel_info << std::hex << (int)((*itr >> 2) & 0x0f);
                    //  load profile interval
                    channel_info << std::dec << (int)!!(*itr & 0x40);
                }
                else
                {
                    channel_info << "00";
                }
            }

            if( getMCTDebugLevel(DebugLevel_DynamicInfo) )
            {
                CTILOG_DEBUG(dout, "device \""<< getName() <<"\" LP config decode - \""<< channel_info);
            }

            setDynamicInfo(key, channel_info.str());

            return;
        }
        case CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1:
        case CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2:
        case CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3:
        case CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4:
        {
            string existingLoadProfileConfig;

            if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, existingLoadProfileConfig) )
            {
                string singleChannelLpConfig = CtiNumStr(*begin & 0x03);

                if( *begin & 0x03 )
                {
                    //  input
                    singleChannelLpConfig  += CtiNumStr((*begin >> 2) & 0x0f).hex();
                    //  load profile interval
                    singleChannelLpConfig  += (*begin & 0x40)?"1":"0";
                }
                else
                {
                    singleChannelLpConfig  += "00";
                }

                unsigned channel = 0;

                switch( key )
                {
                   case CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1:  channel = 1;  break;
                   case CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2:  channel = 2;  break;
                   case CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3:  channel = 3;  break;
                   case CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4:  channel = 4;  break;
                }

                if( existingLoadProfileConfig.size() == 12 )
                {
                    existingLoadProfileConfig.replace((channel - 1) * 3, 3, singleChannelLpConfig);
                }

                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, existingLoadProfileConfig);
            }

            break;  //  Store as normal as well
        }
        case CtiTableDynamicPaoInfo::Key_MCT_TouEnabled:
        {
            setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TouEnabled, *begin != 0xff);
            //  0xFF is No TOU Active -
            //  see SSPEC -S01030, S01037, S01046, MCT-470, MCT-430.doc
            //  section 8.30.   TOU Day Schedule (Function Read 0xAD)

            return;  //  All done with the special processing
        }
    }

    Inherited::decodeReadDataForKey(key, begin, end);
}


bool Mct470Device::hasIedInputs() const
{
    if( isMct430(getType()) )
    {
        return true;
    }

    string lp_config;

    if( !getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, lp_config) )
    {
        return true;  //  if we don't know for sure, be safe and assume it has them
    }

    if( lp_config.length() < LPChannels * 3 )
    {
        return true;
    }

    for( unsigned i = 0; i < lp_config.length(); i += 3 )
    {
        if( lp_config[i] == '1' )
        {
            return true;
        }
    }

    return false;
}


bool Mct470Device::hasPulseInputs() const
{
    if( isMct430(getType()) )
    {
        return false;
    }

    string lp_config;

    if( !getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, lp_config) )
    {
        return true;  //  if we don't know for sure, be safe and assume it has them
    }

    if( lp_config.length() < LPChannels * 3 )
    {
        return true;
    }

    for( unsigned i = 0; i < lp_config.length(); i += 3 )
    {
        if( lp_config[i] > '1' )
        {
            return true;
        }
    }

    return false;
}


Mct470Device::point_info Mct470Device::getLoadProfileData(unsigned channel, long interval_len, const unsigned char *buf, unsigned len)
{
    point_info pi;
    string config;
    ValueType470 vt = ValueType_LoadProfile_PulseDemand;

    if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, config) )
    {
        if( config.length() > channel * 3 )
        {
            if( config[channel*3] == '1' )
            {
                if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) )
                {
                    switch( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) >> 4 )
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
                            CTILOG_ERROR(dout, "device \""<< getName() <<"\" is reporting an invalid IED type (" << (getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) >> 4) << "); aborting decode");
                        }
                    }
                }
                else
                {
                    CTILOG_WARN(dout, "device \""<< getName() <<"\" does not have Key_MCT_Configuration; attempting decode");
                }
            }
            else
            {
                vt = ValueType_LoadProfile_PulseDemand;
            }
        }
        else
        {
            CTILOG_ERROR(dout, "device \""<< getName() <<"\" - config.length() < channel * 3 ("<< config.length() << " < " << channel * 3 <<")");
        }
    }
    else
    {
        CTILOG_ERROR(dout, "device \""<< getName() <<"\" - dynamic LP config not stored");
    }

    pi = getData(buf, len, vt);

    if( interval_len <= 0 )
    {
        CTILOG_ERROR(dout, "interval_len = \""<< interval_len <<"\" for device \""<< getName() <<"\"");

        pi.quality = InvalidQuality;
        pi.description = "Invalid demand interval, cannot adjust reading";
    }
    else
    {
        //  adjust for the demand interval
        pi.value *= 3600 / interval_len;
    }

    return pi;
}


void Mct470Device::calcAndInsertLPRequests(OUTMESS *&OutMessage, OutMessageList &outList)
{
    CtiTime Now;

    if( !isLPDynamicInfoCurrent() )
    {
        if( _lastConfigRequest >= (Now.seconds()) )
        {
            CTILOG_WARN(dout, "config request too soon for device \""<< getName() <<"\"");
        }
        else
        {
            _lastConfigRequest = Now.seconds();

            if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec) )
            {
                requestDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, OutMessage, outList);
            }
            else if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) )
            {
                requestDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, OutMessage, outList);
            }
            else
            {
                if( isMct470(getType()) )
                {
                    if( (getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval) * 60) != getLoadProfile()->getLoadProfileDemandRate() )
                    {
                        if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval) )
                        {
                            OUTMESS *om = CTIDBG_new CtiOutMessage(*OutMessage);

                            //  send the intervals....
                            sendIntervals(om, outList);
                        }
                        //  then verify them - the ordering here does matter
                        requestDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval, OutMessage, outList);
                    }
                }

                const long sspec    = getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec);
                const long sspecRev = getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision);

                //  check if we're the IED sspec
                if( isMct430(getType())
                    || (sspec == Sspec_MCT470_256k)
                    || (sspec == Sspec_MCT470_128k
                        && sspecRev >= SspecRev_Min
                        && sspecRev <= SspecRev_Max) )
                {
                    if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval) )
                    {
                        //  as i understand it, we can only read this, not write it, so we'll never do a write-then-read confirmation
                        requestDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval, OutMessage, outList);
                    }

                    if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig) )
                    {
                        //  this will need to be changed to check for a match like LoadProfileInterval above -
                        //    if it doesn't match, then re-send and re-read
                        //    (which will happen when the 470 config is added)
                        requestDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig, OutMessage, outList);
                    }
                }
            }

            if( outList.empty() )
            {
                CTILOG_ERROR(dout, "!isLPDynamicInfoCurrent(), but no dynamic info requested");
            }
        }
    }
    else if( useScanFlags() )
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
                        CTILOG_DEBUG(dout, "LP variable check for device \""<< getName() <<
                                endl <<"Now.seconds()                                = "<< Now.seconds() <<
                                endl <<"_lp_info[" << channel << "].collection_point = "<< _lp_info[channel].collection_point <<
                                endl <<"MCT4XX_LPRecentBlocks * block_size           = "<< LPRecentBlocks * block_len
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


bool Mct470Device::calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage )
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
        CTILOG_WARN(dout, "Improperly formed LP request discarded for \""<< getName() <<"\"");

        retVal = false;
    }

    return retVal;
}


const Mct470Device::ValueMapping *Mct470Device::getMemoryMap(void) const
{
    return &_memoryMap;
}


const Mct470Device::FunctionReadValueMappings *Mct470Device::getFunctionReadValueMaps(void) const
{
    return &_functionReadValueMaps;
}


Mct470Device::DecodeMapping Mct470Device::initDecodeLookup()
{
    namespace EP = EmetconProtocol;

    return {
        { EP::GetConfig_ChannelSetup,     &Self::decodeGetConfigChannelSetup },
        { EP::GetConfig_IEDDNP,           &Self::decodeGetConfigIED },
        { EP::GetConfig_IEDDNPAddress,    &Self::decodeGetConfigIedDnpAddress },
        { EP::GetConfig_IEDScan,          &Self::decodeGetConfigIED },
        { EP::GetConfig_IEDTime,          &Self::decodeGetConfigIED },
        { EP::GetConfig_Intervals,        &Self::decodeGetConfigIntervals },
        { EP::GetConfig_Model,            &Self::decodeGetConfigModel },
        { EP::GetConfig_Multiplier,       &Self::decodeGetConfigMultiplier },
        // ---
        { EP::GetStatus_IEDDNP,           &Self::decodeGetStatusDNP },
        { EP::GetStatus_Internal,         &Self::decodeGetStatusInternal },
        { EP::GetStatus_LoadProfile,      &Self::decodeGetStatusLoadProfile },
        // ---
        { EP::GetValue_Demand,            &Self::decodeGetValueDemand },
        { EP::GetValue_FrozenKWH,         &Self::decodeGetValueKWH },
        { EP::GetValue_FrozenPeakDemand,  &Self::decodeGetValueMinMaxDemand },
        { EP::GetValue_IED,               &Self::decodeGetValueIED },
        { EP::GetValue_IEDDemand,         &Self::decodeGetValueIED },
        { EP::GetValue_KWH,               &Self::decodeGetValueKWH },
        { EP::GetValue_LoadProfile,       &Self::decodeGetValueLoadProfile },
        { EP::GetValue_PeakDemand,        &Self::decodeGetValueMinMaxDemand },
        { EP::GetValue_PhaseCurrent,      &Self::decodeGetValuePhaseCurrent },
        // ---
        { EP::PutConfig_PrecannedTable,   &Self::decodePutConfig },
        // ---
        { EP::Scan_Accum,                 &Self::decodeGetValueKWH },
        { EP::Scan_Integrity,             &Self::decodeGetValueDemand },
        { EP::Scan_LoadProfile,           &Self::decodeScanLoadProfile }};
}


const Mct470Device::DecodeMapping Mct470Device::_decodeMethods = Mct470Device::initDecodeLookup();


YukonError_t Mct470Device::ModelDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    DecodeMapping::const_iterator itr = _decodeMethods.find(InMessage.Sequence);

    if( itr != _decodeMethods.end() )
    {
        const DecodeMethod decoder = itr->second;

        return (this->*decoder)(InMessage, TimeNow, vgList, retList, outList);
    }

    return Inherited::ModelDecode(InMessage, TimeNow, vgList, retList, outList);
}

YukonError_t Mct470Device::ErrorDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList)
{
    YukonError_t retVal = ClientErrors::None;
    CtiCommandParser  parse(InMessage.Return.CommandStr);
    CtiReturnMsg     *retMsg = new CtiReturnMsg(getID(),
                                                InMessage.Return.CommandStr,
                                                "",
                                                InMessage.ErrorCode,
                                                InMessage.Return.RouteID,
                                                InMessage.Return.RetryMacroOffset,
                                                InMessage.Return.Attempt,
                                                InMessage.Return.GrpMsgID,
                                                InMessage.Return.UserID);

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
                insertPointFail( InMessage, retMsg, ScanRateGeneral, Mct470Device::PointOffset_TotalKW,     AnalogPointType);
                insertPointFail( InMessage, retMsg, ScanRateGeneral, Mct470Device::PointOffset_TotalKM,     AnalogPointType);
                insertPointFail( InMessage, retMsg, ScanRateGeneral, Mct470Device::PointOffset_VoltsPhaseA, AnalogPointType);
                insertPointFail( InMessage, retMsg, ScanRateGeneral, Mct470Device::PointOffset_VoltsPhaseB, AnalogPointType);
                insertPointFail( InMessage, retMsg, ScanRateGeneral, Mct470Device::PointOffset_VoltsPhaseC, AnalogPointType);

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
                retVal = Inherited::ErrorDecode(InMessage, TimeNow, retList);

                break;
            }
        }
    }
    else if( InMessage.Sequence == EmetconProtocol::Scan_Integrity || InMessage.Sequence == EmetconProtocol::GetValue_Demand )
    {
        insertPointFail( InMessage, retMsg, ScanRateIntegrity, PointOffset_Accumulator_Powerfail, PulseAccumulatorPointType);
        insertPointFail( InMessage, retMsg, ScanRateIntegrity, 1, DemandAccumulatorPointType);
        insertPointFail( InMessage, retMsg, ScanRateIntegrity, 2, DemandAccumulatorPointType);
        insertPointFail( InMessage, retMsg, ScanRateIntegrity, 3, DemandAccumulatorPointType);
        insertPointFail( InMessage, retMsg, ScanRateIntegrity, 4, DemandAccumulatorPointType);
    }
    else if( InMessage.Sequence == EmetconProtocol::GetValue_IEDDemand )
    {
        insertPointFail( InMessage, retMsg, ScanRateGeneral, Mct470Device::PointOffset_TotalKW,     AnalogPointType);
        insertPointFail( InMessage, retMsg, ScanRateGeneral, Mct470Device::PointOffset_TotalKM,     AnalogPointType);
        insertPointFail( InMessage, retMsg, ScanRateGeneral, Mct470Device::PointOffset_VoltsPhaseA, AnalogPointType);
        insertPointFail( InMessage, retMsg, ScanRateGeneral, Mct470Device::PointOffset_VoltsPhaseB, AnalogPointType);
        insertPointFail( InMessage, retMsg, ScanRateGeneral, Mct470Device::PointOffset_VoltsPhaseC, AnalogPointType);
    }
    else
    {
        retVal = Inherited::ErrorDecode(InMessage, TimeNow, retList);
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

    return retVal;
}


bool Mct470Device::isPrecannedTableCurrent() const
{
    return hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_PrecannedTableType);
}


YukonError_t Mct470Device::executeGetValue(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t nRet = ClientErrors::NoMethod;

    bool found = false;
    int function;

    if( parse.getFlags() & CMD_FLAG_GV_IED )  //  This parse has the token "IED" in it!
    {
        if( parse.isKeyValid("outage") )
        {
            if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) )
            {
                //  we need to read the IED info byte out of the MCT
                function = EmetconProtocol::GetConfig_Model;
                found = getOperation(function, OutMessage->Buffer.BSt);

                if( found )
                {
                    populateDlcOutMessage(*OutMessage);
                    OutMessage->Sequence  = function;         // Helps us figure it out later!
                    OutMessage->Request.RouteID   = getRouteID();

                    strncpy(OutMessage->Request.CommandStr, "getconfig model", COMMAND_STR_SIZE );
                    sendBackground(*OutMessage, outList);
                }
            }

            function = EmetconProtocol::GetConfig_IEDTime;
            found = getOperation(function, OutMessage->Buffer.BSt);
        }
        else
        {
            if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) < SspecRev_IED_ZeroWriteMin )
            {
                //If we need to read out the time, do so.
                function = EmetconProtocol::GetConfig_IEDTime;
                found = getOperation(function, OutMessage->Buffer.BSt);
                if( found )
                {
                    populateDlcOutMessage(*OutMessage);
                    OutMessage->Sequence  = function;         // Helps us figure it out later!
                    OutMessage->Request.RouteID   = getRouteID();

                    strncpy(OutMessage->Request.CommandStr, "getconfig ied time", COMMAND_STR_SIZE );
                    sendBackground(*OutMessage, outList);
                }
            }
            if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) ||
                !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) )
            {
                //  we need to read the IED info byte out of the MCT
                function = EmetconProtocol::GetConfig_Model;
                found = getOperation(function, OutMessage->Buffer.BSt);

                if( found )
                {
                    populateDlcOutMessage(*OutMessage);
                    OutMessage->Sequence  = function;         // Helps us figure it out later!
                    OutMessage->Request.RouteID   = getRouteID();

                    strncpy(OutMessage->Request.CommandStr, "getconfig model", COMMAND_STR_SIZE );
                    sendBackground(*OutMessage, outList);
                }
            }
            if( !isPrecannedTableCurrent() )
            {
                //  we need to read the IED precanned table type out of the MCT
                function = EmetconProtocol::GetConfig_Intervals;
                found = getOperation(function, OutMessage->Buffer.BSt);

                if( found )
                {
                    populateDlcOutMessage(*OutMessage);
                    OutMessage->Sequence  = function;         // Helps us figure it out later!
                    OutMessage->Request.RouteID   = getRouteID();

                    strncpy(OutMessage->Request.CommandStr, "getconfig intervals", COMMAND_STR_SIZE );
                    sendBackground(*OutMessage, outList);
                }
            }

            if( parse.isKeyValid("ied_dnp") )
            {
                int i = 0;
                if( (i = parse.getiValue("collectionnumber")) != INT_MIN )
                {
                    if( i == 1 )
                    {
                        function = EmetconProtocol::GetValue_IED;
                        found = getOperation(function, OutMessage->Buffer.BSt);
                        OutMessage->Buffer.BSt.Function = FuncRead_IED_RealTime;
                    }
                    else if( i == 2 )
                    {
                        function = EmetconProtocol::GetValue_IED;
                        found = getOperation(function, OutMessage->Buffer.BSt);
                        OutMessage->Buffer.BSt.Function = FuncRead_IED_RealTime2;
                    }
                    else
                    {
                        nRet = ClientErrors::BadRange;
                        found = false;
                    }
                }
                else if( (i = parse.getiValue("analognumber")) != INT_MIN )
                {
                    function = EmetconProtocol::GetValue_IED;   //This means we have to fill in the function
                    found = getOperation(function, OutMessage->Buffer.BSt);

                    OutMessage->Buffer.BSt.Function = FuncRead_IED_Precanned_Base + (i-1) + MCT470_DNP_Analog_Precanned_Offset;
                    if( i > (MCT470_DNP_Counter_Precanned_Offset - MCT470_DNP_Analog_Precanned_Offset) || i < 1 )
                    {
                        nRet = ClientErrors::BadRange;
                        found = false;
                    }
                }
                else if( (i = parse.getiValue("accumulatornumber")) != INT_MIN )
                {
                    function = EmetconProtocol::GetValue_IED;   //This means we have to fill in the function
                    found = getOperation(function, OutMessage->Buffer.BSt);

                    OutMessage->Buffer.BSt.Function = FuncRead_IED_Precanned_Base + (i-1) + MCT470_DNP_Counter_Precanned_Offset;
                    if( i > MCT470_DNP_Counter_Precanned_Reads || i < 1 ) //only 8 reads possible.
                    {
                        nRet = ClientErrors::BadRange;
                        found = false;
                    }
                }
                else if( (i = parse.getiValue("statusnumber")) != INT_MIN )
                {
                    function = EmetconProtocol::GetValue_IED;   //This means we have to fill in the function
                    found = getOperation(function, OutMessage->Buffer.BSt);

                    OutMessage->Buffer.BSt.Function = FuncRead_IED_Precanned_Base + MCT470_DNP_Status_Precanned_Offset;
                }
                else if( parse.isKeyValid("dnp_crc") )
                {
                    function = EmetconProtocol::GetValue_IED;
                    OutMessage->Buffer.BSt.Function = FuncRead_IED_CRCPos;
                    OutMessage->Buffer.BSt.Length   = FuncRead_IED_CRCLen;
                    OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Function_Read;
                    found = true;
                }
            }
            else if( parse.getFlags() & CMD_FLAG_GV_PEAK )
            {
                //  these are the new Precanned Table 11 reads

                function = EmetconProtocol::GetValue_IED;
                found = getOperation(function, OutMessage->Buffer.BSt);

                if( parse.getCommandStr().find(" kva") != string::npos )
                {
                    OutMessage->Buffer.BSt.Function = FuncRead_IED_Peak_kM;
                }
                else
                {
                    OutMessage->Buffer.BSt.Function = FuncRead_IED_Peak_kW;
                }

                if( parse.getFlags() & CMD_FLAG_FROZEN )
                {
                    OutMessage->Buffer.BSt.Function += FuncRead_IED_TOU_PreviousOffset;
                }
            }
            else if( parse.getFlags() & CMD_FLAG_GV_DEMAND )
            {
                //  this will be for the real-time table
                function = EmetconProtocol::GetValue_IEDDemand;
                found = getOperation(function, OutMessage->Buffer.BSt);
            }
            else
            {
                function = EmetconProtocol::GetValue_IED;

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

                    //  note that rate D is not valid for precanned table 11, but we will detect that on decode instead of execute
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
        function = EmetconProtocol::GetValue_TOUPeak;
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
    else if( parse.isKeyValid("lp_command") )
    {
        nRet = Inherited::executeGetValue(pReq, parse, OutMessage, vgList, retList, outList);
    }
    else if( parse.isKeyValid("phasecurrentread") )
    {
        switch( (getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) & 0xf0) >> 4 )
        {
            case IED_Type_LG_S4:
            case IED_Type_Alpha_A3:
            case IED_Type_GE_kV:
            case IED_Type_GE_kV2:
            case IED_Type_Sentinel:
            case IED_Type_DNP:
            case IED_Type_GE_kV2c:
            {
                function = EmetconProtocol::GetValue_PhaseCurrent;
                found = getOperation(function, OutMessage->Buffer.BSt);

                OutMessage->Sequence = EmetconProtocol::GetValue_PhaseCurrent;
                break;
            }
            case IED_Type_Alpha_PP:
            default:
            {
                found = false;
                break;
            }
        }
    }
    else if( parse.getFlags() & CMD_FLAG_GV_PEAK )
    {
        function = EmetconProtocol::GetValue_PeakDemand;
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
        populateDlcOutMessage(*OutMessage);
        OutMessage->Sequence  = function;         // Helps us figure it out later!

        OutMessage->Request.RouteID   = getRouteID();
        ::strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
        incrementGroupMessageCount(pReq->UserMessageId(), pReq->getConnectionHandle());

        nRet = ClientErrors::None;
    }

    return nRet;
}

YukonError_t Mct470Device::executeScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    bool found = false;
    YukonError_t nRet  = ClientErrors::None;

    switch(parse.getiValue("scantype"))
    {
        case ScanRateIntegrity:
        {
            populateDlcOutMessage(*OutMessage);
            OutMessage->Request.RouteID   = getRouteID();

            std::string originalString = pReq->CommandString();
            const std::string scanString ("scan integrity");

            if( getType() == TYPEMCT470 )
            {
                //  Since this is an MCT-470, read the channel config to optimize the next scan
                if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileConfig) )
                {
                    const unsigned function = EmetconProtocol::GetConfig_ChannelSetup;
                    if( getOperation(function, OutMessage->Buffer.BSt) )
                    {
                        OutMessage->Sequence  = function;     // Helps us figure it out later!
                        std::string createdString = originalString;
                        const std::string replacement = "getconfig channels";

                        CtiToLower(createdString);
                        replaceString(createdString, scanString, replacement);//This had better be here, or we have issues.
                        strncpy(OutMessage->Request.CommandStr, createdString.data(), COMMAND_STR_SIZE);

                        outList.push_back(CTIDBG_new OUTMESS(*OutMessage));
                        incrementGroupMessageCount(pReq->UserMessageId(), pReq->getConnectionHandle());
                    }
                }

                //  if the meter has pulse inputs OR doesn't have any IED inputs configured
                if( hasPulseInputs() || !hasIedInputs() )
                {
                    //Read the pulse demand
                    const unsigned function = EmetconProtocol::GetValue_Demand;
                    if( getOperation(function, OutMessage->Buffer.BSt) )
                    {
                        OutMessage->Sequence  = function;     // Helps us figure it out later!

                        std::string createdString = originalString;
                        const std::string replacement = "getvalue demand";

                        CtiToLower(createdString);
                        replaceString(createdString, scanString, replacement);//This had better be here, or we have issues.
                        strncpy(OutMessage->Request.CommandStr, createdString.data(), COMMAND_STR_SIZE);

                        outList.push_back(CTIDBG_new OUTMESS(*OutMessage));
                        incrementGroupMessageCount(pReq->UserMessageId(), pReq->getConnectionHandle());

                        found = true;
                    }
                    else
                    {
                        nRet = ClientErrors::NoMethod;
                    }
                }
            }

            if( hasIedInputs() )
            {
                if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) < SspecRev_IED_ZeroWriteMin )
                {
                    //If we need to read out the time, do so.
                    const unsigned function = EmetconProtocol::GetConfig_IEDTime;
                    if( getOperation(function, OutMessage->Buffer.BSt) )
                    {
                        OutMessage->Sequence  = function;     // Helps us figure it out later!
                        std::string createdString = originalString;
                        const std::string replacement = "getconfig ied time";

                        CtiToLower(createdString);
                        replaceString(createdString, scanString, replacement);//This had better be here, or we have issues.
                        strncpy(OutMessage->Request.CommandStr, createdString.data(), COMMAND_STR_SIZE);

                        sendBackground(*OutMessage, outList);
                    }
                    else
                    {
                        //  IED scans on meters with this version of firmware are invalid without current IED time.
                        //  We should let any pulse scan continue, but yell about the IED scan.
                        nRet = ClientErrors::NoMethod;
                    }
                }

                //Read the IED demand
                const unsigned function = EmetconProtocol::GetValue_IEDDemand;
                if( getOperation(function, OutMessage->Buffer.BSt) )
                {
                    OutMessage->Sequence  = function;     // Helps us figure it out later!
                    std::string createdString = originalString;
                    const std::string replacement = "getvalue ied demand";

                    CtiToLower(createdString);
                    replaceString(createdString, scanString, replacement);//This had better be here, or we have issues.
                    strncpy(OutMessage->Request.CommandStr, createdString.data(), COMMAND_STR_SIZE);

                    outList.push_back(CTIDBG_new OUTMESS(*OutMessage));
                    incrementGroupMessageCount(pReq->UserMessageId(), pReq->getConnectionHandle());

                    found = true;
                }
                else
                {
                    nRet = ClientErrors::NoMethod;
                }
            }

            //  If we had an error, we need to leave the outmessage around for the error message
            if( found && nRet == ClientErrors::None )
            {
                delete OutMessage;
                OutMessage = NULL;
            }

            break;
        }
        default:
        {
            return Inherited::executeScan(pReq, parse, OutMessage, vgList, retList, outList);
        }
    }

    if(!found)
    {
        nRet = ClientErrors::NoMethod;
    }

    return nRet;
}


YukonError_t Mct470Device::executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t nRet = ClientErrors::NoMethod;

    bool found = false;
    int function;

    if(parse.isKeyValid("multiplier"))
    {
        function = EmetconProtocol::GetConfig_Multiplier;
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
            if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) )
            {
                //  we need to read the IED info byte out of the MCT
                function = EmetconProtocol::GetConfig_Model;
                found = getOperation(function, OutMessage->Buffer.BSt);

                if( found )
                {
                    populateDlcOutMessage(*OutMessage);
                    OutMessage->Sequence  = function;         // Helps us figure it out later!
                    OutMessage->Request.RouteID   = getRouteID();

                    strncpy(OutMessage->Request.CommandStr, "getconfig model", COMMAND_STR_SIZE );
                    sendBackground(*OutMessage, outList);
                }
            }

            function = EmetconProtocol::GetConfig_IEDTime;
            found = getOperation(function, OutMessage->Buffer.BSt);
            incrementGroupMessageCount(pReq->UserMessageId(), pReq->getConnectionHandle());
        }
        else if( parse.isKeyValid("scan"))
        {
            function = EmetconProtocol::GetConfig_IEDScan;
            found = getOperation(function, OutMessage->Buffer.BSt);
        }
        else if( parse.isKeyValid("dnp") )
        {
            if( parse.isKeyValid("dnp address") )
            {
                function = EmetconProtocol::GetConfig_IEDDNPAddress;
                found = getOperation(function, OutMessage->Buffer.BSt);
            }
            else
            {
                if( parse.isKeyValid("start address") )
                {
                    function = EmetconProtocol::PutConfig_Raw;
                    OutMessage->Buffer.BSt.Function = FuncWrite_DNPReqTable;
                    OutMessage->Buffer.BSt.Length = 1;
                    OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Function_Write;
                    OutMessage->Buffer.BSt.Message[0] = parse.getiValue("start address");
                    populateDlcOutMessage(*OutMessage);
                    OutMessage->Sequence  = function;         // Helps us figure it out later!
                    OutMessage->Request.RouteID   = getRouteID();

                    strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
                    sendBackground(*OutMessage, outList);
                }

                function = EmetconProtocol::GetConfig_IEDDNP;
                found = getOperation(function, OutMessage->Buffer.BSt);
                incrementGroupMessageCount(pReq->UserMessageId(), pReq->getConnectionHandle());
            }
        }
    }
    else
    {
        nRet = Inherited::executeGetConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }

    if( found )
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Sequence  = function;         // Helps us figure it out later!

        OutMessage->Request.RouteID   = getRouteID();
        ::strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        nRet = ClientErrors::None;
    }

    return nRet;
}


bool Mct470Device::computeMultiplierFactors(double multiplier, unsigned &numerator, unsigned &denominator) const
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
        CTILOG_ERROR(dout, "device \""<< getName() <<"\" - Multiplier too large - must be less than 50000");

        retval = false;
    }
    else if( multiplier < 0.0001 )
    {
        CTILOG_ERROR(dout, "device \""<< getName() <<"\" - Multiplier too small - must be at least 0.0001");

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


YukonError_t Mct470Device::executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t nRet = ClientErrors::NoMethod;

    bool found = false;
    int function;

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ),
                                                   OutMessage->Request.CommandStr,
                                                   "",
                                                   nRet,
                                                   OutMessage->Request.RouteID,
                                                   OutMessage->Request.RetryMacroOffset,
                                                   OutMessage->Request.Attempt,
                                                   OutMessage->Request.GrpMsgID,
                                                   OutMessage->Request.UserID,
                                                   OutMessage->Request.SOE,
                                                   CtiMultiMsg_vec( ));

    if( parse.isKeyValid("precanned_table") )
    {
        function = EmetconProtocol::PutConfig_PrecannedTable;

        if( found = getOperation(function, OutMessage->Buffer.BSt) )
        {
            OutMessage->Buffer.BSt.Message[0] = gMCT400SeriesSPID;
            OutMessage->Buffer.BSt.Message[1] = (parse.getiValue("read_interval", 300) + 14) / 15;  //  count of 15 second increments
            OutMessage->Buffer.BSt.Message[2] = 0;
            OutMessage->Buffer.BSt.Message[3] = parse.getiValue("precanned_table");

            purgeDynamicPaoInfo(CtiTableDynamicPaoInfo::Key_MCT_PrecannedTableType);
        }
    }
    else if( parse.isKeyValid("channel_config") )
    {
        int channel, input, channel_config = 0;
        unsigned numerator, denominator;
        double multiplier;
        string type;

        channel = parse.getiValue("channel_offset");

        //  will be one of "ied", "2-wire", "3-wire", or "none"
        type = parse.getsValue("channel_type");

        //  default the input to the same as the channel if not specified
        input = parse.getiValue("channel_input", channel);

        multiplier = parse.getdValue("channel_multiplier", 1.0);

        function = EmetconProtocol::PutConfig_ChannelSetup;

        if( found = getOperation(function, OutMessage->Buffer.BSt) )
        {
            if( !computeMultiplierFactors(multiplier, numerator, denominator) )
            {
                errRet->setResultString("Invalid multiplier");
                nRet = ClientErrors::BadParameter;
                found = false;

                retList.push_back(errRet);
                errRet = 0;
            }
            else if( channel < 1 || channel > ChannelCount )
            {
                errRet->setResultString("Channel out of range (1-4)");
                nRet = ClientErrors::BadParameter;
                found = false;

                retList.push_back(errRet);
                errRet = 0;
            }
            else if( input < 1 || input > 16 )
            {
                errRet->setResultString("Input out of range (1-16)");
                nRet = ClientErrors::BadParameter;
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

        function = EmetconProtocol::PutConfig_Multiplier;

        if( found = getOperation(function, OutMessage->Buffer.BSt) )
        {
            if( !computeMultiplierFactors(multiplier, numerator, denominator) )
            {
                errRet->setResultString("Invalid multiplier");
                nRet = ClientErrors::BadParameter;
                found = false;

                retList.push_back(errRet);
                errRet = 0;
            }
            else if( channel < 1 || channel > ChannelCount )
            {
                errRet->setResultString("Channel out of range (1-4)");
                nRet = ClientErrors::BadParameter;
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
        if( parse.isKeyValid("ied dnp master address") )
        {
            function = EmetconProtocol::PutConfig_IEDDNPAddress;

            if( found = getOperation(function, OutMessage->Buffer.BSt) )
            {
                unsigned long master     = parse.getiValue("ied dnp master address");
                unsigned long outstation = parse.getiValue("ied dnp outstation address");

                if( master == 0 ||
                    master >  0xffff )
                {
                    errRet->setResultString("Invalid master DNP address (" + CtiNumStr(master) + ")");
                    nRet = ClientErrors::BadParameter;
                    found = false;

                    retList.push_back(errRet);
                    errRet = 0;
                }
                else if( outstation == 0 ||
                         outstation >  0xffff )
                {
                    errRet->setResultString("Invalid outstation DNP address (" + CtiNumStr(master) + ")");
                    nRet = ClientErrors::BadParameter;
                    found = false;

                    retList.push_back(errRet);
                    errRet = 0;
                }
                else
                {
                    OutMessage->Buffer.BSt.Message[0] = master >> 8;
                    OutMessage->Buffer.BSt.Message[1] = master;
                    OutMessage->Buffer.BSt.Message[2] = outstation >> 8;
                    OutMessage->Buffer.BSt.Message[3] = outstation;
                }
            }
        }
        else
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

                function = EmetconProtocol::PutConfig_Options;
                getOperation(function, OutMessage->Buffer.BSt);
            }
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
        populateDlcOutMessage(*OutMessage);
        OutMessage->Sequence  = function;         // Helps us figure it out later!

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        nRet = ClientErrors::None;
    }

    return nRet;
}


boost::optional<Mct470Device::IED_Types> Mct470Device::tryFindIedTypeInCommandString(const string &commandString)
{
    static const std::map<string, IED_Types> IedResetNames = {
        { " alpha",    IED_Type_Alpha_PP },
        { " s4",       IED_Type_LG_S4 },
        { " a3",       IED_Type_Alpha_A3 },
        { " sentinel", IED_Type_Sentinel },
        { " kv2c",     IED_Type_GE_kV2c },
        { " kv2",      IED_Type_GE_kV2 },
        { " kv",       IED_Type_GE_kV }};

    for each( const std::pair<string, IED_Types> iedTypeName in IedResetNames )
    {
        string::size_type pos = commandString.find(iedTypeName.first);

        if( pos != string::npos )
        {
            string::const_iterator end_of_iedtype = commandString.begin() + pos + iedTypeName.first.size();

            //  Check to see if the IED substring is at the end of the string OR if it's followed by a space
            if( end_of_iedtype == commandString.end() || isspace(*end_of_iedtype) )
            {
                return iedTypeName.second;
            }
        }
    }

    return boost::none;
}


boost::optional<long> Mct470Device::tryDetermineIedTypeFromDeviceConfiguration()
{
    //  Try to look it up by the device configs or by the config byte
    if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) )
    {
        return getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) >> 4;
    }
    else if( DeviceConfigSPtr deviceConfig = getDeviceConfig() )
    {
        return deviceConfig->findValue(MCTStrings::ElectronicMeter, IED_TypeMap);
    }

    return boost::none;
}

boost::optional<Mct470Device::IED_Types> Mct470Device::tryDetermineIedTypeFromDeviceType(const int deviceType)
{
    //  we must be an MCT-430 - try to match it up by devicetype
    typedef std::map<int, IED_Types> DeviceTypeToIedType;

    static const DeviceTypeToIedType IedDeviceTypes = {
        { TYPEMCT430A,   IED_Type_Alpha_PP },
        { TYPEMCT430S4,  IED_Type_LG_S4 },
        { TYPEMCT430A3,  IED_Type_Alpha_A3 },
        { TYPEMCT430SL,  IED_Type_Sentinel }};

    return mapFind(IedDeviceTypes, deviceType);
}


const Mct470Device::IedTypesToCommands
    Mct470Device::ResetCommandsByIedType = {
        { IED_Type_Alpha_PP, IedResetCommand
            { FuncWrite_IEDCommand,
                //  SPID, meter type, meter num, function Alpha reset
                { 0xff, IED_Type_Alpha_PP, 0, 1 }}},
        { IED_Type_LG_S4, IedResetCommand
            { FuncWrite_IEDCommand,
                //  SPID, meter type, meter num, function S4 reset
                { 0xff, IED_Type_LG_S4,    0, 0x2b}}},
        { IED_Type_Alpha_A3, IedResetCommand
            { FuncWrite_IEDCommandWithData,
                //  SPID, meter type, meter num, command, data length, demand reset bit set
                { 0xff, IED_Type_Alpha_A3, 0, 9, 1, 1 }}},
        { IED_Type_GE_kV2c, IedResetCommand
            { FuncWrite_IEDCommandWithData,
                //  SPID, meter type, meter num, command, data length, demand reset bit set
                { 0xff, IED_Type_GE_kV2c, 0, 9, 1, 1 }}},
        { IED_Type_GE_kV2, IedResetCommand
            { FuncWrite_IEDCommandWithData,
                //  SPID, meter type, meter num, command, data length, demand reset bit set
                { 0xff, IED_Type_GE_kV2,  0, 9, 1, 1 }}},
        { IED_Type_GE_kV, IedResetCommand
            { FuncWrite_IEDCommandWithData,
                //  SPID, meter type, meter num, command, data length, demand reset bit set
                { 0xff, IED_Type_GE_kV,   0, 9, 1, 1 }}},
        { IED_Type_Sentinel, IedResetCommand
            { FuncWrite_IEDCommandWithData,
                //  SPID, meter type, meter num, command, data length, demand reset bit set
                { 0xff, IED_Type_Sentinel, 0, 9, 1, 1}}}};


YukonError_t Mct470Device::executePutValue(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t nRet = ClientErrors::NoMethod;

    bool found = false;
    int function;

    std::unique_ptr<CtiReturnMsg> errRet(
        new CtiReturnMsg(
                getID( ),
                OutMessage->Request.CommandStr,
                "",
                nRet,
                OutMessage->Request.RouteID,
                OutMessage->Request.RetryMacroOffset,
                OutMessage->Request.Attempt,
                OutMessage->Request.GrpMsgID,
                OutMessage->Request.UserID,
                OutMessage->Request.SOE,
                CtiMultiMsg_vec( )));

    if( parse.isKeyValid("kyz") )
    {
        function = EmetconProtocol::PutValue_KYZ;
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

                        if( tmpPoint && tmpPoint->getType() == PulseAccumulatorPointType)
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

                        errRet->setResultString("Invalid reading specified for command");
                        errRet->setStatus(ClientErrors::NoMethod);
                        retList.push_back(errRet.release());
                    }
                }
                else
                {
                    found = false;

                    errRet->setResultString("Invalid offset specified (" + CtiNumStr(offset) + ")");
                    errRet->setStatus(ClientErrors::NoMethod);
                    retList.push_back(errRet.release());
                }
            }
        }
    }
    else if( parse.isKeyValid("ied") && parse.isKeyValid("reset") )
    {
        function = EmetconProtocol::PutValue_IEDReset;

        if( found = getOperation(function, OutMessage->Buffer.BSt) )
        {
            boost::optional<int> iedType;

            iedType = tryFindIedTypeInCommandString(parse.getCommandStr());

            if( ! iedType )
            {
                iedType = tryDetermineIedTypeFromDeviceType(getType());

                if( ! iedType )
                {
                    iedType = tryDetermineIedTypeFromDeviceConfiguration();

                    if( ! iedType )
                    {
                        errRet->setResultString("Could not determine the IED type");
                        errRet->setStatus(ClientErrors::MissingConfig);
                        retList.push_back(errRet.release());

                        return ClientErrors::MissingConfig;
                    }
                }
            }

            IedTypesToCommands::const_iterator itr = ResetCommandsByIedType.find(*iedType);

            if( itr == ResetCommandsByIedType.end() )
            {
                errRet->setResultString("Could not determine the command for IED type " + CtiNumStr(*iedType));
                errRet->setStatus(ClientErrors::MissingConfig);
                retList.push_back(errRet.release());

                return ClientErrors::MissingConfig;
            }

            const IedResetCommand &command = itr->second;

            OutMessage->Buffer.BSt.Function = command.function;
            OutMessage->Buffer.BSt.Length   = command.payload.size();
            std::copy(command.payload.begin(), command.payload.end(), OutMessage->Buffer.BSt.Message);
        }
    }
    else
    {
        nRet = Inherited::executePutValue(pReq, parse, OutMessage, vgList, retList, outList);
    }

    if( found )
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Sequence  = function;         // Helps us figure it out later!

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().data(), COMMAND_STR_SIZE);

        nRet = ClientErrors::None;
    }

    return nRet;
}


unsigned char makeByteFrom2BitQuantities(unsigned a, unsigned b, unsigned c, unsigned d)
{
    return
        (a << 6 & 0xC0) |
        (b << 4 & 0x30) |
        (c << 2 & 0x0C) |
        (d      & 0x03);
}


YukonError_t Mct470Device::executePutConfigTOU(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,CtiMessageList&vgList,CtiMessageList&retList,OutMessageList &outList, bool readsOnly)
{
    DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( ! deviceConfig )
    {
        return ClientErrors::NoConfigData;
    }

    if( ! readsOnly )
    {
        string rateStringValues[4][6], timeStringValues[4][5],
               daySchedule1, daySchedule2, daySchedule3, daySchedule4,
               dynDaySchedule1, dynDaySchedule2, dynDaySchedule3, dynDaySchedule4;

        const boost::optional<bool> optTouEnabled = deviceConfig->findValue<bool>(MCTStrings::touEnabled);

        if( ! optTouEnabled )
        {
            CTILOG_ERROR(dout, "device "<< getName() <<" missing config value "<< MCTStrings::touEnabled);

            return ClientErrors::NoConfigData;
        }

        const bool cfgTouEnabled = *optTouEnabled;

        const boost::optional<long> mondaySchedule    = deviceConfig->findValue(MCTStrings::MondaySchedule, scheduleMap);
        const boost::optional<long> tuesdaySchedule   = deviceConfig->findValue(MCTStrings::TuesdaySchedule, scheduleMap);
        const boost::optional<long> wednesdaySchedule = deviceConfig->findValue(MCTStrings::WednesdaySchedule, scheduleMap);
        const boost::optional<long> thursdaySchedule  = deviceConfig->findValue(MCTStrings::ThursdaySchedule, scheduleMap);
        const boost::optional<long> fridaySchedule    = deviceConfig->findValue(MCTStrings::FridaySchedule, scheduleMap);
        const boost::optional<long> saturdaySchedule  = deviceConfig->findValue(MCTStrings::SaturdaySchedule, scheduleMap);
        const boost::optional<long> sundaySchedule    = deviceConfig->findValue(MCTStrings::SundaySchedule, scheduleMap);
        const boost::optional<long> holidaySchedule   = deviceConfig->findValue(MCTStrings::HolidaySchedule, scheduleMap);

        //These are all string values
        timeStringValues[0][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Time1);
        timeStringValues[0][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Time2);
        timeStringValues[0][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Time3);
        timeStringValues[0][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Time4);
        timeStringValues[0][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Time5);

        timeStringValues[1][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time1);
        timeStringValues[1][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time2);
        timeStringValues[1][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time3);
        timeStringValues[1][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time4);
        timeStringValues[1][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Time5);

        timeStringValues[2][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time1);
        timeStringValues[2][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time2);
        timeStringValues[2][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time3);
        timeStringValues[2][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time4);
        timeStringValues[2][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Time5);

        timeStringValues[3][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time1);
        timeStringValues[3][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time2);
        timeStringValues[3][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time3);
        timeStringValues[3][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time4);
        timeStringValues[3][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Time5);

        rateStringValues[0][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate0);
        rateStringValues[0][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate1);
        rateStringValues[0][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate2);
        rateStringValues[0][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate3);
        rateStringValues[0][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate4);
        rateStringValues[0][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule1Rate5);

        rateStringValues[1][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate0);
        rateStringValues[1][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate1);
        rateStringValues[1][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate2);
        rateStringValues[1][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate3);
        rateStringValues[1][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate4);
        rateStringValues[1][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule2Rate5);

        rateStringValues[2][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate0);
        rateStringValues[2][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate1);
        rateStringValues[2][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate2);
        rateStringValues[2][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate3);
        rateStringValues[2][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate4);
        rateStringValues[2][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule3Rate5);

        rateStringValues[3][0] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate0);
        rateStringValues[3][1] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate1);
        rateStringValues[3][2] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate2);
        rateStringValues[3][3] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate3);
        rateStringValues[3][4] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate4);
        rateStringValues[3][5] = deviceConfig->getValueFromKey(MCTStrings::Schedule4Rate5);

        string defaultTOURateString = deviceConfig->getValueFromKey(MCTStrings::DefaultTOURate);

        long durations[4][5];
        long rates[4][6];

        for( int i = 0; i < 4; i++ )
        {
            //  first, set up the rates
            for( int j = 0; j < 6; j++ )
            {
                const std::string &rateStringValue = rateStringValues[i][j];

                if( rateStringValue.empty() )
                {
                    CTILOG_ERROR(dout, "device " << getName() << " bad rate string stored");

                    return ClientErrors::NoConfigData;
                }

                if( rateStringValue[0] < 'A' || rateStringValue[0] > 'D' )
                {
                    CTILOG_ERROR(dout, "device "<< getName() <<" bad rate string stored");

                    return ClientErrors::NoConfigData;
                }

                rates[i][j] = rateStringValue[0] - 'A';
            }

            int prevTime = 0;

            //  then calculate the durations from the time strings
            for( int j = 0; j < 5; j++ )
            {
                std::string &timeStringValue = timeStringValues[i][j];

                if( timeStringValue.length() < 4 ) //A time needs at least 4 digits X:XX
                {
                    CTILOG_ERROR(dout, "device "<< getName() <<" bad time string stored");

                    return ClientErrors::NoConfigData;
                }

                const std::string::size_type colonPos = timeStringValue.find(':');
                if( colonPos == std::string::npos )
                {
                    CTILOG_ERROR(dout, "device "<< getName() <<" bad time string stored");

                    return ClientErrors::NoConfigData;
                }

                // Remove the :, get the remaining value, and do simple math to extract the hours and minutes.
                timeStringValue.erase(colonPos, 1);

                const long tempTime = strtol(timeStringValue.data(),NULL,10);

                long &duration = durations[i][j];

                //  if the time is unused, ignore the rate change entirely
                if( tempTime == 0 )
                {
                    //  ignore this rate change, continue the previous
                    rates[i][j+1] = rates[i][j];
                    duration = 255;
                    prevTime = 24 * 60;  //  set to end of day marker
                }
                else
                {
                    const int currentTime = ((tempTime/100) * 60) + (tempTime%100);

                    duration = currentTime / 5;

                    if( j )
                    {
                        duration -= prevTime / 5;
                    }

                    prevTime = currentTime;

                    if( duration < 0 || duration > 255 )
                    {
                        CTILOG_ERROR(dout, "device "<< getName() <<" time sequencing");

                        return ClientErrors::NoConfigData;
                    }
                }
            }
        }

        if( defaultTOURateString.empty() )
        {
            CTILOG_ERROR(dout, "device "<< getName() <<" empty default TOU rate");

            return ClientErrors::NoConfigData;
        }

        const long defaultTOURate = defaultTOURateString[0] - 'A';
        if( defaultTOURate < 0 || defaultTOURate > 3 )
        {
            CTILOG_ERROR(dout, "device "<< getName() <<" bad default rate");

            return ClientErrors::NoConfigData;
        }

        if( !mondaySchedule    ||
            !tuesdaySchedule   ||
            !fridaySchedule    ||
            !saturdaySchedule  ||
            !sundaySchedule    ||
            !holidaySchedule   ||
            !wednesdaySchedule ||
            !thursdaySchedule )
        {
            CTILOG_ERROR(dout, "device "<< getName() <<" no or bad schedule value stored");

            return ClientErrors::NoConfigData;
        }

        long dayTable;
        dayTable =  *holidaySchedule   << 14;
        dayTable |= *saturdaySchedule  << 12;
        dayTable |= *fridaySchedule    << 10;
        dayTable |= *thursdaySchedule  << 8;
        dayTable |= *wednesdaySchedule << 6;
        dayTable |= *tuesdaySchedule   << 4;
        dayTable |= *mondaySchedule    << 2;
        dayTable |= *sundaySchedule;

        createTOUDayScheduleString(daySchedule1, durations[0], rates[0]);
        createTOUDayScheduleString(daySchedule2, durations[1], rates[1]);
        createTOUDayScheduleString(daySchedule3, durations[2], rates[2]);
        createTOUDayScheduleString(daySchedule4, durations[3], rates[3]);
        CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, dynDaySchedule1);
        CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, dynDaySchedule2);
        CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3, dynDaySchedule3);
        CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4, dynDaySchedule4);

        const bool dynTouEnabled = (getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TouEnabled) == (long)true);

        if( ! parse.isKeyValid("force")
            && dynTouEnabled == cfgTouEnabled
            && getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DayTable) == dayTable
            && (cfgTouEnabled == false || getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DefaultTOURate) == defaultTOURate)
            && dynDaySchedule1 == daySchedule1
            && dynDaySchedule2 == daySchedule2
            && dynDaySchedule3 == daySchedule3
            && dynDaySchedule4 == daySchedule4)
        {
            return ClientErrors::ConfigCurrent;
        }

        if( parse.isKeyValid("verify") )
        {
            return ClientErrors::ConfigNotCurrent;
        }

        OutMessage->Buffer.BSt.Function   = cfgTouEnabled ? Command_TOUEnable : Command_TOUDisable;
        OutMessage->Buffer.BSt.Length     = 0;
        OutMessage->Buffer.BSt.IO         = EmetconProtocol::IO_Write;

        outList.push_back( new OUTMESS(*OutMessage) );

        OutMessage->Buffer.BSt.Function    = FuncWrite_TOUSchedule1Pos;
        OutMessage->Buffer.BSt.Length      = FuncWrite_TOUSchedule1Len;
        OutMessage->Buffer.BSt.IO          = EmetconProtocol::IO_Function_Write;
        OutMessage->Buffer.BSt.Message[0]  = dayTable >> 8;
        OutMessage->Buffer.BSt.Message[1]  = dayTable;
        OutMessage->Buffer.BSt.Message[2]  = durations[0][0];
        OutMessage->Buffer.BSt.Message[3]  = durations[0][1];
        OutMessage->Buffer.BSt.Message[4]  = durations[0][2];
        OutMessage->Buffer.BSt.Message[5]  = durations[0][3];
        OutMessage->Buffer.BSt.Message[6]  = durations[0][4];
        OutMessage->Buffer.BSt.Message[7]  = makeByteFrom2BitQuantities(rates[1][5], rates[1][4], rates[0][5], rates[0][4]);
        OutMessage->Buffer.BSt.Message[8]  = makeByteFrom2BitQuantities(rates[0][3], rates[0][2], rates[0][1], rates[0][0]);
        OutMessage->Buffer.BSt.Message[9]  = durations[1][0];
        OutMessage->Buffer.BSt.Message[10] = durations[1][1];
        OutMessage->Buffer.BSt.Message[11] = durations[1][2];
        OutMessage->Buffer.BSt.Message[12] = durations[1][3];
        OutMessage->Buffer.BSt.Message[13] = durations[1][4];
        OutMessage->Buffer.BSt.Message[14] = makeByteFrom2BitQuantities(rates[1][3], rates[1][2], rates[1][1], rates[1][0]);

        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

        OutMessage->Buffer.BSt.Function    = FuncWrite_TOUSchedule2Pos;
        OutMessage->Buffer.BSt.Length      = FuncWrite_TOUSchedule2Len;
        OutMessage->Buffer.BSt.IO          = EmetconProtocol::IO_Function_Write;
        OutMessage->Buffer.BSt.Message[0]  = durations[2][0];
        OutMessage->Buffer.BSt.Message[1]  = durations[2][1];
        OutMessage->Buffer.BSt.Message[2]  = durations[2][2];
        OutMessage->Buffer.BSt.Message[3]  = durations[2][3];
        OutMessage->Buffer.BSt.Message[4]  = durations[2][4];
        OutMessage->Buffer.BSt.Message[5]  = makeByteFrom2BitQuantities(          0,           0, rates[2][5], rates[2][4]);
        OutMessage->Buffer.BSt.Message[6]  = makeByteFrom2BitQuantities(rates[2][3], rates[2][2], rates[2][1], rates[2][0]);

        OutMessage->Buffer.BSt.Message[7]  = durations[3][0];
        OutMessage->Buffer.BSt.Message[8]  = durations[3][1];
        OutMessage->Buffer.BSt.Message[9]  = durations[3][2];
        OutMessage->Buffer.BSt.Message[10] = durations[3][3];
        OutMessage->Buffer.BSt.Message[11] = durations[3][4];
        OutMessage->Buffer.BSt.Message[12] = makeByteFrom2BitQuantities(          0,           0, rates[3][5], rates[3][4]);
        OutMessage->Buffer.BSt.Message[13] = makeByteFrom2BitQuantities(rates[3][3], rates[3][2], rates[3][1], rates[3][0]);
        OutMessage->Buffer.BSt.Message[14] = defaultTOURate;

        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
    }

    // Set up the reads here
    OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Pos;
    OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Len;
    OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;
    OUTMESS *touOutMessage = CTIDBG_new OUTMESS(*OutMessage);
    touOutMessage->Priority             -= 1;//decrease for read. Only want read after a successful write.
    touOutMessage->Sequence = EmetconProtocol::GetConfig_TOU;
    strncpy(touOutMessage->Request.CommandStr, "getconfig tou schedule 1", COMMAND_STR_SIZE );
    outList.push_back( CTIDBG_new OUTMESS(*touOutMessage) );

    touOutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Pos;
    touOutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Len;
    strncpy(touOutMessage->Request.CommandStr, "getconfig tou schedule 3", COMMAND_STR_SIZE );
    outList.push_back( CTIDBG_new OUTMESS(*touOutMessage) );

    touOutMessage->Buffer.BSt.Function = FuncRead_TOUStatusPos;
    touOutMessage->Buffer.BSt.Length   = FuncRead_TOUStatusLen;
    strncpy(touOutMessage->Request.CommandStr, "getconfig tou", COMMAND_STR_SIZE );
    outList.push_back( touOutMessage );
    touOutMessage = 0;

    return ClientErrors::None;
}

YukonError_t Mct470Device::executePutConfigLoadProfileChannel(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,CtiMessageList&vgList,CtiMessageList&retList,OutMessageList &outList, bool readsOnly)
{
    YukonError_t nRet = ClientErrors::None;
    DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if (deviceConfig)
    {
        boost::optional<long>
            spid = CtiDeviceBase::findDynamicInfo<long>(CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID);

        if ( ! spid )       // we don't have it in dynamic pao info, try the device config
        {
            spid = deviceConfig->findValue<long>(MCTStrings::ServiceProviderID);

            if ( ! spid )   // we don't have it in the device config either, set a default
            {
                spid = gMCT400SeriesSPID;
            }
        }

        if( ! readsOnly )
        {
            const boost::optional<long>
                channel1physical = deviceConfig->findValue<long>(MCTStrings::Channel1PhysicalChannel),
                channel1type     = deviceConfig->findValue<long>(MCTStrings::Channel1Type, channelTypeMap),
                channel2physical = deviceConfig->findValue<long>(MCTStrings::Channel2PhysicalChannel),
                channel2type     = deviceConfig->findValue<long>(MCTStrings::Channel2Type, channelTypeMap);

            const boost::optional<double>
                peakKwResolution1             = deviceConfig->findValue<double>(MCTStrings::PeakKwResolution1, resolutionMap),
                lastIntervalDemandResolution1 = deviceConfig->findValue<double>(MCTStrings::LastIntervalDemandResolution1, resolutionMap),
                lpResolution1                 = deviceConfig->findValue<double>(MCTStrings::ProfileResolution1, resolutionMap),
                peakKwResolution2             = deviceConfig->findValue<double>(MCTStrings::PeakKwResolution2, resolutionMap),
                lastIntervalDemandResolution2 = deviceConfig->findValue<double>(MCTStrings::LastIntervalDemandResolution2, resolutionMap),
                lpResolution2                 = deviceConfig->findValue<double>(MCTStrings::ProfileResolution2, resolutionMap);

            if (   ! channel1physical  || ! channel1type
                || ! channel2physical  || ! channel2type
                || ! peakKwResolution1 || ! lastIntervalDemandResolution1 || ! lpResolution1
                || ! peakKwResolution2 || ! lastIntervalDemandResolution2 || ! lpResolution2 )
            {
                if( getMCTDebugLevel(DebugLevel_Configs) )
                {
                    CTILOG_DEBUG(dout, "Configs - no or bad value stored, LPChannel");
                }
                nRet = ClientErrors::NoConfigData;
            }
            else
            {
                unsigned ratio1, kRatio1, ratio2, kRatio2;

                const long channel1 = ( ( ( *channel1physical - 1 ) & 0x0f ) << 2 ) | ( *channel1type & 0x03 );
                const long channel2 = ( ( ( *channel2physical - 1 ) & 0x0f ) << 2 ) | ( *channel2type & 0x03 );

                const double multiplier1 = deviceConfig->getFloatValueFromKey(MCTStrings::ChannelMultiplier1);
                const double multiplier2 = deviceConfig->getFloatValueFromKey(MCTStrings::ChannelMultiplier2);

                if ((setupRatioBytesBasedOnMeterType(channel1,multiplier1,*peakKwResolution1,*lastIntervalDemandResolution1,*lpResolution1,ratio1,kRatio1) == ClientErrors::None) &&
                    (setupRatioBytesBasedOnMeterType(channel2,multiplier2,*peakKwResolution2,*lastIntervalDemandResolution2,*lpResolution2,ratio2,kRatio2) == ClientErrors::None))
                {
                    if (parse.isKeyValid("force") || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig1)  != channel1
                                                  || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig2) != channel2
                                                  || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio1) != ratio1
                                                  || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio2) != ratio2
                                                  || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio1) != kRatio1
                                                  || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio2) != kRatio2)
                    {
                        if( !parse.isKeyValid("verify") )
                        {
                            OutMessage->Buffer.BSt.Function    = FuncWrite_SetupLPChannelsPos;
                            OutMessage->Buffer.BSt.Length      = FuncWrite_SetupLPChannelsLen;
                            OutMessage->Buffer.BSt.IO          = EmetconProtocol::IO_Function_Write;
                            OutMessage->Buffer.BSt.Message[0]  = *spid;
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

                        }
                        else
                        {
                            nRet = ClientErrors::ConfigNotCurrent;
                        }
                    }
                    else if( nRet == ClientErrors::None )
                    {
                        nRet = ClientErrors::ConfigCurrent;
                    }
                }
            }
        }

        //Either we sent the put ok, or we are doing a read to get into here.
        if (nRet == ClientErrors::None && ! strstr(OutMessage->Request.CommandStr, "lpchannel 34") )
        {
            OutMessage->Buffer.BSt.Function   = FuncRead_LoadProfileChannel12Pos;
            OutMessage->Buffer.BSt.Length     = FuncRead_LoadProfileChannel12Len;
            OutMessage->Buffer.BSt.IO         = EmetconProtocol::IO_Function_Read;

            insertConfigReadOutMessage("getconfig install lpchannel 12", *OutMessage, outList);
        }

        if( ! readsOnly )
        {
            const boost::optional<long>
                channel3physical = deviceConfig->findValue<long>(MCTStrings::Channel3PhysicalChannel),
                channel3type     = deviceConfig->findValue<long>(MCTStrings::Channel3Type, channelTypeMap),
                channel4physical = deviceConfig->findValue<long>(MCTStrings::Channel4PhysicalChannel),
                channel4type     = deviceConfig->findValue<long>(MCTStrings::Channel4Type, channelTypeMap);

            const boost::optional<double>
                peakKwResolution3             = deviceConfig->findValue<double>(MCTStrings::PeakKwResolution3, resolutionMap),
                lastIntervalDemandResolution3 = deviceConfig->findValue<double>(MCTStrings::LastIntervalDemandResolution3, resolutionMap),
                lpResolution3                 = deviceConfig->findValue<double>(MCTStrings::ProfileResolution3, resolutionMap),
                peakKwResolution4             = deviceConfig->findValue<double>(MCTStrings::PeakKwResolution4, resolutionMap),
                lastIntervalDemandResolution4 = deviceConfig->findValue<double>(MCTStrings::LastIntervalDemandResolution4, resolutionMap),
                lpResolution4                 = deviceConfig->findValue<double>(MCTStrings::ProfileResolution4, resolutionMap);

            if (   ! channel3physical  || ! channel3type
                || ! channel4physical  || ! channel4type
                || ! peakKwResolution3 || ! lastIntervalDemandResolution3 || ! lpResolution3
                || ! peakKwResolution4 || ! lastIntervalDemandResolution4 || ! lpResolution4 )
            {
                if( getMCTDebugLevel(DebugLevel_Configs) )
                {
                    CTILOG_DEBUG(dout, "Configs - no or bad value stored, LPChannel");
                }
                nRet = ClientErrors::NoConfigData;
            }
            else
            {
                unsigned ratio3, kRatio3, ratio4, kRatio4;

                const long channel3 = ( ( ( *channel3physical - 1 ) & 0x0f ) << 2 ) | ( *channel3type & 0x03 );
                const long channel4 = ( ( ( *channel4physical - 1 ) & 0x0f ) << 2 ) | ( *channel4type & 0x03 );

                const double multiplier3 = deviceConfig->getFloatValueFromKey(MCTStrings::ChannelMultiplier3);
                const double multiplier4 = deviceConfig->getFloatValueFromKey(MCTStrings::ChannelMultiplier4);

                if ((setupRatioBytesBasedOnMeterType(channel3,multiplier3,*peakKwResolution3,*lastIntervalDemandResolution3,*lpResolution3,ratio3,kRatio3) == ClientErrors::None) &&
                    (setupRatioBytesBasedOnMeterType(channel4,multiplier4,*peakKwResolution4,*lastIntervalDemandResolution4,*lpResolution4,ratio4,kRatio4) == ClientErrors::None))
                {
                    if( parse.isKeyValid("force")
                        || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig3) != channel3
                        || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileChannelConfig4) != channel4
                        || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio3)    != ratio3
                        || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileMeterRatio4)    != ratio4
                        || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio3)        != kRatio3
                        || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileKRatio4)        != kRatio4 )
                    {
                        if( !parse.isKeyValid("verify") )
                        {
                            OutMessage->Buffer.BSt.Function    = FuncWrite_SetupLPChannelsPos;
                            OutMessage->Buffer.BSt.Length      = FuncWrite_SetupLPChannelsLen;
                            OutMessage->Buffer.BSt.IO          = EmetconProtocol::IO_Function_Write;
                            OutMessage->Buffer.BSt.Message[0]  = *spid;
                            OutMessage->Buffer.BSt.Message[1]  = 3;
                            OutMessage->Buffer.BSt.Message[2]  = (channel3);
                            OutMessage->Buffer.BSt.Message[3]  = (ratio3>>8);
                            OutMessage->Buffer.BSt.Message[4]  = (ratio3);
                            OutMessage->Buffer.BSt.Message[5]  = (kRatio3>>8);
                            OutMessage->Buffer.BSt.Message[6]  = (kRatio3);
                            OutMessage->Buffer.BSt.Message[7]  = 4;
                            OutMessage->Buffer.BSt.Message[8]  = (channel4);
                            OutMessage->Buffer.BSt.Message[9]  = (ratio4>>8);
                            OutMessage->Buffer.BSt.Message[10] = (ratio4);
                            OutMessage->Buffer.BSt.Message[11] = (kRatio4>>8);
                            OutMessage->Buffer.BSt.Message[12] = (kRatio4);

                            outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                        }
                        else
                        {
                            nRet = ClientErrors::ConfigNotCurrent;
                        }
                    }
                    else if( nRet == ClientErrors::None )
                    {
                        nRet = ClientErrors::ConfigCurrent;
                    }
                }
            }
        }

        //Either we sent the put ok, or we are doing a read to get into here.
        if (nRet == ClientErrors::None && ! strstr(OutMessage->Request.CommandStr, "lpchannel 12") )
        {
            OutMessage->Buffer.BSt.Function   = FuncRead_LoadProfileChannel34Pos;
            OutMessage->Buffer.BSt.Length     = FuncRead_LoadProfileChannel34Len;
            OutMessage->Buffer.BSt.IO         = EmetconProtocol::IO_Function_Read;

            insertConfigReadOutMessage("getconfig install lpchannel 34", *OutMessage, outList);
        }
    }
    else
    {
        nRet = ClientErrors::NoConfigData;
    }

    return nRet;
}

YukonError_t Mct470Device::executePutConfigRelays(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,CtiMessageList&vgList,CtiMessageList&retList,OutMessageList &outList, bool readsOnly)
{
    YukonError_t nRet = ClientErrors::None;
    DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if (deviceConfig)
    {
        if( ! readsOnly )
        {
            boost::optional<long>
                spid        = CtiDeviceBase::findDynamicInfo<long>(CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID);

            const boost::optional<double>
                relayATimer = deviceConfig->findValue<double>(MCTStrings::RelayATimer),
                relayBTimer = deviceConfig->findValue<double>(MCTStrings::RelayBTimer);

            if ( ! spid )       // we don't have it in dynamic pao info, try the device config
            {
                spid = deviceConfig->findValue<long>(MCTStrings::ServiceProviderID);
            }

            if ( ! relayATimer || ! relayBTimer || ! spid )
            {
                CTILOG_ERROR(dout, "no or bad value stored");

                nRet = ClientErrors::NoConfigData;
            }
            else
            {
                double dynRelayATimer;
                double dynRelayBTimer;
                CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_RelayATimer, dynRelayATimer);
                CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_RelayBTimer, dynRelayBTimer);

                if (parse.isKeyValid("force")
                    || dynRelayATimer != (*relayATimer * 4.0)
                    || dynRelayBTimer != (*relayBTimer * 4.0))
                {
                    if (!parse.isKeyValid("verify"))
                    {
                        OutMessage->Buffer.BSt.Function   = FuncWrite_RelaysPos;
                        OutMessage->Buffer.BSt.Length     = FuncWrite_RelaysLen;
                        OutMessage->Buffer.BSt.IO         = EmetconProtocol::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0] = *spid;
                        // From S01030 Software Specification for MCT-470:
                        // relayTimer: a value of 1 corresponds to 250 ms, a value of 4 corresponds to 1 second...  
                        OutMessage->Buffer.BSt.Message[1] = static_cast<unsigned char>((*relayATimer)*4);
                        OutMessage->Buffer.BSt.Message[2] = static_cast<unsigned char>((*relayBTimer)*4);

                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
                    }
                    else
                    {
                        nRet = ClientErrors::ConfigNotCurrent;
                    }
                }
                else
                {
                    nRet = ClientErrors::ConfigCurrent;
                }
            }
        }

        //Either we sent the put ok, or we are doing a read to get into here.
        if (nRet == ClientErrors::None)
        {
            OutMessage->Buffer.BSt.Function   = Memory_RelayATimerPos;
            OutMessage->Buffer.BSt.Length     = Memory_RelayATimerLen + Memory_RelayBTimerLen;
            OutMessage->Buffer.BSt.IO         = EmetconProtocol::IO_Read;

            insertConfigReadOutMessage("getconfig install relays", *OutMessage, outList);
        }
    }
    else
    {
        nRet = ClientErrors::NoConfigData;
    }

    return nRet;
}

YukonError_t Mct470Device::executePutConfigDemandLP(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,CtiMessageList&vgList,CtiMessageList&retList,OutMessageList &outList, bool readsOnly)
{
    DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( ! deviceConfig )
    {
        return ClientErrors::NoConfigData;
    }

    try
    {
        if( ! readsOnly )
        {
            const long cfgDemandInterval = deviceConfig->getLongValueFromKey(MCTStrings::DemandInterval);
            boost::optional<long> cfgProfileInterval;  //  only used for MCT-470s

            bool match = CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DemandInterval) == cfgDemandInterval;

            if( isMct470(getType()) )
            {
                cfgProfileInterval = deviceConfig->getLongValueFromKey(MCTStrings::ProfileInterval);

                match &= CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval) == *cfgProfileInterval;
            }

            if( match && ! parse.isKeyValid("force") )
            {
                return ClientErrors::ConfigCurrent;
            }
            if( ! match && parse.isKeyValid("verify") )
            {
                return ClientErrors::ConfigNotCurrent;
            }

            OutMessage->Buffer.BSt.Function   = FuncWrite_IntervalsPos;
            OutMessage->Buffer.BSt.Length     = isMct470(getType()) ? 2 : 1;  //  only set LP interval if it's an MCT-470
            OutMessage->Buffer.BSt.IO         = EmetconProtocol::IO_Function_Write;
            OutMessage->Buffer.BSt.Message[0] = cfgDemandInterval;
            if( isMct470(getType()) )
            {
                OutMessage->Buffer.BSt.Message[1] = *cfgProfileInterval;
            }

            outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );
        }

        OutMessage->Buffer.BSt.Function   = Memory_IntervalsPos;
        OutMessage->Buffer.BSt.Length     = Memory_IntervalsLen;
        OutMessage->Buffer.BSt.IO         = EmetconProtocol::IO_Read;

        insertConfigReadOutMessage("getconfig install demandlp", *OutMessage, outList);

        return ClientErrors::None;
    }
    catch( const MissingConfigDataException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Device \""<< getName() <<"\"");

        return ClientErrors::NoConfigData;
    }
}

YukonError_t Mct470Device::executePutConfigPrecannedTable(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,CtiMessageList&vgList,CtiMessageList&retList,OutMessageList &outList, bool readsOnly)
{
    YukonError_t nRet = ClientErrors::None;
    DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if (deviceConfig)
    {
        if( ! readsOnly )
        {
            const boost::optional<long> tableReadInterval = deviceConfig->findValue<long>(MCTStrings::TableReadInterval);
            const boost::optional<long> meterNumber       = deviceConfig->findValue<long>(MCTStrings::MeterNumber);
            const boost::optional<long> tableType         = deviceConfig->findValue<long>(MCTStrings::TableType);
            const boost::optional<long> spid              = deviceConfig->findValue<long>(MCTStrings::ServiceProviderID);

            if( ! tableReadInterval || ! tableType || ! spid  //  if we don't have TableReadInterval, TableType, or SPID...
                || (isMct470(getType()) && ! meterNumber) )   //  or we're an MCT-470 and we don't have MeterNumber
            {
                CTILOG_ERROR(dout, "no or bad value stored");

                nRet = ClientErrors::NoConfigData;
            }
            else
            {
                bool stale = false;

                // Dyn data is device value, database is seconds. 
                stale |= getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_PrecannedTableReadInterval) != (*tableReadInterval) / 15; 

                stale |= getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_PrecannedTableType)         != *tableType;
                stale |= getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID)   != *spid;

                if( isMct470(getType()) )
                {
                    stale |= getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_PrecannedMeterNumber)   != *meterNumber;
                }

                if( parse.isKeyValid("force") || stale )
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        OutMessage->Buffer.BSt.Function   = FuncWrite_PrecannedTablePos;
                        OutMessage->Buffer.BSt.Length     = FuncWrite_PrecannedTableLen;
                        OutMessage->Buffer.BSt.IO         = EmetconProtocol::IO_Function_Write;
                        OutMessage->Buffer.BSt.Message[0] = *spid;
                        // From S01030 Software Specification for MCT-470:
                        // This interval is stored in 15 second increments (i.e. 3 = 45 seconds, 4 = 60 seconds, etc.).  
                        // The (hardware) default is 20 (5 minutes). 
                        OutMessage->Buffer.BSt.Message[1] = (*tableReadInterval) / 15;
                        OutMessage->Buffer.BSt.Message[2] = isMct470(getType())
                                                                ? *meterNumber
                                                                : 0;  //  0 for MCT-430s
                        OutMessage->Buffer.BSt.Message[3] = *tableType;
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                    }
                    else
                    {
                        nRet = ClientErrors::ConfigNotCurrent;
                    }
                }
                else
                {
                    nRet = ClientErrors::ConfigCurrent;
                }
            }
        }

        //Either we sent the put ok, or we are doing a read to get into here.
        if (nRet == ClientErrors::None)
        {
            OutMessage->Buffer.BSt.Function   = FuncRead_PrecannedTablePos;
            OutMessage->Buffer.BSt.Length     = FuncRead_PrecannedTableLen;
            OutMessage->Buffer.BSt.IO         = EmetconProtocol::IO_Function_Read;

            if( ! strstr(OutMessage->Request.CommandStr, "precannedtable spid") )
            {
                insertConfigReadOutMessage("getconfig install precannedtable nospid", *OutMessage, outList);
            }

            if( ! strstr(OutMessage->Request.CommandStr, "precannedtable nospid")
                && getOperation(EmetconProtocol::PutConfig_SPID, OutMessage->Buffer.BSt) )
            {
                OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Read;

                insertConfigReadOutMessage("getconfig install precannedtable spid", *OutMessage, outList);
            }
        }
    }
    else
    {
        nRet = ClientErrors::NoConfigData;
    }

    return nRet;
}

YukonError_t Mct470Device::executePutConfigConfigurationByte(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( ! deviceConfig )
    {
        return ClientErrors::NoConfigData;
    }

    if( ! readsOnly )
    {
        const boost::optional<bool> dstEnabled          = deviceConfig->findValue<bool>(MCTStrings::EnableDst);
        const boost::optional<long> electronicMeterType = deviceConfig->findValue<long>(MCTStrings::ElectronicMeter, IED_TypeMap);

        long dynamicPaoConfigByte = getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration);
        boost::optional<long> configuration;

        if( dstEnabled )
        {
            if( isMct430(getType()) )
            {
                //  ignore the electronic meter type when comparing with the expected device config value
                dynamicPaoConfigByte &= 0x0F;

                configuration   =  *dstEnabled & 0x01;
            }
            else if( electronicMeterType )
            {
                configuration   =  *dstEnabled & 0x01;
                *configuration |= (*electronicMeterType & 0x0f) << 4;
            }
        }

        if( ! configuration )
        {
            CTILOG_ERROR(dout, "no or bad configuration value stored");

            return ClientErrors::NoConfigData;
        }

        if( dynamicPaoConfigByte == configuration )
        {
            if( ! parse.isKeyValid("force") )
            {
                return ClientErrors::ConfigCurrent;
            }
        }

        if( parse.isKeyValid("verify") )
        {
            return ClientErrors::ConfigNotCurrent;
        }

        OutMessage->Buffer.BSt.Message[0] = *configuration;
        OutMessage->Buffer.BSt.Function = FuncWrite_ConfigAlarmMaskPos;
        OutMessage->Buffer.BSt.Length = 1; //We are only writing a single byte, the command supports more.
        OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Write;
        outList.push_back( new OUTMESS(*OutMessage) );
    }

    OutMessage->Buffer.BSt.IO         = EmetconProtocol::IO_Read;
    OutMessage->Buffer.BSt.Function   = Memory_ConfigurationPos;
    OutMessage->Buffer.BSt.Length     = Memory_ConfigurationLen;

    insertConfigReadOutMessage("getconfig install configbyte", *OutMessage, outList);

    return ClientErrors::None;
}

int Mct470Device::sendDNPConfigMessages(int startMCTID, OutMessageList &outList, OUTMESS *&OutMessage, string &dataA, string &dataB, CtiTableDynamicPaoInfo::PaoInfoKeys key, bool force, bool verifyOnly)
{
    int nRet = ClientErrors::None;
    const int bufferSize = 26;
    BYTE buffer[bufferSize];

    if( dataA.empty() || dataB.empty() )
    {
        CTILOG_ERROR(dout, "Configuration not found");

        nRet = ClientErrors::NoConfigData;
    }
    else if( bufferSize >= 24 )
    {
        int valCount = 0;
        getBytesFromString(dataA, buffer, bufferSize, valCount, 6, 2);//16 bit, 6 entries min
        if( valCount != 6 )
        {
            CTILOG_ERROR(dout, "Collection improperly set up");
        }

        //offset by 12 as we must have 6 entries in previous table, 6 in this one.
        getBytesFromString(dataB, buffer+12, bufferSize-12, valCount, 6, 2);
        if( valCount != 6 )
        {
            CTILOG_ERROR(dout, "Collection improperly set up");
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
                    OutMessage->Buffer.BSt.IO          = EmetconProtocol::IO_Function_Write;
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
                    OutMessage->Buffer.BSt.IO         = EmetconProtocol::IO_Function_Write;
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
                nRet = ClientErrors::ConfigNotCurrent;
            }
        }
        else
        {
            nRet = ClientErrors::ConfigCurrent;
        }
    }
    return nRet;
}

YukonError_t Mct470Device::decodeGetValueKWH(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CTILOG_DEBUG(dout, "Accumulator Decode for \""<< getName() <<"\"");
    }

    if( InMessage.Sequence == EmetconProtocol::Scan_Accum )
    {
        setScanFlag(ScanRateAccum, false);
    }

    CtiReturnMsg *ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    CtiTime pointTime;
    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    const unsigned char *freeze_counter = 0;

    if( InMessage.Sequence == Cti::Protocols::EmetconProtocol::GetValue_FrozenKWH )
    {
        freeze_counter = DSt->Message + 3;

        string freeze_error;

        if( status = checkFreezeLogic(TimeNow, *freeze_counter, freeze_error) )
        {
            ReturnMsg->setResultString(freeze_error);
        }
        else
        {
            pointTime  = getLastFreezeTimestamp(TimeNow);
            pointTime -= pointTime.seconds() % 60;
        }
    }
    else
    {
        pointTime -= pointTime.seconds() % 300;
    }

    if( !status )
    {
        std::bitset<ChannelCount> points;

        for( int i = 0; i < ChannelCount; i++ )
        {
            if( getDevicePointOffsetTypeEqual(i + 1, PulseAccumulatorPointType) )
            {
                points.set(i);
            }
        }

        for( int i = 0; i < ChannelCount; i++ )
        {
            int offset = (i * 3);

            //  if we have a point for this offset OR it's the first index and we have no points defined
            if( points.test(i) || (i == 0 && points.none()) )
            {
                frozen_point_info pi;

                if( InMessage.Sequence == Cti::Protocols::EmetconProtocol::Scan_Accum ||
                    InMessage.Sequence == Cti::Protocols::EmetconProtocol::GetValue_KWH )
                {
                    //  normal KWH read, nothing too special
                    pi = getAccumulatorData(DSt->Message + offset, 3, 0);
                }
                else if( InMessage.Sequence == Cti::Protocols::EmetconProtocol::GetValue_FrozenKWH )
                {
                    //  this is where the action is - frozen decode
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
                }

                string point_name;

                if( i == 0 )  point_name = "KYZ 1";

                insertPointDataReport(PulseAccumulatorPointType, i + 1,
                                      ReturnMsg, pi, point_name, pointTime, 1.0, TAG_POINT_MUST_ARCHIVE);

                //  if the quality's invalid, throw the status to abnormal
                if( pi.quality == InvalidQuality && !status )
                {
                    status = ClientErrors::InvalidData;
                }
            }
        }
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Mct470Device::decodeGetValueDemand(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    int i;
    bool demand_defined = false;
    point_info pi;

    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CTILOG_DEBUG(dout, "Demand Decode for \""<< getName() <<"\"");
    }

    setScanFlag(ScanRateGeneral, false);
    setScanFlag(ScanRateIntegrity, false);

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

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

    pi = Mct4xxDevice::getData(DSt->Message + 9, 2, ValueType_Raw);

    insertPointDataReport(PulseAccumulatorPointType, PointOffset_Accumulator_Powerfail,
                          ReturnMsg, pi, "Blink Counter");

    decrementGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList, getGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection ));

    return status;
}


YukonError_t Mct470Device::decodeGetValueMinMaxDemand(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    int base_offset;
    point_info  pi, pi_time;
    CtiTime     pointTime;

    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    CtiCommandParser parse(InMessage.Return.CommandStr);

    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CTILOG_DEBUG(dout, "Min/Max Demand Decode for \""<< getName() <<"\"");
    }

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    base_offset = parse.getOffset();

    if( base_offset < 1 || base_offset > 4 )
    {
        CTILOG_WARN(dout, "offset = "<< base_offset <<" - resetting to 1");

        base_offset = 1;
    }

    pi      = getData(DSt->Message + 0, 2, ValueType_PulseDemand);
    pi_time = Mct4xxDevice::getData(DSt->Message + 2, 4, ValueType_Raw);

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
    pi_time = Mct4xxDevice::getData(DSt->Message + 8, 4, ValueType_Raw);

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

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


void Mct470Device::reportPointData(const CtiPointType_t pointType, const int pointOffset, CtiReturnMsg &ReturnMsg, const INMESS &InMessage, const point_info &pi, const string pointName)
{
    insertPointDataReport(pointType, pointOffset, &ReturnMsg, pi, pointName);

    if( pi.quality == InvalidQuality )
    {
        insertPointFail(InMessage, &ReturnMsg, ScanRateGeneral, pointOffset, pointType);
    }
}


YukonError_t Mct470Device::decodeGetValueIED(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    int        frozen = 0,
               offset = 0,
               rate   = 0;
    point_info pi;
    string     point_string, resultString;

    CtiCommandParser parse(InMessage.Return.CommandStr);

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData = NULL;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CTILOG_DEBUG(dout, "IED Decode for \"" << getName() << "\"");
    }

    setScanFlag(ScanRateGeneral, false);
    setScanFlag(ScanRateIntegrity, false);

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    bool dataInvalid = true;
    int  ied_data_end;

    if( parse.getFlags() & CMD_FLAG_GV_DEMAND && !(parse.getFlags() & CMD_FLAG_GV_PEAK) )
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

    //  this must be before CMD_FLAG_GV_DEMAND, since we are looking for peak demand
    if( parse.getFlags() & CMD_FLAG_GV_PEAK )
    {
        if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) )
        {
            resultString += getName() + " / IED peak kW/kM reads require SSPEC rev 4.2 or higher; execute \"getconfig model\" to verify";
            status = ClientErrors::VerifySSPEC;
        }
        else if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) < SspecRev_IED_Precanned11 )
        {
            resultString += getName() + " / IED peak kW/kM reads require SSPEC rev 4.2 or higher; MCT reports " + CtiNumStr(getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) / 10.0, 1);
            status = ClientErrors::InvalidSSPEC;
        }
        else if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) )
        {
            resultString += "Did not retrieve the IED type";
            status = ClientErrors::NoConfigData;
        }
        else if( !isPrecannedTableCurrent() )
        {
            resultString += "Did not retrieve the precanned table type";
            status = ClientErrors::NoConfigData;
        }
        else if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_PrecannedTableType) != 11 )
        {
            resultString += getName() + " / IED peak kW/kM reads require Precanned Table 11; MCT reports " + CtiNumStr(getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_PrecannedTableType));
            status = ClientErrors::NoMethod;
        }
        else if( (getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) >> 4) != IED_Type_LG_S4 )
        {
            resultString += getName() + " / IED peak kW/kM reads require an S4; MCT reports " + resolveIEDName(getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) >> 4);
            status = ClientErrors::NoMethod;
        }
        else
        {
            if( dataInvalid )
            {
                //  If we are here, we believe the data is incorrect!
                resultString += "Device: " + getName() + "\nData buffer is bad, retry command" ;
                status = ClientErrors::IedBufferBad;

                if( parse.getCommandStr().find(" kva") != string::npos )
                {
                    insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_PeakKM,  AnalogPointType);
                    insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_TotalKMH, AnalogPointType);
                }
                else
                {
                    insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_PeakKW,  AnalogPointType);
                    insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_TotalKWH, AnalogPointType);
                }
            }
            else
            {
                unsigned demand_offset, consumption_offset;
                string   demand_name,   consumption_name;

                if( parse.getCommandStr().find(" kva") != string::npos )
                {
                    demand_offset      = PointOffset_PeakKM;
                    demand_name        = "Peak kM";
                    consumption_offset = PointOffset_TotalKMH;
                    consumption_name   = "kMh total";
                }
                else
                {
                    demand_offset      = PointOffset_PeakKW;
                    demand_name        = "Peak kW";
                    consumption_offset = PointOffset_TotalKWH;
                    consumption_name   = "kWh total";
                }

                status = decodeGetValueIEDPrecannedTable11Peak(parse, *DSt, TimeNow, demand_offset, demand_name, consumption_offset, consumption_name, ReturnMsg);
            }
        }
    }
    else if( parse.getFlags() & CMD_FLAG_GV_DEMAND )
    {
        bool has_volts = true;

        if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) )
        {
            switch( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) >> 4 )
            {
                case IED_Type_LG_S4:
                case IED_Type_Alpha_A3:
                case IED_Type_GE_kV:
                case IED_Type_GE_kV2:
                case IED_Type_GE_kV2c:
                case IED_Type_Sentinel:
                {
                    has_volts = true;
                    break;
                }

                default:
                {
                    CTILOG_ERROR(dout, "device \""<< getName() <<"\" is reporting an invalid IED type (" << (getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) >> 4) << ") for getvalue ied demand");
                }
                case IED_Type_Alpha_PP:
                {
                    has_volts = false;
                    break;
                }
            }
        }
        else
        {
            has_volts = getDevicePointOffsetTypeEqual(PointOffset_VoltsPhaseA, AnalogPointType) ||
                        getDevicePointOffsetTypeEqual(PointOffset_VoltsPhaseB, AnalogPointType) ||
                        getDevicePointOffsetTypeEqual(PointOffset_VoltsPhaseC, AnalogPointType);
        }

        if( dataInvalid )
        {
            //  If we are here, we believe the data is incorrect!
            resultString += "Device: " + getName() + "\nData buffer is bad, retry command" ;
            status = ClientErrors::IedBufferBad;

            insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_TotalKW,     AnalogPointType);
            insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_TotalKM,     AnalogPointType);

            if( has_volts )
            {
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_VoltsPhaseA, AnalogPointType);
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_VoltsPhaseB, AnalogPointType);
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_VoltsPhaseC, AnalogPointType);
            }
            else
            {
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_OutageCount, AnalogPointType);
            }
        }
        else
        {
            //  get demand
            pi = getData(DSt->Message, 3, ValueType_IED);

            reportPointData(AnalogPointType, PointOffset_TotalKW, *ReturnMsg, InMessage, pi, "current kW");

            //  get selectable metric (kM, kVAR, etc)
            pi = getData(DSt->Message + 3, 3, ValueType_IED);

            reportPointData(AnalogPointType, PointOffset_TotalKM, *ReturnMsg, InMessage, pi, "current kM");

            if( has_volts )
            {
                pi = getData(DSt->Message + 6, 2, ValueType_IED);
                pi.value /= 100.0;

                reportPointData(AnalogPointType, PointOffset_VoltsPhaseA, *ReturnMsg, InMessage, pi, "Phase A Volts");

                pi = getData(DSt->Message + 8, 2, ValueType_IED);
                pi.value /= 100.0;

                reportPointData(AnalogPointType, PointOffset_VoltsPhaseB, *ReturnMsg, InMessage, pi, "Phase B Volts");

                pi = getData(DSt->Message + 10, 2, ValueType_IED);
                pi.value /= 100.0;

                reportPointData(AnalogPointType, PointOffset_VoltsPhaseC, *ReturnMsg, InMessage, pi, "Phase C Volts");
            }
            else
            {
                pi = getData(DSt->Message + 6, 2, ValueType_IED);

                reportPointData(AnalogPointType, PointOffset_OutageCount, *ReturnMsg, InMessage, pi, "Outage Count");
            }
        }
    }
    else if( parse.getFlags() & CMD_FLAG_GV_RATET )
    {
        if( dataInvalid )
        {
            //If we are here, we believe the data is incorrect!
            resultString += "Device: " + getName() + "\nData buffer is bad, retry command" ;
            status = ClientErrors::IedBufferBad;

            insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_TotalKWH, AnalogPointType);
            insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_TotalKMH, AnalogPointType);
        }
        else
        {
            pi = getData(DSt->Message, 5, ValueType_IED);

            insertPointDataReport(AnalogPointType, PointOffset_TotalKWH,
                                  ReturnMsg, pi, "kWh total", CtiTime(), 1.0, TAG_POINT_MUST_ARCHIVE);

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

                    pi = Mct4xxDevice::getData(DSt->Message + byte*2, 2, ValueType_Raw);

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
                    int pointoffset = PointOffset_DNPCounter_Precanned1+byte+(i-1)*6;

                    string pointname;
                    pointname  = "Pulse Accumulator point ";
                    pointname += CtiNumStr(pointoffset);

                    pi = Mct4xxDevice::getData(DSt->Message + byte*2, 2, ValueType_Raw);

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
            setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_RealTime1CRC, InMessage.Buffer.DSt.Message[0]);
            resultString += "CRCs Returned: " + CtiNumStr(InMessage.Buffer.DSt.Message[0]);

            setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_RealTime2CRC, InMessage.Buffer.DSt.Message[1]);
            resultString += ", " + CtiNumStr(InMessage.Buffer.DSt.Message[1]);

            setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_BinaryCRC, InMessage.Buffer.DSt.Message[2]);
            resultString += ", " + CtiNumStr(InMessage.Buffer.DSt.Message[2]);

            setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC1, InMessage.Buffer.DSt.Message[3]);
            resultString += ", " + CtiNumStr(InMessage.Buffer.DSt.Message[3]);

            setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC2, InMessage.Buffer.DSt.Message[4]);
            resultString += ", " + CtiNumStr(InMessage.Buffer.DSt.Message[4]);

            setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC3, InMessage.Buffer.DSt.Message[5]);
            resultString += ", " + CtiNumStr(InMessage.Buffer.DSt.Message[5]);

            setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC4, InMessage.Buffer.DSt.Message[6]);
            resultString += ", " + CtiNumStr(InMessage.Buffer.DSt.Message[6]);

            setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AnalogCRC5, InMessage.Buffer.DSt.Message[7]);
            resultString += ", " + CtiNumStr(InMessage.Buffer.DSt.Message[7]);

            setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC1, InMessage.Buffer.DSt.Message[8]);
            resultString += ", " + CtiNumStr(InMessage.Buffer.DSt.Message[8]);

            setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC2, InMessage.Buffer.DSt.Message[9]);
            resultString += ", " + CtiNumStr(InMessage.Buffer.DSt.Message[9]);

            setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC3, InMessage.Buffer.DSt.Message[10]);
            resultString += ", " + CtiNumStr(InMessage.Buffer.DSt.Message[10]);

            setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DNP_AccumulatorCRC4, InMessage.Buffer.DSt.Message[11]);
            resultString += ", " + CtiNumStr(InMessage.Buffer.DSt.Message[11]);
        }
    }
    else
    {
        if( dataInvalid )
        {
            //If we are here, we believe the data is incorrect!
            resultString += "Device: " + getName() + "\nData buffer is bad, retry command" ;
            status = ClientErrors::IedBufferBad;

            if( parse.getFlags() & CMD_FLAG_GV_KVARH || parse.getFlags() & CMD_FLAG_GV_KVAH  )
            {
                offset = PointOffset_TOU_KMBase;
            }
            else
            {
                offset = PointOffset_TOU_KWBase;
            }

            if(      parse.getFlags() & CMD_FLAG_GV_RATEA )   rate = 0;
            else if( parse.getFlags() & CMD_FLAG_GV_RATEB )   rate = 1;
            else if( parse.getFlags() & CMD_FLAG_GV_RATEC )   rate = 2;
            else if( parse.getFlags() & CMD_FLAG_GV_RATED )   rate = 3;

            insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, (offset + rate * 2 + 1), AnalogPointType);
            insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, (offset + rate * 2), AnalogPointType);
            if( parse.getFlags() & CMD_FLAG_FROZEN && (offset + rate * 2) == PointOffset_TOU_KWBase ) //Currently we only support frozen rate A
            {
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, (PointOffset_TOU_KWBase + 1 + PointOffset_FrozenPointOffset), AnalogPointType);
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, (PointOffset_TOU_KWBase +     PointOffset_FrozenPointOffset), AnalogPointType);
            }
        }
        else if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) )
        {
            resultString += getName() + " / Did not retrieve SSPEC revision";
            status = ClientErrors::VerifySSPEC;
        }
        else if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) )
        {
            resultString += "Did not retrieve the IED type";
            status = ClientErrors::NoConfigData;
        }
        else if( !isPrecannedTableCurrent() )
        {
            resultString += "Did not retrieve the precanned table type";
            status = ClientErrors::NoConfigData;
        }
        else if( (getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) >= SspecRev_IED_Precanned11)
                 && (getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_PrecannedTableType) == 11)
                 && ((getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) >> 4) == IED_Type_LG_S4) )
        {
            if( parse.getFlags() & CMD_FLAG_GV_RATED )
            {
                status = ClientErrors::NoMethod;
                resultString = "Rate D not supported for Precanned Table 11";
            }
            else
            {
                if(      parse.getFlags() & CMD_FLAG_GV_RATEA )  rate = 0;
                else if( parse.getFlags() & CMD_FLAG_GV_RATEB )  rate = 1;
                else if( parse.getFlags() & CMD_FLAG_GV_RATEC )  rate = 2;

                unsigned demand_offset, consumption_offset;
                string   demand_name,   consumption_name;

                if( parse.getFlags() & CMD_FLAG_GV_KVARH || parse.getFlags() & CMD_FLAG_GV_KVAH  )
                {
                    demand_offset      = PointOffset_TOU_KMBase;
                    demand_name        = "kM rate ";
                    consumption_offset = PointOffset_TOU_KMBase + 1;
                    consumption_name   = "kMh rate ";
                }
                else
                {
                    demand_offset      = PointOffset_TOU_KWBase;
                    demand_name        = "kW rate ";
                    consumption_offset = PointOffset_TOU_KWBase + 1;
                    consumption_name   = "kWh rate ";
                }

                consumption_name += string(1, (char)('A' + rate));
                consumption_name += " total";
                consumption_offset += rate * 2;

                demand_name += string(1, (char)('A' + rate));
                demand_name += " peak";
                demand_offset += rate * 2;

                status = decodeGetValueIEDPrecannedTable11Peak(parse, *DSt, TimeNow, demand_offset, demand_name, consumption_offset, consumption_name, ReturnMsg);
            }
        }
        else
        {
            point_info time_info;
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

            pi = Mct470Device::getData(DSt->Message, 5, ValueType_IED);

            pointname += string(1, (char)('A' + rate));
            pointname += " total";

            unsigned int pointOffset = offset + rate * 2 + 1;

            insertPointDataReport(AnalogPointType, pointOffset,
                                  ReturnMsg, pi, pointname, CtiTime(), 1.0, tags);
            if( parse.getFlags() & CMD_FLAG_FROZEN && pointOffset == PointOffset_TOU_KWBase + 1 ) //Currently we only support frozen rate A
            {
                insertPointDataReport(AnalogPointType, PointOffset_TOU_KWBase + 1 + PointOffset_FrozenPointOffset,
                                  ReturnMsg, pi, "Frozen " + pointname, CtiTime(), 1.0, tags);
            }

            pi        = getData(DSt->Message + 5, 3, ValueType_IED);
            time_info = Mct4xxDevice::getData(DSt->Message + 8, 4, ValueType_Raw);

            CtiTime peak_time = CtiTime::fromLocalSeconds(time_info.value);

            pointname  = "kW rate ";
            pointname += string(1, (char)('A' + rate));
            pointname += " peak";

            if( !is_valid_time(peak_time) )
            {
                pi.quality = InvalidQuality;
                pi.description = "Bad peak kW timestamp";

                CTILOG_ERROR(dout, "invalid time ("<< std::hex << peak_time <<") in IED peak decode for device \""<< getName() <<"\"");
            }
            if( (peak_time.seconds() >= (DawnOfTime_UtcSeconds - 86400) && peak_time.seconds() <= (DawnOfTime_UtcSeconds + 86400) ) ||
                 (peak_time.seconds() <= 86400) )
            {
                // Don't insert a point, just send the message.
                const string noPeak = ": " + CtiNumStr(pi.value) + " [No peak occurred]\n";
                pointOffset = offset + rate * 2;
                CtiPointSPtr p;
                if( p = getDevicePointOffsetTypeEqual(pointOffset, AnalogPointType) )
                {
                    pointname = p->getName();
                }
                resultString += "\n" + getName() + " / " + pointname + noPeak;
                if( parse.getFlags() & CMD_FLAG_FROZEN && pointOffset == PointOffset_TOU_KWBase ) //Currently we only support frozen rate A
                {
                    if( p = getDevicePointOffsetTypeEqual(PointOffset_TOU_KWBase + PointOffset_FrozenPointOffset, AnalogPointType) )
                    {
                        pointname = p->getName();
                    }
                    else
                    {
                        pointname = "Frozen " + pointname;
                    }
                    resultString += "\n" + getName() + " / " + pointname + noPeak;
                }
            }
            else
            {
                pointOffset = offset + rate * 2;
                insertPointDataReport(AnalogPointType, pointOffset,
                                      ReturnMsg, pi, pointname, peak_time);
                if( parse.getFlags() & CMD_FLAG_FROZEN && pointOffset == PointOffset_TOU_KWBase ) //Currently we only support frozen rate A
                {
                    insertPointDataReport(AnalogPointType, PointOffset_TOU_KWBase + PointOffset_FrozenPointOffset,
                                      ReturnMsg, pi, "Frozen " + pointname, peak_time);
                }
            }
        }
    }

    ReturnMsg->setResultString(ReturnMsg->ResultString() + resultString);
    decrementGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList, getGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection) );

    return status;
}


YukonError_t Mct470Device::decodeGetValueIEDPrecannedTable11Peak(const CtiCommandParser &parse, const DSTRUCT &DSt, const CtiTime &TimeNow, const unsigned demand_offset, const string &demand_name, const unsigned consumption_offset, const string &consumption_name, CtiReturnMsg *ReturnMsg)
{
    YukonError_t status = ClientErrors::None;

    {
        point_info consumption_value, consumption_time_info;

        consumption_value     = getData(DSt.Message + 0, 5, ValueType_IED);
        consumption_time_info = Mct4xxDevice::getData(DSt.Message + 10, 3, ValueType_Raw);

        const unsigned long consumption_timestamp = convertTimestamp(consumption_time_info.value);

        if( !is_valid_time(consumption_timestamp) )
        {
            CTILOG_ERROR(dout, "invalid consumption timestamp ("<< std::hex << consumption_timestamp <<") in IED peak decode for device \""<< getName() <<"\"");

            consumption_value.quality = InvalidQuality;
            consumption_value.description = "Bad consumption timestamp";

            status = ClientErrors::Abnormal;
        }

        insertPointDataReport(AnalogPointType, consumption_offset, ReturnMsg, consumption_value, consumption_name, consumption_timestamp, 1.0, TAG_POINT_MUST_ARCHIVE);
        if( parse.getFlags() & CMD_FLAG_FROZEN && consumption_offset == (PointOffset_TOU_KWBase + 1) ) //Currently we only support frozen rate A
        {
            insertPointDataReport(AnalogPointType, PointOffset_TOU_KWBase + 1 + PointOffset_FrozenPointOffset, ReturnMsg, consumption_value, "Frozen " + consumption_name, consumption_timestamp, 1.0, TAG_POINT_MUST_ARCHIVE);
        }
    }

    {
        point_info demand_value, demand_time_info;

        demand_value     = getData(DSt.Message + 5, 2, ValueType_IED);
        demand_time_info = Mct4xxDevice::getData(DSt.Message +  7, 3, ValueType_Raw);

        const unsigned long demand_timestamp = convertTimestamp(demand_time_info.value);

        if( !is_valid_time(demand_timestamp) )
        {
            CTILOG_ERROR(dout, "invalid peak demand timestamp ("<< std::hex << demand_timestamp <<") in IED peak decode for device \"" << getName() << "\"");

            demand_value.quality = InvalidQuality;
            demand_value.description = "Bad peak demand timestamp";

            status = ClientErrors::Abnormal;
        }

        insertPointDataReport(AnalogPointType, demand_offset, ReturnMsg, demand_value, demand_name, demand_timestamp);
        if( parse.getFlags() & CMD_FLAG_FROZEN && demand_offset == PointOffset_TOU_KWBase ) //Currently we only support frozen rate A
        {
            insertPointDataReport(AnalogPointType, PointOffset_TOU_KWBase + PointOffset_FrozenPointOffset, ReturnMsg, demand_value, "Frozen " + demand_name, demand_timestamp);
        }
    }

    return status;
}


unsigned long Mct470Device::convertTimestamp(const unsigned long timestamp, const CtiDate &current_date) const
{
    CtiTime mct_year_end;

    const bool our_year = current_date.year() % 2;
    const bool mct_year = timestamp & 0x800000;

    if( mct_year == our_year )
    {
        mct_year_end = CtiDate(1, 1, current_date.year() + 1);
    }
    else
    {
        if( current_date.month() > 6 )
        {
            //  the year doesn't match, and it's the second half of the year, we assume the MCT is in next year
            mct_year_end = CtiDate(1, 1, current_date.year() + 2);
        }
        else
        {
            //  the year doesn't match, and it's the first half of the year, we assume the MCT is in last year
            mct_year_end = CtiDate(1, 1, current_date.year());
        }
    }

    //  we want to move from our local midnight to GMT midnight, so we subtract the difference between us and GMT
    //  example:  1/1/2009 06:00 - 1/1/2009 00:00 = 06:00;  1/1/2009 00:00 - 06:00 = 12/31/2008 18:00
    mct_year_end = CtiTime(mct_year_end.getLocalTimeSeconds());

    const int minutes_until_end_of_year = timestamp & 0x007fffff;

    if( minutes_until_end_of_year > 0x007fff00 )
    {
        CTILOG_ERROR(dout, "invalid time ("<< std::hex << minutes_until_end_of_year <<") in Mct470Device::convertTimestamp() for device \""<< getName() <<"\"");

        return 0;
    }

    return mct_year_end.addMinutes(-minutes_until_end_of_year).seconds();
}


YukonError_t Mct470Device::decodeGetConfigIED(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    point_info     pi;
    string         resultString;

    CtiCommandParser parse(InMessage.Return.CommandStr);

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pData     = NULL;

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    switch( InMessage.Sequence )
    {
        case EmetconProtocol::GetConfig_IEDTime:
        {
            bool dstEnabled = true;

            if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) )
            {
                dstEnabled = getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) & 0x01;
            }
            else if( const DeviceConfigSPtr deviceConfig = getDeviceConfig() )
            {
                if ( const boost::optional<long> configuration = deviceConfig->findValue<long>(MCTStrings::EnableDst) )
                {
                    dstEnabled = *configuration & 0x01;
                }
            }

            point_info  pi_time  = Mct470Device::getData(DSt->Message, 4, ValueType_IED);

            _iedTime = dstEnabled ?
                CtiTime::fromLocalSeconds(pi_time.value) :
                CtiTime::fromLocalSecondsNoDst(pi_time.value);

            resultString += getName() + " / current time: ";
            resultString += dstEnabled ?
                _iedTime.asString(CtiTime::Local,      CtiTime::IncludeTimezone) :
                _iedTime.asString(CtiTime::LocalNoDst, CtiTime::IncludeTimezone);
            resultString += "\n";

            if( !hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) )
            {
                resultString += getName() + " / configuration byte not read, cannot decode IED TOU rate\n";
            }
            else
            {
                point_info  pi_rate  = Mct470Device::getData(DSt->Message + 4, 1, ValueType_IED);

                int rate = pi_rate.value;

                if( pi_rate.quality == InvalidQuality )
                {
                    resultString += getName() + " / current TOU rate: (invalid data)\n";
                }
                else
                {
                    switch( (getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) & 0xf0) >> 4 )
                    {
                        case IED_Type_LG_S4:
                        {
                            rate = rate & 0x0f;

                            resultString += getName() + " / current TOU rate: " + string(1, 'A' + rate) + "\n";

                            break;
                        }

                        case IED_Type_Alpha_A3:
                        {
                            rate = rate & 0x07;

                            resultString += getName() + " / current TOU rate: " + string(1, 'A' + rate) + "\n";

                            break;
                        }

                        case IED_Type_Alpha_PP:
                        {
                            rate = (rate >> 2) & 0x03;

                            resultString += getName() + " / current TOU rate: " + string(1, 'A' + rate) + "\n";

                            break;
                        }

                        case IED_Type_GE_kV2c:
                        {
                            rate = rate & 0x07;

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

            pi = Mct470Device::getData(DSt->Message + 5, 2, ValueType_IED);

            pi_time  = Mct470Device::getData(DSt->Message + 7, 4, ValueType_IED);

            CtiTime lastReset = dstEnabled ?
                CtiTime::fromLocalSeconds(pi_time.value) :
                CtiTime::fromLocalSecondsNoDst(pi_time.value);

            insertPointDataReport(AnalogPointType, PointOffset_DemandResetCount,
                                  ReturnMsg, pi, "Demand Reset Count", lastReset);

            resultString += "\n" + getName() + " / time of last reset: ";
            resultString += dstEnabled ?
                lastReset.asString(CtiTime::Local,      CtiTime::IncludeTimezone) :
                lastReset.asString(CtiTime::LocalNoDst, CtiTime::IncludeTimezone);
            resultString += "\n";

            pi = Mct470Device::getData(DSt->Message + 11, 2, ValueType_IED);

            insertPointDataReport(AnalogPointType, PointOffset_OutageCount,
                                  ReturnMsg, pi, "Outage Count");

            if( pi.quality == InvalidQuality )
            {
                insertPointFail(InMessage, ReturnMsg, ScanRateGeneral, PointOffset_OutageCount, AnalogPointType);
            }

            break;
        }

        case EmetconProtocol::GetConfig_IEDDNP:
        {
            int mctPoint = DSt->Message[0];
            for( int i = 0; i < 6; i++ )
            {
                pi = Mct4xxDevice::getData(DSt->Message + 1 + 2*i, 2, ValueType_Raw);
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

        case EmetconProtocol::GetConfig_IEDScan:
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

    decrementGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList, getGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection) );

    return status;
}


YukonError_t Mct470Device::decodeGetStatusInternal( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const unsigned char *geneBuf = InMessage.Buffer.DSt.Message;

    string resultString;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    resultString += getName() + " / Internal Status:\n";

    //  Point offset 10
    resultString += (InMessage.Buffer.DSt.Message[1] & 0x01)?"Power fail occurred\n":"";
    resultString += (InMessage.Buffer.DSt.Message[1] & 0x02)?"Electronic meter communication error\n":"";
    resultString += (InMessage.Buffer.DSt.Message[1] & 0x04)?"Stack overflow\n":"";
    resultString += (InMessage.Buffer.DSt.Message[1] & 0x08)?"Power fail carryover\n":"";
    resultString += (InMessage.Buffer.DSt.Message[1] & 0x10)?"RTC adjusted\n":"";
    resultString += (InMessage.Buffer.DSt.Message[1] & 0x20)?"Holiday Event occurred\n":"";
    resultString += (InMessage.Buffer.DSt.Message[1] & 0x40)?"DST Change occurred\n":"";
    resultString += (InMessage.Buffer.DSt.Message[1] & 0x80)?"Negative time sync occurred\n":"";

    //  Point offset 20
    resultString += (InMessage.Buffer.DSt.Message[2] & 0x01)?"Zero usage on channel 1 for 24 hours\n":"";
    resultString += (InMessage.Buffer.DSt.Message[2] & 0x02)?"Zero usage on channel 2 for 24 hours\n":"";
    resultString += (InMessage.Buffer.DSt.Message[2] & 0x04)?"Zero usage on channel 3 for 24 hours\n":"";
    resultString += (InMessage.Buffer.DSt.Message[2] & 0x08)?"Zero usage on channel 4 for 24 hours\n":"";
    resultString += (InMessage.Buffer.DSt.Message[2] & 0x10)?"Address corruption\n":"";
    //  0x20-0x80 aren't used yet

    const bool touDisabled = InMessage.Buffer.DSt.Message[0] & 0x10;

    //  Point offset 30
    resultString += (InMessage.Buffer.DSt.Message[0] & 0x01)?"Group addressing disabled\n":"Group addressing enabled\n";
    //  0x02 is not used yet
    resultString += (InMessage.Buffer.DSt.Message[0] & 0x04)?"DST active\n":"DST inactive\n";
    resultString += (InMessage.Buffer.DSt.Message[0] & 0x08)?"Holiday active\n":"";
    resultString += (touDisabled)?"TOU disabled\n":"TOU enabled\n";
    resultString += (InMessage.Buffer.DSt.Message[0] & 0x20)?"Time sync needed\n":"In time sync\n";
    resultString += (InMessage.Buffer.DSt.Message[0] & 0x40)?"Critical peak active\n":"Critical peak inactive\n";
    //  0x80 is used internally

    setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TouEnabled, ! touDisabled);

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
                double value = (InMessage.Buffer.DSt.Message[i] >> j) & 0x01;

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


YukonError_t Mct470Device::decodeGetStatusLoadProfile( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const unsigned char *geneBuf = InMessage.Buffer.DSt.Message;
    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    CtiCommandParser parse(InMessage.Return.CommandStr);

    int lp_channel = (parse.getiValue("loadprofile_offset") < 3)?1:3;

    string resultString;
    unsigned long lpTime;

    CtiReturnMsg     *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg  *pData = NULL;

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    resultString += getName() + " / Load Profile Channel " + CtiNumStr(lp_channel) + " Status:\n";

    lpTime = DSt->Message[0] << 24 |
             DSt->Message[1] << 16 |
             DSt->Message[2] <<  8 |
             DSt->Message[3];

    resultString += "Long Load Profile Interval Time: " + (lpTime ? printTimestamp(lpTime) : "(none)") + "\n";
    resultString += "Current Interval Pointer: " + CtiNumStr(DSt->Message[4]) + string("\n");

    resultString += "\n";

    resultString += getName() + " / Load Profile Channel " + CtiNumStr(lp_channel + 1) + string(" Status:\n");

    lpTime = DSt->Message[5] << 24 |
             DSt->Message[6] << 16 |
             DSt->Message[7] <<  8 |
             DSt->Message[8];

    resultString += "Long Load Profile Interval Time: " + (lpTime ? printTimestamp(lpTime) : "(none)") + "\n";
    resultString += "Current Interval Pointer: " + CtiNumStr(DSt->Message[9]) + string("\n");

    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Mct470Device::decodeGetStatusDNP( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const unsigned char *geneBuf = InMessage.Buffer.DSt.Message;
    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    CtiCommandParser parse(InMessage.Return.CommandStr);

    std::string resultString;
    CtiTime errTime;

    CtiReturnMsg     *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg  *pData = NULL;

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

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
    resultString += "Last Successful Read: " + printTimestamp(ied_time) + "(" + CtiNumStr(ied_time) + ")" + "\n";

    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

YukonError_t Mct470Device::decodeGetStatusFreeze( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
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

     ReturnMsg->setResultString(resultString);

     retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );


     return status;
}

YukonError_t Mct470Device::decodeGetConfigIntervals(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

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
    resultString += getName() + " / Load Profile Interval 2: " + CtiNumStr(DSt->Message[2]) + " minutes\n";

    resultString += getName() + " / Table Read Interval: " + CtiNumStr(DSt->Message[3] * 15) + " seconds\n";

    resultString += getName() + " / Precanned Meter Number: " + CtiNumStr(DSt->Message[4]) + "\n";
    resultString += getName() + " / Precanned Table Type: "   + CtiNumStr(DSt->Message[5]) + "\n";

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Mct470Device::decodeGetConfigIedDnpAddress(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    string resultString;

    unsigned long master     = DSt->Message[0] << 8 | DSt->Message[1];
    unsigned long outstation = DSt->Message[2] << 8 | DSt->Message[3];

    resultString += getName() + " / IED DNP addresses:\n";

    resultString += getName() + " / IED DNP master address: " + CtiNumStr(master) + " (" + CtiNumStr(master).xhex(4) + ")\n";
    resultString += getName() + " / IED DNP outstation address: " + CtiNumStr(outstation) + " (" + CtiNumStr(outstation).xhex(4) + ")\n";

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


string Mct470Device::describeChannel(unsigned char channel_config) const
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
                if( hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) )
                {
                    result_string += resolveIEDName(getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration) >> 4) + " ";
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
            long lp_interval;

            //  TODO:  add support for LP interval 2
            if( getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LoadProfileInterval, lp_interval) && (lp_interval <= 60) )
            {
                result_string += ", " + CtiNumStr(lp_interval) + " minute interval";
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


YukonError_t Mct470Device::decodeGetConfigChannelSetup(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    const auto &response = InMessage.Buffer.DSt.Message;

    string result_string;

    result_string += getName() + " / Load Profile Interval 1: " + CtiNumStr(response[4]) + " minutes\n";
    result_string += getName() + " / Load Profile Interval 2: " + CtiNumStr(response[5]) + " minutes\n";
    result_string += getName() + " / Electronic Meter Load Profile Interval: " + CtiNumStr(response[6]) + " minutes\n";

    for( int i = 0; i < ChannelCount; i++ )
    {
        result_string += getName() + " / LP Channel " + CtiNumStr(i+1) + " config: ";

        result_string += describeChannel(response[i]) + "\n";
    }

    auto ReturnMsg = std::make_unique<CtiReturnMsg>(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(result_string);

    retMsgHandler(InMessage.Return.CommandStr, ClientErrors::None, ReturnMsg.release(), vgList, retList);

    //  If this was originally a load profile command, resume the original command now that we have the channel info
    if( boost::algorithm::istarts_with(InMessage.Return.CommandStr, "getvalue lp ") )
    {
        resumeAfterAuxiliaryRequest(InMessage, outList, retList, vgList);
    }

    return ClientErrors::None;
}


YukonError_t Mct470Device::decodeGetConfigModel(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    const auto& response = InMessage.Buffer.DSt.Message;

    string sspec;
    string options;
    int ssp, rev;

    ssp = response[0];
    ssp |= response[4] << 8;

    rev = (unsigned)response[1];

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

    options += "Connected Meter: " + resolveIEDName(response[3] >> 4) + "\n";

    if( InMessage.Buffer.DSt.Message[3] & 0x04 )
    {
        options += "Multiple electronic meters attached\n";
    }

    options += "DST ";
    options += (response[3] & 0x01) ? "enabled\n" : "disabled\n";

    auto ReturnMsg = std::make_unique<CtiReturnMsg>(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString( sspec + options );

    decrementGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection);
    retMsgHandler(InMessage.Return.CommandStr, ClientErrors::None, ReturnMsg.release(), vgList, retList);

    return ClientErrors::None;
}

YukonError_t Mct470Device::decodeGetConfigMultiplier(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    CtiCommandParser parse(InMessage.Return.CommandStr);

    string descriptor;

    const DSTRUCT &DSt   = InMessage.Buffer.DSt;

    CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

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

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(descriptor);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

//I hate to do this, we must ensure no one ever passes a null buffer into here...
void Mct470Device::decodeDNPRealTimeRead(const BYTE *buffer, int readNumber, string &resultString, CtiReturnMsg *ReturnMsg, const INMESS &InMessage)
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

        pi = Mct4xxDevice::getData(buffer, 1, ValueType_Raw);//Gets pi built up properly...
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

        int i;
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

                pi = Mct4xxDevice::getData(buffer + 2 * (i + 1), 2, ValueType_Raw);

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

                pi = Mct4xxDevice::getData(buffer + 2 * i + 8, 2, ValueType_Raw);

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


YukonError_t Mct470Device::decodeGetValuePhaseCurrent(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT &DSt   = InMessage.Buffer.DSt;

    CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    point_info  pi;
    pi = getData(DSt.Message, 2, ValueType_IED);
    //  current is reported back from MCT in tens of mA.
    //  converting to Amps.
    pi.value /= 100;
    insertPointDataReport(AnalogPointType, PointOffset_CurrentNeutral,
                          ReturnMsg, pi, "Neutral Current");

    pi = getData(DSt.Message + 2, 2, ValueType_IED);
    //  current is reported back from MCT in tens of mA.
    //  converting to Amps.
    pi.value /= 100;
    insertPointDataReport(AnalogPointType, PointOffset_CurrentPhaseA,
                          ReturnMsg, pi, "Phase A Current");

    pi = getData(DSt.Message + 4, 2, ValueType_IED);
    //  current is reported back from MCT in tens of mA.
    //  converting to Amps.
    pi.value /= 100;
    insertPointDataReport(AnalogPointType, PointOffset_CurrentPhaseB,
                          ReturnMsg, pi, "Phase B Current");

    pi = getData(DSt.Message + 6, 2, ValueType_IED);
    //  current is reported back from MCT in tens of mA.
    //  converting to Amps.
    pi.value /= 100;
    insertPointDataReport(AnalogPointType, PointOffset_CurrentPhaseC,
                          ReturnMsg, pi, "Phase C Current");

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

//This expects a string in the format: "01 0x01 01 020 055 0x0040" ect..
void Mct470Device::getBytesFromString(string &values, BYTE* buffer, int buffLen, int &numValues, int fillCount, int bytesPerValue)
{
    std::string   valueCopy(values);
    std::string   temp;
    std::string   token;
    int iValue;
    numValues = 0;

    if( buffer != NULL )
    {
        const boost::regex anyNum("(([0-9]+) *|(0x[0-9a-f]+) *)+");

        if(!(token = matchRegex(valueCopy, anyNum)).empty())
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


string Mct470Device::resolveDNPStatus(int status)
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


string Mct470Device::resolveIEDName(int bits)
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


Mct470Device::IED_Types Mct470Device::resolveIEDType(const string &iedType)
{
    IED_Types retVal = IED_Type_None;
    std::string iedString = iedType;
    CtiToLower(iedString);

    if(      iedString == "s4" )        retVal = IED_Type_LG_S4;
    else if( iedString == "alphaa3" )   retVal = IED_Type_Alpha_A3;
    else if( iedString == "alphapp" )   retVal = IED_Type_Alpha_PP;
    else if( iedString == "gekv" )      retVal = IED_Type_GE_kV;
    else if( iedString == "gekv2" )     retVal = IED_Type_GE_kV2;
    else if( iedString == "gekv2c" )    retVal = IED_Type_GE_kV2c;
    else if( iedString == "sentinel" )  retVal = IED_Type_Sentinel;

    return retVal;
}

unsigned char Mct470Device::computeResolutionByte(double lpResolution, double peakKwResolution, double lastIntervalDemandResolution)
{
    bool defaultUsed = false;
    unsigned char resolutionByte;
    unsigned char lpByte;
    unsigned char peakKwByte;
    unsigned char lastIntrvlByte;

    unsigned int resolution = lpResolution * 10;//Doing this to factor out the .1 case

    switch (resolution)
    { //Possibles are 10, 1 and .1 (*10)
        case 100://10Wh
            lpByte = 1;
            break;
        case 10://1wh
            lpByte = 2;
            break;
        case 1://.1wh
            lpByte = 3;
            break;
        default:
            {
                CTILOG_WARN(dout, "Unexpected value of "<< lpResolution <<" for Load Profile Resolution - Using default value of 10Wh for Resolution Byte Calculation");
            }
            defaultUsed = true;
            lpByte = 1;
            break;
    }

    switch ((int)peakKwResolution)
    { //Possibles are 10 and 1

        case 10://10wh
            peakKwByte = 0;
            break;
        case 1://1wh
            peakKwByte = 1;
            break;
        default:
            {
                CTILOG_WARN(dout, "Unexpected value of " << peakKwResolution << " for Peak KW Resolution - Using default value of 10Wh for Resolution Byte Calculation");
            }
            defaultUsed = true;
            peakKwByte = 0;
            break;
    }

    resolution = lastIntervalDemandResolution * 100;//Doing this to factor out the .01 case
    switch (resolution)
    { //Possibles are 10, 1, .1, and .001 (*10)
        case 1000://10Wh
            lastIntrvlByte = 0;
            break;
        case 100://1Wh
            lastIntrvlByte = 1;
            break;
        case 10://.1wh
            lastIntrvlByte = 2;
            break;
        case 1://.01wh
            lastIntrvlByte = 3;
            break;
        default:
            {
                CTILOG_WARN(dout, "Unexpected value of "<< lastIntervalDemandResolution <<" for Last Interval Demand Resolution - Using default value of 10Wh for Resolution Byte Calculation");
            }
            defaultUsed = true;
            lastIntrvlByte = 0;
            break;
    }

    resolutionByte = lpByte & 0x07;
    resolutionByte |= (peakKwByte & 0x01) << 3;
    resolutionByte |= (lastIntrvlByte & 0x07) << 4;

    if (defaultUsed == true)
    {
        CTILOG_INFO(dout, "A default value was used to compute the Resolution Byte, the result is: "<< (int)resolutionByte);
    }

    return resolutionByte;
}

int Mct470Device::setupRatioBytesBasedOnMeterType(int channel, double multiplier, double peakKwResolution, double lastIntervalDemandResolution, double lpResolution, unsigned int &ratio, unsigned int &kRatio)
{
    int nRet = ClientErrors::None;
    int meterType = channel & 0x03;

    switch (meterType)
    {
        case Electronic:
        {
            unsigned char resolutionByte = computeResolutionByte(lpResolution,peakKwResolution,lastIntervalDemandResolution);

            ratio = (unsigned)resolutionByte;
            kRatio = 0;

            break;
        }
        case NotUsed:
        {
            ratio = 0;
            kRatio = 0;
            break;
        }
        case TwoWireKYZ:
        case ThreeWireKYZ:
        {
            if (!computeMultiplierFactors(multiplier, ratio, kRatio))
            {
                CTILOG_WARN(dout, "Configs - Cannot compute multiplier");

                nRet = ClientErrors::NoConfigData;
            }
            break;
        }
    }

    return nRet;
}

}
}


