#pragma once

#include <rw/toolpro/sockaddr.h>
#include <rw/toolpro/sockport.h>
#include <rw/thr/thread.h>
#include <rw/thr/recursiv.h>

#include "connection.h"
#include "lmmessage.h"

typedef boost::shared_ptr<CtiConnection> CtiLMConnectionPtr;
typedef std::vector<CtiLMConnectionPtr> CtiLMConnectionVec;
typedef CtiLMConnectionVec::iterator CtiLMConnectionIter;

class CtiLMClientListener
{
public:
    CtiLMClientListener(LONG port);
    virtual ~CtiLMClientListener();

    virtual void start();
    virtual void stop();

    void BroadcastMessage(CtiMessage* msg);
    void sendMessageToClient(CtiMessage *msg);

    static CtiLMClientListener* getInstance();

    CtiMessage* getQueue(unsigned time);

protected:

private:
    LONG _port;   
    RWThread _listenerthr;
    RWThread _checkthr;
    CtiConnection::Que_t _incomingQueue;

    CtiLMConnectionVec _connections;
    RWRecursiveLock<RWMutexLock> _connmutex;

    static CtiLMClientListener* _instance;

    bool _doquit;

    void _listen( );
    void _check( );
};
