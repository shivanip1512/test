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
#include "observe.h"

class CtiLMClientListener : public CtiObservable, public CtiObserver
{
public:
    CtiLMClientListener(UINT port);
    virtual ~CtiLMClientListener();

    virtual void start();
    virtual void stop();

    //Inherited from CtiObserver
    void update(CtiObservable& observable);

protected:

private:
    RWSocketListener* _listener;

    UINT _port;   
    RWThread _listenerthr;
    RWThread _checkthr;

    RWTPtrSlist<CtiLMConnection> _connections;
    RWRecursiveLock<RWMutexLock> _connmutex;

    bool _doquit;

    void _listen( );
    void _check( );
};

#endif

