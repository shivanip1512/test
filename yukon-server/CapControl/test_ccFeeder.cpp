#include <boost/test/unit_test.hpp>

#include "capcontrol_test_helpers.h"
#include "boost_test_helpers.h"
#include "test_reader.h"

#include "ccfeeder.h"
#include "ccsubstationbus.h"
#include "ccsubstationbusstore.h"

#include "ccUnitTestUtil.h"
#include "StrategyManager.h"
#include "PFactorKWKVarStrategy.h"


extern unsigned long _MAX_KVAR;
extern bool _RETRY_FAILED_BANKS;
extern unsigned long _RATE_OF_CHANGE_DEPTH;

using namespace std;
using namespace Cti::Test::CapControl;
using namespace Cti::CapControl;

namespace 
{

struct overrideGlobals
{
    boost::shared_ptr<Cti::Test::test_DeviceConfig>    fixtureConfig;

    Cti::Test::Override_ConfigManager overrideConfigManager;

    overrideGlobals() :
        fixtureConfig(new Cti::Test::test_DeviceConfig),
        overrideConfigManager(fixtureConfig)
    {

        Test_CtiCCSubstationBusStore* store = new Test_CtiCCSubstationBusStore();

        CtiCCSubstationBusStore::setInstance(store);

        feeder = create_object<CtiCCFeeder>(4, "test feeder");
        bank = create_object<CtiCCCapBank>(5, "test cap bank");

        initialize_capbank(store, bank, feeder, 1);

        bank->createCbc(6, "cbc 7010");
        bank->setStatusPointId(8);

        store->addCapBankToCBCMap(bank);
    }

    ~overrideGlobals()
    {
        delete feeder;
    };

    const CtiTime currentTime = CtiTime(CtiDate(1, 1, 2010));

    CtiCCFeeder         *feeder;
    CtiCCCapBank        *bank;
};

struct defaultGlobals : overrideGlobals
{
    defaultGlobals() : overrideGlobals()
    {
        bank->getTwoWayPoints().assignTwoWayPointsAndAttributes(
            {LitePoint (7,  StatusPointType, "CBC Control Point", 6, 1, "control open", "control close", 1.0, 0)}, 
            {}, boost::none, boost::none);
    }
};

struct customGlobals : overrideGlobals
{
    customGlobals() : overrideGlobals()
    {
        bank->getTwoWayPoints().assignTwoWayPointsAndAttributes(
            {LitePoint (7,  StatusPointType, "CBC Control Point", 6, 1, "apple jacks", "banana loops", 1.0, 0)}, 
            {}, boost::none, boost::none);   
    }
};

}

BOOST_AUTO_TEST_SUITE(test_ccFeeder)

void initialize_bank(CtiCCCapBank* bank, int closeOrder = 0, int tripOrder = 0)
{
    bank->setCloseOrder(closeOrder);
    bank->setTripOrder(tripOrder);

    bank->setBankSize(600);

    bank->setControlStatus(CtiCCCapBank::Open);
    bank->setIgnoreFlag(false);
    bank->setDisableFlag(false);
    bank->setOperationalState(CtiCCCapBank::SwitchedOperationalState);
    bank->setControlInhibitFlag(false);
    bank->setMaxDailyOperation(0);
    bank->setMaxDailyOpsHitFlag(false);
}

class StrategyUnitTestLoader : public StrategyLoader
{

public:

    // default construction and destruction is OK

    virtual StrategyManager::StrategyMap load(const long ID)
    {
        StrategyManager::StrategyMap loaded;

        if (ID < 0)
        {
            long IDs[] = { 100 };

            for (int i = 0; i < sizeof(IDs) / sizeof(*IDs); i++)
            {
                loadSingle(IDs[i], loaded);
            }
        }
        else
        {
            loadSingle(ID, loaded);
        }

        return loaded;
    }

private:

    void loadSingle(const long ID, StrategyManager::StrategyMap &strategies)
    {
        bool doInsertion = true;

        StrategyManager::SharedPtr  newStrategy;

        switch (ID)
        {
        case 100:
        {
            newStrategy.reset(new PFactorKWKVarStrategy);

            newStrategy->setStrategyName("StrategyIndvlFdr");
            newStrategy->setControlInterval(0);
            newStrategy->setControlMethod(ControlStrategy::IndividualFeederControlMethod);
            newStrategy->setMaxConfirmTime(60);
            newStrategy->setMinConfirmPercent(75);
            newStrategy->setFailurePercent(25);
            newStrategy->setControlSendRetries(0);
            newStrategy->setPeakLag(80);
            newStrategy->setPeakLead(80);
            newStrategy->setOffPeakLag(80);
            newStrategy->setOffPeakLead(80);
            newStrategy->setPeakPFSetPoint(100);
            newStrategy->setOffPeakPFSetPoint(100);
            newStrategy->setEndDaySettings("(none)");       //_END_DAY_ON_TRIP = false;
            break;
        }
        default:
        {
            doInsertion = false;
            break;
        }
        }

        if (doInsertion)
        {
            newStrategy->setStrategyId(ID);
            strategies[ID] = newStrategy;
        }
    }
};

BOOST_AUTO_TEST_SUITE(test_ccFeeder_fixtures)

BOOST_FIXTURE_TEST_CASE(test_default_resend_control_open, defaultGlobals)
{
    CtiMultiMsg_vec pointChanges;
    Cti::CapControl::PorterRequests pilMessages;
    Cti::CapControl::EventLogEntries ccEvents;

    feeder->setLastCapBankControlledDeviceId(5);
    bank->setControlStatus(CtiCCCapBank::OpenPending);
    bank->setLastStatusChangeTime(currentTime - 1);

    feeder->attemptToResendControl(currentTime, pointChanges, ccEvents, pilMessages, 10);

    BOOST_REQUIRE_EQUAL(pilMessages.size(), 1);

    CtiRequestMsg *pilRequest = pilMessages[0].get();

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control open");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

    delete_container(pointChanges);
}

BOOST_FIXTURE_TEST_CASE(test_default_resend_control_close, defaultGlobals)
{
    CtiMultiMsg_vec pointChanges;
    Cti::CapControl::PorterRequests pilMessages;
    Cti::CapControl::EventLogEntries ccEvents;

    feeder->setLastCapBankControlledDeviceId(5);
    bank->setControlStatus(CtiCCCapBank::ClosePending);
    bank->setLastStatusChangeTime(currentTime - 1);

    feeder->attemptToResendControl(currentTime, pointChanges, ccEvents, pilMessages, 10);

    BOOST_REQUIRE_EQUAL(pilMessages.size(), 1);

    CtiRequestMsg *pilRequest = pilMessages[0].get();

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control close");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

    delete_container(pointChanges);
}

BOOST_FIXTURE_TEST_CASE(test_custom_resend_control_open, customGlobals)
{
    CtiMultiMsg_vec pointChanges;
    Cti::CapControl::PorterRequests pilMessages;
    Cti::CapControl::EventLogEntries ccEvents;

    feeder->setLastCapBankControlledDeviceId(5);
    bank->setControlStatus(CtiCCCapBank::OpenPending);
    bank->setLastStatusChangeTime(currentTime - 1);

    feeder->attemptToResendControl(currentTime, pointChanges, ccEvents, pilMessages, 10);

    BOOST_REQUIRE_EQUAL(pilMessages.size(), 1);

    CtiRequestMsg *pilRequest = pilMessages[0].get();

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "apple jacks");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

    delete_container(pointChanges);
}

BOOST_FIXTURE_TEST_CASE(test_custom_resend_control_close, customGlobals)
{
    CtiMultiMsg_vec pointChanges;
    Cti::CapControl::PorterRequests pilMessages;
    Cti::CapControl::EventLogEntries ccEvents;

    feeder->setLastCapBankControlledDeviceId(5);
    bank->setControlStatus(CtiCCCapBank::ClosePending);
    bank->setLastStatusChangeTime(currentTime - 1);

    feeder->attemptToResendControl(currentTime, pointChanges, ccEvents, pilMessages, 10);

    BOOST_REQUIRE_EQUAL(pilMessages.size(), 1);

    CtiRequestMsg *pilRequest = pilMessages[0].get();

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "banana loops");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

    delete_container(pointChanges);
}

BOOST_FIXTURE_TEST_CASE(test_create_decrease_var_request_close, defaultGlobals)
{
    CtiMultiMsg_vec pointChanges;
    Cti::CapControl::EventLogEntries ccEvents;

    _MAX_KVAR = 20000;

    auto pilRequest = feeder->createDecreaseVarRequest(bank, pointChanges, ccEvents, "n/a", 0, 0, 0, 0);

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control close");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

    delete_container(pointChanges);
}

BOOST_FIXTURE_TEST_CASE(test_create_decrease_var_verification_request_close, defaultGlobals)
{
    CtiMultiMsg_vec pointChanges;
    Cti::CapControl::EventLogEntries ccEvents;

    auto pilRequest = feeder->createDecreaseVarVerificationRequest(bank, pointChanges, ccEvents, "n/a", 0, 0, 0, 0, 0);

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control close");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

    delete_container(pointChanges);
}

BOOST_FIXTURE_TEST_CASE(test_create_forced_var_request_close, defaultGlobals)
{
    CtiMultiMsg_vec pointChanges;
    Cti::CapControl::EventLogEntries ccEvents;

    auto pilRequest = feeder->createForcedVarRequest(bank, pointChanges, ccEvents, CtiCCCapBank::Close, "n/a");

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control close");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

    delete_container(pointChanges);
}

BOOST_FIXTURE_TEST_CASE(test_create_forced_var_request_open, defaultGlobals)
{
    CtiMultiMsg_vec pointChanges;
    Cti::CapControl::EventLogEntries ccEvents;

    auto pilRequest = feeder->createForcedVarRequest(bank, pointChanges, ccEvents, CtiCCCapBank::Open, "n/a");

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control open");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

    delete_container(pointChanges);
}

BOOST_FIXTURE_TEST_CASE(test_create_increase_var_request_open, defaultGlobals)
{
    CtiMultiMsg_vec pointChanges;
    Cti::CapControl::EventLogEntries ccEvents;

    auto pilRequest = feeder->createIncreaseVarRequest(bank, pointChanges, ccEvents, "n/a", 0, 0, 0, 0);

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control open");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

    delete_container(pointChanges);
}

BOOST_FIXTURE_TEST_CASE(test_create_increase_var_verification_request_open, defaultGlobals)
{
    CtiMultiMsg_vec pointChanges;
    Cti::CapControl::EventLogEntries ccEvents;

    auto pilRequest = feeder->createIncreaseVarVerificationRequest(bank, pointChanges, ccEvents, "n/a", 0, 0, 0, 0, 0);

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control open");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

    delete_container(pointChanges);
}

BOOST_FIXTURE_TEST_CASE(test_create_decrease_var_request_custom_close, customGlobals)
{
    CtiMultiMsg_vec pointChanges;
    Cti::CapControl::EventLogEntries ccEvents;

    auto pilRequest = feeder->createDecreaseVarRequest(bank, pointChanges, ccEvents, "n/a", 0, 0, 0, 0);

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "banana loops");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

    delete_container(pointChanges);
}

BOOST_FIXTURE_TEST_CASE(test_create_decrease_var_verification_request_custom_close, customGlobals)
{
    CtiMultiMsg_vec pointChanges;
    Cti::CapControl::EventLogEntries ccEvents;

    auto pilRequest = feeder->createDecreaseVarVerificationRequest(bank, pointChanges, ccEvents, "n/a", 0, 0, 0, 0, 0);

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "banana loops");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

    delete_container(pointChanges);
}

BOOST_FIXTURE_TEST_CASE(test_create_forced_var_request_custom_close, customGlobals)
{
    CtiMultiMsg_vec pointChanges;
    Cti::CapControl::EventLogEntries ccEvents;

    auto pilRequest = feeder->createForcedVarRequest(bank, pointChanges, ccEvents, CtiCCCapBank::Close, "n/a");

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "banana loops");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

    delete_container(pointChanges);
}

BOOST_FIXTURE_TEST_CASE(test_create_forced_var_request_custom_open, customGlobals)
{
    CtiMultiMsg_vec pointChanges;
    Cti::CapControl::EventLogEntries ccEvents;

    auto pilRequest = feeder->createForcedVarRequest(bank, pointChanges, ccEvents, CtiCCCapBank::Open, "n/a");

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "apple jacks");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

    delete_container(pointChanges);
}

BOOST_FIXTURE_TEST_CASE(test_create_increase_var_request_custom_open, customGlobals)
{
    CtiMultiMsg_vec pointChanges;
    Cti::CapControl::EventLogEntries ccEvents;

    auto pilRequest = feeder->createIncreaseVarRequest(bank, pointChanges, ccEvents, "n/a", 0, 0, 0, 0);

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "apple jacks");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

    delete_container(pointChanges);
}

BOOST_FIXTURE_TEST_CASE(test_create_increase_var_verification_request_custom_open, customGlobals)
{
    CtiMultiMsg_vec pointChanges;
    Cti::CapControl::EventLogEntries ccEvents;

    auto pilRequest = feeder->createIncreaseVarVerificationRequest(bank, pointChanges, ccEvents, "n/a", 0, 0, 0, 0, 0);

    BOOST_REQUIRE(pilRequest);

    BOOST_CHECK_EQUAL(pilRequest->CommandString(), "apple jacks");
    BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

    delete_container(pointChanges);
}

BOOST_AUTO_TEST_SUITE_END();



BOOST_AUTO_TEST_CASE(test_findCapBankToChangeVars_basic)
{
    //Overload Store so we don't call the database.
    Test_CtiCCSubstationBusStore* testStore = new Test_CtiCCSubstationBusStore();
    CtiCCSubstationBusStore::setInstance(testStore);

    //Build up a feeder and capbanks.
    CtiCCFeeder * feeder = create_object<CtiCCFeeder>(100, "Feeder");

    CtiCCCapBank * capBank1 = create_object<CtiCCCapBank>(101, "Bank1");
    CtiCCCapBank * capBank2 = create_object<CtiCCCapBank>(102, "Bank2");
    CtiCCCapBank * capBank3 = create_object<CtiCCCapBank>(103, "Bank3");
    CtiCCCapBank * capBank4 = create_object<CtiCCCapBank>(104, "Bank4");

    initialize_bank(capBank1,0,3);
    initialize_bank(capBank2,1,2);
    initialize_bank(capBank3,2,1);
    initialize_bank(capBank4,3,0);

    feeder->getCCCapBanks().insert(capBank1);
    feeder->getCCCapBanks().insert(capBank2);
    feeder->getCCCapBanks().insert(capBank3);
    feeder->getCCCapBanks().insert(capBank4);

    //Not part of this test, but why not.
    BOOST_CHECK_EQUAL(4, feeder->getCCCapBanks().size());

    CtiMultiMsg_vec pointChanges;

    // Create the strategy manager and load the strategies.
    StrategyManager _strategyManager( std::unique_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );

    _strategyManager.reloadAll();

    // attach strategy id 100 to our feeder.
    feeder->setStrategy(100);

    //Simulate a close, should be the first bank.
    CtiCCCapBank * bank = feeder->findCapBankToChangeVars(0.0,pointChanges,-500,500,600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), true);

    //Simulate a close, should be the first bank.
    bank = feeder->findCapBankToChangeVars(-550,pointChanges,-500,500,600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank1");

    //First bank closed, should be Bank2 now
    bank->setControlStatus(CtiCCCapBank::Close);
    bank = feeder->findCapBankToChangeVars(-550,pointChanges,-500,500,600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank2");

    //First two banks closed, should be Bank3 now
    bank->setControlStatus(CtiCCCapBank::Close);
    bank = feeder->findCapBankToChangeVars(-550,pointChanges,-500,500,600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank3");

    //First three banks closed, try an open, Bank3 expected
    bank->setControlStatus(CtiCCCapBank::Close);
    bank = feeder->findCapBankToChangeVars(550,pointChanges,-500,500,-600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank3");

    //First 2 banks closed, try an open, Bank2 expected
    bank->setControlStatus(CtiCCCapBank::Open);
    bank = feeder->findCapBankToChangeVars(550,pointChanges,-500,500,-600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank2");

    //First bank closed, try an open, Bank1 expected
    bank->setControlStatus(CtiCCCapBank::Open);
    bank = feeder->findCapBankToChangeVars(550,pointChanges,-500,500,-600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank1");
    //open the bank.
    bank->setControlStatus(CtiCCCapBank::Open);

    //Test disable flag
    capBank1->setDisableFlag(true);
    bank = feeder->findCapBankToChangeVars(-550,pointChanges,-500,500,600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank2");
    capBank1->setDisableFlag(false);

    //test controlInhibit
    capBank1->setControlInhibitFlag(true);
    bank = feeder->findCapBankToChangeVars(-550,pointChanges,-500,500,600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank2");
    capBank1->setControlInhibitFlag(false);

    //test Operational State != Switched.
    capBank1->setOperationalState(CtiCCCapBank::FixedOperationalState);
    bank = feeder->findCapBankToChangeVars(-550,pointChanges,-500,500,600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank2");
    capBank1->setOperationalState(CtiCCCapBank::SwitchedOperationalState);

    CtiTime timeNow;
    //test Ignore Flag && LastStatus < current time.
    timeNow -= 60;
    capBank1->setIgnoreFlag(true);
    capBank1->setLastStatusChangeTime(timeNow);
    bank = feeder->findCapBankToChangeVars(-550,pointChanges,-500,500,600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank1");
    BOOST_CHECK_EQUAL(bank->getIgnoreFlag(),false);
    capBank1->setIgnoreFlag(false);

    //test Ignore Flag && LastStatus > current time.
    timeNow += 120;
    capBank1->setIgnoreFlag(true);
    capBank1->setLastStatusChangeTime(timeNow);
    bank = feeder->findCapBankToChangeVars(-550,pointChanges,-500,500,600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank2");
    BOOST_CHECK_EQUAL(capBank1->getIgnoreFlag(),true);
    capBank1->setIgnoreFlag(false);
    capBank1->setLastStatusChangeTime(CtiTime());

    //Test Hitting Max Daily Ops Code
    capBank1->setMaxDailyOperation(5);
    capBank1->setMaxDailyOpsHitFlag(false);
    capBank1->setMaxOpsDisableFlag(true);
    capBank1->setCurrentDailyOperations(5);
    capBank1->setOperationAnalogPointId(1);
    bank = feeder->findCapBankToChangeVars(-550,pointChanges,-500,500,600);
    //We should skip the first bank based on max ops and return the second bank.
    BOOST_CHECK_EQUAL(pointChanges.size(),2);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank2");
    //Make sure the fist bank max op stuff was set.
    BOOST_CHECK_EQUAL(capBank1->getMaxDailyOpsHitFlag(),true);
    BOOST_CHECK_EQUAL(capBank1->getDisableFlag(),true);
    capBank1->setMaxDailyOperation(0);
    capBank1->setMaxDailyOpsHitFlag(false);
    capBank1->setMaxOpsDisableFlag(false);
    capBank1->setCurrentDailyOperations(0);
    capBank1->setOperationAnalogPointId(0);
    capBank1->setDisableFlag(false);

    //check retries.
    capBank1->setControlStatus(CtiCCCapBank::OpenFail);
    capBank1->setRetryCloseFailedFlag(true);
    capBank2->setControlStatus(CtiCCCapBank::OpenFail);
    capBank2->setRetryCloseFailedFlag(true);
    capBank3->setControlStatus(CtiCCCapBank::OpenFail);
    capBank3->setRetryCloseFailedFlag(false);
    capBank4->setControlStatus(CtiCCCapBank::OpenFail);
    capBank4->setRetryCloseFailedFlag(true);

    _RETRY_FAILED_BANKS = true;

    bank = feeder->findCapBankToChangeVars(-550,pointChanges,-500,500,600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank3");
    BOOST_CHECK_EQUAL(bank->getRetryCloseFailedFlag(),true);

    capBank4->setControlStatus(CtiCCCapBank::Open);
    feeder->getStrategy()->setEndDaySettings("Close");//  _END_DAY_ON_TRIP = true;
    capBank4->setDisableFlag(false);
    capBank4->setMaxDailyOpsHitFlag(false);
    capBank4->setCurrentDailyOperations(2);
    capBank4->setMaxDailyOperation(2);
    capBank4->setOperationAnalogPointId(1);
    capBank4->setMaxOpsDisableFlag(true);
    bank = feeder->findCapBankToChangeVars(-550,pointChanges,-500,500,600);
    BOOST_CHECK_EQUAL(pointChanges.size(),2);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank4");
    capBank4->setDisableFlag(false);
    capBank4->setMaxDailyOpsHitFlag(false);
    capBank4->setCurrentDailyOperations(0);
    capBank4->setMaxDailyOperation(0);
    capBank4->setOperationAnalogPointId(0);
    capBank4->setMaxOpsDisableFlag(false);
    feeder->getStrategy()->setEndDaySettings("(none)");//_END_DAY_ON_TRIP = false;

    /*************************************************************************************************/
    //Try Kvar > 0
    //Setup for new direction

    //back to normal
    capBank1->setControlStatus(CtiCCCapBank::Close);
    capBank1->setRetryCloseFailedFlag(false);
    capBank2->setControlStatus(CtiCCCapBank::Close);
    capBank2->setRetryCloseFailedFlag(false);
    capBank3->setControlStatus(CtiCCCapBank::Close);
    capBank3->setRetryCloseFailedFlag(false);
    capBank4->setControlStatus(CtiCCCapBank::Close);
    capBank4->setRetryCloseFailedFlag(false);
    _RETRY_FAILED_BANKS = false;

    //Test disable flag
    capBank4->setDisableFlag(true);
    bank = feeder->findCapBankToChangeVars(550,pointChanges,-500,500,-600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank3");
    capBank4->setDisableFlag(false);

    //test controlInhibit
    capBank4->setControlInhibitFlag(true);
    bank = feeder->findCapBankToChangeVars(550,pointChanges,-500,500,-600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank3");
    capBank4->setControlInhibitFlag(false);

    //test Operational State != Switched.
    capBank4->setOperationalState(CtiCCCapBank::FixedOperationalState);
    bank = feeder->findCapBankToChangeVars(550,pointChanges,-500,500,-600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank3");
    capBank4->setOperationalState(CtiCCCapBank::SwitchedOperationalState);

    timeNow = CtiTime();
    //test Ignore Flag && LastStatus < current time.
    timeNow -= 60;
    capBank4->setIgnoreFlag(true);
    capBank4->setLastStatusChangeTime(timeNow);
    bank = feeder->findCapBankToChangeVars(550,pointChanges,-500,500,-600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank4");
    BOOST_CHECK_EQUAL(bank->getIgnoreFlag(),false);
    capBank4->setIgnoreFlag(false);

    //test Ignore Flag && LastStatus > current time.
    timeNow += 120;
    capBank4->setIgnoreFlag(true);
    capBank4->setLastStatusChangeTime(timeNow);
    bank = feeder->findCapBankToChangeVars(550,pointChanges,-500,500,-600);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank3");
    BOOST_CHECK_EQUAL(capBank4->getIgnoreFlag(),true);
    capBank4->setIgnoreFlag(false);
    capBank4->setLastStatusChangeTime(CtiTime());

    //Test Hitting Max Daily Ops Code
    capBank4->setMaxDailyOperation(5);
    capBank4->setMaxDailyOpsHitFlag(false);
    capBank4->setMaxOpsDisableFlag(true);
    capBank4->setCurrentDailyOperations(5);
    capBank4->setOperationAnalogPointId(1);
    bank = feeder->findCapBankToChangeVars(550,pointChanges,-500,500,-600);
    //We should skip the first bank based on max ops and return the second bank.
    BOOST_CHECK_EQUAL(pointChanges.size(),2);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank3");
    //Make sure the fist bank max op stuff was set.
    BOOST_CHECK_EQUAL(capBank4->getMaxDailyOpsHitFlag(),true);
    BOOST_CHECK_EQUAL(capBank4->getDisableFlag(),true);
    capBank4->setMaxDailyOperation(0);
    capBank4->setMaxDailyOpsHitFlag(false);
    capBank4->setCurrentDailyOperations(0);
    capBank4->setOperationAnalogPointId(0);
    capBank4->setDisableFlag(false);
    capBank4->setMaxOpsDisableFlag(false);

    feeder->getStrategy()->setEndDaySettings("Trip");//  _END_DAY_ON_TRIP = true;
    capBank4->setDisableFlag(false);
    capBank4->setMaxDailyOpsHitFlag(false);
    capBank4->setCurrentDailyOperations(2);
    capBank4->setMaxDailyOperation(2);
    capBank4->setOperationAnalogPointId(1);
    capBank4->setMaxOpsDisableFlag(true);
    bank = feeder->findCapBankToChangeVars(550,pointChanges,-500,500,-600);
    BOOST_CHECK_EQUAL(pointChanges.size(),2);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank4");
    capBank4->setDisableFlag(false);
    capBank4->setMaxDailyOpsHitFlag(false);
    capBank4->setCurrentDailyOperations(0);
    capBank4->setMaxDailyOperation(0);
    capBank4->setOperationAnalogPointId(0);
    capBank4->setMaxOpsDisableFlag(false);
    feeder->getStrategy()->setEndDaySettings("(none)");//_END_DAY_ON_TRIP = false;

    delete feeder;
    feeder = NULL;
    CtiCCSubstationBusStore::deleteInstance();
}

BOOST_AUTO_TEST_CASE(test_findCapBankToChangeVars_with_small_lead_lag)
{
    //Overload Store so we don't call the database.
    Test_CtiCCSubstationBusStore* testStore = new Test_CtiCCSubstationBusStore();
    CtiCCSubstationBusStore::setInstance(testStore);

    //Build up a feeder and capbanks.
    CtiCCFeeder * feeder = create_object<CtiCCFeeder>(100, "Feeder");

    CtiCCCapBank * capBank1 = create_object<CtiCCCapBank>(101, "Bank150");
    CtiCCCapBank * capBank2 = create_object<CtiCCCapBank>(102, "Bank600");

    initialize_bank(capBank1,0,1);
    initialize_bank(capBank2,1,0);

    feeder->getCCCapBanks().insert(capBank1);
    feeder->getCCCapBanks().insert(capBank2);

    capBank1->setBankSize(150);
    capBank2->setBankSize(600);

    CtiMultiMsg_vec pointChanges;

    // This should find Bank 150.
    CtiCCCapBank * bank = feeder->findCapBankToChangeVars(-150,pointChanges,-125,125,150);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank150");

    capBank1->setCloseOrder(1);
    capBank1->setTripOrder(0);
    capBank2->setCloseOrder(0);
    capBank2->setTripOrder(1);

    // This should not find a suitable bank.
    bank = feeder->findCapBankToChangeVars(-150,pointChanges,-125,125,150);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), true);

    capBank1->setBankSize(600);

    // This should not find a suitable bank.
    bank = feeder->findCapBankToChangeVars(-150,pointChanges,-125,125,150);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), true);

    // This should return the first bank it finds available. Called by Time of Day Controls
    bank = feeder->findCapBankToChangeVars(-150,pointChanges,0,0,0);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank600");

    capBank1->setBankSize(100);
    capBank2->setBankSize(100);

    // Putting some legitimate 0's in to test skip logic
    bank = feeder->findCapBankToChangeVars(-150,pointChanges,-200,0,0);
    BOOST_CHECK_EQUAL(pointChanges.size(),0);
    delete_container(pointChanges);
    pointChanges.clear();
    BOOST_REQUIRE_EQUAL((bank == NULL), false);
    BOOST_CHECK_EQUAL(bank->getPaoName(),"Bank600");

    delete feeder;
    feeder = NULL;
    CtiCCSubstationBusStore::deleteInstance();
}

struct test_CtiCCFeeder : CtiCCFeeder
{
    test_CtiCCFeeder()
        :   CtiCCFeeder()
    {

    }

    test_CtiCCFeeder( Cti::RowReader & rdr )
        :   CtiCCFeeder( rdr, nullptr )
    {

    }

    test_CtiCCFeeder( StrategyManager * strategyManager )
        :   CtiCCFeeder( strategyManager )
    {

    }

    using CtiCCFeeder::isDirty;
};

BOOST_AUTO_TEST_CASE( test_ccFeeder_default_construction )
{
    _RATE_OF_CHANGE_DEPTH = 5;      // this shows up in the regressions: getRegDepth()

    test_CtiCCFeeder    feeder;

    BOOST_CHECK_EQUAL(  false, feeder.getMultiMonitorFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getNewPointDataReceivedFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getPeakTimeFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getRecentlyControlledFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getWaiveControlFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getLikeDayControlFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getVerificationFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getPerformingVerificationFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getVerificationDoneFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getPreOperationMonitorPointScanFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getOperationSentWaitFlag() );;
    BOOST_CHECK_EQUAL(  false, feeder.getPostOperationMonitorPointScanFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getWaitForReCloseDelayFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getMaxDailyOpsHitFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getCorrectionNeededNoBankAvailFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getUsePhaseData() );
    BOOST_CHECK_EQUAL(  false, feeder.getPorterRetFailFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getLastVerificationMsgSentSuccessfulFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getTotalizedControlFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.isDirty() );

    BOOST_CHECK_EQUAL(      0, feeder.getParentId() );
    BOOST_CHECK_EQUAL(      0, feeder.getCurrentVarLoadPointId() );
    BOOST_CHECK_EQUAL(      0, feeder.getCurrentWattLoadPointId() );
    BOOST_CHECK_EQUAL(      0, feeder.getCurrentVoltLoadPointId() );
    BOOST_CHECK_EQUAL(      0, feeder.getEstimatedVarLoadPointId() );
    BOOST_CHECK_EQUAL(      0, feeder.getDailyOperationsAnalogPointId() );
    BOOST_CHECK_EQUAL(      0, feeder.getPowerFactorPointId() );
    BOOST_CHECK_EQUAL(      0, feeder.getEstimatedPowerFactorPointId() );
    BOOST_CHECK_EQUAL(      0, feeder.getCurrentDailyOperations() );
    BOOST_CHECK_EQUAL(      0, feeder.getLastCapBankControlledDeviceId() );
    BOOST_CHECK_EQUAL(      1, feeder.getBusOptimizedVarCategory() );
    BOOST_CHECK_EQUAL(      0, feeder.getDecimalPlaces() );
    BOOST_CHECK_EQUAL(      0, feeder.getEventSequence() );
    BOOST_CHECK_EQUAL(     -1, feeder.getCurrentVerificationCapBankId() );
    BOOST_CHECK_EQUAL(      0, feeder.getCurrentVerificationCapBankOrigState() );
    BOOST_CHECK_EQUAL(      0, feeder.getIVCount() );
    BOOST_CHECK_EQUAL(      0, feeder.getIWCount() );
    BOOST_CHECK_EQUAL(      0, feeder.getPhaseBId() );
    BOOST_CHECK_EQUAL(      0, feeder.getPhaseCId() );
    BOOST_CHECK_EQUAL(      0, feeder.getRetryIndex() );

    BOOST_CHECK_EQUAL( NormalQuality, feeder.getCurrentVarPointQuality() );
    BOOST_CHECK_EQUAL( NormalQuality, feeder.getCurrentWattPointQuality() );
    BOOST_CHECK_EQUAL( NormalQuality, feeder.getCurrentVoltPointQuality() );

    BOOST_CHECK_EQUAL(   0.0f, feeder.getDisplayOrder() );

    BOOST_CHECK_EQUAL(    0.0, feeder.getCurrentVarLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getCurrentWattLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getCurrentVoltLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getEstimatedVarLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getVarValueBeforeControl() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getBusOptimizedVarOffset() );
    BOOST_CHECK_EQUAL(   -1.0, feeder.getPowerFactorValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getKVARSolution() );
    BOOST_CHECK_EQUAL(   -1.0, feeder.getEstimatedPowerFactorValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getTargetVarValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getIVControlTot() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getIWControlTot() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getIVControl() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getIWControl() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getPhaseAValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getPhaseBValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getPhaseCValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getPhaseAValueBeforeControl() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getPhaseBValueBeforeControl() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getPhaseCValueBeforeControl() );

    BOOST_CHECK_EQUAL(     "", feeder.getMapLocationId() );
    BOOST_CHECK_EQUAL( "IDLE", feeder.getSolution() );
    BOOST_CHECK_EQUAL(     "", feeder.getParentControlUnits() );
    BOOST_CHECK_EQUAL( "none", feeder.getParentName() );

    BOOST_CHECK_EQUAL(      0, feeder.getCCCapBanks().size() );
    BOOST_CHECK_EQUAL(      0, feeder.getMultipleMonitorPoints().size() );

    BOOST_CHECK_EQUAL(      0, feeder.getRegression().getCurDepth() );
    BOOST_CHECK_EQUAL(      5, feeder.getRegression().getRegDepth() );
    BOOST_CHECK_EQUAL(      0, feeder.getRegressionA().getCurDepth() );
    BOOST_CHECK_EQUAL(      5, feeder.getRegressionA().getRegDepth() );
    BOOST_CHECK_EQUAL(      0, feeder.getRegressionB().getCurDepth() );
    BOOST_CHECK_EQUAL(      5, feeder.getRegressionB().getRegDepth() );
    BOOST_CHECK_EQUAL(      0, feeder.getRegressionC().getCurDepth() );
    BOOST_CHECK_EQUAL(      5, feeder.getRegressionC().getRegDepth() );

    BOOST_CHECK_EQUAL( gInvalidCtiTime , feeder.getLastCurrentVarPointUpdateTime() );
    BOOST_CHECK_EQUAL( gInvalidCtiTime , feeder.getLastOperationTime() );
    BOOST_CHECK_EQUAL( gInvalidCtiTime , feeder.getLastWattPointTime() );
    BOOST_CHECK_EQUAL( gInvalidCtiTime , feeder.getLastVoltPointTime() );
}

BOOST_AUTO_TEST_CASE( test_ccFeeder_default_construction_with_strategy_manager )
{
    _RATE_OF_CHANGE_DEPTH = 5;      // this shows up in the regressions: getRegDepth()

    StrategyManager _strategyManager( std::unique_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );

    test_CtiCCFeeder    feeder( &_strategyManager );

    BOOST_CHECK_EQUAL(  false, feeder.getMultiMonitorFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getNewPointDataReceivedFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getPeakTimeFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getRecentlyControlledFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getWaiveControlFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getLikeDayControlFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getVerificationFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getPerformingVerificationFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getVerificationDoneFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getPreOperationMonitorPointScanFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getOperationSentWaitFlag() );;
    BOOST_CHECK_EQUAL(  false, feeder.getPostOperationMonitorPointScanFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getWaitForReCloseDelayFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getMaxDailyOpsHitFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getCorrectionNeededNoBankAvailFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getUsePhaseData() );
    BOOST_CHECK_EQUAL(  false, feeder.getPorterRetFailFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getLastVerificationMsgSentSuccessfulFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.getTotalizedControlFlag() );
    BOOST_CHECK_EQUAL(  false, feeder.isDirty() );

    BOOST_CHECK_EQUAL(      0, feeder.getParentId() );
    BOOST_CHECK_EQUAL(      0, feeder.getCurrentVarLoadPointId() );
    BOOST_CHECK_EQUAL(      0, feeder.getCurrentWattLoadPointId() );
    BOOST_CHECK_EQUAL(      0, feeder.getCurrentVoltLoadPointId() );
    BOOST_CHECK_EQUAL(      0, feeder.getEstimatedVarLoadPointId() );
    BOOST_CHECK_EQUAL(      0, feeder.getDailyOperationsAnalogPointId() );
    BOOST_CHECK_EQUAL(      0, feeder.getPowerFactorPointId() );
    BOOST_CHECK_EQUAL(      0, feeder.getEstimatedPowerFactorPointId() );
    BOOST_CHECK_EQUAL(      0, feeder.getCurrentDailyOperations() );
    BOOST_CHECK_EQUAL(      0, feeder.getLastCapBankControlledDeviceId() );
    BOOST_CHECK_EQUAL(      1, feeder.getBusOptimizedVarCategory() );
    BOOST_CHECK_EQUAL(      0, feeder.getDecimalPlaces() );
    BOOST_CHECK_EQUAL(      0, feeder.getEventSequence() );
    BOOST_CHECK_EQUAL(     -1, feeder.getCurrentVerificationCapBankId() );
    BOOST_CHECK_EQUAL(      0, feeder.getCurrentVerificationCapBankOrigState() );
    BOOST_CHECK_EQUAL(      0, feeder.getIVCount() );
    BOOST_CHECK_EQUAL(      0, feeder.getIWCount() );
    BOOST_CHECK_EQUAL(      0, feeder.getPhaseBId() );
    BOOST_CHECK_EQUAL(      0, feeder.getPhaseCId() );
    BOOST_CHECK_EQUAL(      0, feeder.getRetryIndex() );

    BOOST_CHECK_EQUAL( NormalQuality, feeder.getCurrentVarPointQuality() );
    BOOST_CHECK_EQUAL( NormalQuality, feeder.getCurrentWattPointQuality() );
    BOOST_CHECK_EQUAL( NormalQuality, feeder.getCurrentVoltPointQuality() );

    BOOST_CHECK_EQUAL(   0.0f, feeder.getDisplayOrder() );

    BOOST_CHECK_EQUAL(    0.0, feeder.getCurrentVarLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getCurrentWattLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getCurrentVoltLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getEstimatedVarLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getVarValueBeforeControl() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getBusOptimizedVarOffset() );
    BOOST_CHECK_EQUAL(   -1.0, feeder.getPowerFactorValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getKVARSolution() );
    BOOST_CHECK_EQUAL(   -1.0, feeder.getEstimatedPowerFactorValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getTargetVarValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getIVControlTot() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getIWControlTot() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getIVControl() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getIWControl() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getPhaseAValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getPhaseBValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getPhaseCValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getPhaseAValueBeforeControl() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getPhaseBValueBeforeControl() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getPhaseCValueBeforeControl() );

    BOOST_CHECK_EQUAL(     "", feeder.getMapLocationId() );
    BOOST_CHECK_EQUAL( "IDLE", feeder.getSolution() );
    BOOST_CHECK_EQUAL(     "", feeder.getParentControlUnits() );
    BOOST_CHECK_EQUAL( "none", feeder.getParentName() );

    BOOST_CHECK_EQUAL(      0, feeder.getCCCapBanks().size() );
    BOOST_CHECK_EQUAL(      0, feeder.getMultipleMonitorPoints().size() );

    BOOST_CHECK_EQUAL(      0, feeder.getRegression().getCurDepth() );
    BOOST_CHECK_EQUAL(      5, feeder.getRegression().getRegDepth() );
    BOOST_CHECK_EQUAL(      0, feeder.getRegressionA().getCurDepth() );
    BOOST_CHECK_EQUAL(      5, feeder.getRegressionA().getRegDepth() );
    BOOST_CHECK_EQUAL(      0, feeder.getRegressionB().getCurDepth() );
    BOOST_CHECK_EQUAL(      5, feeder.getRegressionB().getRegDepth() );
    BOOST_CHECK_EQUAL(      0, feeder.getRegressionC().getCurDepth() );
    BOOST_CHECK_EQUAL(      5, feeder.getRegressionC().getRegDepth() );

    BOOST_CHECK_EQUAL( gInvalidCtiTime , feeder.getLastCurrentVarPointUpdateTime() );
    BOOST_CHECK_EQUAL( gInvalidCtiTime , feeder.getLastOperationTime() );
    BOOST_CHECK_EQUAL( gInvalidCtiTime , feeder.getLastWattPointTime() );
    BOOST_CHECK_EQUAL( gInvalidCtiTime , feeder.getLastVoltPointTime() );
}

BOOST_AUTO_TEST_CASE( test_ccFeeder_replication )
{
    Test_CtiCCSubstationBusStore* store = new Test_CtiCCSubstationBusStore();
    CtiCCSubstationBusStore::setInstance( store );

    CtiCCAreaPtr            area    = create_object<CtiCCArea>( 1, "Area-1" );
    CtiCCSubstationPtr      station = create_object<CtiCCSubstation>( 2, "Substation-A" );
    CtiCCSubstationBusPtr   bus     = create_object<CtiCCSubstationBus>( 3, "SubBus-A1" );
    CtiCCFeederPtr          feeder  = create_object<CtiCCFeeder>( 11, "Feeder1" );
    CtiCCCapBankPtr         bank1   = create_object<CtiCCCapBank>(101, "Bank1");
    CtiCCCapBankPtr         bank2   = create_object<CtiCCCapBank>(102, "Bank2");
    CtiCCCapBankPtr         bank3   = create_object<CtiCCCapBank>(103, "Bank3");
    CtiCCCapBankPtr         bank4   = create_object<CtiCCCapBank>(104, "Bank4");

    initialize_area( store, area );
    initialize_station( store, CtiCCSubstationUnqPtr{station}, area, Cti::Test::use_in_unit_tests_only{} );
    initialize_bus( store, bus, station );
    initialize_feeder( store, feeder, bus, 1 );
    initialize_bank( bank1, 0, 3 );
    initialize_bank( bank2, 1, 2 );
    initialize_bank( bank3, 2, 1 );
    initialize_bank( bank4, 3, 0 );

    feeder->getCCCapBanks().insert( bank1 );
    feeder->getCCCapBanks().insert( bank2 );
    feeder->getCCCapBanks().insert( bank3 );
    feeder->getCCCapBanks().insert( bank4 );

    BOOST_CHECK_EQUAL( 4, feeder->getCCCapBanks().size() );

    CtiCCFeederPtr  clone = feeder->replicate();

    BOOST_CHECK_EQUAL( 4, clone->getCCCapBanks().size() );

    for ( auto clonedBank : clone->getCCCapBanks() ) 
    {
        clonedBank->setPaoName( clonedBank->getPaoName() + "-clone" );
    }

    // validate the original banks names haven't been changed -- banks were 'deep' copied.

    CtiCCCapBank_SVector & original = feeder->getCCCapBanks();

    BOOST_CHECK_EQUAL(       4, original.size() );
    BOOST_CHECK_EQUAL( "Bank1", original[ 0 ]->getPaoName() );
    BOOST_CHECK_EQUAL( "Bank2", original[ 1 ]->getPaoName() );
    BOOST_CHECK_EQUAL( "Bank3", original[ 2 ]->getPaoName() );
    BOOST_CHECK_EQUAL( "Bank4", original[ 3 ]->getPaoName() );

    CtiCCCapBank_SVector & clonedBanks = clone->getCCCapBanks();

    BOOST_CHECK_EQUAL(             4, clonedBanks.size() );
    BOOST_CHECK_EQUAL( "Bank1-clone", clonedBanks[ 0 ]->getPaoName() );
    BOOST_CHECK_EQUAL( "Bank2-clone", clonedBanks[ 1 ]->getPaoName() );
    BOOST_CHECK_EQUAL( "Bank3-clone", clonedBanks[ 2 ]->getPaoName() );
    BOOST_CHECK_EQUAL( "Bank4-clone", clonedBanks[ 3 ]->getPaoName() );

    delete clone;

    store->deleteInstance();

    delete area;
}

BOOST_AUTO_TEST_CASE( test_ccFeeder_creation_via_database_reader_no_dynamic_data )
{
    _RATE_OF_CHANGE_DEPTH = 5;      // this shows up in the regressions: getRegDepth()

    boost::ptr_map< long, test_CtiCCFeeder > feeders;

    {   // Core bus object initialization

        using CCFeederRow     = Cti::Test::StringRow<58>;
        using CCFeederReader  = Cti::Test::TestReader<CCFeederRow>;

        CCFeederRow columnNames =
        {
            "PAObjectID",
            "Category",
            "PAOClass",
            "PAOName",
            "Type",
            "Description",
            "DisableFlag",
            "CurrentVarLoadPointID",
            "CurrentWattLoadPointID",
            "MapLocationID",
            "CurrentVoltLoadPointID",
            "MultiMonitorControl",
            "usephasedata",
            "phaseb",
            "phasec",
            "ControlFlag",
            "CurrentVarPointValue",
            "CurrentWattPointValue",
            "NewPointDataReceivedFlag",
            "LastCurrentVarUpdateTime",
            "EstimatedVarPointValue",
            "CurrentDailyOperations",
            "RecentlyControlledFlag",
            "LastOperationTime",
            "VarValueBeforeControl",
            "LastCapBankDeviceID",
            "BusOptimizedVarCategory",
            "BusOptimizedVarOffset",
            "PowerFactorValue",
            "KvarSolution",
            "EstimatedPFValue",
            "CurrentVarPointQuality",
            "WaiveControlFlag",
            "AdditionalFlags",
            "CurrentVoltPointValue",
            "EventSeq",
            "CurrVerifyCBId",
            "CurrVerifyCBOrigState",
            "CurrentWattPointQuality",
            "CurrentVoltPointQuality",
            "iVControlTot",
            "iVCount",
            "iWControlTot",
            "iWCount",
            "phaseavalue",
            "phasebvalue",
            "phasecvalue",
            "LastWattPointTime",
            "LastVoltPointTime",
            "retryIndex",
            "PhaseAValueBeforeControl",
            "PhaseBValueBeforeControl",
            "PhaseCValueBeforeControl",
            "OriginalParentId",
            "OriginalSwitchingOrder",
            "OriginalCloseOrder",
            "OriginalTripOrder",
            "DECIMALPLACES"
        };

        std::vector<CCFeederRow> rowVec
        {
            {
                "5",
                "CAPCONTROL",
                "CAPCONTROL",
                "A Feeder",
                "CCFEEDER",
                "(none)",
                "N",
                "190",
                "191",
                "42",
                "193",
                "Y",
                "Y",
                "195",
                "196",
                "Y",
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString(),
                CCFeederReader::getNullString()
            }
        };

        CCFeederReader reader( columnNames, rowVec );

        while ( reader() )
        {
            long    paoID;

            reader[ "PAObjectID" ] >> paoID;

            feeders.insert( paoID, new test_CtiCCFeeder( reader ) );
        }
    }

    BOOST_CHECK_EQUAL(   true, feeders[ 5 ].getMultiMonitorFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getNewPointDataReceivedFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getPeakTimeFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getRecentlyControlledFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getWaiveControlFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getLikeDayControlFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getVerificationFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getPerformingVerificationFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getVerificationDoneFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getPreOperationMonitorPointScanFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getOperationSentWaitFlag() );;
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getPostOperationMonitorPointScanFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getWaitForReCloseDelayFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getMaxDailyOpsHitFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getCorrectionNeededNoBankAvailFlag() );
    BOOST_CHECK_EQUAL(   true, feeders[ 5 ].getUsePhaseData() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getPorterRetFailFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getLastVerificationMsgSentSuccessfulFlag() );
    BOOST_CHECK_EQUAL(   true, feeders[ 5 ].getTotalizedControlFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].isDirty() );

    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getParentId() );
    BOOST_CHECK_EQUAL(    190, feeders[ 5 ].getCurrentVarLoadPointId() );
    BOOST_CHECK_EQUAL(    191, feeders[ 5 ].getCurrentWattLoadPointId() );
    BOOST_CHECK_EQUAL(    193, feeders[ 5 ].getCurrentVoltLoadPointId() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getEstimatedVarLoadPointId() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getDailyOperationsAnalogPointId() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getPowerFactorPointId() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getEstimatedPowerFactorPointId() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getCurrentDailyOperations() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getLastCapBankControlledDeviceId() );
    BOOST_CHECK_EQUAL(      1, feeders[ 5 ].getBusOptimizedVarCategory() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getDecimalPlaces() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getEventSequence() );
    BOOST_CHECK_EQUAL(     -1, feeders[ 5 ].getCurrentVerificationCapBankId() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getCurrentVerificationCapBankOrigState() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getIVCount() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getIWCount() );
    BOOST_CHECK_EQUAL(    195, feeders[ 5 ].getPhaseBId() );
    BOOST_CHECK_EQUAL(    196, feeders[ 5 ].getPhaseCId() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getRetryIndex() );

    BOOST_CHECK_EQUAL( NormalQuality, feeders[ 5 ].getCurrentVarPointQuality() );
    BOOST_CHECK_EQUAL( NormalQuality, feeders[ 5 ].getCurrentWattPointQuality() );
    BOOST_CHECK_EQUAL( NormalQuality, feeders[ 5 ].getCurrentVoltPointQuality() );

    BOOST_CHECK_EQUAL(   0.0f, feeders[ 5 ].getDisplayOrder() );

    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getCurrentVarLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getCurrentWattLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getCurrentVoltLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getEstimatedVarLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getVarValueBeforeControl() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getBusOptimizedVarOffset() );
    BOOST_CHECK_EQUAL(   -1.0, feeders[ 5 ].getPowerFactorValue() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getKVARSolution() );
    BOOST_CHECK_EQUAL(   -1.0, feeders[ 5 ].getEstimatedPowerFactorValue() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getTargetVarValue() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getIVControlTot() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getIWControlTot() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getIVControl() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getIWControl() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getPhaseAValue() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getPhaseBValue() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getPhaseCValue() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getPhaseAValueBeforeControl() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getPhaseBValueBeforeControl() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getPhaseCValueBeforeControl() );

    BOOST_CHECK_EQUAL(   "42", feeders[ 5 ].getMapLocationId() );
    BOOST_CHECK_EQUAL( "IDLE", feeders[ 5 ].getSolution() );
    BOOST_CHECK_EQUAL(     "", feeders[ 5 ].getParentControlUnits() );
    BOOST_CHECK_EQUAL( "none", feeders[ 5 ].getParentName() );

    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getCCCapBanks().size() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getMultipleMonitorPoints().size() );

    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getRegression().getCurDepth() );
    BOOST_CHECK_EQUAL(      5, feeders[ 5 ].getRegression().getRegDepth() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getRegressionA().getCurDepth() );
    BOOST_CHECK_EQUAL(      5, feeders[ 5 ].getRegressionA().getRegDepth() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getRegressionB().getCurDepth() );
    BOOST_CHECK_EQUAL(      5, feeders[ 5 ].getRegressionB().getRegDepth() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getRegressionC().getCurDepth() );
    BOOST_CHECK_EQUAL(      5, feeders[ 5 ].getRegressionC().getRegDepth() );

    BOOST_CHECK_EQUAL( gInvalidCtiTime, feeders[ 5 ].getLastCurrentVarPointUpdateTime() );
    BOOST_CHECK_EQUAL( gInvalidCtiTime, feeders[ 5 ].getLastOperationTime() );
    BOOST_CHECK_EQUAL( gInvalidCtiTime, feeders[ 5 ].getLastWattPointTime() );
    BOOST_CHECK_EQUAL( gInvalidCtiTime, feeders[ 5 ].getLastVoltPointTime() );
}

BOOST_AUTO_TEST_CASE( test_ccFeeder_creation_via_database_reader_with_dynamic_data )
{
    _RATE_OF_CHANGE_DEPTH = 5;      // this shows up in the regressions: getRegDepth()

    boost::ptr_map< long, test_CtiCCFeeder > feeders;

    {   // Core bus object initialization

        using CCFeederRow     = Cti::Test::StringRow<58>;
        using CCFeederReader  = Cti::Test::TestReader<CCFeederRow>;

        CCFeederRow columnNames =
        {
            "PAObjectID",
            "Category",
            "PAOClass",
            "PAOName",
            "Type",
            "Description",
            "DisableFlag",
            "CurrentVarLoadPointID",
            "CurrentWattLoadPointID",
            "MapLocationID",
            "CurrentVoltLoadPointID",
            "MultiMonitorControl",
            "usephasedata",
            "phaseb",
            "phasec",
            "ControlFlag",
            "CurrentVarPointValue",
            "CurrentWattPointValue",
            "NewPointDataReceivedFlag",
            "LastCurrentVarUpdateTime",
            "EstimatedVarPointValue",
            "CurrentDailyOperations",
            "RecentlyControlledFlag",
            "LastOperationTime",
            "VarValueBeforeControl",
            "LastCapBankDeviceID",
            "BusOptimizedVarCategory",
            "BusOptimizedVarOffset",
            "PowerFactorValue",
            "KvarSolution",
            "EstimatedPFValue",
            "CurrentVarPointQuality",
            "WaiveControlFlag",
            "AdditionalFlags",
            "CurrentVoltPointValue",
            "EventSeq",
            "CurrVerifyCBId",
            "CurrVerifyCBOrigState",
            "CurrentWattPointQuality",
            "CurrentVoltPointQuality",
            "iVControlTot",
            "iVCount",
            "iWControlTot",
            "iWCount",
            "phaseavalue",
            "phasebvalue",
            "phasecvalue",
            "LastWattPointTime",
            "LastVoltPointTime",
            "retryIndex",
            "PhaseAValueBeforeControl",
            "PhaseBValueBeforeControl",
            "PhaseCValueBeforeControl",
            "OriginalParentId",
            "OriginalSwitchingOrder",
            "OriginalCloseOrder",
            "OriginalTripOrder",
            "DECIMALPLACES"
        };

        std::vector<CCFeederRow> rowVec
        {
            {
                "5",
                "CAPCONTROL",
                "CAPCONTROL",
                "A Feeder",
                "CCFEEDER",
                "(none)",
                "N",
                "190",
                "191",
                "42",
                "193",
                "Y",
                "Y",
                "195",
                "196",
                "Y",
                "-400",
                "1300",
                "N",
                "2016-05-04 18:08:14.000",
                "-400",
                "1",
                "Y",
                "2016-02-10 18:41:44.000",
                "-600",
                "364",
                "2",
                "1",
                "1.1",
                "10",
                "1.2",
                "4",
                "Y",
                "NNNNNNNNNYNNNNNNNNNN",
                "121",
                "46",
                "-1",
                "0",
                "4",
                "4",
                "10",
                "6",
                "11",
                "7",
                "100",
                "200",
                "300",
                "2016-05-04 18:08:01.000",
                "2016-05-04 18:08:22.000",
                "0",
                "110",
                "210",
                "310",
                "0",
                "0",
                "0",
                "0",
                "0"
            }
        };

        CCFeederReader reader( columnNames, rowVec );

        while ( reader() )
        {
            long    paoID;

            reader[ "PAObjectID" ] >> paoID;

            feeders.insert( paoID, new test_CtiCCFeeder( reader ) );
        }
    }

    BOOST_CHECK_EQUAL(   true, feeders[ 5 ].getMultiMonitorFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getNewPointDataReceivedFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getPeakTimeFlag() );
    BOOST_CHECK_EQUAL(   true, feeders[ 5 ].getRecentlyControlledFlag() );
    BOOST_CHECK_EQUAL(   true, feeders[ 5 ].getWaiveControlFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getLikeDayControlFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getVerificationFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getPerformingVerificationFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getVerificationDoneFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getPreOperationMonitorPointScanFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getOperationSentWaitFlag() );;
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getPostOperationMonitorPointScanFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getWaitForReCloseDelayFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getMaxDailyOpsHitFlag() );
    BOOST_CHECK_EQUAL(   true, feeders[ 5 ].getOvUvDisabledFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getCorrectionNeededNoBankAvailFlag() );
    BOOST_CHECK_EQUAL(   true, feeders[ 5 ].getUsePhaseData() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getPorterRetFailFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].getLastVerificationMsgSentSuccessfulFlag() );
    BOOST_CHECK_EQUAL(   true, feeders[ 5 ].getTotalizedControlFlag() );
    BOOST_CHECK_EQUAL(  false, feeders[ 5 ].isDirty() );

    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getParentId() );
    BOOST_CHECK_EQUAL(    190, feeders[ 5 ].getCurrentVarLoadPointId() );
    BOOST_CHECK_EQUAL(    191, feeders[ 5 ].getCurrentWattLoadPointId() );
    BOOST_CHECK_EQUAL(    193, feeders[ 5 ].getCurrentVoltLoadPointId() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getEstimatedVarLoadPointId() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getDailyOperationsAnalogPointId() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getPowerFactorPointId() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getEstimatedPowerFactorPointId() );
    BOOST_CHECK_EQUAL(      1, feeders[ 5 ].getCurrentDailyOperations() );
    BOOST_CHECK_EQUAL(    364, feeders[ 5 ].getLastCapBankControlledDeviceId() );
    BOOST_CHECK_EQUAL(      2, feeders[ 5 ].getBusOptimizedVarCategory() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getDecimalPlaces() );
    BOOST_CHECK_EQUAL(     46, feeders[ 5 ].getEventSequence() );
    BOOST_CHECK_EQUAL(     -1, feeders[ 5 ].getCurrentVerificationCapBankId() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getCurrentVerificationCapBankOrigState() );
    BOOST_CHECK_EQUAL(      6, feeders[ 5 ].getIVCount() );
    BOOST_CHECK_EQUAL(      7, feeders[ 5 ].getIWCount() );
    BOOST_CHECK_EQUAL(    195, feeders[ 5 ].getPhaseBId() );
    BOOST_CHECK_EQUAL(    196, feeders[ 5 ].getPhaseCId() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getRetryIndex() );

    BOOST_CHECK_EQUAL( ManualQuality, feeders[ 5 ].getCurrentVarPointQuality() );
    BOOST_CHECK_EQUAL( ManualQuality, feeders[ 5 ].getCurrentWattPointQuality() );
    BOOST_CHECK_EQUAL( ManualQuality, feeders[ 5 ].getCurrentVoltPointQuality() );

    BOOST_CHECK_EQUAL(   0.0f, feeders[ 5 ].getDisplayOrder() );

    BOOST_CHECK_EQUAL( -400.0, feeders[ 5 ].getCurrentVarLoadPointValue() );
    BOOST_CHECK_EQUAL( 1300.0, feeders[ 5 ].getCurrentWattLoadPointValue() );
    BOOST_CHECK_EQUAL(  121.0, feeders[ 5 ].getCurrentVoltLoadPointValue() );
    BOOST_CHECK_EQUAL( -400.0, feeders[ 5 ].getEstimatedVarLoadPointValue() );
    BOOST_CHECK_EQUAL( -600.0, feeders[ 5 ].getVarValueBeforeControl() );
    BOOST_CHECK_EQUAL(    1.0, feeders[ 5 ].getBusOptimizedVarOffset() );
    BOOST_CHECK_EQUAL(    1.1, feeders[ 5 ].getPowerFactorValue() );
    BOOST_CHECK_EQUAL(   10.0, feeders[ 5 ].getKVARSolution() );
    BOOST_CHECK_EQUAL(    1.2, feeders[ 5 ].getEstimatedPowerFactorValue() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getTargetVarValue() );
    BOOST_CHECK_EQUAL(   10.0, feeders[ 5 ].getIVControlTot() );
    BOOST_CHECK_EQUAL(   11.0, feeders[ 5 ].getIWControlTot() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getIVControl() );
    BOOST_CHECK_EQUAL(    0.0, feeders[ 5 ].getIWControl() );
    BOOST_CHECK_EQUAL(  100.0, feeders[ 5 ].getPhaseAValue() );
    BOOST_CHECK_EQUAL(  200.0, feeders[ 5 ].getPhaseBValue() );
    BOOST_CHECK_EQUAL(  300.0, feeders[ 5 ].getPhaseCValue() );
    BOOST_CHECK_EQUAL(  110.0, feeders[ 5 ].getPhaseAValueBeforeControl() );
    BOOST_CHECK_EQUAL(  210.0, feeders[ 5 ].getPhaseBValueBeforeControl() );
    BOOST_CHECK_EQUAL(  310.0, feeders[ 5 ].getPhaseCValueBeforeControl() );

    BOOST_CHECK_EQUAL(   "42", feeders[ 5 ].getMapLocationId() );
    BOOST_CHECK_EQUAL( "IDLE", feeders[ 5 ].getSolution() );
    BOOST_CHECK_EQUAL(     "", feeders[ 5 ].getParentControlUnits() );
    BOOST_CHECK_EQUAL( "none", feeders[ 5 ].getParentName() );

    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getCCCapBanks().size() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getMultipleMonitorPoints().size() );

    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getRegression().getCurDepth() );
    BOOST_CHECK_EQUAL(      5, feeders[ 5 ].getRegression().getRegDepth() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getRegressionA().getCurDepth() );
    BOOST_CHECK_EQUAL(      5, feeders[ 5 ].getRegressionA().getRegDepth() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getRegressionB().getCurDepth() );
    BOOST_CHECK_EQUAL(      5, feeders[ 5 ].getRegressionB().getRegDepth() );
    BOOST_CHECK_EQUAL(      0, feeders[ 5 ].getRegressionC().getCurDepth() );
    BOOST_CHECK_EQUAL(      5, feeders[ 5 ].getRegressionC().getRegDepth() );

//  Cti::Test::TestReader returns CtiTime::now() for all timestamps extracted
//      so we can't test those...

//    BOOST_CHECK_EQUAL( gInvalidCtiTime, feeders[ 5 ].getLastCurrentVarPointUpdateTime() );
//    BOOST_CHECK_EQUAL( gInvalidCtiTime, feeders[ 5 ].getLastOperationTime() );
//    BOOST_CHECK_EQUAL( gInvalidCtiTime, feeders[ 5 ].getLastWattPointTime() );
//    BOOST_CHECK_EQUAL( gInvalidCtiTime, feeders[ 5 ].getLastVoltPointTime() );
}

BOOST_AUTO_TEST_SUITE_END()
