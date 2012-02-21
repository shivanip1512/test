#include <boost/test/unit_test.hpp>

#include "cmd_dlc.h"

BOOST_AUTO_TEST_SUITE( test_cmd_dlc )

struct Test_DlcCommand : Cti::Devices::Commands::DlcCommand
{
    using Cti::Devices::Commands::DlcCommand::getValueFromBits;
    using Cti::Devices::Commands::DlcCommand::getValueVectorFromBits;
};

BOOST_AUTO_TEST_CASE(test_getValueFromBits)
{
    std::vector<unsigned char> init;

    init.push_back(0x01);  //  0 - 0000 0001
    init.push_back(0xff);  //  8 - 1111 1111
    init.push_back(0xa5);  // 16 - 1010 0101
    init.push_back(0xc7);  // 24 - 1100 0111

    const std::vector<unsigned char> data = init;

    BOOST_CHECK_EQUAL(0, Test_DlcCommand::getValueFromBits(data, 0, 1));
    BOOST_CHECK_EQUAL(1, Test_DlcCommand::getValueFromBits(data, 7, 1));

    BOOST_CHECK_EQUAL(0, Test_DlcCommand::getValueFromBits(data,  2, 2));
    BOOST_CHECK_EQUAL(1, Test_DlcCommand::getValueFromBits(data,  6, 2));
    BOOST_CHECK_EQUAL(2, Test_DlcCommand::getValueFromBits(data, 18, 2));
    BOOST_CHECK_EQUAL(3, Test_DlcCommand::getValueFromBits(data,  9, 2));

    BOOST_CHECK_EQUAL(0, Test_DlcCommand::getValueFromBits(data,  4, 3));
    BOOST_CHECK_EQUAL(1, Test_DlcCommand::getValueFromBits(data,  5, 3));
    BOOST_CHECK_EQUAL(2, Test_DlcCommand::getValueFromBits(data, 17, 3));
    BOOST_CHECK_EQUAL(3, Test_DlcCommand::getValueFromBits(data,  6, 3));
    BOOST_CHECK_EQUAL(4, Test_DlcCommand::getValueFromBits(data, 25, 3));
    BOOST_CHECK_EQUAL(5, Test_DlcCommand::getValueFromBits(data, 21, 3));
    BOOST_CHECK_EQUAL(6, Test_DlcCommand::getValueFromBits(data, 15, 3));
    BOOST_CHECK_EQUAL(7, Test_DlcCommand::getValueFromBits(data, 10, 3));

    BOOST_CHECK_EQUAL(10, Test_DlcCommand::getValueFromBits(data, 16, 4));
    BOOST_CHECK_EQUAL( 9, Test_DlcCommand::getValueFromBits(data, 18, 4));

    BOOST_CHECK_EQUAL(20, Test_DlcCommand::getValueFromBits(data, 16, 5));
    BOOST_CHECK_EQUAL(24, Test_DlcCommand::getValueFromBits(data, 24, 5));

    BOOST_CHECK_EQUAL(18, Test_DlcCommand::getValueFromBits(data, 17, 6));

    BOOST_CHECK_EQUAL(127, Test_DlcCommand::getValueFromBits(data, 8, 7));

    BOOST_CHECK_EQUAL(255, Test_DlcCommand::getValueFromBits(data, 8, 8));

    BOOST_CHECK_EQUAL(511, Test_DlcCommand::getValueFromBits(data, 7, 9));

    BOOST_CHECK_EQUAL(1023, Test_DlcCommand::getValueFromBits(data,  7, 10));
    BOOST_CHECK_EQUAL( 604, Test_DlcCommand::getValueFromBits(data, 18, 10));
}

BOOST_AUTO_TEST_CASE(test_getValueVectorFromBits)
{
    std::vector<unsigned char> init;

    init.push_back(0x01);  //  0 - 0000 0001
    init.push_back(0xff);  //  8 - 1111 1111
    init.push_back(0xa5);  // 16 - 1010 0101
    init.push_back(0xc7);  // 24 - 1100 0111

    const std::vector<unsigned char> data = init;

    {
        unsigned check[] = { 0, 0, 0, 0, 0, 0, 0, 1 };

        std::vector<unsigned> result = Test_DlcCommand::getValueVectorFromBits(data, 0, 1, 8);

        BOOST_CHECK(std::equal(result.begin(), result.end(), check));
    }

    {
        unsigned check[] = { 0, 0, 0, 1, 3, 3, 3 };

        std::vector<unsigned> result = Test_DlcCommand::getValueVectorFromBits(data, 0, 2, 7);

        BOOST_CHECK(std::equal(result.begin(), result.end(), check));
    }

    {
        unsigned check[] = { 2, 2, 1, 1, 3, 0 };

        std::vector<unsigned> result = Test_DlcCommand::getValueVectorFromBits(data, 16, 2, 6);

        BOOST_CHECK(std::equal(result.begin(), result.end(), check));
    }

    {
        unsigned check[] = { 0, 0, 0, 3, 3 };

        std::vector<unsigned> result = Test_DlcCommand::getValueVectorFromBits(data, 1, 2, 5);

        BOOST_CHECK(std::equal(result.begin(), result.end(), check));
    }

    {
        unsigned check[] = { 1, 7, 7, 7, 2, 2, 7 };

        std::vector<unsigned> result = Test_DlcCommand::getValueVectorFromBits(data, 5, 3, 7);

        BOOST_CHECK(std::equal(result.begin(), result.end(), check));
    }

    {
        unsigned check[] = { 0, 7, 31, 26 };

        std::vector<unsigned> result = Test_DlcCommand::getValueVectorFromBits(data, 0, 5, 4);

        BOOST_CHECK(std::equal(result.begin(), result.end(), check));
    }

    {
        unsigned check[] = { 3, 510, 302 };

        std::vector<unsigned> result = Test_DlcCommand::getValueVectorFromBits(data, 0, 9, 3);

        BOOST_CHECK(std::equal(result.begin(), result.end(), check));
    }
}

BOOST_AUTO_TEST_CASE( test_getValueVectorFromBits_throw )
{
    std::vector<unsigned char> init;

    init.push_back(0x01);  //  0 - 0000 0001
    init.push_back(0xff);  //  8 - 1111 1111
    init.push_back(0xa5);  // 16 - 1010 0101
    init.push_back(0xc7);  // 24 - 1100 0111

    const std::vector<unsigned char> data = init;

    try
    {
        std::vector<unsigned> result = Test_DlcCommand::getValueVectorFromBits(data, 0, 8, 5);

        BOOST_FAIL("DlcCommand::getValueVectorFromBits() did not throw!");
    }
    catch(Test_DlcCommand::CommandException &ex)
    {
        BOOST_CHECK_EQUAL(ex.error_code,        NOTNORMAL);
        BOOST_CHECK_EQUAL(ex.error_description, "Payload too small");
    }
}

BOOST_AUTO_TEST_SUITE_END()
