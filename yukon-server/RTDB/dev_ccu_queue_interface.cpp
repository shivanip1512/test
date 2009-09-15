#include "yukon.h"

#include "dev_ccu_queue_interface.h"

#include "trx_711.h"


namespace Cti
{

CCU711DeviceQueueInterface::CCU711DeviceQueueInterface()
{
    _p711Info = NULL;
}

void CCU711DeviceQueueInterface::set711Info(CtiTransmitter711Info *pInfo)
{
    _p711Info = pInfo;
}

void CCU711DeviceQueueInterface::getQueueRequestInfo(ULONG requestID, ULONG &count, ULONG &priority)
{
    count = 0;
    priority = 0;
    if( requestID > 0 && _p711Info != NULL )
    {
        GetRequestCountAndPriority(_p711Info->QueueHandle, requestID, count,  priority);
    }
}

void CCU711DeviceQueueInterface::retrieveQueueEntries(bool (*myFindFunc)(void*, void*), void *findParameter, std::list<void*>& entries)
{
    if( _p711Info != NULL )
    {
        if( _p711Info->QueueHandle->Elements > 0 )
        {
            CleanQueue(_p711Info->QueueHandle, findParameter, myFindFunc, copyMessagesToList, (void *)&entries);
        }
    }
}

}
