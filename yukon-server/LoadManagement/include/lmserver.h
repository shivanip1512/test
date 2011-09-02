#pragma once
                                
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
