#include <boost/test/unit_test.hpp>

#include "logger.h"
#include "ctidate.h"

BOOST_AUTO_TEST_SUITE( test_logger )

class Test_CtiLogger : public CtiLogger
{
private:
    typedef CtiLogger Inherited;

public:

    static std::string   scrub(std::string filename)
                {  return Inherited::scrub(filename);  };
    static unsigned secondsUntilMidnight(const CtiTime & tm_now)
                {  return Inherited::secondsUntilMidnight(tm_now);  };

    using Inherited::logFilename;
    using Inherited::shouldDeleteFile;
};

BOOST_AUTO_TEST_CASE(test_logger)
{
    Test_CtiLogger l;

    CtiDate date(29, 4, 2012);  // April 29th, 2012

    BOOST_CHECK_EQUAL("Invalid__________filename", l.scrub("Invalid!@#$%^&*()filename"));

    l.setOutputFile("Invalid!@#$%^&*()filename");

    BOOST_CHECK_EQUAL("..\\log\\Invalid__________filename20120429.log", l.logFilename(date));

    l.setOutputPath("C:\\Yukon\\Server\\Log");

    BOOST_CHECK_EQUAL("C:\\Yukon\\Server\\Log\\Invalid__________filename20120429.log", l.logFilename(date));


    BOOST_CHECK_EQUAL(l.secondsUntilMidnight(date), 86400);                     // 00:00:00

    BOOST_CHECK_EQUAL(l.secondsUntilMidnight(CtiTime(date, 1, 0, 0)), 82800);   // 01:00:00

    BOOST_CHECK_EQUAL(l.secondsUntilMidnight(CtiTime(date, 1, 1, 0)), 82740);   // 01:01:00

    BOOST_CHECK_EQUAL(l.secondsUntilMidnight(CtiTime(date, 1, 1, 1)), 82739);   // 01:01:01

    BOOST_CHECK_EQUAL(l.secondsUntilMidnight(CtiTime(date, 23, 59, 59)), 1);    // 23:59:59
}

BOOST_AUTO_TEST_CASE(test_logger_full_date_on_filename)
{
    Test_CtiLogger l;

    l.setOutputPath("C:\\Yukon\\Server\\Log");
    l.setOutputFile("unit_test");

    // checking that all the zero padding works...

    // April 9th, 2012

    BOOST_CHECK_EQUAL("C:\\Yukon\\Server\\Log\\unit_test20120409.log", l.logFilename( CtiDate(9, 4, 2012) ));

    // April 19th, 2012

    BOOST_CHECK_EQUAL("C:\\Yukon\\Server\\Log\\unit_test20120419.log", l.logFilename( CtiDate(19, 4, 2012) ));

    // November 9th, 2012

    BOOST_CHECK_EQUAL("C:\\Yukon\\Server\\Log\\unit_test20121109.log", l.logFilename( CtiDate(9, 11, 2012) ));

    // November 19th, 2012

    BOOST_CHECK_EQUAL("C:\\Yukon\\Server\\Log\\unit_test20121119.log", l.logFilename( CtiDate(19, 11, 2012) ));
}

BOOST_AUTO_TEST_CASE(test_logger_check_old_file_deletion)
{
    Test_CtiLogger l;

    l.setOutputPath("C:\\Yukon\\Server\\Log");
    l.setOutputFile("unit_test");

    CtiDate cutOff(30, 10, 2012);   // October 30th, 2012

    // Prefixes and suffixes
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\saved.unit_test20121109.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\saved-unit_test20121109.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\saved - unit_test20121109.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\saved unit_test20121109.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121109.saved.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121109-saved.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121109 - saved.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121109 saved.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test00000001.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test01_keep!.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20_save-.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121029xlog", cutOff ) );

    // Old style naming
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test01.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test10.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test31.log", cutOff ) );

    // Range [ date < Jan 01, 2000 OR date > Dec 31, 2035 ]
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test19991231.log", cutOff ) );
        // out of range -- older than 90 days from cut off date -- save
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20360101.log",
                                                  CtiDate(31, 12, 2036) ) );
        // in range -- older than 90 days from cut off date -- delete
    BOOST_CHECK_EQUAL(  true, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20351231.log",
                                                  CtiDate(31, 12, 2036) ) );
    // Invalid dates
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121131.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20120231.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20130229.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20122201.log", cutOff ) );

    // Leap year
    BOOST_CHECK_EQUAL(  true, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20120229.log", cutOff ) );

    // Valid date -- before cutoff -- delete
    BOOST_CHECK_EQUAL(  true, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20100330.log", cutOff ) );
    BOOST_CHECK_EQUAL(  true, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20120330.log", cutOff ) );
    BOOST_CHECK_EQUAL(  true, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20120730.log", cutOff ) );
    BOOST_CHECK_EQUAL(  true, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20120930.log", cutOff ) );
    BOOST_CHECK_EQUAL(  true, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121027.log", cutOff ) );
    BOOST_CHECK_EQUAL(  true, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121028.log", cutOff ) );
    BOOST_CHECK_EQUAL(  true, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121029.log", cutOff ) );

    // Valid -- at or after cut off date -- save
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121030.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121101.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121102.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121103.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121230.log", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20140130.log", cutOff ) );

    // Case-insensitivity
    BOOST_CHECK_EQUAL(  true, l.shouldDeleteFile( "C:\\YUKON\\SERVER\\LOG\\UNIT_TEST20121029.LOG", cutOff ) );
    BOOST_CHECK_EQUAL( false, l.shouldDeleteFile( "C:\\YUKON\\SERVER\\LOG\\UNIT_TEST20121030.LOG", cutOff ) );
}

BOOST_AUTO_TEST_SUITE_END()
