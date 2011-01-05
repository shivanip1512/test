#pragma once

#include "dlldefs.h"
#include <list>

namespace Cti {

class IM_EX_DEVDB DeviceQueueInterface
{
protected:
    virtual ~DeviceQueueInterface() {};

    static void copyMessagesToList(void *listPtr, void* data)
    {
        reinterpret_cast<std::list<void*> *>(listPtr)->push_back(data);
    }

public:

    virtual unsigned long getRequestCount(unsigned long requestID) = 0;
    virtual void retrieveQueueEntries( bool (*myFindFunc)(void*, void*) , void *findParameter, std::list<void*>& entries) = 0;
};

}

