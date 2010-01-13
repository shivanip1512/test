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

    static CtiCCClientListener* _instance;

    bool _doquit;

    void _listen( );
    void _check( );
};

#endif

