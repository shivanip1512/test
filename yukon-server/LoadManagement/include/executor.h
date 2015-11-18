#pragma once

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
    void StartProgram(CtiLMProgramBaseSPtr program, CtiLMControlArea* controlArea, const CtiTime& start, const CtiTime& stop);
    void StopProgram(CtiLMProgramBaseSPtr program, CtiLMControlArea* controlArea, const CtiTime& stop);
    void StartDirectProgram(CtiLMProgramDirectSPtr lmProgramDirect, CtiLMControlArea* controlArea, const CtiTime& start, const CtiTime& stop);
    void StopDirectProgram(CtiLMProgramDirectSPtr lmProgramDirect, CtiLMControlArea* controlArea, const CtiTime& stop);
    void StartCurtailmentProgram(CtiLMProgramCurtailmentSPtr lmProgramCurtailment, CtiLMControlArea* controlArea, const CtiTime& start, const CtiTime& stop);
    void StopCurtailmentProgram(CtiLMProgramCurtailmentSPtr lmProgramCurtailment, CtiLMControlArea* controlArea, const CtiTime& stop);
    void fitTimeToNotifications( CtiTime &proposedStart, CtiTime &proposedStop, CtiLMProgramBaseSPtr program );

    CtiServerRequestMsg* _request;
    CtiLMManualControlRequest* _controlMsg;

protected:
    void CoerceStartStopTime(CtiLMProgramBaseSPtr program, CtiTime& start, CtiTime& stop, CtiLMControlArea *controlArea = NULL);
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
    CtiLMMultiMsgExecutor(CtiMultiMsg* multiMsg);
    virtual ~CtiLMMultiMsgExecutor() { delete _multiMsg;};

    virtual void Execute();

private:
    CtiMultiMsg* _multiMsg;
};


class CtiLMExecutorFactory
{
public:
    CtiLMExecutor* createExecutor(const CtiMessage* message);

};
