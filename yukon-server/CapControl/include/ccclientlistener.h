#pragma once

#include <rw/toolpro/sockaddr.h>
#include <rw/toolpro/sockport.h>
#include <rw/thr/thread.h>

#include <vector>

#include "ccclientconn.h"
#include "ccstate.h"

typedef boost::ptr_vector<CtiCCClientConnection> CtiCCConnectionVec;

class CtiCCClientListener
{
public:

    CtiCCClientListener();
    virtual ~CtiCCClientListener();

    virtual void start();
    virtual void stop();

    void BroadcastMessage(CtiMessage* msg);

    static CtiCCClientListener& getInstance();

    RWRecursiveLock<RWMutexLock> & getMux() { return _connmutex; };

private:

    CtiListenerConnection _listenerConnection;

    RWThread _listenerthr;
    RWThread _checkthr;

    CtiCCConnectionVec _connections;
    RWRecursiveLock<RWMutexLock> _connmutex;

    static CtiCCClientListener _instance;

    bool _doquit;

    void _listen( );
    void _check( );

    void removeAllConnections();
};
