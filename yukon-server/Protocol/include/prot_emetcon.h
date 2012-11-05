#pragma once

#include "dlldefs.h"
#include "dsm2.h"

namespace Cti       {
namespace Protocols {

class IM_EX_PROT EmetconProtocol  //  note that we do NOT inherit from the Protocol::Interface class - Emetcon doesn't fit the generate/decode paradigm
{
private:
    EmetconProtocol();
    EmetconProtocol(const EmetconProtocol& aRef);
    EmetconProtocol& operator=(const EmetconProtocol& aRef);
    virtual ~EmetconProtocol();

public:

    static unsigned determineDWordCount(unsigned length);
    static int calculateControlInterval(int interval);

    static void buildAWordMessage(OUTMESS *&out_result);
    static void buildBWordMessage(OUTMESS *&out_result, bool double_message = false);

    enum IO_Bits
    {
        IO_Write          = 0,
        IO_Read           = 1,
        IO_Function_Write = 2,
        IO_Function_Read  = 3
    };

    enum Commands
    {
        DLCCmd_Invalid = 0,

        // Scan Commands
        Scan_General,
        Scan_Integrity,
        Scan_Accum,
        Scan_LoadProfile,

        // GetValue Commands
        GetValue_PFCount,
        GetValue_KWH,
        GetValue_FrozenKWH,
        GetValue_Demand,
        GetValue_PeakDemand,
        GetValue_FrozenPeakDemand,
        GetValue_Voltage,
        GetValue_FrozenVoltage,
        GetValue_IEDKwh,
        GetValue_IEDKvarh,
        GetValue_IEDKvah,
        GetValue_IEDDemand,
        GetValue_IED,
        GetValue_LoadProfile,
        GetValue_LoadProfilePeakReport,
        GetValue_Outage,
        GetValue_TOUPeak,
        GetValue_TOUkWh,
        GetValue_FrozenTOUkWh,
        GetValue_FreezeCounter,
        GetValue_DailyRead,
        GetValue_IntervalLast,
        GetValue_Runtime,
        GetValue_Shedtime,
        GetValue_PropCount,
        GetValue_PhaseCurrent,
        GetValue_ControlTime,
        GetValue_TransmitPower,
        GetValue_Temperature,
        GetValue_XfmrHistoricalCT,
        GetValue_DutyCycle,
        GetValue_InstantLineData,
        GetValue_TOUkWhReverse,
        GetValue_FrozenTOUkWhReverse,

        // PutValue Commands
        PutValue_IEDReset,
        PutValue_ResetPFCount,
        PutValue_KYZ,  //  KYZ resets are implemented as a putvalue of 0
        PutValue_KYZ2,
        PutValue_KYZ3,
        PutValue_TOUReset,

        // GetStatus Commands
        GetStatus_Default,
        GetStatus_General,
        GetStatus_Disconnect,
        GetStatus_IEDLink,
        GetStatus_IEDDNP,
        GetStatus_LoadProfile,
        GetStatus_Powerfail,
        GetStatus_Internal,
        GetStatus_External,
        GetStatus_Freeze,

        // PutStatus Commands
        PutStatus_Reset,
        PutStatus_ResetAlarms,
        PutStatus_FreezeOne,
        PutStatus_FreezeTwo,
        PutStatus_FreezeVoltageOne,
        PutStatus_FreezeVoltageTwo,
        PutStatus_ResetOverride,
        PutStatus_PeakOn,
        PutStatus_PeakOff,

        // GetConfig commands
        GetConfig_Default,
        GetConfig_Model,
        GetConfig_Time,
        GetConfig_TSync,
        GetConfig_Multiplier,
        GetConfig_Multiplier2,
        GetConfig_Multiplier3,
        GetConfig_Multiplier4,
        GetConfig_IEDScan,
        GetConfig_IEDPassword,
        GetConfig_IEDTime,
        GetConfig_IEDDNP,
        GetConfig_IEDDNPAddress,
        GetConfig_Role,  //  only for repeaters
        GetConfig_Raw,
        GetConfig_Intervals,
        GetConfig_Thresholds,
        GetConfig_ChannelSetup,
        GetConfig_LoadProfileInterval,
        GetConfig_LongLoadProfile,
        GetConfig_DemandInterval,
        GetConfig_Options,
        GetConfig_GroupAddress,
        GetConfig_UniqueAddress,
        GetConfig_Disconnect,
        GetConfig_TOU,
        GetConfig_TOUPart2,
        GetConfig_Holiday,
        GetConfig_MeterParameters,  //  not ideal - hopefully we can manage the InMessage->Sequence better for very specialized
        GetConfig_Freeze,           //    commands like this one, i don't like this being a big mess of non-general commands
        GetConfig_PhaseDetect,
        GetConfig_PhaseDetectArchive,
        GetConfig_Softspec,
        GetConfig_Addressing,
        GetConfig_DailyReadInterest,
        GetConfig_WaterMeterReadInterval,
        GetConfig_PhaseLossThreshold,
        GetConfig_Holiday1_7,
        GetConfig_Holiday8_14,
        GetConfig_Holiday15_21,
        GetConfig_Holiday22_28,

        // PutConfig commands
        PutConfig_Install,
        PutConfig_GroupAddressInhibit,
        PutConfig_GroupAddressEnable,
        PutConfig_GroupAddress_GoldSilver,
        PutConfig_GroupAddress_Bronze,
        PutConfig_GroupAddress_Lead,
        PutConfig_UniqueAddress,
        PutConfig_IEDScan,
        PutConfig_IEDClass,
        PutConfig_Role,   //  only for repeaters
        PutConfig_Raw,
        PutConfig_TSync,
        PutConfig_LoadProfileInterval,
        PutConfig_DemandInterval,
        PutConfig_Intervals,
        PutConfig_ChannelSetup,
        PutConfig_LoadProfileInterest,
        PutConfig_LoadProfileReportPeriod,
        PutConfig_LongLoadProfile,
        PutConfig_DailyReadInterest,
        PutConfig_Multiplier,
        PutConfig_Multiplier2,
        PutConfig_Multiplier3,
        PutConfig_MinMax,
        PutConfig_OnOffPeak,
        PutConfig_Disconnect,
        PutConfig_Addressing,
        PutConfig_SPID,
        PutConfig_VThreshold,
        PutConfig_DST,
        PutConfig_Holiday,
        PutConfig_Options,
        PutConfig_AutoReconnect,
        PutConfig_Outage,
        PutConfig_OutageThreshold,
        PutConfig_TimeAdjustTolerance,
        PutConfig_ConfigurationByte,
        PutConfig_TimeZoneOffset,
        PutConfig_TOU,  //  this may need to be removed in light of the new config install commands
        PutConfig_TOUEnable,
        PutConfig_TOUDisable,
        PutConfig_FreezeDay,
        PutConfig_PrecannedTable,
        PutConfig_PhaseDetect,
        PutConfig_PhaseDetectClear,
        PutConfig_AlarmMask,
        PutConfig_Parameters,
        PutConfig_IEDDNPAddress,
        PutConfig_WaterMeterReadInterval,
        PutConfig_Channel2NetMetering,      // GetConfig_Options was hijacked for the config byte/alarm mask in the 410....
        PutConfig_PhaseLossThreshold,

        PutConfig_ARMC,
        PutConfig_ARML,
        PutConfig_ARMS,

        // Control Commands
        Control_Shed,     //  for MCT Group Addressing
        Control_Restore,  //
        Control_Connect,
        Control_Disconnect,
        Control_Latch,

        Command_Loop,

        DLCCmd_LAST    // PLACEHOLDER!!!
    };
};


}
}

