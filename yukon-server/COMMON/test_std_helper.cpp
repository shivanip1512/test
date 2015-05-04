#include <boost/test/unit_test.hpp>

#include "std_helper.h"

#include <functional>

BOOST_AUTO_TEST_SUITE( test_std_helper )

BOOST_AUTO_TEST_CASE(test_clamp_numeric)
{
    using Cti::clamp;

    BOOST_CHECK_EQUAL( 0, clamp(0, -10, 99));
    BOOST_CHECK_EQUAL( 0, clamp(0,  -1, 99));
    BOOST_CHECK_EQUAL( 0, clamp(0,   0, 99));
    BOOST_CHECK_EQUAL( 1, clamp(0,   1, 99));
    BOOST_CHECK_EQUAL(50, clamp(0,  50, 99));
    BOOST_CHECK_EQUAL(98, clamp(0,  98, 99));
    BOOST_CHECK_EQUAL(99, clamp(0,  99, 99));
    BOOST_CHECK_EQUAL(99, clamp(0, 100, 99));

    BOOST_CHECK_EQUAL( -100, clamp(-100, -1000, 100));
    BOOST_CHECK_EQUAL( -100, clamp(-100,  -101, 100));
    BOOST_CHECK_EQUAL( -100, clamp(-100,  -100, 100));
    BOOST_CHECK_EQUAL(  -99, clamp(-100,   -99, 100));
    BOOST_CHECK_EQUAL(  -50, clamp(-100,   -50, 100));
    BOOST_CHECK_EQUAL(    0, clamp(-100,     0, 100));
    BOOST_CHECK_EQUAL(   50, clamp(-100,    50, 100));
    BOOST_CHECK_EQUAL(   99, clamp(-100,    99, 100));
    BOOST_CHECK_EQUAL(  100, clamp(-100,   100, 100));
    BOOST_CHECK_EQUAL(  100, clamp(-100,   101, 100));
    BOOST_CHECK_EQUAL(  100, clamp(-100,  1000, 100));
}

BOOST_AUTO_TEST_CASE(test_clamp_templated)
{
    std::function<int(int)> clamp99 = Cti::clamp<0, 99>;

    BOOST_CHECK_EQUAL( 0, clamp99(-100));
    BOOST_CHECK_EQUAL( 0, clamp99( -10));
    BOOST_CHECK_EQUAL( 0, clamp99(  -1));
    BOOST_CHECK_EQUAL( 0, clamp99(   0));
    BOOST_CHECK_EQUAL( 1, clamp99(   1));
    BOOST_CHECK_EQUAL(50, clamp99(  50));
    BOOST_CHECK_EQUAL(98, clamp99(  98));
    BOOST_CHECK_EQUAL(99, clamp99(  99));
    BOOST_CHECK_EQUAL(99, clamp99( 100));
    BOOST_CHECK_EQUAL(99, clamp99(1000));

    std::function<int(int)> clamp100s = Cti::clamp<-100, 100>;

    BOOST_CHECK_EQUAL( -100, clamp100s(-1000));
    BOOST_CHECK_EQUAL( -100, clamp100s( -101));
    BOOST_CHECK_EQUAL( -100, clamp100s( -100));
    BOOST_CHECK_EQUAL(  -99, clamp100s(  -99));
    BOOST_CHECK_EQUAL(  -50, clamp100s(  -50));
    BOOST_CHECK_EQUAL(    0, clamp100s(    0));
    BOOST_CHECK_EQUAL(   50, clamp100s(   50));
    BOOST_CHECK_EQUAL(   99, clamp100s(   99));
    BOOST_CHECK_EQUAL(  100, clamp100s(  100));
    BOOST_CHECK_EQUAL(  100, clamp100s(  101));
    BOOST_CHECK_EQUAL(  100, clamp100s( 1000));
}

BOOST_AUTO_TEST_SUITE_END()
