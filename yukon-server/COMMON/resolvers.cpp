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


static const std::map<std::string, int> device_lookups = boost::assign::map_list_of<std::string, int>
    //  --- GridSmart ---
    ("capacitor bank neutral monitor",  TYPE_NEUTRAL_MONITOR)
    ("faulted circuit indicator",       TYPE_FCI)

    //  --- Capacitor Control ---
    ("cap bank",           TYPECAPBANK)
    ("cbc 6510",           TYPECBC6510)
    ("cbc 7010",           TYPECBC7010)
    ("cbc 7011",           TYPECBC7010)
    ("cbc 7012",           TYPECBC7010)
    ("cbc 7020",           TYPECBC7020)
    ("cbc 7022",           TYPECBC7020)
    ("cbc 7023",           TYPECBC7020)
    ("cbc 7024",           TYPECBC7020)
    ("cbc 7030",           TYPECBC7020)
    ("cbc 8020",           TYPECBC8020)
    ("cbc 8024",           TYPECBC8020)
    ("cbc dnp",            TYPECBCDNP)
    ("cbc expresscom",     TYPEEXPRESSCOMCBC)
    ("cbc fp-2800",        TYPEFISHERPCBC)
    ("cbc versacom",       TYPEVERSACOMCBC)
    ("load tap changer",   TYPELTC)

    //  --- Cooper PLC ---
    ("ccu-700",            TYPE_CCU700)
    ("ccu-710a",           TYPE_CCU710)
    ("ccu-711",            TYPE_CCU711)
    ("ccu-721",            TYPE_CCU721)
    ("lcr-3102",           TYPELCR3102)
    ("lmt-2",              TYPELMT2)
    ("mct broadcast",      TYPEMCTBCAST)
    ("mct-210",            TYPEMCT210)
    ("mct-212",            TYPEMCT212)
    ("mct-213",            TYPEMCT213)
    ("mct-224",            TYPEMCT224)
    ("mct-226",            TYPEMCT226)
    ("mct-240",            TYPEMCT240)
    ("mct-242",            TYPEMCT242)
    ("mct-248",            TYPEMCT248)
    ("mct-250",            TYPEMCT250)
    ("mct-310",            TYPEMCT310)
    ("mct-310ct",          TYPEMCT310)
    ("mct-310id",          TYPEMCT310ID)
    ("mct-310idl",         TYPEMCT310IDL)
    ("mct-310il",          TYPEMCT310IL)
    ("mct-318",            TYPEMCT318)
    ("mct-318l",           TYPEMCT318L)
    ("mct-360",            TYPEMCT360)
    ("mct-370",            TYPEMCT370)
    ("mct-410cl",          TYPEMCT410CL)
    ("mct-410fl",          TYPEMCT410FL)
    ("mct-410gl",          TYPEMCT410GL)
    ("mct-410il",          TYPEMCT410IL)
    ("mct-420cl",          TYPEMCT420CL)
    ("mct-420cd",          TYPEMCT420CD)
    ("mct-420fl",          TYPEMCT420FL)
    ("mct-420fd",          TYPEMCT420FD)
    ("mct-430a",           TYPEMCT430A)
    ("mct-430a3",          TYPEMCT430A3)
    ("mct-430s4",          TYPEMCT430S4)
    ("mct-430sl",          TYPEMCT430SL)
    ("mct-470",            TYPEMCT470)
    ("mct-440-2131b",      TYPEMCT440_2131B)
    ("mct-440-2132b",      TYPEMCT440_2132B)
    ("mct-440-2133b",      TYPEMCT440_2133B)
    ("repeater 800",       TYPE_REPEATER800)
    ("repeater 801",       TYPE_REPEATER800)
    ("repeater 850",       TYPE_REPEATER850)
    ("repeater 902",       TYPE_REPEATER900)
    ("repeater 921",       TYPE_REPEATER900)
    ("repeater",           TYPE_REPEATER900)

    //  --- Receivers ---
    ("page receiver",      TYPE_PAGING_RECEIVER)

    //  --- RF mesh meters ---
    ("rfn-410fl",          TYPE_RFN410FL)
    ("rfn-410fx",          TYPE_RFN410FX)
    ("rfn-410fd",          TYPE_RFN410FD)

    ("rfn-420fl",          TYPE_RFN420FL)
    ("rfn-420fx",          TYPE_RFN420FX)
    ("rfn-420fd",          TYPE_RFN420FD)

    ("rfn-420frx",         TYPE_RFN420FRX)
    ("rfn-420frd",         TYPE_RFN420FRD)

    ("rfn-410cl",          TYPE_RFN410CL)
    ("rfn-420cl",          TYPE_RFN420CL)
    ("rfn-420cd",          TYPE_RFN420CD)

    ("rfn-430a3d",         TYPE_RFN430A3D)
    ("rfn-430a3t",         TYPE_RFN430A3T)
    ("rfn-430a3k",         TYPE_RFN430A3K)
    ("rfn-430a3r",         TYPE_RFN430A3R)

    ("rfn-430kv",          TYPE_RFN430KV)

    ("rfn-430sl0",         TYPE_RFN430SL0)
    ("rfn-430sl1",         TYPE_RFN430SL1)
    ("rfn-430sl2",         TYPE_RFN430SL2)
    ("rfn-430sl3",         TYPE_RFN430SL3)
    ("rfn-430sl4",         TYPE_RFN430SL4)

    //  --- RF DA nodes ---
    ("rfn-1200",           TYPE_RFN1200)

    //  --- RTU devices ---
    ("rtu-dart",           TYPE_DARTRTU)
    ("rtu-dnp",            TYPE_DNPRTU)
    ("rtu-ilex",           TYPE_ILEXRTU)
    ("rtu-lmi",            TYPE_SERIESVLMIRTU)
    ("rtu-modbus",         TYPE_MODBUS)
    ("rtu-ses92",          TYPE_SES92RTU)
    ("rtu-welco",          TYPE_WELCORTU)

    //  --- GRE (Great River Energy) transmitters ---
    ("rtc",                TYPE_RTC)
    ("rtm",                TYPE_RTM)

    //  --- GRE (Great River Energy) Load Management groups ---
    ("golay group",        TYPE_LMGROUP_GOLAY)
    ("sa-105 group",       TYPE_LMGROUP_SA105)
    ("sa-205 group",       TYPE_LMGROUP_SA205)
    ("sa-305 group",       TYPE_LMGROUP_SA305)
    ("sa-digital group",   TYPE_LMGROUP_SADIGITAL)

    //  --- Load Management ---
    ("ci customer",        TYPE_CI_CUSTOMER)
    ("lm control area",    TYPE_LM_CONTROL_AREA)
    ("lm curtail program", TYPE_LMPROGRAM_CURTAILMENT)
    ("lm direct program",  TYPE_LMPROGRAM_DIRECT)
    ("lm energy exchange", TYPE_LMPROGRAM_ENERGYEXCHANGE)
    ("lm sep program",     TYPE_LMPROGRAM_DIRECT)
    ("ecobee program",     TYPE_LMPROGRAM_DIRECT)
    ("digi sep group",     TYPE_LMGROUP_DIGI_SEP)
    ("ecobee group",       TYPE_LMGROUP_ECOBEE)
    ("emetcon group",      TYPE_LMGROUP_EMETCON)
    ("expresscom group",   TYPE_LMGROUP_EXPRESSCOM)
    ("rfn expresscom group",TYPE_LMGROUP_RFN_EXPRESSCOM)
    ("mct group",          TYPE_LMGROUP_MCT)
    ("point group",        TYPE_LMGROUP_POINT)
    ("ripple group",       TYPE_LMGROUP_RIPPLE)
    ("versacom group",     TYPE_LMGROUP_VERSACOM)

    //  --- System ---
    ("macro group",        TYPE_MACRO)
    ("script",             0)
    ("simple",             0)
    ("system",             TYPE_SYSTEM)
    ("virtual system",     TYPE_VIRTUAL_SYSTEM)

    //  --- Transmitters ---
    ("lcu-415",            TYPE_LCU415)
    ("lcu-eastriver",      TYPE_LCU415ER)
    ("lcu-lg",             TYPE_LCU415LG)
    ("lcu-t3026",          TYPE_LCUT3026)
    ("rds terminal",       TYPE_RDS)
    ("snpp terminal",      TYPE_SNPP)
    ("tap terminal",       TYPE_TAPTERM)
    ("tcu-5000",           TYPE_TCU5000)
    ("tcu-5500",           TYPE_TCU5500)
    ("tnpp terminal",      TYPE_TNPP)
    ("wctp terminal",      TYPE_WCTP)

    //  --- IEDs and electronic meters ---
    ("alpha a1",           TYPE_ALPHA_A1)
    ("alpha a3",           TYPE_ALPHA_A3)
    ("alpha power plus",   TYPE_ALPHA_PPLUS)
    ("davis weather",      TYPE_DAVIS)
    ("dct-501",            TYPEDCT501)
    ("dr-87",              TYPE_DR87)
    ("focus",              TYPE_FOCUS)
    ("ipc-410fl",          TYPE_IPC_410FL)
    ("ipc-420fd",          TYPE_IPC_420FD)
    ("ipc-430s4e",         TYPE_IPC_430S4E)
    ("ipc-430sl",          TYPE_IPC_430SL)
    ("fulcrum",            TYPE_FULCRUM)
    ("ion-7330",           TYPE_ION7330)
    ("ion-7700",           TYPE_ION7700)
    ("ion-8300",           TYPE_ION8300)
    ("kv",                 TYPE_KV2)
    ("kv2",                TYPE_KV2)
    ("landis-gyr s4",      TYPE_LGS4)
    ("quantum",            TYPE_QUANTUM)
    ("sentinel",           TYPE_SENTINEL)
    ("sixnet",             TYPE_SIXNET)
    ("transdata mark-v",   TYPE_TDMARKV)
    ("vectron",            TYPE_VECTRON);


INT resolveDeviceType(const string& _rwsTemp)
{
    string typestr = boost::trim_copy(boost::to_lower_copy(_rwsTemp));

    if( const boost::optional<int> deviceType = Cti::mapFind(device_lookups, typestr) )
    {
        return *deviceType;
    }

    if( ! isKnownUnsupportedDevice(_rwsTemp) )
    {
        CTILOG_ERROR(dout, "Unsupported DEVICE type \"" << typestr << "\"");
    }

    return 0;
}

//  --- Known unsupported Devices ---
//  Do not report an error ("Unsupported DEVICE type ...") if we try to resolve any of these
static const std::set<string> unsupported_devices = boost::assign::list_of
    ("digi gateway")
    ("rf gateway")
    ("zigbee endpoint")
    ("rfn-440-2131td")
    ("rfn-440-2132td")
    ("rfn-440-2133td")
    ("rfw-meter")
    ("lcr-6200 rfn")
    ("lcr-6600 rfn")
    ("weather location")
    ("ecobee smart si");

/**
 * Check if the device is known and unsupported
 */
bool isKnownUnsupportedDevice(const string& _rwsTemp)
{
    return unsupported_devices.count( boost::to_lower_copy(_rwsTemp) );
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
        CTILOG_ERROR(dout, "Unsupported CAP CONTROL type \"" << rwsTemp << "\"");
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
       rwsTemp == "lm sep program" ||
       rwsTemp == "ecobee program")
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
        CTILOG_ERROR(dout, "Unsupported LOAD MANAGEMENT type \"" << rwsTemp << "\"");
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
        CTILOG_ERROR(dout, "Unsupported scan rate type \"" << rwsTemp << "\"");
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
        CTILOG_ERROR(dout, "Unsupported device window type \"" << rwsTemp << "\"");
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
    else if(rwsTemp == "rfmesh")
    {
        nRet = PAOClassRFMesh;
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
        CTILOG_ERROR(dout, "Unsupported device class \"" << rwsTemp << "\"");
        nRet = PAOClassInvalid;
    }

    return nRet;
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
        CTILOG_ERROR(dout, "Unknown port protocol wrap " << str);
    }

    return nRet;
}

const std::map<std::string, int> PortTypes = boost::assign::map_list_of
    ("local serial port",        PortTypeLocalDirect    )
    ("local dialup",             PortTypeLocalDialup    )
    ("terminal server",          PortTypeTServerDirect  )
    ("tcp",                      PortTypeTcp            )
    ("udp",                      PortTypeUdp            )
    ("terminal server dialup",   PortTypeTServerDialup  )
    ("local dialback",           PortTypeLocalDialBack  )
    ("terminal server dialback", PortTypeTServerDialBack)
    ("dialout pool",             PortTypePoolDialout    )
    ("rfn-1200",                 PortTypeRfDa           );


INT resolvePortType(const string& _str)
{
    string str = _str;
    CtiToLower(str);
    in_place_trim(str);

    if( const boost::optional<int> portType = Cti::mapFind(PortTypes, str) )
    {
        return *portType;
    }

    return PortTypeInvalid;
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
        case TYPE_IPC_430S4E:
        case TYPE_DR87:
        case TYPE_KV2:
        case TYPE_ALPHA_A3:
        case TYPE_SENTINEL:
        case TYPE_IPC_430SL:
        case TYPE_FOCUS:
        case TYPE_IPC_410FL:
        case TYPE_IPC_420FD:
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
        case TYPEMCT420CD:
        case TYPEMCT420FL:
        case TYPEMCT420FD:
        case TYPEMCT430A:
        case TYPEMCT430A3:
        case TYPEMCT430S4:
        case TYPEMCT430SL:
        case TYPEMCT470:
        case TYPEMCT440_2131B:
        case TYPEMCT440_2132B:
        case TYPEMCT440_2133B:
        case TYPE_RFN1200:
        case TYPE_RFN410FL:
        case TYPE_RFN410FX:
        case TYPE_RFN410FD:
        case TYPE_RFN420FL:
        case TYPE_RFN420FX:
        case TYPE_RFN420FD:
        case TYPE_RFN420FRX:
        case TYPE_RFN420FRD:
        case TYPE_RFN410CL:
        case TYPE_RFN420CL:
        case TYPE_RFN420CD:
        case TYPE_RFN430A3D:
        case TYPE_RFN430A3T:
        case TYPE_RFN430A3K:
        case TYPE_RFN430A3R:
        case TYPE_RFN430KV:
        case TYPE_RFN430SL0:
        case TYPE_RFN430SL1:
        case TYPE_RFN430SL2:
        case TYPE_RFN430SL3:
        case TYPE_RFN430SL4:
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
        case TYPE_LMGROUP_RFN_EXPRESSCOM:
        case TYPE_LMGROUP_DIGI_SEP:
        case TYPE_LMGROUP_ECOBEE:
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
            CTILOG_ERROR(dout, "Unable to determine whether device type " << Type <<  " is a targetable device type!");
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


static const std::map<std::string, CtiControlType_t> ControlTypes =
    boost::assign::map_list_of
        ("normal",    ControlType_Normal)
        ("latch",     ControlType_Latch)
        ("pseudo",    ControlType_Pseudo)
        ("sbo pulse", ControlType_SBOPulse)
        ("sbo latch", ControlType_SBOLatch);

CtiControlType_t  resolveControlType(const string& _str)
{
    string str = _str;
    CtiToLower(str);
    in_place_trim(str);

    std::map<std::string, CtiControlType_t>::const_iterator itr = ControlTypes.find(str);

    if( itr == ControlTypes.end() )
    {
        CTILOG_ERROR(dout, "Unknown control type \"" << str << "\"");
        return ControlType_Invalid;
    }

    return itr->second;
}

