#include <boost/test/unit_test.hpp>

#include "logger.h"

using namespace std;

BOOST_AUTO_TEST_SUITE( test_logger )

class Test_CtiLogger : public CtiLogger
{
private:
    typedef CtiLogger Inherited;

public:

    static string   scrub(string filename)
                {  return Inherited::scrub(filename);  };
    static unsigned secondsUntilMidnight(const tm &tm_now)
                {  return Inherited::secondsUntilMidnight(tm_now);  };
    string logFilename(const tm &tm_now)
    {
        return Inherited::logFilename(tm_now);
    }
};

BOOST_AUTO_TEST_CASE(test_logger)
{
    Test_CtiLogger l;

    tm tm_check;

    tm_check.tm_year = 112;     // April 29th, 2012
    tm_check.tm_mon  = 3;
    tm_check.tm_mday = 29;      

    BOOST_CHECK_EQUAL("Invalid__________filename", l.scrub("Invalid!@#$%^&*()filename"));

    l.setOutputFile("Invalid!@#$%^&*()filename");

    BOOST_CHECK_EQUAL("..\\log\\Invalid__________filename20120429.log", l.logFilename(tm_check));

    l.setOutputPath("C:\\Yukon\\Server\\Log");

    BOOST_CHECK_EQUAL("C:\\Yukon\\Server\\Log\\Invalid__________filename20120429.log", l.logFilename(tm_check));

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

BOOST_AUTO_TEST_CASE(test_logger_full_date_on_filename)
{
    Test_CtiLogger l;

    l.setOutputPath("C:\\Yukon\\Server\\Log");
    l.setOutputFile("unit_test");

    tm tm_check;

    tm_check.tm_year = 112;     // 2012

    // checking that all the zero padding works...

    tm_check.tm_mon  = 3;
    tm_check.tm_mday = 9;

    BOOST_CHECK_EQUAL("C:\\Yukon\\Server\\Log\\unit_test20120409.log", l.logFilename(tm_check));

    tm_check.tm_mday = 19;

    BOOST_CHECK_EQUAL("C:\\Yukon\\Server\\Log\\unit_test20120419.log", l.logFilename(tm_check));

    tm_check.tm_mon  = 10;
    tm_check.tm_mday = 9;

    BOOST_CHECK_EQUAL("C:\\Yukon\\Server\\Log\\unit_test20121109.log", l.logFilename(tm_check));

    tm_check.tm_mday = 19;

    BOOST_CHECK_EQUAL("C:\\Yukon\\Server\\Log\\unit_test20121119.log", l.logFilename(tm_check));
}

BOOST_AUTO_TEST_SUITE_END()
