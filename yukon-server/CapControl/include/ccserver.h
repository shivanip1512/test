#pragma once
                                
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
