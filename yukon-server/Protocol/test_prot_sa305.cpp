#include <boost/test/auto_unit_test.hpp>

#include "prot_sa305.h"
#include "devicetypes.h"

BOOST_AUTO_TEST_SUITE( test_prot_sa305 )

BOOST_AUTO_TEST_CASE(test_sa305_description)
{
    const unsigned char buf[] = { 0xa1, 0x29, 0x03, 0x05, 0x24, 0x24, 0x43, 0xf8, 0x08, 0x3d };

    CtiProtocolSA305 prot(buf, sizeof(buf) / sizeof(*buf));

    prot.setTransmitterType(TYPE_RTC);

    std::string expected = "SA 305 - code. Util: 2 , Fam: 1, Mbr: 0 (R: 16)..  Ctl Cmd. Strategy 63: Undefined, Reps 1, Pri 0";

    BOOST_CHECK_EQUAL(prot.getDescription(), expected);
}


BOOST_AUTO_TEST_CASE(test_sa305_asciistring)
{
    const unsigned char buf[] = { 0xa1, 0x29, 0x03, 0x05, 0x24, 0x24, 0x43, 0xf8, 0x08, 0x3d };

    CtiProtocolSA305 prot(buf, sizeof(buf) / sizeof(*buf));

    prot.setTransmitterType(TYPE_RTC);

    std::string expected = "90910fe020";

    BOOST_CHECK_EQUAL(prot.getAsciiString(), expected);
}


BOOST_AUTO_TEST_SUITE_END()
