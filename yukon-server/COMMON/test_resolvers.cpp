#include <boost/test/unit_test.hpp>

#include "dsm2.h"
#include "resolvers.h"
#include "devicetypes.h"
#include "pointtypes.h"
#include "logger.h"
#include "numstr.h"
#include "utility.h"
#include "boost_test_helpers.h"


BOOST_AUTO_TEST_SUITE( test_resolvers )

BOOST_AUTO_TEST_CASE(test_resolvePortType)
{
    BOOST_CHECK_EQUAL(resolvePortType("heffalump"),                 PortTypeInvalid);

    BOOST_CHECK_EQUAL(resolvePortType("tcp"),                       PortTypeTcp);
    BOOST_CHECK_EQUAL(resolvePortType("udp"),                       PortTypeUdp);
    BOOST_CHECK_EQUAL(resolvePortType("rfn-1200"),                  PortTypeRfDa);
    BOOST_CHECK_EQUAL(resolvePortType("dialout pool"),              PortTypePoolDialout);
    BOOST_CHECK_EQUAL(resolvePortType("local dialup"),              PortTypeLocalDialup);
    BOOST_CHECK_EQUAL(resolvePortType("local dialback"),            PortTypeLocalDialBack);
    BOOST_CHECK_EQUAL(resolvePortType("terminal server"),           PortTypeTServerDirect);
    BOOST_CHECK_EQUAL(resolvePortType("local serial port"),         PortTypeLocalDirect);
    BOOST_CHECK_EQUAL(resolvePortType("terminal server dialup"),    PortTypeTServerDialup);
    BOOST_CHECK_EQUAL(resolvePortType("terminal server dialback"),  PortTypeTServerDialBack);
}

BOOST_AUTO_TEST_CASE(test_resolveRouteType)
{
    BOOST_CHECK_EQUAL(resolveRouteType("fakeDevice"),           RouteTypeInvalid);

    BOOST_CHECK_EQUAL(resolveRouteType("ccu"),                  RouteTypeCCU);
    BOOST_CHECK_EQUAL(resolveRouteType("tcu"),                  RouteTypeTCU);
    BOOST_CHECK_EQUAL(resolveRouteType("macrO"),                RouteTypeMacro);
    BOOST_CHECK_EQUAL(resolveRouteType("lcu"),                  RouteTypeLCU);
    BOOST_CHECK_EQUAL(resolveRouteType("versacom"),             RouteTypeVersacom);
    BOOST_CHECK_EQUAL(resolveRouteType("expresscom"),           RouteTypeExpresscom);
    BOOST_CHECK_EQUAL(resolveRouteType("tap paging"),           RouteTypeTap);
    BOOST_CHECK_EQUAL(resolveRouteType("tappaging"),            RouteTypeTap);
    BOOST_CHECK_EQUAL(resolveRouteType("rds terminal route"),   RouteTypeRDS);
    BOOST_CHECK_EQUAL(resolveRouteType("snpp terminal route"),  RouteTypeSNPP);
    BOOST_CHECK_EQUAL(resolveRouteType("wctp terminal route"),  RouteTypeWCTP);
    BOOST_CHECK_EQUAL(resolveRouteType("tnpp terminal route"),  RouteTypeTNPP);
    BOOST_CHECK_EQUAL(resolveRouteType("rtc route"),            RouteTypeRTC);
    BOOST_CHECK_EQUAL(resolveRouteType("series 5 lmi"),         RouteTypeSeriesVLMI);
}

BOOST_AUTO_TEST_CASE(test_resolveControlType)
{
    BOOST_CHECK_EQUAL(resolveControlType("platypus"),  ControlType_Invalid);

    BOOST_CHECK_EQUAL(resolveControlType("normal"),    ControlType_Normal);
    BOOST_CHECK_EQUAL(resolveControlType("latch"),     ControlType_Latch);
    BOOST_CHECK_EQUAL(resolveControlType("pseudo"),    ControlType_Pseudo);
    BOOST_CHECK_EQUAL(resolveControlType("sbo pulse"), ControlType_SBOPulse);
    BOOST_CHECK_EQUAL(resolveControlType("sbo latch"), ControlType_SBOLatch);
}

BOOST_AUTO_TEST_CASE(test_resolveAmpUseType)
{
    BOOST_CHECK_EQUAL(resolveAmpUseType("noname"),              RouteAmpDefault2Fail1);

    BOOST_CHECK_EQUAL(resolveAmpUseType("alternating"),         RouteAmpAlternating);
    BOOST_CHECK_EQUAL(resolveAmpUseType("alt w/failover"),      RouteAmpAltFail);
    BOOST_CHECK_EQUAL(resolveAmpUseType("default 1 fail 2"),    RouteAmpDefault1Fail2);
    BOOST_CHECK_EQUAL(resolveAmpUseType("default 2 fail 1"),    RouteAmpDefault2Fail1);
    BOOST_CHECK_EQUAL(resolveAmpUseType("amp 1"),               RouteAmp1);
    BOOST_CHECK_EQUAL(resolveAmpUseType("amp 2"),               RouteAmp2);
}

BOOST_AUTO_TEST_CASE(test_resolvePaoClass)
{
    BOOST_CHECK_EQUAL(resolvePAOClass("port"),           0);
    BOOST_CHECK_EQUAL(resolvePAOClass("route"),          1);
    BOOST_CHECK_EQUAL(resolvePAOClass("transmitter"),    2);
    BOOST_CHECK_EQUAL(resolvePAOClass("rtu"),            3);
    BOOST_CHECK_EQUAL(resolvePAOClass("ied"),            4);
    BOOST_CHECK_EQUAL(resolvePAOClass("meter"),          5);
    BOOST_CHECK_EQUAL(resolvePAOClass("rfmesh"),         6);
    BOOST_CHECK_EQUAL(resolvePAOClass("gridadvisor"),    7);
    BOOST_CHECK_EQUAL(resolvePAOClass("carrier"),        8);
    BOOST_CHECK_EQUAL(resolvePAOClass("group"),          9);
    BOOST_CHECK_EQUAL(resolvePAOClass("capcontrol"),     10);
    BOOST_CHECK_EQUAL(resolvePAOClass("virtual"),        11);
    BOOST_CHECK_EQUAL(resolvePAOClass("loadmanagement"), 12);
    BOOST_CHECK_EQUAL(resolvePAOClass("system"),         13);
    BOOST_CHECK_EQUAL(resolvePAOClass("schedule"),       14);
    BOOST_CHECK_EQUAL(resolvePAOClass("rutabaga"),       15);
}

BOOST_AUTO_TEST_CASE(test_resolvePointType)
{
    BOOST_CHECK_EQUAL(resolvePointType("amp 2"),                InvalidPointType);

    BOOST_CHECK_EQUAL(resolvePointType("analog"),               AnalogPointType);
    BOOST_CHECK_EQUAL(resolvePointType("status"),               StatusPointType);
    BOOST_CHECK_EQUAL(resolvePointType("pulseaccumulator"),     PulseAccumulatorPointType);
    BOOST_CHECK_EQUAL(resolvePointType("pulse accumulator"),    PulseAccumulatorPointType);
    BOOST_CHECK_EQUAL(resolvePointType("accumulator"),          PulseAccumulatorPointType);
    BOOST_CHECK_EQUAL(resolvePointType("demandaccumulator"),    DemandAccumulatorPointType);
    BOOST_CHECK_EQUAL(resolvePointType("demand accumulator"),   DemandAccumulatorPointType);
    BOOST_CHECK_EQUAL(resolvePointType("calculated"),           CalculatedPointType);
    BOOST_CHECK_EQUAL(resolvePointType("calcanalog"),           CalculatedPointType);
    BOOST_CHECK_EQUAL(resolvePointType("calcstatus"),           CalculatedStatusPointType);
    BOOST_CHECK_EQUAL(resolvePointType("system"),               SystemPointType);
    BOOST_CHECK_EQUAL(resolvePointType("statusoutput"),         StatusOutputPointType);
    BOOST_CHECK_EQUAL(resolvePointType("analogoutput"),         AnalogOutputPointType);
}

BOOST_AUTO_TEST_CASE(test_resolveDeviceType)
{
    //  --- GridSmart ---
    BOOST_CHECK_EQUAL(resolveDeviceType("capacitor bank neutral monitor"),  TYPE_NEUTRAL_MONITOR);
    BOOST_CHECK_EQUAL(resolveDeviceType("faulted circuit indicator"),       TYPE_FCI);

    //  --- Capacitor Control ---
    BOOST_CHECK_EQUAL(resolveDeviceType("cap bank"),        TYPECAPBANK);
    BOOST_CHECK_EQUAL(resolveDeviceType("cbc 6510"),        TYPECBC6510);
    BOOST_CHECK_EQUAL(resolveDeviceType("cbc 7010"),        TYPECBC7010);
    BOOST_CHECK_EQUAL(resolveDeviceType("cbc 7011"),        TYPECBC7010);
    BOOST_CHECK_EQUAL(resolveDeviceType("cbc 7012"),        TYPECBC7010);
    BOOST_CHECK_EQUAL(resolveDeviceType("cbc 7020"),        TYPECBC7020);
    BOOST_CHECK_EQUAL(resolveDeviceType("cbc 7022"),        TYPECBC7020);
    BOOST_CHECK_EQUAL(resolveDeviceType("cbc 7023"),        TYPECBC7020);
    BOOST_CHECK_EQUAL(resolveDeviceType("cbc 7024"),        TYPECBC7020);
    BOOST_CHECK_EQUAL(resolveDeviceType("cbc 7030"),        TYPECBC7020);
    BOOST_CHECK_EQUAL(resolveDeviceType("cbc 8020"),        TYPECBC8020);
    BOOST_CHECK_EQUAL(resolveDeviceType("cbc 8024"),        TYPECBC8020);
    BOOST_CHECK_EQUAL(resolveDeviceType("cbc dnp"),         TYPECBCDNP);
    BOOST_CHECK_EQUAL(resolveDeviceType("cbc expresscom"),  TYPEEXPRESSCOMCBC);
    BOOST_CHECK_EQUAL(resolveDeviceType("cbc fp-2800"),     TYPEFISHERPCBC);
    BOOST_CHECK_EQUAL(resolveDeviceType("cbc versacom"),    TYPEVERSACOMCBC);

    //  --- Voltage Regulators ---
    BOOST_CHECK_EQUAL(resolveDeviceType("ltc"),             TYPE_LOAD_TAP_CHANGER);
    BOOST_CHECK_EQUAL(resolveDeviceType("go_regulator"),    TYPE_GANG_OPERATED_REGULATOR);
    BOOST_CHECK_EQUAL(resolveDeviceType("po_regulator"),    TYPE_PHASE_OPERATED_REGULATOR);

    //  --- Cooper PLC ---
    BOOST_CHECK_EQUAL(resolveDeviceType("ccu-700"),         TYPE_CCU700);
    BOOST_CHECK_EQUAL(resolveDeviceType("ccu-710a"),        TYPE_CCU710);
    BOOST_CHECK_EQUAL(resolveDeviceType("ccu-711"),         TYPE_CCU711);
    BOOST_CHECK_EQUAL(resolveDeviceType("ccu-721"),         TYPE_CCU721);
    BOOST_CHECK_EQUAL(resolveDeviceType("lcr-3102"),        TYPELCR3102);
    BOOST_CHECK_EQUAL(resolveDeviceType("lmt-2"),           TYPELMT2);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct broadcast"),   TYPEMCTBCAST);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-210"),         TYPEMCT210);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-212"),         TYPEMCT212);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-213"),         TYPEMCT213);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-224"),         TYPEMCT224);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-226"),         TYPEMCT226);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-240"),         TYPEMCT240);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-242"),         TYPEMCT242);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-248"),         TYPEMCT248);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-250"),         TYPEMCT250);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-310"),         TYPEMCT310);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-310ct"),       TYPEMCT310);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-310id"),       TYPEMCT310ID);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-310idl"),      TYPEMCT310IDL);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-310il"),       TYPEMCT310IL);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-318"),         TYPEMCT318);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-318l"),        TYPEMCT318L);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-360"),         TYPEMCT360);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-370"),         TYPEMCT370);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-410cl"),       TYPEMCT410CL);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-410fl"),       TYPEMCT410FL);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-410gl"),       TYPEMCT410GL);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-410il"),       TYPEMCT410IL);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-420cl"),       TYPEMCT420CL);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-420cd"),       TYPEMCT420CD);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-420fl"),       TYPEMCT420FL);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-420fd"),       TYPEMCT420FD);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-430a"),        TYPEMCT430A);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-430a3"),       TYPEMCT430A3);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-430s4"),       TYPEMCT430S4);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-430sl"),       TYPEMCT430SL);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-470"),         TYPEMCT470);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-440-2131b"),   TYPEMCT440_2131B);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-440-2132b"),   TYPEMCT440_2132B);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct-440-2133b"),   TYPEMCT440_2133B);
    BOOST_CHECK_EQUAL(resolveDeviceType("repeater 800"),    TYPE_REPEATER800);
    BOOST_CHECK_EQUAL(resolveDeviceType("repeater 801"),    TYPE_REPEATER800);
    BOOST_CHECK_EQUAL(resolveDeviceType("repeater 850"),    TYPE_REPEATER850);
    BOOST_CHECK_EQUAL(resolveDeviceType("repeater 902"),    TYPE_REPEATER900);
    BOOST_CHECK_EQUAL(resolveDeviceType("repeater 921"),    TYPE_REPEATER900);
    BOOST_CHECK_EQUAL(resolveDeviceType("repeater"),        TYPE_REPEATER900);

    //  --- Receivers ---
    BOOST_CHECK_EQUAL(resolveDeviceType("page receiver"),   TYPE_PAGING_RECEIVER);

    //  --- RF mesh meters ---
    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-410fl"),   TYPE_RFN410FL);
    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-410fx"),   TYPE_RFN410FX);
    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-410fd"),   TYPE_RFN410FD);

    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-420fl"),   TYPE_RFN420FL);
    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-420fx"),   TYPE_RFN420FX);
    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-420fd"),   TYPE_RFN420FD);

    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-420frx"),  TYPE_RFN420FRX);
    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-420frd"),  TYPE_RFN420FRD);

    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-410cl"),   TYPE_RFN410CL);
    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-420cl"),   TYPE_RFN420CL);
    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-420cd"),   TYPE_RFN420CD);

    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-430a3d"),  TYPE_RFN430A3D);
    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-430a3t"),  TYPE_RFN430A3T);
    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-430a3k"),  TYPE_RFN430A3K);
    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-430a3r"),  TYPE_RFN430A3R);

    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-430kv"),   TYPE_RFN430KV);

    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-430sl0"),  TYPE_RFN430SL0);
    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-430sl1"),  TYPE_RFN430SL1);
    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-430sl2"),  TYPE_RFN430SL2);
    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-430sl3"),  TYPE_RFN430SL3);
    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-430sl4"),  TYPE_RFN430SL4);

    //  --- RF DA nodes ---
    BOOST_CHECK_EQUAL(resolveDeviceType("rfn-1200"),    TYPE_RFN1200);

    //  --- RTU devices ---
    BOOST_CHECK_EQUAL(resolveDeviceType("rtu-dart"),    TYPE_DARTRTU);
    BOOST_CHECK_EQUAL(resolveDeviceType("rtu-dnp"),     TYPE_DNPRTU);
    BOOST_CHECK_EQUAL(resolveDeviceType("rtu-ilex"),    TYPE_ILEXRTU);
    BOOST_CHECK_EQUAL(resolveDeviceType("rtu-lmi"),     TYPE_SERIESVLMIRTU);
    BOOST_CHECK_EQUAL(resolveDeviceType("rtu-modbus"),  TYPE_MODBUS);
    BOOST_CHECK_EQUAL(resolveDeviceType("rtu-ses92"),   TYPE_SES92RTU);
    BOOST_CHECK_EQUAL(resolveDeviceType("rtu-welco"),   TYPE_WELCORTU);

    //  --- GRE (Great River Energy) transmitters ---
    BOOST_CHECK_EQUAL(resolveDeviceType("rtc"), TYPE_RTC);
    BOOST_CHECK_EQUAL(resolveDeviceType("rtm"), TYPE_RTM);

    //  --- GRE (Great River Energy) Load Management groups ---
    BOOST_CHECK_EQUAL(resolveDeviceType("golay group"),         TYPE_LMGROUP_GOLAY);
    BOOST_CHECK_EQUAL(resolveDeviceType("sa-105 group"),        TYPE_LMGROUP_SA105);
    BOOST_CHECK_EQUAL(resolveDeviceType("sa-205 group"),        TYPE_LMGROUP_SA205);
    BOOST_CHECK_EQUAL(resolveDeviceType("sa-305 group"),        TYPE_LMGROUP_SA305);
    BOOST_CHECK_EQUAL(resolveDeviceType("sa-digital group"),    TYPE_LMGROUP_SADIGITAL);

    //  --- Load Management ---
    BOOST_CHECK_EQUAL(resolveDeviceType("ci customer"),             TYPE_CI_CUSTOMER);
    BOOST_CHECK_EQUAL(resolveDeviceType("lm control area"),         TYPE_LM_CONTROL_AREA);
    BOOST_CHECK_EQUAL(resolveDeviceType("lm curtail program"),      TYPE_LMPROGRAM_CURTAILMENT);
    BOOST_CHECK_EQUAL(resolveDeviceType("lm direct program"),       TYPE_LMPROGRAM_DIRECT);
    BOOST_CHECK_EQUAL(resolveDeviceType("lm energy exchange"),      TYPE_LMPROGRAM_ENERGYEXCHANGE);
    BOOST_CHECK_EQUAL(resolveDeviceType("lm sep program"),          TYPE_LMPROGRAM_DIRECT);
    BOOST_CHECK_EQUAL(resolveDeviceType("ecobee program"),          TYPE_LMPROGRAM_DIRECT);
    BOOST_CHECK_EQUAL(resolveDeviceType("digi sep group"),          TYPE_LMGROUP_DIGI_SEP);
    BOOST_CHECK_EQUAL(resolveDeviceType("ecobee group"),            TYPE_LMGROUP_ECOBEE);
    BOOST_CHECK_EQUAL(resolveDeviceType("emetcon group"),           TYPE_LMGROUP_EMETCON);
    BOOST_CHECK_EQUAL(resolveDeviceType("expresscom group"),        TYPE_LMGROUP_EXPRESSCOM);
    BOOST_CHECK_EQUAL(resolveDeviceType("rfn expresscom group"),    TYPE_LMGROUP_RFN_EXPRESSCOM);
    BOOST_CHECK_EQUAL(resolveDeviceType("mct group"),               TYPE_LMGROUP_MCT);
    BOOST_CHECK_EQUAL(resolveDeviceType("point group"),             TYPE_LMGROUP_POINT);
    BOOST_CHECK_EQUAL(resolveDeviceType("ripple group"),            TYPE_LMGROUP_RIPPLE);
    BOOST_CHECK_EQUAL(resolveDeviceType("versacom group"),          TYPE_LMGROUP_VERSACOM);

    //  --- System ---
    BOOST_CHECK_EQUAL(resolveDeviceType("macro group"),     TYPE_MACRO);
    BOOST_CHECK_EQUAL(resolveDeviceType("script"),          0);
    BOOST_CHECK_EQUAL(resolveDeviceType("simple"),          0);
    BOOST_CHECK_EQUAL(resolveDeviceType("system"),          TYPE_SYSTEM);
    BOOST_CHECK_EQUAL(resolveDeviceType("virtual system"),  TYPE_VIRTUAL_SYSTEM);

    //  --- Transmitters ---
    BOOST_CHECK_EQUAL(resolveDeviceType("lcu-415"),         TYPE_LCU415);
    BOOST_CHECK_EQUAL(resolveDeviceType("lcu-eastriver"),   TYPE_LCU415ER);
    BOOST_CHECK_EQUAL(resolveDeviceType("lcu-lg"),          TYPE_LCU415LG);
    BOOST_CHECK_EQUAL(resolveDeviceType("lcu-t3026"),       TYPE_LCUT3026);
    BOOST_CHECK_EQUAL(resolveDeviceType("rds terminal"),    TYPE_RDS);
    BOOST_CHECK_EQUAL(resolveDeviceType("snpp terminal"),   TYPE_SNPP);
    BOOST_CHECK_EQUAL(resolveDeviceType("tap terminal"),    TYPE_TAPTERM);
    BOOST_CHECK_EQUAL(resolveDeviceType("tcu-5000"),        TYPE_TCU5000);
    BOOST_CHECK_EQUAL(resolveDeviceType("tcu-5500"),        TYPE_TCU5500);
    BOOST_CHECK_EQUAL(resolveDeviceType("tnpp terminal"),   TYPE_TNPP);
    BOOST_CHECK_EQUAL(resolveDeviceType("wctp terminal"),   TYPE_WCTP);

    //  --- IEDs and electronic meters ---
    BOOST_CHECK_EQUAL(resolveDeviceType("alpha a1"),            TYPE_ALPHA_A1);
    BOOST_CHECK_EQUAL(resolveDeviceType("alpha a3"),            TYPE_ALPHA_A3);
    BOOST_CHECK_EQUAL(resolveDeviceType("alpha power plus"),    TYPE_ALPHA_PPLUS);
    BOOST_CHECK_EQUAL(resolveDeviceType("davis weather"),       TYPE_DAVIS);
    BOOST_CHECK_EQUAL(resolveDeviceType("dct-501"),             TYPEDCT501);
    BOOST_CHECK_EQUAL(resolveDeviceType("dr-87"),               TYPE_DR87);
    BOOST_CHECK_EQUAL(resolveDeviceType("focus"),               TYPE_FOCUS);
    BOOST_CHECK_EQUAL(resolveDeviceType("ipc-410fl"),           TYPE_IPC_410FL);
    BOOST_CHECK_EQUAL(resolveDeviceType("ipc-420fd"),           TYPE_IPC_420FD);
    BOOST_CHECK_EQUAL(resolveDeviceType("ipc-430s4e"),          TYPE_IPC_430S4E);
    BOOST_CHECK_EQUAL(resolveDeviceType("ipc-430sl"),           TYPE_IPC_430SL);
    BOOST_CHECK_EQUAL(resolveDeviceType("fulcrum"),             TYPE_FULCRUM);
    BOOST_CHECK_EQUAL(resolveDeviceType("ion-7330"),            TYPE_ION7330);
    BOOST_CHECK_EQUAL(resolveDeviceType("ion-7700"),            TYPE_ION7700);
    BOOST_CHECK_EQUAL(resolveDeviceType("ion-8300"),            TYPE_ION8300);
    BOOST_CHECK_EQUAL(resolveDeviceType("kv"),                  TYPE_KV2);
    BOOST_CHECK_EQUAL(resolveDeviceType("kv2"),                 TYPE_KV2);
    BOOST_CHECK_EQUAL(resolveDeviceType("landis-gyr s4"),       TYPE_LGS4);
    BOOST_CHECK_EQUAL(resolveDeviceType("quantum"),             TYPE_QUANTUM);
    BOOST_CHECK_EQUAL(resolveDeviceType("sentinel"),            TYPE_SENTINEL);
    BOOST_CHECK_EQUAL(resolveDeviceType("sixnet"),              TYPE_SIXNET);
    BOOST_CHECK_EQUAL(resolveDeviceType("transdata mark-v"),    TYPE_TDMARKV);
    BOOST_CHECK_EQUAL(resolveDeviceType("vectron"),             TYPE_VECTRON);
}

BOOST_AUTO_TEST_CASE(test_resolvePointArchiveType)
{
    BOOST_CHECK_EQUAL(resolvePointArchiveType("spare change"),  ArchiveTypeNone);

    BOOST_CHECK_EQUAL(resolvePointArchiveType("none"),          ArchiveTypeNone);
    BOOST_CHECK_EQUAL(resolvePointArchiveType("on change"),     ArchiveTypeOnChange);
    BOOST_CHECK_EQUAL(resolvePointArchiveType("on timer"),      ArchiveTypeOnTimer);
    BOOST_CHECK_EQUAL(resolvePointArchiveType("on update"),     ArchiveTypeOnUpdate);
    BOOST_CHECK_EQUAL(resolvePointArchiveType("time&update"),   ArchiveTypeOnTimerAndUpdated);
    BOOST_CHECK_EQUAL(resolvePointArchiveType("timer|update"),  ArchiveTypeOnTimerOrUpdated);
}

BOOST_AUTO_TEST_CASE(test_resolvePAOCategory)
{
    BOOST_CHECK_EQUAL(resolvePAOCategory("vaporware"),      -1);

    BOOST_CHECK_EQUAL(resolvePAOCategory("device"),         PAO_CATEGORY_DEVICE);
    BOOST_CHECK_EQUAL(resolvePAOCategory("port"),           PAO_CATEGORY_PORT);
    BOOST_CHECK_EQUAL(resolvePAOCategory("route"),          PAO_CATEGORY_ROUTE);
    BOOST_CHECK_EQUAL(resolvePAOCategory("loadmanagement"), PAO_CATEGORY_LOAD_MANAGEMENT);
    BOOST_CHECK_EQUAL(resolvePAOCategory("capcontrol"),     PAO_CATEGORY_CAP_CONTROL);
}

BOOST_AUTO_TEST_CASE(test_resolveCapControlType)
{
    BOOST_CHECK_EQUAL(resolveCapControlType("ccwhatever"),      0);

    BOOST_CHECK_EQUAL(resolveCapControlType("ccarea"),          TYPE_CC_AREA);
    BOOST_CHECK_EQUAL(resolveCapControlType("ccsubstation"),    TYPE_CC_SUBSTATION);
    BOOST_CHECK_EQUAL(resolveCapControlType("ccsubbus"),        TYPE_CC_SUBSTATION_BUS);
    BOOST_CHECK_EQUAL(resolveCapControlType("ccfeeder"),        TYPE_CC_FEEDER);
    BOOST_CHECK_EQUAL(resolveCapControlType("ccspecialarea"),   TYPE_CC_SPECIALAREA);
}

BOOST_AUTO_TEST_CASE(test_resolveLoadManagementType)
{
    BOOST_CHECK_EQUAL(resolveLoadManagementType("lmsomething"),         0);

    BOOST_CHECK_EQUAL(resolveLoadManagementType("lm direct program"),   TYPE_LMPROGRAM_DIRECT);
    BOOST_CHECK_EQUAL(resolveLoadManagementType("lm sep program"),      TYPE_LMPROGRAM_DIRECT);
    BOOST_CHECK_EQUAL(resolveLoadManagementType("ecobee program"),      TYPE_LMPROGRAM_DIRECT);
    BOOST_CHECK_EQUAL(resolveLoadManagementType("lm curtail program"),  TYPE_LMPROGRAM_CURTAILMENT);
    BOOST_CHECK_EQUAL(resolveLoadManagementType("lm control area"),     TYPE_LM_CONTROL_AREA);
    BOOST_CHECK_EQUAL(resolveLoadManagementType("ci customer"),         TYPE_CI_CUSTOMER);
    BOOST_CHECK_EQUAL(resolveLoadManagementType("lm energy exchange"),  TYPE_LMPROGRAM_ENERGYEXCHANGE);
}

BOOST_AUTO_TEST_CASE(test_resolveScanType)
{
    BOOST_CHECK_EQUAL(resolveScanType("HP ScanJet"),    ScanRateInvalid);

    BOOST_CHECK_EQUAL(resolveScanType("general"),       ScanRateGeneral);
    BOOST_CHECK_EQUAL(resolveScanType("accumulator"),   ScanRateAccum);
    BOOST_CHECK_EQUAL(resolveScanType("integrity"),     ScanRateIntegrity);
    BOOST_CHECK_EQUAL(resolveScanType("status"),        ScanRateGeneral);
    BOOST_CHECK_EQUAL(resolveScanType("exception"),     ScanRateGeneral);
    BOOST_CHECK_EQUAL(resolveScanType("loadprofile"),   ScanRateLoadProfile);
}

BOOST_AUTO_TEST_CASE(test_resolveDeviceWindowType)
{
    BOOST_CHECK_EQUAL(resolveDeviceWindowType("Window"),                            DeviceWindowInvalid);

    BOOST_CHECK_EQUAL(resolveDeviceWindowType(DEVICE_WINDOW_TYPE_SCAN),             DeviceWindowScan);
    BOOST_CHECK_EQUAL(resolveDeviceWindowType(DEVICE_WINDOW_TYPE_PEAK),             DeviceWindowPeak);
    BOOST_CHECK_EQUAL(resolveDeviceWindowType(DEVICE_WINDOW_TYPE_ALTERNATE_RATE),   DeviceWindowAlternateRate);
}

BOOST_AUTO_TEST_CASE(test_resolveProtocol)
{
    BOOST_CHECK_EQUAL(resolveProtocol("breach"),    0);

    BOOST_CHECK_EQUAL(resolveProtocol("none"),      ProtocolWrapNone);
    BOOST_CHECK_EQUAL(resolveProtocol("idlc"),      ProtocolWrapIDLC);
}

BOOST_AUTO_TEST_CASE(test_resolveDBChangeType)
{
    // enumeration runs from [0, 2]
    const std::vector<std::string>  expected
    {
        " CHANGED IN",      // -4
        " CHANGED IN",
        " CHANGED IN",
        " CHANGED IN",
        " ADDED TO",        //  0
        " DELETED FROM",
        " UPDATED IN",
        " CHANGED IN",
        " CHANGED IN",      //  4
        " CHANGED IN"
    };

    std::vector<std::string>  output;

    for ( int i = -4; i <= 5; ++i )
    {
        output.push_back( resolveDBChangeType(i) );
    }

    BOOST_CHECK_EQUAL_RANGES( output, expected );
}

BOOST_AUTO_TEST_CASE(test_resolveDBChanged)
{
    // enumeration runs from [-1, 35]
    const std::vector<std::string>  expected
    {
        " DATABASE",                            // -4
        " DATABASE",
        " DATABASE",
        " DATABASE",
        " PAO DB",                              //  0
        " POINT DB",
        " GROUP DB",
        " NOTIFICATIONGROUP/DESTINATION DB",
        " GROUPRECIPIENT DB",                   //  4
        " ALARM Category DB",
        " Customer Contact DB",
        " Graph DB",
        " Holiday Schedule DB",                 //  8
        " Energy Company DB",
        " Yukon User DB",
        " Yukon Customer DB",
        " Yukon Customer Account DB",           // 12
        " Yukon Image DB",
        " Yukon Baseline DB",
        " Yukon Config DB",
        " Yukon Tag DB",                        // 16
        " Yukon CI Customer DB",
        " Yukon LM Constraint DB",
        " DATABASE",
        " Yukon Season Schedule DB",            // 20
        " DATABASE",
        " DATABASE",
        " DATABASE",
        " DATABASE",                            // 24
        " DATABASE",
        " DATABASE",
        " DATABASE",
        " DATABASE",                            // 28
        " DATABASE",
        " DATABASE",
        " DATABASE",
        " DATABASE",                            // 32
        " DATABASE",
        " DATABASE",
        " DATABASE",
        " DATABASE",                            // 36
        " DATABASE",
        " DATABASE",
        " DATABASE",
        " DATABASE"                             // 40
    };

    std::vector<std::string>  output;

    for ( int i = -4; i <= 40; ++i )
    {
        output.push_back( resolveDBChanged(i) );
    }

    BOOST_CHECK_EQUAL_RANGES( output, expected );
}

BOOST_AUTO_TEST_CASE(test_resolvePAOType)
{
    BOOST_CHECK_EQUAL(resolvePAOType("breach", "tcp"),              0);

    BOOST_CHECK_EQUAL(resolvePAOType("device", "system"),           TYPE_SYSTEM);
    BOOST_CHECK_EQUAL(resolvePAOType("device", "beta power plus"),  0);

    BOOST_CHECK_EQUAL(resolvePAOType("port", "tcp"),                PortTypeTcp);
    BOOST_CHECK_EQUAL(resolvePAOType("port", "modem power plus"),   PortTypeInvalid);

    BOOST_CHECK_EQUAL(resolvePAOType("route", "ccu"),               RouteTypeCCU);
    BOOST_CHECK_EQUAL(resolvePAOType("route", "versaspresscom"),    0);

    BOOST_CHECK_EQUAL(resolvePAOType("loadmanagement", "lm control area"),  TYPE_LM_CONTROL_AREA);
    BOOST_CHECK_EQUAL(resolvePAOType("loadmanagement", "thermostat"),       0);

    BOOST_CHECK_EQUAL(resolvePAOType("capcontrol", "ccfeeder"),     TYPE_CC_FEEDER);
    BOOST_CHECK_EQUAL(resolvePAOType("capcontrol", "wire"),         0);
}

BOOST_AUTO_TEST_SUITE_END()

