#include <boost/test/unit_test.hpp>

#include "capcontroller.h"
#include "VoltageRegulatorManager.h"
#include "GangOperatedVoltageRegulator.h"
#include "mgr_config.h"
#include "std_helper.h"
#include "RegulatorEvents.h"

// Objects
using Cti::CapControl::VoltageRegulator;
using Cti::CapControl::VoltageRegulatorManager;
using Cti::CapControl::GangOperatedVoltageRegulator;

// Exceptions
using Cti::CapControl::MissingPointAttribute;


struct gang_operated_voltage_regulator_fixture_core
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

        virtual void sendMessageToDispatch(CtiMessage* message)
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
                { PointAttribute::VoltageYAttribute,
                    { 2203,  AnalogPointType, "VoltageY", 1001, 2, "", "" } },
                { PointAttribute::TapUpAttribute,
                    { 3100,  StatusPointType, "TapUp", 1003, 4, "", "control close" } },
                { PointAttribute::TapDownAttribute,
                    { 3101,  StatusPointType, "TapDown", 1004, 5, "", "control close" } },
                { PointAttribute::KeepAliveAttribute,
                    { 4200,  AnalogPointType, "KeepAlive", 1007, 1, "", "" } },
                { PointAttribute::AutoRemoteControlAttribute,
                    { 5600,  StatusPointType, "AutoRemoteControl", 1009, 6, "", "" } },
                { PointAttribute::TapPositionAttribute,
                    { 3500,  AnalogPointType, "TapPosition", 1013, 3, "", "" } },
                { PointAttribute::ForwardSetPointAttribute,
                    { 7000,  AnalogPointType, "SetPoint", 1020, 10007, "", "" } },
                { PointAttribute::ForwardBandwidthAttribute,
                    { 7100,  AnalogPointType, "Bandwidth", 1021, 8, "", "" } }
            };
        }
    }
    attributes;

    struct test_DeviceConfig : public Cti::Config::DeviceConfig
    {
        using Cti::Config::DeviceConfig::insertValue;
        using Cti::Config::DeviceConfig::findValue;
        using Cti::Config::DeviceConfig::addCategory;
    };

    boost::shared_ptr<test_DeviceConfig>    fixtureConfig;

    struct test_ConfigManager : Cti::ConfigManager
    {
        const Cti::Config::DeviceConfigSPtr config;

        test_ConfigManager( Cti::Config::DeviceConfigSPtr config_ )
            : config( config_ )
        {
        }

        virtual Cti::Config::DeviceConfigSPtr fetchConfig( const long deviceID, const DeviceTypes deviceType )
        {
            return config;
        }
    };

    class Override_ConfigManager
    {
        std::auto_ptr<Cti::ConfigManager> _oldConfigManager;

    public:

        Override_ConfigManager(Cti::Config::DeviceConfigSPtr config)
        {
            _oldConfigManager = Cti::gConfigManager;

            Cti::gConfigManager.reset(new test_ConfigManager(config));
        }

        ~Override_ConfigManager()
        {
            Cti::gConfigManager = _oldConfigManager;
        }
    }
    overrideConfigManager;

    VoltageRegulatorManager::SharedPtr  regulator;

    gang_operated_voltage_regulator_fixture_core()
        :   regulator( new GangOperatedVoltageRegulator ),
            fixtureConfig( new test_DeviceConfig ),
            overrideConfigManager( fixtureConfig )
    {
        regulator->setPaoId( 23456 );
        regulator->setPaoName( "Test Regulator #1" );
        regulator->setPaocategory( "CAPCONTROL" );
        regulator->setPaoType( VoltageRegulator::LoadTapChanger );

        fixtureConfig->insertValue( "voltageChangePerTap", "0.75" );
        fixtureConfig->insertValue( "heartbeatPeriod",     "0" );
        fixtureConfig->insertValue( "heartbeatValue",      "123" );
    }
};


struct gang_operated_voltage_regulator_fixture_direct_tap : gang_operated_voltage_regulator_fixture_core
{
    gang_operated_voltage_regulator_fixture_direct_tap()
        :   gang_operated_voltage_regulator_fixture_core()
    {
        fixtureConfig->insertValue( "voltageControlMode",  "DIRECT_TAP" );
    }
};


struct gang_operated_voltage_regulator_fixture_setpoint : gang_operated_voltage_regulator_fixture_core
{
    gang_operated_voltage_regulator_fixture_setpoint()
        :   gang_operated_voltage_regulator_fixture_core()
    {
        fixtureConfig->insertValue( "voltageControlMode",  "SET_POINT" );
    }
};


BOOST_AUTO_TEST_SUITE( test_GangOperatedVoltageRegulator )


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_IntegrityScan_Fail, gang_operated_voltage_regulator_fixture_direct_tap)
{
    BOOST_CHECK_THROW( regulator->executeIntegrityScan(), MissingPointAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_IntegrityScan_Success, gang_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_NO_THROW( regulator->executeIntegrityScan() );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 2203, signalMsg->getId() );     // ID of the 'VoltageY' LitePoint
    BOOST_CHECK_EQUAL( "Integrity Scan", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1001, requestMsg->DeviceId() );  // PaoID of the 'VoltageY' LitePoint
    BOOST_CHECK_EQUAL( "scan integrity", requestMsg->CommandString() );


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_TapUp_Fail, gang_operated_voltage_regulator_fixture_direct_tap)
{
    BOOST_CHECK_THROW( regulator->adjustVoltage( 0.75 ), MissingPointAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_TapUp_Success, gang_operated_voltage_regulator_fixture_direct_tap)
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
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_REQUIRE_EQUAL( 1, events.size() );

        Cti::CapControl::RegulatorEvent event = events.front();

        BOOST_CHECK_EQUAL( Cti::CapControl::RegulatorEvent::TapUp, event.eventType );
        BOOST_CHECK_EQUAL( 23456,                                  event.regulatorID );
        BOOST_CHECK_EQUAL( Cti::CapControl::Phase_Unknown,         event.phase );

        BOOST_CHECK( ! event.setPointValue );
        BOOST_CHECK( ! event.tapPosition );
    }
}


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_TapDown_Fail, gang_operated_voltage_regulator_fixture_direct_tap)
{
    BOOST_CHECK_THROW( regulator->adjustVoltage( -0.75 ), MissingPointAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_TapDown_Success, gang_operated_voltage_regulator_fixture_direct_tap)
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
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_REQUIRE_EQUAL( 1, events.size() );

        Cti::CapControl::RegulatorEvent event = events.front();

        BOOST_CHECK_EQUAL( Cti::CapControl::RegulatorEvent::TapDown, event.eventType );
        BOOST_CHECK_EQUAL( 23456,                                    event.regulatorID );
        BOOST_CHECK_EQUAL( Cti::CapControl::Phase_Unknown,           event.phase );

        BOOST_CHECK( ! event.setPointValue );
        BOOST_CHECK( ! event.tapPosition );
    }
}


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_EnableKeepAlive_Fail, gang_operated_voltage_regulator_fixture_direct_tap)
{
    BOOST_CHECK_THROW( regulator->executeEnableKeepAlive(), MissingPointAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_EnableKeepAlive_Success, gang_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );
    regulator->setKeepAliveConfig(123);    // this is the value we'll look for in the resulting request message


    BOOST_CHECK_NO_THROW( regulator->executeEnableKeepAlive() );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 1, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 123",
                       requestMsg->CommandString() );       // 'putvalue analog <offset> <value>'


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_DisableKeepAlive_Fail, gang_operated_voltage_regulator_fixture_direct_tap)
{
    BOOST_CHECK_THROW( regulator->executeDisableKeepAlive(), MissingPointAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_DisableKeepAlive_Success, gang_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_NO_THROW( regulator->executeDisableKeepAlive() );


    BOOST_REQUIRE_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 1, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 0",
                       requestMsg->CommandString() );       // 'putvalue analog <offset> <value>'


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_EnableRemoteControl_Fail, gang_operated_voltage_regulator_fixture_direct_tap)
{
    BOOST_CHECK_THROW( regulator->executeEnableRemoteControl(), MissingPointAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_EnableRemoteControl_Success, gang_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );
    regulator->setKeepAliveConfig(123);    // this is the value we'll look for in the resulting request message


    BOOST_CHECK_NO_THROW( regulator->executeEnableRemoteControl() );


    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Enable Remote Control", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );
    BOOST_CHECK_EQUAL( 123, signalMsg->getPointValue() );   // 'KeepAlive' reload value

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 1, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 123",
                       requestMsg->CommandString() );       // 'putvalue analog <offset> <value>'


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_REQUIRE_EQUAL( 1, events.size() );

        Cti::CapControl::RegulatorEvent event = events.front();

        BOOST_CHECK_EQUAL( Cti::CapControl::RegulatorEvent::EnableRemoteControl, event.eventType );
        BOOST_CHECK_EQUAL( 23456,                                                event.regulatorID );
        BOOST_CHECK_EQUAL( Cti::CapControl::Phase_Unknown,                       event.phase );

        BOOST_CHECK( ! event.setPointValue );
        BOOST_CHECK( ! event.tapPosition );
    }
}


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_DisableRemoteControl_Fail, gang_operated_voltage_regulator_fixture_direct_tap)
{
    BOOST_CHECK_THROW( regulator->executeDisableRemoteControl(), MissingPointAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_DisableRemoteControl_Success, gang_operated_voltage_regulator_fixture_direct_tap)
{
    regulator->loadAttributes( &attributes );


    BOOST_CHECK_NO_THROW( regulator->executeDisableRemoteControl() );


    BOOST_REQUIRE_EQUAL( 2, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 4200, signalMsg->getId() );          // ID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Disable Remote Control", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );
    BOOST_CHECK_EQUAL( 0, signalMsg->getPointValue() );     // 'KeepAlive' reload value

    signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 1, signalMsg->getId() );             // Point Offset of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "Keep Alive", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Voltage Regulator Name: Test Regulator #1",
                       signalMsg->getAdditionalInfo() );


    BOOST_REQUIRE_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1007, requestMsg->DeviceId() );      // PaoID of the 'KeepAlive' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 1 0",
                       requestMsg->CommandString() );       // 'putvalue analog <offset> <value>'


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_REQUIRE_EQUAL( 1, events.size() );

        Cti::CapControl::RegulatorEvent event = events.front();

        BOOST_CHECK_EQUAL( Cti::CapControl::RegulatorEvent::DisableRemoteControl, event.eventType );
        BOOST_CHECK_EQUAL( 23456,                                                 event.regulatorID );
        BOOST_CHECK_EQUAL( Cti::CapControl::Phase_Unknown,                        event.phase );

        BOOST_CHECK( ! event.setPointValue );
        BOOST_CHECK( ! event.tapPosition );
    }
}


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_RaiseSetPoint_Fail, gang_operated_voltage_regulator_fixture_setpoint)
{
    BOOST_CHECK_THROW( regulator->adjustVoltage( 0.75 ), MissingPointAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_RaiseSetPoint_Success, gang_operated_voltage_regulator_fixture_setpoint)
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
    BOOST_CHECK_EQUAL( "putvalue analog 7 120.750000",
                       requestMsg->CommandString() );   // Offset of the 'SetPoint' LitePoint and the new value


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_REQUIRE_EQUAL( 1, events.size() );

        Cti::CapControl::RegulatorEvent event = events.front();

        BOOST_CHECK_EQUAL( Cti::CapControl::RegulatorEvent::IncreaseSetPoint, event.eventType );
        BOOST_CHECK_EQUAL( 23456,                                             event.regulatorID );
        BOOST_CHECK_EQUAL( Cti::CapControl::Phase_Unknown,                    event.phase );
        BOOST_CHECK_CLOSE( 120.75,                                           *event.setPointValue,     1e-6 );

        BOOST_CHECK( ! event.tapPosition );
    }
}


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_LowerSetPoint_Fail, gang_operated_voltage_regulator_fixture_setpoint)
{
    BOOST_CHECK_THROW( regulator->adjustVoltage( -0.75 ), MissingPointAttribute );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );

    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_CHECK_EQUAL( 0, events.size() );
    }
}


BOOST_FIXTURE_TEST_CASE(test_GangOperatedVolatgeRegulator_LowerSetPoint_Success, gang_operated_voltage_regulator_fixture_setpoint)
{
    regulator->loadAttributes( &attributes );


    CtiPointDataMsg setPointData( 7000, 120.0, NormalQuality, AnalogPointType );

    regulator->handlePointData( &setPointData );

    CtiPointDataMsg tapPositionData( 3500, 3.0, NormalQuality, AnalogPointType );

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
    BOOST_CHECK_EQUAL( "putvalue analog 7 119.250000",
                       requestMsg->CommandString() );   // Offset of the 'SetPoint' LitePoint and the new value


    // Validate generated RegulatorEvent messages
    {
        std::vector<Cti::CapControl::RegulatorEvent>  events;
        Cti::CapControl::Test::exportRegulatorEvents( events );

        BOOST_REQUIRE_EQUAL( 1, events.size() );

        Cti::CapControl::RegulatorEvent event = events.front();

        BOOST_CHECK_EQUAL( Cti::CapControl::RegulatorEvent::DecreaseSetPoint, event.eventType );
        BOOST_CHECK_EQUAL( 23456,                                             event.regulatorID );
        BOOST_CHECK_EQUAL( Cti::CapControl::Phase_Unknown,                    event.phase );
        BOOST_CHECK_CLOSE( 119.25,                                           *event.setPointValue,     1e-6 );
        BOOST_CHECK_EQUAL( 3,                                                *event.tapPosition );
    }
}

BOOST_AUTO_TEST_SUITE_END()
