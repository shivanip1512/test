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

#include "ctidate.h"
#include "ctitime.h"

using boost::unit_test_framework::test_suite;

void test_ctidate_methods()
{
    // check the function match between CtiTime and RWTime
    RWDate rw_date = RWDate();
    CtiDate d(rw_date.dayOfMonth(), rw_date.month(), rw_date.year());
    CtiDate d1 = CtiDate();
    BOOST_CHECK_EQUAL( d.asString(), d1.asString() );
    BOOST_CHECK_EQUAL( rw_date.dayOfMonth(), d.dayOfMonth() );
    BOOST_CHECK_EQUAL( rw_date.month(), d.month() );
    BOOST_CHECK_EQUAL( rw_date.year(), d.year() );
    BOOST_CHECK_EQUAL( rw_date.weekDayName().data(), d.weekDayName() );
    BOOST_CHECK_EQUAL( rw_date.weekDay(), d.weekDay() );
    BOOST_CHECK_EQUAL( rw_date.day(), d.day() );

    
    //static methods
    BOOST_CHECK_EQUAL( RWDate::now().dayOfMonth(), CtiDate::now().dayOfMonth() );
    BOOST_CHECK_EQUAL( RWDate::daysInMonthYear(12, 1990), CtiDate::daysInMonthYear(12, 1990) );


    // check constructing from CtiDate
	//std::cout << d.asString() << std::endl;
    CtiTime ct(d);
    BOOST_CHECK_EQUAL( d.asString(), CtiDate(ct).asString() );
    CtiDate cd4(ct);
    BOOST_CHECK_EQUAL( cd4.asString(), CtiDate(ct).asString() );


    // check the special time values
    std::cout << "neg_infin date as string: " << CtiDate(CtiDate::neg_infin).asString() << std::endl;

    /*---------- can not call year() day(), etc. on neg_infin
    std::cout << "neg_infin date year: " << CtiDate(CtiDate::neg_infin).year() << std::endl;
    std::cout << "neg_infin date day: " << CtiDate(CtiDate::neg_infin).day() << std::endl;
    */

    CtiDate ct1 = CtiDate(CtiDate::not_a_date);

    std::cout << "not a date as string: " << ct1.asString() << std::endl;

    /*---------- can not call year() day(), etc. on not_a_date
    std::cout << "not a date year: " << ct1.year() << std::endl;
    std::cout << "not a date day: " << ct1.day() << std::endl;
    */


    BOOST_CHECK_EQUAL( CtiDate(CtiDate::neg_infin).isValid(), true );
    BOOST_CHECK_EQUAL( CtiDate(CtiDate::pos_infin).isValid(), true );
    BOOST_CHECK_EQUAL( CtiDate(ct1).isValid(), false );


    // check creating a CtiDate from a special CtiTime
    CtiTime cd1(CtiTime::not_a_time);
    CtiTime cd2(CtiTime::pos_infin);

    CtiDate ct4(cd1);
    CtiDate ct5(cd2);

    BOOST_CHECK_EQUAL( ct4.isValid(), false );
    BOOST_CHECK_EQUAL( ct5.is_pos_infinity(), true );
    BOOST_CHECK_EQUAL( ct5.isValid(), true );

}


void test_ctidate_operators()
{
    // check the operators
    CtiDate d = CtiDate();
    CtiDate d1 = d;
    BOOST_CHECK_EQUAL( true, d1 == d );
    BOOST_CHECK_EQUAL( d.asString(), d1.asString() );
    
    d1 = d - 10;
    BOOST_CHECK_EQUAL( d.day() - 10, d1.day() );
    
    d1 = d + 10;
    BOOST_CHECK_EQUAL( d.day() + 10, d1.day() );
    BOOST_CHECK_EQUAL( false, d1 <= d );
    BOOST_CHECK_EQUAL( false, d1 == d );
    BOOST_CHECK_EQUAL( true, d1 >= d );
    BOOST_CHECK_EQUAL( true, d1 != d );
    BOOST_CHECK_EQUAL( false, d1 < d );
    BOOST_CHECK_EQUAL( true, d1 > d );
    BOOST_CHECK_EQUAL( true, CtiDate(CtiDate::neg_infin) < d );
    BOOST_CHECK_EQUAL( false, CtiDate(CtiDate::pos_infin) < d );
    BOOST_CHECK_EQUAL( true, CtiDate(CtiDate::not_a_date) > d ); //!!!

    // check the operators with special time values
    CtiDate ct(CtiDate::neg_infin);
    CtiDate ct1 = ct + 4;
    BOOST_CHECK_EQUAL( true, ct1 <= ct );
    BOOST_CHECK_EQUAL( true, ct1 == ct );
    BOOST_CHECK_EQUAL( true, ct1 >= ct );
    BOOST_CHECK_EQUAL( false, ct1 != ct );
    BOOST_CHECK_EQUAL( false, ct1 < ct );  //!!!!!!
    BOOST_CHECK_EQUAL( false, ct1 > ct );  //!!!!!! basically, this means ct1==ct

    BOOST_CHECK_EQUAL( false, ct > d );
    BOOST_CHECK_EQUAL( true, ct < d );
    
    ct= CtiDate(CtiDate::not_a_date);
    ct1 = ct - 4;
    BOOST_CHECK_EQUAL( true, ct1 <= ct );
    BOOST_CHECK_EQUAL( true, ct1 == ct );
    BOOST_CHECK_EQUAL( true, ct1 >= ct );
    BOOST_CHECK_EQUAL( false, ct1 != ct );
    BOOST_CHECK_EQUAL( false, ct1 < ct );
    BOOST_CHECK_EQUAL( false, ct1 > ct );  //!!!!!! this means ct == ct1

    BOOST_CHECK_EQUAL( true, ct > d );
    BOOST_CHECK_EQUAL( false, ct < d );  // means the not_a_date greater than current date, and any date!!!


}


test_suite*
init_unit_test_suite( int /*argc*/, char* /*argv*/[] ) {
    test_suite* test= BOOST_TEST_SUITE( "Test CtiDate" );
    test->add( BOOST_TEST_CASE( &test_ctidate_methods )) ;
    test->add( BOOST_TEST_CASE( &test_ctidate_operators ));
    
    return test; 
}


