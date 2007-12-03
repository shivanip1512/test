/*-----------------------------------------------------------------------------*
*
* File:   test_hash.cpp
*
* Date:   11/27/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/INCLUDE/test_queue.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2007/12/03 22:22:11 $
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#define BOOST_AUTO_TEST_MAIN "Test CtiHash"

#include <boost/thread/thread.hpp>
#include <boost/test/unit_test.hpp>
#include <boost/test/auto_unit_test.hpp>
#include <boost/test/floating_point_comparison.hpp>
#include "hashkey.h"

using namespace std;

using boost::unit_test_framework::test_suite;

BOOST_AUTO_UNIT_TEST(test_hash_integer_operators)
{
    CtiHashKey aHashKey = CtiHashKey(1);
    CtiHashKey bHashKey = CtiHashKey(2);
    CtiHashKey cHashKey = CtiHashKey(3);
    CtiHashKey dHashKey = CtiHashKey(2);

    BOOST_CHECK(bHashKey == dHashKey);
    BOOST_CHECK(!(cHashKey == dHashKey));
    BOOST_CHECK(aHashKey < bHashKey);
    BOOST_CHECK(bHashKey < cHashKey);
    BOOST_CHECK(!(bHashKey < aHashKey));
}

BOOST_AUTO_UNIT_TEST(test_hash_string_operators)
{
    CtiHashKey aHashKey = CtiHashKey("abc");
    CtiHashKey bHashKey = CtiHashKey("123");
    CtiHashKey cHashKey = CtiHashKey("abc123");
    CtiHashKey dHashKey = CtiHashKey("123");

    BOOST_CHECK(bHashKey == dHashKey);
    BOOST_CHECK(!(cHashKey == dHashKey));
    BOOST_CHECK(!(aHashKey == dHashKey));
    BOOST_CHECK(!(aHashKey == bHashKey));

    dHashKey.setID(aHashKey.getID());
    BOOST_CHECK(!(aHashKey == dHashKey));

    bHashKey.setID(1);
    cHashKey.setID(2);
    BOOST_CHECK(bHashKey < cHashKey);
}

