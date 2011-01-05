#include "yukon.h"

#include "dev_ccu721_queue_interface.h"

namespace Cti {

CCU721DeviceQueueInterface::CCU721DeviceQueueInterface(Protocol::Klondike &klondike) :
    _klondike(klondike)
{
}

unsigned long CCU721DeviceQueueInterface::getRequestCount(unsigned long requestID)
{
    if( requestID > 0 )
    {
        return _klondike.getQueueCount(requestID);
    }

    return 0;
}

void CCU721DeviceQueueInterface::retrieveQueueEntries(bool (*myFindFunc)(void*, void*), void *findParameter, std::list<void*>& entries)
{
    if( _klondike.hasQueuedWork() )
    {
        _klondike.retrieveQueueEntries(myFindFunc, findParameter, entries);
    }
}

}
