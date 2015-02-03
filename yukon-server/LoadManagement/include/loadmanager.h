#pragma once

#include "dbaccess.h"
#include "connection_client.h"
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
#include "queue.h"

#include <boost/thread.hpp>

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

    boost::shared_ptr<CtiClientConnection> getPorterConnection();
    boost::shared_ptr<CtiClientConnection> getDispatchConnection();
    boost::shared_ptr<CtiClientConnection> getNotificationConnection();

    void checkDispatch(CtiTime currentTime);
    void checkPorter(CtiTime currentTime);
    void registerForPoints(const std::vector<CtiLMControlArea*>& controlAreas);
    void parseMessage( CtiMessage *message, CtiTime currentTime );
    void pointDataMsg( long pointID, double value, unsigned quality, unsigned tags, CtiTime& timestamp, CtiTime currentTime );
    void signalMsg( long pointID, unsigned tags, std::string text, std::string additional );

    void loadControlLoopCParms();

    static CtiLoadManager* _instance;

    boost::thread   _loadManagerThread;

    boost::shared_ptr<CtiClientConnection> _porterConnection;
    boost::shared_ptr<CtiClientConnection> _dispatchConnection;
    boost::shared_ptr<CtiClientConnection> _notificationConnection;

    int control_loop_delay;
    int control_loop_inmsg_delay;
    int control_loop_outmsg_delay;

    mutable CtiCriticalSection _mutex;
};
