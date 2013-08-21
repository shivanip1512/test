#include <boost/test/auto_unit_test.hpp>

#include "decodetextcmdfile.h"

#include <boost/assign/list_of.hpp>

using std::string;
using std::endl;

BOOST_AUTO_TEST_SUITE( test_decodetextcmdfile )

BOOST_AUTO_TEST_CASE(test_validateAndDecodeLine_1)
{
    const string filename = "unusedTestFileName";

    string input = "1,123456,test group name";

    const string expected = "set MessagePriority 5 ; PutConfig serial 123456 template 'test group name'";

    string result;

    BOOST_CHECK(validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, &result, filename));
    BOOST_CHECK_EQUAL(expected, result);
}

BOOST_AUTO_TEST_CASE(test_validateAndDecodeLine_2)
{
    const string filename = "unusedTestFileName";

    const std::vector<std::string>
        opt1 = boost::assign::list_of(",in")(",out"),
        opt2 = boost::assign::list_of("")(",temp"),
        opt3 = boost::assign::list_of("")(",offhours:7");
    const std::vector<int>
        protocolflags = boost::assign::list_of(0)(1)(2);

    std::vector<std::string> resultProgramming;
    std::vector<bool> resultSuccess;

    for each(const int protocolflag in protocolflags)
    {
        for each(const std::string &o1 in opt1)
        {
            for each(const std::string &o2 in opt2)
            {
                for each(const std::string &o3 in opt3)
                {
                    string input = "2,123456";
                    input += o1;
                    input += o2;
                    input += o3;

                    string decodedCommand;

                    bool success = validateAndDecodeLine(input, protocolflag, &decodedCommand, filename);

                    resultSuccess.push_back(success);
                    resultProgramming.push_back(decodedCommand);
                }
            }
        }
    }

    const std::vector<std::string> expectedProgramming = boost::assign::list_of
        ("set MessagePriority 7 ; PutConfig versacom serial 123456 service in")
        ("set MessagePriority 7 ; PutConfig versacom serial 123456 service in temp offhours 7")
        ("set MessagePriority 7 ; PutConfig versacom serial 123456 service in temp ")
        ("set MessagePriority 7 ; PutConfig versacom serial 123456 service in temp offhours 7")
        ("set MessagePriority 7 ; PutConfig versacom serial 123456 service out")
        ("set MessagePriority 7 ; PutConfig versacom serial 123456 service out temp offhours 7")
        ("set MessagePriority 7 ; PutConfig versacom serial 123456 service out temp ")
        ("set MessagePriority 7 ; PutConfig versacom serial 123456 service out temp offhours 7")
        ("set MessagePriority 7 ; PutConfig xcom serial 123456 service in")
        ("set MessagePriority 7 ; PutConfig xcom serial 123456 service in temp offhours 7")
        ("set MessagePriority 7 ; PutConfig xcom serial 123456 service in temp ")
        ("set MessagePriority 7 ; PutConfig xcom serial 123456 service in temp offhours 7")
        ("set MessagePriority 7 ; PutConfig xcom serial 123456 service out")
        ("set MessagePriority 7 ; PutConfig xcom serial 123456 service out temp offhours 7")
        ("set MessagePriority 7 ; PutConfig xcom serial 123456 service out temp ")
        ("set MessagePriority 7 ; PutConfig xcom serial 123456 service out temp offhours 7")
        ("set MessagePriority 7 ; PutConfig serial 123456 service in")
        ("set MessagePriority 7 ; PutConfig serial 123456 service in temp offhours 7")
        ("set MessagePriority 7 ; PutConfig serial 123456 service in temp ")
        ("set MessagePriority 7 ; PutConfig serial 123456 service in temp offhours 7")
        ("set MessagePriority 7 ; PutConfig serial 123456 service out")
        ("set MessagePriority 7 ; PutConfig serial 123456 service out temp offhours 7")
        ("set MessagePriority 7 ; PutConfig serial 123456 service out temp ")
        ("set MessagePriority 7 ; PutConfig serial 123456 service out temp offhours 7");
    const std::vector<bool> expectedSuccess = boost::assign::list_of
        (true).repeat(23, true);

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expectedSuccess.begin(), expectedSuccess.end(),
       resultSuccess.begin(),   resultSuccess.end());

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expectedProgramming.begin(), expectedProgramming.end(),
       resultProgramming.begin(),   resultProgramming.end());
}

BOOST_AUTO_TEST_CASE(test_validateAndDecodeLine_3)
{
    const string filename = "unusedTestFileName";
    bool result;

    const std::vector<std::string>
        opt1 = boost::assign::list_of("")(",s1")(",s1,s2"),
        opt2 = boost::assign::list_of("")(",c1")(",c1,c2"),
        opt3 = boost::assign::list_of("")(",d1")(",d1,d2");
    const std::vector<int>
        protocolflags = boost::assign::list_of(0)(1)(2);

    std::vector<std::string> resultProgramming;
    std::vector<bool> resultSuccess;

    for each(const int protocolflag in protocolflags)
    {
        for each(const std::string &o1 in opt1)
        {
            for each(const std::string &o2 in opt2)
            {
                for each(const std::string &o3 in opt3)
                {
                    string input = "3,123456";
                    input += o1;
                    input += o2;
                    input += o3;

                    string decodedCommand;

                    bool success = validateAndDecodeLine(input, protocolflag, &decodedCommand, filename);

                    resultSuccess.push_back(success);
                    resultProgramming.push_back(decodedCommand);
                }
            }
        }
    }

    const std::vector<std::string> expectedProgramming = boost::assign::list_of
        ("")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 division 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 class 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 class 1 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 class 1 division 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 class 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 class 1,2 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 class 1,2 division 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1 division 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1 class 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1 class 1 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1 class 1 division 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1 class 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1 class 1,2 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1 class 1,2 division 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2 division 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2 class 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2 class 1 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2 class 1 division 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2 class 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2 class 1,2 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2 class 1,2 division 1,2")
        .repeat(27,"")
        ("")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 division 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 class 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 class 1 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 class 1 division 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 class 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 class 1,2 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 class 1,2 division 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1 division 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1 class 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1 class 1 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1 class 1 division 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1 class 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1 class 1,2 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 1 class 1,2 division 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2 division 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2 class 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2 class 1 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2 class 1 division 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2 class 1,2")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2 class 1,2 division 1")
        ("set MessagePriority 5 ; PutConfig versacom serial 123456 section 2 class 1,2 division 1,2");

    const std::vector<bool> expectedSuccess = boost::assign::list_of
        (false).repeat(26, true)
        (false).repeat(26, false)
        (false).repeat(26, true);

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expectedSuccess.begin(), expectedSuccess.end(),
       resultSuccess.begin(),   resultSuccess.end());

    BOOST_CHECK_EQUAL_COLLECTIONS(
       expectedProgramming.begin(), expectedProgramming.end(),
       resultProgramming.begin(),   resultProgramming.end());
}

BOOST_AUTO_TEST_CASE(test_validateAndDecodeLine_6)
{
    const string filename = "unusedTestFileName";
    string input, expectedCommand;
    bool result;
    std::string *decodedCommand = new std::string();

    input = "6,TARGET,spid 1,geo 2,sub 3,feeder 4,feeder 1,zip 5,uda 6,program 7,splinter 8,"
            "ASSIGN,spid 10,geo 11,sub 12,feeder 2,zip 14,uda 15,program 16,splinter 17, relay 2";
    expectedCommand = "set MessagePriority 5 ; PutConfig xcom target "
                     "spid 1 geo 2 sub 3 zip 5 uda 6 program 7 splinter 8 feeder 9 "
                     "assign spid 10 geo 11 sub 12 zip 14 uda 15 program 16 splinter 17 relay 2 feeder 2";

    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(expectedCommand, *decodedCommand);
    BOOST_CHECK_EQUAL(result, true);

    input = "6,TARGET,serial 12345456,"
            "ASSIGN,spid 10,geo 11,sub 12,feeder 16,feeder 1,zip 14,uda 15,program 16,splinter 17, relay 2";
    expectedCommand = "set MessagePriority 5 ; PutConfig xcom target "
                     "serial 12345456 "
                     "assign spid 10 geo 11 sub 12 zip 14 uda 15 program 16 splinter 17 relay 2 feeder 32769";

    result = validateAndDecodeLine(input, TEXT_CMD_FILE_SPECIFY_EXPRESSCOM, decodedCommand, filename);
    BOOST_CHECK_EQUAL(expectedCommand, *decodedCommand);
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

    delete decodedCommand;
}

BOOST_AUTO_TEST_SUITE_END()
