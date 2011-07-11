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
 
#include <boost/test/floating_point_comparison.hpp>

#define BOOST_TEST_MAIN "Test CtiDate"
#define BOOST_AUTO_TEST_MAIN "Test CtiDate"

#include "yukon.h"
#include "ctidate.h"
#include "ctitime.h"

#include <boost/test/unit_test.hpp>

#include "boostutil.h"

#include <string>
#include <rw/rwdate.h>
#include <rw/rwtime.h>
#include <rw/zone.h>
#include <iostream>

using std::string;
using boost::unit_test_framework::test_suite;

BOOST_AUTO_TEST_CASE(test_ctidate_methods)
{
    // check the function match between CtiTime and RWTime
    for( int i = 1; i < 12; i++ )
    {
        {
            RWDate rw_date = RWDate(1, i, 2007);
            CtiDate d(rw_date.dayOfMonth(), rw_date.month(), rw_date.year());
            CtiDate d1 = CtiDate(1, i, 2007);
            BOOST_CHECK_EQUAL( d.asString(), d1.asString() );
            BOOST_CHECK_EQUAL( rw_date.dayOfMonth(), d.dayOfMonth() );
            BOOST_CHECK_EQUAL( rw_date.month(), d.month() );
            BOOST_CHECK_EQUAL( rw_date.year(), d.year() );
            BOOST_CHECK_EQUAL( rw_date.weekDayName().data(), d.weekDayName() );
            BOOST_CHECK_EQUAL( rw_date.weekDay() % 7, d.weekDay() );  //  CtiDate is 0-6 Sun-Sat, RWDate is 1-7 Mon-Sun
            BOOST_CHECK_EQUAL( rw_date.day(), d.day() );

            // check constructing from CtiDate
            //std::cout << d.asString() << std::endl;
            CtiTime ct(d);
            BOOST_CHECK_EQUAL( d.asString(), CtiDate(ct).asString() );
            CtiDate cd4(ct);
            BOOST_CHECK_EQUAL( cd4.asString(), CtiDate(ct).asString() );
        }
        {
            RWDate rw_date = RWDate(RWDate::daysInMonthYear(i, 2007), i, 2007);
            CtiDate d(rw_date.dayOfMonth(), rw_date.month(), rw_date.year());
            CtiDate d1 = CtiDate(CtiDate::daysInMonthYear(i, 2007), i, 2007);
            BOOST_CHECK_EQUAL( d.asString(), d1.asString() );
            BOOST_CHECK_EQUAL( rw_date.dayOfMonth(), d.dayOfMonth() );
            BOOST_CHECK_EQUAL( rw_date.month(), d.month() );
            BOOST_CHECK_EQUAL( rw_date.year(), d.year() );
            BOOST_CHECK_EQUAL( rw_date.weekDayName().data(), d.weekDayName() );
            BOOST_CHECK_EQUAL( rw_date.weekDay() % 7, d.weekDay() );  //  CtiDate is 0-6 Sun-Sat, RWDate is 1-7 Mon-Sun
            BOOST_CHECK_EQUAL( rw_date.day(), d.day() );

            // check constructing from CtiDate
            //std::cout << d.asString() << std::endl;
            CtiTime ct(d);
            BOOST_CHECK_EQUAL( d.asString(), CtiDate(ct).asString() );
            CtiDate cd4(ct);
            BOOST_CHECK_EQUAL( cd4.asString(), CtiDate(ct).asString() );
        }
    }


    //static methods
    BOOST_CHECK_EQUAL( RWDate::now().dayOfMonth(), CtiDate::now().dayOfMonth() );
    BOOST_CHECK_EQUAL( RWDate::daysInMonthYear(12, 1990), CtiDate::daysInMonthYear(12, 1990) );

    BOOST_CHECK_EQUAL( 0, CtiDate::daysInMonthYear(13, 1990) );
    BOOST_CHECK_EQUAL( 0, CtiDate::daysInMonthYear( 0, 1990) );

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
    //  will need to be re-added when special value constructors are handled as special values again
    //BOOST_CHECK_EQUAL( ct5.is_pos_infinity(), true );
    BOOST_CHECK_EQUAL( ct5.isValid(), false );

    string expectedResult = "11/02/2008";
    CtiDate result(2,11,2008);
    BOOST_CHECK_EQUAL(expectedResult, result.asStringUSFormat());
    expectedResult = "11/01/2008";
    result = CtiDate(1,11,2008);
    BOOST_CHECK_EQUAL(expectedResult, result.asStringUSFormat());
    expectedResult = "11/03/2008";
    result = CtiDate(3,11,2008);
    BOOST_CHECK_EQUAL(expectedResult, result.asStringUSFormat());
    expectedResult = "03/09/2008";
    result = CtiDate(9,3,2008);
    BOOST_CHECK_EQUAL(expectedResult, result.asStringUSFormat());
    expectedResult = "03/10/2008";
    result = CtiDate(10,3,2008);
    BOOST_CHECK_EQUAL(expectedResult, result.asStringUSFormat());
    expectedResult = "03/08/2008";
    result = CtiDate(8,3,2008);
    BOOST_CHECK_EQUAL(expectedResult, result.asStringUSFormat());

}


BOOST_AUTO_TEST_CASE(test_ctidate_operators)
{
    // check the operators
    CtiDate d = CtiDate();
    CtiDate d1 = d;
    BOOST_CHECK( d1 == d );
    BOOST_CHECK_EQUAL( d.asString(), d1.asString() );

    d1 = d - 10;
    BOOST_CHECK_EQUAL( d.daysFrom1970() - 10, d1.daysFrom1970() );

    d1 = d + 10;
    BOOST_CHECK_EQUAL( d.daysFrom1970() + 10, d1.daysFrom1970() );

    BOOST_CHECK_EQUAL( false, d1 <= d );
    BOOST_CHECK_EQUAL( false, d1 == d );
    BOOST_CHECK_EQUAL( true,  d1 >= d );
    BOOST_CHECK_EQUAL( true,  d1 != d );
    BOOST_CHECK_EQUAL( false, d1 <  d );
    BOOST_CHECK_EQUAL( true,  d1 >  d );
    BOOST_CHECK_EQUAL( true,  CtiDate(CtiDate::neg_infin)  < d );
    BOOST_CHECK_EQUAL( false, CtiDate(CtiDate::pos_infin)  < d );
    BOOST_CHECK_EQUAL( true,  CtiDate(CtiDate::not_a_date) > d ); //!!!

    // check the operators with special time values
    CtiDate ct(CtiDate::neg_infin);
    CtiDate ct1 = ct + 4;
    BOOST_CHECK_EQUAL( true,  ct1 <= ct );
    BOOST_CHECK_EQUAL( true,  ct1 == ct );
    BOOST_CHECK_EQUAL( true,  ct1 >= ct );
    BOOST_CHECK_EQUAL( false, ct1 != ct );
    BOOST_CHECK_EQUAL( false, ct1 < ct );  //!!!!!!
    BOOST_CHECK_EQUAL( false, ct1 > ct );  //!!!!!! basically, this means ct1==ct

    BOOST_CHECK_EQUAL( false, ct > d );
    BOOST_CHECK_EQUAL( true,  ct < d );

    ct= CtiDate(CtiDate::not_a_date);
    ct1 = ct - 4;
    BOOST_CHECK_EQUAL( true,  ct1 <= ct );
    BOOST_CHECK_EQUAL( true,  ct1 == ct );
    BOOST_CHECK_EQUAL( true,  ct1 >= ct );
    BOOST_CHECK_EQUAL( false, ct1 != ct );
    BOOST_CHECK_EQUAL( false, ct1 < ct );
    BOOST_CHECK_EQUAL( false, ct1 > ct );  //!!!!!! this means ct == ct1

    BOOST_CHECK_EQUAL( true,  ct > d );
    BOOST_CHECK_EQUAL( false, ct < d );  // means the not_a_date greater than current date, and any date!!!


}

BOOST_AUTO_TEST_CASE(test_ctidate_daysfrom1970)
{
    CtiDate zeroTest(1, 1, 1970);
    CtiDate oneTest (2, 1, 1970);
    CtiDate yearTest(1, 1, 1971);
    CtiDate twoYearTest  (1, 1, 1972);
    CtiDate threeYearTest(1, 1, 1973);
    CtiDate manyYearTest (1, 1, 2001); //2000 is a tricky year, and is a leap year. 8 leap years since 1970...

    BOOST_CHECK_EQUAL( 0, zeroTest.daysFrom1970() );
    BOOST_CHECK_EQUAL( 1, oneTest.daysFrom1970() );
    BOOST_CHECK_EQUAL( 365, yearTest.daysFrom1970() );
    BOOST_CHECK_EQUAL( 730, twoYearTest.daysFrom1970() );
    BOOST_CHECK_EQUAL( 1096, threeYearTest.daysFrom1970() );//leap year included!
    BOOST_CHECK_EQUAL( (2001-1970)*365 + 8, manyYearTest.daysFrom1970() );//leap year included!

    CtiDate dayBeforeTest(10, 3, 2007);
    CtiDate dayOfTest    (11, 3, 2007);
    CtiDate dayAfterTest (12, 3, 2007);
    CtiDate endDayBeforeTest(3, 11, 2007);
    CtiDate endDayOfTest    (4, 11, 2007);
    CtiDate endDayAfterTest (5, 11, 2007);

    BOOST_CHECK_EQUAL( dayBeforeTest.daysFrom1970() + 1, dayOfTest.daysFrom1970() );
    BOOST_CHECK_EQUAL( dayBeforeTest.daysFrom1970() + 2, dayAfterTest.daysFrom1970() );
    BOOST_CHECK_EQUAL( endDayBeforeTest.daysFrom1970() + 1, endDayOfTest.daysFrom1970() );
    BOOST_CHECK_EQUAL( endDayBeforeTest.daysFrom1970() + 2, endDayAfterTest.daysFrom1970() );
}


