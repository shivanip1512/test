/*-----------------------------------------------------------------------------
    Filename:  lmserver.h
                
    Programmer:  Josh Wolberg
    
    Description: Header file for CtiLMServer
        
    Initial Date:  8/16/2000
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMSERVER_H
#define CTILMSERVER_H
                                
#include <rw/thr/recursiv.h>
#include <rw/thr/mutex.h>

#include "clistener.h"
#include "observe.h"
#include "lmmessage.h"

class CtiLMServer : public CtiObservable
{
public:
    
    void start();
    void stop();
    
    bool isRunning() const { return _running; };

    static CtiLMServer* getInstance();

private:
    CtiLMServer() : _listener(NULL), _running(FALSE), _dostop(FALSE), _currentmessage(NULL) { };
    virtual ~CtiLMServer() { };

    CtiLMClientListener* _listener;

    bool _running;
    bool _dostop;
    
    RWRecursiveLock<RWMutexLock> _broadcastmutex;
    CtiMessage* _currentmessage;

    RWThread _checkthr;

    void _checkstatus();

    static CtiLMServer* _instance;
    static int _defaultport;
};

#endif
