/*
    File test_fdrareva.cpp

    Author: Thain Spar
    Date:   5/22/2007

    Test Areva interface

*/

#define BOOST_AUTO_TEST_MAIN "Test FdrAreva"

#include <boost/test/unit_test.hpp>
#include <boost/test/auto_unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>

#include <string>
#include <iostream>
#include <time.h>
#include <sstream>    // for istringstream
#include <list>

#include "yukon.h"
#include "ctitime.h"
#include "ctidate.h"
#include "ctistring.h"
#include "fdrAreva.h"

using boost::unit_test_framework::test_suite;
using namespace std;

BOOST_AUTO_UNIT_TEST( test_something )
{   //example cases from an actual data file.
    BOOST_CHECK_EQUAL(1,1);
}
