#include <boost/test/unit_test.hpp>

#include "capcontroller.h"
#include "VoltageRegulatorManager.h"
#include "mgr_config.h"
#include "std_helper.h"
#include "RegulatorEvents.h"

#include "capcontrol_test_helpers.h"
#include "boost_test_helpers.h"

// Objects
using Cti::CapControl::VoltageRegulator;
using Cti::CapControl::VoltageRegulatorManager;
using Cti::CapControl::ControlPolicy;

// Exceptions
using Cti::CapControl::MissingAttribute;

namespace
{

struct load_tap_changer_fixture
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
            };
        }
    }
    attributes;

    Cti::Test::use_in_unit_tests_only   test_limiter;

    VoltageRegulatorManager::SharedPtr  regulator;

    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;

    Cti::Test::Override_ConfigManager overrideConfigManager;

    load_tap_changer_fixture()
        :   regulator( new VoltageRegulator ),
            fixtureConfig( new Cti::Test::test_DeviceConfig ),
            overrideConfigManager( fixtureConfig )
    {
        fixtureConfig->insertValue("voltageChangePerTap", "0.75");

        fixtureConfig->insertValue("regulatorHeartbeatMode", "COUNTDOWN");
        fixtureConfig->insertValue("regulatorHeartbeatPeriod", "0");
        fixtureConfig->insertValue("regulatorHeartbeatValue", "123");

        regulator->setPaoId( 23456 );
        regulator->setPaoName( "Test Regulator #1" );
        regulator->setPaoCategory( "CAPCONTROL" );
        regulator->setPaoType( VoltageRegulator::LoadTapChanger );
    }
};

}


BOOST_FIXTURE_TEST_SUITE( test_LoadTapChanger_DirectTap, load_tap_changer_fixture )

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

BOOST_AUTO_TEST_CASE(test_IntegrityScan)
{
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_NO_THROW( regulator->executeIntegrityScan( "cap control" ) );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    const auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 2203, signalMsg->getId() );     // ID of the 'Voltage' LitePoint
    BOOST_CHECK_EQUAL( "Integrity Scan", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    const auto requestMsg = capController.requestMessages.front().get();

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

BOOST_AUTO_TEST_CASE(test_IntegrityScan_Success_nonstandard_user)
{
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_NO_THROW( regulator->executeIntegrityScan( "unit test" ) );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    const auto signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front().get() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 2203, signalMsg->getId() );     // ID of the 'Voltage' LitePoint
    BOOST_CHECK_EQUAL( "Integrity Scan", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    const auto requestMsg = capController.requestMessages.front().get();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1001, requestMsg->DeviceId() );  // PaoID of the 'Voltage' LitePoint
    BOOST_CHECK_EQUAL( "scan integrity", requestMsg->CommandString() );


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
