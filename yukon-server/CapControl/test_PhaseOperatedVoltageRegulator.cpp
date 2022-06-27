#include <boost/test/unit_test.hpp>

#include "capcontroller.h"
#include "VoltageRegulatorManager.h"
#include "PhaseOperatedVoltageRegulator.h"
#include "mgr_config.h"
#include "std_helper.h"
#include "RegulatorEvents.h"

#include "capcontrol_test_helpers.h"
#include "boost_test_helpers.h"

// Objects
using Cti::CapControl::VoltageRegulator;
using Cti::CapControl::VoltageRegulatorManager;
using Cti::CapControl::PhaseOperatedVoltageRegulator;

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

        fixtureConfig->insertValue( "regulatorHeartbeatMode",   "INCREMENT" );
        fixtureConfig->insertValue( "regulatorHeartbeatPeriod", "0" );
        fixtureConfig->insertValue( "regulatorHeartbeatValue",  "0" );
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


struct phase_operated_voltage_regulator_fixture_core
{
    struct TestCtiCapController : public CtiCapController
    {
        TestCtiCapController()
        {
            CtiCapController::setInstance(this);
        }

        void sendMessageToDispatch(CtiMessage* message, Cti::CallSite cs) override
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
                catch( AttributeNotFound::exception & ex )
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
                    { 4200,  AnalogPointType, "KeepAlive", 1007, 10001, "", "", 1.0, 0 } },
                { Attribute::AutoRemoteControl,
                    { 5600,  StatusPointType, "AutoRemoteControl", 1009, 6, "", "", 1.0, 0 } },
                { Attribute::TapPosition,
                    { 3500,  AnalogPointType, "TapPosition", 1013, 3, "", "", 1.0, 0 } },
                { Attribute::Terminate,
                    { 7500,  StatusPointType, "Terminate", 1022, 9, "", "control close", 1.0, 0 } },
                { Attribute::AutoBlockEnable,
                    { 8100,  StatusPointType, "AutoBlock", 1026, 12, "", "control close", 1.0, 0 } },
                { Attribute::ForwardSetPoint,
                    { 7000,  AnalogPointType, "Forward SetPoint", 1020, 10007, "", "", 0.1, 0 } },
                { Attribute::ForwardBandwidth,
                    { 7100,  AnalogPointType, "Forward Bandwidth", 1021, 8, "", "", 0.1, 0 } },
                { Attribute::ReverseSetPoint,
                    { 7200,  AnalogPointType, "Reverse SetPoint", 1022, 10017, "", "", 0.1, 0 } },
                { Attribute::ReverseBandwidth,
                    { 7300,  AnalogPointType, "Reverse Bandwidth", 1023, 18, "", "", 0.1, 0 } },
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

    phase_operated_voltage_regulator_fixture_core()
        :   regulator( new PhaseOperatedVoltageRegulator )
    {
        regulator->setPaoId( 23456 );
        regulator->setPaoName( "Test Regulator #1" );
        regulator->setPaoCategory( "CAPCONTROL" );
        regulator->setPaoType( VoltageRegulator::PhaseOperatedVoltageRegulator );
        regulator->setRegulatorTimeout(std::chrono::seconds{ 100 });
    }
};


struct phase_operated_voltage_regulator_fixture_direct_tap
    :   phase_operated_voltage_regulator_fixture_core,
        regulator_device_config_direct_tap
{
};


struct phase_operated_voltage_regulator_fixture_setpoint
    :   phase_operated_voltage_regulator_fixture_core,
        regulator_device_config_set_point
{
};

}


BOOST_FIXTURE_TEST_SUITE( test_PhaseOperatedVoltageRegulator_DirectTap, phase_operated_voltage_regulator_fixture_direct_tap )

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

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 2202, signalMsg->getId() );     // ID of the 'SourceVoltage' LitePoint
    BOOST_CHECK_EQUAL( "Integrity Scan", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 2, capController.requestMessages.size() );

    auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1000, requestMsg->DeviceId() );  // PaoID of the 'SourceVoltage' LitePoint
    BOOST_CHECK_EQUAL( "scan integrity", requestMsg->CommandString() );

    requestMsg = capController.requestMessages.back().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1001, requestMsg->DeviceId() );  // PaoID of the 'Voltage' LitePoint
    BOOST_CHECK_EQUAL( "scan integrity", requestMsg->CommandString() );


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

    auto requestMsg = capController.requestMessages.front().get();

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

BOOST_AUTO_TEST_CASE(test_EnableKeepAliveFromRemoteMode_Success)
{
    regulator->loadAttributes( &attributes );

    // put regulator into remote mode

    CtiPointDataMsg remoteMode( 5600, 1.0, NormalQuality, StatusPointType );

    regulator->handlePointData( remoteMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::RemoteMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 100.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( keepAlive );


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
    BOOST_CHECK_EQUAL( "putvalue analog value 101 select pointid 4200",
                       requestMsg->CommandString() );       // The new value and the ID of the 'KeepAlive' LitePoint


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}

BOOST_AUTO_TEST_CASE(test_EnableKeepAliveFromRemoteMode_Success_with_Rollover)
{
    regulator->loadAttributes( &attributes );

    // put regulator into remote mode

    CtiPointDataMsg remoteMode( 5600, 1.0, NormalQuality, StatusPointType );

    regulator->handlePointData( remoteMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::RemoteMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 32767.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( keepAlive );


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
    BOOST_CHECK_EQUAL( "putvalue analog value 0 select pointid 4200",
                       requestMsg->CommandString() );       // The new value and the ID of the 'KeepAlive' LitePoint


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}

BOOST_AUTO_TEST_CASE(test_EnableKeepAliveFromAutoMode_Success)
{
    regulator->loadAttributes( &attributes );

    // put regulator into auto mode

    CtiPointDataMsg autoMode( 5600, 0.0, NormalQuality, StatusPointType );

    regulator->handlePointData( autoMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::LocalMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 100.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( keepAlive );

    // Here is the new sequence of events - messages are generated by repeated calls to executeEnableKeepAlive

    // Call: #1

    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive( "cap control" ) );

    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[0].get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    auto requestMsg = capController.requestMessages[0].get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog value 101 select pointid 4200",
                       requestMsg->CommandString() );       // The new value and the ID of the 'KeepAlive' LitePoint

    {   // update the keep alive
        CtiPointDataMsg keepAlive( 4200, 101.0, NormalQuality, AnalogPointType );

        regulator->handlePointData( keepAlive );
    }

    // Call: #2

    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive( "cap control" ) );

    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[1].get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    BOOST_REQUIRE_EQUAL( 2, capController.requestMessages.size() );

    requestMsg = capController.requestMessages[1].get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog value 102 select pointid 4200",
                       requestMsg->CommandString() );       // The new value and the ID of the 'KeepAlive' LitePoint

    {   // update the keep alive
        CtiPointDataMsg keepAlive( 4200, 102.0, NormalQuality, AnalogPointType );
        regulator->handlePointData( keepAlive );

        // regulator goes to 'remote' mode
        CtiPointDataMsg remoteMode( 5600, 1.0, NormalQuality, StatusPointType );
        regulator->handlePointData( remoteMode );

        // auto block enable shows false
        CtiPointDataMsg autoBlockEnable( 8100, 0.0, NormalQuality, StatusPointType );
        regulator->handlePointData( autoBlockEnable );
    }

    // Call: #3

    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive( "cap control" ) );

    BOOST_REQUIRE_EQUAL( 4, capController.signalMessages.size() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[2].get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[3].get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 8100, signalMsg->getId() );             // PaoID of the 'AutoBlock' LitePoint
    BOOST_CHECK_EQUAL( "Auto Block Enable", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    BOOST_REQUIRE_EQUAL( 4, capController.requestMessages.size() );

    requestMsg = capController.requestMessages[2].get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog value 103 select pointid 4200",
                       requestMsg->CommandString() );       // The new value and the ID of the 'KeepAlive' LitePoint

    requestMsg = capController.requestMessages[3].get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1026, requestMsg->DeviceId() );      // PaoID of the 'AutoBlock' LitePoint
    BOOST_CHECK_EQUAL( "control close select pointid 8100",
                       requestMsg->CommandString() );       // ID of the 'AutoBlock' LitePoint

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}

BOOST_AUTO_TEST_CASE(test_EnableKeepAliveFromAutoMode_Success_with_Rollover)
{
    regulator->loadAttributes( &attributes );

    // put regulator into auto mode

    CtiPointDataMsg autoMode( 5600, 0.0, NormalQuality, StatusPointType );

    regulator->handlePointData( autoMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::LocalMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 32766.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( keepAlive );

    // Here is the new sequence of events - messages are generated by repeated calls to executeEnableKeepAlive

    // Call: #1

    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive( "cap control" ) );

    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[0].get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    auto requestMsg = capController.requestMessages[0].get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog value 32767 select pointid 4200",
                       requestMsg->CommandString() );       // The new value and the ID of the 'KeepAlive' LitePoint

    {   // update the keep alive
        CtiPointDataMsg keepAlive( 4200, 32767.0, NormalQuality, AnalogPointType );

        regulator->handlePointData( keepAlive );
    }

    // Call: #2

    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive( "cap control" ) );

    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[1].get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    BOOST_REQUIRE_EQUAL( 2, capController.requestMessages.size() );

    requestMsg = capController.requestMessages[1].get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog value 0 select pointid 4200",
                       requestMsg->CommandString() );       // The new value and the ID of the 'KeepAlive' LitePoint

    {   // update the keep alive
        CtiPointDataMsg keepAlive( 4200, 0.0, NormalQuality, AnalogPointType );
        regulator->handlePointData( keepAlive );

        // regulator goes to 'remote' mode
        CtiPointDataMsg remoteMode( 5600, 1.0, NormalQuality, StatusPointType );
        regulator->handlePointData( remoteMode );

        // auto block enable shows false
        CtiPointDataMsg autoBlockEnable( 8100, 0.0, NormalQuality, StatusPointType );
        regulator->handlePointData( autoBlockEnable );
    }

    // Call: #3

    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive( "cap control" ) );

    BOOST_REQUIRE_EQUAL( 4, capController.signalMessages.size() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[2].get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[3].get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 8100, signalMsg->getId() );             // PaoID of the 'AutoBlock' LitePoint
    BOOST_CHECK_EQUAL( "Auto Block Enable", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    BOOST_REQUIRE_EQUAL( 4, capController.requestMessages.size() );

    requestMsg = capController.requestMessages[2].get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog value 1 select pointid 4200",
                       requestMsg->CommandString() );       // The new value and the ID of the 'KeepAlive' LitePoint

    requestMsg = capController.requestMessages[3].get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1026, requestMsg->DeviceId() );      // PaoID of the 'AutoBlock' LitePoint
    BOOST_CHECK_EQUAL( "control close select pointid 8100",
                       requestMsg->CommandString() );       // ID of the 'AutoBlock' LitePoint

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

    BOOST_CHECK_EQUAL( 7500, signalMsg->getId() );     // ID of the 'Terminate' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    const auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1022, requestMsg->DeviceId() );  // PaoID of the 'Terminate' LitePoint
    BOOST_CHECK_EQUAL( "control close select pointid 7500",
                       requestMsg->CommandString() );   // ID of the 'Terminate' LitePoint


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

BOOST_AUTO_TEST_CASE(test_EnableRemoteControlFromRemoteMode_Success)
{
    regulator->loadAttributes( &attributes );

    // put regulator into remote mode

    CtiPointDataMsg remoteMode( 5600, 1.0, NormalQuality, StatusPointType );

    regulator->handlePointData( remoteMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::RemoteMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 100.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( keepAlive );


    BOOST_CHECK_NO_THROW( regulator->executeEnableRemoteControl( "unit test" ) );


    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Enable Remote Control", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    const auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog value 101 select pointid 4200",
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

BOOST_AUTO_TEST_CASE(test_EnableRemoteControlFromAutoMode_Success)
{
    regulator->loadAttributes( &attributes );

    // put regulator into auto mode

    CtiPointDataMsg autoMode( 5600, 0.0, NormalQuality, StatusPointType );

    regulator->handlePointData( autoMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::LocalMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 100.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( keepAlive );


    BOOST_CHECK_NO_THROW( regulator->executeEnableRemoteControl( "cap control" ) );

/*
 *  This guys functionality is changed.... - only generates the first 'activate' message now
 */

//    BOOST_REQUIRE_EQUAL( 4, capController.signalMessages.size() );
    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[0].get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Enable Remote Control", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[1].get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

/*
    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[2] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[3] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 8100, signalMsg->getId() );          // PaoID of the 'AutoBlock' LitePoint
    BOOST_CHECK_EQUAL( "Auto Block Enable", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );
*/

//    BOOST_REQUIRE_EQUAL( 3, capController.requestMessages.size() );
    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    const auto requestMsg = capController.requestMessages[0].get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog value 101 select pointid 4200",
                       requestMsg->CommandString() );       // The new value and the ID of the 'KeepAlive' LitePoint
/*
    requestMsg = capController.requestMessages[1];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog value 102 select pointid 4200",
                       requestMsg->CommandString() );       // The new value and the ID of the 'KeepAlive' LitePoint

    requestMsg = capController.requestMessages[2];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1026, requestMsg->DeviceId() );      // PaoID of the 'AutoBlock' LitePoint
    BOOST_CHECK_EQUAL( "control close select pointid 8100",
                       requestMsg->CommandString() );       // ID of the 'AutoBlock' LitePoint
*/

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_REQUIRE_EQUAL( 1, events.size() );

        Cti::CapControl::RegulatorEvent event = events.front();

        BOOST_CHECK_EQUAL( Cti::CapControl::RegulatorEvent::EnableRemoteControl, event.eventType );
        BOOST_CHECK_EQUAL( 23456,                                                event.regulatorID );
        BOOST_CHECK_EQUAL( Cti::CapControl::Phase_Unknown,                       event.phase );
        BOOST_CHECK_EQUAL( "cap control",                                        event.userName );

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

    auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Disable Remote Control", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 7500, signalMsg->getId() );          // ID of the 'Terminate' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    const auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1022, requestMsg->DeviceId() );      // PaoID of the 'Terminate' LitePoint
    BOOST_CHECK_EQUAL( "control close select pointid 7500",
                       requestMsg->CommandString() );       // ID of the 'Terminate' LitePoint


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

BOOST_AUTO_TEST_CASE(test_QueryAutoRemoteStatus_Fail)
{
    BOOST_CHECK_THROW( regulator->getOperatingMode(), MissingAttribute );
}

BOOST_AUTO_TEST_CASE(test_QueryAutoRemoteStatus_Success)
{
    regulator->loadAttributes( &attributes );

    BOOST_CHECK_NO_THROW( regulator->getOperatingMode() );

    // Haven't received a point update since creation - we don't know our operating mode

    BOOST_CHECK_EQUAL( VoltageRegulator::UnknownMode, regulator->getOperatingMode() );

    // point update that tells us we are in local mode

    CtiPointDataMsg pointDataMsg( 5600, 0.0, NormalQuality, StatusPointType );

    regulator->handlePointData( pointDataMsg );

    BOOST_CHECK_EQUAL( VoltageRegulator::LocalMode, regulator->getOperatingMode() );

    // point update that tells us we are in remote mode

    pointDataMsg.setValue(1.0);

    regulator->handlePointData( pointDataMsg );

    BOOST_CHECK_EQUAL( VoltageRegulator::RemoteMode, regulator->getOperatingMode() );
}

BOOST_AUTO_TEST_CASE(test_TapUp_Success_with_Phase_A_info)
{
    regulator->loadAttributes( &attributes );
    regulator->setPhase( Cti::CapControl::Phase_A );


    BOOST_CHECK_EQUAL( 0.75, regulator->adjustVoltage( 0.75 ) );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    const auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 3100, signalMsg->getId() );     // ID of the 'TapUp' LitePoint
    BOOST_CHECK_EQUAL( "Raise Tap Position - Phase: A", signalMsg->getText() );
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
        BOOST_CHECK_EQUAL( Cti::CapControl::Phase_A,               event.phase );

        BOOST_CHECK( ! event.setPointValue );
        BOOST_CHECK( ! event.tapPosition );
    }
}

BOOST_AUTO_TEST_SUITE_END()


BOOST_FIXTURE_TEST_SUITE( test_PhaseOperatedVoltageRegulator_SetPoint, phase_operated_voltage_regulator_fixture_setpoint )

BOOST_AUTO_TEST_CASE(test_RaiseSetPoint_Fail)
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

BOOST_AUTO_TEST_CASE(test_RaiseSetPoint_Success)
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
    BOOST_CHECK_EQUAL( "putvalue analog value 1208 select pointid 7000",
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

BOOST_AUTO_TEST_CASE(test_LowerSetPoint_Fail)
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

    CtiPointDataMsg tapPositionData( 3500, -3.0, NormalQuality, AnalogPointType );

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
    BOOST_CHECK_EQUAL( "putvalue analog value 1193 select pointid 7000",
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
        BOOST_CHECK_EQUAL( -3,                                               *event.tapPosition );
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
    BOOST_CHECK_EQUAL( "putvalue analog value 1193 select pointid 7000",
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
    BOOST_CHECK_EQUAL( "putvalue analog value 1208 select pointid 7200",
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

BOOST_AUTO_TEST_CASE(test_EnableKeepAliveFromAutoMode_SetPointControl_SuppressAutoBlockEnable)
{
    regulator->loadAttributes(&attributes);

    // put regulator into auto mode

    CtiPointDataMsg autoMode(5600, 0.0, NormalQuality, StatusPointType);

    regulator->handlePointData(autoMode);

    BOOST_CHECK_EQUAL(VoltageRegulator::LocalMode, regulator->getOperatingMode());

    // set our keep alive timer

    CtiPointDataMsg keepAlive(4200, 100.0, NormalQuality, AnalogPointType);

    regulator->handlePointData(keepAlive);

    // Here is the new sequence of events - messages are generated by repeated calls to executeEnableKeepAlive

    // Call: #1

    BOOST_CHECK_NO_THROW(regulator->executeEnableKeepAlive("cap control"));

    BOOST_REQUIRE_EQUAL(1, capController.signalMessages.size());

    auto signalMsg = dynamic_cast<CtiSignalMsg *>(capController.signalMessages[0].get());

    BOOST_REQUIRE(signalMsg);

    BOOST_CHECK_EQUAL(4200, signalMsg->getId());          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL("Keep Alive", signalMsg->getText());
    BOOST_CHECK_EQUAL("Voltage Regulator Name: Test Regulator #1",
        signalMsg->getAdditionalInfo());

    BOOST_REQUIRE_EQUAL(1, capController.requestMessages.size());

    auto requestMsg = capController.requestMessages[0].get();

    BOOST_REQUIRE(requestMsg);

    BOOST_CHECK_EQUAL(1007, requestMsg->DeviceId());      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL("putvalue analog value 101 select pointid 4200",
        requestMsg->CommandString());       // The new value and the ID of the 'KeepAlive' LitePoint

    {   // update the keep alive
        CtiPointDataMsg keepAlive(4200, 101.0, NormalQuality, AnalogPointType);

        regulator->handlePointData(keepAlive);
    }

    // Call: #2

    BOOST_CHECK_NO_THROW(regulator->executeEnableKeepAlive("cap control"));

    BOOST_REQUIRE_EQUAL(2, capController.signalMessages.size());

    signalMsg = dynamic_cast<CtiSignalMsg *>(capController.signalMessages[1].get());

    BOOST_REQUIRE(signalMsg);

    BOOST_CHECK_EQUAL(4200, signalMsg->getId());          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL("Keep Alive", signalMsg->getText());
    BOOST_CHECK_EQUAL("Voltage Regulator Name: Test Regulator #1",
        signalMsg->getAdditionalInfo());

    BOOST_REQUIRE_EQUAL(2, capController.requestMessages.size());

    requestMsg = capController.requestMessages[1].get();

    BOOST_REQUIRE(requestMsg);

    BOOST_CHECK_EQUAL(1007, requestMsg->DeviceId());      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL("putvalue analog value 102 select pointid 4200",
        requestMsg->CommandString());       // The new value and the ID of the 'KeepAlive' LitePoint

    {   // update the keep alive
        CtiPointDataMsg keepAlive(4200, 102.0, NormalQuality, AnalogPointType);
        regulator->handlePointData(keepAlive);

        // regulator goes to 'remote' mode
        CtiPointDataMsg remoteMode(5600, 1.0, NormalQuality, StatusPointType);
        regulator->handlePointData(remoteMode);

        // auto block enable shows false
        CtiPointDataMsg autoBlockEnable(8100, 0.0, NormalQuality, StatusPointType);
        regulator->handlePointData(autoBlockEnable);
    }

    // Call: #3
    // In this case, we should NOT recieve an Auto Block Enable signalMessage or requestMessage because
    // the regulator is in SetPoint control mode. See test_EnableKeepAliveFromAutoMode_Success for
    // the case when the regulator is not in SetPoint control mode and we should receive the Auto Block Enable 
    // signalMessage and requestMessage.

    BOOST_CHECK_NO_THROW(regulator->executeEnableKeepAlive("cap control"));

    BOOST_REQUIRE_EQUAL(3, capController.signalMessages.size());

    signalMsg = dynamic_cast<CtiSignalMsg *>(capController.signalMessages[2].get());

    BOOST_REQUIRE(signalMsg);

    BOOST_CHECK_EQUAL(4200, signalMsg->getId());          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL("Keep Alive", signalMsg->getText());
    BOOST_CHECK_EQUAL("Voltage Regulator Name: Test Regulator #1",
        signalMsg->getAdditionalInfo());

    BOOST_REQUIRE_EQUAL(3, capController.requestMessages.size());

    requestMsg = capController.requestMessages[2].get();

    BOOST_REQUIRE(requestMsg);

    BOOST_CHECK_EQUAL(1007, requestMsg->DeviceId());      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL("putvalue analog value 103 select pointid 4200",
        requestMsg->CommandString());       // The new value and the ID of the 'KeepAlive' LitePoint

    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents(events, test_limiter);

        BOOST_CHECK_EQUAL(0, events.size());
    }
}

BOOST_AUTO_TEST_SUITE_END()
