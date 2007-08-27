
/*
 * file test_cmdparse.cpp
 *  
 * Author: Jian Liu 
 * Date: 07/27/2005 14:05:51 
 * 
 *
 * test CtiDate
 * 
 */


#include <boost/test/unit_test.hpp>

#include <string>
#include <rw/rwdate.h>
#include <rw/rwtime.h>
#include <rw/zone.h>
#include <iostream>

#include "dev_grp.h"
#include "dev_grp_expresscom.h"

#define BOOST_AUTO_TEST_MAIN "Test Device Group Base"
#include <boost/test/auto_unit_test.hpp>
using boost::unit_test_framework::test_suite;

BOOST_AUTO_UNIT_TEST(test_dev_group_dynamic_text)
{
    CtiDeviceGroupExpresscom group; // CtiDeviceGroupBase cant be instanciated

    string input1 = "control xcom cycle 50 count 8 period 30 truecycle";
    string input2 = "control xcom cycle 50 count 7 period 30 truecycle";
    string input3 = "control xcom cycle 50 count 1234 period 30 truecycle";
    string output = "control xcom cycle 50 period 30 truecycle";

    BOOST_CHECK_EQUAL( group.removeCommandDynamicText(input1), output );
    BOOST_CHECK_EQUAL( group.removeCommandDynamicText(input2), output );
    BOOST_CHECK_EQUAL( group.removeCommandDynamicText(input3), output );

}

