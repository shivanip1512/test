#pragma once

#include "ccsubstationbusstore.h"
#include "capcontroller.h"
#include "PointDataRequest.h"
#include "PointDataRequestFactory.h"
#include "pointdefs.h"

class Test_CtiCCSubstationBusStore : public CtiCCSubstationBusStore
{
    public:

        virtual bool UpdatePaoDisableFlagInDB(CapControlPao* pao, bool disableFlag){pao->setDisableFlag(disableFlag); return true;};

        void insertAreaToPaoMap(CtiCCAreaPtr area){addAreaToPaoMap(area);};
        void insertSubstationToPaoMap(CtiCCSubstationPtr station){addSubstationToPaoMap(station);};
        void insertSubBusToPaoMap(CtiCCSubstationBusPtr bus){addSubBusToPaoMap(bus);};
        void insertFeederToPaoMap(CtiCCFeederPtr feeder){addFeederToPaoMap(feeder);};
        void insertSubBusToAltBusMap(CtiCCSubstationBusPtr bus){addSubBusToAltBusMap(bus);};

};

class Test_CtiCapController : public CtiCapController
{
    public:
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
