#include <boost/test/unit_test.hpp>

#include "slctdev.h"
#include "devicetypes.h"
#include "dev_lcu.h"

#include "test_reader.h"

#include "boostutil.h"

#include <boost/assign/list_of.hpp>
#include <boost/optional.hpp>

#include <set>

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
        .repeat(9900, _);

    std::vector<bool> results;

    for( int type = 0; type < 10000; ++type )
    {
        results.push_back(isCarrierLPDeviceType(type));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expected.begin(), expected.end(),
       results.begin(), results.end());
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
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("class CtiDeviceCBC")
        ("class CtiDeviceCBC")
        ("class CtiDeviceCBC")
        ("class Cti::Devices::Cbc6510Device")
        ("class CtiDeviceCBC")
        ("class Cti::Devices::Cbc7020Device")
        ("class Cti::Devices::DnpDevice")
        //  50
        ("class Cti::Devices::Cbc8020Device")
        ("class Cti::Devices::RfnMeterDevice")
        ("class Cti::Devices::RfnResidentialDevice")
        ("class Cti::Devices::RfnResidentialDevice")
        ("class Cti::Devices::Rfn420FocusAlDevice")
        ("class Cti::Devices::RfnResidentialDevice")
        ("class Cti::Devices::RfnResidentialDevice")
        ("class Cti::Devices::RfnResidentialDevice")
        ("class Cti::Devices::RfnResidentialDevice")
        ("class Cti::Devices::RfnResidentialDevice")
        //  60
        ("class Cti::Devices::Rfn420CentronDevice")
        ("class Cti::Devices::Rfn420CentronDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        //  70
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfnCommercialDevice")
        ("class Cti::Devices::RfDaDevice")
        ("class CtiDeviceCCU710")
        ("class CtiDeviceCCU710")
        ("class CtiDeviceCCU")
        ("class Cti::Devices::Ccu721Device")
        ("class CtiDeviceILEX")
        ("class CtiDeviceWelco")
        ("class CtiDeviceRemote")
        //  80
        ("class Cti::Devices::DnpDevice")
        ("class Cti::Devices::DnpDevice")
        ("class CtiDeviceION")
        ("class CtiDeviceION")
        ("class CtiDeviceION")
        ("class CtiDeviceLCU")
        ("class CtiDeviceLCU")
        ("class CtiDeviceLCU")
        ("class CtiDeviceLCU")
        ("class CtiDeviceTCU")
        //  90
        ("class CtiDeviceTCU")
        ("class CtiDeviceMarkV")
        ("class CtiDeviceDavis")
        ("class CtiDeviceAlphaPPlus")
        ("class CtiDeviceFulcrum")
        ("class CtiDeviceLandisGyrS4")
        ("class CtiDeviceVectron")
        ("class CtiDeviceAlphaA1")
        ("class CtiDeviceDR87")
        ("class CtiDeviceQuantum")
        //  100
        ("class CtiDeviceKV2")
        ("class Cti::Devices::CtiDeviceSentinel")
        ("class Cti::Devices::CtiDeviceFocus")
        ("class CtiDeviceKV2")
        ("class CtiDeviceSixnet")
        ("class Cti::Devices::Ipc410ALDevice")
        ("class Cti::Devices::Ipc420ADDevice")
        ("class CtiDeviceLandisGyrS4")
        ("class Cti::Devices::CtiDeviceSentinel")
        ("class Cti::Devices::TapPagingTerminal")
        //  110
        ("class CtiDeviceWctpTerminal")
        ("class Cti::Devices::RDSTransmitter")
        ("class CtiDeviceSnppPagingTerminal")
        ("class CtiDevicePagingReceiver")
        ("class CtiDeviceTnppPagingTerminal")
        ("class CtiDeviceRTC")
        ("class CtiDeviceRTM")
        ("class CtiDeviceSeriesV")
        ("class CtiDeviceLMI")
        ("class Cti::Devices::ModbusDevice")
        //  120
        ("class CtiDeviceGridAdvisor")
        ("class CtiDeviceGridAdvisor")
        ("class CtiDeviceGroupEmetcon")
        ("class CtiDeviceGroupVersacom")
        ("class CtiDeviceGroupRipple")
        ("class CtiDeviceGroupPoint")
        ("class CtiDeviceGroupExpresscom")
        ("class CtiDeviceGroupRfnExpresscom")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        //  130
        ("class CtiDeviceGroupMCT")
        ("class CtiDeviceGroupGolay")
        ("class CtiDeviceGroupSADigital")
        ("class CtiDeviceGroupSA105")
        ("class CtiDeviceGroupSA205")
        ("class CtiDeviceGroupSA305")
        ("class Cti::Devices::MctBroadcastDevice")
        .repeat(11, "null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("class CtiDeviceMacro")
        ("class CtiDeviceSystem")
        .repeat(9850, "null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
            ;

    std::vector<std::string> results;

    for( int type = 0; type < 10000; ++type )
    {
        std::auto_ptr<CtiDeviceBase> dev(createDeviceType(type));

        results.push_back(
            dev.get()
                ? typeid(*dev).name()
                : "null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}");
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expected.begin(), expected.end(),
       results.begin(), results.end());
}


BOOST_AUTO_TEST_CASE(test_DeviceFactory)
{
    typedef Cti::Test::StringRow<1> DeviceTypeRow;
    typedef Cti::Test::TestReader<DeviceTypeRow> DeviceTypeReader;

    DeviceTypeRow columnNames    = {"type"};

    std::vector<std::pair<std::string, std::string>> dbTypesToTypenames = boost::assign::list_of
        (std::make_pair("capacitor bank neutral monitor", "class CtiDeviceGridAdvisor"))
        (std::make_pair("faulted circuit indicator",      "class CtiDeviceGridAdvisor"))
        (std::make_pair("cap bank",                       "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        (std::make_pair("cbc 6510",                       "class Cti::Devices::Cbc6510Device"))
        (std::make_pair("cbc 7010",                       "class CtiDeviceCBC"))
        (std::make_pair("cbc 7011",                       "class CtiDeviceCBC"))
        (std::make_pair("cbc 7012",                       "class CtiDeviceCBC"))
        (std::make_pair("cbc 7020",                       "class Cti::Devices::Cbc7020Device"))
        (std::make_pair("cbc 7022",                       "class Cti::Devices::Cbc7020Device"))
        (std::make_pair("cbc 7023",                       "class Cti::Devices::Cbc7020Device"))
        //  10
        (std::make_pair("cbc 7024",                       "class Cti::Devices::Cbc7020Device"))
        (std::make_pair("cbc 7030",                       "class Cti::Devices::Cbc7020Device"))
        (std::make_pair("cbc 8020",                       "class Cti::Devices::Cbc8020Device"))
        (std::make_pair("cbc 8024",                       "class Cti::Devices::Cbc8020Device"))
        (std::make_pair("cbc dnp",                        "class Cti::Devices::DnpDevice"))
        (std::make_pair("cbc expresscom",                 "class CtiDeviceCBC"))
        (std::make_pair("cbc fp-2800",                    "class CtiDeviceCBC"))
        (std::make_pair("cbc versacom",                   "class CtiDeviceCBC"))
        (std::make_pair("load tap changer",               "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        (std::make_pair("ccu-700",                        "class CtiDeviceCCU710"))
        //  20
        (std::make_pair("ccu-710a",                       "class CtiDeviceCCU710"))
        (std::make_pair("ccu-711",                        "class CtiDeviceCCU"))
        (std::make_pair("ccu-721",                        "class Cti::Devices::Ccu721Device"))
        (std::make_pair("lcr-3102",                       "class Cti::Devices::Lcr3102Device"))
        (std::make_pair("lmt-2",                          "class Cti::Devices::Lmt2Device"))
        (std::make_pair("mct broadcast",                  "class Cti::Devices::MctBroadcastDevice"))
        (std::make_pair("mct-210",                        "class Cti::Devices::Mct210Device"))
        (std::make_pair("mct-212",                        "class Cti::Devices::Mct22xDevice"))
        (std::make_pair("mct-213",                        "class Cti::Devices::Mct210Device"))
        (std::make_pair("mct-224",                        "class Cti::Devices::Mct22xDevice"))
        //  30
        (std::make_pair("mct-226",                        "class Cti::Devices::Mct22xDevice"))
        (std::make_pair("mct-240",                        "class Cti::Devices::Mct24xDevice"))
        (std::make_pair("mct-242",                        "class Cti::Devices::Mct24xDevice"))
        (std::make_pair("mct-248",                        "class Cti::Devices::Mct24xDevice"))
        (std::make_pair("mct-250",                        "class Cti::Devices::Mct24xDevice"))
        (std::make_pair("mct-310",                        "class Cti::Devices::Mct310Device"))
        (std::make_pair("mct-310ct",                      "class Cti::Devices::Mct310Device"))
        (std::make_pair("mct-310id",                      "class Cti::Devices::Mct310Device"))
        (std::make_pair("mct-310idl",                     "class Cti::Devices::Mct310Device"))
        (std::make_pair("mct-310il",                      "class Cti::Devices::Mct310Device"))
        //  40
        (std::make_pair("mct-318",                        "class Cti::Devices::Mct31xDevice"))
        (std::make_pair("mct-318l",                       "class Cti::Devices::Mct31xDevice"))
        (std::make_pair("mct-360",                        "class Cti::Devices::Mct31xDevice"))
        (std::make_pair("mct-370",                        "class Cti::Devices::Mct31xDevice"))
        (std::make_pair("mct-410cl",                      "class Cti::Devices::Mct410Device"))
        (std::make_pair("mct-410fl",                      "class Cti::Devices::Mct410Device"))
        (std::make_pair("mct-410gl",                      "class Cti::Devices::Mct410Device"))
        (std::make_pair("mct-410il",                      "class Cti::Devices::Mct410Device"))
        (std::make_pair("mct-420cl",                      "class Cti::Devices::Mct420Device"))
        (std::make_pair("mct-420cd",                      "class Cti::Devices::Mct420Device"))
        //  50
        (std::make_pair("mct-420fl",                      "class Cti::Devices::Mct420Device"))
        (std::make_pair("mct-420fd",                      "class Cti::Devices::Mct420Device"))
        (std::make_pair("mct-430a",                       "class Cti::Devices::Mct470Device"))
        (std::make_pair("mct-430a3",                      "class Cti::Devices::Mct470Device"))
        (std::make_pair("mct-430s4",                      "class Cti::Devices::Mct470Device"))
        (std::make_pair("mct-430sl",                      "class Cti::Devices::Mct470Device"))
        (std::make_pair("mct-470",                        "class Cti::Devices::Mct470Device"))
        (std::make_pair("mct-440-2131b",                  "class Cti::Devices::Mct440_2131BDevice"))
        (std::make_pair("mct-440-2132b",                  "class Cti::Devices::Mct440_2132BDevice"))
        (std::make_pair("mct-440-2133b",                  "class Cti::Devices::Mct440_2133BDevice"))
        //  60
        (std::make_pair("repeater 800",                   "class Cti::Devices::Repeater800Device"))
        (std::make_pair("repeater 801",                   "class Cti::Devices::Repeater800Device"))
        (std::make_pair("repeater 850",                   "class Cti::Devices::Repeater850Device"))
        (std::make_pair("repeater 902",                   "class Cti::Devices::Repeater900Device"))
        (std::make_pair("repeater 921",                   "class Cti::Devices::Repeater900Device"))
        (std::make_pair("repeater",                       "class Cti::Devices::Repeater900Device"))
        (std::make_pair("page receiver",                  "class CtiDevicePagingReceiver"))
        (std::make_pair("rfn-410fl",                      "class Cti::Devices::RfnMeterDevice"))
        (std::make_pair("rfn-410fx",                      "class Cti::Devices::RfnResidentialDevice"))
        (std::make_pair("rfn-410fd",                      "class Cti::Devices::RfnResidentialDevice"))
        //  70
        (std::make_pair("rfn-420fl",                      "class Cti::Devices::Rfn420FocusAlDevice"))
        (std::make_pair("rfn-420fx",                      "class Cti::Devices::RfnResidentialDevice"))
        (std::make_pair("rfn-420fd",                      "class Cti::Devices::RfnResidentialDevice"))
        (std::make_pair("rfn-420frx",                     "class Cti::Devices::RfnResidentialDevice"))
        (std::make_pair("rfn-420frd",                     "class Cti::Devices::RfnResidentialDevice"))
        (std::make_pair("rfn-410cl",                      "class Cti::Devices::RfnResidentialDevice"))
        (std::make_pair("rfn-420cl",                      "class Cti::Devices::Rfn420CentronDevice"))
        (std::make_pair("rfn-420cd",                      "class Cti::Devices::Rfn420CentronDevice"))
        (std::make_pair("rfn-430a3d",                     "class Cti::Devices::RfnCommercialDevice"))
        (std::make_pair("rfn-430a3t",                     "class Cti::Devices::RfnCommercialDevice"))
        //  80
        (std::make_pair("rfn-430a3k",                     "class Cti::Devices::RfnCommercialDevice"))
        (std::make_pair("rfn-430a3r",                     "class Cti::Devices::RfnCommercialDevice"))
        (std::make_pair("rfn-430kv",                      "class Cti::Devices::RfnCommercialDevice"))
        (std::make_pair("rfn-430sl0",                     "class Cti::Devices::RfnCommercialDevice"))
        (std::make_pair("rfn-430sl1",                     "class Cti::Devices::RfnCommercialDevice"))
        (std::make_pair("rfn-430sl2",                     "class Cti::Devices::RfnCommercialDevice"))
        (std::make_pair("rfn-430sl3",                     "class Cti::Devices::RfnCommercialDevice"))
        (std::make_pair("rfn-430sl4",                     "class Cti::Devices::RfnCommercialDevice"))
        (std::make_pair("rtu-dart",                       "class Cti::Devices::DnpDevice"))
        (std::make_pair("rtu-dnp",                        "class Cti::Devices::DnpDevice"))
        //  90
        (std::make_pair("rtu-ilex",                       "class CtiDeviceILEX"))
        (std::make_pair("rtu-lmi",                        "class CtiDeviceLMI"))
        (std::make_pair("rtu-modbus",                     "class Cti::Devices::ModbusDevice"))
        (std::make_pair("rtu-ses92",                      "class CtiDeviceRemote"))
        (std::make_pair("rtu-welco",                      "class CtiDeviceWelco"))
        (std::make_pair("rtc",                            "class CtiDeviceRTC"))
        (std::make_pair("rtm",                            "class CtiDeviceRTM"))
        (std::make_pair("golay group",                    "class CtiDeviceGroupGolay"))
        (std::make_pair("sa-105 group",                   "class CtiDeviceGroupSA105"))
        (std::make_pair("sa-205 group",                   "class CtiDeviceGroupSA205"))
        //  100
        (std::make_pair("sa-305 group",                   "class CtiDeviceGroupSA305"))
        (std::make_pair("sa-digital group",               "class CtiDeviceGroupSADigital"))
        (std::make_pair("ci customer",                    "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        (std::make_pair("lm control area",                "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        (std::make_pair("lm curtail program",             "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        (std::make_pair("lm direct program",              "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        (std::make_pair("lm energy exchange",             "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        (std::make_pair("lm sep program",                 "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        (std::make_pair("digi sep group",                 "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        (std::make_pair("emetcon group",                  "class CtiDeviceGroupEmetcon"))
        //  110
        (std::make_pair("expresscom group",               "class CtiDeviceGroupExpresscom"))
        (std::make_pair("rfn expresscom group",           "class CtiDeviceGroupRfnExpresscom"))
        (std::make_pair("mct group",                      "class CtiDeviceGroupMCT"))
        (std::make_pair("point group",                    "class CtiDeviceGroupPoint"))
        (std::make_pair("ripple group",                   "class CtiDeviceGroupRipple"))
        (std::make_pair("versacom group",                 "class CtiDeviceGroupVersacom"))
        (std::make_pair("macro group",                    "class CtiDeviceMacro"))
        (std::make_pair("script",                         "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        (std::make_pair("simple",                         "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        (std::make_pair("system",                         "class CtiDeviceSystem"))
        //  120
        (std::make_pair("virtual system",                 "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        (std::make_pair("lcu-415",                        "class CtiDeviceLCU"))
        (std::make_pair("lcu-eastriver",                  "class CtiDeviceLCU"))
        (std::make_pair("lcu-lg",                         "class CtiDeviceLCU"))
        (std::make_pair("lcu-t3026",                      "class CtiDeviceLCU"))
        (std::make_pair("rds terminal",                   "class Cti::Devices::RDSTransmitter"))
        (std::make_pair("snpp terminal",                  "class CtiDeviceSnppPagingTerminal"))
        (std::make_pair("tap terminal",                   "class Cti::Devices::TapPagingTerminal"))
        (std::make_pair("tcu-5000",                       "class CtiDeviceTCU"))
        (std::make_pair("tcu-5500",                       "class CtiDeviceTCU"))
        //  130
        (std::make_pair("tnpp terminal",                  "class CtiDeviceTnppPagingTerminal"))
        (std::make_pair("wctp terminal",                  "class CtiDeviceWctpTerminal"))
        (std::make_pair("alpha a1",                       "class CtiDeviceAlphaA1"))
        (std::make_pair("alpha a3",                       "class CtiDeviceKV2"))
        (std::make_pair("alpha power plus",               "class CtiDeviceAlphaPPlus"))
        (std::make_pair("davis weather",                  "class CtiDeviceDavis"))
        (std::make_pair("dct-501",                        "class Cti::Devices::Dct501Device"))
        (std::make_pair("dr-87",                          "class CtiDeviceDR87"))
        (std::make_pair("focus",                          "class Cti::Devices::CtiDeviceFocus"))
        (std::make_pair("ipc-410fl",                      "class Cti::Devices::Ipc410ALDevice"))
        //  140
        (std::make_pair("ipc-420fd",                      "class Cti::Devices::Ipc420ADDevice"))
        (std::make_pair("ipc-430s4e",                     "class CtiDeviceLandisGyrS4"))
        (std::make_pair("ipc-430sl",                      "class Cti::Devices::CtiDeviceSentinel"))
        (std::make_pair("fulcrum",                        "class CtiDeviceFulcrum"))
        (std::make_pair("ion-7330",                       "class CtiDeviceION"))
        (std::make_pair("ion-7700",                       "class CtiDeviceION"))
        (std::make_pair("ion-8300",                       "class CtiDeviceION"))
        (std::make_pair("kv",                             "class CtiDeviceKV2"))
        (std::make_pair("kv2",                            "class CtiDeviceKV2"))
        (std::make_pair("landis-gyr s4",                  "class CtiDeviceLandisGyrS4"))
        //  150
        (std::make_pair("quantum",                        "class CtiDeviceQuantum"))
        (std::make_pair("sentinel",                       "class Cti::Devices::CtiDeviceSentinel"))
        (std::make_pair("sixnet",                         "class CtiDeviceSixnet"))
        (std::make_pair("transdata mark-v",               "class CtiDeviceMarkV"))
        (std::make_pair("vectron",                        "class CtiDeviceVectron"))
        (std::make_pair("unknown",                        "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        (std::make_pair("invalid",                        "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        (std::make_pair("",                               "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        (std::make_pair("snuffleupagus",                  "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        (std::make_pair("giraffe",                        "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        //  160
        (std::make_pair("ecobee program",                 "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
        (std::make_pair("ecobee group",                   "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}"))
            ;

    std::vector<DeviceTypeRow> rowVec;
    std::vector<std::string> expected;

    for each( const std::pair<std::string, std::string> &dbTypeToTypename in dbTypesToTypenames )
    {
        DeviceTypeRow r = { dbTypeToTypename.first };

        rowVec.push_back(r);

        expected.push_back(dbTypeToTypename.second);
    }

    DeviceTypeReader reader(columnNames, rowVec);

    std::vector<std::string> results;

    while( reader() )
    {
        std::auto_ptr<CtiDeviceBase> dev(DeviceFactory(reader));

        results.push_back(
            dev.get()
                ? typeid(*dev).name()
                : "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}");
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expected.begin(), expected.end(),
       results.begin(), results.end());
}


BOOST_AUTO_TEST_CASE(test_lcuDeviceTypes)
{
    const std::vector<int> lcuTypes = boost::assign::list_of
        (TYPE_LCU415)
        (TYPE_LCU415ER)
        (TYPE_LCU415LG)
        (TYPE_LCUT3026)
            ;

    std::vector<CtiDeviceLCU::CtiLCUType_t> expected = boost::assign::list_of
        (CtiDeviceLCU::LCU_STANDARD)
        (CtiDeviceLCU::LCU_EASTRIVER)
        (CtiDeviceLCU::LCU_LANDG)
        (CtiDeviceLCU::LCU_T3026)
            ;

    std::vector<CtiDeviceLCU::CtiLCUType_t> results;

    for each( const int type in lcuTypes )
    {
        const std::auto_ptr<CtiDeviceBase> dev(createDeviceType(type));

        const CtiDeviceLCU *lcuDev = dynamic_cast<const CtiDeviceLCU *>(dev.get());

        BOOST_REQUIRE(lcuDev);

        results.push_back(lcuDev->getLCUType());
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expected.begin(), expected.end(),
       results.begin(), results.end());
}


BOOST_AUTO_TEST_SUITE_END()
