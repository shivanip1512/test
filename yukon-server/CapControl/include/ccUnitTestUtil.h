#pragma once

#include "ccsubstationbusstore.h"
#include "capcontroller.h"
#include "PointDataRequest.h"
#include "PointDataRequestFactory.h"
#include "pointdefs.h"

namespace Cti {
namespace Test {
namespace CapControl {
namespace {

struct Test_CtiCCSubstationBusStore : CtiCCSubstationBusStore
{
    virtual bool UpdatePaoDisableFlagInDB(CapControlPao* pao, bool disableFlag)
        {   pao->setDisableFlag(disableFlag); return true;  }

    using CtiCCSubstationBusStore::addAreaToPaoMap;
    using CtiCCSubstationBusStore::addSubstationToPaoMap;
    using CtiCCSubstationBusStore::addSubBusToPaoMap;
    using CtiCCSubstationBusStore::addFeederToPaoMap;
    using CtiCCSubstationBusStore::addSubBusToAltBusMap;
    using CtiCCSubstationBusStore::addCapBankToCBCMap;

    using CtiCCSubstationBusStore::setAttributeService;
};

struct Test_CtiCapController : CtiCapController
{
    virtual void sendMessageToDispatch(CtiMessage *message){delete message; return;};
    void adjustAlternateSettings(long pointID, CtiCCSubstationBusPtr bus){adjustAlternateBusModeValues(pointID, 0, bus);};
};

template <class T>
T *create_object(long objectid, string name)
{
    T *object = new T();

    object->setPaoId(objectid);
    object->setPaoName(name);
    return object;
}

void initialize_area(Test_CtiCCSubstationBusStore* store, CtiCCArea* area)
{
    store->addAreaToPaoMap(area);
    area->setDisableFlag(false);
}

void initialize_station(Test_CtiCCSubstationBusStore* store, CtiCCSubstation* station, CtiCCArea* parentArea)
{
    station->setSaEnabledFlag(false);
    station->setParentId(parentArea->getPaoId());
    parentArea->getSubstationIds().push_back(station->getPaoId());
    store->addSubstationToPaoMap(station);
    station->setDisableFlag(false);
}

void initialize_bus(Test_CtiCCSubstationBusStore* store, CtiCCSubstationBus* bus, CtiCCSubstation* parentStation)
{
    bus->setParentId(parentStation->getPaoId());
    bus->setEventSequence(22);
    bus->setCurrentVarLoadPointId(1);
    bus->setCurrentVarLoadPointValue(55, CtiTime());
    bus->setVerificationFlag(false);
    parentStation->getCCSubIds().push_back(bus->getPaoId());
    store->addSubBusToPaoMap(bus);
    bus->setDisableFlag(false);
    bus->setVerificationFlag(false);
    bus->setPerformingVerificationFlag(false);
    bus->setVerificationDoneFlag(false);
}

void initialize_feeder(Test_CtiCCSubstationBusStore* store, CtiCCFeeder* feed, CtiCCSubstationBus* parentBus, long displayOrder)
{

    long feederId = feed->getPaoId();
    long busId = parentBus->getPaoId();
    feed->setParentId(busId);
    feed->setDisplayOrder(displayOrder);
    parentBus->getCCFeeders().push_back(feed);
    store->insertItemsIntoMap(CtiCCSubstationBusStore::FeederIdSubBusIdMap, &feederId, &busId);
    store->addFeederToPaoMap(feed);
    feed->setDisableFlag(false);
    feed->setVerificationFlag(false);
    feed->setPerformingVerificationFlag(false);
    feed->setVerificationDoneFlag(false);

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
    store->insertItemsIntoMap(CtiCCSubstationBusStore::PaobjectCapBankMap, &bankId, reinterpret_cast<long *>(cap));
    cap->setOperationalState(CtiCCCapBank::SwitchedOperationalState);
    cap->setDisableFlag(false);
    cap->setVerificationFlag(false);
    cap->setPerformingVerificationFlag(false);
    cap->setVerificationDoneFlag(false);
    cap->setBankSize(600);

    cap->setControlPointId(1);
}

}
}
}
}

