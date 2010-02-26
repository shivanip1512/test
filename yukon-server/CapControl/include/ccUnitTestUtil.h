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
        }
        virtual PointValueMap getPointValues()
        {
            return isComplete() ? _map : PointValueMap();   // return an empty map unless we are complete
        }

        virtual bool isComplete()
        {
            if ( _pointList.empty() )
            {
                return false;
            }

            bool isComplete = true;

            for (std::list<long>::const_iterator b = _pointList.begin(), e = _pointList.end(); isComplete && (b != e); ++b)
            {
                if ( _map.find(*b) == _map.end() )  // if an element in the watchlist lacks a match in the map
                {                                   //  we are NOT complete
                    isComplete = false;
                }
            }

            return isComplete;
        }

        virtual bool watchPoints(std::list<long> points)
        {
            if ( _pointList.empty() )
            {
                _pointList = points;
            }

            return ( ! _pointList.empty() );
        }

        void receivePointUpdate(const long ID, const PointValue & point)
        {
            if ( _pointList.end() != std::find(_pointList.begin(), _pointList.end(), ID) )
            {
                _map[ID] = point;
            }
        }

    private:
        PointValueMap _map;
        std::list<long> _pointList;
};

class test_PointDataRequestFactory : public PointDataRequestFactory
{
    public:
        test_PointDataRequestFactory()
        {

        }

        virtual PointDataRequestPtr createDispatchPointDataRequest(DispatchConnectionPtr conn)
        {
            PointDataRequestPtr pRequest(new test_DispatchPointDataRequest);
            return pRequest;
        }
};
