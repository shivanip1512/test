#include <boost/test/unit_test.hpp>

#include "slctdev.h"
#include "devicetypes.h"

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
        ("class Cti::Devices::Cbc8020Device")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
        ("class CtiDeviceCCU710")
        ("class CtiDeviceCCU710")
        ("class CtiDeviceCCU")
        ("class Cti::Devices::Ccu721Device")
        ("class CtiDeviceILEX")
        ("class CtiDeviceWelco")
        ("class CtiDeviceRemote")
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
        ("class CtiDeviceKV2")
        ("class Cti::Devices::CtiDeviceSentinel")
        ("class Cti::Devices::CtiDeviceFocus")
        ("class CtiDeviceKV2")
        ("class CtiDeviceSixnet")
        ("class Cti::Devices::Ipc410ALDevice")
        ("class Cti::Devices::Ipc420ADDevice")
        ("class CtiDeviceLandisGyrS4")
        ("class Cti::Devices::CtiDeviceSentinel")
        ("class CtiDeviceTapPagingTerminal")
        ("class CtiDeviceWctpTerminal")
        ("class Cti::Devices::RDSTransmitter")
        ("class CtiDeviceSnppPagingTerminal")
        ("class CtiDevicePagingReceiver")
        ("class CtiDeviceTnppPagingTerminal")
        ("class CtiDeviceRTC")
        ("class CtiDeviceRTM")
        ("class CtiDeviceFMU")
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
        ("class CtiDeviceGroupRfnExpresscom")
        ("null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
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
        .repeat(9856, "null pointer {C5BECC2F-478B-FB06-55A5-1A91B7BABB1A}")
            ;

    std::vector<std::string> results;

    for( int type = 0; type < 10000; ++type )
    {
        CtiDeviceBase *dev = createDeviceType(type);

        results.push_back(
           dev ? typeid(*dev).name()
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

    DeviceTypeRow dtValues[] = {
        {"capacitor bank neutral monitor"},
        {"faulted circuit indicator"},
        {"cap bank"},
        {"cbc 6510"},
        {"cbc 7010"},
        {"cbc 7011"},
        {"cbc 7012"},
        {"cbc 7020"},
        {"cbc 7022"},
        {"cbc 7023"},
        {"cbc 7024"},
        {"cbc 7030"},
        {"cbc 8020"},
        {"cbc 8024"},
        {"cbc dnp"},
        {"cbc expresscom"},
        {"cbc fp-2800"},
        {"cbc versacom"},
        {"load tap changer"},
        {"ccu-700"},
        {"ccu-710a"},
        {"ccu-711"},
        {"ccu-721"},
        {"lcr-3102"},
        {"lmt-2"},
        {"mct broadcast"},
        {"mct-210"},
        {"mct-212"},
        {"mct-213"},
        {"mct-224"},
        {"mct-226"},
        {"mct-240"},
        {"mct-242"},
        {"mct-248"},
        {"mct-250"},
        {"mct-310"},
        {"mct-310ct"},
        {"mct-310id"},
        {"mct-310idl"},
        {"mct-310il"},
        {"mct-318"},
        {"mct-318l"},
        {"mct-360"},
        {"mct-370"},
        {"mct-410cl"},
        {"mct-410fl"},
        {"mct-410gl"},
        {"mct-410il"},
        {"mct-420cl"},
        {"mct-420cd"},
        {"mct-420fl"},
        {"mct-420fd"},
        {"mct-430a"},
        {"mct-430a3"},
        {"mct-430s4"},
        {"mct-430sl"},
        {"mct-470"},
        {"mct-440-2131b"},
        {"mct-440-2132b"},
        {"mct-440-2133b"},
        {"repeater 800"},
        {"repeater 801"},
        {"repeater 850"},
        {"repeater 902"},
        {"repeater 921"},
        {"repeater"},
        {"fmu"},
        {"page receiver"},
        {"rtu-dart"},
        {"rtu-dnp"},
        {"rtu-ilex"},
        {"rtu-lmi"},
        {"rtu-modbus"},
        {"rtu-ses92"},
        {"rtu-welco"},
        {"rtc"},
        {"rtm"},
        {"golay group"},
        {"sa-105 group"},
        {"sa-205 group"},
        {"sa-305 group"},
        {"sa-digital group"},
        {"ci customer"},
        {"lm control area"},
        {"lm curtail program"},
        {"lm direct program"},
        {"lm energy exchange"},
        {"lm sep program"},
        {"digi sep group"},
        {"emetcon group"},
        {"expresscom group"},
        {"rfn expresscom group"},
        {"mct group"},
        {"point group"},
        {"ripple group"},
        {"versacom group"},
        {"macro group"},
        {"script"},
        {"simple"},
        {"system"},
        {"virtual system"},
        {"lcu-415"},
        {"lcu-eastriver"},
        {"lcu-lg"},
        {"lcu-t3026"},
        {"rds terminal"},
        {"snpp terminal"},
        {"tap terminal"},
        {"tcu-5000"},
        {"tcu-5500"},
        {"tnpp terminal"},
        {"wctp terminal"},
        {"alpha a1"},
        {"alpha a3"},
        {"alpha power plus"},
        {"davis weather"},
        {"dct-501"},
        {"dr-87"},
        {"focus"},
        {"ipc-410fl"},
        {"ipc-420fd"},
        {"ipc-430s4e"},
        {"ipc-430sl"},
        {"fulcrum"},
        {"ion-7330"},
        {"ion-7700"},
        {"ion-8300"},
        {"kv"},
        {"kv2"},
        {"landis-gyr s4"},
        {"quantum"},
        {"sentinel"},
        {"sixnet"},
        {"transdata mark-v"},
        {"vectron"},
        {"unknown"},
        {"invalid"},
        {""},
        {"snuffleupagus"},
        {"giraffe"},
    };

    const unsigned rowCount = sizeof(dtValues) / sizeof(*dtValues);

    std::vector<DeviceTypeRow> rowVec(dtValues, dtValues + rowCount);

    DeviceTypeReader reader(columnNames, rowVec);

    std::vector<std::string> expected = boost::assign::list_of
        ("class CtiDeviceGridAdvisor")
        ("class CtiDeviceGridAdvisor")
        ("null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}")
        ("class Cti::Devices::Cbc6510Device")
        ("class CtiDeviceCBC")
        ("class CtiDeviceCBC")
        ("class CtiDeviceCBC")
        ("class Cti::Devices::Cbc7020Device")
        ("class Cti::Devices::Cbc7020Device")
        ("class Cti::Devices::Cbc7020Device")
        ("class Cti::Devices::Cbc7020Device")
        ("class Cti::Devices::Cbc7020Device")
        ("class Cti::Devices::Cbc8020Device")
        ("class Cti::Devices::Cbc8020Device")
        ("class Cti::Devices::DnpDevice")
        ("class CtiDeviceCBC")
        ("class CtiDeviceCBC")
        ("class CtiDeviceCBC")
        ("null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}")
        ("class CtiDeviceCCU710")
        ("class CtiDeviceCCU710")
        ("class CtiDeviceCCU")
        ("class Cti::Devices::Ccu721Device")
        ("class Cti::Devices::Lcr3102Device")
        ("class Cti::Devices::Lmt2Device")
        ("class Cti::Devices::MctBroadcastDevice")
        ("class Cti::Devices::Mct210Device")
        ("class Cti::Devices::Mct22xDevice")
        ("class Cti::Devices::Mct210Device")
        ("class Cti::Devices::Mct22xDevice")
        ("class Cti::Devices::Mct22xDevice")
        ("class Cti::Devices::Mct24xDevice")
        ("class Cti::Devices::Mct24xDevice")
        ("class Cti::Devices::Mct24xDevice")
        ("class Cti::Devices::Mct24xDevice")
        ("class Cti::Devices::Mct310Device")
        ("class Cti::Devices::Mct310Device")
        ("class Cti::Devices::Mct310Device")
        ("class Cti::Devices::Mct310Device")
        ("class Cti::Devices::Mct310Device")
        ("class Cti::Devices::Mct31xDevice")
        ("class Cti::Devices::Mct31xDevice")
        ("class Cti::Devices::Mct31xDevice")
        ("class Cti::Devices::Mct31xDevice")
        ("class Cti::Devices::Mct410Device")
        ("class Cti::Devices::Mct410Device")
        ("class Cti::Devices::Mct410Device")
        ("class Cti::Devices::Mct410Device")
        ("class Cti::Devices::Mct420Device")
        ("class Cti::Devices::Mct420Device")
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
        ("class Cti::Devices::Repeater800Device")
        ("class Cti::Devices::Repeater800Device")
        ("class Cti::Devices::Repeater850Device")
        ("class Cti::Devices::Repeater900Device")
        ("class Cti::Devices::Repeater900Device")
        ("class Cti::Devices::Repeater900Device")
        ("class CtiDeviceFMU")
        ("class CtiDevicePagingReceiver")
        ("class Cti::Devices::DnpDevice")
        ("class Cti::Devices::DnpDevice")
        ("class CtiDeviceILEX")
        ("class CtiDeviceLMI")
        ("class Cti::Devices::ModbusDevice")
        ("class CtiDeviceRemote")
        ("class CtiDeviceWelco")
        ("class CtiDeviceRTC")
        ("class CtiDeviceRTM")
        ("class CtiDeviceGroupGolay")
        ("class CtiDeviceGroupSA105")
        ("class CtiDeviceGroupSA205")
        ("class CtiDeviceGroupSA305")
        ("class CtiDeviceGroupSADigital")
        ("null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}")
        ("null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}")
        ("null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}")
        ("null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}")
        ("null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}")
        ("null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}")
        ("null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}")
        ("class CtiDeviceGroupEmetcon")
        ("class CtiDeviceGroupExpresscom")
        ("class CtiDeviceGroupRfnExpresscom")
        ("class CtiDeviceGroupMCT")
        ("class CtiDeviceGroupPoint")
        ("class CtiDeviceGroupRipple")
        ("class CtiDeviceGroupVersacom")
        ("class CtiDeviceMacro")
        ("null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}")
        ("null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}")
        ("class CtiDeviceSystem")
        ("null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}")
        ("class CtiDeviceLCU")
        ("class CtiDeviceLCU")
        ("class CtiDeviceLCU")
        ("class CtiDeviceLCU")
        ("class Cti::Devices::RDSTransmitter")
        ("class CtiDeviceSnppPagingTerminal")
        ("class CtiDeviceTapPagingTerminal")
        ("class CtiDeviceTCU")
        ("class CtiDeviceTCU")
        ("class CtiDeviceTnppPagingTerminal")
        ("class CtiDeviceWctpTerminal")
        ("class CtiDeviceAlphaA1")
        ("class CtiDeviceKV2")
        ("class CtiDeviceAlphaPPlus")
        ("class CtiDeviceDavis")
        ("class Cti::Devices::Dct501Device")
        ("class CtiDeviceDR87")
        ("class Cti::Devices::CtiDeviceFocus")
        ("class Cti::Devices::Ipc410ALDevice")
        ("class Cti::Devices::Ipc420ADDevice")
        ("class CtiDeviceLandisGyrS4")
        ("class Cti::Devices::CtiDeviceSentinel")
        ("class CtiDeviceFulcrum")
        ("class CtiDeviceION")
        ("class CtiDeviceION")
        ("class CtiDeviceION")
        ("class CtiDeviceKV2")
        ("class CtiDeviceKV2")
        ("class CtiDeviceLandisGyrS4")
        ("class CtiDeviceQuantum")
        ("class Cti::Devices::CtiDeviceSentinel")
        ("class CtiDeviceSixnet")
        ("class CtiDeviceMarkV")
        ("class CtiDeviceVectron")
        ("null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}")
        ("null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}")
        ("null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}")
        ("null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}")
        ("null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}")
            ;

    std::vector<std::string> results;

    while( reader() )
    {
        CtiDeviceBase *dev = DeviceFactory(reader);

        results.push_back(
           dev ? typeid(*dev).name()
               : "null pointer {0158EE7B-419F-EC43-9382-3496ED9E5F67}");
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expected.begin(), expected.end(),
       results.begin(), results.end());
}


BOOST_AUTO_TEST_SUITE_END()
