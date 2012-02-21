#include <boost/test/auto_unit_test.hpp>

#include "decodetextcmdfile.h"

using std::string;
using std::endl;

BOOST_AUTO_TEST_SUITE( test_decodetextcmdfile )

BOOST_AUTO_TEST_CASE(test_validateAndDecodeLine)
{
    const string filename = "unusedTestFileName";
    string input, expectedCommand;
    bool result;
    RWCollectableString* decodedCommand = new RWCollectableString();

    input = "6,TARGET,spid 1,geo 2,sub 3,feeder 4,feeder 1,zip 5,uda 6,program 7,splinter 8,"
            "ASSIGN,spid 10,geo 11,sub 12,feeder 2,zip 14,uda 15,program 16,splinter 17, relay 2";
    expectedCommand = "set MessagePriority 5 ; PutConfig xcom target "
                     "spid 1 geo 2 sub 3 zip 5 uda 6 program 7 splinter 8 feeder 9 "
                     "assign spid 10 geo 11 sub 12 zip 14 uda 15 program 16 splinter 17 relay 2 feeder 2";

    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(expectedCommand.c_str(), decodedCommand->data());
    BOOST_CHECK_EQUAL(result, true);

    input = "6,TARGET,serial 12345456,"
            "ASSIGN,spid 10,geo 11,sub 12,feeder 16,feeder 1,zip 14,uda 15,program 16,splinter 17, relay 2";
    expectedCommand = "set MessagePriority 5 ; PutConfig xcom target "
                     "serial 12345456 "
                     "assign spid 10 geo 11 sub 12 zip 14 uda 15 program 16 splinter 17 relay 2 feeder 32769";

    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(expectedCommand.c_str(), decodedCommand->data());
    BOOST_CHECK_EQUAL(result, true);


    input = "6,TARGET,spid 1,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,spid 65534,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,spid 65535,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,spid 0,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);

    input = "6,TARGET,geo 1,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,geo 65534,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,geo 65535,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,geo 0,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);

    input = "6,TARGET,sub 1,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,sub 65534,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,sub 65535,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,sub 0,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);

    input = "6,TARGET,feeder 1,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,feeder 16,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,feeder 17,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,feeder 0,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);

    input = "6,TARGET,zip 1,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,zip 16777214,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,zip 16777215,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,zip 0,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);

    input = "6,TARGET,uda 1,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,uda 65534,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,uda 65535,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,uda 0,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);

    input = "6,TARGET,user 1,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,user 65534,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,user 65535,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,user 0,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);

    input = "6,TARGET,program 1,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,program 254,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,program 255,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,program 0,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);

    input = "6,TARGET,splinter 1,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,splinter 254,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,splinter 255,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,splinter 0,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);


    //test assign portion
    input = "6,TARGET,serial 1,assign, spid 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, spid 65534";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, spid 65535";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,serial 1,assign, spid 0";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);

    input = "6,TARGET,serial 1,assign, geo 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, geo 65534";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, geo 65535";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,serial 1,assign, geo 0";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);

    input = "6,TARGET,serial 1,assign, sub 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, sub 65534";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, sub 65535";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,serial 1,assign, sub 0";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);

    input = "6,TARGET,serial 1,assign, feeder 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, feeder 16";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, feeder 17";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,serial 1,assign, feeder 0";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);

    input = "6,TARGET,serial 1,assign, zip 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, zip 16777214";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, zip 16777215";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,serial 1,assign, zip 0";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);

    input = "6,TARGET,serial 1,assign, uda 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, uda 65534";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, uda 65535";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,serial 1,assign, uda 0";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);

    input = "6,TARGET,serial 1,assign, user 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, user 65534";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, user 65535";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,serial 1,assign, user 0";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);

    input = "6,TARGET,serial 1,assign, program 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, splinter 254";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, program 255";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,serial 1,assign, program 0";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);

    input = "6,TARGET,serial 1,assign, splinter 1";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, splinter 254";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, splinter 255";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
    input = "6,TARGET,serial 1,assign, splinter 0";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);

    input = "6,TARGET,serial 1,assign, relay 0";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, relay 15";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, true);
    input = "6,TARGET,serial 1,assign, relay 16";
    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(result, false);
}

BOOST_AUTO_TEST_SUITE_END()
