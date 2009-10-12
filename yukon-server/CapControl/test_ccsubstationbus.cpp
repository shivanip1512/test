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

#include "ccUnitTestUtil.h"

using boost::unit_test_framework::test_suite;
using namespace std;

void initialize_area(CtiCCSubstationBusStore* store, CtiCCArea* area)
{
    store->addAreaToPaoMap(area);
}
void initialize_station(CtiCCSubstationBusStore* store, CtiCCSubstation* station, CtiCCArea* parentArea)
{
    station->setSaEnabledFlag(FALSE);
    station->setParentId(parentArea->getPAOId());
    parentArea->getSubStationList()->push_back(station->getPAOId());
    store->addSubstationToPaoMap(station);


}
void initialize_bus(CtiCCSubstationBusStore* store, CtiCCSubstationBus* bus, CtiCCSubstation* parentStation)
{
    bus->setParentId(parentStation->getPAOId());
    bus->setEventSequence(22);
    bus->setCurrentVarLoadPointValue(55, CtiTime());
    bus->setVerificationFlag(FALSE);
    parentStation->getCCSubIds()->push_back(bus->getPAOId());
    store->addSubBusToPaoMap(bus);
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
