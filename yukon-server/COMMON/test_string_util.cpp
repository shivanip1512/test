#include <boost/test/unit_test.hpp>

#include "string_util.h"

BOOST_AUTO_TEST_SUITE( test_string_formatter )

BOOST_AUTO_TEST_CASE(test_formatSystemTime)
{
    SYSTEMTIME stm;

    stm.wYear   = 2014;
    stm.wMonth  = 12;
    stm.wDay    = 25;
    stm.wHour   = 12;
    stm.wMinute = 34;
    stm.wSecond = 56;
    stm.wMilliseconds = 789;

    BOOST_CHECK_EQUAL(Cti::formatSystemTime(stm), "12/25/2014 12:34:56.789");
}

BOOST_AUTO_TEST_CASE(test_commaFormatted)
{
    using Cti::commaFormatted;

    //  ignoring negative numbers for now
    BOOST_CHECK_EQUAL(  "0", commaFormatted(0));
    BOOST_CHECK_EQUAL(  "1", commaFormatted(1));
    BOOST_CHECK_EQUAL(  "9", commaFormatted(9));
    BOOST_CHECK_EQUAL( "99", commaFormatted(99));
    BOOST_CHECK_EQUAL("999", commaFormatted(999));
    BOOST_CHECK_EQUAL(  "1,000", commaFormatted(1000));
    BOOST_CHECK_EQUAL(  "9,999", commaFormatted(9999));
    BOOST_CHECK_EQUAL( "99,999", commaFormatted(99999));
    BOOST_CHECK_EQUAL("999,999", commaFormatted(999999));
    BOOST_CHECK_EQUAL(  "1,000,000", commaFormatted(1000000));
    BOOST_CHECK_EQUAL(  "9,999,999", commaFormatted(9999999));
    BOOST_CHECK_EQUAL( "99,999,999", commaFormatted(99999999));
    BOOST_CHECK_EQUAL("999,999,999", commaFormatted(999999999));
    BOOST_CHECK_EQUAL(  "1,000,000,000", commaFormatted(1000000000));
    BOOST_CHECK_EQUAL(  "9,999,999,999", commaFormatted(9999999999));
    BOOST_CHECK_EQUAL( "99,999,999,999", commaFormatted(99999999999));
    BOOST_CHECK_EQUAL("999,999,999,999", commaFormatted(999999999999));
}

BOOST_AUTO_TEST_CASE(test_toAsciiHex)
{
    using Cti::toAsciiHex;

    BOOST_CHECK_EQUAL('0', toAsciiHex(0x00));
    BOOST_CHECK_EQUAL('1', toAsciiHex(0x01));
    BOOST_CHECK_EQUAL('2', toAsciiHex(0x02));
    BOOST_CHECK_EQUAL('3', toAsciiHex(0x03));
    BOOST_CHECK_EQUAL('4', toAsciiHex(0x04));
    BOOST_CHECK_EQUAL('5', toAsciiHex(0x05));
    BOOST_CHECK_EQUAL('6', toAsciiHex(0x06));
    BOOST_CHECK_EQUAL('7', toAsciiHex(0x07));
    BOOST_CHECK_EQUAL('8', toAsciiHex(0x08));
    BOOST_CHECK_EQUAL('9', toAsciiHex(0x09));
    BOOST_CHECK_EQUAL('a', toAsciiHex(0x0a));
    BOOST_CHECK_EQUAL('b', toAsciiHex(0x0b));
    BOOST_CHECK_EQUAL('c', toAsciiHex(0x0c));
    BOOST_CHECK_EQUAL('d', toAsciiHex(0x0d));
    BOOST_CHECK_EQUAL('e', toAsciiHex(0x0e));
    BOOST_CHECK_EQUAL('f', toAsciiHex(0x0f));
}

BOOST_AUTO_TEST_CASE(test_toAscii)
{
    using Cti::toAscii;

    BOOST_CHECK_EQUAL('0', toAscii(0));
    BOOST_CHECK_EQUAL('1', toAscii(1));
    BOOST_CHECK_EQUAL('2', toAscii(2));
    BOOST_CHECK_EQUAL('3', toAscii(3));
    BOOST_CHECK_EQUAL('4', toAscii(4));
    BOOST_CHECK_EQUAL('5', toAscii(5));
    BOOST_CHECK_EQUAL('6', toAscii(6));
    BOOST_CHECK_EQUAL('7', toAscii(7));
    BOOST_CHECK_EQUAL('8', toAscii(8));
    BOOST_CHECK_EQUAL('9', toAscii(9));
}

BOOST_AUTO_TEST_SUITE_END()
