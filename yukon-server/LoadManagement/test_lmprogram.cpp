/*-----------------------------------------------------------------------------*
*
* File:   test_lmprogram
*
* Date:   10/22/2008
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/resolvers.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2008/10/28 19:21:39 $
*
* Copyright (c) 2007 Cannon Technologies. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "lmprogramdirect.h"
#include "lmprogramcontrolwindow.h"
#include <vector>

#define BOOST_AUTO_TEST_MAIN "Test LM Program"

#include <boost/test/unit_test.hpp>

BOOST_AUTO_TEST_CASE(test_get_control_window)
{
    CtiLMProgramControlWindow *window1 = new CtiLMProgramControlWindow();
    CtiLMProgramControlWindow *resultWindow;
    window1->setPAOId(1);
    window1->setWindowNumber(1);
    window1->setAvailableStartTime(79200);
    window1->setAvailableStopTime(82000);

    std::vector<CtiLMProgramControlWindow*> *controlWindowVec;
    CtiLMProgramDirect lmProgram;

    lmProgram.getLMProgramControlWindows().push_back(window1);
    resultWindow = lmProgram.getControlWindow(1200);
    BOOST_CHECK_EQUAL((void *)resultWindow, (void *)NULL);
    resultWindow = lmProgram.getControlWindow(79199);
    BOOST_CHECK_EQUAL((void *)resultWindow, (void *)NULL);
    resultWindow = lmProgram.getControlWindow(82001);
    BOOST_CHECK_EQUAL((void *)resultWindow, (void *)NULL);
    resultWindow = lmProgram.getControlWindow(79200);
    BOOST_CHECK_EQUAL((void *)resultWindow, (void *)window1);

    // Loops a day boundary
    window1->setAvailableStartTime(79200);
    window1->setAvailableStopTime(4000);

    resultWindow = lmProgram.getControlWindow(1200);
    BOOST_CHECK_EQUAL((void *)resultWindow, (void *)window1);
    resultWindow = lmProgram.getControlWindow(79199);
    BOOST_CHECK_EQUAL((void *)resultWindow, (void *)NULL);
    resultWindow = lmProgram.getControlWindow(4001);
    BOOST_CHECK_EQUAL((void *)resultWindow, (void *)NULL);
    resultWindow = lmProgram.getControlWindow(3999);
    BOOST_CHECK_EQUAL((void *)resultWindow, (void *)window1);
    resultWindow = lmProgram.getControlWindow(79200);
    BOOST_CHECK_EQUAL((void *)resultWindow, (void *)window1);

}
