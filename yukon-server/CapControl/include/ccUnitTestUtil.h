#pragma once

#include "ccsubstationbusstore.h"
#include "capcontroller.h"

class Test_CtiCCSubstationBusStore : public CtiCCSubstationBusStore
{
    public:

        virtual bool UpdateBusDisableFlagInDB(CtiCCSubstationBus* bus){return true;};
        virtual bool UpdateCapBankDisableFlagInDB(CtiCCCapBank* capbank){return true;};
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
