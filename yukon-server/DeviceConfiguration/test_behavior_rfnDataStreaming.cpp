#include <boost/test/unit_test.hpp>

#include "boostutil.h"
#include "behavior_rfnDataStreaming.h"

using namespace std::chrono_literals;

namespace std {
ostream &operator<<(ostream& os, const chrono::minutes& m);
}

std::ostream &operator<<(std::ostream& os, const Attribute &a);

BOOST_AUTO_TEST_SUITE(test_behavior_rfnDataStreaming)

BOOST_AUTO_TEST_CASE(test_parsing_no_channels)
{
    Cti::Behaviors::RfnDataStreamingBehavior d
    {
        42,
        {   { "enabled", "false" },
            { "channels", "0" } } };

    BOOST_CHECK_EQUAL(d.enabled, false);
    BOOST_CHECK(d.channels.empty());
}

BOOST_AUTO_TEST_CASE(test_parsing_two_channels)
{
    Cti::Behaviors::RfnDataStreamingBehavior d
    {
        42,
        {   { "enabled", "true" },
            { "channels", "2" },
            { "channels.0.enabled", "true" },
            { "channels.0.attribute", "VOLTAGE" },
            { "channels.0.interval", "3" },
            { "channels.1.enabled", "false" },
            { "channels.1.attribute", "DEMAND" },
            { "channels.1.interval", "7" } } };

    BOOST_CHECK_EQUAL(d.enabled, true);
    BOOST_REQUIRE_EQUAL(d.channels.size(), 2);

    BOOST_CHECK_EQUAL(d.channels[0].attribute, Attribute::Voltage);
    BOOST_CHECK_EQUAL(d.channels[0].interval, 3min);

    BOOST_CHECK_EQUAL(d.channels[1].attribute, Attribute::Demand);
    BOOST_CHECK_EQUAL(d.channels[1].interval, 7min);
}

BOOST_AUTO_TEST_SUITE_END()

