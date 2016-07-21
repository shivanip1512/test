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
using Cti::CapControl::MissingPointAttribute;


struct phase_operated_voltage_regulator_fixture_core
{
    struct TestCtiCapController : public CtiCapController
    {
        TestCtiCapController()
        {
            CtiCapController::setInstance(this);
        }

        ~TestCtiCapController()
        {
            for each ( const CtiMessage * p in signalMessages )
            {
                delete p;
            }
            for each ( CtiRequestMsg *p in requestMessages )
            {
                delete p;
            }
        }

        virtual void sendMessageToDispatch(CtiMessage* message, Cti::CallSite cs) override
        {
            signalMessages.push_back(message);
        }
        virtual void manualCapBankControl(CtiRequestMsg* pilRequest, CtiMultiMsg* multiMsg = NULL)
        {
            requestMessages.push_back(pilRequest);
        }
        virtual void enqueueEventLogEntry(const Cti::CapControl::EventLogEntry &event)
        {
            eventMessages.push_back(event);
        }

        std::vector<CtiMessage*>    signalMessages;
        std::vector<CtiRequestMsg*> requestMessages;
        Cti::CapControl::EventLogEntries eventMessages;
    }
    capController;

    struct TestAttributeService : public AttributeService
    {
        virtual LitePoint getPointByPaoAndAttribute(int paoId, const PointAttribute& attribute)
        {
            if ( boost::optional<LitePoint> point = Cti::mapFind( _attr, attribute.value() ) )
            {
                return *point;
            }

            return LitePoint();
        }

        std::map<PointAttribute::Attribute, LitePoint>  _attr;

        TestAttributeService()
        {
            _attr = decltype( _attr )
            {
                { PointAttribute::VoltageXAttribute,
                    { 2202,  AnalogPointType, "VoltageX", 1000, 1, "", "", 1.0, 0 } },
                { PointAttribute::VoltageYAttribute,
                    { 2203,  AnalogPointType, "VoltageY", 1001, 2, "", "", 1.0, 0 } },
                { PointAttribute::TapUpAttribute,
                    { 3100,  StatusPointType, "TapUp", 1003, 4, "", "control close", 1.0, 0 } },
                { PointAttribute::TapDownAttribute,
                    { 3101,  StatusPointType, "TapDown", 1004, 5, "", "control close", 1.0, 0 } },
                { PointAttribute::KeepAliveAttribute,
                    { 4200,  AnalogPointType, "KeepAlive", 1007, 10001, "", "", 1.0, 0 } },
                { PointAttribute::AutoRemoteControlAttribute,
                    { 5600,  StatusPointType, "AutoRemoteControl", 1009, 6, "", "", 1.0, 0 } },
                { PointAttribute::TapPositionAttribute,
                    { 3500,  AnalogPointType, "TapPosition", 1013, 3, "", "", 1.0, 0 } },
                { PointAttribute::TerminateAttribute,
                    { 7500,  StatusPointType, "Terminate", 1022, 9, "", "control close", 1.0, 0 } },
                { PointAttribute::AutoBlockEnableAttribute,
                    { 8100,  StatusPointType, "AutoBlock", 1026, 12, "", "control close", 1.0, 0 } },
                { PointAttribute::ForwardSetPointAttribute,
                    { 7000,  AnalogPointType, "SetPoint", 1020, 10007, "", "", 0.1, 0 } },
                { PointAttribute::ForwardBandwidthAttribute,
                    { 7100,  AnalogPointType, "Bandwidth", 1021, 8, "", "", 1.0, 0 } }
            };
        }
    }
    attributes;

    Cti::Test::use_in_unit_tests_only   test_limiter;

    boost::shared_ptr<Cti::Test::test_DeviceConfig>    fixtureConfig;

    Cti::Test::Override_ConfigManager overrideConfigManager;

    VoltageRegulatorManager::SharedPtr  regulator;

    phase_operated_voltage_regulator_fixture_core()
        :   regulator( new PhaseOperatedVoltageRegulator ),
            fixtureConfig( new Cti::Test::test_DeviceConfig ),
            overrideConfigManager( fixtureConfig )
    {
        regulator->setPaoId( 23456 );
        regulator->setPaoName( "Test Regulator #1" );
        regulator->setPaoCategory( "CAPCONTROL" );
        regulator->setPaoType( VoltageRegulator::PhaseOperatedVoltageRegulator );

        fixtureConfig->insertValue( "voltageChangePerTap", "0.75" );
        fixtureConfig->insertValue( "heartbeatPeriod",     "0" );
        fixtureConfig->insertValue( "heartbeatValue",      "0" );

        fixtureConfig->insertValue( "heartbeatMode",       "INCREMENT" );
    }
};


struct phase_operated_voltage_regulator_fixture_direct_tap : phase_operated_voltage_regulator_fixture_core
{
    phase_operated_voltage_regulator_fixture_direct_tap()
        :   phase_operated_voltage_regulator_fixture_core()
    {
        fixtureConfig->insertValue( "voltageControlMode",  "DIRECT_TAP" );
    }
};


struct phase_operated_voltage_regulator_fixture_setpoint : phase_operated_voltage_regulator_fixture_core
{
    phase_operated_voltage_regulator_fixture_setpoint()
        :   phase_operated_voltage_regulator_fixture_core()
    {
        fixtureConfig->insertValue( "voltageControlMode",  "SET_POINT" );
    }
};


BOOST_AUTO_TEST_SUITE( test_PhaseOperatedVoltageRegulator )


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_IntegrityScan_Fail, phase_operated_voltage_regulator_fixture_direct_tap)
{
    BOOST_CHECK_THROW( regulator->executeIntegrityScan( "cap control" ), MissingPointAttribute );

    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_IntegrityScan_Success, phase_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );

    BOOST_CHECK_NO_THROW( regulator->executeIntegrityScan( "cap control" ) );


    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 2202, signalMsg->getId() );     // ID of the 'VoltageX' LitePoint
    BOOST_CHECK_EQUAL( "Integrity Scan", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 2203, signalMsg->getId() );     // ID of the 'VoltageY' LitePoint
    BOOST_CHECK_EQUAL( "Integrity Scan", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 2, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1000, requestMsg->DeviceId() );  // PaoID of the 'VoltageX' LitePoint
    BOOST_CHECK_EQUAL( "scan integrity", requestMsg->CommandString() );

    requestMsg = capController.requestMessages.back();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1001, requestMsg->DeviceId() );  // PaoID of the 'VoltageY' LitePoint
    BOOST_CHECK_EQUAL( "scan integrity", requestMsg->CommandString() );


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_TapUp_Fail, phase_operated_voltage_regulator_fixture_direct_tap)
{
    BOOST_CHECK_THROW( regulator->adjustVoltage( 0.75 ), MissingPointAttribute );

    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_TapUp_Success, phase_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_EQUAL( 0.75, regulator->adjustVoltage( 0.75 ) );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 3100, signalMsg->getId() );     // ID of the 'TapUp' LitePoint
    BOOST_CHECK_EQUAL( "Raise Tap Position", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

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


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_TapDown_Fail, phase_operated_voltage_regulator_fixture_direct_tap)
{
    BOOST_CHECK_THROW( regulator->adjustVoltage( -0.75 ), MissingPointAttribute );

    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_TapDown_Success, phase_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_EQUAL( -0.75, regulator->adjustVoltage( -0.75 ) );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 3101, signalMsg->getId() );     // ID of the 'TapDown' LitePoint
    BOOST_CHECK_EQUAL( "Lower Tap Position", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

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


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_EnableKeepAlive_Fail, phase_operated_voltage_regulator_fixture_direct_tap)
{
    BOOST_CHECK_THROW( regulator->executeEnableKeepAlive( "cap control" ), MissingPointAttribute );

    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_EnableKeepAliveFromRemoteMode_Success, phase_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );

    // put regulator into remote mode

    CtiPointDataMsg remoteMode( 5600, 1.0, NormalQuality, StatusPointType );

    regulator->handlePointData( &remoteMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::RemoteMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 100.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( &keepAlive );


    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive( "cap control" ) );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 101",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_EnableKeepAliveFromRemoteMode_Success_with_Rollover, phase_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );

    // put regulator into remote mode

    CtiPointDataMsg remoteMode( 5600, 1.0, NormalQuality, StatusPointType );

    regulator->handlePointData( &remoteMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::RemoteMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 32767.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( &keepAlive );


    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive( "cap control" ) );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 0",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_EnableKeepAliveFromAutoMode_Success, phase_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );

    // put regulator into auto mode

    CtiPointDataMsg autoMode( 5600, 0.0, NormalQuality, StatusPointType );

    regulator->handlePointData( &autoMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::LocalMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 100.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( &keepAlive );

    // Here is the new sequence of events - messages are generated by repeated calls to executeEnableKeepAlive

    // Call: #1

    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive( "cap control" ) );

    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[0] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages[0];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 101",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'

    {   // update the keep alive
        CtiPointDataMsg keepAlive( 4200, 101.0, NormalQuality, AnalogPointType );

        regulator->handlePointData( &keepAlive );
    }

    // Call: #2

    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive( "cap control" ) );

    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[1] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    BOOST_REQUIRE_EQUAL( 2, capController.requestMessages.size() );

    requestMsg = capController.requestMessages[1];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 102",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'

    {   // update the keep alive
        CtiPointDataMsg keepAlive( 4200, 102.0, NormalQuality, AnalogPointType );
        regulator->handlePointData( &keepAlive );

        // regulator goes to 'remote' mode
        CtiPointDataMsg remoteMode( 5600, 1.0, NormalQuality, StatusPointType );
        regulator->handlePointData( &remoteMode );

        // auto block enable shows false
        CtiPointDataMsg autoBlockEnable( 8100, 0.0, NormalQuality, StatusPointType );
        regulator->handlePointData( &autoBlockEnable );
    }

    // Call: #3

    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive( "cap control" ) );

    BOOST_REQUIRE_EQUAL( 4, capController.signalMessages.size() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[2] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[3] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 8100, signalMsg->getId() );             // PaoID of the 'AutoBlock' LitePoint
    BOOST_CHECK_EQUAL( "Auto Block Enable", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    BOOST_REQUIRE_EQUAL( 4, capController.requestMessages.size() );

    requestMsg = capController.requestMessages[2];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 103",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'

    requestMsg = capController.requestMessages[3];

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


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_EnableKeepAliveFromAutoMode_Success_with_Rollover, phase_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );

    // put regulator into auto mode

    CtiPointDataMsg autoMode( 5600, 0.0, NormalQuality, StatusPointType );

    regulator->handlePointData( &autoMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::LocalMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 32766.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( &keepAlive );

    // Here is the new sequence of events - messages are generated by repeated calls to executeEnableKeepAlive

    // Call: #1

    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive( "cap control" ) );

    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[0] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages[0];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 32767",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'

    {   // update the keep alive
        CtiPointDataMsg keepAlive( 4200, 32767.0, NormalQuality, AnalogPointType );

        regulator->handlePointData( &keepAlive );
    }

    // Call: #2

    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive( "cap control" ) );

    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[1] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    BOOST_REQUIRE_EQUAL( 2, capController.requestMessages.size() );

    requestMsg = capController.requestMessages[1];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 0",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'

    {   // update the keep alive
        CtiPointDataMsg keepAlive( 4200, 0.0, NormalQuality, AnalogPointType );
        regulator->handlePointData( &keepAlive );

        // regulator goes to 'remote' mode
        CtiPointDataMsg remoteMode( 5600, 1.0, NormalQuality, StatusPointType );
        regulator->handlePointData( &remoteMode );

        // auto block enable shows false
        CtiPointDataMsg autoBlockEnable( 8100, 0.0, NormalQuality, StatusPointType );
        regulator->handlePointData( &autoBlockEnable );
    }

    // Call: #3

    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive( "cap control" ) );

    BOOST_REQUIRE_EQUAL( 4, capController.signalMessages.size() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[2] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[3] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 8100, signalMsg->getId() );             // PaoID of the 'AutoBlock' LitePoint
    BOOST_CHECK_EQUAL( "Auto Block Enable", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    BOOST_REQUIRE_EQUAL( 4, capController.requestMessages.size() );

    requestMsg = capController.requestMessages[2];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 1",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'

    requestMsg = capController.requestMessages[3];

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


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_DisableKeepAlive_Fail, phase_operated_voltage_regulator_fixture_direct_tap)
{
    BOOST_CHECK_THROW( regulator->executeDisableKeepAlive( "cap control" ), MissingPointAttribute );

    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_DisableKeepAlive_Success, phase_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );

    BOOST_CHECK_NO_THROW( regulator->executeDisableKeepAlive( "cap control" ) );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 7500, signalMsg->getId() );     // ID of the 'Terminate' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

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


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_EnableRemoteControl_Fail, phase_operated_voltage_regulator_fixture_direct_tap)
{
    BOOST_CHECK_THROW( regulator->executeEnableRemoteControl( "unit test" ), MissingPointAttribute );

    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_EnableRemoteControlFromRemoteMode_Success, phase_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );

    // put regulator into remote mode

    CtiPointDataMsg remoteMode( 5600, 1.0, NormalQuality, StatusPointType );

    regulator->handlePointData( &remoteMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::RemoteMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 100.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( &keepAlive );


    BOOST_CHECK_NO_THROW( regulator->executeEnableRemoteControl( "unit test" ) );


    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Enable Remote Control", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 101",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'


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


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_EnableRemoteControlFromAutoMode_Success, phase_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );

    // put regulator into auto mode

    CtiPointDataMsg autoMode( 5600, 0.0, NormalQuality, StatusPointType );

    regulator->handlePointData( &autoMode );

    BOOST_CHECK_EQUAL( VoltageRegulator::LocalMode, regulator->getOperatingMode() );

    // set our keep alive timer

    CtiPointDataMsg keepAlive( 4200, 100.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( &keepAlive );


    BOOST_CHECK_NO_THROW( regulator->executeEnableRemoteControl( "cap control" ) );

/*
 *  This guys functionality is changed.... - only generates the first 'activate' message now
 */

//    BOOST_REQUIRE_EQUAL( 4, capController.signalMessages.size() );
    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[0] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Enable Remote Control", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[1] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

/*
    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[2] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10001, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages[3] );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 8100, signalMsg->getId() );             // PaoID of the 'AutoBlock' LitePoint
    BOOST_CHECK_EQUAL( "Auto Block Enable", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );
*/

//    BOOST_REQUIRE_EQUAL( 3, capController.requestMessages.size() );
    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages[0];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 101",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'
/*
    requestMsg = capController.requestMessages[1];

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 102",
                       requestMsg->CommandString() );       // 'putvalue analog <offset % 10000> <value>'

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


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_DisableRemoteControl_Fail, phase_operated_voltage_regulator_fixture_direct_tap)
{
    BOOST_CHECK_THROW( regulator->executeDisableRemoteControl( "unit test" ), MissingPointAttribute );

    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_DisableRemoteControl_Success, phase_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );

    BOOST_CHECK_NO_THROW( regulator->executeDisableRemoteControl( "unit test" ) );


    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Disable Remote Control", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 7500, signalMsg->getId() );          // ID of the 'Terminate' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

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


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_QueryAutoRemoteStatus_Fail, phase_operated_voltage_regulator_fixture_direct_tap)
{
    BOOST_CHECK_THROW( regulator->getOperatingMode(), MissingPointAttribute );
}


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_QueryAutoRemoteStatus_Success, phase_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );

    BOOST_CHECK_NO_THROW( regulator->getOperatingMode() );

    // Haven't received a point update since creation - we don't know our operating mode

    BOOST_CHECK_EQUAL( VoltageRegulator::UnknownMode, regulator->getOperatingMode() );

    // point update that tells us we are in local mode

    CtiPointDataMsg pointDataMsg( 5600, 0.0, NormalQuality, StatusPointType );

    regulator->handlePointData( &pointDataMsg );

    BOOST_CHECK_EQUAL( VoltageRegulator::LocalMode, regulator->getOperatingMode() );

    // point update that tells us we are in remote mode

    pointDataMsg.setValue(1.0);

    regulator->handlePointData( &pointDataMsg );

    BOOST_CHECK_EQUAL( VoltageRegulator::RemoteMode, regulator->getOperatingMode() );
}


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_TapUp_Success_with_Phase_A_info, phase_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );
    regulator->setPhase( Cti::CapControl::Phase_A );


    BOOST_CHECK_EQUAL( 0.75, regulator->adjustVoltage( 0.75 ) );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 3100, signalMsg->getId() );     // ID of the 'TapUp' LitePoint
    BOOST_CHECK_EQUAL( "Raise Tap Position - Phase: A", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

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


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_RaiseSetPoint_Fail, phase_operated_voltage_regulator_fixture_setpoint)
{
    BOOST_CHECK_THROW( regulator->adjustVoltage( 0.75 ), MissingPointAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_RaiseSetPoint_Success, phase_operated_voltage_regulator_fixture_setpoint)
{
    regulator->loadAttributes( &attributes );


    CtiPointDataMsg setPointData( 7000, 120.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( &setPointData );


    BOOST_CHECK_CLOSE( 0.75, regulator->adjustVoltage( 0.75 ),  1e-6 );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 7000, signalMsg->getId() );     // ID of the 'SetPoint' LitePoint
    BOOST_CHECK_EQUAL( "Raise Set Point", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1020, requestMsg->DeviceId() );  // PaoID of the 'SetPoint' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 7 1208",
                       requestMsg->CommandString() );   // Offset of the 'SetPoint' LitePoint and the new value

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


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_LowerSetPoint_Fail, phase_operated_voltage_regulator_fixture_setpoint)
{
    BOOST_CHECK_THROW( regulator->adjustVoltage( -0.75 ), MissingPointAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events, test_limiter );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_PhaseOperatedVolatgeRegulator_LowerSetPoint_Success, phase_operated_voltage_regulator_fixture_setpoint)
{
    regulator->loadAttributes( &attributes );


    CtiPointDataMsg setPointData( 7000, 120.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( &setPointData );

    CtiPointDataMsg tapPositionData( 3500, -3.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( &tapPositionData );


    BOOST_CHECK_CLOSE( -0.75, regulator->adjustVoltage( -0.75 ),    1e-6 );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 7000, signalMsg->getId() );     // ID of the 'SetPoint' LitePoint
    BOOST_CHECK_EQUAL( "Lower Set Point", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1020, requestMsg->DeviceId() );  // PaoID of the 'SetPoint' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 7 1193",
                       requestMsg->CommandString() );   // Offset of the 'SetPoint' LitePoint and the new value


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

BOOST_AUTO_TEST_SUITE_END()
