/*-----------------------------------------------------------------------------
    Filename:  executor.h
    
    Programmer:  Josh Wolberg
    
    Description:    Header file for the various Load Management executor classes.
                            
    Initial Date:  2/13/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef EXECUTOR_H
#define EXECUTOR_H

#include <rw/thr/countptr.h> 
#include <rw/thr/thrfunc.h>
#include <rw/thr/barrier.h>  

#include "lmmessage.h"
#include "ctdpcptrq.h"

class CtiLMExecutor
{
public:
    virtual ~CtiLMExecutor() {};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results) {};

protected:
    CtiLMExecutor() {};
};


class CtiLMCommandExecutor : public CtiLMExecutor
{
public:
    CtiLMCommandExecutor(CtiLMCommand* command) : _command(command) {};
    virtual ~CtiLMCommandExecutor() { delete _command;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:
    void ChangeThreshold(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void ChangeRestoreOffset(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void ChangeCurrentOperationalState(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void EnableControlArea(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void DisableControlArea(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void EnableProgram(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void DisableProgram(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void SendAllControlAreas(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void ChangeDailyStartTime(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void ChangeDailyStopTime(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

    CtiLMCommand* _command;
    RWRecursiveLock<RWMutexLock> _mutex;
};


class CtiLMManualControlMsgExecutor : public CtiLMExecutor
{
public:
    CtiLMManualControlMsgExecutor(CtiLMManualControlMsg* controlMsg) : _controlMsg(controlMsg) {};
    virtual ~CtiLMManualControlMsgExecutor() { delete _controlMsg;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:
    void ScheduledStart(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void ScheduledStop(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void StartNow(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void StopNow(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

    CtiLMManualControlMsg* _controlMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};


class CtiLMEnergyExchangeControlMsgExecutor : public CtiLMExecutor
{
public:
    CtiLMEnergyExchangeControlMsgExecutor(CtiLMEnergyExchangeControlMsg* energyExchangeMsg) : _energyExchangeMsg(energyExchangeMsg) {};
    virtual ~CtiLMEnergyExchangeControlMsgExecutor() { delete _energyExchangeMsg;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:
    void NewOffer(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void OfferUpdate(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void OfferRevision(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void CloseOffer(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void CancelOffer(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

    CtiLMEnergyExchangeControlMsg* _energyExchangeMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};


class CtiLMControlAreaMsgExecutor : public CtiLMExecutor
{
public:
    CtiLMControlAreaMsgExecutor(CtiLMControlAreaMsg* contAreaMsg) : _controlAreaMsg(contAreaMsg){};
    virtual ~CtiLMControlAreaMsgExecutor(){delete _controlAreaMsg;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:
    CtiLMControlAreaMsg* _controlAreaMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};


class CtiLMCurtailmentAcknowledgeMsgExecutor : public CtiLMExecutor
{
public:
    CtiLMCurtailmentAcknowledgeMsgExecutor(CtiLMCurtailmentAcknowledgeMsg* curtailAckMsg) : _curtailAckMsg(curtailAckMsg) {};
    virtual ~CtiLMCurtailmentAcknowledgeMsgExecutor() { delete _curtailAckMsg;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:
    /*void DirectStart(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void DirectStop(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void CurtailmentStart(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void CurtailmentStop(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);*/

    CtiLMCurtailmentAcknowledgeMsg* _curtailAckMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};


class CtiLMEnergyExchangeAcceptMsgExecutor : public CtiLMExecutor
{
public:
    CtiLMEnergyExchangeAcceptMsgExecutor(CtiLMEnergyExchangeAcceptMsg* energyExchangeAcceptMsg) : _energyExchangeAcceptMsg(energyExchangeAcceptMsg) {};
    virtual ~CtiLMEnergyExchangeAcceptMsgExecutor() { delete _energyExchangeAcceptMsg;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:
    /*void DirectStart(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void DirectStop(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void CurtailmentStart(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void CurtailmentStop(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);*/

    CtiLMEnergyExchangeAcceptMsg* _energyExchangeAcceptMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};


class CtiLMForwardMsgToDispatchExecutor : public CtiLMExecutor
{
public:
    CtiLMForwardMsgToDispatchExecutor(CtiMessage* ctiMsg) : _ctiMessage(ctiMsg) {};
    virtual ~CtiLMForwardMsgToDispatchExecutor() { delete _ctiMessage;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:
    CtiMessage* _ctiMessage;
    RWRecursiveLock<RWMutexLock> _mutex;
};


class CtiLMMultiMsgExecutor : public CtiLMExecutor
{
public:
    CtiLMMultiMsgExecutor(CtiMultiMsg* multiMsg) : _multiMsg(multiMsg) {};
    virtual ~CtiLMMultiMsgExecutor() { delete _multiMsg;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:
    CtiMultiMsg* _multiMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};


class CtiLMShutdownExecutor : public CtiLMExecutor
{
public:
    CtiLMShutdownExecutor() {};
    virtual ~CtiLMShutdownExecutor() {};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
};


class CtiLMExecutorFactory
{
public:
    CtiLMExecutor* createExecutor(const CtiMessage* message);

};
#endif
