#include <boost/test/unit_test.hpp>

#include "slctdev.h"
#include "devicetypes.h"
#include "dev_lcu.h"
#include "test_reader.h"
#include "boostutil.h"
#include "boost_test_helpers.h"

#include <boost/assign/list_of.hpp>


BOOST_AUTO_TEST_SUITE( test_slctdev )

BOOST_AUTO_TEST_CASE(test_is_carrier_lp_device_type)
{
    const bool X = true, _ = false;

    const std::vector<bool> expected = boost::assign::list_of
        (_)(X)(X)(_)(_) (_)(_)(_)(_)(_)  //   0
        (_)(_)(X)(X)(X) (X)(_)(_)(_)(X)  //  10
        (X)(_)(_)(_)(X) (X)(X)(X)(X)(X)  //  20
        (X)(X)(X)(X)(X) (X)(X)(_)(_)(_)  //  30
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  40
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  50
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  60
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  70
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  80
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  90
        .repeat(100, _);

    std::vector<bool> results;

    for ( int type = 0; type < 200; ++type )
    {
        results.push_back(isCarrierLPDeviceType(type));
    }

    BOOST_CHECK_EQUAL_RANGES( expected, results );
}


BOOST_AUTO_TEST_CASE(test_is_dnp_device_type)
{
    const bool X = true, _ = false;

    const std::vector<bool> expected = boost::assign::list_of
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //   0
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  10
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  20
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  30
        (_)(_)(_)(_)(_) (_)(X)(X)(_)(X)  //  40
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  50
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  60
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  70
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  80
        (_)(_)(_)(X)(_) (_)(_)(_)(_)(_)  //  90
        (_)(_)(_)(_)(_) (_)(_)(_)(_)(_)  //  100
        .repeat(90, _);

    std::vector<bool> results;

    for ( int type = 0; type < 200; ++type )
    {
        results.push_back(isDnpDeviceType(type));
    }

    BOOST_CHECK_EQUAL_RANGES( expected, results );
}


BOOST_AUTO_TEST_CASE(test_createDeviceType)
{
    std::vector<std::string> expected = boost::assign::list_of
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("class Cti::Devices::Lmt2Device")
        ("class Cti::Devices::Dct501Device")
        ("class Cti::Devices::Repeater900Device")
        ("class Cti::Devices::Repeater800Device")
        ("class Cti::Devices::Repeater850Device")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("class Cti::Devices::Mct210Device")
        ("class Cti::Devices::Mct22xDevice")
        ("class Cti::Devices::Mct210Device")
        //  10
        ("class Cti::Devices::Mct22xDevice")
        ("class Cti::Devices::Mct22xDevice")
        ("class Cti::Devices::Mct24xDevice")
        ("class Cti::Devices::Mct24xDevice")
        ("class Cti::Devices::Mct24xDevice")
        ("class Cti::Devices::Mct24xDevice")
        ("class Cti::Devices::Mct310Device")
        ("class Cti::Devices::Mct310Device")
        ("class Cti::Devices::Mct31xDevice")
        ("class Cti::Devices::Mct310Device")
        //  20
        ("class Cti::Devices::Mct31xDevice")
        ("class Cti::Devices::Mct310Device")
        ("class Cti::Devices::Mct31xDevice")
        ("class Cti::Devices::Mct31xDevice")
        ("class Cti::Devices::Mct410Device")
        ("class Cti::Devices::Mct410Device")
        ("class Cti::Devices::Mct410Device")
        ("class Cti::Devices::Mct410Device")
        ("class Cti::Devices::Mct420Device")
        ("class Cti::Devices::Mct420Device")
        //  30
        ("class Cti::Devices::Mct420Device")
        ("class Cti::Devices::Mct420Device")
        ("class Cti::Devices::Mct470Device")
        ("class Cti::Devices::Mct470Device")
        ("class Cti::Devices::Mct470Device")
        ("class Cti::Devices::Mct470Device")
        ("class Cti::Devices::Mct470Device")
        ("class Cti::Devices::Mct440_2131BDevice")
        ("class Cti::Devices::Mct440_2132BDevice")
        ("class Cti::Devices::Mct440_2133BDevice")
        //  40
        ("class Cti::Devices::Lcr3102Device")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("class CtiDeviceCBC")
        ("class CtiDeviceCBC")
        ("class CtiDeviceCBC")
        ("class CtiDeviceCBC")
        ("class Cti::Devices::Cbc7020Device")
        ("class Cti::Devices::CbcDnpDevice")
        ("class Cti::Devices::CbcLogicalDevice")
        ("class Cti::Devices::Cbc8020Device")
        //  50
        ("class Cti::Devices::RfnMeterDevice")
        ("class Cti::Devices::RfnResidentialDevice")
        ("class Cti::Devices::RfnResidentialDevice")
        ("class Cti::Devices::RfnLgyrFocusAlDevice")
        ("class Cti::Devices::RfnResidentialVoltageDevice")
        ("class Cti::Devices::RfnResidentialVoltageDevice")
        ("class Cti::Devices::RfnResidentialVoltageDevice")
        ("class Cti::Devices::RfnResidentialVoltageDevice")
        ("class Cti::Devices::RfnLgyrFocusAlDevice")
        ("class Cti::Devices::RfnResidentialVoltageDevice")
        //  60
        ("class Cti::Devices::RfnResidentialVoltageDevice")
        ("class Cti::Devices::RfnResidentialVoltageDevice")
        ("class Cti::Devices::RfnResidentialVoltageDevice")
        ("class Cti::Devices::RfnResidentialVoltageDevice")
        ("class Cti::Devices::RfnResidentialVoltageDevice")
        ("class Cti::Devices::Rfn410CentronDevice")
        ("class Cti::Devices::Rfn420CentronDevice")
        ("class Cti::Devices::Rfn420CentronDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        //  70
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        //  80
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfDaDevice")
        ("class Cti::Devices::RfBatteryNodeDevice")
        ("class Cti::Devices::RfBatteryNodeDevice")
        ("class CtiDeviceCCU710")
        ("class CtiDeviceCCU710")
        ("class CtiDeviceCCU")
        ("class Cti::Devices::Ccu721Device")
        //  90
        ("class CtiDeviceILEX")
        ("class CtiDeviceWelco")
        ("class CtiDeviceRemote")
        ("class Cti::Devices::DnpRtuDevice")
        ("class Cti::Devices::DnpRtuDevice")
        ("class CtiDeviceION")
        ("class CtiDeviceION")
        ("class CtiDeviceION")
        ("class CtiDeviceLCU")
        ("class CtiDeviceLCU")
        //  100
        ("class CtiDeviceLCU")
        ("class CtiDeviceLCU")
        ("class CtiDeviceTCU")
        ("class CtiDeviceTCU")
        ("class CtiDeviceMarkV")
        ("class CtiDeviceDavis")
        ("class CtiDeviceAlphaPPlus")
        ("class CtiDeviceFulcrum")
        ("class CtiDeviceLandisGyrS4")
        ("class CtiDeviceVectron")
        //  110
        ("class CtiDeviceAlphaA1")
        ("class CtiDeviceDR87")
        ("class CtiDeviceQuantum")
        ("class CtiDeviceKV2")
        ("class Cti::Devices::CtiDeviceSentinel")
        ("class Cti::Devices::CtiDeviceFocus")
        ("class CtiDeviceKV2")
        ("class CtiDeviceSixnet")
        ("class Cti::Devices::Ipc410ALDevice")
        ("class Cti::Devices::Ipc420ADDevice")
        //  120
        ("class CtiDeviceLandisGyrS4")
        ("class Cti::Devices::CtiDeviceSentinel")
        ("class Cti::Devices::TapPagingTerminal")
        ("class CtiDeviceWctpTerminal")
        ("class Cti::Devices::RDSTransmitter")
        ("class CtiDeviceSnppPagingTerminal")
        ("class CtiDevicePagingReceiver")
        ("class CtiDeviceTnppPagingTerminal")
        ("class CtiDeviceRTC")
        ("class CtiDeviceRTM")
        //  130
        ("class CtiDeviceSeriesV")
        ("class CtiDeviceLMI")
        ("class Cti::Devices::ModbusDevice")
        ("class CtiDeviceGridAdvisor")
        ("class CtiDeviceGridAdvisor")
        ("class CtiDeviceGroupEmetcon")
        ("class CtiDeviceGroupVersacom")
        ("class CtiDeviceGroupRipple")
        ("class CtiDeviceGroupPoint")
        ("class CtiDeviceGroupExpresscom")
        //  140
        ("class CtiDeviceGroupRfnExpresscom")
        .repeat(5, "null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("class CtiDeviceGroupMCT")
        ("class CtiDeviceGroupGolay")
        ("class CtiDeviceGroupSADigital")
        ("class CtiDeviceGroupSA105")
        //  150
        ("class CtiDeviceGroupSA205")
        ("class CtiDeviceGroupSA305")
        ("class Cti::Devices::MctBroadcastDevice")
        .repeat(7, "null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        //  160
        .repeat(6, "null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("class CtiDeviceMacro")
        ("class CtiDeviceSystem")
        .repeat(9832, "null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
            ;

    std::vector<std::string> results;

    for( int type = 0; type < 10000; ++type )
    {
        std::unique_ptr<CtiDeviceBase> dev(createDeviceType(type));

        results.push_back(
            dev.get()
                ? typeid(*dev).name()
                : "null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}");
    }

    BOOST_CHECK_EQUAL_RANGES( expected, results );
}


BOOST_AUTO_TEST_CASE(test_DeviceFactory)
{
    typedef Cti::Test::StringRow<1> DeviceTypeRow;
    typedef Cti::Test::TestReader<DeviceTypeRow> DeviceTypeReader;

    DeviceTypeRow columnNames    = {"type"};

    std::vector<std::pair<std::string, std::string>> dbTypesToTypenames
    {
        { "capacitor bank neutral monitor", "class CtiDeviceGridAdvisor" },
        { "faulted circuit indicator",      "class CtiDeviceGridAdvisor" },
        { "cap bank",                       "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "cbc 7010",                       "class CtiDeviceCBC" },
        { "cbc 7011",                       "class CtiDeviceCBC" },
        { "cbc 7012",                       "class CtiDeviceCBC" },
        { "cbc 7020",                       "class Cti::Devices::Cbc7020Device" },
        { "cbc 7022",                       "class Cti::Devices::Cbc7020Device" },
        { "cbc 7023",                       "class Cti::Devices::Cbc7020Device" },
        { "cbc 7024",                       "class Cti::Devices::Cbc7020Device" },
        //  10
        { "cbc 7030",                       "class Cti::Devices::Cbc7020Device" },
        { "cbc 8020",                       "class Cti::Devices::Cbc8020Device" },
        { "cbc 8024",                       "class Cti::Devices::Cbc8020Device" },
        { "cbc dnp",                        "class Cti::Devices::CbcDnpDevice" },
        { "cbc expresscom",                 "class CtiDeviceCBC" },
        { "cbc fp-2800",                    "class CtiDeviceCBC" },
        { "cbc versacom",                   "class CtiDeviceCBC" },
        { "ccu-700",                        "class CtiDeviceCCU710" },
        { "ccu-710a",                       "class CtiDeviceCCU710" },
        { "ccu-711",                        "class CtiDeviceCCU" },
        //  20
        { "ccu-721",                        "class Cti::Devices::Ccu721Device" },
        { "lcr-3102",                       "class Cti::Devices::Lcr3102Device" },
        { "lmt-2",                          "class Cti::Devices::Lmt2Device" },
        { "mct broadcast",                  "class Cti::Devices::MctBroadcastDevice" },
        { "mct-210",                        "class Cti::Devices::Mct210Device" },
        { "mct-212",                        "class Cti::Devices::Mct22xDevice" },
        { "mct-213",                        "class Cti::Devices::Mct210Device" },
        { "mct-224",                        "class Cti::Devices::Mct22xDevice" },
        { "mct-226",                        "class Cti::Devices::Mct22xDevice" },
        { "mct-240",                        "class Cti::Devices::Mct24xDevice" },
        //  30
        { "mct-242",                        "class Cti::Devices::Mct24xDevice" },
        { "mct-248",                        "class Cti::Devices::Mct24xDevice" },
        { "mct-250",                        "class Cti::Devices::Mct24xDevice" },
        { "mct-310",                        "class Cti::Devices::Mct310Device" },
        { "mct-310ct",                      "class Cti::Devices::Mct310Device" },
        { "mct-310id",                      "class Cti::Devices::Mct310Device" },
        { "mct-310idl",                     "class Cti::Devices::Mct310Device" },
        { "mct-310il",                      "class Cti::Devices::Mct310Device" },
        { "mct-318",                        "class Cti::Devices::Mct31xDevice" },
        { "mct-318l",                       "class Cti::Devices::Mct31xDevice" },
        //  40
        { "mct-360",                        "class Cti::Devices::Mct31xDevice" },
        { "mct-370",                        "class Cti::Devices::Mct31xDevice" },
        { "mct-410cl",                      "class Cti::Devices::Mct410Device" },
        { "mct-410fl",                      "class Cti::Devices::Mct410Device" },
        { "mct-410gl",                      "class Cti::Devices::Mct410Device" },
        { "mct-410il",                      "class Cti::Devices::Mct410Device" },
        { "mct-420cl",                      "class Cti::Devices::Mct420Device" },
        { "mct-420cd",                      "class Cti::Devices::Mct420Device" },
        { "mct-420fl",                      "class Cti::Devices::Mct420Device" },
        { "mct-420fd",                      "class Cti::Devices::Mct420Device" },
        //  50
        { "mct-430a",                       "class Cti::Devices::Mct470Device" },
        { "mct-430a3",                      "class Cti::Devices::Mct470Device" },
        { "mct-430s4",                      "class Cti::Devices::Mct470Device" },
        { "mct-430sl",                      "class Cti::Devices::Mct470Device" },
        { "mct-470",                        "class Cti::Devices::Mct470Device" },
        { "mct-440-2131b",                  "class Cti::Devices::Mct440_2131BDevice" },
        { "mct-440-2132b",                  "class Cti::Devices::Mct440_2132BDevice" },
        { "mct-440-2133b",                  "class Cti::Devices::Mct440_2133BDevice" },
        { "repeater 800",                   "class Cti::Devices::Repeater800Device" },
        { "repeater 801",                   "class Cti::Devices::Repeater800Device" },
        //  60
        { "repeater 850",                   "class Cti::Devices::Repeater850Device" },
        { "repeater 902",                   "class Cti::Devices::Repeater900Device" },
        { "repeater 921",                   "class Cti::Devices::Repeater900Device" },
        { "repeater",                       "class Cti::Devices::Repeater900Device" },
        { "page receiver",                  "class CtiDevicePagingReceiver" },
        { "rfn-410fl",                      "class Cti::Devices::RfnMeterDevice" },
        { "rfn-410fx",                      "class Cti::Devices::RfnResidentialDevice" },
        { "rfn-410fd",                      "class Cti::Devices::RfnResidentialDevice" },
        { "rfn-420fl",                      "class Cti::Devices::RfnLgyrFocusAlDevice" },
        { "rfn-420fx",                      "class Cti::Devices::RfnResidentialVoltageDevice" },
        //  70
        { "rfn-420fd",                      "class Cti::Devices::RfnResidentialVoltageDevice" },
        { "rfn-420frx",                     "class Cti::Devices::RfnResidentialVoltageDevice" },
        { "rfn-420frd",                     "class Cti::Devices::RfnResidentialVoltageDevice" },
        { "rfn-410cl",                      "class Cti::Devices::Rfn410CentronDevice" },
        { "rfn-420cl",                      "class Cti::Devices::Rfn420CentronDevice" },
        { "rfn-420cd",                      "class Cti::Devices::Rfn420CentronDevice" },
        { "rfn-430a3d",                     "class Cti::Devices::RfnCommercialDevice" },
        { "rfn-430a3t",                     "class Cti::Devices::RfnCommercialDevice" },
        { "rfn-430a3k",                     "class Cti::Devices::RfnCommercialDevice" },
        { "rfn-430a3r",                     "class Cti::Devices::RfnCommercialDevice" },
        //  80
        { "rfn-430kv",                      "class Cti::Devices::RfnCommercialDevice" },
        { "rfn-430sl0",                     "class Cti::Devices::RfnCommercialDevice" },
        { "rfn-430sl1",                     "class Cti::Devices::RfnCommercialDevice" },
        { "rfn-430sl2",                     "class Cti::Devices::RfnCommercialDevice" },
        { "rfn-430sl3",                     "class Cti::Devices::RfnCommercialDevice" },
        { "rfn-430sl4",                     "class Cti::Devices::RfnCommercialDevice" },
        { "rfn-510fl",                      "class Cti::Devices::RfnLgyrFocusAlDevice" },
        { "rfn-520fax",                     "class Cti::Devices::RfnResidentialVoltageDevice" },
        { "rfn-520frx",                     "class Cti::Devices::RfnResidentialVoltageDevice" },
        { "rfn-520faxd",                    "class Cti::Devices::RfnResidentialVoltageDevice" },
        //  90
        { "rfn-520frxd",                    "class Cti::Devices::RfnResidentialVoltageDevice" },
        { "rfn-530s4x",                     "class Cti::Devices::RfnCommercialDevice" },
        { "rfn-530s4eax",                   "class Cti::Devices::RfnCommercialDevice" },
        { "rfn-530s4eaxr",                  "class Cti::Devices::RfnCommercialDevice" },
        { "rfn-530s4erx",                   "class Cti::Devices::RfnCommercialDevice" },
        { "rfn-530s4erxr",                  "class Cti::Devices::RfnCommercialDevice" },

        { "rtu-dart",                       "class Cti::Devices::DnpRtuDevice" },
        { "rtu-dnp",                        "class Cti::Devices::DnpRtuDevice" },
        { "rtu-ilex",                       "class CtiDeviceILEX" },
        { "rtu-lmi",                        "class CtiDeviceLMI" },
        //  100
        { "rtu-modbus",                     "class Cti::Devices::ModbusDevice" },
        { "rtu-ses92",                      "class CtiDeviceRemote" },
        { "rtu-welco",                      "class CtiDeviceWelco" },
        { "rtc",                            "class CtiDeviceRTC" },
        { "rtm",                            "class CtiDeviceRTM" },
        { "golay group",                    "class CtiDeviceGroupGolay" },
        { "sa-105 group",                   "class CtiDeviceGroupSA105" },
        { "sa-205 group",                   "class CtiDeviceGroupSA205" },
        { "sa-305 group",                   "class CtiDeviceGroupSA305" },
        { "sa-digital group",               "class CtiDeviceGroupSADigital" },
        //  110
        { "ci customer",                    "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "lm control area",                "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "lm curtail program",             "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "lm direct program",              "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "lm energy exchange",             "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "lm sep program",                 "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "digi sep group",                 "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "emetcon group",                  "class CtiDeviceGroupEmetcon" },
        { "expresscom group",               "class CtiDeviceGroupExpresscom" },
        { "rfn expresscom group",           "class CtiDeviceGroupRfnExpresscom" },
        //  120
        { "mct group",                      "class CtiDeviceGroupMCT" },
        { "point group",                    "class CtiDeviceGroupPoint" },
        { "ripple group",                   "class CtiDeviceGroupRipple" },
        { "versacom group",                 "class CtiDeviceGroupVersacom" },
        { "macro group",                    "class CtiDeviceMacro" },
        { "script",                         "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "simple",                         "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "system",                         "class CtiDeviceSystem" },
        { "virtual system",                 "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "lcu-415",                        "class CtiDeviceLCU" },
        //  130
        { "lcu-eastriver",                  "class CtiDeviceLCU" },
        { "lcu-lg",                         "class CtiDeviceLCU" },
        { "lcu-t3026",                      "class CtiDeviceLCU" },
        { "rds terminal",                   "class Cti::Devices::RDSTransmitter" },
        { "snpp terminal",                  "class CtiDeviceSnppPagingTerminal" },
        { "tap terminal",                   "class Cti::Devices::TapPagingTerminal" },
        { "tcu-5000",                       "class CtiDeviceTCU" },
        { "tcu-5500",                       "class CtiDeviceTCU" },
        { "tnpp terminal",                  "class CtiDeviceTnppPagingTerminal" },
        { "wctp terminal",                  "class CtiDeviceWctpTerminal" },
        //  140
        { "alpha a1",                       "class CtiDeviceAlphaA1" },
        { "alpha a3",                       "class CtiDeviceKV2" },
        { "alpha power plus",               "class CtiDeviceAlphaPPlus" },
        { "davis weather",                  "class CtiDeviceDavis" },
        { "dct-501",                        "class Cti::Devices::Dct501Device" },
        { "dr-87",                          "class CtiDeviceDR87" },
        { "focus",                          "class Cti::Devices::CtiDeviceFocus" },
        { "ipc-410fl",                      "class Cti::Devices::Ipc410ALDevice" },
        { "ipc-420fd",                      "class Cti::Devices::Ipc420ADDevice" },
        { "ipc-430s4e",                     "class CtiDeviceLandisGyrS4" },
        //  150
        { "ipc-430sl",                      "class Cti::Devices::CtiDeviceSentinel" },
        { "fulcrum",                        "class CtiDeviceFulcrum" },
        { "ion-7330",                       "class CtiDeviceION" },
        { "ion-7700",                       "class CtiDeviceION" },
        { "ion-8300",                       "class CtiDeviceION" },
        { "kv",                             "class CtiDeviceKV2" },
        { "kv2",                            "class CtiDeviceKV2" },
        { "landis-gyr s4",                  "class CtiDeviceLandisGyrS4" },
        { "quantum",                        "class CtiDeviceQuantum" },
        { "sentinel",                       "class Cti::Devices::CtiDeviceSentinel" },
        //  160
        { "sixnet",                         "class CtiDeviceSixnet" },
        { "transdata mark-v",               "class CtiDeviceMarkV" },
        { "vectron",                        "class CtiDeviceVectron" },
        { "unknown",                        "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "invalid",                        "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "",                               "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "snuffleupagus",                  "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "giraffe",                        "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "ecobee program",                 "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "ecobee group",                   "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        //  170
        { "ltc",                            "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "go_regulator",                   "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "po_regulator",                   "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "honeywell program",              "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "honeywell group",                "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "nest program",                   "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "nest group",                     "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "itron program",                  "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" },
        { "itron group",                    "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}" }
    };

    std::vector<DeviceTypeRow> rowVec;
    std::vector<std::string> expected;

    for ( const std::pair<std::string, std::string> &dbTypeToTypename : dbTypesToTypenames )
    {
        DeviceTypeRow r = { dbTypeToTypename.first };

        rowVec.push_back(r);

        expected.push_back(dbTypeToTypename.second);
    }

    DeviceTypeReader reader(columnNames, rowVec);

    std::vector<std::string> results;

    while( reader() )
    {
        std::unique_ptr<CtiDeviceBase> dev(DeviceFactory(reader));

        results.push_back(
            dev.get()
                ? typeid(*dev).name()
                : "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}");
    }

    BOOST_CHECK_EQUAL_RANGES( expected, results );
}


BOOST_AUTO_TEST_CASE(test_lcuDeviceTypes)
{
    const std::vector<int> lcuTypes
    {
        TYPE_LCU415,
        TYPE_LCU415ER,
        TYPE_LCU415LG,
        TYPE_LCUT3026
    };

    std::vector<CtiDeviceLCU::CtiLCUType_t> expected
    {
        CtiDeviceLCU::LCU_STANDARD,
        CtiDeviceLCU::LCU_EASTRIVER,
        CtiDeviceLCU::LCU_LANDG,
        CtiDeviceLCU::LCU_T3026
    };

    std::vector<CtiDeviceLCU::CtiLCUType_t> results;

    for ( const int type : lcuTypes )
    {
        const std::unique_ptr<CtiDeviceBase> dev(createDeviceType(type));

        const CtiDeviceLCU *lcuDev = dynamic_cast<const CtiDeviceLCU *>(dev.get());

        BOOST_REQUIRE(lcuDev);

        results.push_back(lcuDev->getLCUType());
    }

    BOOST_CHECK_EQUAL_RANGES( expected, results );
}


BOOST_AUTO_TEST_SUITE_END()
