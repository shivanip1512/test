#pragma once

#include "device_queue_interface.h"
#include "prot_klondike.h"

namespace Cti {

class IM_EX_DEVDB CCU721DeviceQueueInterface : public DeviceQueueInterface
{
private:
    Protocol::Klondike &_klondike;

public:
    CCU721DeviceQueueInterface(Protocol::Klondike &klondike);
    virtual ~CCU721DeviceQueueInterface() {};

    void getQueueRequestInfo(ULONG requestID, ULONG &count, ULONG &priority);
    void retrieveQueueEntries( bool (*myFindFunc)(void*, void*) , void *findParameter, std::list<void*>& entries);
};

}

