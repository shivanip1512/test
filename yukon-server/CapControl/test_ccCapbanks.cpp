#include <boost/test/unit_test.hpp>

#include "capcontroller.h"
#include "mgr_config.h"
#include "std_helper.h"
#include "cccapbank.h"
#include "capcontrol_test_helpers.h"
#include "boost_test_helpers.h"

// Exceptions
using Cti::CapControl::MissingPointAttribute;

struct empty_fixture
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
                { PointAttribute::ScadaOverrideEnableAttribute,
                    { 1234,  StatusPointType, "CBC Heartbeat Enable", 1000, 1, "control pulse", "control pulse", 1.0, 0 } },

                { PointAttribute::ScadaOverrideCountdownTimerAttribute,
                    { 2345,  AnalogPointType, "CBC Heartbeat Countdown Timer", 1000, 3, "", "", 1.0, 0 } },
                { PointAttribute::ScadaOverrideHeartbeatAttribute,
                    { 3456,  AnalogPointType, "CBC Heartbeat", 1000, 10007, "", "", 1.0, 0 } },
                { PointAttribute::ScadaOverrideTypeAttribute,
                    { 4567,  AnalogPointType, "CBC Heartbeat Type", 1000, 8, "", "", 1.0, 0 } }
            };
        }
    }
    attributes;

    Cti::Test::use_in_unit_tests_only   test_limiter;

    boost::shared_ptr<Cti::Test::test_DeviceConfig>    fixtureConfig;

    Cti::Test::Override_ConfigManager overrideConfigManager;

    std::unique_ptr<CtiCCCapBank>   bank;

    empty_fixture()
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
        fixtureConfig->insertValue( "cbcHeartbeatMode",     "ANALOG" ); // analog countdown timer only
    }
};


BOOST_AUTO_TEST_SUITE( test_ccCapbanks )

BOOST_FIXTURE_TEST_CASE( test_ccCapbanks_test_1, empty_fixture )
{
    BOOST_CHECK_NO_THROW( bank->executeSendHeartbeat( "cap control" ) );

    BOOST_CHECK_EQUAL( 0, capController.signalMessages.size() );
    BOOST_CHECK_EQUAL( 0, capController.requestMessages.size() );
}

BOOST_FIXTURE_TEST_CASE( test_ccCapbanks_test_2, empty_fixture )
{
    bank->loadAttributes( & attributes );

    BOOST_CHECK_NO_THROW( bank->executeSendHeartbeat( "cap control" ) );


    BOOST_CHECK_EQUAL( 1, capController.signalMessages.size() );

    CtiSignalMsg * signalMsg = dynamic_cast<CtiSignalMsg *>( capController.signalMessages.front() );

    BOOST_REQUIRE( signalMsg );

    BOOST_CHECK_EQUAL( 10007, signalMsg->getId() );     // ID of the 'ScadaOverrideHeartbeat' LitePoint
    BOOST_CHECK_EQUAL( "CBC Heartbeat", signalMsg->getText() );
    BOOST_CHECK_EQUAL( "Capbank Name: Test Capbank",
                       signalMsg->getAdditionalInfo() );


    BOOST_CHECK_EQUAL( 1, capController.requestMessages.size() );

    CtiRequestMsg * requestMsg = capController.requestMessages.front();

    BOOST_REQUIRE( requestMsg );

    BOOST_CHECK_EQUAL( 1000, requestMsg->DeviceId() );  // PaoID of the 'ScadaOverrideHeartbeat' LitePoint
    BOOST_CHECK_EQUAL( "putvalue analog 7 15", requestMsg->CommandString() );
}


BOOST_AUTO_TEST_SUITE_END()

