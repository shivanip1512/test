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

#include "ctdpcptrq.h"

#include "msg_server_req.h"

#include "lmmessage.h"
#include "lmprogramdirect.h"
#include "lmprogramcurtailment.h"


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
    void DisableProgram(bool emergency);
    void SendAllControlAreas();
    void ChangeDailyStartTime();
    void ChangeDailyStopTime();
    void ShedGroup();
    void CycleGroup();
    void RestoreGroup();
    void EnableGroup();
    void DisableGroup();
    void ConfirmGroup();
    void ResetPeakPointValue();

    CtiLMCommand* _command;
};


class CtiLMManualControlRequestExecutor : public CtiLMExecutor
{
public:
    CtiLMManualControlRequestExecutor(CtiLMManualControlRequest* controlMsg,
                                  CtiServerRequestMsg* serverReq)
        : _controlMsg(controlMsg), _request(serverReq) { };
    virtual ~CtiLMManualControlRequestExecutor() { delete _controlMsg; delete _request; };

    virtual void Execute();

private:
    void StartProgram(CtiLMProgramBase* program, CtiLMControlArea* controlArea, const RWDBDateTime& start, const RWDBDateTime& stop);
    void StopProgram(CtiLMProgramBase* program, CtiLMControlArea* controlArea, const RWDBDateTime& stop);
    void StartDirectProgram(CtiLMProgramDirect* lmProgramDirect, CtiLMControlArea* controlArea, const RWDBDateTime& start, const RWDBDateTime& stop);
    void StopDirectProgram(CtiLMProgramDirect* lmProgramDirect, CtiLMControlArea* controlArea, const RWDBDateTime& stop);
    void StartCurtailmentProgram(CtiLMProgramCurtailment* lmProgramCurtailment, CtiLMControlArea* controlArea, const RWDBDateTime& start, const RWDBDateTime& stop);
    void StopCurtailmentProgram(CtiLMProgramCurtailment* lmProgramCurtailment, CtiLMControlArea* controlArea, const RWDBDateTime& stop);

    void CoerceStartStopTime(CtiLMProgramBase* program, RWDBDateTime& start, RWDBDateTime& stop);
    void CoerceStopTime(CtiLMProgramBase* program, RWDBDateTime& stop);
    
    CtiServerRequestMsg* _request;
    CtiLMManualControlRequest* _controlMsg;
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
};


class CtiLMControlAreaMsgExecutor : public CtiLMExecutor
{
public:
    CtiLMControlAreaMsgExecutor(CtiLMControlAreaMsg* contAreaMsg) : _controlAreaMsg(contAreaMsg){};
    virtual ~CtiLMControlAreaMsgExecutor(){};

    virtual void Execute();

private:
    CtiLMControlAreaMsg* _controlAreaMsg;
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
};


class CtiLMForwardMsgToDispatchExecutor : public CtiLMExecutor
{
public:
    CtiLMForwardMsgToDispatchExecutor(CtiMessage* ctiMsg) : _ctiMessage(ctiMsg) {};
    virtual ~CtiLMForwardMsgToDispatchExecutor() { delete _ctiMessage;};

    virtual void Execute();

private:
    CtiMessage* _ctiMessage;
};


class CtiLMMultiMsgExecutor : public CtiLMExecutor
{
public:
    CtiLMMultiMsgExecutor(CtiMultiMsg* multiMsg) : _multiMsg(multiMsg) {};
    virtual ~CtiLMMultiMsgExecutor() { delete _multiMsg;};

    virtual void Execute();

private:
    CtiMultiMsg* _multiMsg;
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
