/*-----------------------------------------------------------------------------*
*
* File:   test_lmprogram
*
* Copyright (c) 2009 Cooper Industries. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "lmprogramdirect.h"
#include "lmprogramcontrolwindow.h"

#define BOOST_AUTO_TEST_MAIN "Test LM Program"
#include <boost/test/unit_test.hpp>


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

