
/*-----------------------------------------------------------------------------*
*
* File:   device_queue_interface
*
* Date:   2/19/2007
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/include/device_queue_interface.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2007/02/22 17:49:48 $
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEVICE_QUEUE_INTERFACE_H__
#define __DEVICE_QUEUE_INTERFACE_H__
#pragma warning( disable : 4786)

#include "yukon.h"
#include "trx_711.h"

namespace Cti {

class IM_EX_DEVDB DeviceQueueInterface
{
private:
    CtiTransmitter711Info *_p711Info;
public:

    DeviceQueueInterface(CtiTransmitter711Info *pInfo);
    DeviceQueueInterface();
    ~DeviceQueueInterface();

    void getQueueRequestInfo(ULONG requestID, ULONG &count, ULONG &priority);
    void cancelRequest(ULONG requestID, ULONG &count);
    void set711Info(CtiTransmitter711Info *pInfo);

};

}//Cti Namespace

#endif 
