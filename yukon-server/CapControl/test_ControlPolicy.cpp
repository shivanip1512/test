#include <boost/test/unit_test.hpp>

#include "StandardControlPolicy.h"
#include "test_reader.h"
#include "std_helper.h"
#include "pointdefs.h"
#include "msg_pdata.h"

using namespace std;
using Cti::CapControl::StandardControlPolicy;
using Cti::CapControl::ControlPolicy;

namespace {

struct TestAttributeService : public AttributeService
{
    virtual AttributeMapping getPointsByPaoAndAttributes(int paoId, std::vector<Attribute>& attributes) override
    {
        AttributeMapping pointMapping;

        for (Attribute attribute : attributes)
        {
            LitePoint point = Cti::mapFindOrDefault(_attr, attribute, LitePoint());

            try
            {
                pointMapping.emplace(attribute, point);
            }
            catch (AttributeNotFound::exception & ex)
            {
                CTILOG_EXCEPTION_WARN(dout, ex);
            }
        }

        return pointMapping;
    }

    std::map<Attribute, LitePoint>  _attr;

    TestAttributeService()
    {
        _attr = decltype(_attr)
        {
            { Attribute::SourceVoltage,
            { 2202,  AnalogPointType, "Source Voltage", 1000, 1, "", "", 1.0, 0 } },
            { Attribute::Voltage,
            { 2203,  AnalogPointType, "Load Voltage", 1001, 2, "", "", 1.0, 0 } },
            { Attribute::TapUp,
            { 3100,  StatusPointType, "TapUp", 1003, 4, "", "control close", 1.0, 0 } },
            { Attribute::TapDown,
            { 3101,  StatusPointType, "TapDown", 1004, 5, "", "control close", 1.0, 0 } },
            { Attribute::KeepAlive,
            { 4200,  AnalogPointType, "KeepAlive", 1007, 1, "", "", 1.0, 0 } },
            { Attribute::AutoRemoteControl,
            { 5600,  StatusPointType, "AutoRemoteControl", 1009, 6, "", "", 1.0, 0 } },
            { Attribute::TapPosition,
            { 3500,  AnalogPointType, "TapPosition", 1013, 3, "", "", 1.0, 0 } },
            { Attribute::ForwardSetPoint,
            { 7000,  AnalogPointType, "Forward SetPoint", 1020, 10007, "", "", 1.0, 0 } },
            { Attribute::ForwardBandwidth,
            { 7100,  AnalogPointType, "Forward Bandwidth", 1021, 8, "", "", 1.0, 0 } },
            { Attribute::ReverseSetPoint,
            { 7200,  AnalogPointType, "Reverse SetPoint", 1022, 10017, "", "", 1.0, 0 } },
            { Attribute::ReverseBandwidth,
            { 7300,  AnalogPointType, "Reverse Bandwidth", 1023, 18, "", "", 1.0, 0 } },
            { Attribute::ReverseFlowIndicator,
            { 7400,  StatusPointType, "Reverse Flow Indicator", 1024, 19, "", "", 1.0, 0 } },
            { Attribute::ControlMode,
            { 7450,  AnalogPointType, "Regulator Control Mode", 1026, 21, "", "", 1.0, 0 } }
        };
    }
}
attributes;

}

BOOST_AUTO_TEST_SUITE(TestAttributeService)

BOOST_AUTO_TEST_CASE(test_getSetPointAttribute_getBandwidthAttribute)
{
    auto controlPolicy = std::make_unique<StandardControlPolicy>();
    controlPolicy->loadAttributes(attributes, 1234);

    struct ControlModeAttributes
    {
        ControlPolicy::ControlModes     controlMode;
        struct {
            Attribute setpoint, bandwidth;
        } forwardFlow, reverseFlow;
    };

    const std::map<double, ControlModeAttributes> testCases
    {
        { -1.0, { ControlPolicy::LockedForward        , { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth }, { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth } } },
        {  0.0, { ControlPolicy::LockedForward        , { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth }, { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth } } },
        {  1.0, { ControlPolicy::LockedReverse        , { Attribute::ReverseSetPoint, Attribute::ReverseBandwidth }, { Attribute::ReverseSetPoint, Attribute::ReverseBandwidth } } },
        {  2.0, { ControlPolicy::ReverseIdle          , { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth }, { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth } } },
        {  3.0, { ControlPolicy::Bidirectional        , { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth }, { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth } } },
        {  4.0, { ControlPolicy::NeutralIdle          , { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth }, { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth } } },
        {  5.0, { ControlPolicy::Cogeneration         , { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth }, { Attribute::ReverseSetPoint, Attribute::ReverseBandwidth } } },
        {  6.0, { ControlPolicy::ReactiveBidirectional, { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth }, { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth } } },
        {  7.0, { ControlPolicy::BiasBidirectional    , { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth }, { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth } } },
        {  8.0, { ControlPolicy::BiasCogeneration     , { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth }, { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth } } },
        {  9.0, { ControlPolicy::ReverseCogeneration  , { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth }, { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth } } },
        { 10.0, { ControlPolicy::LockedForward        , { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth }, { Attribute::ForwardSetPoint, Attribute::ForwardBandwidth } } }
    };

    for ( auto testCase : testCases )
    {
        controlPolicy->updatePointData({ 7450, testCase.first, NormalQuality, AnalogPointType }); //set control policy from testCases map
        BOOST_CHECK_EQUAL(controlPolicy->getControlMode(), testCase.second.controlMode);

        controlPolicy->updatePointData({ 7400, 0, NormalQuality, StatusPointType }); // forward flow
        BOOST_CHECK(controlPolicy->getSetPointAttribute() == testCase.second.forwardFlow.setpoint);
        BOOST_CHECK(controlPolicy->getBandwidthAttribute() == testCase.second.forwardFlow.bandwidth);

        controlPolicy->updatePointData({ 7400, 1, NormalQuality, StatusPointType }); // reverse flow
        BOOST_CHECK(controlPolicy->getSetPointAttribute() == testCase.second.reverseFlow.setpoint);
        BOOST_CHECK(controlPolicy->getBandwidthAttribute() == testCase.second.reverseFlow.bandwidth);
    }
}

BOOST_AUTO_TEST_SUITE_END()
