#include <boost/test/unit_test.hpp>

#include "desolvers.h"
#include "devicetypes.h"
#include "boostutil.h"

#include <boost/assign/list_of.hpp>

using namespace std;

BOOST_AUTO_TEST_SUITE( test_desolvers )

BOOST_AUTO_TEST_CASE(test_desolveDeviceType)
{
    const std::string empty;

    const std::vector<std::string> expected = boost::assign::list_of
        (empty)
        ("LMT-2")
        ("DCT-501")
        ("REPEATER")
        ("REPEATER 800")
        ("REPEATER 850")
        (empty)
        ("MCT-210")
        ("MCT-212")
        ("MCT-213")
        ("MCT-224")
        ("MCT-226")
        ("MCT-240")
        ("MCT-242")
        ("MCT-248")
        ("MCT-250")
        ("MCT-310")
        ("MCT-310ID")
        ("MCT-318")
        ("MCT-310IL")
        (empty)
        ("MCT-310IDL")
        ("MCT-360")
        ("MCT-370")
        ("MCT-410CL")
        ("MCT-410FL")
        ("MCT-410GL")
        ("MCT-410IL")
        ("MCT-420CL")
        ("MCT-420CD")
        ("MCT-420FL")
        ("MCT-420FD")
        ("MCT-470")
        ("MCT-430A")
        ("MCT-430A3")
        ("MCT-430S4")
        ("MCT-430SL")
        ("MCT-440-2131B")
        ("MCT-440-2132B")
        ("MCT-440-2133B")
        ("LCR-3102")
        ("CAP BANK")
        ("CBC VERSACOM")
        ("CBC EXPRESSCOM")
        ("CBC FP-2800")
        ("CBC 7010")
        ("CBC 7020")
        ("CBC DNP")
        ("CBC LOGICAL")
        ("CBC 8020")
        ("RFN-410FL")
        ("RFN-410FX")
        ("RFN-410FD")
        ("RFN-420FL")
        ("RFN-420FX")
        ("RFN-420FD")
        ("RFN-420FRX")
        ("RFN-420FRD")
        ("RFN-510FL")
        ("RFN-520FAX")
        ("RFN-520FRX")
        ("RFN-520FAXD")
        ("RFN-520FRXD")
        ("RFN-530FAX")
        ("RFN-530FRX")
        ("RFN-410CL")
        ("RFN-420CL")
        ("RFN-420CD")
        ("RFN-430A3D")
        ("RFN-430A3T")
        ("RFN-430A3K")
        ("RFN-430A3R")
        ("RFN-430KV")
        ("RFN-430SL0")
        ("RFN-430SL1")
        ("RFN-430SL2")
        ("RFN-430SL3")
        ("RFN-430SL4")
        ("RFN-530S4X")
        ("RFN-530S4EAX")
        ("RFN-530S4EAXR")
        ("RFN-530S4ERX")
        ("RFN-530S4ERXR")
        ("RFN-1200")
        ("RFW-201")
        ("RFG-201")
        ("CCU-700")
        ("CCU-710A")
        ("CCU-711")
        ("CCU-721")
        ("RTU-ILEX")
        ("RTU-WELCO")
        ("RTU-SES92")
        ("RTU-DNP")
        ("RTU-DART")
        ("ION-7330")
        ("ION-7700")
        ("ION-8300")
        ("LCU-415")
        ("LCU-LG")
        ("LCU-EASTRIVER")
        ("LCU-T026")
        ("TCU-5000")
        ("TCU-5500")
        ("TRANSDATA MARK-V")
        ("DAVISWEATHER")
        ("ALPHA POWER PLUS")
        ("FULCRUM")
        ("LANDIS-GYR S4")
        ("VECTRON")
        ("ALPHA A1")
        ("DR-87")
        ("QUANTUM")
        ("KV2")
        ("SENTINEL")
        ("FOCUS")
        ("ALPHA A3")
        ("SIXNET")
        ("IPC-410FL")
        ("IPC-420FD")
        ("IPC-430S4E")
        ("IPC-430SL")
        ("TAP TERMINAL")
        ("WCTP TERMINAL")
        ("RDS TERMINAL")
        ("SNPP TERMINAL")
        ("PAGE RECEIVER")
        ("TNPP TERMINAL")
        ("RTC")
        ("RTM")
        (empty)
        (empty)
        ("RTU-MODBUS")
        ("FAULTED CIRCUIT INDICATOR")
        ("CAPACITOR BANK NEUTRAL MONITOR")
        ("EMETCON GROUP")
        ("VERSACOM GROUP")
        ("RIPPLE GROUP")
        ("POINT GROUP")
        ("EXPRESSCOM GROUP")
        ("RFN EXPRESSCOM GROUP")
        ("DIGI SEP GROUP")
        ("ECOBEE GROUP")
        ("HONEYWELL GROUP")
        ("NEST GROUP")
        ("ITRON GROUP")
        ("MCT GROUP")
        ("GOLAY GROUP")
        ("SA-DIGITAL GROUP")
        ("SA-105 GROUP")
        ("SA-205 GROUP")
        ("SA-305 GROUP")
        (empty)
        ("LM DIRECT PROGRAM")
        ("LM CURTAIL PROGRAM")
        ("LM CONTROL AREA")
        ("LM ENERGY EXCHANGE")
        ("CI CUSTOMER")
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        ("LTC")
        ("GO_REGULATOR")
        ("PO_REGULATOR")
        ("MACRO GROUP")
        ("SYSTEM")
        ("VIRTUAL SYSTEM")
            .repeat(9831, empty);

    std::vector<std::string> results;

    //  Currently, device types run to 3000
    //    nonexistent indexes default to a blank string
    for( int i = 0; i < 10000; ++i )
    {
        results.push_back(desolveDeviceType(i));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expected.begin(), expected.end(),
       results.begin(), results.end());
}

BOOST_AUTO_TEST_CASE(test_desolvePointQuality)
{
    std::array<PointQuality_t, 19> qualities = {
        UnintializedQuality,
        InitDefaultQuality,
        InitLastKnownQuality,
        NonUpdatedQuality,
        ManualQuality,
        NormalQuality,
        ExceedsLowQuality,
        ExceedsHighQuality,
        AbnormalQuality,
        UnknownQuality,
        InvalidQuality,
        PartialIntervalQuality,
        DeviceFillerQuality,
        QuestionableQuality,
        OverflowQuality,
        PowerfailQuality,
        UnreasonableQuality,
        ConstantQuality,
        EstimatedQuality
    };

    std::array<std::string, 19> expected = {
        "Unintialized",
        "InitDefault",
        "InitLastKnown",
        "NonUpdated",
        "Manual",
        "Normal",
        "ExceedsLow",
        "ExceedsHigh",
        "Abnormal",
        "Unknown",
        "Invalid",
        "PartialInterval",
        "DeviceFiller",
        "Questionable",
        "Overflow",
        "Powerfail",
        "Unreasonable",
        "Constant",
        "Estimated"
    };

    std::vector<std::string> results;

    for( auto quality : qualities )
    {
        results.push_back(desolvePointQuality(quality));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results.begin(), results.end());
}

BOOST_AUTO_TEST_SUITE_END()
