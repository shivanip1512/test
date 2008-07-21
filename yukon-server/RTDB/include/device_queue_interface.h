
/*-----------------------------------------------------------------------------*
*
* File:   device_queue_interface
*
* Date:   2/19/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/include/device_queue_interface.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2008/07/21 20:38:26 $
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEVICE_QUEUE_INTERFACE_H__
#define __DEVICE_QUEUE_INTERFACE_H__
#pragma warning( disable : 4786)

#include "yukon.h"
#include "trx_711.h"
#include <list>

namespace Cti {

class IM_EX_DEVDB DeviceQueueInterface
{
private:
    CtiTransmitter711Info *_p711Info;
    static void copyMessagesToList(void *listPtr, void* data);
public:

    DeviceQueueInterface(CtiTransmitter711Info *pInfo);
    DeviceQueueInterface();
    ~DeviceQueueInterface();

    void getQueueRequestInfo(ULONG requestID, ULONG &count, ULONG &priority);
    void cancelRequest(ULONG requestID, ULONG &count);
    void set711Info(CtiTransmitter711Info *pInfo);
    void retrieveQueueEntries(bool (*myFindFunc)(void*, PQUEUEENT), void *findParameter, list<void*>& entries);

};

}//Cti Namespace

#endif 
