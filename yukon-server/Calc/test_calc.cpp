
/*
 * file test_cmdparse.cpp
 *
 * Author: Jian Liu
 * Date: 08/05/2005 13:25:55
 *
 *
 * test ctitime.cpp
 *
 *
 */
#define BOOST_AUTO_TEST_MAIN "Test Calc"

#include "precompiled.h"
#include "calc.h"

#include <boost/test/unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>


#include <string>
#include <rw/rwdate.h>
#include <rw/rwtime.h>
#include <rw/zone.h>
#include <iostream>
#include <time.h>
#include <sstream>    // for istringstream
#include <locale>

using boost::unit_test_framework::test_suite;
using namespace std;

BOOST_AUTO_TEST_CASE(test_get_update_type)
{
    CtiCalc calc = CtiCalc(1, "On Timer", 10,  "y");
    BOOST_CHECK_EQUAL(calc.getUpdateType(), periodic);
}

