
/*-----------------------------------------------------------------------------*
*
* File:   test_ctistring
*
* Date:   1/24/2008
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/resolvers.cpp-arc  $
* REVISION     :  $Revision: 1.1.8.1 $
* DATE         :  $Date: 2008/11/12 17:27:31 $
*
* Copyright (c) 2008 Cannon Technologies. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "ctistring.h"

#define BOOST_AUTO_TEST_MAIN "Test CtiString"

#include <boost/test/unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>

using boost::unit_test_framework::test_suite;
using namespace std;


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
