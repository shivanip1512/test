#pragma once
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <rw/toolpro/sockaddr.h>
#include <rw/toolpro/sockport.h>
#include <rw/thr/thread.h>

#include <vector>

#include "ccclientconn.h"
#include "ccstate.h"

class CtiCCClientListener
{
public:
    CtiCCClientListener(LONG port);
    virtual ~CtiCCClientListener();

    virtual void start();
    virtual void stop();

    void BroadcastMessage(CtiMessage* msg);

    static CtiCCClientListener* getInstance();

    RWRecursiveLock<RWMutexLock> & getMux() { return _connmutex; };

    std::vector<CtiCCClientConnection*>& getClientConnectionList();

protected:

private:

    LONG _port;
    RWThread _listenerthr;
    RWThread _checkthr;

    std::vector<CtiCCClientConnection*> _connections;
    RWRecursiveLock<RWMutexLock> _connmutex;
    RWSocketListener* _socketListener;

    static CtiCCClientListener* _instance;

    bool _doquit;

    void _listen( );
    void _check( );
};
