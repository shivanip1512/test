#pragma once

#include <rw/toolpro/sockaddr.h>
#include <rw/toolpro/sockport.h>
#include <rw/thr/thread.h>
#include <rw/thr/recursiv.h>

#include "clientconn.h"
#include "lmmessage.h"

class CtiLMClientListener
{
public:
    CtiLMClientListener(LONG port);
    virtual ~CtiLMClientListener();

    virtual void start();
    virtual void stop();

    void BroadcastMessage(CtiMessage* msg);

    static CtiLMClientListener* getInstance();

protected:

private:
    RWSocketListener* _socketListener;

    LONG _port;   
    RWThread _listenerthr;
    RWThread _checkthr;

    CtiLMConnectionVec _connections;
    RWRecursiveLock<RWMutexLock> _connmutex;

    static CtiLMClientListener* _instance;

    bool _doquit;

    void _listen( );
    void _check( );
};
