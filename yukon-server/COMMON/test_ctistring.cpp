#include <boost/test/unit_test.hpp>

#include "ctistring.h"

BOOST_AUTO_TEST_SUITE( test_ctistring )

BOOST_AUTO_TEST_CASE(test_spad_zpad)
{
    CtiString testString = "test";

    BOOST_CHECK_EQUAL(testString, "test");
    testString.padFront(5, "0");
    BOOST_CHECK_EQUAL(testString, "0test");
    testString.padFront(6, " ");
    BOOST_CHECK_EQUAL(testString, " 0test");
    testString.padFront(6, "0");
    BOOST_CHECK_EQUAL(testString, " 0test");

    testString.padEnd(7, "0");
    BOOST_CHECK_EQUAL(testString, " 0test0");
    testString.padEnd(8, " ");
    BOOST_CHECK_EQUAL(testString, " 0test0 ");
    testString.padEnd(8, "0");
    BOOST_CHECK_EQUAL(testString, " 0test0 ");

    testString.padEnd(10, "0");
    BOOST_CHECK_EQUAL(testString, " 0test0 00");
    testString.padFront(12, "0");
    BOOST_CHECK_EQUAL(testString, "00 0test0 00");
}

BOOST_AUTO_TEST_SUITE_END()
