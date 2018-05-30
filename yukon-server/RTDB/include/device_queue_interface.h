#pragma once

#include "dlldefs.h"

class CtiOutMessage;
using OUTMESS = CtiOutMessage;

namespace Cti {

class IM_EX_DEVDB DeviceQueueInterface
{
public:

    using OutMessagePtrVector = std::vector<OUTMESS*>;

protected:
    virtual ~DeviceQueueInterface() {};

    static void copyOutMessagesToVector(void *listPtr, void* data)
    {
        reinterpret_cast<OutMessagePtrVector *>(listPtr)->push_back(reinterpret_cast<OUTMESS*>(data));
    }

public:

    virtual unsigned long getRequestCount(unsigned long requestID) const = 0;
    virtual OutMessagePtrVector retrieveQueueEntries( bool (*myFindFunc)(void*, void*) , void *findParameter) = 0;
};

}

