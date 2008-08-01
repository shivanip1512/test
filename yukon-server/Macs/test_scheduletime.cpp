
/*---------------------------------------------------------------------------
        Filename:  test_scheduletime.cpp

        Programmer:  Jess Oteson

        Initial Date:  8/22/2008

        COPYRIGHT:  Copyright (C) Cannon Technologies 2007
---------------------------------------------------------------------------*/

#define BOOST_AUTO_TEST_MAIN "Test CCCapBank"
#include "yukon.h"

#include <boost/test/unit_test.hpp>
#include <boost/test/auto_unit_test.hpp>

#include <string>
#include <rw/rwdate.h>
#include <rw/rwtime.h>
#include <rw/zone.h>
#include <iostream>
#include <time.h>
#include <sstream>    // for istringstream
#include <locale>

#include "ctitime.h"
#include "mc_scheduler.h"
#include "mc_sched.h"
#include "mgr_mcsched.h"

using boost::unit_test_framework::test_suite;
using namespace std;

BOOST_AUTO_UNIT_TEST(test_MACS_calc_date_time_start)
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

    tempDate = CtiDate::CtiDate(2, 11, 2008);
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
    expectedResult = CtiTime(CtiTime::specialvalues::not_a_time);

    scheduler.testCalcDateTimeStart(inDst, schedule, startTime);
    BOOST_CHECK_EQUAL(expectedResult, startTime);

    schedule.setStartYear(0);
    tempDate = CtiDate(9, 3, 2009);
    expectedResult = CtiTime(tempDate, 9, 2, 4);

    scheduler.testCalcDateTimeStart(inDst, schedule, startTime);
    BOOST_CHECK_EQUAL(expectedResult, startTime);
}

