#pragma once

#include "device_queue_interface.h"
#include "prot_klondike.h"

namespace Cti {

class IM_EX_DEVDB CCU721DeviceQueueInterface : public DeviceQueueInterface
{
    Protocol::Klondike &_klondike;

public:
    CCU721DeviceQueueInterface(Protocol::Klondike &klondike);
    virtual ~CCU721DeviceQueueInterface() {};

    unsigned long getRequestCount(unsigned long requestID);
    void retrieveQueueEntries( bool (*myFindFunc)(void*, void*) , void *findParameter, std::list<void*>& entries);
};

}

