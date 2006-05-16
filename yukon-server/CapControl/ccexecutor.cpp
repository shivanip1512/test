/*-----------------------------------------------------------------------------
    Filename:  ccexecutor.cpp
    
    Programmer:  Josh Wolberg
    
    Description:    Defines Cap Control executor classes.

    Initial Date:  8/30/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "msg_signal.h"

#include "ccclientlistener.h"
#include "ccexecutor.h"
#include "ccsubstationbusstore.h"
#include "capcontroller.h"
#include "ccid.h"
#include "logger.h"
#include "utility.h"
                     
#include <rw/collstr.h>

extern ULONG _CC_DEBUG;
extern BOOL _IGNORE_NOT_NORMAL_FLAG;
extern ULONG _SEND_TRIES;
extern BOOL _USE_FLIP_FLAG;

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

    case CtiCCCommand::REQUEST_ALL_DATA:
        SendAllData();
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

    case CtiCCCommand::ENABLE_OVUV:
        EnableOvUv();
        break;

    case CtiCCCommand::DISABLE_OVUV:
        DisableOvUv();
        break;

    case CtiCCCommand::DELETE_ITEM:
        DeleteItem();
        break;

    case CtiCCCommand::CONFIRM_SUB:
        ConfirmSub();
        break;

    case CtiCCCommand::CONFIRM_AREA:
        ConfirmArea();
        break;

    
    default:
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - unknown command type in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
        break;

    }

}


/*---------------------------------------------------------------------------
    EnableSubstationVerification
---------------------------------------------------------------------------*/    
void CtiCCSubstationVerificationExecutor::EnableSubstationBusVerification()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG subID = _subVerificationMsg->getSubBusId();
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        if( subID == currentSubstationBus->getPAOId() )
        {
            if (!currentSubstationBus->getDisableFlag())
            {    
                if (!currentSubstationBus->getVerificationFlag())
                {
                    //Check to see if sub is already being operated on.  Set flag, which will finish out normal control
                    //before verification controls start.
                    CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
                    for(LONG j=0;j<ccFeeders.size();j++)
                    {
                        CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                        CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                        for(LONG k=0;k<ccCapBanks.size();k++)
                        {
                            CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];
                            if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending) 
                            {
                                currentSubstationBus->setWaitToFinishRegularControlFlag(TRUE);
                                break;
                            }
                        }
                    }
                    
                    currentSubstationBus->setVerificationFlag(TRUE);
                    currentSubstationBus->setVerificationStrategy(_subVerificationMsg->getStrategy());
                    currentSubstationBus->setCapBankInactivityTime(_subVerificationMsg->getInactivityTime());
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                   //store->UpdateBusVerificationFlagInDB(currentSubstationBus);
                    //string text("Substation Bus Verification Enabled");
                    string text = currentSubstationBus->getVerificationString();
                    text += " Starting";
                    string additional("Bus: ");
                    additional += currentSubstationBus->getPAOName();
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_subVerificationMsg->getUser()));

                    INT seqId = CCEventSeqIdGen();
                    currentSubstationBus->setEventSequence(seqId);
                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), 0, capControlEnableVerification, currentSubstationBus->getEventSequence(), 1, text, "cap control"));

                    CtiCCExecutorFactory f;
                    CtiCCExecutor* executor = f.createExecutor(new CtiCCCommand(CtiCCCommand::DISABLE_SUBSTATION_BUS, currentSubstationBus->getPAOId()));
                    executor->Execute();
                    delete executor;


                }
                else
                {
                    if (currentSubstationBus->getPerformingVerificationFlag() &&
                        _subVerificationMsg->getStrategy() < currentSubstationBus->getVerificationStrategy())
                    {
                        currentSubstationBus->setOverlappingVerificationFlag( TRUE );
                        currentSubstationBus->setVerificationStrategy(_subVerificationMsg->getStrategy());
                        currentSubstationBus->setCapBankInactivityTime(_subVerificationMsg->getInactivityTime());
                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                       //store->UpdateBusVerificationFlagInDB(currentSubstationBus);
                        string text = currentSubstationBus->getVerificationString();
                        text += " Enabled";
                        string additional("Bus: ");
                        additional += currentSubstationBus->getPAOName();
                        CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_subVerificationMsg->getUser()));

                        INT seqId = CCEventSeqIdGen();
                        currentSubstationBus->setEventSequence(seqId);
                        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), 0, capControlEnableVerification, currentSubstationBus->getEventSequence(), 1, text, "cap control"));

                        /*CtiCCExecutorFactory f;
                        CtiCCExecutor* executor = f.createExecutor(new CtiCCCommand(CtiCCCommand::DISABLE_SUBSTATION_BUS, currentSubstationBus->getPAOId()));
                        executor->Execute();
                        */
                    }
                }
            }
            break;
        }
    }
}


/*---------------------------------------------------------------------------
    DisableSubstationVerification
---------------------------------------------------------------------------*/    
void CtiCCSubstationVerificationExecutor::DisableSubstationBusVerification()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG subID = _subVerificationMsg->getSubBusId();
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        if( subID == currentSubstationBus->getPAOId() )
        {
            if (currentSubstationBus->getPerformingVerificationFlag()) 
            {
                CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                for(LONG j=0;j<ccFeeders.size();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)(ccFeeders.at(j));
                    if (currentFeeder->getPerformingVerificationFlag()) 
                    {

                        CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                        for(LONG k=0;k<ccCapBanks.size();k++)
                        {
                            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                            if (currentCapBank->getControlStatus() != CtiCCCapBank::OpenPending &&
                                currentCapBank->getControlStatus() != CtiCCCapBank::ClosePending) 
                            {
                                currentCapBank->setVerificationFlag(FALSE);
                                currentCapBank->setPerformingVerificationFlag(FALSE);
                                currentCapBank->setVerificationDoneFlag(FALSE);
                                //wouldn't hurt to set this.
                                currentCapBank->setVCtrlIndex(0);
                            }
                        }
                    }
                    else
                    {
                        currentFeeder->setVerificationFlag(FALSE);
                        currentFeeder->setPerformingVerificationFlag(FALSE);
                        currentFeeder->setVerificationDoneFlag(FALSE);

                        CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                    
                        for(LONG k=0;k<ccCapBanks.size();k++)
                        {
                            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                            currentCapBank->setVerificationFlag(FALSE);
                            currentCapBank->setPerformingVerificationFlag(FALSE);
                            currentCapBank->setVerificationDoneFlag(FALSE);
                            //wouldn't hurt to set this.
                            currentCapBank->setVCtrlIndex(0);
                        }
                    }
                }
                string text = currentSubstationBus->getVerificationString();
                text += " Stopping after currentBank";
                string additional = string("Bus: ");
                additional += currentSubstationBus->getPAOName();
                CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_subVerificationMsg->getUser()));

                //currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() + 1);
                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text, "cap control"));

                if( _CC_DEBUG & CC_DEBUG_STANDARD )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Verification Stop Message received from client.  Current Cap Bank Verification will complete first."<< endl;
                }

            }
            else
            {
                currentSubstationBus->setVerificationFlag(FALSE);
                currentSubstationBus->setPerformingVerificationFlag(FALSE);
                currentSubstationBus->setOverlappingVerificationFlag( FALSE );
                currentSubstationBus->setVerificationDoneFlag(FALSE);

                currentSubstationBus->setWaitToFinishRegularControlFlag(FALSE);

                currentSubstationBus->setBusUpdatedFlag(TRUE);

                CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                for(LONG j=0;j<ccFeeders.size();j++)
                {
                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)(ccFeeders.at(j));

                    currentFeeder->setVerificationFlag(FALSE);
                    currentFeeder->setPerformingVerificationFlag(FALSE);
                    currentFeeder->setVerificationDoneFlag(FALSE);

                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                    
                    for(LONG k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                        currentCapBank->setVerificationFlag(FALSE);
                        currentCapBank->setPerformingVerificationFlag(FALSE);
                        currentCapBank->setVerificationDoneFlag(FALSE);
                        //wouldn't hurt to set this.
                        currentCapBank->setVCtrlIndex(0);
                    }
                }

                //store->UpdateBusVerificationFlagInDB(currentSubstationBus);
                //CtiString text = CtiString("Substation Bus Verification Disabled");
                string text = currentSubstationBus->getVerificationString();
                text += " Stopping";
                string additional = string("Bus: ");
                additional += currentSubstationBus->getPAOName();
                CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_subVerificationMsg->getUser()));

                //currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() + 1);
                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), 0, capControlDisableVerification, currentSubstationBus->getEventSequence(), 0, text, "cap control"));
                break;
            }
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
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        if( subID == currentSubstationBus->getPAOId() )
        {
            currentSubstationBus->setDisableFlag(FALSE);
            currentSubstationBus->setReEnableBusFlag(FALSE);
            currentSubstationBus->setBusUpdatedFlag(TRUE);
            store->UpdateBusDisableFlagInDB(currentSubstationBus);
            string text = string("Substation Bus Enabled");
            string additional = string("Bus: ");
            additional += currentSubstationBus->getPAOName();
            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

            if (!currentSubstationBus->getVerificationFlag())
            {
                INT seqId = CCEventSeqIdGen();
                currentSubstationBus->setEventSequence(seqId);
            }

            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), 0, capControlEnable, currentSubstationBus->getEventSequence(), 1, text, _command->getUser()));
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
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        if( subID == currentSubstationBus->getPAOId() )
        {
            currentSubstationBus->setDisableFlag(TRUE);
            currentSubstationBus->setReEnableBusFlag(FALSE);
            currentSubstationBus->setBusUpdatedFlag(TRUE);
            store->UpdateBusDisableFlagInDB(currentSubstationBus);
            string text = string("Substation Bus Disabled");
            string additional = string("Bus: ");
            additional += currentSubstationBus->getPAOName();
            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

            if (!currentSubstationBus->getVerificationFlag())
            {
                INT seqId = CCEventSeqIdGen();
                currentSubstationBus->setEventSequence(seqId);
            }

            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), 0, capControlDisable, currentSubstationBus->getEventSequence(), 0, text, _command->getUser()));
            
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
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            if( feederID == currentFeeder->getPAOId() )
            {
                if (!currentSubstationBus->getVerificationFlag())
                {
                    currentFeeder->setDisableFlag(FALSE);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    store->UpdateFeederDisableFlagInDB(currentFeeder);
                    string text("Feeder Enabled");
                    string additional("Feeder: ");
                    additional += currentFeeder->getPAOName();
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                    INT seqId = CCEventSeqIdGen();
                    currentSubstationBus->setEventSequence(seqId);
                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlEnable, currentSubstationBus->getEventSequence(), 1, text, _command->getUser()));
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                     dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                      <<".  Cannot perform ENABLE on Feeder: " << currentFeeder->getPAOName() << " PAOID: " << currentFeeder->getPAOId() << "."<<endl;
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
    DisableFeeder
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::DisableFeeder()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG feederID = _command->getId();
    BOOL found = FALSE;
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            if( feederID == currentFeeder->getPAOId() )
            {
                if (!currentSubstationBus->getVerificationFlag())
                {   
                    currentFeeder->setDisableFlag(TRUE);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    store->UpdateFeederDisableFlagInDB(currentFeeder);
                    string text("Feeder Disabled");
                    string additional("Feeder: ");
                    additional += currentFeeder->getPAOName();
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlDisable, currentSubstationBus->getEventSequence(), 0, text, _command->getUser()));
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                     dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                      <<".  Cannot perform DISABLE on Feeder: " << currentFeeder->getPAOName() << " PAOID: " << currentFeeder->getPAOId() << "."<<endl;
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
    EnableCapBank
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::EnableCapBank()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG capBankID = _command->getId();
    BOOL found = FALSE;
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
        CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)ccFeeders[i];
        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCCapBank_SVector& ccCapBanks = ((CtiCCFeeder*)ccFeeders.at(j))->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( capBankID == currentCapBank->getPAOId() )
                {
                    if (!currentSubstationBus->getVerificationFlag())
                    {   
                        currentCapBank->setDisableFlag(FALSE);
                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                        store->UpdateCapBankDisableFlagInDB(currentCapBank);
                        string text("Cap Bank Enabled");
                        string additional("Cap Bank: ");
                        additional += currentCapBank->getPAOName();
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                            INT seqId = CCEventSeqIdGen();
                            currentSubstationBus->setEventSequence(seqId);
                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlEnable, currentSubstationBus->getEventSequence(), 1, text, _command->getUser()));
                            //setEventSequence(0);
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                          << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;

                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                         <<".  Cannot perform ENABLE on Cap Bank: " << currentCapBank->getPAOName() << " PAOID: " << currentCapBank->getPAOId() << "."<<endl;
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
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
        CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)ccFeeders[i];
        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCCapBank_SVector& ccCapBanks = ((CtiCCFeeder*)ccFeeders.at(j))->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( capBankID == currentCapBank->getPAOId() )
                {
                    if (!currentSubstationBus->getVerificationFlag())
                    {    
                        currentCapBank->setDisableFlag(TRUE);
                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                        store->UpdateCapBankDisableFlagInDB(currentCapBank);
                        string text("Cap Bank Disabled");
                        string additional("Cap Bank: ");
                        additional += currentCapBank->getPAOName();
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlDisable, currentSubstationBus->getEventSequence(), 0, text, _command->getUser()));
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                          << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;

                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                         <<".  Cannot perform DISABLE on Cap Bank: " << currentCapBank->getPAOName() << " PAOID: " << currentCapBank->getPAOId() << "."<<endl;

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
    Enable OV/UV
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::EnableOvUv()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG controlID = 0;
    LONG bankID = _command->getId();
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
   // RWOrdered& pointChanges = multi->getData();

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( bankID == currentCapBank->getControlDeviceId() )
                {
                    controlID = currentCapBank->getControlDeviceId();

                    string text = string("Cap Bank OV/UV Enabled");
                    string additional = string("Cap Bank: ");
                    additional += currentCapBank->getPAOName();
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                    found = TRUE;
                    break;
                }
            }
            if (found)
            {
                break;
            }
        }
        if (found)
        {
            break;
        }
    }
    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"putconfig ovuv enable");
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );
        //CtiCapController::getInstance()->manualCapBankControl( reqMsg );
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
    }


}

/*---------------------------------------------------------------------------
    Disable OV/UV
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::DisableOvUv()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG controlID = 0;
    LONG bankID = _command->getId();
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    //RWOrdered& pointChanges = multi->getData();

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( bankID == currentCapBank->getControlDeviceId() )
                {
                    controlID = currentCapBank->getControlDeviceId();

                    string text = string("Cap Bank OV/UV Disabled");
                    string additional = string("Cap Bank: ");
                    additional += currentCapBank->getPAOName();
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                    found = TRUE;
                    break;

                }

            }
            if (found)
            {
                break;
            }
        }
        if (found)
        {
            break;
        }
    }
    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"putconfig ovuv disable");
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );
        //CtiCapController::getInstance()->manualCapBankControl( reqMsg );
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
    }

}

/*---------------------------------------------------------------------------
    Disable OV/UV
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::DeleteItem()
{
    CtiCCClientListener::getInstance()->BroadcastMessage(_command);
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
    CtiTime savedBusLastOperationTime = gInvalidCtiTime;
    CtiTime savedFeederLastOperationTime = gInvalidCtiTime;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( bankID == currentCapBank->getControlDeviceId() )
                {
                    found = TRUE;

                    if (!currentSubstationBus->getVerificationFlag() && currentSubstationBus->getStrategyId() > 0)
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
                        currentSubstationBus->setLastOperationTime(CtiTime());
                        currentFeeder->setLastOperationTime(CtiTime());
                        currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                        currentSubstationBus->figureEstimatedVarLoadPointValue();
                        currentSubstationBus->setCurrentDailyOperations(currentSubstationBus->getCurrentDailyOperations() + 1);
                        currentFeeder->setCurrentDailyOperations(currentFeeder->getCurrentDailyOperations() + 1);
                        currentCapBank->setTotalOperations(currentCapBank->getTotalOperations() + 1);
                        currentCapBank->setCurrentDailyOperations(currentCapBank->getCurrentDailyOperations() + 1);
                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                        currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue());
                        currentFeeder->setVarValueBeforeControl(currentFeeder->getCurrentVarLoadPointValue());
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            char tempchar[80] = "";
                            char tempchar1[80] = "";
                            string additional("Sub: ");
                            additional += currentSubstationBus->getPAOName();
                            additional += string("  Feeder: ");
                            additional += currentFeeder->getPAOName();

                            string text("Manual Open Sent, Sub VarLoad = ");
                            _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                            text += tempchar;
                            text += string(" Feeder VarLoad = ");
                            _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                            text += tempchar1;
                            pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                            if( !savedBusRecentlyControlledFlag ||
                                (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) && !savedFeederRecentlyControlledFlag) )
                            {
                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                                currentCapBank->setLastStatusChangeTime(CtiTime());

                                //currentFeeder->setEventSequence(currentFeeder->getEventSequence() + 1);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser()));
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                          << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                        }

                        if( currentCapBank->getOperationAnalogPointId() > 0 )
                        {
                            pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);


                            //currentFeeder->setEventSequence(currentFeeder->getEventSequence() + 1);
                            ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlSetOperationCount, 0, currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                        }


                        BOOL confirmImmediately = FALSE;
                        if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) )
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
                        else if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::SubstationBusControlMethod) ||
                                 !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
                        {
                            if( savedBusRecentlyControlledFlag ||
                                ((savedBusLastOperationTime.seconds()+2) >= currentSubstationBus->getLastOperationTime().seconds()) )
                            {
                                confirmImmediately = TRUE;
                            }
                            if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::SubstationBusControlMethod) &&
                                _IGNORE_NOT_NORMAL_FLAG &&
                                currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                            {
                                confirmImmediately = TRUE;
                            }
                            if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::BusOptimizedFeederControlMethod) &&
                                _IGNORE_NOT_NORMAL_FLAG &&
                                currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                            {
                                confirmImmediately = TRUE;
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Unknown Control Method: " << currentSubstationBus->getControlMethod()
                                          << " PAOID: " << currentSubstationBus->getPAOId() << " Name: " << currentSubstationBus->getPAOName() << endl;
                        }

                        if( !confirmImmediately && !currentSubstationBus->isBusPerformingVerification())
                        {
                            currentSubstationBus->setRecentlyControlledFlag(TRUE);
                            currentFeeder->setRecentlyControlledFlag(TRUE);
                        }
                        else if (confirmImmediately)
                        {
                            doConfirmImmediately(currentSubstationBus,pointChanges, bankID);
                        }
                        break;
                    }
                    else
                    {
                        if (currentSubstationBus->getVerificationFlag())
                        {                                              
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                         <<".  Cannot perform CONFIRM CLOSE on Cap Bank: " << currentCapBank->getPAOName() << " PAOID: " << currentCapBank->getPAOId() << "."<<endl;
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - No Control Method: " << currentSubstationBus->getControlMethod()
                                          << " PAOID: " << currentSubstationBus->getPAOId() << " Name: " << currentSubstationBus->getPAOName() << endl;
                        }
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
        dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
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
    CtiTime savedBusLastOperationTime = gInvalidCtiTime;
    CtiTime savedFeederLastOperationTime = gInvalidCtiTime;
    CtiMultiMsg* multi = new CtiMultiMsg();

    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( bankID == currentCapBank->getControlDeviceId() )
                {
                    found = TRUE;
                    if (!currentSubstationBus->getVerificationFlag() && currentSubstationBus->getStrategyId() > 0)
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
                        currentSubstationBus->setLastOperationTime(CtiTime());
                        currentFeeder->setLastOperationTime(CtiTime());
                        currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);
                        currentSubstationBus->figureEstimatedVarLoadPointValue();
                        currentSubstationBus->setCurrentDailyOperations(currentSubstationBus->getCurrentDailyOperations() + 1);
                        currentFeeder->setCurrentDailyOperations(currentFeeder->getCurrentDailyOperations() + 1);
                        currentCapBank->setTotalOperations(currentCapBank->getTotalOperations() + 1);
                        currentCapBank->setCurrentDailyOperations(currentCapBank->getCurrentDailyOperations() + 1);
                        currentSubstationBus->setBusUpdatedFlag(TRUE);
                        currentSubstationBus->setVarValueBeforeControl(currentSubstationBus->getCurrentVarLoadPointValue());
                        currentFeeder->setVarValueBeforeControl(currentFeeder->getCurrentVarLoadPointValue());
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            char tempchar[80] = "";
                            char tempchar1[80] = "";
                            string additional("Sub: ");
                            additional += currentSubstationBus->getPAOName();
                            additional += string("  Feeder: ");
                            additional += currentFeeder->getPAOName();

                            string text("Manual Close Sent, Sub VarLoad = ");
                            _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                            text += tempchar;
                            text += string(" Feeder VarLoad = ");
                            _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                            text += tempchar1;
                            pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                            if( !savedBusRecentlyControlledFlag ||
                                (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) && !savedFeederRecentlyControlledFlag) )
                            {
                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                                currentCapBank->setLastStatusChangeTime(CtiTime());


                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser()));
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                          << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                        }
                        
                        if( currentCapBank->getOperationAnalogPointId() > 0 )
                        {
                            pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

                            //currentFeeder->setEventSequence(currentFeeder->getEventSequence() + 1);
                            ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));

                        }


                        BOOL confirmImmediately = FALSE;
                        if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) )
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
                        else if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::SubstationBusControlMethod) ||
                                 !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
                        {
                            if( savedBusRecentlyControlledFlag ||
                                ((savedBusLastOperationTime.seconds()+2) >= currentSubstationBus->getLastOperationTime().seconds()) )
                            {
                                confirmImmediately = TRUE;
                            }
                            if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::SubstationBusControlMethod) &&
                                _IGNORE_NOT_NORMAL_FLAG &&
                                currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                            {
                                confirmImmediately = TRUE;
                            }
                            if( (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::BusOptimizedFeederControlMethod)) &&
                                _IGNORE_NOT_NORMAL_FLAG &&
                                (currentFeeder->getCurrentVarPointQuality() != NormalQuality) )
                            {
                                confirmImmediately = TRUE;
                            }
                        }  
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Unknown Control Method: " << currentSubstationBus->getControlMethod()
                                          << " PAOID: " << currentSubstationBus->getPAOId() << " Name: " << currentSubstationBus->getPAOName() << endl;
                            
                            /*if (currentSubstationBus->getStrategyId() <= 0)
                            {
                                confirmImmediately = TRUE;
                            }*/
                        }

                        if( !confirmImmediately && !currentSubstationBus->isBusPerformingVerification())
                        {
                            currentSubstationBus->setRecentlyControlledFlag(TRUE);
                            currentFeeder->setRecentlyControlledFlag(TRUE);
                        }
                        else if (confirmImmediately)
                        {
                            doConfirmImmediately(currentSubstationBus,pointChanges, bankID);
                        }
                        break;
                    }
                    else
                    {
                        if (currentSubstationBus->getVerificationFlag())
                        {                                              
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                         <<".  Cannot perform CONFIRM CLOSE on Cap Bank: " << currentCapBank->getPAOName() << " PAOID: " << currentCapBank->getPAOId() << "."<<endl;
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - No Control Method: " << currentSubstationBus->getControlMethod()
                                          << " PAOID: " << currentSubstationBus->getPAOId() << " Name: " << currentSubstationBus->getPAOName() << endl;
                        }
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

        //CtiCapController::getInstance()->eventThing( eventMulti );
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}


/*---------------------------------------------------------------------------
    ConfirmOpen
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::ConfirmSub()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG subId = _command->getId();
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
    CtiMultiMsg_vec& pilMessages = multiPilMsg->getData();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID(subId);
    if (currentSubstationBus != NULL)
    {

        if (!currentSubstationBus->getVerificationFlag() && currentSubstationBus->getStrategyId() > 0)
        {

            string text1 = string("Manual Confirm Sub");
            string additional1 = string("Sub: ");
            additional1 += currentSubstationBus->getPAOName();
            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));

            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

            for(LONG j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(LONG k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];

                    if (!currentCapBank->getDisableFlag() && currentCapBank->getControlDeviceId() > 0 &&
                        currentCapBank->getStatusPointId() > 0)
                    {

                        currentSubstationBus->setRecentlyControlledFlag(FALSE);
                        currentFeeder->setRecentlyControlledFlag(FALSE);
                        controlID = currentCapBank->getControlDeviceId();
                        currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPAOId());
                        currentSubstationBus->setLastFeederControlledPAOId(currentFeeder->getPAOId());
                        currentSubstationBus->setLastFeederControlledPosition(j);
                        currentSubstationBus->setLastOperationTime(CtiTime());
                        currentFeeder->setLastOperationTime(CtiTime());


                        currentSubstationBus->setBusUpdatedFlag(TRUE);


                        char tempchar[80] = "";
                        char tempchar1[80] = "";
                        string additional = string("Sub: ");
                        additional += currentSubstationBus->getPAOName();
                        additional += string("  Feeder: ");
                        additional += currentFeeder->getPAOName();

                        string text = string("Manual Confirm ");

                        if (currentCapBank->getControlStatus() == CtiCCCapBank::Open ||
                            currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                            currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable ||
                            currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail )
                        {
                            text += "Open Sent, Sub VarLoad = ";
                            currentCapBank->setControlStatus(CtiCCCapBank::Open);

                        }
                        else if (currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                            currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending ||
                            currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ||
                            currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail )
                        {
                            text += "Close Sent, Sub VarLoad = ";
                            currentCapBank->setControlStatus(CtiCCCapBank::Close);
                        }

                        _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                        text += tempchar;
                        text += string(" Feeder VarLoad = ");
                        _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                        text += tempchar1;
                        pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                        pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                        currentCapBank->setLastStatusChangeTime(CtiTime());

                        INT seqId = CCEventSeqIdGen();
                        currentSubstationBus->setEventSequence(seqId);
                        ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser()));


                        if( controlID > 0 )
                        {
                            if (currentCapBank->getControlStatus() == CtiCCCapBank::Open) 
                            {
                                CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"control open");
                                reqMsg->setSOE(2);
                                pilMessages.push_back(reqMsg);
                            }
                            else //CLOSE
                            {
                                CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"control close");
                                reqMsg->setSOE(2);
                                pilMessages.push_back(reqMsg);
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
                        }


                    }

                }
            }
        }
    }
    if (multi->getCount() > 0 || multiPilMsg->getCount() > 0)
        CtiCapController::getInstance()->confirmCapBankControl(multiPilMsg, multi);
    if (eventMulti->getCount() > 0)
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    
}



/*---------------------------------------------------------------------------
    ConfirmOpen
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::ConfirmArea()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG subId = _command->getId();
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* confirmMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID(subId);
    if (currentSubstationBus != NULL)
    {
        string text1 = string("Manual Confirm Area");
        string additional1 = string("Area: ");
        additional1 += currentSubstationBus->getPAODescription();
        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
       // currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));



        string areaName = currentSubstationBus->getPAODescription();
        CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

        for(LONG i=0;i<ccSubstationBuses.size();i++)
        {
            currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];

            if (!stringCompareIgnoreCase(currentSubstationBus->getPAODescription(), areaName))
            {
                if (!currentSubstationBus->getVerificationFlag() && currentSubstationBus->getStrategyId() > 0)
                {
                    confirmMulti->insert(new CtiCCCommand(CtiCCCommand::CONFIRM_SUB, currentSubstationBus->getPAOId()));
                }
            }
        }
        if (confirmMulti->getCount() > 0)
        {
            CtiCCExecutorFactory f;
            CtiCCExecutor* executor = f.createExecutor(confirmMulti);
            executor->Execute();
        }
        if (eventMulti->getCount() > 0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        if (multi->getCount() > 0)
            CtiCapController::getInstance()->sendMessageToDispatch(multi);
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
    CtiTime savedBusLastOperationTime = gInvalidCtiTime;
    CtiTime savedFeederLastOperationTime = gInvalidCtiTime;
    CtiMultiMsg* multi = new CtiMultiMsg();

    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( bankID == currentCapBank->getControlDeviceId() )
                {
                    found = TRUE;
                    if (!currentSubstationBus->getVerificationFlag() && currentSubstationBus->getStrategyId() > 0)
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
                        currentSubstationBus->setLastOperationTime(CtiTime());
                        currentFeeder->setLastOperationTime(CtiTime());
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
                            char tempchar[80] = "";
                            char tempchar1[80] = "";
                            string additional("Sub: ");
                            additional += currentSubstationBus->getPAOName();
                            additional += string("  Feeder: ");
                            additional += currentFeeder->getPAOName();

                            string text("Manual Confirm Open Sent, Sub VarLoad = ");
                            _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                            text += tempchar;
                            text += string(" Feeder VarLoad = ");
                            _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                            text += tempchar1;
                            pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                            if( ( !savedBusRecentlyControlledFlag ||
                                  (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) && !savedFeederRecentlyControlledFlag) ) &&
                                 savedControlStatus != CtiCCCapBank::Open )
                            {
                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                                currentCapBank->setLastStatusChangeTime(CtiTime());


                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser()));
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                          << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                        }

                        if( currentCapBank->getOperationAnalogPointId() > 0 )
                        {

                            pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);


                            //currentFeeder->setEventSequence(currentFeeder->getEventSequence() + 1);
                            ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                        }

                        BOOL confirmImmediately = FALSE;
                        if( savedControlStatus == CtiCCCapBank::Open )
                        {
                            confirmImmediately = TRUE;
                        }
                        else if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) )
                        {
                            LONG sendRetries = currentSubstationBus->getControlSendRetries();
                            if (currentFeeder->getStrategyId() > 0 && currentFeeder->getControlSendRetries() > 0)
                                sendRetries = currentFeeder->getControlSendRetries();
                            if( savedFeederRecentlyControlledFlag ||
                                ((savedFeederLastOperationTime.seconds()+(currentSubstationBus->getMaxConfirmTime()/_SEND_TRIES)) >= currentFeeder->getLastOperationTime().seconds()) ||
                                ((savedFeederLastOperationTime.seconds()+(currentSubstationBus->getMaxConfirmTime()/(sendRetries+1))) >= currentFeeder->getLastOperationTime().seconds()) )
                            {
                                CtiCCCapBank_SVector& banks = currentFeeder->getCCCapBanks();

                                for(LONG l=0;l<banks.size();l++)
                                {
                                    CtiCCCapBank* bank = (CtiCCCapBank*)banks[l];
                                    if (bank->getStatusPointId() > 0)
                                    {
                                        if ((bank->getControlDeviceId() != bankID && (bank->getControlStatus() == CtiCCCapBank::OpenPending || bank->getControlStatus() == CtiCCCapBank::ClosePending ) ) ||
                                             savedControlStatus == CtiCCCapBank::OpenPending )
                                        {
                                            confirmImmediately = TRUE;
                                            break;
                                        }

                                    }
                                    //confirmImmediately = TRUE;
                                }
                            }
                            if( _IGNORE_NOT_NORMAL_FLAG &&
                                currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                            {
                                confirmImmediately = TRUE;
                            }
                        }
                        else if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::SubstationBusControlMethod) ||
                                 !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
                        {
                            LONG sendRetries = currentSubstationBus->getControlSendRetries();
                            if (currentFeeder->getStrategyId() > 0 && currentFeeder->getControlSendRetries() > 0)
                                sendRetries = currentFeeder->getControlSendRetries();

                            if( savedBusRecentlyControlledFlag ||
                                ((savedBusLastOperationTime.seconds()+(currentSubstationBus->getMaxConfirmTime()/_SEND_TRIES)) >= currentSubstationBus->getLastOperationTime().seconds()) ||
                                ((savedBusLastOperationTime.seconds()+(currentSubstationBus->getMaxConfirmTime()/(sendRetries+1))) >= currentSubstationBus->getLastOperationTime().seconds()) )
                            {
                                confirmImmediately = TRUE;
                            }
                            if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::SubstationBusControlMethod) &&
                                _IGNORE_NOT_NORMAL_FLAG &&
                                currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                            {
                                confirmImmediately = TRUE;
                            }
                            if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::BusOptimizedFeederControlMethod) &&
                                _IGNORE_NOT_NORMAL_FLAG &&
                                currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                            {
                                confirmImmediately = TRUE;
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Unknown Control Method: " << currentSubstationBus->getControlMethod()
                                          << " PAOID: " << currentSubstationBus->getPAOId() << " Name: " << currentSubstationBus->getPAOName() << endl;
                        }

                        if( !confirmImmediately )
                        {
                            currentSubstationBus->setRecentlyControlledFlag(TRUE);
                            currentFeeder->setRecentlyControlledFlag(TRUE);
                        }
                        else
                        {
                            doConfirmImmediately(currentSubstationBus,pointChanges, bankID);
                        }
                        break;
                    }
                    else
                    {
                        if (currentSubstationBus->getVerificationFlag())
                        {                                              
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                         <<".  Cannot perform CONFIRM CLOSE on Cap Bank: " << currentCapBank->getPAOName() << " PAOID: " << currentCapBank->getPAOId() << "."<<endl;
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - No Control Method: " << currentSubstationBus->getControlMethod()
                                          << " PAOID: " << currentSubstationBus->getPAOId() << " Name: " << currentSubstationBus->getPAOName() << endl;
                        }
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
        if (eventMulti->getCount() >0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
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
    CtiTime savedBusLastOperationTime = gInvalidCtiTime;
    CtiTime savedFeederLastOperationTime = gInvalidCtiTime;
    LONG savedControlStatus = -10;
    CtiMultiMsg* multi = new CtiMultiMsg();

    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( bankID == currentCapBank->getControlDeviceId() )
                {
                    found = TRUE;
                    if (!currentSubstationBus->getVerificationFlag() && currentSubstationBus->getStrategyId() > 0)
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
                        currentSubstationBus->setLastOperationTime(CtiTime());
                        currentFeeder->setLastOperationTime(CtiTime());
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
                            char tempchar[80] = "";
                            char tempchar1[80] = "";
                            string additional("Sub: ");
                            additional += currentSubstationBus->getPAOName();
                            additional += string("  Feeder: ");
                            additional += currentFeeder->getPAOName();

                            string text("Manual Confirm Close Sent, Sub VarLoad = ");
                            _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                            text += tempchar;
                            text += string(" Feeder VarLoad = ");
                            _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                            text += tempchar1;
                            pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                            if( ( !savedBusRecentlyControlledFlag ||
                                  (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) && !savedFeederRecentlyControlledFlag) ) &&
                                savedControlStatus != CtiCCCapBank::Close )
                            {
                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                                currentCapBank->setLastStatusChangeTime(CtiTime());

                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser()));
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                          << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                        }

                        if( currentCapBank->getOperationAnalogPointId() > 0 )
                        {

                            pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);


                            //currentFeeder->setEventSequence(currentFeeder->getEventSequence() + 1);
                            ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                        }

                        BOOL confirmImmediately = FALSE;
                        if( savedControlStatus == CtiCCCapBank::Close )
                        {
                            confirmImmediately = TRUE;
                        }
                        else if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) )
                        {
                            LONG sendRetries = currentSubstationBus->getControlSendRetries();
                            if (currentFeeder->getStrategyId() > 0 && currentFeeder->getControlSendRetries() > 0)
                                sendRetries = currentFeeder->getControlSendRetries();
                            if( savedFeederRecentlyControlledFlag ||
                                ((savedFeederLastOperationTime.seconds()+(currentSubstationBus->getMaxConfirmTime()/_SEND_TRIES)) >= currentFeeder->getLastOperationTime().seconds()) ||
                                ((savedFeederLastOperationTime.seconds()+(currentSubstationBus->getMaxConfirmTime()/(sendRetries+1))) >= currentFeeder->getLastOperationTime().seconds()) )
                            {
                                CtiCCCapBank_SVector& banks = currentFeeder->getCCCapBanks();

                                for(LONG l=0;l<banks.size();l++)
                                {
                                    CtiCCCapBank* bank = (CtiCCCapBank*)banks[l];
                                    if (bank->getStatusPointId() > 0)
                                    {
                                        if ((bank->getControlDeviceId() != bankID && (bank->getControlStatus() == CtiCCCapBank::OpenPending || bank->getControlStatus() == CtiCCCapBank::ClosePending ) ) ||
                                             savedControlStatus == CtiCCCapBank::ClosePending )
                                        {
                                            confirmImmediately = TRUE;
                                            break;
                                        }

                                    }
                                    //confirmImmediately = TRUE;
                                }                            }
                            if( _IGNORE_NOT_NORMAL_FLAG &&
                                currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                            {
                                confirmImmediately = TRUE;
                            }
                        }
                        else if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::SubstationBusControlMethod) ||
                                 !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
                        {
                            LONG sendRetries = currentSubstationBus->getControlSendRetries();
                            if (currentFeeder->getStrategyId() > 0 && currentFeeder->getControlSendRetries() > 0)
                                sendRetries = currentFeeder->getControlSendRetries();
                            if( savedBusRecentlyControlledFlag ||
                                ((savedBusLastOperationTime.seconds()+(currentSubstationBus->getMaxConfirmTime()/_SEND_TRIES)) >= currentSubstationBus->getLastOperationTime().seconds()) ||
                                ((savedBusLastOperationTime.seconds()+(currentSubstationBus->getMaxConfirmTime()/(sendRetries+1))) >= currentSubstationBus->getLastOperationTime().seconds()) )
                            {
                                confirmImmediately = TRUE;
                            }
                            if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::SubstationBusControlMethod) &&
                                _IGNORE_NOT_NORMAL_FLAG &&
                                currentSubstationBus->getCurrentVarPointQuality() != NormalQuality )
                            {
                                confirmImmediately = TRUE;
                            }
                            if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::BusOptimizedFeederControlMethod) &&
                                _IGNORE_NOT_NORMAL_FLAG &&
                                currentFeeder->getCurrentVarPointQuality() != NormalQuality )
                            {
                                confirmImmediately = TRUE;
                            }
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Unknown Control Method: " << currentSubstationBus->getControlMethod()
                                          << " PAOID: " << currentSubstationBus->getPAOId() << " Name: " << currentSubstationBus->getPAOName() << endl;
                            }
                            /*if (currentSubstationBus->getStrategyId() <= 0)
                            {
                                confirmImmediately = TRUE;
                            }*/
                        }

                        if( !confirmImmediately )
                        {
                            currentSubstationBus->setRecentlyControlledFlag(TRUE);
                            currentFeeder->setRecentlyControlledFlag(TRUE);
                        }
                        else
                        {
                            doConfirmImmediately(currentSubstationBus,pointChanges, bankID);
                        }
                        break;
                    }
                    else
                    {   
                        if (currentSubstationBus->getVerificationFlag())
                        {                                              
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                         <<".  Cannot perform CONFIRM CLOSE on Cap Bank: " << currentCapBank->getPAOName() << " PAOID: " << currentCapBank->getPAOId() << "."<<endl;
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - No Control Method: " << currentSubstationBus->getControlMethod()
                                          << " PAOID: " << currentSubstationBus->getPAOId() << " Name: " << currentSubstationBus->getPAOName() << endl;
                        }
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
        if (eventMulti->getCount() >0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}

/*---------------------------------------------------------------------------
    doConfirmImmediately
---------------------------------------------------------------------------*/    

void CtiCCCommandExecutor::doConfirmImmediately(CtiCCSubstationBus* currentSubstationBus, CtiMultiMsg_vec& pointChanges, LONG bankId)
{
    CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[currentSubstationBus->getLastFeederControlledPosition()];

    if( currentFeeder->getPAOId() == currentSubstationBus->getLastFeederControlledPAOId() )
    {
        CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

        for(LONG k=0;k<ccCapBanks.size();k++)
        {
            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
            if (bankId == currentCapBank->getControlDeviceId())
            {       
                currentFeeder->setRecentlyControlledFlag(FALSE);
                if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                {
                    currentCapBank->setControlStatus(CtiCCCapBank::Close);
                    if( currentCapBank->getStatusPointId() > 0 )
                    {
                        pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Close,NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(4);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                      << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                    }
                }
                else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                {
                    currentCapBank->setControlStatus(CtiCCCapBank::Open);
                    if( currentCapBank->getStatusPointId() > 0 )
                    {
                        pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Open,NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(4);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                      << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                    }
                }
            }
        }
        currentSubstationBus->setRecentlyControlledFlag(FALSE);

        for(int j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders[j];
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(int k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                    currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                {
                    currentSubstationBus->setRecentlyControlledFlag(TRUE);
                    currentFeeder->setRecentlyControlledFlag(TRUE);
                }
            }
        }

        if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) )
        {
            for(LONG x=0;x<ccFeeders.size();x++)
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
        for(LONG j=0;j<ccFeeders.size();j++)
        {
            currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            if( currentFeeder->getPAOId() == currentSubstationBus->getLastFeederControlledPAOId() )
            {
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(LONG k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    if (bankId == currentCapBank->getControlDeviceId())
                    {             
                        if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::Close);
                            if( currentCapBank->getStatusPointId() > 0 )
                            {
                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Close,NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(4);
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                              << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                            }
                        }
                        else if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                        {

                            currentCapBank->setControlStatus(CtiCCCapBank::Open);
                            if( currentCapBank->getStatusPointId() > 0 )
                            {
                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),CtiCCCapBank::Open,NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(4);
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                              << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                            }
                        }
                    }
                }
                currentSubstationBus->setRecentlyControlledFlag(FALSE);
                currentFeeder->setRecentlyControlledFlag(FALSE);

                if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) )
                {
                    for(LONG x=0;x<ccFeeders.size();x++)
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
void CtiCCCommandExecutor::SendAllData()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCExecutorFactory f;
    CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg(*(store->getCCSubstationBuses(CtiTime().seconds())), CtiCCSubstationBusMsg::AllSubBusesSent));
    executor->Execute();
    delete executor;
    executor = f.createExecutor(new CtiCCGeoAreasMsg(*store->getCCGeoAreas(CtiTime().seconds())));
    executor->Execute();
    delete executor;
    executor = f.createExecutor(new CtiCCCapBankStatesMsg(*store->getCCCapBankStates(CtiTime().seconds())));
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

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

            for(LONG k=0;k<ccCapBanks.size();k++)
            {
                CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                if( bankId == currentCapBank->getPAOId() )
                {
                    found = TRUE;
                    if (!currentSubstationBus->getVerificationFlag())
                    {

                        tempFeederId = currentFeeder->getPAOId();
                        movedCapBankId = bankId;
                        originalFeederId = currentCapBank->getOriginalFeederId();
                        capSwitchingOrder = currentCapBank->getOriginalSwitchingOrder();
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                         <<".  Cannot perform RETURN CAP TO ORIGINAL FEEDER on Cap Bank: " << currentCapBank->getPAOName() << " PAOID: " << currentCapBank->getPAOId() << "."<<endl;
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

    //moveCapBank(TRUE, oldFeederId, movedCapBankId, newFeederId, capSwitchingOrder);
    if( tempFeederId > 0 && movedCapBankId > 0 && originalFeederId > 0 )
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Move Cap Bank to original feeder PAO Id: " << movedCapBankId << endl;
        }
        moveCapBank(1, tempFeederId, movedCapBankId, originalFeederId, capSwitchingOrder);
    }
    else
    {
        if( tempFeederId==0 )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Feeder not found PAO Id: " << tempFeederId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
        if( originalFeederId==0 )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Feeder not found PAO Id: " << originalFeederId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
        if( movedCapBankId==0 )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Cap Bank not found PAO Id: " << movedCapBankId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
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

    CtiMultiMsg_vec& pointChanges = multiDispatchMsg->getData();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    LONG paoId = _command->getId();
    BOOL found = FALSE;

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);

        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

        if( currentSubstationBus->getPAOId() == paoId )
        {
            for(LONG j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(LONG k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                    if( currentCapBank->getOperationAnalogPointId() > 0 )
                    {
                        pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),0,ManualQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));

                        if (j == 0 && k == 0)
                        {                   
                            INT seqId = CCEventSeqIdGen();
                            currentSubstationBus->setEventSequence(seqId);
                        }
                        ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                    }
                    else
                    {
                        currentCapBank->setTotalOperations(0);
                        currentCapBank->setCurrentDailyOperations(0);
                    }
                }
                currentFeeder->setCurrentDailyOperations(0);
            }
            currentSubstationBus->setCurrentDailyOperations(0);
            currentSubstationBus->setBusUpdatedFlag(TRUE);
            found = TRUE;
        }
        else
        {
            for(LONG j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

                if( currentFeeder->getPAOId() == paoId )
                {
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(LONG k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                        if( currentCapBank->getOperationAnalogPointId() > 0 )
                        {
                            pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),0,ManualQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));

                            if (k == 0)
                            {                   
                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);
                            }
                            ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                        }
                        else
                        {
                            currentCapBank->setTotalOperations(0);
                            currentCapBank->setCurrentDailyOperations(0);
                        }
                    }

                    currentFeeder->setCurrentDailyOperations(0);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    found = TRUE;
                }
                else
                {
                    CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                    for(LONG k=0;k<ccCapBanks.size();k++)
                    {
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];
                        
                        if( currentCapBank->getPAOId() == paoId )
                        {
                            if( currentCapBank->getOperationAnalogPointId() > 0 )
                            {
                                pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),0,ManualQuality,AnalogPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));

                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), "opCount adjustment", _command->getUser()));
                            }
                            else
                            {
                                currentCapBank->setTotalOperations(0);
                                currentCapBank->setCurrentDailyOperations(0);
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
			dout << CtiTime() << "Reset Daily Operations for PAO Id: " << paoId << endl;
		}
		if( pointChanges.size() > 0 )
		{
			CtiCapController::getInstance()->sendMessageToDispatch(multiDispatchMsg);
		}
        if (ccEvents.size() > 0)                                                  
        {
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << "Cannot find PAO Id: " << paoId << " in ResetDailyOperations() in: " << __FILE__ << " at: " << __LINE__ << endl;
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
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        if( subID == currentSubstationBus->getPAOId() )
        {
            if (!currentSubstationBus->getVerificationFlag())
            {    
                currentSubstationBus->setWaiveControlFlag(TRUE);
                currentSubstationBus->setBusUpdatedFlag(TRUE);
                string text("Substation Bus Waived");
                string additional("Bus: ");
                additional += currentSubstationBus->getPAOName();
                CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                 <<".  Cannot perform WAIVE SUBSTATION BUS."<<endl;
            }
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
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        if( subID == currentSubstationBus->getPAOId() )
        {
            if (!currentSubstationBus->getVerificationFlag())
            {   
                currentSubstationBus->setWaiveControlFlag(FALSE);
                currentSubstationBus->setBusUpdatedFlag(TRUE);
                string text("Substation Bus Unwaived");
                string additional("Bus: ");
                additional += currentSubstationBus->getPAOName();
                CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                 <<".  Cannot perform UNWAIVE SUBSTATION BUS."<<endl;
            }
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
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            if( feederID == currentFeeder->getPAOId() )
            {
                if (!currentSubstationBus->getVerificationFlag())
                {
                    currentFeeder->setWaiveControlFlag(TRUE);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    string text("Feeder Waived");
                    string additional("Feeder: ");
                    additional += currentFeeder->getPAOName();
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                     <<".  Cannot perform WAIVE FEEDER on Feeder: "<<currentFeeder->getPAOName()<<" PAOID: "<<currentFeeder->getPAOId()<<"."<<endl;
                }
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
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            if( feederID == currentFeeder->getPAOId() )
            {
                if (!currentSubstationBus->getVerificationFlag())
                {
                    currentFeeder->setWaiveControlFlag(FALSE);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    string text("Feeder Unwaived");
                    string additional("Feeder: ");
                    additional += currentFeeder->getPAOName();
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
                }
                else
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                     <<".  Cannot perform UNWAIVE FEEDER on Feeder: "<<currentFeeder->getPAOName()<<" PAOID: "<<currentFeeder->getPAOId()<<"."<<endl;
                }
                break;
            }
        }
    }
}

/*===========================================================================
    CtiCCSubstationVerificationExecutor
===========================================================================*/
/*---------------------------------------------------------------------------
    Execute
---------------------------------------------------------------------------*/    
void CtiCCSubstationVerificationExecutor::Execute()
{
    LONG strategy = _subVerificationMsg->getStrategy();
    LONG action = _subVerificationMsg->getAction();
    LONG subBusId = _subVerificationMsg->getSubBusId();
    LONG inactivityTime = _subVerificationMsg->getInactivityTime();

    switch (action)
    {
        case CtiCCSubstationVerificationMsg::DISABLE_SUBSTATION_BUS_VERIFICATION:
        DisableSubstationBusVerification();
        break;

        case CtiCCSubstationVerificationMsg::ENABLE_SUBSTATION_BUS_VERIFICATION:
        EnableSubstationBusVerification();
        break;
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
    
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    BOOL found = FALSE;
    BOOL verificationFlag = FALSE;

    for(LONG i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);
        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
            if( oldFeederPtr==NULL && currentFeeder->getPAOId()==oldFeederId )
            {
                if ( currentSubstationBus->getVerificationFlag() )
                {
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                     <<".  Cannot perform MOVE CAPBANK on Feeder: "<<currentFeeder->getPAOName()<<" PAOID: "<<currentFeeder->getPAOId()<<"."<<endl;
                    }
                    verificationFlag = TRUE;
                    found = TRUE;
                }
                else
                {
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    oldFeederPtr = currentFeeder;
                }
            }
            if( newFeederPtr==NULL && currentFeeder->getPAOId()==newFeederId  )
            {
                if ( currentSubstationBus->getVerificationFlag() )
                {
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                     <<".  Cannot perform MOVE CAPBANK on Feeder: "<<currentFeeder->getPAOName()<<" PAOID: "<<currentFeeder->getPAOId()<<"."<<endl;
                    }
                    verificationFlag = TRUE;
                    found = TRUE;
                }
                else
                {
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
                    newFeederPtr = currentFeeder;
                }
            }

            if( movedCapBankPtr==NULL )
            {
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                for(LONG k=0;k<ccCapBanks.size();k++)
                {
                    CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[k];

                    if( currentCapBank->getPAOId()==movedCapBankId )
                    {
                        if ( currentSubstationBus->getVerificationFlag() )
                        {
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                             <<".  Cannot perform MOVE CAPBANK on CapBank: "<<currentCapBank->getPAOName()<<" PAOID: "<<currentCapBank->getPAOId()<<"."<<endl;
                            }
                            verificationFlag = TRUE;
                            found = TRUE;
                        }
                        else
                        {
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            movedCapBankPtr = currentCapBank;
                        }
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
            CtiCCCapBank_SVector& oldFeederCapBanks = oldFeederPtr->getCCCapBanks();

            CtiCCCapBank_SVector::iterator itr = oldFeederCapBanks.begin();
            while( itr != oldFeederCapBanks.end() )
            {
                if (*itr == movedCapBankPtr) {
                    itr = oldFeederCapBanks.erase( itr );
                }else
                    ++itr;
            }

            store->removeItemsFromMap(CtiCCSubstationBusStore::CapBankIdFeederIdMap, movedCapBankId);
            store->removeItemsFromMap(CtiCCSubstationBusStore::CapBankIdSubBusIdMap, movedCapBankId);


            
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

            movedCapBankPtr->setParentId(newFeederId);

            if( oldFeederCapBanks.size() > 0 )
            {
                //reshuffle the cap bank control orders so they are still in sequence and start at 1
                LONG shuffledOrder = 1;

                
                CtiCCCapBank_SVector tempShufflingCapBankList;


                while(!oldFeederCapBanks.empty())
                {
                    //have to remove due to change in sorting field in a sorted vector
                    CtiCCCapBank* currentCapBank = *(oldFeederCapBanks.begin());
                    oldFeederCapBanks.erase(oldFeederCapBanks.begin());
                    currentCapBank->setControlOrder(shuffledOrder);
                    tempShufflingCapBankList.insert(currentCapBank);
                    shuffledOrder++;
                }
                while(tempShufflingCapBankList.size()>0)
                {
                    oldFeederCapBanks.push_back(tempShufflingCapBankList.at(0));
                    tempShufflingCapBankList.erase(oldFeederCapBanks.begin());
                }
            }
        }

        {
            CtiCCCapBank_SVector& newFeederCapBanks = newFeederPtr->getCCCapBanks();

            if( newFeederCapBanks.size() > 0 )
            {
                //search through the list to see if there is a cap bank in the
                //list that already has the switching order
                if( capSwitchingOrder >= ((CtiCCCapBank*)newFeederCapBanks[newFeederCapBanks.size()-1])->getControlOrder() )
                {
                    movedCapBankPtr->setControlOrder( ((CtiCCCapBank*)newFeederCapBanks[newFeederCapBanks.size()-1])->getControlOrder() + 1 );
                }
                else
                {
                    BOOL shuffling = FALSE;
                    movedCapBankPtr->setControlOrder(capSwitchingOrder);
                    for(LONG k=0;k<newFeederCapBanks.size();k++)
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
                    CtiCCCapBank_SVector tempShufflingCapBankList;
                    while(newFeederCapBanks.size()>0)
                    {
                        //have to remove due to change in sorting field in a sorted vector
                        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)newFeederCapBanks.front();
                        newFeederCapBanks.erase(newFeederCapBanks.begin());
                        if( capSwitchingOrder == currentCapBank->getControlOrder() )
                        {
                            //have to make room for the movedCapBank
                            shuffledOrder++;
                        }
                        currentCapBank->setControlOrder(shuffledOrder);
                        tempShufflingCapBankList.push_back(currentCapBank);
                        shuffledOrder++;
                    }
                    while(tempShufflingCapBankList.size()>0)
                    {
                        newFeederCapBanks.push_back(tempShufflingCapBankList.at(0));
                        tempShufflingCapBankList.erase(tempShufflingCapBankList.begin());
                    }
                }
            }
            else
            {
                movedCapBankPtr->setControlOrder(1);
            }

            newFeederCapBanks.push_back(movedCapBankPtr);
            store->insertItemsIntoMap(CtiCCSubstationBusStore::CapBankIdFeederIdMap, &movedCapBankId, &newFeederId);
            long subBusId = store->findSubBusIDbyFeederID(newFeederId);
            if (subBusId != NULL)
            {
                store->insertItemsIntoMap(CtiCCSubstationBusStore::CapBankIdSubBusIdMap, &movedCapBankId, &subBusId);
            }

        }

        store->UpdateFeederBankListInDB(oldFeederPtr);
        store->UpdateFeederBankListInDB(newFeederPtr);

        {
            string typeString = (permanentFlag?"Temporary":"Permanent");

            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Manual "
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
        if (!verificationFlag)
        {    
            if( oldFeederPtr==NULL )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Feeder not found PAO Id: " << oldFeederId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
            }
            if( newFeederPtr==NULL )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Feeder not found PAO Id: " << newFeederId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
            }
            if( movedCapBankPtr==NULL )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Cap Bank not found PAO Id: " << movedCapBankId << " in: " << __FILE__ << " at: " << __LINE__ << endl;
            } 
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
    CtiTime& timestamp = _pointDataMsg->getTime();

    if( _CC_DEBUG & CC_DEBUG_STANDARD )
	{
        char tempchar[80];
        string outString = "Manual point value received from client, ID:";
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
        outString += CtiTime(timestamp).asString();

        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - " << outString.c_str() << endl;
	}

    BOOL found = FALSE;

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    for(int i=0;i<ccSubstationBuses.size();i++)
    {
        CtiCCSubstationBus* currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses.at(i);

        if( !found )
        {
            if( pointID == currentSubstationBus->getCurrentVarLoadPointId() ||
                pointID == currentSubstationBus->getEstimatedVarLoadPointId() )
            {
                found = TRUE;
                break;
            }

            CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

            for(int j=0;j<ccFeeders.size();j++)
            {
                CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                for(int k=0;k<ccCapBanks.size();k++)
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
                                if( !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(),CtiCCSubstationBus::IndividualFeederControlMethod) )
                                {
                                    for(LONG x=0;x<ccFeeders.size();x++)
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
    _pointDataMsg->setTags(tags | TAG_POINT_FORCE_UPDATE);

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
    CtiMultiMsg_vec& messages = _multiMsg->getData();
    while(messages.size( )>=0)
    {
        CtiMessage* message = (CtiMessage*)(messages.back());
        messages.pop_back();
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
            dout << CtiTime() << " - Shutting down client listener thread..." << endl;
        }

        CtiCCClientListener::getInstance()->stop();

        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Client listener thread shutdown." << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    try
    {
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Shutting down cap controller thread..." << endl;
        }
    
        CtiCapController::getInstance()->stop();

        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Cap controller thread shutdown." << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    try
    {
        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Shutting down substation bus store..." << endl;
        }
    
        CtiCCSubstationBusStore::deleteInstance();

        if( _CC_DEBUG & CC_DEBUG_STANDARD )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Substation bus store shutdown." << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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
        //case CTICCITEMDELETE_MSG_ID:
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

        case CTICCSUBVERIFICATIONMSG_ID:
            ret_val = new CtiCCSubstationVerificationExecutor( (CtiCCSubstationVerificationMsg*)message );
            break;

        case CTICCSHUTDOWN_ID:
            ret_val = new CtiCCShutdownExecutor();
            break;
    
        default:
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - CtiCCExecutorFactory::createExecutor - Warning unknown classId: " << classId << endl;
            }
    }

    return ret_val;
}
