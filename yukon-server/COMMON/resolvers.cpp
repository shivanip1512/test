#include "precompiled.h"

#include "dsm2.h"
#include "resolvers.h"
#include "devicetypes.h"
#include "pointtypes.h"
#include "logger.h"
#include "numstr.h"
#include "utility.h"

using std::transform;
using std::endl;
using std::string;


INT resolveRouteType( const string& _rwsTemp)
{
    INT Ret = 0;
    string rwsTemp = _rwsTemp;
    CtiToLower(rwsTemp);

    in_place_trim(rwsTemp);
    if(rwsTemp == "ccu")
    {
        Ret = RouteTypeCCU;
    }
    else if(rwsTemp == "tcu")
    {
        Ret = RouteTypeTCU;
    }
    else if(rwsTemp == "macro")
    {
        Ret = RouteTypeMacro;
    }
    else if(rwsTemp == "lcu")
    {
        Ret = RouteTypeLCU;
    }
    else if(rwsTemp == "versacom")
    {
        Ret = RouteTypeVersacom;
    }
    else if(rwsTemp == "expresscom")
    {
        Ret = RouteTypeExpresscom;
    }
    else if(rwsTemp == "tap paging" || rwsTemp == "tappaging")
    {
        Ret = RouteTypeTap;
    }
    else if(rwsTemp == "rds terminal route")
    {
        Ret = RouteTypeRDS;
    }
    else if(rwsTemp == "snpp terminal route")
    {
        Ret = RouteTypeSNPP;
    }
    else if(rwsTemp == "wctp terminal route")
    {
        Ret = RouteTypeWCTP;
    }
    else if(rwsTemp == "tnpp terminal route")
    {
        Ret = RouteTypeTNPP;
    }
    else if(rwsTemp == "rtc route")
    {
        Ret = RouteTypeRTC;
    }
    else if(rwsTemp == "series 5 lmi")
    {
        Ret = RouteTypeSeriesVLMI;
    }
    else
    {
        Ret = RouteTypeInvalid;
    }

    return Ret;
}

INT resolveAmpUseType(const string& _rwsTemp)
{
    int autype = RouteAmpAlternating;
    string rwsTemp = _rwsTemp;
    CtiToLower(rwsTemp);

    in_place_trim(rwsTemp);

    if( rwsTemp == "alternating" )
    {
        autype = RouteAmpAlternating;
    }
    else if(rwsTemp == "alt w/failover")
    {
        autype = RouteAmpAltFail;
    }
    else if(rwsTemp == "default 1 fail 2")
    {
        autype = (RouteAmpDefault1Fail2);
    }
    else if(rwsTemp == "default 2 fail 1")
    {
        autype = (RouteAmpDefault2Fail1);
    }
    else if(rwsTemp == "amp 1")
    {
        autype = (RouteAmp1);
    }
    else if(rwsTemp == "amp 2")
    {
        autype = (RouteAmp2);
    }
    else
    {
        autype = (RouteAmpDefault2Fail1);
    }

    return autype;
}

CtiPointType_t resolvePointType(const string& _rwsTemp)
{
    static const string analog = "analog";
    static const string status = "status";
    static const string pulseaccumulator  = "pulseaccumulator";
    static const string pulse_accumulator = "pulse accumulator";
    static const string accumulator       = "accumulator";
    static const string demandaccumulator  = "demandaccumulator";
    static const string demand_accumulator = "demand accumulator";
    static const string calculated   = "calculated";
    static const string calcanalog   = "calcanalog";
    static const string calcstatus   = "calcstatus";
    static const string system       = "system";
    static const string statusoutput = "statusoutput";
    static const string analogoutput = "analogoutput";

    CtiPointType_t Ret;
    string rwsTemp = _rwsTemp;
    CtiToLower(rwsTemp);
    in_place_trim(rwsTemp);

    if(rwsTemp == analog)
    {
        Ret = AnalogPointType;
    }
    else if(rwsTemp == status)
    {
        Ret = StatusPointType;
    }
    else if(rwsTemp == pulseaccumulator  ||
            rwsTemp == pulse_accumulator ||    // This WILL go away over time I hope!
            rwsTemp == accumulator)            // This WILL go away over time I hope!
    {
        Ret = PulseAccumulatorPointType;
    }
    else if(rwsTemp == demandaccumulator ||
            rwsTemp == demand_accumulator)     // This WILL go away over time I hope!
    {
        Ret = DemandAccumulatorPointType;
    }
    else if(rwsTemp == calculated || rwsTemp == calcanalog)
    {
        Ret = CalculatedPointType;
    }
    else if(rwsTemp == calcstatus)
    {
        Ret = CalculatedStatusPointType;
    }
    else if(rwsTemp == system)
    {
        Ret = SystemPointType;
    }
    else if(rwsTemp == statusoutput)
    {
        Ret = StatusOutputPointType;
    }
    else if(rwsTemp == analogoutput)
    {
        Ret = AnalogOutputPointType;
    }
    else
    {
        Ret = InvalidPointType;
    }

    return Ret;
}

INT resolvePointArchiveType(const string& _rwsTemp)
{
    INT Ret = ArchiveTypeNone;
    string rwsTemp = _rwsTemp;
    CtiToLower(rwsTemp);
    in_place_trim(rwsTemp);

    if(rwsTemp == "none")
    {
        Ret = ArchiveTypeNone;
    }
    else if(rwsTemp == "on change")
    {
        Ret = ArchiveTypeOnChange;
    }
    else if(rwsTemp == "on timer")
    {
        Ret = ArchiveTypeOnTimer;
    }
    else if(rwsTemp == "on update")
    {
        Ret = ArchiveTypeOnUpdate;
    }
    else if(rwsTemp == "time&update")       // This should help catch interval ending reads.
    {
        Ret = ArchiveTypeOnTimerAndUpdated;
    }
    else if(rwsTemp == "timer|update")       // Every interval (big interval) or every update (infrequent point).
    {
        Ret = ArchiveTypeOnTimerOrUpdated;
    }

    return Ret;
}

INT resolvePAOType(const string& category, const string& rwsTemp)
{
    INT result = 0;

    INT categoryInt = resolvePAOCategory(category);

    if(categoryInt == PAO_CATEGORY_DEVICE)
    {
        result= resolveDeviceType(rwsTemp);
    }
    else if(categoryInt == PAO_CATEGORY_PORT)
    {
        result= resolvePortType(rwsTemp);
    }
    else if(categoryInt == PAO_CATEGORY_ROUTE)
    {
        result= resolveRouteType(rwsTemp);
    }
    else if(categoryInt == PAO_CATEGORY_LOAD_MANAGEMENT)
    {
        result= resolveLoadManagementType(rwsTemp);
    }
    else if(categoryInt == PAO_CATEGORY_CAP_CONTROL)
    {
        result= resolveCapControlType(rwsTemp);
    }

    return result;
}


INT resolvePAOCategory(const string& _category)
{
    INT result = -1;
    string category = _category;
    CtiToLower(category);
    in_place_trim(category);

    if(category == ("device"))
    {
        result = PAO_CATEGORY_DEVICE;
    }
    else if(category == ("port"))
    {
        result = PAO_CATEGORY_PORT;
    }
    else if(category == ("route"))
    {
        result = PAO_CATEGORY_ROUTE;
    }
    else if(category == ("loadmanagement"))
    {
        result = PAO_CATEGORY_LOAD_MANAGEMENT;
    }
    else if(category == ("capcontrol"))
    {
        result = PAO_CATEGORY_CAP_CONTROL;
    }

    return result;
}


typedef std::map<string, int> device_lookup_t;

device_lookup_t init_device_lookups()
{
    device_lookup_t device_types;

    using std::make_pair;

    //  --- GridSmart ---
    device_types.insert(make_pair("capacitor bank neutral monitor",
                                                        TYPE_NEUTRAL_MONITOR));
    device_types.insert(make_pair("faulted circuit indicator",
                                                        TYPE_FCI));

    //  --- Capacitor Control ---
    device_types.insert(make_pair("cap bank",           TYPECAPBANK));
    device_types.insert(make_pair("cbc 6510",           TYPECBC6510));
    device_types.insert(make_pair("cbc 7010",           TYPECBC7010));
    device_types.insert(make_pair("cbc 7011",           TYPECBC7010));
    device_types.insert(make_pair("cbc 7012",           TYPECBC7010));
    device_types.insert(make_pair("cbc 7020",           TYPECBC7020));
    device_types.insert(make_pair("cbc 7022",           TYPECBC7020));
    device_types.insert(make_pair("cbc 7023",           TYPECBC7020));
    device_types.insert(make_pair("cbc 7024",           TYPECBC7020));
    device_types.insert(make_pair("cbc 7030",           TYPECBC7020));
    device_types.insert(make_pair("cbc 8020",           TYPECBC8020));
    device_types.insert(make_pair("cbc 8024",           TYPECBC8020));
    device_types.insert(make_pair("cbc dnp",            TYPECBCDNP));
    device_types.insert(make_pair("cbc expresscom",     TYPEEXPRESSCOMCBC));
    device_types.insert(make_pair("cbc fp-2800",        TYPEFISHERPCBC));
    device_types.insert(make_pair("cbc versacom",       TYPEVERSACOMCBC));
    device_types.insert(make_pair("load tap changer",   TYPELTC));

    //  --- Cooper PLC ---
    device_types.insert(make_pair("ccu-700",            TYPE_CCU700));
    device_types.insert(make_pair("ccu-710a",           TYPE_CCU710));
    device_types.insert(make_pair("ccu-711",            TYPE_CCU711));
    device_types.insert(make_pair("ccu-721",            TYPE_CCU721));
    device_types.insert(make_pair("lcr-3102",           TYPELCR3102));
    device_types.insert(make_pair("lmt-2",              TYPELMT2));
    device_types.insert(make_pair("mct broadcast",      TYPEMCTBCAST));
    device_types.insert(make_pair("mct-210",            TYPEMCT210));
    device_types.insert(make_pair("mct-212",            TYPEMCT212));
    device_types.insert(make_pair("mct-213",            TYPEMCT213));
    device_types.insert(make_pair("mct-224",            TYPEMCT224));
    device_types.insert(make_pair("mct-226",            TYPEMCT226));
    device_types.insert(make_pair("mct-240",            TYPEMCT240));
    device_types.insert(make_pair("mct-242",            TYPEMCT242));
    device_types.insert(make_pair("mct-248",            TYPEMCT248));
    device_types.insert(make_pair("mct-250",            TYPEMCT250));
    device_types.insert(make_pair("mct-310",            TYPEMCT310));
    device_types.insert(make_pair("mct-310ct",          TYPEMCT310));
    device_types.insert(make_pair("mct-310id",          TYPEMCT310ID));
    device_types.insert(make_pair("mct-310idl",         TYPEMCT310IDL));
    device_types.insert(make_pair("mct-310il",          TYPEMCT310IL));
    device_types.insert(make_pair("mct-318",            TYPEMCT318));
    device_types.insert(make_pair("mct-318l",           TYPEMCT318L));
    device_types.insert(make_pair("mct-360",            TYPEMCT360));
    device_types.insert(make_pair("mct-370",            TYPEMCT370));
    device_types.insert(make_pair("mct-410cl",          TYPEMCT410CL));
    device_types.insert(make_pair("mct-410fl",          TYPEMCT410FL));
    device_types.insert(make_pair("mct-410gl",          TYPEMCT410GL));
    device_types.insert(make_pair("mct-410il",          TYPEMCT410IL));
    device_types.insert(make_pair("mct-420cl",          TYPEMCT420CL));
    device_types.insert(make_pair("mct-420cld",         TYPEMCT420CLD));
    device_types.insert(make_pair("mct-420fl",          TYPEMCT420FL));
    device_types.insert(make_pair("mct-420fld",         TYPEMCT420FLD));
    device_types.insert(make_pair("mct-430a",           TYPEMCT430A));
    device_types.insert(make_pair("mct-430a3",          TYPEMCT430A3));
    device_types.insert(make_pair("mct-430s4",          TYPEMCT430S4));
    device_types.insert(make_pair("mct-430sl",          TYPEMCT430SL));
    device_types.insert(make_pair("mct-470",            TYPEMCT470));
    device_types.insert(make_pair("repeater 800",       TYPE_REPEATER800));
    device_types.insert(make_pair("repeater 801",       TYPE_REPEATER800));
    device_types.insert(make_pair("repeater 850",       TYPE_REPEATER850));
    device_types.insert(make_pair("repeater 902",       TYPE_REPEATER900));
    device_types.insert(make_pair("repeater 921",       TYPE_REPEATER900));
    device_types.insert(make_pair("repeater",           TYPE_REPEATER900));

    //  --- Receivers ---
    device_types.insert(make_pair("fmu",                TYPE_FMU));
    device_types.insert(make_pair("page receiver",      TYPE_PAGING_RECEIVER));

    //  --- Eka ---
    device_types.insert(make_pair("rfn-410fd",          TYPERFN410FD));
    device_types.insert(make_pair("rfn-410fl",          TYPERFN410FL));
    device_types.insert(make_pair("rfn-410fx",          TYPERFN410FX));
    device_types.insert(make_pair("rfn-430a3",          TYPERFN430A3));
    device_types.insert(make_pair("rfn-430kv",          TYPERFN430KV));

    //  --- RTU devices ---
    device_types.insert(make_pair("rtu-dart",           TYPE_DARTRTU));
    device_types.insert(make_pair("rtu-dnp",            TYPE_DNPRTU));
    device_types.insert(make_pair("rtu-ilex",           TYPE_ILEXRTU));
    device_types.insert(make_pair("rtu-lmi",            TYPE_SERIESVLMIRTU));
    device_types.insert(make_pair("rtu-modbus",         TYPE_MODBUS));
    device_types.insert(make_pair("rtu-ses92",          TYPE_SES92RTU));
    device_types.insert(make_pair("rtu-welco",          TYPE_WELCORTU));

    //  --- GRE (Great River Energy) transmitters ---
    device_types.insert(make_pair("rtc",                TYPE_RTC));
    device_types.insert(make_pair("rtm",                TYPE_RTM));

    //  --- GRE (Great River Energy) Load Management groups ---
    device_types.insert(make_pair("golay group",        TYPE_LMGROUP_GOLAY));
    device_types.insert(make_pair("sa-105 group",       TYPE_LMGROUP_SA105));
    device_types.insert(make_pair("sa-205 group",       TYPE_LMGROUP_SA205));
    device_types.insert(make_pair("sa-305 group",       TYPE_LMGROUP_SA305));
    device_types.insert(make_pair("sa-digital group",   TYPE_LMGROUP_SADIGITAL));

    //  --- Load Management ---
    device_types.insert(make_pair("ci customer",        TYPE_CI_CUSTOMER));
    device_types.insert(make_pair("lm control area",    TYPE_LM_CONTROL_AREA));
    device_types.insert(make_pair("lm curtail program", TYPE_LMPROGRAM_CURTAILMENT));
    device_types.insert(make_pair("lm direct program",  TYPE_LMPROGRAM_DIRECT));
    device_types.insert(make_pair("lm energy exchange", TYPE_LMPROGRAM_ENERGYEXCHANGE));
    device_types.insert(make_pair("lm sep program",     TYPE_LMPROGRAM_DIRECT));
    device_types.insert(make_pair("digi sep group",     TYPE_LMGROUP_DIGI_SEP));
    device_types.insert(make_pair("emetcon group",      TYPE_LMGROUP_EMETCON));
    device_types.insert(make_pair("expresscom group",   TYPE_LMGROUP_EXPRESSCOM));
    device_types.insert(make_pair("mct group",          TYPE_LMGROUP_MCT));
    device_types.insert(make_pair("point group",        TYPE_LMGROUP_POINT));
    device_types.insert(make_pair("ripple group",       TYPE_LMGROUP_RIPPLE));
    device_types.insert(make_pair("versacom group",     TYPE_LMGROUP_VERSACOM));

    //  --- System ---
    device_types.insert(make_pair("macro group",        TYPE_MACRO));
    device_types.insert(make_pair("script",             0));
    device_types.insert(make_pair("simple",             0));
    device_types.insert(make_pair("system",             TYPE_SYSTEM));
    device_types.insert(make_pair("virtual system",     TYPE_VIRTUAL_SYSTEM));

    //  --- Transmitters ---
    device_types.insert(make_pair("lcu-415",            TYPE_LCU415));
    device_types.insert(make_pair("lcu-eastriver",      TYPE_LCU415ER));
    device_types.insert(make_pair("lcu-lg",             TYPE_LCU415LG));
    device_types.insert(make_pair("lcu-t3026",          TYPE_LCUT3026));
    device_types.insert(make_pair("rds terminal",       TYPE_RDS));
    device_types.insert(make_pair("snpp terminal",      TYPE_SNPP));
    device_types.insert(make_pair("tap terminal",       TYPE_TAPTERM));
    device_types.insert(make_pair("tcu-5000",           TYPE_TCU5000));
    device_types.insert(make_pair("tcu-5500",           TYPE_TCU5500));
    device_types.insert(make_pair("tnpp terminal",      TYPE_TNPP));
    device_types.insert(make_pair("wctp terminal",      TYPE_WCTP));

    //  --- IEDs and electronic meters ---
    device_types.insert(make_pair("alpha a1",           TYPE_ALPHA_A1));
    device_types.insert(make_pair("alpha a3",           TYPE_ALPHA_A3));
    device_types.insert(make_pair("alpha power plus",   TYPE_ALPHA_PPLUS));
    device_types.insert(make_pair("davis weather",      TYPE_DAVIS));
    device_types.insert(make_pair("dct-501",            TYPEDCT501));
    device_types.insert(make_pair("dr-87",              TYPE_DR87));
    device_types.insert(make_pair("focus",              TYPE_FOCUS));
    device_types.insert(make_pair("ipc-410al",          TYPE_IPC_410AL));
    device_types.insert(make_pair("ipc-420ad",          TYPE_IPC_420AD));
    device_types.insert(make_pair("ipc-430s4",          TYPE_IPC_430S4));
    device_types.insert(make_pair("ipc-430sl",          TYPE_IPC_430SL));
    device_types.insert(make_pair("fulcrum",            TYPE_FULCRUM));
    device_types.insert(make_pair("ion-7330",           TYPE_ION7330));
    device_types.insert(make_pair("ion-7700",           TYPE_ION7700));
    device_types.insert(make_pair("ion-8300",           TYPE_ION8300));
    device_types.insert(make_pair("kv",                 TYPE_KV2));
    device_types.insert(make_pair("kv2",                TYPE_KV2));
    device_types.insert(make_pair("landis-gyr s4",      TYPE_LGS4));
    device_types.insert(make_pair("quantum",            TYPE_QUANTUM));
    device_types.insert(make_pair("sentinel",           TYPE_SENTINEL));
    device_types.insert(make_pair("sixnet",             TYPE_SIXNET));
    device_types.insert(make_pair("transdata mark-v",   TYPE_TDMARKV));
    device_types.insert(make_pair("vectron",            TYPE_VECTRON));

    return device_types;
}

static const device_lookup_t device_lookups = init_device_lookups();

INT resolveDeviceType(const string& _rwsTemp)
{
    string rwsTemp = _rwsTemp;
    CtiToLower(rwsTemp);
    in_place_trim(rwsTemp);

    device_lookup_t::const_iterator itr = device_lookups.find(rwsTemp);

    if( itr != device_lookups.end() )
    {
        return itr->second;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Unsupported DEVICE type \"" << rwsTemp << "\" " << endl;
    }

    return 0;
}


INT resolveCapControlType(const string& _rwsTemp)
{
    INT nRet = 0;
    string rwsTemp = _rwsTemp;
    CtiToLower(rwsTemp);
    in_place_trim(rwsTemp);

    if(rwsTemp == "ccarea")
    {
        nRet = TYPE_CC_AREA;
    }
    else if(rwsTemp == "ccsubstation")
    {
        nRet = TYPE_CC_SUBSTATION;
    }
    else if(rwsTemp == "ccsubbus")
    {
        nRet = TYPE_CC_SUBSTATION_BUS;
    }
    else if(rwsTemp == "ccfeeder")
    {
        nRet = TYPE_CC_FEEDER;
    }
    else if(rwsTemp == "ccspecialarea")
    {
        nRet = TYPE_CC_SPECIALAREA;
    }
    else if(rwsTemp == "ltc" || rwsTemp == "go_regulator" || rwsTemp == "po_regulator")
    {
        nRet = TYPE_CC_VOLTAGEREGULATOR;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Unsupported CAP CONTROL type \"" << rwsTemp << "\" " << endl;
        }
    }

    return nRet;
}

INT resolveLoadManagementType(const string& _rwsTemp)
{
    INT nRet = 0;
    string rwsTemp = _rwsTemp;
    CtiToLower(rwsTemp);
    in_place_trim(rwsTemp);

    if(rwsTemp == "lm direct program" ||
       rwsTemp == "lm sep program")
    {
        nRet = TYPE_LMPROGRAM_DIRECT;
    }
    else if(rwsTemp == "lm curtail program")
    {
        nRet = TYPE_LMPROGRAM_CURTAILMENT;
    }
    else if(rwsTemp == "lm control area")
    {
        nRet = TYPE_LM_CONTROL_AREA;
    }
    else if(rwsTemp == "ci customer")
    {
        nRet = TYPE_CI_CUSTOMER;
    }
    else if(rwsTemp == "lm energy exchange")
    {
        nRet = TYPE_LMPROGRAM_ENERGYEXCHANGE;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Unsupported LOAD MANAGEMENT type \"" << rwsTemp << "\" " << endl;
        }
    }

    return nRet;
}


INT resolveScanType(const string& _rwsTemp)
{
    INT nRet = 0;
    string rwsTemp = _rwsTemp;
    CtiToLower(rwsTemp);
    in_place_trim(rwsTemp);


    if(rwsTemp == "general")
    {
        nRet = ScanRateGeneral;
    }
    else if(rwsTemp == "accumulator")
    {
        nRet = ScanRateAccum;
    }
    else if(rwsTemp == "integrity")
    {
        nRet = ScanRateIntegrity;
    }
    else if(rwsTemp == "status")
    {
        nRet = ScanRateGeneral;
    }
    else if(rwsTemp == "exception")
    {
        nRet = ScanRateGeneral;
    }
    else if(rwsTemp == "loadprofile")
    {
        nRet = ScanRateLoadProfile;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Unsupported scan rate type \"" << rwsTemp << "\" " << endl;
        nRet = ScanRateInvalid;
    }

    return nRet;
}

LONG resolveDeviceWindowType(const string& _rwsTemp)
{
    INT nRet = 0;
    string rwsTemp = _rwsTemp;
    CtiToLower(rwsTemp);
    in_place_trim(rwsTemp);


    if(rwsTemp == DEVICE_WINDOW_TYPE_SCAN)
    {
        nRet = DeviceWindowScan;
    }
    else if(rwsTemp == DEVICE_WINDOW_TYPE_PEAK)
    {
        nRet = DeviceWindowPeak;
    }
    else if(rwsTemp == DEVICE_WINDOW_TYPE_ALTERNATE_RATE)
    {
        nRet = DeviceWindowAlternateRate;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Unsupported device window type \"" << rwsTemp << "\" " << endl;
        nRet = DeviceWindowInvalid;
    }

    return nRet;
}


INT resolvePAOClass(const string& _rwsTemp)
{
    INT nRet = 0;
    string rwsTemp = _rwsTemp;
    CtiToLower(rwsTemp);
    in_place_trim(rwsTemp);

    /* The mantra of a professor I once had... Make the common case fast! */
    if(rwsTemp == "transmitter")
    {
        nRet = PAOClassTransmitter;
    }
    else if(rwsTemp == "rtu")
    {
        nRet = PAOClassRTU;
    }
    else if(rwsTemp == "ied")
    {
        nRet = PAOClassIED;
    }
    else if(rwsTemp == "carrier")
    {
        nRet = PAOClassCarrier;
    }
    else if(rwsTemp == "meter")
    {
        nRet = PAOClassMeter;
    }
    else if(rwsTemp == "gridadvisor")
    {
        nRet = PAOClassGridAdvisor;
    }
    else if(rwsTemp == "group")
    {
        nRet = PAOClassGroup;
    }
    else if(rwsTemp == "system")
    {
        nRet = PAOClassSystem;
    }
    else if(rwsTemp == "capcontrol")
    {
        nRet = PAOClassCapControl;
    }
    else if(rwsTemp == "loadmanagement")
    {
        nRet = PAOClassLoadManagement;
    }
    else if(rwsTemp == "virtual")
    {
        nRet = PAOClassVirtual;
    }
    else if(rwsTemp == "port")
    {
        nRet = PAOClassPort;
    }
    else if(rwsTemp == "route")
    {
        nRet = PAOClassRoute;
    }
    else if(rwsTemp == "schedule")
    {
        nRet = PAOClassMACS;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Unsupported device class \"" << rwsTemp << "\" " << endl;
        nRet = PAOClassInvalid;
    }

    return nRet;
}

INT resolveStatisticsType(const string& _rwsTemp)
{
    INT nRet = 0;
    string rwsTemp = _rwsTemp;
    CtiToLower(rwsTemp);
    in_place_trim(rwsTemp);


    if(rwsTemp == "monthly")
    {
        nRet = StatTypeMonthly;
    }
    else if(rwsTemp == "hourly")
    {
        nRet = StatTypeHourly;
    }
    else if(rwsTemp == "daily")
    {
        nRet = StatType24Hour;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Unsupported statistics collection type \"" << rwsTemp << "\" " << endl;
        nRet = StatTypeInvalid;
    }

    return nRet;
}

CtiFilter_t resolveFilterType(const string& _rwsTemp)
{
    CtiFilter_t Ret = InvalidFilter;
    string rwsTemp = _rwsTemp;
    CtiToLower(rwsTemp);
    in_place_trim(rwsTemp);

    if(rwsTemp == "none")
    {
        Ret = NoFilter;
    }
    else if(rwsTemp == "lastvalue")
    {
        Ret = LastValueFilter;
    }
    else if(rwsTemp == "defaultvalue")
    {
        Ret = DefaultValueFilter;
    }

    return Ret;
}



INT resolveProtocol(const string& _str)
{
    INT nRet = 0;
    string str = _str;
    CtiToLower(str);
    in_place_trim(str);

    if(str == "idlc")
    {
        nRet = ProtocolWrapIDLC;
    }
    else if(str == "none")
    {
        nRet = ProtocolWrapNone;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "Unknown port protocol wrap " << str << endl;
    }

    return nRet;
}

INT resolvePortType(const string& _str)
{
    INT nRet = 0;
    string str = _str;
    CtiToLower(str);
    in_place_trim(str);

    if(str == "local serial port")
    {
        nRet = PortTypeLocalDirect;
    }
    else if(str == "local dialup")
    {
        nRet = PortTypeLocalDialup;
    }
    else if(str == "terminal server")
    {
        nRet = PortTypeTServerDirect;
    }
    else if(str == "tcp")
    {
        nRet = PortTypeTcp;
    }
    else if(str == "udp")
    {
        nRet = PortTypeUdp;
    }
    else if(str == "terminal server dialup")
    {
        nRet = PortTypeTServerDialup;
    }
    else if(str == "local dialback")
    {
        nRet = PortTypeLocalDialBack;
    }
    else if(str == "terminal server dialback")
    {
        nRet = PortTypeTServerDialBack;
    }
    else if(str == "dialout pool")
    {
        nRet = PortTypePoolDialout;
    }
    else
    {
        nRet = PortTypeInvalid;
    }

    return nRet;
}


bool resolveIsDeviceTypeSingle(INT Type)
{
    bool bRet = false;

    switch(Type)
    {
        case TYPE_CCU721:
        case TYPE_CCU711:
        case TYPE_CCU710:
        case TYPE_CCU700:
        case TYPE_REPEATER800:
        case TYPE_REPEATER850:
        case TYPE_REPEATER900:
        case TYPE_ILEXRTU:
        case TYPE_WELCORTU:
        case TYPE_SES92RTU:
        case TYPE_DNPRTU:
        case TYPE_DARTRTU:
        case TYPE_SERIESVRTU:
        case TYPE_SERIESVLMIRTU:
        case TYPE_ION7330:
        case TYPE_ION7700:
        case TYPE_ION8300:
        case TYPE_LCU415:
        case TYPE_LCU415LG:
        case TYPE_LCU415ER:
        case TYPE_LCUT3026:
        case TYPE_TAPTERM:
        case TYPE_SNPP:
        case TYPE_RDS:
        case TYPE_TNPP:
        case TYPE_WCTP:
        case TYPE_TCU5000:
        case TYPE_TCU5500:
        case TYPE_TDMARKV:
        case TYPE_DAVIS:
        case TYPE_ALPHA_A1:
        case TYPE_ALPHA_PPLUS:
        case TYPE_FULCRUM:
        case TYPE_QUANTUM:
        case TYPE_VECTRON:
        case TYPE_LGS4:
        case TYPE_IPC_430S4:
        case TYPE_DR87:
        case TYPE_KV2:
        case TYPE_ALPHA_A3:
        case TYPE_SENTINEL:
        case TYPE_IPC_430SL:
        case TYPE_FOCUS:
        case TYPE_IPC_410AL:
        case TYPE_IPC_420AD:
        case TYPE_SIXNET:
        case TYPEDCT501:
        case TYPEMCT210:
        case TYPEMCT212:
        case TYPEMCT213:
        case TYPEMCT224:
        case TYPEMCT226:
        case TYPEMCT240:
        case TYPEMCT242:
        case TYPEMCT248:
        case TYPEMCT250:
        case TYPEMCT310:
        case TYPEMCT310ID:
        case TYPEMCT310IL:
        case TYPEMCT310IDL:
        case TYPEMCT318:
        case TYPEMCT318L:
        case TYPEMCT360:
        case TYPEMCT370:
        case TYPEMCT410CL:
        case TYPEMCT410FL:
        case TYPEMCT410GL:
        case TYPEMCT410IL:
        case TYPEMCT420CL:
        case TYPEMCT420CLD:
        case TYPEMCT420FL:
        case TYPEMCT420FLD:
        case TYPEMCT430A:
        case TYPEMCT430A3:
        case TYPEMCT430S4:
        case TYPEMCT430SL:
        case TYPEMCT470:
        case TYPELCR3102:
        case TYPELTC:
        case TYPE_MODBUS:
        case TYPELMT2:
        case TYPECBC6510:
        case TYPECBC7020:
        case TYPECBC8020:
        case TYPECBCDNP:
        case TYPE_RTC:
        case TYPE_RTM:
        case TYPE_FMU:
        case TYPE_PAGING_RECEIVER:
        case TYPE_FCI:
        case TYPE_NEUTRAL_MONITOR:
        {
            bRet = true;
            break;
        }
        case TYPE_SYSTEM:
        case TYPE_VIRTUAL_SYSTEM:
        case TYPEVERSACOMCBC:
        case TYPECBC7010:
        case TYPEEXPRESSCOMCBC:
        case TYPEFISHERPCBC:
        case TYPE_LMGROUP_EMETCON:
        case TYPE_LMGROUP_POINT:
        case TYPE_LMGROUP_RIPPLE:
        case TYPE_LMGROUP_VERSACOM:
        case TYPE_LMGROUP_EXPRESSCOM:
        case TYPE_LMGROUP_DIGI_SEP:
        case TYPE_LMGROUP_MCT:
        case TYPE_LMGROUP_GOLAY:
        case TYPE_LMGROUP_SADIGITAL:
        case TYPE_LMGROUP_SA105:
        case TYPE_LMGROUP_SA205:
        case TYPE_LMGROUP_SA305:
        case TYPEMCTBCAST:
        case TYPE_MACRO:
        {
            bRet = false;
            break;
        }
        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Unable to determine whether device type " << Type <<  " is a targetable device type!" << endl;
        }
    }

    return bRet;
}

INT resolveRelayUsage(const string& _str)
{

/*
#define A_RESTORE       0
#define A_SHED_A        1
#define A_SHED_B        2
#define A_SHED_C        3
#define A_SHED_D        4
#define A_LATCH_OPEN    5
#define A_LATCH_CLOSE   6
#define A_SCRAM         7
 */

    INT nRet = 0;
    string str = _str;
    CtiToLower(str);

    if(str == "r")
    {
        nRet = A_RESTORE;
    }
    else if(str == "a")
    {
        nRet = A_SHED_A;
    }
    else if(str == "b")
    {
        nRet = A_SHED_B;
    }
    else if(str == "c")
    {
        nRet = A_SHED_C;
    }
    else if(str == "d")
    {
        nRet = A_SHED_D;
    }
    else if(str == "o")
    {
        nRet = A_LATCH_OPEN;
    }
    else if(str == "x")
    {
        nRet = A_LATCH_CLOSE;
    }
    else if(str == "s")
    {
        // cout << "Scram Scram Scram" << endl;
        nRet = A_SCRAM;
    }
    else
    {
        /*
         *  We are either NOT an emetcon load group or there is a problem...
         *  First check if we use the other form of relay db string (a. la versacomgroups)
         */

        string numAsStr;

        for(int i = 0; i < 10; i++)
        {
            numAsStr = CtiNumStr(i + 1);

            if(!(str.find(numAsStr)==string::npos))
            {
                nRet |= (0x00000001 << i);
            }
        }

#if 0
        if(nRet == 0)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Invalid relay type, or no relays selected. " << str << endl;
            nRet = A_RESTORE;
        }
#endif
    }

    return nRet;
}

INT resolveAddressUsage(const string& _str, int type)
{
    INT nRet = 0;
    string str = _str;
    CtiToLower(str);

    switch(type)
    {
    case Cti::AddressUsage_Versacom:
        {
            if(!(str.find("u")==string::npos)) nRet |= 0x08;  // Utility
            if(!(str.find("s")==string::npos)) nRet |= 0x04;  // Section
            if(!(str.find("c")==string::npos)) nRet |= 0x02;  // Class
            if(!(str.find("d")==string::npos)) nRet |= 0x01;  // Division

            break;
        }
    case Cti::AddressUsage_Expresscom:
        {
            if(!(str.find("s")==string::npos)) nRet |= 0x80;  // Service Provider Id
            if(!(str.find("g")==string::npos)) nRet |= 0x40;  // Geo
            if(!(str.find("b")==string::npos)) nRet |= 0x20;  // Substation
            if(!(str.find("f")==string::npos)) nRet |= 0x10;  // Feeder
            if(!(str.find("z")==string::npos)) nRet |= 0x08;  // Zip
            if(!(str.find("u")==string::npos)) nRet |= 0x04;  // User Defined
            if(!(str.find("p")==string::npos)) nRet |= 0x02;  // Program
            if(!(str.find("r")==string::npos)) nRet |= 0x01;  // Splinter

            if(!(str.find("l")==string::npos)) nRet |= 0x8000;  // Load addressing  040903

            break;
        }
    default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "    Invalid address usage type " << str << endl;
            break;
        }
    }

    return nRet;
}

INT resolveAWordTime(INT Seconds)
{
    INT nRet = 0;
/* Time definitions

   #define TIME_7_5        0
   #define TIME_15         1
   #define TIME_30         2
   #define TIME_60         3

 */

    if(Seconds < 5)
    {
        // This must be a restore
        nRet = 0;
    }
    else if(Seconds < 451)
    {
        nRet = TIME_7_5;
    }
    else if(Seconds < 901)
    {
        nRet = TIME_15;
    }
    else if(Seconds < 1801)
    {
        nRet = TIME_30;
    }
    else if(Seconds < 3601)
    {
        nRet = TIME_60;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Time " << Seconds << " is not an Emetcon standard time.  Using 1 hour." << endl;
            // Fall through
        }
        nRet = TIME_60;
    }

    return nRet;
}

string   resolveDBChangeType(INT type)
{
    string rStr;

    switch(type)
    {
    case ChangeTypeAdd:
        {
            rStr = (" ADDED TO");
            break;
        }
    case ChangeTypeDelete:
        {
            rStr = (" DELETED FROM");
            break;
        }
    case ChangeTypeUpdate:
        {
            rStr = (" UPDATED IN");
            break;
        }
    default:
        {
            rStr = (" CHANGED IN");
            break;
        }
    }

    return rStr;
}

string   resolveDBChanged(INT dbnum)
{
    string rStr;
    switch(dbnum)
    {
    case ChangePAODb:
        {
            rStr = (" PAO DB");
            break;
        }
    case ChangePointDb:
        {
            rStr = (" POINT DB");
            break;
        }
    case ChangeStateGroupDb:
        {
            rStr = (" GROUP DB");
            break;
        }
    case ChangeNotificationGroupDb:
        {
            rStr = (" NOTIFICATIONGROUP/DESTINATION DB");
            break;
        }
    case ChangeNotificationRecipientDb:
        {
            rStr = (" GROUPRECIPIENT DB");
            break;
        }
    case ChangeAlarmCategoryDb:
        {
            rStr = (" ALARM Category DB");
            break;
        }
    case ChangeCustomerContactDb:
        {
            rStr = (" Customer Contact DB");
            break;
        }
    case ChangeGraphDb:
        {
            rStr = (" Graph DB");
            break;
        }
    case ChangeHolidayScheduleDb:
        {
            rStr = (" Holiday Schedule DB");
            break;
        }
    case ChangeEnergyCompanyDb:
        {
            rStr = (" Energy Company DB");
            break;
        }
    case ChangeYukonUserDb:
        {
            rStr = (" Yukon User DB");
            break;
        }
    case ChangeCustomerDb:
        {
            rStr = (" Yukon Customer DB");
            break;
        }
    case ChangeCustomerAccountDb:
        {
            rStr = (" Yukon Customer Account DB");
            break;
        }
    case ChangeYukonImageDb:
        {
            rStr = (" Yukon Image DB");
            break;
        }
    case ChangeBaselineDb:
        {
            rStr = (" Yukon Baseline DB");
            break;
        }
    case ChangeConfigDb:
        {
        rStr = (" Yukon Config DB");
        break;
        }
    case ChangeTagDb:
        {
        rStr = (" Yukon Tag DB");
        break;
    }
    case ChangeCICustomerDb:
        {
        rStr = (" Yukon CI Customer DB");
        break;
    }
    case ChangeLMConstraintDb:
        {
        rStr = (" Yukon LM Constraint DB");
        break;
    }
    case ChangeSeasonScheduleDb:
        {
        rStr = (" Yukon Season Schedule DB");
        break;
        }
    default:
        {
            rStr = (" DATABASE");
            break;
        }
    }

    return rStr;
}

INT resolveSlaveAddress(const INT DeviceType, const string& _str)
{
    INT slaveAddress = 0;
    string str = _str;
    CtiToLower(str);
    in_place_trim(str);

    switch( DeviceType )
    {
    case TYPE_FULCRUM:
    case TYPE_VECTRON:
        {
            if(str.empty() || str == "standalone")
            {
                slaveAddress = -1;
            }
            else if(str == "master")
            {
                slaveAddress = 0;
            }
            else if(str == "slave1")
            {
                slaveAddress = 8;
            }
            else if(str == "slave2")
            {
                slaveAddress = 9;
            }
            else if(str == "slave3")
            {
                slaveAddress = 10;
            }
            else if(str == "slave4")
            {
                slaveAddress = 11;
            }
            else
            {
                slaveAddress = -1;
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** ERROR Slave Address not defined **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    case TYPE_QUANTUM:
        {
            if(str.empty() || str == "standalone")
            {
                slaveAddress = -1;
            }
            else if(str == "master")
            {
                slaveAddress = 0;
            }
            else if(str == "slave1")
            {
                slaveAddress = 1;
            }
            else if(str == "slave2")
            {
                slaveAddress = 2;
            }
            else if(str == "slave3")
            {
                slaveAddress = 3;
            }
            else if(str == "slave4")
            {
                slaveAddress = 4;
            }
            else
            {
                slaveAddress = -1;
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** ERROR Slave Address not defined **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    case TYPE_RTM:
    case TYPE_FMU:
    case TYPE_SIXNET:
    case TYPE_RDS:
        {
            slaveAddress = atoi(str.c_str());
            break;
        }
    case TYPE_ALPHA_PPLUS:
        {
            if(str.empty() || str == "standalone")
            {
                slaveAddress = 0;
            }
            else if(str == "master")
            {
                slaveAddress = 0;
            }
            else if(str == "slave1")
            {
                slaveAddress = 1;
            }
            else if(str == "slave2")
            {
                slaveAddress = 2;
            }
            else if(str == "slave3")
            {
                slaveAddress = 3;
            }
            else if(str == "slave4")
            {
                slaveAddress = 4;
            }
            else
            {
                slaveAddress = 0;
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** ERROR Slave Address not defined **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    case TYPE_ALPHA_A1:
        {
            slaveAddress = 0;
            break;
        }
    case TYPE_DAVIS:
        {
            slaveAddress = 0;
            break;
        }
    case TYPE_LGS4:
    case TYPE_IPC_430S4:
    case TYPE_DR87:
    case TYPE_TAPTERM:
    case TYPE_WCTP:
    case TYPE_SNPP:
    case TYPE_TNPP:
    case TYPE_PAGING_RECEIVER:
    case TYPE_KV2:
    case TYPE_ALPHA_A3:
    case TYPE_SENTINEL:
    case TYPE_IPC_430SL:
    case TYPE_FOCUS:
    case TYPE_IPC_410AL:
    case TYPE_IPC_420AD:
    case TYPE_SERIESVRTU:
    case TYPE_SERIESVLMIRTU:
    case TYPE_ION7330:
    case TYPE_ION7700:
    case TYPE_ION8300:
    case TYPE_TDMARKV:
        {
            slaveAddress = -1;
            break;
        }
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Device Type " << DeviceType << " not resolved **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;

        }
    }


    return slaveAddress;
}


CtiControlType_t  resolveControlType(const string& _str)
{
    CtiControlType_t Ret = InvalidControlType;
    string str = _str;
    CtiToLower(str);
    in_place_trim(str);

    if(str == "none")
    {
        Ret = NoneControlType;
    }
    else if(str == "normal")
    {
        Ret = NormalControlType;
    }
    else if(str == "latch")
    {
        Ret = LatchControlType;
    }
    else if(str == "pseudo")
    {
        Ret = PseudoControlType;
    }
    else if(str == "sbo latch")
    {
        Ret = SBOLatchControlType;
    }
    else if(str == "sbo pulse")
    {
        Ret = SBOPulseControlType;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Unknown control type == " << str << endl;
    }

    return Ret;
}

INT resolveUomToCalcType(const string& _str)
{
    INT Ret = CalcTypeNormal;

    return Ret;
}
