package com.cannontech.common.pao;

import org.apache.commons.lang.Validate;

import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum PaoType {
    CCU710A(DeviceTypes.CCU710A),
    CCU711(DeviceTypes.CCU711),
    TCU5000(DeviceTypes.TCU5000),
    TCU5500(DeviceTypes.TCU5500),
    LCU415(DeviceTypes.LCU415),
    LCULG(DeviceTypes.LCULG),
    LCU_ER(DeviceTypes.LCU_ER),
    LCU_T3026(DeviceTypes.LCU_T3026),
    ALPHA_PPLUS(DeviceTypes.ALPHA_PPLUS),
    FULCRUM(DeviceTypes.FULCRUM),
    VECTRON(DeviceTypes.VECTRON),
    LANDISGYRS4(DeviceTypes.LANDISGYRS4),
    DAVISWEATHER(DeviceTypes.DAVISWEATHER),
    ALPHA_A1(DeviceTypes.ALPHA_A1),
    DR_87(DeviceTypes.DR_87),
    QUANTUM(DeviceTypes.QUANTUM),
    SIXNET(DeviceTypes.SIXNET),
    REPEATER_800(DeviceTypes.REPEATER_800),
    MCT310(DeviceTypes.MCT310),
    MCT318(DeviceTypes.MCT318),
    MCT360(DeviceTypes.MCT360),
    MCT370(DeviceTypes.MCT370),
    MCT240(DeviceTypes.MCT240),
    MCT248(DeviceTypes.MCT248),
    MCT250(DeviceTypes.MCT250),
    MCT210(DeviceTypes.MCT210),
    REPEATER(DeviceTypes.REPEATER),
    LMT_2(DeviceTypes.LMT_2),
    RTUILEX(DeviceTypes.RTUILEX),
    RTUWELCO(DeviceTypes.RTUWELCO),
    DCT_501(DeviceTypes.DCT_501),
    RTU_DNP(DeviceTypes.RTU_DNP),
    TAPTERMINAL(DeviceTypes.TAPTERMINAL),
    MCT310ID(DeviceTypes.MCT310ID),
    MCT310IL(DeviceTypes.MCT310IL),
    MCT318L(DeviceTypes.MCT318L),
    MCT213(DeviceTypes.MCT213),
    WCTP_TERMINAL(DeviceTypes.WCTP_TERMINAL),
    MCT310CT(DeviceTypes.MCT310CT),
    MCT310IM(DeviceTypes.MCT310IM),
    LM_GROUP_EMETCON(DeviceTypes.LM_GROUP_EMETCON),
    LM_GROUP_VERSACOM(DeviceTypes.LM_GROUP_VERSACOM),
    LM_GROUP_EXPRESSCOMM(DeviceTypes.LM_GROUP_EXPRESSCOMM),
    LM_GROUP_RIPPLE(DeviceTypes.LM_GROUP_RIPPLE),
    LM_DIRECT_PROGRAM(DeviceTypes.LM_DIRECT_PROGRAM),
    LM_CURTAIL_PROGRAM(DeviceTypes.LM_CURTAIL_PROGRAM),
    LM_CONTROL_AREA(DeviceTypes.LM_CONTROL_AREA),
    LM_ENERGY_EXCHANGE_PROGRAM(DeviceTypes.LM_ENERGY_EXCHANGE_PROGRAM),
    MACRO_GROUP(DeviceTypes.MACRO_GROUP),
    CAPBANK(DeviceTypes.CAPBANK),
    CAPBANKCONTROLLER(DeviceTypes.CAPBANKCONTROLLER),
    VIRTUAL_SYSTEM(DeviceTypes.VIRTUAL_SYSTEM),
    CBC_FP_2800(DeviceTypes.CBC_FP_2800),
    LM_GROUP_POINT(DeviceTypes.LM_GROUP_POINT),
    DNP_CBC_6510(DeviceTypes.DNP_CBC_6510),
    SYSTEM(DeviceTypes.SYSTEM),
    EDITABLEVERSACOMSERIAL(DeviceTypes.EDITABLEVERSACOMSERIAL),
    MCTBROADCAST(DeviceTypes.MCTBROADCAST),
    ION_7700(DeviceTypes.ION_7700),
    ION_8300(DeviceTypes.ION_8300),
    ION_7330(DeviceTypes.ION_7330),
    RTU_DART(DeviceTypes.RTU_DART),
    MCT310IDL(DeviceTypes.MCT310IDL),
    LM_GROUP_MCT(DeviceTypes.LM_GROUP_MCT),
    MCT410IL(DeviceTypes.MCT410IL),
    TRANSDATA_MARKV(DeviceTypes.TRANSDATA_MARKV),
    LM_GROUP_SA305(DeviceTypes.LM_GROUP_SA305),
    LM_GROUP_SA205(DeviceTypes.LM_GROUP_SA205),
    LM_GROUP_SADIGITAL(DeviceTypes.LM_GROUP_SADIGITAL),
    LM_GROUP_GOLAY(DeviceTypes.LM_GROUP_GOLAY),
    SERIES_5_LMI(DeviceTypes.SERIES_5_LMI),
    RTC(DeviceTypes.RTC),
    LM_SCENARIO(DeviceTypes.LM_SCENARIO),
    KV(DeviceTypes.KV),
    KVII(DeviceTypes.KVII),
    RTM(DeviceTypes.RTM),
    CBC_EXPRESSCOM(DeviceTypes.CBC_EXPRESSCOM),
    SENTINEL(DeviceTypes.SENTINEL),
    ALPHA_A3(DeviceTypes.ALPHA_A3),
    MCT470(DeviceTypes.MCT470),
    MCT410CL(DeviceTypes.MCT410CL),
    CBC_7010(DeviceTypes.CBC_7010),
    CBC_7020(DeviceTypes.CBC_7020),
    SNPP_TERMINAL(DeviceTypes.SNPP_TERMINAL),
    RTU_MODBUS(DeviceTypes.RTU_MODBUS),
    MCT430A(DeviceTypes.MCT430A),
    MCT430S4(DeviceTypes.MCT430S4),
    CBC_7022(DeviceTypes.CBC_7022),
    CBC_7023(DeviceTypes.CBC_7023),
    CBC_7024(DeviceTypes.CBC_7024),
    CBC_7011(DeviceTypes.CBC_7011),
    CBC_7012(DeviceTypes.CBC_7012),
    REPEATER_801(DeviceTypes.REPEATER_801),
    REPEATER_921(DeviceTypes.REPEATER_921),
    MCT410FL(DeviceTypes.MCT410FL),
    MCT410GL(DeviceTypes.MCT410GL),
    MCT430SL(DeviceTypes.MCT430SL),
    CCU721(DeviceTypes.CCU721),
    SIMPLE_SCHEDULE(DeviceTypes.SIMPLE_SCHEDULE),
    SCRIPT(DeviceTypes.SCRIPT),
    REPEATER_902(DeviceTypes.REPEATER_902),
    FAULT_CI(DeviceTypes.FAULT_CI),
    NEUTRAL_MONITOR(DeviceTypes.NEUTRAL_MONITOR),
    CBC_DNP(DeviceTypes.CBC_DNP),
    LM_GROUP_XML(DeviceTypes.LM_GROUP_XML),
    XML_TRANSMITTER(DeviceTypes.XML_TRANSMITTER),
    MCT430A3(DeviceTypes.MCT430A3),
    LCR3102(DeviceTypes.LCR3102),
    ;
    
    
    private final int deviceTypeId;
    
    private final static ImmutableMap<Integer, PaoType> lookup;
    
    static {
        Builder<Integer, PaoType> builder = ImmutableMap.builder();
        for (PaoType deviceType : values()) {
            builder.put(deviceType.deviceTypeId, deviceType);
        }
        lookup = builder.build();
    }

    public static PaoType getForId(int deviceTypeId) throws IllegalArgumentException {
        PaoType deviceType = lookup.get(deviceTypeId);
        Validate.notNull(deviceType, Integer.toString(deviceTypeId));
        return deviceType;
    }
    
    private PaoType(int deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public int getDeviceTypeId() {
        return deviceTypeId;
    }
    
    public String getPaoTypeName() {
    	return PAOGroups.getPAOTypeString(this.deviceTypeId);
    }
}
