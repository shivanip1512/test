/*-----------------------------------------------------------------------------
    Filename:  ccexecutor.cpp
    
    Programmer:  Josh Wolberg
    
    Description:    Defines Cap Control executor classes.

    Initial Date:  8/30/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/

#include "ccexecutor.h"
#include "ccserver.h"
#include "ccsubstationbusstore.h"
#include "capcontroller.h"
#include "ccid.h"
                     
#include <rw/collstr.h>

extern BOOL _CC_DEBUG;

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
    switch ( _command->getCommand() )
    {
    case CtiCCCommand::DISABLE_SUBSTATION_BUS:
        DisableSubstationBus(results);
        break;

    case CtiCCCommand::ENABLE_SUBSTATION_BUS:
        EnableSubstationBus(results);
        break;

    case CtiCCCommand::DISABLE_FEEDER:
        DisableFeeder(results);
        break;

    case CtiCCCommand::ENABLE_FEEDER:
        EnableFeeder(results);
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

    case CtiCCCommand::REQUEST_ALL_SUBSTATION_BUSES:
        SendAllSubstationBuses(results);
        break;

    default:
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - unknown command type in: " << __FILE__ << " at: " << __LINE__ << endl;
        }

    }

}

/*---------------------------------------------------------------------------
    EnableSubstation
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::EnableSubstationBus(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    
    ULONG subID = _command->getId();
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(ULONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        if( subID == currentSubstationBus->getPAOId() )
        {
            currentSubstationBus->setDisableFlag(FALSE);
            currentSubstationBus->setBusUpdatedFlag(TRUE);
            store->UpdateBusDisableFlagInDB(currentSubstationBus);
            RWCString text = RWCString("Substation Bus Enabled");
            RWCString additional = RWCString("Bus: ");
            additional += currentSubstationBus->getPAOName();
            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
            break;
        }
    }
}

/*---------------------------------------------------------------------------
    DisableSubstation
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::DisableSubstationBus(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    ULONG subID = _command->getId();
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(ULONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        if( subID == currentSubstationBus->getPAOId() )
        {
            currentSubstationBus->setDisableFlag(TRUE);
            currentSubstationBus->setBusUpdatedFlag(TRUE);
            store->UpdateBusDisableFlagInDB(currentSubstationBus);
            RWCString text = RWCString("Substation Bus Disabled");
            RWCString additional = RWCString("Bus: ");
            additional += currentSubstationBus->getPAOName();
            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
            break;
        }
    }
}

/*---------------------------------------------------------------------------
    EnableFeeder
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::EnableFeeder(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    
    ULONG feederID = _command->getId();
    BOOL found = FALSE;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(ULONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();
        for(ULONG j=0;j<ccFeeders.entries();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            if( feederID == currentFeeder->getPAOId() )
            {
                currentFeeder->setDisableFlag(FALSE);
                currentSubstationBus->setBusUpdatedFlag(TRUE);
                store->UpdateFeederDisableFlagInDB(currentFeeder);
                RWCString text = RWCString("Feeder Enabled");
                RWCString additional = RWCString("Feeder: ");
                additional += currentFeeder->getPAOName();
                CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));

                found = TRUE;
                break;
            }
        }
        if( found )
            break;
    }
}

/*---------------------------------------------------------------------------
    DisableFeeder
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::DisableFeeder(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    
    ULONG feederID = _command->getId();
    BOOL found = FALSE;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(ULONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();
        for(ULONG j=0;j<ccFeeders.entries();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            if( feederID == currentFeeder->getPAOId() )
            {
                currentFeeder->setDisableFlag(TRUE);
                currentSubstationBus->setBusUpdatedFlag(TRUE);
                store->UpdateFeederDisableFlagInDB(currentFeeder);
                RWCString text = RWCString("Feeder Disabled");
                RWCString additional = RWCString("Feeder: ");
                additional += currentFeeder->getPAOName();
                CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));

                found = TRUE;
                break;
            }
        }
        if( found )
            break;
    }
}

/*---------------------------------------------------------------------------
    EnableCapBank
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::EnableCapBank(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    
    ULONG capBankID = _command->getId();
    BOOL found = FALSE;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(ULONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();
        for(ULONG j=0;j<ccFeeders.entries();j++)
        {
            RWOrdered& ccCapBanks = ((CtiCCFeeder*)ccFeeders[j])->getCCCapBanks();

            for(ULONG k=0;k<ccCapBanks.entries();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( capBankID == currentCapBank->getPAOId() )
                {
                    currentCapBank->setDisableFlag(FALSE);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    store->UpdateCapBankDisableFlagInDB(currentCapBank);
                    RWCString text = RWCString("Cap Bank Enabled");
                    RWCString additional = RWCString("Cap Bank: ");
                    additional += currentCapBank->getPAOName();
                    if( currentCapBank->getStatusPointId() > 0 )
                    {
                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getStatusPointId(),0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                      << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;

                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                    }

                    found = TRUE;
                    break;
                }
            }
            if( found )
                break;
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
    
    ULONG capBankID = _command->getId();
    BOOL found = FALSE;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(ULONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();
        for(ULONG j=0;j<ccFeeders.entries();j++)
        {
            RWOrdered& ccCapBanks = ((CtiCCFeeder*)ccFeeders[j])->getCCCapBanks();

            for(ULONG k=0;k<ccCapBanks.entries();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( capBankID == currentCapBank->getPAOId() )
                {
                    currentCapBank->setDisableFlag(TRUE);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    store->UpdateCapBankDisableFlagInDB(currentCapBank);
                    RWCString text = RWCString("Cap Bank Disabled");
                    RWCString additional = RWCString("Cap Bank: ");
                    additional += currentCapBank->getPAOName();
                    if( currentCapBank->getStatusPointId() > 0 )
                    {
                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getStatusPointId(),0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                      << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;

                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                    }

                    found = TRUE;
                    break;
                }
            }
            if( found )
                break;
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
    ULONG bankID = _command->getId();
    BOOL found = FALSE;
    BOOL savedBusRecentlyControlledFlag = FALSE;
    BOOL savedFeederRecentlyControlledFlag = FALSE;
    RWDBDateTime savedBusLastOperationTime = RWDBDateTime(1990,1,1,0,0,0,0);
    RWDBDateTime savedFeederLastOperationTime = RWDBDateTime(1990,1,1,0,0,0,0);
    CtiMultiMsg* multi = new CtiMultiMsg();
    RWOrdered& pointChanges = multi->getData();
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(ULONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();
        for(ULONG j=0;j<ccFeeders.entries();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

            for(ULONG k=0;k<ccCapBanks.entries();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( bankID == currentCapBank->getControlDeviceId() )
                {
                    savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                    savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                    savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                    savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                    currentSubstationBus->setRecentlyControlledFlag(FALSE);
                    currentFeeder->setRecentlyControlledFlag(FALSE);
                    controlID = currentCapBank->getControlDeviceId();
                    currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPAOId());
                    currentSubstationBus->setLastOperationTime(RWDBDateTime());
                    currentFeeder->setLastOperationTime(RWDBDateTime());
                    currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                    currentSubstationBus->figureEstimatedVarLoadPointValue();
                    currentSubstationBus->setCurrentDailyOperations(currentSubstationBus->getCurrentDailyOperations() + 1);
                    currentFeeder->setCurrentDailyOperations(currentFeeder->getCurrentDailyOperations() + 1);
                    currentCapBank->setCurrentDailyOperations(currentCapBank->getCurrentDailyOperations() + 1);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue());
                    if( currentCapBank->getStatusPointId() > 0 )
                    {
                        RWCString text = RWCString("Manual Open Sent");
                        RWCString additional = RWCString("Sub: ");
                        additional += currentSubstationBus->getPAOName();
                        pointChanges.insert(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                        if( !savedBusRecentlyControlledFlag ||
                            (currentSubstationBus->getControlMethod() == CtiCCSubstationBus::IndividualFeederControlMethod && !savedFeederRecentlyControlledFlag) )
                        {
                            pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(2);
                            currentCapBank->setLastStatusChangeTime(RWDBDateTime());
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                      << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                    }

                    if( currentCapBank->getOperationAnalogPointId() > 0 )
                    {
                        pointChanges.insert(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getCurrentDailyOperations(),NormalQuality,AnalogPointType));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
                    }

                    found = TRUE;

                    BOOL confirmImmediately = FALSE;
                    if( currentSubstationBus->getControlMethod() == CtiCCSubstationBus::IndividualFeederControlMethod )
                    {
                        if( savedFeederRecentlyControlledFlag ||
                            ((savedFeederLastOperationTime.seconds()+2) >= currentFeeder->getLastOperationTime()) )
                        {
                            confirmImmediately = TRUE;
                        }
                    }
                    else if( currentSubstationBus->getControlMethod() == CtiCCSubstationBus::SubstationBusControlMethod ||
                             currentSubstationBus->getControlMethod() == CtiCCSubstationBus::BusOptimizedFeederControlMethod )
                    {
                        if( savedBusRecentlyControlledFlag ||
                            ((savedBusLastOperationTime.seconds()+2) >= currentSubstationBus->getLastOperationTime()) )
                        {
                            confirmImmediately = TRUE;
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Unknown Control Method: " << currentSubstationBus->getControlMethod()
                                      << " PAOID: " << currentSubstationBus->getPAOId() << " Name: " << currentSubstationBus->getPAOName() << endl;
                    }

                    if( !confirmImmediately )
                    {
                        currentSubstationBus->setRecentlyControlledFlag(TRUE);
                        currentFeeder->setRecentlyControlledFlag(TRUE);
                    }
                    else
                    {
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                            {
                                currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Close,NormalQuality,StatusPointType));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                                currentCapBank->setLastStatusChangeTime(RWDBDateTime());
                            }
                            else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                            {
                                currentCapBank->setControlStatus(CtiCCCapBank::Open);
                                pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Open,NormalQuality,StatusPointType));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                                currentCapBank->setLastStatusChangeTime(RWDBDateTime());
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                          << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                        }
                        currentSubstationBus->setRecentlyControlledFlag(FALSE);
                        currentFeeder->setRecentlyControlledFlag(FALSE);
                        if( currentSubstationBus->getControlMethod() == CtiCCSubstationBus::IndividualFeederControlMethod )
                        {
                            for(ULONG x=0;x<ccFeeders.entries();x++)
                            {
                                if( ((CtiCCFeeder*)ccFeeders[x])->getRecentlyControlledFlag() )
                                {
                                    currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        if( found )
        {
            break;
        }
    }

    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"control open");
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    CloseCapBank
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::CloseCapBank(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    
    ULONG controlID = 0;
    ULONG bankID = _command->getId();
    BOOL found = FALSE;
    BOOL savedBusRecentlyControlledFlag = FALSE;
    BOOL savedFeederRecentlyControlledFlag = FALSE;
    RWDBDateTime savedBusLastOperationTime = RWDBDateTime(1990,1,1,0,0,0,0);
    RWDBDateTime savedFeederLastOperationTime = RWDBDateTime(1990,1,1,0,0,0,0);
    CtiMultiMsg* multi = new CtiMultiMsg();
    RWOrdered& pointChanges = multi->getData();
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(ULONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();
        for(ULONG j=0;j<ccFeeders.entries();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
            
            for(ULONG k=0;k<ccCapBanks.entries();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( bankID == currentCapBank->getControlDeviceId() )
                {
                    savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                    savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                    savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                    savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                    currentSubstationBus->setRecentlyControlledFlag(FALSE);
                    currentFeeder->setRecentlyControlledFlag(FALSE);
                    controlID = currentCapBank->getControlDeviceId();
                    currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPAOId());
                    currentSubstationBus->setLastOperationTime(RWDBDateTime());
                    currentFeeder->setLastOperationTime(RWDBDateTime());
                    currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);
                    currentSubstationBus->figureEstimatedVarLoadPointValue();
                    currentSubstationBus->setCurrentDailyOperations(currentSubstationBus->getCurrentDailyOperations() + 1);
                    currentFeeder->setCurrentDailyOperations(currentFeeder->getCurrentDailyOperations() + 1);
                    currentCapBank->setCurrentDailyOperations(currentCapBank->getCurrentDailyOperations() + 1);
                    currentSubstationBus->setRecentlyControlledFlag(TRUE);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue());
                    if( currentCapBank->getStatusPointId() > 0 )
                    {
                        RWCString text = RWCString("Manual Close Sent");
                        RWCString additional = RWCString("Sub: ");
                        additional += currentSubstationBus->getPAOName();
                        pointChanges.insert(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                        if( !savedBusRecentlyControlledFlag ||
                            (currentSubstationBus->getControlMethod() == CtiCCSubstationBus::IndividualFeederControlMethod && !savedFeederRecentlyControlledFlag) )
                        {
                            pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(2);
                            currentCapBank->setLastStatusChangeTime(RWDBDateTime());
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                      << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                    }

                    if( currentCapBank->getOperationAnalogPointId() > 0 )
                    {
                        pointChanges.insert(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getCurrentDailyOperations(),NormalQuality,AnalogPointType));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
                    }

                    found = TRUE;

                    BOOL confirmImmediately = FALSE;
                    if( currentSubstationBus->getControlMethod() == CtiCCSubstationBus::IndividualFeederControlMethod )
                    {
                        if( savedFeederRecentlyControlledFlag ||
                            ((savedFeederLastOperationTime.seconds()+2) >= currentFeeder->getLastOperationTime()) )
                        {
                            confirmImmediately = TRUE;
                        }
                    }
                    else if( currentSubstationBus->getControlMethod() == CtiCCSubstationBus::SubstationBusControlMethod ||
                             currentSubstationBus->getControlMethod() == CtiCCSubstationBus::BusOptimizedFeederControlMethod )
                    {
                        if( savedBusRecentlyControlledFlag ||
                            ((savedBusLastOperationTime.seconds()+2) >= currentSubstationBus->getLastOperationTime()) )
                        {
                            confirmImmediately = TRUE;
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Unknown Control Method: " << currentSubstationBus->getControlMethod()
                                      << " PAOID: " << currentSubstationBus->getPAOId() << " Name: " << currentSubstationBus->getPAOName() << endl;
                    }

                    if( !confirmImmediately )
                    {
                        currentSubstationBus->setRecentlyControlledFlag(TRUE);
                        currentFeeder->setRecentlyControlledFlag(TRUE);
                    }
                    else
                    {
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                            {
                                currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Close,NormalQuality,StatusPointType));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                                currentCapBank->setLastStatusChangeTime(RWDBDateTime());
                            }
                            else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                            {
                                currentCapBank->setControlStatus(CtiCCCapBank::Open);
                                pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Open,NormalQuality,StatusPointType));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                                currentCapBank->setLastStatusChangeTime(RWDBDateTime());
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                          << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                        }
                        currentSubstationBus->setRecentlyControlledFlag(FALSE);
                        currentFeeder->setRecentlyControlledFlag(FALSE);
                        if( currentSubstationBus->getControlMethod() == CtiCCSubstationBus::IndividualFeederControlMethod )
                        {
                            for(ULONG x=0;x<ccFeeders.entries();x++)
                            {
                                if( ((CtiCCFeeder*)ccFeeders[x])->getRecentlyControlledFlag() )
                                {
                                    currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        if( found )
        {
            break;
        }
    }

    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"control close");
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    ConfirmOpen
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::ConfirmOpen(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    
    ULONG controlID = 0;
    ULONG bankID = _command->getId();
    BOOL found = FALSE;
    BOOL savedBusRecentlyControlledFlag = FALSE;
    BOOL savedFeederRecentlyControlledFlag = FALSE;
    RWDBDateTime savedBusLastOperationTime = RWDBDateTime(1990,1,1,0,0,0,0);
    RWDBDateTime savedFeederLastOperationTime = RWDBDateTime(1990,1,1,0,0,0,0);
    CtiMultiMsg* multi = new CtiMultiMsg();
    RWOrdered& pointChanges = multi->getData();
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(ULONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

        for(ULONG j=0;j<ccFeeders.entries();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

            for(ULONG k=0;k<ccCapBanks.entries();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( bankID == currentCapBank->getControlDeviceId() )
                {
                    savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                    savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                    savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                    savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                    currentSubstationBus->setRecentlyControlledFlag(FALSE);
                    currentFeeder->setRecentlyControlledFlag(FALSE);
                    controlID = currentCapBank->getControlDeviceId();
                    currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPAOId());
                    currentSubstationBus->setLastOperationTime(RWDBDateTime());
                    currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                    currentSubstationBus->figureEstimatedVarLoadPointValue();
                    //currentSubstationBus->setCurrentDailyOperations(currentSubstationBus->getCurrentDailyOperations() + 1);
                    //currentFeeder->setCurrentDailyOperations(currentFeeder->getCurrentDailyOperations() + 1);
                    //currentCapBank->setCurrentDailyOperations(currentCapBank->getCurrentDailyOperations() + 1);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue());
                    if( currentCapBank->getStatusPointId() > 0 )
                    {
                        RWCString text = RWCString("Manual Confirm Open Sent");
                        RWCString additional = RWCString("Sub: ");
                        additional += currentSubstationBus->getPAOName();
                        pointChanges.insert(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                        if( !(savedFeederRecentlyControlledFlag || savedBusRecentlyControlledFlag) )
                        {
                            pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(2);
                            currentCapBank->setLastStatusChangeTime(RWDBDateTime());
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                      << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                    }

                    if( currentCapBank->getOperationAnalogPointId() > 0 )
                    {
                        pointChanges.insert(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getCurrentDailyOperations(),NormalQuality,AnalogPointType));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
                    }

                    found = TRUE;

                    BOOL confirmImmediately = FALSE;
                    if( currentSubstationBus->getControlMethod() == CtiCCSubstationBus::IndividualFeederControlMethod )
                    {
                        if( savedFeederRecentlyControlledFlag ||
                            ((savedFeederLastOperationTime.seconds()+2) >= currentFeeder->getLastOperationTime()) )
                        {
                            confirmImmediately = TRUE;
                        }
                    }
                    else if( currentSubstationBus->getControlMethod() == CtiCCSubstationBus::SubstationBusControlMethod ||
                             currentSubstationBus->getControlMethod() == CtiCCSubstationBus::BusOptimizedFeederControlMethod )
                    {
                        if( savedBusRecentlyControlledFlag ||
                            ((savedBusLastOperationTime.seconds()+2) >= currentSubstationBus->getLastOperationTime()) )
                        {
                            confirmImmediately = TRUE;
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Unknown Control Method: " << currentSubstationBus->getControlMethod()
                                      << " PAOID: " << currentSubstationBus->getPAOId() << " Name: " << currentSubstationBus->getPAOName() << endl;
                    }

                    if( !confirmImmediately )
                    {
                        currentSubstationBus->setRecentlyControlledFlag(TRUE);
                        currentFeeder->setRecentlyControlledFlag(TRUE);
                    }
                    else
                    {
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                            {
                                currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Close,NormalQuality,StatusPointType));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                                currentCapBank->setLastStatusChangeTime(RWDBDateTime());
                            }
                            else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                            {
                                currentCapBank->setControlStatus(CtiCCCapBank::Open);
                                pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Open,NormalQuality,StatusPointType));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                                currentCapBank->setLastStatusChangeTime(RWDBDateTime());
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                          << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                        }
                        currentSubstationBus->setRecentlyControlledFlag(FALSE);
                        currentFeeder->setRecentlyControlledFlag(FALSE);
                        if( currentSubstationBus->getControlMethod() == CtiCCSubstationBus::IndividualFeederControlMethod )
                        {
                            for(ULONG x=0;x<ccFeeders.entries();x++)
                            {
                                if( ((CtiCCFeeder*)ccFeeders[x])->getRecentlyControlledFlag() )
                                {
                                    currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        if( found )
        {
            break;
        }
    }

    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"control open");
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    ConfirmClose
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::ConfirmClose(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    
    ULONG controlID = 0;
    ULONG bankID = _command->getId();
    BOOL found = FALSE;
    BOOL savedBusRecentlyControlledFlag = FALSE;
    BOOL savedFeederRecentlyControlledFlag = FALSE;
    RWDBDateTime savedBusLastOperationTime = RWDBDateTime(1990,1,1,0,0,0,0);
    RWDBDateTime savedFeederLastOperationTime = RWDBDateTime(1990,1,1,0,0,0,0);
    CtiMultiMsg* multi = new CtiMultiMsg();
    RWOrdered& pointChanges = multi->getData();
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(ULONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

        for(ULONG j=0;j<ccFeeders.entries();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

            for(ULONG k=0;k<ccCapBanks.entries();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( bankID == currentCapBank->getControlDeviceId() )
                {
                    savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                    savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                    savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                    savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                    currentSubstationBus->setRecentlyControlledFlag(FALSE);
                    currentFeeder->setRecentlyControlledFlag(FALSE);
                    controlID = currentCapBank->getControlDeviceId();
                    currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPAOId());
                    currentSubstationBus->setLastOperationTime(RWDBDateTime());
                    currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);
                    currentSubstationBus->figureEstimatedVarLoadPointValue();
                    //currentSubstationBus->setCurrentDailyOperations(currentSubstationBus->getCurrentDailyOperations() + 1);
                    //currentFeeder->setCurrentDailyOperations(currentFeeder->getCurrentDailyOperations() + 1);
                    //currentCapBank->setCurrentDailyOperations(currentCapBank->getCurrentDailyOperations() + 1);
                    currentSubstationBus->setRecentlyControlledFlag(TRUE);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue());
                    if( currentCapBank->getStatusPointId() > 0 )
                    {
                        RWCString text = RWCString("Manually Confirm Close Sent");
                        RWCString additional = RWCString("Sub: ");
                        additional += currentSubstationBus->getPAOName();
                        pointChanges.insert(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                        if( !savedBusRecentlyControlledFlag ||
                            (currentSubstationBus->getControlMethod() == CtiCCSubstationBus::IndividualFeederControlMethod && !savedFeederRecentlyControlledFlag) )
                        {
                            pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(2);
                            currentCapBank->setLastStatusChangeTime(RWDBDateTime());
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                      << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                    }

                    if( currentCapBank->getOperationAnalogPointId() > 0 )
                    {
                        pointChanges.insert(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getCurrentDailyOperations(),NormalQuality,AnalogPointType));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
                    }

                    found = TRUE;

                    BOOL confirmImmediately = FALSE;
                    if( currentSubstationBus->getControlMethod() == CtiCCSubstationBus::IndividualFeederControlMethod )
                    {
                        if( savedFeederRecentlyControlledFlag ||
                            ((savedFeederLastOperationTime.seconds()+2) >= currentFeeder->getLastOperationTime()) )
                        {
                            confirmImmediately = TRUE;
                        }
                    }
                    else if( currentSubstationBus->getControlMethod() == CtiCCSubstationBus::SubstationBusControlMethod ||
                             currentSubstationBus->getControlMethod() == CtiCCSubstationBus::BusOptimizedFeederControlMethod )
                    {
                        if( savedBusRecentlyControlledFlag ||
                            ((savedBusLastOperationTime.seconds()+2) >= currentSubstationBus->getLastOperationTime()) )
                        {
                            confirmImmediately = TRUE;
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << RWTime() << " - Unknown Control Method: " << currentSubstationBus->getControlMethod()
                                      << " PAOID: " << currentSubstationBus->getPAOId() << " Name: " << currentSubstationBus->getPAOName() << endl;
                    }

                    if( !confirmImmediately )
                    {
                        currentSubstationBus->setRecentlyControlledFlag(TRUE);
                        currentFeeder->setRecentlyControlledFlag(TRUE);
                    }
                    else
                    {
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                            {
                                currentCapBank->setControlStatus(CtiCCCapBank::Close);
                                pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Close,NormalQuality,StatusPointType));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                                currentCapBank->setLastStatusChangeTime(RWDBDateTime());
                            }
                            else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                            {
                                currentCapBank->setControlStatus(CtiCCCapBank::Open);
                                pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Open,NormalQuality,StatusPointType));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                                currentCapBank->setLastStatusChangeTime(RWDBDateTime());
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                          << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                        }
                        currentSubstationBus->setRecentlyControlledFlag(FALSE);
                        currentFeeder->setRecentlyControlledFlag(FALSE);
                        if( currentSubstationBus->getControlMethod() == CtiCCSubstationBus::IndividualFeederControlMethod )
                        {
                            for(ULONG x=0;x<ccFeeders.entries();x++)
                            {
                                if( ((CtiCCFeeder*)ccFeeders[x])->getRecentlyControlledFlag() )
                                {
                                    currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        if( found )
        {
            break;
        }
    }

    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"control close");
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    SendAllSubstationBuses
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::SendAllSubstationBuses(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    CtiCCExecutorFactory f;
    RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue = new CtiCountedPCPtrQueue<RWCollectable>();
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg(*(store->getCCSubstationBuses(RWDBDateTime().seconds()))));
    executor->Execute(queue);
    delete executor;
}


/*===========================================================================
    CtiCCSubstationBusMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiCCSubstationBusMsgExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    CtiCCServer::getInstance()->Broadcast(_ccSubstationBusesMsg->replicateMessage());
}


/*===========================================================================
    CtiCCCapBankStatesMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiCCCapBankStatesMsgExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    CtiCCServer::getInstance()->Broadcast(_ccCapBankStatesMsg->replicateMessage());
}


/*===========================================================================
    CtiCCGeoAreasMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiCCGeoAreasMsgExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    CtiCCServer::getInstance()->Broadcast(_ccGeoAreasMsg->replicateMessage());
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
    CtiCapController::getInstance()->sendMessageToDispatch(_ctiMessage->replicateMessage());
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

    //if( _CC_DEBUG )
	{
        char tempchar[80];
        RWCString outString = "Manual point value received from client, ID:";
        _ltoa(pointID,tempchar,10);
        outString += tempchar;
        outString += " Val:";
        int precision = 3;
        _snprintf(tempchar,80,"%.*f",precision,value);
        outString += tempchar;
        outString += " Tags:";
        _ultoa(tags,tempchar,10);
        outString += tempchar;
        outString += " Timestamp: ";
        outString += RWDBDateTime(timestamp).asString();

        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - " << outString.data() << endl;
	}

    BOOL found = FALSE;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(int i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];

        if( !found )
        {
            if( pointID == currentSubstationBus->getCurrentVarLoadPointId() ||
                pointID == currentSubstationBus->getEstimatedVarLoadPointId() )
            {
                found = TRUE;
                break;
            }

            RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

            for(int j=0;j<ccFeeders.entries();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
                RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

                for(int k=0;k<ccCapBanks.entries();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                    if( currentCapBank->getStatusPointId() == pointID )
                    {
                        if( currentCapBank->getControlStatus() != (ULONG)value )
                        {
                            if( currentSubstationBus->getRecentlyControlledFlag() &&
                                currentFeeder->getLastCapBankControlledDeviceId() == currentCapBank->getPAOId() )
                            {
                                currentSubstationBus->setRecentlyControlledFlag(FALSE);
                                currentFeeder->setRecentlyControlledFlag(FALSE);
                                if( currentSubstationBus->getControlMethod() == CtiCCSubstationBus::IndividualFeederControlMethod )
                                {
                                    for(ULONG x=0;x<ccFeeders.entries();x++)
                                    {
                                        if( ((CtiCCFeeder*)ccFeeders[x])->getRecentlyControlledFlag() )
                                        {
                                            currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                            break;
                                        }
                                    }
                                }
                            }
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            currentCapBank->setControlStatus((ULONG)value);
                            currentCapBank->setTagsControlStatus((ULONG)tags);
                            currentCapBank->setLastStatusChangeTime(timestamp);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            if( currentSubstationBus->getEstimatedVarLoadPointId() > 0 )
                                CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedVarLoadPointId(),currentSubstationBus->getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                        }
                        found = TRUE;
                        break;
                    }
                    else if( currentCapBank->getOperationAnalogPointId() == pointID )
                    {
                        found = TRUE;
                        break;
                    }
                }
                if( found )
                {
                    break;
                }
            }
        }
        else if( found )
        {
            break;
        }
    }

    CtiCapController::getInstance()->sendMessageToDispatch(_pointDataMsg->replicateMessage());
}


/*===========================================================================
    CtiCCMultiMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiCCMultiMsgExecutor::Execute(RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > results)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);

    CtiCCExecutorFactory f;
    RWOrdered& messages = _multiMsg->getData();
    while(messages.entries( )>0)
    {
        RWCountedPointer< CtiCountedPCPtrQueue<RWCollectable> > queue = new CtiCountedPCPtrQueue<RWCollectable>();
        CtiMessage* message = (CtiMessage*)(messages.pop());
        if( message != NULL )
        {
            CtiCCExecutor* executor = f.createExecutor(message);
            executor->Execute(queue);
            delete executor;
        }
        else
        {
            break;
        }
    }
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
    /*if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Shutting down client connection thread..." << endl;
    }*/

    CtiCCServer::getInstance()->stop();

    /*if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Shutting down the controller thread..." << endl;
    }*/

    CtiCapController::getInstance()->stop();
   
    /*if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Shutting down the substationbusstore..." << endl;
    }*/

    CtiCCSubstationBusStore::deleteInstance();

    /*if( _CC_DEBUG )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Done shutting down" << endl;
        dout << RWTime() << " - done with shutdown executor" << endl;
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
        case CTICCSUBSTATIONBUS_MSG_ID:
            ret_val = new CtiCCSubstationBusMsgExecutor( (CtiCCSubstationBusMsg*)message );
            break;
    
        case CTICCCOMMAND_ID:
            ret_val = new CtiCCCommandExecutor( (CtiCCCommand*)message );
            break;
    
        case CTICCCAPBANKSTATES_MSG_ID:
            ret_val = new CtiCCCapBankStatesMsgExecutor( (CtiCCCapBankStatesMsg*)message );
            break;
    
        case CTICCGEOAREAS_MSG_ID:
            ret_val = new CtiCCGeoAreasMsgExecutor( (CtiCCGeoAreasMsg*)message );
            break;
    
        case MSG_POINTDATA:
            ret_val = new CtiCCPointDataMsgExecutor( (CtiPointDataMsg*)message );
            break;
    
        case MSG_MULTI:
            ret_val = new CtiCCMultiMsgExecutor( (CtiMultiMsg*)message );
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
                dout << RWTime() << " - CtiCCExecutorFactory::createExecutor - Warning unknown classId: " << classId << endl;
            }
    }

    return ret_val;
}
