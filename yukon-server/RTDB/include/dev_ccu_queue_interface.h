#pragma once

#include "device_queue_interface.h"
#include "trx_711.h"

namespace Cti {

class IM_EX_DEVDB CCU711DeviceQueueInterface : public DeviceQueueInterface
{
    CtiTransmitter711Info *_p711Info;

public:

    CCU711DeviceQueueInterface();

    void set711Info(CtiTransmitter711Info *pInfo);

    unsigned long getRequestCount(unsigned long requestID) const override;
    OutMessagePtrVector retrieveQueueEntries( bool (*myFindFunc)(void*, void*) , void *findParameter) override;
};

}

