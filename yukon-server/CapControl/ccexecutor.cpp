/*-----------------------------------------------------------------------------
    Filename:  ccexecutor.cpp
    
    Programmer:  Josh Wolberg
    
    Description:    Defines Cap Control executor classes.

    Initial Date:  8/30/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/

#include "ccclientlistener.h"
#include "ccexecutor.h"
#include "ccsubstationbusstore.h"
#include "capcontroller.h"
#include "ccid.h"
                     
#include <rw/collstr.h>

extern ULONG _CC_DEBUG;
extern BOOL _IGNORE_NOT_NORMAL_FLAG;
extern ULONG _SEND_TRIES;

/*===========================================================================
    CtiCCCommandExecutor
===========================================================================*/

/*---------------------------------------------------------------------------
    Execute
    
    Executes the command and places any resulting messages on the result
    queue.
---------------------------------------------------------------------------*/
void CtiCCCommandExecutor::Execute()
{
    switch ( _command->getCommand() )
    {
    case CtiCCCommand::DISABLE_SUBSTATION_BUS:
        DisableSubstationBus();
        break;

    case CtiCCCommand::ENABLE_SUBSTATION_BUS:
        EnableSubstationBus();
        break;

    case CtiCCCommand::DISABLE_FEEDER:
        DisableFeeder();
        break;

    case CtiCCCommand::ENABLE_FEEDER:
        EnableFeeder();
        break;

    case CtiCCCommand::DISABLE_CAPBANK:
        DisableCapBank();
        break;

    case CtiCCCommand::ENABLE_CAPBANK:
        EnableCapBank();
        break;

    case CtiCCCommand::OPEN_CAPBANK:
        OpenCapBank();
        break;

    case CtiCCCommand::CLOSE_CAPBANK:
        CloseCapBank();
        break;

    case CtiCCCommand::CONFIRM_OPEN:
        ConfirmOpen();
        break;

    case CtiCCCommand::CONFIRM_CLOSE:
        ConfirmClose();
        break;

    case CtiCCCommand::REQUEST_ALL_SUBSTATION_BUSES:
        SendAllSubstationBuses();
        break;

    case CtiCCCommand::RETURN_CAP_TO_ORIGINAL_FEEDER:
        ReturnCapToOriginalFeeder();
        break;

    case CtiCCCommand::RESET_DAILY_OPERATIONS:
        ResetDailyOperations();
        break;

    case CtiCCCommand::WAIVE_SUBSTATION_BUS:
        WaiveSubstationBus();
        break;

    case CtiCCCommand::UNWAIVE_SUBSTATION_BUS:
        UnwaiveSubstationBus();
        break;

    case CtiCCCommand::WAIVE_FEEDER:
        WaiveFeeder();
        break;

    case CtiCCCommand::UNWAIVE_FEEDER:
        UnwaiveFeeder();
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
void CtiCCCommandExecutor::EnableSubstationBus()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG subID = _command->getId();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.entries();i++)
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
void CtiCCCommandExecutor::DisableSubstationBus()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG subID = _command->getId();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.entries();i++)
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
void CtiCCCommandExecutor::EnableFeeder()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG feederID = _command->getId();
    BOOL found = FALSE;
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();
        for(LONG j=0;j<ccFeeders.entries();j++)
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
void CtiCCCommandExecutor::DisableFeeder()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG feederID = _command->getId();
    BOOL found = FALSE;
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();
        for(LONG j=0;j<ccFeeders.entries();j++)
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
void CtiCCCommandExecutor::EnableCapBank()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG capBankID = _command->getId();
    BOOL found = FALSE;
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();
        for(LONG j=0;j<ccFeeders.entries();j++)
        {
            RWOrdered& ccCapBanks = ((CtiCCFeeder*)ccFeeders[j])->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.entries();k++)
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
void CtiCCCommandExecutor::DisableCapBank()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG capBankID = _command->getId();
    BOOL found = FALSE;
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();
        for(LONG j=0;j<ccFeeders.entries();j++)
        {
            RWOrdered& ccCapBanks = ((CtiCCFeeder*)ccFeeders[j])->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.entries();k++)
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
void CtiCCCommandExecutor::OpenCapBank()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG controlID = 0;
    LONG bankID = _command->getId();
    BOOL found = FALSE;
    BOOL savedBusRecentlyControlledFlag = FALSE;
    BOOL savedFeederRecentlyControlledFlag = FALSE;
    RWDBDateTime savedBusLastOperationTime = RWDBDateTime(1990,1,1,0,0,0,0);
    RWDBDateTime savedFeederLastOperationTime = RWDBDateTime(1990,1,1,0,0,0,0);
    CtiMultiMsg* multi = new CtiMultiMsg();
    RWOrdered& pointChanges = multi->getData();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();
        for(LONG j=0;j<ccFeeders.entries();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.entries();k++)
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
                    currentSubstationBus->setLastFeederControlledPAOId(currentFeeder->getPAOId());
                    currentSubstationBus->setLastFeederControlledPosition(j);
                    currentSubstationBus->setLastOperationTime(RWDBDateTime());
                    currentFeeder->setLastOperationTime(RWDBDateTime());
                    currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                    currentSubstationBus->figureEstimatedVarLoadPointValue();
                    currentSubstationBus->setCurrentDailyOperations(currentSubstationBus->getCurrentDailyOperations() + 1);
                    currentFeeder->setCurrentDailyOperations(currentFeeder->getCurrentDailyOperations() + 1);
                    currentCapBank->setTotalOperations(currentCapBank->getTotalOperations() + 1);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue());
                    currentFeeder->setVarValueBeforeControl(currentFeeder->getCurrentVarLoadPointValue());
                    if( currentCapBank->getStatusPointId() > 0 )
                    {
                        RWCString text = RWCString("Manual Open Sent");
                        RWCString additional = RWCString("Sub: ");
                        additional += currentSubstationBus->getPAOName();
                        pointChanges.insert(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                        if( !savedBusRecentlyControlledFlag ||
                            (!currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) && !savedFeederRecentlyControlledFlag) )
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
                        pointChanges.insert(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
                    }

                    found = TRUE;

                    BOOL confirmImmediately = FALSE;
                    if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) )
                    {
                        if( savedFeederRecentlyControlledFlag ||
                            ((savedFeederLastOperationTime.seconds()+2) >= currentFeeder->getLastOperationTime().seconds()) )
                        {
                            confirmImmediately = TRUE;
                        }
                        if( _IGNORE_NOT_NORMAL_FLAG &&
                            currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                        {
                            confirmImmediately = TRUE;
                        }
                    }
                    else if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::SubstationBusControlMethod,RWCString::ignoreCase) ||
                             !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::BusOptimizedFeederControlMethod,RWCString::ignoreCase) )
                    {
                        if( savedBusRecentlyControlledFlag ||
                            ((savedBusLastOperationTime.seconds()+2) >= currentSubstationBus->getLastOperationTime().seconds()) )
                        {
                            confirmImmediately = TRUE;
                        }
                        if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::SubstationBusControlMethod,RWCString::ignoreCase) &&
                            _IGNORE_NOT_NORMAL_FLAG &&
                            currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                        {
                            confirmImmediately = TRUE;
                        }
                        if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::BusOptimizedFeederControlMethod,RWCString::ignoreCase) &&
                            _IGNORE_NOT_NORMAL_FLAG &&
                            currentFeeder->getCurrentVarPointQuality() != NormalQuality )
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
                        doConfirmImmediately(currentSubstationBus,pointChanges);
                    }
                    break;
                }
            }
            if( found )
            {
                break;
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
void CtiCCCommandExecutor::CloseCapBank()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG controlID = 0;
    LONG bankID = _command->getId();
    BOOL found = FALSE;
    BOOL savedBusRecentlyControlledFlag = FALSE;
    BOOL savedFeederRecentlyControlledFlag = FALSE;
    RWDBDateTime savedBusLastOperationTime = RWDBDateTime(1990,1,1,0,0,0,0);
    RWDBDateTime savedFeederLastOperationTime = RWDBDateTime(1990,1,1,0,0,0,0);
    CtiMultiMsg* multi = new CtiMultiMsg();
    RWOrdered& pointChanges = multi->getData();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.entries();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.entries();k++)
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
                    currentSubstationBus->setLastFeederControlledPAOId(currentFeeder->getPAOId());
                    currentSubstationBus->setLastFeederControlledPosition(j);
                    currentSubstationBus->setLastOperationTime(RWDBDateTime());
                    currentFeeder->setLastOperationTime(RWDBDateTime());
                    currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);
                    currentSubstationBus->figureEstimatedVarLoadPointValue();
                    currentSubstationBus->setCurrentDailyOperations(currentSubstationBus->getCurrentDailyOperations() + 1);
                    currentFeeder->setCurrentDailyOperations(currentFeeder->getCurrentDailyOperations() + 1);
                    currentCapBank->setTotalOperations(currentCapBank->getTotalOperations() + 1);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue());
                    currentFeeder->setVarValueBeforeControl(currentFeeder->getCurrentVarLoadPointValue());
                    if( currentCapBank->getStatusPointId() > 0 )
                    {
                        RWCString text = RWCString("Manual Close Sent");
                        RWCString additional = RWCString("Sub: ");
                        additional += currentSubstationBus->getPAOName();
                        pointChanges.insert(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                        if( !savedBusRecentlyControlledFlag ||
                            (!currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) && !savedFeederRecentlyControlledFlag) )
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
                        pointChanges.insert(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
                    }

                    found = TRUE;

                    BOOL confirmImmediately = FALSE;
                    if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) )
                    {
                        if( savedFeederRecentlyControlledFlag ||
                            ((savedFeederLastOperationTime.seconds()+2) >= currentFeeder->getLastOperationTime().seconds()) )
                        {
                            confirmImmediately = TRUE;
                        }
                        if( _IGNORE_NOT_NORMAL_FLAG &&
                            currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                        {
                            confirmImmediately = TRUE;
                        }
                    }
                    else if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::SubstationBusControlMethod,RWCString::ignoreCase) ||
                             !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::BusOptimizedFeederControlMethod,RWCString::ignoreCase) )
                    {
                        if( savedBusRecentlyControlledFlag ||
                            ((savedBusLastOperationTime.seconds()+2) >= currentSubstationBus->getLastOperationTime().seconds()) )
                        {
                            confirmImmediately = TRUE;
                        }
                        if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::SubstationBusControlMethod,RWCString::ignoreCase) &&
                            _IGNORE_NOT_NORMAL_FLAG &&
                            currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                        {
                            confirmImmediately = TRUE;
                        }
                        if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::BusOptimizedFeederControlMethod,RWCString::ignoreCase) &&
                            _IGNORE_NOT_NORMAL_FLAG &&
                            currentFeeder->getCurrentVarPointQuality() != NormalQuality )
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
                        doConfirmImmediately(currentSubstationBus,pointChanges);
                    }
                    break;
                }
            }
            if( found )
            {
                break;
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
void CtiCCCommandExecutor::ConfirmOpen()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG controlID = 0;
    LONG bankID = _command->getId();
    BOOL found = FALSE;
    BOOL savedBusRecentlyControlledFlag = FALSE;
    BOOL savedFeederRecentlyControlledFlag = FALSE;
    LONG savedControlStatus = -10;
    RWDBDateTime savedBusLastOperationTime = RWDBDateTime(1990,1,1,0,0,0,0);
    RWDBDateTime savedFeederLastOperationTime = RWDBDateTime(1990,1,1,0,0,0,0);
    CtiMultiMsg* multi = new CtiMultiMsg();
    RWOrdered& pointChanges = multi->getData();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.entries();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.entries();k++)
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
                    currentSubstationBus->setLastFeederControlledPAOId(currentFeeder->getPAOId());
                    currentSubstationBus->setLastFeederControlledPosition(j);
                    currentSubstationBus->setLastOperationTime(RWDBDateTime());
                    currentFeeder->setLastOperationTime(RWDBDateTime());
                    savedControlStatus = currentCapBank->getControlStatus();
                    currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                    currentSubstationBus->figureEstimatedVarLoadPointValue();
                    //currentSubstationBus->setCurrentDailyOperations(currentSubstationBus->getCurrentDailyOperations() + 1);
                    //currentFeeder->setCurrentDailyOperations(currentFeeder->getCurrentDailyOperations() + 1);
                    //currentCapBank->setTotalOperations(currentCapBank->getTotalOperations() + 1);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue());
                    currentFeeder->setVarValueBeforeControl(currentFeeder->getCurrentVarLoadPointValue());
                    if( currentCapBank->getStatusPointId() > 0 )
                    {
                        RWCString text = RWCString("Manual Confirm Open Sent");
                        RWCString additional = RWCString("Sub: ");
                        additional += currentSubstationBus->getPAOName();
                        pointChanges.insert(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                        if( ( !savedBusRecentlyControlledFlag ||
                              (!currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) && !savedFeederRecentlyControlledFlag) ) &&
                             savedControlStatus != CtiCCCapBank::Open )
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
                        pointChanges.insert(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
                    }

                    found = TRUE;

                    BOOL confirmImmediately = FALSE;
                    if( savedControlStatus == CtiCCCapBank::Open )
                    {
                        confirmImmediately = TRUE;
                    }
                    else if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) )
                    {
                        if( savedFeederRecentlyControlledFlag ||
                            ((savedFeederLastOperationTime.seconds()+(currentSubstationBus->getMaxConfirmTime()/_SEND_TRIES)) >= currentFeeder->getLastOperationTime().seconds()) ||
                            ((savedFeederLastOperationTime.seconds()+(currentSubstationBus->getMaxConfirmTime()/(currentSubstationBus->getControlSendRetries()+1))) >= currentFeeder->getLastOperationTime().seconds()) )
                        {
                            confirmImmediately = TRUE;
                        }
                        if( _IGNORE_NOT_NORMAL_FLAG &&
                            currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                        {
                            confirmImmediately = TRUE;
                        }
                    }
                    else if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::SubstationBusControlMethod,RWCString::ignoreCase) ||
                             !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::BusOptimizedFeederControlMethod,RWCString::ignoreCase) )
                    {
                        if( savedBusRecentlyControlledFlag ||
                            ((savedBusLastOperationTime.seconds()+(currentSubstationBus->getMaxConfirmTime()/_SEND_TRIES)) >= currentSubstationBus->getLastOperationTime().seconds()) ||
                            ((savedBusLastOperationTime.seconds()+(currentSubstationBus->getMaxConfirmTime()/(currentSubstationBus->getControlSendRetries()+1))) >= currentSubstationBus->getLastOperationTime().seconds()) )
                        {
                            confirmImmediately = TRUE;
                        }
                        if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::SubstationBusControlMethod,RWCString::ignoreCase) &&
                            _IGNORE_NOT_NORMAL_FLAG &&
                            currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                        {
                            confirmImmediately = TRUE;
                        }
                        if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::BusOptimizedFeederControlMethod,RWCString::ignoreCase) &&
                            _IGNORE_NOT_NORMAL_FLAG &&
                            currentFeeder->getCurrentVarPointQuality() != NormalQuality )
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
                        doConfirmImmediately(currentSubstationBus,pointChanges);
                    }
                    break;
                }
            }
            if( found )
            {
                break;
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
void CtiCCCommandExecutor::ConfirmClose()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG controlID = 0;
    LONG bankID = _command->getId();
    BOOL found = FALSE;
    BOOL savedBusRecentlyControlledFlag = FALSE;
    BOOL savedFeederRecentlyControlledFlag = FALSE;
    RWDBDateTime savedBusLastOperationTime = RWDBDateTime(1990,1,1,0,0,0,0);
    RWDBDateTime savedFeederLastOperationTime = RWDBDateTime(1990,1,1,0,0,0,0);
    LONG savedControlStatus = -10;
    CtiMultiMsg* multi = new CtiMultiMsg();
    RWOrdered& pointChanges = multi->getData();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.entries();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.entries();k++)
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
                    currentSubstationBus->setLastFeederControlledPAOId(currentFeeder->getPAOId());
                    currentSubstationBus->setLastFeederControlledPosition(j);
                    currentSubstationBus->setLastOperationTime(RWDBDateTime());
                    currentFeeder->setLastOperationTime(RWDBDateTime());
                    savedControlStatus = currentCapBank->getControlStatus();
                    currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);
                    currentSubstationBus->figureEstimatedVarLoadPointValue();
                    //currentSubstationBus->setCurrentDailyOperations(currentSubstationBus->getCurrentDailyOperations() + 1);
                    //currentFeeder->setCurrentDailyOperations(currentFeeder->getCurrentDailyOperations() + 1);
                    //currentCapBank->setTotalOperations(currentCapBank->getTotalOperations() + 1);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue());
                    currentFeeder->setVarValueBeforeControl(currentFeeder->getCurrentVarLoadPointValue());
                    if( currentCapBank->getStatusPointId() > 0 )
                    {
                        RWCString text = RWCString("Manual Confirm Close Sent");
                        RWCString additional = RWCString("Sub: ");
                        additional += currentSubstationBus->getPAOName();
                        pointChanges.insert(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(1);
                        if( ( !savedBusRecentlyControlledFlag ||
                              (!currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) && !savedFeederRecentlyControlledFlag) ) &&
                            savedControlStatus != CtiCCCapBank::Close )
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
                        pointChanges.insert(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(3);
                    }

                    found = TRUE;

                    BOOL confirmImmediately = FALSE;
                    if( savedControlStatus == CtiCCCapBank::Close )
                    {
                        confirmImmediately = TRUE;
                    }
                    else if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) )
                    {
                        if( savedFeederRecentlyControlledFlag ||
                            ((savedFeederLastOperationTime.seconds()+(currentSubstationBus->getMaxConfirmTime()/_SEND_TRIES)) >= currentFeeder->getLastOperationTime().seconds()) ||
                            ((savedFeederLastOperationTime.seconds()+(currentSubstationBus->getMaxConfirmTime()/(currentSubstationBus->getControlSendRetries()+1))) >= currentFeeder->getLastOperationTime().seconds()) )
                        {
                            confirmImmediately = TRUE;
                        }
                        if( _IGNORE_NOT_NORMAL_FLAG &&
                            currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                        {
                            confirmImmediately = TRUE;
                        }
                    }
                    else if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::SubstationBusControlMethod,RWCString::ignoreCase) ||
                             !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::BusOptimizedFeederControlMethod,RWCString::ignoreCase) )
                    {
                        if( savedBusRecentlyControlledFlag ||
                            ((savedBusLastOperationTime.seconds()+(currentSubstationBus->getMaxConfirmTime()/_SEND_TRIES)) >= currentSubstationBus->getLastOperationTime().seconds()) ||
                            ((savedBusLastOperationTime.seconds()+(currentSubstationBus->getMaxConfirmTime()/(currentSubstationBus->getControlSendRetries()+1))) >= currentSubstationBus->getLastOperationTime().seconds()) )
                        {
                            confirmImmediately = TRUE;
                        }
                        if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::SubstationBusControlMethod,RWCString::ignoreCase) &&
                            _IGNORE_NOT_NORMAL_FLAG &&
                            currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                        {
                            confirmImmediately = TRUE;
                        }
                        if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::BusOptimizedFeederControlMethod,RWCString::ignoreCase) &&
                            _IGNORE_NOT_NORMAL_FLAG &&
                            currentFeeder->getCurrentVarPointQuality() != NormalQuality )
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
                        doConfirmImmediately(currentSubstationBus,pointChanges);
                    }
                    break;
                }
            }
            if( found )
            {
                break;
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
    doConfirmImmediately
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::doConfirmImmediately(CtiCCSubstationBus* currentSubstationBus, RWOrdered& pointChanges)
{
    RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();
    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[currentSubstationBus->getLastFeederControlledPosition()];

    if( currentFeeder->getPAOId() == currentSubstationBus->getLastFeederControlledPAOId() )
    {
        RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

        for(LONG k=0;k<ccCapBanks.entries();k++)
        {
            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
            if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
            {
                currentCapBank->setControlStatus(CtiCCCapBank::Close);
                if( currentCapBank->getStatusPointId() > 0 )
                {
                    pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Close,NormalQuality,StatusPointType));
                    ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                  << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                }
            }
            else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
            {
                currentCapBank->setControlStatus(CtiCCCapBank::Open);
                if( currentCapBank->getStatusPointId() > 0 )
                {
                    pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Open,NormalQuality,StatusPointType));
                    ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                  << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                }
            }
        }
        currentSubstationBus->setRecentlyControlledFlag(FALSE);
        currentFeeder->setRecentlyControlledFlag(FALSE);

        if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) )
        {
            for(LONG x=0;x<ccFeeders.entries();x++)
            {
                if( ((CtiCCFeeder*)ccFeeders[x])->getRecentlyControlledFlag() )
                {
                    currentSubstationBus->setRecentlyControlledFlag(TRUE);
                    break;
                }
            }
        }
    }
    else
    {
        for(LONG j=0;j<ccFeeders.entries();j++)
        {
            currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            if( currentFeeder->getPAOId() == currentSubstationBus->getLastFeederControlledPAOId() )
            {
                RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

                for(LONG k=0;k<ccCapBanks.entries();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                    {
                        currentCapBank->setControlStatus(CtiCCCapBank::Close);
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Close,NormalQuality,StatusPointType));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                          << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                        }
                    }
                    else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                    {
                        currentCapBank->setControlStatus(CtiCCCapBank::Open);
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            pointChanges.insert(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Open,NormalQuality,StatusPointType));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.entries()-1])->setSOE(4);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << RWTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                          << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                        }
                    }
                }
                currentSubstationBus->setRecentlyControlledFlag(FALSE);
                currentFeeder->setRecentlyControlledFlag(FALSE);

                if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) )
                {
                    for(LONG x=0;x<ccFeeders.entries();x++)
                    {
                        if( ((CtiCCFeeder*)ccFeeders[x])->getRecentlyControlledFlag() )
                        {
                            currentSubstationBus->setRecentlyControlledFlag(TRUE);
                            break;
                        }
                    }
                }
                break;
            }
        }
    }
}

/*---------------------------------------------------------------------------
    SendAllSubstationBuses
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::SendAllSubstationBuses()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCExecutorFactory f;
    CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg(*(store->getCCSubstationBuses(RWDBDateTime().seconds())), CtiCCSubstationBusMsg::AllSubBusesSent));
    executor->Execute();
    delete executor;
}

/*---------------------------------------------------------------------------
    ReturnCapToOriginalFeeder
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::ReturnCapToOriginalFeeder()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG bankId = _command->getId();
    BOOL found = FALSE;
    LONG tempFeederId = 0;
    LONG movedCapBankId = 0;
    LONG originalFeederId = 0;
    LONG capSwitchingOrder = 0;

    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.entries();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.entries();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( bankId == currentCapBank->getPAOId() )
                {
                    tempFeederId = currentFeeder->getPAOId();
                    movedCapBankId = bankId;
                    originalFeederId = currentCapBank->getOriginalFeederId();
                    capSwitchingOrder = currentCapBank->getOriginalSwitchingOrder();
                    found = TRUE;
                    break;
                }
            }
            if( found )
            {
                break;
            }
        }
        if( found )
        {
            break;
        }
    }

    //moveCapBank(TRUE, oldFeederId, movedCapBankId, newFeederId, capSwitchingOrder);
    if( tempFeederId > 0 && movedCapBankId > 0 && originalFeederId > 0 )
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Move Cap Bank to original feeder PAO Id: " << movedCapBankId << endl;
        }
        moveCapBank(1, tempFeederId, movedCapBankId, originalFeederId, capSwitchingOrder);
    }
    else
    {
        if( tempFeederId==0 )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Feeder not found PAO Id: " << tempFeederId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
        if( originalFeederId==0 )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Feeder not found PAO Id: " << originalFeederId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
        if( movedCapBankId==0 )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Cap Bank not found PAO Id: " << movedCapBankId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*---------------------------------------------------------------------------
    ResetDailyOperations
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::ResetDailyOperations()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiMultiMsg* multiDispatchMsg = new CtiMultiMsg();
    RWOrdered& pointChanges = multiDispatchMsg->getData();

    LONG paoId = _command->getId();
    BOOL found = FALSE;

    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];

        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

        if( currentSubstationBus->getPAOId() == paoId )
        {
            for(LONG j=0;j<ccFeeders.entries();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
                RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

                for(LONG k=0;k<ccCapBanks.entries();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    if( currentCapBank->getOperationAnalogPointId() > 0 )
                    {
                        pointChanges.insert(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),0,ManualQuality,AnalogPointType));
                    }
                    else
                    {
                        currentCapBank->setTotalOperations(0);
                    }
                }
            }
            currentSubstationBus->setBusUpdatedFlag(TRUE);
            found = TRUE;
        }
        else
        {
            for(LONG j=0;j<ccFeeders.entries();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];

                if( currentFeeder->getPAOId() == paoId )
                {
                    RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(LONG k=0;k<ccCapBanks.entries();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                        if( currentCapBank->getOperationAnalogPointId() > 0 )
                        {
                            pointChanges.insert(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),0,ManualQuality,AnalogPointType));
                        }
                        else
                        {
                            currentCapBank->setTotalOperations(0);
                        }
                    }

                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    found = TRUE;
                }
                else
                {
                    RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(LONG k=0;k<ccCapBanks.entries();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                        
                        if( currentCapBank->getPAOId() == paoId )
                        {
                            if( currentCapBank->getOperationAnalogPointId() > 0 )
                            {
                                pointChanges.insert(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),0,ManualQuality,AnalogPointType));
                            }
                            else
                            {
                                currentCapBank->setTotalOperations(0);
                            }

                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            found = TRUE;
                            break;
                        }
                    }
                }

                if( found )
                {
                    break;
                }
            }
        }

        if( found )
        {
            break;
        }
    }

    if( found )
    {
		{
			CtiLockGuard<CtiLogger> logger_guard(dout);
			dout << RWTime() << "Reset Daily Operations for PAO Id: " << paoId << endl;
		}
		if( pointChanges.entries() > 0 )
		{
			CtiCapController::getInstance()->sendMessageToDispatch(multiDispatchMsg);
		}
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << "Cannot find PAO Id: " << paoId << " in ResetDailyOperations() in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    WaiveSubstationBus
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::WaiveSubstationBus()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG subID = _command->getId();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        if( subID == currentSubstationBus->getPAOId() )
        {
            currentSubstationBus->setWaiveControlFlag(TRUE);
            currentSubstationBus->setBusUpdatedFlag(TRUE);
            RWCString text = RWCString("Substation Bus Waived");
            RWCString additional = RWCString("Bus: ");
            additional += currentSubstationBus->getPAOName();
            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
            break;
        }
    }
}

/*---------------------------------------------------------------------------
    EnableSubstationBus
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::UnwaiveSubstationBus()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG subID = _command->getId();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        if( subID == currentSubstationBus->getPAOId() )
        {
            currentSubstationBus->setWaiveControlFlag(FALSE);
            currentSubstationBus->setBusUpdatedFlag(TRUE);
            RWCString text = RWCString("Substation Bus Unwaived");
            RWCString additional = RWCString("Bus: ");
            additional += currentSubstationBus->getPAOName();
            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
            break;
        }
    }
}

/*---------------------------------------------------------------------------
    WaiveFeeder
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::WaiveFeeder()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG feederID = _command->getId();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.entries();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            if( feederID == currentFeeder->getPAOId() )
            {
                currentFeeder->setWaiveControlFlag(TRUE);
                currentSubstationBus->setBusUpdatedFlag(TRUE);
                RWCString text = RWCString("Feeder Waived");
                RWCString additional = RWCString("Feeder: ");
                additional += currentFeeder->getPAOName();
                CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                break;
            }
        }
    }
}

/*---------------------------------------------------------------------------
    EnableFeeder
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::UnwaiveFeeder()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG feederID = _command->getId();
    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.entries();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            if( feederID == currentFeeder->getPAOId() )
            {
                currentFeeder->setWaiveControlFlag(FALSE);
                currentSubstationBus->setBusUpdatedFlag(TRUE);
                RWCString text = RWCString("Feeder Unwaived");
                RWCString additional = RWCString("Feeder: ");
                additional += currentFeeder->getPAOName();
                CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,GeneralLogType,SignalEvent,_command->getUser()));
                break;
            }
        }
    }
}


/*===========================================================================
    CtiCCCapBankMoveExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiCCCapBankMoveExecutor::Execute()
{
    INT permanentFlag = _capMoveMsg->getPermanentFlag();
    LONG oldFeederId = _capMoveMsg->getOldFeederId();
    LONG movedCapBankId = _capMoveMsg->getCapBankId();
    LONG newFeederId = _capMoveMsg->getNewFeederId();
    LONG capSwitchingOrder = _capMoveMsg->getCapSwitchingOrder();

    moveCapBank(permanentFlag, oldFeederId, movedCapBankId, newFeederId, capSwitchingOrder);
}

/*---------------------------------------------------------------------------
    moveCapBank
---------------------------------------------------------------------------*/    
void CtiCCExecutor::moveCapBank(INT permanentFlag, LONG oldFeederId, LONG movedCapBankId, LONG newFeederId, LONG capSwitchingOrder)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCFeeder* oldFeederPtr = NULL;
    CtiCCFeeder* newFeederPtr = NULL;
    CtiCCCapBank* movedCapBankPtr = NULL;

    RWOrdered& ccSubstationBuses = *store->getCCSubstationBuses(RWDBDateTime().seconds());

    BOOL found = FALSE;
    for(LONG i=0;i<ccSubstationBuses.entries();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        RWOrdered& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.entries();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            if( oldFeederPtr==NULL && currentFeeder->getPAOId()==oldFeederId )
            {
                currentSubstationBus->setBusUpdatedFlag(TRUE);
                oldFeederPtr = currentFeeder;
            }
            if( newFeederPtr==NULL && currentFeeder->getPAOId()==newFeederId )
            {
                currentSubstationBus->setBusUpdatedFlag(TRUE);
                newFeederPtr = currentFeeder;
            }

            if( movedCapBankPtr==NULL )
            {
                RWOrdered& ccCapBanks = currentFeeder->getCCCapBanks();
                for(LONG k=0;k<ccCapBanks.entries();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                    if( currentCapBank->getPAOId()==movedCapBankId )
                    {
                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                        movedCapBankPtr = currentCapBank;
                        break;
                    }
                }
            }

            if( oldFeederPtr!=NULL && newFeederPtr!=NULL && movedCapBankPtr!=NULL )
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

    if( oldFeederPtr!=NULL && newFeederPtr!=NULL && movedCapBankPtr!=NULL )
    {
        {
            RWSortedVector& oldFeederCapBanks = oldFeederPtr->getCCCapBanks();

            oldFeederCapBanks.remove(movedCapBankPtr);

            if( !permanentFlag )
            {
                movedCapBankPtr->setOriginalFeederId(oldFeederPtr->getPAOId());
                movedCapBankPtr->setOriginalSwitchingOrder(movedCapBankPtr->getControlOrder());
            }
            else
            {
                movedCapBankPtr->setOriginalFeederId(0);
                movedCapBankPtr->setOriginalSwitchingOrder(0);
            }

            if( oldFeederCapBanks.entries() > 0 )
            {
                //reshuffle the cap bank control orders so they are still in sequence and start at 1
                LONG shuffledOrder = 1;
                RWOrdered tempShufflingCapBankList;
                while(oldFeederCapBanks.entries()>0)
                {
                    //have to remove due to change in sorting field in a sorted vector
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)oldFeederCapBanks.removeAt(0);
                    currentCapBank->setControlOrder(shuffledOrder);
                    tempShufflingCapBankList.insert(currentCapBank);
                    shuffledOrder++;
                }
                while(tempShufflingCapBankList.entries()>0)
                {
                    oldFeederCapBanks.insert(tempShufflingCapBankList.removeAt(0));
                }
            }
        }

        {
            RWSortedVector& newFeederCapBanks = newFeederPtr->getCCCapBanks();

            if( newFeederCapBanks.entries() > 0 )
            {
                //search through the list to see if there is a cap bank in the
                //list that already has the switching order
                if( capSwitchingOrder >= ((CtiCCCapBank*)newFeederCapBanks[newFeederCapBanks.entries()-1])->getControlOrder() )
                {
                    movedCapBankPtr->setControlOrder( ((CtiCCCapBank*)newFeederCapBanks[newFeederCapBanks.entries()-1])->getControlOrder() + 1 );
                }
                else
                {
                    BOOL shuffling = FALSE;
                    movedCapBankPtr->setControlOrder(capSwitchingOrder);
                    for(LONG k=0;k<newFeederCapBanks.entries();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)newFeederCapBanks[k];
                        if( capSwitchingOrder == currentCapBank->getControlOrder() )
                        {
                            //if the new switching order matches a current control
                            //order, then we need to shuffle all the current cap banks
                            //up one in order
                            shuffling = TRUE;
                            break;
                        }
                        else if( currentCapBank->getControlOrder() > capSwitchingOrder )
                        {
                            break;
                        }
                    }
                    //reshuffle the cap bank control orders so they are still in sequence and start at 1
                    LONG shuffledOrder = 1;
                    RWOrdered tempShufflingCapBankList;
                    while(newFeederCapBanks.entries()>0)
                    {
                        //have to remove due to change in sorting field in a sorted vector
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)newFeederCapBanks.removeAt(0);
                        if( capSwitchingOrder == currentCapBank->getControlOrder() )
                        {
                            //have to make room for the movedCapBank
                            shuffledOrder++;
                        }
                        currentCapBank->setControlOrder(shuffledOrder);
                        tempShufflingCapBankList.insert(currentCapBank);
                        shuffledOrder++;
                    }
                    while(tempShufflingCapBankList.entries()>0)
                    {
                        newFeederCapBanks.insert(tempShufflingCapBankList.removeAt(0));
                    }
                }
            }
            else
            {
                movedCapBankPtr->setControlOrder(1);
            }

            newFeederCapBanks.insert(movedCapBankPtr);
        }

        store->UpdateFeederBankListInDB(oldFeederPtr);
        store->UpdateFeederBankListInDB(newFeederPtr);

        {
            RWCString typeString = (permanentFlag?"Temporary":"Permanent");

            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Manual "
                 << typeString
                 << " Cap Bank with PAO Id: "
                 << movedCapBankPtr->getPAOId() << ", name: "
                 << movedCapBankPtr->getPAOName()
                 << ", was moved from feeder PAO Id: "
                 << oldFeederPtr->getPAOId() << ", name: "
                 << oldFeederPtr->getPAOName() << ", to feeder PAO Id: "
                 << newFeederPtr->getPAOId() << ", name: "
                 << newFeederPtr->getPAOName() << ", with order: "
                 << movedCapBankPtr->getControlOrder() << endl;
        }
    }
    else
    {
        if( oldFeederPtr==NULL )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Feeder not found PAO Id: " << oldFeederId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
        if( newFeederPtr==NULL )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Feeder not found PAO Id: " << newFeederId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
        if( movedCapBankPtr==NULL )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Cap Bank not found PAO Id: " << movedCapBankId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}


/*===========================================================================
    CtiCCClientMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiCCClientMsgExecutor::Execute()
{
    CtiCCClientListener::getInstance()->BroadcastMessage(_ccMsg);
}


/*===========================================================================
    CtiCCForwardMsgToDispatchExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiCCForwardMsgToDispatchExecutor::Execute()
{
    CtiCapController::getInstance()->sendMessageToDispatch(_ctiMessage->replicateMessage());
}


/*===========================================================================
    CtiCCPointDataMsgExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiCCPointDataMsgExecutor::Execute()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    long pointID = _pointDataMsg->getId();
    double value = _pointDataMsg->getValue();
    unsigned tags = _pointDataMsg->getTags();
    RWTime& timestamp = _pointDataMsg->getTime();

    if( _CC_DEBUG & CC_DEBUG_STANDARD )
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
                        if( currentCapBank->getControlStatus() != (LONG)value )
                        {
                            if( currentSubstationBus->getRecentlyControlledFlag() &&
                                currentFeeder->getLastCapBankControlledDeviceId() == currentCapBank->getPAOId() )
                            {
                                currentSubstationBus->setRecentlyControlledFlag(FALSE);
                                currentFeeder->setRecentlyControlledFlag(FALSE);
                                if( !currentSubstationBus->getControlMethod().compareTo(CtiCCSubstationBus::IndividualFeederControlMethod,RWCString::ignoreCase) )
                                {
                                    for(LONG x=0;x<ccFeeders.entries();x++)
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
                            currentCapBank->setControlStatus((LONG)value);
                            currentCapBank->setTagsControlStatus((LONG)tags);
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
void CtiCCMultiMsgExecutor::Execute()
{
    CtiCCExecutorFactory f;
    RWOrdered& messages = _multiMsg->getData();
    while(messages.entries( )>=0)
    {
        CtiMessage* message = (CtiMessage*)(messages.pop());
        if( message != NULL )
        {
            CtiCCExecutor* executor = f.createExecutor(message);
            executor->Execute();
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
void CtiCCShutdownExecutor::Execute()
{
    try
    {
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Shutting down client listener thread..." << endl;
        }

        CtiCCClientListener::getInstance()->stop();

        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Client listener thread shutdown." << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    try
    {
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Shutting down cap controller thread..." << endl;
        }
    
        CtiCapController::getInstance()->stop();

        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Cap controller thread shutdown." << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    try
    {
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Shutting down substation bus store..." << endl;
        }
    
        CtiCCSubstationBusStore::deleteInstance();

        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Substation bus store shutdown." << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

/*===========================================================================
    CtiCCExecutorFactory
===========================================================================*/
CtiCCExecutor* CtiCCExecutorFactory::createExecutor(const CtiMessage* message)
{
    CtiCCExecutor* ret_val = NULL;
    LONG classId = message->isA();

    switch ( classId )
    {
        case CTICCSUBSTATIONBUS_MSG_ID:
        case CTICCCAPBANKSTATES_MSG_ID:
        case CTICCGEOAREAS_MSG_ID:
            ret_val = new CtiCCClientMsgExecutor( (CtiMessage*)message );
            break;
    
        case CTICCCOMMAND_ID:
            ret_val = new CtiCCCommandExecutor( (CtiCCCommand*)message );
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
    
        case CTICCCAPBANKMOVEMSG_ID:
            ret_val = new CtiCCCapBankMoveExecutor( (CtiCCCapBankMoveMsg*)message );
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
