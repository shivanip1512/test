
/*-----------------------------------------------------------------------------*
*
* File:   test_resolvers
*
* Date:   5/14/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/resolvers.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2007/10/12 21:14:17 $
*
* Copyright (c) 2007 Cannon Technologies. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw/db/db.h>
#include <rw/re.h>


#include "dsm2.h"
#include "resolvers.h"
#include "devicetypes.h"
#include "pointtypes.h"
#include "logger.h"
#include "numstr.h"
#include "utility.h"

#define BOOST_AUTO_TEST_MAIN "Test Resolvers"

#include <boost/test/unit_test.hpp>
#include <boost/test/auto_unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>

using boost::unit_test_framework::test_suite;
using namespace std;

BOOST_AUTO_UNIT_TEST(test_resolveRouteType)
{
    string tempName = "fakeDevice";
    CtiRoute_t route = RouteTypeInvalid;

    BOOST_CHECK_EQUAL(resolveRouteType(tempName), route);
    tempName = "ccu";
    route = RouteTypeCCU;
    BOOST_CHECK_EQUAL(resolveRouteType(tempName), route);
    tempName = "tcu";
    route = RouteTypeTCU;
    BOOST_CHECK_EQUAL(resolveRouteType(tempName), route);
    tempName = "macrO";
    route = RouteTypeMacro;
    BOOST_CHECK_EQUAL(resolveRouteType(tempName), route);
    tempName = "lcu";
    route = RouteTypeLCU;
    BOOST_CHECK_EQUAL(resolveRouteType(tempName), route);
    tempName = "repeater";
    route = RouteTypeRepeater;
    BOOST_CHECK_EQUAL(resolveRouteType(tempName), route);
    tempName = "versacom";
    route = RouteTypeVersacom;
    BOOST_CHECK_EQUAL(resolveRouteType(tempName), route);
    tempName = "tap paging";
    route = RouteTypeTap;
    BOOST_CHECK_EQUAL(resolveRouteType(tempName), route);
    tempName = "tappaging";
    route = RouteTypeTap;
    BOOST_CHECK_EQUAL(resolveRouteType(tempName), route);
    tempName = "snpp terminal";
    route = RouteTypeSNPP;
    BOOST_CHECK_EQUAL(resolveRouteType(tempName), route);
    tempName = "wctp terminal";
    route = RouteTypeWCTP;
    BOOST_CHECK_EQUAL(resolveRouteType(tempName), route);
    tempName = "rtc";
    route = RouteTypeRTC;
    BOOST_CHECK_EQUAL(resolveRouteType(tempName), route);
    tempName = "series 5 lmi";
    route = RouteTypeSeriesVLMI;
    BOOST_CHECK_EQUAL(resolveRouteType(tempName), route);
    tempName = "foreign porter";
    route = RouteTypeForeignPorter;
    BOOST_CHECK_EQUAL(resolveRouteType(tempName), route);
}

BOOST_AUTO_UNIT_TEST(test_resolveAmpUseType)
{
    string tempName = "noname";
    int amptype = RouteAmpDefault2Fail1;
    BOOST_CHECK_EQUAL(resolveAmpUseType(tempName), amptype);

    tempName = "alternating";
    amptype = RouteAmpAlternating;
    BOOST_CHECK_EQUAL(resolveAmpUseType(tempName), amptype);
    tempName = "alt w/failover";
    amptype = RouteAmpAltFail;
    BOOST_CHECK_EQUAL(resolveAmpUseType(tempName), amptype);
    tempName = "default 1 fail 2";
    amptype = RouteAmpDefault1Fail2;
    BOOST_CHECK_EQUAL(resolveAmpUseType(tempName), amptype);
    tempName = "default 2 fail 1";
    amptype = RouteAmpDefault2Fail1;
    BOOST_CHECK_EQUAL(resolveAmpUseType(tempName), amptype);
    tempName = "amp 1";
    amptype = RouteAmp1;
    BOOST_CHECK_EQUAL(resolveAmpUseType(tempName), amptype);
    tempName = "amp 2";
    amptype = RouteAmp2;
    BOOST_CHECK_EQUAL(resolveAmpUseType(tempName), amptype);
}

BOOST_AUTO_UNIT_TEST(test_resolvePointType)
{
    string tempName = "amp 2";
    int pointType = InvalidPointType;
    BOOST_CHECK_EQUAL(resolvePointType(tempName), pointType);
    tempName = "analog";
    pointType = AnalogPointType;
    BOOST_CHECK_EQUAL(resolvePointType(tempName), pointType);
    tempName = "status";
    pointType = StatusPointType;
    BOOST_CHECK_EQUAL(resolvePointType(tempName), pointType);
    tempName = "pulseaccumulator";
    pointType = PulseAccumulatorPointType;
    BOOST_CHECK_EQUAL(resolvePointType(tempName), pointType);
    tempName = "pulse accumulator";
    BOOST_CHECK_EQUAL(resolvePointType(tempName), pointType);
    tempName = "accumulator";
    BOOST_CHECK_EQUAL(resolvePointType(tempName), pointType);
    tempName = "demandaccumulator";
    pointType = DemandAccumulatorPointType;
    BOOST_CHECK_EQUAL(resolvePointType(tempName), pointType);
    tempName = "demandaccumulator";
    pointType = DemandAccumulatorPointType;
    BOOST_CHECK_EQUAL(resolvePointType(tempName), pointType);
    tempName = "demandaccumulator";
    pointType = DemandAccumulatorPointType;
    BOOST_CHECK_EQUAL(resolvePointType(tempName), pointType);
    tempName = "demandaccumulator";
    pointType = DemandAccumulatorPointType;
    BOOST_CHECK_EQUAL(resolvePointType(tempName), pointType);
    tempName = "demand accumulator";
    BOOST_CHECK_EQUAL(resolvePointType(tempName), pointType);
    tempName = "calculated";
    pointType = CalculatedPointType;
    BOOST_CHECK_EQUAL(resolvePointType(tempName), pointType);
    tempName = "calcanalog";
    BOOST_CHECK_EQUAL(resolvePointType(tempName), pointType);
    tempName = "calcstatus";
    pointType = CalculatedStatusPointType;
    BOOST_CHECK_EQUAL(resolvePointType(tempName), pointType);
    tempName = "system";
    pointType = SystemPointType;
    BOOST_CHECK_EQUAL(resolvePointType(tempName), pointType);
    tempName = "statusoutput";
    pointType = StatusOutputPointType;
    BOOST_CHECK_EQUAL(resolvePointType(tempName), pointType);
    tempName = "analogoutput";
    pointType = AnalogOutputPointType;
    BOOST_CHECK_EQUAL(resolvePointType(tempName), pointType);
}

BOOST_AUTO_UNIT_TEST(test_resolveDeviceType)
{
    string tempName;
    int devType = 0;

    tempName = "ccu-711";
    devType = TYPE_CCU711;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);

    tempName = "ccu-721";
    devType = TYPE_CCU721;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "ccu-710a";
    devType = TYPE_CCU710;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "ccu-700";
    devType = TYPE_CCU700;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "repeater";
    devType = TYPE_REPEATER900;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "repeater 921";
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "repeater 902";
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "repeater 800";
    devType = TYPE_REPEATER800;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "repeater 801";
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "rtu-ilex";
    devType = TYPE_ILEXRTU;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "rtu-welco";
    devType = TYPE_WELCORTU;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "rtu-ses92";
    devType = TYPE_SES92RTU;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "rtu-dnp";
    devType = TYPE_DNPRTU;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "rtu-dart";
    devType = TYPE_DARTRTU;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "rtu-lmi";
    devType = TYPE_SERIESVLMIRTU;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "rtu-modbus";
    devType = TYPE_MODBUS;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "ion-7330";
    devType = TYPE_ION7330;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "ion-7700";
    devType = TYPE_ION7700;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "ion-8300";
    devType = TYPE_ION8300;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "davis weather";
    devType = TYPE_DAVIS;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "lcu-415";
    devType = TYPE_LCU415;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "lcu-lg";
    devType = TYPE_LCU415LG;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "lcu-eastriver";
    devType = TYPE_LCU415ER;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "lcu-t3026";
    devType = TYPE_LCUT3026;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "tcu-5000";
    devType = TYPE_TCU5000;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "tcu-5500";
    devType = TYPE_TCU5500;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "transdata mark-v";
    devType = TYPE_TDMARKV;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "davis weather";
    devType = TYPE_DAVIS;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "alpha power plus";
    devType = TYPE_ALPHA_PPLUS;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "alpha a1";
    devType = TYPE_ALPHA_A1;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "fulcrum";
    devType = TYPE_FULCRUM;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "quantum";
    devType = TYPE_QUANTUM;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "vectron";
    devType = TYPE_VECTRON;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "landis-gyr s4";
    devType = TYPE_LGS4;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "dr-87";
    devType = TYPE_DR87;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "kv2";
    devType = TYPE_KV2;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "alpha a3";
    devType = TYPE_ALPHA_A3;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "sentinel";
    devType = TYPE_SENTINEL;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "dct-501";
    devType = TYPEDCT501;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-210";
    devType = TYPEMCT210;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-212";
    devType = TYPEMCT212;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-213";
    devType = TYPEMCT213;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-224";
    devType = TYPEMCT224;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-226";
    devType = TYPEMCT226;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-240";
    devType = TYPEMCT240;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-242";
    devType = TYPEMCT242;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-248";
    devType = TYPEMCT248;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-250";
    devType = TYPEMCT250;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-310";
    devType = TYPEMCT310;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-310ct";
    devType = TYPEMCT310;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-310idl";
    devType = TYPEMCT310IDL;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-310id";
    devType = TYPEMCT310ID;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-310il";
    devType = TYPEMCT310IL;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-318";
    devType = TYPEMCT318;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-318l";
    devType = TYPEMCT318L;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-360";
    devType = TYPEMCT360;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-370";
    devType = TYPEMCT370;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-410il";
    devType = TYPEMCT410;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-410cl";
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-470";
    devType = TYPEMCT470;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-430a";
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-430s";
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName) == devType, false);
    tempName = "mct-430sl";
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct-430s4";
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "lmt-2";
    devType = TYPELMT2;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "sixnet";
    devType = TYPE_SIXNET;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "emetcon group";
    devType = TYPE_LMGROUP_EMETCON;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "point group";
    devType = TYPE_LMGROUP_POINT;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "ripple group";
    devType = TYPE_LMGROUP_RIPPLE;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "versacom group";
    devType = TYPE_LMGROUP_VERSACOM;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "expresscom group";
    devType = TYPE_LMGROUP_EXPRESSCOM;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "energypro group";
    devType = TYPE_LMGROUP_ENERGYPRO;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct group";
    devType = TYPE_LMGROUP_MCT;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "macro group";
    devType = TYPE_MACRO;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "cbc 6510";
    devType = TYPECBC6510;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "cbc 7010";
    devType = TYPECBC7010;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "cbc 7011";
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "cbc 7012";
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "cbc 7020";
    devType = TYPECBC7020;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "cbc 7022";
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "cbc 7023";
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "cbc 7024";
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "cbc 7030";
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "cbc versacom";
    devType = TYPEVERSACOMCBC;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "cbc expresscom";
    devType = TYPEEXPRESSCOMCBC;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "cbc fp-2800";
    devType = TYPEFISHERPCBC;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "tap terminal";
    devType = TYPE_TAPTERM;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "snpp terminal";
    devType = TYPE_SNPP;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "tnpp terminal";
    devType = TYPE_TNPP;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "page receiver";
    devType = TYPE_PAGING_RECEIVER;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "wctp terminal";
    devType = TYPE_WCTP;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "lm direct program";
    devType = TYPE_LMPROGRAM_DIRECT;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "lm curtail program";
    devType = TYPE_LMPROGRAM_CURTAILMENT;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "lm control area";
    devType = TYPE_LM_CONTROL_AREA;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "ci customer";
    devType = TYPE_CI_CUSTOMER;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "lm energy exchange";
    devType = TYPE_LMPROGRAM_ENERGYEXCHANGE;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "system";
    devType = TYPE_SYSTEM;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "script";
    devType = 0;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "simple";
    devType = 0;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "mct broadcast";
    devType = TYPEMCTBCAST;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "golay group";
    devType = TYPE_LMGROUP_GOLAY;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "sa-digital group";
    devType = TYPE_LMGROUP_SADIGITAL;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "sa-105 group";
    devType = TYPE_LMGROUP_SA105;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "sa-205 group";
    devType = TYPE_LMGROUP_SA205;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "sa-305 group";
    devType = TYPE_LMGROUP_SA305;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "rtc";
    devType = TYPE_RTC;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "rtm";
    devType = TYPE_RTM;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "energypro";
    devType = TYPE_ENERGYPRO;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "foreign porter";
    devType = TYPE_FOREIGNPORTER;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "fmu";
    devType = TYPE_FMU;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);
    tempName = "virtual system";
    devType = TYPE_VIRTUAL_SYSTEM;
    BOOST_CHECK_EQUAL(resolveDeviceType(tempName), devType);

}
