#include "precompiled.h"

#include "CapControlDispatchConnection.h"
#include "ccsubstationbusstore.h"

CapControlDispatchConnection::CapControlDispatchConnection( const std::string& connectionName, Que_t *inQ, int tt) :
    DispatchConnection( connectionName, inQ, tt)
{
}

void CapControlDispatchConnection::writeIncomingMessageToQueue(CtiMessage *msgPtr)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    DispatchConnection::writeIncomingMessageToQueue(msgPtr);
}
