#include "yukon.h"

#include "dev_ccu721_queue_interface.h"

namespace Cti {

CCU721DeviceQueueInterface::CCU721DeviceQueueInterface(Protocol::Klondike &klondike) :
    _klondike(klondike)
{
}

void CCU721DeviceQueueInterface::getQueueRequestInfo(ULONG requestID, ULONG &count, ULONG &priority)
{
    count = 0;
    priority = 0;  //  priority is unused
    if( requestID > 0 )
    {
        count = _klondike.getQueueCount(requestID);
    }
}

void CCU721DeviceQueueInterface::retrieveQueueEntries(bool (*myFindFunc)(void*, void*), void *findParameter, std::list<void*>& entries)
{
    if( _klondike.hasQueuedWork() )
    {
        _klondike.retrieveQueueEntries(myFindFunc, findParameter, entries);
    }
}

}
