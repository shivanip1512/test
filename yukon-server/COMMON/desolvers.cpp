#include "precompiled.h"


#include "desolvers.h"
#include "dsm2.h"
#include "resolvers.h"
#include "devicetypes.h"
#include "pointtypes.h"
#include "logger.h"
#include "utility.h"

using std::endl;
using std::string;

string desolveScanType( long scanType )
{
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

string desolveDeviceWindowType( long aType )
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


string desolveDeviceType( int aType )
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
        case TYPE_IPC_410FL:    Ret = "ipc-410fl";      break;
        case TYPE_IPC_420FD:    Ret = "ipc-420fd";      break;
        case TYPE_IPC_430S4E:   Ret = "ipc-430s4e";     break;
        case TYPE_IPC_430SL:    Ret = "ipc-430sl";      break;

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
        case TYPEMCT410CL:      Ret = "mct-410cl";      break;
        case TYPEMCT410FL:      Ret = "mct-410fl";      break;
        case TYPEMCT410GL:      Ret = "mct-410gl";      break;
        case TYPEMCT410IL:      Ret = "mct-410il";      break;
        case TYPEMCT420CL:      Ret = "mct-420cl";      break;
        case TYPEMCT420CD:      Ret = "mct-420cd";      break;
        case TYPEMCT420FL:      Ret = "mct-420fl";      break;
        case TYPEMCT420FD:      Ret = "mct-420fd";      break;
        case TYPEMCT430A:       Ret = "mct-430a";       break;
        case TYPEMCT430A3:      Ret = "mct-430a3";      break;
        case TYPEMCT430S4:      Ret = "mct-430s4";      break;
        case TYPEMCT430SL:      Ret = "mct-430sl";      break;
        case TYPEMCT470:        Ret = "mct-470";        break;
        case TYPEMCT440_2131B:  Ret = "mct-440-2131b";  break;
        case TYPEMCT440_2132B:  Ret = "mct-440-2132b";  break;
        case TYPEMCT440_2133B:  Ret = "mct-440-2133b";  break;
        case TYPELMT2:          Ret = "lmt-2";          break;

        case TYPE_RFN410FL:     Ret = "rfn-410fl";      break;
        case TYPE_RFN410FX:     Ret = "rfn-410fx";      break;
        case TYPE_RFN410FD:     Ret = "rfn-410fd";      break;
        case TYPE_RFN420FL:     Ret = "rfn-420fl";      break;
        case TYPE_RFN420FX:     Ret = "rfn-420fx";      break;
        case TYPE_RFN420FD:     Ret = "rfn-420fd";      break;
        case TYPE_RFN420FRX:    Ret = "rfn-420frx";     break;
        case TYPE_RFN420FRD:    Ret = "rfn-420frd";     break;
        case TYPE_RFN410CL:     Ret = "rfn-410cl";      break;
        case TYPE_RFN420CL:     Ret = "rfn-420cl";      break;
        case TYPE_RFN420CD:     Ret = "rfn-420cd";      break;
        case TYPE_RFN430A3D:    Ret = "rfn-430a3d";     break;
        case TYPE_RFN430A3T:    Ret = "rfn-430a3t";     break;
        case TYPE_RFN430A3K:    Ret = "rfn-430a3k";     break;
        case TYPE_RFN430A3R:    Ret = "rfn-430a3r";     break;
        case TYPE_RFN430KV:     Ret = "rfn-430kv";      break;
        case TYPE_RFN430SL0:    Ret = "rfn-430sl0";     break;
        case TYPE_RFN430SL1:    Ret = "rfn-430sl1";     break;
        case TYPE_RFN430SL2:    Ret = "rfn-430sl2";     break;
        case TYPE_RFN430SL3:    Ret = "rfn-430sl3";     break;
        case TYPE_RFN430SL4:    Ret = "rfn-430sl4";     break;

        case TYPE_RF_DA:        Ret = "rf-da";          break;

        case TYPELCR3102:       Ret = "lcr-3102";       break;

        case TYPELTC:           Ret = "load tap changer";   break;

        case TYPE_SIXNET:       Ret = "sixnet";         break;

        case TYPE_RTC:          Ret = "rtc";            break;
        case TYPE_RTM:          Ret = "rtm";            break;
        case TYPE_FMU:          Ret = "fmu";            break;

        case TYPE_SYSTEM:       Ret = "system";         break;

        case TYPE_FCI:              Ret = "faulted circuit indicator";          break;
        case TYPE_NEUTRAL_MONITOR:  Ret = "capacitor bank neutral monitor";     break;

        case TYPE_LMGROUP_EMETCON:        Ret = "emetcon group";        break;
        case TYPE_LMGROUP_POINT:          Ret = "point group";          break;
        case TYPE_LMGROUP_RIPPLE:         Ret = "ripple group";         break;
        case TYPE_LMGROUP_VERSACOM:       Ret = "versacom group";       break;
        case TYPE_LMGROUP_EXPRESSCOM:     Ret = "expresscom group";     break;
        case TYPE_LMGROUP_RFN_EXPRESSCOM: Ret = "rfn expresscom group"; break;
        case TYPE_LMGROUP_DIGI_SEP:       Ret = "digi sep group";       break;
        case TYPE_LMGROUP_ECOBEE:         Ret = "ecobee group";         break;
        case TYPE_LMGROUP_MCT:            Ret = "mct group";            break;
        case TYPE_LMGROUP_GOLAY:          Ret = "golay group";          break;
        case TYPE_LMGROUP_SADIGITAL:      Ret = "sa-digital group";     break;
        case TYPE_LMGROUP_SA105:          Ret = "sa-105 group";         break;
        case TYPE_LMGROUP_SA205:          Ret = "sa-205 group";         break;
        case TYPE_LMGROUP_SA305:          Ret = "sa-305 group";         break;
        case TYPE_MACRO:                  Ret = "macro group";          break;

        case TYPEVERSACOMCBC:             Ret = "cbc versacom";         break;
        case TYPEEXPRESSCOMCBC:           Ret = "cbc expresscom";       break;
        case TYPEFISHERPCBC:              Ret = "cbc fp-2800";          break;

        case TYPECAPBANK:                 Ret = "cap bank";             break;
        case TYPECBC6510:                 Ret = "cbc 6510";             break;
        case TYPECBC7010:                 Ret = "cbc 7010";             break;
        case TYPECBC7020:                 Ret = "cbc 7020";             break;
        case TYPECBC8020:                 Ret = "cbc 8020";             break;
        case TYPECBCDNP:                  Ret = "cbc dnp";              break;

        case TYPE_TAPTERM:                Ret = "tap terminal";         break;
        case TYPE_SNPP:                   Ret = "snpp terminal";        break;
        case TYPE_RDS:                    Ret = "rds terminal";         break;
        case TYPE_TNPP:                   Ret = "tnpp terminal";        break;
        case TYPE_WCTP:                   Ret = "wctp terminal";        break;

        case TYPE_PAGING_RECEIVER:        Ret = "page receiver";        break;

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

string desolvePointType( int aPointType )
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

