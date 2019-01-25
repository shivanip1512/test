#include "precompiled.h"

#include "dsm2.h"
#include "resolvers.h"
#include "devicetypes.h"
#include "pointtypes.h"
#include "logger.h"
#include "numstr.h"
#include "utility.h"
#include "std_helper.h"

#include <boost/assign.hpp>
#include <boost/algorithm/string.hpp>

using std::string;

namespace   {

std::string sanitizeInput( const std::string & input )
{
    using boost::algorithm::to_lower_copy;
    using boost::algorithm::trim_copy;

    return trim_copy( to_lower_copy( input ) );
}

}

INT resolveRouteType( const string& _routeStr)
{
    static const std::map<std::string, CtiRoute_t>  routeLookup
    {
        { "ccu",                    RouteTypeCCU },
        { "tcu",                    RouteTypeTCU },
        { "macro",                  RouteTypeMacro },
        { "lcu",                    RouteTypeLCU },
        { "versacom",               RouteTypeVersacom },
        { "expresscom",             RouteTypeExpresscom },
        { "tap paging",             RouteTypeTap },
        { "tappaging",              RouteTypeTap },
        { "rds terminal route",     RouteTypeRDS },
        { "snpp terminal route",    RouteTypeSNPP },
        { "wctp terminal route",    RouteTypeWCTP },
        { "tnpp terminal route",    RouteTypeTNPP },
        { "rtc route",              RouteTypeRTC },
        { "series 5 lmi",           RouteTypeSeriesVLMI }
    };

    if ( const auto result = Cti::mapFind( routeLookup, sanitizeInput( _routeStr ) ) )
    {
        return *result;
    }

    return RouteTypeInvalid;
}

INT resolveAmpUseType(const string& _ampStr)
{
    static const std::map<std::string, CtiAmpUsage_t>  ampLookup
    {
        { "alternating",        RouteAmpAlternating },
        { "alt w/failover",     RouteAmpAltFail },
        { "default 1 fail 2",   RouteAmpDefault1Fail2 },
        { "default 2 fail 1",   RouteAmpDefault2Fail1 },
        { "amp 1",              RouteAmp1 },
        { "amp 2",              RouteAmp2 }
    };

    if ( const auto result = Cti::mapFind( ampLookup, sanitizeInput( _ampStr ) ) )
    {
        return *result;
    }

    return RouteAmpDefault2Fail1;
}

CtiPointType_t resolvePointType(const string& _pointStr)
{
    static const std::map<std::string, CtiPointType_t>  pointTypeLookup
    {
        { "analog",             AnalogPointType },
        { "status",             StatusPointType },
        { "pulseaccumulator",   PulseAccumulatorPointType },
        { "pulse accumulator",  PulseAccumulatorPointType },
        { "accumulator",        PulseAccumulatorPointType },
        { "demandaccumulator",  DemandAccumulatorPointType },
        { "demand accumulator", DemandAccumulatorPointType },
        { "calculated",         CalculatedPointType },
        { "calcanalog",         CalculatedPointType },
        { "calcstatus",         CalculatedStatusPointType },
        { "system",             SystemPointType },
        { "statusoutput",       StatusOutputPointType },
        { "analogoutput",       AnalogOutputPointType }
    };

    if ( const auto result = Cti::mapFind( pointTypeLookup, sanitizeInput( _pointStr ) ) )
    {
        return *result;
    }

    return InvalidPointType;
}

INT resolvePointArchiveType(const string& _archiveStr)
{
    static const std::map<std::string, CtiArchiveType_t>  archiveTypeLookup
    {
        { "on change",      ArchiveTypeOnChange },
        { "on timer",       ArchiveTypeOnTimer },
        { "on update",      ArchiveTypeOnUpdate },
        { "time&update",    ArchiveTypeOnTimerAndUpdated },
        { "timer|update",   ArchiveTypeOnTimerOrUpdated }
    };

    if ( const auto result = Cti::mapFind( archiveTypeLookup, sanitizeInput( _archiveStr ) ) )
    {
        return *result;
    }

    return ArchiveTypeNone;
}

INT resolvePAOType(const string& category, const string& typeStr)
{
    static const std::map<INT, std::function<INT(const std::string&)>>  subcategoryLookup
    {
        { PAO_CATEGORY_DEVICE,          resolveDeviceType },
        { PAO_CATEGORY_PORT,            resolvePortType },
        { PAO_CATEGORY_ROUTE,           resolveRouteType },
        { PAO_CATEGORY_LOAD_MANAGEMENT, resolveLoadManagementType },
        { PAO_CATEGORY_CAP_CONTROL,     resolveCapControlType }
    };

    if ( const auto resolver = Cti::mapFind( subcategoryLookup, resolvePAOCategory(category) ) )
    {
        return (*resolver)(typeStr);
    }

    return 0;
}

INT resolvePAOCategory(const string& _category)
{
    static const std::map<std::string, CtiPaoCategory_t>  paoCategoryLookup
    {
        { "device",         PAO_CATEGORY_DEVICE },
        { "port",           PAO_CATEGORY_PORT },
        { "route",          PAO_CATEGORY_ROUTE },
        { "loadmanagement", PAO_CATEGORY_LOAD_MANAGEMENT },
        { "capcontrol",     PAO_CATEGORY_CAP_CONTROL }
    };

    if ( const auto result = Cti::mapFind( paoCategoryLookup, sanitizeInput( _category ) ) )
    {
        return *result;
    }

    return -1;
}


static const std::map<std::string, DeviceTypes> device_lookups
{
    //  --- GridSmart ---
    { "capacitor bank neutral monitor",  TYPE_NEUTRAL_MONITOR },
    { "faulted circuit indicator",       TYPE_FCI },

    //  --- Capacitor Control ---
    { "cap bank",           TYPE_CAPBANK },
    { "cbc 7010",           TYPE_CBC7010 },
    { "cbc 7011",           TYPE_CBC7010 },
    { "cbc 7012",           TYPE_CBC7010 },
    { "cbc 7020",           TYPE_CBC7020 },
    { "cbc 7022",           TYPE_CBC7020 },
    { "cbc 7023",           TYPE_CBC7020 },
    { "cbc 7024",           TYPE_CBC7020 },
    { "cbc 7030",           TYPE_CBC7020 },
    { "cbc 8020",           TYPE_CBC8020 },
    { "cbc 8024",           TYPE_CBC8020 },
    { "cbc dnp",            TYPE_CBCDNP },
    { "cbc logical",        TYPE_CBCLOGICAL },
    { "cbc expresscom",     TYPE_EXPRESSCOMCBC },
    { "cbc fp-2800",        TYPE_FISHERPCBC },
    { "cbc versacom",       TYPE_VERSACOMCBC },
    
    //  --- Voltage Regulators ---
    { "ltc",                TYPE_LOAD_TAP_CHANGER },
    { "go_regulator",       TYPE_GANG_OPERATED_REGULATOR },
    { "po_regulator",       TYPE_PHASE_OPERATED_REGULATOR },

    //  --- Cooper PLC ---
    { "ccu-700",            TYPE_CCU700 },
    { "ccu-710a",           TYPE_CCU710 },
    { "ccu-711",            TYPE_CCU711 },
    { "ccu-721",            TYPE_CCU721 },
    { "lcr-3102",           TYPELCR3102 },
    { "lmt-2",              TYPELMT2 },
    { "mct broadcast",      TYPEMCTBCAST },
    { "mct-210",            TYPEMCT210 },
    { "mct-212",            TYPEMCT212 },
    { "mct-213",            TYPEMCT213 },
    { "mct-224",            TYPEMCT224 },
    { "mct-226",            TYPEMCT226 },
    { "mct-240",            TYPEMCT240 },
    { "mct-242",            TYPEMCT242 },
    { "mct-248",            TYPEMCT248 },
    { "mct-250",            TYPEMCT250 },
    { "mct-310",            TYPEMCT310 },
    { "mct-310ct",          TYPEMCT310 },
    { "mct-310id",          TYPEMCT310ID },
    { "mct-310idl",         TYPEMCT310IDL },
    { "mct-310il",          TYPEMCT310IL },
    { "mct-318",            TYPEMCT318 },
    { "mct-318l",           TYPEMCT318L },
    { "mct-360",            TYPEMCT360 },
    { "mct-370",            TYPEMCT370 },
    { "mct-410cl",          TYPEMCT410CL },
    { "mct-410fl",          TYPEMCT410FL },
    { "mct-410gl",          TYPEMCT410GL },
    { "mct-410il",          TYPEMCT410IL },
    { "mct-420cl",          TYPEMCT420CL },
    { "mct-420cd",          TYPEMCT420CD },
    { "mct-420fl",          TYPEMCT420FL },
    { "mct-420fd",          TYPEMCT420FD },
    { "mct-430a",           TYPEMCT430A },
    { "mct-430a3",          TYPEMCT430A3 },
    { "mct-430s4",          TYPEMCT430S4 },
    { "mct-430sl",          TYPEMCT430SL },
    { "mct-470",            TYPEMCT470 },
    { "mct-440-2131b",      TYPEMCT440_2131B },
    { "mct-440-2132b",      TYPEMCT440_2132B },
    { "mct-440-2133b",      TYPEMCT440_2133B },
    { "repeater 800",       TYPE_REPEATER800 },
    { "repeater 801",       TYPE_REPEATER800 },
    { "repeater 850",       TYPE_REPEATER850 },
    { "repeater 902",       TYPE_REPEATER900 },
    { "repeater 921",       TYPE_REPEATER900 },
    { "repeater",           TYPE_REPEATER900 },

    //  --- Receivers ---
    { "page receiver",      TYPE_PAGING_RECEIVER },

    //  --- RF mesh meters ---
    { "rfn-410fl",          TYPE_RFN410FL },
    { "rfn-410fx",          TYPE_RFN410FX },
    { "rfn-410fd",          TYPE_RFN410FD },
    { "rfn-510fl",          TYPE_RFN510FL },

    { "rfn-420fl",          TYPE_RFN420FL },
    { "rfn-420fx",          TYPE_RFN420FX },
    { "rfn-420fd",          TYPE_RFN420FD },

    { "rfn-420frx",         TYPE_RFN420FRX },
    { "rfn-420frd",         TYPE_RFN420FRD },
    { "rfn-520fax",         TYPE_RFN520FAX },
    { "rfn-520frx",         TYPE_RFN520FRX },
    { "rfn-520faxd",        TYPE_RFN520FAXD },
    { "rfn-520frxd",        TYPE_RFN520FRXD },
    { "rfn-530fax",         TYPE_RFN530FAX },
    { "rfn-530frx",         TYPE_RFN530FRX },

    { "rfn-410cl",          TYPE_RFN410CL },
    { "rfn-420cl",          TYPE_RFN420CL },
    { "rfn-420cd",          TYPE_RFN420CD },

    { "rfn-430a3d",         TYPE_RFN430A3D },
    { "rfn-430a3t",         TYPE_RFN430A3T },
    { "rfn-430a3k",         TYPE_RFN430A3K },
    { "rfn-430a3r",         TYPE_RFN430A3R },

    { "rfn-430kv",          TYPE_RFN430KV },

    { "rfn-430sl0",         TYPE_RFN430SL0 },
    { "rfn-430sl1",         TYPE_RFN430SL1 },
    { "rfn-430sl2",         TYPE_RFN430SL2 },
    { "rfn-430sl3",         TYPE_RFN430SL3 },
    { "rfn-430sl4",         TYPE_RFN430SL4 },

    { "rfn-530s4x",         TYPE_RFN530S4X },
    { "rfn-530s4eax",       TYPE_RFN530S4EAX },
    { "rfn-530s4eaxr",      TYPE_RFN530S4EAXR },
    { "rfn-530s4erx",       TYPE_RFN530S4ERX },
    { "rfn-530s4erxr",      TYPE_RFN530S4ERXR },

    //  --- RF DA nodes ---
    { "rfn-1200",           TYPE_RFN1200 },

    //  --- RF water meters ---
    { "rfw-201",            TYPE_RFW201 },

    //  --- RF gas meters ---
    { "rfg-201",            TYPE_RFG201 },
    { "rfg-301",            TYPE_RFG301 },

    //  --- RTU devices ---
    { "rtu-dart",           TYPE_DARTRTU },
    { "rtu-dnp",            TYPE_DNPRTU },
    { "rtu-ilex",           TYPE_ILEXRTU },
    { "rtu-lmi",            TYPE_SERIESVLMIRTU },
    { "rtu-modbus",         TYPE_MODBUS },
    { "rtu-ses92",          TYPE_SES92RTU },
    { "rtu-welco",          TYPE_WELCORTU },

    //  --- GRE (Great River Energy) transmitters ---
    { "rtc",                TYPE_RTC },
    { "rtm",                TYPE_RTM },

    //  --- GRE (Great River Energy) Load Management groups ---
    { "golay group",        TYPE_LMGROUP_GOLAY },
    { "sa-105 group",       TYPE_LMGROUP_SA105 },
    { "sa-205 group",       TYPE_LMGROUP_SA205 },
    { "sa-305 group",       TYPE_LMGROUP_SA305 },
    { "sa-digital group",   TYPE_LMGROUP_SADIGITAL },

    //  --- Load Management ---
    { "ci customer",        TYPE_CI_CUSTOMER },
    { "lm control area",    TYPE_LM_CONTROL_AREA },
    { "lm curtail program", TYPE_LMPROGRAM_CURTAILMENT },
    { "lm direct program",  TYPE_LMPROGRAM_DIRECT },
    { "lm energy exchange", TYPE_LMPROGRAM_ENERGYEXCHANGE },
    { "lm sep program",     TYPE_LMPROGRAM_DIRECT },
    { "ecobee program",     TYPE_LMPROGRAM_DIRECT },
    { "honeywell program",  TYPE_LMPROGRAM_DIRECT },
    { "nest program",       TYPE_LMPROGRAM_DIRECT },
    { "itron program",      TYPE_LMPROGRAM_DIRECT },
    { "digi sep group",     TYPE_LMGROUP_DIGI_SEP },
    { "ecobee group",       TYPE_LMGROUP_ECOBEE },
    { "honeywell group",    TYPE_LMGROUP_HONEYWELL },
    { "nest group",         TYPE_LMGROUP_NEST },
    { "itron group",        TYPE_LMGROUP_ITRON },
    { "emetcon group",      TYPE_LMGROUP_EMETCON },
    { "expresscom group",   TYPE_LMGROUP_EXPRESSCOM },
    { "rfn expresscom group",TYPE_LMGROUP_RFN_EXPRESSCOM },
    { "mct group",          TYPE_LMGROUP_MCT },
    { "point group",        TYPE_LMGROUP_POINT },
    { "ripple group",       TYPE_LMGROUP_RIPPLE },
    { "versacom group",     TYPE_LMGROUP_VERSACOM },

    //  --- System ---
    { "macro group",        TYPE_MACRO },
    { "script",             TYPE_NONE },
    { "simple",             TYPE_NONE },
    { "system",             TYPE_SYSTEM },
    { "virtual system",     TYPE_VIRTUAL_SYSTEM },

    //  --- Transmitters ---
    { "lcu-415",            TYPE_LCU415 },
    { "lcu-eastriver",      TYPE_LCU415ER },
    { "lcu-lg",             TYPE_LCU415LG },
    { "lcu-t3026",          TYPE_LCUT3026 },
    { "rds terminal",       TYPE_RDS },
    { "snpp terminal",      TYPE_SNPP },
    { "tap terminal",       TYPE_TAPTERM },
    { "tcu-5000",           TYPE_TCU5000 },
    { "tcu-5500",           TYPE_TCU5500 },
    { "tnpp terminal",      TYPE_TNPP },
    { "wctp terminal",      TYPE_WCTP },

    //  --- IEDs and electronic meters ---
    { "alpha a1",           TYPE_ALPHA_A1 },
    { "alpha a3",           TYPE_ALPHA_A3 },
    { "alpha power plus",   TYPE_ALPHA_PPLUS },
    { "davis weather",      TYPE_DAVIS },
    { "dct-501",            TYPEDCT501 },
    { "dr-87",              TYPE_DR87 },
    { "focus",              TYPE_FOCUS },
    { "ipc-410fl",          TYPE_IPC_410FL },
    { "ipc-420fd",          TYPE_IPC_420FD },
    { "ipc-430s4e",         TYPE_IPC_430S4E },
    { "ipc-430sl",          TYPE_IPC_430SL },
    { "fulcrum",            TYPE_FULCRUM },
    { "ion-7330",           TYPE_ION7330 },
    { "ion-7700",           TYPE_ION7700 },
    { "ion-8300",           TYPE_ION8300 },
    { "kv",                 TYPE_KV2 },
    { "kv2",                TYPE_KV2 },
    { "landis-gyr s4",      TYPE_LGS4 },
    { "quantum",            TYPE_QUANTUM },
    { "sentinel",           TYPE_SENTINEL },
    { "sixnet",             TYPE_SIXNET },
    { "transdata mark-v",   TYPE_TDMARKV },
    { "vectron",            TYPE_VECTRON }
};


DeviceTypes resolveDeviceType(const string& _typeStr)
{
    string typestr = sanitizeInput( _typeStr );

    if( const auto deviceType = Cti::mapFind(device_lookups, typestr) )
    {
        return *deviceType;
    }

    if( ! isKnownUnsupportedDevice(typestr) )
    {
        CTILOG_ERROR(dout, "Unsupported DEVICE type \"" << typestr << "\"");
    }

    return TYPE_NONE;
}

/**
 * Check if the device is known and unsupported
 */
bool isKnownUnsupportedDevice(const string& typeStr)
{
    //  --- Known unsupported Devices ---
    //  Do not report an error ("Unsupported DEVICE type ...") if we try to resolve any of these
    static const std::set<string> unsupported_devices
    {
        "digi gateway",
        "rf gateway",
        "gwy-800",
        "zigbee endpoint",
        "rfn-440-2131td",
        "rfn-440-2132td",
        "rfn-440-2133td",
        "rfw-meter",
        "lcr-6200 rfn",
        "lcr-6600 rfn",
        "lcr-6700 rfn",
        "weather location",
        "ecobee3",
        "ecobee3 lite",
        "ecobee smart",
        "ecobee smart si",
        "ltc",
        "go_regulator",
        "po_regulator",
        "honeywell wi-fi 9000",
        "honeywell wi-fi focuspro",
        "honeywell wi-fi thermostat",
        "honeywell wi-fi visionpro 8000",
        "rfn relay"
    };

    return unsupported_devices.count( sanitizeInput( typeStr ) );
}


INT resolveCapControlType(const string& _typeStr)
{
    string typeStr = sanitizeInput( _typeStr );

    static const std::map<std::string, DeviceTypes>  deviceTypeLookup
    {
        { "ccarea",         TYPE_CC_AREA },
        { "ccsubstation",   TYPE_CC_SUBSTATION },
        { "ccsubbus",       TYPE_CC_SUBSTATION_BUS },
        { "ccfeeder",       TYPE_CC_FEEDER },
        { "ccspecialarea",  TYPE_CC_SPECIALAREA }
    };

    if ( const auto result = Cti::mapFind( deviceTypeLookup, typeStr ) )
    {
        return *result;
    }

    CTILOG_ERROR(dout, "Unsupported CAP CONTROL type \"" << typeStr << "\"");

    return 0;
}

INT resolveLoadManagementType(const string& _typeStr)
{
    string typeStr = sanitizeInput( _typeStr );

    static const std::map<std::string, DeviceTypes>  deviceTypeLookup
    {
        { "lm direct program",  TYPE_LMPROGRAM_DIRECT },
        { "lm sep program",     TYPE_LMPROGRAM_DIRECT },
        { "ecobee program",     TYPE_LMPROGRAM_DIRECT },
        { "honeywell program",  TYPE_LMPROGRAM_DIRECT },
        { "nest program",       TYPE_LMPROGRAM_DIRECT },
        { "itron program",      TYPE_LMPROGRAM_DIRECT },
        { "lm curtail program", TYPE_LMPROGRAM_CURTAILMENT },
        { "lm control area",    TYPE_LM_CONTROL_AREA },
        { "ci customer",        TYPE_CI_CUSTOMER },
        { "lm energy exchange", TYPE_LMPROGRAM_ENERGYEXCHANGE }
    };

    if ( const auto result = Cti::mapFind( deviceTypeLookup, typeStr ) )
    {
        return *result;
    }

    CTILOG_ERROR(dout, "Unsupported LOAD MANAGEMENT type \"" << typeStr << "\"");

    return 0;
}

INT resolveScanType(const string& _typeStr)
{
    string typeStr = sanitizeInput( _typeStr );

    static const std::map<std::string, CtiScanRate_t>  scanRateLookup
    {
        { "general",        ScanRateGeneral },
        { "accumulator",    ScanRateAccum },
        { "integrity",      ScanRateIntegrity },
        { "status",         ScanRateGeneral },
        { "exception",      ScanRateGeneral },
        { "loadprofile",    ScanRateLoadProfile }
    };

    if ( const auto result = Cti::mapFind( scanRateLookup, typeStr ) )
    {
        return *result;
    }

    CTILOG_ERROR(dout, "Unsupported scan rate type \"" << typeStr << "\"");

    return ScanRateInvalid;
}

LONG resolveDeviceWindowType(const string& _windowStr)
{
    string windowStr = sanitizeInput( _windowStr );

    static const std::map<std::string, CtiDeviceWindow_t>  windowLookup
    {
        { DEVICE_WINDOW_TYPE_SCAN,              DeviceWindowScan },
        { DEVICE_WINDOW_TYPE_PEAK,              DeviceWindowPeak },
        { DEVICE_WINDOW_TYPE_ALTERNATE_RATE,    DeviceWindowAlternateRate }
    };

    if ( const auto result = Cti::mapFind( windowLookup, windowStr ) )
    {
        return *result;
    }

    CTILOG_ERROR(dout, "Unsupported device window type \"" << windowStr << "\"");

    return DeviceWindowInvalid;
}

INT resolvePAOClass(const string& _classStr)
{
    string classStr = sanitizeInput( _classStr );

    static const std::map<std::string, CtiPAOClass_t>  paoClassLookup
    {
        { "transmitter",    PAOClassTransmitter },
        { "rtu",            PAOClassRTU },
        { "ied",            PAOClassIED },
        { "carrier",        PAOClassCarrier },
        { "meter",          PAOClassMeter },
        { "rfmesh",         PAOClassRFMesh },
        { "gridadvisor",    PAOClassGridAdvisor },
        { "group",          PAOClassGroup },
        { "system",         PAOClassSystem },
        { "capcontrol",     PAOClassCapControl },
        { "loadmanagement", PAOClassLoadManagement },
        { "virtual",        PAOClassVirtual },
        { "port",           PAOClassPort },
        { "route",          PAOClassRoute },
        { "schedule",       PAOClassMACS }
    };

    if ( const auto result = Cti::mapFind( paoClassLookup, classStr ) )
    {
        return *result;
    }

    CTILOG_ERROR(dout, "Unsupported device class \"" << classStr << "\"");

    return PAOClassInvalid;
}

INT resolveProtocol(const string& _str)
{
    string str = sanitizeInput( _str );

    static const std::map<std::string, CtiProtocolWrap_t>  protocolLookup
    {
        { "idlc",    ProtocolWrapIDLC },
        { "none",    ProtocolWrapNone }
    };

    if ( const auto result = Cti::mapFind( protocolLookup, str ) )
    {
        return *result;
    }

    CTILOG_ERROR(dout, "Unknown port protocol wrap " << str);

    return 0;
}

INT resolvePortType(const string& _str)
{
    const std::map<std::string, int> PortTypes
    {
        { "local serial port",        PortTypeLocalDirect     },
        { "local dialup",             PortTypeLocalDialup     },
        { "terminal server",          PortTypeTServerDirect   },
        { "tcp",                      PortTypeTcp             },
        { "udp",                      PortTypeUdp             },
        { "terminal server dialup",   PortTypeTServerDialup   },
        { "local dialback",           PortTypeLocalDialBack   },
        { "terminal server dialback", PortTypeTServerDialBack },
        { "dialout pool",             PortTypePoolDialout     },
        { "rfn-1200",                 PortTypeRfDa            }
    };

    if( const auto portType = Cti::mapFind(PortTypes, sanitizeInput( _str ) ) )
    {
        return *portType;
    }

    return PortTypeInvalid;
}


bool resolveIsDeviceTypeSingle(INT Type)
{
    static const std::set<DeviceTypes>  targetableDevices
    {
        TYPE_CCU721,
        TYPE_CCU711,
        TYPE_CCU710,
        TYPE_CCU700,
        TYPE_REPEATER800,
        TYPE_REPEATER850,
        TYPE_REPEATER900,
        TYPE_ILEXRTU,
        TYPE_WELCORTU,
        TYPE_SES92RTU,
        TYPE_DNPRTU,
        TYPE_DARTRTU,
        TYPE_SERIESVRTU,
        TYPE_SERIESVLMIRTU,
        TYPE_ION7330,
        TYPE_ION7700,
        TYPE_ION8300,
        TYPE_LCU415,
        TYPE_LCU415LG,
        TYPE_LCU415ER,
        TYPE_LCUT3026,
        TYPE_TAPTERM,
        TYPE_SNPP,
        TYPE_RDS,
        TYPE_TNPP,
        TYPE_WCTP,
        TYPE_TCU5000,
        TYPE_TCU5500,
        TYPE_TDMARKV,
        TYPE_DAVIS,
        TYPE_ALPHA_A1,
        TYPE_ALPHA_PPLUS,
        TYPE_FULCRUM,
        TYPE_QUANTUM,
        TYPE_VECTRON,
        TYPE_LGS4,
        TYPE_IPC_430S4E,
        TYPE_DR87,
        TYPE_KV2,
        TYPE_ALPHA_A3,
        TYPE_SENTINEL,
        TYPE_IPC_430SL,
        TYPE_FOCUS,
        TYPE_IPC_410FL,
        TYPE_IPC_420FD,
        TYPE_SIXNET,
        TYPEDCT501,
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
        TYPEMCT310IL,
        TYPEMCT310IDL,
        TYPEMCT318,
        TYPEMCT318L,
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
        TYPEMCT430A,
        TYPEMCT430A3,
        TYPEMCT430S4,
        TYPEMCT430SL,
        TYPEMCT470,
        TYPEMCT440_2131B,
        TYPEMCT440_2132B,
        TYPEMCT440_2133B,
        TYPE_RFN1200,
        TYPE_RFW201,
        TYPE_RFG201,
        TYPE_RFN410FL,
        TYPE_RFN410FX,
        TYPE_RFN410FD,
        TYPE_RFN420FL,
        TYPE_RFN420FX,
        TYPE_RFN420FD,
        TYPE_RFN420FRX,
        TYPE_RFN420FRD,
        TYPE_RFN410CL,
        TYPE_RFN420CL,
        TYPE_RFN420CD,
        TYPE_RFN430A3D,
        TYPE_RFN430A3T,
        TYPE_RFN430A3K,
        TYPE_RFN430A3R,
        TYPE_RFN430KV,
        TYPE_RFN430SL0,
        TYPE_RFN430SL1,
        TYPE_RFN430SL2,
        TYPE_RFN430SL3,
        TYPE_RFN430SL4,
        TYPE_RFN510FL,
        TYPE_RFN520FAX,
        TYPE_RFN520FRX,
        TYPE_RFN520FAXD,
        TYPE_RFN520FRXD,
        TYPE_RFN530FAX,
        TYPE_RFN530FRX,
        TYPE_RFN530S4X,
        TYPE_RFN530S4EAX,
        TYPE_RFN530S4EAXR,
        TYPE_RFN530S4ERX,
        TYPE_RFN530S4ERXR,
        TYPELCR3102,
        TYPE_LOAD_TAP_CHANGER,
        TYPE_GANG_OPERATED_REGULATOR,
        TYPE_PHASE_OPERATED_REGULATOR,
        TYPE_MODBUS,
        TYPELMT2,
        TYPE_CBC7020,
        TYPE_CBC8020,
        TYPE_CBCDNP,
        TYPE_RTC,
        TYPE_RTM,
        TYPE_PAGING_RECEIVER,
        TYPE_FCI,
        TYPE_NEUTRAL_MONITOR
    };

    static const std::set<DeviceTypes>  nontargetableDevices
    {
        TYPE_SYSTEM,
        TYPE_VIRTUAL_SYSTEM,
        TYPE_VERSACOMCBC,
        TYPE_CBC7010,
        TYPE_EXPRESSCOMCBC,
        TYPE_FISHERPCBC,
        TYPE_CBCLOGICAL,
        TYPE_LMGROUP_EMETCON,
        TYPE_LMGROUP_POINT,
        TYPE_LMGROUP_RIPPLE,
        TYPE_LMGROUP_VERSACOM,
        TYPE_LMGROUP_EXPRESSCOM,
        TYPE_LMGROUP_RFN_EXPRESSCOM,
        TYPE_LMGROUP_DIGI_SEP,
        TYPE_LMGROUP_ECOBEE,
        TYPE_LMGROUP_MCT,
        TYPE_LMGROUP_GOLAY,
        TYPE_LMGROUP_SADIGITAL,
        TYPE_LMGROUP_SA105,
        TYPE_LMGROUP_SA205,
        TYPE_LMGROUP_SA305,
        TYPEMCTBCAST,
        TYPE_MACRO
    };

    if ( targetableDevices.count( static_cast<DeviceTypes>(Type) ) )
    {
        return true;
    }

    if ( ! nontargetableDevices.count( static_cast<DeviceTypes>(Type) ) )
    {
        CTILOG_ERROR(dout, "Unable to determine whether device type " << Type <<  " is a targetable device type!");
    }

    return false;
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
            CTILOG_ERROR(dout, "Invalid address usage type " << str);
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
        CTILOG_WARN(dout, "Time " << Seconds << " is not an Emetcon standard time.  Using 1 hour.");
        nRet = TIME_60;
    }

    return nRet;
}

string   resolveDBChangeType(INT type)
{
    static const std::map<CtiDBChangedType_t, std::string>  lookup
    {
        { ChangeTypeAdd,    " ADDED TO" },
        { ChangeTypeDelete, " DELETED FROM" },
        { ChangeTypeUpdate, " UPDATED IN" }
    };
    
    if ( const auto result = Cti::mapFind( lookup, static_cast<CtiDBChangedType_t>(type) ) )
    {
        return *result;
    }

    return " CHANGED IN";
}

string   resolveDBChanged(INT dbnum)
{
    static const std::map<CtiDBChanged_t, std::string>  lookup
    {
        { ChangePAODb,                      " PAO DB" },
        { ChangePointDb,                    " POINT DB" },
        { ChangeStateGroupDb,               " GROUP DB" },
        { ChangeNotificationGroupDb,        " NOTIFICATIONGROUP/DESTINATION DB" },
        { ChangeNotificationRecipientDb,    " GROUPRECIPIENT DB" },
        { ChangeAlarmCategoryDb,            " ALARM Category DB" },
        { ChangeCustomerContactDb,          " Customer Contact DB" },
        { ChangeGraphDb,                    " Graph DB" },
        { ChangeHolidayScheduleDb,          " Holiday Schedule DB" },
        { ChangeEnergyCompanyDb,            " Energy Company DB" },
        { ChangeYukonUserDb,                " Yukon User DB" },
        { ChangeCustomerDb,                 " Yukon Customer DB" },
        { ChangeCustomerAccountDb,          " Yukon Customer Account DB" },
        { ChangeYukonImageDb,               " Yukon Image DB" },
        { ChangeBaselineDb,                 " Yukon Baseline DB" },
        { ChangeConfigDb,                   " Yukon Config DB" },
        { ChangeTagDb,                      " Yukon Tag DB" },
        { ChangeCICustomerDb,               " Yukon CI Customer DB" },
        { ChangeLMConstraintDb,             " Yukon LM Constraint DB" },
        { ChangeSeasonScheduleDb,           " Yukon Season Schedule DB" }
    };

    if ( const auto result = Cti::mapFind( lookup, static_cast<CtiDBChanged_t>(dbnum) ) )
    {
        return *result;
    }

    return " DATABASE";
}

CtiDBChangeCategory resolveDBCategory(const string& category)
{
    static const std::map<std::string, CtiDBChangeCategory>  lookup
    {
        { "energycompany",                  CtiDBChangeCategory::EnergyCompany },
        { "energy_company_route",           CtiDBChangeCategory::EnergyCompanyRoute },
        { "energy_company_substations",     CtiDBChangeCategory::EnergyCompanySubstations },
        { "appliance",                      CtiDBChangeCategory::Appliance },
        { "servicecompany",                 CtiDBChangeCategory::ServiceCompany },
        { "servicecompanydesignationcode",  CtiDBChangeCategory::ServiceCompanyDesignationCode },
        { "warehouse",                      CtiDBChangeCategory::Warehouse },
        { "yukonselectionlist",             CtiDBChangeCategory::YukonSelectionList },
        { "yukonlistentry",                 CtiDBChangeCategory::YukonListEntry },
        { "porter_response_monitor",        CtiDBChangeCategory::PorterResponseMonitor },
        { "device_data_monitor",            CtiDBChangeCategory::DeviceDataMonitor },
        { "cc_monitor_bank_list",           CtiDBChangeCategory::CCMonitorBankList },
        { "globalsetting",                  CtiDBChangeCategory::GlobalSetting },
        { "energycompanysetting",           CtiDBChangeCategory::EnergyCompanySetting },
        { "repeatingjob",                   CtiDBChangeCategory::RepeatingJob },
        { "dataexportformat",               CtiDBChangeCategory::DataExportFormat },
        { "monitor",                        CtiDBChangeCategory::Monitor }
    };

    if (const auto result = Cti::mapFind(lookup, sanitizeInput(category)))
    {
        return *result;
    }

    return CtiDBChangeCategory::Invalid;
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
                CTILOG_ERROR(dout, "Slave Address not defined");
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
                CTILOG_ERROR(dout, "Slave Address not defined");
            }

            break;
        }
    case TYPE_RTM:
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
                CTILOG_ERROR(dout, "Slave Address not defined");
            }
            break;
        }
    case TYPE_ALPHA_A1:
    case TYPE_DAVIS:
        {
            slaveAddress = 0;
            break;
        }
    case TYPE_LGS4:
    case TYPE_IPC_430S4E:
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
    case TYPE_IPC_410FL:
    case TYPE_IPC_420FD:
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
            CTILOG_ERROR(dout, "Device Type " << DeviceType << " not resolved" );
            break;
        }
    }


    return slaveAddress;
}

CtiControlType_t  resolveControlType(const string& _str)
{
    string str = sanitizeInput( _str );

    static const std::map<std::string, CtiControlType_t> ControlTypes
    {
        { "normal",    ControlType_Normal },
        { "latch",     ControlType_Latch },
        { "pseudo",    ControlType_Pseudo },
        { "sbo pulse", ControlType_SBOPulse },
        { "sbo latch", ControlType_SBOLatch }
    };

    if( const auto controlType = Cti::mapFind(ControlTypes, str) )
    {
        return *controlType;
    }

    CTILOG_ERROR(dout, "Unknown control type \"" << str << "\"");

    return ControlType_Invalid;
}

ErrorTypes resolveErrorType(const string& _str)
{
    string str = sanitizeInput(_str);

    static const std::map<std::string, ErrorTypes> ErrorTypeMap
    {
        { "none",       ERRTYPENONE },
        { "unknown",    ERRTYPEUNKNOWN },
        { "system",     ERRTYPESYSTEM },
        { "protocol",   ERRTYPEPROTOCOL },
        { "comm",       ERRTYPECOMM }
    };

    if (const auto errorType = Cti::mapFind( ErrorTypeMap, str ) )
    {
        return *errorType;
    }

    CTILOG_ERROR(dout, "Unknown error type \"" << str << "\"");

    return ERRTYPEUNKNOWN;
}
