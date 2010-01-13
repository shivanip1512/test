#pragma once

#include "ccsubstationbusstore.h"

class Test_CtiCCSubstationBusStore : public CtiCCSubstationBusStore
{
    public:
        virtual bool UpdateCapBankDisableFlagInDB(CtiCCCapBank* capbank){return true;};
};


template <class T>
T *create_object(long objectid, string name)
{
    T *object = new T();

    object->setPAOId(objectid);
    object->setPAOName(name);
    return object;
}
