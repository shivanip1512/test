#pragma once

#include <rw/toolpro/sockaddr.h>
#include <rw/toolpro/sockport.h>
#include <rw/thr/thread.h>
#include <rw/thr/recursiv.h>

#include "connection_server.h"
#include "lmmessage.h"

typedef boost::shared_ptr<CtiServerConnection> CtiLMConnectionPtr;
typedef std::vector<CtiLMConnectionPtr> CtiLMConnectionVec;

class CtiLMClientListener
{
public:

    CtiLMClientListener();
    virtual ~CtiLMClientListener();

    virtual void start();
    virtual void stop();

    void BroadcastMessage(CtiMessage* msg);
    void sendMessageToClient(std::auto_ptr<CtiMessage> msg);

    static CtiLMClientListener& getInstance();

    CtiMessage* getQueue(unsigned time);

private:

    CtiListenerConnection _listenerConnection;

    RWThread _listenerthr;
    RWThread _checkthr;
    CtiConnection::Que_t _incomingQueue;

    CtiLMConnectionVec _connections;
    RWRecursiveLock<RWMutexLock> _connmutex;

    static CtiLMClientListener _instance;

    bool _doquit;

    void _listen( );
    void _check( );

    void removeAllConnections();
};
