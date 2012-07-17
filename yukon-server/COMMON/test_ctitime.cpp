#include <boost/test/unit_test.hpp>

#include "ctitime.h"
#include "ctidate.h"
#include <rw/rwtime.h>
#include <boost/date_time/posix_time/posix_time.hpp>

using namespace boost::gregorian;
using namespace boost::posix_time;

BOOST_AUTO_TEST_SUITE( test_ctitime )

/*
BOOST_AUTO_TEST_CASE(test_ptime)
{
    ptime pt = from_time_t((time_t)0);
    std::cout << to_simple_string(pt) << std::endl;
}*/

/* I dont think this belongs in the unit test - Jess*/
/*BOOST_AUTO_TEST_CASE(test_rwtime_methods)
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



BOOST_AUTO_TEST_CASE(test_ctitime_methods)
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
    struct tm rw_extracted_tm;
    rw_time.extract(&rw_extracted_tm);
    struct tm extracted_tm;
    d.extract(&extracted_tm);
    BOOST_CHECK_EQUAL( extracted_tm.tm_mday, rw_extracted_tm.tm_mday );
    BOOST_CHECK_EQUAL( extracted_tm.tm_mon,  rw_extracted_tm.tm_mon );
    BOOST_CHECK_EQUAL( extracted_tm.tm_year, rw_extracted_tm.tm_year );
    BOOST_CHECK_EQUAL( extracted_tm.tm_hour, rw_extracted_tm.tm_hour );

    //check copy constructor
    CtiTime ct5(d);
    BOOST_CHECK_EQUAL( d.second(), ct5.second() );
    // using the ctime struct to construct CtiTime, check its correctness
    CtiTime d2(&rw_extracted_tm);
    RWTime rw2(&rw_extracted_tm);
    BOOST_CHECK_EQUAL( d2.second(), rw2.second() );

    ct5 = d2;

    //static methods
    BOOST_CHECK_EQUAL( RWTime::now().minute(), CtiTime::now().minute() );

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

BOOST_AUTO_TEST_CASE(test_ctitime_specials)
{

    //check boost special values
    /*std::cout << endl;*/
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
    BOOST_CHECK_EQUAL( CtiTime(CtiTime::neg_infin).isValid(), false );
    BOOST_CHECK_EQUAL( CtiTime(CtiTime::pos_infin).isValid(), false );
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
    BOOST_CHECK_EQUAL( ct6.isValid(), false );
}


BOOST_AUTO_TEST_CASE(test_ctitime_operators)
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


BOOST_AUTO_TEST_CASE(test_ctitime_DST)
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

    // check the time duration calculation acrossing the DST boundary
    BOOST_CHECK_EQUAL( 2 * timeduration, cta.seconds() - ctb.seconds() );
    CtiTime ctt(cta - 6*60*60 + 30);
    CtiTime ctt1(ct.date(), 1, 0, 0);
    //BOOST_CHECK_EQUAL( ctt1.asString(), ctt.asString() ); This is a legitimate test!
    ctt = ctb + 6*60*60;
    ctt1 = CtiTime(ct.date(), 4, 0, 0);
    BOOST_CHECK_EQUAL( ctt1.asString(), ctt.asString() );

    // check the ambiguous time points

    timeduration = 60*60; // timeduration is one hour
    ct = CtiTime::endDST(2005);
    ct = ct - timeduration; // first 1am
    ctt = ct - 30*60; // 0:30am
    ctt1 = ctt - 90*60; // 23:00pm

    BOOST_CHECK_EQUAL( ctt1.seconds() + 2*timeduration, ct.seconds() );

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


struct time_parts
{
    int year, month, day, hour, minute, second, expected_offset;
};


unsigned long mkGmtSeconds(const time_parts &tp)
{
    return CtiTime(CtiDate(tp.day, tp.month, tp.year), tp.hour, tp.minute, tp.second).seconds();
}

unsigned long mkLocalSeconds(const time_parts &tp)
{
    //  local seconds = GMT seconds + TZ offset
    return mkGmtSeconds(tp) + tp.expected_offset;
}

BOOST_AUTO_TEST_CASE(test_ctitime_fromLocalSeconds)
{
    _TIME_ZONE_INFORMATION tzinfo;
    GetTimeZoneInformation(&tzinfo);

    //  Windows defines the bias as the difference from local time to GMT;
    //    since we want GMT to local, we negate the values
    const int standard_offset = -(tzinfo.Bias + tzinfo.StandardBias) * 60;
    const int daylight_offset = -(tzinfo.Bias + tzinfo.DaylightBias) * 60;

    //  =====  2009 test cases  =====
    time_parts tc2009[15] =
    {
        { 2009,  1,  1,  0, 00, 00, standard_offset },  //  known ST date

        { 2009,  3,  7,  0, 00, 00, standard_offset },  //  standard -> daylight-saving-time transition
        { 2009,  3,  8,  1, 59, 59, standard_offset },  //

        { 2009,  3,  8,  2, 00, 00, standard_offset },  //  nonexistent hour;  this test is here to pin the behavior
        { 2009,  3,  8,  2, 59, 59, standard_offset },  //

        { 2009,  3,  8,  3, 00, 00, daylight_offset },  //
        { 2009,  3,  9,  0, 00, 00, daylight_offset },  //

        { 2009,  7,  1,  0, 00, 00, daylight_offset },  //  known DST date

        { 2009, 10, 31,  0, 00, 00, daylight_offset },  //  daylight-saving-time -> standard transition
        { 2009, 11,  1,  0, 59, 59, daylight_offset },  //

        { 2009, 11,  1,  1, 00, 00, standard_offset },  //  ambiguous hour;  this test is here to pin the behavior
        { 2009, 11,  1,  1, 59, 59, standard_offset },  //

        { 2009, 11,  1,  2, 00, 00, standard_offset },  //
        { 2009, 11,  2,  0, 00, 00, standard_offset },  //

        { 2009, 12, 31,  0, 00, 00, standard_offset }   //  known ST date
    };

    BOOST_CHECK_EQUAL(mkGmtSeconds(tc2009[ 0]), CtiTime::fromLocalSeconds(mkLocalSeconds(tc2009[ 0])).seconds());
    BOOST_CHECK_EQUAL(mkGmtSeconds(tc2009[ 1]), CtiTime::fromLocalSeconds(mkLocalSeconds(tc2009[ 1])).seconds());
    BOOST_CHECK_EQUAL(mkGmtSeconds(tc2009[ 2]), CtiTime::fromLocalSeconds(mkLocalSeconds(tc2009[ 2])).seconds());
    BOOST_CHECK_EQUAL(mkGmtSeconds(tc2009[ 3]), CtiTime::fromLocalSeconds(mkLocalSeconds(tc2009[ 3])).seconds());
    BOOST_CHECK_EQUAL(mkGmtSeconds(tc2009[ 4]), CtiTime::fromLocalSeconds(mkLocalSeconds(tc2009[ 4])).seconds());
    BOOST_CHECK_EQUAL(mkGmtSeconds(tc2009[ 5]), CtiTime::fromLocalSeconds(mkLocalSeconds(tc2009[ 5])).seconds());
    BOOST_CHECK_EQUAL(mkGmtSeconds(tc2009[ 6]), CtiTime::fromLocalSeconds(mkLocalSeconds(tc2009[ 6])).seconds());
    BOOST_CHECK_EQUAL(mkGmtSeconds(tc2009[ 7]), CtiTime::fromLocalSeconds(mkLocalSeconds(tc2009[ 7])).seconds());
    BOOST_CHECK_EQUAL(mkGmtSeconds(tc2009[ 8]), CtiTime::fromLocalSeconds(mkLocalSeconds(tc2009[ 8])).seconds());
    BOOST_CHECK_EQUAL(mkGmtSeconds(tc2009[ 9]), CtiTime::fromLocalSeconds(mkLocalSeconds(tc2009[ 9])).seconds());
    BOOST_CHECK_EQUAL(mkGmtSeconds(tc2009[10]), CtiTime::fromLocalSeconds(mkLocalSeconds(tc2009[10])).seconds());
    BOOST_CHECK_EQUAL(mkGmtSeconds(tc2009[11]), CtiTime::fromLocalSeconds(mkLocalSeconds(tc2009[11])).seconds());
    BOOST_CHECK_EQUAL(mkGmtSeconds(tc2009[12]), CtiTime::fromLocalSeconds(mkLocalSeconds(tc2009[12])).seconds());
    BOOST_CHECK_EQUAL(mkGmtSeconds(tc2009[13]), CtiTime::fromLocalSeconds(mkLocalSeconds(tc2009[13])).seconds());
    BOOST_CHECK_EQUAL(mkGmtSeconds(tc2009[14]), CtiTime::fromLocalSeconds(mkLocalSeconds(tc2009[14])).seconds());
}


BOOST_AUTO_TEST_CASE(test_ctitime_GMT_conversions)
{
    // Wed Jan 20th, 2010 at 12:00:00 CST == Wed Jan 20th, 2010 at 18:00:00 GMT

    CtiTime theTime( CtiDate( 20, 1, 2010),  12,  0,  0  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 18               , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Wed Jan 20th, 2010 at 22:00:00 CST == Thu Jan 21st, 2010 at 04:00:00 GMT

    theTime = CtiTime(CtiDate( 20, 1, 2010),  22,  0,  0  );

    BOOST_CHECK_EQUAL( 1                , theTime.dateGMT().month()         );
    BOOST_CHECK_EQUAL( 21               , theTime.dateGMT().dayOfMonth()    );
    BOOST_CHECK_EQUAL( 2010             , theTime.dateGMT().year()          );
    BOOST_CHECK_EQUAL( 4                , theTime.dateGMT().weekDay()       );      // 4 == Thursday
    BOOST_CHECK_EQUAL( 4                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Sun Jan 31st, 2010 at 20:00:00 CST == Mon Feb 1st, 2010 at 02:00:00 GMT

    theTime = CtiTime(CtiDate( 31, 1, 2010),  20,  0,  0  );

    BOOST_CHECK_EQUAL( 2                , theTime.dateGMT().month()         );
    BOOST_CHECK_EQUAL( 1                , theTime.dateGMT().dayOfMonth()    );
    BOOST_CHECK_EQUAL( 2010             , theTime.dateGMT().year()          );
    BOOST_CHECK_EQUAL( 1                , theTime.dateGMT().weekDay()       );      // 1 == Monday
    BOOST_CHECK_EQUAL( 2                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Sun Mar 14th, 2010 at 1:00:00 CST == Sun Mar 14th, 2010 at 7:00:00 GMT

    theTime = CtiTime(CtiDate( 14, 3, 2010),  1,  0,  0  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 7                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Sun Mar 14th, 2010 at 1:59:59 CST == Sun Mar 14th, 2010 at 7:59:59 GMT

    theTime = CtiTime(CtiDate( 14, 3, 2010),  1,  59,  59  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 7                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 59               , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 59               , theTime.secondGMT()   );


    // Sun Mar 14th, 2010 at 2:00:00 CST == Sun Mar 14th, 2010 at 7:00:00 GMT?!?!
    //  Just for documentation and completeness and fail.

    theTime = CtiTime(CtiDate( 14, 3, 2010),  2,   0,   0  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 7                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Sun Mar 14th, 2010 at 3:00:00 CDT == Sun Mar 14th, 2010 at 8:00:00 GMT

    theTime = CtiTime(CtiDate( 14, 3, 2010),  3,  0,  0  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 8                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Sun Mar 14th, 2010 at 4:00:00 CDT == Sun Mar 14th, 2010 at 9:00:00 GMT

    theTime = CtiTime(CtiDate( 14, 3, 2010),  4,  0,  0  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 9                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Wed July 14th, 2010 at 12:00:00 CDT == Wed July 14th, 2010 at 17:00:00 GMT

    theTime = CtiTime(CtiDate( 14, 7, 2010),  12,  0,  0  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 17               , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Sun Nov 7th, 2010 at 00:59:59 CDT == Sun Nov 7th, 2010 at 5:59:59 GMT

    theTime = CtiTime(CtiDate( 7, 11, 2010),  0, 59, 59  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 5                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 59               , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 59               , theTime.secondGMT()   );

    theTime += 1;  //  Move it to 1:00 CDT (the first 1:00)

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 6                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );

    // Sun Nov 7th, 2010 at 1:00:00 CST == Sun Nov 7th, 2010 at 7:00:00 GMT

    theTime = CtiTime(CtiDate( 7, 11, 2010),  1,  0,  0  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 7                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Sun Nov 7th, 2010 at 4:00:00 CDT == Sun Nov 7th, 2010 at 10:00:00 GMT

    theTime = CtiTime(CtiDate( 7, 11, 2010),  4,  0,  0  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 10               , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Fri Dec 31st, 2010 at 22:00:00 CDT == Sat Jan 1st, 2011 at 4:00:00 GMT

    theTime = CtiTime(CtiDate( 31, 12, 2010),  22,  0,  0  );

    BOOST_CHECK_EQUAL( 1                , theTime.dateGMT().month()         );
    BOOST_CHECK_EQUAL( 1                , theTime.dateGMT().dayOfMonth()    );
    BOOST_CHECK_EQUAL( 2011             , theTime.dateGMT().year()          );
    BOOST_CHECK_EQUAL( 6                , theTime.dateGMT().weekDay()       );      // 6 == Saturday
    BOOST_CHECK_EQUAL( 4                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );
}


BOOST_AUTO_TEST_CASE(test_ctitime_fall_dst_creation)
{
    {
        CtiTime t0(CtiDate(7, 11, 2010), 0, 59, 59);
        CtiTime t1(CtiDate(7, 11, 2010), 1,  0,  0);

        //  verify that the time is always created as the later of the two 1 AMs on the DST change day
        BOOST_CHECK( t0.isDST() );
        BOOST_CHECK( ! t1.isDST() );
    }
}

BOOST_AUTO_TEST_CASE(test_ctitime_asString_standard_time)
{
    CtiDate d = CtiDate(1, 1, 2009);

    CtiTime t = CtiTime(d, 0, 0, 0);

    BOOST_CHECK_EQUAL(t.asString(), "01/01/2009 00:00:00");

    struct test_case
    {
        CtiTime::DisplayOffset offset;
        CtiTime::DisplayTimezone timezone;
        std::string expected;
    }
    const test_cases[] = {
        { CtiTime::Gmt,        CtiTime::OmitTimezone,    "01/01/2009 06:00:00" },
        { CtiTime::Gmt,        CtiTime::IncludeTimezone, "01/01/2009 06:00:00 (UTC+0:00 GMT)" },
        { CtiTime::Local,      CtiTime::OmitTimezone,    "01/01/2009 00:00:00" },
        { CtiTime::Local,      CtiTime::IncludeTimezone, "01/01/2009 00:00:00 (UTC-6:00 CST)" },
        { CtiTime::LocalNoDst, CtiTime::OmitTimezone,    "01/01/2009 00:00:00" },
        { CtiTime::LocalNoDst, CtiTime::IncludeTimezone, "01/01/2009 00:00:00 (UTC-6:00 CST)" } };

    std::vector<std::string> results, expected;

    for each( const test_case &tc in test_cases )
    {
        results.push_back(t.asString(tc.offset, tc.timezone));
        expected.push_back(tc.expected);
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results.begin(), results.end());
}


BOOST_AUTO_TEST_CASE(test_ctitime_asString_daylight_saving_time)
{
    CtiDate d = CtiDate(1, 6, 2009);

    CtiTime t = CtiTime(d, 0, 0, 0);

    BOOST_CHECK_EQUAL(t.asString(), "06/01/2009 00:00:00");

    struct test_case
    {
        CtiTime::DisplayOffset offset;
        CtiTime::DisplayTimezone timezone;
        std::string expected;
    }
    const test_cases[] = {
        { CtiTime::Gmt,        CtiTime::OmitTimezone,    "06/01/2009 05:00:00" },
        { CtiTime::Gmt,        CtiTime::IncludeTimezone, "06/01/2009 05:00:00 (UTC+0:00 GMT)" },
        { CtiTime::Local,      CtiTime::OmitTimezone,    "06/01/2009 00:00:00" },
        { CtiTime::Local,      CtiTime::IncludeTimezone, "06/01/2009 00:00:00 (UTC-5:00 CDT)" },
        { CtiTime::LocalNoDst, CtiTime::OmitTimezone,    "05/31/2009 23:00:00" },
        { CtiTime::LocalNoDst, CtiTime::IncludeTimezone, "05/31/2009 23:00:00 (UTC-6:00 CST)" } };

    std::vector<std::string> results, expected;

    for each( const test_case &tc in test_cases )
    {
        results.push_back(t.asString(tc.offset, tc.timezone));
        expected.push_back(tc.expected);
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results.begin(), results.end());
}


BOOST_AUTO_TEST_SUITE_END()
