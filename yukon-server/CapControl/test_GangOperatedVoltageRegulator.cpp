#include <boost/test/unit_test.hpp>

#include "capcontroller.h"
#include "VoltageRegulatorManager.h"
#include "GangOperatedVoltageRegulator.h"
#include "mgr_config.h"
#include "std_helper.h"
#include "RegulatorEvents.h"

#include "capcontrol_test_helpers.h"
#include "boost_test_helpers.h"

// Objects
using Cti::CapControl::VoltageRegulator;
using Cti::CapControl::VoltageRegulatorManager;
using Cti::CapControl::GangOperatedVoltageRegulator;
using Cti::CapControl::ControlPolicy;

// Exceptions
using Cti::CapControl::MissingAttribute;

namespace
{

struct regulator_device_config_base
{
    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;

    Cti::Test::Override_ConfigManager overrideConfigManager;

    regulator_device_config_base()
        :   fixtureConfig( new Cti::Test::test_DeviceConfig ),
            overrideConfigManager( fixtureConfig )
    {
        fixtureConfig->insertValue( "installOrientation",       "FORWARD" );

        fixtureConfig->insertValue( "voltageChangePerTap",      "0.75" );

        fixtureConfig->insertValue( "regulatorHeartbeatMode",   "COUNTDOWN" );
        fixtureConfig->insertValue( "regulatorHeartbeatPeriod", "0" );
        fixtureConfig->insertValue( "regulatorHeartbeatValue",  "123" );
    }
};

struct regulator_device_config_direct_tap : regulator_device_config_base
{
    regulator_device_config_direct_tap()
        :   regulator_device_config_base()
    {
        fixtureConfig->insertValue( "voltageControlMode",  "DIRECT_TAP" );
    }
};

struct regulator_device_config_set_point : regulator_device_config_base
{
    regulator_device_config_set_point()
        :   regulator_device_config_base()
    {
        fixtureConfig->insertValue( "voltageControlMode",  "SET_POINT" );
    }
};

struct regulator_device_config_set_point_reversed_install : regulator_device_config_base
{
    regulator_device_config_set_point_reversed_install()
        :   regulator_device_config_base()
    {
        fixtureConfig->insertValue( "installOrientation",  "REVERSE" );
        fixtureConfig->insertValue( "voltageControlMode",  "SET_POINT" );
    }
};

struct regulator_device_config_direct_tap_with_limits : regulator_device_config_base
{
    regulator_device_config_direct_tap_with_limits()
        :   regulator_device_config_base()
    {
        fixtureConfig->insertValue( "voltageControlMode",  "DIRECT_TAP" );

        fixtureConfig->insertValue( "minTapPosition",  "-10" );
        fixtureConfig->insertValue( "maxTapPosition",   "12" );
    }
};

struct gang_operated_voltage_regulator_fixture_core
{
    struct TestCtiCapController : public CtiCapController
    {
        TestCtiCapController()
        {
            CtiCapController::setInstance(this);
        }

        virtual void sendMessageToDispatch(CtiMessage* message, Cti::CallSite cs) override
        {
            signalMessages.emplace_back( message );
        }
        void manualCapBankControl(Cti::CapControl::PorterRequest pilRequest, CtiMultiMsg* multiMsg = NULL) override
        {
            requestMessages.emplace_back( std::move( pilRequest ) );
        }
        void enqueueEventLogEntry(const Cti::CapControl::EventLogEntry &event) override
        {
            eventMessages.push_back(event);
        }

        std::vector< std::unique_ptr<CtiMessage> >      signalMessages;
        Cti::CapControl::PorterRequests                 requestMessages;
        Cti::CapControl::EventLogEntries                eventMessages;
    }
    capController;

    struct TestAttributeService : public AttributeService
    {
        virtual AttributeMapping getPointsByPaoAndAttributes( int paoId, std::vector<Attribute>& attributes ) override
        {
            AttributeMapping pointMapping;

            for( Attribute attribute : attributes )
            {
                LitePoint point = Cti::mapFindOrDefault( _attr, attribute, LitePoint() );

                try
                {
                    pointMapping.emplace( attribute, point );
                }
                catch ( AttributeNotFound::exception & ex )
                {
                    BOOST_WARN(ex.what());
                }
            }

            return pointMapping;
        }

        std::map<Attribute, LitePoint>  _attr;

        TestAttributeService()
        {
            _attr = decltype( _attr )
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
                    { 7450,  AnalogPointType, "Regulator Control Mode", 1026, 21, "", "", 1.0, 0 } },
                { Attribute::PowerFlowIndeterminate,
                    { 7500,  StatusPointType, "Power Flow Indeterminate", 1030, 23, "", "", 1.0, 0 } },
                { Attribute::ControlPowerFlowReverse,
                    { 7550,  StatusPointType, "Control Power Flow Reverse", 1031, 24, "", "", 1.0, 0 } }
            };
        }
    }
    attributes;

    Cti::Test::use_in_unit_tests_only   test_limiter;

    VoltageRegulatorManager::SharedPtr  regulator;

    gang_operated_voltage_regulator_fixture_core()
        :   regulator( new GangOperatedVoltageRegulator )
    {
        regulator->setPaoId( 23456 );
        regulator->setPaoName( "Test Regulator #1" );
        regulator->setPaoCategory( "CAPCONTROL" );
        regulator->setPaoType( VoltageRegulator::GangOperatedVoltageRegulator );
        regulator->setRegulatorTimeout( 100 );
    }
};


struct gang_operated_voltage_regulator_fixture_direct_tap
    :   gang_operated_voltage_regulator_fixture_core,
        regulator_device_config_direct_tap
{
};


struct gang_operated_voltage_regulator_fixture_setpoint
    :   gang_operated_voltage_regulator_fixture_core,
        regulator_device_config_set_point
{
};

struct gang_operated_voltage_regulator_fixture_setpoint_reversed_install
    :   gang_operated_voltage_regulator_fixture_core,
        regulator_device_config_set_point_reversed_install
{
};

}

struct gang_operated_voltage_regulator_fixture_direct_tap_with_limits
    :   gang_operated_voltage_regulator_fixture_core,
        regulator_device_config_direct_tap_with_limits
{
};

BOOST_FIXTURE_TEST_SUITE( test_GangOperatedVoltageRegulator_DirectTap, gang_operated_voltage_regulator_fixture_direct_tap )

BOOST_AUTO_TEST_CASE(test_requestVoltageChange)
{
    regulator->loadAttributes( &attributes );

    BOOST_CHECK_EQUAL( 0.75, regulator->requestVoltageChange( 0.75 ) );

    BOOST_CHECK_EQUAL( 0.75, regulator->requestVoltageChange( 0.75, VoltageRegulator::Single ) );
    BOOST_CHECK_EQUAL( 0.75, regulator->requestVoltageChange( 1.50, VoltageRegulator::Single ) );

    BOOST_CHECK_EQUAL( 1.50, regulator->requestVoltageChange( 1.50, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( 2.25, regulator->requestVoltageChange( 2.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( 3.00, regulator->requestVoltageChange( 2.50, VoltageRegulator::Inclusive ) );

    BOOST_CHECK_EQUAL( 1.50, regulator->requestVoltageChange( 1.50, VoltageRegulator::Exclusive ) );
    BOOST_CHECK_EQUAL( 1.50, regulator->requestVoltageChange( 2.00, VoltageRegulator::Exclusive ) );
    BOOST_CHECK_EQUAL( 2.25, regulator->requestVoltageChange( 2.50, VoltageRegulator::Exclusive ) );

    BOOST_CHECK_EQUAL( -0.75, regulator->requestVoltageChange( -0.75 ) );

    BOOST_CHECK_EQUAL( -0.75, regulator->requestVoltageChange( -0.75, VoltageRegulator::Single ) );
    BOOST_CHECK_EQUAL( -0.75, regulator->requestVoltageChange( -1.50, VoltageRegulator::Single ) );

    BOOST_CHECK_EQUAL( -1.50, regulator->requestVoltageChange( -1.50, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( -2.25, regulator->requestVoltageChange( -2.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( -3.00, regulator->requestVoltageChange( -2.50, VoltageRegulator::Inclusive ) );

    BOOST_CHECK_EQUAL( -1.50, regulator->requestVoltageChange( -1.50, VoltageRegulator::Exclusive ) );
    BOOST_CHECK_EQUAL( -1.50, regulator->requestVoltageChange( -2.00, VoltageRegulator::Exclusive ) );
    BOOST_CHECK_EQUAL( -2.25, regulator->requestVoltageChange( -2.50, VoltageRegulator::Exclusive ) );
}

BOOST_AUTO_TEST_CASE(test_IntegrityScan_Fail)
{
    BOOST_CHECK_THROW( regulator->executeIntegrityScan( "cap control" ), MissingAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}

BOOST_AUTO_TEST_CASE(test_IntegrityScan_MultiplePao)
{
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_NO_THROW( regulator->executeIntegrityScan( "cap control" ) );


    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 2203, signalMsg->getId() );     // ID of the 'Voltage' LitePoint
    BOOST_CHECK_EQUAL( "Integrity Scan", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    signalMsg = dynamic_cast<CtiSignalMsg *>(capController.signalMessages.back().get());

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 2202, signalMsg->getId() );     // ID of the 'SourceVoltage' LitePoint
    BOOST_CHECK_EQUAL( "Integrity Scan", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 2, capController.requestMessages.size() );

    auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1000, requestMsg->DeviceId() );  // PaoID of the 'Voltage' LitePoint
    BOOST_CHECK_EQUAL( "scan integrity", requestMsg->CommandString() );

    requestMsg = capController.requestMessages.back().get();

    BOOST_REQUIRE(requestMsg);

    BOOST_CHECK_EQUAL( 1001, requestMsg->DeviceId());  // PaoID of the 'SourceVoltage' LitePoint
    BOOST_CHECK_EQUAL( "scan integrity", requestMsg->CommandString());


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}

BOOST_AUTO_TEST_CASE(test_IntegrityScan_SinglePao)
{
    // Set the Voltage point pao ID to the same as the SourceVoltage point pao ID
    const auto sourceVoltagePaoId = attributes._attr[Attribute::SourceVoltage].getPaoId();
    attributes._attr[Attribute::Voltage].setPaoId(sourceVoltagePaoId);

    regulator->loadAttributes( &attributes );


    BOOST_CHECK_NO_THROW( regulator->executeIntegrityScan( "cap control" ) );


    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 2203, signalMsg->getId() );     // ID of the 'Voltage' LitePoint
    BOOST_CHECK_EQUAL( "Integrity Scan", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 2202, signalMsg->getId() );     // ID of the 'SourceVoltage' LitePoint
    BOOST_CHECK_EQUAL( "Integrity Scan", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    const auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1000, requestMsg->DeviceId() );  // PaoID of the 'SourceVoltage' and 'Voltage' LitePoints
    BOOST_CHECK_EQUAL( "scan integrity", requestMsg->CommandString() );


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}

BOOST_AUTO_TEST_CASE(test_IntegrityScan_nonstandard_user)
{
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_NO_THROW( regulator->executeIntegrityScan( "unit test" ) );


    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 2203, signalMsg->getId() );     // ID of the 'Voltage' LitePoint
    BOOST_CHECK_EQUAL( "Integrity Scan", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    signalMsg = dynamic_cast<CtiSignalMsg *>(capController.signalMessages.back().get());

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 2202, signalMsg->getId() );     // ID of the 'SourceVoltage' LitePoint
    BOOST_CHECK_EQUAL( "Integrity Scan", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 2, capController.requestMessages.size() );

    auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1000, requestMsg->DeviceId() );  // PaoID of the 'Voltage' LitePoint
    BOOST_CHECK_EQUAL( "scan integrity", requestMsg->CommandString() );

    requestMsg = capController.requestMessages.back().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1001, requestMsg->DeviceId());  // PaoID of the 'SourceVoltage' LitePoint
    BOOST_CHECK_EQUAL( "scan integrity", requestMsg->CommandString());


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_REQUIRE_EQUAL( 1, events.size() );

        Cti::CapControl::RegulatorEvent event = events.front();

        BOOST_CHECK_EQUAL( Cti::CapControl::RegulatorEvent::IntegrityScan,       event.eventType );
        BOOST_CHECK_EQUAL( 23456,                                                event.regulatorID );
        BOOST_CHECK_EQUAL( Cti::CapControl::Phase_Unknown,                       event.phase );
        BOOST_CHECK_EQUAL( "unit test",                                          event.userName );

        BOOST_CHECK( ! event.setPointValue );
        BOOST_CHECK( ! event.tapPosition );
    }
}

BOOST_AUTO_TEST_CASE(test_TapUp_Fail)
{
    BOOST_CHECK_THROW( regulator->adjustVoltage( 0.75 ), MissingAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}

BOOST_AUTO_TEST_CASE(test_TapUp_Success)
{
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_EQUAL( 0.75, regulator->adjustVoltage( 0.75 ) );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    const auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 3100, signalMsg->getId() );     // ID of the 'TapUp' LitePoint
    BOOST_CHECK_EQUAL( "Raise Tap Position", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    const auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1003, requestMsg->DeviceId() );  // PaoID of the 'TapUp' LitePoint
    BOOST_CHECK_EQUAL( "control close select pointid 3100",
                       requestMsg->CommandString() );   // ID of the 'TapUp' LitePoint


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_REQUIRE_EQUAL( 1, events.size() );

        Cti::CapControl::RegulatorEvent event = events.front();

        BOOST_CHECK_EQUAL( Cti::CapControl::RegulatorEvent::TapUp, event.eventType );
        BOOST_CHECK_EQUAL( 23456,                                  event.regulatorID );
        BOOST_CHECK_EQUAL( Cti::CapControl::Phase_Unknown,         event.phase );

        BOOST_CHECK( ! event.setPointValue );
        BOOST_CHECK( ! event.tapPosition );
    }
}

BOOST_AUTO_TEST_CASE(test_TapDown_Fail)
{
    BOOST_CHECK_THROW( regulator->adjustVoltage( -0.75 ), MissingAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}

BOOST_AUTO_TEST_CASE(test_TapDown_Success)
{
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_EQUAL( -0.75, regulator->adjustVoltage( -0.75 ) );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    const auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 3101, signalMsg->getId() );     // ID of the 'TapDown' LitePoint
    BOOST_CHECK_EQUAL( "Lower Tap Position", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    const auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1004, requestMsg->DeviceId() );  // PaoID of the 'TapDown' LitePoint
    BOOST_CHECK_EQUAL( "control close select pointid 3101",
                       requestMsg->CommandString() );   // ID of the 'TapDown' LitePoint


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_REQUIRE_EQUAL( 1, events.size() );

        Cti::CapControl::RegulatorEvent event = events.front();

        BOOST_CHECK_EQUAL( Cti::CapControl::RegulatorEvent::TapDown, event.eventType );
        BOOST_CHECK_EQUAL( 23456,                                    event.regulatorID );
        BOOST_CHECK_EQUAL( Cti::CapControl::Phase_Unknown,           event.phase );

        BOOST_CHECK( ! event.setPointValue );
        BOOST_CHECK( ! event.tapPosition );
    }
}

BOOST_AUTO_TEST_CASE(test_EnableKeepAlive_Fail)
{
    BOOST_CHECK_THROW( regulator->executeEnableKeepAlive( "cap control" ), MissingAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}

BOOST_AUTO_TEST_CASE(test_EnableKeepAlive_Success)
{
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive( "cap control" ) );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    const auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    const auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog value 123 select pointid 4200",
                       requestMsg->CommandString() );       // The new value and the ID of the 'KeepAlive' LitePoint


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}

BOOST_AUTO_TEST_CASE(test_DisableKeepAlive_Fail)
{
    BOOST_CHECK_THROW( regulator->executeDisableKeepAlive( "cap control" ), MissingAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}

BOOST_AUTO_TEST_CASE(test_DisableKeepAlive_Success)
{
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_NO_THROW( regulator->executeDisableKeepAlive( "cap control" ) );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    const auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    const auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog value 0 select pointid 4200",
                       requestMsg->CommandString() );       // The new value and the ID of the 'KeepAlive' LitePoint


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}

BOOST_AUTO_TEST_CASE(test_EnableRemoteControl_Fail)
{
    BOOST_CHECK_THROW( regulator->executeEnableRemoteControl( "unit test" ), MissingAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}

BOOST_AUTO_TEST_CASE(test_EnableRemoteControl_Success)
{
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_NO_THROW( regulator->executeEnableRemoteControl( "unit test" ) );


    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    const auto signalMsg_er = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg_er );

    BOOST_CHECK_EQUAL( 4200, signalMsg_er->getId() );       // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Enable Remote Control", signalMsg_er->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg_er->getAdditionalInfo() );
    BOOST_CHECK_EQUAL( 123, signalMsg_er->getPointValue() );    // 'KeepAlive' reload value

    const auto signalMsg_ka = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back().get() );

    BOOST_REQUIRE( signalMsg_ka );

    BOOST_CHECK_EQUAL( 4200, signalMsg_ka->getId() );       // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg_ka->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg_ka->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    const auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog value 123 select pointid 4200",
                       requestMsg->CommandString() );       // The new value and the ID of the 'KeepAlive' LitePoint


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_REQUIRE_EQUAL( 1, events.size() );

        Cti::CapControl::RegulatorEvent event = events.front();

        BOOST_CHECK_EQUAL( Cti::CapControl::RegulatorEvent::EnableRemoteControl, event.eventType );
        BOOST_CHECK_EQUAL( 23456,                                                event.regulatorID );
        BOOST_CHECK_EQUAL( Cti::CapControl::Phase_Unknown,                       event.phase );
        BOOST_CHECK_EQUAL( "unit test",                                          event.userName );

        BOOST_CHECK( ! event.setPointValue );
        BOOST_CHECK( ! event.tapPosition );
    }
}

BOOST_AUTO_TEST_CASE(test_DisableRemoteControl_Fail)
{
    BOOST_CHECK_THROW( regulator->executeDisableRemoteControl( "unit test" ), MissingAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}

BOOST_AUTO_TEST_CASE(test_DisableRemoteControl_Success)
{
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_NO_THROW( regulator->executeDisableRemoteControl( "unit test" ) );


    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    const auto signalMsg_dr = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg_dr );

    BOOST_CHECK_EQUAL( 4200, signalMsg_dr->getId() );       // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Disable Remote Control", signalMsg_dr->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg_dr->getAdditionalInfo() );
    BOOST_CHECK_EQUAL( 0, signalMsg_dr->getPointValue() );  // 'KeepAlive' reload value

    const auto signalMsg_ka = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back().get() );

    BOOST_REQUIRE( signalMsg_ka );

    BOOST_CHECK_EQUAL( 4200, signalMsg_ka->getId() );       // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg_ka->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg_ka->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    const auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog value 0 select pointid 4200",
                       requestMsg->CommandString() );       // The new value and the ID of the 'KeepAlive' LitePoint


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_REQUIRE_EQUAL( 1, events.size() );

        Cti::CapControl::RegulatorEvent event = events.front();

        BOOST_CHECK_EQUAL( Cti::CapControl::RegulatorEvent::DisableRemoteControl, event.eventType );
        BOOST_CHECK_EQUAL( 23456,                                                 event.regulatorID );
        BOOST_CHECK_EQUAL( Cti::CapControl::Phase_Unknown,                        event.phase );
        BOOST_CHECK_EQUAL( "unit test",                                           event.userName );

        BOOST_CHECK( ! event.setPointValue );
        BOOST_CHECK( ! event.tapPosition );
    }
}

BOOST_AUTO_TEST_SUITE_END()


BOOST_FIXTURE_TEST_SUITE( test_GangOperatedVoltageRegulator_SetPoint, gang_operated_voltage_regulator_fixture_setpoint )

BOOST_AUTO_TEST_CASE(test_requestVoltageChange_Single_and_Inclusive)
{
    regulator->loadAttributes( &attributes );

    {
        const std::vector<CtiPointDataMsg>    incomingPointData
        {
            { 2203, 120.0,  NormalQuality,  AnalogPointType },      // Load side voltage is 120 volts
            { 7000, 120.0,  NormalQuality,  AnalogPointType },      // Forward SetPoint is 120 volts
            { 7100,   2.0,  NormalQuality,  AnalogPointType },      // Forward Bandwidth is 2 volts
            { 3500,   3.0,  NormalQuality,  AnalogPointType }       // Tap is in position +3
        };

        for ( auto message : incomingPointData )
        {
            regulator->handlePointData( message );
        }
    }

    BOOST_CHECK_EQUAL( 1.50, regulator->requestVoltageChange( 0.75 ) );
    BOOST_CHECK_EQUAL( 1.50, regulator->requestVoltageChange( 1.25 ) );
    BOOST_CHECK_EQUAL( 1.50, regulator->requestVoltageChange( 2.50 ) );

    BOOST_CHECK_EQUAL( 1.50, regulator->requestVoltageChange( 0.75, VoltageRegulator::Single ) );
    BOOST_CHECK_EQUAL( 1.50, regulator->requestVoltageChange( 1.25, VoltageRegulator::Single ) );
    BOOST_CHECK_EQUAL( 1.50, regulator->requestVoltageChange( 2.50, VoltageRegulator::Single ) );

    BOOST_CHECK_EQUAL( 3.25, regulator->requestVoltageChange( 3.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( 4.25, regulator->requestVoltageChange( 4.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( 5.25, regulator->requestVoltageChange( 5.00, VoltageRegulator::Inclusive ) );

    BOOST_CHECK_EQUAL( -1.50, regulator->requestVoltageChange( -0.75 ) );
    BOOST_CHECK_EQUAL( -1.50, regulator->requestVoltageChange( -1.25 ) );
    BOOST_CHECK_EQUAL( -1.50, regulator->requestVoltageChange( -2.50 ) );

    BOOST_CHECK_EQUAL( -3.25, regulator->requestVoltageChange( -3.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( -4.25, regulator->requestVoltageChange( -4.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( -5.25, regulator->requestVoltageChange( -5.00, VoltageRegulator::Inclusive ) );


    regulator->handlePointData( { 2203, 120.8,  NormalQuality,  AnalogPointType } );    // Load side voltage is 120.8 volts

    BOOST_CHECK_EQUAL( 2.25, regulator->requestVoltageChange( 0.75 ) );
    BOOST_CHECK_EQUAL( 2.25, regulator->requestVoltageChange( 1.25 ) );
    BOOST_CHECK_EQUAL( 2.25, regulator->requestVoltageChange( 2.50 ) );

    BOOST_CHECK_EQUAL( 3.25, regulator->requestVoltageChange( 3.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( 4.25, regulator->requestVoltageChange( 4.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( 5.25, regulator->requestVoltageChange( 5.00, VoltageRegulator::Inclusive ) );

    BOOST_CHECK_EQUAL( -0.75, regulator->requestVoltageChange( -0.75 ) );
    BOOST_CHECK_EQUAL( -0.75, regulator->requestVoltageChange( -1.25 ) );
    BOOST_CHECK_EQUAL( -0.75, regulator->requestVoltageChange( -2.55 ) );

    BOOST_CHECK_EQUAL( -3.25, regulator->requestVoltageChange( -3.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( -4.25, regulator->requestVoltageChange( -4.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( -5.25, regulator->requestVoltageChange( -5.00, VoltageRegulator::Inclusive ) );


    regulator->handlePointData( { 2203, 119.2,  NormalQuality,  AnalogPointType } );    // Load side voltage is 119.2 volts

    BOOST_CHECK_EQUAL( 0.75, regulator->requestVoltageChange( 0.75 ) );
    BOOST_CHECK_EQUAL( 0.75, regulator->requestVoltageChange( 1.25 ) );
    BOOST_CHECK_EQUAL( 0.75, regulator->requestVoltageChange( 2.50 ) );

    BOOST_CHECK_EQUAL( 3.25, regulator->requestVoltageChange( 3.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( 4.25, regulator->requestVoltageChange( 4.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( 5.25, regulator->requestVoltageChange( 5.00, VoltageRegulator::Inclusive ) );

    BOOST_CHECK_EQUAL( -2.25, regulator->requestVoltageChange( -0.75 ) );
    BOOST_CHECK_EQUAL( -2.25, regulator->requestVoltageChange( -1.25 ) );
    BOOST_CHECK_EQUAL( -2.25, regulator->requestVoltageChange( -2.55 ) );

    BOOST_CHECK_EQUAL( -3.25, regulator->requestVoltageChange( -3.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( -4.25, regulator->requestVoltageChange( -4.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( -5.25, regulator->requestVoltageChange( -5.00, VoltageRegulator::Inclusive ) );
}

BOOST_AUTO_TEST_CASE(test_requestVoltageChange_Exclusive)
{
    regulator->loadAttributes( &attributes );

    {
        const std::vector<CtiPointDataMsg>    incomingPointData
        {
            { 2203, 120.0,  NormalQuality,  AnalogPointType },      // Load side voltage is 120 volts
            { 7000, 120.0,  NormalQuality,  AnalogPointType },      // Forward SetPoint is 120 volts
            { 7100,   2.0,  NormalQuality,  AnalogPointType },      // Forward Bandwidth is 2 volts
            { 3500,   3.0,  NormalQuality,  AnalogPointType }       // Tap is in position +3
        };

        for ( auto message : incomingPointData )
        {
            regulator->handlePointData( message );
        }
    }

    BOOST_CHECK_CLOSE( 3.75, regulator->requestVoltageChange( 3.00, VoltageRegulator::Exclusive ), 1e-6 );
    BOOST_CHECK_CLOSE( 4.50, regulator->requestVoltageChange( 4.00, VoltageRegulator::Exclusive ), 1e-6 );
    BOOST_CHECK_CLOSE( 5.25, regulator->requestVoltageChange( 5.00, VoltageRegulator::Exclusive ), 1e-6 );

    BOOST_CHECK_CLOSE( -3.75, regulator->requestVoltageChange( -3.00, VoltageRegulator::Exclusive ), 1e-6 );
    BOOST_CHECK_CLOSE( -4.50, regulator->requestVoltageChange( -4.00, VoltageRegulator::Exclusive ), 1e-6 );
    BOOST_CHECK_CLOSE( -5.25, regulator->requestVoltageChange( -5.00, VoltageRegulator::Exclusive ), 1e-6 );


    regulator->handlePointData( { 2203, 120.8,  NormalQuality,  AnalogPointType } );    // Load side voltage is 120.8 volts

    BOOST_CHECK_CLOSE( 4.55, regulator->requestVoltageChange( 3.00, VoltageRegulator::Exclusive ), 1e-6 );
    BOOST_CHECK_CLOSE( 5.30, regulator->requestVoltageChange( 4.00, VoltageRegulator::Exclusive ), 1e-6 );
    BOOST_CHECK_CLOSE( 6.05, regulator->requestVoltageChange( 5.00, VoltageRegulator::Exclusive ), 1e-6 );

    BOOST_CHECK_CLOSE( -2.95, regulator->requestVoltageChange( -3.00, VoltageRegulator::Exclusive ), 1e-6 );
    BOOST_CHECK_CLOSE( -3.70, regulator->requestVoltageChange( -4.00, VoltageRegulator::Exclusive ), 1e-6 );
    BOOST_CHECK_CLOSE( -4.45, regulator->requestVoltageChange( -5.00, VoltageRegulator::Exclusive ), 1e-6 );


    regulator->handlePointData( { 2203, 119.2,  NormalQuality,  AnalogPointType } );    // Load side voltage is 119.2 volts

    BOOST_CHECK_CLOSE( 2.95, regulator->requestVoltageChange( 3.00, VoltageRegulator::Exclusive ), 1e-6 );
    BOOST_CHECK_CLOSE( 3.70, regulator->requestVoltageChange( 4.00, VoltageRegulator::Exclusive ), 1e-6 );
    BOOST_CHECK_CLOSE( 4.45, regulator->requestVoltageChange( 5.00, VoltageRegulator::Exclusive ), 1e-6 );

    BOOST_CHECK_CLOSE( -4.55, regulator->requestVoltageChange( -3.00, VoltageRegulator::Exclusive ), 1e-6 );
    BOOST_CHECK_CLOSE( -5.30, regulator->requestVoltageChange( -4.00, VoltageRegulator::Exclusive ), 1e-6 );
    BOOST_CHECK_CLOSE( -6.05, regulator->requestVoltageChange( -5.00, VoltageRegulator::Exclusive ), 1e-6 );
}

BOOST_AUTO_TEST_CASE(test_GangOperatedVoltageRegulator_RaiseSetPoint_Fail)
{
    BOOST_CHECK_THROW( regulator->adjustVoltage( 0.75 ), MissingAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}

BOOST_AUTO_TEST_CASE(test_GangOperatedVoltageRegulator_RaiseSetPoint_Success)
{
    regulator->loadAttributes( &attributes );


    CtiPointDataMsg setPointData( 7000, 120.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( setPointData );


    BOOST_CHECK_CLOSE( 0.75, regulator->adjustVoltage( 0.75 ),  1e-6 );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    const auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 7000, signalMsg->getId() );     // ID of the 'SetPoint' LitePoint
    BOOST_CHECK_EQUAL( "Raise Set Point", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    const auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1020, requestMsg->DeviceId() );  // PaoID of the 'SetPoint' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog value 120.750000 select pointid 7000",
                       requestMsg->CommandString() );   // The new value and the ID of the 'SetPoint' LitePoint


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_REQUIRE_EQUAL( 1, events.size() );

        Cti::CapControl::RegulatorEvent event = events.front();

        BOOST_CHECK_EQUAL( Cti::CapControl::RegulatorEvent::IncreaseSetPoint, event.eventType );
        BOOST_CHECK_EQUAL( 23456,                                             event.regulatorID );
        BOOST_CHECK_EQUAL( Cti::CapControl::Phase_Unknown,                    event.phase );
        BOOST_CHECK_CLOSE( 120.75,                                           *event.setPointValue,     1e-6 );

        BOOST_CHECK( ! event.tapPosition );
    }
}

BOOST_AUTO_TEST_CASE(test_GangOperatedVoltageRegulator_LowerSetPoint_Fail)
{
    BOOST_CHECK_THROW( regulator->adjustVoltage( -0.75 ), MissingAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}

BOOST_AUTO_TEST_CASE(test_LowerSetPoint_Success)
{
    regulator->loadAttributes( &attributes );


    CtiPointDataMsg setPointData( 7000, 120.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( setPointData );

    CtiPointDataMsg tapPositionData( 3500, 3.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( tapPositionData );


    BOOST_CHECK_CLOSE( -0.75, regulator->adjustVoltage( -0.75 ),    1e-6 );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    const auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 7000, signalMsg->getId() );     // ID of the 'SetPoint' LitePoint
    BOOST_CHECK_EQUAL( "Lower Set Point", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    const auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1020, requestMsg->DeviceId() );  // PaoID of the 'SetPoint' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog value 119.250000 select pointid 7000",
                       requestMsg->CommandString() );   // The new value and the ID of the 'SetPoint' LitePoint


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_REQUIRE_EQUAL( 1, events.size() );

        Cti::CapControl::RegulatorEvent event = events.front();

        BOOST_CHECK_EQUAL( Cti::CapControl::RegulatorEvent::DecreaseSetPoint, event.eventType );
        BOOST_CHECK_EQUAL( 23456,                                             event.regulatorID );
        BOOST_CHECK_EQUAL( Cti::CapControl::Phase_Unknown,                    event.phase );
        BOOST_CHECK_CLOSE( 119.25,                                           *event.setPointValue,     1e-6 );
        BOOST_CHECK_EQUAL( 3,                                                *event.tapPosition );
    }
}

BOOST_AUTO_TEST_CASE(test_LowerSetPoint_Cogeneration_ForwardFlow_Success)
{
    regulator->loadAttributes( &attributes );

    const std::vector<CtiPointDataMsg>    incomingPointData
    {
        { 7450,   5.0,  NormalQuality,  AnalogPointType },      // Regulator is in Cogeneration mode
        { 7400,   0.0,  NormalQuality,  AnalogPointType },      // Forward Flow
        { 7000, 120.0,  NormalQuality,  AnalogPointType },      // Forward SetPoint is 120 volts
        { 3500,   3.0,  NormalQuality,  AnalogPointType }       // Tap is in position +3
    };

    for ( auto message : incomingPointData )
    {
        regulator->handlePointData( message );
    }
    

    BOOST_CHECK_CLOSE( -0.75, regulator->adjustVoltage( -0.75 ),    1e-6 );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    const auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 7000, signalMsg->getId() );     // ID of the 'SetPoint' LitePoint
    BOOST_CHECK_EQUAL( "Lower Set Point", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    const auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1020, requestMsg->DeviceId() );  // PaoID of the 'SetPoint' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog value 119.250000 select pointid 7000",
                       requestMsg->CommandString() );   // The new value and the ID of the 'SetPoint' LitePoint


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_REQUIRE_EQUAL( 1, events.size() );

        Cti::CapControl::RegulatorEvent event = events.front();

        BOOST_CHECK_EQUAL( Cti::CapControl::RegulatorEvent::DecreaseSetPoint, event.eventType );
        BOOST_CHECK_EQUAL( 23456,                                             event.regulatorID );
        BOOST_CHECK_EQUAL( Cti::CapControl::Phase_Unknown,                    event.phase );
        BOOST_CHECK_CLOSE( 119.25,                                           *event.setPointValue,     1e-6 );
        BOOST_CHECK_EQUAL( 3,                                                *event.tapPosition );
    }
}

BOOST_AUTO_TEST_CASE(test_LowerSetPoint_Cogeneration_ReverseFlow_Success)
{
    regulator->loadAttributes( &attributes );

    const std::vector<CtiPointDataMsg>    incomingPointData
    {
        { 7450,   5.0,  NormalQuality,  AnalogPointType },      // Regulator is in Cogeneration mode
        { 7400,   1.0,  NormalQuality,  AnalogPointType },      // Reverse Flow
        { 7200, 121.5,  NormalQuality,  AnalogPointType },      // Reverse SetPoint is 121.5 volts
        { 3500,   4.0,  NormalQuality,  AnalogPointType }       // Tap is in position +4
    };

    for ( auto message : incomingPointData )
    {
        regulator->handlePointData( message );
    }
    

    BOOST_CHECK_CLOSE( -0.75, regulator->adjustVoltage( -0.75 ),    1e-6 );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    const auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 7200, signalMsg->getId() );     // ID of the 'SetPoint' LitePoint
    BOOST_CHECK_EQUAL( "Lower Set Point", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    const auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1022, requestMsg->DeviceId() );  // PaoID of the 'SetPoint' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog value 120.750000 select pointid 7200",
                       requestMsg->CommandString() );   // The new value and the ID of the 'SetPoint' LitePoint


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_REQUIRE_EQUAL( 1, events.size() );

        Cti::CapControl::RegulatorEvent event = events.front();

        BOOST_CHECK_EQUAL( Cti::CapControl::RegulatorEvent::DecreaseSetPoint, event.eventType );
        BOOST_CHECK_EQUAL( 23456,                                             event.regulatorID );
        BOOST_CHECK_EQUAL( Cti::CapControl::Phase_Unknown,                    event.phase );
        BOOST_CHECK_CLOSE( 120.75,                                           *event.setPointValue,     1e-6 );
        BOOST_CHECK_EQUAL( 4,                                                *event.tapPosition );
    }
}

BOOST_AUTO_TEST_CASE(test_Mode_Documentation_setPoint)
{
    regulator->loadAttributes( &attributes );
    // give set points an initial value so regulator->setSetPointValue() can be called directly during the test
    regulator->handlePointData({ 7000, 120.0,  NormalQuality,  AnalogPointType }); 
    regulator->handlePointData({ 7200, 120.0,  NormalQuality,  AnalogPointType });

    struct ControlModeAttributes
    {
        ControlPolicy::ControlModes     controlMode;
        Attribute forwardFlowSetPoint;
        Attribute reverseFlowSetPoint;
    };

    const auto fwd_sp = Attribute::ForwardSetPoint;
    const auto rev_sp = Attribute::ReverseSetPoint;

    const std::map<double, ControlModeAttributes> testCases
    {
        { -1.0, { ControlPolicy::LockedForward        ,  fwd_sp,  fwd_sp } },
        {  0.0, { ControlPolicy::LockedForward        ,  fwd_sp,  fwd_sp } },
        {  1.0, { ControlPolicy::LockedReverse        ,  rev_sp,  rev_sp } },
        {  2.0, { ControlPolicy::ReverseIdle          ,  fwd_sp,  fwd_sp } },
        {  3.0, { ControlPolicy::Bidirectional        ,  fwd_sp,  rev_sp } },
        {  4.0, { ControlPolicy::NeutralIdle          ,  fwd_sp,  fwd_sp } },
        {  5.0, { ControlPolicy::Cogeneration         ,  fwd_sp,  rev_sp } },
        {  6.0, { ControlPolicy::ReactiveBidirectional,  fwd_sp,  fwd_sp } },
        {  7.0, { ControlPolicy::BiasBidirectional    ,  fwd_sp,  rev_sp } },
        {  8.0, { ControlPolicy::BiasCogeneration     ,  fwd_sp,  fwd_sp } },
        {  9.0, { ControlPolicy::ReverseCogeneration  ,  fwd_sp,  rev_sp } },
        { 10.0, { ControlPolicy::LockedForward        ,  fwd_sp,  fwd_sp } }
    };

    for ( auto testCase : testCases )
    {
        const auto& controlModeValue = testCase.first;
        const auto& controlModeAttributes = testCase.second;

        // set control policy from testCases map
        regulator->handlePointData({ 7450, controlModeValue, NormalQuality, AnalogPointType });
        BOOST_CHECK_EQUAL(regulator->getConfigurationMode(), controlModeAttributes.controlMode);

        for ( auto value : { 0.0, 1.0 } )
        {
            // forward flow when i = 0.0, reverse flow when i = 1.0
            regulator->handlePointData({ 7400, value, NormalQuality, AnalogPointType });

            // issue set point control with regulator
            auto actions = regulator->setSetPointValue(120.0);
            BOOST_REQUIRE(actions.first && actions.second);

            // litePoint to use for comparison in this iteration, out of bounds exception if attribute not in _attr map
            auto litePoint = attributes._attr.at(regulator->isReverseFlowDetected() ? controlModeAttributes.reverseFlowSetPoint : controlModeAttributes.forwardFlowSetPoint);

            // validate that the set point control went to the correct pointId using the signal message
            const auto signalMsg = dynamic_cast<CtiSignalMsg *>(actions.first.get());
            BOOST_REQUIRE(signalMsg);
            BOOST_CHECK_EQUAL(litePoint.getPointId(), signalMsg->getId()); // ID of SetPoint associated w/ current control mode and flow

            // validate that the set point control went to the correct pointId using the request message
            const auto requestMsg = actions.second.get();
            BOOST_REQUIRE(requestMsg);
            BOOST_CHECK_EQUAL(litePoint.getPaoId(), requestMsg->DeviceId()); // PaoID of SetPoint associated w/ current control mode and flow
        }
    }
}

BOOST_AUTO_TEST_CASE(test_Mode_Documentation_bandwidth)
{
    regulator->loadAttributes(&attributes);
    // give set points an initial value so regulator->setSetPointValue() can be called directly during the test
    regulator->handlePointData({ 7000, 120.0,  NormalQuality,  AnalogPointType });
    regulator->handlePointData({ 7200, 120.0,  NormalQuality,  AnalogPointType });

    //cogen mode
    regulator->handlePointData( { 7450, 5.0, NormalQuality, AnalogPointType } );
    BOOST_CHECK_EQUAL( regulator->getConfigurationMode(), ControlPolicy::Cogeneration );

    //reverse flow
    regulator->handlePointData({ 7400, 1, NormalQuality, AnalogPointType });

    // set reverseBandwidth to a small value
    regulator->handlePointData({ 7300, 1, NormalQuality, AnalogPointType });
    // issue reverseSetPoint control and save new value
    double reverseChangeDistance = std::abs(regulator->requestVoltageChange(0.75, VoltageRegulator::Inclusive));

    // back to forward flow
    regulator->handlePointData({ 7400, 0, NormalQuality, AnalogPointType });
    // set forwardBandwidth to a large value
    regulator->handlePointData({ 7100, 10, NormalQuality, AnalogPointType });
    // issue reverseSetPoint control and save new value
    double forwardChangeDistance = std::abs(regulator->requestVoltageChange(0.75, VoltageRegulator::Inclusive));

    // assert that distance between original setpoint and reverseBandwidth is smaller than between
    // the original setpoint and the forwardBandwidth
    BOOST_CHECK_LT(reverseChangeDistance, forwardChangeDistance);
}

// Below here are tests for the new functionality in 9.1.0, namely the 3 new control modes, 7, 8, 9

BOOST_AUTO_TEST_CASE( test_Determine_Power_Flow_Situation_input_output_codes )
{
    using PFS = VoltageRegulator::PowerFlowSituations;

    regulator->loadAttributes( &attributes );

    struct the_test_cases
    {
        std::vector<double> inputs;
        PFS                 expected_output;
    }
    t[]
    {   //  mode    revflow     controlpfr   indeterminate      output
        { { 0.0,    0.0,        0.0,         0.0 },              PFS::OK },
        { { 0.0,    0.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 0.0,    0.0,        1.0,         0.0 },              PFS::ReverseControlPowerFlow },
        { { 0.0,    0.0,        1.0,         1.0 },              PFS::IndeterminateFlow },
        { { 0.0,    1.0,        0.0,         0.0 },              PFS::ReverseFlow },
        { { 0.0,    1.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 0.0,    1.0,        1.0,         0.0 },              PFS::ReverseFlow },
        { { 0.0,    1.0,        1.0,         1.0 },              PFS::IndeterminateFlow },

        { { 1.0,    0.0,        0.0,         0.0 },              PFS::ReverseInstallation },
        { { 1.0,    0.0,        0.0,         1.0 },              PFS::ReverseInstallation },
        { { 1.0,    0.0,        1.0,         0.0 },              PFS::ReverseInstallation },
        { { 1.0,    0.0,        1.0,         1.0 },              PFS::ReverseInstallation },
        { { 1.0,    1.0,        0.0,         0.0 },              PFS::ReverseInstallation },
        { { 1.0,    1.0,        0.0,         1.0 },              PFS::ReverseInstallation },
        { { 1.0,    1.0,        1.0,         0.0 },              PFS::ReverseInstallation },
        { { 1.0,    1.0,        1.0,         1.0 },              PFS::ReverseInstallation },

        { { 2.0,    0.0,        0.0,         0.0 },              PFS::ReverseControlPowerFlow },
        { { 2.0,    0.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 2.0,    0.0,        1.0,         0.0 },              PFS::OK },
        { { 2.0,    0.0,        1.0,         1.0 },              PFS::IndeterminateFlow },
        { { 2.0,    1.0,        0.0,         0.0 },              PFS::ReverseFlow },
        { { 2.0,    1.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 2.0,    1.0,        1.0,         0.0 },              PFS::ReverseFlow },
        { { 2.0,    1.0,        1.0,         1.0 },              PFS::IndeterminateFlow },

        { { 3.0,    0.0,        0.0,         0.0 },              PFS::OK },
        { { 3.0,    0.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 3.0,    0.0,        1.0,         0.0 },              PFS::ReverseControlPowerFlow },
        { { 3.0,    0.0,        1.0,         1.0 },              PFS::IndeterminateFlow },
        { { 3.0,    1.0,        0.0,         0.0 },              PFS::ReverseFlow },
        { { 3.0,    1.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 3.0,    1.0,        1.0,         0.0 },              PFS::ReverseFlow },
        { { 3.0,    1.0,        1.0,         1.0 },              PFS::IndeterminateFlow },

        { { 4.0,    0.0,        0.0,         0.0 },              PFS::ReverseControlPowerFlow },
        { { 4.0,    0.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 4.0,    0.0,        1.0,         0.0 },              PFS::OK },
        { { 4.0,    0.0,        1.0,         1.0 },              PFS::IndeterminateFlow },
        { { 4.0,    1.0,        0.0,         0.0 },              PFS::ReverseFlow },
        { { 4.0,    1.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 4.0,    1.0,        1.0,         0.0 },              PFS::ReverseFlow },
        { { 4.0,    1.0,        1.0,         1.0 },              PFS::IndeterminateFlow },

        { { 5.0,    0.0,        0.0,         0.0 },              PFS::OK },
        { { 5.0,    0.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 5.0,    0.0,        1.0,         0.0 },              PFS::ReverseControlPowerFlow },
        { { 5.0,    0.0,        1.0,         1.0 },              PFS::IndeterminateFlow },
        { { 5.0,    1.0,        0.0,         0.0 },              PFS::OK },
        { { 5.0,    1.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 5.0,    1.0,        1.0,         0.0 },              PFS::ReverseControlPowerFlow },
        { { 5.0,    1.0,        1.0,         1.0 },              PFS::IndeterminateFlow },

        { { 6.0,    0.0,        0.0,         0.0 },              PFS::UnsupportedMode },
        { { 6.0,    0.0,        0.0,         1.0 },              PFS::UnsupportedMode },
        { { 6.0,    0.0,        1.0,         0.0 },              PFS::UnsupportedMode },
        { { 6.0,    0.0,        1.0,         1.0 },              PFS::UnsupportedMode },
        { { 6.0,    1.0,        0.0,         0.0 },              PFS::UnsupportedMode },
        { { 6.0,    1.0,        0.0,         1.0 },              PFS::UnsupportedMode },
        { { 6.0,    1.0,        1.0,         0.0 },              PFS::UnsupportedMode },
        { { 6.0,    1.0,        1.0,         1.0 },              PFS::UnsupportedMode },

        { { 7.0,    0.0,        0.0,         0.0 },              PFS::OK },
        { { 7.0,    0.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 7.0,    0.0,        1.0,         0.0 },              PFS::ReverseControlPowerFlow },
        { { 7.0,    0.0,        1.0,         1.0 },              PFS::IndeterminateFlow },
        { { 7.0,    1.0,        0.0,         0.0 },              PFS::ReverseFlow },
        { { 7.0,    1.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 7.0,    1.0,        1.0,         0.0 },              PFS::ReverseFlow },
        { { 7.0,    1.0,        1.0,         1.0 },              PFS::IndeterminateFlow },

        { { 8.0,    0.0,        0.0,         0.0 },              PFS::OK },
        { { 8.0,    0.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 8.0,    0.0,        1.0,         0.0 },              PFS::ReverseControlPowerFlow },
        { { 8.0,    0.0,        1.0,         1.0 },              PFS::IndeterminateFlow },
        { { 8.0,    1.0,        0.0,         0.0 },              PFS::OK },
        { { 8.0,    1.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 8.0,    1.0,        1.0,         0.0 },              PFS::ReverseControlPowerFlow },
        { { 8.0,    1.0,        1.0,         1.0 },              PFS::IndeterminateFlow },

        { { 9.0,    0.0,        0.0,         0.0 },              PFS::ReverseInstallation },
        { { 9.0,    0.0,        0.0,         1.0 },              PFS::ReverseInstallation },
        { { 9.0,    0.0,        1.0,         0.0 },              PFS::ReverseInstallation },
        { { 9.0,    0.0,        1.0,         1.0 },              PFS::ReverseInstallation },
        { { 9.0,    1.0,        0.0,         0.0 },              PFS::ReverseInstallation },
        { { 9.0,    1.0,        0.0,         1.0 },              PFS::ReverseInstallation },
        { { 9.0,    1.0,        1.0,         0.0 },              PFS::ReverseInstallation },
        { { 9.0,    1.0,        1.0,         1.0 },              PFS::ReverseInstallation }
    };

    for ( auto & z : t )
    {
        regulator->handlePointData( { 7450, z.inputs[0], NormalQuality, AnalogPointType } );    // control mode
        regulator->handlePointData( { 7400, z.inputs[1], NormalQuality, AnalogPointType } );    // reverse power flow
        regulator->handlePointData( { 7550, z.inputs[2], NormalQuality, AnalogPointType } );    // control power flow reverse
        regulator->handlePointData( { 7500, z.inputs[3], NormalQuality, AnalogPointType } );    // power flow indeterminate

        BOOST_CHECK( z.expected_output == regulator->determinePowerFlowSituation() );
    }
}

BOOST_AUTO_TEST_SUITE_END()

BOOST_FIXTURE_TEST_SUITE( test_GangOperatedVoltageRegulator_SetPoint_Reversed_Installation, gang_operated_voltage_regulator_fixture_setpoint_reversed_install )

BOOST_AUTO_TEST_CASE( test_Determine_Power_Flow_Situation_input_output_codes_reversed_install )
{
    using PFS = VoltageRegulator::PowerFlowSituations;

    regulator->loadAttributes( &attributes );

    struct the_test_cases
    {
        std::vector<double> inputs;
        PFS                 expected_output;
    }
    t[]
    {   //  mode    revflow     controlpfr   indeterminate      output
        { { 0.0,    0.0,        0.0,         0.0 },              PFS::ReverseInstallation },
        { { 0.0,    0.0,        0.0,         1.0 },              PFS::ReverseInstallation },
        { { 0.0,    0.0,        1.0,         0.0 },              PFS::ReverseInstallation },
        { { 0.0,    0.0,        1.0,         1.0 },              PFS::ReverseInstallation },
        { { 0.0,    1.0,        0.0,         0.0 },              PFS::ReverseInstallation },
        { { 0.0,    1.0,        0.0,         1.0 },              PFS::ReverseInstallation },
        { { 0.0,    1.0,        1.0,         0.0 },              PFS::ReverseInstallation },
        { { 0.0,    1.0,        1.0,         1.0 },              PFS::ReverseInstallation },

        { { 1.0,    0.0,        0.0,         0.0 },              PFS::ReverseFlow },
        { { 1.0,    0.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 1.0,    0.0,        1.0,         0.0 },              PFS::ReverseFlow },
        { { 1.0,    0.0,        1.0,         1.0 },              PFS::IndeterminateFlow },
        { { 1.0,    1.0,        0.0,         0.0 },              PFS::ReverseControlPowerFlow },
        { { 1.0,    1.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 1.0,    1.0,        1.0,         0.0 },              PFS::OK },
        { { 1.0,    1.0,        1.0,         1.0 },              PFS::IndeterminateFlow },

        { { 2.0,    0.0,        0.0,         0.0 },              PFS::ReverseInstallation },
        { { 2.0,    0.0,        0.0,         1.0 },              PFS::ReverseInstallation },
        { { 2.0,    0.0,        1.0,         0.0 },              PFS::ReverseInstallation },
        { { 2.0,    0.0,        1.0,         1.0 },              PFS::ReverseInstallation },
        { { 2.0,    1.0,        0.0,         0.0 },              PFS::ReverseInstallation },
        { { 2.0,    1.0,        0.0,         1.0 },              PFS::ReverseInstallation },
        { { 2.0,    1.0,        1.0,         0.0 },              PFS::ReverseInstallation },
        { { 2.0,    1.0,        1.0,         1.0 },              PFS::ReverseInstallation },

        { { 3.0,    0.0,        0.0,         0.0 },              PFS::ReverseFlow },
        { { 3.0,    0.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 3.0,    0.0,        1.0,         0.0 },              PFS::ReverseFlow },
        { { 3.0,    0.0,        1.0,         1.0 },              PFS::IndeterminateFlow },
        { { 3.0,    1.0,        0.0,         0.0 },              PFS::ReverseControlPowerFlow },
        { { 3.0,    1.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 3.0,    1.0,        1.0,         0.0 },              PFS::OK },
        { { 3.0,    1.0,        1.0,         1.0 },              PFS::IndeterminateFlow },

        { { 4.0,    0.0,        0.0,         0.0 },              PFS::ReverseInstallation },
        { { 4.0,    0.0,        0.0,         1.0 },              PFS::ReverseInstallation },
        { { 4.0,    0.0,        1.0,         0.0 },              PFS::ReverseInstallation },
        { { 4.0,    0.0,        1.0,         1.0 },              PFS::ReverseInstallation },
        { { 4.0,    1.0,        0.0,         0.0 },              PFS::ReverseInstallation },
        { { 4.0,    1.0,        0.0,         1.0 },              PFS::ReverseInstallation },
        { { 4.0,    1.0,        1.0,         0.0 },              PFS::ReverseInstallation },
        { { 4.0,    1.0,        1.0,         1.0 },              PFS::ReverseInstallation },

        { { 5.0,    0.0,        0.0,         0.0 },              PFS::ReverseInstallation },
        { { 5.0,    0.0,        0.0,         1.0 },              PFS::ReverseInstallation },
        { { 5.0,    0.0,        1.0,         0.0 },              PFS::ReverseInstallation },
        { { 5.0,    0.0,        1.0,         1.0 },              PFS::ReverseInstallation },
        { { 5.0,    1.0,        0.0,         0.0 },              PFS::ReverseInstallation },
        { { 5.0,    1.0,        0.0,         1.0 },              PFS::ReverseInstallation },
        { { 5.0,    1.0,        1.0,         0.0 },              PFS::ReverseInstallation },
        { { 5.0,    1.0,        1.0,         1.0 },              PFS::ReverseInstallation },

        { { 6.0,    0.0,        0.0,         0.0 },              PFS::UnsupportedMode },
        { { 6.0,    0.0,        0.0,         1.0 },              PFS::UnsupportedMode },
        { { 6.0,    0.0,        1.0,         0.0 },              PFS::UnsupportedMode },
        { { 6.0,    0.0,        1.0,         1.0 },              PFS::UnsupportedMode },
        { { 6.0,    1.0,        0.0,         0.0 },              PFS::UnsupportedMode },
        { { 6.0,    1.0,        0.0,         1.0 },              PFS::UnsupportedMode },
        { { 6.0,    1.0,        1.0,         0.0 },              PFS::UnsupportedMode },
        { { 6.0,    1.0,        1.0,         1.0 },              PFS::UnsupportedMode },

        { { 7.0,    0.0,        0.0,         0.0 },              PFS::ReverseFlow },
        { { 7.0,    0.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 7.0,    0.0,        1.0,         0.0 },              PFS::ReverseFlow },
        { { 7.0,    0.0,        1.0,         1.0 },              PFS::IndeterminateFlow },
        { { 7.0,    1.0,        0.0,         0.0 },              PFS::ReverseControlPowerFlow },
        { { 7.0,    1.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 7.0,    1.0,        1.0,         0.0 },              PFS::OK },
        { { 7.0,    1.0,        1.0,         1.0 },              PFS::IndeterminateFlow },

        { { 8.0,    0.0,        0.0,         0.0 },              PFS::ReverseInstallation },
        { { 8.0,    0.0,        0.0,         1.0 },              PFS::ReverseInstallation },
        { { 8.0,    0.0,        1.0,         0.0 },              PFS::ReverseInstallation },
        { { 8.0,    0.0,        1.0,         1.0 },              PFS::ReverseInstallation },
        { { 8.0,    1.0,        0.0,         0.0 },              PFS::ReverseInstallation },
        { { 8.0,    1.0,        0.0,         1.0 },              PFS::ReverseInstallation },
        { { 8.0,    1.0,        1.0,         0.0 },              PFS::ReverseInstallation },
        { { 8.0,    1.0,        1.0,         1.0 },              PFS::ReverseInstallation },

        { { 9.0,    0.0,        0.0,         0.0 },              PFS::OK },
        { { 9.0,    0.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 9.0,    0.0,        1.0,         0.0 },              PFS::ReverseControlPowerFlow },
        { { 9.0,    0.0,        1.0,         1.0 },              PFS::IndeterminateFlow },
        { { 9.0,    1.0,        0.0,         0.0 },              PFS::ReverseControlPowerFlow },
        { { 9.0,    1.0,        0.0,         1.0 },              PFS::IndeterminateFlow },
        { { 9.0,    1.0,        1.0,         0.0 },              PFS::OK },
        { { 9.0,    1.0,        1.0,         1.0 },              PFS::IndeterminateFlow }
    };

    for ( auto & z : t )
    {
        regulator->handlePointData( { 7450, z.inputs[0], NormalQuality, AnalogPointType } );    // control mode
        regulator->handlePointData( { 7400, z.inputs[1], NormalQuality, AnalogPointType } );    // reverse power flow
        regulator->handlePointData( { 7550, z.inputs[2], NormalQuality, AnalogPointType } );    // control power flow reverse
        regulator->handlePointData( { 7500, z.inputs[3], NormalQuality, AnalogPointType } );    // power flow indeterminate

        BOOST_CHECK( z.expected_output == regulator->determinePowerFlowSituation() );
    }
}

BOOST_AUTO_TEST_SUITE_END()


BOOST_FIXTURE_TEST_SUITE( test_GangOperatedVoltageRegulator_DirectTap_DefaultTapLimits, gang_operated_voltage_regulator_fixture_direct_tap )

BOOST_AUTO_TEST_CASE( test_verify_default_tap_position_limits )
{
    regulator->loadAttributes( &attributes );

    BOOST_CHECK_EQUAL(  -16,  regulator->getMinTapPosition()  );
    BOOST_CHECK_EQUAL(   16,  regulator->getMaxTapPosition()  );
}

BOOST_AUTO_TEST_SUITE_END()


BOOST_FIXTURE_TEST_SUITE( test_GangOperatedVoltageRegulator_DirectTap_CustomTapLimits, gang_operated_voltage_regulator_fixture_direct_tap_with_limits )

BOOST_AUTO_TEST_CASE( test_verify_custom_tap_position_limits )
{
    regulator->loadAttributes( &attributes );

    BOOST_CHECK_EQUAL(  -10,  regulator->getMinTapPosition()  );
    BOOST_CHECK_EQUAL(   12,  regulator->getMaxTapPosition()  );
}

BOOST_AUTO_TEST_SUITE_END()

