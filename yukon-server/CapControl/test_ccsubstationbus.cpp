/*---------------------------------------------------------------------------
        Filename:  test_ccapbank.cpp

        Programmer:  Jess Oteson

        Initial Date:  5/11/2007

        COPYRIGHT:  Copyright (C) Cannon Technologies 2007
---------------------------------------------------------------------------*/

#define BOOST_AUTO_TEST_MAIN "Test CCCapBank"

#include <boost/test/unit_test.hpp>
#include <boost/test/auto_unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>


#include <string>
#include <rw/rwdate.h>
#include <rw/rwtime.h>
#include <rw/zone.h>
#include <iostream>
#include <time.h>
#include <sstream>    // for istringstream
#include <locale>

#include "yukon.h"
#include "ctitime.h"
#include "ccsubstationbus.h"

using boost::unit_test_framework::test_suite;
using namespace std;

BOOST_AUTO_UNIT_TEST(test_get_bank_size)
{
    CtiCCSubstationBus bus;
    bus.setPAOId(1);
    BOOST_CHECK_EQUAL(bus.getPAOId(), 1);
}

