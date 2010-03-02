#pragma once

#include "ccsubstationbusstore.h"
#include "capcontroller.h"
#include "PointDataRequest.h"
#include "PointDataRequestFactory.h"
#include "pointdefs.h"

class Test_CtiCCSubstationBusStore : public CtiCCSubstationBusStore
{
    public:

        virtual bool UpdateBusDisableFlagInDB(CtiCCSubstationBus* bus){return true;};
        virtual bool UpdateCapBankDisableFlagInDB(CtiCCCapBank* capbank){return true;};

        void insertAreaToPaoMap(CtiCCAreaPtr area){addAreaToPaoMap(area);};
        void insertSubstationToPaoMap(CtiCCSubstationPtr station){addSubstationToPaoMap(station);};
        void insertSubBusToPaoMap(CtiCCSubstationBusPtr bus){addSubBusToPaoMap(bus);};
        void insertFeederToPaoMap(CtiCCFeederPtr feeder){addFeederToPaoMap(feeder);};
        void insertLtcToPaoMap(LoadTapChangerPtr ltc){addLtcToPaoMap(ltc);};
};

class Test_CtiCapController : public CtiCapController
{
    public:
        virtual void sendMessageToDispatch(CtiMessage *message){delete message; return;};
};

template <class T>
T *create_object(long objectid, string name)
{
    T *object = new T();

    object->setPaoId(objectid);
    object->setPaoName(name);
    return object;
}


// mock dispatch connection to test the point data request functionality
class mock_DispatchConnection : public DispatchConnection
{
public:

    mock_DispatchConnection() : DispatchConnection("mock_DispatchConnection", -1, "localhost") {  }
};

