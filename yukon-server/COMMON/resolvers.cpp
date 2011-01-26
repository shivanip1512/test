#include "yukon.h"

#include "dsm2.h"
#include "resolvers.h"
#include "devicetypes.h"
#include "pointtypes.h"
#include "logger.h"
#include "numstr.h"
#include "utility.h"

using std::transform;
using std::endl;


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
    else if(rwsTemp == "integration route")
    {
        Ret = RouteTypeXML;
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


INT resolveDeviceType(const string& _rwsTemp)
{
    INT nRet = 0;
    string rwsTemp = _rwsTemp;
    CtiToLower(rwsTemp);
    in_place_trim(rwsTemp);

    if(rwsTemp == "mct-410il" || rwsTemp == "mct-410cl" ||
       rwsTemp == "mct-410fl" || rwsTemp == "mct-410gl")
    {
        nRet = TYPEMCT410;
    }
    else if(rwsTemp == "mct-420cl")
    {
        nRet = TYPEMCT420CL;
    }
    else if(rwsTemp == "mct-420cld")
    {
        nRet = TYPEMCT420CLD;
    }
    else if(rwsTemp == "mct-420fl")
    {
        nRet = TYPEMCT420FL;
    }
    else if(rwsTemp == "mct-420fld")
    {
        nRet = TYPEMCT420FLD;
    }
    else if(rwsTemp == "mct-470")
    {
        nRet = TYPEMCT470;
    }
    else if (rwsTemp == "mct-430a"  || rwsTemp == "mct-430a3"
             || rwsTemp == "mct-430s4" || rwsTemp == "mct-430sl")
    {
        nRet = TYPEMCT430;
    }
    else if(rwsTemp == "lcr-3102")
    {
        nRet = TYPELCR3102;
    }
    else if(rwsTemp == "load tap changer")
    {
        nRet = TYPELTC;
    }
    else if(rwsTemp == "ccu-711")
    {
        nRet = TYPE_CCU711;
    }
    else if(rwsTemp == "ccu-721")
    {
        nRet = TYPE_CCU721;
    }
    else if(rwsTemp == "ccu-710a")
    {
        nRet = TYPE_CCU710;
    }
    else if(rwsTemp == "ccu-700")
    {
        nRet = TYPE_CCU700;
    }
    else if(rwsTemp == "repeater" ||
            rwsTemp == "repeater 902" ||
            rwsTemp == "repeater 921")
    {
        nRet = TYPE_REPEATER900;
    }
    else if(rwsTemp == "repeater 800" ||
            rwsTemp == "repeater 801")
    {
        nRet = TYPE_REPEATER800;
    }
    else if(rwsTemp == "repeater 850")
    {
        nRet = TYPE_REPEATER850;
    }
    else if(rwsTemp == "dct-501")
    {
        nRet = TYPEDCT501;
    }
    else if(rwsTemp == "mct-210")
    {
        nRet = TYPEMCT210;
    }
    else if(rwsTemp == "mct-212")
    {
        nRet = TYPEMCT212;
    }
    else if(rwsTemp == "mct-213")
    {
        nRet = TYPEMCT213;
    }
    else if(rwsTemp == "mct-224")
    {
        nRet = TYPEMCT224;
    }
    else if(rwsTemp == "mct-226")
    {
        nRet = TYPEMCT226;
    }
    else if(rwsTemp == "mct-240")
    {
        nRet = TYPEMCT240;
    }
    else if(rwsTemp == "mct-242")
    {
        nRet = TYPEMCT242;
    }
    else if(rwsTemp == "mct-248")
    {
        nRet = TYPEMCT248;
    }
    else if(rwsTemp == "mct-250")
    {
        nRet = TYPEMCT250;
    }
    else if(rwsTemp == "mct-310")
    {
        nRet = TYPEMCT310;
    }
    else if(rwsTemp == "mct-310ct")
    {
        nRet = TYPEMCT310;
    }
    else if(rwsTemp == "mct-310idl")
    {
        nRet = TYPEMCT310IDL;
    }
    else if(rwsTemp == "mct-310id")
    {
        nRet = TYPEMCT310ID;
    }
    else if(rwsTemp == "mct-310il")
    {
        nRet = TYPEMCT310IL;
    }
    else if(rwsTemp == "mct-318")
    {
        nRet = TYPEMCT318;
    }
    else if(rwsTemp == "mct-318l")
    {
        nRet = TYPEMCT318L;
    }
    else if(rwsTemp == "mct-360")
    {
        nRet = TYPEMCT360;
    }
    else if(rwsTemp == "mct-370")
    {
        nRet = TYPEMCT370;
    }
    else if(rwsTemp == "lmt-2")
    {
        nRet = TYPELMT2;
    }
    else if(rwsTemp == "rtu-ilex")
    {
        nRet = TYPE_ILEXRTU;
    }
    else if(rwsTemp == "rtu-welco")
    {
        nRet = TYPE_WELCORTU;
    }
    else if(rwsTemp == "rtu-ses92")
    {
        nRet = TYPE_SES92RTU;
    }
    else if(rwsTemp == "rtu-dnp")
    {
        nRet = TYPE_DNPRTU;
    }
    else if(rwsTemp == "rtu-dart")
    {
        nRet = TYPE_DARTRTU;
    }
    else if(rwsTemp == "rtu-lmi")
    {
        nRet = TYPE_SERIESVLMIRTU;
    }
    else if(rwsTemp == "rtu-modbus")
    {
        nRet = TYPE_MODBUS;
    }
    else if(rwsTemp == "ion-7330")
    {
        nRet = TYPE_ION7330;
    }
    else if(rwsTemp == "ion-7700")
    {
        nRet = TYPE_ION7700;
    }
    else if(rwsTemp == "ion-8300")
    {
        nRet = TYPE_ION8300;
    }
    else if(rwsTemp == "davis weather")
    {
        nRet = TYPE_DAVIS;
    }
    else if(rwsTemp == "lcu-415")
    {
        nRet = TYPE_LCU415;
    }
    else if(rwsTemp == "lcu-lg")
    {
        nRet = TYPE_LCU415LG;
    }
    else if(rwsTemp == "lcu-eastriver")
    {
        nRet = TYPE_LCU415ER;
    }
    else if(rwsTemp == "lcu-t3026")
    {
        nRet = TYPE_LCUT3026;
    }
    else if(rwsTemp == "tcu-5000")
    {
        nRet = TYPE_TCU5000;
    }
    else if(rwsTemp == "tcu-5500")
    {
        nRet = TYPE_TCU5500;
    }
    else if(rwsTemp == "transdata mark-v")
    {
        nRet = TYPE_TDMARKV;
    }
    else if(rwsTemp == "davis weather")
    {
        nRet = TYPE_DAVIS;
    }
    else if(rwsTemp == "alpha power plus")
    {
        nRet = TYPE_ALPHA_PPLUS;
    }
    else if(rwsTemp == "alpha a1")
    {
        nRet = TYPE_ALPHA_A1;
    }
    else if(rwsTemp == "fulcrum")
    {
        nRet = TYPE_FULCRUM;
    }
    else if(rwsTemp == "quantum")
    {
        nRet = TYPE_QUANTUM;
    }
    else if(rwsTemp == "vectron")
    {
        nRet = TYPE_VECTRON;
    }
    else if(rwsTemp == "landis-gyr s4")
    {
        nRet = TYPE_LGS4;
    }
    else if(rwsTemp == "dr-87")
    {
        nRet = TYPE_DR87;
    }
    else if(rwsTemp == "kv2" || rwsTemp == "kv")
    {
        nRet = TYPE_KV2;
    }
    else if(rwsTemp == "alpha a3")
    {
        nRet = TYPE_ALPHA_A3;
    }
    else if(rwsTemp == "sentinel")
    {
        nRet = TYPE_SENTINEL;
    }
    else if(rwsTemp == "focus")
    {
        nRet = TYPE_FOCUS;
    }
    else if(rwsTemp == "sixnet")
    {
        nRet = TYPE_SIXNET;
    }
    else if(rwsTemp == "emetcon group")
    {
        nRet = TYPE_LMGROUP_EMETCON;
    }
    else if(rwsTemp == "point group")
    {
        nRet = TYPE_LMGROUP_POINT;
    }
    else if(rwsTemp == "ripple group")
    {
        nRet = TYPE_LMGROUP_RIPPLE;
    }
    else if(rwsTemp == "versacom group")
    {
        nRet = TYPE_LMGROUP_VERSACOM;
    }
    else if(rwsTemp == "expresscom group")
    {
        nRet = TYPE_LMGROUP_EXPRESSCOM;
    }
    else if(rwsTemp == "integration group")
    {
        nRet = TYPE_LMGROUP_XML;
    }
    else if(rwsTemp == "energypro group")
    {
        nRet = TYPE_LMGROUP_ENERGYPRO;
    }
    else if(rwsTemp == "mct group")
    {
        nRet = TYPE_LMGROUP_MCT;
    }
    else if(rwsTemp == "macro group")
    {
        nRet = TYPE_MACRO;
    }
    else if(rwsTemp == "cbc 6510")
    {
        nRet = TYPECBC6510;
    }
    else if(rwsTemp == "cbc 7010" || rwsTemp == "cbc 7011" || rwsTemp == "cbc 7012")
    {
        nRet = TYPECBC7010;
    }
    else if(rwsTemp == "cbc 7020" || rwsTemp == "cbc 7022" || rwsTemp == "cbc 7023" || rwsTemp == "cbc 7024" || rwsTemp == "cbc 7030")
    {
        nRet = TYPECBC7020;
    }
    else if (rwsTemp == "cbc dnp")
    {
        nRet = TYPECBCDNP;
    }
    else if(rwsTemp == "cbc versacom")
    {
        nRet = TYPEVERSACOMCBC;
    }
    else if(rwsTemp == "cbc expresscom")
    {
        nRet = TYPEEXPRESSCOMCBC;
    }
    else if(rwsTemp == "cbc fp-2800")
    {
        nRet = TYPEFISHERPCBC;
    }
    else if(rwsTemp == "tap terminal")
    {
        nRet = TYPE_TAPTERM;
    }
    else if(rwsTemp == "snpp terminal")
    {
        nRet = TYPE_SNPP;
    }
    else if(rwsTemp == "rds terminal")
    {
        nRet = TYPE_RDS;
    }
    else if(rwsTemp == "tnpp terminal")
    {
        nRet = TYPE_TNPP;
    }
    else if(rwsTemp == "page receiver")
    {
      nRet = TYPE_PAGING_RECEIVER;
    }
    else if(rwsTemp == "wctp terminal")
    {
        nRet = TYPE_WCTP;
    }
    else if(rwsTemp == "lm direct program")
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
    else if(rwsTemp == "system")
    {
        nRet = TYPE_SYSTEM;
    }
    else if(rwsTemp == "script")
    {
        nRet = 0;
    }
    else if(rwsTemp == "simple")
    {
        nRet = 0;
    }
    else if(rwsTemp == "mct broadcast")
    {
        nRet = TYPEMCTBCAST;
    }
    else if(rwsTemp == "golay group")
    {
        nRet = TYPE_LMGROUP_GOLAY;
    }
    else if(rwsTemp == "sa-digital group")
    {
        nRet = TYPE_LMGROUP_SADIGITAL;
    }
    else if(rwsTemp == "sa-105 group")
    {
        nRet = TYPE_LMGROUP_SA105;
    }
    else if(rwsTemp == "sa-205 group")
    {
        nRet = TYPE_LMGROUP_SA205;
    }
    else if(rwsTemp == "sa-305 group")
    {
        nRet = TYPE_LMGROUP_SA305;
    }
    else if(rwsTemp == "rtc")
    {
        nRet = TYPE_RTC;
    }
    else if(rwsTemp == "rtm")
    {
        nRet = TYPE_RTM;
    }
    else if(rwsTemp == "energypro")
    {
       nRet = TYPE_ENERGYPRO;
    }
    else if(rwsTemp == "fmu")
    {
        nRet = TYPE_FMU;
    }
    else if(rwsTemp == "faulted circuit indicator")
    {
        nRet = TYPE_FCI;
    }
    else if(rwsTemp == "capacitor bank neutral monitor")
    {
        nRet = TYPE_NEUTRAL_MONITOR;
    }
    else if(rwsTemp == "virtual system")
    {
        nRet = TYPE_VIRTUAL_SYSTEM;
    }
    else if(rwsTemp == "integration")
    {
        nRet = TYPE_XML_XMIT;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Unsupported DEVICE type \"" << rwsTemp << "\" " << endl;
        }
    }

    return nRet;
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

    if(rwsTemp == "lm direct program")
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
        case TYPE_DR87:
        case TYPE_KV2:
        case TYPE_ALPHA_A3:
        case TYPE_SENTINEL:
        case TYPE_FOCUS:
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
        case TYPEMCT410:
        case TYPEMCT420CL:
        case TYPEMCT420CLD:
        case TYPEMCT420FL:
        case TYPEMCT420FLD:
        case TYPEMCT430:
        case TYPEMCT470:
        case TYPELCR3102:
        case TYPELTC:
        case TYPE_MODBUS:
        case TYPELMT2:
        case TYPECBC6510:
        case TYPECBC7020:
        case TYPECBCDNP:
        case TYPE_RTC:
        case TYPE_RTM:
        case TYPE_FMU:
        case TYPE_PAGING_RECEIVER:
        case TYPE_FCI:
        case TYPE_NEUTRAL_MONITOR:
        case TYPE_XML_XMIT:
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
        case TYPE_LMGROUP_XML:
        case TYPE_LMGROUP_ENERGYPRO:
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
    case TYPE_DR87:
    case TYPE_TAPTERM:
    case TYPE_WCTP:
    case TYPE_SNPP:
    case TYPE_TNPP:
    case TYPE_PAGING_RECEIVER:
    case TYPE_KV2:
    case TYPE_ALPHA_A3:
    case TYPE_SENTINEL:
    case TYPE_FOCUS:
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

#if 0
    string str = _str;
    CtiToLower(str);
    in_place_trim(str);

    if(str == "volts")
    {
        Ret = CalcTypeVoltsFromV2H;
    }
#endif

    return Ret;
}
