#include <boost/test/unit_test.hpp>

#include "dev_rtm.h"

BOOST_AUTO_TEST_SUITE( test_dev_rtm )

struct test_CtiDeviceRTM : public CtiDeviceRTM
{
    using CtiDeviceRTM::tryDecodeAsSA305;
};

BOOST_AUTO_TEST_CASE(test_dev_rtm_tryDecodeSA305)
{
    test_CtiDeviceRTM rtm;

    std::string code, cmd;

    const unsigned char buf[] = {
        0x42, 0x31, 0x32, 0x41, 0x30, 0x33, 0x30, 0x37,
        0x30, 0x31, 0x39, 0x30, 0x39, 0x31, 0x30, 0x46, 0x45, 0x30, 0x32, 0x30, 0x35, 0x30
    };

    BOOST_CHECK( rtm.tryDecodeAsSA305(buf, sizeof(buf) / sizeof(*buf), code, cmd) );

    BOOST_CHECK_EQUAL(cmd, "SA 305 - code. Util: 2 , Fam: 1, Mbr: 0 (R: 16)..  Ctl Cmd. Strategy 63: Undefined, Reps 1, Pri 0");

    BOOST_CHECK_EQUAL(code, "90910fe020");
}

BOOST_AUTO_TEST_SUITE_END()
