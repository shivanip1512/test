/*-----------------------------------------------------------------------------*
*
* File:   desolvers
*
* Date:   8/16/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/desolvers.cpp-arc  $
* REVISION     :  $Revision: 1.49 $
* DATE         :  $Date: 2008/10/21 21:51:12 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include "desolvers.h"
#include "dsm2.h"
#include "resolvers.h"
#include "devicetypes.h"
#include "pointtypes.h"
#include "logger.h"
#include "utility.h"

string desolveScanType( LONG scanType )
{
/*
   ScanRateGeneral = 0,
   ScanRateAccum,
   ScanRateStatus,
   ScanRateIntegrity,
   ScanRateInvalid
*/

    string Ret;

    switch( scanType )
    {
        case ScanRateGeneral:   Ret = SCANRATE_GENERAL;     break;
        case ScanRateAccum:     Ret = SCANRATE_ACCUM;       break;
        case ScanRateIntegrity: Ret = SCANRATE_INTEGRITY;   break;
        case ScanRateStatus:    Ret = SCANRATE_STATUS;      break;
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Unsupported scan rate type " << endl;
            }

            Ret = SCANRATE_INVALID;

            break;
        }
    }

    return Ret;
}

string desolveDeviceWindowType( LONG aType )
{
    string Ret;

    switch( aType )
    {
        case DeviceWindowScan:          Ret = DEVICE_WINDOW_TYPE_SCAN;              break;
        case DeviceWindowPeak:          Ret = DEVICE_WINDOW_TYPE_PEAK;              break;
        case DeviceWindowAlternateRate: Ret = DEVICE_WINDOW_TYPE_ALTERNATE_RATE;    break;
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Unsupported device window type " << endl;
            }

            Ret = DEVICE_WINDOW_TYPE_INVALID;

            break;
        }
    }

    return Ret;
}


string desolveStatisticsType( INT statType )
{
    string Ret;

    switch( statType )
    {
        case StatTypeMonthly:   Ret = STATTYPE_MONTHLY;     break;
        case StatTypeHourly:    Ret = STATTYPE_HOURLY;      break;
        case StatType24Hour:    Ret = STATTYPE_DAILY;       break;
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Unsupported statistics collection type " << endl;
            }

            Ret = STATTYPE_INVALID;

            break;
        }
    }

    return Ret;
}

string desolveAmpUseType( INT useType )
{
    string autype;

    switch( useType )
    {
        case RouteAmpAlternating:    autype = AMPUSE_ATLTERNATE;     break;
        case RouteAmpAltFail:        autype = AMPUSE_WITHFAIL;       break;
        case RouteAmpDefault1Fail2:  autype = AMPUSE_DEFAULTONE;     break;
        case RouteAmpDefault2Fail1:  autype = AMPUSE_DEFAULTTWO;     break;
        case RouteAmp1:              autype = AMPUSE_AMPONE;         break;
        case RouteAmp2:              autype = AMPUSE_AMPTWO;         break;
        default:                     autype = AMPUSE_DEFAULTTWO;     break;
    }

    return autype;
}


string desolveDeviceType( INT aType )
{
    string Ret;

    switch( aType )
    {
        case TYPE_CCU721:       Ret = "ccu-721";        break;
        case TYPE_CCU711:       Ret = "ccu-711";        break;
        case TYPE_CCU710:       Ret = "ccu-710a";       break;
        case TYPE_CCU700:       Ret = "ccu-700";        break;

        case TYPE_REPEATER900:  Ret = "repeater";       break;
        case TYPE_REPEATER800:  Ret = "repeater 800";   break;
        case TYPE_REPEATER850:  Ret = "repeater 850";   break;

        case TYPE_ILEXRTU:      Ret = "rtu-ilex";       break;
        case TYPE_WELCORTU:     Ret = "rtu-welco";      break;
        case TYPE_SES92RTU:     Ret = "rtu-ses92";      break;
        case TYPE_DNPRTU:       Ret = "rtu-dnp";        break;
        case TYPE_DARTRTU:      Ret = "rtu-dart";       break;
        case TYPE_MODBUS:       Ret = "rtu-modbus";     break;

        case TYPE_ION7330:      Ret = "ion-7330";       break;
        case TYPE_ION7700:      Ret = "ion-7700";       break;
        case TYPE_ION8300:      Ret = "ion-8300";       break;

        case TYPE_LCU415:       Ret = "lcu-415";        break;
        case TYPE_LCU415LG:     Ret = "lcu-lg";         break;
        case TYPE_LCU415ER:     Ret = "lcu-eastriver";  break;
        case TYPE_LCUT3026:     Ret = "lcu-t026";       break;
        case TYPE_TCU5000:      Ret = "tcu-5000";       break;
        case TYPE_TCU5500:      Ret = "tcu-5500";       break;

        case TYPE_XML_XMIT:     Ret = "integration"; break;

        case TYPE_TDMARKV:      Ret = "transdata mark-v";   break;

        case TYPE_DAVIS:        Ret = "davisweather";       break;

        case TYPE_ALPHA_PPLUS:  Ret = "alpha power plus";   break;
        case TYPE_ALPHA_A1:     Ret = "alpha a1";       break;
        case TYPE_FULCRUM:      Ret = "fulcrum";        break;
        case TYPE_QUANTUM:      Ret = "quantum";        break;
        case TYPE_VECTRON:      Ret = "vectron";        break;
        case TYPE_LGS4:         Ret = "landis-gyr s4";  break;
        case TYPE_DR87:         Ret = "dr-87";          break;
        case TYPE_KV2:          Ret = "kv2";            break;
        case TYPE_ALPHA_A3:     Ret = "alpha a3";       break;
        case TYPE_SENTINEL:     Ret = "sentinel";       break;
        case TYPE_FOCUS:        Ret = "focus";          break;

        case TYPEDCT501:        Ret = "dct-501";        break;
        case TYPEMCT210:        Ret = "mct-210";        break;
        case TYPEMCT212:        Ret = "mct-212";        break;
        case TYPEMCT213:        Ret = "mct-213";        break;
        case TYPEMCT224:        Ret = "mct-224";        break;
        case TYPEMCT226:        Ret = "mct-226";        break;
        case TYPEMCT240:        Ret = "mct-240";        break;
        case TYPEMCT242:        Ret = "mct-242";        break;
        case TYPEMCT248:        Ret = "mct-248";        break;
        case TYPEMCT250:        Ret = "mct-250";        break;
        case TYPEMCT310:        Ret = "mct-310";        break;
        case TYPEMCT310ID:      Ret = "mct-310id";      break;
        case TYPEMCT310IL:      Ret = "mct-310il";      break;
        case TYPEMCT310IDL:     Ret = "mct-310idl";     break;
        case TYPEMCT318:        Ret = "mct-318";        break;
        case TYPEMCT360:        Ret = "mct-360";        break;
        case TYPEMCT370:        Ret = "mct-370";        break;
            //  encapsulates mct-410il and mct-410cl...  both resolve to TYPEMCT410
        case TYPEMCT410:        Ret = "mct-410";        break;
        case TYPEMCT420CL:      Ret = "mct-420cl";      break;
        case TYPEMCT420CLD:     Ret = "mct-420cld";     break;
        case TYPEMCT420FL:      Ret = "mct-420fl";      break;
        case TYPEMCT420FLD:     Ret = "mct-420fld";     break;
        case TYPEMCT430:        Ret = "mct-430";        break;
        case TYPEMCT470:        Ret = "mct-470";        break;
        case TYPELMT2:          Ret = "lmt-2";          break;

        case TYPERFN430A3:      Ret = "rfn-430a3"; break;
        case TYPERFN430KV:      Ret = "rfn-430kv"; break;
        case TYPERFN410FL:      Ret = "rfn-410fl"; break;
        case TYPERFN410FX:      Ret = "rfn-410fx"; break;
        case TYPERFN410FD:      Ret = "rfn-410fd"; break;

        case TYPELCR3102:       Ret = "lcr-3102";       break;

        case TYPELTC:           Ret = "load tap changer";   break;

        case TYPE_SIXNET:       Ret = "sixnet";         break;

        case TYPE_RTC:          Ret = "rtc";            break;
        case TYPE_RTM:          Ret = "rtm";            break;
        case TYPE_FMU:          Ret = "fmu";            break;

        case TYPE_SYSTEM:       Ret = "system";         break;

        case TYPE_FCI:              Ret = "faulted circuit indicator";          break;
        case TYPE_NEUTRAL_MONITOR:  Ret = "capacitor bank neutral monitor";     break;

        case TYPE_LMGROUP_EMETCON:      Ret = "emetcon group";      break;
        case TYPE_LMGROUP_POINT:        Ret = "point group";        break;
        case TYPE_LMGROUP_RIPPLE:       Ret = "ripple group";       break;
        case TYPE_LMGROUP_VERSACOM:     Ret = "versacom group";     break;
        case TYPE_LMGROUP_EXPRESSCOM:   Ret = "expresscom group";   break;
        case TYPE_LMGROUP_XML:          Ret = "integration group";  break;
        case TYPE_LMGROUP_DIGI_SEP:     Ret = "digi sep group";     break;
        case TYPE_LMGROUP_MCT:          Ret = "mct group";          break;
        case TYPE_LMGROUP_GOLAY:        Ret = "golay group";        break;
        case TYPE_LMGROUP_SADIGITAL:    Ret = "sa-digital group";   break;
        case TYPE_LMGROUP_SA105:        Ret = "sa-105 group";       break;
        case TYPE_LMGROUP_SA205:        Ret = "sa-205 group";       break;
        case TYPE_LMGROUP_SA305:        Ret = "sa-305 group";       break;
        case TYPE_MACRO:                Ret = "macro group";        break;

        case TYPEVERSACOMCBC:           Ret = "cbc versacom";       break;
        case TYPEEXPRESSCOMCBC:         Ret = "cbc expresscom";     break;
        case TYPEFISHERPCBC:            Ret = "cbc fp-2800";        break;

        case TYPECAPBANK:               Ret = "cap bank";           break;
        case TYPECBC6510:               Ret = "cbc 6510";           break;
        case TYPECBC7010:               Ret = "cbc 7010";           break;
        case TYPECBC7020:               Ret = "cbc 7020";           break;
        case TYPECBCDNP:                Ret = "cbc dnp";           break;

        case TYPE_TAPTERM:              Ret = "tap terminal";       break;
        case TYPE_SNPP:                 Ret = "snpp terminal";      break;
        case TYPE_RDS:                  Ret = "rds terminal";       break;
        case TYPE_TNPP:                 Ret = "tnpp terminal";      break;
        case TYPE_WCTP:                 Ret = "wctp terminal";      break;

        case TYPE_PAGING_RECEIVER:      Ret = "page receiver";      break;

        case TYPE_LMPROGRAM_DIRECT:         Ret = "lm direct program";  break;
        case TYPE_LMPROGRAM_CURTAILMENT:    Ret = "lm curtail program"; break;
        case TYPE_LMPROGRAM_ENERGYEXCHANGE: Ret = "lm energy exchange"; break;
        case TYPE_LM_CONTROL_AREA:          Ret = "lm control area";    break;
        case TYPE_CI_CUSTOMER:              Ret = "ci customer";        break;
        case TYPE_VIRTUAL_SYSTEM:           Ret = "virtual system";         break;

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Unknown DEVICE type \"" << aType << "\" " << endl;
            }

            break;
        }
    }

    CtiToUpper(Ret);

    return Ret;
}

string desolvePAOCategory( INT aCategory )
{
    string Ret;

    switch( aCategory )
    {
        case PAO_CATEGORY_DEVICE:           Ret = "device";         break;
        case PAO_CATEGORY_PORT:             Ret = "port";           break;
        case PAO_CATEGORY_ROUTE:            Ret = "route";          break;
        case PAO_CATEGORY_LOAD_MANAGEMENT:  Ret = "loadmanagement"; break;
        case PAO_CATEGORY_CAP_CONTROL:      Ret = "capcontrol";     break;
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Unsupported pao category in: " << __FILE__ << " at: " << __LINE__ << endl;
            }

            Ret = "invalid";

            break;
        }
    }

    return Ret;
}

string desolvePointType( INT aPointType )
{
    static const string analog = "Analog";
    static const string status = "Status";
    static const string pulseaccumulator  = "PulseAccumulator";
    static const string accumulator       = "Accumulator";
    static const string demandaccumulator  = "DemandAccumulator";
    static const string calculated   = "Calculated";
    static const string calcanalog   = "CalcAnalog";
    static const string calcstatus   = "CalcStatus";
    static const string system       = "System";
    static const string statusoutput = "StatusOutput";
    static const string analogoutput = "AnalogOutput";

    string Ret;

    switch( aPointType )
    {
        case StatusPointType:            Ret = status;            break;
        case AnalogPointType:            Ret = analog;            break;
        case PulseAccumulatorPointType:  Ret = pulseaccumulator;  break;
        case DemandAccumulatorPointType: Ret = demandaccumulator; break;
        case CalculatedPointType:        Ret = calculated;        break;
        case StatusOutputPointType:      Ret = statusoutput;      break;
        case AnalogOutputPointType:      Ret = analogoutput;      break;
        case SystemPointType:            Ret = system;            break;
        case CalculatedStatusPointType:  Ret = calcstatus;        break;
    }

    return Ret;
}

string desolvePortType( INT aType )
{
    string Ret;

    switch( aType )
    {
        case PortTypeLocalDirect:       Ret = "local serial port";      break;
        case PortTypeLocalDialup:       Ret = "local dialup";           break;
        case PortTypeLocalDialBack:     Ret = "local dialback";         break;
        case PortTypeTcp:               Ret = "tcp";                    break;
        case PortTypeUdp:               Ret = "udp";                    break;
        case PortTypeTServerDirect:     Ret = "terminal server";        break;
        case PortTypeTServerDialup:     Ret = "terminal server dialup"; break;
        case PortTypeTServerDialBack:   Ret = "terminal server dialback";   break;
        case PortTypePoolDialout:       Ret = "dialout pool";           break;
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Unsupported port type in: " << __FILE__ << " at: " << __LINE__ << endl;
            }

            Ret = "invalid";

            break;
        }
    }

    return Ret;
}


string desolveRouteType( INT aType )
{
    string Ret;

    switch( aType )
    {
        case RouteTypeCCU:          Ret = "ccu";    break;
        case RouteTypeTCU:          Ret = "tcu";    break;
        case RouteTypeMacro:        Ret = "macro";  break;
        case RouteTypeLCU:          Ret = "lcu";    break;
        case RouteTypeVersacom:     Ret = "versacom";   break;
        case RouteTypeExpresscom:   Ret = "expresscom"; break;
        case RouteTypeTap:          Ret = "tap paging"; break;
        case RouteTypeXML:          Ret = "integration route"; break;
        case RouteTypeWCTP:         Ret = "wctp terminal route";    break;
        case RouteTypeTNPP:         Ret = "tnpp terminal route";    break;
        //paging?
        case RouteTypeSNPP:         Ret = "snpp terminal route";  break;
        case RouteTypeRDS:          Ret = "rds terminal route"; break;
        case RouteTypeRTC:          Ret = "rtc route";    break;
        case RouteTypeSeriesVLMI:   Ret = "series 5 lmi";   break;
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Unsupported route type in: " << __FILE__ << " at: " << __LINE__ << endl;
            }

            Ret = "invalid";

            break;
        }
    }

    return Ret;
}


string desolveLoadManagementType( INT aType )
{
    string Ret;

    switch( aType )
    {
        case TYPE_LMPROGRAM_DIRECT:         Ret = "lm direct program";      break;
        case TYPE_LMPROGRAM_CURTAILMENT:    Ret = "lm curtail program";     break;
        case TYPE_LM_CONTROL_AREA:          Ret = "lm control area";        break;
        case TYPE_CI_CUSTOMER:              Ret = "ci customer";            break;
        case TYPE_LMPROGRAM_ENERGYEXCHANGE: Ret = "lm energy exchange";     break;
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Unsupported load management type in: " << __FILE__ << " at: " << __LINE__ << endl;
            }

            Ret = "invalid";

            break;
        }
    }

    return Ret;
}


string desolveCapControlType( INT aType )
{
    string Ret;

    switch( aType )
    {
        case TYPE_CC_SUBSTATION_BUS:    Ret = "ccsubbus";   break;
        case TYPE_CC_FEEDER:            Ret = "ccfeeder";   break;
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Unsupported cap control type in: " << __FILE__ << " at: " << __LINE__ << endl;
            }

            Ret = "invalid";

        break;
        }
    }

    return Ret;
}



