#pragma once

#include "connection.h"
#include "streamLocalConnection.h"
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
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    SystemMsgThread(const SystemMsgThread&);
    SystemMsgThread& operator=(const SystemMsgThread&);

    CtiDeviceManager     &_devManager;
    CtiPortManager       &_portManager;
    StreamLocalConnection<OUTMESS, INMESS> &_pilToPorter;

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
                    StreamLocalConnection<OUTMESS, INMESS> &pilToPorter);

    void push(CtiRequestMsg *e);
    void run();
};

}
}

