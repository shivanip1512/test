#pragma once

#include "connection.h"
#include "ctilocalconnect.h"
#include "queue.h"
#include "mgr_device.h"
#include "mgr_port.h"
#include "thread.h"

class CtiCommandParser;
class CtiRequestMsg;
class CtiDeviceManager;
class CtiPortManager;

namespace Cti {
namespace Porter {

class SystemMsgThread : public CtiThread
{
    CtiDeviceManager     &_devManager;
    CtiPortManager       &_portManager;
    CtiLocalConnect<OUTMESS, INMESS> &_pilToPorter;

    //  the input queue
    CtiFIFOQueue< CtiMessage > &_input;

    void executeSystemMessage(CtiRequestMsg *msg);
    void executePortEntryRequest(CtiRequestMsg *msg, CtiCommandParser &parse);
    void executeRequestCount(CtiRequestMsg *msg, CtiCommandParser &parse);
    void executeCancelRequest(CtiRequestMsg *msg, CtiCommandParser &parse);

public:

    SystemMsgThread(CtiFIFOQueue< CtiMessage > &inputQueue,
                    CtiDeviceManager &devMgr,
                    CtiPortManager &portMgr,
                    CtiLocalConnect<OUTMESS, INMESS> &pilToPorter);

    void push(CtiRequestMsg *e);
    void run();
};

}
}

