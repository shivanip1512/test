/*-----------------------------------------------------------------------------
    Filename:  ccserver.h

    Programmer:  Josh Wolberg

    Description: Header file for CtiCCServer

    Initial Date:  9/04/2001

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTICCSERVER_H
#define CTICCSERVER_H
                                
#include <rw/thr/recursiv.h>
#include <rw/thr/mutex.h>

#include "ccclientlistener.h"
#include "observe.h"
#include "ccmessage.h"

class CtiCCServer : public CtiObservable
{
public:
    
    void start();
    void stop();
    
    void Broadcast(CtiMessage* message);
    CtiMessage* getBroadcastMessage();

    bool isRunning() const { return _running; };

    static CtiCCServer* getInstance();

private:
    CtiCCServer() : _clientListener(0), _running(FALSE), _dostop(FALSE), _currentmessage(NULL) { };
    virtual ~CtiCCServer() { };

    CtiCCClientListener* _clientListener;

    bool _running;
    bool _dostop;
    
    RWRecursiveLock<RWMutexLock> _broadcastmutex;
    CtiMessage* _currentmessage;

    RWThread _checkthr;

    void _checkstatus();

    static CtiCCServer* _instance;
    static int _capcontrolclientsport;
};

#endif
