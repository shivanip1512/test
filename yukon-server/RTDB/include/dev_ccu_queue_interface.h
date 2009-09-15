#pragma once

#include "device_queue_interface.h"
#include "trx_711.h"

namespace Cti {

class IM_EX_DEVDB CCU711DeviceQueueInterface : public DeviceQueueInterface
{
private:
    CtiTransmitter711Info *_p711Info;

public:
    CCU711DeviceQueueInterface();
    virtual ~CCU711DeviceQueueInterface() {};

    void set711Info(CtiTransmitter711Info *pInfo);

    void getQueueRequestInfo(ULONG requestID, ULONG &count, ULONG &priority);
    void retrieveQueueEntries( bool (*myFindFunc)(void*, void*) , void *findParameter, std::list<void*>& entries);
};

}

