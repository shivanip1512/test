/*-----------------------------------------------------------------------------
    Filename:  executor.cpp
    
    Programmer:  Josh Wolberg
    
    Description:    Defines Cap Control executor classes.

    Initial Date:  8/17/2000
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/

#include "executor.h"
#include "capcontrol.h"
#include "strategystore.h"
#include "controller.h"
#include "ccid.h"
                     
#include <rw/collstr.h>

extern BOOL _CAP_DEBUG;

/*===========================================================================
    CtiCCCommandExecutor
===========================================================================*/

/*---------------------------------------------------------------------------
    Execute
    
    Executes the command and places any resulting messages on the result
    queue.
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    switch ( _command->Command() )
    {
    case CtiCCCommand::DISABLE_STRATEGY:
        DisableStrategy(results);
        break;

    case CtiCCCommand::ENABLE_STRATEGY:
        EnableStrategy(results);
        break;

    case CtiCCCommand::DISABLE_CAPBANK:
        DisableCapBank(results);
        break;

    case CtiCCCommand::ENABLE_CAPBANK:
        EnableCapBank(results);
        break;

    case CtiCCCommand::OPEN_CAPBANK:
        OpenCapBank(results);
        break;

    case CtiCCCommand::CLOSE_CAPBANK:
        CloseCapBank(results);
        break;

    case CtiCCCommand::CONFIRM_OPEN:
        ConfirmOpen(results);
        break;

    case CtiCCCommand::CONFIRM_CLOSE:
        ConfirmClose(results);
        break;

    default:
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime().asString() << " - " << "executor.cpp::Execute - unknown command type" << endl;
        }

    }

}

/*---------------------------------------------------------------------------
    EnableStrategy
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::EnableStrategy(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    
    ULONG stratID = _command->Id();
    CtiCCStrategyStore* store = CtiCCStrategyStore::Instance();
    RWOrdered& strategies = store->Strategies();

    for(ULONG i=0;i<strategies.entries();i++)
    {
        CtiCCStrategy* current = (CtiCCStrategy*)strategies[i];
        if( stratID == current->Id() )
        {
            current->setStatus(CtiCCStrategy::Enabled);
            ((CtiCCStrategy*)strategies[i])->setStrategyUpdated(TRUE);
            store->UpdateStrategy(current);
            RWCString text = RWCString("Strategy Enabled");
            RWCString additional = RWCString("Strat: ");
            additional += current->Name();
            CtiCController::Instance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
            break;
        }
    }
}

/*---------------------------------------------------------------------------
    DisableStrategy
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::DisableStrategy(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    
    ULONG stratID = _command->Id();
    CtiCCStrategyStore* store = CtiCCStrategyStore::Instance();
    RWOrdered& strategies = store->Strategies();

    for(ULONG i=0;i<strategies.entries();i++)
    {
        CtiCCStrategy* current = (CtiCCStrategy*)strategies[i];
        if( stratID == current->Id() )
        {
            current->setStatus(CtiCCStrategy::Disabled);
            ((CtiCCStrategy*)strategies[i])->setStrategyUpdated(TRUE);
            store->UpdateStrategy(current);
            RWCString text = RWCString("Strategy Disabled");
            RWCString additional = RWCString("Strat: ");
            additional += current->Name();
            CtiCController::Instance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
            break;
        }
    }
}

/*---------------------------------------------------------------------------
    EnableCapBank
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::EnableCapBank(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    
    ULONG controlID = _command->Id();
    bool found = FALSE;
    CtiCCStrategyStore* store = CtiCCStrategyStore::Instance();
    RWOrdered& strategies = store->Strategies();

    for(ULONG i=0;i<strategies.entries();i++)
    {
        RWOrdered& capBanks = ((CtiCCStrategy*)strategies[i])->CapBankList();
        for(ULONG j=0;j<capBanks.entries();j++)
        {
            CtiCapBank* current = (CtiCapBank*)capBanks[j];
            if( controlID == current->ControlDeviceId() )
            {
                current->setDisableFlag(FALSE);
                ((CtiCCStrategy*)strategies[i])->setStrategyUpdated(TRUE);
                store->UpdateCapBank(current);
                if( current->StatusPointId() > 0 )
                {
                    RWCString text = RWCString("Enabled");
                    RWCString additional = RWCString("Strat: ");
                    additional += ((CtiCCStrategy*)strategies[i])->Name();
                    CtiCController::Instance()->sendMessageToDispatch(new CtiSignalMsg(current->StatusPointId(),0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Cap Bank: " << current->Name()
                                  << " DeviceID: " << current->Id() << " doesn't have a status point!" << endl;
                }

                found = TRUE;
                break;
            }
        }
        if( found )
            break;
    }
}

/*---------------------------------------------------------------------------
    DisableCapBank
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::DisableCapBank(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    
    ULONG controlID = _command->Id();
    bool found = FALSE;
    CtiCCStrategyStore* store = CtiCCStrategyStore::Instance();
    RWOrdered& strategies = store->Strategies();

    for(ULONG i=0;i<strategies.entries();i++)
    {
        RWOrdered& capBanks = ((CtiCCStrategy*)strategies[i])->CapBankList();
        for(ULONG j=0;j<capBanks.entries();j++)
        {
            CtiCapBank* current = (CtiCapBank*)capBanks[j];
            if( controlID == current->ControlDeviceId() )
            {
                current->setDisableFlag(TRUE);
                ((CtiCCStrategy*)strategies[i])->setStrategyUpdated(TRUE);
                store->UpdateCapBank(current);
                if( current->StatusPointId() )
                {
                    RWCString text = RWCString("Disabled");
                    RWCString additional = RWCString("Strat: ");
                    additional += ((CtiCCStrategy*)strategies[i])->Name();
                    CtiCController::Instance()->sendMessageToDispatch(new CtiSignalMsg(current->StatusPointId(),0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Cap Bank: " << current->Name()
                                  << " DeviceID: " << current->Id() << " doesn't have a status point!" << endl;
                }

                found = TRUE;
                break;
            }
        }
        if( found )
            break;
    }
}

/*---------------------------------------------------------------------------
    OpenCapBank
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::OpenCapBank(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    
    ULONG controlID = 0;
    ULONG bankID = _command->Id();
    bool found = FALSE;
    bool savedRecentlyControlledFlag = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    RWOrdered& pointChanges = multi->getData();
    CtiCCStrategyStore* store = CtiCCStrategyStore::Instance();
    RWOrdered& strategies = store->Strategies();

    for(ULONG i=0;i<strategies.entries();i++)
    {
        CtiCCStrategy* currentStrat = (CtiCCStrategy*)strategies[i];
        RWOrdered& capBanks = currentStrat->CapBankList();
        for(ULONG j=0;j<capBanks.entries();j++)
        {
            CtiCapBank* currentBank = (CtiCapBank*)capBanks[j];
            if( bankID == currentBank->ControlDeviceId() )
            {
                savedRecentlyControlledFlag = currentStrat->RecentlyControlled();
                currentStrat->setRecentlyControlled(FALSE);
                controlID = currentBank->ControlDeviceId();
                currentStrat->setLastCapBankControlled(currentBank->Id());
                currentStrat->setLastOperation(RWDBDateTime());
                currentBank->setControlStatus(CtiCapBank::OpenPending);
                currentStrat->figureActualVarPointValue();
                currentStrat->setOperations(currentStrat->Operations() + 1);
                currentBank->setOperations(currentBank->Operations() + 1);
                currentStrat->setStrategyUpdated(TRUE);
                currentStrat->setCalculatedValueBeforeControl(currentStrat->CalculatedVarPointValue());
                if( currentBank->StatusPointId() > 0 )
                {
                    RWCString text = RWCString("Manual Open Sent");
                    RWCString additional = RWCString("Strat: ");
                    additional += currentStrat->Name();
                    pointChanges.insert(new CtiSignalMsg(currentBank->StatusPointId(),1,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                    ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                    if( !savedRecentlyControlledFlag )
                    {
                        pointChanges.insert(new CtiPointDataMsg(currentBank->StatusPointId(),currentBank->ControlStatus(),NormalQuality,StatusPointType));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(2);
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Cap Bank: " << currentBank->Name()
                                  << " DeviceID: " << currentBank->Id() << " doesn't have a status point!" << endl;
                }

                if( currentBank->OperationAnalogPointId() > 0 )
                {
                    pointChanges.insert(new CtiPointDataMsg(currentBank->OperationAnalogPointId(),currentBank->Operations(),NormalQuality,AnalogPointType));
                    ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
                }

                found = TRUE;
                if( !savedRecentlyControlledFlag )
                {
                    currentStrat->setRecentlyControlled(TRUE);
                }
                else
                {
                    currentStrat->setRecentlyControlled(FALSE);
                }
                break;
            }
        }
        if( found )
        {
            if( savedRecentlyControlledFlag && !currentStrat->RecentlyControlled() )
            {
                for(ULONG k=0;k<capBanks.entries();k++)
                {
                    CtiCapBank* currentBank = (CtiCapBank*)capBanks[k];
                    if( currentBank->ControlStatus() == CtiCapBank::ClosePending )
                    {
                        if( currentBank->StatusPointId() > 0 )
                        {
                            currentBank->setControlStatus(CtiCapBank::Close);
                            pointChanges.insert(new CtiPointDataMsg(currentBank->StatusPointId(),CtiCapBank::Close,NormalQuality,StatusPointType));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime().asString() << " - " << "Cap Bank: " << currentBank->Name()
                                          << " DeviceID: " << currentBank->Id() << " doesn't have a status point!" << endl;
                        }
                    }
                    else if( currentBank->ControlStatus() == CtiCapBank::OpenPending )
                    {
                        if( currentBank->StatusPointId() > 0 )
                        {
                            currentBank->setControlStatus(CtiCapBank::Open);
                            pointChanges.insert(new CtiPointDataMsg(currentBank->StatusPointId(),CtiCapBank::Open,NormalQuality,StatusPointType));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime().asString() << " - " << "Cap Bank: " << currentBank->Name()
                                          << " DeviceID: " << currentBank->Id() << " doesn't have a status point!" << endl;
                        }
                    }
                }
            }
            break;
        }
    }

    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"control open");
        reqMsg->setSOE(5);
        CtiCController::Instance()->manualCapBankControl( reqMsg, multi );
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << "Could not create Porter Request Message - executor.cpp::OpenCapBank" << endl;
    }
}

/*---------------------------------------------------------------------------
    CloseCapBank
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::CloseCapBank(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    
    ULONG controlID = 0;
    ULONG bankID = _command->Id();
    bool found = FALSE;
    bool savedRecentlyControlledFlag = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    RWOrdered& pointChanges = multi->getData();
    CtiCCStrategyStore* store = CtiCCStrategyStore::Instance();
    RWOrdered& strategies = store->Strategies();

    for(ULONG i=0;i<strategies.entries();i++)
    {
        CtiCCStrategy* currentStrat = (CtiCCStrategy*)strategies[i];
        RWOrdered& capBanks = currentStrat->CapBankList();
        for(ULONG j=0;j<capBanks.entries();j++)
        {
            CtiCapBank* currentBank = (CtiCapBank*)capBanks[j];
            if( bankID == currentBank->ControlDeviceId() )
            {
                savedRecentlyControlledFlag = currentStrat->RecentlyControlled();
                currentStrat->setRecentlyControlled(FALSE);
                controlID = currentBank->ControlDeviceId();
                currentStrat->setLastCapBankControlled(currentBank->Id());
                currentStrat->setLastOperation(RWDBDateTime());
                currentBank->setControlStatus(CtiCapBank::ClosePending);
                currentStrat->figureActualVarPointValue();
                currentStrat->setOperations(currentStrat->Operations() + 1);
                currentBank->setOperations(currentBank->Operations() + 1);
                currentStrat->setRecentlyControlled(TRUE);
                currentStrat->setStrategyUpdated(TRUE);
                currentStrat->setCalculatedValueBeforeControl(currentStrat->CalculatedVarPointValue());
                if( currentBank->StatusPointId() > 0 )
                {
                    RWCString text = RWCString("Manual Close Sent");
                    RWCString additional = RWCString("Strat: ");
                    additional += currentStrat->Name();
                    pointChanges.insert(new CtiSignalMsg(currentBank->StatusPointId(),1,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                    ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                    if( !savedRecentlyControlledFlag )
                    {
                        pointChanges.insert(new CtiPointDataMsg(currentBank->StatusPointId(),currentBank->ControlStatus(),NormalQuality,StatusPointType));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(2);
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Cap Bank: " << currentBank->Name()
                                  << " DeviceID: " << currentBank->Id() << " doesn't have a status point!" << endl;
                }

                if( currentBank->OperationAnalogPointId() > 0 )
                {
                    pointChanges.insert(new CtiPointDataMsg(currentBank->OperationAnalogPointId(),currentBank->Operations(),NormalQuality,AnalogPointType));
                    ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
                }
                
                found = TRUE;
                if( !savedRecentlyControlledFlag )
                {
                    currentStrat->setRecentlyControlled(TRUE);
                }
                else
                {
                    currentStrat->setRecentlyControlled(FALSE);
                }
                break;
            }
        }
        if( found )
        {
            if( savedRecentlyControlledFlag && !currentStrat->RecentlyControlled() )
            {
                for(ULONG k=0;k<capBanks.entries();k++)
                {
                    CtiCapBank* currentBank = (CtiCapBank*)capBanks[k];
                    if( currentBank->ControlStatus() == CtiCapBank::ClosePending )
                    {
                        if( currentBank->StatusPointId() > 0 )
                        {
                            currentBank->setControlStatus(CtiCapBank::Close);
                            pointChanges.insert(new CtiPointDataMsg(currentBank->StatusPointId(),CtiCapBank::Close,NormalQuality,StatusPointType));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime().asString() << " - " << "Cap Bank: " << currentBank->Name()
                                          << " DeviceID: " << currentBank->Id() << " doesn't have a status point!" << endl;
                        }
                    }
                    else if( currentBank->ControlStatus() == CtiCapBank::OpenPending )
                    {
                        if( currentBank->StatusPointId() > 0 )
                        {
                            currentBank->setControlStatus(CtiCapBank::Open);
                            pointChanges.insert(new CtiPointDataMsg(currentBank->StatusPointId(),CtiCapBank::Open,NormalQuality,StatusPointType));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime().asString() << " - " << "Cap Bank: " << currentBank->Name()
                                          << " DeviceID: " << currentBank->Id() << " doesn't have a status point!" << endl;
                        }
                    }
                }
            }
            break;
        }
    }

    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"control close");
        reqMsg->setSOE(5);
        CtiCController::Instance()->manualCapBankControl( reqMsg, multi );
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << "Could not create Porter Request Message - executor.cpp::CloseCapBank" << endl;
    }
}

/*---------------------------------------------------------------------------
    ConfirmOpen
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::ConfirmOpen(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    
    ULONG controlID = 0;
    ULONG bankID = _command->Id();
    bool found = FALSE;
    bool savedRecentlyControlledFlag = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    RWOrdered& pointChanges = multi->getData();
    CtiCCStrategyStore* store = CtiCCStrategyStore::Instance();
    RWOrdered& strategies = store->Strategies();

    for(ULONG i=0;i<strategies.entries();i++)
    {
        CtiCCStrategy* currentStrat = (CtiCCStrategy*)strategies[i];
        RWOrdered& capBanks = currentStrat->CapBankList();
        for(ULONG j=0;j<capBanks.entries();j++)
        {
            CtiCapBank* currentBank = (CtiCapBank*)capBanks[j];
            if( bankID == currentBank->ControlDeviceId() )
            {
                savedRecentlyControlledFlag = currentStrat->RecentlyControlled();
                currentStrat->setRecentlyControlled(FALSE);
                controlID = currentBank->ControlDeviceId();
                currentStrat->setLastCapBankControlled(currentBank->Id());
                currentStrat->setLastOperation(RWDBDateTime());
                currentBank->setControlStatus(CtiCapBank::OpenPending);
                currentStrat->figureActualVarPointValue();
                currentStrat->setOperations(currentStrat->Operations() + 1);
                currentBank->setOperations(currentBank->Operations() + 1);
                currentStrat->setStrategyUpdated(TRUE);
                currentStrat->setCalculatedValueBeforeControl(currentStrat->CalculatedVarPointValue());
                if( currentBank->StatusPointId() > 0 )
                {
                    RWCString text = RWCString("Manual Confirm Open Sent");
                    RWCString additional = RWCString("Strat: ");
                    additional += currentStrat->Name();
                    pointChanges.insert(new CtiSignalMsg(currentBank->StatusPointId(),1,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                    ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                    if( !savedRecentlyControlledFlag )
                    {
                        pointChanges.insert(new CtiPointDataMsg(currentBank->StatusPointId(),currentBank->ControlStatus(),NormalQuality,StatusPointType));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(2);
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Cap Bank: " << currentBank->Name()
                                  << " DeviceID: " << currentBank->Id() << " doesn't have a status point!" << endl;
                }

                if( currentBank->OperationAnalogPointId() > 0 )
                {
                    pointChanges.insert(new CtiPointDataMsg(currentBank->OperationAnalogPointId(),currentBank->Operations(),NormalQuality,AnalogPointType));
                    ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
                }

                found = TRUE;
                if( !savedRecentlyControlledFlag )
                {
                    currentStrat->setRecentlyControlled(TRUE);
                }
                else
                {
                    currentStrat->setRecentlyControlled(FALSE);
                }
                break;
            }
        }
        if( found )
        {
            if( savedRecentlyControlledFlag && !currentStrat->RecentlyControlled() )
            {
                for(ULONG k=0;k<capBanks.entries();k++)
                {
                    CtiCapBank* currentBank = (CtiCapBank*)capBanks[k];
                    if( currentBank->ControlStatus() == CtiCapBank::ClosePending )
                    {
                        if( currentBank->StatusPointId() > 0 )
                        {
                            currentBank->setControlStatus(CtiCapBank::Close);
                            pointChanges.insert(new CtiPointDataMsg(currentBank->StatusPointId(),CtiCapBank::Close,NormalQuality,StatusPointType));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime().asString() << " - " << "Cap Bank: " << currentBank->Name()
                                          << " DeviceID: " << currentBank->Id() << " doesn't have a status point!" << endl;
                        }
                    }
                    else if( currentBank->ControlStatus() == CtiCapBank::OpenPending )
                    {
                        if( currentBank->StatusPointId() > 0 )
                        {
                            currentBank->setControlStatus(CtiCapBank::Open);
                            pointChanges.insert(new CtiPointDataMsg(currentBank->StatusPointId(),CtiCapBank::Open,NormalQuality,StatusPointType));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime().asString() << " - " << "Cap Bank: " << currentBank->Name()
                                          << " DeviceID: " << currentBank->Id() << " doesn't have a status point!" << endl;
                        }
                    }
                }
            }
            break;
        }
    }

    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"control open");
        reqMsg->setSOE(5);
        CtiCController::Instance()->manualCapBankControl( reqMsg, multi );
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << "Could not create Porter Request Message - executor.cpp::OpenCapBank" << endl;
    }
}

/*---------------------------------------------------------------------------
    ConfirmClose
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::ConfirmClose(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    
    ULONG controlID = 0;
    ULONG bankID = _command->Id();
    bool found = FALSE;
    bool savedRecentlyControlledFlag = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    RWOrdered& pointChanges = multi->getData();
    CtiCCStrategyStore* store = CtiCCStrategyStore::Instance();
    RWOrdered& strategies = store->Strategies();

    for(ULONG i=0;i<strategies.entries();i++)
    {
        CtiCCStrategy* currentStrat = (CtiCCStrategy*)strategies[i];
        RWOrdered& capBanks = currentStrat->CapBankList();
        for(ULONG j=0;j<capBanks.entries();j++)
        {
            CtiCapBank* currentBank = (CtiCapBank*)capBanks[j];
            if( bankID == currentBank->ControlDeviceId() )
            {
                savedRecentlyControlledFlag = currentStrat->RecentlyControlled();
                currentStrat->setRecentlyControlled(FALSE);
                controlID = currentBank->ControlDeviceId();
                currentStrat->setLastCapBankControlled(currentBank->Id());
                currentStrat->setLastOperation(RWDBDateTime());
                currentBank->setControlStatus(CtiCapBank::ClosePending);
                currentStrat->figureActualVarPointValue();
                currentStrat->setOperations(currentStrat->Operations() + 1);
                currentBank->setOperations(currentBank->Operations() + 1);
                currentStrat->setRecentlyControlled(TRUE);
                currentStrat->setStrategyUpdated(TRUE);
                currentStrat->setCalculatedValueBeforeControl(currentStrat->CalculatedVarPointValue());
                if( currentBank->StatusPointId() > 0 )
                {
                    RWCString text = RWCString("Manually Confirm Close Sent");
                    RWCString additional = RWCString("Strat: ");
                    additional += currentStrat->Name();
                    pointChanges.insert(new CtiSignalMsg(currentBank->StatusPointId(),1,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                    ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                    if( !savedRecentlyControlledFlag )
                    {
                        pointChanges.insert(new CtiPointDataMsg(currentBank->StatusPointId(),currentBank->ControlStatus(),NormalQuality,StatusPointType));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(2);
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime().asString() << " - " << "Cap Bank: " << currentBank->Name()
                                  << " DeviceID: " << currentBank->Id() << " doesn't have a status point!" << endl;
                }

                if( currentBank->OperationAnalogPointId() > 0 )
                {
                    pointChanges.insert(new CtiPointDataMsg(currentBank->OperationAnalogPointId(),currentBank->Operations(),NormalQuality,AnalogPointType));
                    ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
                }
                
                found = TRUE;
                if( !savedRecentlyControlledFlag )
                {
                    currentStrat->setRecentlyControlled(TRUE);
                }
                else
                {
                    currentStrat->setRecentlyControlled(FALSE);
                }
                break;
            }
        }
        if( found )
        {
            if( savedRecentlyControlledFlag && !currentStrat->RecentlyControlled() )
            {
                for(ULONG k=0;k<capBanks.entries();k++)
                {
                    CtiCapBank* currentBank = (CtiCapBank*)capBanks[k];
                    if( currentBank->ControlStatus() == CtiCapBank::ClosePending )
                    {
                        if( currentBank->StatusPointId() > 0 )
                        {
                            currentBank->setControlStatus(CtiCapBank::Close);
                            pointChanges.insert(new CtiPointDataMsg(currentBank->StatusPointId(),CtiCapBank::Close,NormalQuality,StatusPointType));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime().asString() << " - " << "Cap Bank: " << currentBank->Name()
                                          << " DeviceID: " << currentBank->Id() << " doesn't have a status point!" << endl;
                        }
                    }
                    else if( currentBank->ControlStatus() == CtiCapBank::OpenPending )
                    {
                        if( currentBank->StatusPointId() > 0 )
                        {
                            currentBank->setControlStatus(CtiCapBank::Open);
                            pointChanges.insert(new CtiPointDataMsg(currentBank->StatusPointId(),CtiCapBank::Open,NormalQuality,StatusPointType));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime().asString() << " - " << "Cap Bank: " << currentBank->Name()
                                          << " DeviceID: " << currentBank->Id() << " doesn't have a status point!" << endl;
                        }
                    }
                }
            }
            break;
        }
    }

    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"control close");
        reqMsg->setSOE(5);
        CtiCController::Instance()->manualCapBankControl( reqMsg, multi );
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << "Could not create Porter Request Message - executor.cpp::CloseCapBank" << endl;
    }
}


/*===========================================================================
    CtiCCStrategyListMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiCCStrategyListMsgExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    CtiCCServer::Instance()->Broadcast(_stratListMsg->replicateMessage());
}


/*===========================================================================
    CtiCCStateListMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiCCStateListMsgExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    CtiCCServer::Instance()->Broadcast(_stateListMsg->replicateMessage());
}


/*===========================================================================
    CtiCCAreaListMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiCCAreaListMsgExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    CtiCCServer::Instance()->Broadcast(_areaListMsg->replicateMessage());
}


/*===========================================================================
    CtiCCForwardMsgToDispatchExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiCCForwardMsgToDispatchExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    CtiCController::Instance()->sendMessageToDispatch(_ctiMessage->replicateMessage());
}


/*===========================================================================
    CtiCCPointDataMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiCCPointDataMsgExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    long pointID = _pointDataMsg->getId();
    double value = _pointDataMsg->getValue();
    unsigned tags = _pointDataMsg->getTags();
    RWTime& timestamp = _pointDataMsg->getTime();

    if( _CAP_DEBUG )
	{
        char tempchar[64];
        RWCString outString = "Point data message received from a client. ID:";
        _ltoa(pointID,tempchar,10);
        outString += tempchar;
        outString += " Value:";
        int precision = 3;
        sprintf(tempchar,"%.*f",precision,value);
        outString += tempchar;
        outString += " Tags:";
        _ultoa(tags,tempchar,10);
        outString += tempchar;
        outString += " Timestamp: ";
        outString += RWDBDateTime(timestamp).asString();

        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << outString.data() << endl;
	}

    bool found = FALSE;
    CtiCCStrategyStore* store = CtiCCStrategyStore::Instance();

    RWOrdered &strategies = store->Strategies();

    for(int i=0;i<strategies.entries();i++)
    {
        CtiCCStrategy *currentStrategy = (CtiCCStrategy*)strategies[i];

        if( !found )
        {
            RWOrdered &capBanks = currentStrategy->CapBankList();

            if( pointID == currentStrategy->CalculatedVarPointId() ||
                pointID == currentStrategy->ActualVarPointId() )
            {
                found = TRUE;
                break;
            }
            for(int j=0;j<capBanks.entries();j++)
            {
                CtiCapBank *currentCapBank = (CtiCapBank*)capBanks[j];

                if( currentCapBank->StatusPointId() == pointID )
                {
                    if( currentCapBank->ControlStatus() != (ULONG)value )
                    {
                        if( currentStrategy->RecentlyControlled() &&
                            currentStrategy->LastCapBankControlled() == currentCapBank->Id() )
                        {
                            currentStrategy->setRecentlyControlled(FALSE);
                        }
                        currentStrategy->setStrategyUpdated(TRUE);
                        currentCapBank->setControlStatus((ULONG)value);
                        currentCapBank->setTagsControlStatus((ULONG)tags);
                        currentCapBank->setLastStatusChangeTime(timestamp);
                        currentStrategy->figureActualVarPointValue();
                        if( currentStrategy->ActualVarPointId() > 0 )
                            CtiCController::Instance()->sendMessageToDispatch(new CtiPointDataMsg(currentStrategy->ActualVarPointId(),currentStrategy->ActualVarPointValue(),NormalQuality,AnalogPointType));
                    }
                    found = TRUE;
                    break;
                }
                else if( currentCapBank->OperationAnalogPointId() == pointID )
                {
                    found = TRUE;
                    break;
                }
            }
        }
        else if( found )
        {
            break;
        }
    }

    CtiCController::Instance()->sendMessageToDispatch(_pointDataMsg->replicateMessage());
}


/*===========================================================================
    CtiCCShutdown
===========================================================================*/

/*---------------------------------------------------------------------------
    Execute
    
    Executes a shutdown on the server
---------------------------------------------------------------------------*/
void CtiCCShutdownExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results )
{
    /*if( _CAP_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << "Shutting down client connection thread..." << endl;
    }*/

    CtiCCServer::Instance()->stop();

    /*if( _CAP_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << "Shutting down the controller thread..." << endl;
    }*/

    CtiCController::Instance()->stop();
   
    /*if( _CAP_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << "Shutting down the strategystore..." << endl;
    }*/

    CtiCCStrategyStore::DeleteInstance();

    /*if( _CAP_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime().asString() << " - " << "Done shutting down" << endl;
        dout << RWTime().asString() << " - " << "done with shutdown executor" << endl;
    }*/
}

/*===========================================================================
    CtiCCExecutorFactory
===========================================================================*/
CtiCCExecutor* CtiCCExecutorFactory::createExecutor(const CtiMessage* message)
{
    CtiCCExecutor* ret_val = NULL;
    UINT classId = message->isA();

    switch ( classId )
    {
        case CTICCSTRATEGYLIST_MSG_ID:
            ret_val = new CtiCCStrategyListMsgExecutor( (CtiCCStrategyListMsg*)message );
            break;
    
        case CTICCCOMMAND_ID:
            ret_val = new CtiCCCommandExecutor( (CtiCCCommand*)message );
            break;
    
        case CTICCSTATELIST_MSG_ID:
            ret_val = new CtiCCStateListMsgExecutor( (CtiCCStateListMsg*)message );
            break;
    
        case CTICCAREALIST_MSG_ID:
            ret_val = new CtiCCAreaListMsgExecutor( (CtiCCAreaListMsg*)message );
            break;
    
        case MSG_POINTDATA:
            ret_val = new CtiCCPointDataMsgExecutor( (CtiPointDataMsg*)message );
            break;
    
        case MSG_COMMAND:
            ret_val = new CtiCCForwardMsgToDispatchExecutor( (CtiMessage*)message );
            break;
    
        case CTICCSHUTDOWN_ID:
            ret_val = new CtiCCShutdownExecutor();
            break;
    
        default:
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime().asString() << " - " << "CtiCCExecutorFactory::createExecutor - Warning unknown classId: " << classId << endl;
            }
    }

    return ret_val;
}
