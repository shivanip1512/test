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

class test_DispatchPointDataRequest : public PointDataRequest
{
    public:
        test_DispatchPointDataRequest()
        {
            _pointsSet = false;
        }
        virtual PointValueMap getPointValues()
        {
            return _map;
        }

        virtual bool isComplete()
        {
            return true;
        }

        virtual bool watchPoints(std::list<long> points)
        {
            if (_pointsSet == false)
            {
                CtiTime time;
                for each (long pointId in points)
                {
                    PointValue pv;

                    pv.timestamp = time;
                    pv.value = 1.0;
                    pv.quality = NormalQuality;

                    _map[pointId] = pv;
                }
            }
            return true;
        }

    private:
        PointValueMap _map;
        bool _pointsSet;
};

class test_PointDataRequestFactory : public PointDataRequestFactory
{
    public:
        test_PointDataRequestFactory()
        {

        }

        virtual PointDataRequestPtr createDispatchPointDataRequest(DispatchConnectionPtr conn)
        {
            PointDataRequestPtr pRequest(new test_DispatchPointDataRequest());
            return pRequest;
        }
};
