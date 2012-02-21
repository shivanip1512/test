#include <boost/test/auto_unit_test.hpp>

#include "ctitime.h"
#include "mc_scheduler.h"
#include "mc_sched.h"
#include "mgr_mcsched.h"

using namespace std;

BOOST_AUTO_TEST_SUITE( test_scheduletime )

BOOST_AUTO_TEST_CASE(test_MACS_calc_date_time_start)
{
    CtiMCScheduleManager schedMgr;
    Test_CtiMCScheduler scheduler = Test_CtiMCScheduler::Test_CtiMCScheduler(schedMgr);
    CtiMCSchedule schedule;
    CtiTime startTime, expectedResult;
    CtiDate inDst(22, 4, 2008);
    CtiDate beforeDST(22, 1, 2008);
    CtiDate tempDate;
    CtiTime now(inDst, 3, 3, 3);//Unimportant really.

    schedule.setStartTime("09:02:04");//after DST change
    schedule.setStartYear(2008);
    schedule.setStartMonth(11);//Months are 1-12
    schedule.setStartDay(2);// 11/2/2008 is last day of DST (change on this day)

    tempDate = CtiDate(2, 11, 2008);
    expectedResult = CtiTime(tempDate, 9, 2, 4);

    scheduler.testCalcDateTimeStart(now, schedule, startTime);

    BOOST_CHECK_EQUAL(expectedResult, startTime);

    schedule.setStartTime("09:02:04");//after DST change
    schedule.setStartYear(2008);
    schedule.setStartMonth(3);//Months are 1-12
    schedule.setStartDay(9);// 3/9/2008 is first day of DST (change on this day)
    tempDate = CtiDate(9, 3, 2008);
    expectedResult = CtiTime(tempDate, 9, 2, 4);

    scheduler.testCalcDateTimeStart(beforeDST, schedule, startTime);
    BOOST_CHECK_EQUAL(expectedResult, startTime);

    schedule.setStartYear(0);

    scheduler.testCalcDateTimeStart(beforeDST, schedule, startTime);
    BOOST_CHECK_EQUAL(expectedResult, startTime);

    schedule.setStartTime("09:02:04");//after DST change
    schedule.setStartYear(2008);
    schedule.setStartMonth(3);//Months are 1-12
    schedule.setStartDay(9);// 3/9/2008 is first day of DST (change on this day)
    tempDate = CtiDate(9, 3, 2008);
    expectedResult = CtiTime(CtiTime::not_a_time);

    scheduler.testCalcDateTimeStart(inDst, schedule, startTime);
    BOOST_CHECK_EQUAL(expectedResult, startTime);

    schedule.setStartYear(0);
    tempDate = CtiDate(9, 3, 2009);
    expectedResult = CtiTime(tempDate, 9, 2, 4);

    scheduler.testCalcDateTimeStart(inDst, schedule, startTime);
    BOOST_CHECK_EQUAL(expectedResult, startTime);
}

BOOST_AUTO_TEST_CASE(test_MACS_day_of_month_start)
{
    CtiMCScheduleManager schedMgr;
    Test_CtiMCScheduler scheduler = Test_CtiMCScheduler::Test_CtiMCScheduler(schedMgr);
    CtiMCSchedule schedule;
    CtiTime startTime, expectedResult;
    CtiDate febLeapYear(22, 2, 2008); // February has 29 days in 2008
    CtiDate febNonLeapYear(22, 2, 2009); // February has 28 days in 2009

    schedule.setStartTime("09:02:04");
    schedule.setStartDay(31);
    schedule.setStartYear(2008); // Irrelevant
    schedule.setStartMonth(11);// Irrelevant

    CtiTime now(febLeapYear, 3, 3, 3);
    CtiDate tempDate = CtiDate(29, 2, 2008); // Testing leap year first
    expectedResult = CtiTime(tempDate, 9, 2, 4);
    scheduler.testCalcDayOfMonthStart(now, schedule, startTime);

    BOOST_CHECK_EQUAL(expectedResult, startTime);

    now = CtiTime::CtiTime(febNonLeapYear, 3, 3, 3);
    tempDate = CtiDate(28, 2, 2009); // Testing non leap year second
    expectedResult = CtiTime(tempDate, 9, 2, 4);
    scheduler.testCalcDayOfMonthStart(now, schedule, startTime);

    BOOST_CHECK_EQUAL(expectedResult, startTime);

    schedule.setStartDay(28);

    now = CtiTime::CtiTime(CtiDate(22,3,2009), 3, 3, 3);
    expectedResult = CtiTime(CtiDate(28, 3, 2009), 9, 2, 4); // Testing March
    scheduler.testCalcDayOfMonthStart(now, schedule, startTime);

    BOOST_CHECK_EQUAL(expectedResult, startTime);

    now = CtiTime::CtiTime(CtiDate(28,1,2009), 9, 2, 5); // 1 second after the time
    expectedResult = CtiTime(CtiDate(28, 2, 2009), 9, 2, 4); // Means next month is selected
    scheduler.testCalcDayOfMonthStart(now, schedule, startTime);

    BOOST_CHECK_EQUAL(expectedResult, startTime);

    now = CtiTime::CtiTime(CtiDate(28,1,2009), 9, 2, 4); // Exactly on the time
    expectedResult = CtiTime(CtiDate(28, 1, 2009), 9, 2, 4);
    scheduler.testCalcDayOfMonthStart(now, schedule, startTime);

    BOOST_CHECK_EQUAL(expectedResult, startTime);

    now = CtiTime::CtiTime(CtiDate(28,1,2009), 9, 2, 3); // 1 second before the time
    expectedResult = CtiTime(CtiDate(28, 1, 2009), 9, 2, 4);
    scheduler.testCalcDayOfMonthStart(now, schedule, startTime);

    BOOST_CHECK_EQUAL(expectedResult, startTime);

    now = CtiTime::CtiTime(CtiDate(29,12,2009), 3, 3, 3);
    expectedResult = CtiTime(CtiDate(28, 1, 2010), 9, 2, 4); // This overlaps the year!
    scheduler.testCalcDayOfMonthStart(now, schedule, startTime);

    BOOST_CHECK_EQUAL(expectedResult, startTime);

    //DST TESTING!!!!!
    schedule.setStartTime("03:02:04");//after DST change
    schedule.setStartDay(9);// 3/9/2008 is first day of DST (change on this day)
    expectedResult = CtiTime(CtiDate(9, 3, 2008), 3, 2, 4);
    now = CtiTime::CtiTime(CtiDate(3, 3, 2008), 3, 3, 3);
    scheduler.testCalcDayOfMonthStart(now, schedule, startTime);

    BOOST_CHECK_EQUAL(expectedResult, startTime);

    expectedResult = CtiTime(CtiDate(9, 3, 2008), 3, 2, 4);
    now = CtiTime::CtiTime(CtiDate(9, 2, 2008), 3, 3, 3); // hour after last month's
    scheduler.testCalcDayOfMonthStart(now, schedule, startTime);

    BOOST_CHECK_EQUAL(expectedResult, startTime);

    expectedResult = CtiTime(CtiDate(9, 3, 2008), 3, 2, 4);
    now = CtiTime::CtiTime(CtiDate(9, 3, 2008), 1, 1, 1); // few hours before, on same day
    scheduler.testCalcDayOfMonthStart(now, schedule, startTime);

    BOOST_CHECK_EQUAL(expectedResult, startTime);

    schedule.setStartMonth(11);//Months are 1-12
    schedule.setStartDay(2);// 11/2/2008 is last day of DST (change on this day)
    now = CtiTime::CtiTime(CtiDate(2, 11, 2008), 1, 1, 1); // few hours before, same day
    expectedResult = CtiTime(CtiDate(2, 11, 2008), 3, 2, 4);
    scheduler.testCalcDayOfMonthStart(now, schedule, startTime);

    BOOST_CHECK_EQUAL(expectedResult, startTime);

    now = CtiTime::CtiTime(CtiDate(2, 11, 2008), 3, 2, 5); // 1 second past, on DST day
    expectedResult = CtiTime(CtiDate(2, 12, 2008), 3, 2, 4);
    scheduler.testCalcDayOfMonthStart(now, schedule, startTime);

    BOOST_CHECK_EQUAL(expectedResult, startTime);

    now = CtiTime::CtiTime(CtiDate(2, 10, 2008), 3, 2, 5); // 1 second after previous month's end
    expectedResult = CtiTime(CtiDate(2, 11, 2008), 3, 2, 4);
    scheduler.testCalcDayOfMonthStart(now, schedule, startTime);

    BOOST_CHECK_EQUAL(expectedResult, startTime);
}

BOOST_AUTO_TEST_SUITE_END()
