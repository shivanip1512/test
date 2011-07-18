/*-----------------------------------------------------------------------------*
*
* File:   test_logger
*
* Date:   10/22/2008
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/resolvers.cpp-arc  $
* REVISION     :  $Revision: 1.2.2.1 $
* DATE         :  $Date: 2008/11/12 17:27:31 $
*
* Copyright (c) 2007 Cannon Technologies. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"

#define BOOST_AUTO_TEST_MAIN "Test Logger"

#include <boost/test/unit_test.hpp>

using boost::unit_test_framework::test_suite;
using namespace std;

class Test_CtiLogger : public CtiLogger
{
private:
    typedef CtiLogger Inherited;

public:

    static string   scrub(string filename)
                {  return Inherited::scrub(filename);  };
    static unsigned secondsUntilMidnight(const tm &tm_now)
                {  return Inherited::secondsUntilMidnight(tm_now);  };
    static bool     fileDateMatches(const string &filename, const unsigned month)
                {  return Inherited::fileDateMatches(filename, month);  };

    string dayFilename(const unsigned day_of_month)
                {  return Inherited::dayFilename(day_of_month);  };
};

BOOST_AUTO_TEST_CASE(test_logger)
{
    Test_CtiLogger l;

    BOOST_CHECK_EQUAL("Invalid__________filename", l.scrub("Invalid!@#$%^&*()filename"));

    l.setOutputFile("Invalid!@#$%^&*()filename");

    BOOST_CHECK_EQUAL("..\\log\\Invalid__________filename29.log", l.dayFilename(29));

    l.setOutputPath("C:\\Yukon\\Server\\Log");

    BOOST_CHECK_EQUAL("C:\\Yukon\\Server\\Log\\Invalid__________filename29.log", l.dayFilename(29));

    tm tm_check;

    tm_check.tm_hour = 0;
    tm_check.tm_min  = 0;
    tm_check.tm_sec  = 0;

    BOOST_CHECK_EQUAL(l.secondsUntilMidnight(tm_check), 86400);  //  next midnight

    tm_check.tm_hour = 1;
    tm_check.tm_min  = 0;
    tm_check.tm_sec  = 0;

    BOOST_CHECK_EQUAL(l.secondsUntilMidnight(tm_check), 82800);

    tm_check.tm_hour = 1;
    tm_check.tm_min  = 1;
    tm_check.tm_sec  = 0;

    BOOST_CHECK_EQUAL(l.secondsUntilMidnight(tm_check), 82740);

    tm_check.tm_hour = 1;
    tm_check.tm_min  = 1;
    tm_check.tm_sec  = 1;

    BOOST_CHECK_EQUAL(l.secondsUntilMidnight(tm_check), 82739);

    tm_check.tm_hour = 23;
    tm_check.tm_min  = 59;
    tm_check.tm_sec  = 59;

    BOOST_CHECK_EQUAL(l.secondsUntilMidnight(tm_check), 1);
}

