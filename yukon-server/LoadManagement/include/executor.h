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

    virtual void Execute() {};

protected:
    CtiLMExecutor() {};
};


class CtiLMCommandExecutor : public CtiLMExecutor
{
public:
    CtiLMCommandExecutor(CtiLMCommand* command) : _command(command) {};
    virtual ~CtiLMCommandExecutor() { delete _command;};

    virtual void Execute();

private:
    void ChangeThreshold();
    void ChangeRestoreOffset();
    void ChangeCurrentOperationalState();
    void EnableControlArea();
    void DisableControlArea();
    void EnableProgram();
    void DisableProgram();
    void SendAllControlAreas();
    void ChangeDailyStartTime();
    void ChangeDailyStopTime();

    CtiLMCommand* _command;
    RWRecursiveLock<RWMutexLock> _mutex;
};


class CtiLMManualControlMsgExecutor : public CtiLMExecutor
{
public:
    CtiLMManualControlMsgExecutor(CtiLMManualControlMsg* controlMsg) : _controlMsg(controlMsg) {};
    virtual ~CtiLMManualControlMsgExecutor() { delete _controlMsg;};

    virtual void Execute();

private:
    void ScheduledStart();
    void ScheduledStop();
    void StartNow();
    void StopNow();

    CtiLMManualControlMsg* _controlMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};


class CtiLMEnergyExchangeControlMsgExecutor : public CtiLMExecutor
{
public:
    CtiLMEnergyExchangeControlMsgExecutor(CtiLMEnergyExchangeControlMsg* energyExchangeMsg) : _energyExchangeMsg(energyExchangeMsg) {};
    virtual ~CtiLMEnergyExchangeControlMsgExecutor() { delete _energyExchangeMsg;};

    virtual void Execute();

private:
    void NewOffer();
    void OfferUpdate();
    void OfferRevision();
    void CloseOffer();
    void CancelOffer();

    CtiLMEnergyExchangeControlMsg* _energyExchangeMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};


class CtiLMControlAreaMsgExecutor : public CtiLMExecutor
{
public:
    CtiLMControlAreaMsgExecutor(CtiLMControlAreaMsg* contAreaMsg) : _controlAreaMsg(contAreaMsg){};
    virtual ~CtiLMControlAreaMsgExecutor(){};

    virtual void Execute();

private:
    CtiLMControlAreaMsg* _controlAreaMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};


class CtiLMCurtailmentAcknowledgeMsgExecutor : public CtiLMExecutor
{
public:
    CtiLMCurtailmentAcknowledgeMsgExecutor(CtiLMCurtailmentAcknowledgeMsg* curtailAckMsg) : _curtailAckMsg(curtailAckMsg) {};
    virtual ~CtiLMCurtailmentAcknowledgeMsgExecutor() { delete _curtailAckMsg;};

    virtual void Execute();

private:
    /*void DirectStart();
    void DirectStop();
    void CurtailmentStart();
    void CurtailmentStop();*/

    CtiLMCurtailmentAcknowledgeMsg* _curtailAckMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};


class CtiLMEnergyExchangeAcceptMsgExecutor : public CtiLMExecutor
{
public:
    CtiLMEnergyExchangeAcceptMsgExecutor(CtiLMEnergyExchangeAcceptMsg* energyExchangeAcceptMsg) : _energyExchangeAcceptMsg(energyExchangeAcceptMsg) {};
    virtual ~CtiLMEnergyExchangeAcceptMsgExecutor() { delete _energyExchangeAcceptMsg;};

    virtual void Execute();

private:
    /*void DirectStart();
    void DirectStop();
    void CurtailmentStart();
    void CurtailmentStop();*/

    CtiLMEnergyExchangeAcceptMsg* _energyExchangeAcceptMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};


class CtiLMForwardMsgToDispatchExecutor : public CtiLMExecutor
{
public:
    CtiLMForwardMsgToDispatchExecutor(CtiMessage* ctiMsg) : _ctiMessage(ctiMsg) {};
    virtual ~CtiLMForwardMsgToDispatchExecutor() { delete _ctiMessage;};

    virtual void Execute();

private:
    CtiMessage* _ctiMessage;
    RWRecursiveLock<RWMutexLock> _mutex;
};


class CtiLMMultiMsgExecutor : public CtiLMExecutor
{
public:
    CtiLMMultiMsgExecutor(CtiMultiMsg* multiMsg) : _multiMsg(multiMsg) {};
    virtual ~CtiLMMultiMsgExecutor() { delete _multiMsg;};

    virtual void Execute();

private:
    CtiMultiMsg* _multiMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};


class CtiLMShutdownExecutor : public CtiLMExecutor
{
public:
    CtiLMShutdownExecutor() {};
    virtual ~CtiLMShutdownExecutor() {};

    virtual void Execute();
};


class CtiLMExecutorFactory
{
public:
    CtiLMExecutor* createExecutor(const CtiMessage* message);

};
#endif
