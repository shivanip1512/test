#pragma once

#include "connection_server.h"
#include "lmmessage.h"

#include <boost/thread.hpp>
#include <memory>

typedef std::vector<std::shared_ptr<CtiServerConnection>> CtiLMConnectionVec;

class CtiLMClientListener
{
public:

    CtiLMClientListener();
    virtual ~CtiLMClientListener();

    virtual void start();
    virtual void stop();

    void BroadcastMessage(CtiMessage* msg);
    void sendMessageToClient(std::unique_ptr<CtiMessage> msg);

    static CtiLMClientListener& getInstance();

    CtiMessage* getQueue(unsigned time);

private:

    boost::thread   _listenerthr;
    boost::thread   _checkthr;

    CtiConnection::Que_t _incomingQueue;

    CtiLMConnectionVec _connections;
    CtiCriticalSection _connmutex;

    static CtiLMClientListener _instance;

    bool _started;
    bool _doquit;

    void _listen( );
    void _check( );

    void removeAllConnections();
};
