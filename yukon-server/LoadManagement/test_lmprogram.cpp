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

