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

#include <boost/test/unit_test.hpp>

#include <string>

#include "rwutil.h"

using boost::unit_test_framework::test_suite;

void test_to_boost_date()
{
    RWDate rw_date = RWDate();
    date d = to_boost_date(rw_date);
    BOOST_CHECK_EQUAL( rw_date.dayOfMonth(), d.day() );
    BOOST_CHECK_EQUAL( rw_date.month(), d.month() );
    BOOST_CHECK_EQUAL( rw_date.year(), d.year() );
}


void test_stringCompareIgnoreCase()
{
    std::string s1 = "My Compare";
    std::string s2 = "my cOmParE";
    BOOST_CHECK_EQUAL( stringCompareIgnoreCase(s1, s2), 0 );


}


test_suite*
init_unit_test_suite( int /*argc*/, char* /*argv*/[] ) {
    test_suite* test= BOOST_TEST_SUITE( "Test rwutil" );
    test->add( BOOST_TEST_CASE( &test_stringCompareIgnoreCase ) );
    test->add( BOOST_TEST_CASE( &test_to_boost_date ) );
    
    return test; 
}


