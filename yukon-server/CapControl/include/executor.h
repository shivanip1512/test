/*-----------------------------------------------------------------------------
    Filename:  executor.h
    
    Programmer:  Josh Wolberg
    
    Description:    Header file for the various Cap Control executor classes.
                            
    Initial Date:  8/16/2000
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef EXECUTOR_H
    #define EXECUTOR_H

    #include <rw/thr/countptr.h> 
    #include <rw/thr/thrfunc.h>
    #include <rw/thr/barrier.h>  
    
    #include "ccmessage.h"
    #include "ctdpcptrq.h"
    #include "strategylist.h"

class CtiCCExecutor
{
public:
    virtual ~CtiCCExecutor() {};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results) {};

protected:
    CtiCCExecutor() {};
};

class CtiCCStrategyListMsgExecutor : public CtiCCExecutor
{
public:
    CtiCCStrategyListMsgExecutor(CtiCCStrategyListMsg* stratMsg) : _stratListMsg(stratMsg){};
    virtual ~CtiCCStrategyListMsgExecutor(){delete _stratListMsg;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:

    CtiCCStrategyListMsg* _stratListMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};

class CtiCCStateListMsgExecutor : public CtiCCExecutor
{
public:
    CtiCCStateListMsgExecutor(CtiCCStateListMsg* stateMsg) : _stateListMsg(stateMsg){};
    virtual ~CtiCCStateListMsgExecutor(){delete _stateListMsg;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:

    CtiCCStateListMsg* _stateListMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};

class CtiCCAreaListMsgExecutor : public CtiCCExecutor
{
public:
    CtiCCAreaListMsgExecutor(CtiCCAreaListMsg* areaMsg) : _areaListMsg(areaMsg){};
    virtual ~CtiCCAreaListMsgExecutor(){delete _areaListMsg;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:

    CtiCCAreaListMsg* _areaListMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};

class CtiCCCommandExecutor : public CtiCCExecutor
{
public:
    CtiCCCommandExecutor(CtiCCCommand* command) : _command(command) {};
    virtual ~CtiCCCommandExecutor() { delete _command;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:
    void EnableStrategy(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void DisableStrategy(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void EnableCapBank(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void DisableCapBank(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void OpenCapBank(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void CloseCapBank(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void ConfirmOpen(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void ConfirmClose(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

    CtiCCCommand* _command;
    RWRecursiveLock<RWMutexLock> _mutex;
};

class CtiCCPointDataMsgExecutor : public CtiCCExecutor
{
public:
    CtiCCPointDataMsgExecutor(CtiPointDataMsg* pointMsg) : _pointDataMsg(pointMsg) {};
    virtual ~CtiCCPointDataMsgExecutor() { delete _pointDataMsg;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:
    CtiPointDataMsg* _pointDataMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};

class CtiCCForwardMsgToDispatchExecutor : public CtiCCExecutor
{
public:
    CtiCCForwardMsgToDispatchExecutor(CtiMessage* ctiMsg) : _ctiMessage(ctiMsg) {};
    virtual ~CtiCCForwardMsgToDispatchExecutor() { delete _ctiMessage;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:
    CtiMessage* _ctiMessage;
    RWRecursiveLock<RWMutexLock> _mutex;
};

class CtiCCShutdownExecutor : public CtiCCExecutor
{
public:
    CtiCCShutdownExecutor() {};
    virtual ~CtiCCShutdownExecutor() {};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
};

class CtiCCExecutorFactory
{
public:
    CtiCCExecutor* createExecutor(const CtiMessage* message);

};
#endif
