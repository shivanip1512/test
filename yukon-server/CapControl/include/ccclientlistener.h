/*-----------------------------------------------------------------------------
    Filename:  ccclientlistener.h
                    
    Programmer:  Josh Wolberg
    
    Description: Header file for CtiCCClientListener
        
    Initial Date:  8/16/2000
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTICCCLIENTLISTENER_H
#define CTICCCLIENTLISTENER_H

#include <rw/toolpro/sockaddr.h>
#include <rw/toolpro/sockport.h>
#include <rw/thr/thread.h>

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

    RWTPtrSlist<CtiCCClientConnection>& getClientConnectionList();


protected:

private:
    RWSocketListener* _socketListener;

    LONG _port;   
    RWThread _listenerthr;
    RWThread _checkthr;

    RWTPtrSlist<CtiCCClientConnection> _connections;
    RWRecursiveLock<RWMutexLock> _connmutex;

    static CtiCCClientListener* _instance;

    bool _doquit;

    void _listen( );
    void _check( );
};

#endif

