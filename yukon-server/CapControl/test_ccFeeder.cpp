#include <boost/test/unit_test.hpp>

#include "ccfeeder.h"
#include "ccsubstationbus.h"
#include "ccsubstationbusstore.h"

#include "ccUnitTestUtil.h"
#include "StrategyManager.h"
#include "PFactorKWKVarStrategy.h"


extern BOOL _RETRY_FAILED_BANKS;

using namespace std;

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
    BOOST_CHECK_EQUAL(bank->getIgnoreFlag(),FALSE);
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
    BOOST_CHECK_EQUAL(capBank1->getIgnoreFlag(),TRUE);
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
    BOOST_CHECK_EQUAL(capBank1->getMaxDailyOpsHitFlag(),TRUE);
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
    BOOST_CHECK_EQUAL(bank->getRetryCloseFailedFlag(),TRUE);

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
    BOOST_CHECK_EQUAL(bank->getIgnoreFlag(),FALSE);
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
    BOOST_CHECK_EQUAL(capBank4->getIgnoreFlag(),TRUE);
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
    BOOST_CHECK_EQUAL(capBank4->getMaxDailyOpsHitFlag(),TRUE);
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

BOOST_AUTO_TEST_SUITE_END()
