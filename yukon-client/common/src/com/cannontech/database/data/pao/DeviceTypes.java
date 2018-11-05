package com.cannontech.database.data.pao;


/**
 * Insert the type's description here.
 * Creation date: (10/2/2001 1:45:23 PM)
 * @author:
 */
public interface DeviceTypes extends TypeBase
{
    public final static int CCU710A       = DEVICE_OFFSET + 1;
    public final static int CCU711        = DEVICE_OFFSET + 2;
    public final static int TCU5000       = DEVICE_OFFSET + 3;
    public final static int TCU5500       = DEVICE_OFFSET + 4;
    public final static int LCU415        = DEVICE_OFFSET + 5;
    public final static int LCULG         = DEVICE_OFFSET + 6;
    public final static int LCU_ER        = DEVICE_OFFSET + 7;
    public final static int LCU_T3026     = DEVICE_OFFSET + 8;
    public final static int ALPHA_PPLUS   = DEVICE_OFFSET + 9; //replaced ALPHA
    public final static int FULCRUM       = DEVICE_OFFSET + 10;  // replaces SCHLUMBERGER
    public final static int VECTRON       = DEVICE_OFFSET + 11;    // is a type of SCHLUMBERGER
    public final static int LANDISGYRS4   = DEVICE_OFFSET + 12;
    public final static int DAVISWEATHER  = DEVICE_OFFSET + 13;
    public final static int ALPHA_A1      = DEVICE_OFFSET + 14;
    public final static int DR_87         = DEVICE_OFFSET + 15;
    public final static int QUANTUM       = DEVICE_OFFSET + 16;
    public final static int SIXNET        = DEVICE_OFFSET + 17;
    public final static int REPEATER_800  = DEVICE_OFFSET + 18;
    public final static int MCT310        = DEVICE_OFFSET + 19;
    public final static int MCT318        = DEVICE_OFFSET + 20;
    public final static int MCT360        = DEVICE_OFFSET + 21;
    public final static int MCT370        = DEVICE_OFFSET + 22;
    public final static int MCT240        = DEVICE_OFFSET + 23;
    public final static int MCT248        = DEVICE_OFFSET + 24;
    public final static int MCT250        = DEVICE_OFFSET + 25;
    public final static int MCT210        = DEVICE_OFFSET + 26;
    public final static int REPEATER      = DEVICE_OFFSET + 27;
    public final static int LMT_2         = DEVICE_OFFSET + 28;
    public final static int RTUILEX       = DEVICE_OFFSET + 29;
    public final static int RTUWELCO      = DEVICE_OFFSET + 30;
    public final static int DCT_501       = DEVICE_OFFSET + 31;
    public final static int RTU_DNP       = DEVICE_OFFSET + 32;

    public final static int TAPTERMINAL    = DEVICE_OFFSET + 33;
    public final static int MCT310ID       = DEVICE_OFFSET + 34;
    public final static int MCT310IL       = DEVICE_OFFSET + 35;
    public final static int MCT318L        = DEVICE_OFFSET + 36;
    public final static int MCT213         = DEVICE_OFFSET + 37;
    public final static int WCTP_TERMINAL  = DEVICE_OFFSET + 38;
    public final static int MCT310CT       = DEVICE_OFFSET + 39;
    public final static int MCT310IM       = DEVICE_OFFSET + 40;

    public final static int LM_GROUP_EMETCON             = DEVICE_OFFSET + 41;
    public final static int LM_GROUP_VERSACOM            = DEVICE_OFFSET + 42;
    public final static int LM_GROUP_EXPRESSCOMM         = DEVICE_OFFSET + 43;
    public final static int LM_GROUP_RIPPLE              = DEVICE_OFFSET + 44;
    public final static int LM_DIRECT_PROGRAM            = DEVICE_OFFSET + 45;
    public final static int LM_CURTAIL_PROGRAM           = DEVICE_OFFSET + 46;
    public final static int LM_CONTROL_AREA              = DEVICE_OFFSET + 47;
    public final static int LM_ENERGY_EXCHANGE_PROGRAM   = DEVICE_OFFSET + 48;
    public final static int MACRO_GROUP                  = DEVICE_OFFSET + 49;
    public final static int CAPBANK                      = DEVICE_OFFSET + 50;
    public final static int CAPBANKCONTROLLER            = DEVICE_OFFSET + 51;
    public final static int VIRTUAL_SYSTEM               = DEVICE_OFFSET + 52;
    public final static int CBC_FP_2800                  = DEVICE_OFFSET + 53;
    public final static int LM_GROUP_POINT               = DEVICE_OFFSET + 54;
    public final static int SYSTEM                       = DEVICE_OFFSET + 56;
    public final static int EDITABLEVERSACOMSERIAL       = DEVICE_OFFSET + 57;
    public final static int MCTBROADCAST                 = DEVICE_OFFSET + 58;
    public final static int ION_7700                     = DEVICE_OFFSET + 59;
    public final static int ION_8300                     = DEVICE_OFFSET + 60;
    public final static int ION_7330                     = DEVICE_OFFSET + 61;

    public final static int RTU_DART                     = DEVICE_OFFSET + 62;
    public final static int MCT310IDL                    = DEVICE_OFFSET + 63;
    public final static int LM_GROUP_MCT                      = DEVICE_OFFSET + 64;

    public final static int MCT410IL                 = DEVICE_OFFSET + 65;
    public final static int TRANSDATA_MARKV           = DEVICE_OFFSET + 66;

    public final static int LM_GROUP_SA305            = DEVICE_OFFSET + 67;
    public final static int LM_GROUP_SA205            = DEVICE_OFFSET + 68;
    public final static int LM_GROUP_SADIGITAL        = DEVICE_OFFSET + 69;
    public final static int LM_GROUP_GOLAY            = DEVICE_OFFSET + 70;

    public final static int SERIES_5_LMI            = DEVICE_OFFSET + 71;
    public final static int RTC                        = DEVICE_OFFSET + 72;

    //not actually a device, but useful to throw it in here
    public final static int LM_SCENARIO                = DEVICE_OFFSET + 73;

    public final static int KV                         = DEVICE_OFFSET + 74;
    public final static int KVII                    = DEVICE_OFFSET + 75;

    public final static int RTM                        = DEVICE_OFFSET + 76;

    public final static int CBC_EXPRESSCOM            = DEVICE_OFFSET + 77;

    public final static int SENTINEL                = DEVICE_OFFSET + 78;
    public final static int ALPHA_A3                = DEVICE_OFFSET + 79;
    public final static int MCT470                    = DEVICE_OFFSET + 80;
    public final static int MCT410CL                 = DEVICE_OFFSET + 81;

    public final static int CBC_7010                = DEVICE_OFFSET + 82;
    public final static int CBC_7020                = DEVICE_OFFSET + 83;



    public final static int SNPP_TERMINAL            = DEVICE_OFFSET + 84;
    public final static int RTU_MODBUS                = DEVICE_OFFSET + 85;
    public final static int MCT430A                  = DEVICE_OFFSET + 86;
    public final static int MCT430S4                  = DEVICE_OFFSET + 87;


    public final static int CBC_7022                = DEVICE_OFFSET + 88;
    public final static int CBC_7023                = DEVICE_OFFSET + 89;
    public final static int CBC_7024                = DEVICE_OFFSET + 90;

    public final static int CBC_7011                = DEVICE_OFFSET + 91;
    public final static int CBC_7012                = DEVICE_OFFSET + 92;
    public final static int REPEATER_801        = DEVICE_OFFSET + 93;
    public final static int REPEATER_921        = DEVICE_OFFSET + 94;

    public final static int MCT410FL                = DEVICE_OFFSET + 95;
    public final static int MCT410GL                = DEVICE_OFFSET + 96;

    public final static int MCT430SL                  = DEVICE_OFFSET + 97;

    public final static int CCU721                  = DEVICE_OFFSET + 98;

    public final static int SIMPLE_SCHEDULE                = DEVICE_OFFSET + 99;
    public final static int SCRIPT                        = DEVICE_OFFSET + 100;

    public final static int REPEATER_902      = DEVICE_OFFSET + 101;
    public final static int FAULT_CI          = DEVICE_OFFSET + 102;
    public final static int NEUTRAL_MONITOR   = DEVICE_OFFSET + 103;
    public final static int CBC_DNP           = DEVICE_OFFSET + 104;

    public final static int MCT430A3                  = DEVICE_OFFSET + 107;

    public final static int LCR3102                 = DEVICE_OFFSET + 108;
    public final static int TNPP_TERMINAL    = DEVICE_OFFSET + 109;
    public final static int FOCUS             = DEVICE_OFFSET + 110;
    public final static int REPEATER_850     = DEVICE_OFFSET + 111;


    //public final static int RFN_AX    = DEVICE_OFFSET + 112;
    //public final static int RFN_AL    = DEVICE_OFFSET + 113;
    public final static int RDS_TERMINAL    = DEVICE_OFFSET + 114;
    public final static int MCT420FL        = DEVICE_OFFSET + 115;
    public final static int MCT420FD        = DEVICE_OFFSET + 116;
    public final static int MCT420CL        = DEVICE_OFFSET + 117;
    public final static int MCT420CD        = DEVICE_OFFSET + 118;
    //public final static int RFN_AXSD    = DEVICE_OFFSET + 119;
    public final static int RFN410FL = DEVICE_OFFSET + 120;
    public final static int RFN410FX = DEVICE_OFFSET + 121;
    public final static int RFN410FD = DEVICE_OFFSET + 122;
    public final static int RFN430A3D = DEVICE_OFFSET + 123;
    public final static int RFN430KV = DEVICE_OFFSET + 124;

    public final static int ZIGBEE_ENDPOINT = DEVICE_OFFSET + 125;
    public final static int DIGI_GATEWAY = DEVICE_OFFSET + 126;
    public final static int LM_SEP_PROGRAM = DEVICE_OFFSET + 127;
    public final static int LM_GROUP_DIGI_SEP  = DEVICE_OFFSET + 128;

    public final static int RFWMETER = DEVICE_OFFSET + 129;

    public final static int CBC_8020 = DEVICE_OFFSET + 130;
    public final static int CBC_8024 = DEVICE_OFFSET + 131;

    // RFN420FRX and RFN420FRD are below at +153/+154
    public final static int RFN420FL = DEVICE_OFFSET + 132;
    public final static int RFN420FX = DEVICE_OFFSET + 133;
    public final static int RFN420FD = DEVICE_OFFSET + 134;
    public final static int RFN420CL = DEVICE_OFFSET + 135;
    public final static int RFN420CD = DEVICE_OFFSET + 136;

    public final static int IPC430S4E = DEVICE_OFFSET + 137;
    public final static int IPC430SL = DEVICE_OFFSET + 138;
    public final static int IPC420FD = DEVICE_OFFSET + 139;
    public final static int IPC410FL = DEVICE_OFFSET + 140;
    public final static int LCR6200_RFN = DEVICE_OFFSET + 141;
    public final static int LCR6600_RFN = DEVICE_OFFSET + 142;

    //public final static int RFN420ELO = DEVICE_OFFSET + 143;
    //public final static int RFN430ELO = DEVICE_OFFSET + 144;

    public final static int LM_GROUP_RFN_EXPRESSCOMM = DEVICE_OFFSET +145;

    public final static int MCT440_2131B = DEVICE_OFFSET + 146;
    public final static int MCT440_2132B = DEVICE_OFFSET + 147;
    public final static int MCT440_2133B = DEVICE_OFFSET + 148;

    // RFN430A3D is above (offset + 123);
    public final static int RFN430A3K = DEVICE_OFFSET + 149;
    public final static int RFN430A3T = DEVICE_OFFSET + 150;
    public final static int RFN430A3R = DEVICE_OFFSET + 151;
    
    // Other RFN410__ models are above (offset + 120)
    public final static int RFN410CL = DEVICE_OFFSET + 152;

    // RFN420F* models above around (offset + 132)
    public final static int RFN420FRX = DEVICE_OFFSET + 153;
    public final static int RFN420FRD = DEVICE_OFFSET + 154;
    
//    public final static int RFN440_2131T = DEVICE_OFFSET + 155;
    public final static int RFN440_2131TD = DEVICE_OFFSET + 156;
//    public final static int RFN440_2132T = DEVICE_OFFSET + 157;
    public final static int RFN440_2132TD = DEVICE_OFFSET + 158;
//    public final static int RFN440_2133T = DEVICE_OFFSET + 159;
    public final static int RFN440_2133TD = DEVICE_OFFSET + 160;
    
    public final static int WEATHER_LOCATION = DEVICE_OFFSET + 161;

    public final static int RFN430SL0 = DEVICE_OFFSET + 162;
    public final static int RFN430SL1 = DEVICE_OFFSET + 163;
    public final static int RFN430SL2 = DEVICE_OFFSET + 164;
    public final static int RFN430SL3 = DEVICE_OFFSET + 165;
    public final static int RFN430SL4 = DEVICE_OFFSET + 166;
    
    public final static int RFN_1200 = DEVICE_OFFSET + 167;
            
    public final static int ECOBEE_SMART_SI = DEVICE_OFFSET + 168;
    public final static int LM_GROUP_ECOBEE = DEVICE_OFFSET + 169;
    public final static int LM_ECOBEE_PROGRAM = DEVICE_OFFSET + 170;
    
    public final static int RFN_GATEWAY = DEVICE_OFFSET + 171;
    
    public final static int ECOBEE_3 = DEVICE_OFFSET + 172;
    public final static int ECOBEE_SMART = DEVICE_OFFSET + 173;
    
    public final static int GWY800 = DEVICE_OFFSET + 174;
    
    public final static int RFN530S4X = DEVICE_OFFSET + 175; 
    public final static int RFN530S4EAX = DEVICE_OFFSET + 176;
    public final static int RFN530S4EAXR = DEVICE_OFFSET + 177;
    public final static int RFN530S4ERX = DEVICE_OFFSET + 178;
    public final static int RFN530S4ERXR = DEVICE_OFFSET + 179;
    
    public final static int RFN510FL = DEVICE_OFFSET + 180;
    public final static int RFN520FAX = DEVICE_OFFSET + 181;
    public final static int RFN520FRX = DEVICE_OFFSET + 182;
    public final static int RFN520FAXD = DEVICE_OFFSET + 183;
    public final static int RFN520FRXD = DEVICE_OFFSET + 184;
    
    public final static int RFN530FAX = DEVICE_OFFSET + 185;
    public final static int RFN530FRX = DEVICE_OFFSET + 186;
    
    public final static int HONEYWELL_9000 = DEVICE_OFFSET + 187;
    public final static int HONEYWELL_VISIONPRO_8000 = DEVICE_OFFSET + 188;
    public final static int HONEYWELL_FOCUSPRO = DEVICE_OFFSET + 189;
    public final static int HONEYWELL_THERMOSTAT = DEVICE_OFFSET + 190;
    public final static int LM_GROUP_HONEYWELL = DEVICE_OFFSET + 191;
    public final static int LM_HONEYWELL_PROGRAM = DEVICE_OFFSET + 192;
    public final static int RFN_RELAY = DEVICE_OFFSET + 193;
    public final static int RFW201 = DEVICE_OFFSET + 194;
    //public final static int RFW205 = DEVICE_OFFSET + 195;
    public final static int ECOBEE_3_LITE = DEVICE_OFFSET + 196;
    public final static int LCR6700_RFN = DEVICE_OFFSET + 197;

    public final static int CBC_LOGICAL = DEVICE_OFFSET + 198;
    
    public final static int RFG201 = DEVICE_OFFSET + 199;
    //public final static int RFG205 = DEVICE_OFFSET + 200;

    public final static int NEST = DEVICE_OFFSET + 201;
    public final static int LM_GROUP_NEST = DEVICE_OFFSET + 202;
    public final static int LM_NEST_PROGRAM = DEVICE_OFFSET + 203;
    
    public final static int LCR6601S = DEVICE_OFFSET + 204;
    
    //all the possible text representation of each device type.
    //If you want the string of any type, just access the zeroth element, example: STRING_CCU_710[0]
    public static final String[] STRING_CCU_710 = {"CCU-710A", "CCU710A"};
    public static final String[] STRING_CCU_711 = {"CCU-711", "CCU711"};
    public static final String[] STRING_CCU_721 = {"CCU-721"};
    public static final String[] STRING_TCU_5000 = {"TCU-5000", "TCU5000"};
    public static final String[] STRING_TCU_5500 = {"TCU-5500", "TCU5500"};
    public static final String[] STRING_LCU_415 = {"LCU-415", "LCU415"};
    public static final String[] STRING_LCU_LG = {"LCU-LG", "LCULG"};
    public static final String[] STRING_LCU_T3026 = {"LCU-T3026", "LCUT3026"};
    public static final String[] STRING_LCU_ER = {"LCU-EASTRIVER"};
    public static final String[] STRING_ALPHA_POWERPLUS = {"ALPHA POWER PLUS", "ALPHA METER"};
    public static final String[] STRING_ALPHA_A1 = {"ALPHA A1"};
    public static final String[] STRING_TRANSDATA_MARKV = {"TRANSDATA MARK-V"};
    public static final String[] STRING_FULCRUM = {"FULCRUM"}; //replaced Schlumberger
    public static final String[] STRING_VECTRON = {"VECTRON"}; //replaced Schlumberger
    public static final String[] STRING_QUANTUM = {"QUANTUM"}; //replaced Schlumberger
    public static final String[] STRING_KV = {"KV"};
    public static final String[] STRING_KVII = {"KV2", "KVII"};
    public static final String[] STRING_LANDISGYR_RS4 = {"LANDIS-GYR S4", "LANDISGYRS4", "LANDIS GYR S4"};
    public static final String[] STRING_DR_87 = {"DR-87", "DR87"};
    public static final String[] STRING_DAVIS_WEATHER = {"DAVIS WEATHER", "DAVISWEATHER"};
    public static final String[] STRING_MCT_318L = {"MCT-318L", "MCT318L"};
    public static final String[] STRING_MCT_310ID = {"MCT-310ID", "MCT310ID"};
    public static final String[] STRING_MCT_310IDL = {"MCT-310IDL", "MCT310IDL"};
    public static final String[] STRING_MCT_310IL = {"MCT-310IL", "MCT310IL"};
    public static final String[] STRING_MCT_310CT = {"MCT-310CT", "MCT310CT"};
    public static final String[] STRING_MCT_310IM = {"MCT-310IM", "MCT310IM"};
    public static final String[] STRING_MCT_310 = {"MCT-310", "MCT310"};
    public static final String[] STRING_MCT_410IL = {"MCT-410iL", "MCT410IL", "MCT-410iLE", "MCT-410 kWh Only"};
    public static final String[] STRING_MCT_410CL = {"MCT-410cL", "MCT410CL"};
    public static final String[] STRING_MCT_410FL = {"MCT-410fL"};
    public static final String[] STRING_MCT_410GL = {"MCT-410gL"};

    public static final String[] STRING_MCT_440_2131B = {"MCT-440-2131B"};
    public static final String[] STRING_MCT_440_2132B = {"MCT-440-2132B"};
    public static final String[] STRING_MCT_440_2133B = {"MCT-440-2133B"};

    public static final String[] STRING_MCT_470 = {"MCT-470", "MCT470"};
    public static final String[] STRING_MCT_430A = {"MCT-430A"};
    public static final String[] STRING_MCT_430S4 = {"MCT-430S4"};
    public static final String[] STRING_MCT_430SL = {"MCT-430SL"};
    public static final String[] STRING_MCT_430A3 = {"MCT-430A3"};
    public static final String[] STRING_MCT_318 = {"MCT-318", "MCT318"};
    public static final String[] STRING_MCT_360 = {"MCT-360", "MCT360"};
    public static final String[] STRING_MCT_370 = {"MCT-370", "MCT370"};
    public static final String[] STRING_MCT_240 = {"MCT-240", "MCT240"};
    public static final String[] STRING_MCT_248 = {"MCT-248", "MCT248"};
    public static final String[] STRING_MCT_250 = {"MCT-250", "MCT250"};
    public static final String[] STRING_MCT_210 = {"MCT-210", "MCT210"};
    public static final String[] STRING_MCT_213 = {"MCT-213", "MCT213"};
    public static final String[] STRING_MCT_BROADCAST = {"MCT Broadcast", "MCT-Broadcast", "MCT_Broadcast", "MCT_Broadcast"};

    public static final String[] STRING_LCR_3102 = {"LCR-3102"};
    public static final String[] STRING_LCR_6200_RFN = {"LCR-6200 RFN"};
    public static final String[] STRING_LCR_6600_RFN = {"LCR-6600 RFN"};
    public static final String[] STRING_LCR_6700_RFN = {"LCR-6700 RFN"};
    
    public static final String[] STRING_LCR_6601S = {"LCR-6601S"};

    public static final String[] STRING_REPEATER = {"REPEATER", "REPEATER 900"};
    public static final String[] STRING_REPEATER_902 = {"REPEATER 902"};
    public static final String[] STRING_RTU_ILEX = {"RTU-ILEX", "RTUILEX"};
    public static final String[] STRING_RTU_DNP = {"RTU-DNP", "RTUDNP"};
    public static final String[] STRING_RTU_MODBUS = {"RTU-MODBUS", "RTUMODBUS"};
    public static final String[] STRING_RTU_DART = {"RTU-DART", "RTUDART"};
    public static final String[] STRING_RTU_WELCO = {"RTU-WELCO", "RTUWELCO"};
    //general RTU - used by point picker to get status point for ANY RTU
    public static final String[] STRING_RTU_X = {
                                                    STRING_RTU_WELCO[0],STRING_RTU_WELCO[1],
                                                    STRING_RTU_DART[0],STRING_RTU_DART[1],
                                                    STRING_RTU_MODBUS[0],STRING_RTU_MODBUS[1],
                                                    STRING_RTU_DNP[0],STRING_RTU_DNP[1],
                                                    STRING_RTU_ILEX[0],STRING_RTU_ILEX [1]
                                                };

    public static final String[] STRING_TAP_TERMINAL = {"TAP TERMINAL", "TAPTERMINAL", "PAGING TAP TERMINAL"};
    public static final String[] STRING_TNPP_TERMINAL = {"TNPP TERMINAL"};
    public static final String[] STRING_WCTP_TERMINAL = {"WCTP TERMINAL"};
    public static final String[] STRING_SNPP_TERMINAL = {"SNPP TERMINAL"};
    public static final String[] STRING_RDS_TERMINAL = {"RDS TERMINAL"};
    public static final String[] STRING_EMETCON_GROUP = {"EMETCON GROUP"};
    public static final String[] STRING_VERSACOM_GROUP = {"VERSACOM GROUP"};
    public static final String[] STRING_EXPRESSCOMM_GROUP = {"EXPRESSCOM GROUP"};
    public static final String[] STRING_RFN_EXPRESSCOMM_GROUP = {"RFN EXPRESSCOM GROUP"};
    public static final String[] STRING_DIGI_SEP_GROUP = {"DIGI SEP GROUP"};
    public static final String[] STRING_ECOBEE_GROUP = {"ECOBEE GROUP"};
    public static final String[] STRING_HONEYWELL_GROUP = {"HONEYWELL GROUP"};
    public static final String[] STRING_NEST_GROUP = {"NEST GROUP"};
    public static final String[] STRING_POINT_GROUP = {"POINT GROUP"};
    public static final String[] STRING_RIPPLE_GROUP = {"RIPPLE GROUP"};
    public static final String[] STRING_MACRO_GROUP = {"MACRO GROUP"};
    public static final String[] STRING_CAP_BANK = {"CAP BANK", "CAPBANK"};
    public static final String[] STRING_CAP_BANK_CONTROLLER = { "CBC Versacom", "CBC", "CAPBANKCONTROLLER" };
    public static final String[] STRING_CBC_EXPRESSCOM = { "CBC Expresscom" };
    public static final String[] STRING_SYSTEM = {"SYSTEM"};
    public static final String[] STRING_VIRTUAL_SYSTEM = {"VIRTUAL SYSTEM", "VIRTUALSYSTEM"};
    public static final String[] STRING_LM_DIRECT_PROGRAM = {"LM DIRECT PROGRAM"};
    public static final String[] STRING_LM_SEP_PROGRAM = {"LM SEP PROGRAM"};
    public static final String[] STRING_LM_ECOBEE_PROGRAM = {"LM ECOBEE PROGRAM"};
    public static final String[] STRING_LM_HONEYWELL_PROGRAM = {"LM HONEYWELL PROGRAM"};
    public static final String[] STRING_LM_NEST_PROGRAM = {"LM NEST PROGRAM"};
    public static final String[] STRING_LM_CURTAIL_PROGRAM = {"LM CURTAIL PROGRAM"};
    public static final String[] STRING_LM_CONTROL_AREA = {"LM CONTROL AREA"};
    public static final String[] STRING_LM_SCENARIO = {"LMSCENARIO"};
    public static final String[] STRING_VERSACOM_SERIAL_NUMBER = {"VERSACOM SERIAL #"};
    public static final String[] STRING_LMT_2 = {"LMT-2", "LMT 2"};
    public static final String[] STRING_DCT_501 = {"DCT-501"};
    public static final String[] STRING_CBC_FP_2800 = {"CBC FP-2800", "CBC FP 2800"};
    public static final String[] STRING_REPEATER_800 = {"REPEATER 800"};
    public static final String[] STRING_REPEATER_801 = {"REPEATER 801"};
    public static final String[] STRING_REPEATER_850 = {"REPEATER 850"};
    public static final String[] STRING_REPEATER_921 = {"REPEATER 921"};
    public static final String[] STRING_LM_ENERGY_EXCHANGE_PROGRAM = {"LM ENERGY EXCHANGE", "ENERGY EXCHANGE" };
    public static final String[] STRING_SIXNET = {"SIXNET"};
    public static final String[] STRING_ION_7700 = {"ION-7700", "ION 7700" };
    public static final String[] STRING_ION_8300 = {"ION-8300", "ION 8300" };
    public static final String[] STRING_ION_7330 = {"ION-7330", "ION 7330" };
    public static final String[] STRING_MCT_GROUP = {"MCT GROUP", "MCT-GROUP"};
    public static final String[] STRING_SA305_GROUP = {"SA-305 Group"};
    public static final String[] STRING_SA205_GROUP = {"SA-205 Group"};
    public static final String[] STRING_SADIGITAL_GROUP = {"SA-Digital Group"};
    public static final String[] STRING_GOLAY_GROUP = {"Golay Group"};
    public static final String[] STRING_SERIES_5_LMI = {"RTU-LMI", "RTU LMI"};
    public static final String[] STRING_RTC = {"RTC"};
    public static final String[] STRING_RTM = {"RTM"};
    public static final String[] STRING_SENTINEL = {"SENTINEL"};
    public static final String[] STRING_FOCUS = {"FOCUS"};
    public static final String[] STRING_ALPHA_A3 = {"ALPHA A3"};
    public static final String[] STRING_CBC_7010 = {"CBC 7010", "CBC 7011", "CBC 7012"};
    public static final String[] STRING_CBC_7020 = {"CBC 7020", "CBC 7022", "CBC 7023", "CBC 7024"};
    public static final String[] STRING_CBC_DNP  = {"CBC DNP"};
    public static final String[] STRING_NEUTRAL_MONITOR = { "Capacitor Bank Neutral Monitor" };
    public static final String[] STRING_FAULT_CI = { "Faulted Circuit Indicator" };
    public static final String[] STRING_MCT_420FL = {"MCT-420fL"};
    public static final String[] STRING_MCT_420FD = {"MCT-420fD"};
    public static final String[] STRING_MCT_420CL = {"MCT-420cL"};
    public static final String[] STRING_MCT_420CD = {"MCT-420cD"};
    public static final String[] STRING_IPC_430S4 = {"IPC-430S4"};
    public static final String[] STRING_IPC_430SL = {"IPC-430SL"};
    public static final String[] STRING_IPC_420FD = {"IPC-420fD"};
    public static final String[] STRING_IPC_410FL = {"IPC-410fL"};
    public static final String[] STRING_WEATHER_LOCATION = {"WEATHER LOCATION"};
    public static final String[] STRING_ECOBEE_SMART_SI = {"ecobee Smart Si"};
    public static final String[] STRING_ECOBEE_3 = {"ecobee3"};
    public static final String[] STRING_ECOBEE_3_LITE = {"ecobee3 Lite"};
    public static final String[] STRING_RFN_GATEWAY = {"RFN Gateway"};
    public static final String[] STRING_RFN_GATEWAY_2 = {"RFN Gateway 2"};
    public static final String[] STRING_HONEYWELL_9000 = {"Honeywell Wi-Fi 9000"};
    public static final String[] STRING_HONEYWELL_VISIONPRO_8000 = {"Honeywell Wi-Fi VisionPRO 8000"};
    public static final String[] STRING_HONEYWELL_FOCUSPRO = {"Honeywell Wi-Fi FocusPRO"};
    public static final String[] STRING_HONEYWELL_THERMOSTAT = {"Honeywell Wi-Fi Thermostat"};
    public static final String[] STRING_NEST = {"Nest"};
    public static final String[] STRING_RFN_RELAY = {"RFN Relay"};
    public static final String[] STRING_RFW201 = {"RFW-201", "RFW 201" };
    public static final String[] STRING_CBC_LOGICAL  = {"CBC Logical"};
    public static final String[] STRING_RFG201 = {"RFG-201", "RFG 201" };
}