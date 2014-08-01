#pragma once

#include "device_queue_interface.h"
#include "trx_711.h"

namespace Cti {

class IM_EX_DEVDB CCU711DeviceQueueInterface : public DeviceQueueInterface
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CCU711DeviceQueueInterface(const CCU711DeviceQueueInterface&);
    CCU711DeviceQueueInterface& operator=(const CCU711DeviceQueueInterface&);

    CtiTransmitter711Info *_p711Info;

public:

    CCU711DeviceQueueInterface();
    virtual ~CCU711DeviceQueueInterface() {};

    void set711Info(CtiTransmitter711Info *pInfo);

    unsigned long getRequestCount(unsigned long requestID) const;
    void retrieveQueueEntries( bool (*myFindFunc)(void*, void*) , void *findParameter, std::list<void*>& entries);
};

}

