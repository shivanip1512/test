#include <boost/test/unit_test.hpp>

#include "boostutil.h"
#include "mgr_behavior.h"
#include "behavior_rfnDataStreaming.h"
#include "test_reader.h"

using Cti::Behaviors::RfnDataStreamingBehavior;
using namespace std::chrono_literals;

namespace std {
ostream &operator<<(ostream& os, const chrono::minutes& m);
}

std::ostream &operator<<(std::ostream& os, const Attribute &a);

struct test_BehaviorManager : public Cti::BehaviorManager
{
    BehaviorValues behaviorValues;

    BehaviorValues loadBehavior(const long paoId, const std::string& behaviorType) override
    {
        return behaviorValues;
    }

    BehaviorValues loadBehaviorReport(const long paoId, const std::string& behaviorType) override
    {
        return behaviorValues;
    }
};

struct overrideGlobals {

    std::unique_ptr<Cti::BehaviorManager> original;

    test_BehaviorManager *behaviorManagerHandle;

    overrideGlobals() 
    {
        auto b = std::make_unique<test_BehaviorManager>();

        behaviorManagerHandle = b.get();

        original = std::move(b);

        original.swap(Cti::gBehaviorManager);
    }

    ~overrideGlobals()
    {
        original.swap(Cti::gBehaviorManager);
    }
};

BOOST_FIXTURE_TEST_SUITE( test_mgr_behavior, overrideGlobals )

BOOST_AUTO_TEST_CASE(test_getBehaviorForPao_no_records)
{
    BOOST_CHECK_THROW(
            Cti::BehaviorManager::getBehaviorForPao<RfnDataStreamingBehavior>(42),
            Cti::Behaviors::BehaviorItemNotFoundException);
}

BOOST_AUTO_TEST_CASE(test_getBehaviorForPao_no_channels)
{
    behaviorManagerHandle->behaviorValues.emplace("enabled", "true");
    behaviorManagerHandle->behaviorValues.emplace("channels", "0");

    auto rfnBehavior = Cti::BehaviorManager::getBehaviorForPao<RfnDataStreamingBehavior>(42);

    BOOST_CHECK_EQUAL(rfnBehavior.enabled, true);

    BOOST_REQUIRE_EQUAL(rfnBehavior.channels.size(), 0);
}

BOOST_AUTO_TEST_CASE(test_getBehaviorForPao_one_channel)
{
    behaviorManagerHandle->behaviorValues.emplace("enabled", "false");
    behaviorManagerHandle->behaviorValues.emplace("channels", "1");
    behaviorManagerHandle->behaviorValues.emplace("channels.0.attribute", "VOLTAGE");
    behaviorManagerHandle->behaviorValues.emplace("channels.0.interval", "4");

    auto rfnBehavior = Cti::BehaviorManager::getBehaviorForPao<RfnDataStreamingBehavior>(42);

    BOOST_CHECK_EQUAL(rfnBehavior.enabled, false);

    BOOST_REQUIRE_EQUAL(rfnBehavior.channels.size(), 1);
    
    BOOST_CHECK_EQUAL(rfnBehavior.channels[0].attribute, Attribute::Voltage);
    BOOST_CHECK_EQUAL(rfnBehavior.channels[0].interval, 4min);
}

BOOST_AUTO_TEST_CASE(test_getDeviceStateForPao_no_records)
{
    auto rfnBehavior = Cti::BehaviorManager::getDeviceStateForPao<RfnDataStreamingBehavior>(42);

    BOOST_CHECK(! rfnBehavior.is_initialized());
}

BOOST_AUTO_TEST_CASE(test_getDeviceStateForPao_no_channels)
{
    using BehaviorValueRow = Cti::Test::StringRow<2>;
    using BehaviorValueReader = Cti::Test::TestReader<BehaviorValueRow>;

    behaviorManagerHandle->behaviorValues.clear();
    behaviorManagerHandle->behaviorValues.emplace("enabled", "true");
    behaviorManagerHandle->behaviorValues.emplace("channels", "0");

    auto optRfnBehavior = Cti::BehaviorManager::getDeviceStateForPao<RfnDataStreamingBehavior>(42);

    BOOST_REQUIRE(optRfnBehavior.is_initialized());

    const RfnDataStreamingBehavior &rfnBehavior = *optRfnBehavior;

    BOOST_CHECK_EQUAL(rfnBehavior.enabled, true);

    BOOST_CHECK(rfnBehavior.channels.empty());
}

BOOST_AUTO_TEST_CASE(test_getDeviceStateForPao_three_channels)
{
    using BehaviorValueRow = Cti::Test::StringRow<2>;
    using BehaviorValueReader = Cti::Test::TestReader<BehaviorValueRow>;

    behaviorManagerHandle->behaviorValues.clear();
    behaviorManagerHandle->behaviorValues.emplace("enabled", "false");
    behaviorManagerHandle->behaviorValues.emplace("channels", "3");
    behaviorManagerHandle->behaviorValues.emplace("channels.0.attribute", "VOLTAGE");
    behaviorManagerHandle->behaviorValues.emplace("channels.0.interval", "4");
    behaviorManagerHandle->behaviorValues.emplace("channels.1.attribute", "DEMAND");
    behaviorManagerHandle->behaviorValues.emplace("channels.1.interval", "7");
    behaviorManagerHandle->behaviorValues.emplace("channels.2.attribute", "KVAR");
    behaviorManagerHandle->behaviorValues.emplace("channels.2.interval", "17");

    auto optRfnBehavior = Cti::BehaviorManager::getDeviceStateForPao<RfnDataStreamingBehavior>(42);

    BOOST_REQUIRE(optRfnBehavior.is_initialized());

    const RfnDataStreamingBehavior &rfnBehavior = *optRfnBehavior;

    BOOST_CHECK_EQUAL(rfnBehavior.enabled, false);

    BOOST_REQUIRE_EQUAL(rfnBehavior.channels.size(), 3);

    BOOST_CHECK_EQUAL(rfnBehavior.channels[0].attribute, Attribute::Voltage);
    BOOST_CHECK_EQUAL(rfnBehavior.channels[0].interval, 4min);

    BOOST_CHECK_EQUAL(rfnBehavior.channels[1].attribute, Attribute::Demand);
    BOOST_CHECK_EQUAL(rfnBehavior.channels[1].interval, 7min);

    BOOST_CHECK_EQUAL(rfnBehavior.channels[2].attribute, Attribute::kVAr);
    BOOST_CHECK_EQUAL(rfnBehavior.channels[2].interval, 17min);
}

BOOST_AUTO_TEST_SUITE_END()
