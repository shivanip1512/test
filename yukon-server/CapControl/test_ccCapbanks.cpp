#include <boost/test/unit_test.hpp>

#include "capcontroller.h"
#include "mgr_config.h"
#include "std_helper.h"
#include "cccapbank.h"
#include "capcontrol_test_helpers.h"
#include "boost_test_helpers.h"

// Exceptions
using Cti::CapControl::MissingAttribute;

struct cbc_heartbeat_fixture_core
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
        virtual LitePoint getPointByPaoAndAttribute(int paoId, const Attribute& attribute)
        {
            if ( boost::optional<LitePoint> point = Cti::mapFind( _attr, attribute ) )
            {
                return *point;
            }

            return LitePoint();
        }

        std::map<Attribute, LitePoint>  _attr;

        TestAttributeService()
        {
            _attr = decltype( _attr )
            {
                { Attribute::ScadaOverrideEnable,
                    { 1234,  StatusPointType, "CBC Heartbeat Enable", 1000, 1, "control pulse", "control pulse", 1.0, 0 } },
                { Attribute::ScadaOverrideClear,
                    { 5678,  StatusPointType, "CBC Heartbeat Clear", 1000, 2, "control pulse", "control pulse", 1.0, 0 } },
                { Attribute::ScadaOverrideMode,
                    { 4567,  StatusPointType, "CBC Heartbeat Type", 1000, 3, "", "", 1.0, 0 } },

                { Attribute::ScadaOverrideCountdownTimer,
                    { 2345,  AnalogPointType, "CBC Heartbeat Countdown Timer", 1000, 10466, "", "", 1.0, 0 } },
                { Attribute::ScadaOverrideHeartbeat,
                    { 3456,  AnalogPointType, "CBC Heartbeat Analog Timer", 1000, 10467, "", "", 1.0, 0 } }
            };
        }
    }
    attributes;

    Cti::Test::use_in_unit_tests_only   test_limiter;

    boost::shared_ptr<Cti::Test::test_DeviceConfig>    fixtureConfig;

    Cti::Test::Override_ConfigManager overrideConfigManager;

    std::unique_ptr<CtiCCCapBank>   bank;

    cbc_heartbeat_fixture_core()
        :   bank( new CtiCCCapBank ),
            fixtureConfig( new Cti::Test::test_DeviceConfig ),
            overrideConfigManager( fixtureConfig )
    {
        bank->setPaoId( 8675309 );
        bank->setPaoName( "Test Capbank" );
        bank->setPaoClass( "CAPCONTROL" );
        bank->setPaoCategory( "DEVICE" );
        bank->setPaoType( "CAP BANK" );

        fixtureConfig->insertValue( "cbcHeartbeatPeriod",   "5" );      // message every 5 minutes
        fixtureConfig->insertValue( "cbcHeartbeatValue",    "15" );     // duration is 15 minutes
    }
};

struct cbc_heartbeat_fixture_analog : cbc_heartbeat_fixture_core
{
    cbc_heartbeat_fixture_analog()
        :   cbc_heartbeat_fixture_core()
    {
        fixtureConfig->insertValue( "cbcHeartbeatMode",     "ANALOG" ); // analog countdown timer only
    }
};

struct cbc_heartbeat_fixture_pulsed : cbc_heartbeat_fixture_core
{
    cbc_heartbeat_fixture_pulsed()
        :   cbc_heartbeat_fixture_core()
    {
        fixtureConfig->insertValue( "cbcHeartbeatMode",     "PULSED" ); // status point pulse control
    }
};

BOOST_AUTO_TEST_SUITE( test_ccCapbanks )

BOOST_FIXTURE_TEST_CASE( test_ccCapbanks_no_attributes, cbc_heartbeat_fixture_analog )
{
    BOOST_CHECK_NO_THROW( bank->executeSendHeartbeat( "cap control" ) );

    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
}

BOOST_FIXTURE_TEST_CASE( test_ccCapbanks_analog_send_heartbeat, cbc_heartbeat_fixture_analog )
{
    bank->loadAttributes( & attributes );

    // In analog mode, we always send the configuration value to the heartbeat point.

    BOOST_CHECK_NO_THROW( bank->executeSendHeartbeat( "cap control" ) );


    BOOST_CHECK_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10467, signalMsg->getId() );     // Offset of the 'ScadaOverrideHeartbeat' LitePoint
    BOOST_CHECK_EQUAL( "CBC Heartbeat", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Capbank Name: Test Capbank", signalMsg->getAdditionalInfo() );


    BOOST_CHECK_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1000, requestMsg->DeviceId() );  // PaoID of the 'ScadaOverrideHeartbeat' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 467 15", requestMsg->CommandString() );
}

BOOST_FIXTURE_TEST_CASE( test_ccCapbanks_analog_stop_heartbeat_normal_mode, cbc_heartbeat_fixture_analog )
{
    bank->loadAttributes( & attributes );

    CtiPointDataMsg normal( 4567, 0.0, NormalQuality, StatusPointType );

    bank->handlePointData( normal );

    // Since the CBC is already in local mode, the following shouldn't create any messages to send.

    BOOST_CHECK_NO_THROW( bank->executeStopHeartbeat( "cap control" ) );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
}

BOOST_FIXTURE_TEST_CASE( test_ccCapbanks_analog_stop_heartbeat_scada_override_mode, cbc_heartbeat_fixture_analog )
{
    bank->loadAttributes( & attributes );

    CtiPointDataMsg scadaOverride( 4567, 1.0, NormalQuality, StatusPointType );

    bank->handlePointData( scadaOverride );

    // Since the CBC is still in override mode, the following should create a pulse to the SCADA Override Clear point.

    BOOST_CHECK_NO_THROW( bank->executeStopHeartbeat( "cap control" ) );


    BOOST_CHECK_EQUAL( 1, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 1, capController.requestMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 5678, signalMsg->getId() );      // PointID of the 'ScadaOverrideClear' LitePoint
    BOOST_CHECK_EQUAL( "CBC Heartbeat Clear", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Capbank Name: Test Capbank", signalMsg->getAdditionalInfo() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1000, requestMsg->DeviceId() );  // PaoID of the 'ScadaOverrideClear' LitePoint
    BOOST_CHECK_EQUAL( "control pulse select pointid 5678", requestMsg->CommandString() );
}

BOOST_FIXTURE_TEST_CASE( test_ccCapbanks_pulsed_send_heartbeat_zero_value, cbc_heartbeat_fixture_pulsed )
{
    bank->loadAttributes( & attributes );

    // ince the CBC doesn't have a value for the heartbeat, first we initialize it, then pulse the enable point.

    BOOST_CHECK_NO_THROW( bank->executeSendHeartbeat( "cap control" ) );


    BOOST_CHECK_EQUAL( 2, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 2, capController.requestMessages.size() );

    {
        CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

        BOOST_REQUIRE( signalMsg );

        BOOST_CHECK_EQUAL( 10466, signalMsg->getId() );     // Offset of the 'ScadaOverrideCountdownTimer' LitePoint
        BOOST_CHECK_EQUAL( "CBC Heartbeat", signalMsg->getText() );
        BOOST_CHECK_EQUAL( "Capbank Name: Test Capbank", signalMsg->getAdditionalInfo() );

        CtiRequestMsg * requestMsg = capController.requestMessages.front();

        BOOST_REQUIRE( requestMsg );

        BOOST_CHECK_EQUAL( 1000, requestMsg->DeviceId() );  // PaoID of the 'ScadaOverrideCountdownTimer' LitePoint
        BOOST_CHECK_EQUAL( "putvalue analog 466 15", requestMsg->CommandString() );
    }

    {
        CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back() );

        BOOST_REQUIRE( signalMsg );

        BOOST_CHECK_EQUAL( 1234, signalMsg->getId() );      // PointID of the 'ScadaOverrideEnable' LitePoint
        BOOST_CHECK_EQUAL( "CBC Heartbeat Pulse", signalMsg->getText() );
        BOOST_CHECK_EQUAL( "Capbank Name: Test Capbank", signalMsg->getAdditionalInfo() );

        CtiRequestMsg * requestMsg = capController.requestMessages.back();

        BOOST_REQUIRE( requestMsg );

        BOOST_CHECK_EQUAL( 1000, requestMsg->DeviceId() );  // PaoID of the 'ScadaOverrideEnable' LitePoint
        BOOST_CHECK_EQUAL( "control pulse select pointid 1234", requestMsg->CommandString() );
    }
}

BOOST_FIXTURE_TEST_CASE( test_ccCapbanks_pulsed_send_heartbeat_differing_value, cbc_heartbeat_fixture_pulsed )
{
    bank->loadAttributes( & attributes );

    CtiPointDataMsg scada( 2345, 10.0, NormalQuality, AnalogPointType );

    bank->handlePointData( scada );

    // Here we have a heartbeat value, but it doesn't match the config - re-initialize it to the proper value
    //  and pulse the enable point.

    BOOST_CHECK_NO_THROW( bank->executeSendHeartbeat( "cap control" ) );


    BOOST_CHECK_EQUAL( 2, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 2, capController.requestMessages.size() );

    {
        CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

        BOOST_REQUIRE( signalMsg );

        BOOST_CHECK_EQUAL( 10466, signalMsg->getId() );     // Offset of the 'ScadaOverrideCountdownTimer' LitePoint
        BOOST_CHECK_EQUAL( "CBC Heartbeat", signalMsg->getText() );
        BOOST_CHECK_EQUAL( "Capbank Name: Test Capbank", signalMsg->getAdditionalInfo() );

        CtiRequestMsg * requestMsg = capController.requestMessages.front();

        BOOST_REQUIRE( requestMsg );

        BOOST_CHECK_EQUAL( 1000, requestMsg->DeviceId() );  // PaoID of the 'ScadaOverrideCountdownTimer' LitePoint
        BOOST_CHECK_EQUAL( "putvalue analog 466 15", requestMsg->CommandString() );
    }

    {
        CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.back() );

        BOOST_REQUIRE( signalMsg );

        BOOST_CHECK_EQUAL( 1234, signalMsg->getId() );      // PointID of the 'ScadaOverrideEnable' LitePoint
        BOOST_CHECK_EQUAL( "CBC Heartbeat Pulse", signalMsg->getText() );
        BOOST_CHECK_EQUAL( "Capbank Name: Test Capbank", signalMsg->getAdditionalInfo() );

        CtiRequestMsg * requestMsg = capController.requestMessages.back();

        BOOST_REQUIRE( requestMsg );

        BOOST_CHECK_EQUAL( 1000, requestMsg->DeviceId() );  // PaoID of the 'ScadaOverrideEnable' LitePoint
        BOOST_CHECK_EQUAL( "control pulse select pointid 1234", requestMsg->CommandString() );
    }
}

BOOST_FIXTURE_TEST_CASE( test_ccCapbanks_pulsed_send_heartbeat_correct_value, cbc_heartbeat_fixture_pulsed )
{
    bank->loadAttributes( & attributes );

    CtiPointDataMsg scada( 2345, 15.0, NormalQuality, AnalogPointType );

    bank->handlePointData( scada );

    // Here we have the proper value already in the CBC so we just need to pulse the enable point.

    BOOST_CHECK_NO_THROW( bank->executeSendHeartbeat( "cap control" ) );


    BOOST_CHECK_EQUAL( 1, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 1, capController.requestMessages.size() );

    {
        CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

        BOOST_REQUIRE( signalMsg );

        BOOST_CHECK_EQUAL( 1234, signalMsg->getId() );      // PointID of the 'ScadaOverrideEnable' LitePoint
        BOOST_CHECK_EQUAL( "CBC Heartbeat Pulse", signalMsg->getText() );
        BOOST_CHECK_EQUAL( "Capbank Name: Test Capbank", signalMsg->getAdditionalInfo() );

        CtiRequestMsg * requestMsg = capController.requestMessages.front();

        BOOST_REQUIRE( requestMsg );

        BOOST_CHECK_EQUAL( 1000, requestMsg->DeviceId() );  // PaoID of the 'ScadaOverrideEnable' LitePoint
        BOOST_CHECK_EQUAL( "control pulse select pointid 1234", requestMsg->CommandString() );
    }
}

BOOST_FIXTURE_TEST_CASE( test_ccCapbanks_pulsed_stop_heartbeat_normal_mode, cbc_heartbeat_fixture_pulsed )
{
    bank->loadAttributes( & attributes );

    CtiPointDataMsg normal( 4567, 0.0, NormalQuality, StatusPointType );

    bank->handlePointData( normal );

    // Since the CBC is already in local mode, the following shouldn't create any messages to send.

    BOOST_CHECK_NO_THROW( bank->executeStopHeartbeat( "cap control" ) );


    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
}

BOOST_FIXTURE_TEST_CASE( test_ccCapbanks_pulsed_stop_heartbeat_scada_override_mode, cbc_heartbeat_fixture_pulsed )
{
    bank->loadAttributes( & attributes );

    CtiPointDataMsg scadaOverride( 4567, 1.0, NormalQuality, StatusPointType );

    bank->handlePointData( scadaOverride );

    // Since the CBC is still in override mode, the following should create a pulse to the SCADA Override Clear point.

    BOOST_CHECK_NO_THROW( bank->executeStopHeartbeat( "cap control" ) );


    BOOST_CHECK_EQUAL( 1, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 1, capController.requestMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 5678, signalMsg->getId() );      // PointID of the 'ScadaOverrideClear' LitePoint
    BOOST_CHECK_EQUAL( "CBC Heartbeat Clear", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Capbank Name: Test Capbank", signalMsg->getAdditionalInfo() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1000, requestMsg->DeviceId() );  // PaoID of the 'ScadaOverrideClear' LitePoint
    BOOST_CHECK_EQUAL( "control pulse select pointid 5678", requestMsg->CommandString() );
}

BOOST_AUTO_TEST_SUITE_END()

