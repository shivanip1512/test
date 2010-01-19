/*-----------------------------------------------------------------------------*
*
* File:   test_lmprogram
*
* Copyright (c) 2009 Cooper Industries. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "lmprogramdirect.h"
#include "lmprogramcontrolwindow.h"
#include "lmutility.h"
#include "lmconstraint.h"
#include "executor.h"

#include <string>
#include <vector>

#define BOOST_AUTO_TEST_MAIN "Test LM Program"
#include <boost/test/unit_test.hpp>

// Nonexistent window
const CtiLMProgramControlWindow *no_window = 0;


/*
***  TESTING: GetTimeFromOffsetAndDate()
*/
BOOST_AUTO_TEST_CASE(test_get_wallclock_time)
{
    CtiDate date(1, 7, 2009);

    CtiTime time = GetTimeFromOffsetAndDate(14400, date);

    BOOST_CHECK_EQUAL( time.date().month(),       7 );
    BOOST_CHECK_EQUAL( time.date().dayOfMonth(),  1 );
    BOOST_CHECK_EQUAL( time.date().year(),     2009 );
    BOOST_CHECK_EQUAL( time.hour(),               4 );
    BOOST_CHECK_EQUAL( time.minute(),             0 );
    BOOST_CHECK_EQUAL( time.second(),             0 );
}


BOOST_AUTO_TEST_CASE(test_get_wallclock_time_no_offset)
{
    CtiDate date(1, 7, 2009);

    CtiTime time = GetTimeFromOffsetAndDate(0, date);

    BOOST_CHECK_EQUAL( time.date().month(),       7 );
    BOOST_CHECK_EQUAL( time.date().dayOfMonth(),  1 );
    BOOST_CHECK_EQUAL( time.date().year(),     2009 );
    BOOST_CHECK_EQUAL( time.hour(),               0 );
    BOOST_CHECK_EQUAL( time.minute(),             0 );
    BOOST_CHECK_EQUAL( time.second(),             0 );
}


BOOST_AUTO_TEST_CASE(test_get_wallclock_time_exactly_one_day_offset)
{
    CtiDate date(1, 7, 2009);

    CtiTime time = GetTimeFromOffsetAndDate(86400, date);

    BOOST_CHECK_EQUAL( time.date().month(),       7 );
    BOOST_CHECK_EQUAL( time.date().dayOfMonth(),  2 );
    BOOST_CHECK_EQUAL( time.date().year(),     2009 );
    BOOST_CHECK_EQUAL( time.hour(),               0 );
    BOOST_CHECK_EQUAL( time.minute(),             0 );
    BOOST_CHECK_EQUAL( time.second(),             0 );
}


BOOST_AUTO_TEST_CASE(test_get_wallclock_time_more_than_one_day_offset)
{
    CtiDate date(1, 7, 2009);

    CtiTime time = GetTimeFromOffsetAndDate(120000, date);  // 1*86400 + 9*3600 + 20*60

    BOOST_CHECK_EQUAL( time.date().month(),       7 );
    BOOST_CHECK_EQUAL( time.date().dayOfMonth(),  2 );
    BOOST_CHECK_EQUAL( time.date().year(),     2009 );
    BOOST_CHECK_EQUAL( time.hour(),               9 );
    BOOST_CHECK_EQUAL( time.minute(),            20 );
    BOOST_CHECK_EQUAL( time.second(),             0 );
}


BOOST_AUTO_TEST_CASE(test_get_wallclock_time_exactly_one_day_negative_offset)
{
    CtiDate date(1, 7, 2009);

    CtiTime time = GetTimeFromOffsetAndDate(-86400, date);

    BOOST_CHECK_EQUAL( time.date().month(),       6 );
    BOOST_CHECK_EQUAL( time.date().dayOfMonth(), 30 );
    BOOST_CHECK_EQUAL( time.date().year(),     2009 );
    BOOST_CHECK_EQUAL( time.hour(),               0 );
    BOOST_CHECK_EQUAL( time.minute(),             0 );
    BOOST_CHECK_EQUAL( time.second(),             0 );
}


BOOST_AUTO_TEST_CASE(test_get_wallclock_time_more_than_one_day_negative_offset)
{
    CtiDate date(1, 7, 2009);

    CtiTime time = GetTimeFromOffsetAndDate(-120000, date);  // -(1*86400 + 9*3600 + 20*60)

    BOOST_CHECK_EQUAL( time.date().month(),       6 );
    BOOST_CHECK_EQUAL( time.date().dayOfMonth(), 29 );
    BOOST_CHECK_EQUAL( time.date().year(),     2009 );
    BOOST_CHECK_EQUAL( time.hour(),              14 );
    BOOST_CHECK_EQUAL( time.minute(),            40 );
    BOOST_CHECK_EQUAL( time.second(),             0 );
}


BOOST_AUTO_TEST_CASE(test_get_wallclock_time_spring_2009_dst)
{
    CtiDate date(8, 3, 2009);

    CtiTime time = GetTimeFromOffsetAndDate(14400, date);  // 4am - not 5am!!  dst switch ignored...

    BOOST_CHECK_EQUAL( time.date().month(),       3 );
    BOOST_CHECK_EQUAL( time.date().dayOfMonth(),  8 );
    BOOST_CHECK_EQUAL( time.date().year(),     2009 );
    BOOST_CHECK_EQUAL( time.hour(),               4 );
    BOOST_CHECK_EQUAL( time.minute(),             0 );
    BOOST_CHECK_EQUAL( time.second(),             0 );
}


BOOST_AUTO_TEST_CASE(test_get_wallclock_time_fall_2009_dst)
{
    CtiDate date(1, 11, 2009);

    CtiTime time = GetTimeFromOffsetAndDate(14400, date);  // 4am - not 3am!! dst switch ignored...

    BOOST_CHECK_EQUAL( time.date().month(),      11 );
    BOOST_CHECK_EQUAL( time.date().dayOfMonth(),  1 );
    BOOST_CHECK_EQUAL( time.date().year(),     2009 );
    BOOST_CHECK_EQUAL( time.hour(),               4 );
    BOOST_CHECK_EQUAL( time.minute(),             0 );
    BOOST_CHECK_EQUAL( time.second(),             0 );
}


BOOST_AUTO_TEST_CASE(test_get_wallclock_time_spring_2009_dst_invalid_time)
{
    CtiDate date(8, 3, 2009);

    CtiTime time = GetTimeFromOffsetAndDate(7199, date);    // 1:59:59am - OK

    BOOST_CHECK_EQUAL( time.date().month(),       3 );
    BOOST_CHECK_EQUAL( time.date().dayOfMonth(),  8 );
    BOOST_CHECK_EQUAL( time.date().year(),     2009 );
    BOOST_CHECK_EQUAL( time.hour(),               1 );
    BOOST_CHECK_EQUAL( time.minute(),            59 );
    BOOST_CHECK_EQUAL( time.second(),            59 );

    time = GetTimeFromOffsetAndDate(7200, date);            // 2am - when is that???

    BOOST_CHECK_EQUAL( time.date().month(),       3 );      // returns 1am
    BOOST_CHECK_EQUAL( time.date().dayOfMonth(),  8 );
    BOOST_CHECK_EQUAL( time.date().year(),     2009 );
    BOOST_CHECK_EQUAL( time.hour(),               1 );
    BOOST_CHECK_EQUAL( time.minute(),             0 );
    BOOST_CHECK_EQUAL( time.second(),             0 );

    time = GetTimeFromOffsetAndDate(9000, date);            // 2:30am

    BOOST_CHECK_EQUAL( time.date().month(),       3 );      // returns 1:30am
    BOOST_CHECK_EQUAL( time.date().dayOfMonth(),  8 );
    BOOST_CHECK_EQUAL( time.date().year(),     2009 );
    BOOST_CHECK_EQUAL( time.hour(),               1 );
    BOOST_CHECK_EQUAL( time.minute(),            30 );
    BOOST_CHECK_EQUAL( time.second(),             0 );

    time = GetTimeFromOffsetAndDate(10800, date);           // 3am - OK

    BOOST_CHECK_EQUAL( time.date().month(),       3 );
    BOOST_CHECK_EQUAL( time.date().dayOfMonth(),  8 );
    BOOST_CHECK_EQUAL( time.date().year(),     2009 );
    BOOST_CHECK_EQUAL( time.hour(),               3 );
    BOOST_CHECK_EQUAL( time.minute(),             0 );
    BOOST_CHECK_EQUAL( time.second(),             0 );
}


BOOST_AUTO_TEST_CASE(test_get_wallclock_time_fall_2009_dst_invalid_time)
{
    CtiDate date(1, 11, 2009);

    CtiTime time = GetTimeFromOffsetAndDate(7199, date);    // 1:59:59am - OK

    BOOST_CHECK_EQUAL( time.date().month(),      11 );
    BOOST_CHECK_EQUAL( time.date().dayOfMonth(),  1 );
    BOOST_CHECK_EQUAL( time.date().year(),     2009 );
    BOOST_CHECK_EQUAL( time.hour(),               1 );
    BOOST_CHECK_EQUAL( time.minute(),            59 );
    BOOST_CHECK_EQUAL( time.second(),            59 );

    time = GetTimeFromOffsetAndDate(7200, date);            // 2am - OK

    BOOST_CHECK_EQUAL( time.date().month(),      11 );
    BOOST_CHECK_EQUAL( time.date().dayOfMonth(),  1 );
    BOOST_CHECK_EQUAL( time.date().year(),     2009 );
    BOOST_CHECK_EQUAL( time.hour(),               2 );
    BOOST_CHECK_EQUAL( time.minute(),             0 );
    BOOST_CHECK_EQUAL( time.second(),             0 );

    time = GetTimeFromOffsetAndDate(9000, date);            // 2:30am - OK

    BOOST_CHECK_EQUAL( time.date().month(),      11 );
    BOOST_CHECK_EQUAL( time.date().dayOfMonth(),  1 );
    BOOST_CHECK_EQUAL( time.date().year(),     2009 );
    BOOST_CHECK_EQUAL( time.hour(),               2 );
    BOOST_CHECK_EQUAL( time.minute(),            30 );
    BOOST_CHECK_EQUAL( time.second(),             0 );

    time = GetTimeFromOffsetAndDate(10800, date);           // 3am - OK

    BOOST_CHECK_EQUAL( time.date().month(),      11 );
    BOOST_CHECK_EQUAL( time.date().dayOfMonth(),  1 );
    BOOST_CHECK_EQUAL( time.date().year(),     2009 );
    BOOST_CHECK_EQUAL( time.hour(),               3 );
    BOOST_CHECK_EQUAL( time.minute(),             0 );
    BOOST_CHECK_EQUAL( time.second(),             0 );
}


/*
*** TESTING: getControlWindow()
*/
BOOST_AUTO_TEST_CASE(test_get_control_window_same_day)
{
    CtiLMProgramDirect lmProgram;
    CtiLMProgramControlWindow *window1 = new CtiLMProgramControlWindow();

    window1->setPAOId(1);
    window1->setWindowNumber(1);
    window1->setAvailableStartTime(79200);
    window1->setAvailableStopTime(82000);

    lmProgram.getLMProgramControlWindows().push_back(window1);

    /* todays window */
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 1200), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(78000), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79199), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79200), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79201), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(80000), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(82000), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(82001), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(83000), no_window);

    /* tomorrows window */
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 1200 + 86400), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(78000 + 86400), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79199 + 86400), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79200 + 86400), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79201 + 86400), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(80000 + 86400), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(82000 + 86400), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(82001 + 86400), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(83000 + 86400), no_window);
}


BOOST_AUTO_TEST_CASE(test_get_control_window_overlap_two_days)
{
    CtiLMProgramDirect lmProgram;
    CtiLMProgramControlWindow *window1 = new CtiLMProgramControlWindow();

    window1->setPAOId(1);
    window1->setWindowNumber(1);
    window1->setAvailableStartTime(79200);
    window1->setAvailableStopTime(90400); // 86400 + 4000

    lmProgram.getLMProgramControlWindows().push_back(window1);

    /* todays window */
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(    0), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 2000), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 4000), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 4001), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(50000), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79199), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79200), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(86399), window1);

    /* tomorrows window */
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(    0 + 86400), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 2000 + 86400), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 4000 + 86400), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 4001 + 86400), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(50000 + 86400), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79199 + 86400), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79200 + 86400), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(86399 + 86400), window1);
}


BOOST_AUTO_TEST_CASE(test_get_control_window_overlap_two_days_end_of_month)
{
    CtiLMProgramDirect lmProgram;
    CtiLMProgramControlWindow *window1 = new CtiLMProgramControlWindow();

    window1->setPAOId(1);
    window1->setWindowNumber(1);
    window1->setAvailableStartTime(79200);
    window1->setAvailableStopTime(90400); // 86400 + 4000

    lmProgram.getLMProgramControlWindows().push_back(window1);

    CtiDate targetDate(31, 8, 2009);

    /* todays window */
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(    0, targetDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 2000, targetDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 4000, targetDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 4001, targetDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(50000, targetDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79199, targetDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79200, targetDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(86399, targetDate), window1);

    /* tomorrows window */
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(    0 + 86400, targetDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 2000 + 86400, targetDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 4000 + 86400, targetDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 4001 + 86400, targetDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(50000 + 86400, targetDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79199 + 86400, targetDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79200 + 86400, targetDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(86399 + 86400, targetDate), window1);
}


BOOST_AUTO_TEST_CASE(test_get_control_window_spring_2009_dst)
{
    CtiLMProgramDirect lmProgram;
    CtiLMProgramControlWindow *window1 = new CtiLMProgramControlWindow();

    window1->setPAOId(1);
    window1->setWindowNumber(1);
    window1->setAvailableStartTime(3600);   // 1am
    window1->setAvailableStopTime(14400);   // 4am

    lmProgram.getLMProgramControlWindows().push_back(window1);

    CtiDate dstDate(8, 3, 2009);     // day of dst switch - make sure window didn't get lenghtened to 5am

    /* todays window */
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(    0, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 2000, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 3599, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 3600, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 4000, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 7200, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 7201, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(14400, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(14401, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(18000, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(18001, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(20000, dstDate), no_window);

    /* tomorrows window */
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(    0 + 86400, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 2000 + 86400, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 3599 + 86400, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 3600 + 86400, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 4000 + 86400, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 7200 + 86400, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 7201 + 86400, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(14400 + 86400, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(14401 + 86400, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(18000 + 86400, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(18001 + 86400, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(20000 + 86400, dstDate), no_window);
}


BOOST_AUTO_TEST_CASE(test_get_control_window_fall_2009_dst)
{
    CtiLMProgramDirect lmProgram;
    CtiLMProgramControlWindow *window1 = new CtiLMProgramControlWindow();

    window1->setPAOId(1);
    window1->setWindowNumber(1);
    window1->setAvailableStartTime(3600);   // 1am
    window1->setAvailableStopTime(14400);   // 4am

    lmProgram.getLMProgramControlWindows().push_back(window1);

    CtiDate dstDate(1, 11, 2009);     // day of dst switch  - make sure window didn't get shortened to 3am

    /* todays window */
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(    0, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 2000, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 3599, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 3600, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 4000, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 7200, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 7201, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(14400, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(14401, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(18000, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(18001, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(20000, dstDate), no_window);

    /* tomorrows window */
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(    0 + 86400, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 2000 + 86400, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 3599 + 86400, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 3600 + 86400, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 4000 + 86400, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 7200 + 86400, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 7201 + 86400, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(14400 + 86400, dstDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(14401 + 86400, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(18000 + 86400, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(18001 + 86400, dstDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(20000 + 86400, dstDate), no_window);
}


/*
*** TESTING: checkControlAreaControlWindows()
*/
BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_and_stop_before)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 08:00 to 16:00
    controlArea.setCurrentDailyStartTime(28800);    // 08:00 == 8 * 3600 == 28800
    controlArea.setCurrentDailyStopTime(57600);     // 16:00 == 16 * 3600 == 57600

    CtiTime startTime = GetTimeFromOffsetAndDate(14400, today);     // 4:00am
    CtiTime stopTime = GetTimeFromOffsetAndDate(21600, today);      // 6:00am

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed start time of 07/01/2009 04:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/01/2009 08:00:00 to 07/01/2009 16:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_before_stop_in)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 08:00 to 16:00
    controlArea.setCurrentDailyStartTime(28800);    // 08:00 == 8 * 3600 == 28800
    controlArea.setCurrentDailyStopTime(57600);     // 16:00 == 16 * 3600 == 57600

    CtiTime startTime = GetTimeFromOffsetAndDate(14400, today);     // 4:00am
    CtiTime stopTime = GetTimeFromOffsetAndDate(36000, today);      // 10:00am

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed start time of 07/01/2009 04:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/01/2009 08:00:00 to 07/01/2009 16:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_before_stop_after)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 08:00 to 16:00
    controlArea.setCurrentDailyStartTime(28800);    // 08:00 == 8 * 3600 == 28800
    controlArea.setCurrentDailyStopTime(57600);     // 16:00 == 16 * 3600 == 57600

    CtiTime startTime = GetTimeFromOffsetAndDate(14400, today);     // 4:00am
    CtiTime stopTime = GetTimeFromOffsetAndDate(72000, today);      // 8:00pm

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed start time of 07/01/2009 04:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/01/2009 08:00:00 to 07/01/2009 16:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_and_stop_in)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 08:00 to 16:00
    controlArea.setCurrentDailyStartTime(28800);    // 08:00 == 8 * 3600 == 28800
    controlArea.setCurrentDailyStopTime(57600);     // 16:00 == 16 * 3600 == 57600

    CtiTime startTime = GetTimeFromOffsetAndDate(36000, today);     // 10:00am
    CtiTime stopTime = GetTimeFromOffsetAndDate(43200, today);      // noon

    BOOST_CHECK_EQUAL(true, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(   0, constraints.getViolations().size() );
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_in_stop_after)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 08:00 to 16:00
    controlArea.setCurrentDailyStartTime(28800);    // 08:00 == 8 * 3600 == 28800
    controlArea.setCurrentDailyStopTime(57600);     // 16:00 == 16 * 3600 == 57600

    CtiTime startTime = GetTimeFromOffsetAndDate(36000, today);     // 10:00am
    CtiTime stopTime = GetTimeFromOffsetAndDate(64800, today);      // 6:00pm

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed stop time of 07/01/2009 18:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/01/2009 08:00:00 to 07/01/2009 16:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_and_stop_after)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 08:00 to 16:00
    controlArea.setCurrentDailyStartTime(28800);    // 08:00 == 8 * 3600 == 28800
    controlArea.setCurrentDailyStopTime(57600);     // 16:00 == 16 * 3600 == 57600

    CtiTime startTime = GetTimeFromOffsetAndDate(64800, today);     // 6:00pm
    CtiTime stopTime = GetTimeFromOffsetAndDate(72000, today);      // 8:00pm

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed start time of 07/01/2009 18:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/01/2009 08:00:00 to 07/01/2009 16:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_and_stop_before_tomorrow)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 08:00 to 16:00
    controlArea.setCurrentDailyStartTime(28800);    // 08:00 == 8 * 3600 == 28800
    controlArea.setCurrentDailyStopTime(57600);     // 16:00 == 16 * 3600 == 57600

    CtiTime startTime = GetTimeFromOffsetAndDate(100800, today);    // 4:00am tomorrow
    CtiTime stopTime = GetTimeFromOffsetAndDate(108000, today);     // 6:00am tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    // since our window is tomorrow the constraint error message should report tomorrows control window

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed start time of 07/02/2009 04:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/02/2009 08:00:00 to 07/02/2009 16:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_before_stop_in_tomorrow)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 08:00 to 16:00
    controlArea.setCurrentDailyStartTime(28800);    // 08:00 == 8 * 3600 == 28800
    controlArea.setCurrentDailyStopTime(57600);     // 16:00 == 16 * 3600 == 57600

    CtiTime startTime = GetTimeFromOffsetAndDate(100800, today);    // 4:00am tomorrow
    CtiTime stopTime = GetTimeFromOffsetAndDate(122400, today);     // 10:00am tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    // since our window is tomorrow the constraint error message should report tomorrows control window

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed start time of 07/02/2009 04:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/02/2009 08:00:00 to 07/02/2009 16:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_before_stop_after_tomorrow)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 08:00 to 16:00
    controlArea.setCurrentDailyStartTime(28800);    // 08:00 == 8 * 3600 == 28800
    controlArea.setCurrentDailyStopTime(57600);     // 16:00 == 16 * 3600 == 57600

    CtiTime startTime = GetTimeFromOffsetAndDate(100800, today);    // 4:00am tomorrow
    CtiTime stopTime = GetTimeFromOffsetAndDate(158400, today);     // 8:00pm tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    // since our window is tomorrow the constraint error message should report tomorrows control window

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed start time of 07/02/2009 04:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/02/2009 08:00:00 to 07/02/2009 16:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_and_stop_in_tomorrow)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 08:00 to 16:00
    controlArea.setCurrentDailyStartTime(28800);    // 08:00 == 8 * 3600 == 28800
    controlArea.setCurrentDailyStopTime(57600);     // 16:00 == 16 * 3600 == 57600

    CtiTime startTime = GetTimeFromOffsetAndDate(122400, today);    // 10:00am tomorrow
    CtiTime stopTime = GetTimeFromOffsetAndDate(129600, today);     // noon tomorrow

    BOOST_CHECK_EQUAL(true, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(   0, constraints.getViolations().size() );
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_in_stop_after_tomorrow)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 08:00 to 16:00
    controlArea.setCurrentDailyStartTime(28800);    // 08:00 == 8 * 3600 == 28800
    controlArea.setCurrentDailyStopTime(57600);     // 16:00 == 16 * 3600 == 57600

    CtiTime startTime = GetTimeFromOffsetAndDate(122400, today);    // 10:00am tomorrow
    CtiTime stopTime = GetTimeFromOffsetAndDate(151200, today);     // 6:00pm tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    // since our window is tomorrow the constraint error message should report tomorrows control window

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed stop time of 07/02/2009 18:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/02/2009 08:00:00 to 07/02/2009 16:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_and_stop_after_tomorrow)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 08:00 to 16:00
    controlArea.setCurrentDailyStartTime(28800);    // 08:00 == 8 * 3600 == 28800
    controlArea.setCurrentDailyStopTime(57600);     // 16:00 == 16 * 3600 == 57600

    CtiTime startTime = GetTimeFromOffsetAndDate(151200, today);    // 6:00pm tomorrow
    CtiTime stopTime = GetTimeFromOffsetAndDate(158400, today);     // 8:00pm tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    // since our window is tomorrow the constraint error message should report tomorrows control window

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed start time of 07/02/2009 18:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/02/2009 08:00:00 to 07/02/2009 16:00:00");
}


// ASCII ART!!!
//           today                                            tomorrow
// timeline:  |------------------------------------------------|------------------------------------------------|
// window:    xxxxxxxxxxxxx                            xxxxxxxxxxxxxxxxxxxx                             xxxxxxxxxxxxxxxxxxxx
// section:   |      1     |             2            |     3  |     4     |            5              |    6   |    7

BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_both_in_section_1)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(1800, today);      // 12:30am
    CtiTime stopTime = GetTimeFromOffsetAndDate(18000, today);      // 5:00am

    BOOST_CHECK_EQUAL(true, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(   0, constraints.getViolations().size() );
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_1_stop_section_2)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(1800, today);      // 12:30am
    CtiTime stopTime = GetTimeFromOffsetAndDate(43200, today);      // noon

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    // since our start time falls in yesterdays window, the constraint error message should report yesterdays control window

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed stop time of 07/01/2009 12:00:00 is outside the CONTROL AREA control window "
                       "that runs from 06/30/2009 20:00:00 to 07/01/2009 06:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_1_stop_section_3)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(1800, today);      // 12:30am
    CtiTime stopTime = GetTimeFromOffsetAndDate(79200, today);      // 10:00pm

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    // since our start time falls in yesterdays window, the constraint error message should report yesterdays control window

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed stop time of 07/01/2009 22:00:00 is outside the CONTROL AREA control window "
                       "that runs from 06/30/2009 20:00:00 to 07/01/2009 06:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_1_stop_section_4)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(1800, today);      // 12:30am
    CtiTime stopTime = GetTimeFromOffsetAndDate(104400, today);     // 5:00am tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    // since our start time falls in yesterdays window, the constraint error message should report yesterdays control window

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed stop time of 07/02/2009 05:00:00 is outside the CONTROL AREA control window "
                       "that runs from 06/30/2009 20:00:00 to 07/01/2009 06:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_1_stop_section_5)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(1800, today);      // 12:30am
    CtiTime stopTime = GetTimeFromOffsetAndDate(129600, today);     // noon tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    // since our start time falls in yesterdays window, the constraint error message should report yesterdays control window

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed stop time of 07/02/2009 12:00:00 is outside the CONTROL AREA control window "
                       "that runs from 06/30/2009 20:00:00 to 07/01/2009 06:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_1_stop_section_6)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(1800, today);      // 12:30am
    CtiTime stopTime = GetTimeFromOffsetAndDate(165600, today);     // 10:00pm tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    // since our start time falls in yesterdays window, the constraint error message should report yesterdays control window

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed stop time of 07/02/2009 22:00:00 is outside the CONTROL AREA control window "
                       "that runs from 06/30/2009 20:00:00 to 07/01/2009 06:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_2_stop_section_2)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(43200, today);     // noon
    CtiTime stopTime = GetTimeFromOffsetAndDate(50400, today);      // 2:00pm

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed start time of 07/01/2009 12:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/01/2009 20:00:00 to 07/02/2009 06:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_2_stop_section_3)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(43200, today);     // noon
    CtiTime stopTime = GetTimeFromOffsetAndDate(79200, today);      // 10:00pm

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed start time of 07/01/2009 12:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/01/2009 20:00:00 to 07/02/2009 06:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_2_stop_section_4)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(43200, today);     // noon
    CtiTime stopTime = GetTimeFromOffsetAndDate(104400, today);     // 5:00am tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed start time of 07/01/2009 12:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/01/2009 20:00:00 to 07/02/2009 06:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_2_stop_section_5)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(43200, today);     // noon
    CtiTime stopTime = GetTimeFromOffsetAndDate(129600, today);     // noon tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed start time of 07/01/2009 12:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/01/2009 20:00:00 to 07/02/2009 06:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_2_stop_section_6)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(43200, today);     // noon
    CtiTime stopTime = GetTimeFromOffsetAndDate(165600, today);     // 10:00pm tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed start time of 07/01/2009 12:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/01/2009 20:00:00 to 07/02/2009 06:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_3_stop_section_3)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(79200, today);     // 10:00pm
    CtiTime stopTime = GetTimeFromOffsetAndDate(82800, today);      // 11:00pm

    BOOST_CHECK_EQUAL(true, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(   0, constraints.getViolations().size() );
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_3_stop_section_4)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(79200, today);     // 10:00pm
    CtiTime stopTime = GetTimeFromOffsetAndDate(104400, today);     // 5:00am tomorrow

    BOOST_CHECK_EQUAL(true, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(   0, constraints.getViolations().size() );
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_3_stop_section_5)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(79200, today);     // 10:00pm
    CtiTime stopTime = GetTimeFromOffsetAndDate(129600, today);     // noon tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed stop time of 07/02/2009 12:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/01/2009 20:00:00 to 07/02/2009 06:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_3_stop_section_6)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(79200, today);     // 10:00pm
    CtiTime stopTime = GetTimeFromOffsetAndDate(165600, today);     // 10:00pm tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed stop time of 07/02/2009 22:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/01/2009 20:00:00 to 07/02/2009 06:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_4_stop_section_4)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(100800, today);    // 4:00am tomorrow
    CtiTime stopTime = GetTimeFromOffsetAndDate(104400, today);     // 5:00am tomorrow

    BOOST_CHECK_EQUAL(true, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(   0, constraints.getViolations().size() );
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_4_stop_section_5)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(100800, today);    // 4:00am tomorrow
    CtiTime stopTime = GetTimeFromOffsetAndDate(129600, today);     // noon tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed stop time of 07/02/2009 12:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/01/2009 20:00:00 to 07/02/2009 06:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_4_stop_section_6)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(100800, today);    // 4:00am tomorrow
    CtiTime stopTime = GetTimeFromOffsetAndDate(165600, today);     // 10:00pm tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed stop time of 07/02/2009 22:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/01/2009 20:00:00 to 07/02/2009 06:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_5_stop_section_5)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(129600, today);    // noon tomorrow
    CtiTime stopTime = GetTimeFromOffsetAndDate(144000, today);     // 4:00pm tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    // since our start time has moved out beyond todays window, we compare to tomorrows, error message reflects that

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed start time of 07/02/2009 12:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/02/2009 20:00:00 to 07/03/2009 06:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_5_stop_section_6)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(129600, today);    // noon tomorrow
    CtiTime stopTime = GetTimeFromOffsetAndDate(165600, today);     // 10:00pm tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    // since our start time has moved out beyond todays window, we compare to tomorrows, error message reflects that

    BOOST_CHECK_EQUAL( constraints.getViolations()[0],
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed start time of 07/02/2009 12:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/02/2009 20:00:00 to 07/03/2009 06:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_6_stop_section_6)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(165600, today);    // 10:00pm tomorrow
    CtiTime stopTime = GetTimeFromOffsetAndDate(169200, today);     // 11:00pm tomorrow

    BOOST_CHECK_EQUAL(true, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(   0, constraints.getViolations().size() );
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_6_stop_section_7)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(165600, today);    // 10:00pm tomorrow
    CtiTime stopTime = GetTimeFromOffsetAndDate(190800, today);     // 5:00am day after tomorrow

    BOOST_CHECK_EQUAL(true, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds(), today));
    BOOST_CHECK_EQUAL(   0, constraints.getViolations().size() );
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_stop_reversed)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // set the date to: July 01, 2009
    CtiDate today(1, 7, 2009);

    // define control area window from 20:00 to 06:00
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime = GetTimeFromOffsetAndDate(1800, today);      // 12:30am
    CtiTime stopTime = GetTimeFromOffsetAndDate(43200, today);      // noon

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, stopTime.seconds(), startTime.seconds(), today));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    BOOST_CHECK_EQUAL(constraints.getViolations()[0],
                      "The proposed start time of 07/01/2009 12:00:00 is after the stop time of 07/01/2009 00:30:00");
}


/*
*** TESTING: controlWindowErrorMessage()
*/
BOOST_AUTO_TEST_CASE(test_control_window_error_message_start_program_window)
{
    CtiDate date(1, 7, 2009);

    CtiTime windowStart  = GetTimeFromOffsetAndDate(14400, date);
    CtiTime windowStop   = GetTimeFromOffsetAndDate(28800, date);
    CtiTime proposedTime = GetTimeFromOffsetAndDate(43200, date);

    BOOST_CHECK_EQUAL( ControlWindowErrorMessage(windowStart, windowStop, proposedTime, "start", ""),
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed start time of 07/01/2009 12:00:00 is outside the control window that runs from "
                       "07/01/2009 04:00:00 to 07/01/2009 08:00:00");
}


BOOST_AUTO_TEST_CASE(test_control_window_error_message_stop_control_area_window)
{
    CtiDate date(1, 7, 2009);

    CtiTime windowStart  = GetTimeFromOffsetAndDate(14400, date);
    CtiTime windowStop   = GetTimeFromOffsetAndDate(28800, date);
    CtiTime proposedTime = GetTimeFromOffsetAndDate(43200, date);

    BOOST_CHECK_EQUAL( ControlWindowErrorMessage(windowStart, windowStop, proposedTime, "stop", "CONTROL AREA"),
                       "The program cannot run outside of its prescribed control windows.  "
                       "The proposed stop time of 07/01/2009 12:00:00 is outside the CONTROL AREA control window "
                       "that runs from 07/01/2009 04:00:00 to 07/01/2009 08:00:00");
}

// Note that this is nearly identical to the test below,
// the same function is really being tested twice
BOOST_AUTO_TEST_CASE(test_coerce_start_stop_time)
{
    Test_CtiLMManualControlRequestExecutor executor;

    CtiLMProgramControlWindow *window1 = new CtiLMProgramControlWindow();
    window1->setPAOId(1);
    window1->setWindowNumber(1);
    window1->setAvailableStartTime(3600);   // 1am
    window1->setAvailableStopTime(14400);   // 4am

    CtiLMProgramControlWindow *window2 = new CtiLMProgramControlWindow();
    window2->setPAOId(1);
    window2->setWindowNumber(2);
    window2->setAvailableStartTime(14460);   // 4am, 1 minute
    window2->setAvailableStopTime(28860);   // 8am, 1 minute

    CtiLMProgramBaseSPtr lmProgram = CtiLMProgramBaseSPtr(CTIDBG_new CtiLMProgramDirect());
    lmProgram->getLMProgramControlWindows().push_back(window1);
    lmProgram->getLMProgramControlWindows().push_back(window2);

    // define control area window from 02:00 to 08:00
    CtiLMControlArea controlArea;
    controlArea.setCurrentDailyStartTime(7200);
    controlArea.setCurrentDailyStopTime(28800);

    CtiTime start = CtiTime(0, 0, 0);
    CtiTime stop = CtiTime(23, 0, 0);
    executor.CoerceStartStopTime(lmProgram, start, stop, &controlArea);
    BOOST_CHECK_EQUAL(start, CtiTime::CtiTime(2, 0, 0));
    BOOST_CHECK_EQUAL(stop,  CtiTime::CtiTime(4, 0, 0));

    start = CtiTime(0,0,0);
    stop  = CtiTime(1,0,0);
    executor.CoerceStartStopTime(lmProgram, start, stop, &controlArea);
    BOOST_CHECK_EQUAL(start, CtiTime::CtiTime(0, 0, 0));
    BOOST_CHECK_EQUAL(stop, CtiTime::CtiTime(1, 0, 0));

    start = CtiTime(4,0,0);
    stop  = CtiTime(8,2,0);
    executor.CoerceStartStopTime(lmProgram, start, stop, &controlArea);
    BOOST_CHECK_EQUAL(start, CtiTime::CtiTime(4, 1, 0));
    BOOST_CHECK_EQUAL(stop, CtiTime::CtiTime(8, 0, 0));

    start = CtiTime(21,0,0);
    stop  = CtiTime(3,0,0);
    stop.addDays(1);
    executor.CoerceStartStopTime(lmProgram, start, stop, &controlArea);
    BOOST_CHECK_EQUAL(start, CtiTime::CtiTime(2, 0, 0).addDays(1));
    BOOST_CHECK_EQUAL(stop, CtiTime::CtiTime(3, 0, 0).addDays(1));

    // Note this completely bypasses one of the programs
    controlArea.setCurrentDailyStartTime(14520); // 4 h, 2 minute

    start = CtiTime(0, 0, 0);
    stop = CtiTime(23, 0, 0);
    executor.CoerceStartStopTime(lmProgram, start, stop, &controlArea);
    BOOST_CHECK_EQUAL(start, CtiTime::CtiTime(4, 2, 0));
    BOOST_CHECK_EQUAL(stop,  CtiTime::CtiTime(8, 0, 0));

    start = CtiTime(0,0,0);
    stop  = CtiTime(1,0,0);
    executor.CoerceStartStopTime(lmProgram, start, stop, &controlArea);
    BOOST_CHECK_EQUAL(start, CtiTime::CtiTime(0, 0, 0));
    BOOST_CHECK_EQUAL(stop, CtiTime::CtiTime(1, 0, 0));

    start = CtiTime(4,0,0);
    stop  = CtiTime(8,2,0);
    executor.CoerceStartStopTime(lmProgram, start, stop, &controlArea);
    BOOST_CHECK_EQUAL(start, CtiTime::CtiTime(4, 2, 0));
    BOOST_CHECK_EQUAL(stop, CtiTime::CtiTime(8, 0, 0));

    start = CtiTime(21,0,0);
    stop  = CtiTime(3,0,0);
    stop.addDays(1);
    executor.CoerceStartStopTime(lmProgram, start, stop, &controlArea);
    BOOST_CHECK_EQUAL(start, CtiTime::CtiTime(21, 0, 0));
    BOOST_CHECK_EQUAL(stop, CtiTime::CtiTime(3, 0, 0).addDays(1));

    //Bypass all programs with control area, no match at all
    // Note this completely bypasses one of the programs
    controlArea.setCurrentDailyStartTime(30000);
    controlArea.setCurrentDailyStopTime(38000);

    start = CtiTime(0, 0, 0);
    stop = CtiTime(23, 0, 0);
    executor.CoerceStartStopTime(lmProgram, start, stop, &controlArea);
    BOOST_CHECK_EQUAL(start, CtiTime::CtiTime(0, 0, 0));
    BOOST_CHECK_EQUAL(stop,  CtiTime::CtiTime(23, 0, 0));

    start = CtiTime(0,0,0);
    stop  = CtiTime(1,0,0);
    executor.CoerceStartStopTime(lmProgram, start, stop, &controlArea);
    BOOST_CHECK_EQUAL(start, CtiTime::CtiTime(0, 0, 0));
    BOOST_CHECK_EQUAL(stop, CtiTime::CtiTime(1, 0, 0));

    start = CtiTime(4,0,0);
    stop  = CtiTime(8,2,0);
    executor.CoerceStartStopTime(lmProgram, start, stop, &controlArea);
    BOOST_CHECK_EQUAL(start, CtiTime::CtiTime(4, 0, 0));
    BOOST_CHECK_EQUAL(stop, CtiTime::CtiTime(8, 2, 0));

    start = CtiTime(21,0,0);
    stop  = CtiTime(3,0,0);
    stop.addDays(1);
    executor.CoerceStartStopTime(lmProgram, start, stop, &controlArea);
    BOOST_CHECK_EQUAL(start, CtiTime::CtiTime(21, 0, 0));
    BOOST_CHECK_EQUAL(stop, CtiTime::CtiTime(3, 0, 0).addDays(1));

    // Test with no programs
    lmProgram->getLMProgramControlWindows().clear();

    controlArea.setCurrentDailyStartTime(14520); // 4 h, 2 minute
    controlArea.setCurrentDailyStopTime(28800);

    start = CtiTime(21,0,0);
    stop  = CtiTime(10,0,0);
    stop.addDays(1);
    executor.CoerceStartStopTime(lmProgram, start, stop, &controlArea);
    BOOST_CHECK_EQUAL(start, CtiTime::CtiTime(4, 2, 0).addDays(1));
    BOOST_CHECK_EQUAL(stop, CtiTime::CtiTime(8, 0, 0).addDays(1));

    controlArea.setCurrentDailyStartTime(-1);
    controlArea.setCurrentDailyStopTime(-1);

    start = CtiTime(0, 0, 0);
    stop = CtiTime(23, 0, 0);
    executor.CoerceStartStopTime(lmProgram, start, stop, &controlArea);
    BOOST_CHECK_EQUAL(start, CtiTime::CtiTime(0, 0, 0));
    BOOST_CHECK_EQUAL(stop,  CtiTime::CtiTime(23, 0, 0));

    start = CtiTime(0,0,0);
    stop  = CtiTime(1,0,0);
    executor.CoerceStartStopTime(lmProgram, start, stop, &controlArea);
    BOOST_CHECK_EQUAL(start, CtiTime::CtiTime(0, 0, 0));
    BOOST_CHECK_EQUAL(stop, CtiTime::CtiTime(1, 0, 0));

    start = CtiTime(4,0,0);
    stop  = CtiTime(8,2,0);
    executor.CoerceStartStopTime(lmProgram, start, stop, &controlArea);
    BOOST_CHECK_EQUAL(start, CtiTime::CtiTime(4, 0, 0));
    BOOST_CHECK_EQUAL(stop, CtiTime::CtiTime(8, 2, 0));

    start = CtiTime(21,0,0);
    stop  = CtiTime(3,0,0);
    stop.addDays(1);
    executor.CoerceStartStopTime(lmProgram, start, stop, &controlArea);
    BOOST_CHECK_EQUAL(start, CtiTime::CtiTime(21, 0, 0));
    BOOST_CHECK_EQUAL(stop, CtiTime::CtiTime(3, 0, 0).addDays(1));

}

// Note that this is nearly identical to the test above,
// the same function is really being tested twice
BOOST_AUTO_TEST_CASE(test_util_fit_to_window)
{
    CtiLMProgramControlWindow *window1 = new CtiLMProgramControlWindow();
    window1->setPAOId(1);
    window1->setWindowNumber(1);
    window1->setAvailableStartTime(3600);   // 1am
    window1->setAvailableStopTime(14400);   // 4am

    CtiLMProgramControlWindow *window2 = new CtiLMProgramControlWindow();
    window2->setPAOId(1);
    window2->setWindowNumber(2);
    window2->setAvailableStartTime(14460);   // 4am, 1 minute
    window2->setAvailableStopTime(28860);   // 8am, 1 minute

    CtiLMProgramBaseSPtr lmProgram = CtiLMProgramBaseSPtr(CTIDBG_new CtiLMProgramDirect());
    lmProgram->getLMProgramControlWindows().push_back(window1);
    lmProgram->getLMProgramControlWindows().push_back(window2);

    // define control area window from 02:00 to 08:00
    CtiLMControlArea controlArea;
    controlArea.setCurrentDailyStartTime(7200);
    controlArea.setCurrentDailyStopTime(28800);

    CtiTime resultStart;
    CtiTime resultStop;

    CtiTime start = CtiTime(0, 0, 0);
    CtiTime stop = CtiTime(23, 0, 0);
    BOOST_CHECK_EQUAL(FitTimeToWindows(start, stop, resultStart, resultStop, &controlArea, lmProgram), true);
    BOOST_CHECK_EQUAL(resultStart, CtiTime::CtiTime(2, 0, 0));
    BOOST_CHECK_EQUAL(resultStop,  CtiTime::CtiTime(4, 0, 0));

    start = CtiTime(0,0,0);
    stop  = CtiTime(1,0,0);
    BOOST_CHECK_EQUAL(FitTimeToWindows(start, stop, resultStart, resultStop, &controlArea, lmProgram), false);

    start = CtiTime(4,0,0);
    stop  = CtiTime(8,2,0);
    BOOST_CHECK_EQUAL(FitTimeToWindows(start, stop, resultStart, resultStop, &controlArea, lmProgram), true);
    BOOST_CHECK_EQUAL(resultStart, CtiTime::CtiTime(4, 1, 0));
    BOOST_CHECK_EQUAL(resultStop, CtiTime::CtiTime(8, 0, 0));

    start = CtiTime(21,0,0);
    stop  = CtiTime(3,0,0);
    stop.addDays(1);
    BOOST_CHECK_EQUAL(FitTimeToWindows(start, stop, resultStart, resultStop, &controlArea, lmProgram), true);
    BOOST_CHECK_EQUAL(resultStart, CtiTime::CtiTime(2, 0, 0).addDays(1));
    BOOST_CHECK_EQUAL(resultStop, CtiTime::CtiTime(3, 0, 0).addDays(1));

    // Note this completely bypasses one of the programs
    controlArea.setCurrentDailyStartTime(14520); // 4 h, 2 minute

    start = CtiTime(0, 0, 0);
    stop = CtiTime(23, 0, 0);
    BOOST_CHECK_EQUAL(FitTimeToWindows(start, stop, resultStart, resultStop, &controlArea, lmProgram), true);
    BOOST_CHECK_EQUAL(resultStart, CtiTime::CtiTime(4, 2, 0));
    BOOST_CHECK_EQUAL(resultStop,  CtiTime::CtiTime(8, 0, 0));

    start = CtiTime(0,0,0);
    stop  = CtiTime(1,0,0);
    BOOST_CHECK_EQUAL(FitTimeToWindows(start, stop, resultStart, resultStop, &controlArea, lmProgram), false);

    start = CtiTime(4,0,0);
    stop  = CtiTime(8,2,0);
    BOOST_CHECK_EQUAL(FitTimeToWindows(start, stop, resultStart, resultStop, &controlArea, lmProgram), true);
    BOOST_CHECK_EQUAL(resultStart, CtiTime::CtiTime(4, 2, 0));
    BOOST_CHECK_EQUAL(resultStop, CtiTime::CtiTime(8, 0, 0));

    start = CtiTime(21,0,0);
    stop  = CtiTime(3,0,0);
    stop.addDays(1);
    BOOST_CHECK_EQUAL(FitTimeToWindows(start, stop, resultStart, resultStop, &controlArea, lmProgram), false);

    //Bypass all programs with control area, no match at all
    // Note this completely bypasses one of the programs
    controlArea.setCurrentDailyStartTime(30000);
    controlArea.setCurrentDailyStopTime(38000);

    start = CtiTime(0, 0, 0);
    stop = CtiTime(23, 0, 0);
    BOOST_CHECK_EQUAL(FitTimeToWindows(start, stop, resultStart, resultStop, &controlArea, lmProgram), false);

    start = CtiTime(0,0,0);
    stop  = CtiTime(1,0,0);
    BOOST_CHECK_EQUAL(FitTimeToWindows(start, stop, resultStart, resultStop, &controlArea, lmProgram), false);

    start = CtiTime(4,0,0);
    stop  = CtiTime(8,2,0);
    BOOST_CHECK_EQUAL(FitTimeToWindows(start, stop, resultStart, resultStop, &controlArea, lmProgram), false);

    start = CtiTime(21,0,0);
    stop  = CtiTime(3,0,0);
    stop.addDays(1);
    BOOST_CHECK_EQUAL(FitTimeToWindows(start, stop, resultStart, resultStop, &controlArea, lmProgram), false);

    // Test with no programs
    lmProgram->getLMProgramControlWindows().clear();

    controlArea.setCurrentDailyStartTime(14520); // 4 h, 2 minute
    controlArea.setCurrentDailyStopTime(28800);

    start = CtiTime(21,0,0);
    stop  = CtiTime(10,0,0);
    stop.addDays(1);
    BOOST_CHECK_EQUAL(FitTimeToWindows(start, stop, resultStart, resultStop, &controlArea, lmProgram), true);
    BOOST_CHECK_EQUAL(resultStart, CtiTime::CtiTime(4, 2, 0).addDays(1));
    BOOST_CHECK_EQUAL(resultStop, CtiTime::CtiTime(8, 0, 0).addDays(1));

    controlArea.setCurrentDailyStartTime(-1);
    controlArea.setCurrentDailyStopTime(-1);

    start = CtiTime(0, 0, 0);
    stop = CtiTime(23, 0, 0);
    BOOST_CHECK_EQUAL(FitTimeToWindows(start, stop, resultStart, resultStop, &controlArea, lmProgram), true);
    BOOST_CHECK_EQUAL(resultStart, CtiTime::CtiTime(0, 0, 0));
    BOOST_CHECK_EQUAL(resultStop,  CtiTime::CtiTime(23, 0, 0));

    start = CtiTime(0,0,0);
    stop  = CtiTime(1,0,0);
    BOOST_CHECK_EQUAL(FitTimeToWindows(start, stop, resultStart, resultStop, &controlArea, lmProgram), true);
    BOOST_CHECK_EQUAL(resultStart, CtiTime::CtiTime(0, 0, 0));
    BOOST_CHECK_EQUAL(resultStop, CtiTime::CtiTime(1, 0, 0));

    start = CtiTime(4,0,0);
    stop  = CtiTime(8,2,0);
    BOOST_CHECK_EQUAL(FitTimeToWindows(start, stop, resultStart, resultStop, &controlArea, lmProgram), true);
    BOOST_CHECK_EQUAL(resultStart, CtiTime::CtiTime(4, 0, 0));
    BOOST_CHECK_EQUAL(resultStop, CtiTime::CtiTime(8, 2, 0));

    start = CtiTime(21,0,0);
    stop  = CtiTime(3,0,0);
    stop.addDays(1);
    BOOST_CHECK_EQUAL(FitTimeToWindows(start, stop, resultStart, resultStop, &controlArea, lmProgram), true);
    BOOST_CHECK_EQUAL(resultStart, CtiTime::CtiTime(21, 0, 0));
    BOOST_CHECK_EQUAL(resultStop, CtiTime::CtiTime(3, 0, 0).addDays(1));

}
