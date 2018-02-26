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
        virtual void manualCapBankControl(CtiRequestMsg* pilRequest, CtiMultiMsg* multiMsg = NULL)
        {
            requestMessages.emplace_back( pilRequest );
        }
        virtual void enqueueEventLogEntry(const Cti::CapControl::EventLogEntry &event)
        {
            eventMessages.push_back(event);
        }

        std::vector< std::unique_ptr<CtiMessage> >      signalMessages;
        std::vector< std::unique_ptr<CtiRequestMsg> >   requestMessages;
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
                    CTILOG_EXCEPTION_WARN( dout, ex );
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
                    { 7450,  AnalogPointType, "Regulator Control Mode", 1026, 21, "", "", 1.0, 0 } }
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

}


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

BOOST_AUTO_TEST_CASE(test_requestVoltageChange)
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

    // Inclusive and Exclusive are the same for SetPoint controller regulators

    BOOST_CHECK_EQUAL( 3.25, regulator->requestVoltageChange( 3.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( 4.25, regulator->requestVoltageChange( 4.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( 5.25, regulator->requestVoltageChange( 5.00, VoltageRegulator::Inclusive ) );

    BOOST_CHECK_EQUAL( 3.25, regulator->requestVoltageChange( 3.00, VoltageRegulator::Exclusive ) );
    BOOST_CHECK_EQUAL( 4.25, regulator->requestVoltageChange( 4.00, VoltageRegulator::Exclusive ) );
    BOOST_CHECK_EQUAL( 5.25, regulator->requestVoltageChange( 5.00, VoltageRegulator::Exclusive ) );

    BOOST_CHECK_EQUAL( -1.50, regulator->requestVoltageChange( -0.75 ) );
    BOOST_CHECK_EQUAL( -1.50, regulator->requestVoltageChange( -1.25 ) );
    BOOST_CHECK_EQUAL( -1.50, regulator->requestVoltageChange( -2.50 ) );

    BOOST_CHECK_EQUAL( -3.25, regulator->requestVoltageChange( -3.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( -4.25, regulator->requestVoltageChange( -4.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( -5.25, regulator->requestVoltageChange( -5.00, VoltageRegulator::Inclusive ) );

    BOOST_CHECK_EQUAL( -3.25, regulator->requestVoltageChange( -3.00, VoltageRegulator::Exclusive ) );
    BOOST_CHECK_EQUAL( -4.25, regulator->requestVoltageChange( -4.00, VoltageRegulator::Exclusive ) );
    BOOST_CHECK_EQUAL( -5.25, regulator->requestVoltageChange( -5.00, VoltageRegulator::Exclusive ) );


    regulator->handlePointData( { 2203, 120.8,  NormalQuality,  AnalogPointType } );    // Load side voltage is 120.8 volts

    BOOST_CHECK_EQUAL( 2.25, regulator->requestVoltageChange( 0.75 ) );
    BOOST_CHECK_EQUAL( 2.25, regulator->requestVoltageChange( 1.25 ) );
    BOOST_CHECK_EQUAL( 2.25, regulator->requestVoltageChange( 2.50 ) );

    BOOST_CHECK_EQUAL( 3.25, regulator->requestVoltageChange( 3.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( 4.25, regulator->requestVoltageChange( 4.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( 5.25, regulator->requestVoltageChange( 5.00, VoltageRegulator::Inclusive ) );

    BOOST_CHECK_EQUAL( 3.25, regulator->requestVoltageChange( 3.00, VoltageRegulator::Exclusive ) );
    BOOST_CHECK_EQUAL( 4.25, regulator->requestVoltageChange( 4.00, VoltageRegulator::Exclusive ) );
    BOOST_CHECK_EQUAL( 5.25, regulator->requestVoltageChange( 5.00, VoltageRegulator::Exclusive ) );

    BOOST_CHECK_EQUAL( -0.75, regulator->requestVoltageChange( -0.75 ) );
    BOOST_CHECK_EQUAL( -0.75, regulator->requestVoltageChange( -1.25 ) );
    BOOST_CHECK_EQUAL( -0.75, regulator->requestVoltageChange( -2.55 ) );

    BOOST_CHECK_EQUAL( -3.25, regulator->requestVoltageChange( -3.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( -4.25, regulator->requestVoltageChange( -4.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( -5.25, regulator->requestVoltageChange( -5.00, VoltageRegulator::Inclusive ) );

    BOOST_CHECK_EQUAL( -3.25, regulator->requestVoltageChange( -3.00, VoltageRegulator::Exclusive ) );
    BOOST_CHECK_EQUAL( -4.25, regulator->requestVoltageChange( -4.00, VoltageRegulator::Exclusive ) );
    BOOST_CHECK_EQUAL( -5.25, regulator->requestVoltageChange( -5.00, VoltageRegulator::Exclusive ) );


    regulator->handlePointData( { 2203, 119.2,  NormalQuality,  AnalogPointType } );    // Load side voltage is 119.2 volts

    BOOST_CHECK_EQUAL( 0.75, regulator->requestVoltageChange( 0.75 ) );
    BOOST_CHECK_EQUAL( 0.75, regulator->requestVoltageChange( 1.25 ) );
    BOOST_CHECK_EQUAL( 0.75, regulator->requestVoltageChange( 2.50 ) );

    BOOST_CHECK_EQUAL( 3.25, regulator->requestVoltageChange( 3.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( 4.25, regulator->requestVoltageChange( 4.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( 5.25, regulator->requestVoltageChange( 5.00, VoltageRegulator::Inclusive ) );

    BOOST_CHECK_EQUAL( 3.25, regulator->requestVoltageChange( 3.00, VoltageRegulator::Exclusive ) );
    BOOST_CHECK_EQUAL( 4.25, regulator->requestVoltageChange( 4.00, VoltageRegulator::Exclusive ) );
    BOOST_CHECK_EQUAL( 5.25, regulator->requestVoltageChange( 5.00, VoltageRegulator::Exclusive ) );

    BOOST_CHECK_EQUAL( -2.25, regulator->requestVoltageChange( -0.75 ) );
    BOOST_CHECK_EQUAL( -2.25, regulator->requestVoltageChange( -1.25 ) );
    BOOST_CHECK_EQUAL( -2.25, regulator->requestVoltageChange( -2.55 ) );

    BOOST_CHECK_EQUAL( -3.25, regulator->requestVoltageChange( -3.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( -4.25, regulator->requestVoltageChange( -4.00, VoltageRegulator::Inclusive ) );
    BOOST_CHECK_EQUAL( -5.25, regulator->requestVoltageChange( -5.00, VoltageRegulator::Inclusive ) );

    BOOST_CHECK_EQUAL( -3.25, regulator->requestVoltageChange( -3.00, VoltageRegulator::Exclusive ) );
    BOOST_CHECK_EQUAL( -4.25, regulator->requestVoltageChange( -4.00, VoltageRegulator::Exclusive ) );
    BOOST_CHECK_EQUAL( -5.25, regulator->requestVoltageChange( -5.00, VoltageRegulator::Exclusive ) );
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

BOOST_AUTO_TEST_CASE(test_Mode_Documentation)
{
    regulator->loadAttributes( &attributes );

    const std::map<double, ControlPolicy::ControlModes> testCases
    {
        {   -1.0,   ControlPolicy::LockedForward            },
        {    0.0,   ControlPolicy::LockedForward            },
        {    1.0,   ControlPolicy::LockedReverse            },
        {    2.0,   ControlPolicy::ReverseIdle              },
        {    3.0,   ControlPolicy::Bidirectional            },
        {    4.0,   ControlPolicy::NeutralIdle              },
        {    5.0,   ControlPolicy::Cogeneration             },
        {    6.0,   ControlPolicy::ReactiveBidirectional    },
        {    7.0,   ControlPolicy::BiasBidirectional        },
        {    8.0,   ControlPolicy::BiasCogeneration         },
        {    9.0,   ControlPolicy::ReverseCogeneration      },
        {   10.0,   ControlPolicy::LockedForward            }
    };

    CtiPointDataMsg message { 7450,  0.0,  NormalQuality,  AnalogPointType };

    for ( auto testCase : testCases )
    {
        message.setValue( testCase.first );

        regulator->handlePointData( message );

        BOOST_CHECK_EQUAL( regulator->getConfigurationMode(), testCase.second );
    }
}

BOOST_AUTO_TEST_SUITE_END()
