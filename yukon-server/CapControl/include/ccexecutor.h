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
};

class CtiCCSubstationBusMsgExecutor : public CtiCCExecutor
{
public:
    CtiCCSubstationBusMsgExecutor(CtiCCSubstationBusMsg* busMsg) : _ccSubstationBusesMsg(busMsg){};
    virtual ~CtiCCSubstationBusMsgExecutor(){};

    virtual void Execute();

private:

    CtiCCSubstationBusMsg* _ccSubstationBusesMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};

class CtiCCCapBankStatesMsgExecutor : public CtiCCExecutor
{
public:
    CtiCCCapBankStatesMsgExecutor(CtiCCCapBankStatesMsg* stateMsg) : _ccCapBankStatesMsg(stateMsg){};
    virtual ~CtiCCCapBankStatesMsgExecutor(){};

    virtual void Execute();

private:

    CtiCCCapBankStatesMsg* _ccCapBankStatesMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};

class CtiCCGeoAreasMsgExecutor : public CtiCCExecutor
{
public:
    CtiCCGeoAreasMsgExecutor(CtiCCGeoAreasMsg* ccGeoAreasMsg) : _ccGeoAreasMsg(ccGeoAreasMsg){};
    virtual ~CtiCCGeoAreasMsgExecutor(){};

    virtual void Execute();

private:

    CtiCCGeoAreasMsg* _ccGeoAreasMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
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
    void EnableFeeder();
    void DisableFeeder();
    void EnableCapBank();
    void DisableCapBank();
    void OpenCapBank();
    void CloseCapBank();
    void ConfirmOpen();
    void ConfirmClose();
    void SendAllSubstationBuses();

    CtiCCCommand* _command;
    RWRecursiveLock<RWMutexLock> _mutex;
};

class CtiCCPointDataMsgExecutor : public CtiCCExecutor
{
public:
    CtiCCPointDataMsgExecutor(CtiPointDataMsg* pointMsg) : _pointDataMsg(pointMsg) {};
    virtual ~CtiCCPointDataMsgExecutor() { delete _pointDataMsg;};

    virtual void Execute();

private:
    CtiPointDataMsg* _pointDataMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};

class CtiCCForwardMsgToDispatchExecutor : public CtiCCExecutor
{
public:
    CtiCCForwardMsgToDispatchExecutor(CtiMessage* ctiMsg) : _ctiMessage(ctiMsg) {};
    virtual ~CtiCCForwardMsgToDispatchExecutor() { delete _ctiMessage;};

    virtual void Execute();

private:
    CtiMessage* _ctiMessage;
    RWRecursiveLock<RWMutexLock> _mutex;
};

class CtiCCMultiMsgExecutor : public CtiCCExecutor
{
public:
    CtiCCMultiMsgExecutor(CtiMultiMsg* multiMsg) : _multiMsg(multiMsg) {};
    virtual ~CtiCCMultiMsgExecutor() { delete _multiMsg;};

    virtual void Execute();

private:
    CtiMultiMsg* _multiMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
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
    CtiCCExecutor* createExecutor(const CtiMessage* message);

};
#endif
