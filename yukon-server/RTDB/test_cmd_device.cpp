#include <boost/test/unit_test.hpp>

#include "cmd_device.h"

#include <boost/assign/list_of.hpp>

namespace std {

ostream &operator<<(ostream &o, const std::vector<unsigned char> &bytes)
{
    o << "[";

    const unsigned char oldfill = o.fill('0');

    for each( unsigned char b in bytes )
    {
        o << hex << setw(2) << (unsigned)b << " ";
    }

    o.fill(oldfill);

    o << "]";

    return o;
}

}

BOOST_AUTO_TEST_SUITE( test_cmd_device )

struct Test_DeviceCommand : Cti::Devices::Commands::DeviceCommand
{
    using Cti::Devices::Commands::DeviceCommand::getValueFromBits;
    using Cti::Devices::Commands::DeviceCommand::getValueVectorFromBits;
    using Cti::Devices::Commands::DeviceCommand::setBits;
};

BOOST_AUTO_TEST_CASE(test_getValueFromBits)
{
    std::vector<unsigned char> init;

    init.push_back(0x01);  //  0 - 0000 0001
    init.push_back(0xff);  //  8 - 1111 1111
    init.push_back(0xa5);  // 16 - 1010 0101
    init.push_back(0xc7);  // 24 - 1100 0111

    const std::vector<unsigned char> data = init;

    BOOST_CHECK_EQUAL(0, Test_DeviceCommand::getValueFromBits(data, 0, 1));
    BOOST_CHECK_EQUAL(1, Test_DeviceCommand::getValueFromBits(data, 7, 1));

    BOOST_CHECK_EQUAL(0, Test_DeviceCommand::getValueFromBits(data,  2, 2));
    BOOST_CHECK_EQUAL(1, Test_DeviceCommand::getValueFromBits(data,  6, 2));
    BOOST_CHECK_EQUAL(2, Test_DeviceCommand::getValueFromBits(data, 18, 2));
    BOOST_CHECK_EQUAL(3, Test_DeviceCommand::getValueFromBits(data,  9, 2));

    BOOST_CHECK_EQUAL(0, Test_DeviceCommand::getValueFromBits(data,  4, 3));
    BOOST_CHECK_EQUAL(1, Test_DeviceCommand::getValueFromBits(data,  5, 3));
    BOOST_CHECK_EQUAL(2, Test_DeviceCommand::getValueFromBits(data, 17, 3));
    BOOST_CHECK_EQUAL(3, Test_DeviceCommand::getValueFromBits(data,  6, 3));
    BOOST_CHECK_EQUAL(4, Test_DeviceCommand::getValueFromBits(data, 25, 3));
    BOOST_CHECK_EQUAL(5, Test_DeviceCommand::getValueFromBits(data, 21, 3));
    BOOST_CHECK_EQUAL(6, Test_DeviceCommand::getValueFromBits(data, 15, 3));
    BOOST_CHECK_EQUAL(7, Test_DeviceCommand::getValueFromBits(data, 10, 3));

    BOOST_CHECK_EQUAL(10, Test_DeviceCommand::getValueFromBits(data, 16, 4));
    BOOST_CHECK_EQUAL( 9, Test_DeviceCommand::getValueFromBits(data, 18, 4));

    BOOST_CHECK_EQUAL(20, Test_DeviceCommand::getValueFromBits(data, 16, 5));
    BOOST_CHECK_EQUAL(24, Test_DeviceCommand::getValueFromBits(data, 24, 5));

    BOOST_CHECK_EQUAL(18, Test_DeviceCommand::getValueFromBits(data, 17, 6));

    BOOST_CHECK_EQUAL(127, Test_DeviceCommand::getValueFromBits(data, 8, 7));

    BOOST_CHECK_EQUAL(255, Test_DeviceCommand::getValueFromBits(data, 8, 8));

    BOOST_CHECK_EQUAL(511, Test_DeviceCommand::getValueFromBits(data, 7, 9));

    BOOST_CHECK_EQUAL(1023, Test_DeviceCommand::getValueFromBits(data,  7, 10));
    BOOST_CHECK_EQUAL( 604, Test_DeviceCommand::getValueFromBits(data, 18, 10));
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

        std::vector<unsigned> result = Test_DeviceCommand::getValueVectorFromBits(data, 0, 1, 8);

        BOOST_CHECK(std::equal(result.begin(), result.end(), check));
    }

    {
        unsigned check[] = { 0, 0, 0, 1, 3, 3, 3 };

        std::vector<unsigned> result = Test_DeviceCommand::getValueVectorFromBits(data, 0, 2, 7);

        BOOST_CHECK(std::equal(result.begin(), result.end(), check));
    }

    {
        unsigned check[] = { 2, 2, 1, 1, 3, 0 };

        std::vector<unsigned> result = Test_DeviceCommand::getValueVectorFromBits(data, 16, 2, 6);

        BOOST_CHECK(std::equal(result.begin(), result.end(), check));
    }

    {
        unsigned check[] = { 0, 0, 0, 3, 3 };

        std::vector<unsigned> result = Test_DeviceCommand::getValueVectorFromBits(data, 1, 2, 5);

        BOOST_CHECK(std::equal(result.begin(), result.end(), check));
    }

    {
        unsigned check[] = { 1, 7, 7, 7, 2, 2, 7 };

        std::vector<unsigned> result = Test_DeviceCommand::getValueVectorFromBits(data, 5, 3, 7);

        BOOST_CHECK(std::equal(result.begin(), result.end(), check));
    }

    {
        unsigned check[] = { 0, 7, 31, 26 };

        std::vector<unsigned> result = Test_DeviceCommand::getValueVectorFromBits(data, 0, 5, 4);

        BOOST_CHECK(std::equal(result.begin(), result.end(), check));
    }

    {
        unsigned check[] = { 3, 510, 302 };

        std::vector<unsigned> result = Test_DeviceCommand::getValueVectorFromBits(data, 0, 9, 3);

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
        std::vector<unsigned> result = Test_DeviceCommand::getValueVectorFromBits(data, 0, 8, 5);

        BOOST_FAIL("DeviceCommand::getValueVectorFromBits() did not throw!");
    }
    catch(Test_DeviceCommand::CommandException &ex)
    {
        BOOST_CHECK_EQUAL(ex.error_code,        NOTNORMAL);
        BOOST_CHECK_EQUAL(ex.error_description, "Payload too small");
    }
}


BOOST_AUTO_TEST_CASE( test_setBits )
{
    unsigned values[] = { 0, 0x55555555, 0xaaaaaaaa, 0xffffffff };

    //  10 * 10 * 4 = 400 testcases

    std::vector<std::vector<unsigned char> > results;

    for each( unsigned value in values )
    {
        for( unsigned length = 0; length < 10; ++length )
        {
            for( unsigned offset = 0; offset < 10; ++offset)
            {
                std::vector<unsigned char> result;
                Test_DeviceCommand::setBits(result, offset, length, value);

                results.push_back(result);
            }
        }
    }

    using boost::assign::list_of;

    std::vector<unsigned char> empty;

    const std::vector<std::vector<unsigned char> > expected = list_of
    //  length: 0
        //  value: 0
        (empty)
        .repeat(7, list_of(0))
        .repeat(2, list_of(0)(0))
        //  value: a
        (empty)
        .repeat(7, list_of(0))
        .repeat(2, list_of(0)(0))
        //  value: f
        (empty)
        .repeat(7, list_of(0))
        .repeat(2, list_of(0)(0))
    //  length: 1
        //  value: 0
        .repeat(8, list_of(0))
        .repeat(2, list_of(0)(0))
        //  value: 5
        (list_of(0x01))
        (list_of(0x02))
        (list_of(0x04))
        (list_of(0x08))
        (list_of(0x10))
        (list_of(0x20))
        (list_of(0x40))
        (list_of(0x80))
        (list_of(0x00)(0x01))
        (list_of(0x00)(0x02))
        //  value: a
        .repeat(7, list_of(0))
        .repeat(3, list_of(0)(0))
        //  value: f
        (list_of(0x01))
        (list_of(0x02))
        (list_of(0x04))
        (list_of(0x08))
        (list_of(0x10))
        (list_of(0x20))
        (list_of(0x40))
        (list_of(0x80))
        (list_of(0x00)(0x01))
        (list_of(0x00)(0x02))
    //  length: 2
        //  value: 0
        .repeat(6, list_of(0))
        .repeat(4, list_of(0)(0))
        //  value: 5
        (list_of(0x01))
        (list_of(0x02))
        (list_of(0x04))
        (list_of(0x08))
        (list_of(0x10))
        (list_of(0x20))
        (list_of(0x40))
        (list_of(0x80)(0x00))
        (list_of(0x00)(0x01))
        //  value: a
        (list_of(0x02))
        (list_of(0x04))
        (list_of(0x08))
        (list_of(0x10))
        (list_of(0x20))
        (list_of(0x40))
        (list_of(0x80))
        (list_of(0x00)(0x01))
        (list_of(0x00)(0x02))
        //  value: f
        (list_of(0x03))
        (list_of(0x06))
        (list_of(0x0c))
        (list_of(0x18))
        (list_of(0x30))
        (list_of(0x60))
        (list_of(0xc0))
        (list_of(0x08)(0x01))
        (list_of(0x00)(0x03))
        (list_of(0x00)(0x06))
    //  length: 3
        //  value: 0
        .repeat(5, list_of(0))
        .repeat(5, list_of(0)(0))
        //  value: 5
        (list_of(0x01))
        (list_of(0x02))
        (list_of(0x04))
        (list_of(0x08))
        (list_of(0x10))
        (list_of(0x20))
        (list_of(0x40))
        (list_of(0x80)(0x00))
        (list_of(0x00)(0x01))
        //  value: a
        (list_of(0x02))
        (list_of(0x04))
        (list_of(0x08))
        (list_of(0x10))
        (list_of(0x20))
        (list_of(0x40))
        (list_of(0x80))
        (list_of(0x00)(0x01))
        (list_of(0x00)(0x02))
        //  value: f
        (list_of(0x03))
        (list_of(0x06))
        (list_of(0x0c))
        (list_of(0x18))
        (list_of(0x30))
        (list_of(0x60))
        (list_of(0xc0))
        (list_of(0x08)(0x01))
        (list_of(0x00)(0x03))
        (list_of(0x00)(0x06))
    .repeat(240, list_of(0));

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}


BOOST_AUTO_TEST_SUITE_END()
