/*---------------------------------------------------------------------------
        Filename:  test_ccapbank.cpp

        Programmer:  Jess Oteson

        Initial Date:  5/11/2007

        COPYRIGHT:  Copyright (C) Cannon Technologies 2007
---------------------------------------------------------------------------*/

#define BOOST_AUTO_TEST_MAIN "Test CCCapBank"

#include <boost/test/unit_test.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>


#include <string>
#include <rw/rwdate.h>
#include <rw/rwtime.h>
#include <rw/zone.h>
#include <iostream>
#include <time.h>
#include <sstream>    // for istringstream
#include <locale>

#include "yukon.h"
#include "ctitime.h"
#include "ccsubstationbus.h"
#include "ccsubstation.h"
#include "ccarea.h"
#include "ccsubstationbusstore.h"
#include "ccexecutor.h"
#include "ccmessage.h"
#include "mgr_paosched.h"

#include "ccUnitTestUtil.h"

using boost::unit_test_framework::test_suite;
using namespace std;

void initialize_area(CtiCCSubstationBusStore* store, CtiCCArea* area)
{
    store->addAreaToPaoMap(area);
    area->setDisableFlag(FALSE);
}
void initialize_station(CtiCCSubstationBusStore* store, CtiCCSubstation* station, CtiCCArea* parentArea)
{
    station->setSaEnabledFlag(FALSE);
    station->setParentId(parentArea->getPAOId());
    parentArea->getSubStationList()->push_back(station->getPAOId());
    store->addSubstationToPaoMap(station);
    station->setDisableFlag(FALSE);


}
void initialize_bus(CtiCCSubstationBusStore* store, CtiCCSubstationBus* bus, CtiCCSubstation* parentStation)
{
    bus->setParentId(parentStation->getPAOId());
    bus->setEventSequence(22);
    bus->setCurrentVarLoadPointValue(55, CtiTime());
    bus->setVerificationFlag(FALSE);
    parentStation->getCCSubIds()->push_back(bus->getPAOId());
    store->addSubBusToPaoMap(bus);
    bus->setDisableFlag(FALSE);
    bus->setVerificationFlag(FALSE);           
    bus->setPerformingVerificationFlag(FALSE); 
    bus->setVerificationDoneFlag(FALSE);       
}
void initialize_feeder(CtiCCSubstationBusStore* store, CtiCCFeeder* feed, CtiCCSubstationBus* parentBus, long displayOrder)
{
    long feederId = feed->getPAOId();
    long busId = parentBus->getPAOId();
    feed->setParentId(busId);
    feed->setDisplayOrder(displayOrder);
    parentBus->getCCFeeders().push_back(feed);
    store->insertItemsIntoMap(CtiCCSubstationBusStore::FeederIdSubBusIdMap, &feederId, &busId);
    store->addFeederToPaoMap(feed);
    feed->setDisableFlag(FALSE);
    feed->setVerificationFlag(FALSE);
    feed->setPerformingVerificationFlag(FALSE);
    feed->setVerificationDoneFlag(FALSE);

}

void initialize_capbank(CtiCCSubstationBusStore* store, CtiCCCapBank* cap, CtiCCFeeder* parentFeed, long displayOrder)
{
    long bankId = cap->getPAOId();
    long fdrId = parentFeed->getPAOId();
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
    
}

BOOST_AUTO_TEST_CASE(test_cannot_control_bank_text)
{
    CtiCCSubstationBus *bus = new CtiCCSubstationBus();
    CtiCCSubstation *station = new CtiCCSubstation();
    CtiCCArea *area = new CtiCCArea();
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance(false);
    area->setPAOId(3);
    station->setPAOId(2);
    bus->setPAOId(1);
    bus->setPAOName("Test SubBus");
    bus->setParentId(2);
    bus->setEventSequence(22);
    bus->setCurrentVarLoadPointValue(55, CtiTime());
    station->setParentId(1);
    station->setSaEnabledFlag(FALSE);
    store->addAreaToPaoMap(area);
    area->getSubStationList()->push_back(station->getPAOId());
    store->addSubstationToPaoMap(station);
    station->getCCSubIds()->push_back(bus->getPAOId());
    store->addSubBusToPaoMap(bus);

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

BOOST_AUTO_TEST_CASE(test_temp_move_feeder)
{

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

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance(false);
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

        CtiCCExecutorFactory f;
        CtiCCExecutor* executor = f.createExecutor(new CtiCCObjectMoveMsg(0, bus1->getPAOId(), currentFeeder->getPAOId(), bus2->getPAOId(), currentFeeder->getDisplayOrder() + 0.5));
        executor->Execute();
        delete executor;
        j--;
    }

    BOOST_CHECK_EQUAL(bus1->getCCFeeders().size(), 0);
    BOOST_CHECK_EQUAL(bus2->getCCFeeders().size(), 6);


    CtiFeeder_vec& ccFeeders2 = bus2->getCCFeeders();
    j = ccFeeders2.size();
    while (j > 0)
    {
        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders2[j-1];
        if (currentFeeder->getOriginalSubBusId() == bus1->getPAOId())
        {

            CtiCCExecutorFactory f;
            CtiCCExecutor* executor = f.createExecutor(new CtiCCCommand(CtiCCCommand::RETURN_FEEDER_TO_ORIGINAL_SUBBUS, currentFeeder->getPAOId()));
            executor->Execute();
            delete executor;
        }
        j--;
    }

    BOOST_CHECK_EQUAL(bus1->getCCFeeders().size(), 3);
    BOOST_CHECK_EQUAL(bus2->getCCFeeders().size(), 3);

    store->deleteInstance();
}

BOOST_AUTO_TEST_CASE(test_analyze_feeder_for_verification)
{

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

    CtiCCStrategy* strat = new CtiCCStrategy();
    strat->setStrategyId(100);
    strat->setStrategyName("StrategyIndvlFdr");
    strat->setControlInterval(0);
    strat->setControlMethod(CtiCCSubstationBus::IndividualFeederControlMethod);
    strat->setControlUnits (CtiCCSubstationBus::PF_BY_KVARControlUnits);
    strat->setPeakLag(80);
    strat->setPeakLead(80);
    strat->setOffPeakLag(80);
    strat->setOffPeakLead(80);
    strat->setPeakPFSetPoint(100);
    strat->setOffPeakPFSetPoint(100);
    strat->setMaxConfirmTime(60);
    strat->setMinConfirmPercent(75);
    strat->setFailurePercent(25);
    strat->setControlSendRetries(0);
    

    CtiCCSubstationBus *bus1 = create_object<CtiCCSubstationBus>(3, "SubBus-A1");
    CtiCCFeeder *feed11 = create_object<CtiCCFeeder>(11, "Feeder11");
    CtiCCFeeder *feed12 = create_object<CtiCCFeeder>(12, "Feeder12");
    CtiCCFeeder *feed13 = create_object<CtiCCFeeder>(13, "Feeder13");
    CtiCCCapBank *cap11a = create_object<CtiCCCapBank>(14, "capBank11a");
    CtiCCCapBank *cap11b = create_object<CtiCCCapBank>(15, "capBank11b");
    CtiCCCapBank *cap11c = create_object<CtiCCCapBank>(16, "capBank11c");
    
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance(false);
    initialize_area(store, area);
    initialize_station(store, station, area);
    initialize_bus(store, bus1, station);

    initialize_feeder(store, feed11, bus1, 1);
    initialize_feeder(store, feed12, bus1, 2);
    initialize_feeder(store, feed13, bus1, 3);


    initialize_capbank(store, cap11a, feed11, 1);
    initialize_capbank(store, cap11b, feed11, 2);
    initialize_capbank(store, cap11c, feed11, 3);

    bus1->setVerificationDisableOvUvFlag(false);
  
    feed11->setUsePhaseData(false);
    cap11a->setControlStatus(CtiCCCapBank::Open); 
    cap11b->setControlStatus(CtiCCCapBank::OpenQuestionable); 
    cap11c->setControlStatus(CtiCCCapBank::OpenFail); 
   
    bus1->setStrategyId(strat->getStrategyId());
    bus1->setStrategyValues(strat);
    feed11->setCurrentVarLoadPointValue(700, currentDateTime);
    feed11->setCurrentWattLoadPointValue(1200);

    CtiCCExecutorFactory f;
    CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationVerificationMsg(CtiCCSubstationVerificationMsg::ENABLE_SUBSTATION_BUS_VERIFICATION, bus1->getPAOId(), CtiPAOScheduleManager::AllBanks, 0, false));
    executor->Execute();
    
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

    
    currentDateTime = currentDateTime + bus1->getMaxConfirmTime() + 1;

    //should go to CloseFail, then OpenPending again.
    bus1->analyzeVerificationByFeeder(currentDateTime, pointChanges, ccEvents, pilMessages, capMessages);

    BOOST_CHECK_EQUAL(cap11c->getControlStatus(), CtiCCCapBank::OpenPending);

    currentDateTime = currentDateTime + 1;
    feed11->setCurrentVarLoadPointValue(1200, currentDateTime);

    bus1->analyzeVerificationByFeeder(currentDateTime, pointChanges, ccEvents, pilMessages, capMessages);

    BOOST_CHECK_EQUAL(cap11c->getControlStatus(), CtiCCCapBank::ClosePending);

    currentDateTime = currentDateTime + 1;
    feed11->setCurrentVarLoadPointValue(1, currentDateTime);

    bus1->analyzeVerificationByFeeder(currentDateTime, pointChanges, ccEvents, pilMessages, capMessages);

    BOOST_CHECK_EQUAL(cap11c->getControlStatus(), CtiCCCapBank::Close);


    BOOST_CHECK_EQUAL(  bus1->getPerformingVerificationFlag(), FALSE);
    BOOST_CHECK_EQUAL(feed11->getPerformingVerificationFlag(), FALSE);
    BOOST_CHECK_EQUAL(cap11a->getPerformingVerificationFlag(), FALSE);
    BOOST_CHECK_EQUAL(  bus1->getVerificationFlag(), FALSE);
    BOOST_CHECK_EQUAL(feed11->getVerificationFlag(), FALSE);
    BOOST_CHECK_EQUAL(cap11a->getVerificationFlag(), FALSE);
}
