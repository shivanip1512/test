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

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results) {};

protected:
    CtiCCExecutor() {};
};

class CtiCCSubstationBusMsgExecutor : public CtiCCExecutor
{
public:
    CtiCCSubstationBusMsgExecutor(CtiCCSubstationBusMsg* busMsg) : _ccSubstationBusesMsg(busMsg){};
    virtual ~CtiCCSubstationBusMsgExecutor(){delete _ccSubstationBusesMsg;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:

    CtiCCSubstationBusMsg* _ccSubstationBusesMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};

class CtiCCCapBankStatesMsgExecutor : public CtiCCExecutor
{
public:
    CtiCCCapBankStatesMsgExecutor(CtiCCCapBankStatesMsg* stateMsg) : _ccCapBankStatesMsg(stateMsg){};
    virtual ~CtiCCCapBankStatesMsgExecutor(){delete _ccCapBankStatesMsg;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:

    CtiCCCapBankStatesMsg* _ccCapBankStatesMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};

class CtiCCGeoAreasMsgExecutor : public CtiCCExecutor
{
public:
    CtiCCGeoAreasMsgExecutor(CtiCCGeoAreasMsg* ccGeoAreasMsg) : _ccGeoAreasMsg(ccGeoAreasMsg){};
    virtual ~CtiCCGeoAreasMsgExecutor(){delete _ccGeoAreasMsg;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:

    CtiCCGeoAreasMsg* _ccGeoAreasMsg;
    RWRecursiveLock<RWMutexLock> _mutex;
};

class CtiCCCommandExecutor : public CtiCCExecutor
{
public:
    CtiCCCommandExecutor(CtiCCCommand* command) : _command(command) {};
    virtual ~CtiCCCommandExecutor() { delete _command;};

    virtual void Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

private:
    void EnableSubstationBus(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void DisableSubstationBus(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void EnableFeeder(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void DisableFeeder(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void EnableCapBank(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void DisableCapBank(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void OpenCapBank(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void CloseCapBank(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void ConfirmOpen(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void ConfirmClose(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);
    void SendAllSubstationBuses(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results);

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
