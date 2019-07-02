#include <boost/test/unit_test.hpp>

#include "ctitime.h"
#include "ctidate.h"
#include <boost/assign/list_of.hpp>

#include "boost_test_helpers.h"

#include <windows.h>

using namespace boost::gregorian;

BOOST_AUTO_TEST_SUITE( test_ctitime )

BOOST_AUTO_TEST_CASE(test_ctitime_specials)
{
    {
        CtiTime t{ CtiTime::neg_infin };

        BOOST_CHECK_EQUAL(t.is_special(), true);
        BOOST_CHECK_EQUAL(t.isValid(), false);
        BOOST_CHECK_EQUAL(t.isDST(), false);
        BOOST_CHECK_EQUAL(t.is_neg_infinity(), true);

        t.addDays(1);

        BOOST_CHECK_EQUAL(t.is_neg_infinity(), true);

        t.addDays(-1);

        BOOST_CHECK_EQUAL(t.is_neg_infinity(), true);
    }

    {
        CtiTime t{ CtiTime::pos_infin };

        BOOST_CHECK_EQUAL(t.is_special(), true);
        BOOST_CHECK_EQUAL(t.isValid(), false);
        BOOST_CHECK_EQUAL(t.isDST(), false);
        BOOST_CHECK_EQUAL(t.is_pos_infinity(), true);

        t.addDays(1);

        BOOST_CHECK_EQUAL(t.is_pos_infinity(), true);

        t.addDays(-1);

        BOOST_CHECK_EQUAL(t.is_pos_infinity(), true);
    }

    {
        CtiTime t{ CtiTime::not_a_time };

        BOOST_CHECK_EQUAL(t.is_special(), true);
        BOOST_CHECK_EQUAL(t.isValid(), false);
        BOOST_CHECK_EQUAL(t.isDST(), false);
        BOOST_CHECK_EQUAL(t.seconds(), 0);

        t.addDays(1);

        BOOST_CHECK_EQUAL(t.seconds(), 0);

        t.addDays(-1);

        BOOST_CHECK_EQUAL(t.seconds(), 0);
    }

    {
        CtiTime t{ 0UL };

        BOOST_CHECK_EQUAL(t.is_special(), true);
        BOOST_CHECK_EQUAL(t.isValid(), false);
        BOOST_CHECK_EQUAL(t.isDST(), false);
        BOOST_CHECK_EQUAL(t.seconds(), 0);

        t.addDays(1);

        BOOST_CHECK_EQUAL(t.seconds(), 0);

        t.addDays(-1);

        BOOST_CHECK_EQUAL(t.seconds(), 0);
    }

    // check creating a CtiTime using a CtiDate
    {
        CtiDate d{ 25, 12, 2016 };

        CtiTime t{ d };

        BOOST_CHECK_EQUAL(d.asString(), t.date().asString());
    }

    // check creating special CtiTimes from special CtiDates
    {
        CtiDate d{ CtiDate::neg_infin };
        CtiTime t{ d };

        BOOST_CHECK_EQUAL(t.is_special(), true);
        BOOST_CHECK_EQUAL(t.isValid(), false);
        BOOST_CHECK_EQUAL(t.is_neg_infinity(), true);
    }

    {
        CtiDate d{ CtiDate::pos_infin };
        CtiTime t{ d };

        BOOST_CHECK_EQUAL(t.is_special(), true);
        BOOST_CHECK_EQUAL(t.isValid(), false);
        BOOST_CHECK_EQUAL(t.is_pos_infinity(), true);
    }

    {
        CtiDate d{ CtiDate::not_a_date };
        CtiTime t{ d };

        BOOST_CHECK_EQUAL(t.isValid(), false);
        BOOST_CHECK_EQUAL(t.is_special(), true);
        BOOST_CHECK_EQUAL(t.seconds(), 0);
    }
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

namespace { // anonymous namespace

/**
 * retrieve Time Zone registry key name
 */
int getTimeZoneOffset()
{
    HKEY hKey = 0;

    RegOpenKey(HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\TimeZoneInformation", &hKey);

    DWORD lpType = REG_SZ;
    TCHAR buffer[512]; // we expect ANSI
    DWORD bufferSize = sizeof(buffer);

    if( RegQueryValueEx(hKey, "TimeZoneKeyName", NULL, &lpType, (LPBYTE)buffer, &bufferSize) != ERROR_SUCCESS )
    {
        BOOST_FAIL("No time zone key");
    }

    std::string result(buffer, bufferSize);

    result.erase(result.find_first_of('\0'));

    if( result == "Central Standard Time" )
        return -6;
    if( result == "Eastern Standard Time" )
        return -5;
    if( result == "Pacific Standard Time" )
        return -8;

    BOOST_FAIL("Unknown time zone " << result);

    return 0;
}

struct TimeParts
{
    int year, month, day, hour, minute, second, tz_offset;

    TimeParts( int year, int month, int day, int hour, int minute, int second, int tz_offset ) :
        year(year), month(month), day(day), hour(hour), minute(minute), second(second), tz_offset(tz_offset)
    {}
};

unsigned long mkGmtSeconds(const TimeParts &tp)
{
    tm tm_info = {};

    tm_info.tm_year  = tp.year - 1900;
    tm_info.tm_mon   = tp.month - 1;
    tm_info.tm_mday  = tp.day;
    tm_info.tm_hour  = tp.hour;
    tm_info.tm_min   = tp.minute;
    tm_info.tm_sec   = tp.second;
    tm_info.tm_isdst = -1;

    return mktime( &tm_info );
}

unsigned long mkLocalSeconds(const TimeParts &tp)
{
    return mkGmtSeconds(tp) + (tp.tz_offset * 60);
}

} // anonymous namespace

BOOST_AUTO_TEST_CASE(test_ctitime_fromLocalSeconds)
{
    const long standard_offset = getTimeZoneOffset() * 60;
    const long daylight_offset = standard_offset + 60;

    std::vector<TimeParts> time_parts
    {
        { 2009,  1,  1,  0, 00, 00, standard_offset }, //  known ST date

        { 2009,  3,  7,  0, 00, 00, standard_offset }, //  standard -> daylight-saving-time transition
        { 2009,  3,  8,  1, 59, 59, standard_offset }, //

        { 2009,  3,  8,  2, 00, 00, standard_offset }, //  nonexistent hour;  this test is here to pin the behavior
        { 2009,  3,  8,  2, 59, 59, standard_offset }, //

        { 2009,  3,  8,  3, 00, 00, daylight_offset }, //
        { 2009,  3,  9,  0, 00, 00, daylight_offset }, //

        { 2009,  7,  1,  0, 00, 00, daylight_offset }, //  known DST date

        { 2009, 10, 31,  0, 00, 00, daylight_offset }, //  daylight-saving-time -> standard transition
        { 2009, 11,  1,  0, 59, 59, daylight_offset }, //

        { 2009, 11,  1,  1, 00, 00, standard_offset }, //  ambiguous hour;  this test is here to pin the behavior
        { 2009, 11,  1,  1, 59, 59, standard_offset }, //

        { 2009, 11,  1,  2, 00, 00, standard_offset }, //
        { 2009, 11,  2,  0, 00, 00, standard_offset }, //

        { 2009, 12, 31,  0, 00, 00, standard_offset }  //  known ST date
    };

    BOOST_REQUIRE( ! time_parts.empty() );

    for each(const TimeParts & tp in time_parts )
    {
        BOOST_CHECK_EQUAL(mkGmtSeconds(tp), CtiTime::fromLocalSeconds(mkLocalSeconds(tp)).seconds());
    }
}

BOOST_AUTO_TEST_CASE(test_ctitime_CentralTime_GMT_conversions)
{
    Cti::Test::set_to_central_timezone();

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


BOOST_AUTO_TEST_CASE(test_ctitime_EasternTime_GMT_conversions)
{
    Cti::Test::set_to_eastern_timezone();

    // Wed Jan 20th, 2010 at 12:00:00 EST == Wed Jan 20th, 2010 at 17:00:00 GMT

    CtiTime theTime( CtiDate( 20, 1, 2010),  12,  0,  0  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 17               , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Wed Jan 20th, 2010 at 22:00:00 EST == Thu Jan 21st, 2010 at 03:00:00 GMT

    theTime = CtiTime(CtiDate( 20, 1, 2010),  22,  0,  0  );

    BOOST_CHECK_EQUAL( 1                , theTime.dateGMT().month()         );
    BOOST_CHECK_EQUAL( 21               , theTime.dateGMT().dayOfMonth()    );
    BOOST_CHECK_EQUAL( 2010             , theTime.dateGMT().year()          );
    BOOST_CHECK_EQUAL( 4                , theTime.dateGMT().weekDay()       );      // 4 == Thursday
    BOOST_CHECK_EQUAL( 3                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Sun Jan 31st, 2010 at 20:00:00 EST == Mon Feb 1st, 2010 at 01:00:00 GMT

    theTime = CtiTime(CtiDate( 31, 1, 2010),  20,  0,  0  );

    BOOST_CHECK_EQUAL( 2                , theTime.dateGMT().month()         );
    BOOST_CHECK_EQUAL( 1                , theTime.dateGMT().dayOfMonth()    );
    BOOST_CHECK_EQUAL( 2010             , theTime.dateGMT().year()          );
    BOOST_CHECK_EQUAL( 1                , theTime.dateGMT().weekDay()       );      // 1 == Monday
    BOOST_CHECK_EQUAL( 1                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Sun Mar 14th, 2010 at 1:00:00 EST == Sun Mar 14th, 2010 at 6:00:00 GMT

    theTime = CtiTime(CtiDate( 14, 3, 2010),  1,  0,  0  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 6                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Sun Mar 14th, 2010 at 1:59:59 EST == Sun Mar 14th, 2010 at 6:59:59 GMT

    theTime = CtiTime(CtiDate( 14, 3, 2010),  1,  59,  59  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 6                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 59               , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 59               , theTime.secondGMT()   );


    // Sun Mar 14th, 2010 at 2:00:00 EST == Sun Mar 14th, 2010 at 6:00:00 GMT?!?!
    //  Just for documentation and completeness and fail.

    theTime = CtiTime(CtiDate( 14, 3, 2010),  2,   0,   0  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 6                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Sun Mar 14th, 2010 at 3:00:00 EDT == Sun Mar 14th, 2010 at 7:00:00 GMT

    theTime = CtiTime(CtiDate( 14, 3, 2010),  3,  0,  0  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 7                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Sun Mar 14th, 2010 at 4:00:00 EDT == Sun Mar 14th, 2010 at 8:00:00 GMT

    theTime = CtiTime(CtiDate( 14, 3, 2010),  4,  0,  0  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 8                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Wed July 14th, 2010 at 12:00:00 EDT == Wed July 14th, 2010 at 16:00:00 GMT

    theTime = CtiTime(CtiDate( 14, 7, 2010),  12,  0,  0  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 16               , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Sun Nov 7th, 2010 at 00:59:59 EDT == Sun Nov 7th, 2010 at 4:59:59 GMT

    theTime = CtiTime(CtiDate( 7, 11, 2010),  0, 59, 59  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 4                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 59               , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 59               , theTime.secondGMT()   );

    theTime += 1;  //  Move it to 1:00 EDT (the first 1:00)

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 5                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );

    // Sun Nov 7th, 2010 at 1:00:00 EST == Sun Nov 7th, 2010 at 6:00:00 GMT

    theTime = CtiTime(CtiDate( 7, 11, 2010),  1,  0,  0  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 6                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Sun Nov 7th, 2010 at 4:00:00 EDT == Sun Nov 7th, 2010 at 9:00:00 GMT

    theTime = CtiTime(CtiDate( 7, 11, 2010),  4,  0,  0  );

    BOOST_CHECK_EQUAL( theTime.date()   , theTime.dateGMT()     );
    BOOST_CHECK_EQUAL( 9                , theTime.hourGMT()     );
    BOOST_CHECK_EQUAL( 0                , theTime.minuteGMT()   );
    BOOST_CHECK_EQUAL( 0                , theTime.secondGMT()   );


    // Fri Dec 31st, 2010 at 22:00:00 EDT == Sat Jan 1st, 2011 at 3:00:00 GMT

    theTime = CtiTime(CtiDate( 31, 12, 2010),  22,  0,  0  );

    BOOST_CHECK_EQUAL( 1                , theTime.dateGMT().month()         );
    BOOST_CHECK_EQUAL( 1                , theTime.dateGMT().dayOfMonth()    );
    BOOST_CHECK_EQUAL( 2011             , theTime.dateGMT().year()          );
    BOOST_CHECK_EQUAL( 6                , theTime.dateGMT().weekDay()       );      // 6 == Saturday
    BOOST_CHECK_EQUAL( 3                , theTime.hourGMT()     );
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


BOOST_AUTO_TEST_CASE(test_ctitime_asString)
{
    Cti::Test::unset_timezone();

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
        { CtiTime::Local,      CtiTime::OmitTimezone,    "01/01/2009 00:00:00" },
        { CtiTime::LocalNoDst, CtiTime::OmitTimezone,    "01/01/2009 00:00:00" } };

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

/*
BOOST_AUTO_TEST_CASE(test_ctitime_asString_CST)
{
    Cti::Test::set_to_central_timezone();

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


BOOST_AUTO_TEST_CASE(test_ctitime_asString_CDT)
{
    Cti::Test::set_to_central_timezone();

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


BOOST_AUTO_TEST_CASE(test_ctitime_asString_EST)
{
    Cti::Test::set_to_eastern_timezone();

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
        { CtiTime::Gmt,        CtiTime::OmitTimezone,    "01/01/2009 05:00:00" },
        { CtiTime::Gmt,        CtiTime::IncludeTimezone, "01/01/2009 05:00:00 (UTC+0:00 GMT)" },
        { CtiTime::Local,      CtiTime::OmitTimezone,    "01/01/2009 00:00:00" },
        { CtiTime::Local,      CtiTime::IncludeTimezone, "01/01/2009 00:00:00 (UTC-5:00 EST)" },
        { CtiTime::LocalNoDst, CtiTime::OmitTimezone,    "01/01/2009 00:00:00" },
        { CtiTime::LocalNoDst, CtiTime::IncludeTimezone, "01/01/2009 00:00:00 (UTC-5:00 EST)" } };

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


BOOST_AUTO_TEST_CASE(test_ctitime_asString_EDT)
{
    Cti::Test::set_to_eastern_timezone();

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
        { CtiTime::Gmt,        CtiTime::OmitTimezone,    "06/01/2009 04:00:00" },
        { CtiTime::Gmt,        CtiTime::IncludeTimezone, "06/01/2009 04:00:00 (UTC+0:00 GMT)" },
        { CtiTime::Local,      CtiTime::OmitTimezone,    "06/01/2009 00:00:00" },
        { CtiTime::Local,      CtiTime::IncludeTimezone, "06/01/2009 00:00:00 (UTC-4:00 EDT)" },
        { CtiTime::LocalNoDst, CtiTime::OmitTimezone,    "05/31/2009 23:00:00" },
        { CtiTime::LocalNoDst, CtiTime::IncludeTimezone, "05/31/2009 23:00:00 (UTC-5:00 EST)" } };

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
*/

BOOST_AUTO_TEST_SUITE_END()
