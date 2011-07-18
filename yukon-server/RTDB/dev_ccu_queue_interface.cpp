#include "precompiled.h"

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

unsigned long CCU711DeviceQueueInterface::getRequestCount(unsigned long requestID) const
{
    unsigned long count = 0;

    if( requestID && _p711Info )
    {
        GetRequestCount(_p711Info->QueueHandle, requestID, count);
    }

    return count;
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
