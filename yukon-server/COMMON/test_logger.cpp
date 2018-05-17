#include <boost/test/unit_test.hpp>

#include "logManager.h"
#include "ctidate.h"

BOOST_AUTO_TEST_SUITE( test_logger )

class Test_LogManager : public Cti::Logging::LogManager
{
private:
    typedef Cti::Logging::LogManager Inherited;

public:
    Test_LogManager() : Cti::Logging::LogManager("test")
    {}

    static std::string   scrub(std::string filename)
                {  return Inherited::scrub(filename);  };

    using Inherited::getFileInfo;
};

BOOST_AUTO_TEST_CASE(test_logger)
{
    Test_LogManager l;

    const Cti::Logging::FileInfo& fileInfo = l.getFileInfo();

    BOOST_CHECK_EQUAL("Invalid__________filename", l.scrub("Invalid!@#$%^&*()filename"));
}

BOOST_AUTO_TEST_CASE(test_logger_check_old_file_deletion)
{
    Test_LogManager l;

    l.setOutputPath("C:\\Yukon\\Server\\Log");
    l.setOutputFile("unit_test");

    const Cti::Logging::FileInfo& fileInfo = l.getFileInfo();

    CtiDate cutOff(30, 10, 2012);   // October 30th, 2012

    {
        // Prefixes and suffixes
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\saved.unit_test20121109.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\saved-unit_test20121109.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\saved - unit_test20121109.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\saved unit_test20121109.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121109.saved.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121109-saved.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121109 - saved.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121109 saved.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test00000001.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test01_keep!.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20_save-.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121029xlog", cutOff ) );

        // Old style naming
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test01.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test10.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test31.log", cutOff ) );

        // Range [ date < Jan 01, 2000 OR date > Dec 31, 2035 ]
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test19991231.log", cutOff ) );
            // out of range -- older than 90 days from cut off date -- save
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20360101.log",
                                                      CtiDate(31, 12, 2036) ) );
            // in range -- older than 90 days from cut off date -- delete
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20351231.log",
                                                      CtiDate(31, 12, 2036) ) );
        // Invalid dates
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121131.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20120231.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20130229.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20122201.log", cutOff ) );

        // Leap year
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20120229.log", cutOff ) );

        // Valid date -- before cutoff -- delete
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20100330.log", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20120330.log", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20120730.log", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20120930.log", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121027.log", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121028.log", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121029.log", cutOff ) );

        // Valid -- at or after cut off date -- save
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121030.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121101.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121102.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121103.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20121230.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test20140130.log", cutOff ) );

        // Case-insensitivity
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\YUKON\\SERVER\\LOG\\UNIT_TEST20121029.LOG", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\YUKON\\SERVER\\LOG\\UNIT_TEST20121030.LOG", cutOff ) );

    }

    // re-test with format 'filename_YYYYDDMM.log'

    {
        // Prefixes and suffixes
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\saved.unit_test_20121109.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\saved-unit_test_20121109.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\saved - unit_test_20121109.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\saved unit_test_20121109.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121109.saved.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121109-saved.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121109 - saved.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121109 saved.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_00000001.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_01_keep!.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20_save-.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121029xlog", cutOff ) );

        // Old style naming
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_01.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_10.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_31.log", cutOff ) );

        // Range [ date < Jan 01, 2000 OR date > Dec 31, 2035 ]
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_19991231.log", cutOff ) );
            // out of range -- older than 90 days from cut off date -- save
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20360101.log",
                                                      CtiDate(31, 12, 2036) ) );
            // in range -- older than 90 days from cut off date -- delete
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20351231.log",
                                                      CtiDate(31, 12, 2036) ) );
        // Invalid dates
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121131.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20120231.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20130229.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20122201.log", cutOff ) );

        // Leap year
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20120229.log", cutOff ) );

        // Valid date -- before cutoff -- delete
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20100330.log", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20120330.log", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20120730.log", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20120930.log", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121027.log", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121028.log", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121029.log", cutOff ) );

        // Valid -- at or after cut off date -- save
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121030.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121101.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121102.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121103.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121230.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20140130.log", cutOff ) );

        // Case-insensitivity
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\YUKON\\SERVER\\LOG\\UNIT_TEST_20121029.LOG", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\YUKON\\SERVER\\LOG\\UNIT_TEST_20121030.LOG", cutOff ) );
    }

    // re-test with format 'filename_YYYYDDMM.zip'

    {
        // Prefixes and suffixes
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\saved.unit_test_20121109.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\saved-unit_test_20121109.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\saved - unit_test_20121109.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\saved unit_test_20121109.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121109.saved.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121109-saved.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121109 - saved.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121109 saved.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_00000001.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_01_keep!.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20_save-.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121029xlog", cutOff ) );

        // Old style naming
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_01.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_10.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_31.zip", cutOff ) );

        // Range [ date < Jan 01, 2000 OR date > Dec 31, 2035 ]
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_19991231.zip", cutOff ) );
            // out of range -- older than 90 days from cut off date -- save
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20360101.zip",
                                                      CtiDate(31, 12, 2036) ) );
            // in range -- older than 90 days from cut off date -- delete
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20351231.zip",
                                                      CtiDate(31, 12, 2036) ) );
        // Invalid dates
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121131.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20120231.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20130229.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20122201.zip", cutOff ) );

        // Leap year
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20120229.zip", cutOff ) );

        // Valid date -- before cutoff -- delete
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20100330.zip", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20120330.zip", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20120730.zip", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20120930.zip", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121027.zip", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121028.zip", cutOff ) );
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121029.zip", cutOff ) );

        // Valid -- at or after cut off date -- save
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121030.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121101.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121102.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121103.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20121230.zip", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test_20140130.zip", cutOff ) );

        // Case-insensitivity
        BOOST_CHECK_EQUAL(  true, fileInfo.shouldDeleteFile( "C:\\YUKON\\SERVER\\LOG\\UNIT_TEST_20121029.ZIP", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\YUKON\\SERVER\\LOG\\UNIT_TEST_20121030.ZIP", cutOff ) );
    }

    // make sure name style 'filename.YYYYDDMM.log' wont be deleted

    {
        // in range -- older than 90 days from cut off date -- delete
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test.20351231.log", CtiDate(31, 12, 2036) ) );

        // Leap year
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test.20120229.log", cutOff ) );

        // Valid date -- before cutoff -- should not delete (wrong name format)
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test.20100330.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test.20120330.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test.20120730.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test.20120930.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test.20121027.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test.20121028.log", cutOff ) );
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\Yukon\\Server\\Log\\unit_test.20121029.log", cutOff ) );

        // Case-insensitivity
        BOOST_CHECK_EQUAL( false, fileInfo.shouldDeleteFile( "C:\\YUKON\\SERVER\\LOG\\UNIT_TEST.20121029.LOG", cutOff ) );
    }
}

BOOST_AUTO_TEST_SUITE_END()
