#include <boost/test/unit_test.hpp>

#include "boostutil.h"
#include "mgr_behavior.h"
#include "behavior_rfnDataStreaming.h"
#include "test_reader.h"

#include "deviceconfig_test_helpers.h"

#include "boost_test_helpers.h"

using Cti::Behaviors::RfnDataStreamingBehavior;
using namespace std::chrono_literals;

namespace std {
ostream &operator<<(ostream& os, const chrono::minutes& m);
}

std::ostream &operator<<(std::ostream& os, const Attribute &a);

struct overrideGlobals : Cti::Test::Override_BehaviorManager {

};

BOOST_FIXTURE_TEST_SUITE( test_mgr_behavior, overrideGlobals )

BOOST_AUTO_TEST_CASE(test_getBehaviorForPao_no_records)
{
    const auto rfnBehavior = Cti::BehaviorManager::getBehaviorForPao<RfnDataStreamingBehavior>(42);
    
    BOOST_CHECK( ! rfnBehavior );
}

BOOST_AUTO_TEST_CASE(test_getBehaviorForPao_no_channels)
{
    behaviorManagerHandle->behaviorValues.emplace("channels", "0");

    auto rfnBehavior = Cti::BehaviorManager::getBehaviorForPao<RfnDataStreamingBehavior>(42);

    BOOST_REQUIRE( rfnBehavior );

    BOOST_CHECK_EQUAL(rfnBehavior->enabled, true);

    BOOST_REQUIRE_EQUAL(rfnBehavior->channels.size(), 0);
}

BOOST_AUTO_TEST_CASE(test_getBehaviorForPao_one_channel)
{
    behaviorManagerHandle->behaviorValues = std::map<std::string, std::string> {
        { "channels", "1" },
        { "channels.0.attribute", "VOLTAGE" },
        { "channels.0.interval", "4" }};

    auto rfnBehavior = Cti::BehaviorManager::getBehaviorForPao<RfnDataStreamingBehavior>(42);

    BOOST_REQUIRE(rfnBehavior);

    BOOST_CHECK_EQUAL(rfnBehavior->enabled, true);

    BOOST_REQUIRE_EQUAL(rfnBehavior->channels.size(), 1);

    BOOST_CHECK_EQUAL(rfnBehavior->channels[0].attribute, Attribute::Voltage);
    BOOST_CHECK_EQUAL(rfnBehavior->channels[0].interval, 4min);
}

BOOST_AUTO_TEST_CASE(test_getBehaviorForPao_two_channels)
{
    behaviorManagerHandle->behaviorValues = std::map<std::string, std::string> {
        { "channels", "2" },
        { "channels.0.attribute", "VOLTAGE" },
        { "channels.0.interval", "4" },
        { "channels.1.attribute", "DEMAND" },
        { "channels.1.interval", "7" }};

    auto rfnBehavior = Cti::BehaviorManager::getBehaviorForPao<RfnDataStreamingBehavior>(42);

    BOOST_REQUIRE(rfnBehavior);

    BOOST_CHECK_EQUAL(rfnBehavior->enabled, true);

    BOOST_REQUIRE_EQUAL(rfnBehavior->channels.size(), 2);

    BOOST_CHECK_EQUAL(rfnBehavior->channels[0].attribute, Attribute::Voltage);
    BOOST_CHECK_EQUAL(rfnBehavior->channels[0].interval, 4min);
    BOOST_CHECK_EQUAL(rfnBehavior->channels[1].attribute, Attribute::Demand);
    BOOST_CHECK_EQUAL(rfnBehavior->channels[1].interval, 7min);
}

BOOST_AUTO_TEST_CASE(test_getDeviceStateForPao_no_records)
{
    auto rfnBehavior = Cti::BehaviorManager::getDeviceStateForPao<RfnDataStreamingBehavior>(42);

    BOOST_CHECK(! rfnBehavior.is_initialized());
}

BOOST_AUTO_TEST_CASE(test_getDeviceStateForPao_no_channels)
{
    behaviorManagerHandle->behaviorReport.emplace("enabled", "true");
    behaviorManagerHandle->behaviorReport.emplace("channels", "0");

    auto rfnBehaviorReport = Cti::BehaviorManager::getDeviceStateForPao<RfnDataStreamingBehavior>(42);

    BOOST_REQUIRE(rfnBehaviorReport);

    BOOST_CHECK_EQUAL(rfnBehaviorReport->enabled, true);

    BOOST_CHECK(rfnBehaviorReport->channels.empty());
}

BOOST_AUTO_TEST_CASE(test_getDeviceStateForPao_invalid_attribute)
{
    behaviorManagerHandle->behaviorReport = std::map<std::string, std::string> {
        { "enabled", "false" },
        { "channels", "1" },
        { "channels.0.attribute", "BANANA" },
        { "channels.0.interval", "4" },
        { "channels.0.enabled", "true" }};

    try
    {
        auto rfnBehaviorReport = Cti::BehaviorManager::getDeviceStateForPao<RfnDataStreamingBehavior>(42);

        BOOST_FAIL("Did not throw");
    }
    catch( AttributeNotFound &ex )
    {
        BOOST_CHECK_EQUAL(ex.desc, "Attribute not found: BANANA");
    }
}

BOOST_AUTO_TEST_CASE(test_getDeviceStateForPao_three_channels)
{
    behaviorManagerHandle->behaviorReport = std::map<std::string, std::string> {
        { "enabled", "false" },
        { "channels", "3" },
        { "channels.0.attribute", "VOLTAGE" },
        { "channels.0.interval", "4" },
        { "channels.0.enabled", "true" },
        { "channels.1.attribute", "DEMAND" },
        { "channels.1.interval", "7" },
        { "channels.1.enabled", "true" },
        { "channels.2.attribute", "KVAR" },
        { "channels.2.interval", "17" },
        { "channels.2.enabled", "false" }};

    auto rfnBehaviorReport = Cti::BehaviorManager::getDeviceStateForPao<RfnDataStreamingBehavior>(42);

    BOOST_REQUIRE(rfnBehaviorReport);

    BOOST_CHECK_EQUAL(rfnBehaviorReport->enabled, false);

    BOOST_REQUIRE_EQUAL(rfnBehaviorReport->channels.size(), 3);

    BOOST_CHECK_EQUAL(rfnBehaviorReport->channels[0].attribute, Attribute::Voltage);
    BOOST_CHECK_EQUAL(rfnBehaviorReport->channels[0].interval, 4min);
                      
    BOOST_CHECK_EQUAL(rfnBehaviorReport->channels[1].attribute, Attribute::Demand);
    BOOST_CHECK_EQUAL(rfnBehaviorReport->channels[1].interval, 7min);
                      
    BOOST_CHECK_EQUAL(rfnBehaviorReport->channels[2].attribute, Attribute::kVAr);
    BOOST_CHECK_EQUAL(rfnBehaviorReport->channels[2].interval, 0min);
}

BOOST_AUTO_TEST_SUITE_END()
