#pragma once

#include "ccclientconn.h"

#include <boost/thread.hpp>

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

private:

    boost::thread   _listenerThr;
    boost::thread   _checkThr;

    CtiCCConnectionVec _connections;
    CtiCriticalSection _connmutex;

    static CtiCCClientListener _instance;

    bool _started;
    bool _doquit;

    void _listen( );
    void _check( );

    void removeAllConnections();
};
