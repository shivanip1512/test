#define BOOST_AUTO_TEST_MAIN "Test Resolvers"

#include "desolvers.h"
#include "devicetypes.h"
#include "boostutil.h"

#include <boost/test/unit_test.hpp>


using namespace std;

BOOST_AUTO_TEST_CASE(test_desolveDeviceType)
{
    std::map<int, std::string> type_lookups;

    type_lookups[TYPE_CCU721]      = "CCU-721";
    type_lookups[TYPE_CCU711]      = "CCU-711";
    type_lookups[TYPE_CCU710]      = "CCU-710A";
    type_lookups[TYPE_CCU700]      = "CCU-700";

    type_lookups[TYPE_REPEATER900] = "REPEATER";
    type_lookups[TYPE_REPEATER800] = "REPEATER 800";
    type_lookups[TYPE_REPEATER850] = "REPEATER 850";

    type_lookups[TYPE_ILEXRTU]     = "RTU-ILEX";
    type_lookups[TYPE_WELCORTU]    = "RTU-WELCO";
    type_lookups[TYPE_SES92RTU]    = "RTU-SES92";
    type_lookups[TYPE_DNPRTU]      = "RTU-DNP";
    type_lookups[TYPE_DARTRTU]     = "RTU-DART";
    type_lookups[TYPE_MODBUS]      = "RTU-MODBUS";

    type_lookups[TYPE_ION7330]     = "ION-7330";
    type_lookups[TYPE_ION7700]     = "ION-7700";
    type_lookups[TYPE_ION8300]     = "ION-8300";

    type_lookups[TYPE_LCU415]      = "LCU-415";
    type_lookups[TYPE_LCU415LG]    = "LCU-LG";
    type_lookups[TYPE_LCU415ER]    = "LCU-EASTRIVER";
    type_lookups[TYPE_LCUT3026]    = "LCU-T026";
    type_lookups[TYPE_TCU5000]     = "TCU-5000";
    type_lookups[TYPE_TCU5500]     = "TCU-5500";

    type_lookups[TYPE_TDMARKV]     = "TRANSDATA MARK-V";

    type_lookups[TYPE_DAVIS]       = "DAVISWEATHER";

    type_lookups[TYPE_ALPHA_PPLUS] = "ALPHA POWER PLUS";
    type_lookups[TYPE_ALPHA_A1]    = "ALPHA A1";
    type_lookups[TYPE_FULCRUM]     = "FULCRUM";
    type_lookups[TYPE_QUANTUM]     = "QUANTUM";
    type_lookups[TYPE_VECTRON]     = "VECTRON";
    type_lookups[TYPE_LGS4]        = "LANDIS-GYR S4";
    type_lookups[TYPE_DR87]        = "DR-87";
    type_lookups[TYPE_KV2]         = "KV2";
    type_lookups[TYPE_ALPHA_A3]    = "ALPHA A3";
    type_lookups[TYPE_SENTINEL]    = "SENTINEL";
    type_lookups[TYPE_FOCUS]       = "FOCUS";

    type_lookups[TYPEDCT501]       = "DCT-501";
    type_lookups[TYPEMCT210]       = "MCT-210";
    type_lookups[TYPEMCT212]       = "MCT-212";
    type_lookups[TYPEMCT213]       = "MCT-213";
    type_lookups[TYPEMCT224]       = "MCT-224";
    type_lookups[TYPEMCT226]       = "MCT-226";
    type_lookups[TYPEMCT240]       = "MCT-240";
    type_lookups[TYPEMCT242]       = "MCT-242";
    type_lookups[TYPEMCT248]       = "MCT-248";
    type_lookups[TYPEMCT250]       = "MCT-250";
    type_lookups[TYPEMCT310]       = "MCT-310";
    type_lookups[TYPEMCT310ID]     = "MCT-310ID";
    type_lookups[TYPEMCT310IL]     = "MCT-310IL";
    type_lookups[TYPEMCT310IDL]    = "MCT-310IDL";
    type_lookups[TYPEMCT318]       = "MCT-318";
    type_lookups[TYPEMCT360]       = "MCT-360";
    type_lookups[TYPEMCT370]       = "MCT-370";

    type_lookups[TYPEMCT410]       = "MCT-410";

    type_lookups[TYPEMCT420CL]     = "MCT-420CL";
    type_lookups[TYPEMCT420CLD]    = "MCT-420CLD";
    type_lookups[TYPEMCT420FL]     = "MCT-420FL";
    type_lookups[TYPEMCT420FLD]    = "MCT-420FLD";

    type_lookups[TYPEMCT430]       = "MCT-430";
    type_lookups[TYPEMCT470]       = "MCT-470";

    type_lookups[TYPERFN430A3]     = "RFN-430A3";
    type_lookups[TYPERFN430KV]     = "RFN-430KV";
    type_lookups[TYPERFN410FL]     = "RFN-410FL";
    type_lookups[TYPERFN410FX]     = "RFN-410FX";
    type_lookups[TYPERFN410FD]     = "RFN-410FD";

    type_lookups[TYPELMT2]         = "LMT-2";

    type_lookups[TYPELCR3102]      = "LCR-3102";

    type_lookups[TYPELTC]          = "LOAD TAP CHANGER";

    type_lookups[TYPE_SIXNET]      = "SIXNET";

    type_lookups[TYPE_RTC]         = "RTC";
    type_lookups[TYPE_RTM]         = "RTM";
    type_lookups[TYPE_FMU]         = "FMU";

    type_lookups[TYPE_SYSTEM]      = "SYSTEM";

    type_lookups[TYPE_FCI]             = "FAULTED CIRCUIT INDICATOR";
    type_lookups[TYPE_NEUTRAL_MONITOR] = "CAPACITOR BANK NEUTRAL MONITOR";

    type_lookups[TYPE_LMGROUP_EMETCON]     = "EMETCON GROUP";
    type_lookups[TYPE_LMGROUP_POINT]       = "POINT GROUP";
    type_lookups[TYPE_LMGROUP_RIPPLE]      = "RIPPLE GROUP";
    type_lookups[TYPE_LMGROUP_VERSACOM]    = "VERSACOM GROUP";
    type_lookups[TYPE_LMGROUP_EXPRESSCOM]  = "EXPRESSCOM GROUP";
    type_lookups[TYPE_LMGROUP_DIGI_SEP]    = "DIGI SEP GROUP";
    type_lookups[TYPE_LMGROUP_MCT]         = "MCT GROUP";
    type_lookups[TYPE_LMGROUP_GOLAY]       = "GOLAY GROUP";
    type_lookups[TYPE_LMGROUP_SADIGITAL]   = "SA-DIGITAL GROUP";
    type_lookups[TYPE_LMGROUP_SA105]       = "SA-105 GROUP";
    type_lookups[TYPE_LMGROUP_SA205]       = "SA-205 GROUP";
    type_lookups[TYPE_LMGROUP_SA305]       = "SA-305 GROUP";
    type_lookups[TYPE_MACRO]               = "MACRO GROUP";

    type_lookups[TYPEVERSACOMCBC]          = "CBC VERSACOM";
    type_lookups[TYPEEXPRESSCOMCBC]        = "CBC EXPRESSCOM";
    type_lookups[TYPEFISHERPCBC]           = "CBC FP-2800";

    type_lookups[TYPECBC6510]              = "CBC 6510";
    type_lookups[TYPECBC7010]              = "CBC 7010";
    type_lookups[TYPECBC7020]              = "CBC 7020";
    type_lookups[TYPECBC8020]              = "CBC 8020";
    type_lookups[TYPECBCDNP]               = "CBC DNP";
    type_lookups[TYPECAPBANK]              = "CAP BANK";

    type_lookups[TYPE_TAPTERM]             = "TAP TERMINAL";
    type_lookups[TYPE_SNPP]                = "SNPP TERMINAL";
    type_lookups[TYPE_RDS]                 = "RDS TERMINAL";
    type_lookups[TYPE_TNPP]                = "TNPP TERMINAL";
    type_lookups[TYPE_WCTP]                = "WCTP TERMINAL";

    type_lookups[TYPE_PAGING_RECEIVER]     = "PAGE RECEIVER";

    type_lookups[TYPE_LMPROGRAM_DIRECT]         = "LM DIRECT PROGRAM";
    type_lookups[TYPE_LMPROGRAM_CURTAILMENT]    = "LM CURTAIL PROGRAM";
    type_lookups[TYPE_LMPROGRAM_ENERGYEXCHANGE] = "LM ENERGY EXCHANGE";
    type_lookups[TYPE_LM_CONTROL_AREA]          = "LM CONTROL AREA";
    type_lookups[TYPE_CI_CUSTOMER]              = "CI CUSTOMER";
    type_lookups[TYPE_VIRTUAL_SYSTEM]           = "VIRTUAL SYSTEM";

    //  Currently, device types run to 3000
    //    nonexistent indexes default to a blank string
    for( int i = 0; i < 10000; ++i )
    {
        BOOST_CHECK_INDEXED_EQUAL(i, type_lookups[i], desolveDeviceType(i));
    }
}
