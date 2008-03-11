/*-----------------------------------------------------------------------------
    Filename:  ccexecutor.h
    
    Programmer:  Josh Wolberg
    
    Description:    Header file for the various Cap Control executor classes.
                            
    Initial Date:  8/30/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CCEXECUTOR_H
#define CCEXECUTOR_H

#include <rw/thr/countptr.h> 
#include <rw/thr/thrfunc.h>
#include <rw/thr/barrier.h>  

#include "ccmessage.h"
#include "ctdpcptrq.h"

class CtiCCExecutor
{
public:
    virtual ~CtiCCExecutor() {};

    virtual void Execute() {};


protected:
    CtiCCExecutor() {};
    void moveCapBank(INT permanentFlag, LONG oldFeederId, LONG movedCapBankId, LONG newFeederId, float capSwitchingOrder, float closeOrder, float tripOrder);
};

class CtiCCClientMsgExecutor : public CtiCCExecutor
{
public:
    CtiCCClientMsgExecutor(CtiMessage* ccMsg) : _ccMsg(ccMsg){};
    virtual ~CtiCCClientMsgExecutor(){delete _ccMsg;};

    virtual void Execute();

private:

    CtiMessage* _ccMsg;
};

class CtiCCCommandExecutor : public CtiCCExecutor
{
public:
    CtiCCCommandExecutor(CtiCCCommand* command) : _command(command) {};
    virtual ~CtiCCCommandExecutor() { delete _command;};

    virtual void Execute();

private:
    void EnableSubstationBus();
    void DisableSubstationBus();
    //void EnableSubstationBusVerification();
    //void DisableSubstationBusVerification();
    void EnableFeeder();
    void DisableFeeder();
    void EnableCapBank();
    void DisableCapBank();
    void OpenCapBank();
    void CloseCapBank();
    void ConfirmOpen();
    void ConfirmClose();
    void doConfirmImmediately(CtiCCSubstationBus* currentSubstationBus, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, LONG bankId);
    void SendAllData();
    void ReturnCapToOriginalFeeder();
    void ResetDailyOperations();
    void WaiveSubstationBus();
    void UnwaiveSubstationBus();
    void WaiveFeeder();
    void UnwaiveFeeder();
    void EnableOvUv();
    void DisableOvUv();
    void DeleteItem();
    void ConfirmSub();
    void ConfirmArea();
    void EnableArea();
    void DisableArea();
    void EnableSystem();
    void DisableSystem();
    void Scan2WayDevice();
    void Flip7010Device();
    void SendSystemStatus();
    void SendAllCapBankCommands();
    void SendTimeSync();
    void changeBankOperationalState();
    void AutoEnableOvUv();
    void AutoDisableOvUv();
    void AutoEnableOvUvByArea();
    void AutoDisableOvUvByArea();
    void AutoControlOvUvBySubstation(BOOL disableFlag);
    void AutoControlOvUvBySubBus(BOOL disableFlag);
    CtiCCCommand* _command;
};

class CtiCCCapBankMoveExecutor : public CtiCCExecutor
{
public:
    CtiCCCapBankMoveExecutor(CtiCCCapBankMoveMsg* capMoveMsg) : _capMoveMsg(capMoveMsg) {};
    virtual ~CtiCCCapBankMoveExecutor() { delete _capMoveMsg;};

    virtual void Execute();

private:

    CtiCCCapBankMoveMsg* _capMoveMsg;
};


class CtiCCSubstationVerificationExecutor : public CtiCCExecutor
{
public:
    CtiCCSubstationVerificationExecutor(CtiCCSubstationVerificationMsg* subVerificationMsg) : _subVerificationMsg(subVerificationMsg) {};
    virtual ~CtiCCSubstationVerificationExecutor() { delete _subVerificationMsg;};

    virtual void Execute();

private:

    void EnableSubstationBusVerification();   
    void DisableSubstationBusVerification();  

    CtiCCSubstationVerificationMsg* _subVerificationMsg;
};

class CtiCCPointDataMsgExecutor : public CtiCCExecutor
{
public:
    CtiCCPointDataMsgExecutor(CtiPointDataMsg* pointMsg) : _pointDataMsg(pointMsg) {};
    virtual ~CtiCCPointDataMsgExecutor() { delete _pointDataMsg;};

    virtual void Execute();

private:
    CtiPointDataMsg* _pointDataMsg;
};

class CtiCCForwardMsgToDispatchExecutor : public CtiCCExecutor
{
public:
    CtiCCForwardMsgToDispatchExecutor(CtiMessage* ctiMsg) : _ctiMessage(ctiMsg) {};
    virtual ~CtiCCForwardMsgToDispatchExecutor() { delete _ctiMessage;};

    virtual void Execute();

private:
    CtiMessage* _ctiMessage;
};

class CtiCCMultiMsgExecutor : public CtiCCExecutor
{
public:
    CtiCCMultiMsgExecutor(CtiMultiMsg* multiMsg) : _multiMsg(multiMsg) {};
    virtual ~CtiCCMultiMsgExecutor() { delete _multiMsg;};

    virtual void Execute();

private:
    CtiMultiMsg* _multiMsg;
};

class CtiCCShutdownExecutor : public CtiCCExecutor
{
public:
    CtiCCShutdownExecutor() {};
    virtual ~CtiCCShutdownExecutor() {};

    virtual void Execute();
};

class CtiCCExecutorFactory
{
public:
    CtiCCExecutorFactory() {};
    CtiCCExecutor* createExecutor(const CtiMessage* message);

};
#endif
