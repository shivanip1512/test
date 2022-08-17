#include <boost/test/unit_test.hpp>

#include "ctidate.h"
#include "ctitime.h"

#include "boost_test_helpers.h"

using std::string;
using boost::unit_test_framework::test_suite;

BOOST_AUTO_TEST_SUITE( test_ctidate )

BOOST_AUTO_TEST_CASE(test_ctidate_methods)
{
    //static methods
    BOOST_CHECK_EQUAL( 0, CtiDate::daysInMonthYear(13, 1990) );
    BOOST_CHECK_EQUAL( 0, CtiDate::daysInMonthYear( 0, 1990) );

    // check the special time values
    BOOST_CHECK_EQUAL( "-infinity",  CtiDate(CtiDate::neg_infin).asString());

    // cannot call year() day(), etc. on neg_infin
    try
    {
        CtiDate(CtiDate::neg_infin).year();
        BOOST_FAIL("neg_infin year() did not throw");
    }
    catch( ... )
    {

    }

    try
    {
        CtiDate(CtiDate::neg_infin).day();
        BOOST_FAIL("neg_infin day() did not throw");
    }
    catch( ... )
    {
    }

    CtiDate ct1 = CtiDate(CtiDate::not_a_date);

    BOOST_CHECK_EQUAL( "not-a-date-time", ct1.asString());

    //  cannot call year() day(), etc. on not_a_date
    try
    {
        ct1.year();
        BOOST_FAIL("not_a_date year() did not throw");
    }
    catch( ... )
    {
    }

    try
    {
        ct1.day();
        BOOST_FAIL("not_a_date day() did not throw");
    }
    catch( ... )
    {
    }


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

BOOST_AUTO_TEST_CASE(test_ctidate_now)
{
    Cti::Test::Override_CtiDate_Now override(CtiDate(14, 7, 2014));

    BOOST_CHECK_EQUAL(CtiDate::now(), CtiDate(14, 7, 2014));
}


BOOST_AUTO_TEST_SUITE_END()
