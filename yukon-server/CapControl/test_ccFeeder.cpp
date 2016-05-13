#include <boost/test/unit_test.hpp>

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

BOOST_AUTO_TEST_SUITE( test_ccFeeder )

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

            for (int i = 0; i < sizeof(IDs)/ sizeof(*IDs); i++)
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
                newStrategy.reset( new PFactorKWKVarStrategy );

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


BOOST_AUTO_TEST_CASE(test_attemptToResendControl)
{
    Test_CtiCCSubstationBusStore* store = new Test_CtiCCSubstationBusStore();

    CtiCCSubstationBusStore::setInstance(store);

    CtiCCFeeder         *feeder     = create_object<CtiCCFeeder>         (4, "test feeder");
    CtiCCCapBank        *bank       = create_object<CtiCCCapBank>        (5, "test cap bank");

    initialize_capbank(store, bank, feeder, 1);

    bank->setControlDeviceId(6);
    bank->setControlPointId(7);
    bank->setStatusPointId(8);
    bank->setControlDeviceType("cbc 7010");

    store->addCapBankToCBCMap(bank);

    std::auto_ptr<test_AttributeService> attributeService(new test_AttributeService);

    test_AttributeService &attributeBackdoor = *attributeService;

    store->setAttributeService(std::auto_ptr<AttributeService>(attributeService.release()));

    LitePoint p;

    p.setPaoId(6);
    p.setPointId(7);

    attributeBackdoor.points[p.getPointId()] = p;

    const CtiTime currentTime(CtiDate(1, 1, 2010));

    {
        CtiMultiMsg_vec pointChanges, pilMessages;
        Cti::CapControl::EventLogEntries ccEvents;

        feeder->setLastCapBankControlledDeviceId(5);
        bank->setControlStatus(CtiCCCapBank::OpenPending);
        bank->setLastStatusChangeTime(currentTime - 1);

        feeder->attemptToResendControl(currentTime, pointChanges, ccEvents, pilMessages, 10);

        BOOST_REQUIRE_EQUAL(pilMessages.size(), 1);

        CtiRequestMsg *pilRequest = dynamic_cast<CtiRequestMsg *>(pilMessages[0]);

        BOOST_REQUIRE(pilRequest);

        BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control open");
        BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

        delete_container(pointChanges);
        delete_container(pilMessages);
    }
    {
        CtiMultiMsg_vec pointChanges, pilMessages;
        Cti::CapControl::EventLogEntries ccEvents;

        feeder->setLastCapBankControlledDeviceId(5);
        bank->setControlStatus(CtiCCCapBank::ClosePending);
        bank->setLastStatusChangeTime(currentTime - 1);

        feeder->attemptToResendControl(currentTime, pointChanges, ccEvents, pilMessages, 10);

        BOOST_REQUIRE_EQUAL(pilMessages.size(), 1);

        CtiRequestMsg *pilRequest = dynamic_cast<CtiRequestMsg *>(pilMessages[0]);

        BOOST_REQUIRE(pilRequest);

        BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control close");
        BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

        delete_container(pointChanges);
        delete_container(pilMessages);
    }

    p.setStateZeroControl("apple jacks");
    p.setStateOneControl("banana loops");

    attributeBackdoor.points[p.getPointId()] = p;

    {
        CtiMultiMsg_vec pointChanges, pilMessages;
        Cti::CapControl::EventLogEntries ccEvents;

        feeder->setLastCapBankControlledDeviceId(5);
        bank->setControlStatus(CtiCCCapBank::OpenPending);
        bank->setLastStatusChangeTime(currentTime - 1);

        feeder->attemptToResendControl(currentTime, pointChanges, ccEvents, pilMessages, 10);

        BOOST_REQUIRE_EQUAL(pilMessages.size(), 1);

        CtiRequestMsg *pilRequest = dynamic_cast<CtiRequestMsg *>(pilMessages[0]);

        BOOST_REQUIRE(pilRequest);

        BOOST_CHECK_EQUAL(pilRequest->CommandString(), "apple jacks");
        BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

        delete_container(pointChanges);
        delete_container(pilMessages);
    }
    {
        CtiMultiMsg_vec pointChanges, pilMessages;
        Cti::CapControl::EventLogEntries ccEvents;

        feeder->setLastCapBankControlledDeviceId(5);
        bank->setControlStatus(CtiCCCapBank::ClosePending);
        bank->setLastStatusChangeTime(currentTime - 1);

        feeder->attemptToResendControl(currentTime, pointChanges, ccEvents, pilMessages, 10);

        BOOST_REQUIRE_EQUAL(pilMessages.size(), 1);

        CtiRequestMsg *pilRequest = dynamic_cast<CtiRequestMsg *>(pilMessages[0]);

        BOOST_REQUIRE(pilRequest);

        BOOST_CHECK_EQUAL(pilRequest->CommandString(), "banana loops");
        BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

        delete_container(pointChanges);
        delete_container(pilMessages);
    }

    delete feeder;
}


BOOST_AUTO_TEST_CASE(test_create_requests)
{
    Test_CtiCCSubstationBusStore* store = new Test_CtiCCSubstationBusStore();

    CtiCCSubstationBusStore::setInstance(store);

    CtiCCFeeder         *feeder     = create_object<CtiCCFeeder>         (4, "test feeder");
    CtiCCCapBank        *bank       = create_object<CtiCCCapBank>        (5, "test cap bank");

    initialize_capbank(store, bank, feeder, 1);

    bank->setControlDeviceId(6);
    bank->setControlPointId(7);
    bank->setStatusPointId(8);
    bank->setControlDeviceType("cbc 7010");

    store->addCapBankToCBCMap(bank);

    std::auto_ptr<test_AttributeService> attributeService(new test_AttributeService);

    test_AttributeService &attributeBackdoor = *attributeService;

    store->setAttributeService(std::auto_ptr<AttributeService>(attributeService.release()));

    LitePoint p;

    p.setPaoId(6);
    p.setPointId(7);

    attributeBackdoor.points[p.getPointId()] = p;

    const CtiTime currentTime(CtiDate(1, 1, 2010));

    {
        CtiMultiMsg_vec pointChanges;
        Cti::CapControl::EventLogEntries ccEvents;

        _MAX_KVAR = 20000;

        CtiRequestMsg *pilRequest = feeder->createDecreaseVarRequest(bank, pointChanges, ccEvents, "n/a", 0, 0, 0, 0);

        BOOST_REQUIRE(pilRequest);

        BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control close");
        BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

        delete pilRequest;
        delete_container(pointChanges);
    }
    {
        CtiMultiMsg_vec pointChanges;
        Cti::CapControl::EventLogEntries ccEvents;

        CtiRequestMsg *pilRequest = feeder->createDecreaseVarVerificationRequest(bank, pointChanges, ccEvents, "n/a", 0, 0, 0, 0, 0);

        BOOST_REQUIRE(pilRequest);

        BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control close");
        BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

        delete pilRequest;
        delete_container(pointChanges);
    }
    {
        CtiMultiMsg_vec pointChanges;
        Cti::CapControl::EventLogEntries ccEvents;

        CtiRequestMsg *pilRequest = feeder->createForcedVarRequest(bank, pointChanges, ccEvents, CtiCCCapBank::Close, "n/a");

        BOOST_REQUIRE(pilRequest);

        BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control close");
        BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

        delete pilRequest;
        delete_container(pointChanges);
    }
    {
        CtiMultiMsg_vec pointChanges;
        Cti::CapControl::EventLogEntries ccEvents;

        CtiRequestMsg *pilRequest = feeder->createForcedVarRequest(bank, pointChanges, ccEvents, CtiCCCapBank::Open, "n/a");

        BOOST_REQUIRE(pilRequest);

        BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control open");
        BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

        delete pilRequest;
        delete_container(pointChanges);
    }
    {
        CtiMultiMsg_vec pointChanges;
        Cti::CapControl::EventLogEntries ccEvents;

        CtiRequestMsg *pilRequest = feeder->createIncreaseVarRequest(bank, pointChanges, ccEvents, "n/a", 0, 0, 0, 0);

        BOOST_REQUIRE(pilRequest);

        BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control open");
        BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

        delete pilRequest;
        delete_container(pointChanges);
    }
    {
        CtiMultiMsg_vec pointChanges;
        Cti::CapControl::EventLogEntries ccEvents;

        CtiRequestMsg *pilRequest = feeder->createIncreaseVarVerificationRequest(bank, pointChanges, ccEvents, "n/a", 0, 0, 0, 0, 0);

        BOOST_REQUIRE(pilRequest);

        BOOST_CHECK_EQUAL(pilRequest->CommandString(), "control open");
        BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

        delete pilRequest;
        delete_container(pointChanges);
    }

    p.setStateZeroControl("apple jacks");
    p.setStateOneControl("banana loops");

    attributeBackdoor.points[p.getPointId()] = p;

    {
        CtiMultiMsg_vec pointChanges;
        Cti::CapControl::EventLogEntries ccEvents;

        CtiRequestMsg *pilRequest = feeder->createDecreaseVarRequest(bank, pointChanges, ccEvents, "n/a", 0, 0, 0, 0);

        BOOST_REQUIRE(pilRequest);

        BOOST_CHECK_EQUAL(pilRequest->CommandString(), "banana loops");
        BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

        delete pilRequest;
        delete_container(pointChanges);
    }
    {
        CtiMultiMsg_vec pointChanges;
        Cti::CapControl::EventLogEntries ccEvents;

        CtiRequestMsg *pilRequest = feeder->createDecreaseVarVerificationRequest(bank, pointChanges, ccEvents, "n/a", 0, 0, 0, 0, 0);

        BOOST_REQUIRE(pilRequest);

        BOOST_CHECK_EQUAL(pilRequest->CommandString(), "banana loops");
        BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

        delete pilRequest;
        delete_container(pointChanges);
    }
    {
        CtiMultiMsg_vec pointChanges;
        Cti::CapControl::EventLogEntries ccEvents;

        CtiRequestMsg *pilRequest = feeder->createForcedVarRequest(bank, pointChanges, ccEvents, CtiCCCapBank::Close, "n/a");

        BOOST_REQUIRE(pilRequest);

        BOOST_CHECK_EQUAL(pilRequest->CommandString(), "banana loops");
        BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

        delete pilRequest;
        delete_container(pointChanges);
    }
    {
        CtiMultiMsg_vec pointChanges;
        Cti::CapControl::EventLogEntries ccEvents;

        CtiRequestMsg *pilRequest = feeder->createForcedVarRequest(bank, pointChanges, ccEvents, CtiCCCapBank::Open, "n/a");

        BOOST_REQUIRE(pilRequest);

        BOOST_CHECK_EQUAL(pilRequest->CommandString(), "apple jacks");
        BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

        delete pilRequest;
        delete_container(pointChanges);
    }
    {
        CtiMultiMsg_vec pointChanges;
        Cti::CapControl::EventLogEntries ccEvents;

        CtiRequestMsg *pilRequest = feeder->createIncreaseVarRequest(bank, pointChanges, ccEvents, "n/a", 0, 0, 0, 0);

        BOOST_REQUIRE(pilRequest);

        BOOST_CHECK_EQUAL(pilRequest->CommandString(), "apple jacks");
        BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

        delete pilRequest;
        delete_container(pointChanges);
    }
    {
        CtiMultiMsg_vec pointChanges;
        Cti::CapControl::EventLogEntries ccEvents;

        CtiRequestMsg *pilRequest = feeder->createIncreaseVarVerificationRequest(bank, pointChanges, ccEvents, "n/a", 0, 0, 0, 0, 0);

        BOOST_REQUIRE(pilRequest);

        BOOST_CHECK_EQUAL(pilRequest->CommandString(), "apple jacks");
        BOOST_CHECK_EQUAL(pilRequest->DeviceId(), 6);

        delete pilRequest;
        delete_container(pointChanges);
    }

    delete feeder;
}


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
    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );

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

BOOST_AUTO_TEST_CASE( test_ccFeeder_default_construction )
{
    _RATE_OF_CHANGE_DEPTH = 5;      // this shows up in the regressions: getRegDepth()

    CtiCCFeeder feeder;

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
    BOOST_CHECK_EQUAL(   true, feeder.isDirty() );

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
    BOOST_CHECK_EQUAL(      0, feeder.getBusOptimizedVarCategory() );
    BOOST_CHECK_EQUAL(      0, feeder.getDecimalPlaces() );
    BOOST_CHECK_EQUAL(      0, feeder.getEventSequence() );
    BOOST_CHECK_EQUAL(      0, feeder.getCurrentVerificationCapBankId() );
    BOOST_CHECK_EQUAL(      0, feeder.getCurrentVerificationCapBankOrigState() );
    BOOST_CHECK_EQUAL(      0, feeder.getIVCount() );
    BOOST_CHECK_EQUAL(      0, feeder.getIWCount() );
    BOOST_CHECK_EQUAL(      0, feeder.getPhaseBId() );
    BOOST_CHECK_EQUAL(      0, feeder.getPhaseCId() );
    BOOST_CHECK_EQUAL(      0, feeder.getRetryIndex() );

    BOOST_CHECK_EQUAL( UnintializedQuality, feeder.getCurrentVarPointQuality() );
    BOOST_CHECK_EQUAL( UnintializedQuality, feeder.getCurrentWattPointQuality() );
    BOOST_CHECK_EQUAL( UnintializedQuality, feeder.getCurrentVoltPointQuality() );

    BOOST_CHECK_EQUAL(   0.0f, feeder.getDisplayOrder() );

    BOOST_CHECK_EQUAL(    0.0, feeder.getCurrentVarLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getCurrentWattLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getCurrentVoltLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getEstimatedVarLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getVarValueBeforeControl() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getBusOptimizedVarOffset() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getPowerFactorValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getKVARSolution() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getEstimatedPowerFactorValue() );
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
    BOOST_CHECK_EQUAL(     "", feeder.getSolution() );
    BOOST_CHECK_EQUAL(     "", feeder.getParentControlUnits() );
    BOOST_CHECK_EQUAL(     "", feeder.getParentName() );

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

//  These are default constructed to CtiTime::now() -- don't test...
//
//    BOOST_CHECK_EQUAL( gInvalidCtiTime , feeder.getLastCurrentVarPointUpdateTime() );
//    BOOST_CHECK_EQUAL( gInvalidCtiTime , feeder.getLastOperationTime() );
//    BOOST_CHECK_EQUAL( gInvalidCtiTime , feeder.getLastWattPointTime() );
//    BOOST_CHECK_EQUAL( gInvalidCtiTime , feeder.getLastVoltPointTime() );
}

BOOST_AUTO_TEST_CASE( test_ccFeeder_default_construction_with_strategy_manager )
{
    _RATE_OF_CHANGE_DEPTH = 5;      // this shows up in the regressions: getRegDepth()

    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );

    CtiCCFeeder feeder( &_strategyManager );

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
    BOOST_CHECK_EQUAL(   true, feeder.isDirty() );

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
    BOOST_CHECK_EQUAL(      0, feeder.getBusOptimizedVarCategory() );
    BOOST_CHECK_EQUAL(      0, feeder.getDecimalPlaces() );
    BOOST_CHECK_EQUAL(      0, feeder.getEventSequence() );
    BOOST_CHECK_EQUAL(      0, feeder.getCurrentVerificationCapBankId() );
    BOOST_CHECK_EQUAL(      0, feeder.getCurrentVerificationCapBankOrigState() );
    BOOST_CHECK_EQUAL(      0, feeder.getIVCount() );
    BOOST_CHECK_EQUAL(      0, feeder.getIWCount() );
    BOOST_CHECK_EQUAL(      0, feeder.getPhaseBId() );
    BOOST_CHECK_EQUAL(      0, feeder.getPhaseCId() );
    BOOST_CHECK_EQUAL(      0, feeder.getRetryIndex() );

    BOOST_CHECK_EQUAL( UnintializedQuality, feeder.getCurrentVarPointQuality() );
    BOOST_CHECK_EQUAL( UnintializedQuality, feeder.getCurrentWattPointQuality() );
    BOOST_CHECK_EQUAL( UnintializedQuality, feeder.getCurrentVoltPointQuality() );

    BOOST_CHECK_EQUAL(   0.0f, feeder.getDisplayOrder() );

    BOOST_CHECK_EQUAL(    0.0, feeder.getCurrentVarLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getCurrentWattLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getCurrentVoltLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getEstimatedVarLoadPointValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getVarValueBeforeControl() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getBusOptimizedVarOffset() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getPowerFactorValue() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getKVARSolution() );
    BOOST_CHECK_EQUAL(    0.0, feeder.getEstimatedPowerFactorValue() );
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
    BOOST_CHECK_EQUAL(     "", feeder.getSolution() );
    BOOST_CHECK_EQUAL(     "", feeder.getParentControlUnits() );
    BOOST_CHECK_EQUAL(     "", feeder.getParentName() );

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

//  These are default constructed to CtiTime::now() -- don't test...
//
//    BOOST_CHECK_EQUAL( gInvalidCtiTime , feeder.getLastCurrentVarPointUpdateTime() );
//    BOOST_CHECK_EQUAL( gInvalidCtiTime , feeder.getLastOperationTime() );
//    BOOST_CHECK_EQUAL( gInvalidCtiTime , feeder.getLastWattPointTime() );
//    BOOST_CHECK_EQUAL( gInvalidCtiTime , feeder.getLastVoltPointTime() );
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
    initialize_station( store, station, area );
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

    delete station;
    delete area;
}

BOOST_AUTO_TEST_SUITE_END()
