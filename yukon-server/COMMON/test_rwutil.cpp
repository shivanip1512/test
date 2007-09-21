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
#include <boost/date_time/posix_time/posix_time.hpp>
#include <boost/date_time/local_time_adjustor.hpp>
#include <boost/date_time/c_local_time_adjustor.hpp>

#include <string>

#include "rwutil.h"
#include "utility.h"
#include "ctitime.h"
#include "ctidate.h"
#include "boost_time.h"

using boost::unit_test_framework::test_suite;

BOOST_AUTO_UNIT_TEST(test_to_rwdbdt)
{
    CtiTime nowCtiTime;
    RWDBDateTime rwdbdt = toRWDBDT(nowCtiTime);

    BOOST_CHECK_EQUAL( rwdbdt.year(), nowCtiTime.date().year() );
    BOOST_CHECK_EQUAL( rwdbdt.dayOfMonth(), nowCtiTime.date().dayOfMonth() );
    BOOST_CHECK_EQUAL( rwdbdt.hour(), nowCtiTime.hour() );
    BOOST_CHECK_EQUAL( rwdbdt.minute(), nowCtiTime.minute() );
    BOOST_CHECK_EQUAL( rwdbdt.second(), nowCtiTime.second() );

    nowCtiTime = nowCtiTime.beginDST(2004);
    rwdbdt = toRWDBDT(nowCtiTime);

    BOOST_CHECK_EQUAL( rwdbdt.year(), nowCtiTime.date().year() );
    BOOST_CHECK_EQUAL( rwdbdt.dayOfMonth(), nowCtiTime.date().dayOfMonth() );
    BOOST_CHECK_EQUAL( rwdbdt.hour(), nowCtiTime.hour() );
    BOOST_CHECK_EQUAL( rwdbdt.minute(), nowCtiTime.minute() );
    BOOST_CHECK_EQUAL( rwdbdt.second(), nowCtiTime.second() );

    nowCtiTime = nowCtiTime.endDST(2004);
    rwdbdt = toRWDBDT(nowCtiTime);

    BOOST_CHECK_EQUAL( rwdbdt.year(), nowCtiTime.date().year() );
    BOOST_CHECK_EQUAL( rwdbdt.dayOfMonth(), nowCtiTime.date().dayOfMonth() );
    BOOST_CHECK_EQUAL( rwdbdt.hour(), nowCtiTime.hour() );
    BOOST_CHECK_EQUAL( rwdbdt.minute(), nowCtiTime.minute() );
    BOOST_CHECK_EQUAL( rwdbdt.second(), nowCtiTime.second() );

    nowCtiTime = nowCtiTime.beginDST(2007);
    rwdbdt = toRWDBDT(nowCtiTime);

    BOOST_CHECK_EQUAL( rwdbdt.year(), nowCtiTime.date().year() );
    BOOST_CHECK_EQUAL( rwdbdt.dayOfMonth(), nowCtiTime.date().dayOfMonth() );
    BOOST_CHECK_EQUAL( rwdbdt.hour(), nowCtiTime.hour() );
    BOOST_CHECK_EQUAL( rwdbdt.minute(), nowCtiTime.minute() );
    BOOST_CHECK_EQUAL( rwdbdt.second(), nowCtiTime.second() );
}

BOOST_AUTO_UNIT_TEST(test_to_boost_date)
{
    RWDate rw_date = RWDate();
    date d = to_boost_date(rw_date);
    BOOST_CHECK_EQUAL( rw_date.dayOfMonth(), d.day() );
    BOOST_CHECK_EQUAL( rw_date.month(), d.month() );
    BOOST_CHECK_EQUAL( rw_date.year(), d.year() );
}

BOOST_AUTO_UNIT_TEST(test_to_boost_ptime)
{
    typedef boost::date_time::c_local_adjustor<ptime> local_adj;
    RWDBDateTime rw_date_time;
    ptime boost_time = to_boost_ptime(rw_date_time);
    boost_time = local_adj::utc_to_local(boost_time);

    date d = boost_time.date();
    BOOST_CHECK_EQUAL( rw_date_time.dayOfMonth(), d.day() );
    BOOST_CHECK_EQUAL( rw_date_time.month(), d.month() );
    BOOST_CHECK_EQUAL( rw_date_time.year(), d.year() );

    time_duration td = boost_time.time_of_day();
    std::cout << "Boost, rwdate times: " <<  to_simple_string(boost_time) << ", " << rw_date_time.asString() << std::endl;
    BOOST_CHECK_EQUAL( rw_date_time.hour(), td.hours() );
    BOOST_CHECK_EQUAL( rw_date_time.minute(), td.minutes() );
    BOOST_CHECK_EQUAL( rw_date_time.second(), td.seconds() );
}


BOOST_AUTO_UNIT_TEST(test_stringCompareIgnoreCase)
{
    std::string s1 = "My Compare";
    std::string s2 = "my cOmParE";
    BOOST_CHECK_EQUAL( stringCompareIgnoreCase(s1, s2), 0 );


}
