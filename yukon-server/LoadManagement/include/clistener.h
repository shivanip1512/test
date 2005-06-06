/*-----------------------------------------------------------------------------
    Filename:  clistener.h
                    
    Programmer:  Josh Wolberg
    
    Description: Header file for CtiLMClientListener
        
    Initial Date:  2/5/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMCLIENTLISTENER_H
#define CTILMCLIENTLISTENER_H

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

#endif

