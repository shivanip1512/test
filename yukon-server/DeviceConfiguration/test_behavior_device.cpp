#include <boost/test/unit_test.hpp>

#include "boostutil.h"
#include "behavior_device.h"

BOOST_AUTO_TEST_SUITE( test_behavior_device )

struct test_DeviceBehavior : Cti::Behaviors::DeviceBehavior
{
    test_DeviceBehavior(const long paoId, const std::map<std::string, std::string>&& parameters)
        : DeviceBehavior{ paoId, std::move(parameters) }
    {}

    using DeviceBehavior::parseItem;
    using DeviceBehavior::parseIndexedItems;

    using DeviceBehavior::IndexedItemDescriptor;
};

BOOST_AUTO_TEST_CASE(test_parsing)
{
    test_DeviceBehavior d
    {
        42,
        {   { "jimmy", "1942" },
            { "bubba", "gump" },
            { "shrimp", "12" },
            { "plato", "true" } } };

    BOOST_CHECK_EQUAL(d.parseItem<unsigned long>("jimmy"), 1942);
    BOOST_CHECK_EQUAL(d.parseItem<std::string>("bubba"), "gump");
    BOOST_CHECK_EQUAL(d.parseItem<unsigned long>("shrimp"), 12);
    BOOST_CHECK_EQUAL(d.parseItem<uint8_t>("shrimp"), 12);
    BOOST_CHECK_EQUAL(true, d.parseItem<bool>("plato"));
}

BOOST_AUTO_TEST_CASE(test_exceptions)
{
    test_DeviceBehavior d
    {
        42,
        {   { "jimmy", "1942" },
            { "bubba", "gump" } } };

    //  1942 is greater than 255
    BOOST_REQUIRE_THROW(d.parseItem<unsigned char>("jimmy"), std::out_of_range);

    //  "bubba" is not "true" or "false"
    BOOST_REQUIRE_THROW(d.parseItem<bool>("bubba"), std::invalid_argument);
}

BOOST_AUTO_TEST_SUITE_END()

