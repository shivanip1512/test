/*-----------------------------------------------------------------------------*
*
* File:   SystemMsgThread.h
*
* Class:  SystemMsgThread
* Date:   1/15/2007
*
* Author: Jess Otteson
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2007/02/22 17:46:43 $
* HISTORY      :
* $Log: systemmsgthread.h,v $
* Revision 1.2  2007/02/22 17:46:43  jotteson
* Bug Id: 814, 651
* Completed integration of MACS with new system messages. QueueWrites were changed to be sure they put the proper ID into the queues. New messaging used, new device interface used.
*
* Revision 1.1  2007/01/22 21:40:08  jotteson
* Initial Revision. Thread in porter that executes system messages and returns results to requestor.
*
*
* Copyright (c) 2007 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __SYSTEMMSGTHREAD_H__
#define __SYSTEMMSGTHREAD_H__

#include "connection.h"
#include "queue.h"
#include "mgr_device.h"
#include "mgr_port.h"
#include "thread.h"

class CtiCommandParser;
class CtiRequestMsg;

namespace Cti {
namespace Porter {

class SystemMsgThread : public CtiThread
{
public:

private:
    RWThreadFunction  _dbThread;

    CtiDeviceManager     *_pDevManager;
    CtiPortManager       *_pPortManager;

    //  the input queue
    CtiFIFOQueue< CtiMessage > *_input;

    void executeSystemMessage(CtiRequestMsg *msg);
    void executePortEntryRequest(CtiRequestMsg *msg, CtiCommandParser &parse);
    void executeRequestCount(CtiRequestMsg *msg, CtiCommandParser &parse);
    void executeCancelRequest(CtiRequestMsg *msg, CtiCommandParser &parse);
    void getPorts(vector<CtiPortManager::ptr_type> &ports);

protected:

public:

    SystemMsgThread(CtiFIFOQueue< CtiMessage > *inputQueue);
    virtual ~SystemMsgThread();

    void push(CtiRequestMsg *e);
    void run();

    void setDeviceManager(CtiDeviceManager *devMgr);
    void setPortManager(CtiPortManager *portMgr);
};

}
}
#endif // #ifndef __PILSYSTEMMSGTHREAD_H__
