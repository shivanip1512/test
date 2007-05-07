/*
 * file test_cmdparse.cpp
 *  
 * Author: Jian Liu 
 * Date: 07/26/2005 10:10:13 
 * 
 *
 * test rwutil.h
 * 
 */
#define BOOST_AUTO_TEST_MAIN "Test RW Utils"

#include <boost/test/auto_unit_test.hpp>
#include <boost/test/unit_test.hpp>

#include <string>

#include "rwutil.h"
#include "utility.h"

using boost::unit_test_framework::test_suite;

BOOST_AUTO_UNIT_TEST(test_to_boost_date)
{
    RWDate rw_date = RWDate();
    date d = to_boost_date(rw_date);
    BOOST_CHECK_EQUAL( rw_date.dayOfMonth(), d.day() );
    BOOST_CHECK_EQUAL( rw_date.month(), d.month() );
    BOOST_CHECK_EQUAL( rw_date.year(), d.year() );
}


BOOST_AUTO_UNIT_TEST(test_stringCompareIgnoreCase)
{
    std::string s1 = "My Compare";
    std::string s2 = "my cOmParE";
    BOOST_CHECK_EQUAL( stringCompareIgnoreCase(s1, s2), 0 );


}
