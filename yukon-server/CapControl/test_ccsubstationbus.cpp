#include <boost/test/unit_test.hpp>

#include "ccsubstationbus.h"
#include "ccsubstationbusstore.h"
#include "mgr_paosched.h"
#include "ccUnitTestUtil.h"

#include "StrategyManager.h"
#include "PFactorKWKVarStrategy.h"
#include "ExecutorFactory.h"
#include "MsgVerifyBanks.h"

using namespace std;

extern ULONG _MAX_KVAR;
extern ULONG _SEND_TRIES;

BOOST_AUTO_TEST_SUITE( test_ccsubstationbus )

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
                newStrategy->setControlMethod( ControlStrategy::IndividualFeederControlMethod );
                newStrategy->setControlInterval(0);
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


void initialize_area(Test_CtiCCSubstationBusStore* store, CtiCCArea* area)
{
    store->insertAreaToPaoMap(area);
    area->setDisableFlag(FALSE);
}
void initialize_station(Test_CtiCCSubstationBusStore* store, CtiCCSubstation* station, CtiCCArea* parentArea)
{
    station->setSaEnabledFlag(FALSE);
    station->setParentId(parentArea->getPaoId());
    parentArea->getSubStationList()->push_back(station->getPaoId());
    store->insertSubstationToPaoMap(station);
    station->setDisableFlag(FALSE);


}
void initialize_bus(Test_CtiCCSubstationBusStore* store, CtiCCSubstationBus* bus, CtiCCSubstation* parentStation)
{


    bus->setParentId(parentStation->getPaoId());
    bus->setEventSequence(22);
    bus->setCurrentVarLoadPointId(1);
    bus->setCurrentVarLoadPointValue(55, CtiTime());
    bus->setVerificationFlag(FALSE);
    parentStation->getCCSubIds()->push_back(bus->getPaoId());
    store->insertSubBusToPaoMap(bus);
    bus->setDisableFlag(FALSE);
    bus->setVerificationFlag(FALSE);
    bus->setPerformingVerificationFlag(FALSE);
    bus->setVerificationDoneFlag(FALSE);
}
void initialize_feeder(Test_CtiCCSubstationBusStore* store, CtiCCFeeder* feed, CtiCCSubstationBus* parentBus, long displayOrder)
{

    long feederId = feed->getPaoId();
    long busId = parentBus->getPaoId();
    feed->setParentId(busId);
    feed->setDisplayOrder(displayOrder);
    parentBus->getCCFeeders().push_back(feed);
    store->insertItemsIntoMap(CtiCCSubstationBusStore::FeederIdSubBusIdMap, &feederId, &busId);
    store->insertFeederToPaoMap(feed);
    feed->setDisableFlag(FALSE);
    feed->setVerificationFlag(FALSE);
    feed->setPerformingVerificationFlag(FALSE);
    feed->setVerificationDoneFlag(FALSE);

    feed->setStrategy( -1 );        // init to NoStrategy

    feed->setCurrentVarPointQuality(NormalQuality);
    feed->setWaitForReCloseDelayFlag(false);

}

void initialize_capbank(Test_CtiCCSubstationBusStore* store, CtiCCCapBank* cap, CtiCCFeeder* parentFeed, long displayOrder)
{
    long bankId = cap->getPaoId();
    long fdrId = parentFeed->getPaoId();
    cap->setParentId(fdrId);
    cap->setControlOrder(displayOrder);
    cap->setCloseOrder(displayOrder);
    cap->setTripOrder(displayOrder);
    parentFeed->getCCCapBanks().push_back(cap);
    store->insertItemsIntoMap(CtiCCSubstationBusStore::CapBankIdFeederIdMap, &bankId, &fdrId);
    cap->setOperationalState(CtiCCCapBank::SwitchedOperationalState);
    cap->setDisableFlag(FALSE);
    cap->setVerificationFlag(FALSE);
    cap->setPerformingVerificationFlag(FALSE);
    cap->setVerificationDoneFlag(FALSE);
    cap->setBankSize(600);

    cap->setControlPointId(1);
}

BOOST_AUTO_TEST_CASE(test_cannot_control_bank_text)
{
    Test_CtiCCSubstationBusStore* store = new Test_CtiCCSubstationBusStore();
    CtiCCSubstationBusStore::setInstance(store);

    CtiCCSubstationBus *bus = new CtiCCSubstationBus();
    CtiCCSubstation *station = new CtiCCSubstation();
    CtiCCArea *area = new CtiCCArea();

    bus->setStrategy( -1 );         // init to NoStrategy

    area->setPaoId(3);
    station->setPaoId(2);
    bus->setPaoId(1);
    bus->setPaoName("Test SubBus");
    bus->setParentId(2);
    bus->setEventSequence(22);
    bus->setCurrentVarLoadPointValue(55, CtiTime());
    station->setParentId(1);
    station->setSaEnabledFlag(FALSE);
    store->insertAreaToPaoMap(area);
    area->getSubStationList()->push_back(station->getPaoId());
    store->insertSubstationToPaoMap(station);
    station->getCCSubIds()->push_back(bus->getPaoId());
    store->insertSubBusToPaoMap(bus);

    bus->setCorrectionNeededNoBankAvailFlag(FALSE);
    CtiMultiMsg_vec ccEvents;
    bus->createCannotControlBankText("Increase Var", "Open", ccEvents);
    BOOST_CHECK_EQUAL(bus->getCorrectionNeededNoBankAvailFlag(), 1);
    BOOST_CHECK_EQUAL(ccEvents.size(), 1);
    bus->createCannotControlBankText("Increase Var", "Open", ccEvents);
    BOOST_CHECK_EQUAL(ccEvents.size(), 1);
    bus->setCorrectionNeededNoBankAvailFlag(FALSE);
    bus->createCannotControlBankText("Increase Var", "Open", ccEvents);
    BOOST_CHECK_EQUAL(ccEvents.size(), 2);
    store->deleteInstance();
}

BOOST_AUTO_TEST_CASE(test_lock_invalid_ctitime)
{
    unsigned long x = 0;
    CtiTime now(x);
    struct tm start_tm;

    now.extract(&start_tm);


}
BOOST_AUTO_TEST_CASE(test_temp_move_feeder)
{
    Test_CtiCCSubstationBusStore* store = new Test_CtiCCSubstationBusStore();
    CtiCCSubstationBusStore::setInstance(store);

    CtiCCArea *area = create_object<CtiCCArea>(1, "Area-1");
    CtiCCSubstation *station = create_object<CtiCCSubstation>(2, "Substation-A");


    CtiCCSubstationBus *bus1 = create_object<CtiCCSubstationBus>(3, "SubBus-A1");
    CtiCCFeeder *feed11 = create_object<CtiCCFeeder>(11, "Feeder11");
    CtiCCFeeder *feed12 = create_object<CtiCCFeeder>(12, "Feeder12");
    CtiCCFeeder *feed13 = create_object<CtiCCFeeder>(13, "Feeder13");

    CtiCCSubstationBus *bus2 = create_object<CtiCCSubstationBus>(4, "SubBus-A2");
    CtiCCFeeder *feed21 = create_object<CtiCCFeeder>(21, "Feeder21");
    CtiCCFeeder *feed22 = create_object<CtiCCFeeder>(22, "Feeder22");
    CtiCCFeeder *feed23 = create_object<CtiCCFeeder>(23, "Feeder23");


    initialize_area(store, area);
    initialize_station(store, station, area);
    initialize_bus(store, bus1, station);
    initialize_bus(store, bus2, station);

    initialize_feeder(store, feed11, bus1, 1);
    initialize_feeder(store, feed12, bus1, 2);
    initialize_feeder(store, feed13, bus1, 3);
    initialize_feeder(store, feed21, bus2, 1);
    initialize_feeder(store, feed22, bus2, 2);
    initialize_feeder(store, feed23, bus2, 3);


    BOOST_CHECK_EQUAL(bus1->getCCFeeders().size(), 3);
    BOOST_CHECK_EQUAL(bus2->getCCFeeders().size(), 3);



    CtiFeeder_vec& ccFeeders = bus1->getCCFeeders();
    int j = ccFeeders.size();
    while (j > 0)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j-1];

        CtiCCExecutorFactory::createExecutor(new CtiCCObjectMoveMsg(false, bus1->getPaoId(), currentFeeder->getPaoId(), bus2->getPaoId(), currentFeeder->getDisplayOrder() + 0.5))->execute();
        j--;
    }

    BOOST_CHECK_EQUAL(bus1->getCCFeeders().size(), 0);
    BOOST_CHECK_EQUAL(bus2->getCCFeeders().size(), 6);


    CtiFeeder_vec& ccFeeders2 = bus2->getCCFeeders();
    j = ccFeeders2.size();
    while (j > 0)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders2[j-1];
        if (currentFeeder->getOriginalParent().getOriginalParentId() == bus1->getPaoId())
        {
            CtiCCExecutorFactory::createExecutor(new ItemCommand(CapControlCommand::RETURN_FEEDER_TO_ORIGINAL_SUBBUS, currentFeeder->getPaoId()))->execute();
        }
        j--;
    }

    BOOST_CHECK_EQUAL(bus1->getCCFeeders().size(), 3);
    BOOST_CHECK_EQUAL(bus2->getCCFeeders().size(), 3);

    store->deleteInstance();
}


BOOST_AUTO_TEST_CASE(test_parallel_bus)
{
    Test_CtiCCSubstationBusStore* store = new Test_CtiCCSubstationBusStore();
    CtiCCSubstationBusStore::setInstance(store);

    _MAX_KVAR = 20000;
    _SEND_TRIES = 1;

    CtiTime currentDateTime;
    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );
    _strategyManager.reloadAll();

    CtiCCArea *area = create_object<CtiCCArea>(1, "Area-1");
    CtiCCSubstation *station = create_object<CtiCCSubstation>(2, "Substation-A");


    CtiCCSubstationBus *bus1 = create_object<CtiCCSubstationBus>(3, "SubBus-A1");
    CtiCCFeeder *feed11 = create_object<CtiCCFeeder>(11, "Feeder11");
    CtiCCFeeder *feed12 = create_object<CtiCCFeeder>(12, "Feeder12");
    CtiCCFeeder *feed13 = create_object<CtiCCFeeder>(13, "Feeder13");

    CtiCCSubstationBus *bus2 = create_object<CtiCCSubstationBus>(4, "SubBus-A2");
    CtiCCFeeder *feed21 = create_object<CtiCCFeeder>(21, "Feeder21");
    CtiCCFeeder *feed22 = create_object<CtiCCFeeder>(22, "Feeder22");
    CtiCCFeeder *feed23 = create_object<CtiCCFeeder>(23, "Feeder23");
    CtiCCCapBank *cap11a = create_object<CtiCCCapBank>(14, "capBank11a");
    CtiCCCapBank *cap11b = create_object<CtiCCCapBank>(15, "capBank11b");
    CtiCCCapBank *cap11c = create_object<CtiCCCapBank>(16, "capBank11c");

    CtiCCCapBank *cap21a = create_object<CtiCCCapBank>(24, "capBank21a");
    CtiCCCapBank *cap21b = create_object<CtiCCCapBank>(25, "capBank21b");
    CtiCCCapBank *cap21c = create_object<CtiCCCapBank>(26, "capBank21c");

    Test_CtiCapController *controller = new Test_CtiCapController();
    CtiCapController::setInstance(controller);

    initialize_area(store, area);
    initialize_station(store, station, area);
    initialize_bus(store, bus1, station);
    initialize_bus(store, bus2, station);

    initialize_feeder(store, feed11, bus1, 1);
    initialize_feeder(store, feed12, bus1, 2);
    initialize_feeder(store, feed13, bus1, 3);
    initialize_feeder(store, feed21, bus2, 1);
    initialize_feeder(store, feed22, bus2, 2);
    initialize_feeder(store, feed23, bus2, 3);


    initialize_capbank(store, cap11a, feed11, 1);
    initialize_capbank(store, cap11b, feed11, 2);
    initialize_capbank(store, cap11c, feed11, 3);

    initialize_capbank(store, cap21a, feed21, 1);
    initialize_capbank(store, cap21b, feed21, 2);
    initialize_capbank(store, cap21c, feed21, 3);

    BOOST_CHECK_EQUAL(bus1->getCCFeeders().size(), 3);
    BOOST_CHECK_EQUAL(bus2->getCCFeeders().size(), 3);



    CtiFeeder_vec& ccFeeders = bus1->getCCFeeders();
    int j = ccFeeders.size();
    while (j > 0)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j-1];

        CtiCCExecutorFactory::createExecutor(new CtiCCObjectMoveMsg(false, bus1->getPaoId(), currentFeeder->getPaoId(), bus2->getPaoId(), currentFeeder->getDisplayOrder() + 0.5))->execute();
        j--;
    }

    bus1->setAltDualSubId(bus2->getPaoId());
    bus1->setSwitchOverStatus(true);
    bus1->setDualBusEnable(true);
    bus2->setPrimaryBusFlag(true);
    store->insertSubBusToAltBusMap(bus1);

    bus1->setCurrentVarLoadPointId(1);
    bus1->setCurrentWattLoadPointId(1);
    bus2->setCurrentVarLoadPointId(2);
    bus2->setCurrentWattLoadPointId(2);
    bus1->setStrategyManager( &_strategyManager );
    bus1->setStrategy(100);
    bus2->setStrategyManager( &_strategyManager );
    bus2->setStrategy(100);

    bus1->setCurrentVarLoadPointValue(700, currentDateTime);
    bus1->setCurrentVoltLoadPointValue(120);
    bus1->setCurrentWattLoadPointValue(1200);
    bus2->setCurrentVarLoadPointValue(700, currentDateTime);
    bus2->setCurrentVoltLoadPointValue(120);
    bus2->setCurrentWattLoadPointValue(1200);

    bus1->figureEstimatedVarLoadPointValue();
    controller->adjustAlternateSettings(1, bus1);

    BOOST_CHECK_EQUAL(bus1->getCurrentVarLoadPointValue(), 700);
    BOOST_CHECK_EQUAL(bus2->getCurrentVarLoadPointValue(), 1400);
    BOOST_CHECK_EQUAL(bus1->getEstimatedVarLoadPointValue(), 700);
    BOOST_CHECK_EQUAL(bus2->getEstimatedVarLoadPointValue(), 1400);

    cap11a->setControlStatus(CtiCCCapBank::Close);

    bus2->figureEstimatedVarLoadPointValue();
    BOOST_CHECK_EQUAL(bus2->getEstimatedVarLoadPointValue(), 2000);

    store->deleteInstance();
}

BOOST_AUTO_TEST_CASE(test_analyze_feeder_for_verification)
{

    Test_CtiCCSubstationBusStore* store = new Test_CtiCCSubstationBusStore();
    CtiCCSubstationBusStore::setInstance(store);

    _MAX_KVAR = 20000;
    _SEND_TRIES = 1;

    StrategyManager _strategyManager( std::auto_ptr<StrategyUnitTestLoader>( new StrategyUnitTestLoader ) );
    _strategyManager.reloadAll();

    CtiTime currentDateTime;
    CtiMultiMsg* multiDispatchMsg = new CtiMultiMsg();
    CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
    CtiMultiMsg* multiCapMsg = new CtiMultiMsg();
    CtiMultiMsg* multiCCEventMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multiDispatchMsg->getData();
    CtiMultiMsg_vec& pilMessages = multiPilMsg->getData();
    CtiMultiMsg_vec& capMessages = multiCapMsg->getData();
    CtiMultiMsg_vec& ccEvents = multiCCEventMsg->getData();

    CtiCCArea *area = create_object<CtiCCArea>(1, "Area-1");
    CtiCCSubstation *station = create_object<CtiCCSubstation>(2, "Substation-A");

    CtiCCSubstationBus *bus1 = create_object<CtiCCSubstationBus>(3, "SubBus-A1");
    CtiCCFeeder *feed11 = create_object<CtiCCFeeder>(11, "Feeder11");
    CtiCCFeeder *feed12 = create_object<CtiCCFeeder>(12, "Feeder12");
    CtiCCFeeder *feed13 = create_object<CtiCCFeeder>(13, "Feeder13");
    CtiCCCapBank *cap11a = create_object<CtiCCCapBank>(14, "capBank11a");
    CtiCCCapBank *cap11b = create_object<CtiCCCapBank>(15, "capBank11b");
    CtiCCCapBank *cap11c = create_object<CtiCCCapBank>(16, "capBank11c");

    initialize_area(store, area);
    initialize_station(store, station, area);
    initialize_bus(store, bus1, station);

    initialize_feeder(store, feed11, bus1, 1);
    initialize_feeder(store, feed12, bus1, 2);
    initialize_feeder(store, feed13, bus1, 3);


    initialize_capbank(store, cap11a, feed11, 1);
    initialize_capbank(store, cap11b, feed11, 2);
    initialize_capbank(store, cap11c, feed11, 3);

    bus1->setStrategyManager( &_strategyManager );
    bus1->setStrategy(100);
    bus1->setVerificationDisableOvUvFlag(false);

    feed11->setUsePhaseData(false);
    cap11a->setControlStatus(CtiCCCapBank::Open);
    cap11b->setControlStatus(CtiCCCapBank::OpenQuestionable);
    cap11c->setControlStatus(CtiCCCapBank::OpenFail);

    feed11->setCurrentVarLoadPointId(1);
    feed11->setCurrentWattLoadPointId(1);
    feed11->setCurrentVarLoadPointValue(700, currentDateTime);
    feed11->setCurrentWattLoadPointValue(1200);

    CtiCCExecutorFactory::createExecutor(new VerifyBanks(bus1->getPaoId(),false, CapControlCommand::VERIFY_ALL_BANK))->execute();

    bus1->setCapBanksToVerifyFlags(CtiPAOScheduleManager::AllBanks, ccEvents);

    BOOST_CHECK_EQUAL(bus1->getVerificationFlag(), TRUE);
    BOOST_CHECK_EQUAL(feed11->getVerificationFlag(), TRUE);
    BOOST_CHECK_EQUAL(cap11a->getVerificationFlag(), TRUE);
    BOOST_CHECK_EQUAL(cap11b->getVerificationFlag(), TRUE);
    BOOST_CHECK_EQUAL(cap11c->getVerificationFlag(), TRUE);
    BOOST_CHECK_EQUAL(ccEvents.size(), 3);

    bus1->analyzeVerificationByFeeder(currentDateTime, pointChanges, ccEvents, pilMessages, capMessages);
    BOOST_CHECK_EQUAL(bus1->getPerformingVerificationFlag(), TRUE);
    BOOST_CHECK_EQUAL(feed11->getPerformingVerificationFlag(), TRUE);
    BOOST_CHECK_EQUAL(cap11a->getPerformingVerificationFlag(), TRUE);
    BOOST_CHECK_EQUAL(cap11a->getControlStatus(), CtiCCCapBank::ClosePending);
    currentDateTime = currentDateTime + 1;
    feed11->setCurrentVarLoadPointValue(0, currentDateTime);

    bus1->analyzeVerificationByFeeder(currentDateTime, pointChanges, ccEvents, pilMessages, capMessages);

    BOOST_CHECK_EQUAL(cap11a->getControlStatus(), CtiCCCapBank::OpenPending);

    currentDateTime = currentDateTime + 1;
    feed11->setCurrentVarLoadPointValue(600, currentDateTime);

    bus1->analyzeVerificationByFeeder(currentDateTime, pointChanges, ccEvents, pilMessages, capMessages);

    BOOST_CHECK_EQUAL(cap11a->getControlStatus(), CtiCCCapBank::Open);
    BOOST_CHECK_EQUAL(cap11b->getControlStatus(), CtiCCCapBank::ClosePending);

    currentDateTime = currentDateTime + 1;
    feed11->setCurrentVarLoadPointValue(1, currentDateTime);

    bus1->analyzeVerificationByFeeder(currentDateTime, pointChanges, ccEvents, pilMessages, capMessages);

    BOOST_CHECK_EQUAL(cap11b->getControlStatus(), CtiCCCapBank::OpenPending);

    currentDateTime = currentDateTime + 1;
    feed11->setCurrentVarLoadPointValue(600, currentDateTime);

    bus1->analyzeVerificationByFeeder(currentDateTime, pointChanges, ccEvents, pilMessages, capMessages);

    BOOST_CHECK_EQUAL(cap11b->getControlStatus(), CtiCCCapBank::Open);
    BOOST_CHECK_EQUAL(cap11c->getControlStatus(), CtiCCCapBank::ClosePending);

    currentDateTime = currentDateTime + bus1->getStrategy()->getMaxConfirmTime() + 1;

    bus1->analyzeVerificationByFeeder(currentDateTime, pointChanges, ccEvents, pilMessages, capMessages);

    BOOST_CHECK_EQUAL(cap11c->getControlStatus(), CtiCCCapBank::OpenPending);

    currentDateTime = currentDateTime + 1;
    feed11->setCurrentVarLoadPointValue(1200, currentDateTime);

    bus1->analyzeVerificationByFeeder(currentDateTime, pointChanges, ccEvents, pilMessages, capMessages);

    BOOST_CHECK_EQUAL(cap11c->getControlStatus(), CtiCCCapBank::ClosePending);

    currentDateTime = currentDateTime + 1;
    feed11->setCurrentVarLoadPointValue(0, currentDateTime);

    bus1->analyzeVerificationByFeeder(currentDateTime, pointChanges, ccEvents, pilMessages, capMessages);

    BOOST_CHECK_EQUAL(cap11c->getControlStatus(), CtiCCCapBank::Close);

    CtiCCExecutorFactory::createExecutor(new VerifyBanks(bus1->getPaoId(),false, CapControlCommand::STOP_VERIFICATION))->execute();

    BOOST_CHECK_EQUAL(  bus1->getPerformingVerificationFlag(), FALSE);
    BOOST_CHECK_EQUAL(feed11->getPerformingVerificationFlag(), FALSE);
    BOOST_CHECK_EQUAL(cap11a->getPerformingVerificationFlag(), FALSE);
    BOOST_CHECK_EQUAL(cap11b->getPerformingVerificationFlag(), FALSE);
    BOOST_CHECK_EQUAL(cap11c->getPerformingVerificationFlag(), FALSE);
    BOOST_CHECK_EQUAL(  bus1->getVerificationFlag(), FALSE);
    BOOST_CHECK_EQUAL(feed11->getVerificationFlag(), FALSE);
    BOOST_CHECK_EQUAL(cap11a->getVerificationFlag(), FALSE);

    store->deleteInstance();
}

BOOST_AUTO_TEST_SUITE_END()
