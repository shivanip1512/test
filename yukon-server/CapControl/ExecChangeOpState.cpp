#include "precompiled.h"

#include "ExecChangeOpState.h"
#include "capcontroller.h"
#include "ccsubstationbusstore.h"
#include "cccapbank.h"
#include "ccsubstationbus.h"

using std::endl;

ChangeOpStateExecutor::ChangeOpStateExecutor(ChangeOpState* message)
{
    _bankId = message->getItemId();
    _newState = message->getOpStateName();
    _userName = message->getUser();
}

ChangeOpStateExecutor::~ChangeOpStateExecutor()
{

}

void ChangeOpStateExecutor::execute()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    CtiCCCapBankPtr capBankPtr = store->findCapBankByPAObjectID(_bankId);
    if( capBankPtr == NULL )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " Capbank does not exist, cannot change operational state." << endl;
        return;
    }
    capBankPtr->setOperationalState(_newState);
    store->UpdateCapBankOperationalStateInDB( capBankPtr );

    CtiSignalMsg* msg = new CtiSignalMsg(SYS_PID_CAPCONTROL, 0,
                                         "Cap Bank Update",
                                         "Manual Op State Changed: " + capBankPtr->getOperationalState() + " for Bank: " + capBankPtr->getPaoName(),
                                         CapControlLogType );
    msg->setUser(_userName);
    msg->setMessageTime( CtiTime() );
    CtiCapController::getInstance()->sendMessageToDispatch( msg );

    CtiCCFeederPtr feederPtr = store->findFeederByPAObjectID( capBankPtr->getParentId() );
    if( feederPtr == NULL )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " WARNING: Changed operational state of an orphaned Cap Bank." << endl;
        return;
    }

    CtiCCSubstationBusPtr subbusPtr = store->findSubBusByPAObjectID( feederPtr->getParentId() );
    if( subbusPtr == NULL )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " WARNING: Changed operational state of an orphaned Cap Bank." << endl;
        return;
    }

    subbusPtr->setBusUpdatedFlag(true);
}

