#pragma once

enum DeviceTypes {

    // A catchall for any op which needs an ID
    SYS_DID_SYSTEM = 0,

    TYPE_NONE = 0,

    // Definitions for Device types
    TYPELMT2,
    TYPEDCT501,
    TYPE_REPEATER900,
    TYPE_REPEATER800,
    TYPE_REPEATER850,
    TYPEPSEUDODEVICE,
    TYPEMCT210,
    TYPEMCT212,
    TYPEMCT213,
    TYPEMCT224,
    TYPEMCT226,
    TYPEMCT240,
    TYPEMCT242,
    TYPEMCT248,
    TYPEMCT250,
    TYPEMCT310,
    TYPEMCT310ID,
    TYPEMCT318,
    TYPEMCT310IL,
    TYPEMCT318L,
    TYPEMCT310IDL,
    TYPEMCT360,
    TYPEMCT370,
    TYPEMCT410CL,
    TYPEMCT410FL,
    TYPEMCT410GL,
    TYPEMCT410IL,
    TYPEMCT420CL,
    TYPEMCT420CD,
    TYPEMCT420FL,
    TYPEMCT420FD,
    TYPEMCT470,
    TYPEMCT430A,
    TYPEMCT430A3,
    TYPEMCT430S4,
    TYPEMCT430SL,
    TYPEMCT440_2131B,
    TYPEMCT440_2132B,
    TYPEMCT440_2133B,

    TYPELCR3102,

    TYPE_CAPBANK,
    TYPE_VERSACOMCBC,
    TYPE_EXPRESSCOMCBC,
    TYPE_FISHERPCBC,
    TYPE_CBC7010,
    TYPE_CBC7020,
    TYPE_CBCDNP,
    TYPE_CBCLOGICAL,
    TYPE_CBC8020,

    //  RFN Focus
    TYPE_RFN410FL,
    TYPE_RFN410FX,
    TYPE_RFN410FD,
    TYPE_RFN420FL,
    TYPE_RFN420FX,
    TYPE_RFN420FD,
    TYPE_RFN420FRX,
    TYPE_RFN420FRD,
    TYPE_RFN510FL,
    TYPE_RFN520FAX,
    TYPE_RFN520FRX,
    TYPE_RFN520FAXD,
    TYPE_RFN520FRXD,
    TYPE_RFN530FAX,
    TYPE_RFN530FRX,
    //  RFN Centron
    TYPE_RFN410CL,
    TYPE_RFN420CL,
    TYPE_RFN420CD,
    //  RFN A3
    TYPE_RFN430A3D,
    TYPE_RFN430A3T,
    TYPE_RFN430A3K,
    TYPE_RFN430A3R,
    //  RFN KV
    TYPE_RFN430KV,
    //  RFN Sentinel
    TYPE_RFN430SL0,
    TYPE_RFN430SL1,
    TYPE_RFN430SL2,
    TYPE_RFN430SL3,
    TYPE_RFN430SL4,
    //   RFN Focus S4
    TYPE_RFN530S4X,
    TYPE_RFN530S4EAX,
    TYPE_RFN530S4EAXR,
    TYPE_RFN530S4ERX,
    TYPE_RFN530S4ERXR,

    //  RF DA devices
    TYPE_RFN1200,

    // RF water meters
    TYPE_RFW201,

    // RF gas meters
    TYPE_RFG201,

    // Substation device identifiers
    TYPE_CCU700,
    TYPE_CCU710,
    TYPE_CCU711,
    TYPE_CCU721,
    TYPE_ILEXRTU,
    TYPE_WELCORTU,
    TYPE_SES92RTU,
    TYPE_DNPRTU,
    TYPE_DARTRTU,
    TYPE_ION7330,
    TYPE_ION7700,
    TYPE_ION8300,
    TYPE_LCU415,
    TYPE_LCU415LG,
    TYPE_LCU415ER,
    TYPE_LCUT3026,
    TYPE_TCU5000,
    TYPE_TCU5500,
    TYPE_TDMARKV,
    TYPE_DAVIS,
    TYPE_ALPHA_PPLUS,
    TYPE_FULCRUM,   // Schlumberger Fulcrum....
    TYPE_LGS4,      // Landis and Gyr S4....
    TYPE_VECTRON,   // Schlumberger Vectron...
    TYPE_ALPHA_A1,
    TYPE_DR87,
    TYPE_QUANTUM,   // Schlumberger Quantum
    TYPE_KV2,       // GE KV2
    TYPE_SENTINEL,  // Schlumberger Sentinel
    TYPE_FOCUS,     // Focus
    TYPE_ALPHA_A3,  // ABB Alpha A3

    TYPE_SIXNET,    // Sixnet VersaTrak/SiteTrak firmware > 7/1/01

    TYPE_IPC_410FL,
    TYPE_IPC_420FD,
    TYPE_IPC_430S4E,
    TYPE_IPC_430SL,

    TYPE_TAPTERM,
    TYPE_WCTP,
    TYPE_RDS,
    TYPE_SNPP,
    TYPE_PAGING_RECEIVER,
    TYPE_TNPP,

    TYPE_RTC,
    TYPE_RTM,
    TYPE_SERIESVRTU,
    TYPE_SERIESVLMIRTU,
    TYPE_MODBUS,

    TYPE_FCI,
    TYPE_NEUTRAL_MONITOR,

    // Group type devices...
    TYPE_LMGROUP_EMETCON,
    TYPE_LMGROUP_VERSACOM,
    TYPE_LMGROUP_RIPPLE,
    TYPE_LMGROUP_POINT,
    TYPE_LMGROUP_EXPRESSCOM,
    TYPE_LMGROUP_RFN_EXPRESSCOM,
    TYPE_LMGROUP_DIGI_SEP,
    TYPE_LMGROUP_ECOBEE,
    TYPE_LMGROUP_HONEYWELL,
    TYPE_LMGROUP_NEST,
    TYPE_LMGROUP_ITRON,
    TYPE_LMGROUP_MCT,
    
    TYPE_LMGROUP_GOLAY,
    TYPE_LMGROUP_SADIGITAL,
    TYPE_LMGROUP_SA105,
    TYPE_LMGROUP_SA205,
    TYPE_LMGROUP_SA305,

    TYPEMCTBCAST,

    //  Several of the following are NOT devices and should be moved out into 
    //    separate LmTypes/CcTypes/PortTypes enums 
    //  OR this enum should be renamed to PaoTypes.

    // LM category objects...
    TYPE_LMPROGRAM_DIRECT,
    TYPE_LMPROGRAM_CURTAILMENT,
    TYPE_LM_CONTROL_AREA,
    TYPE_LMPROGRAM_ENERGYEXCHANGE,

    // Customer devices...
    TYPE_CI_CUSTOMER,

    // CC category objects...
    TYPE_CC_SUBSTATION_BUS,
    TYPE_CC_FEEDER,
    TYPE_CC_AREA,
    TYPE_CC_SUBSTATION,
    TYPE_CC_SPECIALAREA,

    // Voltage Regulators
    TYPE_LOAD_TAP_CHANGER,
    TYPE_GANG_OPERATED_REGULATOR,
    TYPE_PHASE_OPERATED_REGULATOR,

    // A macro device that can contain other devices - basically a generalized group of devices
    TYPE_MACRO,

    // A system defined pseudo device.
    TYPE_SYSTEM,
    TYPE_VIRTUAL_SYSTEM,

    PortTypeLocalDirect,
    PortTypeLocalDialup,
    PortTypeTServerDirect,
    PortTypeTServerDialup,
    PortTypeLocalDialBack,
    PortTypeTServerDialBack,
    PortTypePoolDialout,
    PortTypeTcp,
    PortTypeUdp,
    PortTypeRfDa,

    PortTypeInvalid,
};

