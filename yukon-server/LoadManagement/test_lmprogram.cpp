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

#include <string>
#include <vector>

#define BOOST_AUTO_TEST_MAIN "Test LM Program"
#include <boost/test/unit_test.hpp>


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
    CtiLMProgramControlWindow *no_window = static_cast<CtiLMProgramControlWindow *>(0);
    CtiLMProgramControlWindow *window1 = new CtiLMProgramControlWindow();

    window1->setPAOId(1);
    window1->setWindowNumber(1);
    window1->setAvailableStartTime(79200);
    window1->setAvailableStopTime(82000);

    lmProgram.getLMProgramControlWindows().push_back(window1);

    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 1200), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(78000), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79199), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79200), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79201), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(80000), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(82000), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(82001), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(83000), no_window);
}


BOOST_AUTO_TEST_CASE(test_get_control_window_overlap_two_days)
{
    CtiLMProgramDirect lmProgram;
    CtiLMProgramControlWindow *no_window = static_cast<CtiLMProgramControlWindow *>(0);
    CtiLMProgramControlWindow *window1 = new CtiLMProgramControlWindow();

    window1->setPAOId(1);
    window1->setWindowNumber(1);
    window1->setAvailableStartTime(79200);
    window1->setAvailableStopTime(90400); // 86400 + 4000

    lmProgram.getLMProgramControlWindows().push_back(window1);

    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(    0), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 2000), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 4000), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 4001), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(50000), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79199), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79200), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(86399), window1);
}


BOOST_AUTO_TEST_CASE(test_get_control_window_overlap_two_days_end_of_month)
{
    CtiLMProgramDirect lmProgram;
    CtiLMProgramControlWindow *no_window = static_cast<CtiLMProgramControlWindow *>(0);
    CtiLMProgramControlWindow *window1 = new CtiLMProgramControlWindow();

    window1->setPAOId(1);
    window1->setWindowNumber(1);
    window1->setAvailableStartTime(79200);
    window1->setAvailableStopTime(90400); // 86400 + 4000

    lmProgram.getLMProgramControlWindows().push_back(window1);

    CtiDate targetDate(31, 8, 2009);

    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(    0, targetDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 2000, targetDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 4000, targetDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow( 4001, targetDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(50000, targetDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79199, targetDate), no_window);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(79200, targetDate), window1);
    BOOST_CHECK_EQUAL( lmProgram.getControlWindow(86399, targetDate), window1);
}


BOOST_AUTO_TEST_CASE(test_get_control_window_spring_2009_dst)
{
    CtiLMProgramDirect lmProgram;
    CtiLMProgramControlWindow *no_window = static_cast<CtiLMProgramControlWindow *>(0);
    CtiLMProgramControlWindow *window1 = new CtiLMProgramControlWindow();

    window1->setPAOId(1);
    window1->setWindowNumber(1);
    window1->setAvailableStartTime(3600);   // 1am
    window1->setAvailableStopTime(14400);   // 4am

    lmProgram.getLMProgramControlWindows().push_back(window1);

    CtiDate dstDate(8, 3, 2009);     // day of dst switch - make sure window didn't get lenghtened to 5am

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
}


BOOST_AUTO_TEST_CASE(test_get_control_window_fall_2009_dst)
{
    CtiLMProgramDirect lmProgram;
    CtiLMProgramControlWindow *no_window = static_cast<CtiLMProgramControlWindow *>(0);
    CtiLMProgramControlWindow *window1 = new CtiLMProgramControlWindow();

    window1->setPAOId(1);
    window1->setWindowNumber(1);
    window1->setAvailableStartTime(3600);   // 1am
    window1->setAvailableStopTime(14400);   // 4am

    lmProgram.getLMProgramControlWindows().push_back(window1);

    CtiDate dstDate(1, 11, 2009);     // day of dst switch  - make sure window didn't get shortened to 3am

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
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_and_stop_before)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 08:00 to 16:00
    CtiTime controlAreaStartTime(8, 0, 0);  // 08:00 == 8 * 3600 == 28800
    CtiTime controlAreaStopTime(16, 0, 0);  // 16:00 == 16 * 3600 == 57600
    controlArea.setCurrentDailyStartTime(28800);
    controlArea.setCurrentDailyStopTime(57600);

    CtiTime startTime(4, 0, 0);     // 04:00
    CtiTime stopTime(6, 0, 0);      // 06:00

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("start"));                 // start time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(startTime.asString()));    // start time printed in error message

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_before_stop_in)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 08:00 to 16:00
    CtiTime controlAreaStartTime(8, 0, 0);  // 08:00 == 8 * 3600 == 28800
    CtiTime controlAreaStopTime(16, 0, 0);  // 16:00 == 16 * 3600 == 57600
    controlArea.setCurrentDailyStartTime(28800);
    controlArea.setCurrentDailyStopTime(57600);

    CtiTime startTime(4, 0, 0);     // 04:00
    CtiTime stopTime(10, 0, 0);     // 10:00

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("start"));                 // start time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(startTime.asString()));    // start time printed in error message

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_before_stop_after)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 08:00 to 16:00
    CtiTime controlAreaStartTime(8, 0, 0);  // 08:00 == 8 * 3600 == 28800
    CtiTime controlAreaStopTime(16, 0, 0);  // 16:00 == 16 * 3600 == 57600
    controlArea.setCurrentDailyStartTime(28800);
    controlArea.setCurrentDailyStopTime(57600);

    CtiTime startTime(4, 0, 0);     // 04:00
    CtiTime stopTime(20, 0, 0);     // 20:00

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("start"));                 // start time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(startTime.asString()));    // start time printed in error message

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_and_stop_in)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 08:00 to 16:00
    CtiTime controlAreaStartTime(8, 0, 0);  // 08:00 == 8 * 3600 == 28800
    CtiTime controlAreaStopTime(16, 0, 0);  // 16:00 == 16 * 3600 == 57600
    controlArea.setCurrentDailyStartTime(28800);
    controlArea.setCurrentDailyStopTime(57600);

    CtiTime startTime(10, 0, 0);    // 10:00
    CtiTime stopTime(12, 0, 0);     // 12:00

    BOOST_CHECK_EQUAL(true, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(   0, constraints.getViolations().size() );
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_in_stop_after)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 08:00 to 16:00
    CtiTime controlAreaStartTime(8, 0, 0);  // 08:00 == 8 * 3600 == 28800
    CtiTime controlAreaStopTime(16, 0, 0);  // 16:00 == 16 * 3600 == 57600
    controlArea.setCurrentDailyStartTime(28800);
    controlArea.setCurrentDailyStopTime(57600);

    CtiTime startTime(10, 0, 0);    // 10:00
    CtiTime stopTime(18, 0, 0);     // 18:00

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("stop"));                  // stop time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(stopTime.asString()));     // stop time printed in error message

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_and_stop_after)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 08:00 to 16:00
    CtiTime controlAreaStartTime(8, 0, 0);  // 08:00 == 8 * 3600 == 28800
    CtiTime controlAreaStopTime(16, 0, 0);  // 16:00 == 16 * 3600 == 57600
    controlArea.setCurrentDailyStartTime(28800);
    controlArea.setCurrentDailyStopTime(57600);

    CtiTime startTime(18, 0, 0);    // 18:00
    CtiTime stopTime(20, 0, 0);     // 20:00

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("start"));                 // start time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(startTime.asString()));    // start time printed in error message

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_and_stop_before_tomorrow)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 08:00 to 16:00
    CtiTime controlAreaStartTime(8, 0, 0);  // 08:00 == 8 * 3600 == 28800
    CtiTime controlAreaStopTime(16, 0, 0);  // 16:00 == 16 * 3600 == 57600
    controlArea.setCurrentDailyStartTime(28800);
    controlArea.setCurrentDailyStopTime(57600);

    CtiTime startTime(4, 0, 0);     // 04:00
    CtiTime stopTime(6, 0, 0);      // 06:00

    // move window to tomorrow
    startTime.addDays(1);
    stopTime.addDays(1);

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("start"));                 // start time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(startTime.asString()));    // start time printed in error message

    // since our window is tomorrow the constraint error message should report tomorrows control window
    controlAreaStartTime.addDays(1);
    controlAreaStopTime.addDays(1);

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_before_stop_in_tomorrow)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 08:00 to 16:00
    CtiTime controlAreaStartTime(8, 0, 0);  // 08:00 == 8 * 3600 == 28800
    CtiTime controlAreaStopTime(16, 0, 0);  // 16:00 == 16 * 3600 == 57600
    controlArea.setCurrentDailyStartTime(28800);
    controlArea.setCurrentDailyStopTime(57600);

    CtiTime startTime(4, 0, 0);     // 04:00
    CtiTime stopTime(10, 0, 0);     // 10:00

    // move window to tomorrow
    startTime.addDays(1);
    stopTime.addDays(1);

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("start"));                 // start time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(startTime.asString()));    // start time printed in error message

    // since our window is tomorrow the constraint error message should report tomorrows control window
    controlAreaStartTime.addDays(1);
    controlAreaStopTime.addDays(1);

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_before_stop_after_tomorrow)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 08:00 to 16:00
    CtiTime controlAreaStartTime(8, 0, 0);  // 08:00 == 8 * 3600 == 28800
    CtiTime controlAreaStopTime(16, 0, 0);  // 16:00 == 16 * 3600 == 57600
    controlArea.setCurrentDailyStartTime(28800);
    controlArea.setCurrentDailyStopTime(57600);

    CtiTime startTime(4, 0, 0);     // 04:00
    CtiTime stopTime(20, 0, 0);     // 20:00

    // move window to tomorrow
    startTime.addDays(1);
    stopTime.addDays(1);

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("start"));                 // start time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(startTime.asString()));    // start time printed in error message

    // since our window is tomorrow the constraint error message should report tomorrows control window
    controlAreaStartTime.addDays(1);
    controlAreaStopTime.addDays(1);

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_and_stop_in_tomorrow)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 08:00 to 16:00
    CtiTime controlAreaStartTime(8, 0, 0);  // 08:00 == 8 * 3600 == 28800
    CtiTime controlAreaStopTime(16, 0, 0);  // 16:00 == 16 * 3600 == 57600
    controlArea.setCurrentDailyStartTime(28800);
    controlArea.setCurrentDailyStopTime(57600);

    CtiTime startTime(10, 0, 0);    // 10:00
    CtiTime stopTime(12, 0, 0);     // 12:00

    // move window to tomorrow
    startTime.addDays(1);
    stopTime.addDays(1);

    BOOST_CHECK_EQUAL(true, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(   0, constraints.getViolations().size() );
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_in_stop_after_tomorrow)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 08:00 to 16:00
    CtiTime controlAreaStartTime(8, 0, 0);  // 08:00 == 8 * 3600 == 28800
    CtiTime controlAreaStopTime(16, 0, 0);  // 16:00 == 16 * 3600 == 57600
    controlArea.setCurrentDailyStartTime(28800);
    controlArea.setCurrentDailyStopTime(57600);

    CtiTime startTime(10, 0, 0);    // 04:00
    CtiTime stopTime(18, 0, 0);     // 20:00

    // move window to tomorrow
    startTime.addDays(1);
    stopTime.addDays(1);

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("stop"));                  // stop time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(stopTime.asString()));     // stop time printed in error message

    // since our window is tomorrow the constraint error message should report tomorrows control window
    controlAreaStartTime.addDays(1);
    controlAreaStopTime.addDays(1);

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_no_midnight_overlap_start_and_stop_after_tomorrow)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 08:00 to 16:00
    CtiTime controlAreaStartTime(8, 0, 0);  // 08:00 == 8 * 3600 == 28800
    CtiTime controlAreaStopTime(16, 0, 0);  // 16:00 == 16 * 3600 == 57600
    controlArea.setCurrentDailyStartTime(28800);
    controlArea.setCurrentDailyStopTime(57600);

    CtiTime startTime(18, 0, 0);    // 18:00
    CtiTime stopTime(20, 0, 0);     // 20:00

    // move window to tomorrow
    startTime.addDays(1);
    stopTime.addDays(1);

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("start"));                 // start time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(startTime.asString()));    // start time printed in error message

    // since our window is tomorrow the constraint error message should report tomorrows control window
    controlAreaStartTime.addDays(1);
    controlAreaStopTime.addDays(1);

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
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

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(0, 30, 0);    // 12:30am
    CtiTime stopTime(5, 0, 0);      // 5:00am

    BOOST_CHECK_EQUAL(true, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(   0, constraints.getViolations().size() );
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_1_stop_section_2)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(0, 30, 0);    // 12:30am
    CtiTime stopTime(12, 0, 0);     // noon

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("stop"));                  // stop time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(stopTime.asString()));     // stop time printed in error message

    // since our start time falls in yesterdays window, the constraint error message should report yesterdays control window
    controlAreaStartTime.addDays(-1);
    controlAreaStopTime.addDays(-1);

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_1_stop_section_3)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(0, 30, 0);    // 12:30am
    CtiTime stopTime(22, 0, 0);     // 10:00pm

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("stop"));                  // stop time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(stopTime.asString()));     // stop time printed in error message

    // since our start time falls in yesterdays window, the constraint error message should report yesterdays control window
    controlAreaStartTime.addDays(-1);
    controlAreaStopTime.addDays(-1);

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_1_stop_section_4)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(0, 30, 0);    // 12:30am
    CtiTime stopTime(5, 0, 0);      // 5:00am
    stopTime.addDays(1);            // 5:00am tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("stop"));                  // stop time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(stopTime.asString()));     // stop time printed in error message

    // since our start time falls in yesterdays window, the constraint error message should report yesterdays control window
    controlAreaStartTime.addDays(-1);
    controlAreaStopTime.addDays(-1);

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_1_stop_section_5)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(0, 30, 0);    // 12:30am
    CtiTime stopTime(12, 0, 0);     // noon
    stopTime.addDays(1);            // noon tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("stop"));                  // stop time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(stopTime.asString()));     // stop time printed in error message

    // since our start time falls in yesterdays window, the constraint error message should report yesterdays control window
    controlAreaStartTime.addDays(-1);
    controlAreaStopTime.addDays(-1);

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_1_stop_section_6)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(0, 30, 0);    // 12:30am
    CtiTime stopTime(22, 0, 0);     // 10:00pm
    stopTime.addDays(1);            // 10:00pm tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("stop"));                  // stop time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(stopTime.asString()));     // stop time printed in error message

    // since our start time falls in yesterdays window, the constraint error message should report yesterdays control window
    controlAreaStartTime.addDays(-1);
    controlAreaStopTime.addDays(-1);

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_2_stop_section_2)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(12, 0, 0);    // noon
    CtiTime stopTime(14, 0, 0);     // 2:00pm

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("start"));                  // start time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(startTime.asString()));     // start time printed in error message

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_2_stop_section_3)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(12, 0, 0);    // noon
    CtiTime stopTime(22, 0, 0);     // 10:00pm

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("start"));                  // start time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(startTime.asString()));     // start time printed in error message

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_2_stop_section_4)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(12, 0, 0);    // noon
    CtiTime stopTime(5, 0, 0);      // 5:00am
    stopTime.addDays(1);            // 5:00am tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("start"));                  // start time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(startTime.asString()));     // start time printed in error message

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_2_stop_section_5)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(12, 0, 0);    // noon
    CtiTime stopTime(12, 0, 0);     // noon
    stopTime.addDays(1);            // noon tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("start"));                  // start time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(startTime.asString()));     // start time printed in error message

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_2_stop_section_6)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(12, 0, 0);    // noon
    CtiTime stopTime(22, 0, 0);     // 10:00pm
    stopTime.addDays(1);            // 10:00pm tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("start"));                  // start time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(startTime.asString()));     // start time printed in error message

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_3_stop_section_3)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(22, 0, 0);    // 10:00pm
    CtiTime stopTime(23, 0, 0);     // 11:00pm

    BOOST_CHECK_EQUAL(true, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(   0, constraints.getViolations().size() );
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_3_stop_section_4)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(22, 0, 0);    // 10:00pm
    CtiTime stopTime(5, 0, 0);      // 5:00am
    stopTime.addDays(1);            // 5:00am tomorrow

    BOOST_CHECK_EQUAL(true, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(   0, constraints.getViolations().size() );
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_3_stop_section_5)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(22, 0, 0);    // 10:00pm
    CtiTime stopTime(12, 0, 0);     // noon
    stopTime.addDays(1);            // noon tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("stop"));                  // stop time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(stopTime.asString()));     // stop time printed in error message

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_3_stop_section_6)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(22, 0, 0);    // 10:00pm
    CtiTime stopTime(22, 0, 0);     // 10:00pm
    stopTime.addDays(1);            // 10:00pm tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("stop"));                  // stop time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(stopTime.asString()));     // stop time printed in error message

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_4_stop_section_4)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(4, 0, 0);     // 4:00am
    startTime.addDays(1);           // 4:00am tomorrow
    CtiTime stopTime(5, 0, 0);      // 5:00am
    stopTime.addDays(1);            // 5:00am tomorrow

    BOOST_CHECK_EQUAL(true, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(   0, constraints.getViolations().size() );
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_4_stop_section_5)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(4, 0, 0);     // 4:00am
    startTime.addDays(1);           // 4:00am tomorrow
    CtiTime stopTime(12, 0, 0);     // noon
    stopTime.addDays(1);            // noon tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("stop"));                  // stop time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(stopTime.asString()));     // stop time printed in error message

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_4_stop_section_6)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(4, 0, 0);     // 4:00am
    startTime.addDays(1);           // 4:00am tomorrow
    CtiTime stopTime(22, 0, 0);     // 10:00pm
    stopTime.addDays(1);            // 10:00pm tomorrow
 
    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("stop"));                  // stop time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(stopTime.asString()));     // stop time printed in error message

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_5_stop_section_5)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(12, 0, 0);    // noon
    startTime.addDays(1);           // noon tomorrow
    CtiTime stopTime(16, 0, 0);     // 4:00pm
    stopTime.addDays(1);            // 4:00pm tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("start"));                  // start time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(startTime.asString()));     // start time printed in error message

    // since our start time has moved out beyond todays window, we compare to tomorrows, error message reflects that
    controlAreaStartTime.addDays(1);
    controlAreaStopTime.addDays(1);

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_5_stop_section_6)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(12, 0, 0);    // noon
    startTime.addDays(1);           // noon tomorrow
    CtiTime stopTime(22, 0, 0);     // 10:00pm
    stopTime.addDays(1);            // 10:00pm tomorrow

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    BOOST_CHECK_NE(std::string::npos, violation.find("start"));                  // start time violation
    BOOST_CHECK_NE(std::string::npos, violation.find(startTime.asString()));     // start time printed in error message

    // since our start time has moved out beyond todays window, we compare to tomorrows, error message reflects that
    controlAreaStartTime.addDays(1);
    controlAreaStopTime.addDays(1);

    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStartTime.asString()));     // control area start time in error message
    BOOST_CHECK_NE(std::string::npos, violation.find(controlAreaStopTime.asString()));      // control area stop time in error message
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_6_stop_section_6)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(22, 0, 0);    // 10:00pm
    startTime.addDays(1);           // 10:00pm tomorrow
    CtiTime stopTime(23, 0, 0);     // 11:00pm
    stopTime.addDays(1);            // 11:00pm tomorrow

    BOOST_CHECK_EQUAL(true, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(   0, constraints.getViolations().size() );
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_section_6_stop_section_7)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(22, 0, 0);    // 10:00pm
    startTime.addDays(1);           // 10:00pm tomorrow
    CtiTime stopTime(5, 0, 0);      // 5:00am
    stopTime.addDays(2);            // 5:00am 2 days from now

    BOOST_CHECK_EQUAL(true, constraints.checkControlAreaControlWindows(controlArea, startTime.seconds(), stopTime.seconds()));
    BOOST_CHECK_EQUAL(   0, constraints.getViolations().size() );
}


BOOST_AUTO_TEST_CASE(test_control_area_constraint_check_with_midnight_overlap_start_stop_reversed)
{
    CtiLMProgramDirect lmProgram;
    CtiLMControlArea controlArea;

    CtiLMProgramConstraintChecker constraints(lmProgram, CtiTime().seconds());

    // define control area window from 20:00 to 06:00
    CtiTime controlAreaStartTime(20, 0, 0);
    CtiTime controlAreaStopTime(6, 0, 0);
    controlAreaStopTime.addDays(1);     // move it to tomorrow morning

    controlArea.setCurrentDailyStartTime(72000);    // 20:00 == 20 * 3600 == 72000
    controlArea.setCurrentDailyStopTime(108000);    // 06:00 ==  6 * 3600 == 21600 + 86400 == 108000 --  tomorrow morning

    CtiTime startTime(0, 30, 0);    // 12:30am
    CtiTime stopTime(12, 0, 0);     // noon

    BOOST_CHECK_EQUAL(false, constraints.checkControlAreaControlWindows(controlArea, stopTime.seconds(), startTime.seconds()));
    BOOST_CHECK_EQUAL(    1, constraints.getViolations().size() );

    std::string violation = constraints.getViolations()[0];

    /*  Violation:                 ( stopTime        )                           ( startTime       )
        The proposed start time of mm/dd/yyyy hh:mm:ss is after the stop time of mm/dd/yyyy hh:mm:ss
    */

    BOOST_CHECK_LT(violation.find("start"), violation.find("stop"));    // message contains 'start' and 'stop' in that order
    BOOST_CHECK_LT(violation.find(stopTime.asString()), violation.find(startTime.asString()));  // stop time printed first
}

