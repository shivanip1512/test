
/*-----------------------------------------------------------------------------*
*
* File:   device_queue_interface
*
* Date:   2/19/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/include/device_queue_interface.h-arc  $
* REVISION     :  $Revision: 1.2.4.1 $
* DATE         :  $Date: 2008/11/20 16:49:27 $
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEVICE_QUEUE_INTERFACE_H__
#define __DEVICE_QUEUE_INTERFACE_H__
#pragma warning( disable : 4786)

#include "dlldefs.h"
#include "queues.h"
#include <list>

namespace Cti {

class IM_EX_DEVDB DeviceQueueInterface
{
protected:
    DeviceQueueInterface() {};

    static void copyMessagesToList(void *listPtr, void* data)
    {
        reinterpret_cast<std::list<void*> *>(listPtr)->push_back(data);
    }

public:
    virtual ~DeviceQueueInterface() {};

    virtual void getQueueRequestInfo(ULONG requestID, ULONG &count, ULONG &priority) = 0;
    virtual void retrieveQueueEntries( bool (*myFindFunc)(void*, void*) , void *findParameter, std::list<void*>& entries) = 0;
};

}//Cti Namespace

#endif
