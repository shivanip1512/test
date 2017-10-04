#include <boost/test/unit_test.hpp>

#include "capcontroller.h"
#include "mgr_config.h"
#include "std_helper.h"
#include "cccapbank.h"
#include "capcontrol_test_helpers.h"
#include "boost_test_helpers.h"
#include "test_reader.h"

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
        virtual AttributeMapping getPointsByPaoAndAttributes( int paoId, std::vector<Attribute>& attributes ) override
        {
            AttributeMapping pointMapping;

            for( Attribute attribute : attributes )
            {
                LitePoint point = Cti::mapFindOrDefault( _attr, attribute, {} );

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
                { Attribute::ScadaOverrideEnable,
                    { 1234,  StatusPointType, "CBC Heartbeat Enable", 1000, -1, "control pulse", "control pulse", 1.0, 0 } },
                { Attribute::ScadaOverrideClear,
                    { 5678,  StatusPointType, "CBC Heartbeat Clear", 1000, 0, "control pulse", "control pulse", 1.0, 0 } },
                { Attribute::ScadaOverrideMode,
                    { 4567,  StatusPointType, "CBC Heartbeat Type", 1000, 2, "", "", 1.0, 0 } },

                { Attribute::ScadaOverrideCountdownTimer,
                    { 2345,  AnalogPointType, "CBC Heartbeat Countdown Timer", 1000, 10466, "", "", 1.0, 0 } },
                { Attribute::ScadaOverrideHeartbeat,
                    { 3456,  AnalogPointType, "CBC Heartbeat Analog Timer", 1000, 10467, "", "", 1.0, 0 } }
            };
        }
    }
    attributes;

    std::vector<LitePoint>  twoWayPoints
    {
        LitePoint(1234,  StatusPointType, "CBC Heartbeat Enable", 1000, -1, "control pulse", "control pulse", 1.0, 0),
        LitePoint(5678,  StatusPointType, "CBC Heartbeat Clear", 1000, 0, "control pulse", "control pulse", 1.0, 0),
        LitePoint(4567,  StatusPointType, "CBC Heartbeat Type", 1000, 2, "", "", 1.0, 0),
        LitePoint(2345,  AnalogPointType, "CBC Heartbeat Countdown Timer", 1000, 10466, "", "", 1.0, 0),
        LitePoint(3456,  AnalogPointType, "CBC Heartbeat Analog Timer", 1000, 10467, "", "", 1.0, 0)
    };

    std::map<Attribute, std::string> twoWayOverloads
    {
    };

    //Cti::Test::use_in_unit_tests_only   test_limiter;

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
        bank->createCbc( 2837465823, "CBC 8024" );
        bank->getTwoWayPoints().assignTwoWayPointsAndAttributes( twoWayPoints, twoWayOverloads );

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

    // Since the CBC doesn't have a value for the heartbeat, first we initialize it, then pulse the enable point.

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

BOOST_AUTO_TEST_CASE( test_capbank_db_loading_and_initialization )
{
    // test to document the existig CapBank & CBC initialization process.

    std::unique_ptr<CtiCCCapBank>   bank;

    {
        using CcCapBankRow      = Cti::Test::StringRow<41>;
        using CcCapBankReader   = Cti::Test::TestReader<CcCapBankRow>;

        CcCapBankRow columnNames =
        {
            "PAObjectID",
            "Category",
            "PAOClass",
            "PAOName",
            "Type",
            "Description",
            "DisableFlag",
            "OPERATIONALSTATE",
            "ControllerType",
            "CONTROLDEVICEID",
            "CONTROLPOINTID",
            "BANKSIZE",
            "TypeOfSwitch",
            "SwitchManufacture",
            "MapLocationID",
            "RecloseDelay",
            "MaxDailyOps",
            "MaxOpDisable",
            "ALARMINHIBIT",
            "CONTROLINHIBIT",
            "CbcType",
            "ControlStatus",
            "TotalOperations",
            "LastStatusChangeTime",
            "TagsControlStatus",
            "AssumedStartVerificationStatus",
            "PrevVerificationControlStatus",
            "VerificationControlIndex",
            "AdditionalFlags",
            "CurrentDailyOperations",
            "TwoWayCBCState",
            "TwoWayCBCStateTime",
            "beforeVar",
            "afterVar",
            "changeVar",
            "twoWayCBCLastControl",
            "PartialPhaseInfo",
            "OriginalParentId",
            "OriginalSwitchingOrder",
            "OriginalCloseOrder",
            "OriginalTripOrder"
        };

        std::vector< CcCapBankRow > columnValues
        {
            {
                "185",
                "DEVICE",
                "CAPCONTROL",
                "Bank 8",
                "CAP BANK",
                "----",
                "N",
                "Switched",
                "",
                "184",
                "333",
                "600",
                "",
                "",
                "0",
                "0",
                "10",
                "Y",
                "N",
                "N",
                "CBC 8024",
                "0",
                "16",
                "2017-08-01 14:17:44.000",
                "0",
                "0",
                "0",
                "-1",
                "NNNNNYNNNNNNNNNNNNNN",
                "4",
                "1",
                "1990-01-01 00:00:00.000",
                "-500.00",
                "100.00",
                "100.00",
                "0",
                "(none)",
                "0",
                "0",
                "0",
                "0"
            }
        };

        CcCapBankReader reader( columnNames, columnValues );

        reader();

        bank.reset( new CtiCCCapBank( reader ) );
    }
    {
        using CcCapBankRow      = Cti::Test::StringRow<5>;
        using CcCapBankReader   = Cti::Test::TestReader<CcCapBankRow>;

        CcCapBankRow columnNames =
        {
            "DeviceID",
            "FeederID",
            "ControlOrder",
            "CloseOrder",
            "TripOrder"
        };

        std::vector< CcCapBankRow > columnValues
        {
            {
                "185",
                "5",
                "2.00000",
                "3.00000",
                "1.00000"
            }
        };

        CcCapBankReader reader( columnNames, columnValues );

        reader();

        long feederid;
        float controlOrder;
        float tripOrder;
        float closeOrder;

        reader["FeederID"]      >> feederid;
        reader["ControlOrder"]  >> controlOrder;
        reader["CloseOrder"]    >> closeOrder;
        reader["TripOrder"]     >> tripOrder;

        bank->setControlOrder( controlOrder );
        bank->setTripOrder( tripOrder );
        bank->setCloseOrder( closeOrder );
        bank->setParentId( feederid );
    }

    BOOST_REQUIRE( bank );

    BOOST_CHECK_EQUAL( bank->getPaoId(),                        185 );
    BOOST_CHECK_EQUAL( bank->getPaoCategory(),                  "DEVICE" );
    BOOST_CHECK_EQUAL( bank->getPaoClass(),                     "CAPCONTROL" );
    BOOST_CHECK_EQUAL( bank->getPaoName(),                      "Bank 8" );
    BOOST_CHECK_EQUAL( bank->getPaoType(),                      "CAP BANK" );
    BOOST_CHECK_EQUAL( bank->getPaoDescription(),               "----" );
    BOOST_CHECK_EQUAL( bank->getDisableFlag(),                  false );
    BOOST_CHECK_EQUAL( bank->getDisabledStatePointId(),         0 );
    BOOST_CHECK_EQUAL( bank->getOperationalState(),             "Switched" );
    BOOST_CHECK_EQUAL( bank->getControllerType(),               "" );
    BOOST_CHECK_EQUAL( bank->getControlDeviceId(),              184 );
    BOOST_CHECK_EQUAL( bank->getControlPointId(),               333 );
    BOOST_CHECK_EQUAL( bank->getBankSize(),                     600 );
    BOOST_CHECK_EQUAL( bank->getTypeOfSwitch(),                 "" );
    BOOST_CHECK_EQUAL( bank->getSwitchManufacture(),            "" );
    BOOST_CHECK_EQUAL( bank->getMapLocationId(),                "0" );
    BOOST_CHECK_EQUAL( bank->getRecloseDelay(),                 0 );
    BOOST_CHECK_EQUAL( bank->getMaxDailyOps(),                  10 );
    BOOST_CHECK_EQUAL( bank->getMaxOpsDisableFlag(),            true );

    Cti::CapControl::PointIdVector * points = bank->getPointIds();
    BOOST_CHECK_EQUAL( points->size(), 0 );

    std::set<long>  p;
    bank->getPointRegistrationIds( p );
    BOOST_CHECK_EQUAL( p.size(), 0 );

    BOOST_CHECK_EQUAL( bank->getParentId(),                     5 );
    BOOST_CHECK_EQUAL( bank->getAlarmInhibitFlag(),             false );
    BOOST_CHECK_EQUAL( bank->getControlInhibitFlag(),           false );
    BOOST_CHECK_EQUAL( bank->getCurrentDailyOperations(),       4 );
    BOOST_CHECK_EQUAL( bank->getControlDeviceType(),            "CBC 8024" );
    BOOST_CHECK_EQUAL( bank->getControlOrder(),                 2.0f );
    BOOST_CHECK_EQUAL( bank->getTripOrder(),                    1.0f );
    BOOST_CHECK_EQUAL( bank->getCloseOrder(),                   3.0f );
    BOOST_CHECK_EQUAL( bank->getStatusPointId(),                0 );
    BOOST_CHECK_EQUAL( bank->getControlStatus(),                0 );
    BOOST_CHECK_EQUAL( bank->getOperationAnalogPointId(),       0 );
    BOOST_CHECK_EQUAL( bank->getTotalOperations(),              16 );

    {
        //  "2017-08-01 14:17:44.000"
        CtiTime lastStatus( CtiDate( 1, 8, 2017 ), 14, 17, 44 );

        BOOST_CHECK_EQUAL( bank->getLastStatusChangeTime(),         lastStatus );
    }

    BOOST_CHECK_EQUAL( bank->getTagsControlStatus(),            0 );
    BOOST_CHECK_EQUAL( bank->getVCtrlIndex(),                   -1 );
    BOOST_CHECK_EQUAL( bank->isSelectedForVerification(),       false );
    BOOST_CHECK_EQUAL( bank->getVerificationFlag(),             false );
    BOOST_CHECK_EQUAL( bank->getRetryOpenFailedFlag(),          false );
    BOOST_CHECK_EQUAL( bank->getRetryCloseFailedFlag(),         false );
    BOOST_CHECK_EQUAL( bank->getOvUvDisabledFlag(),             true );
    BOOST_CHECK_EQUAL( bank->getMaxDailyOpsHitFlag(),           false );
    BOOST_CHECK_EQUAL( bank->getControlStatusPartialFlag(),     false );
    BOOST_CHECK_EQUAL( bank->getControlStatusSignificantFlag(), false );
    BOOST_CHECK_EQUAL( bank->getControlStatusAbnQualityFlag(),  false );
    BOOST_CHECK_EQUAL( bank->getControlStatusFailFlag(),        false );
    BOOST_CHECK_EQUAL( bank->getControlStatusCommFailFlag(),    false );
    BOOST_CHECK_EQUAL( bank->getControlStatusNoControlFlag(),   false );
    BOOST_CHECK_EQUAL( bank->getControlStatusUnSolicitedFlag(), false );
    BOOST_CHECK_EQUAL( bank->getOvUvSituationFlag(),            false );
    BOOST_CHECK_EQUAL( bank->getReEnableOvUvFlag(),             false );
    BOOST_CHECK_EQUAL( bank->getLocalControlFlag(),             false );
    BOOST_CHECK_EQUAL( bank->getControlRecentlySentFlag(),      false );
    BOOST_CHECK_EQUAL( bank->getPorterRetFailFlag(),            false );
    BOOST_CHECK_EQUAL( bank->getUnsolicitedPendingFlag(),       false );
    BOOST_CHECK_EQUAL( bank->getPerformingVerificationFlag(),   false );
    BOOST_CHECK_EQUAL( bank->getVerificationDoneFlag(),         false );
    BOOST_CHECK_EQUAL( bank->getIpAddress(),                    "(none)" );
    BOOST_CHECK_EQUAL( bank->getUDPPort(),                      0 );
    BOOST_CHECK_EQUAL( bank->getReportedCBCState(),             1 );
    BOOST_CHECK_EQUAL( bank->getReportedCBCLastControlReason(), 0 );
    BOOST_CHECK_EQUAL( bank->getReportedCBCStateTime(),         gInvalidCtiTime );
    BOOST_CHECK_EQUAL( bank->getPartialPhaseInfo(),             "(none)" );
    BOOST_CHECK_EQUAL( bank->getIgnoreFlag(),                   false );
    BOOST_CHECK_EQUAL( bank->getIgnoredReason(),                0 );
    BOOST_CHECK_EQUAL( bank->getBeforeVarsString(),             "-500.00" );
    BOOST_CHECK_EQUAL( bank->getAfterVarsString(),              "100.00" );
    BOOST_CHECK_EQUAL( bank->getPercentChangeString(),          "100.00" );
    BOOST_CHECK_EQUAL( bank->getControlStatusQuality(),         0 );
    BOOST_CHECK_EQUAL( bank->getIgnoreIndicatorTimeUpdated(),   gInvalidCtiTime );
    BOOST_CHECK_EQUAL( bank->getUnsolicitedChangeTimeUpdated(), gInvalidCtiTime );
    BOOST_CHECK_EQUAL( bank->getInsertDynamicDataFlag(),        false );
    BOOST_CHECK_EQUAL( bank->isDirty(),                         false );
    BOOST_CHECK_EQUAL( bank->getOriginalParent().getPAOId(),    185 );

    BOOST_CHECK_EQUAL( bank->getMonitorPoint().size(),          0 );

/* 
    These members are not available in the public interface:
 
    std::string _additionalFlags;
    long        _verificationControlStatus;
    bool        _retryFlag;
    int         _assumedOrigCapBankPos;
    long        _prevVerificationControlStatus;

    Don't call the following as it hits the database:
 
    BOOST_CHECK_EQUAL( bank->getActionId(),          0 );
*/
}

BOOST_FIXTURE_TEST_CASE( test_capbank_point_loading_and_initialization_with_cbc_dnp_logical, cbc_heartbeat_fixture_pulsed )
{
    std::unique_ptr<CtiCCCapBank>   bank;

    {
        using CcCapBankRow      = Cti::Test::StringRow<41>;
        using CcCapBankReader   = Cti::Test::TestReader<CcCapBankRow>;

        CcCapBankRow columnNames =
        {
            "PAObjectID",
            "Category",
            "PAOClass",
            "PAOName",
            "Type",
            "Description",
            "DisableFlag",
            "OPERATIONALSTATE",
            "ControllerType",
            "CONTROLDEVICEID",
            "CONTROLPOINTID",
            "BANKSIZE",
            "TypeOfSwitch",
            "SwitchManufacture",
            "MapLocationID",
            "RecloseDelay",
            "MaxDailyOps",
            "MaxOpDisable",
            "ALARMINHIBIT",
            "CONTROLINHIBIT",
            "CbcType",
            "ControlStatus",
            "TotalOperations",
            "LastStatusChangeTime",
            "TagsControlStatus",
            "AssumedStartVerificationStatus",
            "PrevVerificationControlStatus",
            "VerificationControlIndex",
            "AdditionalFlags",
            "CurrentDailyOperations",
            "TwoWayCBCState",
            "TwoWayCBCStateTime",
            "beforeVar",
            "afterVar",
            "changeVar",
            "twoWayCBCLastControl",
            "PartialPhaseInfo",
            "OriginalParentId",
            "OriginalSwitchingOrder",
            "OriginalCloseOrder",
            "OriginalTripOrder"
        };

        std::vector< CcCapBankRow > columnValues
        {
            {
                "185",
                "DEVICE",
                "CAPCONTROL",
                "Bank 8",
                "CAP BANK",
                "----",
                "N",
                "Switched",
                "",
                "184",
                "333",
                "600",
                "",
                "",
                "0",
                "0",
                "10",
                "Y",
                "N",
                "N",
                "CBC Logical",
                "0",
                "16",
                "2017-08-01 14:17:44.000",
                "0",
                "0",
                "0",
                "-1",
                "NNNNNYNNNNNNNNNNNNNN",
                "4",
                "1",
                "1990-01-01 00:00:00.000",
                "-500.00",
                "100.00",
                "100.00",
                "0",
                "(none)",
                "0",
                "0",
                "0",
                "0"
            }
        };

        CcCapBankReader reader( columnNames, columnValues );

        reader();

        bank.reset( new CtiCCCapBank( reader ) );
    }

    BOOST_REQUIRE( bank );

    BOOST_CHECK_EQUAL( bank->getControlDeviceType(),            "CBC Logical" );

    {
        std::vector<LitePoint>  pointCache
        {
            LitePoint( 4631,  AnalogPointType, "*Logical<Some Logical CBC> Banana Pancakes",     1773, 1, "", "", 1.0, 0 ),
            LitePoint( 4634,  AnalogPointType, "*Logical<Some Logical CBC> Blueberry Pancakes",  1773, 4, "", "", 1.0, 0 ),
            LitePoint( 4635,  AnalogPointType, "*Logical<Some Logical CBC> Maple Syrup",         1773, 5, "", "", 1.0, 0 )
        };

        std::map<Attribute, std::string>    pointOverloads
        {
            { Attribute::Voltage,       "Maple Syrup" },
            { Attribute::HighVoltage,   "Banana Pancakes" },
            { Attribute::LowVoltage,    "Blueberry Pancakes" }
        };

        bank->getTwoWayPoints().assignTwoWayPointsAndAttributes( pointCache, pointOverloads );
    }

    // check our three points

    {
        LitePoint point = bank->getTwoWayPoints().getPointByAttribute( Attribute::Voltage );

        BOOST_CHECK_EQUAL( point.getPointId(),      4635 );
        BOOST_CHECK_EQUAL( point.getPaoId(),        1773 );
        BOOST_CHECK_EQUAL( point.getPointOffset(),  5 );
        BOOST_CHECK_EQUAL( point.getPointType(),    AnalogPointType );
    }

    {
        LitePoint point = bank->getTwoWayPoints().getPointByAttribute( Attribute::HighVoltage );

        BOOST_CHECK_EQUAL( point.getPointId(),      4631 );
        BOOST_CHECK_EQUAL( point.getPaoId(),        1773 );
        BOOST_CHECK_EQUAL( point.getPointOffset(),  1 );
        BOOST_CHECK_EQUAL( point.getPointType(),    AnalogPointType );
    }

    {
        LitePoint point = bank->getTwoWayPoints().getPointByAttribute( Attribute::LowVoltage );

        BOOST_CHECK_EQUAL( point.getPointId(),      4634 );
        BOOST_CHECK_EQUAL( point.getPaoId(),        1773 );
        BOOST_CHECK_EQUAL( point.getPointOffset(),  4 );
        BOOST_CHECK_EQUAL( point.getPointType(),    AnalogPointType );
    }

    // check a non-existent point

    {
        LitePoint point = bank->getTwoWayPoints().getPointByAttribute( Attribute::DeltaVoltage );

        BOOST_CHECK_EQUAL( point.getPointId(),      0 );
        BOOST_CHECK_EQUAL( point.getPaoId(),        0 );
        BOOST_CHECK_EQUAL( point.getPointOffset(),  0 );
        BOOST_CHECK_EQUAL( point.getPointType(),    InvalidPointType );
    }
}

BOOST_AUTO_TEST_SUITE_END()

