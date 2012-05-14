#pragma once

#include <rw/thr/thrfunc.h>
#include <rw/thr/runfunc.h>
#include <rw/thr/srvpool.h>
#include <rw/thr/thrutil.h>
#include <rw/thr/countptr.h>
#include <rw/collect.h>

#include "dbaccess.h"
#include "connection.h"
#include "message.h"
#include "msg_multi.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "pointtypes.h"
#include "loadmanager.h"
#include "lmcontrolareastore.h"
#include "executor.h"
#include "ctibase.h"
#include "logger.h"
#include "yukon.h"
#include "ctdpcptrq.h"
#include "queue.h"

class CtiLMControlArea;
class CtiLMExecutor;

class CtiLoadManager
{
public:

    static CtiLoadManager* getInstance();

    void start();
    void stop();

    void sendMessageToDispatch(CtiMessage* message);
    void sendMessageToPIL(CtiMessage* message);
    void sendMessageToNotification(CtiMessage* message);
    void sendMessageToClients(CtiMessage* message);

private:

    CtiLoadManager();
    virtual ~CtiLoadManager();

    void controlLoop();

    CtiConnection* getPILConnection();
    CtiConnection* getDispatchConnection();
    CtiConnection* getNotificationConnection();

    void checkDispatch(CtiTime currentTime);
    void checkPIL(CtiTime currentTime);
    void registerForPoints(const std::vector<CtiLMControlArea*>& controlAreas);
    void parseMessage( RWCollectable *message, CtiTime currentTime );
    void pointDataMsg( long pointID, double value, unsigned quality, unsigned tags, CtiTime& timestamp, CtiTime currentTime );
    void signalMsg( long pointID, unsigned tags, std::string text, std::string additional );

    void loadControlLoopCParms();

    static CtiLoadManager* _instance;
    RWThread _loadManagerThread;

    CtiConnection* _pilConnection;
    CtiConnection* _dispatchConnection;
    CtiConnection* _notificationConnection;

    int control_loop_delay;
    int control_loop_inmsg_delay;
    int control_loop_outmsg_delay;

    mutable RWRecursiveLock<RWMutexLock> _mutex;
};
