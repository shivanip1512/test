package com.cannontech.common.pao;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.data.pao.DeviceTypes;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Sets;

public enum PaoType implements DisplayableEnum, DatabaseRepresentationSource {
    
    CCU710A(DeviceTypes.CCU710A, "CCU-710A", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    CCU711(DeviceTypes.CCU711, "CCU-711", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    CCU721(DeviceTypes.CCU721, "CCU-721", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    TCU5000(DeviceTypes.TCU5000, "TCU-5000", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    TCU5500(DeviceTypes.TCU5500, "TCU-5500", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    LCU415(DeviceTypes.LCU415, "LCU-415", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    LCULG(DeviceTypes.LCULG, "LCU-LG", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    LCU_ER(DeviceTypes.LCU_ER, "LCU-EASTRIVER", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    LCU_T3026(DeviceTypes.LCU_T3026, "LCU-T3026", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    RDS_TERMINAL(DeviceTypes.RDS_TERMINAL, "RDS TERMINAL", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    SNPP_TERMINAL(DeviceTypes.SNPP_TERMINAL, "SNPP TERMINAL", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    TAPTERMINAL(DeviceTypes.TAPTERMINAL, "TAP TERMINAL", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    TNPP_TERMINAL(DeviceTypes.TNPP_TERMINAL, "TNPP TERMINAL", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    WCTP_TERMINAL(DeviceTypes.WCTP_TERMINAL, "WCTP TERMINAL", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    SERIES_5_LMI(DeviceTypes.SERIES_5_LMI, "RTU-LMI", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    RTC(DeviceTypes.RTC, "RTC", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    DIGIGATEWAY(DeviceTypes.DIGI_GATEWAY, "Digi Gateway", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    
    REPEATER(DeviceTypes.REPEATER, "REPEATER", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    REPEATER_800(DeviceTypes.REPEATER_800, "REPEATER 800", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    REPEATER_801(DeviceTypes.REPEATER_801, "REPEATER 801", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    REPEATER_850(DeviceTypes.REPEATER_850, "REPEATER 850", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    REPEATER_902(DeviceTypes.REPEATER_902, "REPEATER 902", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    REPEATER_921(DeviceTypes.REPEATER_921, "REPEATER 921", PaoCategory.DEVICE, PaoClass.TRANSMITTER),
    
    ALPHA_A1(DeviceTypes.ALPHA_A1, "ALPHA A1", PaoCategory.DEVICE, PaoClass.METER),
    ALPHA_A3(DeviceTypes.ALPHA_A3, "ALPHA A3", PaoCategory.DEVICE, PaoClass.METER),
    ALPHA_PPLUS(DeviceTypes.ALPHA_PPLUS, "ALPHA POWER PLUS", PaoCategory.DEVICE, PaoClass.METER),
    DR_87(DeviceTypes.DR_87, "DR-87", PaoCategory.DEVICE, PaoClass.METER),
    FOCUS(DeviceTypes.FOCUS, "FOCUS", PaoCategory.DEVICE, PaoClass.METER),
    FULCRUM(DeviceTypes.FULCRUM, "FULCRUM", PaoCategory.DEVICE, PaoClass.METER),
    KV(DeviceTypes.KV, "KV", PaoCategory.DEVICE, PaoClass.METER),
    KVII(DeviceTypes.KVII, "KV2", PaoCategory.DEVICE, PaoClass.METER),
    LANDISGYRS4(DeviceTypes.LANDISGYRS4, "LANDIS-GYR S4", PaoCategory.DEVICE, PaoClass.METER),
    QUANTUM(DeviceTypes.QUANTUM, "QUANTUM", PaoCategory.DEVICE, PaoClass.METER),
    SENTINEL(DeviceTypes.SENTINEL, "SENTINEL", PaoCategory.DEVICE, PaoClass.METER),
    SIXNET(DeviceTypes.SIXNET, "SIXNET", PaoCategory.DEVICE, PaoClass.METER),
    TRANSDATA_MARKV(DeviceTypes.TRANSDATA_MARKV, "TRANSDATA MARK-V", PaoCategory.DEVICE, PaoClass.METER),
    VECTRON(DeviceTypes.VECTRON, "VECTRON", PaoCategory.DEVICE, PaoClass.METER),
    
    IPC430S4E(DeviceTypes.IPC430S4E, "IPC-430S4e", PaoCategory.DEVICE, PaoClass.METER),
    IPC430SL(DeviceTypes.IPC430SL, "IPC-430SL", PaoCategory.DEVICE, PaoClass.METER),
    IPC420FD(DeviceTypes.IPC420FD, "IPC-420fD", PaoCategory.DEVICE, PaoClass.METER),
    IPC410FL(DeviceTypes.IPC410FL, "IPC-410fL", PaoCategory.DEVICE, PaoClass.METER),
    
    DAVISWEATHER(DeviceTypes.DAVISWEATHER, "DAVIS WEATHER", PaoCategory.DEVICE, PaoClass.IED),
    
    DCT_501(DeviceTypes.DCT_501, "DCT-501", PaoCategory.DEVICE, PaoClass.CARRIER),
    LCR3102(DeviceTypes.LCR3102, "LCR-3102", PaoCategory.DEVICE, PaoClass.CARRIER),
    LMT_2(DeviceTypes.LMT_2, "LMT-2", PaoCategory.DEVICE, PaoClass.CARRIER),
    ZIGBEE_ENDPOINT(DeviceTypes.ZIGBEE_ENDPOINT, "ZigBee Endpoint", PaoCategory.DEVICE, PaoClass.CARRIER),
    
    MCTBROADCAST(DeviceTypes.MCTBROADCAST, "MCT Broadcast", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT210(DeviceTypes.MCT210, "MCT-210", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT213(DeviceTypes.MCT213, "MCT-213", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT240(DeviceTypes.MCT240, "MCT-240", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT248(DeviceTypes.MCT248, "MCT-248", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT250(DeviceTypes.MCT250, "MCT-250", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT310(DeviceTypes.MCT310, "MCT-310", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT310CT(DeviceTypes.MCT310CT, "MCT-310CT", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT310ID(DeviceTypes.MCT310ID, "MCT-310ID", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT310IDL(DeviceTypes.MCT310IDL, "MCT-310IDL", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT310IL(DeviceTypes.MCT310IL, "MCT-310IL", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT310IM(DeviceTypes.MCT310IM, "MCT-310IM", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT318(DeviceTypes.MCT318, "MCT-318", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT318L(DeviceTypes.MCT318L, "MCT-318L", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT360(DeviceTypes.MCT360, "MCT-360", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT370(DeviceTypes.MCT370, "MCT-370", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT410CL(DeviceTypes.MCT410CL, "MCT-410cL", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT410FL(DeviceTypes.MCT410FL, "MCT-410fL", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT410GL(DeviceTypes.MCT410GL, "MCT-410gL", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT410IL(DeviceTypes.MCT410IL, "MCT-410iL", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT420CL(DeviceTypes.MCT420CL, "MCT-420cL", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT420CD(DeviceTypes.MCT420CD, "MCT-420cD", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT420FL(DeviceTypes.MCT420FL, "MCT-420fL", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT420FD(DeviceTypes.MCT420FD, "MCT-420fD", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT430A(DeviceTypes.MCT430A, "MCT-430A", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT430A3(DeviceTypes.MCT430A3, "MCT-430A3", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT430S4(DeviceTypes.MCT430S4, "MCT-430S4", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT430SL(DeviceTypes.MCT430SL, "MCT-430SL", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT440_2131B(DeviceTypes.MCT440_2131B, "MCT-440-2131B", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT440_2132B(DeviceTypes.MCT440_2132B, "MCT-440-2132B", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT440_2133B(DeviceTypes.MCT440_2133B, "MCT-440-2133B", PaoCategory.DEVICE, PaoClass.CARRIER),
    MCT470(DeviceTypes.MCT470, "MCT-470", PaoCategory.DEVICE, PaoClass.CARRIER),
    
    RFN410FL(DeviceTypes.RFN410FL, "RFN-410fL", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN410FX(DeviceTypes.RFN410FX, "RFN-410fX", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN410FD(DeviceTypes.RFN410FD, "RFN-410fD", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN420FL(DeviceTypes.RFN420FL, "RFN-420fL", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN420FX(DeviceTypes.RFN420FX, "RFN-420fX", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN420FD(DeviceTypes.RFN420FD, "RFN-420fD", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN420FRX(DeviceTypes.RFN420FRX, "RFN-420fRX", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN420FRD(DeviceTypes.RFN420FRD, "RFN-420fRD", PaoCategory.DEVICE, PaoClass.RFMESH),
    
    RFN410CL(DeviceTypes.RFN410CL, "RFN-410cL", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN420CL(DeviceTypes.RFN420CL, "RFN-420cL", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN420CD(DeviceTypes.RFN420CD, "RFN-420cD", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN420CLW(DeviceTypes.RFN420CLW, "RFN-420cLW", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN420CDW(DeviceTypes.RFN420CDW, "RFN-420cDW", PaoCategory.DEVICE, PaoClass.RFMESH),
    
    RFN430A3D(DeviceTypes.RFN430A3D, "RFN-430A3D", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN430A3T(DeviceTypes.RFN430A3T, "RFN-430A3T", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN430A3K(DeviceTypes.RFN430A3K, "RFN-430A3K", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN430A3R(DeviceTypes.RFN430A3R, "RFN-430A3R", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN430KV(DeviceTypes.RFN430KV, "RFN-430KV", PaoCategory.DEVICE, PaoClass.RFMESH),
    
    RFN430SL0(DeviceTypes.RFN430SL0, "RFN-430SL0", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN430SL1(DeviceTypes.RFN430SL1, "RFN-430SL1", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN430SL2(DeviceTypes.RFN430SL2, "RFN-430SL2", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN430SL3(DeviceTypes.RFN430SL3, "RFN-430SL3", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN430SL4(DeviceTypes.RFN430SL4, "RFN-430SL4", PaoCategory.DEVICE, PaoClass.RFMESH),
    
    RFN440_2131TD(DeviceTypes.RFN440_2131TD, "RFN-440-2131TD", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN440_2132TD(DeviceTypes.RFN440_2132TD, "RFN-440-2132TD", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN440_2133TD(DeviceTypes.RFN440_2133TD, "RFN-440-2133TD", PaoCategory.DEVICE, PaoClass.RFMESH),
    
    RFN510FL(DeviceTypes.RFN510FL, "RFN-510fL", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN520FAX(DeviceTypes.RFN520FAX, "RFN-520fAX", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN520FRX(DeviceTypes.RFN520FRX, "RFN-520fRX", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN520FAXD(DeviceTypes.RFN520FAXD, "RFN-520fAXD", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN520FRXD(DeviceTypes.RFN520FRXD, "RFN-520fRXD", PaoCategory.DEVICE, PaoClass.RFMESH),
    
    RFN530FAX(DeviceTypes.RFN530FAX, "RFN-530fAX", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN530FRX(DeviceTypes.RFN530FRX, "RFN-530fRX", PaoCategory.DEVICE, PaoClass.RFMESH),
    
    RFN530S4X(DeviceTypes.RFN530S4X, "RFN-530S4x", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN530S4EAX(DeviceTypes.RFN530S4EAX, "RFN-530S4eAX", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN530S4EAXR(DeviceTypes.RFN530S4EAXR, "RFN-530S4eAXR", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN530S4ERX(DeviceTypes.RFN530S4ERX, "RFN-530S4eRX", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFN530S4ERXR(DeviceTypes.RFN530S4ERXR, "RFN-530S4eRXR", PaoCategory.DEVICE, PaoClass.RFMESH),
    
    RFWMETER(DeviceTypes.RFWMETER, "RFW-Meter", PaoCategory.DEVICE, PaoClass.RFMESH),
    LCR6200_RFN(DeviceTypes.LCR6200_RFN, "LCR-6200 RFN", PaoCategory.DEVICE, PaoClass.RFMESH),
    LCR6600_RFN(DeviceTypes.LCR6600_RFN, "LCR-6600 RFN", PaoCategory.DEVICE, PaoClass.RFMESH),
    LCR6700_RFN(DeviceTypes.LCR6700_RFN, "LCR-6700 RFN", PaoCategory.DEVICE, PaoClass.RFMESH),
    
    LCR6600S(DeviceTypes.LCR6600S, "LCR-6600S", PaoCategory.DEVICE, PaoClass.ITRON),
    LCR6601S(DeviceTypes.LCR6601S, "LCR-6601S", PaoCategory.DEVICE, PaoClass.ITRON),
    
    RFN_GATEWAY(DeviceTypes.RFN_GATEWAY, "RF Gateway", PaoCategory.DEVICE, PaoClass.RFMESH),
    GWY800(DeviceTypes.GWY800, "GWY-800", PaoCategory.DEVICE, PaoClass.RFMESH),
    
    RFN_RELAY(DeviceTypes.RFN_RELAY, "RFN Relay", PaoCategory.DEVICE, PaoClass.RFMESH),
    
    ION_7700(DeviceTypes.ION_7700, "ION-7700", PaoCategory.DEVICE, PaoClass.RTU),
    ION_8300(DeviceTypes.ION_8300, "ION-8300", PaoCategory.DEVICE, PaoClass.RTU),
    ION_7330(DeviceTypes.ION_7330, "ION-7330", PaoCategory.DEVICE, PaoClass.RTU),
    RTM(DeviceTypes.RTM, "RTM", PaoCategory.DEVICE, PaoClass.RTU),
    RTU_DART(DeviceTypes.RTU_DART, "RTU-DART", PaoCategory.DEVICE, PaoClass.RTU),
    RTU_DNP(DeviceTypes.RTU_DNP, "RTU-DNP", PaoCategory.DEVICE, PaoClass.RTU),
    RTUILEX(DeviceTypes.RTUILEX, "RTU-ILEX", PaoCategory.DEVICE, PaoClass.RTU),
    RTU_MODBUS(DeviceTypes.RTU_MODBUS, "RTU-MODBUS", PaoCategory.DEVICE, PaoClass.RTU),
    RTUWELCO(DeviceTypes.RTUWELCO, "RTU-WELCO", PaoCategory.DEVICE, PaoClass.RTU),
    
    LM_GROUP_DIGI_SEP(DeviceTypes.LM_GROUP_DIGI_SEP, "DIGI SEP GROUP", PaoCategory.DEVICE, PaoClass.GROUP),
    LM_GROUP_ECOBEE(DeviceTypes.LM_GROUP_ECOBEE, "ECOBEE GROUP", PaoCategory.DEVICE, PaoClass.GROUP),
    LM_GROUP_HONEYWELL(DeviceTypes.LM_GROUP_HONEYWELL, "HONEYWELL GROUP", PaoCategory.DEVICE, PaoClass.GROUP),
    LM_GROUP_ITRON(DeviceTypes.LM_GROUP_ITRON, "ITRON GROUP", PaoCategory.DEVICE, PaoClass.GROUP),
    LM_GROUP_NEST(DeviceTypes.LM_GROUP_NEST, "NEST GROUP", PaoCategory.DEVICE, PaoClass.GROUP),
    LM_GROUP_EMETCON(DeviceTypes.LM_GROUP_EMETCON, "EMETCON GROUP", PaoCategory.DEVICE, PaoClass.GROUP),
    LM_GROUP_EXPRESSCOMM(DeviceTypes.LM_GROUP_EXPRESSCOMM, "EXPRESSCOM GROUP", PaoCategory.DEVICE, PaoClass.GROUP),
    LM_GROUP_RFN_EXPRESSCOMM(DeviceTypes.LM_GROUP_RFN_EXPRESSCOMM, "RFN EXPRESSCOM GROUP", PaoCategory.DEVICE, PaoClass.GROUP),
    LM_GROUP_GOLAY(DeviceTypes.LM_GROUP_GOLAY, "Golay Group", PaoCategory.DEVICE, PaoClass.GROUP),
    LM_GROUP_MCT(DeviceTypes.LM_GROUP_MCT, "MCT GROUP", PaoCategory.DEVICE, PaoClass.GROUP),
    LM_GROUP_POINT(DeviceTypes.LM_GROUP_POINT, "POINT GROUP", PaoCategory.DEVICE, PaoClass.GROUP),
    LM_GROUP_RIPPLE(DeviceTypes.LM_GROUP_RIPPLE, "RIPPLE GROUP", PaoCategory.DEVICE, PaoClass.GROUP),
    LM_GROUP_SA305(DeviceTypes.LM_GROUP_SA305, "SA-305 Group", PaoCategory.DEVICE, PaoClass.GROUP),
    LM_GROUP_SA205(DeviceTypes.LM_GROUP_SA205, "SA-205 Group", PaoCategory.DEVICE, PaoClass.GROUP),
    LM_GROUP_SADIGITAL(DeviceTypes.LM_GROUP_SADIGITAL, "SA-Digital Group", PaoCategory.DEVICE, PaoClass.GROUP),
    LM_GROUP_VERSACOM(DeviceTypes.LM_GROUP_VERSACOM, "VERSACOM GROUP", PaoCategory.DEVICE, PaoClass.GROUP),
    LM_GROUP_METER_DISCONNECT(DeviceTypes.LM_GROUP_METER_DISCONNECT, "METER DISCONNECT GROUP", PaoCategory.DEVICE, PaoClass.GROUP),
    MACRO_GROUP(DeviceTypes.MACRO_GROUP, "MACRO GROUP", PaoCategory.DEVICE, PaoClass.GROUP),
    
    LM_CURTAIL_PROGRAM(DeviceTypes.LM_CURTAIL_PROGRAM, "LM CURTAIL PROGRAM", PaoCategory.LOADMANAGEMENT, PaoClass.LOADMANAGEMENT),
    LM_DIRECT_PROGRAM(DeviceTypes.LM_DIRECT_PROGRAM, "LM DIRECT PROGRAM", PaoCategory.LOADMANAGEMENT, PaoClass.LOADMANAGEMENT),
    LM_ENERGY_EXCHANGE_PROGRAM(DeviceTypes.LM_ENERGY_EXCHANGE_PROGRAM, "LM ENERGY EXCHANGE", PaoCategory.LOADMANAGEMENT, PaoClass.LOADMANAGEMENT),
    LM_SEP_PROGRAM(DeviceTypes.LM_SEP_PROGRAM, "LM SEP PROGRAM", PaoCategory.LOADMANAGEMENT, PaoClass.LOADMANAGEMENT),
    LM_ECOBEE_PROGRAM(DeviceTypes.LM_ECOBEE_PROGRAM, "ECOBEE PROGRAM", PaoCategory.LOADMANAGEMENT, PaoClass.LOADMANAGEMENT),
    LM_HONEYWELL_PROGRAM(DeviceTypes.LM_HONEYWELL_PROGRAM, "HONEYWELL PROGRAM", PaoCategory.LOADMANAGEMENT, PaoClass.LOADMANAGEMENT),
    LM_ITRON_PROGRAM(DeviceTypes.LM_ITRON_PROGRAM, "ITRON PROGRAM", PaoCategory.LOADMANAGEMENT, PaoClass.LOADMANAGEMENT),
    LM_NEST_PROGRAM(DeviceTypes.LM_NEST_PROGRAM, "NEST PROGRAM", PaoCategory.LOADMANAGEMENT, PaoClass.LOADMANAGEMENT),
    LM_METER_DISCONNECT_PROGRAM(DeviceTypes.LM_METER_DISCONNECT_PROGRAM, "METER DISCONNECT PROGRAM", PaoCategory.LOADMANAGEMENT, PaoClass.LOADMANAGEMENT),
    LM_CONTROL_AREA(DeviceTypes.LM_CONTROL_AREA, "LM CONTROL AREA", PaoCategory.LOADMANAGEMENT, PaoClass.LOADMANAGEMENT),
    LM_SCENARIO(DeviceTypes.LM_SCENARIO, "LMSCENARIO", PaoCategory.LOADMANAGEMENT, PaoClass.LOADMANAGEMENT),
    
    CAPBANK(DeviceTypes.CAPBANK, "CAP BANK", PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    CAPBANKCONTROLLER(DeviceTypes.CAPBANKCONTROLLER, "CBC Versacom", PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    CBC_EXPRESSCOM(DeviceTypes.CBC_EXPRESSCOM, "CBC Expresscom", PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    
    CBC_7010(DeviceTypes.CBC_7010, "CBC 7010", PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    CBC_7020(DeviceTypes.CBC_7020, "CBC 7020", PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    CBC_7022(DeviceTypes.CBC_7022, "CBC 7022", PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    CBC_7023(DeviceTypes.CBC_7023, "CBC 7023", PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    CBC_7024(DeviceTypes.CBC_7024, "CBC 7024", PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    CBC_7011(DeviceTypes.CBC_7011, "CBC 7011", PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    CBC_7012(DeviceTypes.CBC_7012, "CBC 7012", PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    CBC_8020(DeviceTypes.CBC_8020, "CBC 8020", PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    CBC_8024(DeviceTypes.CBC_8024, "CBC 8024", PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    CBC_DNP(DeviceTypes.CBC_DNP, "CBC DNP", PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    CBC_FP_2800(DeviceTypes.CBC_FP_2800, "CBC FP-2800", PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    CBC_LOGICAL(DeviceTypes.CBC_LOGICAL, "CBC Logical", PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    
    CAP_CONTROL_SUBBUS(CapControlTypes.CAP_CONTROL_SUBBUS, CapControlType.SUBBUS.getDbValue(), PaoCategory.CAPCONTROL, PaoClass.CAPCONTROL),
    CAP_CONTROL_FEEDER(CapControlTypes.CAP_CONTROL_FEEDER, CapControlType.FEEDER.getDbValue(), PaoCategory.CAPCONTROL, PaoClass.CAPCONTROL),
    CAP_CONTROL_AREA(CapControlTypes.CAP_CONTROL_AREA, CapControlType.AREA.getDbValue(), PaoCategory.CAPCONTROL, PaoClass.CAPCONTROL),
    CAP_CONTROL_SPECIAL_AREA(CapControlTypes.CAP_CONTROL_SPECIAL_AREA, CapControlType.SPECIAL_AREA.getDbValue(), PaoCategory.CAPCONTROL, PaoClass.CAPCONTROL),
    CAP_CONTROL_SUBSTATION(CapControlTypes.CAP_CONTROL_SUBSTATION, CapControlType.SUBSTATION.getDbValue(), PaoCategory.CAPCONTROL, PaoClass.CAPCONTROL),
    LOAD_TAP_CHANGER(CapControlTypes.CAP_CONTROL_LTC, CapControlType.LTC.getDbValue(), PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    GANG_OPERATED(CapControlTypes.GANG_OPERATED_REGULATOR, CapControlType.GO_REGULATOR.getDbValue(), PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    PHASE_OPERATED(CapControlTypes.PHASE_OPERATED_REGULATOR, CapControlType.PO_REGULATOR.getDbValue(), PaoCategory.DEVICE, PaoClass.CAPCONTROL),
    
    FAULT_CI(DeviceTypes.FAULT_CI, "Faulted Circuit Indicator", PaoCategory.DEVICE, PaoClass.GRID),
    NEUTRAL_MONITOR(DeviceTypes.NEUTRAL_MONITOR, "Capacitor Bank Neutral Monitor", PaoCategory.DEVICE, PaoClass.GRID),
    
    ROUTE_CCU(2000, "CCU", PaoCategory.ROUTE, PaoClass.ROUTE),
    ROUTE_TCU(2001, "TCU", PaoCategory.ROUTE, PaoClass.ROUTE),
    ROUTE_LCU(2002, "LCU", PaoCategory.ROUTE, PaoClass.ROUTE),
    ROUTE_MACRO(2003, "Macro", PaoCategory.ROUTE, PaoClass.ROUTE),
    ROUTE_VERSACOM(2004, "Versacom", PaoCategory.ROUTE, PaoClass.ROUTE),
    ROUTE_TAP_PAGING(2005, "Tap Paging", PaoCategory.ROUTE, PaoClass.ROUTE),
    ROUTE_WCTP_TERMINAL(2006, "WCTP Terminal Route", PaoCategory.ROUTE, PaoClass.ROUTE),
    ROUTE_SERIES_5_LMI(2007, "Series 5 LMI", PaoCategory.ROUTE, PaoClass.ROUTE),
    ROUTE_RTC(2008, "RTC Route", PaoCategory.ROUTE, PaoClass.ROUTE),
    ROUTE_SNPP_TERMINAL(2009, "SNPP Terminal Route", PaoCategory.ROUTE, PaoClass.ROUTE),
    ROUTE_TNPP_TERMINAL(2011, "TNPP Terminal Route", PaoCategory.ROUTE, PaoClass.ROUTE),
    ROUTE_RDS_TERMINAL(2012, "RDS Terminal Route", PaoCategory.ROUTE, PaoClass.ROUTE),
    
    LOCAL_DIRECT(3000, "Local Direct", PaoCategory.PORT, PaoClass.PORT),
    LOCAL_SHARED(3001, "Local Serial Port", PaoCategory.PORT, PaoClass.PORT),
    LOCAL_RADIO(3002, "Local Radio", PaoCategory.PORT, PaoClass.PORT),
    LOCAL_DIALUP(3003, "Local Dialup", PaoCategory.PORT, PaoClass.PORT),
    TSERVER_DIRECT(3004, "Terminal Server Direct", PaoCategory.PORT, PaoClass.PORT),
    TCPPORT(3010, "TCP", PaoCategory.PORT, PaoClass.PORT),
    UDPPORT(3011, "UDP", PaoCategory.PORT, PaoClass.PORT),
    TSERVER_SHARED(3005, "Terminal Server", PaoCategory.PORT, PaoClass.PORT),
    TSERVER_RADIO(3006, "Terminal Server Radio", PaoCategory.PORT, PaoClass.PORT),
    TSERVER_DIALUP(3007, "Terminal Server Dialup", PaoCategory.PORT, PaoClass.PORT),
    LOCAL_DIALBACK(3008, "Local Dialback", PaoCategory.PORT, PaoClass.PORT),
    DIALOUT_POOL(3009, "Dialout Pool", PaoCategory.PORT, PaoClass.PORT),
    
    SCRIPT(DeviceTypes.SCRIPT, "Script", PaoCategory.SCHEDULE, PaoClass.SCHEDULE),
    SIMPLE_SCHEDULE(DeviceTypes.SIMPLE_SCHEDULE, "Simple", PaoCategory.SCHEDULE, PaoClass.SCHEDULE),
    SYSTEM(DeviceTypes.SYSTEM, "SYSTEM", PaoCategory.DEVICE, PaoClass.SYSTEM),
    VIRTUAL_SYSTEM(DeviceTypes.VIRTUAL_SYSTEM, "VIRTUAL SYSTEM", PaoCategory.DEVICE, PaoClass.VIRTUAL),
    
    WEATHER_LOCATION(DeviceTypes.WEATHER_LOCATION, "WEATHER LOCATION", PaoCategory.DEVICE, PaoClass.VIRTUAL),
    
    RFN_1200(DeviceTypes.RFN_1200, "RFN-1200", PaoCategory.DEVICE, PaoClass.RFMESH),
    
    ECOBEE_SMART_SI(DeviceTypes.ECOBEE_SMART_SI, "ecobee Smart Si", PaoCategory.DEVICE, PaoClass.THERMOSTAT),
    ECOBEE_3(DeviceTypes.ECOBEE_3, "ecobee3", PaoCategory.DEVICE, PaoClass.THERMOSTAT),
    ECOBEE_3_LITE(DeviceTypes.ECOBEE_3_LITE, "ecobee3 Lite", PaoCategory.DEVICE, PaoClass.THERMOSTAT),
    ECOBEE_SMART(DeviceTypes.ECOBEE_SMART, "ecobee Smart", PaoCategory.DEVICE, PaoClass.THERMOSTAT),
    HONEYWELL_9000(DeviceTypes.HONEYWELL_9000, "Honeywell Wi-Fi 9000", PaoCategory.DEVICE, PaoClass.THERMOSTAT),
    HONEYWELL_VISIONPRO_8000(DeviceTypes.HONEYWELL_VISIONPRO_8000, "Honeywell Wi-Fi VisionPRO 8000", PaoCategory.DEVICE, PaoClass.THERMOSTAT),
    HONEYWELL_FOCUSPRO(DeviceTypes.HONEYWELL_FOCUSPRO, "Honeywell Wi-Fi FocusPRO", PaoCategory.DEVICE, PaoClass.THERMOSTAT),
    HONEYWELL_THERMOSTAT(DeviceTypes.HONEYWELL_THERMOSTAT, "Honeywell Wi-Fi Thermostat", PaoCategory.DEVICE, PaoClass.THERMOSTAT),

    RFW201(DeviceTypes.RFW201, "RFW-201", PaoCategory.DEVICE, PaoClass.RFMESH),
    
    RFG201(DeviceTypes.RFG201, "RFG-201", PaoCategory.DEVICE, PaoClass.RFMESH),
    RFG301(DeviceTypes.RFG301, "RFG-301", PaoCategory.DEVICE, PaoClass.RFMESH),
    
    NEST(DeviceTypes.NEST, "Nest", PaoCategory.DEVICE, PaoClass.THERMOSTAT),
    
    // WHEN ADDING A NEW PAO TYPE, REMEMBER TO ADD A NEW I18N ENTRY TO 
    // common/i18n/en_US/com/cannontech/yukon/common/pao.xml
    ;
    
    private final int deviceTypeId;
    private final String dbString;
    private final PaoCategory paoCategory;
    private final PaoClass paoClass;
    private final static Logger log = YukonLogManager.getLogger(PaoType.class);
    
    private final static ImmutableMap<Integer, PaoType> lookupById;
    private final static ImmutableMap<String, PaoType> lookupByDbString;
    private final static ImmutableSet<PaoType> meterTypes;
    private final static ImmutableSet<PaoType> itronTypes;
    private final static ImmutableSet<PaoType> rfTypes;
    private final static ImmutableSet<PaoType> rfMeterTypes;
    private final static ImmutableSet<PaoType> rfElectricTypes;
    private final static ImmutableSet<PaoType> cbcTypes;
    private final static ImmutableSet<PaoType> regulatorTypes;
    private final static ImmutableSet<PaoType> mctTypes;
    private final static ImmutableSet<PaoType> iedTypes;
    private final static ImmutableSet<PaoType> rtuTypes;
    private final static ImmutableSet<PaoType> ionTypes;
    private final static ImmutableSet<PaoType> ipcTypes;
    private final static ImmutableSet<PaoType> portTypes;
    private final static ImmutableSet<PaoType> lmProgramTypes;
    private final static ImmutableSet<PaoType> directProgramTypes;
    private final static ImmutableSet<PaoType> twoWayLcrTypes;
    private final static ImmutableSet<PaoType> rfLcrTypes;
    private final static ImmutableSet<PaoType> twoWayPlcLcrTypes;
    private final static ImmutableSet<PaoType> capControlTypes;
    private final static ImmutableSet<PaoType> ccuTypes;
    private final static ImmutableSet<PaoType> tcuTypes;
    private final static ImmutableSet<PaoType> lcuTypes;
    private final static ImmutableSet<PaoType> repeaterTypes;
    private final static ImmutableSet<PaoType> routableTypes;
    private final static ImmutableSet<PaoType> ecobeeTypes;
    private final static ImmutableSet<PaoType> transmitterTypes;
    private final static ImmutableSet<PaoType> rfGatewayTypes;
    private final static ImmutableSet<PaoType> substationBusTypes;
    private final static ImmutableSet<PaoType> rfDaTypes;
    private final static ImmutableSet<PaoType> honeywellTypes;
    private final static ImmutableSet<PaoType> rfRelayTypes;
    private final static ImmutableSet<PaoType> waterMeterTypes;
    private final static ImmutableSet<PaoType> thermostatTypes;
    private final static ImmutableSet<PaoType> tlvReportingTypes;
    private final static ImmutableSet<PaoType> gasMeterTypes;
    private final static ImmutableSet<PaoType> nestTypes;
    private final static ImmutableSet<PaoType> loadGroupSupportingRoute;

    public final static int INVALID = -1;
    
    static {
        try {
            ImmutableMap.Builder<Integer, PaoType> idBuilder = ImmutableMap.builder();
            ImmutableMap.Builder<String, PaoType> dbBuilder = ImmutableMap.builder();
            for (PaoType deviceType : values()) {
                idBuilder.put(deviceType.deviceTypeId, deviceType);
                dbBuilder.put(deviceType.dbString.toUpperCase(), deviceType);
            }
            lookupById = idBuilder.build();
            lookupByDbString = dbBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate name or db string.", e);
            throw e;
        }
        
        substationBusTypes = ImmutableSet.of(CAP_CONTROL_SUBBUS);
        
        lmProgramTypes = ImmutableSet.of(
            LM_CURTAIL_PROGRAM,
            LM_DIRECT_PROGRAM,
            LM_ENERGY_EXCHANGE_PROGRAM,
            LM_SEP_PROGRAM,
            LM_ECOBEE_PROGRAM,
            LM_HONEYWELL_PROGRAM,
            LM_NEST_PROGRAM,
            LM_ITRON_PROGRAM,
            LM_METER_DISCONNECT_PROGRAM
            );
        
        directProgramTypes = ImmutableSet.of(
            LM_DIRECT_PROGRAM,
            LM_SEP_PROGRAM,
            LM_ECOBEE_PROGRAM,
            LM_HONEYWELL_PROGRAM,
            LM_NEST_PROGRAM,
            LM_ITRON_PROGRAM,
            LM_METER_DISCONNECT_PROGRAM
            );
        
        cbcTypes = ImmutableSet.of(
            CBC_EXPRESSCOM,
            CAPBANKCONTROLLER,
            CBC_7010,
            CBC_7011,
            CBC_7012,
            CBC_7020,
            CBC_7022,
            CBC_7023,
            CBC_7024,
            CBC_8020,
            CBC_8024,
            CBC_DNP,
            CBC_LOGICAL,
            CBC_FP_2800);
        
        regulatorTypes = ImmutableSet.of(
            GANG_OPERATED,
            PHASE_OPERATED,
            LOAD_TAP_CHANGER);

        ccuTypes = ImmutableSet.of(
            CCU710A,
            CCU711,
            CCU721);
        
        tcuTypes = ImmutableSet.of(
            TCU5000,
            TCU5500);
        
        lcuTypes = ImmutableSet.of(
            LCU415,
            LCU_ER,
            LCU_T3026,
            LCULG);
        
        repeaterTypes = ImmutableSet.of(
            REPEATER,
            REPEATER_800,
            REPEATER_801,
            REPEATER_850,
            REPEATER_902,
            REPEATER_921);
        
        meterTypes = ImmutableSet.of(
            ALPHA_A1,
            ALPHA_A3,
            ALPHA_PPLUS,
            DR_87,
            FOCUS,
            FULCRUM,
            ION_7330,
            ION_7700,
            ION_8300,
            IPC410FL,
            IPC420FD,
            IPC430S4E,
            IPC430SL,
            KV,
            KVII,
            LANDISGYRS4,
            MCT210,
            MCT213,
            MCT240,
            MCT248,
            MCT250,
            MCT310,
            MCT310CT,
            MCT310ID,
            MCT310IDL,
            MCT310IL,
            MCT310IM,
            MCT318,
            MCT318L,
            MCT360,
            MCT370,
            MCT410CL,
            MCT410FL,
            MCT410GL,
            MCT410IL,
            MCT420CD,
            MCT420CL,
            MCT420FD,
            MCT420FL,
            MCT430A,
            MCT430A3,
            MCT430S4,
            MCT430SL,
            MCT440_2131B,
            MCT440_2132B,
            MCT440_2133B,
            MCT470,
            MCTBROADCAST,
            QUANTUM,
            RFN410CL,
            RFN410FD,
            RFN410FL,
            RFN410FX,
            RFN420CD,
            RFN420CDW,
            RFN420CL,
            RFN420CLW,
            RFN420FD,
            RFN420FL,
            RFN420FRD,
            RFN420FRX,
            RFN420FX,
            RFN430A3D,
            RFN430A3K,
            RFN430A3R,
            RFN430A3T,
            RFN430KV,
            RFN430SL0,
            RFN430SL1,
            RFN430SL2,
            RFN430SL3,
            RFN430SL4,
            RFN440_2131TD,
            RFN440_2132TD,
            RFN440_2133TD,
            RFN510FL,
            RFN520FAX,
            RFN520FRX,
            RFN520FAXD,
            RFN520FRXD,
            RFN530FAX,
            RFN530FRX,
            RFN530S4X,
            RFN530S4EAX,
            RFN530S4EAXR,
            RFN530S4ERX,
            RFN530S4ERXR,
            RFWMETER,
            RFW201,
            RFG201,
            RFG301,
            SENTINEL,
            SIXNET,
            TRANSDATA_MARKV,
            VECTRON
            );
        
        rfTypes = ImmutableSet.of(
            RFN410CL,
            RFN410FD,
            RFN410FL,
            RFN410FX,
            RFN420CD,
            RFN420CDW,
            RFN420CL,
            RFN420CLW,
            RFN420FD,
            RFN420FL,
            RFN420FRD,
            RFN420FRX,
            RFN420FX,
            RFN430A3D,
            RFN430A3K,
            RFN430A3R,
            RFN430A3T,
            RFN430KV,
            RFN430SL0,
            RFN430SL1,
            RFN430SL2,
            RFN430SL3,
            RFN430SL4,
            RFN440_2131TD,
            RFN440_2132TD,
            RFN440_2133TD,
            RFN510FL,
            RFN520FAX,
            RFN520FRX,
            RFN520FAXD,
            RFN520FRXD,
            RFN530FAX,
            RFN530FRX,
            RFN530S4X,
            RFN530S4EAX,
            RFN530S4EAXR,
            RFN530S4ERX,
            RFN530S4ERXR,
            RFWMETER,
            RFW201,
            RFG201,
            RFG301,
            RFN_1200,
            LCR6200_RFN,
            LCR6600_RFN,
            LCR6700_RFN,
            RFN_RELAY);
        
        rfMeterTypes = Sets.intersection(rfTypes, meterTypes).immutableCopy();
        
        itronTypes = ImmutableSet.of(
            LCR6600S,
            LCR6601S
        );
        
        mctTypes = ImmutableSet.of(
            MCT213,
            MCT310,
            MCT318,
            MCT360,
            MCT370,
            MCT240,
            MCT248,
            MCT250,
            MCT210,
            LMT_2,
            DCT_501,
            MCT310ID,
            MCT310IDL,
            MCT310IL,
            MCT410IL,
            MCT410CL,
            MCT410FL,
            MCT410GL,
            MCT420FL,
            MCT420FD,
            MCT420CL,
            MCT420CD,
            MCT430A,
            MCT430S4,
            MCT430SL,
            MCT430A3,
            MCT440_2131B,
            MCT440_2132B,
            MCT440_2133B,
            MCT470,
            MCT310CT,
            MCT310IM,
            MCT318L);
        
        iedTypes = ImmutableSet.of(
            ALPHA_PPLUS,
            ALPHA_A1,
            FULCRUM,
            VECTRON,
            LANDISGYRS4,
            DR_87,
            QUANTUM,
            SIXNET,
            ION_7700,
            ION_7330,
            ION_8300,
            TRANSDATA_MARKV,
            KV,
            KVII,
            SENTINEL,
            FOCUS,
            ALPHA_A3,
            DAVISWEATHER,
            IPC430SL,
            IPC430S4E,
            IPC420FD,
            IPC410FL);
        
        rtuTypes = ImmutableSet.of(
            RTU_DNP,
            RTU_MODBUS,
            RTU_DART,
            RTUILEX,
            RTUWELCO,
            RTM);
        
        ionTypes = ImmutableSet.of(
            ION_7330,
            ION_7700,
            ION_8300);
        
        ipcTypes = ImmutableSet.of(
            IPC430SL,
            IPC430S4E,
            IPC420FD,
            IPC410FL);
        
        portTypes = ImmutableSet.of(
            LOCAL_DIRECT,
            LOCAL_SHARED,
            LOCAL_RADIO,
            LOCAL_DIALUP,
            TSERVER_DIRECT,
            TCPPORT,
            UDPPORT,
            TSERVER_SHARED,
            TSERVER_RADIO,
            TSERVER_DIALUP,
            LOCAL_DIALBACK,
            DIALOUT_POOL,
            RFN_1200);
        
        rfLcrTypes = ImmutableSet.of(
            LCR6200_RFN,
            LCR6600_RFN,
            LCR6700_RFN);
        
        twoWayPlcLcrTypes = ImmutableSet.of(
            LCR3102
            );
        
        twoWayLcrTypes = Sets.union(rfLcrTypes, twoWayPlcLcrTypes).immutableCopy();
        
        rfGatewayTypes = ImmutableSet.of(
            RFN_GATEWAY,
            GWY800);
        
        rfRelayTypes = ImmutableSet.of(
            RFN_RELAY);
        
        rfDaTypes = ImmutableSet.of(RFN_1200);
        
        tlvReportingTypes = ImmutableSet.of(LCR6700_RFN);
        
        Builder<PaoType> capControlTypeBuilder = ImmutableSet.builder();
        for (PaoType paoType : PaoType.values()) {
            if (paoType.isCapControl()) {
                capControlTypeBuilder.add(paoType);
            }
        }
        capControlTypes = capControlTypeBuilder.build();
        
        Builder<PaoType> b = ImmutableSet.builder();
        b.add(LCR3102);
        b.add(MCTBROADCAST);
        b.addAll(mctTypes);
        b.addAll(repeaterTypes);
        routableTypes = b.build();
        
        ecobeeTypes = ImmutableSet.of(ECOBEE_SMART_SI, ECOBEE_3, ECOBEE_SMART, ECOBEE_3_LITE);
        honeywellTypes = ImmutableSet.of(HONEYWELL_9000, HONEYWELL_FOCUSPRO, HONEYWELL_VISIONPRO_8000, HONEYWELL_THERMOSTAT);
        nestTypes = ImmutableSet.of(NEST);
        b = ImmutableSet.builder();
        b.addAll(ecobeeTypes);
        b.addAll(honeywellTypes);
        b.addAll(nestTypes);
        thermostatTypes = b.build();
        
        b = ImmutableSet.builder();
        b.addAll(ccuTypes);
        b.addAll(lcuTypes);
        b.addAll(tcuTypes);
        b.addAll(repeaterTypes);
        b.add(TAPTERMINAL);
        b.add(RDS_TERMINAL);
        b.add(RTC);
        b.add(SERIES_5_LMI);
        b.add(SNPP_TERMINAL);
        b.add(TNPP_TERMINAL);
        b.add(WCTP_TERMINAL);
        transmitterTypes = b.build();

        waterMeterTypes = ImmutableSet.of(
        	RFWMETER, 
        	RFW201
        	);
        gasMeterTypes = ImmutableSet.of(
            RFG201,
            RFG301
            );

        loadGroupSupportingRoute = ImmutableSet.of(
            LM_GROUP_EXPRESSCOMM,
            LM_GROUP_EMETCON,
            LM_GROUP_GOLAY,
            LM_GROUP_MCT,
            LM_GROUP_RIPPLE,
            LM_GROUP_VERSACOM
          );
        
        rfElectricTypes = Sets.difference(rfMeterTypes, Sets.union(waterMeterTypes, gasMeterTypes)).immutableCopy();
    }
    
    /**
     * Looks up the PaoType based on its Java constant ID.
     * 
     * @param paoTypeId
     * @return
     * @throws IllegalArgumentException - if no match
     */
    public static PaoType getForId(int paoTypeId) throws IllegalArgumentException {
        PaoType deviceType = lookupById.get(paoTypeId);
        checkArgument(deviceType != null, Integer.toString(paoTypeId));
        return deviceType;
    }
    
    /**
     * Looks up the the PaoType based on the string that is stored in the PAObject table.
     * 
     * NOTE: Use YukonJdbcConnection or YukonResultSet.getEnum instead when retrieving a pao type
     * from a result set.  This method should only be used for retrieving pao types from 
     * messaging classes holding pao type strings.
     * 
     * @param dbString - type name to lookup, case insensitive
     * @return
     * @throws IllegalArgumentException - if no match
     */
    public static PaoType getForDbString(String dbString) throws IllegalArgumentException {
        PaoType deviceType = lookupByDbString.get(dbString.toUpperCase().trim());
        checkArgument(deviceType != null, dbString);
        return deviceType;
    }
    
    public boolean isDirectProgram() {
        return directProgramTypes.contains(this);
    }
    
    public boolean isLmProgram() {
        return lmProgramTypes.contains(this);
    }
    
    public boolean isCbc() {
        return cbcTypes.contains(this);
    }

    public boolean isRegulator() {
        return regulatorTypes.contains(this);
    }
    
    public boolean isCcu() {
        return ccuTypes.contains(this);
    }

    public boolean isTcu() {
        return tcuTypes.contains(this);
    }

    public boolean isLcu() {
        return lcuTypes.contains(this);
    }

    public boolean isRepeater() {
        return repeaterTypes.contains(this);
    }
    
    public boolean isEcobee() {
        return ecobeeTypes.contains(this);
    }
    
    public boolean isHoneywell() {
        return honeywellTypes.contains(this);
    }
    
    public boolean isNest() {
        return nestTypes.contains(this);
    }
    
    public boolean isMeter() {
        return meterTypes.contains(this);
    }
    
    public boolean isIed() {
        return iedTypes.contains(this);
    }
    
    public boolean isMct() {
        return mctTypes.contains(this);
    }
    
    public boolean isRtu() {
        return rtuTypes.contains(this);
    }

    public boolean isIon() {
        return ionTypes.contains(this);
    }
    
    public boolean isIpc() {
        return ipcTypes.contains(this);
    }
    
    public boolean isRfn() {
        return paoClass == PaoClass.RFMESH;
    }
    
    public boolean isRfRelay() {
        return rfRelayTypes.contains(this);
    }
    
    public boolean isPlc() {
        return paoClass == PaoClass.CARRIER;
    }
    
    public boolean isTwoWayRfnLcr() {
        return twoWayLcrTypes.contains(this) 
                && isRfn();
    }
    
    public boolean isTwoWayPlcLcr() {
        return twoWayLcrTypes.contains(this) 
                && paoClass == PaoClass.CARRIER;
    }

    public boolean isTwoWayLcr() {
        return twoWayLcrTypes.contains(this);
    }
    
    public boolean isRfLcr() {
        return rfLcrTypes.contains(this);
    }
    
    public boolean isTransmitter() {
        return PaoClass.TRANSMITTER == paoClass;
    }
    
    public boolean isCapControl() {
        return PaoClass.CAPCONTROL == paoClass;
    }

    public static Set<PaoType> getCapControlTypes() {
        return capControlTypes;
    }

    public static boolean isCapControl(LiteYukonPAObject lite) {
        boolean isCategoryCapControl = lite.getPaoType().getPaoCategory() == PaoCategory.CAPCONTROL;
        boolean isCategoryDevice = lite.getPaoType().getPaoCategory() == PaoCategory.DEVICE;
        boolean isClassCapControl = lite.getPaoType().getPaoClass() == PaoClass.CAPCONTROL;
        return isCategoryCapControl || isCategoryDevice && isClassCapControl;
    }
    
    public boolean isPort() {
        return portTypes.contains(this);
    }

    public boolean isDialupPort() {
        return DIALOUT_POOL == this || LOCAL_DIALUP == this
            || LOCAL_DIALBACK == this || TSERVER_DIALUP == this;
    }

    public boolean isTcpPortEligible() {
        return CBC_7020 == this || CBC_7022 == this || CBC_7023 == this || CBC_7024 == this || CBC_8020 == this
            || CBC_8024 == this || CBC_DNP == this || RTU_DNP == this || FAULT_CI == this || NEUTRAL_MONITOR == this;
    }

    public boolean isLoadManagement() {
        return PaoClass.GROUP == paoClass || PaoClass.LOADMANAGEMENT == paoClass;
    }

    public boolean isWaterMeter() {
        return waterMeterTypes.contains(this);
    }
    
    public boolean isGasMeter() {
        return gasMeterTypes.contains(this);
    }
    
    public boolean isLoadGroup() {
        return paoCategory == PaoCategory.DEVICE && paoClass == PaoClass.GROUP;
    }
    
    public boolean isRoutable() {
        return routableTypes.contains(this);
    }
    
    public boolean isLogicalCBC() {
        return CBC_LOGICAL == this;
    }
    
    public boolean supportsPqr() {
        return this == LCR6700_RFN;
    }
    
    private PaoType(int deviceTypeId, String dbString, PaoCategory paoCategory, PaoClass paoClass) {
        this.deviceTypeId = deviceTypeId;
        this.dbString = dbString;
        this.paoCategory = paoCategory;
        this.paoClass = paoClass;
    }

    public int getDeviceTypeId() {
        return deviceTypeId;
    }

    public String getDbString() {
        return dbString;
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return getDbString();
    }

    public PaoCategory getPaoCategory() {
        return paoCategory;
    }

    public PaoClass getPaoClass() {
        return paoClass;
    }

    public String getPaoTypeName() {
    	return getDbString();
    }

    public static ImmutableSet<PaoType> getCbcTypes() {
        return cbcTypes;
    }

    public static ImmutableSet<PaoType> getRegulatorTypes() {
        return regulatorTypes;
    }

    public static ImmutableSet<PaoType> getCcuTypes() {
        return ccuTypes;
    }

    public static ImmutableSet<PaoType> getRepeaterTypes() {
        return repeaterTypes;
    }

    public static ImmutableSet<PaoType> getMeterTypes() {
        return meterTypes;
    }
    
    public static ImmutableSet<PaoType> getMctTypes() {
        return mctTypes;
    }
    
    public static ImmutableSet<PaoType> getRfMeterTypes() {
        return rfMeterTypes;
    }
    
    public static ImmutableSet<PaoType> getRfElectricTypes() {
        return rfElectricTypes;
    }
    
    public static ImmutableSet<PaoType> getRfDaTypes() {
        return rfDaTypes;
    }
    
    public boolean isRfMeter() {
        return rfMeterTypes.contains(this);
    }
    
    public boolean isItron() {
        return itronTypes.contains(this);
    }
    
    public boolean isLongMacAddressSupported() {
        return itronTypes.contains(this);
    }
    
    public boolean isRfElectric() {
        return rfElectricTypes.contains(this);
    }
    
    public boolean isRfGateway() {
        return rfGatewayTypes.contains(this);
    }

    public boolean hasMeterNumber() {
        return isMct() || isIed() || isRfMeter();
    }
    
    public boolean isTlvReporting() {
        return tlvReportingTypes.contains(this);
    }
    
    public static ImmutableSet<PaoType> getIedTypes() {
        return iedTypes;
    }
    
    public static ImmutableSet<PaoType> getRtuTypes() {
        return rtuTypes;
    }

    public static ImmutableSet<PaoType> getIonTypes() {
        return ionTypes;
    }
    
    public static ImmutableSet<PaoType> getIpcTypes() {
        return ipcTypes;
    }
    
    public static ImmutableSet<PaoType> getRfLcrTypes() {
        return rfLcrTypes;
    }
    
    public static ImmutableSet<PaoType> getTwoWayPlcLcrTypes() {
        return twoWayPlcLcrTypes;
    }
    
    public static ImmutableSet<PaoType> getRoutableTypes() {
        return routableTypes;
    }
    
    public static ImmutableSet<PaoType> getEcobeeTypes() {
        return ecobeeTypes;
    }
    
    public static ImmutableSet<PaoType> getHoneywellTypes() {
        return honeywellTypes;
    }
    
    public static ImmutableSet<PaoType> getItronTypes() {
        return itronTypes;
    }
    
    public static ImmutableSet<PaoType> getLongMacSupportedTypes() {
        return itronTypes;
    }
    
    public static ImmutableSet<PaoType> getNestTypes() {
        return nestTypes;
    }
    
    public static ImmutableSet<PaoType> getTransmitterTypes() {
        return transmitterTypes;
    }
    
    public static ImmutableSet<PaoType> getRfGatewayTypes() {
        return rfGatewayTypes;
    }
    
    public static ImmutableSet<PaoType> getRfRelayTypes() {
        return rfRelayTypes;
    }
    
    public static ImmutableSet<PaoType> getThermostatTypes() {
        return thermostatTypes;
    }
    
    public static ImmutableSet<PaoType> getPortTypes() {
        return portTypes;
    }

    public static ImmutableSet<PaoType> getTwoWayLcrTypes() {
        return twoWayLcrTypes;
    }

    public static ImmutableSet<PaoType> getTlvReportingTypes() {
        return tlvReportingTypes;
    }

    /**
     * @return List of all LM Group types
     */
    public static List<PaoType> getAllLMGroupTypes() {
        List<PaoType> paoTypes = Arrays.stream(PaoType.values())
                                       .filter(paoType -> paoType.isLoadGroup() && paoType != PaoType.MACRO_GROUP)
                                       .collect(Collectors.toList());
        return paoTypes;
    }

    /**
     * Maps PaoType String IDs to their corresponding integer device IDs.
     * @param typeString
     * @return If typeString does not match a PaoType string, an IllegalArgumentException
     * will be thrown by getForDbString().
     */
    public static int getPaoTypeId(String typeString) {
        PaoType device = getForDbString(typeString);
        return device.getDeviceTypeId();
    }
    
    /**
     * Maps integer device IDs to their corresponding PaoType String IDs.
     * @param typeId
     * @return
     */
    public static String getPaoTypeString(int typeId) {
        PaoType paoTypeObject = getForId(typeId);
        return paoTypeObject.getDbString();
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.pao." + name();
    }

    public static ImmutableSet<PaoType> getSubstationbustypes() {
        return substationBusTypes;
    }

    public static ImmutableSet<PaoType> getWaterMeterTypes() {
        return waterMeterTypes;
    }
    
    public static ImmutableSet<PaoType> getGasMeterTypes() {
        return gasMeterTypes;
    }
    
    public boolean supportsMacroGroup() {
        return (isLoadGroup() && (this != PaoType.LM_GROUP_ECOBEE && this != PaoType.LM_GROUP_HONEYWELL
            && this != PaoType.LM_GROUP_NEST && this != PaoType.LM_GROUP_ITRON));
    }
    
    public boolean isLoadGroupSupportRoute() {
        return (isLoadGroup() && loadGroupSupportingRoute.contains(this));
    }
}
