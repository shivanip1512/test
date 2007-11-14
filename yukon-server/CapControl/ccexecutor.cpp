/*-----------------------------------------------------------------------------
    Filename:  ccexecutor.cpp
    
    Programmer:  Josh Wolberg
    
    Description:    Defines Cap Control executor classes.

    Initial Date:  8/30/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <list>
using std::list;
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
extern BOOL _LOG_MAPID_INFO;

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
    case CtiCCCommand::ENABLE_AREA:
        EnableArea();
        break;
    case CtiCCCommand::DISABLE_AREA:
        DisableArea();
        break;
    case CtiCCCommand::ENABLE_SYSTEM:
        EnableSystem();
        break;
    case CtiCCCommand::DISABLE_SYSTEM:
        DisableSystem();
        break;
    case CtiCCCommand::SCAN_2WAY_DEVICE:
        Scan2WayDevice();
        break;
    case CtiCCCommand::FLIP_7010_CAPBANK:
        Flip7010Device();
        break;
    case CtiCCCommand::SYSTEM_STATUS:
        SendSystemStatus();
        break;
    case CtiCCCommand::SEND_ALL_OPEN:
    case CtiCCCommand::SEND_ALL_CLOSE:
    case CtiCCCommand::SEND_ALL_ENABLE_OVUV:
    case CtiCCCommand::SEND_ALL_DISABLE_OVUV:
    case CtiCCCommand::SEND_ALL_SCAN_2WAY_DEVICE:
        SendAllCapBankCommands(); 
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
                    if( _CC_DEBUG & CC_DEBUG_STANDARD )
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Verification Start Message received from client for sub: "<<currentSubstationBus->getPAOName()<<" ("<<currentSubstationBus->getPAOId()<<")"<< endl;
                    }

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
                                string text =  "Verification will be delayed for Sub: ";
                                text += currentSubstationBus->getPAOName();
                                text += ", Cap Bank: ";
                                text += currentCapBank->getPAOName();
                                text += " is being controlled.";


                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);
                                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), 0, capControlEnableVerification, currentSubstationBus->getEventSequence(), 1, text, "cap control"));
                                
                                if( _CC_DEBUG & CC_DEBUG_STANDARD )
                                {
                                    CtiLockGuard<CtiLogger> logger_guard(dout);
                                    dout << CtiTime() <<  " - Verification will be delayed until Current Cap Bank Control is completed."<< endl;
                                }

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
                    if (_LOG_MAPID_INFO) 
                    {
                        additional += " MapID: ";
                        additional += currentSubstationBus->getMapLocationId();
                        additional += " (";
                        additional += currentSubstationBus->getPAODescription();
                        additional += ")";
                    }
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
                        if (_LOG_MAPID_INFO) 
                        {
                            additional += " MapID: ";
                            additional += currentSubstationBus->getMapLocationId();
                            additional += " (";
                            additional += currentSubstationBus->getPAODescription();
                            additional += ")";
                        }
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
                if (_LOG_MAPID_INFO) 
                {
                    additional += " MapID: ";
                    additional += currentSubstationBus->getMapLocationId();
                    additional += " (";
                    additional += currentSubstationBus->getPAODescription();
                    additional += ")";
                }
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
                if (_LOG_MAPID_INFO) 
                {
                    additional += " MapID: ";
                    additional += currentSubstationBus->getMapLocationId();
                    additional += " (";
                    additional += currentSubstationBus->getPAODescription();
                    additional += ")";
                }
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
            if (_LOG_MAPID_INFO) 
            {
                additional += " MapID: ";
                additional += currentSubstationBus->getMapLocationId();
                additional += " (";
                additional += currentSubstationBus->getPAODescription();
                additional += ")";
            }
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
            if (_LOG_MAPID_INFO) 
            {
                additional += " MapID: ";
                additional += currentSubstationBus->getMapLocationId();
                additional += " (";
                additional += currentSubstationBus->getPAODescription();
                additional += ")";
            }
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
                    if (_LOG_MAPID_INFO) 
                    {
                        additional += " MapID: ";
                        additional += currentFeeder->getMapLocationId();
                        additional += " (";
                        additional += currentFeeder->getPAODescription();
                        additional += ")";
                    }
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
                    if (_LOG_MAPID_INFO) 
                    {
                        additional += " MapID: ";
                        additional += currentFeeder->getMapLocationId();
                        additional += " (";
                        additional += currentFeeder->getPAODescription();
                        additional += ")";
                    }
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
        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)ccFeeders[j];
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

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
                        if (_LOG_MAPID_INFO) 
                        {
                            additional += " MapID: ";
                            additional += currentCapBank->getMapLocationId();
                            additional += " (";
                            additional += currentCapBank->getPAODescription();
                            additional += ")";
                        }
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,_command->getUser(), TAG_POINT_FORCE_UPDATE));

                            INT seqId = CCEventSeqIdGen();
                            currentSubstationBus->setEventSequence(seqId);
                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlEnable, currentSubstationBus->getEventSequence(), 1, text, _command->getUser()));
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                          << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;

                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
                        }
                        if( currentCapBank->getOperationAnalogPointId() > 0 )
                        {
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),NormalQuality,StatusPointType,_command->getUser(), TAG_POINT_FORCE_UPDATE));
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
        for(LONG j=0;j<ccFeeders.size();j++)
        {
            CtiCCFeederPtr currentFeeder = (CtiCCFeederPtr)ccFeeders[j];
            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

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
                        if (_LOG_MAPID_INFO) 
                        {
                            additional += " MapID: ";
                            additional += currentCapBank->getMapLocationId();
                            additional += " (";
                            additional += currentCapBank->getPAODescription();
                            additional += ")";
                        }
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
    BOOL cbc702 = FALSE;

    CtiMultiMsg* multi = new CtiMultiMsg();

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
                if( currentCapBank->getControlDeviceId() > 0 &&
                    bankID == currentCapBank->getControlDeviceId() )
                {
                    if (stringContainsIgnoreCase( currentCapBank->getControlDeviceType(),"CBC 702")) 
                        cbc702 = TRUE;
                    
                    controlID = currentCapBank->getControlDeviceId();

                    string text = string("Cap Bank OV/UV Enabled");
                    string additional = string("Cap Bank: ");
                    additional += currentCapBank->getPAOName();
                    if (_LOG_MAPID_INFO) 
                    {
                        additional += " MapID: ";
                        additional += currentCapBank->getMapLocationId();
                        additional += " (";
                        additional += currentCapBank->getPAODescription();
                        additional += ")";
                    }
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getControlPointId(),0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));


                    INT seqId = CCEventSeqIdGen();
                    currentSubstationBus->setEventSequence(seqId);
                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, currentCapBank->getControlPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlEnableOvUv, currentSubstationBus->getEventSequence(), 1, text, _command->getUser()));

                    currentCapBank->setOvUvDisabledFlag(FALSE);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
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

        CtiRequestMsg* reqMsg = NULL;
        if (cbc702) 
        {
            reqMsg = new CtiRequestMsg(controlID,"putvalue analog 1 1");
        }
        else
        {
            reqMsg = new CtiRequestMsg(controlID,"putconfig ovuv enable");
        }
      
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );

        
        //CtiCapController::getInstance()->manualCapBankControl( reqMsg );
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - ControlDeviceID: "<<controlID<<" - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
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
    BOOL cbc702 = FALSE;
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
                if( currentCapBank->getControlDeviceId() > 0 &&
                    bankID == currentCapBank->getControlDeviceId() )
                {
                    if (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 702")) 
                        cbc702 = TRUE;

                    controlID = currentCapBank->getControlDeviceId();

                    string text = string("Cap Bank OV/UV Disabled");
                    string additional = string("Cap Bank: ");
                    additional += currentCapBank->getPAOName();
                    if (_LOG_MAPID_INFO) 
                    {
                        additional += " MapID: ";
                        additional += currentCapBank->getMapLocationId();
                        additional += " (";
                        additional += currentCapBank->getPAODescription();
                        additional += ")";
                    }
                    CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                    INT seqId = CCEventSeqIdGen();
                    currentSubstationBus->setEventSequence(seqId);
                    CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlDisableOvUv, currentSubstationBus->getEventSequence(), 0, text, _command->getUser()));

                    currentCapBank->setOvUvDisabledFlag(TRUE);
                    currentSubstationBus->setBusUpdatedFlag(TRUE);
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
        CtiRequestMsg* reqMsg = NULL;
        if (cbc702)
        {
            reqMsg = new CtiRequestMsg(controlID,"putvalue analog 1 0");
        }
        else
        {
            reqMsg = new CtiRequestMsg(controlID,"putconfig ovuv disable");
        }

        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );

        
        //CtiCapController::getInstance()->manualCapBankControl( reqMsg );
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - ControlDeviceID: "<<controlID<<" - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
    }

}

void CtiCCCommandExecutor::SendAllCapBankCommands()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG paoId = _command->getId();
    LONG controlID = 0;
    BOOL found = FALSE;
    string actionText = "";
    LONG action = CtiCCCommand::UNDEFINED;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* actionMulti = new CtiMultiMsg();
    CtiMultiMsg* multiPilMsg = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pilMessages = multiPilMsg->getData();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();
    CtiMultiMsg_vec modifiedSubsList;
    modifiedSubsList.clear();

    switch ( _command->getCommand() ) 
    {       

        case CtiCCCommand::SEND_ALL_OPEN:
        {
            actionText = string(" - Open");
            action = CtiCCCommand::OPEN_CAPBANK;
            break;
        }
        case CtiCCCommand::SEND_ALL_CLOSE:
        {
            actionText = string(" - Close");
            action = CtiCCCommand::CLOSE_CAPBANK;
            break;
        }
        case CtiCCCommand::SEND_ALL_ENABLE_OVUV:
        {
            actionText = string(" - Enable OvUv");
            action = CtiCCCommand::ENABLE_OVUV;
            break;
        }
        case CtiCCCommand::SEND_ALL_DISABLE_OVUV:
        {
            actionText = string(" - Disable OvUv");
            action = CtiCCCommand::DISABLE_OVUV;
            break;
        }
        case CtiCCCommand::SEND_ALL_SCAN_2WAY_DEVICE:
        {
            actionText = string(" - Integrity Scan");
            action = CtiCCCommand::SCAN_2WAY_DEVICE;
            break;
        }
        default:
        {
            actionText = string(" - ERROR...");
            break;
        }    
    }


    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    CtiCCSpArea_vec& ccSpAreas = *store->getCCSpecialAreas(CtiTime().seconds());

    CtiCCSpecial* currentSpArea = NULL;
    CtiCCAreaPtr currentArea = NULL;
    CtiCCSubstationPtr currentStation = NULL;
    CtiCCSubstationBusPtr currentSubstationBus = NULL;
    CtiCCFeederPtr currentFeeder = store->findFeederByPAObjectID(paoId);
    if (currentFeeder != NULL) 
    {
        currentSubstationBus = store->findSubBusByPAObjectID(currentFeeder->getParentId());
        if (currentSubstationBus != NULL) 
        {
            currentStation = store->findSubstationByPAObjectID(currentSubstationBus->getParentId());
            if (currentStation != NULL)
            {   
                currentArea = store->findAreaByPAObjectID(currentStation->getParentId());
                if (currentArea != NULL) 
                {
                    if (_command->getCommand() == CtiCCCommand::SEND_ALL_ENABLE_OVUV) 
                        currentFeeder->setOvUvDisabledFlag(FALSE);
                    if (_command->getCommand() == CtiCCCommand::SEND_ALL_DISABLE_OVUV) 
                        currentFeeder->setOvUvDisabledFlag(TRUE);
                    if (((action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK) &&
                         !currentSubstationBus->getDisableFlag() && !currentArea->getDisableFlag())  ||
                         (action  == CtiCCCommand::ENABLE_OVUV || action  == CtiCCCommand::DISABLE_OVUV ||
                          action  == CtiCCCommand::SCAN_2WAY_DEVICE) ) 
                    {
                        string text1 = string("Feeder: ");
                        text1 += currentFeeder->getPAOName();
                        text1 += actionText;
                        text1 += string(" All CapBanks");
                        string additional1 = string("Feeder: ");
                        additional1 += currentFeeder->getPAOName();
                        if (((action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK) &&
                             !currentFeeder->getDisableFlag())  ||
                             (action  == CtiCCCommand::ENABLE_OVUV || action  == CtiCCCommand::DISABLE_OVUV ||
                          action  == CtiCCCommand::SCAN_2WAY_DEVICE) ) 
                        {
                            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
                            currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
                            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));
                            
                            CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                            
                            for(LONG k=0;k<ccCapBanks.size();k++)
                            {
                                CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];
                                if (((action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK) &&
                                    !currentCapBank->getDisableFlag() && 
                                    !stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) ) ||
                                    (action  == CtiCCCommand::ENABLE_OVUV || action  == CtiCCCommand::DISABLE_OVUV ||
                                     action  == CtiCCCommand::SCAN_2WAY_DEVICE) ) 
                                {
                                    actionMulti->insert(new CtiCCCommand(action, currentCapBank->getControlDeviceId()));
                                }
                            }
                            modifiedSubsList.push_back(currentSubstationBus);
                        }
                    }
                    else
                    {
                        if( currentSubstationBus->getDisableFlag())
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Sub: "<<currentSubstationBus->getPAOName()<<" DISABLED "<<actionText<<" Inhibited. " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                        if (currentArea->getDisableFlag() )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Area: "<<currentArea->getPAOName()<<" DISABLED "<<actionText<<" Inhibited. " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                    }
                }
            }
        }
    }
    if (currentSubstationBus == NULL) 
    {
        currentSubstationBus = store->findSubBusByPAObjectID(paoId);
        if (currentSubstationBus != NULL) 
        {
            currentStation = store->findSubstationByPAObjectID(currentSubstationBus->getParentId());
            if (currentStation != NULL)
            {   
                currentArea = store->findAreaByPAObjectID(currentStation->getParentId());
                if (currentArea != NULL) 
                {
                    if (_command->getCommand() == CtiCCCommand::SEND_ALL_ENABLE_OVUV) 
                        currentSubstationBus->setOvUvDisabledFlag(FALSE);
                    if (_command->getCommand() == CtiCCCommand::SEND_ALL_DISABLE_OVUV) 
                        currentSubstationBus->setOvUvDisabledFlag(TRUE);
              
                    string text1 = string("SubBus: ");
                    text1 += currentSubstationBus->getPAOName();
                    text1 += actionText;
                    text1 += string(" All CapBanks");
                    string additional1 = string("SubBus: ");
                    additional1 += currentSubstationBus->getPAOName();
                    if (((action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK) &&
                          !currentSubstationBus->getDisableFlag() && !currentArea->getDisableFlag())  ||
                          (action  == CtiCCCommand::ENABLE_OVUV || action  == CtiCCCommand::DISABLE_OVUV ||
                          action  == CtiCCCommand::SCAN_2WAY_DEVICE) ) 
                    {
                        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
                        currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
                        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));
                       
                        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
                       
                        for(LONG j=0;j<ccFeeders.size();j++)
                        {
                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                       
                            if (_command->getCommand() == CtiCCCommand::SEND_ALL_ENABLE_OVUV) 
                                currentFeeder->setOvUvDisabledFlag(FALSE);
                            if (_command->getCommand() == CtiCCCommand::SEND_ALL_DISABLE_OVUV) 
                                currentFeeder->setOvUvDisabledFlag(TRUE);
                            if (((action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK) &&
                                 !currentFeeder->getDisableFlag())  ||
                                 (action  == CtiCCCommand::ENABLE_OVUV || action  == CtiCCCommand::DISABLE_OVUV ||
                                  action  == CtiCCCommand::SCAN_2WAY_DEVICE) ) 
                            {
                                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                       
                                for(LONG k=0;k<ccCapBanks.size();k++)
                                {
                                    CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];
                                    if (((action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK) &&
                                         !currentCapBank->getDisableFlag() && 
                                         !stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) ) ||
                                        (action  == CtiCCCommand::ENABLE_OVUV || action  == CtiCCCommand::DISABLE_OVUV ||
                                         action  == CtiCCCommand::SCAN_2WAY_DEVICE) ) 
                                    {
                                        actionMulti->insert(new CtiCCCommand(action, currentCapBank->getControlDeviceId()));
                                    }  
                       
                                }
                            }
                        }
                        modifiedSubsList.push_back(currentSubstationBus);
                    }
                    else
                    {
                        if( currentSubstationBus->getDisableFlag())
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - SubBus: "<<currentSubstationBus->getPAOName()<<" DISABLED "<<actionText<<" Inhibited. " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                        if (currentStation->getDisableFlag() )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Substation: "<<currentStation->getPAOName()<<" DISABLED "<<actionText<<" Inhibited. " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                        if (currentArea->getDisableFlag() )
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " - Area: "<<currentArea->getPAOName()<<" DISABLED "<<actionText<<" Inhibited. " << __FILE__ << " at: " << __LINE__ << endl;
                        }
                    }
                }
            }
        }
    }
    if (currentArea == NULL) 
    {
        currentArea = store->findAreaByPAObjectID(paoId);
        if (currentArea != NULL) 
        {
            string text1 = string("Area: ");
            text1 += currentArea->getPAOName();
            text1 += actionText;
            text1 += string(" All CapBanks");
            string additional1 = string("Area: ");
            additional1 += currentArea->getPAOName();

            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            if (_command->getCommand() == CtiCCCommand::SEND_ALL_ENABLE_OVUV) 
                currentArea->setOvUvDisabledFlag(FALSE);
            if (_command->getCommand() == CtiCCCommand::SEND_ALL_DISABLE_OVUV) 
                currentArea->setOvUvDisabledFlag(TRUE);
            std::list <long>::iterator subIter = currentArea->getSubStationList()->begin();

            while (subIter != currentArea->getSubStationList()->end())
            {
                currentStation = store->findSubstationByPAObjectID(*subIter);
                subIter++;

                if (currentStation != NULL)
                {
                
                    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());
                    for(LONG i=0;i<ccSubstationBuses.size();i++)
                    {
                         currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
                         
                        if (currentSubstationBus->getParentId() == currentStation->getPAOId())
                        {
                            if (_command->getCommand() == CtiCCCommand::SEND_ALL_ENABLE_OVUV) 
                                currentSubstationBus->setOvUvDisabledFlag(FALSE);
                            if (_command->getCommand() == CtiCCCommand::SEND_ALL_DISABLE_OVUV) 
                                currentSubstationBus->setOvUvDisabledFlag(TRUE);
                            if (((action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK) &&
                                   !currentSubstationBus->getDisableFlag() && !currentArea->getDisableFlag())  ||
                                   (action  == CtiCCCommand::ENABLE_OVUV || action  == CtiCCCommand::DISABLE_OVUV ||
                                    action  == CtiCCCommand::SCAN_2WAY_DEVICE) ) 
                            {
                                currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));
                               
                                CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();
                               
                                for(LONG j=0;j<ccFeeders.size();j++)
                                {
                                    CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);
                   
                                    if (_command->getCommand() == CtiCCCommand::SEND_ALL_ENABLE_OVUV) 
                                        currentFeeder->setOvUvDisabledFlag(FALSE);
                                    if (_command->getCommand() == CtiCCCommand::SEND_ALL_DISABLE_OVUV) 
                                        currentFeeder->setOvUvDisabledFlag(TRUE);
                                    if (((action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK) &&
                                       !currentFeeder->getDisableFlag())  ||
                                       (action  == CtiCCCommand::ENABLE_OVUV || action  == CtiCCCommand::DISABLE_OVUV ||
                                        action  == CtiCCCommand::SCAN_2WAY_DEVICE) ) 
                                    {
                                        CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();
                                   
                                        for(LONG k=0;k<ccCapBanks.size();k++)
                                        {
                                            CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];
                   
                                            if (((action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK) &&
                                                !currentCapBank->getDisableFlag()   && 
                                                !stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) ) ||
                                                (action  == CtiCCCommand::ENABLE_OVUV || action  == CtiCCCommand::DISABLE_OVUV ||
                                                 action  == CtiCCCommand::SCAN_2WAY_DEVICE) ) 
                                            {
                                                actionMulti->insert(new CtiCCCommand(action, currentCapBank->getControlDeviceId()));
                                            }
                                        }
                                    }
                                }
                   
                                modifiedSubsList.push_back(currentSubstationBus);
                            }
                        }
                    }
                }
            }
        }
        else // didn't find feeder, subbus, or area...so we'll try last resort, special area.
        {
            if (currentSpArea == NULL) 
            {
                currentSpArea = store->findSpecialAreaByPAObjectID(paoId);
                if (currentSpArea != NULL) 
                {
                    string text1 = string("Special Area: ");
                    text1 += currentSpArea->getPAOName();
                    text1 += actionText;
                    text1 += string(" All CapBanks");
                    string additional1 = string("Special Area: ");
                    additional1 += currentSpArea->getPAOName();

                    pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
                    if (_command->getCommand() == CtiCCCommand::SEND_ALL_ENABLE_OVUV) 
                        currentSpArea->setOvUvDisabledFlag(FALSE);
                    if (_command->getCommand() == CtiCCCommand::SEND_ALL_DISABLE_OVUV) 
                        currentSpArea->setOvUvDisabledFlag(TRUE);

                    std::list <long>::iterator subIter = currentSpArea->getSubstationIds()->begin();

                    while (subIter != currentSpArea->getSubstationIds()->end())
                    {
                        currentStation = store->findSubstationByPAObjectID(*subIter);
                        subIter++;
                        if (currentStation != NULL)
                        {
                        
                            CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());
                            for(LONG i=0;i<ccSubstationBuses.size();i++)
                            {
                                 currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];


                                if (currentSubstationBus->getParentId() == currentStation->getPAOId())
                                {
                                    if (_command->getCommand() == CtiCCCommand::SEND_ALL_ENABLE_OVUV) 
                                        currentSubstationBus->setOvUvDisabledFlag(FALSE);
                                    if (_command->getCommand() == CtiCCCommand::SEND_ALL_DISABLE_OVUV) 
                                        currentSubstationBus->setOvUvDisabledFlag(TRUE);
                                    if (((action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK) &&
                                           !currentSubstationBus->getDisableFlag() && !currentSpArea->getDisableFlag())  ||
                                           (action  == CtiCCCommand::ENABLE_OVUV || action  == CtiCCCommand::DISABLE_OVUV ||
                                            action  == CtiCCCommand::SCAN_2WAY_DEVICE) ) 
                                    {
                                        currentSubstationBus->setEventSequence(currentSubstationBus->getEventSequence() +1);
                                        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), 0, capControlManualCommand, currentSubstationBus->getEventSequence(), 0, text1, _command->getUser()));

                                        CtiFeeder_vec& ccFeeders = currentSubstationBus->getCCFeeders();

                                        for(LONG j=0;j<ccFeeders.size();j++)
                                        {
                                            CtiCCFeeder* currentFeeder = (CtiCCFeeder*)ccFeeders.at(j);

                                            if (_command->getCommand() == CtiCCCommand::SEND_ALL_ENABLE_OVUV) 
                                                currentFeeder->setOvUvDisabledFlag(FALSE);
                                            if (_command->getCommand() == CtiCCCommand::SEND_ALL_DISABLE_OVUV) 
                                                currentFeeder->setOvUvDisabledFlag(TRUE);
                                            if (((action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK) &&
                                               !currentFeeder->getDisableFlag())  ||
                                               (action  == CtiCCCommand::ENABLE_OVUV || action  == CtiCCCommand::DISABLE_OVUV ||
                                                action  == CtiCCCommand::SCAN_2WAY_DEVICE) ) 
                                            {
                                                CtiCCCapBank_SVector& ccCapBanks = currentFeeder->getCCCapBanks();

                                                for(LONG k=0;k<ccCapBanks.size();k++)
                                                {
                                                    CtiCCCapBankPtr currentCapBank = (CtiCCCapBankPtr)ccCapBanks[k];

                                                    if (((action  == CtiCCCommand::OPEN_CAPBANK || action  == CtiCCCommand::CLOSE_CAPBANK) &&
                                                        !currentCapBank->getDisableFlag()   && 
                                                        !stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) ) ||
                                                        (action  == CtiCCCommand::ENABLE_OVUV || action  == CtiCCCommand::DISABLE_OVUV ||
                                                         action  == CtiCCCommand::SCAN_2WAY_DEVICE) ) 
                                                    {
                                                        actionMulti->insert(new CtiCCCommand(action, currentCapBank->getControlDeviceId()));
                                                    }
                                                }
                                            }
                                        }

                                        modifiedSubsList.push_back(currentSubstationBus);
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

    }
    

    if (actionMulti->getCount() > 0)
    {
       CtiCCExecutorFactory f;
       CtiCCExecutor* executor = f.createExecutor(actionMulti);
       executor->Execute();
       delete executor;
    }
    CtiCCExecutorFactory f;
    CtiCCExecutor* executor = f.createExecutor(new CtiCCGeoAreasMsg(ccAreas));
    executor->Execute();
    delete executor;
    executor = f.createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas(CtiTime().seconds())));
    executor->Execute();
    delete executor;


    executor = f.createExecutor(new CtiCCSubstationBusMsg((CtiCCSubstationBus_vec&)modifiedSubsList,CtiCCSubstationBusMsg::SubBusModified ));
    executor->Execute();
    delete executor;
    if (multi->getCount() > 0 || multiPilMsg->getCount() > 0)
        CtiCapController::getInstance()->confirmCapBankControl(multiPilMsg, multi);
    if (eventMulti->getCount() > 0)
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);

}


/*---------------------------------------------------------------------------
    DeleteItem
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::DeleteItem()
{
    CtiCCClientListener::getInstance()->BroadcastMessage(_command);
}
/*---------------------------------------------------------------------------
    SendSystemStatus
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::SendSystemStatus()
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
    CtiCCSubstationBus_vec updatedSubs;

    if (bankID != 0) 
    {
    
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
                        updatedSubs.push_back(currentSubstationBus);

                        if (!currentSubstationBus->getVerificationFlag() && currentSubstationBus->getStrategyId() > 0)
                        {    
                            savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                            savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                            savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                            savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                            currentSubstationBus->setRecentlyControlledFlag(FALSE);
                            currentFeeder->setRecentlyControlledFlag(FALSE);
                            controlID = currentCapBank->getControlDeviceId();
                            if (!stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState)) 
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
                                if (_LOG_MAPID_INFO) 
                                {
                                    additional += " MapID: ";
                                    additional += currentSubstationBus->getMapLocationId();
                                    additional += " (";
                                    additional += currentSubstationBus->getPAODescription();
                                    additional += ")";
                                }
                                additional += string("  Feeder: ");
                                additional += currentFeeder->getPAOName();
                                if (_LOG_MAPID_INFO) 
                                {
                                    additional += " MapID: ";
                                    additional += currentFeeder->getMapLocationId();
                                    additional += " (";
                                    additional += currentFeeder->getPAODescription();
                                    additional += ")";
                                }
                                string text("Manual Open Sent, Sub VarLoad = ");
                                _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                                text += tempchar;
                                text += string(" Feeder VarLoad = ");
                                _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                                text += tempchar1;
                                pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                                DOUBLE kvarBefore = 0;
                                DOUBLE kvarAfter = 0;
                                DOUBLE kvarChange = 0;

                                if (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) ||
                                    !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
                                {
                                    kvarBefore = currentFeeder->getCurrentVarLoadPointValue();
                                    kvarAfter = currentFeeder->getCurrentVarLoadPointValue();
                                    kvarChange = 0;
                                }
                                else
                                {
                                    kvarBefore = currentSubstationBus->getCurrentVarLoadPointValue();
                                    kvarAfter = currentSubstationBus->getCurrentVarLoadPointValue();
                                    kvarChange = 0;
                                }
                                
                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                                if( !savedBusRecentlyControlledFlag ||
                                    (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) && !savedFeederRecentlyControlledFlag) )
                                {
                                    pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));
                                    ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                                    currentCapBank->setLastStatusChangeTime(CtiTime());

                                }
                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);
                                INT actionId = CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1;
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), actionId));

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
                                doConfirmImmediately(currentSubstationBus,pointChanges, ccEvents, bankID);
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
    }
    else
    {
        {                                              
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Manual Control Open received for BankID 0...Cannot control device."<<endl;
        }    
    }


    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"control open");
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );

        if (eventMulti->getCount() >0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        if (updatedSubs.size() > 0) 
        {
            CtiCCExecutorFactory f;
            CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg(updatedSubs));
            executor->Execute();
            delete executor;
        }
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
    CtiCCSubstationBus_vec updatedSubs;

    if (bankID != 0) 
    {
    
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
                        updatedSubs.push_back(currentSubstationBus);
                        if (!currentSubstationBus->getVerificationFlag() && currentSubstationBus->getStrategyId() > 0)
                        {    

                            savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                            savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                            savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                            savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                            currentSubstationBus->setRecentlyControlledFlag(FALSE);
                            currentFeeder->setRecentlyControlledFlag(FALSE);
                            controlID = currentCapBank->getControlDeviceId();
                            if (!stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState)) 
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
                                if (_LOG_MAPID_INFO) 
                                {
                                    additional += " MapID: ";
                                    additional += currentSubstationBus->getMapLocationId();
                                    additional += " (";
                                    additional += currentSubstationBus->getPAODescription();
                                    additional += ")";
                                }
                                additional += string("  Feeder: ");
                                additional += currentFeeder->getPAOName();
                                if (_LOG_MAPID_INFO) 
                                {
                                    additional += " MapID: ";
                                    additional += currentFeeder->getMapLocationId();
                                    additional += " (";
                                    additional += currentFeeder->getPAODescription();
                                    additional += ")";
                                }
                                string text("Manual Close Sent, Sub VarLoad = ");
                                _snprintf(tempchar,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentSubstationBus->getCurrentVarLoadPointValue());
                                text += tempchar;
                                text += string(" Feeder VarLoad = ");
                                _snprintf(tempchar1,80,"%.*f",currentSubstationBus->getDecimalPlaces(),currentFeeder->getCurrentVarLoadPointValue());
                                text += tempchar1;
                                pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                                DOUBLE kvarBefore = 0;
                                DOUBLE kvarAfter = 0;
                                DOUBLE kvarChange = 0;

                                ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
                                if( !savedBusRecentlyControlledFlag ||
                                    (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) && !savedFeederRecentlyControlledFlag) )
                                {
                                    pointChanges.push_back(new CtiPointDataMsg(currentCapBank->getStatusPointId(),currentCapBank->getControlStatus(),NormalQuality,StatusPointType,"Forced ccServer Update", TAG_POINT_FORCE_UPDATE));

                                    if (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) ||
                                        !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
                                    {
                                        kvarBefore = currentFeeder->getCurrentVarLoadPointValue();
                                        kvarAfter = currentFeeder->getCurrentVarLoadPointValue();
                                        kvarChange = 0;
                                    }
                                    else
                                    {
                                        kvarBefore = currentSubstationBus->getCurrentVarLoadPointValue();
                                        kvarAfter = currentSubstationBus->getCurrentVarLoadPointValue();
                                        kvarChange = 0;
                                    }
                                
                                    ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
                                    currentCapBank->setLastStatusChangeTime(CtiTime());
                                }

                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);
                                INT actionId = CCEventActionIdGen(currentCapBank->getStatusPointId()) + 1;
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser(), kvarBefore, kvarAfter, kvarChange, currentCapBank->getIpAddress(), actionId));
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
                                
                            }

                            if( !confirmImmediately && !currentSubstationBus->isBusPerformingVerification())
                            {
                                currentSubstationBus->setRecentlyControlledFlag(TRUE);
                                currentFeeder->setRecentlyControlledFlag(TRUE);
                            }
                            else if (confirmImmediately)
                            {
                                doConfirmImmediately(currentSubstationBus,pointChanges, ccEvents, bankID);
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
    }
    else
    {
        {                                              
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Manual Control Close received for BankID 0...Cannot control device."<<endl;
        }    
    }

    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"control close");
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );

        if (eventMulti->getCount() >0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        if (updatedSubs.size() > 0) 
        {
            CtiCCExecutorFactory f;
            CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg(updatedSubs));
            executor->Execute();
            delete executor;
        }

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
void CtiCCCommandExecutor::EnableArea()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG areaId = _command->getId();
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    
    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(areaId);
    if (currentArea != NULL)
    {
        string text1 = string("Manual Enable Area");
        string additional1 = string("Area: ");
        additional1 += currentArea->getPAOName();
        
        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

        currentArea->setDisableFlag(FALSE);
        store->UpdateAreaDisableFlagInDB(currentArea);

               
        if (eventMulti->getCount() > 0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        if (multi->getCount() > 0)
            CtiCapController::getInstance()->sendMessageToDispatch(multi);

        
        CtiCCExecutorFactory f;
        CtiCCExecutor *executor = f.createExecutor(new CtiCCGeoAreasMsg(ccAreas)); 
        executor->Execute();
        delete executor;
    }
    else
    {
        CtiCCSpArea_vec& ccSpAreas = *store->getCCSpecialAreas(CtiTime().seconds());

        CtiCCSpecial* currentSpArea = store->findSpecialAreaByPAObjectID(areaId);
        if (currentSpArea != NULL)
        {
            string text1 = string("Manual Enable Special Area");
            string additional1 = string("Special Area: ");
            additional1 += currentSpArea->getPAOName();

            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

            currentSpArea->setDisableFlag(FALSE);
            store->UpdateSpecialAreaDisableFlagInDB(currentSpArea);

            std::list <long>::iterator subIter = currentSpArea->getSubstationIds()->begin();
            
            while (subIter != currentSpArea->getSubstationIds()->end())
            {
                CtiCCSubstationPtr currentSubstation = NULL;
                currentSubstation = store->findSubstationByPAObjectID(*subIter);
                subIter++;            
                if (currentSubstation != NULL)
                {
                    currentSubstation->setSaEnabledFlag(TRUE);
                    currentSubstation->setSaEnabledId(areaId);
                }
            }
            store->setValid(false);

            if (eventMulti->getCount() > 0)
                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
            if (multi->getCount() > 0)
                CtiCapController::getInstance()->sendMessageToDispatch(multi);


            CtiCCExecutorFactory f;
            CtiCCExecutor *executor = f.createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas(CtiTime().seconds())));
            executor->Execute();
            delete executor;


        }
    }
}

/*---------------------------------------------------------------------------
    ConfirmOpen
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::DisableArea()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG areaId = _command->getId();
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(areaId);
    if (currentArea != NULL)
    {
        string text1 = string("Manual Disable Area");
        string additional1 = string("Area: ");
        additional1 += currentArea->getPAOName();
        
        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

        currentArea->setDisableFlag(TRUE);
        store->UpdateAreaDisableFlagInDB(currentArea);
        
        
        if (eventMulti->getCount() > 0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        if (multi->getCount() > 0)
            CtiCapController::getInstance()->sendMessageToDispatch(multi);

        CtiCCExecutorFactory f;
        CtiCCExecutor *executor = f.createExecutor(new CtiCCGeoAreasMsg(ccAreas)); 
        executor->Execute();
        delete executor;

    }
    else
    {
        CtiCCSpArea_vec& ccSpAreas = *store->getCCSpecialAreas(CtiTime().seconds());

        CtiCCSpecial* currentSpArea = store->findSpecialAreaByPAObjectID(areaId);
        if (currentSpArea != NULL)
        {
            string text1 = string("Manual Disable Special Area");
            string additional1 = string("Special Area: ");
            additional1 += currentSpArea->getPAOName();

            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

            currentSpArea->setDisableFlag(TRUE);
            store->UpdateSpecialAreaDisableFlagInDB(currentSpArea);

            std::list <long>::iterator subIter = currentSpArea->getSubstationIds()->begin();

            while (subIter != currentSpArea->getSubstationIds()->end())
            {
                CtiCCSubstationPtr currentSubstation = NULL;
                currentSubstation = store->findSubstationByPAObjectID(*subIter);
                subIter++;            
                if (currentSubstation != NULL)
                {
                    currentSubstation->setSaEnabledFlag(FALSE);
                    currentSubstation->setSaEnabledId(0);
                }
            }
            store->setValid(false);
            if (eventMulti->getCount() > 0)
                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
            if (multi->getCount() > 0)
                CtiCapController::getInstance()->sendMessageToDispatch(multi);

            CtiCCExecutorFactory f;
            CtiCCExecutor *executor = f.createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas(CtiTime().seconds())));
            executor->Execute();
            delete executor;


        }
    }

}
/*---------------------------------------------------------------------------
    EnableSystem
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::EnableSystem()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG areaId = _command->getId();
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    string text1 = string("Manual Enable System");
    string additional1 = string("CapControl SYSTEM ENABLE ");

    pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
    ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    for(LONG i=0;i<ccAreas.size();i++)
    { 
        CtiCCAreaPtr currentArea = (CtiCCArea*)ccAreas.at(i);
        if (currentArea != NULL)
        {
             currentArea->setDisableFlag(FALSE);
             store->UpdateAreaDisableFlagInDB(currentArea);
        }
    }
               
    if (eventMulti->getCount() > 0)
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    if (multi->getCount() > 0)
        CtiCapController::getInstance()->sendMessageToDispatch(multi);

    
    CtiCCExecutorFactory f;
    CtiCCExecutor*executor = f.createExecutor(new CtiCCGeoAreasMsg(ccAreas)); 
    executor->Execute();
    delete executor;

    executor = f.createExecutor(new CtiCCCommand(CtiCCCommand::SYSTEM_STATUS, 1));
    executor->Execute();
    delete executor;
}

/*---------------------------------------------------------------------------
    DisableSystem
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::DisableSystem()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG areaId = _command->getId();
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    string text1 = string("Manual Disable System");
    string additional1 = string("CapControl SYSTEM DISABLE ");

    pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
    ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());
    for(LONG i=0;i<ccAreas.size();i++)
    { 
        CtiCCAreaPtr currentArea = (CtiCCArea*)ccAreas.at(i);
        if (currentArea != NULL)
        {
             currentArea->setDisableFlag(TRUE);
             store->UpdateAreaDisableFlagInDB(currentArea);
        }
    }
               
    if (eventMulti->getCount() > 0)
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    if (multi->getCount() > 0)
        CtiCapController::getInstance()->sendMessageToDispatch(multi);

    
    CtiCCExecutorFactory f;
    CtiCCExecutor*executor = f.createExecutor(new CtiCCGeoAreasMsg(ccAreas)); 
    executor->Execute();
    delete executor;
    executor = f.createExecutor(new CtiCCCommand(CtiCCCommand::SYSTEM_STATUS, 0));
    executor->Execute();
    delete executor;

}



/*---------------------------------------------------------------------------
    Scan2WayDevice
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::Flip7010Device()
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
    CtiCCSubstationBus_vec updatedSubs;

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
                if( bankID == currentCapBank->getPAOId() )
                {
                    found = TRUE;
                    if (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 7010")) 
                    {
                    
                        updatedSubs.push_back(currentSubstationBus);

                        if (!currentSubstationBus->getVerificationFlag() && currentSubstationBus->getStrategyId() > 0)
                        {    
                            savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                            savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                            savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                            savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                            currentSubstationBus->setRecentlyControlledFlag(FALSE);
                            currentFeeder->setRecentlyControlledFlag(FALSE);
                            controlID = currentCapBank->getControlDeviceId();

                            if (!stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState)) 
                                currentFeeder->setLastCapBankControlledDeviceId(currentCapBank->getPAOId());
                            currentSubstationBus->setLastFeederControlledPAOId(currentFeeder->getPAOId());
                            currentSubstationBus->setLastFeederControlledPosition(j);
                            currentSubstationBus->setLastOperationTime(CtiTime());
                            currentFeeder->setLastOperationTime(CtiTime());

                            if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable||
                                currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail ||
                                currentCapBank->getControlStatus() == CtiCCCapBank::Open ) 
                            {
                                currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);
                            }
                            else
                            {
                                currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                            }
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
                                if (_LOG_MAPID_INFO) 
                                {
                                    additional += " MapID: ";
                                    additional += currentSubstationBus->getMapLocationId();
                                    additional += " (";
                                    additional += currentSubstationBus->getPAODescription();
                                    additional += ")";
                                }
                                additional += string("  Feeder: ");
                                additional += currentFeeder->getPAOName();
                                if (_LOG_MAPID_INFO) 
                                {
                                    additional += " MapID: ";
                                    additional += currentFeeder->getMapLocationId();
                                    additional += " (";
                                    additional += currentFeeder->getPAODescription();
                                    additional += ")";
                                }
                                string text("Manual Flip Sent, Sub VarLoad = ");
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

                                }

                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser()));
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
                                doConfirmImmediately(currentSubstationBus,pointChanges, ccEvents, bankID);
                            }
                            break;
                        }
                        else
                        {
                            if (currentSubstationBus->getVerificationFlag())
                            {                                              
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank Verification is ENABLED on SubstationsBus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                             <<".  Cannot perform Flip on Cap Bank: " << currentCapBank->getPAOName() << " PAOID: " << currentCapBank->getPAOId() << "."<<endl;
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
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - CBC Not 7010 Device Type.  Flip command not supported " << endl;
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
            break;
        }
    }

    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"control flip");
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );

        if (eventMulti->getCount() >0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        if (updatedSubs.size() > 0) 
        {
            CtiCCExecutorFactory f;
            CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg(updatedSubs));
            executor->Execute();
            delete executor;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Could not create Porter Request Message in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}


/*---------------------------------------------------------------------------
    Scan2WayDevice
---------------------------------------------------------------------------*/    
void CtiCCCommandExecutor::Scan2WayDevice()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    LONG controlID = 0;
    LONG cbcID = _command->getId();
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();
    CtiCCSubstationBusPtr currentSubstationBus = NULL;
    CtiCCFeederPtr currentFeeder = NULL;

    LONG bankID = store->findCapBankIDbyCbcID(cbcID);
    if (bankID != NULL) 
    {
        CtiCCCapBankPtr currentCapBank = store->findCapBankByPAObjectID(bankID);

        if (currentCapBank != NULL)
        {   long subBusId = store->findSubBusIDbyCapBankID(currentCapBank->getPAOId());
            if (subBusId != NULL)
            {
                currentSubstationBus = store->findSubBusByPAObjectID(subBusId);
                if (currentSubstationBus != NULL)
                {
                    long feederId = store->findFeederIDbyCapBankID(currentCapBank->getPAOId());
                    if (feederId != NULL)
                    {
                        currentFeeder = store->findFeederByPAObjectID(feederId);
                    }
                }
            }

            if (currentSubstationBus != NULL && currentFeeder != NULL)
            {
                if (currentCapBank->getControlDeviceId() > 0 &&
                    stringContainsIgnoreCase(currentCapBank->getControlDeviceType(), "CBC 702")) 
                {

                    CtiCommandMsg *pAltRate = CTIDBG_new CtiCommandMsg( CtiCommandMsg::AlternateScanRate );

                    if(pAltRate)
                    {
                        pAltRate->insert(-1);                       // token, not yet used.
                        pAltRate->insert( currentCapBank->getControlDeviceId() );       // Device to poke.

                        pAltRate->insert( -1 );                      // Seconds since midnight, or NOW if negative.

                        pAltRate->insert( 0 );                      // Duration of zero should cause 1 scan.

                        CtiCapController::getInstance()->sendMessageToDispatch(pAltRate);

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Requesting scans at the alternate scan rate for " << currentCapBank->getPAOName() << endl;
                        }
                    }

                    string text = string("Manual Scan 2-Way CBC attached to CapBank: ");
                    text += currentCapBank->getPAOName();
                    string additional = string("CBC: ");
                    CHAR devID[20];
                    additional += string (ltoa(currentCapBank->getControlDeviceId(),devID,10));

                    pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text,additional,CapControlLogType,SignalEvent,_command->getUser()));

                    ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser()));

                }

                //CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(SYS_PID_CAPCONTROL,0,text,additional,CapControlLogType,SignalEvent,_command->getUser()));
                           
                //CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, currentSubstationBus->getPAOId(), 0, capControlDisable, currentSubstationBus->getEventSequence(), 1, text, _command->getUser()));
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - CapBank Not attached to Feeder/Sub.  Cannot Scan Control Device " << endl;
                }
            }
      

        }
    }
    if (eventMulti->getCount() > 0)
        CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
    if (multi->getCount() > 0)
        CtiCapController::getInstance()->sendMessageToDispatch(multi);
  
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
            if (_LOG_MAPID_INFO) 
            {
                additional1 += " MapID: ";
                additional1 += currentSubstationBus->getMapLocationId();
                additional1 += " (";
                additional1 += currentSubstationBus->getPAODescription();
                additional1 += ")";
            }
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

                        if (!stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState)) 
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
                        if (_LOG_MAPID_INFO) 
                        {
                            additional += " MapID: ";
                            additional += currentSubstationBus->getMapLocationId();
                            additional += " (";
                            additional += currentSubstationBus->getPAODescription();
                            additional += ")";
                        }
                        additional += string("  Feeder: ");
                        additional += currentFeeder->getPAOName();
                        if (_LOG_MAPID_INFO) 
                        {
                            additional += " MapID: ";
                            additional += currentFeeder->getMapLocationId();
                            additional += " (";
                            additional += currentFeeder->getPAODescription();
                            additional += ")";
                        }

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

    LONG areaId = _command->getId();
    LONG controlID = 0;
    BOOL found = FALSE;
    CtiMultiMsg* multi = new CtiMultiMsg();
    CtiMultiMsg* eventMulti = new CtiMultiMsg();
    CtiMultiMsg* confirmMulti = new CtiMultiMsg();
    CtiMultiMsg_vec& pointChanges = multi->getData();
    CtiMultiMsg_vec& ccEvents = eventMulti->getData();

    CtiCCArea_vec& ccAreas = *store->getCCGeoAreas(CtiTime().seconds());

    CtiCCAreaPtr currentArea = store->findAreaByPAObjectID(areaId);

    if (currentArea != NULL) 
    {
        string text1 = string("Manual Confirm Area");
        string additional1 = string("Area: ");
        additional1 += currentArea->getPAOName();
        
        pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
        ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));

        CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());
        CtiCCSubstationBus* currentSubstationBus = NULL;
        for(LONG i=0;i<ccSubstationBuses.size();i++)
        {
            currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];

            if (currentSubstationBus->getParentId() == areaId)
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
            delete executor;

            executor = f.createExecutor(new CtiCCGeoAreasMsg(ccAreas));
            executor->Execute();
            delete executor;


        }
        if (eventMulti->getCount() > 0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        if (multi->getCount() > 0)
            CtiCapController::getInstance()->sendMessageToDispatch(multi);

    }
    else 
    {
        CtiCCSpArea_vec& ccSpAreas = *store->getCCSpecialAreas(CtiTime().seconds());

        CtiCCSpecial* currentSpArea = store->findSpecialAreaByPAObjectID(areaId);
        if (currentSpArea != NULL)
        {
            string text1 = string("Manual Confirm Special Area");
            string additional1 = string("Special Area: ");
            additional1 += currentSpArea->getPAOName();
            
            pointChanges.push_back(new CtiSignalMsg(SYS_PID_CAPCONTROL,1,text1,additional1,CapControlLogType,SignalEvent,_command->getUser()));
            ccEvents.push_back(new CtiCCEventLogMsg(0, SYS_PID_CAPCONTROL, 0, 0, capControlManualCommand, 0, 0, text1, _command->getUser()));
        
            CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());
            CtiCCSubstationBus* currentSubstationBus = NULL;
            for(LONG i=0;i<ccSubstationBuses.size();i++)
            {
                currentSubstationBus = (CtiCCSubstationBus*)ccSubstationBuses[i];
        
                if (currentSubstationBus->getParentId() == areaId)
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
                delete executor;
        
                executor = f.createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas(CtiTime().seconds())));
                executor->Execute();
                delete executor;
            }
            if (eventMulti->getCount() > 0)
                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
            if (multi->getCount() > 0)
                CtiCapController::getInstance()->sendMessageToDispatch(multi);

        }

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
    CtiCCSubstationBus_vec updatedSubs;

    if (bankID != 0) 
    {
    
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
                        updatedSubs.push_back(currentSubstationBus);
                        if (!currentSubstationBus->getVerificationFlag() && currentSubstationBus->getStrategyId() > 0)
                        {   
                            savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                            savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                            savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                            savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                            currentSubstationBus->setRecentlyControlledFlag(FALSE);
                            currentFeeder->setRecentlyControlledFlag(FALSE);
                            controlID = currentCapBank->getControlDeviceId();

                            if (!stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState)) 
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
                                if (_LOG_MAPID_INFO) 
                                {
                                    additional += " MapID: ";
                                    additional += currentSubstationBus->getMapLocationId();
                                    additional += " (";
                                    additional += currentSubstationBus->getPAODescription();
                                    additional += ")";
                                }
                                additional += string("  Feeder: ");
                                additional += currentFeeder->getPAOName();
                                if (_LOG_MAPID_INFO) 
                                {
                                    additional += " MapID: ";
                                    additional += currentFeeder->getMapLocationId();
                                    additional += " (";
                                    additional += currentFeeder->getPAODescription();
                                    additional += ")";
                                }

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
                                }
                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser()));

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
                                doConfirmImmediately(currentSubstationBus,pointChanges, ccEvents, bankID);
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
    }
    else
    {
        {                                              
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Manual Control Open received for BankID 0...Cannot control device."<<endl;
        }    
    }

    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"control open");
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );
        if (eventMulti->getCount() >0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        if (updatedSubs.size() > 0) 
        {
            CtiCCExecutorFactory f;
            CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg(updatedSubs, CtiCCSubstationBusMsg::SubBusAdded));
            executor->Execute();
            delete executor;
        }
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
    CtiCCSubstationBus_vec updatedSubs;


    if (bankID != 0) 
    {
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
                        updatedSubs.push_back(currentSubstationBus);
                        if (!currentSubstationBus->getVerificationFlag() && currentSubstationBus->getStrategyId() > 0)
                        {
                            savedBusRecentlyControlledFlag = currentSubstationBus->getRecentlyControlledFlag();
                            savedFeederRecentlyControlledFlag = currentFeeder->getRecentlyControlledFlag();
                            savedBusLastOperationTime = currentSubstationBus->getLastOperationTime();
                            savedFeederLastOperationTime = currentFeeder->getLastOperationTime();
                            currentSubstationBus->setRecentlyControlledFlag(FALSE);
                            currentFeeder->setRecentlyControlledFlag(FALSE);
                            controlID = currentCapBank->getControlDeviceId();

                            if (!stringCompareIgnoreCase(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState)) 
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
                                if (_LOG_MAPID_INFO) 
                                {
                                    additional += " MapID: ";
                                    additional += currentSubstationBus->getMapLocationId();
                                    additional += " (";
                                    additional += currentSubstationBus->getPAODescription();
                                    additional += ")";
                                }
                                additional += string("  Feeder: ");
                                additional += currentFeeder->getPAOName();
                                if (_LOG_MAPID_INFO) 
                                {
                                    additional += " MapID: ";
                                    additional += currentFeeder->getMapLocationId();
                                    additional += " (";
                                    additional += currentFeeder->getPAODescription();
                                    additional += ")";
                                }
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
                                }
                                INT seqId = CCEventSeqIdGen();
                                currentSubstationBus->setEventSequence(seqId);
                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, _command->getUser()));

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
                                doConfirmImmediately(currentSubstationBus,pointChanges, ccEvents, bankID);
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
    }
    else
    {
        {                                              
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Manual Confirm Close received for BankID 0...Cannot control device."<<endl;
        }    
    }

    if( controlID > 0 )
    {
        CtiRequestMsg* reqMsg = new CtiRequestMsg(controlID,"control close");
        reqMsg->setSOE(5);
        CtiCapController::getInstance()->manualCapBankControl( reqMsg, multi );
        if (eventMulti->getCount() >0)
            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMulti);
        if (updatedSubs.size() > 0) 
        {
            CtiCCExecutorFactory f;
            CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg(updatedSubs, CtiCCSubstationBusMsg::SubBusAdded));
            executor->Execute();
            delete executor;
        }
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

void CtiCCCommandExecutor::doConfirmImmediately(CtiCCSubstationBus* currentSubstationBus, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, LONG bankId)
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

                        DOUBLE kvarBefore = 0;
                        DOUBLE kvarAfter = 0;
                        DOUBLE kvarChange = 0;

                        if (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) ||
                            !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
                        {
                            kvarBefore = currentFeeder->getCurrentVarLoadPointValue();
                            kvarAfter = currentFeeder->getCurrentVarLoadPointValue();
                            kvarChange = 0;
                        }
                        else
                        {
                            kvarBefore = currentSubstationBus->getCurrentVarLoadPointValue();
                            kvarAfter = currentSubstationBus->getCurrentVarLoadPointValue();
                            kvarChange = 0;
                        }

                        ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), "Var: Forced Close by manual confirm, Close", _command->getUser(), kvarBefore, kvarAfter, kvarChange));

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

                        DOUBLE kvarBefore = 0;
                        DOUBLE kvarAfter = 0;
                        DOUBLE kvarChange = 0;

                        if (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) ||
                            !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
                        {
                            kvarBefore = currentFeeder->getCurrentVarLoadPointValue();
                            kvarAfter = currentFeeder->getCurrentVarLoadPointValue();
                            kvarChange = 0;
                        }
                        else
                        {
                            kvarBefore = currentSubstationBus->getCurrentVarLoadPointValue();
                            kvarAfter = currentSubstationBus->getCurrentVarLoadPointValue();
                            kvarChange = 0;
                        }

                        ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), "Var: Forced Open by manual confirm, Open", _command->getUser(), kvarBefore, kvarAfter, kvarChange));
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                      << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                    }
                }

                currentCapBank->setRetryOpenFailedFlag(FALSE);
                currentCapBank->setRetryCloseFailedFlag(FALSE);

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

                                DOUBLE kvarBefore = 0;
                                DOUBLE kvarAfter = 0;
                                DOUBLE kvarChange = 0;

                                if (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) ||
                                    !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
                                {
                                    kvarBefore = currentFeeder->getCurrentVarLoadPointValue();
                                    kvarAfter = currentFeeder->getCurrentVarLoadPointValue();
                                    kvarChange = 0;
                                }
                                else
                                {
                                    kvarBefore = currentSubstationBus->getCurrentVarLoadPointValue();
                                    kvarAfter = currentSubstationBus->getCurrentVarLoadPointValue();
                                    kvarChange = 0;
                                }

                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), "Var: Forced Close by manual confirm, Close", _command->getUser(), kvarBefore, kvarAfter, kvarChange));
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

                                DOUBLE kvarBefore = 0;
                                DOUBLE kvarAfter = 0;
                                DOUBLE kvarChange = 0;

                                if (!stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::IndividualFeederControlMethod) ||
                                    !stringCompareIgnoreCase(currentSubstationBus->getControlMethod(), CtiCCSubstationBus::BusOptimizedFeederControlMethod) )
                                {
                                    kvarBefore = currentFeeder->getCurrentVarLoadPointValue();
                                    kvarAfter = currentFeeder->getCurrentVarLoadPointValue();
                                    kvarChange = 0;
                                }
                                else
                                {
                                    kvarBefore = currentSubstationBus->getCurrentVarLoadPointValue();
                                    kvarAfter = currentSubstationBus->getCurrentVarLoadPointValue();
                                    kvarChange = 0;
                                }

                                ccEvents.push_back(new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), "Var: Forced Open by manual confirm, Open", _command->getUser(), kvarBefore, kvarAfter, kvarChange));
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " - Cap Bank: " << currentCapBank->getPAOName()
                                              << " PAOID: " << currentCapBank->getPAOId() << " doesn't have a status point!" << endl;
                            }
                        }
                        currentCapBank->setRetryOpenFailedFlag(FALSE);
                        currentCapBank->setRetryCloseFailedFlag(FALSE);
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
    executor = f.createExecutor(new CtiCCSpecialAreasMsg(*store->getCCSpecialAreas(CtiTime().seconds())));
    executor->Execute();
    delete executor;
    executor = f.createExecutor(new CtiCCSubstationsMsg(*store->getCCSubstations(CtiTime().seconds())));
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
    LONG movedCapBankId = bankId;
    LONG originalFeederId = 0;
    float capSwitchingOrder = 0.0;
    float closeOrder = 0.0;
    float tripOrder = 0.0;

    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    CtiCCSubstationBus* currentSubstationBus = NULL;
    CtiCCFeeder* currentFeeder = NULL;

    CtiCCCapBank* currentCapBank = store->findCapBankByPAObjectID(movedCapBankId);
    if (currentCapBank != NULL) 
    {
        currentFeeder = store->findFeederByPAObjectID(currentCapBank->getParentId());
        if (currentFeeder != NULL) 
        { 
            currentSubstationBus = store->findSubBusByPAObjectID(currentFeeder->getParentId());
            if (currentSubstationBus != NULL && !currentSubstationBus->getVerificationFlag())
            {
                tempFeederId = currentFeeder->getPAOId();
                movedCapBankId = bankId;
                originalFeederId = currentCapBank->getOriginalFeederId();
                capSwitchingOrder = currentCapBank->getOriginalSwitchingOrder();
                closeOrder = currentCapBank->getOriginalCloseOrder();
                tripOrder = currentCapBank->getOriginalTripOrder();
            }
            else
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< currentSubstationBus->getPAOName() <<" PAOID: "<< currentSubstationBus->getPAOId() 
                                 <<".  Cannot perform RETURN CAP TO ORIGINAL FEEDER on Cap Bank: " << currentCapBank->getPAOName() << " PAOID: " << currentCapBank->getPAOId() << "."<<endl;
            }
        }

    }


    //moveCapBank(TRUE, oldFeederId, movedCapBankId, newFeederId, capSwitchingOrder);
    if( tempFeederId > 0 && movedCapBankId > 0 && originalFeederId > 0 )
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Move Cap Bank to original feeder PAO Id: " << movedCapBankId << endl;
        }
        moveCapBank(1, tempFeederId, movedCapBankId, originalFeederId, capSwitchingOrder, closeOrder, tripOrder);
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
                    }
                    currentCapBank->setCurrentDailyOperations(0);
                    currentCapBank->setMaxDailyOpsHitFlag(FALSE);
                }
                currentFeeder->setCurrentDailyOperations(0);
                currentFeeder->setMaxDailyOpsHitFlag(FALSE);
            }
            currentSubstationBus->setCurrentDailyOperations(0);
            currentSubstationBus->setMaxDailyOpsHitFlag(FALSE);
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
                        }
                        currentCapBank->setCurrentDailyOperations(0);
                        currentCapBank->setMaxDailyOpsHitFlag(FALSE);
                    }

                    currentFeeder->setCurrentDailyOperations(0);
                    currentFeeder->setMaxDailyOpsHitFlag(FALSE);
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
                            }
                            currentCapBank->setCurrentDailyOperations(0);
                            currentCapBank->setMaxDailyOpsHitFlag(FALSE);

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
                if (_LOG_MAPID_INFO) 
                {
                    additional += " MapID: ";
                    additional += currentSubstationBus->getMapLocationId();
                    additional += " (";
                    additional += currentSubstationBus->getPAODescription();
                    additional += ")";
                }

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
                if (_LOG_MAPID_INFO) 
                {
                    additional += " MapID: ";
                    additional += currentSubstationBus->getMapLocationId();
                    additional += " (";
                    additional += currentSubstationBus->getPAODescription();
                    additional += ")";
                }
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
                    if (_LOG_MAPID_INFO) 
                    {
                        additional += " MapID: ";
                        additional += currentFeeder->getMapLocationId();
                        additional += " (";
                        additional += currentFeeder->getPAODescription();
                        additional += ")";
                    }
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
                    if (_LOG_MAPID_INFO) 
                    {
                        additional += " MapID: ";
                        additional += currentFeeder->getMapLocationId();
                        additional += " (";
                        additional += currentFeeder->getPAODescription();
                        additional += ")";
                    }
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
    float capSwitchingOrder = _capMoveMsg->getCapSwitchingOrder();
    float capCloseOrder = _capMoveMsg->getCloseOrder();
    float capTripOrder = _capMoveMsg->getTripOrder();

    moveCapBank(permanentFlag, oldFeederId, movedCapBankId, newFeederId, capSwitchingOrder, capCloseOrder, capTripOrder);
}

/*---------------------------------------------------------------------------
    moveCapBank
---------------------------------------------------------------------------*/    
void CtiCCExecutor::moveCapBank(INT permanentFlag, LONG oldFeederId, LONG movedCapBankId, LONG newFeederId, float capSwitchingOrder, float closeOrder, float tripOrder)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

    CtiCCFeeder* oldFeederPtr = NULL;
    CtiCCFeeder* newFeederPtr = NULL;
    CtiCCCapBank* movedCapBankPtr = NULL;
    CtiCCSubstationBus* oldFeederParentSub = NULL;
    CtiCCSubstationBus* newFeederParentSub = NULL;
    CtiCCSubstationBus_vec& ccSubstationBuses = *store->getCCSubstationBuses(CtiTime().seconds());

    BOOL found = FALSE;
    BOOL verificationFlag = FALSE;

    oldFeederPtr = store->findFeederByPAObjectID(oldFeederId);
   if (oldFeederPtr != NULL) 
   {
       oldFeederParentSub = store->findSubBusByPAObjectID(oldFeederPtr->getParentId());
       if (oldFeederParentSub != NULL) 
       {
           if (oldFeederParentSub->getVerificationFlag()) 
           {
               {
                   CtiLockGuard<CtiLogger> logger_guard(dout);
                   dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< oldFeederParentSub->getPAOName() <<" PAOID: "<< oldFeederParentSub->getPAOId() 
                                <<".  Cannot perform MOVE CAPBANK on Feeder: "<<oldFeederPtr->getPAOName()<<" PAOID: "<<oldFeederPtr->getPAOId()<<"."<<endl;
               }
               verificationFlag = TRUE;
               found = TRUE;
           }
           else
           {
               oldFeederParentSub->setBusUpdatedFlag(TRUE);
           }
       }
   }
   newFeederPtr = store->findFeederByPAObjectID(newFeederId);
   if (newFeederPtr != NULL) 
   {
       newFeederParentSub = store->findSubBusByPAObjectID(newFeederPtr->getParentId());
       if (newFeederParentSub != NULL) 
       {
           if (newFeederParentSub->getVerificationFlag()) 
           {
               {
                   CtiLockGuard<CtiLogger> logger_guard(dout);
                   dout << CtiTime() << " - Cap Bank Verification is ENABLED on Substation Bus: "<< newFeederParentSub->getPAOName() <<" PAOID: "<< newFeederParentSub->getPAOId() 
                                <<".  Cannot perform MOVE CAPBANK on Feeder: "<<newFeederPtr->getPAOName()<<" PAOID: "<<newFeederPtr->getPAOId()<<"."<<endl;
               }
               verificationFlag = TRUE;
               found = TRUE;
           }
           else
           {
               newFeederParentSub->setBusUpdatedFlag(TRUE);
           }        
       }
   }
   movedCapBankPtr = store->findCapBankByPAObjectID(movedCapBankId);

    if( oldFeederPtr!=NULL && newFeederPtr!=NULL && movedCapBankPtr!=NULL && !verificationFlag)
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
                movedCapBankPtr->setOriginalCloseOrder(movedCapBankPtr->getCloseOrder());
                movedCapBankPtr->setOriginalTripOrder(movedCapBankPtr->getTripOrder());
            }
            else
            {
                movedCapBankPtr->setOriginalFeederId(0);
                movedCapBankPtr->setOriginalSwitchingOrder(0.0);
                movedCapBankPtr->setOriginalCloseOrder(0.0);
                movedCapBankPtr->setOriginalTripOrder(0.0);

            }

            movedCapBankPtr->setParentId(newFeederId);
          
        }

        {
            CtiCCCapBank_SVector& newFeederCapBanks = newFeederPtr->getCCCapBanks();

            movedCapBankPtr->setControlOrder(capSwitchingOrder);
            movedCapBankPtr->setCloseOrder(closeOrder);
            movedCapBankPtr->setTripOrder(tripOrder);

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
            string typeString = (permanentFlag?"Permanent":"Temporary");

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

        CtiMultiMsg_vec modifiedSubsList;
        modifiedSubsList.clear();
        oldFeederParentSub = store->findSubBusByPAObjectID(oldFeederPtr->getParentId());
        newFeederParentSub = store->findSubBusByPAObjectID(newFeederPtr->getParentId());

        if (newFeederParentSub != NULL) 
        {
            modifiedSubsList.push_back(newFeederParentSub);
        }
        if (oldFeederParentSub != NULL) 
        {
            modifiedSubsList.push_back(oldFeederParentSub);
        }
        CtiCCExecutorFactory f;
        CtiCCExecutor* executor = f.createExecutor(new CtiCCSubstationBusMsg((CtiCCSubstationBus_vec&)modifiedSubsList,CtiCCSubstationBusMsg::SubBusModified ));
        executor->Execute();
        delete executor;


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
    BOOL logToCCEvent = FALSE;

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
                            if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||  
                                currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending) 
                            { 
                                logToCCEvent = TRUE; 
                            } 

                            currentCapBank->setIgnoreFlag(FALSE);

                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            currentCapBank->setControlStatus((LONG)value);
                            currentCapBank->setTagsControlStatus((LONG)tags);
                            currentCapBank->setLastStatusChangeTime(timestamp);
                            currentSubstationBus->figureEstimatedVarLoadPointValue();
                            if( currentSubstationBus->getEstimatedVarLoadPointId() > 0 )
                                CtiCapController::getInstance()->sendMessageToDispatch(new CtiPointDataMsg(currentSubstationBus->getEstimatedVarLoadPointId(),currentSubstationBus->getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                            if (logToCCEvent)  
                            { 
                                string text = string("Var: Cancelled by Pending Override, "); 
                                if (currentCapBank->getControlStatus() == CtiCCCapBank::Open)  
                                    text += "Open"; 
                                else if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable)  
                                    text += "OpenQuestionable"; 
                                else if (currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail)  
                                    text += "OpenFail"; 
                                else if (currentCapBank->getControlStatus() == CtiCCCapBank::Close)  
                                    text += "Close"; 
                                else if (currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable)  
                                    text += "CloseQuestionable"; 
                                else if (currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail)  
                                    text += "CloseFail"; 

                                CtiCCEventLogMsg* eventMsg = new CtiCCEventLogMsg(0, currentCapBank->getStatusPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capBankStateUpdate, currentSubstationBus->getEventSequence(), currentCapBank->getControlStatus(), text, "unknown user" );
                                eventMsg->setActionId(CCEventActionIdGen(currentCapBank->getStatusPointId()));
                                CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(eventMsg); 
                            } 

                        }
                        found = TRUE;
                        break;
                    }
                    else if( currentCapBank->getOperationAnalogPointId() == pointID )
                    {
                        /* Commenting this out, to reset disabled bank op counts.  makes sense.*/
                        //if (!currentCapBank->getDisableFlag()) 
                        {
                            currentSubstationBus->setBusUpdatedFlag(TRUE);
                            currentCapBank->setTotalOperations((LONG) value);
                            currentCapBank->setCurrentDailyOperations((LONG) value);
                            currentCapBank->setLastStatusChangeTime(timestamp);

                            char tempchar[80] = "";
                            string text = "CapBank: ";
                            text+= currentCapBank->getPAOName();
                            text += " Operation Count Change";
                            string additional = "Value = ";
                            _snprintf(tempchar,80,"%.*f",1,currentCapBank->getTotalOperations());
                            additional += tempchar;

                            CtiCapController::getInstance()->getCCEventMsgQueueHandle().write(new CtiCCEventLogMsg(0, currentCapBank->getOperationAnalogPointId(), currentSubstationBus->getPAOId(), currentFeeder->getPAOId(), capControlSetOperationCount, currentSubstationBus->getEventSequence(), currentCapBank->getTotalOperations(), text, "unknown user" )); 
                            CtiCapController::getInstance()->sendMessageToDispatch(new CtiSignalMsg(currentCapBank->getOperationAnalogPointId(),currentCapBank->getTotalOperations(),text,additional,CapControlLogType,SignalEvent,"unknown user"));
                        }
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
    while(messages.size( )>0)
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
        case CTICCSPECIALAREAS_MSG_ID:
        case CTICCSUBSTATION_MSG_ID:
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
