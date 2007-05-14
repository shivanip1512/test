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
#define BOOST_AUTO_TEST_MAIN "Test CtiTime"

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



#include "ctitime.h"
#include "ctidate.h"

using boost::unit_test_framework::test_suite;
using namespace boost::gregorian;
using namespace boost::posix_time;
using namespace std;

/*
BOOST_AUTO_UNIT_TEST(test_ptime)
{
    ptime pt = from_time_t((time_t)0);
    std::cout << to_simple_string(pt) << std::endl;
}*/

/* I dont think this belongs in the unit test - Jess*/
/*BOOST_AUTO_UNIT_TEST(test_rwtime_methods)
{
    int timeduration = 60*60;
    RWTime rt1;
    RWTime rt2((unsigned long)0);
    RWTime rt3(RWTime::beginDST(2005));
    RWTime rt4 = rt3 + timeduration;
    RWTime rt5 = rt3 - timeduration;
    cout << rt2.asString() << endl;
    //cout << rt2.hour() << rt2.minute() << rt2.second() << endl;
    cout << rt3.asString() << endl;
    cout << rt4.asString() << endl;
    cout << rt5.asString() << endl << endl;
    rt3 = RWTime::endDST(2005);
    rt4 = rt3 - timeduration;
    rt5 = rt3 - 2*timeduration;
    cout << rt3.asString() << endl;
    cout << rt4.asString() << endl;
    cout << rt5.asString() << endl << endl; 

    rt3 = RWTime(RWDate(3,4,2005), 2, 30, 0);
    rt4 = rt3 + timeduration;
    rt5 = rt3 - 2*timeduration;
    cout << rt3.asString() << endl;
    cout << rt4.asString() << endl;
    cout << rt5.asString() << endl << endl; 


    rt3 = RWTime(RWDate(30,10,2005), 1, 30, 0);
    rt4 = rt3 + timeduration;
    rt5 = rt3 - 2*timeduration;
    cout << rt3.asString() << endl;
    cout << rt4.asString() << endl;
    cout << rt5.asString() << endl << endl; 
    
}*/



BOOST_AUTO_UNIT_TEST(test_ctitime_methods)
{

    // check the functionality match between CtiTime and RWTime

    RWTime rw_time = RWTime();
    CtiTime d = CtiTime(rw_time.hour(), rw_time.minute(), rw_time.second());
    //std::cout << "Current time: " << d.asString() << std::endl;
    BOOST_CHECK_EQUAL( rw_time.hour(), d.hour() );
    BOOST_CHECK_EQUAL( rw_time.minute(), d.minute() );
    BOOST_CHECK_EQUAL( rw_time.second(), d.second() );
    BOOST_CHECK_EQUAL( rw_time.minuteGMT(), d.minuteGMT() );
    BOOST_CHECK_EQUAL( rw_time.hourGMT(), d.hourGMT() );
    BOOST_CHECK_EQUAL( rw_time.isDST(), d.isDST() );

    d.addDays(1);
    rw_time = RWTime(d.toRwSeconds());
    //std::cout << "Current time: " << d.asString() << std::endl;
    BOOST_CHECK_EQUAL( rw_time.hour(), d.hour() );
    BOOST_CHECK_EQUAL( rw_time.minute(), d.minute() );
    BOOST_CHECK_EQUAL( rw_time.second(), d.second() );
    BOOST_CHECK_EQUAL( rw_time.minuteGMT(), d.minuteGMT() );
    BOOST_CHECK_EQUAL( rw_time.hourGMT(), d.hourGMT() );
    BOOST_CHECK_EQUAL( rw_time.isDST(), d.isDST() );

    // check the CtiTime.extract() method, to see if the extracted ctime is correct
    struct tm ctime;
    rw_time.extract(&ctime);
    struct tm ctime1;
    d.extract(&ctime1);
    BOOST_CHECK_EQUAL( ctime1.tm_mday, ctime.tm_mday );
    BOOST_CHECK_EQUAL( ctime1.tm_mon, ctime.tm_mon );
    BOOST_CHECK_EQUAL( ctime1.tm_year, ctime.tm_year );
    BOOST_CHECK_EQUAL( ctime1.tm_hour, ctime.tm_hour );

    //check copy constructor
    CtiTime ct5(d);
    BOOST_CHECK_EQUAL( d.second(), ct5.second() );
    // using the ctime struct to construct CtiTime, check its correctness
    CtiTime d2(&ctime);
    RWTime rw2(&ctime);
    BOOST_CHECK_EQUAL( d2.second(), rw2.second() );

    ct5 = d2;
    //std::cout << std::endl << "Time, " << ct5.asString() << ", to UTC time: " << d2.toUTCtime().asString() << std::endl;

    //static methods
    BOOST_CHECK_EQUAL( RWTime::now().minute(), CtiTime::now().minute() );
    /*std::cout << std::endl << "DST for 2005 in your local is: " << std::endl << CtiTime::beginDST(2005).asString();
    std::cout << " to " << CtiTime::endDST(2005).asString() << std::endl;*/

    // check the constructor CtiTime(seconds), and the special case CtiTime(0)
    long timep = 5*24*60*60 + 8*60*60;
    CtiTime d1(timep);
    RWTime rw1(timep);
    CtiTime d3((unsigned long)0);
    RWTime rw3((unsigned long)0);

    BOOST_CHECK_EQUAL( rw1.hour(), d1.hour() );
    BOOST_CHECK_EQUAL( rw1.minute(), d1.minute() );
    
    BOOST_CHECK_EQUAL( rw1.second(), d1.second() );
    
    BOOST_CHECK_EQUAL( rw1.minuteGMT(), d1.minuteGMT() );
    
    BOOST_CHECK_EQUAL( rw1.hourGMT(), d1.hourGMT() );
    BOOST_CHECK_EQUAL( timep, d1.seconds());
    BOOST_CHECK_EQUAL( false, d1.isDST() );
    BOOST_CHECK_EQUAL( rw3.isValid(), d3.isValid() );
}

BOOST_AUTO_UNIT_TEST(test_ctitime_specials)
{

    //check boost special values
    std::cout << endl;
    ptime x(date(boost::date_time::neg_infin), boost::date_time::neg_infin);
    /*std::cout << "neg_infin ptime hour: " << x.time_of_day().hours() << std::endl;
    std::cout << "neg_infin ptime seconds: " << x.time_of_day().seconds() << std::endl;
    std::cout << endl;*/

    ptime y(date(boost::date_time::not_a_date_time), boost::date_time::not_a_date_time);
    /*std::cout << "not_a_date_time ptime hour: " << y.time_of_day().hours() << std::endl;
    std::cout << "not_a_date_time ptime seconds: " << y.time_of_day().seconds() << std::endl;
    std::cout << endl;*/


    // check the special time values
    CtiTime ct1 = CtiTime(CtiTime::neg_infin);
    BOOST_CHECK_EQUAL( ct1.is_special(), true );

   /* std::cout << "neg_infin time as string: " << ct1.asString() << std::endl;
    std::cout << "neg_infin time hour: " << ct1.hour() << std::endl;
    std::cout << "neg_infin time seconds: " << ct1.second() << std::endl;
    std::cout << endl;*/

    ct1 = CtiTime(CtiTime::not_a_time);

    BOOST_CHECK_EQUAL( ct1.is_special(), true );
    /*std::cout << "not_a_time as string: " << ct1.asString() << std::endl;
    std::cout << "not_a_time hour: " << ct1.hour() << std::endl;
    std::cout << "not_a_time seconds: " << ct1.second() << std::endl;
    std::cout << endl;*/

    BOOST_CHECK_EQUAL( CtiTime((unsigned long)0).is_special(), true );
    BOOST_CHECK_EQUAL( CtiTime(CtiTime::neg_infin).isValid(), true );
    BOOST_CHECK_EQUAL( CtiTime(CtiTime::pos_infin).isValid(), true );
    BOOST_CHECK_EQUAL( CtiTime((unsigned long)0).isValid(), false );
    BOOST_CHECK_EQUAL( CtiTime(ct1).isValid(), false );
    

    // check creating a CtiTime using a CtiDate
    CtiDate cd;
    
    CtiTime ct3(cd);
    //std::cout << "here" << std::endl;
    BOOST_CHECK_EQUAL( cd.asString(), ct3.date().asString() );

    
    // check creating a CtiTime from a special CtiDate
    
    CtiDate cd1(CtiDate::not_a_date);
    CtiDate cd2(CtiDate::pos_infin);
    
    CtiTime ct4(cd1);
    CtiTime ct6(cd2);

    BOOST_CHECK_EQUAL( ct4.isValid(), false );
    BOOST_CHECK_EQUAL( ct6.is_pos_infinity(), true );
    BOOST_CHECK_EQUAL( ct6.isValid(), true );
}


BOOST_AUTO_UNIT_TEST(test_ctitime_operators)
{
    // check the == operator
    CtiTime d;
    CtiTime d1 = d;
    BOOST_CHECK_EQUAL( true, d1 == d );
    BOOST_CHECK_EQUAL( d.asString(), d1.asString() );

    // check the -, +, <=, >=, >, < !=
    d1 = d - 100;
    BOOST_CHECK_EQUAL( d.seconds() - 100, d1.seconds() );
    d1 = d + 100;
    BOOST_CHECK_EQUAL( d.seconds() + 100, d1.seconds() );
    BOOST_CHECK_EQUAL( false, d1 <= d );
    BOOST_CHECK_EQUAL( false, d1 == d );
    BOOST_CHECK_EQUAL( true, d1 >= d );
    BOOST_CHECK_EQUAL( true, d1 != d );
    BOOST_CHECK_EQUAL( false, d1 < d );
    BOOST_CHECK_EQUAL( true, d1 > d );

    // check the operators with special time values

    CtiTime ct(CtiTime::neg_infin);
    CtiTime ct1 = ct + 4;
    BOOST_CHECK_EQUAL( true, ct1 <= ct );
    BOOST_CHECK_EQUAL( true, ct1 == ct );
    BOOST_CHECK_EQUAL( true, ct1 >= ct );
    BOOST_CHECK_EQUAL( false, ct1 != ct );
    BOOST_CHECK_EQUAL( false, ct1 < ct );
    BOOST_CHECK_EQUAL( false, ct1 > ct ); // means ct==ct1

    BOOST_CHECK_EQUAL( false, ct1 > d );
    BOOST_CHECK_EQUAL( true, ct1 < d );

    ct= CtiTime(CtiTime::not_a_time);
    ct1 = ct - 4;
    BOOST_CHECK_EQUAL( true, ct1 <= ct );
    BOOST_CHECK_EQUAL( true, ct1 == ct );
    BOOST_CHECK_EQUAL( true, ct1 >= ct );
    BOOST_CHECK_EQUAL( false, ct1 != ct );
    BOOST_CHECK_EQUAL( false, ct1 < ct );
    BOOST_CHECK_EQUAL( false, ct1 > ct );  // means ct==ct1

    BOOST_CHECK_EQUAL( true, ct1 < d );
    BOOST_CHECK_EQUAL( false, ct1 > d );  // means not_a_time is less than current time, any time
}


BOOST_AUTO_UNIT_TEST(test_ctitime_DST)
{
    //explicitly create a time 1:00, then add 1 hour to it, which results the boundary of DST
    CtiTime ict(CtiTime::beginDST(2005).date(), 1, 0 ,0 );
    //std::cout << std::endl << ict.asString() << std::endl;
    ict += 1*60*60;
    //std::cout << ict.asString() << std::endl;


    CtiTime ct(CtiTime::beginDST(2005));

    int timeduration = 5*60*60;

    CtiTime ctb;
    ctb = ct - timeduration;
    CtiTime cta;
    cta = ct + timeduration;
    
    /*std::cout << std::endl;
    std::cout << "DST begin time:\t\t\t" << ct.asString() << std::endl;
    std::cout << "A not-exsit time (2:30am):\t" << CtiTime(ct.date(), 2, 30, 0).asString() << std::endl;
    std::cout << "DST begin time - 5hrs:\t\t" << ctb.asString() << std::endl;
    std::cout << "DST begin time + 5hrs:\t\t" << cta.asString() << std::endl;*/

    // check the time duration calculation acrossing the DST boundary
    BOOST_CHECK_EQUAL( 2 * timeduration, cta.seconds() - ctb.seconds() );
    CtiTime ctt(cta - 6*60*60 + 30);
    CtiTime ctt1(ct.date(), 1, 0, 0);
    //BOOST_CHECK_EQUAL( ctt1.asString(), ctt.asString() ); This is a legitimate test!
    ctt = ctb + 6*60*60;
    ctt1 = CtiTime(ct.date(), 4, 0, 0);
    BOOST_CHECK_EQUAL( ctt1.asString(), ctt.asString() );
    std::cout << "There are problems with DST in the current code. Add testing when these are gone." << std::endl;

    // check the ambiguous time points
    
    timeduration = 60*60; // timeduration is one hour
    ct = CtiTime::endDST(2005); 
    ct = ct - timeduration; // first 1am
    ctt = ct - 30*60; // 0:30am
    ctt1 = ctt - 90*60; // 23:00pm

    /*std::cout << std::endl << "DST end time - 1hr: \t\t" << ct.asString() << std::endl;
    std::cout << "DST end time - 1.5hr: \t\t" << ctt.asString() << std::endl;
    std::cout << "DST end time - 3hr: \t\t" << ctt1.asString() << std::endl;*/
    
    BOOST_CHECK_EQUAL( ctt1.seconds() + 2*timeduration, ct.seconds() );
    //asdf
    ct = ctt; // 0:30am
    ctt = ct + 6*60*60; //5.30am
    ctt1 = ct + 5*60*60; //4.30am
    //std::cout << "DST end time + 3.5hr: \t\t" << ctt1.asString() << std::endl;
    BOOST_CHECK_EQUAL( ctt1.seconds() + timeduration, ctt.seconds() );

    ctt = CtiTime(ct.date() - 1, 23, 30, 0);
    ctt1 = CtiTime(ct.date(), 1, 30, 0); // this is the second one

    //BOOST_CHECK_EQUAL( ct.seconds() + 2*timeduration, ctt1.seconds() );
    //BOOST_CHECK_EQUAL( ctt.seconds() + 3*timeduration, ctt1.seconds() );

    ct = CtiTime(ct.date(), 2, 30 ,0);
    //BOOST_CHECK_EQUAL( ct.seconds() - 4*timeduration, ctt.seconds() );



}
/*
BOOST_AUTO_UNIT_TEST(test_locale)
{
    using namespace boost::gregorian;
    using namespace boost::posix_time;
    std::stringstream ss;
    CtiDate d1(5,1,2002);
    CtiTime t1(d1, 12, 10, 5);

    std::locale global = std::locale::classic();
    ss.imbue(global); 
    ss << t1.asString();
    std::cout << "using default locale: " << ss.str() << std::endl;
}*/

//test_suite*
//init_unit_test_suite( int /*argc*/, char* /*argv*/[] ) {
//    test_suite* test= BOOST_TEST_SUITE( "Test CtiTime" );
//    test->add( BOOST_TEST_CASE( test_rwtime_methods )) ;
//    test->add( BOOST_TEST_CASE( test_ctitime_methods )) ;
//    test->add( BOOST_TEST_CASE( test_ctitime_operators ));
//    test->add( BOOST_TEST_CASE( test_ctitime_specials ));
//    test->add( BOOST_TEST_CASE( test_locale ) );
//    test->add( BOOST_TEST_CASE( test_ctitime_DST ) );
//    return test; 
//}


