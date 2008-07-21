
/*-----------------------------------------------------------------------------*
*
* File:   device_queue_interface
*
* Date:   2/19/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/device_queue_interface.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2008/07/21 20:38:26 $
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "trx_711.h"
#include "device_queue_interface.h"
#include "utility.h"
#include "dlldefs.h"
#include "queues.h"

namespace Cti
{

DeviceQueueInterface::DeviceQueueInterface()
{
    _p711Info = NULL;
}

DeviceQueueInterface::DeviceQueueInterface(CtiTransmitter711Info *pInfo) :
_p711Info(pInfo)
{
}

DeviceQueueInterface::~DeviceQueueInterface()
{
}


void DeviceQueueInterface::set711Info(CtiTransmitter711Info *pInfo)
{
    _p711Info = pInfo;
}

void DeviceQueueInterface::getQueueRequestInfo(ULONG requestID, ULONG &count, ULONG &priority)
{
    count = 0;
    priority = 0;
    if( requestID > 0 && _p711Info != NULL )
    {
        GetRequestCountAndPriority(_p711Info->QueueHandle, requestID, count,  priority);
    }
}

void DeviceQueueInterface::cancelRequest(ULONG requestID, ULONG &count)
{
    count = 0;
    ULONG priority, queueCount = 0;
    if( requestID > 0 && _p711Info != NULL )
    {
        getQueueRequestInfo(requestID, queueCount, priority);

        if( queueCount > 0 )
        {
            count = CleanQueue(_p711Info->QueueHandle, (void *)requestID, findRequestIDMatch, cleanupOutMessages);
        }
    }
}

void DeviceQueueInterface::retrieveQueueEntries(bool (*myFindFunc)(void*, PQUEUEENT), void *findParameter, list<void*>& entries)
{
    if( _p711Info != NULL )
    {
        if( _p711Info->QueueHandle->Elements > 0 )
        {
            CleanQueue(_p711Info->QueueHandle, findParameter, myFindFunc, copyMessagesToList, (void *)&entries);
        }
    }
}

void DeviceQueueInterface::copyMessagesToList(void *listPtr, void* data)
{
    list<void*> *entryList = (list<void*>*)listPtr;
    
    entryList->push_back(data);
}

} //Cti Namespace
